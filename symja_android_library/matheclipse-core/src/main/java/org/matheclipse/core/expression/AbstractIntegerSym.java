package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.math.BigInteger;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import edu.jas.arith.PrimeInteger;

/**
 * Abstract base class for IntegerSym and BigIntegerSym
 * 
 * @see IntegerSym
 * @see BigIntegerSym
 *
 */
public abstract class AbstractIntegerSym implements IInteger, Externalizable {
	static final int low = -128;
	static final int high = 128;
	static final IntegerSym cache[];

	static {
		cache = new IntegerSym[(high - low) + 1];
		int j = low;
		for (int k = 0; k < cache.length; k++) {
			cache[k] = new IntegerSym(j++);
		}
	}
	/**
	 * The BigInteger constant minus one.
	 * 
	 */
	public static final BigInteger BI_MINUS_ONE = BigInteger.valueOf(-1L);
	public static final BigInteger BI_TWO = BigInteger.valueOf(2L);
	public static final BigInteger BI_THREE = BigInteger.valueOf(3L);
	public static final BigInteger BI_FOUR = BigInteger.valueOf(4L);
	public static final BigInteger BI_SEVEN = BigInteger.valueOf(7L);
	public static final BigInteger BI_EIGHT = BigInteger.valueOf(8L);

	public static BigInteger jacobiSymbol(BigInteger a, BigInteger b) {
		if (a.equals(BigInteger.ONE)) {
			return BigInteger.ONE;
		}
		if (a.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		if (a.equals(BI_TWO)) {
			return BigIntegerSym.jacobiSymbolF(b);
		}
		if (!NumberUtil.isOdd(a)) {
			BigInteger aDIV2 = a.shiftRight(1);
			return jacobiSymbol(aDIV2, b).multiply(jacobiSymbol(BI_TWO, b));
		}
		return jacobiSymbol(b.mod(a), a).multiply(BigIntegerSym.jacobiSymbolG(a, b));
	}

	public static long jacobiSymbol(long a, long b) {
		if (a == 1L) {
			return 1l;
		}
		if (a == 0L) {
			return 0l;
		}
		if (a == 2L) {
			return jacobiSymbolF(b);
		}
		if (!((a & 1L) == 1L)) { // ! a.isOdd()
			long aDIV2 = a >> 1;
			// BigInteger aDIV2 = a.shiftRight(1);
			return jacobiSymbol(aDIV2, b) * jacobiSymbol(2L, b);
		}
		return jacobiSymbol(b % a, a) * jacobiSymbolG(a, b);
	}

	public static long jacobiSymbolF(long val) {
		long a = val % 8;
		if (a == 1L) {
			return 1L;
		}
		if (a == 7L) {
			return 1L;
		}
		return -1L;
	}

	public static long jacobiSymbolG(long a, long b) {
		long i1 = a % 4L;
		if (i1 == 1L) {
			return 1L;
		}
		long i2 = b % 4L;
		if (i2 == 1L) {
			return 1L;
		}
		return -1L;
	}

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
			return valueOf(bigInteger.intValue());
		}
		return new BigIntegerSym(bigInteger);
	}

	public static IntegerSym valueOf(final int newnum) {
		return (newnum >= low && newnum <= high) ? cache[newnum + (-low)] : new IntegerSym(newnum);
	}

	public static IInteger valueOf(final long newnum) {
		if (Integer.MIN_VALUE <= newnum && newnum <= Integer.MAX_VALUE) {
			return valueOf((int) newnum);
		}
		BigIntegerSym z = new BigIntegerSym();
		z.fBigIntValue = BigInteger.valueOf(newnum);
		return z;
	}

	/**
	 * Returns the IInteger for the specified character sequence stated in the specified radix. The characters must all
	 * be digits of the specified radix, except the first character which may be a plus sign <code>'+'</code> or a minus
	 * sign <code>'-'</code> .
	 * 
	 * @param integerString
	 *            the character sequence to parse.
	 * @param radix
	 *            the radix to be used while parsing.
	 * @return the corresponding large integer.
	 * @throws NumberFormatException
	 *             if the specified character sequence does not contain a parsable large integer.
	 */
	public static IInteger valueOf(final String integerString, final int radix) {
		if (integerString.length() >= 1) {
			char ch = integerString.charAt(0);
			if (ch == '-') {
				if (integerString.length() == 2) {
					ch = integerString.charAt(1);
					switch (ch) {
					case '0':
						return F.C0;
					case '1':
						return F.CN1;
					case '2':
						return F.CN2;
					case '3':
						return F.CN3;
					case '4':
						return F.CN4;
					case '5':
						return F.CN5;
					case '6':
						return F.CN6;
					case '7':
						return F.CN7;
					case '8':
						return F.CN8;
					case '9':
						return F.CN9;
					}
				}
			} else {
				if (integerString.length() == 1) {
					switch (ch) {
					case '0':
						return F.C0;
					case '1':
						return F.C1;
					case '2':
						return F.C2;
					case '3':
						return F.C3;
					case '4':
						return F.C4;
					case '5':
						return F.C5;
					case '6':
						return F.C6;
					case '7':
						return F.C7;
					case '8':
						return F.C8;
					case '9':
						return F.C9;
					}
				}
			}
		}
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
		return new Apcomplex(new Apfloat(toBigNumerator(), precision));
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(toBigNumerator(), precision);
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
	public IInteger charmichaelLambda() {
		return AbstractIntegerSym.valueOf(Primality.charmichaelLambda(toBigNumerator()));
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

	@Override
	public IRational divideBy(IRational that) {
		return AbstractFractionSym.valueOf(this).divideBy(that);
	}

	// /** {@inheritDoc} */
	// @Override
	// public IInteger gcd(final IInteger that) {
	// return gcd( that);
	// }

	/**
	 * IntegerSym extended greatest common divisor.
	 * 
	 * @param that
	 *            if that is of type IntegerSym calculate the extended GCD otherwise call <code>super#egcd(IExpr)</code>
	 * 
	 * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
	 */
	@Override
	public IExpr[] egcd(IExpr that) {
		if (that instanceof IInteger) {
			BigInteger S = ((IInteger) that).toBigNumerator();
			IInteger[] result = new IInteger[3];
			result[0] = null;
			result[1] = F.C1;
			result[2] = F.C1;
			if (that.isZero()) {
				result[0] = this;
				return result;
			}
			if (this.isZero()) {
				result[0] = (BigIntegerSym) that;
				return result;
			}
			BigInteger[] qr;
			BigInteger q = toBigNumerator();
			BigInteger r = S;
			BigInteger c1 = BigInteger.ONE;
			BigInteger d1 = BigInteger.ZERO;
			BigInteger c2 = BigInteger.ZERO;
			BigInteger d2 = BigInteger.ONE;
			BigInteger x1;
			BigInteger x2;
			while (!r.equals(BigInteger.ZERO)) {
				qr = q.divideAndRemainder(r);
				q = qr[0];
				x1 = c1.subtract(q.multiply(d1));
				x2 = c2.subtract(q.multiply(d2));
				c1 = d1;
				c2 = d2;
				d1 = x1;
				d2 = x2;
				q = r;
				r = qr[1];
			}
			if (q.signum() < 0) {
				q = q.negate();
				c1 = c1.negate();
				c2 = c2.negate();
			}
			result[0] = valueOf(q);
			result[1] = valueOf(c1);
			result[2] = valueOf(c2);
			return result;
		}
		return IInteger.super.egcd(that);
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
	public IInteger eulerPhi() throws ArithmeticException {
		return AbstractIntegerSym.valueOf(Primality.eulerPhi(toBigNumerator()));
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable factorInteger() {
		IInteger factor;
		IInteger last = F.CN2;
		int count = 0;
		final IAST iFactors = factorize();

		IASTAppendable subList = null;
		int size = iFactors.size();
		final IASTAppendable list = F.ListAlloc(size);
		for (int i = 1; i < size; i++) {
			factor = (IInteger) iFactors.get(i);
			if (!last.equals(factor)) {
				if (subList != null) {
					subList.append(AbstractIntegerSym.valueOf(count));
					list.append(subList);
				}
				count = 0;
				subList = F.ListAlloc(2);
				subList.append(factor);
			}
			count++;
			last = factor;
		}
		if (subList != null) {
			subList.append(AbstractIntegerSym.valueOf(count));
			list.append(subList);
		}
		return list;
	}

	/**
	 * Get all prime factors of this integer
	 * 
	 * @param result
	 *            add the prime factors to this result list
	 * @return
	 */
	public IAST factorize() {

		IInteger b = this;
		if (sign() < 0) {
			b = b.negate();
		} else if (b.isZero()) {
			return F.List(F.C0);
		} else if (b.isOne()) {
			return F.List(F.C1);
		}

		if (b instanceof IntegerSym) {
			Map<Long, Integer> map = PrimeInteger.factors(b.longValue());
			IASTAppendable result = F.ListAlloc(map.size() + 1);
			if (sign() < 0) {
				result.append(F.CN1);
			}
			for (Map.Entry<Long, Integer> entry : map.entrySet()) {
				long key = entry.getKey();
				IInteger is = valueOf(key);
				for (int i = 0; i < entry.getValue(); i++) {
					result.append(is);
				}
			}
			return result;
		}

		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		BigInteger rest = Primality.countPrimes32749(b.toBigNumerator(), map);

		IASTAppendable result = F.ListAlloc(map.size() + 10);
		if (sign() < 0) {
			result.append(F.CN1);
		}
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			int key = entry.getKey();
			AbstractIntegerSym is = valueOf(key);
			for (int i = 0; i < entry.getValue(); i++) {
				result.append(is);
			}
		}
		if (rest.equals(BigInteger.ONE)) {
			return result;
		}
		if (rest.isProbablePrime(PRIME_CERTAINTY)) {
			result.append(valueOf(rest));
			return result;
		}
		b = valueOf(rest);

		SortedMap<BigInteger, Integer> bigMap = new TreeMap<BigInteger, Integer>();
		Primality.factorInteger(rest, bigMap);

		for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
			BigInteger key = entry.getKey();
			IInteger is = valueOf(key);
			for (int i = 0; i < entry.getValue(); i++) {
				result.append(is);
			}
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IRational fractionalPart() {
		return F.C0;
	}

	@Override
	public IInteger floor() {
		return this;
	}

	@Override
	public IInteger floorFraction() {
		return this;
	}

	public IInteger factorial() {
		int ni = toIntDefault(Integer.MIN_VALUE);
		if (ni > Integer.MIN_VALUE) {
			return NumberTheory.factorial(ni);
		}

		IInteger result = F.C1;
		if (compareTo(F.C0) == -1) {
			result = F.CN1;
			for (IInteger i = F.CN2; i.compareTo(this) >= 0; i = i.add(F.CN1)) {
				result = result.multiply(i);
			}
		} else {
			for (IInteger i = F.C2; i.compareTo(this) <= 0; i = i.add(F.C1)) {
				result = result.multiply(i);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger[] gaussianIntegers() {
		return new IInteger[] { this, F.C0 };
	}

	/** {@inheritDoc} */
	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof IInteger) {
			return gcd((IInteger) that);
		}
		return F.C1;
	}

	@Override
	public double imDoubleValue() {
		return 0.0;
	}

	@Override
	public double reDoubleValue() {
		return doubleValue();
	}

	@Override
	public ISymbol head() {
		return F.Integer;
	}

	@Override
	public int hierarchy() {
		return INTEGERID;
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false, false, false);
	}

	@Override
	public abstract ISignedNumber inverse();

	// public static BigInteger jacobiSymbol(long a, long b) {
	// return jacobiSymbol(BigInteger.valueOf(a), BigInteger.valueOf(b));
	// }

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
	 * See: <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a><br/>
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

	@Override
	public long leafCountSimplify() {
		if (isZero()) {
			return 1;
		}
		return integerLength(F.C10) + (isPositive() ? 0 : 1);
	}

	/**
	 * Returns the least common multiple of this large integer and the one specified.
	 * 
	 */
	@Override
	public IInteger lcm(final IInteger that) {
		if (this.isZero() || that.isZero()) {
			return F.C0;
		}
		if (this.equals(that)) {
			return this.abs();
		}
		if (this.isOne()) {
			return that.abs();
		}
		if (that.isOne()) {
			return this.abs();
		}
		IInteger a = abs();
		IInteger b = that.abs();
		IInteger gcd = a.gcd(b);
		IInteger lcm = a.multiply(b).div(gcd);
		return lcm;
	}

	@Override
	public IInteger moebiusMu() {
		return AbstractIntegerSym.valueOf(Primality.moebiusMu(toBigNumerator()));
	}

	/**
	 * @param val
	 * @return
	 */
	public BigInteger multiply(final long val) {
		return toBigNumerator().multiply(BigInteger.valueOf(val));
	}

	@Override
	public abstract IInteger negate();

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest-factor&quot;, so
	 * that <code>this== (nth-root ^ n) + rest</code>
	 * 
	 * @return <code>{nth-root, rest}</code>
	 */
	@Override
	public IInteger[] nthRootSplit(int n) throws ArithmeticException {
		IInteger[] result = new IInteger[2];
		if (sign() == 0) {
			result[0] = F.C0;
			result[1] = F.C1;
			return result;
		} else if (sign() < 0) {
			if (n % 2 == 0) {
				// even exponent n
				throw new ArithmeticException();
			} else {
				// odd exponent n
				result = negate().nthRootSplit(n);
				result[1] = result[1].negate();
				return result;
			}
		}

		AbstractIntegerSym b = this;
		BigInteger[] nthRoot = Primality.countRoot1021(b.toBigNumerator(), n);
		result[0] = AbstractIntegerSym.valueOf(nthRoot[0]);
		result[1] = AbstractIntegerSym.valueOf(nthRoot[1]);
		return result;
	}

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
			return ((ComplexSym) that).add(ComplexSym.valueOf(this)).normalize();
		}
		return IInteger.super.plus(that);
	}

	/** {@inheritDoc} */
	@Override
	public final IInteger pow(final long exponent) throws ArithmeticException {
		if (exponent < 0L) {
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
		if (isOne()) {
			return F.C1;
		}
		if (isMinusOne()) {
			if ((exponent & 1L) == 1L) { // isOdd(exponent) ?
				return F.CN1;
			} else {
				return F.C1;
			}
		}

		if (this instanceof IntegerSym && exponent < 63) {
			try {
				return valueOf(ArithmeticUtils.pow((long) ((IntegerSym) this).fIntValue, (int) exponent));
			} catch (RuntimeException ex) {
				//
			}
		}

		long exp = exponent;
		long b2pow = 0;

		while ((exp & 1) == 0L) {
			b2pow++;
			exp >>= 1;
		}

		IInteger r = this;
		IInteger x = r;

		while ((exp >>= 1) > 0L) {
			x = x.multiply(x);
			if ((exp & 1) != 0) {
				r = r.multiply(x);
			}
		}

		while (b2pow-- > 0L) {
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
	public IInteger[] primitiveRootList() throws ArithmeticException {
		IInteger phi = eulerPhi();
		int size = phi.eulerPhi().toInt();
		if (size <= 0) {
			return null;
		}

		IAST ast = phi.factorInteger();
		IInteger d[] = new IInteger[ast.argSize()];
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			IInteger q = (IInteger) element.arg1();
			d[i - 1] = phi.quotient(q);
		}
		int k = 0;
		IInteger n = this;
		IInteger m = F.C1;

		IInteger resultArray[] = new IInteger[size];
		boolean b;
		while (m.compareTo(n) < 0) {
			b = m.gcd(n).isOne();
			for (int i = 0; i < d.length; i++) {
				b = b && m.modPow(d[i], n).isGreaterThan(F.C1);
			}
			if (b) {
				resultArray[k++] = m;
			}
			m = m.add(F.C1);
		}
		if (resultArray[0] == null) {
			return new IInteger[0];
		}
		return resultArray;
	}

	@Override
	public IInteger quotient(final IInteger that) {
		BigInteger quotient = toBigNumerator().divide(that.toBigNumerator());
		BigInteger mod = toBigNumerator().remainder(that.toBigNumerator());
		if (mod.equals(BigInteger.ZERO)) {
			return valueOf(quotient);
		}
		if (quotient.compareTo(BigInteger.ZERO) < 0) {
			return valueOf(quotient.subtract(BigInteger.ONE));
		}
		return valueOf(quotient);
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
			return AbstractFractionSym.valueOf(this).mul((IFraction) that).normalize();
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this)).normalize();
		}
		return IInteger.super.times(that);
	}

	@Override
	public byte[] toByteArray() {
		return toBigNumerator().toByteArray();
	}
}
