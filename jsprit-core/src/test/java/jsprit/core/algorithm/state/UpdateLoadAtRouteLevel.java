package jsprit.core.algorithm.state;

import java.util.Collection;

import jsprit.core.algorithm.recreate.listener.InsertionStartsListener;
import jsprit.core.algorithm.recreate.listener.JobInsertedListener;
import jsprit.core.algorithm.state.StateFactory;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.algorithm.state.StateUpdater;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.route.VehicleRoute;


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