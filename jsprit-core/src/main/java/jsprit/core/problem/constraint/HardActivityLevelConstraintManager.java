package jsprit.core.problem.constraint;

import java.util.ArrayList;
import java.util.Collection;

import jsprit.core.algorithm.recreate.InsertionContext;
import jsprit.core.problem.solution.route.activity.TourActivity;


class HardActivityLevelConstraintManager implements HardActivityStateLevelConstraint {

	private Collection<HardActivityStateLevelConstraint> hardConstraints = new ArrayList<HardActivityStateLevelConstraint>();
	
	public void addConstraint(HardActivityStateLevelConstraint constraint){
		hardConstraints.add(constraint);
	}
	
	@Override
	public ConstraintsStatus fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		for(HardActivityStateLevelConstraint constraint : hardConstraints){
			ConstraintsStatus status = constraint.fulfilled(iFacts, prevAct, newAct, nextAct, prevActDepTime);
			if(status.equals(ConstraintsStatus.NOT_FULFILLED_BREAK) || status.equals(ConstraintsStatus.NOT_FULFILLED)){
				return status;
			}
		}
		return ConstraintsStatus.FULFILLED;
	}
	
}