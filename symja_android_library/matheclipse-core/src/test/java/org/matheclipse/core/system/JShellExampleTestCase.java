package org.matheclipse.core.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Test JShell examples */
public class JShellExampleTestCase extends ExprEvaluatorTestCase {
  public JShellExampleTestCase(String name) {
    super(name);
  }

  public void test001() {
    IExpr x = S.FactorInteger.ofObject(Integer.valueOf(42));
    assertEquals(x.toString(), //
        "{{2,1},{3,1},{7,1}}");
  }

  public void test002() {
    String str = F.usage("Im");
    assertEquals(str, //
        "## Im\n" + "\n" + "Im(z)\n" + "\n"
            + "> returns the imaginary component of the complex number `z`.\n" + " \n" + "See\n"
            + "* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number)\n"
            + "\n" + "### Examples\n" + "\n" + ">> Im(3+4I)\n" + "4\n" + "\n"
            + ">> Im(0.5 + 2.3*I)\n" + "2.3\n" + "\n" + "### Related terms \n"
            + "[Complex](Complex.md), [Re](Re.md)\n" + "\n" + "### Github\n" + "\n"
            + "* [Implementation of Im](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2297) \n"
            + "[Github master](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L2279)\n"
            + "\n" + "");
  }
}
