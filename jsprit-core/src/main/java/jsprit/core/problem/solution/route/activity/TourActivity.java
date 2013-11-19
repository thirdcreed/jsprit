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
package jsprit.core.problem.solution.route.activity;

import jsprit.core.problem.job.Job;


public interface TourActivity {

	public interface JobActivity<T extends Job> extends TourActivity {
		
		public T getJob();
		
	}
	
	public int getCapacityDemand();
	
	public abstract String getName();
	
	public abstract String getLocationId();
	
	public abstract double getTheoreticalEarliestOperationStartTime();
	
	public abstract double getTheoreticalLatestOperationStartTime();

	public abstract double getOperationTime();
	
	public abstract double getArrTime();
	
	public abstract double getEndTime();
	
	public abstract void setArrTime(double arrTime);
	
	public abstract void setEndTime(double endTime);
	
	public TourActivity duplicate();
		
}
