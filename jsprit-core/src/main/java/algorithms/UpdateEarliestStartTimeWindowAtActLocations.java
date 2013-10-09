package algorithms;

import algorithms.StateManagerImpl.StateImpl;
import basics.costs.VehicleRoutingTransportCosts;
import basics.route.TourActivity;
import basics.route.VehicleRoute;

public class UpdateEarliestStartTimeWindowAtActLocations implements ActivityVisitor{

	private StateManagerImpl states;
	
	private ActivityTimeTracker timeTracker;
	
	public UpdateEarliestStartTimeWindowAtActLocations(StateManagerImpl states, VehicleRoutingTransportCosts transportCosts) {
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
		states.putActivityState(activity, StateIdFactory.EARLIEST_OPERATION_START_TIME, new StateImpl(Math.max(timeTracker.getActArrTime(), activity.getTheoreticalEarliestOperationStartTime())));
		
	}

	@Override
	public void finish() {}

}