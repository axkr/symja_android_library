/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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

import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Ensure;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Iterator for superior highly composite numbers 2,6,12,... (A002201).
 * @author Tilman Neumann
 */
public class SHCNIterator {
	private static final Logger LOG = LogManager.getLogger(SHCNIterator.class);
	private static final boolean DEBUG = false;
	
	private SHCNEntry last = null;
	private TreeMap<Integer, SHCNEntry> exponentSum_2_shcnEntries = new TreeMap<Integer, SHCNEntry>();
	
	/**
	 * @return next SHCN
	 */
	public SHCNEntry next() {
		if (last == null) {
			// SHCN(1) = 2
			last = SHCNEntry.computeSHCN(1);
			Ensure.ensureEquals(I_2, last.getSHCN());
			return last;
		}
		
		// now we have a last entry, and we want to find an x such that the new exponent sum is lastExponentSum+1
		int lastExponentSum = last.getExponentSum();
		int wantedExponentSum = lastExponentSum+1;
		SHCNEntry precomputedEntry = exponentSum_2_shcnEntries.remove(wantedExponentSum);
		if (precomputedEntry!=null) {
			// we had the entry already precomputed!
			last = precomputedEntry;
			return precomputedEntry;
		}
		
		// we need to compute the entry
		double lastX = last.getX();
		double minX = lastX;
		double maxX;
		if (exponentSum_2_shcnEntries.isEmpty()) {
			// maxX = lastX+1 worked, but this should be slightly better
			maxX = (wantedExponentSum==2 || wantedExponentSum==4) ? lastX+1.25 : lastX+0.5;
		} else {
			// take the x from the smallest precomputed (and too big) entry as maxX
			maxX = exponentSum_2_shcnEntries.firstEntry().getValue().getX();
		}
		
		double currentX = (minX+maxX)/2;
		SHCNEntry current;
		while(true) {
			current = SHCNEntry.computeSHCN(currentX);
			int currentExponentSum = current.getExponentSum();
			int cmp = currentExponentSum - wantedExponentSum;
			if (cmp==0) break;
			if (cmp < 0) {
				// currentX was too small
				if (DEBUG) LOG.debug("    minX=" + minX + ", maxX=" + maxX + ", too small currentX=" + currentX + ", currentExponentSum = " + currentExponentSum);
				minX = currentX;
				currentX = (currentX + maxX) / 2;
			} else {
				// currentX was too big
				if (DEBUG) LOG.debug("    minX=" + minX + ", maxX=" + maxX + ", too big currentX=" + currentX + ", currentExponentSum = " + currentExponentSum);
				maxX = currentX;
				currentX = (minX + currentX) / 2;
				// store the entry for later!
				exponentSum_2_shcnEntries.put(currentExponentSum, current);
			}
		}

		// return found SHCN
		last = current;
		return current;
	}

	TreeMap<Integer, SHCNEntry> getExponentSum2SHCNEntries() {
		return exponentSum_2_shcnEntries;
	}
}
