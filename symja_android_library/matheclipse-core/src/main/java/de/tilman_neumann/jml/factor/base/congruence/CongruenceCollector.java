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
package de.tilman_neumann.jml.factor.base.congruence;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.base.matrixSolver.PartialSolverController;
import de.tilman_neumann.util.Multiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Collects smooth and partial congruences, and assembles partials to smooth congruences on-the-fly.
 * Partials may have any number of large factors.
 * 
 * @author Tilman Neumann
 */
public class CongruenceCollector {
//	private static final Logger LOG = Logger.getLogger(CongruenceCollector.class);
//	private static final boolean DEBUG = false; // used for logs and asserts
	
	public static final boolean ANALYZE_BIG_FACTOR_SIZES = false;
	public static final boolean ANALYZE_Q_SIGNS = false;

	/** smooth congruences */
	private ArrayList<Smooth> smoothCongruences;
	/** 
	 * A map from big factors with odd exp to partial congruences.
	 * Here we need a 1:n relation because one partial can have several big factors;
	 * thus one big factor may be contained in many distinct partials.
	 */
	private HashMap<Integer, ArrayList<Partial>> oddExpBigFactors_2_partialCongruences; // here HashMap is faster than TreeMap or LinkedHashMap
	/** A controller for the solver used to create smooth congruences from partials */
	private PartialSolverController partialSolverController;
	/** factor tester */
	private FactorTest factorTest;

	// statistics
	private boolean analyzeBigFactorCounts;
	private int perfectSmoothCount, totalPartialCount;
	private int[] smoothFromPartialCounts, partialCounts;
	private Multiset<Integer> oddExpBigFactorSizes4Smooth, oddExpBigFactorSizes;
	private int partialWithPositiveQCount, smoothWithPositiveQCount;
	
	/**
	 * Unique constructor.
	 */
	public CongruenceCollector() {
		partialSolverController = new PartialSolverController(new MatrixSolver01_Gauss());
	}
	
	/**
	 * Initialize congruence collector for a new N.
	 * @param N
	 * @param factorTest
	 * @param analyzeBigFactorCounts must be true if getReport() shall be used
	 */
	public void initialize(BigInteger N, FactorTest factorTest, boolean analyzeBigFactorCounts) {
		smoothCongruences = new ArrayList<Smooth>();
		oddExpBigFactors_2_partialCongruences = new HashMap<Integer, ArrayList<Partial>>();
		partialSolverController.initialize(N, factorTest);
		this.factorTest = factorTest;
		this.analyzeBigFactorCounts = analyzeBigFactorCounts;
		
		// statistics
		perfectSmoothCount = 0;
		// zero-initialized smoothFromPartialCounts: index 0 -> from 1-partials, index 1 -> from 2-partials, index 2 -> from 2-partials
		smoothFromPartialCounts = new int[3];
		partialCounts = new int[3];
		totalPartialCount = 0;
		// collected vs. useful partials
		oddExpBigFactorSizes4Smooth = new SortedMultiset_BottomUp<Integer>();
		oddExpBigFactorSizes = new SortedMultiset_BottomUp<Integer>();
		// Q-analysis
		partialWithPositiveQCount = 0;
		smoothWithPositiveQCount = 0;
	}
	
	/**
	 * Add a new elementary partial or smooth congruence.
	 * @param aqPair
	 * @return true if a smooth congruence was added
	 * @throws FactorException
	 */
	public boolean add(AQPair aqPair) throws FactorException {
//		if (DEBUG) LOG.debug("new aqPair = " + aqPair);
		if (aqPair instanceof Smooth) {
			Smooth smooth = (Smooth) aqPair;
			boolean added = addSmooth(smooth);
			if (added) {
//				if (DEBUG) LOG.debug("Found new smooth congruence " + smooth + " --> #smooth = " + smoothCongruences.size() + ", #partials = " + getPartialCongruenceCount());
				perfectSmoothCount++;
				return true;
			}
			return false;
		}
		
		// otherwise aqPair must be a partial with at least one large factor.
		Partial partial = (Partial) aqPair;
		Integer[] oddExpBigFactors = partial.getMatrixElements();
		int oddExpBigFactorsCount = oddExpBigFactors.length;
//		if (DEBUG) assertTrue(oddExpBigFactorsCount > 0);
		
		// Check if the partial helps to assemble a smooth congruence:
		// First collect all partials that are somehow related to the new partial via big factors:
		HashSet<Partial> relatedPartials = findRelatedPartials(oddExpBigFactors); // oddExpBigFactors is not modified in the method
//		if (DEBUG) LOG.debug("#relatedPartials = " + relatedPartials.size());
		if (relatedPartials.size()>0) {
			// We found some "old" partials that share at least one big factor with the new partial.
			// Since relatedPartials is a set, we can not get duplicate AQ-pairs.
			relatedPartials.add(partial);
			// Solve partial congruence equation system
			ArrayList<Smooth> foundSmooths = partialSolverController.solve(relatedPartials); // throws FactorException
			if (foundSmooths.size()>0) {
				// We found one or more smooths from the new partial
				int addedCount = 0;
				for (Smooth foundSmooth : foundSmooths) {
					if (addSmooth(foundSmooth)) {
						if (analyzeBigFactorCounts) {
							// count kind of partials that helped to find smooths
							int maxLargeFactorCount = 0;
							HashSet<AQPair> aqPairsFromSmooth = new HashSet<AQPair>();
							foundSmooth.addMyAQPairsViaXor(aqPairsFromSmooth);
							for (AQPair aqPairFromSmooth : aqPairsFromSmooth) {
								int largeFactorCount = aqPairFromSmooth.getNumberOfLargeQFactors();
								if (largeFactorCount > maxLargeFactorCount) maxLargeFactorCount = largeFactorCount;
							}
							smoothFromPartialCounts[maxLargeFactorCount-1]++;
//							if (DEBUG) LOG.debug("Found smooth congruence from " + maxLargeFactorCount + "-partial --> #smooth = " + smoothCongruences.size() + ", #partials = " + getPartialCongruenceCount());
						}
						addedCount++; // increment counter if foundSmooth was really added
					}
				}
				if (addedCount>0) {
					if (ANALYZE_BIG_FACTOR_SIZES) {
						// register size of large factors that helped to find smooths
						for (Integer oddExpBigFactor : oddExpBigFactors) {
							int oddExpBigFactorBits = 32 - Integer.numberOfLeadingZeros(oddExpBigFactor);
							oddExpBigFactorSizes4Smooth.add(oddExpBigFactorBits);
						}
					}
					return true;
				}
				return false;
				// Not adding the new partial is sufficient to keep the old partials linear independent,
				// which is required to avoid duplicate solutions.
			}
		}
		
		// We were not able to construct a smooth congruence with the new partial, so just keep the partial:
		addPartial(partial, oddExpBigFactors);
		totalPartialCount++;
//		if (DEBUG) LOG.debug("Found new partial relation " + aqPair + " --> #smooth = " + smoothCongruences.size() + ", #partials = " + totalPartialCount);
		if (analyzeBigFactorCounts) partialCounts[oddExpBigFactorsCount-1]++;
		return false; // no smooth added
	}
	
	/**
	 * Find "old" partials related to a new partial.
	 * The oddExpBigFactors remain unaltered!
	 * 
	 * @param oddExpBigFactors the big factors with odd exponent of the new partial
	 * @return set of related partial congruences
	 */
	private HashSet<Partial> findRelatedPartials(Integer[] oddExpBigFactors) {
		HashSet<Integer> bigFactorsAlreadyUsed = new HashSet<Integer>();
		HashSet<Partial> relatedPartials = new HashSet<Partial>(); // we need a set to avoid adding the same partial more than once
		ArrayList<Integer> currentFactors = new ArrayList<Integer>();
		for (Integer oddExpBigFactor : oddExpBigFactors) {
			currentFactors.add(oddExpBigFactor);
		}
		while (currentFactors.size()>0) {
//			if (DEBUG) LOG.debug("currentFactors = " + currentFactors);
			ArrayList<Integer> nextFactors = new ArrayList<Integer>(); // no Set required, ArrayList has faster iteration
			for (Integer oddExpBigFactor : currentFactors) {
				bigFactorsAlreadyUsed.add(oddExpBigFactor);
				ArrayList<Partial> partialList = oddExpBigFactors_2_partialCongruences.get(oddExpBigFactor);
				if (partialList!=null && partialList.size()>0) {
					for (Partial relatedPartial : partialList) {
						relatedPartials.add(relatedPartial);
						for (Integer nextFactor : relatedPartial.getMatrixElements()) {
							if (!bigFactorsAlreadyUsed.contains(nextFactor)) nextFactors.add(nextFactor);
						}
					}
				}
			}
			currentFactors = nextFactors;
		}
		return relatedPartials;
	}

	/**
	 * Add smooth congruence.
	 * @param smoothCongruence
	 * @return true if a smooth congruence was added
	 * @throws FactorException
	 */
	protected boolean addSmooth(Smooth smoothCongruence) throws FactorException {
		if (smoothCongruence.isExactSquare()) {
			// We found a square congruence!
			HashSet<AQPair> totalAQPairs = new HashSet<AQPair>();
			smoothCongruence.addMyAQPairsViaXor(totalAQPairs);
			factorTest.testForFactor(totalAQPairs);
			// no FactorException -> the square congruence was improper -> drop it
			return false;
		}
		// No square -> add.
		// Here the same congruence may be added several times. This results in the need to test too many null vectors.
		// But avoiding such duplicates is asymptotically unfavourable because their likelihood decreases quickly.
		smoothCongruences.add(smoothCongruence);
		
		// Q-analysis
		if (ANALYZE_Q_SIGNS) if (smoothCongruence.getMatrixElements()[0] != -1) smoothWithPositiveQCount++;

		return true;
	}
	
	private void addPartial(Partial newPartial, Integer[] oddExpBigFactors) {
		for (Integer oddExpBigFactor : oddExpBigFactors) {
			ArrayList<Partial> partialCongruenceList = oddExpBigFactors_2_partialCongruences.get(oddExpBigFactor);
			// For large N, most large factors appear only once. Therefore we create an ArrayList with initialCapacity=1 to safe memory.
			// Even less memory would be needed if we had a HashMap<Long, Object> oddExpBigFactors_2_partialCongruences
			// and store AQPairs or AQPair[] in the Object part. But I do not want to break the generics...
			if (partialCongruenceList==null) partialCongruenceList = new ArrayList<Partial>(1);
			partialCongruenceList.add(newPartial);
			oddExpBigFactors_2_partialCongruences.put(oddExpBigFactor, partialCongruenceList);
		}
		
		if (ANALYZE_BIG_FACTOR_SIZES) {
			for (Integer oddExpBigFactor : oddExpBigFactors) {
				int oddExpBigFactorBits = 32 - Integer.numberOfLeadingZeros(oddExpBigFactor);
				oddExpBigFactorSizes.add(oddExpBigFactorBits);
			}
		}

		if (ANALYZE_Q_SIGNS) {
			if (newPartial.smallFactors[0] != -1 || (newPartial.smallFactorExponents[0]&1) == 0) partialWithPositiveQCount++;
		}
	}
	
	@SuppressWarnings("unused")
	private void dropPartial(Partial partial, Integer[] oddExpBigFactors) {
		for (Integer oddExpBigFactor : oddExpBigFactors) {
			ArrayList<Partial> partialCongruenceList = oddExpBigFactors_2_partialCongruences.get(oddExpBigFactor);
			partialCongruenceList.remove(partial);
			if (partialCongruenceList.size()==0) {
				// partialCongruenceList is empty now -> drop the whole entry
				oddExpBigFactors_2_partialCongruences.remove(oddExpBigFactor);
			}
		}
	}

	/**
	 * @return number of smooth congruences found so far.
	 */
	public int getSmoothCongruenceCount() {
		return smoothCongruences.size();
	}

	/**
	 * @return smooth congruences found so far.
	 */
	public ArrayList<Smooth> getSmoothCongruences() {
		return smoothCongruences;
	}
	
	/**
	 * @return number of partial congruences found so far.
	 */
	public int getPartialCongruenceCount() {
		return totalPartialCount;
	}

	public CongruenceCollectorReport getReport() {
		return new CongruenceCollectorReport(getPartialCongruenceCount(), smoothCongruences.size(), smoothFromPartialCounts, partialCounts, perfectSmoothCount,
				                             oddExpBigFactorSizes, oddExpBigFactorSizes4Smooth, partialWithPositiveQCount, smoothWithPositiveQCount);
	}
	
	/**
	 * Release memory after a factorization.
	 */
	public void cleanUp() {
		smoothCongruences = null;
		oddExpBigFactors_2_partialCongruences = null;
		partialSolverController.cleanUp();
		factorTest = null;
	}
}