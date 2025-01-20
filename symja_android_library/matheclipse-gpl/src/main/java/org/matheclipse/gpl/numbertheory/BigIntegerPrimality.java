package org.matheclipse.gpl.numbertheory;

import java.math.BigInteger;
import java.util.Map;
import java.util.SortedMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.numbertheory.Primality;
import de.tilman_neumann.jml.factor.CombinedFactorAlgorithm;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;
import edu.jas.arith.PrimeInteger;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;

/** Provides primality probabilistic methods for BigInteger numbers */
public class BigIntegerPrimality extends Primality {

  private static final transient ThreadLocal<CombinedFactorAlgorithm> INSTANCE =
      ThreadLocal.withInitial(() -> {
        if (Config.JAVA_UNSAFE) {
          int cores = Runtime.getRuntime().availableProcessors();
          return new CombinedFactorAlgorithm(cores / 2 + 1, null, true);
        } else {
          return new CombinedFactorAlgorithm(1, null, false);
        }
      });

  public static CombinedFactorAlgorithm getFactorizer() {
    return INSTANCE.get();
  }

  /**
   * Decomposes the argument <code>n</code> into prime factors. The result is stored in the <code>
   * map</code> .
   *
   * @param n
   * @param sortedMap of all BigInteger primes and their associated exponents
   */
  @Override
  public void factorInteger(BigInteger n, SortedMap<BigInteger, Integer> sortedMap) {
    SortedMultiset<BigInteger> map = new SortedMultiset_BottomUp<>();
    // Do trial division by all primes < 131072.
    // TDiv tdiv = new TDiv();
    // n = tdiv.findSmallFactors(n, 131072, map);
    // if (n.equals(BigInteger.ONE)) {
    // return;
    // }

    // if (n.compareTo(BigInteger.ONE) > 0) {
    getFactorizer().factor(n, map);
    for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
      Integer value = sortedMap.get(entry.getKey());
      if (value != null) {
        entry.setValue(entry.getValue() + value);
      } else {
        sortedMap.put(entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * Decomposes the argument <code>n</code> into prime factors. The result is a multiset of
   * BigIntegers, sorted bottom-up.
   *
   * @param n
   * @return map of all BigInteger primes and their associated exponents
   */
  @Override
  public SortedMap<BigInteger, Integer> factorInteger(BigInteger n) {
    SortedMultiset<BigInteger> map = new SortedMultiset_BottomUp<>();
    // Do trial division by all primes < 131072.
    // TDiv tdiv = new TDiv();
    // n = tdiv.findSmallFactors(n, 131072, map);
    // if (n.equals(BigInteger.ONE)) {
    // return map;
    // }

    // if (n.compareTo(BigInteger.ONE) > 0) {
    getFactorizer().factor(n, map);
    // }
    return map;
  }

  @Override
  public IAST factorIInteger(IInteger b) {
    if (b.isZero()) {
      return F.CListC0;
    } else if (b.isOne()) {
      return F.CListC1;
    } else if (b.isMinusOne()) {
      return F.CListCN1;
    }
    if (b instanceof IntegerSym) {
      int intValue = b.intValue();
      return AbstractIntegerSym.factorizeLong(intValue);
    }
    boolean negative = false;
    if (b.complexSign() < 0) {
      b = b.negate();
      negative = true;
    }

    BigInteger big = b.toBigNumerator();
    try {
      if (NumberUtil.hasLongValue(big)) {
        // Android doesn't know method longValueExact
        long longValue = big.longValue();
        if (longValue < PrimeInteger.BETA) {
          return AbstractIntegerSym.factorizeLong(longValue);
        }
      }
    } catch (ArithmeticException aex) {
      // go on with big integers
    }
    Int2IntMap map = new Int2IntRBTreeMap();
    // SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
    BigInteger rest = Primality.countPrimes32749(big, map);
    int allocSize = 1;
    for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
      allocSize += entry.getIntValue();
    }
    IASTAppendable result = F.ListAlloc(allocSize);
    if (negative) {
      result.append(F.CN1);
    }
    for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
      int key = entry.getIntKey();
      IInteger is = AbstractIntegerSym.valueOf(key);
      for (int i = 0; i < entry.getIntValue(); i++) {
        result.append(is);
      }
    }
    if (rest.equals(BigInteger.ONE)) {
      return result;
    }
    if (rest.isProbablePrime(IInteger.PRIME_CERTAINTY)) {
      result.append(AbstractIntegerSym.valueOf(rest));
      return result;
    }
    SortedMap<BigInteger, Integer> bigMap = factorInteger(rest);

    for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
      BigInteger key = entry.getKey();
      IInteger is = AbstractIntegerSym.valueOf(key);
      for (int i = 0; i < entry.getValue(); i++) {
        result.append(is);
      }
    }

    return result;
  }
}
