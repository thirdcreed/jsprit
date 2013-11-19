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

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;


/**
 * Counter which is a copy of Counter.java at package org.matsim.core.utils.misc (www.matsim.org);
 * 
 * @author Schroeder
 *
 */

	
public final class Counter {
	private final String prefix;
	private AtomicLong counter = new AtomicLong(0);
	private AtomicLong nextCounter = new AtomicLong(1);
	private static final Logger log = Logger.getLogger(Counter.class);

	/**
	 * @param prefix Some text that is output just before the counter-value.
	 */
	public Counter(final String prefix) {
		this.prefix = prefix;
	}

	public void incCounter() {
		long i = this.counter.incrementAndGet();
		long n = this.nextCounter.get();
		if (i >= n) {
			if (this.nextCounter.compareAndSet(n, n*2)) {
				log.info(this.prefix + n);
			}
		}
	}

	public void printCounter() {
		log.info(this.prefix + this.counter.get());
	}

	public long getCounter() {
		return this.counter.get();
	}

	public void reset() {
		this.counter.set(0);
		this.nextCounter.set(1);
	}
}


