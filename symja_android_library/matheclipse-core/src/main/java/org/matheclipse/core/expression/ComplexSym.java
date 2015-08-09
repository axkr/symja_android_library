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
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A symbolic complex implementation
 * 
 */
public class ComplexSym extends ExprImpl implements IComplex {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1489050560741527824L;

	private BigFraction _real;

	private BigFraction _imaginary;

	private transient int fHashValue;
	/**
	 * Holds the factory constructing complex instances.
	 */
	// private static final ObjectFactory<ComplexSym> FACTORY = new
	// ObjectFactory<ComplexSym>() {
	// @Override
	// public ComplexSym create() {
	// if (Config.SERVER_MODE && currentQueue().getSize() >=
	// Config.COMPLEX_MAX_POOL_SIZE) {
	// throw new PoolMemoryExceededException("ComplexImpl",
	// currentQueue().getSize());
	// }
	// return new ComplexSym();
	// }
	// };
	// write this object after the FACTORY initialization
	public final static ComplexSym ZERO = ComplexSym.valueOf(BigFraction.ZERO);

	private ComplexSym() {
	}

	public static ComplexSym valueOf(final BigInteger real, final BigInteger imaginary) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = new BigFraction(real, BigInteger.ONE);
		c._imaginary = new BigFraction(imaginary, BigInteger.ONE);
		return c;
	}

	public static ComplexSym valueOf(final BigInteger real) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = new BigFraction(real, BigInteger.ONE);
		c._imaginary = BigFraction.ZERO;
		return c;
	}

	public static ComplexSym valueOf(final IInteger real, final IInteger imaginary) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = new BigFraction(real.getBigNumerator(), BigInteger.ONE);
		c._imaginary = new BigFraction(imaginary.getBigNumerator(), BigInteger.ONE);
		return c;
	}

	public static ComplexSym valueOf(final IInteger real) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = new BigFraction(real.getBigNumerator(), BigInteger.ONE);
		c._imaginary = BigFraction.ZERO;
		return c;
	}

	public static ComplexSym valueOf(final BigFraction real) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = real;
		c._imaginary = BigFraction.ZERO;
		return c;
	}

	public static ComplexSym valueOf(final BigFraction real, final BigFraction imaginary) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = real;
		c._imaginary = imaginary;
		return c;
	}

	public static ComplexSym valueOf(final long real_numerator, final long real_denominator, final long imag_numerator,
			final long imag_denominator) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = new BigFraction(real_numerator, real_denominator);
		c._imaginary = new BigFraction(imag_numerator, imag_denominator);
		return c;
	}

	public static ComplexSym valueOf(final IFraction real) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = real.getRational();
		c._imaginary = BigFraction.ZERO;
		return c;
	}

	public static ComplexSym valueOf(final IFraction real, final IFraction imaginary) {
		final ComplexSym c = new ComplexSym();// FACTORY.object();
		c._real = real.getRational();
		c._imaginary = imaginary.getRational();
		return c;
	}

	/** {@inheritDoc} */
	@Override
	public IComplex conjugate() {
		return ComplexSym.valueOf(_real, _imaginary.negate());
	}

	/** {@inheritDoc} */
	public IExpr eabs() {
		return F.Sqrt(F.QQ(_real.multiply(_real).add(_imaginary.multiply(_imaginary))));
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		BigFraction temp = _real.multiply(_real).add(_imaginary.multiply(_imaginary));
		return temp.compareTo(BigFraction.ONE);
	}

	public ComplexSym add(final ComplexSym parm1) throws java.lang.ArithmeticException {
		return ComplexSym.valueOf(_real.add(parm1._real), _imaginary.add(parm1._imaginary));
	}

	public IComplex add(final IComplex parm1) {
		return ComplexSym.valueOf(_real.add(parm1.getRealPart()), _imaginary.add(parm1.getImaginaryPart()));
	}

	public Apcomplex apcomplexValue(long precision) {
		Apfloat real = new Apfloat(_real.getNumerator(), precision).divide(new Apfloat(_real.getDenominator(), precision));
		Apfloat imag = new Apfloat(_imaginary.getNumerator(), precision)
				.divide(new Apfloat(_imaginary.getDenominator(), precision));
		return new Apcomplex(real, imag);
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
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
		return F.complexNum(this);
	}

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return imaginary part
	 */
	public BigFraction getImaginaryPart() {
		return _imaginary;
	}

	public ISignedNumber getIm() {
		if (_imaginary.getDenominator().equals(BigInteger.ONE)) {
			return IntegerSym.newInstance(_imaginary.getNumerator());
		}
		return FractionSym.newInstance(_imaginary);
	}

	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public BigFraction getRealPart() {
		return _real;
	}

	public ISignedNumber getRe() {
		if (_real.getDenominator().equals(BigInteger.ONE)) {
			return IntegerSym.newInstance(_real.getNumerator());
		}
		return FractionSym.newInstance(_real);
	}

	@Override
	public final int hashCode() {
		if (fHashValue == 0) {
			fHashValue = _real.hashCode() * 29 + _imaginary.hashCode();
		}
		return fHashValue;
	}

	public int hierarchy() {
		return COMPLEXID;
	}

	@Override
	public boolean isZero() {
		return NumberUtil.isZero(_real) && NumberUtil.isZero(_imaginary);
	}

	public IComplex multiply(final IComplex parm1) {
		return ComplexSym.valueOf(_real.multiply(parm1.getRealPart()).subtract(_imaginary.multiply(parm1.getImaginaryPart())),
				_real.multiply(parm1.getImaginaryPart()).add(parm1.getRealPart().multiply(_imaginary)));
	}

	public IComplex pow(final int parm1) {
		int temp = parm1;

		if ((parm1 == 0) && _real.equals(BigFraction.ZERO) && _imaginary.equals(BigFraction.ZERO)) {
			throw new java.lang.ArithmeticException();
		}

		if (parm1 == 1) {
			return this;
		}

		IComplex res = ComplexSym.valueOf(BigFraction.ONE, BigFraction.ZERO);

		if (parm1 < 0) {
			temp *= -1;
		}

		for (int i = 0; i < temp; i++) {
			res = res.multiply(this);
		}

		if (parm1 < 0) {
			final BigFraction d = res.getRealPart().multiply(res.getRealPart())
					.add(res.getImaginaryPart().multiply(res.getImaginaryPart()));

			return ComplexSym.valueOf(res.getRealPart().divide(d), res.getImaginaryPart().negate().divide(d));
		}

		return res;
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof ComplexSym) {
			return this.add((ComplexSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.add(valueOf((IntegerSym) that));
		}
		if (that instanceof FractionSym) {
			return this.add(valueOf((FractionSym) that));
		}
		return super.plus(that);
	}

	@Override
	public ComplexSym negate() {
		return ComplexSym.valueOf(_real.negate(), _imaginary.negate());
	}

	@Override
	public INumber opposite() {
		return ComplexSym.valueOf(_real.negate(), _imaginary.negate());
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ComplexSym) {
			return multiply((ComplexSym) that);
		}
		if (that instanceof IntegerSym) {
			return this.multiply(valueOf((IntegerSym) that));
		}
		if (that instanceof FractionSym) {
			return this.multiply(valueOf((FractionSym) that));
		}
		return super.times(that);
	}

	@Override
	public IExpr inverse() {
		final BigFraction tmp = (_real.multiply(_real)).add((_imaginary.multiply(_imaginary)));
		return ComplexSym.valueOf(_real.divide(tmp), _imaginary.negate().divide(tmp));
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

	@Override
	public String fullFormString() {
		StringBuffer buf = new StringBuffer("Complex");
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			buf.append('(');
		} else {
			buf.append('[');
		}
		if (_real.getDenominator().equals(BigInteger.ONE)) {
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

		if (_imaginary.getDenominator().equals(BigInteger.ONE)) {
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
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		int real_numerator = NumberUtil.toInt(_real.getNumerator());
		int real_denominator = NumberUtil.toInt(_real.getDenominator());
		int imag_numerator = NumberUtil.toInt(_imaginary.getNumerator());
		int imag_denominator = NumberUtil.toInt(_imaginary.getDenominator());
		if (_real.equals(BigFraction.ZERO)) {
			if (_imaginary.equals(BigFraction.ONE)) {
				return "CI";
			}
			if (_imaginary.equals(BigFraction.MINUS_ONE)) {
				return "CNI";
			}
		}
		return "CC(" + real_numerator + "L," + real_denominator + "L," + imag_numerator + "L," + imag_denominator + "L)";
	}

	public INumber normalize() {
		if (_imaginary.equals(BigFraction.ZERO)) {
			if (_real.getDenominator().equals(BigInteger.ONE)) {
				return F.integer(_real.getNumerator());
			}
			if (_real.getNumerator().equals(BigInteger.ZERO)) {
				return F.C0;
			}
			return FractionSym.newInstance(_real);
		}
		return this;
	}

	public int complexSign() {
		final int i = _real.getNumerator().signum();

		if (i == 0) {
			return _imaginary.getNumerator().signum();
		}

		return i;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
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
	public ISymbol head() {
		return F.Complex;
	}

	/** {@inheritDoc} */
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean equalsInt(int i) {
		return false;
	}

	@Override
	public INumber ceil() throws ArithmeticException {
		return valueOf(NumberUtil.ceiling(_real), NumberUtil.ceiling(_imaginary));
	}

	@Override
	public INumber floor() throws ArithmeticException {
		return valueOf(NumberUtil.floor(_real), NumberUtil.floor(_imaginary));
	}
}