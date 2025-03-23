package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for statistics functions */
public class StatisticsTest extends ExprEvaluatorTestCase {

  @Test
  public void testAbsoluteCorrelation() {
    check("AbsoluteCorrelation({5, 3/4, 1}, {2, 1/2, 1})", //
        "91/24");
    check("AbsoluteCorrelation({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", //
        "40.4375");
    check("AbsoluteCorrelation(N({1, 2, 5, 6}, 20), N({2, 3, 6, 8}, 20))", //
        "21.5");
    check("AbsoluteCorrelation({2 + I, 3 - 2*I, 5 + 4* I}, {I, 1 + 2*I, 10 - 5*I})", //
        "10+I*55/3");
  }

  @Test
  public void testBinCounts() {

    check("BinCounts({{1,0}, {0,1}},-Infinity)", //
        "BinCounts({{1,0},{0,1}},-Infinity)");

    check("BinCounts({1,2,3,4,5},{1,7,2})", //
        "{2,2,1}");
    check("BinCounts({1,2,3,4,5},{1,7,3})", //
        "{3,2}");
    check("BinCounts({1,2,3,4,5,6,7,8,9,10,11,12,13},{1,13,4})", //
        "{4,4,4}");
    check("BinCounts({2,-1,a,b},{-1,3,1})", //
        "{1,0,0,1}");
    check("BinCounts({3/4,-2},{-1,3,1})", //
        "{0,1,0,0}");
    check("BinCounts({3/4},{1,3,1})", //
        "{0,0}");
    check("BinCounts({3/4} )", //
        "{1}");

    check("BinCounts({1,2,3,4,5})", //
        "{0,1,1,1,1,1}");
    check("BinCounts({1,2,3,4,5},3)", //
        "{2,3}");
    check("BinCounts({1,2,3,4,5},4)", //
        "{3,2}");
    check("BinCounts({1,2,3,4,5},5)", //
        "{4,1}");
    check("BinCounts({1,2,3,4,5},10)", //
        "{5}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0)", //
        "BinCounts({0.04,0.75,0.3333,0.03344,0.9999},0)");
    check("BinCounts({1,2,3,4,5},{3,3,-1})", //
        "BinCounts({1,2,3,4,5},{3,3,-1})");
    check("BinCounts({1,2,3,4,5},{3,3,1})", //
        "{}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0.1)", //
        "{2,0,0,1,0,0,0,1,0,1}");
    check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},-0.1)", //
        "BinCounts({0.04,0.75,0.3333,0.03344,0.9999},-0.1)");
    check("BinCounts({1, 2, 3, 4, 5})", //
        "{0,1,1,1,1,1}");
    check("BinCounts({1.5, 3, a, 2.5, 1, I}, 2)", //
        "{2,2}");
    check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, {0, 10, 1})", //
        "{0,2,2,1,1,1,1,0,0,0}");
    check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, 2)", //
        "{2,3,2,1}");
    check("BinCounts({1.5, 3, N(3, 20), 2.5, 1, E}, 2)", //
        "{2,4}");
  }

  @Test
  public void testMean() {
    // 2,2,2 tensor
    check("Mean({{{a,b},{c,d}},{{e,f},{g,h}}})", //
        "{{1/2*(a+e),1/2*(b+f)},{1/2*(c+g),1/2*(d+h)}}");

    check("Mean(Array(Subscript(a, ##) &, {2, 2}))", //
        "{1/2*(Subscript(a,1,1)+Subscript(a,2,1)),1/2*(Subscript(a,1,2)+Subscript(a,2,2))}");
    check("Mean(Array(Subscript(a, ##) &, {2, 2, 2}))", //
        "{{1/2*(Subscript(a,1,1,1)+Subscript(a,2,1,1)),1/2*(Subscript(a,1,1,2)+Subscript(a,\n"
            + "2,1,2))},{1/2*(Subscript(a,1,2,1)+Subscript(a,2,2,1)),1/2*(Subscript(a,1,2,2)+Subscript(a,\n"
            + "2,2,2))}}");
    check("Mean({26, 64, 36})", //
        "42");
    check("Mean({1, 1, 2, 3, 5, 8})", //
        "10/3");
    check("Mean({a, b})", //
        "1/2*(a+b)");

    check("Mean({{a, u}, {b, v}, {c, w}})", //
        "{1/3*(a+b+c),1/3*(u+v+w)}");
    check("Mean({1.21, 3.4, 2.15, 4, 1.55})", //
        "2.462");
    check("Mean({a,b,c,d})", //
        "1/4*(a+b+c+d)");

    check("Mean(BetaDistribution(a,b))", //
        "a/(a+b)");
    check("Mean(BernoulliDistribution(p))", //
        "p");
    check("Mean(BinomialDistribution(n, m))", //
        "m*n");
    check("Mean(ExponentialDistribution(n))", "1/n");
    check("Mean(PoissonDistribution(p))", //
        "p");
    check("Mean(BinomialDistribution(n, p))", //
        "n*p");
    check("Mean(DiscreteUniformDistribution({l, r}))", //
        "1/2*(l+r)");
    check("Mean(ErlangDistribution(n, m))", //
        "n/m");
    check("Mean(LogNormalDistribution(m,s))", //
        "E^(m+s^2/2)");
    check("Mean(NakagamiDistribution(n, m))", //
        "(Sqrt(m)*Pochhammer(n,1/2))/Sqrt(n)");
    check("Mean(NormalDistribution(n, p))", //
        "n");
    check("Mean(FrechetDistribution(n, m))", //
        "Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
    check("Mean(GammaDistribution(n, m))", //
        "m*n");
    check("Mean(GeometricDistribution(n))", //
        "-1+1/n");
    check("Mean(GumbelDistribution(n, m))", //
        "-EulerGamma*m+n");
    check("Mean(HypergeometricDistribution(n, ns, nt))", //
        "(n*ns)/nt");
    check("Mean(StudentTDistribution(4))", //
        "0");
    check("Mean(StudentTDistribution(n))", //
        "Piecewise({{0,n>1}},Indeterminate)");
    check("Mean(WeibullDistribution(n, m))", //
        "m*Gamma(1+1/n)");
  }

  @Test
  public void testMeanFilter() {
    check("MeanFilter({-3, 3, 6, 0, 0, 3, -3, -9}, 2)", //
        "{2,3/2,6/5,12/5,6/5,-9/5,-9/4,-3}");
    check("MeanFilter({1, 2, 3, 2, 1}, 1)", //
        "{3/2,2,7/3,2,3/2}");
    check("MeanFilter({0, 3, 8, 2}, 1)", //
        "{3/2,11/3,13/3,5}");
    check("MeanFilter({a,b,c}, 1)", //
        "{1/2*(a+b),1/3*(a+b+c),1/2*(b+c)}");
  }

  @Test
  public void testMeanDeviation() {
    // Config.MAX_AST_SIZE=Integer.MAX_VALUE;
    // check(
    // "MeanDeviation(RandomReal(1, 10^4))", //
    // "0.243758");
    check("MeanDeviation(SparseArray({{1, 2}, {4, 8}, {5, 3}, {2, 15}}))", //
        "{3/2,9/2}");
    check("MeanDeviation(1+(-1)*1)", //
        "MeanDeviation(0)");
    check("MeanDeviation({a, b, c})", //
        "1/3*(Abs(a+1/3*(-a-b-c))+Abs(b+1/3*(-a-b-c))+Abs(1/3*(-a-b-c)+c))");
    check("MeanDeviation({{1, 2}, {4, 8}, {5, 3}, {2, 15}})", //
        "{3/2,9/2}");
    check("MeanDeviation({1, 2, 3, 7})", //
        "15/8");
    check("MeanDeviation({Pi, E, 2})//Together", //
        "2/9*(-4+E+Pi)");
  }

  @Test
  public void testMedian() {
    // check("Median({a,b})", //
    // "");
    // 2,2,2 tensor
    check("Median({{{3, 7}, {2, 1}}, {{5, 19}, {12, 4}}})", //
        "{{4,13},{7,5/2}}");
    // message Median: Rectangular array of real numbers is expected at position 1 in
    // Median({{{a,b},{c,d}},{{e,f},{g,h}}}).
    check("Median({{{a,b},{c,d}},{{e,f},{g,h}}})", //
        "Median({{{a,b},{c,d}},{{e,f},{g,h}}})");

    check("Median(WeightedData({8, 3, 5,4}, " + //
        "{0.15, 0.09, 0.12,0.10}))", //
        "5");
    check("Median(WeightedData({3, 4,5,8}, " + //
        "{0.09, 0.10,0.12,0.15 }))", //
        "5");

    check("Median(WeightedData({8, 3, 5, 4, 9, 0, 4, 2, 2, 3}, " + //
        "{0.15, 0.09, 0.12, 0.10, 0.16, 0., 0.11, 0.08, 0.08, 0.09}))", //
        "4");
    check("Median(WeightedData({a,b,c,g}, {d,e,f,h}))", //
        "b*Boole(d/(d+e+f+h)<1/2<=d/(d+e+f+h)+e/(d+e+f+h))+c*Boole(d/(d+e+f+h)+e/(d+e+f+h)<1/\n"
            + "2<=d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h))+g*Boole(d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h)<\n"
            + "1/2<=d/(d+e+f+h)+e/(d+e+f+h)+f/(d+e+f+h)+h/(d+e+f+h))+a*Boole(1/2<=d/(d+e+f+h))");
    check("Median(WeightedData({a,b,c}, {d,e,f}))", //
        "b*Boole(d/(d+e+f)<1/2<=d/(d+e+f)+e/(d+e+f))+c*Boole(d/(d+e+f)+e/(d+e+f)<1/2<=d/(d+e+f)+e/(d+e+f)+f/(d+e+f))+a*Boole(\n"
            + //
            "1/2<=d/(d+e+f))");
    check("Median(WeightedData({a,b}, {d,e}))", //
        "b*Boole(d/(d+e)<1/2<=d/(d+e)+e/(d+e))+a*Boole(1/2<=d/(d+e))");

    check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
        "{99/2,1,4,26}");
    check("Median({26, 64, 36})", //
        "36");
    check("Median({-11, 38, 501, 1183})", //
        "539/2");
    check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
        "{99/2,1,4,26}");

    check("Median({1,2,3,4,5,6,7.0})", //
        "4.0");
    check("Median({1,2,3,4,5,6,7.0,8})", //
        "4.5");
    check("Median({1,2,3,4,5,6,7})", //
        "4");

    check("Median(BernoulliDistribution(p))", //
        "Piecewise({{1,p>1/2}},0)");
    check("Median(BetaDistribution(a,b))", //
        "InverseBetaRegularized(1/2,a,b)");
    check("Median(BinomialDistribution(n, m))", //
        "Median(BinomialDistribution(n,m))");
    check("Median(ExponentialDistribution(n))", //
        "Log(2)/n");
    check("Median(PoissonDistribution(p))", //
        "Median(PoissonDistribution(p))");
    check("Median(DiscreteUniformDistribution({l, r}))", //
        "-1+l+Max(1,Ceiling(1/2*(1-l+r)))");
    check("Median(UniformDistribution({l, r}))", //
        "1/2*(l+r)");
    check("Median(ErlangDistribution(n, m))", //
        "InverseGammaRegularized(n,0,1/2)/m");
    check("Median(LogNormalDistribution(m,s))", //
        "E^m");
    check("Median(NakagamiDistribution(n, m))", //
        "Sqrt((m*InverseGammaRegularized(n,0,1/2))/n)");
    check("Median(NormalDistribution())", //
        "0");
    check("Median(NormalDistribution(n, p))", //
        "n");
    check("Median(FrechetDistribution(n, m))", //
        "m/Log(2)^(1/n)");
    check("Median(GammaDistribution(n, m))", //
        "m*InverseGammaRegularized(n,0,1/2)");
    check("Median(GammaDistribution(a,b,g,d))", //
        "d+b*InverseGammaRegularized(a,1/2)^(1/g)");
    check("Median(GeometricDistribution(n))", //
        "Median(GeometricDistribution(n))");
    check("Median(GumbelDistribution( ))", //
        "Log(Log(2))");
    check("Median(GumbelDistribution(n, m))", //
        "n+m*Log(Log(2))");
    check("Median(HypergeometricDistribution(n, ns, nt))", //
        "Median(HypergeometricDistribution(n,ns,nt))");
    check("Median(StudentTDistribution(4))", //
        "0");
    check("Median(StudentTDistribution(n))", //
        "0");
    check("Median(StudentTDistribution(m,s,v))", //
        "m");
    check("Median(WeibullDistribution(a, b))", //
        "b*Log(2)^(1/a)");
    check("Median(WeibullDistribution(a, b, m))", //
        "m+b*Log(2)^(1/a)");
  }

  @Test
  public void testMedianFilter() {
    // Wikipedia example with "shrinking the window near the boundaries"
    check("MedianFilter({2,3,80,6}, 1)", //
        "{5/2,3,6,43}");

    check("MedianFilter({1, 2, 3, 2, 1}, 1)", //
        "{3/2,2,2,2,3/2}");
    check("MedianFilter({0, 3, 8, 2}, 1)", //
        "{3/2,3,3,5}");
    check("MedianFilter({a,b,c}, 1)", //
        "MedianFilter({a,b,c},1)");
  }

  @Test
  public void testRootMeanSquare() {
    // check("RootMeanSquare(RandomReal(1, 10^4))", //
    // "");
    check("RootMeanSquare({x,y,z})", //
        "Sqrt(x^2+y^2+z^2)/Sqrt(3)");
    check("RootMeanSquare({42})", //
        "42");
    check("RootMeanSquare({42,43})", //
        "Sqrt(3613/2)");
    check("{RootMeanSquare({42.0,43}),N(Sqrt(3613/2))}", //
        "{42.50294,42.50294}");
    check("RootMeanSquare({{1, 2}, {5, 10}, {5, 2}, {4, 8}})", //
        "{Sqrt(67)/2,Sqrt(43)}");
  }

  @Test
  public void testStandardDeviation() {
    // 2,2,2 tensor
    check("StandardDeviation({{{a,b},{c,d}},{{e,f},{g,h}}})", //
        "{{Sqrt((a-e)*(Conjugate(a)-Conjugate(e)))/Sqrt(2),Sqrt((b-f)*(Conjugate(b)-Conjugate(f)))/Sqrt(\n" //
            + "2)},{Sqrt((c-g)*(Conjugate(c)-Conjugate(g)))/Sqrt(2),Sqrt((d-h)*(Conjugate(d)-Conjugate(h)))/Sqrt(\n" //
            + "2)}}");
    checkNumeric(
        "StandardDeviation(<|1->0.41983028266218847,2->0.40423614350552506,3->0.8670734533759055|>)", //
        "0.26283328249866773");
    checkNumeric(
        "StandardDeviation(<|1 -> {0.41983028266218847, 0.49761928174436054},2 -> {0.40423614350552506, 0.09023790529445708}, 3 -> {0.8670734533759055, 0.684810805973151}|>)", //
        "{0.2628332824986678,0.30400580774134117}");
    checkNumeric(
        "StandardDeviation(<|1 -> {0.41983028266218847},2 -> {0.40423614350552506}, 3 -> {0.8670734533759055}|>)", //
        "{0.2628332824986678}");
    check("StandardDeviation(SparseArray({{1} -> 1, {100} -> 1}))", //
        "7/15*1/Sqrt(11)");
    check("StandardDeviation(SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}))", //
        "{1/Sqrt(3),2/Sqrt(3),Sqrt(13/3)}");
    check("StandardDeviation({0,0})", //
        "0");
    check("StandardDeviation({a,a})", //
        "0");
    check("StandardDeviation({42})", //
        "StandardDeviation({42})");
    check("StandardDeviation({1, 2, 3})", //
        "1");
    check("StandardDeviation({7, -5, 101, 100})", //
        "Sqrt(13297)/2");
    check("StandardDeviation({a, a})", //
        "0");
    check("StandardDeviation({{1, 10}, {-1, 20}})", //
        "{Sqrt(2),5*Sqrt(2)}");

    check("StandardDeviation({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
        "2.27183");
    check("StandardDeviation(LogNormalDistribution(0, 1))", //
        "Sqrt((-1+E)*E)");

    check("StandardDeviation({{5.2, 7}, {5.3, 8}, {5.4, 9}})", //
        "{0.1,1}");

    check(
        "data={Quantity(0.7158053755285898`,\"Meters\"),Quantity(0.6165538695742894`,\"Meters\"),Quantity(0.28515333590035374`,\"Meters\")," //
            + "Quantity(0.7076548991038116`,\"Meters\"),Quantity(0.266982550128781`,\"Meters\"),Quantity(0.8175584666730205`,\"Meters\")}", //
        "{0.715805[Meters],0.616554[Meters],0.285153[Meters],0.707655[Meters],0.266983[Meters],0.817558[Meters]}");
    check("StandardDeviation(data)", //
        "0.235202[Meters]");
  }

  @Test
  public void testVariance() {
    check("Variance({a,b})", //
        "1/2*(a-b)*(Conjugate(a)-Conjugate(b))");
    // 2,2,2 tensor
    check("Variance({{{a,b},{c,d}},{{e,f},{g,h}}})", //
        "{{1/2*(a-e)*(Conjugate(a)-Conjugate(e)),1/2*(b-f)*(Conjugate(b)-Conjugate(f))},{\n" //
            + "1/2*(c-g)*(Conjugate(c)-Conjugate(g)),1/2*(d-h)*(Conjugate(d)-Conjugate(h))}}");
    checkNumeric(
        "Variance(<|1->0.41983028266218847 ,2->0.40423614350552506 , 3->0.8670734533759055 |>)", //
        "0.069081334389024475");
    checkNumeric(
        "Variance(<|1 -> {0.41983028266218847, 0.49761928174436054},2 ->{0.40423614350552506, 0.09023790529445708}, 3 -> {0.8670734533759055, 0.684810805973151}|>)", //
        "{0.06908133438902449,0.0924195311404653}");
    check("Variance(BinomialDistribution(n, m))", //
        "(1-m)*m*n");
    check("Variance(BernoulliDistribution(n))", //
        "(1-n)*n");
    check("Variance(BetaDistribution(a,b))", //
        "(a*b)/((a+b)^2*(1+a+b))");
    check("Variance(DiscreteUniformDistribution({l, r}))", //
        "1/12*(-1+(1-l+r)^2)");
    check("Variance(ErlangDistribution(n, m))", //
        "n/m^2");
    check("Variance(ExponentialDistribution(n))", //
        "1/n^2");
    check("Variance(LogNormalDistribution(m,s))", //
        "(-1+E^s^2)*E^(2*m+s^2)");
    check("Variance(NakagamiDistribution(n, m))", //
        "m+(-m*Pochhammer(n,1/2)^2)/n");
    check("Variance(NormalDistribution(n, m))", //
        "m^2");
    check("Variance(FrechetDistribution(n, m))", //
        "Piecewise({{m^2*(Gamma(1-2/n)-Gamma(1-1/n)^2),n>2}},Infinity)");
    check("Variance(GammaDistribution(n, m))", //
        "m^2*n");
    check("Variance(GeometricDistribution(n))", //
        "(1-n)/n^2");
    check("Variance(GumbelDistribution(n, m))", //
        "1/6*m^2*Pi^2");
    check("Variance(GompertzMakehamDistribution(m,n))", //
        "(E^n*(6*EulerGamma^2+Pi^2-6*E^n*ExpIntegralEi(-n)^2-12*n*HypergeometricPFQ({1,1,\n" + //
            "1},{2,2,2},-n)+12*EulerGamma*Log(n)+6*Log(n)^2))/(6*m^2)");
    check("Variance(HypergeometricDistribution(n, ns, nt))", //
        "(n*ns*(1-ns/nt)*(-n+nt))/((-1+nt)*nt)");
    check("Variance(PoissonDistribution(n))", //
        "n");
    check("Variance(StudentTDistribution(4))", //
        "2");
    check("Variance(StudentTDistribution(n))", //
        "Piecewise({{n/(-2+n),n>2}},Indeterminate)");

    check("Variance(WeibullDistribution(n, m))", //
        "m^2*(-Gamma(1+1/n)^2+Gamma(1+2/n))");

    check("Variance({1, 2, 3})", //
        "1");
    check("Variance({7, -5, 101, 3})", //
        "7475/3");
    check("Variance({1 + 2*I, 3 - 10*I})", //
        "74");
    check("Variance({a, a})", //
        "0");
    check("Variance({{1, 3, 5}, {4, 10, 100}})", //
        "{9/2,49/2,9025/2}");

    check("Variance({Pi,E,3})//Together", //
        "1/3*(9-3*E+E^2-3*Pi-E*Pi+Pi^2)");
    check("Variance({a,b,c,d})", //
        "1/12*(-(-3*a+b+c+d)*Conjugate(a)-(a-3*b+c+d)*Conjugate(b)-(a+b-3*c+d)*Conjugate(c)-(a+b+c-\n"
            + "3*d)*Conjugate(d))");
    checkNumeric("Variance({1., 2., 3., 4.})", //
        "1.6666666666666667");
    checkNumeric("Variance({{5.2, 7}, {5.3, 8}, {5.4, 9}})", //
        "{0.010000000000000018,1.0}");
    checkNumeric("Variance({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
        "5.16122380952381");
    check("Variance({1.21, 3.4, 2+3*I, 4.66-0.1*I, 1.5, 5.61, 7.22})", //
        "6.46265");
    // check("Variance(BernoulliDistribution(p))", "p*(1-p)");
    // check("Variance(BinomialDistribution(n, p))", "n*p*(1-p)");
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
