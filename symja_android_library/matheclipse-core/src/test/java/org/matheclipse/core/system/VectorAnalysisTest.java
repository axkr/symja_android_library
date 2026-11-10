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
            + "0,1)[g][a,b])/a}}");
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
    // the theta component includes D(A_theta, {r, 2}): 12*r - r*Csc(t)^2 for A_theta = r^3
    check("Laplacian({0, r^3, 0}, {r,t,p},\"Spherical\")", //
        "{-2*r*Cot(t),8*r+(Csc(t)*(-r^2*Cos(t)*Cot(t)+3*r^2*Sin(t)))/r,0}");
  }

  @Test
  public void testLaplacianConstant() {
    // The Laplacian of a constant should evaluate to 0
    check("Laplacian(5, {x, y})", //
        "0");

    // The Laplacian of an independent variable should evaluate to 0
    check("Laplacian(a, {x, y, z})", //
        "0");
  }

  @Test
  public void testLaplacianCartesianOptimization() {
    // Ensures that variables not present in the function are correctly pruned
    // without breaking the summation
    check("Laplacian(x^2 + 5, {x, y, z})", //
        "2");
  }

  @Test
  public void testCurlSphericalScalar() {
    // Ensures that the Spherical evaluation path for Curl doesn't throw or map incorrectly
    // due to the old Metric.CYLINDRICAL misassignment.
    check("Curl(r*Cos(p), {r, t, p}, \"Spherical\")", //
        "{{0,-Csc(t)*Sin(p),0},{Csc(t)*Sin(p),0,Cos(p)},{0,-Cos(p),0}}");
  }

  @Test
  public void testCoordinateTransformDataListing() {
    check("t=CoordinateTransformData();{Length(t),Counts(t[[All,3]]),Counts(t[[All,2]])}", //
        "{278,<|2->40,3->229,1->5,4->4|>,<|Euclidean->266,{Sphere,{R}}->12|>}");
    // every chart of a metric and dimension is transformed into every chart of it
    check("t=CoordinateTransformData();"
        + "Union(Map(Length(Cases(t, {_, #[[1]], #[[2]]}))&, Union(t[[All,{2,3}]])))", //
        "{1,4,36,225}");
    // a clashing parameter of the target chart is renamed
    check("InputForm(Select(CoordinateTransformData(), "
        + "MatchQ(#[[1]], {\"Conical\", _} -> {\"Conical\", _})&))", //
        "{{{\"Conical\",{\\[FormalB],\\[FormalC]}}->{\"Conical\",{\\[FormalB](1),\\[FormalC](1)}},\"Euclidean\",3}}");
    check("InputForm(Select(CoordinateTransformData(), "
        + "MatchQ(#, {{\"Confocal\", _} -> \"Hyperspherical\", _, 4})&))", //
        "{{{\"Confocal\",{Subscript(\\[FormalAlpha],1),Subscript(\\[FormalAlpha],2),Subscript(\\[FormalAlpha],3),Subscript(\\[FormalAlpha],4)}}->\"Hyperspherical\",\"Euclidean\",4}}");
    // ordered by the source chart, the dimension and the target chart
    check("Take(Cases(CoordinateTransformData(), {\"Cartesian\" -> c_, _, n_} :> {c, n}), 8)", //
        "{{Cartesian,1},{{Bipolar,{a}},2},{Cartesian,2},{{Confocal,{α,β}},2},{{Elliptic,{a}},\n"
            + "2},{Polar,2},{PlanarParabolic,2},{{BipolarCylindrical,{a}},3}}");
    check("Last(CoordinateTransformData())", //
        "{Stereographic->Stereographic,{Sphere,{R}},3}");
  }

  @Test
  public void testCoordinateTransformDataCharts() {
    check("CoordinateTransformData({{\"BipolarCylindrical\", {k}} -> \"Cartesian\", "
        + "\"Euclidean\", 3}, \"Mapping\", {s, t, 7})", //
        "{(k*Sinh(t))/(-Cos(s)+Cosh(t)),(k*Sin(s))/(-Cos(s)+Cosh(t)),7}");
    check("CoordinateTransformData(\"Bipolar\" -> \"Cartesian\", \"Mapping\")[{Pi/2, Log(3)}]", //
        "{4/5*a,3/5*a}");
    check("CoordinateTransformData({\"Bipolar\", {2}} -> \"Cartesian\", \"Mapping\")", //
        "CoordinateTransformData({Bipolar,{2}}->Cartesian,Mapping)");
    check("CoordinateTransformData({{\"Bipolar\", {2}} -> \"Cartesian\", \"Euclidean\", 2}, "
        + "\"MappingJacobianDeterminant\", {s, t})", //
        "4/(-Cos(s)+Cosh(t))^2");
    check("CoordinateTransformData({\"Cylindrical\", 3} -> \"Cartesian\", \"Mapping\", {2, Pi, h})", //
        "{-2,0,h}");
    // the Cartesian chart exists in several dimensions
    check("CoordinateTransformData(\"Cartesian\" -> \"Cartesian\", \"Mapping\")", //
        "CoordinateTransformData(Cartesian->Cartesian,Mapping)");
    check("CoordinateTransformData({\"Cartesian\" -> \"Cartesian\", \"Euclidean\", 2}, "
        + "\"Mapping\", {p, q})", //
        "{p,q}");
    // the dimensions of the charts differ
    check("CoordinateTransformData(\"Polar\" -> \"Spherical\", \"Mapping\")", //
        "CoordinateTransformData(Polar->Spherical,Mapping)");
    // the dimension of the Cartesian chart is taken from the point
    check("CoordinateTransformData(\"Cartesian\" -> \"Cartesian\", \"Mapping\", {p, q, s, t})", //
        "{p,q,s,t}");
    // the short form doesn't take parameters, the dimension is needed for the Cartesian chart
    check("Check(CoordinateTransformData({\"Elliptic\", {k}} -> \"Cartesian\", \"Mapping\"), "
        + "failed)", //
        "failed");
    check("Check(CoordinateTransformData(\"Cartesian\" -> \"Cartesian\", \"Mapping\"), "
        + "failed)", //
        "failed");
  }

  @Test
  public void testCoordinateTransformDataToCartesian() {
    check("CoordinateTransformData({{\"Toroidal\", {2}} -> \"Cartesian\", \"Euclidean\", 3}, "
        + "\"Mapping\", {Log(2), Pi/2, 0})", //
        "{6/5,0,8/5}");
    check("CoordinateTransformData({{\"OblateSpheroidal\", {3}} -> \"Cartesian\", \"Euclidean\", 3}, "
        + "\"Mapping\", {0, Pi/2, Pi})", //
        "{-3,0,0}");
    check("CoordinateTransformData({{\"Conical\", {1, 2}} -> \"Cartesian\", \"Euclidean\", 3}, "
        + "\"Mapping\", {2, 3/2, 1/2})", //
        "{Sqrt(35)/4,Sqrt(5)/2,3/4}");
    // the conical coordinates keep the distance from the origin
    check("Simplify(Total(CoordinateTransformData({{\"Conical\", {1, 2}} -> \"Cartesian\", "
        + "\"Euclidean\", 3}, \"Mapping\", {r, m, n})^2))", //
        "r^2");
    check("CoordinateTransformData({{\"Confocal\", {5, 1}} -> \"Cartesian\", \"Euclidean\", 2}, "
        + "\"Mapping\", {-2, 3})", //
        "{Sqrt(6),1}");
    check("CoordinateTransformData({{\"ConfocalParaboloidal\", {4, 1}} -> \"Cartesian\", "
        + "\"Euclidean\", 3}, \"Mapping\", {0, 2, 5})", //
        "{2*Sqrt(2/3),2/Sqrt(3),-1}");
    // the squares of the confocal coordinates add up to the sum of the parameters and coordinates
    check("Together(Total(CoordinateTransformData({{\"Confocal\", {4, 3, 2, 1}} -> \"Cartesian\", "
        + "\"Euclidean\", 4}, \"Mapping\", {p, q, s, t})^2))", //
        "10+p+q+s+t");
    check("CoordinateTransformData({\"Hyperspherical\" -> \"Cartesian\", \"Euclidean\", 4}, "
        + "\"Mapping\", {6, Pi/2, Pi/3, Pi})", //
        "{0,3,-3*Sqrt(3),0}");
    check("CoordinateTransformData({{\"Toroidal\", {k}} -> \"Cartesian\", \"Euclidean\", 3}, "
        + "\"MappingJacobianDeterminant\", {s, t, 0})", //
        "(k^3*Sinh(s))/(-Cos(t)+Cosh(s))^3");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

}
