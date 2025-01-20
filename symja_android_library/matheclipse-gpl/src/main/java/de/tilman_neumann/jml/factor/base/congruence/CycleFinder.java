/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2021-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base.congruence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * A cycle finding algorithm implementation following [LLDMW02], finding smooth congruences from partial relations.
 * This algorithm has been tested to work fine for partials with <= 3 large primes, and probably does so for any number of large primes.
 * 
 * As reported in the paper, the algorithm does not find cycles that do not include at least one 1LP-partial; but such cycles occur very rarely and so that's no problem at all.
 * 
 * @see [LLDMW02] Leyland, Lenstra, Dodson, Muffett, Wagstaff 2002: "MPQS with three large primes", Lecture Notes in Computer Science, 2369.
 * 
 * @author Tilman Neumann
 */
public class CycleFinder {
	
	private static final Logger LOG = LogManager.getLogger(CycleFinder.class);
	private static final boolean DEBUG = false; // used for logs and asserts

	/**
	 * Finds independent cycles and uses them to combine partial to smooth relations.
	 * 
	 * @return smooth relations found by combining partial relations
	 */
	public static ArrayList<Smooth> findIndependentCycles(HashSet<Partial> relations) {
		// Create maps from large primes to partials, vice versa, and chains.
		// These are needed so we can remove elements without changing the relations itself.
		HashMap<Long, ArrayList<Partial>> rbp = new HashMap<>();
		HashMap<Partial, ArrayList<Long>> pbr = new HashMap<>();
		HashMap<Partial, ArrayList<Partial>> chains = new HashMap<>();
		for (Partial newPartial : relations) {
			Long[] oddExpBigFactors = newPartial.getLargeFactorsWithOddExponent();
			ArrayList<Long> factorsList = new ArrayList<>(); // copy needed
			for (Long oddExpBigFactor : oddExpBigFactors) {
				factorsList.add(oddExpBigFactor);				
				ArrayList<Partial> partialCongruenceList = rbp.get(oddExpBigFactor);
				// For large N, most large factors appear only once. Therefore we create an ArrayList with initialCapacity=1 to safe memory.
				// Even less memory would be needed if we had a HashMap<Long, Object>
				// and store AQPairs or AQPair[] in the Object part. But I do not want to break the generics...
				if (partialCongruenceList==null) partialCongruenceList = new ArrayList<Partial>(1);
				partialCongruenceList.add(newPartial);
				rbp.put(oddExpBigFactor, partialCongruenceList);
			}
			pbr.put(newPartial, factorsList);
			chains.put(newPartial, new ArrayList<>());
		}
		
		// result
		ArrayList<Smooth> smoothsFromPartials = new ArrayList<>();
		
		boolean tablesChanged;
		do {
			tablesChanged = false;
			Iterator<Partial> r0Iter = pbr.keySet().iterator();
			while (r0Iter.hasNext()) {
				Partial r0 = r0Iter.next();
				ArrayList<Long> r0Factors = pbr.get(r0);
				if (r0Factors.size() != 1) continue;
				
				Long p = r0Factors.get(0);
				ArrayList<Partial> riList = rbp.get(p);
				for (Partial ri : riList) {
					if (r0.equals(ri)) continue;
					
					ArrayList<Long> riFactors = pbr.get(ri);
					if (DEBUG) Ensure.ensureNotNull(riFactors);
					if (riFactors.size() == 1) {
						// found cycle -> create new Smooth consisting of r0, ri and their chains
						if (DEBUG) {
							SortedMultiset<Long> combinedLargeFactors = new SortedMultiset_BottomUp<Long>();
							combinedLargeFactors.addAll(r0.getLargeFactorsWithOddExponent());
							for (Partial partial : chains.get(r0)) combinedLargeFactors.addAll(partial.getLargeFactorsWithOddExponent());
							combinedLargeFactors.addAll(ri.getLargeFactorsWithOddExponent());
							for (Partial partial : chains.get(ri)) combinedLargeFactors.addAll(partial.getLargeFactorsWithOddExponent());
							// test combinedLargeFactors
							for (Long factor : combinedLargeFactors.keySet()) {
								Ensure.ensureEquals((combinedLargeFactors.get(factor) & 1), 0);
							}
						}
						HashSet<Partial> allPartials = new HashSet<>();
						allPartials.add(r0);
						allPartials.addAll(chains.get(r0));
						allPartials.add(ri);
						allPartials.addAll(chains.get(ri));
						Smooth smooth = new SmoothComposite(allPartials);
						smoothsFromPartials.add(smooth);
						continue;
					}
					
					// otherwise add r0 and its chain to the chain of ri
					ArrayList<Partial> riChain = chains.get(ri);
					riChain.add(r0);
					riChain.addAll(chains.get(r0));
					// delete p from the prime list of ri
					riFactors.remove(p);
				} // end for ri

				// "the entry keyed by r0 is deleted from pbr";
				// this requires Iterator.remove() so we can continue working with the outer collection
				r0Iter.remove(); // except avoiding ConcurrentModificationExceptions the same as pbr.remove(r0);
				
				// "the entry for r0 keyed by p is deleted from rbp";
				// This choice promised finding more smooths, but unfortunately it was wrong, delivered combinations with odd exponents
				// riList.remove(r0);
				// The following works
				ArrayList<Partial> partials = rbp.get(p);
				for (Partial partial : partials) {
					ArrayList<Long> pList = pbr.get(partial);
					if (pList != null) pList.remove(p);
				}
				
				tablesChanged = true;
			} // end while r0
		} while (tablesChanged);
		
		if (DEBUG) LOG.debug("Found " + smoothsFromPartials.size() + " smooths from partials");
		return smoothsFromPartials;
	}
}
