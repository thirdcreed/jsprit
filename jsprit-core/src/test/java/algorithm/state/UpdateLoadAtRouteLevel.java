package algorithm.state;

import java.util.Collection;

import problem.job.Job;
import problem.job.Service;
import problem.solution.route.VehicleRoute;
import algorithm.recreate.listener.InsertionStartsListener;
import algorithm.recreate.listener.JobInsertedListener;
import algorithm.state.StateFactory;
import algorithm.state.StateManager;
import algorithm.state.StateUpdater;

/**
 * Updates load at route level, i.e. modifies StateTypes.LOAD for each route.
 * 
 * @author stefan
 *
 */
class UpdateLoadAtRouteLevel implements JobInsertedListener, InsertionStartsListener, StateUpdater{

	private StateManager states;
	
	/**
	 * Updates load at route level, i.e. modifies StateTypes.LOAD for each route.
	 * 
	 * @author stefan
	 *
	 */
	public UpdateLoadAtRouteLevel(StateManager states) {
		super();
		this.states = states;
	}

	@Override
	public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
		if(!(job2insert instanceof Service)){
			return;
		}
		double oldLoad = states.getRouteState(inRoute, StateFactory.LOAD).toDouble();
		states.putRouteState(inRoute, StateFactory.LOAD, StateFactory.createState(oldLoad + job2insert.getCapacityDemand()));
	}

	@Override
	public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
		for(VehicleRoute route : vehicleRoutes){
			int load = 0;
			for(Job j : route.getTourActivities().getJobs()){
				load += j.getCapacityDemand();
			}
			states.putRouteState(route, StateFactory.LOAD, StateFactory.createState(load));
		}
		
	}

}