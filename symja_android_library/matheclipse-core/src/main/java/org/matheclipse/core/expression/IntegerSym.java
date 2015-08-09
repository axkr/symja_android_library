package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.F.List;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math4.fraction.BigFraction;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Subsets;
import org.matheclipse.core.reflection.system.Subsets.KSubsetsList;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.math.BigIntegerMath;

/**
 * IInteger implementation which delegates most of the methods to the BigInteger methods
 */
public class IntegerSym extends ExprImpl implements IInteger, Externalizable {
	/**
	 * The BigInteger constant minus one.
	 * 
	 */
	public static final BigInteger BI_MINUS_ONE = BigInteger.valueOf(-1L);

	/**
	 * Be cautious with this method, no new internal BigInteger is created
	 * 
	 * @param value
	 * @return
	 */
	protected static IntegerSym newInstance(final BigInteger value) {
		IntegerSym z = new IntegerSym();
		z.fInteger = value;
		return z;
	}

	public static IntegerSym valueOf(final long value) {
		IntegerSym z = new IntegerSym();
		z.fInteger = BigInteger.valueOf(value);
		return z;
	}

	/**
	 * Returns the IntegerImpl for the specified character sequence stated in the specified radix. The characters must all be digits
	 * of the specified radix, except the first character which may be a plus sign <code>'+'</code> or a minus sign <code>'-'</code>
	 * .
	 * 
	 * @param chars
	 *            the character sequence to parse.
	 * @param radix
	 *            the radix to be used while parsing.
	 * @return the corresponding large integer.
	 * @throws NumberFormatException
	 *             if the specified character sequence does not contain a parsable large integer.
	 */
	public static IntegerSym valueOf(final String integerString, final int radix) {
		IntegerSym z = new IntegerSym();
		z.fInteger = new BigInteger(integerString, radix);
		return z;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6389228668633533063L;

	/* package private */BigInteger fInteger;

	private transient int fHashValue = 0;

	/**
	 * do not use directly, needed for serialization/deserialization
	 * 
	 */
	public IntegerSym() {
		fInteger = null;
	}

	@Override
	public boolean equalsInt(final int i) {
		return fInteger.equals(BigInteger.valueOf(i));
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (engine.isNumericMode()) {
			return numericNumber();
		}
		return null;
	}

	public final INumber numericNumber() {
		return F.num(this);
	}

	@Override
	public int hierarchy() {
		return INTEGERID;
	}

	/**
	 * @param val
	 * @return
	 */
	@Override
	public IInteger add(final IInteger val) {
		return newInstance(fInteger.add(val.getBigNumerator()));
	}

	/**
	 * @param val
	 * @return
	 */
	@Override
	public IInteger multiply(final IInteger val) {
		return newInstance(fInteger.multiply(val.getBigNumerator()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IntegerSym) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			return fInteger.equals(((IntegerSym) obj).fInteger);
		}
		return false;
	}

	/**
	 * @param bigInteger
	 * @return
	 */
	public static IntegerSym valueOf(final BigInteger bigInteger) {
		return newInstance(bigInteger);
	}

	/**
	 * IntegerSym extended greatest common divisor.
	 * 
	 * @param that
	 *            if that is of type IntegerSym calculate the extended GCD otherwise call {@link super#egcd(IExpr)};
	 * 
	 * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
	 */
	@Override
	public IExpr[] egcd(IExpr that) {
		if (that instanceof IntegerSym) {
			BigInteger S = ((IntegerSym) that).fInteger;
			IInteger[] result = new IInteger[3];
			result[0] = null;
			result[1] = F.C1;
			result[2] = F.C1;
			if (that == null || that.isZero()) {
				result[0] = ((IntegerSym) this);
				return result;
			}
			if (this.isZero()) {
				result[0] = ((IntegerSym) that);
				return result;
			}
			BigInteger[] qr;
			BigInteger q = fInteger;
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
		return super.egcd(that);
	}

	/** {@inheritDoc} */
	@Override
	public IntegerSym eabs() {
		return newInstance(fInteger.abs());
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		BigInteger temp = fInteger;
		if (fInteger.compareTo(BigInteger.ZERO) < 0) {
			temp = temp.negate();
		}
		return temp.compareTo(BigInteger.ONE);
	}

	/**
	 * @param that
	 * @return
	 */
	public IntegerSym add(final IntegerSym that) {
		return newInstance(fInteger.add(that.fInteger));
	}

	/**
	 * @return
	 */
	public int bitLength() {
		return fInteger.bitLength();
	}

	/**
	 * @param val
	 * @return
	 */
	public BigInteger divide(final long val) {
		return fInteger.divide(BigInteger.valueOf(val));
	}

	/**
	 * @param that
	 * @return
	 */
	public BigInteger divide(final BigInteger that) {
		return fInteger.divide(that);
	}

	public IntegerSym quotient(final IntegerSym that) {
		return newInstance(fInteger.divide(that.fInteger));
	}

	/**
	 * @return
	 */
	@Override
	public double doubleValue() {
		return fInteger.doubleValue();
	}

	/**
	 * Returns the greatest common divisor of this large integer and the one specified.
	 * 
	 */
	public IntegerSym gcd(final IntegerSym that) {
		return newInstance(fInteger.gcd(that.fInteger));
	}

	/**
	 * Returns the greatest common divisor of this large integer and the one specified.
	 * 
	 */
	@Override
	public IInteger gcd(final IInteger that) {
		return newInstance(fInteger.gcd(((IntegerSym) that).fInteger));
	}

	@Override
	public IExpr gcd(IExpr that) {
		if (that instanceof IInteger) {
			return gcd((IInteger) that);
		}
		return F.C1;
	}

	/**
	 * Returns the least common multiple of this large integer and the one specified.
	 * 
	 */
	public IntegerSym lcm(final IntegerSym that) {
		if (this.isZero() && that.isZero()) {
			return (IntegerSym) F.C0;
		}
		BigInteger lcm = lcm(fInteger, that.fInteger);
		return newInstance(lcm);
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
	 * Returns the least common multiple of this large integer and the one specified.
	 * 
	 */
	@Override
	public IInteger lcm(final IInteger that) {
		return lcm((IntegerSym) that);
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		if (fHashValue == 0) {
			fHashValue = fInteger.hashCode();
		}
		return fHashValue;
	}

	/**
	 * @param that
	 * @return
	 */
	public boolean isLargerThan(final BigInteger that) {
		return fInteger.compareTo(that) > 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return fInteger.compareTo(BigInteger.ZERO) < 0;
	}

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

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return fInteger.compareTo(BigInteger.ZERO) > 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isProbablePrime() {
		return isProbablePrime(PRIME_CERTAINTY);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isProbablePrime(int certainty) {
		return fInteger.isProbablePrime(certainty);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return equals(value);
	}

	@Override
	public boolean isZero() {
		return fInteger.equals(BigInteger.ZERO);
	}

	@Override
	public boolean isOne() {
		return fInteger.equals(BigInteger.ONE);
	}

	@Override
	public boolean isMinusOne() {
		return fInteger.equals(BI_MINUS_ONE);
	}

	@Override
	public int intValue() {
		return (int) fInteger.intValue();
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long longValue() {
		return fInteger.longValue();
	}

	public IntegerSym mod(final IntegerSym that) {
		return newInstance(fInteger.mod(that.fInteger));
	}

	/**
	 * @param that
	 * @return
	 */
	public IntegerSym multiply(final IntegerSym that) {
		return newInstance(fInteger.multiply(that.fInteger));
	}

	/**
	 * @param val
	 * @return
	 */
	public BigInteger multiply(final long val) {
		return fInteger.multiply(BigInteger.valueOf(val));
	}

	 
	@Override
	public IntegerSym negate() {
		return newInstance(fInteger.negate());
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber opposite() {
		return newInstance(fInteger.negate());
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof IntegerSym) {
			return this.add((IntegerSym) that);
		}
		if (isZero()) {
			return that;
		}
		if (that instanceof FractionSym) {
			return FractionSym.valueOf(fInteger).add((FractionSym) that);
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).add(ComplexSym.valueOf(this));
		}
		return super.plus(that);
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		if (that instanceof IntegerSym) {
			return FractionSym.valueOf(fInteger).divideBy(that);
		}
		if (that instanceof FractionSym) {
			return FractionSym.valueOf(fInteger).divideBy(that);
		}
		return Num.valueOf(fInteger.doubleValue() / that.doubleValue());
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
		if (that instanceof IntegerSym) {
			return this.add((IntegerSym) that.negate());
		}
		if (isZero()) {
			return that.negate();
		}
		if (that instanceof FractionSym) {
			return FractionSym.valueOf(fInteger).subtractFrom(that);
		}
		return Num.valueOf(fInteger.doubleValue() - that.doubleValue());
	}

	/**
	 * @param exp
	 * @return
	 */
	@Override
	public IntegerSym pow(final int exp) {
		return newInstance(fInteger.pow(exp));
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber inverse() {
		if (isOne()) {
			return this;
		}
		if (NumberUtil.isNegative(fInteger)) {
			return FractionSym.valueOf(BigInteger.valueOf(-1), fInteger.negate()).normalize();
		}
		return FractionSym.valueOf(BigInteger.ONE, fInteger).normalize();
	}

	/**
	 * @param n
	 * @return
	 */
	public BigInteger shiftLeft(final int n) {
		return fInteger.shiftLeft(n);
	}

	/**
	 * @param n
	 * @return
	 */
	public BigInteger shiftRight(final int n) {
		return fInteger.shiftRight(n);
	}

	/**
	 * @param that
	 * @return
	 */
	public BigInteger subtract(final BigInteger that) {
		return fInteger.subtract(that);
	}

	@Override
	public IInteger subtract(final IInteger that) {
		return newInstance(fInteger.subtract(that.getBigNumerator()));
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof IntegerSym) {
			return this.multiply((IntegerSym) that);
		}
		if (isZero()) {
			return F.C0;
		}
		if (isOne()) {
			return that;
		}
		if (that instanceof FractionSym) {
			return FractionSym.valueOf(fInteger).multiply((FractionSym) that).normalize();
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this));
		}
		return super.times(that);
	}

	public byte[] toByteArray() {
		return fInteger.toByteArray();
	}

	/** {@inheritDoc} */
	@Override
	public BigInteger getBigDenominator() {
		return BigInteger.ONE;
	}
	
	/** {@inheritDoc} */
	@Override
	public BigInteger getBigNumerator() {
		return fInteger;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger getNumerator() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger getDenominator() {
		return F.C1;
	}

	/** {@inheritDoc} */
	@Override
	public BigFraction getFraction() {
		return new BigFraction(fInteger);
	}

	/**
	 * Get all prime factors of this integer
	 * 
	 * @return
	 */
	public IAST factorize(IAST result) {
		// final ArrayList<IInteger> result = new ArrayList<IInteger>();
		IntegerSym b = this;
		if (sign() < 0) {
			b = b.multiply(IntegerSym.valueOf(-1));
			result.add(IntegerSym.valueOf(-1));
		} else if (b.fInteger.equals(BigInteger.ZERO)) {
			result.add(IntegerSym.valueOf(0));
			return result;
		} else if (b.fInteger.equals(BigInteger.ONE)) {
			result.add(IntegerSym.valueOf(1));
			return result;
		}
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		BigInteger rest = Primality.countPrimes32749(b.fInteger, map);

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			int key = entry.getKey();
			IntegerSym is = valueOf(key);
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(is);
			}
		}
		if (rest.equals(BigInteger.ONE)) {
			return result;
		}
		b = valueOf(rest);

		Map<BigInteger, Integer> bigMap = new TreeMap<BigInteger, Integer>();
		Primality.pollardRhoFactors(b.getBigNumerator(), bigMap);

		for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
			BigInteger key = entry.getKey();
			IntegerSym is = valueOf(key);
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(is);
			}
		}
		//
		// if (b.fInteger.isProbablePrime(32)) {
		// result.add(b);
		// return result;
		// }

		// TODO improve performance
		// IntegerSym p = IntegerSym.valueOf(1023);
		// while (true) {
		// final IntegerSym q[] = b.divideAndRemainder(p);
		// if (q[0].compareTo(p) < 0) {
		// result.add(b);
		// break;
		// }
		// if (q[1].sign() == 0) {
		// result.add(p);
		// b = q[0];
		// } else {
		// // test only odd integers
		// p = p.add(IntegerSym.valueOf(2));
		// }
		// }
		return result;
	}

	/**
	 * Get the highest exponent of <code>base</code> that divides <code>this</code>
	 * 
	 * @return the exponent
	 */
	public IExpr exponent(IInteger base) {
		IntegerSym b = this;
		if (sign() < 0) {
			b = (IntegerSym) b.negate();
		} else if (b.isZero()) {
			return F.CInfinity;
		} else if (b.isOne()) {
			return F.C0;
		}
		if (b.equals(base)) {
			return F.C1;
		}
		BigInteger rest = Primality.countExponent(b.fInteger, base.getBigNumerator());
		return valueOf(rest);
	}

	/** {@inheritDoc} */
	public IAST factorInteger() {
		IInteger factor;
		IInteger last = IntegerSym.valueOf(-2);
		int count = 0;
		final IAST iFactors = factorize(F.List());
		final IAST list = List();
		IAST subList = null;
		for (int i = 1; i < iFactors.size(); i++) {
			factor = (IInteger) iFactors.get(i);
			if (!last.equals(factor)) {
				if (subList != null) {
					subList.add(IntegerSym.valueOf(count));
					list.add(subList);
				}
				count = 0;
				subList = List(factor);
			}
			count++;
			last = factor;
		}
		if (subList != null) {
			subList.add(IntegerSym.valueOf(count));
			list.add(subList);
		}
		return list;
	}

	/**
	 * Return the divisors of this integer number.
	 * 
	 * <pre>
	 * divisors(24) ==> {1,2,3,4,6,8,12,24}
	 * </pre>
	 */
	public IAST divisors() {
		Set<IInteger> set = new TreeSet<IInteger>();
		final IAST primeFactorsList = factorize(F.List());
		int len = primeFactorsList.size() - 1;

		// build the k-subsets from the primeFactorsList
		for (int k = 1; k < len; k++) {
			final KSubsetsList iter = Subsets.createKSubsets(primeFactorsList, k, F.List(), 1);
			for (IAST subset : iter) {
				if (subset == null) {
					break;
				}
				// create the product of all integers in the k-subset
				IInteger factor = F.C1;
				for (int j = 1; j < subset.size(); j++) {
					factor = factor.multiply((IInteger) subset.get(j));
				}
				// add this divisor to the set collection
				set.add(factor);
			}
		}

		// build the final divisors list from the tree set
		final IAST resultList = List(F.C1);
		for (IInteger entry : set) {
			resultList.add(entry);
		}
		resultList.add(this);
		return resultList;
	}

	public IInteger eulerPhi() throws ArithmeticException {
		IAST ast = factorInteger();
		IInteger phi = IntegerSym.valueOf(1);
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			IntegerSym q = (IntegerSym) element.arg1();
			int c = ((IInteger) element.arg2()).toInt();
			if (c == 1) {
				phi = phi.multiply(q.subtract(IntegerSym.valueOf(1)));
			} else {
				phi = phi.multiply(q.subtract(IntegerSym.valueOf(1)).multiply(q.pow(c - 1)));
			}
		}
		return phi;
	}

	public IntegerSym moebiusMu() {
		if (this.compareTo(IntegerSym.valueOf(1)) == 0) {
			return IntegerSym.valueOf(1);
		}
		IAST ast = factorInteger();
		IntegerSym max = IntegerSym.valueOf(1);
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			IntegerSym c = (IntegerSym) element.get(2);
			if (c.compareTo(max) > 0) {
				max = c;
			}
		}
		if (max.compareTo(IntegerSym.valueOf(1)) > 0) {
			return IntegerSym.valueOf(0);
		}
		if (((ast.size() - 1) & 0x00000001) == 0x00000001) {
			// odd number
			return IntegerSym.valueOf(-1);
		}
		return IntegerSym.valueOf(1);
	}

	private IntegerSym jacobiSymbolF() {
		IntegerSym a = mod(F.C8);
		if (a.isOne()) {
			return F.C1;
		}
		if (a.equals(F.C7)) {
			return F.C1;
		}
		return F.CN1;
	}

	private IntegerSym jacobiSymbolG(IntegerSym b) {
		IntegerSym i1 = mod(F.C4);
		if (i1.isOne()) {
			return F.C1;
		}
		IntegerSym i2 = b.mod(F.C4);
		if (i2.isOne()) {
			return F.C1;
		}
		return F.CN1;
	}

	/**
	 * See: <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a><br/>
	 * Book: Algorithmen Arbeitsbuch - D.Herrmann page 160
	 * 
	 * @param b
	 * @return
	 */
	public IntegerSym jacobiSymbol(IntegerSym b) {
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
			IntegerSym aDIV2 = valueOf(shiftRight(1));
			return aDIV2.jacobiSymbol(b).multiply(F.C2.jacobiSymbol(b));
		}
		return b.mod(this).jacobiSymbol(this).multiply(jacobiSymbolG(b));
	}

	/**
	 * The primitive roots of this integer number
	 * 
	 * @return the primitive roots
	 * @throws ArithmeticException
	 */
	public IInteger[] primitiveRoots() throws ArithmeticException {
		IntegerSym phi = (IntegerSym) eulerPhi();
		int size = phi.eulerPhi().toInt();
		if (size <= 0) {
			return null;
		}

		IAST ast = phi.factorInteger();
		IntegerSym d[] = new IntegerSym[ast.size() - 1];
		IAST element;
		for (int i = 1; i < ast.size(); i++) {
			element = (IAST) ast.get(i);
			IntegerSym q = (IntegerSym) element.arg1();
			d[i - 1] = phi.quotient(q);
		}
		int k = 0;
		IntegerSym n = this;
		IntegerSym m = IntegerSym.valueOf(1);

		IntegerSym resultArray[] = new IntegerSym[size];
		boolean b;
		while (m.compareTo(n) < 0) {
			b = m.gcd(n).compareTo(IntegerSym.valueOf(1)) == 0;
			for (int i = 0; i < d.length; i++) {
				b = b && m.modPow(d[i], n).compareTo(IntegerSym.valueOf(1)) > 0;
			}
			if (b) {
				resultArray[k++] = m;
			}
			m = m.add(IntegerSym.valueOf(1));
		}
		if (resultArray[0] == null) {
			return new IntegerSym[0];
		}
		return resultArray;
	}

	public int compareTo(final IntegerSym that) {
		return fInteger.compareTo(that.fInteger);
	}

	/** {@inheritDoc} */
	public IInteger[] divideAndRemainder(final IInteger that) {
		final IntegerSym[] res = new IntegerSym[2];
		BigInteger[] largeRes = fInteger.divideAndRemainder(that.getBigNumerator());
		res[0] = newInstance(largeRes[0]);
		res[1] = newInstance(largeRes[1]);

		return res;
	}

	public IntegerSym remainder(final IntegerSym that) {
		return newInstance(fInteger.remainder(that.fInteger));
	}

	public IExpr remainder(final IExpr that) {
		if (that instanceof IntegerSym) {
			return newInstance(fInteger.remainder(((IntegerSym) that).fInteger));
		}
		return this;
	}

	@Override
	public boolean isEven() {
		return NumberUtil.isEven(fInteger);
	}

	@Override
	public boolean isOdd() {
		return NumberUtil.isOdd(fInteger);
	}

	public IntegerSym modInverse(final IntegerSym m) {
		return newInstance(fInteger.modInverse(m.fInteger));
	}

	public IntegerSym modPow(final IntegerSym exp, final IntegerSym m) {
		return newInstance(fInteger.modPow(exp.fInteger, m.fInteger));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		return NumberUtil.toInt(fInteger);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		return NumberUtil.toLong(fInteger);
	}

	@Override
	public int sign() {
		return fInteger.signum();
	}

	/**
	 * Returns the integer square root of this integer.
	 * 
	 * @return <code>k<code> such as <code>k^2 <= this < (k + 1)^2</code>
	 * @throws ArithmeticException
	 *             if this integer is negative.
	 */
	public IInteger sqrt() throws ArithmeticException {
		return valueOf(BigIntegerMath.sqrt(fInteger, RoundingMode.UNNECESSARY));
	}

	/**
	 * Returns the nth-root of this integer.
	 * 
	 * @return <code>k<code> such as <code>k^n <= this < (k + 1)^n</code>
	 * @throws IllegalArgumentException
	 *             if {@code this < 0}
	 * @throws ArithmeticException
	 *             if this integer is negative and n is even.
	 */
	@Override
	public IInteger nthRoot(int n) throws ArithmeticException {
		if (n < 0) {
			throw new IllegalArgumentException("nthRoot(" + n + ") n must be >= 0");
		}
		if (n == 2) {
			return sqrt();
		}
		if (sign() == 0) {
			return IntegerSym.valueOf(0);
		} else if (sign() < 0) {
			if (n % 2 == 0) {
				// even exponent n
				throw new ArithmeticException();
			} else {
				// odd exponent n
				return (IntegerSym) ((IntegerSym) negate()).nthRoot(n).negate();
			}
		} else {
			IInteger result;
			IInteger temp = this;
			do {
				result = temp;
				temp = divideAndRemainder(temp.pow(n - 1))[0].add(temp.multiply(IntegerSym.valueOf(n - 1))).divideAndRemainder(
						IntegerSym.valueOf(n))[0];
			} while (temp.compareTo(result) < 0);
			return result;
		}
	}

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest-factor&quot;, so that
	 * <code>this== (nth-root)^n + rest</code>
	 * 
	 * @return <code>{nth-root, rest}</code>
	 */
	@Override
	public IInteger[] nthRootSplit(int n) throws ArithmeticException {
		IInteger[] result = new IInteger[2];
		if (sign() == 0) {
			result[0] = IntegerSym.valueOf(0);
			result[1] = IntegerSym.valueOf(1);
			return result;
		} else if (sign() < 0) {
			if (n % 2 == 0) {
				// even exponent n
				throw new ArithmeticException();
			} else {
				// odd exponent n
				result = ((IntegerSym) negate()).nthRootSplit(n);
				result[1] = (IInteger) result[1].negate();
				return result;
			}
		}

		IntegerSym b = this;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		BigInteger rest = Primality.countPrimes1021(b.fInteger, map);
		IntegerSym nthRoot = IntegerSym.valueOf(1);
		IntegerSym restFactors = IntegerSym.valueOf(rest);
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			IntegerSym primeLE1021 = valueOf(entry.getKey());
			int primeCounter = entry.getValue();
			int div = primeCounter / n;
			if (div > 0) {
				// build nth-root
				nthRoot = nthRoot.multiply(primeLE1021.pow(div));
			}
			int mod = primeCounter % n;
			if (mod > 0) {
				// build rest factor
				restFactors = restFactors.multiply(primeLE1021.pow(mod));
			}
		}
		result[0] = nthRoot;
		result[1] = restFactors;
		return result;

	}

	@Override
	public int complexSign() {
		return sign();
	}

	@Override
	public IInteger ceil() {
		return this;
	}

	@Override
	public IInteger floor() {
		return this;
	}

	@Override
	public IInteger round() {
		return this;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof IntegerSym) {
			return fInteger.compareTo(((IntegerSym) expr).fInteger);
		}
		if (expr instanceof FractionSym) {
			return -((FractionSym) expr).fRational.compareTo(new BigFraction(fInteger, BigInteger.ONE));
		}
		if (expr instanceof Num) {
			double d = fInteger.doubleValue() - ((Num) expr).getRealPart();
			if (d < 0.0) {
				return -1;
			}
			if (d > 0.0) {
				return 1;
			}
		}
		return super.compareTo(expr);
	}

	@Override
	public boolean isLessThan(ISignedNumber obj) {
		if (obj instanceof IntegerSym) {
			return fInteger.compareTo(((IntegerSym) obj).fInteger) < 0;
		}
		if (obj instanceof FractionSym) {
			return -((FractionSym) obj).fRational.compareTo(new BigFraction(fInteger, BigInteger.ONE)) < 0;
		}
		return fInteger.doubleValue() < obj.doubleValue();
	}

	@Override
	public boolean isGreaterThan(ISignedNumber obj) {
		if (obj instanceof IntegerSym) {
			return fInteger.compareTo(((IntegerSym) obj).fInteger) > 0;
		}
		if (obj instanceof FractionSym) {
			return -((FractionSym) obj).fRational.compareTo(new BigFraction(fInteger, BigInteger.ONE)) > 0;
		}
		return fInteger.doubleValue() > obj.doubleValue();
	}

	@Override
	public ISymbol head() {
		return F.IntegerHead;
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get().convertInteger(sb, this, Integer.MIN_VALUE, OutputFormFactory.NO_PLUS_CALL);
			return sb.toString();
		} catch (Exception e1) {
		}
		// fall back to simple output format
		return fInteger.toString();
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		int value = NumberUtil.toInt(fInteger);
		switch (value) {
		case -1:
			return "CN1";
		case -2:
			return "CN2";
		case -3:
			return "CN3";
		case -4:
			return "CN4";
		case -5:
			return "CN5";
		case -6:
			return "CN6";
		case -7:
			return "CN7";
		case -8:
			return "CN8";
		case -9:
			return "CN9";
		case -10:
			return "CN10";
		case 0:
			return "C0";
		case 1:
			return "C1";
		case 2:
			return "C2";
		case 3:
			return "C3";
		case 4:
			return "C4";
		case 5:
			return "C5";
		case 6:
			return "C6";
		case 7:
			return "C7";
		case 8:
			return "C8";
		case 9:
			return "C9";
		case 10:
			return "C10";
		}
		return "ZZ(" + value + "L)";
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

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getIm() {
		return F.C0;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getRe() {
		return this;
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(fInteger, precision);
	}

	@Override
	public Num numValue() {
		return Num.valueOf(doubleValue());
	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(new Apfloat(fInteger, precision));
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		return ComplexNum.valueOf(doubleValue());
	}

	private Object writeReplace() throws ObjectStreamException {
		ExprID temp = F.GLOBAL_IDS_MAP.get(this);
		if (temp != null) {
			return temp;
		}
		return this;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if ((fInteger.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0)
				&& (fInteger.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0)) {
			int value = fInteger.intValue();
			if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
				objectOutput.writeByte(1);
				objectOutput.writeByte((byte) value);
				return;
			}
			if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
				objectOutput.writeByte(2);
				objectOutput.writeShort((short) value);
				return;
			}
			if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
				objectOutput.writeByte(4);
				objectOutput.writeInt((int) value);
				return;
			}
		}

		objectOutput.writeByte(0);
		objectOutput.writeObject(fInteger);
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		byte attributeFlags = objectInput.readByte();
		int value;
		switch (attributeFlags) {
		case 1:
			value = objectInput.readByte();
			fInteger = BigInteger.valueOf(value);
			return;
		case 2:
			value = objectInput.readShort();
			fInteger = BigInteger.valueOf(value);
			return;
		case 4:
			value = objectInput.readInt();
			fInteger = BigInteger.valueOf(value);
			return;
		}
		fInteger = (BigInteger) objectInput.readObject();
	}
}