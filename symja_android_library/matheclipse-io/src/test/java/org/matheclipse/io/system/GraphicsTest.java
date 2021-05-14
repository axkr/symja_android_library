package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.script.engine.MathScriptEngine;

/** Tests for graphics functions */
public class GraphicsTest extends AbstractTestCase {

  public GraphicsTest(String name) {
    super(name);
  }

  public void testBernsteinBasis() {
    check(
        "BernsteinBasis(3,2,0.3)", //
        "0.189");
  }

  public void testVolume() {
    check(
        "Volume(Ball({a,b,c}, r))", //
        "3/4*Pi*r^3");
    check(
        "Volume(Cuboid({a,b,c}, {x,y,z}))", //
        "Abs((-a+x)*(-b+y)*(-c+z))");
    check(
        "Volume(Ellipsoid({a,b,c}, {x,y,z}))", //
        "4/3*Pi*x*y*z");
    check(
        "Volume(Ellipsoid({0,0,0}, {3,2,1}))", //
        "8*Pi");
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
