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
package algorithm.modules;

import java.util.Collection;

import problem.job.Job;
import problem.solution.VehicleRoutingProblemSolution;
import algorithm.SearchStrategyModule;
import algorithm.listener.SearchStrategyModuleListener;
import algorithm.recreate.InsertionStrategy;
import algorithm.recreate.listener.InsertionListener;
import algorithm.ruin.RuinStrategy;
import algorithm.ruin.listener.RuinListener;


public class RuinAndRecreateModule implements SearchStrategyModule{

	private InsertionStrategy insertion;
	
	private RuinStrategy ruin;
	
	private String moduleName;
	
	public RuinAndRecreateModule(String moduleName, InsertionStrategy insertion, RuinStrategy ruin) {
		super();
		this.insertion = insertion;
		this.ruin = ruin;
		this.moduleName = moduleName;
	}

	@Override
	public VehicleRoutingProblemSolution runAndGetSolution(VehicleRoutingProblemSolution vrpSolution) {
		Collection<Job> ruinedJobs = ruin.ruin(vrpSolution.getRoutes());
		insertion.insertJobs(vrpSolution.getRoutes(), ruinedJobs);
		return vrpSolution;

	}

	@Override
	public String getName() {
		return moduleName;
	}

	@Override
	public void addModuleListener(SearchStrategyModuleListener moduleListener) {
		if(moduleListener instanceof InsertionListener){
			InsertionListener iListener = (InsertionListener) moduleListener; 
			if(!insertion.getListeners().contains(iListener)){
				insertion.addListener(iListener);
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
