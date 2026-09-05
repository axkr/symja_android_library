package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Dummy;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class LogCombineTest {

  private EvalEngine engine;

  @BeforeEach
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
    IExpr result = org.matheclipse.core.reflection.system.LimitGruntz.logCombine(expr, true);

    assertEquals(Log(Times(x, y)), result);
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

    IExpr result = org.matheclipse.core.reflection.system.LimitGruntz.logCombine(expr, true);


    assertEquals(Times(z, Log(Times(a, b))), result);
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

    IExpr result = org.matheclipse.core.reflection.system.LimitGruntz.logCombine(expr, true);

    // (1/z) * Log((1/2 + z)/z) expanded to (1/z) * Log(1 + 1/(2z)) - the additive form this
    // test's javadoc asks for, and the one the Stirling/Gruntz callers need: it exposes the
    // 1 + o(1) shape of the argument as z -> Infinity, which the quotient form (1 + 2z)/(2z)
    // hides. logCombine merges grouped arguments with ExpandAll for exactly that reason.
    IExpr expected = Times(Power(z, CN1), Log(Plus(C1, Times(C1D2, Power(z, CN1)))));

    assertEquals(expected, result);
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

    IExpr result = org.matheclipse.core.reflection.system.LimitGruntz.logCombine(expr, true);

    assertEquals(Plus(constant, Log(Times(x, y))), result);
  }

  /**
   * Test subtraction: Log(x) - Log(y) -> Log(x/y)
   */
  @Test
  public void testSubtraction() {
    ISymbol x = Dummy("x");
    ISymbol y = Dummy("y");
    IExpr expr = Plus(Log(x), Times(CN1, Log(y)));

    IExpr result = org.matheclipse.core.reflection.system.LimitGruntz.logCombine(expr, true);

    assertEquals(Log(Times(x, Power(y, CN1))), result);
  }
}