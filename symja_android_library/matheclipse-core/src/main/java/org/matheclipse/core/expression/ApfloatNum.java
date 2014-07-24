package org.matheclipse.core.expression;

import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;

/**
 * <code>INum</code> implementation which wraps a <code>Apfloat</code> value to represent a numeric floating-point number.
 */
public class ApfloatNum extends ExprImpl implements INum {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2500259920655377884L;

	Apfloat fApfloat;

	/**
	 * Create a new instance.
	 * 
	 * @param numerator
	 * @return
	 */
	protected static ApfloatNum newInstance(final double value, int precision) {
		return new ApfloatNum(value, precision);
	}

	protected static ApfloatNum newInstance(final Apfloat value) {
		return new ApfloatNum(value);
	}

	protected static ApfloatNum newInstance(final String value, int precision) {
		return new ApfloatNum(value, precision);
	}

	private ApfloatNum(final double value, int precision) {
		fApfloat = new Apfloat(value, precision);
	}

	private ApfloatNum(final String value, int precision) {
		fApfloat = new Apfloat(value, precision);
	}

	private ApfloatNum(final Apfloat value) {
		fApfloat = value;
	}

	@Override
	public int hierarchy() {
		return DOUBLEID;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger ii) throws ArithmeticException {
		// return F.isNumEqualInteger(fDouble, ii);
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		// return F.isNumIntValue(fDouble);
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return fApfloat.compareTo(Apfloat.ZERO) < 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return fApfloat.compareTo(Apfloat.ZERO) > 0;
	}

	@Override
	public boolean equalsInt(final int i) {
		return fApfloat.intValue() == i;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		// if (engine.isNumericMode() && engine.getNumericPrecision() <= ApfloatNum.DOUBLE_PRECISION) {
		// return Num.valueOf(fApfloat.doubleValue());
		// }
		return null;
	}

	@Override
	public INum add(final INum val) {
		return newInstance(fApfloat.add(((ApfloatNum) val).fApfloat));
	}

	@Override
	public INum multiply(final INum val) {
		return newInstance(fApfloat.multiply(((ApfloatNum) val).fApfloat));
	}

	@Override
	public INum pow(final INum val) {
		return newInstance(ApfloatMath.pow(fApfloat, ((ApfloatNum) val).fApfloat));
	}

	public static ApfloatNum valueOf(final double doubleValue, int precision) {
		return newInstance(doubleValue, precision);
	}

	public static ApfloatNum valueOf(final String strValue, int precision) {
		return newInstance(strValue, precision);
	}

	public static ApfloatNum valueOf(final Apfloat af) {
		return newInstance(af);
	}

	/** {@inheritDoc} */
	@Override
	public ApfloatNum eabs() {
		return newInstance(ApfloatMath.abs(fApfloat));
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		return ApfloatMath.abs(fApfloat).compareTo(Apfloat.ONE);
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof ApfloatNum) {
			return add((ApfloatNum) that);
		}
		return super.plus(that);
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		return newInstance(fApfloat.divide(((ApfloatNum) that).fApfloat));
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		return newInstance(fApfloat.subtract(((ApfloatNum) that).fApfloat));
	}

	/**
	 * @return
	 */
	@Override
	public double doubleValue() {
		return fApfloat.doubleValue();
	}

	public Apfloat apfloatValue() {
		return fApfloat;
	}

	@Override
	public boolean equals(final Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof ApfloatNum) {
			return fApfloat == ((ApfloatNum) arg0).fApfloat;
		}
		return false;
	}

	@Override
	public boolean isSame(IExpr expression, double epsilon) {
		if (expression instanceof ApfloatNum) {
			return fApfloat.equals(((ApfloatNum) expression).fApfloat);
			// return F.isZero(fDouble - ((ApfloatNum) expression).fDouble, epsilon);
		}
		return false;
	}

	public Apfloat exp() {
		return ApfloatMath.exp(fApfloat);
	}

	@Override
	public final int hashCode() {
		return fApfloat.hashCode();
	}

	/**
	 * @return
	 */
	@Override
	public int intValue() {
		return fApfloat.intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		int i = fApfloat.intValue();
		if (i == Integer.MAX_VALUE || i == Integer.MIN_VALUE) {
			throw new ArithmeticException("ApfloatNum:toInt: number out of range");
		}
		return i;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		long l = fApfloat.longValue();
		if (l == Long.MAX_VALUE || l == Long.MIN_VALUE) {
			throw new ArithmeticException("ApfloatNum:toLong: number out of range");
		}
		return l;
	}

	/**
	 * @return
	 */
	public Apfloat log() {
		return ApfloatMath.log(fApfloat);
	}

	/**
	 * @return
	 */
	public long longValue() {
		return fApfloat.longValue();
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ApfloatNum) {
			return newInstance(fApfloat.multiply(((ApfloatNum) that).fApfloat));
		}
		return super.times(that);
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber negate() {
		return newInstance(fApfloat.negate());
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber opposite() {
		return newInstance(fApfloat.negate());
	}

	@Override
	public ISignedNumber inverse() {
		return newInstance(ApfloatMath.inverseRoot(fApfloat, 1));
	}

	/**
	 * @return
	 */
	public Apfloat sqrt() {
		return ApfloatMath.sqrt(fApfloat);
	}

	@Override
	public double getRealPart() {
		double temp = fApfloat.doubleValue();
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	@Override
	public boolean isZero() {
		return fApfloat.equals(Apfloat.ZERO);
		// return fDouble == 0.0;
	}

	@Override
	public boolean isOne() {
		return fApfloat.equals(Apfloat.ONE);
	}

	@Override
	public boolean isMinusOne() {
		return fApfloat.equals(Apfloat.MINUS_ONE);
	}

	@Override
	public IInteger round() {
		Apfloat f = ApfloatMath.round(fApfloat, fApfloat.precision(), RoundingMode.HALF_UP);
		Apint r = ApfloatMath.floor(f);
		return F.integer(r.toBigInteger());
	}

	@Override
	public int sign() {
		return fApfloat.signum();
	}

	/** {@inheritDoc} */
	@Override
	public int complexSign() {
		return sign();
	}

	/** {@inheritDoc} */
	@Override
	public IInteger ceil() {
		return F.integer(ApfloatMath.ceil(fApfloat).toBigInteger());
	}

	/** {@inheritDoc} */
	@Override
	public IInteger floor() {
		return F.integer(ApfloatMath.floor(fApfloat).toBigInteger());
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr obj) {
		if (obj instanceof ApfloatNum) {
			return fApfloat.compareTo(((ApfloatNum) obj).fApfloat);
		}
		return (hierarchy() - (obj).hierarchy());
	}

	@Override
	public boolean isLessThan(ISignedNumber that) {
		return fApfloat.compareTo(((ApfloatNum) that).fApfloat) < 0;
	}

	@Override
	public boolean isGreaterThan(ISignedNumber that) {
		return fApfloat.compareTo(((ApfloatNum) that).fApfloat) > 0;
	}

	@Override
	public ISymbol head() {
		return F.RealHead;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return fApfloat.toString();
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
	public ISignedNumber getIm() {
		return F.CD0;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getRe() {
		return this;
	}
}