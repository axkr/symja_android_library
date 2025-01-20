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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.IntHolder;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;
import de.tilman_neumann.jml.factor.base.matrixSolver.util.CompareEntry;
import de.tilman_neumann.jml.factor.base.matrixSolver.util.StackEntry;

/**
 * Base implementation for a congruence equation system (the "LinAlg phase matrix") solver.
 * Much faster than the first version due to great improvements by Dave McGuigan.
 * 
 * @author Tilman Neumann, Dave McGuigan
 */
abstract public class MatrixSolverBase02 extends MatrixSolver {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(MatrixSolverBase02.class);
		
	/**
	 * Main method to solve a congruence equation system.
	 * @param congruences the congruences forming the equation system
	 * @throws FactorException if a factor of N was found
	 */
	public void solve(Collection<? extends Smooth> congruences) throws FactorException {
		//LOG.debug("#congruences = " + congruences.size());
		
		// 1. remove singletons
		int nextPrimeIndex = 0;
		Map<Integer,Integer> primeIndexMap = new HashMap<Integer,Integer>(congruences.size());
		for (Smooth congruence : congruences) {
			for (Integer p : congruence.getMatrixElements()) {
				if (!primeIndexMap.containsKey(p)) {
					primeIndexMap.put(p, nextPrimeIndex++);
				}
			}
		}
		
		// When removing, it may be better to leave a few singletons vs. the cost of removing when the 
		// number of congruences gets large.
		final int DELTA = 0;
		int lastSize = congruences.size();
		List<Smooth> noSingletons = removeSingletons(congruences, primeIndexMap);
		while (lastSize-noSingletons.size()>DELTA) {
			lastSize = noSingletons.size();
			noSingletons = removeSingletons(noSingletons, primeIndexMap);
		}
		
		// Sort smooths? Gives a nice improvement for large N in version 03
		sortSmooths(noSingletons);

		// 2. Re-map odd-exp-elements to column indices and sort if appropriate.		
		Map<Integer,IntHolder> oddExpFactors = new HashMap<Integer,IntHolder>(primeIndexMap.size());
		for (Smooth congruence : noSingletons) {
			for (int f : congruence.getMatrixElements()) {
				IntHolder h = oddExpFactors.get(f);
				if (h == null) {
					oddExpFactors.put(f, new IntHolder(1));
				} else {
					h.value++;
				}
			}
		}
		
		Map<Integer, Integer> factors_2_indices = new HashMap<Integer,Integer>(oddExpFactors.size());
		Set<Map.Entry<Integer,IntHolder>> l = oddExpFactors.entrySet();
		Object[] sorted = l.toArray();
		Arrays.sort(sorted, new CompareEntry());
		for (int index=0; index<sorted.length; index++) {
			@SuppressWarnings("unchecked")
			Entry<Integer,IntHolder> e = (Entry<Integer,IntHolder>)sorted[index];
			factors_2_indices.put(e.getKey(), index);
		}

		// 4+5. Create & solve matrix
		solve(noSingletons, factors_2_indices);
	}
	 
	/**
	 * Remove singletons by maintaining a structure of what primes have been seen
	 * multiple times. When a prime is first seen the congruence is held as a 
	 * possible singleton. When a prime has been matched, processing of the current 
	 * congruence and held congruence can proceed. Any other congruences with that
	 * prime seen after matching can just proceed.
	 * @param congruences - collecting to be reduced
	 * @param primeIndexMap - Map of primes to unique indexes
	 * @return list of entries with singletons removed.
	 */
	protected List<Smooth> removeSingletons(Collection<? extends Smooth> congruences, Map<Integer,Integer> primeIndexMap) {
		List<Smooth> noSingles = new ArrayList<Smooth>(congruences.size());
		LinkedList<StackEntry<Smooth>> stack = new LinkedList<>();
		@SuppressWarnings("unchecked")
		StackEntry<Smooth>[] onHold = new StackEntry[primeIndexMap.size()];
		boolean[] haveMatch = new boolean[primeIndexMap.size()];
		for (Smooth congruence : congruences) {
			StackEntry<Smooth> entry = new StackEntry<Smooth>(congruence,0);
			while (entry != null) {
				Smooth currentCongruence = entry.congruence;
				int factor = currentCongruence.getMatrixElements()[entry.currentPrimeIndex];
				int ci = primeIndexMap.get(factor);
				if (haveMatch[ci]) {
					// This prime has been seen multiple times, just check the next prime.
					entry.currentPrimeIndex++;
				} else {
					// This prime hasn't been matched yet.
					if (onHold[ci] == null) {
						// First time seeing this prime. Hang on to the congruence.
						onHold[ci] = entry;
						entry = null;
					} else {
						// Second time seeing this prime. It's now matched. 
						// Stack the held congruence for further processing.
						haveMatch[ci] = true;
						StackEntry<Smooth> held = onHold[ci];
						held.currentPrimeIndex++;
						if (held.currentPrimeIndex>=held.congruence.getMatrixElements().length) {
							noSingles.add(held.congruence);
						} else {
							stack.addFirst(held);
						}
						entry.currentPrimeIndex++;
					}
				}
				// the current entry may have examined all it's primes
				if (entry != null) {
					if (entry.currentPrimeIndex>=entry.congruence.getMatrixElements().length) {
						// every factor seen at least twice
						noSingles.add(entry.congruence);
						entry = null;						
					}
				}
				// if the current entry is complete, see it there's more in the stack to do.
				if (entry == null) {
					if (stack.size()>0) {
						entry = stack.removeFirst();
					}
				}
			}
		}
		
		return noSingles;
	}
	
	protected void sortSmooths(List<Smooth> list) {
		// in this version we keep the sorting as is
	}

	/**
	 * Create the matrix from the pre-processed congruences and solve it.
	 * @param congruences
	 * @param factors_2_columnIndices map from factors to matrix column indices
	 * @throws FactorException 
	 */
	abstract protected void solve(List<Smooth> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException;
}
