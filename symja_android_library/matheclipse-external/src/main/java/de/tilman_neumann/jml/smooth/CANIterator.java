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
package de.tilman_neumann.jml.smooth;

import java.math.BigInteger;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.TimeUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static org.junit.Assert.*;

/**
 * Iterator for colossally abundant numbers 2,6,12,... (A004490).
 * @author Tilman Neumann
 */
public class CANIterator {
	private static final Logger LOG = Logger.getLogger(CANIterator.class);
	private static final boolean DEBUG = false;
	
	private CANEntry last = null;
	private TreeMap<Integer, CANEntry> exponentSum_2_canEntries = new TreeMap<Integer, CANEntry>();
	
	/**
	 * @return next CAN
	 */
	public CANEntry next() {
		if (last == null) {
			// CAN(0.5) = 2 // see test in class CANEntry
			last = CANEntry.computeCAN(0.5);
			assertEquals(I_2, last.getCAN());
			return last;
		}
		
		// now we have a last entry, and we want to find a generic epsilon such that the new exponent sum is lastExponentSum+1
		int lastExponentSum = last.getExponentSum();
		int wantedExponentSum = lastExponentSum+1;
		CANEntry precomputedEntry = exponentSum_2_canEntries.remove(wantedExponentSum);
		if (precomputedEntry!=null) {
			// we had the entry already precomputed!
			last = precomputedEntry;
			return precomputedEntry;
		}
		
		// we need to compute the entry
		double lastEpsilon = last.getEpsilon();
		double minEpsilon = 0;
		if (exponentSum_2_canEntries.isEmpty()) {
			// minEpsilon = 0 works, but we can do better. but this is no improvement (same performance)
			minEpsilon = (wantedExponentSum<4) ? lastEpsilon/4 : lastEpsilon/2;
		} else {
			// take the epsilon from the smallest precomputed (and too big) CAN as minEpsilon
			minEpsilon = exponentSum_2_canEntries.firstEntry().getValue().getEpsilon();
		}
		double maxEpsilon = lastEpsilon;
		
		double currentEpsilon = (minEpsilon+maxEpsilon)/2;
		CANEntry current;
		while(true) {
			current = CANEntry.computeCAN(currentEpsilon);
			int currentExponentSum = current.getExponentSum();
			int cmp = currentExponentSum - wantedExponentSum;
			if (cmp==0) break;
			if (cmp < 0) {
				// currentEpsilon was too big, CAN too small
				if (DEBUG) LOG.debug("    minEpsilon=" + minEpsilon + ", maxEpsilon=" + maxEpsilon + ", too small currentEpsilon=" + currentEpsilon + ", currentExponentSum = " + currentExponentSum);
				maxEpsilon = currentEpsilon;
				//currentEpsilon = (minEpsilon + currentEpsilon) / 2;
				currentEpsilon = Math.sqrt(minEpsilon * currentEpsilon); // slightly faster
			} else {
				// currentEpsilon was too small, CAN too big
				if (DEBUG) LOG.debug("    minEpsilon=" + minEpsilon + ", maxEpsilon=" + maxEpsilon + ", too big currentEpsilon=" + currentEpsilon + ", currentExponentSum = " + currentExponentSum);
				minEpsilon = currentEpsilon;
				//currentEpsilon = (currentEpsilon + maxEpsilon) / 2;
				currentEpsilon = Math.sqrt(currentEpsilon * maxEpsilon);
				// store the entry for later!
				exponentSum_2_canEntries.put(currentExponentSum, current);
			}
		}

		// return found CAN
		last = current;
		return current;
	}

	/**
	 * Test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		long startTimeMillis = System.currentTimeMillis();
		
		CANIterator canIter = new CANIterator();
		Double lastEpsilon = null;
		for (int n=1; n<=1000; n++) {
			CANEntry entry = canIter.next();
			assertEquals(n, entry.getExponentSum());
			double epsilon = entry.getEpsilon();
			Double epsilonQuot = lastEpsilon!=null ? lastEpsilon/epsilon : null;
			BigInteger can = entry.getCAN();
			int digits = Magnitude.of(can);
			LOG.info("n=" + n + ": epsilon=" + epsilon + ", epsilonQuot=" + epsilonQuot + ", " + digits + " digits CAN = " + entry.getCAN());
			lastEpsilon = epsilon;
		}
		LOG.info(canIter.exponentSum_2_canEntries.size() + " remaining precomputed CANs with exponentSums " + canIter.exponentSum_2_canEntries.keySet());

		long endTimeMillis = System.currentTimeMillis();
		String durationStr = TimeUtil.timeDiffStr(startTimeMillis, endTimeMillis);
		LOG.info("Computation took " + durationStr);
	}
}
