package org.matheclipse.core.patternmatching.ruleindex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Integrate;

/**
 * Measures and validates the {@link RuleFeatureIndex} on the Rubi rule set.
 *
 * <p>
 * The probes are integrands which actually reach the Rubi rules; the native pre-Rubi stages of
 * {@code Integrate} answer most textbook integrals long before the rule scan.
 */
public class RuleFeatureIndexTest {

  /**
   * Integrands which reach the Rubi rule scan. The native pre-Rubi stages of {@code Integrate}
   * answer most textbook integrals without ever looking at a rule, so a probe set has to be picked
   * by measurement - these all walk thousands of rules per integral.
   *
   * <p>
   * Every probe is evaluated exactly once per measured run and each run gets a fresh
   * {@link EvalEngine}, because {@code EvalEngine#rubiASTCache} would otherwise answer the second
   * run from the first run's results.
   */
  private static final String[] PROBES = { //
      "x*Log(1+x)/(1-x)", //
      "x^2*Cosh(x)^3", //
      "x*ArcSin(x)^2", //
      "Sin(x)/Sqrt(1+Cos(x)^2)", //
      "Sin(a+b*x)^3*Cos(a+b*x)^2", //
      "1/(1+Tan(x))", //
      "(a+b*x^2)^(5/2)/x^3", //
      "Cos(x)^5/Sin(x)^2", //
      "ArcTanh(x)/(1-x^2)", //
      "1/(a+b*Sin(x))", //
      "Log(x)^3/x^2", //
      "E^(a*x)*Sin(b*x)^2", //
      "(a+b*x)^m*(c+d*x)^n", //
      "x^2/Sqrt(1+x^2)^3", //
      "Gamma(3,x)/x", //
      "x^2*BesselJ(0,x)", //
      "Tan(x)^5*Sec(x)^3", //
      "Sech(x)^4*Tanh(x)^3", //
      "Erf(x)*E^(-x^2)", //
      "Sqrt(Tan(x))"};

  private static EvalEngine newEngine() {
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    return engine;
  }

  private static void awaitRubi() {
    try {
      F.initSymja();
      Integrate.CONST.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  /** Evaluate all probes and return their results. */
  private static IExpr[] evaluateProbes(EvalEngine engine) {
    IExpr[] results = new IExpr[PROBES.length];
    for (int i = 0; i < PROBES.length; i++) {
      results[i] = engine.evaluate(engine.parse("Integrate(" + PROBES[i] + ", x)"));
    }
    return results;
  }

  /**
   * Compare the number of rules the dispatch walks with and without the index and verify that the
   * results are unchanged. The index has to cut the scan by at least the factor the plan set as its
   * gate.
   */
  @Test
  public void testIndexReducesRuleScanAndKeepsResults() {
    awaitRubi();
    final int minRules = Config.RULE_INDEX_MIN_RULES;
    final boolean statistics = Config.RULE_DISPATCH_STATISTICS;
    try {
      Config.RULE_DISPATCH_STATISTICS = true;

      // warm up the JIT and build the index; a separate engine, so that neither measured run below
      // sees a populated rubiASTCache
      Config.RULE_INDEX_MIN_RULES = 16;
      evaluateProbes(newEngine());

      Config.RULE_INDEX_MIN_RULES = Integer.MAX_VALUE;
      EvalEngine engine = newEngine();
      RuleDispatchStats.reset();
      long startScan = System.nanoTime();
      IExpr[] linear = evaluateProbes(engine);
      long scanNanos = System.nanoTime() - startScan;
      long scanVisited = RuleDispatchStats.indexableRulesVisited();
      long scanTotal = RuleDispatchStats.rulesVisited();
      long scanAttempts = RuleDispatchStats.matchAttempts();

      Config.RULE_INDEX_MIN_RULES = 16;
      engine = newEngine();
      RuleDispatchStats.reset();
      long startIndex = System.nanoTime();
      IExpr[] indexed = evaluateProbes(engine);
      long indexNanos = System.nanoTime() - startIndex;
      long indexVisited = RuleDispatchStats.indexableRulesVisited();
      long indexTotal = RuleDispatchStats.rulesVisited();
      long indexAttempts = RuleDispatchStats.matchAttempts();

      System.out.println("[RuleFeatureIndex] indexed symbols, rules visited: linear=" + scanVisited
          + " indexed=" + indexVisited + " factor="
          + (indexVisited == 0 ? "inf" : String.format("%.1f", scanVisited / (double) indexVisited)));
      System.out.println("[RuleFeatureIndex] all symbols, rules visited: linear=" + scanTotal
          + " indexed=" + indexTotal + " factor="
          + (indexTotal == 0 ? "inf" : String.format("%.1f", scanTotal / (double) indexTotal)));
      System.out.println("[RuleFeatureIndex] match attempts: linear=" + scanAttempts + " indexed="
          + indexAttempts);
      System.out.println("[RuleFeatureIndex] wall clock: linear=" + (scanNanos / 1000000) + "ms"
          + " indexed=" + (indexNanos / 1000000) + "ms");
      System.out.println("[RuleFeatureIndex] " + RuleDispatchStats.summary());

      for (int i = 0; i < PROBES.length; i++) {
        assertEquals(linear[i].toString(), indexed[i].toString(),
            "the index changed the result of Integrate(" + PROBES[i] + ", x)");
      }
      // Measured on this probe set: the index visits about a third of the rules and the probes
      // run in about half the time. The bound is kept below the measured factor so that normal
      // fluctuation does not fail the build, but a regression of the analysis does.
      assertTrue(indexVisited * 5 <= scanVisited * 2,
          "the index should cut the rule scan of the indexed symbols by at least a factor of 2.5,"
              + " but visited " + indexVisited + " of " + scanVisited + " rules");
    } finally {
      Config.RULE_INDEX_MIN_RULES = minRules;
      Config.RULE_DISPATCH_STATISTICS = statistics;
    }
  }

  /**
   * Dual run check: evaluate with a full linear scan and verify for every rule which fires that the
   * index reports it as a candidate. A miss means {@link RuleFeatureAnalyzer} claimed a symbol is
   * required which the matcher does not actually require.
   */
  @Test
  public void testIndexNeverDropsAMatchingRule() {
    awaitRubi();
    final boolean validate = Config.RULE_INDEX_VALIDATE;
    final int minRules = Config.RULE_INDEX_MIN_RULES;
    try {
      Config.RULE_INDEX_MIN_RULES = 16;
      Config.RULE_INDEX_VALIDATE = true;
      RuleIndexValidation.reset();
      EvalEngine engine = newEngine();

      for (String probe : PROBES) {
        engine.evaluate(engine.parse("Integrate(" + probe + ", x)"));
      }
      // exercise the other rule sets which are dispatched through RulesData as well
      for (String expression : new String[] {"D(Sin(x)*Log(x), x)", "D(x^n, x)",
          "FunctionExpand(Binomial(n, 3))", "FunctionExpand(Gamma(n+1))", "Sum(i, {i, 1, n})",
          "Sum(i^2, {i, 1, 10})", "Product(i, {i, 1, n})", "SeriesCoefficient(Sin(x), {x, 0, 3})",
          "Limit(Sin(x)/x, x->0)", "Simplify(Sin(x)^2+Cos(x)^2)"}) {
        engine.evaluate(engine.parse(expression));
      }

      System.out.println("[RuleFeatureIndex] validated " + RuleIndexValidation.checked()
          + " firing rules, " + RuleIndexValidation.missed() + " missed");
      assertEquals(0, RuleIndexValidation.missed(),
          "the index dropped a rule which fired:\n" + RuleIndexValidation.firstMiss());
      assertTrue(RuleIndexValidation.checked() > 0, "no rule dispatch was validated at all");
    } finally {
      Config.RULE_INDEX_VALIDATE = validate;
      Config.RULE_INDEX_MIN_RULES = minRules;
    }
  }
}
