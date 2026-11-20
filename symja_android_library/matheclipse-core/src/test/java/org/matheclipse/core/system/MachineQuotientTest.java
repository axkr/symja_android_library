package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * A quotient <code>a/b</code> of machine numbers is <code>a*b^(-1)</code>: the numerator is
 * multiplied with the rounded reciprocal <code>1/b</code>. <code>Divide(a,b)</code> divides once.
 */
public class MachineQuotientTest extends ExprEvaluatorTestCase {

  @Test
  public void testQuotientUsesRoundedReciprocal() {
    check("InputForm({0.7/0.1,5.4/0.9,-5.4/0.9,Times(0.7,Power(0.1,-1))})", //
        "{7.0`,6.000000000000001`,-6.000000000000001`,7.0`}");
    check("b=0.7;c=0.1;", //
        "");
    check("InputForm({b*y/c,y*b/c})", //
        "{7.0`*y,7.0`*y}");
    check("InputForm({0.7,5.4,{-0.7}}/0.1)", //
        "{7.0`,54.0`,{-7.0`}}");
    check("InputForm((2.0+3.0*I)/(5.0-1.0*I))", //
        "0.26923076923076916` + 0.6538461538461537`*I");
  }

  @Test
  public void testDivideIsOneDivision() {
    check("InputForm({Divide(0.7,0.1),Divide(5.4,0.9)})", //
        "{6.999999999999999`,6.0`}");
  }

  @Test
  public void testResultBackInDoubleRangeIsMachineNumber() {
    // beyond the double range and back: the nearest double, not the number cut to 16 digits
    check("InputForm((1.0*10^340)/(1.0*10^300))", //
        "1.0`*^40");
    check("MachineNumberQ((1.0*10^340)/(1.0*10^300))", //
        "True");
    // a complex number too
    // the nearest doubles of the exact value with the binary numbers 2.0*^200, 5.0*^200, 1.0*^300
    check("InputForm((2.0*^200+5.0*^200*I)^2/1.0*^300)", //
        "-2.1000000000000003`*^101 + 2.0`*^101*I");
    check("MachineNumberQ((2.0*^200+5.0*^200*I)^2/1.0*^300)", //
        "True");
  }

  @Test
  public void testDivisionEdges() {
    check("1.0/0.0", //
        "ComplexInfinity");
    check("0.0/0.0", //
        "Indeterminate");
    check("InputForm(0.0/0.7)", //
        "0.0`");
    // beyond the double range the usual promotion applies
    check("(3.0*10^300)/(2.0*10^-10)", //
        "1.500000000000000*10^310");
  }
}
