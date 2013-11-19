package jsprit.core.algorithm;

import jsprit.core.algorithm.state.StateManager;
import jsprit.core.algorithm.state.UpdateActivityTimes;
import jsprit.core.algorithm.state.UpdateVariableCosts;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.vehicle.VehicleFleetManager;


public class VehicleRoutingAlgorithmFactoryImpl implements VehicleRoutingAlgorithmFactory{

	private SearchStrategyManager searchStrategyManager;
	
	private StateManager stateManager;

	private VehicleFleetManager fleetManager;
	
	public VehicleRoutingAlgorithmFactoryImpl(SearchStrategyManager searchStrategyManager,
			StateManager stateManager, VehicleFleetManager fleetManager) {
		super();
		this.searchStrategyManager = searchStrategyManager;
		this.stateManager = stateManager;
		this.fleetManager = fleetManager;
	}

	@Override
	public VehicleRoutingAlgorithm createAlgorithm(VehicleRoutingProblem vrp) {
		this.stateManager.addStateUpdater(new UpdateVariableCosts(vrp.getActivityCosts(), vrp.getTransportCosts(), this.stateManager));
		this.stateManager.addStateUpdater(new UpdateActivityTimes(vrp.getTransportCosts()));
		VehicleRoutingAlgorithm algorithm = new VehicleRoutingAlgorithm(vrp, searchStrategyManager);
		algorithm.getAlgorithmListeners().addListener(stateManager);
		algorithm.getSearchStrategyManager().addSearchStrategyModuleListener(stateManager);
		algorithm.getSearchStrategyManager().addSearchStrategyModuleListener(new RemoveEmptyVehicles(fleetManager));
		return algorithm;
	}

}
