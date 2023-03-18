package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

public class OutputFormTest extends ExprEvaluatorTestCase {
  public OutputFormTest(String name) {
    super(name);
  }

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  public void testPlusReversed() {
    // github issue 694
    ExprEvaluator evaluator = new ExprEvaluator();
    IExpr input = evaluator.eval("I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");
    assertEquals(input.toString(), "I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false);
    String str = outputFormFactory.toString(input);
    assertEquals(str, "I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");

    OutputFormFactory outputFormFactory2 = OutputFormFactory.get(true, true);
    String str2 = outputFormFactory2.toString(input);
    assertEquals(str2, "27*Sqrt(3)+(-46+I*46)*Sqrt(2)+I*27");

    assertEquals(evaluator.evalComplex(str), evaluator.evalComplex(str2));

  }

  public void testInfix() {
    check("Infix(f(x,y,z))", //
        "x ~ y ~ z");
    check("Infix(f(x,y,z), \"$~$\")", //
        "x $~$ y $~$ z");
  }

  public void testPrecedenceForm() {
    // Times precedence == 400
    check("Times(b, PrecedenceForm(3*A ,400))", //
        "b*(3*A)");
    check("Times(b, PrecedenceForm(3*A ,401))", //
        "b*3*A");

    check("Times(b, PrecedenceForm(A ,400))", //
        "b*(A)");
    check("Times(b, PrecedenceForm(A ,401))", //
        "b*A");
    check("Times(b, PrecedenceForm(A ,399))", //
        "b*(A)");

    check("a+PrecedenceForm(b*c, 10)", //
        "a+(b*c)");

    check("s=Infix(f(a,b), \"|>\")", //
        "a |> b");
    check("PrecedenceForm(s, 350)+y", //
        "y+a |> b");
    check("PrecedenceForm(s, 350)*y", //
        "y*(a |> b)");

    check("PrecedenceForm(s, 500)+y", //
        "y+a |> b");
    check("PrecedenceForm(s, 500)*y", //
        "y*a |> b");

    check("PrecedenceForm(s, 1)+y", //
        "y+(a |> b)");
    check("PrecedenceForm(s, 1)*y", //
        "y*(a |> b)");
  }

  public void testPreFix() {
    check("PreFix(f(x))", //
        "f @ x");
    check("PreFix(f(x), \"$-$\")", //
        "$-$ x");
  }


  public void testPostFix() {
    check("PostFix(f(x))", //
        "x // f");
    check("PostFix(f(x), \"$+$\")", //
        "x $+$");
  }

  @Override
  protected void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }

}
