package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;

/**
 * IFraction implementation which uses methods of the Apache
 * <code>org.apache.commons.math4.fraction.BigFraction</code> methods.
 * 
 * @see AbstractFractionSym
 * @see FractionSym
 */
public class BigFractionSym extends AbstractFractionSym {

	/**
	 * 
	 */
	private static final long serialVersionUID = -553051997353641162L;

	transient int fHashValue;

	BigFraction fFraction;

	BigFractionSym(BigFraction fraction) {
		fFraction = fraction;
	}

	/**
	 * <p>
	 * Construct a rational from two BigIntegers.
	 * </p>
	 * <b>Note:</b> the constructor is package private and does not normalize.
	 * Use the static constructor valueOf instead.
	 * 
	 * @param nom
	 *            Numerator
	 * @param denom
	 *            Denominator
	 */
	BigFractionSym(BigInteger nom, BigInteger denom) {
		fFraction = new BigFraction(nom, denom);
	}

	@Override
	public AbstractFractionSym abs() {
		return new BigFractionSym(fFraction.abs());
	}

	/**
	 * Return a new rational representing <code>this + other</code>.
	 * 
	 * @param other
	 *            Rational to add.
	 * @return Sum of <code>this</code> and <code>other</code>.
	 */
	@Override
	public AbstractFractionSym add(AbstractFractionSym other) {
		if (other.isZero())
			return this;

		BigInteger tdenom = getBigDenominator();
		BigInteger odenom = other.getBigDenominator();
		if (tdenom.equals(odenom)) {
			return valueOf(getBigNumerator().add(other.getBigNumerator()), tdenom);
		}
		BigInteger gcd = tdenom.gcd(odenom);
		BigInteger tdenomgcd = tdenom.divide(gcd);
		BigInteger odenomgcd = odenom.divide(gcd);
		BigInteger newnum = getBigNumerator().multiply(odenomgcd).add(other.getBigNumerator().multiply(tdenomgcd));
		BigInteger newdenom = tdenom.multiply(odenomgcd);
		return valueOf(newnum, newdenom);
	}

	@Override
	public IRational add(IRational parm1) {
		if (parm1.isZero()) {
			return this;
		}
		if (parm1 instanceof AbstractFractionSym) {
			return add((AbstractFractionSym) parm1);
		}
		AbstractIntegerSym p1 = (AbstractIntegerSym) parm1;
		BigInteger newnum = getBigNumerator().add(getBigDenominator().multiply(p1.getBigNumerator()));
		return valueOf(newnum, getBigDenominator());
	}

	@Override
	public IInteger ceil() {
		if (isIntegral()) {
			return AbstractIntegerSym.valueOf(getBigNumerator());
		}
		BigInteger div = getBigNumerator().divide(getBigDenominator());
		if (getBigNumerator().signum() > 0)
			div = div.add(BigInteger.ONE);
		return AbstractIntegerSym.valueOf(div);
	}

	// public int compareFraction(final int numerator, final int denominator) {
	// BigInteger num = fFraction.getNumerator();
	// BigInteger den = fFraction.getDenominator();
	// if (num.bitLength() <= 31) {
	// int temp = num.intValue();
	// return temp > value ? 1 : temp == value ? 0 : -1;
	// }
	// return num.signum();
	// }

	/**
	 * Return a new rational representing the smallest integral rational not
	 * smaller than <code>this</code>.
	 * 
	 * @return Next bigger integer of <code>this</code>.
	 */
	@Override
	public AbstractFractionSym ceilFraction() {
		if (isIntegral()) {
			return this;
		}
		BigInteger div = getBigNumerator().divide(getBigDenominator());
		if (getBigNumerator().signum() > 0)
			div = div.add(BigInteger.ONE);
		return AbstractFractionSym.valueOf(div, BigInteger.ONE);
	}

	@Override
	public int compareAbsValueToOne() {
		BigFraction temp = fFraction;
		if (fFraction.compareTo(BigFraction.ZERO) < 0) {
			temp = temp.negate();
		}
		return temp.compareTo(BigFraction.ONE);
	}

	@Override
	public int compareInt(final int value) {
		BigInteger dOn = getBigDenominator().multiply(BigInteger.valueOf(value));
		return getBigNumerator().compareTo(dOn);
	}

	@Override
	public int compareTo(IExpr expr) {
		if (expr instanceof AbstractFractionSym) {
			BigInteger valthis = getBigNumerator().multiply(((AbstractFractionSym) expr).getBigDenominator());
			BigInteger valo = ((AbstractFractionSym) expr).getBigNumerator().multiply(getBigDenominator());
			return valthis.compareTo(valo);
		}
		if (expr instanceof AbstractIntegerSym) {
			return fFraction.compareTo(new BigFraction(((AbstractIntegerSym) expr).getBigNumerator(), BigInteger.ONE));
		}
		if (expr instanceof Num) {
			double d = fFraction.doubleValue() - ((Num) expr).getRealPart();
			if (d < 0.0) {
				return -1;
			}
			if (d > 0.0) {
				return 1;
			}
		}
		return super.compareTo(expr);
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		double nr = getBigNumerator().doubleValue();
		double dr = getBigDenominator().doubleValue();
		return ComplexNum.valueOf(nr / dr);
	}

	/**
	 * Return a new rational representing <code>this / other</code>.
	 * 
	 * @param other
	 *            Rational to divide.
	 * @return Quotient of <code>this</code> and <code>other</code>.
	 */
	@Override
	public AbstractFractionSym div(AbstractFractionSym other) {
		if (other.isOne()) {
			return this;
		}
		if (other.isMinusOne()) {
			return this.negate();
		}
		BigInteger denom = getBigDenominator().multiply(other.getBigNumerator());
		BigInteger nom = getBigNumerator().multiply(other.getBigDenominator());
		// +-inf : -c = -+inf
		if (denom.equals(BigInteger.ZERO) && other.getBigNumerator().signum() == -1)
			nom = nom.negate();
		return valueOf(nom, denom);
	}

	@Override
	public IInteger[] divideAndRemainder() {
		IInteger[] result = new IInteger[2];
		BigInteger[] intResult = getBigNumerator().divideAndRemainder(getBigDenominator());
		result[0] = AbstractIntegerSym.valueOf(intResult[0]);
		result[1] = AbstractIntegerSym.valueOf(intResult[1]);
		return result;
	}

	@Override
	public double doubleValue() {
		return fFraction.doubleValue();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FractionSym) {
			final FractionSym r = (FractionSym) o;
			return equalsFraction(r.fNumerator, r.fDenominator);
			// return NumberUtil.isInt(fFraction.getNumerator(), r.fNumerator)
			// && NumberUtil.isInt(fFraction.getDenominator(), r.fDenominator);
		}
		if (o instanceof BigFractionSym) {
			BigFractionSym r = (BigFractionSym) o;
			return fFraction.equals(r.fFraction);
		}
		return false;
	}

	@Override
	public final boolean equalsFraction(final int numerator, final int denominator) {
		BigInteger num = fFraction.getNumerator();
		BigInteger den = fFraction.getDenominator();
		return num.intValue() == numerator && den.intValue() == denominator && num.bitLength() <= 31
				&& den.bitLength() <= 31;
	}

	// public IInteger ceil() {
	//
	// }
	//
	// public IInteger floor() {
	//
	// }

	@Override
	public boolean equalsInt(final int numerator) {
		BigInteger num = fFraction.getNumerator();
		return num.intValue() == numerator && fFraction.getDenominator().equals(BigInteger.ONE)
				&& num.bitLength() <= 31;
	}

	@Override
	public IInteger floor() {
		if (isIntegral()) {
			return AbstractIntegerSym.valueOf(getBigNumerator());
		}
		BigInteger div = getBigNumerator().divide(getBigDenominator());
		if (getBigNumerator().signum() < 0) {
			div = div.subtract(BigInteger.ONE);
		}
		return AbstractIntegerSym.valueOf(div);
	}

	/**
	 * Return a new rational representing the biggest integral rational not
	 * bigger than <code>this</code>.
	 * 
	 * @return Next smaller integer of <code>this</code>.
	 */
	@Override
	public AbstractFractionSym floorFraction() {
		if (isIntegral()) {
			return this;
		}
		BigInteger div = getBigNumerator().divide(getBigDenominator());
		if (getBigNumerator().signum() < 0) {
			div = div.subtract(BigInteger.ONE);
		}
		return AbstractFractionSym.valueOf(div, BigInteger.ONE);
	}

	/**
	 * Returns the fractional part of the rational, i.e. the number
	 * this.sub(this.floor()).
	 * 
	 * @return Next smaller integer of <code>this</code>.
	 */
	public AbstractFractionSym frac() {
		if (isIntegral()) {
			return AbstractFractionSym.ZERO;
		}
		BigInteger den = fFraction.getDenominator();
		BigInteger newnum = fFraction.getNumerator().mod(den);
		if (newnum.signum() < 0) {
			newnum = newnum.add(den);
		}
		return AbstractFractionSym.valueOf(newnum, den);
	}

	@Override
	public String fullFormString() {
		StringBuffer buf = new StringBuffer("Rational");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		buf.append(fFraction.getNumerator().toString());
		buf.append(',');
		buf.append(fFraction.getDenominator().toString());
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append(')');
		} else {
			buf.append(']');
		}
		return buf.toString();
	}

	/**
	 * Compute the gcd of two rationals (this and other). The gcd is the
	 * rational number, such that dividing this and other with the gcd will
	 * yield two co-prime integers.
	 * 
	 * @param other
	 *            the second rational argument.
	 * @return the gcd of this and other.
	 */
	public AbstractFractionSym gcd(FractionSym other) {
		if (other.isZero()) {
			return this;
		}
		BigInteger tdenom = this.getBigDenominator();
		BigInteger odenom = other.getBigDenominator();
		BigInteger gcddenom = tdenom.gcd(odenom);
		BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
		BigInteger num = getBigNumerator().gcd(other.getBigNumerator());
		return AbstractFractionSym.valueOf(num, denom);
	}

	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof AbstractFractionSym) {
			BigFraction arg2 = ((AbstractFractionSym) that).getRational();
			return valueOf(fFraction.getNumerator().gcd(arg2.getNumerator()),
					AbstractIntegerSym.lcm(fFraction.getDenominator(), arg2.getDenominator()));
		}
		return super.gcd(that);
	}

	@Override
	public BigInteger getBigDenominator() {
		return fFraction.getDenominator();
	}

	@Override
	public BigInteger getBigNumerator() {
		return fFraction.getNumerator();
	}

	/** {@inheritDoc} */
	@Override
	public BigFraction getFraction() {
		return fFraction;
	}

	/** {@inheritDoc} */
	@Override
	public BigFraction getRational() {
		return fFraction;
	}

	@Override
	public int hashCode() {
		if (fHashValue == 0) {
			fHashValue = fFraction.hashCode();
		}
		return fHashValue;
	}

	/**
	 * Return a new rational representing <code>this / other</code>.
	 * 
	 * @param other
	 *            Rational to divide.
	 * @return Quotient of <code>this</code> and <code>other</code>.
	 */
	public AbstractFractionSym idiv(AbstractFractionSym other) {
		BigInteger num = getBigDenominator().multiply(other.getBigNumerator());
		BigInteger denom = getBigNumerator().multiply(other.getBigDenominator());

		if (denom.equals(BigInteger.ZERO) && getBigNumerator().signum() == -1) {
			num = num.negate();
		}
		return valueOf(num, denom);
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		try {
			int numerator = NumberUtil.toInt(fFraction.getNumerator());
			int denominator = NumberUtil.toInt(fFraction.getDenominator());
			if (numerator == 1) {
				switch (denominator) {
				case 2:
					return "C1D2";
				case 3:
					return "C1D3";
				case 4:
					return "C1D4";
				}
			}
			if (numerator == -1) {
				switch (denominator) {
				case 2:
					return "CN1D2";
				case 3:
					return "CN1D3";
				case 4:
					return "CN1D4";
				}
			}
		} catch (Exception e) {

		}
		return "QQ(" + fFraction.getNumerator().toString() + "L," + fFraction.getDenominator().toString() + "L)";
	}

	/**
	 * Returns a new rational representing the inverse of <code>this</code>.
	 * 
	 * @return Inverse of <code>this</code>.
	 */
	@Override
	public AbstractFractionSym inverse() {
		return new BigFractionSym(fFraction.reciprocal());
	}

	/**
	 * Check whether this rational represents an integral value (i.e. the
	 * denominator equals 1).
	 * 
	 * @return <code>true</code> iff value is integral.
	 */
	@Override
	public boolean isIntegral() {
		return fFraction.getDenominator().equals(BigInteger.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMinusOne() {
		return fFraction.equals(BigFraction.MINUS_ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return (fFraction.getNumerator().compareTo(BigInteger.ZERO) == -1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return fFraction.equals(BigFraction.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return (fFraction.getNumerator().compareTo(BigInteger.ZERO) == 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return fFraction.equals(BigFraction.ZERO);
	}

	/**
	 * Return a new rational representing <code>this * other</code>.
	 * 
	 * @param other
	 *            big integer to multiply.
	 * @return Product of <code>this</code> and <code>other</code>.
	 */
	public AbstractFractionSym mul(BigInteger other) {
		if (other.bitLength() < 2) {
			int oint = other.intValue();
			if (oint == 1)
				return this;
			if (oint == -1)
				return this.negate();
			if (oint == 0)
				return AbstractFractionSym.ZERO;
		}
		return valueOf(getBigNumerator().multiply(other), getBigDenominator());
	}

	@Override
	public IRational multiply(IRational parm1) {
		if (parm1.isOne()) {
			return this;
		}
		if (parm1.isZero()) {
			return parm1;
		}
		if (parm1.isMinusOne()) {
			return this.negate();
		}
		if (parm1 instanceof AbstractFractionSym) {
			return mul((AbstractFractionSym) parm1);
		}
		AbstractIntegerSym p1 = (AbstractIntegerSym) parm1;
		BigInteger newnum = getBigNumerator().multiply(p1.getBigNumerator());
		return valueOf(newnum, getBigDenominator());
	}

	/**
	 * Returns a new rational equal to <code>-this</code>.
	 * 
	 * @return <code>-this</code>
	 */
	@Override
	public AbstractFractionSym negate() {
		return new BigFractionSym(fFraction.negate());
	}

	@Override
	public INumber normalize() {
		if (getBigDenominator().equals(BigInteger.ONE)) {
			return F.integer(getBigNumerator());
		}
		if (getBigNumerator().equals(BigInteger.ZERO)) {
			return F.C0;
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger round() {
		return AbstractIntegerSym.valueOf(NumberUtil.round(fFraction, BigDecimal.ROUND_HALF_EVEN));
	}

	/** {@inheritDoc} */
	@Override
	public int sign() {
		return fFraction.getNumerator().signum();
	}

	/** {@inheritDoc} */
	@Override
	public int toInt() throws ArithmeticException {
		if (getBigDenominator().equals(BigInteger.ONE)) {
			return NumberUtil.toInt(getBigNumerator());
		}
		if (getBigNumerator().equals(BigInteger.ZERO)) {
			return 0;
		}
		throw new ArithmeticException("toInt: denominator != 1");
	}

	/** {@inheritDoc} */
	@Override
	public long toLong() throws ArithmeticException {
		if (getBigDenominator().equals(BigInteger.ONE)) {
			return NumberUtil.toLong(getBigNumerator());
		}
		if (getBigNumerator().equals(BigInteger.ZERO)) {
			return 0L;
		}
		throw new ArithmeticException("toLong: denominator != 1");
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get().convertFraction(sb, this, Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
			return sb.toString();
		} catch (Exception e1) {
		}
		// fall back to simple output format
		return fFraction.getNumerator().toString() + "/" + fFraction.getDenominator().toString();
	}
}
