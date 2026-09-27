package org.matheclipse.core.sympy.ntheory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Egyptian fraction expansions. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/ntheory/egyptian_fraction.py">sympy/ntheory/egyptian_fraction.py</a>
 */
public class EgyptianFraction {

  public enum Algorithm {
    GREEDY, GRAHAM_JEWETT, TAKENOUCHI, GOLOMB
  }

  private EgyptianFraction() {}

  /**
   * Return the list of denominators of an Egyptian fraction expansion of the positive rational
   * <code>r</code> with the greedy (Fibonacci-Sylvester) algorithm.
   *
   * @param r a positive rational number
   * @return the list of denominators
   */
  public static IAST egyptianFraction(IRational r) {
    return egyptianFraction(r, Algorithm.GREEDY);
  }

  /**
   * Return the list of denominators of an Egyptian fraction expansion of the positive rational
   * <code>r</code>.
   *
   * @param r a positive rational number
   * @param algorithm the algorithm to be used
   * @return the list of denominators
   * @throws ValueError if <code>r</code> isn't positive
   */
  public static IAST egyptianFraction(IRational r, Algorithm algorithm) {
    // >>> egyptian_fraction(Rational(3, 7))
    // [3, 11, 231]
    // >>> egyptian_fraction((3, 7), "Graham Jewett")
    // [7, 8, 9, 56, 57, 72, 3192]
    // >>> egyptian_fraction((3, 7), "Takenouchi")
    // [4, 7, 28]
    // >>> egyptian_fraction((3, 7), "Golomb")
    // [3, 15, 35]
    // >>> egyptian_fraction((11, 5), "Golomb")
    // [1, 2, 3, 4, 9, 234, 1118, 2580]
    if (!r.isPositive()) {
      throw new ValueError("Value must be positive");
    }

    // common cases that all methods agree on
    BigInteger x = r.toBigNumerator();
    BigInteger y = r.toBigDenominator();
    if (y.equals(BigInteger.ONE) && x.equals(BigInteger.TWO)) {
      return F.List(F.C1, F.C2, F.C3, F.C6);
    }
    if (x.equals(y.add(BigInteger.ONE))) {
      return F.List(F.C1, F.ZZ(y));
    }

    List<BigInteger> result = new ArrayList<BigInteger>();
    BigInteger[] rem = egyptHarmonic(x, y, result);
    if (rem[0].signum() != 0) {
      // assert x < y and gcd(x, y) = 1
      x = rem[0];
      y = rem[1];
      switch (algorithm) {
        case GREEDY:
          egyptGreedy(x, y, result);
          break;
        case GRAHAM_JEWETT:
          result.addAll(egyptGrahamJewett(x, y));
          break;
        case TAKENOUCHI:
          result.addAll(egyptTakenouchi(x, y));
          break;
        case GOLOMB:
          result.addAll(egyptGolomb(x, y));
          break;
        default:
          throw new ValueError("Entered invalid algorithm");
      }
    }
    IASTAppendable list = F.ListAlloc(result.size());
    for (int i = 0; i < result.size(); i++) {
      list.append(F.ZZ(result.get(i)));
    }
    return list;
  }

  private static void egyptGreedy(BigInteger x, BigInteger y, List<BigInteger> result) {
    // assumes gcd(x, y) == 1
    while (!x.equals(BigInteger.ONE)) {
      // a = (-y) % x
      BigInteger a = y.negate().mod(x);
      // b = y*(y//x + 1)
      BigInteger q = y.divide(x).add(BigInteger.ONE);
      BigInteger b = y.multiply(q);
      BigInteger c = a.gcd(b);
      result.add(q);
      x = a.divide(c);
      y = b.divide(c);
    }
    result.add(y);
  }

  /**
   * Find the index of the smallest pair of identical elements in the sorted list <code>l</code>.
   *
   * @return <code>-1</code> if all elements are unique
   */
  private static int firstDuplicate(List<BigInteger> l) {
    // l.sort() # so the list has duplicates. find a smallest pair
    Collections.sort(l);
    for (int i = 0; i < l.size() - 1; i++) {
      if (l.get(i).equals(l.get(i + 1))) {
        return i;
      }
    }
    return -1;
  }

  private static List<BigInteger> unitFractions(BigInteger x, BigInteger y) {
    // l = [y] * x
    int n = x.intValueExact();
    List<BigInteger> l = new ArrayList<BigInteger>(n);
    for (int i = 0; i < n; i++) {
      l.add(y);
    }
    return l;
  }

  private static List<BigInteger> egyptGrahamJewett(BigInteger x, BigInteger y) {
    // assumes gcd(x, y) == 1
    List<BigInteger> l = unitFractions(x, y);
    // l is now a list of integers whose reciprocals sum to x/y.
    // we shall now proceed to manipulate the elements of l without
    // changing the reciprocated sum until all elements are unique.
    int i;
    while ((i = firstDuplicate(l)) >= 0) {
      // we have now identified a pair of identical
      // elements: l[i] and l[i + 1].
      // now comes the application of the result of graham and jewett:
      BigInteger li = l.get(i);
      BigInteger li1 = li.add(BigInteger.ONE);
      l.set(i + 1, li1);
      // and we just iterate that until the list has no duplicates.
      l.add(li.multiply(li1));
    }
    Collections.sort(l);
    return l;
  }

  private static List<BigInteger> egyptTakenouchi(BigInteger x, BigInteger y) {
    // assumes gcd(x, y) == 1
    // special cases for 3/y
    if (x.equals(BigInteger.valueOf(3))) {
      List<BigInteger> l = new ArrayList<BigInteger>(3);
      if (!y.testBit(0)) {
        l.add(y.shiftRight(1));
        l.add(y);
        return l;
      }
      BigInteger i = y.subtract(BigInteger.ONE).shiftRight(1);
      BigInteger j = i.add(BigInteger.ONE);
      BigInteger k = j.add(i);
      l.add(j);
      l.add(k);
      l.add(j.multiply(k));
      return l;
    }
    List<BigInteger> l = unitFractions(x, y);
    int i;
    while ((i = firstDuplicate(l)) >= 0) {
      BigInteger k = l.get(i);
      if (!k.testBit(0)) {
        l.set(i, k.shiftRight(1));
        l.remove(i + 1);
      } else {
        BigInteger k1 = k.add(BigInteger.ONE);
        l.set(i, k1.shiftRight(1));
        l.set(i + 1, k.multiply(k1).shiftRight(1));
      }
    }
    Collections.sort(l);
    return l;
  }

  private static List<BigInteger> egyptGolomb(BigInteger x, BigInteger y) {
    // assumes x < y and gcd(x, y) == 1
    List<BigInteger> rv = new ArrayList<BigInteger>();
    while (!x.equals(BigInteger.ONE)) {
      BigInteger xp = x.modInverse(y);
      rv.add(xp.multiply(y));
      x = x.multiply(xp).subtract(BigInteger.ONE).divide(y);
      y = xp;
    }
    rv.add(y);
    Collections.sort(rv);
    return rv;
  }

  /**
   * Take all the unit fractions of the harmonic sequence <code>1/1 + 1/2 + 1/3 + ...</code> until
   * adding one more would be greater than <code>x/y</code>.
   *
   * @param prefix the denominators of the harmonic sequence are appended to this list
   * @return the remainder as <code>{numerator, denominator}</code> in lowest terms
   */
  private static BigInteger[] egyptHarmonic(BigInteger x, BigInteger y, List<BigInteger> prefix) {
    BigInteger d = BigInteger.ONE;
    // acc + 1/d <= r <=> (r - 1/d) >= 0 with r := r - acc
    while (true) {
      // x/y - 1/d = (x*d - y) / (y*d)
      BigInteger numer = x.multiply(d).subtract(y);
      if (numer.signum() < 0) {
        break;
      }
      BigInteger denom = y.multiply(d);
      BigInteger g = numer.gcd(denom);
      x = numer.divide(g);
      y = denom.divide(g);
      prefix.add(d);
      d = d.add(BigInteger.ONE);
    }
    return new BigInteger[] {x, y};
  }
}
