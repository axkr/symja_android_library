package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@link GraphicsOptions#plotStyleColor} maps a zero-based curve index onto a user's
 * {@code PlotStyle}.
 *
 * <p>
 * It used to index the {@code PlotStyle} list with that count directly, but {@link IAST#get} is
 * one-based: {@code get(0)} is the list's head, not its first element. So curve zero always fell
 * back to the default palette instead of its requested style, every later curve was drawn in the
 * style meant for the curve before it, and a style list exactly as long as the curve count left its
 * last entry unused. This is reachable from {@code Manipulate} (which reads {@code PlotStyle} for
 * its JSXGraph curves) and from SVG legend swatches, which fall back to it whenever a legend source
 * has no explicit colour of its own.
 */
public class PlotStyleColorTest {

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

  private static void assertColor(int index, IExpr plotStyle, int r, int g, int b) {
    RGBColor color = GraphicsOptions.plotStyleColor(index, plotStyle);
    assertEquals(r, color.getRed(), "red at index " + index);
    assertEquals(g, color.getGreen(), "green at index " + index);
    assertEquals(b, color.getBlue(), "blue at index " + index);
  }

  @Test
  public void eachCurveGetsItsOwnRequestedColor() {
    IExpr styles = evaluator.eval("{Red,Green,Blue}");
    assertColor(0, styles, 255, 0, 0);
    assertColor(1, styles, 0, 255, 0);
    assertColor(2, styles, 0, 0, 255);
  }

  /** More curves than styles cycles back to the start. */
  @Test
  public void aStyleListCyclesWhenThereAreMoreCurvesThanStyles() {
    IExpr styles = evaluator.eval("{Red,Green}");
    assertColor(0, styles, 255, 0, 0);
    assertColor(1, styles, 0, 255, 0);
    assertColor(2, styles, 255, 0, 0);
    assertColor(3, styles, 0, 255, 0);
  }

  /** {@code PlotStyle -> Red}, not wrapped in a list, colors every curve the same way. */
  @Test
  public void aBareNonListStyleAppliesToEveryCurve() {
    IExpr red = evaluator.eval("Red");
    assertColor(0, red, 255, 0, 0);
    assertColor(4, red, 255, 0, 0);
  }

  /** A {@code Directive} still yields the color inside it. */
  @Test
  public void aDirectiveStillYieldsItsColor() {
    IExpr styles = evaluator.eval("{Directive[Red,Thick],Directive[Green,Dashed]}");
    assertColor(0, styles, 255, 0, 0);
    assertColor(1, styles, 0, 255, 0);
  }

  /**
   * Nothing here should ever throw, including on the sentinel {@code F.NIL} that most callers pass
   * when the caller has no {@code PlotStyle} of its own to offer.
   */
  @Test
  public void absentOrEmptyStylesFallBackWithoutThrowing() {
    assertDoesNotThrow(() -> GraphicsOptions.plotStyleColor(0, F.NIL));
    assertDoesNotThrow(() -> GraphicsOptions.plotStyleColor(0, null));
    assertDoesNotThrow(() -> GraphicsOptions.plotStyleColor(0, evaluator.eval("{}")));
    assertDoesNotThrow(() -> GraphicsOptions.plotStyleColor(-1, evaluator.eval("{Red,Green}")));
  }

  /**
   * {@code Manipulate} describes a widget, so it holds its arguments and stays unevaluated for a
   * front end to render - see {@code org.matheclipse.core.manipulate.ManipulateSpec}. It used to
   * transpile the body to JavaScript and answer with {@code JSFormData}, which only worked for the
   * handful of body shapes that transpiler covered.
   */
  @Test
  public void manipulateHoldsItsArgumentsAndStaysUnevaluated() {
    IExpr result = evaluator
        .eval("Manipulate[Plot[{Sin[a x],Cos[a x]},{x,0,6},PlotStyle->{Red,Green}],{a,1,3}]");
    assertEquals(S.Manipulate, result.isAST() ? ((IAST) result).head() : F.NIL,
        "Manipulate is a front end object and evaluates to itself");
    // the body is held: the control variable is still free inside it
    assertEquals(S.Plot, ((IAST) result).arg1().isAST() ? ((IAST) ((IAST) result).arg1()).head()
        : F.NIL, "the body of a Manipulate must not be evaluated");
  }

  /**
   * The first legend swatch takes the color the plot actually asked for.
   *
   * <p>
   * The legend line for curve zero has to carry {@code stroke="rgb(255,0,0)"} - not the second
   * color, {@code rgb(0,255,0)}, which is what curve zero drew with before this fix.
   */
  @Test
  public void theFirstLegendSwatchIsNoLongerForcedToTheDefaultPalette() {
    String svg = evaluator.eval(
        "ExportString[Plot[{Sin[x],Cos[x]},{x,0,6},PlotStyle->{Red,Green},PlotLegends->{\"s\",\"c\"}],\"SVG\"]")
        .toString();
    int sLine = svg.indexOf(">s<");
    int swatchBeforeIt = svg.lastIndexOf("stroke=\"rgb(", sLine);
    assertEquals(true, sLine > 0 && swatchBeforeIt > 0,
        "could not locate the \"s\" legend row: " + svg);
    assertEquals(true, svg.substring(swatchBeforeIt, sLine).contains("rgb(255,0,0)"),
        "the legend swatch immediately before the \"s\" label has to be red: "
            + svg.substring(swatchBeforeIt, sLine));
  }
}
