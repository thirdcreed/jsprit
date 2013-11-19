/*******************************************************************************
 * Copyright (C) 2013  Stefan Schroeder
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package jsprit.core.algorithm.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jsprit.core.algorithm.recreate.listener.InsertionListeners;
import jsprit.core.algorithm.recreate.listener.InsertionStartsListener;
import jsprit.core.algorithm.recreate.listener.JobInsertedListener;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.algorithm.state.TimeWindowUpdater;
import jsprit.core.algorithm.state.UpdateActivityTimes;
import jsprit.core.algorithm.state.UpdateLoads;
import jsprit.core.algorithm.state.UpdateMaxLoad;
import jsprit.core.algorithm.state.UpdateVariableCosts;
import jsprit.core.problem.cost.VehicleRoutingActivityCosts;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.solution.route.ReverseRouteActivityVisitor;
import jsprit.core.problem.solution.route.RouteActivityVisitor;
import jsprit.core.problem.solution.route.VehicleRoute;




public class UpdateStates implements JobInsertedListener, InsertionStartsListener{

		private RouteActivityVisitor routeActivityVisitor;
		
		private ReverseRouteActivityVisitor revRouteActivityVisitor;
		
		private InsertionListeners insertionListeners = new InsertionListeners();
		
		public UpdateStates(StateManager states, VehicleRoutingTransportCosts routingCosts, VehicleRoutingActivityCosts activityCosts) {
			routeActivityVisitor = new RouteActivityVisitor();
			routeActivityVisitor.addActivityVisitor(new UpdateActivityTimes(routingCosts));
			routeActivityVisitor.addActivityVisitor(new UpdateVariableCosts(activityCosts, routingCosts, states));
			routeActivityVisitor.addActivityVisitor(new UpdateLoads(states));
			routeActivityVisitor.addActivityVisitor(new UpdateMaxLoad(states));
			revRouteActivityVisitor = new ReverseRouteActivityVisitor();
			revRouteActivityVisitor.addActivityVisitor(new TimeWindowUpdater(states, routingCosts));
			insertionListeners.addListener(new UpdateLoads(states));
//			insertionListeners.addListener(new UpdateLoadsAtStartAndEndOfRouteWhenJobHasBeenInserted(states));
		}
		
		public void update(VehicleRoute route){
			List<VehicleRoute> routes = Arrays.asList(route);
			insertionListeners.informInsertionStarts(routes, Collections.EMPTY_LIST);
			routeActivityVisitor.visit(route);
			revRouteActivityVisitor.visit(route);
		}

		@Override
		public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
			insertionListeners.informJobInserted(job2insert, inRoute, additionalCosts, additionalTime);
			routeActivityVisitor.visit(inRoute);
			revRouteActivityVisitor.visit(inRoute);
		}

		@Override
		public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes,Collection<Job> unassignedJobs) {
			insertionListeners.informInsertionStarts(vehicleRoutes, unassignedJobs);
			for(VehicleRoute route : vehicleRoutes) {
				routeActivityVisitor.visit(route);
				revRouteActivityVisitor.visit(route);
			}
		}

	}


