package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>SingularValueDecomposition</code> of an exact matrix turns {@link EvalEngine#isTogetherMode()}
 * on for its own benefit and used to leave it on, because its <code>try</code> had no
 * <code>finally</code> putting the caller's value back - the only such site in
 * <code>LinearAlgebra</code>, where nine others save the flag and restore it.
 *
 * <p>
 * The engine outlives one evaluation, so the flag stayed on for whatever the caller evaluated next,
 * and <code>TogetherMode</code> is not cosmetic: it puts a sum of fractions over a common
 * denominator. A calculator sharing one engine across calculations answered <code>1/x + 1/y</code>
 * as <code>(x + y)/(x*y)</code> for the rest of the session once an SVD had been asked for.
 *
 * <p>
 * Both tests below take the symbolic branch of <code>SingularValueDecomposition#evaluate</code>:
 * the matrix is exact rationals and the engine is not in numeric mode, so
 * <code>ToggleFeature.EIGENSYSTEM_SYMBOLIC</code> sends it to
 * <code>SymbolicSingularValueDecomposition</code> rather than to <code>numericSVD</code>. The
 * numeric branch never touched the flag.
 */
public class SingularValueDecompositionTogetherModeTest {

  /** Exact rationals, so the symbolic branch runs. A matrix of doubles would not reach it. */
  private static final String EXACT_MATRIX = "SingularValueDecomposition({{3/2, 2}, {5/2, 3}})";

  /**
   * A fresh engine on this thread, in the state a caller hands over: TogetherMode off.
   *
   * <p>
   * Built per test rather than in a field because {@link EvalEngine} is held per thread.
   */
  private static ExprEvaluator freshEvaluator() throws InterruptedException {
    F.await();
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    assertFalse(engine.isTogetherMode(), "a fresh engine should start with TogetherMode off");
    return new ExprEvaluator(engine, false, (short) 100);
  }

  /** The flag itself: whatever the caller had is what the caller gets back. */
  @Test
  public void testSymbolicSvdRestoresTogetherMode() throws InterruptedException {
    ExprEvaluator evaluator = freshEvaluator();
    EvalEngine engine = evaluator.getEvalEngine();

    IExpr svd = evaluator.eval(EXACT_MATRIX);
    assertTrue(svd.isList(), () -> "expected the decomposition as a list, got " + svd);

    assertFalse(engine.isTogetherMode(),
        "SingularValueDecomposition left TogetherMode on; its try needs a finally putting the"
            + " caller's value back, like the other sites in LinearAlgebra");
  }

  /**
   * What the leak does to later arithmetic. TogetherMode is read by the Java-level helpers
   * {@code IExpr#divide}, {@code #plus} and {@code #times} - not by the evaluation of a parsed
   * <code>Plus(...)</code> - so it is those a caller sees change:
   *
   * <pre>
   * TogetherMode off: 49.divide(25*Sqrt(2)) = 49/(25*Sqrt(2))
   * TogetherMode on : 49.divide(25*Sqrt(2)) = 49/50*Sqrt(2)
   * </pre>
   *
   * Those two are the same number with the denominator rationalised, and they are the two forms a
   * calculator printed for the same integral either side of an SVD:
   * <code>Erf(49/(25*Sqrt(2)))</code> against <code>Erf(49/50*Sqrt(2))</code>.
   *
   * <p>
   * The decomposition is parsed before anything is measured, because
   * {@link ExprEvaluator#parse(String)} and every <code>eval(String)</code> open with
   * <code>EvalEngine.setReset(fEngine)</code>, which runs <code>initInstance()</code> and puts
   * <code>fTogetherMode</code> back to false. A caller that parses afresh before each evaluation
   * cannot see this leak at all; one that holds an <code>IExpr</code> and evaluates it again -
   * which is what a calculator does with an already-parsed input - sees it.
   */
  @Test
  public void testLaterDivisionIsNotRationalisedAfterSymbolicSvd() throws InterruptedException {
    ExprEvaluator evaluator = freshEvaluator();
    EvalEngine engine = evaluator.getEvalEngine();

    // parse first: a parse after this point would reset the flag and hide the leak
    IExpr svdInput = evaluator.parse(EXACT_MATRIX);
    IExpr denominator = engine.evaluate(F.Times(F.ZZ(25), F.Sqrt(F.C2)));

    IExpr before = F.ZZ(49).divide(denominator);
    assertEquals("49/(25*Sqrt(2))", before.toString(),
        "the denominator is left alone while TogetherMode is off");

    engine.evaluate(svdInput);

    IExpr after = F.ZZ(49).divide(denominator);
    assertEquals(before.toString(), after.toString(),
        "asking for a decomposition changed how a later, unrelated division is written");
  }

  /** The other branch, to pin that this test is about the symbolic one. */
  @Test
  public void testNumericSvdLeavesTogetherModeAlone() throws InterruptedException {
    ExprEvaluator evaluator = freshEvaluator();
    EvalEngine engine = evaluator.getEvalEngine();

    IExpr svd = evaluator.eval("SingularValueDecomposition({{1.5, 2.0}, {2.5, 3.0}})");
    assertTrue(svd.isList(), () -> "expected the decomposition as a list, got " + svd);

    assertFalse(engine.isTogetherMode(), "the numeric branch never sets TogetherMode");
  }
}
