package org.matheclipse.core.sympy.simplify;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Dummy;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.S.Together;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class LogCombineTest {

  private EvalEngine engine;

  @Before
  public void setUp() {
    engine = new EvalEngine();
  }

  /**
   * Test basic Log(x) + Log(y) -> Log(x*y)
   */
  @Test
  public void testSimpleAddition() {
    ISymbol x = Dummy("x");
    ISymbol y = Dummy("y");
    IExpr expr = Plus(Log(x), Log(y));

    // Using force=true to combine symbols without explicit positive assumptions
    IExpr result = org.matheclipse.core.sympy.simplify.Simplify.logCombine(expr, true);

    Assert.assertEquals(Log(Times(x, y)), result);
  }

  /**
   * Test coefficient grouping: z*Log(a) + z*Log(b) -> z*Log(a*b)
   */
  @Test
  public void testCoefficientGrouping() {
    ISymbol z = Dummy("z");
    ISymbol a = Dummy("a");
    ISymbol b = Dummy("b");
    IExpr expr = Plus(Times(z, Log(a)), Times(z, Log(b)));

    IExpr result = org.matheclipse.core.sympy.simplify.Simplify.logCombine(expr, true);

    Assert.assertEquals(Times(z, Log(Times(a, b))), result);
  }

  /**
   * Test the specific Stirling/Gruntz case: z*Log(1/2 + z) - z*Log(z) -> z*Log((1/2 + z)/z) ->
   * z*Log(1 + 1/(2z))
   */
  @Test
  public void testStirlingCase() {
    ISymbol z = Dummy("z");
    // Log(1/2 + z) / z - Log(z) / z
    IExpr term1 = Times(Power(z, CN1), Log(Plus(C1D2, z)));
    IExpr term2 = Times(CN1, Power(z, CN1), Log(z));
    IExpr expr = Plus(term1, term2);

    IExpr result = org.matheclipse.core.sympy.simplify.Simplify.logCombine(expr, true);

    // The result should have combined arguments simplified by Together
    // Result: (1/z) * Log( (1/2 + z) / z ) => (1/z) * Log( 1 + 1/(2z) )
    IExpr expectedArg = Together.of(Times(Plus(C1D2, z), Power(z, CN1)));
    IExpr expected = Times(Power(z, CN1), Log(expectedArg));

    Assert.assertEquals(expected, result);
  }

  /**
   * Test that non-logarithmic terms remain untouched.
   */
  @Test
  public void testMixedTerms() {
    ISymbol x = Dummy("x");
    ISymbol y = Dummy("y");
    IExpr constant = ZZ(7);
    IExpr expr = Plus(constant, Log(x), Log(y));

    IExpr result = org.matheclipse.core.sympy.simplify.Simplify.logCombine(expr, true);

    Assert.assertEquals(Plus(constant, Log(Times(x, y))), result);
  }

  /**
   * Test subtraction: Log(x) - Log(y) -> Log(x/y)
   */
  @Test
  public void testSubtraction() {
    ISymbol x = Dummy("x");
    ISymbol y = Dummy("y");
    IExpr expr = Plus(Log(x), Times(CN1, Log(y)));

    IExpr result = org.matheclipse.core.sympy.simplify.Simplify.logCombine(expr, true);

    Assert.assertEquals(Log(Times(x, Power(y, CN1))), result);
  }
}