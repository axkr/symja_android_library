package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 7 Inverse hyperbolic functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class InverseHyperbolicFunctions extends AbstractRubiTestCase {
  static boolean init = true;

  public InverseHyperbolicFunctions(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("InverseHyperbolicFunctions");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 7.5.1 u (a+b arcsech(c x))^n.input:129
  public void test0001() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*ArcSech[c*x]), x]", //
        "d*x*(a+b*ArcSech[c*x])+1/3*e*x^3*(a+b*ArcSech[c*x])+1/6*b*(6*c^2*d+e)*ArcSin[c*x]*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]/c^3-1/6*b*e*x*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]*Sqrt[1-c^2*x^2]/c^2");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:222
  public void test0002() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^3/Sqrt[x], x]", //
        "-32/35*b^3*x^(7/2)+16/5*b^2*x^(5/2)*ArcTanh[Tanh[a+b*x]]-4*b*x^(3/2)*ArcTanh[Tanh[a+b*x]]^2+2*ArcTanh[Tanh[a+b*x]]^3*Sqrt[x]");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:398
  public void test0003() {
    check( //
        "Integrate[E^ArcCosh[a+b*x]/x^5, x]", //
        "-1/4*a/x^4-1/3*b/x^3-1/4*(1+4*a^2)*b^4*ArcTan[Sqrt[1-a]*Sqrt[1+a+b*x]/(Sqrt[1+a]*Sqrt[-1+a+b*x])]/(1-a^2)^(7/2)-1/4*Sqrt[-1+a+b*x]*Sqrt[1+a+b*x]/x^4+1/12*a*b*Sqrt[-1+a+b*x]*Sqrt[1+a+b*x]/((1-a^2)*x^3)+1/24*(3+2*a^2)*b^2*Sqrt[-1+a+b*x]*Sqrt[1+a+b*x]/((1-a^2)^2*x^2)+1/24*a*(13+2*a^2)*b^3*Sqrt[-1+a+b*x]*Sqrt[1+a+b*x]/((1-a^2)^3*x)");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:604
  public void test0004() {
    check( //
        "Integrate[Sqrt[c-c/(a*x)]/(E^ArcCoth[a*x]*x^2), x]", //
        "-8/3*a*c*Sqrt[1+(-1)/(a^2*x^2)]/Sqrt[c-c/(a*x)]-2/3*a*Sqrt[1+(-1)/(a^2*x^2)]*Sqrt[c-c/(a*x)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:517
  public void test0005() {
    check( //
        "Integrate[E^ArcCoth[a*x]*Sqrt[c-c/(a*x)], x]", //
        "ArcTanh[Sqrt[c]*Sqrt[1+(-1)/(a^2*x^2)]/Sqrt[c-c/(a*x)]]*Sqrt[c]/a+c*x*Sqrt[1+(-1)/(a^2*x^2)]/Sqrt[c-c/(a*x)]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1235
  public void test0006() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])*(c-a^2*c*x^2)^3/x^3, x]", //
        "-1/2*c^3/x^2-2*a*c^3/x-4*a^3*c^3*x-1/2*a^4*c^3*x^2+2/3*a^5*c^3*x^3+1/4*a^6*c^3*x^4-a^2*c^3*Log[x]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:61
  public void test0007() {
    check( //
        "Integrate[1/(E^(2*ArcCoth[a*x])*x^3), x]", //
        "1/2/x^2-2*a/x-2*a^2*Log[x]+2*a^2*Log[1+a*x]");
  }

  // 7.1.2 (d x)^m (a+b arcsinh(c x))^n.input:32
  public void test0008() {
    check( //
        "Integrate[ArcSinh[a*x]^2/x^5, x]", //
        "-1/12*a^2/x^2-1/4*ArcSinh[a*x]^2/x^4-1/3*a^4*Log[x]-1/6*a*ArcSinh[a*x]*Sqrt[1+a^2*x^2]/x^3+1/3*a^3*ArcSinh[a*x]*Sqrt[1+a^2*x^2]/x");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:98
  public void test0009() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^4/x^3, x]", //
        "-6*b^3*x*(b*x-ArcTanh[Tanh[a+b*x]])+3*b^2*ArcTanh[Tanh[a+b*x]]^2-2*b*ArcTanh[Tanh[a+b*x]]^3/x-1/2*ArcTanh[Tanh[a+b*x]]^4/x^2+6*b^2*(b*x-ArcTanh[Tanh[a+b*x]])^2*Log[x]");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:425
  public void test0010() {
    check( //
        "Integrate[ArcTanh[a+b*f^(c+d*x)], x]", //
        "-ArcTanh[a+b*f^(c+d*x)]*Log[2/(1+a+b*f^(c+d*x))]/(d*Log[f])+ArcTanh[a+b*f^(c+d*x)]*Log[2*b*f^(c+d*x)/((1-a)*(1+a+b*f^(c+d*x)))]/(d*Log[f])+1/2*PolyLog[2,1+(-2)/(1+a+b*f^(c+d*x))]/(d*Log[f])-1/2*PolyLog[2,1-2*b*f^(c+d*x)/((1-a)*(1+a+b*f^(c+d*x)))]/(d*Log[f])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1145
  public void test0011() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^5/(c-a^2*c*x^2)^(3/2), x]", //
        "2*x*Sqrt[1-a^2*x^2]/(a^5*c*Sqrt[c-a^2*c*x^2])+1/2*x^2*Sqrt[1-a^2*x^2]/(a^4*c*Sqrt[c-a^2*c*x^2])+1/3*x^3*Sqrt[1-a^2*x^2]/(a^3*c*Sqrt[c-a^2*c*x^2])+1/2*Sqrt[1-a^2*x^2]/(a^6*c*(1-a*x)*Sqrt[c-a^2*c*x^2])+9/4*Log[1-a*x]*Sqrt[1-a^2*x^2]/(a^6*c*Sqrt[c-a^2*c*x^2])-1/4*Log[1+a*x]*Sqrt[1-a^2*x^2]/(a^6*c*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:16
  public void test0012() {
    check( //
        "Integrate[(d+c*d*x)*(a+b*ArcTanh[c*x])/x, x]", //
        "a*c*d*x+b*c*d*x*ArcTanh[c*x]+a*d*Log[x]+1/2*b*d*Log[1-c^2*x^2]-1/2*b*d*PolyLog[2,-c*x]+1/2*b*d*PolyLog[2,c*x]");
  }

  // 7.3.3 (d+e x)^m (a+b arctanh(c x^n))^p.input:26
  public void test0013() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])^3/(d+e*x)^2, x]", //
        "-(a+b*ArcTanh[c*x])^3/(e*(d+e*x))+3/2*b*c*(a+b*ArcTanh[c*x])^2*Log[2/(1-c*x)]/(e*(c*d+e))-3/2*b*c*(a+b*ArcTanh[c*x])^2*Log[2/(1+c*x)]/((c*d-e)*e)+3*b*c*(a+b*ArcTanh[c*x])^2*Log[2/(1+c*x)]/(c^2*d^2-e^2)-3*b*c*(a+b*ArcTanh[c*x])^2*Log[2*c*(d+e*x)/((c*d+e)*(1+c*x))]/(c^2*d^2-e^2)+3/2*b^2*c*(a+b*ArcTanh[c*x])*PolyLog[2,1+(-2)/(1-c*x)]/(e*(c*d+e))+3/2*b^2*c*(a+b*ArcTanh[c*x])*PolyLog[2,1+(-2)/(1+c*x)]/((c*d-e)*e)-3*b^2*c*(a+b*ArcTanh[c*x])*PolyLog[2,1+(-2)/(1+c*x)]/(c^2*d^2-e^2)+3*b^2*c*(a+b*ArcTanh[c*x])*PolyLog[2,1-2*c*(d+e*x)/((c*d+e)*(1+c*x))]/(c^2*d^2-e^2)-3/4*b^3*c*PolyLog[3,1+(-2)/(1-c*x)]/(e*(c*d+e))+3/4*b^3*c*PolyLog[3,1+(-2)/(1+c*x)]/((c*d-e)*e)-3/2*b^3*c*PolyLog[3,1+(-2)/(1+c*x)]/(c^2*d^2-e^2)+3/2*b^3*c*PolyLog[3,1-2*c*(d+e*x)/((c*d+e)*(1+c*x))]/(c^2*d^2-e^2)");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:74
  public void test0014() {
    check( //
        "Integrate[x/ArcCosh[a*x]^3, x]", //
        "1/2/(a^2*ArcCosh[a*x])-x^2/ArcCosh[a*x]+SinhIntegral[2*ArcCosh[a*x]]/a^2-1/2*x*Sqrt[-1+a*x]*Sqrt[1+a*x]/(a*ArcCosh[a*x]^2)");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:209
  public void test0015() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/x, x]", //
        "2*b^2*Sqrt[d-c^2*d*x^2]+(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]-2*a*b*c*x*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-2*b^2*c*x*ArcCosh[c*x]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-2*(a+b*ArcCosh[c*x])^2*ArcTan[E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+2*I*b*(a+b*ArcCosh[c*x])*PolyLog[2,-I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-2*I*b*(a+b*ArcCosh[c*x])*PolyLog[2,I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-2*I*b^2*PolyLog[3,-I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+2*I*b^2*PolyLog[3,I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:226
  public void test0016() {
    check( //
        "Integrate[x/ArcCoth[Tanh[a+b*x]], x]", //
        "x/b+(b*x-ArcCoth[Tanh[a+b*x]])*Log[ArcCoth[Tanh[a+b*x]]]/b^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1310
  public void test0017() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])/(x^2*(c-a^2*c*x^2)^(1/2)), x]", //
        "-2*a*ArcTanh[Sqrt[c-a^2*c*x^2]/Sqrt[c]]/Sqrt[c]+2*a*(1+a*x)/Sqrt[c-a^2*c*x^2]-Sqrt[c-a^2*c*x^2]/(c*x)");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:170
  public void test0018() {
    check( //
        "Integrate[1/(a+b*ArcSinh[c+d*x])^(1/2), x]", //
        "1/2*E^(a/b)*Erf[Sqrt[a+b*ArcSinh[c+d*x]]/Sqrt[b]]*Sqrt[Pi]/(d*Sqrt[b])+1/2*Erfi[Sqrt[a+b*ArcSinh[c+d*x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*d*Sqrt[b])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:298
  public void test0019() {
    check( //
        "Integrate[1/(x^(5/2)*ArcTanh[Tanh[a+b*x]]^(3/2)), x]", //
        "2/3/(x^(3/2)*(b*x-ArcTanh[Tanh[a+b*x]])*Sqrt[ArcTanh[Tanh[a+b*x]]])+8/3*b/((b*x-ArcTanh[Tanh[a+b*x]])^2*Sqrt[x]*Sqrt[ArcTanh[Tanh[a+b*x]]])-16/3*b^2*Sqrt[x]/((b*x-ArcTanh[Tanh[a+b*x]])^3*Sqrt[ArcTanh[Tanh[a+b*x]]])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:208
  public void test0020() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-a*c*x)^p, x]", //
        "2*(c-a*c*x)^p/(a*p)-(c-a*c*x)^(1+p)/(a*c*(1+p))");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:170
  public void test0021() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])/x^5, x]", //
        "1/4*(-a-b*ArcCosh[c*x])/x^4+1/12*b*c*Sqrt[-1+c*x]*Sqrt[1+c*x]/x^3+1/6*b*c^3*Sqrt[-1+c*x]*Sqrt[1+c*x]/x");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:63
  public void test0022() {
    check( //
        "Integrate[1/(E^(2*ArcTanh[a*x])*x^4), x]", //
        "(-1/3)/x^3+a/x^2-2*a^2/x-2*a^3*Log[x]+2*a^3*Log[1+a*x]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:59
  public void test0023() {
    check( //
        "Integrate[1/(E^(2*ArcCoth[a*x])*x), x]", //
        "-Log[x]+2*Log[1+a*x]");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:388
  public void test0024() {
    check( //
        "Integrate[x^2*ArcTanh[1-I*d+d*Tan[a+b*x]], x]", //
        "1/12*I*b*x^4+1/3*x^3*ArcTanh[1-I*d+d*Tan[a+b*x]]-1/6*x^3*Log[1+E^(2*I*a+2*I*b*x)*(1-I*d)]+1/4*I*x^2*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b-1/4*x*PolyLog[3,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b^2-1/8*I*PolyLog[4,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b^3");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:364
  public void test0025() {
    check( //
        "Integrate[x^2/((1-a^2*x^2)^2*ArcTanh[a*x]^3), x]", //
        "-1/2*x^2/(a*(1-a^2*x^2)*ArcTanh[a*x]^2)-x/(a^2*(1-a^2*x^2)*ArcTanh[a*x])+CoshIntegral[2*ArcTanh[a*x]]/a^3");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:299
  public void test0026() {
    check( //
        "Integrate[1/(x^(7/2)*ArcTanh[Tanh[a+b*x]]^(3/2)), x]", //
        "4/5*b/(x^(3/2)*(b*x-ArcTanh[Tanh[a+b*x]])^2*Sqrt[ArcTanh[Tanh[a+b*x]]])+2/5/(x^(5/2)*(b*x-ArcTanh[Tanh[a+b*x]])*Sqrt[ArcTanh[Tanh[a+b*x]]])+16/5*b^2/((b*x-ArcTanh[Tanh[a+b*x]])^3*Sqrt[x]*Sqrt[ArcTanh[Tanh[a+b*x]]])-32/5*b^3*Sqrt[x]/((b*x-ArcTanh[Tanh[a+b*x]])^4*Sqrt[ArcTanh[Tanh[a+b*x]]])");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:362
  public void test0027() {
    check( //
        "Integrate[1/(ArcSinh[a+b*x]*Sqrt[1+a^2+2*a*b*x+b^2*x^2]), x]", //
        "Log[ArcSinh[a+b*x]]/b");
  }

  // 7.6.2 Inverse hyperbolic cosecant functions.input:56
  public void test0028() {
    check( //
        "Integrate[E^ArcCsch[a*x^2]*x, x]", //
        "-1/2*ArcCsch[a*x^2]/a+Log[x]/a+1/2*x^2*Sqrt[1+1/(a^2*x^4)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:458
  public void test0029() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-c/(a*x))^3, x]", //
        "-1/2*c^3/(a^3*x^2)+c^3/(a^2*x)+c^3*x-c^3*Log[x]/a");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:38
  public void test0030() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])^3, x]", //
        "(a+b*ArcTanh[c*x])^3/c+x*(a+b*ArcTanh[c*x])^3-3*b*(a+b*ArcTanh[c*x])^2*Log[2/(1-c*x)]/c-3*b^2*(a+b*ArcTanh[c*x])*PolyLog[2,1+(-2)/(1-c*x)]/c+3/2*b^3*PolyLog[3,1+(-2)/(1-c*x)]/c");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:209
  public void test0031() {
    check( //
        "Integrate[(a+b*ArcTanh[c/x])^3/x, x]", //
        "-2*(a+b*ArcCoth[x/c])^3*ArcTanh[1+(-2)/(1-c/x)]+3/2*b*(a+b*ArcCoth[x/c])^2*PolyLog[2,1+(-2)/(1-c/x)]-3/2*b*(a+b*ArcCoth[x/c])^2*PolyLog[2,-1+2/(1-c/x)]-3/2*b^2*(a+b*ArcCoth[x/c])*PolyLog[3,1+(-2)/(1-c/x)]+3/2*b^2*(a+b*ArcCoth[x/c])*PolyLog[3,-1+2/(1-c/x)]+3/4*b^3*PolyLog[4,1+(-2)/(1-c/x)]-3/4*b^3*PolyLog[4,-1+2/(1-c/x)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:998
  public void test0032() {
    check( //
        "Integrate[(c-c/(a^2*x^2))^(9/2)/E^(3*ArcCoth[a*x]), x]", //
        "-1/8*c^4*Sqrt[c-c/(a^2*x^2)]/(a^9*x^8*Sqrt[1+(-1)/(a^2*x^2)])+3/7*c^4*Sqrt[c-c/(a^2*x^2)]/(a^8*x^7*Sqrt[1+(-1)/(a^2*x^2)])-8/5*c^4*Sqrt[c-c/(a^2*x^2)]/(a^6*x^5*Sqrt[1+(-1)/(a^2*x^2)])+3/2*c^4*Sqrt[c-c/(a^2*x^2)]/(a^5*x^4*Sqrt[1+(-1)/(a^2*x^2)])+2*c^4*Sqrt[c-c/(a^2*x^2)]/(a^4*x^3*Sqrt[1+(-1)/(a^2*x^2)])-4*c^4*Sqrt[c-c/(a^2*x^2)]/(a^3*x^2*Sqrt[1+(-1)/(a^2*x^2)])+c^4*x*Sqrt[c-c/(a^2*x^2)]/Sqrt[1+(-1)/(a^2*x^2)]-3*c^4*Log[x]*Sqrt[c-c/(a^2*x^2)]/(a*Sqrt[1+(-1)/(a^2*x^2)])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:182
  public void test0033() {
    check( //
        "Integrate[x^2/ArcTanh[Tanh[a+b*x]]^(3/2), x]", //
        "-16/3*ArcTanh[Tanh[a+b*x]]^(3/2)/b^3-2*x^2/(b*Sqrt[ArcTanh[Tanh[a+b*x]]])+8*x*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^2");
  }

  // 7.3.3 (d+e x)^m (a+b arctanh(c x^n))^p.input:22
  public void test0034() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*ArcTanh[c*x])^3, x]", //
        "3*a*b^2*d*e^2*x/c^2+1/4*b^3*e^3*x/c^3-1/4*b^3*e^3*ArcTanh[c*x]/c^4+3*b^3*d*e^2*x*ArcTanh[c*x]/c^2+1/4*b^2*e^3*x^2*(a+b*ArcTanh[c*x])/c^2-3/2*b*d*e^2*(a+b*ArcTanh[c*x])^2/c^3+1/4*b*e^3*(a+b*ArcTanh[c*x])^2/c^4+3/4*b*e*(6*c^2*d^2+e^2)*(a+b*ArcTanh[c*x])^2/c^4+3/4*b*e*(6*c^2*d^2+e^2)*x*(a+b*ArcTanh[c*x])^2/c^3+3/2*b*d*e^2*x^2*(a+b*ArcTanh[c*x])^2/c+1/4*b*e^3*x^3*(a+b*ArcTanh[c*x])^2/c+d*(c^2*d^2+e^2)*(a+b*ArcTanh[c*x])^3/c^3-1/4*(c^4*d^4+6*c^2*d^2*e^2+e^4)*(a+b*ArcTanh[c*x])^3/(c^4*e)+1/4*(d+e*x)^4*(a+b*ArcTanh[c*x])^3/e-1/2*b^2*e^3*(a+b*ArcTanh[c*x])*Log[2/(1-c*x)]/c^4-3/2*b^2*e*(6*c^2*d^2+e^2)*(a+b*ArcTanh[c*x])*Log[2/(1-c*x)]/c^4-3*b*d*(c^2*d^2+e^2)*(a+b*ArcTanh[c*x])^2*Log[2/(1-c*x)]/c^3+3/2*b^3*d*e^2*Log[1-c^2*x^2]/c^3-1/4*b^3*e^3*PolyLog[2,1+(-2)/(1-c*x)]/c^4-3/4*b^3*e*(6*c^2*d^2+e^2)*PolyLog[2,1+(-2)/(1-c*x)]/c^4-3*b^2*d*(c^2*d^2+e^2)*(a+b*ArcTanh[c*x])*PolyLog[2,1+(-2)/(1-c*x)]/c^3+3/2*b^3*d*(c^2*d^2+e^2)*PolyLog[3,1+(-2)/(1-c*x)]/c^3");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:405
  public void test0035() {
    check( //
        "Integrate[ArcTanh[c+d*Cot[a+b*x]], x]", //
        "x*ArcTanh[c+d*Cot[a+b*x]]+1/2*x*Log[1-E^(2*I*a+2*I*b*x)*(1-c-I*d)/(1-c+I*d)]-1/2*x*Log[1-E^(2*I*a+2*I*b*x)*(1+c+I*d)/(1+c-I*d)]-1/4*I*PolyLog[2,E^(2*I*a+2*I*b*x)*(1-c-I*d)/(1-c+I*d)]/b+1/4*I*PolyLog[2,E^(2*I*a+2*I*b*x)*(1+c+I*d)/(1+c-I*d)]/b");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:612
  public void test0036() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])*Sqrt[c-c/(a*x)], x]", //
        "-5*ArcSinh[Sqrt[a]*Sqrt[x]]*Sqrt[c-c/(a*x)]*Sqrt[x]/(Sqrt[a]*Sqrt[1-a*x])+4*ArcTanh[Sqrt[2]*Sqrt[a]*Sqrt[x]/Sqrt[1+a*x]]*Sqrt[2]*Sqrt[c-c/(a*x)]*Sqrt[x]/(Sqrt[a]*Sqrt[1-a*x])-x*Sqrt[c-c/(a*x)]*Sqrt[1+a*x]/Sqrt[1-a*x]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:303
  public void test0037() {
    check( //
        "Integrate[x^2*ArcTanh[a*x]^3/(1-a^2*x^2), x]", //
        "-ArcTanh[a*x]^3/a^3-x*ArcTanh[a*x]^3/a^2+1/4*ArcTanh[a*x]^4/a^3+3*ArcTanh[a*x]^2*Log[2/(1-a*x)]/a^3+3*ArcTanh[a*x]*PolyLog[2,1+(-2)/(1-a*x)]/a^3-3/2*PolyLog[3,1+(-2)/(1-a*x)]/a^3");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:306
  public void test0038() {
    check( //
        "Integrate[1/(E^ArcCoth[a*x]*(c-a*c*x)^(1/2)), x]", //
        "2*(1+a*x)/(E^ArcCoth[a*x]*a*Sqrt[c-a*c*x])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:499
  public void test0039() {
    check( //
        "Integrate[x*ArcTanh[a*x]^3/(1-a^2*x^2)^(3/2), x]", //
        "-6*x/(a*Sqrt[1-a^2*x^2])+6*ArcTanh[a*x]/(a^2*Sqrt[1-a^2*x^2])-3*x*ArcTanh[a*x]^2/(a*Sqrt[1-a^2*x^2])+ArcTanh[a*x]^3/(a^2*Sqrt[1-a^2*x^2])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:832
  public void test0040() {
    check( //
        "Integrate[Sqrt[c-a^2*c*x^2]/(E^(3*ArcCoth[a*x])*x), x]", //
        "Sqrt[c-a^2*c*x^2]/Sqrt[1+(-1)/(a^2*x^2)]+Log[x]*Sqrt[c-a^2*c*x^2]/(a*x*Sqrt[1+(-1)/(a^2*x^2)])-4*Log[1+a*x]*Sqrt[c-a^2*c*x^2]/(a*x*Sqrt[1+(-1)/(a^2*x^2)])");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:408
  public void test0041() {
    check( //
        "Integrate[x/(ArcCosh[x]*Sqrt[-1+x]*Sqrt[1+x]), x]", //
        "CoshIntegral[ArcCosh[x]]");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:141
  public void test0042() {
    check( //
        "Integrate[a+b*ArcTanh[c*x^3], x]", //
        "a*x+b*x*ArcTanh[c*x^3]+1/2*b*Log[1-c^(2/3)*x^2]/c^(1/3)-1/4*b*Log[1+c^(2/3)*x^2+c^(4/3)*x^4]/c^(1/3)+1/2*b*ArcTan[(1+2*c^(2/3)*x^2)/Sqrt[3]]*Sqrt[3]/c^(1/3)");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:73
  public void test0043() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^2/x^2, x]", //
        "2*b^2*x-ArcTanh[Tanh[a+b*x]]^2/x-2*b*(b*x-ArcTanh[Tanh[a+b*x]])*Log[x]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:799
  public void test0044() {
    check( //
        "Integrate[E^ArcTanh[a*x]*Sqrt[c-c/(a^2*x^2)], x]", //
        "a*x^2*Sqrt[c-c/(a^2*x^2)]/Sqrt[1-a^2*x^2]+x*Log[x]*Sqrt[c-c/(a^2*x^2)]/Sqrt[1-a^2*x^2]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:336
  public void test0045() {
    check( //
        "Integrate[x^2*ArcTanh[a*x]^2/(1-a^2*x^2)^2, x]", //
        "1/4*x/(a^2*(1-a^2*x^2))+1/4*ArcTanh[a*x]/a^3-1/2*ArcTanh[a*x]/(a^3*(1-a^2*x^2))+1/2*x*ArcTanh[a*x]^2/(a^2*(1-a^2*x^2))-1/6*ArcTanh[a*x]^3/a^3");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:232
  public void test0046() {
    check( //
        "Integrate[(a+b*ArcTanh[c/x^2])^2/x, x]", //
        "-(a+b*ArcCoth[x^2/c])^2*ArcTanh[1+(-2)/(1-c/x^2)]+1/2*b*(a+b*ArcCoth[x^2/c])*PolyLog[2,1+(-2)/(1-c/x^2)]-1/2*b*(a+b*ArcCoth[x^2/c])*PolyLog[2,-1+2/(1-c/x^2)]-1/4*b^2*PolyLog[3,1+(-2)/(1-c/x^2)]+1/4*b^2*PolyLog[3,-1+2/(1-c/x^2)]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:281
  public void test0047() {
    check( //
        "Integrate[(1-a^2*x^2)^3*ArcTanh[a*x]^3, x]", //
        "-13/210*(1-a^2*x^2)/a-1/140*(1-a^2*x^2)^2/a-14/15*x*ArcTanh[a*x]-13/105*x*(1-a^2*x^2)*ArcTanh[a*x]-1/35*x*(1-a^2*x^2)^2*ArcTanh[a*x]+12/35*(1-a^2*x^2)*ArcTanh[a*x]^2/a+9/70*(1-a^2*x^2)^2*ArcTanh[a*x]^2/a+1/14*(1-a^2*x^2)^3*ArcTanh[a*x]^2/a+16/35*ArcTanh[a*x]^3/a+16/35*x*ArcTanh[a*x]^3+8/35*x*(1-a^2*x^2)*ArcTanh[a*x]^3+6/35*x*(1-a^2*x^2)^2*ArcTanh[a*x]^3+1/7*x*(1-a^2*x^2)^3*ArcTanh[a*x]^3-48/35*ArcTanh[a*x]^2*Log[2/(1-a*x)]/a-7/15*Log[1-a^2*x^2]/a-48/35*ArcTanh[a*x]*PolyLog[2,1+(-2)/(1-a*x)]/a+24/35*PolyLog[3,1+(-2)/(1-a*x)]/a");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:783
  public void test0048() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*Sqrt[c-a^2*c*x^2]/x^2, x]", //
        "-a*ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]*Sqrt[c]+2*a*ArcTanh[Sqrt[c-a^2*c*x^2]/Sqrt[c]]*Sqrt[c]+Sqrt[c-a^2*c*x^2]/x");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:43
  public void test0049() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])^3/x^5, x]", //
        "-1/4*b^3*c^3/x+1/4*b^3*c^4*ArcTanh[c*x]-1/4*b^2*c^2*(a+b*ArcTanh[c*x])/x^2+b*c^4*(a+b*ArcTanh[c*x])^2-1/4*b*c*(a+b*ArcTanh[c*x])^2/x^3-3/4*b*c^3*(a+b*ArcTanh[c*x])^2/x+1/4*c^4*(a+b*ArcTanh[c*x])^3-1/4*(a+b*ArcTanh[c*x])^3/x^4+2*b^2*c^4*(a+b*ArcTanh[c*x])*Log[2+(-2)/(1+c*x)]-b^3*c^4*PolyLog[2,-1+2/(1+c*x)]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:905
  public void test0050() {
    check( //
        "Integrate[Sqrt[c-c/(a^2*x^2)]/(E^(3*ArcTanh[a*x])*x^5), x]", //
        "-4*a^4*Sqrt[c-c/(a^2*x^2)]/Sqrt[1-a^2*x^2]-1/5*Sqrt[c-c/(a^2*x^2)]/(x^4*Sqrt[1-a^2*x^2])+3/4*a*Sqrt[c-c/(a^2*x^2)]/(x^3*Sqrt[1-a^2*x^2])-4/3*a^2*Sqrt[c-c/(a^2*x^2)]/(x^2*Sqrt[1-a^2*x^2])+2*a^3*Sqrt[c-c/(a^2*x^2)]/(x*Sqrt[1-a^2*x^2])-4*a^5*x*Log[x]*Sqrt[c-c/(a^2*x^2)]/Sqrt[1-a^2*x^2]+4*a^5*x*Log[1+a*x]*Sqrt[c-c/(a^2*x^2)]/Sqrt[1-a^2*x^2]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:559
  public void test0051() {
    check( //
        "Integrate[(1-a^2*x^2)^(3/2)*ArcTanh[a*x]/x^3, x]", //
        "a^2*ArcSin[a*x]+3*a^2*ArcTanh[a*x]*ArcTanh[Sqrt[1-a*x]/Sqrt[1+a*x]]-3/2*a^2*PolyLog[2,-Sqrt[1-a*x]/Sqrt[1+a*x]]+3/2*a^2*PolyLog[2,Sqrt[1-a*x]/Sqrt[1+a*x]]-1/2*a*Sqrt[1-a^2*x^2]/x-a^2*ArcTanh[a*x]*Sqrt[1-a^2*x^2]-1/2*ArcTanh[a*x]*Sqrt[1-a^2*x^2]/x^2");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:15
  public void test0052() {
    check( //
        "Integrate[(d+c*d*x)*(a+b*ArcTanh[c*x]), x]", //
        "1/2*b*d*x+1/2*d*(1+c*x)^2*(a+b*ArcTanh[c*x])/c+b*d*Log[1-c*x]/c");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:19
  public void test0053() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])/x^4, x]", //
        "-1/6*b*c/x^2+1/3*(-a-b*ArcTanh[c*x])/x^3+1/3*b*c^3*Log[x]-1/6*b*c^3*Log[1-c^2*x^2]");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:390
  public void test0054() {
    check( //
        "Integrate[ArcTanh[1-I*d+d*Tan[a+b*x]], x]", //
        "1/2*I*b*x^2+x*ArcTanh[1-I*d+d*Tan[a+b*x]]-1/2*x*Log[1+E^(2*I*a+2*I*b*x)*(1-I*d)]+1/4*I*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:92
  public void test0055() {
    check( //
        "Integrate[(f+g*x)^2*(d-c^2*d*x^2)^(3/2)*(a+b*ArcCosh[c*x]), x]", //
        "3/8*d*f^2*x*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]-1/16*d*g^2*x*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]/c^2+1/8*d*g^2*x^3*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]+1/4*d*f^2*x*(1-c*x)*(1+c*x)*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]+1/6*d*g^2*x^3*(1-c*x)*(1+c*x)*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]-2/5*d*f*g*(1-c*x)^2*(1+c*x)^2*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]/c^2+2/5*b*d*f*g*x*Sqrt[d-c^2*d*x^2]/(c*Sqrt[-1+c*x]*Sqrt[1+c*x])-5/16*b*c*d*f^2*x^2*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+1/32*b*d*g^2*x^2*Sqrt[d-c^2*d*x^2]/(c*Sqrt[-1+c*x]*Sqrt[1+c*x])-4/15*b*c*d*f*g*x^3*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+1/16*b*c^3*d*f^2*x^4*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-7/96*b*c*d*g^2*x^4*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+2/25*b*c^3*d*f*g*x^5*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+1/36*b*c^3*d*g^2*x^6*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-3/16*d*f^2*(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c*Sqrt[-1+c*x]*Sqrt[1+c*x])-1/32*d*g^2*(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c^3*Sqrt[-1+c*x]*Sqrt[1+c*x])");
  }

  // 7.3.3 (d+e x)^m (a+b arctanh(c x^n))^p.input:16
  public void test0056() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*ArcTanh[c*x])^2, x]", //
        "b^2*d*e^2*x/c^2+1/2*a*b*e*(6*c^2*d^2+e^2)*x/c^3+1/12*b^2*e^3*x^2/c^2-b^2*d*e^2*ArcTanh[c*x]/c^3+1/2*b^2*e*(6*c^2*d^2+e^2)*x*ArcTanh[c*x]/c^3+b*d*e^2*x^2*(a+b*ArcTanh[c*x])/c+1/6*b*e^3*x^3*(a+b*ArcTanh[c*x])/c+d*(c^2*d^2+e^2)*(a+b*ArcTanh[c*x])^2/c^3-1/4*(c^4*d^4+6*c^2*d^2*e^2+e^4)*(a+b*ArcTanh[c*x])^2/(c^4*e)+1/4*(d+e*x)^4*(a+b*ArcTanh[c*x])^2/e-2*b*d*(c^2*d^2+e^2)*(a+b*ArcTanh[c*x])*Log[2/(1-c*x)]/c^3+1/12*b^2*e^3*Log[1-c^2*x^2]/c^4+1/4*b^2*e*(6*c^2*d^2+e^2)*Log[1-c^2*x^2]/c^4-b^2*d*(c^2*d^2+e^2)*PolyLog[2,1+(-2)/(1-c*x)]/c^3");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:389
  public void test0057() {
    check( //
        "Integrate[ArcSinh[Sqrt[x]]/x^2, x]", //
        "-ArcSinh[Sqrt[x]]/x-Sqrt[1+x]/Sqrt[x]");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:17
  public void test0058() {
    check( //
        "Integrate[ArcCosh[c*x]/(d+e*x)^3, x]", //
        "-1/2*ArcCosh[c*x]/(e*(d+e*x)^2)+c^3*d*ArcTanh[Sqrt[c*d+e]*Sqrt[1+c*x]/(Sqrt[c*d-e]*Sqrt[-1+c*x])]/((c*d-e)^(3/2)*e*(c*d+e)^(3/2))-1/2*c*Sqrt[-1+c*x]*Sqrt[1+c*x]/((c^2*d^2-e^2)*(d+e*x))");
  }

  // 7.6.1 u (a+b arccsch(c x))^n.input:22
  public void test0059() {
    check( //
        "Integrate[(a+b*ArcCsch[c*x])/x^4, x]", //
        "1/9*b*c^3*(1+1/(c^2*x^2))^(3/2)+1/3*(-a-b*ArcCsch[c*x])/x^3-1/3*b*c^3*Sqrt[1+1/(c^2*x^2)]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:830
  public void test0060() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*Sqrt[c-c/(a^2*x^2)]), x]", //
        "Sqrt[1-a^2*x^2]/(a*Sqrt[c-c/(a^2*x^2)])-Log[1+a*x]*Sqrt[1-a^2*x^2]/(a^2*x*Sqrt[c-c/(a^2*x^2)])");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:514
  public void test0061() {
    check( //
        "Integrate[x^3/((a+b*ArcSinh[c*x])^2*Sqrt[1+c^2*x^2]), x]", //
        "-x^3/(b*c*(a+b*ArcSinh[c*x]))-3/4*CoshIntegral[(a+b*ArcSinh[c*x])/b]*Cosh[a/b]/(b^2*c^4)+3/4*CoshIntegral[3*(a+b*ArcSinh[c*x])/b]*Cosh[3*a/b]/(b^2*c^4)+3/4*SinhIntegral[(a+b*ArcSinh[c*x])/b]*Sinh[a/b]/(b^2*c^4)-3/4*SinhIntegral[3*(a+b*ArcSinh[c*x])/b]*Sinh[3*a/b]/(b^2*c^4)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:349
  public void test0062() {
    check( //
        "Integrate[E^ArcTanh[a*x]*(c-a*c*x)/x^4, x]", //
        "-1/3*c*(1-a^2*x^2)^(3/2)/x^3");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:410
  public void test0063() {
    check( //
        "Integrate[(c+a^2*c*x^2)^2/ArcSinh[a*x], x]", //
        "5/8*c^2*CoshIntegral[ArcSinh[a*x]]/a+5/16*c^2*CoshIntegral[3*ArcSinh[a*x]]/a+1/16*c^2*CoshIntegral[5*ArcSinh[a*x]]/a");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:745
  public void test0064() {
    check( //
        "Integrate[(c-a^2*c*x^2)^(5/2)/E^ArcCoth[a*x], x]", //
        "(1-a*x)^4*(c-a^2*c*x^2)^(5/2)/(a^6*(1+(-1)/(a^2*x^2))^(5/2)*x^5)-4/5*(1-a*x)^5*(c-a^2*c*x^2)^(5/2)/(a^6*(1+(-1)/(a^2*x^2))^(5/2)*x^5)+1/6*(1-a*x)^6*(c-a^2*c*x^2)^(5/2)/(a^6*(1+(-1)/(a^2*x^2))^(5/2)*x^5)");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:133
  public void test0065() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])*Log[h*(f+g*x)^m]/Sqrt[1-c^2*x^2], x]", //
        "1/6*m*(a+b*ArcCosh[c*x])^3*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b^2*c*Sqrt[1-c^2*x^2])+1/2*(a+b*ArcCosh[c*x])^2*Log[h*(f+g*x)^m]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[1-c^2*x^2])-1/2*m*(a+b*ArcCosh[c*x])^2*Log[1+E^ArcCosh[c*x]*g/(c*f-Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[1-c^2*x^2])-1/2*m*(a+b*ArcCosh[c*x])^2*Log[1+E^ArcCosh[c*x]*g/(c*f+Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[1-c^2*x^2])-m*(a+b*ArcCosh[c*x])*PolyLog[2,-E^ArcCosh[c*x]*g/(c*f-Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(c*Sqrt[1-c^2*x^2])-m*(a+b*ArcCosh[c*x])*PolyLog[2,-E^ArcCosh[c*x]*g/(c*f+Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(c*Sqrt[1-c^2*x^2])+b*m*PolyLog[3,-E^ArcCosh[c*x]*g/(c*f-Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(c*Sqrt[1-c^2*x^2])+b*m*PolyLog[3,-E^ArcCosh[c*x]*g/(c*f+Sqrt[c^2*f^2-g^2])]*Sqrt[-1+c*x]*Sqrt[1+c*x]/(c*Sqrt[1-c^2*x^2])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:620
  public void test0066() {
    check( //
        "Integrate[(c+d*x^2)^3*ArcTanh[a*x], x]", //
        "1/70*d*(35*a^4*c^2+21*a^2*c*d+5*d^2)*x^2/a^5+1/140*d^2*(21*a^2*c+5*d)*x^4/a^3+1/42*d^3*x^6/a+c^3*x*ArcTanh[a*x]+c^2*d*x^3*ArcTanh[a*x]+3/5*c*d^2*x^5*ArcTanh[a*x]+1/7*d^3*x^7*ArcTanh[a*x]+1/70*(35*a^6*c^3+35*a^4*c^2*d+21*a^2*c*d^2+5*d^3)*Log[1-a^2*x^2]/a^7");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:179
  public void test0067() {
    check( //
        "Integrate[(a+b*ArcSinh[c*x])/(x^4*Sqrt[d+c^2*d*x^2]), x]", //
        "-1/6*b*c*Sqrt[1+c^2*x^2]/(x^2*Sqrt[d+c^2*d*x^2])-2/3*b*c^3*Log[x]*Sqrt[1+c^2*x^2]/Sqrt[d+c^2*d*x^2]-1/3*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/(d*x^3)+2/3*c^2*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/(d*x)");
  }

  // 7.5.1 u (a+b arcsech(c x))^n.input:177
  public void test0068() {
    check( //
        "Integrate[x*(a+b*ArcSech[c*x])*Sqrt[d+e*x^2], x]", //
        "1/3*(d+e*x^2)^(3/2)*(a+b*ArcSech[c*x])/e-1/3*b*d^(3/2)*ArcTanh[Sqrt[d+e*x^2]/(Sqrt[d]*Sqrt[1-c^2*x^2])]*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]/e-1/6*b*(3*c^2*d+e)*ArcTan[Sqrt[e]*Sqrt[1-c^2*x^2]/(c*Sqrt[d+e*x^2])]*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]/(c^3*Sqrt[e])-1/6*b*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]*Sqrt[1-c^2*x^2]*Sqrt[d+e*x^2]/c^2");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:219
  public void test0069() {
    check( //
        "Integrate[(1-a^2*x^2)*ArcTanh[a*x]^2, x]", //
        "-1/3*x+1/3*(1-a^2*x^2)*ArcTanh[a*x]/a+2/3*ArcTanh[a*x]^2/a+2/3*x*ArcTanh[a*x]^2+1/3*x*(1-a^2*x^2)*ArcTanh[a*x]^2-4/3*ArcTanh[a*x]*Log[2/(1-a*x)]/a-2/3*PolyLog[2,1+(-2)/(1-a*x)]/a");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:147
  public void test0070() {
    check( //
        "Integrate[Sqrt[ArcTanh[Tanh[a+b*x]]]/x^3, x]", //
        "1/4*b^2*ArcTan[Sqrt[ArcTanh[Tanh[a+b*x]]]/Sqrt[b*x-ArcTanh[Tanh[a+b*x]]]]/(b*x-ArcTanh[Tanh[a+b*x]])^(3/2)-1/4*b/(x*Sqrt[ArcTanh[Tanh[a+b*x]]])+1/4*b^2/((b*x-ArcTanh[Tanh[a+b*x]])*Sqrt[ArcTanh[Tanh[a+b*x]]])-1/2*Sqrt[ArcTanh[Tanh[a+b*x]]]/x^2");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:66
  public void test0071() {
    check( //
        "Integrate[ArcCoth[x]/(a-a*x^2)^(1/2), x]", //
        "-2*ArcCoth[x]*ArcTan[Sqrt[1-x]/Sqrt[1+x]]*Sqrt[1-x^2]/Sqrt[a-a*x^2]-I*PolyLog[2,-I*Sqrt[1-x]/Sqrt[1+x]]*Sqrt[1-x^2]/Sqrt[a-a*x^2]+I*PolyLog[2,I*Sqrt[1-x]/Sqrt[1+x]]*Sqrt[1-x^2]/Sqrt[a-a*x^2]");
  }

  // 7.6.2 Inverse hyperbolic cosecant functions.input:31
  public void test0072() {
    check( //
        "Integrate[ArcCsch[1/x], x]", //
        "x*ArcSinh[x]-Sqrt[1+x^2]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:62
  public void test0073() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])/(x^3*(d+c*d*x)), x]", //
        "-1/2*b*c/(d*x)+1/2*b*c^2*ArcTanh[c*x]/d+1/2*(-a-b*ArcTanh[c*x])/(d*x^2)+c*(a+b*ArcTanh[c*x])/(d*x)-b*c^2*Log[x]/d+1/2*b*c^2*Log[1-c^2*x^2]/d+c^2*(a+b*ArcTanh[c*x])*Log[2+(-2)/(1+c*x)]/d-1/2*b*c^2*PolyLog[2,-1+2/(1+c*x)]/d");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:788
  public void test0074() {
    check( //
        "Integrate[1/(E^(3*ArcTanh[a*x])*(c-c/(a^2*x^2))^2), x]", //
        "-1/5*(1-a*x)^3/(a*c^2*(1-a^2*x^2)^(5/2))+6/5*(1-a*x)^2/(a*c^2*(1-a^2*x^2)^(3/2))-3*ArcSin[a*x]/(a*c^2)-24/5*(1-a*x)/(a*c^2*Sqrt[1-a^2*x^2])-Sqrt[1-a^2*x^2]/(a*c^2)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:223
  public void test0075() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])/(c-a*c*x), x]", //
        "2/3*(1-a^2*x^2)^(3/2)/(a*c*(1-a*x)^3)+ArcSin[a*x]/(a*c)-2*Sqrt[1-a^2*x^2]/(a*c*(1-a*x))");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:119
  public void test0076() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])^2/(x^2*(d+c*d*x)), x]", //
        "c*(a+b*ArcTanh[c*x])^2/d-(a+b*ArcTanh[c*x])^2/(d*x)+2*b*c*(a+b*ArcTanh[c*x])*Log[2+(-2)/(1+c*x)]/d-c*(a+b*ArcTanh[c*x])^2*Log[2+(-2)/(1+c*x)]/d-b^2*c*PolyLog[2,-1+2/(1+c*x)]/d+b*c*(a+b*ArcTanh[c*x])*PolyLog[2,-1+2/(1+c*x)]/d+1/2*b^2*c*PolyLog[3,-1+2/(1+c*x)]/d");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1309
  public void test0077() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])/(x*(c-a^2*c*x^2)^(1/2)), x]", //
        "-ArcTanh[Sqrt[c-a^2*c*x^2]/Sqrt[c]]/Sqrt[c]+2*(1+a*x)/Sqrt[c-a^2*c*x^2]");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:22
  public void test0078() {
    check( //
        "Integrate[ArcCosh[c*x]^2/(d+e*x), x]", //
        "-1/3*ArcCosh[c*x]^3/e+ArcCosh[c*x]^2*Log[1+E^ArcCosh[c*x]*e/(c*d-Sqrt[c^2*d^2-e^2])]/e+ArcCosh[c*x]^2*Log[1+E^ArcCosh[c*x]*e/(c*d+Sqrt[c^2*d^2-e^2])]/e+2*ArcCosh[c*x]*PolyLog[2,-E^ArcCosh[c*x]*e/(c*d-Sqrt[c^2*d^2-e^2])]/e+2*ArcCosh[c*x]*PolyLog[2,-E^ArcCosh[c*x]*e/(c*d+Sqrt[c^2*d^2-e^2])]/e-2*PolyLog[3,-E^ArcCosh[c*x]*e/(c*d-Sqrt[c^2*d^2-e^2])]/e-2*PolyLog[3,-E^ArcCosh[c*x]*e/(c*d+Sqrt[c^2*d^2-e^2])]/e");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1385
  public void test0079() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])/(c-a^2*c*x^2)^(3/2), x]", //
        "1/2*Sqrt[1-a^2*x^2]/(a*c*(1-a*x)^2*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:352
  public void test0080() {
    check( //
        "Integrate[ArcTanh[1+d+d*Tanh[a+b*x]], x]", //
        "1/2*b*x^2+x*ArcTanh[1+d+d*Tanh[a+b*x]]-1/2*x*Log[1+E^(2*a+2*b*x)*(1+d)]-1/4*PolyLog[2,-E^(2*a+2*b*x)*(1+d)]/b");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:327
  public void test0081() {
    check( //
        "Integrate[ArcCoth[c+d*Tan[a+b*x]], x]", //
        "x*ArcCoth[c+d*Tan[a+b*x]]+1/2*x*Log[1+E^(2*I*a+2*I*b*x)*(1-c+I*d)/(1-c-I*d)]-1/2*x*Log[1+E^(2*I*a+2*I*b*x)*(1+c-I*d)/(1+c+I*d)]-1/4*I*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1-c+I*d)/(1-c-I*d)]/b+1/4*I*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1+c-I*d)/(1+c+I*d)]/b");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:427
  public void test0082() {
    check( //
        "Integrate[ArcTanh[a*x]^2/(1-a^2*x^2)^4, x]", //
        "1/108*x/(1-a^2*x^2)^3+65/1728*x/(1-a^2*x^2)^2+245/1152*x/(1-a^2*x^2)+245/1152*ArcTanh[a*x]/a-1/18*ArcTanh[a*x]/(a*(1-a^2*x^2)^3)-5/48*ArcTanh[a*x]/(a*(1-a^2*x^2)^2)-5/16*ArcTanh[a*x]/(a*(1-a^2*x^2))+1/6*x*ArcTanh[a*x]^2/(1-a^2*x^2)^3+5/24*x*ArcTanh[a*x]^2/(1-a^2*x^2)^2+5/16*x*ArcTanh[a*x]^2/(1-a^2*x^2)+5/48*ArcTanh[a*x]^3/a");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:309
  public void test0083() {
    check( //
        "Integrate[1/(E^ArcCoth[a*x]*(c-a*c*x)^(7/2)), x]", //
        "-3/16*a^(5/2)*(1+(-1)/(a*x))^(7/2)*ArcTanh[Sqrt[2]*Sqrt[1/x]/(Sqrt[a]*Sqrt[1+1/(a*x)])]/((1/x)^(7/2)*(c-a*c*x)^(7/2)*Sqrt[2])-1/4*a^3*(1+(-1)/(a*x))^(7/2)*x^2*Sqrt[1+1/(a*x)]/((a+(-1)/x)^2*(c-a*c*x)^(7/2))+3/16*a^3*(1+(-1)/(a*x))^(7/2)*x^3*Sqrt[1+1/(a*x)]/((a+(-1)/x)*(c-a*c*x)^(7/2))");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:205
  public void test0084() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]*Sqrt[x], x]", //
        "-4/15*b*x^(5/2)+2/3*x^(3/2)*ArcTanh[Tanh[a+b*x]]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:80
  public void test0085() {
    check( //
        "Integrate[ArcTanh[a*x]/(c*x+a*c*x^2), x]", //
        "ArcTanh[a*x]*Log[2+(-2)/(1+a*x)]/c-1/2*PolyLog[2,-1+2/(1+a*x)]/c");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:723
  public void test0086() {
    check( //
        "Integrate[1/(a+b*ArcCosh[c*x])^(3/2), x]", //
        "E^(a/b)*Erf[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(b^(3/2)*c)+Erfi[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*b^(3/2)*c)-2*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[a+b*ArcCosh[c*x]])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:279
  public void test0087() {
    check( //
        "Integrate[(1-a^2*x^2)^3*ArcTanh[a*x], x]", //
        "4/35*(1-a^2*x^2)/a+3/70*(1-a^2*x^2)^2/a+1/42*(1-a^2*x^2)^3/a+16/35*x*ArcTanh[a*x]+8/35*x*(1-a^2*x^2)*ArcTanh[a*x]+6/35*x*(1-a^2*x^2)^2*ArcTanh[a*x]+1/7*x*(1-a^2*x^2)^3*ArcTanh[a*x]+8/35*Log[1-a^2*x^2]/a");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:653
  public void test0088() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])*(d+e*Log[1-c^2*x^2]), x]", //
        "-2*a*e*x-2*b*e*x*ArcTanh[c*x]+e*(a+b*ArcTanh[c*x])^2/(b*c)-b*e*Log[1-c^2*x^2]/c+x*(a+b*ArcTanh[c*x])*(d+e*Log[1-c^2*x^2])+1/4*b*(d+e*Log[1-c^2*x^2])^2/(c*e)");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:658
  public void test0089() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-a^2*c*x^2)^4, x]", //
        "-4/3*c^4*(1+a*x)^6/a+12/7*c^4*(1+a*x)^7/a-3/4*c^4*(1+a*x)^8/a+1/9*c^4*(1+a*x)^9/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:413
  public void test0090() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^3/(c-a*c*x)^4, x]", //
        "1/7*(1-a^2*x^2)^(3/2)/(a^4*c^4*(1-a*x)^5)-19/35*(1-a^2*x^2)^(3/2)/(a^4*c^4*(1-a*x)^4)+86/105*(1-a^2*x^2)^(3/2)/(a^4*c^4*(1-a*x)^3)+ArcSin[a*x]/(a^4*c^4)-2*Sqrt[1-a^2*x^2]/(a^4*c^4*(1-a*x))");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:370
  public void test0091() {
    check( //
        "Integrate[1/((1-a^2*x^2)^2*ArcTanh[a*x]^6), x]", //
        "(-1/5)/(a*(1-a^2*x^2)*ArcTanh[a*x]^5)-1/10*x/((1-a^2*x^2)*ArcTanh[a*x]^4)+1/30*(-1-a^2*x^2)/(a*(1-a^2*x^2)*ArcTanh[a*x]^3)-1/15*x/((1-a^2*x^2)*ArcTanh[a*x]^2)+1/15*(-1-a^2*x^2)/(a*(1-a^2*x^2)*ArcTanh[a*x])+2/15*SinhIntegral[2*ArcTanh[a*x]]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:745
  public void test0092() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])/(c-c/(a^2*x^2))^2, x]", //
        "-x/c^2+1/4/(a*c^2*(1-a*x)^2)+(-7/4)/(a*c^2*(1-a*x))-17/8*Log[1-a*x]/(a*c^2)+1/8*Log[1+a*x]/(a*c^2)");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:293
  public void test0093() {
    check( //
        "Integrate[x^(5/2)/ArcTanh[Tanh[a+b*x]]^(3/2), x]", //
        "15/4*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[ArcTanh[Tanh[a+b*x]]]]*(b*x-ArcTanh[Tanh[a+b*x]])^2/b^(7/2)-2*x^(5/2)/(b*Sqrt[ArcTanh[Tanh[a+b*x]]])+5/2*x^(3/2)*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^2+15/4*(b*x-ArcTanh[Tanh[a+b*x]])*Sqrt[x]*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^3");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:665
  public void test0094() {
    check( //
        "Integrate[E^ArcTanh[a*x]*Sqrt[c-c/(a*x)]/x^5, x]", //
        "-2/9*(1+a*x)^(3/2)*Sqrt[c-c/(a*x)]/(x^4*Sqrt[1-a*x])+4/21*a*(1+a*x)^(3/2)*Sqrt[c-c/(a*x)]/(x^3*Sqrt[1-a*x])-16/105*a^2*(1+a*x)^(3/2)*Sqrt[c-c/(a*x)]/(x^2*Sqrt[1-a*x])+32/315*a^3*(1+a*x)^(3/2)*Sqrt[c-c/(a*x)]/(x*Sqrt[1-a*x])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:628
  public void test0095() {
    check( //
        "Integrate[ArcTanh[a+b*x]/(1-x^2), x]", //
        "1/4*Log[-b*(1-x)/(1-a-b)]*Log[1-a-b*x]-1/4*Log[b*(1+x)/(1-a+b)]*Log[1-a-b*x]-1/4*Log[b*(1-x)/(1+a+b)]*Log[1+a+b*x]+1/4*Log[-b*(1+x)/(1+a-b)]*Log[1+a+b*x]+1/4*PolyLog[2,(1-a-b*x)/(1-a-b)]-1/4*PolyLog[2,(1-a-b*x)/(1-a+b)]+1/4*PolyLog[2,(1+a+b*x)/(1+a-b)]-1/4*PolyLog[2,(1+a+b*x)/(1+a+b)]");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:435
  public void test0096() {
    check( //
        "Integrate[1/(a+b*ArcSinh[I+d*x^2])^(3/2), x]", //
        "-((-I)/b)^(3/2)*x*FresnelC[Sqrt[(-I)/b]*Sqrt[a+I*b*ArcSin[1-I*d*x^2]]/Sqrt[Pi]]*(Cosh[1/2*a/b]-I*Sinh[1/2*a/b])*Sqrt[Pi]/(Cos[1/2*ArcSin[1-I*d*x^2]]-Sin[1/2*ArcSin[1-I*d*x^2]])+((-I)/b)^(3/2)*x*FresnelS[Sqrt[(-I)/b]*Sqrt[a+I*b*ArcSin[1-I*d*x^2]]/Sqrt[Pi]]*(Cosh[1/2*a/b]+I*Sinh[1/2*a/b])*Sqrt[Pi]/(Cos[1/2*ArcSin[1-I*d*x^2]]-Sin[1/2*ArcSin[1-I*d*x^2]])-Sqrt[2*I*d*x^2+d^2*x^4]/(b*d*x*Sqrt[a+I*b*ArcSin[1-I*d*x^2]])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1164
  public void test0097() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(x^3*(c-a^2*c*x^2)^(5/2)), x]", //
        "-1/2*Sqrt[1-a^2*x^2]/(c^2*x^2*Sqrt[c-a^2*c*x^2])-a*Sqrt[1-a^2*x^2]/(c^2*x*Sqrt[c-a^2*c*x^2])+1/8*a^2*Sqrt[1-a^2*x^2]/(c^2*(1-a*x)^2*Sqrt[c-a^2*c*x^2])+a^2*Sqrt[1-a^2*x^2]/(c^2*(1-a*x)*Sqrt[c-a^2*c*x^2])+1/8*a^2*Sqrt[1-a^2*x^2]/(c^2*(1+a*x)*Sqrt[c-a^2*c*x^2])+3*a^2*Log[x]*Sqrt[1-a^2*x^2]/(c^2*Sqrt[c-a^2*c*x^2])-39/16*a^2*Log[1-a*x]*Sqrt[1-a^2*x^2]/(c^2*Sqrt[c-a^2*c*x^2])-9/16*a^2*Log[1+a*x]*Sqrt[1-a^2*x^2]/(c^2*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:133
  public void test0098() {
    check( //
        "Integrate[x^8*(a+b*ArcTanh[c*x^3]), x]", //
        "1/18*b*x^6/c+1/9*x^9*(a+b*ArcTanh[c*x^3])+1/18*b*Log[1-c^2*x^6]/c^3");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:460
  public void test0099() {
    check( //
        "Integrate[x^3*ArcTanh[a*x]^2/(1-a^2*x^2)^(1/2), x]", //
        "-10/3*ArcTan[Sqrt[1-a*x]/Sqrt[1+a*x]]*ArcTanh[a*x]/a^4-5/3*I*PolyLog[2,-I*Sqrt[1-a*x]/Sqrt[1+a*x]]/a^4+5/3*I*PolyLog[2,I*Sqrt[1-a*x]/Sqrt[1+a*x]]/a^4-1/3*Sqrt[1-a^2*x^2]/a^4-1/3*x*ArcTanh[a*x]*Sqrt[1-a^2*x^2]/a^3-2/3*ArcTanh[a*x]^2*Sqrt[1-a^2*x^2]/a^4-1/3*x^2*ArcTanh[a*x]^2*Sqrt[1-a^2*x^2]/a^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1513
  public void test0100() {
    check( //
        "Integrate[1/(E^(2*ArcTanh[a*x])*(c-a^2*c*x^2)^(1/2)), x]", //
        "-ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]/(a*Sqrt[c])-2*(1-a*x)/(a*Sqrt[c-a^2*c*x^2])");
  }

  // 7.5.1 u (a+b arcsech(c x))^n.input:140
  public void test0101() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*ArcSech[c*x]), x]", //
        "d^2*x*(a+b*ArcSech[c*x])+2/3*d*e*x^3*(a+b*ArcSech[c*x])+1/5*e^2*x^5*(a+b*ArcSech[c*x])+1/120*b*(120*c^4*d^2+40*c^2*d*e+9*e^2)*ArcSin[c*x]*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]/c^5-1/120*b*e*(40*c^2*d+9*e)*x*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]*Sqrt[1-c^2*x^2]/c^4-1/20*b*e^2*x^3*Sqrt[1/(1+c*x)]*Sqrt[1+c*x]*Sqrt[1-c^2*x^2]/c^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:485
  public void test0102() {
    check( //
        "Integrate[Sqrt[c-a*c*x]/(E^ArcTanh[a*x]*x^4), x]", //
        "11/8*a^3*ArcTanh[Sqrt[c]*Sqrt[1-a^2*x^2]/Sqrt[c-a*c*x]]*Sqrt[c]-1/3*c*Sqrt[1-a^2*x^2]/(x^3*Sqrt[c-a*c*x])+11/12*a*c*Sqrt[1-a^2*x^2]/(x^2*Sqrt[c-a*c*x])-11/8*a^2*c*Sqrt[1-a^2*x^2]/(x*Sqrt[c-a*c*x])");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:81
  public void test0103() {
    check( //
        "Integrate[x/ArcCosh[a*x]^4, x]", //
        "1/6/(a^2*ArcCosh[a*x]^2)-1/3*x^2/ArcCosh[a*x]^2+2/3*CoshIntegral[2*ArcCosh[a*x]]/a^2-1/3*x*Sqrt[-1+a*x]*Sqrt[1+a*x]/(a*ArcCosh[a*x]^3)-2/3*x*Sqrt[-1+a*x]*Sqrt[1+a*x]/(a*ArcCosh[a*x])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:357
  public void test0104() {
    check( //
        "Integrate[ArcTanh[1-d-d*Tanh[a+b*x]], x]", //
        "1/2*b*x^2+x*ArcTanh[1-d-d*Tanh[a+b*x]]-1/2*x*Log[1+E^(2*a+2*b*x)*(1-d)]-1/4*PolyLog[2,-E^(2*a+2*b*x)*(1-d)]/b");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1152
  public void test0105() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(x^2*(c-a^2*c*x^2)^(3/2)), x]", //
        "-Sqrt[1-a^2*x^2]/(c*x*Sqrt[c-a^2*c*x^2])+1/2*a*Sqrt[1-a^2*x^2]/(c*(1-a*x)*Sqrt[c-a^2*c*x^2])+a*Log[x]*Sqrt[1-a^2*x^2]/(c*Sqrt[c-a^2*c*x^2])-5/4*a*Log[1-a*x]*Sqrt[1-a^2*x^2]/(c*Sqrt[c-a^2*c*x^2])+1/4*a*Log[1+a*x]*Sqrt[1-a^2*x^2]/(c*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:27
  public void test0106() {
    check( //
        "Integrate[(d+c*d*x)^2*(a+b*ArcTanh[c*x])/x^3, x]", //
        "-1/2*b*c*d^2/x+1/2*b*c^2*d^2*ArcTanh[c*x]-1/2*d^2*(a+b*ArcTanh[c*x])/x^2-2*c*d^2*(a+b*ArcTanh[c*x])/x+a*c^2*d^2*Log[x]+2*b*c^2*d^2*Log[x]-b*c^2*d^2*Log[1-c^2*x^2]-1/2*b*c^2*d^2*PolyLog[2,-c*x]+1/2*b*c^2*d^2*PolyLog[2,c*x]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1537
  public void test0107() {
    check( //
        "Integrate[1/(E^(3*ArcTanh[a*x])*(c-a^2*c*x^2)^3), x]", //
        "8/35*x/(c^3*Sqrt[1-a^2*x^2])+(-1/7)/(a*c^3*(1+a*x)^3*Sqrt[1-a^2*x^2])+(-4/35)/(a*c^3*(1+a*x)^2*Sqrt[1-a^2*x^2])+(-4/35)/(a*c^3*(1+a*x)*Sqrt[1-a^2*x^2])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:292
  public void test0108() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])*(c-a*c*x)^(5/2), x]", //
        "-18/35*(1+1/(a*x))^(5/2)*(c-a*c*x)^(5/2)/(a*(1+(-1)/(a*x))^(5/2))+2/7*(1+1/(a*x))^(5/2)*x*(c-a*c*x)^(5/2)/(1+(-1)/(a*x))^(5/2)");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:589
  public void test0109() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*ArcCosh[c*x])/x^4, x]", //
        "-1/3*d*(a+b*ArcCosh[c*x])/x^3-e*(a+b*ArcCosh[c*x])/x+1/6*b*c*(c^2*d+6*e)*ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]+1/6*b*c*d*Sqrt[-1+c*x]*Sqrt[1+c*x]/x^2");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:133
  public void test0110() {
    check( //
        "Integrate[1/(x*ArcTanh[Tanh[a+b*x]]^3), x]", //
        "(-1/2)/((b*x-ArcTanh[Tanh[a+b*x]])*ArcTanh[Tanh[a+b*x]]^2)+1/((b*x-ArcTanh[Tanh[a+b*x]])^2*ArcTanh[Tanh[a+b*x]])-Log[x]/(b*x-ArcTanh[Tanh[a+b*x]])^3+Log[ArcTanh[Tanh[a+b*x]]]/(b*x-ArcTanh[Tanh[a+b*x]])^3");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:624
  public void test0111() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*(c-c/(a*x))^(3/2)), x]", //
        "-(1-a*x)^(3/2)*ArcSinh[Sqrt[a]*Sqrt[x]]/(a^(5/2)*(c-c/(a*x))^(3/2)*x^(3/2))+(1-a*x)^(3/2)*ArcTanh[Sqrt[2]*Sqrt[a]*Sqrt[x]/Sqrt[1+a*x]]*Sqrt[2]/(a^(5/2)*(c-c/(a*x))^(3/2)*x^(3/2))-(1-a*x)^(3/2)*Sqrt[1+a*x]/(a^2*(c-c/(a*x))^(3/2)*x)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1682
  public void test0112() {
    check( //
        "Integrate[E^(2*p*ArcTanh[a*x])*(c-a^2*c*x^2)^p, x]", //
        "(1+a*x)^(1+2*p)*(c-a^2*c*x^2)^p/(a*(1+2*p)*(1-a^2*x^2)^p)");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:25
  public void test0113() {
    check( //
        "Integrate[x^2*ArcCosh[a*x]^2, x]", //
        "4/9*x/a^2+2/27*x^3+1/3*x^3*ArcCosh[a*x]^2-4/9*ArcCosh[a*x]*Sqrt[-1+a*x]*Sqrt[1+a*x]/a^3-2/9*x^2*ArcCosh[a*x]*Sqrt[-1+a*x]*Sqrt[1+a*x]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:347
  public void test0114() {
    check( //
        "Integrate[E^ArcTanh[a*x]*(c-a*c*x)/x^2, x]", //
        "-a*c*ArcSin[a*x]-c*Sqrt[1-a^2*x^2]/x");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:300
  public void test0115() {
    check( //
        "Integrate[ArcTanh[a*x]^2/(x^2*(1-a^2*x^2)), x]", //
        "a*ArcTanh[a*x]^2-ArcTanh[a*x]^2/x+1/3*a*ArcTanh[a*x]^3+2*a*ArcTanh[a*x]*Log[2+(-2)/(1+a*x)]-a*PolyLog[2,-1+2/(1+a*x)]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:443
  public void test0116() {
    check( //
        "Integrate[E^ArcTanh[x]/(1-x)^(1/2), x]", //
        "2*ArcTanh[Sqrt[1+x]/Sqrt[2]]*Sqrt[2]-2*Sqrt[1+x]");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:184
  public void test0117() {
    check( //
        "Integrate[1/ArcTanh[Tanh[a+b*x]]^(3/2), x]", //
        "(-2)/(b*Sqrt[ArcTanh[Tanh[a+b*x]]])");
  }

  // 7.1.2 (d x)^m (a+b arcsinh(c x))^n.input:115
  public void test0118() {
    check( //
        "Integrate[1/Sqrt[ArcSinh[a*x]], x]", //
        "1/2*Erf[Sqrt[ArcSinh[a*x]]]*Sqrt[Pi]/a+1/2*Erfi[Sqrt[ArcSinh[a*x]]]*Sqrt[Pi]/a");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:163
  public void test0119() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^(5/2)/x, x]", //
        "-2*ArcTan[Sqrt[ArcTanh[Tanh[a+b*x]]]/Sqrt[b*x-ArcTanh[Tanh[a+b*x]]]]*(b*x-ArcTanh[Tanh[a+b*x]])^(5/2)-2/3*(b*x-ArcTanh[Tanh[a+b*x]])*ArcTanh[Tanh[a+b*x]]^(3/2)+2/5*ArcTanh[Tanh[a+b*x]]^(5/2)+2*(b*x-ArcTanh[Tanh[a+b*x]])^2*Sqrt[ArcTanh[Tanh[a+b*x]]]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:295
  public void test0120() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])/Sqrt[c-a*c*x], x]", //
        "c^2*(1-a^2*x^2)^(3/2)/(a*(c-a*c*x)^(5/2))-3*ArcTanh[Sqrt[c]*Sqrt[1-a^2*x^2]/(Sqrt[2]*Sqrt[c-a*c*x])]*Sqrt[2]/(a*Sqrt[c])+3*Sqrt[1-a^2*x^2]/(a*Sqrt[c-a*c*x])");
  }

  // 7.6.1 u (a+b arccsch(c x))^n.input:172
  public void test0121() {
    check( //
        "Integrate[x*(d+e*x^2)^(3/2)*(a+b*ArcCsch[c*x]), x]", //
        "1/5*(d+e*x^2)^(5/2)*(a+b*ArcCsch[c*x])/e+1/5*b*c*d^(5/2)*x*ArcTan[Sqrt[d+e*x^2]/(Sqrt[d]*Sqrt[-1-c^2*x^2])]/(e*Sqrt[-c^2*x^2])+1/40*b*(15*c^4*d^2-10*c^2*d*e+3*e^2)*x*ArcTan[Sqrt[e]*Sqrt[-1-c^2*x^2]/(c*Sqrt[d+e*x^2])]/(c^4*Sqrt[e]*Sqrt[-c^2*x^2])+1/20*b*x*(d+e*x^2)^(3/2)*Sqrt[-1-c^2*x^2]/(c*Sqrt[-c^2*x^2])+1/40*b*(7*c^2*d-3*e)*x*Sqrt[-1-c^2*x^2]*Sqrt[d+e*x^2]/(c^3*Sqrt[-c^2*x^2])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:250
  public void test0122() {
    check( //
        "Integrate[1/(x^(5/2)*ArcTanh[Tanh[a+b*x]]^3), x]", //
        "-35/4*b^(3/2)*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[b*x-ArcTanh[Tanh[a+b*x]]]]/(b*x-ArcTanh[Tanh[a+b*x]])^(9/2)+35/12/(x^(3/2)*(b*x-ArcTanh[Tanh[a+b*x]])^3)+7/4/(b*x^(5/2)*(b*x-ArcTanh[Tanh[a+b*x]])^2)+5/4/(b^2*x^(7/2)*(b*x-ArcTanh[Tanh[a+b*x]]))+(-1/2)/(b*x^(5/2)*ArcTanh[Tanh[a+b*x]]^2)+5/4/(b^2*x^(7/2)*ArcTanh[Tanh[a+b*x]])+35/4*b/((b*x-ArcTanh[Tanh[a+b*x]])^4*Sqrt[x])");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:198
  public void test0123() {
    check( //
        "Integrate[x^3*(a+b*ArcTanh[c/x])^2, x]", //
        "1/12*b^2*c^2*x^2+1/2*b*c^3*x*(a+b*ArcCoth[x/c])+1/6*b*c*x^3*(a+b*ArcCoth[x/c])-1/4*c^4*(a+b*ArcCoth[x/c])^2+1/4*x^4*(a+b*ArcCoth[x/c])^2+1/3*b^2*c^4*Log[1-c^2/x^2]+2/3*b^2*c^4*Log[x]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:708
  public void test0124() {
    check( //
        "Integrate[1/(E^(3*ArcCoth[a*x])*(c-a^2*c*x^2)^4), x]", //
        "16/63/(E^(3*ArcCoth[a*x])*a*c^4)+1/9*(1+2*a*x)/(E^(3*ArcCoth[a*x])*a*c^4*(1-a^2*x^2)^3)+10/63*(3+4*a*x)/(E^(3*ArcCoth[a*x])*a*c^4*(1-a^2*x^2)^2)-8/21*(3+2*a*x)/(E^(3*ArcCoth[a*x])*a*c^4*(1-a^2*x^2))");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:856
  public void test0125() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^m*Sqrt[c-c/(a^2*x^2)], x]", //
        "x^(1+m)*Sqrt[c-c/(a^2*x^2)]/(m*Sqrt[1-a^2*x^2])+a*x^(2+m)*Sqrt[c-c/(a^2*x^2)]/((1+m)*Sqrt[1-a^2*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1448
  public void test0126() {
    check( //
        "Integrate[x^m*(c-a^2*c*x^2)^(1/2)/E^ArcTanh[a*x], x]", //
        "x^(1+m)*Sqrt[c-a^2*c*x^2]/((1+m)*Sqrt[1-a^2*x^2])-a*x^(2+m)*Sqrt[c-a^2*c*x^2]/((2+m)*Sqrt[1-a^2*x^2])");
  }

  // 7.5.2 Inverse hyperbolic secant functions.input:67
  public void test0127() {
    check( //
        "Integrate[E^ArcSech[a*x^2]*x^4, x]", //
        "2/15*x^3/a+1/5*E^ArcSech[a*x^2]*x^5+2/5*EllipticE[ArcSin[x*Sqrt[a]],-1]*Sqrt[1/(1+a*x^2)]*Sqrt[1+a*x^2]/a^(5/2)-2/5*EllipticF[ArcSin[x*Sqrt[a]],-1]*Sqrt[1/(1+a*x^2)]*Sqrt[1+a*x^2]/a^(5/2)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:279
  public void test0128() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(c-a*c*x)^(3/2), x]", //
        "-ArcTanh[Sqrt[c]*Sqrt[1-a^2*x^2]/(Sqrt[2]*Sqrt[c-a*c*x])]/(a*c^(3/2)*Sqrt[2])+Sqrt[1-a^2*x^2]/(a*(c-a*c*x)^(3/2))");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:172
  public void test0129() {
    check( //
        "Integrate[x^3*(a+b*ArcSinh[c*x])/Sqrt[d+c^2*d*x^2], x]", //
        "2/3*b*x*Sqrt[1+c^2*x^2]/(c^3*Sqrt[d+c^2*d*x^2])-1/9*b*x^3*Sqrt[1+c^2*x^2]/(c*Sqrt[d+c^2*d*x^2])-2/3*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/(c^4*d)+1/3*x^2*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/(c^2*d)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:16
  public void test0130() {
    check( //
        "Integrate[E^ArcTanh[a*x], x]", //
        "ArcSin[a*x]/a-Sqrt[1-a^2*x^2]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:210
  public void test0131() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])*(c-a*c*x)^4, x]", //
        "-1/2*c^4*(1-a*x)^4/a+1/5*c^4*(1-a*x)^5/a");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:25
  public void test0132() {
    check( //
        "Integrate[(a+b*ArcSinh[c*x])/(d+e*x), x]", //
        "-1/2*(a+b*ArcSinh[c*x])^2/(b*e)+(a+b*ArcSinh[c*x])*Log[1+E^ArcSinh[c*x]*e/(c*d-Sqrt[c^2*d^2+e^2])]/e+(a+b*ArcSinh[c*x])*Log[1+E^ArcSinh[c*x]*e/(c*d+Sqrt[c^2*d^2+e^2])]/e+b*PolyLog[2,-E^ArcSinh[c*x]*e/(c*d-Sqrt[c^2*d^2+e^2])]/e+b*PolyLog[2,-E^ArcSinh[c*x]*e/(c*d+Sqrt[c^2*d^2+e^2])]/e");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:242
  public void test0133() {
    check( //
        "Integrate[x^3/ArcCoth[Tanh[a+b*x]]^3, x]", //
        "3*x/b^3-1/2*x^3/(b*ArcCoth[Tanh[a+b*x]]^2)-3/2*x^2/(b^2*ArcCoth[Tanh[a+b*x]])+3*(b*x-ArcCoth[Tanh[a+b*x]])*Log[ArcCoth[Tanh[a+b*x]]]/b^4");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:734
  public void test0134() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])*(c-a^2*c*x^2)^(5/2), x]", //
        "-2/5*(1+a*x)^5*(c-a^2*c*x^2)^(5/2)/(a^6*(1+(-1)/(a^2*x^2))^(5/2)*x^5)+1/6*(1+a*x)^6*(c-a^2*c*x^2)^(5/2)/(a^6*(1+(-1)/(a^2*x^2))^(5/2)*x^5)");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:164
  public void test0135() {
    check( //
        "Integrate[x*(a+b*ArcCosh[c*x]), x]", //
        "-1/4*b*ArcCosh[c*x]/c^2+1/2*x^2*(a+b*ArcCosh[c*x])-1/4*b*x*Sqrt[-1+c*x]*Sqrt[1+c*x]/c");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:822
  public void test0136() {
    check( //
        "Integrate[Sqrt[c-a^2*c*x^2]/E^(2*ArcCoth[a*x]), x]", //
        "-3/2*ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]*Sqrt[c]/a-3/2*Sqrt[c-a^2*c*x^2]/a-1/2*(1-a*x)*Sqrt[c-a^2*c*x^2]/a");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:265
  public void test0137() {
    check( //
        "Integrate[(a+b*ArcTanh[c*Sqrt[x]])^2, x]", //
        "-(a+b*ArcTanh[c*Sqrt[x]])^2/c^2+x*(a+b*ArcTanh[c*Sqrt[x]])^2+b^2*Log[1-c^2*x]/c^2+2*a*b*Sqrt[x]/c+2*b^2*ArcTanh[c*Sqrt[x]]*Sqrt[x]/c");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:736
  public void test0138() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])*Sqrt[c-a^2*c*x^2], x]", //
        "3*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1+(-1)/(a^2*x^2)])+1/2*x*Sqrt[c-a^2*c*x^2]/Sqrt[1+(-1)/(a^2*x^2)]+4*Log[1-a*x]*Sqrt[c-a^2*c*x^2]/(a^2*x*Sqrt[1+(-1)/(a^2*x^2)])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1453
  public void test0139() {
    check( //
        "Integrate[(c-a^2*c*x^2)^(1/2)/(E^ArcTanh[a*x]*x^2), x]", //
        "-Sqrt[c-a^2*c*x^2]/(x*Sqrt[1-a^2*x^2])-a*Log[x]*Sqrt[c-a^2*c*x^2]/Sqrt[1-a^2*x^2]");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:41
  public void test0140() {
    check( //
        "Integrate[1/(a+b*ArcSinh[c*x]), x]", //
        "CoshIntegral[(a+b*ArcSinh[c*x])/b]*Cosh[a/b]/(b*c)-SinhIntegral[(a+b*ArcSinh[c*x])/b]*Sinh[a/b]/(b*c)");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:455
  public void test0141() {
    check( //
        "Integrate[x*ArcTanh[a*x]/(1-a^2*x^2)^(1/2), x]", //
        "ArcSin[a*x]/a^2-ArcTanh[a*x]*Sqrt[1-a^2*x^2]/a^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:224
  public void test0142() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])/(c-a*c*x)^2, x]", //
        "-1/5*a^4*(1+(-1)/(a^2*x^2))^(5/2)/(c^2*(a+(-1)/x)^5)");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:211
  public void test0143() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/x^3, x]", //
        "-1/2*(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/x^2-b*c*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]/(x*Sqrt[-1+c*x]*Sqrt[1+c*x])+c^2*(a+b*ArcCosh[c*x])^2*ArcTan[E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+b^2*c^2*ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-I*b*c^2*(a+b*ArcCosh[c*x])*PolyLog[2,-I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+I*b*c^2*(a+b*ArcCosh[c*x])*PolyLog[2,I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])+I*b^2*c^2*PolyLog[3,-I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-I*b^2*c^2*PolyLog[3,I*E^ArcCosh[c*x]]*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:397
  public void test0144() {
    check( //
        "Integrate[x^4*ArcSinh[a*x]^3/Sqrt[1+a^2*x^2], x]", //
        "45/128*x^2/a^3-3/128*x^4/a+45/128*ArcSinh[a*x]^2/a^5+9/16*x^2*ArcSinh[a*x]^2/a^3-3/16*x^4*ArcSinh[a*x]^2/a+3/32*ArcSinh[a*x]^4/a^5-45/64*x*ArcSinh[a*x]*Sqrt[1+a^2*x^2]/a^4+3/32*x^3*ArcSinh[a*x]*Sqrt[1+a^2*x^2]/a^2-3/8*x*ArcSinh[a*x]^3*Sqrt[1+a^2*x^2]/a^4+1/4*x^3*ArcSinh[a*x]^3*Sqrt[1+a^2*x^2]/a^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:731
  public void test0145() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])/(c-a^2*c*x^2)^(9/2), x]", //
        "-2/9*(1+a*x)/(a*(c-a^2*c*x^2)^(9/2))-1/9*x/(c*(c-a^2*c*x^2)^(7/2))-2/15*x/(c^2*(c-a^2*c*x^2)^(5/2))-8/45*x/(c^3*(c-a^2*c*x^2)^(3/2))-16/45*x/(c^4*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:202
  public void test0146() {
    check( //
        "Integrate[E^ArcTanh[a*x]*(c-a*c*x), x]", //
        "1/2*c*ArcSin[a*x]/a+1/2*c*x*Sqrt[1-a^2*x^2]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:281
  public void test0147() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(c-a*c*x)^(7/2), x]", //
        "-1/32*ArcTanh[Sqrt[c]*Sqrt[1-a^2*x^2]/(Sqrt[2]*Sqrt[c-a*c*x])]/(a*c^(7/2)*Sqrt[2])+1/3*Sqrt[1-a^2*x^2]/(a*(c-a*c*x)^(7/2))-1/24*Sqrt[1-a^2*x^2]/(a*c*(c-a*c*x)^(5/2))-1/32*Sqrt[1-a^2*x^2]/(a*c^2*(c-a*c*x)^(3/2))");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:202
  public void test0148() {
    check( //
        "Integrate[(a+b*ArcTanh[c/x])^2/x, x]", //
        "-2*(a+b*ArcCoth[x/c])^2*ArcTanh[1+(-2)/(1-c/x)]+b*(a+b*ArcCoth[x/c])*PolyLog[2,1+(-2)/(1-c/x)]-b*(a+b*ArcCoth[x/c])*PolyLog[2,-1+2/(1-c/x)]-1/2*b^2*PolyLog[3,1+(-2)/(1-c/x)]+1/2*b^2*PolyLog[3,-1+2/(1-c/x)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:916
  public void test0149() {
    check( //
        "Integrate[E^(4*ArcCoth[a*x])*(c-c/(a^2*x^2))^5, x]", //
        "1/9*c^5/(a^10*x^9)+1/2*c^5/(a^9*x^8)+3/7*c^5/(a^8*x^7)-4/3*c^5/(a^7*x^6)-14/5*c^5/(a^6*x^5)+14/3*c^5/(a^4*x^3)+4*c^5/(a^3*x^2)-3*c^5/(a^2*x)+c^5*x+4*c^5*Log[x]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1111
  public void test0150() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^6/(1-a^2*x^2)^(5/2), x]", //
        "-x/a^6-1/2*x^2/a^5+1/8/(a^7*(1-a*x)^2)+(-5/4)/(a^7*(1-a*x))+(-1/8)/(a^7*(1+a*x))-39/16*Log[1-a*x]/a^7-9/16*Log[1+a*x]/a^7");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:350
  public void test0151() {
    check( //
        "Integrate[ArcCoth[1+I*d+d*Cot[a+b*x]], x]", //
        "1/2*I*b*x^2+x*ArcCoth[1+I*d+d*Cot[a+b*x]]-1/2*x*Log[1-E^(2*I*a+2*I*b*x)*(1+I*d)]+1/4*I*PolyLog[2,E^(2*I*a+2*I*b*x)*(1+I*d)]/b");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:35
  public void test0152() {
    check( //
        "Integrate[(d+c*d*x)^3*(a+b*ArcTanh[c*x])/x, x]", //
        "3*a*c*d^3*x+3/2*b*c*d^3*x+1/6*b*c^2*d^3*x^2-3/2*b*d^3*ArcTanh[c*x]+3*b*c*d^3*x*ArcTanh[c*x]+3/2*c^2*d^3*x^2*(a+b*ArcTanh[c*x])+1/3*c^3*d^3*x^3*(a+b*ArcTanh[c*x])+a*d^3*Log[x]+5/3*b*d^3*Log[1-c^2*x^2]-1/2*b*d^3*PolyLog[2,-c*x]+1/2*b*d^3*PolyLog[2,c*x]");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:334
  public void test0153() {
    check( //
        "Integrate[x*ArcCoth[1+I*d-d*Tan[a+b*x]], x]", //
        "1/6*I*b*x^3+1/2*x^2*ArcCoth[1+I*d-d*Tan[a+b*x]]-1/4*x^2*Log[1+E^(2*I*a+2*I*b*x)*(1+I*d)]+1/4*I*x*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1+I*d)]/b-1/8*PolyLog[3,-E^(2*I*a+2*I*b*x)*(1+I*d)]/b^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:752
  public void test0154() {
    check( //
        "Integrate[(c-a^2*c*x^2)^(5/2)/E^(2*ArcCoth[a*x]), x]", //
        "-7/24*c*x*(c-a^2*c*x^2)^(3/2)-7/30*(c-a^2*c*x^2)^(5/2)/a-1/6*(1-a*x)*(c-a^2*c*x^2)^(5/2)/a-7/16*c^(5/2)*ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]/a-7/16*c^2*x*Sqrt[c-a^2*c*x^2]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1536
  public void test0155() {
    check( //
        "Integrate[1/(E^(3*ArcTanh[a*x])*(c-a^2*c*x^2)^2), x]", //
        "-1/5*Sqrt[1-a^2*x^2]/(a*c^2*(1+a*x)^3)-2/15*Sqrt[1-a^2*x^2]/(a*c^2*(1+a*x)^2)-2/15*Sqrt[1-a^2*x^2]/(a*c^2*(1+a*x))");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:574
  public void test0156() {
    check( //
        "Integrate[(c-c/(a*x))/E^(2*ArcTanh[a*x]), x]", //
        "-c*x-c*Log[x]/a+4*c*Log[1+a*x]/a");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:456
  public void test0157() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-c/(a*x))^5, x]", //
        "-1/4*c^5/(a^5*x^4)+c^5/(a^4*x^3)-c^5/(a^3*x^2)-2*c^5/(a^2*x)+c^5*x-3*c^5*Log[x]/a");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:193
  public void test0158() {
    check( //
        "Integrate[a+b*ArcTanh[c/x], x]", //
        "a*x+b*x*ArcTanh[c/x]+1/2*b*c*Log[c^2-x^2]");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1579
  public void test0159() {
    check( //
        "Integrate[E^(1/2*ArcTanh[a*x])/(1-a^2*x^2)^(5/2), x]", //
        "-2/35*E^(1/2*ArcTanh[a*x])*(1-6*a*x)/(a*(1-a^2*x^2)^(3/2))-16/35*E^(1/2*ArcTanh[a*x])*(1-2*a*x)/(a*Sqrt[1-a^2*x^2])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:28
  public void test0160() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])/x^4, x]", //
        "1/3/x^3+a/x^2+2*a^2/x-2*a^3*Log[x]+2*a^3*Log[1-a*x]");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:12
  public void test0161() {
    check( //
        "Integrate[ArcSinh[c*x]/(d+e*x), x]", //
        "-1/2*ArcSinh[c*x]^2/e+ArcSinh[c*x]*Log[1+E^ArcSinh[c*x]*e/(c*d-Sqrt[c^2*d^2+e^2])]/e+ArcSinh[c*x]*Log[1+E^ArcSinh[c*x]*e/(c*d+Sqrt[c^2*d^2+e^2])]/e+PolyLog[2,-E^ArcSinh[c*x]*e/(c*d-Sqrt[c^2*d^2+e^2])]/e+PolyLog[2,-E^ArcSinh[c*x]*e/(c*d+Sqrt[c^2*d^2+e^2])]/e");
  }

  // 7.2.2 (d x)^m (a+b arccosh(c x))^n.input:75
  public void test0162() {
    check( //
        "Integrate[1/ArcCosh[a*x]^3, x]", //
        "-1/2*x/ArcCosh[a*x]+1/2*SinhIntegral[ArcCosh[a*x]]/a-1/2*Sqrt[-1+a*x]*Sqrt[1+a*x]/(a*ArcCosh[a*x]^2)");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:923
  public void test0163() {
    check( //
        "Integrate[E^(4*ArcCoth[a*x])/(c-c/(a^2*x^2))^3, x]", //
        "x/c^3+(-1/8)/(a*c^3*(1-a*x)^4)+11/12/(a*c^3*(1-a*x)^3)+(-49/16)/(a*c^3*(1-a*x)^2)+111/16/(a*c^3*(1-a*x))+129/32*Log[1-a*x]/(a*c^3)-1/32*Log[1+a*x]/(a*c^3)");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:409
  public void test0164() {
    check( //
        "Integrate[(c+a^2*c*x^2)^3/ArcSinh[a*x], x]", //
        "35/64*c^3*CoshIntegral[ArcSinh[a*x]]/a+21/64*c^3*CoshIntegral[3*ArcSinh[a*x]]/a+7/64*c^3*CoshIntegral[5*ArcSinh[a*x]]/a+1/64*c^3*CoshIntegral[7*ArcSinh[a*x]]/a");
  }

  // 7.2.5 Inverse hyperbolic cosine functions.input:88
  public void test0165() {
    check( //
        "Integrate[(f+g*x)*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2], x]", //
        "1/2*f*x*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]-1/3*g*(1-c*x)*(1+c*x)*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]/c^2+1/3*b*g*x*Sqrt[d-c^2*d*x^2]/(c*Sqrt[-1+c*x]*Sqrt[1+c*x])-1/4*b*c*f*x^2*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-1/9*b*c*g*x^3*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-1/4*f*(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c*Sqrt[-1+c*x]*Sqrt[1+c*x])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:947
  public void test0166() {
    check( //
        "Integrate[1/(E^(3*ArcCoth[a*x])*(c-c/(a^2*x^2))), x]", //
        "-3*ArcTanh[Sqrt[1+(-1)/(a*x)]*Sqrt[1+1/(a*x)]]/(a*c)+5/3*Sqrt[1+(-1)/(a*x)]/(a*c*(1+1/(a*x))^(3/2))+x*Sqrt[1+(-1)/(a*x)]/(c*(1+1/(a*x))^(3/2))+14/3*Sqrt[1+(-1)/(a*x)]/(a*c*Sqrt[1+1/(a*x)])");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:82
  public void test0167() {
    check( //
        "Integrate[(f+g*x)^3*(a+b*ArcSinh[c*x])/Sqrt[d+c^2*d*x^2], x]", //
        "3*f^2*g*(1+c^2*x^2)*(a+b*ArcSinh[c*x])/(c^2*Sqrt[d+c^2*d*x^2])-2/3*g^3*(1+c^2*x^2)*(a+b*ArcSinh[c*x])/(c^4*Sqrt[d+c^2*d*x^2])+3/2*f*g^2*x*(1+c^2*x^2)*(a+b*ArcSinh[c*x])/(c^2*Sqrt[d+c^2*d*x^2])+1/3*g^3*x^2*(1+c^2*x^2)*(a+b*ArcSinh[c*x])/(c^2*Sqrt[d+c^2*d*x^2])-3*b*f^2*g*x*Sqrt[1+c^2*x^2]/(c*Sqrt[d+c^2*d*x^2])+2/3*b*g^3*x*Sqrt[1+c^2*x^2]/(c^3*Sqrt[d+c^2*d*x^2])-3/4*b*f*g^2*x^2*Sqrt[1+c^2*x^2]/(c*Sqrt[d+c^2*d*x^2])-1/9*b*g^3*x^3*Sqrt[1+c^2*x^2]/(c*Sqrt[d+c^2*d*x^2])+1/2*f^3*(a+b*ArcSinh[c*x])^2*Sqrt[1+c^2*x^2]/(b*c*Sqrt[d+c^2*d*x^2])-3/4*f*g^2*(a+b*ArcSinh[c*x])^2*Sqrt[1+c^2*x^2]/(b*c^3*Sqrt[d+c^2*d*x^2])");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:349
  public void test0168() {
    check( //
        "Integrate[x*ArcCoth[1+I*d+d*Cot[a+b*x]], x]", //
        "1/6*I*b*x^3+1/2*x^2*ArcCoth[1+I*d+d*Cot[a+b*x]]-1/4*x^2*Log[1-E^(2*I*a+2*I*b*x)*(1+I*d)]+1/4*I*x*PolyLog[2,E^(2*I*a+2*I*b*x)*(1+I*d)]/b-1/8*PolyLog[3,E^(2*I*a+2*I*b*x)*(1+I*d)]/b^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1372
  public void test0169() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])*(c-a^2*c*x^2)^(1/2), x]", //
        "-3*x*Sqrt[c-a^2*c*x^2]/Sqrt[1-a^2*x^2]-1/2*a*x^2*Sqrt[c-a^2*c*x^2]/Sqrt[1-a^2*x^2]-4*Log[1-a*x]*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:37
  public void test0170() {
    check( //
        "Integrate[x*(a+b*ArcTanh[c*x])^3, x]", //
        "3/2*b*(a+b*ArcTanh[c*x])^2/c^2+3/2*b*x*(a+b*ArcTanh[c*x])^2/c-1/2*(a+b*ArcTanh[c*x])^3/c^2+1/2*x^2*(a+b*ArcTanh[c*x])^3-3*b^2*(a+b*ArcTanh[c*x])*Log[2/(1-c*x)]/c^2-3/2*b^3*PolyLog[2,1+(-2)/(1-c*x)]/c^2");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:255
  public void test0171() {
    check( //
        "Integrate[x*(1-a^2*x^2)^2*ArcTanh[a*x]^2, x]", //
        "2/45*(1-a^2*x^2)/a^2+1/60*(1-a^2*x^2)^2/a^2+8/45*x*ArcTanh[a*x]/a+4/45*x*(1-a^2*x^2)*ArcTanh[a*x]/a+1/15*x*(1-a^2*x^2)^2*ArcTanh[a*x]/a-1/6*(1-a^2*x^2)^3*ArcTanh[a*x]^2/a^2+4/45*Log[1-a^2*x^2]/a^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:724
  public void test0172() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-a^2*c*x^2)^(5/2), x]", //
        "-7/24*c*x*(c-a^2*c*x^2)^(3/2)+7/30*(c-a^2*c*x^2)^(5/2)/a+1/6*(1+a*x)*(c-a^2*c*x^2)^(5/2)/a-7/16*c^(5/2)*ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]/a-7/16*c^2*x*Sqrt[c-a^2*c*x^2]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:315
  public void test0173() {
    check( //
        "Integrate[1/(E^(2*ArcCoth[a*x])*(c-a*c*x)^(3/2)), x]", //
        "ArcTanh[Sqrt[c-a*c*x]/(Sqrt[2]*Sqrt[c])]*Sqrt[2]/(a*c^(3/2))");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:186
  public void test0174() {
    check( //
        "Integrate[a+b*ArcSinh[c+d*x], x]", //
        "a*x+b*(c+d*x)*ArcSinh[c+d*x]/d-b*Sqrt[1+(c+d*x)^2]/d");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:626
  public void test0175() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*(c-c/(a*x))^(7/2)), x]", //
        "-5*(1-a*x)^(7/2)*ArcSinh[Sqrt[a]*Sqrt[x]]/(a^(9/2)*(c-c/(a*x))^(7/2)*x^(7/2))+115/16*(1-a*x)^(7/2)*ArcTanh[Sqrt[2]*Sqrt[a]*Sqrt[x]/Sqrt[1+a*x]]/(a^(9/2)*(c-c/(a*x))^(7/2)*x^(7/2)*Sqrt[2])+1/4*(1-a*x)^(3/2)*Sqrt[1+a*x]/(a^2*(c-c/(a*x))^(7/2)*x)-15/16*(1-a*x)^(5/2)*Sqrt[1+a*x]/(a^3*(c-c/(a*x))^(7/2)*x^2)-35/16*(1-a*x)^(7/2)*Sqrt[1+a*x]/(a^4*(c-c/(a*x))^(7/2)*x^3)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:290
  public void test0176() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])*(c-a*c*x)^(9/2), x]", //
        "256/1155*c^7*(1-a^2*x^2)^(5/2)/(a*(c-a*c*x)^(5/2))+64/231*c^6*(1-a^2*x^2)^(5/2)/(a*(c-a*c*x)^(3/2))+8/33*c^5*(1-a^2*x^2)^(5/2)/(a*Sqrt[c-a*c*x])+2/11*c^4*(1-a^2*x^2)^(5/2)*Sqrt[c-a*c*x]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1514
  public void test0177() {
    check( //
        "Integrate[1/(E^(2*ArcTanh[a*x])*(c-a^2*c*x^2)^(3/2)), x]", //
        "-2/3*(1-a*x)/(a*(c-a^2*c*x^2)^(3/2))+1/3*x/(c*Sqrt[c-a^2*c*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1133
  public void test0178() {
    check( //
        "Integrate[E^ArcTanh[a*x]*(c-a^2*c*x^2)^(7/2), x]", //
        "8/5*c^3*(1+a*x)^5*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])-2*c^3*(1+a*x)^6*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])+6/7*c^3*(1+a*x)^7*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])-1/8*c^3*(1+a*x)^8*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1113
  public void test0179() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^4/(1-a^2*x^2)^(5/2), x]", //
        "1/8/(a^5*(1-a*x)^2)+(-3/4)/(a^5*(1-a*x))+(-1/8)/(a^5*(1+a*x))-11/16*Log[1-a*x]/a^5-5/16*Log[1+a*x]/a^5");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:937
  public void test0180() {
    check( //
        "Integrate[(c-c/(a^2*x^2))^2/E^(2*ArcCoth[a*x]), x]", //
        "1/3*c^2/(a^4*x^3)-c^2/(a^3*x^2)+c^2*x-2*c^2*Log[x]/a");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:295
  public void test0181() {
    check( //
        "Integrate[x^(1/2)/ArcTanh[Tanh[a+b*x]]^(3/2), x]", //
        "2*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[ArcTanh[Tanh[a+b*x]]]]/b^(3/2)-2*Sqrt[x]/(b*Sqrt[ArcTanh[Tanh[a+b*x]]])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:754
  public void test0182() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])/(c-c/(a^2*x^2))^3, x]", //
        "-1/7*(1+a*x)^3/(a*c^3*(1-a^2*x^2)^(7/2))+38/35*(1+a*x)^2/(a*c^3*(1-a^2*x^2)^(5/2))-137/35*(1+a*x)/(a*c^3*(1-a^2*x^2)^(3/2))-3*ArcSin[a*x]/(a*c^3)+1/35*(245+181*a*x)/(a*c^3*Sqrt[1-a^2*x^2])+Sqrt[1-a^2*x^2]/(a*c^3)");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:97
  public void test0183() {
    check( //
        "Integrate[x*(a+b*ArcTanh[c*x^2])^3, x]", //
        "1/2*(a+b*ArcTanh[c*x^2])^3/c+1/2*x^2*(a+b*ArcTanh[c*x^2])^3-3/2*b*(a+b*ArcTanh[c*x^2])^2*Log[2/(1-c*x^2)]/c-3/2*b^2*(a+b*ArcTanh[c*x^2])*PolyLog[2,1+(-2)/(1-c*x^2)]/c+3/4*b^3*PolyLog[3,1+(-2)/(1-c*x^2)]/c");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1109
  public void test0184() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(x^3*(1-a^2*x^2)^(3/2)), x]", //
        "(-1/2)/x^2-a/x+1/2*a^2/(1-a*x)+2*a^2*Log[x]-7/4*a^2*Log[1-a*x]-1/4*a^2*Log[1+a*x]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:602
  public void test0185() {
    check( //
        "Integrate[1/((1-a^2*x^2)^(7/2)*ArcTanh[a*x]^2), x]", //
        "(-1)/(a*(1-a^2*x^2)^(5/2)*ArcTanh[a*x])+5/8*SinhIntegral[ArcTanh[a*x]]/a+15/16*SinhIntegral[3*ArcTanh[a*x]]/a+5/16*SinhIntegral[5*ArcTanh[a*x]]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:826
  public void test0186() {
    check( //
        "Integrate[(c-c/(a^2*x^2))^(7/2)/E^ArcTanh[a*x], x]", //
        "-1/6*(c-c/(a^2*x^2))^(7/2)*x/(1-a^2*x^2)^(7/2)+1/5*a*(c-c/(a^2*x^2))^(7/2)*x^2/(1-a^2*x^2)^(7/2)+3/4*a^2*(c-c/(a^2*x^2))^(7/2)*x^3/(1-a^2*x^2)^(7/2)-a^3*(c-c/(a^2*x^2))^(7/2)*x^4/(1-a^2*x^2)^(7/2)-3/2*a^4*(c-c/(a^2*x^2))^(7/2)*x^5/(1-a^2*x^2)^(7/2)+3*a^5*(c-c/(a^2*x^2))^(7/2)*x^6/(1-a^2*x^2)^(7/2)+a^7*(c-c/(a^2*x^2))^(7/2)*x^8/(1-a^2*x^2)^(7/2)-a^6*(c-c/(a^2*x^2))^(7/2)*x^7*Log[x]/(1-a^2*x^2)^(7/2)");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:38
  public void test0187() {
    check( //
        "Integrate[x*ArcCoth[a*x]^3, x]", //
        "3/2*ArcCoth[a*x]^2/a^2+3/2*x*ArcCoth[a*x]^2/a-1/2*ArcCoth[a*x]^3/a^2+1/2*x^2*ArcCoth[a*x]^3-3*ArcCoth[a*x]*Log[2/(1-a*x)]/a^2-3/2*PolyLog[2,1+(-2)/(1-a*x)]/a^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:543
  public void test0188() {
    check( //
        "Integrate[Sqrt[c-c/(a*x)]/E^ArcCoth[a*x], x]", //
        "-3*ArcTanh[Sqrt[c]*Sqrt[1+(-1)/(a^2*x^2)]/Sqrt[c-c/(a*x)]]*Sqrt[c]/a+c*x*Sqrt[1+(-1)/(a^2*x^2)]/Sqrt[c-c/(a*x)]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:88
  public void test0189() {
    check( //
        "Integrate[(d+c*d*x)*(a+b*ArcTanh[c*x])^2, x]", //
        "a*b*d*x+b^2*d*x*ArcTanh[c*x]+1/2*d*(1+c*x)^2*(a+b*ArcTanh[c*x])^2/c-2*b*d*(a+b*ArcTanh[c*x])*Log[2/(1-c*x)]/c+1/2*b^2*d*Log[1-c^2*x^2]/c-b^2*d*PolyLog[2,1+(-2)/(1-c*x)]/c");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:465
  public void test0190() {
    check( //
        "Integrate[ArcTanh[a*x]^2/(x^2*(1-a^2*x^2)^(1/2)), x]", //
        "-4*a*ArcTanh[a*x]*ArcTanh[Sqrt[1-a*x]/Sqrt[1+a*x]]+2*a*PolyLog[2,-Sqrt[1-a*x]/Sqrt[1+a*x]]-2*a*PolyLog[2,Sqrt[1-a*x]/Sqrt[1+a*x]]-ArcTanh[a*x]^2*Sqrt[1-a^2*x^2]/x");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:1056
  public void test0191() {
    check( //
        "Integrate[Sqrt[c-c/(a^2*x^2)]/(E^(3*ArcCoth[a*x])*x), x]", //
        "-Sqrt[c-c/(a^2*x^2)]/(a*x*Sqrt[1+(-1)/(a^2*x^2)])-3*Log[x]*Sqrt[c-c/(a^2*x^2)]/Sqrt[1+(-1)/(a^2*x^2)]+4*Log[1+a*x]*Sqrt[c-c/(a^2*x^2)]/Sqrt[1+(-1)/(a^2*x^2)]");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:132
  public void test0192() {
    check( //
        "Integrate[x^(3/2)*ArcCoth[Sqrt[x]], x]", //
        "1/5*x+1/10*x^2+2/5*x^(5/2)*ArcCoth[Sqrt[x]]+1/5*Log[1-x]");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:428
  public void test0193() {
    check( //
        "Integrate[1/(a+b*ArcSinh[-I+d*x^2])^3, x]", //
        "-1/8*x/(b^2*(a-I*b*ArcSin[1+I*d*x^2]))+1/16*x*SinhIntegral[1/2*(a-I*b*ArcSin[1+I*d*x^2])/b]*(Cosh[1/2*a/b]+I*Sinh[1/2*a/b])/(b^3*(Cos[1/2*ArcSin[1+I*d*x^2]]-Sin[1/2*ArcSin[1+I*d*x^2]]))-1/16*x*CosIntegral[1/2*I*(a-I*b*ArcSin[1+I*d*x^2])/b]*(I*Cosh[1/2*a/b]+Sinh[1/2*a/b])/(b^3*(Cos[1/2*ArcSin[1+I*d*x^2]]-Sin[1/2*ArcSin[1+I*d*x^2]]))-1/4*Sqrt[-2*I*d*x^2+d^2*x^4]/(b*d*x*(a-I*b*ArcSin[1+I*d*x^2])^2)");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:440
  public void test0194() {
    check( //
        "Integrate[1/((1-a^2*x^2)^4*ArcTanh[a*x]), x]", //
        "15/32*CoshIntegral[2*ArcTanh[a*x]]/a+3/16*CoshIntegral[4*ArcTanh[a*x]]/a+1/32*CoshIntegral[6*ArcTanh[a*x]]/a+5/16*Log[ArcTanh[a*x]]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1443
  public void test0195() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*(c-a^2*c*x^2)^5), x]", //
        "1/9*(-1+a*x)/(a*c^5*(1-a^2*x^2)^(9/2))+8/63*x/(c^5*(1-a^2*x^2)^(7/2))+16/105*x/(c^5*(1-a^2*x^2)^(5/2))+64/315*x/(c^5*(1-a^2*x^2)^(3/2))+128/315*x/(c^5*Sqrt[1-a^2*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:550
  public void test0196() {
    check( //
        "Integrate[E^(4*ArcTanh[a*x])*(c-c/(a*x))^5, x]", //
        "1/4*c^5/(a^5*x^4)-1/3*c^5/(a^4*x^3)-c^5/(a^3*x^2)+2*c^5/(a^2*x)+c^5*x-c^5*Log[x]/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1254
  public void test0197() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])/(x*(c-a^2*c*x^2)^2), x]", //
        "1/4/(c^2*(1-a*x)^2)+3/4/(c^2*(1-a*x))+Log[x]/c^2-7/8*Log[1-a*x]/c^2-1/8*Log[1+a*x]/c^2");
  }

  // 7.6.2 Inverse hyperbolic cosecant functions.input:8
  public void test0198() {
    check( //
        "Integrate[x^3*ArcCsch[a+b*x], x]", //
        "-1/4*a^4*ArcCsch[a+b*x]/b^4+1/4*x^4*ArcCsch[a+b*x]+1/2*a*(1-2*a^2)*ArcTanh[Sqrt[1+1/(a+b*x)^2]]/b^4-1/12*(2-17*a^2)*(a+b*x)*Sqrt[1+1/(a+b*x)^2]/b^4+1/12*x^2*(a+b*x)*Sqrt[1+1/(a+b*x)^2]/b^2-1/3*a*(a+b*x)^2*Sqrt[1+1/(a+b*x)^2]/b^4");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:660
  public void test0199() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])*(c-a^2*c*x^2)^2, x]", //
        "-1/2*c^2*(1+a*x)^4/a+1/5*c^2*(1+a*x)^5/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:205
  public void test0200() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(c-a*c*x)^3, x]", //
        "1/5*(1-a^2*x^2)^(3/2)/(a*c^3*(1-a*x)^4)+1/15*(1-a^2*x^2)^(3/2)/(a*c^3*(1-a*x)^3)");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:469
  public void test0201() {
    check( //
        "Integrate[x*ArcTanh[a*x]^3/(1-a^2*x^2)^(1/2), x]", //
        "6*ArcTan[E^ArcTanh[a*x]]*ArcTanh[a*x]^2/a^2-6*I*ArcTanh[a*x]*PolyLog[2,-I*E^ArcTanh[a*x]]/a^2+6*I*ArcTanh[a*x]*PolyLog[2,I*E^ArcTanh[a*x]]/a^2+6*I*PolyLog[3,-I*E^ArcTanh[a*x]]/a^2-6*I*PolyLog[3,I*E^ArcTanh[a*x]]/a^2-ArcTanh[a*x]^3*Sqrt[1-a^2*x^2]/a^2");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:207
  public void test0202() {
    check( //
        "Integrate[E^ArcCoth[a*x]/(c-a*c*x)^5, x]", //
        "1/9*a^5*(1+(-1)/(a^2*x^2))^(3/2)/(c^5*(a+(-1)/x)^6)-8/21*a^4*(1+(-1)/(a^2*x^2))^(3/2)/(c^5*(a+(-1)/x)^5)+47/105*a^3*(1+(-1)/(a^2*x^2))^(3/2)/(c^5*(a+(-1)/x)^4)-58/315*a^2*(1+(-1)/(a^2*x^2))^(3/2)/(c^5*(a+(-1)/x)^3)");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:253
  public void test0203() {
    check( //
        "Integrate[(d+c^2*d*x^2)^3*(a+b*ArcSinh[c*x])^2, x]", //
        "4322/3675*b^2*d^3*x+1514/11025*b^2*c^2*d^3*x^3+234/6125*b^2*c^4*d^3*x^5+2/343*b^2*c^6*d^3*x^7-16/105*b*d^3*(1+c^2*x^2)^(3/2)*(a+b*ArcSinh[c*x])/c-12/175*b*d^3*(1+c^2*x^2)^(5/2)*(a+b*ArcSinh[c*x])/c-2/49*b*d^3*(1+c^2*x^2)^(7/2)*(a+b*ArcSinh[c*x])/c+16/35*d^3*x*(a+b*ArcSinh[c*x])^2+8/35*d^3*x*(1+c^2*x^2)*(a+b*ArcSinh[c*x])^2+6/35*d^3*x*(1+c^2*x^2)^2*(a+b*ArcSinh[c*x])^2+1/7*d^3*x*(1+c^2*x^2)^3*(a+b*ArcSinh[c*x])^2-32/35*b*d^3*(a+b*ArcSinh[c*x])*Sqrt[1+c^2*x^2]/c");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1021
  public void test0204() {
    check( //
        "Integrate[E^ArcTanh[1+b*x]/(2+b*x), x]", //
        "ArcSin[1+b*x]/b");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:505
  public void test0205() {
    check( //
        "Integrate[1/((c-a^2*c*x^2)^(1/2)*Sqrt[ArcCosh[a*x]]), x]", //
        "2*Sqrt[-1+a*x]*Sqrt[1+a*x]*Sqrt[ArcCosh[a*x]]/(a*Sqrt[c-a^2*c*x^2])");
  }

  // 7.5.1 u (a+b arcsech(c x))^n.input:23
  public void test0206() {
    check( //
        "Integrate[ArcSech[a*x]^3, x]", //
        "x*ArcSech[a*x]^3-6*ArcSech[a*x]^2*ArcTan[E^ArcSech[a*x]]/a+6*I*ArcSech[a*x]*PolyLog[2,-I*E^ArcSech[a*x]]/a-6*I*ArcSech[a*x]*PolyLog[2,I*E^ArcSech[a*x]]/a-6*I*PolyLog[3,-I*E^ArcSech[a*x]]/a+6*I*PolyLog[3,I*E^ArcSech[a*x]]/a");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:309
  public void test0207() {
    check( //
        "Integrate[ArcTanh[a*x]^(1/2)/(1-a^2*x^2), x]", //
        "2/3*ArcTanh[a*x]^(3/2)/a");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1681
  public void test0208() {
    check( //
        "Integrate[E^(2*(1+p)*ArcTanh[a*x])/(c-a^2*c*x^2)^p, x]", //
        "(1-a*x)^(1-2*p)*(1-a^2*x^2)^p/(a*(1-2*p)*(c-a^2*c*x^2)^p)+(1-a^2*x^2)^p/(a*p*(1-a*x)^(2*p)*(c-a^2*c*x^2)^p)");
  }

  // 7.5.1 u (a+b arcsech(c x))^n.input:48
  public void test0209() {
    check( //
        "Integrate[x*(a+b*ArcSech[c*x])^2, x]", //
        "1/2*x^2*(a+b*ArcSech[c*x])^2-b^2*Log[x]/c^2-b*(1+c*x)*(a+b*ArcSech[c*x])*Sqrt[(1-c*x)/(1+c*x)]/c^2");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:294
  public void test0210() {
    check( //
        "Integrate[x^(3/2)/ArcTanh[Tanh[a+b*x]]^(3/2), x]", //
        "3*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[ArcTanh[Tanh[a+b*x]]]]*(b*x-ArcTanh[Tanh[a+b*x]])/b^(5/2)-2*x^(3/2)/(b*Sqrt[ArcTanh[Tanh[a+b*x]]])+3*Sqrt[x]*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^2");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:722
  public void test0211() {
    check( //
        "Integrate[(d+e*x^2)/(a+b*ArcCosh[c*x])^(3/2), x]", //
        "E^(a/b)*d*Erf[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(b^(3/2)*c)+1/4*E^(a/b)*e*Erf[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(b^(3/2)*c^3)+d*Erfi[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*b^(3/2)*c)+1/4*e*Erfi[Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*b^(3/2)*c^3)+1/4*E^(3*a/b)*e*Erf[Sqrt[3]*Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[3*Pi]/(b^(3/2)*c^3)+1/4*e*Erfi[Sqrt[3]*Sqrt[a+b*ArcCosh[c*x]]/Sqrt[b]]*Sqrt[3*Pi]/(E^(3*a/b)*b^(3/2)*c^3)-2*d*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[a+b*ArcCosh[c*x]])-2*e*x^2*Sqrt[-1+c*x]*Sqrt[1+c*x]/(b*c*Sqrt[a+b*ArcCosh[c*x]])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:50
  public void test0212() {
    check( //
        "Integrate[(d+c*d*x)^4*(a+b*ArcTanh[c*x])/x^5, x]", //
        "-1/12*b*c*d^4/x^3-2/3*b*c^2*d^4/x^2-13/4*b*c^3*d^4/x+13/4*b*c^4*d^4*ArcTanh[c*x]-1/4*d^4*(a+b*ArcTanh[c*x])/x^4-4/3*c*d^4*(a+b*ArcTanh[c*x])/x^3-3*c^2*d^4*(a+b*ArcTanh[c*x])/x^2-4*c^3*d^4*(a+b*ArcTanh[c*x])/x+a*c^4*d^4*Log[x]+16/3*b*c^4*d^4*Log[x]-8/3*b*c^4*d^4*Log[1-c^2*x^2]-1/2*b*c^4*d^4*PolyLog[2,-c*x]+1/2*b*c^4*d^4*PolyLog[2,c*x]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:673
  public void test0213() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])/(c-a^2*c*x^2)^4, x]", //
        "-16/63*E^(3*ArcCoth[a*x])/(a*c^4)-1/9*E^(3*ArcCoth[a*x])*(1-2*a*x)/(a*c^4*(1-a^2*x^2)^3)-10/63*E^(3*ArcCoth[a*x])*(3-4*a*x)/(a*c^4*(1-a^2*x^2)^2)+8/21*E^(3*ArcCoth[a*x])*(3-2*a*x)/(a*c^4*(1-a^2*x^2))");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:160
  public void test0214() {
    check( //
        "Integrate[x^2*ArcTanh[Tanh[a+b*x]]^(5/2), x]", //
        "2/7*x^2*ArcTanh[Tanh[a+b*x]]^(7/2)/b-8/63*x*ArcTanh[Tanh[a+b*x]]^(9/2)/b^2+16/693*ArcTanh[Tanh[a+b*x]]^(11/2)/b^3");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:141
  public void test0215() {
    check( //
        "Integrate[x^3*Sqrt[ArcTanh[Tanh[a+b*x]]], x]", //
        "2/3*x^3*ArcTanh[Tanh[a+b*x]]^(3/2)/b-4/5*x^2*ArcTanh[Tanh[a+b*x]]^(5/2)/b^2+16/35*x*ArcTanh[Tanh[a+b*x]]^(7/2)/b^3-32/315*ArcTanh[Tanh[a+b*x]]^(9/2)/b^4");
  }

  // 7.6.1 u (a+b arccsch(c x))^n.input:16
  public void test0216() {
    check( //
        "Integrate[x^2*(a+b*ArcCsch[c*x]), x]", //
        "1/3*x^3*(a+b*ArcCsch[c*x])-1/6*b*ArcTanh[Sqrt[1+1/(c^2*x^2)]]/c^3+1/6*b*x^2*Sqrt[1+1/(c^2*x^2)]/c");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:556
  public void test0217() {
    check( //
        "Integrate[E^(4*ArcTanh[a*x])/(c-c/(a*x))^2, x]", //
        "x/c^2+4/3/(a*c^2*(1-a*x)^3)+(-6)/(a*c^2*(1-a*x)^2)+13/(a*c^2*(1-a*x))+6*Log[1-a*x]/(a*c^2)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:529
  public void test0218() {
    check( //
        "Integrate[E^ArcTanh[a*x]/(c-c/(a*x))^3, x]", //
        "-1/5*(1-a^2*x^2)^(3/2)/(a*c^3*(1-a*x)^4)+14/15*(1-a^2*x^2)^(3/2)/(a*c^3*(1-a*x)^3)+(1-a^2*x^2)^(3/2)/(a*c^3*(1-a*x)^2)+4*ArcSin[a*x]/(a*c^3)-8*Sqrt[1-a^2*x^2]/(a*c^3*(1-a*x))");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:400
  public void test0219() {
    check( //
        "Integrate[x^4/((1-a^2*x^2)^3*ArcTanh[a*x]), x]", //
        "-1/2*CoshIntegral[2*ArcTanh[a*x]]/a^5+1/8*CoshIntegral[4*ArcTanh[a*x]]/a^5+3/8*Log[ArcTanh[a*x]]/a^5");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:317
  public void test0220() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x^n])^2/x, x]", //
        "2*(a+b*ArcTanh[c*x^n])^2*ArcTanh[1+(-2)/(1-c*x^n)]/n-b*(a+b*ArcTanh[c*x^n])*PolyLog[2,1+(-2)/(1-c*x^n)]/n+b*(a+b*ArcTanh[c*x^n])*PolyLog[2,-1+2/(1-c*x^n)]/n+1/2*b^2*PolyLog[3,1+(-2)/(1-c*x^n)]/n-1/2*b^2*PolyLog[3,-1+2/(1-c*x^n)]/n");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:350
  public void test0221() {
    check( //
        "Integrate[Sqrt[1+a^2+2*a*b*x+b^2*x^2]/ArcSinh[a+b*x]^3, x]", //
        "1/2*(-1-(a+b*x)^2)/(b*ArcSinh[a+b*x]^2)+CoshIntegral[2*ArcSinh[a+b*x]]/b-(a+b*x)*Sqrt[1+(a+b*x)^2]/(b*ArcSinh[a+b*x])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:273
  public void test0222() {
    check( //
        "Integrate[E^ArcTanh[a*x]*(c-a*c*x)^(9/2), x]", //
        "4096/3465*c^6*(1-a^2*x^2)^(3/2)/(a*(c-a*c*x)^(3/2))+32/99*c^3*(c-a*c*x)^(3/2)*(1-a^2*x^2)^(3/2)/a+2/11*c^2*(c-a*c*x)^(5/2)*(1-a^2*x^2)^(3/2)/a+1024/1155*c^5*(1-a^2*x^2)^(3/2)/(a*Sqrt[c-a*c*x])+128/231*c^4*(1-a^2*x^2)^(3/2)*Sqrt[c-a*c*x]/a");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:332
  public void test0223() {
    check( //
        "Integrate[ArcTanh[a*x]/(x*(1-a^2*x^2)^2), x]", //
        "-1/4*a*x/(1-a^2*x^2)-1/4*ArcTanh[a*x]+1/2*ArcTanh[a*x]/(1-a^2*x^2)+1/2*ArcTanh[a*x]^2+ArcTanh[a*x]*Log[2+(-2)/(1+a*x)]-1/2*PolyLog[2,-1+2/(1+a*x)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:214
  public void test0224() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])/(c-a*c*x), x]", //
        "(-2)/(a*c*(1-a*x))-Log[1-a*x]/(a*c)");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:473
  public void test0225() {
    check( //
        "Integrate[E^(4*ArcCoth[a*x])*(c-c/(a*x))^5, x]", //
        "1/4*c^5/(a^5*x^4)-1/3*c^5/(a^4*x^3)-c^5/(a^3*x^2)+2*c^5/(a^2*x)+c^5*x-c^5*Log[x]/a");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:250
  public void test0226() {
    check( //
        "Integrate[x^3*(d+c^2*d*x^2)^3*(a+b*ArcSinh[c*x])^2, x]", //
        "-79/5120*b^2*d^3*x^2/c^2+79/15360*b^2*d^3*x^4+401/28800*b^2*c^2*d^3*x^6+57/6400*b^2*c^4*d^3*x^8+1/500*b^2*c^6*d^3*x^10-1/32*b*c*d^3*x^5*(1+c^2*x^2)^(3/2)*(a+b*ArcSinh[c*x])-1/50*b*c*d^3*x^5*(1+c^2*x^2)^(5/2)*(a+b*ArcSinh[c*x])-79/5120*d^3*(a+b*ArcSinh[c*x])^2/c^4+1/40*d^3*x^4*(a+b*ArcSinh[c*x])^2+1/20*d^3*x^4*(1+c^2*x^2)*(a+b*ArcSinh[c*x])^2+3/40*d^3*x^4*(1+c^2*x^2)^2*(a+b*ArcSinh[c*x])^2+1/10*d^3*x^4*(1+c^2*x^2)^3*(a+b*ArcSinh[c*x])^2+79/2560*b*d^3*x*(a+b*ArcSinh[c*x])*Sqrt[1+c^2*x^2]/c^3-79/3840*b*d^3*x^3*(a+b*ArcSinh[c*x])*Sqrt[1+c^2*x^2]/c-31/960*b*c*d^3*x^5*(a+b*ArcSinh[c*x])*Sqrt[1+c^2*x^2]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:655
  public void test0227() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x])*(d+e*Log[1-c^2*x^2])/x^2, x]", //
        "-c*e*(a+b*ArcTanh[c*x])^2/b-(a+b*ArcTanh[c*x])*(d+e*Log[1-c^2*x^2])/x+1/2*b*c*(d+e*Log[1-c^2*x^2])*Log[1+(-1)/(1-c^2*x^2)]-1/2*b*c*e*PolyLog[2,1/(1-c^2*x^2)]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:904
  public void test0228() {
    check( //
        "Integrate[E^(2*ArcCoth[a*x])/(c-c/(a^2*x^2)), x]", //
        "x/c+1/(a*c*(1-a*x))+2*Log[1-a*x]/(a*c)");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1364
  public void test0229() {
    check( //
        "Integrate[E^(3*ArcTanh[a*x])/(c-a^2*c*x^2)^4, x]", //
        "8/63*x/(c^4*(1-a^2*x^2)^(3/2))+1/9/(a*c^4*(1-a*x)^3*(1-a^2*x^2)^(3/2))+2/21/(a*c^4*(1-a*x)^2*(1-a^2*x^2)^(3/2))+2/21/(a*c^4*(1-a*x)*(1-a^2*x^2)^(3/2))+16/63*x/(c^4*Sqrt[1-a^2*x^2])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:1037
  public void test0230() {
    check( //
        "Integrate[x^m*Sqrt[c-c/(a^2*x^2)]/E^ArcCoth[a*x], x]", //
        "-x^m*Sqrt[c-c/(a^2*x^2)]/(a*m*Sqrt[1+(-1)/(a^2*x^2)])+x^(1+m)*Sqrt[c-c/(a^2*x^2)]/((1+m)*Sqrt[1+(-1)/(a^2*x^2)])");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:64
  public void test0231() {
    check( //
        "Integrate[(f+g*x)^2*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2], x]", //
        "1/2*f^2*x*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]+1/8*g^2*x*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/c^2+1/4*g^2*x^3*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]+2/3*f*g*(1+c^2*x^2)*(a+b*ArcSinh[c*x])*Sqrt[d+c^2*d*x^2]/c^2-2/3*b*f*g*x*Sqrt[d+c^2*d*x^2]/(c*Sqrt[1+c^2*x^2])-1/4*b*c*f^2*x^2*Sqrt[d+c^2*d*x^2]/Sqrt[1+c^2*x^2]-1/16*b*g^2*x^2*Sqrt[d+c^2*d*x^2]/(c*Sqrt[1+c^2*x^2])-2/9*b*c*f*g*x^3*Sqrt[d+c^2*d*x^2]/Sqrt[1+c^2*x^2]-1/16*b*c*g^2*x^4*Sqrt[d+c^2*d*x^2]/Sqrt[1+c^2*x^2]+1/4*f^2*(a+b*ArcSinh[c*x])^2*Sqrt[d+c^2*d*x^2]/(b*c*Sqrt[1+c^2*x^2])-1/16*g^2*(a+b*ArcSinh[c*x])^2*Sqrt[d+c^2*d*x^2]/(b*c^3*Sqrt[1+c^2*x^2])");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:791
  public void test0232() {
    check( //
        "Integrate[E^(3*ArcCoth[a*x])*Sqrt[c-a^2*c*x^2]/x, x]", //
        "Sqrt[c-a^2*c*x^2]/Sqrt[1+(-1)/(a^2*x^2)]-Log[x]*Sqrt[c-a^2*c*x^2]/(a*x*Sqrt[1+(-1)/(a^2*x^2)])+4*Log[1-a*x]*Sqrt[c-a^2*c*x^2]/(a*x*Sqrt[1+(-1)/(a^2*x^2)])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:304
  public void test0233() {
    check( //
        "Integrate[(c-a*c*x)^(3/2)/E^ArcTanh[a*x], x]", //
        "2/5*(c-a*c*x)^(3/2)*Sqrt[1-a^2*x^2]/a+64/15*c^2*Sqrt[1-a^2*x^2]/(a*Sqrt[c-a*c*x])+16/15*c*Sqrt[c-a*c*x]*Sqrt[1-a^2*x^2]/a");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:287
  public void test0234() {
    check( //
        "Integrate[x*ArcCoth[c+d*Tanh[a+b*x]], x]", //
        "1/2*x^2*ArcCoth[c+d*Tanh[a+b*x]]+1/4*x^2*Log[1+E^(2*a+2*b*x)*(1-c-d)/(1-c+d)]-1/4*x^2*Log[1+E^(2*a+2*b*x)*(1+c+d)/(1+c-d)]+1/4*x*PolyLog[2,-E^(2*a+2*b*x)*(1-c-d)/(1-c+d)]/b-1/4*x*PolyLog[2,-E^(2*a+2*b*x)*(1+c+d)/(1+c-d)]/b-1/8*PolyLog[3,-E^(2*a+2*b*x)*(1-c-d)/(1-c+d)]/b^2+1/8*PolyLog[3,-E^(2*a+2*b*x)*(1+c+d)/(1+c-d)]/b^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1112
  public void test0235() {
    check( //
        "Integrate[E^ArcTanh[a*x]*x^5/(1-a^2*x^2)^(5/2), x]", //
        "-x/a^5+1/8/(a^6*(1-a*x)^2)+(-1)/(a^6*(1-a*x))+1/8/(a^6*(1+a*x))-23/16*Log[1-a*x]/a^6+7/16*Log[1+a*x]/a^6");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1301
  public void test0236() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])*(c-a^2*c*x^2)^(5/2)/x^5, x]", //
        "-1/24*a*c*(16+9*a*x)*(c-a^2*c*x^2)^(3/2)/x^3-1/4*(c-a^2*c*x^2)^(5/2)/x^4+2*a^4*c^(5/2)*ArcTan[a*x*Sqrt[c]/Sqrt[c-a^2*c*x^2]]+9/8*a^4*c^(5/2)*ArcTanh[Sqrt[c-a^2*c*x^2]/Sqrt[c]]+1/8*a^3*c^2*(16-9*a*x)*Sqrt[c-a^2*c*x^2]/x");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:77
  public void test0237() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2], x]", //
        "1/2*x*(a+b*ArcCosh[c*x])*Sqrt[d-c^2*d*x^2]-1/4*b*c*x^2*Sqrt[d-c^2*d*x^2]/(Sqrt[-1+c*x]*Sqrt[1+c*x])-1/4*(a+b*ArcCosh[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c*Sqrt[-1+c*x]*Sqrt[1+c*x])");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:540
  public void test0238() {
    check( //
        "Integrate[x*ArcTanh[a*x]^2*Sqrt[1-a^2*x^2], x]", //
        "-2/3*ArcTan[Sqrt[1-a*x]/Sqrt[1+a*x]]*ArcTanh[a*x]/a^2-1/3*(1-a^2*x^2)^(3/2)*ArcTanh[a*x]^2/a^2-1/3*I*PolyLog[2,-I*Sqrt[1-a*x]/Sqrt[1+a*x]]/a^2+1/3*I*PolyLog[2,I*Sqrt[1-a*x]/Sqrt[1+a*x]]/a^2+1/3*Sqrt[1-a^2*x^2]/a^2+1/3*x*ArcTanh[a*x]*Sqrt[1-a^2*x^2]/a");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:363
  public void test0239() {
    check( //
        "Integrate[ArcTanh[c+d*Coth[a+b*x]], x]", //
        "x*ArcTanh[c+d*Coth[a+b*x]]+1/2*x*Log[1-E^(2*a+2*b*x)*(1-c-d)/(1-c+d)]-1/2*x*Log[1-E^(2*a+2*b*x)*(1+c+d)/(1+c-d)]+1/4*PolyLog[2,E^(2*a+2*b*x)*(1-c-d)/(1-c+d)]/b-1/4*PolyLog[2,E^(2*a+2*b*x)*(1+c+d)/(1+c-d)]/b");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:354
  public void test0240() {
    check( //
        "Integrate[ArcCoth[1-I*d-d*Cot[a+b*x]], x]", //
        "1/2*I*b*x^2+x*ArcCoth[1-I*d-d*Cot[a+b*x]]-1/2*x*Log[1-E^(2*I*a+2*I*b*x)*(1-I*d)]+1/4*I*PolyLog[2,E^(2*I*a+2*I*b*x)*(1-I*d)]/b");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:148
  public void test0241() {
    check( //
        "Integrate[x^2*ArcTanh[a*x]^3/(c+a*c*x), x]", //
        "3/2*ArcTanh[a*x]^2/(a^3*c)+3/2*x*ArcTanh[a*x]^2/(a^2*c)-3/2*ArcTanh[a*x]^3/(a^3*c)-x*ArcTanh[a*x]^3/(a^2*c)+1/2*x^2*ArcTanh[a*x]^3/(a*c)-3*ArcTanh[a*x]*Log[2/(1-a*x)]/(a^3*c)+3*ArcTanh[a*x]^2*Log[2/(1-a*x)]/(a^3*c)-ArcTanh[a*x]^3*Log[2/(1+a*x)]/(a^3*c)-3/2*PolyLog[2,1+(-2)/(1-a*x)]/(a^3*c)+3*ArcTanh[a*x]*PolyLog[2,1+(-2)/(1-a*x)]/(a^3*c)+3/2*ArcTanh[a*x]^2*PolyLog[2,1+(-2)/(1+a*x)]/(a^3*c)-3/2*PolyLog[3,1+(-2)/(1-a*x)]/(a^3*c)+3/2*ArcTanh[a*x]*PolyLog[3,1+(-2)/(1+a*x)]/(a^3*c)+3/4*PolyLog[4,1+(-2)/(1+a*x)]/(a^3*c)");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:261
  public void test0242() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^(1/2)/x^(7/2), x]", //
        "4/15*b*ArcTanh[Tanh[a+b*x]]^(3/2)/(x^(3/2)*(b*x-ArcTanh[Tanh[a+b*x]])^2)+2/5*ArcTanh[Tanh[a+b*x]]^(3/2)/(x^(5/2)*(b*x-ArcTanh[Tanh[a+b*x]]))");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:642
  public void test0243() {
    check( //
        "Integrate[(a+b*ArcCosh[c*x])/(d+e*x^2)^(3/2), x]", //
        "-b*ArcTanh[Sqrt[e]*Sqrt[-1+c^2*x^2]/(c*Sqrt[d+e*x^2])]*Sqrt[-1+c^2*x^2]/(d*Sqrt[e]*Sqrt[-1+c*x]*Sqrt[1+c*x])+x*(a+b*ArcCosh[c*x])/(d*Sqrt[d+e*x^2])");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:307
  public void test0244() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*(c-a*c*x)^(3/2)), x]", //
        "ArcTanh[Sqrt[c]*Sqrt[1-a^2*x^2]/(Sqrt[2]*Sqrt[c-a*c*x])]*Sqrt[2]/(a*c^(3/2))");
  }

  // 7.1.4 (f x)^m (d+e x^2)^p (a+b arcsinh(c x))^n.input:273
  public void test0245() {
    check( //
        "Integrate[(a+b*ArcSinh[c*x])^2/(d+c^2*d*x^2)^2, x]", //
        "1/2*x*(a+b*ArcSinh[c*x])^2/(d^2*(1+c^2*x^2))+(a+b*ArcSinh[c*x])^2*ArcTan[E^ArcSinh[c*x]]/(c*d^2)-b^2*ArcTan[c*x]/(c*d^2)-I*b*(a+b*ArcSinh[c*x])*PolyLog[2,-I*E^ArcSinh[c*x]]/(c*d^2)+I*b*(a+b*ArcSinh[c*x])*PolyLog[2,I*E^ArcSinh[c*x]]/(c*d^2)+I*b^2*PolyLog[3,-I*E^ArcSinh[c*x]]/(c*d^2)-I*b^2*PolyLog[3,I*E^ArcSinh[c*x]]/(c*d^2)+b*(a+b*ArcSinh[c*x])/(c*d^2*Sqrt[1+c^2*x^2])");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:125
  public void test0246() {
    check( //
        "Integrate[1/(x^2*ArcTanh[Tanh[a+b*x]]^2), x]", //
        "-2*b/((b*x-ArcTanh[Tanh[a+b*x]])^2*ArcTanh[Tanh[a+b*x]])+1/(x*(b*x-ArcTanh[Tanh[a+b*x]])*ArcTanh[Tanh[a+b*x]])+2*b*Log[x]/(b*x-ArcTanh[Tanh[a+b*x]])^3-2*b*Log[ArcTanh[Tanh[a+b*x]]]/(b*x-ArcTanh[Tanh[a+b*x]])^3");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:97
  public void test0247() {
    check( //
        "Integrate[ArcTanh[Tanh[a+b*x]]^4/x^2, x]", //
        "4*b^2*x*(b*x-ArcTanh[Tanh[a+b*x]])^2-2*b*(b*x-ArcTanh[Tanh[a+b*x]])*ArcTanh[Tanh[a+b*x]]^2+4/3*b*ArcTanh[Tanh[a+b*x]]^3-ArcTanh[Tanh[a+b*x]]^4/x-4*b*(b*x-ArcTanh[Tanh[a+b*x]])^3*Log[x]");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:444
  public void test0248() {
    check( //
        "Integrate[1/((1-a^2*x^2)^4*ArcTanh[a*x]^2), x]", //
        "(-1)/(a*(1-a^2*x^2)^3*ArcTanh[a*x])+15/16*SinhIntegral[2*ArcTanh[a*x]]/a+3/4*SinhIntegral[4*ArcTanh[a*x]]/a+3/16*SinhIntegral[6*ArcTanh[a*x]]/a");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:329
  public void test0249() {
    check( //
        "Integrate[x^2*ArcCoth[1-I*d+d*Tan[a+b*x]], x]", //
        "1/12*I*b*x^4+1/3*x^3*ArcCoth[1-I*d+d*Tan[a+b*x]]-1/6*x^3*Log[1+E^(2*I*a+2*I*b*x)*(1-I*d)]+1/4*I*x^2*PolyLog[2,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b-1/4*x*PolyLog[3,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b^2-1/8*I*PolyLog[4,-E^(2*I*a+2*I*b*x)*(1-I*d)]/b^3");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:189
  public void test0250() {
    check( //
        "Integrate[x^4/ArcTanh[Tanh[a+b*x]]^(5/2), x]", //
        "-2/3*x^4/(b*ArcTanh[Tanh[a+b*x]]^(3/2))-128/3*x*ArcTanh[Tanh[a+b*x]]^(3/2)/b^4+256/15*ArcTanh[Tanh[a+b*x]]^(5/2)/b^5-16/3*x^3/(b^2*Sqrt[ArcTanh[Tanh[a+b*x]]])+32*x^2*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^3");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:1209
  public void test0251() {
    check( //
        "Integrate[E^(2*ArcTanh[a*x])*x^3*(c-a^2*c*x^2), x]", //
        "1/4*c*x^4+2/5*a*c*x^5+1/6*a^2*c*x^6");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:256
  public void test0252() {
    check( //
        "Integrate[x^(3/2)*ArcTanh[Tanh[a+b*x]]^(1/2), x]", //
        "-1/8*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[ArcTanh[Tanh[a+b*x]]]]*(b*x-ArcTanh[Tanh[a+b*x]])^3/b^(5/2)+1/3*x^(5/2)*Sqrt[ArcTanh[Tanh[a+b*x]]]-1/12*x^(3/2)*(b*x-ArcTanh[Tanh[a+b*x]])*Sqrt[ArcTanh[Tanh[a+b*x]]]/b-1/8*(b*x-ArcTanh[Tanh[a+b*x]])^2*Sqrt[x]*Sqrt[ArcTanh[Tanh[a+b*x]]]/b^2");
  }

  // 7.3.4 u (a+b arctanh(c x))^p.input:49
  public void test0253() {
    check( //
        "Integrate[(d+c*d*x)^4*(a+b*ArcTanh[c*x])/x^4, x]", //
        "-1/6*b*c*d^4/x^2-2*b*c^2*d^4/x+a*c^4*d^4*x+2*b*c^3*d^4*ArcTanh[c*x]+b*c^4*d^4*x*ArcTanh[c*x]-1/3*d^4*(a+b*ArcTanh[c*x])/x^3-2*c*d^4*(a+b*ArcTanh[c*x])/x^2-6*c^2*d^4*(a+b*ArcTanh[c*x])/x+4*a*c^3*d^4*Log[x]+19/3*b*c^3*d^4*Log[x]-8/3*b*c^3*d^4*Log[1-c^2*x^2]-2*b*c^3*d^4*PolyLog[2,-c*x]+2*b*c^3*d^4*PolyLog[2,c*x]");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:281
  public void test0254() {
    check( //
        "Integrate[E^ArcCoth[a*x]/(c-a*c*x)^(7/2), x]", //
        "-1/6*a^4*(1+(-1)/(a*x))^(7/2)*(1+1/(a*x))^(3/2)*x^2/((a+(-1)/x)^3*(c-a*c*x)^(7/2))+1/16*a^4*(1+(-1)/(a*x))^(7/2)*(1+1/(a*x))^(3/2)*x^3/((a+(-1)/x)^2*(c-a*c*x)^(7/2))-1/32*a^(5/2)*(1+(-1)/(a*x))^(7/2)*ArcTanh[Sqrt[2]*Sqrt[1/x]/(Sqrt[a]*Sqrt[1+1/(a*x)])]/((1/x)^(7/2)*(c-a*c*x)^(7/2)*Sqrt[2])-1/32*a^3*(1+(-1)/(a*x))^(7/2)*x^3*Sqrt[1+1/(a*x)]/((a+(-1)/x)*(c-a*c*x)^(7/2))");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:113
  public void test0255() {
    check( //
        "Integrate[x/ArcTanh[Tanh[a+b*x]], x]", //
        "x/b+(b*x-ArcTanh[Tanh[a+b*x]])*Log[ArcTanh[Tanh[a+b*x]]]/b^2");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:632
  public void test0256() {
    check( //
        "Integrate[1/(E^(2*ArcTanh[a*x])*Sqrt[c-c/(a*x)]), x]", //
        "3*ArcTanh[Sqrt[c-c/(a*x)]/Sqrt[c]]/(a*Sqrt[c])-2*ArcTanh[Sqrt[c-c/(a*x)]/(Sqrt[2]*Sqrt[c])]*Sqrt[2]/(a*Sqrt[c])-x*Sqrt[c-c/(a*x)]/c");
  }

  // 7.1.5 Inverse hyperbolic sine functions.input:364
  public void test0257() {
    check( //
        "Integrate[1/(ArcSinh[a+b*x]^3*Sqrt[1+a^2+2*a*b*x+b^2*x^2]), x]", //
        "(-1/2)/(b*ArcSinh[a+b*x]^2)");
  }

  // 7.3.7 Inverse hyperbolic tangent functions.input:132
  public void test0258() {
    check( //
        "Integrate[1/ArcTanh[Tanh[a+b*x]]^3, x]", //
        "(-1/2)/(b*ArcTanh[Tanh[a+b*x]]^2)");
  }

  // 7.4.2 Exponentials of inverse hyperbolic cotangent functions.input:714
  public void test0259() {
    check( //
        "Integrate[E^ArcCoth[a*x]*(c-a^2*c*x^2)^(7/2), x]", //
        "-8/5*(1+a*x)^5*(c-a^2*c*x^2)^(7/2)/(a^8*(1+(-1)/(a^2*x^2))^(7/2)*x^7)+2*(1+a*x)^6*(c-a^2*c*x^2)^(7/2)/(a^8*(1+(-1)/(a^2*x^2))^(7/2)*x^7)-6/7*(1+a*x)^7*(c-a^2*c*x^2)^(7/2)/(a^8*(1+(-1)/(a^2*x^2))^(7/2)*x^7)+1/8*(1+a*x)^8*(c-a^2*c*x^2)^(7/2)/(a^8*(1+(-1)/(a^2*x^2))^(7/2)*x^7)");
  }

  // 7.3.2 (d x)^m (a+b arctanh(c x^n))^p.input:76
  public void test0260() {
    check( //
        "Integrate[(a+b*ArcTanh[c*x^2])/x^7, x]", //
        "-1/12*b*c/x^4+1/6*(-a-b*ArcTanh[c*x^2])/x^6+1/3*b*c^3*Log[x]-1/12*b*c^3*Log[1-c^2*x^4]");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:19
  public void test0261() {
    check( //
        "Integrate[ArcCoth[a*x]/x^2, x]", //
        "-ArcCoth[a*x]/x+a*Log[x]-1/2*a*Log[1-a^2*x^2]");
  }

  // 7.2.4 (f x)^m (d+e x^2)^p (a+b arccosh(c x))^n.input:658
  public void test0262() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*ArcCosh[c*x])^2, x]", //
        "2*b^2*d^2*x+8/9*b^2*d*e*x/c^2+16/75*b^2*e^2*x/c^4+4/27*b^2*d*e*x^3+8/225*b^2*e^2*x^3/c^2+2/125*b^2*e^2*x^5+d^2*x*(a+b*ArcCosh[c*x])^2+2/3*d*e*x^3*(a+b*ArcCosh[c*x])^2+1/5*e^2*x^5*(a+b*ArcCosh[c*x])^2-2*b*d^2*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c-8/9*b*d*e*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^3-16/75*b*e^2*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^5-4/9*b*d*e*x^2*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c-8/75*b*e^2*x^2*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^3-2/25*b*e^2*x^4*(a+b*ArcCosh[c*x])*Sqrt[-1+c*x]*Sqrt[1+c*x]/c");
  }

  // 7.3.6 Exponentials of inverse hyperbolic tangent functions.input:567
  public void test0263() {
    check( //
        "Integrate[1/(E^ArcTanh[a*x]*(c-c/(a*x))^2), x]", //
        "-ArcSin[a*x]/(a*c^2)+Sqrt[1-a^2*x^2]/(a*c^2)+Sqrt[1-a^2*x^2]/(a*c^2*(1-a*x))");
  }

  // 7.4.1 Inverse hyperbolic cotangent functions.input:237
  public void test0264() {
    check( //
        "Integrate[1/(x*ArcCoth[Tanh[a+b*x]]^2), x]", //
        "(-1)/((b*x-ArcCoth[Tanh[a+b*x]])*ArcCoth[Tanh[a+b*x]])+Log[x]/(b*x-ArcCoth[Tanh[a+b*x]])^2-Log[ArcCoth[Tanh[a+b*x]]]/(b*x-ArcCoth[Tanh[a+b*x]])^2");
  }
}
