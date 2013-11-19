package problem.constraint;

import problem.constraint.HardRouteStateLevelConstraint;
import problem.job.Service;
import algorithm.recreate.InsertionContext;
import algorithm.state.StateFactory;
import algorithm.state.StateGetter;

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