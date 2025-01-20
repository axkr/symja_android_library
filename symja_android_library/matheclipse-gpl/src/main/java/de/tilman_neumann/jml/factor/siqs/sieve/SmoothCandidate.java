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

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;

/**
 * A sieve hit that is a candidate to yield a smooth relation.
 * @author Tilman Neumann
 */
public class SmoothCandidate {
	/** The sieve location */
	public int x;
	/** the logP sum from trial dividing unsieved base elements */
	public double logPSum;
	/** The rest of Q(x)/(da) that still needs to get factorized, where a is the a-parameter of the polynomial and d==2 for kN==1 (mod 8), 2 else */
	public BigInteger QRest;
	/** A(x) = d*a*x + b */
	public BigInteger A;
	/** Small factors found by trial dividing small primes */
	public SortedIntegerArray smallFactors;
	
	public SmoothCandidate(int x) {
		this(x, null, null, null);
	}

	public SmoothCandidate(int x, BigInteger QRest, BigInteger A) {
		this(x, QRest, A, null);
	}

	public SmoothCandidate(int x, BigInteger QRest, BigInteger A, SortedIntegerArray smallFactors) {
		this.x = x;
		this.QRest = QRest;
		this.A = A;
		this.smallFactors = smallFactors;
	}
}
