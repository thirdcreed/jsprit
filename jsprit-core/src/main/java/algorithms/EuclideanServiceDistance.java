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

import util.EuclideanDistanceCalculator;
import basics.Job;
import basics.Service;
import basics.algo.JobDistance;

class EuclideanServiceDistance implements JobDistance {

	public EuclideanServiceDistance() {
		super();
	}

	@Override
	public double getDistance(Job i, Job j) {
		double avgCost = 0.0;
		if (i instanceof Service && j instanceof Service) {
			if (i.equals(j)) {
				avgCost = 0.0;
			} else {
				Service s_i = (Service) i;
				Service s_j = (Service) j;
				if(s_i.getCoord() == null || s_j.getCoord() == null) throw new IllegalStateException("cannot calculate euclidean distance. since service coords are missing");
				avgCost = EuclideanDistanceCalculator.calculateDistance(s_i.getCoord(), s_j.getCoord());
			}
		} else {
			throw new UnsupportedOperationException(
					"currently, this class just works with shipments and services.");
		}
		return avgCost;
	}

}
