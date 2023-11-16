package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

import static org.junit.Assert.assertEquals;

/** Test JShell examples */
public class JShellExampleTestCase extends ExprEvaluatorTestCase {

  @Test
  public void test001() {
    IExpr x = S.FactorInteger.ofObject(Integer.valueOf(42));
    assertEquals(x.toString(), //
        "{{2,1},{3,1},{7,1}}");
  }

  @Test
  public void test002() {
    String str = F.usage("Im");
    assertEquals(str.substring(0, str.length() - 450), //
        "## Im\n" //
            + "\n" //
            + "Im(z)\n" //
            + "\n" //
            + "> returns the imaginary component of the complex number `z`.\n" //
            + " \n" //
            + "See\n" //
            + "* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number)\n" //
            + "\n" //
            + "### Examples\n" //
            + "\n" //
            + ">> Im(3+4*I)\n" //
            + "4\n" //
            + "\n" //
            + ">> Im(0.5 + 2.3*I)\n" //
            + "2.3\n" //
            + "\n" //
            + "### Related terms \n" //
            + "[Complex](Complex.md), [Re](Re.md), [ReI");
  }
}
