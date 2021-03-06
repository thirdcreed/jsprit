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
package algorithms;

import org.apache.log4j.Logger;

import algorithms.ForwardInTimeListeners.ForwardInTimeListener;
import basics.costs.ForwardTransportTime;
import basics.route.Driver;
import basics.route.End;
import basics.route.TourActivity;
import basics.route.Vehicle;
import basics.route.VehicleRoute;


/**
 * 
 * @author sschroeder
 *
 */

class IterateRouteForwardInTime implements VehicleRouteUpdater{
	
	private static Logger log = Logger.getLogger(IterateRouteForwardInTime.class);
	
	private ForwardTransportTime transportTime;

	private ForwardInTimeListeners listeners;
	
	public IterateRouteForwardInTime(ForwardTransportTime transportTime) {
		super();
		this.transportTime = transportTime;
		listeners = new ForwardInTimeListeners();
	}

	/**
	 * 
	 * 
	 */
	public void iterate(VehicleRoute vehicleRoute) {
		if(listeners.isEmpty()) return;
		if(vehicleRoute.isEmpty()) return;
		listeners.start(vehicleRoute, vehicleRoute.getStart(), vehicleRoute.getStart().getEndTime());
		
		Vehicle vehicle = vehicleRoute.getVehicle();
		Driver driver = vehicleRoute.getDriver();
		TourActivity prevAct = vehicleRoute.getStart(); 
		double startAtPrevAct = prevAct.getEndTime();		
		
		for(TourActivity currentAct : vehicleRoute.getTourActivities().getActivities()){ 
			double transportTime = this.transportTime.getTransportTime(prevAct.getLocationId(), currentAct.getLocationId(), startAtPrevAct, driver, vehicle);
			double arrivalTimeAtCurrAct = startAtPrevAct + transportTime; 
			double operationStartTime = Math.max(currentAct.getTheoreticalEarliestOperationStartTime(), arrivalTimeAtCurrAct);
			double operationEndTime = operationStartTime + currentAct.getOperationTime();
			
			listeners.nextActivity(currentAct,arrivalTimeAtCurrAct,operationEndTime);
			
			prevAct = currentAct;
			startAtPrevAct = operationEndTime;
		}
		
		End currentAct = vehicleRoute.getEnd();
		double transportTime = this.transportTime.getTransportTime(prevAct.getLocationId(), currentAct.getLocationId(), startAtPrevAct, driver, vehicle);
		double arrivalTimeAtCurrAct = startAtPrevAct + transportTime; 
		
		listeners.end(vehicleRoute.getEnd(), arrivalTimeAtCurrAct);
	}
	
	public void addListener(ForwardInTimeListener l){
		listeners.addListener(l);
	}

}
