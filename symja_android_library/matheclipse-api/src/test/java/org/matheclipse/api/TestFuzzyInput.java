package org.matheclipse.api;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.api.parser.FuzzyParserFactory;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class TestFuzzyInput {
  static {
    ToggleFeature.COMPILE = false;
    ToggleFeature.COMPILE_PRINT = true;
    Config.FUZZY_PARSER = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    Config.JAS_NO_THREADS = false;
    Config.MATHML_TRIG_LOWERCASE = false;
    Config.MAX_AST_SIZE = 10000;
    Config.MAX_OUTPUT_SIZE = 10000;
    Config.MAX_BIT_LENGTH = 200000;
    Config.MAX_INPUT_LEAVES = 100L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    EvalEngine.get().setPackageMode(true);
    F.initSymbols();
    FuzzyParserFactory.initialize();
  }

  @Test
  public void testExpand001() {
    IExpr expr = Pods.parseInput("(a+b)^4 expanded", EvalEngine.get());
    assertEquals("ExpandAll((a+b)^4)", expr.toString());
  }

  @Test
  public void testExpand002() {
    IExpr expr = Pods.parseInput("expanding f((a+b)^4)", EvalEngine.get());
    assertEquals("ExpandAll(f((a+b)^4))", expr.toString());
  }

  @Test
  public void testIntegerate001() {
    IExpr expr = Pods.parseInput("(a+b)^4 integrated", EvalEngine.get());
    assertEquals("Integrate((a+b)^4)", expr.toString());
  }

  @Test
  public void testSimplify001() {
    IExpr expr = Pods.parseInput("Simplify x^2+4*x+4", EvalEngine.get());
    assertEquals("FullSimplify(x^2+4*x+4)", expr.toString());
  }

  @Test
  public void testList001() {
    IExpr expr = Pods.parseInput("3,Sin(1),Pi,3/4,42,1.2", EvalEngine.get());
    assertEquals("{3,Sin(1),Pi,3/4,42,1.2}", expr.toString());

    expr = Pods.parseInput("[3,Sin(1),Pi,3/4,42,1.2]", EvalEngine.get());
    assertEquals("{3,Sin(1),Pi,3/4,42,1.2}", expr.toString());
  }

  @Test
  public void testList002() {
    IExpr expr = Pods.parseInput("plot(sin(x),(x,-10,10))", EvalEngine.get());
    assertEquals("Plot(Sin(x),{x,-10,10})", expr.toString());

    expr = Pods.parseInput("plot(sin(x),{x,-10,10})", EvalEngine.get());
    assertEquals("Plot(Sin(x),{x,-10,10})", expr.toString());
  }

  @Test
  public void testMatrix001() {
    IExpr expr = Pods.parseInput("1, 17 + 4*I\n17 - 4*I, 10", EvalEngine.get());
    assertEquals("{{1,17+4*I},{17+(-4)*I,10}}", expr.toString());
  }

  @Test
  public void testMatrix002() {
    IExpr expr = Pods.parseInput("1, 17 + (\n 4*I)\n17 - 4*I, 10", EvalEngine.get());
    assertEquals("{{1,17+4*I},{17+(-4)*I,10}}", expr.toString());
  }

  @Test
  public void testLogic001() {
    IExpr expr = Pods.parseInput("a&&b||c", EvalEngine.get());
    assertEquals("(a&&b)||c", expr.toString());

    expr = Pods.parseInput("a&b|c", EvalEngine.get());
    assertEquals("(a&&b)||c", expr.toString());
  }

  @Test
  public void testTeX001() {
    IExpr expr = Pods.parseInput("\\sin 30 ^ { \\circ }", EvalEngine.get());
    assertEquals("Sin(30*Degree)", expr.toString());
  }
}
