package algorithms;

import basics.VehicleRoutingProblem;
import basics.algo.RuinStrategy;
import basics.algo.RuinStrategyFactory;

public class RandomRuinStrategyFactory implements RuinStrategyFactory{

	private double fraction;
	
	public RandomRuinStrategyFactory(double fraction) {
		super();
		this.fraction = fraction;
	}

	@Override
	public RuinStrategy createStrategy(VehicleRoutingProblem vrp) {
		return new RuinRandom(vrp, fraction);
	}

}
