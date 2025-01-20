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
 * Cycle counting algorithm implementation following [LLDMW02], as far as possible.
 * The algorithm is exact for partials with 2 large primes.
 * With three large primes, unlike in [LLDMW02] we can only obtain an upper bound estimate of the cycle count.
 * So if the counting algorithm predicts a possible new smooth we need to run some PartialSolver to verify it.
 * 
 * @see [LLDMW02] Leyland, Lenstra, Dodson, Muffett, Wagstaff 2002: "MPQS with three large primes", Lecture Notes in Computer Science, 2369.
 * @see [LM94] Lenstra, Manasse 1994: "Factoring With Two Large Primes", Mathematics of Computation, volume 63, number 208, page 789.
 * 
 * @author Tilman Neumann
 */
public class CycleCounter3LP implements CycleCounter {
	
	private static final Logger LOG = LogManager.getLogger(CycleCounter3LP.class);
	private static final boolean DEBUG = false; // used for logs and asserts
	
	/** edges from bigger to smaller prime; size is v = #vertices */
	private HashMap<Long, Long> edges = new HashMap<>();
	
	/** number of relations */
	private int relationCount;
	/** all distinct relations (only for debugging) */
	private HashSet<Partial> relations = new HashSet<>();
	
	/** number of disconnected components */
	private int componentCount;
	/** roots of disconnected components (only for debugging) */
	private HashSet<Long> roots = new HashSet<>();
	
	/**
	 * A correction term that makes the estimated cycle count #cycles = #relations + #components + #corrections - #vertices an upper bound of the true 3LP cycle count.
	 * It is computed such that the estimated cycle count can never decrement when a new partial is added.
	 */
	private int corrections;
	
	/** the number of smooths from partials found */
	private int cycleCount;
	
	private int lastCorrectSmoothCount;

	@Override
	public void initializeForN() {
		edges.clear();
		edges.put(1L, 1L);
		relationCount = 0;
		if (DEBUG) relations.clear();
		componentCount = 1; // account for vertex 1
		if (DEBUG) {
			roots.clear();
			roots.add(1L);
		}
		corrections = 0;
		cycleCount = 0;
		lastCorrectSmoothCount = 0;
	}

	@Override
	public int addPartial(Partial partial, int correctSmoothCount) {
		relationCount++;
		
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
		} else if (largeFactorsCount==3) {
			if (DEBUG) {
				Ensure.ensureSmaller(largeFactors[0], largeFactors[1]);
				Ensure.ensureSmaller(largeFactors[1], largeFactors[2]);
			}
			insert3LP(largeFactors[0], largeFactors[1], largeFactors[2]);
		} else {
			LOG.warn("Holy shit, we found a " + largeFactorsCount + "-partial!");
		}
		
		// update cycle count
		int vertexCount = edges.size();
		if (DEBUG) {
			Ensure.ensureEquals(relations.size(), relationCount);
			Ensure.ensureEquals(roots.size(), componentCount);
		}
		int lastCycleCount = cycleCount;
		cycleCount = relationCount + corrections + componentCount - vertexCount; 

		if (DEBUG) {
			int correctSmoothCountIncr = correctSmoothCount - lastCorrectSmoothCount;
			lastCorrectSmoothCount = correctSmoothCount;
			LOG.debug("correctSmoothCount = " + correctSmoothCount);

			String cycleCountFormula = "#relations + #components + #corrections - #vertices";
			LOG.debug("#relations=" + relationCount + ", #components=" + componentCount + ", #corrections = " + corrections + ", #vertices=" + edges.size() + " -> cycleCount = " + cycleCountFormula + " = " + cycleCount);
			
			int cycleCountIncr = cycleCount - lastCycleCount;
			if (cycleCountIncr != correctSmoothCountIncr) {
				String errorType = (cycleCountIncr < correctSmoothCountIncr) ? "false-negative" : "false-positive";
				LOG.debug("ERROR: " + largeFactorsCount + "LP-partial " + Arrays.toString(largeFactors) + " led to " + errorType + " cycle count update!");
			}
			
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
	 * Update the edge graph for a 3-partial, with large primes p1 <= p2 <= p3.
	 * 
	 * With 3 large primes, the standard formula #cycles = R+C-V can not be computed exactly.
	 * So we use #cycles = R+C+X-V, where X is a correction that applies when adding a new 3LP would lead to a reduced R+C-V.
	 * 
	 * There are seven cases to distinguish:
	 * #new vertices | #distinct roots | delta(R+C-V) | -> X | -> delta(R+C+X-V)
	 * --------------+-----------------+--------------+------+------------------
	 *             3 |               3 |   1+1-3 = -1 |    1 |      1+1+1-3 = 0
	 *             2 |               3 |   1+0-2 = -1 |    1 |      1+0+1-2 = 0
	 *             1 |               3 |   1-1-1 = -1 |    1 |      1-1+1-1 = 0
	 *             1 |               2 |   1+0-1 =  0 |    0 |      1+0+0-1 = 0
	 *             0 |               3 |   1-2-0 = -1 |    1 |      1-2+1-0 = 0
	 *             0 |               2 |   1-1-0 =  0 |    0 |      1-1+0-0 = 0
	 *             0 |               1 |   1+0-0 =  1 |    0 |      1+0+0-0 = 1
	 *             
	 * So the correction X is 1 if we have three distinct roots, 0 otherwise;
	 * and the update of R+C+X-V is 1 only iff all primes are already known and belong to the same disconnected component, 0 otherwise.
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	private void insert3LP(long p1, long p2, long p3) {
		Long r1 = getRoot(p1);
		Long r2 = getRoot(p2);
		Long r3 = getRoot(p3);
		
		if (r1!=null && r2!=null && r3!=null) {
			// all three vertices already exist. in this case we only need the roots, so we can sort them without keeping the reference to the primes
			long tmp;
			if (r2<r1) { tmp=r1; r1=r2; r2=tmp; }
			if (r3<r2) { tmp=r2; r2=r3; r3=tmp; }
			if (r2<r1) { tmp=r1; r1=r2; r2=tmp; }
			// now r1 <= r2 <= r3
			
			// if they lie in different components we can connect them
			if (r1<r2) {
				if (r2<r3) {
					// r1 < r2 < r3, three different components
					if (DEBUG) LOG.debug("3LP: 3 old vertices from 3 distinct components");
					edges.put(r2, r1);
					edges.put(r3, r1);
					componentCount-=2;
					if (DEBUG) {
						Ensure.ensureTrue(roots.remove(r2));
						Ensure.ensureTrue(roots.remove(r3));
					}
					corrections++;
				} else {
					// r1 < r2==r3, two different components
					if (DEBUG) LOG.debug("3LP: 3 old vertices from 2 distinct components");
					edges.put(r2, r1);
					componentCount--;
					if (DEBUG) Ensure.ensureTrue(roots.remove(r2));
				}
			} else {
				// r1==r2
				if (r2<r3) {
					// r1==r2 < r3, two different components
					if (DEBUG) LOG.debug("3LP: 3 old vertices from 2 distinct components");
					edges.put(r3, r1);
					componentCount--;
					if (DEBUG) Ensure.ensureTrue(roots.remove(r3));
				} else {
					// r1==r2==r3, all vertices lie in the same component
					if (DEBUG) LOG.debug("3LP: 3 old vertices all from the same components");
				}
			}
		} else if (r1!=null && r2!=null) {
			// p1 and p2 already existed, p3 is new.
			// if the two existing roots are different, we can connect their components and add p3 to it.
			// thereby, the number of components reduces by 1.
			if (r1<r2) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r2, r1);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r2));
				edges.put(p3, r1);
				corrections++;
			} else if (r2<r1) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r1, r2);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r1));
				edges.put(p3, r2);
				corrections++;
			} else {
				// the two existing primes belong to the same component, thus we only add the new prime to it
				if (DEBUG) LOG.debug("3LP: 2 old vertices from the same components, one new vertex");
				edges.put(p3, r1);
			}
		} else if (r1!=null && r3!=null) {
			// p1 and p3 already existed, p2 is new.
			if (r1<r3) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r3, r1);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r3));
				edges.put(p2, r1);
				corrections++;
			} else if (r3<r1) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r1, r3);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r1));
				edges.put(p2, r3);
				corrections++;
			} else {
				// the two existing primes belong to the same component, thus we only add the new prime to it
				if (DEBUG) LOG.debug("3LP: 2 old vertices from the same components, one new vertex");
				edges.put(p2, r1);
			}
		} else if (r2!=null && r3!=null) {
			// p2 and p3 already existed, p1 is new.
			if (r2<r3) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r3, r2);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r3));
				edges.put(p1, r2);
				corrections++;
			} else if (r3<r2) {
				if (DEBUG) LOG.debug("3LP: 2 old vertices from distinct components, one new vertex");
				edges.put(r2, r3);
				componentCount--;
				if (DEBUG) Ensure.ensureTrue(roots.remove(r2));
				edges.put(p1, r3);
				corrections++;
			} else {
				// the two existing primes belong to the same component, thus we only add the new prime to it
				if (DEBUG) LOG.debug("3LP: 2 old vertices from the same components, one new vertex");
				edges.put(p1, r2);
			}
		} else if (r1!=null) {
			// p1 already existed, p2 and p3 are new.
			// We add both new primes to the existing component. The number of components remains unchanged.
			if (DEBUG) LOG.debug("3LP: 1 old vertex, two new vertices");
			edges.put(p2, r1);
			edges.put(p3, r1);
			corrections++;
		} else if (r2!=null) {
			// p2 already existed, p1 and p3 are new.
			if (DEBUG) LOG.debug("3LP: 1 old vertex, two new vertices");
			edges.put(p1, r2);
			edges.put(p3, r2);
			corrections++;
		} else if (r3!=null) {
			// p3 already existed, p1 and p2 are new.
			if (DEBUG) LOG.debug("3LP: 1 old vertex, two new vertices");
			edges.put(p1, r3);
			edges.put(p2, r3);
			corrections++;
		} else {
			// all three vertices are new -> we create a new component with the smallest prime as root
			// fortunately we already know that p1 <= p2 <= p3
			if (DEBUG) LOG.debug("3LP: three new vertices");
			edges.put(p1, p1);
			edges.put(p2, p1);
			edges.put(p3, p2); // TODO (p3, p1) ?
			componentCount++;
			if (DEBUG) Ensure.ensureTrue(roots.add(p1));
			corrections++;
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
