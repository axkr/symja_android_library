package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.math.DoubleMath;

/**
 * <code>INum</code> implementation which wraps a <code>double</code> value to represent a numeric floating-point
 * number.
 */
public class Num implements INum {
	/**
	 * 
	 */
	private static final long serialVersionUID = 188084692735007429L;

	/**
	 * Returns a {@code Num} instance representing the specified {@code double} value. If a new {@code Num} instance is
	 * not required, this method should generally be used in preference to the constructor {@link #Num(double)}, as this
	 * method is likely to yield significantly better space and time performance by caching frequently requested values.
	 *
	 * @param d
	 *            a double value.
	 * @return a {@code Double} instance representing {@code d}.
	 */
	public static Num valueOf(final double d) {
		int i = (int) d;
		if (i > (-2) && i < 2) {
			switch (i) {
			case -1:
				if (d == (-1.0)) {
					return F.CND1;
				}
				break;
			case 0:
				if (d == 0.0) {
					return F.CD0;
				}
				break;
			case 1:
				if (d == 1.0) {
					return F.CD1;
				}
				break;
			}
		}
		return new Num(d);
	}

	/**
	 * @param chars
	 * @return
	 */
	public static double valueOf(final String chars) {
		return Double.parseDouble(chars);
	}

	double fDouble;

	protected Num() {
		fDouble = 0.0;
	}

	Num(final double value) {
		fDouble = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	@Override
	public INum add(final INum val) {
		if (val instanceof ApfloatNum) {
			return ApfloatNum.valueOf(fDouble, ((ApfloatNum) val).precision()).add(val);
		}
		return valueOf(fDouble + val.getRealPart());
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(new Apfloat(new BigDecimal(fDouble), precision));
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(fDouble, precision);
	}

	/** {@inheritDoc} */
	@Override
	public IInteger ceilFraction() {
		return F.integer(NumberUtil.toLong(Math.ceil(fDouble)));
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		double temp = Math.abs(fDouble);
		return Double.compare(temp, 1.0);
	}

	/**
	 * @param that
	 * @return
	 */
	public int compareTo(final double that) {
		return Double.compare(fDouble, that);
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof Num) {
			return Double.compare(fDouble, ((Num) expr).fDouble);
		}
		if (expr.isReal()) {
			return Double.compare(fDouble, ((ISignedNumber) expr).doubleValue());
		}
		return INum.super.compareTo(expr);
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		return ComplexNum.valueOf(doubleValue(), 0.0);
	}

	/** {@inheritDoc} */
	@Override
	public int complexSign() {
		return sign();
	}

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param that
	 * @return
	 */
	public double divide(final double that) {
		return fDouble / that;
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		return Num.valueOf(doubleValue() / that.doubleValue());
	}

	/**
	 * @return
	 */
	@Override
	public double doubleValue() {
		return fDouble;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr dec() {
		return valueOf(fDouble - 1.0);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr inc() {
		return valueOf(fDouble + 1.0);
	}

	/** {@inheritDoc} */
	@Override
	public Num abs() {
		return valueOf(Math.abs(fDouble));
	}

	@Override
	public boolean equals(final Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof Num) {
			return fDouble == ((Num) arg0).fDouble;
		}
		return false;
	}

	@Override
	public boolean equalsInt(final int i) {
		return F.isNumIntValue(fDouble, i);
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (fDouble == Double.POSITIVE_INFINITY) {
			return F.CInfinity;
		}
		if (fDouble == Double.NEGATIVE_INFINITY) {
			return F.CNInfinity;
		}
		if (Double.isNaN(fDouble)) {
			return F.Indeterminate;
		}
		if (engine.isNumericMode() && engine.isApfloat()) {
			return ApfloatNum.valueOf(fDouble, engine.getNumericPrecision());
		}
		return F.NIL;
	}

	@Override
	public ISignedNumber evalReal() {
		return this;
	}

	@Override
	public INumber evalNumber() {
		return this;
	}

	public double exp() {
		return Math.exp(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber fractionalPart() {
		return F.num(getRealPart() % 1);
	}

	/** {@inheritDoc} */
	@Override
	public IInteger floorFraction() {
		return F.integer(NumberUtil.toLong(Math.floor(fDouble)));
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber im() {
		return F.CD0;
	}

	@Override
	public double imDoubleValue() {
		return 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber re() {
		return this;
	}

	@Override
	public double reDoubleValue() {
		return doubleValue();
	}

	@Override
	public double getRealPart() {
		double temp = fDouble;
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	@Override
	public final int hashCode() {
		return Double.hashCode(fDouble);
	}

	@Override
	public ISymbol head() {
		return F.Real;
	}

	@Override
	public int hierarchy() {
		return DOUBLEID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false, false, false);
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix,
			boolean noSymbolPrefix) {
		String prefix = usePrefix ? "F." : "";
		return prefix + "num(" + fDouble + ")";
	}

	@Override
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true, false, false);
	}

	/**
	 * @return
	 */
	@Override
	public int intValue() {
		return (int) fDouble;
	}

	@Override
	public ISignedNumber inverse() {
		if (isOne()) {
			return this;
		}
		return valueOf(1 / fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isE() {
		return F.isZero(fDouble - Math.E);
	}

	@Override
	public boolean isGT(ISignedNumber that) {
		return fDouble > that.doubleValue();
	}

	/**
	 * @return
	 */
	public boolean isInfinite() {
		return Double.isInfinite(fDouble);
	}

	@Override
	public boolean isLT(ISignedNumber that) {
		return fDouble < that.doubleValue();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMinusOne() {
		return F.isZero(fDouble + 1.0);
	}

	/**
	 * @return
	 */
	public boolean isNaN() {
		return Double.isNaN(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return fDouble < 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
		return F.isNumEqualInteger(fDouble, value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return F.isNumEqualRational(fDouble, value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		return F.isNumIntValue(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return F.isZero(fDouble - 1.0);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPi() {
		return F.isZero(fDouble - Math.PI);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return fDouble > 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return F.isZero(fDouble - value.doubleValue());
	}

	@Override
	public boolean isSame(IExpr expression, double epsilon) {
		if (expression instanceof Num) {
			return F.isZero(fDouble - ((Num) expression).fDouble, epsilon);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return F.isZero(fDouble, Config.DOUBLE_TOLERANCE);
	}

	@Override
	public long leafCountSimplify() {
		return 2;
	}

	public double log() {
		return Math.log(fDouble);
	}

	/**
	 * @return
	 */
	public long longValue() {
		return (long) fDouble;
	}

	/**
	 * @param that
	 * @return
	 */
	public double minus(final double that) {
		return fDouble - that;
	}

	@Override
	public INum multiply(final INum val) {
		if (val instanceof ApfloatNum) {
			return ApfloatNum.valueOf(fDouble, ((ApfloatNum) val).precision()).multiply(val);
		}
		return valueOf(fDouble * val.getRealPart());
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber negate() {
		return valueOf(-fDouble);
	}

	@Override
	public Num numValue() {
		return this;
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber opposite() {
		return valueOf(-fDouble);
	}

	/**
	 * @param that
	 * @return
	 */
	public double plus(final double that) {
		return fDouble + that;
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof ApfloatNum) {
			return add(ApfloatNum.valueOf(fDouble, ((ApfloatNum) that).fApfloat.precision()));
		}
		if (that instanceof Num) {
			return valueOf(fDouble + ((Num) that).fDouble);
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fDouble, ((ApcomplexNum) that).fApcomplex.precision()).add((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			return ComplexNum.valueOf(fDouble).add((ComplexNum) that);
		}
		return INum.super.plus(that);
	}

	/**
	 * @param that
	 * @return
	 */
	public double pow(final double that) {
		return Math.pow(fDouble, that);
	}

	/**
	 * @param exp
	 * @return
	 */
	public double pow(final int exp) {
		return Math.pow(fDouble, exp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.parser.interfaces.IDouble#pow(org.matheclipse.parser. interfaces .IDouble)
	 */
	@Override
	public INum pow(final INum val) {
		return valueOf(Math.pow(fDouble, val.getRealPart()));
	}

	@Override
	public long precision() throws ApfloatRuntimeException {
		return 15L;
	}

	@Override
	public IInteger round() {
		return F.integer(DoubleMath.roundToBigInteger(fDouble, RoundingMode.HALF_EVEN));
		// return F.integer(NumberUtil.toLong(Math.rint(fDouble)));
	}

	@Override
	public int sign() {
		return (int) Math.signum(fDouble);
	}

	/**
	 * @return
	 */
	// public double sqrt() {
	// return Math.sqrt(fDouble);
	// }

	public IExpr sqrt() {
		return valueOf(Math.sqrt(fDouble));
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		return valueOf(doubleValue() - that.doubleValue());
	}

	/**
	 * @param that
	 * @return
	 */
	public double times(final double that) {
		return fDouble * that;
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ApfloatNum) {
			return multiply(ApfloatNum.valueOf(fDouble, ((ApfloatNum) that).fApfloat.precision()));
		}
		if (that instanceof Num) {
			return valueOf(fDouble * ((Num) that).fDouble);
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fDouble, ((ApcomplexNum) that).fApcomplex.precision())
					.multiply((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			return ComplexNum.valueOf(fDouble).multiply((ComplexNum) that);
		}
		return INum.super.times(that);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		return NumberUtil.toInt(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public int toIntDefault(int defaultValue) {
		try {
			return NumberUtil.toInt(fDouble);
		} catch (ArithmeticException ae) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		return NumberUtil.toLong(fDouble);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Double.toString(fDouble);
	}
}