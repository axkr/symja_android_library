package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** The names a {@code ColorData} scheme answers to. */
public class ColorSchemeNameTest {

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

  /**
   * Several schemes are named both with and without a trailing <code>Colors</code>, and the
   * reference writes some of them the long way.
   */
  @Test
  public void testATrailingColorsIsOptional() {
    assertEquals(evaluator.eval("ColorData(\"Avocado\")").toString(),
        evaluator.eval("ColorData(\"AvocadoColors\")").toString());
    assertEquals(evaluator.eval("ColorData(\"Rose\")").toString(),
        evaluator.eval("ColorData(\"RoseColors\")").toString());
  }

  /** A name no scheme goes by is still no scheme, whatever it ends in. */
  @Test
  public void testAnUnknownNameStaysUnknown() {
    assertEquals("ColorData(NotAScheme)", evaluator.eval("ColorData(\"NotAScheme\")").toString());
    assertEquals("ColorData(Colors)", evaluator.eval("ColorData(\"Colors\")").toString());
  }

  /**
   * {@code BrightBands} runs red to orange through bands of blue, green and yellow, each followed
   * by a paler band of the same hue. The table is the eleven colours the reference gives for
   * {@code ColorData["BrightBands"] /@ Range[0, 1, 0.1]}, so those points come back exactly.
   */
  @Test
  public void testBrightBands() {
    IExpr scheme = evaluator.eval("ColorData(\"BrightBands\")");
    assertEquals("ColorDataFunction(BRIGHT_BANDS,Gradients,{0,1})", scheme.toString());
    assertEquals("RGBColor(0.90222,0.101808,0.198306,1.0)", sample(scheme, 0.0));
    assertEquals("RGBColor(0.234991,0.967604,0.115633,1.0)", sample(scheme, 0.5));
    assertEquals("RGBColor(1.0,0.749752,0.501183,1.0)", sample(scheme, 1.0));
  }

  private static String sample(IExpr scheme, double t) {
    return EvalEngine.get().evaluate(F.unaryAST1(scheme, F.num(t))).toString();
  }
}
