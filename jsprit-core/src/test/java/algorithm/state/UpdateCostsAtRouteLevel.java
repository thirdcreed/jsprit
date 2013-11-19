package algorithm.state;

import java.util.Collection;

import problem.cost.VehicleRoutingActivityCosts;
import problem.cost.VehicleRoutingTransportCosts;
import problem.job.Job;
import problem.solution.route.RouteActivityVisitor;
import problem.solution.route.VehicleRoute;
import algorithm.recreate.listener.InsertionEndsListener;
import algorithm.recreate.listener.InsertionStartsListener;
import algorithm.recreate.listener.JobInsertedListener;
import algorithm.state.StateFactory;
import algorithm.state.StateManager;
import algorithm.state.StateUpdater;
import algorithm.state.UpdateVariableCosts;

class UpdateCostsAtRouteLevel implements StateUpdater,JobInsertedListener, InsertionStartsListener, InsertionEndsListener{
		
		private StateManager states;
		
		private VehicleRoutingTransportCosts tpCosts;
		
		private VehicleRoutingActivityCosts actCosts;
		
		public UpdateCostsAtRouteLevel(StateManager states, VehicleRoutingTransportCosts tpCosts, VehicleRoutingActivityCosts actCosts) {
			super();
			this.states = states;
			this.tpCosts = tpCosts;
			this.actCosts = actCosts;
		}

		@Override
		public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
//			inRoute.getVehicleRouteCostCalculator().addTransportCost(additionalCosts);
			double oldCosts = states.getRouteState(inRoute, StateFactory.COSTS).toDouble();
			oldCosts += additionalCosts;
			states.putRouteState(inRoute, StateFactory.COSTS, StateFactory.createState(oldCosts));
		}

		@Override
		public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
			RouteActivityVisitor forwardInTime = new RouteActivityVisitor();
			forwardInTime.addActivityVisitor(new UpdateVariableCosts(actCosts, tpCosts, states));
			for(VehicleRoute route : vehicleRoutes){
				forwardInTime.visit(route);
			}
			
		}

		@Override
		public void informInsertionEnds(Collection<VehicleRoute> vehicleRoutes) {
			
//			IterateRouteForwardInTime forwardInTime = new IterateRouteForwardInTime(tpCosts);
//			forwardInTime.addListener(new UpdateCostsAtAllLevels(actCosts, tpCosts, states));
			for(VehicleRoute route : vehicleRoutes){
				if(route.isEmpty()) continue;
				route.getVehicleRouteCostCalculator().reset();
				route.getVehicleRouteCostCalculator().addOtherCost(states.getRouteState(route, StateFactory.COSTS).toDouble());
				route.getVehicleRouteCostCalculator().price(route.getVehicle());
//				forwardInTime.iterate(route);
			}
			
		}

	}