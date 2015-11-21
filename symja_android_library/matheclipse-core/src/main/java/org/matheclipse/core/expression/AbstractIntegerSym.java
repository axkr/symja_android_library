package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.math.BigInteger;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

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
	public static AbstractIntegerSym valueOf(final BigInteger bigInteger) {
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

	public static AbstractIntegerSym valueOf(final long newnum) {
		if (Integer.MIN_VALUE <= newnum && newnum <= Integer.MAX_VALUE) {
			return new IntegerSym((int) newnum);
		}
		BigIntegerSym z = new BigIntegerSym();
		z.fBigIntValue = BigInteger.valueOf(newnum);
		return z;
	}

	/**
	 * Returns the AbstractIntegerSym for the specified character sequence
	 * stated in the specified radix. The characters must all be digits of the
	 * specified radix, except the first character which may be a plus sign
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
	public static AbstractIntegerSym valueOf(final String integerString, final int radix) {
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

	public abstract AbstractIntegerSym add(final AbstractIntegerSym that);

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

	public abstract AbstractIntegerSym div(final AbstractIntegerSym that);

	public abstract IAST divisors();

	@Override
	public abstract AbstractIntegerSym eabs();

	public IInteger eulerPhi() throws ArithmeticException {
		IAST ast = factorInteger();
		IInteger phi = AbstractIntegerSym.valueOf(1);
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			AbstractIntegerSym q = (AbstractIntegerSym) element.arg1();
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
		return null;
	}

	@Override
	public IInteger floor() {
		return this;
	}

	public abstract AbstractIntegerSym gcd(final AbstractIntegerSym that);

	/** {@inheritDoc} */
	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof IInteger) {
			return gcd((AbstractIntegerSym) that);
		}
		return F.C1;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger gcd(final IInteger that) {
		return gcd((AbstractIntegerSym) that);
	}

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
	public AbstractIntegerSym jacobiSymbol(AbstractIntegerSym b) {
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
			AbstractIntegerSym aDIV2 = valueOf(shiftRight(1));
			return aDIV2.jacobiSymbol(b).multiply(F.C2.jacobiSymbol(b));
		}
		return b.mod(this).jacobiSymbol(this).multiply(jacobiSymbolG(b));
	}

	protected AbstractIntegerSym jacobiSymbolF() {
		AbstractIntegerSym a = mod(F.C8);
		if (a.isOne()) {
			return F.C1;
		}
		if (a.equals(F.C7)) {
			return F.C1;
		}
		return F.CN1;
	}

	protected AbstractIntegerSym jacobiSymbolG(AbstractIntegerSym b) {
		AbstractIntegerSym i1 = mod(F.C4);
		if (i1.isOne()) {
			return F.C1;
		}
		AbstractIntegerSym i2 = b.mod(F.C4);
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
	public AbstractIntegerSym lcm(final AbstractIntegerSym that) {
		if (this.isZero() || that.isZero()) {
			return F.C0;
		}
		AbstractIntegerSym a = eabs();
		AbstractIntegerSym b = that.eabs();
		AbstractIntegerSym lcm = a.multiply(b).div(gcd(b));
		return lcm;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger lcm(final IInteger that) {
		return lcm((AbstractIntegerSym) that);
	}

	public abstract AbstractIntegerSym mod(final AbstractIntegerSym that);

	public abstract AbstractIntegerSym modPow(final AbstractIntegerSym exp, final AbstractIntegerSym m);

	public abstract AbstractIntegerSym moebiusMu();

	public abstract AbstractIntegerSym multiply(AbstractIntegerSym value);

	/**
	 * @param val
	 * @return
	 */
	public BigInteger multiply(final long val) {
		return getBigNumerator().multiply(BigInteger.valueOf(val));
	}

	@Override
	public abstract AbstractIntegerSym negate();

	@Override
	public AbstractIntegerSym opposite() {
		return negate();
	}

	/** {@inheritDoc} */
	@Override
	public final AbstractIntegerSym pow(final int exponent) throws ArithmeticException {
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

		AbstractIntegerSym r = this;
		AbstractIntegerSym x = r;

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
	public AbstractIntegerSym[] primitiveRoots() throws ArithmeticException {
		AbstractIntegerSym phi = (AbstractIntegerSym) eulerPhi();
		int size = phi.eulerPhi().toInt();
		if (size <= 0) {
			return null;
		}

		IAST ast = phi.factorInteger();
		AbstractIntegerSym d[] = new AbstractIntegerSym[ast.size() - 1];
		IAST element;
		for (int i = 1; i < ast.size(); i++) {
			element = (IAST) ast.get(i);
			AbstractIntegerSym q = (AbstractIntegerSym) element.arg1();
			d[i - 1] = phi.quotient(q);
		}
		int k = 0;
		AbstractIntegerSym n = this;
		AbstractIntegerSym m = AbstractIntegerSym.valueOf(1);

		AbstractIntegerSym resultArray[] = new AbstractIntegerSym[size];
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
			return new AbstractIntegerSym[0];
		}
		return resultArray;
	}

	public abstract AbstractIntegerSym quotient(final AbstractIntegerSym that);

	/**
	 * @param n
	 * @return
	 */
	public BigInteger shiftLeft(final int n) {
		return getBigNumerator().shiftLeft(n);
	}

	/**
	 * @param n
	 * @return
	 */
	public BigInteger shiftRight(final int n) {
		return getBigNumerator().shiftRight(n);
	}

	public byte[] toByteArray() {
		return getBigNumerator().toByteArray();
	}
}
