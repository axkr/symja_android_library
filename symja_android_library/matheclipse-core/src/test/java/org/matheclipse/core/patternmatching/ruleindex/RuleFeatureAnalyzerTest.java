package org.matheclipse.core.patternmatching.ruleindex;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Unit tests for the collapse analysis in {@link RuleFeatureAnalyzer}.
 *
 * <p>
 * The decisive property is soundness: a symbol reported as required must occur in every expression
 * the left-hand-side can match. {@link #assertNotRequired} pins down the constructions where a head
 * present in the pattern is <b>not</b> required, which are the cases that would silently drop
 * matching rules.
 */
public class RuleFeatureAnalyzerTest extends ExprEvaluatorTestCase {

  private Set<ISymbol> required(String lhs) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr parsed = engine.parse(lhs);
    if (parsed.isAST()) {
      parsed = engine.evalHoldPattern((org.matheclipse.core.interfaces.IAST) parsed);
    }
    return RuleFeatureAnalyzer.requiredSymbols(parsed);
  }

  private void assertRequired(String lhs, ISymbol symbol) {
    assertTrue(required(lhs).contains(symbol),
        lhs + " should require " + symbol + " but got " + required(lhs));
  }

  private void assertNotRequired(String lhs, ISymbol symbol) {
    assertFalse(required(lhs).contains(symbol),
        lhs + " must not require " + symbol + " - the pattern matches expressions without it");
  }

  /** An ordinary head has to be present in every matched expression. */
  @Test
  public void testOrdinaryHeadIsRequired() {
    assertRequired("Log(x_)", S.Log);
    assertRequired("f(Sin(x_))", S.Sin);
    assertRequired("Integrate(Log(x_), x_Symbol)", S.Log);
  }

  /** <code>x^n_.</code> matches the bare <code>x</code>, so Power is not required. */
  @Test
  public void testPowerWithDefaultExponentCollapses() {
    assertNotRequired("x_^n_.", S.Power);
    assertRequired("x_^n_", S.Power);
    assertRequired("Sqrt(x_)", S.Power);
    assertRequired("x_^2", S.Power);
  }

  /** <code>u_. * v</code> matches a single factor, so Times is not required. */
  @Test
  public void testTimesWithDefaultFactorCollapses() {
    assertNotRequired("u_.*Log(x_)", S.Times);
    assertRequired("u_.*Log(x_)", S.Log);
    assertRequired("u_*Log(x_)", S.Times);
  }

  /** <code>a_. + b_.*x</code> matches the bare <code>x</code>: neither Plus nor Times required. */
  @Test
  public void testPlusWithDefaultSummandCollapses() {
    assertNotRequired("a_.+b_.*x_", S.Plus);
    assertNotRequired("a_.+b_.*x_", S.Times);
    assertRequired("a_+b_*x_", S.Plus);
  }

  /** A nested collapse: the whole Rubi base pattern matches <code>x</code>. */
  @Test
  public void testNestedRubiBasePatternCollapses() {
    assertNotRequired("(a_.+b_.*x_^n_.)^p_.", S.Plus);
    assertNotRequired("(a_.+b_.*x_^n_.)^p_.", S.Power);
    assertNotRequired("(a_.+b_.*x_^n_.)^p_.", S.Times);
  }

  /** The predicate of a PatternTest is applied to the match, it is not part of it. */
  @Test
  public void testPatternTestPredicateIsNotRequired() {
    assertNotRequired("Log(x_?IntegerQ)", S.IntegerQ);
    assertRequired("Log(x_?IntegerQ)", S.Log);
  }

  /** The condition of a rule is not part of the matched expression either. */
  @Test
  public void testConditionIsNotRequired() {
    assertNotRequired("Log(x_) /; FreeQ(x, y)", S.FreeQ);
    assertRequired("Log(x_) /; FreeQ(x, y)", S.Log);
  }

  /** Alternatives require only what all branches require. */
  @Test
  public void testAlternativesIntersect() {
    assertNotRequired("f(Sin(x_)|Cos(x_))", S.Sin);
    assertNotRequired("f(Sin(x_)|Cos(x_))", S.Cos);
    assertRequired("f(Sin(x_)|Sin(y_))", S.Sin);
  }

  /** A curried head must be analyzed as well, for example the Derivative rules. */
  @Test
  public void testCurriedHead() {
    assertRequired("Derivative(1)[f_][x_]", S.Derivative);
  }

  /**
   * Cross-check the analysis against the matcher: for a set of expressions which the left-hand-side
   * actually matches, every required symbol has to occur in the expression.
   */
  @Test
  public void testRequiredSymbolsOccurInMatchedExpressions() {
    String[][] cases = { //
        {"x_^n_.", "x", "x^2", "x^(1/2)"}, //
        {"u_.*Log(x_)", "Log(y)", "3*Log(y)", "a*b*Log(y)"}, //
        {"(a_.+b_.*x_^n_.)^p_.", "x", "x^2", "1+x", "(1+x^3)^2", "2*x"}, //
        {"a_.+b_.*x_", "x", "1+x", "2*x", "1+2*x"}};
    EvalEngine engine = evaluator.getEvalEngine();
    for (String[] testCase : cases) {
      Set<ISymbol> requiredSymbols = required(testCase[0]);
      for (int i = 1; i < testCase.length; i++) {
        IExpr subject = engine.evaluate(engine.parse(testCase[i]));
        Set<ISymbol> present = new HashSet<ISymbol>();
        RuleFeatureAnalyzer.symbolsOf(subject, present);
        for (ISymbol symbol : requiredSymbols) {
          assertTrue(present.contains(symbol), testCase[0] + " requires " + symbol
              + " which does not occur in the matching expression " + testCase[i]);
        }
      }
    }
  }

  /** A complex number literal in the pattern is observable on the expression side. */
  @Test
  public void testComplexHeadIsObservable() {
    Set<ISymbol> present = new HashSet<ISymbol>();
    RuleFeatureAnalyzer.symbolsOf(F.CI, present);
    assertTrue(present.contains(S.Complex), "the head of a complex number must be observable");
    assertRequired("Complex(0, a_)*u_", S.Complex);
  }
}
