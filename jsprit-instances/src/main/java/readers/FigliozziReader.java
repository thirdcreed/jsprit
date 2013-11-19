/*******************************************************************************
 * Copyright (C) 2013  Stefan Schroeder
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import problem.VehicleRoutingProblem;
import problem.VehicleRoutingProblem.Builder;
import problem.cost.VehicleRoutingTransportCosts;
import problem.driver.Driver;
import problem.vehicle.Vehicle;

import util.CrowFlyCosts;
import util.Locations;

public class FigliozziReader {
	
	public static class TDCosts implements VehicleRoutingTransportCosts {
		
		private static Logger log = Logger.getLogger(TDCosts.class);
		
		private List<Double> timeBins;
		
		private List<Double> speed;
		
		private CrowFlyCosts crowFly;
		
		public TDCosts(Locations locations, List<Double> timeBins, List<Double> speedValues) {
			super();
			speed = speedValues;
			this.timeBins = timeBins;
			crowFly = new CrowFlyCosts(locations);
		}
		
		@Override
		public String toString() {
			return "timeDependentTransportCosts according to Figliozzi";
		}
			
		@Override
		public double getTransportCost(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
			return 1.0*crowFly.getTransportCost(fromId, toId, departureTime, null, null) + 
					1.0*getTransportTime(fromId,toId,departureTime, null, null);
//			return getTransportTime(fromId, toId, departureTime, driver, vehicle);
//			return crowFly.getTransportCost(fromId, toId, departureTime, null, null);
		}
		
		@Override
		public double getBackwardTransportCost(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
//			return crowFly.getTransportCost(fromId, toId, arrivalTime, null,null) + getBackwardTransportTime(fromId, toId, arrivalTime,null,null);
			return getBackwardTransportTime(fromId, toId, arrivalTime, driver, vehicle);
//			return crowFly.getTransportCost(fromId, toId, arrivalTime, null, null);
		}

		
		@Override
		public double getTransportTime(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
			if(fromId.equals(toId)){
				return 0.0;
			}
			double totalTravelTime = 0.0;
			double distanceToTravel = crowFly.getTransportCost(fromId, toId, departureTime, null, null);
			double currentTime = departureTime;
			double speedOfLastBin = 0.0;
			for(int i=0;i<timeBins.size();i++){
				speedOfLastBin = speed.get(i);
				double timeThreshold = timeBins.get(i);
				if(currentTime < timeThreshold || (i==timeBins.size()-1 && currentTime <= timeThreshold)){
					double maxReachableDistance = (timeThreshold-currentTime)*speed.get(i);
					if(distanceToTravel > maxReachableDistance){
						distanceToTravel = distanceToTravel - maxReachableDistance;
						totalTravelTime += (timeThreshold-currentTime);
						currentTime = timeThreshold;
						continue;
					}
					else{ //<= maxReachableDistance
						totalTravelTime += distanceToTravel/speed.get(i);
						return totalTravelTime;
					}
				}
			}
			assert speedOfLastBin != 0.0 : "speed cannot be 0.0";
			totalTravelTime += distanceToTravel/speedOfLastBin;
			return totalTravelTime;
		}


		@Override
		public double getBackwardTransportTime(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
			if(fromId.equals(toId)){
				return 0.0;
			}
			double totalTravelTime = 0.0;
			double distanceToTravel = crowFly.getTransportCost(fromId, toId, arrivalTime, null, null);
			double currentTime = arrivalTime;
			double speedOfLastBin = 0.0;
			for(int i=timeBins.size()-1;i>=0;i--){
				speedOfLastBin = speed.get(i);
				double nextLowerTimeThreshold;
				if(i>0){
					nextLowerTimeThreshold = timeBins.get(i-1);
				}
				else{
					nextLowerTimeThreshold = 0;
				}
				if(currentTime > nextLowerTimeThreshold || (i==0 && currentTime >= nextLowerTimeThreshold)){
					double maxReachableDistance = (currentTime - nextLowerTimeThreshold)*speed.get(i);
					if(distanceToTravel > maxReachableDistance){
						distanceToTravel = distanceToTravel - maxReachableDistance;
						totalTravelTime += (currentTime-nextLowerTimeThreshold);
						currentTime = nextLowerTimeThreshold;
						continue;
					}
					else{ //<= maxReachableDistance
						totalTravelTime += distanceToTravel/speed.get(i);
						return totalTravelTime;
					}
				}
			}
			assert speedOfLastBin != 0.0 : "speed cannot be 0.0";
			totalTravelTime += distanceToTravel/speedOfLastBin;
			return totalTravelTime;
		}
	}
	
	private VehicleRoutingProblem.Builder builder;
	private double fixCostsPerVehicle;

	public FigliozziReader(Builder builder) {
		super();
		this.builder = builder;
	}
	
	public void read(String solomonFile, String speedScenarioFile, String speedScenario){
		SolomonReader solomonReader = new SolomonReader(builder,fixCostsPerVehicle);
		solomonReader.read(solomonFile);
		double depotClosingTime = getDepotClosingTime();
		readAndCreateTransportCostsFunction(speedScenarioFile,speedScenario,depotClosingTime);
	}

	private double getDepotClosingTime() {
		assert builder.getAddedVehicles().size() == 1 : "strange. there should only be one solomon-vehicle";
		Vehicle v = builder.getAddedVehicles().iterator().next();
		return v.getLatestArrival();
	}

	private void readAndCreateTransportCostsFunction(String speedScenarioFile, String speedScenario, double depotClosingTime) {
		List<Double> timeBins = new ArrayList<Double>();
		timeBins.add(0.2*depotClosingTime);
		timeBins.add(0.4*depotClosingTime);
		timeBins.add(0.6*depotClosingTime);
		timeBins.add(0.8*depotClosingTime);
		timeBins.add(1.0*depotClosingTime);
		
		List<Double> speedValues = new ArrayList<Double>();
		readTravelTimeDistribution(speedScenarioFile,speedScenario,speedValues);
		
		TDCosts tdcosts = new TDCosts(builder.getLocations(), timeBins, speedValues);
		builder.setRoutingCost(tdcosts);
	}

	private void readTravelTimeDistribution(String speedScenarioFile, String speedScenario, List<Double> speedValues) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(speedScenarioFile)));
			String line = null;
			while((line = reader.readLine()) != null){
				line = line.replace("\r", "");
				line = line.trim();
				String[] tokens = line.split("\t");
				assert tokens.length == 6 : "could not read file correctly";
				String speedScen = tokens[0];
				if(speedScen.equals(speedScenario)){
					for(int i=1;i<6;i++){
						speedValues.add(Double.parseDouble(tokens[i]));
					}
				}
			}
			reader.close();
			assert speedValues.size() == 5 : "could not read speed-values correctly"; 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setFixCostsPerVehicle(double fix) {
		this.fixCostsPerVehicle=fix;
	}
	

}
