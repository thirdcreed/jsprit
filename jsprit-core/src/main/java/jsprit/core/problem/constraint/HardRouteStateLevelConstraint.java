package jsprit.core.problem.constraint;

import jsprit.core.algorithm.recreate.InsertionContext;


public interface HardRouteStateLevelConstraint {

	public boolean fulfilled(InsertionContext insertionContext);
	
}