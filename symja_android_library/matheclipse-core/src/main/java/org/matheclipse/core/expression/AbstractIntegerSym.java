package org.matheclipse.core.expression;

import static java.math.RoundingMode.CEILING;
import java.io.Externalizable;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.combinatoric.BinomialCache;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import com.google.common.math.BigIntegerMath;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import edu.jas.arith.PrimeInteger;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Abstract base class for IntegerSym and BigIntegerSym
 *
 * @see IntegerSym
 * @see BigIntegerSym
 */
public abstract class AbstractIntegerSym implements IInteger, Externalizable {

  public static final int[] FIBONACCI_45 = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377,
  610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811,
  514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169,
  63245986, 102334155, 165580141, 267914296, 433494437, 701408733, 1134903170};

  /**
   * Integer logarithm of <code>arg</code> for base <code>b</code>. Gives Log <sub>b</sub>(arg) or
   * <code>Log(arg)/Log(b)</code> as a {@link IRational} number if the result is rational
   *
   * @param b the base of the logarithm
   * @param arg
   * @return {@link F#NIL} if the result is not rational.
   */
  public static IExpr baseBLog(final IInteger b, final IInteger arg) {
    try {
      long l1 = b.toLong();
      long l2 = arg.toLong();
      if (l1 > 0L && l2 > 0L) {
        boolean inverse = false;
        if (l1 > l2) {
          long t = l2;
          l2 = l1;
          l1 = t;
          inverse = true;
        }
        double numericResult = Math.log(l2) / Math.log(l1);
        if (F.isNumIntValue(numericResult)) {
          long symbolicResult = DoubleMath.roundToLong(numericResult, Config.ROUNDING_MODE);
          if (inverse) {
            if (b.equals(arg.powerRational(symbolicResult))) {
              // cross checked result
              return F.QQ(1L, symbolicResult);
            }
          } else {
            if (arg.equals(b.powerRational(symbolicResult))) {
              // cross checked result
              return F.ZZ(symbolicResult);
            }
          }
        }
      }
    } catch (ArithmeticException ae) {
      // toLong() method failed
    }
    return F.NIL;
  }

  /**
   * Calculate integer binomial number. See definitions by
   * <a href="https://arxiv.org/abs/1105.3689">Kronenburg 2011</a>
   *
   * @param n
   * @param k
   * @return
   */
  public static IInteger binomial(final IInteger n, final IInteger k)
      throws BigIntegerLimitExceeded {
    if (k.isZero() || k.equals(n)) {
      return F.C1;
    }

    if (!n.isNegative() && !k.isNegative()) {
      // k>n : by definition --> 0
      if (k.compareTo(n) > 0) {
        return F.C0;
      }

      int ni = n.toIntDefault(-1);
      if (ni >= 0) {
        int ki = k.toIntDefault(-1);
        if (ki >= 0) {
          if (ki > ni) {
            return F.C0;
          }

          long bits = LongMath.log2(ni, CEILING) * ki;
          if (bits < Config.MAX_BIT_LENGTH) {
            return binomialBigInteger(ni, ki);
          } else {
            BigIntegerLimitExceeded.throwIt(bits);
          }
        }
      }

      IInteger bin = F.C1;
      IInteger i = F.C1;
      while (!(i.compareTo(k) > 0)) {
        bin = bin.multiply(n.subtract(i).add(F.C1)).div(i);
        i = i.add(F.C1);
      }
      return bin;
    } else if (n.isNegative()) {
      // see definitions at https://arxiv.org/abs/1105.3689
      if (!k.isNegative()) {
        // (-1)^k * Binomial(-n+k-1, k)
        IInteger factor = k.isOdd() ? F.CN1 : F.C1;
        return binomial(n.negate().add(k).add(F.CN1), k).multiply(factor);
      }
      if (n.compareTo(k) >= 0) {
        // (-1)^(n-k) * Binomial(-k-1, n-k)
        IInteger factor = n.subtract(k).isOdd() ? F.CN1 : F.C1;
        return binomial(k.add(F.C1).negate(), n.subtract(k)).multiply(factor);
      }
    }
    return F.C0;
  }

  public static IInteger binomial(final int n, final int k) {
    return binomial(valueOf(n), valueOf(k));
  }

  private static IInteger binomialBigInteger(int n, int k) {
    IInteger binomial;
    if (n <= BinomialCache.MAX_N && k <= BinomialCache.MAX_N) {
      binomial = BinomialCache.binomial(n, k);
    } else {
      binomial = F.ZZ(BigIntegerMath.binomial(n, k));
    }
    return binomial;
  }

  public static org.matheclipse.core.interfaces.IInteger catalanNumber(org.matheclipse.core.interfaces.IInteger n) {
    if (n.equals(F.CN1)) {
      return F.CN1;
    }
    n = n.add(F.C1);
    if (n.isPositive()) {
      org.matheclipse.core.interfaces.IInteger i = F.C1;
      org.matheclipse.core.interfaces.IInteger c = F.C1;
      final org.matheclipse.core.interfaces.IInteger temp1 = n.shiftLeft(1).subtract(F.C1);
      while (i.compareTo(n) < 0) {
        c = c.multiply(temp1.subtract(i)).div(i);
        i = i.add(F.C1);
      }
      return c.div(n);
    }
    return F.C0;
  }

  protected static IAST factorBigInteger(BigInteger number, boolean isNegative, int rootNumerator,
      int rootDenominator, Int2IntMap map) {
    if (number.compareTo(BigInteger.valueOf(7)) <= 0) {
      return F.NIL;
    }
    if (number.bitLength() > Config.MAX_BIT_LENGTH / 100) {
      BigIntegerLimitExceeded.throwIt(number.bitLength());
    }
    BigInteger rest = Primality.countPrimes32749(number, map);
    if (map.size() == 0) {
      return F.NIL;
    }
    final IASTAppendable result = F.TimesAlloc(map.size() + 4);
    boolean evaled = false;
    for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
      int key = entry.getIntKey();
      int value = entry.getIntValue();
      int mod = value % rootDenominator;
      int div = value / rootDenominator;
      if (div != 0) {
        result.append(F.Power(valueOf(key), F.ZZ(div)));
        if (mod != 0) {
          result.append(F.Power(valueOf(key), F.QQ(mod, rootDenominator)));
        }
        evaled = true;
      } else {
        result.append(F.Power(F.Power(valueOf(key), valueOf(value)), F.QQ(1, rootDenominator)));
      }
    }
    if (rootDenominator == 2 && rootNumerator == 1
        && rest.compareTo(BigInteger.valueOf(Short.MAX_VALUE - 20)) > 0) {
      // exponent 1/2 ==> special case - try to get exact square root of rest
      IInteger[] sr = F.ZZ(rest).sqrtAndRemainder();
      if (sr != null && sr[1].isZero()) {
        result.append(sr[0]);
        rest = BigInteger.ONE;
        evaled = true;
      }
    }

    if (evaled) {
      if (!rest.equals(BigInteger.ONE)) {
        result.append(F.Power(valueOf(rest), F.QQ(1, rootDenominator)));
      }
      if (isNegative) {
        result.append(F.Power(F.CN1, F.QQ(rootNumerator, rootDenominator)));
      }
      return result;
    }
    return F.NIL;
  }

  public static org.matheclipse.core.interfaces.IInteger factorial(int n) {
    final int absN = Math.abs(n);
    final int iterationLimit = EvalEngine.get().getIterationLimit();
    if (iterationLimit >= 0 && iterationLimit < absN) {
      IterationLimitExceeded.throwIt(absN, F.Factorial(F.ZZ(n)));
    }
  
    BigInteger result;
    if (absN <= 20) {
      result = BigInteger.valueOf(LongMath.factorial(absN));
    } else {
      result = BigIntegerMath.factorial(absN);
    }
  
    if (n < 0 && n % 2 != 0) {
      result = result.negate();
    }
  
    return valueOf(result);
  }

  public static IAST factorizeLong(long value) {
    int allocSize = 0;
    long longValue = value;
    if (longValue < 0) {
      allocSize = 1;
      longValue = -longValue;
    }
    Map<Long, Integer> map = PrimeInteger.factors(longValue);
    for (Map.Entry<Long, Integer> entry : map.entrySet()) {
      allocSize += entry.getValue();
    }
    IASTAppendable result = F.ListAlloc(allocSize);
    if (value < 0) {
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

  /**
   * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time. See:
   * <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code:
   * Fibonacci sequence.</a>
   *
   * @param iArg
   * @return
   */
  public static org.matheclipse.core.interfaces.IInteger fibonacci(int iArg) {
    int temp = iArg;
    if (temp < 0) {
      temp *= (-1);
    }
    if (temp < AbstractIntegerSym.FIBONACCI_45.length) {
      int result = AbstractIntegerSym.FIBONACCI_45[temp];
      if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) {
        return F.ZZ(-result);
      }
      return F.ZZ(result);
    }
  
    BigInteger a = BigInteger.ONE;
    BigInteger b = BigInteger.ZERO;
    BigInteger c = BigInteger.ONE;
    BigInteger d = BigInteger.ZERO;
    BigInteger result = BigInteger.ZERO;
    while (temp != 0) {
      if ((temp & 0x00000001) == 0x00000001) { // odd?
        d = result.multiply(c);
        result = a.multiply(c).add(result.multiply(b).add(d));
        if (result.bitLength() > Config.MAX_BIT_LENGTH) {
          BigIntegerLimitExceeded.throwIt(result.bitLength());
        }
        a = a.multiply(b).add(d);
      }
  
      d = c.multiply(c);
      c = b.multiply(c).shiftLeft(1).add(d);
      b = b.multiply(b).add(d);
      temp >>= 1;
    }
  
    if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) { // even
      return F.ZZ(result.negate());
    }
    return F.ZZ(result);
  }

  /**
   * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if {@code a == 0 && b ==
   * 0}.
   * <p>
   * See: <a href="https://medium.com/@m.langer798/stein-vs-stein-on-the-jvm-c911809bfce1">GCD:
   * Stein vs. Stein on the JVM</a>
   * 
   * @param p
   * @param q
   * @return
   */
  public static long gcd(int p, int q) {
    if (p == Integer.MIN_VALUE || q == Integer.MIN_VALUE) {
      long pl = p;
      long ql = q;
      return LongMath.gcd(pl < 0L ? -pl : pl, ql < 0L ? -ql : ql);
    }
    return IntMath.gcd(p < 0 ? -p : p, q < 0 ? -q : q);
  }

  public static BigInteger jacobiSymbol(BigInteger a, BigInteger b) {
    if (a.equals(BigInteger.ONE)) {
      return BigInteger.ONE;
    }
    if (a.signum() == 0) {
      return BigInteger.ZERO;
    }
    if (a.equals(IInteger.BI_TWO)) {
      return BigIntegerSym.jacobiSymbolF(b);
    }
    if (!NumberUtil.isOdd(a)) {
      BigInteger aDIV2 = a.shiftRight(1);
      return jacobiSymbol(aDIV2, b).multiply(jacobiSymbol(IInteger.BI_TWO, b));
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
    if (i0.signum() == 0 && i1.signum() == 0) {
      return BigInteger.ZERO;
    }
    BigInteger a = i0.abs();
    BigInteger b = i1.abs();
    BigInteger gcd = i0.gcd(b);
    BigInteger lcm = (a.multiply(b)).divide(gcd);
    return lcm;
  }

  /**
   * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
   *
   * @param kArray the non-negative coefficients
   * @param n the sum of the non-negative coefficients
   */
  public static org.matheclipse.core.interfaces.IInteger multinomial(final int[] kArray, final int n) {
    org.matheclipse.core.interfaces.IInteger pPlus = F.C1;
    org.matheclipse.core.interfaces.IRational pNeg = F.C1;
    int nNeg = 0;
    for (int k : kArray) {
      if (k != 0) {
        if (k < 0) {
          nNeg++;
          int temp = -1 - k;
          pNeg = pNeg.divideBy(factorial(temp));
          if ((temp & 1) == 1) {
            pNeg = pNeg.negate();
          }
        } else {
          pPlus = pPlus.multiply(factorial(k));
        }
      }
    }
    if (n < 0) {
      nNeg--;
      if (nNeg > 0) {
        return F.C0;
      }
      int kFactor = -1 - n;
      org.matheclipse.core.interfaces.IRational p = pPlus.multiply(pNeg).multiply(factorial(kFactor));
      if ((kFactor & 1) == 1) {
        p = p.negate();
      }
      return p.isNegative() ? p.denominator().negate() : p.denominator();
    }
    if (nNeg > 0) {
      return F.C0;
    }
    org.matheclipse.core.interfaces.IInteger result = factorial(n).div(pPlus);
    return result;
  }

  /**
   * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
   *
   * @param kArray non-negative coefficients
   * @return
   */
  public static org.matheclipse.core.interfaces.IInteger multinomial(org.matheclipse.core.interfaces.IInteger[] kArray) {
    if (kArray == null || kArray.length == 0) {
      return F.C1;
    }
    org.matheclipse.core.interfaces.IInteger n = F.C0;
    for (int i = 0; i < kArray.length; i++) {
      n = n.add(kArray[i]);
    }
    int ni = n.toIntDefault();
    if (F.isNotPresent(ni)) {
      return null;
    }
    int[] kIntArray = new int[kArray.length];
    for (int i = 0; i < kArray.length; i++) {
      kIntArray[i] = kArray[i].toIntDefault();
      if (F.isNotPresent(kIntArray[i])) {
        return null;
      }
    }
    return AbstractIntegerSym.multinomial(kIntArray, ni);
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

  public static IInteger valueOf(final int newnum) {
    if (newnum >= IntegerSym.LOW && newnum <= IntegerSym.HIGH) {
      return IntegerSym.CACHE[newnum + (-IntegerSym.LOW)];
    }
    if (newnum == Integer.MIN_VALUE) {
      return new BigIntegerSym(newnum);
    }
    if (newnum == 1000) {
      return F.C1000;
    }
    return new IntegerSym(newnum);
  }

  public static IInteger valueOf(final long newnum) {
    if (Integer.MIN_VALUE < newnum && newnum <= Integer.MAX_VALUE) {
      return valueOf((int) newnum);
    }
    return new BigIntegerSym(newnum);
  }

  /**
   * Returns the IInteger for the specified character sequence stated in the specified radix. The
   * characters must all be digits of the specified radix, except the first character which may be a
   * plus sign <code>'+'</code> or a minus sign <code>'-'</code> .
   *
   * @param integerString the character sequence to parse.
   * @param radix the radix to be used while parsing.
   * @return the corresponding large integer.
   * @throws NumberFormatException if the specified character sequence does not contain a parsable
   *         large integer.
   */
  public static IInteger valueOf(final String integerString, final int radix) {
    final int length = integerString.length();
    if (radix == 10) {
      if (length == 1) {
        int digit = Character.digit(integerString.charAt(0), radix);
        switch (digit) {
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
        }
      } else if (length == 2 && integerString.charAt(0) == '-') {
        int digit = Character.digit(integerString.charAt(1), radix);
        switch (digit) {
          case 0:
            return F.C0;
          case 1:
            return F.CN1;
          case 2:
            return F.CN2;
          case 3:
            return F.CN3;
          case 4:
            return F.CN4;
          case 5:
            return F.CN5;
          case 6:
            return F.CN6;
          case 7:
            return F.CN7;
          case 8:
            return F.CN8;
          case 9:
            return F.CN9;
        }
      }
    }
    try {
      int newnum = Integer.parseInt(integerString, radix);
      return valueOf(newnum);
    } catch (NumberFormatException nfe) {
      // probably a big integer?
    }
    return new BigIntegerSym(new BigInteger(integerString, radix));
  }

  public static IInteger valueOfUniqueReference(final int newnum) {
    if (newnum == Integer.MIN_VALUE) {
      return new BigIntegerSym(newnum);
    }
    return new IntegerSym(newnum);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
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

  // /** {@inheritDoc} */
  // @Override
  // public IInteger gcd(final IInteger that) {
  // return gcd( that);
  // }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(new Apfloat(toBigNumerator(), EvalEngine.getApfloat().precision()));
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(toBigNumerator());
  }

  @Override
  public Apfloat apfloatValue() {
    return new Apint(toBigNumerator());
    // return new Apfloat(toBigNumerator(), EvalEngine.getApfloat().precision());
  }

  @Override
  public IInteger ceil() {
    return this;
  }

  @Override
  public IInteger ceilFraction() {
    return this;
  }

  // private IAST factorizeInt(int intValue) {
  // IASTAppendable result = F.ListAlloc();// tdivFactors.size() + extraSize);
  // if (intValue < 0) {
  // intValue *= -1;
  // result.append(F.CN1);
  // }
  // TDiv31Barrett TDIV31 = new TDiv31Barrett();
  // int prime = TDIV31.findSingleFactor(intValue);
  // while (true) {
  // prime = TDIV31.findSingleFactor(intValue);
  // intValue /= prime;
  // if (prime != 1) {
  // result.append(prime);
  // } else {
  // break;
  // }
  // }
  // if (intValue != 1) {
  // SortedMultiset<BigInteger> tdivFactors = TDIV31.factor(BigInteger.valueOf(intValue));
  // for (Map.Entry<BigInteger, Integer> entry : tdivFactors.entrySet()) {
  // final IInteger is = valueOf(entry.getKey());
  // final int value = entry.getValue();
  // for (int i = 0; i < value; i++) {
  // result.append(is);
  // }
  // }
  // }
  // return result;
  // }

  @Override
  public IInteger charmichaelLambda() {
    return AbstractIntegerSym.valueOf(Primality.charmichaelLambda(toBigNumerator()));
  }

  @Override
  public void checkBitLength() {
    final long bitLength = bitLength();
    if (bitLength > Config.MAX_BIT_LENGTH) {
      BigIntegerLimitExceeded.throwIt(bitLength);
    }
  }

  @Override
  public int compareTo(final IExpr expr) {
    if (expr.isNumber() && !expr.isReal()) {
      int c = this.compareTo(expr.re());
      if (c != 0) {
        return c;
      }
      IExpr im = expr.im();
      return im.isPositive() ? -1 : im.isNegative() ? 1 : IExpr.compareHierarchy(this, expr);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IRational divideBy(IRational that) {
    return AbstractFractionSym.valueOf(this).divideBy(that);
  }

  @Override
  public IAST divisors() {
    if (isOne() || isMinusOne()) {
      return F.CListC1;
    }
    Set<IInteger> set = divisorsSet();
    return F.mapSet(set, divisor -> divisor);
  }

  /**
   * Bottom-up divisors construction algorithm. Slightly faster than top-down.
   *
   * @return the set of divisors of the number thats prime factorization is calculated
   */
  private SortedSet<IInteger> divisorsSet() throws ASTElementLimitExceeded {
    IAST factors = factorInteger();
    if (factors.size() == 1) {
      TreeSet<IInteger> treeSet = new TreeSet<IInteger>();
      treeSet.add(F.C1);
      return treeSet;
    }

    ArrayList<IInteger> primes = new ArrayList<>();
    IntList maxPowers = new IntArrayList();
    for (int i = 1; i < factors.size(); i++) {
      IExpr arg = factors.get(i);
      primes.add((IInteger) arg.first());
      maxPowers.add(arg.second().toIntDefault());
    }

    TreeSet<IInteger> divisors = new TreeSet<IInteger>();
    if (primes.size() == 0 || (primes.size() == 1 && primes.get(0).equals(F.C0))) {
      return divisors;
    }

    Stack<IntArrayList> stack = new Stack<IntArrayList>();
    IntArrayList emptyPowers = new IntArrayList();
    for (int i = 0; i < maxPowers.size(); i++) {
      emptyPowers.add(0);
    }
    stack.push(emptyPowers);

    while (!stack.isEmpty()) {
      IntArrayList powers = stack.pop();
      // compute divisor from stack element
      IInteger divisor = F.C1;
      for (int i = 0; i < powers.size(); i++) {
        int power = powers.getInt(i);
        if (power > 0) {
          // multiply entry to divisor
          divisor = divisor.multiply(primes.get(i).powerRational(power));
        }
      }
      if (divisors.add(divisor)) {
        if (Config.MAX_AST_SIZE < divisors.size()) {
          ASTElementLimitExceeded.throwIt(divisors.size());
        }
        for (int i = 0; i < maxPowers.size(); i++) {
          int maxPower = maxPowers.getInt(i);
          int power = powers.getInt(i);
          if (power < maxPower) {
            // create new entry
            IntArrayList enhancedPowers = new IntArrayList(powers); // copy
            enhancedPowers.set(i, power + 1);
            stack.push(enhancedPowers);
          }
        }
      }
    }
    return divisors;
  }

  /**
   * IntegerSym extended greatest common divisor.
   *
   * @param that if that is of type IntegerSym calculate the extended GCD otherwise call <code>
   *     super#egcd(IExpr)</code>
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
      while (r.signum() != 0) {
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

  @Override
  public IInteger eulerPhi() throws ArithmeticException {
    return AbstractIntegerSym.valueOf(Primality.eulerPhi(toBigNumerator()));
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
  public IInteger factorial() {
    int ni = toIntDefault();
    if (ni > Integer.MIN_VALUE) {
      return AbstractIntegerSym.factorial(ni);
    }
    // Machine-sized integer expected at position `2` in `1`.
    throw new ArgumentTypeException("intm", F.list(F.Factorial(this), F.C1));
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
   * Get all prime factors of this integer.
   *
   * @return
   */
  public IAST factorize() {
    return Config.PRIME_FACTORS.factorIInteger(this);
  }

  /** {@inheritDoc} */
  @Override
  public IAST factorSmallPrimes(int rootNumerator, int rootDenominator) {
    IInteger b = this;
    boolean isNegative = false;
    if (complexSign() < 0) {
      b = b.negate();
      isNegative = true;
    }
    if (rootNumerator != 1) {
      b = b.powerRational(rootNumerator);
    }
    if (b.isLT(F.C8)) {
      return F.NIL;
    }

    BigInteger number = b.toBigNumerator();
    Int2IntMap map = new Int2IntRBTreeMap();
    return factorBigInteger(number, isNegative, rootNumerator, rootDenominator, map);
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
  public IRational fractionalPart() {
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<IInteger[]> gaussianIntegers() {
    return Optional.of(new IInteger[] {this, F.C0});
  }

  /** {@inheritDoc} */
  @Override
  public IExpr gcd(IExpr that) {
    if (that instanceof IInteger) {
      return gcd((IInteger) that);
    }
    if (that instanceof IFraction) {
      ((IFraction) that).gcd(F.fraction(toBigNumerator(), BigInteger.ONE));
    }
    return F.C1;
  }

  // public static BigInteger jacobiSymbol(long a, long b) {
  // return jacobiSymbol(BigInteger.valueOf(a), BigInteger.valueOf(b));
  // }

  /** {@inheritDoc} */
  @Override
  public IRational gcd(IRational that) {
    if (that.isZero()) {
      return this;
    }
    if (this.isZero()) {
      return that;
    }
    if (that.isInteger()) {
      return gcd((IInteger) that);
    }
    BigInteger tdenom = this.toBigDenominator();
    BigInteger odenom = that.toBigDenominator();
    BigInteger gcddenom = tdenom.gcd(odenom);
    BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
    BigInteger num = toBigNumerator().gcd(that.toBigNumerator());
    return AbstractFractionSym.valueOf(num, denom);
  }

  @Override
  public IRational getImaginaryPart() {
    return F.C0;
  }

  @Override
  public IRational getRealPart() {
    return this;
  }

  @Override
  public ISymbol head() {
    return S.Integer;
  }

  @Override
  public int hierarchy() {
    return INTEGERID;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return this;
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public abstract IRational inverse();

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
   * See: <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a><br>
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
    if (b.isProbablePrime()) {
      IInteger mod = mod(b);
      if (mod.isZero()) {
        return F.C0;
      }
      // PowerMod(a, (b - 1)/2, b) == 1

      // (b - 1) / 2
      IInteger quotient = b.subtract(F.C1).quotient(F.C2);
      IInteger modPow = modPow(quotient, b);
      if (modPow.isOne()) {
        return F.C1;
      }
      return F.CN1;

    }
    if (b.isOne()) {
      return F.C1;
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

  /** Returns the least common multiple of this large integer and the one specified. */
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
  public long leafCountSimplify() {
    if (isZero()) {
      return 1;
    }
    return integerLength(F.C10) + (isPositive() ? 0 : 1);
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
   * Split this integer into the nth-root (with prime factors less equal 1021) and the
   * &quot;rest-factor&quot;, so that <code>this== (nth-root ^ n) + rest</code>
   *
   * @return <code>{nth-root, rest}</code>
   */
  @Override
  public IInteger[] nthRootSplit(int n) throws ArithmeticException {
    IInteger[] result = new IInteger[2];
    if (complexSign() == 0) {
      result[0] = F.C0;
      result[1] = F.C1;
      return result;
    } else if (complexSign() < 0) {
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
    if (that instanceof INumber) {
      return this.plus((INumber) that);
    }
    return IInteger.super.plus(that);
  }

  @Override
  public INumber plus(final INumber that) {
    if (isZero()) {
      return that;
    }
    if (that.isZero()) {
      if (that.isInexactNumber()) {
        return numericNumber();
      }
      return this;
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
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().add(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value + evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IInteger) {
      if (that.isZero()) {
        if (!this.isZero()) {
          return F.C1;
        }
        return IInteger.super.power(that);
      } else if (that.isOne()) {
        return this;
      } else if (that.isMinusOne()) {
        return inverse();
      }
      long n = that.toLongDefault();
      if (F.isPresent(n)) {
        return power(n);
      }
    }
    return IInteger.super.power(that);
  }

  /** {@inheritDoc} */
  @Override
  public final IInteger powerRational(final long exponent) throws ArithmeticException {
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
      } catch (MathRuntimeException mrex) {
        // result doesn't fit into a Java long
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
        r.checkBitLength();
        r = r.multiply(x);
      }
    }

    while (b2pow-- > 0L) {
      r.checkBitLength();
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
    int size = phi.eulerPhi().toIntDefault();
    if (size <= 0) {
      return null;
    }
    if (isEven() && !equals(F.C2) && !equals(F.C4)) {
      if (quotient(F.C2).isEven()) {
        return new IInteger[0];
      }
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

    if (Config.MAX_AST_SIZE < size) {
      throw new ASTElementLimitExceeded(size);
    }
    IInteger resultArray[] = new IInteger[size];
    boolean b;
    while (m.compareTo(n) < 0) {
      b = m.gcd(n).isOne();
      for (int i = 0; i < d.length; i++) {
        b = b && m.modPow(d[i], n).isGT(F.C1);
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
    if (mod.signum() == 0) {
      return valueOf(quotient);
    }
    if (quotient.signum() < 0) {
      return valueOf(quotient.subtract(BigInteger.ONE));
    }
    return valueOf(quotient);
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public IRational roundClosest(IReal multiple) {
    if (!multiple.isRational()) {
      multiple = F.fraction(multiple.doubleValue(), Config.DOUBLE_EPSILON);
    }
    IInteger ii = this.divideBy((IRational) multiple).roundExpr();
    return ii.multiply((IRational) multiple);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger[] sqrtAndRemainder() {
    if (complexSign() > 0) {
      // TODO BigInteger#sqrtAndRemainder() introduced in Android API level 33
      // https://developer.android.com/reference/java/math/BigInteger#sqrtAndRemainder()
      BigInteger bignum = toBigNumerator();
      BigInteger s = BigIntegerMath.sqrt(bignum, RoundingMode.FLOOR);
      BigInteger r = bignum.subtract(s.multiply(s));
      return new IInteger[] {valueOf(s), valueOf(r)};
    }
    return null;
  }

  @Override
  public IRational subtract(final IRational that) {
    if (isZero()) {
      return that.negate();
    }
    return this.add(that.negate());
  }

  @Override
  public IReal subtractFrom(IReal that) {
    if (that instanceof IRational) {
      return this.add((IRational) that.negate());
    }
    return Num.valueOf(doubleValue() - that.doubleValue());
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return this.times((INumber) that);
    }
    return IInteger.super.times(that);
  }

  @Override
  public INumber times(final INumber that) {
    if (isOne()) {
      return that;
    }
    if (isMinusOne()) {
      return that.negate();
    }
    if (that.isOne()) {
      if (that.isInexactNumber()) {
        return numericNumber();
      }
      return this;
    }
    if (that.isMinusOne()) {
      if (that.isInexactNumber()) {
        return negate().numericNumber();
      }
      return negate();
    }
    if (isZero() || that.isZero()) {
      if (that.isInexactNumber()) {
        return F.CD0;
      }
      return F.C0;
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
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().multiply(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value * evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public byte[] toByteArray() {
    return toBigNumerator().toByteArray();
  }
}
