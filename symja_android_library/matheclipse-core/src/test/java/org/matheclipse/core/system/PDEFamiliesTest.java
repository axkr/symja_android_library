package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Initial value problems of evolution equations solved by a family of functions containing the
 * data, the two boundary problems on a half line, and Liouville's equation.
 */
public class PDEFamiliesTest extends ExprEvaluatorTestCase {

  @Test
  public void testAmplitudeFamily() {
    // u_t + u*u_x == 0 with u0 = 3*x: a(t)*3*x with a' == -3*a^2
    check("DSolve({D(u(x, t), t) + u(x, t)*D(u(x, t), x) == 0, u(x, 0) == 3*x}, {u(x, t)}, {x, t})", //
        "{{u(x,t)->(3*x)/(1+3*t)}}");
    // in three variables
    check("DSolve({D(w(x, y, t), t) == D(w(x, y, t), {x, 2}) + D(w(x, y, t), {y, 2}), "
        + "w(x, y, 0) == Sin(2*x)*Cos(y)}, {w(x, y, t)}, {x, y, t})", //
        "{{w(x,y,t)->(Cos(y)*Sin(2*x))/E^(5*t)}}");
    // the data at t == 1
    check("DSolve({D(w(x, y, t), t) == D(w(x, y, t), {x, 2}) + D(w(x, y, t), {y, 2}), "
        + "w(x, y, 1) == Cos(x)*Cos(2*y)}, {w(x, y, t)}, {x, y, t})", //
        "{{w(x,y,t)->E^(5-5*t)*Cos(x)*Cos(2*y)}}");
  }

  @Test
  public void testDataOnWholeLine() {
    // Sqrt(x^2) isn't x: a(t)*x with a' == -2*a^2 solves the problem for x > 0 only
    check("FreeQ(DSolve({D(u(x, t), t) + u(x, t)*D(u(x, t), x) == 0, u(x, 0) == 2*Sqrt(x^2)}, "
        + "{u(x, t)}, {x, t}), Rule)", //
        "True");
    check("DSolve({D(u(x, t), t) + 3*D(u(x, t), x) == -2*u(x, t), u(x, 0) == Sqrt(x^2)}, "
        + "{u(x, t)}, {x, t})", //
        "{{u(x,t)->Sqrt((-3*t+x)^2)/E^(2*t)}}");
    // nor is Sqrt(a^2) the parameter a
    check("DSolve({D(u(x, t), t) + a*D(u(x, t), x) == 0, u(x, 0) == Sqrt(a^2)*Sin(x)}, "
        + "{u(x, t)}, {x, t})", //
        "{{u(x,t)->-Sqrt(a^2)*Sin(a*t-x)}}");
  }

  @Test
  public void testSmallResidualIsNoZero() {
    // the data solves the equation up to -101*x/E^(50*x^2), which is 10^-30 at x == 6/5
    check("DSolve({D(u(x, t), t) + 2*D(u(x, t), x) == x*u(x, t), u(x, 0) == E^(-50*x^2)}, "
        + "{u(x, t)}, {x, t})", //
        "{{u(x,t)->E^(-201*t^2+201*t*x-50*x^2)}}");
  }

  @Test
  public void testExponentialPolynomialFamily() {
    check("DSolve({D(u(x, t), t) == 2*D(u(x, t), {x, 2}), u(x, 0) == E^(-3*x^2)}, {u(x, t)}, {x, t})", //
        "{{u(x,t)->1/(E^((3*x^2)/(1+24*t))*Sqrt(1+24*t))}}");
  }

  @Test
  public void testShiftFamily() {
    check("DSolve({D(u(x, t), t) + D(u(x, t), {x, 3}) + 6*u(x, t)*D(u(x, t), x) == 0, "
        + "u(x, 0) == 2*Sech(x)^2}, {u(x, t)}, {x, t})", //
        "{{u(x,t)->2*Sech(4*t-x)^2}}");
  }

  @Test
  public void testDecayingShiftFamily() {
    // drift with diffusion of a periodic profile: E^(k*t)*u0(x - c*t)
    check("DSolve({D(u(x, t), t) + 4*D(u(x, t), x) == D(u(x, t), {x, 2}), u(x, 0) == Sin(x)}, "
        + "{u(x, t)}, {x, t})", //
        "{{u(x,t)->-Sin(4*t-x)/E^t}}");
    // the data at t == 1
    check("DSolve({D(u(x, t), t) == 3*D(u(x, t), x) + 2*D(u(x, t), {x, 2}), u(x, 1) == Cos(2*x)}, "
        + "{u(x, t)}, {x, t})", //
        "{{u(x,t)->E^(8-8*t)*Cos(2*(-3+3*t+x))}}");
    // transport with damping
    check("DSolve({D(u(x, t), t) + 2*D(u(x, t), x) == -u(x, t), u(x, 0) == x^2}, {u(x, t)}, {x, t})", //
        "{{u(x,t)->(-2*t+x)^2/E^t}}");
  }

  @Test
  public void testHalfLine() {
    // the similarity variable y/Sqrt(t)
    check("DSolve({D(u(y, t), t) == 3*D(u(y, t), {y, 2}), u(y, 0) == 0, u(0, t) == 5}, {u(y, t)}, {y, t})", //
        "{{u(y,t)->5-5*Erf(y/(2*Sqrt(3)*Sqrt(t)))}}");
    // the periodic steady state of a time-harmonic boundary value
    check("DSolve({D(u(y, t), t) == D(u(y, t), {y, 2}), u(0, t) == 2*Cos(3*t)}, {u(y, t)}, {y, t})", //
        "{{u(y,t)->(2*Cos(3*t-Sqrt(3/2)*y))/E^(Sqrt(3/2)*y)}}");
  }

  @Test
  public void testLiouville() {
    check("DSolve(D(u(x, y), x, y) == 3*E^(2*u(x, y)), u(x, y), {x, y})", //
        "{{u(x,y)->Log((C(1)'[x]*C(2)'[y])/(3*(C(1)[x]+C(2)[y])^2))/2}}");
  }

  @Test
  public void testRefinePowerOfConstantNoLoop() {
    // Sqrt(a)*Sqrt(Pi) merged and split again forever
    check("Refine(Sqrt(a)*Sqrt(Pi)*b, a > 0)", //
        "Sqrt(a)*b*Sqrt(Pi)");
  }

  @Test
  public void testPowerTowerIntegral() {
    check("Integrate(x^(3*x), {x, 0, 1})", //
        "Sum(1/((-3)^(1-n)*n^n),{n,1,Infinity})");
    check("Integrate(x^(-x), {x, 0, 1})", //
        "Sum(n^(-n),{n,1,Infinity})");
    // the index of the sum isn't the symbol n of the user
    check("Block({n = 7}, Integrate(x^(2*x), {x, 0, 1}))", //
        "Sum(1/((-2)^(1-n)*n^n),{n,1,Infinity})");
  }

  @Test
  public void testPolyLogOfExponentialAtInfinity() {
    check("Abs(N(Integrate(x/(2*Cosh(x) - 1), {x, 0, Infinity})) - 1.1719536193447295) < 10^-12", //
        "True");
    // PolyLog(2, 3*E^x) == -Pi^2/6 - (x + Log(3) + I*Pi)^2/2 - PolyLog(2, E^(-x)/3)
    check("Limit(PolyLog(2, 3*E^x) + x^2/2, x -> Infinity)", //
        "DirectedInfinity((-I*Pi-Log(3))/Sqrt(Pi^2+Log(3)^2))");
    check("Limit(PolyLog(2, -E^x) + x^2/2, x -> Infinity)", //
        "-Pi^2/6");
    check("Limit((PolyLog(2, 2*E^x) + x^2/2)/x, x -> Infinity)", //
        "-I*Pi-Log(2)");
    // the polynomial summands together, the rest finite
    check("Limit(-E^(-x) - I*Pi*x - x*Log(3), x -> Infinity)", //
        "DirectedInfinity((-I*Pi-Log(3))/Sqrt(Pi^2+Log(3)^2))");
  }
}
