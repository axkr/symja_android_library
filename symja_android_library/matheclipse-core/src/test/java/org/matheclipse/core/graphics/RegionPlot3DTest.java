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

/**
 * {@code RegionPlot3D}: the surface of the solid where a condition holds, closed where it meets
 * the box - the example of the WLJS demo notebook "Pathtracing" among them.
 */
public class RegionPlot3DTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
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
    evaluator.eval("ClearAll(p,x,y,z)");
  }

  private static String eval(String expression) {
    return evaluator.eval(expression).toString();
  }

  private static int count(String expression) {
    IExpr result = evaluator.eval(expression);
    int value = result.toIntDefault(-1);
    assertTrue(value >= 0, expression + " did not count anything: " + result);
    return value;
  }

  @Test
  public void testDemoNotebookRegion() {
    evaluator.eval("p=RegionPlot3D(x*y*z<1,{x,-5,5},{y,-5,5},{z,-5,5},"
        + "PlotStyle->{Directive(Cyan,\"Roughness\"->0.0)},Mesh->None,"
        + "Lighting->{SpotLight(Green,{-2.7718,-8.7007,8.1559}),"
        + "SpotLight(Red,{-2.7718,8.7007,8.1559})})");
    assertEquals("Graphics3D", eval("Head(p)"));
    assertTrue(count("Length(Cases(p,Polygon(l_):>Length(l),Infinity))") > 0);
    // Mathematica puts the lights into the style of the surface too, beside the colour and the
    // material property
    assertEquals("1", eval("Length(Cases(p,Directive(Lighting->{_SpotLight,_SpotLight},"
        + "RGBColor(0,1,1),\"Roughness\"->0.0),Infinity))"));
    // the options in a list, as Mathematica writes them, so the demo's Insert adds one more
    assertEquals("True", eval("MemberQ(p[[2]],Lighting->{_SpotLight,_SpotLight})"));
    assertEquals("{True,2,True}",
        eval("q=Insert(p,\"Renderer\"->\"PathTracing\",{2,-1});"
            + "{Head(q)===Graphics3D,Length(q),Last(q[[2]])===(\"Renderer\"->\"PathTracing\")}"));
  }

  @Test
  public void testSolidIsCappedAtTheBox() {
    // x+y+z<1 fills a corner of the box: its surface is the slanted plane and three flat caps on
    // the faces x=-1, y=-1 and z=-1
    evaluator.eval("p=RegionPlot3D(x+y+z<1,{x,-1,1},{y,-1,1},{z,-1,1})");
    for (int c = 1; c <= 3; c++) {
      assertTrue(count("Length(Select(First(Cases(p,GraphicsComplex(v_,__):>v,Infinity)),"
          + "Abs(#[[" + c + "]]+1.0)<1.0*^-9&))") > 3, "no cap on the face of axis " + c);
    }
  }

  @Test
  public void testSphereStaysNearTheUnitSphere() {
    evaluator.eval("p=RegionPlot3D(x^2+y^2+z^2<1,{x,-1.5,1.5},{y,-1.5,1.5},{z,-1.5,1.5})");
    // no cap: the ball does not reach the box, and every vertex lies close to its surface
    assertEquals("True", eval("Max(Abs(Norm/@First(Cases(p,GraphicsComplex(v_,__):>v,"
        + "Infinity))-1.0))<0.05"));
  }

  @Test
  public void testConditions() {
    // And, Or, Not and a chain of comparisons
    assertEquals("Graphics3D",
        eval("Head(RegionPlot3D(x^2+y^2<1&&Abs(z)<0.5||!(x<0),{x,-1,1},{y,-1,1},{z,-1,1}))"));
    assertEquals("Graphics3D",
        eval("Head(RegionPlot3D(-0.5<x<0.5,{x,-1,1},{y,-1,1},{z,-1,1}))"));
    // an empty region is an empty picture
    assertEquals("0", eval("Length(Cases(RegionPlot3D(x^2<-1,{x,-1,1},{y,-1,1},{z,-1,1}),"
        + "_Polygon,Infinity))"));
  }
}
