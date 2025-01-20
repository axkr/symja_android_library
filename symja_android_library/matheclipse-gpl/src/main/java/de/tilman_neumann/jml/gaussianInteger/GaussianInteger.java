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
package de.tilman_neumann.jml.gaussianInteger;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigRational;
import de.tilman_neumann.util.Ensure;

/**
 * The Gaussian integers are the set Z[i] = {x + iy : x, y ∈ Z} of complex numbers whose real and imaginary parts are both integers.
 * 
 * @author Tilman Neumann
 * 
 * @see https://www.math.uci.edu/~ndonalds/math180b/6gaussian.pdf
 * @see https://en.wikipedia.org/wiki/Gaussian_integer
 */
public class GaussianInteger {

	private static final Logger LOG = LogManager.getLogger(GaussianInteger.class);
	
	private static final boolean DEBUG = false;
	
	/** Real part */
	private BigInteger x;
	
	/** Imaginary part */
	private BigInteger y;
	
	public GaussianInteger(BigInteger x, BigInteger y) {
		this.x = x;
		this.y = y;
	}
	
	public BigInteger realPart() {
		return x;
	}
	
	public BigInteger imaginaryPart() {
		return y;
	}
	
	public GaussianInteger conjugate() {
		return new GaussianInteger(x, y.negate());
	}
	
	public GaussianInteger negate() {
		return new GaussianInteger(x.negate(), y.negate());
	}
	
	/**
	 * The norm of a Gaussian integer is its product with its conjugate: N(a+ib) = (a+ib)*(a-ib) = a^2 + b^2<br/>
	 * It has the following useful properties:<br/>
	 * 1. (Multiplicativity) N(αβ) = N(α)N(β): this holds for any complex numbers α, β.<br/>
     * 2. (Units) α is a unit if and only if N(α) = 1, whence Z[i] has precisely four units: ±1, ±i.<br/>
     * 3. The norm of a Gaussian integer is the square of its absolute value as a complex number.<br/>
	 * 4. As a sum of two squares, the norm of a Gaussian integer cannot be of the form 4k + 3, with k integer.<br/>
	 * 
	 * @return the norm of this
	 */
	public BigInteger norm() {
		return x.multiply(x).add(y.multiply(y));
	}

	public boolean isZero() {
		return x.equals(I_0) && y.equals(I_0);
	}
	
	/**
	 * @return true if this is one of the four units: ±1, ±i, otherwise false.
	 */
	public boolean isUnit() {
		return (x.equals(I_0) && y.abs().equals(I_1)) || (x.abs().equals(I_1) && y.equals(I_0));
	}
	
	public GaussianInteger add(GaussianInteger b) {
		return new GaussianInteger(x.add(b.x), y.add(b.y));
	}
	
	public GaussianInteger subtract(GaussianInteger b) {
		return new GaussianInteger(x.subtract(b.x), y.subtract(b.y));
	}

	public GaussianInteger multiply(GaussianInteger b) {
		BigInteger bx = b.x;
		BigInteger by = b.y;
		BigInteger cx = x.multiply(bx).subtract(y.multiply(by));
		BigInteger cy = x.multiply(by).add(y.multiply(bx));
		return new GaussianInteger(cx, cy);
	}
	
	public GaussianInteger square() {
		// (x+iy)^2 = x^2 + 2ixy + i2y2 = x^2-y^2 + i * 2xy
		return new GaussianInteger(x.multiply(x).subtract(y.multiply(y)), x.multiply(y).shiftLeft(1));
	}
	
	/**
	 * Divisibility test.
	 * @param b GaussianInteger
	 * @return true if b divides this, false otherwise.
	 * @throws ArithmeticException if b is zero
	 */
	public boolean isDivisibleBy(GaussianInteger b) throws ArithmeticException {
		if (b.isZero()) throw new ArithmeticException("division by zero");
		
		// multiply numerator and denominator of this by the conjugate of b, so that the resulting denominator is the norm of b and integer
		GaussianInteger num = this.multiply(b.conjugate());
		BigInteger den = b.norm();
		return num.x.mod(den).equals(I_0) && num.y.mod(den).equals(I_0);
	}
	
	/**
	 * Divide this by b.
	 * 
	 * @param b
	 * @return [quotient, remainder]
	 */
	public GaussianInteger[] divide(GaussianInteger b) {
		if (DEBUG) LOG.debug("divide (" + this + ") / (" + b + ") with N(" + b + ") = " + b.norm());
		
		if (b.isZero()) throw new ArithmeticException("division by zero");
		
		// Theorem: Let a, b ∈ Z[i] with b != 0. Then there are c, d ∈ Z[i] for which a = b*c + d and N(d) < N(b).
		// Notes: * The norm gives the required notion that the remainder d be smaller than the divisor b.
		//        * Unlike with the division algorithm in Z, we make no claim that c and d are unique
		
		// a/b = (a*conjugate(b)) / (b*conjugate(b)) = a*conjugate(b) / N(b)
		GaussianInteger aDivBNum = this.multiply(b.conjugate());
		BigInteger aDivBDen = b.norm();
		
		// The exact a/b value lies within four elements of the Gaussian integer lattice.
		// We want the quotient c to be the lattice element with minimal N(d), which is also the solution closest to the exact value of a/b.
		// The best choice can be computed independently in the two lattice dimensions.
		BigInteger cRe = new BigRational(aDivBNum.x, aDivBDen).round();
		BigInteger cIm = new BigRational(aDivBNum.y, aDivBDen).round();
		
		// Finally compute d = a - b*c
		GaussianInteger c = new GaussianInteger(cRe, cIm);
		GaussianInteger d = this.subtract(b.multiply(c));

		if (DEBUG) LOG.debug("result: c = " + c + ", d = " + d + ", N(d) = " + d.norm());

		// Make sure that N(d) < N(b)
		if (DEBUG) Ensure.ensureSmaller(d.norm(), b.norm());
		
		return new GaussianInteger[] {c, d};
	}
	
	/**
	 * Computes the gaussian integer gcd of this and b.
	 * A gcd multiplied by one of the four units +-1, +-i is a valid gcd, too. Here we return the single solution with real part > 0 and imaginary part >=0.
	 * 
	 * @param b
	 * @return gcd(this, b)
	 */
	public GaussianInteger gcd(GaussianInteger b) {
		GaussianInteger d;
		
		if (this.isZero()) {
			d = b; // gcd(0, n) = n
		} else {
			GaussianInteger a = this;
			while (!b.isZero()) {
				if (DEBUG) LOG.debug("gcd: a = " + a + ", b = " + b);
				GaussianInteger[] divRem = a.divide(b);
				a = b;
				b = divRem[1];
			}
			d = a; // preliminary result (up to units)
		}
		
		// If the result so far is d, then there are four gcds: +d = (x, y), -d = (-x, -y), +id = (-y, x), -id = (y, -x).
		// It is common to choose the one with real part > 0 and imaginary part >= 0.
		int reSign = d.realPart().signum();
		int imSign = d.imaginaryPart().signum();
		if (reSign>0 && imSign>=0) return d;                                                             // x>0, y>=0 =>  +d = (x, y)
		if (reSign<0 && imSign<=0) return d.negate();                                                    // x<0, y<=0 =>  -d = (-x, -y) has -x>0, -y>=0
		if (reSign>=0 && imSign<0) return new GaussianInteger(d.imaginaryPart().negate(), d.realPart()); // x>=0, y<0 => +id = (-y, x) has -y>0, x>=0
		if (reSign<=0 && imSign>0) return new GaussianInteger(d.imaginaryPart(), d.realPart().negate()); // x<=0, y>0 => -id = (y, -x) has y>0, -x>=0
		
		// The sign condition that has not been covered yet is d==0. This should never happen...
		throw new IllegalStateException("gcd(" + this + ", " + b + ") is zero ???");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof GaussianInteger)) return false;
		GaussianInteger b = (GaussianInteger) o;
		return x.equals(b.x) && y.equals(b.y);
	}
	
	@Override
	public int hashCode() {
		return (int) (x.longValue() + 31 * y.longValue());
	}
	
	@Override
	public String toString() {
		String imSign = y.signum() < 0 ? "-" : "+";
		return x + " " + imSign + " I*" + y.abs();
	}
}
