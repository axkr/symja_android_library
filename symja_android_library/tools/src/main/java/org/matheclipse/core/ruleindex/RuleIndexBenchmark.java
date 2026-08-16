package org.matheclipse.core.ruleindex;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.ruleindex.RuleDispatchStats;
import org.matheclipse.core.patternmatching.ruleindex.RuleFeatureIndex;
import org.matheclipse.core.reflection.system.Integrate;

/**
 * Benchmark for the {@link RuleFeatureIndex} prefilter on the Rubi rule set.
 *
 * <p>
 * Measuring the rule dispatch has three pitfalls, all of which this class handles:
 * <ul>
 * <li>The ~6750 Rubi rules are installed by a background thread, so {@link Integrate#await()} has to
 * be called before the rules are touched.</li>
 * <li>Most textbook integrals never reach the rules at all - the native pre-Rubi stages of
 * {@code Integrate} answer them first. The probes below were picked by measurement; each of them
 * walks thousands of rules.</li>
 * <li>{@code EvalEngine#rubiASTCache} answers a repeated integral from the previous result, so
 * every measured run needs a fresh {@link EvalEngine}.</li>
 * </ul>
 *
 * <p>
 * Run it with the rule threshold sweep to re-tune {@link Config#RULE_INDEX_MIN_RULES}:
 *
 * <pre>
 * mvn -pl tools exec:java -Dexec.mainClass=org.matheclipse.core.ruleindex.RuleIndexBenchmark
 * </pre>
 */
public class RuleIndexBenchmark {

  /** Integrands which reach the Rubi rule scan. */
  private static final String[] PROBES = { //
      "x*Log(1+x)/(1-x)", "x^2*Cosh(x)^3", "x*ArcSin(x)^2", "Sin(x)/Sqrt(1+Cos(x)^2)",
      "Sin(a+b*x)^3*Cos(a+b*x)^2", "1/(1+Tan(x))", "(a+b*x^2)^(5/2)/x^3", "Cos(x)^5/Sin(x)^2",
      "ArcTanh(x)/(1-x^2)", "1/(a+b*Sin(x))", "Log(x)^3/x^2", "E^(a*x)*Sin(b*x)^2",
      "(a+b*x)^m*(c+d*x)^n", "x^2/Sqrt(1+x^2)^3", "Gamma(3,x)/x", "x^2*BesselJ(0,x)",
      "Tan(x)^5*Sec(x)^3", "Sech(x)^4*Tanh(x)^3", "Erf(x)*E^(-x^2)", "Sqrt(Tan(x))"};

  private static IExpr[] runProbes() {
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    IExpr[] results = new IExpr[PROBES.length];
    for (int i = 0; i < PROBES.length; i++) {
      results[i] = engine.evaluate(engine.parse("Integrate(" + PROBES[i] + ", x)"));
    }
    return results;
  }

  public static void main(String[] args) throws Exception {
    F.initSymja();
    Integrate.CONST.await();

    System.out.println("Integrate pattern down-rules: "
        + Integrate.INTEGRATE_RULES_DATA.patternDownRulesSize());
    Config.RULE_INDEX_MIN_RULES = 16;
    RuleFeatureIndex index = Integrate.INTEGRATE_RULES_DATA.diagnosticRuleIndex();
    System.out.println("Integrate rule index: " + (index == null ? "none"
        : index.ruleCount() + " rules, " + index.featureCount() + " indexed symbols"));

    Config.RULE_DISPATCH_STATISTICS = true;
    runProbes(); // warm up the JIT

    IExpr[] reference = null;
    System.out.println();
    System.out.println(String.format("%12s %14s %14s %10s %12s", "minRules", "rulesVisited",
        "matchAttempts", "ms", "sameResult"));
    for (int threshold : new int[] {Integer.MAX_VALUE, 64, 16, 8, 4, 2}) {
      Config.RULE_INDEX_MIN_RULES = threshold;
      RuleDispatchStats.reset();
      long start = System.nanoTime();
      IExpr[] results = runProbes();
      long millis = (System.nanoTime() - start) / 1000000;
      boolean same = true;
      if (reference == null) {
        reference = results;
      } else {
        for (int i = 0; i < results.length; i++) {
          same &= reference[i].toString().equals(results[i].toString());
        }
      }
      System.out.println(String.format("%12s %14d %14d %10d %12s",
          threshold == Integer.MAX_VALUE ? "off" : Integer.toString(threshold),
          RuleDispatchStats.rulesVisited(), RuleDispatchStats.matchAttempts(), millis, same));
    }
    System.exit(0);
  }
}
