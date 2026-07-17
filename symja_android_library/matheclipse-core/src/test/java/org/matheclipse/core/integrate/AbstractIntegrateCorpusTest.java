package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Base class for corpus-driven {@code Integrate} tests. Each corpus is a resource file of
 * {@code integrand ; var} lines; every line is validated with the form-independent
 * differentiate-back predicate {@code Simplify[Together[D[Integrate[f, x], x] - f]] == 0}, the same
 * oracle used by {@code IntegrateAlgorithmsTest}.
 *
 * <p>
 * Every case is classified three ways:
 * <ul>
 * <li>{@link Outcome#PASS} — the integral resolved and differentiates back to the integrand.
 * <li>{@link Outcome#UNRESOLVED} — the integral stayed unevaluated (a coverage gap; tolerated and
 * reported, not a failure).
 * <li>{@link Outcome#FAIL} — the integral resolved but differentiates to something else (a real
 * regression: a wrong antiderivative).
 * </ul>
 * 
 */
public abstract class AbstractIntegrateCorpusTest extends ExprEvaluatorTestCase {

  public enum Outcome {
    PASS, UNRESOLVED, FAIL
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    try {
      // Wait for the Integrate() rule initializer, exactly like IntegrateAlgorithmsTest.
      S.Integrate.getEvaluator().await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Classify one integrand via the differentiate-back predicate.
   *
   * @param integrand the integrand, in relaxed (parenthesis) syntax
   * @param var the integration variable, e.g. {@code "x"}
   * @param forcedMethod a {@code Method ->} value to pin a single native stage, or {@code null} for
   *        the Automatic cascade
   */
  protected Outcome classify(String integrand, String var, String forcedMethod) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr xVar = engine.parse(var);
    IExpr fEval = evaluator.eval("(" + integrand + ")");
    IExpr integral = evaluator.eval(forcedMethod == null //
        ? "Integrate(" + integrand + ", " + var + ")"
        : "Integrate(" + integrand + ", " + var + ", Method -> \"" + forcedMethod + "\")");
    if (!integral.isFree(S.Integrate)) {
      return Outcome.UNRESOLVED;
    }
    // Differentiate the concrete antiderivative (never the unevaluated Integrate call, whose
    // derivative would collapse back to the integrand and mask coverage gaps).
    IExpr diffExpr = F.Subtract(F.D(integral, xVar), fEval);
    if (engine.evaluate(F.Simplify(F.Together(diffExpr))).isZero()) {
      return Outcome.PASS;
    }
    // Symbolic Simplify cannot always reduce correct half-angle / special-function antiderivatives;
    // confirm numerically at several generic interior points before declaring a regression.
    return numericallyZero(diffExpr, xVar, engine) ? Outcome.PASS : Outcome.FAIL;
  }

  /** True iff {@code expr} numerically vanishes at every sample point (all chosen in (0, Pi)). */
  private boolean numericallyZero(IExpr expr, IExpr xVar, EvalEngine engine) {
    final double[] points = {0.35, 0.9, 1.4, 2.3};
    for (double pt : points) {
      IExpr check = engine.evaluate(
          F.Less(F.Abs(F.ReplaceAll(expr, F.Rule(xVar, F.num(pt)))), F.num(1.0e-9)));
      if (!check.isTrue()) {
        return false;
      }
    }
    return true;
  }

  /** Aggregate outcome of one corpus run, for summary reporting. */
  protected static final class CorpusResult {
    public int pass;
    public int unresolved;
    public int fail;
    public final List<String> failures = new ArrayList<>();
    public final List<String> unresolvedCases = new ArrayList<>();
  }

  /**
   * Run every {@code integrand[ ; var]} line of a classpath resource through {@link #classify} and
   * assert there are no {@link Outcome#FAIL} (regression) cases. {@link Outcome#UNRESOLVED} cases are
   * tolerated and reported to stdout so coverage gaps stay visible.
   *
   * @param resourcePath classpath resource path, e.g. {@code "/integrate/rational_seed.txt"}
   * @param forcedMethod a {@code Method ->} value, or {@code null} for the Automatic cascade
   */
  protected CorpusResult runCorpusResource(String resourcePath, String forcedMethod) {
    CorpusResult r = new CorpusResult();
    for (String[] row : readCorpus(resourcePath)) {
      Outcome outcome = classify(row[0], row[1], forcedMethod);
      switch (outcome) {
        case PASS:
          r.pass++;
          break;
        case UNRESOLVED:
          r.unresolved++;
          r.unresolvedCases.add(row[0]);
          break;
        case FAIL:
          r.fail++;
          r.failures.add(row[0]);
          break;
      }
    }
    System.out.println(resourcePath + (forcedMethod == null ? " [Automatic]" : " [" + forcedMethod
        + "]") + " -> pass=" + r.pass + " unresolved=" + r.unresolved + " fail=" + r.fail);
    if (!r.unresolvedCases.isEmpty()) {
      System.out.println("  unresolved (coverage gaps): " + r.unresolvedCases);
    }
    assertEquals(0, r.fail,
        "regressions (resolved but wrong antiderivative) for " + resourcePath + ": " + r.failures);
    return r;
  }

  private List<String[]> readCorpus(String resourcePath) {
    List<String[]> rows = new ArrayList<>();
    try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IOException("corpus resource not found on test classpath: " + resourcePath);
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      String line;
      while ((line = br.readLine()) != null) {
        String s = line.trim();
        if (s.isEmpty() || s.startsWith("#")) {
          continue;
        }
        int semi = s.indexOf(';');
        String integrand = semi >= 0 ? s.substring(0, semi).trim() : s;
        String var = semi >= 0 ? s.substring(semi + 1).trim() : "x";
        rows.add(new String[] {integrand, var});
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return rows;
  }
}
