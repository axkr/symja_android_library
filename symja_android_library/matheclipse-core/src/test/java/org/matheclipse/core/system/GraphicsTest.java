package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for graphics functions */
public class GraphicsTest extends ExprEvaluatorTestCase {

  public GraphicsTest(String name) {
    super(name);
  }

  public void testBernsteinBasis() {
    check("BernsteinBasis(3,2,0.3)", //
        "0.189");
  }

  public void testCircle() {
    check("Circle( )", //
        "Circle({0,0})");
  }

  public void testDisk() {
    check("Disk( )", //
        "Disk({0,0})");
  }

  public void testRectangle() {
    check("Rectangle( )", //
        "Rectangle({0,0})");
  }

  public void testVolume() {
    check("Volume(Ball({a,b,c}, r))", //
        "3/4*Pi*r^3");
    check("Volume(Cuboid({a,b,c}, {x,y,z}))", //
        "Abs((-a+x)*(-b+y)*(-c+z))");
    check("Volume(Ellipsoid({a,b,c}, {x,y,z}))", //
        "4/3*Pi*x*y*z");
    check("Volume(Ellipsoid({0,0,0}, {3,2,1}))", //
        "8*Pi");
  }

  public void testRGBColor() {
    check("Black", //
        "RGBColor(0.0,0.0,0.0)");
    check("Blue", //
        "RGBColor(0.0,0.0,1.0)");
  }

  public void testGraphicsJSON() {
    check("GraphicsJSON(Graphics(Polygon({{1, -1}, {0, Sqrt(7)}, {-1, -1}})))", //
        "{\"elements\":[{\"type\":\"polygon\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[[1.0,-1.0]],[[0.0,2.6457513110645907]],[[-1.0,-1.0]]]}]}");

    check("GraphicsJSON(Graphics({Red, Rectangle({0, 0}), Purple, Rectangle({0.75, 0.75})}))", //
        "{\"elements\":[{\"type\":\"rectangle\",\"color\":[1.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[0.0,0.0]],[[1.0,1.0]]]},{\"type\":\"rectangle\",\"color\":[0.5,0.0,0.5],\"opacity\":1.0,\"coords\":[[[0.75,0.75]],[[1.0,1.0]]]}]}");

    check("GraphicsJSON(Graphics({Red, Disk({1, 1}, 1, {0, Pi/4})}))", //
        "{\"elements\":[{\"type\":\"disk\",\"color\":[1.0,0.0,0.0],\"opacity\":0.5,\"radius1\":1.0,\"radius2\":1.0,\"angle1\":0.0,\"angle2\":0.7853981633974483,\"coords\":[[[1.0,1.0]]]}]}");

    check(
        "GraphicsJSON(Graphics({Blue, Circle({2,0}), Yellow, Polygon( {{2, 0}, {4, 1}, {4, -1}})}))", //
        "{\"elements\":[{\"type\":\"circle\",\"color\":[0.0,0.0,1.0],\"opacity\":0.5,\"radius1\":1.0,\"radius2\":1.0,\"coords\":[[[2.0,0.0]]]},{\"type\":\"polygon\",\"color\":[1.0,1.0,0.0],\"opacity\":1.0,\"coords\":[[[2.0,0.0]],[[4.0,1.0]],[[4.0,-1.0]]]}]}");

    check("GraphicsJSON(Graphics(Point( Table({t, Sin(t)}, {t, 0, 2 Pi, 2*Pi/10}) )))", //
        "{\"elements\":[{\"type\":\"point\",\"color\":[0.0,0.0,0.0],\"opacity\":1.0,\"coords\":[[[0.0,0.0]],[[0.6283185307179586,0.5877852522924731]],[[1.2566370614359172,0.9510565162951535]],[[1.8849555921538759,0.9510565162951535]],[[2.5132741228718345,0.5877852522924731]],[[3.141592653589793,0.0]],[[3.7699111843077517,-0.5877852522924731]],[[4.39822971502571,-0.9510565162951535]],[[5.026548245743669,-0.9510565162951535]],[[5.654866776461628,-0.5877852522924731]],[[6.283185307179586,0.0]]]}]}");
    
    check("GraphicsJSON(Graphics(Line({{1, 1}, {3, 1}, {4, 3}, {4, 7}})))", //
        "{\"elements\":[{\"type\":\"line\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[[1.0,1.0]],[[3.0,1.0]],[[4.0,3.0]],[[4.0,7.0]]]}]}");
    
    check("GraphicsJSON(Graphics(Circle({1, 1}, {3, 4})))", //
        "{\"elements\":[{\"type\":\"circle\",\"color\":[1.0,0.5,0.0],\"opacity\":0.5,\"radius1\":3.0,\"radius2\":4.0,\"coords\":[[[1.0,1.0]]]}]}");

    check("GraphicsJSON(Graphics(Arrow({{1, 0}, {2, 3}, {3, 7}, {4, 2}})))", //
        "{\"elements\":[{\"type\":\"arrow\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[[1.0,0.0]],[[2.0,3.0]],[[3.0,7.0]],[[4.0,2.0]]]}]}");
  }

  public void testGraphics3DJSON() {
    check("Graphics3DJSON(Graphics3D(Arrow({{1, 1, -1}, {2, 3, 0}, {3, 7, -1}, {4, 2, 0}})))", //
        "{\"elements\":[{\"type\":\"arrow\",\"color\":[1.0,0.5,0.0],\"opacity\":1.0,\"coords\":[[[1.0,1.0,-1.0]],[[2.0,3.0,0.0]],[[3.0,7.0,-1.0]],[[4.0,2.0,0.0]]]}],\"lighting\":[{\"type\":\"ambient\",\"color\":[0.4,0.2,0.2]},{\"type\":\"directional\",\"color\":[0.0,0.18,0.5],\"coords\":[[2.0,0.0,2.0]]},{\"type\":\"directional\",\"color\":[0.18,0.5,0.18],\"coords\":[[2.0,2.0,3.0]]},{\"type\":\"directional\",\"color\":[0.5,0.18,0.0],\"coords\":[[0.0,2.0,2.0]]},{\"type\":\"directional\",\"color\":[0.0,0.0,0.18],\"coords\":[[0.0,0.0,2.0]]}],\"viewpoint\":[1.3,-2.4,2.0]}");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
