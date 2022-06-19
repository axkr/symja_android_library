package org.matheclipse.core.system;

public class DistributionTest extends ExprEvaluatorTestCase {
  public DistributionTest(String name) {
    super(name);
  }

  public void testBernoulliDistribution() {
    check("Mean(BernoulliDistribution(x))", //
        "x");
    check("Quantile(BernoulliDistribution(x), {1/4, 1/2, 3/4})", //
        "{Piecewise({{1,x>3/4}},0),Piecewise({{1,x>1/2}},0),Piecewise({{1,x>1/4}},0)}");
  }

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
    // TODO
    // check("Skewness(ChiSquareDistribution(v))", //
    // "");
  }

  public void testErlangDistribution() {
    check("Mean(ErlangDistribution(n, m))", //
        "n/m");
    check("Quantile(ErlangDistribution(n, m), {1/4, 1/2, 3/4})", //
        "{InverseGammaRegularized(n,0,1/4)/m,InverseGammaRegularized(n,0,1/2)/m,InverseGammaRegularized(n,\n"
            + "0,3/4)/m}");
  }

  public void testExponentialDistribution() {
    check("Mean(ExponentialDistribution(x))", //
        "1/x");
    check("Quantile(ExponentialDistribution(x), {1/4, 1/2, 3/4})", //
        "{Log(4/3)/x,Log(2)/x,Log(4)/x}");
  }

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

  public void testFrechetDistribution() {
    check("Mean(FrechetDistribution(n, m))", //
        "Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
    check("Quantile(FrechetDistribution(n, m), {1/4, 1/2, 3/4})", //
        "{m/Log(4)^(1/n),m/Log(2)^(1/n),m/Log(4/3)^(1/n)}");
  }

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

  public void testNormalDistribution() {

    check("CentralMoment(NormalDistribution(a, b),n)", //
        "Piecewise({{b^n*(-1+n)!!,Mod(n,2)==0&&n>=0}},0)");
    check("CentralMoment(NormalDistribution(a, b),2)", //
        "b^2");
    check("CentralMoment(NormalDistribution(a, b),3)", //
        "0");
    check("PDF(NormalDistribution(m,0), x)", //
        "PDF(NormalDistribution(m,0),x)");
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

  public void testLogNormalDistribution() {
    check("Mean(LogNormalDistribution(m,d))", //
        "E^(d^2/2+m)");
    check("Quantile(LogNormalDistribution(m,d), {1/4, 1/2, 3/4})", //
        "{E^(m-Sqrt(2)*d*InverseErfc(1/2)),E^m,E^(m-Sqrt(2)*d*InverseErfc(3/2))}");
  }

  public void testNakagamiDistribution() {
    check("Mean(NakagamiDistribution(m,w))", //
        "(Sqrt(w)*Pochhammer(m,1/2))/Sqrt(m)");
    check("Variance(NakagamiDistribution(m,w))", //
        "w+(-w*Pochhammer(m,1/2)^2)/m");
    check("Quantile(NakagamiDistribution(m,w), {1/4, 1/2, 3/4})", //
        "{Sqrt((w*InverseGammaRegularized(m,0,1/4))/m),Sqrt((w*InverseGammaRegularized(m,\n"
            + "0,1/2))/m),Sqrt((w*InverseGammaRegularized(m,0,3/4))/m)}");
  }

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

  public void testPoissonDistribution() {
    check("Mean(PoissonDistribution(m))", //
        "m");
    check("Variance(PoissonDistribution(m))", //
        "m");
  }

  public void testQuantileSparseArray() {
    check("sp = SparseArray({{i_, i_} :> i, {i_, j_} /; j == i + 1 :> i - 1}, {100, 10})", //
        "SparseArray(Number of elements: 18 Dimensions: {100,10} Default value: 0)");
    check("Quantile(sp, 99/100)", //
        "{0,0,1,2,3,4,5,6,7,8}");
  }

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
}
