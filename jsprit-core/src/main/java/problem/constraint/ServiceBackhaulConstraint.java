package problem.constraint;

import problem.solution.route.activity.DeliveryActivity;
import problem.solution.route.activity.PickupActivity;
import problem.solution.route.activity.ServiceActivity;
import problem.solution.route.activity.TourActivity;
import algorithm.recreate.InsertionContext;

public class ServiceBackhaulConstraint implements HardActivityStateLevelConstraint {

	@Override
	public ConstraintsStatus fulfilled(InsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
		if(newAct instanceof PickupActivity && nextAct instanceof DeliveryActivity){ return ConstraintsStatus.NOT_FULFILLED; }
		if(newAct instanceof ServiceActivity && nextAct instanceof DeliveryActivity){ return ConstraintsStatus.NOT_FULFILLED; }
		if(newAct instanceof DeliveryActivity && prevAct instanceof PickupActivity){ return ConstraintsStatus.NOT_FULFILLED; }
		if(newAct instanceof DeliveryActivity && prevAct instanceof ServiceActivity){ return ConstraintsStatus.NOT_FULFILLED; }
		return ConstraintsStatus.FULFILLED;
	}
		
}