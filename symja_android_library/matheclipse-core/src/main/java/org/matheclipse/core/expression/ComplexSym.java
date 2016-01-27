package org.matheclipse.core.expression;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IComplex;
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
 * A symbolic complex number implementation
 * 
 */
public class ComplexSym extends ExprImpl implements IComplex {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1489050560741527824L;

	public final static ComplexSym ZERO = ComplexSym.valueOf(F.C0);

	public static ComplexSym valueOf(final BigFraction real, final BigFraction imaginary) {
		final ComplexSym c = new ComplexSym();
		c._real = AbstractFractionSym.valueOf(real);
		c._imaginary = AbstractFractionSym.valueOf(imaginary);
		return c;
	}

	public static ComplexSym valueOf(final BigInteger real) {
		final ComplexSym c = new ComplexSym();
		c._real = AbstractIntegerSym.valueOf(real);
		c._imaginary = F.C0;
		return c;
	}

	public static ComplexSym valueOf(final BigInteger real, final BigInteger imaginary) {
		final ComplexSym c = new ComplexSym();
		c._real = AbstractIntegerSym.valueOf(real);
		c._imaginary = AbstractIntegerSym.valueOf(imaginary);
		return c;
	}

	public static ComplexSym valueOf(final IFraction real) {
		final ComplexSym c = new ComplexSym();
		c._real = real;
		c._imaginary = F.C0;
		return c;
	}

	public static ComplexSym valueOf(final IRational real) {
		final ComplexSym c = new ComplexSym();
		c._real = real;
		c._imaginary = F.C0;
		return c;
	}

	public static ComplexSym valueOf(final IRational real, final IRational imaginary) {
		final ComplexSym c = new ComplexSym();
		c._real = real;
		c._imaginary = imaginary;
		return c;
	}

	public static ComplexSym valueOf(final long real_numerator, final long real_denominator, final long imag_numerator,
			final long imag_denominator) {
		final ComplexSym c = new ComplexSym();
		c._real = AbstractFractionSym.valueOf(real_numerator, real_denominator);
		c._imaginary = AbstractFractionSym.valueOf(imag_numerator, imag_denominator);
		return c;
	}

	private IRational _real;

	private IRational _imaginary;

	private transient int fHashValue;

	private ComplexSym() {
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

	public ComplexSym add(final ComplexSym parm1) throws java.lang.ArithmeticException {
		return ComplexSym.valueOf(_real.add(parm1._real), _imaginary.add(parm1._imaginary));
	}

	@Override
	public IComplex add(final IComplex parm1) {
		return ComplexSym.valueOf(_real.add(parm1.getRealPart()), _imaginary.add(parm1.getImaginaryPart()));
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	public Apcomplex apcomplexValue(long precision) {
		Apfloat real = new Apfloat(_real.getBigNumerator(), precision)
				.divide(new Apfloat(_real.getBigDenominator(), precision));
		Apfloat imag = new Apfloat(_imaginary.getBigNumerator(), precision)
				.divide(new Apfloat(_imaginary.getBigDenominator(), precision));
		return new Apcomplex(real, imag);
	}

	@Override
	public INumber ceilFraction() throws ArithmeticException {
		return valueOf((IRational) _real.ceilFraction(), (IRational) _imaginary.ceilFraction());
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		IRational temp = _real.multiply(_real).add(_imaginary.multiply(_imaginary));
		return temp.compareTo(F.C1);
	}

	/**
	 * Compares this expression with the specified expression for order. Returns
	 * a negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof ComplexSym) {
			final int cp = _real.compareTo(((ComplexSym) expr)._real);
			if (cp != 0) {
				return cp;
			}
			return _imaginary.compareTo(((ComplexSym) expr)._imaginary);
		}
		return super.compareTo(expr);
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		double nr = _real.getNumerator().doubleValue();
		double dr = _real.getDenominator().doubleValue();
		double ni = _imaginary.getNumerator().doubleValue();
		double di = _imaginary.getDenominator().doubleValue();
		return ComplexNum.valueOf(nr / dr, ni / di);
	}

	@Override
	public int complexSign() {
		final int i = _real.getNumerator().sign();

		if (i == 0) {
			return _imaginary.getNumerator().sign();
		}

		return i;
	}

	/** {@inheritDoc} */
	@Override
	public IComplex conjugate() {
		return ComplexSym.valueOf(_real, _imaginary.negate());
	}

	/** {@inheritDoc} */
	@Override
	public IExpr eabs() {
		return F.Sqrt(_real.multiply(_real).add(_imaginary.multiply(_imaginary)));
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ComplexSym) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			return _real.equals(((ComplexSym) obj)._real) && _imaginary.equals(((ComplexSym) obj)._imaginary);
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
		if (engine.isNumericMode()) {
			return numericNumber();
		}
		final INumber cTemp = normalize();
		return (cTemp == this) ? F.NIL : cTemp;
	}

	@Override
	public INumber floorFraction() throws ArithmeticException {
		return valueOf((IRational) _real.floorFraction(), (IRational) _imaginary.floorFraction());
	}

	@Override
	public String fullFormString() {
		StringBuffer buf = new StringBuffer("Complex");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		if (_real.getDenominator().isOne()) {
			buf.append(_real.getNumerator().toString());
		} else {
			buf.append("Rational");
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				buf.append('(');
			} else {
				buf.append('[');
			}
			buf.append(_real.getNumerator().toString().toString());
			buf.append(',');
			buf.append(_real.getDenominator().toString().toString());
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				buf.append(')');
			} else {
				buf.append(']');
			}
		}
		buf.append(',');

		if (_imaginary.getDenominator().isOne()) {
			buf.append(_imaginary.getNumerator().toString());
		} else {
			buf.append("Rational");
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				buf.append('(');
			} else {
				buf.append('[');
			}
			buf.append(_imaginary.getNumerator().toString().toString());
			buf.append(',');
			buf.append(_imaginary.getDenominator().toString().toString());
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				buf.append(')');
			} else {
				buf.append(']');
			}
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append(')');
		} else {
			buf.append(']');
		}
		return buf.toString();
	}

	@Override
	public ISignedNumber getIm() {
		if (_imaginary.getDenominator().isOne()) {
			return _imaginary.getNumerator();
		}
		return _imaginary;
	}

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return imaginary part
	 */
	@Override
	public IRational getImaginaryPart() {
		return _imaginary;
	}

	@Override
	public ISignedNumber getRe() {
		if (_real.getDenominator().isOne()) {
			return _real.getNumerator();
		}
		return _real;
	}

	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	@Override
	public IRational getRealPart() {
		return _real;
	}

	@Override
	public final int hashCode() {
		if (fHashValue == 0) {
			fHashValue = _real.hashCode() * 29 + _imaginary.hashCode();
		}
		return fHashValue;
	}

	@Override
	public ISymbol head() {
		return F.Complex;
	}

	@Override
	public int hierarchy() {
		return COMPLEXID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false);
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		if (_real.isZero()) {
			if (_imaginary.isOne()) {
				return "CI";
			}
			if (_imaginary.isMinusOne()) {
				return "CNI";
			}
		}

		int real_numerator = NumberUtil.toInt(_real.getBigNumerator());
		int real_denominator = NumberUtil.toInt(_real.getBigDenominator());
		int imag_numerator = NumberUtil.toInt(_imaginary.getBigNumerator());
		int imag_denominator = NumberUtil.toInt(_imaginary.getBigDenominator());
		return "CC(" + real_numerator + "L," + real_denominator + "L," + imag_numerator + "L," + imag_denominator
				+ "L)";
	}

	@Override
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true);
	}

	@Override
	public IExpr inverse() {
		final IRational tmp = (_real.multiply(_real)).add((_imaginary.multiply(_imaginary)));
		return ComplexSym.valueOf(_real.divideBy(tmp), _imaginary.negate().divideBy(tmp));
	}

	@Override
	public boolean isZero() {
		return NumberUtil.isZero(_real) && NumberUtil.isZero(_imaginary);
	}

	@Override
	public IComplex multiply(final IComplex parm1) {
		return ComplexSym.valueOf(
				_real.multiply(parm1.getRealPart()).subtract(_imaginary.multiply(parm1.getImaginaryPart())),
				_real.multiply(parm1.getImaginaryPart()).add(parm1.getRealPart().multiply(_imaginary)));
	}

	@Override
	public ComplexSym negate() {
		return ComplexSym.valueOf(_real.negate(), _imaginary.negate());
	}

	@Override
	public INumber normalize() {
		if (_imaginary.isZero()) {
			if (_real instanceof IFraction) {
				if (_real.getDenominator().isOne()) {
					return _real.getNumerator();
				}
				if (_real.getNumerator().isZero()) {
					return F.C0;
				}
			}
			return _real;
		}
		boolean evaled = false;
		IRational newRe = _real;
		IRational newIm = _imaginary;
		if (_real instanceof IFraction) {
			if (_real.getDenominator().isOne()) {
				newRe = _real.getNumerator();
				evaled = true;
			}
			if (_real.getNumerator().isZero()) {
				newRe = F.C0;
				evaled = true;
			}
		}
		if (_imaginary instanceof IFraction) {
			if (_imaginary.getDenominator().isOne()) {
				newIm = _imaginary.getNumerator();
				evaled = true;
			}
		}
		if (evaled) {
			return valueOf(newRe, newIm);
		}
		return this;
	}

	@Override
	public final INumber numericNumber() {
		return F.complexNum(this);
	}

	@Override
	public INumber opposite() {
		return ComplexSym.valueOf(_real.negate(), _imaginary.negate());
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof ComplexSym) {
			return this.add((ComplexSym) that);
		}
		if (that instanceof IInteger) {
			return this.add(valueOf((IInteger) that));
		}
		if (that instanceof IFraction) {
			return this.add(valueOf((IFraction) that));
		}
		return super.plus(that);
	}

	@Override
	public IComplex pow(final int parm1) {
		int temp = parm1;

		if ((parm1 == 0) && _real.isZero() && _imaginary.isZero()) {
			throw new java.lang.ArithmeticException();
		}

		if (parm1 == 1) {
			return this;
		}

		IComplex res = ComplexSym.valueOf(F.C1, F.C0);

		if (parm1 < 0) {
			temp *= -1;
		}

		for (int i = 0; i < temp; i++) {
			res = res.multiply(this);
		}

		if (parm1 < 0) {
			final IRational d = res.getRealPart().multiply(res.getRealPart())
					.add(res.getImaginaryPart().multiply(res.getImaginaryPart()));

			return ComplexSym.valueOf(res.getRealPart().divideBy(d), res.getImaginaryPart().negate().divideBy(d));
		}

		return res;
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ComplexSym) {
			return multiply((ComplexSym) that);
		}
		if (that instanceof IInteger) {
			return this.multiply(valueOf((IInteger) that));
		}
		if (that instanceof IFraction) {
			return this.multiply(valueOf((IFraction) that));
		}
		return super.times(that);
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get().convertComplex(sb, this, Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
			return sb.toString();
		} catch (Exception e1) {
		}
		// fall back to simple output format
		final StringBuilder tb = new StringBuilder();
		tb.append('(');
		tb.append(_real.toString());
		tb.append(")+I*(");
		tb.append(_imaginary.toString());
		tb.append(')');
		return tb.toString();
	}
	
	public double getImaginary() {
		return _imaginary.doubleValue();
	}

	public double getReal() {
		return _real.doubleValue();
	}
}