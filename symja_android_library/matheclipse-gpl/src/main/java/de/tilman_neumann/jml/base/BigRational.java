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
package de.tilman_neumann.jml.base;

import java.lang.Number;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.util.Ensure;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Big rational numbers with exact arithmetics.
 * @author Tilman Neumann
 */
public class BigRational extends Number implements Comparable<BigRational> {
	
	private static final long serialVersionUID = -578518708160143029L;
	
	private static final Logger LOG = LogManager.getLogger(BigRational.class);
	
	private static final boolean DEBUG = false;
	
	// Constants -----------------------------------------------------------------------------
	
	public static final BigRational ZERO = new BigRational(I_0);
	public static final BigRational ONE_HALF = new BigRational(I_1, I_2);
	public static final BigRational ONE = new BigRational(I_1);
	
	// Fields -----------------------------------------------------------------------------
	
	/** Numerator. */
	private BigInteger num = null;
	/** Denominator. */
	private BigInteger den = null;

	// Constructors -----------------------------------------------------------------------------
	
	/**
	 * Constructor for an integer.
	 * @param n Number in decimal representation.
	 */
	public BigRational(BigInteger n) {
		this(n, I_1);
	}
	
	/**
	 * Constructor for a rational number.
	 * @param num Numerator
	 * @param den Denominator
	 */
	public BigRational(BigInteger num, BigInteger den) {
		this.num = num;
		if (den.signum() == 0) throw new ArithmeticException("Rational numbers must have non-zero denominator");
		this.den = den;
	}
	
	// Basic Arithmetic -----------------------------------------------------------------------------
	
	/**
	 * Computes the sum of this and the argument.
	 * @param b Argument.
	 * @return sum
	 */
	public BigRational add(BigRational b) {
		BigInteger n = this.num.multiply(b.den).add(b.num.multiply(this.den));
		BigInteger d = this.den.multiply(b.den);
		return new BigRational(n, d);
	}
	
	/**
	 * Computes the subtraction of this and the argument.
	 * @param b Argument.
	 * @return subtraction
	 */
	public BigRational subtract(BigRational b) {
		BigInteger n = this.num.multiply(b.den).subtract(b.num.multiply(this.den));
		BigInteger d = this.den.multiply(b.den);
		return new BigRational(n, d);
	}
	
	/**
	 * Computes the product of this and the argument.
	 * @param b Argument.
	 * @return product
	 */
	public BigRational multiply(BigInteger b) {
		BigInteger n = this.num.multiply(b);
		return new BigRational(n, this.den);
	}
	
	/**
	 * Computes the product of this and the argument.
	 * @param b Argument.
	 * @return product
	 */
	public BigRational multiply(BigRational b) {
		BigInteger n = this.num.multiply(b.num);
		BigInteger d = this.den.multiply(b.den);
		return new BigRational(n, d);
	}
	
	/**
	 * Computes the fraction of this and the argument.
	 * @param b Argument.
	 * @return fraction
	 */
	public BigRational divide(BigInteger b) {
		BigInteger d = this.den.multiply(b);
		return new BigRational(this.num, d);
	}
	
	/**
	 * Computes the fraction of this and the argument.
	 * @param b Argument.
	 * @return fraction
	 */
	public BigRational divide(BigRational b) {
		BigInteger n = this.num.multiply(b.den);
		BigInteger d = this.den.multiply(b.num);
		return new BigRational(n, d);
	}

	/**
	 * @return absolut value
	 */
	public BigRational abs() {
		return new BigRational(this.num.abs(), this.den.abs());
	}
	
	/**
	 * @return copy of this with opposite sign
	 */
	public BigRational negate() {
		return new BigRational(this.num.negate(), this.den); // It is not required to copy the denominator because BigIntegers are inmutable
	}
	
	/**
	 * @return 1/this
	 */
	public BigRational reciprocal() {
		return new BigRational(this.den, this.num);
	}
	
	/**
	 * @return this with gcd of numerator and denominator reduced to 1, and the denominator being positive.
	 */
	public BigRational normalize() {
		if (num.signum() == 0) return new BigRational(I_0);

		BigInteger gcd = num.gcd(den);
		if (gcd!=null && gcd.compareTo(I_1)>0) {
			return (den.signum() < 0) ? new BigRational(num.divide(gcd).negate(), den.divide(gcd).negate()) : new BigRational(num.divide(gcd), den.divide(gcd));
		} else {
			return (den.signum() < 0) ? new BigRational(num.negate(), den.negate()) : this;
		}
	}
	
	public BigRational expandTo(BigInteger newDenominator) {
		BigInteger multiplier = newDenominator.divide(this.den);
		return new BigRational(this.num.multiply(multiplier), this.den.multiply(multiplier));
	}

	// Informations about this number ---------------------------------------------------

	/**
	 * @return The numerator of this number.
	 */
	public BigInteger getNumerator() {
		return this.num;
	}
	
	/**
	 * @return The denominator of this number.
	 */
	public BigInteger getDenominator() {
		return this.den;
	}
	
	/**
	 * @return <0/0/>0 if this is negative/zero/positive.
	 */
	public int signum() {
		return this.num.signum()*this.den.signum();
	}
	
	/**
	 * @return true if this is zero, false otherwise
	 */
	public boolean isZero() {
		return this.num.signum() == 0;
	}

	/**
	 * @return true if this is integral.
	 */
	public boolean isInteger() {
		return this.normalize().den.equals(I_1);
	}
	
	/**
	 * @return the nearest smaller-or-equal integer value.
	 */
	public BigInteger floor() {
		if (this.isZero()) return I_0; // exact; no need for division
		
		BigInteger[] divRem = num.divideAndRemainder(den);
		if (divRem[1].equals(I_0)) return divRem[0]; // exact
		// The result for non-exact divisions is the quotient if this is positive, and the quotient-1 if this is negative.
		return this.signum() > 0 ? divRem[0] : divRem[0].subtract(I_1);
	}

	/**
	 * @return the nearest bigger-or-equal integer value.
	 */
	public BigInteger ceil() {
		if (this.isZero()) return I_0; // exact; no need for division
		
		BigInteger[] divRem = num.divideAndRemainder(den);
		if (divRem[1].equals(I_0)) return divRem[0]; // exact
		// The result for non-exact divisions is the quotient+1 if this is positive, and the quotient if this is negative.
		return this.signum() > 0 ? divRem[0].add(I_1) : divRem[0];
	}
	
	/**
	 * @return the nearest integer value. In case of a tie, the smaller integer value is returned (rounding mode HALF_DOWN).
	 */
	public BigInteger round() {
		if (this.isZero()) return I_0; // exact; no need for division
		
		BigInteger[] divRem = num.divideAndRemainder(den);
		if (divRem[1].equals(I_0)) return divRem[0]; // exact
		BigInteger floor, rem;
		if (this.signum() < 0) {
			if (DEBUG) LOG.debug("num=" + num  + ", den = " + den + ", quot = " + divRem[0] + ", rem = " + divRem[1]);
			floor = divRem[0].subtract(I_1);
			if (DEBUG) Ensure.ensureEquals(den.signum(), -divRem[1].signum());
			rem = den.add(divRem[1]);
			if (DEBUG) Ensure.ensureEquals(num, den.multiply(floor).add(rem));
		} else {
			floor = divRem[0];
			rem = divRem[1];
		}
		return rem.shiftLeft(1).compareTo(den)<=0 ? floor : floor.add(I_1); // we use RoundingMode HALF_DOWN
	}
	
	// Comparison --------------------------------------------------------------------
	
	@Override
	public int compareTo(BigRational other) {
		// simple and correct :)
		BigRational diff = this.subtract(other);
		return diff.signum();
	}
	
	public int compareTo(BigInteger other) {
		return this.compareTo(new BigRational(other));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof BigRational)) return false;
		BigRational other = (BigRational) o;
		return (this.num.multiply(other.den).equals(other.num.multiply(this.den)));
	}
	
	public boolean equals(BigInteger other) {
		if (other==null) return false;
		return (this.num.equals(other.multiply(this.den)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((den == null) ? 0 : den.hashCode());
		result = prime * result + ((num == null) ? 0 : num.hashCode());
		return result;
	}
	
	// Conversion --------------------------------------------------------------------

	/**
	 * @return this as a byte (rounding may be necessary)
	 */
	@Override
	public byte byteValue() {
		return this.toBigDecimal(Scale.valueOf(0)).byteValue();
	}

	/**
	 * @return this as an int (rounding may be necessary)
	 */
	@Override
	public int intValue() {
		return this.toBigDecimal(Scale.valueOf(0)).intValue();
	}

	/**
	 * @return this as a long integer (rounding may be necessary)
	 */
	@Override
	public long longValue() {
		return this.toBigDecimal(Scale.valueOf(0)).longValue();
	}
	
	/**
	 * @return this as a float (rounding may be necessary)
	 */
	@Override
	public float floatValue() {
		return this.toBigDecimal(Precision.valueOf(7)).floatValue();
	}
	
	/**
	 * @return this as a double (rounding may be necessary)
	 */
	@Override
	public double doubleValue() {
		return this.toBigDecimal(Precision.valueOf(15)).doubleValue();
	}
	
	public BigDecimal toBigDecimal(Precision decPrec) {
		return BigDecimalMath.divide(new BigDecimal(this.num), this.den, decPrec);
	}
	
	/**
	 * Converts this to a BigDecimal with decPrec digits precision. 
	 * 
	 * @param decPrec Precision in decimal digits.
	 * @return this as a BigDecimal with the wanted precision.
	 */
	public BigDecimal toBigDecimal(Scale decPrec) {
		return BigDecimalMath.divide(new BigDecimal(this.num), this.den, decPrec);
	}
	
	/**
	 * @return this as a (fractional) string
	 */
	@Override
	public String toString() {
		if (this.den.equals(I_1)) {
			return this.num.toString();
		}
		return this.num + "/" + this.den;
	}
	
	/**
	 * Converts this into a string with the given decimal digits precision.
	 * @param decPrec output precision in decimal digits.
	 * @return this as a string with the wanted precision.
	 */
	public String toString(Scale decPrec) {
		return this.toBigDecimal(decPrec).toString();
	}
}
