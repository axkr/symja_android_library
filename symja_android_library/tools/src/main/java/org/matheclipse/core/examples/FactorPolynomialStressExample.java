package org.matheclipse.core.examples;

import java.util.SortedMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

/**
 * See <a href="https://github.com/kredel/java-algebra-system/issues/12">java-algebra-system #12</a>
 */
public class FactorPolynomialStressExample {

  public static void main(String[] args) {

    for (int i = 0; i < 100000; i++) {
      String[] vars = new String[] {"a", "c", "d", "e", "x"};
      GenPolynomialRing<edu.jas.arith.BigInteger> fac;
      fac = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
          vars.length, new TermOrder(TermOrder.INVLEX), vars);

      GenPolynomial<edu.jas.arith.BigInteger> poly =
          fac.parse("a*d*e + c*d^2*x + a*e^2*x + c*d*e*x^2");
      System.out.println("Run: " + i + " -" + poly.toString());
      FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
          FactorFactory.getImplementation(edu.jas.arith.BigInteger.ZERO);
      SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map = factorAbstract.factors(poly);
      System.out.println("Factors: " + map.toString());
    }
  }
}
