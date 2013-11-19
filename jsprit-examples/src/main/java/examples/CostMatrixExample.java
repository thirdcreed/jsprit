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
package examples;

import java.io.File;
import java.util.Collection;

import problem.VehicleRoutingProblem;
import problem.VehicleRoutingProblem.FleetSize;
import problem.cost.VehicleRoutingTransportCosts;
import problem.job.Service;
import problem.solution.VehicleRoutingProblemSolution;
import problem.vehicle.Vehicle;
import problem.vehicle.VehicleImpl;
import problem.vehicle.VehicleType;
import problem.vehicle.VehicleTypeImpl;

import util.Solutions;
import util.VehicleRoutingTransportCostsMatrix;
import algorithm.VehicleRoutingAlgorithm;
import algorithm.box.GreedySchrimpfFactory;
import algorithm.io.VehicleRoutingAlgorithms;
import analysis.SolutionPlotter;
import analysis.SolutionPrinter;
import analysis.SolutionPrinter.Print;

/**
 * Illustrates how you can use jsprit with an already compiled distance and time matrix.
 * 
 * @author schroeder
 *
 */
public class CostMatrixExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * some preparation - create output folder
		 */
		File dir = new File("output");
		// if the directory does not exist, create it
		if (!dir.exists()){
			System.out.println("creating directory ./output");
			boolean result = dir.mkdir();  
			if(result) System.out.println("./output created");  
		}
		VehicleType type = VehicleTypeImpl.Builder.newInstance("type", 2).setCostPerDistance(1).setCostPerTime(2).build();
		Vehicle vehicle = VehicleImpl.Builder.newInstance("vehicle").setLocationId("0").setType(type).build();
		
		Service s1 = Service.Builder.newInstance("1", 1).setLocationId("1").build();
		Service s2 = Service.Builder.newInstance("2", 1).setLocationId("2").build();
		Service s3 = Service.Builder.newInstance("3", 1).setLocationId("3").build();
		
		
		/*
		 * Assume the following symmetric distance-matrix
		 * from,to,distance
		 * 0,1,10.0
		 * 0,2,20.0
		 * 0,3,5.0
		 * 1,2,4.0
		 * 1,3,1.0
		 * 2,3,2.0
		 * 
		 * and this time-matrix
		 * 0,1,5.0
		 * 0,2,10.0
		 * 0,3,2.5
		 * 1,2,2.0
		 * 1,3,0.5
		 * 2,3,1.0
		 */
		//define a matrix-builder building a symmetric matrix
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
		costMatrixBuilder.addTransportDistance("0", "1", 10.0);
		costMatrixBuilder.addTransportDistance("0", "2", 20.0);
		costMatrixBuilder.addTransportDistance("0", "3", 5.0);
		costMatrixBuilder.addTransportDistance("1", "2", 4.0);
		costMatrixBuilder.addTransportDistance("1", "3", 1.0);
		costMatrixBuilder.addTransportDistance("2", "3", 2.0);
		
		costMatrixBuilder.addTransportTime("0", "1", 10.0);
		costMatrixBuilder.addTransportTime("0", "2", 20.0);
		costMatrixBuilder.addTransportTime("0", "3", 5.0);
		costMatrixBuilder.addTransportTime("1", "2", 4.0);
		costMatrixBuilder.addTransportTime("1", "3", 1.0);
		costMatrixBuilder.addTransportTime("2", "3", 2.0);
		
		VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
		
		VehicleRoutingProblem vrp = VehicleRoutingProblem.Builder.newInstance().setFleetSize(FleetSize.INFINITE).setRoutingCost(costMatrix)
				.addVehicle(vehicle).addService(s1).addService(s2).addService(s3).build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/fastAlgo.xml");
		
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		SolutionPrinter.print(Solutions.getBest(solutions), Print.VERBOSE);
				
		SolutionPlotter.plotSolutionAsPNG(vrp, Solutions.getBest(solutions), "output/yo.png", "po");

	}

}
