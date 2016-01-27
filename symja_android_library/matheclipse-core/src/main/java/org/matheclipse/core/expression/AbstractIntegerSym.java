package org.matheclipse.core.expression;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Externalizable;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.math.LongMath;

/**
 * Abstract base class for IntegerSym and BigIntegerSym
 * 
 * @see IntegerSym
 * @see BigIntegerSym
 *
 */
public abstract class AbstractIntegerSym extends ExprImpl implements IInteger, Externalizable {
	/**
	 * The BigInteger constant minus one.
	 * 
	 */
	public static final BigInteger BI_MINUS_ONE = BigInteger.valueOf(-1L);

	public static BigInteger lcm(final BigInteger i0, final BigInteger i1) {
		if (i0.equals(BigInteger.ZERO) && i1.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		BigInteger a = i0.abs();
		BigInteger b = i1.abs();
		BigInteger gcd = i0.gcd(b);
		BigInteger lcm = (a.multiply(b)).divide(gcd);
		return lcm;
	}

	/**
	 * @param bigInteger
	 * @return
	 */
	public static IInteger valueOf(final BigInteger bigInteger) {
		if (bigInteger.bitLength() <= 31) {
			return new IntegerSym(bigInteger.intValue());
		}
		return new BigIntegerSym(bigInteger);
	}

	public static IntegerSym valueOf(final int newnum) {
		switch (newnum) {
		case -1:
			return F.CN1;
		case -2:
			return F.CN2;
		case -3:
			return F.CN3;
		case -4:
			return F.CN4;
		case -5:
			return F.CN5;
		case -6:
			return F.CN6;
		case -7:
			return F.CN7;
		case -8:
			return F.CN8;
		case -9:
			return F.CN9;
		case -10:
			return F.CN10;
		case 0:
			return F.C0;
		case 1:
			return F.C1;
		case 2:
			return F.C2;
		case 3:
			return F.C3;
		case 4:
			return F.C4;
		case 5:
			return F.C5;
		case 6:
			return F.C6;
		case 7:
			return F.C7;
		case 8:
			return F.C8;
		case 9:
			return F.C9;
		case 10:
			return F.C10;
		}
		return new IntegerSym(newnum);
	}

	public static IInteger valueOf(final long newnum) {
		if (Integer.MIN_VALUE <= newnum && newnum <= Integer.MAX_VALUE) {
			return new IntegerSym((int) newnum);
		}
		BigIntegerSym z = new BigIntegerSym();
		z.fBigIntValue = BigInteger.valueOf(newnum);
		return z;
	}

	/**
	 * Returns the IInteger for the specified character sequence stated in the
	 * specified radix. The characters must all be digits of the specified
	 * radix, except the first character which may be a plus sign
	 * <code>'+'</code> or a minus sign <code>'-'</code> .
	 * 
	 * @param chars
	 *            the character sequence to parse.
	 * @param radix
	 *            the radix to be used while parsing.
	 * @return the corresponding large integer.
	 * @throws NumberFormatException
	 *             if the specified character sequence does not contain a
	 *             parsable large integer.
	 */
	public static IInteger valueOf(final String integerString, final int radix) {
		try {
			int value = Integer.parseInt(integerString, radix);
			return new IntegerSym(value);
		} catch (NumberFormatException nfe) {
			// possibly a big integer?
		}
		BigIntegerSym z = new BigIntegerSym();
		z.fBigIntValue = new BigInteger(integerString, radix);
		return z;
	}

	@Override
	public IRational abs() {
		return eabs();
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(new Apfloat(getBigNumerator(), precision));
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(getBigNumerator(), precision);
	}

	@Override
	public IInteger ceil() {
		return this;
	}

	@Override
	public IInteger ceilFraction() {
		return this;
	}

	@Override
	public IRational divideBy(IRational that) {
		return AbstractFractionSym.valueOf(this).divideBy(that);
	}

	@Override
	public IInteger eulerPhi() throws ArithmeticException {
		IAST ast = factorInteger();
		IInteger phi = AbstractIntegerSym.valueOf(1);
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			IInteger q = (IInteger) element.arg1();
			int c = ((IInteger) element.arg2()).toInt();
			if (c == 1) {
				phi = phi.multiply(q.subtract(AbstractIntegerSym.valueOf(1)));
			} else {
				phi = phi.multiply(q.subtract(AbstractIntegerSym.valueOf(1)).multiply(q.pow(c - 1)));
			}
		}
		return phi;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (engine.isNumericMode()) {
			return numericNumber();
		}
		return F.NIL;
	}

	@Override
	public IInteger floor() {
		return this;
	}

	@Override
	public IInteger floorFraction() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof IInteger) {
			return gcd((IInteger) that);
		}
		return F.C1;
	}

	// /** {@inheritDoc} */
	// @Override
	// public IInteger gcd(final IInteger that) {
	// return gcd( that);
	// }

	@Override
	public ISymbol head() {
		return F.IntegerHead;
	}

	@Override
	public int hierarchy() {
		return INTEGERID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false);
	}

	@Override
	public abstract ISignedNumber inverse();

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
		return equals(value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return equals(value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		return true;
	}

	/**
	 * See: <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia -
	 * Jacobi symbol</a><br/>
	 * Book: Algorithmen Arbeitsbuch - D.Herrmann page 160
	 * 
	 * @param b
	 * @return
	 */
	@Override
	public IInteger jacobiSymbol(IInteger b) {
		if (isOne()) {
			return F.C1;
		}
		if (isZero()) {
			return F.C0;
		}
		if (equals(F.C2)) {
			return b.jacobiSymbolF();
		}
		if (!isOdd()) {
			IInteger aDIV2 = shiftRight(1);
			return aDIV2.jacobiSymbol(b).multiply(F.C2.jacobiSymbol(b));
		}
		return b.mod(this).jacobiSymbol(this).multiply(jacobiSymbolG(b));
	}

	@Override
	public IInteger jacobiSymbolF() {
		IInteger a = mod(F.C8);
		if (a.isOne()) {
			return F.C1;
		}
		if (a.equals(F.C7)) {
			return F.C1;
		}
		return F.CN1;
	}

	@Override
	public IInteger jacobiSymbolG(IInteger b) {
		IInteger i1 = mod(F.C4);
		if (i1.isOne()) {
			return F.C1;
		}
		IInteger i2 = b.mod(F.C4);
		if (i2.isOne()) {
			return F.C1;
		}
		return F.CN1;
	}

	/**
	 * Returns the least common multiple of this large integer and the one
	 * specified.
	 * 
	 */
	@Override
	public IInteger lcm(final IInteger that) {
		if (this.isZero() || that.isZero()) {
			return F.C0;
		}
		IInteger a = eabs();
		IInteger b = that.eabs();
		IInteger lcm = a.multiply(b).div(gcd(b));
		return lcm;
	}

	/** {@inheritDoc} */
	// @Override
	// public IInteger lcm(final IInteger that) {
	// return lcm((IInteger) that);
	// }

	/**
	 * @param val
	 * @return
	 */
	public BigInteger multiply(final long val) {
		return getBigNumerator().multiply(BigInteger.valueOf(val));
	}

	@Override
	public abstract IInteger negate();

	@Override
	public IInteger opposite() {
		return negate();
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (isZero()) {
			return that;
		}
		if (that instanceof IInteger) {
			return this.add((IInteger) that);
		}
		if (that instanceof IFraction) {
			return AbstractFractionSym.valueOf(this).add((IFraction) that);
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).add(ComplexSym.valueOf(this));
		}
		return super.plus(that);
	}

	/** {@inheritDoc} */
	@Override
	public final IInteger pow(final int exponent) throws ArithmeticException {
		if (exponent < 0) {
			throw new ArithmeticException("Negative exponent");
		}
		if (exponent == 0L) {
			if (!this.isZero()) {
				return F.C1;
			}
			throw new ArithmeticException("Indeterminate: 0^0");
		} else if (exponent == 1L) {
			return this;
		}
		long exp = exponent;
		if (exponent < 0) {
			exp *= -1;
		}
		int b2pow = 0;

		while ((exp & 1) == 0) {
			b2pow++;
			exp >>= 1;
		}

		IInteger r = this;
		IInteger x = r;

		while ((exp >>= 1) > 0) {
			x = x.multiply(x);
			if ((exp & 1) != 0) {
				r = r.multiply(x);
			}
		}

		while (b2pow-- > 0) {
			r = r.multiply(r);
		}
		return r;
	}

	/**
	 * The primitive roots of this integer number
	 * 
	 * @return the primitive roots
	 * @throws ArithmeticException
	 */
	@Override
	public IInteger[] primitiveRoots() throws ArithmeticException {
		IInteger phi = eulerPhi();
		int size = phi.eulerPhi().toInt();
		if (size <= 0) {
			return null;
		}

		IAST ast = phi.factorInteger();
		IInteger d[] = new IInteger[ast.size() - 1];
		IAST element;
		for (int i = 1; i < ast.size(); i++) {
			element = (IAST) ast.get(i);
			IInteger q = (IInteger) element.arg1();
			d[i - 1] = phi.quotient(q);
		}
		int k = 0;
		IInteger n = this;
		IInteger m = AbstractIntegerSym.valueOf(1);

		IInteger resultArray[] = new IInteger[size];
		boolean b;
		while (m.compareTo(n) < 0) {
			b = m.gcd(n).compareTo(AbstractIntegerSym.valueOf(1)) == 0;
			for (int i = 0; i < d.length; i++) {
				b = b && m.modPow(d[i], n).compareTo(AbstractIntegerSym.valueOf(1)) > 0;
			}
			if (b) {
				resultArray[k++] = m;
			}
			m = m.add(AbstractIntegerSym.valueOf(1));
		}
		if (resultArray[0] == null) {
			return new IInteger[0];
		}
		return resultArray;
	}

	@Override
	public IRational subtract(final IRational that) {
		if (isZero()) {
			return that.negate();
		}
		return this.add(that.negate());
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		if (that instanceof IRational) {
			return this.add((IRational) that.negate());
		}
		return Num.valueOf(doubleValue() - that.doubleValue());
	}

	@Override
	public IExpr times(final IExpr that) {
		if (isZero()) {
			return F.C0;
		}
		if (isOne()) {
			return that;
		}
		if (that instanceof IInteger) {
			return this.multiply((IInteger) that);
		}
		if (that instanceof IFraction) {
			return AbstractFractionSym.valueOf(this).multiply((IFraction) that).normalize();
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this));
		}
		return super.times(that);
	}

	@Override
	public byte[] toByteArray() {
		return getBigNumerator().toByteArray();
	}

	public double getImaginary() {
		return 0.0;
	}

	public double getReal() {
		return doubleValue();
	}
}
