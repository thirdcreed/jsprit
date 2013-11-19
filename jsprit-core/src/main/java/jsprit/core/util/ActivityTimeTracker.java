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
package jsprit.core.util;

import jsprit.core.problem.cost.ForwardTransportTime;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.ActivityVisitor;
import jsprit.core.problem.solution.route.activity.TourActivity;

public class ActivityTimeTracker implements ActivityVisitor{

	private ForwardTransportTime transportTime;
	
	private TourActivity prevAct = null;
	
	private double startAtPrevAct;
	
	private VehicleRoute route;
	
	private boolean beginFirst = false;
	
	private double actArrTime;
	
	private double actEndTime;
	
	public ActivityTimeTracker(ForwardTransportTime transportTime) {
		super();
		this.transportTime = transportTime;
	}

	/**
	 * Returns the arrTime of the activity that was visited last.
	 * 
	 * @return
	 */
	public double getActArrTime(){
		return actArrTime;
	}
	
	/**
	 * Returns the endTime of the activity that was visited last.
	 * 
	 * @return
	 */
	public double getActEndTime(){
		return actEndTime;
	}
	
	/**
	 * Returns the last visited activity.
	 * 
	 */
	public TourActivity getLastActivity(){
		return prevAct;
	}
	
	@Override
	public void begin(VehicleRoute route) {
		prevAct = route.getStart(); 
		startAtPrevAct = prevAct.getEndTime();
		
		actEndTime = startAtPrevAct;
		
		this.route = route;
		
		beginFirst = true;
	}

	@Override
	public void visit(TourActivity activity) {
		if(!beginFirst) throw new IllegalStateException("never called begin. this however is essential here");
		double transportTime = this.transportTime.getTransportTime(prevAct.getLocationId(), activity.getLocationId(), startAtPrevAct, route.getDriver(), route.getVehicle());
		double arrivalTimeAtCurrAct = startAtPrevAct + transportTime; 
		
		actArrTime = arrivalTimeAtCurrAct;
		
		double operationStartTime = Math.max(activity.getTheoreticalEarliestOperationStartTime(), arrivalTimeAtCurrAct);
		double operationEndTime = operationStartTime + activity.getOperationTime();
		
		actEndTime = operationEndTime;
		
		prevAct = activity;
		startAtPrevAct = operationEndTime;

	}

	@Override
	public void finish() {
		double transportTime = this.transportTime.getTransportTime(prevAct.getLocationId(), route.getEnd().getLocationId(), startAtPrevAct, route.getDriver(), route.getVehicle());
		double arrivalTimeAtCurrAct = startAtPrevAct + transportTime; 
		
		actArrTime = arrivalTimeAtCurrAct;
		actEndTime = arrivalTimeAtCurrAct;
		
		beginFirst = false;
	}
	
	
	

}
