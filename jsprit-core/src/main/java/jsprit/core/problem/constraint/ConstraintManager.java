package jsprit.core.problem.constraint;

import jsprit.core.algorithm.recreate.InsertionContext;
import jsprit.core.algorithm.state.StateGetter;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Constraint;
import jsprit.core.problem.solution.route.activity.TourActivity;

public class ConstraintManager implements HardActivityStateLevelConstraint, HardRouteStateLevelConstraint{

	private HardActivityLevelConstraintManager actLevelConstraintManager = new HardActivityLevelConstraintManager();
	
	private HardRouteLevelConstraintManager routeLevelConstraintManager = new HardRouteLevelConstraintManager();
	
	private VehicleRoutingProblem vrp;
	
	private StateGetter stateManager;
	
	private boolean loadConstraintsSet = false;
	
	private boolean timeWindowConstraintsSet = false;
	
	public ConstraintManager(VehicleRoutingProblem vrp, StateGetter stateGetter) {
		this.vrp = vrp;
		this.stateManager = stateGetter;
	}
	
	public void addTimeWindowConstraint(){
		if(!timeWindowConstraintsSet){
			addConstraint(new TimeWindowConstraint(stateManager, vrp.getTransportCosts()));
			timeWindowConstraintsSet = true;
		}
	}

	public void addLoadConstraint(){
		if(!loadConstraintsSet){
			if(vrp.getProblemConstraints().contains(Constraint.DELIVERIES_FIRST)){
				addConstraint(new ServiceBackhaulConstraint());
			}
			addConstraint(new ServiceLoadActivityLevelConstraint(stateManager));
			addConstraint(new ServiceLoadRouteLevelConstraint(stateManager));
			loadConstraintsSet=true;
		}
	}
	
	public void addConstraint(HardActivityStateLevelConstraint actLevelConstraint){
		actLevelConstraintManager.addConstraint(actLevelConstraint);
	}
	
	public void addConstraint(HardRouteStateLevelConstraint routeLevelConstraint){
		routeLevelConstraintManager.addConstraint(routeLevelConstraint);
	}
	
	@Override
	public boolean fulfilled(InsertionContext insertionContext) {
		return routeLevelConstraintManager.fulfilled(insertionContext);
	}

	@Override
	public ConstraintsStatus fulfilled(InsertionContext iFacts, TourActivity prevAct,TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		return actLevelConstraintManager.fulfilled(iFacts, prevAct, newAct, nextAct, prevActDepTime);
	}
	
}