package jsprit.core.problem.constraint;

import jsprit.core.algorithm.recreate.InsertionContext;
import jsprit.core.algorithm.state.StateFactory;
import jsprit.core.algorithm.state.StateGetter;
import jsprit.core.problem.constraint.HardRouteStateLevelConstraint;
import jsprit.core.problem.job.Service;

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