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
package algorithm.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import problem.VehicleRoutingProblem;
import problem.constraint.ConstraintManager;
import problem.vehicle.VehicleFleetManager;
import algorithm.listener.VehicleRoutingAlgorithmListeners.PrioritizedVRAListener;
import algorithm.recreate.BestInsertionBuilder;
import algorithm.recreate.InsertionStrategy;
import algorithm.recreate.JobInsertionCostsCalculator;
import algorithm.recreate.listener.InsertionListener;
import algorithm.state.StateManager;

class InsertionFactory {
	
	private static Logger log = Logger.getLogger(InsertionFactory.class);
	
	public static InsertionStrategy createInsertion(VehicleRoutingProblem vrp, HierarchicalConfiguration config, 
			VehicleFleetManager vehicleFleetManager, StateManager routeStates, List<PrioritizedVRAListener> algorithmListeners, ExecutorService executorService, int nuOfThreads, ConstraintManager constraintManager){

		if(config.containsKey("[@name]")){
			String insertionName = config.getString("[@name]");
			if(!insertionName.equals("bestInsertion") && !insertionName.equals("regretInsertion")){
				new IllegalStateException(insertionName + " is not supported. use either \"bestInsertion\" or \"regretInsertion\"");
			}
			InsertionStrategy insertionStrategy = null;
			List<InsertionListener> insertionListeners = new ArrayList<InsertionListener>();
			List<PrioritizedVRAListener> algoListeners = new ArrayList<PrioritizedVRAListener>();
	
			BestInsertionBuilder bestIBuilder = new BestInsertionBuilder(vrp, vehicleFleetManager, routeStates, constraintManager);
			
			
			
//			CalculatorBuilder calcBuilder = new CalculatorBuilder(insertionListeners, algorithmListeners);
//			calcBuilder.setStates(routeStates);
//			calcBuilder.setVehicleRoutingProblem(vrp);
//			calcBuilder.setVehicleFleetManager(vehicleFleetManager);
//			calcBuilder.setConstraintManager(constraintManager);
			
			if(config.containsKey("level")){
				String level = config.getString("level");
				if(level.equals("local")){
					bestIBuilder.setLocalLevel();
//					calcBuilder.setLocalLevel();
				}
				else if(level.equals("route")){
					int forwardLooking = 0;
					int memory = 1;
					String forward = config.getString("level[@forwardLooking]");
					String mem = config.getString("level[@memory]");
					if(forward != null) forwardLooking = Integer.parseInt(forward);
					else log.warn("parameter route[@forwardLooking] is missing. by default it is 0 which equals to local level");
					if(mem != null) memory = Integer.parseInt(mem);
					else log.warn("parameter route[@memory] is missing. by default it is 1");
					bestIBuilder.setRouteLevel(forwardLooking, memory);
//					
//					calcBuilder.setRouteLevel(forwardLooking, memory);
				}
				else throw new IllegalStateException("level " + level + " is not known. currently it only knows \"local\" or \"route\"");
			}
			else {
				bestIBuilder.setLocalLevel();
//				calcBuilder.setLocalLevel(); 
			}
			
			if(config.containsKey("considerFixedCosts") || config.containsKey("considerFixedCost")){
				String val = config.getString("considerFixedCosts");
				if(val == null) val = config.getString("considerFixedCost");
				if(val.equals("true")){
					double fixedCostWeight = 0.5;
					String weight = config.getString("considerFixedCosts[@weight]");
					if(weight == null) weight = config.getString("considerFixedCost[@weight]");
					if(weight != null) fixedCostWeight = Double.parseDouble(weight);
					else log.warn("parameter considerFixedCosts[@weight] is missing. by default, it is 0.5.");
					bestIBuilder.considerFixedCosts(fixedCostWeight);
//					calcBuilder.considerFixedCosts(fixedCostWeight);
				}
			}
			String timeSliceString = config.getString("experimental[@timeSlice]");
			String neighbors = config.getString("experimental[@neighboringSlices]");
			if(timeSliceString != null && neighbors != null){
				bestIBuilder.experimentalTimeScheduler(Double.parseDouble(timeSliceString),Integer.parseInt(neighbors));
			}
			
			if(insertionName.equals("bestInsertion")){		
				insertionStrategy = bestIBuilder.build();
			}
			else throw new IllegalStateException("currently only 'bestInsertion' is supported");
			for(InsertionListener l : insertionListeners) insertionStrategy.addListener(l);

			algorithmListeners.addAll(algoListeners);
			
			return insertionStrategy;
		}
		else throw new IllegalStateException("cannot create insertionStrategy, since it has no name.");
	}

	
}



