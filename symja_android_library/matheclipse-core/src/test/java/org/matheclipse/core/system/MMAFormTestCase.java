package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/** */
public class MMAFormTestCase extends ExprEvaluatorTestCase {

  @Test
  public void testWLForm001() {
    IAST function = Sinc(Times(CI, CInfinity));

    assertEquals(function.toMMA(), "Sinc[I*Infinity]");
  }

  @Test
  public void testWLForm002() {
    IAST function = Sinc(Times(CI, CInfinity));
    assertEquals(function.toMMA(), "Sinc[I*Infinity]");
  }

  @Test
  public void testWLForm003() {
    IAST function = F.Together(Times(2, F.x));
    assertEquals(function.toMMA(), "Together[2*x]");
  }

  @Test
  public void testWLForm004() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("{f'(x), f''(x), f'''(x)} // Together");
    assertEquals(expr.toMMA(), "Together[{f'[x],f''[x],Derivative[3][f][x]}]");
  }

  @Test
  public void testWLForm005() {
    IAST function = F.Solve(F.Equal(F.Denominator(F.x), F.C0), F.x);
    assertEquals(function.toMMA(), "Solve[Denominator[x]==0,x]");
  }

  @Test
  public void testWLForm006() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("a+i*b^2+k*c^3+d");
    assertEquals(expr.toMMA(), "a + i*b^2 + k*c^3 + d");
  }

  /**
   * The non-finite machine doubles have to be named the way the Wolfram Language names them.
   * Symja's own input form appends the precision marker to <code>Double.toString()</code> and so
   * prints <code>Infinity`</code>, <code>-Infinity`</code> and <code>NaN`</code>, all of which
   * Symja reads back. The Wolfram Language reads none of them: a backtick separates a context from
   * a symbol there rather than marking precision, and it calls <code>NaN</code>
   * <code>Indeterminate</code>.
   */
  @Test
  public void testWLNonFiniteDoubles() {
    assertEquals(F.num(Double.POSITIVE_INFINITY).toMMA(), "Infinity");
    assertEquals(F.num(Double.NEGATIVE_INFINITY).toMMA(), "-Infinity");
    assertEquals(F.num(Double.NaN).toMMA(), "Indeterminate");
    assertEquals(F.List(F.C1, F.num(Double.POSITIVE_INFINITY), F.C2).toMMA(),
        "{1,Infinity,2}");
  }

  /** A leading minus has to be parenthesised exactly as it is for a finite negative number. */
  @Test
  public void testWLNonFiniteDoublesPrecedence() {
    IExpr negativeInfinity = F.num(Double.NEGATIVE_INFINITY);
    assertEquals(F.Plus(F.C1, negativeInfinity).toMMA(), "-Infinity + 1");
    assertEquals(F.Times(F.C2, negativeInfinity).toMMA(), "(-Infinity)*2");
    assertEquals(F.Power(negativeInfinity, F.C2).toMMA(), "(-Infinity)^2");
    assertEquals(F.Sin(negativeInfinity).toMMA(), "Sin[-Infinity]");
  }

  /** A machine complex carries the same names into both of its parts. */
  @Test
  public void testWLNonFiniteComplex() {
    assertEquals(F.complexNum(Double.POSITIVE_INFINITY, 1.0).toMMA(), "Infinity + I*1.0`");
    assertEquals(F.complexNum(1.0, Double.NaN).toMMA(), "1.0` + I*Indeterminate");
  }

  @Test
  public void testWLHeadTest() {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    ExprParser parser = new ExprParser(engine, true);
    IExpr expr = parser.parse("f=compile({{x, _real}}, E^3-cos(Pi^2/x));");
    assertEquals(expr.toMMA(), "f=Compile[{{x,_Real}},E^3 - Cos[Pi^2/x]];Null");
  }

}
