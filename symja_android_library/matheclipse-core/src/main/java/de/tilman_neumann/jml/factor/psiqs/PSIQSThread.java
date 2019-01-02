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

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_2Large_UBI;

/**
 * A polynomial generation/sieve/trial division thread using Sieve03g.
 * @author Tilman Neumann
 */
public class PSIQSThread extends PSIQSThreadBase {

	/**
	 * Standard constructor.
	 * @param k
	 * @param N
	 * @param kN
	 * @param d the d-parameter of quadratic polynomials Q(x) = (d*a*x + b)^2 - kN; typically 1 or 2
	 * @param sieveParams basic sieve parameters
	 * @param baseArrays primes, power arrays after adding powers
	 * @param apg
	 * @param aqPairBuffer
	 * @param threadIndex
	 * @param profile
	 */
	public PSIQSThread(
			int k, BigInteger N, BigInteger kN, int d, SieveParams sieveParams, BaseArrays baseArrays,
			AParamGenerator apg, AQPairBuffer aqPairBuffer, int threadIndex, boolean profile) {
		
		super(k, N, kN, d, sieveParams, baseArrays, apg, aqPairBuffer, new SIQSPolyGenerator(), new Sieve03g(),
			  new TDiv_QS_2Large_UBI(), threadIndex, profile);
	}
}
