package org.matheclipse.core.expression;

import java.math.BigInteger;

import org.apache.commons.math4.exception.ZeroException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.fraction.FractionConversionException;
import org.apache.commons.math4.util.ArithmeticUtils;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * Abstract base class for FractionSym and BigFractionSym
 * 
 * @see FractionSym
 * @see BigFractionSym
 *
 */
public abstract class AbstractFractionSym extends ExprImpl implements IFraction {

	public final static FractionSym ZERO = new FractionSym(0, 1);

	public final static FractionSym ONE = new FractionSym(1, 1);

	public final static FractionSym MONE = new FractionSym(-1, 1);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8743141041586314213L;

	public static BigInteger gcd(BigInteger i1, BigInteger i2) {
		if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE))
			return BigInteger.ONE;
		int l1 = i1.bitLength();
		int l2 = i2.bitLength();
		if (l1 < 31 && l2 < 31) {
			return BigInteger.valueOf(ArithmeticUtils.gcd(i1.intValue(), i2.intValue()));
		} else if (l1 < 63 && l2 < 63) {
			return BigInteger.valueOf(ArithmeticUtils.gcd(i1.longValue(), i2.longValue()));
		} else {
			return i1.gcd(i2);
		}
	}

	public static AbstractFractionSym valueOf(BigFraction fraction) {
		return valueOf(fraction.getNumerator(), fraction.getDenominator());
	}

	public static AbstractFractionSym valueOf(BigInteger num) {
		return valueOf(num, BigInteger.ONE);
	}

	/**
	 * Construct a rational from two BigIntegers. Use this method to create a
	 * rational number if the numerator or denominator may be to big to fit in
	 * an Java int. This method normalizes the rational number.
	 * 
	 * @param num
	 *            Numerator
	 * @param den
	 *            Denominator
	 */
	public static AbstractFractionSym valueOf(BigInteger num, BigInteger den) {
		if (BigInteger.ZERO.equals(den)) {
			throw new ZeroException(LocalizedFormats.ZERO_DENOMINATOR);
		}
		int cp = den.signum();
		if (cp < 0) {
			num = num.negate();
			den = den.negate();
		}
		if (!BigInteger.ONE.equals(den)) {
			BigInteger norm = gcd(num, den).abs();
			if (!norm.equals(BigInteger.ONE)) {
				num = num.divide(norm);
				den = den.divide(norm);
			}
		}
		if (den.bitLength() <= 31 && num.bitLength() <= 31) {
			return valueOf(num.intValue(), den.intValue());
		} else {
			return new BigFractionSym(num, den);
		}
	}

	public static AbstractFractionSym valueOf(final double value) {
		BigFraction fraction;
		try {
			fraction = new BigFraction(value, Config.DOUBLE_EPSILON, 200);
			return new BigFractionSym(fraction);
		} catch (FractionConversionException e) {
			fraction = new BigFraction(value);
		}
		return new BigFractionSym(fraction);
	}

	public static AbstractFractionSym valueOf(IInteger numerator) {
		if (numerator instanceof IntegerSym) {
			return valueOf(((IntegerSym) numerator).fIntValue, 1);
		}
		return valueOf(numerator.getBigNumerator());
	}

	public static AbstractFractionSym valueOf(IInteger numerator, IInteger denominator) {
		if (numerator instanceof IntegerSym && denominator instanceof IntegerSym) {
			return valueOf(((IntegerSym) numerator).fIntValue, ((IntegerSym) denominator).fIntValue);
		}
		return valueOf(numerator.getBigNumerator(), denominator.getBigNumerator());
	}
	
	/**
	 * Construct a rational from two longs. Use this method to create a rational
	 * number. This method normalizes the rational number and may return a
	 * previously created one. This method does not work if called with value
	 * Long.MIN_VALUE.
	 * 
	 * @param newnum
	 *            Numerator.
	 * @param newdenom
	 *            Denominator.
	 */
	public static AbstractFractionSym valueOf(long newnum, long newdenom) {
		if (newdenom != 1) {
			if (newdenom == 0) {
				throw new ZeroException(LocalizedFormats.ZERO_DENOMINATOR);
			}
			long gcd2 = Math.abs(ArithmeticUtils.gcd(newnum, newdenom));
			if (newdenom < 0) {
				gcd2 = -gcd2;
			}
			newnum /= gcd2;
			newdenom /= gcd2;
		}

		if (newdenom == 1) {
			if (newnum == 0) {
				return ZERO;
			}
			if (newnum == 1) {
				return ONE;
			}
			if (newnum == -1) {
				return MONE;
			}
		}

		if (Integer.MIN_VALUE <= newnum && newnum <= Integer.MAX_VALUE && newdenom <= Integer.MAX_VALUE) {
			return new FractionSym((int) newnum, (int) newdenom);
		}
		return new BigFractionSym(BigInteger.valueOf(newnum), BigInteger.valueOf(newdenom));
	}

	/**
	 * Compute the absolute of this rational.
	 * 
	 * @return Rational that is equal to the absolute value of this rational.
	 */
	@Override
	public abstract AbstractFractionSym abs();

	/** {@inheritDoc} */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	public abstract AbstractFractionSym add(AbstractFractionSym other);

	@Override
	public IFraction add(IFraction parm1) {
		return add((AbstractFractionSym) parm1);
	}

	/**
	 * Returns <code>this+(fac1*fac2)</code>.
	 */
	public AbstractFractionSym addmul(AbstractFractionSym fac1, AbstractFractionSym fac2) {
		return add(fac1.mul(fac2));
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	public Apcomplex apcomplexValue(long precision) {
		Apfloat real = new Apfloat(getBigNumerator(), precision).divide(new Apfloat(getBigDenominator(), precision));
		return new Apcomplex(real);
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(getBigNumerator(), getBigDenominator(), precision);
	}

	@Override
	public abstract AbstractFractionSym ceilFraction();

	/** {@inheritDoc} */
	@Override
	public int complexSign() {
		return sign();
	}

	public abstract AbstractFractionSym div(AbstractFractionSym other);

	/** {@inheritDoc} */
	@Override
	public IRational divideBy(IRational that) {
		if (that instanceof AbstractFractionSym) {
			return this.div((AbstractFractionSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.div(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
		}
		return this.div(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		if (that instanceof IRational) {
			return this.divideBy((IRational) that);
		}
		return Num.valueOf(doubleValue() / that.doubleValue());
	}

	@Override
	public IExpr eabs() {
		return abs();
	}

	@Override
	public boolean equalsInt(int i) {
		return getBigNumerator().equals(BigInteger.valueOf(i)) && getBigDenominator().equals(BigInteger.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (engine.isNumericMode()) {
			return numericNumber();
		}
		final INumber cTemp = normalize();
		return (cTemp == this) ? null : cTemp;
	}

	@Override
	/** {@inheritDoc} */
	public IAST factorInteger() {
		IInteger num = getNumerator();
		IInteger den = getDenominator();
		IAST result = den.factorInteger();

		// negate the exponents of the denominator part
		for (int i = 1; i < result.size(); i++) {
			IAST list = (IAST) result.get(i);
			list.set(2, ((ISignedNumber) list.arg2()).negate());
		}

		// add the factors from the numerator part
		result.addAll(num.factorInteger());
		EvalAttributes.sort(result);
		return result;
	}

	@Override
	public abstract AbstractFractionSym floorFraction();

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	@Override
	public IInteger getDenominator() {
		return AbstractIntegerSym.valueOf(getBigDenominator());
	}

	@Override
	public ISignedNumber getIm() {
		return F.C0;
	}

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	@Override
	public IInteger getNumerator() {
		return AbstractIntegerSym.valueOf(getBigNumerator());
	}

	@Override
	public ISignedNumber getRe() {
		return this;
	}

	@Override
	public ISymbol head() {
		return F.Rational;
	}

	@Override
	public int hierarchy() {
		return FRACTIONID;
	}

	/**
	 * Returns a new rational representing the inverse of <code>this</code>.
	 * 
	 * @return Inverse of <code>this</code>.
	 */
	@Override
	public abstract AbstractFractionSym inverse();

	@Override
	public boolean isGreaterThan(ISignedNumber obj) {
		if (obj instanceof FractionSym) {
			return compareTo((obj)) > 0;
		}
		if (obj instanceof AbstractIntegerSym) {
			return compareTo(
					AbstractFractionSym.valueOf(((AbstractIntegerSym) obj).getBigNumerator(), BigInteger.ONE)) > 0;
		}
		return doubleValue() > obj.doubleValue();
	}

	/**
	 * Check whether this rational represents an integral value (i.e. the
	 * denominator equals 1).
	 * 
	 * @return <code>true</code> iff value is integral.
	 */
	public abstract boolean isIntegral();

	@Override
	public boolean isLessThan(ISignedNumber obj) {
		if (obj instanceof FractionSym) {
			return compareTo((obj)) < 0;
		}
		if (obj instanceof AbstractIntegerSym) {
			return compareTo(
					AbstractFractionSym.valueOf(((AbstractIntegerSym) obj).getBigNumerator(), BigInteger.ONE)) < 0;
		}
		return doubleValue() < obj.doubleValue();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return equals(value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return equals(value);
	}

	/**
	 * Return a new rational representing <code>this * other</code>.
	 * 
	 * @param other
	 *            Rational to multiply.
	 * @return Product of <code>this</code> and <code>other</code>.
	 */
	public AbstractFractionSym mul(AbstractFractionSym other) {
		if (other.isOne()) {
			return this;
		}
		if (other.isZero()) {
			return other;
		}
		if (other.isMinusOne()) {
			return this.negate();
		}

		BigInteger newnum = getBigNumerator().multiply(other.getBigNumerator());
		BigInteger newdenom = getBigDenominator().multiply(other.getBigDenominator());
		return valueOf(newnum, newdenom);
	}

	@Override
	public IFraction multiply(IFraction parm1) {
		return mul((AbstractFractionSym) parm1);
	}

	/**
	 * Returns a new rational equal to <code>-this</code>.
	 * 
	 * @return <code>-this</code>.
	 */
	@Override
	public abstract AbstractFractionSym negate();

	@Override
	public abstract INumber normalize();

	@Override
	public INumber numericNumber() {
		return F.num(this);
	}

	@Override
	public Num numValue() {
		return Num.valueOf(doubleValue());
	}

	@Override
	public ISignedNumber opposite() {
		return negate();
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof AbstractFractionSym) {
			return this.add((AbstractFractionSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.add(valueOf(((IntegerSym) that).fIntValue));
		}
		if (that instanceof BigIntegerSym) {
			return this.add(valueOf(((BigIntegerSym) that).fBigIntValue));
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).add(ComplexSym.valueOf(this));
		}
		return super.plus(that);
	}

	/** {@inheritDoc} */
	@Override
	public final AbstractFractionSym pow(final long n) throws ArithmeticException {
		if (n == 0L) {
			if (!this.isZero()) {
				return AbstractFractionSym.ONE;
			}
			throw new ArithmeticException("Indeterminate: 0^0");
		} else if (n == 1L) {
			return this;
		}
		long exp = n;
		if (n < 0) {
			exp *= -1;
		}
		int b2pow = 0;

		while ((exp & 1) == 0) {
			b2pow++;
			exp >>= 1;
		}

		AbstractFractionSym r = this;
		AbstractFractionSym x = r;

		while ((exp >>= 1) > 0) {
			x = (AbstractFractionSym) x.multiply(x);
			if ((exp & 1) != 0) {
				r = (AbstractFractionSym) r.multiply(x);
			}
		}

		while (b2pow-- > 0) {
			r = (AbstractFractionSym) r.multiply(r);
		}
		if (n < 0) {
			return r.inverse();
		}
		return r;
	}

	/** {@inheritDoc} */
	@Override
	public int sign() {
		return getBigNumerator().signum();
	}

	/**
	 * Return a new rational representing <code>this - other</code>.
	 * 
	 * @param other
	 *            Rational to subtract.
	 * @return Difference of <code>this</code> and <code>other</code>.
	 */
	public AbstractFractionSym sub(AbstractFractionSym other) {
		return add(other.negate());
	}

	/**
	 * Returns <code>(this-s)/d</code>.
	 */
	public AbstractFractionSym subdiv(AbstractFractionSym s, FractionSym d) {
		return sub(s).div(d);
	}

	/** {@inheritDoc} */
	@Override
	public IRational subtract(IRational that) {
		if (isZero()) {
			return that.negate();
		}
		return this.add(that.negate());
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		if (that instanceof IRational) {
			return this.add((IRational) that.negate());
		}
		return Num.valueOf(doubleValue() - that.doubleValue());
	}

	/** {@inheritDoc} */
	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof AbstractFractionSym) {
			return this.multiply((AbstractFractionSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.multiply(valueOf(((IntegerSym) that).fIntValue));
		}
		if (that instanceof BigIntegerSym) {
			return this.multiply(valueOf(((BigIntegerSym) that).fBigIntValue));
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this));
		}
		return super.times(that);
	}
}
