package org.matheclipse.core.patternmatching.ruleindex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Validates the precompiled right-hand-side substitution
 * ({@link org.matheclipse.core.patternmatching.SubstitutionPlan}).
 *
 * <p>
 * Two properties are checked. The dual run mode
 * ({@link Config#SUBSTITUTION_PLAN_VALIDATE}) computes every substitution both ways and compares
 * the results including their flattened/sorted evaluation flags, so a plan which rebuilds the wrong
 * node is a test failure rather than a wrong result. And the same probes are evaluated with the
 * plan switched off, so that a plan which is <em>consistently</em> wrong in both the check and the
 * result would still be caught by the comparison of the final answers.
 */
public class SubstitutionPlanTest {

  /**
   * Probes chosen to exercise rule right-hand-sides, not arithmetic: rule based integration, user
   * defined functions with several patterns, optional and default arguments, conditions,
   * <code>With</code> and <code>Module</code> bodies, and Orderless/Flat heads.
   */
  private static final String[] PROBES = { //
      "Integrate(x*Log(1+x)/(1-x), x)", //
      "Integrate(x^2*Cosh(x)^3, x)", //
      "Integrate(x*ArcSin(x)^2, x)", //
      "Integrate(1/(1+Tan(x)), x)", //
      "Integrate(Cos(x)^5/Sin(x)^2, x)", //
      "Integrate(Log(x)^3/x^2, x)", //
      "Integrate(Sqrt(1+Sin(x)), x)", //
      "D(ArcTan(x^2)*Log(1+x^2), x)", //
      "FunctionExpand(Gamma(1/2+n))", //
      "Simplify(Sin(x)^2+Cos(x)^2)", //
      "Limit(Sin(x)/x, x->0)", //
      "Series(E^x, {x, 0, 6})", //
      "Sum(k^3, {k, 1, n})", //
      // user defined rules: several patterns, defaults, conditions, scoping constructs
      "Module({f}, f[x_, y_] := x^2 + y^2; f[a, b])", //
      "Module({g}, g[x_, y_:7] := {x, y}; {g[a], g[a, b]})", //
      "Module({h}, h[x_ /; x > 0] := Sqrt[x]; h[9])", //
      "Module({p}, p[x_] := With({t = x^2}, t + 1); p[a])", //
      "Module({q}, q[x__] := Length[{x}]; q[a, b, c])", //
      "Module({r}, r[x_ + y_] := {x, y}; r[a + b])", //
      "Module({s}, s[x_?NumberQ] := x + 1; {s[2], s[a]})", //
      "Module({t}, t[x_] := Module[{u = x}, u*2]; t[a])"};

  /**
   * A <code>Module</code> renames its local symbols with a counter which keeps running across
   * evaluations, so a result which still contains such a symbol prints differently on every run.
   * That is unrelated to the substitution, so the counter is removed before comparing.
   */
  private static String normalize(IExpr expr) {
    return expr.toString().replaceAll("\\$\\d+", "\\$");
  }

  private static IExpr[] evaluate(String[] probes) {
    F.initSymja();
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    IExpr[] results = new IExpr[probes.length];
    for (int i = 0; i < probes.length; i++) {
      results[i] = engine.evaluate(engine.parse(probes[i]));
    }
    return results;
  }

  /**
   * Run every substitution both ways and require that they agree. The generic result is the one
   * which is returned while the check is on, so this test cannot be fooled by a plan which is
   * wrong in a way that changes the answer.
   */
  @Test
  public void testPlanAgreesWithGenericSubstitution() {
    boolean plan = Config.SUBSTITUTION_PLAN;
    boolean validate = Config.SUBSTITUTION_PLAN_VALIDATE;
    try {
      SubstitutionPlanStats.reset();
      Config.SUBSTITUTION_PLAN = true;
      Config.SUBSTITUTION_PLAN_VALIDATE = true;
      evaluate(PROBES);

      assertTrue(SubstitutionPlanStats.checked() > 1000,
          "expected the probes to substitute many right-hand-sides, but only "
              + SubstitutionPlanStats.checked() + " were checked");
      assertEquals(0, SubstitutionPlanStats.mismatched(),
          "planned substitution differs from the generic one:\n"
              + SubstitutionPlanStats.firstMismatch());
    } finally {
      Config.SUBSTITUTION_PLAN_VALIDATE = validate;
      Config.SUBSTITUTION_PLAN = plan;
    }
  }

  /** The results with the plan and without it have to be identical. */
  @Test
  public void testSameResultsWithAndWithoutPlan() {
    boolean plan = Config.SUBSTITUTION_PLAN;
    try {
      Config.SUBSTITUTION_PLAN = false;
      IExpr[] generic = evaluate(PROBES);
      Config.SUBSTITUTION_PLAN = true;
      IExpr[] planned = evaluate(PROBES);
      for (int i = 0; i < PROBES.length; i++) {
        assertEquals(normalize(generic[i]), normalize(planned[i]), PROBES[i]);
      }
    } finally {
      Config.SUBSTITUTION_PLAN = plan;
    }
  }

  /** Every right-hand-side the builder declines must still be substituted by the generic path. */
  @Test
  public void testRefusedRightHandSidesStillEvaluate() {
    boolean plan = Config.SUBSTITUTION_PLAN;
    try {
      Config.SUBSTITUTION_PLAN = true;
      SubstitutionPlanStats.reset();
      F.initSymja();
      EvalEngine engine = new EvalEngine(true);
      EvalEngine.set(engine);
      // OptionValue() in the right-hand-side is one of the declined shapes
      IExpr result = engine.evaluate(engine.parse(
          "Module({f}, Options(f) = {n -> 3}; f[x_, OptionsPattern()] := x^OptionValue(n); f[a])"));
      assertEquals("a^3", result.toString());
    } finally {
      Config.SUBSTITUTION_PLAN = plan;
    }
  }
}
