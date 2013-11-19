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
import java.util.Arrays;
import java.util.Collection;

import problem.VehicleRoutingProblem;
import problem.VehicleRoutingProblem.FleetSize;
import problem.io.VrpXMLReader;
import problem.solution.VehicleRoutingProblemSolution;
import problem.vehicle.PenaltyVehicleType;
import problem.vehicle.Vehicle;
import problem.vehicle.VehicleImpl;
import problem.vehicle.VehicleType;
import problem.vehicle.VehicleTypeImpl;

import util.Coordinate;
import util.Solutions;
import algorithm.VehicleRoutingAlgorithm;
import algorithm.io.VehicleRoutingAlgorithms;
import algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import analysis.AlgorithmSearchProgressChartListener;
import analysis.SolutionPlotter;
import analysis.SolutionPrinter;
import analysis.SolutionPrinter.Print;
import analysis.StopWatch;

public class MultipleDepotExampleWithPenaltyVehicles {

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
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		/*
		 * Read cordeau-instance p01, BUT only its services without any vehicles 
		 */
		new VrpXMLReader(vrpBuilder).read("input/vrp_cordeau_08.xml");
		
		/*
		 * add vehicles with its depots
		 * 2 depots:
		 * (-33,33)
		 * (33,-33)
		 * 
		 * each with 14 vehicles each with a capacity of 500 and a maximum duration of 310
		 */
		int nuOfVehicles = 14;
		int capacity = 500;
		double maxDuration = 310;
		Coordinate firstDepotCoord = Coordinate.newInstance(-33, 33);
		Coordinate second = Coordinate.newInstance(33, -33);
		
		int depotCounter = 1;
		for(Coordinate depotCoord : Arrays.asList(firstDepotCoord,second)){
			for(int i=0;i<nuOfVehicles;i++){
				VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type", capacity).setCostPerDistance(1.0).build();
				String vehicleId = depotCounter + "_" + (i+1) + "_vehicle";
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
				vehicleBuilder.setLocationCoord(depotCoord);
				vehicleBuilder.setType(vehicleType);
				vehicleBuilder.setLatestArrival(maxDuration);
				Vehicle vehicle = vehicleBuilder.build();
				vrpBuilder.addVehicle(vehicle);
			}
			/*
			 * define penalty-type with the same id, but other higher fixed and variable costs
			 */
			VehicleType penaltyType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type", capacity).setFixedCost(50).setCostPerDistance(3.0).build();
			/*
			 * to mark the penalty-type as penalty-type, wrap it with PenaltyVehicleType(Wrapper)
			 * this is to tell the fleetManager that this is not a regular but a penalty vehicle
			 */
			PenaltyVehicleType penaltyVehicleType = new PenaltyVehicleType(penaltyType);
			String vehicleId = depotCounter + "_vehicle#penalty";
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
			vehicleBuilder.setLocationCoord(depotCoord);
			/*
			 * set PenaltyVehicleType
			 */
			vehicleBuilder.setType(penaltyVehicleType);
			vehicleBuilder.setLatestArrival(maxDuration);
			Vehicle penaltyVehicle = vehicleBuilder.build();
			vrpBuilder.addVehicle(penaltyVehicle);

			depotCounter++;
		}
		
		
		
		/*
		 * define problem with finite fleet
		 */
		vrpBuilder.setFleetSize(FleetSize.FINITE);
		
		/*
		 * build the problem
		 */
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		/*
		 * plot to see how the problem looks like
		 */
//		SolutionPlotter.plotVrpAsPNG(vrp, "output/problem08.png", "p08");

		/*
		 * solve the problem
		 */
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig.xml");
		vra.getAlgorithmListeners().addListener(new StopWatch(),Priority.HIGH);
		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/progress.png"));
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		SolutionPrinter.print(Solutions.getBest(solutions),Print.VERBOSE);
		SolutionPlotter.plotSolutionAsPNG(vrp, Solutions.getBest(solutions), "output/p08_solution.png", "p08");

	}

}
