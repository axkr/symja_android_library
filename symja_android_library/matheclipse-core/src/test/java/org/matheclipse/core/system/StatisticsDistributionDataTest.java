package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Statistics, distributions, wavelet filters, dates and cities. */
public class StatisticsDistributionDataTest extends ExprEvaluatorTestCase {

  @Test
  public void testKolmogorovSmirnovTwoSample() {
    // the exact permutation distribution, ties kept together: 2/Binomial(6,3) and 2/7
    check("{KolmogorovSmirnovTest({1, 2, 3}, {4, 5, 6}), "
        + "KolmogorovSmirnovTest({1, 1, 2, 5}, {2, 3, 3, 4, 6}, {\"TestStatistic\", \"PValue\"})}", //
        "{0.1,{11/20,0.285714}}");
    // inexact data give an inexact statistic
    check("KolmogorovSmirnovTest({1., 1, 2, 5}, {2, 3, 3, 4, 6}, \"TestData\")", //
        "{0.55,0.285714}");
  }

  @Test
  public void testKendallTauTestProperties() {
    // a property is a string
    check("KendallTauTest({1, 2, 3, 4}, {2, 1, 4, 3}, pvalue)", //
        "KendallTauTest({1,2,3,4},{2,1,4,3},pvalue)");
    check("KendallTauTest({1, 2, 3, 4, 5}, {2, 1, 4, 3, 5}, {\"TestStatistic\", \"TestData\"})", //
        "{0.6,{0.6,0.141645}}");
  }

  @Test
  public void testSmoothKernelDistributionGrid() {
    check("d = SmoothKernelDistribution({0., 1., 3.}, 0.4); "
        + "{PDF(d, 0.5), CDF(d, 2.), PDF(d, 10.), CDF(d, 10.)}", //
        "{0.30444,0.666667,0.0,1.0}");
    // the biweight kernel lives on [-1, 1]
    check("PDF(SmoothKernelDistribution({0., 1., 3.}, 0.4, \"Biweight\"), 0.5)", //
        "0.0");
  }

  @Test
  public void testNumericDistributions() {
    check("{CDF(NoncentralBetaDistribution(3, 2, 1), 0.4), PDF(BeckmannDistribution(1, 0, 1, 1), 1.5)}", //
        "{0.138444,0.486389}");
    check("{LandauDistribution(), Quantile(LandauDistribution(0, 1), 1), "
        + "Quantile(NoncentralBetaDistribution(1, 2, 3), 0)}", //
        "{LandauDistribution(0,1),Infinity,0}");
    // exact arguments without a closed form stay unevaluated
    check("Quantile(LandauDistribution(0, 1), 1/3)", //
        "Quantile(LandauDistribution(0,1),1/3)");
    check("PDF(NoncentralBetaDistribution(2, 1, 4), y)", //
        "Piecewise({{(y*(2+2*y))/E^(2-2*y),0<y<1}},0)");
  }

  @Test
  public void testRayleighAndInverseChiSquare() {
    check("{CDF(RayleighDistribution(s), x), Mean(RayleighDistribution(3)), "
        + "Quantile(RayleighDistribution(2), 3/4)}", //
        "{Piecewise({{1-1/E^(x^2/(2*s^2)),x>0}},0),3*Sqrt(Pi/2),2*Sqrt(Log(16))}");
    check("{Mean(InverseChiSquareDistribution(6)), Variance(InverseChiSquareDistribution(8)), "
        + "CDF(InverseChiSquareDistribution(4, 2), x)}", //
        "{1/4,1/72,Piecewise({{(1+4/x)/E^(4/x),x>0}},0)}");
  }

  @Test
  public void testMultivariateHypergeometricDistribution() {
    check("{Mean(MultivariateHypergeometricDistribution(6, {4, 2, 2})), "
        + "PDF(MultivariateHypergeometricDistribution(3, {2, 2, 1}), {1, 1, 1})}", //
        "{{3,3/2,3/2},2/5}");
  }

  @Test
  public void testDistributionsOfQuantities() {
    check("{NormalDistribution(Quantity(2, \"Meters\"), Quantity(1/2, \"Meters\")), "
        + "NormalDistribution(Quantity(2, \"Meters\"), 3)}", //
        "{QuantityDistribution(NormalDistribution(2,1/2),\"Meters\"),"
            + "NormalDistribution(Quantity(2,\"Meters\"),3)}");
  }

  @Test
  public void testDerivedDistributions() {
    check("Probability(x > 2, x \\[Distributed] EmpiricalDistribution({1, 2, 3, 4}))", //
        "1/2");
    check("Probability(g(x) > 0, x \\[Distributed] NormalDistribution())", //
        "Probability(g(x)>0,Distributed(x,NormalDistribution(0,1)))");
    check("CDF(CensoredDistribution({1, 1}, ExponentialDistribution(2)), x)", //
        "Piecewise({{1,x>=1}},0)");
    check("PDF(ProductDistribution({UniformDistribution(), 2}), {x, y})", //
        "Piecewise({{1,0<=x<=1&&0<=y<=1}},0)");
    check("EstimatedDistribution({2, 7, 3, 5}, UniformDistribution({a, b}))", //
        "UniformDistribution({2.0,7.0})");
    check("GaussianUnitaryMatrixDistribution(5)", //
        "GaussianUnitaryMatrixDistribution(1,5)");
  }

  @Test
  public void testCovarianceFunctionArma() {
    check("Table(CovarianceFunction(ARMAProcess({1/3}, {1/2}, 2), h), {h, 0, 2})", //
        "{57/16,35/16,35/48}");
    check("CovarianceFunction(ARProcess({0.5}, 1.), 1, 3)", //
        "0.333333");
    // not stationary
    check("CovarianceFunction(ARMAProcess({2}, {1}, 1), 1)", //
        "CovarianceFunction(ARMAProcess({2},{1},1),1)");
  }

  @Test
  public void testCorrelationFunctions() {
    // the sample autocovariance divides by the length of the data; {hmax} gives the lags 0..hmax
    check("x = {4, 1, 3, 6, 2, 5}; {CovarianceFunction(x, 2), CorrelationFunction(x, {2}), "
        + "PartialCorrelationFunction(x, {3})}", //
        "{-1/3,{1,-29/70,-4/35},{-29/70,-467/1353,571/11518}}");
    // a lag of at least the length of the data has no value
    check("CorrelationFunction({4, 1, 3, 6, 2, 5}, 6)", //
        "CorrelationFunction({4,1,3,6,2,5},6)");
    check("CorrelationFunction({2, 2, 2}, 1)", //
        "Indeterminate");
    // the theoretical functions of a process; the partial autocorrelations of an AR(p) process
    // vanish beyond the lag p
    check("{Table(CorrelationFunction(ARProcess({1/3}, 2), h), {h, 0, 2}), "
        + "Table(PartialCorrelationFunction(ARProcess({1/3, 1/5}, 2), h), {h, 1, 3}), "
        + "CorrelationFunction(MAProcess({1/4}, 1), 1)}", //
        "{{1,1/3,1/9},{5/12,1/5,0},4/17}");
    // the partial autocorrelations are symmetric in the lag
    check("PartialCorrelationFunction({3, 7, 2, 5, 1}, -2) == "
        + "PartialCorrelationFunction({3, 7, 2, 5, 1}, 2)", //
        "True");
  }

  @Test
  public void testCorrelationFunctionsOfPairsAndPaths() {
    // uniformly spaced {time, value} pairs are the series of their values
    check("v = {3, 7, 2, 5, 1}; {CorrelationFunction(Transpose({{2, 4, 6, 8, 10}, v}), 1), "
        + "CorrelationFunction(v, 1)}", //
        "{-167/290,-167/290}");
    check("CovarianceFunction({{1, 3}, {2, 7}, {4, 2}}, 1)", //
        "CovarianceFunction({{1,3},{2,7},{4,2}},1)");
    // several paths are taken around the mean of all values, their autocovariances averaged
    check("td = TemporalData({{1, 4, 2}, {5, 3, 6}}, {0}); "
        + "{CorrelationFunction(td, 1), CovarianceFunction(td, 1)}", //
        "{-8/35,-2/3}");
    check("td = TemporalData({{2, 5, 1}, {4, 6, 3, 7}}, {0}); "
        + "{CovarianceFunction(td, 1), CorrelationFunction(td, 2), Head(CorrelationFunction(td, 3))}", //
        "{-35/24,3/7,CorrelationFunction}");
    // the data of a TemporalData object is a list of paths, also of one path
    check("td = TemporalData({{1, 4, 2}}, {0}); {Normal(td), Normal(TimeSeries({1, 4, 2}, {0}))}", //
        "{{{{0,1},{1,4},{2,2}}},{{0,1},{1,4},{2,2}}}");
  }

  @Test
  public void testCovarianceFunctionAtSymbolicLags() {
    // AR(1): v*a^|h|/(1-a^2), also of symbolic parameters; the autocorrelation a^|h|
    check("{CovarianceFunction(ARProcess({1/3}, 2), k), CovarianceFunction(ARProcess({p}, s2), k), "
        + "CorrelationFunction(ARProcess({-2/5}, 3), k)}", //
        "{3^(2-Abs(k))/4,(p^Abs(k)*s2)/(1-p^2),(-2/5)^Abs(k)}");
    // MA(q): one value for each lag up to q
    check("CovarianceFunction(MAProcess({c1, c2}, w), k)", //
        "Piecewise({{c2*w,Abs(k)==2},{c1*(1+c2)*w,Abs(k)==1},{(1+c1^2+c2^2)*w,Abs(k)==0}},\n"
            + "0)");
    // equal values share one case, a zero value has none
    check("CovarianceFunction(MAProcess({1, 0, 1}, 1), k)", //
        "Piecewise({{1,Abs(k)==1||Abs(k)==2||Abs(k)==3},{3,Abs(k)==0}},0)");
    check("CovarianceFunction(MAProcess({1/2, 0, 0, 1/4}, 4), h)", //
        "Piecewise({{1,Abs(h)==4},{1/2,Abs(h)==3},{2,Abs(h)==1},{21/4,Abs(h)==0}},0)");
    // ARMA(1,1): gamma(1)*a^(|h|-1) beyond the lag 0
    check("CorrelationFunction(ARMAProcess({1/4}, {1/2}, 1), k)", //
        "5/8*Piecewise({{9/5*2^(1-2*Abs(k)),Abs(k)>0}},8/5)");
    // ARMA(1,q): gamma(q)*a^(|h|-q) from the lag q on
    check("CovarianceFunction(ARMAProcess({1/3}, {1/2, -1/4}, 2), k)", //
        "Piecewise({{41/(64*3^Abs(k)),Abs(k)>1},{217/64,Abs(k)==0}},329/192)");
    check("CovarianceFunction(ARMAProcess({p}, {q}, w), k)", //
        "Piecewise({{((p+q)*(1+p*q)*w)/(p^(1-Abs(k))*(1-p^2)),Abs(k)>0}},((1+2*p*q+q^2)*w)/(\n1-p^2))");
    // integer lags of symbolic parameters
    check("{CovarianceFunction(ARProcess({p}, w), 3), CorrelationFunction(MAProcess({c}, w), {0, 2}), "
        + "CovarianceFunction(ARProcess({p}, w), u, u + 2)}", //
        "{(p^3*w)/(1-p^2),{1,c/(1+c^2),0},(p^2*w)/(1-p^2)}");
    // two times: the lag |s-t|
    check("CovarianceFunction(ARProcess({1/3}, 2), u, 4)", //
        "3^(2-Abs(-4+u))/4");
    // the closed forms agree with the values at integer lags
    check("{Table((CovarianceFunction(ARMAProcess({1/4}, {1/2}, 1), k) /. k -> j) - "
        + "CovarianceFunction(ARMAProcess({1/4}, {1/2}, 1), j), {j, 0, 3}), "
        + "Table((CorrelationFunction(MAProcess({2/3, -1/4}, 5), k) /. k -> j) - "
        + "CorrelationFunction(MAProcess({2/3, -1/4}, 5), j), {j, -3, 3})}", //
        "{{0,0,0,0},{0,0,0,0,0,0,0}}");
    check("{Table((CovarianceFunction(ARMAProcess({-1/3}, {1/2, 1/4, -1/5}, 3), k) /. k -> j) - "
        + "CovarianceFunction(ARMAProcess({-1/3}, {1/2, 1/4, -1/5}, 3), j), {j, -5, 5}), "
        + "Table((CovarianceFunction(ARMAProcess({p}, {q1, q2}, w), k) /. "
        + "{k -> j, p -> 2/5, q1 -> -1/2, q2 -> 1/3, w -> 3}) - "
        + "CovarianceFunction(ARMAProcess({2/5}, {-1/2, 1/3}, 3), j), {j, 0, 4})}", //
        "{{0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0}}");
    // no closed form: a process which is not stationary, AR(2) and data
    check("{Head(CovarianceFunction(ARProcess({3}, 1), k)), "
        + "Head(CovarianceFunction(ARProcess({1/2, 1/5}, 1), k)), "
        + "Head(CovarianceFunction({1, 2, 3}, k))}", //
        "{CovarianceFunction,CovarianceFunction,CovarianceFunction}");
    // no autoregressive part: white noise or MA(q), no 0^0
    check("{CovarianceFunction(ARMAProcess({0}, {b}, w), 1), CovarianceFunction(ARProcess({0}, w), {0, 2}), "
        + "CovarianceFunction(ARMAProcess({0}, {b}, w), k), CorrelationFunction(ARProcess({0}, 2), k)}", //
        "{b*w,{w,0,0},Piecewise({{b*w,Abs(k)==1},{(1+b^2)*w,Abs(k)==0}},0),Piecewise({{1,Abs(k)==\n0}},0)}");
    // a process which is not stationary and data with a symbolic lag give a message
    check("{Check(CovarianceFunction(ARProcess({3}, 1), k), bad, ARProcess::nonwkst), "
        + "Check(CovarianceFunction(ARProcess({-2}, w), k), bad, ARProcess::nonwkst), "
        + "Check(CorrelationFunction(ARProcess({5/4}, 1), 2), bad, ARProcess::nonwkst), "
        + "Check(CovarianceFunction({1, 2, 3}, k), bad, CovarianceFunction::bdlag)}", //
        "{bad,bad,bad,bad}");
  }

  @Test
  public void testPartialCorrelationFunctionAtSymbolicLag() {
    // AR(p): the values at the lags +-1, ..., +-p, for integers h != 0
    check("PartialCorrelationFunction(ARProcess({1/4}, 3), h)", //
        "ConditionalExpression(Piecewise({{1/4,h==1||h==-1}},0),h∈Integers&&h!=0)");
    check("PartialCorrelationFunction(ARProcess({1/4, 1/5}, 1), h)", //
        "ConditionalExpression(Piecewise({{5/16,h==1||h==-1},{1/5,h==2||h==-2}},0),h∈Integers&&h!=\n0)");
    check("Table(PartialCorrelationFunction(ARProcess({1/4, 1/5}, 1), h), {h, 1, 3}) == "
        + "PartialCorrelationFunction(ARProcess({1/4, 1/5}, 1), {3})", //
        "True");
    // no closed form: an MA process or a process without parameters
    check("{Head(PartialCorrelationFunction(MAProcess({1/2}, 1), h)), "
        + "Head(PartialCorrelationFunction(ARProcess(), h))}", //
        "{PartialCorrelationFunction,PartialCorrelationFunction}");
  }

  @Test
  public void testCorrelationFunctionLagRanges() {
    // a lag which is not a machine integer has no value
    check("Head /@ {CovarianceFunction({2,6,1}, 10^20), CorrelationFunction({2,6,1}, -2^31), "
        + "PartialCorrelationFunction({2,6,1,4}, {10^30}), CovarianceFunction({2,6,1}, {10^30})}", //
        "{CovarianceFunction,CorrelationFunction,PartialCorrelationFunction,CovarianceFunction}");
    // a lag range gives the values of its lags
    check("w = {4,8,1,6,2,5}; {CovarianceFunction(w, {3}) == Table(CovarianceFunction(w, k), {k, 0, 3}), "
        + "CorrelationFunction(w, {1, 5, 2}) == Table(CorrelationFunction(w, k), {k, 1, 5, 2}), "
        + "PartialCorrelationFunction(w, {4}) == Table(PartialCorrelationFunction(w, k), {k, 4})}", //
        "{True,True,True}");
    check("p = ARProcess({1/3, 1/6}, 2); "
        + "{CovarianceFunction(p, {0, 4, 2}) == Table(CovarianceFunction(p, k), {k, 0, 4, 2}), "
        + "PartialCorrelationFunction(p, {3}) == Table(PartialCorrelationFunction(p, k), {k, 3})}", //
        "{True,True}");
    // the lags beyond the shorter path have no value
    check("td = TemporalData({{2, 5, 1}, {4, 6, 3, 7}}, {0}); Head /@ CorrelationFunction(td, {1, 4})", //
        "{Rational,Rational,CorrelationFunction,CorrelationFunction}");
    // complex data: plain products, the autocorrelations scaled by the real variance
    check("z = {3*I, -I, 1, -3}; {CovarianceFunction(z, {1}), CorrelationFunction(z, 1)}", //
        "{{I*1/2,-I*3/8},-I*1/12}");
    // a numeric lag without a value and a lag list which is no range give a message
    check("w = {4,8,1,6,2,5}; {Check(CovarianceFunction(w, 6), bad, CovarianceFunction::bdlag), "
        + "Check(CorrelationFunction(w, 2.5), bad, CorrelationFunction::bdlag), "
        + "Check(CovarianceFunction(ARProcess({1/4}, 1), {1, 2, 3, 4}), bad, CovarianceFunction::bdlag), "
        + "Check(Head(CovarianceFunction(g(x), {1, 2, 3, 4})), bad, CovarianceFunction::bdlag)}", //
        "{bad,bad,bad,CovarianceFunction}");
  }

  @Test
  public void testDiscreteMarkovProcessPath() {
    check("RandomFunction(DiscreteMarkovProcess(1, {{0, 1}, {1, 0}}), {2, 5})[\"Path\"]", //
        "{{2,1},{3,2},{4,1},{5,2}}");
  }

  @Test
  public void testDatePlusListLength() {
    check("{DatePlus({2024, 2, 28}, {36, \"Hour\"}), DatePlus({2024, 2, 28, 6}, {1, \"Day\"}), "
        + "DatePlus({2024, 2, 28, 6, 30, 0}, {3., \"Second\"})}", //
        "{{2024,2,29,12},{2024,2,29,6},{2024,2,28,6,30,3.0}}");
    check("{DatePlus({2024, 1, 1}, {0.25, \"Day\"}), "
        + "First(DatePlus(DateObject({2024, 5, 1, 8, 0, 0}), {1.5, \"Second\"}))}", //
        "{{2024,1,1,6},{2024,5,1,8,0,1.5}}");
  }

  @Test
  public void testCityData() {
    check("{CityData(\"Vienna\", \"Coordinates\"), CityData({\"Hamburg\", \"Germany\"}, \"Country\"), "
        + "CityData(\"Atlantis\")}", //
        "{{48.22,16.37},Entity(Country,Germany),{}}");
  }

  @Test
  public void testWaveletFilterCoefficients() {
    check("WaveletFilterCoefficients(BiorthogonalSplineWavelet(1, 1), "
        + "{\"PrimalLowpass\", \"DualHighpass\"}, WorkingPrecision -> Infinity)", //
        "{{{0,1/2},{1,1/2}},{{0,-1/2},{1,1/2}}}");
    check("WaveletFilterCoefficients(SymletWavelet(2), \"Properties\")", //
        "{PrimalLowpass,PrimalHighpass,LiftingFilter,AllLiftingFilter,BestLiftingFilter}");
    // an orthogonal wavelet has no dual filters
    check("{Length(WaveletFilterCoefficients(BattleLemarieWavelet(2, 4))), "
        + "WaveletFilterCoefficients(HaarWavelet(), \"DualLowpass\")}", //
        "{9,WaveletFilterCoefficients(HaarWavelet(),DualLowpass)}");
    check("WaveletPhi(BattleLemarieWavelet(1, 8), 0.) > WaveletPhi(BattleLemarieWavelet(1, 8), 0.5)", //
        "True");
  }

  @Test
  public void testQRDecompositionFull() {
    check("QRDecomposition({{1, 0}, {1, 1}, {0, 1}}, Full)", //
        "{{{1/Sqrt(2),1/Sqrt(2),0},{-1/Sqrt(6),1/Sqrt(6),Sqrt(2/3)},{1/Sqrt(3),-1/Sqrt(3),1/Sqrt(\n"
            + "3)}},{{Sqrt(2),1/Sqrt(2)},{0,Sqrt(3/2)},{0,0}}}");
  }

  @Test
  public void testLinearModelFitWeights() {
    // different weights give a weighted fit, invalid ones are rejected
    check("Head(LinearModelFit({{1, 2}, {2, 3}, {3, 5}}, x, x, Weights -> {1, 2, 3}))", //
        "FittedModel");
    check("Head(LinearModelFit({{1, 2}, {2, 3}, {3, 5}}, x, x, Weights -> w))", //
        "LinearModelFit");
  }
}
