package jsprit.core.problem.constraint;

import jsprit.core.algorithm.recreate.InsertionContext;
import jsprit.core.algorithm.state.StateFactory;
import jsprit.core.algorithm.state.StateGetter;
import jsprit.core.problem.solution.route.activity.DeliveryActivity;
import jsprit.core.problem.solution.route.activity.PickupActivity;
import jsprit.core.problem.solution.route.activity.ServiceActivity;
import jsprit.core.problem.solution.route.activity.Start;
import jsprit.core.problem.solution.route.activity.TourActivity;

/**
 * Ensures load constraint for inserting ServiceActivity.
 * 
 * <p>When using this, you need to use<br>
 * 
 * 
 * @author schroeder
 *
 */
class ServiceLoadActivityLevelConstraint implements HardActivityStateLevelConstraint {
	
	private StateGetter stateManager;
	
	public ServiceLoadActivityLevelConstraint(StateGetter stateManager) {
		super();
		this.stateManager = stateManager;
	}

	@Override
	public ConstraintsStatus fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		int loadAtPrevAct;
		int futurePicks;
		int pastDeliveries;
		if(prevAct instanceof Start){
			loadAtPrevAct = (int)stateManager.getRouteState(iFacts.getRoute(), StateFactory.LOAD_AT_BEGINNING).toDouble();
			futurePicks = (int)stateManager.getRouteState(iFacts.getRoute(), StateFactory.LOAD_AT_END).toDouble();
			pastDeliveries = 0;
		}
		else{
			loadAtPrevAct = (int) stateManager.getActivityState(prevAct, StateFactory.LOAD).toDouble();
			futurePicks = (int) stateManager.getActivityState(prevAct, StateFactory.FUTURE_PICKS).toDouble();
			pastDeliveries = (int) stateManager.getActivityState(prevAct, StateFactory.PAST_DELIVERIES).toDouble();
		}
		if(newAct instanceof PickupActivity || newAct instanceof ServiceActivity){
			if(loadAtPrevAct + newAct.getCapacityDemand() + futurePicks > iFacts.getNewVehicle().getCapacity()){
				return ConstraintsStatus.NOT_FULFILLED;
			}
		}
		if(newAct instanceof DeliveryActivity){
			if(loadAtPrevAct + Math.abs(newAct.getCapacityDemand()) + pastDeliveries > iFacts.getNewVehicle().getCapacity()){
				return ConstraintsStatus.NOT_FULFILLED;
			}
			
		}
		return ConstraintsStatus.FULFILLED;
	}
		
}