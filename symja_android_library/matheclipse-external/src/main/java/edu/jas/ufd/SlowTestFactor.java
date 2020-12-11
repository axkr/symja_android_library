package edu.jas.ufd;

import java.util.SortedMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;

public class SlowTestFactor {

  public static void main(String[] args) {

    for (int i = 0; i < 100000; i++) {
      String[] vars = new String[] {"x", "y"};
      GenPolynomialRing<edu.jas.arith.BigInteger> fac;
      fac =
          new GenPolynomialRing<edu.jas.arith.BigInteger>(
              edu.jas.arith.BigInteger.ZERO, vars.length, new TermOrder(TermOrder.INVLEX), vars);

      GenPolynomial<edu.jas.arith.BigInteger> poly = fac.parse("12 * x^2 -75 * y^2");
      System.out.println("Run: " + i + " -" + poly.toString());
      FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
          FactorFactory.getImplementation(edu.jas.arith.BigInteger.ZERO);
      SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map = factorAbstract.factors(poly);
      System.out.println("Factors: " + map.toString());
    }
  }
}
