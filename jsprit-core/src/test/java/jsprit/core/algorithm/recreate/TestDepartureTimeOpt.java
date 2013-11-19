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
package jsprit.core.algorithm.recreate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Builder;
import jsprit.core.problem.cost.VehicleRoutingActivityCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.activity.TimeWindow;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;

import org.junit.Test;



public class TestDepartureTimeOpt {
	
	@Test
	public void whenSettingOneCustWithTWAnd_NO_DepTimeChoice_totalCostsShouldBe50(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addVehicle(vehicle).build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfig.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(20.0+30.0,Solutions.getBest(solutions).getCost(),0.1);
		
	}
	
	@Test
	public void whenSettingOneCustWithTWAnd_NO_DepTimeChoice_depTimeShouldBe0(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addVehicle(vehicle).build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfig.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(0.0,Solutions.getBest(solutions).getRoutes().iterator().next().getStart().getEndTime(),0.1);
		
	}
	
	@Test
	public void whenSettingOneCustWithTWAndDepTimeChoice_totalCostsShouldBe50(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addVehicle(vehicle).build();
		
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfigWithDepartureTimeChoice.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(20.0,Solutions.getBest(solutions).getCost(),0.1);
		
	}
	
	@Test
	public void whenSettingOneCustWithTWAndDepTimeChoice_depTimeShouldBe0(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addVehicle(vehicle).build();
		
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfigWithDepartureTimeChoice.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(30.0,Solutions.getBest(solutions).getRoutes().iterator().next().getStart().getEndTime(),0.1);
		
	}
	
	@Test
	public void whenSettingTwoCustWithTWAndDepTimeChoice_totalCostsShouldBe50(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		
		Service service2 = Service.Builder.newInstance("s2", 0).setLocationId("servLoc2").setCoord(Coordinate.newInstance(0, 20)).
				setTimeWindow(TimeWindow.newInstance(30, 40)).build();
		
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addService(service2).addVehicle(vehicle).build();
		
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfigWithDepartureTimeChoice.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(40.0,Solutions.getBest(solutions).getCost(),0.1);
		
	}
	
	@Test
	public void whenSettingTwoCustWithTWAndDepTimeChoice_depTimeShouldBe10(){
		TimeWindow timeWindow = TimeWindow.newInstance(40, 45);
		Service service = Service.Builder.newInstance("s", 0).setLocationId("servLoc").setCoord(Coordinate.newInstance(0, 10)).setTimeWindow(timeWindow).build();
		
		Service service2 = Service.Builder.newInstance("s2", 0).setLocationId("servLoc2").setCoord(Coordinate.newInstance(0, 20)).
				setTimeWindow(TimeWindow.newInstance(30, 40)).build();
		
		Vehicle vehicle = VehicleImpl.Builder.newInstance("v").setLocationId("vehLoc").setLocationCoord(Coordinate.newInstance(0, 0))
				.setType(VehicleTypeImpl.Builder.newInstance("vType", 0).build()).build();
		
		Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setActivityCosts(new VehicleRoutingActivityCosts(){

			@Override
			public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
				double waiting = Math.max(0, tourAct.getTheoreticalEarliestOperationStartTime() - arrivalTime)*1;
				double late = Math.max(0, arrivalTime - tourAct.getTheoreticalLatestOperationStartTime())*100;
				return  waiting + late;
			}
			
		});
		VehicleRoutingProblem vrp = vrpBuilder.addService(service).addService(service2).addVehicle(vehicle).build();
		
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/algorithmConfigWithDepartureTimeChoice.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(10.0,Solutions.getBest(solutions).getRoutes().iterator().next().getStart().getEndTime(),0.1);
		
	}	

}
