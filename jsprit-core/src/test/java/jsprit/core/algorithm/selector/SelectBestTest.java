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
package jsprit.core.algorithm.selector;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import jsprit.core.algorithm.selector.SelectBest;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;

import org.junit.Test;




public class SelectBestTest {
	
	@Test
	public void whenHaving2Solutions_selectBest(){
		VehicleRoutingProblemSolution sol1 = mock(VehicleRoutingProblemSolution.class);
		VehicleRoutingProblemSolution sol2 = mock(VehicleRoutingProblemSolution.class);
		when(sol1.getCost()).thenReturn(1.0);
		when(sol2.getCost()).thenReturn(2.0);
		assertThat(new SelectBest().selectSolution(Arrays.asList(sol1,sol2)), is(sol1));
	}
	
	@Test
	public void whenHavingOnly1Solutions_selectThisOne(){
		VehicleRoutingProblemSolution sol1 = mock(VehicleRoutingProblemSolution.class);
		when(sol1.getCost()).thenReturn(1.0);
		assertThat(new SelectBest().selectSolution(Arrays.asList(sol1)), is(sol1));
	}
	
	@Test
	public void whenHavingNoSolutions_returnNull(){
		assertNull(new SelectBest().selectSolution(Collections.EMPTY_LIST));
	}

}
