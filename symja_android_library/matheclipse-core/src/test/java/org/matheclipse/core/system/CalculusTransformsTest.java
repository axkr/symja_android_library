package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Tests for the tier 2.3 calculus functions: Fourier and Mellin transforms, ListZTransform, DiscreteConvolve, CaputoD, FractionalD, MaxLimit, MinLimit, SumConvergence, the asymptotic comparisons, ArcCurvature, FrenetSerretSystem, FindMaxValue and ParametricNDSolve. */
public class CalculusTransformsTest extends ExprEvaluatorTestCase {

  @Tag(TestTags.SLOW)
  @Test
  public void testFourierTransform() {
    check("FourierTransform(Exp(-t^2), t, w)", //
        "1/(Sqrt(2)*E^(w^2/4))");
    check("FourierTransform(Exp(-3*t^2 + t), t, w)", //
        "(E^((1+I*w)^2/12)*Sqrt(Pi/3))/Sqrt(2*Pi)");
    check("FourierTransform(Exp(-2*Abs(t)), t, w)", //
        "(2*Sqrt(2))/(Sqrt(Pi)*(4+w^2))");
    check("FourierTransform(HeavisideTheta(t)*Exp(-5*t), t, w)", //
        "1/(Sqrt(2*Pi)*(5-I*w))");
    check("FourierTransform(1/(4 + t^2), t, w)", //
        "Sqrt(Pi)/(2*Sqrt(2)*E^(2*Abs(w)))");
    check("FourierTransform(DiracDelta(t - 3), t, w)", //
        "E^(I*3*w)/Sqrt(2*Pi)");
    check("FourierTransform(1, t, w)", //
        "Sqrt(2*Pi)*DiracDelta(w)");
    check("FourierTransform(Cos(2*t), t, w)", //
        "(Pi*(DiracDelta(-2+w)+DiracDelta(2+w)))/Sqrt(2*Pi)");
    check("FourierTransform(Sin(3*t)/t, t, w)", //
        "(Sqrt(Pi)*(Sign(3-w)+Sign(3+w)))/(2*Sqrt(2))");
    check("FourierTransform(t*Exp(-t^2), t, w)", //
        "(I*1/2*w)/(Sqrt(2)*E^(w^2/4))");
    check("FourierTransform(Sign(t), t, w)", //
        "(I*Sqrt(2))/(Sqrt(Pi)*w)");
    check("FourierTransform(HeavisideTheta(t), t, w)", //
        "(I/w+Pi*DiracDelta(w))/Sqrt(2*Pi)");
    check("FourierTransform(Exp(-t^2), t, w, FourierParameters -> {1, -1})", //
        "Sqrt(Pi)/E^(w^2/4)");
    check("FourierTransform(Exp(-Abs(t))*Cos(t), t, w)", //
        "(2*(2+w^2))/(Sqrt(2*Pi)*(4+w^4))");
  }

  @Test
  public void testInverseFourierTransform() {
    check("InverseFourierTransform(Exp(-w^2/4)/Sqrt(2), w, t)", //
        "E^(-t^2)");
    check("InverseFourierTransform(2/(1 + w^2), w, t, FourierParameters -> {1, 1})", //
        "E^(-Abs(t))");
  }

  @Test
  public void testInverseFourierCosTransform() {
    check("InverseFourierCosTransform(Sqrt(2/Pi)/(1 + w^2), w, t)", //
        "E^(-t)");
  }

  @Test
  public void testInverseFourierSinTransform() {
    check("InverseFourierSinTransform(Sqrt(2/Pi)*w/(1 + w^2), w, t)", //
        "E^(-t)");
  }

  @Test
  public void testMellinTransform() {
    check("MellinTransform(Exp(-x), x, s)", //
        "Gamma(s)");
    check("MellinTransform(Exp(-3*x), x, s)", //
        "Gamma(s)/3^s");
    check("MellinTransform(x^2*Exp(-x), x, s)", //
        "Gamma(2+s)");
    check("MellinTransform(Exp(-x^2), x, s)", //
        "Gamma(s/2)/2");
    check("MellinTransform(1/(1 + x), x, s)", //
        "Pi*Csc(Pi*s)");
    check("MellinTransform(1/(1 + x)^3, x, s)", //
        "1/2*Gamma(3-s)*Gamma(s)");
    check("MellinTransform(Sin(x), x, s)", //
        "Gamma(s)*Sin(1/2*Pi*s)");
    check("MellinTransform(Cos(2*x), x, s)", //
        "(Cos(1/2*Pi*s)*Gamma(s))/2^s");
  }

  @Test
  public void testInverseMellinTransform() {
    check("InverseMellinTransform(Gamma(s), s, x)", //
        "E^(-x)");
    check("InverseMellinTransform(Gamma(s)/2^s, s, x)", //
        "E^(-2*x)");
    check("InverseMellinTransform(Gamma(s + 2), s, x)", //
        "x^2/E^x");
    check("InverseMellinTransform(Gamma(s/2)/2, s, x)", //
        "E^(-x^2)");
    check("InverseMellinTransform(Pi*Csc(Pi*s), s, x)", //
        "1/(1+x)");
    check("InverseMellinTransform(Gamma(s)*Sin(Pi*s/2), s, x)", //
        "Sin(x)");
    check("InverseMellinTransform(Gamma(s)*Gamma(3 - s)/2, s, x)", //
        "1/(1+x)^3");
  }

  @Test
  public void testListZTransform() {
    check("ListZTransform({1, 2, 3}, z)", //
        "1+3/z^2+2/z");
    check("ListZTransform({a, b}, z, 2)", //
        "b/z^3+a/z^2");
    check("ListZTransform({}, z)", //
        "{}");
  }

  @Test
  public void testDiscreteConvolve() {
    check("DiscreteConvolve(UnitStep(n), UnitStep(n), n, m)", //
        "Piecewise({{1+m,m>=0}},0)");
    check("DiscreteConvolve(DiscreteDelta(n - 2), f(n), n, m)", //
        "f(-2+m)");
    check("DiscreteConvolve(UnitStep(n)*2^(-n), UnitStep(n), n, m)", //
        "Piecewise({{2-1/2^m,m>=0}},0)");
  }

  @Test
  public void testCaputoD() {
    check("CaputoD(x^2, {x, 1/2})", //
        "8/3*x^(3/2)/Sqrt(Pi)");
    check("CaputoD(5, {x, 1/2})", //
        "0");
    check("CaputoD(x, {x, 3/2})", //
        "0");
    check("CaputoD(x^3 + 2*x, {x, 1/2})", //
        "(4*Sqrt(x))/Sqrt(Pi)+16/5*x^(5/2)/Sqrt(Pi)");
    check("CaputoD(x^2, {x, 2})", //
        "2");
    check("CaputoD(Exp(2*x), {x, 1/2})", //
        "Sqrt(2)*E^(2*x)*(1-Erfc(Sqrt(2)*Sqrt(x)))");
  }

  @Test
  public void testFractionalD() {
    check("FractionalD(x^2, {x, 1/2})", //
        "8/3*x^(3/2)/Sqrt(Pi)");
    check("FractionalD(1, {x, 1/2})", //
        "1/(Sqrt(Pi)*Sqrt(x))");
    check("FractionalD(x, {x, -1})", //
        "x^2/2");
  }

  @Test
  public void testMaxLimit() {
    check("MaxLimit(Sin(1/x), x -> 0)", //
        "1");
    check("MaxLimit(Sign(x), x -> 0)", //
        "1");
    check("MaxLimit(2 + 3*Cos(x), x -> Infinity)", //
        "5");
    check("MaxLimit((x^2 - 1)/(x - 1), x -> 1)", //
        "2");
    check("MaxLimit(Abs(Sin(x)), x -> Infinity)", //
        "1");
  }

  @Test
  public void testMinLimit() {
    check("MinLimit(Sin(1/x), x -> 0)", //
        "-1");
    check("MinLimit(Sign(x), x -> 0)", //
        "-1");
    check("MinLimit(1/x + Sin(x)*x/(x + 1), x -> Infinity)", //
        "-1");
    check("MinLimit(Abs(2*Cos(x)), x -> Infinity)", //
        "0");
  }

  @Test
  public void testSumConvergence() {
    check("SumConvergence(1/n^2, n)", //
        "True");
    check("SumConvergence(1/n, n)", //
        "False");
    check("SumConvergence((-1)^n/n, n)", //
        "True");
    check("SumConvergence((n + 1)/(n^3 + 2), n)", //
        "True");
    check("SumConvergence(2^n/n!, n)", //
        "True");
    check("SumConvergence(3^n/n^2, n)", //
        "False");
    check("SumConvergence(x^n, n)", //
        "Abs(x)<1");
    check("SumConvergence(1/n^p, n)", //
        "Re(p)>1");
    check("SumConvergence(n/(n + 1), n)", //
        "False");
  }

  @Test
  public void testAsymptoticLess() {
    check("AsymptoticLess(x, x^2, x -> Infinity)", //
        "True");
    check("AsymptoticLess(x^2, x, x -> Infinity)", //
        "False");
    check("AsymptoticLess(Sin(x), x, x -> Infinity)", //
        "True");
  }

  @Test
  public void testAsymptoticLessEqual() {
    check("AsymptoticLessEqual(Sin(x), 1, x -> Infinity)", //
        "True");
    check("AsymptoticLessEqual(3*x^2 + x, x^2, x -> Infinity)", //
        "True");
  }

  @Test
  public void testAsymptoticGreater() {
    check("AsymptoticGreater(Exp(x), x^10, x -> Infinity)", //
        "True");
  }

  @Test
  public void testAsymptoticGreaterEqual() {
    check("AsymptoticGreaterEqual(x, x^2, x -> Infinity)", //
        "False");
  }

  @Test
  public void testAsymptoticEqual() {
    check("AsymptoticEqual(3*x^2 + x, x^2, x -> Infinity)", //
        "True");
  }

  @Test
  public void testAsymptoticEquivalent() {
    check("AsymptoticEquivalent(x^2 + x, x^2, x -> Infinity)", //
        "True");
    check("AsymptoticEquivalent(Sin(x), x, x -> 0)", //
        "True");
    check("AsymptoticEquivalent(2*x, x, x -> 0)", //
        "False");
  }

  @Test
  public void testArcCurvature() {
    check("ArcCurvature({Cos(t), Sin(t)}, t)", //
        "1");
    check("ArcCurvature({t, t^2}, t)", //
        "2/(1+4*t^2)^(3/2)");
    check("ArcCurvature({3*Cos(t), 3*Sin(t), 4*t}, t)", //
        "3/25");
    check("ArcCurvature(t^3, t)", //
        "(6*Abs(t))/(1+9*t^4)^(3/2)");
  }

  @Test
  public void testFrenetSerretSystem() {
    check("FrenetSerretSystem({Cos(t), Sin(t)}, t)", //
        "{{1},{{-Sin(t),Cos(t)},{-Cos(t),-Sin(t)}}}");
    check("FrenetSerretSystem({Cos(t), Sin(t), t}, t)", //
        "{{1/2,1/2},{{-Sin(t)/Sqrt(2),Cos(t)/Sqrt(2),1/Sqrt(2)},{-Cos(t),-Sin(t),0},{Sin(t)/Sqrt(\n2),-Cos(t)/Sqrt(2),1/Sqrt(2)}}}");
  }

  @Test
  public void testFindMaxValue() {
    check("FindMaxValue(-(x - 2)^2 + 3, {x, 1})", //
        "3.0");
    check("FindMaxValue(Sin(x)*Exp(-x/5), {x, 1})", //
        "0.74506");
  }

  @Test
  public void testParametricNDSolve() {
    check("ParametricNDSolve({y'(x) == a*y(x), y(0) == 1}, y, {x, 0, 1}, {a})", //
        "{y->ParametricFunction(<>)}");
  }

  @Test
  public void testParametricNDSolveHead() {
    check("Head(y /. First(ParametricNDSolve({y'(x) == a*y(x), y(0) == 1}, y, {x, 0, 1}, {a})))", //
        "ParametricFunction");
  }

  @Test
  public void testMaxStepSize() {
    check("NDSolveValue({y'(x) == -y(x), y(0) == 1}, y(1), {x, 0, 1}, MaxStepSize -> 0.01)", //
        "0.367879");
  }

  @Test
  public void testSolveOptions() {
    check("Options(Solve)", //
        "{Assumptions->Automatic,GenerateConditions->True,InverseFunctions->Automatic,MaxExtraConditions->\n0,MaxRoots->1000,Method->Automatic,Modulus->0,VerifySolutions->Automatic}");
  }

  @Test
  public void testVerifySolutions() {
    check("Solve(Sqrt(x + 2) == x, x, VerifySolutions -> True)", //
        "{{x->2}}");
  }
}
