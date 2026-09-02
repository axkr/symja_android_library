package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 5 Inverse trig functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class InverseTrigFunctions extends AbstractRubiTestCase {
  static boolean init = true;

  public InverseTrigFunctions(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("InverseTrigFunctions");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 5.3.3 (d+e x)^m (a+b arctan(c x^n))^p.input:8
  public void test0001() {
    check( //
        "Integrate[(d+e*x)^4*(a+b*ArcTan[c*x]), x]", //
        "-b*d*e*(2*c^2*d^2-e^2)*x/c^3-1/10*b*e^2*(10*c^2*d^2-e^2)*x^2/c^3-1/3*b*d*e^3*x^3/c-1/20*b*e^4*x^4/c-1/5*b*d*(c^4*d^4-10*c^2*d^2*e^2+5*e^4)*ArcTan[c*x]/(c^4*e)+1/5*(d+e*x)^5*(a+b*ArcTan[c*x])/e-1/10*b*(5*c^4*d^4-10*c^2*d^2*e^2+e^4)*Log[1+c^2*x^2]/c^5");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:339
  public void test0002() {
    check( //
        "Integrate[ArcSin[a*x]^3/(c-a^2*c*x^2), x]", //
        "-2*I*ArcSin[a*x]^3*ArcTan[E^(I*ArcSin[a*x])]/(a*c)+3*I*ArcSin[a*x]^2*PolyLog[2,-I*E^(I*ArcSin[a*x])]/(a*c)-3*I*ArcSin[a*x]^2*PolyLog[2,I*E^(I*ArcSin[a*x])]/(a*c)-6*ArcSin[a*x]*PolyLog[3,-I*E^(I*ArcSin[a*x])]/(a*c)+6*ArcSin[a*x]*PolyLog[3,I*E^(I*ArcSin[a*x])]/(a*c)-6*I*PolyLog[4,-I*E^(I*ArcSin[a*x])]/(a*c)+6*I*PolyLog[4,I*E^(I*ArcSin[a*x])]/(a*c)");
  }

  // 5.3.7 Inverse tangent functions.input:79
  public void test0003() {
    check( //
        "Integrate[x^2*ArcTan[c+(1+I*c)*Tan[a+b*x]], x]", //
        "-1/12*b*x^4+1/3*x^3*ArcTan[c+(1+I*c)*Tan[a+b*x]]-1/6*I*x^3*Log[1-I*E^(2*I*a+2*I*b*x)*c]-1/4*x^2*PolyLog[2,I*E^(2*I*a+2*I*b*x)*c]/b-1/4*I*x*PolyLog[3,I*E^(2*I*a+2*I*b*x)*c]/b^2+1/8*PolyLog[4,I*E^(2*I*a+2*I*b*x)*c]/b^3");
  }

  // 5.3.3 (d+e x)^m (a+b arctan(c x^n))^p.input:33
  public void test0004() {
    check( //
        "Integrate[(d+e*x)*(a+b*ArcTan[c*x^2]), x]", //
        "-1/2*b*d^2*ArcTan[c*x^2]/e+1/2*(d+e*x)^2*(a+b*ArcTan[c*x^2])/e-1/4*b*e*Log[1+c^2*x^4]/c+b*d*ArcTan[1-x*Sqrt[2]*Sqrt[c]]/(Sqrt[2]*Sqrt[c])-b*d*ArcTan[1+x*Sqrt[2]*Sqrt[c]]/(Sqrt[2]*Sqrt[c])-1/2*b*d*Log[1+c*x^2-x*Sqrt[2]*Sqrt[c]]/(Sqrt[2]*Sqrt[c])+1/2*b*d*Log[1+c*x^2+x*Sqrt[2]*Sqrt[c]]/(Sqrt[2]*Sqrt[c])");
  }

  // 5.4.1 Inverse cotangent functions.input:311
  public void test0005() {
    check( //
        "Integrate[x*ArcCot[E^(a+b*x)], x]", //
        "-1/2*I*x*PolyLog[2,-I*E^(-a-b*x)]/b+1/2*I*x*PolyLog[2,I*E^(-a-b*x)]/b-1/2*I*PolyLog[3,-I*E^(-a-b*x)]/b^2+1/2*I*PolyLog[3,I*E^(-a-b*x)]/b^2");
  }

  // 5.2.5 Inverse cosine functions.input:100
  public void test0006() {
    check( //
        "Integrate[ArcCos[a*x^2]/x, x]", //
        "-1/4*I*ArcCos[a*x^2]^2+1/2*ArcCos[a*x^2]*Log[1+E^(2*I*ArcCos[a*x^2])]-1/4*I*PolyLog[2,-E^(2*I*ArcCos[a*x^2])]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:334
  public void test0007() {
    check( //
        "Integrate[(c+a^2*c*x^2)^3*ArcTan[a*x]^2, x]", //
        "38/105*c^3*x+19/315*a^2*c^3*x^3+1/105*a^4*c^3*x^5-8/35*c^3*(1+a^2*x^2)*ArcTan[a*x]/a-3/35*c^3*(1+a^2*x^2)^2*ArcTan[a*x]/a-1/21*c^3*(1+a^2*x^2)^3*ArcTan[a*x]/a+16/35*I*c^3*ArcTan[a*x]^2/a+16/35*c^3*x*ArcTan[a*x]^2+8/35*c^3*x*(1+a^2*x^2)*ArcTan[a*x]^2+6/35*c^3*x*(1+a^2*x^2)^2*ArcTan[a*x]^2+1/7*c^3*x*(1+a^2*x^2)^3*ArcTan[a*x]^2+32/35*c^3*ArcTan[a*x]*Log[2/(1+I*a*x)]/a+16/35*I*c^3*PolyLog[2,1+(-2)/(1+I*a*x)]/a");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:519
  public void test0008() {
    check( //
        "Integrate[x*ArcTan[a*x]^3/Sqrt[c+a^2*c*x^2], x]", //
        "6*I*ArcTan[E^(I*ArcTan[a*x])]*ArcTan[a*x]^2*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])-6*I*ArcTan[a*x]*PolyLog[2,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+6*I*ArcTan[a*x]*PolyLog[2,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+6*PolyLog[3,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])-6*PolyLog[3,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]^3*Sqrt[c+a^2*c*x^2]/(a^2*c)");
  }

  // 5.4.1 Inverse cotangent functions.input:115
  public void test0009() {
    check( //
        "Integrate[ArcCot[a*x^2]/x^4, x]", //
        "2/3*a/x-1/3*ArcCot[a*x^2]/x^3-1/3*a^(3/2)*ArcTan[1-x*Sqrt[2]*Sqrt[a]]/Sqrt[2]+1/3*a^(3/2)*ArcTan[1+x*Sqrt[2]*Sqrt[a]]/Sqrt[2]+1/6*a^(3/2)*Log[1+a*x^2-x*Sqrt[2]*Sqrt[a]]/Sqrt[2]-1/6*a^(3/2)*Log[1+a*x^2+x*Sqrt[2]*Sqrt[a]]/Sqrt[2]");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:62
  public void test0010() {
    check( //
        "Integrate[x*(a+b*ArcSin[c*x])/(d-c^2*d*x^2)^3, x]", //
        "-1/12*b*x/(c*d^3*(1-c^2*x^2)^(3/2))+1/4*(a+b*ArcSin[c*x])/(c^2*d^3*(1-c^2*x^2)^2)-1/6*b*x/(c*d^3*Sqrt[1-c^2*x^2])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:37
  public void test0011() {
    check( //
        "Integrate[(d+I*c*d*x)^3*(a+b*ArcTan[c*x])/x^3, x]", //
        "-1/2*b*c*d^3/x-I*a*c^3*d^3*x-1/2*b*c^2*d^3*ArcTan[c*x]-I*b*c^3*d^3*x*ArcTan[c*x]-1/2*d^3*(a+b*ArcTan[c*x])/x^2-3*I*c*d^3*(a+b*ArcTan[c*x])/x-3*a*c^2*d^3*Log[x]+3*I*b*c^2*d^3*Log[x]-I*b*c^2*d^3*Log[1+c^2*x^2]-3/2*I*b*c^2*d^3*PolyLog[2,-I*c*x]+3/2*I*b*c^2*d^3*PolyLog[2,I*c*x]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:762
  public void test0012() {
    check( //
        "Integrate[x/((c+a^2*c*x^2)^3*ArcTan[a*x]^3), x]", //
        "-1/2*x/(a*c^3*(1+a^2*x^2)^2*ArcTan[a*x]^2)+(-2)/(a^2*c^3*(1+a^2*x^2)^2*ArcTan[a*x])+3/2/(a^2*c^3*(1+a^2*x^2)*ArcTan[a*x])-1/2*SinIntegral[2*ArcTan[a*x]]/(a^2*c^3)-SinIntegral[4*ArcTan[a*x]]/(a^2*c^3)");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:72
  public void test0013() {
    check( //
        "Integrate[x^4*(d-c^2*d*x^2)^(1/2)*(a+b*ArcSin[c*x]), x]", //
        "-1/16*x*(a+b*ArcSin[c*x])*Sqrt[d-c^2*d*x^2]/c^4-1/24*x^3*(a+b*ArcSin[c*x])*Sqrt[d-c^2*d*x^2]/c^2+1/6*x^5*(a+b*ArcSin[c*x])*Sqrt[d-c^2*d*x^2]+1/32*b*x^2*Sqrt[d-c^2*d*x^2]/(c^3*Sqrt[1-c^2*x^2])+1/96*b*x^4*Sqrt[d-c^2*d*x^2]/(c*Sqrt[1-c^2*x^2])-1/36*b*c*x^6*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]+1/32*(a+b*ArcSin[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c^5*Sqrt[1-c^2*x^2])");
  }

  // 5.5.1 u (a+b arcsec(c x))^n.input:167
  public void test0014() {
    check( //
        "Integrate[x*(d+e*x^2)^(3/2)*(a+b*ArcSec[c*x]), x]", //
        "1/5*(d+e*x^2)^(5/2)*(a+b*ArcSec[c*x])/e+1/5*b*c*d^(5/2)*x*ArcTan[Sqrt[d+e*x^2]/(Sqrt[d]*Sqrt[-1+c^2*x^2])]/(e*Sqrt[c^2*x^2])-1/40*b*(15*c^4*d^2+10*c^2*d*e+3*e^2)*x*ArcTanh[Sqrt[e]*Sqrt[-1+c^2*x^2]/(c*Sqrt[d+e*x^2])]/(c^4*Sqrt[e]*Sqrt[c^2*x^2])-1/20*b*x*(d+e*x^2)^(3/2)*Sqrt[-1+c^2*x^2]/(c*Sqrt[c^2*x^2])-1/40*b*(7*c^2*d+3*e)*x*Sqrt[-1+c^2*x^2]*Sqrt[d+e*x^2]/(c^3*Sqrt[c^2*x^2])");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:183
  public void test0015() {
    check( //
        "Integrate[(a+b*ArcCos[c*x])^2, x]", //
        "-2*b^2*x+x*(a+b*ArcCos[c*x])^2-2*b*(a+b*ArcCos[c*x])*Sqrt[1-c^2*x^2]/c");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:397
  public void test0016() {
    check( //
        "Integrate[(1-c^2*x^2)^(5/2)/(a+b*ArcSin[c*x]), x]", //
        "15/32*CosIntegral[2*(a+b*ArcSin[c*x])/b]*Cos[2*a/b]/(b*c)+3/16*CosIntegral[4*(a+b*ArcSin[c*x])/b]*Cos[4*a/b]/(b*c)+1/32*CosIntegral[6*(a+b*ArcSin[c*x])/b]*Cos[6*a/b]/(b*c)+5/16*Log[a+b*ArcSin[c*x]]/(b*c)+15/32*SinIntegral[2*(a+b*ArcSin[c*x])/b]*Sin[2*a/b]/(b*c)+3/16*SinIntegral[4*(a+b*ArcSin[c*x])/b]*Sin[4*a/b]/(b*c)+1/32*SinIntegral[6*(a+b*ArcSin[c*x])/b]*Sin[6*a/b]/(b*c)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:285
  public void test0017() {
    check( //
        "Integrate[ArcTan[a*x]/(x*(c+a^2*c*x^2)^(3/2)), x]", //
        "-a*x/(c*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]/(c*Sqrt[c+a^2*c*x^2])-2*ArcTan[a*x]*ArcTanh[Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])+I*PolyLog[2,-Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])-I*PolyLog[2,Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])");
  }

  // 5.3.7 Inverse tangent functions.input:185
  public void test0018() {
    check( //
        "Integrate[ArcTan[c*x/Sqrt[a-c^2*x^2]]^m/Sqrt[d-c^2*d*x^2/a], x]", //
        "ArcTan[c*x/Sqrt[a-c^2*x^2]]^(1+m)*Sqrt[a-c^2*x^2]/(c*(1+m)*Sqrt[d-c^2*d*x^2/a])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:120
  public void test0019() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])^2/(x^3*(d+I*c*d*x)), x]", //
        "-b*c*(a+b*ArcTan[c*x])/(d*x)-3/2*c^2*(a+b*ArcTan[c*x])^2/d-1/2*(a+b*ArcTan[c*x])^2/(d*x^2)+I*c*(a+b*ArcTan[c*x])^2/(d*x)+b^2*c^2*Log[x]/d-1/2*b^2*c^2*Log[1+c^2*x^2]/d-2*I*b*c^2*(a+b*ArcTan[c*x])*Log[2+(-2)/(1-I*c*x)]/d-c^2*(a+b*ArcTan[c*x])^2*Log[2+(-2)/(1+I*c*x)]/d-b^2*c^2*PolyLog[2,-1+2/(1-I*c*x)]/d-I*b*c^2*(a+b*ArcTan[c*x])*PolyLog[2,-1+2/(1+I*c*x)]/d-1/2*b^2*c^2*PolyLog[3,-1+2/(1+I*c*x)]/d");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:389
  public void test0020() {
    check( //
        "Integrate[(1-c^2*x^2)^(3/2)/(a+b*ArcSin[c*x]), x]", //
        "1/2*CosIntegral[2*(a+b*ArcSin[c*x])/b]*Cos[2*a/b]/(b*c)+1/8*CosIntegral[4*(a+b*ArcSin[c*x])/b]*Cos[4*a/b]/(b*c)+3/8*Log[a+b*ArcSin[c*x]]/(b*c)+1/2*SinIntegral[2*(a+b*ArcSin[c*x])/b]*Sin[2*a/b]/(b*c)+1/8*SinIntegral[4*(a+b*ArcSin[c*x])/b]*Sin[4*a/b]/(b*c)");
  }

  // 5.3.7 Inverse tangent functions.input:157
  public void test0021() {
    check( //
        "Integrate[ArcTan[a+b*f^(c+d*x)], x]", //
        "-ArcTan[a+b*f^(c+d*x)]*Log[2/(1-I*(a+b*f^(c+d*x)))]/(d*Log[f])+ArcTan[a+b*f^(c+d*x)]*Log[2*b*f^(c+d*x)/((I-a)*(1-I*(a+b*f^(c+d*x))))]/(d*Log[f])+1/2*I*PolyLog[2,1+(-2)/(1-I*(a+b*f^(c+d*x)))]/(d*Log[f])-1/2*I*PolyLog[2,1-2*b*f^(c+d*x)/((I-a)*(1-I*(a+b*f^(c+d*x))))]/(d*Log[f])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:325
  public void test0022() {
    check( //
        "Integrate[x*(c+a^2*c*x^2)^2*ArcTan[a*x]^2, x]", //
        "2/45*c^2*(1+a^2*x^2)/a^2+1/60*c^2*(1+a^2*x^2)^2/a^2-8/45*c^2*x*ArcTan[a*x]/a-4/45*c^2*x*(1+a^2*x^2)*ArcTan[a*x]/a-1/15*c^2*x*(1+a^2*x^2)^2*ArcTan[a*x]/a+1/6*c^2*(1+a^2*x^2)^3*ArcTan[a*x]^2/a^2+4/45*c^2*Log[1+a^2*x^2]/a^2");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:212
  public void test0023() {
    check( //
        "Integrate[(d-c^2*d*x^2)^3*(a+b*ArcSin[c*x])^2/x, x]", //
        "71/144*b^2*c^2*d^3*x^2-7/144*b^2*c^4*d^3*x^4-1/108*b^2*d^3*(1-c^2*x^2)^3-7/36*b*c*d^3*x*(1-c^2*x^2)^(3/2)*(a+b*ArcSin[c*x])-1/18*b*c*d^3*x*(1-c^2*x^2)^(5/2)*(a+b*ArcSin[c*x])-19/48*d^3*(a+b*ArcSin[c*x])^2+1/2*d^3*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2+1/4*d^3*(1-c^2*x^2)^2*(a+b*ArcSin[c*x])^2+1/6*d^3*(1-c^2*x^2)^3*(a+b*ArcSin[c*x])^2-1/3*I*d^3*(a+b*ArcSin[c*x])^3/b+d^3*(a+b*ArcSin[c*x])^2*Log[1-E^(2*I*ArcSin[c*x])]-I*b*d^3*(a+b*ArcSin[c*x])*PolyLog[2,E^(2*I*ArcSin[c*x])]+1/2*b^2*d^3*PolyLog[3,E^(2*I*ArcSin[c*x])]-19/24*b*c*d^3*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]");
  }

  // 5.2.5 Inverse cosine functions.input:170
  public void test0024() {
    check( //
        "Integrate[(a+b*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])^2/(1-c^2*x^2), x]", //
        "1/3*I*(a+b*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])^3/(b*c)-(a+b*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])^2*Log[1+E^(2*I*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])]/c+I*b*(a+b*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])*PolyLog[2,-E^(2*I*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])]/c-1/2*b^2*PolyLog[3,-E^(2*I*ArcCos[Sqrt[1-c*x]/Sqrt[1+c*x]])]/c");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:714
  public void test0025() {
    check( //
        "Integrate[(d+c*d*x)^(1/2)*(e-c*e*x)^(1/2)*(a+b*ArcSin[c*x])^2/x, x]", //
        "-2*b^2*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]+(a+b*ArcSin[c*x])^2*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]-2*a*b*c*x*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]-2*b^2*c*x*ArcSin[c*x]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]-2*(a+b*ArcSin[c*x])^2*ArcTanh[E^(I*ArcSin[c*x])]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]+2*I*b*(a+b*ArcSin[c*x])*PolyLog[2,-E^(I*ArcSin[c*x])]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]-2*I*b*(a+b*ArcSin[c*x])*PolyLog[2,E^(I*ArcSin[c*x])]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]-2*b^2*PolyLog[3,-E^(I*ArcSin[c*x])]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]+2*b^2*PolyLog[3,E^(I*ArcSin[c*x])]*Sqrt[d+c*d*x]*Sqrt[e-c*e*x]/Sqrt[1-c^2*x^2]");
  }

  // 5.4.1 Inverse cotangent functions.input:119
  public void test0026() {
    check( //
        "Integrate[ArcCot[Sqrt[x]]/x, x]", //
        "-I*PolyLog[2,(-I)/Sqrt[x]]+I*PolyLog[2,I/Sqrt[x]]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:98
  public void test0027() {
    check( //
        "Integrate[(d+I*c*d*x)^2*(a+b*ArcTan[c*x])^2/x^2, x]", //
        "-2*I*c*d^2*(a+b*ArcTan[c*x])^2-d^2*(a+b*ArcTan[c*x])^2/x-c^2*d^2*x*(a+b*ArcTan[c*x])^2+4*I*c*d^2*(a+b*ArcTan[c*x])^2*ArcTanh[1+(-2)/(1+I*c*x)]-2*b*c*d^2*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]+2*b*c*d^2*(a+b*ArcTan[c*x])*Log[2+(-2)/(1-I*c*x)]-I*b^2*c*d^2*PolyLog[2,-1+2/(1-I*c*x)]-I*b^2*c*d^2*PolyLog[2,1+(-2)/(1+I*c*x)]+2*b*c*d^2*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]-2*b*c*d^2*(a+b*ArcTan[c*x])*PolyLog[2,-1+2/(1+I*c*x)]-I*b^2*c*d^2*PolyLog[3,1+(-2)/(1+I*c*x)]+I*b^2*c*d^2*PolyLog[3,-1+2/(1+I*c*x)]");
  }

  // 5.3.6 Exponentials of inverse tangent.input:396
  public void test0028() {
    check( //
        "Integrate[E^(2*I*ArcTan[a*x])/(c+a^2*c*x^2)^(3/2), x]", //
        "-2/3*I*(1+I*a*x)/(a*(c+a^2*c*x^2)^(3/2))+1/3*x/(c*Sqrt[c+a^2*c*x^2])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:278
  public void test0029() {
    check( //
        "Integrate[ArcTan[a*x]/(x^2*Sqrt[c+a^2*c*x^2]), x]", //
        "-a*ArcTanh[Sqrt[c+a^2*c*x^2]/Sqrt[c]]/Sqrt[c]-ArcTan[a*x]*Sqrt[c+a^2*c*x^2]/(c*x)");
  }

  // 5.1.5 Inverse sine functions.input:347
  public void test0030() {
    check( //
        "Integrate[(a+b*ArcSin[c+d*x])^(3/2), x]", //
        "(c+d*x)*(a+b*ArcSin[c+d*x])^(3/2)/d-3/2*b^(3/2)*Cos[a/b]*FresnelC[Sqrt[2/Pi]*Sqrt[a+b*ArcSin[c+d*x]]/Sqrt[b]]*Sqrt[1/2*Pi]/d-3/2*b^(3/2)*FresnelS[Sqrt[2/Pi]*Sqrt[a+b*ArcSin[c+d*x]]/Sqrt[b]]*Sin[a/b]*Sqrt[1/2*Pi]/d+3/2*b*Sqrt[1-(c+d*x)^2]*Sqrt[a+b*ArcSin[c+d*x]]/d");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:96
  public void test0031() {
    check( //
        "Integrate[x^5*(a+b*ArcTan[c*x^2]), x]", //
        "-1/12*b*x^4/c+1/6*x^6*(a+b*ArcTan[c*x^2])+1/12*b*Log[1+c^2*x^4]/c^3");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1536
  public void test0032() {
    check( //
        "Integrate[ArcTan[x]*Log[1+x^2]/x^2, x]", //
        "ArcTan[x]^2-ArcTan[x]*Log[1+x^2]/x-1/4*Log[1+x^2]^2-1/2*PolyLog[2,-x^2]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:493
  public void test0033() {
    check( //
        "Integrate[x*ArcTan[a*x]^3*Sqrt[c+a^2*c*x^2], x]", //
        "1/3*(c+a^2*c*x^2)^(3/2)*ArcTan[a*x]^3/(a^2*c)-ArcTanh[a*x*Sqrt[c]/Sqrt[c+a^2*c*x^2]]*Sqrt[c]/a^2+I*c*ArcTan[E^(I*ArcTan[a*x])]*ArcTan[a*x]^2*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])-I*c*ArcTan[a*x]*PolyLog[2,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+I*c*ArcTan[a*x]*PolyLog[2,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+c*PolyLog[3,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])-c*PolyLog[3,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a^2*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]*Sqrt[c+a^2*c*x^2]/a^2-1/2*x*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]/a");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:761
  public void test0034() {
    check( //
        "Integrate[x^2/((c+a^2*c*x^2)^3*ArcTan[a*x]^3), x]", //
        "1/2/(a^3*c^3*(1+a^2*x^2)^2*ArcTan[a*x]^2)+(-1/2)/(a^3*c^3*(1+a^2*x^2)*ArcTan[a*x]^2)-2*x/(a^2*c^3*(1+a^2*x^2)^2*ArcTan[a*x])+x/(a^2*c^3*(1+a^2*x^2)*ArcTan[a*x])+CosIntegral[4*ArcTan[a*x]]/(a^3*c^3)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:63
  public void test0035() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])/(x^4*(d+I*c*d*x)), x]", //
        "-1/6*b*c/(d*x^2)+1/2*I*b*c^2/(d*x)+1/2*I*b*c^3*ArcTan[c*x]/d+1/3*(-a-b*ArcTan[c*x])/(d*x^3)+1/2*I*c*(a+b*ArcTan[c*x])/(d*x^2)+c^2*(a+b*ArcTan[c*x])/(d*x)-4/3*b*c^3*Log[x]/d+2/3*b*c^3*Log[1+c^2*x^2]/d+I*c^3*(a+b*ArcTan[c*x])*Log[2+(-2)/(1+I*c*x)]/d-1/2*b*c^3*PolyLog[2,-1+2/(1+I*c*x)]/d");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:46
  public void test0036() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])/(x*(d-c^2*d*x^2)), x]", //
        "-2*(a+b*ArcSin[c*x])*ArcTanh[E^(2*I*ArcSin[c*x])]/d+1/2*I*b*PolyLog[2,-E^(2*I*ArcSin[c*x])]/d-1/2*I*b*PolyLog[2,E^(2*I*ArcSin[c*x])]/d");
  }

  // 5.1.5 Inverse sine functions.input:433
  public void test0037() {
    check( //
        "Integrate[(1-a^2-2*a*b*x-b^2*x^2)^(3/2)*ArcSin[a+b*x]^3, x]", //
        "51/128*(a+b*x)^2/b-3/128*(a+b*x)^4/b-3/32*(a+b*x)*(1-(a+b*x)^2)^(3/2)*ArcSin[a+b*x]/b+27/128*ArcSin[a+b*x]^2/b-9/16*(a+b*x)^2*ArcSin[a+b*x]^2/b+3/16*(1-(a+b*x)^2)^2*ArcSin[a+b*x]^2/b+1/4*(a+b*x)*(1-(a+b*x)^2)^(3/2)*ArcSin[a+b*x]^3/b+3/32*ArcSin[a+b*x]^4/b-45/64*(a+b*x)*ArcSin[a+b*x]*Sqrt[1-(a+b*x)^2]/b+3/8*(a+b*x)*ArcSin[a+b*x]^3*Sqrt[1-(a+b*x)^2]/b");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:768
  public void test0038() {
    check( //
        "Integrate[x^3/((1+a^2*x^2)*ArcTan[a*x]^3)-3/2*x^2/(a*ArcTan[a*x]^2), x]", //
        "-1/2*x^3/(a*ArcTan[a*x]^2)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:582
  public void test0039() {
    check( //
        "Integrate[x^2/((c+a^2*c*x^2)^3*ArcTan[a*x]), x]", //
        "-1/8*CosIntegral[4*ArcTan[a*x]]/(a^3*c^3)+1/8*Log[ArcTan[a*x]]/(a^3*c^3)");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:560
  public void test0040() {
    check( //
        "Integrate[(a^2-x^2)^(1/2)*ArcSin[x/a]^(3/2), x]", //
        "1/2*x*ArcSin[x/a]^(3/2)*Sqrt[a^2-x^2]+1/5*a*ArcSin[x/a]^(5/2)*Sqrt[a^2-x^2]/Sqrt[1-x^2/a^2]-3/32*a*FresnelC[2*Sqrt[ArcSin[x/a]]/Sqrt[Pi]]*Sqrt[Pi]*Sqrt[a^2-x^2]/Sqrt[1-x^2/a^2]+3/16*a*Sqrt[a^2-x^2]*Sqrt[ArcSin[x/a]]/Sqrt[1-x^2/a^2]-3/8*x^2*Sqrt[a^2-x^2]*Sqrt[ArcSin[x/a]]/(a*Sqrt[1-x^2/a^2])");
  }

  // 5.6.1 u (a+b arccsc(c x))^n.input:51
  public void test0041() {
    check( //
        "Integrate[1/(x^4*(a+b*ArcCsc[c*x])), x]", //
        "-1/4*c^3*CosIntegral[a/b+ArcCsc[c*x]]*Cos[a/b]/b+1/4*c^3*CosIntegral[3*a/b+3*ArcCsc[c*x]]*Cos[3*a/b]/b-1/4*c^3*SinIntegral[a/b+ArcCsc[c*x]]*Sin[a/b]/b+1/4*c^3*SinIntegral[3*a/b+3*ArcCsc[c*x]]*Sin[3*a/b]/b");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:136
  public void test0042() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])/(x^3*(d-c^2*d*x^2)^(1/2)), x]", //
        "-1/2*b*c*Sqrt[1-c^2*x^2]/(x*Sqrt[d-c^2*d*x^2])-c^2*(a+b*ArcSin[c*x])*ArcTanh[E^(I*ArcSin[c*x])]*Sqrt[1-c^2*x^2]/Sqrt[d-c^2*d*x^2]+1/2*I*b*c^2*PolyLog[2,-E^(I*ArcSin[c*x])]*Sqrt[1-c^2*x^2]/Sqrt[d-c^2*d*x^2]-1/2*I*b*c^2*PolyLog[2,E^(I*ArcSin[c*x])]*Sqrt[1-c^2*x^2]/Sqrt[d-c^2*d*x^2]-1/2*(a+b*ArcSin[c*x])*Sqrt[d-c^2*d*x^2]/(d*x^2)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:346
  public void test0043() {
    check( //
        "Integrate[ArcTan[a*x]^2/(x*(c+a^2*c*x^2)), x]", //
        "-1/3*I*ArcTan[a*x]^3/c+ArcTan[a*x]^2*Log[2+(-2)/(1-I*a*x)]/c-I*ArcTan[a*x]*PolyLog[2,-1+2/(1-I*a*x)]/c+1/2*PolyLog[3,-1+2/(1-I*a*x)]/c");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:122
  public void test0044() {
    check( //
        "Integrate[x^2/ArcCos[a*x]^(3/2), x]", //
        "-FresnelC[Sqrt[2/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[1/2*Pi]/a^3-FresnelC[Sqrt[6/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[3/2*Pi]/a^3+2*x^2*Sqrt[1-a^2*x^2]/(a*Sqrt[ArcCos[a*x]])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:344
  public void test0045() {
    check( //
        "Integrate[x*ArcTan[a*x]^2/(c+a^2*c*x^2), x]", //
        "-1/3*I*ArcTan[a*x]^3/(a^2*c)-ArcTan[a*x]^2*Log[2/(1+I*a*x)]/(a^2*c)-I*ArcTan[a*x]*PolyLog[2,1+(-2)/(1+I*a*x)]/(a^2*c)-1/2*PolyLog[3,1+(-2)/(1+I*a*x)]/(a^2*c)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:361
  public void test0046() {
    check( //
        "Integrate[ArcTan[a*x]^2/(c+a^2*c*x^2)^3, x]", //
        "-1/32*x/(c^3*(1+a^2*x^2)^2)-15/64*x/(c^3*(1+a^2*x^2))-15/64*ArcTan[a*x]/(a*c^3)+1/8*ArcTan[a*x]/(a*c^3*(1+a^2*x^2)^2)+3/8*ArcTan[a*x]/(a*c^3*(1+a^2*x^2))+1/4*x*ArcTan[a*x]^2/(c^3*(1+a^2*x^2)^2)+3/8*x*ArcTan[a*x]^2/(c^3*(1+a^2*x^2))+1/8*ArcTan[a*x]^3/(a*c^3)");
  }

  // 5.3.7 Inverse tangent functions.input:122
  public void test0047() {
    check( //
        "Integrate[x^2*ArcTan[c+(I+c)*Tanh[a+b*x]], x]", //
        "-1/12*I*b*x^4+1/3*x^3*ArcTan[c+(I+c)*Tanh[a+b*x]]+1/6*I*x^3*Log[1+I*E^(2*a+2*b*x)*c]+1/4*I*x^2*PolyLog[2,-I*E^(2*a+2*b*x)*c]/b-1/4*I*x*PolyLog[3,-I*E^(2*a+2*b*x)*c]/b^2+1/8*I*PolyLog[4,-I*E^(2*a+2*b*x)*c]/b^3");
  }

  // 5.5.1 u (a+b arcsec(c x))^n.input:158
  public void test0048() {
    check( //
        "Integrate[x*(a+b*ArcSec[c*x])*Sqrt[d+e*x^2], x]", //
        "1/3*(d+e*x^2)^(3/2)*(a+b*ArcSec[c*x])/e+1/3*b*c*d^(3/2)*x*ArcTan[Sqrt[d+e*x^2]/(Sqrt[d]*Sqrt[-1+c^2*x^2])]/(e*Sqrt[c^2*x^2])-1/6*b*(3*c^2*d+e)*x*ArcTanh[Sqrt[e]*Sqrt[-1+c^2*x^2]/(c*Sqrt[d+e*x^2])]/(c^2*Sqrt[e]*Sqrt[c^2*x^2])-1/6*b*x*Sqrt[-1+c^2*x^2]*Sqrt[d+e*x^2]/(c*Sqrt[c^2*x^2])");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:29
  public void test0049() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])^2, x]", //
        "I*(a+b*ArcTan[c*x])^2/c+x*(a+b*ArcTan[c*x])^2+2*b*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]/c+I*b^2*PolyLog[2,1+(-2)/(1+I*c*x)]/c");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:118
  public void test0050() {
    check( //
        "Integrate[x^6/ArcCos[a*x]^(3/2), x]", //
        "-5/16*FresnelC[Sqrt[2/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[1/2*Pi]/a^7-9/16*FresnelC[Sqrt[6/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[3/2*Pi]/a^7-5/16*FresnelC[Sqrt[10/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[5/2*Pi]/a^7-1/16*FresnelC[Sqrt[14/Pi]*Sqrt[ArcCos[a*x]]]*Sqrt[7/2*Pi]/a^7+2*x^6*Sqrt[1-a^2*x^2]/(a*Sqrt[ArcCos[a*x]])");
  }

  // 5.5.2 Inverse secant functions.input:16
  public void test0051() {
    check( //
        "Integrate[ArcSec[Sqrt[x]], x]", //
        "x*ArcSec[Sqrt[x]]-Sqrt[-1+x]");
  }

  // 5.1.5 Inverse sine functions.input:214
  public void test0052() {
    check( //
        "Integrate[x*ArcSin[a+b*x]^3, x]", //
        "3/8*ArcSin[a+b*x]/b^2+6*a*(a+b*x)*ArcSin[a+b*x]/b^2-3/4*(a+b*x)^2*ArcSin[a+b*x]/b^2-1/4*ArcSin[a+b*x]^3/b^2-1/2*a^2*ArcSin[a+b*x]^3/b^2+1/2*x^2*ArcSin[a+b*x]^3+6*a*Sqrt[1-(a+b*x)^2]/b^2-3/8*(a+b*x)*Sqrt[1-(a+b*x)^2]/b^2-3*a*ArcSin[a+b*x]^2*Sqrt[1-(a+b*x)^2]/b^2+3/4*(a+b*x)*ArcSin[a+b*x]^2*Sqrt[1-(a+b*x)^2]/b^2");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:702
  public void test0053() {
    check( //
        "Integrate[x^3/((c+a^2*c*x^2)^(5/2)*ArcTan[a*x]^2), x]", //
        "-x^3/(a*c*(c+a^2*c*x^2)^(3/2)*ArcTan[a*x])+3/4*CosIntegral[ArcTan[a*x]]*Sqrt[1+a^2*x^2]/(a^4*c^2*Sqrt[c+a^2*c*x^2])-3/4*CosIntegral[3*ArcTan[a*x]]*Sqrt[1+a^2*x^2]/(a^4*c^2*Sqrt[c+a^2*c*x^2])");
  }

  // 5.4.1 Inverse cotangent functions.input:223
  public void test0054() {
    check( //
        "Integrate[(a+b*ArcCot[Sqrt[1-c*x]/Sqrt[1+c*x]])/(1-c^2*x^2), x]", //
        "-a*Log[Sqrt[1-c*x]/Sqrt[1+c*x]]/c+1/2*I*b*PolyLog[2,-I*Sqrt[1+c*x]/Sqrt[1-c*x]]/c-1/2*I*b*PolyLog[2,I*Sqrt[1+c*x]/Sqrt[1-c*x]]/c");
  }

  // 5.1.5 Inverse sine functions.input:611
  public void test0055() {
    check( //
        "Integrate[E^ArcSin[a*x]*(1-a^2*x^2)^(3/2), x]", //
        "24/85*E^ArcSin[a*x]/a+12/85*E^ArcSin[a*x]*(1-a^2*x^2)/a+4/17*E^ArcSin[a*x]*x*(1-a^2*x^2)^(3/2)+1/17*E^ArcSin[a*x]*(1-a^2*x^2)^2/a+24/85*E^ArcSin[a*x]*x*Sqrt[1-a^2*x^2]");
  }

  // 5.4.1 Inverse cotangent functions.input:273
  public void test0056() {
    check( //
        "Integrate[ArcCot[c+d*Tanh[a+b*x]], x]", //
        "x*ArcCot[c+d*Tanh[a+b*x]]-1/2*I*x*Log[1+E^(2*a+2*b*x)*(I-c-d)/(I-c+d)]+1/2*I*x*Log[1+E^(2*a+2*b*x)*(I+c+d)/(I+c-d)]-1/4*I*PolyLog[2,-E^(2*a+2*b*x)*(I-c-d)/(I-c+d)]/b+1/4*I*PolyLog[2,-E^(2*a+2*b*x)*(I+c+d)/(I+c-d)]/b");
  }

  // 5.4.1 Inverse cotangent functions.input:18
  public void test0057() {
    check( //
        "Integrate[ArcCot[a*x]/x, x]", //
        "-1/2*I*PolyLog[2,(-I)/(a*x)]+1/2*I*PolyLog[2,I/(a*x)]");
  }

  // 5.3.3 (d+e x)^m (a+b arctan(c x^n))^p.input:26
  public void test0058() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])^3/(d+e*x)^2, x]", //
        "I*c*(a+b*ArcTan[c*x])^3/(c^2*d^2+e^2)+c^2*d*(a+b*ArcTan[c*x])^3/(e*(c^2*d^2+e^2))-(a+b*ArcTan[c*x])^3/(e*(d+e*x))-3*b*c*(a+b*ArcTan[c*x])^2*Log[2/(1-I*c*x)]/(c^2*d^2+e^2)+3*b*c*(a+b*ArcTan[c*x])^2*Log[2/(1+I*c*x)]/(c^2*d^2+e^2)+3*b*c*(a+b*ArcTan[c*x])^2*Log[2*c*(d+e*x)/((c*d+I*e)*(1-I*c*x))]/(c^2*d^2+e^2)+3*I*b^2*c*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1-I*c*x)]/(c^2*d^2+e^2)+3*I*b^2*c*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]/(c^2*d^2+e^2)-3*I*b^2*c*(a+b*ArcTan[c*x])*PolyLog[2,1-2*c*(d+e*x)/((c*d+I*e)*(1-I*c*x))]/(c^2*d^2+e^2)-3/2*b^3*c*PolyLog[3,1+(-2)/(1-I*c*x)]/(c^2*d^2+e^2)+3/2*b^3*c*PolyLog[3,1+(-2)/(1+I*c*x)]/(c^2*d^2+e^2)+3/2*b^3*c*PolyLog[3,1-2*c*(d+e*x)/((c*d+I*e)*(1-I*c*x))]/(c^2*d^2+e^2)");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:198
  public void test0059() {
    check( //
        "Integrate[x^2/(a+b*ArcCos[c*x])^2, x]", //
        "-1/4*CosIntegral[(a+b*ArcCos[c*x])/b]*Cos[a/b]/(b^2*c^3)-3/4*CosIntegral[3*(a+b*ArcCos[c*x])/b]*Cos[3*a/b]/(b^2*c^3)-1/4*SinIntegral[(a+b*ArcCos[c*x])/b]*Sin[a/b]/(b^2*c^3)-3/4*SinIntegral[3*(a+b*ArcCos[c*x])/b]*Sin[3*a/b]/(b^2*c^3)+x^2*Sqrt[1-c^2*x^2]/(b*c*(a+b*ArcCos[c*x]))");
  }

  // 5.1.5 Inverse sine functions.input:20
  public void test0060() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*ArcSin[c*x])^2, x]", //
        "-2*b^2*d^3*x-4/3*b^2*d*e^2*x/c^2-3/4*b^2*d^2*e*x^2-3/32*b^2*e^3*x^2/c^2-2/9*b^2*d*e^2*x^3-1/32*b^2*e^3*x^4-1/4*d^4*(a+b*ArcSin[c*x])^2/e-3/4*d^2*e*(a+b*ArcSin[c*x])^2/c^2-3/32*e^3*(a+b*ArcSin[c*x])^2/c^4+1/4*(d+e*x)^4*(a+b*ArcSin[c*x])^2/e+2*b*d^3*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+4/3*b*d*e^2*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c^3+3/2*b*d^2*e*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+3/16*b*e^3*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c^3+2/3*b*d*e^2*x^2*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+1/8*b*e^3*x^3*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c");
  }

  // 5.1.5 Inverse sine functions.input:571
  public void test0061() {
    check( //
        "Integrate[(a+b*ArcSin[Sqrt[1-c*x]/Sqrt[1+c*x]])/(1-c^2*x^2), x]", //
        "1/2*I*(a+b*ArcSin[Sqrt[1-c*x]/Sqrt[1+c*x]])^2/(b*c)-(a+b*ArcSin[Sqrt[1-c*x]/Sqrt[1+c*x]])*Log[1-E^(2*I*ArcSin[Sqrt[1-c*x]/Sqrt[1+c*x]])]/c+1/2*I*b*PolyLog[2,E^(2*I*ArcSin[Sqrt[1-c*x]/Sqrt[1+c*x]])]/c");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:319
  public void test0062() {
    check( //
        "Integrate[(c+a^2*c*x^2)*ArcTan[a*x]^2/x, x]", //
        "-a*c*x*ArcTan[a*x]+1/2*c*ArcTan[a*x]^2+1/2*a^2*c*x^2*ArcTan[a*x]^2+2*c*ArcTan[a*x]^2*ArcTanh[1+(-2)/(1+I*a*x)]+1/2*c*Log[1+a^2*x^2]-I*c*ArcTan[a*x]*PolyLog[2,1+(-2)/(1+I*a*x)]+I*c*ArcTan[a*x]*PolyLog[2,-1+2/(1+I*a*x)]-1/2*c*PolyLog[3,1+(-2)/(1+I*a*x)]+1/2*c*PolyLog[3,-1+2/(1+I*a*x)]");
  }

  // 5.1.5 Inverse sine functions.input:23
  public void test0063() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])^2, x]", //
        "-2*b^2*x+x*(a+b*ArcSin[c*x])^2+2*b*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:580
  public void test0064() {
    check( //
        "Integrate[1/((c-a^2*c*x^2)^(1/2)*ArcSin[a*x]^(5/2)), x]", //
        "-2/3*Sqrt[1-a^2*x^2]/(a*(c-a^2*c*x^2)^(1/2)*ArcSin[a*x]^(3/2))");
  }

  // 5.1.2 (d x)^m (a+b arcsin(c x))^n.input:68
  public void test0065() {
    check( //
        "Integrate[x^2/ArcSin[a*x]^2, x]", //
        "-1/4*SinIntegral[ArcSin[a*x]]/a^3+3/4*SinIntegral[3*ArcSin[a*x]]/a^3-x^2*Sqrt[1-a^2*x^2]/(a*ArcSin[a*x])");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:41
  public void test0066() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])^3/x, x]", //
        "2*(a+b*ArcTan[c*x])^3*ArcTanh[1+(-2)/(1+I*c*x)]-3/2*I*b*(a+b*ArcTan[c*x])^2*PolyLog[2,1+(-2)/(1+I*c*x)]+3/2*I*b*(a+b*ArcTan[c*x])^2*PolyLog[2,-1+2/(1+I*c*x)]-3/2*b^2*(a+b*ArcTan[c*x])*PolyLog[3,1+(-2)/(1+I*c*x)]+3/2*b^2*(a+b*ArcTan[c*x])*PolyLog[3,-1+2/(1+I*c*x)]+3/4*I*b^3*PolyLog[4,1+(-2)/(1+I*c*x)]-3/4*I*b^3*PolyLog[4,-1+2/(1+I*c*x)]");
  }

  // 5.1.5 Inverse sine functions.input:206
  public void test0067() {
    check( //
        "Integrate[x*ArcSin[a+b*x]^2, x]", //
        "2*a*x/b-1/4*(a+b*x)^2/b^2-1/4*ArcSin[a+b*x]^2/b^2-1/2*a^2*ArcSin[a+b*x]^2/b^2+1/2*x^2*ArcSin[a+b*x]^2-2*a*ArcSin[a+b*x]*Sqrt[1-(a+b*x)^2]/b^2+1/2*(a+b*x)*ArcSin[a+b*x]*Sqrt[1-(a+b*x)^2]/b^2");
  }

  // 5.2.5 Inverse cosine functions.input:131
  public void test0068() {
    check( //
        "Integrate[x^(-1+n)*ArcCos[a+b*x^n], x]", //
        "(a+b*x^n)*ArcCos[a+b*x^n]/(b*n)-Sqrt[1-(a+b*x^n)^2]/(b*n)");
  }

  // 5.3.7 Inverse tangent functions.input:47
  public void test0069() {
    check( //
        "Integrate[ArcTan[1+x+x^2]/x^2, x]", //
        "1/2*ArcTan[1+x]-ArcTan[1+x+x^2]/x+1/2*Log[x]-1/2*Log[1+x^2]+1/4*Log[2+2*x+x^2]");
  }

  // 5.1.5 Inverse sine functions.input:483
  public void test0070() {
    check( //
        "Integrate[ArcSin[a*x^5]/x, x]", //
        "-1/10*I*ArcSin[a*x^5]^2+1/5*ArcSin[a*x^5]*Log[1-E^(2*I*ArcSin[a*x^5])]-1/10*I*PolyLog[2,E^(2*I*ArcSin[a*x^5])]");
  }

  // 5.3.7 Inverse tangent functions.input:108
  public void test0071() {
    check( //
        "Integrate[x^2*ArcTan[Sinh[x]], x]", //
        "-2/3*x^3*ArcTan[E^x]+1/3*x^3*ArcTan[Sinh[x]]+I*x^2*PolyLog[2,-I*E^x]-I*x^2*PolyLog[2,I*E^x]-2*I*x*PolyLog[3,-I*E^x]+2*I*x*PolyLog[3,I*E^x]+2*I*PolyLog[4,-I*E^x]-2*I*PolyLog[4,I*E^x]");
  }

  // 5.4.1 Inverse cotangent functions.input:118
  public void test0072() {
    check( //
        "Integrate[ArcCot[Sqrt[x]], x]", //
        "x*ArcCot[Sqrt[x]]-ArcTan[Sqrt[x]]+Sqrt[x]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:105
  public void test0073() {
    check( //
        "Integrate[(d+I*c*d*x)^3*(a+b*ArcTan[c*x])^2/x, x]", //
        "3*a*b*c*d^3*x-1/3*I*b^2*c*d^3*x+1/3*I*b^2*d^3*ArcTan[c*x]+3*b^2*c*d^3*x*ArcTan[c*x]+1/3*I*b*c^2*d^3*x^2*(a+b*ArcTan[c*x])-29/6*d^3*(a+b*ArcTan[c*x])^2+3*I*c*d^3*x*(a+b*ArcTan[c*x])^2-3/2*c^2*d^3*x^2*(a+b*ArcTan[c*x])^2-1/3*I*c^3*d^3*x^3*(a+b*ArcTan[c*x])^2+2*d^3*(a+b*ArcTan[c*x])^2*ArcTanh[1+(-2)/(1+I*c*x)]+20/3*I*b*d^3*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]-3/2*b^2*d^3*Log[1+c^2*x^2]-10/3*b^2*d^3*PolyLog[2,1+(-2)/(1+I*c*x)]-I*b*d^3*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]+I*b*d^3*(a+b*ArcTan[c*x])*PolyLog[2,-1+2/(1+I*c*x)]-1/2*b^2*d^3*PolyLog[3,1+(-2)/(1+I*c*x)]+1/2*b^2*d^3*PolyLog[3,-1+2/(1+I*c*x)]");
  }

  // 5.3.6 Exponentials of inverse tangent.input:510
  public void test0074() {
    check( //
        "Integrate[E^(5*I*ArcTan[a*x])*x^2/(c+a^2*c*x^2)^(27/2), x]", //
        "-1/120*(I+5*a*x)*Sqrt[1+a^2*x^2]/(a^3*c^13*(1-I*a*x)^15*(1+I*a*x)^10*Sqrt[c+a^2*c*x^2])");
  }

  // 5.3.7 Inverse tangent functions.input:21
  public void test0075() {
    check( //
        "Integrate[ArcTan[x*Sqrt[-e]/Sqrt[d+e*x^2]]/x^9, x]", //
        "-1/8*ArcTan[x*Sqrt[-e]/Sqrt[d+e*x^2]]/x^8-3/140*(-e)^(3/2)*Sqrt[d+e*x^2]/(d^2*x^5)-1/35*(-e)^(5/2)*Sqrt[d+e*x^2]/(d^3*x^3)-2/35*(-e)^(7/2)*Sqrt[d+e*x^2]/(d^4*x)-1/56*Sqrt[-e]*Sqrt[d+e*x^2]/(d*x^7)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1497
  public void test0076() {
    check( //
        "Integrate[x^2*(d+e*x^2)*(a+b*ArcTan[c*x])^2, x]", //
        "1/3*b^2*d*x/c^2-3/10*b^2*e*x/c^4+1/30*b^2*e*x^3/c^2-1/3*b^2*d*ArcTan[c*x]/c^3+3/10*b^2*e*ArcTan[c*x]/c^5-1/3*b*d*x^2*(a+b*ArcTan[c*x])/c+1/5*b*e*x^2*(a+b*ArcTan[c*x])/c^3-1/10*b*e*x^4*(a+b*ArcTan[c*x])/c-1/3*I*d*(a+b*ArcTan[c*x])^2/c^3+1/5*I*e*(a+b*ArcTan[c*x])^2/c^5+1/3*d*x^3*(a+b*ArcTan[c*x])^2+1/5*e*x^5*(a+b*ArcTan[c*x])^2-2/3*b*d*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]/c^3+2/5*b*e*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]/c^5-1/3*I*b^2*d*PolyLog[2,1+(-2)/(1+I*c*x)]/c^3+1/5*I*b^2*e*PolyLog[2,1+(-2)/(1+I*c*x)]/c^5");
  }

  // 5.5.2 Inverse secant functions.input:17
  public void test0077() {
    check( //
        "Integrate[ArcSec[Sqrt[x]]/x, x]", //
        "I*ArcSec[Sqrt[x]]^2-2*ArcSec[Sqrt[x]]*Log[1+E^(2*I*ArcSec[Sqrt[x]])]+I*PolyLog[2,-E^(2*I*ArcSec[Sqrt[x]])]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:114
  public void test0078() {
    check( //
        "Integrate[x^3*(a+b*ArcTan[c*x])^2/(d+I*c*d*x), x]", //
        "-a*b*x/(c^3*d)-1/3*I*b^2*x/(c^3*d)+1/3*I*b^2*ArcTan[c*x]/(c^4*d)-b^2*x*ArcTan[c*x]/(c^3*d)+1/3*I*b*x^2*(a+b*ArcTan[c*x])/(c^2*d)-5/6*(a+b*ArcTan[c*x])^2/(c^4*d)+I*x*(a+b*ArcTan[c*x])^2/(c^3*d)+1/2*x^2*(a+b*ArcTan[c*x])^2/(c^2*d)-1/3*I*x^3*(a+b*ArcTan[c*x])^2/(c*d)+8/3*I*b*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]/(c^4*d)+(a+b*ArcTan[c*x])^2*Log[2/(1+I*c*x)]/(c^4*d)+1/2*b^2*Log[1+c^2*x^2]/(c^4*d)-4/3*b^2*PolyLog[2,1+(-2)/(1+I*c*x)]/(c^4*d)+I*b*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]/(c^4*d)+1/2*b^2*PolyLog[3,1+(-2)/(1+I*c*x)]/(c^4*d)");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:176
  public void test0079() {
    check( //
        "Integrate[a+b*ArcCos[c*x], x]", //
        "a*x+b*x*ArcCos[c*x]-b*Sqrt[1-c^2*x^2]/c");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:388
  public void test0080() {
    check( //
        "Integrate[x*(1-c^2*x^2)^(3/2)/(a+b*ArcSin[c*x]), x]", //
        "1/8*Cos[a/b]*SinIntegral[(a+b*ArcSin[c*x])/b]/(b*c^2)+3/16*Cos[3*a/b]*SinIntegral[3*(a+b*ArcSin[c*x])/b]/(b*c^2)+1/16*Cos[5*a/b]*SinIntegral[5*(a+b*ArcSin[c*x])/b]/(b*c^2)-1/8*CosIntegral[(a+b*ArcSin[c*x])/b]*Sin[a/b]/(b*c^2)-3/16*CosIntegral[3*(a+b*ArcSin[c*x])/b]*Sin[3*a/b]/(b*c^2)-1/16*CosIntegral[5*(a+b*ArcSin[c*x])/b]*Sin[5*a/b]/(b*c^2)");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:221
  public void test0081() {
    check( //
        "Integrate[x*(a+b*ArcSin[c*x])^2/(d-c^2*d*x^2), x]", //
        "1/3*I*(a+b*ArcSin[c*x])^3/(b*c^2*d)-(a+b*ArcSin[c*x])^2*Log[1+E^(2*I*ArcSin[c*x])]/(c^2*d)+I*b*(a+b*ArcSin[c*x])*PolyLog[2,-E^(2*I*ArcSin[c*x])]/(c^2*d)-1/2*b^2*PolyLog[3,-E^(2*I*ArcSin[c*x])]/(c^2*d)");
  }

  // 5.4.1 Inverse cotangent functions.input:287
  public void test0082() {
    check( //
        "Integrate[(e+f*x)*ArcCot[Coth[a+b*x]], x]", //
        "1/2*(e+f*x)^2*ArcCot[Coth[a+b*x]]/f-1/2*(e+f*x)^2*ArcTan[E^(2*a+2*b*x)]/f+1/4*I*(e+f*x)*PolyLog[2,-I*E^(2*a+2*b*x)]/b-1/4*I*(e+f*x)*PolyLog[2,I*E^(2*a+2*b*x)]/b-1/8*I*f*PolyLog[3,-I*E^(2*a+2*b*x)]/b^2+1/8*I*f*PolyLog[3,I*E^(2*a+2*b*x)]/b^2");
  }

  // 5.5.1 u (a+b arcsec(c x))^n.input:49
  public void test0083() {
    check( //
        "Integrate[1/(x^2*(a+b*ArcSec[c*x])), x]", //
        "c*Cos[a/b]*SinIntegral[a/b+ArcSec[c*x]]/b-c*CosIntegral[a/b+ArcSec[c*x]]*Sin[a/b]/b");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:219
  public void test0084() {
    check( //
        "Integrate[x^3*(a+b*ArcSin[c*x])^2/(d-c^2*d*x^2), x]", //
        "1/4*b^2*x^2/(c^2*d)+1/4*(a+b*ArcSin[c*x])^2/(c^4*d)-1/2*x^2*(a+b*ArcSin[c*x])^2/(c^2*d)+1/3*I*(a+b*ArcSin[c*x])^3/(b*c^4*d)-(a+b*ArcSin[c*x])^2*Log[1+E^(2*I*ArcSin[c*x])]/(c^4*d)+I*b*(a+b*ArcSin[c*x])*PolyLog[2,-E^(2*I*ArcSin[c*x])]/(c^4*d)-1/2*b^2*PolyLog[3,-E^(2*I*ArcSin[c*x])]/(c^4*d)-1/2*b*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/(c^3*d)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:510
  public void test0085() {
    check( //
        "Integrate[(c+a^2*c*x^2)^(5/2)*ArcTan[a*x]^3, x]", //
        "-1/60*c*(c+a^2*c*x^2)^(3/2)/a+1/20*c*x*(c+a^2*c*x^2)^(3/2)*ArcTan[a*x]-5/24*c*(c+a^2*c*x^2)^(3/2)*ArcTan[a*x]^2/a-1/10*(c+a^2*c*x^2)^(5/2)*ArcTan[a*x]^2/a+5/24*c*x*(c+a^2*c*x^2)^(3/2)*ArcTan[a*x]^3+1/6*x*(c+a^2*c*x^2)^(5/2)*ArcTan[a*x]^3-5/8*I*c^3*ArcTan[E^(I*ArcTan[a*x])]*ArcTan[a*x]^3*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-259/60*I*c^3*ArcTan[a*x]*ArcTan[Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])+15/16*I*c^3*ArcTan[a*x]^2*PolyLog[2,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-15/16*I*c^3*ArcTan[a*x]^2*PolyLog[2,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])+259/120*I*c^3*PolyLog[2,-I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-259/120*I*c^3*PolyLog[2,I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-15/8*c^3*ArcTan[a*x]*PolyLog[3,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])+15/8*c^3*ArcTan[a*x]*PolyLog[3,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-15/8*I*c^3*PolyLog[4,-I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])+15/8*I*c^3*PolyLog[4,I*E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(a*Sqrt[c+a^2*c*x^2])-17/60*c^2*Sqrt[c+a^2*c*x^2]/a+17/60*c^2*x*ArcTan[a*x]*Sqrt[c+a^2*c*x^2]-15/16*c^2*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]/a+5/16*c^2*x*ArcTan[a*x]^3*Sqrt[c+a^2*c*x^2]");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:688
  public void test0086() {
    check( //
        "Integrate[(d+c*d*x)^(3/2)*(a+b*ArcSin[c*x])^2/Sqrt[e-c*e*x], x]", //
        "4*b^2*d^2*(1-c^2*x^2)/(c*Sqrt[d+c*d*x]*Sqrt[e-c*e*x])+1/4*b^2*d^2*x*(1-c^2*x^2)/(Sqrt[d+c*d*x]*Sqrt[e-c*e*x])-2*d^2*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2/(c*Sqrt[d+c*d*x]*Sqrt[e-c*e*x])-1/2*d^2*x*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2/(Sqrt[d+c*d*x]*Sqrt[e-c*e*x])-1/4*b^2*d^2*ArcSin[c*x]*Sqrt[1-c^2*x^2]/(c*Sqrt[d+c*d*x]*Sqrt[e-c*e*x])+4*b*d^2*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/(Sqrt[d+c*d*x]*Sqrt[e-c*e*x])+1/2*b*c*d^2*x^2*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/(Sqrt[d+c*d*x]*Sqrt[e-c*e*x])+1/2*d^2*(a+b*ArcSin[c*x])^3*Sqrt[1-c^2*x^2]/(b*c*Sqrt[d+c*d*x]*Sqrt[e-c*e*x])");
  }

  // 5.2.5 Inverse cosine functions.input:148
  public void test0087() {
    check( //
        "Integrate[1/(a+b*ArcCos[-1+d*x^2])^2, x]", //
        "-1/2*x*CosIntegral[1/2*(a+b*ArcCos[-1+d*x^2])/b]*Cos[1/2*a/b]/(b^2*Sqrt[2]*Sqrt[d*x^2])-1/2*x*SinIntegral[1/2*(a+b*ArcCos[-1+d*x^2])/b]*Sin[1/2*a/b]/(b^2*Sqrt[2]*Sqrt[d*x^2])+1/2*Sqrt[2*d*x^2-d^2*x^4]/(b*d*x*(a+b*ArcCos[-1+d*x^2]))");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:694
  public void test0088() {
    check( //
        "Integrate[(d+c*d*x)^(3/2)*(a+b*ArcSin[c*x])^2/(e-c*e*x)^(3/2), x]", //
        "-2*a*b*d^3*x*(1-c^2*x^2)^(3/2)/((d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-2*b^2*d^3*(1-c^2*x^2)^2/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-2*b^2*d^3*x*(1-c^2*x^2)^(3/2)*ArcSin[c*x]/((d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+4*d^3*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+4*d^3*x*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2/((d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-4*I*d^3*(1-c^2*x^2)^(3/2)*(a+b*ArcSin[c*x])^2/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+d^3*(1-c^2*x^2)^2*(a+b*ArcSin[c*x])^2/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-d^3*(1-c^2*x^2)^(3/2)*(a+b*ArcSin[c*x])^3/(b*c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+16*I*b*d^3*(1-c^2*x^2)^(3/2)*(a+b*ArcSin[c*x])*ArcTan[E^(I*ArcSin[c*x])]/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+8*b*d^3*(1-c^2*x^2)^(3/2)*(a+b*ArcSin[c*x])*Log[1+E^(2*I*ArcSin[c*x])]/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-8*I*b^2*d^3*(1-c^2*x^2)^(3/2)*PolyLog[2,-I*E^(I*ArcSin[c*x])]/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))+8*I*b^2*d^3*(1-c^2*x^2)^(3/2)*PolyLog[2,I*E^(I*ArcSin[c*x])]/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))-4*I*b^2*d^3*(1-c^2*x^2)^(3/2)*PolyLog[2,-E^(2*I*ArcSin[c*x])]/(c*(d+c*d*x)^(3/2)*(e-c*e*x)^(3/2))");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:384
  public void test0089() {
    check( //
        "Integrate[(c+a^2*c*x^2)^(3/2)*ArcTan[a*x]^2/x^3, x]", //
        "-a^2*c^(3/2)*ArcTanh[Sqrt[c+a^2*c*x^2]/Sqrt[c]]+4*I*a^2*c^2*ArcTan[a*x]*ArcTan[Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]-3*a^2*c^2*ArcTan[a*x]^2*ArcTanh[E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]+3*I*a^2*c^2*ArcTan[a*x]*PolyLog[2,-E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]-3*I*a^2*c^2*ArcTan[a*x]*PolyLog[2,E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]-2*I*a^2*c^2*PolyLog[2,-I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]+2*I*a^2*c^2*PolyLog[2,I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]-3*a^2*c^2*PolyLog[3,-E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]+3*a^2*c^2*PolyLog[3,E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/Sqrt[c+a^2*c*x^2]-a*c*ArcTan[a*x]*Sqrt[c+a^2*c*x^2]/x+a^2*c*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]-1/2*c*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]/x^2");
  }

  // 5.4.1 Inverse cotangent functions.input:133
  public void test0090() {
    check( //
        "Integrate[ArcCot[a*x^5]/x, x]", //
        "-1/10*I*PolyLog[2,(-I)/(a*x^5)]+1/10*I*PolyLog[2,I/(a*x^5)]");
  }

  // 5.1.5 Inverse sine functions.input:436
  public void test0091() {
    check( //
        "Integrate[(1-a^2-2*a*b*x-b^2*x^2)^(3/2)/ArcSin[a+b*x], x]", //
        "1/2*CosIntegral[2*ArcSin[a+b*x]]/b+1/8*CosIntegral[4*ArcSin[a+b*x]]/b+3/8*Log[ArcSin[a+b*x]]/b");
  }

  // 5.5.2 Inverse secant functions.input:43
  public void test0092() {
    check( //
        "Integrate[ArcSec[a+b*x]/x^2, x]", //
        "-b*ArcSec[a+b*x]/a-ArcSec[a+b*x]/x+2*b*ArcTan[Sqrt[1+a]*Tan[1/2*ArcSec[a+b*x]]/Sqrt[1-a]]/(a*Sqrt[1-a^2])");
  }

  // 5.3.7 Inverse tangent functions.input:81
  public void test0093() {
    check( //
        "Integrate[ArcTan[c+(1+I*c)*Tan[a+b*x]], x]", //
        "-1/2*b*x^2+x*ArcTan[c+(1+I*c)*Tan[a+b*x]]-1/2*I*x*Log[1-I*E^(2*I*a+2*I*b*x)*c]-1/4*PolyLog[2,I*E^(2*I*a+2*I*b*x)*c]/b");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:628
  public void test0094() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])*Sqrt[f-c*f*x]/(d+c*d*x)^(1/2), x]", //
        "f*(1-c^2*x^2)*(a+b*ArcSin[c*x])/(c*Sqrt[d+c*d*x]*Sqrt[f-c*f*x])-b*f*x*Sqrt[1-c^2*x^2]/(Sqrt[d+c*d*x]*Sqrt[f-c*f*x])+1/2*f*(a+b*ArcSin[c*x])^2*Sqrt[1-c^2*x^2]/(b*c*Sqrt[d+c*d*x]*Sqrt[f-c*f*x])");
  }

  // 5.2.5 Inverse cosine functions.input:16
  public void test0095() {
    check( //
        "Integrate[(f+g*x)*(a+b*ArcCos[c*x])*Sqrt[d-c^2*d*x^2], x]", //
        "1/2*f*x*(a+b*ArcCos[c*x])*Sqrt[d-c^2*d*x^2]-1/3*g*(1-c^2*x^2)*(a+b*ArcCos[c*x])*Sqrt[d-c^2*d*x^2]/c^2-1/3*b*g*x*Sqrt[d-c^2*d*x^2]/(c*Sqrt[1-c^2*x^2])+1/4*b*c*f*x^2*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]+1/9*b*c*g*x^3*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]-1/4*f*(a+b*ArcCos[c*x])^2*Sqrt[d-c^2*d*x^2]/(b*c*Sqrt[1-c^2*x^2])");
  }

  // 5.4.1 Inverse cotangent functions.input:142
  public void test0096() {
    check( //
        "Integrate[x*ArcCot[a+b*x], x]", //
        "1/2*x/b+1/2*x^2*ArcCot[a+b*x]-1/2*(1-a^2)*ArcTan[a+b*x]/b^2-1/2*a*Log[1+(a+b*x)^2]/b^2");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:97
  public void test0097() {
    check( //
        "Integrate[(d+I*c*d*x)^2*(a+b*ArcTan[c*x])^2/x, x]", //
        "a*b*c*d^2*x+b^2*c*d^2*x*ArcTan[c*x]-5/2*d^2*(a+b*ArcTan[c*x])^2+2*I*c*d^2*x*(a+b*ArcTan[c*x])^2-1/2*c^2*d^2*x^2*(a+b*ArcTan[c*x])^2+2*d^2*(a+b*ArcTan[c*x])^2*ArcTanh[1+(-2)/(1+I*c*x)]+4*I*b*d^2*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]-1/2*b^2*d^2*Log[1+c^2*x^2]-2*b^2*d^2*PolyLog[2,1+(-2)/(1+I*c*x)]-I*b*d^2*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]+I*b*d^2*(a+b*ArcTan[c*x])*PolyLog[2,-1+2/(1+I*c*x)]-1/2*b^2*d^2*PolyLog[3,1+(-2)/(1+I*c*x)]+1/2*b^2*d^2*PolyLog[3,-1+2/(1+I*c*x)]");
  }

  // 5.2.2 (d x)^m (a+b arccos(c x))^n.input:43
  public void test0098() {
    check( //
        "Integrate[x^5*ArcCos[a*x]^4, x]", //
        "245/1152*x^2/a^4+65/3456*x^4/a^2+1/324*x^6+245/1152*ArcCos[a*x]^2/a^6-5/16*x^2*ArcCos[a*x]^2/a^4-5/48*x^4*ArcCos[a*x]^2/a^2-1/18*x^6*ArcCos[a*x]^2-5/96*ArcCos[a*x]^4/a^6+1/6*x^6*ArcCos[a*x]^4+245/576*x*ArcCos[a*x]*Sqrt[1-a^2*x^2]/a^5+65/864*x^3*ArcCos[a*x]*Sqrt[1-a^2*x^2]/a^3+1/54*x^5*ArcCos[a*x]*Sqrt[1-a^2*x^2]/a-5/24*x*ArcCos[a*x]^3*Sqrt[1-a^2*x^2]/a^5-5/36*x^3*ArcCos[a*x]^3*Sqrt[1-a^2*x^2]/a^3-1/9*x^5*ArcCos[a*x]^3*Sqrt[1-a^2*x^2]/a");
  }

  // 5.2.5 Inverse cosine functions.input:75
  public void test0099() {
    check( //
        "Integrate[ArcCos[a+b*x]^(3/2), x]", //
        "(a+b*x)*ArcCos[a+b*x]^(3/2)/b+3/2*FresnelS[Sqrt[2/Pi]*Sqrt[ArcCos[a+b*x]]]*Sqrt[1/2*Pi]/b-3/2*Sqrt[1-(a+b*x)^2]*Sqrt[ArcCos[a+b*x]]/b");
  }

  // 5.1.5 Inverse sine functions.input:529
  public void test0100() {
    check( //
        "Integrate[x^(-1+n)*ArcSin[a+b*x^n], x]", //
        "(a+b*x^n)*ArcSin[a+b*x^n]/(b*n)+Sqrt[1-(a+b*x^n)^2]/(b*n)");
  }

  // 5.1.5 Inverse sine functions.input:143
  public void test0101() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])^2*Log[h*(f+g*x)^m]/Sqrt[1-c^2*x^2], x]", //
        "1/12*I*m*(a+b*ArcSin[c*x])^4/(b^2*c)+1/3*(a+b*ArcSin[c*x])^3*Log[h*(f+g*x)^m]/(b*c)-1/3*m*(a+b*ArcSin[c*x])^3*Log[1-I*E^(I*ArcSin[c*x])*g/(c*f-Sqrt[c^2*f^2-g^2])]/(b*c)-1/3*m*(a+b*ArcSin[c*x])^3*Log[1-I*E^(I*ArcSin[c*x])*g/(c*f+Sqrt[c^2*f^2-g^2])]/(b*c)+I*m*(a+b*ArcSin[c*x])^2*PolyLog[2,I*E^(I*ArcSin[c*x])*g/(c*f-Sqrt[c^2*f^2-g^2])]/c+I*m*(a+b*ArcSin[c*x])^2*PolyLog[2,I*E^(I*ArcSin[c*x])*g/(c*f+Sqrt[c^2*f^2-g^2])]/c-2*b*m*(a+b*ArcSin[c*x])*PolyLog[3,I*E^(I*ArcSin[c*x])*g/(c*f-Sqrt[c^2*f^2-g^2])]/c-2*b*m*(a+b*ArcSin[c*x])*PolyLog[3,I*E^(I*ArcSin[c*x])*g/(c*f+Sqrt[c^2*f^2-g^2])]/c-2*I*b^2*m*PolyLog[4,I*E^(I*ArcSin[c*x])*g/(c*f-Sqrt[c^2*f^2-g^2])]/c-2*I*b^2*m*PolyLog[4,I*E^(I*ArcSin[c*x])*g/(c*f+Sqrt[c^2*f^2-g^2])]/c");
  }

  // 5.6.1 u (a+b arccsc(c x))^n.input:31
  public void test0102() {
    check( //
        "Integrate[(a+b*ArcCsc[c*x])^2/x^2, x]", //
        "2*b^2/x-(a+b*ArcCsc[c*x])^2/x-2*b*c*(a+b*ArcCsc[c*x])*Sqrt[1+(-1)/(c^2*x^2)]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:320
  public void test0103() {
    check( //
        "Integrate[(c+a^2*c*x^2)*ArcTan[a*x]^2/x^2, x]", //
        "-c*ArcTan[a*x]^2/x+a^2*c*x*ArcTan[a*x]^2+2*a*c*ArcTan[a*x]*Log[2/(1+I*a*x)]+2*a*c*ArcTan[a*x]*Log[2+(-2)/(1-I*a*x)]-I*a*c*PolyLog[2,-1+2/(1-I*a*x)]+I*a*c*PolyLog[2,1+(-2)/(1+I*a*x)]");
  }

  // 5.6.1 u (a+b arccsc(c x))^n.input:15
  public void test0104() {
    check( //
        "Integrate[x^3*(a+b*ArcCsc[c*x]), x]", //
        "1/4*x^4*(a+b*ArcCsc[c*x])+1/6*b*x*Sqrt[1+(-1)/(c^2*x^2)]/c^3+1/12*b*x^3*Sqrt[1+(-1)/(c^2*x^2)]/c");
  }

  // 5.3.7 Inverse tangent functions.input:126
  public void test0105() {
    check( //
        "Integrate[x^2*ArcTan[c-(I-c)*Tanh[a+b*x]], x]", //
        "1/12*I*b*x^4+1/3*x^3*ArcTan[c-(I-c)*Tanh[a+b*x]]-1/6*I*x^3*Log[1-I*E^(2*a+2*b*x)*c]-1/4*I*x^2*PolyLog[2,I*E^(2*a+2*b*x)*c]/b+1/4*I*x*PolyLog[3,I*E^(2*a+2*b*x)*c]/b^2-1/8*I*PolyLog[4,I*E^(2*a+2*b*x)*c]/b^3");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1347
  public void test0106() {
    check( //
        "Integrate[x*(d+e*x^2)*(a+b*ArcTan[c*x]), x]", //
        "-1/4*b*(2*c^2*d-e)*x/c^3-1/12*b*e*x^3/c-1/4*b*(c^2*d-e)^2*ArcTan[c*x]/(c^4*e)+1/4*(d+e*x^2)^2*(a+b*ArcTan[c*x])/e");
  }

  // 5.3.5 u (a+b arctan(c+d x))^p.input:46
  public void test0107() {
    check( //
        "Integrate[(a+b*ArcTan[c+d*x])^2, x]", //
        "I*(a+b*ArcTan[c+d*x])^2/d+(c+d*x)*(a+b*ArcTan[c+d*x])^2/d+2*b*(a+b*ArcTan[c+d*x])*Log[2/(1+I*(c+d*x))]/d+I*b^2*PolyLog[2,1+(-2)/(1+I*(c+d*x))]/d");
  }

  // 5.2.4 (f x)^m (d+e x^2)^p (a+b arccos(c x))^n.input:24
  public void test0108() {
    check( //
        "Integrate[(a+b*ArcCos[c*x])/(x*(d-c^2*d*x^2)^2), x]", //
        "1/2*(a+b*ArcCos[c*x])/(d^2*(1-c^2*x^2))+2*(a+b*ArcCos[c*x])*ArcTanh[E^(2*I*ArcCos[c*x])]/d^2-1/2*I*b*PolyLog[2,-E^(2*I*ArcCos[c*x])]/d^2+1/2*I*b*PolyLog[2,E^(2*I*ArcCos[c*x])]/d^2+1/2*b*c*x/(d^2*Sqrt[1-c^2*x^2])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1358
  public void test0109() {
    check( //
        "Integrate[x*(d+e*x^2)^2*(a+b*ArcTan[c*x]), x]", //
        "-1/6*b*(3*c^4*d^2-3*c^2*d*e+e^2)*x/c^5-1/18*b*(3*c^2*d-e)*e*x^3/c^3-1/30*b*e^2*x^5/c-1/6*b*(c^2*d-e)^3*ArcTan[c*x]/(c^6*e)+1/6*(d+e*x^2)^3*(a+b*ArcTan[c*x])/e");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:104
  public void test0110() {
    check( //
        "Integrate[x^2*(a+b*ArcTan[c*x^2]), x]", //
        "-2/3*b*x/c+1/3*x^3*(a+b*ArcTan[c*x^2])-1/3*b*ArcTan[1-x*Sqrt[2]*Sqrt[c]]/(c^(3/2)*Sqrt[2])+1/3*b*ArcTan[1+x*Sqrt[2]*Sqrt[c]]/(c^(3/2)*Sqrt[2])-1/6*b*Log[1+c*x^2-x*Sqrt[2]*Sqrt[c]]/(c^(3/2)*Sqrt[2])+1/6*b*Log[1+c*x^2+x*Sqrt[2]*Sqrt[c]]/(c^(3/2)*Sqrt[2])");
  }

  // 5.4.1 Inverse cotangent functions.input:143
  public void test0111() {
    check( //
        "Integrate[ArcCot[a+b*x], x]", //
        "(a+b*x)*ArcCot[a+b*x]/b+1/2*Log[1+(a+b*x)^2]/b");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:161
  public void test0112() {
    check( //
        "Integrate[x^8*(a+b*ArcTan[c*x^3])^2, x]", //
        "1/9*b^2*x^3/c^2-1/9*b^2*ArcTan[c*x^3]/c^3-1/9*b*x^6*(a+b*ArcTan[c*x^3])/c-1/9*I*(a+b*ArcTan[c*x^3])^2/c^3+1/9*x^9*(a+b*ArcTan[c*x^3])^2-2/9*b*(a+b*ArcTan[c*x^3])*Log[2/(1+I*c*x^3)]/c^3-1/9*I*b^2*PolyLog[2,1+(-2)/(1+I*c*x^3)]/c^3");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:948
  public void test0113() {
    check( //
        "Integrate[x^2*ArcTan[a*x]^(3/2)/(c+a^2*c*x^2)^3, x]", //
        "1/20*ArcTan[a*x]^(5/2)/(a^3*c^3)-1/32*ArcTan[a*x]^(3/2)*Sin[4*ArcTan[a*x]]/(a^3*c^3)+3/512*FresnelC[2*Sqrt[2/Pi]*Sqrt[ArcTan[a*x]]]*Sqrt[1/2*Pi]/(a^3*c^3)-3/256*Cos[4*ArcTan[a*x]]*Sqrt[ArcTan[a*x]]/(a^3*c^3)");
  }

  // 5.2.5 Inverse cosine functions.input:130
  public void test0114() {
    check( //
        "Integrate[x^3*ArcCos[a+b*x^4], x]", //
        "1/4*(a+b*x^4)*ArcCos[a+b*x^4]/b-1/4*Sqrt[1-(a+b*x^4)^2]/b");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:196
  public void test0115() {
    check( //
        "Integrate[(d-c^2*d*x^2)*(a+b*ArcSin[c*x])^2/x^3, x]", //
        "-1/2*c^2*d*(a+b*ArcSin[c*x])^2-1/2*d*(1-c^2*x^2)*(a+b*ArcSin[c*x])^2/x^2+1/3*I*c^2*d*(a+b*ArcSin[c*x])^3/b-c^2*d*(a+b*ArcSin[c*x])^2*Log[1-E^(2*I*ArcSin[c*x])]+b^2*c^2*d*Log[x]+I*b*c^2*d*(a+b*ArcSin[c*x])*PolyLog[2,E^(2*I*ArcSin[c*x])]-1/2*b^2*c^2*d*PolyLog[3,E^(2*I*ArcSin[c*x])]-b*c*d*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/x");
  }

  // 5.2.5 Inverse cosine functions.input:76
  public void test0116() {
    check( //
        "Integrate[ArcCos[a+b*x]^(1/2), x]", //
        "-FresnelC[Sqrt[2/Pi]*Sqrt[ArcCos[a+b*x]]]*Sqrt[1/2*Pi]/b+(a+b*x)*Sqrt[ArcCos[a+b*x]]/b");
  }

  // 5.1.2 (d x)^m (a+b arcsin(c x))^n.input:45
  public void test0117() {
    check( //
        "Integrate[x^3*ArcSin[a*x]^4, x]", //
        "45/128*x^2/a^2+3/128*x^4+45/128*ArcSin[a*x]^2/a^4-9/16*x^2*ArcSin[a*x]^2/a^2-3/16*x^4*ArcSin[a*x]^2-3/32*ArcSin[a*x]^4/a^4+1/4*x^4*ArcSin[a*x]^4-45/64*x*ArcSin[a*x]*Sqrt[1-a^2*x^2]/a^3-3/32*x^3*ArcSin[a*x]*Sqrt[1-a^2*x^2]/a+3/8*x*ArcSin[a*x]^3*Sqrt[1-a^2*x^2]/a^3+1/4*x^3*ArcSin[a*x]^3*Sqrt[1-a^2*x^2]/a");
  }

  // 5.3.7 Inverse tangent functions.input:167
  public void test0118() {
    check( //
        "Integrate[ArcTan[x]/(-1+x)^3, x]", //
        "1/4/(1-x)-1/2*ArcTan[x]/(1-x)^2-1/4*Log[1-x]+1/8*Log[1+x^2]");
  }

  // 5.3.7 Inverse tangent functions.input:134
  public void test0119() {
    check( //
        "Integrate[(e+f*x)*ArcTan[Coth[a+b*x]], x]", //
        "1/2*(e+f*x)^2*ArcTan[E^(2*a+2*b*x)]/f+1/2*(e+f*x)^2*ArcTan[Coth[a+b*x]]/f-1/4*I*(e+f*x)*PolyLog[2,-I*E^(2*a+2*b*x)]/b+1/4*I*(e+f*x)*PolyLog[2,I*E^(2*a+2*b*x)]/b+1/8*I*f*PolyLog[3,-I*E^(2*a+2*b*x)]/b^2-1/8*I*f*PolyLog[3,I*E^(2*a+2*b*x)]/b^2");
  }

  // 5.3.6 Exponentials of inverse tangent.input:303
  public void test0120() {
    check( //
        "Integrate[E^ArcTan[a*x]/(c+a^2*c*x^2)^3, x]", //
        "24/85*E^ArcTan[a*x]/(a*c^3)+1/17*E^ArcTan[a*x]*(1+4*a*x)/(a*c^3*(1+a^2*x^2)^2)+12/85*E^ArcTan[a*x]*(1+2*a*x)/(a*c^3*(1+a^2*x^2))");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1445
  public void test0121() {
    check( //
        "Integrate[(a+b*ArcTan[c*x])/(x^2*(d+e*x^2)^(1/2)), x]", //
        "-b*c*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]/Sqrt[d]+b*ArcTanh[c*Sqrt[d+e*x^2]/Sqrt[c^2*d-e]]*Sqrt[c^2*d-e]/d-(a+b*ArcTan[c*x])*Sqrt[d+e*x^2]/(d*x)");
  }

  // 5.5.2 Inverse secant functions.input:45
  public void test0122() {
    check( //
        "Integrate[ArcSec[a+b*x]/x^4, x]", //
        "-1/3*b^3*ArcSec[a+b*x]/a^3-1/3*ArcSec[a+b*x]/x^3+1/3*(2-5*a^2+6*a^4)*b^3*ArcTan[Sqrt[1+a]*Tan[1/2*ArcSec[a+b*x]]/Sqrt[1-a]]/(a^3*(1-a^2)^(5/2))+1/6*b*(a+b*x)*Sqrt[1+(-1)/(a+b*x)^2]/(a*(1-a^2)*x^2)-1/6*(2-5*a^2)*b^2*(a+b*x)*Sqrt[1+(-1)/(a+b*x)^2]/(a^2*(1-a^2)^2*x)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:476
  public void test0123() {
    check( //
        "Integrate[ArcTan[a*x]^3/(x^2*(c+a^2*c*x^2)^2), x]", //
        "3/8*a/(c^2*(1+a^2*x^2))+3/4*a^2*x*ArcTan[a*x]/(c^2*(1+a^2*x^2))+3/8*a*ArcTan[a*x]^2/c^2-3/4*a*ArcTan[a*x]^2/(c^2*(1+a^2*x^2))-I*a*ArcTan[a*x]^3/c^2-ArcTan[a*x]^3/(c^2*x)-1/2*a^2*x*ArcTan[a*x]^3/(c^2*(1+a^2*x^2))-3/8*a*ArcTan[a*x]^4/c^2+3*a*ArcTan[a*x]^2*Log[2+(-2)/(1-I*a*x)]/c^2-3*I*a*ArcTan[a*x]*PolyLog[2,-1+2/(1-I*a*x)]/c^2+3/2*a*PolyLog[3,-1+2/(1-I*a*x)]/c^2");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:37
  public void test0124() {
    check( //
        "Integrate[x^3*(a+b*ArcTan[c*x])^3, x]", //
        "-1/4*b^3*x/c^3+1/4*b^3*ArcTan[c*x]/c^4+1/4*b^2*x^2*(a+b*ArcTan[c*x])/c^2+I*b*(a+b*ArcTan[c*x])^2/c^4+3/4*b*x*(a+b*ArcTan[c*x])^2/c^3-1/4*b*x^3*(a+b*ArcTan[c*x])^2/c-1/4*(a+b*ArcTan[c*x])^3/c^4+1/4*x^4*(a+b*ArcTan[c*x])^3+2*b^2*(a+b*ArcTan[c*x])*Log[2/(1+I*c*x)]/c^4+I*b^3*PolyLog[2,1+(-2)/(1+I*c*x)]/c^4");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:655
  public void test0125() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])/((d+c*d*x)^(3/2)*(f-c*f*x)^(3/2)), x]", //
        "x*(1-c^2*x^2)*(a+b*ArcSin[c*x])/((d+c*d*x)^(3/2)*(f-c*f*x)^(3/2))+1/2*b*(1-c^2*x^2)^(3/2)*Log[1-c^2*x^2]/(c*(d+c*d*x)^(3/2)*(f-c*f*x)^(3/2))");
  }

  // 5.1.5 Inverse sine functions.input:439
  public void test0126() {
    check( //
        "Integrate[(1-a^2-2*a*b*x-b^2*x^2)^(3/2)/ArcSin[a+b*x]^4, x]", //
        "-1/3*(1-(a+b*x)^2)^2/(b*ArcSin[a+b*x]^3)+2/3*(a+b*x)*(1-(a+b*x)^2)^(3/2)/(b*ArcSin[a+b*x]^2)+2/3*(1-(a+b*x)^2)/(b*ArcSin[a+b*x])-8/3*(a+b*x)^2*(1-(a+b*x)^2)/(b*ArcSin[a+b*x])+2/3*SinIntegral[2*ArcSin[a+b*x]]/b+4/3*SinIntegral[4*ArcSin[a+b*x]]/b");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:48
  public void test0127() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])/(x^3*(d-c^2*d*x^2)), x]", //
        "1/2*(-a-b*ArcSin[c*x])/(d*x^2)-2*c^2*(a+b*ArcSin[c*x])*ArcTanh[E^(2*I*ArcSin[c*x])]/d+1/2*I*b*c^2*PolyLog[2,-E^(2*I*ArcSin[c*x])]/d-1/2*I*b*c^2*PolyLog[2,E^(2*I*ArcSin[c*x])]/d-1/2*b*c*Sqrt[1-c^2*x^2]/(d*x)");
  }

  // 5.4.1 Inverse cotangent functions.input:108
  public void test0128() {
    check( //
        "Integrate[ArcCot[a*x^2]/x, x]", //
        "-1/4*I*PolyLog[2,(-I)/(a*x^2)]+1/4*I*PolyLog[2,I/(a*x^2)]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:16
  public void test0129() {
    check( //
        "Integrate[(d+I*c*d*x)*(a+b*ArcTan[c*x])/x, x]", //
        "I*a*c*d*x+I*b*c*d*x*ArcTan[c*x]+a*d*Log[x]-1/2*I*b*d*Log[1+c^2*x^2]+1/2*I*b*d*PolyLog[2,-I*c*x]-1/2*I*b*d*PolyLog[2,I*c*x]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:608
  public void test0130() {
    check( //
        "Integrate[1/((c+a^2*c*x^2)^(3/2)*ArcTan[a*x]), x]", //
        "CosIntegral[ArcTan[a*x]]*Sqrt[1+a^2*x^2]/(a*c*Sqrt[c+a^2*c*x^2])");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:125
  public void test0131() {
    check( //
        "Integrate[ArcSin[a*x]/(x*Sqrt[1-a^2*x^2]), x]", //
        "-2*ArcSin[a*x]*ArcTanh[E^(I*ArcSin[a*x])]+I*PolyLog[2,-E^(I*ArcSin[a*x])]-I*PolyLog[2,E^(I*ArcSin[a*x])]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:408
  public void test0132() {
    check( //
        "Integrate[ArcTan[a*x]^2/(x*(c+a^2*c*x^2)^(3/2)), x]", //
        "(-2)/(c*Sqrt[c+a^2*c*x^2])-2*a*x*ArcTan[a*x]/(c*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]^2/(c*Sqrt[c+a^2*c*x^2])-2*ArcTan[a*x]^2*ArcTanh[E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])+2*I*ArcTan[a*x]*PolyLog[2,-E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])-2*I*ArcTan[a*x]*PolyLog[2,E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])-2*PolyLog[3,-E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])+2*PolyLog[3,E^(I*ArcTan[a*x])]*Sqrt[1+a^2*x^2]/(c*Sqrt[c+a^2*c*x^2])");
  }

  // 5.6.2 Inverse cosecant functions.input:41
  public void test0133() {
    check( //
        "Integrate[x*ArcCsc[a+b*x], x]", //
        "-1/2*a^2*ArcCsc[a+b*x]/b^2+1/2*x^2*ArcCsc[a+b*x]-a*ArcTanh[Sqrt[1+(-1)/(a+b*x)^2]]/b^2+1/2*(a+b*x)*Sqrt[1+(-1)/(a+b*x)^2]/b^2");
  }

  // 5.3.6 Exponentials of inverse tangent.input:511
  public void test0134() {
    check( //
        "Integrate[E^(3*I*ArcTan[a*x])*x^2/(c+a^2*c*x^2)^(11/2), x]", //
        "-1/24*(I+3*a*x)*Sqrt[1+a^2*x^2]/(a^3*c^5*(1-I*a*x)^6*(1+I*a*x)^3*Sqrt[c+a^2*c*x^2])");
  }

  // 5.2.5 Inverse cosine functions.input:136
  public void test0135() {
    check( //
        "Integrate[(a+b*ArcCos[1+d*x^2])^4, x]", //
        "384*b^4*x-48*b^2*x*(a+b*ArcCos[1+d*x^2])^2+x*(a+b*ArcCos[1+d*x^2])^4+192*b^3*(a+b*ArcCos[1+d*x^2])*Sqrt[-2*d*x^2-d^2*x^4]/(d*x)-8*b*(a+b*ArcCos[1+d*x^2])^3*Sqrt[-2*d*x^2-d^2*x^4]/(d*x)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:481
  public void test0136() {
    check( //
        "Integrate[x*ArcTan[a*x]^3/(c+a^2*c*x^2)^3, x]", //
        "-3/128*x/(a*c^3*(1+a^2*x^2)^2)-45/256*x/(a*c^3*(1+a^2*x^2))-45/256*ArcTan[a*x]/(a^2*c^3)+3/32*ArcTan[a*x]/(a^2*c^3*(1+a^2*x^2)^2)+9/32*ArcTan[a*x]/(a^2*c^3*(1+a^2*x^2))+3/16*x*ArcTan[a*x]^2/(a*c^3*(1+a^2*x^2)^2)+9/32*x*ArcTan[a*x]^2/(a*c^3*(1+a^2*x^2))+3/32*ArcTan[a*x]^3/(a^2*c^3)-1/4*ArcTan[a*x]^3/(a^2*c^3*(1+a^2*x^2)^2)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:1502
  public void test0137() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*ArcTan[c*x])^2/x^3, x]", //
        "-b*c*d*(a+b*ArcTan[c*x])/x-1/2*c^2*d*(a+b*ArcTan[c*x])^2-1/2*d*(a+b*ArcTan[c*x])^2/x^2+2*e*(a+b*ArcTan[c*x])^2*ArcTanh[1+(-2)/(1+I*c*x)]+b^2*c^2*d*Log[x]-1/2*b^2*c^2*d*Log[1+c^2*x^2]-I*b*e*(a+b*ArcTan[c*x])*PolyLog[2,1+(-2)/(1+I*c*x)]+I*b*e*(a+b*ArcTan[c*x])*PolyLog[2,-1+2/(1+I*c*x)]-1/2*b^2*e*PolyLog[3,1+(-2)/(1+I*c*x)]+1/2*b^2*e*PolyLog[3,-1+2/(1+I*c*x)]");
  }

  // 5.4.1 Inverse cotangent functions.input:140
  public void test0138() {
    check( //
        "Integrate[x^3*ArcCot[a+b*x], x]", //
        "-1/4*(1-6*a^2)*x/b^3-1/2*a*(a+b*x)^2/b^4+1/12*(a+b*x)^3/b^4+1/4*x^4*ArcCot[a+b*x]+1/4*(1-6*a^2+a^4)*ArcTan[a+b*x]/b^4+1/2*a*(1-a^2)*Log[1+(a+b*x)^2]/b^4");
  }

  // 5.1.2 (d x)^m (a+b arcsin(c x))^n.input:73
  public void test0139() {
    check( //
        "Integrate[x^4/ArcSin[a*x]^3, x]", //
        "-2*x^3/(a^2*ArcSin[a*x])+5/2*x^5/ArcSin[a*x]-1/16*CosIntegral[ArcSin[a*x]]/a^5+27/32*CosIntegral[3*ArcSin[a*x]]/a^5-25/32*CosIntegral[5*ArcSin[a*x]]/a^5-1/2*x^4*Sqrt[1-a^2*x^2]/(a*ArcSin[a*x]^2)");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:649
  public void test0140() {
    check( //
        "Integrate[(a+b*ArcSin[c*x])/((d+c*d*x)^(3/2)*Sqrt[f-c*f*x]), x]", //
        "-f*(1-c*x)*(1-c^2*x^2)*(a+b*ArcSin[c*x])/(c*(d+c*d*x)^(3/2)*(f-c*f*x)^(3/2))+b*f*(1-c^2*x^2)^(3/2)*Log[1+c*x]/(c*(d+c*d*x)^(3/2)*(f-c*f*x)^(3/2))");
  }

  // 5.4.1 Inverse cotangent functions.input:252
  public void test0141() {
    check( //
        "Integrate[ArcCot[c+(1-I*c)*Cot[a+b*x]], x]", //
        "-1/2*b*x^2+x*ArcCot[c+(1-I*c)*Cot[a+b*x]]-1/2*I*x*Log[1-I*E^(2*I*a+2*I*b*x)*c]-1/4*PolyLog[2,I*E^(2*I*a+2*I*b*x)*c]/b");
  }

  // 5.4.1 Inverse cotangent functions.input:98
  public void test0142() {
    check( //
        "Integrate[x*ArcCot[x]/(1+x^2)^3, x]", //
        "-1/16*x/(1+x^2)^2-3/32*x/(1+x^2)-1/4*ArcCot[x]/(1+x^2)^2-3/32*ArcTan[x]");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:646
  public void test0143() {
    check( //
        "Integrate[(d+c*d*x)^(3/2)*(a+b*ArcSin[c*x])/Sqrt[f-c*f*x], x]", //
        "-2*d^2*(1-c^2*x^2)*(a+b*ArcSin[c*x])/(c*Sqrt[d+c*d*x]*Sqrt[f-c*f*x])-1/2*d^2*x*(1-c^2*x^2)*(a+b*ArcSin[c*x])/(Sqrt[d+c*d*x]*Sqrt[f-c*f*x])+2*b*d^2*x*Sqrt[1-c^2*x^2]/(Sqrt[d+c*d*x]*Sqrt[f-c*f*x])+1/4*b*c*d^2*x^2*Sqrt[1-c^2*x^2]/(Sqrt[d+c*d*x]*Sqrt[f-c*f*x])+3/4*d^2*(a+b*ArcSin[c*x])^2*Sqrt[1-c^2*x^2]/(b*c*Sqrt[d+c*d*x]*Sqrt[f-c*f*x])");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:348
  public void test0144() {
    check( //
        "Integrate[ArcTan[a*x]^2/(x^3*(c+a^2*c*x^2)), x]", //
        "-a*ArcTan[a*x]/(c*x)-1/2*a^2*ArcTan[a*x]^2/c-1/2*ArcTan[a*x]^2/(c*x^2)+1/3*I*a^2*ArcTan[a*x]^3/c+a^2*Log[x]/c-1/2*a^2*Log[1+a^2*x^2]/c-a^2*ArcTan[a*x]^2*Log[2+(-2)/(1-I*a*x)]/c+I*a^2*ArcTan[a*x]*PolyLog[2,-1+2/(1-I*a*x)]/c-1/2*a^2*PolyLog[3,-1+2/(1-I*a*x)]/c");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:414
  public void test0145() {
    check( //
        "Integrate[x^3/((a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]), x]", //
        "3/4*Cos[a/b]*SinIntegral[(a+b*ArcSin[c*x])/b]/(b*c^4)-1/4*Cos[3*a/b]*SinIntegral[3*(a+b*ArcSin[c*x])/b]/(b*c^4)-3/4*CosIntegral[(a+b*ArcSin[c*x])/b]*Sin[a/b]/(b*c^4)+1/4*CosIntegral[3*(a+b*ArcSin[c*x])/b]*Sin[3*a/b]/(b*c^4)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:536
  public void test0146() {
    check( //
        "Integrate[ArcTan[a*x]^3/(c+a^2*c*x^2)^(5/2), x]", //
        "(-2/27)/(a*c*(c+a^2*c*x^2)^(3/2))-2/9*x*ArcTan[a*x]/(c*(c+a^2*c*x^2)^(3/2))+1/3*ArcTan[a*x]^2/(a*c*(c+a^2*c*x^2)^(3/2))+1/3*x*ArcTan[a*x]^3/(c*(c+a^2*c*x^2)^(3/2))+(-40/9)/(a*c^2*Sqrt[c+a^2*c*x^2])-40/9*x*ArcTan[a*x]/(c^2*Sqrt[c+a^2*c*x^2])+2*ArcTan[a*x]^2/(a*c^2*Sqrt[c+a^2*c*x^2])+2/3*x*ArcTan[a*x]^3/(c^2*Sqrt[c+a^2*c*x^2])");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:82
  public void test0147() {
    check( //
        "Integrate[(d-c^2*d*x^2)^(1/2)*(a+b*ArcSin[c*x])/x, x]", //
        "(a+b*ArcSin[c*x])*Sqrt[d-c^2*d*x^2]-b*c*x*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]-2*(a+b*ArcSin[c*x])*ArcTanh[E^(I*ArcSin[c*x])]*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]+I*b*PolyLog[2,-E^(I*ArcSin[c*x])]*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]-I*b*PolyLog[2,E^(I*ArcSin[c*x])]*Sqrt[d-c^2*d*x^2]/Sqrt[1-c^2*x^2]");
  }

  // 5.1.2 (d x)^m (a+b arcsin(c x))^n.input:65
  public void test0148() {
    check( //
        "Integrate[x^5/ArcSin[a*x]^2, x]", //
        "5/16*CosIntegral[2*ArcSin[a*x]]/a^6-1/2*CosIntegral[4*ArcSin[a*x]]/a^6+3/16*CosIntegral[6*ArcSin[a*x]]/a^6-x^5*Sqrt[1-a^2*x^2]/(a*ArcSin[a*x])");
  }

  // 5.1.5 Inverse sine functions.input:182
  public void test0149() {
    check( //
        "Integrate[(g+h*x)*(d+e*x+f*x^2)*(a+b*ArcSin[c*x])^2, x]", //
        "-2*b^2*d*g*x-4/9*b^2*(f*g+e*h)*x/c^2-3/32*b^2*f*h*x^2/c^2-1/4*b^2*(e*g+d*h)*x^2-2/27*b^2*(f*g+e*h)*x^3-1/32*b^2*f*h*x^4-3/32*f*h*(a+b*ArcSin[c*x])^2/c^4-1/4*(e*g+d*h)*(a+b*ArcSin[c*x])^2/c^2+d*g*x*(a+b*ArcSin[c*x])^2+1/2*(e*g+d*h)*x^2*(a+b*ArcSin[c*x])^2+1/3*(f*g+e*h)*x^3*(a+b*ArcSin[c*x])^2+1/4*f*h*x^4*(a+b*ArcSin[c*x])^2+2*b*d*g*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+4/9*b*(f*g+e*h)*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c^3+3/16*b*f*h*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c^3+1/2*b*(e*g+d*h)*x*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+2/9*b*(f*g+e*h)*x^2*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c+1/8*b*f*h*x^3*(a+b*ArcSin[c*x])*Sqrt[1-c^2*x^2]/c");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:396
  public void test0150() {
    check( //
        "Integrate[x^3*ArcTan[a*x]^2/Sqrt[c+a^2*c*x^2], x]", //
        "-10/3*I*ArcTan[a*x]*ArcTan[Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a^4*Sqrt[c+a^2*c*x^2])+5/3*I*PolyLog[2,-I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a^4*Sqrt[c+a^2*c*x^2])-5/3*I*PolyLog[2,I*Sqrt[1+I*a*x]/Sqrt[1-I*a*x]]*Sqrt[1+a^2*x^2]/(a^4*Sqrt[c+a^2*c*x^2])+1/3*Sqrt[c+a^2*c*x^2]/(a^4*c)-1/3*x*ArcTan[a*x]*Sqrt[c+a^2*c*x^2]/(a^3*c)-2/3*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]/(a^4*c)+1/3*x^2*ArcTan[a*x]^2*Sqrt[c+a^2*c*x^2]/(a^2*c)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:281
  public void test0151() {
    check( //
        "Integrate[x^3*ArcTan[a*x]/(c+a^2*c*x^2)^(3/2), x]", //
        "-ArcTanh[a*x*Sqrt[c]/Sqrt[c+a^2*c*x^2]]/(a^4*c^(3/2))-x/(a^3*c*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]/(a^4*c*Sqrt[c+a^2*c*x^2])+ArcTan[a*x]*Sqrt[c+a^2*c*x^2]/(a^4*c^2)");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:160
  public void test0152() {
    check( //
        "Integrate[x^11*(a+b*ArcTan[c*x^3])^2, x]", //
        "1/6*a*b*x^3/c^3+1/36*b^2*x^6/c^2+1/6*b^2*x^3*ArcTan[c*x^3]/c^3-1/18*b*x^9*(a+b*ArcTan[c*x^3])/c-1/12*(a+b*ArcTan[c*x^3])^2/c^4+1/12*x^12*(a+b*ArcTan[c*x^3])^2-1/9*b^2*Log[1+c^2*x^6]/c^4");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:34
  public void test0153() {
    check( //
        "Integrate[(d+I*c*d*x)^3*(a+b*ArcTan[c*x]), x]", //
        "-I*b*d^3*x-1/4*b*d^3*(1+I*c*x)^2/c-1/12*b*d^3*(1+I*c*x)^3/c-1/4*I*d^3*(1+I*c*x)^4*(a+b*ArcTan[c*x])/c-2*b*d^3*Log[1-I*c*x]/c");
  }

  // 5.3.2 (d x)^m (a+b arctan(c x^n))^p.input:177
  public void test0154() {
    check( //
        "Integrate[x^5*(a+b*ArcTan[c*x^3])^3, x]", //
        "-1/2*I*b*(a+b*ArcTan[c*x^3])^2/c^2-1/2*b*x^3*(a+b*ArcTan[c*x^3])^2/c+1/6*(a+b*ArcTan[c*x^3])^3/c^2+1/6*x^6*(a+b*ArcTan[c*x^3])^3-b^2*(a+b*ArcTan[c*x^3])*Log[2/(1+I*c*x^3)]/c^2-1/2*I*b^3*PolyLog[2,1+(-2)/(1+I*c*x^3)]/c^2");
  }

  // 5.4.1 Inverse cotangent functions.input:19
  public void test0155() {
    check( //
        "Integrate[ArcCot[a*x]/x^2, x]", //
        "-ArcCot[a*x]/x-a*Log[x]+1/2*a*Log[1+a^2*x^2]");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:669
  public void test0156() {
    check( //
        "Integrate[1/((c+a^2*c*x^2)^3*ArcTan[a*x]^2), x]", //
        "(-1)/(a*c^3*(1+a^2*x^2)^2*ArcTan[a*x])-SinIntegral[2*ArcTan[a*x]]/(a*c^3)-1/2*SinIntegral[4*ArcTan[a*x]]/(a*c^3)");
  }

  // 5.3.4 u (a+b arctan(c x))^p.input:36
  public void test0157() {
    check( //
        "Integrate[(d+I*c*d*x)^3*(a+b*ArcTan[c*x])/x^2, x]", //
        "-3*a*c^2*d^3*x+1/2*I*b*c^2*d^3*x-1/2*I*b*c*d^3*ArcTan[c*x]-3*b*c^2*d^3*x*ArcTan[c*x]-d^3*(a+b*ArcTan[c*x])/x-1/2*I*c^3*d^3*x^2*(a+b*ArcTan[c*x])+3*I*a*c*d^3*Log[x]+b*c*d^3*Log[x]+b*c*d^3*Log[1+c^2*x^2]-3/2*b*c*d^3*PolyLog[2,-I*c*x]+3/2*b*c*d^3*PolyLog[2,I*c*x]");
  }

  // 5.1.4 (f x)^m (d+e x^2)^p (a+b arcsin(c x))^n.input:348
  public void test0158() {
    check( //
        "Integrate[(c-a^2*c*x^2)^(1/2)*ArcSin[a*x]^3, x]", //
        "-3/4*x*ArcSin[a*x]*Sqrt[c-a^2*c*x^2]+1/2*x*ArcSin[a*x]^3*Sqrt[c-a^2*c*x^2]+3/8*a*x^2*Sqrt[c-a^2*c*x^2]/Sqrt[1-a^2*x^2]+3/8*ArcSin[a*x]^2*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])-3/4*a*x^2*ArcSin[a*x]^2*Sqrt[c-a^2*c*x^2]/Sqrt[1-a^2*x^2]+1/8*ArcSin[a*x]^4*Sqrt[c-a^2*c*x^2]/(a*Sqrt[1-a^2*x^2])");
  }
}
