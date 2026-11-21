package edu.jas.ufd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.junit.jupiter.api.Test;
import edu.jas.arith.BigInteger;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

/**
 * Tests that {@link FactorInteger} splits off cyclotomic factors of polynomials which are not
 * binomials before the Hensel recombination. Without the split, <code>(x^96-1)*(x+2)</code> took
 * about 17 seconds, because every prime splits its cyclotomic factors into at least 26 modular
 * factors.
 */
public class CyclotomicSplitTest {

  private static final GenPolynomialRing<BigInteger> R =
      new GenPolynomialRing<BigInteger>(BigInteger.ONE, new String[] {"x"});

  private static final FactorAbstract<BigInteger> FACTOR =
      FactorFactory.getImplementation(BigInteger.ONE);

  private static GenPolynomial<BigInteger> p(String s) {
    return R.parse(s);
  }

  private static GenPolynomial<BigInteger> phi(long n) {
    return CycloUtil.cyclotomicPolynomial(R, n);
  }

  /** Factors P, checks that the product of the factors is P and returns the number of factors. */
  private static int factorCount(GenPolynomial<BigInteger> P) {
    SortedMap<GenPolynomial<BigInteger>, Long> m = FACTOR.factors(P);
    GenPolynomial<BigInteger> prod = R.getONE();
    for (Map.Entry<GenPolynomial<BigInteger>, Long> e : m.entrySet()) {
      assertTrue(FACTOR.isIrreducible(e.getKey()) || e.getKey().isConstant(), e.getKey().toString());
      prod = prod.multiply(e.getKey().power(e.getValue()));
    }
    assertTrue(prod.equals(P) || prod.negate().equals(P), "product " + prod + " != " + P);
    return m.size();
  }

  @Test
  public void testHighDegreeCyclotomicTimesLinear() {
    GenPolynomial<BigInteger> P = p("x^96 - 1").multiply(p("x + 2"));
    // 12 divisors of 96 and the linear factor
    int count = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> factorCount(P));
    assertEquals(13, count);
  }

  @Test
  public void testCyclotomicProducts() {
    assertEquals(7, factorCount(p("x^54 - 1").divide(p("x - 1"))));
    assertEquals(5, factorCount(p("x^46 - 1").multiply(p("x^2 + 3"))));
    assertEquals(3, factorCount(p("x^40 + 1").multiply(p("x^3 + x + 1"))));
    assertEquals(2, factorCount(p("x^64 + 1").multiply(p("x + 3"))));
  }

  @Test
  public void testNonMonicCofactor() {
    // 3 is the content, Phi_15 and 2x^2+1
    GenPolynomial<BigInteger> P = phi(15).multiply(p("2*x^2 + 1"));
    assertEquals(2, factorCount(P));
    assertEquals(3, factorCount(P.multiply(p("3"))));
  }

  @Test
  public void testPhi105() {
    // Phi_105 is the first cyclotomic polynomial with a coefficient -2
    GenPolynomial<BigInteger> P = phi(105).multiply(phi(7)).multiply(p("x^5 - x - 1"));
    assertEquals(3, factorCount(P));
    List<GenPolynomial<BigInteger>> cyclo = CycloUtil.cyclotomicFactorsDividing(P);
    assertEquals(2, cyclo.size());
    assertTrue(cyclo.contains(phi(7)));
    assertTrue(cyclo.contains(phi(105)));
  }

  @Test
  public void testNoCyclotomicFactor() {
    assertEquals(1, factorCount(p("x^4 + x + 1")));
    // both quadratics agree modulo every prime from 5 to 29
    assertEquals(2, factorCount(p("x^2 - 2").multiply(p("x^2 - 2 + 3234846615"))));
    assertTrue(CycloUtil.cyclotomicFactorsDividing(p("x^4 + x + 1")).isEmpty());
    assertTrue(CycloUtil.cyclotomicFactorsDividing(p("x^2 + x + 2")).isEmpty());
  }

  @Test
  public void testDividingFactorsOfBinomial() {
    List<GenPolynomial<BigInteger>> cyclo = CycloUtil.cyclotomicFactorsDividing(p("x^6 - 1"));
    assertEquals(Arrays.asList(phi(1), phi(2), phi(3), phi(6)), cyclo);
  }

  @Test
  public void testRepeatedFactor() {
    // not squarefree: the factor is listed once for each power
    List<GenPolynomial<BigInteger>> cyclo =
        CycloUtil.cyclotomicFactorsDividing(phi(5).power(2).multiply(p("x + 2")));
    assertEquals(Arrays.asList(phi(5), phi(5)), cyclo);
  }
}
