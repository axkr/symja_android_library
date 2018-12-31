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
package de.tilman_neumann.jml.factor.siqs.sieve;

import java.math.BigInteger;

//import org.apache.log4j.Logger;

/**
 * Basic parameters for the quadratic sieve.
 * @author Tilman Neumann
 */
public class SieveParams {
//	private static final Logger LOG = Logger.getLogger(SieveParams.class);
//	private static final boolean DEBUG = false;

	/** the index of the smallest prime used for sieving. */
	public int pMinIndex;
	/** the smallest prime used for sieving. */
	public int pMin;
	/** the largest prime in the prime base */
	public int pMax;
	/** the size of the sieve array (per sign) */
	public int sieveArraySize;
	/** maximal Q_rest accepted as smooth candidate */
	public double maxQRest;
	/** sieve array initializer value */
	public byte initializer;
	/** multiplier to scale ln(p) values to the chosen log base */
	public float lnPMultiplier;
	
	public SieveParams(BigInteger kN, int[] primeBase, int primeBaseSize, int sieveArraySize, double maxQRest, int wantedMinLogPSum) {
		// pMinIndex ~ primeBaseSize^0.33 looks clearly better than primeBaseSize^0.3 or primeBaseSize^0.36.
		// We avoid p[0]==2 which is not used in several sieves.
		this.pMinIndex = Math.max(1, (int) Math.cbrt(primeBaseSize));
		this.pMin = primeBase[pMinIndex];
		this.pMax = primeBase[primeBaseSize-1];
		this.sieveArraySize = sieveArraySize;
		this.maxQRest = maxQRest;
		
		// Computes the minimal sum of ln(p_i) values required for a Q to pass the sieve.
		// This bound is computed as ln(Q/a) - ln(max rest), where ln(Q/a) is the natural logarithm of average (Q/a)-values,
		// estimated according to the papers of Contini, Pomerance and Silverman as ln(Q/a) = ln(M) + ln(kN)/2 - 1/2.
		double minLnPSum = Math.log(sieveArraySize) + Math.log(kN.doubleValue())/2 - 0.5 - Math.log(maxQRest);
		// convert the sieve bound from natural logarithm to the actual logBase:
		float lnLogBase = (float) (minLnPSum / wantedMinLogPSum); // normalizer to be used as a divisor for p_i values
		int minLogPSum = (int) (minLnPSum / lnLogBase); // floor, result should be ~wantedMinLogPSum
//		if (DEBUG) {
//			float logBase = (float) Math.exp(lnLogBase);
//			LOG.debug("logBase=" + logBase + ", lnLogBase=" + lnLogBase + ", minLnPSum = " + minLnPSum + ", minLogPSum = " + minLogPSum);
//		}
		initializer = computeInitializerValue(primeBase, pMinIndex, minLogPSum, lnLogBase);
		lnPMultiplier = 1.0F/lnLogBase; // normalizer to be used as a multiplier for p_i values (faster than a divisor)
	}
	
	/**
	 * Compute the initializer value.
	 * @param primesArray prime base
	 * @param pMinIndex the index of the first prime used for sieving
	 * @param minLogPSum
	 * @param lnLogBase
	 * @return initializer byte value
	 */
	private byte computeInitializerValue(int[] primesArray, int pMinIndex, int minLogPSum, double lnLogBase) {
		// compute contribution of small primes in nats
		double lnSmallPSum = 0;
		for (int i=pMinIndex-1; i>=0; i--) {
			int p = primesArray[i];
			lnSmallPSum += Math.log(p) / p;
		}
		// convert value from base e to wanted log base
		double logSmallPSum = lnSmallPSum / lnLogBase;
		// compute initializerValue, rounded
		byte initializerValue = (byte) (128 - minLogPSum + logSmallPSum + 0.5);
//		if (DEBUG) LOG.debug("initializerValue = " + initializerValue);
		return initializerValue;
	}

	public byte[] getInitializerBlock() {
		byte[] initializerBlock = new byte[256];
		for (int i=255; i>=0; i--) {
			initializerBlock[i] = initializer;
		}
		return initializerBlock;
	}
}
