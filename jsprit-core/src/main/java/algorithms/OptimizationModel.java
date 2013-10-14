package algorithms;

import java.util.Collection;

import basics.algo.SolutionCostFunction;

public interface OptimizationModel {
	
	public SolutionCostFunction getObjectiveFunction();
	
	public Collection<HardRouteLevelConstraint> getRouteLevelConstraints();
	
	public Collection<HardActivityLevelConstraint> getActivityLevelConstraints();
	
	public Collection<ActivityVisitor> getActivityLevelStateUpdaters();
	
	public Collection<RouteVisitor> getRouteLevelStateUpdaters();

}
