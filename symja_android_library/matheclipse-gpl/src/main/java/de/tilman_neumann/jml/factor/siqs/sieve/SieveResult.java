/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.siqs.sieve;

import java.util.Iterator;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;

/**
 * The result of a sieve run. Reusable, does not require to allocate new SmoothCandidates all the time.
 * @author Tilman Neumann
 */
public class SieveResult implements Iterable<SmoothCandidate>, Iterator<SmoothCandidate> {
	public SmoothCandidate[] smoothCandidates;
	
	public int usedSize;
	
	private int iteratorPosition;
	
	public SieveResult(int initalCapacity) {
		smoothCandidates = new SmoothCandidate[initalCapacity];
		for (int i=initalCapacity-1; i>=0; i--) {
			smoothCandidates[i] = new SmoothCandidate(-1, null, null, new SortedIntegerArray());
		}
		usedSize = 0;
	}
	
	public void reset() {
		usedSize = 0;
	}
	
	public SmoothCandidate peekNextSmoothCandidate() {
		if (usedSize == smoothCandidates.length) {
			// array expansion necessary
			int newCapacity = usedSize<<1;
			SmoothCandidate[] newArray = new SmoothCandidate[newCapacity];
			// create new entries
			for (int i=newCapacity-1; i>=usedSize; i--) {
				newArray[i] = new SmoothCandidate(-1, null, null, new SortedIntegerArray());
			}
			// keep old entries
			for (int i=usedSize-1; i>=0; i--) {
				newArray[i] = smoothCandidates[i];
			}
			// assign
			smoothCandidates = newArray;
		}
		// no increment of usedSize yet, this comes with submit
		return smoothCandidates[usedSize];
	}
	
	public void commitNextSmoothCandidate() {
		usedSize++;
	}

	// Iterable implementation
	
	@Override
	public Iterator<SmoothCandidate> iterator() {
		iteratorPosition = 0;
		return this;
	}

	// Iterator implementation
	
	@Override
	public boolean hasNext() {
		return iteratorPosition < usedSize;
	}

	@Override
	public SmoothCandidate next() {
		return smoothCandidates[iteratorPosition++];
	}
}
