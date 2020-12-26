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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorParallel;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver02_BlockLanczos;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator01;
import de.tilman_neumann.jml.factor.siqs.powers.NoPowerFinder;
import de.tilman_neumann.jml.factor.siqs.powers.PowerFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.TimeUtil;
import de.tilman_neumann.util.Timer;

/**
 * Multi-threaded SIQS using Sieve03gU.
 * 
 * @author Tilman Neumann
 */
public class PSIQS_U extends PSIQSBase {

	private static final Logger LOG = Logger.getLogger(PSIQS_U.class);

	/**
	 * Standard constructor.
	 * @param Cmult multiplier for prime base size
	 * @param Mmult multiplier for sieve array size
	 * @param wantedQCount hypercube dimension (null for automatic selection)
	 * @param maxQRestExponent A Q with unfactored rest QRest is considered smooth if QRest <= N^maxQRestExponent.
	 *                         Good values are 0.16..0.19; null means that it is determined automatically.
	 * @param numberOfThreads
	 * @param powerFinder algorithm to add powers to the primes used for sieving
	 * @param matrixSolver solver for smooth congruences matrix
	 * @param useLegacyFactoring if true then factor() uses findSingleFactor(), otherwise searchFactors()
	 * @param searchSmallFactors if true then search for small factors before SIQS or PSIQS is run
	 */
	public PSIQS_U(float Cmult, float Mmult, Integer wantedQCount, Float maxQRestExponent, 
				   int numberOfThreads, PowerFinder powerFinder, MatrixSolver matrixSolver, boolean useLegacyFactoring, boolean searchSmallFactors) {
		
		super(Cmult, Mmult, maxQRestExponent, numberOfThreads, null, powerFinder, matrixSolver, new AParamGenerator01(wantedQCount), useLegacyFactoring, searchSmallFactors);
	}

	@Override
	public String getName() {
		String maxQRestExponentStr = "maxQRestExponent=" + String.format("%.3f", maxQRestExponent);
		String modeStr = "mode = " + (useLegacyFactoring ? "legacy" : "advanced");
		return "PSIQS_U(Cmult=" + Cmult + ", Mmult=" + Mmult + ", qCount=" + apg.getQCount() + ", " + maxQRestExponentStr + ", " + powerFinder.getName() + ", " + matrixSolver.getName() + ", " + numberOfThreads + " threads, " + modeStr + ")";
	}

	@Override
	protected PSIQSThreadBase createThread(
			int k, BigInteger N, BigInteger kN, int d, SieveParams sieveParams, BaseArrays baseArrays,
			AParamGenerator apg, CongruenceCollectorParallel cc, int threadIndex) {
		
		return new PSIQSThread_U(k, N, kN, d, sieveParams, baseArrays, apg, cc, threadIndex);
	}

	// Standalone test --------------------------------------------------------------------------------------------------

	/**
	 * Stand-alone test.
	 * @param args ignored
	 * 
	 * Some test numbers:
	 * 
	 * 15841065490425479923 (64 bit) = 2604221509 * 6082841047 in 211ms. (done by PSIQS)
	 * 
	 * 11111111111111111111111111 (84 bit) = {11=1, 53=1, 79=1, 859=1, 265371653=1, 1058313049=1} in 185ms. (tdiv + PSIQS)
	 * 
	 * 5679148659138759837165981543 (93 bit) = {3=3, 466932157=1, 450469808245315337=1} in 194ms. (tdiv + PSIQS)
	 * 
	 * 11111111111111111111111111155555555555111111111111111 = {67=1, 157=1, 1056289676880987842105819104055096069503860738769=1} in 21ms. (only tdiv needed)
	 * 
	 * 1388091470446411094380555803943760956023126025054082930201628998364642 (230 bit) = {2=1, 3=1, 1907=1, 1948073=1, 1239974331653=1, 50222487570895420624194712095309533522213376829=1} in 304ms. (tdiv + ECM + PSIQS)
	 * 
	 * 10^71-1 = 99999999999999999999999999999999999999999999999999999999999999999999999 (236 bits) = {3=2, 241573142393627673576957439049=1, 45994811347886846310221728895223034301839=1} in 14s, 471ms. (tdiv + PSIQS)
	 * 
	 * 10^79+5923 = 10000000000000000000000000000000000000000000000000000000000000000000000000005923 (263 bit) = {1333322076518899001350381760807974795003=1, 7500063320115780212377802894180923803641=1} in 1m, 35s, 243ms. (PSIQS)
     *
	 * 2900608971182010301486951469292513060638582965350239259380273225053930627446289431038392125 (301 bit)
	 * = 33333 * 33335 * 33337 * 33339 * 33341 * 33343 * 33345 * 33347 * 33349 * 33351 * 33353 * 33355 * 33357 * 33359 * 33361 * 33363 * 33367 * 33369 * 33369 * 33371
	 * = {3=11, 5=3, 7=6, 11=2, 13=2, 17=2, 19=1, 37=1, 41=1, 53=1, 59=1, 61=1, 73=1, 113=1, 151=1, 227=2, 271=1, 337=1, 433=1, 457=1, 547=1, 953=1, 11113=1, 11117=1, 11119=1, 33343=1, 33347=1, 33349=1, 33353=1, 33359=1} in 10ms.
	 * (only tdiv)
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		Timer timer = new Timer();
		PSIQS_U qs = new PSIQS_U(0.32F, 0.37F, null, null, 6, new NoPowerFinder(), new MatrixSolver02_BlockLanczos(), false, true);

		while(true) {
			try {
				LOG.info("Please insert the number to factor:");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = in.readLine();
				String input = line !=null ? line.trim() : "";
				//LOG.debug("input = >" + input + "<");
				BigInteger N = new BigInteger(input);
				LOG.info("Factoring " + N + " (" + N.bitLength() + " bits)...");
				timer.capture();
				SortedMultiset<BigInteger> factors = qs.factor(N);
				if (factors != null) {
					long duration = timer.capture();
					LOG.info("Factored N = " + factors + " in " + TimeUtil.timeStr(duration) + ".");
			} else {
					LOG.info("No factor found...");
				}
			} catch (Exception ex) {
				LOG.error("Error " + ex, ex);
			}
		}
	}
}
