package algorithm.recreate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import problem.VehicleRoutingProblem;
import problem.constraint.ConstraintManager;
import problem.vehicle.VehicleFleetManager;
import algorithm.listener.VehicleRoutingAlgorithmListeners.PrioritizedVRAListener;
import algorithm.recreate.listener.InsertionListener;
import algorithm.state.StateManager;

public class BestInsertionBuilder {

	private VehicleRoutingProblem vrp;
	
	private StateManager stateManager;
	
	private boolean local = true;
	
	private ConstraintManager constraintManager;

	private VehicleFleetManager fleetManager;

	private double weightOfFixedCosts;

	private boolean considerFixedCosts = false;

	private ActivityInsertionCostsCalculator actInsertionCostsCalculator = null;

	private int forwaredLooking;

	private int memory;

	private ExecutorService executor;

	private int nuOfThreads;

	private boolean timeScheduling;

	private double timeSlice;

	private int neighbors;
	
	public BestInsertionBuilder(VehicleRoutingProblem vrp, VehicleFleetManager vehicleFleetManager, StateManager stateManager, ConstraintManager constraintManager) {
		super();
		this.vrp = vrp;
		this.stateManager = stateManager;
		this.constraintManager = constraintManager;
		this.fleetManager = vehicleFleetManager;
	}
		
	public BestInsertionBuilder setRouteLevel(int forwardLooking, int memory){

		local = false;
		this.forwaredLooking = forwardLooking;
		this.memory = memory;
		return this;
	};
	
	public BestInsertionBuilder setLocalLevel(){
		local = true;
		return this;
	};
	
	public BestInsertionBuilder considerFixedCosts(double weightOfFixedCosts){
		this.weightOfFixedCosts = weightOfFixedCosts;
		this.considerFixedCosts  = true;
		return this;
	}
	
	public BestInsertionBuilder setActivityInsertionCostCalculator(ActivityInsertionCostsCalculator activityInsertionCostsCalculator){
		this.actInsertionCostsCalculator = activityInsertionCostsCalculator;
		return this;
	};
	
	public BestInsertionBuilder setConcurrentMode(ExecutorService executor, int nuOfThreads){
		this.executor = executor;
		this.nuOfThreads = nuOfThreads;
		return this;
	}
	
	public InsertionStrategy build() {
		List<InsertionListener> iListeners = new ArrayList<InsertionListener>();
		List<PrioritizedVRAListener> algorithmListeners = new ArrayList<PrioritizedVRAListener>();
		CalculatorBuilder calcBuilder = new CalculatorBuilder(iListeners, algorithmListeners);
		if(local){
			calcBuilder.setLocalLevel();
		}
		else {
			calcBuilder.setRouteLevel(forwaredLooking, memory);
		}
		calcBuilder.setConstraintManager(constraintManager);
		calcBuilder.setStates(stateManager);
		calcBuilder.setVehicleRoutingProblem(vrp);
		calcBuilder.setVehicleFleetManager(fleetManager);
		calcBuilder.setActivityInsertionCostsCalculator(actInsertionCostsCalculator);
		if(considerFixedCosts) {
			calcBuilder.considerFixedCosts(weightOfFixedCosts);
		}
		if(timeScheduling){
			calcBuilder.experimentalTimeScheduler(timeSlice, neighbors);
		}
		JobInsertionCostsCalculator jobInsertions = calcBuilder.build();
		InsertionStrategy bestInsertion;
		if(executor == null){
			bestInsertion = new BestInsertion(jobInsertions);
			
		}
		else{
			bestInsertion = new BestInsertionConcurrent(jobInsertions,executor,nuOfThreads);
		}
		for(InsertionListener l : iListeners) bestInsertion.addListener(l);
		return bestInsertion;
	}

	/**
	 * @deprecated it is only use experimentally and might disappear.
	 * @param parseDouble
	 * @param parseInt
	 */
	@Deprecated
	public void experimentalTimeScheduler(double timeSlice, int neighbors) {
		timeScheduling = true;
		this.timeSlice = timeSlice;
		this.neighbors = neighbors;
		
	}

}
