package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Significance arithmetic of arbitrary precision numbers: real-valued precision marks, the errors
 * of sums and products, zeros with an accuracy, <code>N</code> with a real precision, and the
 * precision of function values from the condition numbers of the functions.
 */
public class SignificanceArithmeticTest extends ExprEvaluatorTestCase {

  @Test
  public void testRealPrecisionMark() {
    check("Precision(2.25`7.3)", //
        "7.3");
    check("InputForm(2.25`7.3)", //
        "2.25`7.3");
    // the accuracy 12 of 0.5 is the precision 12 + log10(0.5)
    check("Precision(0.5``12)", //
        "11.69897");
    // the accuracy 10 of 3.75 is 10 - log10(3.75)
    check("Accuracy(3.75`10)", //
        "9.42597");
  }

  @Test
  public void testPlusAddsAbsoluteErrors() {
    // the accuracies 19.9031 and 14.3233 give the accuracy 14.3233 of 6
    check("Precision(1.25`20 + 4.75`15)", //
        "15.10146");
    // the exact 6 cancels all digits: a zero with the accuracy of the sum
    check("InputForm((1.25`20 + 4.75`15) - 6)", //
        "0``14.3233");
    check("InputForm(1.75`20 - 1.75`20)", //
        "0``19.4559");
    check("Accuracy(1.75`20 - 1.75`20)", //
        "19.45593");
    check("Precision(1.75`20 - 1.75`20)", //
        "0");
    // a machine number counts as exact: the absolute error of N(Pi,30) next to 4.64...
    check("Precision(1.5 + N(Pi,30))", //
        "30.16951715139201");
  }

  @Test
  public void testTimesAddsRelativeErrors() {
    check("Precision(2.5`12 * 3.5`14)", //
        "11.99568");
    check("Precision(2.5`20^3)", //
        "19.52288");
  }

  @Test
  public void testZeroWithAccuracy() {
    check("InputForm(0``12)", //
        "0``12");
    check("Accuracy(0``12)", //
        "12.0");
  }

  @Test
  public void testNRealPrecision() {
    check("Precision(N(E, 25.5))", //
        "25.5");
    // N doesn't raise the precision of a number
    check("Precision(N(2.5`10, 30))", //
        "10");
  }

  @Test
  public void testNOfExactExpressionKeepsPrecision() {
    // the working digits of a difference of nearly equal values are no errors of the exact input
    check("Precision(N(GammaRegularized(4,2,3), 40))", //
        "40");
    check("Precision(N(Sqrt(7)-2645751311/10^9, 30))", //
        "30");
    check("Precision(N(LerchPhi(-1,3,7/2), 25))", //
        "25");
  }

  @Test
  public void testConditionNumbers() {
    // Sqrt halves the relative error
    check("Precision(Sqrt(2.75`25))", //
        "25.30103");
    check("InputForm(Sqrt(2.75`25))", //
        "1.658312395177699924557466`25.301");
    // the condition number of Exp is |x|
    check("Precision(Exp(3.25`20))", //
        "19.48812");
    // the condition number of Log is 1/|Log(x)|
    check("Precision(Log(5.5`20))", //
        "20.23166");
    check("Precision(Log(2, 6.5`20))", //
        "20.27226");
    // |x*Cot(x)|, |x*Tan(x)|, |x*(1+Tan(x)^2)/Tan(x)|
    check("Precision(Sin(0.75`20))", //
        "20.09417");
    check("Precision(Cos(0.75`20))", //
        "20.15571");
    check("Precision(Tan(0.75`20))", //
        "19.82282");
    check("Precision(Sec(0.75`20))", //
        "20.15571");
    // the cosine next to its zero loses digits
    check("Precision(Cos(1.5707963`20))", //
        "12.23193");
    check("Precision(ArcSin(0.25`20))", //
        "19.99062");
    check("Precision(ArcTan(2.5`20))", //
        "20.53805");
    check("Precision(ArcCot(2.5`20))", //
        "20.04276");
  }

  @Test
  public void testFunctionWithoutConditionNumbers() {
    // the value gets the precision of the argument
    check("Precision(LaguerreL(7, 0, 2.5`20))", //
        "20");
  }

  @Test
  public void testHyperbolicConditionNumbers() {
    // |x*Coth(x)| and |x*Tanh(x)|
    check("Precision(Sinh(0.4`20))", //
        "19.97767");
    check("Precision(Cosh(2.5`20))", //
        "19.60791");
    check("Precision(Sech(0.7`20))", //
        "20.3736");
    check("Precision(Csch(2.5`20))", //
        "19.59621");
    // below 1 as Sinh/Cosh, above 1 as |2*x/Sinh(2*x)|: a large argument gains digits
    check("Precision(Tanh(0.6`20))", //
        "19.8418");
    check("Precision(Tanh(30.0`20))", //
        "43.97849");
    check("Precision(Coth(2.5`20))", //
        "21.17145");
    check("Precision(ArcSinh(0.6`20))", //
        "20.0436");
    check("Precision(ArcCosh(3.5`20))", //
        "20.2659");
    check("Precision(ArcTanh(0.95`20))", //
        "19.27415");
    check("Precision(ArcCoth(2.5`20))", //
        "19.94923");
    check("Precision(ArcSech(0.6`20))", //
        "19.94393");
    check("Precision(ArcCsch(0.6`20))", //
        "20.17527");
    // beyond the range of a double value
    check("Accuracy(Sinh(2000.0`3))", //
        "-868.589");
  }

  @Test
  public void testZeroArgumentAndDomainEnds() {
    // a zero has no relative error: the change of the function within its error
    check("Accuracy(Sin(0``15))", //
        "15.0");
    check("Accuracy(Tanh(0``15))", //
        "15.0");
    // the cosine changes to the second order only
    check("Precision(Cos(0``15))", //
        "30.30103");
    check("Precision(Sech(0``15))", //
        "30.30103");
    // at the end of the domain the change is Sqrt(2*10^-16)
    check("Accuracy(ArcCos(1.0`16))", //
        "7.84949");
    check("Accuracy(ArcCosh(1.0`16))", //
        "7.84949");
    check("Precision(ArcSin(0.99999`16))", //
        "13.8454");
  }

  @Test
  public void testPolesAndBounds() {
    // not one digit is known next to a pole
    check("Cot(3.14159`4)", //
        "ComplexInfinity");
    check("Coth(0``12)", //
        "ComplexInfinity");
    check("Csch(0``12)", //
        "ComplexInfinity");
    check("Csch(0.0)", //
        "ComplexInfinity");
    // the error of a bounded function is bounded too
    check("Accuracy(Cos(1.0`15*10^25))", //
        "0.0");
  }

  @Test
  public void testInexactZeroInPlusAndTimes() {
    check("Precision(2.5`15 + 0``8)", //
        "8.39794");
    check("Precision(0``8 + 3)", //
        "8.47712");
    check("Accuracy(0``8 + 0``12)", //
        "7.99996");
    // the error of the zero times the other factors
    check("Accuracy(2.5`15 * 0``8)", //
        "7.60206");
    check("Accuracy(3*0``8)", //
        "7.52288");
    check("Accuracy(0``8*0``12)", //
        "20.0");
    // the exact zero gives the exact zero, a machine number the machine zero
    check("0*2.5`15*3.5`20", //
        "0");
    check("0*0``8", //
        "0");
    check("0*2.5", //
        "0.0");
    // a low precision stays an arbitrary precision: 2 isn't made a machine number
    check("Precision(1.5`5 + 2)", //
        "5.36798");
  }

  @Test
  public void testPowersOfInexactZero() {
    // below 10^-20, so the square root is below 10^-10
    check("InputForm(Sqrt(0``20))", //
        "0``10");
    check("InputForm((0``10)^2)", //
        "0``20");
  }

  @Test
  public void testInexactZeroNextToSymbols() {
    // left out as a machine zero is, whatever else the sum has
    check("x + 0``10", //
        "x");
    check("x + 0``10 + 0", //
        "x");
    // with a number it adds its error
    check("InputForm(2 + 0``10 + x)", //
        "2`10.301 + x");
  }

  @Test
  public void testMachineZeroProducts() {
    check("0 * 10.0^400", //
        "0.0");
    check("0.0 * 2.5`20", //
        "0.0");
    check("0 * I * 2.5`15", //
        "0");
  }

  @Test
  public void testNLowersToTheRequestedPrecision() {
    check("Precision(N(1.5`40, 20))", //
        "20");
    // a precision of the numbers set, which is above the one of the evaluation, stays: Sin of a
    // number of the precision 35 keeps about 4 digits
    check("Precision(SetAccuracy(Sin(10^30), 5))", //
        "3.95658");
  }

  @Test
  public void testSetAccuracyOfComplexNumbers() {
    // the real part is below the accuracy 3
    check("InputForm(SetAccuracy(0.0001 + I, 3))", //
        "I*1`3");
  }

  @Test
  public void testInexactZeroOutputForm() {
    // the exponent is the one of the first unknown digit, -Floor(accuracy)
    check("0``12.7", //
        "0.0*10^-12");
    check("1.5`15 - 1.5`15", //
        "0.0*10^-14");
    check("0``-1.5", //
        "0.0");
    check("InputForm(0``12.7)", //
        "0``12.7");
  }

  @Test
  public void testPrecisionWithMachineNumber() {
    // a machine number anywhere gives the machine precision
    check("Precision({2.5`15, 0.5})", //
        "16");
    check("Precision({x, 1.5``4, 2.0})", //
        "16");
    // a literal with more digits isn't a machine number: 23 digits
    check("Precision(1.3333333333333333333333)", //
        "23");
    check("Precision({1.3333333333333333333333, 2.5})", //
        "16");
  }

  @Test
  public void testSetPrecisionRealValued() {
    check("Precision(SetPrecision(2.5, 12.7))", //
        "12.7");
    check("Precision(SetPrecision(0.25`30, 8.5))", //
        "8.5");
    // a zero has no digits: the exact 0
    check("SetPrecision(0.0, 12)", //
        "0");
    // the exact exponent stays exact: the square root of a number of 25 digits
    check("Precision(SetPrecision(Sqrt(3), 25))", //
        "25.30103");
    check("Precision(SetPrecision(E, 12.5))", //
        "12.5");
    // set, then evaluated
    check("Precision(SetPrecision(Cos(3), 15))", //
        "15.36892");
  }

  @Test
  public void testSetAccuracy() {
    check("Precision(SetAccuracy(2.5, 12))", //
        "12.39794");
    check("Precision /@ SetAccuracy({2.5, 250.0}, 8)", //
        "{8.39794,10.39794}");
    // a zero, and a number of which no digit is left, are zeros of that accuracy
    check("Accuracy(SetAccuracy(0, 12.5))", //
        "12.5");
    check("Accuracy(SetAccuracy(0.0001, 3))", //
        "3.0");
  }

  @Test
  public void testLowPrecisionKeepsItsDigits() {
    // the accuracy of 2.7183, not of the 3 digits shown
    check("Accuracy(2.7183`3)", //
        "2.5657");
    // the difference is taken with 2.4007
    check("Precision(2.5`20 - 2.4007`3)", //
        "1.61661");
  }

  @Test
  public void testExactNumbersStayExactNextToTrackedNumbers() {
    // the fraction isn't made a machine number: its difference to N(1.234, 40) is a zero of 40
    // digits
    check("InputForm(-617/500 + N(1234/1000, 40))", //
        "0``39.9087");
  }

  @Test
  public void testLogOfZeroValue() {
    // the error of x divided by Log(3) at x=1; the base doesn't change a zero value
    check("InputForm(Log(3, 1.0`18))", //
        "0``18.0408");
    check("InputForm(Log(5.0`20, 1))", //
        "0");
    // a base which is 1, an inexact zero
    check("InputForm(Log(1.0`20, 1.0`20))", //
        "Indeterminate");
    check("Log(1.0`20, 3)", //
        "ComplexInfinity");
    check("InputForm(Log(4.0`20, 0``15))", //
        "Indeterminate");
  }

  @Test
  public void testChangeIsTheLeadingTerm() {
    // also at a low precision the leading term of the change, not the change itself
    check("Accuracy(Log(1.0`4))", //
        "4.0");
    check("Accuracy(Sin(0``2.5))", //
        "2.5");
    check("Accuracy(ArcCos(1.0`4))", //
        "1.84949");
  }

  @Test
  public void testComplexNumbersOfParts() {
    // parts of the same precision: the number has that precision
    check("Precision(2.25`18.5 + 1.75`18.5*I)", //
        "18.5");
    // the less precise part decides the error of the number; no part shows more digits
    check("Precision(2.25`18.5 + 1.75`12*I)", //
        "12.21187");
    check("InputForm(2.25`18.5 + 1.75`12*I)", //
        "2.25`12.1091+I*1.75`12");
    check("InputForm(Re(2.25`18.5 + 1.75`12*I))", //
        "2.25`12.1091");
  }

  @Test
  public void testComplexArithmetic() {
    // the relative errors of a product add
    check("Precision((2.0`19 + 1.0`23*I)*(1.5`21 - 3.0`17*I))", //
        "17.04413");
    check("Precision((1.5`20 + 2.0`20*I)^3)", //
        "19.52288");
  }

  @Test
  public void testPoleWithinTheError() {
    // the error of the argument covers the pole of ArcTanh and ArcCoth at 1 and -1
    check("ArcTanh(1.0`15)", //
        "Indeterminate");
    check("ArcTanh(-1.0`15)", //
        "Indeterminate");
    check("ArcCoth(1.0`15)", //
        "Indeterminate");
    // the error 10^-5 of 1.0000001`5 covers 1
    check("ArcTanh(1.0000001`5)", //
        "Indeterminate");
    // exact and machine arguments keep their values
    check("ArcTanh(1)", //
        "Infinity");
    check("ArcTanh(1.0)", //
        "Infinity");
    // the condition number 0.999/((1-0.999^2)*ArcTanh(0.999)) is 131.5
    check("Precision(ArcTanh(0.999`15))", //
        "12.88105");
  }

  @Test
  public void testComplexValuesAtRealArguments() {
    check("Precision(ArcSin(3.0`20))", //
        "20.34753");
    check("Precision(Log(-2.5`20))", //
        "20.51488");
    check("Precision((-27.0`20)^(1/3))", //
        "20.47712");
    check("Precision(ArcSec(0.25`20))", //
        "20.30058");
    // the square root halves the relative error
    check("InputForm(Sqrt(-5.0`20))", //
        "I*2.2360679774997896964`20.301");
    // a zero of a given accuracy
    check("InputForm(ArcCosh(0``15))", //
        "I*1.5707963267949`15.1961");
  }
}
