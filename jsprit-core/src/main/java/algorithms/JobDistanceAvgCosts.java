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
package algorithms;

import org.apache.log4j.Logger;

import basics.Job;
import basics.Service;
import basics.costs.VehicleRoutingTransportCosts;


/**
 * Calculator that calculates average distance between two jobs based on the input-transport costs.
 * 
 * <p>If the distance between two jobs cannot be calculated with input-transport costs, it tries the euclidean distance between these jobs.
 * 
 * @author stefan schroeder
 *
 */
class JobDistanceAvgCosts implements JobDistance {

	private static Logger log = Logger.getLogger(JobDistanceAvgCosts.class);
	
	private VehicleRoutingTransportCosts costs;

	public JobDistanceAvgCosts(VehicleRoutingTransportCosts costs) {
		super();
		this.costs = costs;

	}

	/**
	 * Calculates and returns the average distance between two jobs based on the input-transport costs.
	 * 
	 * <p>If the distance between two jobs cannot be calculated with input-transport costs, it tries the euclidean distance between these jobs.
	 */ 
	@Override
	public double calculateDistance(Job i, Job j) {
		double avgCost = 0.0;
		if (i instanceof Service && j instanceof Service) {
			if (i.equals(j)) {
				avgCost = 0.0;
			} else {
				Service s_i = (Service) i;
				Service s_j = (Service) j;
				avgCost = calcDist(s_i, s_j);
			}
		} else {
			throw new UnsupportedOperationException(
					"currently, this class just works with shipments and services.");
		}
		return avgCost;
	}

	private double calcDist(Service s_i, Service s_j) {
		double distance;
		try{
			distance = costs.getTransportCost(s_i.getLocationId(), s_j.getLocationId(), 0.0, null, null);
			return distance;
		}
		catch(IllegalStateException e){
			// now try the euclidean distance between these two services
		}
		EuclideanServiceDistance euclidean = new EuclideanServiceDistance();
		distance = euclidean.calculateDistance(s_i, s_j);
		return distance;
	}

}
