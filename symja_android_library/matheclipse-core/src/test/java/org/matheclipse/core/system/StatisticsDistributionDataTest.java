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
