package analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import problem.VehicleRoutingProblem;
import problem.solution.VehicleRoutingProblemSolution;

import util.Solutions;
import algorithm.SearchStrategy.DiscoveredSolution;
import algorithm.listener.AlgorithmEndsListener;
import algorithm.listener.IterationStartsListener;
import algorithm.listener.SelectedStrategySuccessListener;

public class StrategySuccessListener implements SelectedStrategySuccessListener, IterationStartsListener, AlgorithmEndsListener{

	static class SolWrap {
		double currentBest;
		int nVehicleBest;
		double discoveredSolution;
		int nVehicle;
		boolean accepted;
		String stratName;
		
		public SolWrap(double currentBest, int nVehicleBest, double discoveredSolution, int nVehicle,
				boolean accepted, String stratName) {
			super();
			this.currentBest = currentBest;
			this.discoveredSolution = discoveredSolution;
			this.accepted = accepted;
			this.stratName = stratName;
			this.nVehicleBest=nVehicleBest;
			this.nVehicle=nVehicle;
		}
		
		
	}
	
	private static Logger log = Logger.getLogger(StrategySuccessListener.class);
	
	private List<SolWrap> solutions = new ArrayList<SolWrap>();
	
	private String filename;
	
	private VehicleRoutingProblemSolution best;
	
	public StrategySuccessListener(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public void informSuccessOfStrategy(String name, DiscoveredSolution solution) {
		solutions.add(new SolWrap(best.getCost(),best.getRoutes().size(),solution.getSolution().getCost(),solution.getSolution().getRoutes().size(),solution.isAccepted(),solution.getStrategyName()));
		
	}

	@Override
	public void informAlgorithmEnds(VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		log.info("write strategy-success to " + filename);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
			writer.write("iteration\tstrategyName\taccepted\tbest\tsolution\tbestNuVehicles\tsolNuVehicles\n");
			int counter = 1;
			for(SolWrap sol : this.solutions){
				writer.write(counter+"\t"+sol.stratName+"\t"+sol.accepted+"\t"+sol.currentBest+"\t"+sol.discoveredSolution+
						"\t"+sol.nVehicleBest+"\t"+sol.nVehicle+"\n");
				counter++;
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void informIterationStarts(int i, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		best = Solutions.bestOf(solutions);
	}

}
