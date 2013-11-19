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
package algorithm.recreate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import problem.driver.Driver;
import problem.job.Job;
import problem.solution.route.VehicleRoute;
import problem.vehicle.Vehicle;


class CalculatesServiceInsertionWithTimeScheduling implements JobInsertionCostsCalculator{

	private static Logger log = Logger.getLogger(CalculatesServiceInsertionWithTimeScheduling.class);
	
	private JobInsertionCostsCalculator jic;
	
	private Random random = new Random();
	
	private int nOfDepartureTimes = 3;
	
	private double timeSlice = 900.0;
	
	public CalculatesServiceInsertionWithTimeScheduling(JobInsertionCostsCalculator jic, double timeSlice, int neighbors) {
		super();
		this.jic = jic;
		this.timeSlice = timeSlice;
		this.nOfDepartureTimes = neighbors;
		log.info("initialise " + this);
	}
	
	@Override
	public String toString() {
		return "[name=calculatesServiceInsertionWithTimeScheduling][timeSlice="+timeSlice+"][#timeSlice="+nOfDepartureTimes+"]";
	}

	@Override
	public InsertionData getInsertionData(VehicleRoute currentRoute, Job jobToInsert, Vehicle newVehicle, double newVehicleDepartureTime, Driver newDriver, double bestKnownScore) {
		List<Double> vehicleDepartureTimes = new ArrayList<Double>();
		double currentStart;
		if(currentRoute.getStart() == null){
			currentStart = newVehicleDepartureTime;
		}
		else currentStart = currentRoute.getStart().getEndTime();
		
		vehicleDepartureTimes.add(currentStart);
		double earliestDeparture = newVehicle.getEarliestDeparture();
		double latestEnd = newVehicle.getLatestArrival();
		
		for(int i=0;i<nOfDepartureTimes;i++){
			double neighborStartTime_earlier = currentStart - (i+1)*timeSlice;
			if(neighborStartTime_earlier > earliestDeparture) vehicleDepartureTimes.add(neighborStartTime_earlier);
			double neighborStartTime_later = currentStart + (i+1)*timeSlice;
			if(neighborStartTime_later < latestEnd) vehicleDepartureTimes.add(neighborStartTime_later);
		}
	
		InsertionData bestIData = null;
		for(Double departureTime : vehicleDepartureTimes){
			InsertionData iData = jic.getInsertionData(currentRoute, jobToInsert, newVehicle, departureTime, newDriver, bestKnownScore);
			if(bestIData == null) bestIData = iData;
			else if(iData.getInsertionCost() < bestIData.getInsertionCost()){
				iData.setVehicleDepartureTime(departureTime);
				bestIData = iData;
			}
		}
//		log.info(bestIData);
		return bestIData;
	}

}
