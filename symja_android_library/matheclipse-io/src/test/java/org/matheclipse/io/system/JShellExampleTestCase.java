package org.matheclipse.io.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** Test JShell examples */
public class JShellExampleTestCase extends AbstractTestCase {
  public JShellExampleTestCase(String name) {
    super(name);
  }

  public void test001() {
    String str = F.usage("Im");
    assertEquals(
        str,
        "## Im\n"
            + "\n"
            + "Im(z)\n"
            + "\n"
            + "> returns the imaginary component of the complex number `z`.\n"
            + " \n"
            + "See\n"
            + "* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number)\n"
            + "\n"
            + "### Examples\n"
            + "\n"
            + ">> Im(3+4I)\n"
            + "4\n"
            + "\n"
            + ">> Im(0.5 + 2.3*I)\n"
            + "2.3\n"
            + "\n"
            + "### Related terms \n"
            + "[Complex](Complex.md), [Re](Re.md)\n"
            + "");
  }
}
