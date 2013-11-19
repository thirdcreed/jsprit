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
package algorithm.termination;

import java.util.Collection;

import org.apache.log4j.Logger;

import problem.VehicleRoutingProblem;
import problem.solution.VehicleRoutingProblemSolution;
import algorithm.SearchStrategy.DiscoveredSolution;
import algorithm.listener.AlgorithmStartsListener;
import algorithm.VehicleRoutingAlgorithm;

/**
 * Breaks algorithm prematurely based on specified time.
 * 
 * <p>Note, TimeBreaker must be registered as AlgorithmListener <br>
 * <code>agorithm.getAlgorithmListeners().addListener(this);</code>
 * 
 * @author stefan
 *
 */
public class TimeBreaker implements PrematureAlgorithmBreaker, AlgorithmStartsListener{

	private static Logger logger = Logger.getLogger(TimeBreaker.class);
	
	private double timeThreshold;
	
	private double startTime;
	
	/**
	 * Constructs TimeBreaker that breaks algorithm prematurely based on specified time.
	 * 
	 * <p>Note, TimeBreaker must be registered as AlgorithmListener <br>
	 * <code>agorithm.getAlgorithmListeners().addListener(this);</code>
	 * 
	 * @author stefan
	 *
	 */
	public TimeBreaker(double time_in_seconds) {
		super();
		this.timeThreshold = time_in_seconds;
		logger.info("initialise " + this);
	}
	
	@Override
	public String toString() {
		return "[name=TimeBreaker][timeThreshold="+timeThreshold+"]";
	}

	@Override
	public boolean isPrematureBreak(DiscoveredSolution discoveredSolution) {
		if((System.currentTimeMillis() - startTime)/1000.0 > timeThreshold) return true;
		return false;
	}
	
	@Override
	public void informAlgorithmStarts(VehicleRoutingProblem problem,VehicleRoutingAlgorithm algorithm,Collection<VehicleRoutingProblemSolution> solutions) {
		startTime = System.currentTimeMillis();
	}

}
