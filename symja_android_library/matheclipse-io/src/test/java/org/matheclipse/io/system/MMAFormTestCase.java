package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.FEConfig;

/** */
public class MMAFormTestCase extends AbstractTestCase {
  public MMAFormTestCase(String name) {
    super(name);
  }

  public void testWLForm001() {
    IAST function = Sinc(Times(CI, CInfinity));

    assertEquals(
        function.toMMA(), //
        "Sinc[Times[I, DirectedInfinity[1]]]");
  }

  public void testWLForm002() {
    IAST function = Sinc(Times(CI, CInfinity));
    assertEquals(
        function.toMMA(), //
        "Sinc[Times[I, DirectedInfinity[1]]]");
  }

  public void testWLForm003() {
    IAST function = F.Together(Times(2, F.x));
    assertEquals(
        function.toMMA(), //
        "Together[Times[2, x]]");
  }

  public void testWLForm004() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("{f'(x), f''(x), f'''(x)} // Together");
    assertEquals(expr.toMMA(), "Together[{Derivative[1][f][x], Derivative[2][f][x], Derivative[3][f][x]}]");
  }
}
