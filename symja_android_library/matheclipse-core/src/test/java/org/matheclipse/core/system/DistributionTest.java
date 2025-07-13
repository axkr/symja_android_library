package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class DistributionTest extends ExprEvaluatorTestCase {

  @Test
  public void testBernoulliDistribution() {
    check("Mean(BernoulliDistribution(x))", //
        "x");
    check("Quantile(BernoulliDistribution(x), {1/4, 1/2, 3/4})", //
        "{Piecewise({{1,x>3/4}},0),Piecewise({{1,x>1/2}},0),Piecewise({{1,x>1/4}},0)}");
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
        "Piecewise({{1-E^((1-E^(m*#1))*n),#1>=0}},0)&");
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
        "Piecewise({{GammaRegularized(v/2,0,#1/2),#1>0}},0)&");
    check("PDF(ChiSquareDistribution(v))", //
        "Piecewise({{1/(2^(v/2)*E^(#1/2)*Gamma(v/2)*#1^(1-v/2)),#1>0}},0)&");

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
        "{0.7,0.9,1.0,1.0,1.0}");
    // https://github.com/Hipparchus-Math/hipparchus/pull/317
    check("{PDF(edata,0.25),PDF(edata,1),PDF(edata,3),PDF(edata,4),PDF(edata,5)}", //
        "{0.0,0.0,0.0,0.0,0.0}");
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
    // check("Expectation(x^2+7*x+8,Distributed(x,PoissonDistribution(m)))", //
    // "8+8*m+m^2");
    // check("Expectation(E^(2*x) + 3, Distributed( x, PoissonDistribution(l)))", //
    // "");
    //
    // check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 9})))", "13/2");
    // check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 10})))", "7");
    //
    // check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 9})))", "16");
    // check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 10})))", "17");

  }

  @Test
  public void testNExpectation() {
    // check("NExpectation(E^x, Distributed(x, PoissonDistribution(1.0)))", //
    // "5.57494");


    check("NExpectation(Abs(2*x-1), Distributed(x, GammaDistribution(3.5,2)))", //
        "13.00025");

    // check("NExpectation(E^x, Distributed(x, HypergeometricDistribution(20,50,100)))", //
    // "");
    // check("NExpectation(E^x, Distributed(x, GeometricDistribution(0.1)))", //
    // "");
    // check("NExpectation(E^x, Distributed(x, DiscreteUniformDistribution({20,40})))", //
    // "");
    // check(
    // "NExpectation(E^x, Distributed(x, BinomialDistribution(10,0.4)))", //
    // "187.049");
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
    // check("NProbability(E^x < 3, Distributed(x,PoissonDistribution(1)))", //
    // "");


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
        "Piecewise({{BetaRegularized((n*#1)/(m+n*#1),n/2,m/2),#1>0}},0)&");
    check("PDF(FRatioDistribution(n, m))", //
        "Piecewise({{(m^(m/2)*n^(n/2)*(m+n*#1)^(1/2*(-m-n)))/(Beta(n/2,m/2)*#1^(1-n/2)),#1>\n"
            + "0}},0)&");
    // TODO
    // check("Skewness(FRatioDistribution(n, m))", //
    // "");
  }

  @Test
  public void testFrechetDistribution() {
    check("Mean(FrechetDistribution(n, m))", //
        "Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
    check("Quantile(FrechetDistribution(n, m), {1/4, 1/2, 3/4})", //
        "{m/Log(4)^(1/n),m/Log(2)^(1/n),m/Log(4/3)^(1/n)}");
  }

  @Test
  public void testGammaDistribution() {
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
        "Piecewise({{GammaRegularized(a,0,#1/b),#1>0}},0)&");
    check("CDF(GammaDistribution(a, b, g, d))", //
        "Piecewise({{GammaRegularized(a,0,((-d+#1)/b)^g),#1>d}},0)&");
    check("PDF(GammaDistribution(a, b))", //
        "Piecewise({{1/(b^a*E^(#1/b)*Gamma(a)*#1^(1-a)),#1>0}},0)&");
    check("PDF(GammaDistribution(a, b, g, d))", //
        "Piecewise({{g/(b*E^((-d+#1)/b)^g*Gamma(a)*((-d+#1)/b)^(1-a*g)),#1>d}},0)&");
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
        "{2-3*Sqrt(2)*InverseErfc(1/2),2,2-3*Sqrt(2)*InverseErfc(3/2)}");
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
  public void testLogNormalDistribution() {
    check("Mean(LogNormalDistribution(m,d))", //
        "E^(d^2/2+m)");
    check("Quantile(LogNormalDistribution(m,d), {1/4, 1/2, 3/4})", //
        "{E^(m-Sqrt(2)*d*InverseErfc(1/2)),E^m,E^(m-Sqrt(2)*d*InverseErfc(3/2))}");
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
        "Piecewise({{1-(k/#1)^a,#1>=k}},0)&");
    check("CDF(ParetoDistribution(k,a,m))", //
        "Piecewise({{1-1/(1+(-m+#1)/k)^a,#1>=m}},0)&");
    check("CDF(ParetoDistribution(k,a,g,m))", //
        "Piecewise({{1-1/(1+((-m+#1)/k)^(1/g))^a,#1>=m}},0)&");

    check("InverseCDF(ParetoDistribution(k,a))", //
        "ConditionalExpression(Piecewise({{k/(1-#1)^(1/a),#1<1}},Infinity),0<=#1<=1)&");
    check("InverseCDF(ParetoDistribution(k,a,m))", //
        "ConditionalExpression(Piecewise({{m+k*(-1+(1-#1)^(-1/a)),0<#1<1},{m,#1<=0}},Infinity),\n"
            + "0<=#1<=1)&");
    check("InverseCDF(ParetoDistribution(k,a,g,m))", //
        "ConditionalExpression(Piecewise({{m+k*(-1+(1-#1)^(-1/a))^g,0<#1<1},{m,#1<=0}},Infinity),\n"
            + "0<=#1<=1)&");

    check("PDF(ParetoDistribution(k,a))", //
        "Piecewise({{(a*k^a)/#1^(1+a),#1>=k}},0)&");
    check("PDF(ParetoDistribution(k,a,m))", //
        "Piecewise({{a/(k*((k-m+#1)/k)^(1+a)),#1>=m}},0)&");
    check("PDF(ParetoDistribution(k,a,g,m))", //
        "Piecewise({{a/(g*k^(1/g)*(-m+#1)^(1-1/g)*(1+(k/(-m+#1))^(-1/g))^(1+a)),#1>=m}},0)&");

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
        "1/(E^((-n+#1)^2/(2*m^2))*m*Sqrt(2*Pi))&");
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
        "12*((1/(4+k^2)))^(5/2)");

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
  public void testPoissonProcess() {
    check("PoissonProcess(m)[t]", //
        "PoissonDistribution(m*t)");
  }

  @Test
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
    check("Probability(Abs(x) < 1, Distributed(x,ExponentialDistribution(y)))", //
        "-1/E^y");

    check("Probability(Abs(x-1) < 1, Distributed(x,ExponentialDistribution(y)))", //
        "1-1/E^(2*y)");


    check("Probability(0<x<2, Distributed(x,ExponentialDistribution(y)))", //
        "1-1/E^(2*y)");

    check("Probability((x-1)^2<=3*x, Distributed(x,NormalDistribution()))", //
        "1/2*(-Erf((5-Sqrt(21))/(2*Sqrt(2)))+Erf((5+Sqrt(21))/(2*Sqrt(2))))");

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
        "Piecewise({{a^#1/(E^a*#1!),#1>=0}},0)&");
    //
    check("1/(2!*E) + 1/(3!*E)+ 1/(4!*E)+ 1/(5!*E)+ 1/(6!*E) ", //
        "517/720*1/E");
    check("Probability(x<=3, Distributed(x, GeometricDistribution(1/5)))", //
        "369/625");
    check("Probability(x<=3, Distributed(x, PoissonDistribution(m)))", //
        "E^(-m)+m/E^m+m^2/(2*E^m)+m^3/(6*E^m)");
    check("Probability(3 == x, Distributed(x, PoissonDistribution(1)))", //
        "1/(6*E)");
    check("Probability(1.1 <= x <= 6.9, Distributed(x, PoissonDistribution(1)))", //
        "517/720*1/E");
    check("Probability(1.1 < x < 6.9, Distributed(x, PoissonDistribution(1)))", //
        "517/720*1/E");
    check("Probability(1 < x < 7, Distributed(x, PoissonDistribution(1)))", //
        "517/720*1/E");
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
}
