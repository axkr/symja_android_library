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

import static de.tilman_neumann.jml.factor.base.AnalysisOptions.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.util.Multiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Collects smooth and partial congruences, and assembles partials to smooth congruences on-the-fly.
 * Partials may have any number of large factors.
 *
 * @author Tilman Neumann
 */
public class CongruenceCollector {
  private static final Logger LOG = Logger.getLogger(CongruenceCollector.class);
  private static final boolean DEBUG = false; // used for logs and asserts

  /** smooth congruences */
  private ArrayList<Smooth> smoothCongruences;
  /**
   * A map from big factors with odd exp to partial congruences. Here we need a 1:n relation because
   * one partial can have several big factors; thus one big factor may be contained in many distinct
   * partials.
   */
  private HashMap<Long, ArrayList<Partial>>
      largeFactors_2_partials; // here HashMap is faster than TreeMap or LinkedHashMap
  /** A solver used to create smooth congruences from partials */
  private PartialSolver partialSolver = new PartialSolver();
  /** factor tester */
  private FactorTest factorTest;

  // statistics
  private int perfectSmoothCount, totalPartialCount;
  private int[] smoothFromPartialCounts, partialCounts;
  private Multiset<Integer> oddExpBigFactorSizes4Smooth, oddExpBigFactorSizes;
  private int partialWithPositiveQCount, smoothWithPositiveQCount;

  /**
   * Initialize congruence collector for a new N.
   *
   * @param N
   * @param factorTest
   */
  public void initialize(BigInteger N, FactorTest factorTest) {
    smoothCongruences = new ArrayList<Smooth>();
    largeFactors_2_partials = new HashMap<Long, ArrayList<Partial>>();
    this.factorTest = factorTest;

    // statistics
    totalPartialCount = 0;
    if (ANALYZE) {
      perfectSmoothCount = 0;
      // zero-initialized smoothFromPartialCounts: index 0 -> from 1-partials, index 1 -> from
      // 2-partials, index 2 -> from 2-partials
      smoothFromPartialCounts = new int[3];
      partialCounts = new int[3];
    }
    if (ANALYZE_LARGE_FACTOR_SIZES) {
      // collected vs. useful partials
      oddExpBigFactorSizes4Smooth = new SortedMultiset_BottomUp<Integer>();
      oddExpBigFactorSizes = new SortedMultiset_BottomUp<Integer>();
    }
    if (ANALYZE_Q_SIGNS) {
      // Q-analysis
      partialWithPositiveQCount = 0;
      smoothWithPositiveQCount = 0;
    }
  }

  /**
   * Add a new elementary partial or smooth congruence.
   *
   * @param aqPair
   * @return true if a smooth congruence was added
   * @throws FactorException
   */
  public boolean add(AQPair aqPair) throws FactorException {
    if (DEBUG) LOG.debug("new aqPair = " + aqPair);
    if (aqPair instanceof Smooth) {
      Smooth smooth = (Smooth) aqPair;
      boolean added = addSmooth(smooth);
      if (added) {
        if (DEBUG)
          LOG.debug(
              "Found new smooth congruence "
                  + smooth
                  + " --> #smooth = "
                  + smoothCongruences.size()
                  + ", #partials = "
                  + getPartialCongruenceCount());
        if (ANALYZE) perfectSmoothCount++;
        return true;
      }
      return false;
    }

    // otherwise aqPair must be a partial with at least one large factor.
    Partial partial = (Partial) aqPair;
    Long[] oddExpBigFactors = partial.getLargeFactorsWithOddExponent();
    int oddExpBigFactorsCount = oddExpBigFactors.length;
    if (DEBUG) assert (oddExpBigFactorsCount > 0);

    // Check if the partial helps to assemble a smooth congruence:
    // First collect all partials that are somehow related to the new partial via big factors:
    HashSet<Partial> relatedPartials =
        findRelatedPartials(oddExpBigFactors); // oddExpBigFactors is not modified in the method
    if (DEBUG) LOG.debug("#relatedPartials = " + relatedPartials.size());
    if (relatedPartials.size() > 0) {
      // We found some "old" partials that share at least one big factor with the new partial.
      // Since relatedPartials is a set, we can not get duplicate AQ-pairs.
      relatedPartials.add(partial);
      // Solve partial congruence equation system
      ArrayList<Smooth> foundSmooths =
          partialSolver.solve(relatedPartials); // throws FactorException
      if (foundSmooths.size() > 0) {
        // We found one or more smooths from the new partial
        int addedCount = 0;
        for (Smooth foundSmooth : foundSmooths) {
          if (addSmooth(foundSmooth)) {
            if (ANALYZE) {
              // count kind of partials that helped to find smooths
              int maxLargeFactorCount = 0;
              for (AQPair aqPairFromSmooth : foundSmooth.getAQPairs()) {
                int largeFactorCount = aqPairFromSmooth.getNumberOfLargeQFactors();
                if (largeFactorCount > maxLargeFactorCount) maxLargeFactorCount = largeFactorCount;
              }
              smoothFromPartialCounts[maxLargeFactorCount - 1]++;
              if (DEBUG)
                LOG.debug(
                    "Found smooth congruence from "
                        + maxLargeFactorCount
                        + "-partial --> #smooth = "
                        + smoothCongruences.size()
                        + ", #partials = "
                        + getPartialCongruenceCount());
            }
            addedCount++; // increment counter if foundSmooth was really added
          }
        }
        if (addedCount > 0) {
          if (ANALYZE_LARGE_FACTOR_SIZES) {
            // register size of large factors that helped to find smooths
            for (Long oddExpBigFactor : oddExpBigFactors) {
              int oddExpBigFactorBits = 64 - Long.numberOfLeadingZeros(oddExpBigFactor);
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

    // We were not able to construct a smooth congruence with the new partial, so just keep the
    // partial:
    addPartial(partial, oddExpBigFactors);
    totalPartialCount++;
    if (DEBUG)
      LOG.debug(
          "Found new partial relation "
              + aqPair
              + " --> #smooth = "
              + smoothCongruences.size()
              + ", #partials = "
              + totalPartialCount);
    if (ANALYZE) partialCounts[oddExpBigFactorsCount - 1]++;
    return false; // no smooth added
  }

  /**
   * Find "old" partials related to a new partial. The large factors of the new partial remain
   * unaltered.
   *
   * @param largeFactorsOfPartial the large factors with odd exponent of the new partial
   * @return set of related partial congruences
   */
  private HashSet<Partial> findRelatedPartials(Long[] largeFactorsOfPartial) {
    HashSet<Long> processedLargeFactors = new HashSet<>();
    HashSet<Partial> relatedPartials =
        new HashSet<>(); // we need a set to avoid adding the same partial more than once
    ArrayList<Long> currentLargeFactors = new ArrayList<>();
    for (Long largeFactor : largeFactorsOfPartial) {
      currentLargeFactors.add(largeFactor);
    }
    while (currentLargeFactors.size() > 0) {
      if (DEBUG) LOG.debug("currentLargeFactors = " + currentLargeFactors);
      ArrayList<Long> nextLargeFactors =
          new ArrayList<>(); // no Set required, ArrayList has faster iteration
      for (Long largeFactor : currentLargeFactors) {
        processedLargeFactors.add(largeFactor);
        ArrayList<Partial> partialList = largeFactors_2_partials.get(largeFactor);
        if (partialList != null && partialList.size() > 0) {
          for (Partial relatedPartial : partialList) {
            relatedPartials.add(relatedPartial);
            for (Long nextLargeFactor : relatedPartial.getLargeFactorsWithOddExponent()) {
              if (!processedLargeFactors.contains(nextLargeFactor))
                nextLargeFactors.add(nextLargeFactor);
            }
          }
        }
      }
      currentLargeFactors = nextLargeFactors;
    }
    return relatedPartials;
  }

  /**
   * Add smooth congruence.
   *
   * @param smoothCongruence
   * @return true if a smooth congruence was added
   * @throws FactorException
   */
  protected boolean addSmooth(Smooth smoothCongruence) throws FactorException {
    if (smoothCongruence.isExactSquare()) {
      // We found a square congruence!
      factorTest.testForFactor(smoothCongruence.getAQPairs());
      // no FactorException -> the square congruence was improper -> drop it
      return false;
    }
    // No square -> add.
    // Here the same congruence may be added several times. This results in the need to test too
    // many null vectors.
    // But avoiding such duplicates is asymptotically unfavourable because their likelihood
    // decreases quickly.
    smoothCongruences.add(smoothCongruence);

    // Q-analysis
    if (ANALYZE_Q_SIGNS)
      if (smoothCongruence.getMatrixElements()[0] != -1) smoothWithPositiveQCount++;

    return true;
  }

  private void addPartial(Partial newPartial, Long[] oddExpBigFactors) {
    for (Long oddExpBigFactor : oddExpBigFactors) {
      ArrayList<Partial> partialCongruenceList = largeFactors_2_partials.get(oddExpBigFactor);
      // For large N, most large factors appear only once. Therefore we create an ArrayList with
      // initialCapacity=1 to safe memory.
      // Even less memory would be needed if we had a HashMap<Long, Object>
      // oddExpBigFactors_2_partialCongruences
      // and store AQPairs or AQPair[] in the Object part. But I do not want to break the
      // generics...
      if (partialCongruenceList == null) partialCongruenceList = new ArrayList<Partial>(1);
      partialCongruenceList.add(newPartial);
      largeFactors_2_partials.put(oddExpBigFactor, partialCongruenceList);
    }

    if (ANALYZE_LARGE_FACTOR_SIZES) {
      for (Long oddExpBigFactor : oddExpBigFactors) {
        int oddExpBigFactorBits = 64 - Long.numberOfLeadingZeros(oddExpBigFactor);
        oddExpBigFactorSizes.add(oddExpBigFactorBits);
      }
    }

    if (ANALYZE_Q_SIGNS) {
      if (newPartial.smallFactors[0] != -1 || (newPartial.smallFactorExponents[0] & 1) == 0)
        partialWithPositiveQCount++;
    }
  }

  @SuppressWarnings("unused")
  private void dropPartial(Partial partial, Long[] oddExpBigFactors) {
    for (Long oddExpBigFactor : oddExpBigFactors) {
      ArrayList<Partial> partialCongruenceList = largeFactors_2_partials.get(oddExpBigFactor);
      partialCongruenceList.remove(partial);
      if (partialCongruenceList.size() == 0) {
        // partialCongruenceList is empty now -> drop the whole entry
        largeFactors_2_partials.remove(oddExpBigFactor);
      }
    }
  }

  /** @return number of smooth congruences found so far. */
  public int getSmoothCongruenceCount() {
    return smoothCongruences.size();
  }

  /** @return smooth congruences found so far. */
  public ArrayList<Smooth> getSmoothCongruences() {
    return smoothCongruences;
  }

  /** @return number of partial congruences found so far. */
  public int getPartialCongruenceCount() {
    return totalPartialCount;
  }

  public CongruenceCollectorReport getReport() {
    return new CongruenceCollectorReport(
        getPartialCongruenceCount(),
        smoothCongruences.size(),
        smoothFromPartialCounts,
        partialCounts,
        perfectSmoothCount,
        oddExpBigFactorSizes,
        oddExpBigFactorSizes4Smooth,
        partialWithPositiveQCount,
        smoothWithPositiveQCount);
  }

  /** Release memory after a factorization. */
  public void cleanUp() {
    smoothCongruences = null;
    largeFactors_2_partials = null;
    factorTest = null;
    partialSolver.cleanUp();
  }
}
