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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Ensure;

/**
 * Cycle counting algorithm implementation for two large primes, following [LM94].
 * The algorithm is exact for partials with 2 large primes.
 * 
 * @see [LM94] Lenstra, Manasse 1994: "Factoring With Two Large Primes", Mathematics of Computation, volume 63, number 208, page 789.
 * 
 * @author Tilman Neumann
 */
public class CycleCounter2LP implements CycleCounter {
	
	private static final Logger LOG = LogManager.getLogger(CycleCounter2LP.class);
	private static final boolean DEBUG = false; // used for logs and asserts
	
	/** edges from bigger to smaller prime; size is v = #vertices */
	private HashMap<Long, Long> edges = new HashMap<>();
	
	/** collected relations */
	private HashSet<Partial> relations = new HashSet<>();
	
	/** number of disconnected components */
	private int componentCount;
	/** roots of disconnected components (only for debugging) */
	private HashSet<Long> roots = new HashSet<>();
	
	/** the number of smooths from partials found */
	private int cycleCount;
	
	@Override
	public void initializeForN() {
		relations.clear();
		edges.clear();
		edges.put(1L, 1L);
		componentCount = 1; // account for vertex 1
		if (DEBUG) {
			roots.clear();
			roots.add(1L);
		}
		cycleCount = 0;
	}
	
	@Override
	public int addPartial(Partial partial, int correctSmoothCount) {
		
		boolean added = relations.add(partial);
		if (!added) {
			// The partial is a duplicate of another relation we already have
			LOG.error("Found duplicate relation: " + partial);
			return cycleCount;
		}

		Long[] largeFactors = partial.getLargeFactorsWithOddExponent();
		int largeFactorsCount = largeFactors.length;
		if (DEBUG) LOG.debug("Add " + largeFactorsCount + "LP-partial " + Arrays.toString(largeFactors));
		
		// add edges
		if (largeFactorsCount==1) {
			if (DEBUG) Ensure.ensureSmaller(1, largeFactors[0]);
			insert1LP(largeFactors[0]);
		} else if (largeFactorsCount==2) {
			if (DEBUG) Ensure.ensureSmaller(largeFactors[0], largeFactors[1]);
			insert2LP(largeFactors[0], largeFactors[1]);
		} else {
			LOG.warn("Holy shit, we found a " + largeFactorsCount + "-partial!");
		}
		
		// update cycle count by standard formula: #cycles = #edges (one per relation) + #components - #vertices
		int vertexCount = edges.size();
		cycleCount = relations.size() + componentCount - vertexCount;

		if (DEBUG) {
			LOG.debug("correctSmoothCount = " + correctSmoothCount);
			String cycleCountFormula = "#relations + #roots - #vertices";
			LOG.debug("#relations=" + relations.size() + ", #roots=" + componentCount + ", #vertices=" + edges.size() + " -> cycleCount = " + cycleCountFormula + " = " + cycleCount);

			Ensure.ensureEquals(roots.size(), componentCount);
			//Ensure.ensureEquals(getRootsFromVertices().size(), componentCount); // expensive test
			LOG.debug("-------------------------------------------------------------");
		}
		
		return cycleCount;
	}
	
	/**
	 * Update the edge graph for a 1-partial.
	 * @param p1 the only large prime of the partial
	 */
	private void insert1LP(long p1) {
		Long r1 = getRoot(p1);

		if (r1!=null) {
			// The prime already existed
			if (DEBUG) LOG.debug("1LP: 1 old vertex: p1 = " + p1 + ", r1 = " + r1);
			if (r1 != 1) {
				// Add it to the component with root 1 and remove the old root if it existed
				edges.put(r1, 1L);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r1));
			}
		} else {
			// The prime is new -> just add it to the component with root 1
			if (DEBUG) LOG.debug("1LP: 1 new vertex");
			edges.put(p1, 1L);
		}
	}

	/**
	 * Update the edge graph for a 2-partial, with large primes p1 <= p2.
	 * @param p1
	 * @param p2
	 */
	private void insert2LP(long p1, long p2) {
		Long r1 = getRoot(p1);
		Long r2 = getRoot(p2);

		if (r1!=null && r2!=null) {
			// both vertices already exist.
			// if the roots are different, then we have distinct components which we can join now
			if (r1<r2) {
				if (DEBUG) LOG.debug("2LP: 2 old vertices from distinct components");
				edges.put(r2, r1);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r2));
			} else if (r2<r1) {
				if (DEBUG) LOG.debug("2LP: 2 old vertices from distinct components");
				edges.put(r1, r2);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r1));
			} else {
				// if the roots are equal than both primes are already part of the same component so nothing more happens
				if (DEBUG) LOG.debug("2LP: 2 old vertices from the same components");
			}
		} else if (r1 != null) {
			// p1 already exists, p2 is new -> we just add p2 to the component of p1
			if (DEBUG) LOG.debug("2LP: 1 old vertex, 1 new vertex");
			edges.put(p2, r1);
		} else if (r2 != null) {
			// p2 already exists, p1 is new -> we just add p1 to the component of p2
			if (DEBUG) LOG.debug("2LP: 1 old vertex, 1 new vertex");
			edges.put(p1, r2);
		} else {
			// both primes are new and form their own new disconnected component
			// we know p1 < p2
			if (DEBUG) LOG.debug("2LP: 2 new vertices");
			edges.put(p1, p1);
			edges.put(p2, p1);
			componentCount++;
			if (DEBUG) Ensure.ensureTrue(roots.add(p1));
		}
	}

	/**
	 * Find the root of a prime p in the edges graph.
	 * @param p
	 * @return the root of p: this is null if 'edges' has no key 'p' yet; otherwise it may be any root with 1 <= root <= p.
	 */
	private Long getRoot(Long p) {
		Long q = edges.get(p);
		if (q==null) return null; // edges has no key 'p' yet
		
		// Now we know that edges has a key 'p', and in that case this method will always return a root != null, because there will be at least a mapping p->p
		// (where both p's are even the same object, but we don't want to exploit that, SpotBugs and the like would be very unhappy about it)
		while (!q.equals(p)) {
			p = q;
			q = edges.get(p);
		}
		return p;
	}
	
	@SuppressWarnings("unused")
	private HashSet<Long> getRootsFromVertices() {
		HashSet<Long> roots = new HashSet<>();
		for (long vertex : edges.keySet()) {
			long r = getRoot(vertex);
			roots.add(r);
		}
		return roots;
	}

	@Override
	public HashSet<Partial> getPartialRelations() {
		return relations;
	}
	
	@Override
	public int getPartialRelationsCount() {
		return relations.size();
	}
	
	@Override
	public int getCycleCount() {
		return cycleCount;
	}
}
