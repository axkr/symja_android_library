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
package de.tilman_neumann.jml;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import de.tilman_neumann.jml.base.BigRational;
import de.tilman_neumann.jml.combinatorics.Binomial;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.transcendental.EulerConstant;
import de.tilman_neumann.jml.transcendental.Ln;

/**
 * Computation of harmonic and "hyper-harmonic" numbers.
 * 
 * @author Tilman Neumann
 */
public class HarmonicNumbers {

	private HarmonicNumbers() {
		// static class
	}
	
	/**
	 * Simple series computation of harmonic numbers H_{n} = 1/1 + 1/2 + 1/3 + ... + 1/n.
	 * @param n
	 * @return harmonic number H_{n}
	 */
	public static BigRational harmonic(int n) {
		if (n<1) throw new IllegalArgumentException("argument n must be positive, but is " + n);
		BigRational result = new BigRational(I_0);
		for (int i=1; i<=n; i++) {
			result = result.add(new BigRational(I_1, BigInteger.valueOf(i)));
			// Every now and then we should reduce the fraction...
			// each 100th time is much better than each 10th, which was better than never...
			if (i % 200 == 0) {
				result = result.normalize();
			}
		}
		return result.normalize();
	}
	
	/**
	 * Simple series computation of harmonic numbers H_{n} = 1/1 + 1/2 + 1/3 + ... + 1/n.
	 * @param n
	 * @return harmonic number H_{n}
	 */
	public static double harmonic_dbl(int n) {
		if (n<1) throw new IllegalArgumentException("argument n must be positive, but is " + n);
		double result = 0;
		for (int i=1; i<=n; i++) {
			result += 1.0/i;
		}
		return result;
	}

	/**
	 * Upper bound for the harmonic number H_n.
	 * From http://fredrik-j.blogspot.com/2009/02/how-not-to-compute-harmonic-numbers.html:
	 * H_n = ln(n) + gamma + 1/2*n^-1 - 1/12*n^-2 + 1/120*n^-4 + O(n^-6)
	 * 
	 * @param n
	 * @param scale number of exact after-komma digits
	 * @return upper bound for the harmonic number H_n
	 */
	public static BigDecimal harmonic_upperBound(BigInteger n, Scale scale) {
		BigDecimal result = EulerConstant.gamma(scale);
		result = result.setScale(scale.digits(), RoundingMode.CEILING);
		result = result.add(Ln.ln(new BigDecimal(n), scale));
		BigInteger den = n.shiftLeft(1); // 2n
		result = result.add(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		den = den.multiply(I_6).multiply(n); // 12 n^2
		result = result.subtract(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		den = den.multiply(I_10).multiply(n.multiply(n)); // 120 n^4
		result = result.add(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		return result;
	}

	/**
	 * Lower bound for the harmonic number H_n.
	 * From https://math.stackexchange.com/questions/306371/simple-proof-of-showing-the-harmonic-number-h-n-theta-log-n:
	 * H_n = ln(n) + gamma + 1/2*n^-1 - 1/12*n^-2 + 1/120*n^-4 - 1/252*n^-6 + O(n^-8)
	 * 
	 * @param n
	 * @param scale number of exact after-komma digits
	 * @return lower bound for the harmonic number H_n
	 */
	// TODO larger series expansion using Bernoulli numbers, see https://en.wikipedia.org/wiki/Harmonic_number#Calculation
	public static BigDecimal harmonic_lowerBound(BigInteger n, Scale scale) {
		BigDecimal result = EulerConstant.gamma(scale);
		result = result.setScale(scale.digits(), RoundingMode.FLOOR);
		result = result.add(Ln.ln(new BigDecimal(n), scale));
		BigInteger den = n.shiftLeft(1); // 2n
		result = result.add(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		den = den.multiply(I_6).multiply(n); // 12 n^2
		result = result.subtract(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		BigInteger nSquare = n.multiply(n);
		BigInteger tmp = den.multiply(nSquare); // 12 n^4
		den = tmp.multiply(I_10); // 120 n^4
		result = result.add(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		den = tmp.multiply(I_21).multiply(nSquare); // 252 n^6
		result = result.subtract(F_1.divide(new BigDecimal(den), scale.digits(), RoundingMode.HALF_EVEN));
		return result;
	}

	/**
	 * Closed-form evaluation of "hyper-harmonic numbers" defined by<br>
	 * H_{n,1} = sum_{i=1..n} 1/i<br>
	 * H_{n,r} = sum_{i=1..n} H_{i,r-1}; r>1
	 * @param n principal argument
	 * @param r order
	 * @return hyper-harmonic number H_{n,r}
	 */
	public static BigRational hyperharmonic_closedForm(int n, int r) {
		if (n<1) throw new IllegalArgumentException("argument n must be positive, but is " + n);
		if (r>1) return (harmonic(n+r-1).subtract(harmonic(r-1))).multiply(Binomial.binomial((n+r-1), (r-1)));
		if (r<1) throw new IllegalArgumentException("argument r must be positive, but is " + r);
		return harmonic(n);
	}

	/**
	 * Recurrent computation of "hyper-harmonic numbers" defined by<br>
	 * H_{n,1} = sum_{i=1..n} 1/i<br>
	 * H_{n,r} = sum_{i=1..n} H_{i,r-1}; r>1
	 * @param n principal argument
	 * @param r order
	 * @return hyper-harmonic number H_{n,r}
	 */
	public static BigRational hyperharmonic_recurrent(int n, int r) {
		if (n<1) throw new IllegalArgumentException("argument n must be positive, but is " + n);
		if (r<1) throw new IllegalArgumentException("argument r must be positive, but is " + r);
		return _hyperharmonic_recurrent(n, r);
	}
	
	private static BigRational _hyperharmonic_recurrent(int n, int r) {
		// arguments are already checked
		if (r>1) {
			BigRational ret = new BigRational(I_0, I_1);
			for (int i=1; i<=n; i++) {
				ret = ret.add(_hyperharmonic_recurrent(i,r-1));
			}
			return ret.normalize();
		}
		// r==1
		return harmonic(n);
	}

	/**
	 * Harmonic power series H_{n,r} = sum_{i=1..n} 1/i^r.
	 * Can also be understood as a finite version of Riemanns zeta function.
	 * @param n
	 * @param r
	 * @return H_{n,r}
	 */
	public static BigRational harmonicPower(int n, int r) {
		if (n<1) throw new IllegalArgumentException("argument n must be positive, but is " + n);
		if (r>1) {
			BigRational ret = new BigRational(I_0, I_1);
			for (int i=1; i<=n; i++) {
				ret = ret.add(new BigRational(I_1, BigInteger.valueOf(i).pow(r)));
			}
			return ret.normalize();
		}
		if (r<1) throw new IllegalArgumentException("argument r must be positive, but is " + r);
		// r==1
		return harmonic(n);
	}
}
