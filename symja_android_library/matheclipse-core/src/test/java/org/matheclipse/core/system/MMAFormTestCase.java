package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/** */
public class MMAFormTestCase extends ExprEvaluatorTestCase {
  public MMAFormTestCase(String name) {
    super(name);
  }

  public void testWLForm001() {
    IAST function = Sinc(Times(CI, CInfinity));

    assertEquals(function.toMMA(), "Sinc[I*Infinity]");
  }

  public void testWLForm002() {
    IAST function = Sinc(Times(CI, CInfinity));
    assertEquals(function.toMMA(), "Sinc[I*Infinity]");
  }

  public void testWLForm003() {
    IAST function = F.Together(Times(2, F.x));
    assertEquals(function.toMMA(), "Together[2*x]");
  }

  public void testWLForm004() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("{f'(x), f''(x), f'''(x)} // Together");
    assertEquals(expr.toMMA(), "Together[{f'[x],f''[x],Derivative[3][f][x]}]");
  }

  public void testWLForm005() {
    IAST function = F.Solve(F.Equal(F.Denominator(F.x), F.C0), F.x);
    assertEquals(function.toMMA(), "Solve[Denominator[x]==0,x]");
  }

  public void testWLForm006() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("a+i*b^2+k*c^3+d");
    assertEquals(expr.toMMA(), "a + i*b^2 + k*c^3 + d");
  }
}
