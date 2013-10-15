package org.matheclipse.core.expression;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;

/**
 * A concrete complex implementation
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

	public IComplex conjugate() {
		return ComplexSym.valueOf(_real, _imaginary.negate());
	}

	public IExpr eabs() {
		return F.Sqrt(FractionSym.valueOf(_real.multiply(_real).add(_imaginary.multiply(_imaginary))));
	}

	public ComplexSym add(final ComplexSym parm1) throws java.lang.ArithmeticException {
		return ComplexSym.valueOf(_real.add(parm1._real), _imaginary.add(parm1._imaginary));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.matheclipse.parser.interfaces.IComplex#add(org.matheclipse.parser. interfaces.IComplex)
	 */
	public IComplex add(final IComplex parm1) {
		return ComplexSym.valueOf(_real.add(parm1.getRealPart()), _imaginary.add(parm1.getImaginaryPart()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (hashCode() != obj.hashCode()) {
			return false;
		}
		if (obj instanceof ComplexSym) {
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
			return F.complexNum(this);
		}
		final INumber cTemp = normalize();
		if (cTemp == this) {
			return null;
		}
		return cTemp;
	}

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return imaginary part
	 */
	public BigFraction getImaginaryPart() {
		return _imaginary;
	}

	public IExpr getIm() {
		return FractionSym.valueOf(_imaginary);
	}

	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public BigFraction getRealPart() {
		return _real;
	}

	public IExpr getRe() {
		return FractionSym.valueOf(_real);
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
		return super.plus(that);
	}

	@Override
	public IExpr opposite() {
		return ComplexSym.valueOf(_real.negate(), _imaginary.negate());
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof ComplexSym) {
			return multiply((ComplexSym) that);
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
			OutputFormFactory.get().convertComplex(sb, this, Integer.MIN_VALUE);
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
			if (_real.getDenominator().equals(BigInteger.ZERO)) {
				return IntegerSym.valueOf(_real.getNumerator());
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

	public static void main(final String[] args) {
		final ComplexSym c = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE);
		final IExpr e = c.times(c);
		System.out.println(e);
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	public int compareTo(final IExpr obj) {
		if (obj instanceof ComplexSym) {
			final int cp = _real.compareTo(((ComplexSym) obj)._real);
			if (cp != 0) {
				return cp;
			}
			return _imaginary.compareTo(((ComplexSym) obj)._imaginary);
		}
		return (hierarchy() - (obj).hierarchy());
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

	@Override
	public boolean equalsInt(int i) {
		return false;
	}
}