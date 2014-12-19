package org.matheclipse.core.expression;

import java.math.BigInteger;
import java.math.RoundingMode;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

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
	public static ApfloatNum valueOf(final double value, long precision) {
		return new ApfloatNum(value, precision);
	}

	public static ApfloatNum valueOf(final Apfloat value) {
		return new ApfloatNum(value);
	}

	public static ApfloatNum valueOf(final BigInteger numerator, long precision) {
		return new ApfloatNum(numerator, precision);
	}

	public static ApfloatNum valueOf(final BigInteger numerator, final BigInteger denominator, long precision) {
		Apfloat n = new Apfloat(numerator, precision);
		Apfloat d = new Apfloat(denominator, precision);
		return new ApfloatNum(n.divide(d));
	}

	public static ApfloatNum valueOf(final String value, int precision) {
		return new ApfloatNum(value, precision);
	}

	private ApfloatNum(final double value, long precision) {
		fApfloat = new Apfloat(value, precision);
	}

	private ApfloatNum(final String value, long precision) {
		fApfloat = new Apfloat(value, precision);
	}

	private ApfloatNum(final BigInteger value, long precision) {
		fApfloat = new Apfloat(value, precision);
	}

	private ApfloatNum(final Apfloat value) {
		fApfloat = value;
	}

	@Override
	public int hierarchy() {
		// TODO check this ID
		return DOUBLEID;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger ii) throws ArithmeticException {
		return fApfloat.frac().equals(new Apint(ii.getBigNumerator()));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		return fApfloat.frac().equals(Apfloat.ZERO);
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
		return valueOf(fApfloat.add(((ApfloatNum) val).fApfloat));
	}

	@Override
	public INum multiply(final INum val) {
		return valueOf(fApfloat.multiply(((ApfloatNum) val).fApfloat));
	}

	@Override
	public INum pow(final INum val) {
		return valueOf(ApfloatMath.pow(fApfloat, ((ApfloatNum) val).fApfloat));
	}

	public long precision() throws ApfloatRuntimeException {
		return fApfloat.precision();
	}

	/** {@inheritDoc} */
	@Override
	public ApfloatNum eabs() {
		return valueOf(ApfloatMath.abs(fApfloat));
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
		if (that instanceof Num) {
			return add(ApfloatNum.valueOf(((Num) that).getRealPart(), fApfloat.precision()));
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fApfloat, Apfloat.ZERO).add((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			ComplexNum cn = (ComplexNum) that;
			return ApcomplexNum.valueOf(fApfloat, Apfloat.ZERO).add(
					ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart(), fApfloat.precision()));
		}
		return super.plus(that);
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		return valueOf(fApfloat.divide(((ApfloatNum) that).fApfloat));
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		return valueOf(fApfloat.subtract(((ApfloatNum) that).fApfloat));
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
			return multiply((ApfloatNum) that);
		}
		if (that instanceof Num) {
			return multiply(ApfloatNum.valueOf(((Num) that).getRealPart(), fApfloat.precision()));
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fApfloat, Apfloat.ZERO).multiply((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			ComplexNum cn = (ComplexNum) that;
			return ApcomplexNum.valueOf(fApfloat, Apfloat.ZERO).multiply(
					ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart(), fApfloat.precision()));
		}
		return super.times(that);
	}

	/**
	 * @return
	 */
	@Override
	public ApfloatNum negate() {
		return valueOf(fApfloat.negate());
	}

	/**
	 * @return
	 */
	@Override
	public ApfloatNum opposite() {
		return valueOf(fApfloat.negate());
	}

	@Override
	public ApfloatNum inverse() {
		return valueOf(ApfloatMath.inverseRoot(fApfloat, 1));
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

	/** {@inheritDoc} */
	@Override
	public boolean isE() {
		return fApfloat.equals(ApfloatMath.exp(Apfloat.ONE));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMinusOne() {
		return fApfloat.equals(Apfloat.MINUS_ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return fApfloat.equals(Apfloat.ONE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPi() {
		return fApfloat.equals(ApfloatMath.pi(fApfloat.precision()));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return fApfloat.equals(value.apfloatNumValue(fApfloat.precision()));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return fApfloat.equals(Apfloat.ZERO);
		// return fDouble == 0.0;
	}

	@Override
	public IInteger round() {
		Apfloat f = ApfloatMath.round(fApfloat, 1, RoundingMode.HALF_UP);
		return F.integer(ApfloatMath.floor(f).toBigInteger());
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
	public int compareTo(final IExpr expr) {
		if (expr instanceof ApfloatNum) {
			return fApfloat.compareTo(((ApfloatNum) expr).fApfloat);
		}
		return super.compareTo(expr);
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

	/** {@inheritDoc} */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	@Override
	public long accept(IVisitorLong visitor) {
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

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return this;
	}

	@Override
	public Num numValue() {
		return Num.valueOf(doubleValue());
	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(fApfloat);
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(fApfloat, Apfloat.ZERO);
	}

	@Override
	public ComplexNum complexNumValue() {
		return ComplexNum.valueOf(fApfloat.doubleValue());
	}
}