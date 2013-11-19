package problem.constraint;

import problem.job.Delivery;
import problem.job.Pickup;
import problem.job.Service;
import algorithm.recreate.InsertionContext;
import algorithm.state.StateFactory;
import algorithm.state.StateGetter;

/**
 * lsjdfjsdlfjsa
 * 
 * @author stefan
 *
 */
class ServiceLoadRouteLevelConstraint implements HardRouteStateLevelConstraint {

	private StateGetter stateManager;
	
	public ServiceLoadRouteLevelConstraint(StateGetter stateManager) {
		super();
		this.stateManager = stateManager;
	}

	@Override
	public boolean fulfilled(InsertionContext insertionContext) {
		if(insertionContext.getJob() instanceof Delivery){
			int loadAtDepot = (int) stateManager.getRouteState(insertionContext.getRoute(), StateFactory.LOAD_AT_BEGINNING).toDouble();
			if(loadAtDepot + insertionContext.getJob().getCapacityDemand() > insertionContext.getNewVehicle().getCapacity()){
				return false;
			}
		}
		else if(insertionContext.getJob() instanceof Pickup || insertionContext.getJob() instanceof Service){
			int loadAtEnd = (int) stateManager.getRouteState(insertionContext.getRoute(), StateFactory.LOAD_AT_END).toDouble();
			if(loadAtEnd + insertionContext.getJob().getCapacityDemand() > insertionContext.getNewVehicle().getCapacity()){
				return false;
			}
		}
		return true;
	}
	
}