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
    check(
        "GraphicsJSON(Graphics({Blue, Circle({2,0}), Yellow, Polygon( {{2, 0}, {4, 1}, {4, -1}})}))", //
        "{elements: [{type: 'circle',color: [0.0,0.0,1.0],opacity: 0.5,radius1: 1.0,radius2: 1.0,coords: [[[2.0,0.0]]]},{type: 'polygon',color: [1.0,1.0,0.0],opacity: 1.0,coords: [[[2.0,0.0]],[[4.0,1.0]],[[4.0,-1.0]]]}]}");

    check("GraphicsJSON(Graphics(Point( Table({t, Sin(t)}, {t, 0, 2 Pi, 2*Pi/10}) )))", //
        "{elements: [{type: 'point',color: [0.0,0.0,0.0],opacity: 1.0,coords: [[[0.0,0.0]],[[0.628319,0.587785]],[[1.25664,0.951057]],[[1.88496,0.951057]],[[2.51327,0.587785]],[[3.14159,0.0]],[[3.76991,-0.587785]],[[4.39823,-0.951057]],[[5.02655,-0.951057]],[[5.65487,-0.587785]],[[6.28319,0.0]]],pointSize: 0.02}]}");
    
    check("GraphicsJSON(Graphics(Line({{1, 1}, {3, 1}, {4, 3}, {4, 7}})))", //
        "{elements: [{type: 'line',color: [1.0, 0.5, 0.0],opacity: 1.0,coords: [[[1.0,1.0]],[[3.0,1.0]],[[4.0,3.0]],[[4.0,7.0]]]}]}");
    
    check("GraphicsJSON(Graphics(Circle({1, 1}, {3, 4})))", //
        "{elements: [{type: 'circle',color: [1.0, 0.5, 0.0],opacity: 0.5,radius1: 3.0,radius2: 4.0,coords: [[[1.0,1.0]]]}]}");

    check("GraphicsJSON(Graphics(Arrow({{1, 0}, {2, 3}, {3, 7}, {4, 2}})))", //
        "{elements: [{type: 'arrow',color: [1.0, 0.5, 0.0],opacity: 1.0,coords: [[[1.0,0.0]],[[2.0,3.0]],[[3.0,7.0]],[[4.0,2.0]]]}]}");
  }

  public void testGraphics3DJSON() {
    check("Graphics3DJSON(Graphics3D(Arrow({{1, 1, -1}, {2, 3, 0}, {3, 7, -1}, {4, 2, 0}})))", //
        "{\n" //
            + "axes: {},\n" //
            + "elements: [{type: 'arrow',color: [1.0, 0.5, 0.0],opacity: 1.0,coords: [[[1.0,1.0,-1.0]],[[2.0,3.0,0.0]],[[3.0,7.0,-1.0]],[[4.0,2.0,0.0]]]}],\n" //
            + "lighting: [\n" //
            + "{type: 'ambient',color: [0.4,0.2,0.2]},\n" //
            + "{type: 'directional',color: [0.0,0.18,0.5],coords: [[2,0,2]]},\n" //
            + "{type: 'directional',color: [0.18,0.5,0.18],coords: [[2,2,3]]},\n" //
            + "{type: 'directional',color: [0.5,0.18,0.0],coords: [[0,2,2]]},\n" //
            + "{type: 'directional',color: [0.0,0.0,0.18],coords: [[0,0,2]]}\n" //
            + "],\n" //
            + "viewpoint: [1.3, -2.4, 2.0]}");
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
