package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Trigonometric functions in degrees, window functions, special values, Solve/NSolve/Reduce fixes
 * and SameQ of nested machine numbers.
 */
public class Woxi1114BTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testDegreeTrigFunctions() {
    check("SinDegrees(-2*y)", //
        "-SinDegrees(2*y)");
    check("CosDegrees(-a)", //
        "CosDegrees(a)");
    check("{TanDegrees(60), SinDegrees(45), CosDegrees(150), CscDegrees(-90)}", //
        "{Sqrt(3),1/Sqrt(2),-Sqrt(3)/2,-1}");
    check("SinDegrees(7/2)", //
        "SinDegrees(7/2)");
    check("SecDegrees(1.5)", //
        "1.00034");
    check("CotDegrees(1.5+I)", //
        "26.43548+I*(-17.63529)");
    check("N(SinDegrees(7/2), 30)", //
        "0.0610485395348568720366701303848");
    check("SinDegrees(Around(30, 0.5))", //
        "Around(0.5,0.0075575)");
    check("TanDegrees(Interval({-30, 30}))", //
        "Interval({-1/Sqrt(3),1/Sqrt(3)})");
    check("D(CosDegrees(x), x)", //
        "-1/180*Pi*SinDegrees(x)");
    check("Refine(SinDegrees(360*k), Element(k, Integers))", //
        "0");
    check("Refine(CosDegrees(180*k), Element(k, Integers))", //
        "(-1)^k");
  }

  @Test
  public void testInverseDegreeTrigFunctions() {
    check("{ArcSinDegrees(1), ArcTanDegrees(-1), ArcCosDegrees(1/2), ArcTanDegrees(-1, 0)}", //
        "{90,-45,60,180}");
    check("ArcCosDegrees(-x)", //
        "ArcCosDegrees(-x)");
    check("ArcTanDegrees(x)", //
        "ArcTanDegrees(x)");
    check("ArcSinDegrees(0.25)", //
        "14.47751");
    check("ArcSecDegrees(Interval({1, 2}))", //
        "Interval({0,60})");
    check("ArcCscDegrees(Interval({2, 4}))", //
        "Interval({ArcSinDegrees(1/4),30})");
    check("D(ArcSinDegrees(x), x)", //
        "180/(Pi*Sqrt(1-x^2))");
    check("FullSimplify(ArcSinDegrees(Sqrt(1-y^2)), 0<=y<=1)", //
        "ArcCosDegrees(y)");
    check("FullSimplify(ArcCsc(1/Sqrt(1-y^2)), 0<=y<=1)", //
        "ArcCos(y)");
    check("FullSimplify(ArcSin(Sqrt(1-y^2)), -1<=y<=1)", //
        "ArcSin(Sqrt(1-y^2))");
  }

  @Test
  public void testWindowFunctions() {
    check("FunctionExpand(CauchyWindow(t, 2))", //
        "Piecewise({{1/(1+16*t^2),-1/2<=t<=1/2}},0)");
    check("FunctionExpand(WelchWindow(t))", //
        "Piecewise({{1-4*t^2,-1/2<=t<=1/2}},0)");
    check("{WelchWindow(1/4, 1), CosineWindow(1/4, 2), HannWindow(1/3, 1/4), ConnesWindow(1, 2)}", //
        "{3/4,1/2,-1/8,0}");
    check("KaiserWindow(0.25)", //
        "0.726926");
    check("HannWindow(t, b)", //
        "HannWindow(t,b)");
  }

  @Test
  public void testFunctionExpandSpecial() {
    check("FunctionExpand(Erfc(2*t))", //
        "1-Erf(2*t)");
    check("FunctionExpand(HeavisidePi(2*t))", //
        "-HeavisideTheta(-1/2+2*t)+HeavisideTheta(1/2+2*t)");
    check("FunctionExpand(DiracComb(a, b, c))", //
        "DiracComb(a)*DiracComb(b)*DiracComb(c)");
    check("{DiracComb(1/3), DiracComb(-2*t), DiracComb(2)}", //
        "{0,DiracComb(2*t),DiracComb(2)}");
  }

  @Test
  public void testSpecialValues() {
    check("HurwitzLerchPhi(-1, 3, b)", //
        "HurwitzZeta(3,b/2)/8-HurwitzZeta(3,1/2*(1+b))/8");
    check("{EllipticTheta(1, 2*Pi, 1/3), EllipticTheta(1, 0, w), EllipticTheta(1, 2*Pi, 3)}", //
        "{0,0,EllipticTheta(1,2*Pi,3)}");
    check("{InverseJacobiSN(z, Infinity), InverseJacobiNC(z, Infinity)}", //
        "{0,0}");
    check("InverseWeierstrassP(1, {0, 0})", //
        "InverseWeierstrassP(1,{0,0})");
    check("Gudermannian(Around(1, 0.1))", //
        "Around(0.865769,0.0648054)");
    check("Attributes(DifferenceRoot)", //
        "{NHoldAll,Protected,ReadProtected}");
  }

  @Test
  public void testCircleThrough() {
    check("CircleThrough({{2, 0}, {0, 2}, {-2, 0}})", //
        "Circle({0,0},2)");
    check("CircleThrough({{1, 1}, {3, 1}}, {2, 0})", //
        "Circle({2,0},Sqrt(2))");
    check("CircleThrough({{1, 1}, {3, 1}}, {2, 0}, Sqrt(2))", //
        "Circle({2,0},Sqrt(2))");
    check("CircleThrough({{1, 1}, {4, 1}}, {2, 0})", //
        "CircleThrough({{1,1},{4,1}},{2,0})");
  }

  @Test
  public void testRefineAndTrigFactor() {
    check("Refine(KroneckerDelta(a, b), a > 0 && b < 0)", //
        "0");
    check("Refine(KroneckerDelta(a, b), a > 0 && b > 0)", //
        "KroneckerDelta(a,b)");
    check("TrigFactor(Tan(a) - Tan(b))", //
        "Sec(a)*Sec(b)*Sin(a-b)");
    check("TrigFactor(Coth(u) - Coth(v))", //
        "-Csch(u)*Csch(v)*Sinh(u-v)");
    check("TrigFactor(Csc(u) - Csc(v))", //
        "-1/2*Cos(u/2+v/2)*Csc(u/2)*Csc(v/2)*Sec(u/2)*Sec(v/2)*Sin(u/2-v/2)");
  }

  @Test
  public void testComplexExpandReciprocal() {
    check("ComplexExpand(1/(2*t+I))", //
        "-I/(1+4*t^2)+(2*t)/(1+4*t^2)");
    check("ComplexExpand((a-I*b)^(-2))", //
        "a^2/(a^2+b^2)^2+(I*2*a*b)/(a^2+b^2)^2-b^2/(a^2+b^2)^2");
  }

  @Test
  public void testSameQNestedMachineNumbers() {
    check("{0.1+0.2, z} === {0.3, z}", //
        "True");
    check("f(0.7+0.1) === f(0.8)", //
        "True");
    check("{1.0, 2} === {1.0000000000000007, 2}", //
        "False");
    check("{0.1+0.2} =!= {0.3}", //
        "False");
  }

  @Test
  public void testSolveIntervalAndRegions() {
    check("Solve(Sin(x) + Cos(2*x) == 0.5 && 0 <= x <= 2, x)", //
        "{{x->0.942478}}");
    check("Solve(Cos(x) == 3.0 && 0 <= x <= 2, x)", //
        "{}");
    check("Solve(Element({x, y}, Circle({1, 1}, 1)) && x == 1, {x, y})", //
        "{{x->1,y->0},{x->1,y->2}}");
    check("Solve(Element({x, y}, HalfLine({1, 0}, {0, 1})) && y == 2, {x, y})", //
        "{{x->1,y->2}}");
    check("Solve(Element({x, y}, HalfLine({1, 0}, {0, 1})) && y == -2, {x, y})", //
        "{}");
    check("NSolve(Element({x, y}, Line({{0, 0}, {1, 1}, {2, 0}})) && y == 1/2, {x, y})", //
        "{{x->0.5,y->0.5},{x->1.5,y->0.5}}");
    check("NSolve(GammaRegularized(5., 2.*x) == 0.5, x)", //
        "{{x->2.33545}}");
    check("Length(NSolve({x^2 + (y - 0.21345678901234567)^2 == 0.51234567890123456, "
        + "(x - 0.61234567890123456)^2 + y^2 == 0.11234567890123456}, {x, y}))", //
        "2");
  }

  @Test
  public void testReduceAndSum() {
    check("Reduce({x + 1, y} == {3, 4}, {x, y})", //
        "x==2&&y==4");
    check("Reduce(3*y == c/d, y)", //
        "d!=0&&y==c/(3*d)");
    check("Sum(k^2, {k, -3, n})", //
        "14+1/6*n*(1+n)*(1+2*n)");
    check("Sum(c, {k, -1, n})", //
        "2*c+c*n");
  }
}
