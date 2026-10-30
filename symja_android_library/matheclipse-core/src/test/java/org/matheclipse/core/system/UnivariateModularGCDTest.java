package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.polynomials.UnivariateModularGCD;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrderByName;
import edu.jas.ufd.GCDFactory;

/** The modular GCD against the GCD of the polynomial library, on random polynomials. */
public class UnivariateModularGCDTest extends ExprEvaluatorTestCase {

  private static final BigInteger ONE = BigInteger.ONE;

  private static BigInteger[] random(Random random, int degree, int bits) {
    BigInteger[] polynomial = new BigInteger[degree + 1];
    for (int i = 0; i <= degree; i++) {
      polynomial[i] = new BigInteger(bits, random);
      if (random.nextBoolean()) {
        polynomial[i] = polynomial[i].negate();
      }
    }
    if (polynomial[degree].signum() == 0) {
      polynomial[degree] = ONE;
    }
    return polynomial;
  }

  private static BigInteger[] product(BigInteger[] a, BigInteger[] b) {
    BigInteger[] result = new BigInteger[a.length + b.length - 1];
    java.util.Arrays.fill(result, BigInteger.ZERO);
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < b.length; j++) {
        result[i + j] = result[i + j].add(a[i].multiply(b[j]));
      }
    }
    return result;
  }

  private static BigInteger[] primitive(BigInteger[] polynomial) {
    BigInteger content = BigInteger.ZERO;
    for (BigInteger coefficient : polynomial) {
      content = content.gcd(coefficient);
    }
    BigInteger[] result = new BigInteger[polynomial.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = polynomial[i].divide(content);
    }
    return result;
  }

  private static GenPolynomial<edu.jas.arith.BigInteger> jas(BigInteger[] polynomial,
      GenPolynomialRing<edu.jas.arith.BigInteger> ring) {
    GenPolynomial<edu.jas.arith.BigInteger> result = ring.getZERO().copy();
    for (int i = 0; i < polynomial.length; i++) {
      if (polynomial[i].signum() != 0) {
        result = result.sum(new edu.jas.arith.BigInteger(polynomial[i]),
            edu.jas.poly.ExpVector.create(1, 0, i));
      }
    }
    return result;
  }

  @Test
  public void testAgainstTheLibrary() {
    EvalEngine engine = EvalEngine.get();
    GenPolynomialRing<edu.jas.arith.BigInteger> ring =
        new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ONE, 1,
            TermOrderByName.INVLEX, new String[] {"t"});
    Random random = new Random(20261030L);
    for (int round = 0; round < 200; round++) {
      int bits = 1 + random.nextInt(round % 5 == 0 ? 200 : 40);
      BigInteger[] common = primitive(random(random, random.nextInt(8), bits));
      BigInteger[] a = primitive(product(common, random(random, 1 + random.nextInt(12), bits)));
      BigInteger[] b = primitive(product(common, random(random, 1 + random.nextInt(12), bits)));
      GenPolynomial<edu.jas.arith.BigInteger> p = jas(a, ring);
      GenPolynomial<edu.jas.arith.BigInteger> q = jas(b, ring);
      GenPolynomial<edu.jas.arith.BigInteger> expected =
          GCDFactory.getImplementation(edu.jas.arith.BigInteger.ONE).gcd(p, q);
      GenPolynomial<edu.jas.arith.BigInteger> gcd = UnivariateModularGCD.gcd(p, q, engine);
      if (expected.signum() < 0) {
        expected = expected.negate();
      }
      assertEquals(expected, gcd, "round " + round);
    }
  }

  @Test
  public void testConstantPolynomialsAndTheModulus() {
    EvalEngine engine = EvalEngine.get();
    assertEquals(ONE, UnivariateModularGCD.gcd(new BigInteger[] {BigInteger.valueOf(3)},
        new BigInteger[] {BigInteger.valueOf(5), ONE}, engine)[0]);
    // (t+1)*(t+2) and (t+1)*(t+3) modulo 7
    long[] gcd = UnivariateModularGCD.gcdModulo(new long[] {2L, 3L, 1L}, new long[] {3L, 4L, 1L},
        7L, engine);
    assertEquals(2, gcd.length);
    assertEquals(1L, gcd[0]);
    assertEquals(1L, gcd[1]);
    // the zero polynomial modulo 7
    assertEquals(3, UnivariateModularGCD.gcdModulo(new long[] {0L}, new long[] {2L, 3L, 1L}, 7L,
        engine).length);
  }

  @Test
  public void testRationalCoefficientsThroughTheBuiltin() {
    check("{PolynomialGCD(t/2+1/2, t/3+1/3), PolynomialGCD(4*t+4, 6*t+6), "
        + "PolynomialGCD(-2*t-2, 4*t+4), PolynomialGCD(t^2-1, -t+1), "
        + "PolynomialGCD(3/4*t^2-3/4, 9/2*t-9/2)}", //
        "{1/6+t/6,2+2*t,2+2*t,-1+t,-3/4+3/4*t}");
    check("{PolynomialLCM(3*t+3, 6*t-6), PolynomialLCM(t-1, t^2-1), PolynomialLCM(t^2+5, t^2+6)}", //
        "{(1+t)*(-6+6*t),-1+t^2,(5+t^2)*(6+t^2)}");
  }

  @Test
  public void testHighDegree() {
    // 8 seconds with a GCD over the rationals; a limit for the modular one
    check("a = Sum(Mod(k^2+3*k+1, 17)*t^k, {k, 0, 900}) + 1; "
        + "b = Sum(Mod(k^2+5*k+2, 13)*t^k, {k, 0, 899}) + t^500 + 2; "
        + "{First(Timing(PolynomialGCD(a, b))) < 2, PolynomialGCD(a, b)}", //
        "{True,1}");
    check("c = Expand((t^5+3*t+1)*(t^7-t^2+4)); "
        + "PolynomialGCD(Expand(c*(t^300+t+1)), Expand(c*(t^299-t+3))) === c", //
        "True");
  }
}
