package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>MantissaExponent(x, b)</code> is <code>{m, e}</code> with <code>x == m*b^e</code> and
 * <code>1/b &lt;= |m| &lt; 1</code>, for every sign and size of <code>x</code>.
 */
public class MantissaExponentTest extends ExprEvaluatorTestCase {

  @Test
  public void testPowersOfTheBase() {
    check("InputForm({MantissaExponent(0.001),MantissaExponent(100.0),MantissaExponent(0.1)})", //
        "{{0.1`,-2},{0.1`,3},{0.1`,0}}");
    check("InputForm({MantissaExponent(0.25,2),MantissaExponent(16.0,2),MantissaExponent(9.0,3)})", //
        "{{0.5`,-1},{0.5`,5},{0.3333333333333333`,3}}");
    check("{MantissaExponent(1/100),MantissaExponent(10000),MantissaExponent(27,3)}", //
        "{{1/10,-1},{1/10,5},{1/3,4}}");
  }

  @Test
  public void testNegativeNumbers() {
    check("InputForm({MantissaExponent(-0.625,2),MantissaExponent(-4.5),MantissaExponent(-0.03)})", //
        "{{-0.625`,0},{-0.45`,1},{-0.3`,-1}}");
    check("{MantissaExponent(-250),MantissaExponent(-3/16,2)}", //
        "{{-1/4,3},{-3/4,-2}}");
  }

  @Test
  public void testEdgesOfTheMachineRange() {
    // the mantissa is the nearest double of the exact quotient
    check("InputForm(MantissaExponent(1.25*^307))", //
        "{0.125`,308}");
    check("InputForm({MantissaExponent(2.0^-1020,2),MantissaExponent(2.0^1020,2)})", //
        "{{0.5`,-1019},{0.5`,1021}}");
  }

  @Test
  public void testArbitraryPrecision() {
    // the exponent of a power too large for an exact power of the base
    check("MantissaExponent(N(3,30)^(3^12),3)", //
        "{0.333333333333333333333333,531442}");
    check("MantissaExponent(N(-7/3,25))", //
        "{-0.2333333333333333333333333,1}");
  }

  @Test
  public void testNoNumber() {
    // a symbol stays without a message, a number which is not real gets one
    check("MantissaExponent(y)", //
        "MantissaExponent(y)");
    check("MantissaExponent(3-4*I)", //
        "MantissaExponent(3-I*4)");
    check("MantissaExponent(Pi^3,Pi)", //
        "{1/Pi,4}");
    // a base of a few digits
    check("Last(MantissaExponent(100,N(3,4)))", //
        "5");
  }
}
