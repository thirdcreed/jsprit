package algorithms;

import java.util.ArrayList;
import java.util.Collection;

import basics.algo.InsertionListener;
import basics.algo.SolutionCostFunction;
import basics.costs.VehicleRoutingActivityCosts;
import basics.costs.VehicleRoutingTransportCosts;

public class DefaultOptimizationModel implements OptimizationModel{

	private StateManagerImpl stateManager;
	
	private SolutionCostFunction solutionCostFunction;
	
	private Collection<HardRouteLevelConstraint> routeLevelConstraints = new ArrayList<HardRouteLevelConstraint>();
	
	private Collection<HardActivityLevelConstraint> activityLevelConstraints = new ArrayList<HardActivityLevelConstraint>();
	
	private Collection<InsertionListener> insertionListeners = new ArrayList<InsertionListener>();
	
	private Collection<ActivityVisitor> activityVisitors = new ArrayList<ActivityVisitor>();
	
	private Collection<ReverseActivityVisitor> revActivityVisitors = new ArrayList<ReverseActivityVisitor>();
	
	private Collection<RouteVisitor> routeVisitors = new ArrayList<RouteVisitor>();
	
	private VehicleRoutingActivityCosts activityCosts;
	
	private VehicleRoutingTransportCosts transportCosts;
	
	public DefaultOptimizationModel(VehicleRoutingActivityCosts activityCosts, VehicleRoutingTransportCosts transportCosts){
		this.activityCosts = activityCosts;
		this.transportCosts = transportCosts;
		
		stateManager = new StateManagerImpl();
		
		insertionListeners.add(new InitializeLoadsAtStartAndEndOfRouteWhenInsertionStarts(stateManager));
		insertionListeners.add(new UpdateLoadsAtStartAndEndOfRouteWhenJobHasBeenInserted(stateManager));
	
		activityVisitors.add(new UpdateActivityTimes(transportCosts));
		activityVisitors.add(new UpdateLoadAtActivityLevel(stateManager));
		
		activityVisitors.add(new UpdateCostsAtAllLevels(activityCosts, transportCosts, stateManager));
		
		activityVisitors.add(new UpdateOccuredDeliveriesAtActivityLevel(stateManager));
		
		revActivityVisitors.add(new UpdateLatestOperationStartTimeAtActLocations(stateManager, transportCosts));
		revActivityVisitors.add(new UpdateFuturePickupsAtActivityLevel(stateManager));

	}
	
	@Override
	public SolutionCostFunction getObjectiveFunction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<HardRouteLevelConstraint> getRouteLevelConstraints() {
		return routeLevelConstraints;
	}

	@Override
	public Collection<HardActivityLevelConstraint> getActivityLevelConstraints() {
		return activityLevelConstraints;
	}

	@Override
	public Collection<ActivityVisitor> getActivityLevelStateUpdaters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RouteVisitor> getRouteLevelStateUpdaters() {
		// TODO Auto-generated method stub
		return null;
	}

}
