package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.util.CompensatedSum;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * The folding of numbers in <code>F.intSum()</code>, <code>F.intProduct()</code> and the
 * <code>Sum()</code> / <code>Product()</code> builtins: compensated machine sums, machine products
 * without intermediate overflow, pairwise exact arithmetic and the iteration bounds.
 */
public class SumProductAccumulatorTest extends ExprEvaluatorTestCase {

  @Test
  public void testMachineSumIsCompensated() {
    // adding 1.0 to 1.0e16 one at a time rounds every single addition away
    IExpr sum = F.intSum(k -> k.isOne() ? F.num(1.0e16) : F.num(1.0), 1, 1001, 1);
    assertEquals(1.0e16 + 1000.0, ((Num) sum).doubleValue(), 0.0);

    sum = F.intSum(k -> F.num(0.1), 1, 10, 1);
    assertEquals(1.0, ((Num) sum).doubleValue(), 0.0);

    // an overflow or an infinite term stays infinite, it doesn't become NaN
    CompensatedSum compensated = new CompensatedSum();
    compensated.add(Double.MAX_VALUE);
    compensated.add(Double.MAX_VALUE);
    compensated.add(1.0);
    assertEquals(Double.POSITIVE_INFINITY, compensated.value(), 0.0);
    compensated = new CompensatedSum();
    compensated.add(1.0);
    compensated.add(Double.NEGATIVE_INFINITY);
    assertEquals(Double.NEGATIVE_INFINITY, compensated.value(), 0.0);

    // real and complex machine terms together
    sum = F.intSum(k -> k.isOdd() ? F.complexNum(0.1, 0.2) : F.num(0.1), 1, 20, 1);
    assertEquals("(2.0+I*2.0)", sum.toString());
  }

  @Test
  public void testMachineProductBeyondDoubleRange() {
    // 1.0e200 * 1.0e200 * 1.0e-300 overflows on its way when multiplied one after the other
    IExpr product = F.intProduct(
        k -> k.isOne() ? F.num(1.0e-300) : F.num(1.0e200), 1, 3, 1);
    assertTrue(product instanceof Num);
    assertEquals(1.0e100, ((Num) product).doubleValue(), 1.0e86);

    // 2.0^1100 is beyond the double range
    product = F.intProduct(k -> F.num(2.0), 1, 1100, 1);
    assertTrue(product instanceof ApfloatNum, product.toString());

    // a product which ends below the smallest double isn't 0.0
    product = F.intProduct(k -> F.num(0.5), 1, 1100, 1);
    assertTrue(product instanceof ApfloatNum, product.toString());
  }

  @Test
  public void testExactPairwise() {
    // the 1000th harmonic number, added pairwise
    IExpr sum = F.intSum(k -> F.QQ(F.C1, k), 1, 1000, 1);
    IRational sequential = F.C0;
    for (int k = 1; k <= 1000; k++) {
      sequential = sequential.add(F.QQ(1, k));
    }
    assertEquals(sequential, sum);
    assertEquals(sequential, F.sumRational(k -> F.QQ(1, k), 1, 1000));

    IExpr product = F.intProduct(k -> k, 1, 200, 1);
    assertEquals(AbstractIntegerSym.factorial(200), product);
    assertEquals(AbstractIntegerSym.factorial(200),
        F.productRational(k -> F.ZZ(k), 1, 200, 1));
  }

  @Test
  public void testIterationBounds() {
    // a negative step counts down
    assertEquals(F.ZZ(30), F.intSum(k -> k, 10, 1, -2));
    assertEquals(F.C0, F.intSum(k -> k, 1, 10, -1));
    assertEquals(F.C1, F.intProduct(k -> k, 10, 1, 1));
    // the index can't overflow at the end of the int range
    assertEquals(F.ZZ(3L * Integer.MAX_VALUE - 3L),
        F.intSum(k -> k, Integer.MAX_VALUE - 2, Integer.MAX_VALUE, 1));
    assertThrows(IllegalArgumentException.class, () -> F.intSum(k -> k, 1, 10, 0));
  }

  @Test
  public void testNoNeutralPlaceholder() {
    IExpr x = F.Dummy("x");
    // no exact 0 or 1 in front of the terms which aren't numbers
    assertEquals(F.Plus(F.Power(x, F.C2), F.Power(x, F.C3)),
        F.intSum(k -> F.Power(x, k), 2, 3, 1));
    assertEquals(F.Times(F.Plus(F.C1, x), F.Plus(F.C2, x)),
        F.intProduct(k -> F.Plus(k, x), 1, 2, 1));
    assertEquals(F.Plus(F.C2, x), F.intSum(k -> k.isOne() ? x : k, 1, 2, 1));
  }

  @Test
  public void testSumProductBuiltins() {
    check("Sum(0.1,{k,1,10})-1", //
        "0.0");
    check("Sum(1.5+I*0.5,{k,1,10})", //
        "15.0+I*5.0");
    check("Sum(Sqrt(k),{k,1,4})", //
        "3+Sqrt(2)+Sqrt(3)");
    check("Sum(Sqrt(i)*j,{i,1,3},{j,1,2})", //
        "3+3*Sqrt(2)+3*Sqrt(3)");
    check("Sum(k,{k,10,1,-2})", //
        "30");
    check("Product(Sqrt(k),{k,1,4})", //
        "2*Sqrt(6)");
    check("Product(k-3,{k,1,10^5})", //
        "0");
    // all factors are evaluated after the exact 0: 0*ComplexInfinity
    check("Product(If(k==3,0,1/(k-5)),{k,1,7})", //
        "Indeterminate");
    // the machine number of the summand isn't lost: 1.0*f evaluates to f
    check("Sum(1.0/k^2,{k,1,10})", //
        "1.54977");
    check("Sum(1.0*Prime(k),{k,1,5})", //
        "28.0");
    check("Product(1.0*k,{k,1,5})", //
        "120.0");
    check("Sum(1.0*x^k,{k,1,3})", //
        "x+x^2+x^3");
    check("Sum(1.0*Sqrt(k),{k,1,4})", //
        "6.14626");
    check("Sum(1.0,{k,1,3},{j,1,k})", //
        "6.0");
    // an exact 0 absorbs the machine factors
    check("Product(If(k==2,0,1.5),{k,1,3})", //
        "0");
    // the summand is evaluated term by term, not with a symbolic k
    check("Sum(1.0*{a,b,c}[[k]],{k,1,3})", //
        "a+b+c");
    check("Product(k*(2.0+I),{k,1,200})", //
        "3.267135050728400*10^443+I*(-6.212829094964026*10^444)");
    check("Product(x+k,{k,1,3})", //
        "(1+x)*(2+x)*(3+x)");
    check("Product(k*2.0,{k,1,200})", //
        "1.267324330972641*10^435");
    check("Product(1+2.0/k^2,{k,1,3})", //
        "5.5");
  }

  @Test
  public void testMigratedCallSites() {
    check("Pochhammer(10.0,3)", //
        "1320.0");
    check("Pochhammer(10,3)", //
        "1320");
    check("FactorialPower(7,-3)", //
        "1/720");
    check("FactorialPower(7/2,-2)", //
        "4/99");
    check("StirlingS1(9,6)", //
        "-4536");
    check("HarmonicNumber(12,2)", //
        "240505109/153679680");
    check("HarmonicNumber(5,-3)", //
        "225");
    check("QPochhammer(1/2,1/3,3)", //
        "85/216");
    check("QPochhammer(2,3,-2)", //
        "27/7");
    check("QPochhammer(a,q,3)", //
        "(1-a)*(1-a*q)*(1-a*q^2)");
    check("BellY(6,2,{x1,x2,x3,x4,x5})", //
        "10*x3^2+15*x2*x4+6*x1*x5");
    check("Series(1/(1+x+x^2),{x,0,5})", //
        "1-x+x^3-x^4+O(x)^6");
    check("Series((1+x)^(1/3),{x,0,4})", //
        "1+x/3-x^2/9+5/81*x^3-10/243*x^4+O(x)^5");
    check("CDF(BetaBinomialDistribution(2,3,10),4)", //
        "85/143");
    check("ArcLength(Line({{0,0},{1,1},{3,2}}))", //
        "Sqrt(2)+Sqrt(5)");
    check("RegionCentroid(Line({{0,0},{2,0},{2,2}}))", //
        "{3/2,1/2}");
    check("Beta(3,b)//FunctionExpand", //
        "2/(b*(1+b)*(2+b))");
  }
}
