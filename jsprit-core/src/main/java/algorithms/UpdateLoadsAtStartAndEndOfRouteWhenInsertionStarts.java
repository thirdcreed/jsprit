package algorithms;

import java.util.Collection;

import algorithms.StateManager.StateImpl;
import basics.Delivery;
import basics.Job;
import basics.Pickup;
import basics.Service;
import basics.algo.InsertionStartsListener;
import basics.route.VehicleRoute;

/**
 * Initializes the load of each route/vehicle at start- and end-location before insertion starts.
 * 
 * <p>StateTypes.LOAD_AT_DEPOT and StateTypes.LOAD are modified for each route 
 * <p>These states can be retrieved by <br> 
 * stateManager.getRouteState(route, StateTypes.LOAD_AT_DEPOT) for LOAD_AT_DEPOT and <br>
 * stateManager.getRouteState(route, StateTypes.LOAD) for LOAD (i.e. load at end)
 * 
 * @param stateManager
 */
class UpdateLoadsAtStartAndEndOfRouteWhenInsertionStarts implements InsertionStartsListener {

	private StateManager stateManager;
	
	/**
	 * Initializes the load of each route/vehicle at start- and end-location before insertion starts.
	 * 
	 * <p>StateTypes.LOAD_AT_DEPOT and StateTypes.LOAD are modified for each route 
	 * <p>These states can be retrieved by <br> 
	 * stateManager.getRouteState(route, StateTypes.LOAD_AT_DEPOT) for LOAD_AT_DEPOT and <br>
	 * stateManager.getRouteState(route, StateTypes.LOAD) for LOAD (i.e. load at end)
	 * 
	 * @param stateManager
	 */
	public UpdateLoadsAtStartAndEndOfRouteWhenInsertionStarts(StateManager stateManager) {
		super();
		this.stateManager = stateManager;
	}

	void insertionStarts(VehicleRoute route) {
		int loadAtDepot = 0;
		int loadAtEnd = 0;
		for(Job j : route.getTourActivities().getJobs()){
			if(j instanceof Delivery){
				loadAtDepot += j.getCapacityDemand();
			}
			else if(j instanceof Pickup || j instanceof Service){
				loadAtEnd += j.getCapacityDemand();
			}
		}
		stateManager.putRouteState(route, StateFactory.LOAD_AT_BEGINNING, StateFactory.createState(loadAtDepot));
		stateManager.putRouteState(route, StateFactory.LOAD_AT_END, StateFactory.createState(loadAtEnd));
	}

	@Override
	public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
		for(VehicleRoute route : vehicleRoutes){ insertionStarts(route); }
	}
	
}