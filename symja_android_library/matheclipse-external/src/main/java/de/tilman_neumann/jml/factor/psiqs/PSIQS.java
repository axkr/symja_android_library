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
package de.tilman_neumann.jml.factor.psiqs;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorParallel;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator01;
import de.tilman_neumann.jml.factor.siqs.powers.PowerFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;

/**
 * Multi-threaded SIQS using Sieve03g.
 *
 * @author Tilman Neumann
 */
public class PSIQS extends PSIQSBase {

  /**
   * Standard constructor.
   *
   * @param Cmult multiplier for prime base size
   * @param Mmult multiplier for sieve array size
   * @param wantedQCount hypercube dimension (null for automatic selection)
   * @param maxQRestExponent A Q with unfactored rest QRest is considered smooth if QRest <=
   *     N^maxQRestExponent. Good values are 0.16..0.19; null means that it is determined
   *     automatically.
   * @param numberOfThreads
   * @param powerFinder algorithm to add powers to the primes used for sieving
   * @param matrixSolver solver for smooth congruences matrix
   * @param useLegacyFactoring if true then factor() uses findSingleFactor(), otherwise
   *     searchFactors()
   * @param searchSmallFactors if true then search for small factors before SIQS or PSIQS is run
   */
  public PSIQS(
      float Cmult,
      float Mmult,
      Integer wantedQCount,
      Float maxQRestExponent,
      int numberOfThreads,
      PowerFinder powerFinder,
      MatrixSolver matrixSolver,
      boolean useLegacyFactoring,
      boolean searchSmallFactors) {

    super(
        Cmult,
        Mmult,
        maxQRestExponent,
        numberOfThreads,
        null,
        powerFinder,
        matrixSolver,
        new AParamGenerator01(wantedQCount),
        useLegacyFactoring,
        searchSmallFactors);
  }

  @Override
  public String getName() {
    String maxQRestExponentStr = "maxQRestExponent=" + String.format("%.3f", maxQRestExponent);
    String modeStr = "mode = " + (useLegacyFactoring ? "legacy" : "advanced");
    return "PSIQS(Cmult="
        + Cmult
        + ", Mmult="
        + Mmult
        + ", qCount="
        + apg.getQCount()
        + ", "
        + maxQRestExponentStr
        + ", "
        + powerFinder.getName()
        + ", "
        + matrixSolver.getName()
        + ", "
        + numberOfThreads
        + " threads, "
        + modeStr
        + ")";
  }

  @Override
  protected PSIQSThreadBase createThread(
      int k,
      BigInteger N,
      BigInteger kN,
      int d,
      SieveParams sieveParams,
      BaseArrays baseArrays,
      AParamGenerator apg,
      CongruenceCollectorParallel cc,
      int threadIndex) {

    return new PSIQSThread(k, N, kN, d, sieveParams, baseArrays, apg, cc, threadIndex);
  }
}
