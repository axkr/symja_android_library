package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Calculus: DiscreteAsymptotic, nested iterators, piecewise derivatives and integrals. */
public class DiscreteAsymptoticCalculusTest extends ExprEvaluatorTestCase {

  @Test
  public void testDiscreteAsymptoticRational() {
    check("DiscreteAsymptotic(2*k^5-7*k^2+1, k->Infinity)", //
        "2*k^5");
    check("DiscreteAsymptotic((7*k^2-1)/(2*k^4+k), k->Infinity)", //
        "7/(2*k^2)");
    check("DiscreteAsymptotic((k^3+1)/(5*k^3-2), k->Infinity)", //
        "1/5");
    check("DiscreteAsymptotic(Sqrt(k^2+3)-k, k->Infinity)", //
        "3/(2*k)");
  }

  @Test
  public void testDiscreteAsymptoticExponential() {
    check("DiscreteAsymptotic(5^k+k^9, k->Infinity)", //
        "5^k");
    check("DiscreteAsymptotic(9^k/(3+3^k), k->Infinity)", //
        "3^k");
    check("DiscreteAsymptotic(Cosh(3*k), k->Infinity)", //
        "E^(3*k)/2");
    check("DiscreteAsymptotic(Sinh(2*k)+k^3, k->Infinity)", //
        "Sinh(2*k)");
    check("DiscreteAsymptotic(Coth(k), k->Infinity)", //
        "1");
    check("DiscreteAsymptotic(LucasL(k), k->Infinity)", //
        "GoldenRatio^k");
  }

  @Test
  public void testDiscreteAsymptoticSpecial() {
    check("DiscreteAsymptotic(ArcSinh(5*k), k->Infinity)", //
        "Log(k)");
    check("DiscreteAsymptotic(HarmonicNumber(k), k->Infinity)", //
        "Log(k)");
    check("DiscreteAsymptotic(Binomial(3*k,k), k->Infinity)", //
        "3^(1/2+3*k)/(2^(1+2*k)*Sqrt(k)*Sqrt(Pi))");
    check("DiscreteAsymptotic(Binomial(k,4), k->Infinity)", //
        "k^4/24");
    check("DiscreteAsymptotic(Gamma(k+2), k->Infinity)", //
        "(k^(3/2+k)*Sqrt(2*Pi))/E^k");
    check("DiscreteAsymptotic((k+1)!, {k, Infinity, 2})", //
        "((1+13/(12*k))*k^(3/2+k)*Sqrt(2*Pi))/E^k");
    check("DiscreteAsymptotic(Log(Gamma(k)), k->Infinity, SeriesTermGoal->2)", //
        "Log(Sqrt(2*Pi)/(12*E^k*k^(3/2-k))+Sqrt(2*Pi)/(E^k*k^(1/2-k)))");
    check("DiscreteAsymptotic(g(k), k->Infinity)", //
        "g(k)");
    check("DiscreteAsymptotic(Cos(k), k->Infinity)", //
        "DiscreteAsymptotic(Cos(k),k->Infinity)");
    check("DiscreteAsymptotic(k!, k->Infinity, SeriesTermGoal->-1)", //
        "DiscreteAsymptotic(k!,k->Infinity,SeriesTermGoal->-1)");
  }

  @Test
  public void testDiscreteAsymptoticSeriesCoefficient() {
    check("DiscreteAsymptotic(SeriesCoefficient(1/(1-3*z), {z, 0, k}), k->Infinity)", //
        "3^k");
    check("DiscreteAsymptotic(Inactive(SeriesCoefficient)[1/((1-2*z)*(1-z)), {z, 0, k}], k->Infinity)", //
        "2^(1+k)");
  }

  @Test
  public void testSeriesHighDegreeDenominator() {
    // the Taylor coefficients of a polynomial of degree 21 or more were divided by overflowed
    // factorials
    check("Series(1/((1-x^9)*(1-x^22)), {x, 0, 31})", //
        "1+x^9+x^18+x^22+x^27+x^31+O(x)^32");
    check("SeriesCoefficient(1/((1-x^10)*(1-x^25)), {x, 0, 50})", //
        "2");
    check("Normal(Series(1/((1-x^10)*(1-x^25)), {x, 0, 50}))", //
        "1+x^10+x^20+x^25+x^30+x^35+x^40+x^45+2*x^50");
  }

  @Test
  public void testNestedIteratorVariable() {
    check("Table(Table(j, {j, 2}), {j, 3})", //
        "{{1,2},{1,2},{1,2}}");
    check("Sum(Sum(j^2, {j, 1, 2}), {j, 1, 4})", //
        "20");
    check("Sum(Sum(j, {j, 1, j}), {j, 1, 4})", //
        "20");
    check("Product(Sum(j, {j, 1, 2}), {j, 1, 3})", //
        "27");
    check("Sum(Table(g(j), {j, 1, 3})[[j]], {j, 1, 3})", //
        "g(1)+g(2)+g(3)");
  }

  @Test
  public void testDerivativeRegularized() {
    check("D(GammaRegularized(b, y), y)", //
        "-1/(E^y*y^(1-b)*Gamma(b))");
    check("D(GammaRegularized(3, 0, 2*y), y)", //
        "(4*y^2)/E^(2*y)");
    check("D(BetaRegularized(y, 2, 5), y)", //
        "2*(1+2*(1-y)+3*(1-y)^2+4*(1-y)^3+5*(1-y)^4)*y+(-2-6*(1-y)-12*(1-y)^2-20*(1-y)^3)*y^\n2");
    check("D(BetaRegularized(y, p, q), y)", //
        "1/((1-y)^(1-q)*y^(1-p)*Beta(p,q))");
    check("D(BetaRegularized(y, 1, p, q), y)", //
        "-1/((1-y)^(1-q)*y^(1-p)*Beta(p,q))");
  }

  @Test
  public void testDerivativePiecewise() {
    check("D(Piecewise({{x^3, -1 <= x <= 2}}, 5*x), x)", //
        "Piecewise({{3*x^2,x>-1&&x<2},{5,x<-1||x>2}},Indeterminate)");
    check("D(Piecewise({{x, x > 3}, {x^2, x > 1}}, 2), x)", //
        "Piecewise({{1,x>3},{2*x,x>1&&x<3},{0,x<1}},Indeterminate)");
    check("D(Max(2*x, 3 - x), x)", //
        "Piecewise({{2,x>1},{-1,x<1}},Indeterminate)");
    check("D(Min(x^2, 4), x)", //
        "Piecewise({{2*x,x>-2&&x<2},{0,x<-2||x>2}},Indeterminate)");
    check("D(If(s > 2, 4, 7), y)", //
        "0");
  }

  @Test
  public void testFindMinValue() {
    check("FindMinValue(x^4 - 3*x^2 + x, {x, 1})", //
        "-1.07023");
    check("FindMinValue(Sqrt(x-5)*Sqrt(1/(x-5)) + (x-2)^2, {x, 1.})", //
        "-1.0");
  }

  @Test
  public void testCurlMatrix() {
    check("Curl({{0, x*y, z}, {-x*y, 0, y^2}, {-z, -y^2, 0}}, {x, y, z})", //
        "0");
    check("Curl({{x^2, y, 0}, {z, 0, x}, {y, z, x*y}}, {x, y, z})", //
        "1/2");
  }

  @Test
  public void testLimitNested() {
    check("Limit(x^2+y, {x -> 3, y -> -1})", //
        "8");
    check("Limit((x-y)/(x+y), {y -> 0, x -> 0})", //
        "-1");
    check("Limit((x-y)/(x+y), {x -> 0, y -> 0})", //
        "1");
  }

  @Test
  public void testDifferenceDeltaSymbolicOrder() {
    check("DifferenceDelta(3^k, {k, m})", //
        "2^m*3^k");
    check("DifferenceDelta(c*7^(2*k), {k, m, s})", //
        "7^(2*k)*(-1+7^(2*s))^m*c");
  }

  @Test
  public void testRecurrenceTableJoint() {
    check("RecurrenceTable({a(k+1) - b(k+1) == a(k) + 1, a(k) + b(k) == 3, a(0) == 2, b(0) == 1}, {a, b}, {k, 0, 3})", //
        "{{2,1},{3,0},{7/2,-1/2},{15/4,-3/4}}");
    check("RecurrenceTable({a(k+1) + b(k+1) == 2*a(k), a(k+1) - b(k+1) == b(k), a(0) == 1, b(0) == 1}, {a, b}, {k, 0, 3})", //
        "{{1,1},{3/2,1/2},{7/4,5/4},{19/8,9/8}}");
  }

  @Test
  public void testIntegratePiecewiseShift() {
    check("Integrate(Piecewise({{3, c + 1 <= x <= c + 4}}, 0), {x, -Infinity, Infinity})", //
        "9");
    check("Integrate(Piecewise({{x - c, c <= x <= c + 2}}, 0), {x, -Infinity, Infinity})", //
        "2");
    // the antiderivative of the pieces jumps at the conditions
    check("Integrate(Piecewise({{1, x < 3}}, 0), {x, 0, c})", //
        "Integrate(Piecewise({{1,x<3}},0),{x,0,c})");
  }

  @Test
  public void testIntegrateInterpolationExact() {
    check("ip1 = Interpolation({{0, 1}, {1, 3}, {2, 2}, {3, 4}}); Integrate(ip1(x), {x, 0, 3})", //
        "15/2");
    check("ip2 = Interpolation({{0, 1}, {1, 3}, {2, 2}}, InterpolationOrder -> 1); Integrate(ip2(x), {x, 0, 2})", //
        "9/2");
  }

  @Test
  public void testWhenEventUnitVector() {
    check("s = NDSolve({y'(t) == -2*y(t), y(0) == {1.0}, WhenEvent(First(y(t)) == 0.25, {tm = t, \"StopIntegration\"})}, y, {t, 0, 2}); Round(tm, 0.001)", //
        "0.693");
  }
}
