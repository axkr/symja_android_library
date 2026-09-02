package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 8 Special functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class SpecialFunctions extends AbstractRubiTestCase {
  static boolean init = true;

  public SpecialFunctions(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("SpecialFunctions");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 8.1 Error functions.input:10
  public void test0001() {
    check( //
        "Integrate[x^5*Erf[b*x], x]", //
        "-5/16*Erf[b*x]/b^6+1/6*x^6*Erf[b*x]+5/8*x/(E^(b^2*x^2)*b^5*Sqrt[Pi])+5/12*x^3/(E^(b^2*x^2)*b^3*Sqrt[Pi])+1/6*x^5/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:19
  public void test0002() {
    check( //
        "Integrate[x^2*Erf[b*x], x]", //
        "1/3*x^3*Erf[b*x]+1/3/(E^(b^2*x^2)*b^3*Sqrt[Pi])+1/3*x^2/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:62
  public void test0003() {
    check( //
        "Integrate[Erf[d*(a+b*Log[c*x^n])]/x, x]", //
        "Erf[d*(a+b*Log[c*x^n])]*(a+b*Log[c*x^n])/(b*n)+1/(E^(d^2*(a+b*Log[c*x^n])^2)*b*d*n*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:145
  public void test0004() {
    check( //
        "Integrate[Erfc[b*x]/x^7, x]", //
        "4/45*b^6*Erf[b*x]-1/6*Erfc[b*x]/x^6+1/15*b/(E^(b^2*x^2)*x^5*Sqrt[Pi])-2/45*b^3/(E^(b^2*x^2)*x^3*Sqrt[Pi])+4/45*b^5/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:155
  public void test0005() {
    check( //
        "Integrate[(c+d*x)^3*Erfc[a+b*x], x]", //
        "3/16*d^3*Erf[a+b*x]/b^4+3/4*d*(b*c-a*d)^2*Erf[a+b*x]/b^4+1/4*(b*c-a*d)^4*Erf[a+b*x]/(b^4*d)+1/4*(c+d*x)^4*Erfc[a+b*x]/d-d^2*(b*c-a*d)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])-(b*c-a*d)^3/(E^((a+b*x)^2)*b^4*Sqrt[Pi])-3/8*d^3*(a+b*x)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])-3/2*d*(b*c-a*d)^2*(a+b*x)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])-d^2*(b*c-a*d)*(a+b*x)^2/(E^((a+b*x)^2)*b^4*Sqrt[Pi])-1/4*d^3*(a+b*x)^3/(E^((a+b*x)^2)*b^4*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:171
  public void test0006() {
    check( //
        "Integrate[Erfc[b*x]^2/x^5, x]", //
        "-1/3*b^2/(E^(2*b^2*x^2)*Pi*x^2)-4/3*b^4*ExpIntegralEi[-2*b^2*x^2]/Pi+1/3*b^4*Erfc[b*x]^2-1/4*Erfc[b*x]^2/x^4+1/3*b*Erfc[b*x]/(E^(b^2*x^2)*x^3*Sqrt[Pi])-2/3*b^3*Erfc[b*x]/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:218
  public void test0007() {
    check( //
        "Integrate[E^(c+b^2*x^2)*x^5*Erfc[b*x], x]", //
        "E^(c+b^2*x^2)*Erfc[b*x]/b^6-E^(c+b^2*x^2)*x^2*Erfc[b*x]/b^4+1/2*E^(c+b^2*x^2)*x^4*Erfc[b*x]/b^2+2*E^c*x/(b^5*Sqrt[Pi])-2/3*E^c*x^3/(b^3*Sqrt[Pi])+1/5*E^c*x^5/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:270
  public void test0008() {
    check( //
        "Integrate[x*Erfi[b*x], x]", //
        "1/4*Erfi[b*x]/b^2+1/2*x^2*Erfi[b*x]-1/2*E^(b^2*x^2)*x/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:296
  public void test0009() {
    check( //
        "Integrate[x^3*Erfi[b*x]^2, x]", //
        "-1/2*E^(2*b^2*x^2)/(Pi*b^4)+1/4*E^(2*b^2*x^2)*x^2/(Pi*b^2)-3/16*Erfi[b*x]^2/b^4+1/4*x^4*Erfi[b*x]^2+3/4*E^(b^2*x^2)*x*Erfi[b*x]/(b^3*Sqrt[Pi])-1/2*E^(b^2*x^2)*x^3*Erfi[b*x]/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:322
  public void test0010() {
    check( //
        "Integrate[Erfi[d*(a+b*Log[c*x^n])]/x^3, x]", //
        "-1/2*Erfi[d*(a+b*Log[c*x^n])]/x^2+1/2*(c*x^n)^(2/n)*Erfi[(a*b*d^2+(-1)/n+b^2*d^2*Log[c*x^n])/(b*d)]/(E^((1-2*a*b*d^2*n)/(b^2*d^2*n^2))*x^2)");
  }

  // 8.1 Error functions.input:336
  public void test0011() {
    check( //
        "Integrate[E^(c+d*x^2)*x^5*Erfi[b*x], x]", //
        "E^(c+d*x^2)*Erfi[b*x]/d^3-E^(c+d*x^2)*x^2*Erfi[b*x]/d^2+1/2*E^(c+d*x^2)*x^4*Erfi[b*x]/d-3/8*E^c*b*Erfi[x*Sqrt[b^2+d]]/(d*(b^2+d)^(5/2))-1/2*E^c*b*Erfi[x*Sqrt[b^2+d]]/(d^2*(b^2+d)^(3/2))+3/4*E^(c+(b^2+d)*x^2)*b*x/(d*(b^2+d)^2*Sqrt[Pi])+E^(c+(b^2+d)*x^2)*b*x/(d^2*(b^2+d)*Sqrt[Pi])-1/2*E^(c+(b^2+d)*x^2)*b*x^3/(d*(b^2+d)*Sqrt[Pi])-E^c*b*Erfi[x*Sqrt[b^2+d]]/(d^3*Sqrt[b^2+d])");
  }

  // 8.1 Error functions.input:382
  public void test0012() {
    check( //
        "Integrate[Erfi[b*x]/(E^(b^2*x^2)*x^3)+b^2*Erfi[b*x]/(E^(b^2*x^2)*x), x]", //
        "-1/2*Erfi[b*x]/(E^(b^2*x^2)*x^2)-b/(x*Sqrt[Pi])");
  }

  // 8.2 Fresnel integral functions.input:26
  public void test0013() {
    check( //
        "Integrate[FresnelS[b*x]/x^9, x]", //
        "-1/280*Pi*b^3*Cos[1/2*Pi*b^2*x^2]/x^5+1/840*Pi^3*b^7*Cos[1/2*Pi*b^2*x^2]/x+1/840*Pi^4*b^8*FresnelS[b*x]-1/8*FresnelS[b*x]/x^8-1/56*b*Sin[1/2*Pi*b^2*x^2]/x^7+1/840*Pi^2*b^5*Sin[1/2*Pi*b^2*x^2]/x^3");
  }

  // 8.2 Fresnel integral functions.input:38
  public void test0014() {
    check( //
        "Integrate[x*FresnelS[a+b*x], x]", //
        "-a*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^2)+1/2*(a+b*x)*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^2)-1/2*FresnelC[a+b*x]/(Pi*b^2)-1/2*a^2*FresnelS[a+b*x]/b^2+1/2*x^2*FresnelS[a+b*x]");
  }

  // 8.2 Fresnel integral functions.input:58
  public void test0015() {
    check( //
        "Integrate[FresnelS[b*x]^2/x^5, x]", //
        "-1/24*b^2/x^2+1/24*b^2*Cos[Pi*b^2*x^2]/x^2-1/6*Pi*b^3*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x-1/12*Pi^2*b^4*FresnelS[b*x]^2-1/4*FresnelS[b*x]^2/x^4+1/12*Pi*b^4*SinIntegral[Pi*b^2*x^2]-1/6*b*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^3");
  }

  // 8.2 Fresnel integral functions.input:100
  public void test0016() {
    check( //
        "Integrate[x^8*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2], x]", //
        "105/4*x^2/(Pi^4*b^7)-7/12*x^6/(Pi^2*b^3)+55/4*x^2*Cos[Pi*b^2*x^2]/(Pi^4*b^7)-1/4*x^6*Cos[Pi*b^2*x^2]/(Pi^2*b^3)+35*x^3*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/(Pi^3*b^6)-x^7*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/(Pi*b^2)+105/2*FresnelS[b*x]^2/(Pi^4*b^9)-105*x*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi^4*b^8)+7*x^5*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^4)-40*Sin[Pi*b^2*x^2]/(Pi^5*b^9)+5/2*x^4*Sin[Pi*b^2*x^2]/(Pi^3*b^5)");
  }

  // 8.2 Fresnel integral functions.input:116
  public void test0017() {
    check( //
        "Integrate[FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^8, x]", //
        "-1/84*b/x^6+1/420*Pi^2*b^5/x^2+1/84*b*Cos[Pi*b^2*x^2]/x^6-1/84*Pi^2*b^5*Cos[Pi*b^2*x^2]/x^2-1/35*Pi*b^2*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^5+1/105*Pi^3*b^6*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x+1/210*Pi^4*b^7*FresnelS[b*x]^2-1/70*Pi^3*b^7*SinIntegral[Pi*b^2*x^2]-1/7*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^7+1/105*Pi^2*b^4*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^3-1/105*Pi*b^3*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:142
  public void test0018() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^6, x]", //
        "1/60*Pi*b^3/x^2-1/24*Pi*b^3*Cos[Pi*b^2*x^2]/x^2-1/5*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^5+1/15*Pi^2*b^4*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x+1/30*Pi^3*b^5*FresnelS[b*x]^2-7/120*Pi^2*b^5*SinIntegral[Pi*b^2*x^2]+1/15*Pi*b^2*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^3-1/40*b*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:161
  public void test0019() {
    check( //
        "Integrate[x*FresnelC[b*x], x]", //
        "1/2*x^2*FresnelC[b*x]+1/2*FresnelS[b*x]/(Pi*b^2)-1/2*x*Sin[1/2*Pi*b^2*x^2]/(Pi*b)");
  }

  // 8.2 Fresnel integral functions.input:170
  public void test0020() {
    check( //
        "Integrate[FresnelC[b*x]/x^8, x]", //
        "-1/42*b*Cos[1/2*Pi*b^2*x^2]/x^6+1/336*Pi^2*b^5*Cos[1/2*Pi*b^2*x^2]/x^2-1/7*FresnelC[b*x]/x^7+1/672*Pi^3*b^7*SinIntegral[1/2*Pi*b^2*x^2]+1/168*Pi*b^3*Sin[1/2*Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:182
  public void test0021() {
    check( //
        "Integrate[x^2*FresnelC[a+b*x], x]", //
        "-2/3*Cos[1/2*Pi*(a+b*x)^2]/(Pi^2*b^3)+1/3*a^3*FresnelC[a+b*x]/b^3+1/3*x^3*FresnelC[a+b*x]-a*FresnelS[a+b*x]/(Pi*b^3)-a^2*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)+a*(a+b*x)*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)-1/3*(a+b*x)^2*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)");
  }

  // 8.2 Fresnel integral functions.input:242
  public void test0022() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]^n, x]", //
        "FresnelC[b*x]^(1+n)/(b*(1+n))");
  }

  // 8.2 Fresnel integral functions.input:257
  public void test0023() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^4, x]", //
        "-1/12*b/x^2-1/12*b*Cos[Pi*b^2*x^2]/x^2-1/3*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^3-1/6*Pi^2*b^3*FresnelC[b*x]^2-1/6*Pi*b^3*SinIntegral[Pi*b^2*x^2]+1/3*Pi*b^2*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.2 Fresnel integral functions.input:283
  public void test0024() {
    check( //
        "Integrate[FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x^2, x]", //
        "1/2*Pi*b*FresnelC[b*x]^2+1/4*b*SinIntegral[Pi*b^2*x^2]-FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.3 Exponential integral functions.input:175
  public void test0025() {
    check( //
        "Integrate[ExpIntegralEi[b*x]/x^2, x]", //
        "-E^(b*x)/x+b*ExpIntegralEi[b*x]-ExpIntegralEi[b*x]/x");
  }

  // 8.3 Exponential integral functions.input:228
  public void test0026() {
    check( //
        "Integrate[x^2*ExpIntegralEi[d*(a+b*Log[c*x^n])], x]", //
        "1/3*x^3*ExpIntegralEi[d*(a+b*Log[c*x^n])]-1/3*x^3*ExpIntegralEi[(3+b*d*n)*(a+b*Log[c*x^n])/(b*n)]/(E^(3*a/(b*n))*(c*x^n)^(3/n))");
  }

  // 8.3 Exponential integral functions.input:242
  public void test0027() {
    check( //
        "Integrate[E^(b*x)*ExpIntegralEi[b*x]/x^2, x]", //
        "-E^(2*b*x)/x-E^(b*x)*ExpIntegralEi[b*x]/x+1/2*b*ExpIntegralEi[b*x]^2+2*b*ExpIntegralEi[2*b*x]");
  }

  // 8.3 Exponential integral functions.input:252
  public void test0028() {
    check( //
        "Integrate[E^(a+b*x)*x*ExpIntegralEi[c+d*x], x]", //
        "-E^(a+c+(b+d)*x)/(b*(b+d))-E^(a+b*x)*ExpIntegralEi[c+d*x]/b^2+E^(a+b*x)*x*ExpIntegralEi[c+d*x]/b+E^(a-b*c/d)*ExpIntegralEi[(b+d)*(c+d*x)/d]/b^2+E^(a-b*c/d)*c*ExpIntegralEi[(b+d)*(c+d*x)/d]/(b*d)");
  }

  // 8.3 Exponential integral functions.input:269
  public void test0029() {
    check( //
        "Integrate[(d*x)^m*LogIntegral[b*x], x]", //
        "-b*(b*x)^(-2-m)*(d*x)^(2+m)*ExpIntegralEi[(2+m)*Log[b*x]]/(d^2*(1+m))+(d*x)^(1+m)*LogIntegral[b*x]/(d*(1+m))");
  }

  // 8.4 Trig integral functions.input:56
  public void test0030() {
    check( //
        "Integrate[SinIntegral[b*x]*Sin[b*x]/x, x]", //
        "1/2*SinIntegral[b*x]^2");
  }

  // 8.4 Trig integral functions.input:114
  public void test0031() {
    check( //
        "Integrate[CosIntegral[b*x]^2, x]", //
        "x*CosIntegral[b*x]^2+SinIntegral[2*b*x]/b-2*CosIntegral[b*x]*Sin[b*x]/b");
  }

  // 8.4 Trig integral functions.input:156
  public void test0032() {
    check( //
        "Integrate[CosIntegral[b*x]*Cos[b*x], x]", //
        "-1/2*SinIntegral[2*b*x]/b+CosIntegral[b*x]*Sin[b*x]/b");
  }

  // 8.4 Trig integral functions.input:170
  public void test0033() {
    check( //
        "Integrate[CosIntegral[a+b*x]*Sin[a+b*x], x]", //
        "1/2*CosIntegral[2*a+2*b*x]/b-CosIntegral[a+b*x]*Cos[a+b*x]/b+1/2*Log[a+b*x]/b");
  }

  // 8.6 Gamma functions.input:25
  public void test0034() {
    check( //
        "Integrate[Gamma[1,a*x]/x^2, x]", //
        "(-1)/(E^(a*x)*x)-a*ExpIntegralEi[-a*x]");
  }

  // 8.6 Gamma functions.input:137
  public void test0035() {
    check( //
        "Integrate[(c+d*x)^4*Gamma[1,a+b*x], x]", //
        "-24*E^(-a-b*x)*d^4/b^5-24*E^(-a-b*x)*d^3*(c+d*x)/b^4-12*E^(-a-b*x)*d^2*(c+d*x)^2/b^3-4*E^(-a-b*x)*d*(c+d*x)^3/b^2-E^(-a-b*x)*(c+d*x)^4/b");
  }

  // 8.6 Gamma functions.input:145
  public void test0036() {
    check( //
        "Integrate[Gamma[1,a+b*x]/(c+d*x)^4, x]", //
        "-1/3*E^(-a-b*x)/(d*(c+d*x)^3)+1/6*E^(-a-b*x)*b/(d^2*(c+d*x)^2)-1/6*E^(-a-b*x)*b^2/(d^3*(c+d*x))-1/6*E^(-a+b*c/d)*b^3*ExpIntegralEi[-b*(c+d*x)/d]/d^4");
  }

  // 8.6 Gamma functions.input:224
  public void test0037() {
    check( //
        "Integrate[(c+d*x)^m*Gamma[1,a+b*x], x]", //
        "-E^(-a+b*c/d)*(c+d*x)^m*Gamma[1+m,b*(c+d*x)/d]/(b*(b*(c+d*x)/d)^m)");
  }

  // 8.6 Gamma functions.input:245
  public void test0038() {
    check( //
        "Integrate[Gamma[p,d*(a+b*Log[c*x^n])], x]", //
        "x*Gamma[p,d*(a+b*Log[c*x^n])]-x*Gamma[p,-(1-b*d*n)*(a+b*Log[c*x^n])/(b*n)]*(d*(a+b*Log[c*x^n]))^p/(E^(a/(b*n))*(c*x^n)^(1/n)*(-(1-b*d*n)*(a+b*Log[c*x^n])/(b*n))^p)");
  }

  // 8.6 Gamma functions.input:280
  public void test0039() {
    check( //
        "Integrate[x^2*PolyGamma[1,a+b*x], x]", //
        "-2*x*LogGamma[a+b*x]/b^2+2*PolyGamma[-2,a+b*x]/b^3+x^2*PolyGamma[0,a+b*x]/b");
  }

  // 8.7 Zeta function.input:14
  public void test0040() {
    check( //
        "Integrate[-b*PolyGamma[2,a+b*x]/x+Zeta[2,a+b*x]/x^2, x]", //
        "-PolyGamma[1,a+b*x]/x");
  }

  // 8.8 Polylogarithm function.input:41
  public void test0041() {
    check( //
        "Integrate[PolyLog[2,a*x^2], x]", //
        "-4*x+2*x*Log[1-a*x^2]+x*PolyLog[2,a*x^2]+4*ArcTanh[x*Sqrt[a]]/Sqrt[a]");
  }

  // 8.8 Polylogarithm function.input:49
  public void test0042() {
    check( //
        "Integrate[PolyLog[3,a*x^2]/x^3, x]", //
        "a*Log[x]-1/2*a*Log[1-a*x^2]+1/2*Log[1-a*x^2]/x^2-1/2*PolyLog[2,a*x^2]/x^2-1/2*PolyLog[3,a*x^2]/x^2");
  }

  // 8.8 Polylogarithm function.input:57
  public void test0043() {
    check( //
        "Integrate[PolyLog[3,a*x^2]/x^6, x]", //
        "-8/375*a/x^3-8/125*a^2/x+8/125*a^(5/2)*ArcTanh[x*Sqrt[a]]+4/125*Log[1-a*x^2]/x^5-2/25*PolyLog[2,a*x^2]/x^5-1/5*PolyLog[3,a*x^2]/x^5");
  }

  // 8.8 Polylogarithm function.input:174
  public void test0044() {
    check( //
        "Integrate[(d+e*x)*PolyLog[2,c*(a+b*x)], x]", //
        "-1/2*(b*d-a*e)*x/b-1/4*(b*c*d+e-a*c*e)*x/(b*c)-1/8*(d+e*x)^2/e-1/4*(b*c*d+e-a*c*e)^2*Log[1-a*c-b*c*x]/(b^2*c^2*e)-1/2*(b*d-a*e)*(1-a*c-b*c*x)*Log[1-a*c-b*c*x]/(b^2*c)+1/4*(d+e*x)^2*Log[1-a*c-b*c*x]/e-1/2*(b*d-a*e)^2*PolyLog[2,c*(a+b*x)]/(b^2*e)+1/2*(d+e*x)^2*PolyLog[2,c*(a+b*x)]/e");
  }

  // 8.8 Polylogarithm function.input:184
  public void test0045() {
    check( //
        "Integrate[PolyLog[2,x]/((-1+x)*x), x]", //
        "Log[1-x]^2*Log[x]+2*Log[1-x]*PolyLog[2,1-x]+Log[1-x]*PolyLog[2,x]-2*PolyLog[3,1-x]-PolyLog[3,x]");
  }

  // 8.9 Product logarithm function.input:16
  public void test0046() {
    check( //
        "Integrate[1/ProductLog[a+b*x]^3, x]", //
        "3/2*ExpIntegralEi[ProductLog[a+b*x]]/b+1/2*(-a-b*x)/(b*ProductLog[a+b*x]^3)-3/2*(a+b*x)/(b*ProductLog[a+b*x]^2)");
  }

  // 8.9 Product logarithm function.input:24
  public void test0047() {
    check( //
        "Integrate[1/(c*ProductLog[a+b*x])^(5/2), x]", //
        "-2/3*(a+b*x)/(b*(c*ProductLog[a+b*x])^(5/2))-10/3*(a+b*x)/(b*c*(c*ProductLog[a+b*x])^(3/2))+10/3*Erfi[Sqrt[c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/(b*c^(5/2))");
  }

  // 8.9 Product logarithm function.input:32
  public void test0048() {
    check( //
        "Integrate[1/(-c*ProductLog[a+b*x])^(7/2), x]", //
        "-2/5*(a+b*x)/(b*(-c*ProductLog[a+b*x])^(7/2))+14/15*(a+b*x)/(b*c*(-c*ProductLog[a+b*x])^(5/2))-28/15*(a+b*x)/(b*c^2*(-c*ProductLog[a+b*x])^(3/2))+28/15*Erf[Sqrt[-c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/(b*c^(7/2))");
  }

  // 8.9 Product logarithm function.input:44
  public void test0049() {
    check( //
        "Integrate[x*ProductLog[a+b*x]^2, x]", //
        "-4*a*x/b+3/4*(a+b*x)^2/b^2+3/8*(a+b*x)^2/(b^2*ProductLog[a+b*x]^2)+4*a*(a+b*x)/(b^2*ProductLog[a+b*x])-3/4*(a+b*x)^2/(b^2*ProductLog[a+b*x])+2*a*(a+b*x)*ProductLog[a+b*x]/b^2-1/2*(a+b*x)^2*ProductLog[a+b*x]/b^2-a*(a+b*x)*ProductLog[a+b*x]^2/b^2+1/2*(a+b*x)^2*ProductLog[a+b*x]^2/b^2");
  }

  // 8.9 Product logarithm function.input:70
  public void test0050() {
    check( //
        "Integrate[x/(d+d*ProductLog[a+b*x]), x]", //
        "-1/4*(a+b*x)^2/(b^2*d*ProductLog[a+b*x]^2)-a*(a+b*x)/(b^2*d*ProductLog[a+b*x])+1/2*(a+b*x)^2/(b^2*d*ProductLog[a+b*x])");
  }

  // 8.9 Product logarithm function.input:100
  public void test0051() {
    check( //
        "Integrate[ProductLog[a*x]^3, x]", //
        "-18*x+18*x/ProductLog[a*x]+9*x*ProductLog[a*x]-3*x*ProductLog[a*x]^2+x*ProductLog[a*x]^3");
  }

  // 8.9 Product logarithm function.input:140
  public void test0052() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a*x]], x]", //
        "1/4*Erfi[Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/a-1/2*c*x/Sqrt[c*ProductLog[a*x]]+x*Sqrt[c*ProductLog[a*x]]");
  }

  // 8.9 Product logarithm function.input:148
  public void test0053() {
    check( //
        "Integrate[x^3/Sqrt[c*ProductLog[a*x]], x]", //
        "15/2048*c^3*x^4/(c*ProductLog[a*x])^(7/2)-5/256*c^2*x^4/(c*ProductLog[a*x])^(5/2)+1/32*c*x^4/(c*ProductLog[a*x])^(3/2)-15/8192*Erfi[2*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]/(a^4*Sqrt[c])+1/4*x^4/Sqrt[c*ProductLog[a*x]]");
  }

  // 8.9 Product logarithm function.input:156
  public void test0054() {
    check( //
        "Integrate[1/(x^5*Sqrt[c*ProductLog[a*x]]), x]", //
        "16/315*(c*ProductLog[a*x])^(3/2)/(c^2*x^4)-128/945*(c*ProductLog[a*x])^(5/2)/(c^3*x^4)+1024/945*(c*ProductLog[a*x])^(7/2)/(c^4*x^4)+2048/945*a^4*Erf[2*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]/Sqrt[c]+(-2/9)/(x^4*Sqrt[c*ProductLog[a*x]])-2/63*Sqrt[c*ProductLog[a*x]]/(c*x^4)");
  }

  // 8.9 Product logarithm function.input:208
  public void test0055() {
    check( //
        "Integrate[x^5/ProductLog[a*x^2], x]", //
        "-1/54*x^6/ProductLog[a*x^2]^3+1/18*x^6/ProductLog[a*x^2]^2+1/6*x^6/ProductLog[a*x^2]");
  }

  // 8.9 Product logarithm function.input:245
  public void test0056() {
    check( //
        "Integrate[x^7/Sqrt[c*ProductLog[a*x^2]], x]", //
        "15/4096*c^3*x^8/(c*ProductLog[a*x^2])^(7/2)-5/512*c^2*x^8/(c*ProductLog[a*x^2])^(5/2)+1/64*c*x^8/(c*ProductLog[a*x^2])^(3/2)-15/16384*Erfi[2*Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]]*Sqrt[Pi]/(a^4*Sqrt[c])+1/8*x^8/Sqrt[c*ProductLog[a*x^2]]");
  }

  // 8.9 Product logarithm function.input:255
  public void test0057() {
    check( //
        "Integrate[1/(x^3*Sqrt[c*ProductLog[a*x^2]]), x]", //
        "-1/3*a*Erf[Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]]*Sqrt[Pi]/Sqrt[c]+(-1/3)/(x^2*Sqrt[c*ProductLog[a*x^2]])-1/3*Sqrt[c*ProductLog[a*x^2]]/(c*x^2)");
  }

  // 8.9 Product logarithm function.input:269
  public void test0058() {
    check( //
        "Integrate[x^2*ProductLog[a/x], x]", //
        "-3/2*a^3*ExpIntegralEi[-3*ProductLog[a/x]]+1/2*x^3*ProductLog[a/x]-1/2*x^3*ProductLog[a/x]^2");
  }

  // 8.9 Product logarithm function.input:277
  public void test0059() {
    check( //
        "Integrate[x^4*ProductLog[a/x]^2, x]", //
        "25/3*a^5*ExpIntegralEi[-5*ProductLog[a/x]]+1/3*x^5*ProductLog[a/x]^2-1/3*x^5*ProductLog[a/x]^3+5/3*x^5*ProductLog[a/x]^4");
  }

  // 8.9 Product logarithm function.input:329
  public void test0060() {
    check( //
        "Integrate[x^(-1-n)*(c*ProductLog[a*x^n])^(9/2), x]", //
        "-135/8*c^3*(c*ProductLog[a*x^n])^(3/2)/(n*x^n)-45/4*c^2*(c*ProductLog[a*x^n])^(5/2)/(n*x^n)-9/2*c*(c*ProductLog[a*x^n])^(7/2)/(n*x^n)-(c*ProductLog[a*x^n])^(9/2)/(n*x^n)+135/16*a*c^(9/2)*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/n");
  }

  // 8.9 Product logarithm function.input:345
  public void test0061() {
    check( //
        "Integrate[x^(-1+n)*(c*ProductLog[a*x^n])^(5/2), x]", //
        "-5/2*c*x^n*(c*ProductLog[a*x^n])^(3/2)/n+x^n*(c*ProductLog[a*x^n])^(5/2)/n+75/16*c^(5/2)*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(a*n)-75/8*c^3*x^n/(n*Sqrt[c*ProductLog[a*x^n]])+25/4*c^2*x^n*Sqrt[c*ProductLog[a*x^n]]/n");
  }

  // 8.9 Product logarithm function.input:371
  public void test0062() {
    check( //
        "Integrate[x^(-1+n*(3-p))*(c*ProductLog[a*x^n])^p, x]", //
        "-2*c^3*p*x^(n*(3-p))*(c*ProductLog[a*x^n])^(-3+p)/(n*(3-p)^4)+2*c^2*p*x^(n*(3-p))*(c*ProductLog[a*x^n])^(-2+p)/(n*(3-p)^3)-c*p*x^(n*(3-p))*(c*ProductLog[a*x^n])^(-1+p)/(n*(3-p)^2)+x^(n*(3-p))*(c*ProductLog[a*x^n])^p/(n*(3-p))");
  }

  // 8.1 Error functions.input:11
  public void test0063() {
    check( //
        "Integrate[x^3*Erf[b*x], x]", //
        "-3/16*Erf[b*x]/b^4+1/4*x^4*Erf[b*x]+3/8*x/(E^(b^2*x^2)*b^3*Sqrt[Pi])+1/4*x^3/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:37
  public void test0064() {
    check( //
        "Integrate[x^5*Erf[b*x]^2, x]", //
        "11/12/(E^(2*b^2*x^2)*Pi*b^6)+7/12*x^2/(E^(2*b^2*x^2)*Pi*b^4)+1/6*x^4/(E^(2*b^2*x^2)*Pi*b^2)-5/16*Erf[b*x]^2/b^6+1/6*x^6*Erf[b*x]^2+5/4*x*Erf[b*x]/(E^(b^2*x^2)*b^5*Sqrt[Pi])+5/6*x^3*Erf[b*x]/(E^(b^2*x^2)*b^3*Sqrt[Pi])+1/3*x^5*Erf[b*x]/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:146
  public void test0065() {
    check( //
        "Integrate[x^6*Erfc[b*x], x]", //
        "1/7*x^7*Erfc[b*x]+(-6/7)/(E^(b^2*x^2)*b^7*Sqrt[Pi])-6/7*x^2/(E^(b^2*x^2)*b^5*Sqrt[Pi])-3/7*x^4/(E^(b^2*x^2)*b^3*Sqrt[Pi])-1/7*x^6/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:156
  public void test0066() {
    check( //
        "Integrate[(c+d*x)^2*Erfc[a+b*x], x]", //
        "1/2*d*(b*c-a*d)*Erf[a+b*x]/b^3+1/3*(b*c-a*d)^3*Erf[a+b*x]/(b^3*d)+1/3*(c+d*x)^3*Erfc[a+b*x]/d-1/3*d^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])-(b*c-a*d)^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])-d*(b*c-a*d)*(a+b*x)/(E^((a+b*x)^2)*b^3*Sqrt[Pi])-1/3*d^2*(a+b*x)^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:172
  public void test0067() {
    check( //
        "Integrate[Erfc[b*x]^2/x^7, x]", //
        "-1/15*b^2/(E^(2*b^2*x^2)*Pi*x^4)+2/9*b^4/(E^(2*b^2*x^2)*Pi*x^2)+28/45*b^6*ExpIntegralEi[-2*b^2*x^2]/Pi-4/45*b^6*Erfc[b*x]^2-1/6*Erfc[b*x]^2/x^6+2/15*b*Erfc[b*x]/(E^(b^2*x^2)*x^5*Sqrt[Pi])-4/45*b^3*Erfc[b*x]/(E^(b^2*x^2)*x^3*Sqrt[Pi])+8/45*b^5*Erfc[b*x]/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:189
  public void test0068() {
    check( //
        "Integrate[x*Erfc[d*(a+b*Log[c*x^n])], x]", //
        "1/2*E^((1-2*a*b*d^2*n)/(b^2*d^2*n^2))*x^2*Erf[(a*b*d^2+(-1)/n+b^2*d^2*Log[c*x^n])/(b*d)]/(c*x^n)^(2/n)+1/2*x^2*Erfc[d*(a+b*Log[c*x^n])]");
  }

  // 8.1 Error functions.input:219
  public void test0069() {
    check( //
        "Integrate[E^(c+b^2*x^2)*x^3*Erfc[b*x], x]", //
        "-1/2*E^(c+b^2*x^2)*Erfc[b*x]/b^4+1/2*E^(c+b^2*x^2)*x^2*Erfc[b*x]/b^2-E^c*x/(b^3*Sqrt[Pi])+1/3*E^c*x^3/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:238
  public void test0070() {
    check( //
        "Integrate[Erfc[b*x]/(E^(b^2*x^2)*x^2), x]", //
        "-Erfc[b*x]/(E^(b^2*x^2)*x)-b*ExpIntegralEi[-2*b^2*x^2]/Sqrt[Pi]+1/2*b*Erfc[b*x]^2*Sqrt[Pi]");
  }

  // 8.1 Error functions.input:272
  public void test0071() {
    check( //
        "Integrate[Erfi[b*x]/x^3, x]", //
        "b^2*Erfi[b*x]-1/2*Erfi[b*x]/x^2-E^(b^2*x^2)*b/(x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:280
  public void test0072() {
    check( //
        "Integrate[Erfi[b*x]/x^4, x]", //
        "-1/3*Erfi[b*x]/x^3-1/3*E^(b^2*x^2)*b/(x^2*Sqrt[Pi])+1/3*b^3*ExpIntegralEi[b^2*x^2]/Sqrt[Pi]");
  }

  // 8.1 Error functions.input:297
  public void test0073() {
    check( //
        "Integrate[x*Erfi[b*x]^2, x]", //
        "1/2*E^(2*b^2*x^2)/(Pi*b^2)+1/4*Erfi[b*x]^2/b^2+1/2*x^2*Erfi[b*x]^2-E^(b^2*x^2)*x*Erfi[b*x]/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:337
  public void test0074() {
    check( //
        "Integrate[E^(c+d*x^2)*x^3*Erfi[b*x], x]", //
        "-1/2*E^(c+d*x^2)*Erfi[b*x]/d^2+1/2*E^(c+d*x^2)*x^2*Erfi[b*x]/d+1/4*E^c*b*Erfi[x*Sqrt[b^2+d]]/(d*(b^2+d)^(3/2))-1/2*E^(c+(b^2+d)*x^2)*b*x/(d*(b^2+d)*Sqrt[Pi])+1/2*E^c*b*Erfi[x*Sqrt[b^2+d]]/(d^2*Sqrt[b^2+d])");
  }

  // 8.1 Error functions.input:366
  public void test0075() {
    check( //
        "Integrate[E^(c+b^2*x^2)*x^4*Erfi[b*x], x]", //
        "-3/4*E^(c+b^2*x^2)*x*Erfi[b*x]/b^4+1/2*E^(c+b^2*x^2)*x^3*Erfi[b*x]/b^2+1/2*E^(c+2*b^2*x^2)/(b^5*Sqrt[Pi])-1/4*E^(c+2*b^2*x^2)*x^2/(b^3*Sqrt[Pi])+3/16*E^c*Erfi[b*x]^2*Sqrt[Pi]/b^5");
  }

  // 8.2 Fresnel integral functions.input:10
  public void test0076() {
    check( //
        "Integrate[x^7*FresnelS[b*x], x]", //
        "-35/8*x^3*Cos[1/2*Pi*b^2*x^2]/(Pi^3*b^5)+1/8*x^7*Cos[1/2*Pi*b^2*x^2]/(Pi*b)-105/8*FresnelS[b*x]/(Pi^4*b^8)+1/8*x^8*FresnelS[b*x]+105/8*x*Sin[1/2*Pi*b^2*x^2]/(Pi^4*b^7)-7/8*x^5*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.2 Fresnel integral functions.input:27
  public void test0077() {
    check( //
        "Integrate[FresnelS[b*x]/x^10, x]", //
        "-1/432*Pi*b^3*Cos[1/2*Pi*b^2*x^2]/x^6+1/3456*Pi^3*b^7*Cos[1/2*Pi*b^2*x^2]/x^2-1/9*FresnelS[b*x]/x^9+1/6912*Pi^4*b^9*SinIntegral[1/2*Pi*b^2*x^2]-1/72*b*Sin[1/2*Pi*b^2*x^2]/x^8+1/1728*Pi^2*b^5*Sin[1/2*Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:62
  public void test0078() {
    check( //
        "Integrate[FresnelS[b*x]^2/x^9, x]", //
        "-1/336*b^2/x^6+1/1680*Pi^2*b^6/x^2+1/336*b^2*Cos[Pi*b^2*x^2]/x^6-1/336*Pi^2*b^6*Cos[Pi*b^2*x^2]/x^2-1/140*Pi*b^3*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^5+1/420*Pi^3*b^7*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x+1/840*Pi^4*b^8*FresnelS[b*x]^2-1/8*FresnelS[b*x]^2/x^8-1/280*Pi^3*b^8*SinIntegral[Pi*b^2*x^2]-1/28*b*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^7+1/420*Pi^2*b^5*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^3-1/420*Pi*b^4*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:146
  public void test0079() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^10, x]", //
        "1/756*Pi*b^3/x^6-1/3780*Pi^3*b^7/x^2-11/3024*Pi*b^3*Cos[Pi*b^2*x^2]/x^6+5/2016*Pi^3*b^7*Cos[Pi*b^2*x^2]/x^2-1/9*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^9+1/315*Pi^2*b^4*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x^5-1/945*Pi^4*b^8*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/x-1/1890*Pi^5*b^9*FresnelS[b*x]^2+83/30240*Pi^4*b^9*SinIntegral[Pi*b^2*x^2]+1/63*Pi*b^2*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^7-1/945*Pi^3*b^6*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/x^3-1/144*b*Sin[Pi*b^2*x^2]/x^8+67/30240*Pi^2*b^5*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:171
  public void test0080() {
    check( //
        "Integrate[FresnelC[b*x]/x^9, x]", //
        "-1/56*b*Cos[1/2*Pi*b^2*x^2]/x^7+1/840*Pi^2*b^5*Cos[1/2*Pi*b^2*x^2]/x^3+1/840*Pi^4*b^8*FresnelC[b*x]-1/8*FresnelC[b*x]/x^8+1/280*Pi*b^3*Sin[1/2*Pi*b^2*x^2]/x^5-1/840*Pi^3*b^7*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.2 Fresnel integral functions.input:183
  public void test0081() {
    check( //
        "Integrate[x*FresnelC[a+b*x], x]", //
        "-1/2*a^2*FresnelC[a+b*x]/b^2+1/2*x^2*FresnelC[a+b*x]+1/2*FresnelS[a+b*x]/(Pi*b^2)+a*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^2)-1/2*(a+b*x)*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^2)");
  }

  // 8.2 Fresnel integral functions.input:203
  public void test0082() {
    check( //
        "Integrate[FresnelC[b*x]^2/x^5, x]", //
        "-1/24*b^2/x^2-1/24*b^2*Cos[Pi*b^2*x^2]/x^2-1/6*b*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^3-1/12*Pi^2*b^4*FresnelC[b*x]^2-1/4*FresnelC[b*x]^2/x^4-1/12*Pi*b^4*SinIntegral[Pi*b^2*x^2]+1/6*Pi*b^3*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.2 Fresnel integral functions.input:245
  public void test0083() {
    check( //
        "Integrate[x^8*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x], x]", //
        "105/4*x^2/(Pi^4*b^7)-7/12*x^6/(Pi^2*b^3)-55/4*x^2*Cos[Pi*b^2*x^2]/(Pi^4*b^7)+1/4*x^6*Cos[Pi*b^2*x^2]/(Pi^2*b^3)-105*x*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/(Pi^4*b^8)+7*x^5*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/(Pi^2*b^4)+105/2*FresnelC[b*x]^2/(Pi^4*b^9)-35*x^3*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi^3*b^6)+x^7*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi*b^2)+40*Sin[Pi*b^2*x^2]/(Pi^5*b^9)-5/2*x^4*Sin[Pi*b^2*x^2]/(Pi^3*b^5)");
  }

  // 8.2 Fresnel integral functions.input:261
  public void test0084() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^8, x]", //
        "-1/84*b/x^6+1/420*Pi^2*b^5/x^2-1/84*b*Cos[Pi*b^2*x^2]/x^6+1/84*Pi^2*b^5*Cos[Pi*b^2*x^2]/x^2-1/7*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^7+1/105*Pi^2*b^4*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^3+1/210*Pi^4*b^7*FresnelC[b*x]^2+1/70*Pi^3*b^7*SinIntegral[Pi*b^2*x^2]+1/35*Pi*b^2*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x^5-1/105*Pi^3*b^6*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x+1/105*Pi*b^3*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:287
  public void test0085() {
    check( //
        "Integrate[FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x^6, x]", //
        "-1/60*Pi*b^3/x^2-1/24*Pi*b^3*Cos[Pi*b^2*x^2]/x^2-1/15*Pi*b^2*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/x^3-1/30*Pi^3*b^5*FresnelC[b*x]^2-7/120*Pi^2*b^5*SinIntegral[Pi*b^2*x^2]-1/5*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x^5+1/15*Pi^2*b^4*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/x-1/40*b*Sin[Pi*b^2*x^2]/x^4");
  }

  // 8.3 Exponential integral functions.input:229
  public void test0086() {
    check( //
        "Integrate[x*ExpIntegralEi[d*(a+b*Log[c*x^n])], x]", //
        "1/2*x^2*ExpIntegralEi[d*(a+b*Log[c*x^n])]-1/2*x^2*ExpIntegralEi[(2+b*d*n)*(a+b*Log[c*x^n])/(b*n)]/(E^(2*a/(b*n))*(c*x^n)^(2/n))");
  }

  // 8.3 Exponential integral functions.input:243
  public void test0087() {
    check( //
        "Integrate[E^(b*x)*ExpIntegralEi[b*x]/x, x]", //
        "1/2*ExpIntegralEi[b*x]^2");
  }

  // 8.3 Exponential integral functions.input:274
  public void test0088() {
    check( //
        "Integrate[x^2*LogIntegral[a+b*x], x]", //
        "-a^2*ExpIntegralEi[2*Log[a+b*x]]/b^3+a*ExpIntegralEi[3*Log[a+b*x]]/b^3-1/3*ExpIntegralEi[4*Log[a+b*x]]/b^3+1/3*a^3*LogIntegral[a+b*x]/b^3+1/3*x^3*LogIntegral[a+b*x]");
  }

  // 8.4 Trig integral functions.input:16
  public void test0089() {
    check( //
        "Integrate[SinIntegral[b*x]/x^2, x]", //
        "b*CosIntegral[b*x]-SinIntegral[b*x]/x-Sin[b*x]/x");
  }

  // 8.4 Trig integral functions.input:57
  public void test0090() {
    check( //
        "Integrate[SinIntegral[b*x]*Sin[b*x], x]", //
        "-Cos[b*x]*SinIntegral[b*x]/b+1/2*SinIntegral[2*b*x]/b");
  }

  // 8.4 Trig integral functions.input:84
  public void test0091() {
    check( //
        "Integrate[Cos[a+b*x]*SinIntegral[a+b*x], x]", //
        "1/2*CosIntegral[2*a+2*b*x]/b-1/2*Log[a+b*x]/b+SinIntegral[a+b*x]*Sin[a+b*x]/b");
  }

  // 8.4 Trig integral functions.input:131
  public void test0092() {
    check( //
        "Integrate[CosIntegral[a+b*x]^2, x]", //
        "(a+b*x)*CosIntegral[a+b*x]^2/b+SinIntegral[2*a+2*b*x]/b-2*CosIntegral[a+b*x]*Sin[a+b*x]/b");
  }

  // 8.5 Hyperbolic integral functions.input:126
  public void test0093() {
    check( //
        "Integrate[CoshIntegral[a+b*x]/x^2, x]", //
        "-b*CoshIntegral[a+b*x]/a-CoshIntegral[a+b*x]/x+b*CoshIntegral[b*x]*Cosh[a]/a+b*SinhIntegral[b*x]*Sinh[a]/a");
  }

  // 8.5 Hyperbolic integral functions.input:140
  public void test0094() {
    check( //
        "Integrate[CoshIntegral[d*(a+b*Log[c*x^n])]/x, x]", //
        "CoshIntegral[d*(a+b*Log[c*x^n])]*(a+b*Log[c*x^n])/(b*n)-Sinh[d*(a+b*Log[c*x^n])]/(b*d*n)");
  }

  // 8.6 Gamma functions.input:26
  public void test0095() {
    check( //
        "Integrate[Gamma[1,a*x]/x^3, x]", //
        "(-1/2)/(E^(a*x)*x^2)+1/2*a/(E^(a*x)*x)+1/2*a^2*ExpIntegralEi[-a*x]");
  }

  // 8.6 Gamma functions.input:138
  public void test0096() {
    check( //
        "Integrate[(c+d*x)^3*Gamma[1,a+b*x], x]", //
        "-6*E^(-a-b*x)*d^3/b^4-6*E^(-a-b*x)*d^2*(c+d*x)/b^3-3*E^(-a-b*x)*d*(c+d*x)^2/b^2-E^(-a-b*x)*(c+d*x)^3/b");
  }

  // 8.6 Gamma functions.input:246
  public void test0097() {
    check( //
        "Integrate[Gamma[p,d*(a+b*Log[c*x^n])]/x, x]", //
        "-Gamma[1+p,a*d+b*d*Log[c*x^n]]/(b*d*n)+Gamma[p,a*d+b*d*Log[c*x^n]]*(a+b*Log[c*x^n])/(b*n)");
  }

  // 8.6 Gamma functions.input:281
  public void test0098() {
    check( //
        "Integrate[PolyGamma[1,a+b*x]/x^2-b*PolyGamma[2,a+b*x]/x, x]", //
        "-PolyGamma[1,a+b*x]/x");
  }

  // 8.8 Polylogarithm function.input:16
  public void test0099() {
    check( //
        "Integrate[PolyLog[2,a*x], x]", //
        "-x-(1-a*x)*Log[1-a*x]/a+x*PolyLog[2,a*x]");
  }

  // 8.8 Polylogarithm function.input:34
  public void test0100() {
    check( //
        "Integrate[x*PolyLog[2,a*x^2], x]", //
        "-1/2*x^2-1/2*(1-a*x^2)*Log[1-a*x^2]/a+1/2*x^2*PolyLog[2,a*x^2]");
  }

  // 8.8 Polylogarithm function.input:42
  public void test0101() {
    check( //
        "Integrate[PolyLog[2,a*x^2]/x^2, x]", //
        "2*Log[1-a*x^2]/x-PolyLog[2,a*x^2]/x+4*ArcTanh[x*Sqrt[a]]*Sqrt[a]");
  }

  // 8.8 Polylogarithm function.input:50
  public void test0102() {
    check( //
        "Integrate[PolyLog[3,a*x^2]/x^5, x]", //
        "-1/16*a/x^2+1/8*a^2*Log[x]-1/16*a^2*Log[1-a*x^2]+1/16*Log[1-a*x^2]/x^4-1/8*PolyLog[2,a*x^2]/x^4-1/4*PolyLog[3,a*x^2]/x^4");
  }

  // 8.8 Polylogarithm function.input:175
  public void test0103() {
    check( //
        "Integrate[PolyLog[2,c*(a+b*x)], x]", //
        "-x-(1-a*c-b*c*x)*Log[1-a*c-b*c*x]/(b*c)+a*PolyLog[2,c*(a+b*x)]/b+x*PolyLog[2,c*(a+b*x)]");
  }

  // 8.8 Polylogarithm function.input:185
  public void test0104() {
    check( //
        "Integrate[-PolyLog[2,x]/((1-x)*x), x]", //
        "Log[1-x]^2*Log[x]+2*Log[1-x]*PolyLog[2,1-x]+Log[1-x]*PolyLog[2,x]-2*PolyLog[3,1-x]-PolyLog[3,x]");
  }

  // 8.8 Polylogarithm function.input:197
  public void test0105() {
    check( //
        "Integrate[x^3*PolyLog[n,d*(F^(c*(a+b*x)))^p], x]", //
        "x^3*PolyLog[1+n,d*(F^(c*(a+b*x)))^p]/(b*c*p*Log[F])-3*x^2*PolyLog[2+n,d*(F^(c*(a+b*x)))^p]/(b^2*c^2*p^2*Log[F]^2)+6*x*PolyLog[3+n,d*(F^(c*(a+b*x)))^p]/(b^3*c^3*p^3*Log[F]^3)-6*PolyLog[4+n,d*(F^(c*(a+b*x)))^p]/(b^4*c^4*p^4*Log[F]^4)");
  }

  // 8.8 Polylogarithm function.input:210
  public void test0106() {
    check( //
        "Integrate[Log[1-c*x]*PolyLog[2,c*x]/x, x]", //
        "-1/2*PolyLog[2,c*x]^2");
  }

  // 8.8 Polylogarithm function.input:220
  public void test0107() {
    check( //
        "Integrate[(g+h*Log[1-c*x])*PolyLog[2,c*x]/x, x]", //
        "-1/2*h*PolyLog[2,c*x]^2+g*PolyLog[3,c*x]");
  }

  // 8.9 Product logarithm function.input:17
  public void test0108() {
    check( //
        "Integrate[1/ProductLog[a+b*x]^4, x]", //
        "2/3*ExpIntegralEi[ProductLog[a+b*x]]/b+1/3*(-a-b*x)/(b*ProductLog[a+b*x]^4)-2/3*(a+b*x)/(b*ProductLog[a+b*x]^3)-2/3*(a+b*x)/(b*ProductLog[a+b*x]^2)");
  }

  // 8.9 Product logarithm function.input:25
  public void test0109() {
    check( //
        "Integrate[1/(c*ProductLog[a+b*x])^(7/2), x]", //
        "-2/5*(a+b*x)/(b*(c*ProductLog[a+b*x])^(7/2))-14/15*(a+b*x)/(b*c*(c*ProductLog[a+b*x])^(5/2))-28/15*(a+b*x)/(b*c^2*(c*ProductLog[a+b*x])^(3/2))+28/15*Erfi[Sqrt[c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/(b*c^(7/2))");
  }

  // 8.9 Product logarithm function.input:45
  public void test0110() {
    check( //
        "Integrate[ProductLog[a+b*x]^2, x]", //
        "4*x-4*(a+b*x)/(b*ProductLog[a+b*x])-2*(a+b*x)*ProductLog[a+b*x]/b+(a+b*x)*ProductLog[a+b*x]^2/b");
  }

  // 8.9 Product logarithm function.input:57
  public void test0111() {
    check( //
        "Integrate[1/Sqrt[-c*ProductLog[a+b*x]], x]", //
        "-1/2*Erf[Sqrt[-c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/(b*Sqrt[c])+(a+b*x)/(b*Sqrt[-c*ProductLog[a+b*x]])");
  }

  // 8.9 Product logarithm function.input:71
  public void test0112() {
    check( //
        "Integrate[1/(d+d*ProductLog[a+b*x]), x]", //
        "(a+b*x)/(b*d*ProductLog[a+b*x])");
  }

  // 8.9 Product logarithm function.input:133
  public void test0113() {
    check( //
        "Integrate[1/ProductLog[a*x]^3, x]", //
        "3/2*ExpIntegralEi[ProductLog[a*x]]/a-1/2*x/ProductLog[a*x]^3-3/2*x/ProductLog[a*x]^2");
  }

  // 8.9 Product logarithm function.input:157
  public void test0114() {
    check( //
        "Integrate[x^2*(c*ProductLog[a*x])^p, x]", //
        "3^(-3-p)*x^2*Gamma[3+p,-3*ProductLog[a*x]]*(-ProductLog[a*x])^(-2-p)*(c*ProductLog[a*x])^p/(E^(2*ProductLog[a*x])*a)+3^(-4-p)*x^2*Gamma[4+p,-3*ProductLog[a*x]]*(-ProductLog[a*x])^(-3-p)*(c*ProductLog[a*x])^(1+p)/(E^(2*ProductLog[a*x])*a*c)");
  }

  // 8.9 Product logarithm function.input:195
  public void test0115() {
    check( //
        "Integrate[ProductLog[a*x^2]^2/x^9, x]", //
        "2*a^4*ExpIntegralEi[-4*ProductLog[a*x^2]]-1/4*ProductLog[a*x^2]^2/x^8+1/2*ProductLog[a*x^2]^3/x^8");
  }

  // 8.9 Product logarithm function.input:235
  public void test0116() {
    check( //
        "Integrate[x^2*Sqrt[c*ProductLog[a*x^2]], x]", //
        "2/27*c^2*x^3/(c*ProductLog[a*x^2])^(3/2)-1/9*c*x^3/Sqrt[c*ProductLog[a*x^2]]+1/3*x^3*Sqrt[c*ProductLog[a*x^2]]");
  }

  // 8.9 Product logarithm function.input:246
  public void test0117() {
    check( //
        "Integrate[x^6/Sqrt[c*ProductLog[a*x^2]], x]", //
        "8/2401*c^3*x^7/(c*ProductLog[a*x^2])^(7/2)-4/343*c^2*x^7/(c*ProductLog[a*x^2])^(5/2)+1/49*c*x^7/(c*ProductLog[a*x^2])^(3/2)+1/7*x^7/Sqrt[c*ProductLog[a*x^2]]");
  }

  // 8.9 Product logarithm function.input:257
  public void test0118() {
    check( //
        "Integrate[1/(x^5*Sqrt[c*ProductLog[a*x^2]]), x]", //
        "4/15*(c*ProductLog[a*x^2])^(3/2)/(c^2*x^4)+4/15*a^2*Erf[Sqrt[2]*Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]]*Sqrt[2*Pi]/Sqrt[c]+(-1/5)/(x^4*Sqrt[c*ProductLog[a*x^2]])-1/15*Sqrt[c*ProductLog[a*x^2]]/(c*x^4)");
  }

  // 8.9 Product logarithm function.input:278
  public void test0119() {
    check( //
        "Integrate[x^3*ProductLog[a/x]^2, x]", //
        "-4*a^4*ExpIntegralEi[-4*ProductLog[a/x]]+1/2*x^4*ProductLog[a/x]^2-x^4*ProductLog[a/x]^3");
  }

  // 8.9 Product logarithm function.input:330
  public void test0120() {
    check( //
        "Integrate[x^(-1-n)*(c*ProductLog[a*x^n])^(7/2), x]", //
        "-21/4*c^2*(c*ProductLog[a*x^n])^(3/2)/(n*x^n)-7/2*c*(c*ProductLog[a*x^n])^(5/2)/(n*x^n)-(c*ProductLog[a*x^n])^(7/2)/(n*x^n)+21/8*a*c^(7/2)*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/n");
  }

  // 8.9 Product logarithm function.input:346
  public void test0121() {
    check( //
        "Integrate[x^(-1+n)*(c*ProductLog[a*x^n])^(3/2), x]", //
        "x^n*(c*ProductLog[a*x^n])^(3/2)/n-9/8*c^(3/2)*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(a*n)+9/4*c^2*x^n/(n*Sqrt[c*ProductLog[a*x^n]])-3/2*c*x^n*Sqrt[c*ProductLog[a*x^n]]/n");
  }

  // 8.9 Product logarithm function.input:402
  public void test0122() {
    check( //
        "Integrate[1/(x^3*(1+ProductLog[a/x^2])), x]", //
        "(-1/2)/(x^2*ProductLog[a/x^2])");
  }

  // 8.1 Error functions.input:15
  public void test0123() {
    check( //
        "Integrate[Erf[b*x]/x^5, x]", //
        "1/3*b^4*Erf[b*x]-1/4*Erf[b*x]/x^4-1/6*b/(E^(b^2*x^2)*x^3*Sqrt[Pi])+1/3*b^3/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:23
  public void test0124() {
    check( //
        "Integrate[Erf[b*x]/x^6, x]", //
        "-1/5*Erf[b*x]/x^5-1/10*b/(E^(b^2*x^2)*x^4*Sqrt[Pi])+1/10*b^3/(E^(b^2*x^2)*x^2*Sqrt[Pi])+1/10*b^5*ExpIntegralEi[-b^2*x^2]/Sqrt[Pi]");
  }

  // 8.1 Error functions.input:41
  public void test0125() {
    check( //
        "Integrate[Erf[b*x]^2/x^3, x]", //
        "2*b^2*ExpIntegralEi[-2*b^2*x^2]/Pi-b^2*Erf[b*x]^2-1/2*Erf[b*x]^2/x^2-2*b*Erf[b*x]/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:107
  public void test0126() {
    check( //
        "Integrate[x^2*Erf[b*x]/E^(b^2*x^2), x]", //
        "-1/2*x*Erf[b*x]/(E^(b^2*x^2)*b^2)+(-1/4)/(E^(2*b^2*x^2)*b^3*Sqrt[Pi])+1/8*Erf[b*x]^2*Sqrt[Pi]/b^3");
  }

  // 8.1 Error functions.input:140
  public void test0127() {
    check( //
        "Integrate[x^3*Erfc[b*x], x]", //
        "3/16*Erf[b*x]/b^4+1/4*x^4*Erfc[b*x]-3/8*x/(E^(b^2*x^2)*b^3*Sqrt[Pi])-1/4*x^3/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:166
  public void test0128() {
    check( //
        "Integrate[x^5*Erfc[b*x]^2, x]", //
        "11/12/(E^(2*b^2*x^2)*Pi*b^6)+7/12*x^2/(E^(2*b^2*x^2)*Pi*b^4)+1/6*x^4/(E^(2*b^2*x^2)*Pi*b^2)-5/16*Erfc[b*x]^2/b^6+1/6*x^6*Erfc[b*x]^2-5/4*x*Erfc[b*x]/(E^(b^2*x^2)*b^5*Sqrt[Pi])-5/6*x^3*Erfc[b*x]/(E^(b^2*x^2)*b^3*Sqrt[Pi])-1/3*x^5*Erfc[b*x]/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:275
  public void test0129() {
    check( //
        "Integrate[x^6*Erfi[b*x], x]", //
        "1/7*x^7*Erfi[b*x]+6/7*E^(b^2*x^2)/(b^7*Sqrt[Pi])-6/7*E^(b^2*x^2)*x^2/(b^5*Sqrt[Pi])+3/7*E^(b^2*x^2)*x^4/(b^3*Sqrt[Pi])-1/7*E^(b^2*x^2)*x^6/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:285
  public void test0130() {
    check( //
        "Integrate[(c+d*x)^2*Erfi[a+b*x], x]", //
        "1/2*d*(b*c-a*d)*Erfi[a+b*x]/b^3-1/3*(b*c-a*d)^3*Erfi[a+b*x]/(b^3*d)+1/3*(c+d*x)^3*Erfi[a+b*x]/d+1/3*E^((a+b*x)^2)*d^2/(b^3*Sqrt[Pi])-E^((a+b*x)^2)*(b*c-a*d)^2/(b^3*Sqrt[Pi])-E^((a+b*x)^2)*d*(b*c-a*d)*(a+b*x)/(b^3*Sqrt[Pi])-1/3*E^((a+b*x)^2)*d^2*(a+b*x)^2/(b^3*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:301
  public void test0131() {
    check( //
        "Integrate[Erfi[b*x]^2/x^7, x]", //
        "-1/15*E^(2*b^2*x^2)*b^2/(Pi*x^4)-2/9*E^(2*b^2*x^2)*b^4/(Pi*x^2)+28/45*b^6*ExpIntegralEi[2*b^2*x^2]/Pi+4/45*b^6*Erfi[b*x]^2-1/6*Erfi[b*x]^2/x^6-2/15*E^(b^2*x^2)*b*Erfi[b*x]/(x^5*Sqrt[Pi])-4/45*E^(b^2*x^2)*b^3*Erfi[b*x]/(x^3*Sqrt[Pi])-8/45*E^(b^2*x^2)*b^5*Erfi[b*x]/(x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:318
  public void test0132() {
    check( //
        "Integrate[x*Erfi[d*(a+b*Log[c*x^n])], x]", //
        "1/2*x^2*Erfi[d*(a+b*Log[c*x^n])]-1/2*x^2*Erfi[(a*b*d^2+1/n+b^2*d^2*Log[c*x^n])/(b*d)]/(E^((1+2*a*b*d^2*n)/(b^2*d^2*n^2))*(c*x^n)^(2/n))");
  }

  // 8.1 Error functions.input:348
  public void test0133() {
    check( //
        "Integrate[x^3*Erfi[b*x]/E^(b^2*x^2), x]", //
        "-1/2*Erfi[b*x]/(E^(b^2*x^2)*b^4)-1/2*x^2*Erfi[b*x]/(E^(b^2*x^2)*b^2)+x/(b^3*Sqrt[Pi])+1/3*x^3/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:369
  public void test0134() {
    check( //
        "Integrate[E^(c+b^2*x^2)*Erfi[b*x]/x^2, x]", //
        "-E^(c+b^2*x^2)*Erfi[b*x]/x+E^c*b*ExpIntegralEi[2*b^2*x^2]/Sqrt[Pi]+1/2*E^c*b*Erfi[b*x]^2*Sqrt[Pi]");
  }

  // 8.2 Fresnel integral functions.input:13
  public void test0135() {
    check( //
        "Integrate[x^4*FresnelS[b*x], x]", //
        "-8/5*Cos[1/2*Pi*b^2*x^2]/(Pi^3*b^5)+1/5*x^4*Cos[1/2*Pi*b^2*x^2]/(Pi*b)+1/5*x^5*FresnelS[b*x]-4/5*x^2*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.2 Fresnel integral functions.input:22
  public void test0136() {
    check( //
        "Integrate[FresnelS[b*x]/x^5, x]", //
        "-1/12*Pi*b^3*Cos[1/2*Pi*b^2*x^2]/x-1/12*Pi^2*b^4*FresnelS[b*x]-1/4*FresnelS[b*x]/x^4-1/12*b*Sin[1/2*Pi*b^2*x^2]/x^3");
  }

  // 8.2 Fresnel integral functions.input:32
  public void test0137() {
    check( //
        "Integrate[(c+d*x)*FresnelS[a+b*x], x]", //
        "(b*c-a*d)*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^2)+1/2*d*(a+b*x)*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^2)-1/2*d*FresnelC[a+b*x]/(Pi*b^2)-1/2*(b*c-a*d)^2*FresnelS[a+b*x]/(b^2*d)+1/2*(c+d*x)^2*FresnelS[a+b*x]/d");
  }

  // 8.2 Fresnel integral functions.input:94
  public void test0138() {
    check( //
        "Integrate[Sin[1/2*Pi*b^2*x^2]/FresnelS[b*x], x]", //
        "Log[FresnelS[b*x]]/b");
  }

  // 8.2 Fresnel integral functions.input:157
  public void test0139() {
    check( //
        "Integrate[x^5*FresnelC[b*x], x]", //
        "-5/6*x^3*Cos[1/2*Pi*b^2*x^2]/(Pi^2*b^3)+1/6*x^6*FresnelC[b*x]-5/2*FresnelS[b*x]/(Pi^3*b^6)+5/2*x*Sin[1/2*Pi*b^2*x^2]/(Pi^3*b^5)-1/6*x^5*Sin[1/2*Pi*b^2*x^2]/(Pi*b)");
  }

  // 8.2 Fresnel integral functions.input:166
  public void test0140() {
    check( //
        "Integrate[FresnelC[b*x]/x^4, x]", //
        "-1/6*b*Cos[1/2*Pi*b^2*x^2]/x^2-1/3*FresnelC[b*x]/x^3-1/12*Pi*b^3*SinIntegral[1/2*Pi*b^2*x^2]");
  }

  // 8.2 Fresnel integral functions.input:176
  public void test0141() {
    check( //
        "Integrate[(c+d*x)^2*FresnelC[a+b*x], x]", //
        "-2/3*d^2*Cos[1/2*Pi*(a+b*x)^2]/(Pi^2*b^3)-1/3*(b*c-a*d)^3*FresnelC[a+b*x]/(b^3*d)+1/3*(c+d*x)^3*FresnelC[a+b*x]/d+d*(b*c-a*d)*FresnelS[a+b*x]/(Pi*b^3)-(b*c-a*d)^2*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)-d*(b*c-a*d)*(a+b*x)*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)-1/3*d^2*(a+b*x)^2*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^3)");
  }

  // 8.2 Fresnel integral functions.input:238
  public void test0142() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x], x]", //
        "1/2*FresnelC[b*x]^2/b");
  }

  // 8.2 Fresnel integral functions.input:249
  public void test0143() {
    check( //
        "Integrate[x^4*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x], x]", //
        "-3/4*x^2/(Pi^2*b^3)+1/4*x^2*Cos[Pi*b^2*x^2]/(Pi^2*b^3)+3*x*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/(Pi^2*b^4)-3/2*FresnelC[b*x]^2/(Pi^2*b^5)+x^3*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi*b^2)-Sin[Pi*b^2*x^2]/(Pi^3*b^5)");
  }

  // 8.3 Exponential integral functions.input:179
  public void test0144() {
    check( //
        "Integrate[x*ExpIntegralEi[b*x]^2, x]", //
        "1/2*E^(2*b*x)/b^2+E^(b*x)*ExpIntegralEi[b*x]/b^2-E^(b*x)*x*ExpIntegralEi[b*x]/b+1/2*x^2*ExpIntegralEi[b*x]^2-ExpIntegralEi[2*b*x]/b^2");
  }

  // 8.3 Exponential integral functions.input:204
  public void test0145() {
    check( //
        "Integrate[ExpIntegralEi[a+b*x]/x^3, x]", //
        "-1/2*E^(a+b*x)*b/(a*x)-1/2*E^a*b^2*ExpIntegralEi[b*x]/a^2+1/2*E^a*b^2*ExpIntegralEi[b*x]/a+1/2*b^2*ExpIntegralEi[a+b*x]/a^2-1/2*ExpIntegralEi[a+b*x]/x^2");
  }

  // 8.3 Exponential integral functions.input:232
  public void test0146() {
    check( //
        "Integrate[ExpIntegralEi[d*(a+b*Log[c*x^n])]/x^2, x]", //
        "-ExpIntegralEi[d*(a+b*Log[c*x^n])]/x+E^(a/(b*n))*(c*x^n)^(1/n)*ExpIntegralEi[-(1-b*d*n)*(a+b*Log[c*x^n])/(b*n)]/x");
  }

  // 8.4 Trig integral functions.input:35
  public void test0147() {
    check( //
        "Integrate[SinIntegral[a+b*x]/x^3, x]", //
        "1/2*b^2*CosIntegral[b*x]*Cos[a]/a-1/2*b^2*Cos[a]*SinIntegral[b*x]/a^2+1/2*b^2*SinIntegral[a+b*x]/a^2-1/2*SinIntegral[a+b*x]/x^2-1/2*b^2*CosIntegral[b*x]*Sin[a]/a^2-1/2*b^2*SinIntegral[b*x]*Sin[a]/a-1/2*b*Sin[a+b*x]/(a*x)");
  }

  // 8.5 Hyperbolic integral functions.input:56
  public void test0148() {
    check( //
        "Integrate[SinhIntegral[b*x]*Sinh[b*x]/x, x]", //
        "1/2*SinhIntegral[b*x]^2");
  }

  // 8.5 Hyperbolic integral functions.input:114
  public void test0149() {
    check( //
        "Integrate[CoshIntegral[b*x]^2, x]", //
        "x*CoshIntegral[b*x]^2+SinhIntegral[2*b*x]/b-2*CoshIntegral[b*x]*Sinh[b*x]/b");
  }

  // 8.5 Hyperbolic integral functions.input:170
  public void test0150() {
    check( //
        "Integrate[CoshIntegral[a+b*x]*Sinh[a+b*x], x]", //
        "-1/2*CoshIntegral[2*a+2*b*x]/b+CoshIntegral[a+b*x]*Cosh[a+b*x]/b-1/2*Log[a+b*x]/b");
  }

  // 8.6 Gamma functions.input:21
  public void test0151() {
    check( //
        "Integrate[x^2*Gamma[1,a*x], x]", //
        "(-2)/(E^(a*x)*a^3)-2*x/(E^(a*x)*a^2)-x^2/(E^(a*x)*a)");
  }

  // 8.6 Gamma functions.input:141
  public void test0152() {
    check( //
        "Integrate[Gamma[1,a+b*x], x]", //
        "-E^(-a-b*x)/b");
  }

  // 8.6 Gamma functions.input:269
  public void test0153() {
    check( //
        "Integrate[(c+d*x)^3*PolyGamma[n,a+b*x], x]", //
        "-6*d^3*PolyGamma[-4+n,a+b*x]/b^4+6*d^2*(c+d*x)*PolyGamma[-3+n,a+b*x]/b^3-3*d*(c+d*x)^2*PolyGamma[-2+n,a+b*x]/b^2+(c+d*x)^3*PolyGamma[-1+n,a+b*x]/b");
  }

  // 8.8 Polylogarithm function.input:19
  public void test0154() {
    check( //
        "Integrate[PolyLog[2,a*x]/x^3, x]", //
        "-1/4*a/x+1/4*a^2*Log[x]-1/4*a^2*Log[1-a*x]+1/4*Log[1-a*x]/x^2-1/2*PolyLog[2,a*x]/x^2");
  }

  // 8.8 Polylogarithm function.input:27
  public void test0155() {
    check( //
        "Integrate[PolyLog[3,a*x]/x^2, x]", //
        "a*Log[x]-a*Log[1-a*x]+Log[1-a*x]/x-PolyLog[2,a*x]/x-PolyLog[3,a*x]/x");
  }

  // 8.8 Polylogarithm function.input:37
  public void test0156() {
    check( //
        "Integrate[PolyLog[2,a*x^2]/x^5, x]", //
        "-1/8*a/x^2+1/4*a^2*Log[x]-1/8*a^2*Log[1-a*x^2]+1/8*Log[1-a*x^2]/x^4-1/4*PolyLog[2,a*x^2]/x^4");
  }

  // 8.8 Polylogarithm function.input:53
  public void test0157() {
    check( //
        "Integrate[x^2*PolyLog[3,a*x^2], x]", //
        "8/27*x/a+8/81*x^3-8/27*ArcTanh[x*Sqrt[a]]/a^(3/2)-4/27*x^3*Log[1-a*x^2]-2/9*x^3*PolyLog[2,a*x^2]+1/3*x^3*PolyLog[3,a*x^2]");
  }

  // 8.8 Polylogarithm function.input:160
  public void test0158() {
    check( //
        "Integrate[PolyLog[2,c*(a+b*x)]/x, x]", //
        "Log[x]*Log[1+b*x/a]*Log[1-c*(a+b*x)]+1/2*(Log[1+b*x/a]+Log[(1-a*c)/(1-c*(a+b*x))]-Log[(1-a*c)*(a+b*x)/(a*(1-c*(a+b*x)))])*Log[-a*(1-c*(a+b*x))/(b*x)]^2+1/2*(Log[c*(a+b*x)]-Log[1+b*x/a])*(Log[x]+Log[-a*(1-c*(a+b*x))/(b*x)])^2+(Log[1-c*(a+b*x)]-Log[-a*(1-c*(a+b*x))/(b*x)])*PolyLog[2,-b*x/a]+Log[x]*PolyLog[2,c*(a+b*x)]+Log[-a*(1-c*(a+b*x))/(b*x)]*PolyLog[2,-b*x/(a*(1-c*(a+b*x)))]-Log[-a*(1-c*(a+b*x))/(b*x)]*PolyLog[2,-b*c*x/(1-c*(a+b*x))]+(Log[x]+Log[-a*(1-c*(a+b*x))/(b*x)])*PolyLog[2,1-c*(a+b*x)]-PolyLog[3,-b*x/a]+PolyLog[3,-b*x/(a*(1-c*(a+b*x)))]-PolyLog[3,-b*c*x/(1-c*(a+b*x))]-PolyLog[3,1-c*(a+b*x)]");
  }

  // 8.8 Polylogarithm function.input:213
  public void test0159() {
    check( //
        "Integrate[Log[1-c*x]*PolyLog[2,c*x]/x^4, x]", //
        "7/36*c^2/x-3/4*c^3*Log[x]+3/4*c^3*Log[1-c*x]-7/36*c*Log[1-c*x]/x^2-5/9*c^2*Log[1-c*x]/x-1/9*c^3*Log[1-c*x]^2+1/9*Log[1-c*x]^2/x^3+1/3*c^3*Log[c*x]*Log[1-c*x]^2-2/9*c^3*PolyLog[2,c*x]+1/6*c*PolyLog[2,c*x]/x^2+1/3*c^2*PolyLog[2,c*x]/x+1/3*c^3*Log[1-c*x]*PolyLog[2,c*x]-1/3*Log[1-c*x]*PolyLog[2,c*x]/x^3+2/3*c^3*Log[1-c*x]*PolyLog[2,1-c*x]-1/3*c^3*PolyLog[3,c*x]-2/3*c^3*PolyLog[3,1-c*x]");
  }

  // 8.8 Polylogarithm function.input:223
  public void test0160() {
    check( //
        "Integrate[(g+h*Log[1-c*x])*PolyLog[2,c*x]/x^4, x]", //
        "7/36*c^2*h/x-3/4*c^3*h*Log[x]+19/36*c^3*h*Log[1-c*x]-1/12*c*h*Log[1-c*x]/x^2-1/3*c^2*h*Log[1-c*x]/x+1/3*c^3*h*Log[c*x]*Log[1-c*x]^2+1/9*Log[1-c*x]*(g+h*Log[1-c*x])/x^3-1/18*c*(g+2*h*Log[1-c*x])/x^2-1/9*c^2*(1-c*x)*(g+2*h*Log[1-c*x])/x+1/9*c^3*(g+2*h*Log[1-c*x])*Log[1+(-1)/(1-c*x)]+1/6*c*h*PolyLog[2,c*x]/x^2+1/3*c^2*h*PolyLog[2,c*x]/x+1/3*c^3*h*Log[1-c*x]*PolyLog[2,c*x]-1/3*(g+h*Log[1-c*x])*PolyLog[2,c*x]/x^3-2/9*c^3*h*PolyLog[2,1/(1-c*x)]+2/3*c^3*h*Log[1-c*x]*PolyLog[2,1-c*x]-1/3*c^3*h*PolyLog[3,c*x]-2/3*c^3*h*PolyLog[3,1-c*x]");
  }

  // 8.9 Product logarithm function.input:20
  public void test0161() {
    check( //
        "Integrate[(c*ProductLog[a+b*x])^(3/2), x]", //
        "(a+b*x)*(c*ProductLog[a+b*x])^(3/2)/b-9/8*c^(3/2)*Erfi[Sqrt[c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/b+9/4*c^2*(a+b*x)/(b*Sqrt[c*ProductLog[a+b*x]])-3/2*c*(a+b*x)*Sqrt[c*ProductLog[a+b*x]]/b");
  }

  // 8.9 Product logarithm function.input:28
  public void test0162() {
    check( //
        "Integrate[Sqrt[-c*ProductLog[a+b*x]], x]", //
        "1/4*Erf[Sqrt[-c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/b+1/2*c*(a+b*x)/(b*Sqrt[-c*ProductLog[a+b*x]])+(a+b*x)*Sqrt[-c*ProductLog[a+b*x]]/b");
  }

  // 8.9 Product logarithm function.input:38
  public void test0163() {
    check( //
        "Integrate[x*ProductLog[a+b*x], x]", //
        "a*x/b-1/4*(a+b*x)^2/b^2-1/8*(a+b*x)^2/(b^2*ProductLog[a+b*x]^2)-a*(a+b*x)/(b^2*ProductLog[a+b*x])+1/4*(a+b*x)^2/(b^2*ProductLog[a+b*x])-a*(a+b*x)*ProductLog[a+b*x]/b^2+1/2*(a+b*x)^2*ProductLog[a+b*x]/b^2");
  }

  // 8.9 Product logarithm function.input:144
  public void test0164() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a*x]]/x^4, x]", //
        "2/15*(c*ProductLog[a*x])^(3/2)/(c*x^3)-4/5*(c*ProductLog[a*x])^(5/2)/(c^2*x^3)-4/5*a^3*Erf[Sqrt[3]*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[3*Pi]*Sqrt[c]-2/5*Sqrt[c*ProductLog[a*x]]/x^3");
  }

  // 8.9 Product logarithm function.input:160
  public void test0165() {
    check( //
        "Integrate[(c*ProductLog[a*x])^p/x^2, x]", //
        "-E^(2*ProductLog[a*x])*Gamma[-1+p,ProductLog[a*x]]*ProductLog[a*x]^(2-p)*(c*ProductLog[a*x])^p/(a*x^2)-E^(2*ProductLog[a*x])*Gamma[p,ProductLog[a*x]]*ProductLog[a*x]^(1-p)*(c*ProductLog[a*x])^(1+p)/(a*c*x^2)");
  }

  // 8.9 Product logarithm function.input:185
  public void test0166() {
    check( //
        "Integrate[x*ProductLog[a*x^2]^2, x]", //
        "2*x^2-2*x^2/ProductLog[a*x^2]-x^2*ProductLog[a*x^2]+1/2*x^2*ProductLog[a*x^2]^2");
  }

  // 8.9 Product logarithm function.input:201
  public void test0167() {
    check( //
        "Integrate[ProductLog[a*x^2]^3/x^3, x]", //
        "-3/2*ProductLog[a*x^2]/x^2-3/2*ProductLog[a*x^2]^2/x^2-1/2*ProductLog[a*x^2]^3/x^2");
  }

  // 8.9 Product logarithm function.input:230
  public void test0168() {
    check( //
        "Integrate[x^7*Sqrt[c*ProductLog[a*x^2]], x]", //
        "105/32768*c^4*x^8/(c*ProductLog[a*x^2])^(7/2)-35/4096*c^3*x^8/(c*ProductLog[a*x^2])^(5/2)+7/512*c^2*x^8/(c*ProductLog[a*x^2])^(3/2)-105/131072*Erfi[2*Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/a^4-1/64*c*x^8/Sqrt[c*ProductLog[a*x^2]]+1/8*x^8*Sqrt[c*ProductLog[a*x^2]]");
  }

  // 8.9 Product logarithm function.input:273
  public void test0169() {
    check( //
        "Integrate[ProductLog[a/x]/x^2, x]", //
        "1/x+(-1)/(x*ProductLog[a/x])-ProductLog[a/x]/x");
  }

  // 8.9 Product logarithm function.input:289
  public void test0170() {
    check( //
        "Integrate[x*Sqrt[ProductLog[a/x]], x]", //
        "-2/3*x^2*ProductLog[a/x]^(3/2)-2/3*a^2*Erf[Sqrt[2]*Sqrt[ProductLog[a/x]]]*Sqrt[2*Pi]+2/3*x^2*Sqrt[ProductLog[a/x]]");
  }

  // 8.9 Product logarithm function.input:297
  public void test0171() {
    check( //
        "Integrate[x/Sqrt[ProductLog[a/x]], x]", //
        "-8/15*x^2*ProductLog[a/x]^(3/2)-8/15*a^2*Erf[Sqrt[2]*Sqrt[ProductLog[a/x]]]*Sqrt[2*Pi]+2/5*x^2/Sqrt[ProductLog[a/x]]+2/15*x^2*Sqrt[ProductLog[a/x]]");
  }

  // 8.9 Product logarithm function.input:315
  public void test0172() {
    check( //
        "Integrate[1/ProductLog[a*x^(1/3)]^2, x]", //
        "2/3*x/ProductLog[a*x^(1/3)]^3+x/ProductLog[a*x^(1/3)]^2");
  }

  // 8.9 Product logarithm function.input:323
  public void test0173() {
    check( //
        "Integrate[1/ProductLog[a*x^(1/3)]^4, x]", //
        "12*ExpIntegralEi[3*ProductLog[a*x^(1/3)]]/a^3-3*x/ProductLog[a*x^(1/3)]^4");
  }

  // 8.9 Product logarithm function.input:333
  public void test0174() {
    check( //
        "Integrate[x^(-1-n)*(c*ProductLog[a*x^n])^(1/2), x]", //
        "-a*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/n-2*Sqrt[c*ProductLog[a*x^n]]/(n*x^n)");
  }

  // 8.9 Product logarithm function.input:377
  public void test0175() {
    check( //
        "Integrate[1/(1+ProductLog[a*x]), x]", //
        "x/ProductLog[a*x]");
  }

  // 8.1 Error functions.input:16
  public void test0176() {
    check( //
        "Integrate[Erf[b*x]/x^7, x]", //
        "-4/45*b^6*Erf[b*x]-1/6*Erf[b*x]/x^6-1/15*b/(E^(b^2*x^2)*x^5*Sqrt[Pi])+2/45*b^3/(E^(b^2*x^2)*x^3*Sqrt[Pi])-4/45*b^5/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:26
  public void test0177() {
    check( //
        "Integrate[(c+d*x)^3*Erf[a+b*x], x]", //
        "-3/16*d^3*Erf[a+b*x]/b^4-3/4*d*(b*c-a*d)^2*Erf[a+b*x]/b^4-1/4*(b*c-a*d)^4*Erf[a+b*x]/(b^4*d)+1/4*(c+d*x)^4*Erf[a+b*x]/d+d^2*(b*c-a*d)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])+(b*c-a*d)^3/(E^((a+b*x)^2)*b^4*Sqrt[Pi])+3/8*d^3*(a+b*x)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])+3/2*d*(b*c-a*d)^2*(a+b*x)/(E^((a+b*x)^2)*b^4*Sqrt[Pi])+d^2*(b*c-a*d)*(a+b*x)^2/(E^((a+b*x)^2)*b^4*Sqrt[Pi])+1/4*d^3*(a+b*x)^3/(E^((a+b*x)^2)*b^4*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:42
  public void test0178() {
    check( //
        "Integrate[Erf[b*x]^2/x^5, x]", //
        "-1/3*b^2/(E^(2*b^2*x^2)*Pi*x^2)-4/3*b^4*ExpIntegralEi[-2*b^2*x^2]/Pi+1/3*b^4*Erf[b*x]^2-1/4*Erf[b*x]^2/x^4-1/3*b*Erf[b*x]/(E^(b^2*x^2)*x^3*Sqrt[Pi])+2/3*b^3*Erf[b*x]/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:89
  public void test0179() {
    check( //
        "Integrate[E^(c+b^2*x^2)*x^5*Erf[b*x], x]", //
        "E^(c+b^2*x^2)*Erf[b*x]/b^6-E^(c+b^2*x^2)*x^2*Erf[b*x]/b^4+1/2*E^(c+b^2*x^2)*x^4*Erf[b*x]/b^2-2*E^c*x/(b^5*Sqrt[Pi])+2/3*E^c*x^3/(b^3*Sqrt[Pi])-1/5*E^c*x^5/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:141
  public void test0180() {
    check( //
        "Integrate[x*Erfc[b*x], x]", //
        "1/4*Erf[b*x]/b^2+1/2*x^2*Erfc[b*x]-1/2*x/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:167
  public void test0181() {
    check( //
        "Integrate[x^3*Erfc[b*x]^2, x]", //
        "1/2/(E^(2*b^2*x^2)*Pi*b^4)+1/4*x^2/(E^(2*b^2*x^2)*Pi*b^2)-3/16*Erfc[b*x]^2/b^4+1/4*x^4*Erfc[b*x]^2-3/4*x*Erfc[b*x]/(E^(b^2*x^2)*b^3*Sqrt[Pi])-1/2*x^3*Erfc[b*x]/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:207
  public void test0182() {
    check( //
        "Integrate[E^(c+d*x^2)*x^5*Erfc[b*x], x]", //
        "-1/2*E^c*b*Erf[x*Sqrt[b^2-d]]/((b^2-d)^(3/2)*d^2)+3/8*E^c*b*Erf[x*Sqrt[b^2-d]]/((b^2-d)^(5/2)*d)+E^(c+d*x^2)*Erfc[b*x]/d^3-E^(c+d*x^2)*x^2*Erfc[b*x]/d^2+1/2*E^(c+d*x^2)*x^4*Erfc[b*x]/d+E^(c-(b^2-d)*x^2)*b*x/((b^2-d)*d^2*Sqrt[Pi])-3/4*E^(c-(b^2-d)*x^2)*b*x/((b^2-d)^2*d*Sqrt[Pi])-1/2*E^(c-(b^2-d)*x^2)*b*x^3/((b^2-d)*d*Sqrt[Pi])+E^c*b*Erf[x*Sqrt[b^2-d]]/(d^3*Sqrt[b^2-d])");
  }

  // 8.1 Error functions.input:276
  public void test0183() {
    check( //
        "Integrate[x^4*Erfi[b*x], x]", //
        "1/5*x^5*Erfi[b*x]-2/5*E^(b^2*x^2)/(b^5*Sqrt[Pi])+2/5*E^(b^2*x^2)*x^2/(b^3*Sqrt[Pi])-1/5*E^(b^2*x^2)*x^4/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:286
  public void test0184() {
    check( //
        "Integrate[(c+d*x)*Erfi[a+b*x], x]", //
        "1/4*d*Erfi[a+b*x]/b^2-1/2*(b*c-a*d)^2*Erfi[a+b*x]/(b^2*d)+1/2*(c+d*x)^2*Erfi[a+b*x]/d-E^((a+b*x)^2)*(b*c-a*d)/(b^2*Sqrt[Pi])-1/2*E^((a+b*x)^2)*d*(a+b*x)/(b^2*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:319
  public void test0185() {
    check( //
        "Integrate[Erfi[d*(a+b*Log[c*x^n])], x]", //
        "x*Erfi[d*(a+b*Log[c*x^n])]-x*Erfi[1/2*(2*a*b*d^2+1/n+2*b^2*d^2*Log[c*x^n])/(b*d)]/(E^(1/4*(1+4*a*b*d^2*n)/(b^2*d^2*n^2))*(c*x^n)^(1/n))");
  }

  // 8.1 Error functions.input:349
  public void test0186() {
    check( //
        "Integrate[x*Erfi[b*x]/E^(b^2*x^2), x]", //
        "-1/2*Erfi[b*x]/(E^(b^2*x^2)*b^2)+x/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:370
  public void test0187() {
    check( //
        "Integrate[E^(c+b^2*x^2)*Erfi[b*x]/x^4, x]", //
        "-1/3*E^(c+b^2*x^2)*Erfi[b*x]/x^3-2/3*E^(c+b^2*x^2)*b^2*Erfi[b*x]/x-1/3*E^(c+2*b^2*x^2)*b/(x^2*Sqrt[Pi])+4/3*E^c*b^3*ExpIntegralEi[2*b^2*x^2]/Sqrt[Pi]+1/3*E^c*b^3*Erfi[b*x]^2*Sqrt[Pi]");
  }

  // 8.2 Fresnel integral functions.input:14
  public void test0188() {
    check( //
        "Integrate[x^3*FresnelS[b*x], x]", //
        "1/4*x^3*Cos[1/2*Pi*b^2*x^2]/(Pi*b)+3/4*FresnelS[b*x]/(Pi^2*b^4)+1/4*x^4*FresnelS[b*x]-3/4*x*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.2 Fresnel integral functions.input:23
  public void test0189() {
    check( //
        "Integrate[FresnelS[b*x]/x^6, x]", //
        "-1/40*Pi*b^3*Cos[1/2*Pi*b^2*x^2]/x^2-1/5*FresnelS[b*x]/x^5-1/80*Pi^2*b^5*SinIntegral[1/2*Pi*b^2*x^2]-1/20*b*Sin[1/2*Pi*b^2*x^2]/x^4");
  }

  // 8.2 Fresnel integral functions.input:50
  public void test0190() {
    check( //
        "Integrate[x^3*FresnelS[b*x]^2, x]", //
        "3/8*x^2/(Pi^2*b^2)+1/8*x^2*Cos[Pi*b^2*x^2]/(Pi^2*b^2)+1/2*x^3*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x]/(Pi*b)+3/4*FresnelS[b*x]^2/(Pi^2*b^4)+1/4*x^4*FresnelS[b*x]^2-3/2*x*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^3)-1/2*Sin[Pi*b^2*x^2]/(Pi^3*b^4)");
  }

  // 8.2 Fresnel integral functions.input:95
  public void test0191() {
    check( //
        "Integrate[Sin[1/2*Pi*b^2*x^2]/FresnelS[b*x]^2, x]", //
        "(-1)/(b*FresnelS[b*x])");
  }

  // 8.2 Fresnel integral functions.input:134
  public void test0192() {
    check( //
        "Integrate[x^2*Cos[1/2*Pi*b^2*x^2]*FresnelS[b*x], x]", //
        "-1/4*x^2/(Pi*b)-1/2*FresnelS[b*x]^2/(Pi*b^3)+x*FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi*b^2)+1/4*Sin[Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.2 Fresnel integral functions.input:158
  public void test0193() {
    check( //
        "Integrate[x^4*FresnelC[b*x], x]", //
        "-4/5*x^2*Cos[1/2*Pi*b^2*x^2]/(Pi^2*b^3)+1/5*x^5*FresnelC[b*x]+8/5*Sin[1/2*Pi*b^2*x^2]/(Pi^3*b^5)-1/5*x^4*Sin[1/2*Pi*b^2*x^2]/(Pi*b)");
  }

  // 8.2 Fresnel integral functions.input:167
  public void test0194() {
    check( //
        "Integrate[FresnelC[b*x]/x^5, x]", //
        "-1/12*b*Cos[1/2*Pi*b^2*x^2]/x^3-1/12*Pi^2*b^4*FresnelC[b*x]-1/4*FresnelC[b*x]/x^4+1/12*Pi*b^3*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.2 Fresnel integral functions.input:177
  public void test0195() {
    check( //
        "Integrate[(c+d*x)*FresnelC[a+b*x], x]", //
        "-1/2*(b*c-a*d)^2*FresnelC[a+b*x]/(b^2*d)+1/2*(c+d*x)^2*FresnelC[a+b*x]/d+1/2*d*FresnelS[a+b*x]/(Pi*b^2)-(b*c-a*d)*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^2)-1/2*d*(a+b*x)*Sin[1/2*Pi*(a+b*x)^2]/(Pi*b^2)");
  }

  // 8.2 Fresnel integral functions.input:239
  public void test0196() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]/FresnelC[b*x], x]", //
        "Log[FresnelC[b*x]]/b");
  }

  // 8.3 Exponential integral functions.input:180
  public void test0197() {
    check( //
        "Integrate[ExpIntegralEi[b*x]^2, x]", //
        "-2*E^(b*x)*ExpIntegralEi[b*x]/b+x*ExpIntegralEi[b*x]^2+2*ExpIntegralEi[2*b*x]/b");
  }

  // 8.3 Exponential integral functions.input:233
  public void test0198() {
    check( //
        "Integrate[ExpIntegralEi[d*(a+b*Log[c*x^n])]/x^3, x]", //
        "-1/2*ExpIntegralEi[d*(a+b*Log[c*x^n])]/x^2+1/2*E^(2*a/(b*n))*(c*x^n)^(2/n)*ExpIntegralEi[-(2-b*d*n)*(a+b*Log[c*x^n])/(b*n)]/x^2");
  }

  // 8.4 Trig integral functions.input:126
  public void test0199() {
    check( //
        "Integrate[CosIntegral[a+b*x]/x^2, x]", //
        "-b*CosIntegral[a+b*x]/a-CosIntegral[a+b*x]/x+b*CosIntegral[b*x]*Cos[a]/a-b*SinIntegral[b*x]*Sin[a]/a");
  }

  // 8.4 Trig integral functions.input:140
  public void test0200() {
    check( //
        "Integrate[CosIntegral[d*(a+b*Log[c*x^n])]/x, x]", //
        "CosIntegral[d*(a+b*Log[c*x^n])]*(a+b*Log[c*x^n])/(b*n)-Sin[d*(a+b*Log[c*x^n])]/(b*d*n)");
  }

  // 8.5 Hyperbolic integral functions.input:16
  public void test0201() {
    check( //
        "Integrate[SinhIntegral[b*x]/x^2, x]", //
        "b*CoshIntegral[b*x]-SinhIntegral[b*x]/x-Sinh[b*x]/x");
  }

  // 8.5 Hyperbolic integral functions.input:57
  public void test0202() {
    check( //
        "Integrate[SinhIntegral[b*x]*Sinh[b*x], x]", //
        "Cosh[b*x]*SinhIntegral[b*x]/b-1/2*SinhIntegral[2*b*x]/b");
  }

  // 8.5 Hyperbolic integral functions.input:84
  public void test0203() {
    check( //
        "Integrate[Cosh[a+b*x]*SinhIntegral[a+b*x], x]", //
        "-1/2*CoshIntegral[2*a+2*b*x]/b+1/2*Log[a+b*x]/b+SinhIntegral[a+b*x]*Sinh[a+b*x]/b");
  }

  // 8.5 Hyperbolic integral functions.input:131
  public void test0204() {
    check( //
        "Integrate[CoshIntegral[a+b*x]^2, x]", //
        "(a+b*x)*CoshIntegral[a+b*x]^2/b+SinhIntegral[2*a+2*b*x]/b-2*CoshIntegral[a+b*x]*Sinh[a+b*x]/b");
  }

  // 8.6 Gamma functions.input:22
  public void test0205() {
    check( //
        "Integrate[x*Gamma[1,a*x], x]", //
        "(-1)/(E^(a*x)*a^2)-x/(E^(a*x)*a)");
  }

  // 8.6 Gamma functions.input:142
  public void test0206() {
    check( //
        "Integrate[Gamma[1,a+b*x]/(c+d*x), x]", //
        "E^(-a+b*c/d)*ExpIntegralEi[-b*(c+d*x)/d]/d");
  }

  // 8.6 Gamma functions.input:252
  public void test0207() {
    check( //
        "Integrate[(c+d*x)^3*LogGamma[a+b*x], x]", //
        "-6*d^3*PolyGamma[-5,a+b*x]/b^4+6*d^2*(c+d*x)*PolyGamma[-4,a+b*x]/b^3-3*d*(c+d*x)^2*PolyGamma[-3,a+b*x]/b^2+(c+d*x)^3*PolyGamma[-2,a+b*x]/b");
  }

  // 8.6 Gamma functions.input:270
  public void test0208() {
    check( //
        "Integrate[(c+d*x)^2*PolyGamma[n,a+b*x], x]", //
        "2*d^2*PolyGamma[-3+n,a+b*x]/b^3-2*d*(c+d*x)*PolyGamma[-2+n,a+b*x]/b^2+(c+d*x)^2*PolyGamma[-1+n,a+b*x]/b");
  }

  // 8.7 Zeta function.input:8
  public void test0209() {
    check( //
        "Integrate[x^2*Zeta[2,a+b*x], x]", //
        "-2*x*LogGamma[a+b*x]/b^2+2*PolyGamma[-2,a+b*x]/b^3+x^2*PolyGamma[0,a+b*x]/b");
  }

  // 8.8 Polylogarithm function.input:20
  public void test0210() {
    check( //
        "Integrate[PolyLog[2,a*x]/x^4, x]", //
        "-1/18*a/x^2-1/9*a^2/x+1/9*a^3*Log[x]-1/9*a^3*Log[1-a*x]+1/9*Log[1-a*x]/x^3-1/3*PolyLog[2,a*x]/x^3");
  }

  // 8.8 Polylogarithm function.input:28
  public void test0211() {
    check( //
        "Integrate[PolyLog[3,a*x]/x^3, x]", //
        "-1/8*a/x+1/8*a^2*Log[x]-1/8*a^2*Log[1-a*x]+1/8*Log[1-a*x]/x^2-1/4*PolyLog[2,a*x]/x^2-1/2*PolyLog[3,a*x]/x^2");
  }

  // 8.8 Polylogarithm function.input:38
  public void test0212() {
    check( //
        "Integrate[PolyLog[2,a*x^2]/x^7, x]", //
        "-1/36*a/x^4-1/18*a^2/x^2+1/9*a^3*Log[x]-1/18*a^3*Log[1-a*x^2]+1/18*Log[1-a*x^2]/x^6-1/6*PolyLog[2,a*x^2]/x^6");
  }

  // 8.8 Polylogarithm function.input:54
  public void test0213() {
    check( //
        "Integrate[PolyLog[3,a*x^2], x]", //
        "8*x-4*x*Log[1-a*x^2]-2*x*PolyLog[2,a*x^2]+x*PolyLog[3,a*x^2]-8*ArcTanh[x*Sqrt[a]]/Sqrt[a]");
  }

  // 8.8 Polylogarithm function.input:88
  public void test0214() {
    check( //
        "Integrate[PolyLog[3,a*x]/(d*x)^(3/2), x]", //
        "16*ArcTanh[Sqrt[a]*Sqrt[d*x]/Sqrt[d]]*Sqrt[a]/d^(3/2)+8*Log[1-a*x]/(d*Sqrt[d*x])-4*PolyLog[2,a*x]/(d*Sqrt[d*x])-2*PolyLog[3,a*x]/(d*Sqrt[d*x])");
  }

  // 8.8 Polylogarithm function.input:161
  public void test0215() {
    check( //
        "Integrate[PolyLog[2,c*(a+b*x)]/x^2, x]", //
        "-b*Log[b*c*x/(1-a*c)]*Log[1-a*c-b*c*x]/a-b*PolyLog[2,c*(a+b*x)]/a-PolyLog[2,c*(a+b*x)]/x-b*PolyLog[2,1-b*c*x/(1-a*c)]/a");
  }

  // 8.8 Polylogarithm function.input:191
  public void test0216() {
    check( //
        "Integrate[PolyLog[1,e*((a+b*x)/(c+d*x))^n]/((a+b*x)*(c+d*x)), x]", //
        "PolyLog[2,e*((a+b*x)/(c+d*x))^n]/((b*c-a*d)*n)");
  }

  // 8.8 Polylogarithm function.input:214
  public void test0217() {
    check( //
        "Integrate[Log[1-c*x]*PolyLog[2,c*x]/x^5, x]", //
        "5/144*c^2/x^2+7/36*c^3/x-41/72*c^4*Log[x]+41/72*c^4*Log[1-c*x]-5/72*c*Log[1-c*x]/x^3-1/8*c^2*Log[1-c*x]/x^2-3/8*c^3*Log[1-c*x]/x-1/16*c^4*Log[1-c*x]^2+1/16*Log[1-c*x]^2/x^4+1/4*c^4*Log[c*x]*Log[1-c*x]^2-1/8*c^4*PolyLog[2,c*x]+1/12*c*PolyLog[2,c*x]/x^3+1/8*c^2*PolyLog[2,c*x]/x^2+1/4*c^3*PolyLog[2,c*x]/x+1/4*c^4*Log[1-c*x]*PolyLog[2,c*x]-1/4*Log[1-c*x]*PolyLog[2,c*x]/x^4+1/2*c^4*Log[1-c*x]*PolyLog[2,1-c*x]-1/4*c^4*PolyLog[3,c*x]-1/2*c^4*PolyLog[3,1-c*x]");
  }

  // 8.9 Product logarithm function.input:21
  public void test0218() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a+b*x]], x]", //
        "1/4*Erfi[Sqrt[c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/b-1/2*c*(a+b*x)/(b*Sqrt[c*ProductLog[a+b*x]])+(a+b*x)*Sqrt[c*ProductLog[a+b*x]]/b");
  }

  // 8.9 Product logarithm function.input:39
  public void test0219() {
    check( //
        "Integrate[ProductLog[a+b*x], x]", //
        "-x+(a+b*x)/(b*ProductLog[a+b*x])+(a+b*x)*ProductLog[a+b*x]/b");
  }

  // 8.9 Product logarithm function.input:81
  public void test0220() {
    check( //
        "Integrate[ProductLog[a*x], x]", //
        "-x+x/ProductLog[a*x]+x*ProductLog[a*x]");
  }

  // 8.9 Product logarithm function.input:113
  public void test0221() {
    check( //
        "Integrate[1/ProductLog[a*x], x]", //
        "ExpIntegralEi[ProductLog[a*x]]/a+x/ProductLog[a*x]");
  }

  // 8.9 Product logarithm function.input:137
  public void test0222() {
    check( //
        "Integrate[x^3*Sqrt[c*ProductLog[a*x]], x]", //
        "105/16384*c^4*x^4/(c*ProductLog[a*x])^(7/2)-35/2048*c^3*x^4/(c*ProductLog[a*x])^(5/2)+7/256*c^2*x^4/(c*ProductLog[a*x])^(3/2)-105/65536*Erfi[2*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/a^4-1/32*c*x^4/Sqrt[c*ProductLog[a*x]]+1/4*x^4*Sqrt[c*ProductLog[a*x]]");
  }

  // 8.9 Product logarithm function.input:145
  public void test0223() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a*x]]/x^5, x]", //
        "2/35*(c*ProductLog[a*x])^(3/2)/(c*x^4)-16/105*(c*ProductLog[a*x])^(5/2)/(c^2*x^4)+128/105*(c*ProductLog[a*x])^(7/2)/(c^3*x^4)+256/105*a^4*Erf[2*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]-2/7*Sqrt[c*ProductLog[a*x]]/x^4");
  }

  // 8.9 Product logarithm function.input:153
  public void test0224() {
    check( //
        "Integrate[1/(x^2*Sqrt[c*ProductLog[a*x]]), x]", //
        "-2/3*a*Erf[Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[Pi]/Sqrt[c]+(-2/3)/(x*Sqrt[c*ProductLog[a*x]])-2/3*Sqrt[c*ProductLog[a*x]]/(c*x)");
  }

  // 8.9 Product logarithm function.input:161
  public void test0225() {
    check( //
        "Integrate[(c*ProductLog[a*x])^p/x^3, x]", //
        "-2^(2-p)*E^(3*ProductLog[a*x])*Gamma[-2+p,2*ProductLog[a*x]]*ProductLog[a*x]^(3-p)*(c*ProductLog[a*x])^p/(a*x^3)-2^(1-p)*E^(3*ProductLog[a*x])*Gamma[-1+p,2*ProductLog[a*x]]*ProductLog[a*x]^(2-p)*(c*ProductLog[a*x])^(1+p)/(a*c*x^3)");
  }

  // 8.9 Product logarithm function.input:231
  public void test0226() {
    check( //
        "Integrate[x^6*Sqrt[c*ProductLog[a*x^2]], x]", //
        "48/16807*c^4*x^7/(c*ProductLog[a*x^2])^(7/2)-24/2401*c^3*x^7/(c*ProductLog[a*x^2])^(5/2)+6/343*c^2*x^7/(c*ProductLog[a*x^2])^(3/2)-1/49*c*x^7/Sqrt[c*ProductLog[a*x^2]]+1/7*x^7*Sqrt[c*ProductLog[a*x^2]]");
  }

  // 8.9 Product logarithm function.input:264
  public void test0227() {
    check( //
        "Integrate[(c*ProductLog[a*x^2])^p/x^3, x]", //
        "-1/2*E^(2*ProductLog[a*x^2])*Gamma[-1+p,ProductLog[a*x^2]]*ProductLog[a*x^2]^(2-p)*(c*ProductLog[a*x^2])^p/(a*x^4)-1/2*E^(2*ProductLog[a*x^2])*Gamma[p,ProductLog[a*x^2]]*ProductLog[a*x^2]^(2-p)*(c*ProductLog[a*x^2])^p/(a*x^4)");
  }

  // 8.9 Product logarithm function.input:306
  public void test0228() {
    check( //
        "Integrate[(c*ProductLog[a/x])^p/x^2, x]", //
        "-(c*ProductLog[a/x])^p/x+p*Gamma[1+p,-ProductLog[a/x]]*(c*ProductLog[a/x])^p/(a*(-ProductLog[a/x])^p)");
  }

  // 8.9 Product logarithm function.input:316
  public void test0229() {
    check( //
        "Integrate[1/ProductLog[a*x^(1/4)]^3, x]", //
        "3/4*x/ProductLog[a*x^(1/4)]^4+x/ProductLog[a*x^(1/4)]^3");
  }

  // 8.9 Product logarithm function.input:324
  public void test0230() {
    check( //
        "Integrate[1/ProductLog[a*x^(1/4)]^5, x]", //
        "20*ExpIntegralEi[4*ProductLog[a*x^(1/4)]]/a^4-4*x/ProductLog[a*x^(1/4)]^5");
  }

  // 8.9 Product logarithm function.input:334
  public void test0231() {
    check( //
        "Integrate[x^(-1-n)/(c*ProductLog[a*x^n])^(1/2), x]", //
        "-2/3*a*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(n*Sqrt[c])+(-2/3)/(n*x^n*Sqrt[c*ProductLog[a*x^n]])-2/3*Sqrt[c*ProductLog[a*x^n]]/(c*n*x^n)");
  }

  // 8.9 Product logarithm function.input:342
  public void test0232() {
    check( //
        "Integrate[x^(-1-2*n)*(c*ProductLog[a*x^n])^(1/2), x]", //
        "2/3*(c*ProductLog[a*x^n])^(3/2)/(c*n*x^(2*n))+2/3*a^2*Erf[Sqrt[2]*Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[2*Pi]*Sqrt[c]/n-2/3*Sqrt[c*ProductLog[a*x^n]]/(n*x^(2*n))");
  }

  // 8.9 Product logarithm function.input:350
  public void test0233() {
    check( //
        "Integrate[x^(-1+n)/(c*ProductLog[a*x^n])^(5/2), x]", //
        "-2/3*x^n/(n*(c*ProductLog[a*x^n])^(5/2))-10/3*x^n/(c*n*(c*ProductLog[a*x^n])^(3/2))+10/3*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(a*c^(5/2)*n)");
  }

  // 8.9 Product logarithm function.input:358
  public void test0234() {
    check( //
        "Integrate[x^(-1+2*n)/(c*ProductLog[a*x^n])^(7/2), x]", //
        "-2/3*x^(2*n)/(n*(c*ProductLog[a*x^n])^(7/2))-14/3*x^(2*n)/(c*n*(c*ProductLog[a*x^n])^(5/2))+14/3*Erfi[Sqrt[2]*Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[2*Pi]/(a^2*c^(7/2)*n)");
  }

  // 8.1 Error functions.input:17
  public void test0235() {
    check( //
        "Integrate[x^6*Erf[b*x], x]", //
        "1/7*x^7*Erf[b*x]+6/7/(E^(b^2*x^2)*b^7*Sqrt[Pi])+6/7*x^2/(E^(b^2*x^2)*b^5*Sqrt[Pi])+3/7*x^4/(E^(b^2*x^2)*b^3*Sqrt[Pi])+1/7*x^6/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:27
  public void test0236() {
    check( //
        "Integrate[(c+d*x)^2*Erf[a+b*x], x]", //
        "-1/2*d*(b*c-a*d)*Erf[a+b*x]/b^3-1/3*(b*c-a*d)^3*Erf[a+b*x]/(b^3*d)+1/3*(c+d*x)^3*Erf[a+b*x]/d+1/3*d^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])+(b*c-a*d)^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])+d*(b*c-a*d)*(a+b*x)/(E^((a+b*x)^2)*b^3*Sqrt[Pi])+1/3*d^2*(a+b*x)^2/(E^((a+b*x)^2)*b^3*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:43
  public void test0237() {
    check( //
        "Integrate[Erf[b*x]^2/x^7, x]", //
        "-1/15*b^2/(E^(2*b^2*x^2)*Pi*x^4)+2/9*b^4/(E^(2*b^2*x^2)*Pi*x^2)+28/45*b^6*ExpIntegralEi[-2*b^2*x^2]/Pi-4/45*b^6*Erf[b*x]^2-1/6*Erf[b*x]^2/x^6-2/15*b*Erf[b*x]/(E^(b^2*x^2)*x^5*Sqrt[Pi])+4/45*b^3*Erf[b*x]/(E^(b^2*x^2)*x^3*Sqrt[Pi])-8/45*b^5*Erf[b*x]/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:60
  public void test0238() {
    check( //
        "Integrate[x*Erf[d*(a+b*Log[c*x^n])], x]", //
        "1/2*x^2*Erf[d*(a+b*Log[c*x^n])]-1/2*E^((1-2*a*b*d^2*n)/(b^2*d^2*n^2))*x^2*Erf[(a*b*d^2+(-1)/n+b^2*d^2*Log[c*x^n])/(b*d)]/(c*x^n)^(2/n)");
  }

  // 8.1 Error functions.input:90
  public void test0239() {
    check( //
        "Integrate[E^(c+b^2*x^2)*x^3*Erf[b*x], x]", //
        "-1/2*E^(c+b^2*x^2)*Erf[b*x]/b^4+1/2*E^(c+b^2*x^2)*x^2*Erf[b*x]/b^2+E^c*x/(b^3*Sqrt[Pi])-1/3*E^c*x^3/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:109
  public void test0240() {
    check( //
        "Integrate[Erf[b*x]/(E^(b^2*x^2)*x^2), x]", //
        "-Erf[b*x]/(E^(b^2*x^2)*x)+b*ExpIntegralEi[-2*b^2*x^2]/Sqrt[Pi]-1/2*b*Erf[b*x]^2*Sqrt[Pi]");
  }

  // 8.1 Error functions.input:143
  public void test0241() {
    check( //
        "Integrate[Erfc[b*x]/x^3, x]", //
        "b^2*Erf[b*x]-1/2*Erfc[b*x]/x^2+b/(E^(b^2*x^2)*x*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:151
  public void test0242() {
    check( //
        "Integrate[Erfc[b*x]/x^4, x]", //
        "-1/3*Erfc[b*x]/x^3+1/3*b/(E^(b^2*x^2)*x^2*Sqrt[Pi])+1/3*b^3*ExpIntegralEi[-b^2*x^2]/Sqrt[Pi]");
  }

  // 8.1 Error functions.input:168
  public void test0243() {
    check( //
        "Integrate[x*Erfc[b*x]^2, x]", //
        "1/2/(E^(2*b^2*x^2)*Pi*b^2)-1/4*Erfc[b*x]^2/b^2+1/2*x^2*Erfc[b*x]^2-x*Erfc[b*x]/(E^(b^2*x^2)*b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:208
  public void test0244() {
    check( //
        "Integrate[E^(c+d*x^2)*x^3*Erfc[b*x], x]", //
        "1/4*E^c*b*Erf[x*Sqrt[b^2-d]]/((b^2-d)^(3/2)*d)-1/2*E^(c+d*x^2)*Erfc[b*x]/d^2+1/2*E^(c+d*x^2)*x^2*Erfc[b*x]/d-1/2*E^(c-(b^2-d)*x^2)*b*x/((b^2-d)*d*Sqrt[Pi])-1/2*E^c*b*Erf[x*Sqrt[b^2-d]]/(d^2*Sqrt[b^2-d])");
  }

  // 8.1 Error functions.input:235
  public void test0245() {
    check( //
        "Integrate[x^4*Erfc[b*x]/E^(b^2*x^2), x]", //
        "-3/4*x*Erfc[b*x]/(E^(b^2*x^2)*b^4)-1/2*x^3*Erfc[b*x]/(E^(b^2*x^2)*b^2)+1/2/(E^(2*b^2*x^2)*b^5*Sqrt[Pi])+1/4*x^2/(E^(2*b^2*x^2)*b^3*Sqrt[Pi])-3/16*Erfc[b*x]^2*Sqrt[Pi]/b^5");
  }

  // 8.1 Error functions.input:268
  public void test0246() {
    check( //
        "Integrate[x^5*Erfi[b*x], x]", //
        "5/16*Erfi[b*x]/b^6+1/6*x^6*Erfi[b*x]-5/8*E^(b^2*x^2)*x/(b^5*Sqrt[Pi])+5/12*E^(b^2*x^2)*x^3/(b^3*Sqrt[Pi])-1/6*E^(b^2*x^2)*x^5/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:277
  public void test0247() {
    check( //
        "Integrate[x^2*Erfi[b*x], x]", //
        "1/3*x^3*Erfi[b*x]+1/3*E^(b^2*x^2)/(b^3*Sqrt[Pi])-1/3*E^(b^2*x^2)*x^2/(b*Sqrt[Pi])");
  }

  // 8.1 Error functions.input:320
  public void test0248() {
    check( //
        "Integrate[Erfi[d*(a+b*Log[c*x^n])]/x, x]", //
        "Erfi[d*(a+b*Log[c*x^n])]*(a+b*Log[c*x^n])/(b*n)-E^((a*d+b*d*Log[c*x^n])^2)/(b*d*n*Sqrt[Pi])");
  }

  // 8.2 Fresnel integral functions.input:15
  public void test0249() {
    check( //
        "Integrate[x^2*FresnelS[b*x], x]", //
        "1/3*x^2*Cos[1/2*Pi*b^2*x^2]/(Pi*b)+1/3*x^3*FresnelS[b*x]-2/3*Sin[1/2*Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.2 Fresnel integral functions.input:24
  public void test0250() {
    check( //
        "Integrate[FresnelS[b*x]/x^7, x]", //
        "-1/90*Pi*b^3*Cos[1/2*Pi*b^2*x^2]/x^3-1/90*Pi^3*b^6*FresnelC[b*x]-1/6*FresnelS[b*x]/x^6-1/30*b*Sin[1/2*Pi*b^2*x^2]/x^5+1/90*Pi^2*b^5*Sin[1/2*Pi*b^2*x^2]/x");
  }

  // 8.2 Fresnel integral functions.input:36
  public void test0251() {
    check( //
        "Integrate[x^3*FresnelS[a+b*x], x]", //
        "-a^3*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^4)+3/2*a^2*(a+b*x)*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^4)-a*(a+b*x)^2*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^4)+1/4*(a+b*x)^3*Cos[1/2*Pi*(a+b*x)^2]/(Pi*b^4)-3/2*a^2*FresnelC[a+b*x]/(Pi*b^4)+3/4*FresnelS[a+b*x]/(Pi^2*b^4)-1/4*a^4*FresnelS[a+b*x]/b^4+1/4*x^4*FresnelS[a+b*x]+2*a*Sin[1/2*Pi*(a+b*x)^2]/(Pi^2*b^4)-3/4*(a+b*x)*Sin[1/2*Pi*(a+b*x)^2]/(Pi^2*b^4)");
  }

  // 8.2 Fresnel integral functions.input:76
  public void test0252() {
    check( //
        "Integrate[FresnelS[d*(a+b*Log[c*x^n])]/x, x]", //
        "Cos[1/2*Pi*d^2*(a+b*Log[c*x^n])^2]/(Pi*b*d*n)+FresnelS[d*(a+b*Log[c*x^n])]*(a+b*Log[c*x^n])/(b*n)");
  }

  // 8.2 Fresnel integral functions.input:96
  public void test0253() {
    check( //
        "Integrate[Sin[1/2*Pi*b^2*x^2]/FresnelS[b*x]^3, x]", //
        "(-1/2)/(b*FresnelS[b*x]^2)");
  }

  // 8.2 Fresnel integral functions.input:108
  public void test0254() {
    check( //
        "Integrate[FresnelS[b*x]*Sin[1/2*Pi*b^2*x^2], x]", //
        "1/2*FresnelS[b*x]^2/b");
  }

  // 8.2 Fresnel integral functions.input:159
  public void test0255() {
    check( //
        "Integrate[x^3*FresnelC[b*x], x]", //
        "-3/4*x*Cos[1/2*Pi*b^2*x^2]/(Pi^2*b^3)+3/4*FresnelC[b*x]/(Pi^2*b^4)+1/4*x^4*FresnelC[b*x]-1/4*x^3*Sin[1/2*Pi*b^2*x^2]/(Pi*b)");
  }

  // 8.2 Fresnel integral functions.input:168
  public void test0256() {
    check( //
        "Integrate[FresnelC[b*x]/x^6, x]", //
        "-1/80*Pi^2*b^5*CosIntegral[1/2*Pi*b^2*x^2]-1/20*b*Cos[1/2*Pi*b^2*x^2]/x^4-1/5*FresnelC[b*x]/x^5+1/40*Pi*b^3*Sin[1/2*Pi*b^2*x^2]/x^2");
  }

  // 8.2 Fresnel integral functions.input:195
  public void test0257() {
    check( //
        "Integrate[x^3*FresnelC[b*x]^2, x]", //
        "3/8*x^2/(Pi^2*b^2)-1/8*x^2*Cos[Pi*b^2*x^2]/(Pi^2*b^2)-3/2*x*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/(Pi^2*b^3)+3/4*FresnelC[b*x]^2/(Pi^2*b^4)+1/4*x^4*FresnelC[b*x]^2-1/2*x^3*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2]/(Pi*b)+1/2*Sin[Pi*b^2*x^2]/(Pi^3*b^4)");
  }

  // 8.2 Fresnel integral functions.input:240
  public void test0258() {
    check( //
        "Integrate[Cos[1/2*Pi*b^2*x^2]/FresnelC[b*x]^2, x]", //
        "(-1)/(b*FresnelC[b*x])");
  }

  // 8.2 Fresnel integral functions.input:279
  public void test0259() {
    check( //
        "Integrate[x^2*FresnelC[b*x]*Sin[1/2*Pi*b^2*x^2], x]", //
        "1/4*x^2/(Pi*b)-x*Cos[1/2*Pi*b^2*x^2]*FresnelC[b*x]/(Pi*b^2)+1/2*FresnelC[b*x]^2/(Pi*b^3)+1/4*Sin[Pi*b^2*x^2]/(Pi^2*b^3)");
  }

  // 8.3 Exponential integral functions.input:189
  public void test0260() {
    check( //
        "Integrate[(d*x)^m*ExpIntegralEi[b*x], x]", //
        "(d*x)^(1+m)*ExpIntegralEi[b*x]/(d*(1+m))-(d*x)^m*Gamma[1+m,-b*x]/(b*(1+m)*(-b*x)^m)");
  }

  // 8.3 Exponential integral functions.input:208
  public void test0261() {
    check( //
        "Integrate[x*ExpIntegralEi[a+b*x]^2, x]", //
        "1/2*E^(2*a+2*b*x)/b^2+E^(a+b*x)*ExpIntegralEi[a+b*x]/b^2+E^(a+b*x)*a*ExpIntegralEi[a+b*x]/b^2-E^(a+b*x)*x*ExpIntegralEi[a+b*x]/b+1/2*a*x*ExpIntegralEi[a+b*x]^2/b+1/2*x^2*ExpIntegralEi[a+b*x]^2-1/2*a*(a+b*x)*ExpIntegralEi[a+b*x]^2/b^2-ExpIntegralEi[2*(a+b*x)]/b^2-2*a*ExpIntegralEi[2*(a+b*x)]/b^2");
  }

  // 8.3 Exponential integral functions.input:234
  public void test0262() {
    check( //
        "Integrate[(e*x)^m*ExpIntegralEi[d*(a+b*Log[c*x^n])], x]", //
        "(e*x)^(1+m)*ExpIntegralEi[d*(a+b*Log[c*x^n])]/(e*(1+m))-(e*x)^(1+m)*ExpIntegralEi[(1+m+b*d*n)*(a+b*Log[c*x^n])/(b*n)]/(E^(a*(1+m)/(b*n))*e*(1+m)*(c*x^n)^((1+m)/n))");
  }

  // 8.3 Exponential integral functions.input:265
  public void test0263() {
    check( //
        "Integrate[LogIntegral[b*x]/x^2, x]", //
        "-LogIntegral[b*x]/x+b*Log[Log[b*x]]");
  }

  // 8.4 Trig integral functions.input:22
  public void test0264() {
    check( //
        "Integrate[SinIntegral[b*x]^2, x]", //
        "2*Cos[b*x]*SinIntegral[b*x]/b+x*SinIntegral[b*x]^2-SinIntegral[2*b*x]/b");
  }

  // 8.4 Trig integral functions.input:78
  public void test0265() {
    check( //
        "Integrate[SinIntegral[a+b*x]*Sin[a+b*x], x]", //
        "-Cos[a+b*x]*SinIntegral[a+b*x]/b+1/2*SinIntegral[2*a+2*b*x]/b");
  }

  // 8.4 Trig integral functions.input:127
  public void test0266() {
    check( //
        "Integrate[CosIntegral[a+b*x]/x^3, x]", //
        "1/2*b^2*CosIntegral[a+b*x]/a^2-1/2*CosIntegral[a+b*x]/x^2-1/2*b^2*CosIntegral[b*x]*Cos[a]/a^2-1/2*b*Cos[a+b*x]/(a*x)-1/2*b^2*Cos[a]*SinIntegral[b*x]/a-1/2*b^2*CosIntegral[b*x]*Sin[a]/a+1/2*b^2*SinIntegral[b*x]*Sin[a]/a^2");
  }

  // 8.5 Hyperbolic integral functions.input:148
  public void test0267() {
    check( //
        "Integrate[CoshIntegral[b*x]*Cosh[b*x]/x, x]", //
        "1/2*CoshIntegral[b*x]^2");
  }

  // 8.6 Gamma functions.input:23
  public void test0268() {
    check( //
        "Integrate[Gamma[1,a*x], x]", //
        "(-1)/(E^(a*x)*a)");
  }

  // 8.6 Gamma functions.input:143
  public void test0269() {
    check( //
        "Integrate[Gamma[1,a+b*x]/(c+d*x)^2, x]", //
        "-E^(-a-b*x)/(d*(c+d*x))-E^(-a+b*c/d)*b*ExpIntegralEi[-b*(c+d*x)/d]/d^2");
  }

  // 8.6 Gamma functions.input:243
  public void test0270() {
    check( //
        "Integrate[x^2*Gamma[p,d*(a+b*Log[c*x^n])], x]", //
        "1/3*x^3*Gamma[p,d*(a+b*Log[c*x^n])]-1/3*x^3*Gamma[p,-(3-b*d*n)*(a+b*Log[c*x^n])/(b*n)]*(d*(a+b*Log[c*x^n]))^p/(E^(3*a/(b*n))*(c*x^n)^(3/n)*(-(3-b*d*n)*(a+b*Log[c*x^n])/(b*n))^p)");
  }

  // 8.6 Gamma functions.input:253
  public void test0271() {
    check( //
        "Integrate[(c+d*x)^2*LogGamma[a+b*x], x]", //
        "2*d^2*PolyGamma[-4,a+b*x]/b^3-2*d*(c+d*x)*PolyGamma[-3,a+b*x]/b^2+(c+d*x)^2*PolyGamma[-2,a+b*x]/b");
  }

  // 8.7 Zeta function.input:9
  public void test0272() {
    check( //
        "Integrate[x*Zeta[2,a+b*x], x]", //
        "-LogGamma[a+b*x]/b^2+x*PolyGamma[0,a+b*x]/b");
  }

  // 8.8 Polylogarithm function.input:21
  public void test0273() {
    check( //
        "Integrate[PolyLog[2,a*x]/x^5, x]", //
        "-1/48*a/x^3-1/32*a^2/x^2-1/16*a^3/x+1/16*a^4*Log[x]-1/16*a^4*Log[1-a*x]+1/16*Log[1-a*x]/x^4-1/4*PolyLog[2,a*x]/x^4");
  }

  // 8.8 Polylogarithm function.input:29
  public void test0274() {
    check( //
        "Integrate[PolyLog[3,a*x]/x^4, x]", //
        "-1/54*a/x^2-1/27*a^2/x+1/27*a^3*Log[x]-1/27*a^3*Log[1-a*x]+1/27*Log[1-a*x]/x^3-1/9*PolyLog[2,a*x]/x^3-1/3*PolyLog[3,a*x]/x^3");
  }

  // 8.8 Polylogarithm function.input:39
  public void test0275() {
    check( //
        "Integrate[x^4*PolyLog[2,a*x^2], x]", //
        "-4/25*x/a^2-4/75*x^3/a-4/125*x^5+4/25*ArcTanh[x*Sqrt[a]]/a^(5/2)+2/25*x^5*Log[1-a*x^2]+1/5*x^5*PolyLog[2,a*x^2]");
  }

  // 8.8 Polylogarithm function.input:47
  public void test0276() {
    check( //
        "Integrate[x*PolyLog[3,a*x^2], x]", //
        "1/2*x^2+1/2*(1-a*x^2)*Log[1-a*x^2]/a-1/2*x^2*PolyLog[2,a*x^2]+1/2*x^2*PolyLog[3,a*x^2]");
  }

  // 8.8 Polylogarithm function.input:55
  public void test0277() {
    check( //
        "Integrate[PolyLog[3,a*x^2]/x^2, x]", //
        "4*Log[1-a*x^2]/x-2*PolyLog[2,a*x^2]/x-PolyLog[3,a*x^2]/x+8*ArcTanh[x*Sqrt[a]]*Sqrt[a]");
  }

  // 8.8 Polylogarithm function.input:81
  public void test0278() {
    check( //
        "Integrate[PolyLog[2,a*x]/(d*x)^(3/2), x]", //
        "8*ArcTanh[Sqrt[a]*Sqrt[d*x]/Sqrt[d]]*Sqrt[a]/d^(3/2)+4*Log[1-a*x]/(d*Sqrt[d*x])-2*PolyLog[2,a*x]/(d*Sqrt[d*x])");
  }

  // 8.8 Polylogarithm function.input:162
  public void test0279() {
    check( //
        "Integrate[PolyLog[2,c*(a+b*x)]/x^3, x]", //
        "1/2*b^2*c*Log[x]/(a*(1-a*c))-1/2*b^2*c*Log[1-a*c-b*c*x]/(a*(1-a*c))+1/2*b*Log[1-a*c-b*c*x]/(a*x)+1/2*b^2*Log[b*c*x/(1-a*c)]*Log[1-a*c-b*c*x]/a^2+1/2*b^2*PolyLog[2,c*(a+b*x)]/a^2-1/2*PolyLog[2,c*(a+b*x)]/x^2+1/2*b^2*PolyLog[2,1-b*c*x/(1-a*c)]/a^2");
  }

  // 8.8 Polylogarithm function.input:182
  public void test0280() {
    check( //
        "Integrate[PolyLog[2,x]/(-1+x), x]", //
        "Log[1-x]^2*Log[x]+2*Log[1-x]*PolyLog[2,1-x]+Log[1-x]*PolyLog[2,x]-2*PolyLog[3,1-x]");
  }

  // 8.8 Polylogarithm function.input:192
  public void test0281() {
    check( //
        "Integrate[PolyLog[0,e*((a+b*x)/(c+d*x))^n]/((a+b*x)*(c+d*x)), x]", //
        "PolyLog[1,e*((a+b*x)/(c+d*x))^n]/((b*c-a*d)*n)");
  }

  // 8.9 Product logarithm function.input:14
  public void test0282() {
    check( //
        "Integrate[1/ProductLog[a+b*x], x]", //
        "ExpIntegralEi[ProductLog[a+b*x]]/b+(a+b*x)/(b*ProductLog[a+b*x])");
  }

  // 8.9 Product logarithm function.input:30
  public void test0283() {
    check( //
        "Integrate[1/(-c*ProductLog[a+b*x])^(3/2), x]", //
        "-2*(a+b*x)/(b*(-c*ProductLog[a+b*x])^(3/2))+3*Erf[Sqrt[-c*ProductLog[a+b*x]]/Sqrt[c]]*Sqrt[Pi]/(b*c^(3/2))");
  }

  // 8.9 Product logarithm function.input:42
  public void test0284() {
    check( //
        "Integrate[x^3*ProductLog[a+b*x]^2, x]", //
        "-4*a^3*x/b^3+9/4*a^2*(a+b*x)^2/b^4-8/9*a*(a+b*x)^3/b^4+5/32*(a+b*x)^4/b^4+15/1024*(a+b*x)^4/(b^4*ProductLog[a+b*x]^4)+16/81*a*(a+b*x)^3/(b^4*ProductLog[a+b*x]^3)-15/256*(a+b*x)^4/(b^4*ProductLog[a+b*x]^3)+9/8*a^2*(a+b*x)^2/(b^4*ProductLog[a+b*x]^2)-16/27*a*(a+b*x)^3/(b^4*ProductLog[a+b*x]^2)+15/128*(a+b*x)^4/(b^4*ProductLog[a+b*x]^2)+4*a^3*(a+b*x)/(b^4*ProductLog[a+b*x])-9/4*a^2*(a+b*x)^2/(b^4*ProductLog[a+b*x])+8/9*a*(a+b*x)^3/(b^4*ProductLog[a+b*x])-5/32*(a+b*x)^4/(b^4*ProductLog[a+b*x])+2*a^3*(a+b*x)*ProductLog[a+b*x]/b^4-3/2*a^2*(a+b*x)^2*ProductLog[a+b*x]/b^4+2/3*a*(a+b*x)^3*ProductLog[a+b*x]/b^4-1/8*(a+b*x)^4*ProductLog[a+b*x]/b^4-a^3*(a+b*x)*ProductLog[a+b*x]^2/b^4+3/2*a^2*(a+b*x)^2*ProductLog[a+b*x]^2/b^4-a*(a+b*x)^3*ProductLog[a+b*x]^2/b^4+1/4*(a+b*x)^4*ProductLog[a+b*x]^2/b^4");
  }

  // 8.9 Product logarithm function.input:68
  public void test0285() {
    check( //
        "Integrate[x^3/(d+d*ProductLog[a+b*x]), x]", //
        "-3/128*(a+b*x)^4/(b^4*d*ProductLog[a+b*x]^4)-2/9*a*(a+b*x)^3/(b^4*d*ProductLog[a+b*x]^3)+3/32*(a+b*x)^4/(b^4*d*ProductLog[a+b*x]^3)-3/4*a^2*(a+b*x)^2/(b^4*d*ProductLog[a+b*x]^2)+2/3*a*(a+b*x)^3/(b^4*d*ProductLog[a+b*x]^2)-3/16*(a+b*x)^4/(b^4*d*ProductLog[a+b*x]^2)-a^3*(a+b*x)/(b^4*d*ProductLog[a+b*x])+3/2*a^2*(a+b*x)^2/(b^4*d*ProductLog[a+b*x])-a*(a+b*x)^3/(b^4*d*ProductLog[a+b*x])+1/4*(a+b*x)^4/(b^4*d*ProductLog[a+b*x])");
  }

  // 8.9 Product logarithm function.input:90
  public void test0286() {
    check( //
        "Integrate[ProductLog[a*x]^2, x]", //
        "4*x-4*x/ProductLog[a*x]-2*x*ProductLog[a*x]+x*ProductLog[a*x]^2");
  }

  // 8.9 Product logarithm function.input:146
  public void test0287() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a*x]]/x^6, x]", //
        "2/63*(c*ProductLog[a*x])^(3/2)/(c*x^5)-4/63*(c*ProductLog[a*x])^(5/2)/(c^2*x^5)+40/189*(c*ProductLog[a*x])^(7/2)/(c^3*x^5)-400/189*(c*ProductLog[a*x])^(9/2)/(c^4*x^5)-400/189*a^5*Erf[Sqrt[5]*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[5*Pi]*Sqrt[c]-2/9*Sqrt[c*ProductLog[a*x]]/x^5");
  }

  // 8.9 Product logarithm function.input:154
  public void test0288() {
    check( //
        "Integrate[1/(x^3*Sqrt[c*ProductLog[a*x]]), x]", //
        "8/15*(c*ProductLog[a*x])^(3/2)/(c^2*x^2)+8/15*a^2*Erf[Sqrt[2]*Sqrt[c*ProductLog[a*x]]/Sqrt[c]]*Sqrt[2*Pi]/Sqrt[c]+(-2/5)/(x^2*Sqrt[c*ProductLog[a*x]])-2/15*Sqrt[c*ProductLog[a*x]]/(c*x^2)");
  }

  // 8.9 Product logarithm function.input:174
  public void test0289() {
    check( //
        "Integrate[x*ProductLog[a*x^2], x]", //
        "-1/2*x^2+1/2*x^2/ProductLog[a*x^2]+1/2*x^2*ProductLog[a*x^2]");
  }

  // 8.9 Product logarithm function.input:219
  public void test0290() {
    check( //
        "Integrate[x^7/ProductLog[a*x^2]^2, x]", //
        "-1/64*x^8/ProductLog[a*x^2]^4+1/16*x^8/ProductLog[a*x^2]^3+1/8*x^8/ProductLog[a*x^2]^2");
  }

  // 8.9 Product logarithm function.input:242
  public void test0291() {
    check( //
        "Integrate[Sqrt[c*ProductLog[a*x^2]]/x^5, x]", //
        "1/3*(c*ProductLog[a*x^2])^(3/2)/(c*x^4)+1/3*a^2*Erf[Sqrt[2]*Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]]*Sqrt[2*Pi]*Sqrt[c]-1/3*Sqrt[c*ProductLog[a*x^2]]/x^4");
  }

  // 8.9 Product logarithm function.input:267
  public void test0292() {
    check( //
        "Integrate[x^4*ProductLog[a/x], x]", //
        "-125/24*a^5*ExpIntegralEi[-5*ProductLog[a/x]]+1/4*x^5*ProductLog[a/x]-1/12*x^5*ProductLog[a/x]^2+5/24*x^5*ProductLog[a/x]^3-25/24*x^5*ProductLog[a/x]^4");
  }

  // 8.9 Product logarithm function.input:283
  public void test0293() {
    check( //
        "Integrate[ProductLog[a/x]^2/x^2, x]", //
        "(-4)/x+4/(x*ProductLog[a/x])+2*ProductLog[a/x]/x-ProductLog[a/x]^2/x");
  }

  // 8.9 Product logarithm function.input:307
  public void test0294() {
    check( //
        "Integrate[(c*ProductLog[a/x])^p/x^3, x]", //
        "-2^(-2-p)*Gamma[2+p,-2*ProductLog[a/x]]*(-ProductLog[a/x])^(-1-p)*(c*ProductLog[a/x])^p/(E^ProductLog[a/x]*a*x)-2^(-3-p)*Gamma[3+p,-2*ProductLog[a/x]]*(-ProductLog[a/x])^(-2-p)*(c*ProductLog[a/x])^(1+p)/(E^ProductLog[a/x]*a*c*x)");
  }

  // 8.9 Product logarithm function.input:335
  public void test0295() {
    check( //
        "Integrate[x^(-1-n)/(c*ProductLog[a*x^n])^(3/2), x]", //
        "(-2/5)/(n*x^n*(c*ProductLog[a*x^n])^(3/2))+4/5*a*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(c^(3/2)*n)+(-2/5)/(c*n*x^n*Sqrt[c*ProductLog[a*x^n]])+4/5*Sqrt[c*ProductLog[a*x^n]]/(c^2*n*x^n)");
  }

  // 8.9 Product logarithm function.input:343
  public void test0296() {
    check( //
        "Integrate[x^(-1-2*n)/(c*ProductLog[a*x^n])^(1/2), x]", //
        "8/15*(c*ProductLog[a*x^n])^(3/2)/(c^2*n*x^(2*n))+8/15*a^2*Erf[Sqrt[2]*Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[2*Pi]/(n*Sqrt[c])+(-2/5)/(n*x^(2*n)*Sqrt[c*ProductLog[a*x^n]])-2/15*Sqrt[c*ProductLog[a*x^n]]/(c*n*x^(2*n))");
  }

  // 8.9 Product logarithm function.input:351
  public void test0297() {
    check( //
        "Integrate[x^(-1+n)/(c*ProductLog[a*x^n])^(7/2), x]", //
        "-2/5*x^n/(n*(c*ProductLog[a*x^n])^(7/2))-14/15*x^n/(c*n*(c*ProductLog[a*x^n])^(5/2))-28/15*x^n/(c^2*n*(c*ProductLog[a*x^n])^(3/2))+28/15*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[Pi]/(a*c^(7/2)*n)");
  }

  // 8.9 Product logarithm function.input:359
  public void test0298() {
    check( //
        "Integrate[x^(-1+2*n)/(c*ProductLog[a*x^n])^(9/2), x]", //
        "-2/5*x^(2*n)/(n*(c*ProductLog[a*x^n])^(9/2))-6/5*x^(2*n)/(c*n*(c*ProductLog[a*x^n])^(7/2))-24/5*x^(2*n)/(c^2*n*(c*ProductLog[a*x^n])^(5/2))+24/5*Erfi[Sqrt[2]*Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]]*Sqrt[2*Pi]/(a^2*c^(9/2)*n)");
  }
}
