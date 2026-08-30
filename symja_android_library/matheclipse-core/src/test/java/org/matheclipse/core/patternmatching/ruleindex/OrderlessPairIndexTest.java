package org.matheclipse.core.patternmatching.ruleindex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Measures and validates the {@link OrderlessPairIndex} on the <code>Plus</code> and
 * <code>Times</code> rules of {@link org.matheclipse.core.builtin.Arithmetic} and
 * {@link org.matheclipse.core.eval.SimplifyUtil}.
 *
 * <p>
 * Two probe sets are needed: expressions which actually rewrite a pair, so that the dual run check
 * has something to verify, and polynomial expressions which never rewrite anything but walk
 * thousands of argument pairs - that is the work the index removes.
 */
public class OrderlessPairIndexTest {

  /** Probes which rewrite <code>Plus</code> or <code>Times</code> argument pairs. */
  private static final String[] REWRITING = { //
      "Simplify(Sin(x)^2+Cos(x)^2)", //
      "Simplify(Sec(x)^2-Tan(x)^2)", //
      "Simplify(Csc(x)^2-Cot(x)^2)", //
      "Simplify(Tan(x)*Cot(x)+Sin(x)*Csc(x))", //
      "FullSimplify(Cosh(x)^2-Sinh(x)^2)", //
      "Simplify((1-Cos(x)^2)/Sin(x)^2)", //
      "Simplify(Sech(x)^2+Tanh(x)^2)", //
      "TrigReduce(Sin(x)^3*Cos(x)^2)", //
      "TrigExpand(Sin(3*x)*Cos(2*x))", //
      "Sin(x)*Cot(x)", //
      "Cos(x)*Tan(x)", //
      "Erf(x)+Erfc(x)", //
      "Log(1000)/Log(10)", //
      "Gamma(1/4)*Gamma(3/4)", //
      "Simplify(ArcTan(1/2)+ArcTan(1/3))", //
      "Simplify(ArcTan(1/3)+ArcTan(1/7))"};

  /** Probes which never rewrite a pair but walk many of them. */
  private static final String[] POLYNOMIAL = { //
      "Expand((1+x+y)^10)", //
      "Expand((a+b+c+d)^7)", //
      "Together(1/(x+1)+1/(x+2)+1/(x^2+3*x+2))", //
      "Factor(x^10-1)", //
      "D(Expand((1+x+y)^8), x)", //
      "Sum(i^3*x^i, {i, 1, 40})", //
      "Table(Expand((1+x)^i), {i, 1, 30})", //
      "Det(Table(1/(i+j+x), {i,1,5},{j,1,5}))"};

  private static EvalEngine newEngine() {
    F.initSymja();
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    return engine;
  }

  private static IExpr[] evaluate(EvalEngine engine, String[] probes) {
    IExpr[] results = new IExpr[probes.length];
    for (int i = 0; i < probes.length; i++) {
      results[i] = engine.evaluate(engine.parse(probes[i]));
    }
    return results;
  }

  /**
   * Dual run check: evaluate with a full pair scan and verify for every rewritten pair that the
   * index would have kept it. A miss is the one failure mode which changes results.
   */
  @Test
  public void testIndexNeverDropsAPairWhichFires() {
    final boolean validate = Config.ORDERLESS_PAIR_INDEX_VALIDATE;
    try {
      Config.ORDERLESS_PAIR_INDEX_VALIDATE = true;
      OrderlessIndexValidation.reset();
      EvalEngine engine = newEngine();
      evaluate(engine, REWRITING);
      evaluate(engine, POLYNOMIAL);

      System.out.println("[OrderlessPairIndex] validated " + OrderlessIndexValidation.checked()
          + " rewritten pairs, " + OrderlessIndexValidation.missed() + " missed");
      assertEquals(0, OrderlessIndexValidation.missed(),
          "the index dropped a pair which was rewritten:\n" + OrderlessIndexValidation.firstMiss());
      assertTrue(OrderlessIndexValidation.checked() > 0,
          "no pair was rewritten at all - the probe set no longer exercises the matcher");
    } finally {
      Config.ORDERLESS_PAIR_INDEX_VALIDATE = validate;
    }
  }

  /**
   * Compare a full pair scan with the indexed dispatch: the results and the number of rewrites have
   * to be identical, and the index has to remove nearly all of the pair lookups.
   */
  @Test
  public void testIndexCutsPairLookupsAndKeepsResults() {
    final boolean index = Config.ORDERLESS_PAIR_INDEX;
    final boolean statistics = Config.ORDERLESS_HASH_STATISTICS;
    try {
      Config.ORDERLESS_HASH_STATISTICS = true;
      // warm up and build the indexes, so that neither measured run pays for it
      Config.ORDERLESS_PAIR_INDEX = true;
      evaluate(newEngine(), REWRITING);

      Config.ORDERLESS_PAIR_INDEX = false;
      EvalEngine engine = newEngine();
      OrderlessHashStats.reset();
      IExpr[] scanRewriting = evaluate(engine, REWRITING);
      IExpr[] scanPolynomial = evaluate(engine, POLYNOMIAL);
      long scanPairs = OrderlessHashStats.pairsProbed();
      long scanHits = OrderlessHashStats.probeHits();
      long scanFires = OrderlessHashStats.fires();

      Config.ORDERLESS_PAIR_INDEX = true;
      engine = newEngine();
      OrderlessHashStats.reset();
      IExpr[] indexedRewriting = evaluate(engine, REWRITING);
      IExpr[] indexedPolynomial = evaluate(engine, POLYNOMIAL);
      long indexPairs = OrderlessHashStats.pairsProbed();
      long indexHits = OrderlessHashStats.probeHits();
      long indexFires = OrderlessHashStats.fires();

      System.out.println("[OrderlessPairIndex] pairs probed: scan=" + scanPairs + " indexed="
          + indexPairs + " factor="
          + (indexPairs == 0 ? "inf" : String.format("%.1f", scanPairs / (double) indexPairs)));
      System.out.println("[OrderlessPairIndex] probe hits: scan=" + scanHits + " indexed="
          + indexHits + ", rewrites: scan=" + scanFires + " indexed=" + indexFires);

      for (int i = 0; i < REWRITING.length; i++) {
        assertEquals(scanRewriting[i].toString(), indexedRewriting[i].toString(),
            "the index changed the result of " + REWRITING[i]);
      }
      for (int i = 0; i < POLYNOMIAL.length; i++) {
        assertEquals(scanPolynomial[i].toString(), indexedPolynomial[i].toString(),
            "the index changed the result of " + POLYNOMIAL[i]);
      }
      assertEquals(scanFires, indexFires, "the index changed the number of rewrites");
      assertTrue(scanFires > 0, "no pair was rewritten at all");
      // Measured on this probe set: the index removes more than 99% of the lookups, and every
      // lookup it leaves is a hit. The bound is kept well below that so that normal fluctuation
      // does not fail the build, but a regression of the filter does.
      assertTrue(indexPairs * 10 <= scanPairs, "the index should remove at least 90% of the pair"
          + " lookups, but left " + indexPairs + " of " + scanPairs);
      assertEquals(scanHits, indexHits, "the index dropped a pair which the rule map answered");
    } finally {
      Config.ORDERLESS_PAIR_INDEX = index;
      Config.ORDERLESS_HASH_STATISTICS = statistics;
    }
  }
}
