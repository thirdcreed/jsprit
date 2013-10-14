package basics.algo;

import basics.VehicleRoutingProblemSolution;

public interface SolutionCostFunction {
	
	/**
	 * Calculates total solution costs.
	 * 
	 * @param solution
	 * @return TODO
	 */
	public double getValue(VehicleRoutingProblemSolution solution);

}
