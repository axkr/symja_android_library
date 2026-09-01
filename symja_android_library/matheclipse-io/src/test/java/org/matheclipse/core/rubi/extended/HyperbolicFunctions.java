package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 6 Hyperbolic functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class HyperbolicFunctions extends AbstractRubiTestCase {
  static boolean init = true;

  public HyperbolicFunctions(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("HyperbolicFunctions");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 6.4.2 Hyperbolic cotangent functions.input:208
  public void test0001() {
    check( //
        "Integrate[x^3*Coth[a+2*Log[x]], x]", //
        "1/4*x^4+1/2*Log[1-E^(2*a)*x^4]/E^(2*a)");
  }

  // 6.2.5 Hyperbolic cosine functions.input:288
  public void test0002() {
    check( //
        "Integrate[Cosh[Sqrt[1-a*x]/Sqrt[1+a*x]]^3/(1-a^2*x^2), x]", //
        "-3/4*CoshIntegral[Sqrt[1-a*x]/Sqrt[1+a*x]]/a-1/4*CoshIntegral[3*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 6.7.1 Hyperbolic functions.input:167
  public void test0003() {
    check( //
        "Integrate[Coth[c+b*x]*Sinh[a+b*x], x]", //
        "-ArcTanh[Cosh[c+b*x]]*Sinh[a-c]/b+Sinh[a+b*x]/b");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:529
  public void test0004() {
    check( //
        "Integrate[Coth[e+f*x]/Sqrt[a+a*Sinh[e+f*x]^2], x]", //
        "-ArcTanh[Sqrt[a*Cosh[e+f*x]^2]/Sqrt[a]]/(f*Sqrt[a])");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:130
  public void test0005() {
    check( //
        "Integrate[Cosh[x]^4/(a+b*Csch[x]), x]", //
        "1/8*(3*a^4+12*a^2*b^2+8*b^4)*x/a^5+2*b*(a^2+b^2)^(3/2)*ArcTanh[(a-b*Tanh[1/2*x])/Sqrt[a^2+b^2]]/a^5-1/12*Cosh[x]^3*(4*b-3*a*Sinh[x])/a^2-1/8*Cosh[x]*(8*b*(a^2+b^2)-a*(3*a^2+4*b^2)*Sinh[x])/a^4");
  }

  // 6.7.1 Hyperbolic functions.input:956
  public void test0006() {
    check( //
        "Integrate[1/(Coth[x]^2-Csch[x]^2)^3, x]", //
        "x");
  }

  // 6.7.1 Hyperbolic functions.input:893
  public void test0007() {
    check( //
        "Integrate[1/(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^(3/2), x]", //
        "1/2*ArcTan[(b^2-c^2)^(1/4)*Sinh[x+I*ArcTan[b,-I*c]]/(Sqrt[2]*Sqrt[Sqrt[b^2-c^2]+Cosh[x+I*ArcTan[b,-I*c]]*Sqrt[b^2-c^2]])]/((b^2-c^2)^(3/4)*Sqrt[2])+1/2*(c*Cosh[x]+b*Sinh[x])/(Sqrt[b^2-c^2]*(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^(3/2))");
  }

  // 6.7.1 Hyperbolic functions.input:788
  public void test0008() {
    check( //
        "Integrate[Sinh[x]^2/(a*Cosh[x]+b*Sinh[x]), x]", //
        "-a^2*ArcTan[(b*Cosh[x]+a*Sinh[x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(3/2)-b*Cosh[x]/(a^2-b^2)+a*Sinh[x]/(a^2-b^2)");
  }

  // 6.3.2 Hyperbolic tangent functions.input:182
  public void test0009() {
    check( //
        "Integrate[Tanh[x]^2/Sqrt[1+Tanh[x]], x]", //
        "ArcTanh[Sqrt[1+Tanh[x]]/Sqrt[2]]/Sqrt[2]+(-1)/Sqrt[1+Tanh[x]]-2*Sqrt[1+Tanh[x]]");
  }

  // 6.7.1 Hyperbolic functions.input:881
  public void test0010() {
    check( //
        "Integrate[1/(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^4, x]", //
        "1/7*(c*Cosh[x]+b*Sinh[x])/(Sqrt[b^2-c^2]*(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^4)+3/35*(c*Cosh[x]+b*Sinh[x])/((b^2-c^2)*(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^3)+2/35*(c*Cosh[x]+b*Sinh[x])/((b^2-c^2)^(3/2)*(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^2)-2/35*(c+Sinh[x]*Sqrt[b^2-c^2])/(c*(b^2-c^2)^(3/2)*(c*Cosh[x]+b*Sinh[x]))");
  }

  // 6.5.3 Hyperbolic secant functions.input:51
  public void test0011() {
    check( //
        "Integrate[(a*Sech[x]^2)^(1/2), x]", //
        "ArcTan[Sqrt[a]*Tanh[x]/Sqrt[a*Sech[x]^2]]*Sqrt[a]");
  }

  // 6.7.1 Hyperbolic functions.input:1251
  public void test0012() {
    check( //
        "Integrate[Coth[x]^3*Csch[x]*Sqrt[1+Csch[x]], x]", //
        "-4/3*(1+Csch[x])^(3/2)+4/5*(1+Csch[x])^(5/2)-2/7*(1+Csch[x])^(7/2)");
  }

  // 6.7.1 Hyperbolic functions.input:840
  public void test0013() {
    check( //
        "Integrate[(A+C*Sinh[x])/(b*Cosh[x]+c*Sinh[x])^2, x]", //
        "-c*C*ArcTan[(c*Cosh[x]+b*Sinh[x])/Sqrt[b^2-c^2]]/(b^2-c^2)^(3/2)+(-b*C+A*c*Cosh[x]+A*b*Sinh[x])/((b^2-c^2)*(b*Cosh[x]+c*Sinh[x]))");
  }

  // 6.7.1 Hyperbolic functions.input:642
  public void test0014() {
    check( //
        "Integrate[(b+c+Sinh[x])/(a-b*Cosh[x]), x]", //
        "-Log[a-b*Cosh[x]]/b+2*(b+c)*ArcTanh[Sqrt[a+b]*Tanh[1/2*x]/Sqrt[a-b]]/(Sqrt[a-b]*Sqrt[a+b])");
  }

  // 6.1.5 Hyperbolic sine functions.input:119
  public void test0015() {
    check( //
        "Integrate[1/(3+5*I*Sinh[c+d*x]), x]", //
        "1/4*I*Log[3*Cosh[1/2*(c+d*x)]+I*Sinh[1/2*(c+d*x)]]/d-1/4*I*Log[Cosh[1/2*(c+d*x)]+3*I*Sinh[1/2*(c+d*x)]]/d");
  }

  // 6.3.7 (d hyper)^m (a+b (c tanh)^n)^p.input:15
  public void test0016() {
    check( //
        "Integrate[Csch[c+d*x]^2*(a+b*Tanh[c+d*x]^2), x]", //
        "-a*Coth[c+d*x]/d+b*Tanh[c+d*x]/d");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:121
  public void test0017() {
    check( //
        "Integrate[Cosh[x]^2/(I+Csch[x]), x]", //
        "1/2*I*x+Cosh[x]-1/2*I*Cosh[x]*Sinh[x]");
  }

  // 6.2.5 Hyperbolic cosine functions.input:111
  public void test0018() {
    check( //
        "Integrate[1/(a+b*Cosh[x])^(1/2), x]", //
        "-2*I*EllipticF[1/2*I*x,2*b/(a+b)]*Sqrt[(a+b*Cosh[x])/(a+b)]/Sqrt[a+b*Cosh[x]]");
  }

  // 6.5.3 Hyperbolic secant functions.input:27
  public void test0019() {
    check( //
        "Integrate[(b*Sech[c+d*x])^(5/2), x]", //
        "2/3*b*(b*Sech[c+d*x])^(3/2)*Sinh[c+d*x]/d-2/3*I*b^2*EllipticF[1/2*I*(c+d*x),2]*Sqrt[Cosh[c+d*x]]*Sqrt[b*Sech[c+d*x]]/d");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:33
  public void test0020() {
    check( //
        "Integrate[Cosh[c+d*x]/(a+b*x), x]", //
        "CoshIntegral[a*d/b+d*x]*Cosh[c-a*d/b]/b+SinhIntegral[a*d/b+d*x]*Sinh[c-a*d/b]/b");
  }

  // 6.7.1 Hyperbolic functions.input:671
  public void test0021() {
    check( //
        "Integrate[(a*Cosh[x]+b*Sinh[x])^(1/2), x]", //
        "-2*I*EllipticE[1/2*(I*x-ArcTan[a,-I*b]),2]*Sqrt[a*Cosh[x]+b*Sinh[x]]/Sqrt[(a*Cosh[x]+b*Sinh[x])/Sqrt[a^2-b^2]]");
  }

  // 6.7.1 Hyperbolic functions.input:766
  public void test0022() {
    check( //
        "Integrate[(Csch[x]+Sinh[x])^(1/2), x]", //
        "2*Sqrt[Cosh[x]*Coth[x]]*Tanh[x]");
  }

  // 6.1.5 Hyperbolic sine functions.input:96
  public void test0023() {
    check( //
        "Integrate[1/(a+I*a*Sinh[c+d*x])^(5/2), x]", //
        "1/4*I*Cosh[c+d*x]/(d*(a+I*a*Sinh[c+d*x])^(5/2))+3/16*I*Cosh[c+d*x]/(a*d*(a+I*a*Sinh[c+d*x])^(3/2))+3/16*I*ArcTanh[Cosh[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Sinh[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 6.5.3 Hyperbolic secant functions.input:257
  public void test0024() {
    check( //
        "Integrate[Sech[a+b*Log[c*x^n]]^2/x, x]", //
        "Tanh[a+b*Log[c*x^n]]/(b*n)");
  }

  // 6.1.3 (e x)^m (a+b sinh(c+d x^n))^p.input:31
  public void test0025() {
    check( //
        "Integrate[Sinh[a+b*x^2]^3/x^2, x]", //
        "-Sinh[a+b*x^2]^3/x-3/8*Erf[x*Sqrt[b]]*Sqrt[Pi]*Sqrt[b]/E^a-3/8*E^a*Erfi[x*Sqrt[b]]*Sqrt[Pi]*Sqrt[b]+1/8*Erf[x*Sqrt[3]*Sqrt[b]]*Sqrt[3*Pi]*Sqrt[b]/E^(3*a)+1/8*E^(3*a)*Erfi[x*Sqrt[3]*Sqrt[b]]*Sqrt[3*Pi]*Sqrt[b]");
  }

  // 6.5.3 Hyperbolic secant functions.input:81
  public void test0026() {
    check( //
        "Integrate[Sinh[x]^4/(a+a*Sech[x]), x]", //
        "-1/8*x/a-1/8*Cosh[x]*Sinh[x]/a+1/4*Cosh[x]^3*Sinh[x]/a-1/3*Sinh[x]^3/a");
  }

  // 6.2.5 Hyperbolic cosine functions.input:171
  public void test0027() {
    check( //
        "Integrate[1/(a*Cosh[x]^2)^(3/2), x]", //
        "1/2*ArcTan[Sinh[x]]*Cosh[x]/(a*Sqrt[a*Cosh[x]^2])+1/2*Tanh[x]/(a*Sqrt[a*Cosh[x]^2])");
  }

  // 6.3.2 Hyperbolic tangent functions.input:21
  public void test0028() {
    check( //
        "Integrate[Coth[a+b*x]^6, x]", //
        "x-Coth[a+b*x]/b-1/3*Coth[a+b*x]^3/b-1/5*Coth[a+b*x]^5/b");
  }

  // 6.1.5 Hyperbolic sine functions.input:470
  public void test0029() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*Sinh[d+f*x^2]^2, x]", //
        "-1/4*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(Sqrt[c]*Sqrt[Log[f]])-1/8*E^(-2*d+b^2*Log[f]^2/(8*f-4*c*Log[f]))*f^a*Erf[1/2*(b*Log[f]-2*x*(2*f-c*Log[f]))/Sqrt[2*f-c*Log[f]]]*Sqrt[Pi]/Sqrt[2*f-c*Log[f]]+1/8*E^(2*d-b^2*Log[f]^2/(8*f+4*c*Log[f]))*f^a*Erfi[1/2*(b*Log[f]+2*x*(2*f+c*Log[f]))/Sqrt[2*f+c*Log[f]]]*Sqrt[Pi]/Sqrt[2*f+c*Log[f]]");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:217
  public void test0030() {
    check( //
        "Integrate[Csch[a-Log[c*x^n]/(n*(-2+p))]^p, x]", //
        "1/2*(2-p)*x*(1+(-1)/(E^(2*a)*(c*x^n)^(2/(n*(2-p)))))*Csch[a+Log[c*x^n]/(n*(2-p))]^p/(1-p)");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:147
  public void test0031() {
    check( //
        "Integrate[Cosh[x]^3/(1+Coth[x]), x]", //
        "1/5*Cosh[x]^5-1/3*Sinh[x]^3-1/5*Sinh[x]^5");
  }

  // 6.1.5 Hyperbolic sine functions.input:282
  public void test0032() {
    check( //
        "Integrate[Coth[x]^6/(I+Sinh[x]), x]", //
        "-3/8*ArcTanh[Cosh[x]]+1/5*I*Coth[x]^5-3/8*Coth[x]*Csch[x]-1/4*Coth[x]^3*Csch[x]");
  }

  // 6.7.1 Hyperbolic functions.input:954
  public void test0033() {
    check( //
        "Integrate[1/(Coth[x]^2-Csch[x]^2), x]", //
        "x");
  }

  // 6.2.7 hyper^m (a+b cosh^n)^p.input:108
  public void test0034() {
    check( //
        "Integrate[1/(1-Cosh[x]^4), x]", //
        "1/2*Coth[x]+1/2*ArcTanh[Tanh[x]/Sqrt[2]]/Sqrt[2]");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:91
  public void test0035() {
    check( //
        "Integrate[Cosh[c+d*x]/(a+b*Sech[c+d*x]^2), x]", //
        "Sinh[c+d*x]/(a*d)-b*ArcTan[Sinh[c+d*x]*Sqrt[a]/Sqrt[a+b]]/(a^(3/2)*d*Sqrt[a+b])");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:107
  public void test0036() {
    check( //
        "Integrate[(a+b*x^3)*Cosh[c+d*x]/x^4, x]", //
        "b*CoshIntegral[d*x]*Cosh[c]-1/3*a*Cosh[c+d*x]/x^3-1/6*a*d^2*Cosh[c+d*x]/x+1/6*a*d^3*Cosh[c]*SinhIntegral[d*x]+1/6*a*d^3*CoshIntegral[d*x]*Sinh[c]+b*SinhIntegral[d*x]*Sinh[c]-1/6*a*d*Sinh[c+d*x]/x^2");
  }

  // 6.2.5 Hyperbolic cosine functions.input:176
  public void test0037() {
    check( //
        "Integrate[(a*Cosh[x]^3)^(3/2), x]", //
        "-14/15*I*a*EllipticE[1/2*I*x,2]*Sqrt[a*Cosh[x]^3]/Cosh[x]^(3/2)+14/45*a*Sinh[x]*Sqrt[a*Cosh[x]^3]+2/9*a*Cosh[x]^2*Sinh[x]*Sqrt[a*Cosh[x]^3]");
  }

  // 6.1.5 Hyperbolic sine functions.input:73
  public void test0038() {
    check( //
        "Integrate[Sinh[x]^4/(I+Sinh[x])^2, x]", //
        "-7/2*x-16/3*I*Cosh[x]+7/2*Cosh[x]*Sinh[x]-1/3*Cosh[x]*Sinh[x]^3/(I+Sinh[x])^2-8/3*Cosh[x]*Sinh[x]^2/(I+Sinh[x])");
  }

  // 6.7.1 Hyperbolic functions.input:720
  public void test0039() {
    check( //
        "Integrate[(Sech[x]-I*Tanh[x])^2, x]", //
        "-x+2*I*Cosh[x]/(1+I*Sinh[x])");
  }

  // 6.3.1 (c+d x)^m (a+b tanh)^n.input:110
  public void test0040() {
    check( //
        "Integrate[(c+d*x)/(a+b*Tanh[e+f*x])^2, x]", //
        "-1/2*(c+d*x)^2/((a^2-b^2)*d)+1/4*(b*d-2*a*c*f-2*a*d*f*x)^2/(a*(a-b)*(a+b)^2*d*f^2)+b*(b*d-2*a*c*f-2*a*d*f*x)*Log[1+(a-b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)^2*f^2)+a*b*d*PolyLog[2,(-a+b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)^2*f^2)+b*(c+d*x)/((a^2-b^2)*f*(a+b*Tanh[e+f*x]))");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:401
  public void test0041() {
    check( //
        "Integrate[Cosh[c+d*x]^3/(a+b*Sinh[c+d*x]^2)^3, x]", //
        "1/8*(a+3*b)*ArcTan[Sinh[c+d*x]*Sqrt[b]/Sqrt[a]]/(a^(5/2)*b^(3/2)*d)-1/4*(a-b)*Sinh[c+d*x]/(a*b*d*(a+b*Sinh[c+d*x]^2)^2)+1/8*(a+3*b)*Sinh[c+d*x]/(a^2*b*d*(a+b*Sinh[c+d*x]^2))");
  }

  // 6.5.1 (c+d x)^m (a+b sech)^n.input:12
  public void test0042() {
    check( //
        "Integrate[(c+d*x)^3*Sech[a+b*x]^2, x]", //
        "(c+d*x)^3/b-3*d*(c+d*x)^2*Log[1+E^(2*(a+b*x))]/b^2-3*d^2*(c+d*x)*PolyLog[2,-E^(2*(a+b*x))]/b^3+3/2*d^3*PolyLog[3,-E^(2*(a+b*x))]/b^4+(c+d*x)^3*Tanh[a+b*x]/b");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:231
  public void test0043() {
    check( //
        "Integrate[(c+d*x)^2/(a+b*Sinh[e+f*x]), x]", //
        "(c+d*x)^2*Log[1+E^(e+f*x)*b/(a-Sqrt[a^2+b^2])]/(f*Sqrt[a^2+b^2])-(c+d*x)^2*Log[1+E^(e+f*x)*b/(a+Sqrt[a^2+b^2])]/(f*Sqrt[a^2+b^2])+2*d*(c+d*x)*PolyLog[2,-E^(e+f*x)*b/(a-Sqrt[a^2+b^2])]/(f^2*Sqrt[a^2+b^2])-2*d*(c+d*x)*PolyLog[2,-E^(e+f*x)*b/(a+Sqrt[a^2+b^2])]/(f^2*Sqrt[a^2+b^2])-2*d^2*PolyLog[3,-E^(e+f*x)*b/(a-Sqrt[a^2+b^2])]/(f^3*Sqrt[a^2+b^2])+2*d^2*PolyLog[3,-E^(e+f*x)*b/(a+Sqrt[a^2+b^2])]/(f^3*Sqrt[a^2+b^2])");
  }

  // 6.7.1 Hyperbolic functions.input:851
  public void test0044() {
    check( //
        "Integrate[(B*Cosh[x]+C*Sinh[x])/(b*Cosh[x]+c*Sinh[x])^2, x]", //
        "(b*B-c*C)*ArcTan[(c*Cosh[x]+b*Sinh[x])/Sqrt[b^2-c^2]]/(b^2-c^2)^(3/2)+(B*c-b*C)/((b^2-c^2)*(b*Cosh[x]+c*Sinh[x]))");
  }

  // 6.7.1 Hyperbolic functions.input:823
  public void test0045() {
    check( //
        "Integrate[Cosh[x]^2*Sinh[x]^2/(a*Cosh[x]+b*Sinh[x]), x]", //
        "a^2*b^2*ArcTan[(b*Cosh[x]+a*Sinh[x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(5/2)+a^2*b*Cosh[x]/(a^2-b^2)^2-1/3*b*Cosh[x]^3/(a^2-b^2)-a*b^2*Sinh[x]/(a^2-b^2)^2+1/3*a*Sinh[x]^3/(a^2-b^2)");
  }

  // 6.2.5 Hyperbolic cosine functions.input:330
  public void test0046() {
    check( //
        "Integrate[Cosh[a+b*Log[c*x^n]]^3/x, x]", //
        "Sinh[a+b*Log[c*x^n]]/(b*n)+1/3*Sinh[a+b*Log[c*x^n]]^3/(b*n)");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:375
  public void test0047() {
    check( //
        "Integrate[Cosh[c+d*x]^7/(a+b*Sinh[c+d*x]^2), x]", //
        "(a^2-3*a*b+3*b^2)*Sinh[c+d*x]/(b^3*d)-1/3*(a-3*b)*Sinh[c+d*x]^3/(b^2*d)+1/5*Sinh[c+d*x]^5/(b*d)-(a-b)^3*ArcTan[Sinh[c+d*x]*Sqrt[b]/Sqrt[a]]/(b^(7/2)*d*Sqrt[a])");
  }

  // 6.7.1 Hyperbolic functions.input:296
  public void test0048() {
    check( //
        "Integrate[x^2*Cosh[a+b*x]^2*Sinh[a+b*x], x]", //
        "4/9*Cosh[a+b*x]/b^3+2/27*Cosh[a+b*x]^3/b^3+1/3*x^2*Cosh[a+b*x]^3/b-4/9*x*Sinh[a+b*x]/b^2-2/9*x*Cosh[a+b*x]^2*Sinh[a+b*x]/b^2");
  }

  // 6.3.2 Hyperbolic tangent functions.input:111
  public void test0049() {
    check( //
        "Integrate[Csch[x]^2/(1+Tanh[x]), x]", //
        "-Coth[x]-Log[Tanh[x]]+Log[1+Tanh[x]]");
  }

  // 6.1.5 Hyperbolic sine functions.input:83
  public void test0050() {
    check( //
        "Integrate[1/(1+I*Sinh[c+d*x])^3, x]", //
        "1/5*I*Cosh[c+d*x]/(d*(1+I*Sinh[c+d*x])^3)+2/15*I*Cosh[c+d*x]/(d*(1+I*Sinh[c+d*x])^2)+2/15*I*Cosh[c+d*x]/(d*(1+I*Sinh[c+d*x]))");
  }

  // 6.7.1 Hyperbolic functions.input:1176
  public void test0051() {
    check( //
        "Integrate[Sech[x]^2*(a+b*Tanh[x])^2/(c+d*Tanh[x]), x]", //
        "(b*c-a*d)^2*Log[c+d*Tanh[x]]/d^3-b*(b*c-a*d)*Tanh[x]/d^2+1/2*(a+b*Tanh[x])^2/d");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:94
  public void test0052() {
    check( //
        "Integrate[x/Sinh[x]^(5/2)+1/3*x/Sqrt[Sinh[x]], x]", //
        "-2/3*x*Cosh[x]/Sinh[x]^(3/2)+(-4/3)/Sqrt[Sinh[x]]");
  }

  // 6.3.7 (d hyper)^m (a+b (c tanh)^n)^p.input:79
  public void test0053() {
    check( //
        "Integrate[Csch[c+d*x]^4*(a+b*Tanh[c+d*x]^3)^2, x]", //
        "a^2*Coth[c+d*x]/d-1/3*a^2*Coth[c+d*x]^3/d+2*a*b*Log[Tanh[c+d*x]]/d-a*b*Tanh[c+d*x]^2/d+1/3*b^2*Tanh[c+d*x]^3/d-1/5*b^2*Tanh[c+d*x]^5/d");
  }

  // 6.1.5 Hyperbolic sine functions.input:312
  public void test0054() {
    check( //
        "Integrate[Coth[x]/Sqrt[a+b*Sinh[x]], x]", //
        "-2*ArcTanh[Sqrt[a+b*Sinh[x]]/Sqrt[a]]/Sqrt[a]");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:94
  public void test0055() {
    check( //
        "Integrate[1/(1+Coth[x]), x]", //
        "1/2*x+(-1/2)/(1+Coth[x])");
  }

  // 6.2.5 Hyperbolic cosine functions.input:130
  public void test0056() {
    check( //
        "Integrate[(A+B*Cosh[x])/(1+Cosh[x])^3, x]", //
        "1/5*(A-B)*Sinh[x]/(1+Cosh[x])^3+1/15*(2*A+3*B)*Sinh[x]/(1+Cosh[x])^2+1/15*(2*A+3*B)*Sinh[x]/(1+Cosh[x])");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:62
  public void test0057() {
    check( //
        "Integrate[(a+b*x^2)*Cosh[c+d*x]/x^4, x]", //
        "-1/3*a*Cosh[c+d*x]/x^3-b*Cosh[c+d*x]/x-1/6*a*d^2*Cosh[c+d*x]/x+b*d*Cosh[c]*SinhIntegral[d*x]+1/6*a*d^3*Cosh[c]*SinhIntegral[d*x]+b*d*CoshIntegral[d*x]*Sinh[c]+1/6*a*d^3*CoshIntegral[d*x]*Sinh[c]-1/6*a*d*Sinh[c+d*x]/x^2");
  }

  // 6.5.3 Hyperbolic secant functions.input:46
  public void test0058() {
    check( //
        "Integrate[1/(Sech[a+b*x]^2)^(3/2), x]", //
        "1/3*Tanh[a+b*x]/(b*(Sech[a+b*x]^2)^(3/2))+2/3*Tanh[a+b*x]/(b*Sqrt[Sech[a+b*x]^2])");
  }

  // 6.2.3 (e x)^m (a+b cosh(c+d x^n))^p.input:21
  public void test0059() {
    check( //
        "Integrate[x*Cosh[a+b*x^2]^2, x]", //
        "1/4*x^2+1/4*Cosh[a+b*x^2]*Sinh[a+b*x^2]/b");
  }

  // 6.7.1 Hyperbolic functions.input:1224
  public void test0060() {
    check( //
        "Integrate[(a+b*Coth[x])^3*Csch[x]^2/(c+d*Coth[x]), x]", //
        "-b*(b*c-a*d)^2*Coth[x]/d^3+1/2*(b*c-a*d)*(a+b*Coth[x])^2/d^2-1/3*(a+b*Coth[x])^3/d+(b*c-a*d)^3*Log[c+d*Coth[x]]/d^4");
  }

  // 6.7.1 Hyperbolic functions.input:208
  public void test0061() {
    check( //
        "Integrate[Cosh[c+d*x]*Sinh[a+b*x]^3, x]", //
        "-3/8*Cosh[a-c+(b-d)*x]/(b-d)+1/8*Cosh[3*a-c+(3*b-d)*x]/(3*b-d)-3/8*Cosh[a+c+(b+d)*x]/(b+d)+1/8*Cosh[3*a+c+(3*b+d)*x]/(3*b+d)");
  }

  // 6.7.1 Hyperbolic functions.input:872
  public void test0062() {
    check( //
        "Integrate[1/(a+a*Cosh[x]+c*Sinh[x])^3, x]", //
        "1/2*(3*a^2-c^2)*Log[a+c*Tanh[1/2*x]]/c^5+1/2*(-c*Cosh[x]-a*Sinh[x])/(c^2*(a+a*Cosh[x]+c*Sinh[x])^2)-3/2*(a*c*Cosh[x]+a^2*Sinh[x])/(c^4*(a+a*Cosh[x]+c*Sinh[x]))");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:206
  public void test0063() {
    check( //
        "Integrate[(c+d*x)^m*(a+I*a*Sinh[e+f*x])^2, x]", //
        "3/2*a^2*(c+d*x)^(1+m)/(d*(1+m))-2^(-3-m)*E^(2*e-2*c*f/d)*a^2*(c+d*x)^m*Gamma[1+m,-2*f*(c+d*x)/d]/(f*(-f*(c+d*x)/d)^m)+I*E^(e-c*f/d)*a^2*(c+d*x)^m*Gamma[1+m,-f*(c+d*x)/d]/(f*(-f*(c+d*x)/d)^m)+I*E^(-e+c*f/d)*a^2*(c+d*x)^m*Gamma[1+m,f*(c+d*x)/d]/(f*(f*(c+d*x)/d)^m)+2^(-3-m)*E^(-2*e+2*c*f/d)*a^2*(c+d*x)^m*Gamma[1+m,2*f*(c+d*x)/d]/(f*(f*(c+d*x)/d)^m)");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:40
  public void test0064() {
    check( //
        "Integrate[(c+d*x)^2*Sech[a+b*x], x]", //
        "2*(c+d*x)^2*ArcTan[E^(a+b*x)]/b-2*I*d*(c+d*x)*PolyLog[2,-I*E^(a+b*x)]/b^2+2*I*d*(c+d*x)*PolyLog[2,I*E^(a+b*x)]/b^2+2*I*d^2*PolyLog[3,-I*E^(a+b*x)]/b^3-2*I*d^2*PolyLog[3,I*E^(a+b*x)]/b^3");
  }

  // 6.2.3 (e x)^m (a+b cosh(c+d x^n))^p.input:12
  public void test0065() {
    check( //
        "Integrate[x^3*Cosh[a+b*x^2], x]", //
        "-1/2*Cosh[a+b*x^2]/b^2+1/2*x^2*Sinh[a+b*x^2]/b");
  }

  // 6.4.1 (c+d x)^m (a+b coth)^n.input:43
  public void test0066() {
    check( //
        "Integrate[(c+d*x)/(a+a*Coth[e+f*x])^2, x]", //
        "3/16*d*x/(a^2*f)-1/8*d*x^2/a^2+1/4*x*(c+d*x)/a^2-1/16*d/(f^2*(a+a*Coth[e+f*x])^2)+1/4*(-c-d*x)/(f*(a+a*Coth[e+f*x])^2)-3/16*d/(f^2*(a^2+a^2*Coth[e+f*x]))+1/4*(-c-d*x)/(f*(a^2+a^2*Coth[e+f*x]))");
  }

  // 6.7.1 Hyperbolic functions.input:179
  public void test0067() {
    check( //
        "Integrate[Cosh[a+b*x]*Coth[c+b*x], x]", //
        "-ArcTanh[Cosh[c+b*x]]*Cosh[a-c]/b+Cosh[a+b*x]/b");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:102
  public void test0068() {
    check( //
        "Integrate[(a+b*Csch[c+d*x])^3, x]", //
        "a^3*x-1/2*b*(6*a^2-b^2)*ArcTanh[Cosh[c+d*x]]/d-5/2*a*b^2*Coth[c+d*x]/d-1/2*b^2*Coth[c+d*x]*(a+b*Csch[c+d*x])/d");
  }

  // 6.2.5 Hyperbolic cosine functions.input:76
  public void test0069() {
    check( //
        "Integrate[1/(a-a*Cosh[c+d*x])^(5/2), x]", //
        "-1/4*Sinh[c+d*x]/(d*(a-a*Cosh[c+d*x])^(5/2))-3/16*Sinh[c+d*x]/(a*d*(a-a*Cosh[c+d*x])^(3/2))-3/16*ArcTan[Sinh[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a-a*Cosh[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:119
  public void test0070() {
    check( //
        "Integrate[Cosh[x]^4/(I+Csch[x]), x]", //
        "1/8*I*x+1/3*Cosh[x]^3+1/8*I*Cosh[x]*Sinh[x]-1/4*I*Cosh[x]^3*Sinh[x]");
  }

  // 6.3.2 Hyperbolic tangent functions.input:82
  public void test0071() {
    check( //
        "Integrate[(1+Tanh[x])^(5/2), x]", //
        "4*ArcTanh[Sqrt[1+Tanh[x]]/Sqrt[2]]*Sqrt[2]-4*Sqrt[1+Tanh[x]]-2/3*(1+Tanh[x])^(3/2)");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:104
  public void test0072() {
    check( //
        "Integrate[1/(1+Coth[x])^(3/2), x]", //
        "(-1/3)/(1+Coth[x])^(3/2)+1/2*ArcTanh[Sqrt[1+Coth[x]]/Sqrt[2]]/Sqrt[2]+(-1/2)/Sqrt[1+Coth[x]]");
  }

  // 6.2.5 Hyperbolic cosine functions.input:160
  public void test0073() {
    check( //
        "Integrate[(A+B*Cosh[x])/(a+b*Cosh[x])^(3/2), x]", //
        "-2*(A*b-a*B)*Sinh[x]/((a^2-b^2)*Sqrt[a+b*Cosh[x]])-2*I*(A*b-a*B)*EllipticE[1/2*I*x,2*b/(a+b)]*Sqrt[a+b*Cosh[x]]/(b*(a^2-b^2)*Sqrt[(a+b*Cosh[x])/(a+b)])-2*I*B*EllipticF[1/2*I*x,2*b/(a+b)]*Sqrt[(a+b*Cosh[x])/(a+b)]/(b*Sqrt[a+b*Cosh[x]])");
  }

  // 6.2.5 Hyperbolic cosine functions.input:73
  public void test0074() {
    check( //
        "Integrate[(a-a*Cosh[c+d*x])^(1/2), x]", //
        "-2*a*Sinh[c+d*x]/(d*Sqrt[a-a*Cosh[c+d*x]])");
  }

  // 6.4.1 (c+d x)^m (a+b coth)^n.input:47
  public void test0075() {
    check( //
        "Integrate[(c+d*x)^2/(a+a*Coth[e+f*x])^3, x]", //
        "1/864*E^(-6*e-6*f*x)*d^2/(a^3*f^3)-3/256*E^(-4*e-4*f*x)*d^2/(a^3*f^3)+3/32*E^(-2*e-2*f*x)*d^2/(a^3*f^3)+1/144*E^(-6*e-6*f*x)*d*(c+d*x)/(a^3*f^2)-3/64*E^(-4*e-4*f*x)*d*(c+d*x)/(a^3*f^2)+3/16*E^(-2*e-2*f*x)*d*(c+d*x)/(a^3*f^2)+1/48*E^(-6*e-6*f*x)*(c+d*x)^2/(a^3*f)-3/32*E^(-4*e-4*f*x)*(c+d*x)^2/(a^3*f)+3/16*E^(-2*e-2*f*x)*(c+d*x)^2/(a^3*f)+1/24*(c+d*x)^3/(a^3*d)");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:153
  public void test0076() {
    check( //
        "Integrate[Coth[x]^6/(I+Csch[x]), x]", //
        "-I*x-3/8*ArcTanh[Cosh[x]]+1/12*Coth[x]^3*(4*I-3*Csch[x])+1/8*Coth[x]*(8*I-3*Csch[x])");
  }

  // 6.2.5 Hyperbolic cosine functions.input:168
  public void test0077() {
    check( //
        "Integrate[(a*Cosh[x]^2)^(3/2), x]", //
        "1/3*(a*Cosh[x]^2)^(3/2)*Tanh[x]+2/3*a*Sqrt[a*Cosh[x]^2]*Tanh[x]");
  }

  // 6.5.3 Hyperbolic secant functions.input:91
  public void test0078() {
    check( //
        "Integrate[Sinh[x]^4/(a+b*Sech[x]), x]", //
        "1/8*(3*a^4-12*a^2*b^2+8*b^4)*x/a^5-2*(a-b)^(3/2)*b*(a+b)^(3/2)*ArcTan[Sqrt[a-b]*Tanh[1/2*x]/Sqrt[a+b]]/a^5+1/8*(8*b*(a^2-b^2)-a*(3*a^2-4*b^2)*Cosh[x])*Sinh[x]/a^4-1/12*(4*b-3*a*Cosh[x])*Sinh[x]^3/a^2");
  }

  // 6.4.1 (c+d x)^m (a+b coth)^n.input:37
  public void test0079() {
    check( //
        "Integrate[(c+d*x)/(a+a*Coth[e+f*x]), x]", //
        "1/4*d*x/(a*f)+1/4*(c+d*x)^2/(a*d)-1/4*d/(f^2*(a+a*Coth[e+f*x]))+1/2*(-c-d*x)/(f*(a+a*Coth[e+f*x]))");
  }

  // 6.2.5 Hyperbolic cosine functions.input:276
  public void test0080() {
    check( //
        "Integrate[(A+B*Cosh[d+e*x]+C*Sinh[d+e*x])/(a+b*Cosh[d+e*x])^2, x]", //
        "2*(a*A-b*B)*ArcTanh[Sqrt[a-b]*Tanh[1/2*(d+e*x)]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2)*e)-C/(b*e*(a+b*Cosh[d+e*x]))-(A*b-a*B)*Sinh[d+e*x]/((a^2-b^2)*e*(a+b*Cosh[d+e*x]))");
  }

  // 6.7.1 Hyperbolic functions.input:944
  public void test0081() {
    check( //
        "Integrate[1/(Cosh[x]^2-Sinh[x]^2)^3, x]", //
        "x");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:569
  public void test0082() {
    check( //
        "Integrate[(a+b*Sinh[e+f*x]^2)^(3/2), x]", //
        "1/3*b*Cosh[e+f*x]*Sinh[e+f*x]*Sqrt[a+b*Sinh[e+f*x]^2]/f-2/3*I*(2*a-b)*EllipticE[I*e+I*f*x,b/a]*Sqrt[a+b*Sinh[e+f*x]^2]/(f*Sqrt[1+b*Sinh[e+f*x]^2/a])+1/3*I*a*(a-b)*EllipticF[I*e+I*f*x,b/a]*Sqrt[1+b*Sinh[e+f*x]^2/a]/(f*Sqrt[a+b*Sinh[e+f*x]^2])");
  }

  // 6.1.5 Hyperbolic sine functions.input:239
  public void test0083() {
    check( //
        "Integrate[Sech[x]^4/(I+Sinh[x])^2, x]", //
        "-1/7*I*Sech[x]^3/(I+Sinh[x])^2-1/7*Sech[x]^3/(I+Sinh[x])-4/7*Tanh[x]+4/21*Tanh[x]^3");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:41
  public void test0084() {
    check( //
        "Integrate[Csch[c+d*x]^4/(a+b*Sech[c+d*x]^2), x]", //
        "a*Coth[c+d*x]/((a+b)^2*d)-1/3*Coth[c+d*x]^3/((a+b)*d)-a*ArcTanh[Sqrt[b]*Tanh[c+d*x]/Sqrt[a+b]]*Sqrt[b]/((a+b)^(5/2)*d)");
  }

  // 6.1.3 (e x)^m (a+b sinh(c+d x^n))^p.input:111
  public void test0085() {
    check( //
        "Integrate[x^2*Sinh[a+b*x^n], x]", //
        "-1/2*E^a*x^3*Gamma[3/n,-b*x^n]/(n*(-b*x^n)^(3/n))+1/2*x^3*Gamma[3/n,b*x^n]/(E^a*n*(b*x^n)^(3/n))");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:57
  public void test0086() {
    check( //
        "Integrate[x*(a+b*x^2)*Cosh[c+d*x], x]", //
        "-6*b*Cosh[c+d*x]/d^4-a*Cosh[c+d*x]/d^2-3*b*x^2*Cosh[c+d*x]/d^2+6*b*x*Sinh[c+d*x]/d^3+a*x*Sinh[c+d*x]/d+b*x^3*Sinh[c+d*x]/d");
  }

  // 6.3.2 Hyperbolic tangent functions.input:198
  public void test0087() {
    check( //
        "Integrate[x*Sech[x]^2/(a+b*Tanh[x])^2, x]", //
        "a*x/(b*(a^2-b^2))-Log[a*Cosh[x]+b*Sinh[x]]/(a^2-b^2)-x/(b*(a+b*Tanh[x]))");
  }

  // 6.5.3 Hyperbolic secant functions.input:67
  public void test0088() {
    check( //
        "Integrate[(a*Sech[x]^4)^(5/2), x]", //
        "a^2*Cosh[x]*Sinh[x]*Sqrt[a*Sech[x]^4]-4/3*a^2*Sinh[x]^2*Sqrt[a*Sech[x]^4]*Tanh[x]+6/5*a^2*Sinh[x]^2*Sqrt[a*Sech[x]^4]*Tanh[x]^3-4/7*a^2*Sinh[x]^2*Sqrt[a*Sech[x]^4]*Tanh[x]^5+1/9*a^2*Sinh[x]^2*Sqrt[a*Sech[x]^4]*Tanh[x]^7");
  }

  // 6.3.2 Hyperbolic tangent functions.input:84
  public void test0089() {
    check( //
        "Integrate[(1+Tanh[x])^(1/2), x]", //
        "ArcTanh[Sqrt[1+Tanh[x]]/Sqrt[2]]*Sqrt[2]");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:397
  public void test0090() {
    check( //
        "Integrate[Sech[c+d*x]^4/(a+b*Sinh[c+d*x]^2)^2, x]", //
        "1/2*(6*a-b)*b^2*ArcTanh[Sqrt[a-b]*Tanh[c+d*x]/Sqrt[a]]/(a^(3/2)*(a-b)^(7/2)*d)+(a-3*b)*Tanh[c+d*x]/((a-b)^3*d)-1/3*Tanh[c+d*x]^3/((a-b)^2*d)-1/2*b^3*Tanh[c+d*x]/(a*(a-b)^3*d*(a-(a-b)*Tanh[c+d*x]^2))");
  }

  // 6.2.5 Hyperbolic cosine functions.input:242
  public void test0091() {
    check( //
        "Integrate[Tanh[x]^2/(a+b*Cosh[x]), x]", //
        "b*ArcTan[Sinh[x]]/a^2+2*ArcTanh[Sqrt[a-b]*Tanh[1/2*x]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/a^2-Tanh[x]/a");
  }

  // 6.5.3 Hyperbolic secant functions.input:47
  public void test0092() {
    check( //
        "Integrate[1/(Sech[a+b*x]^2)^(5/2), x]", //
        "1/5*Tanh[a+b*x]/(b*(Sech[a+b*x]^2)^(5/2))+4/15*Tanh[a+b*x]/(b*(Sech[a+b*x]^2)^(3/2))+8/15*Tanh[a+b*x]/(b*Sqrt[Sech[a+b*x]^2])");
  }

  // 6.7.1 Hyperbolic functions.input:1128
  public void test0093() {
    check( //
        "Integrate[E^(c+d*x)*Cosh[a+b*x]^2*Sinh[a+b*x]^2, x]", //
        "-1/8*E^(c+d*x)/d-1/8*E^(c+d*x)*d*Cosh[4*a+4*b*x]/(16*b^2-d^2)+1/2*E^(c+d*x)*b*Sinh[4*a+4*b*x]/(16*b^2-d^2)");
  }

  // 6.2.7 hyper^m (a+b cosh^n)^p.input:59
  public void test0094() {
    check( //
        "Integrate[Cosh[x]^3/(a+b*Cosh[x]^2), x]", //
        "Sinh[x]/b-a*ArcTan[Sinh[x]*Sqrt[b]/Sqrt[a+b]]/(b^(3/2)*Sqrt[a+b])");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:158
  public void test0095() {
    check( //
        "Integrate[Cosh[x]^3/(a+b*Coth[x]), x]", //
        "a^3*b*ArcTanh[(a*Cosh[x]+b*Sinh[x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(5/2)-a^2*b*Cosh[x]/(a^2-b^2)^2-1/3*b*Cosh[x]^3/(a^2-b^2)+a*b^2*Sinh[x]/(a^2-b^2)^2+a*Sinh[x]/(a^2-b^2)+1/3*a*Sinh[x]^3/(a^2-b^2)");
  }

  // 6.1.5 Hyperbolic sine functions.input:169
  public void test0096() {
    check( //
        "Integrate[(a+b*Sinh[x])^(1/2)*(A+B*Sinh[x]), x]", //
        "2/3*B*Cosh[x]*Sqrt[a+b*Sinh[x]]+2/3*I*(3*A*b+a*B)*EllipticE[1/4*Pi-1/2*I*x,2*b/(I*a+b)]*Sqrt[a+b*Sinh[x]]/(b*Sqrt[(a+b*Sinh[x])/(a-I*b)])-2/3*I*(a^2+b^2)*B*EllipticF[1/4*Pi-1/2*I*x,2*b/(I*a+b)]*Sqrt[(a+b*Sinh[x])/(a-I*b)]/(b*Sqrt[a+b*Sinh[x]])");
  }

  // 6.7.1 Hyperbolic functions.input:304
  public void test0097() {
    check( //
        "Integrate[x^3*Cosh[a+b*x]^3*Sinh[a+b*x], x]", //
        "-45/256*x/b^3-3/32*x^3/b+9/32*x*Cosh[a+b*x]^2/b^3+3/32*x*Cosh[a+b*x]^4/b^3+1/4*x^3*Cosh[a+b*x]^4/b-45/256*Cosh[a+b*x]*Sinh[a+b*x]/b^4-9/32*x^2*Cosh[a+b*x]*Sinh[a+b*x]/b^2-3/128*Cosh[a+b*x]^3*Sinh[a+b*x]/b^4-3/16*x^2*Cosh[a+b*x]^3*Sinh[a+b*x]/b^2");
  }

  // 6.1.5 Hyperbolic sine functions.input:219
  public void test0098() {
    check( //
        "Integrate[Cosh[x]^6/(I+Sinh[x]), x]", //
        "-3/8*I*x+1/5*Cosh[x]^5-3/8*I*Cosh[x]*Sinh[x]-1/4*I*Cosh[x]^3*Sinh[x]");
  }

  // 6.7.1 Hyperbolic functions.input:971
  public void test0099() {
    check( //
        "Integrate[(a+b*Cosh[x])/(b^2+2*a*b*Cosh[x]+a^2*Cosh[x]^2), x]", //
        "Sinh[x]/(b+a*Cosh[x])");
  }

  // 6.2.3 (e x)^m (a+b cosh(c+d x^n))^p.input:28
  public void test0100() {
    check( //
        "Integrate[x*Cosh[a+b*x^2]^3, x]", //
        "1/2*Sinh[a+b*x^2]/b+1/6*Sinh[a+b*x^2]^3/b");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:75
  public void test0101() {
    check( //
        "Integrate[1/(1+Sinh[x]^2)^3, x]", //
        "Tanh[x]-2/3*Tanh[x]^3+1/5*Tanh[x]^5");
  }

  // 6.7.1 Hyperbolic functions.input:942
  public void test0102() {
    check( //
        "Integrate[1/(Cosh[x]^2-Sinh[x]^2), x]", //
        "x");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:139
  public void test0103() {
    check( //
        "Integrate[(a+a*Cosh[e+f*x])/(c+d*x), x]", //
        "a*CoshIntegral[c*f/d+f*x]*Cosh[e-c*f/d]/d+a*Log[c+d*x]/d+a*SinhIntegral[c*f/d+f*x]*Sinh[e-c*f/d]/d");
  }

  // 6.7.1 Hyperbolic functions.input:128
  public void test0104() {
    check( //
        "Integrate[Cosh[a+b*x]^3*Coth[a+b*x]^2, x]", //
        "-Csch[a+b*x]/b+2*Sinh[a+b*x]/b+1/3*Sinh[a+b*x]^3/b");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:202
  public void test0105() {
    check( //
        "Integrate[1/Sqrt[-1+Sech[x]^2], x]", //
        "Log[Sinh[x]]*Tanh[x]/Sqrt[-Tanh[x]^2]");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:64
  public void test0106() {
    check( //
        "Integrate[x^2*(a+b*x^2)^2*Cosh[c+d*x], x]", //
        "-720*b^2*x*Cosh[c+d*x]/d^6-48*a*b*x*Cosh[c+d*x]/d^4-2*a^2*x*Cosh[c+d*x]/d^2-120*b^2*x^3*Cosh[c+d*x]/d^4-8*a*b*x^3*Cosh[c+d*x]/d^2-6*b^2*x^5*Cosh[c+d*x]/d^2+720*b^2*Sinh[c+d*x]/d^7+48*a*b*Sinh[c+d*x]/d^5+2*a^2*Sinh[c+d*x]/d^3+360*b^2*x^2*Sinh[c+d*x]/d^5+24*a*b*x^2*Sinh[c+d*x]/d^3+a^2*x^2*Sinh[c+d*x]/d+30*b^2*x^4*Sinh[c+d*x]/d^3+2*a*b*x^4*Sinh[c+d*x]/d+b^2*x^6*Sinh[c+d*x]/d");
  }

  // 6.2.5 Hyperbolic cosine functions.input:104
  public void test0107() {
    check( //
        "Integrate[1/(5+3*Cosh[c+d*x]), x]", //
        "1/4*x-1/2*ArcTanh[Sinh[c+d*x]/(3+Cosh[c+d*x])]/d");
  }

  // 6.7.1 Hyperbolic functions.input:175
  public void test0108() {
    check( //
        "Integrate[Csch[c+b*x]^3*Sinh[a+b*x], x]", //
        "-Cosh[a-c]*Coth[c+b*x]/b-1/2*Csch[c+b*x]^2*Sinh[a-c]/b");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:463
  public void test0109() {
    check( //
        "Integrate[1/(a+b*Sinh[e+f*x]^2)^(5/2), x]", //
        "-1/3*b*Cosh[e+f*x]*Sinh[e+f*x]/(a*(a-b)*f*(a+b*Sinh[e+f*x]^2)^(3/2))-2/3*(2*a-b)*b*Cosh[e+f*x]*Sinh[e+f*x]/(a^2*(a-b)^2*f*Sqrt[a+b*Sinh[e+f*x]^2])-2/3*I*(2*a-b)*EllipticE[I*e+I*f*x,b/a]*Sqrt[a+b*Sinh[e+f*x]^2]/(a^2*(a-b)^2*f*Sqrt[1+b*Sinh[e+f*x]^2/a])+1/3*I*EllipticF[I*e+I*f*x,b/a]*Sqrt[1+b*Sinh[e+f*x]^2/a]/(a*(a-b)*f*Sqrt[a+b*Sinh[e+f*x]^2])");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:58
  public void test0110() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Cosh[a+b*x], x]", //
        "-5/2*d*(c+d*x)^(3/2)*Cosh[a+b*x]/b^2+(c+d*x)^(5/2)*Sinh[a+b*x]/b+15/16*E^(-a+b*c/d)*d^(5/2)*Erf[Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sqrt[Pi]/b^(7/2)-15/16*E^(a-b*c/d)*d^(5/2)*Erfi[Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sqrt[Pi]/b^(7/2)+15/4*d^2*Sinh[a+b*x]*Sqrt[c+d*x]/b^3");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:109
  public void test0111() {
    check( //
        "Integrate[(a+b*Coth[c+d*x])^4, x]", //
        "(a^4+6*a^2*b^2+b^4)*x-b^2*(3*a^2+b^2)*Coth[c+d*x]/d-a*b*(a+b*Coth[c+d*x])^2/d-1/3*b*(a+b*Coth[c+d*x])^3/d+4*a*b*(a^2+b^2)*Log[Sinh[c+d*x]]/d");
  }

  // 6.3.2 Hyperbolic tangent functions.input:137
  public void test0112() {
    check( //
        "Integrate[Cosh[x]^3/(1+Tanh[x]), x]", //
        "4/5*Sinh[x]+4/15*Sinh[x]^3-1/5*Cosh[x]^3/(1+Tanh[x])");
  }

  // 6.1.3 (e x)^m (a+b sinh(c+d x^n))^p.input:151
  public void test0113() {
    check( //
        "Integrate[Sinh[(a+b*x)^2], x]", //
        "-1/4*Erf[a+b*x]*Sqrt[Pi]/b+1/4*Erfi[a+b*x]*Sqrt[Pi]/b");
  }

  // 6.7.1 Hyperbolic functions.input:187
  public void test0114() {
    check( //
        "Integrate[Cosh[a+b*x]*Csch[c+b*x]^3, x]", //
        "-1/2*Cosh[a-c]*Csch[c+b*x]^2/b-Coth[c+b*x]*Sinh[a-c]/b");
  }

  // 6.7.1 Hyperbolic functions.input:170
  public void test0115() {
    check( //
        "Integrate[Sech[c+b*x]*Sinh[a+b*x], x]", //
        "Cosh[a-c]*Log[Cosh[c+b*x]]/b+x*Sinh[a-c]");
  }

  // 6.5.3 Hyperbolic secant functions.input:29
  public void test0116() {
    check( //
        "Integrate[(b*Sech[c+d*x])^(1/2), x]", //
        "-2*I*EllipticF[1/2*I*(c+d*x),2]*Sqrt[Cosh[c+d*x]]*Sqrt[b*Sech[c+d*x]]/d");
  }

  // 6.2.5 Hyperbolic cosine functions.input:159
  public void test0117() {
    check( //
        "Integrate[(A+B*Cosh[x])/(a+b*Cosh[x])^(1/2), x]", //
        "-2*I*B*EllipticE[1/2*I*x,2*b/(a+b)]*Sqrt[a+b*Cosh[x]]/(b*Sqrt[(a+b*Cosh[x])/(a+b)])-2*I*(A*b-a*B)*EllipticF[1/2*I*x,2*b/(a+b)]*Sqrt[(a+b*Cosh[x])/(a+b)]/(b*Sqrt[a+b*Cosh[x]])");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:152
  public void test0118() {
    check( //
        "Integrate[(c+d*x)/(a+a*Cosh[e+f*x]), x]", //
        "-2*d*Log[Cosh[1/2*e+1/2*f*x]]/(a*f^2)+(c+d*x)*Tanh[1/2*e+1/2*f*x]/(a*f)");
  }

  // 6.2.7 hyper^m (a+b cosh^n)^p.input:74
  public void test0119() {
    check( //
        "Integrate[1/(1-Cosh[x]^2)^2, x]", //
        "Coth[x]-1/3*Coth[x]^3");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:103
  public void test0120() {
    check( //
        "Integrate[Sech[c+d*x]^3/(a+b*Sech[c+d*x]^2)^2, x]", //
        "1/2*Sinh[c+d*x]/((a+b)*d*(a+b+a*Sinh[c+d*x]^2))+1/2*ArcTan[Sinh[c+d*x]*Sqrt[a]/Sqrt[a+b]]/((a+b)^(3/2)*d*Sqrt[a])");
  }

  // 6.1.5 Hyperbolic sine functions.input:380
  public void test0121() {
    check( //
        "Integrate[Sinh[a+2*Log[c*x^n]/n]^(5/2), x]", //
        "-1/4*x*Sinh[a+2*Log[c*x^n]/n]^(5/2)-5/4*x*Sinh[a+2*Log[c*x^n]/n]^(5/2)/(E^(2*a)*(c*x^n)^(4/n)*(1+(-1)/(E^(2*a)*(c*x^n)^(4/n)))^2)+5/12*x*Sinh[a+2*Log[c*x^n]/n]^(5/2)/(1+(-1)/(E^(2*a)*(c*x^n)^(4/n)))-5/4*x*ArcCsc[E^a*(c*x^n)^(2/n)]*Sinh[a+2*Log[c*x^n]/n]^(5/2)/(E^(3*a)*(c*x^n)^(6/n)*(1+(-1)/(E^(2*a)*(c*x^n)^(4/n)))^(5/2))");
  }

  // 6.2.5 Hyperbolic cosine functions.input:113
  public void test0122() {
    check( //
        "Integrate[1/(a+b*Cosh[x])^(5/2), x]", //
        "-2/3*b*Sinh[x]/((a^2-b^2)*(a+b*Cosh[x])^(3/2))-8/3*a*b*Sinh[x]/((a^2-b^2)^2*Sqrt[a+b*Cosh[x]])-8/3*I*a*EllipticE[1/2*I*x,2*b/(a+b)]*Sqrt[a+b*Cosh[x]]/((a^2-b^2)^2*Sqrt[(a+b*Cosh[x])/(a+b)])+2/3*I*EllipticF[1/2*I*x,2*b/(a+b)]*Sqrt[(a+b*Cosh[x])/(a+b)]/((a^2-b^2)*Sqrt[a+b*Cosh[x]])");
  }

  // 6.3.2 Hyperbolic tangent functions.input:199
  public void test0123() {
    check( //
        "Integrate[x*Sech[c+d*x]^2/(a+b*Tanh[c+d*x]^2), x]", //
        "1/2*x*Log[1+E^(2*c+2*d*x)*(a+b)/(a-b-2*Sqrt[-a]*Sqrt[b])]/(d*Sqrt[-a]*Sqrt[b])-1/2*x*Log[1+E^(2*c+2*d*x)*(a+b)/(a-b+2*Sqrt[-a]*Sqrt[b])]/(d*Sqrt[-a]*Sqrt[b])+1/4*PolyLog[2,-E^(2*c+2*d*x)*(a+b)/(a-b-2*Sqrt[-a]*Sqrt[b])]/(d^2*Sqrt[-a]*Sqrt[b])-1/4*PolyLog[2,-E^(2*c+2*d*x)*(a+b)/(a-b+2*Sqrt[-a]*Sqrt[b])]/(d^2*Sqrt[-a]*Sqrt[b])");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:30
  public void test0124() {
    check( //
        "Integrate[(c+d*x)*Cosh[a+b*x]^3, x]", //
        "-2/3*d*Cosh[a+b*x]/b^2-1/9*d*Cosh[a+b*x]^3/b^2+2/3*(c+d*x)*Sinh[a+b*x]/b+1/3*(c+d*x)*Cosh[a+b*x]^2*Sinh[a+b*x]/b");
  }

  // 6.7.1 Hyperbolic functions.input:408
  public void test0125() {
    check( //
        "Integrate[x^3*Sech[a+b*x]^2*Sinh[a+b*x]^2, x]", //
        "-x^3/b+1/4*x^4+3*x^2*Log[1+E^(2*(a+b*x))]/b^2+3*x*PolyLog[2,-E^(2*(a+b*x))]/b^3-3/2*PolyLog[3,-E^(2*(a+b*x))]/b^4-x^3*Tanh[a+b*x]/b");
  }

  // 6.7.1 Hyperbolic functions.input:152
  public void test0126() {
    check( //
        "Integrate[Sinh[a+b*x]*Sinh[c+b*x], x]", //
        "-1/2*x*Cosh[a-c]+1/4*Sinh[a+c+2*b*x]/b");
  }

  // 6.7.1 Hyperbolic functions.input:600
  public void test0127() {
    check( //
        "Integrate[x*Sinh[a+b*x]/Cosh[a+b*x]^(5/2), x]", //
        "4/3*I*EllipticE[1/2*I*(a+b*x),2]/b^2-2/3*x/(b*Cosh[a+b*x]^(3/2))+4/3*Sinh[a+b*x]/(b^2*Sqrt[Cosh[a+b*x]])");
  }

  // 6.4.7 (d hyper)^m (a+b (c coth)^n)^p.input:67
  public void test0128() {
    check( //
        "Integrate[Coth[x]^3/(a+b*Coth[x]^2)^(3/2), x]", //
        "ArcTanh[Sqrt[a+b*Coth[x]^2]/Sqrt[a+b]]/(a+b)^(3/2)+a/(b*(a+b)*Sqrt[a+b*Coth[x]^2])");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:62
  public void test0129() {
    check( //
        "Integrate[Cosh[c+d*x]^4*(a+b*Sech[c+d*x]^2), x]", //
        "1/8*(3*a+4*b)*x+1/8*(3*a+4*b)*Cosh[c+d*x]*Sinh[c+d*x]/d+1/4*a*Cosh[c+d*x]^3*Sinh[c+d*x]/d");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:277
  public void test0130() {
    check( //
        "Integrate[1/(a-b*Sinh[c+d*x]^4), x]", //
        "1/2*ArcTanh[Sqrt[Sqrt[a]-Sqrt[b]]*Tanh[c+d*x]/a^(1/4)]/(a^(3/4)*d*Sqrt[Sqrt[a]-Sqrt[b]])+1/2*ArcTanh[Sqrt[Sqrt[a]+Sqrt[b]]*Tanh[c+d*x]/a^(1/4)]/(a^(3/4)*d*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:13
  public void test0131() {
    check( //
        "Integrate[(c+d*x)^3*Cosh[a+b*x], x]", //
        "-6*d^3*Cosh[a+b*x]/b^4-3*d*(c+d*x)^2*Cosh[a+b*x]/b^2+6*d^2*(c+d*x)*Sinh[a+b*x]/b^3+(c+d*x)^3*Sinh[a+b*x]/b");
  }

  // 6.7.1 Hyperbolic functions.input:199
  public void test0132() {
    check( //
        "Integrate[Cosh[a+b*x]^2*Cosh[c+d*x]^2, x]", //
        "1/4*x+1/8*Sinh[2*a+2*b*x]/b+1/16*Sinh[2*(a-c)+2*(b-d)*x]/(b-d)+1/8*Sinh[2*c+2*d*x]/d+1/16*Sinh[2*(a+c)+2*(b+d)*x]/(b+d)");
  }

  // 6.5.3 Hyperbolic secant functions.input:234
  public void test0133() {
    check( //
        "Integrate[x^6/Sech[2*Log[c*x]]^(3/2), x]", //
        "1/10*(c^4+1/x^4)*x^7/(c^4*Sech[2*Log[c*x]]^(3/2))");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:190
  public void test0134() {
    check( //
        "Integrate[x^2/(a+a*Cosh[x])^(3/2), x]", //
        "2*x/(a*Sqrt[a+a*Cosh[x]])+x^2*ArcTan[E^(1/2*x)]*Cosh[1/2*x]/(a*Sqrt[a+a*Cosh[x]])-4*ArcTan[Sinh[1/2*x]]*Cosh[1/2*x]/(a*Sqrt[a+a*Cosh[x]])-2*I*x*Cosh[1/2*x]*PolyLog[2,-I*E^(1/2*x)]/(a*Sqrt[a+a*Cosh[x]])+2*I*x*Cosh[1/2*x]*PolyLog[2,I*E^(1/2*x)]/(a*Sqrt[a+a*Cosh[x]])+4*I*Cosh[1/2*x]*PolyLog[3,-I*E^(1/2*x)]/(a*Sqrt[a+a*Cosh[x]])-4*I*Cosh[1/2*x]*PolyLog[3,I*E^(1/2*x)]/(a*Sqrt[a+a*Cosh[x]])+1/2*x^2*Tanh[1/2*x]/(a*Sqrt[a+a*Cosh[x]])");
  }

  // 6.2.3 (e x)^m (a+b cosh(c+d x^n))^p.input:63
  public void test0135() {
    check( //
        "Integrate[Cosh[a+b*x^n]^3/x, x]", //
        "3/4*CoshIntegral[b*x^n]*Cosh[a]/n+1/4*CoshIntegral[3*b*x^n]*Cosh[3*a]/n+3/4*SinhIntegral[b*x^n]*Sinh[a]/n+1/4*SinhIntegral[3*b*x^n]*Sinh[3*a]/n");
  }

  // 6.4.2 Hyperbolic cotangent functions.input:102
  public void test0136() {
    check( //
        "Integrate[(1+Coth[x])^(1/2), x]", //
        "ArcTanh[Sqrt[1+Coth[x]]/Sqrt[2]]*Sqrt[2]");
  }

  // 6.7.1 Hyperbolic functions.input:877
  public void test0137() {
    check( //
        "Integrate[b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2], x]", //
        "c*Cosh[x]+b*Sinh[x]+x*Sqrt[b^2-c^2]");
  }

  // 6.4.7 (d hyper)^m (a+b (c coth)^n)^p.input:56
  public void test0138() {
    check( //
        "Integrate[Sqrt[-1-Coth[x]^2], x]", //
        "ArcTan[Coth[x]/Sqrt[-1-Coth[x]^2]]-ArcTan[Coth[x]*Sqrt[2]/Sqrt[-1-Coth[x]^2]]*Sqrt[2]");
  }

  // 6.7.1 Hyperbolic functions.input:839
  public void test0139() {
    check( //
        "Integrate[(A+C*Sinh[x])/(b*Cosh[x]+c*Sinh[x]), x]", //
        "-c*C*x/(b^2-c^2)+b*C*Log[b*Cosh[x]+c*Sinh[x]]/(b^2-c^2)+A*ArcTan[(c*Cosh[x]+b*Sinh[x])/Sqrt[b^2-c^2]]/Sqrt[b^2-c^2]");
  }

  // 6.3.2 Hyperbolic tangent functions.input:62
  public void test0140() {
    check( //
        "Integrate[1/Sqrt[a*Tanh[x]^4], x]", //
        "-Tanh[x]/Sqrt[a*Tanh[x]^4]+x*Tanh[x]^2/Sqrt[a*Tanh[x]^4]");
  }

  // 6.7.1 Hyperbolic functions.input:768
  public void test0141() {
    check( //
        "Integrate[(Csch[x]+Sinh[x])^(5/2), x]", //
        "-16/15*Coth[x]*Sqrt[Cosh[x]*Coth[x]]+2/5*Cosh[x]^2*Coth[x]*Sqrt[Cosh[x]*Coth[x]]+64/15*Sqrt[Cosh[x]*Coth[x]]*Tanh[x]");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:171
  public void test0142() {
    check( //
        "Integrate[x^2*Sqrt[a+a*Cosh[x]], x]", //
        "-8*x*Sqrt[a+a*Cosh[x]]+16*Sqrt[a+a*Cosh[x]]*Tanh[1/2*x]+2*x^2*Sqrt[a+a*Cosh[x]]*Tanh[1/2*x]");
  }

  // 6.2.5 Hyperbolic cosine functions.input:301
  public void test0143() {
    check( //
        "Integrate[x*Sinh[c+d*x]/(a+b*Cosh[c+d*x]), x]", //
        "-1/2*x^2/b+x*Log[1+E^(c+d*x)*b/(a-Sqrt[a^2-b^2])]/(b*d)+x*Log[1+E^(c+d*x)*b/(a+Sqrt[a^2-b^2])]/(b*d)+PolyLog[2,-E^(c+d*x)*b/(a-Sqrt[a^2-b^2])]/(b*d^2)+PolyLog[2,-E^(c+d*x)*b/(a+Sqrt[a^2-b^2])]/(b*d^2)");
  }

  // 6.7.1 Hyperbolic functions.input:172
  public void test0144() {
    check( //
        "Integrate[Sech[c+b*x]^3*Sinh[a+b*x], x]", //
        "-1/2*Cosh[a-c]*Sech[c+b*x]^2/b+Sinh[a-c]*Tanh[c+b*x]/b");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:67
  public void test0145() {
    check( //
        "Integrate[(a+b*x^2)^2*Cosh[c+d*x]/x, x]", //
        "a^2*CoshIntegral[d*x]*Cosh[c]-6*b^2*Cosh[c+d*x]/d^4-2*a*b*Cosh[c+d*x]/d^2-3*b^2*x^2*Cosh[c+d*x]/d^2+a^2*SinhIntegral[d*x]*Sinh[c]+6*b^2*x*Sinh[c+d*x]/d^3+2*a*b*x*Sinh[c+d*x]/d+b^2*x^3*Sinh[c+d*x]/d");
  }

  // 6.7.1 Hyperbolic functions.input:161
  public void test0146() {
    check( //
        "Integrate[Sech[c-b*x]*Sech[a+b*x], x]", //
        "-Csch[a+c]*Log[Cosh[c-b*x]]/b+Csch[a+c]*Log[Cosh[a+b*x]]/b");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:392
  public void test0147() {
    check( //
        "Integrate[(e+f*x)*Cosh[c+d*x]/(a+b*Sinh[c+d*x]), x]", //
        "-1/2*(e+f*x)^2/(b*f)+(e+f*x)*Log[1+E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b*d)+(e+f*x)*Log[1+E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b*d)+f*PolyLog[2,-E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b*d^2)+f*PolyLog[2,-E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b*d^2)");
  }

  // 6.1.3 (e x)^m (a+b sinh(c+d x^n))^p.input:41
  public void test0148() {
    check( //
        "Integrate[(e*x)^m*Sinh[a+b*x^2], x]", //
        "-1/4*E^a*(e*x)^(1+m)*(-b*x^2)^(1/2*(-1-m))*Gamma[1/2*(1+m),-b*x^2]/e+1/4*(e*x)^(1+m)*(b*x^2)^(1/2*(-1-m))*Gamma[1/2*(1+m),b*x^2]/(E^a*e)");
  }

  // 6.2.2 (e x)^m (a+b x^n)^p cosh.input:17
  public void test0149() {
    check( //
        "Integrate[(a+b*x)*Cosh[c+d*x]/x^4, x]", //
        "1/2*b*d^2*CoshIntegral[d*x]*Cosh[c]-1/3*a*Cosh[c+d*x]/x^3-1/2*b*Cosh[c+d*x]/x^2-1/6*a*d^2*Cosh[c+d*x]/x+1/6*a*d^3*Cosh[c]*SinhIntegral[d*x]+1/6*a*d^3*CoshIntegral[d*x]*Sinh[c]+1/2*b*d^2*SinhIntegral[d*x]*Sinh[c]-1/6*a*d*Sinh[c+d*x]/x^2-1/2*b*d*Sinh[c+d*x]/x");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:88
  public void test0150() {
    check( //
        "Integrate[Sqrt[3-3*I*Csch[x]], x]", //
        "2*ArcTanh[Coth[x]/Sqrt[1-I*Csch[x]]]*Sqrt[3]");
  }

  // 6.7.1 Hyperbolic functions.input:610
  public void test0151() {
    check( //
        "Integrate[x*Sinh[a+b*x]/Sech[a+b*x]^(5/2), x]", //
        "2/7*x/(b*Sech[a+b*x]^(7/2))-4/49*Sinh[a+b*x]/(b^2*Sech[a+b*x]^(5/2))-20/147*Sinh[a+b*x]/(b^2*Sqrt[Sech[a+b*x]])+20/147*I*EllipticF[1/2*I*(a+b*x),2]*Sqrt[Cosh[a+b*x]]*Sqrt[Sech[a+b*x]]/b^2");
  }

  // 6.7.1 Hyperbolic functions.input:288
  public void test0152() {
    check( //
        "Integrate[x*Cosh[a+b*x]*Sinh[a+b*x], x]", //
        "1/4*x/b-1/4*Cosh[a+b*x]*Sinh[a+b*x]/b^2+1/2*x*Sinh[a+b*x]^2/b");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:365
  public void test0153() {
    check( //
        "Integrate[Sech[c+d*x]*(a+b*Sinh[c+d*x]^2)^3, x]", //
        "(a-b)^3*ArcTan[Sinh[c+d*x]]/d+b*(3*a^2-3*a*b+b^2)*Sinh[c+d*x]/d+1/3*(3*a-b)*b^2*Sinh[c+d*x]^3/d+1/5*b^3*Sinh[c+d*x]^5/d");
  }

  // 6.7.1 Hyperbolic functions.input:876
  public void test0154() {
    check( //
        "Integrate[(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])^2, x]", //
        "3/2*(b^2-c^2)*x+3/2*c*Cosh[x]*Sqrt[b^2-c^2]+3/2*b*Sinh[x]*Sqrt[b^2-c^2]+1/2*(c*Cosh[x]+b*Sinh[x])*(b*Cosh[x]+c*Sinh[x]+Sqrt[b^2-c^2])");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:103
  public void test0155() {
    check( //
        "Integrate[(c+d*x)^m*Cosh[a+b*x]^3, x]", //
        "1/8*3^(-1-m)*E^(3*a-3*b*c/d)*(c+d*x)^m*Gamma[1+m,-3*b*(c+d*x)/d]/(b*(-b*(c+d*x)/d)^m)+3/8*E^(a-b*c/d)*(c+d*x)^m*Gamma[1+m,-b*(c+d*x)/d]/(b*(-b*(c+d*x)/d)^m)-3/8*E^(-a+b*c/d)*(c+d*x)^m*Gamma[1+m,b*(c+d*x)/d]/(b*(b*(c+d*x)/d)^m)-1/8*3^(-1-m)*E^(-3*a+3*b*c/d)*(c+d*x)^m*Gamma[1+m,3*b*(c+d*x)/d]/(b*(b*(c+d*x)/d)^m)");
  }

  // 6.5.3 Hyperbolic secant functions.input:155
  public void test0156() {
    check( //
        "Integrate[Coth[x]^4/(a+a*Sech[x]), x]", //
        "x/a-1/15*Coth[x]*(15-8*Sech[x])/a-1/15*Coth[x]^3*(5-4*Sech[x])/a-1/5*Coth[x]^5*(1-Sech[x])/a");
  }

  // 6.7.1 Hyperbolic functions.input:159
  public void test0157() {
    check( //
        "Integrate[Coth[c-b*x]*Coth[a+b*x], x]", //
        "-x-Coth[a+c]*Log[Sinh[c-b*x]]/b+Coth[a+c]*Log[Sinh[a+b*x]]/b");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:66
  public void test0158() {
    check( //
        "Integrate[(a*Csch[x]^4)^(1/2), x]", //
        "-Cosh[x]*Sinh[x]*Sqrt[a*Csch[x]^4]");
  }

  // 6.1.5 Hyperbolic sine functions.input:200
  public void test0159() {
    check( //
        "Integrate[1/(a*Sinh[x]^3)^(5/2), x]", //
        "-154/585*Coth[x]/(a^2*Sqrt[a*Sinh[x]^3])+22/117*Coth[x]*Csch[x]^2/(a^2*Sqrt[a*Sinh[x]^3])-2/13*Coth[x]*Csch[x]^4/(a^2*Sqrt[a*Sinh[x]^3])+154/195*Cosh[x]*Sinh[x]/(a^2*Sqrt[a*Sinh[x]^3])-154/195*I*EllipticE[1/4*Pi-1/2*I*x,2]*Sinh[x]^2/(a^2*Sqrt[I*Sinh[x]]*Sqrt[a*Sinh[x]^3])");
  }

  // 6.2.5 Hyperbolic cosine functions.input:167
  public void test0160() {
    check( //
        "Integrate[(a*Cosh[x]^2)^(5/2), x]", //
        "4/15*a*(a*Cosh[x]^2)^(3/2)*Tanh[x]+1/5*(a*Cosh[x]^2)^(5/2)*Tanh[x]+8/15*a^2*Sqrt[a*Cosh[x]^2]*Tanh[x]");
  }

  // 6.5.3 Hyperbolic secant functions.input:267
  public void test0161() {
    check( //
        "Integrate[1/(x*Sech[a+b*Log[c*x^n]]^(3/2)), x]", //
        "2/3*Sinh[a+b*Log[c*x^n]]/(b*n*Sqrt[Sech[a+b*Log[c*x^n]]])-2/3*I*EllipticF[1/2*I*(a+b*Log[c*x^n]),2]*Sqrt[Cosh[a+b*Log[c*x^n]]]*Sqrt[Sech[a+b*Log[c*x^n]]]/(b*n)");
  }

  // 6.3.2 Hyperbolic tangent functions.input:60
  public void test0162() {
    check( //
        "Integrate[(a*Tanh[x]^4)^(3/2), x]", //
        "-a*Coth[x]*Sqrt[a*Tanh[x]^4]+a*x*Coth[x]^2*Sqrt[a*Tanh[x]^4]-1/3*a*Sqrt[a*Tanh[x]^4]*Tanh[x]-1/5*a*Sqrt[a*Tanh[x]^4]*Tanh[x]^3");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:101
  public void test0163() {
    check( //
        "Integrate[(c+d*x)^m*Sinh[a+b*x]^2, x]", //
        "-1/2*(c+d*x)^(1+m)/(d*(1+m))+2^(-3-m)*E^(2*a-2*b*c/d)*(c+d*x)^m*Gamma[1+m,-2*b*(c+d*x)/d]/(b*(-b*(c+d*x)/d)^m)-2^(-3-m)*E^(-2*a+2*b*c/d)*(c+d*x)^m*Gamma[1+m,2*b*(c+d*x)/d]/(b*(b*(c+d*x)/d)^m)");
  }

  // 6.5.3 Hyperbolic secant functions.input:242
  public void test0164() {
    check( //
        "Integrate[Sech[2*Log[c*x]]^(3/2)/x^2, x]", //
        "1/2*(c^4+1/x^4)*x^3*Sech[2*Log[c*x]]^(3/2)");
  }

  // 6.5.2 (e x)^m (a+b sech(c+d x^n))^p.input:16
  public void test0165() {
    check( //
        "Integrate[x*(a+b*Sech[c+d*x^2]), x]", //
        "1/2*a*x^2+1/2*b*ArcTan[Sinh[c+d*x^2]]/d");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:311
  public void test0166() {
    check( //
        "Integrate[1/(a+b*Sinh[x]^6), x]", //
        "1/3*ArcTanh[Sqrt[a^(1/3)-b^(1/3)]*Tanh[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)-b^(1/3)])+1/3*ArcTanh[Sqrt[a^(1/3)+(-1)^(1/3)*b^(1/3)]*Tanh[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)+(-1)^(1/3)*b^(1/3)])+1/3*ArcTanh[Sqrt[a^(1/3)-(-1)^(2/3)*b^(1/3)]*Tanh[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)-(-1)^(2/3)*b^(1/3)])");
  }

  // 6.5.3 Hyperbolic secant functions.input:96
  public void test0167() {
    check( //
        "Integrate[Csch[x]^2/(a+b*Sech[x]), x]", //
        "2*a*b*ArcTan[Sqrt[a-b]*Tanh[1/2*x]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2))+(b-a*Cosh[x])*Csch[x]/(a^2-b^2)");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:505
  public void test0168() {
    check( //
        "Integrate[(e+f*x)*Cosh[c+d*x]^3*Sinh[c+d*x]^2/(a+b*Sinh[c+d*x]), x]", //
        "1/4*a^2*f*x/(b^3*d)-3/32*f*x/(b*d)-1/2*a^2*(a^2+b^2)*(e+f*x)^2/(b^5*f)+a^3*f*Cosh[c+d*x]/(b^4*d^2)+2/3*a*f*Cosh[c+d*x]/(b^2*d^2)+1/9*a*f*Cosh[c+d*x]^3/(b^2*d^2)+1/4*(e+f*x)*Cosh[c+d*x]^4/(b*d)+a^2*(a^2+b^2)*(e+f*x)*Log[1+E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b^5*d)+a^2*(a^2+b^2)*(e+f*x)*Log[1+E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b^5*d)+a^2*(a^2+b^2)*f*PolyLog[2,-E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b^5*d^2)+a^2*(a^2+b^2)*f*PolyLog[2,-E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b^5*d^2)-a^3*(e+f*x)*Sinh[c+d*x]/(b^4*d)-2/3*a*(e+f*x)*Sinh[c+d*x]/(b^2*d)-1/4*a^2*f*Cosh[c+d*x]*Sinh[c+d*x]/(b^3*d^2)-3/32*f*Cosh[c+d*x]*Sinh[c+d*x]/(b*d^2)-1/3*a*(e+f*x)*Cosh[c+d*x]^2*Sinh[c+d*x]/(b^2*d)-1/16*f*Cosh[c+d*x]^3*Sinh[c+d*x]/(b*d^2)+1/2*a^2*(e+f*x)*Sinh[c+d*x]^2/(b^3*d)");
  }

  // 6.1.5 Hyperbolic sine functions.input:454
  public void test0169() {
    check( //
        "Integrate[f^(a+b*x)*Sinh[d+e*x+f*x^2], x]", //
        "-1/4*E^(-d+1/4*(e-b*Log[f])^2/f)*f^(-1/2+a)*Erf[1/2*(e+2*f*x-b*Log[f])/Sqrt[f]]*Sqrt[Pi]+1/4*E^(d-1/4*(e+b*Log[f])^2/f)*f^(-1/2+a)*Erfi[1/2*(e+2*f*x+b*Log[f])/Sqrt[f]]*Sqrt[Pi]");
  }

  // 6.4.7 (d hyper)^m (a+b (c coth)^n)^p.input:37
  public void test0170() {
    check( //
        "Integrate[1/Sqrt[1-Coth[x]^2], x]", //
        "Coth[x]/Sqrt[-Csch[x]^2]");
  }

  // 6.7.1 Hyperbolic functions.input:947
  public void test0171() {
    check( //
        "Integrate[1/(Sech[x]^2+Tanh[x]^2)^3, x]", //
        "x");
  }

  // 6.7.1 Hyperbolic functions.input:45
  public void test0172() {
    check( //
        "Integrate[Csch[a+b*x]^2*Sech[a+b*x]^2, x]", //
        "-Coth[a+b*x]/b-Tanh[a+b*x]/b");
  }

  // 6.4.1 (c+d x)^m (a+b coth)^n.input:81
  public void test0173() {
    check( //
        "Integrate[(c+d*x)^3/(a+b*Coth[e+f*x]), x]", //
        "1/4*(c+d*x)^4/((a+b)*d)-b*(c+d*x)^3*Log[1+(-a+b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)*f)+3/2*b*d*(c+d*x)^2*PolyLog[2,(a-b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)*f^2)+3/2*b*d^2*(c+d*x)*PolyLog[3,(a-b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)*f^3)+3/4*b*d^3*PolyLog[4,(a-b)/(E^(2*(e+f*x))*(a+b))]/((a^2-b^2)*f^4)");
  }

  // 6.2.7 hyper^m (a+b cosh^n)^p.input:113
  public void test0174() {
    check( //
        "Integrate[1/(a+b*Cosh[x]^8), x]", //
        "-1/4*ArcTanh[(-a)^(1/8)*Tanh[x]/Sqrt[(-a)^(1/4)-b^(1/4)]]/((-a)^(7/8)*Sqrt[(-a)^(1/4)-b^(1/4)])-1/4*ArcTanh[(-a)^(1/8)*Tanh[x]/Sqrt[(-a)^(1/4)-I*b^(1/4)]]/((-a)^(7/8)*Sqrt[(-a)^(1/4)-I*b^(1/4)])-1/4*ArcTanh[(-a)^(1/8)*Tanh[x]/Sqrt[(-a)^(1/4)+I*b^(1/4)]]/((-a)^(7/8)*Sqrt[(-a)^(1/4)+I*b^(1/4)])-1/4*ArcTanh[(-a)^(1/8)*Tanh[x]/Sqrt[(-a)^(1/4)+b^(1/4)]]/((-a)^(7/8)*Sqrt[(-a)^(1/4)+b^(1/4)])");
  }

  // 6.7.1 Hyperbolic functions.input:943
  public void test0175() {
    check( //
        "Integrate[1/(Cosh[x]^2-Sinh[x]^2)^2, x]", //
        "x");
  }

  // 6.5.3 Hyperbolic secant functions.input:150
  public void test0176() {
    check( //
        "Integrate[Tanh[x]^2/(a+a*Sech[x]), x]", //
        "x/a-ArcTan[Sinh[x]]/a");
  }

  // 6.3.7 (d hyper)^m (a+b (c tanh)^n)^p.input:87
  public void test0177() {
    check( //
        "Integrate[Csch[c+d*x]^4*(a+b*Tanh[c+d*x]^3)^3, x]", //
        "a^3*Coth[c+d*x]/d-1/3*a^3*Coth[c+d*x]^3/d+3*a^2*b*Log[Tanh[c+d*x]]/d-3/2*a^2*b*Tanh[c+d*x]^2/d+a*b^2*Tanh[c+d*x]^3/d-3/5*a*b^2*Tanh[c+d*x]^5/d+1/6*b^3*Tanh[c+d*x]^6/d-1/8*b^3*Tanh[c+d*x]^8/d");
  }

  // 6.3.7 (d hyper)^m (a+b (c tanh)^n)^p.input:141
  public void test0178() {
    check( //
        "Integrate[Sech[c+d*x]^4/(a+b*Tanh[c+d*x]^2), x]", //
        "(a+b)*ArcTan[Sqrt[b]*Tanh[c+d*x]/Sqrt[a]]/(b^(3/2)*d*Sqrt[a])-Tanh[c+d*x]/(b*d)");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:63
  public void test0179() {
    check( //
        "Integrate[Cosh[a+b*x]/(c+d*x)^(5/2), x]", //
        "-2/3*Cosh[a+b*x]/(d*(c+d*x)^(3/2))+2/3*E^(-a+b*c/d)*b^(3/2)*Erf[Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sqrt[Pi]/d^(5/2)+2/3*E^(a-b*c/d)*b^(3/2)*Erfi[Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sqrt[Pi]/d^(5/2)-4/3*b*Sinh[a+b*x]/(d^2*Sqrt[c+d*x])");
  }

  // 6.2.5 Hyperbolic cosine functions.input:343
  public void test0180() {
    check( //
        "Integrate[1/Cosh[a+2*Log[c*x^n]/n]^(3/2), x]", //
        "-1/2*x*(1+1/(E^(2*a)*(c*x^n)^(4/n)))/Cosh[a+2*Log[c*x^n]/n]^(3/2)");
  }

  // 6.1.5 Hyperbolic sine functions.input:203
  public void test0181() {
    check( //
        "Integrate[(a*Sinh[x]^4)^(5/2), x]", //
        "63/256*a^2*Coth[x]*Sqrt[a*Sinh[x]^4]-63/256*a^2*x*Csch[x]^2*Sqrt[a*Sinh[x]^4]-21/128*a^2*Cosh[x]*Sinh[x]*Sqrt[a*Sinh[x]^4]+21/160*a^2*Cosh[x]*Sinh[x]^3*Sqrt[a*Sinh[x]^4]-9/80*a^2*Cosh[x]*Sinh[x]^5*Sqrt[a*Sinh[x]^4]+1/10*a^2*Cosh[x]*Sinh[x]^7*Sqrt[a*Sinh[x]^4]");
  }

  // 6.7.1 Hyperbolic functions.input:107
  public void test0182() {
    check( //
        "Integrate[Sech[a+b*x]^4*Tanh[a+b*x]^2, x]", //
        "1/3*Tanh[a+b*x]^3/b-1/5*Tanh[a+b*x]^5/b");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:627
  public void test0183() {
    check( //
        "Integrate[Coth[x]/Sqrt[a+b*Sinh[x]^n], x]", //
        "-2*ArcTanh[Sqrt[a+b*Sinh[x]^n]/Sqrt[a]]/(n*Sqrt[a])");
  }

  // 6.7.1 Hyperbolic functions.input:640
  public void test0184() {
    check( //
        "Integrate[(b+c+Cosh[x])/(a-b*Sinh[x]), x]", //
        "-Log[a-b*Sinh[x]]/b+2*(b+c)*ArcTanh[(b+a*Tanh[1/2*x])/Sqrt[a^2+b^2]]/Sqrt[a^2+b^2]");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:434
  public void test0185() {
    check( //
        "Integrate[(e+f*x)^2*Cosh[c+d*x]/(a+b*Sinh[c+d*x])^2, x]", //
        "-(e+f*x)^2/(b*d*(a+b*Sinh[c+d*x]))+2*f*(e+f*x)*Log[1+E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b*d^2*Sqrt[a^2+b^2])-2*f*(e+f*x)*Log[1+E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b*d^2*Sqrt[a^2+b^2])+2*f^2*PolyLog[2,-E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/(b*d^3*Sqrt[a^2+b^2])-2*f^2*PolyLog[2,-E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/(b*d^3*Sqrt[a^2+b^2])");
  }

  // 6.7.1 Hyperbolic functions.input:330
  public void test0186() {
    check( //
        "Integrate[Cosh[a+b*x]^2*Sinh[a+b*x]^2, x]", //
        "-1/8*x-1/8*Cosh[a+b*x]*Sinh[a+b*x]/b+1/4*Cosh[a+b*x]^3*Sinh[a+b*x]/b");
  }

  // 6.2.7 hyper^m (a+b cosh^n)^p.input:70
  public void test0187() {
    check( //
        "Integrate[1/(1+Cosh[x]^2), x]", //
        "ArcTanh[Tanh[x]/Sqrt[2]]/Sqrt[2]");
  }

  // 6.5.3 Hyperbolic secant functions.input:22
  public void test0188() {
    check( //
        "Integrate[Sech[a+b*x]^(1/2), x]", //
        "-2*I*EllipticF[1/2*I*(a+b*x),2]*Sqrt[Cosh[a+b*x]]*Sqrt[Sech[a+b*x]]/b");
  }

  // 6.1.5 Hyperbolic sine functions.input:75
  public void test0189() {
    check( //
        "Integrate[Sinh[x]^2/(I+Sinh[x])^2, x]", //
        "x+1/3*I*Cosh[x]/(I+Sinh[x])^2-5/3*Cosh[x]/(I+Sinh[x])");
  }

  // 6.5.2 (e x)^m (a+b sech(c+d x^n))^p.input:48
  public void test0190() {
    check( //
        "Integrate[Sech[1/x]^2/x^2, x]", //
        "-Tanh[1/x]");
  }

  // 6.2.1 (c+d x)^m (a+b cosh)^n.input:180
  public void test0191() {
    check( //
        "Integrate[(a+a*Cosh[x])^(3/2)/x^2, x]", //
        "-2*a*Cosh[1/2*x]^2*Sqrt[a+a*Cosh[x]]/x+3/4*a*Sech[1/2*x]*SinhIntegral[1/2*x]*Sqrt[a+a*Cosh[x]]+3/4*a*Sech[1/2*x]*SinhIntegral[3/2*x]*Sqrt[a+a*Cosh[x]]");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:204
  public void test0192() {
    check( //
        "Integrate[Csch[2*Log[c*x]]^(3/2)/x^2, x]", //
        "-1/2*(c^4+(-1)/x^4)*x^3*Csch[2*Log[c*x]]^(3/2)");
  }

  // 6.4.1 (c+d x)^m (a+b coth)^n.input:17
  public void test0193() {
    check( //
        "Integrate[x*Coth[a+b*x]^2, x]", //
        "1/2*x^2-x*Coth[a+b*x]/b+Log[Sinh[a+b*x]]/b^2");
  }

  // 6.3.7 (d hyper)^m (a+b (c tanh)^n)^p.input:258
  public void test0194() {
    check( //
        "Integrate[1/Sqrt[-1+Tanh[x]^2], x]", //
        "Tanh[x]/Sqrt[-Sech[x]^2]");
  }

  // 6.2.5 Hyperbolic cosine functions.input:24
  public void test0195() {
    check( //
        "Integrate[1/Cosh[a+b*x]^(5/2), x]", //
        "-2/3*I*EllipticF[1/2*I*(a+b*x),2]/b+2/3*Sinh[a+b*x]/(b*Cosh[a+b*x]^(3/2))");
  }

  // 6.3.2 Hyperbolic tangent functions.input:207
  public void test0196() {
    check( //
        "Integrate[x*Tanh[a+2*Log[x]], x]", //
        "1/2*x^2-ArcTan[E^a*x^2]/E^a");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:348
  public void test0197() {
    check( //
        "Integrate[(e+f*x)*Cosh[c+d*x]/(a+I*a*Sinh[c+d*x]), x]", //
        "1/2*I*(e+f*x)^2/(a*f)-2*I*(e+f*x)*Log[1+I*E^(c+d*x)]/(a*d)-2*I*f*PolyLog[2,-I*E^(c+d*x)]/(a*d^2)");
  }

  // 6.6.1 (c+d x)^m (a+b csch)^n.input:55
  public void test0198() {
    check( //
        "Integrate[(e+f*x)*Cosh[c+d*x]^3/(a+b*Csch[c+d*x]), x]", //
        "-1/4*b*f*x/(a^2*d)+1/2*b*(a^2+b^2)*(e+f*x)^2/(a^4*f)-2/3*f*Cosh[c+d*x]/(a*d^2)-b^2*f*Cosh[c+d*x]/(a^3*d^2)-1/9*f*Cosh[c+d*x]^3/(a*d^2)-b*(a^2+b^2)*(e+f*x)*Log[1+E^(c+d*x)*a/(b-Sqrt[a^2+b^2])]/(a^4*d)-b*(a^2+b^2)*(e+f*x)*Log[1+E^(c+d*x)*a/(b+Sqrt[a^2+b^2])]/(a^4*d)-b*(a^2+b^2)*f*PolyLog[2,-E^(c+d*x)*a/(b-Sqrt[a^2+b^2])]/(a^4*d^2)-b*(a^2+b^2)*f*PolyLog[2,-E^(c+d*x)*a/(b+Sqrt[a^2+b^2])]/(a^4*d^2)+2/3*(e+f*x)*Sinh[c+d*x]/(a*d)+b^2*(e+f*x)*Sinh[c+d*x]/(a^3*d)+1/4*b*f*Cosh[c+d*x]*Sinh[c+d*x]/(a^2*d^2)+1/3*(e+f*x)*Cosh[c+d*x]^2*Sinh[c+d*x]/(a*d)-1/2*b*(e+f*x)*Sinh[c+d*x]^2/(a^2*d)");
  }

  // 6.6.3 Hyperbolic cosecant functions.input:93
  public void test0199() {
    check( //
        "Integrate[Sinh[x]^2/(I+Csch[x]), x]", //
        "3/2*I*x+2*Cosh[x]-3/2*I*Cosh[x]*Sinh[x]-Cosh[x]*Sinh[x]/(I+Csch[x])");
  }

  // 6.1.1 (c+d x)^m (a+b sinh)^n.input:112
  public void test0200() {
    check( //
        "Integrate[x^(3+m)*Sinh[a+b*x]^2, x]", //
        "-1/2*x^(4+m)/(4+m)-2^(-6-m)*E^(2*a)*x^m*Gamma[4+m,-2*b*x]/(b^4*(-b*x)^m)-2^(-6-m)*x^m*Gamma[4+m,2*b*x]/(E^(2*a)*b^4*(b*x)^m)");
  }

  // 6.1.7 hyper^m (a+b sinh^n)^p.input:403
  public void test0201() {
    check( //
        "Integrate[Cosh[c+d*x]/(a+b*Sinh[c+d*x]^2)^3, x]", //
        "1/4*Sinh[c+d*x]/(a*d*(a+b*Sinh[c+d*x]^2)^2)+3/8*Sinh[c+d*x]/(a^2*d*(a+b*Sinh[c+d*x]^2))+3/8*ArcTan[Sinh[c+d*x]*Sqrt[b]/Sqrt[a]]/(a^(5/2)*d*Sqrt[b])");
  }

  // 6.7.1 Hyperbolic functions.input:791
  public void test0202() {
    check( //
        "Integrate[Cosh[x]^2/(a*Cosh[x]+b*Sinh[x]), x]", //
        "-b^2*ArcTan[(b*Cosh[x]+a*Sinh[x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(3/2)-b*Cosh[x]/(a^2-b^2)+a*Sinh[x]/(a^2-b^2)");
  }

  // 6.5.7 (d hyper)^m (a+b (c sech)^n)^p.input:68
  public void test0203() {
    check( //
        "Integrate[Sech[c+d*x]^3*(a+b*Sech[c+d*x]^2), x]", //
        "1/8*(4*a+3*b)*ArcTan[Sinh[c+d*x]]/d+1/8*(4*a+3*b)*Sech[c+d*x]*Tanh[c+d*x]/d+1/4*b*Sech[c+d*x]^3*Tanh[c+d*x]/d");
  }
}
