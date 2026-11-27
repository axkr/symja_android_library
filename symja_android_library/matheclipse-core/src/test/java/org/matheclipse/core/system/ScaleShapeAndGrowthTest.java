package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Statistics of machine data near the ends of the double range, regions with several components or
 * holes, measures of reversed radii, grouping by <code>SameQ</code>, limits of sequences which grow
 * exponentially, and a few exact decisions.
 */
public class ScaleShapeAndGrowthTest extends ExprEvaluatorTestCase {

  @Test
  public void testStatisticsOfTinyAndHugeData() {
    // squares of the deviations underflow: the data is scaled by a power of two
    check("StandardDeviation({3.0*10^-180, 6.0*10^-180, 9.0*10^-180})*10^180", //
        "3.0");
    check("RootMeanSquare({6.0*10^-190, 8.0*10^-190})*10^190", //
        "7.07107");
    check("Kurtosis({2,5,6,11}*1.0*10^-40)", //
        "2.0");
    check("Kurtosis({7.0, 7.0, 7.0})", //
        "Indeterminate");
    // fourth powers of 10^-85 underflow
    check("Kurtosis({2,5,6,11}*1.0*10^-85)", //
        "2.0");
    // exact and machine numbers: the square of the largest value overflows
    check("Kurtosis({1, 3.0*10^170, 2})", //
        "1.5");
    check("Skewness({2,6,8}*1.0*10^-120)", //
        "-0.381802");
    check("Mean({{1,2},{5}})", //
        "Mean({{1,2},{5}})");
    check("CentralMoment({{1,2},{5}},2)", //
        "CentralMoment({{1,2},{5}},2)");
    check("Fourier({u,3})", //
        "Fourier({u,3})");
  }

  @Test
  public void testWeightsBeyondTheMachineRange() {
    check("Union(RandomChoice({10^400, 1} -> {u, v}, 10))", //
        "{u}");
    check("Union(RandomChoice({1.5*10^308, 1.5*10^308} -> {u, v}, 80))", //
        "{u,v}");
  }

  @Test
  public void testUniformBox() {
    check("Mean(UniformDistribution({{1,3},{2,8}}))", //
        "{2,5}");
    check("Variance(UniformDistribution({{1,3},{2,8}}))", //
        "{1/3,3}");
    check("PDF(UniformDistribution({{1,3},{2,8}}), {2,4})", //
        "1/12");
    check("CDF(UniformDistribution({{1,3},{2,8}}), {2,5})", //
        "1/4");
    // a lower bound which isn't less than the upper bound
    check("{PDF(UniformDistribution({5,2}), x), CDF(UniformDistribution({5,2}), x)}", //
        "{PDF(UniformDistribution({5,2}),x),CDF(UniformDistribution({5,2}),x)}");
    check("Mean(UniformDistribution({5,2}))", //
        "Mean(UniformDistribution({5,2}))");
    check("Variance(UniformDistribution({{0,4},{6,6}}))", //
        "Variance(UniformDistribution({{0,4},{6,6}}))");
    check("PDF(UniformDistribution({{0,4},{6,6}}), {1,6})", //
        "PDF(UniformDistribution({{0,4},{6,6}}),{1,6})");
  }

  @Test
  public void testRegionsWithComponentsAndHoles() {
    check("Perimeter(Polygon({{{0,0},{4,0},{0,3}},{{5,5},{6,5},{5,6}}}))", //
        "14+Sqrt(2)");
    check("Perimeter(Polygon({{0,0},{6,0},{6,6},{0,6}} -> {{{2,2},{4,2},{4,4},{2,4}}}))", //
        "32");
    check("RegionCentroid(Polygon({{{0,0},{4,0},{0,3}},{{5,5},{6,5},{5,6}}}))", //
        "{64/39,4/3}");
    check("RegionCentroid(Polygon({{0,0},{6,0},{6,6},{0,6}} -> {{{1,1},{2,1},{2,2},{1,2}}}))", //
        "{213/70,213/70}");
    check("Area(Annulus({0,0},{4,2}))", //
        "12*Pi");
    // a negative radius describes no region
    check("ArcLength(Circle({0,0},-5))", //
        "ArcLength(Circle({0,0},-5))");
    check("Area(Disk({0,0},{-2,5}))", //
        "Area(Disk({0,0},{-2,5}))");
    check("Area(Annulus({0,0},{-1,4}))", //
        "Area(Annulus({0,0},{-1,4}))");
    check("RegionCentroid(Disk({0,0},-3,{0,Pi/2}))", //
        "RegionCentroid(Disk({0,0},-3,{0,Pi/2}))");
    check("RegionMember(Disk({0,0},-2), {1/2,1/2})", //
        "RegionMember(Disk({0,0},-2),{1/2,1/2})");
    check("{RegionQ(Disk({0,0},-2)), RegionQ(Annulus({0,0},{1,2}))}", //
        "{False,True}");
  }

  @Test
  public void testGroupingBySameQ() {
    check("Gather({0.1+0.7, 0.8, 2})", //
        "{{0.8,0.8},{2}}");
    check("Length /@ GatherBy({0.1+0.7, 0.8, 5.0}, Identity)", //
        "{2,1}");
    check("SplitBy({0.1+0.7, 0.8}, Identity)", //
        "{{0.8,0.8}}");
    check("Length /@ Gather({(0.1+0.7)+I, 0.8+I, 3.0})", //
        "{2,1}");
    // lists of machine numbers which differ in their last bits
    check("Tally({{0.2+0.1, 2}, {0.3, 2}, {0.3, 3}})", //
        "{{{0.3,2},2},{{0.3,3},1}}");
    check("Length /@ Gather({{0.2+0.1}, {0.3}})", //
        "{2}");
    // the zeros of both signs
    check("Length /@ Gather({-0.0, 0.0, 0.6, 0.2+0.4, 0.7})", //
        "{2,2,1}");
    // many distinct machine numbers are grouped without comparing all pairs
    check("Length(GatherBy(N(Range(15000)/7), Identity))", //
        "15000");
  }

  @Test
  public void testExactDecisions() {
    check("MatrixPower({{1,3},{0,1}}, 2*10^6)", //
        "{{1,6000000},\n" //
            + " {0,1}}");
    check("D(ConditionalExpression(Sin(x),x<1),x)", //
        "ConditionalExpression(Cos(x),x<1)");
    // a sparse array is an atom for the pattern functions
    check("Count(SparseArray({0,5,5,0}),5)", //
        "0");
    check("{FirstPosition(SparseArray({0,6}),6), SelectFirst(SparseArray({0,0,3}), #>0&)}", //
        "{Missing(NotFound),3}");
    check("{FirstPosition(SparseArray({0,6}),_SparseArray), "
        + "FirstPosition(SparseArray({0,6}),_SparseArray,u,{1})}", //
        "{{},u}");
    check("Integrate(Boole(x<3),{x,0,7})", //
        "3");
    check("ConstantArray(u,{})", //
        "u");
    check("Join()", //
        "{}");
    check("Solve(x^4+9*y^2==0,{x,y},Reals)", //
        "{{x->0,y->0}}");
    check("Solve((x+3)^2+(y-1)^2==0,{x,y},Reals)", //
        "{{x->-3,y->1}}");
    // complex bases: 2*x^2-8*y^2 == 0
    check("Solve((x+2*I*y)^2+(x-2*I*y)^2==0,{x,y},Reals)", //
        "{{y->-x/2},{y->x/2}}");
    check("Dt(x^4, h(x))", //
        "4*x^3*Dt(x,h(x))");
    check("PolynomialMod({x^2, x^3}, x - 3)", //
        "{9,27}");
    check("{Numerator(1/3+I/5), Denominator(1/3+I/5)}", //
        "{5+I*3,15}");
    check("Integrate(1/x^2, {x, c, 5})", //
        "ConditionalExpression(-1/5+1/c,0<c)");
    // the pole at 0 lies between the limits for d < 0
    check("Integrate(1/t^3, {t, 1, d})", //
        "ConditionalExpression(1/2-1/(2*d^2),0<d)");
    // the pole conditions hold for every d
    check("Integrate(1/(t^2-1), {t, 2, Sin(d)+3})", //
        "-I*1/2*Pi+Log(3)/2+Log(-2-Sin(d))/2-Log(4+Sin(d))/2");
    // one condition for the poles -3 and 3; two for -5 and 2
    check("Integrate(1/(t^2-9), {t, 4, d})", //
        "ConditionalExpression(-I*1/6*Pi+Log(7)/6+Log(3-d)/6-Log(3+d)/6,d>3)");
    check("Integrate(1/((t-2)*(t+5)), {t, 0, d})", //
        "ConditionalExpression(-Log(2)/7+Log(5)/7+Log(2-d)/7-Log(5+d)/7,2>d&&-5<d)");
    check("Binomial(Infinity, Infinity)", //
        "Binomial(Infinity,Infinity)");
    check("{E^(3*Log(2)), E^(2*ArcCsch(2))}", //
        "{8,GoldenRatio^2}");
    check("Surd(u,7)^7", //
        "u");
    check("PolyLog(1,-1)", //
        "-Log(2)");
    check("LerchPhi(-1,1,3)", //
        "-1/2+Log(2)");
    // the terms (-1)^k/|a+k|
    check("{LerchPhi(-1,1,7/2), LerchPhi(-1,1,-3/2)}", //
        "{26/15-Pi/2,-4/3+Pi/2}");
    // Gauss' digamma theorem for the denominator 6
    check("LerchPhi(-1,1,5/3)", //
        "3/2-Pi/Sqrt(3)+Log(2)");
    // c == a+b: the series diverges logarithmically
    check("{Hypergeometric2F1(1/2, 3/2, 2, 1.0), Hypergeometric0F1(2, 1.0*10^280)}", //
        "{Infinity,Overflow()}");
    check("Zeta(1, 2/9)", //
        "ComplexInfinity");
    // an accuracy zero has no significant digits
    check("Precision({0``20, 1.`20})", //
        "0");
    check("D(EulerE(n,x),x)", //
        "n*EulerE(-1+n,x)");
    check("HermiteH(2^35+1, 0)", //
        "0");
  }

  @Test
  public void testGrowthOfSequences() {
    check("Limit(Fibonacci(n)/GoldenRatio^n, n->Infinity)", //
        "1/Sqrt(5)");
    check("Limit(Binomial(2*n, n)^(1/n), n->Infinity)", //
        "4");
    check("Limit(Fibonacci(n)^(1/n), n->Infinity)", //
        "GoldenRatio");
    check("Limit((Gamma(2*n+1)/(Gamma(n+1)*Gamma(n+3)))^(1/n), n->Infinity)", //
        "4");
    // the leading asymptotic forms of the Airy functions
    check("Limit(AiryBi(n)/E^(2*n), n->Infinity)", //
        "Infinity");
    check("Limit(AiryAi(n)*E^(3*n), n->Infinity)", //
        "0");
    check("Limit(AiryBiPrime(n)/(AiryBi(n)*Sqrt(n)), n->Infinity)", //
        "1");
    check("Limit(Log(AiryAi(n))/n^(3/2), n->Infinity)", //
        "-2/3");
    // Subfactorial(n) ~ n!/E
    check("Limit(Subfactorial(n)^(1/n)/n, n->Infinity)", //
        "1/E");
    // a large b keeps the value near 1
    check("Hypergeometric0F1(1.*^300, 1.*^20)", //
        "1.0");
    // products of growing factors, not quotients for L'Hopital's rule
    check("Limit(n*E^((n+1)^(3/2)-n^(3/2)), n->Infinity)", //
        "Infinity");
    check("Limit(AiryAi(n)/AiryAi(n+1), n->Infinity)", //
        "Infinity");
    // the recurrence of a degree above 100000
    check("GegenbauerC(150000, 3.5, 0.2)", //
        "2.16429*10^11");
  }
}
