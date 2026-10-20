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
    // a machine number counts as exact
    check("Precision(1.5 + N(Pi,30))", //
        "30");
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
}
