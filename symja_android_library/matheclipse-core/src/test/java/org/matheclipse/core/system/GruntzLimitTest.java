package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.LimitGruntz;

/**
 * Unit tests for the {@link LimitGruntz} helpers plus evaluator-level regressions for the bugs
 * fixed in the 2026-08 Limit overhaul (two-Log-product corruption in logCombine, mrv divergence
 * gate for bounded powers, direction mapping of the reciprocal substitutions, rewrite
 * abort-on-failure).
 */
public class GruntzLimitTest extends ExprEvaluatorTestCase {

  private EvalEngine engine() {
    EvalEngine engine = evaluator.getEvalEngine();
    EvalEngine.set(engine);
    return engine;
  }

  /**
   * {@link Simplify#singleLogTermParts(IExpr)} must refuse to split a product of TWO logarithms:
   * extracting either Log would silently drop the other factor (this corrupted
   * <code>Log(x)*Log(Log(x)) - Log(x)</code> into <code>Log(Log(x)/x)</code>, turning a +Infinity
   * expression into a -Infinity one).
   */
  @Test
  public void testSingleLogTermPartsTwoLogProduct() {
    engine();
    IExpr twoLogs = evaluator.eval("Log(x)*Log(Log(x))");
    assertNull(LimitGruntz.singleLogTermParts(twoLogs));

    IExpr plain = evaluator.eval("Log(x)");
    IExpr[] parts = LimitGruntz.singleLogTermParts(plain);
    assertNotNull(parts);
    assertEquals("1", parts[0].toString());
    assertEquals("x", parts[1].toString());

    // -Log(x): the numeric -1 moves into the argument so subtraction merges into a quotient
    IExpr negated = evaluator.eval("-Log(x)");
    parts = LimitGruntz.singleLogTermParts(negated);
    assertNotNull(parts);
    assertEquals("1", parts[0].toString());
    assertEquals("1/x", parts[1].toString());

    // coefficient extraction
    IExpr scaled = evaluator.eval("3*z*Log(a)");
    parts = LimitGruntz.singleLogTermParts(scaled);
    assertNotNull(parts);
    assertEquals("3*z", parts[0].toString());
    assertEquals("a", parts[1].toString());

    // no Log factor at all
    assertNull(LimitGruntz.singleLogTermParts(evaluator.eval("3*z")));
  }

  /** logCombine must preserve the VALUE of a sum containing a two-Log product term. */
  @Test
  public void testLogCombineKeepsTwoLogProducts() {
    EvalEngine engine = engine();
    ISymbol x = (ISymbol) evaluator.eval("x");
    IExpr input = evaluator.eval("Log(x)*Log(Log(x)) - Log(x)");
    IExpr combined = LimitGruntz.logCombine(input, true);
    // numeric value-preservation probe at x = 100 (robust against representation changes)
    IExpr difference = engine.evaluate(F.subst(F.Subtract(combined, input), x, F.ZZ(100)));
    double error = Math.abs(engine.evaluate(F.N(difference)).evalDouble());
    assertTrue(error < 1.0e-10, "logCombine changed the value, difference: " + difference);
    // and the product term itself must survive
    assertTrue(combined.toString().contains("Log(x)*Log(Log(x))"),
        "two-Log product was split: " + combined);
  }

  /** combineExponentials must not discard rewrites nested below a non-exponential factor. */
  @Test
  public void testCombineExponentialsNested() {
    EvalEngine engine = engine();
    // build f(E^a*E^b)*x without engine pre-combination: substitute into an inert head
    IExpr inner = F.Times(F.Power(S.E, F.Dummy("a")), F.Power(S.E, F.Dummy("b")));
    IExpr expr = F.Times(evaluator.eval("x"), F.unaryAST1(F.Dummy("f"), inner));
    IExpr combined = LimitGruntz.combineExponentials(expr, engine);
    assertTrue(combined.toString().contains("E^(a+b)"),
        "nested exponential combination was discarded: " + combined);
  }

  /**
   * The mrv Power cases only admit rapidly-varying candidates whose exponent diverges (the E-case
   * restriction, applied uniformly): <code>2^(1/x)</code> is bounded and must NOT be an mrv
   * element, while <code>2^x</code> must be.
   */
  @Test
  public void testMrvBoundedPowerGate() {
    EvalEngine engine = engine();
    ISymbol x = (ISymbol) evaluator.eval("x");
    IExpr bounded = evaluator.eval("2^(1/x)");
    IExpr mrvBounded = LimitGruntz.mrv(bounded, x, engine);
    assertTrue(mrvBounded.isAST(), "mrv failed on 2^(1/x)");
    assertTrue(!((IAST) mrvBounded).contains(bounded),
        "bounded power entered the mrv set: " + mrvBounded);

    IExpr divergent = evaluator.eval("2^x");
    IExpr mrvDivergent = LimitGruntz.mrv(divergent, x, engine);
    assertTrue(mrvDivergent.isAST() && ((IAST) mrvDivergent).contains(divergent),
        "divergent power missing from the mrv set: " + mrvDivergent);
  }

  /** Basic asymptotic sign queries. */
  @Test
  public void testSignInf() {
    EvalEngine engine = engine();
    ISymbol x = (ISymbol) evaluator.eval("x");
    assertEquals(1, LimitGruntz.signInf(evaluator.eval("E^x"), x, engine));
    assertEquals(-1, LimitGruntz.signInf(evaluator.eval("-3*x^2"), x, engine));
    assertEquals(1, LimitGruntz.signInf(evaluator.eval("x-10"), x, engine));
  }

  /**
   * End-to-end regressions for the two-Log-product fix: the Stirling pipeline expands
   * <code>LogGamma(Log(x))</code> to exactly the vulnerable
   * <code>Log(x)*Log(Log(x)) - Log(x) + ...</code> shape before logCombine(force) runs.
   */
  @Test
  public void testLimitLogGammaOfLog() {
    check("Limit(LogGamma(Log(x))/(Log(x)*Log(Log(x))), x->Infinity)", //
        "1");
    check("Limit(LogGamma(Log(x)) - Log(x)*Log(Log(x)), x->Infinity)", //
        "-Infinity");
    check("Limit(Log(Gamma(Log(x)))/(Log(x)*Log(Log(x))), x->Infinity)", //
        "1");
    check("Limit(E^(Log(x)*Log(Log(x)) - Log(x)), x->Infinity)", //
        "Infinity");
    check("Limit(E^(Log(x)*Log(Log(x)) - Log(x))/Sqrt(x), x->Infinity)", //
        "Infinity");
  }

  /** Bounded powers and the classic e-limit keep working through the gated mrv Power cases. */
  @Test
  public void testLimitBoundedAndVariablePowers() {
    check("Limit(2^(1/x), x->Infinity)", //
        "1");
    check("Limit((1+1/x)^x, x->Infinity)", //
        "E");
    check("Limit((2^x+3^x)^(1/x), x->Infinity)", //
        "3");
  }

  /**
   * Direction mapping of the zero-point reciprocal substitution: <code>x*E^(1/x)</code> is
   * asymmetric at 0 - the two-sided limit must stay Indeterminate, never the one-sided +Infinity.
   */
  @Test
  public void testLimitReciprocalDirections() {
    check("Limit(x*E^(1/x), x->0)", //
        "Indeterminate");
    check("Limit(x*E^(1/x), x->0, Direction->\"FromAbove\")", //
        "Infinity");
    check("Limit(x*E^(1/x), x->0, Direction->\"FromBelow\")", //
        "0");
  }
  /**
   * Exponential races between different bases. Rewriting in w produces an irrational w-power such
   * as <code>w^(1-Log(5)/Log(3))</code>, whose exponent the rational-only Puiseux lattice cannot
   * represent - it used to drop the term silently and report the wrong finite limit 1 for the
   * first case below. The leading-term primitive carries the exponent as a plain real, so the
   * ordinary algorithm handles it.
   */
  @Test
  public void testIrrationalExponentRace() {
    // 5^x outruns 3^x, so the quotient vanishes
    assertEquals("0", evaluator.eval("Limit(3^x/(3^x+5^x), x->Infinity)").toString());
    // and the other way round the quotient tends to 1
    assertEquals("1", evaluator.eval("Limit(5^x/(3^x+5^x), x->Infinity)").toString());
    // equal bases: the race is a tie
    assertEquals("1/2", evaluator.eval("Limit(3^x/(3^x+3^x), x->Infinity)").toString());
  }

}
