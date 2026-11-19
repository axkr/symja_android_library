package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>NIntegrate(f, Element({x, y, z}, Sphere(c, r)))</code> over the surface of a sphere and
 * <code>NIntegrate(f, Element({x, y, z}, Ball(c, r)))</code> over a ball, by Lebedev rules.
 */
public class SphericalRegionNIntegrateTest extends ExprEvaluatorTestCase {

  @Test
  public void testSphereSurface() {
    // the area 4*Pi*r^2
    check("Abs(NIntegrate(1, Element({x, y, z}, Sphere())) - 4*Pi) < 10^-12", //
        "True");
    check("Abs(NIntegrate(1, Element({u, v, w}, Sphere({1, -2, 1/2}, 3/2))) - 9*Pi) < 10^-12", //
        "True");
    // a polynomial: the mean of x^2 over the unit sphere is 1/3
    check("Abs(NIntegrate(x^2 - 2*y*z, Element({x, y, z}, Sphere())) - 4*Pi/3) < 10^-12", //
        "True");
    // Exp(x) on the sphere of radius 2 about {1, 0, 0}: r^2*E*4*Pi*Sinh(2)/2
    check("Abs(NIntegrate(Exp(x), Element({x, y, z}, Sphere({1, 0, 0}, 2))) "
        + "- 8*Pi*E*Sinh(2)) < 10^-9", //
        "True");
    // 1/(5 + x) on the sphere of radius 3: 2*Pi*9*Integrate(1/(5 + 3*c), {c, -1, 1})
    check("Abs(NIntegrate(x*y*z + 1/(5 + x), Element({x, y, z}, Sphere({0, 0, 0}, 3)))"
        + " - 6*Pi*Log(4)) < 10^-10", //
        "True");
    // an odd integrand
    check("Abs(NIntegrate(x^3*y + z, Element({x, y, z}, Sphere()))) < 10^-12", //
        "True");
  }

  @Test
  public void testBall() {
    // the volume 4/3*Pi*r^3
    check("Abs(NIntegrate(1, Element({x, y, z}, Ball({1, 2, 3}, 2))) - 32*Pi/3) < 10^-10", //
        "True");
    // the moment of inertia of the unit ball
    check("Abs(NIntegrate(x^2 + y^2 + z^2, Element({x, y, z}, Ball())) - 4*Pi/5) < 10^-12", //
        "True");
    // the kink of Abs(x - 3/10) keeps the rules from converging: within their evaluation budget
    // they come close (NIntegrate: Numerical integration converging too slowly ...)
    check("Abs(NIntegrate(Abs(x - 3/10), Element({x, y, z}, Ball())) "
        + "- N(Integrate(Pi*Abs(x - 3/10)*(1 - x^2), {x, -1, 1}))) < 10^-2", //
        "True");
    // a radial Gaussian: 4*Pi*Integrate(r^2*Exp(-r^2), {r, 0, 3/2})
    check("Abs(NIntegrate(Exp(-x^2 - y^2 - z^2), Element({x, y, z}, Ball({0, 0, 0}, 3/2)))"
        + " - N(4*Pi*Integrate(r^2*Exp(-r^2), {r, 0, 3/2}))) < 10^-10", //
        "True");
  }

  @Test
  public void testComplexIntegrand() {
    // Integrate(Exp(I*x), sphere) == 4*Pi*Sin(1); the imaginary part vanishes
    check("Abs(NIntegrate(Exp(I*x), Element({x, y, z}, Sphere())) - 4*Pi*Sin(1)) < 10^-12", //
        "True");
    // 2*Pi*Integrate(Exp(2*I*c)*(1 - c^2), {c, -1, 1}) on the unit sphere, scaled by r^2 = 4
    check("Abs(NIntegrate(Exp(I*z)*(x^2 + y^2), Element({x, y, z}, Sphere({0, 0, 0}, 2))) "
        + "- N(4*2*Pi*Integrate(Exp(2*I*c)*4*(1 - c^2), {c, -1, 1}))) < 10^-9", //
        "True");
    check("Head(NIntegrate(I*Exp(x), Element({x, y, z}, Ball())))", //
        "Complex");
  }

  @Test
  public void testSingularityAtARuleNode() {
    // the points on the axes are nodes of the Lebedev rules: the iterated integral in spherical
    // coordinates has the singular point at an end of its range
    check("Abs(NIntegrate(1/Sqrt(1 - x), Element({x, y, z}, Sphere())) - 4*Sqrt(2)*Pi) < 10^-8", //
        "True");
    check("Abs(NIntegrate(1/Sqrt(1 + x), Element({x, y, z}, Sphere())) - 4*Sqrt(2)*Pi) < 10^-8", //
        "True");
    // the kink of Abs(x - 3/10): 2*Pi*(13^2 + 7^2)/200
    check("Abs(NIntegrate(Abs(x - 3/10), Element({x, y, z}, Sphere())) - 109*Pi/50) < 10^-8", //
        "True");
    // the integrable singularity at the center of the ball isn't a node
    check("Abs(NIntegrate(1/Sqrt(x^2 + y^2 + z^2), Element({x, y, z}, Ball())) - 2*Pi) < 10^-12", //
        "True");
  }

  @Test
  public void testWorkingPrecision() {
    // NIntegrate: Requested precision 30 is greater than MachinePrecision.
    check("Abs(NIntegrate(x^2, Element({x, y, z}, Sphere()), WorkingPrecision -> 30) - 4*Pi/3)"
        + " < 10^-12", //
        "True");
    // NIntegrate: Requested precision -3 is not a machine-sized real number.
    check("NIntegrate(x^2, Element({x, y, z}, Sphere()), WorkingPrecision -> -3)", //
        "NIntegrate(x^2,{x,y,z}∈Sphere(),WorkingPrecision->-3)");
  }

  @Test
  public void testInvalidRegions() {
    // NIntegrate: Invalid integration variable or limit(s) in {x,y,z}∈Sphere({0,0},1).
    check("NIntegrate(1, Element({x, y, z}, Sphere({0, 0}, 1)))", //
        "NIntegrate(1,{x,y,z}∈Sphere({0,0},1))");
    check("NIntegrate(1, Element({x, y, z}, Ball({0, 0, 0}, -1)))", //
        "NIntegrate(1,{x,y,z}∈Ball({0,0,0},-1))");
    check("NIntegrate(1, Element({x, y, z}, Ball({0, 0, 0}, r)))", //
        "NIntegrate(1,{x,y,z}∈Ball({0,0,0},r))");
    // NIntegrate: The integrand f(x) has evaluated to non-numerical values for all sampling points
    // in the region with boundaries {Sphere({0,0,0})}.
    check("NIntegrate(f(x), Element({x, y, z}, Sphere()))", //
        "NIntegrate(f(x),{x,y,z}∈Sphere())");
  }
}
