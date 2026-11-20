package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The numbers which <code>N(expr)</code> and <code>N(expr, precision)</code> convert: every number
 * on a level which isn't held, also inside calls of functions without definitions; a machine number
 * of the input stays a machine number; a machine zero stays the machine zero, an exact zero stays
 * exact, and a zero which is left of a cancellation has the accuracy which was reached.
 */
public class NNumberConversionTest extends ExprEvaluatorTestCase {

  @Test
  public void testMachineZero() {
    check("InputForm(N(0.0, 35))", //
        "0.0`");
    check("InputForm(N(a*4.75 - 4.75*b /. {a -> 2.2, b -> 2.2}, 25))", //
        "0.0`");
    check("InputForm(N(Complex(0.0, 0.0), 40))", //
        "0.0`");
    // a machine expression which cancels: the machine zero, not a zero without any precision
    check("InputForm(N((a + b)^2 - a^2 - 2*a*b - b^2 /. {a -> 0.5, b -> 1.25}, 30))", //
        "0.0`");
    check("{Precision(N(0.0, 30)) == Precision(0.0), Accuracy(N(0.0, 30)) > 300}", //
        "{True,True}");
  }

  @Test
  public void testExactZero() {
    check("InputForm(N(0, 30))", //
        "0");
    check("InputForm(N({0, 3/8, 0.0}, 20))", //
        "{0,3.75`20*^-1,0.0`}");
    check("Precision(N(Sin(3*Pi), 25))", //
        "Infinity");
  }

  @Test
  public void testZeroOfCancellation() {
    // an exact difference which is zero: a zero of the accuracy reached
    check("Precision(N((2+Sqrt(5))^2 - 9 - 4*Sqrt(5), 30))", //
        "0");
    check("Accuracy(N((2+Sqrt(5))^2 - 9 - 4*Sqrt(5), 30)) > 30", //
        "True");
  }

  @Test
  public void testNumbersInsideCalls() {
    check("InputForm(N(w(2/5), 25))", //
        "w(4`25*^-1)");
    check("InputForm(N(w(2.5, 0, 1/4), 20))", //
        "w(2.5`,0,2.5`20*^-1)");
    check("N(x + w(v(1/4), 3))", //
        "x+w(v(0.25),3.0)");
    check("N({w(v(5/8))}, 20)", //
        "{w(v(0.625))}");
    // held arguments stay
    check("N(Hold(1/4), 20)", //
        "Hold(1/4)");
    // the calls of unknowns in a solution keep their arguments
    check("Solve(z == 0.25*w(1/3) + v(k + 1), z)", //
        "{{z->v(1+k)+0.25*w(1/3)}}");
  }
}
