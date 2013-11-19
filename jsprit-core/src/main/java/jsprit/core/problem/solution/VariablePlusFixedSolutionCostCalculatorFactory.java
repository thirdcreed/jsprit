package jsprit.core.problem.solution;

import jsprit.core.algorithm.state.StateFactory;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.problem.solution.route.VehicleRoute;

public class VariablePlusFixedSolutionCostCalculatorFactory {
	
	private StateManager stateManager;
	
	public VariablePlusFixedSolutionCostCalculatorFactory(StateManager stateManager) {
		super();
		this.stateManager = stateManager;
	}

	public SolutionCostCalculator createCalculator(){
		return new SolutionCostCalculator() {
			
			@Override
			public double getCosts(VehicleRoutingProblemSolution solution) {
				double c = 0.0;
				for(VehicleRoute r : solution.getRoutes()){
					c += stateManager.getRouteState(r, StateFactory.COSTS).toDouble();
					c += r.getVehicle().getType().getVehicleCostParams().fix;
				}
				return c;
			}
		};
	}

}
