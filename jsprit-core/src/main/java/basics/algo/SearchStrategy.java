/*******************************************************************************
 * Copyright (C) 2013  Stefan Schroeder
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Contributors:
 *     Stefan Schroeder - initial API and implementation
 ******************************************************************************/
package basics.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

import algorithms.acceptors.SolutionAcceptor;
import algorithms.selectors.SolutionSelector;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblemSolution;





public class SearchStrategy {
	
	public static class DiscoveredSolution {
		private VehicleRoutingProblemSolution solution;
		private boolean accepted;
		private String strategyName;
		
		public DiscoveredSolution(VehicleRoutingProblemSolution solution,boolean accepted, String strategyName) {
			super();
			this.solution = solution;
			this.accepted = accepted;
			this.strategyName = strategyName;
		}

		public VehicleRoutingProblemSolution getSolution() {
			return solution;
		}

		public boolean isAccepted() {
			return accepted;
		}

		public String getStrategyName() {
			return strategyName;
		}
		
	}
	
	private static Logger logger = Logger.getLogger(SearchStrategy.class);
	
	private Collection<SearchStrategyModule> searchStrategyModules = new ArrayList<SearchStrategyModule>();
	
	private SolutionSelector solutionSelector;
	
	private SolutionCostCalculator solutionCostCalculator;

	private SolutionAcceptor solutionAcceptor;
	
	private String name;
	
	public SearchStrategy(SolutionSelector solutionSelector, SolutionAcceptor solutionAcceptor, SolutionCostCalculator solutionCostCalculator) {
		super();
		this.solutionSelector = solutionSelector;
		this.solutionAcceptor = solutionAcceptor;
		this.solutionCostCalculator = solutionCostCalculator;
		logger.info("initialise " + this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<SearchStrategyModule> getSearchStrategyModules() {
		return Collections.unmodifiableCollection(searchStrategyModules);
	}

	public SolutionSelector getSolutionSelector() {
		return solutionSelector;
	}

	public SolutionAcceptor getSolutionAcceptor() {
		return solutionAcceptor;
	}

	@Override
	public String toString() {
		return "searchStrategy [#modules="+searchStrategyModules.size()+"][selector="+solutionSelector+"][acceptor="+solutionAcceptor+"]";
	}
 
	/**
	 * Runs the search-strategy and its according modules, and returns DiscoveredSolution.
	 * 
	 * <p>This involves three basic steps: 1) Selecting a solution from solutions (input parameter) according to {@link SolutionSelector}, 2) running the modules 
	 * ({@link SearchStrategyModule}) on the selectedSolution and 3) accepting the new solution according to {@link SolutionAcceptor}. 
	 * <p> Note that after 1) the selected solution is copied, thus the original solution is not modified.
	 * <p> Note also that 3) modifies the input parameter solutions by adding, removing, replacing the existing solutions or whatever is defined in the solutionAcceptor.
	 *  
	 * @param vrp
	 * @param solutions which will be modified 
	 * @return discoveredSolutin 
	 * @see SolutionSelector, SearchStrategyModule, SolutionAcceptor 
	 */
	public DiscoveredSolution run(VehicleRoutingProblem vrp, Collection<VehicleRoutingProblemSolution> solutions){
		VehicleRoutingProblemSolution solution = solutionSelector.selectSolution(solutions);
		if(solution == null) throw new IllegalStateException("solution is null. check solutionSelector to return an appropiate solution.");
		VehicleRoutingProblemSolution lastSolution = VehicleRoutingProblemSolution.copyOf(solution);
		for(SearchStrategyModule module : searchStrategyModules){
			VehicleRoutingProblemSolution newSolution = module.runAndGetSolution(lastSolution);
			lastSolution = newSolution;
		}
		solutionCostCalculator.calculateCosts(lastSolution);
		boolean solutionAccepted = solutionAcceptor.acceptSolution(solutions, lastSolution);
		DiscoveredSolution discoveredSolution = new DiscoveredSolution(lastSolution, solutionAccepted, getName());
		return discoveredSolution;
	}

	
	public void addModule(SearchStrategyModule module){
		if(module == null) throw new IllegalStateException("module to be added is null.");
		searchStrategyModules.add(module);
		logger.info("module added [module="+module+"][#modules="+searchStrategyModules.size()+"]");
	}

	public void addModuleListener(SearchStrategyModuleListener moduleListener) {
		for(SearchStrategyModule module : searchStrategyModules){
			module.addModuleListener(moduleListener);
		}
		
	}

}
