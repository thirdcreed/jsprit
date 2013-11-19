package problem.solution;

import problem.solution.route.VehicleRoute;
import algorithm.state.StateFactory;
import algorithm.state.StateManager;

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
