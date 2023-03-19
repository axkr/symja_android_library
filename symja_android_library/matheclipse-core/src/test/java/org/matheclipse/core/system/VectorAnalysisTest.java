package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for vector analysis functions */
public class VectorAnalysisTest extends ExprEvaluatorTestCase {

  public VectorAnalysisTest(String name) {
    super(name);
  }

  public void testCurl2D() {
    check("v(x_, y_) := {Cos(x)*Sin(y), Cos(y)*Sin(x)}", //
        "");
    check("Curl(v(x, y), {x, y})", //
        "0");
  }

  public void testCurl3D() {
    check("Curl({y, -x, 2*z}, {x, y, z})", //
        "{0,0,-2}");
    check("Curl({f(x, y, z), g(x, y, z), h(x, y, z)}, {x, y, z})", //
        "{-Derivative(0,0,1)[g][x,y,z]+Derivative(0,1,0)[h][x,y,z],Derivative(0,0,1)[f][x,y,z]-Derivative(\n"
            + "1,0,0)[h][x,y,z],-Derivative(0,1,0)[f][x,y,z]+Derivative(1,0,0)[g][x,y,z]}");
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
