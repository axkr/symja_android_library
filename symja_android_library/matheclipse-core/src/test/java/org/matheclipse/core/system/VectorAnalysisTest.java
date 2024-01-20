package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for vector analysis functions */
public class VectorAnalysisTest extends ExprEvaluatorTestCase {

  @Test
  public void testCurl2D() {
    check("v(x_, y_) := {Cos(x)*Sin(y), Cos(y)*Sin(x)}", //
        "");
    check("Curl(v(x, y), {x, y})", //
        "0");
  }

  @Test
  public void testCurl3D() {
    check("Curl({y, -x, 2*z}, {x, y, z})", //
        "{0,0,-2}");
    check("Curl({f(x, y, z), g(x, y, z), h(x, y, z)}, {x, y, z})", //
        "{-Derivative(0,0,1)[g][x,y,z]+Derivative(0,1,0)[h][x,y,z],Derivative(0,0,1)[f][x,y,z]-Derivative(\n"
            + "1,0,0)[h][x,y,z],-Derivative(0,1,0)[f][x,y,z]+Derivative(1,0,0)[g][x,y,z]}");
  }

  @Test
  public void testGrad() {
    // create Jacobian matrix

    // https://en.wikipedia.org/wiki/Jacobian_matrix_and_determinant
    check("Grad({x1,5*x3,4*x2^2-2*x3,x3*Sin[x1]},{x1,x2,x3})", //
        "{{1,0,0},{0,0,5},{0,8*x2,-2},{x3*Cos(x1),0,Sin(x1)}}");

    check("Grad({f(x, y),g(x,y)}, {x, y})", //
        "{{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]},{Derivative(1,0)[g][x,y],Derivative(\n" //
            + "0,1)[g][x,y]}}");

    check("Grad({f(x, y, z), g(x, y, z), h(x, y, z)}, {x, y, z})", //
        "{{Derivative(1,0,0)[f][x,y,z],Derivative(0,1,0)[f][x,y,z],Derivative(0,0,1)[f][x,y,z]},{Derivative(\n" //
            + "1,0,0)[g][x,y,z],Derivative(0,1,0)[g][x,y,z],Derivative(0,0,1)[g][x,y,z]},{Derivative(\n" //
            + "1,0,0)[h][x,y,z],Derivative(0,1,0)[h][x,y,z],Derivative(0,0,1)[h][x,y,z]}}");

    check("Grad({2*x1+x2-Exp(-x1), -x1+2*x2-Exp(-x2)},{x1,x2})", //
        "{{2+E^(-x1),1},{-1,2+E^(-x2)}}");
    check("Grad({Exp(-Exp(-(x1+x2)))-x2*(1+x1^2), x1*Cos(x2)+x2*Sin(x1)-0.5},{x1,x2})", //
        "{{E^(-1/E^(x1+x2)-x1-x2)-2*x1*x2,-1+E^(-1/E^(x1+x2)-x1-x2)-x1^2},{x2*Cos(x1)+Cos(x2),Sin(x1)-x1*Sin(x2)}}");

    // gradient
    check("Grad(f(x, y), {x, y})", //
        "{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
    check("Grad(Sin(x^2 + y^2), {x, y})", //
        "{2*x*Cos(x^2+y^2),2*y*Cos(x^2+y^2)}");

    // wikipedia
    check("Grad(2*x+3*y^2-Sin(z), {x, y, z})", //
        "{2,6*y,-Cos(z)}");

    check("gr=Grad(x*y*z, {x, y, z})", //
        "{y*z,x*z,x*y}");
    check("Grad(gr, {x, y, z})", //
        "{{0,z,y},{z,0,x},{y,x,0}}");
  }

  @Test
  public void testHessianMatrix() {
    // create Hessian matrix
    // https://en.wikipedia.org/wiki/Hessian_matrix
    check("HessianMatrix(x*Cos(y), {x, y})", //
        "{{0,-Sin(y)},{-Sin(y),-x*Cos(y)}}");
    check("HessianMatrix(x^3 - 2 x y - y^6, {x, y})", //
        "{{6*x,-2},{-2,-30*y^4}}");

    check("HessianMatrix(f(x,y,z), {x, y,z})", //
        "{{Derivative(2,0,0)[f][x,y,z],Derivative(1,1,0)[f][x,y,z],Derivative(1,0,1)[f][x,y,z]},{Derivative(\n" //
            + "1,1,0)[f][x,y,z],Derivative(0,2,0)[f][x,y,z],Derivative(0,1,1)[f][x,y,z]},{Derivative(\n" //
            + "1,0,1)[f][x,y,z],Derivative(0,1,1)[f][x,y,z],Derivative(0,0,2)[f][x,y,z]}}");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
