package org.matheclipse.core.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Tests for {@link LeadTerm}, the leading-term primitive at <code>t -&gt; 0+</code>.
 *
 * <p>
 * The case list is drawn from SymPy's <code>test_as_leading_term</code> / <code>test_leadterm</code>
 * plus the per-head shapes Symja already knew about in {@code ASTSeriesData.leadingTerm}. The
 * "declines" tests matter as much as the positive ones: this primitive must return {@code null}
 * rather than guess, because its callers treat a non-null answer as authoritative.
 */
public class LeadTermTest extends ExprEvaluatorTestCase {

  private EvalEngine engine() {
    EvalEngine engine = evaluator.getEvalEngine();
    EvalEngine.set(engine);
    return engine;
  }

  private ISymbol t() {
    return (ISymbol) evaluator.eval("t");
  }

  /** Assert the leading term of the parsed expression is {@code coeff * t^exponent}. */
  private void checkLead(String input, String coefficient, String exponent) {
    EvalEngine engine = engine();
    ISymbol t = t();
    IExpr f = evaluator.eval(input);
    Lead lead = LeadTerm.leadTerm(f, t, engine);
    assertNotNull(lead, "no leading term found for " + input);
    assertEquals(coefficient, lead.coefficient().toString(), "coefficient of " + input);
    assertEquals(exponent, lead.exponent().toString(), "exponent of " + input);
  }

  /** Assert that the primitive declines rather than guessing. */
  private void checkDeclines(String input) {
    EvalEngine engine = engine();
    Lead lead = LeadTerm.leadTerm(evaluator.eval(input), t(), engine);
    assertNull(lead, "expected no leading term for " + input + " but got " + lead);
  }

  @Test
  public void testConstantAndVariable() {
    checkLead("3", "3", "0");
    checkLead("t", "1", "1");
    checkLead("a", "a", "0");
  }

  @Test
  public void testPolynomialAndLaurent() {
    // the lowest power wins, whatever order the summands are written in
    checkLead("1+t+t^2", "1", "0");
    checkLead("t+t^2", "1", "1");
    checkLead("1/t^2+t+t^2", "1", "-2");
    checkLead("3*t^4", "3", "4");
    checkLead("2/t-5", "2", "-1");
  }

  @Test
  public void testPuiseuxExponents() {
    checkLead("Sqrt(t)+t", "1", "1/2");
    checkLead("t^(2/3)+t^(1/3)", "1", "1/3");
  }

  /**
   * The exponent is a plain t-free real, not a lattice point, so an irrational exponent is
   * representable. This is the shape that the rational-only Puiseux lattice used to drop silently,
   * turning <code>3^x/(3^x+5^x)</code> into a wrong finite limit.
   */
  @Test
  public void testIrrationalExponent() {
    EvalEngine engine = engine();
    ISymbol t = t();
    IExpr f = evaluator.eval("t^(1-Log(5)/Log(3))+t");
    Lead lead = LeadTerm.leadTerm(f, t, engine);
    assertNotNull(lead);
    assertEquals("1-Log(5)/Log(3)", lead.exponent().toString());
    assertEquals(-1, lead.exponentSign(engine), "1-Log(5)/Log(3) is negative");
  }

  @Test
  public void testProductsAndQuotients() {
    checkLead("t*(1+t)", "1", "1");
    checkLead("(1+t)/t", "1", "-1");
    checkLead("t^2*(3+t)", "3", "2");
  }

  @Test
  public void testTrigonometricAtZero() {
    checkLead("Sin(t)", "1", "1");
    checkLead("Sin(3*t)", "3", "1");
    checkLead("Tan(t)", "1", "1");
    checkLead("Cos(t)", "1", "0");
    checkLead("Sin(t)/t", "1", "0");
    checkLead("Sin(t^2)", "1", "2");
  }

  @Test
  public void testReciprocalTrigonometric() {
    checkLead("Cot(t)", "1", "-1");
    checkLead("Csc(t)", "1", "-1");
    checkLead("Csc(2*t)", "1/2", "-1");
  }

  @Test
  public void testHyperbolicAndInverse() {
    checkLead("Sinh(t)", "1", "1");
    checkLead("Tanh(t)", "1", "1");
    checkLead("ArcSin(t)", "1", "1");
    checkLead("ArcTan(t)", "1", "1");
    checkLead("ArcSinh(t)", "1", "1");
  }

  @Test
  public void testErrorFunctions() {
    checkLead("Erf(t)", "2/Sqrt(Pi)", "1");
    checkLead("Erfc(t)", "1", "0");
  }

  /** Gamma has a simple pole at zero and at every negative integer. */
  @Test
  public void testGammaPoles() {
    checkLead("Gamma(t)", "1", "-1");
    checkLead("Gamma(t-1)", "-1", "-1");
    checkLead("Gamma(2*t)", "1/2", "-1");
  }

  /**
   * Cancellation: the structural pass sees the minimal-order parts annihilate, and only a series
   * expansion finds the term that actually survives.
   */
  @Test
  public void testCancellationFallsBackToSeries() {
    checkLead("Sin(t)-t", "-1/6", "3");
    checkLead("Cos(t)-1", "-1/2", "2");
    checkLead("E^t-1-t", "1/2", "2");
  }

  /** Log needs the logx dummy, and cancels its constant term when the argument tends to 1. */
  @Test
  public void testLogarithms() {
    EvalEngine engine = engine();
    ISymbol t = t();
    ISymbol logx = F.Dummy("logx");

    // Log(1+t) ~ t : the constant part cancels
    Lead log1p = LeadTerm.leadTerm(evaluator.eval("Log(1+t)"), t, logx, engine);
    assertNotNull(log1p);
    assertEquals("1", log1p.coefficient().toString());
    assertEquals("1", log1p.exponent().toString());

    // Log(2*t) -> Log(2) + Log(t), with Log(t) carried by the dummy
    Lead log2t = LeadTerm.leadTerm(evaluator.eval("Log(2*t)"), t, logx, engine);
    assertNotNull(log2t);
    assertEquals("0", log2t.exponent().toString());
    assertEquals("logx+Log(2)", log2t.coefficient().toString());

    // Log(t^3) -> 3*Log(t)
    Lead log3 = LeadTerm.leadTerm(evaluator.eval("Log(t^3)"), t, logx, engine);
    assertNotNull(log3);
    assertEquals("0", log3.exponent().toString());
    assertEquals("3*logx", log3.coefficient().toString());
  }

  /**
   * <code>Exp</code> of a logarithm must come back as an honest power of {@code t}, not as an
   * opaque exponential - the Gruntz caller reads the exponent to decide the limit.
   */
  @Test
  public void testExpOfLogIsAPower() {
    EvalEngine engine = engine();
    ISymbol t = t();
    ISymbol logx = F.Dummy("logx");
    Lead lead = LeadTerm.leadTerm(evaluator.eval("E^(3*Log(t))"), t, logx, engine);
    assertNotNull(lead);
    assertEquals("1", lead.coefficient().toString());
    assertEquals("3", lead.exponent().toString());
  }

  @Test
  public void testExponentialOfVanishingArgument() {
    checkLead("E^t", "1", "0");
    checkLead("E^(t^2)", "1", "0");
    checkLead("E^t-1", "1", "1");
  }

  @Test
  public void testAbsAndSign() {
    checkLead("Abs(t)", "1", "1");
    checkLead("Abs(-2*t)", "2", "1");
    checkLead("Sign(t)", "1", "0");
  }

  /** A power whose base has a leading term raises both parts. */
  @Test
  public void testPowerOfCompositeBase() {
    checkLead("(2*t)^3", "8", "3");
    checkLead("Sqrt(Sin(t))", "1", "1/2");
    checkLead("(1+t)^5", "1", "0");
  }

  /** An essential singularity is the mrv algorithm's problem, not the leading term's. */
  @Test
  public void testDeclinesOnEssentialSingularity() {
    checkDeclines("E^(1/t)");
    checkDeclines("E^(-1/t)");
  }

  /** An oscillating argument has no leading term at all. */
  @Test
  public void testDeclinesOnOscillation() {
    checkDeclines("Sin(1/t)");
    checkDeclines("Cos(1/t)");
  }

  /** An unknown head could be anything; continuity may not be assumed. */
  @Test
  public void testDeclinesOnUnknownFunction() {
    checkDeclines("f(t)");
    checkDeclines("f(t)+t");
  }

  @Test
  public void testExponentComparator() {
    EvalEngine engine = engine();
    assertEquals(-1, LeadTerm.compareExponents(F.C1, F.C2, engine));
    assertEquals(1, LeadTerm.compareExponents(F.C2, F.C1, engine));
    assertEquals(0, LeadTerm.compareExponents(F.C2, F.C2, engine));
    assertEquals(-1, LeadTerm.compareExponents(F.QQ(1, 3), F.QQ(1, 2), engine));
    // irrational, decided exactly rather than by an epsilon
    assertEquals(-1,
        LeadTerm.compareExponents(evaluator.eval("1-Log(5)/Log(3)"), F.C0, engine));
    // a free parameter with no assumptions cannot be ordered
    assertEquals(LeadTerm.UNDECIDABLE,
        LeadTerm.compareExponents(evaluator.eval("a"), F.C0, engine));
  }

  /** The rebuilt expression substitutes Log(t) back for the dummy. */
  @Test
  public void testAsLeadingTermExpression() {
    EvalEngine engine = engine();
    assertEquals("t", LeadTerm.asLeadingTerm(evaluator.eval("Sin(t)"), t(), engine).toString());
    assertEquals("1/t", LeadTerm.asLeadingTerm(evaluator.eval("Gamma(t)"), t(), engine).toString());
    assertEquals("Log(t)",
        LeadTerm.asLeadingTerm(evaluator.eval("Log(t)"), t(), engine).toString());
  }
}
