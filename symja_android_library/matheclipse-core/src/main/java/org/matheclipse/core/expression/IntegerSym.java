package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.F.List;

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
import org.apache.commons.math4.util.ArithmeticUtils;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.reflection.system.Subsets;
import org.matheclipse.core.reflection.system.Subsets.KSubsetsList;

import com.google.common.math.BigIntegerMath;

/**
 * IInteger implementation which uses an internal <code>int</code> value
 * 
 * @see AbstractIntegerSym
 * @see BigIntegerSym
 */
public class IntegerSym extends AbstractIntegerSym {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6389228668633533063L;

	/* package private */int fIntValue;

	/**
	 * do not use directly, needed for serialization/deserialization
	 * 
	 */
	public IntegerSym() {
		fIntValue = 0;
	}

	/**
	 * do not use directly, needed for serialization/deserialization
	 * 
	 */
	public IntegerSym(int value) {
		fIntValue = value;
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public AbstractIntegerSym add(final AbstractIntegerSym that) {
		if (fIntValue == 0) {
			return that;
		}
		if (that instanceof BigIntegerSym) {
			return ((BigIntegerSym) that).add(this);
		}
		IntegerSym is = (IntegerSym) that;
		if (is.fIntValue == 0) {
			return this;
		}
		return valueOf((long) fIntValue + is.fIntValue);
	}

	/**
	 * @param val
	 * @return
	 */
	@Override
	public IInteger add(final IInteger val) {
		return add((AbstractIntegerSym) val);
	}

	@Override
	public IRational add(IRational parm1) {
		if (parm1.isZero()) {
			return this;
		}
		if (parm1 instanceof AbstractFractionSym) {
			return ((AbstractFractionSym) parm1).add(this);
		}
		if (parm1 instanceof IntegerSym) {
			IntegerSym is = (IntegerSym) parm1;
			long newnum = (long) fIntValue + (long) is.fIntValue;
			return valueOf(newnum);
		}
		BigIntegerSym p1 = (BigIntegerSym) parm1;
		BigInteger newnum = getBigNumerator().add(p1.getBigNumerator());
		return valueOf(newnum);
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		long num = fIntValue;
		if (fIntValue < 0) {
			num *= (-1);
		}
		if (num == 1L) {
			return 0;
		}
		return (num > 0) ? 1 : -1;
	}

	@Override
	public int compareInt(final int value) {
		return (fIntValue > value) ? 1 : (fIntValue == value) ? 0 : -1;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns
	 * a negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof IntegerSym) {
			int num = ((IntegerSym) expr).fIntValue;
			return fIntValue < num ? -1 : num == fIntValue ? 0 : 1;
		}
		if (expr instanceof BigIntegerSym) {
			return -expr.compareTo(this);
		}
		if (expr instanceof AbstractFractionSym) {
			return -((AbstractFractionSym) expr).compareTo(AbstractFractionSym.valueOf(fIntValue));
		}
		if (expr instanceof Num) {
			double d = (fIntValue) - ((Num) expr).getRealPart();
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
	public ComplexNum complexNumValue() {
		// double precision complex number
		return ComplexNum.valueOf(doubleValue());
	}

	@Override
	public int complexSign() {
		return sign();
	}

	@Override
	public AbstractIntegerSym div(final AbstractIntegerSym that) {
		return valueOf(getBigNumerator().divide(that.getBigNumerator()));
	}

	/** {@inheritDoc} */
	@Override
	public IInteger[] divideAndRemainder(final IInteger that) {
		final AbstractIntegerSym[] res = new IntegerSym[2];
		BigInteger[] largeRes = getBigNumerator().divideAndRemainder(that.getBigNumerator());
		res[0] = valueOf(largeRes[0]);
		res[1] = valueOf(largeRes[1]);

		return res;
	}

	@Override
	public IRational divideBy(IRational that) {
		return AbstractFractionSym.valueOf(fIntValue).divideBy(that);
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		if (that instanceof IntegerSym) {
			return AbstractFractionSym.valueOf(fIntValue).divideBy(that);
		}
		if (that instanceof AbstractFractionSym) {
			return AbstractFractionSym.valueOf(fIntValue).divideBy(that);
		}
		return Num.valueOf((fIntValue) / that.doubleValue());
	}

	/**
	 * Return the divisors of this integer number.
	 * 
	 * <pre>
	 * divisors(24) ==> {1,2,3,4,6,8,12,24}
	 * </pre>
	 */
	@Override
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

	/**
	 * @return
	 */
	@Override
	public double doubleValue() {
		return fIntValue;
	}

	/** {@inheritDoc} */
	@Override
	public AbstractIntegerSym eabs() {
		if (fIntValue < 0) {
			return valueOf(fIntValue * (-1L));
		}
		return this;
	}

	/**
	 * IntegerSym extended greatest common divisor.
	 * 
	 * @param that
	 *            if that is of type IntegerSym calculate the extended GCD
	 *            otherwise call {@link super#egcd(IExpr)};
	 * 
	 * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
	 */
	@Override
	public IExpr[] egcd(IExpr that) {
		if (that instanceof IntegerSym) {
			BigInteger S = ((IntegerSym) that).getBigNumerator();
			IInteger[] result = new IInteger[3];
			result[0] = null;
			result[1] = F.C1;
			result[2] = F.C1;
			if (that == null || that.isZero()) {
				result[0] = (this);
				return result;
			}
			if (this.isZero()) {
				result[0] = ((IntegerSym) that);
				return result;
			}
			BigInteger[] qr;
			BigInteger q = getBigNumerator();
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntegerSym) {
			return fIntValue == ((IntegerSym) obj).fIntValue;
		}
		if (obj instanceof BigIntegerSym) {
			return ((BigIntegerSym) obj).equalsInt(fIntValue);
		}
		return false;
	}

	@Override
	public final boolean equalsFraction(final int numerator, final int denominator) {
		return denominator == 1 && fIntValue == numerator;
	}

	@Override
	public boolean equalsInt(final int value) {
		return fIntValue == value;
	}

	/**
	 * Get the highest exponent of <code>base</code> that divides
	 * <code>this</code>
	 * 
	 * @return the exponent
	 */
	@Override
	public IExpr exponent(IInteger base) {
		IntegerSym b = this;
		if (sign() < 0) {
			b = b.negate();
		} else if (b.isZero()) {
			return F.CInfinity;
		} else if (b.isOne()) {
			return F.C0;
		}
		if (b.equals(base)) {
			return F.C1;
		}
		BigInteger rest = Primality.countExponent(b.getBigNumerator(), base.getBigNumerator());
		return valueOf(rest);
	}

	/** {@inheritDoc} */
	@Override
	public IAST factorInteger() {
		IInteger factor;
		IInteger last = AbstractIntegerSym.valueOf(-2);
		int count = 0;
		final IAST iFactors = factorize(F.List());
		final IAST list = List();
		IAST subList = null;
		for (int i = 1; i < iFactors.size(); i++) {
			factor = (IInteger) iFactors.get(i);
			if (!last.equals(factor)) {
				if (subList != null) {
					subList.add(AbstractIntegerSym.valueOf(count));
					list.add(subList);
				}
				count = 0;
				subList = List(factor);
			}
			count++;
			last = factor;
		}
		if (subList != null) {
			subList.add(AbstractIntegerSym.valueOf(count));
			list.add(subList);
		}
		return list;
	}

	/**
	 * Get all prime factors of this integer
	 * 
	 * @return
	 */
	public IAST factorize(IAST result) {
		// final ArrayList<IInteger> result = new ArrayList<IInteger>();
		AbstractIntegerSym b = this;
		if (sign() < 0) {
			b = b.multiply(AbstractIntegerSym.valueOf(-1));
			result.add(AbstractIntegerSym.valueOf(-1));
		} else if (b.getBigNumerator().equals(BigInteger.ZERO)) {
			result.add(AbstractIntegerSym.valueOf(0));
			return result;
		} else if (b.getBigNumerator().equals(BigInteger.ONE)) {
			result.add(AbstractIntegerSym.valueOf(1));
			return result;
		}
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		BigInteger rest = Primality.countPrimes32749(b.getBigNumerator(), map);

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			int key = entry.getKey();
			IntegerSym is = AbstractIntegerSym.valueOf(key);
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
			AbstractIntegerSym is = valueOf(key);
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
	 * Returns the greatest common divisor of this large integer and the one
	 * specified.
	 * 
	 */
	@Override
	public AbstractIntegerSym gcd(final AbstractIntegerSym that) {
		if (that instanceof IntegerSym) {
			return valueOf(ArithmeticUtils.gcd(fIntValue, ((IntegerSym) that).fIntValue));
		}
		return valueOf(getBigNumerator().gcd(that.getBigNumerator()));
	}

	/** {@inheritDoc} */
	@Override
	public BigInteger getBigDenominator() {
		return BigInteger.ONE;
	}

	/** {@inheritDoc} */
	@Override
	public BigInteger getBigNumerator() {
		return BigInteger.valueOf(fIntValue);
	}

	/** {@inheritDoc} */
	@Override
	public IInteger getDenominator() {
		return F.C1;
	}

	/** {@inheritDoc} */
	@Override
	public BigFraction getFraction() {
		return new BigFraction(fIntValue);
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getIm() {
		return F.C0;
	}

	/** {@inheritDoc} */
	@Override
	public IInteger getNumerator() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getRe() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return fIntValue;
	}

	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		int value = NumberUtil.toInt(fIntValue);
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
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true);
	}

	@Override
	public int intValue() {
		return fIntValue;
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber inverse() {
		if (isOne()) {
			return this;
		}
		if (fIntValue < 0) {
			return AbstractFractionSym.valueOf(-1, -fIntValue);
		}
		return AbstractFractionSym.valueOf(1, fIntValue);
	}

	@Override
	public boolean isEven() {
		return (fIntValue & 0x00000001) == 0x00000000;
	}

	@Override
	public boolean isGreaterThan(ISignedNumber obj) {
		if (obj instanceof IntegerSym) {
			return fIntValue > ((IntegerSym) obj).fIntValue;
		}
		if (obj instanceof BigIntegerSym) {
			return getBigNumerator().compareTo(((BigIntegerSym) obj).getBigNumerator()) > 0;
		}
		if (obj instanceof AbstractFractionSym) {
			return -((AbstractFractionSym) obj).compareTo(AbstractFractionSym.valueOf(fIntValue, 1)) > 0;
		}
		return doubleValue() > obj.doubleValue();
	}

	/**
	 * @param that
	 * @return
	 */
	public boolean isLargerThan(final BigInteger that) {
		return getBigNumerator().compareTo(that) > 0;
	}

	@Override
	public boolean isLessThan(ISignedNumber obj) {
		if (obj instanceof IntegerSym) {
			return fIntValue < ((IntegerSym) obj).fIntValue;
		}
		if (obj instanceof BigIntegerSym) {
			return getBigNumerator().compareTo(((BigIntegerSym) obj).getBigNumerator()) < 0;
		}
		if (obj instanceof AbstractFractionSym) {
			return -((AbstractFractionSym) obj).compareTo(AbstractFractionSym.valueOf(fIntValue, 1)) < 0;
		}
		return doubleValue() < obj.doubleValue();
	}

	@Override
	public boolean isMinusOne() {
		return fIntValue == -1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return fIntValue < 0;
	}

	@Override
	public boolean isOdd() {
		return (fIntValue & 0x00000001) == 0x00000001;
	}

	@Override
	public boolean isOne() {
		return fIntValue == 1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return fIntValue > 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isProbablePrime() {
		return isProbablePrime(PRIME_CERTAINTY);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isProbablePrime(int certainty) {
		return getBigNumerator().isProbablePrime(certainty);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return equals(value);
	}

	@Override
	public boolean isZero() {
		return fIntValue == 0;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long longValue() {
		return fIntValue;
	}

	@Override
	public AbstractIntegerSym mod(final AbstractIntegerSym that) {
		return valueOf(getBigNumerator().mod(that.getBigNumerator()));
	}

	public AbstractIntegerSym modInverse(final IntegerSym m) {
		return valueOf(getBigNumerator().modInverse(m.getBigNumerator()));
	}

	@Override
	public AbstractIntegerSym modPow(final AbstractIntegerSym exp, final AbstractIntegerSym m) {
		return valueOf(getBigNumerator().modPow(exp.getBigNumerator(), m.getBigNumerator()));
	}

	@Override
	public AbstractIntegerSym moebiusMu() {
		if (this.compareTo(AbstractIntegerSym.valueOf(1)) == 0) {
			return AbstractIntegerSym.valueOf(1);
		}
		IAST ast = factorInteger();
		AbstractIntegerSym max = AbstractIntegerSym.valueOf(1);
		for (int i = 1; i < ast.size(); i++) {
			IAST element = (IAST) ast.get(i);
			AbstractIntegerSym c = (AbstractIntegerSym) element.arg2();
			if (c.compareTo(max) > 0) {
				max = c;
			}
		}
		if (max.compareTo(AbstractIntegerSym.valueOf(1)) > 0) {
			return AbstractIntegerSym.valueOf(0);
		}
		if (((ast.size() - 1) & 0x00000001) == 0x00000001) {
			// odd number
			return AbstractIntegerSym.valueOf(-1);
		}
		return AbstractIntegerSym.valueOf(1);
	}

	/**
	 * @param that
	 * @return
	 */
	@Override
	public AbstractIntegerSym multiply(final AbstractIntegerSym that) {
		switch (fIntValue) {
		case 0:
			return F.C0;
		case 1:
			return that;
		case -1:
			return that.negate();
		}
		if (that instanceof BigIntegerSym) {
			return ((BigIntegerSym) that).multiply(this);
		}
		IntegerSym is = (IntegerSym) that;
		if (is.fIntValue == 1) {
			return this;
		}
		return valueOf((long) fIntValue * is.fIntValue);
	}

	/**
	 * @param val
	 * @return
	 */
	@Override
	public IInteger multiply(final IInteger val) {
		return multiply((AbstractIntegerSym) val);
	}

	@Override
	public IRational multiply(IRational parm1) {
		if (parm1.isZero()) {
			return F.C0;
		}
		if (parm1.isOne()) {
			return this;
		}
		if (parm1.isMinusOne()) {
			return this.negate();
		}
		if (parm1 instanceof AbstractFractionSym) {
			return ((AbstractFractionSym) parm1).multiply(this);
		}
		if (parm1 instanceof IntegerSym) {
			IntegerSym is = (IntegerSym) parm1;
			long newnum = (long) fIntValue * (long) is.fIntValue;
			return valueOf(newnum);
		}
		BigIntegerSym p1 = (BigIntegerSym) parm1;
		BigInteger newnum = getBigNumerator().multiply(p1.getBigNumerator());
		return valueOf(newnum);
	}

	@Override
	public IntegerSym negate() {
		return valueOf(-fIntValue);
	}

	@Override
	public INumber normalize() {
		return this;
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
			return AbstractIntegerSym.valueOf(0);
		} else if (sign() < 0) {
			if (n % 2 == 0) {
				// even exponent n
				throw new ArithmeticException();
			} else {
				// odd exponent n
				return negate().nthRoot(n).negate();
			}
		} else {
			IInteger result;
			IInteger temp = this;
			do {
				result = temp;
				temp = divideAndRemainder(temp.pow(n - 1))[0].add(temp.multiply(AbstractIntegerSym.valueOf(n - 1)))
						.divideAndRemainder(AbstractIntegerSym.valueOf(n))[0];
			} while (temp.compareTo(result) < 0);
			return result;
		}
	}

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021)
	 * and the &quot;rest-factor&quot;, so that
	 * <code>this== (nth-root)^n + rest</code>
	 * 
	 * @return <code>{nth-root, rest}</code>
	 */
	@Override
	public IInteger[] nthRootSplit(int n) throws ArithmeticException {
		IInteger[] result = new IInteger[2];
		if (sign() == 0) {
			result[0] = AbstractIntegerSym.valueOf(0);
			result[1] = AbstractIntegerSym.valueOf(1);
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

		IntegerSym b = this;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		BigInteger rest = Primality.countPrimes1021(b.getBigNumerator(), map);
		AbstractIntegerSym nthRoot = AbstractIntegerSym.valueOf(1);
		AbstractIntegerSym restFactors = AbstractIntegerSym.valueOf(rest);
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			IntegerSym primeLE1021 = AbstractIntegerSym.valueOf(entry.getKey());
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
	public final INumber numericNumber() {
		return F.num(this);
	}

	@Override
	public Num numValue() {
		return Num.valueOf(doubleValue());
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof AbstractIntegerSym) {
			return this.add((AbstractIntegerSym) that);
		}
		if (isZero()) {
			return that;
		}
		if (that instanceof AbstractFractionSym) {
			return AbstractFractionSym.valueOf(fIntValue).add((AbstractFractionSym) that);
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).add(ComplexSym.valueOf(this));
		}
		return super.plus(that);
	}

	@Override
	public AbstractIntegerSym quotient(final AbstractIntegerSym that) {
		return valueOf(getBigNumerator().divide(that.getBigNumerator()));
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		byte attributeFlags = objectInput.readByte();
		int value;
		switch (attributeFlags) {
		case 1:
			value = objectInput.readByte();
			fIntValue = value;
			return;
		case 2:
			value = objectInput.readShort();
			fIntValue = value;
			return;
		case 4:
			value = objectInput.readInt();
			fIntValue = value;
			return;
		}
	}

	public AbstractIntegerSym remainder(final AbstractIntegerSym that) {
		return valueOf(getBigNumerator().remainder(that.getBigNumerator()));
	}

	@Override
	public IExpr remainder(final IExpr that) {
		if (that instanceof AbstractIntegerSym) {
			return remainder((AbstractIntegerSym) that);
		}
		return this;
	}

	@Override
	public IInteger round() {
		return this;
	}

	@Override
	public int sign() {
		return (fIntValue > 0) ? 1 : (fIntValue == 0) ? 0 : -1;
	}

	/**
	 * Returns the integer square root of this integer.
	 * 
	 * @return <code>k<code> such as <code>k^2 <= this < (k + 1)^2</code>
	 * @throws ArithmeticException
	 *             if this integer is negative.
	 */
	public IInteger sqrt() throws ArithmeticException {
		return valueOf(BigIntegerMath.sqrt(getBigNumerator(), RoundingMode.UNNECESSARY));
	}

	@Override
	public IInteger subtract(final IInteger that) {
		return add(that.negate());
	}

	@Override
	public IRational subtract(IRational parm1) {
		if (parm1.isZero()) {
			return this;
		}
		if (parm1 instanceof AbstractFractionSym) {
			return ((AbstractFractionSym) parm1).negate().add(this);
		}
		if (parm1 instanceof IntegerSym) {
			IntegerSym is = (IntegerSym) parm1;
			long newnum = (long) fIntValue - (long) is.fIntValue;
			return valueOf(newnum);
		}
		BigIntegerSym p1 = (BigIntegerSym) parm1;
		BigInteger newnum = getBigNumerator().subtract(p1.getBigNumerator());
		return valueOf(newnum);
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
		if (that instanceof AbstractFractionSym) {
			return AbstractFractionSym.valueOf(fIntValue).multiply((AbstractFractionSym) that).normalize();
		}
		if (that instanceof ComplexSym) {
			return ((ComplexSym) that).multiply(ComplexSym.valueOf(this));
		}
		return super.times(that);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		return NumberUtil.toInt(fIntValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		return NumberUtil.toLong(fIntValue);
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
		return Integer.toString(fIntValue);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (fIntValue <= Byte.MAX_VALUE && fIntValue >= Byte.MIN_VALUE) {
			objectOutput.writeByte(1);
			objectOutput.writeByte((byte) fIntValue);
			return;
		}
		if (fIntValue <= Short.MAX_VALUE && fIntValue >= Short.MIN_VALUE) {
			objectOutput.writeByte(2);
			objectOutput.writeShort((short) fIntValue);
			return;
		}
		objectOutput.writeByte(4);
		objectOutput.writeInt(fIntValue);
		return;
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}

}