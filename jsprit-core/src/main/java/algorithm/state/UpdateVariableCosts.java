package algorithm.state;

import org.apache.log4j.Logger;

import problem.cost.ForwardTransportCost;
import problem.cost.VehicleRoutingActivityCosts;
import problem.cost.VehicleRoutingTransportCosts;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.ActivityVisitor;
import problem.solution.route.activity.TourActivity;
import util.ActivityTimeTracker;
import algorithm.state.StateManager.StateImpl;

/**
 * Updates total costs (i.e. transport and activity costs) at route and activity level.
 * 
 * <p>Thus it modifies <code>stateManager.getRouteState(route, StateTypes.COSTS)</code> and <br>
 * <code>stateManager.getActivityState(activity, StateTypes.COSTS)</code>
 * 
 * 
 * @param activityCost
 * @param transportCost
 * @param states
 */
public class UpdateVariableCosts implements ActivityVisitor,StateUpdater{

	private static Logger log = Logger.getLogger(UpdateVariableCosts.class);
	
	private VehicleRoutingActivityCosts activityCost;

	private ForwardTransportCost transportCost;
	
	private StateManager states;
	
	private double totalOperationCost = 0.0;
	
	private VehicleRoute vehicleRoute = null;
	
	private TourActivity prevAct = null;
	
	private double startTimeAtPrevAct = 0.0;
	
	private ActivityTimeTracker timeTracker;
	
	/**
	 * Updates total costs (i.e. transport and activity costs) at route and activity level.
	 * 
	 * <p>Thus it modifies <code>stateManager.getRouteState(route, StateTypes.COSTS)</code> and <br>
	 * <code>stateManager.getActivityState(activity, StateTypes.COSTS)</code>
	 * 
	 * 
	 * @param activityCost
	 * @param transportCost
	 * @param states
	 */
	public UpdateVariableCosts(VehicleRoutingActivityCosts activityCost, VehicleRoutingTransportCosts transportCost, StateManager states) {
		super();
		this.activityCost = activityCost;
		this.transportCost = transportCost;
		this.states = states;
		timeTracker = new ActivityTimeTracker(transportCost);
	}

	@Override
	public void begin(VehicleRoute route) {
		vehicleRoute = route;
		vehicleRoute.getVehicleRouteCostCalculator().reset();
		timeTracker.begin(route);
		prevAct = route.getStart();
		startTimeAtPrevAct = timeTracker.getActEndTime();
	}

	@Override
	public void visit(TourActivity act) {
		timeTracker.visit(act);
		
		double transportCost = this.transportCost.getTransportCost(prevAct.getLocationId(), act.getLocationId(), startTimeAtPrevAct, vehicleRoute.getDriver(), vehicleRoute.getVehicle());
		double actCost = activityCost.getActivityCost(act, timeTracker.getActArrTime(), vehicleRoute.getDriver(), vehicleRoute.getVehicle());

		vehicleRoute.getVehicleRouteCostCalculator().addTransportCost(transportCost);
		vehicleRoute.getVehicleRouteCostCalculator().addActivityCost(actCost);
		
		totalOperationCost += transportCost;
		totalOperationCost += actCost;

		states.putInternalActivityState(act, StateFactory.COSTS, new StateImpl(totalOperationCost));

		prevAct = act;
		startTimeAtPrevAct = timeTracker.getActEndTime();
	}

	@Override
	public void finish() {
		timeTracker.finish();
		double transportCost = this.transportCost.getTransportCost(prevAct.getLocationId(), vehicleRoute.getEnd().getLocationId(), startTimeAtPrevAct, vehicleRoute.getDriver(), vehicleRoute.getVehicle());
		double actCost = activityCost.getActivityCost(vehicleRoute.getEnd(), timeTracker.getActEndTime(), vehicleRoute.getDriver(), vehicleRoute.getVehicle());
		
		vehicleRoute.getVehicleRouteCostCalculator().addTransportCost(transportCost);
		vehicleRoute.getVehicleRouteCostCalculator().addActivityCost(actCost);
		
		totalOperationCost += transportCost;
		totalOperationCost += actCost;
//		totalOperationCost += getFixCosts(vehicleRoute.getVehicle());
		
		states.putInternalRouteState(vehicleRoute, StateFactory.COSTS, new StateImpl(totalOperationCost));
		
		//this is rather strange and likely to change
		vehicleRoute.getVehicleRouteCostCalculator().price(vehicleRoute.getDriver());
		vehicleRoute.getVehicleRouteCostCalculator().price(vehicleRoute.getVehicle());
		vehicleRoute.getVehicleRouteCostCalculator().finish();
		
		startTimeAtPrevAct = 0.0;
		prevAct = null;
		vehicleRoute = null;
		totalOperationCost = 0.0;
	}

}