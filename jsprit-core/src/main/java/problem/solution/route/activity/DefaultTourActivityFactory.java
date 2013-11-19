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
package problem.solution.route.activity;

import problem.job.Delivery;
import problem.job.Pickup;
import problem.job.Service;

public class DefaultTourActivityFactory implements TourActivityFactory{

	@Override
	public TourActivity createActivity(Service service) {
		TourActivity act;
		if(service instanceof Pickup){
			act = new PickupActivity((Pickup) service);
		}
		else if(service instanceof Delivery){
			act = new DeliveryActivity((Delivery) service);
		}
		else{
			act = ServiceActivity.newInstance(service);
		}
		return act;
	}

}
