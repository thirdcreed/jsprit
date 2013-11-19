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
package jsprit.instances.readers;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.vehicle.PenaltyVehicleType;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.util.Coordinate;
import jsprit.instances.readers.CordeauReader;

import org.apache.log4j.Logger;




/**
 * Reader that reads instances developed by:
 * 
 * <p>Cordeau, J.-F., Gendreau, M. and Laporte, G. (1997), A tabu search heuristic for periodic and multi-depot vehicle routing problems. 
 * Networks, 30: 105–119. doi: 10.1002/(SICI)1097-0037(199709)30:2<105::AID-NET5>3.0.CO;2-G
 * 
 * <p>Files and file-description can be found <a href="http://neo.lcc.uma.es/vrp/vrp-instances/multiple-depot-vrp-instances/">here</a>. 
 * 
 * @author stefan schroeder
 *
 */
public class CordeauReader {

	private static Logger logger = Logger.getLogger(CordeauReader.class);
	
	private final VehicleRoutingProblem.Builder vrpBuilder;

	private double coordProjectionFactor = 1;
	
	private boolean addPenaltyVehicles = false;

	public CordeauReader(VehicleRoutingProblem.Builder vrpBuilder) {
		super();
		this.vrpBuilder = vrpBuilder;
	}
	
	public CordeauReader(VehicleRoutingProblem.Builder vrpBuilder, boolean penaltyVehicles) {
		super();
		this.vrpBuilder = vrpBuilder;
		this.addPenaltyVehicles = penaltyVehicles;
	}
	
	public void read(String fileName){
		vrpBuilder.setFleetSize(FleetSize.FINITE);
		BufferedReader reader = getReader(fileName);
		int vrpType;
		int nOfDepots = 0;
		int nOfCustomers = 0;
		int nOfVehiclesAtEachDepot = 0;
	
		int counter = 0;
		String line = null; 
		List<List<Builder>> vehiclesAtDepot = new ArrayList<List<Builder>>();
		int depotCounter = 0;
		while((line = readLine(reader)) != null){
			line = line.replace("\r", "");
			line = line.trim();
			String[] tokens = line.split("\\s+");
			if(counter == 0){
				vrpType = Integer.parseInt(tokens[0].trim());
				if(vrpType != 2) throw new IllegalStateException("expect vrpType to be equal to 2 and thus to be MDVRP");
				nOfVehiclesAtEachDepot = Integer.parseInt(tokens[1].trim());
				nOfCustomers = Integer.parseInt(tokens[2].trim());
				nOfDepots = Integer.parseInt(tokens[3].trim());
			}
			else if(counter <= nOfDepots){
				String depot = Integer.valueOf(counter).toString();
				int duration = Integer.parseInt(tokens[0].trim());
				if(duration == 0) duration = 999999;
				int capacity = Integer.parseInt(tokens[1].trim());
				VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(counter + "_cordeauType", capacity).
						setCostPerDistance(1.0).setFixedCost(0).build();
				List<Builder> builders = new ArrayList<VehicleImpl.Builder>();
				for(int vehicleCounter=0;vehicleCounter<nOfVehiclesAtEachDepot;vehicleCounter++){
					Builder vBuilder = VehicleImpl.Builder.newInstance(depot+"_"+(vehicleCounter+1) + "_cordeauVehicle");
					vBuilder.setLatestArrival(duration).setType(vehicleType);
					builders.add(vBuilder);
				}
				vehiclesAtDepot.add(builders);
			}
			else if(counter <= (nOfCustomers+nOfDepots)){
				String id = tokens[0].trim();
				Coordinate customerCoord = makeCoord(tokens[1].trim(),tokens[2].trim());
				double serviceTime = Double.parseDouble(tokens[3].trim());
				int demand = Integer.parseInt(tokens[4].trim());
				Service service = Service.Builder.newInstance(id, demand).setServiceTime(serviceTime).setLocationId(id).setCoord(customerCoord).build();
				vrpBuilder.addService(service);				
			}
			else if(counter <= (nOfCustomers+nOfDepots+nOfDepots)){
				Coordinate depotCoord = makeCoord(tokens[1].trim(),tokens[2].trim());
				List<Builder> vBuilders = vehiclesAtDepot.get(depotCounter);
				int cap = 0;
				double latestArrTime = 0.0;
				Coordinate coord = null;
				String typeId = null;
				for(Builder vBuilder : vBuilders){
					vBuilder.setLocationCoord(depotCoord);
					VehicleImpl vehicle = vBuilder.build();
					cap = vehicle.getCapacity();
					typeId = vehicle.getType().getTypeId();
					latestArrTime = vehicle.getLatestArrival();
					coord = vehicle.getCoord();
					vrpBuilder.addVehicle(vehicle);
				}
				if(addPenaltyVehicles){
					VehicleTypeImpl penaltyType = VehicleTypeImpl.Builder.newInstance(typeId, cap).setCostPerDistance(3.0).setFixedCost(50).build();
					VehicleImpl penaltyVehicle = VehicleImpl.Builder.newInstance(counter + "_penaltyVehicle").setLatestArrival(latestArrTime)
							.setType(new PenaltyVehicleType(penaltyType)).setLocationCoord(coord).build();
					vrpBuilder.addVehicle(penaltyVehicle);
				}
				depotCounter++;
			}
			else{
				throw new IllegalStateException("there are more lines than expected in file.");
			}
			counter++;
		}
		close(reader);
	}

	public void setCoordProjectionFactor(double coordProjectionFactor) {
		this.coordProjectionFactor = coordProjectionFactor;
	}

	private void close(BufferedReader reader)  {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			System.exit(1);
		}
	}

	private String readLine(BufferedReader reader) {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			System.exit(1);
			return null;
		}
	}
	
	private Coordinate makeCoord(String xString, String yString) {
		double x = Double.parseDouble(xString);
		double y = Double.parseDouble(yString);
		return new Coordinate(x*coordProjectionFactor,y*coordProjectionFactor);
	}

	private BufferedReader getReader(String solomonFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(solomonFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error(e1);
			System.exit(1);
		}
		return reader;
	}
}
