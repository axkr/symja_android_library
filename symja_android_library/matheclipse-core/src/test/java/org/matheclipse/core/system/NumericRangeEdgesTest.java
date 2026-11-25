package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Numbers at the edges of the machine and arbitrary precision range: powers beyond any
 * representable magnitude, the objects <code>Underflow()</code> and <code>Overflow()</code> next to
 * inexact numbers, and machine values which are rounded correctly.
 */
public class NumericRangeEdgesTest extends ExprEvaluatorTestCase {

  @Test
  public void testPowersBeyondAnyMagnitude() {
    // the powers of numerator and denominator overflow one by one; a numerator 1 or a negative
    // power of an integer is the reciprocal of an overflow
    check("{(3/5)^(-2^64),(5/3)^(-2^64),(3/5)^(2^64),7^(2^63)}", //
        "{Overflow(),Overflow(),Overflow(),Overflow()}");
    check("{(1/7)^(2^63),(-1/7)^(2^63),3^(-2^64)}", //
        "{Underflow(),Underflow(),Underflow()}");
    check("0.5^(1.0*10^400)", //
        "Underflow()");
    check("{N(Underflow()),N(x+Underflow())}", //
        "{0.0,x}");
    check("0.25^(10^500)", //
        "Overflow()");
    // a base next to 1 makes an ordinary number of a huge exponent
    check("InputForm(0.9999999999999999^(2^62))", //
        "4.377491037052927`*^-223");
    // the parity of the exponent decides the sign
    check("InputForm({(-1.0)^(10^500+3),(-1.0)^(10^500),1.0^(10^500)})", //
        "{-1.0`,1.0`,1.0`}");
  }

  @Test
  public void testUnderflowNextToInexactNumbers() {
    check("InputForm({Underflow()*3.5,Underflow()+2.5,2.5+Underflow()})", //
        "{0.0`,2.5`,2.5`}");
    // the zero of an arbitrary precision factor keeps its precision
    check("InputForm(Underflow()*N(2,30))", //
        "0``29.3979");
    check("{Underflow()*3,Underflow()*Overflow(),-Underflow()}", //
        "{Underflow(),Indeterminate,Underflow()}");
  }

  @Test
  public void testArcSinhRoundedCorrectly() {
    check("InputForm({ArcSinh(0.22),ArcSinh(0.57),ArcSinh(1.37),ArcSinh(-2.05)})", //
        "{0.21826290743988225`,0.5429305054577264`,1.1204203164435755`,-1.465775129148317`}");
  }

  @Test
  public void testInverseJacobiDNBelowTheComplementaryModulus() {
    // between 0 and Sqrt(1-m) both K(m)+I*y and K(m)-I*y are inverses: the upper one is taken
    check("Im(InverseJacobiDN(0.25,0.6))>0", //
        "True");
    check("InputForm(InverseJacobiDN(0.25,0.6))", //
        "1.949567749806026` + 1.366685299025551`*I");
    // a negative value is the one at -x shifted by 2*I*K(1-m), as before
    check("InputForm(InverseJacobiDN(-0.4,0.3))", //
        "1.713889448178791` + 2.58870618389321`*I");
  }

  @Test
  public void testLogarithmOfNegativeMultipleInIntegrand() {
    // Log(-2*x) is no Log(2)+I*Pi+Log(x) for x<0
    check("Integrate(Log(-2*x)*Log(5*x)/x,x)", //
        "-Log(-2*x)^3/6+1/2*Log(-2*x)^2*Log(5*x)");
    check("Integrate((Log(-x)+Log(3*x))/x,x)", //
        "Log(-x)^2/2+Log(3*x)^2/2");
    // Log(x^3) is 3*Log(x) in the right half plane, where the antiderivative is checked
    check("Integrate(1/(Log(x)+Log(x^3)),x)", //
        "LogIntegral(x)/4");
  }

  @Test
  public void testNKeepsExactExponentsOfSymbolicBases() {
    // an integer exponent and the 1/2 of a square root stay exact; only the base and the
    // coefficients become numbers
    check("FullForm(N(u^3-2/7*v))", //
        "Plus(Power(u, 3), Times(-0.2857142857142857`, v))");
    check("FullForm(N(Sqrt(w^2+5/3)))", //
        "Power(Plus(1.6666666666666667`, Power(w, 2)), Rational(1,2))");
    check("FullForm(N(1/(1+t)^2+Cos(t)^-1))", //
        "Plus(Power(Plus(1.0`, t), -2), Sec(t))");
    // every rational exponent stays exact
    check("FullForm(N(r^(2/5)+s^(-7/3)))", //
        "Plus(Power(r, Rational(2,5)), Power(s, Rational(-7,3)))");
    // a numeric base is numericized with its exponent, and so is an exponent which isn't rational
    check("FullForm(N(q^E))", //
        "Power(q, 2.718281828459045`)");
    check("FullForm(N(3^k+Pi^2))", //
        "Plus(9.869604401089358`, Power(3.0`, k))");
  }
}
