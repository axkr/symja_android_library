package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** The color tables behind the named gradients and indexed schemes of {@code ColorData}. */
public class ColorDataTablesTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
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
   * <code>ColorData(97)</code> applied to <code>index</code>. Built in Java, because in the relaxed
   * syntax <code>ColorData(97)(1)</code> is a product.
   */
  private static IExpr scheme97(String index) {
    return scheme(97, index);
  }

  /** <code>ColorData(number)</code> applied to <code>index</code>. */
  private static IExpr scheme(int number, String index) {
    IExpr function = evaluator.eval("ColorData(" + number + ")");
    return evaluator.eval(F.unaryAST1(function, evaluator.eval(index)));
  }

  /** The red, green and blue components of an <code>RGBColor</code>. */
  private static double[] rgb(IExpr color) {
    assertTrue(color.isAST(S.RGBColor), color.toString());
    IAST ast = (IAST) color;
    return new double[] {ast.arg1().evalf(), ast.arg2().evalf(), ast.arg3().evalf()};
  }

  private static void assertRGB(double[] expected, IExpr color, double tolerance) {
    double[] actual = rgb(color);
    for (int i = 0; i < 3; i++) {
      assertEquals(expected[i], actual[i], tolerance, color.toString());
    }
  }

  /** Colors 1 to 10 of scheme 97 are listed; color 8 has exact components. */
  @Test
  public void testScheme97ListedColors() {
    assertEquals("RGBColor(0.368417,0.506779,0.709798)", scheme97("1").toString());
    assertEquals("RGBColor(1,0.75,0)", scheme97("8").toString());
    assertEquals("RGBColor(0.571589,0.586483,0.0)", scheme97("10").toString());
  }

  /** A real index is taken down to the integer below it. */
  @Test
  public void testScheme97RealIndexIsFloored() {
    assertEquals("RGBColor(0.880722,0.611041,0.142051)", scheme97("2.5").toString());
    assertEquals(scheme97("2").toString(), scheme97("2.7").toString());
    assertEquals(scheme97("2").toString(), scheme97("5/2").toString());
  }

  /**
   * Beyond the listed colors the scheme blends its anchors at the fractional part of
   * <code>(n - 11) / GoldenRatio</code>, so it never repeats.
   */
  @Test
  public void testScheme97BlendsWithoutEnd() {
    assertEquals("RGBColor(0.915,0.3325,0.2125)", scheme97("11").toString());
    // compared bit for bit: the arithmetic is in machine numbers
    IAST color16 = (IAST) scheme97("16");
    assertEquals(0.838355547812947, color16.arg1().evalf(), 0.0);
    assertEquals(0.44746667828057946, color16.arg2().evalf(), 0.0);
    assertEquals(0.0208888695323676, color16.arg3().evalf(), 0.0);
    assertTrue(scheme97("2^40+1").isAST(S.RGBColor));
    assertFalse(scheme97("2^40+1").toString().equals(scheme97("1").toString()));
    assertFalse(scheme97("17").toString().equals(scheme97("2").toString()));
  }

  /** Index 0 blends to the first anchor; a negative index has no color. */
  @Test
  public void testScheme97BelowOne() {
    assertEquals("RGBColor(0.915,0.3325,0.2125)", scheme97("0").toString());
    assertFalse(scheme97("-1").isAST(S.RGBColor));
  }

  @Test
  public void testScheme97ColorListAndRange() {
    assertEquals("15", evaluator.eval("Length(ColorData(97, \"ColorList\"))").toString());
    assertEquals("{1,Infinity,1}", evaluator.eval("ColorData(97, \"Range\")").toString());
  }

  /**
   * A scheme with listed colors repeats them: index <code>n</code> is color
   * <code>Mod(Floor(n), length, 1)</code>, so 0 is the last color. A negative index has no color.
   */
  @Test
  public void testListedSchemesRepeat() {
    String first = scheme(3, "1").toString();
    assertEquals("RGBColor(0.0,0.0,0.0)", first);
    assertEquals(first, scheme(3, "11").toString());
    assertEquals(first, scheme(3, "10*2^40+1").toString());
    assertEquals(scheme(3, "2").toString(), scheme(3, "52").toString());
    assertEquals(scheme(3, "10").toString(), scheme(3, "0").toString());
    assertEquals(scheme(3, "2").toString(), scheme(3, "2.7").toString());
    assertFalse(scheme(3, "-1").isAST(S.RGBColor));
    assertEquals("{1,10,1}", evaluator.eval("ColorData(3, \"Range\")").toString());
  }

  /** Scheme 0 is black only, and scheme 74 lists its colors by hue. */
  @Test
  public void testListedSchemesKeepTheirColorForm() {
    assertEquals("GrayLevel(0)", scheme(0, "5").toString());
    assertEquals("{1,1,1}", evaluator.eval("ColorData(0, \"Range\")").toString());
    assertTrue(scheme(74, "1").isAST(S.Hue), scheme(74, "1").toString());
    assertEquals("RGBColor(0,0.501961,0)", scheme(58, "2").toString());
  }

  @Test
  public void testColorListLengths() {
    int[][] lengths = {{0, 1}, {1, 15}, {3, 10}, {58, 14}, {61, 9}, {63, 15}, {74, 10}, {94, 15},
        {98, 15}, {112, 15}};
    for (int[] numberAndLength : lengths) {
      assertEquals(String.valueOf(numberAndLength[1]),
          evaluator.eval("Length(ColorData(" + numberAndLength[0] + ", \"ColorList\"))")
              .toString(),
          "scheme " + numberAndLength[0]);
    }
  }

  /** Hues spaced by the golden ratio, computed exactly as the reference does. */
  @Test
  public void testGoldenHueSchemes() {
    assertRGB(new double[] {0.24720000000000014, 0.24, 0.6}, scheme(1, "1"), 0.0);
    assertRGB(new double[] {0.6, 0.24, 0.2941043604063603}, scheme(1, "15"), 0.0);
    assertRGB(new double[] {0.1971, 0.5022473119339774, 0.73}, scheme(63, "2"), 0.0);
    assertEquals("{1,Infinity,1}", evaluator.eval("ColorData(1, \"Range\")").toString());
    assertFalse(scheme(1, "16").toString().equals(scheme(1, "1").toString()));
  }

  /** L*a*b* colors around a circle; the conversion to sRGB agrees to a few units in the last place. */
  @Test
  public void testGoldenLabSchemes() {
    assertRGB(new double[] {0.2101494560119695, 0.4780271451416804, 0.8021972051611045},
        scheme(94, "1"), 1.0e-14);
    assertRGB(new double[] {1.0, 0.4770663208192674, 0.4000029301063918}, scheme(96, "15"),
        1.0e-14);
  }

  /** After the listed colors the anchors are blended at golden ratio steps. */
  @Test
  public void testListedThenBlendedSchemes() {
    assertRGB(new double[] {0.5170108648476139, 0.4061934810914317, 0.72}, scheme(98, "11"),
        1.0e-14);
    // scheme 110 lists 8 colors only
    assertRGB(new double[] {0.31102485460500007, 0.76, 0.6186956395936363}, scheme(110, "9"),
        1.0e-14);
    // a listed hue is given as RGB
    assertTrue(scheme(104, "2").isAST(S.RGBColor), scheme(104, "2").toString());
    assertRGB(new double[] {0.7529330319872088, 0.4176501130047967, 1.0}, scheme(109, "11"), 0.0);
  }

  /** A hue just below 0 rounds up to a full turn, which is red again. */
  @Test
  public void testHueJustBelowZero() {
    assertEquals("{255,0,0,255}", ColorDataGradients.HUE.apply(F.num(-1.0e-17)).toString());
  }

  @Test
  public void testTableGradientEnds() {
    assertEquals("{53,52,61,255}", ColorDataGradients.CLASSIC.apply(F.C0).toString());
    assertEquals("{255,237,237,255}", ColorDataGradients.CLASSIC.apply(F.C1).toString());
  }

  /** A table cell with a decimal point is a machine number, the others integers. */
  @Test
  public void testDecimalCells() {
    IAST first = (IAST) ColorDataGradients.PASTEL.apply(F.C0);
    assertTrue(first.arg1().isReal() && !first.arg1().isInteger(), first.toString());
    assertEquals(194.3, first.arg1().evalf(), 0.0);
    assertTrue(first.arg4().isInteger(), first.toString());
  }

  /** Halfway between the first two rows of the 16 rows of the table. */
  @Test
  public void testInterpolation() {
    IAST middle = (IAST) ColorDataGradients.PASTEL.apply(F.QQ(1, 30));
    assertEquals((194.3 + 203.23) / 2, middle.arg1().evalf(), 1.0e-12);
    assertEquals((120.06 + 141.53) / 2, middle.arg2().evalf(), 1.0e-12);
    assertEquals((239.85 + 242.22) / 2, middle.arg3().evalf(), 1.0e-12);
    assertEquals(255.0, middle.arg4().evalf(), 1.0e-12);
  }

  @Test
  public void testOutOfRangeIsClamped() {
    assertEquals(ColorDataGradients.CLASSIC.apply(F.C1),
        ColorDataGradients.CLASSIC.apply(F.num(1.5)));
    assertEquals(ColorDataGradients.CLASSIC.apply(F.C0),
        ColorDataGradients.CLASSIC.apply(F.CN1));
  }

  @Test
  public void testNoNumberIsTransparent() {
    assertEquals("{0.0,0.0,0.0,0.0}",
        ColorDataGradients.CLASSIC.apply(F.symbol("x")).toString());
    assertEquals("{0.0,0.0,0.0,0.0}", ColorDataGradients.HUE.apply(F.CInfinity).toString());
    assertEquals("{0.0,0.0,0.0,0.0}", ColorDataGradients.GRAYSCALE.apply(F.num(1.5)).toString());
  }

  @Test
  public void testHue() {
    assertEquals("{255,0,0,255}", ColorDataGradients.HUE.apply(F.C0).toString());
    assertEquals("{0,255,0,255}", ColorDataGradients.HUE.apply(F.QQ(1, 3)).toString());
    assertEquals("{0,0,255,255}", ColorDataGradients.HUE.apply(F.QQ(2, 3)).toString());
    // the hue circle is periodic
    assertEquals(ColorDataGradients.HUE.apply(F.num(0.25)),
        ColorDataGradients.HUE.apply(F.num(1.25)));
  }

  @Test
  public void testGrayscale() {
    assertEquals("{128,128,128,255}", ColorDataGradients.GRAYSCALE.apply(F.C1D2).toString());
    assertEquals("{0,0,0,255}", ColorDataGradients.GRAYSCALE.apply(F.C0).toString());
  }

  @Test
  public void testRGBAListRoundTrip() {
    RGBColor color = new RGBColor(12, 34, 56, 78);
    IAST rgba = color.toRGBAList();
    assertEquals("{12,34,56,78}", rgba.toString());
    assertEquals(color, RGBColor.fromRGBAList(rgba));
    IExpr threeComponents = F.List(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> RGBColor.fromRGBAList(threeComponents));
  }
}
