package algorithms;

import basics.Service;
import basics.algo.InsertionContext;
import basics.algo.StateGetter;
import basics.constraints.HardRouteStateLevelConstraint;

class LoadConstraint implements HardRouteStateLevelConstraint{

	private StateGetter states;
	
	public LoadConstraint(StateGetter states) {
		super();
		this.states = states;
	}

	@Override
	public boolean fulfilled(InsertionContext insertionContext) {
		int currentLoad = (int) states.getRouteState(insertionContext.getRoute(), StateFactory.LOAD).toDouble();
		Service service = (Service) insertionContext.getJob();
		if(currentLoad + service.getCapacityDemand() > insertionContext.getNewVehicle().getCapacity()){
			return false;
		}
		return true;
	}
}