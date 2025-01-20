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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.Timer;

/**
 * A copy of CongruenceCollector01 used for collecting congruences in nested SIQS.
 * Reduced to work with perfect smooths and 1-partials only.
 * @author Tilman Neumann
 */
public class CongruenceCollectorSmall implements CongruenceCollector {
	private static final Logger LOG = LogManager.getLogger(CongruenceCollectorSmall.class);
	private static final boolean DEBUG = false; // used for logs and asserts
	private static final boolean ANALYZE = false;
	
	/** smooth congruences; is a list here because we won't get 3-partials from small N */
	private ArrayList<Smooth> smoothCongruences;
	
	/** For N <= 200 bit we should have only perfect smooths or 1-partials, so we can simplify the map here. */
	private HashMap<Long, Partial> largeFactors_2_partials;
	
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
	private int[] smoothFromPartialCounts, partialCounts;
	
	/** the biggest number of partials involved to find a smooth relation from partials */
	private int maxRelatedPartialsCount;

	private Timer timer = new Timer();
	private long ccDuration, solverDuration;
	private int solverRunCount, testedNullVectorCount;

	/**
	 * Default constructor that expects 10 more equations than variables to run the matrix solver.
	 */
	public CongruenceCollectorSmall() {
		this(10);
	}

	/**
	 * Full constructor.
	 * @param extraCongruences The difference #equations-#variables required before the solver is started.
	 */
	public CongruenceCollectorSmall(int extraCongruences) {
		this.extraCongruences = extraCongruences;
	}
	
	@Override
	public void initialize(BigInteger N, int primeBaseSize, MatrixSolver matrixSolver, FactorTest factorTest) {
		smoothCongruences = new ArrayList<Smooth>();
		largeFactors_2_partials = new HashMap<Long, Partial>();
		this.factorTest = factorTest;
		
		// statistics
		totalPartialCount = 0;
		if (ANALYZE) {
			perfectSmoothCount = 0;
			// zero-initialized smoothFromPartialCounts: index 0 -> from 1-partials, index 1 -> from 2-partials, index 2 -> from 3-partials
			smoothFromPartialCounts = new int[3];
			partialCounts = new int[3];
			maxRelatedPartialsCount = 0;
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
			if (ANALYZE) if (addedSmooth) perfectSmoothCount++;
			return addedSmooth;
		}
		
		// otherwise aqPair must be a partial with one large factor.
		if (DEBUG) Ensure.ensureTrue(aqPair instanceof Partial1Large);
		Partial1Large partial = (Partial1Large) aqPair;
		final Long bigFactor = partial.getLargeFactorsWithOddExponent()[0];
				
		// Check if the partial helps to assemble a smooth congruence:
		// This is the case if we already had another partial with the same large factor.
		Partial relatedPartial = largeFactors_2_partials.get(bigFactor);
		if (relatedPartial != null) {
			Set<AQPair> partials = new LinkedHashSet<>();
			partials.add(partial);
			partials.add(relatedPartial);
			Smooth foundSmooth = new SmoothComposite(partials);
			boolean added = addSmooth(foundSmooth); // throws FactorException
			if (ANALYZE) {
				if (added) {
					smoothFromPartialCounts[0]++;
					maxRelatedPartialsCount = 1;
				}
			}
			return added;
			// Not adding the new partial is sufficient to keep the old partials linear independent,
			// which is required to avoid duplicate solutions.
		}
		
		// We were not able to construct a smooth congruence with the new partial, so just keep the partial:
		largeFactors_2_partials.put(bigFactor, partial);
		totalPartialCount++;
		if (DEBUG) LOG.debug("Found new partial relation " + aqPair + " --> #smooth = " + smoothCongruences.size() + ", #partials = " + totalPartialCount);
		if (ANALYZE) partialCounts[0]++;
		return false; // no smooth added
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
		// No square -> add.
		// With no 3LP around we won't get many duplicates here. But we keep the check just in case.
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
		return smoothCongruences.add(smoothCongruence);
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
											 null, null, null, null, 0, 0, maxRelatedPartialsCount, 0);
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
	}
}