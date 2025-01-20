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
package de.tilman_neumann.jml.factor.base.congruence;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.Multiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;
import de.tilman_neumann.util.Timer;

/**
 * First congruence collector using a cycle counter for 3LP.
 * With 3LP, the cycle count from the cycle counting algorithm is an upper bound of the true number of cycles.
 * So if it indicates that we might have found a new cycle, then the partial solver must be run to check that and resolve the smooth congruence.
 * 
 * With 3-partials and large enough N, some partials may have hundreds of thousands of "related partials".
 * Collecting them all would be such a heavy load for findRelatedPartials() and for the partial solver that factoring progress would eventually nearly come to a stop.
 * To prevent that, we had to insert a "cutoff" where findRelatedPartials() stops looking for more related partials;
 * furthermore this seems to produce a lot of duplicate smooth relations... Thus we had to make smoothCongruences a Set instead of a List.
 * But now it works!
 * 
 * @author Tilman Neumann
 */
// XXX Experimental, more tests and adjustment required
public class CongruenceCollector03 implements CongruenceCollector {
	private static final Logger LOG = LogManager.getLogger(CongruenceCollector03.class);
	private static final boolean DEBUG = false; // used for logs and asserts

	/** smooth congruences: must be a set here to avoid duplicates when 3-partials are involved */
	private HashSet<Smooth> smoothCongruences;
	
	/** 
	 * A map from big factors with odd exp to partial congruences.
	 * Here we need a 1:n relation because one partial can have several big factors;
	 * thus one big factor may be contained in many distinct partials.
	 */
	private HashMap<Long, ArrayList<Partial>> largeFactors_2_partials; // rbp !
	
	/** cycle counter */
	private CycleCounter cycleCounter = new CycleCounter3LP();
	
	/** A solver used to create smooth congruences from partials */
	private PartialSolver partialSolver = new PartialSolver01();
	
	/** factor tester */
	private FactorTest factorTest;
	
	// The number of congruences we need to find before we try to solve the smooth congruence equation system:
	// We want: #equations = #variables + some extra congruences
	private int requiredSmoothCongruenceCount;
	// extra congruences to have a bigger chance that the equation system solves. the likelihood is >= 1-2^-(extraCongruences+1)
	private int extraCongruences;
	
	private MatrixSolver matrixSolver;

	// Storing a found factor in this class permits it to be retrieved by multiple threads
	public BigInteger factor;

	// statistics
	private int totalPartialCount; // standard
	private int perfectSmoothCount; // needs ANALYZE
	private int cycleCount;
	private int[] smoothFromPartialCounts, partialCounts;
	private Multiset<Integer>[] partialQRestSizes, partialBigFactorSizes;
	private Multiset<Integer>[] smoothQRestSizes, smoothBigFactorSizes;
	private int partialWithPositiveQCount, smoothWithPositiveQCount;
	
	/** the biggest number of partials involved to find a smooth relation from partials */
	private int maxRelatedPartialsCount;

	private Timer timer = new Timer();
	private long ccDuration, solverDuration;
	private int solverRunCount, testedNullVectorCount;

	/**
	 * Default constructor that expects 10 more equations than variables to run the matrix solver.
	 */
	public CongruenceCollector03() {
		this(10);
	}

	/**
	 * Full constructor.
	 * @param extraCongruences The difference #equations-#variables required before the solver is started.
	 */
	public CongruenceCollector03(int extraCongruences) {
		this.extraCongruences = extraCongruences;
	}
	
	@Override
	public void initialize(BigInteger N, int primeBaseSize, MatrixSolver matrixSolver, FactorTest factorTest) {
		smoothCongruences = new HashSet<Smooth>();
		largeFactors_2_partials = new HashMap<Long, ArrayList<Partial>>();
		this.factorTest = factorTest;
		cycleCounter.initializeForN();
		
		// statistics
		cycleCount = 0;
		totalPartialCount = 0;
		if (ANALYZE) {
			perfectSmoothCount = 0;
			// zero-initialized smoothFromPartialCounts: index 0 -> from 1-partials, index 1 -> from 2-partials, index 2 -> from 3-partials
			smoothFromPartialCounts = new int[3];
			partialCounts = new int[3];
			maxRelatedPartialsCount = 0;
		}
		if (ANALYZE_LARGE_FACTOR_SIZES) {
			// collected vs. useful big factor and QRest bit sizes distinguished by the number of large primes
			int maxLPCount = 5; // works up to 4LP
			smoothQRestSizes = createSizeCountsArray(maxLPCount);
			smoothBigFactorSizes = createSizeCountsArray(maxLPCount);
			partialQRestSizes = createSizeCountsArray(maxLPCount);
			partialBigFactorSizes = createSizeCountsArray(maxLPCount);
		}
		if (ANALYZE_Q_SIGNS) {
			// Q-analysis
			partialWithPositiveQCount = 0;
			smoothWithPositiveQCount = 0;
		}
		this.requiredSmoothCongruenceCount = primeBaseSize + extraCongruences;
		this.matrixSolver = matrixSolver;
		ccDuration = solverDuration = 0;
		solverRunCount = testedNullVectorCount = 0;
		factor = null;
	}
	
	@Override
	public void setPrimeBaseSize(int newPrimeBaseSize) {
		this.requiredSmoothCongruenceCount = newPrimeBaseSize + extraCongruences;
	}

	@SuppressWarnings("unchecked")
	private SortedMultiset_BottomUp<Integer>[] createSizeCountsArray(int maxLPCount) {
		SortedMultiset_BottomUp<Integer>[] array = new SortedMultiset_BottomUp[maxLPCount];
		for (int i=0; i<maxLPCount; i++) {
			array[i] = new SortedMultiset_BottomUp<Integer>();
		}
		return array;
	}
	
	@Override
	public void collectAndProcessAQPairs(List<AQPair> aqPairs) {
		if (DEBUG) LOG.debug("add " + aqPairs.size() + " new AQ-pairs to CC");
		for (AQPair aqPair : aqPairs) {
			collectAndProcessAQPair(aqPair);
			if (factor != null) return;
		}
	}

	@Override
	public void collectAndProcessAQPair(AQPair aqPair) {
		if (DEBUG) LOG.debug("add new AQ-pair " + aqPair + " to CC");
		if (ANALYZE) timer.capture();
		boolean addedSmooth = false;
		try {
			addedSmooth = add(aqPair); // throws FactorException
		} catch (FactorException fe) {
			factor = fe.getFactor();
		}
		
		if (addedSmooth) {
			int smoothCongruenceCount = getSmoothCongruenceCount();
			if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
				// Try to solve equation system
				if (ANALYZE) {
					ccDuration += timer.capture();
					solverRunCount++;
					if (DEBUG) LOG.debug("Found " + smoothCongruenceCount + " / " + requiredSmoothCongruenceCount + " smooth congruences -> Start matrix solver run #" + solverRunCount + " ...");
				}
				try {
					matrixSolver.solve(getSmoothCongruences()); // throws FactorException
				} catch (FactorException fe) {
					factor = fe.getFactor();
				}
				if (ANALYZE) {
					testedNullVectorCount += matrixSolver.getTestedNullVectorCount();
					solverDuration += timer.capture();
				}
				if (factor != null) return;

				// No factor found -> extend equation system and continue searching smooth congruences
				requiredSmoothCongruenceCount += extraCongruences;
			}
		}
		if (ANALYZE) ccDuration += timer.capture();
	}
	
	private boolean add(AQPair aqPair) throws FactorException {
		if (DEBUG) LOG.debug("new aqPair = " + aqPair);
		if (aqPair instanceof Smooth) {
			Smooth smooth = (Smooth) aqPair;
			boolean addedSmooth = addSmooth(smooth); // throws FactorException
			if (ANALYZE) {
				if (addedSmooth) {
					if (ANALYZE_PROGRESS) {
						if (smoothCongruences.size() % 100 == 0) {
							LOG.debug("Found perfect smooth congruence --> #requiredSmooths = " + requiredSmoothCongruenceCount + ", #smooths = " + smoothCongruences.size() + ", #partials = " + getPartialCongruenceCount());
							LOG.debug("maxRelatedPartialsCount = " + maxRelatedPartialsCount + ", maxPartialMatrixSize = " + partialSolver.getMaxMatrixSize());
						}
					}
					perfectSmoothCount++;
				}
			}
			return addedSmooth;
		}
		
		// otherwise aqPair must be a partial with at least one large factor.
		Partial partial = (Partial) aqPair;
		final Long[] bigFactors = partial.getLargeFactorsWithOddExponent();
		if (DEBUG) {
			LOG.debug("bigFactors = " + Arrays.toString(bigFactors));
			Ensure.ensureGreater(bigFactors.length, 0);
		}
		
		int lastCycleCount = cycleCount;
		cycleCount = cycleCounter.addPartial(partial, /* dummy values, no debugging in this class yet*/ -123456789);
		if (cycleCount > lastCycleCount) {
			// The cycle counter suggests that there might be a new smooth. Use the partial solver to check that
			// we use Long[] and not long[] for bigFactors, because in the following they will be used a lot in Collections
			
			// Check if the partial helps to assemble a smooth congruence:
			// First collect all partials that are somehow related to the new partial via big factors:
			HashSet<Partial> relatedPartials = findRelatedPartials(bigFactors); // bigFactors is not modified in the method
			if (DEBUG) LOG.debug("#relatedPartials = " + relatedPartials.size());
			if (relatedPartials.size()>0) {
				// We found some "old" partials that share at least one big factor with the new partial.
				// Since relatedPartials is a set, we can not get duplicate AQ-pairs.
				relatedPartials.add(partial);
				if (ANALYZE) {
					if (relatedPartials.size() > maxRelatedPartialsCount) {
						maxRelatedPartialsCount = relatedPartials.size();
					}
				}

				// Solve partial congruence equation system
				Smooth foundSmooth = partialSolver.solve(relatedPartials);
				if (foundSmooth != null) {
					// We found a smooth from the new partial
					boolean added = addSmooth(foundSmooth); // throws FactorException
					if (ANALYZE) {
						if (added) {
							// count kind of partials that helped to find smooths
							int maxLargeFactorCount = 0;
							for (AQPair aqPairFromSmooth : foundSmooth.getAQPairs()) {
								int largeFactorCount = aqPairFromSmooth.getNumberOfLargeQFactors();
								if (largeFactorCount > maxLargeFactorCount) maxLargeFactorCount = largeFactorCount;
							}
							smoothFromPartialCounts[maxLargeFactorCount-1]++;
							if (ANALYZE_PROGRESS) {
								if (smoothCongruences.size() % 100 == 0) {
									LOG.debug("Found smooth congruence from " + maxLargeFactorCount + "-partial --> #requiredSmooths = " + requiredSmoothCongruenceCount + ", #smooths = " + smoothCongruences.size() + ", #partials = " + getPartialCongruenceCount());
									LOG.debug("maxRelatedPartialsCount = " + maxRelatedPartialsCount + ", maxPartialMatrixSize = " + partialSolver.getMaxMatrixSize());
								}
							}
						}
					}
					if (ANALYZE_LARGE_FACTOR_SIZES) {
						if (added) {
							// register size of large factors that helped to find smooths
							BigInteger prod = I_1;
							for (Long bigFactor : bigFactors) {
								int bigFactorBits = 64 - Long.numberOfLeadingZeros(bigFactor);
								smoothBigFactorSizes[bigFactors.length].add(bigFactorBits);
								prod = prod.multiply(BigInteger.valueOf(bigFactor));
							}
							smoothQRestSizes[bigFactors.length].add(prod.bitLength());
						}
					}
					return added;
					// Not adding the new partial is sufficient to keep the old partials linear independent,
					// which is required to avoid duplicate solutions.
				}
				// With 3LP, we may not have collected all related partials because of the cutoff, and a smooth may not have been found only for that reason.
				// Keeping this partial nonetheless may cause duplicate smooths; but making smoothCongruences a Set resolves the problem.
			}
		}

		// The new partial did not yield a smooth congruence, so just keep it:
		addPartial(partial, bigFactors);
		totalPartialCount++;
		if (DEBUG) LOG.debug("Found new partial relation --> #requiredSmooths = " + requiredSmoothCongruenceCount +", #smooths = " + smoothCongruences.size() + ", #partials = " + totalPartialCount);
		if (ANALYZE) partialCounts[bigFactors.length-1]++;
		return false; // no smooth added
	}
	
	/**
	 * Find "old" partials related to a new partial. The large factors of the new partial remain unaltered.
	 * 
	 * @param largeFactorsOfNewPartial the large factors with odd exponent of the new partial
	 * @return set of related partial congruences
	 */
	private HashSet<Partial> findRelatedPartials(Long[] largeFactorsOfNewPartial) {
		HashSet<Long> processedLargeFactors = new HashSet<>();
		HashSet<Partial> relatedPartials = new HashSet<>(); // we need a set to avoid adding the same partial more than once
		ArrayList<Long> currentLargeFactors = new ArrayList<>();
		for (Long largeFactor : largeFactorsOfNewPartial) {
			currentLargeFactors.add(largeFactor);
		}
		while (currentLargeFactors.size()>0) {
			if (DEBUG) LOG.debug("currentLargeFactors = " + currentLargeFactors);
			ArrayList<Long> nextLargeFactors = new ArrayList<>(); // no Set required, ArrayList has faster iteration
			for (Long largeFactor : currentLargeFactors) {
				processedLargeFactors.add(largeFactor);
				ArrayList<Partial> partialList = largeFactors_2_partials.get(largeFactor);
				// Removing some singletons on-the-fly improves performance considerably.
				// The conditions get a bit more complicated because the new partial has not been added yet to largeFactors_2_partials...
				// 1) if partialList has only 1 element and that element shares no factor with the new partial, then largeFactor is a singleton column
				if (partialList!=null && (partialList.size() > 1 || (partialList.size()==1 && arrayContains(largeFactorsOfNewPartial, largeFactor)))) {
					for (Partial partial : partialList) {
						// 2) if one of its factors occurs only once, then partial is a singleton row
						if (!isSingletonRow(partial, largeFactorsOfNewPartial)) {
							relatedPartials.add(partial);
							if (relatedPartials.size() >= 500) return relatedPartials; // XXX cutoff
							for (Long nextLargeFactor : partial.getLargeFactorsWithOddExponent()) {
								if (!processedLargeFactors.contains(nextLargeFactor)) nextLargeFactors.add(nextLargeFactor);
							}
						}
					}
				}
			}
			currentLargeFactors = nextLargeFactors;
		}
		return relatedPartials;
	}
	
	private boolean isSingletonRow(Partial p, Long[] largeFactorsOfNewPartial) {
		Long[] largeFactors = p.getLargeFactorsWithOddExponent();
		for (Long largeFactor : largeFactors) {
			ArrayList<Partial> partialList = largeFactors_2_partials.get(largeFactor);
			int listSize = partialList.size();
			if (listSize==0 || (listSize==1 && !arrayContains(largeFactorsOfNewPartial, largeFactor))) return true;
		}
		return false;
	}

	private static boolean arrayContains(Long[] array, long l) {
		for (int i=0; i<array.length; i++) {
			if (array[i] == l) return true;
		}
		return false;
	}

	/**
	 * (Try to) add a new smooth congruence.
	 * @param smoothCongruence
	 * @return true if a smooth congruence was added
	 * @throws FactorException
	 */
	private boolean addSmooth(Smooth smoothCongruence) throws FactorException {
		if (smoothCongruence.isExactSquare()) {
			// We found a square congruence!
			factorTest.testForFactor(smoothCongruence.getAQPairs());
			// no FactorException -> the square congruence was improper -> drop it
			return false;
		}
		// No square -> try to add.
		// Duplicate smooths cause the final matrix solver to need more null vectors (in the better case) or many unsuccessful solver runs (in the worse case).
		// With partials having no more than 2 large factors, we hardly got any duplicates. Avoiding them was unfavorable for performance.
		// With 3LP-partials, we can get many more duplicates of SmoothComposites (when the number of related partials exceeds the cutoff!).
		// This is the reason why smoothCongruences is a Set in this class.
		if (DEBUG) {
			if (smoothCongruences.contains(smoothCongruence)) {
				LOG.debug("Found duplicate smooth congruence!");
				LOG.debug("New: " + smoothCongruence);
				for (Smooth smooth : smoothCongruences) {
					if (smooth.equals(smoothCongruence)) {
						LOG.debug("Old: " + smooth); // yes, they are SmoothComposites and they are indeed equal
					}
				}
			}
		}
		boolean added = smoothCongruences.add(smoothCongruence);
		
		// Q-analysis
		if (ANALYZE_Q_SIGNS) if (added && smoothCongruence.getMatrixElements()[0] != -1) smoothWithPositiveQCount++;

		return added;
	}
	
	private void addPartial(Partial newPartial, Long[] bigFactors) {
		for (Long bigFactor : bigFactors) {
			ArrayList<Partial> partialCongruenceList = largeFactors_2_partials.get(bigFactor);
			// For large N, most large factors appear only once. Therefore we create an ArrayList with initialCapacity=1 to safe memory.
			// Even less memory would be needed if we had a HashMap<Long, Object> bigFactors_2_partialCongruences
			// and store AQPairs or AQPair[] in the Object part. But I do not want to break the generics...
			if (partialCongruenceList==null) partialCongruenceList = new ArrayList<Partial>(1);
			partialCongruenceList.add(newPartial);
			largeFactors_2_partials.put(bigFactor, partialCongruenceList);
		}
		
		if (ANALYZE_LARGE_FACTOR_SIZES) {
			BigInteger prod = I_1;
			for (Long bigFactor : bigFactors) {
				int bigFactorBits = 64 - Long.numberOfLeadingZeros(bigFactor);
				partialBigFactorSizes[bigFactors.length].add(bigFactorBits);
				prod = prod.multiply(BigInteger.valueOf(bigFactor));
			}
			partialQRestSizes[bigFactors.length].add(prod.bitLength());
		}

		if (ANALYZE_Q_SIGNS) {
			if (newPartial.smallFactors[0] != -1 || (newPartial.smallFactorExponents[0]&1) == 0) partialWithPositiveQCount++;
		}
	}
	
	@SuppressWarnings("unused")
	private void dropPartial(Partial partial, Long[] bigFactors) {
		for (Long bigFactor : bigFactors) {
			ArrayList<Partial> partialCongruenceList = largeFactors_2_partials.get(bigFactor);
			partialCongruenceList.remove(partial);
			if (partialCongruenceList.size()==0) {
				// partialCongruenceList is empty now -> drop the whole entry
				largeFactors_2_partials.remove(bigFactor);
			}
		}
	}

	@Override
	public int getSmoothCongruenceCount() {
		return smoothCongruences.size();
	}

	@Override
	public Collection<Smooth> getSmoothCongruences() {
		return smoothCongruences;
	}
	
	@Override
	public int getPartialCongruenceCount() {
		return totalPartialCount;
	}

	@Override
	public BigInteger getFactor() {
		return factor;
	}
	
	@Override
	public CongruenceCollectorReport getReport() {
		return new CongruenceCollectorReport(getPartialCongruenceCount(), smoothCongruences.size(), smoothFromPartialCounts, partialCounts, perfectSmoothCount,
											 partialQRestSizes, partialBigFactorSizes, smoothQRestSizes, smoothBigFactorSizes, partialWithPositiveQCount, smoothWithPositiveQCount,
											 maxRelatedPartialsCount, partialSolver.getMaxMatrixSize());
	}
	
	@Override
	public long getCollectDuration() {
		return ccDuration;
	}
	
	@Override
	public long getSolverDuration() {
		return solverDuration;
	}
	
	@Override
	public int getSolverRunCount() {
		return solverRunCount;
	}

	@Override
	public int getTestedNullVectorCount() {
		return testedNullVectorCount;
	}
	
	@Override
	public void cleanUp() {
		smoothCongruences = null;
		largeFactors_2_partials = null;
		factorTest = null;
		partialSolver.cleanUp();
	}
}