package algorithm.state;

import problem.cost.VehicleRoutingTransportCosts;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.ActivityVisitor;
import problem.solution.route.activity.TourActivity;
import util.ActivityTimeTracker;
import algorithm.state.StateManager.StateImpl;

class UpdateEarliestStartTime implements ActivityVisitor,StateUpdater{

	private StateManager states;
	
	private ActivityTimeTracker timeTracker;
	
	public UpdateEarliestStartTime(StateManager states, VehicleRoutingTransportCosts transportCosts) {
		super();
		this.states = states;
		timeTracker = new ActivityTimeTracker(transportCosts);
	}

	@Override
	public void begin(VehicleRoute route) {
		timeTracker.begin(route);
	}

	@Override
	public void visit(TourActivity activity) {
		timeTracker.visit(activity);
		states.putInternalActivityState(activity, StateFactory.EARLIEST_OPERATION_START_TIME, new StateImpl(Math.max(timeTracker.getActArrTime(), activity.getTheoreticalEarliestOperationStartTime())));
		
	}

	@Override
	public void finish() {}

}