package algorithms;

import basics.VehicleRoutingProblem;
import basics.algo.InsertionStrategy;
import basics.algo.InsertionStrategyFactory;
import basics.algo.JobInsertionCostsCalculator;

public class BestInsertionStrategyFactory implements InsertionStrategyFactory{

	private JobInsertionCostsCalculator jobInsertionCalculator;
	
	public BestInsertionStrategyFactory(JobInsertionCostsCalculator jobInsertionCalculator) {
		super();
		this.jobInsertionCalculator = jobInsertionCalculator;
	}

	@Override
	public InsertionStrategy createStrategy(VehicleRoutingProblem vrp) {
		return new BestInsertion(jobInsertionCalculator);
	}

}
