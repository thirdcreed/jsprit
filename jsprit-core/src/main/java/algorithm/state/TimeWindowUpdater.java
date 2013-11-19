package algorithm.state;

import org.apache.log4j.Logger;

import problem.cost.VehicleRoutingTransportCosts;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.ReverseActivityVisitor;
import problem.solution.route.activity.TourActivity;
import algorithm.state.StateManager.StateImpl;


class TimeWindowUpdater implements ReverseActivityVisitor, StateUpdater{

	private static Logger log = Logger.getLogger(TimeWindowUpdater.class);
	
	private StateManager states;
	
	private VehicleRoute route;
	
	private VehicleRoutingTransportCosts transportCosts;
	
	private double latestArrTimeAtPrevAct;
	
	private TourActivity prevAct;
	
	public TimeWindowUpdater(StateManager states, VehicleRoutingTransportCosts tpCosts) {
		super();
		this.states = states;
		this.transportCosts = tpCosts;
	}

	@Override
	public void begin(VehicleRoute route) {
		this.route = route;
		latestArrTimeAtPrevAct = route.getEnd().getTheoreticalLatestOperationStartTime();
		prevAct = route.getEnd();
	}

	@Override
	public void visit(TourActivity activity) {
		double potentialLatestArrivalTimeAtCurrAct = latestArrTimeAtPrevAct - transportCosts.getBackwardTransportTime(activity.getLocationId(), prevAct.getLocationId(), latestArrTimeAtPrevAct, route.getDriver(),route.getVehicle()) - activity.getOperationTime();
		double latestArrivalTime = Math.min(activity.getTheoreticalLatestOperationStartTime(), potentialLatestArrivalTimeAtCurrAct);
		
		states.putInternalActivityState(activity, StateFactory.LATEST_OPERATION_START_TIME, new StateImpl(latestArrivalTime));
		
		latestArrTimeAtPrevAct = latestArrivalTime;
		prevAct = activity;
	}

	@Override
	public void finish() {}
}