package org.matheclipse.core.expression;

import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.fraction.FractionConversionException;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
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
 * IFraction implementation which delegates most of the methods to the Apache commons BigFraction methods
 */
public class FractionSym extends ExprImpl implements IFraction {

	/**
	 * Be cautious with this method, no new internal rational is created
	 * 
	 * @param numerator
	 * @return
	 */
	protected static FractionSym newInstance(final BigFraction rational) {
		FractionSym r = new FractionSym();
		r.fRational = rational;
		return r;
	}

	public static FractionSym valueOf(final BigInteger numerator) {
		FractionSym r = new FractionSym();
		r.fRational = new BigFraction(numerator, BigInteger.ONE);
		return r;
	}

	/**
	 * 
	 * @param rat
	 * @return
	 * 
	 */
	public static FractionSym valueOf(final BigFraction rat) {
		return newInstance(rat);
	}

	public static FractionSym valueOf(final BigInteger numerator, final BigInteger denominator) {
		FractionSym r = new FractionSym();
		r.fRational = new BigFraction(numerator, denominator);
		return r;
	}

	public static FractionSym valueOf(final IInteger numerator, final IInteger denominator) {
		FractionSym r = new FractionSym();
		r.fRational = new BigFraction(numerator.getBigNumerator(), denominator.getBigNumerator());
		return r;
	}

	public static FractionSym valueOf(final long numerator, final long denominator) {
		FractionSym r = new FractionSym();
		r.fRational = new BigFraction(numerator, denominator);
		return r;
	}

	public static FractionSym valueOf(final double value) {
		FractionSym r = new FractionSym();
		try {
			r.fRational = new BigFraction(value, Config.DOUBLE_EPSILON, 200);
		} catch (FractionConversionException e) {
			r.fRational = new BigFraction(value);
		}
		return r;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2396715994276842438L;

	/* package private */BigFraction fRational;

	private transient int fHashValue = 0;

	private FractionSym() {
		fRational = null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return getBigNumerator().equals(BigInteger.ZERO);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return getBigNumerator().equals(BigInteger.ONE) && getBigDenominator().equals(BigInteger.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equalsInt(final int i) {
		return fRational.getNumerator().equals(BigInteger.valueOf(i)) && fRational.getDenominator().equals(BigInteger.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public BigInteger getBigDenominator() {
		return fRational.getDenominator();
	}

	/** {@inheritDoc} */
	@Override
	public BigInteger getBigNumerator() {
		return fRational.getNumerator();
	}

	/** {@inheritDoc} */
	@Override
	public BigFraction getFraction() {
		return fRational;
	}

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	@Override
	public IInteger getDenominator() {
		return IntegerSym.valueOf(fRational.getDenominator());
	}

	/**
	 * Returns the numerator of this Rational.
	 * 
	 * @return numerator
	 */
	@Override
	public IInteger getNumerator() {
		return IntegerSym.valueOf(fRational.getNumerator());
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return FRACTIONID;
	}

	@Override
	public IFraction add(final IFraction parm1) {
		return newInstance(fRational.add(((FractionSym) parm1).fRational));
	}

	@Override
	public IFraction multiply(final IFraction parm1) {
		return newInstance(fRational.multiply(((FractionSym) parm1).fRational));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return (fRational.getNumerator().compareTo(BigInteger.ZERO) == -1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return equals(value);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return (fRational.getNumerator().compareTo(BigInteger.ZERO) == 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return equals(value);
	}

	/** {@inheritDoc} */
	@Override
	public FractionSym eabs() {
		return newInstance(fRational.abs());
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		BigFraction temp = fRational;
		if (fRational.compareTo(BigFraction.ZERO) < 0) {
			temp = temp.negate();
		}
		return temp.compareTo(BigFraction.ONE);
	}

	/**
	 * @param that
	 * @return
	 */
	public BigFraction add(final BigFraction that) {
		return fRational.add(that);
	}

	/**
	 * @param that
	 * @return
	 */
	public BigFraction divide(final BigFraction that) {
		return fRational.divide(that);
	}

	/**
	 * Returns an array of two BigIntegers containing (numerator / denominator) followed by (numerator % denominator).
	 * 
	 * @return
	 */
	public BigInteger[] divideAndRemainder() {
		return fRational.getNumerator().divideAndRemainder(fRational.getDenominator());
	}

	/**
	 * @return
	 */
	@Override
	public double doubleValue() {
		return fRational.doubleValue();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof FractionSym) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			return fRational.equals(((FractionSym) obj).fRational);
		}
		return false;
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

	public final INumber numericNumber() {
		return F.num(this);
	}

	public ISignedNumber normalize() {
		if (getBigDenominator().equals(BigInteger.ONE)) {
			return F.integer(getBigNumerator());
		}
		if (getBigNumerator().equals(BigInteger.ZERO)) {
			return F.C0;
		}
		return this;
	}

	/**
	 * @return
	 */
	public BigInteger getDividend() {
		return fRational.getNumerator();
	}

	/**
	 * @return
	 */
	public BigInteger getDivisor() {
		return fRational.getDenominator();
	}

	@Override
	public int hashCode() {
		if (fHashValue == 0) {
			fHashValue = fRational.hashCode();
		}
		return fHashValue;
	}

	/**
	 * @return
	 */
	public long longValue() {
		return fRational.longValue();
	}

	/**
	 * @param that
	 * @return
	 */
	public BigFraction multiply(final BigFraction that) {
		return fRational.multiply(that);
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber negate() {
		return newInstance(fRational.negate());
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber opposite() {
		return newInstance(fRational.negate());
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof FractionSym) {
			return this.add((FractionSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.add(valueOf(((IntegerSym) that).fInteger));
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).add(ComplexSym.valueOf(this));
		}
		return super.plus(that);
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		if (that instanceof FractionSym) {
			return newInstance(this.divide(((FractionSym) that).fRational));
		}
		if (that instanceof IntegerSym) {
			return this.divideBy(valueOf(((IntegerSym) that).fInteger));
		}
		return Num.valueOf(doubleValue() / that.doubleValue());
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		if (that instanceof FractionSym) {
			return this.add((FractionSym) that.negate());
		}
		if (isZero()) {
			return that.negate();
		}
		if (that instanceof IntegerSym) {
			return this.subtractFrom(valueOf(((IntegerSym) that).fInteger));
		}
		return Num.valueOf(doubleValue() - that.doubleValue());
	}

	/**
	 * Returns this number raised at the specified positive exponent.
	 * 
	 * @param exp
	 *            the positive exponent.
	 * @return <code>this<sup>exp</sup></code>
	 * @throws IllegalArgumentException
	 *             if <code>exp &lt;= 0</code>
	 */
	@Override
	public IFraction pow(int exp) {
		if (exp <= 0)
			throw new IllegalArgumentException("exp: " + exp + " should be a positive number");
		IFraction powSqr = this;
		IFraction result = null;
		while (exp >= 1) { // Iteration.
			if ((exp & 1) == 1) {
				result = (result == null) ? powSqr : result.multiply(powSqr);
			}
			powSqr = powSqr.multiply(powSqr);
			exp >>>= 1;
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber inverse() {
		return newInstance(NumberUtil.inverse(fRational)).normalize();
	}

	/**
	 * @param that
	 * @return
	 */
	public BigFraction subtract(final BigFraction that) {
		return fRational.subtract(that);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof FractionSym) {
			return this.multiply((FractionSym) that).normalize();
		}
		if (that instanceof IntegerSym) {
			return this.multiply(valueOf(((IntegerSym) that).fInteger)).normalize();
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this));
		}
		return super.times(that);
	}

	/** {@inheritDoc} */
	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		int numerator = fRational.getNumerator().intValue();
		int denominator = fRational.getDenominator().intValue();
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
		return "QQ(" + numerator + "L," + denominator + "L)";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		if (fRational.getDenominator().equals(BigInteger.ONE)) {
			return NumberUtil.toInt(fRational.getNumerator());
		}
		if (fRational.getNumerator().equals(BigInteger.ZERO)) {
			return 0;
		}
		throw new ArithmeticException("toInt: denominator != 1");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		if (fRational.getDenominator().equals(BigInteger.ONE)) {
			return NumberUtil.toLong(fRational.getNumerator());
		}
		if (fRational.getNumerator().equals(BigInteger.ZERO)) {
			return 0L;
		}
		throw new ArithmeticException("toLong: denominator != 1");
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get().convertFraction(sb, fRational, Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
			return sb.toString();
		} catch (Exception e1) {
		}
		// fall back to simple output format
		return fRational.getNumerator().toString() + "/" + fRational.getDenominator().toString();
	}

	@Override
	public String fullFormString() {
		StringBuffer buf = new StringBuffer("Rational");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		buf.append(fRational.getNumerator().toString().toString());
		buf.append(',');
		buf.append(fRational.getDenominator().toString().toString());
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append(')');
		} else {
			buf.append(']');
		}
		return buf.toString();
	}

	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof FractionSym) {
			BigFraction arg2 = ((FractionSym) that).getRational();
			return valueOf(fRational.getNumerator().gcd(arg2.getNumerator()),
					IntegerSym.lcm(fRational.getDenominator(), arg2.getDenominator()));
		}
		return super.gcd(that);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.parser.interfaces.IFraction#getRational()
	 */
	@Override
	public BigFraction getRational() {
		return fRational;
	}

	/** {@inheritDoc} */
	@Override
	public int sign() {
		return fRational.getNumerator().signum();
	}

	/** {@inheritDoc} */
	@Override
	public int complexSign() {
		return sign();
	}

	/** {@inheritDoc} */
	@Override
	public IInteger ceil() {
		return IntegerSym.valueOf(NumberUtil.ceiling(fRational));
	}

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

		// add th factors from the numerator part
		result.addAll(num.factorInteger());
		EvalAttributes.sort(result);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger floor() {
		return IntegerSym.valueOf(NumberUtil.floor(fRational));
	}

	/** {@inheritDoc} */
	@Override
	public IInteger round() {
		return IntegerSym.valueOf(NumberUtil.round(fRational, BigDecimal.ROUND_HALF_EVEN));
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof FractionSym) {
			return fRational.compareTo(((FractionSym) expr).fRational);
		}
		if (expr instanceof IntegerSym) {
			return fRational.compareTo(new BigFraction(((IntegerSym) expr).fInteger, BigInteger.ONE));
		}
		if (expr instanceof Num) {
			double d = fRational.doubleValue() - ((Num) expr).getRealPart();
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
	public boolean isLessThan(ISignedNumber obj) {
		if (obj instanceof FractionSym) {
			return fRational.compareTo(((FractionSym) obj).fRational) < 0;
		}
		if (obj instanceof IntegerSym) {
			return fRational.compareTo(new BigFraction(((IntegerSym) obj).fInteger, BigInteger.ONE)) < 0;
		}
		return fRational.doubleValue() < obj.doubleValue();
	}

	@Override
	public boolean isGreaterThan(ISignedNumber obj) {
		if (obj instanceof FractionSym) {
			return fRational.compareTo(((FractionSym) obj).fRational) > 0;
		}
		if (obj instanceof IntegerSym) {
			return fRational.compareTo(new BigFraction(((IntegerSym) obj).fInteger, BigInteger.ONE)) > 0;
		}
		return fRational.doubleValue() > obj.doubleValue();
	}

	@Override
	public ISymbol head() {
		return F.Rational;
	}

	/** {@inheritDoc} */
	@Override
	public IFraction abs() {
		return eabs();
	}

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

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getIm() {
		return F.C0;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getRe() {
		return this;
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(fRational.getNumerator(), fRational.getDenominator(), precision);
	}

	@Override
	public Num numValue() {
		return Num.valueOf(fRational.doubleValue());
	}

	public Apcomplex apcomplexValue(long precision) {
		Apfloat real = new Apfloat(fRational.getNumerator(), precision).divide(new Apfloat(fRational.getDenominator(), precision));
		return new Apcomplex(real);
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		double nr = fRational.getNumerator().doubleValue();
		double dr = fRational.getDenominator().doubleValue();
		return ComplexNum.valueOf(nr / dr);
	}

	private Object writeReplace() throws ObjectStreamException {
		ExprID temp = F.GLOBAL_IDS_MAP.get(this);
		if (temp != null) {
			return temp;
		}
		return this;
	}
}