/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.factor.psiqs;

import java.util.ArrayList;
import java.util.Collection;

import de.tilman_neumann.jml.factor.base.congruence.AQPair;

/**
 * A synchronized buffer used to pass AQ-pairs from sieve threads to the control thread.
 * @author Tilman Neumann
 */
public class AQPairBuffer {
	private static final int INITIAL_BUFFER_SIZE = 100;
	
	private ArrayList<AQPair> aqPairs = new ArrayList<AQPair>(INITIAL_BUFFER_SIZE);
	
	/**
	 * Called by the control thread, indicating that it waits for AQPairs.
	 * @return collected AQPairs
	 */
	ArrayList<AQPair> collectAQPairs() {
		synchronized (this) {
			while (true) {
				try {
					wait(); // is woken up by notify() after new data has been added to the buffer
					//LOG.debug("Control thread got notified about new data");
					break;
				} catch (InterruptedException ie) {
					// ignore
				}
			}
			// get new data
			ArrayList<AQPair> ret = aqPairs;
			aqPairs = new ArrayList<AQPair>(INITIAL_BUFFER_SIZE);
			return ret;
		}
	}

	/**
	 * Called by sieve threads, indicating that they want to add AQPairs.
	 * @param newAQPairs non-empty collection of AQPairs
	 */
	public void addAll(Collection<AQPair> newAQPairs) {
		synchronized (this) { // block write from sieve threads and read access from control thread
			aqPairs.addAll(newAQPairs);
			this.notify(); // notify control thread about incoming data
		}
	}
}
