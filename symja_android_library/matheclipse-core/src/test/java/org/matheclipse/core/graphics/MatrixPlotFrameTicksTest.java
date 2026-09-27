package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;

/**
 * The frame ticks of {@code MatrixPlot}: one per cell index, at the centre of its cell, with row 1
 * at the top.
 *
 * <pre>
 * Cases[MatrixPlot[m], (FrameTicks -&gt; t_) :&gt; t, Infinity]
 * </pre>
 */
public class MatrixPlotFrameTicksTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
  }

  private static String frameTicks(String plot) {
    return evaluator.eval("Cases(" + plot + ", (FrameTicks->t_):>t, Infinity)").toString()
        .replace("\n", "");
  }

  @Test
  public void testTwoByTwo() {
    String rows = "{{1.5,1},{0.5,2}}";
    String cols = "{{0.5,1},{1.5,2}}";
    assertEquals("{{{" + rows + "," + rows + "},{" + cols + "," + cols + "}}}",
        frameTicks("MatrixPlot(PauliMatrix(3))"));
  }

  @Test
  public void testTenByTenLabelsEveryIndex() {
    String rows =
        "{{9.5,1},{8.5,2},{7.5,3},{6.5,4},{5.5,5},{4.5,6},{3.5,7},{2.5,8},{1.5,9},{0.5,10}}";
    String cols =
        "{{0.5,1},{1.5,2},{2.5,3},{3.5,4},{4.5,5},{5.5,6},{6.5,7},{7.5,8},{8.5,9},{9.5,10}}";
    assertEquals("{{{" + rows + "," + rows + "},{" + cols + "," + cols + "}}}",
        frameTicks("MatrixPlot(Table(i+j,{i,10},{j,10}))"));
  }

  /** A large matrix is labelled at 1 and at round multiples. */
  @Test
  public void testLargeMatrixUsesRoundSteps() {
    assertEquals("{{0.5,1},{4.5,5},{9.5,10},{14.5,15},{19.5,20},{24.5,25}}",
        evaluator.eval("First(Last(First(" + "Cases(MatrixPlot(Table(i+j,{i,3},{j,26})),"
            + "(FrameTicks->t_):>t, Infinity))))").toString());
  }

  /** Ticks the caller wrote are kept. */
  @Test
  public void testExplicitFrameTicksAreKept() {
    assertEquals("{None}", frameTicks("MatrixPlot(PauliMatrix(3), FrameTicks->None)"));
  }

  /** ArrayPlot has no ticks unless asked; asked, they count cells as MatrixPlot's do. */
  @Test
  public void testArrayPlotTicksWhenAsked() {
    assertEquals("{None}", frameTicks("ArrayPlot(PauliMatrix(3))"));
    String rows = "{{1.5,1},{0.5,2}}";
    String cols = "{{0.5,1},{1.5,2}}";
    assertEquals("{{{" + rows + "," + rows + "},{" + cols + "," + cols + "}}}",
        frameTicks("ArrayPlot(PauliMatrix(3), FrameTicks->Automatic)"));
  }
}
