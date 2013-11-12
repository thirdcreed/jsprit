package algorithms;

import basics.VehicleRoutingProblem;
import basics.algo.JobDistance;
import basics.algo.RuinStrategy;
import basics.algo.RuinStrategyFactory;

public class RadialRuinStrategyFactory implements RuinStrategyFactory{

	private double fraction;
	
	private JobDistance jobDistance;
	
	public RadialRuinStrategyFactory(double fraction, JobDistance jobDistance) {
		super();
		this.fraction = fraction;
		this.jobDistance = jobDistance;
	}

	@Override
	public RuinStrategy createStrategy(VehicleRoutingProblem vrp) {
		return new RuinRadial(vrp,fraction,jobDistance);
	}

}
