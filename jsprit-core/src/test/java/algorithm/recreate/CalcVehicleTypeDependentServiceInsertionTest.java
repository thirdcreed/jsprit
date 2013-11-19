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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import problem.job.Service;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.TimeWindow;
import problem.vehicle.Vehicle;
import problem.vehicle.VehicleFleetManager;
import problem.vehicle.VehicleImpl;
import problem.vehicle.VehicleTypeImpl;
import algorithm.recreate.InsertionData;
import algorithm.recreate.JobInsertionCostsCalculator;





public class CalcVehicleTypeDependentServiceInsertionTest {
	
	Vehicle veh1;
	Vehicle veh2;
	VehicleFleetManager fleetManager;
	Service service;
	VehicleRoute vehicleRoute;
	
	@Before
	public void doBefore(){
		veh1 = mock(Vehicle.class);
		veh2 = mock(Vehicle.class);
		when(veh1.getType()).thenReturn(VehicleTypeImpl.Builder.newInstance("type1", 0).build());
		when(veh2.getType()).thenReturn(VehicleTypeImpl.Builder.newInstance("type2", 0).build());
		when(veh1.getLocationId()).thenReturn("loc1");
		when(veh2.getLocationId()).thenReturn("loc2");
		fleetManager = mock(VehicleFleetManager.class);
		service = mock(Service.class);
		vehicleRoute = mock(VehicleRoute.class);
		
		when(fleetManager.getAvailableVehicles()).thenReturn(Arrays.asList(veh1,veh2));
		
		when(veh1.getCapacity()).thenReturn(10);
		when(veh2.getCapacity()).thenReturn(10);
		
		when(service.getCapacityDemand()).thenReturn(0);
		when(service.getTimeWindow()).thenReturn(TimeWindow.newInstance(0.0, Double.MAX_VALUE));
		
		when(vehicleRoute.getDriver()).thenReturn(null);
		when(vehicleRoute.getVehicle()).thenReturn(VehicleImpl.createNoVehicle());
	}
	
	@Test
	public void whenHaving2Vehicle_calcInsertionOfCheapest(){
		JobInsertionCostsCalculator calc = mock(JobInsertionCostsCalculator.class);
		InsertionData iDataVeh1 = new InsertionData(10.0,InsertionData.NO_INDEX, 1, veh1, null);
		InsertionData iDataVeh2 = new InsertionData(20.0,InsertionData.NO_INDEX, 1, veh2, null);
		when(calc.getInsertionData(vehicleRoute, service, veh1, veh1.getEarliestDeparture(), null, Double.MAX_VALUE)).thenReturn(iDataVeh1);
		when(calc.getInsertionData(vehicleRoute, service, veh2, veh2.getEarliestDeparture(), null, Double.MAX_VALUE)).thenReturn(iDataVeh2);
		when(calc.getInsertionData(vehicleRoute, service, veh2, veh2.getEarliestDeparture(), null, 10.0)).thenReturn(iDataVeh2);
		VehicleTypeDependentJobInsertionCalculator insertion = new VehicleTypeDependentJobInsertionCalculator(fleetManager,calc);
		InsertionData iData = insertion.getInsertionData(vehicleRoute, service, null, 0.0, null, Double.MAX_VALUE);
		assertThat(iData.getSelectedVehicle(), is(veh1));

	}

	@Test
	public void whenHaving2Vehicle_calcInsertionOfCheapest2(){
		JobInsertionCostsCalculator calc = mock(JobInsertionCostsCalculator.class);
		InsertionData iDataVeh1 = new InsertionData(20.0,InsertionData.NO_INDEX, 1, veh1, null);
		InsertionData iDataVeh2 = new InsertionData(10.0,InsertionData.NO_INDEX, 1, veh2, null);
		when(calc.getInsertionData(vehicleRoute, service, veh1, veh1.getEarliestDeparture(), null, Double.MAX_VALUE)).thenReturn(iDataVeh1);
		when(calc.getInsertionData(vehicleRoute, service, veh2, veh2.getEarliestDeparture(), null, Double.MAX_VALUE)).thenReturn(iDataVeh2);
		when(calc.getInsertionData(vehicleRoute, service, veh2, veh2.getEarliestDeparture(), null, 20.0)).thenReturn(iDataVeh2);
		VehicleTypeDependentJobInsertionCalculator insertion = new VehicleTypeDependentJobInsertionCalculator(fleetManager,calc);
		InsertionData iData = insertion.getInsertionData(vehicleRoute, service, null, 0.0, null, Double.MAX_VALUE);
		assertThat(iData.getSelectedVehicle(), is(veh2));

	}
}
