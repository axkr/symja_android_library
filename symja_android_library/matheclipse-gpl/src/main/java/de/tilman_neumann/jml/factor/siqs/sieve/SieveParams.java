/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

/**
 * Basic parameters for the quadratic sieve.
 * @author Tilman Neumann
 */
public class SieveParams {
	/** k * N */
	public BigInteger kN;
	/** the index of the smallest prime used for sieving. */
	public int pMinIndex;
	/** the smallest prime used for sieving. */
	public int pMin;
	/** the largest prime in the prime base */
	public int pMax;
	/** the size of the sieve array (per sign) */
	public int sieveArraySize;
	
	/** maximal QRest for a sieve hit */
	public double sieveHitBound;
	/** maximal QRest to pass Q to trial division */
	public double tdivTestBound;
	/** maximal QRest to accept any kind of relation as smooth enough */
	public double smoothBound;
	
	/** the scaled logarithm of the avg. Q/(da) size */
	public int logQdivDaEstimate;
	/** scaled minimum logPSum to pass a sieve hit to tdiv */
	public int tdivTestMinLogPSum;
	/** sieve array initializer value */
	public byte initializer;
	/** multiplier to scale ln(p) values to the chosen log base */
	public float lnPMultiplier;
	
	public SieveParams(BigInteger kN, int pMinIndex, int pMin, int pMax, int sieveArraySize, double sieveHitBound, double tdivTestBound, double smoothBound,
			int logQdivDaEstimate, int tdivTestMinLogPSum, byte initializer, float lnPMultiplier) {
		this.kN = kN;
		this.pMinIndex = pMinIndex;
		this.pMin = pMin;
		this.pMax = pMax;
		this.sieveArraySize = sieveArraySize;
		this.sieveHitBound = sieveHitBound;
		this.tdivTestBound = tdivTestBound;
		this.smoothBound = smoothBound;
		this.logQdivDaEstimate = logQdivDaEstimate;
		this.tdivTestMinLogPSum = tdivTestMinLogPSum;
		this.initializer = initializer;
		this.lnPMultiplier = lnPMultiplier;
	}

	public byte[] getInitializerBlock() {
		byte[] initializerBlock = new byte[256];
		for (int i=255; i>=0; i--) {
			initializerBlock[i] = initializer;
		}
		return initializerBlock;
	}
}
