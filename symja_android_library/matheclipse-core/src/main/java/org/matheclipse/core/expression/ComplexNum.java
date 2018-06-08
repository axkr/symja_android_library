package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.F.num;

import java.math.BigDecimal;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * <code>IComplexNum</code> implementation which wraps a <code>org.apache.commons.math3.complex.Complex</code> value to
 * represent a numeric complex floating-point number.
 */
public class ComplexNum implements IComplexNum {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6033055105824482264L;

	/** The square root of -1. A number representing "0.0 + 1.0i" */
	public static final ComplexNum I = valueOf(0.0, 1.0);

	public static final ComplexNum INF = valueOf(Complex.INF);

	/** A complex number representing "NaN + NaNi" */
	public static final ComplexNum NaN = valueOf(Complex.NaN);

	/** The square root of -1. A number representing "0.0 - 1.0i" */
	public static final ComplexNum NI = valueOf(0.0, -1.0);

	/** A complex number representing "-1.0 + 0.0i" */
	public static final ComplexNum MINUS_ONE = valueOf(-1.0, 0.0);

	/** A complex number representing "1.0 + 0.0i" */
	public static final ComplexNum ONE = valueOf(1.0, 0.0);

	/** A complex number representing "0.0 + 0.0i" */
	public static final ComplexNum ZERO = valueOf(0.0, 0.0);

	/**
	 * Return the absolute value of this complex number. Returns {@code NaN} if either real or imaginary part is
	 * {@code NaN} and {@code Double.POSITIVE_INFINITY} if neither part is {@code NaN}, but at least one part is
	 * infinite.
	 *
	 * @return the absolute value.
	 */
	public static double dabs(Complex c) {
		if (c.isNaN()) {
			return Double.NaN;
		}

		if (c.isInfinite()) {
			return Double.POSITIVE_INFINITY;
		}

		if (Math.abs(c.getReal()) < Math.abs(c.getImaginary())) {
			if (F.isZero(c.getImaginary())) {
				return Math.abs(c.getReal());
			}
			final double q = c.getReal() / c.getImaginary();
			return Math.abs(c.getImaginary()) * Math.sqrt(1 + q * q);
		} else {
			if (F.isZero(c.getReal())) {
				return Math.abs(c.getImaginary());
			}
			final double q = c.getImaginary() / c.getReal();
			return Math.abs(c.getReal()) * Math.sqrt(1 + q * q);
		}
	}

	/**
	 * Be cautious with this method, no new internal couble complex is created
	 * 
	 * @param value
	 *            a double complex numeric value
	 * @return
	 */
	protected static ComplexNum newInstance(final Complex value) {
		ComplexNum d = new ComplexNum(0.0, 0.0);
		d.fComplex = value;
		return d;
	}

	public static ComplexNum valueOf(final Complex c) {
		return newInstance(c);
	}

	public static ComplexNum valueOf(final double real) {
		return newInstance(new Complex(real, 0.0));
	}

	public static ComplexNum valueOf(final double real, final double imaginary) {
		return newInstance(new Complex(real, imaginary));
	}

	public static ComplexNum valueOf(final INum d) {
		return newInstance(new Complex(d.getRealPart(), 0.0));
	}

	Complex fComplex;

	private ComplexNum(final double r, final double i) {
		fComplex = new Complex(r, i);
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

	/**
	 * Returns a {@code Complex} whose value is {@code (this + addend)}. Uses the definitional formula
	 * <p>
	 * {@code (a + bi) + (c + di) = (a+c) + (b+d)i}
	 * </p>
	 * If either {@code this} or {@code addend} has a {@code NaN} value in either part, {@link #NaN} is returned;
	 * otherwise {@code Infinite} and {@code NaN} values are returned in the parts of the result according to the rules
	 * for {@link java.lang.Double} arithmetic.
	 *
	 * @param addend
	 *            Value to be added to this {@code Complex}.
	 * @return {@code this + addend}.
	 * @throws NullArgumentException
	 *             if {@code addend} is {@code null}.
	 */
	public Complex add(final Complex addend) {
		return fComplex.add(addend);
	}

	/**
	 * Returns a {@code ComplexNum} whose value is {@code (this + addend)}. Uses the definitional formula
	 * <p>
	 * {@code (a + bi) + (c + di) = (a+c) + (b+d)i}
	 * </p>
	 * If either {@code this} or {@code addend} has a {@code NaN} value in either part, {@link #NaN} is returned;
	 * otherwise {@code Infinite} and {@code NaN} values are returned in the parts of the result according to the rules
	 * for {@link java.lang.Double} arithmetic.
	 *
	 * @param addend
	 *            Value to be added to this {@code Complex}.
	 * @return {@code this + addend}.
	 * @throws NullArgumentException
	 *             if {@code addend} is {@code null}.
	 */
	public ComplexNum add(final ComplexNum that) {
		return newInstance(fComplex.add(that.fComplex));
	}

	@Override
	public IComplexNum add(final IComplexNum val) {
		if (val instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fComplex.getReal(), fComplex.getImaginary(), ((ApcomplexNum) val).precision())
					.add(val);
		}
		return newInstance(fComplex.add(((ComplexNum) val).fComplex));
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));

	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(new Apfloat(new BigDecimal(fComplex.getReal()), precision),
				new Apfloat(new BigDecimal(fComplex.getImaginary()), precision));
	}

	@Override
	public INumber ceilFraction() throws ArithmeticException {
		return F.complex(NumberUtil.toLong(Math.ceil(fComplex.getReal())),
				NumberUtil.toLong(Math.ceil(fComplex.getImaginary())));
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		double temp = dabs();
		return Double.compare(temp, 1.0);
	}

	public int compareTo(final Complex that) {
		if (fComplex.getReal() < that.getReal()) {
			return -1;
		}
		if (fComplex.getReal() > that.getReal()) {
			return 1;
		}
		long l1 = Double.doubleToLongBits(fComplex.getReal());
		long l2 = Double.doubleToLongBits(that.getReal());
		if (l1 < l2) {
			return -1;
		}
		if (l1 > l2) {
			return 1;
		}
		if (fComplex.getImaginary() < that.getImaginary()) {
			return -1;
		}
		if (fComplex.getImaginary() > that.getImaginary()) {
			return 1;
		}
		l1 = Double.doubleToLongBits(fComplex.getImaginary());
		l2 = Double.doubleToLongBits(that.getImaginary());
		if (l1 < l2) {
			return -1;
		}
		if (l1 > l2) {
			return 1;
		}
		return 0;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof ComplexNum) {
			return compareTo(((ComplexNum) expr).fComplex);
		}
		return IComplexNum.super.compareTo(expr);
	}

	@Override
	public ComplexNum complexNumValue() {
		return this;
	}

	@Override
	public int complexSign() {
		final int i = (int) Math.signum(fComplex.getReal());
		if (i == 0) {
			return (int) Math.signum(fComplex.getImaginary());
		}
		return i;
	}

	public Complex complexValue() {
		return fComplex;
	}

	/** {@inheritDoc} */
	@Override
	public IComplexNum conjugate() {
		return newInstance(fComplex.conjugate());
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

	/** {@inheritDoc} */
	@Override
	public IExpr dec() {
		return add(MINUS_ONE);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr inc() {
		return add(ONE);
	}

	/** {@inheritDoc} */
	@Override
	public double dabs() {
		return dabs(fComplex);
	}

	/**
	 * @param that
	 * @return
	 */
	public Complex divide(final Complex that) {
		return fComplex.divide(that);
	}

	public ComplexNum divide(final ComplexNum that) throws ArithmeticException {
		return newInstance(fComplex.divide(that.fComplex));
	}

	@Override
	public Num abs() {
		return Num.valueOf(dabs());
	}

	@Override
	public IExpr complexArg() {
		return num(Math.atan2(imDoubleValue(), reDoubleValue()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ComplexNum) {
			return fComplex.equals(((ComplexNum) obj).fComplex);
		}
		return false;
	}

	@Override
	public boolean equalsInt(int i) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (fComplex.isInfinite()) {
			return F.CComplexInfinity;
		}
		if (fComplex.isNaN()) {
			return F.Indeterminate;
		}
		if (engine.isNumericMode() && engine.isApfloat()) {
			return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart(), engine.getNumericPrecision());
		}
		// if (F.isZero(getImaginaryPart())) {
		// return F.num(getRealPart());
		// }
		return F.NIL;
	}

	@Override
	public INumber evalNumber() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public INumber fractionalPart() {
		return F.complexNum(getRealPart() % 1, getImaginaryPart() % 1);
	}

	@Override
	public INumber floorFraction() throws ArithmeticException {
		return F.complex(NumberUtil.toLong(Math.floor(fComplex.getReal())),
				NumberUtil.toLong(Math.floor(fComplex.getImaginary())));
	}

	public Complex getCMComplex() {
		return new Complex(fComplex.getReal(), fComplex.getImaginary());
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber im() {
		return F.num(getImaginaryPart());
	}

	@Override
	public double imDoubleValue() {
		return fComplex.getImaginary();
	}

	/**
	 * @return
	 */
	@Override
	public double getImaginaryPart() {
		double temp = fComplex.getImaginary();
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber re() {
		return F.num(getRealPart());
	}

	public IExpr sqrt() {
		return valueOf(fComplex.sqrt());
	}

	@Override
	public double reDoubleValue() {
		return fComplex.getReal();
	}

	@Override
	public double getRealPart() {
		double temp = fComplex.getReal();
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	@Override
	public final int hashCode() {
		return fComplex.hashCode();
	}

	@Override
	public ISymbol head() {
		return F.Complex;
	}

	@Override
	public int hierarchy() {
		return DOUBLECOMPLEXID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false, false, false);
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix,
			boolean noSymbolPrefix) {
		String prefix = usePrefix ? "F." : "";
		return prefix + "complexNum(" + fComplex.getReal() + "," + fComplex.getImaginary() + ")";
	}

	@Override
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true, false, false);
	}

	@Override
	public IExpr inverse() {
		final double tmp = (fComplex.getReal() * fComplex.getReal())
				+ (fComplex.getImaginary() * fComplex.getImaginary());
		return valueOf(fComplex.getReal() / tmp, -fComplex.getImaginary() / tmp);
	}

	@Override
	public boolean isImaginaryUnit() {
		return equals(I);
	}

	/**
	 * @return
	 */
	public boolean isInfinite() {
		return fComplex.isInfinite();
	}

	/**
	 * @return
	 */
	public boolean isNaN() {
		return fComplex.isNaN();
	}

	@Override
	public boolean isNegativeImaginaryUnit() {
		return equals(NI);
	}

	@Override
	public boolean isSame(IExpr expression, double epsilon) {
		if (expression instanceof ComplexNum) {
			return F.isZero(fComplex.getReal() - ((ComplexNum) expression).fComplex.getReal(), epsilon)
					&& F.isZero(fComplex.getImaginary() - ((ComplexNum) expression).fComplex.getImaginary(), epsilon);
		}
		return false;
	}

	@Override
	public boolean isZero() {
		return F.isZero(fComplex.getReal()) && F.isZero(fComplex.getImaginary());
	}

	/**
	 * Returns a {@code ComplexNum} whose value is {@code this * factor}. Implements preliminary checks for {@code NaN}
	 * and infinity followed by the definitional formula:
	 * <p>
	 * {@code (a + bi)(c + di) = (ac - bd) + (ad + bc)i}
	 * </p>
	 * Returns {@link #NaN} if either {@code this} or {@code factor} has one or more {@code NaN} parts.
	 * <p>
	 * Returns {@link #INF} if neither {@code this} nor {@code factor} has one or more {@code NaN} parts and if either
	 * {@code this} or {@code factor} has one or more infinite parts (same result is returned regardless of the sign of
	 * the components).
	 * </p>
	 * <p>
	 * Returns finite values in components of the result per the definitional formula in all remaining cases.
	 * </p>
	 *
	 * @param factor
	 *            value to be multiplied by this {@code ComplexNum}.
	 * @return {@code this * factor}.
	 * @throws NullArgumentException
	 *             if {@code factor} is {@code null}.
	 */
	public ComplexNum multiply(final ComplexNum factor) {
		return newInstance(fComplex.multiply(factor.fComplex));
	}

	@Override
	public IComplexNum multiply(final IComplexNum val) {
		if (val instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fComplex.getReal(), fComplex.getImaginary(), ((ApcomplexNum) val).precision())
					.multiply(val);
		}
		return newInstance(fComplex.multiply(((ComplexNum) val).fComplex));
	}

	/**
	 * Returns a {@code ComplexNum} whose value is {@code (-this)}. Returns {@code NaN} if either real or imaginary part
	 * of this Complex number is {@code Double.NaN}.
	 *
	 * @return {@code -this}.
	 */
	@Override
	public ComplexNum negate() {
		return newInstance(fComplex.negate());
	}

	/**
	 * @return
	 */
	@Override
	public INumber opposite() {
		return newInstance(fComplex.negate());
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof ComplexNum) {
			return newInstance(fComplex.add(((ComplexNum) that).fComplex));
		}
		if (that instanceof ApcomplexNum) {
			ApcomplexNum acn = (ApcomplexNum) that;
			return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart(), acn.fApcomplex.precision()).add(acn);
		}
		if (that instanceof ApfloatNum) {
			ApfloatNum afn = (ApfloatNum) that;
			return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart(), afn.fApfloat.precision())
					.add(ApcomplexNum.valueOf(afn.fApfloat, Apcomplex.ZERO));
		}
		if (that instanceof Num) {
			return add(ComplexNum.valueOf(((Num) that).getRealPart()));
		}
		return IComplexNum.super.plus(that);
	}

	@Override
	public IComplexNum pow(final IComplexNum val) {
		if (Complex.equals(fComplex, Complex.ZERO, Config.DOUBLE_EPSILON)) {
			ISignedNumber sn = val.re();
			if (sn.isNegative()) {
				EvalEngine.get().printMessage("Infinite expression 0^(negative number)");
				return INF;
			}
			if (sn.isZero()) {
				EvalEngine.get().printMessage("Infinite expression 0^0.");
				return NaN;
			}
			return ZERO;
		}
		return newInstance(fComplex.pow(((ComplexNum) val).fComplex));
	}

	@Override
	public long precision() throws ApfloatRuntimeException {
		return 15;
	}

	/**
	 * Returns a {@code Complex} whose value is {@code (this - subtrahend)}. Uses the definitional formula
	 * <p>
	 * {@code (a + bi) - (c + di) = (a-c) + (b-d)i}
	 * </p>
	 * If either {@code this} or {@code subtrahend} has a {@code NaN]} value in either part, {@link #NaN} is returned;
	 * otherwise infinite and {@code NaN} values are returned in the parts of the result according to the rules for
	 * {@link java.lang.Double} arithmetic.
	 *
	 * @param subtrahend
	 *            value to be subtracted from this {@code Complex}.
	 * @return {@code this - subtrahend}.
	 * @throws NullArgumentException
	 *             if {@code subtrahend} is {@code null}.
	 */
	public Complex subtract(final Complex subtrahend) {
		return fComplex.subtract(subtrahend);
	}

	/**
	 * Returns a {@code ComplexNum} whose value is {@code (this - subtrahend)}. Uses the definitional formula
	 * <p>
	 * {@code (a + bi) - (c + di) = (a-c) + (b-d)i}
	 * </p>
	 * If either {@code this} or {@code subtrahend} has a {@code NaN]} value in either part, {@link #NaN} is returned;
	 * otherwise infinite and {@code NaN} values are returned in the parts of the result according to the rules for
	 * {@link java.lang.Double} arithmetic.
	 *
	 * @param subtrahend
	 *            value to be subtracted from this {@code ComplexNum}.
	 * @return {@code this - subtrahend}.
	 * @throws NullArgumentException
	 *             if {@code subtrahend} is {@code null}.
	 */
	public ComplexNum subtract(final ComplexNum subtrahend) {
		return newInstance(fComplex.subtract(subtrahend.fComplex));
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ComplexNum) {
			return newInstance(fComplex.multiply(((ComplexNum) that).fComplex));
		}
		if (that instanceof ApcomplexNum) {
			ApcomplexNum acn = (ApcomplexNum) that;
			return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart(), acn.fApcomplex.precision()).multiply(acn);
		}
		if (that instanceof ApfloatNum) {
			ApfloatNum afn = (ApfloatNum) that;
			return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart(), afn.fApfloat.precision())
					.multiply(ApcomplexNum.valueOf(afn.fApfloat, Apcomplex.ZERO));
		}
		if (that instanceof Num) {
			return multiply(ComplexNum.valueOf(((Num) that).getRealPart()));
		}
		return IComplexNum.super.times(that);
	}

	@Override
	public String toString() {
		// try {
		// StringBuilder sb = new StringBuilder();
		// OutputFormFactory.get().convertDoubleComplex(sb, this, Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
		// return sb.toString();
		// } catch (Exception e1) {
		// fall back to simple output format
		return fComplex.toString();
		// }
	}

}