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
package jsprit.core.algorithm.ruin.distance;

import jsprit.core.algorithm.ruin.distance.AvgCostsServiceDistance;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.vehicle.Vehicle;

import org.junit.Test;


public class TestJobDistanceAvgCosts {
	
	public static void main(String[] args) {
		VehicleRoutingTransportCosts costs = new VehicleRoutingTransportCosts() {
			
			@Override
			public double getBackwardTransportTime(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
				
				return 0;
			}
			
			@Override
			public double getBackwardTransportCost(String fromId, String toId,
					double arrivalTime, Driver driver, Vehicle vehicle) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public double getTransportCost(String fromId, String toId,
					double departureTime, Driver driver, Vehicle vehicle) {
				String vehicleId = vehicle.getId();
				return 0;
			}
			
			@Override
			public double getTransportTime(String fromId, String toId,
					double departureTime, Driver driver, Vehicle vehicle) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		AvgCostsServiceDistance c = new AvgCostsServiceDistance(costs);
		c.getDistance(Service.Builder.newInstance("1", 1).setLocationId("foo").build(), Service.Builder.newInstance("2", 2).setLocationId("foo").build());
	}
	
	@Test(expected=NullPointerException.class)
	public void whenVehicleAndDriverIsNull_And_CostsDoesNotProvideAMethodForThis_throwException(){
//		(expected=NullPointerException.class)
		VehicleRoutingTransportCosts costs = new VehicleRoutingTransportCosts() {
			
			@Override
			public double getBackwardTransportTime(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
				
				return 0;
			}
			
			@Override
			public double getBackwardTransportCost(String fromId, String toId,
					double arrivalTime, Driver driver, Vehicle vehicle) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public double getTransportCost(String fromId, String toId,
					double departureTime, Driver driver, Vehicle vehicle) {
				String vehicleId = vehicle.getId();
				return 0;
			}
			
			@Override
			public double getTransportTime(String fromId, String toId,
					double departureTime, Driver driver, Vehicle vehicle) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		AvgCostsServiceDistance c = new AvgCostsServiceDistance(costs);
		c.getDistance(Service.Builder.newInstance("1", 1).setLocationId("loc").build(), Service.Builder.newInstance("2", 2).setLocationId("loc").build());
	}

}
