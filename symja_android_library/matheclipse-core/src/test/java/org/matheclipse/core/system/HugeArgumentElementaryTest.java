package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Exponential, circular and hyperbolic functions of arbitrary precision numbers whose real or
 * imaginary part is huge: the phase of <code>E^(a+I*b)</code> is reduced with enough digits of
 * <code>b</code>, the bounded functions go to their limits, and the unbounded ones give
 * <code>Overflow()</code> instead of an exception.
 */
public class HugeArgumentElementaryTest extends ExprEvaluatorTestCase {

  @Test
  public void testExpOfHugeImaginaryPart() {
    // the modulus is E^(-1/2) = 0.6065..
    check("N(E^(-1/2+I*7^90),20)", //
        "0.1698561866444326176+I*0.58226138205281482678");
    check("Exp(N(-1/2+I*7^90,25))", //
        "0.1698561866444326176018551+I*0.5822613820528148267763238");
    check("Chop(N(Abs(Exp(N(-1/2+I*7^90,25)))-1/Sqrt(E),20))", //
        "0");
  }

  @Test
  public void testTanCotOfHugeImaginaryPart() {
    check("Tan(N(3*10^40*I,25))", //
        "I*1");
    check("Tan(N(-3*10^40*I,25))", //
        "I*(-1)");
    check("Cot(N(5*10^35*I,25))", //
        "I*(-1)");
  }

  @Test
  public void testSecCscOfHugeImaginaryPart() {
    // below the smallest number the library can represent
    check("Sec(N(2*10^50*I,25))", //
        "Underflow()");
    check("Csc(N(2*10^50*I,25))", //
        "Underflow()");
    // 2*E^(-100) and 2*E^(-80)*(Sin(1)-I*Cos(1)), not zero
    check("Sec(N(100*I,25))", //
        "7.440151952041671925919390*10^-44");
    check("Csc(N(1+80*I,25))", //
        "3.037460149524360552402495*10^-35+I*(-1.95033073320437995015404*10^-35)");
    check("Sec(3.0*10^22*I)", //
        "0.0");
    check("Csc(3.0*10^22*I)", //
        "0.0");
  }

  @Test
  public void testHyperbolicOfHugeRealPart() {
    check("Tanh(N(-4*10^45+I,25))", //
        "-1");
    check("Coth(N(4*10^45+I,25))", //
        "1");
    check("Sech(N(4*10^45+I,25))", //
        "Underflow()");
    check("Csch(N(4*10^45+I,25))", //
        "Underflow()");
    check("Sech(N(2*10^50,25))", //
        "Underflow()");
    // 2*E^(-10^10): the argument has 15 digits after the decimal point
    check("Sech(N(10^10,25))/Exp(N(-10^10,25))==2", //
        "True");
  }

  @Test
  public void testCancellationIsInexactZero() {
    check("Precision(N(Cos(10^40)+I*Sin(10^40),20)-N(E^(I*10^40),20))", //
        "0");
    check("Accuracy(N(Cos(10^40)+I*Sin(10^40),20)-N(E^(I*10^40),20))>19", //
        "True");
  }

  @Test
  public void testUnboundedOverflow() {
    check("Sin(N(1+6*10^45*I,25))", //
        "Overflow()");
    check("Cos(N(1+6*10^45*I,25))", //
        "Overflow()");
    check("Cosh(N(6*10^45+I,25))", //
        "Overflow()");
  }

  @Test
  public void testExpBeyondExponentRange() {
    check("Exp(N(7*10^40,25))", //
        "Overflow()");
    check("Exp(N(-7*10^40,25))", //
        "Underflow()");
    check("E^N(7*10^40+2*I,25)", //
        "Overflow()");
    check("Exp(N(-7*10^40+2*I,25))", //
        "Underflow()");
  }

  @Test
  public void testPeriodicArgumentBeyondExtraPrecision() {
    // the fraction 1/7 needs 85 digits more than the precision: no digit is known
    check("Precision(N(Cos(10^85+1/7),25))", //
        "0");
    check("Block({$MaxExtraPrecision=150}, N(Cos(10^85+1/7),25))", //
        "0.5317671228363621429573602");
    // an exact integer argument loses no fraction
    check("N(Cos(10^85),25)", //
        "0.6469234325728997207594676");
  }

  @Test
  public void testPeriodicArgumentBelowMachinePrecision() {
    // the double of 10^30+1/7 lost the fraction: the evaluation goes on with more digits
    check("N(Cos(10^30+1/7),12)", //
        "-0.972955803506");
  }

  @Test
  public void testSmallPartHasItsOwnDigits() {
    // the imaginary part is right to the digits which the reduction of 10^62+1/7 left, not to
    // the digits of the real part near 10^9
    check("Precision(Im(N(Exp(I*(10^62+1/7))+10^9,25)))<20", //
        "True");
    check("Im(N(Exp(I*(10^62+1/7))+10^9,25))", //
        "0.4335742199016524");
  }
}
