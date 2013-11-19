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
package algorithm;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import problem.VehicleRoutingProblem;
import problem.constraint.ConstraintManager;
import problem.constraint.HardActivityStateLevelConstraint;
import problem.io.VrpXMLReader;
import problem.solution.SolutionCostCalculator;
import problem.solution.VehicleRoutingProblemSolution;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.TourActivity;
import problem.vehicle.InfiniteFleetManagerFactory;
import problem.vehicle.VehicleFleetManager;
import util.Solutions;
import algorithm.acceptor.AcceptNewIfBetterThanWorst;
import algorithm.modules.RuinAndRecreateModule;
import algorithm.recreate.BestInsertionBuilder;
import algorithm.recreate.InsertionContext;
import algorithm.recreate.InsertionInitialSolutionFactory;
import algorithm.recreate.InsertionStrategy;
import algorithm.ruin.RadialRuinStrategyFactory;
import algorithm.ruin.RandomRuinStrategyFactory;
import algorithm.ruin.RuinStrategy;
import algorithm.ruin.distance.AvgCostsServiceDistance;
import algorithm.selector.SelectBest;
import algorithm.state.StateFactory;
import algorithm.state.StateManager;
import algorithm.state.UpdateVariableCosts;

public class BuildCVRPAlgoFromScratchTest {
	
	VehicleRoutingProblem vrp;
	
	VehicleRoutingAlgorithm vra;
	
	@Before
	public void setup(){
		VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(builder).read("src/test/resources/vrpnc1-jsprit.xml");
		vrp = builder.build();
		
		final StateManager stateManager = new StateManager(vrp);
		stateManager.updateLoadStates();
		stateManager.updateTimeWindowStates();
		stateManager.addStateUpdater(new UpdateVariableCosts(vrp.getActivityCosts(), vrp.getTransportCosts(), stateManager));

		VehicleFleetManager fleetManager = new InfiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
		
		ConstraintManager cManager = new ConstraintManager(vrp, stateManager);
		cManager.addLoadConstraint();
		cManager.addTimeWindowConstraint();
		
		BestInsertionBuilder iBuilder = new BestInsertionBuilder(vrp, fleetManager, stateManager, cManager);
		InsertionStrategy bestInsertion = iBuilder.build();
		
		RuinStrategy radial = new RadialRuinStrategyFactory(0.15, new AvgCostsServiceDistance(vrp.getTransportCosts())).createStrategy(vrp);
		RuinStrategy random = new RandomRuinStrategyFactory(0.25).createStrategy(vrp);
		
		SolutionCostCalculator solutionCostCalculator = new SolutionCostCalculator() {
			
			@Override
			public double getCosts(VehicleRoutingProblemSolution solution) {
				double costs = 0.0;
				for(VehicleRoute route : solution.getRoutes()){
					costs += stateManager.getRouteState(route, StateFactory.COSTS).toDouble();
				}
				return costs;
			}
		};
		
		SearchStrategy randomStrategy = new SearchStrategy(new SelectBest(), new AcceptNewIfBetterThanWorst(1), solutionCostCalculator);
		RuinAndRecreateModule randomModule = new RuinAndRecreateModule("randomRuin_bestInsertion", bestInsertion, random);
		randomStrategy.addModule(randomModule);
		
		SearchStrategy radialStrategy = new SearchStrategy(new SelectBest(), new AcceptNewIfBetterThanWorst(1), solutionCostCalculator);
		RuinAndRecreateModule radialModule = new RuinAndRecreateModule("radialRuin_bestInsertion", bestInsertion, radial);
		radialStrategy.addModule(radialModule);
		
		SearchStrategyManager strategyManager = new SearchStrategyManager();
		strategyManager.addStrategy(radialStrategy, 0.5);
		strategyManager.addStrategy(randomStrategy, 0.5);
		
		vra = new VehicleRoutingAlgorithm(vrp, strategyManager);
		vra.getAlgorithmListeners().addListener(stateManager);
		vra.getSearchStrategyManager().addSearchStrategyModuleListener(stateManager);
		vra.getSearchStrategyManager().addSearchStrategyModuleListener(new RemoveEmptyVehicles(fleetManager));

		VehicleRoutingProblemSolution iniSolution = new InsertionInitialSolutionFactory(bestInsertion, solutionCostCalculator).createSolution(vrp);
		vra.addInitialSolution(iniSolution);
		
		vra.setNuOfIterations(2000);
//		vra.setPrematureBreak(200);
		
	}
	
	@Test
	public void testVRA(){
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		System.out.println("costs="+Solutions.getBest(solutions).getCost()+";#routes="+Solutions.getBest(solutions).getRoutes().size());
		assertEquals(530.0, Solutions.getBest(solutions).getCost(),15.0);
		assertEquals(5, Solutions.getBest(solutions).getRoutes().size());
	}

}
