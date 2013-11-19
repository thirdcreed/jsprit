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
package jsprit.core.algorithm.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jsprit.core.algorithm.SearchStrategyModule;
import jsprit.core.algorithm.listener.SearchStrategyModuleListener;
import jsprit.core.algorithm.recreate.Inserter;
import jsprit.core.algorithm.recreate.InsertionStrategy;
import jsprit.core.algorithm.recreate.listener.InsertionListener;
import jsprit.core.algorithm.recreate.listener.InsertionListeners;
import jsprit.core.algorithm.ruin.RuinStrategy;
import jsprit.core.algorithm.ruin.listener.RuinListener;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;
import jsprit.core.problem.vehicle.VehicleFleetManager;
import jsprit.core.util.RandomNumberGeneration;

import org.apache.log4j.Logger;




public final class Gendreau implements SearchStrategyModule{

	private final static Logger log = Logger.getLogger(Gendreau.class);
	
	private final static String NAME = "gendreauPostOpt"; 
	
	private final RuinStrategy ruin;
	
	private final VehicleRoutingProblem vrp;
	
	private final InsertionStrategy insertionStrategy;
	
	private VehicleFleetManager fleetManager;

	private Random random = RandomNumberGeneration.getRandom();
	
	private int nOfIterations = 10;

	private double shareOfJobsToRuin = 0.15;

	public void setShareOfJobsToRuin(double shareOfJobsToRuin) {
		this.shareOfJobsToRuin = shareOfJobsToRuin;
	}

	public Gendreau(VehicleRoutingProblem vrp, RuinStrategy ruin, InsertionStrategy insertionStrategy, VehicleFleetManager vehicleFleetManager) {
		super();
		InsertionListeners insertionListeners = new InsertionListeners();
		insertionListeners.addAllListeners(insertionStrategy.getListeners());
		new Inserter(insertionListeners);
		this.ruin = ruin;
		this.vrp = vrp;
		this.insertionStrategy = insertionStrategy;
		this.fleetManager = vehicleFleetManager;
	}

	@Override
	public String toString() {
		return "[name=gendreau][iterations="+nOfIterations+"][share2ruin="+shareOfJobsToRuin+"]";
	}
	
	public void setRandom(Random random) {
		this.random = random;
	}


	public void setNuOfIterations(int nOfIterations) {
		this.nOfIterations = nOfIterations;
	}

//	public void setFleetManager(VehicleFleetManager vehicleFleetManager) {
//		this.fleetManager = vehicleFleetManager;
//		
//	}

	@Override
	public VehicleRoutingProblemSolution runAndGetSolution(VehicleRoutingProblemSolution vrpSolution) {
//		log.info("run gendreau postopt");
		VehicleRoutingProblemSolution bestSolution = vrpSolution;
		int itersWithoutImprovement = 0;
		
		for(int i=0;i<nOfIterations;i++){
			List<VehicleRoute> copiedRoutes = copyRoutes(bestSolution.getRoutes());
			iniFleet(copiedRoutes);
				
			VehicleRoute route2split = pickRouteThatHasAtLeastTwoJobs(copiedRoutes);
			if(route2split == null) continue;
			List<Job> jobsInRoute = getJobs(route2split);
			Set<Job> unassignedJobs = new HashSet<Job>();
			unassignedJobs.addAll(jobsInRoute);
			copiedRoutes.remove(route2split);
			
			Collections.shuffle(jobsInRoute,random);
			Job targetJob = jobsInRoute.get(0);
			int nOfJobs2BeRemovedAdditionally = (int) (shareOfJobsToRuin*(double)vrp.getJobs().size());
			Collection<Job> unassignedJobsList = ruin.ruin(copiedRoutes, targetJob, nOfJobs2BeRemovedAdditionally);
			unassignedJobs.addAll(unassignedJobsList);
			
			VehicleRoute emptyRoute1 = VehicleRoute.emptyRoute();
			copiedRoutes.add(emptyRoute1);
			insertionStrategy.insertJobs(Arrays.asList(emptyRoute1), Arrays.asList(targetJob));

			unassignedJobs.remove(targetJob);
			
			VehicleRoute emptyRoute2 = VehicleRoute.emptyRoute();
			copiedRoutes.add(emptyRoute2);
			Job job2 = jobsInRoute.get(1);
			insertionStrategy.insertJobs(Arrays.asList(emptyRoute2), Arrays.asList(job2));

			unassignedJobs.remove(job2);
			
			insertionStrategy.insertJobs(copiedRoutes, unassignedJobs);
			double cost = getCost(copiedRoutes);
			
			if(cost < bestSolution.getCost()){
//				log.info("BING - new: " + cost + " old: " + bestSolution.getCost());
				bestSolution = new VehicleRoutingProblemSolution(copiedRoutes, cost);
				itersWithoutImprovement=0;
			}
			else{
				itersWithoutImprovement++;
				if(itersWithoutImprovement > 200){
//					log.info("BREAK i="+i);
					break;
				}
			}
		}
		return bestSolution;
	}

	private List<VehicleRoute> copyRoutes(Collection<VehicleRoute> routes) {
		List<VehicleRoute> routeList = new ArrayList<VehicleRoute>();
		for(VehicleRoute r : routes){
			routeList.add(VehicleRoute.copyOf(r));
		}
		return routeList;
	}

	private void iniFleet(Collection<VehicleRoute> routes) {
		fleetManager.unlockAll();
		for(VehicleRoute route : routes){
			if(!route.isEmpty()){
				fleetManager.lock(route.getVehicle());
			}
		}
	}

	private double getCost(Collection<VehicleRoute> routes) {
		double c = 0.0;
		for(VehicleRoute r : routes){
			c+=r.getCost();
		}
		return c;
	}

	private List<Job> getJobs(VehicleRoute route2split) {
		Set<Job> jobs = new HashSet<Job>();
		for(TourActivity act : route2split.getTourActivities().getActivities()){
			if(act instanceof JobActivity){
				jobs.add(((JobActivity) act).getJob());
			}
		}
		return new ArrayList<Job>(jobs);
	}

	private VehicleRoute pickRouteThatHasAtLeastTwoJobs(Collection<VehicleRoute> routeList) {
		List<VehicleRoute> routes = new ArrayList<VehicleRoute>();
		for(VehicleRoute r : routeList){
			if(getJobs(r).size() > 1){
				routes.add(r);
			}
		}
		if(routes.isEmpty()) return null;
		Collections.shuffle(routes,random);
		return routes.get(0);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addModuleListener(SearchStrategyModuleListener moduleListener) {
		if(moduleListener instanceof InsertionListener){
			InsertionListener iListener = (InsertionListener) moduleListener; 
			if(!insertionStrategy.getListeners().contains(iListener)){
				insertionStrategy.addListener(iListener);
			}
		}
		if(moduleListener instanceof RuinListener){
			RuinListener rListener = (RuinListener) moduleListener;
			if(!ruin.getListeners().contains(rListener)){
				ruin.addListener(rListener);
			}
		}
		
	}
}
