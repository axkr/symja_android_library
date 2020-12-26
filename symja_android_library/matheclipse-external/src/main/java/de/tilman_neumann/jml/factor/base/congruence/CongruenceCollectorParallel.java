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
import java.util.List;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.util.Timer;

/**
 * Collects smooth and partial congruences, and assembles partials to smooth congruences on-the-fly.
 * Partials may have any number of large factors.
 *
 * <p>This implementation is appropriate for multi-threaded congruence generation.
 *
 * @author Tilman Neumann
 */
public class CongruenceCollectorParallel extends CongruenceCollector {
  private static final Logger LOG = Logger.getLogger(CongruenceCollectorParallel.class);
  private static final boolean DEBUG = false; // used for logs and asserts

  // The number of congruences we need to find before we try to solve the smooth congruence equation
  // system:
  // We want: #equations = #variables + some extra congruences
  private int requiredSmoothCongruenceCount;
  // extra congruences to have a bigger chance that the equation system solves. the likelihood is >=
  // 1-2^(extraCongruences+1)
  private int extraCongruences;

  private MatrixSolver matrixSolver;

  public BigInteger factor;

  // statistics
  private Timer timer = new Timer();
  private long ccDuration, solverDuration;
  private int solverRunCount;

  public CongruenceCollectorParallel(int extraCongruences) {
    this.extraCongruences = extraCongruences;
  }

  /**
   * Initialize congruence collector for a new N.
   *
   * @param N
   * @param requiredSmoothCongruenceCount
   * @param matrixSolver
   * @param factorTest
   */
  public void initialize(
      BigInteger N,
      int requiredSmoothCongruenceCount,
      MatrixSolver matrixSolver,
      FactorTest factorTest) {
    super.initialize(N, factorTest);
    this.requiredSmoothCongruenceCount = requiredSmoothCongruenceCount + extraCongruences;
    this.matrixSolver = matrixSolver;
    ccDuration = solverDuration = 0;
    solverRunCount = 0;
    factor = null;
  }

  /**
   * Collect AQ pairs and run the matrix solver if appropriate. This method should be run in a block
   * synchronized on this.
   *
   * @param aqPairs
   */
  public void collectAndProcessAQPairs(List<AQPair> aqPairs) {
    // LOG.debug("add " + aqPairs.size() + " new AQ-pairs to CC");
    try {
      // Add new data to the congruenceCollector and eventually run the matrix solver.
      if (ANALYZE) timer.capture();
      for (AQPair aqPair : aqPairs) {
        boolean addedSmooth = add(aqPair);
        if (addedSmooth) {
          int smoothCongruenceCount = getSmoothCongruenceCount();
          if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
            // Try to solve equation system
            if (ANALYZE) {
              ccDuration += timer.capture();
              solverRunCount++;
              if (DEBUG)
                LOG.debug(
                    "Run "
                        + solverRunCount
                        + ": #smooths = "
                        + smoothCongruenceCount
                        + ", #requiredSmooths = "
                        + requiredSmoothCongruenceCount);
            }
            ArrayList<Smooth> congruences = getSmoothCongruences();
            // The matrix solver should also run synchronized, because blocking the other threads
            // means that the current thread can run at a higher clock rate.
            matrixSolver.solve(congruences); // throws FactorException
            if (ANALYZE) solverDuration += timer.capture();
            // Extend equation system and continue searching smooth congruences
            requiredSmoothCongruenceCount += extraCongruences;
          }
        }
      }
      if (ANALYZE) ccDuration += timer.capture();
    } catch (FactorException fe) {
      factor = fe.getFactor();
      if (ANALYZE) solverDuration += timer.capture();
      return;
    }
  }

  public long getCollectDuration() {
    return ccDuration;
  }

  public long getSolverDuration() {
    return solverDuration;
  }

  public int getSolverRunCount() {
    return solverRunCount;
  }
}
