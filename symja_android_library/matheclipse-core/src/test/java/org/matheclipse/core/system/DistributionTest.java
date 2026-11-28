package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class DistributionTest extends ExprEvaluatorTestCase {

  @Test
  public void testBatesDistribution() {
    check("PDF(BatesDistribution(1), x)", //
        "Piecewise({{1,0<=x<=1}},0)");
    check("PDF(BatesDistribution(2), x)", //
        "Piecewise({{0,x<0},{4*x,0<=x<1/2},{2*(2-2*x),1/2<=x<=1}},0)");
    check("CDF(BatesDistribution(2), x)", //
        "Piecewise({{0,x<0},{2*x^2,0<=x<1/2},{1+1/2*(-4+8*x-4*x^2),1/2<=x<=1}},1)");
    check("CDF(BatesDistribution(3), x)", //
        "Piecewise({{0,x<0},{9/2*x^3,0<=x<1/3},{1/6*(3-27*x+81*x^2-54*x^3),1/3<=x<2/3},{1+1/6*(-\n"
            + "27+81*x-81*x^2+27*x^3),2/3<=x<=1}},1)");
    check("CDF(BatesDistribution(3, {-1, 1}), x)", //
        "Piecewise({{0,x<-1},{1/6*(27/8+81/8*x+81/8*x^2+27/8*x^3),-1<=x<-1/3},{1/6*(3+27/4*x-\n"
            + "27/4*x^3),-1/3<=x<1/3},{1+1/6*(-27/8+81/8*x-81/8*x^2+27/8*x^3),1/3<=x<=1}},1)");
    check("CDF(BatesDistribution(2, {a, b}), x)", //
        "Piecewise({{0,x<a},{(2*(-a+x)^2)/(-a+b)^2,a<=x<a+1/2*(-a+b)},{1-(2+(-2*(-a+x))/(-a+b))^\n"
            + "2/2,a+1/2*(-a+b)<=x<=b}},1)");

    check("Mean(BatesDistribution(n, {a, b}))", //
        "1/2*(a+b)");
    check("Median(BatesDistribution(n, {a, b}))", //
        "1/2*(a+b)");
    check("Variance(BatesDistribution(n, {a, b}))", //
        "(-a+b)^2/(12*n)");
    check("Variance(BatesDistribution(n))", //
        "1/(12*n)");
    check("Skewness(BatesDistribution(n, {a, b}))", //
        "0");
    check("Kurtosis(BatesDistribution(n, {a, b}))", //
        "3-6/(5*n)");
    check("CentralMoment(BatesDistribution(n, {a, b}), 3)", //
        "0");
    check("CentralMoment(BatesDistribution(n, {a, b}), 4)", //
        "((-a+b)^4*(-2+5*n))/(240*n^3)");
    check("Moment(BatesDistribution(3), 3)", //
        "1/6");

    // the moments agree with the integrals of the density
    check("NIntegrate(PDF(BatesDistribution(5), x), {x, 0, 1})", //
        "1.0");
    check("Integrate(x^2*PDF(BatesDistribution(3), x), {x, 0, 1})", //
        "5/18");
    check("Moment(BatesDistribution(3), 2)", //
        "5/18");
    check("NIntegrate((x-1/2)^4*PDF(BatesDistribution(5), x), {x, 0, 1})", //
        "0.000766667");
    check("N(CentralMoment(BatesDistribution(5), 4))", //
        "0.000766667");
    check("NIntegrate(x^4*PDF(BatesDistribution(3, {1, 4}), x), {x, 1, 4})", //
        "48.6");
    check("Moment(BatesDistribution(3, {1, 4}), 4)", //
        "243/5");

    check("CDF(BatesDistribution(5), {-0.5, 0.3, 0.5, 0.7, 1.5})", //
        "{0,0.0619792,0.5,0.938021,1}");
    // the mean of 4 variables on [2, 6] is 2 plus the sum of 4 variables on [0, 1]
    check("PDF(BatesDistribution(4, {2, 6}), {3.0, 4.0, 5.5})", //
        "{0.166667,0.666667,0.0208333}");
    check("CDF(BatesDistribution(4, {2, 6}), 3.0) == CDF(UniformSumDistribution(4), 1.0)", //
        "True");

    check("Length(RandomVariate(BatesDistribution(4), 10))", //
        "10");
    check("Abs(Mean(RandomVariate(BatesDistribution(4, {2, 6}), 10^4)) - 4) < 0.05", //
        "True");
    check("Abs(Variance(RandomVariate(BatesDistribution(4, {2, 6}), 10^4)) - 1/3) < 0.05", //
        "True");
    check("MinMax(RandomVariate(BatesDistribution(4, {2, 6}), 10^3)) // "
        + "(2 <= First(#) && Last(#) <= 6)&", //
        "True");

    check("DistributionParameterAssumptions(BatesDistribution(n, {a, b}))", //
        "n∈Integers&&n>0&&a<b");
    // message: BatesDistribution: Parameter 2.5 at position 1 in BatesDistribution(2.5) is
    // expected to be a positive integer.
    check("PDF(BatesDistribution(2.5), x)", //
        "PDF(BatesDistribution(2.5),x)");
    check("PDF(BatesDistribution(-2), x)", //
        "PDF(BatesDistribution(-2),x)");
  }

  @Test
  public void testBernoulliDistribution() {
    check("Mean(BernoulliDistribution(x))", //
        "x");
    check("Quantile(BernoulliDistribution(x), {1/4, 1/2, 3/4})", //
        "{Piecewise({{1,x>3/4}},0),Piecewise({{1,x>1/2}},0),Piecewise({{1,x>1/4}},0)}");
  }

  @Test
  public void testPoissonDistributionNonPositiveRate() {
    check("Mean(PoissonDistribution(-7/2))", //
        "Mean(PoissonDistribution(-7/2))");
    check("Variance(PoissonDistribution(0))", //
        "Variance(PoissonDistribution(0))");
    check("CDF(PoissonDistribution(-2), 3)", //
        "CDF(PoissonDistribution(-2),3)");
    check("{Mean(PoissonDistribution(5/3)), Skewness(PoissonDistribution(9))}", //
        "{5/3,1/3}");
  }

  @Test
  public void testBernoulliProcess() {
    check("BernoulliProcess(p)[t]", //
        "BernoulliDistribution(p)");
  }

  @Test
  public void testBinomialProcess() {
    check("BinomialProcess(p)[t]", //
        "BinomialDistribution(t,p)");
  }

  @Test
  public void testBrownianBridgeProcess() {
    check("BrownianBridgeProcess( )", //
        "BrownianBridgeProcess(1,{0,0},{1,0})");
    check("BrownianBridgeProcess( )[t]", //
        "NormalDistribution(0,Sqrt((1-t)*t))");

    check("BrownianBridgeProcess({t1, a}, {t2, b})", //
        "BrownianBridgeProcess(1,{t1,a},{t2,b})");
    check("BrownianBridgeProcess({t1, a}, {t2, b,c})", //
        "BrownianBridgeProcess(1,{{t1,a},0},{{t2,b,c},0})");

    check("BrownianBridgeProcess({t1, a}, {t2, b})[t]", //
        "NormalDistribution((b*(t-t1))/(-t1+t2)+(a*(-t+t2))/(-t1+t2),Sqrt(((t-t1)*(-t+t2))/(-t1+t2)))");
    check("BrownianBridgeProcess(s, {t1, a}, {t2, b})[t]", //
        "NormalDistribution((b*(t-t1))/(-t1+t2)+(a*(-t+t2))/(-t1+t2),s*Sqrt(((t-t1)*(-t+t2))/(-t1+t2)))");
    check("Mean(BrownianBridgeProcess(s, {t1, a}, {t2, b})[t])", //
        "(b*(t-t1))/(-t1+t2)+(a*(-t+t2))/(-t1+t2)");
  }


  @Test
  public void testCDF() {

    check("CDF(BetaDistribution(2,3), 0.1)", //
        "0.0523");
    check("CDF(BetaDistribution(2,3), 0.9)", //
        "0.9963");
    check("CDF(ChiSquareDistribution(3), 0.1)", //
        "0.00816258");
    check("CDF(ChiSquareDistribution(3), 0.9)", //
        "0.174572");
    check("CDF(ExponentialDistribution(3), 0.1)", //
        "0.259182");
    check("CDF(ExponentialDistribution(3), 0.9)", //
        "0.932794");
    check("CDF(FRatioDistribution(2,3), 0.1)", //
        "0.0922695");
    check("CDF(FRatioDistribution(2,3), 0.9)", //
        "0.505894");
    check("CDF(GammaDistribution(2,3), 0.1)", //
        "0.000543363");
    check("CDF(GammaDistribution(2,3), 0.9)", //
        "0.0369363");
    check("CDF(GumbelDistribution(2,3), 0.1)", //
        "0.411877");
    check("CDF(GompertzMakehamDistribution(2,3), 0.1)", //
        "0.485319");
    check("CDF(GumbelDistribution(2,3), 0.9)", //
        "0.499947");
    check("CDF(LogNormalDistribution(2,3), 0.1)", //
        "0.0757583");
    check("CDF(LogNormalDistribution(2,3), 0.9)", //
        "0.241406");
    check("CDF(NakagamiDistribution(2,3), 0.1)", //
        "0.0000221237");
    check("CDF(NakagamiDistribution(2,3), 0.9)", //
        "0.102568");
    check("CDF(NormalDistribution(2,3), 0.1)", //
        "0.263258");
    check("CDF(NormalDistribution(2,3), 0.9)", //
        "0.356934");
    check("CDF(UniformDistribution({0,1}), 0.1)", //
        "0.1");
    check("CDF(UniformDistribution({0,1}), 0.9)", //
        "0.9");
    check("CDF(WeibullDistribution(2,3), 0.1)", //
        "0.00111049");
    check("CDF(WeibullDistribution(2,3), 0.9)", //
        "0.0860688");

    // github #56
    check("CDF(NormalDistribution(),-0.41)", //
        "0.340903");
    check("CDF(NormalDistribution(),0.41)", //
        "0.659097");
    check("Table(CDF(NormalDistribution(0, s), x), {s, {.75, 1, 2}}, {x, -6,6}) // N", //
        "{{6.22096*10^-16,1.30839*10^-11,4.8213*10^-8,0.0000316712,0.00383038,0.0912112,0.5,0.908789,0.99617,0.999968,1.0,1.0,1.0},{9.86588*10^-10,2.86652*10^-7,0.0000316712,0.0013499,0.0227501,0.158655,0.5,0.841345,0.97725,0.99865,0.999968,1.0,1.0},{0.0013499,0.00620967,0.0227501,0.0668072,0.158655,0.308538,0.5,0.691462,0.841345,0.933193,0.97725,0.99379,0.99865}}");
    checkNumeric("Table(CDF(NormalDistribution(0, s), x), {s, {.75, 1, 2}}, {x, -6,6}) // N", //
        "{{6.220960574271835E-16,1.308392468605314E-11,4.821303365114145E-8,3.167124183311997E-5,0.0038303805675897404,0.09121121972586804,0.5,0.9087887802741319,0.9961696194324102,0.9999683287581669,0.9999999517869663,0.999999999986916,0.9999999999999993},{9.86587645037693E-10,2.866515718791932E-7,3.167124183311985E-5,0.001349898031630092,0.022750131948179146,0.15865525393145696,0.5,0.841344746068543,0.9772498680518209,0.9986501019683699,0.9999683287581669,0.9999997133484281,0.9999999990134123},{0.001349898031630092,0.006209665325776124,0.022750131948179146,0.06680720126885814,0.15865525393145696,0.308537538725987,0.5,0.691462461274013,0.841344746068543,0.9331927987311419,0.9772498680518209,0.9937903346742238,0.9986501019683699}}");
    check("CDF(NormalDistribution(n, m),k)", //
        "Erfc((-k+n)/(Sqrt(2)*m))/2");

    check("CDF(BernoulliDistribution(p),k)", //
        "Piecewise({{0,k<0},{1-p,0<=k&&k<1}},1)");
    check("CDF(BetaDistribution(a,b),k)", //
        "Piecewise({{BetaRegularized(k,a,b),0<k<1},{1,k>=1}},0)");
    check("CDF(BinomialDistribution(n, m),k)", //
        "Piecewise({{BetaRegularized(1-m,n-Floor(k),1+Floor(k)),0<=k&&k<n},{1,k>=n}},0)");
    check("CDF(ExponentialDistribution(n),k)", //
        "Piecewise({{1-1/E^(k*n),k>=0}},0)");
    check("CDF(PoissonDistribution(p),k)", //
        "Piecewise({{GammaRegularized(1+Floor(k),p),k>=0}},0)");
    check("CDF(DiscreteUniformDistribution({a, b}), k)", //
        "Piecewise({{(1-a+Floor(k))/(1-a+b),a<=k&&k<b},{1,k>=b}},0)");
    check("CDF(UniformDistribution({a, b}), k)", //
        "Piecewise({{(-a+k)/(-a+b),a<=k<=b},{1,k>b}},0)");
    check("CDF(ErlangDistribution(n, m),k)", //
        "Piecewise({{GammaRegularized(n,0,k*m),k>0}},0)");
    check("CDF(LogNormalDistribution(n,m),k)", //
        "Piecewise({{Erfc((n-Log(k))/(Sqrt(2)*m))/2,k>0}},0)");
    check("CDF(NakagamiDistribution(n, m),k)", //
        "Piecewise({{GammaRegularized(n,0,(k^2*n)/m),k>0}},0)");
    check("CDF(NormalDistribution(n, m),k)", //
        "Erfc((-k+n)/(Sqrt(2)*m))/2");
    check("CDF(FrechetDistribution(n, m),k)", //
        "Piecewise({{E^(-1/(k/m)^n),k>0}},0)");
    check("CDF(GammaDistribution(n, m),k)", //
        "Piecewise({{GammaRegularized(n,0,k/m),k>0}},0)");
    check("CDF(GeometricDistribution(n),k)", //
        "Piecewise({{1-(1-n)^(1+Floor(k)),k>=0}},0)");
    check("CDF(GumbelDistribution(n, m),k)", //
        "1-1/E^E^((k-n)/m)");
    check("CDF(GompertzMakehamDistribution(m,n) )", //
        "Function(#1,Piecewise({{1-E^((1-E^(m*#1))*n),#1>=0}},0),Listable)");
    check("CDF(HypergeometricDistribution(n, ns, nt),k)", //
        "Piecewise({{1+(-ns!*(-ns+nt)!*HypergeometricPFQRegularized({1,1-n+Floor(k),1-ns+Floor(k)},{\n"
            + "2+Floor(k),2-n-ns+nt+Floor(k)},1))/(Binomial(nt,n)*(-1+n-Floor(k))!*(-1+ns-Floor(k))!),\n"
            + "0<=k&&n+ns-nt<=k&&k<n&&k<ns},{1,k>=n||k>=ns}},0)");
    check("CDF(StudentTDistribution(n),k)", //
        "Piecewise({{BetaRegularized(n/(k^2+n),n/2,1/2)/2,k<=0}},1/2*(1+BetaRegularized(k^\n"
            + "2/(n+k^2),1/2,n/2)))");
    check("CDF(WeibullDistribution(n, m),k)", //
        "Piecewise({{1-1/E^(k/m)^n,k>0}},0)");
    check("CDF(BernoulliDistribution(4),k)", //
        "Piecewise({{0,k<0},{-3,0<=k&&k<1}},1)");

    check("CDF(DiscreteUniformDistribution({1, 5}), 3)", //
        "3/5");
  }

  @Test
  public void testChiSquareDistribution() {
    check("StandardDeviation(ChiSquareDistribution(v))", //
        "Sqrt(2)*Sqrt(v)");
    check("Mean(ChiSquareDistribution(v))", //
        "v");
    check("Variance(ChiSquareDistribution(v))", //
        "2*v");
    check("CDF(ChiSquareDistribution(v))", //
        "Function(#1,Piecewise({{GammaRegularized(v/2,0,#1/2),#1>0}},0),Listable)");
    check("PDF(ChiSquareDistribution(v))", //
        "Function(#1,Piecewise({{1/(2^(v/2)*E^(#1/2)*Gamma(v/2)*#1^(1-v/2)),#1>0}},0),Listable)");

    check("CDF(ChiSquareDistribution(v), k)", //
        "Piecewise({{GammaRegularized(v/2,0,k/2),k>0}},0)");
    check("PDF(ChiSquareDistribution(v), k)", //
        "Piecewise({{1/(2^(v/2)*E^(k/2)*k^(1-v/2)*Gamma(v/2)),k>0}},0)");
    check("Skewness(ChiSquareDistribution(v))", //
        "2*Sqrt(2)*Sqrt(1/v)");
  }

  @Test
  public void testEmpiricalDistribution() {
    check(
        "data={0.056996052779672735, -0.044108236741598965, -0.9261489534265283, -0.43709234535272856, " //
            + "-0.2720099029639003, 0.3173834473707418, 1.9652135846762475, -1.426022310225606, " //
            + "0.8632622190346015, -0.8927279617406512};", //
        "");
    check("edata=EmpiricalDistribution(data);", //
        "");
    check("{CDF(edata,0.25),CDF(edata,1),CDF(edata,3),CDF(edata,4),CDF(edata,5)}", //
        "{0.7,0.9,1,1,1}");
    // https://github.com/Hipparchus-Math/hipparchus/pull/317
    check("{PDF(edata,0.25),PDF(edata,1),PDF(edata,3),PDF(edata,4),PDF(edata,5)}", //
        "{0,0,0,0,0}");
    check("Mean(edata)", //
        "-0.0795254");
    check("Variance(edata)", //
        "0.860326");
  }

  @Test
  public void testErlangDistribution() {
    check("Mean(ErlangDistribution(n, m))", //
        "n/m");
    check("Quantile(ErlangDistribution(n, m), {1/4, 1/2, 3/4})", //
        "{InverseGammaRegularized(n,0,1/4)/m,InverseGammaRegularized(n,0,1/2)/m,InverseGammaRegularized(n,\n"
            + "0,3/4)/m}");
  }

  @Test
  public void testExpectation() {
    // TODO improve integration for piecewise functions
    // check("Expectation((x + 3)/(x + 5), Distributed(x, ExponentialDistribution(2)))", //
    // "Expectation((3+x)/(5+x),x\uF3D2ExponentialDistribution(2))");

    check("Limit((-3*x)/(E^(x^2/2)*Sqrt(2*Pi))+4*Erf(x/Sqrt(2)), x -> -Infinity)", //
        "-4");
    check("Expectation(2*x+3, x \\[Distributed] NormalDistribution() )", //
        "3");
    check("Expectation(3*x^2 + 5, Distributed(x, NormalDistribution()))", //
        "8");

    check("Expectation((#^3)&, {a,b,c})", //
        "1/3*(a^3+b^3+c^3)");
    check("Expectation(2*x+3,Distributed(x,{a,b,c,d}))", //
        "1/4*(12+2*a+2*b+2*c+2*d)");
    check("Expectation(f(x),Distributed(x,{a,b}))", //
        "1/2*(f(a)+f(b))");
    // check("PDF( PoissonDistribution(m),x)", //
    // "Piecewise({{m^x/(E^m*x!),x>=0}},0)");
    check("Expectation(x^2+7*x+8,Distributed(x,PoissonDistribution(m)))", //
        "8+8*m+m^2");
    check("Expectation(E^(2*x) + 3, Distributed( x, PoissonDistribution(l)))", //
        "3+E^((-1+E^2)*l)");

    check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 9})))", "13/2");
    check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 10})))", "7");

    check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 9})))", "16");
    check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 10})))", "17");
  }

  @Test
  public void testExpectationConditioned() {
    // E(expr | pred) == E(expr*Boole(pred)) / P(pred)
    check("Expectation(Conditioned(x, x > 0), Distributed(x, NormalDistribution()))", //
        "2/Sqrt(2*Pi)");
    check("NExpectation(Conditioned(x, x > 0), Distributed(x, NormalDistribution()))", //
        "0.797885");
    check("NProbability(Conditioned(x > 1, x > 0), Distributed(x, NormalDistribution()))", //
        "0.317311");
  }

  @Test
  public void testExpectationMultipleVariables() {
    // independent random variables are computed by iterated expectation
    check(
        "Expectation(x*y, {Distributed(x, NormalDistribution(1,1)), Distributed(y, NormalDistribution(2,1))})", //
        "2");
    check(
        "Expectation(x^2+y, {Distributed(x, PoissonDistribution(2)), Distributed(y, PoissonDistribution(3))})", //
        "9");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testExpectationNumericFallback() {
    // N(Expectation(...)) falls back to NExpectation if there is no symbolic result
    check("N(Expectation(Sin(x), Distributed(x, PoissonDistribution(2))))", //
        "0.396255");
    check("N(Probability(1 < x && x < 3, Distributed(x, NormalDistribution())))", //
        "0.157305");
  }

  @Test
  public void testExpectationGeneratingFunctions() {
    // closed forms from the moment generating function of the distribution
    check("Expectation(z^x, Distributed(x, PoissonDistribution(l)))", //
        "E^(l*(-1+z))");
    check("Expectation(E^(t*x), Distributed(x, BinomialDistribution(n,p)))", //
        "(1-p+E^t*p)^n");
    check("Expectation(2^x, Distributed(x, HypergeometricDistribution(3,3,6)))", //
        "63/20");
    check("Expectation(E^(2*x), Distributed(x, NormalDistribution()))", //
        "E^2");
    check("Expectation(x^2*E^x, Distributed(x, NormalDistribution()))", //
        "2*Sqrt(E)");
    // closed forms found by summing the unwrapped piecewise density
    check("Expectation(z^x, Distributed(x, GeometricDistribution(p)))", //
        "p/(1-z+p*z)");
    check("Expectation(x*E^x, Distributed(x, PoissonDistribution(1)))", //
        "E^E");
    // the true series diverges - the closed form of the sum is not valid here
    check("Expectation(3^x, Distributed(x, GeometricDistribution(1/5)))", //
        "Expectation(3^x,Distributed(x,GeometricDistribution(1/5)))");
    // numeric cross checks of the symbolic closed forms
    check("N(Expectation(E^(2*x) + 3, Distributed(x, PoissonDistribution(1/2))))", //
        "27.39866");
    check("NExpectation(E^(2*x) + 3, Distributed(x, PoissonDistribution(0.5)))", //
        "27.39866");
  }

  @Test
  public void testMomentGeneratingFunction() {
    check("MomentGeneratingFunction(NormalDistribution(m,s), t)", //
        "E^(m*t+1/2*s^2*t^2)");
    check("MomentGeneratingFunction(GammaDistribution(a,b), t)", //
        "(1-b*t)^(-a)");
    check("MomentGeneratingFunction(ExponentialDistribution(l), t)", //
        "l/(l-t)");
    check("MomentGeneratingFunction(GeometricDistribution(p), t)", //
        "p/(1+E^t*(-1+p))");
    check("MomentGeneratingFunction(UniformDistribution({a,b}), t)", //
        "(-E^(a*t)+E^(b*t))/((-a+b)*t)");
    check("MomentGeneratingFunction(ChiSquareDistribution(k), t)", //
        "(1-2*t)^(-k/2)");
    check("MomentGeneratingFunction(GumbelDistribution(n,m), t)", //
        "E^(n*t)*Gamma(1+m*t)");
    check("MomentGeneratingFunction(BetaDistribution(a,b), t)", //
        "Hypergeometric1F1(a,a+b,t)");
    check("MomentGeneratingFunction(ErlangDistribution(n,m), t)", //
        "(1-t/m)^(-n)");
    check("MomentGeneratingFunction(LaplaceDistribution(m,b), t)", //
        "E^(m*t)/(1-b^2*t^2)");
    // empirical moment generating function of a data list
    check("MomentGeneratingFunction({1,2,3}, t)", //
        "1/3*(E^t+E^(2*t)+E^(3*t))");
    // MGF(0) == 1 and D(MGF)(0) == Mean
    check("MomentGeneratingFunction(GammaDistribution(2,3), 0)", //
        "1");
    check("ReplaceAll(D(MomentGeneratingFunction(PoissonDistribution(l), t), t), t->0)", //
        "l");
  }

  @Test
  public void testCharacteristicFunction() {
    check("CharacteristicFunction(NormalDistribution(m,s), t)", //
        "E^(I*m*t-1/2*s^2*t^2)");
    check("CharacteristicFunction(CauchyDistribution(a,b), t)", //
        "E^(I*a*t-b*Abs(t))");
    check("CharacteristicFunction(StudentTDistribution(v), t)", //
        "(2^(1-v/2)*(Sqrt(v)*Abs(t))^(v/2)*BesselK(v/2,Sqrt(v)*Abs(t)))/Gamma(v/2)");
    check("CharacteristicFunction(ExponentialDistribution(l), t)", //
        "l/(l-I*t)");
    // StudentTDistribution(1) is CauchyDistribution(0,1): CF == E^(-Abs(t))
    check("N(CharacteristicFunction(StudentTDistribution(1), 7/10))", //
        "0.496585");
    check("N(CharacteristicFunction(CauchyDistribution(0,1), 1))", //
        "0.367879");
    check("CharacteristicFunction(PoissonDistribution(l), 0)", //
        "1");
  }

  @Test
  public void testFactorialMomentGeneratingFunction() {
    check("FactorialMomentGeneratingFunction(BinomialDistribution(n,p), z)", //
        "(1-p+p*z)^n");
    check("FactorialMomentGeneratingFunction(PoissonDistribution(l), z)", //
        "E^(l*(-1+z))");
    check("FactorialMomentGeneratingFunction(GeometricDistribution(p), z)", //
        "p/(1+(-1+p)*z)");
    check("FactorialMomentGeneratingFunction(DiscreteUniformDistribution({a,b}), z)", //
        "(z^a-z^(1+b))/((1-a+b)*(1-z))");
    check("FactorialMomentGeneratingFunction(BetaBinomialDistribution(a,b,n), z)", //
        "Hypergeometric2F1(a,-n,a+b,1-z)");
    check("FactorialMomentGeneratingFunction(LogSeriesDistribution(q), z)", //
        "Log(1-q*z)/Log(1-q)");
    check("FactorialMomentGeneratingFunction(ZipfDistribution(r), z)", //
        "PolyLog(1+r,z)/Zeta(1+r)");
    // exact enumeration of a small finite support
    check("FactorialMomentGeneratingFunction(HypergeometricDistribution(3,3,6), 2)", //
        "63/20");
    check("N(FactorialMomentGeneratingFunction(BenfordDistribution(10), 1))", //
        "1.0");
  }

  @Test
  public void testCentralMomentGeneratingFunction() {
    check("CentralMomentGeneratingFunction(NormalDistribution(m,s), t)", //
        "E^(1/2*s^2*t^2)");
    check("CentralMomentGeneratingFunction(PoissonDistribution(l), t)", //
        "E^((-1+E^t)*l-l*t)");
  }

  @Test
  public void testCumulantGeneratingFunction() {
    check("CumulantGeneratingFunction(NormalDistribution(m,s), t)", //
        "m*t+1/2*s^2*t^2");
    check("CumulantGeneratingFunction(PoissonDistribution(l), t)", //
        "(-1+E^t)*l");
    check("CumulantGeneratingFunction(GammaDistribution(a,b), t)", //
        "Log((1-b*t)^(-a))");
    check("CumulantGeneratingFunction(LaplaceDistribution(m,b), t)", //
        "m*t+Log(1/(1-b^2*t^2))");
    // the second derivative at 0 is the variance
    check("ReplaceAll(D(CumulantGeneratingFunction(NormalDistribution(m,s), t), {t,2}), t->0)", //
        "s^2");
  }

  @Test
  public void testNExpectation() {
    // check("NExpectation(E^x, Distributed(x, PoissonDistribution(1.0)))", //
    // "5.57494");


    check("NExpectation(Abs(2*x-1), Distributed(x, GammaDistribution(3.5,2)))", //
        "13.00025");

    check("NExpectation(E^x, Distributed(x, HypergeometricDistribution(20,50,100)))", //
        "158982.7");
    // E^x grows faster than the geometric tail decays - the expectation diverges
    // check("NExpectation(E^x, Distributed(x, GeometricDistribution(0.1)))", //
    // "");
    check("NExpectation(E^x, Distributed(x, DiscreteUniformDistribution({20,40})))", //
        "1.77321*10^16");
    check("NExpectation(E^x, Distributed(x, BinomialDistribution(10,0.4)))", //
        "187.0492");
    check("NExpectation(E^x, Distributed(x, PoissonDistribution(1.0)))", //
        "5.57494");
    check("NExpectation(x^2+7*x+8, Distributed(x, PoissonDistribution(2.7)))", //
        "36.89");
    check("NExpectation(2*x+7, Distributed(x, PoissonDistribution(1)))", //
        "9.0");

    check("NExpectation(x^2+7*x+8, Distributed(x, NormalDistribution()))", //
        "9.0");
    check("NExpectation((x-1)^2+E^(-x), Distributed(x, NormalDistribution()))", //
        "3.64872");
    check("NExpectation(2*x+3, Distributed(x, NormalDistribution()))", //
        "3.0");

    check("NExpectation(Abs(2*x-1), Distributed(x, ExponentialDistribution(3)))", //
        "0.63084");
    check("NExpectation((x + 3)/(x + 5), Distributed(x, ExponentialDistribution(2)))", //
        "0.633747");
    check("NExpectation(E^(-3*x^2)+1, Distributed(x, ExponentialDistribution(2)))", //
        "1.59157");
    check(
        "NExpectation(Piecewise({{5, x<1}, {x^2+3, x>=1}}), Distributed(x, ExponentialDistribution(2)))", //
        "5.06767");
  }


  @Test
  public void testNProbability() {
    check("NProbability(90<=x <=110, Distributed(x,NormalDistribution(100,15)))", //
        "0.495015");
    check("NProbability(E^x<3, Distributed(x,PoissonDistribution(1)))", //
        "0.735759");
    check("NProbability(x < 3, Distributed(x,NormalDistribution()))", //
        "0.99865");
    check("NProbability(3 < x < 5, Distributed(x,NormalDistribution()))", //
        "0.00134961");
    check("NProbability(E^x < 3, Distributed(x,NormalDistribution()))", //
        "0.864031");
    check("NProbability(x^2 > 3, Distributed(x,ExponentialDistribution(2)))", //
        "0.0313011");
    check("NProbability(x^2 > 3 || Abs(x)<1, Distributed(x,ExponentialDistribution(2)))", //
        "0.895966");
    check("Sum(1/(E*x!), {x,0,Infinity})", //
        "1");
    check("NSum(1/(E*x!), {x,0,Infinity})", //
        "1.0");
    check("NProbability(x == 3, Distributed(x, PoissonDistribution(5)))", //
        "0.140374");
    check("NProbability(1 < x && x < 3, Distributed(x, NormalDistribution()))", //
        "0.157305");
    check("NProbability(x < 1, Distributed(x, NormalDistribution()), WorkingPrecision -> 20)", //
        "0.841345");
    check("NExpectation(#^2 &, {1,2,3})", //
        "4.66667");
  }

  @Test
  public void testProbabilityDiscreteTail() {
    // before the support bound fixes these enumerations ran out of memory
    check("Probability(x >= 3, Distributed(x, GeometricDistribution(1/5)))", //
        "64/125");
    check("Probability(x >= 3, Distributed(x, BinomialDistribution(10, 1/2)))", //
        "121/128");
    check("NProbability(x >= 3, Distributed(x, GeometricDistribution(1/5)))", //
        "0.512");
    check("Probability(x >= 3, Distributed(x, PoissonDistribution(2)))", //
        "1-5/E^2");
    check("Probability(x != 5, Distributed(x, PoissonDistribution(3)))", //
        "1-81/(40*E^3)");
  }

  @Test
  public void testNExpectationLargeParameters() {
    // overflow of the probability mass function terms produced Indeterminate before
    check("NExpectation(x, Distributed(x, PoissonDistribution(1000.0)))", //
        "1000.0");
    check("NExpectation(x, Distributed(x, PoissonDistribution(3000.0)))", //
        "3000.0");
    check("NExpectation(x, Distributed(x, BinomialDistribution(20, 0.5)))", //
        "10.0");
    check("NProbability(x <= 500, Distributed(x, PoissonDistribution(400.)))", //
        "0.999999");
    // a piecewise density is integrated over its support intervals only
    check("NExpectation(x^2, Distributed(x, UniformDistribution({2,4})))", //
        "9.33333");
  }

  @Test
  public void testExponentialDistribution() {
    check("Mean(ExponentialDistribution(x))", //
        "1/x");
    check("Quantile(ExponentialDistribution(x), {1/4, 1/2, 3/4})", //
        "{Log(4/3)/x,Log(2)/x,Log(4)/x}");
  }

  @Test
  public void testFRatioDistribution() {
    check("Mean(FRatioDistribution(n, m))", //
        "Piecewise({{m/(-2+m),m>2}},Indeterminate)");
    // TODO distribute Sqrt over Piecewise
    check("StandardDeviation(FRatioDistribution(n, m))", //
        "Sqrt(Piecewise({{(2*m^2*(-2+m+n))/((2-m)^2*(-4+m)*n),m>4}},Indeterminate))");
    check("Variance(FRatioDistribution(n, m))", //
        "Piecewise({{(2*m^2*(-2+m+n))/((2-m)^2*(-4+m)*n),m>4}},Indeterminate)");
    check("CDF(FRatioDistribution(n, m))", //
        "Function(#1,Piecewise({{BetaRegularized((n*#1)/(m+n*#1),n/2,m/2),#1>0}},0),Listable)");
    check("PDF(FRatioDistribution(n, m))", //
        "Function(#1,Piecewise({{(m^(m/2)*n^(n/2)*(m+n*#1)^(1/2*(-m-n)))/(Beta(n/2,m/2)*#1^(\n1-n/2)),#1>0}},0),Listable)");
    // TODO
    // check("Skewness(FRatioDistribution(n, m))", //
    // "");
  }

  @Test
  public void testFisherZDistribution() {
    check("PDF(FisherZDistribution(n, m), x)", //
        "(2*E^(n*x)*m^(m/2)*n^(n/2)*(m+E^(2*x)*n)^(1/2*(-m-n)))/Beta(n/2,m/2)");
    check("CDF(FisherZDistribution(n, m), x)", //
        "BetaRegularized((E^(2*x)*n)/(m+E^(2*x)*n),n/2,m/2)");
    check("SurvivalFunction(FisherZDistribution(n, m), x)", //
        "BetaRegularized(m/(m+E^(2*x)*n),m/2,n/2)");
    check("Quantile(FisherZDistribution(n, m), q)", //
        "ConditionalExpression(Piecewise({{Log((m*(-1+1/InverseBetaRegularized(1,-q,m/2,n/\n"
            + "2)))/n)/2,0<q<1},{-Infinity,q<=0}},Infinity),0<=q<=1)");
    check("Mean(FisherZDistribution(n, m))", //
        "1/2*(Log(m/n)-PolyGamma(0,m/2)+PolyGamma(0,n/2))");
    check("Median(FisherZDistribution(n, m))", //
        "Log((m*(-1+1/InverseBetaRegularized(1,-1/2,m/2,n/2)))/n)/2");
    check("Variance(FisherZDistribution(n, m))", //
        "1/4*(PolyGamma(1,m/2)+PolyGamma(1,n/2))");
    check("Skewness(FisherZDistribution(n, m))", //
        "(-PolyGamma(2,m/2)+PolyGamma(2,n/2))/(PolyGamma(1,m/2)+PolyGamma(1,n/2))^(3/2)");
    check("Kurtosis(FisherZDistribution(n, m))", //
        "3+(PolyGamma(3,m/2)+PolyGamma(3,n/2))/(PolyGamma(1,m/2)+PolyGamma(1,n/2))^2");
    check("CentralMoment(FisherZDistribution(n, m), 3)", //
        "1/8*(-PolyGamma(2,m/2)+PolyGamma(2,n/2))");
    check("MomentGeneratingFunction(FisherZDistribution(n, m), t)", //
        "((m/n)^(t/2)*Gamma(1/2*(m-t))*Gamma(1/2*(n+t)))/(Gamma(m/2)*Gamma(n/2))");
    check("CharacteristicFunction(FisherZDistribution(4, 2), t)", //
        "(Gamma(1/2*(2-I*t))*Gamma(1/2*(4+I*t)))/2^(I*1/2*t)");
    check("{Mean(FisherZDistribution(n, n)), Skewness(FisherZDistribution(n, n))}", //
        "{0,0}");
    check("Mean(FisherZDistribution(4, 2))", //
        "1/2*(1-Log(2))");
    check("Variance(FisherZDistribution(4, 2))", //
        "1/4*(-1+Pi^2/3)");
    check("CDF(FisherZDistribution(4, 2), x)", //
        "(16*E^(4*x))/(2+4*E^(2*x))^2");

    // the moments agree with the integrals of the density
    check("NIntegrate(PDF(FisherZDistribution(4, 2), x), {x, -Infinity, Infinity})", //
        "1.0");
    check("NIntegrate((x-Mean(FisherZDistribution(4, 2)))^3*PDF(FisherZDistribution(4, 2), x), "
        + "{x, -Infinity, Infinity})", //
        "0.25");
    check("N({CentralMoment(FisherZDistribution(4, 2), 3), " //
        + "Kurtosis(FisherZDistribution(4, 2))})", //
        "{0.25,4.33268}");

    check("PDF(FisherZDistribution(3, 5), {-1.0, 0.0, 0.3})", //
        "{0.172472,0.722349,0.606443}");
    check("CDF(FisherZDistribution(3, 5), {-1.0, 0.0, 0.3})", //
        "{0.0652221,0.535145,0.739932}");
    check("N(CDF(FisherZDistribution(3, 5), 3/10))", //
        "0.739932");
    check("N(CDF(FisherZDistribution(4, 2), 1/2), 30)", //
        "0.71341266942642611597008092327");
    // no overflow of E^(2*x) and no cancellation in the upper tail
    check("{PDF(FisherZDistribution(4, 2), 400.0), CDF(FisherZDistribution(4, 2), 400.0)}", //
        "{0.0,1.0}");
    check("SurvivalFunction(FisherZDistribution(4, 2), 20.0)", //
        "4.24835*10^-18");
    check("N(SurvivalFunction(FisherZDistribution(4, 2), 20), 20)", //
        "0.0000000000000000042483542552915889818");
    check("Quantile(FisherZDistribution(4, 2), {0, 0.25, 0.5, 0.75, 1})", //
        "{-Infinity,-0.346574,0.0941132,0.586558,Infinity}");
    check("CDF(FisherZDistribution(4, 2), Quantile(FisherZDistribution(4, 2), 0.3))", //
        "0.3");

    check("Length(RandomVariate(FisherZDistribution(4, 2), 10))", //
        "10");
    check("Abs(Mean(RandomVariate(FisherZDistribution(4, 2), 10^4)) - 0.153426) < 0.05", //
        "True");

    check("DistributionParameterAssumptions(FisherZDistribution(n, m))", //
        "n>0&&m>0");
    // message: FisherZDistribution: Parameter -1 at position 1 in FisherZDistribution(-1,2) is
    // expected to be positive.
    check("PDF(FisherZDistribution(-1, 2), x)", //
        "PDF(FisherZDistribution(-1,2),x)");
    check("RandomVariate(FisherZDistribution(2, 0))", //
        "RandomVariate(FisherZDistribution(2,0))");
  }

  @Test
  public void testFrechetDistribution() {
    check("Mean(FrechetDistribution(n, m))", //
        "Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
    check("Quantile(FrechetDistribution(n, m), {1/4, 1/2, 3/4})", //
        "{m/Log(4)^(1/n),m/Log(2)^(1/n),m/Log(4/3)^(1/n)}");
  }

  @Test
  public void testInverseGammaDistribution() {
    check("PDF(InverseGammaDistribution(a, b), x)", //
        "Piecewise({{(b/x)^a/(E^(b/x)*x*Gamma(a)),x>0}},0)");
    check("CDF(InverseGammaDistribution(a, b), x)", //
        "Piecewise({{GammaRegularized(a,b/x),x>0}},0)");
    check("PDF(InverseGammaDistribution(a, b, g, m), x)", //
        "Piecewise({{(g*(b/(-m+x))^(1+a*g))/(b*E^(b/(-m+x))^g*Gamma(a)),x>m}},0)");
    check("CDF(InverseGammaDistribution(a, b, g, m), x)", //
        "Piecewise({{GammaRegularized(a,(b/(-m+x))^g),x>m}},0)");
    check("{PDF(InverseGammaDistribution(2, 3), 1), CDF(InverseGammaDistribution(2, 3), 1), " //
        + "PDF(InverseGammaDistribution(2, 3), -1), CDF(InverseGammaDistribution(2,3,2,1), 5/2)}", //
        "{9/E^3,4/E^3,0,5/E^4}");
    // machine numbers agree with the exact form
    check(
        "{PDF(InverseGammaDistribution(2.5, 1.5), 2.3), CDF(InverseGammaDistribution(2.5, 1.5), 2.3), " //
            + "PDF(InverseGammaDistribution(2.5, 1.5, 0.8, 1), 2.3), " //
            + "CDF(InverseGammaDistribution(2.5, 1.5, 0.8, 1), 2.3)}", //
        "{0.0585206,0.934484,0.200833,0.814662}");
    check("{N(PDF(InverseGammaDistribution(5/2, 3/2), 23/10)), " //
        + "N(CDF(InverseGammaDistribution(5/2, 3/2, 4/5, 1), 23/10))}", //
        "{0.0585206,0.814662}");
    // a large shape does not overflow
    check("{PDF(InverseGammaDistribution(300.0, 500.0), 1.7), " //
        + "CDF(InverseGammaDistribution(300.0, 500.0), 1.7)}", //
        "{3.83286,0.626495}");
    check("NIntegrate(PDF(InverseGammaDistribution(2.5, 1.5, 0.8, 1), x), {x, 1, Infinity})", //
        "1.0");
    check(
        "CDF(InverseGammaDistribution(2.5,1.5,0.8,1), Median(InverseGammaDistribution(2.5,1.5,0.8,1)))", //
        "0.5");
    // sampler
    check("AllTrue(RandomVariate(InverseGammaDistribution(2, 3), 50), #>0&)", //
        "True");
    check("AllTrue(RandomVariate(InverseGammaDistribution(2, 3, 2, -1), 50), #>-1&)", //
        "True");
    check("Dimensions(RandomVariate(InverseGammaDistribution(2, 3), {2,3}))", //
        "{2,3}");
    check(
        "Table(SeedRandom(9); RandomVariate(InverseGammaDistribution(2,3,2,1), 4), {2}) // Apply(SameQ)", //
        "True");
    check("RandomVariate(InverseGammaDistribution(-2, 3))", //
        "RandomVariate(InverseGammaDistribution(-2,3))");
    check("PDF(InverseGammaDistribution(-2, 3), 1)", //
        "PDF(InverseGammaDistribution(-2,3),1)");
    // Skewness and InverseCDF
    check("Skewness(InverseGammaDistribution(a, b))", //
        "Piecewise({{(4*Sqrt(-2+a))/(-3+a),a>3}},Indeterminate)");
    check("Skewness(InverseGammaDistribution(a, b, g, m)) === " //
        + "Piecewise({{(Gamma(a)^2*Gamma(a-3/g)-3*Gamma(a)*Gamma(a-2/g)*Gamma(a-1/g)" //
        + "+2*Gamma(a-1/g)^3)/(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2)^(3/2),a*g>3}},Indeterminate)", //
        "True");
    check("{Variance(InverseGammaDistribution(5, 2)), Skewness(InverseGammaDistribution(5, 2)), " //
        + "Variance(InverseGammaDistribution(3/2, 2)), Skewness(InverseGammaDistribution(3, 2))}", //
        "{1/12,2*Sqrt(3),Indeterminate,Indeterminate}");
    // the moments by quadrature
    check("Module({d=InverseGammaDistribution(4.5, 1.5, 1.3, 0.5), m1, m2, m3}, " //
        + "m1=NIntegrate(x*PDF(d,x),{x,0.5,Infinity}); " //
        + "m2=NIntegrate((x-m1)^2*PDF(d,x),{x,0.5,Infinity}); " //
        + "m3=NIntegrate((x-m1)^3*PDF(d,x),{x,0.5,Infinity}); " //
        + "{m1, N(Mean(d)), m2, N(Variance(d)), m3/m2^(3/2), N(Skewness(d))})", //
        "{1.05758,1.05758,0.0625915,0.0625915,2.5741,2.5741}");
    check("InverseCDF(InverseGammaDistribution(a, b), q) === " //
        + "ConditionalExpression(Piecewise({{b/InverseGammaRegularized(a,q),0<q<1},{0,q<=0}}," //
        + "Infinity),0<=q<=1)", //
        "True");
    check("InverseCDF(InverseGammaDistribution(a, b, g, m), q) === " //
        + "ConditionalExpression(Piecewise({{m+b/InverseGammaRegularized(a,q)^(1/g),0<q<1}," //
        + "{m,q<=0}},Infinity),0<=q<=1)", //
        "True");
    check(
        "{InverseCDF(InverseGammaDistribution(2, 3), 0), InverseCDF(InverseGammaDistribution(2, 3), 1), " //
            + "InverseCDF(InverseGammaDistribution(2, 3, 2, 1), 0)}", //
        "{0,Infinity,1}");
    check("InverseCDF(InverseGammaDistribution(2, 3), 1/2)", //
        "3/InverseGammaRegularized(2,1/2)");
    check("Table(CDF(InverseGammaDistribution(2.5, 1.5, 0.8, 1), " //
        + "InverseCDF(InverseGammaDistribution(2.5, 1.5, 0.8, 1), q)) - q, {q, {0.001, 0.3, 0.9}}) // Chop", //
        "{0,0,0}");
    check("Quantile(InverseGammaDistribution(2.5, 1.5), {0.1, 0.3, 0.9})", //
        "{0.324803,0.494688,1.863}");
    // Kurtosis
    check("Kurtosis(InverseGammaDistribution(a, b))", //
        "Piecewise({{3+(-66+30*a)/((-4+a)*(-3+a)),a>4}},Indeterminate)");
    check("Kurtosis(InverseGammaDistribution(a, b, g, m)) === " //
        + "Piecewise({{(Gamma(a)^3*Gamma(a-4/g)-4*Gamma(a)^2*Gamma(a-3/g)*Gamma(a-1/g)" //
        + "+6*Gamma(a)*Gamma(a-2/g)*Gamma(a-1/g)^2-3*Gamma(a-1/g)^4)" //
        + "/(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2)^2,a*g>4}},Indeterminate)", //
        "True");
    check("{Kurtosis(InverseGammaDistribution(5, 2)), Kurtosis(InverseGammaDistribution(4, 2)), " //
        + "Kurtosis(InverseGammaDistribution(9/2, 1, 1, 0))}", //
        "{45,Indeterminate,95}");
    check("Module({d=InverseGammaDistribution(6.0, 1.5, 1.2, 0.5), m1, m2, m4}, " //
        + "m1=NIntegrate(x*PDF(d,x),{x,0.5,Infinity}); " //
        + "m2=NIntegrate((x-m1)^2*PDF(d,x),{x,0.5,Infinity}); " //
        + "m4=NIntegrate((x-m1)^4*PDF(d,x),{x,0.5,Infinity}); {m4/m2^2, Kurtosis(d)})", //
        "{13.11791,13.11791}");
    // machine numbers with a large shape: the Gamma values overflow a double and the central
    // moments cancel most of their digits (reference values from 80 digit arithmetic)
    check("{Mean(InverseGammaDistribution(1000.0, 1.5, 0.7, 2.0)), " //
        + "Variance(InverseGammaDistribution(1000.0, 1.5, 0.7, 2.0)), " //
        + "Skewness(InverseGammaDistribution(1000.0, 1.5, 0.7, 2.0)), " //
        + "Kurtosis(InverseGammaDistribution(1000.0, 1.5, 0.7, 2.0))}", //
        "{2.00008,1.23979*10^-11,0.167664,3.05218}");
    check("{Skewness(InverseGammaDistribution(1000000.0, 1.5, 0.7, 2.0)), " //
        + "Kurtosis(InverseGammaDistribution(1000000.0, 1.5, 0.7, 2.0))}", //
        "{0.00528573,3.00005}");
    check(
        "{Variance(InverseGammaDistribution(2.5, 1, 0.8, 0)), Kurtosis(InverseGammaDistribution(4.5, 1, 0.8, 0))}", //
        "{Indeterminate,Indeterminate}");
    // an exact large shape evaluates; N adds the digits which the central moments cancel: 60
    // digits give 3.17701661259482712864361153349405687903351...
    check("N(Kurtosis(InverseGammaDistribution(300, 3/2, 7/10, 2)), 40)", //
        "3.177016612594827128643611533494056879034");
  }

  @Test
  public void testGammaDistribution() {
    // PDF and CDF of the 4 argument form for machine numbers and a large shape, where Gamma(a)
    // overflows a double (reference values from 20 digit arithmetic)
    check("{PDF(GammaDistribution(150.0, 1.5, 0.7, 2.0), 1932.5), " //
        + "PDF(GammaDistribution(1000.0, 1.5, 0.7, 2.0), 28971.3)}", //
        "{0.00177043,0.000304806}");
    check("{CDF(GammaDistribution(200.0, 1.0, 1.0, 0.0), 200.0), " //
        + "CDF(GammaDistribution(1000.0, 1.5, 0.7, 2.0), 28971.3)}", //
        "{0.509403,0.506898}");
    check(
        "{PDF(GammaDistribution(200.0, 1.0, 1.0, 0.0), 200.0) - PDF(GammaDistribution(200.0, 1.0), 200.0), " //
            + "CDF(GammaDistribution(200.0, 1.0, 1.0, 0.0), 200.0) - CDF(GammaDistribution(200.0, 1.0), 200.0)} // Chop", //
        "{0,0}");
    check(
        "{PDF(GammaDistribution(2.0, 1.5, 0.05, 0.0), 10.0^19), CDF(GammaDistribution(2.0, 1.5, 0.05, 0.0), 10.0^19)}", //
        "{6.1431*10^-23,0.998432}");
    check(
        "{PDF(GammaDistribution(4.5, 1.5, 0.8, 2.0), 5.0), N(PDF(GammaDistribution(9/2, 3/2, 4/5, 2), 5)), " //
            + "PDF(GammaDistribution(4.5, 1.5, 0.8, 2.0), 1.0), CDF(GammaDistribution(4.5, 1.5, 0.8, 2.0), 1.0)}", //
        "{0.0487396,0.0487396,0,0}");
    // machine numbers in the 4 argument form: Gamma(a) overflows a double from a > 171 on and the
    // central moments cancel most of their digits (reference values from 80 digit arithmetic)
    check(
        "{Mean(GammaDistribution(1000.0, 1.5, 0.7, 2.0)), Kurtosis(GammaDistribution(1000.0, 1.5, 0.7, 2.0))}", //
        "{28971.33,3.01752}");
    check("{Mean(GammaDistribution(1000000.0, 1.5, 0.7, 2.0)), " //
        + "Kurtosis(GammaDistribution(1000000.0, 1.5, 0.7, 2.0))}", //
        "{5.59139*10^8,3.00002}");
    check(
        "{Mean(GammaDistribution(2.0, 1.5, 0.05, 0.0)), Kurtosis(GammaDistribution(2.0, 1.5, 0.05, 0.0))}", //
        "{7.66364*10^19,5.1803*10^21}");
    check(
        "N({Mean(GammaDistribution(9/2, 3/2, 4/5, 2)), Kurtosis(GammaDistribution(9/2, 3/2, 4/5, 2))}) - " //
            + "{Mean(GammaDistribution(4.5, 1.5, 0.8, 2.0)), Kurtosis(GammaDistribution(4.5, 1.5, 0.8, 2.0))} // Chop", //
        "{0,0}");
    check("Module({d=GammaDistribution(2.5, 1.5, 1.3, 0.5), m1, m2, m4}, " //
        + "m1=NIntegrate(x*PDF(d,x),{x,0.5,Infinity}); " //
        + "m2=NIntegrate((x-m1)^2*PDF(d,x),{x,0.5,Infinity}); " //
        + "m4=NIntegrate((x-m1)^4*PDF(d,x),{x,0.5,Infinity}); {m1, Mean(d), m4/m2^2, Kurtosis(d)})", //
        "{3.43353,3.43353,3.93407,3.93407}");
    check("CentralMoment(GammaDistribution(a, b),n)", //
        "b^n*Hypergeometric1F1(-n,1-a-n,-a)*Pochhammer(a,n)");
    check("CentralMoment(GammaDistribution(a, b),2)", //
        "a*b^2");
    check("Mean(GammaDistribution(a, b))", //
        "a*b");
    check("Mean(GammaDistribution(a, b, g, d))", //
        "d+(b*Gamma(a+1/g))/Gamma(a)");
    check("StandardDeviation(GammaDistribution(m, s))", //
        "Sqrt(m*s^2)");
    check("Variance(GammaDistribution(m, s))", //
        "m*s^2");
    check("CDF(GammaDistribution(a, b))", //
        "Function(#1,Piecewise({{GammaRegularized(a,0,#1/b),#1>0}},0),Listable)");
    check("CDF(GammaDistribution(a, b, g, d))", //
        "Function(#1,Piecewise({{GammaRegularized(a,0,((-d+#1)/b)^g),#1>d}},0),Listable)");
    check("PDF(GammaDistribution(a, b))", //
        "Function(#1,Piecewise({{1/(b^a*E^(#1/b)*Gamma(a)*#1^(1-a)),#1>0}},0),Listable)");
    check("PDF(GammaDistribution(a, b, g, d))", //
        "Function(#1,Piecewise({{g/(b*E^((-d+#1)/b)^g*Gamma(a)*((-d+#1)/b)^(1-a*g)),#1>d}},\n0),Listable)");
    check("Quantile(GammaDistribution(a, b), {1/4, 1/2, 3/4})", //
        "{b*InverseGammaRegularized(a,0,1/4),b*InverseGammaRegularized(a,0,1/2),b*InverseGammaRegularized(a,\n"
            + "0,3/4)}");
    check("Quantile(GammaDistribution(a, b, g, d), {1/4, 1/2, 3/4})", //
        "{d+b*InverseGammaRegularized(a,0,1/4)^(1/g),d+b*InverseGammaRegularized(a,0,1/2)^(1/g),d+b*InverseGammaRegularized(a,\n"
            + "0,3/4)^(1/g)}");
  }

  @Test
  public void testNormalDistribution() {
    // message: NormalDistribution: Parameter 0 at position 2 in NormalDistribution(m,0) is expected
    // to be positive.
    check("PDF(NormalDistribution(m,0), x)", //
        "PDF(NormalDistribution(m,0),x)");


    check("CentralMoment(NormalDistribution(a, b),n)", //
        "Piecewise({{b^n*(-1+n)!!,Mod(n,2)==0&&n>=0}},0)");
    check("CentralMoment(NormalDistribution(a, b),2)", //
        "b^2");
    check("CentralMoment(NormalDistribution(a, b),3)", //
        "0");
    check("Mean(NormalDistribution( ) )", //
        "0");
    check("Mean(NormalDistribution(m, s))", //
        "m");
    check("CDF(NormalDistribution( ), x)", //
        "Erfc(-x/Sqrt(2))/2");
    check("CDF(NormalDistribution(n,m), x)", //
        "Erfc((n-x)/(Sqrt(2)*m))/2");
    check("PDF(NormalDistribution( ), x)", //
        "1/(E^(x^2/2)*Sqrt(2*Pi))");
    check("PDF(NormalDistribution(m, s), x)", //
        "1/(E^((-m+x)^2/(2*s^2))*Sqrt(2*Pi)*s)");

    check("StandardDeviation(NormalDistribution(m, s))", //
        "s");
    check("Variance(NormalDistribution(m, s))", //
        "s^2");

    check("Quantile(NormalDistribution(m, s), q)", //
        "ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*q),0<=q<=1)");
    check("Quantile(NormalDistribution(2, 3), {1/4, 1/2, 3/4})", //
        "{2-3*Sqrt(2)*InverseErfc(1/2),2,2+3*Sqrt(2)*InverseErfc(1/2)}");
  }

  @Test
  public void testRandomVariateShape() {
    // Dimensions(RandomVariate(dist,spec)) == Join(spec,componentShape), where componentShape is
    // {} for a univariate and {n} for a multivariate distribution. Without a size specification
    // only the component itself is returned - a number, not a list of one number.
    check("Head(RandomVariate(NormalDistribution(0,1)))", //
        "Real");
    check("Head(RandomVariate(BinomialDistribution(10,1/2)))", //
        "Integer");
    check("Head(RandomVariate(PoissonDistribution(3)))", //
        "Integer");
    check("Dimensions(RandomVariate(NormalDistribution(0,1),1))", //
        "{1}");
    check("Dimensions(RandomVariate(NormalDistribution(0,1),0))", //
        "{0}");
    check("Dimensions(RandomVariate(NormalDistribution(0,1),{3}))", //
        "{3}");
    // a multivariate distribution keeps its component axis
    check("Dimensions(RandomVariate(MultinormalDistribution({0,0},IdentityMatrix(2))))", //
        "{2}");
    check("Dimensions(RandomVariate(MultinormalDistribution({0,0},IdentityMatrix(2)),3))", //
        "{3,2}");
    check("Dimensions(RandomVariate(MultinormalDistribution({0,0},IdentityMatrix(2)),{2,3}))", //
        "{2,3,2}");
    // a general covariance matrix in any dimension
    check(
        "Dimensions(RandomVariate(MultinormalDistribution({1,2,3},{{2,1/2,0},{1/2,1,1/4},{0,1/4,3}}),5))", //
        "{5,3}");
    check("Dimensions(RandomVariate(MultinormalDistribution({{1,1/2},{1/2,1}}),4))", //
        "{4,2}");
    check("SeedRandom(42);s=RandomVariate(MultinormalDistribution({1,-2},{{2,3/2},{3/2,3}}),5000);" //
        + "{Max(Abs(Mean(s)-{1,-2}))<0.1, Max(Abs(Flatten(Covariance(s)-{{2,3/2},{3/2,3}})))<0.25}", //
        "{True,True}");
    // MultinormalDistribution: The value {{1,2},{2,1}} at position 2 in
    // MultinormalDistribution({0,0},{{1,2},{2,1}}) is expected to be a symmetric positive definite
    // matrix
    check("RandomVariate(MultinormalDistribution({0,0},{{1,2},{2,1}}),3)", //
        "RandomVariate(MultinormalDistribution({0,0},\n{{1,2},\n {2,1}}),3)");
    // asymmetric
    check("RandomVariate(MultinormalDistribution({0,0},{{1,0},{1,1}}),3)", //
        "RandomVariate(MultinormalDistribution({0,0},\n{{1,0},\n {1,1}}),3)");
    // singular
    check("RandomVariate(MultinormalDistribution({{1,1},{1,1}}))", //
        "RandomVariate(MultinormalDistribution(\n{{1,1},\n {1,1}}))");
    check("Dimensions(RandomVariate(BinormalDistribution(1/2)))", //
        "{2}");
    check("Dimensions(RandomVariate(BinormalDistribution(1/2),3))", //
        "{3,2}");
  }

  @Test
  public void testGompertzMakehamDistributionRandomVariate() {
    // RandomVariate() has to draw `size` deviates, not a single one
    check("Length(RandomVariate(GompertzMakehamDistribution(2,3), 20))", //
        "20");
    check("Dimensions(RandomVariate(GompertzMakehamDistribution(2,3), {2,3}))", //
        "{2,3}");
    check("Length(RandomVariate(GompertzMakehamDistribution(2,3), 0))", //
        "0");
    // the distribution is supported on x >= 0
    check("Min(RandomVariate(GompertzMakehamDistribution(2,3), 100))>=0", //
        "True");
    check("Head(RandomVariate(GompertzMakehamDistribution(2,3)))", //
        "Real");
  }

  @Test
  public void testVonMisesDistribution() {
    // Piecewise[{{E^(2*Cos[1-x])/(2*Pi*BesselI[0,2]), 1-Pi <= x <= 1+Pi}}, 0]
    check("PDF(VonMisesDistribution(m,k),x)", //
        "Piecewise({{E^(k*Cos(m-x))/(2*Pi*BesselI(0,k)),m-Pi<=x<=m+Pi}},0)");
    check("Mean(VonMisesDistribution(m,k))", //
        "m");
    check("Median(VonMisesDistribution(m,k))", //
        "m");
    // the density is symmetric around the mean direction
    check("Skewness(VonMisesDistribution(m,k))", //
        "0");
    check("N(PDF(VonMisesDistribution(1,2),1.5))", //
        "0.403853");
    // outside m-Pi < x < m+Pi the density is 0
    check("N(PDF(VonMisesDistribution(1,2),5.0))", //
        "0.0");
    // no closed form CDF - the quadrature has to agree with integrating the density
    check("N(CDF(VonMisesDistribution(1,2),1))", //
        "0.5");
    check("N(CDF(VonMisesDistribution(1,2),1.5))", //
        "0.738192");
    check("N(NIntegrate(PDF(VonMisesDistribution(1,2),x),{x,1-Pi,1.5}))", //
        "0.738192");
    check("N(NIntegrate(PDF(VonMisesDistribution(1,2),x),{x,1-Pi,1+Pi}))", //
        "1.0");
    check("N(InverseCDF(VonMisesDistribution(1,2),1/2))", //
        "1.0");
    // the inverse round trips, also for a sharply concentrated distribution
    check("N(CDF(VonMisesDistribution(1,2), InverseCDF(VonMisesDistribution(1,2), 0.3)))", //
        "0.3");
    check("N(CDF(VonMisesDistribution(0,50), InverseCDF(VonMisesDistribution(0,50), 0.75)))", //
        "0.75");
    check("Length(RandomVariate(VonMisesDistribution(1,2),100))", //
        "100");
    check(
        "Module({s=RandomVariate(VonMisesDistribution(1,2),2000)},"
            + " {Min(s)>1-Pi, Max(s)<1+Pi})", //
        "{True,True}");
  }

  @Test
  public void testVarianceNumericFallback() {
    // VonMisesDistribution has no closed form variance, so it is integrated against the density.
    // Pi^2/3 + 4*Sum((-1)^j*BesselI(j,k)/(j^2*BesselI(0,k)),{j,1,Infinity}) gives 0.764456 for k==2
    check("N(Variance(VonMisesDistribution(1,2)))", //
        "0.764462");
    check("N(StandardDeviation(VonMisesDistribution(1,2)))", //
        "0.874335");
    // for a small concentration it approaches the uniform distribution with variance Pi^2/3
    check("N(Variance(VonMisesDistribution(0,1/100)))", //
        "3.26988");
    check("N(Pi^2/3)", //
        "3.28987");
    // symbolic parameters keep returning unevaluated instead of a number
    check("N(Variance(VonMisesDistribution(m,k)))", //
        "Variance(VonMisesDistribution(m,k))");
    check("Variance(VonMisesDistribution(1,2))", //
        "Variance(VonMisesDistribution(1,2))");
    // a closed form always wins over the fallback
    check("Variance(NormalDistribution(m,s))", //
        "s^2");
    check("StandardDeviation(NormalDistribution(m,s))", //
        "s");
    check("Variance(BinomialDistribution(10,1/2))", //
        "5/2");
  }

  @Test
  public void testTruncatedDistribution() {
    check("TruncatedDistribution({0,1}, NormalDistribution())", //
        "TruncatedDistribution({0,1},NormalDistribution(0,1))");
    // the density is renormalized by CDF(dist,max)-CDF(dist,min) == 0.341345
    check("N(PDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), 0.5))", //
        "1.03141");
    check("N(CDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), 0.5))", //
        "0.560906");
    check("N(CDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), -1))", //
        "0.0");
    check("N(CDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), 2))", //
        "1.0");
    check("N(NIntegrate(PDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), x), {x,0,1}))", //
        "1.0");
    check("Mean(TruncatedDistribution({0,1}, NormalDistribution(0,1)))", //
        "(1/Sqrt(2*Pi)-1/Sqrt(2*E*Pi))/(-1/2+Erfc(-1/Sqrt(2))/2)");
    check("N(Mean(TruncatedDistribution({0,1}, NormalDistribution(0,1))))", //
        "0.459862");
    check("N(Variance(TruncatedDistribution({0,1}, NormalDistribution(0,1))))", //
        "0.0796518");
    check("N(Median(TruncatedDistribution({0,1}, NormalDistribution(0,1))))", //
        "0.441771");
    check("N(InverseCDF(TruncatedDistribution({0,1}, NormalDistribution(0,1)), 1/2))", //
        "0.441771");
    // an infinite bound is allowed: truncating a normal at the mean gives -PDF(0)/(1/2)
    check("N(Mean(TruncatedDistribution({-Infinity,0}, NormalDistribution(0,1))))", //
        "-0.797885");
    // the exponential distribution is memoryless, so truncating from below shifts it by 1
    check(
        "NExpectation(x, Distributed(x, TruncatedDistribution({1,Infinity}, ExponentialDistribution(2))))", //
        "1.5");
    check(
        "NExpectation(x, Distributed(x, TruncatedDistribution({0,Infinity}, ExponentialDistribution(2))))", //
        "0.5");
    check("NExpectation(x, Distributed(x, TruncatedDistribution({0,1}, NormalDistribution(0,1))))", //
        "0.459862");
    check("Length(RandomVariate(TruncatedDistribution({0,1}, NormalDistribution(0,1)), 50))", //
        "50");
    check(
        "Module({s=RandomVariate(TruncatedDistribution({0,1},NormalDistribution(0,1)),200)},"
            + " {Min(s)>=0, Max(s)<=1})", //
        "{True,True}");
    // a discrete distribution keeps the values min < x <= max
    check("PDF(TruncatedDistribution({1,5}, PoissonDistribution(3)), x)", //
        "Piecewise({{5/(8*3^(2-x)*x!),1<x<=5}},0)");
  }

  @Test
  public void testExpectationFromMoments() {
    // a distribution which only provides Mean/Variance/Skewness gets its first three raw moments
    // generically - it doesn't need the symbolic Integrate of expr*pdf to succeed
    check("Expectation(x, Distributed(x, GompertzMakehamDistribution(2,3)))", //
        "1/2*E^3*Gamma(0,3)");
    check("Expectation(x, Distributed(x, GumbelDistribution(2,3)))", //
        "2-3*EulerGamma");
    check("Expectation(x, Distributed(x, FrechetDistribution(3,2)))", //
        "2*Gamma(2/3)");
    // the distributions which bring their own Moment() are unaffected
    check("Expectation(x, Distributed(x, NormalDistribution(m,s)))", //
        "m");
    check("Expectation(x^2, Distributed(x, NormalDistribution(m,s)))", //
        "m^2+s^2");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testNExpectationEffectiveSupport() {
    // the density decays doubly exponentially, so quadrature over the full support samples the
    // far tail where the density evaluates to Overflow() instead of 0 - fall back to the interval
    // which carries the probability mass. Reference values from quadrature of the density:
    // 0.131041870128, 0.0289120734266, 0.00836207937093
    check("NExpectation(x, Distributed(x, GompertzMakehamDistribution(2,3)))", //
        "0.131042");
    check("NExpectation(x^2, Distributed(x, GompertzMakehamDistribution(2,3)))", //
        "0.0289121");
    check("NExpectation(x^3, Distributed(x, GompertzMakehamDistribution(2,3)))", //
        "0.00836208");
    // a non polynomial expression has no moment shortcut and needs the quadrature
    check("NExpectation(Sin(x), Distributed(x, GompertzMakehamDistribution(2,3)))", //
        "0.129658");
    check("NExpectation(x, Distributed(x, GumbelDistribution(2,3)))", //
        "0.268353");
    check("NExpectation(x, Distributed(x, FrechetDistribution(3,2)))", //
        "2.70824");
    // quadrature over the full support still runs first where it works
    check("NExpectation(x, Distributed(x, NormalDistribution(0,1)))", //
        "0.0");
    check("NExpectation(x^2, Distributed(x, NormalDistribution(0,1)))", //
        "1.0");
    check("NExpectation(x, Distributed(x, ExponentialDistribution(2)))", //
        "0.5");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testGompertzMakehamDistributionSkewness() {
    check("Skewness(GompertzMakehamDistribution(m,n))", //
        "(2*E^(3*n)*Gamma(0,n)^3-3*E^(2*n)*Gamma(0,n)*(Pi^2/6-2*n*HypergeometricPFQ({1,1,\n"
            + "1},{2,2,2},-n)+(EulerGamma+Log(n))^2)+E^n*(6*n*HypergeometricPFQ({1,1,1,1},{2,2,\n"
            + "2,2},-n)+1/2*Pi^2*(-EulerGamma-Log(n))-(EulerGamma+Log(n))^3-2*Zeta(3)))/(-E^(2*n)*Gamma(\n"
            + "0,n)^2+E^n*(Pi^2/6-2*n*HypergeometricPFQ({1,1,1},{2,2,2},-n)+(EulerGamma+Log(n))^\n"
            + "2))^(3/2)");
    // the rate parameter is a pure scale parameter and cancels
    check("FreeQ(Skewness(GompertzMakehamDistribution(m,n)), m)", //
        "True");
    // quadrature of the density gives 1.17643568158 for m=2, n=3
    // 45 digits give 1.17643568157490182729191058717879...
    check("N(Skewness(GompertzMakehamDistribution(2,3)),25)", //
        "1.176435681574901827291911");
    check("N(Skewness(GompertzMakehamDistribution(2,3)))", //
        "1.17644");
    // the skewness is negative for a small shape parameter (-0.351639884786 from quadrature)
    check("N(Skewness(GompertzMakehamDistribution(1,0.05)))", //
        "-0.35164");
  }

  @Test
  public void testGompertzMakehamDistributionNumericMoments() {
    // the closed forms contain HypergeometricPFQ({1,..,1},{2,..,2},-n), which is summed numerically
    check("N(Mean(GompertzMakehamDistribution(2,3)))", //
        "0.131042");
    // 0.0117398 from quadrature of the density
    check("N(Variance(GompertzMakehamDistribution(2,3)))", //
        "0.0117401");
    check("N(StandardDeviation(GompertzMakehamDistribution(2,3)))", //
        "0.108352");
  }

  @Test
  public void testGumbelDistribution() {
    check("Mean(GumbelDistribution())", //
        "-EulerGamma");
    check("Mean(GumbelDistribution(a,b))", //
        "a-b*EulerGamma");
    check("Quantile(GumbelDistribution(), {1/4, 1/2, 3/4})", //
        "{Log(Log(4/3)),Log(Log(2)),Log(Log(4))}");
    check("Quantile(GumbelDistribution(a,b), {1/4, 1/2, 3/4})", //
        "{a+b*Log(Log(4/3)),a+b*Log(Log(2)),a+b*Log(Log(4))}");
  }


  @Test
  public void testKurtosis() {
    check("Kurtosis({1.1, 1.2, 1.4, 2.1, 2.4})", //
        "1.42098");

    // message Kurtosis: The argument {x} should have at least 2 arguments.
    check("Kurtosis({x})", //
        "Kurtosis({x})");
    check("Kurtosis({x,y})", //
        "(2*((x+1/2*(-x-y))^4+(1/2*(-x-y)+y)^4))/((x+1/2*(-x-y))^2+(1/2*(-x-y)+y)^2)^2");

    check("Kurtosis({0,0})", //
        "Indeterminate");
    check("Kurtosis({1.1, 1.2, 1.4, 2.1, 2.4})", //
        "1.42098");
    check("Kurtosis(BernoulliDistribution(a))", //
        "3+(1-6*(1-a)*a)/((1-a)*a)");
    check("Kurtosis(BinomialDistribution(10^3,0.5))", //
        "2.998");
    check("Kurtosis(ChiSquareDistribution(n))", //
        "3+12/n");
    check("Kurtosis(GammaDistribution(a,b))", //
        "3+6/a");
    check("Kurtosis(NormalDistribution())", //
        "3");
    check("Kurtosis(NormalDistribution(a,b))", //
        "3");
    check("Kurtosis(LogNormalDistribution(a,b))", //
        "-3+3*E^(2*b^2)+2*E^(3*b^2)+E^(4*b^2)");
    check("Kurtosis(GeometricDistribution(a))", //
        "3+(6-6*a+a^2)/(1-a)");
  }

  @Test
  public void testDavisDistribution() {
    check("PDF(DavisDistribution(b, n, mu), x)", //
        "Piecewise({{b^n/(E^(b/(-mu+x))*(1-1/E^(b/(-mu+x)))*(-mu+x)^(1+n)*Gamma(n)*Zeta(n)),"
            + "x>mu}},\n" //
            + "0)");
    check("Mean(DavisDistribution(b, n, mu))", //
        "Piecewise({{mu+(b*Zeta(-1+n))/((-1+n)*Zeta(n)),n>2}},Infinity)");
    check("Mean(DavisDistribution(2, 4, 1))", //
        "1+(60*Zeta(3))/Pi^4");
    check("Variance(DavisDistribution(2, 5, 1))", //
        "4*(-Pi^8/(129600*Zeta(5)^2)+Zeta(3)/(12*Zeta(5)))");
    check("Moment(DavisDistribution(b, 5, mu), 2)", //
        "mu^2+(b*mu*Pi^4)/(180*Zeta(5))+(b^2*Zeta(3))/(12*Zeta(5))");
    // the moments of the orders k >= n - 1 are infinite
    check("Mean(DavisDistribution(1, 2, 0))", //
        "Infinity");
    check("Variance(DavisDistribution(1, 3, 0))", //
        "Infinity");
    check("CentralMoment(DavisDistribution(b, n, mu), 1)", //
        "Piecewise({{0,n>2}},Indeterminate)");

    // the moments agree with the integrals of the density
    check("NIntegrate(PDF(DavisDistribution(2, 3.5, 1), x), {x, 1, Infinity})", //
        "1.0");
    check("NIntegrate(PDF(DavisDistribution(1, 1.5, 0), x), {x, 0, Infinity})", //
        "1.0");
    check("NIntegrate((x-N(Mean(DavisDistribution(2, 6, 1))))^3*"
        + "PDF(DavisDistribution(2, 6, 1), x), {x, 1, Infinity})", //
        "0.0328328");
    check("N(CentralMoment(DavisDistribution(2, 6, 1), 3))", //
        "0.0328328");
    check("NIntegrate((x-N(Mean(DavisDistribution(2, 7, 1))))^4*"
        + "PDF(DavisDistribution(2, 7, 1), x), {x, 1, Infinity})", //
        "0.0113913");
    check("N({CentralMoment(DavisDistribution(2, 7, 1), 4), Skewness(DavisDistribution(2, 6, 1)), "
        + "Kurtosis(DavisDistribution(2, 7, 1))})", //
        "{0.0113913,3.26859,19.75976}");

    // CDF and SurvivalFunction are evaluated numerically, and agree with NIntegrate of the density
    check("CDF(DavisDistribution(2, 3.5, 1), {0.5, 1.0, 1.2, 1.5, 2.0, 3.0, 10.0, 100.0})", //
        "{0.0,0.0,0.00494331,0.297176,0.720262,0.926439,0.997705,0.999994}");
    check("NIntegrate(PDF(DavisDistribution(2, 3.5, 1), x), {x, 1, 1.5})", //
        "0.297176");
    check("NIntegrate(PDF(DavisDistribution(2, 3.5, 1), x), {x, 1, 3.0})", //
        "0.926439");
    check("SurvivalFunction(DavisDistribution(2, 3.5, 1), {1.5, 10.0, 100.0, 10.0^6})", //
        "{0.702824,0.00229507,6.15195*10^-6,6.0428*10^-16}");
    check("NIntegrate(PDF(DavisDistribution(2, 3.5, 1), x), {x, 100, Infinity})", //
        "6.15195*10^-6");
    check("CDF(DavisDistribution(1, 1.5, 0), {0.01, 0.5, 5.0})", //
        "{1.61483*10^-43,0.106919,0.626285}");
    check("NIntegrate(PDF(DavisDistribution(1, 1.5, 0), x), {x, 0, 0.5})", //
        "0.106919");
    check("CDF(DavisDistribution(1, 1.001, 0), 0.5)", //
        "0.000145565");
    check("SurvivalFunction(DavisDistribution(1, 5, 0), {0.1, 0.3})", //
        "{0.971789,0.264131}");
    check("NIntegrate(PDF(DavisDistribution(1, 5, 0), x), {x, 0.3, Infinity})", //
        "0.264131");
    // a small survival probability for a large shape n isn't lost in 1 - CDF
    check("SurvivalFunction(DavisDistribution(1, 50, 0), {0.02, 0.05, 0.1})", //
        "{0.518808,1.24589*10^-8,1.85484*10^-19}");
    check("CDF(DavisDistribution(2, 3, 1/2), 3.0)", //
        "0.898865");
    check("N(CDF(DavisDistribution(2, 3, 1), 3))", //
        "0.852778");
    check("CDF(DavisDistribution(2, 3, 1), x)", //
        "CDF(DavisDistribution(2,3,1),x)");

    check("Length(RandomVariate(DavisDistribution(2, 3.5, 1), 10))", //
        "10");
    // the mean is 1.4077
    check("Abs(Mean(RandomVariate(DavisDistribution(2, 6, 1), 10^4)) - 1.4077) < 0.02", //
        "True");
    // the median is between 1.6 and 1.75
    check("{CDF(DavisDistribution(2, 3.5, 1), 1.6), CDF(DavisDistribution(2, 3.5, 1), 1.75)}", //
        "{0.417317,0.56226}");
    check("1.6 < Median(RandomVariate(DavisDistribution(2, 3.5, 1), 10^4)) < 1.75", //
        "True");

    check("DistributionParameterAssumptions(DavisDistribution(b, n, mu))", //
        "b>0&&n>1&&mu∈Reals");
    check("DistributionParameterQ(DavisDistribution(1, 1, 0))", //
        "False");
    // message: DavisDistribution: Parameter -1 at position 1 in DavisDistribution(-1,3,0) is
    // expected to be positive.
    check("PDF(DavisDistribution(-1, 3, 0), x)", //
        "PDF(DavisDistribution(-1,3,0),x)");
    // the density is not normalizable for n <= 1
    check("PDF(DavisDistribution(1, 1/2, 0), x)", //
        "PDF(DavisDistribution(1,1/2,0),x)");
  }

  @Test
  public void testKDistribution() {
    check("PDF(KDistribution(nu, w), x)", //
        "Piecewise({{(4*(nu/w)^(1/2*(1+nu))*x^nu*BesselK(-1+nu,2*Sqrt(nu/w)*x))/Gamma(nu),x>\n"
            + "0}},0)");
    check("CDF(KDistribution(nu, w), x)", //
        "Piecewise({{1+(-2*(Sqrt(nu/w)*x)^nu*BesselK(nu,2*Sqrt(nu/w)*x))/Gamma(nu),x>0}},\n"
            + "0)");
    check("SurvivalFunction(KDistribution(nu, w), x)", //
        "Piecewise({{(2*(Sqrt(nu/w)*x)^nu*BesselK(nu,2*Sqrt(nu/w)*x))/Gamma(nu),x>0}},1)");
    check("Mean(KDistribution(nu, w))", //
        "(Sqrt(Pi)*Sqrt(w)*Pochhammer(nu,1/2))/(2*Sqrt(nu))");
    check("Variance(KDistribution(nu, w))", //
        "w*(1+(-Pi*Pochhammer(nu,1/2)^2)/(4*nu))");
    check("Moment(KDistribution(nu, w), 3)", //
        "3/4*Sqrt(Pi)*(w/nu)^(3/2)*Pochhammer(nu,3/2)");
    // w is the mean square
    check("Expectation(x^2, x \\[Distributed] KDistribution(nu, w))", //
        "w");
    check("CDF(KDistribution(2, 3), x)", //
        "Piecewise({{1-4/3*x^2*BesselK(2,2*Sqrt(2/3)*x),x>0}},0)");
    check("Mean(KDistribution(2, 3))", //
        "3/8*Sqrt(3/2)*Pi");
    check("Variance(KDistribution(2, 3))", //
        "3*(1-9/128*Pi^2)");

    // the moments agree with the integrals of the density
    check("NIntegrate(PDF(KDistribution(2.5, 3), x), {x, 0, Infinity})", //
        "1.0");
    check("NIntegrate((x-Mean(KDistribution(2.5, 3)))^4*PDF(KDistribution(2.5, 3), x), "
        + "{x, 0, Infinity})", //
        "3.86667");
    check("N({CentralMoment(KDistribution(2.5, 3), 3), " //
        + "CentralMoment(KDistribution(2.5, 3), 4)})", //
        "{0.973729,3.86667}");
    check("N({Skewness(KDistribution(2.5, 3)), Kurtosis(KDistribution(2.5, 3))})", //
        "{1.20687,5.14793}");
    // for nu -> Infinity the limit is the RayleighDistribution
    check("N({Skewness(KDistribution(10^6, 1)), Kurtosis(KDistribution(10^6, 1))})", //
        "{0.631113,3.2451}");

    check("PDF(KDistribution(2.5, 3), {0.5, 1.0, 2.0})", //
        "{0.426539,0.505802,0.268262}");
    check("CDF(KDistribution(2.5, 3), {-1.0, 0.5, 1.0, 2.0})", //
        "{0,0.120739,0.365781,0.763937}");
    check("{PDF(KDistribution(2, 3), 1.5), CDF(KDistribution(2, 3), 1.5)}", //
        "{0.385516,0.608643}");
    check("SurvivalFunction(KDistribution(2, 3), 40.0)", //
        "1.45872*10^-26");

    check("Length(RandomVariate(KDistribution(2, 3), 10))", //
        "10");
    check("Abs(Mean(RandomVariate(KDistribution(2, 3), 10^4)^2) - 3) < 0.3", //
        "True");

    check("DistributionParameterAssumptions(KDistribution(nu, w))", //
        "nu>0&&w>0");
    // message: KDistribution: Parameter -2 at position 1 in KDistribution(-2,3) is expected to be
    // positive.
    check("PDF(KDistribution(-2, 3), x)", //
        "PDF(KDistribution(-2,3),x)");
    check("RandomVariate(KDistribution(2, -3))", //
        "RandomVariate(KDistribution(2,-3))");
  }

  @Test
  public void testLogNormalDistribution() {
    check("Mean(LogNormalDistribution(m,d))", //
        "E^(d^2/2+m)");
    check("Quantile(LogNormalDistribution(m,d), {1/4, 1/2, 3/4})", //
        "{E^(m-Sqrt(2)*d*InverseErfc(1/2)),E^m,E^(m+Sqrt(2)*d*InverseErfc(1/2))}");
  }

  @Test
  public void testLogisticDistribution() {
    check("PDF(LogisticDistribution(a,b), x)", //
        "E^((a-x)/b)/(b*(1+E^((a-x)/b))^2)");
    check("CDF(LogisticDistribution(a,b), x)", //
        "1/(1+E^((a-x)/b))");
    check("InverseCDF(LogisticDistribution(a,b), p)", //
        "ConditionalExpression(Piecewise({{a+b*Log(p/(1-p)),0<p<1},{-Infinity,p<=0}},Infinity),\n"
            + "0<=p<=1)");
    check("Mean(LogisticDistribution(a,b))", //
        "a");
    check("Median(LogisticDistribution(a,b))", //
        "a");
    check("Variance(LogisticDistribution(a,b))", //
        "1/3*b^2*Pi^2");
    check("StandardDeviation(LogisticDistribution(a,b))", //
        "(b*Pi)/Sqrt(3)");
    check("Skewness(LogisticDistribution(a,b))", //
        "0");
    // Kurtosis is not the excess kurtosis: 3 + 6/5
    check("Kurtosis(LogisticDistribution(a,b))", //
        "21/5");

    // LogisticDistribution() is LogisticDistribution(0, 1)
    check("PDF(LogisticDistribution(), x)", //
        "1/(E^x*(1+E^(-x))^2)");
    check("CDF(LogisticDistribution(), x)", //
        "1/(1+E^(-x))");
    check("Mean(LogisticDistribution())", //
        "0");
    check("Variance(LogisticDistribution())", //
        "Pi^2/3");

    // the median is the 1/2 quantile and CDF and InverseCDF are inverse to each other
    check("CDF(LogisticDistribution(0,1), 0)", //
        "1/2");
    check("Quantile(LogisticDistribution(0,1), 1/2)", //
        "0");
    check("N(CDF(LogisticDistribution(1,2), 3))", //
        "0.731059");

    // the scale parameter must be positive
    check("DistributionParameterQ(LogisticDistribution(0,2))", //
        "True");
    check("DistributionParameterQ(LogisticDistribution(0,-2))", //
        "False");
    check("Length(RandomVariate(LogisticDistribution(0,1), 20))", //
        "20");
  }

  @Test
  public void testLogisticDistributionGeneratingFunctions() {
    // the Gamma function form evaluates to 1 at t == 0 and can be differentiated symbolically
    check("MomentGeneratingFunction(LogisticDistribution(a,b), t)", //
        "E^(a*t)*Gamma(1+b*t)*Gamma(1-b*t)");
    check("MomentGeneratingFunction(LogisticDistribution(a,b), 0)", //
        "1");
    check("CharacteristicFunction(LogisticDistribution(a,b), t)", //
        "E^(I*a*t)*Gamma(1-I*b*t)*Gamma(1+I*b*t)");
    check("CharacteristicFunction(LogisticDistribution(a,b), 0)", //
        "1");
    check("CumulantGeneratingFunction(LogisticDistribution(a,b), t)", //
        "a*t+Log(Gamma(1+b*t))+Log(Gamma(1-b*t))");
    check("CentralMomentGeneratingFunction(LogisticDistribution(a,b), t)", //
        "Gamma(1+b*t)*Gamma(1-b*t)");
    // the first derivative at 0 is the mean, the second derivative of the cumulant generating
    // function at 0 is the variance
    check("ReplaceAll(D(MomentGeneratingFunction(LogisticDistribution(a,b), t), t), t->0)", //
        "a");
    check("ReplaceAll(D(CumulantGeneratingFunction(LogisticDistribution(a,b), t), {t,2}), t->0)", //
        "1/3*b^2*Pi^2");
    // numeric cross check of the moment generating function
    check("N(MomentGeneratingFunction(LogisticDistribution(1,1/2), 1/5))", //
        "1.24173");
    check("NExpectation(E^(0.2*x), Distributed(x, LogisticDistribution(1,0.5)))", //
        "1.24173");
  }

  @Test
  public void testLogisticDistributionExpectation() {
    // the moments are used for polynomial expressions
    check("Expectation(2*x+3, Distributed(x, LogisticDistribution(a,b)))", //
        "3+2*a");
    check("Expectation(x^2, Distributed(x, LogisticDistribution(a,b)))", //
        "a^2+1/3*b^2*Pi^2");
    check("NExpectation(x, Distributed(x, LogisticDistribution(2,3)))", //
        "2.0");
    check("NExpectation(x^2, Distributed(x, LogisticDistribution(0,1)))", //
        "3.28987");
    // the probability is computed from the CDF
    check("Probability(x < 1, Distributed(x, LogisticDistribution(0,1)))", //
        "1/(1+1/E)");
    check("NProbability(x < 1, Distributed(x, LogisticDistribution(0,1)))", //
        "0.731059");
    // the moment generating function converges only for Abs(t) < 1/b, so the formal value must
    // not be used here
    check("Expectation(E^(2*x), Distributed(x, LogisticDistribution(0,1)))", //
        "Integrate(E^(2*x)/(E^x*(1+E^(-x))^2),{x,-Infinity,Infinity})");
  }

  @Test
  public void testBinormalDistribution() {
    check("Skewness(BinormalDistribution(p))", //
        "{0,0}");
    check("Skewness(BinormalDistribution({s1,s2},p))", //
        "{0,0}");
    check("Skewness(BinormalDistribution({m1, m2}, {s1,s2},p))", //
        "{0,0}");

    check("Mean(BinormalDistribution(p))", //
        "{0,0}");
    check("Mean(BinormalDistribution({s1,s2},p))", //
        "{0,0}");
    check("Mean(BinormalDistribution({m1, m2}, {s1,s2},p))", //
        "{m1,m2}");
    check("Variance(BinormalDistribution(p))", //
        "{1,1}");
    check("Variance(BinormalDistribution({s1,s2},p))", //
        "{s1^2,s2^2}");
    check("Variance(BinormalDistribution({m1, m2}, {s1,s2},p))", //
        "{s1^2,s2^2}");

    check("Covariance(BinormalDistribution(r))", //
        "{{1,r},{r,1}}");
    check("Covariance(BinormalDistribution({s1,s2},r))", //
        "{{s1^2,r*s1*s2},{r*s1*s2,s2^2}}");
    check("Covariance(BinormalDistribution({m1,m2},{s1,s2},r))", //
        "{{s1^2,r*s1*s2},{r*s1*s2,s2^2}}");
    check("Dimensions(RandomVariate(BinormalDistribution({2,3},1/2),4))", //
        "{4,2}");
    check("Dimensions(RandomVariate(BinormalDistribution({1,-1},{2,3},1/3),5))", //
        "{5,2}");
  }

  @Test
  public void testMultivariateTDistributionMoments() {
    check("Variance(MultivariateTDistribution({1,2},{{2,1},{1,3}},nu))", //
        "Piecewise({{{(2*nu)/(-2+nu),(3*nu)/(-2+nu)},nu>2}},Indeterminate)");
    check("Kurtosis(MultivariateTDistribution({{2,1},{1,3}},nu))", //
        "Piecewise({{{3+6/(-4+nu),3+6/(-4+nu)},nu>4}},Indeterminate)");
    check("Skewness(MultivariateTDistribution({1,2},{{2,1},{1,3}},nu))", //
        "Piecewise({{{0,0},nu>3}},Indeterminate)");
    // the location is no vector
    check("Kurtosis(MultivariateTDistribution(m,{{2,1},{1,3}},nu))", //
        "Kurtosis(MultivariateTDistribution(m,{{2,1},{1,3}},nu))");
  }

  @Test
  public void testMultinormalDistribution() {
    check("s = {{s1^2, p*s1*s2}, {p*s1*s2, s2^2}};", //
        "");
    check("Mean(MultinormalDistribution({m1, m2}, s))", //
        "{m1,m2}");
    check("Variance(MultinormalDistribution({m1, m2}, s))", //
        "{s1^2,s2^2}");
    check("Skewness(MultinormalDistribution({m1, m2}, s))", //
        "{0,0}");
    check("Skewness(MultinormalDistribution(s))", //
        "{0,0}");
    // a covariance matrix given as a SparseArray
    check("Variance(MultinormalDistribution({1,2}, SparseArray({{4,1},{1,9}})))", //
        "{4,9}");
  }

  @Test
  public void testNakagamiDistribution() {
    check("Mean(NakagamiDistribution(m,w))", //
        "(Sqrt(w)*Pochhammer(m,1/2))/Sqrt(m)");
    check("Variance(NakagamiDistribution(m,w))", //
        "w+(-w*Pochhammer(m,1/2)^2)/m");
    check("Quantile(NakagamiDistribution(m,w), {1/4, 1/2, 3/4})", //
        "{Sqrt((w*InverseGammaRegularized(m,0,1/4))/m),Sqrt((w*InverseGammaRegularized(m,\n"
            + "0,1/2))/m),Sqrt((w*InverseGammaRegularized(m,0,3/4))/m)}");
  }

  @Test
  public void testParetoDistribution() {
    check("CDF(ParetoDistribution(k,a))", //
        "Function(#1,Piecewise({{1-(k/#1)^a,#1>=k}},0),Listable)");
    check("CDF(ParetoDistribution(k,a,m))", //
        "Function(#1,Piecewise({{1-1/(1+(-m+#1)/k)^a,#1>=m}},0),Listable)");
    check("CDF(ParetoDistribution(k,a,g,m))", //
        "Function(#1,Piecewise({{1-1/(1+((-m+#1)/k)^(1/g))^a,#1>=m}},0),Listable)");

    check("InverseCDF(ParetoDistribution(k,a))", //
        "Function(#1,ConditionalExpression(Piecewise({{k/(1-#1)^(1/a),#1<1}},Infinity),0<=#1<=\n1),Listable)");
    check("InverseCDF(ParetoDistribution(k,a,m))", //
        "Function(#1,ConditionalExpression(Piecewise({{m+k*(-1+(1-#1)^(-1/a)),0<#1<1},{m,#1<=\n0}},Infinity),0<=#1<=1),Listable)");
    check("InverseCDF(ParetoDistribution(k,a,g,m))", //
        "Function(#1,ConditionalExpression(Piecewise({{m+k*(-1+(1-#1)^(-1/a))^g,0<#1<1},{m,#1<=\n0}},Infinity),0<=#1<=1),Listable)");

    check("PDF(ParetoDistribution(k,a))", //
        "Function(#1,Piecewise({{(a*k^a)/#1^(1+a),#1>=k}},0),Listable)");
    check("PDF(ParetoDistribution(k,a,m))", //
        "Function(#1,Piecewise({{a/(k*((k-m+#1)/k)^(1+a)),#1>=m}},0),Listable)");
    check("PDF(ParetoDistribution(k,a,g,m))", //
        "Function(#1,Piecewise({{a/(g*k^(1/g)*(-m+#1)^(1-1/g)*(1+(k/(-m+#1))^(-1/g))^(1+a)),#1>=m}},\n0),Listable)");

    check("Mean(ParetoDistribution(k,a))", //
        "Piecewise({{(a*k)/(-1+a),a>1}},Indeterminate)");
    check("Mean(ParetoDistribution(k,a,m))", //
        "Piecewise({{k/(-1+a)+m,a>1}},Indeterminate)");
    check("Mean(ParetoDistribution(k,a,g,m))", //
        "Piecewise({{m+(k*Gamma(a-g)*Gamma(1+g))/Gamma(a),a>g}},Indeterminate)");
    check("Median(ParetoDistribution(k,a))", //
        "2^(1/a)*k");
    check("Median(ParetoDistribution(k,a,m))", //
        "(-1+2^(1/a))*k+m");
    check("Median(ParetoDistribution(k,a,g,m))", //
        "(-1+2^(1/a))^g*k+m");
    check("Skewness(ParetoDistribution(k,a))", //
        "Piecewise({{(2*Sqrt((-2+a)/a)*(1+a))/(-3+a),a>3}},Indeterminate)");
    check("Skewness(ParetoDistribution(k,a,m))", //
        "Piecewise({{(2*Sqrt((-2+a)/a)*(1+a))/(-3+a),a>3}},Indeterminate)");
    check("Skewness(ParetoDistribution(k,a,g,m))", //
        "Piecewise({{(k^3*(2*Gamma(a-g)^3*Gamma(1+g)^3-3*Gamma(a)*Gamma(a-2*g)*Gamma(a-g)*Gamma(\n"
            + "1+g)*Gamma(1+2*g)+Gamma(a)^2*Gamma(a-3*g)*Gamma(1+3*g)))/(k^2*(-Gamma(a-g)^2*Gamma(\n"
            + "1+g)^2+Gamma(a)*Gamma(a-2*g)*Gamma(1+2*g)))^(3/2),a>3*g}},Indeterminate)");
    check("Variance(ParetoDistribution(k,a))", //
        "Piecewise({{(a*k^2)/((1-a)^2*(-2+a)),a>2}},Indeterminate)");
    check("Variance(ParetoDistribution(k,a,m))", //
        "Piecewise({{(a*k^2)/((1-a)^2*(-2+a)),a>2}},Indeterminate)");
    check("Variance(ParetoDistribution(k,a,g,m))", //
        "Piecewise({{(k^2*(-Gamma(a-g)^2*Gamma(1+g)^2+Gamma(a)*Gamma(a-2*g)*Gamma(1+2*g)))/Gamma(a)^\n"
            + "2,a>2*g}},Indeterminate)");
  }

  @Test
  public void testPDF() {
    check("PDF(BinomialDistribution(50.0, 0.5), 27.0)", //
        "0.0959617");
    check("PDF(BetaDistribution(2,3), 0.1)", //
        "0.972");
    check("PDF(BetaDistribution(2,3), 0.9)", //
        "0.108");
    check("PDF(ChiSquareDistribution(3), 0.1)", //
        "0.120004");
    check("PDF(CauchyDistribution(a, b), x)", //
        "1/(b*Pi*(1+(-a+x)^2/b^2))");
    check("PDF(ChiSquareDistribution(3), 0.9)", //
        "0.241323");
    check("PDF(FRatioDistribution(2,3), 0.1)", //
        "0.850997");
    check("PDF(FRatioDistribution(2,3), 0.9)", //
        "0.308816");
    check("PDF(GammaDistribution(2,3), 0.1)", //
        "0.0107468");
    check("PDF(GammaDistribution(2,3), 0.9)", //
        "0.0740818");
    check("PDF(GompertzMakehamDistribution(2,3), 0.9)", //
        "9.56708*10^-6");
    check("PDF(LogNormalDistribution(2,3), 0.1)", //
        "0.475483");
    check("PDF(LogNormalDistribution(2,3), 0.9)", //
        "0.115505");
    check("PDF(NakagamiDistribution(2,3), 0.1)", //
        "0.000882983");
    check("PDF(NakagamiDistribution(2,3), 0.9)", //
        "0.377621");
    check("PDF(NormalDistribution(2,3), 0.1)", //
        "0.108815");
    check("PDF(NormalDistribution(2,3), 0.9)", //
        "0.124335");
    check("PDF(StudentTDistribution(3), 0.1)", //
        "0.365114");
    check("PDF(StudentTDistribution(3), 0.9)", //
        "0.227883");
    check("PDF(UniformDistribution({0,1}), 0.1)", //
        "1");
    check("PDF(UniformDistribution({0,1}), 0.9)", //
        "1");
    check("PDF(WeibullDistribution(2,3), 0.1)", //
        "0.0221975");
    check("PDF(WeibullDistribution(2,3), 0.9)", //
        "0.182786");

    check("Table(PDF(NormalDistribution(m, 1.5), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}}) ", //
        "{{0.265962,0.10934,0.035994},{0.10934,0.265962,0.212965},{0.035994,0.212965,0.265962}}");
    check("Table(PDF(NormalDistribution(0.0,1.0), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}})//N ", //
        "{{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991}}");
    check("Table(PDF(NormalDistribution( ), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}})//N ", //
        "{{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991}}");

    check("PDF(NormalDistribution(0, 1), {x, y})", //
        "{1/(E^(x^2/2)*Sqrt(2*Pi)),1/(E^(y^2/2)*Sqrt(2*Pi))}");

    check("PDF(NormalDistribution(n, m))", //
        "Function(#1,1/(E^((-n+#1)^2/(2*m^2))*m*Sqrt(2*Pi)),Listable)");
    check("PDF(NormalDistribution(n, m),k)", //
        "1/(E^((k-n)^2/(2*m^2))*m*Sqrt(2*Pi))");
    check("PDF(BernoulliDistribution(p),k)", //
        "Piecewise({{1-p,k==0},{p,k==1}},0)");
    check("PDF(BetaDistribution(a,b),k)", //
        "Piecewise({{1/((1-k)^(1-b)*k^(1-a)*Beta(a,b)),0<k<1}},0)");
    check("PDF(BinomialDistribution(n, m),k)", //
        "Piecewise({{(m^k*Binomial(n,k))/(1-m)^(k-n),0<=k<=n}},0)");
    check("PDF(ExponentialDistribution(n),k)", //
        "Piecewise({{n/E^(k*n),k>=0}},0)");
    check("PDF(PoissonDistribution(p),k)", //
        "Piecewise({{p^k/(E^p*k!),k>=0}},0)");
    check("PDF(DiscreteUniformDistribution({a, b}), k)", //
        "Piecewise({{1/(1-a+b),a<=k<=b}},0)");
    check("PDF(UniformDistribution({a, b}), k)", //
        "Piecewise({{1/(-a+b),a<=k<=b}},0)");
    check("PDF(ErlangDistribution(n, m),k)", //
        "Piecewise({{m^n/(E^(k*m)*k^(1-n)*Gamma(n)),k>0}},0)");
    check("PDF(GompertzMakehamDistribution(m,n),k)", //
        "Piecewise({{E^(k*m+(1-E^(k*m))*n)*m*n,k>=0}},0)");
    check("PDF(LogNormalDistribution(n,m),k)", //
        "Piecewise({{1/(E^((-n+Log(k))^2/(2*m^2))*k*m*Sqrt(2*Pi)),k>0}},0)");
    check("PDF(NakagamiDistribution(n, m),k)", //
        "Piecewise({{(2*(n/m)^n)/(E^((k^2*n)/m)*k^(1-2*n)*Gamma(n)),k>0}},0)");

    check("PDF(FrechetDistribution(n, m),k)", //
        "Piecewise({{n/(E^(k/m)^(-n)*(k/m)^(1+n)*m),k>0}},0)");
    check("PDF(GammaDistribution(n, m),k)", //
        "Piecewise({{1/(E^(k/m)*k^(1-n)*m^n*Gamma(n)),k>0}},0)");
    check("PDF(GeometricDistribution(n),k)", //
        "Piecewise({{(1-n)^k*n,k>=0}},0)");
    check("PDF(GumbelDistribution(n, m),k)", //
        "1/(E^(E^((k-n)/m)-(k-n)/m)*m)");
    check("PDF(HypergeometricDistribution(n, ns, nt),k)", //
        "Piecewise({{(Binomial(ns,k)*Binomial(-ns+nt,-k+n))/Binomial(nt,n),0<=k<=n&&n+ns-nt<=k<=n&&\n"
            + "0<=k<=ns&&n+ns-nt<=k<=ns}},0)");
    check("PDF(StudentTDistribution(n),k)", //
        "(n/(k^2+n))^(1/2*(1+n))/(Sqrt(n)*Beta(n/2,1/2))");
    check("PDF(WeibullDistribution(n, m),k)", //
        "Piecewise({{n/(E^(k/m)^n*(k/m)^(1-n)*m),k>0}},0)");
    check("PDF(StudentTDistribution(4),k)", //
        "12*(1/(4+k^2))^(5/2)");

    check("PDF(DiscreteUniformDistribution({1, 5}), 3)", //
        "1/5");
    check("N(PDF(NormalDistribution(0, 1), 0))", //
        "0.398942");
    checkNumeric("N(PDF(BinomialDistribution(40, 0.5), 1))", //
        "3.637978807091713E-11");
    checkNumeric("N(PDF(HypergeometricDistribution(20,50,100), 10))", //
        "0.19687121770654945");
    checkNumeric("N(PDF(PoissonDistribution(10), 15))", //
        "0.03471806963068413");
  }

  @Test
  public void testPearsonCorrelationTest() {
    check("PearsonCorrelationTest({0,0},{0,0}, \"PValue\")", //
        "PearsonCorrelationTest({0,0},{0,0},PValue)");
    // example from https://en.wikipedia.org/wiki/Pearson_correlation_coefficient
    check("PearsonCorrelationTest({1, 2, 3, 5, 8}, {0.11, 0.12, 0.13, 0.15, 0.18}, \"TestData\")", //
        "{1.0,0.0}");

    check("{v1,v2}={{-0.4419826574006463, 1.3555554108658099, 1.0682434836787194, \n" //
        + "   -0.38987245177468655, -0.10731306756447762, 0.3196868564615598, \n" //
        + "   -0.3529758086947655, 0.18291780864085908, 0.8684236548039314, \n" //
        + "   0.5762176807966645}, {-0.7305764643508801, 1.557234211193269, \n" //
        + "   -0.17012396015011189, 0.29431425806601874, 0.08261208227839614, \n" //
        + "   0.7274500346408124, 0.054140609659740824, -0.3063622721420619, \n" //
        + "   0.68649654204442, -0.3934999984696224}};", //
        "");
    check("PearsonCorrelationTest(v1, v2, \"PValue\")", //
        "0.104957");
    check("PearsonCorrelationTest(v1, v2, \"TestData\")", //
        "{0.542801,0.104957}");
    check("PearsonCorrelationTest(v1, v2, \"TestStatistic\")", //
        "0.542801");
    checkNumeric("PearsonCorrelationTest(v1, v2)", //
        "{0.5428012916808163,0.10495674920981242}");
  }

  @Test
  public void testPoissonDistribution() {
    // Message: PoissonDistribution: Parameter -1.5 at position 1 in PoissonDistribution(-1.5) is
    // expected to be positive.
    check("Mean(PoissonDistribution(-1.5))", //
        "Mean(PoissonDistribution(-1.5))");
    // Message: PoissonDistribution: Parameter -1.5 at position 1 in PoissonDistribution(-1.5) is
    // expected to be positive.
    check("Variance(PoissonDistribution(-1.5))", //
        "Variance(PoissonDistribution(-1.5))");

    check("Mean(PoissonDistribution(1.5))", //
        "1.5");
    check("Mean(PoissonDistribution(m))", //
        "m");
    check("Variance(PoissonDistribution(m))", //
        "m");
  }

  @Test
  public void testMarginalDistribution() {
    // 1-argument BinormalDistribution(rho) -> NormalDistribution(0, 1)
    check("MarginalDistribution(BinormalDistribution(1/2), 1)", //
        "NormalDistribution(0,1)");
    check("MarginalDistribution(BinormalDistribution(1/2), 2)", //
        "NormalDistribution(0,1)");

    // 3-argument BinormalDistribution(sigma1, sigma2, rho)
    check("MarginalDistribution(BinormalDistribution(2, 3, 1/2), 1)", "NormalDistribution(0,2)");
    check("MarginalDistribution(BinormalDistribution(2, 3, 1/2), 2)", "NormalDistribution(0,3)");

    // 5-argument BinormalDistribution(mu1, mu2, sigma1, sigma2, rho)
    check("MarginalDistribution(BinormalDistribution(10, 20, 2, 3, 1/2), 1)",
        "MarginalDistribution(BinormalDistribution(10,20,2,3,1/2),1)");

    // Index swapping
    check("MarginalDistribution(BinormalDistribution(10, 20, 2, 3, 1/2), {2, 1})",
        "MarginalDistribution(BinormalDistribution(10,20,2,3,1/2),{2,1})");

    // Note: The covariance matrix diagonal represents Variance.
    // MarginalDistribution extracts Sqrt(Variance) for the 1D NormalDistribution scale.

    // 1D Marginal from 2D (Sigma only)
    // Variance = 4 -> stddev = 2
    check("MarginalDistribution(MultinormalDistribution({{4, 1}, {1, 9}}), 1)",
        "NormalDistribution(0,2)");
    // Variance = 9 -> stddev = 3
    check("MarginalDistribution(MultinormalDistribution({{4, 1}, {1, 9}}), 2)",
        "NormalDistribution(0,3)");

    // 1D Marginal from 3D (Mu, Sigma)
    check(
        "MarginalDistribution(MultinormalDistribution({10, 20, 30}, {{4, 1, 0}, {1, 9, 0}, {0, 0, 16}}), 2)",
        "NormalDistribution(20,3)");

    // 2D Joint Marginal from 3D
    check(
        "MarginalDistribution(MultinormalDistribution({10, 20, 30}, {{4, 1, 0}, {1, 9, 0}, {0, 0, 16}}), {1, 3})",
        "MultinormalDistribution({10,30},{{4,0},{0,16}})");

    // When degrees of freedom (nu) == 1, it should collapse into a CauchyDistribution

    // Standard Multivariate Cauchy mapping (nu = 1) -> CauchyDistribution()
    check("MarginalDistribution(MultivariateTDistribution({{1, 0}, {0, 1}}, 1), 1)",
        "CauchyDistribution()");

    // Located Multivariate Cauchy mapping (nu = 1) -> CauchyDistribution(mu, sigma)
    check("MarginalDistribution(MultivariateTDistribution({10, 20}, {{4, 0}, {0, 9}}, 1), 2)",
        "CauchyDistribution(20,3)");

    // When degrees of freedom (nu) > 1, it should collapse into a StudentTDistribution
    // nu = 5
    check("MarginalDistribution(MultivariateTDistribution({10, 20}, {{4, 0}, {0, 9}}, 5), 1)",
        "StudentTDistribution(10,2,5)");

    // 2D Joint Marginal from 3D MultivariateT
    check(
        "MarginalDistribution(MultivariateTDistribution({10, 20, 30}, {{4, 1, 0}, {1, 9, 0}, {0, 0, 16}}, 5), {1, 3})",
        "MultivariateTDistribution({10,30},{{4,0},{0,16}},5)");

    // Asking for the 1st marginal of a 1D distribution should yield the distribution itself

    check("MarginalDistribution(CauchyDistribution(5, 2), 1)", //
        "CauchyDistribution(5,2)");
    check("MarginalDistribution(CauchyDistribution(), 1)", //
        "CauchyDistribution()");
    check("MarginalDistribution(StudentTDistribution(10, 2, 5), 1)", //
        "StudentTDistribution(10,2,5)");
    check("MarginalDistribution(NormalDistribution(0, 1), 1)", //
        "NormalDistribution(0,1)");
    check("MarginalDistribution(PoissonDistribution(5), 1)", //
        "PoissonDistribution(5)");

    // Mean of the 2nd marginal
    check("Mean(MarginalDistribution(MultinormalDistribution({10, 20}, {{4, 1}, {1, 9}}), 2))",
        "20");

    // Variance of the 2nd marginal (Extracted NormalDistribution(20, 3) -> var is 3^2 = 9)
    check("Variance(MarginalDistribution(MultinormalDistribution({10, 20}, {{4, 1}, {1, 9}}), 2))",
        "9");

    // CDF evaluation of the marginal
    // CDF(NormalDistribution(0, 1), 0) = 1/2
    check("CDF(MarginalDistribution(BinormalDistribution(1/2), 1), 0)", "1/2");

    // Median of a collapsed CauchyDistribution
    check(
        "Median(MarginalDistribution(MultivariateTDistribution({10, 20}, {{4, 0}, {0, 9}}, 1), 1))",
        "10");

    // Out of bounds index
    check("MarginalDistribution(BinormalDistribution(1/2), 3)",
        "MarginalDistribution(BinormalDistribution(1/2),3)");

    // Negative index
    check("MarginalDistribution(BinormalDistribution(1/2), -1)",
        "MarginalDistribution(BinormalDistribution(1/2),-1)");
  }

  @Test
  public void testMultivariateTDistribution() {
    // Evaluate PDF at the origin {0, 0} for nu = 1 (Equivalent to Bivariate Cauchy)
    // Gamma(1.5) / (Gamma(0.5) * Pi * 1) * (1 + 0)^(-1.5) = 1 / (2*Pi)
    check("PDF(MultivariateTDistribution({0, 0}, {{1, 0}, {0, 1}}, 1), {0, 0})", //
        "1/(2*Pi)");

    // Evaluate PDF for nu = 2, p = 2, at {0, 0}
    // Gamma(2) / (Gamma(1) * 2*Pi * 1) = 1 / (2*Pi)
    check("PDF(MultivariateTDistribution({0, 0}, {{1, 0}, {0, 1}}, 2), {0, 0})", //
        "1/(2*Pi)");

    // Evaluate PDF at origin for 2-argument signature: MultivariateTDistribution(Sigma, nu)
    check("PDF(MultivariateTDistribution({{1, 0}, {0, 1}}, 1), {0, 0})", "1/(2*Pi)");

    // Mean: defined for nu > 1
    // nu = 3 > 1 -> Should evaluate exactly to {10, 20}
    check("Mean(MultivariateTDistribution({10, 20}, {{1, 0}, {0, 1}}, 3))", //
        "{10,20}");

    // Covariance: defined for nu > 2, scales Sigma by nu/(nu-2)
    // nu = 4 -> scale is 4/2 = 2. So Covariance is 2 * IdentityMatrix
    check("Covariance(MultivariateTDistribution({{1, 0}, {0, 1}}, 4))", //
        "{{2,0},{0,2}}");

    // Variance: Diagonal of covariance matrix
    check("Variance(MultivariateTDistribution({{1, 0}, {0, 1}}, 4))", "{2,2}");

    // Skewness: defined for nu > 3, always 0 vector
    check("Skewness(MultivariateTDistribution({5, 5}, {{1, 0}, {0, 1}}, 4))", "{0,0}");

    // Kurtosis: defined for nu > 4, 3 + 6/(nu-4)
    // nu = 5 -> 3 + 6/1 = 9
    check("Kurtosis(MultivariateTDistribution({0, 0}, {{1, 0}, {0, 1}}, 5))", "{9,9}");

    // nu = 6 -> 3 + 6/2 = 6
    check("Kurtosis(MultivariateTDistribution({0, 0}, {{1, 0}, {0, 1}}, 6))", "{6,6}");

    // When evaluated with an unknown nu, it should fallback to the Piecewise definition

    // Mean -> Piecewise({{{0, 0}, nu > 1}}, Indeterminate)
    check("Mean(MultivariateTDistribution({{1, 0}, {0, 1}}, nu))",
        "Piecewise({{{0,0},nu>1}},Indeterminate)");

    // Covariance -> Piecewise({{{nu/(-2+nu), 0}, {0, nu/(-2+nu)}}, nu > 2}, Indeterminate)
    check("Covariance(MultivariateTDistribution({{1, 0}, {0, 1}}, nu))",
        "Piecewise({{{{nu/(-2+nu),0},{0,nu/(-2+nu)}},nu>2}},Indeterminate)");

    // Since RandomVariate output is stochastic, we evaluate its structural dimensions
    // Generate 5 independent samples of a 3-dimensional t-distribution
    check(
        "Dimensions(RandomVariate(MultivariateTDistribution({0, 0, 0}, {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, 3), 5))",
        "{5,3}");

    // Generate 10 independent samples of a 2-dimensional t-distribution (2-arg constructor)
    check("Dimensions(RandomVariate(MultivariateTDistribution({{1, 0.5}, {0.5, 1}}, 4), 10))",
        "{10,2}");

    // Unsupported 1-arg signature
    check("MultivariateTDistribution(1)", "MultivariateTDistribution(1)");

    // Unevaluated when Sigma is not a square matrix
    check("Mean(MultivariateTDistribution({1, 2}, {1, 2}, 3))",
        "Mean(MultivariateTDistribution({1,2},{1,2},3))");
  }

  @Test
  public void testMultivariatePoissonDistribution() {
    check("Mean(MultivariatePoissonDistribution(m, {m1,m2,m3})) // MatrixForm", //
        "{m+m1,m+m2,m+m3}");
    check("Skewness(MultivariatePoissonDistribution(m, {m1,m2,m3})) // MatrixForm", //
        "{1/Sqrt(m+m1),1/Sqrt(m+m2),1/Sqrt(m+m3)}");
    check("Covariance(MultivariatePoissonDistribution(m, {m1,m2,m3})) // MatrixForm", //
        "{{m+m1,m,m},\n" //
            + " {m,m+m2,m},\n" //
            + " {m,m,m+m3}}");
    check("PDF(MultivariatePoissonDistribution(m, {m1,m2}), {x, y})", //
        "Piecewise({{((-m)^x*HypergeometricU(-x,1-x+y,(-m1*m2)/m))/(E^(m+m1+m2)*m2^(x-y)*x!*y!),x>=\n"
            + "0&&y>=0}},0)");
    check("CDF(MultivariatePoissonDistribution(m, {m1,m2}), {x, y})", //
        "CDF(MultivariatePoissonDistribution(m,{m1,m2}),{x,y})");
  }

  @Test
  public void testPoissonProcess() {
    check("PoissonProcess(m)[t]", //
        "PoissonDistribution(m*t)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testProbability() {
    check("Probability(x<=3, Distributed(x, GeometricDistribution(1/5)))", //
        "369/625");

    // TODO from ExponentialDistribution(y) we can make assumption that y>0
    // check("Probability(Abs(x) > 1, Distributed(x,ExponentialDistribution(y)))", //
    // "Integrate(Boole(Abs(x)>1)*Piecewise({{y/E^(x*y),x>=0}},0),{x,-Infinity,Infinity})");
    check("Probability(Abs(x)==1, Distributed(x,ExponentialDistribution(2/3)))", //
        "0");
    check("Probability(Abs(x) < -1, Distributed(x,ExponentialDistribution(2/3)))", //
        "0");
    check("Probability(Abs(x) > 1, Distributed(x,ExponentialDistribution(2/3)))", //
        "1/E^(2/3)");
    // was the wrong answer -1/E^y before the definite integral was split at x == -1, 1
    check("Probability(Abs(x) < 1, Distributed(x,ExponentialDistribution(y)))", //
        "1-1/E^y");

    check("Probability(Abs(x-1) < 1, Distributed(x,ExponentialDistribution(y)))", //
        "1-1/E^(2*y)");


    check("Probability(0<x<2, Distributed(x,ExponentialDistribution(y)))", //
        "1-1/E^(2*y)");

    check("Probability((x-1)^2<=3*x, Distributed(x,NormalDistribution()))", //
        "1/2*(-Erf((5/2-Sqrt(21)/2)/Sqrt(2))+Erf((5/2+Sqrt(21)/2)/Sqrt(2)))");

    check("Probability(90<=x<=110, Distributed(x,NormalDistribution(100,15)))", //
        "Erf(Sqrt(2)/3)");

    check("Probability(x<Log(3), Distributed(x, NormalDistribution()))", //
        "1/2+Erf(Log(3)/Sqrt(2))/2");
    check("Probability(E^x<3, Distributed(x, NormalDistribution()))", //
        "1/2+Erf(Log(3)/Sqrt(2))/2");
    check("Probability(10^x<3, Distributed(x, NormalDistribution()))", //
        "1/2+Erf(Log(3)/(Sqrt(2)*Log(10)))/2");
    // check("RandomVariate(NormalDistribution(), 10)", //
    // "{-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223}");
    check(
        "Probability(x^2 + 3*x < 11,Distributed(x,{-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223}))", //
        "9/10");
    check(
        "Probability(#^2 + 3*# < 11 &, {-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223})", //
        "9/10");
    check(
        "Probability(#^2 + 3*# < 11 &, {-0.21848,1.67503,0.78687,4.9887,7.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223})", //
        "7/10");
    check("PDF(PoissonDistribution(a))", //
        "Function(#1,Piecewise({{a^#1/(E^a*#1!),#1>=0}},0),Listable)");
    //
    check("1/(2!*E) + 1/(3!*E)+ 1/(4!*E)+ 1/(5!*E)+ 1/(6!*E) ", //
        "517/(720*E)");
    check("Probability(x<=3, Distributed(x, GeometricDistribution(1/5)))", //
        "369/625");
    check("Probability(x<=3, Distributed(x, PoissonDistribution(m)))", //
        "E^(-m)+m/E^m+m^2/(2*E^m)+m^3/(6*E^m)");
    check("Probability(3 == x, Distributed(x, PoissonDistribution(1)))", //
        "1/(6*E)");
    check("Probability(1.1 <= x <= 6.9, Distributed(x, PoissonDistribution(1)))", //
        "517/(720*E)");
    check("Probability(1.1 < x < 6.9, Distributed(x, PoissonDistribution(1)))", //
        "517/(720*E)");
    check("Probability(1 < x < 7, Distributed(x, PoissonDistribution(1)))", //
        "517/(720*E)");
    check("Probability(E^x<3, Distributed(x,PoissonDistribution(1)))", //
        "2/E");
  }

  @Test
  public void testQuantile() {
    // message Quantile: The Quantile specification {1/4,2} should be a number or a list of numbers
    // between 0 and 1.
    check("Quantile({1, 2, 3, 4, 5, 6, 7}, {1/4, 2})", //
        "Quantile({1,2,3,4,5,6,7},{1/4,2})");

    check("Quantile({1, 2, 3, 4, 5, 6, 7}, {1/4, 3/4})", //
        "{2,6}");
  }

  @Test
  public void testQuantileSparseArray() {
    check("sp = SparseArray({{i_, i_} :> i, {i_, j_} /; j == i + 1 :> i - 1}, {100, 10})", //
        "SparseArray(Number of elements: 18 Dimensions: {100,10} Default value: 0)");
    check("Quantile(sp, 99/100)", //
        "{0,0,1,2,3,4,5,6,7,8}");
  }

  @Test
  public void testStudentTDistribution() {
    check("Mean(StudentTDistribution(v))", //
        "Piecewise({{0,v>1}},Indeterminate)");
    check("Mean(StudentTDistribution(m,s,v))", //
        "Piecewise({{m,v>1}},Indeterminate)");
    check("Quantile(StudentTDistribution(v), {1/4, 1/2, 3/4})", //
        "{-Sqrt(v)*Sqrt(-1+1/InverseBetaRegularized(1/2,v/2,1/2)),0,Sqrt(v)*Sqrt(-1+1/InverseBetaRegularized(\n"
            + "1/2,v/2,1/2))}");
    check("Quantile(StudentTDistribution(m,s,v), {1/4, 1/2, 3/4})", //
        "{m-s*Sqrt(v)*Sqrt(-1+1/InverseBetaRegularized(1/2,v/2,1/2)),m,m+s*Sqrt(v)*Sqrt(-\n"
            + "1+1/InverseBetaRegularized(1/2,v/2,1/2))}");
  }

  @Test
  public void testTTest() {
    check(
        "data1={0.536463693808193,-1.511974629293994,-0.22845265689863847,0.4114790735362004,-1.372540834688803,0.18841748289331972,0.7678270833344806,0.7820712767427386,0.027735965955395632,0.38766508070235384};", //
        "");
    check("TTest(data1)", //
        "0.99662");
    check("TTest({data1,data1+2})", //
        "0.0000356619");
    check("TTest({data1,{3.2,4.3}})", //
        "0.000145336");
    check("TTest({data1,{0.536463693808193, 0.7678270833344806,1.1,4.5 }})", //
        "0.0283933");
    check("TTest({{0.5,1.3,-0,7},{0.5,1.3,-0,7}})", //
        "1.0");
    check("TTest({{0.5,1.3,-0,7},{-0.7,1.3,0.5}})", //
        "0.396998");
    check("TTest({0.5,1.3,-0,7})", //
        "0.268096");
  }

  @Test
  public void testWeibullDistribution() {
    check("Mean(WeibullDistribution(a,b))", //
        "b*Gamma(1+1/a)");
    check("Mean(WeibullDistribution(a,b,m))", //
        "m+b*Gamma(1+1/a)");

    check("Quantile(WeibullDistribution(a,b), {1/4, 1/2, 3/4})", //
        "{b*Log(4/3)^(1/a),b*Log(2)^(1/a),b*Log(4)^(1/a)}");
    check("Quantile(WeibullDistribution(a,b,m), {1/4, 1/2, 3/4})", //
        "{m+b*Log(4/3)^(1/a),m+b*Log(2)^(1/a),m+b*Log(4)^(1/a)}");
  }

  @Test
  public void testChiSquareTest() {
    // Issue #824
    ExprEvaluator exprEvaluator = new ExprEvaluator();
    exprEvaluator.eval("ChiSquareTest[observed_, any_] :=\n" //
        + "    Block[{chi2, p, df, expected, sumRow, sumCol, sumAll},\n" //
        + "       \n" //
        + "       \n" //
        + "      sumRow = Total[observed, {2}];\n" //
        + "       \n" //
        + "      sumCol = Total[observed];\n" //
        + "       \n" //
        + "      sumAll = Total[observed, 2];\n" //
        + "       \n" //
        + "      expected = Partition[Apply[Times, CartesianProduct[sumRow, sumCol], {1}] / sumAll, Length[sumCol]];\n" //
        + "       \n" //
        + "      df = (Dimensions[observed][[1]] - 1) * (Dimensions[observed][[2]] - 1);\n" //
        + "      chi2 = Total[Flatten[(observed - expected) ^ 2 / expected]] // N;\n" //
        + "      p = 1 - CDF[ChiSquareDistribution[df], chi2] // N;\n" //
        + "      {\"chi2\" -> chi2, \"p\" -> p, \"df\" -> df, \"expected\" -> expected}\n" //
        + "    ];\n");
    exprEvaluator.eval("M1 = {{8, 10, 6, 9, 9}, {4, 6, 9,6, 5}}");
    IExpr result = exprEvaluator.eval("N[ChiSquareTest[M1, M2]]");
    assertEquals(result.toString(), //
        "{chi2->2.75265,p->0.600033,df->4.0,expected->{{7.0,9.33333,8.75,8.75,8.16667},{5.0,6.66667,6.25,6.25,5.83333}}}");
  }

  @Test
  public void testSkewNormalDistribution() {
    check("PDF(SkewNormalDistribution(m, s, a), x)", //
        "Erfc((a*(m-x))/(Sqrt(2)*s))/(E^((-m+x)^2/(2*s^2))*Sqrt(2*Pi)*s)");
    check("CDF(SkewNormalDistribution(m, s, a), x)", //
        "Erfc((m-x)/(Sqrt(2)*s))/2-2*OwenT((-m+x)/s,a)");
    check("Mean(SkewNormalDistribution(m, s, a))", //
        "m+(a*Sqrt(2/Pi)*s)/Sqrt(1+a^2)");
    check("Variance(SkewNormalDistribution(m, s, a))", //
        "(1+(-2*a^2)/((1+a^2)*Pi))*s^2");
    check("Skewness(SkewNormalDistribution(m, s, a))", //
        "(Sqrt(2)*a^3*(4-Pi))/(a^2*(-2+Pi)+Pi)^(3/2)");
    check("Kurtosis(SkewNormalDistribution(m, s, a))", //
        "3+(8*a^4*(-3+Pi))/(a^2*(-2+Pi)+Pi)^2");
    check("Simplify(PDF(SkewNormalDistribution(m, s, 0), x) - PDF(NormalDistribution(m, s), x))", //
        "0");
    checkNumeric("CDF(SkewNormalDistribution(2), 0.5)", //
        "0.4083012539660562");
    checkNumeric("N(Mean(SkewNormalDistribution(1, 2, 3)))", //
        "2.513879513212096");
    check("Skewness(SkewNormalDistribution(1, 2, -3)) + Skewness(SkewNormalDistribution(1, 2, 3))", //
        "0");
    check("PDF(SkewNormalDistribution(0, -1, 1), x)", //
        "PDF(SkewNormalDistribution(0,-1,1),x)");
    // quantiles of machine probabilities are found numerically, exact ones are kept
    check("d = SkewNormalDistribution(1, 3, 6); q = InverseCDF(d, 0.65);"
        + "{q > Mean(d), Abs(CDF(d, q) - 0.65) < 10^-14}", //
        "{True,True}");
    check("d = SkewNormalDistribution(-1, 1/2, -4); q = InverseCDF(d, 0.01);"
        + "Abs(CDF(d, q) - 0.01) < 10^-15", //
        "True");
    check("{Abs(N(InverseCDF(SkewNormalDistribution(0, 1, 0), 4/5)) - N(InverseCDF(NormalDistribution(), 4/5))) < 10^-14, "
        + "InverseCDF(SkewNormalDistribution(0., 2, 5), 0), InverseCDF(SkewNormalDistribution(0., 2, 5), 1)}", //
        "{True,-Infinity,Infinity}");
    check("InverseCDF(SkewNormalDistribution(0, 1, 6), 2/5)", //
        "InverseCDF(SkewNormalDistribution(0,1,6),2/5)");
    // a machine probability gives a machine quantile, also next to an N(x, p) of the input
    check("{Precision(InverseCDF(SkewNormalDistribution(0, 1, 6), 0.35)), N(E, 30);"
        + " Precision(InverseCDF(SkewNormalDistribution(0, 1`30, 6), 0.35))}", //
        "{16,16}");
    // arbitrary precision: Newton steps from the machine quantile
    check("InverseCDF(SkewNormalDistribution(2, 1/2, 3), 0.7`30)", //
        "2.5181773931530600818172475471");
    check("d = SkewNormalDistribution(0, 2, -5); q = N(InverseCDF(d, 1/4), 20);"
        + "{q, Abs(CDF(d, q) - 1/4) < 10^-19}", //
        "{-2.3006987604717846224,True}");
  }

  @Test
  public void testOwenTMachinePrecision() {
    // machine values agree with 30 digits, for small and large a and h
    check("Table(Module({v = OwenT @@ p, w = N(OwenT @@ SetPrecision(p, 30), 30)}, "
        + "Abs(v - w) <= 10^-15*Abs(w)), "
        + "{p, {{0.4, 0.7}, {2.2, 6.5}, {-0.9, 2.0}, {0.6, -11.0}, {7.5, 0.1}, {0.02, 250.0}, "
        + "{9.0, 0.3}}})", //
        "{True,True,True,True,True,True,True}");
  }

  @Test
  public void testSkewNormalDistributionRandomVariate() {
    check("Head(RandomVariate(SkewNormalDistribution(-1, 3, 2)))", //
        "Real");
    check("Dimensions(RandomVariate(SkewNormalDistribution(0, 2, -3), {2, 5}))", //
        "{2,5}");
    check("Dimensions(RandomReal(SkewNormalDistribution(0, 1, 5), {4, 3}))", //
        "{4,3}");
    // the sample moments are near the moments of the distribution
    check("SeedRandom(1234); d = SkewNormalDistribution(-2, 3, 5); s = RandomVariate(d, 20000);" //
        + "{Abs(Mean(s) - N(Mean(d))) < 0.1, Abs(Variance(s)/N(Variance(d)) - 1) < 0.05, "
        + "Abs(Skewness(s) - N(Skewness(d))) < 0.1}", //
        "{True,True,True}");
    check("SeedRandom(4321); s = RandomVariate(SkewNormalDistribution(1, 2, -7), 20000); "
        + "Skewness(s) < -0.6", //
        "True");
    check("RandomVariate(SkewNormalDistribution(0, 1, b))", //
        "RandomVariate(SkewNormalDistribution(0,1,b))");
  }

  @Test
  public void testInverseGaussianDistribution() {
    check("PDF(InverseGaussianDistribution(1, 4), x)", //
        "Piecewise({{(Sqrt(2/Pi)*Sqrt(1/x^3))/E^((2*(1-x)^2)/x),x>0}},0)");
    check("CDF(InverseGaussianDistribution(1, 4), x)", //
        "Piecewise({{1/2*(Erfc((Sqrt(2)*(1-x))/Sqrt(x))+E^8*Erfc((Sqrt(2)*(1+x))/Sqrt(x))),x>\n"
            + "0}},0)");
    // the machine value and the exact form agree
    checkNumeric("{CDF(InverseGaussianDistribution(1, 4), 0.7), N(CDF(InverseGaussianDistribution(1, 4), 7/10)),"
        + " PDF(InverseGaussianDistribution(1, 4), 0.7)}", //
        "{0.3086123662056899,0.30861236620568966,1.0534581261650413}");
    checkNumeric("NIntegrate(PDF(InverseGaussianDistribution(1, 4), x), {x, 0, 0.7})", //
        "0.3086123662056896");
    // E^(2*l/m) overflows: the second term from its exponent
    checkNumeric("CDF(InverseGaussianDistribution(1., 2000.), 1.05)", //
        "0.9858640712899996");
    check("{Mean(InverseGaussianDistribution(m, l)), Variance(InverseGaussianDistribution(m, l)),"
        + " Skewness(InverseGaussianDistribution(m, l)), Kurtosis(InverseGaussianDistribution(m, l))}", //
        "{m,m^3/l,3*Sqrt(m/l),3+(15*m)/l}");
    check("Expectation(x^2, x \\[Distributed] InverseGaussianDistribution(1, 4))", //
        "5/4");
    // outside of the support, without evaluating the density at 0
    check("{PDF(InverseGaussianDistribution(1, 4), 0), PDF(InverseGaussianDistribution(1, 4), -2.),"
        + " CDF(InverseGaussianDistribution(1, 4), -2.), CDF(InverseGaussianDistribution(1, 4), 0.)}", //
        "{0,0.0,0,0}");
    checkNumeric("Quantile(InverseGaussianDistribution(1, 4), {0.25, 0.5, 0.75})", //
        "{0.644190837561728,0.8904967267002923,1.2355139068256054}");
    check("Quantile(InverseGaussianDistribution(1, 4), {0, 1})", //
        "{0,Infinity}");
    check("SeedRandom(42); r = RandomVariate(InverseGaussianDistribution(1, 4), 10^4);"
        + " {Min(r) > 0, Abs(Mean(r) - 1) < 0.05, Abs(Variance(r) - 1/4) < 0.05}", //
        "{True,True,True}");
    check("SeedRandom(7); Length(RandomReal(InverseGaussianDistribution(0.2, 6), 5))", //
        "5");
  }

  /**
   * The machine value <code>v</code> in a form the parser reads back exactly.
   */
  private static String machine(double v) {
    return String.format(java.util.Locale.ROOT, "%.17e", v).replace("e", "*10^");
  }

  /**
   * The density and the CDF of <code>distribution</code> agree with those of the Hipparchus
   * distribution <code>hipparchus</code>, whose parameters are translated from the parameters of
   * <code>distribution</code>.
   */
  private void checkAgainstHipparchus(String distribution,
      org.hipparchus.distribution.RealDistribution hipparchus, double... xs) {
    for (double x : xs) {
      check("Abs(PDF(" + distribution + ", " + machine(x) + ")/(" + machine(hipparchus.density(x))
          + ") - 1) < 10^-12", //
          "True");
      check("Abs(CDF(" + distribution + ", " + machine(x) + ") - ("
          + machine(hipparchus.cumulativeProbability(x)) + ")) < 10^-12", //
          "True");
    }
  }

  @Test
  public void testHipparchusParametrization() {
    // ExponentialDistribution(rate) - Hipparchus takes the mean 1/rate
    checkAgainstHipparchus("ExponentialDistribution(2.5)",
        new org.hipparchus.distribution.continuous.ExponentialDistribution(1.0 / 2.5), 0.1, 0.7,
        2.0);
    // ErlangDistribution(k, rate) - GammaDistribution with the shape k and the scale 1/rate
    checkAgainstHipparchus("ErlangDistribution(3, 2.5)",
        new org.hipparchus.distribution.continuous.GammaDistribution(3.0, 1.0 / 2.5), 0.2, 1.1,
        3.0);
    // ChiSquareDistribution(v) - GammaDistribution with the shape v/2 and the scale 2
    checkAgainstHipparchus("ChiSquareDistribution(3.4)",
        new org.hipparchus.distribution.continuous.GammaDistribution(1.7, 2.0), 0.3, 2.2, 7.5);
    // NakagamiDistribution(m, w) - the same parameters
    checkAgainstHipparchus("NakagamiDistribution(1.7, 2.3)",
        new org.hipparchus.distribution.continuous.NakagamiDistribution(1.7, 2.3), 0.4, 1.3,
        2.6);
    // HalfNormalDistribution(t) - the standard deviation Sqrt(Pi/2)/t of the folded normal
    checkAgainstHipparchus("HalfNormalDistribution(1.3)",
        new org.hipparchus.distribution.continuous.HalfNormalDistribution(
            Math.sqrt(Math.PI / 2.0) / 1.3),
        0.05, 0.8, 2.1);
  }

  @Test
  public void testRandomVariateHipparchusGenerators() {
    // the sample moments are those of the distribution with the same parameters; a parameter
    // taken as the wrong one of rate and scale is off by a factor
    String[] distributions = {"ExponentialDistribution(3)", "ErlangDistribution(3, 2)",
        "ChiSquareDistribution(5)", "ChiSquareDistribution(0.3)", "NakagamiDistribution(2.5, 3)",
        "NakagamiDistribution(0.7, 1.5)", "GammaDistribution(2, 3, 3/2, 1)",
        "GammaDistribution(0.6, 2, 0.8, -1)", "HalfNormalDistribution(3/2)"};
    for (String d : distributions) {
      check("SeedRandom(2024); d = " + d + "; s = RandomVariate(d, 20000);"
          + "{Abs(Mean(s)/N(Mean(d)) - 1) < 0.05, Abs(Variance(s)/N(Variance(d)) - 1) < 0.15}", //
          "{True,True}");
    }
    // seeded samples repeat
    check("SeedRandom(11); a = RandomVariate(NakagamiDistribution(2, 3), 4);"
        + " SeedRandom(11); b = RandomVariate(NakagamiDistribution(2, 3), 4); a === b", //
        "True");
    check("SeedRandom(11); a = RandomVariate(ErlangDistribution(4, 1/2), {2, 3});"
        + " SeedRandom(11); b = RandomVariate(ErlangDistribution(4, 1/2), {2, 3});"
        + " {a === b, Dimensions(a)}", //
        "{True,{2,3}}");
    // the support
    check("SeedRandom(5); {Min(RandomVariate(GammaDistribution(2, 3, 3/2, 1), 1000)) > 1,"
        + " Min(RandomVariate(HalfNormalDistribution(2), 1000)) >= 0,"
        + " Min(RandomVariate(NakagamiDistribution(0.6, 2), 1000)) >= 0}", //
        "{True,True,True}");
    check("Head(RandomVariate(ErlangDistribution(2, 5)))", //
        "Real");
    // invalid parameters
    check("RandomVariate(ErlangDistribution(-1, 2))", //
        "RandomVariate(ErlangDistribution(-1,2))");
    check("RandomVariate(ChiSquareDistribution(-2), 3)", //
        "RandomVariate(ChiSquareDistribution(-2),3)");
    check("RandomVariate(GammaDistribution(2, 3, -1, 0), 3)", //
        "RandomVariate(GammaDistribution(2,3,-1,0),3)");
    // HalfNormalDistribution: Parameter 0 at position 1 in HalfNormalDistribution(0) is expected
    // to be positive.
    check("RandomVariate(HalfNormalDistribution(0))", //
        "RandomVariate(HalfNormalDistribution(0))");
    // NakagamiDistribution: Parameter -1 at position 1 in NakagamiDistribution(-1,3) is expected
    // to be positive.
    check("RandomVariate(NakagamiDistribution(-1, 3))", //
        "RandomVariate(NakagamiDistribution(-1,3))");
    // ErlangDistribution: Parameter 5/2 at position 1 in ErlangDistribution(5/2,1) is expected to
    // be a positive integer.
    check("RandomVariate(ErlangDistribution(5/2, 1), 3)", //
        "RandomVariate(ErlangDistribution(5/2,1),3)");
    // the shape of ErlangDistribution is an exact integer
    check("RandomVariate(ErlangDistribution(3.0, 1), 3)", //
        "RandomVariate(ErlangDistribution(3.0,1),3)");
    // a small shape: U^(1/shape) underflows to 0.0, as in Mathematica
    check("SeedRandom(1); {Min(RandomVariate(ChiSquareDistribution(1/100), 10000)) >= 0,"
        + " Min(RandomVariate(GammaDistribution(1/200, 1, 1, 0), 10000)) >= 0}", //
        "{True,True}");
  }

  @Test
  public void testHalfNormalDistribution() {
    check("PDF(HalfNormalDistribution(t), x)", //
        "Piecewise({{(2*t)/(E^((t^2*x^2)/Pi)*Pi),x>0}},0)");
    check("CDF(HalfNormalDistribution(t), x)", //
        "Piecewise({{Erf((t*x)/Sqrt(Pi)),x>0}},0)");
    check("Quantile(HalfNormalDistribution(t), q)", //
        "ConditionalExpression(Piecewise({{(Sqrt(Pi)*InverseErf(q))/t,0<q<1},{0,q<=0}},Infinity),\n"
            + "0<=q<=1)");
    check("{Mean(HalfNormalDistribution(t)), Variance(HalfNormalDistribution(t)),"
        + " Median(HalfNormalDistribution(t))}", //
        "{1/t,(-2+Pi)/(2*t^2),(Sqrt(Pi)*InverseErf(1/2))/t}");
    check("{Skewness(HalfNormalDistribution(t)), Kurtosis(HalfNormalDistribution(t))}", //
        "{(Sqrt(2)*(4-Pi))/(-2+Pi)^(3/2),(-12-4*Pi+3*Pi^2)/(2-Pi)^2}");
    check("{Moment(HalfNormalDistribution(t), 3), Moment(HalfNormalDistribution(t), 4)}", //
        "{Pi/t^3,3/4*Pi^2/t^4}");
    check("Table(CentralMoment(HalfNormalDistribution(t), m), {m, 0, 4})", //
        "{1,0,(-1+Pi/2)/t^2,(2-Pi/2)/t^3,(-3-Pi+3/4*Pi^2)/t^4}");
    check("Simplify(CentralMoment(HalfNormalDistribution(t), 3) - "
        + "Skewness(HalfNormalDistribution(t))*Variance(HalfNormalDistribution(t))^(3/2), t > 0)", //
        "0");
    check("Expectation(x^2, Distributed(x, HalfNormalDistribution(t)))", //
        "Pi/(2*t^2)");
    check("Integrate(PDF(HalfNormalDistribution(t), x), {x, 0, Infinity}, Assumptions -> t > 0)", //
        "1");
    checkNumeric("NIntegrate(x*PDF(HalfNormalDistribution(2/3), x), {x, 0, Infinity})", //
        "1.5");
    check("{CDF(HalfNormalDistribution(2), -1), PDF(HalfNormalDistribution(2), -1),"
        + " Quantile(HalfNormalDistribution(2), {0, 1})}", //
        "{0,0,{0,Infinity}}");
    checkNumeric("CDF(HalfNormalDistribution(2), Quantile(HalfNormalDistribution(2), 0.3))", //
        "0.3");
  }

  /** The piecewise density and distribution function, which BatesDistribution shares. */
  @Test
  public void testUniformSumDistribution() {
    check("PDF(UniformSumDistribution(1), x)", //
        "Piecewise({{1,0<=x<=1}},0)");
    check("PDF(UniformSumDistribution(3), x)", //
        "Piecewise({{0,x<0},{x^2/2,0<=x<1},{1/2*(-3*(1-x)^2+x^2),1<=x<2},{(3-x)^2/2,2<=x<=3}},0)");
    check("CDF(UniformSumDistribution(3), x)", //
        "Piecewise({{0,x<0},{x^3/6,0<=x<1},{1/6*(3-9*x+9*x^2-2*x^3),1<=x<2},{1+1/6*(-27+27*x-9*x^\n"
            + "2+x^3),2<=x<=3}},1)");
    check("PDF(UniformSumDistribution(4), {0.5,1.5,2.5,3.5,4.5})", //
        "{0.0208333,0.479167,0.479167,0.0208333,0}");
    check("CDF(UniformSumDistribution(5), 2.2)", //
        "0.325816");
  }
}
