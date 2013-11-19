package jsprit.core.algorithm.state;

import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.PickupActivity;
import jsprit.core.problem.solution.route.activity.ReverseActivityVisitor;
import jsprit.core.problem.solution.route.activity.ServiceActivity;
import jsprit.core.problem.solution.route.activity.TourActivity;

class UpdateFuturePickups implements ReverseActivityVisitor, StateUpdater {
	private StateManager stateManager;
	private int futurePicks = 0;
	private VehicleRoute route;
	
	public UpdateFuturePickups(StateManager stateManager) {
		super();
		this.stateManager = stateManager;
	}

	@Override
	public void begin(VehicleRoute route) {
		this.route = route;
	}

	@Override
	public void visit(TourActivity act) {
		stateManager.putInternalActivityState(act, StateFactory.FUTURE_PICKS, StateFactory.createState(futurePicks));
		if(act instanceof PickupActivity || act instanceof ServiceActivity){
			futurePicks += act.getCapacityDemand();
		}
		assert futurePicks <= route.getVehicle().getCapacity() : "sum of pickups must not be > vehicleCap";
		assert futurePicks >= 0 : "sum of pickups must not < 0";
	}

	@Override
	public void finish() {
		futurePicks = 0;
		route = null;
	}
}