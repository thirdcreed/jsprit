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
package problem.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import problem.VehicleRoutingProblem;
import problem.VehicleRoutingProblem.FleetComposition;
import problem.VehicleRoutingProblem.FleetSize;
import problem.io.VrpXMLReader;
import problem.io.VrpXMLWriter;
import problem.job.Service;
import problem.solution.VehicleRoutingProblemSolution;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.End;
import problem.solution.route.activity.ServiceActivity;
import problem.solution.route.activity.Start;
import problem.vehicle.Vehicle;
import problem.vehicle.VehicleImpl;
import problem.vehicle.VehicleTypeImpl;


public class VrpWriterV3Test {
	
	private String infileName;
	
	@Before
	public void doBefore(){
		infileName = "src/test/resources/infiniteWriterV2Test.xml";
	}
	
	@Test
	public void whenWritingSolutions_itWritesThemCorrectly(){
		VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
		builder.setFleetComposition(FleetComposition.HETEROGENEOUS);
		builder.setFleetSize(FleetSize.FINITE);
		VehicleTypeImpl type1 = VehicleTypeImpl.Builder.newInstance("vehType", 20).build();
		VehicleTypeImpl type2 = VehicleTypeImpl.Builder.newInstance("vehType2", 200).build();
		Vehicle v1 = VehicleImpl.Builder.newInstance("v1").setLocationId("loc").setType(type1).build();
		Vehicle v2 = VehicleImpl.Builder.newInstance("v2").setLocationId("loc").setType(type2).build();
		builder.addVehicleType(type1);
		builder.addVehicleType(type2);
		builder.addVehicle(v1);
		builder.addVehicle(v2);
		
		Service s1 = Service.Builder.newInstance("1", 1).setLocationId("loc").setServiceTime(2.0).build();
		Service s2 = Service.Builder.newInstance("2", 1).setLocationId("loc2").setServiceTime(4.0).build();
		builder.addService(s1).addService(s2);
		
		VehicleRoutingProblem vrp = builder.build();
		
		Collection<VehicleRoute> routes = new ArrayList<VehicleRoute>();
		Start start = Start.newInstance("start", 0.0, Double.MAX_VALUE);
		start.setEndTime(10.0);
		End end = End.newInstance("end", 0.0, Double.MAX_VALUE);
		end.setArrTime(100);
		VehicleRoute.Builder routebuilder = VehicleRoute.Builder.newInstance(start, end);
		
		ServiceActivity act1 = ServiceActivity.newInstance(s1);
		ServiceActivity act2 = ServiceActivity.newInstance(s2);
		act1.setArrTime(20.0);
		act1.setEndTime(30.0);
		
		act2.setArrTime(40.0);
		act2.setEndTime(80.0);
		
		routebuilder.addActivity(act1).addActivity(act2).setVehicle(v1);
		VehicleRoute route = routebuilder.build();
		routes.add(route);
		
		VehicleRoutingProblemSolution solution = new VehicleRoutingProblemSolution(routes, 100);
		
		new VrpXMLWriter(vrp, Arrays.asList(solution)).write(infileName);
		
		VehicleRoutingProblem.Builder vrpToReadBuilder = VehicleRoutingProblem.Builder.newInstance();
		Collection<VehicleRoutingProblemSolution> solutions = new ArrayList<VehicleRoutingProblemSolution>();
		new VrpXMLReader(vrpToReadBuilder, solutions).read(infileName);
		VehicleRoutingProblem readVrp = vrpToReadBuilder.build();

	}

}
