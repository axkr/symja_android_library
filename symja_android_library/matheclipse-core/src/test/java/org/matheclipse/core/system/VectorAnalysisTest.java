package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
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
  public void testCurlPolar() {
    check("Curl(f(x, y), {x, y},\"Polar\")", //
        "{-Derivative(0,1)[f][x,y]/x,Derivative(1,0)[f][x,y]}");
    check("Curl(f(a,b,x, y,z), {x, y},\"Polar\")", //
        "{-Derivative(0,0,0,1,0)[f][a,b,x,y,z]/x,Derivative(0,0,1,0,0)[f][a,b,x,y,z]}");
    check("Curl(f(x), {x, y},\"Polar\")", //
        "{0,f'(x)}");
  }

  @Test
  public void testCurlPolarVector() {
    check("Curl({f(x, y), g(x, y)}, {x, y},\"Polar\")", //
        "(g(x,y)-Derivative(0,1)[f][x,y])/x+Derivative(1,0)[g][x,y]");

  }

  @Test
  public void testCurlCylindrical() {
    check("Curl(f(x,y,z), {x,y,z},\"Cylindrical\")", //
        "{{0,Derivative(0,0,1)[f][x,y,z],-Derivative(0,1,0)[f][x,y,z]/x},{-Derivative(0,0,\n"
            + "1)[f][x,y,z],0,Derivative(1,0,0)[f][x,y,z]},{Derivative(0,1,0)[f][x,y,z]/x,-Derivative(\n"
            + "1,0,0)[f][x,y,z],0}}");
  }

  @Test
  public void testCurlSpherical() {
    check("Curl(f(x,y,z), {x,y,z},\"Spherical\")", //
        "{{0,(Csc(y)*Derivative(0,0,1)[f][x,y,z])/x,-Derivative(0,1,0)[f][x,y,z]/x},{(-Csc(y)*Derivative(\n" //
            + "0,0,1)[f][x,y,z])/x,0,Derivative(1,0,0)[f][x,y,z]},{Derivative(0,1,0)[f][x,y,z]/x,-Derivative(\n" //
            + "1,0,0)[f][x,y,z],0}}");
  }

  @Test
  public void testDiv() {
    // message Div: The scalar expression x^2 does not have a divergence.
    check("Div(x^2, {x, y, z})", //
        "Div(x^2,{x,y,z})");
    check(
        "Div({{x*y, x*y^2, x*y^3}, {x^2*y, x^2*y^2, x^2*y^3}, {x^3*y, x^3*y^2, x^3*y^3}}, {x, y, z})", //
        "{y+2*x*y,2*x*y+2*x^2*y,3*x^2*y+2*x^3*y}");
    check("Div({a(x, y), b(x, y)}, {x, y})", //
        "Derivative(0,1)[b][x,y]+Derivative(1,0)[a][x,y]");

    check("Div({{a[x, y], b[x, y]}, {c[x, y], d[x, y]}}, {x, y})", //
        "{Derivative(0,1)[b][x,y]+Derivative(1,0)[a][x,y],Derivative(0,1)[d][x,y]+Derivative(\n"
            + "1,0)[c][x,y]}");
  }

  @Test
  public void testDivCylindrical() {
    check("Div({f(r,t,z), g(r,t, z), h(r,t, z)},  {r,t,z},\"Cylindrical\")", //
        "Derivative(0,0,1)[h][r,t,z]+(f(r,t,z)+Derivative(0,1,0)[g][r,t,z])/r+Derivative(\n" //
            + "1,0,0)[f][r,t,z]");
  }


  @Test
  public void testDivSpherical() {
    check("Div({1, 1, 1}, {r,t,p}, \"Spherical\") // Expand", //
        "2/r+Cot(t)/r");
    check("Div(IdentityMatrix(3), {r,t,p}, \"Spherical\")", //
        "{0,0,0}");

  }

  @Test
  public void testDivPolarVector() {
    check("Div({r*Sin(t), -r*Cos(t)}, {r,t}, \"Polar\")", //
        "3*Sin(t)");
  }

  @Test
  public void testDivPolarMatrix() {
    check("Div({{f(x,y),g(x,y)},{h(x,y),i(x,y)}}, {x,y},\"Polar\")", //
        "{(f(x,y)-i(x,y)+Derivative(0,1)[g][x,y])/x+Derivative(1,0)[f][x,y],(g(x,y)+h(x,y)+Derivative(\n" //
            + "0,1)[i][x,y])/x+Derivative(1,0)[h][x,y]}");
    check("Div({{f(y,x),g(x,y)},{h(x,y),i(x,y)}}, {x,y},\"Polar\")", //
        "{Derivative(0,1)[f][y,x]+(f(y,x)-i(x,y)+Derivative(0,1)[g][x,y])/x,(g(x,y)+h(x,y)+Derivative(\n"
            + "0,1)[i][x,y])/x+Derivative(1,0)[h][x,y]}");
  }

  @Test
  public void testGrad() {
    check("Grad(6+a+b^4,{a,b})", //
        "{1,4*b^3}");
    check("Grad({6+a+b^4},{a,b})", //
        "{{1,4*b^3}}");

    check("Grad(Sin(x^2+y^2), {x, y})", //
        "{2*x*Cos(x^2+y^2),2*y*Cos(x^2+y^2)}");

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

    check("Grad({f(x, y), g(x, y)}, {x, y})", //
        "{{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]},{Derivative(1,0)[g][x,y],Derivative(\n" //
            + "0,1)[g][x,y]}}");
  }

  @Test
  public void testGradPolar() {
    check("Grad(f(a,b),{a,b},\"Polar\")", //
        "{Derivative(1,0)[f][a,b],Derivative(0,1)[f][a,b]/a}");
  }

  @Test
  public void testGradPolarVector() {
    check("Grad({f(a,b),g(a,b)},{a,b},\"Polar\")", //
        "{{Derivative(1,0)[f][a,b],(-g(a,b)+Derivative(0,1)[f][a,b])/a},{Derivative(1,0)[g][a,b],(f(a,b)+Derivative(\n" //
            + "0,1)[f][a,b])/a}}");
  }

  @Test
  public void testGradPolarMatrix() {
    check("Grad({{f(a,b),g(a,b)},{h(a,b),i(a,b)}},{a,b},\"Polar\")", //
        "{{{Derivative(1,0)[f][a,b],(-g(a,b)-h(a,b)+Derivative(0,1)[f][a,b])/a},{Derivative(\n" //
            + "1,0)[g][a,b],(f(a,b)-i(a,b)+Derivative(0,1)[g][a,b])/a}},{{Derivative(1,0)[h][a,b],(f(a,b)-i(a,b)+Derivative(\n" //
            + "0,1)[h][a,b])/a},{Derivative(1,0)[i][a,b],(g(a,b)+h(a,b)+Derivative(0,1)[i][a,b])/a}}}");
  }

  @Test
  public void testGradCylindrical() {
    check(" Grad(f(r,t,z), {r, t, z}, \"Cylindrical\")", //
        "{Derivative(1,0,0)[f][r,t,z],Derivative(0,1,0)[f][r,t,z]/r,Derivative(0,0,1)[f][r,t,z]}");
  }

  @Test
  public void testGradCylindricalVector() {
    // test some other variable/function orderings
    check("Grad({g(r,t,z),h(r,t,z),f(r,t,z)}, {z,r,t}, \"Cylindrical\")", //
        "{{Derivative(0,0,1)[g][r,t,z],(-h(r,t,z)+Derivative(1,0,0)[g][r,t,z])/z,Derivative(\n"
            + "0,1,0)[g][r,t,z]},{Derivative(0,0,1)[h][r,t,z],(g(r,t,z)+Derivative(1,0,0)[h][r,t,z])/z,Derivative(\n"
            + "0,1,0)[h][r,t,z]},{Derivative(0,0,1)[f][r,t,z],Derivative(1,0,0)[f][r,t,z]/z,Derivative(\n"
            + "0,1,0)[f][r,t,z]}}");
  }

  @Test
  public void testGradCylindricalMatrix() {
    check("Grad(IdentityMatrix(3), {r,t,z}, \"Cylindrical\")", //
        "{{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}}}");
  }

  @Test
  public void testGradSpherical() {
    check("-Grad(k*q/r, {r,t,z}, \"Spherical\")", //
        "{(k*q)/r^2,0,0}");
  }

  @Test
  public void testGradSphericalVector() {
    check("Grad({1, 1, 1}, {r,t,p}, \"Spherical\")", //
        "{{0,-1/r,-1/r},{0,1/r,-Cot(t)/r},{0,0,(Csc(t)*(Cos(t)+Sin(t)))/r}}");
    check("Grad({f(r,t,z),g(r,t,z),h(r,t,z)}, {r, t, z}, \"Spherical\")", //
        "{{Derivative(1,0,0)[f][r,t,z],(-g(r,t,z)+Derivative(0,1,0)[f][r,t,z])/r,(Csc(t)*(-h(r,t,z)*Sin(t)+Derivative(\n"//
            + "0,0,1)[f][r,t,z]))/r},{Derivative(1,0,0)[g][r,t,z],(f(r,t,z)+Derivative(0,1,0)[g][r,t,z])/r,(Csc(t)*(-Cos(t)*h(r,t,z)+Derivative(\n"//
            + "0,0,1)[g][r,t,z]))/r},{Derivative(1,0,0)[h][r,t,z],Derivative(0,1,0)[h][r,t,z]/r,(Csc(t)*(Cos(t)*g(r,t,z)+f(r,t,z)*Sin(t)+Derivative(\n"
            + "0,0,1)[h][r,t,z]))/r}}");
  }

  @Test
  public void testGradSphericalMatrix() {
    check("Grad(IdentityMatrix(3), {r,t,z}, \"Spherical\")", //
        "{{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}}}");
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

  @Test
  public void testLaplacian() {
    // https://mathematica.stackexchange.com/a/196958/21734
    check("Laplacian(Sin(x^2 + y^2), {x, y}) // Simplify", //
        "4*(Cos(x^2+y^2)-x^2*Sin(x^2+y^2)-y^2*Sin(x^2+y^2))");
    check("Laplacian(Sin(x^2 + y^2), {x, y}, \"Cartesian\")", //
        "4*Cos(x^2+y^2)-4*x^2*Sin(x^2+y^2)-4*y^2*Sin(x^2+y^2)");
    check("Laplacian(Sin(r^2), {r, t}, \"Polar\")", //
        "4*Cos(r^2)-4*r^2*Sin(r^2)");



    check("Laplacian(f(x,y,z),{x,y,z})", //
        "Derivative(0,0,2)[f][x,y,z]+Derivative(0,2,0)[f][x,y,z]+Derivative(2,0,0)[f][x,y,z]");
    check("Laplacian({Sin(x/y), Cos(y/x)}, {x, y}) // Simplify", //
        "{(2*x*y*Cos(x/y)-x^2*Sin(x/y)-y^2*Sin(x/y))/y^4,(-x^2*Cos(y/x)-y^2*Cos(y/x)-2*x*y*Sin(y/x))/x^\n"
            + "4}");
    check("Laplacian({{x y, x y^2}, {x^2 y, x^2 y^2}}, {x, y})", //
        "{{0,2*x},{2*y,2*x^2+2*y^2}}");

    check("Laplacian({{a(x, y), b(x, y)}, {c(x, y), d(x, y)}}, {x, y})", //
        "{{Derivative(0,2)[a][x,y]+Derivative(2,0)[a][x,y],Derivative(0,2)[b][x,y]+Derivative(\n" //
            + "2,0)[b][x,y]},{Derivative(0,2)[c][x,y]+Derivative(2,0)[c][x,y],Derivative(0,2)[d][x,y]+Derivative(\n" //
            + "2,0)[d][x,y]}}");
  }

  @Test
  public void testLaplacianCylindrical() {
    check("Laplacian(f(r,t,z), {r,t,z}, \"Cylindrical\") // Expand", //
        "Derivative(0,0,2)[f][r,t,z]+Derivative(0,2,0)[f][r,t,z]/r^2+Derivative(1,0,0)[f][r,t,z]/r+Derivative(\n" //
            + "2,0,0)[f][r,t,z]");
  }

  @Test
  public void testLaplacianCylindricalVector() {
    check(
        "Laplacian({Sqrt(r^2 + z^2), 0, Sqrt(r^2 + z^2)}, {r, t, z}, \"Cylindrical\") // Simplify", //
        "{-r^2/(r^2+z^2)^(3/2)-z^2/(r^2+z^2)^(3/2)+2/Sqrt(r^2+z^2)-z^2/(r^2*Sqrt(r^2+z^2)),\n" //
            + "0,2/Sqrt(r^2+z^2)}");
  }

  @Test
  public void testLaplacianPolar() {
    check("Laplacian(Sin(r^2), {r,t}, \"Polar\")", //
        "4*Cos(r^2)-4*r^2*Sin(r^2)");
  }

  @Test
  public void testLaplacianPolarVector() {
    check("Laplacian({a,b}, {r,t}, \"Polar\")", //
        "{-a/r^2,-b/r^2}");
    check("Laplacian({a*t,b*r}, {r,t}, \"Polar\")", //
        "{(-a*t)/r^2,(b+a/r+(a-b*r)/r)/r}");
  }

  @Test
  public void testLaplacianSpherical() {
    check("Laplacian(f(x,y,z), {x,y,z},\"Spherical\")", //
        "(Derivative(0,2,0)[f][x,y,z]/x+Derivative(1,0,0)[f][x,y,z])/x+(Csc(y)*((Csc(y)*Derivative(\n" //
            + "0,0,2)[f][x,y,z])/x+(Cos(y)*Derivative(0,1,0)[f][x,y,z])/x+Sin(y)*Derivative(1,0,\n" //
            + "0)[f][x,y,z]))/x+Derivative(2,0,0)[f][x,y,z]");
  }

  @Test
  public void testLaplacianSphericalVector() {
    check("Laplacian({a,b,c}, {r,t,p},\"Spherical\")", //
        "{-a/r^2+(Csc(t)*((-b*Cos(t))/r-(b*Cos(t)+a*Sin(t))/r))/r,"//
            + "-b/r^2+(Csc(t)*((a*Cos(t))/r+(-Cot(t)*(b*Cos(t)+a*Sin(t)))/r))/r,"//
            + "(Csc(t)*((-c*Cos(t)*Cot(t))/r+(-c*Sin(t))/r))/r}");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

}
