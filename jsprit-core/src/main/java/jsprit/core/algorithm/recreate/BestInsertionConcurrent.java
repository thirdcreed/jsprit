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
package jsprit.core.algorithm.recreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import jsprit.core.algorithm.recreate.InsertionData.NoInsertionData;
import jsprit.core.algorithm.recreate.listener.InsertionListener;
import jsprit.core.algorithm.recreate.listener.InsertionListeners;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.util.RandomNumberGeneration;

import org.apache.log4j.Logger;




/**
 * 
 * @author stefan schroeder
 * 
 */

final class BestInsertionConcurrent implements InsertionStrategy{
	
	static class Batch {
		List<VehicleRoute> routes = new ArrayList<VehicleRoute>();
		
	}
	
	class Insertion {
		
		private final VehicleRoute route;
		
		private final InsertionData insertionData;

		public Insertion(VehicleRoute vehicleRoute, InsertionData insertionData) {
			super();
			this.route = vehicleRoute;
			this.insertionData = insertionData;
		}

		public VehicleRoute getRoute() {
			return route;
		}
		
		public InsertionData getInsertionData() {
			return insertionData;
		}
		
	}
	
	private static Logger logger = Logger.getLogger(BestInsertionConcurrent.class);

	private Random random = RandomNumberGeneration.getRandom();
	
	private final static double NO_NEW_DEPARTURE_TIME_YET = -12345.12345;
	
	private final static Vehicle NO_NEW_VEHICLE_YET = null;
	
	private final static Driver NO_NEW_DRIVER_YET = null;
	
	private InsertionListeners insertionsListeners;
	
	private Inserter inserter;
	
	private JobInsertionCostsCalculator bestInsertionCostCalculator;

	private boolean minVehiclesFirst = false;
	
	private int nuOfBatches;
	
	private ExecutorService executor;
	
	private ExecutorCompletionService<Insertion> completionService;

	public void setRandom(Random random) {
		this.random = random;
	}
	
	public BestInsertionConcurrent(JobInsertionCostsCalculator jobInsertionCalculator, ExecutorService executorService, int nuOfBatches) {
		super();
		this.insertionsListeners = new InsertionListeners();
		this.executor = executorService;
		this.nuOfBatches = nuOfBatches;
		inserter = new Inserter(insertionsListeners);
		bestInsertionCostCalculator = jobInsertionCalculator;
		completionService = new ExecutorCompletionService<Insertion>(executor);
		logger.info("initialise " + this);
	}

	@Override
	public String toString() {
		return "[name=bestInsertion]";
	}

	@Override
	public void insertJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
		insertionsListeners.informInsertionStarts(vehicleRoutes,unassignedJobs);
		List<Job> unassignedJobList = new ArrayList<Job>(unassignedJobs);
		Collections.shuffle(unassignedJobList, random);
		
		List<Batch> batches = distributeRoutes(vehicleRoutes,nuOfBatches);
		
		for(final Job unassignedJob : unassignedJobList){			
			
			Insertion bestInsertion = null;
			double bestInsertionCost = Double.MAX_VALUE;
			
			for(final Batch batch : batches){
				completionService.submit(new Callable<Insertion>() {
					
					@Override
					public Insertion call() throws Exception {
						return getBestInsertion(batch,unassignedJob);
					}
					
				});
				
			}
			
			try{
				for(int i=0;i<batches.size();i++){
					Future<Insertion> futureIData = completionService.take();
					Insertion insertion = futureIData.get();
					if(insertion == null) continue;
					if(insertion.getInsertionData().getInsertionCost() < bestInsertionCost){
						bestInsertion = insertion;
						bestInsertionCost = insertion.getInsertionData().getInsertionCost();
					}
				}
			}
			catch(InterruptedException e){
				Thread.currentThread().interrupt();
			} 
			catch (ExecutionException e) {
				e.printStackTrace();
				logger.error(e.getCause().toString());
				System.exit(1);
			}
			
			if(!minVehiclesFirst){
				VehicleRoute newRoute = VehicleRoute.emptyRoute();
				InsertionData newIData = bestInsertionCostCalculator.getInsertionData(newRoute, unassignedJob, NO_NEW_VEHICLE_YET, NO_NEW_DEPARTURE_TIME_YET, NO_NEW_DRIVER_YET, bestInsertionCost);
				if(newIData.getInsertionCost() < bestInsertionCost){
					bestInsertion = new Insertion(newRoute,newIData);
					bestInsertionCost = newIData.getInsertionCost();
					vehicleRoutes.add(newRoute);
					batches.get(0).routes.add(newRoute);
				}
			}	
			
			if(bestInsertion == null){
				VehicleRoute newRoute = VehicleRoute.emptyRoute();
				InsertionData bestI = bestInsertionCostCalculator.getInsertionData(newRoute, unassignedJob, NO_NEW_VEHICLE_YET, NO_NEW_DEPARTURE_TIME_YET, NO_NEW_DRIVER_YET, Double.MAX_VALUE);
				if(bestI instanceof InsertionData.NoInsertionData){
					throw new IllegalStateException(getErrorMsg(unassignedJob));
				}
				else{
					bestInsertion = new Insertion(newRoute,bestI);
					vehicleRoutes.add(newRoute);
				}
			}
//			logger.info("insert " + unassignedJob + " pickup@" + bestInsertion.getInsertionData().getPickupInsertionIndex() + " delivery@" + bestInsertion.getInsertionData().getDeliveryInsertionIndex());
			inserter.insertJob(unassignedJob, bestInsertion.getInsertionData(), bestInsertion.getRoute());
		}
		insertionsListeners.informInsertionEndsListeners(vehicleRoutes);
	}

	private String getErrorMsg(Job unassignedJob) {
		return "given the vehicles, could not insert job\n" +
				"\t" + unassignedJob + 
				"\n\tthis might have the following reasons:\n" + 
				"\t- no vehicle has the capacity to transport the job [check whether there is at least one vehicle that is capable to transport the job]\n" +
				"\t- the time-window cannot be met, even in a commuter tour the time-window is missed [check whether it is possible to reach the time-window on the shortest path or make hard time-windows soft]\n" +
				"\t- if you deal with finite vehicles, and the available vehicles are already fully employed, no vehicle can be found anymore to transport the job [add penalty-vehicles]";
	}

	@Override
	public void removeListener(InsertionListener insertionListener) {
		insertionsListeners.removeListener(insertionListener);
	}

	@Override
	public Collection<InsertionListener> getListeners() {
		return Collections.unmodifiableCollection(insertionsListeners.getListeners());
	}

	@Override
	public void addListener(InsertionListener insertionListener) {
		insertionsListeners.addListener(insertionListener);
		
	}
	
	private Insertion getBestInsertion(Batch batch, Job unassignedJob) {
		Insertion bestInsertion = null;
		double bestInsertionCost = Double.MAX_VALUE;
		for(VehicleRoute vehicleRoute : batch.routes){
			InsertionData iData = bestInsertionCostCalculator.getInsertionData(vehicleRoute, unassignedJob, NO_NEW_VEHICLE_YET, NO_NEW_DEPARTURE_TIME_YET, NO_NEW_DRIVER_YET, bestInsertionCost); 
			if(iData instanceof NoInsertionData) {
				continue;
			}
			if(iData.getInsertionCost() < bestInsertionCost){
				bestInsertion = new Insertion(vehicleRoute,iData);
				bestInsertionCost = iData.getInsertionCost();
			}
		}
		return bestInsertion;
	}
	
	private List<Batch> distributeRoutes(Collection<VehicleRoute> vehicleRoutes, int nuOfBatches) {
		List<Batch> batches = new ArrayList<Batch>();
		for(int i=0;i<nuOfBatches;i++) batches.add(new Batch()); 
		/*
		 * if route.size < nuOfBatches add as much routes as empty batches are available
		 * else add one empty route anyway
		 */
		if(vehicleRoutes.size()<nuOfBatches){
			int nOfNewRoutes = nuOfBatches-vehicleRoutes.size();
			for(int i=0;i<nOfNewRoutes;i++){
				vehicleRoutes.add(VehicleRoute.emptyRoute());
			}
		}
		else{
			vehicleRoutes.add(VehicleRoute.emptyRoute());
		}
		/*
		 * distribute routes to batches equally
		 */
		int count = 0;
		for(VehicleRoute route : vehicleRoutes){
			if(count == nuOfBatches) count=0;
			batches.get(count).routes.add(route);
			count++;
		}
		return batches;
	}


}
