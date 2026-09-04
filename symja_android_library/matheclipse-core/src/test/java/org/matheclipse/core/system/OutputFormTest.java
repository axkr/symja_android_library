package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

public class OutputFormTest extends ExprEvaluatorTestCase {

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, "", -1);
  }

  @Test
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

  @Test
  public void testComplexFormatSymbolic() {
    ExprEvaluator evaluator = new ExprEvaluator();
    IExpr input = evaluator.eval("4+(4*I)");
    assertEquals(input.toString(), "4+I*4");

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, true, true, -1, -1);
    String str = outputFormFactory.toString(input);
    assertEquals(str, "4+4*I");

    input = evaluator.eval("-3/4-I*(2/3)");
    assertEquals(input.toString(), "-3/4-I*2/3");

    str = outputFormFactory.toString(input);
    assertEquals(str, "-3/4-2/3*I");

    input = evaluator.eval("a-3/4+I*(2/3)");
    assertEquals(input.toString(), "-3/4+I*2/3+a");

    str = outputFormFactory.toString(input);
    assertEquals(str, "a-3/4+2/3*I");

    input = evaluator.eval("a*(3/4-I*(2/3))");
    assertEquals(input.toString(), "(3/4-I*2/3)*a");

    str = outputFormFactory.toString(input);
    assertEquals(str, "(3/4-2/3*I)*a");
  }

  @Test
  public void testDoubleComplexFormat() {
    ExprEvaluator evaluator = new ExprEvaluator();
    IExpr input = evaluator.eval("4.0+(4.0*I)");
    assertEquals(input.toString(), "(4.0+I*4.0)");

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, true, true, -1, -1);
    String str = outputFormFactory.toString(input);
    assertEquals(str, "4.0+4.0*I");

    input = evaluator.eval("-4.25*I");
    assertEquals(input.toString(), "(-I*4.25)");

    str = outputFormFactory.toString(input);
    assertEquals(str, "-4.25*I");

    input = evaluator.eval("-0.1-4.25*I");
    assertEquals(input.toString(), "(-0.1-I*4.25)");

    str = outputFormFactory.toString(input);
    assertEquals(str, "-0.1-4.25*I");
  }

  @Test
  public void testApcomplexFormat() {
    ExprEvaluator evaluator = new ExprEvaluator();
    IExpr input = evaluator.eval("N(4+(4*I),20)");
    assertEquals(input.toString(), "4+I*4");

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, true, true, -1, -1);
    String str = outputFormFactory.toString(input);
    assertEquals(str, "4+4*I");

    input = evaluator.eval("N(3/7*I,20)");
    assertEquals(input.toString(), "I*0.42857142857142857142");

    str = outputFormFactory.toString(input);
    assertEquals(str, "0.42857142857142857142*I");

    input = evaluator.eval("N(-3/7*I,20)");
    assertEquals(input.toString(), "I*(-0.42857142857142857142)");

    str = outputFormFactory.toString(input);
    assertEquals(str, "-0.42857142857142857142*I");

    input = evaluator.eval("N(a+(-2/3-3/7*I),20)");
    assertEquals(input.toString(), "-0.66666666666666666666+I*(-0.42857142857142857142)+a");

    str = outputFormFactory.toString(input);
    assertEquals(str, "a-0.66666666666666666666-0.42857142857142857142*I");
  }

  @Test
  public void testInfix() {
    check("Infix(f(x,y,z))", //
        "x ~ y ~ z");
    check("Infix(f(x,y,z), \"$~$\")", //
        "x $~$ y $~$ z");
  }

  @Test
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

  @Test
  public void testPreFix() {
    check("PreFix(f(x))", //
        "f @ x");
    check("PreFix(f(x), \"$-$\")", //
        "$-$ x");
  }


  @Test
  public void testPostFix() {
    check("PostFix(f(x))", //
        "x // f");
    check("PostFix(f(x), \"$+$\")", //
        "x $+$");
  }

  @Override
  public void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }

  /**
   * <code>Grid, Pane, TableForm</code> are display wrappers: they survive evaluation and only the
   * printed representation changes. Evaluating them repeatedly in one session must keep returning
   * the same result.
   */
  @Test
  public void testCases() {
    check("Grid({{a, b}, {c, d}})", "Grid({{a,b},{c,d}})");
    check("Grid({{a, b}, {c, d}}); Grid({a, b, c})", "Grid({a,b,c})");
    // OutputForm prints strings without the surrounding quotes
    check(
        "Grid({{a, b}, {c, d}}); Grid({a, b, c}); Grid({{\"first\", \"second\", \"third\"},{a},{1, 2, 3}})",
        "Grid({{first,second,third},{a},{1,2,3}})");
    check(
        "Grid({{a, b}, {c, d}}); Grid({a, b, c}); Grid({{\"first\", \"second\", \"third\"},{a},{1, 2, 3}}); Grid({\"This is a long title\", {\"first\", \"second\", \"third\"},{a},{1, 2, 3}})",
        "Grid({This is a long title,{first,second,third},{a},{1,2,3}})");
    // ... InputForm keeps them, so the grid can be read back
    check("InputForm(Grid({{\"first\", \"second\", \"third\"},{a},{1, 2, 3}}))",
        "Grid({{\"first\",\"second\",\"third\"},{a},{1,2,3}})");
    check("Pane(37!)", "Pane(13763753091226345046315979581580902400000000)");
    check("Pane(37!); {{Pane(a,3), Pane(expt, 3)}}//TableForm//TeXForm",
        "\\begin{array}{cc}\n \\text{Pane}(a,3) & \\text{Pane}(expt,3) \\\\\n\\end{array}");
    // the delayed rule is held, so the option value stays unevaluated
    check("Grid({{a,bc},{d,e}}, ColumnAlignments:>Symbol(\"Rig\"<>\"ht\"))",
        "Grid({{a,bc},{d,e}},ColumnAlignments:>Symbol(Rig<>ht))");
    // Grid is set as an array. ColumnAlignments is not a Wolfram option and is ignored; the
    // Alignment option is the one that moves the columns.
    check(
        "Grid({{a,bc},{d,e}}, ColumnAlignments:>Symbol(\"Rig\"<>\"ht\")); TeXForm@Grid({{a,bc},{d,e}}, ColumnAlignments->Left)",
        "\\begin{array}{cc}\n a & bc \\\\\n d & e \\\\\n\\end{array}");
    check(
        "Grid({{a,bc},{d,e}}, ColumnAlignments:>Symbol(\"Rig\"<>\"ht\")); TeXForm@Grid({{a,bc},{d,e}}, ColumnAlignments->Left); TeXForm(TableForm({{a,b},{c,d}}))",
        "\\begin{array}{cc}\n a & b \\\\\n c & d \\\\\n\\end{array}");
  }

  /**
   * <code>Overlay</code> composites graphics; an overlay of anything else is inert and has to keep
   * printing as itself rather than being swallowed by the graphics converter.
   */
  @Test
  public void testOverlayOfPlainExpressionsStaysInert() {
    check("Overlay({1, 2, 3})", "Overlay({1,2,3})");
    check("Overlay({1, 2, 3}); Overlay({a, b}, {2}, None)", "Overlay({a,b},{2},None)");
    check("Overlay({1, 2, 3}); Overlay({a, b}, {2}, None); Overlay(x)", "Overlay(x)");
  }

  /** <code>TableForm</code> stays in the expression tree - only its printed form is a table. */
  @Test
  public void testTableFormIsADisplayWrapper() {
    check("Head(TableForm({{a,b},{c,d}}))", "TableForm");
    check("FullForm(TableForm({{a,b},{c,d}}))", "TableForm(List(List(a, b), List(c, d)))");
    check("TableForm({{a,b},{c,d}})", " a  b \n c  d ");
  }
}
