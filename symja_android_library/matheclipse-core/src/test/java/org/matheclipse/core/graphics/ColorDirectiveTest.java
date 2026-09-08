package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What counts as a colour when a {@code ColorFunction} answers with one.
 *
 * <p>
 * The predicate used to enumerate four heads, which quietly rejected the two commonest things a
 * colour function can produce: a named colour, and an {@code Opacity} or {@code Directive}. A
 * rejected answer falls back to the plot's own colouring, so the option looked as if it did
 * nothing.
 */
public class ColorDirectiveTest {

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
    evaluator.eval("ClearAll(a,b,c,x,y,z)");
  }

  private static IExpr eval(String input) {
    return evaluator.eval(input);
  }

  // ------------------------------------------------------------- accepted

  @Test
  public void testTheColourSpaceHeadsAreColours() {
    for (String colour : new String[] {"RGBColor(1,0,0)", "RGBColor(1,0,0,0.5)", "Hue(0.5)",
        "Hue(0.5,1,1)", "GrayLevel(0.2)", "GrayLevel(0.2,0.5)", "CMYKColor(0,0,0,1)"}) {
      assertTrue(GraphicsOptions.isColorExpr(eval(colour)), colour + " should be a colour");
    }
  }

  /**
   * The one that mattered most: a named colour is a bare symbol in Symja, and
   * {@code If(#2 > 0, Red, Blue)} is how a colour function is usually written.
   */
  @Test
  public void testNamedColoursAreColours() {
    for (String colour : new String[] {"Red", "Blue", "Green", "Black", "White", "Orange",
        "LightBlue", "DarkGray", "Transparent"}) {
      assertTrue(GraphicsOptions.isColorExpr(eval(colour)), colour + " should be a colour");
    }
  }

  @Test
  public void testDerivedColoursAreColours() {
    for (String colour : new String[] {"Blend({Red, Blue}, 0.5)", "Lighter(Red)", "Darker(Blue)",
        "Opacity(0.5, Red)"}) {
      assertTrue(GraphicsOptions.isColorExpr(eval(colour)), colour + " should be a colour");
    }
  }

  @Test
  public void testABareOpacityIsADirective() {
    assertTrue(GraphicsOptions.isColorExpr(eval("Opacity(0.5)")),
        "Opacity(o) names no colour of its own, but it is something a renderer can apply");
  }

  @Test
  public void testADirectiveIsAcceptedWhenEveryPartIs() {
    assertTrue(GraphicsOptions.isColorExpr(eval("Directive(Red)")));
    assertTrue(GraphicsOptions.isColorExpr(eval("Directive(Opacity(0.5), Red)")));
    assertTrue(GraphicsOptions.isColorExpr(eval("Directive({Opacity(0.5), Orange})")));
    assertTrue(GraphicsOptions.isColorExpr(eval("Directive(Red, Thickness(0.02))")));
    // a bare list of directives means what Directive means, which is what ColorUtil already
    // implements for Background; the last colour in it wins
    assertTrue(GraphicsOptions.isColorExpr(eval("{Red, Blue}")));
    assertEquals(255, ColorUtil.parse(GraphicsOptions.toColorExpr(eval("{Red, Blue}"), F.NIL))
        .getBlue(), "the last colour of a directive list is the one that applies");
  }

  @Test
  public void testTheTwoColourSpacesTheChemModuleAlreadyUsed() {
    assertTrue(GraphicsOptions.isColorExpr(eval("XYZColor(0.4, 0.2, 0.1)")));
    assertTrue(GraphicsOptions.isColorExpr(eval("LABColor(0.5, 0.1, -0.1)")));
  }

  // ------------------------------------------------------------- rejected

  @Test
  public void testThingsThatAreNotColours() {
    for (String other : new String[] {"42", "\"red\"", "x", "Thickness(0.1)", "{}",
        "{1, 2}", "Sin(x)", "Glow(Red)"}) {
      assertFalse(GraphicsOptions.isColorExpr(eval(other)), other + " should not be a colour");
    }
    assertFalse(GraphicsOptions.isColorExpr(null));
    assertFalse(GraphicsOptions.isColorExpr(F.NIL));
  }

  // ------------------------------------------------------------- flattening

  @Test
  public void testADirectiveFoldsDownToOneColour() {
    IExpr flat = GraphicsOptions.toColorExpr(eval("Directive(Opacity(0.5), Red)"), F.NIL);
    assertTrue(flat.isAST(S.RGBColor), () -> "expected an RGBColor, got " + flat);
    Color c = ColorUtil.parse(flat);
    assertNotNull(c);
    assertEquals(255, c.getRed());
    assertEquals(0, c.getGreen());
    assertEquals(128, c.getAlpha(), 2, "the opacity has to survive into the alpha channel");
  }

  /** A bare opacity names no colour, so it fades whatever the plot would have drawn there. */
  @Test
  public void testABareOpacityFadesTheColourUnderIt() {
    IExpr flat = GraphicsOptions.toColorExpr(eval("Opacity(0.5)"), eval("Blue"));
    Color c = ColorUtil.parse(flat);
    assertNotNull(c);
    assertEquals(0, c.getRed());
    assertEquals(255, c.getBlue());
    assertEquals(128, c.getAlpha(), 2);
  }

  @Test
  public void testAThicknessIsDroppedButItsColourIsKept() {
    Color c = ColorUtil.parse(GraphicsOptions.toColorExpr(eval("Directive(Red, Thickness(0.02))"),
        F.NIL));
    assertNotNull(c, "a raster cell has no stroke, but it still has the colour");
    assertEquals(255, c.getRed());
  }

  @Test
  public void testSomethingUnrenderableFlattensToNothing() {
    assertFalse(GraphicsOptions.toColorExpr(eval("42"), F.NIL).isPresent());
    assertFalse(GraphicsOptions.toColorExpr(F.NIL, F.NIL).isPresent());
  }

  // ------------------------------------------------------------- the new colour spaces

  @Test
  public void testLabAndXyzLandWhereTheyShould() {
    // L* = 1 with no chroma is the white point
    Color white = ColorUtil.parse(eval("LABColor(1, 0, 0)"));
    assertNotNull(white);
    assertEquals(255, white.getRed(), 2);
    assertEquals(255, white.getGreen(), 2);
    assertEquals(255, white.getBlue(), 2);

    Color black = ColorUtil.parse(eval("LABColor(0, 0, 0)"));
    assertNotNull(black);
    assertEquals(0, black.getRed(), 2);
    assertEquals(0, black.getGreen(), 2);
    assertEquals(0, black.getBlue(), 2);

    // the D65 white point in XYZ is white in sRGB too
    Color xyzWhite = ColorUtil.parse(eval("XYZColor(0.95047, 1.0, 1.08883)"));
    assertNotNull(xyzWhite);
    assertEquals(255, xyzWhite.getRed(), 2);
    assertEquals(255, xyzWhite.getGreen(), 2);
    assertEquals(255, xyzWhite.getBlue(), 2);
  }

  @Test
  public void testAnAlphaChannelSurvivesBothSpaces() {
    Color lab = ColorUtil.parse(eval("LABColor(0.5, 0, 0, 0.25)"));
    assertNotNull(lab);
    assertEquals(64, lab.getAlpha(), 2);
    Color xyz = ColorUtil.parse(eval("XYZColor(0.2, 0.2, 0.2, 0.25)"));
    assertNotNull(xyz);
    assertEquals(64, xyz.getAlpha(), 2);
  }

  // ------------------------------------------------------------- ColorRules

  /** A rule written with an integer has to match data that came back as a real. */
  @Test
  public void testColorRulesMatchAcrossNumberTypes() {
    IAST rules = (IAST) eval("{1 -> Red, 2 -> Blue}");
    assertNotNull(GraphicsOptions.colorRule(rules, F.C1), "1 should match the rule for 1");
    assertNotNull(GraphicsOptions.colorRule(rules, F.num(1.0)),
        "1.0 should match the rule written as 1");
    assertNotNull(GraphicsOptions.colorRule(rules, F.num(2.0)));
    org.junit.jupiter.api.Assertions.assertNull(GraphicsOptions.colorRule(rules, F.num(3.0)));
  }
}
