package problem.constraint;

import problem.solution.route.activity.TourActivity;
import algorithm.recreate.InsertionContext;

public interface HardActivityStateLevelConstraint {
	
	static enum ConstraintsStatus {
		
		NOT_FULFILLED_BREAK, NOT_FULFILLED, FULFILLED;

	}
	
	public ConstraintsStatus fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime);

}