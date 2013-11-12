package algorithms;

import basics.Delivery;
import basics.Pickup;
import basics.Service;
import basics.algo.InsertionContext;
import basics.algo.StateGetter;
import basics.constraints.HardRouteStateLevelConstraint;

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