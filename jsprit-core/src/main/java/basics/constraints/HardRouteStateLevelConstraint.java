package basics.constraints;

import basics.algo.InsertionContext;


public interface HardRouteStateLevelConstraint {

	public boolean fulfilled(InsertionContext insertionContext);
	
}