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
package algorithm.recreate;

import org.apache.log4j.Logger;

import problem.constraint.HardActivityStateLevelConstraint;
import problem.constraint.HardActivityStateLevelConstraint.ConstraintsStatus;
import problem.constraint.HardRouteStateLevelConstraint;
import problem.cost.VehicleRoutingTransportCosts;
import problem.driver.Driver;
import problem.job.Job;
import problem.job.Service;
import problem.solution.route.VehicleRoute;
import problem.solution.route.activity.DefaultTourActivityFactory;
import problem.solution.route.activity.End;
import problem.solution.route.activity.Start;
import problem.solution.route.activity.TourActivity;
import problem.solution.route.activity.TourActivityFactory;
import problem.vehicle.Vehicle;
import problem.vehicle.VehicleImpl.NoVehicle;
import util.CalculationUtils;
import util.Neighborhood;
import algorithm.recreate.ActivityInsertionCostsCalculator.ActivityInsertionCosts;



final class ServiceInsertionCalculator implements JobInsertionCostsCalculator{

	private static final Logger logger = Logger.getLogger(ServiceInsertionCalculator.class);

	private HardRouteStateLevelConstraint hardRouteLevelConstraint;
	
	private HardActivityStateLevelConstraint hardActivityLevelConstraint;
	
	private Neighborhood neighborhood = new Neighborhood() {
		
		@Override
		public boolean areNeighbors(String location1, String location2) {
			return true;
		}
	};
	
	private ActivityInsertionCostsCalculator activityInsertionCostsCalculator;
	
	private VehicleRoutingTransportCosts transportCosts;
	
	private TourActivityFactory activityFactory;
	
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
		logger.info("initialise neighborhood " + neighborhood);
	}
	

	public ServiceInsertionCalculator(VehicleRoutingTransportCosts routingCosts, ActivityInsertionCostsCalculator activityInsertionCostsCalculator, HardRouteStateLevelConstraint hardRouteLevelConstraint, HardActivityStateLevelConstraint hardActivityLevelConstraint) {
		super();
		this.activityInsertionCostsCalculator = activityInsertionCostsCalculator;
		this.hardRouteLevelConstraint = hardRouteLevelConstraint;
		this.hardActivityLevelConstraint = hardActivityLevelConstraint;
		this.transportCosts = routingCosts;
		activityFactory = new DefaultTourActivityFactory();
		logger.info("initialise " + this);
	}
	
	@Override
	public String toString() {
		return "[name=calculatesServiceInsertion]";
	}
	
	/**
	 * Calculates the marginal cost of inserting job i locally. This is based on the
	 * assumption that cost changes can entirely covered by only looking at the predecessor i-1 and its successor i+1.
	 *  
	 */
	@Override
	public InsertionData getInsertionData(final VehicleRoute currentRoute, final Job jobToInsert, final Vehicle newVehicle, double newVehicleDepartureTime, final Driver newDriver, final double bestKnownCosts) {
		if(jobToInsert == null) throw new IllegalStateException("jobToInsert is missing.");
		if(newVehicle == null || newVehicle instanceof NoVehicle) throw new IllegalStateException("newVehicle is missing.");
		
		InsertionContext insertionContext = new InsertionContext(currentRoute, jobToInsert, newVehicle, newDriver, newVehicleDepartureTime);
		if(!hardRouteLevelConstraint.fulfilled(insertionContext)){
			return InsertionData.createEmptyInsertionData();
		}
		
		double bestCost = bestKnownCosts;
		ActivityInsertionCosts bestMarginals = null;
		Service service = (Service)jobToInsert;
		int insertionIndex = InsertionData.NO_INDEX;
		
		TourActivity deliveryAct2Insert = activityFactory.createActivity(service);
		
		Start start = Start.newInstance(newVehicle.getLocationId(), newVehicle.getEarliestDeparture(), newVehicle.getLatestArrival());
		start.setEndTime(newVehicleDepartureTime);
		End end = End.newInstance(newVehicle.getLocationId(), 0.0, newVehicle.getLatestArrival());
		
		TourActivity prevAct = start;
		double prevActStartTime = newVehicleDepartureTime;
		int actIndex = 0;
		boolean loopBroken = false;
		for(TourActivity nextAct : currentRoute.getTourActivities().getActivities()){
			if(neighborhood.areNeighbors(deliveryAct2Insert.getLocationId(), prevAct.getLocationId()) && neighborhood.areNeighbors(deliveryAct2Insert.getLocationId(), nextAct.getLocationId())){
				ConstraintsStatus status = hardActivityLevelConstraint.fulfilled(insertionContext, prevAct, deliveryAct2Insert, nextAct, prevActStartTime);
				if(status.equals(ConstraintsStatus.FULFILLED)){
					ActivityInsertionCosts mc = calculate(insertionContext, prevAct, nextAct, deliveryAct2Insert, prevActStartTime);
					if(mc.getAdditionalCosts() < bestCost){
						bestCost = mc.getAdditionalCosts();
						bestMarginals = mc;
						insertionIndex = actIndex;
					}
				}
				else if(status.equals(ConstraintsStatus.NOT_FULFILLED_BREAK)){
					loopBroken = true;
					break;
				}
			}
			double nextActArrTime = prevActStartTime + transportCosts.getTransportTime(prevAct.getLocationId(), nextAct.getLocationId(), prevActStartTime, newDriver, newVehicle);
			double nextActEndTime = CalculationUtils.getActivityEndTime(nextActArrTime, nextAct);
			prevActStartTime = nextActEndTime;
			prevAct = nextAct;
			actIndex++;
		}
		End nextAct = end;
		if(!loopBroken){
			if(neighborhood.areNeighbors(deliveryAct2Insert.getLocationId(), prevAct.getLocationId()) && neighborhood.areNeighbors(deliveryAct2Insert.getLocationId(), nextAct.getLocationId())){
				ConstraintsStatus status = hardActivityLevelConstraint.fulfilled(insertionContext, prevAct, deliveryAct2Insert, nextAct, prevActStartTime); 
				if(status.equals(ConstraintsStatus.FULFILLED)){
					ActivityInsertionCosts mc = calculate(insertionContext, prevAct, nextAct, deliveryAct2Insert, prevActStartTime);
					if(mc.getAdditionalCosts() < bestCost){
						bestCost = mc.getAdditionalCosts();
						bestMarginals = mc;
						insertionIndex = actIndex;
					}
				}
			}
		}

		if(insertionIndex == InsertionData.NO_INDEX) {
			return InsertionData.createEmptyInsertionData();
		}
		InsertionData insertionData = new InsertionData(bestCost, InsertionData.NO_INDEX, insertionIndex, newVehicle, newDriver);
		insertionData.setVehicleDepartureTime(newVehicleDepartureTime);
		insertionData.setAdditionalTime(bestMarginals.getAdditionalTime());
		return insertionData;
	}

	public ActivityInsertionCosts calculate(InsertionContext iFacts, TourActivity prevAct, TourActivity nextAct, TourActivity newAct, double departureTimeAtPrevAct) {	
		return activityInsertionCostsCalculator.getCosts(iFacts, prevAct, nextAct, newAct, departureTimeAtPrevAct);
		
	}
}
