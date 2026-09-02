package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 4 Trig functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class TrigFunctions3 extends AbstractRubiTestCase {
  static boolean init = true;

  public TrigFunctions3(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("TrigFunctions3");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1690
  public void test0001() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "-1/2*(2*a^2-5*b^2)*x/b^3+2*(a^2-b^2)^(5/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^3*b^3*d)+1/2*(5*a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^3*d)-a*Cos[c+d*x]/(b^2*d)+b*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(b*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1906
  public void test0002() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "3/8*b*ArcTanh[Sin[c+d*x]]/d-3/8*b*Sec[c+d*x]*Tan[c+d*x]/d+1/4*b*Sec[c+d*x]*Tan[c+d*x]^3/d+1/4*a*Tan[c+d*x]^4/d");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:72
  public void test0003() {
    check( //
        "Integrate[Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]/(c+d*Sin[e+f*x]), x]", //
        "-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[g]/(Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]*Sqrt[g]/(d*f)+2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[c]*Sqrt[g]/(Sqrt[c+d]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]*Sqrt[c]*Sqrt[g]/(d*f*Sqrt[c+d])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:41
  public void test0004() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^3, x]", //
        "1/8*a*(5*A-2*B)*c^3*x+1/12*a*(5*A-2*B)*c^3*Cos[e+f*x]^3/f+1/8*a*(5*A-2*B)*c^3*Cos[e+f*x]*Sin[e+f*x]/f-1/5*a*B*c*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^2/f+1/20*a*(5*A-2*B)*Cos[e+f*x]^3*(c^3-c^3*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:58
  public void test0005() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^5, x]", //
        "1/9*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^7)+1/63*a^2*(2*A-7*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+1/315*a^2*(2*A-7*B)*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:74
  public void test0006() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^8, x]", //
        "1/15*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^11)+1/195*a^3*(4*A-11*B)*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^10)+1/715*a^3*(4*A-11*B)*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+2/6435*a^3*(4*A-11*B)*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+2/45045*a^3*(4*A-11*B)*Cos[e+f*x]^7/(c*f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:92
  public void test0007() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^3), x]", //
        "1/5*(A+B)*Sec[e+f*x]^3/(a^2*f*(c^3-c^3*Sin[e+f*x]))+1/5*(4*A-B)*Tan[e+f*x]/(a^2*c^3*f)+1/15*(4*A-B)*Tan[e+f*x]^3/(a^2*c^3*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:112
  public void test0008() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/105*a*(7*A-B)*c^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+2/35*a*(7*A-B)*c^2*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])-2/7*a*B*c*Cos[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:128
  public void test0009() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/9009*a^3*(13*A+B)*c^6*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+16/1287*a^3*(13*A+B)*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))+2/143*a^3*(13*A+B)*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(3/2))-2/13*a^3*B*c^3*Cos[e+f*x]^7/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:146
  public void test0010() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^2, x]", //
        "-512/105*(7*A-13*B)*c^3*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-64/105*(7*A-13*B)*c^2*Sec[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)-16/105*(7*A-13*B)*c*Sec[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a^2*f)-1/21*(7*A-13*B)*Sec[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(a^2*f)-1/3*(A-B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(13/2)/(a^2*c^2*f)+2048/105*(7*A-13*B)*c^4*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:166
  public void test0011() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/4*a*(A+B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])+1/5*a*B*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:182
  public void test0012() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/8*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(9/2))+1/24*(A-3*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(7/2))+1/96*(A-3*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^2*f*(c-c*Sin[e+f*x])^(5/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:198
  public void test0013() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2), x]", //
        "-1/6*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(3/2)/f+1/30*(3*A+B)*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])+1/15*(3*A+B)*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:216
  public void test0014() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "1/4*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+1/4*(A-B)*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/4*(A-B)*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:276
  public void test0015() {
    check( //
        "Integrate[Csc[c+d*x]^2*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "-1/2*a^3*A*x-2*a^3*A*ArcTanh[Cos[c+d*x]]/d+2*a^3*A*Cos[c+d*x]/d-a^3*A*Cot[c+d*x]/d+1/2*a^3*A*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:371
  public void test0016() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-(A-B)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-2*B*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:25
  public void test0017() {
    check( //
        "Integrate[1/Sin[b*x]^(3/2), x]", //
        "2*EllipticE[1/4*Pi-1/2*b*x,2]/b-2*Cos[b*x]/(b*Sqrt[Sin[b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:82
  public void test0018() {
    check( //
        "Integrate[Sec[a+b*x]^6*Sin[a+b*x]^2, x]", //
        "1/3*Tan[a+b*x]^3/b+1/5*Tan[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:98
  public void test0019() {
    check( //
        "Integrate[Sec[a+b*x]*Sin[a+b*x]^3, x]", //
        "1/2*Cos[a+b*x]^2/b-Log[Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:114
  public void test0020() {
    check( //
        "Integrate[Sec[a+b*x]^8*Sin[a+b*x]^4, x]", //
        "1/5*Tan[a+b*x]^5/b+1/7*Tan[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:130
  public void test0021() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[a+b*x]^5, x]", //
        "-1/3*Cos[a+b*x]^3/b+2/5*Cos[a+b*x]^5/b-1/7*Cos[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:164
  public void test0022() {
    check( //
        "Integrate[Cos[a+b*x]^5/Sin[a+b*x]^2, x]", //
        "-Csc[a+b*x]/b-2*Sin[a+b*x]/b+1/3*Sin[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:180
  public void test0023() {
    check( //
        "Integrate[Cos[a+b*x]/Sin[a+b*x]^3, x]", //
        "-1/2*Csc[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:232
  public void test0024() {
    check( //
        "Integrate[Sin[a+b*x]^2/(d*Cos[a+b*x])^(1/2), x]", //
        "4/3*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-2/3*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/(b*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:248
  public void test0025() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*Sin[a+b*x]^4, x]", //
        "-4/15*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/(b*d)-2/9*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]^3/(b*d)+8/15*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:310
  public void test0026() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(1/2), x]", //
        "-ArcTan[1-Sqrt[2]*Sqrt[d]*Sqrt[c*Sin[a+b*x]]/(Sqrt[c]*Sqrt[d*Cos[a+b*x]])]*Sqrt[c]/(b*Sqrt[2]*Sqrt[d])+ArcTan[1+Sqrt[2]*Sqrt[d]*Sqrt[c*Sin[a+b*x]]/(Sqrt[c]*Sqrt[d*Cos[a+b*x]])]*Sqrt[c]/(b*Sqrt[2]*Sqrt[d])+1/2*Log[Sqrt[c]-Sqrt[2]*Sqrt[d]*Sqrt[c*Sin[a+b*x]]/Sqrt[d*Cos[a+b*x]]+Sqrt[c]*Tan[a+b*x]]*Sqrt[c]/(b*Sqrt[2]*Sqrt[d])-1/2*Log[Sqrt[c]+Sqrt[2]*Sqrt[d]*Sqrt[c*Sin[a+b*x]]/Sqrt[d*Cos[a+b*x]]+Sqrt[c]*Tan[a+b*x]]*Sqrt[c]/(b*Sqrt[2]*Sqrt[d])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:326
  public void test0027() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(5/2)/(d*Cos[a+b*x])^(3/2), x]", //
        "2*c*(c*Sin[a+b*x])^(3/2)/(b*d*Sqrt[d*Cos[a+b*x]])-3*c^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d^2*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:344
  public void test0028() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(9/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "2/7*Sqrt[c*Sin[a+b*x]]/(b*c*d*(d*Cos[a+b*x])^(7/2))+4/7*Sqrt[c*Sin[a+b*x]]/(b*c*d^3*(d*Cos[a+b*x])^(3/2))+4/7*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*d^4*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:463
  public void test0029() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^4, x]", //
        "12/5*b^3*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-24/5*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+2*b*Sin[e+f*x]^3*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:479
  public void test0030() {
    check( //
        "Integrate[Csc[e+f*x]^2*(b*Sec[e+f*x])^(5/2), x]", //
        "2/3*b*Csc[e+f*x]*(b*Sec[e+f*x])^(3/2)/f-5/3*b^3*Csc[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])+5/3*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:533
  public void test0031() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(7/2)*Sqrt[b*Sec[e+f*x]], x]", //
        "-1/3*a*b*(a*Sin[e+f*x])^(5/2)/(f*Sqrt[b*Sec[e+f*x]])-5/6*a^3*b*Sqrt[a*Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]])+5/12*a^4*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:15
  public void test0032() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(1/2), x]", //
        "-2*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:49
  public void test0033() {
    check( //
        "Integrate[1/(-5-3*Sin[c+d*x]), x]", //
        "-1/4*x-1/2*ArcTan[Cos[c+d*x]/(3+Sin[c+d*x])]/d");
  }

  // 4.1.1.1 (a+b sin)^n.input:65
  public void test0034() {
    check( //
        "Integrate[1/(-3-5*Sin[c+d*x]), x]", //
        "1/4*Log[3*Cos[1/2*(c+d*x)]+Sin[1/2*(c+d*x)]]/d-1/4*Log[Cos[1/2*(c+d*x)]+3*Sin[1/2*(c+d*x)]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:16
  public void test0035() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "2/3*(a+a*Sin[c+d*x])^3/(a^2*d)-1/4*(a+a*Sin[c+d*x])^4/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:33
  public void test0036() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "1/3*a^4*Cos[c+d*x]/(d*(a-a*Sin[c+d*x])^2)+1/3*a^4*Cos[c+d*x]/(d*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:49
  public void test0037() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^3, x]", //
        "1/5*a^6*Cos[c+d*x]/(d*(a-a*Sin[c+d*x])^3)+2/15*a^5*Cos[c+d*x]/(d*(a-a*Sin[c+d*x])^2)+2/15*a^6*Cos[c+d*x]/(d*(a^3-a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:99
  public void test0038() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "-1/9*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^3)-2/21*Sec[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^2)-2/21*Sec[c+d*x]^3/(d*(a^3+a^3*Sin[c+d*x]))+8/21*Tan[c+d*x]/(a^3*d)+8/63*Tan[c+d*x]^3/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:119
  public void test0039() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-256/3003*a^4*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(7/2))-64/429*a^3*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(5/2))-24/143*a^2*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(3/2))-2/13*a*Cos[c+d*x]^7/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:151
  public void test0040() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+a*Sin[c+d*x])^(5/2), x]", //
        "a*Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d-a^(5/2)*ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:167
  public void test0041() {
    check( //
        "Integrate[Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-1/8*a^2*Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d+1/2*a*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^(5/2)/d-1/8*a^(7/2)*ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:185
  public void test0042() {
    check( //
        "Integrate[Sec[c+d*x]^4/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-35/64*a*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-7/24*a*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-35/64*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2]*Sqrt[a])+35/48*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+1/3*Sec[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:201
  public void test0043() {
    check( //
        "Integrate[Cos[c+d*x]^10/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-64/2145*a^3*Cos[c+d*x]^11/(d*(a+a*Sin[c+d*x])^(11/2))-16/195*a^2*Cos[c+d*x]^11/(d*(a+a*Sin[c+d*x])^(9/2))-2/15*a*Cos[c+d*x]^11/(d*(a+a*Sin[c+d*x])^(7/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:221
  public void test0044() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x]), x]", //
        "-2/5*a*(e*Cos[c+d*x])^(5/2)/(d*e)+2/3*a*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/3*a*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:237
  public void test0045() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])^3, x]", //
        "-34/99*a^3*(e*Cos[c+d*x])^(9/2)/(d*e)+34/77*a^3*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d-2/13*a*(e*Cos[c+d*x])^(9/2)*(a+a*Sin[c+d*x])^2/(d*e)-34/143*(e*Cos[c+d*x])^(9/2)*(a^3+a^3*Sin[c+d*x])/(d*e)+170/231*a^3*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+170/231*a^3*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:253
  public void test0046() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(9/2), x]", //
        "4/7*a^7*(e*Cos[c+d*x])^(5/2)/(d*e^7*(a-a*Sin[c+d*x])^3)+10/21*a^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^4*Sqrt[e*Cos[c+d*x]])-20/21*a^8*Sqrt[e*Cos[c+d*x]]/(d*e^5*(a^4-a^4*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:271
  public void test0047() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x])^2, x]", //
        "-4*e*(e*Cos[c+d*x])^(3/2)/(d*(a^2+a^2*Sin[c+d*x]))-6*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:287
  public void test0048() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^3), x]", //
        "14/39*Sin[c+d*x]/(a^3*d*e*Sqrt[e*Cos[c+d*x]])+(-2/13)/(d*e*(a+a*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]])+(-14/117)/(a*d*e*(a+a*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]])+(-14/117)/(d*e*(a^3+a^3*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]])-14/39*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^3*d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:307
  public void test0049() {
    check( //
        "Integrate[Sqrt[a+a*Sin[c+d*x]]/(e*Cos[c+d*x])^(7/2), x]", //
        "8/3*(a+a*Sin[c+d*x])^(3/2)/(a*d*e*(e*Cos[c+d*x])^(5/2))-16/15*(a+a*Sin[c+d*x])^(5/2)/(a^2*d*e*(e*Cos[c+d*x])^(5/2))-2/3*Sqrt[a+a*Sin[c+d*x]]/(d*e*(e*Cos[c+d*x])^(5/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:341
  public void test0050() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(3/2)), x]", //
        "(-2/7)/(d*e*(a+a*Sin[c+d*x])^(3/2)*Sqrt[e*Cos[c+d*x]])+(-8/21)/(a*d*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]])+16/21*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*e*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:398
  public void test0051() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(-3-m)*(a+a*Sin[c+d*x])^m, x]", //
        "-(e*Cos[c+d*x])^(-2-m)*(a+a*Sin[c+d*x])^m/(d*e*(2-m))+2*(e*Cos[c+d*x])^(-2-m)*(a+a*Sin[c+d*x])^(1+m)/(a*d*e*(2-m)*m)-2*(e*Cos[c+d*x])^(-2-m)*(a+a*Sin[c+d*x])^(2+m)/(a^2*d*e*m*(4-m^2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:430
  public void test0052() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x])^2, x]", //
        "-1/3*a*b*Cos[c+d*x]^6/d+a^2*Sin[c+d*x]/d-1/3*(2*a^2-b^2)*Sin[c+d*x]^3/d+1/5*(a^2-2*b^2)*Sin[c+d*x]^5/d+1/7*b^2*Sin[c+d*x]^7/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:480
  public void test0053() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "2*b^4*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)-1/3*Sec[c+d*x]^3*(b-a*Sin[c+d*x])/((a^2-b^2)*d)+1/3*Sec[c+d*x]*(3*b^3+a*(2*a^2-5*b^2)*Sin[c+d*x])/((a^2-b^2)^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:496
  public void test0054() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Sin[c+d*x])^3, x]", //
        "-Log[a+b*Sin[c+d*x]]/(b^3*d)+1/2*(a^2-b^2)/(b^3*d*(a+b*Sin[c+d*x])^2)-2*a/(b^3*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:602
  public void test0055() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^2, x]", //
        "-26/99*a*b*(e*Cos[c+d*x])^(9/2)/(d*e)+2/77*(11*a^2+2*b^2)*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d-2/11*b*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])/(d*e)+10/231*(11*a^2+2*b^2)*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+10/231*(11*a^2+2*b^2)*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:618
  public void test0056() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3/(e*Cos[c+d*x])^(9/2), x]", //
        "2/7*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/(d*e*(e*Cos[c+d*x])^(7/2))+2/21*(a+b*Sin[c+d*x])*(a*b+(5*a^2-4*b^2)*Sin[c+d*x])/(d*e^3*(e*Cos[c+d*x])^(3/2))+2/21*a*(5*a^2-6*b^2)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^4*Sqrt[e*Cos[c+d*x]])+2/21*b*(5*a^2-4*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e^5)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:13
  public void test0057() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])*Tan[c+d*x]^3, x]", //
        "5/4*a*Log[1-Sin[c+d*x]]/d-1/4*a*Log[1+Sin[c+d*x]]/d+a*Sin[c+d*x]/d+1/2*a^2/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:29
  public void test0058() {
    check( //
        "Integrate[Cot[c+d*x]^7*(a+a*Sin[c+d*x])^2, x]", //
        "-6*a^2*Csc[c+d*x]/d+2*a^2*Csc[c+d*x]^3/d+1/2*a^2*Csc[c+d*x]^4/d-2/5*a^2*Csc[c+d*x]^5/d-1/6*a^2*Csc[c+d*x]^6/d+2*a^2*Log[Sin[c+d*x]]/d-2*a^2*Sin[c+d*x]/d-1/2*a^2*Sin[c+d*x]^2/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:64
  public void test0059() {
    check( //
        "Integrate[Cot[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "-1/6*Cot[c+d*x]^6/(a*d)+Csc[c+d*x]/(a*d)-2/3*Csc[c+d*x]^3/(a*d)+1/5*Csc[c+d*x]^5/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:80
  public void test0060() {
    check( //
        "Integrate[Cot[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*Csc[c+d*x]^2/(a^2*d)+2/3*Csc[c+d*x]^3/(a^2*d)-1/4*Csc[c+d*x]^4/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:96
  public void test0061() {
    check( //
        "Integrate[Tan[c+d*x]^3/(a+a*Sin[c+d*x])^4, x]", //
        "1/20*a/(d*(a+a*Sin[c+d*x])^5)+(-1/8)/(d*(a+a*Sin[c+d*x])^4)+1/16/(a*d*(a+a*Sin[c+d*x])^3)+1/32/(d*(a^2+a^2*Sin[c+d*x])^2)+1/64/(d*(a^4-a^4*Sin[c+d*x]))+1/64/(d*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:119
  public void test0062() {
    check( //
        "Integrate[Cot[e+f*x]^4*(a+a*Sin[e+f*x])^(5/2), x]", //
        "55/8*a^(5/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/f-2/5*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-5/12*a*Cot[e+f*x]*Csc[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-1/3*Cot[e+f*x]*Csc[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)/f-9/40*a^3*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-16/15*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f+17/24*a^2*Cot[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:179
  public void test0063() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "2*b*Csc[c+d*x]/d+a*Csc[c+d*x]^2/d-1/3*b*Csc[c+d*x]^3/d-1/4*a*Csc[c+d*x]^4/d+a*Log[Sin[c+d*x]]/d+b*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:213
  public void test0064() {
    check( //
        "Integrate[Tan[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "2*a^4*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)+a^2*b*Sec[c+d*x]/((a^2-b^2)^2*d)+b*Sec[c+d*x]/((a^2-b^2)*d)-1/3*b*Sec[c+d*x]^3/((a^2-b^2)*d)-a^3*Tan[c+d*x]/((a^2-b^2)^2*d)+1/3*a*Tan[c+d*x]^3/((a^2-b^2)*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:17
  public void test0065() {
    check( //
        "Integrate[Sin[a+b*x]/(c+d*x)^2, x]", //
        "b*CosIntegral[b*c/d+b*x]*Cos[a-b*c/d]/d^2-b*SinIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^2-Sin[a+b*x]/(d*(c+d*x))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:33
  public void test0066() {
    check( //
        "Integrate[Sin[a+b*x]^3/(c+d*x)^3, x]", //
        "-3/8*b^2*Cos[a-b*c/d]*SinIntegral[b*c/d+b*x]/d^3+9/8*b^2*Cos[3*a-3*b*c/d]*SinIntegral[3*b*c/d+3*b*x]/d^3+9/8*b^2*CosIntegral[3*b*c/d+3*b*x]*Sin[3*a-3*b*c/d]/d^3-3/8*b^2*CosIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^3-3/2*b*Cos[a+b*x]*Sin[a+b*x]^2/(d^2*(c+d*x))-1/2*Sin[a+b*x]^3/(d*(c+d*x)^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:77
  public void test0067() {
    check( //
        "Integrate[(d*x)^(3/2)*Sin[f*x], x]", //
        "-(d*x)^(3/2)*Cos[f*x]/f-3/2*d^(3/2)*FresnelS[Sqrt[2/Pi]*Sqrt[f]*Sqrt[d*x]/Sqrt[d]]*Sqrt[1/2*Pi]/f^(5/2)+3/2*d*Sin[f*x]*Sqrt[d*x]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:108
  public void test0068() {
    check( //
        "Integrate[x^(-1+m)*Sin[a+b*x], x]", //
        "1/2*I*E^(I*a)*x^m*Gamma[m,-I*b*x]/(-I*b*x)^m-1/2*I*x^m*Gamma[m,I*b*x]/(E^(I*a)*(I*b*x)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:138
  public void test0069() {
    check( //
        "Integrate[(c+d*x)*(a+a*Sin[e+f*x]), x]", //
        "1/2*a*(c+d*x)^2/d-a*(c+d*x)*Cos[e+f*x]/f+a*d*Sin[e+f*x]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:184
  public void test0070() {
    check( //
        "Integrate[x^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-4*x^2*ArcTanh[E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+8*I*x*PolyLog[2,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])-8*I*x*PolyLog[2,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])-16*PolyLog[3,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^3*Sqrt[a+a*Sin[c+d*x]])+16*PolyLog[3,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^3*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:281
  public void test0071() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "ArcTanh[Cos[c+d*x]]/(a*d)-2*Cot[c+d*x]/(a*d)+Cot[c+d*x]/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:314
  public void test0072() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "1/2*(2*a^2+b^2)*x/b^3+a*Cos[c+d*x]/(b^2*d)-1/2*Cos[c+d*x]*Sin[c+d*x]/(b*d)-2*a^3*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^3*d*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:351
  public void test0073() {
    check( //
        "Integrate[(e+f*x)^2*Cos[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/3*I*(e+f*x)^3/(a*f)+2*(e+f*x)^2*Log[1-I*E^(I*(c+d*x))]/(a*d)-4*I*f*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)+4*f^2*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:371
  public void test0074() {
    check( //
        "Integrate[(e+f*x)^2*Sec[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-I*(e+f*x)^2*ArcTan[E^(I*(c+d*x))]/(a*d)+f^2*ArcTanh[Sin[c+d*x]]/(a*d^3)+f^2*Log[Cos[c+d*x]]/(a*d^3)+I*f*(e+f*x)*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-I*f*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-f^2*PolyLog[3,-I*E^(I*(c+d*x))]/(a*d^3)+f^2*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)-f*(e+f*x)*Sec[c+d*x]/(a*d^2)-1/2*(e+f*x)^2*Sec[c+d*x]^2/(a*d)+f*(e+f*x)*Tan[c+d*x]/(a*d^2)+1/2*(e+f*x)^2*Sec[c+d*x]*Tan[c+d*x]/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:405
  public void test0075() {
    check( //
        "Integrate[(e+f*x)*Cos[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-1/2*I*(e+f*x)^2/(b*f)+(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d)+(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d)-I*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d^2)-I*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d^2)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:10
  public void test0076() {
    check( //
        "Integrate[x^3*(a+b*x)*Sin[c+d*x], x]", //
        "-24*b*Cos[c+d*x]/d^5+6*a*x*Cos[c+d*x]/d^3+12*b*x^2*Cos[c+d*x]/d^3-a*x^3*Cos[c+d*x]/d-b*x^4*Cos[c+d*x]/d-6*a*Sin[c+d*x]/d^4-24*b*x*Sin[c+d*x]/d^4+3*a*x^2*Sin[c+d*x]/d^2+4*b*x^3*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:27
  public void test0077() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^2])^2/x^3, x]", //
        "1/4*(-2*a^2-b^2)/x^2+a*b*d*CosIntegral[d*x^2]*Cos[c]+1/4*b^2*Cos[2*(c+d*x^2)]/x^2+1/2*b^2*d*Cos[2*c]*SinIntegral[2*d*x^2]-a*b*d*SinIntegral[d*x^2]*Sin[c]+1/2*b^2*d*CosIntegral[2*d*x^2]*Sin[2*c]-a*b*Sin[c+d*x^2]/x^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:45
  public void test0078() {
    check( //
        "Integrate[(1+Sin[x^2])^2/x^3, x]", //
        "(-3/4)/x^2+CosIntegral[x^2]+1/4*Cos[2*x^2]/x^2+1/2*SinIntegral[2*x^2]-Sin[x^2]/x^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:86
  public void test0079() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^3])/x^2, x]", //
        "-a/x-1/2*E^(I*c)*b*d*x^2*Gamma[2/3,-I*d*x^3]/(-I*d*x^3)^(2/3)-1/2*b*d*x^2*Gamma[2/3,I*d*x^3]/(E^(I*c)*(I*d*x^3)^(2/3))-b*Sin[c+d*x^3]/x");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:145
  public void test0080() {
    check( //
        "Integrate[Sin[a+b/x]/x^5, x]", //
        "Cos[a+b/x]/(b*x^3)-6*Cos[a+b/x]/(b^3*x)+6*Sin[a+b/x]/b^4-3*Sin[a+b/x]/(b^2*x^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:193
  public void test0081() {
    check( //
        "Integrate[x^(-1-n)*Sin[a+b*x^n]^2, x]", //
        "(-1/2)/(n*x^n)+1/2*Cos[2*(a+b*x^n)]/(n*x^n)+b*Cos[2*a]*SinIntegral[2*b*x^n]/n+b*CosIntegral[2*b*x^n]*Sin[2*a]/n");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:227
  public void test0082() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^2], x]", //
        "Cos[a]*FresnelS[(c+d*x)*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/(d*Sqrt[b])+FresnelC[(c+d*x)*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*Sqrt[1/2*Pi]/(d*Sqrt[b])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:257
  public void test0083() {
    check( //
        "Integrate[(e+f*x)^2*Sin[a+b*(c+d*x)^(3/2)], x]", //
        "-2/3*f^2*(c+d*x)^(3/2)*Cos[a+b*(c+d*x)^(3/2)]/(b*d^3)+1/3*I*E^(I*a)*(d*e-c*f)^2*(c+d*x)*Gamma[2/3,-I*b*(c+d*x)^(3/2)]/(d^3*(-I*b*(c+d*x)^(3/2))^(2/3))-1/3*I*(d*e-c*f)^2*(c+d*x)*Gamma[2/3,I*b*(c+d*x)^(3/2)]/(E^(I*a)*d^3*(I*b*(c+d*x)^(3/2))^(2/3))+2/3*f^2*Sin[a+b*(c+d*x)^(3/2)]/(b^2*d^3)-4/3*f*(d*e-c*f)*Cos[a+b*(c+d*x)^(3/2)]*Sqrt[c+d*x]/(b*d^3)-2/9*E^(I*a)*f*(d*e-c*f)*Gamma[1/3,-I*b*(c+d*x)^(3/2)]*Sqrt[c+d*x]/(b*d^3*(-I*b*(c+d*x)^(3/2))^(1/3))-2/9*f*(d*e-c*f)*Gamma[1/3,I*b*(c+d*x)^(3/2)]*Sqrt[c+d*x]/(E^(I*a)*b*d^3*(I*b*(c+d*x)^(3/2))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:327
  public void test0084() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(8/3), x]", //
        "-36*Cos[a+b/(c+d*x)^(1/3)]/(b^3*d*e^2*(e*(c+d*x))^(2/3))+3*Cos[a+b/(c+d*x)^(1/3)]/(b*d*e^2*(c+d*x)^(2/3)*(e*(c+d*x))^(2/3))+72*(c+d*x)^(2/3)*Cos[a+b/(c+d*x)^(1/3)]/(b^5*d*e^2*(e*(c+d*x))^(2/3))-12*Sin[a+b/(c+d*x)^(1/3)]/(b^2*d*e^2*(c+d*x)^(1/3)*(e*(c+d*x))^(2/3))+72*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/(b^4*d*e^2*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:354
  public void test0085() {
    check( //
        "Integrate[x^2*(a+b*Sin[c+d*(f+g*x)^n]), x]", //
        "1/3*a*x^3+1/2*I*E^(I*c)*b*f^2*(f+g*x)*Gamma[1/n,-I*d*(f+g*x)^n]/(g^3*n*(-I*d*(f+g*x)^n)^(1/n))-1/2*I*b*f^2*(f+g*x)*Gamma[1/n,I*d*(f+g*x)^n]/(E^(I*c)*g^3*n*(I*d*(f+g*x)^n)^(1/n))-I*E^(I*c)*b*f*(f+g*x)^2*Gamma[2/n,-I*d*(f+g*x)^n]/(g^3*n*(-I*d*(f+g*x)^n)^(2/n))+I*b*f*(f+g*x)^2*Gamma[2/n,I*d*(f+g*x)^n]/(E^(I*c)*g^3*n*(I*d*(f+g*x)^n)^(2/n))+1/2*I*E^(I*c)*b*(f+g*x)^3*Gamma[3/n,-I*d*(f+g*x)^n]/(g^3*n*(-I*d*(f+g*x)^n)^(3/n))-1/2*I*b*(f+g*x)^3*Gamma[3/n,I*d*(f+g*x)^n]/(E^(I*c)*g^3*n*(I*d*(f+g*x)^n)^(3/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:436
  public void test0086() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(1/3)/x^3, x]", //
        "-1/2*(c*Sin[a+b*x^2]^3)^(1/3)/x^2+1/2*b*CosIntegral[b*x^2]*Cos[a]*Csc[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)-1/2*b*Csc[a+b*x^2]*SinIntegral[b*x^2]*Sin[a]*(c*Sin[a+b*x^2]^3)^(1/3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:460
  public void test0087() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(2/3)/x^3, x]", //
        "-1/2*(c*Sin[a+b*x]^3)^(2/3)/x^2-b*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/x+b^2*CosIntegral[2*b*x]*Cos[2*a]*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)-b^2*Csc[a+b*x]^2*SinIntegral[2*b*x]*Sin[2*a]*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:28
  public void test0088() {
    check( //
        "Integrate[x*Sin[a+b*x-c*x^2]^2, x]", //
        "1/4*x^2+1/8*Sin[2*a+2*b*x-2*c*x^2]/c+1/8*b*Cos[2*a+1/2*b^2/c]*FresnelC[(b-2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sqrt[Pi]/c^(3/2)+1/8*b*FresnelS[(b-2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sin[2*a+1/2*b^2/c]*Sqrt[Pi]/c^(3/2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:56
  public void test0089() {
    check( //
        "Integrate[Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-3/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-3/4*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:90
  public void test0090() {
    check( //
        "Integrate[Sin[c+d*x]/(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-3/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:204
  public void test0091() {
    check( //
        "Integrate[Csc[e+f*x]*(a+b*Sin[e+f*x])^2, x]", //
        "2*a*b*x-a^2*ArcTanh[Cos[e+f*x]]/f-b^2*Cos[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:224
  public void test0092() {
    check( //
        "Integrate[Sin[x]/(a+b*Sin[x]), x]", //
        "x/b-2*a*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(b*Sqrt[a^2-b^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:240
  public void test0093() {
    check( //
        "Integrate[Sin[x]^3/(a+b*Sin[x])^3, x]", //
        "x/b^3-a*(2*a^4-5*a^2*b^2+6*b^4)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(b^3*(a^2-b^2)^(5/2))+1/2*a^2*Cos[x]*Sin[x]/(b*(a^2-b^2)*(a+b*Sin[x])^2)+1/2*a^2*(2*a^2-5*b^2)*Cos[x]/(b^2*(a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:313
  public void test0094() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^6, x]", //
        "1/11*a^2*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^8)+1/33*a^2*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^7)+2/231*a^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+2/1155*a^2*Cos[e+f*x]^5/(c*f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:331
  public void test0095() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^3/(a+a*Sin[e+f*x]), x]", //
        "-15/2*c^3*x/a-15/2*c^3*Cos[e+f*x]/(a*f)-2*a^2*c^3*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^3)-5/2*c^3*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:347
  public void test0096() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^5), x]", //
        "1/9*Sec[e+f*x]^3/(a^2*c^2*f*(c-c*Sin[e+f*x])^3)+2/21*Sec[e+f*x]^3/(a^2*c^3*f*(c-c*Sin[e+f*x])^2)+2/21*Sec[e+f*x]^3/(a^2*f*(c^5-c^5*Sin[e+f*x]))+8/21*Tan[e+f*x]/(a^2*c^5*f)+8/63*Tan[e+f*x]^3/(a^2*c^5*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:367
  public void test0097() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "2*a*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[c])-2*a*Cos[e+f*x]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:383
  public void test0098() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2/7*a^3*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:401
  public void test0099() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^2, x]", //
        "64/3*c*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-16*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)+2*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(7/2)/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:421
  public void test0100() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(3/2), x]", //
        "-1/2*a*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:437
  public void test0101() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/6*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/15*a^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])-2/15*a^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:453
  public void test0102() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])-1/3*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*Sqrt[c-c*Sin[e+f*x]])-8*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-4*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:471
  public void test0103() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*(a+a*Sin[e+f*x])^(3/2))-4*c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:496
  public void test0104() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^m/(f*(1+2*m)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:550
  public void test0105() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x]), x]", //
        "-Cos[e+f*x]/(f*(a+a*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:566
  public void test0106() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^3, x]", //
        "d^3*x/a^3-1/15*(c-d)^2*(2*c+7*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^2)-1/15*(c-d)*(2*c^2+11*c*d+29*d^2)*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))-1/5*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(f*(a+a*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:640
  public void test0107() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c+d*Sin[e+f*x])^2, x]", //
        "-2/105*a*(21*c^2+30*c*d+13*d^2)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-4/63*(9*c-d)*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/f-2/9*d^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*f)-64/315*a^3*(21*c^2+30*c*d+13*d^2)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-16/315*a^2*(21*c^2+30*c*d+13*d^2)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:658
  public void test0108() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:678
  public void test0109() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^(5/2), x]", //
        "-2/3*a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-4/3*a*Cos[e+f*x]/((c+d)^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:694
  public void test0110() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^(7/2), x]", //
        "2/15*a^3*(c-d)*(3*c+11*d)*Cos[e+f*x]/(d^2*(c+d)^2*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+2/5*a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*(c+d*Sin[e+f*x])^(5/2))-2/15*a^3*(3*c^2+14*c*d+43*d^2)*Cos[e+f*x]/(d^2*(c+d)^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:712
  public void test0111() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-3/16*(c+d)^2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2]*Sqrt[c-d])-1/4*(c-d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c+7*d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(a*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:795
  public void test0112() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])/(c+d*Sin[e+f*x])^2, x]", //
        "2*(a*c-b*d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((c^2-d^2)^(3/2)*f)-(b*c-a*d)*Cos[e+f*x]/((c^2-d^2)*f*(c+d*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:830
  public void test0113() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+b*Sin[e+f*x])^2, x]", //
        "2*(a*c-b*d)*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*f)+(b*c-a*d)*Cos[e+f*x]/((a^2-b^2)*f*(a+b*Sin[e+f*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:123
  public void test0114() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/Sqrt[c-c*Sin[e+f*x]], x]", //
        "-2/3*a*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*a*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:139
  public void test0115() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]], x]", //
        "-2/21*a*c*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])+2/9*c*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-22/45*a^3*c*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/15*a^3*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-22/105*a^2*c*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:155
  public void test0116() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(11/2), x]", //
        "4/17*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(11/2))-60/221*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(9/2))-308/663*a^4*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+154/221*a^4*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+220/663*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*(c-c*Sin[e+f*x])^(7/2))-154/221*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^5*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:173
  public void test0117() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-2*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2))+6/5*(g*Cos[e+f*x])^(5/2)/(a*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+6/5*(g*Cos[e+f*x])^(5/2)/(a*c*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-6/5*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:253
  public void test0118() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "-1/3*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:355
  public void test0119() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "1/2*a^3*x-3*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d-1/3*a^3*Cos[c+d*x]^3/d-a^3*Cot[c+d*x]/d+3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:373
  public void test0120() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "1/2*ArcTanh[Cos[c+d*x]]/(a*d)-Cot[c+d*x]/(a*d)-1/3*Cot[c+d*x]^3/(a*d)+1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:411
  public void test0121() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-4/105*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a^2*d)-4/45*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/63*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+2/9*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+8/315*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:433
  public void test0122() {
    check( //
        "Integrate[Cos[c+d*x]^3*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "-1/4*a*Cos[c+d*x]^4/d+1/3*a*Sin[c+d*x]^3/d-1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:459
  public void test0123() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "1/16*a*x-1/5*a*Cos[c+d*x]^5/d+1/16*a*Cos[c+d*x]*Sin[c+d*x]/d+1/24*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*a*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:475
  public void test0124() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "-3*a^2*x+1/2*a^2*ArcTanh[Cos[c+d*x]]/d+1/3*a^2*Cos[c+d*x]^3/d-2*a^2*Cot[c+d*x]/d-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]/d-a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:491
  public void test0125() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^3, x]", //
        "3/2*a^3*x+33/8*a^3*ArcTanh[Cos[c+d*x]]/d-3*a^3*Cos[c+d*x]/d+2*a^3*Cot[c+d*x]/d-a^3*Cot[c+d*x]^3/d-7/8*a^3*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:509
  public void test0126() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "-1/2*ArcTanh[Cos[c+d*x]]/(a*d)-1/3*Cot[c+d*x]^3/(a*d)+1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:546
  public void test0127() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-67/64*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+61/64*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+61/96*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/24*a*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])-1/4*Cot[c+d*x]*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:564
  public void test0128() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "8/315*a^2*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))+2/63*a*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/9*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:580
  public void test0129() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)+4496/693*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+200/231*Cos[c+d*x]*Sin[c+d*x]^2/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-424/693*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+46/99*Cos[c+d*x]*Sin[c+d*x]^4/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-2/11*Cos[c+d*x]*Sin[c+d*x]^5/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-1048/693*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:678
  public void test0130() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^n*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*Sin[c+d*x]^(1+n)/(d*(1+n))+3*a^3*Sin[c+d*x]^(2+n)/(d*(2+n))+a^3*Sin[c+d*x]^(3+n)/(d*(3+n))-5*a^3*Sin[c+d*x]^(4+n)/(d*(4+n))-5*a^3*Sin[c+d*x]^(5+n)/(d*(5+n))+a^3*Sin[c+d*x]^(6+n)/(d*(6+n))+3*a^3*Sin[c+d*x]^(7+n)/(d*(7+n))+a^3*Sin[c+d*x]^(8+n)/(d*(8+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:702
  public void test0131() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^8*(a+a*Sin[c+d*x]), x]", //
        "5/16*a*ArcTanh[Cos[c+d*x]]/d-1/7*a*Cot[c+d*x]^7/d-5/16*a*Cot[c+d*x]*Csc[c+d*x]/d+5/24*a*Cot[c+d*x]^3*Csc[c+d*x]/d-1/6*a*Cot[c+d*x]^5*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:718
  public void test0132() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^8*(a+a*Sin[c+d*x])^2, x]", //
        "-a^2*x+5/8*a^2*ArcTanh[Cos[c+d*x]]/d-a^2*Cot[c+d*x]/d+1/3*a^2*Cot[c+d*x]^3/d-1/5*a^2*Cot[c+d*x]^5/d-1/7*a^2*Cot[c+d*x]^7/d-5/8*a^2*Cot[c+d*x]*Csc[c+d*x]/d+5/12*a^2*Cot[c+d*x]^3*Csc[c+d*x]/d-1/3*a^2*Cot[c+d*x]^5*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:734
  public void test0133() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^3, x]", //
        "-1/2*a^3*x-85/16*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d-a^3*Cot[c+d*x]/d+2/3*a^3*Cot[c+d*x]^3/d-3/5*a^3*Cot[c+d*x]^5/d+43/16*a^3*Cot[c+d*x]*Csc[c+d*x]/d-5/24*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d+1/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:769
  public void test0134() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "3*x/a^3+3*ArcTanh[Cos[c+d*x]]/(a^3*d)+Cos[c+d*x]/(a^3*d)-Cot[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:818
  public void test0135() {
    check( //
        "Integrate[Cos[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "-(a-a*Sin[c+d*x])^4/(a^5*d)+4/5*(a-a*Sin[c+d*x])^5/(a^6*d)-1/6*(a-a*Sin[c+d*x])^6/(a^7*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:838
  public void test0136() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^n*(a+a*Sin[c+d*x]), x]", //
        "a*Sin[c+d*x]^(1+n)/(d*(1+n))+a*Sin[c+d*x]^(2+n)/(d*(2+n))-3*a*Sin[c+d*x]^(3+n)/(d*(3+n))-3*a*Sin[c+d*x]^(4+n)/(d*(4+n))+3*a*Sin[c+d*x]^(5+n)/(d*(5+n))+3*a*Sin[c+d*x]^(6+n)/(d*(6+n))-a*Sin[c+d*x]^(7+n)/(d*(7+n))-a*Sin[c+d*x]^(8+n)/(d*(8+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:864
  public void test0137() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^8/(a+a*Sin[c+d*x]), x]", //
        "-5/16*ArcTanh[Cos[c+d*x]]/(a*d)-1/7*Cot[c+d*x]^7/(a*d)+5/16*Cot[c+d*x]*Csc[c+d*x]/(a*d)-5/24*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)+1/6*Cot[c+d*x]^5*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:880
  public void test0138() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^7/(a+a*Sin[c+d*x])^2, x]", //
        "-7/16*ArcTanh[Cos[c+d*x]]/(a^2*d)+2/5*Cot[c+d*x]^5/(a^2*d)+5/16*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/4*Cot[c+d*x]^3*Csc[c+d*x]/(a^2*d)+1/8*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/6*Cot[c+d*x]^3*Csc[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:896
  public void test0139() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^8/(a+a*Sin[c+d*x])^3, x]", //
        "-5/16*ArcTanh[Cos[c+d*x]]/(a^3*d)-4/3*Cot[c+d*x]^3/(a^3*d)-Cot[c+d*x]^5/(a^3*d)-1/7*Cot[c+d*x]^7/(a^3*d)-5/16*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)+1/8*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*d)+1/2*Cot[c+d*x]*Csc[c+d*x]^5/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:940
  public void test0140() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "Sec[c+d*x]/(a^2*d)-Sec[c+d*x]^3/(a^2*d)+2/5*Sec[c+d*x]^5/(a^2*d)-2/5*Tan[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1019
  public void test0141() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*x+1/3*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^3/d-2*a^5*Cos[c+d*x]/(d*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1037
  public void test0142() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^6/(a+a*Sin[c+d*x])^2, x]", //
        "x/a^2+2*Sec[c+d*x]/(a^2*d)-2*Sec[c+d*x]^3/(a^2*d)+6/5*Sec[c+d*x]^5/(a^2*d)-2/7*Sec[c+d*x]^7/(a^2*d)-Tan[c+d*x]/(a^2*d)+1/3*Tan[c+d*x]^3/(a^2*d)-1/5*Tan[c+d*x]^5/(a^2*d)+2/7*Tan[c+d*x]^7/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1168
  public void test0143() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^4*(c+d*Sin[e+f*x])^n, x]", //
        "a^4*(c-d)^4*(c+d*Sin[e+f*x])^(1+n)/(d^5*f*(1+n))-4*a^4*(c-d)^3*(c+d*Sin[e+f*x])^(2+n)/(d^5*f*(2+n))+6*a^4*(c-d)^2*(c+d*Sin[e+f*x])^(3+n)/(d^5*f*(3+n))-4*a^4*(c-d)*(c+d*Sin[e+f*x])^(4+n)/(d^5*f*(4+n))+a^4*(c+d*Sin[e+f*x])^(5+n)/(d^5*f*(5+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1230
  public void test0144() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "2/3*(A-B)*(a+a*Sin[c+d*x])^3/(a^2*d)-1/4*(A-3*B)*(a+a*Sin[c+d*x])^4/(a^3*d)-1/5*B*(a+a*Sin[c+d*x])^5/(a^4*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1246
  public void test0145() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/2*(A-B)*(a+a*Sin[c+d*x])^4/(a^2*d)-1/5*(A-3*B)*(a+a*Sin[c+d*x])^5/(a^3*d)-1/6*B*(a+a*Sin[c+d*x])^6/(a^4*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1262
  public void test0146() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "2/3*(A-B)*(a+a*Sin[c+d*x])^6/(a^3*d)-4/7*(A-2*B)*(a+a*Sin[c+d*x])^7/(a^4*d)+1/8*(A-5*B)*(a+a*Sin[c+d*x])^8/(a^5*d)+1/9*B*(a+a*Sin[c+d*x])^9/(a^6*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1305
  public void test0147() {
    check( //
        "Integrate[Cos[e+f*x]^7*(a+a*Sin[e+f*x])^m*(A+B*Sin[e+f*x]), x]", //
        "8*(A-B)*(a+a*Sin[e+f*x])^(4+m)/(a^4*f*(4+m))-4*(3*A-5*B)*(a+a*Sin[e+f*x])^(5+m)/(a^5*f*(5+m))+6*(A-3*B)*(a+a*Sin[e+f*x])^(6+m)/(a^6*f*(6+m))-(A-7*B)*(a+a*Sin[e+f*x])^(7+m)/(a^7*f*(7+m))-B*(a+a*Sin[e+f*x])^(8+m)/(a^8*f*(8+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1358
  public void test0148() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "1/8*a*ArcTanh[Cos[c+d*x]]/d-1/3*b*Cot[c+d*x]^3/d+1/8*a*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1374
  public void test0149() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3, x]", //
        "-1/2*b*(6*a^2-b^2)*x+1/2*a*(a^2-6*b^2)*ArcTanh[Cos[c+d*x]]/d+15/2*a*b^2*Cos[c+d*x]/d+5/2*b^3*Cos[c+d*x]*Sin[c+d*x]/d-3/2*b*Cot[c+d*x]*(a+b*Sin[c+d*x])^2/d-1/2*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1420
  public void test0150() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "3/64*a*b*x-1/105*(9*a^2+4*b^2)*Cos[c+d*x]/d+1/315*(9*a^2+4*b^2)*Cos[c+d*x]^3/d-3/64*a*b*Cos[c+d*x]*Sin[c+d*x]/d-1/32*a*b*Cos[c+d*x]*Sin[c+d*x]^3/d-1/630*(15*a^4-44*a^2*b^2+6*b^4)*Cos[c+d*x]*Sin[c+d*x]^4/(b^2*d)-1/504*a*(10*a^2-29*b^2)*Cos[c+d*x]*Sin[c+d*x]^5/(b*d)-5/252*(3*a^2-8*b^2)*Cos[c+d*x]*Sin[c+d*x]^4*(a+b*Sin[c+d*x])^2/(b^2*d)+1/12*a*Cos[c+d*x]*Sin[c+d*x]^4*(a+b*Sin[c+d*x])^3/(b^2*d)-1/9*Cos[c+d*x]*Sin[c+d*x]^5*(a+b*Sin[c+d*x])^3/(b*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1437
  public void test0151() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^3, x]", //
        "3/2*b*(2*a^2-b^2)*x-3/8*a*(a^2-12*b^2)*ArcTanh[Cos[c+d*x]]/d-1/8*b^2*(73*a^2-2*b^2)*Cos[c+d*x]/(a*d)-13/4*b^3*Cos[c+d*x]*Sin[c+d*x]/d+17/8*b*Cot[c+d*x]*(a+b*Sin[c+d*x])^2/d+5/8*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^3/d-1/4*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1456
  public void test0152() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2/(a+b*Sin[c+d*x])^3, x]", //
        "3*b*ArcTanh[Cos[c+d*x]]/(a^4*d)+1/2*(a^2-3*b^2)*Cos[c+d*x]/(a^2*b*d*(a+b*Sin[c+d*x])^2)-Cot[c+d*x]/(a*d*(a+b*Sin[c+d*x])^2)-1/2*(a^2+6*b^2)*Cos[c+d*x]/(a^3*b*d*(a+b*Sin[c+d*x]))-3*(a^2-2*b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^4*d*Sqrt[a^2-b^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1546
  public void test0153() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^9*(a+b*Sin[c+d*x]), x]", //
        "-1/6*a*Cot[c+d*x]^6/d-1/8*a*Cot[c+d*x]^8/d-1/3*b*Csc[c+d*x]^3/d+2/5*b*Csc[c+d*x]^5/d-1/7*b*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1637
  public void test0154() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])^2/((g*Cos[e+f*x])^(7/2)*Sqrt[d*Sin[e+f*x]]), x]", //
        "8/5*a*b*(d*Sin[e+f*x])^(3/2)/(d^2*f*g^3*Sqrt[g*Cos[e+f*x]])+2/5*(a+b*Sin[e+f*x])^2*Sqrt[d*Sin[e+f*x]]/(d*f*g*(g*Cos[e+f*x])^(5/2))+8/5*a^2*Sqrt[d*Sin[e+f*x]]/(d*f*g^3*Sqrt[g*Cos[e+f*x]])-8/5*a*b*EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[g*Cos[e+f*x]]*Sqrt[d*Sin[e+f*x]]/(d*f*g^4*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1987
  public void test0155() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+b*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "-1/3*(a^2-b^2)^3*(A*b-a*B)*(a+b*Sin[c+d*x])^3/(b^8*d)+1/4*(a^2-b^2)^2*(6*a*A*b-7*a^2*B+b^2*B)*(a+b*Sin[c+d*x])^4/(b^8*d)-3/5*(a^2-b^2)*(5*a^2*A*b-A*b^3-7*a^3*B+3*a*b^2*B)*(a+b*Sin[c+d*x])^5/(b^8*d)+1/6*(20*a^3*A*b-12*a*A*b^3-35*a^4*B+30*a^2*b^2*B-3*b^4*B)*(a+b*Sin[c+d*x])^6/(b^8*d)-1/7*(15*a^2*A*b-3*A*b^3-35*a^3*B+15*a*b^2*B)*(a+b*Sin[c+d*x])^7/(b^8*d)+3/8*(2*a*A*b-7*a^2*B+b^2*B)*(a+b*Sin[c+d*x])^8/(b^8*d)-1/9*(A*b-7*a*B)*(a+b*Sin[c+d*x])^9/(b^8*d)-1/10*B*(a+b*Sin[c+d*x])^10/(b^8*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:18
  public void test0156() {
    check( //
        "Integrate[Csc[e+f*x]^5*(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "1/8*a^2*c*ArcTanh[Cos[e+f*x]]/f-1/3*a^2*c*Cot[e+f*x]^3/f+1/8*a^2*c*Cot[e+f*x]*Csc[e+f*x]/f-1/4*a^2*c*Cot[e+f*x]*Csc[e+f*x]^3/f");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:73
  public void test0157() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]/((c+d*Sin[e+f*x])*Sqrt[g*Sin[e+f*x]]), x]", //
        "-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[c]*Sqrt[g]/(Sqrt[c+d]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[c]*Sqrt[c+d]*Sqrt[g])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:43
  public void test0158() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x]), x]", //
        "1/2*a*A*c*x-1/3*a*B*c*Cos[e+f*x]^3/f+1/2*a*A*c*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:59
  public void test0159() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^6, x]", //
        "1/11*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^8)+1/99*a^2*(3*A-8*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^7)+2/693*a^2*(3*A-8*B)*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+2/3465*a^2*(3*A-8*B)*Cos[e+f*x]^5/(c*f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:77
  public void test0160() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^4/(a+a*Sin[e+f*x]), x]", //
        "-35/8*(4*A-5*B)*c^4*x/a-35/12*(4*A-5*B)*c^4*Cos[e+f*x]^3/(a*f)-35/8*(4*A-5*B)*c^4*Cos[e+f*x]*Sin[e+f*x]/(a*f)-a^4*(A-B)*c^4*Cos[e+f*x]^9/(f*(a+a*Sin[e+f*x])^5)-2*a^2*(4*A-5*B)*c^4*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^3)-7/4*(4*A-5*B)*c^4*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:93
  public void test0161() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^4), x]", //
        "1/7*(A+B)*Sec[e+f*x]^3/(a^2*f*(c^2-c^2*Sin[e+f*x])^2)+1/35*(5*A-2*B)*Sec[e+f*x]^3/(a^2*f*(c^4-c^4*Sin[e+f*x]))+4/35*(5*A-2*B)*Tan[e+f*x]/(a^2*c^4*f)+4/105*(5*A-2*B)*Tan[e+f*x]^3/(a^2*c^4*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:113
  public void test0162() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2/15*a*(5*A+B)*c^2*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))-2/5*a*B*c*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:129
  public void test0163() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/693*a^3*(11*A+3*B)*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+2/99*a^3*(11*A+3*B)*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))-2/11*a^3*B*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(3/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:147
  public void test0164() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^2, x]", //
        "-32/15*(5*A-11*B)*c^2*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-4/15*(5*A-11*B)*c*Sec[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)-1/15*(5*A-11*B)*Sec[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a^2*f)-1/3*(A-B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(11/2)/(a^2*c^2*f)+128/15*(5*A-11*B)*c^3*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:167
  public void test0165() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/3*a*(A+B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*Sqrt[a+a*Sin[e+f*x]])+1/4*a*B*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:183
  public void test0166() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/10*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(11/2))-1/120*a^2*(3*A-7*B)*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+1/40*a*(3*A-7*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:199
  public void test0167() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2), x]", //
        "1/4*(A-B)*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])+1/5*B*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:217
  public void test0168() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(3*A-5*B)*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])-1/6*(3*A-5*B)*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])-4*(3*A-5*B)*c^4*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*(3*A-5*B)*c^3*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:246
  public void test0169() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^m*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2*(A-B)*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^m/(f*(1+2*m)*Sqrt[c-c*Sin[e+f*x]])+2*B*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(1+m)/(a*f*(3+2*m)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:277
  public void test0170() {
    check( //
        "Integrate[Csc[c+d*x]^3*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "-2*a^3*A*x-1/2*a^3*A*ArcTanh[Cos[c+d*x]]/d+a^3*A*Cos[c+d*x]/d-2*a^3*A*Cot[c+d*x]/d-1/2*a^3*A*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:354
  public void test0171() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x]), x]", //
        "-2/35*(7*B*c+7*A*d-2*B*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-2/7*B*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(a*f)-8/105*a^2*(35*A*c+21*B*c+21*A*d+19*B*d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/105*a*(35*A*c+21*B*c+21*A*d+19*B*d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:372
  public void test0172() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "-(A-B)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/((c-d)*f*Sqrt[a])-2*(B*c-A*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/((c-d)*f*Sqrt[a]*Sqrt[d]*Sqrt[c+d])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:26
  public void test0173() {
    check( //
        "Integrate[1/Sin[b*x]^(5/2), x]", //
        "-2/3*EllipticF[1/4*Pi-1/2*b*x,2]/b-2/3*Cos[b*x]/(b*Sin[b*x]^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:83
  public void test0174() {
    check( //
        "Integrate[Sec[a+b*x]^8*Sin[a+b*x]^2, x]", //
        "1/3*Tan[a+b*x]^3/b+2/5*Tan[a+b*x]^5/b+1/7*Tan[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:99
  public void test0175() {
    check( //
        "Integrate[Sec[a+b*x]^2*Sin[a+b*x]^3, x]", //
        "Cos[a+b*x]/b+Sec[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:115
  public void test0176() {
    check( //
        "Integrate[Sec[a+b*x]^10*Sin[a+b*x]^4, x]", //
        "1/5*Tan[a+b*x]^5/b+2/7*Tan[a+b*x]^7/b+1/9*Tan[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:181
  public void test0177() {
    check( //
        "Integrate[Sec[a+b*x]/Sin[a+b*x]^3, x]", //
        "-1/2*Cot[a+b*x]^2/b+Log[Tan[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:233
  public void test0178() {
    check( //
        "Integrate[Sin[a+b*x]^2/(d*Cos[a+b*x])^(3/2), x]", //
        "2*Sin[a+b*x]/(b*d*Sqrt[d*Cos[a+b*x]])-4*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^2*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:249
  public void test0179() {
    check( //
        "Integrate[Sin[a+b*x]^4/(d*Cos[a+b*x])^(1/2), x]", //
        "8/7*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-4/7*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/(b*d)-2/7*Sin[a+b*x]^3*Sqrt[d*Cos[a+b*x]]/(b*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:267
  public void test0180() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(11/2)*Csc[a+b*x]^2, x]", //
        "-d*(d*Cos[a+b*x])^(9/2)*Csc[a+b*x]/b-9/7*d^3*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]/b-15/7*d^6*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-15/7*d^5*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:311
  public void test0181() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(5/2), x]", //
        "2/3*(c*Sin[a+b*x])^(3/2)/(b*c*d*(d*Cos[a+b*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:327
  public void test0182() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(5/2)/(d*Cos[a+b*x])^(7/2), x]", //
        "2/5*c*(c*Sin[a+b*x])^(3/2)/(b*d*(d*Cos[a+b*x])^(5/2))-6/5*c*(c*Sin[a+b*x])^(3/2)/(b*d^3*Sqrt[d*Cos[a+b*x]])+6/5*c^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d^4*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:345
  public void test0183() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)/(c*Sin[a+b*x])^(1/2), x]", //
        "ArcTan[1-Sqrt[2]*Sqrt[c]*Sqrt[d*Cos[a+b*x]]/(Sqrt[d]*Sqrt[c*Sin[a+b*x]])]*Sqrt[d]/(b*Sqrt[2]*Sqrt[c])-ArcTan[1+Sqrt[2]*Sqrt[c]*Sqrt[d*Cos[a+b*x]]/(Sqrt[d]*Sqrt[c*Sin[a+b*x]])]*Sqrt[d]/(b*Sqrt[2]*Sqrt[c])-1/2*Log[Sqrt[d]+Cot[a+b*x]*Sqrt[d]-Sqrt[2]*Sqrt[c]*Sqrt[d*Cos[a+b*x]]/Sqrt[c*Sin[a+b*x]]]*Sqrt[d]/(b*Sqrt[2]*Sqrt[c])+1/2*Log[Sqrt[d]+Cot[a+b*x]*Sqrt[d]+Sqrt[2]*Sqrt[c]*Sqrt[d*Cos[a+b*x]]/Sqrt[c*Sin[a+b*x]]]*Sqrt[d]/(b*Sqrt[2]*Sqrt[c])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:464
  public void test0184() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^2, x]", //
        "-4*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+2*b*Sin[e+f*x]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:480
  public void test0185() {
    check( //
        "Integrate[Csc[e+f*x]^4*(b*Sec[e+f*x])^(5/2), x]", //
        "b*Csc[e+f*x]*(b*Sec[e+f*x])^(3/2)/f-1/3*b*Csc[e+f*x]^3*(b*Sec[e+f*x])^(3/2)/f-5/2*b^3*Csc[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])+5/2*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:534
  public void test0186() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]], x]", //
        "-a*b*Sqrt[a*Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]])+1/2*a^2*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:552
  public void test0187() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(5/2)/(b*Sec[e+f*x])^(3/2), x]", //
        "-1/16*a*(a*Sin[e+f*x])^(3/2)/(b*f*Sqrt[b*Sec[e+f*x]])+1/4*(a*Sin[e+f*x])^(7/2)/(a*b*f*Sqrt[b*Sec[e+f*x]])-3/32*a^(5/2)*ArcTan[1-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^(5/2)*f*Sqrt[2])+3/32*a^(5/2)*ArcTan[1+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^(5/2)*f*Sqrt[2])+3/64*a^(5/2)*Log[Sqrt[a]-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^(5/2)*f*Sqrt[2])-3/64*a^(5/2)*Log[Sqrt[a]+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^(5/2)*f*Sqrt[2])");
  }

  // 4.1.1.1 (a+b sin)^n.input:16
  public void test0188() {
    check( //
        "Integrate[1/(a+a*Sin[c+d*x])^(1/2), x]", //
        "-ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:17
  public void test0189() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "1/2*a*x-1/3*a*Cos[c+d*x]^3/d+1/2*a*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:84
  public void test0190() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "-1/5*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^2)-1/5*Sec[c+d*x]/(d*(a^2+a^2*Sin[c+d*x]))+2/5*Tan[c+d*x]/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:136
  public void test0191() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-64/105*a^3*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-16/35*a^2*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/7*a*Cos[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:222
  public void test0192() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]], x]", //
        "-2/3*a*(e*Cos[c+d*x])^(3/2)/(d*e)+2*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:238
  public void test0193() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^3, x]", //
        "-10/21*a^3*(e*Cos[c+d*x])^(7/2)/(d*e)+2/3*a^3*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-2/11*a*(e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])^2/(d*e)-10/33*(e*Cos[c+d*x])^(7/2)*(a^3+a^3*Sin[c+d*x])/(d*e)+2*a^3*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:254
  public void test0194() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(11/2), x]", //
        "4/9*a^7*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a-a*Sin[c+d*x])^3)-2/15*a^8*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a^2-a^2*Sin[c+d*x])^2)-2/15*a^8*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a^4-a^4*Sin[c+d*x]))+2/15*a^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^6*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:272
  public void test0195() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/(a+a*Sin[c+d*x])^2, x]", //
        "-2/3*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^2*d*Sqrt[e*Cos[c+d*x]])-4/3*e*Sqrt[e*Cos[c+d*x]]/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:288
  public void test0196() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(15/2)/(a+a*Sin[c+d*x])^4, x]", //
        "234/35*e^5*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a^4*d)+4*e*(e*Cos[c+d*x])^(13/2)/(a*d*(a+a*Sin[c+d*x])^3)+52/5*e^3*(e*Cos[c+d*x])^(9/2)/(d*(a^4+a^4*Sin[c+d*x]))+78/7*e^8*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^4*d*Sqrt[e*Cos[c+d*x]])+78/7*e^7*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^4*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:308
  public void test0197() {
    check( //
        "Integrate[Sqrt[a+a*Sin[c+d*x]]/(e*Cos[c+d*x])^(9/2), x]", //
        "-12/5*(a+a*Sin[c+d*x])^(3/2)/(a*d*e*(e*Cos[c+d*x])^(7/2))+16/5*(a+a*Sin[c+d*x])^(5/2)/(a^2*d*e*(e*Cos[c+d*x])^(7/2))-32/35*(a+a*Sin[c+d*x])^(7/2)/(a^3*d*e*(e*Cos[c+d*x])^(7/2))-2/5*Sqrt[a+a*Sin[c+d*x]]/(d*e*(e*Cos[c+d*x])^(7/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:342
  public void test0198() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^(3/2)), x]", //
        "(-2/9)/(d*e*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(3/2))+32/45*(a+a*Sin[c+d*x])^(3/2)/(a^3*d*e*(e*Cos[c+d*x])^(3/2))+(-4/15)/(a*d*e*(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]])-16/15*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*e*(e*Cos[c+d*x])^(3/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:431
  public void test0199() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "-1/3*(a^2-b^2)*(a+b*Sin[c+d*x])^3/(b^3*d)+1/2*a*(a+b*Sin[c+d*x])^4/(b^3*d)-1/5*(a+b*Sin[c+d*x])^5/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:463
  public void test0200() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+b*Sin[c+d*x])^8, x]", //
        "-7/16*b^2*(64*a^6+240*a^4*b^2+120*a^2*b^4+5*b^6)*x+1/20*a*b*(40*a^6+1664*a^4*b^2+2789*a^2*b^4+512*b^6)*Cos[c+d*x]/d+1/80*b^2*(80*a^6+2248*a^4*b^2+2502*a^2*b^4+175*b^6)*Cos[c+d*x]*Sin[c+d*x]/d+1/40*a*b*(40*a^4+624*a^2*b^2+337*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d+1/120*b*(120*a^4+992*a^2*b^2+175*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/d+1/30*a*b*(30*a^2+113*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^4/d+1/6*b*(6*a^2+7*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^5/d+a*b*Cos[c+d*x]*(a+b*Sin[c+d*x])^6/d+Sec[c+d*x]*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^7/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:481
  public void test0201() {
    check( //
        "Integrate[Sec[c+d*x]^6/(a+b*Sin[c+d*x]), x]", //
        "-2*b^6*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(7/2)*d)-1/5*Sec[c+d*x]^5*(b-a*Sin[c+d*x])/((a^2-b^2)*d)+1/15*Sec[c+d*x]^3*(5*b^3+a*(4*a^2-9*b^2)*Sin[c+d*x])/((a^2-b^2)^2*d)-1/15*Sec[c+d*x]*(15*b^5-a*(8*a^4-26*a^2*b^2+33*b^4)*Sin[c+d*x])/((a^2-b^2)^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:567
  public void test0202() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Sin[c+d*x])^(3/2), x]", //
        "4/3*(3*a^2-b^2)*(a+b*Sin[c+d*x])^(3/2)/(b^5*d)-8/5*a*(a+b*Sin[c+d*x])^(5/2)/(b^5*d)+2/7*(a+b*Sin[c+d*x])^(7/2)/(b^5*d)-2*(a^2-b^2)^2/(b^5*d*Sqrt[a+b*Sin[c+d*x]])-8*a*(a^2-b^2)*Sqrt[a+b*Sin[c+d*x]]/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:603
  public void test0203() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^2, x]", //
        "-22/63*a*b*(e*Cos[c+d*x])^(7/2)/(d*e)+2/45*(9*a^2+2*b^2)*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-2/9*b*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])/(d*e)+2/15*(9*a^2+2*b^2)*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:619
  public void test0204() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^4, x]", //
        "-34/6435*a*b*(53*a^2+38*b^2)*(e*Cos[c+d*x])^(9/2)/(d*e)+2/385*(55*a^4+60*a^2*b^2+4*b^4)*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d-2/715*b*(93*a^2+26*b^2)*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])/(d*e)-14/65*a*b*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])^2/(d*e)-2/15*b*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])^3/(d*e)+2/231*(55*a^4+60*a^2*b^2+4*b^4)*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/231*(55*a^4+60*a^2*b^2+4*b^4)*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:14
  public void test0205() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])*Tan[c+d*x], x]", //
        "-a*Log[1-Sin[c+d*x]]/d-a*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:65
  public void test0206() {
    check( //
        "Integrate[Cot[c+d*x]^9/(a+a*Sin[c+d*x]), x]", //
        "-1/8*Cot[c+d*x]^8/(a*d)-Csc[c+d*x]/(a*d)+Csc[c+d*x]^3/(a*d)-3/5*Csc[c+d*x]^5/(a*d)+1/7*Csc[c+d*x]^7/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:81
  public void test0207() {
    check( //
        "Integrate[Cot[c+d*x]^7/(a+a*Sin[c+d*x])^2, x]", //
        "1/2*Csc[c+d*x]^2/(a^2*d)-2/3*Csc[c+d*x]^3/(a^2*d)+2/5*Csc[c+d*x]^5/(a^2*d)-1/6*Csc[c+d*x]^6/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:123
  public void test0208() {
    check( //
        "Integrate[Tan[e+f*x]^2/Sqrt[a+a*Sin[e+f*x]], x]", //
        "5/4*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(f*Sqrt[2]*Sqrt[a])-1/2*Sec[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+3/4*Sec[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:214
  public void test0209() {
    check( //
        "Integrate[Tan[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "-2*a^2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)-b*Sec[c+d*x]/((a^2-b^2)*d)+a*Tan[c+d*x]/((a^2-b^2)*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:18
  public void test0210() {
    check( //
        "Integrate[Sin[a+b*x]/(c+d*x)^3, x]", //
        "-1/2*b*Cos[a+b*x]/(d^2*(c+d*x))-1/2*b^2*Cos[a-b*c/d]*SinIntegral[b*c/d+b*x]/d^3-1/2*b^2*CosIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^3-1/2*Sin[a+b*x]/(d*(c+d*x)^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:36
  public void test0211() {
    check( //
        "Integrate[(c+d*x)^3*Csc[a+b*x], x]", //
        "-2*(c+d*x)^3*ArcTanh[E^(I*(a+b*x))]/b+3*I*d*(c+d*x)^2*PolyLog[2,-E^(I*(a+b*x))]/b^2-3*I*d*(c+d*x)^2*PolyLog[2,E^(I*(a+b*x))]/b^2-6*d^2*(c+d*x)*PolyLog[3,-E^(I*(a+b*x))]/b^3+6*d^2*(c+d*x)*PolyLog[3,E^(I*(a+b*x))]/b^3-6*I*d^3*PolyLog[4,-E^(I*(a+b*x))]/b^4+6*I*d^3*PolyLog[4,E^(I*(a+b*x))]/b^4");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:78
  public void test0212() {
    check( //
        "Integrate[(d*x)^(1/2)*Sin[f*x], x]", //
        "FresnelC[Sqrt[2/Pi]*Sqrt[f]*Sqrt[d*x]/Sqrt[d]]*Sqrt[1/2*Pi]*Sqrt[d]/f^(3/2)-Cos[f*x]*Sqrt[d*x]/f");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:109
  public void test0213() {
    check( //
        "Integrate[x^(-2+m)*Sin[a+b*x], x]", //
        "1/2*E^(I*a)*b*x^m*Gamma[-1+m,-I*b*x]/(-I*b*x)^m+1/2*b*x^m*Gamma[-1+m,I*b*x]/(E^(I*a)*(I*b*x)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:139
  public void test0214() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*x), x]", //
        "a*Log[c+d*x]/d+a*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d+a*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:185
  public void test0215() {
    check( //
        "Integrate[x/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-4*x*ArcTanh[E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+4*I*PolyLog[2,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])-4*I*PolyLog[2,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:221
  public void test0216() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])^2/(c+d*x), x]", //
        "-1/2*b^2*CosIntegral[2*c*f/d+2*f*x]*Cos[2*e-2*c*f/d]/d+a^2*Log[c+d*x]/d+1/2*b^2*Log[c+d*x]/d+2*a*b*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d+1/2*b^2*SinIntegral[2*c*f/d+2*f*x]*Sin[2*e-2*c*f/d]/d+2*a*b*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:352
  public void test0217() {
    check( //
        "Integrate[(e+f*x)*Cos[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/2*I*(e+f*x)^2/(a*f)+2*(e+f*x)*Log[1-I*E^(I*(c+d*x))]/(a*d)-2*I*f*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:372
  public void test0218() {
    check( //
        "Integrate[(e+f*x)*Sec[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-I*(e+f*x)*ArcTan[E^(I*(c+d*x))]/(a*d)+1/2*I*f*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-1/2*I*f*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-1/2*f*Sec[c+d*x]/(a*d^2)-1/2*(e+f*x)*Sec[c+d*x]^2/(a*d)+1/2*f*Tan[c+d*x]/(a*d^2)+1/2*(e+f*x)*Sec[c+d*x]*Tan[c+d*x]/(a*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:11
  public void test0219() {
    check( //
        "Integrate[x^2*(a+b*x)*Sin[c+d*x], x]", //
        "2*a*Cos[c+d*x]/d^3+6*b*x*Cos[c+d*x]/d^3-a*x^2*Cos[c+d*x]/d-b*x^3*Cos[c+d*x]/d-6*b*Sin[c+d*x]/d^4+2*a*x*Sin[c+d*x]/d^2+3*b*x^2*Sin[c+d*x]/d^2");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:332
  public void test0220() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^2/(a+a*Sin[e+f*x]), x]", //
        "-3*c^2*x/a-3*c^2*Cos[e+f*x]/(a*f)-2*a*c^2*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:348
  public void test0221() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^5/(a+a*Sin[e+f*x])^3, x]", //
        "-63/2*c^5*x/a^3-63/2*c^5*Cos[e+f*x]/(a^3*f)-2/5*a^4*c^5*Cos[e+f*x]^9/(f*(a+a*Sin[e+f*x])^7)+6/5*a^2*c^5*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^5)-42/5*c^5*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^3)-21/2*c^5*Cos[e+f*x]^3/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:368
  public void test0222() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(3/2))-a*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(3/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:384
  public void test0223() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-2/5*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))-4/3*a^3*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+8*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[c])-8*a^3*Cos[e+f*x]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:402
  public void test0224() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^2, x]", //
        "8/3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-2*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:422
  public void test0225() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(1/2), x]", //
        "-a*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:438
  public void test0226() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "-1/5*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)/f-2/15*a^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/5*a^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:454
  public void test0227() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(3/2))+3/2*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])+12*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:472
  public void test0228() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:551
  public void test0229() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c+d*Sin[e+f*x])), x]", //
        "-Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x]))-2*d*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(a*(c-d)*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:567
  public void test0230() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^3, x]", //
        "-1/15*(c-d)*(2*c+5*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^2)-1/15*(2*c^2+6*c*d+7*d^2)*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))-1/5*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])/(f*(a+a*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:625
  public void test0231() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^3, x]", //
        "-12/35*d^2*(c+d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(a*f)-4/35*a*(c+d)*(15*c^2+10*c*d+7*d^2)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/7*a*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(f*Sqrt[a+a*Sin[e+f*x]])-8/35*(5*c-d)*d*(c+d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:641
  public void test0232() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c+d*Sin[e+f*x]), x]", //
        "-2/35*a*(7*c+5*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-2/7*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/f-64/105*a^3*(7*c+5*d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-16/105*a^2*(7*c+5*d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:659
  public void test0233() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])), x]", //
        "-1/2*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])^(3/2))-1/2*(c-5*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*(c-d)^2*f*Sqrt[2])-2*d^(3/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*(c-d)^2*f*Sqrt[c+d])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:679
  public void test0234() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^(7/2), x]", //
        "-2/5*a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-8/15*a*Cos[e+f*x]/((c+d)^2*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-16/15*a*Cos[e+f*x]/((c+d)^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:695
  public void test0235() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^(9/2), x]", //
        "6/35*a^3*(c-d)*(c+5*d)*Cos[e+f*x]/(d^2*(c+d)^2*f*(c+d*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-2/105*a^3*(3*c^2+22*c*d+115*d^2)*Cos[e+f*x]/(d^2*(c+d)^3*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+2/7*a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*(c+d*Sin[e+f*x])^(7/2))-4/105*a^3*(3*c^2+22*c*d+115*d^2)*Cos[e+f*x]/(d^2*(c+d)^4*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:713
  public void test0236() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/16*(3*c-5*d)*(c+d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(5/2)*(c-d)^(3/2)*f*Sqrt[2])-1/4*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c-d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(a*(c-d)*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:813
  public void test0237() {
    check( //
        "Integrate[(b*B/a+B*Sin[x])/(a+b*Sin[x]), x]", //
        "B*x/b-2*B*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a*b)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:831
  public void test0238() {
    check( //
        "Integrate[1/(a+b*Sin[e+f*x])^2, x]", //
        "2*a*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*f)+b*Cos[e+f*x]/((a^2-b^2)*f*(a+b*Sin[e+f*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:80
  public void test0239() {
    check( //
        "Integrate[Cos[e+f*x]^2/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-1/2*Cos[e+f*x]/(a*c*f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:124
  public void test0240() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x])^(3/2), x]", //
        "4*a*(g*Cos[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-6*a*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:140
  public void test0241() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)/Sqrt[c-c*Sin[e+f*x]], x]", //
        "-2/7*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-22/15*a^3*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/5*a^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-22/35*a^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:156
  public void test0242() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "4/21*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(13/2))-20/119*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(11/2))-220/1989*a^4*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+22/663*a^4*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+22/663*a^4*(g*Cos[e+f*x])^(5/2)/(c^5*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+220/1547*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*(c-c*Sin[e+f*x])^(9/2))-22/663*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^6*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:174
  public void test0243() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)), x]", //
        "-2*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2))+10/9*(g*Cos[e+f*x])^(5/2)/(a*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+2/3*(g*Cos[e+f*x])^(5/2)/(a*c*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+2/3*(g*Cos[e+f*x])^(5/2)/(a*c^2*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-2/3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:338
  public void test0244() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "-a*x+1/2*a*ArcTanh[Cos[c+d*x]]/d-a*Cot[c+d*x]/d-1/2*a*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:356
  public void test0245() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "-5/2*a^3*x-5/2*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d-3*a^3*Cot[c+d*x]/d-1/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d+1/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:374
  public void test0246() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "-3/8*ArcTanh[Cos[c+d*x]]/(a*d)+Cot[c+d*x]/(a*d)+1/3*Cot[c+d*x]^3/(a*d)-3/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:390
  public void test0247() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sin[e+f*x]/(a+a*Sin[e+f*x])^6, x]", //
        "2/9*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^5)-19/63*Cos[e+f*x]/(a^2*f*(a+a*Sin[e+f*x])^4)+2/105*Cos[e+f*x]/(f*(a^2+a^2*Sin[e+f*x])^3)+4/315*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x])^2)+4/315*Cos[e+f*x]/(f*(a^6+a^6*Sin[e+f*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:412
  public void test0248() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-22/105*a*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))+12/35*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/7*Cos[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:476
  public void test0249() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*a^2*x+3*a^2*ArcTanh[Cos[c+d*x]]/d-2*a^2*Cos[c+d*x]/d-1/3*a^2*Cot[c+d*x]^3/d-a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:492
  public void test0250() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^3, x]", //
        "3*a^3*x+3/8*a^3*ArcTanh[Cos[c+d*x]]/d-a^3*Cos[c+d*x]/d+3*a^3*Cot[c+d*x]/d-a^3*Cot[c+d*x]^3/d-1/5*a^3*Cot[c+d*x]^5/d+11/8*a^3*Cot[c+d*x]*Csc[c+d*x]/d-3/4*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:510
  public void test0251() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "1/8*ArcTanh[Cos[c+d*x]]/(a*d)+1/3*Cot[c+d*x]^3/(a*d)+1/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:547
  public void test0252() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-31/128*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-31/128*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+97/192*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+97/240*a*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])-1/40*a*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-1/5*Cot[c+d*x]*Csc[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:565
  public void test0253() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+32/15*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/5*Cos[c+d*x]*Sin[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+2/15*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:581
  public void test0254() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^(5/2), x]", //
        "4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)-2048/315*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-92/105*Cos[c+d*x]*Sin[c+d*x]^2/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+38/63*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-2/9*Cos[c+d*x]*Sin[c+d*x]^4/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+472/315*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:625
  public void test0255() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "4*a^2*Csc[c+d*x]/d+1/2*a^2*Csc[c+d*x]^2/d-2/3*a^2*Csc[c+d*x]^3/d-1/4*a^2*Csc[c+d*x]^4/d-a^2*Log[Sin[c+d*x]]/d+2*a^2*Sin[c+d*x]/d+1/2*a^2*Sin[c+d*x]^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:643
  public void test0256() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/3*Sin[c+d*x]^3/(a*d)-1/4*Sin[c+d*x]^4/(a*d)-1/5*Sin[c+d*x]^5/(a*d)+1/6*Sin[c+d*x]^6/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:659
  public void test0257() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "-1/3*Csc[c+d*x]^3*(a-a*Sin[c+d*x])^3/(a^5*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:679
  public void test0258() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^n*(a+a*Sin[c+d*x])^2, x]", //
        "a^2*Sin[c+d*x]^(1+n)/(d*(1+n))+2*a^2*Sin[c+d*x]^(2+n)/(d*(2+n))-a^2*Sin[c+d*x]^(3+n)/(d*(3+n))-4*a^2*Sin[c+d*x]^(4+n)/(d*(4+n))-a^2*Sin[c+d*x]^(5+n)/(d*(5+n))+2*a^2*Sin[c+d*x]^(6+n)/(d*(6+n))+a^2*Sin[c+d*x]^(7+n)/(d*(7+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:703
  public void test0259() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^9*(a+a*Sin[c+d*x]), x]", //
        "5/128*a*ArcTanh[Cos[c+d*x]]/d-1/7*a*Cot[c+d*x]^7/d+5/128*a*Cot[c+d*x]*Csc[c+d*x]/d-5/64*a*Cot[c+d*x]*Csc[c+d*x]^3/d+5/48*a*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/8*a*Cot[c+d*x]^5*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:719
  public void test0260() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^9*(a+a*Sin[c+d*x])^2, x]", //
        "45/128*a^2*ArcTanh[Cos[c+d*x]]/d-2/7*a^2*Cot[c+d*x]^7/d-35/128*a^2*Cot[c+d*x]*Csc[c+d*x]/d+5/24*a^2*Cot[c+d*x]^3*Csc[c+d*x]/d-1/6*a^2*Cot[c+d*x]^5*Csc[c+d*x]/d-5/64*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+5/48*a^2*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/8*a^2*Cot[c+d*x]^5*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:735
  public void test0261() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^8*(a+a*Sin[c+d*x])^3, x]", //
        "-3*a^3*x-15/16*a^3*ArcTanh[Cos[c+d*x]]/d+a^3*Cos[c+d*x]/d-3*a^3*Cot[c+d*x]/d+a^3*Cot[c+d*x]^3/d-3/5*a^3*Cot[c+d*x]^5/d-1/7*a^3*Cot[c+d*x]^7/d-15/16*a^3*Cot[c+d*x]*Csc[c+d*x]/d+11/8*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/2*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:753
  public void test0262() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "-x/a-3/8*ArcTanh[Cos[c+d*x]]/(a*d)-Cot[c+d*x]/(a*d)+1/3*Cot[c+d*x]^3/(a*d)+3/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)-1/4*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:770
  public void test0263() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "-x/a^3-7/2*ArcTanh[Cos[c+d*x]]/(a^3*d)+3*Cot[c+d*x]/(a^3*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:801
  public void test0264() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^7*(a+a*Sin[c+d*x]), x]", //
        "-3*a*Csc[c+d*x]/d-3/2*a*Csc[c+d*x]^2/d+a*Csc[c+d*x]^3/d+3/4*a*Csc[c+d*x]^4/d-1/5*a*Csc[c+d*x]^5/d-1/6*a*Csc[c+d*x]^6/d-a*Log[Sin[c+d*x]]/d-a*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:839
  public void test0265() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^n/(a+a*Sin[c+d*x]), x]", //
        "Sin[c+d*x]^(1+n)/(a*d*(1+n))-Sin[c+d*x]^(2+n)/(a*d*(2+n))-2*Sin[c+d*x]^(3+n)/(a*d*(3+n))+2*Sin[c+d*x]^(4+n)/(a*d*(4+n))+Sin[c+d*x]^(5+n)/(a*d*(5+n))-Sin[c+d*x]^(6+n)/(a*d*(6+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:865
  public void test0266() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^9/(a+a*Sin[c+d*x]), x]", //
        "5/128*ArcTanh[Cos[c+d*x]]/(a*d)+1/7*Cot[c+d*x]^7/(a*d)+5/128*Cot[c+d*x]*Csc[c+d*x]/(a*d)-5/64*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)+5/48*Cot[c+d*x]^3*Csc[c+d*x]^3/(a*d)-1/8*Cot[c+d*x]^5*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:881
  public void test0267() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^8/(a+a*Sin[c+d*x])^2, x]", //
        "1/8*ArcTanh[Cos[c+d*x]]/(a^2*d)-2/5*Cot[c+d*x]^5/(a^2*d)-1/7*Cot[c+d*x]^7/(a^2*d)+1/8*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-7/12*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)+1/3*Cot[c+d*x]*Csc[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:897
  public void test0268() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^9/(a+a*Sin[c+d*x])^3, x]", //
        "29/128*ArcTanh[Cos[c+d*x]]/(a^3*d)+4/3*Cot[c+d*x]^3/(a^3*d)+7/5*Cot[c+d*x]^5/(a^3*d)+3/7*Cot[c+d*x]^7/(a^3*d)+29/128*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)+29/192*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*d)-23/48*Cot[c+d*x]*Csc[c+d*x]^5/(a^3*d)-1/8*Cot[c+d*x]*Csc[c+d*x]^7/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:941
  public void test0269() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "2/3*Sec[c+d*x]^3/(a^2*d)-2/5*Sec[c+d*x]^5/(a^2*d)+1/3*Tan[c+d*x]^3/(a^2*d)+2/5*Tan[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1003
  public void test0270() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "a*x-a*Sec[c+d*x]/d+1/3*a*Sec[c+d*x]^3/d-a*Tan[c+d*x]/d+1/3*a*Tan[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1020
  public void test0271() {
    check( //
        "Integrate[Csc[c+d*x]*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "-a^3*ArcTanh[Cos[c+d*x]]/d+2/3*a^3*Cos[c+d*x]/(d*(1-Sin[c+d*x])^2)+5/3*a^3*Cos[c+d*x]/(d*(1-Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1038
  public void test0272() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-Sec[c+d*x]/(a^2*d)+4/3*Sec[c+d*x]^3/(a^2*d)-Sec[c+d*x]^5/(a^2*d)+2/7*Sec[c+d*x]^7/(a^2*d)-2/7*Tan[c+d*x]^7/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1169
  public void test0273() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^3*(c+d*Sin[e+f*x])^n, x]", //
        "-a^3*(c-d)^3*(c+d*Sin[e+f*x])^(1+n)/(d^4*f*(1+n))+3*a^3*(c-d)^2*(c+d*Sin[e+f*x])^(2+n)/(d^4*f*(2+n))-3*a^3*(c-d)*(c+d*Sin[e+f*x])^(3+n)/(d^4*f*(3+n))+a^3*(c+d*Sin[e+f*x])^(4+n)/(d^4*f*(4+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1231
  public void test0274() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "a*A*Sin[c+d*x]/d+1/2*a*(A+B)*Sin[c+d*x]^2/d+1/3*a*B*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1247
  public void test0275() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/3*(A-B)*(a+a*Sin[c+d*x])^3/(a*d)+1/4*B*(a+a*Sin[c+d*x])^4/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1263
  public void test0276() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "2/5*(A-B)*(a+a*Sin[c+d*x])^5/(a^2*d)-1/6*(A-3*B)*(a+a*Sin[c+d*x])^6/(a^3*d)-1/7*B*(a+a*Sin[c+d*x])^7/(a^4*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1281
  public void test0277() {
    check( //
        "Integrate[Cos[c+d*x]^5*(A+B*Sin[c+d*x])/(a+a*Sin[c+d*x]), x]", //
        "-2/3*(A+B)*(a-a*Sin[c+d*x])^3/(a^4*d)+1/4*(A+3*B)*(a-a*Sin[c+d*x])^4/(a^5*d)-1/5*B*(a-a*Sin[c+d*x])^5/(a^6*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1306
  public void test0278() {
    check( //
        "Integrate[Cos[e+f*x]^5*(a+a*Sin[e+f*x])^m*(A+B*Sin[e+f*x]), x]", //
        "4*(A-B)*(a+a*Sin[e+f*x])^(3+m)/(a^3*f*(3+m))-4*(A-2*B)*(a+a*Sin[e+f*x])^(4+m)/(a^4*f*(4+m))+(A-5*B)*(a+a*Sin[e+f*x])^(5+m)/(a^5*f*(5+m))+B*(a+a*Sin[e+f*x])^(6+m)/(a^6*f*(6+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1359
  public void test0279() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^6*(a+b*Sin[c+d*x]), x]", //
        "1/8*b*ArcTanh[Cos[c+d*x]]/d-1/3*a*Cot[c+d*x]^3/d-1/5*a*Cot[c+d*x]^5/d+1/8*b*Cot[c+d*x]*Csc[c+d*x]/d-1/4*b*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1375
  public void test0280() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^3, x]", //
        "-3*a*b^2*x+1/2*b*(3*a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/d+11/6*b^3*Cos[c+d*x]/d+1/3*a*(a^2-3*b^2)*Cot[c+d*x]/d-1/2*b*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^2/d-1/3*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1421
  public void test0281() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2*(a+b*Sin[c+d*x])^2, x]", //
        "1/128*(8*a^2+3*b^2)*x-6/35*a*b*Cos[c+d*x]/d+2/35*a*b*Cos[c+d*x]^3/d-1/128*(8*a^2+3*b^2)*Cos[c+d*x]*Sin[c+d*x]/d-1/1344*(40*a^4-140*a^2*b^2+21*b^4)*Cos[c+d*x]*Sin[c+d*x]^3/(b^2*d)-1/840*a*(20*a^2-69*b^2)*Cos[c+d*x]*Sin[c+d*x]^4/(b*d)-1/336*(20*a^2-63*b^2)*Cos[c+d*x]*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^2/(b^2*d)+5/56*a*Cos[c+d*x]*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^3/(b^2*d)-1/8*Cos[c+d*x]*Sin[c+d*x]^4*(a+b*Sin[c+d*x])^3/(b*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1438
  public void test0282() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+b*Sin[c+d*x])^3, x]", //
        "3*a*b^2*x-3/8*b*(3*a^2-4*b^2)*ArcTanh[Cos[c+d*x]]/d-1/40*b^3*(83*a^2+2*b^2)*Cos[c+d*x]/(a^2*d)-1/20*a*(4*a^2-29*b^2)*Cot[c+d*x]/d+27/40*b*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^2/d+2/5*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^3/d+1/20*b*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^4/(a^2*d)-1/5*Cot[c+d*x]*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1547
  public void test0283() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^10*(a+b*Sin[c+d*x]), x]", //
        "-1/6*b*Cot[c+d*x]^6/d-1/8*b*Cot[c+d*x]^8/d-1/5*a*Csc[c+d*x]^5/d+2/7*a*Csc[c+d*x]^7/d-1/9*a*Csc[c+d*x]^9/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1694
  public void test0284() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^7/(a+b*Sin[c+d*x]), x]", //
        "2*b*(a^2-b^2)^(5/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^7*d)+1/16*(5*a^6-30*a^4*b^2+40*a^2*b^4-16*b^6)*ArcTanh[Cos[c+d*x]]/(a^7*d)+1/15*b*(23*a^4-35*a^2*b^2+15*b^4)*Cot[c+d*x]/(a^6*d)-1/16*(11*a^4-18*a^2*b^2+8*b^4)*Cot[c+d*x]*Csc[c+d*x]/(a^5*d)-1/2*Cot[c+d*x]*Csc[c+d*x]^2/(b*d)+1/30*(15*a^4-22*a^2*b^2+10*b^4)*Cot[c+d*x]*Csc[c+d*x]^2/(a^4*b*d)+1/3*a*Cot[c+d*x]*Csc[c+d*x]^3/(b^2*d)-1/24*(8*a^4-13*a^2*b^2+6*b^4)*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*b^2*d)+1/5*b*Cot[c+d*x]*Csc[c+d*x]^4/(a^2*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1908
  public void test0285() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]*(a+b*Sin[c+d*x]), x]", //
        "-1/8*b*ArcTanh[Sin[c+d*x]]/d+1/4*a*Sec[c+d*x]^4/d-1/8*b*Sec[c+d*x]*Tan[c+d*x]/d+1/4*b*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1988
  public void test0286() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/3*(a^2-b^2)^2*(A*b-a*B)*(a+b*Sin[c+d*x])^3/(b^6*d)-1/4*(a^2-b^2)*(4*a*A*b-5*a^2*B+b^2*B)*(a+b*Sin[c+d*x])^4/(b^6*d)+2/5*(3*a^2*A*b-A*b^3-5*a^3*B+3*a*b^2*B)*(a+b*Sin[c+d*x])^5/(b^6*d)-1/3*(2*a*A*b-5*a^2*B+b^2*B)*(a+b*Sin[c+d*x])^6/(b^6*d)+1/7*(A*b-5*a*B)*(a+b*Sin[c+d*x])^7/(b^6*d)+1/8*B*(a+b*Sin[c+d*x])^8/(b^6*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:19
  public void test0287() {
    check( //
        "Integrate[Csc[e+f*x]^6*(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "1/8*a^2*c*ArcTanh[Cos[e+f*x]]/f-1/3*a^2*c*Cot[e+f*x]^3/f-1/5*a^2*c*Cot[e+f*x]^5/f+1/8*a^2*c*Cot[e+f*x]*Csc[e+f*x]/f-1/4*a^2*c*Cot[e+f*x]*Csc[e+f*x]^3/f");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:76
  public void test0288() {
    check( //
        "Integrate[Sqrt[g*Sin[e+f*x]]/((c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[g]/(Sqrt[2]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]*Sqrt[g]/((c-d)*f*Sqrt[a])-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[c]*Sqrt[g]/(Sqrt[c+d]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[c]*Sqrt[g]/((c-d)*f*Sqrt[a]*Sqrt[c+d])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:44
  public void test0289() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x]), x]", //
        "-a*(A+2*B)*x/c+a*B*Cos[e+f*x]/(c*f)+2*a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:60
  public void test0290() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^7, x]", //
        "1/13*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^9)+1/143*a^2*(4*A-9*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^8)+1/429*a^2*(4*A-9*B)*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^7)+2/3003*a^2*(4*A-9*B)*Cos[e+f*x]^5/(c*f*(c-c*Sin[e+f*x])^6)+2/15015*a^2*(4*A-9*B)*Cos[e+f*x]^5/(c^2*f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:78
  public void test0291() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^3/(a+a*Sin[e+f*x]), x]", //
        "-5/2*(3*A-4*B)*c^3*x/a-5/3*(3*A-4*B)*c^3*Cos[e+f*x]^3/(a*f)-5/2*(3*A-4*B)*c^3*Cos[e+f*x]*Sin[e+f*x]/(a*f)-a^3*(A-B)*c^3*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^4)-2*a^3*(3*A-4*B)*c^3*Cos[e+f*x]^5/(f*(a^2+a^2*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:94
  public void test0292() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^5), x]", //
        "1/9*(A+B)*Sec[e+f*x]^3/(a^2*c^2*f*(c-c*Sin[e+f*x])^3)+1/21*(2*A-B)*Sec[e+f*x]^3/(a^2*c^3*f*(c-c*Sin[e+f*x])^2)+1/21*(2*A-B)*Sec[e+f*x]^3/(a^2*f*(c^5-c^5*Sin[e+f*x]))+4/21*(2*A-B)*Tan[e+f*x]/(a^2*c^5*f)+4/63*(2*A-B)*Tan[e+f*x]^3/(a^2*c^5*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:114
  public void test0293() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "2*a*(A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[c])-2/3*a*(3*A+5*B)*Cos[e+f*x]/(f*Sqrt[c-c*Sin[e+f*x]])+2/3*a*B*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:130
  public void test0294() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2/63*a^3*(9*A+5*B)*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))-2/9*a^3*B*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:148
  public void test0295() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^2, x]", //
        "-8/3*(A-3*B)*c*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-1/3*(A-3*B)*Sec[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)-1/3*(A-B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(9/2)/(a^2*c^2*f)+32/3*(A-3*B)*c^2*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:168
  public void test0296() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/2*a*(A+B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])+1/3*a*B*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:184
  public void test0297() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/42*a*(7*A-B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/7*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/105*a^3*(7*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])-2/105*a^2*(7*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:200
  public void test0298() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-a^2*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])-1/3*a*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*Sqrt[c-c*Sin[e+f*x]])-1/4*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])-8*a^4*(A+B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-4*a^3*(A+B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:218
  public void test0299() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(A-2*B)*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])-4*(A-2*B)*c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*(A-2*B)*c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:278
  public void test0300() {
    check( //
        "Integrate[Csc[c+d*x]^4*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "-a^3*A*x+a^3*A*ArcTanh[Cos[c+d*x]]/d-a^3*A*Cot[c+d*x]/d-1/3*a^3*A*Cot[c+d*x]^3/d-a^3*A*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:355
  public void test0301() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x]), x]", //
        "-2/5*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-8/15*a^2*(5*A+3*B)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/15*a*(5*A+3*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:14
  public void test0302() {
    check( //
        "Integrate[1/(a*Sin[x]^2)^(3/2), x]", //
        "-1/2*Cot[x]/(a*Sqrt[a*Sin[x]^2])-1/2*ArcTanh[Cos[x]]*Sin[x]/(a*Sqrt[a*Sin[x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:59
  public void test0303() {
    check( //
        "Integrate[(a-a*Sin[x]^2)^2, x]", //
        "3/8*a^2*x+3/8*a^2*Cos[x]*Sin[x]+1/4*a^2*Cos[x]^3*Sin[x]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:115
  public void test0304() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a+b*Sin[c+d*x]^2), x]", //
        "-Cos[c+d*x]/(b*d)+a*ArcTanh[Cos[c+d*x]*Sqrt[b]/Sqrt[a+b]]/(b^(3/2)*d*Sqrt[a+b])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:355
  public void test0305() {
    check( //
        "Integrate[Cos[x]^7/(a-a*Sin[x]^2)^2, x]", //
        "Sin[x]/a^2-1/3*Sin[x]^3/a^2");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:393
  public void test0306() {
    check( //
        "Integrate[Cos[x]^2/(a+b*Sin[x]^2), x]", //
        "-x/b+ArcTan[Sqrt[a+b]*Tan[x]/Sqrt[a]]*Sqrt[a+b]/(b*Sqrt[a])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:722
  public void test0307() {
    check( //
        "Integrate[Tan[c+d*x]/Sqrt[a+b*Sin[c+d*x]^4], x]", //
        "1/2*ArcTanh[(a+b*Sin[c+d*x]^2)/(Sqrt[a+b]*Sqrt[a+b*Sin[c+d*x]^4])]/(d*Sqrt[a+b])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:59
  public void test0308() {
    check( //
        "Integrate[(a*Cos[x]^2)^(3/2), x]", //
        "1/3*(a*Cos[x]^2)^(3/2)*Tan[x]+2/3*a*Sqrt[a*Cos[x]^2]*Tan[x]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:75
  public void test0309() {
    check( //
        "Integrate[1/(a*Cos[x]^4)^(5/2), x]", //
        "Cos[x]*Sin[x]/(a^2*Sqrt[a*Cos[x]^4])+4/3*Sin[x]^2*Tan[x]/(a^2*Sqrt[a*Cos[x]^4])+6/5*Sin[x]^2*Tan[x]^3/(a^2*Sqrt[a*Cos[x]^4])+4/7*Sin[x]^2*Tan[x]^5/(a^2*Sqrt[a*Cos[x]^4])+1/9*Sin[x]^2*Tan[x]^7/(a^2*Sqrt[a*Cos[x]^4])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:113
  public void test0310() {
    check( //
        "Integrate[Cos[c+d*x]^2*(b*Cos[c+d*x])^(3/2), x]", //
        "2/7*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b*d)+10/21*b^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*b*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:129
  public void test0311() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*Sec[c+d*x]^3, x]", //
        "2*b^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:147
  public void test0312() {
    check( //
        "Integrate[Sec[c+d*x]^3/Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+6/5*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:163
  public void test0313() {
    check( //
        "Integrate[Cos[c+d*x]^7/(b*Cos[c+d*x])^(5/2), x]", //
        "14/45*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^4*d)+2/9*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b^6*d)+14/15*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:183
  public void test0314() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(1/2), x]", //
        "x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:199
  public void test0315() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(b*Cos[c+d*x])^(5/2), x]", //
        "b^2*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-2/3*b^2*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/5*b^2*Sin[c+d*x]^5*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:217
  public void test0316() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)/(b*Cos[c+d*x])^(1/2), x]", //
        "x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:233
  public void test0317() {
    check( //
        "Integrate[Cos[c+d*x]^(13/2)/(b*Cos[c+d*x])^(5/2), x]", //
        "3/8*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])+1/4*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])+3/8*x*Sqrt[Cos[c+d*x]]/(b^2*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.1.1 (a+b cos)^n.input:16
  public void test0318() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x])^(1/2), x]", //
        "ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.2.1.2 (g sin)^p (a+b cos)^m.input:49
  public void test0319() {
    check( //
        "Integrate[Sin[x]^2/(a+b*Cos[x]), x]", //
        "a*x/b^2-Sin[x]/b-2*ArcTan[Sqrt[a-b]*Tan[1/2*x]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/b^2");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:10
  public void test0320() {
    check( //
        "Integrate[Tan[x]^4/(a+a*Cos[x]), x]", //
        "1/2*ArcTanh[Sin[x]]/a-1/2*Sec[x]*Tan[x]/a+1/3*Tan[x]^3/a");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:46
  public void test0321() {
    check( //
        "Integrate[Cot[x]^4/(a+b*Cos[x]), x]", //
        "2*a^4*ArcTan[Sqrt[a-b]*Tan[1/2*x]/Sqrt[a+b]]/((a-b)^(5/2)*(a+b)^(5/2))+a^3*Cot[x]/(a^2-b^2)^2-1/3*a*Cot[x]^3/(a^2-b^2)-a^2*b*Csc[x]/(a^2-b^2)^2-b*Csc[x]/(a^2-b^2)+1/3*b*Csc[x]^3/(a^2-b^2)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:24
  public void test0322() {
    check( //
        "Integrate[Cos[a+b*x]^2/(c+d*x), x]", //
        "1/2*CosIntegral[2*b*c/d+2*b*x]*Cos[2*a-2*b*c/d]/d+1/2*Log[c+d*x]/d-1/2*SinIntegral[2*b*c/d+2*b*x]*Sin[2*a-2*b*c/d]/d");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:42
  public void test0323() {
    check( //
        "Integrate[(c+d*x)^3*Sec[a+b*x], x]", //
        "-2*I*(c+d*x)^3*ArcTan[E^(I*(a+b*x))]/b+3*I*d*(c+d*x)^2*PolyLog[2,-I*E^(I*(a+b*x))]/b^2-3*I*d*(c+d*x)^2*PolyLog[2,I*E^(I*(a+b*x))]/b^2-6*d^2*(c+d*x)*PolyLog[3,-I*E^(I*(a+b*x))]/b^3+6*d^2*(c+d*x)*PolyLog[3,I*E^(I*(a+b*x))]/b^3-6*I*d^3*PolyLog[4,-I*E^(I*(a+b*x))]/b^4+6*I*d^3*PolyLog[4,I*E^(I*(a+b*x))]/b^4");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:81
  public void test0324() {
    check( //
        "Integrate[x^(1/2)*Cos[x], x]", //
        "-FresnelS[Sqrt[2/Pi]*Sqrt[x]]*Sqrt[1/2*Pi]+Sin[x]*Sqrt[x]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:139
  public void test0325() {
    check( //
        "Integrate[x^(-1+m)*Cos[a+b*x], x]", //
        "-1/2*E^(I*a)*x^m*Gamma[m,-I*b*x]/(-I*b*x)^m-1/2*x^m*Gamma[m,I*b*x]/(E^(I*a)*(I*b*x)^m)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:163
  public void test0326() {
    check( //
        "Integrate[(c+d*x)^2*(a+a*Cos[e+f*x])^2, x]", //
        "-1/4*a^2*d^2*x/f^2+1/2*a^2*(c+d*x)^3/d+4*a^2*d*(c+d*x)*Cos[e+f*x]/f^2+1/2*a^2*d*(c+d*x)*Cos[e+f*x]^2/f^2-4*a^2*d^2*Sin[e+f*x]/f^3+2*a^2*(c+d*x)^2*Sin[e+f*x]/f-1/4*a^2*d^2*Cos[e+f*x]*Sin[e+f*x]/f^3+1/2*a^2*(c+d*x)^2*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:207
  public void test0327() {
    check( //
        "Integrate[Sqrt[a-a*Cos[x]]/x^2, x]", //
        "-Sqrt[a-a*Cos[x]]/x+1/2*CosIntegral[1/2*x]*Csc[1/2*x]*Sqrt[a-a*Cos[x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:227
  public void test0328() {
    check( //
        "Integrate[x^3/(a+a*Cos[x])^(3/2), x]", //
        "-3*x^2/(a*Sqrt[a+a*Cos[x]])-24*I*x*ArcTan[E^(1/2*I*x)]*Cos[1/2*x]/(a*Sqrt[a+a*Cos[x]])-I*x^3*ArcTan[E^(1/2*I*x)]*Cos[1/2*x]/(a*Sqrt[a+a*Cos[x]])+24*I*Cos[1/2*x]*PolyLog[2,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+3*I*x^2*Cos[1/2*x]*PolyLog[2,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-24*I*Cos[1/2*x]*PolyLog[2,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-3*I*x^2*Cos[1/2*x]*PolyLog[2,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-12*x*Cos[1/2*x]*PolyLog[3,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+12*x*Cos[1/2*x]*PolyLog[3,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-24*I*Cos[1/2*x]*PolyLog[4,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+24*I*Cos[1/2*x]*PolyLog[4,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+1/2*x^3*Tan[1/2*x]/(a*Sqrt[a+a*Cos[x]])");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:19
  public void test0329() {
    check( //
        "Integrate[x*Cos[a+b*x^2]^2, x]", //
        "1/4*x^2+1/4*Cos[a+b*x^2]*Sin[a+b*x^2]/b");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:39
  public void test0330() {
    check( //
        "Integrate[Cos[a+b*x^2]/x^(1/2), x]", //
        "-1/4*E^(I*a)*Gamma[1/4,-I*b*x^2]*Sqrt[x]/(-I*b*x^2)^(1/4)-1/4*Gamma[1/4,I*b*x^2]*Sqrt[x]/(E^(I*a)*(I*b*x^2)^(1/4))");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:65
  public void test0331() {
    check( //
        "Integrate[Cos[a+b/x^2]/x^2, x]", //
        "-Cos[a]*FresnelC[Sqrt[2/Pi]*Sqrt[b]/x]*Sqrt[1/2*Pi]/Sqrt[b]+FresnelS[Sqrt[2/Pi]*Sqrt[b]/x]*Sin[a]*Sqrt[1/2*Pi]/Sqrt[b]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:85
  public void test0332() {
    check( //
        "Integrate[Cos[a+b*x^(1/3)]^2/x^(3/2), x]", //
        "8*b*Cos[a+b*x^(1/3)]*Sin[a+b*x^(1/3)]/x^(1/6)-8*b^(3/2)*Cos[2*a]*FresnelC[2*x^(1/6)*Sqrt[b]/Sqrt[Pi]]*Sqrt[Pi]+8*b^(3/2)*FresnelS[2*x^(1/6)*Sqrt[b]/Sqrt[Pi]]*Sin[2*a]*Sqrt[Pi]-2*Cos[a+b*x^(1/3)]^2/Sqrt[x]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:109
  public void test0333() {
    check( //
        "Integrate[x^(-1-n)*Cos[a+b*x^n]^2, x]", //
        "(-1/2)/(n*x^n)-1/2*Cos[2*(a+b*x^n)]/(n*x^n)-b*Cos[2*a]*SinIntegral[2*b*x^n]/n-b*CosIntegral[2*b*x^n]*Sin[2*a]/n");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:135
  public void test0334() {
    check( //
        "Integrate[Cos[a+b*(c+d*x)^(1/3)]/x, x]", //
        "CosIntegral[b*c^(1/3)-b*(c+d*x)^(1/3)]*Cos[a+b*c^(1/3)]+CosIntegral[(-1)^(1/3)*b*c^(1/3)+b*(c+d*x)^(1/3)]*Cos[a-(-1)^(1/3)*b*c^(1/3)]+CosIntegral[(-1)^(2/3)*b*c^(1/3)-b*(c+d*x)^(1/3)]*Cos[a+(-1)^(2/3)*b*c^(1/3)]+SinIntegral[b*c^(1/3)-b*(c+d*x)^(1/3)]*Sin[a+b*c^(1/3)]-SinIntegral[(-1)^(1/3)*b*c^(1/3)+b*(c+d*x)^(1/3)]*Sin[a-(-1)^(1/3)*b*c^(1/3)]+SinIntegral[(-1)^(2/3)*b*c^(1/3)-b*(c+d*x)^(1/3)]*Sin[a+(-1)^(2/3)*b*c^(1/3)]");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:35
  public void test0335() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^3, x]", //
        "13/8*a^3*x+4*a^3*Sin[c+d*x]/d+13/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d+3/4*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d-5/3*a^3*Sin[c+d*x]^3/d+1/5*a^3*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:53
  public void test0336() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*Sec[c+d*x]^7, x]", //
        "49/16*a^4*ArcTanh[Sin[c+d*x]]/d+8*a^4*Tan[c+d*x]/d+49/16*a^4*Sec[c+d*x]*Tan[c+d*x]/d+41/24*a^4*Sec[c+d*x]^3*Tan[c+d*x]/d+1/6*a^4*Sec[c+d*x]^5*Tan[c+d*x]/d+4*a^4*Tan[c+d*x]^3/d+4/5*a^4*Tan[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:71
  public void test0337() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x])^2, x]", //
        "1/3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)+1/3*Sin[c+d*x]/(d*(a^2+a^2*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:87
  public void test0338() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Cos[c+d*x])^4, x]", //
        "x/a^4+11/21*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-43/21*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/7*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:103
  public void test0339() {
    check( //
        "Integrate[Sec[c+d*x]/(a+a*Cos[c+d*x])^5, x]", //
        "ArcTanh[Sin[c+d*x]]/(a^5*d)-1/9*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^5)-13/63*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^4)-34/105*Sin[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^3)-173/315*Sin[c+d*x]/(a^3*d*(a+a*Cos[c+d*x])^2)-488/315*Sin[c+d*x]/(d*(a^5+a^5*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:123
  public void test0340() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^(3/2), x]", //
        "2/5*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+8/5*a^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/5*a*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:141
  public void test0341() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Cos[c+d*x])^(1/2), x]", //
        "ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-148/105*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-2/35*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/7*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+62/105*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:157
  public void test0342() {
    check( //
        "Integrate[Sec[c+d*x]^3/(a+a*Cos[c+d*x])^(3/2), x]", //
        "19/4*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)-13/2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))-7/4*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+Sec[c+d*x]*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:177
  public void test0343() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2, x]", //
        "12/5*a^2*EllipticE[1/2*(c+d*x),2]/d+8/7*a^2*EllipticF[1/2*(c+d*x),2]/d+4/5*a^2*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*a^2*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+8/7*a^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:193
  public void test0344() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4/Cos[c+d*x]^(3/2), x]", //
        "56/5*a^4*EllipticE[1/2*(c+d*x),2]/d+32/3*a^4*EllipticF[1/2*(c+d*x),2]/d+2/5*a^4*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2*a^4*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])+8/3*a^4*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:211
  public void test0345() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^2), x]", //
        "EllipticE[1/2*(c+d*x),2]/(a^2*d)+2/3*EllipticF[1/2*(c+d*x),2]/(a^2*d)-Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))-1/3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:231
  public void test0346() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(3/2), x]", //
        "2*a*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:247
  public void test0347() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)/Cos[c+d*x]^(7/2), x]", //
        "22/15*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+86/15*a^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/5*a^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(5/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:265
  public void test0348() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(1/2)*(1+Cos[c+d*x])^(1/2)), x]", //
        "ArcSin[Sin[c+d*x]/(1+Cos[c+d*x])]*Sqrt[2]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:281
  public void test0349() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "-1/4*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2))-17/16*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2))+163/16*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+95/48*Sin[c+d*x]/(a^2*d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-299/48*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:301
  public void test0350() {
    check( //
        "Integrate[(a-a*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(3/2), x]", //
        "2*a*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:470
  public void test0351() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Cos[c+d*x]), x]", //
        "5/16*b*x+a*Sin[c+d*x]/d+5/16*b*Cos[c+d*x]*Sin[c+d*x]/d+5/24*b*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*b*Cos[c+d*x]^5*Sin[c+d*x]/d-2/3*a*Sin[c+d*x]^3/d+1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:504
  public void test0352() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Cos[c+d*x])^4, x]", //
        "1/16*(8*a^4+36*a^2*b^2+5*b^4)*x-1/60*a*(4*a^4-121*a^2*b^2-128*b^4)*Sin[c+d*x]/(b*d)-1/240*(8*a^4-178*a^2*b^2-75*b^4)*Cos[c+d*x]*Sin[c+d*x]/d-1/120*a*(4*a^2-53*b^2)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(b*d)-1/120*(4*a^2-25*b^2)*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/(b*d)-1/30*a*(a+b*Cos[c+d*x])^4*Sin[c+d*x]/(b*d)+1/6*(a+b*Cos[c+d*x])^5*Sin[c+d*x]/(b*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:522
  public void test0353() {
    check( //
        "Integrate[Sec[c+d*x]/(a+b*Cos[c+d*x]), x]", //
        "ArcTanh[Sin[c+d*x]]/(a*d)-2*b*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:559
  public void test0354() {
    check( //
        "Integrate[Cos[c+d*x]*Sqrt[a+b*Cos[c+d*x]], x]", //
        "2/3*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+2/3*a*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/3*(a^2-b^2)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:588
  public void test0355() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sqrt[3-4*Cos[c+d*x]], x]", //
        "-1/10*(3-4*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+21/20*EllipticE[1/2*(Pi+c+d*x),8/7]*Sqrt[7]/d-1/20*EllipticF[1/2*(Pi+c+d*x),8/7]*Sqrt[7]/d+1/5*Sin[c+d*x]*Sqrt[3-4*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:615
  public void test0356() {
    check( //
        "Integrate[Cos[c+d*x]/(a+b*Cos[c+d*x])^(5/2), x]", //
        "2/3*a*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^(3/2))+2/3*(a^2+3*b^2)*Sin[c+d*x]/((a^2-b^2)^2*d*Sqrt[a+b*Cos[c+d*x]])-2/3*(a^2+3*b^2)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/3*a*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:643
  public void test0357() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/Cos[c+d*x]^(5/2), x]", //
        "-2*B*EllipticE[1/2*(c+d*x),2]/d+2/3*A*EllipticF[1/2*(c+d*x),2]/d+2/3*A*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2*B*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:915
  public void test0358() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(1/2), x]", //
        "2*B*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:949
  public void test0359() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x], x]", //
        "2/5*b*B*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/3*A*b^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*A*b^2*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+6/5*b^2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:967
  public void test0360() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(3/2), x]", //
        "2*A*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])+2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:987
  public void test0361() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(3/2), x]", //
        "B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*ArcTanh[Sin[c+d*x]]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1003
  public void test0362() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "b^2*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*b^2*ArcTanh[Sin[c+d*x]]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:18
  public void test0363() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "1/2*a*(A+2*B)*ArcTanh[Sin[c+d*x]]/d+a*(A+B)*Tan[c+d*x]/d+1/2*a*A*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:34
  public void test0364() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "1/2*a^3*(6*A+7*B)*x+a^3*(3*A+B)*ArcTanh[Sin[c+d*x]]/d+5/2*a^3*B*Sin[c+d*x]/d-1/2*(2*A-B)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+a*A*(a+a*Cos[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:52
  public void test0365() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x]), x]", //
        "3/2*(A-B)*x/a-(3*A-4*B)*Sin[c+d*x]/(a*d)+3/2*(A-B)*Cos[c+d*x]*Sin[c+d*x]/(a*d)+(A-B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+1/3*(3*A-4*B)*Sin[c+d*x]^3/(a*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:69
  public void test0366() {
    check( //
        "Integrate[Cos[c+d*x]^5*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^3, x]", //
        "1/2*(13*A-23*B)*x/a^3-4/5*(19*A-34*B)*Sin[c+d*x]/(a^3*d)+1/2*(13*A-23*B)*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+1/5*(A-B)*Cos[c+d*x]^5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(8*A-13*B)*Cos[c+d*x]^4*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/3*(13*A-23*B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))+4/15*(19*A-34*B)*Sin[c+d*x]^3/(a^3*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:85
  public void test0367() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^4, x]", //
        "-(4*A-B)*ArcTanh[Sin[c+d*x]]/(a^4*d)+8/105*(83*A-20*B)*Tan[c+d*x]/(a^4*d)-1/105*(88*A-25*B)*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-(4*A-B)*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/35*(12*A-5*B)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:105
  public void test0368() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "1/4*a^(3/2)*(7*A+12*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/4*a^2*(5*A+4*B)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*a*A*Sec[c+d*x]*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:123
  public void test0369() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+a*Cos[c+d*x])^(1/2), x]", //
        "2*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-(A-B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:139
  public void test0370() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+a*Cos[c+d*x])^(5/2), x]", //
        "2*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/4*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(11*A-3*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(43*A-3*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:159
  public void test0371() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x])/Cos[c+d*x]^(9/2), x]", //
        "-4/5*a^2*(3*A+4*B)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(6*A+7*B)*EllipticF[1/2*(c+d*x),2]/d+2/35*a^2*(9*A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+4/21*a^2*(6*A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/7*A*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+4/5*a^2*(3*A+4*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:177
  public void test0372() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^2, x]", //
        "(4*A-7*B)*EllipticE[1/2*(c+d*x),2]/(a^2*d)-5/3*(A-2*B)*EllipticF[1/2*(c+d*x),2]/(a^2*d)+1/3*(4*A-7*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))+1/3*(A-B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)-5/3*(A-2*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:197
  public void test0373() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]), x]", //
        "1/4*(4*A+3*B)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/2*a*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*a*(4*A+3*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:213
  public void test0374() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(1/2), x]", //
        "1/8*a^(5/2)*(38*A+25*B)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/3*a*B*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+1/24*a^3*(54*A+49*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*a^2*(2*A+3*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:231
  public void test0375() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "-1/2*(7*A-3*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2)*Sqrt[Cos[c+d*x]])+1/2*(5*A-B)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:253
  public void test0376() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "1/2*(A*b+a*B)*x+1/3*(3*a*A+2*b*B)*Sin[c+d*x]/d+1/2*(A*b+a*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*b*B*Cos[c+d*x]^2*Sin[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:269
  public void test0377() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x]), x]", //
        "1/8*(12*a^2*A*b+3*A*b^3+4*a^3*B+9*a*b^2*B)*x+1/30*(15*a^3*A*b+60*a*A*b^3-3*a^4*B+52*a^2*b^2*B+16*b^4*B)*Sin[c+d*x]/(b*d)+1/120*(30*a^2*A*b+45*A*b^3-6*a^3*B+71*a*b^2*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/60*(15*a*A*b-3*a^2*B+16*b^2*B)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(b*d)+1/20*(5*A*b-a*B)*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/(b*d)+1/5*B*(a+b*Cos[c+d*x])^4*Sin[c+d*x]/(b*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:285
  public void test0378() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^6, x]", //
        "1/8*(12*a^3*A*b+16*a*A*b^3+3*a^4*B+24*a^2*b^2*B+8*b^4*B)*ArcTanh[Sin[c+d*x]]/d+1/15*(8*a^4*A+60*a^2*A*b^2+15*A*b^4+40*a^3*b*B+60*a*b^3*B)*Tan[c+d*x]/d+1/40*a*(60*a^2*A*b+56*A*b^3+15*a^3*B+110*a*b^2*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/30*a^2*(8*a^2*A+18*A*b^2+25*a*b*B)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/20*a*(8*A*b+5*a*B)*(a+b*Cos[c+d*x])^2*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*a*A*(a+b*Cos[c+d*x])^3*Sec[c+d*x]^4*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:401
  public void test0379() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(a+b*Cos[c+d*x])^2*(A+B*Cos[c+d*x]), x]", //
        "2/15*(9*a^2*A+7*A*b^2+14*a*b*B)*EllipticE[1/2*(c+d*x),2]/d+10/231*(9*b^2*B+11*a*(2*A*b+a*B))*EllipticF[1/2*(c+d*x),2]/d+2/45*(9*a^2*A+7*A*b^2+14*a*b*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/77*(9*b^2*B+11*a*(2*A*b+a*B))*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/99*b*(11*A*b+13*a*B)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/d+2/11*b*B*Cos[c+d*x]^(7/2)*(a+b*Cos[c+d*x])*Sin[c+d*x]/d+10/231*(9*b^2*B+11*a*(2*A*b+a*B))*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:437
  public void test0380() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])/(Cos[c+d*x]^(1/2)*(a+b*Cos[c+d*x])), x]", //
        "2*B*EllipticF[1/2*(c+d*x),2]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:671
  public void test0381() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])/((a+b*Cos[c+d*x])*Sec[c+d*x]^(3/2)), x]", //
        "2/3*B*Sin[c+d*x]/(d*Sqrt[Sec[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:19
  public void test0382() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+C*Cos[c+d*x]^2), x]", //
        "1/16*(6*A+5*C)*x+1/16*(6*A+5*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(6*A+5*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*C*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:39
  public void test0383() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*(b*Sec[c+d*x])^(9/2), x]", //
        "2/21*b^3*(5*A+7*C)*(b*Sec[c+d*x])^(3/2)*Sin[c+d*x]/d+2/21*b^4*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]]/d+2/7*A*b^2*(b*Sec[c+d*x])^(5/2)*Tan[c+d*x]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:64
  public void test0384() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4*Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*A*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/5*b*(3*A+5*C)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2/5*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:80
  public void test0385() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "2/5*A*b^5*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/5*b^3*(3*A+5*C)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2/5*b^2*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:98
  public void test0386() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "2*A*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])-2*(A-C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:118
  public void test0387() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]], x]", //
        "(A+C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*C*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:134
  public void test0388() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "b^2*(A+C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*b^2*(A+2*C)*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/5*b^2*C*Sin[c+d*x]^5*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:361
  public void test0389() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "2/7*A*b^5*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/5*b^4*B*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/21*b^3*(5*A+7*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+6/5*b^2*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/21*b^2*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-6/5*b*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:379
  public void test0390() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/Sqrt[b*Cos[c+d*x]], x]", //
        "2/7*A*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/5*b^2*B*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/21*b*(5*A+7*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+6/5*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/21*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-6/5*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:395
  public void test0391() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(b*Cos[c+d*x])^(5/2), x]", //
        "2/7*A*b*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/5*B*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/21*(5*A+7*C)*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(3/2))+6/5*B*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/21*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])-6/5*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:433
  public void test0392() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]), x]", //
        "B*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]+A*ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:449
  public void test0393() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "B*x*Sqrt[Cos[c+d*x]]/(b^2*Sqrt[b*Cos[c+d*x]])+A*ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:23
  public void test0394() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "a^2*(2*A+C)*x+a^2*A*ArcTanh[Sin[c+d*x]]/d+a^2*(A+C)*Sin[c+d*x]/d+1/3*C*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/3*C*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:39
  public void test0395() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^4*(A+C*Cos[c+d*x]^2), x]", //
        "1/128*a^4*(392*A+323*C)*x+4/35*a^4*(63*A+52*C)*Sin[c+d*x]/d+1/128*a^4*(392*A+323*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/2240*a^4*(2408*A+2007*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/14*a*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d+1/8*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^4*Sin[c+d*x]/d+1/336*(56*A+61*C)*Cos[c+d*x]^3*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d+7/120*(8*A+7*C)*Cos[c+d*x]^3*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d-4/105*a^4*(63*A+52*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:57
  public void test0396() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x]), x]", //
        "-A*ArcTanh[Sin[c+d*x]]/(a*d)+(2*A+C)*Tan[c+d*x]/(a*d)-(A+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:73
  public void test0397() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "1/5*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+2/15*(A-4*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/15*(2*A+7*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:93
  public void test0398() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]], x]", //
        "2/35*C*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+2/105*a*(35*A+27*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*(35*A+18*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/7*C*Cos[c+d*x]^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:109
  public void test0399() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2), x]", //
        "2/15015*a*(10439*A+8368*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+10/143*a*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/13*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/6435*a^3*(10439*A+8368*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/9009*a^3*(2717*A+2224*C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-4/45045*a^2*(10439*A+8368*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/1287*a^2*(143*A+136*C)*Cos[c+d*x]^3*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:127
  public void test0400() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/4*(7*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-(A+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-1/4*A*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*A*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:143
  public void test0401() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-5*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)+1/16*(115*A+3*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(15*A-C)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+1/16*(35*A+3*C)*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:163
  public void test0402() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(9/2), x]", //
        "-4/5*a^2*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]/d+8/21*a^2*(3*A+7*C)*EllipticF[1/2*(c+d*x),2]/d+2/105*a^2*(33*A+35*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/7*A*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+8/35*A*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+4/5*a^2*(3*A+5*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:181
  public void test0403() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])), x]", //
        "(3*A+C)*EllipticE[1/2*(c+d*x),2]/(a*d)+1/3*(5*A+3*C)*EllipticF[1/2*(c+d*x),2]/(a*d)+1/3*(5*A+3*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))-(A+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x]))-(3*A+C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:201
  public void test0404() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/8*(8*A+5*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/12*a*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/8*a*(8*A+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:217
  public void test0405() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2), x]", //
        "1/512*a^(5/2)*(1304*A+1015*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/12*a*C*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+1/6*C*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+1/768*a^3*(1304*A+1015*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/192*a^3*(136*A+109*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/512*a^3*(1304*A+1015*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/96*a^2*(24*A+23*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:235
  public void test0406() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(9/2)*Sqrt[a+a*Cos[c+d*x]]), x]", //
        "(A+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/7*A*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])-2/35*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*(31*A+35*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-2/105*(43*A+35*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:257
  public void test0407() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "B*x+C*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:273
  public void test0408() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/8*a^2*(8*B+7*C)*x+1/6*a^2*(8*B+7*C)*Sin[c+d*x]/d+1/24*a^2*(8*B+7*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*(4*B-C)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:289
  public void test0409() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^7, x]", //
        "1/8*a^3*(13*B+15*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^3*(38*B+45*C)*Tan[c+d*x]/d+1/8*a^3*(13*B+15*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/60*a^3*(43*B+45*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/20*(7*B+5*C)*(a^3+a^3*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*a*B*(a+a*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:307
  public void test0410() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(a+a*Cos[c+d*x])^2, x]", //
        "1/2*(7*B-4*C)*ArcTanh[Sin[c+d*x]]/(a^2*d)-2/3*(8*B-5*C)*Tan[c+d*x]/(a^2*d)+1/2*(7*B-4*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d)-1/3*(8*B-5*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(B-C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:333
  public void test0411() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "6/5*C*EllipticE[1/2*(c+d*x),2]/d+2/3*B*EllipticF[1/2*(c+d*x),2]/d+2/5*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:359
  public void test0412() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "3/8*B*ArcTanh[Sin[c+d*x]]/d+1/5*(4*A+5*C)*Tan[c+d*x]/d+3/8*B*Sec[c+d*x]*Tan[c+d*x]/d+1/4*B*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*A*Sec[c+d*x]^4*Tan[c+d*x]/d+1/15*(4*A+5*C)*Tan[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:375
  public void test0413() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "1/8*a^2*(7*A+8*B+12*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(4*A+5*B+6*C)*Tan[c+d*x]/d+1/24*a^2*(11*A+16*B+12*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/6*(A+2*B)*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*A*(a+a*Cos[c+d*x])^2*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:391
  public void test0414() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "1/8*a^4*(52*A+48*B+35*C)*x+a^4*(4*A+B)*ArcTanh[Sin[c+d*x]]/d+5/8*a^4*(4*A+8*B+7*C)*Sin[c+d*x]/d-1/4*a*(4*A-C)*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d-1/12*(12*A-4*B-7*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d-1/24*(12*A-32*B-35*C)*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d+A*(a+a*Cos[c+d*x])^4*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:409
  public void test0415() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^2, x]", //
        "1/2*(2*A-4*B+7*C)*x/a^2-2/3*(2*A-5*B+8*C)*Sin[c+d*x]/(a^2*d)+1/2*(2*A-4*B+7*C)*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/3*(2*A-5*B+8*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B+C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:425
  public void test0416() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^4, x]", //
        "1/2*(2*A-8*B+21*C)*x/a^4-8/105*(20*A-83*B+216*C)*Sin[c+d*x]/(a^4*d)+1/2*(2*A-8*B+21*C)*Cos[c+d*x]*Sin[c+d*x]/(a^4*d)-1/105*(10*A-52*B+129*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-4/105*(20*A-83*B+216*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B+C)*Cos[c+d*x]^5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)+1/5*(B-2*C)*Cos[c+d*x]^4*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:445
  public void test0417() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "1/8*(5*A+6*B+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/8*a*(5*A+6*B+8*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/12*a*(A+6*B)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*A*Sec[c+d*x]^2*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:461
  public void test0418() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/4*a^(5/2)*(19*A+20*B+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/12*a^3*(27*A-12*B-56*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/12*a^2*(21*A+12*B-8*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+1/4*a*(5*A+4*B)*(a+a*Cos[c+d*x])^(3/2)*Tan[c+d*x]/d+1/2*A*(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:479
  public void test0419() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/2*(A-B+C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*(3*A-7*B+11*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/3*(3*A-9*B+13*C)*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+1/6*(3*A-3*B+7*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:499
  public void test0420() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "-2*(A-C)*EllipticE[1/2*(c+d*x),2]/d+2*B*EllipticF[1/2*(c+d*x),2]/d+2*A*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:515
  public void test0421() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(7/2), x]", //
        "-4/5*a^2*(4*A+5*B)*EllipticE[1/2*(c+d*x),2]/d+4/3*a^2*(A+2*B+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/5*A*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/15*(4*A+5*B)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/15*a^2*(17*A+25*B+15*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:553
  public void test0422() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/64*(48*A+40*B+35*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/96*a*(48*A+40*B+35*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/24*a*(8*B+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/64*a*(48*A+40*B+35*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:569
  public void test0423() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(13/2), x]", //
        "2/11*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(11/2))+2/693*a^2*(84*A+110*B+99*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])+2/1155*a^2*(336*A+374*B+429*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+8/3465*a^2*(336*A+374*B+429*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+16/3465*a^2*(336*A+374*B+429*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/99*a*(3*A+11*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(9/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:661
  public void test0424() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^2, x]", //
        "-2*a*C*x/b^3-2*(A*b^4-2*a^4*C+3*a^2*b^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^3*(a+b)^(3/2)*d)+C*Sin[c+d*x]/(b^2*d)+a*(A*b^2+a^2*C)*Sin[c+d*x]/(b^2*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:693
  public void test0425() {
    check( //
        "Integrate[Cos[c+d*x]*(1-Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^2, x]", //
        "2*a*x/b^3-Sin[c+d*x]/(b^2*d)-a*Sin[c+d*x]/(b^2*d*(a+b*Cos[c+d*x]))-2*(2*a^2-b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b^3*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:709
  public void test0426() {
    check( //
        "Integrate[(a^2-b^2*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^2, x]", //
        "-x+4*a*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:745
  public void test0427() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)/Sqrt[a+b*Cos[c+d*x]], x]", //
        "-8/15*a*C*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d)+2/5*C*Cos[c+d*x]*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)+2/15*(8*a^2*C+3*b^2*(5*A+3*C))*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^3*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/15*a*(15*A*b^2+8*a^2*C+7*b^2*C)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^3*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:774
  public void test0428() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2), x]", //
        "2/15*b*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*a*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]/d+2/45*b*(9*A+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*a*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*b*C*Cos[c+d*x]^(7/2)*Sin[c+d*x]/d+2/21*a*(7*A+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:86
  public void test0429() {
    check( //
        "Integrate[Cos[a+b*x]^4*Sin[a+b*x]^2, x]", //
        "1/16*x+1/16*Cos[a+b*x]*Sin[a+b*x]/b+1/24*Cos[a+b*x]^3*Sin[a+b*x]/b-1/6*Cos[a+b*x]^5*Sin[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:102
  public void test0430() {
    check( //
        "Integrate[Sec[a+b*x]^5*Sin[a+b*x]^3, x]", //
        "1/4*Tan[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:118
  public void test0431() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[a+b*x]^4, x]", //
        "1/16*x+1/16*Cos[a+b*x]*Sin[a+b*x]/b-1/8*Cos[a+b*x]^3*Sin[a+b*x]/b-1/6*Cos[a+b*x]^3*Sin[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:134
  public void test0432() {
    check( //
        "Integrate[Sec[a+b*x]^3*Sin[a+b*x]^5, x]", //
        "-1/2*Cos[a+b*x]^2/b+2*Log[Cos[a+b*x]]/b+1/2*Sec[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:152
  public void test0433() {
    check( //
        "Integrate[Cos[a+b*x]^3/Sin[a+b*x], x]", //
        "Log[Sin[a+b*x]]/b-1/2*Sin[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:168
  public void test0434() {
    check( //
        "Integrate[Cos[a+b*x]/Sin[a+b*x]^2, x]", //
        "-Csc[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:236
  public void test0435() {
    check( //
        "Integrate[Sin[a+b*x]^2/(d*Cos[a+b*x])^(9/2), x]", //
        "2/7*Sin[a+b*x]/(b*d*(d*Cos[a+b*x])^(7/2))-4/21*Sin[a+b*x]/(b*d^3*(d*Cos[a+b*x])^(3/2))-4/21*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*d^4*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:252
  public void test0436() {
    check( //
        "Integrate[Sin[a+b*x]^4/(d*Cos[a+b*x])^(7/2), x]", //
        "2/5*Sin[a+b*x]^3/(b*d*(d*Cos[a+b*x])^(5/2))-12/5*Sin[a+b*x]/(b*d^3*Sqrt[d*Cos[a+b*x]])+24/5*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^4*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:270
  public void test0437() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(5/2)*Csc[a+b*x]^2, x]", //
        "-d*(d*Cos[a+b*x])^(3/2)*Csc[a+b*x]/b-3*d^2*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:314
  public void test0438() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2), x]", //
        "-1/3*c*(d*Cos[a+b*x])^(5/2)*Sqrt[c*Sin[a+b*x]]/(b*d)+1/6*c*d*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/b+1/12*c^2*d^2*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:348
  public void test0439() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(11/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "2/9*Sqrt[c*Sin[a+b*x]]/(b*c*d*(d*Cos[a+b*x])^(9/2))+16/45*Sqrt[c*Sin[a+b*x]]/(b*c*d^3*(d*Cos[a+b*x])^(5/2))+64/45*Sqrt[c*Sin[a+b*x]]/(b*c*d^5*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:451
  public void test0440() {
    check( //
        "Integrate[Sin[e+f*x]^2*Sqrt[b*Sec[e+f*x]], x]", //
        "-2/3*b*Sin[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])+4/3*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:467
  public void test0441() {
    check( //
        "Integrate[Csc[e+f*x]^4*(b*Sec[e+f*x])^(3/2), x]", //
        "-7/2*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-7/6*b*Csc[e+f*x]*Sqrt[b*Sec[e+f*x]]/f-1/3*b*Csc[e+f*x]^3*Sqrt[b*Sec[e+f*x]]/f+7/2*b*Sin[e+f*x]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:517
  public void test0442() {
    check( //
        "Integrate[Sin[e+f*x]^4/(b*Sec[e+f*x])^(5/2), x]", //
        "-4/39*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(7/2))+8/195*Sin[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))-2/13*b*Sin[e+f*x]^3/(f*(b*Sec[e+f*x])^(7/2))+8/65*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:537
  public void test0443() {
    check( //
        "Integrate[Sqrt[b*Sec[e+f*x]]/(a*Sin[e+f*x])^(9/2), x]", //
        "-2/7*b/(a*f*(a*Sin[e+f*x])^(7/2)*Sqrt[b*Sec[e+f*x]])-4/7*b/(a^3*f*(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]])+4/7*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(a^4*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:37
  public void test0444() {
    check( //
        "Integrate[1/(5+3*Sin[c+d*x]), x]", //
        "1/4*x+1/2*ArcTan[Cos[c+d*x]/(3+Sin[c+d*x])]/d");
  }

  // 4.1.1.1 (a+b sin)^n.input:53
  public void test0445() {
    check( //
        "Integrate[1/(3+5*Sin[c+d*x]), x]", //
        "-1/4*Log[3*Cos[1/2*(c+d*x)]+Sin[1/2*(c+d*x)]]/d+1/4*Log[Cos[1/2*(c+d*x)]+3*Sin[1/2*(c+d*x)]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:37
  public void test0446() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])^2, x]", //
        "2/7*Sec[c+d*x]^7*(a^2+a^2*Sin[c+d*x])/d+5/7*a^2*Tan[c+d*x]/d+10/21*a^2*Tan[c+d*x]^3/d+1/7*a^2*Tan[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:53
  public void test0447() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^8, x]", //
        "4199/1024*a^8*x-4199/1920*a^8*Cos[c+d*x]^5/d+4199/1024*a^8*Cos[c+d*x]*Sin[c+d*x]/d+4199/1536*a^8*Cos[c+d*x]^3*Sin[c+d*x]/d-323/1320*a^3*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^5/d-19/132*a^2*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^6/d-1/12*a*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^7/d-4199/6336*a^2*Cos[c+d*x]^5*(a^2+a^2*Sin[c+d*x])^3/d-323/792*Cos[c+d*x]^5*(a^2+a^2*Sin[c+d*x])^4/d-4199/4032*Cos[c+d*x]^5*(a^4+a^4*Sin[c+d*x])^2/d-4199/2688*Cos[c+d*x]^5*(a^8+a^8*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:71
  public void test0448() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "-1/3*Sec[c+d*x]/(d*(a+a*Sin[c+d*x]))+2/3*Tan[c+d*x]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:225
  public void test0449() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])/(e*Cos[c+d*x])^(5/2), x]", //
        "2/3*a/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*a*Sin[c+d*x]/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*a*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:241
  public void test0450() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/Sqrt[e*Cos[c+d*x]], x]", //
        "6*a^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-6*a^3*Sqrt[e*Cos[c+d*x]]/(d*e)-2/5*a*(a+a*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]/(d*e)-6/5*(a^3+a^3*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:259
  public void test0451() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(9/2)/(a+a*Sin[c+d*x]), x]", //
        "2/7*e*(e*Cos[c+d*x])^(7/2)/(a*d)+2/5*e^3*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+6/5*e^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:275
  public void test0452() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^2), x]", //
        "2/3*Sin[c+d*x]/(a^2*d*e*Sqrt[e*Cos[c+d*x]])+(-2/9)/(d*e*(a+a*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]])+(-2/9)/(d*e*(a^2+a^2*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]])-2/3*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^2*d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:291
  public void test0453() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(9/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-4/5*e*(e*Cos[c+d*x])^(7/2)/(a*d*(a+a*Sin[c+d*x])^3)+28/5*e^3*(e*Cos[c+d*x])^(3/2)/(d*(a^4+a^4*Sin[c+d*x]))+42/5*e^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:311
  public void test0454() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(3/2)*Sqrt[e*Cos[c+d*x]], x]", //
        "-5/4*a^2*(e*Cos[c+d*x])^(3/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])-1/2*a*(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]]/(d*e)+5/4*a*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+5/4*a*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:329
  public void test0455() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-1/2*a*(e*Cos[c+d*x])^(7/2)/(d*e*(a+a*Sin[c+d*x])^(3/2))+1/4*e*(e*Cos[c+d*x])^(3/2)/(d*Sqrt[a+a*Sin[c+d*x]])+3/4*e^(5/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))+3/4*e^(5/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:345
  public void test0456() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-4*e*(e*Cos[c+d*x])^(5/2)/(a*d*(a+a*Sin[c+d*x])^(3/2))-5*e^3*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)+5*e^(7/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d*(1+Cos[c+d*x]+Sin[c+d*x]))-5*e^(7/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:434
  public void test0457() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "1/2*(a^2-b^2)*ArcTanh[Sin[c+d*x]]/d+1/2*Sec[c+d*x]^2*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:450
  public void test0458() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Sin[c+d*x])^3, x]", //
        "1/8*a*(4*a^2+3*b^2)*x-1/60*b*(27*a^2+8*b^2)*Cos[c+d*x]^3/d+1/8*a*(4*a^2+3*b^2)*Cos[c+d*x]*Sin[c+d*x]/d-7/20*a*b*Cos[c+d*x]^3*(a+b*Sin[c+d*x])/d-1/5*b*Cos[c+d*x]^3*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:466
  public void test0459() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+b*Sin[c+d*x])^8, x]", //
        "b^8*x+4/105*a*b*(24*a^6-88*a^4*b^2+125*a^2*b^4-96*b^6)*Cos[c+d*x]/d+1/105*b^2*(48*a^6-152*a^4*b^2+174*a^2*b^4-105*b^6)*Cos[c+d*x]*Sin[c+d*x]/d+2/105*a*b*(24*a^4-40*a^2*b^2+9*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d+2/105*b*(24*a^4+8*a^2*b^2-35*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/d+1/7*Sec[c+d*x]^7*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^7/d-2/105*Sec[c+d*x]^3*(a+b*Sin[c+d*x])^5*(b*(6*a^2-7*b^2)-a*(12*a^2-11*b^2)*Sin[c+d*x])/d-1/35*Sec[c+d*x]^5*(a+b*Sin[c+d*x])^6*(a*b-(6*a^2-7*b^2)*Sin[c+d*x])/d-2/105*Sec[c+d*x]*(a+b*Sin[c+d*x])^4*(3*a*b*(12*a^2-11*b^2)-(24*a^4+8*a^2*b^2-35*b^4)*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:484
  public void test0460() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Sin[c+d*x])^2, x]", //
        "2*a*Log[a+b*Sin[c+d*x]]/(b^3*d)-Sin[c+d*x]/(b^2*d)+(a^2-b^2)/(b^3*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:606
  public void test0461() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^2/Sqrt[e*Cos[c+d*x]], x]", //
        "2/3*(3*a^2+2*b^2)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-10/3*a*b*Sqrt[e*Cos[c+d*x]]/(d*e)-2/3*b*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:622
  public void test0462() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4*Sqrt[e*Cos[c+d*x]], x]", //
        "-22/315*a*b*(17*a^2+18*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e)-2/105*b*(41*a^2+14*b^2)*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e)-10/21*a*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^2/(d*e)-2/9*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^3/(d*e)+2/15*(15*a^4+36*a^2*b^2+4*b^4)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:17
  public void test0463() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "2*a*Csc[c+d*x]/d+a*Csc[c+d*x]^2/d-1/3*a*Csc[c+d*x]^3/d-1/4*a*Csc[c+d*x]^4/d+a*Log[Sin[c+d*x]]/d+a*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:68
  public void test0464() {
    check( //
        "Integrate[Tan[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "Sec[c+d*x]/(a*d)-1/3*Sec[c+d*x]^3/(a*d)+1/3*Tan[c+d*x]^3/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:84
  public void test0465() {
    check( //
        "Integrate[Cot[c+d*x]^13/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*Csc[c+d*x]^2/(a^2*d)+2/3*Csc[c+d*x]^3/(a^2*d)+3/4*Csc[c+d*x]^4/(a^2*d)-8/5*Csc[c+d*x]^5/(a^2*d)-1/3*Csc[c+d*x]^6/(a^2*d)+12/7*Csc[c+d*x]^7/(a^2*d)-1/4*Csc[c+d*x]^8/(a^2*d)-8/9*Csc[c+d*x]^9/(a^2*d)+3/10*Csc[c+d*x]^10/(a^2*d)+2/11*Csc[c+d*x]^11/(a^2*d)-1/12*Csc[c+d*x]^12/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:100
  public void test0466() {
    check( //
        "Integrate[Tan[c+d*x]^2/(a+a*Sin[c+d*x])^4, x]", //
        "-4/5*Sec[c+d*x]^5/(a^4*d)+12/7*Sec[c+d*x]^7/(a^4*d)-8/9*Sec[c+d*x]^9/(a^4*d)+1/3*Tan[c+d*x]^3/(a^4*d)+9/5*Tan[c+d*x]^5/(a^4*d)+16/7*Tan[c+d*x]^7/(a^4*d)+8/9*Tan[c+d*x]^9/(a^4*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:127
  public void test0467() {
    check( //
        "Integrate[Tan[e+f*x]^2/(a+a*Sin[e+f*x])^(3/2), x]", //
        "1/32*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))-1/4*Sec[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))+1/32*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])+5/8*Sec[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:199
  public void test0468() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+b*Sin[c+d*x])^3, x]", //
        "b*(6*a^2-b^2)*Csc[c+d*x]/d+1/2*a*(2*a^2-3*b^2)*Csc[c+d*x]^2/d-a^2*b*Csc[c+d*x]^3/d-1/4*a^3*Csc[c+d*x]^4/d+a*(a^2-6*b^2)*Log[Sin[c+d*x]]/d+b*(3*a^2-2*b^2)*Sin[c+d*x]/d+3/2*a*b^2*Sin[c+d*x]^2/d+1/3*b^3*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:217
  public void test0469() {
    check( //
        "Integrate[Cot[c+d*x]^6/(a+b*Sin[c+d*x]), x]", //
        "-2*(a^2-b^2)^(5/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^6*d)+1/8*b*(15*a^4-20*a^2*b^2+8*b^4)*ArcTanh[Cos[c+d*x]]/(a^6*d)-1/15*(23*a^4-35*a^2*b^2+15*b^4)*Cot[c+d*x]/(a^5*d)-Cot[c+d*x]*Csc[c+d*x]/(b*d)+1/8*(8*a^4-9*a^2*b^2+4*b^4)*Cot[c+d*x]*Csc[c+d*x]/(a^4*b*d)+1/2*a*Cot[c+d*x]*Csc[c+d*x]^2/(b^2*d)-1/30*(15*a^4-22*a^2*b^2+10*b^4)*Cot[c+d*x]*Csc[c+d*x]^2/(a^3*b^2*d)+1/4*b*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/5*Cot[c+d*x]*Csc[c+d*x]^4/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:21
  public void test0470() {
    check( //
        "Integrate[(c+d*x)^2*Sin[a+b*x]^2, x]", //
        "-1/4*d^2*x/b^2+1/6*(c+d*x)^3/d+1/4*d^2*Cos[a+b*x]*Sin[a+b*x]/b^3-1/2*(c+d*x)^2*Cos[a+b*x]*Sin[a+b*x]/b+1/2*d*(c+d*x)*Sin[a+b*x]^2/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:41
  public void test0471() {
    check( //
        "Integrate[(c+d*x)^3*Csc[a+b*x]^2, x]", //
        "-I*(c+d*x)^3/b-(c+d*x)^3*Cot[a+b*x]/b+3*d*(c+d*x)^2*Log[1-E^(2*I*(a+b*x))]/b^2-3*I*d^2*(c+d*x)*PolyLog[2,E^(2*I*(a+b*x))]/b^3+3/2*d^3*PolyLog[3,E^(2*I*(a+b*x))]/b^4");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:65
  public void test0472() {
    check( //
        "Integrate[Sin[a+b*x]^2/(c+d*x)^(1/2), x]", //
        "-1/2*Cos[2*a-2*b*c/d]*FresnelC[2*Sqrt[b]*Sqrt[c+d*x]/(Sqrt[Pi]*Sqrt[d])]*Sqrt[Pi]/(Sqrt[b]*Sqrt[d])+1/2*FresnelS[2*Sqrt[b]*Sqrt[c+d*x]/(Sqrt[Pi]*Sqrt[d])]*Sin[2*a-2*b*c/d]*Sqrt[Pi]/(Sqrt[b]*Sqrt[d])+Sqrt[c+d*x]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:170
  public void test0473() {
    check( //
        "Integrate[x^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "8*x*Sqrt[a+a*Sin[c+d*x]]/d^2+16*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/d^3-2*x^2*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:190
  public void test0474() {
    check( //
        "Integrate[x/(a+a*Sin[e+f*x])^(3/2), x]", //
        "(-1)/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-1/2*x*Cot[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])-x*ArcTanh[E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])+I*PolyLog[2,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-I*PolyLog[2,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:287
  public void test0475() {
    check( //
        "Integrate[Csc[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/2*ArcTanh[Cos[c+d*x]]/(a*d)+2*Cot[c+d*x]/(a*d)-3/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)+Cot[c+d*x]*Csc[c+d*x]/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:320
  public void test0476() {
    check( //
        "Integrate[Csc[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-ArcTanh[Cos[c+d*x]]/(a*d)-2*b*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a*d*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:357
  public void test0477() {
    check( //
        "Integrate[(e+f*x)^2*Cos[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/3*(e+f*x)^3/(a*f)-2*f^2*Cos[c+d*x]/(a*d^3)+(e+f*x)^2*Cos[c+d*x]/(a*d)-2*f*(e+f*x)*Sin[c+d*x]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:377
  public void test0478() {
    check( //
        "Integrate[(e+f*x)^2*Sec[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "-2/3*I*(e+f*x)^2/(a*d)-2/3*I*f*(e+f*x)*ArcTan[E^(I*(c+d*x))]/(a*d^2)+4/3*f*(e+f*x)*Log[1+E^(2*I*(c+d*x))]/(a*d^2)+1/3*I*f^2*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^3)-1/3*I*f^2*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^3)-2/3*I*f^2*PolyLog[2,-E^(2*I*(c+d*x))]/(a*d^3)-1/3*f^2*Sec[c+d*x]/(a*d^3)-1/3*f*(e+f*x)*Sec[c+d*x]^2/(a*d^2)-1/3*(e+f*x)^2*Sec[c+d*x]^3/(a*d)+1/3*f^2*Tan[c+d*x]/(a*d^3)+2/3*(e+f*x)^2*Tan[c+d*x]/(a*d)+1/3*f*(e+f*x)*Sec[c+d*x]*Tan[c+d*x]/(a*d^2)+1/3*(e+f*x)^2*Sec[c+d*x]^2*Tan[c+d*x]/(a*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:14
  public void test0479() {
    check( //
        "Integrate[(a+b*x)*Sin[c+d*x]/x, x]", //
        "-b*Cos[c+d*x]/d+a*Cos[c]*SinIntegral[d*x]+a*CosIntegral[d*x]*Sin[c]");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:106
  public void test0480() {
    check( //
        "Integrate[(a+b*x^3)*Sin[c+d*x]/x^3, x]", //
        "-b*Cos[c+d*x]/d-1/2*a*d*Cos[c+d*x]/x-1/2*a*d^2*Cos[c]*SinIntegral[d*x]-1/2*a*d^2*CosIntegral[d*x]*Sin[c]-1/2*a*Sin[c+d*x]/x^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:149
  public void test0481() {
    check( //
        "Integrate[Sin[a+b/x]^2/x, x]", //
        "1/2*CosIntegral[2*b/x]*Cos[2*a]+1/2*Log[x]-1/2*SinIntegral[2*b/x]*Sin[2*a]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:181
  public void test0482() {
    check( //
        "Integrate[Sin[a+b*x^n]^2/x, x]", //
        "-1/2*CosIntegral[2*b*x^n]*Cos[2*a]/n+1/2*Log[x]+1/2*SinIntegral[2*b*x^n]*Sin[2*a]/n");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:197
  public void test0483() {
    check( //
        "Integrate[x^(-1-2*n)*Sin[a+b*x^n]^3, x]", //
        "-3/8*b*Cos[a+b*x^n]/(n*x^n)+3/8*b*Cos[3*(a+b*x^n)]/(n*x^n)-3/8*b^2*Cos[a]*SinIntegral[b*x^n]/n+9/8*b^2*Cos[3*a]*SinIntegral[3*b*x^n]/n-3/8*b^2*CosIntegral[b*x^n]*Sin[a]/n+9/8*b^2*CosIntegral[3*b*x^n]*Sin[3*a]/n-3/8*Sin[a+b*x^n]/(n*x^(2*n))+1/8*Sin[3*(a+b*x^n)]/(n*x^(2*n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:233
  public void test0484() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^3], x]", //
        "1/6*I*E^(I*a)*(c+d*x)*Gamma[1/3,-I*b*(c+d*x)^3]/(d*(-I*b*(c+d*x)^3)^(1/3))-1/6*I*(c+d*x)*Gamma[1/3,I*b*(c+d*x)^3]/(E^(I*a)*d*(I*b*(c+d*x)^3)^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:265
  public void test0485() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b/Sqrt[c+d*x]], x]", //
        "1/6*b*f*(c+d*x)^(3/2)*Cos[a+b/Sqrt[c+d*x]]/d^2-1/12*b^4*f*Cos[a]*SinIntegral[b/Sqrt[c+d*x]]/d^2+b^2*(d*e-c*f)*Cos[a]*SinIntegral[b/Sqrt[c+d*x]]/d^2-1/12*b^4*f*CosIntegral[b/Sqrt[c+d*x]]*Sin[a]/d^2+b^2*(d*e-c*f)*CosIntegral[b/Sqrt[c+d*x]]*Sin[a]/d^2-1/12*b^2*f*(c+d*x)*Sin[a+b/Sqrt[c+d*x]]/d^2+(d*e-c*f)*(c+d*x)*Sin[a+b/Sqrt[c+d*x]]/d^2+1/2*f*(c+d*x)^2*Sin[a+b/Sqrt[c+d*x]]/d^2-1/12*b^3*f*Cos[a+b/Sqrt[c+d*x]]*Sqrt[c+d*x]/d^2+b*(d*e-c*f)*Cos[a+b/Sqrt[c+d*x]]*Sqrt[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:291
  public void test0486() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b/(c+d*x)^(1/3)], x]", //
        "1/2*b^3*(d*e-c*f)*CosIntegral[b/(c+d*x)^(1/3)]*Cos[a]/d^2+1/240*b^5*f*(c+d*x)^(1/3)*Cos[a+b/(c+d*x)^(1/3)]/d^2+1/2*b*(d*e-c*f)*(c+d*x)^(2/3)*Cos[a+b/(c+d*x)^(1/3)]/d^2-1/120*b^3*f*(c+d*x)*Cos[a+b/(c+d*x)^(1/3)]/d^2+1/10*b*f*(c+d*x)^(5/3)*Cos[a+b/(c+d*x)^(1/3)]/d^2+1/240*b^6*f*Cos[a]*SinIntegral[b/(c+d*x)^(1/3)]/d^2+1/240*b^6*f*CosIntegral[b/(c+d*x)^(1/3)]*Sin[a]/d^2-1/2*b^3*(d*e-c*f)*SinIntegral[b/(c+d*x)^(1/3)]*Sin[a]/d^2-1/2*b^2*(d*e-c*f)*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/d^2+1/240*b^4*f*(c+d*x)^(2/3)*Sin[a+b/(c+d*x)^(1/3)]/d^2+(d*e-c*f)*(c+d*x)*Sin[a+b/(c+d*x)^(1/3)]/d^2-1/40*b^2*f*(c+d*x)^(4/3)*Sin[a+b/(c+d*x)^(1/3)]/d^2+1/2*f*(c+d*x)^2*Sin[a+b/(c+d*x)^(1/3)]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:331
  public void test0487() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(2/3)]/(c*e+d*e*x)^(1/3), x]", //
        "-3/2*b*(c+d*x)^(1/3)*CosIntegral[b/(c+d*x)^(2/3)]*Cos[a]/(d*(e*(c+d*x))^(1/3))+3/2*b*(c+d*x)^(1/3)*SinIntegral[b/(c+d*x)^(2/3)]*Sin[a]/(d*(e*(c+d*x))^(1/3))+3/2*(c+d*x)*Sin[a+b/(c+d*x)^(2/3)]/(d*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:424
  public void test0488() {
    check( //
        "Integrate[x*(c*Sin[a+b*x]^3)^(1/3), x]", //
        "(c*Sin[a+b*x]^3)^(1/3)/b^2-x*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:444
  public void test0489() {
    check( //
        "Integrate[x*(c*Sin[a+b*x^n]^3)^(1/3), x]", //
        "1/2*I*E^(I*a)*x^2*Csc[a+b*x^n]*Gamma[2/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*(-I*b*x^n)^(2/n))-1/2*I*x^2*Csc[a+b*x^n]*Gamma[2/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*(I*b*x^n)^(2/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:464
  public void test0490() {
    check( //
        "Integrate[x*(c*Sin[a+b*x^2]^3)^(2/3), x]", //
        "-1/4*Cot[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(2/3)/b+1/4*x^2*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:33
  public void test0491() {
    check( //
        "Integrate[Sin[1/4+x+x^2]^2, x]", //
        "1/2*x-1/4*FresnelC[(1+2*x)/Sqrt[Pi]]*Sqrt[Pi]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:23
  public void test0492() {
    check( //
        "Integrate[Csc[x]^3/(a+a*Sin[x]), x]", //
        "-3/2*ArcTanh[Cos[x]]/a+2*Cot[x]/a-3/2*Cot[x]*Csc[x]/a+Cot[x]*Csc[x]/(a+a*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:40
  public void test0493() {
    check( //
        "Integrate[1/(a+a*Sin[x])^3, x]", //
        "-1/5*Cos[x]/(a+a*Sin[x])^3-2/15*Cos[x]/(a*(a+a*Sin[x])^2)-2/15*Cos[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:76
  public void test0494() {
    check( //
        "Integrate[Csc[c+d*x]^4*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-25/8*a^(5/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-25/8*a^3*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-13/12*a^3*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/3*a^2*Cot[c+d*x]*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:94
  public void test0495() {
    check( //
        "Integrate[Csc[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-19/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)+1/2*Cot[c+d*x]*Csc[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))+13/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+7/4*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:192
  public void test0496() {
    check( //
        "Integrate[Sin[e+f*x]^3*(a+b*Sin[e+f*x]), x]", //
        "3/8*b*x-a*Cos[e+f*x]/f+1/3*a*Cos[e+f*x]^3/f-3/8*b*Cos[e+f*x]*Sin[e+f*x]/f-1/4*b*Cos[e+f*x]*Sin[e+f*x]^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:208
  public void test0497() {
    check( //
        "Integrate[Csc[e+f*x]^5*(a+b*Sin[e+f*x])^2, x]", //
        "-1/8*(3*a^2+4*b^2)*ArcTanh[Cos[e+f*x]]/f-2*a*b*Cot[e+f*x]/f-2/3*a*b*Cot[e+f*x]^3/f-1/8*(3*a^2+4*b^2)*Cot[e+f*x]*Csc[e+f*x]/f-1/4*a^2*Cot[e+f*x]*Csc[e+f*x]^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:244
  public void test0498() {
    check( //
        "Integrate[Csc[x]/(a+b*Sin[x])^3, x]", //
        "-b*(6*a^4-5*a^2*b^2+2*b^4)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^3*(a^2-b^2)^(5/2))-ArcTanh[Cos[x]]/a^3-1/2*b^2*Cos[x]/(a*(a^2-b^2)*(a+b*Sin[x])^2)-1/2*b^2*(5*a^2-2*b^2)*Cos[x]/(a^2*(a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:301
  public void test0499() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^4, x]", //
        "1/7*a*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^5)+2/35*a*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^4)+2/105*a*Cos[e+f*x]^3/(c*f*(c-c*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:317
  public void test0500() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^3, x]", //
        "5/16*a^3*c^3*x+5/16*a^3*c^3*Cos[e+f*x]*Sin[e+f*x]/f+5/24*a^3*c^3*Cos[e+f*x]^3*Sin[e+f*x]/f+1/6*a^3*c^3*Cos[e+f*x]^5*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:335
  public void test0501() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^2), x]", //
        "1/3*Sec[e+f*x]/(a*f*(c^2-c^2*Sin[e+f*x]))+2/3*Tan[e+f*x]/(a*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:371
  public void test0502() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/1155*a^2*c^6*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+64/231*a^2*c^5*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))+8/33*a^2*c^4*Cos[e+f*x]^5/(f*Sqrt[c-c*Sin[e+f*x]])+2/11*a^2*c^3*Cos[e+f*x]^5*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:387
  public void test0503() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(11/2))-5/12*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(7/2))+5/8*a^3*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))-5/8*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(7/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:405
  public void test0504() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "5/8*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(3/2))+5/8*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^2*c^(3/2)*f*Sqrt[2])-5/6*Sec[e+f*x]/(a^2*c*f*Sqrt[c-c*Sin[e+f*x]])-1/3*Sec[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/(a^2*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:425
  public void test0505() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:441
  public void test0506() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-1/2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])-4*a^3*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:475
  public void test0507() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-1/2*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+1/2*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:516
  public void test0508() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c+d*Sin[e+f*x])^4, x]", //
        "1/8*a*(8*c^4+16*c^3*d+24*c^2*d^2+12*c*d^3+3*d^4)*x-1/30*a*(12*c^4+95*c^3*d+112*c^2*d^2+80*c*d^3+16*d^4)*Cos[e+f*x]/f-1/120*a*d*(24*c^3+130*c^2*d+116*c*d^2+45*d^3)*Cos[e+f*x]*Sin[e+f*x]/f-1/60*a*(12*c^2+35*c*d+16*d^2)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/f-1/20*a*(4*c+5*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/f-1/5*a*Cos[e+f*x]*(c+d*Sin[e+f*x])^4/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:554
  public void test0509() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^5/(a+a*Sin[e+f*x])^2, x]", //
        "5/2*(2*c-d)*d^2*(2*c^2-3*c*d+2*d^2)*x/a^2+2/3*d*(c^4+10*c^3*d-44*c^2*d^2+40*c*d^3-12*d^4)*Cos[e+f*x]/(a^2*f)+1/6*d^2*(2*c^3+20*c^2*d-57*c*d^2+30*d^3)*Cos[e+f*x]*Sin[e+f*x]/(a^2*f)+1/3*d*(c^2+10*c*d-12*d^2)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(a^2*f)-1/3*(c-d)*(c+10*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(a^2*f*(1+Sin[e+f*x]))-1/3*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^4/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:628
  public void test0510() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2), x]", //
        "-2*a*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:644
  public void test0511() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^2, x]", //
        "a^(5/2)*(c-d)*(3*c+5*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(5/2)*(c+d)^(3/2)*f)-a^3*(3*c+d)*Cos[e+f*x]/(d^2*(c+d)*f*Sqrt[a+a*Sin[e+f*x]])+a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*(c+d*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:662
  public void test0512() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/16*(c-d)^2*(3*c+13*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/4*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(f*(a+a*Sin[e+f*x])^(5/2))-3/16*(c-d)*(c^2+6*c*d+25*d^2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])+1/4*(c-9*d)*d^2*Cos[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:682
  public void test0513() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(1/2), x]", //
        "1/4*a^(3/2)*(c-7*d)*(c+d)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(3/2)*f)-1/2*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])+1/4*a^2*(c-7*d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:700
  public void test0514() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-(c-d)^(3/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-(3*c-d)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[d]/(f*Sqrt[a])-d*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:87
  public void test0515() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[a+b*x]^2, x]", //
        "1/8*x+1/8*Cos[a+b*x]*Sin[a+b*x]/b-1/4*Cos[a+b*x]^3*Sin[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:103
  public void test0516() {
    check( //
        "Integrate[Sec[a+b*x]^6*Sin[a+b*x]^3, x]", //
        "-1/3*Sec[a+b*x]^3/b+1/5*Sec[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:119
  public void test0517() {
    check( //
        "Integrate[Sin[a+b*x]^4, x]", //
        "3/8*x-3/8*Cos[a+b*x]*Sin[a+b*x]/b-1/4*Cos[a+b*x]*Sin[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:185
  public void test0518() {
    check( //
        "Integrate[Sec[a+b*x]^5/Sin[a+b*x]^3, x]", //
        "-1/2*Cot[a+b*x]^2/b+3*Log[Tan[a+b*x]]/b+3/2*Tan[a+b*x]^2/b+1/4*Tan[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:221
  public void test0519() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*Sin[a+b*x], x]", //
        "-2/3*(d*Cos[a+b*x])^(3/2)/(b*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:253
  public void test0520() {
    check( //
        "Integrate[Sin[a+b*x]^4/(d*Cos[a+b*x])^(9/2), x]", //
        "-4/7*Sin[a+b*x]/(b*d^3*(d*Cos[a+b*x])^(3/2))+2/7*Sin[a+b*x]^3/(b*d*(d*Cos[a+b*x])^(7/2))+8/7*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*d^4*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:271
  public void test0521() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(3/2)*Csc[a+b*x]^2, x]", //
        "-d^2*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-d*Csc[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:315
  public void test0522() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(3/2)/(d*Cos[a+b*x])^(1/2), x]", //
        "-c*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d)+1/2*c^2*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:349
  public void test0523() {
    check( //
        "Integrate[Cos[a+b*x]^(1/2)/Sin[a+b*x]^(1/2), x]", //
        "ArcTan[1-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-ArcTan[1+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-1/2*Log[1+Cot[a+b*x]-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])+1/2*Log[1+Cot[a+b*x]+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:403
  public void test0524() {
    check( //
        "Integrate[Cos[a+b*x]^5*(c*Sin[a+b*x])^m, x]", //
        "(c*Sin[a+b*x])^(1+m)/(b*c*(1+m))-2*(c*Sin[a+b*x])^(3+m)/(b*c^3*(3+m))+(c*Sin[a+b*x])^(5+m)/(b*c^5*(5+m))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:518
  public void test0525() {
    check( //
        "Integrate[Sin[e+f*x]^2/(b*Sec[e+f*x])^(5/2), x]", //
        "-2/9*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(7/2))+4/45*Sin[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))+4/15*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:540
  public void test0526() {
    check( //
        "Integrate[Sin[e+f*x]^(9/2)/Sqrt[b*Sec[e+f*x]], x]", //
        "-7/30*b*Sin[e+f*x]^(3/2)/(f*(b*Sec[e+f*x])^(3/2))-1/5*b*Sin[e+f*x]^(7/2)/(f*(b*Sec[e+f*x])^(3/2))+7/20*EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[Sin[e+f*x]]/(f*(b*Sec[e+f*x])^(1/2)*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:556
  public void test0527() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(7/2)/(b*Sec[e+f*x])^(3/2), x]", //
        "-1/30*a*(a*Sin[e+f*x])^(5/2)/(b*f*Sqrt[b*Sec[e+f*x]])+1/5*(a*Sin[e+f*x])^(9/2)/(a*b*f*Sqrt[b*Sec[e+f*x]])-1/12*a^3*Sqrt[a*Sin[e+f*x]]/(b*f*Sqrt[b*Sec[e+f*x]])+1/24*a^4*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:22
  public void test0528() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "1/3*a*Sec[c+d*x]^3/d+a*Tan[c+d*x]/d+1/3*a*Tan[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:38
  public void test0529() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^3, x]", //
        "55/128*a^3*x-11/56*a^3*Cos[c+d*x]^7/d+55/128*a^3*Cos[c+d*x]*Sin[c+d*x]/d+55/192*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d+11/48*a^3*Cos[c+d*x]^5*Sin[c+d*x]/d-1/9*a*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^2/d-11/72*Cos[c+d*x]^7*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:54
  public void test0530() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^8, x]", //
        "1/5*(a+a*Sin[c+d*x])^10/(a^2*d)-1/11*(a+a*Sin[c+d*x])^11/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:88
  public void test0531() {
    check( //
        "Integrate[Cos[c+d*x]^8/(a+a*Sin[c+d*x])^3, x]", //
        "7/8*x/a^3+7/15*Cos[c+d*x]^5/(a^3*d)+7/8*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+7/12*Cos[c+d*x]^3*Sin[c+d*x]/(a^3*d)+2/3*Cos[c+d*x]^7/(a*d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:104
  public void test0532() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Sin[c+d*x])^8, x]", //
        "(-4/5)/(a^3*d*(a+a*Sin[c+d*x])^5)+(-1/3)/(a^5*d*(a+a*Sin[c+d*x])^3)+1/(d*(a^2+a^2*Sin[c+d*x])^4)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:140
  public void test0533() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/2*Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d+1/2*a^(3/2)*ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:172
  public void test0534() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-11/64*a^5*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))+11/140*a^2*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d+11/126*a*Sec[c+d*x]^7*(a+a*Sin[c+d*x])^(5/2)/d+1/9*Sec[c+d*x]^9*(a+a*Sin[c+d*x])^(7/2)/d-11/64*a^(7/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2])+11/48*a^4*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+11/120*a^3*Sec[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:206
  public void test0535() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-8/3*(a+a*Sin[c+d*x])^(3/2)/(a^4*d)+2/5*(a+a*Sin[c+d*x])^(5/2)/(a^5*d)+8*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:226
  public void test0536() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*a/(d*e*(e*Cos[c+d*x])^(5/2))+2/5*a*Sin[c+d*x]/(d*e*(e*Cos[c+d*x])^(5/2))+6/5*a*Sin[c+d*x]/(d*e^3*Sqrt[e*Cos[c+d*x]])-6/5*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:242
  public void test0537() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/(e*Cos[c+d*x])^(3/2), x]", //
        "14/3*a^3*(e*Cos[c+d*x])^(3/2)/(d*e^3)+4*a^5*(e*Cos[c+d*x])^(7/2)/(d*e^5*(a-a*Sin[c+d*x])^2)-14*a^3*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:260
  public void test0538() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x]), x]", //
        "2/5*e*(e*Cos[c+d*x])^(5/2)/(a*d)+2/3*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a*d*Sqrt[e*Cos[c+d*x]])+2/3*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:276
  public void test0539() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^2), x]", //
        "10/33*Sin[c+d*x]/(a^2*d*e*(e*Cos[c+d*x])^(3/2))+(-2/11)/(d*e*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^2)+(-2/11)/(d*e*(e*Cos[c+d*x])^(3/2)*(a^2+a^2*Sin[c+d*x]))+10/33*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^2*d*e^2*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:292
  public void test0540() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-4/7*e*(e*Cos[c+d*x])^(5/2)/(a*d*(a+a*Sin[c+d*x])^3)+10/21*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^4*d*Sqrt[e*Cos[c+d*x]])+20/21*e^3*Sqrt[e*Cos[c+d*x]]/(d*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:312
  public void test0541() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(3/2)/Sqrt[e*Cos[c+d*x]], x]", //
        "-a*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e)-3*a*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])+3*a*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:330
  public void test0542() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/Sqrt[a+a*Sin[c+d*x]], x]", //
        "e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a*d)-e^(3/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a*d*(1+Cos[c+d*x]+Sin[c+d*x]))+e^(3/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a*d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:346
  public void test0543() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-4/3*e*(e*Cos[c+d*x])^(3/2)/(a*d*(a+a*Sin[c+d*x])^(3/2))-2*e^(5/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]+a^3*Sin[c+d*x]))-2*e^(5/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:607
  public void test0544() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^2/(e*Cos[c+d*x])^(3/2), x]", //
        "2*a*b*(e*Cos[c+d*x])^(3/2)/(d*e^3)+2*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/(d*e*Sqrt[e*Cos[c+d*x]])-2*(a^2+2*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:623
  public void test0545() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4/Sqrt[e*Cos[c+d*x]], x]", //
        "2/7*(7*a^4+28*a^2*b^2+4*b^4)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-6/35*a*b*(31*a^2+34*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e)-2/35*b*(29*a^2+10*b^2)*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)-26/35*a*b*(a+b*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]/(d*e)-2/7*b*(a+b*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:18
  public void test0546() {
    check( //
        "Integrate[Cot[c+d*x]^7*(a+a*Sin[c+d*x]), x]", //
        "-3*a*Csc[c+d*x]/d-3/2*a*Csc[c+d*x]^2/d+a*Csc[c+d*x]^3/d+3/4*a*Csc[c+d*x]^4/d-1/5*a*Csc[c+d*x]^5/d-1/6*a*Csc[c+d*x]^6/d-a*Log[Sin[c+d*x]]/d-a*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:34
  public void test0547() {
    check( //
        "Integrate[Cot[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*a^2*x-2*a^2*ArcTanh[Cos[c+d*x]]/d+2*a^2*Cos[c+d*x]/d-a^2*Cot[c+d*x]/d+1/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:69
  public void test0548() {
    check( //
        "Integrate[1/(a+a*Sin[c+d*x]), x]", //
        "-Cos[c+d*x]/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:128
  public void test0549() {
    check( //
        "Integrate[Cot[e+f*x]^2/(a+a*Sin[e+f*x])^(3/2), x]", //
        "3*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(a^(3/2)*f)-2*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(a^(3/2)*f)-Cot[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:42
  public void test0550() {
    check( //
        "Integrate[(c+d*x)^2*Csc[a+b*x]^2, x]", //
        "-I*(c+d*x)^2/b-(c+d*x)^2*Cot[a+b*x]/b+2*d*(c+d*x)*Log[1-E^(2*I*(a+b*x))]/b^2-I*d^2*PolyLog[2,E^(2*I*(a+b*x))]/b^3");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:143
  public void test0551() {
    check( //
        "Integrate[(c+d*x)^2*(a+a*Sin[e+f*x])^2, x]", //
        "-1/4*a^2*d^2*x/f^2+1/2*a^2*(c+d*x)^3/d+4*a^2*d^2*Cos[e+f*x]/f^3-2*a^2*(c+d*x)^2*Cos[e+f*x]/f+4*a^2*d*(c+d*x)*Sin[e+f*x]/f^2+1/4*a^2*d^2*Cos[e+f*x]*Sin[e+f*x]/f^3-1/2*a^2*(c+d*x)^2*Cos[e+f*x]*Sin[e+f*x]/f+1/2*a^2*d*(c+d*x)*Sin[e+f*x]^2/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:171
  public void test0552() {
    check( //
        "Integrate[x*Sqrt[a+a*Sin[c+d*x]], x]", //
        "4*Sqrt[a+a*Sin[c+d*x]]/d^2-2*x*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:227
  public void test0553() {
    check( //
        "Integrate[(c+d*x)^2/(a+b*Sin[e+f*x]), x]", //
        "-I*(c+d*x)^2*Log[1-I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/(f*Sqrt[a^2-b^2])+I*(c+d*x)^2*Log[1-I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/(f*Sqrt[a^2-b^2])-2*d*(c+d*x)*PolyLog[2,I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/(f^2*Sqrt[a^2-b^2])+2*d*(c+d*x)*PolyLog[2,I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/(f^2*Sqrt[a^2-b^2])-2*I*d^2*PolyLog[3,I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/(f^3*Sqrt[a^2-b^2])+2*I*d^2*PolyLog[3,I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/(f^3*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:378
  public void test0554() {
    check( //
        "Integrate[(e+f*x)*Sec[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/6*f*ArcTanh[Sin[c+d*x]]/(a*d^2)+2/3*f*Log[Cos[c+d*x]]/(a*d^2)-1/6*f*Sec[c+d*x]^2/(a*d^2)-1/3*(e+f*x)*Sec[c+d*x]^3/(a*d)+2/3*(e+f*x)*Tan[c+d*x]/(a*d)+1/6*f*Sec[c+d*x]*Tan[c+d*x]/(a*d^2)+1/3*(e+f*x)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:439
  public void test0555() {
    check( //
        "Integrate[(e+f*x)*Cos[c+d*x]/(a+b*Sin[c+d*x])^3, x]", //
        "a*f*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b*(a^2-b^2)^(3/2)*d^2)+1/2*(-e-f*x)/(b*d*(a+b*Sin[c+d*x])^2)+1/2*f*Cos[c+d*x]/((a^2-b^2)*d^2*(a+b*Sin[c+d*x]))");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:15
  public void test0556() {
    check( //
        "Integrate[(a+b*x)*Sin[c+d*x]/x^2, x]", //
        "a*d*CosIntegral[d*x]*Cos[c]+b*Cos[c]*SinIntegral[d*x]+b*CosIntegral[d*x]*Sin[c]-a*d*SinIntegral[d*x]*Sin[c]-a*Sin[c+d*x]/x");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:33
  public void test0557() {
    check( //
        "Integrate[Sin[c+d*x]/(a+b*x), x]", //
        "Cos[c-a*d/b]*SinIntegral[a*d/b+d*x]/b+CosIntegral[a*d/b+d*x]*Sin[c-a*d/b]/b");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:69
  public void test0558() {
    check( //
        "Integrate[(a+b*x^2)^2*Sin[c+d*x]/x^3, x]", //
        "-1/2*a^2*d*Cos[c+d*x]/x-b^2*x*Cos[c+d*x]/d+2*a*b*Cos[c]*SinIntegral[d*x]-1/2*a^2*d^2*Cos[c]*SinIntegral[d*x]+2*a*b*CosIntegral[d*x]*Sin[c]-1/2*a^2*d^2*CosIntegral[d*x]*Sin[c]+b^2*Sin[c+d*x]/d^2-1/2*a^2*Sin[c+d*x]/x^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:107
  public void test0559() {
    check( //
        "Integrate[(a+b*x^3)*Sin[c+d*x]/x^4, x]", //
        "-1/6*a*d^3*CosIntegral[d*x]*Cos[c]-1/6*a*d*Cos[c+d*x]/x^2+b*Cos[c]*SinIntegral[d*x]+b*CosIntegral[d*x]*Sin[c]+1/6*a*d^3*SinIntegral[d*x]*Sin[c]-1/3*a*Sin[c+d*x]/x^3+1/6*a*d^2*Sin[c+d*x]/x");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:91
  public void test0560() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^3])/x^6, x]", //
        "-1/5*a/x^5-3/10*b*d*Cos[c+d*x^3]/x^2-3/20*I*E^(I*c)*b*d^2*x*Gamma[1/3,-I*d*x^3]/(-I*d*x^3)^(1/3)+3/20*I*b*d^2*x*Gamma[1/3,I*d*x^3]/(E^(I*c)*(I*d*x^3)^(1/3))-1/5*b*Sin[c+d*x^3]/x^5");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:115
  public void test0561() {
    check( //
        "Integrate[x^2/(a+b*Sin[c+d*x^3])^2, x]", //
        "2/3*a*ArcTan[(b+a*Tan[1/2*(c+d*x^3)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)+1/3*b*Cos[c+d*x^3]/((a^2-b^2)*d*(a+b*Sin[c+d*x^3]))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:150
  public void test0562() {
    check( //
        "Integrate[Sin[a+b/x]^2/x^2, x]", //
        "(-1/2)/x+1/2*Cos[a+b/x]*Sin[a+b/x]/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:182
  public void test0563() {
    check( //
        "Integrate[Sin[a+b*x^n]^3/x, x]", //
        "3/4*Cos[a]*SinIntegral[b*x^n]/n-1/4*Cos[3*a]*SinIntegral[3*b*x^n]/n+3/4*CosIntegral[b*x^n]*Sin[a]/n-1/4*CosIntegral[3*b*x^n]*Sin[3*a]/n");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:206
  public void test0564() {
    check( //
        "Integrate[(e+f*x)^3*Sin[b*(c+d*x)^2], x]", //
        "-3/2*f*(d*e-c*f)^2*Cos[b*(c+d*x)^2]/(b*d^4)-3/2*f^2*(d*e-c*f)*(c+d*x)*Cos[b*(c+d*x)^2]/(b*d^4)-1/2*f^3*(c+d*x)^2*Cos[b*(c+d*x)^2]/(b*d^4)+1/2*f^3*Sin[b*(c+d*x)^2]/(b^2*d^4)+3/2*f^2*(d*e-c*f)*FresnelC[(c+d*x)*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/(b^(3/2)*d^4)+(d*e-c*f)^3*FresnelS[(c+d*x)*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/(d^4*Sqrt[b])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:292
  public void test0565() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)], x]", //
        "1/2*b^3*CosIntegral[b/(c+d*x)^(1/3)]*Cos[a]/d+1/2*b*(c+d*x)^(2/3)*Cos[a+b/(c+d*x)^(1/3)]/d-1/2*b^3*SinIntegral[b/(c+d*x)^(1/3)]*Sin[a]/d-1/2*b^2*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/d+(c+d*x)*Sin[a+b/(c+d*x)^(1/3)]/d");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:314
  public void test0566() {
    check( //
        "Integrate[(c*e+d*e*x)^(1/3)*Sin[a+b*(c+d*x)^(2/3)], x]", //
        "-3/2*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(2/3)]/(b*d)+3/2*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(2/3)]/(b^2*d*(c+d*x)^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:445
  public void test0567() {
    check( //
        "Integrate[(c*Sin[a+b*x^n]^3)^(1/3), x]", //
        "1/2*I*E^(I*a)*x*Csc[a+b*x^n]*Gamma[1/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*(-I*b*x^n)^(1/n))-1/2*I*x*Csc[a+b*x^n]*Gamma[1/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*(I*b*x^n)^(1/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:465
  public void test0568() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(2/3), x]", //
        "1/2*x*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)-1/4*Cos[2*a]*Csc[a+b*x^2]^2*FresnelC[2*x*Sqrt[b]/Sqrt[Pi]]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]/Sqrt[b]+1/4*Csc[a+b*x^2]^2*FresnelS[2*x*Sqrt[b]/Sqrt[Pi]]*Sin[2*a]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]/Sqrt[b]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:41
  public void test0569() {
    check( //
        "Integrate[Csc[x]/(a+a*Sin[x])^3, x]", //
        "-ArcTanh[Cos[x]]/a^3+1/5*Cos[x]/(a+a*Sin[x])^3+7/15*Cos[x]/(a*(a+a*Sin[x])^2)+22/15*Cos[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:61
  public void test0570() {
    check( //
        "Integrate[Sin[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-68/105*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-68/45*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-34/63*a^2*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/9*a^2*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+136/315*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:77
  public void test0571() {
    check( //
        "Integrate[Csc[c+d*x]^5*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-163/64*a^(5/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-163/64*a^3*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-163/96*a^3*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-17/24*a^3*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])-1/4*a^2*Cot[c+d*x]*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:95
  public void test0572() {
    check( //
        "Integrate[Sin[c+d*x]^5/(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/4*Cos[c+d*x]*Sin[c+d*x]^4/(d*(a+a*Sin[c+d*x])^(5/2))+21/16*Cos[c+d*x]*Sin[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^(3/2))+283/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1729/120*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-157/80*Cos[c+d*x]*Sin[c+d*x]^2/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+787/240*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:193
  public void test0573() {
    check( //
        "Integrate[Sin[e+f*x]^2*(a+b*Sin[e+f*x]), x]", //
        "1/2*a*x-b*Cos[e+f*x]/f+1/3*b*Cos[e+f*x]^3/f-1/2*a*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:211
  public void test0574() {
    check( //
        "Integrate[Sin[e+f*x]*(a+b*Sin[e+f*x])^3, x]", //
        "3/8*b*(4*a^2+b^2)*x-1/2*a*(a^2+4*b^2)*Cos[e+f*x]/f-1/8*b*(2*a^2+3*b^2)*Cos[e+f*x]*Sin[e+f*x]/f-1/4*a*Cos[e+f*x]*(a+b*Sin[e+f*x])^2/f-1/4*Cos[e+f*x]*(a+b*Sin[e+f*x])^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:245
  public void test0575() {
    check( //
        "Integrate[Csc[x]^2/(a+b*Sin[x])^3, x]", //
        "3*b^2*(4*a^4-5*a^2*b^2+2*b^4)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^4*(a^2-b^2)^(5/2))+3*b*ArcTanh[Cos[x]]/a^4-1/2*(2*a^4-11*a^2*b^2+6*b^4)*Cot[x]/(a^3*(a^2-b^2)^2)-1/2*b^2*Cot[x]/(a*(a^2-b^2)*(a+b*Sin[x])^2)-3/2*b^2*(2*a^2-b^2)*Cot[x]/(a^2*(a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:302
  public void test0576() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^5, x]", //
        "1/9*a*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^6)+1/21*a*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^5)+2/105*a*Cos[e+f*x]^3/(c*f*(c-c*Sin[e+f*x])^4)+2/315*a*c*Cos[e+f*x]^3/(f*(c^2-c^2*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:318
  public void test0577() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^2, x]", //
        "3/8*a^3*c^2*x-1/5*a^3*c^2*Cos[e+f*x]^5/f+3/8*a^3*c^2*Cos[e+f*x]*Sin[e+f*x]/f+1/4*a^3*c^2*Cos[e+f*x]^3*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:336
  public void test0578() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^3), x]", //
        "1/5*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^2)+1/5*Sec[e+f*x]/(a*f*(c^3-c^3*Sin[e+f*x]))+2/5*Tan[e+f*x]/(a*c^3*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:352
  public void test0579() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])/(a+a*Sin[e+f*x])^3, x]", //
        "-1/5*a*c*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^4)-1/15*c*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:372
  public void test0580() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/315*a^2*c^5*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+16/63*a^2*c^4*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))+2/9*a^2*c^3*Cos[e+f*x]^5/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:388
  public void test0581() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/4*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(13/2))-5/24*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(9/2))+5/32*a^3*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(5/2))-5/128*a^3*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(3/2))-5/128*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(9/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:406
  public void test0582() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "35/64*Cos[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2))+7/24*Sec[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2))+35/64*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^2*c^(5/2)*f*Sqrt[2])-35/48*Sec[e+f*x]/(a^2*c^2*f*Sqrt[c-c*Sin[e+f*x]])-1/3*Sec[e+f*x]^3/(a^2*c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:426
  public void test0583() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:442
  public void test0584() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(3/2))+4*a^3*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:476
  public void test0585() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-1/2*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2))+3/8*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*Cos[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:517
  public void test0586() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c+d*Sin[e+f*x])^3, x]", //
        "1/8*a*(8*c^3+12*c^2*d+12*c*d^2+3*d^3)*x-1/6*a*(3*c^3+16*c^2*d+12*c*d^2+4*d^3)*Cos[e+f*x]/f-1/24*a*d*(6*c^2+20*c*d+9*d^2)*Cos[e+f*x]*Sin[e+f*x]/f-1/12*a*(3*c+4*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/f-1/4*a*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:555
  public void test0587() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^4/(a+a*Sin[e+f*x])^2, x]", //
        "1/2*d^2*(12*c^2-16*c*d+7*d^2)*x/a^2+2/3*d*(c^3+8*c^2*d-20*c*d^2+8*d^3)*Cos[e+f*x]/(a^2*f)+1/6*d^2*(2*c^2+16*c*d-21*d^2)*Cos[e+f*x]*Sin[e+f*x]/(a^2*f)-1/3*(c-d)*(c+8*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(a^2*f*(1+Sin[e+f*x]))-1/3*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:629
  public void test0588() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x]), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[d]*Sqrt[c+d])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:645
  public void test0589() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^3, x]", //
        "-1/4*a^(5/2)*(3*c^2+10*c*d+19*d^2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(5/2)*(c+d)^(5/2)*f)+3/4*a^3*(c-d)*(c+3*d)*Cos[e+f*x]/(d^2*(c+d)^2*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])+1/2*a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*(c+d*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:663
  public void test0590() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-3/16*(c-d)*(c+3*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/4*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c^2+10*c*d+19*d^2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:683
  public void test0591() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^(1/2), x]", //
        "a^(3/2)*(c-3*d)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(3/2)*f)-a^2*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:701
  public void test0592() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]*Sqrt[c-d]/(f*Sqrt[a])-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[d]/(f*Sqrt[a])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:50
  public void test0593() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/Sqrt[c-c*Sin[e+f*x]], x]", //
        "1/5*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:68
  public void test0594() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])+1/3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])+8*c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+4*c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:94
  public void test0595() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^m*Sqrt[c-c*Sin[e+f*x]], x]", //
        "8*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(1+m)/(a*f*(15+16*m+4*m^2)*Sqrt[c-c*Sin[e+f*x]])+2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(1+m)*Sqrt[c-c*Sin[e+f*x]]/(a*f*(5+2*m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:128
  public void test0596() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "2/33*a^2*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-14/99*a^2*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-2/11*a*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g)+14/45*a^2*c^3*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+14/15*a^2*c^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2/15*a^2*c^2*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:144
  public void test0597() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(9/2), x]", //
        "4/13*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*(c-c*Sin[e+f*x])^(9/2))+308/585*a^3*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-154/195*a^3*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-44/117*a^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f*g*(c-c*Sin[e+f*x])^(7/2))+154/195*a^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^4*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:162
  public void test0598() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]]/Sqrt[a+a*Sin[e+f*x]], x]", //
        "2/3*c*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:178
  public void test0599() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "28/5*c^2*(g*Cos[e+f*x])^(5/2)/(a*f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+42/5*c^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-4/5*c*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*(a+a*Sin[e+f*x])^(5/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:342
  public void test0600() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "1/8*a^2*x-2/3*a^2*Cos[c+d*x]^3/d+3/5*a^2*Cos[c+d*x]^5/d-1/7*a^2*Cos[c+d*x]^7/d+1/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-1/3*a^2*Cos[c+d*x]^3*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:360
  public void test0601() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^3, x]", //
        "7/16*a^3*ArcTanh[Cos[c+d*x]]/d-4/3*a^3*Cot[c+d*x]^3/d-3/5*a^3*Cot[c+d*x]^5/d+7/16*a^3*Cot[c+d*x]*Csc[c+d*x]/d-17/24*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:398
  public void test0602() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+2/3*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+2/3*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:416
  public void test0603() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3/Sqrt[a+a*Sin[c+d*x]], x]", //
        "1/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+1/4*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:464
  public void test0604() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "a*x-3/8*a*ArcTanh[Cos[c+d*x]]/d+a*Cot[c+d*x]/d-1/3*a*Cot[c+d*x]^3/d+3/8*a*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a*Cot[c+d*x]^3*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:480
  public void test0605() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^9*(a+a*Sin[c+d*x])^2, x]", //
        "-11/128*a^2*ArcTanh[Cos[c+d*x]]/d-2/5*a^2*Cot[c+d*x]^5/d-2/7*a^2*Cot[c+d*x]^7/d-11/128*a^2*Cot[c+d*x]*Csc[c+d*x]/d+7/64*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^2*Cot[c+d*x]^3*Csc[c+d*x]^3/d+1/16*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d-1/8*a^2*Cot[c+d*x]^3*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:496
  public void test0606() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^10*(a+a*Sin[c+d*x])^3, x]", //
        "-17/128*a^3*ArcTanh[Cos[c+d*x]]/d-4/5*a^3*Cot[c+d*x]^5/d-5/7*a^3*Cot[c+d*x]^7/d-1/9*a^3*Cot[c+d*x]^9/d-17/128*a^3*Cot[c+d*x]*Csc[c+d*x]/d+5/64*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^3*Cot[c+d*x]^3*Csc[c+d*x]^3/d+3/16*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d-3/8*a^3*Cot[c+d*x]^3*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:514
  public void test0607() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "11/16*x/a^2+2*Cos[c+d*x]/(a^2*d)-4/3*Cos[c+d*x]^3/(a^2*d)+2/5*Cos[c+d*x]^5/(a^2*d)-11/16*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-11/24*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d)-1/6*Cos[c+d*x]*Sin[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:551
  public void test0608() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-256/5005*a^4*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-64/1001*a^3*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/13*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d-8/143*a^2*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-6/143*a*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:569
  public void test0609() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-11/64*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])-11/64*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+53/96*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+1/24*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:585
  public void test0610() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "5*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)-4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)-Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:613
  public void test0611() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^8*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cot[c+d*x]^6/d-1/3*a*Csc[c+d*x]^3/d+2/5*a*Csc[c+d*x]^5/d-1/7*a*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:691
  public void test0612() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "3/256*a*x-1/7*a*Cos[c+d*x]^7/d+2/9*a*Cos[c+d*x]^9/d-1/11*a*Cos[c+d*x]^11/d+3/256*a*Cos[c+d*x]*Sin[c+d*x]/d+1/128*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/160*a*Cos[c+d*x]^5*Sin[c+d*x]/d-3/80*a*Cos[c+d*x]^7*Sin[c+d*x]/d-1/10*a*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:707
  public void test0613() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "17/1024*a^2*x-2/7*a^2*Cos[c+d*x]^7/d+4/9*a^2*Cos[c+d*x]^9/d-2/11*a^2*Cos[c+d*x]^11/d+17/1024*a^2*Cos[c+d*x]*Sin[c+d*x]/d+17/1536*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+17/1920*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-17/320*a^2*Cos[c+d*x]^7*Sin[c+d*x]/d-17/120*a^2*Cos[c+d*x]^7*Sin[c+d*x]^3/d-1/12*a^2*Cos[c+d*x]^7*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:723
  public void test0614() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^13*(a+a*Sin[c+d*x])^2, x]", //
        "17/1024*a^2*ArcTanh[Cos[c+d*x]]/d-2/7*a^2*Cot[c+d*x]^7/d-4/9*a^2*Cot[c+d*x]^9/d-2/11*a^2*Cot[c+d*x]^11/d+17/1024*a^2*Cot[c+d*x]*Csc[c+d*x]/d+17/1536*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d-11/384*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d+1/16*a^2*Cot[c+d*x]^3*Csc[c+d*x]^5/d-1/10*a^2*Cot[c+d*x]^5*Csc[c+d*x]^5/d-1/64*a^2*Cot[c+d*x]*Csc[c+d*x]^7/d+1/24*a^2*Cot[c+d*x]^3*Csc[c+d*x]^7/d-1/12*a^2*Cot[c+d*x]^5*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:739
  public void test0615() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^12*(a+a*Sin[c+d*x])^3, x]", //
        "19/256*a^3*ArcTanh[Cos[c+d*x]]/d-4/7*a^3*Cot[c+d*x]^7/d-5/9*a^3*Cot[c+d*x]^9/d-1/11*a^3*Cot[c+d*x]^11/d+19/256*a^3*Cot[c+d*x]*Csc[c+d*x]/d-7/128*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d+5/48*a^3*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/8*a^3*Cot[c+d*x]^5*Csc[c+d*x]^3/d-3/32*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d+3/16*a^3*Cot[c+d*x]^3*Csc[c+d*x]^5/d-3/10*a^3*Cot[c+d*x]^5*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:757
  public void test0616() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "-1/4*x/a^2-2/15*Cos[c+d*x]^5/(a^2*d)-1/4*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/6*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)-1/3*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:805
  public void test0617() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^11*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cot[c+d*x]^8/d-1/10*a*Cot[c+d*x]^10/d+1/3*a*Csc[c+d*x]^3/d-3/5*a*Csc[c+d*x]^5/d+3/7*a*Csc[c+d*x]^7/d-1/9*a*Csc[c+d*x]^9/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:853
  public void test0618() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "3/256*x/a+1/7*Cos[c+d*x]^7/(a*d)-2/9*Cos[c+d*x]^9/(a*d)+1/11*Cos[c+d*x]^11/(a*d)+3/256*Cos[c+d*x]*Sin[c+d*x]/(a*d)+1/128*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)+1/160*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)-3/80*Cos[c+d*x]^7*Sin[c+d*x]/(a*d)-1/10*Cos[c+d*x]^7*Sin[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:869
  public void test0619() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-3/128*x/a^2-2/5*Cos[c+d*x]^5/(a^2*d)+5/7*Cos[c+d*x]^7/(a^2*d)-4/9*Cos[c+d*x]^9/(a^2*d)+1/11*Cos[c+d*x]^11/(a^2*d)-3/128*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/64*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)+1/16*Cos[c+d*x]^5*Sin[c+d*x]/(a^2*d)+1/8*Cos[c+d*x]^5*Sin[c+d*x]^3/(a^2*d)+1/5*Cos[c+d*x]^5*Sin[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:885
  public void test0620() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^12/(a+a*Sin[c+d*x])^2, x]", //
        "3/128*ArcTanh[Cos[c+d*x]]/(a^2*d)-2/5*Cot[c+d*x]^5/(a^2*d)-5/7*Cot[c+d*x]^7/(a^2*d)-4/9*Cot[c+d*x]^9/(a^2*d)-1/11*Cot[c+d*x]^11/(a^2*d)+3/128*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+1/64*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)+1/80*Cot[c+d*x]*Csc[c+d*x]^5/(a^2*d)-3/40*Cot[c+d*x]*Csc[c+d*x]^7/(a^2*d)+1/5*Cot[c+d*x]^3*Csc[c+d*x]^7/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:911
  public void test0621() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "-a*x+a*Sec[c+d*x]/d+a*Tan[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1042
  public void test0622() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "1/7*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^2)-2/35*Sec[c+d*x]^3/(d*(a^2+a^2*Sin[c+d*x]))+8/35*Tan[c+d*x]/(a^2*d)+8/105*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1067
  public void test0623() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "-23/16*a*Log[1-Sin[c+d*x]]/d+7/16*a*Log[1+Sin[c+d*x]]/d-a*Sin[c+d*x]/d+1/8*a^3/(d*(a-a*Sin[c+d*x])^2)-a^2/(d*(a-a*Sin[c+d*x]))+1/8*a^2/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1113
  public void test0624() {
    check( //
        "Integrate[Sec[c+d*x]^7*Sin[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "5/128*ArcTanh[Sin[c+d*x]]/(a*d)+5/128*Sec[c+d*x]*Tan[c+d*x]/(a*d)-5/64*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+5/48*Sec[c+d*x]^3*Tan[c+d*x]^3/(a*d)-1/8*Sec[c+d*x]^3*Tan[c+d*x]^5/(a*d)+1/6*Tan[c+d*x]^6/(a*d)+1/8*Tan[c+d*x]^8/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1147
  public void test0625() {
    check( //
        "Integrate[Sec[c+d*x]^9*Sin[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "-7/256*ArcTanh[Sin[c+d*x]]/(a*d)-7/256*Sec[c+d*x]*Tan[c+d*x]/(a*d)+7/128*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)-7/96*Sec[c+d*x]^3*Tan[c+d*x]^3/(a*d)+7/80*Sec[c+d*x]^3*Tan[c+d*x]^5/(a*d)-1/10*Sec[c+d*x]^3*Tan[c+d*x]^7/(a*d)+1/8*Tan[c+d*x]^8/(a*d)+1/10*Tan[c+d*x]^10/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1176
  public void test0626() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^m*(c+d*Sin[e+f*x])^3, x]", //
        "(c-d)^3*(a+a*Sin[e+f*x])^(1+m)/(a*f*(1+m))+3*(c-d)^2*d*(a+a*Sin[e+f*x])^(2+m)/(a^2*f*(2+m))+3*(c-d)*d^2*(a+a*Sin[e+f*x])^(3+m)/(a^3*f*(3+m))+d^3*(a+a*Sin[e+f*x])^(4+m)/(a^4*f*(4+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1363
  public void test0627() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]*(a+b*Sin[c+d*x])^2, x]", //
        "a*b*x-a^2*ArcTanh[Cos[c+d*x]]/d+1/3*(2*a^2-b^2)*Cos[c+d*x]/d+1/3*a*b*Cos[c+d*x]*Sin[c+d*x]/d+1/3*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1409
  public void test0628() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2*(a+b*Sin[c+d*x]), x]", //
        "1/16*a*x-1/5*b*Cos[c+d*x]^5/d+1/7*b*Cos[c+d*x]^7/d+1/16*a*Cos[c+d*x]*Sin[c+d*x]/d+1/24*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*a*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1535
  public void test0629() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "-1/6*a*Cos[c+d*x]^6/d+1/8*a*Cos[c+d*x]^8/d+1/5*b*Sin[c+d*x]^5/d-2/7*b*Sin[c+d*x]^7/d+1/9*b*Sin[c+d*x]^9/d");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:38
  public void test0630() {
    check( //
        "Integrate[Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x]), x]", //
        "2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[g]/(Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]*Sqrt[g]/(c*f)+2*Sec[e+f*x]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]/(c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:48
  public void test0631() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^5, x]", //
        "2/9*a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^5)-1/63*a*(A+19*B)*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^4)-1/105*a*(A-2*B)*c*Cos[e+f*x]/(f*(c^2-c^2*Sin[e+f*x])^3)-2/315*a*(A-2*B)*c*Cos[e+f*x]/(f*(c^3-c^3*Sin[e+f*x])^2)-2/315*a*(A-2*B)*Cos[e+f*x]/(f*(c^5-c^5*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:64
  public void test0632() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^3, x]", //
        "5/16*a^3*A*c^3*x-1/7*a^3*B*c^3*Cos[e+f*x]^7/f+5/16*a^3*A*c^3*Cos[e+f*x]*Sin[e+f*x]/f+5/24*a^3*A*c^3*Cos[e+f*x]^3*Sin[e+f*x]/f+1/6*a^3*A*c^3*Cos[e+f*x]^5*Sin[e+f*x]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:82
  public void test0633() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^2), x]", //
        "1/3*(A+B)*Sec[e+f*x]/(a*f*(c^2-c^2*Sin[e+f*x]))+1/3*(2*A-B)*Tan[e+f*x]/(a*c^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:98
  public void test0634() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^3, x]", //
        "B*c^2*x/a^3-1/5*a^2*(A-B)*c^2*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^5)-2/3*B*c^2*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^3)+2*B*c^2*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:118
  public void test0635() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/15015*a^2*(13*A-3*B)*c^6*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+64/3003*a^2*(13*A-3*B)*c^5*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))-2/13*a^2*B*c^2*Cos[e+f*x]^5*(c-c*Sin[e+f*x])^(3/2)/f+8/429*a^2*(13*A-3*B)*c^4*Cos[e+f*x]^5/(f*Sqrt[c-c*Sin[e+f*x]])+2/143*a^2*(13*A-3*B)*c^3*Cos[e+f*x]^5*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:134
  public void test0636() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/6*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(13/2))-1/24*a^3*(A+13*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(9/2))+5/48*a^3*(A+13*B)*Cos[e+f*x]^3/(c*f*(c-c*Sin[e+f*x])^(5/2))-5/8*a^3*(A+13*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(7/2)*f*Sqrt[2])+5/16*a^3*(A+13*B)*Cos[e+f*x]/(c^3*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:152
  public void test0637() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "1/8*(5*A+B)*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(3/2))+1/8*(5*A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^2*c^(3/2)*f*Sqrt[2])-1/6*(5*A+B)*Sec[e+f*x]/(a^2*c*f*Sqrt[c-c*Sin[e+f*x]])-1/3*(A-B)*Sec[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/(a^2*c^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:172
  public void test0638() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-a*B*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:188
  public void test0639() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-1/2*a*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])-1/3*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*Sqrt[c-c*Sin[e+f*x]])-4*a^3*(A+B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*a^2*(A+B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:204
  public void test0640() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/8*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(9/2))-1/3*a*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*(c-c*Sin[e+f*x])^(7/2))+1/2*a^2*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^2*f*(c-c*Sin[e+f*x])^(5/2))-a^3*B*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^3*f*(c-c*Sin[e+f*x])^(3/2))-a^4*B*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^4*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:222
  public void test0641() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-1/2*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+1/2*A*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/2*A*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:377
  public void test0642() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(A-B)*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(A*c+3*B*c+3*A*d-7*B*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])-2*B*d*Cos[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:20
  public void test0643() {
    check( //
        "Integrate[(a*Sin[x]^3)^(1/2), x]", //
        "-2/3*Cot[x]*Sqrt[a*Sin[x]^3]-2/3*EllipticF[1/4*Pi-1/2*x,2]*Sqrt[a*Sin[x]^3]/Sin[x]^(3/2)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:65
  public void test0644() {
    check( //
        "Integrate[Sin[c+d*x]^5/(a-a*Sin[c+d*x]^2), x]", //
        "2*Cos[c+d*x]/(a*d)-1/3*Cos[c+d*x]^3/(a*d)+Sec[c+d*x]/(a*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:81
  public void test0645() {
    check( //
        "Integrate[Sin[c+d*x]/(a-a*Sin[c+d*x]^2)^2, x]", //
        "1/3*Sec[c+d*x]^3/(a^2*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:101
  public void test0646() {
    check( //
        "Integrate[Sin[c+d*x]^4*(a+b*Sin[c+d*x]^2), x]", //
        "1/16*(6*a+5*b)*x-1/16*(6*a+5*b)*Cos[c+d*x]*Sin[c+d*x]/d-1/24*(6*a+5*b)*Cos[c+d*x]*Sin[c+d*x]^3/d-1/6*b*Cos[c+d*x]*Sin[c+d*x]^5/d");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:154
  public void test0647() {
    check( //
        "Integrate[(a-a*Sin[x]^2)^(3/2), x]", //
        "1/3*(a*Cos[x]^2)^(3/2)*Tan[x]+2/3*a*Sqrt[a*Cos[x]^2]*Tan[x]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:192
  public void test0648() {
    check( //
        "Integrate[Sin[e+f*x]^2/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "EllipticE[e+f*x,-b/a]*Sqrt[a+b*Sin[e+f*x]^2]/(b*f*Sqrt[1+b*Sin[e+f*x]^2/a])-a*EllipticF[e+f*x,-b/a]*Sqrt[1+b*Sin[e+f*x]^2/a]/(b*f*Sqrt[a+b*Sin[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:245
  public void test0649() {
    check( //
        "Integrate[1/(a+b*Sin[c+d*x]^3), x]", //
        "2/3*ArcTan[(b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)-b^(2/3)])+2/3*ArcTan[((-1)^(2/3)*b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)])-2/3*ArcTan[(-1)^(1/3)*(b^(1/3)+(-1)^(2/3)*a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:311
  public void test0650() {
    check( //
        "Integrate[Sin[c+d*x]^3/Sqrt[a+b*Sin[c+d*x]^4], x]", //
        "Cos[c+d*x]*Sqrt[a+b-2*b*Cos[c+d*x]^2+b*Cos[c+d*x]^4]/(d*Sqrt[b]*(1+Cos[c+d*x]^2*Sqrt[b]/Sqrt[a+b])*Sqrt[a+b])-(a+b)^(3/4)*EllipticE[2*ArcTan[b^(1/4)*Cos[c+d*x]/(a+b)^(1/4)],1/2*(1+Sqrt[b]/Sqrt[a+b])]*(1+Cos[c+d*x]^2*Sqrt[b]/Sqrt[a+b])*Sqrt[(a+b-2*b*Cos[c+d*x]^2+b*Cos[c+d*x]^4)/((a+b)*(1+Cos[c+d*x]^2*Sqrt[b]/Sqrt[a+b])^2)]/(b^(3/4)*d*Sqrt[a+b-2*b*Cos[c+d*x]^2+b*Cos[c+d*x]^4])-1/2*(a+b)^(1/4)*EllipticF[2*ArcTan[b^(1/4)*Cos[c+d*x]/(a+b)^(1/4)],1/2*(1+Sqrt[b]/Sqrt[a+b])]*(1+Cos[c+d*x]^2*Sqrt[b]/Sqrt[a+b])*(Sqrt[b]-Sqrt[a+b])*Sqrt[(a+b-2*b*Cos[c+d*x]^2+b*Cos[c+d*x]^4)/((a+b)*(1+Cos[c+d*x]^2*Sqrt[b]/Sqrt[a+b])^2)]/(b^(3/4)*d*Sqrt[a+b-2*b*Cos[c+d*x]^2+b*Cos[c+d*x]^4])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:343
  public void test0651() {
    check( //
        "Integrate[Cos[x]^7/(a-a*Sin[x]^2), x]", //
        "Sin[x]/a-2/3*Sin[x]^3/a+1/5*Sin[x]^5/a");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:359
  public void test0652() {
    check( //
        "Integrate[Sec[x]/(a-a*Sin[x]^2)^2, x]", //
        "3/8*ArcTanh[Sin[x]]/a^2+3/8*Sec[x]*Tan[x]/a^2+1/4*Sec[x]^3*Tan[x]/a^2");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:419
  public void test0653() {
    check( //
        "Integrate[Sec[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "-ArcTanh[Sin[e+f*x]*Sqrt[b]/Sqrt[a+b*Sin[e+f*x]^2]]*Sqrt[b]/f+ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]*Sqrt[a+b]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:441
  public void test0654() {
    check( //
        "Integrate[Cos[e+f*x]/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "ArcTanh[Sin[e+f*x]*Sqrt[b]/Sqrt[a+b*Sin[e+f*x]^2]]/(f*Sqrt[b])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:72
  public void test0655() {
    check( //
        "Integrate[Sec[a+b*x]*Sin[a+b*x], x]", //
        "-Log[Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:104
  public void test0656() {
    check( //
        "Integrate[Sec[a+b*x]^7*Sin[a+b*x]^3, x]", //
        "-1/4*Sec[a+b*x]^4/b+1/6*Sec[a+b*x]^6/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:136
  public void test0657() {
    check( //
        "Integrate[Sec[a+b*x]^5*Sin[a+b*x]^5, x]", //
        "-Log[Cos[a+b*x]]/b-1/2*Tan[a+b*x]^2/b+1/4*Tan[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:154
  public void test0658() {
    check( //
        "Integrate[Cos[a+b*x]/Sin[a+b*x], x]", //
        "Log[Sin[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:170
  public void test0659() {
    check( //
        "Integrate[Sec[a+b*x]^2/Sin[a+b*x]^2, x]", //
        "-Cot[a+b*x]/b+Tan[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:222
  public void test0660() {
    check( //
        "Integrate[Sin[a+b*x]/(d*Cos[a+b*x])^(1/2), x]", //
        "-2*Sqrt[d*Cos[a+b*x]]/(b*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:254
  public void test0661() {
    check( //
        "Integrate[Cos[a+b*x]^(3/2)*Sin[a+b*x]^5, x]", //
        "-2/5*Cos[a+b*x]^(5/2)/b+4/9*Cos[a+b*x]^(9/2)/b-2/13*Cos[a+b*x]^(13/2)/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:272
  public void test0662() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*Csc[a+b*x]^2, x]", //
        "-(d*Cos[a+b*x])^(3/2)*Csc[a+b*x]/(b*d)-EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:316
  public void test0663() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(3/2)/(d*Cos[a+b*x])^(5/2), x]", //
        "2/3*c*Sqrt[c*Sin[a+b*x]]/(b*d*(d*Cos[a+b*x])^(3/2))-1/3*c^2*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*d^2*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:332
  public void test0664() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(5/2)/(d*Cos[a+b*x])^(13/2), x]", //
        "2/11*c*(c*Sin[a+b*x])^(3/2)/(b*d*(d*Cos[a+b*x])^(11/2))-6/77*c*(c*Sin[a+b*x])^(3/2)/(b*d^3*(d*Cos[a+b*x])^(7/2))-8/77*c*(c*Sin[a+b*x])^(3/2)/(b*d^5*(d*Cos[a+b*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:350
  public void test0665() {
    check( //
        "Integrate[Cos[a+b*x]^(3/2)/Sin[a+b*x]^(3/2), x]", //
        "ArcTan[1-Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]]/(b*Sqrt[2])-ArcTan[1+Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]]/(b*Sqrt[2])-1/2*Log[1-Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]+Tan[a+b*x]]/(b*Sqrt[2])+1/2*Log[1+Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]+Tan[a+b*x]]/(b*Sqrt[2])-2*Sqrt[Cos[a+b*x]]/(b*Sqrt[Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:404
  public void test0666() {
    check( //
        "Integrate[Cos[a+b*x]^3*(c*Sin[a+b*x])^m, x]", //
        "(c*Sin[a+b*x])^(1+m)/(b*c*(1+m))-(c*Sin[a+b*x])^(3+m)/(b*c^3*(3+m))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:453
  public void test0667() {
    check( //
        "Integrate[Csc[e+f*x]^2*Sqrt[b*Sec[e+f*x]], x]", //
        "-b*Csc[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])+EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:519
  public void test0668() {
    check( //
        "Integrate[1/(b*Sec[e+f*x])^(5/2), x]", //
        "2/5*Sin[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))+6/5*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:541
  public void test0669() {
    check( //
        "Integrate[Sin[e+f*x]^(5/2)/Sqrt[b*Sec[e+f*x]], x]", //
        "-1/3*b*Sin[e+f*x]^(3/2)/(f*(b*Sec[e+f*x])^(3/2))+1/2*EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:557
  public void test0670() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(3/2)/(b*Sec[e+f*x])^(3/2), x]", //
        "1/3*(a*Sin[e+f*x])^(5/2)/(a*b*f*Sqrt[b*Sec[e+f*x]])-1/6*a*Sqrt[a*Sin[e+f*x]]/(b*f*Sqrt[b*Sec[e+f*x]])+1/12*a^2*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:39
  public void test0671() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^3, x]", //
        "2/3*(a+a*Sin[c+d*x])^6/(a^3*d)-4/7*(a+a*Sin[c+d*x])^7/(a^4*d)+1/8*(a+a*Sin[c+d*x])^8/(a^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:55
  public void test0672() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^8, x]", //
        "2431/256*a^8*x-2431/384*a^8*Cos[c+d*x]^3/d+2431/256*a^8*Cos[c+d*x]*Sin[c+d*x]/d-17/48*a^3*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^5/d-17/90*a^2*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^6/d-1/10*a*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^7/d-2431/2016*a^2*Cos[c+d*x]^3*(a^2+a^2*Sin[c+d*x])^3/d-221/336*Cos[c+d*x]^3*(a^2+a^2*Sin[c+d*x])^4/d-2431/1120*Cos[c+d*x]^3*(a^4+a^4*Sin[c+d*x])^2/d-2431/640*Cos[c+d*x]^3*(a^8+a^8*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:73
  public void test0673() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "-1/5*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x]))+4/5*Tan[c+d*x]/(a*d)+4/15*Tan[c+d*x]^3/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:105
  public void test0674() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Sin[c+d*x])^8, x]", //
        "-1/11*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^8)-1/33*Cos[c+d*x]^5/(a*d*(a+a*Sin[c+d*x])^7)-2/231*Cos[c+d*x]^5/(a^2*d*(a+a*Sin[c+d*x])^6)-2/1155*Cos[c+d*x]^5/(a^3*d*(a+a*Sin[c+d*x])^5)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:125
  public void test0675() {
    check( //
        "Integrate[Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]], x]", //
        "ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]*Sqrt[a]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:141
  public void test0676() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/3*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d-1/2*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2])+1/2*a*Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:157
  public void test0677() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-131072/969969*a^7*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(7/2))-32768/138567*a^6*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(5/2))-12288/46189*a^5*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(3/2))-48/323*a^2*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^(3/2)/d-2/19*a*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^(5/2)/d-1024/4199*a^4*Cos[c+d*x]^7/(d*Sqrt[a+a*Sin[c+d*x]])-64/323*a^3*Cos[c+d*x]^7*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:207
  public void test0678() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Sin[c+d*x])^(5/2), x]", //
        "2/3*Cos[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^(3/2))-4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)+4*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:227
  public void test0679() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])^2, x]", //
        "-26/99*a^2*(e*Cos[c+d*x])^(9/2)/(d*e)+26/77*a^2*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d-2/11*(e*Cos[c+d*x])^(9/2)*(a^2+a^2*Sin[c+d*x])/(d*e)+130/231*a^2*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+130/231*a^2*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:243
  public void test0680() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/(e*Cos[c+d*x])^(5/2), x]", //
        "4/3*a^5*(e*Cos[c+d*x])^(5/2)/(d*e^5*(a-a*Sin[c+d*x])^2)-10/3*a^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])+10/3*a^3*Sqrt[e*Cos[c+d*x]]/(d*e^3)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:261
  public void test0681() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x]), x]", //
        "2/3*e*(e*Cos[c+d*x])^(3/2)/(a*d)+2*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:277
  public void test0682() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])^2), x]", //
        "14/65*Sin[c+d*x]/(a^2*d*e*(e*Cos[c+d*x])^(5/2))+(-2/13)/(d*e*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^2)+(-2/13)/(d*e*(e*Cos[c+d*x])^(5/2)*(a^2+a^2*Sin[c+d*x]))+42/65*Sin[c+d*x]/(a^2*d*e^3*Sqrt[e*Cos[c+d*x]])-42/65*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^2*d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:293
  public void test0683() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-4/9*e*(e*Cos[c+d*x])^(3/2)/(a*d*(a+a*Sin[c+d*x])^3)+2/15*e*(e*Cos[c+d*x])^(3/2)/(d*(a^2+a^2*Sin[c+d*x])^2)+2/15*e*(e*Cos[c+d*x])^(3/2)/(d*(a^4+a^4*Sin[c+d*x]))+2/15*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:313
  public void test0684() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(3/2)/(e*Cos[c+d*x])^(3/2), x]", //
        "4*a*Sqrt[a+a*Sin[c+d*x]]/(d*e*Sqrt[e*Cos[c+d*x]])-2*a^2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(3/2)*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))-2*a^2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(3/2)*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:331
  public void test0685() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))+2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a+a*Cos[c+d*x]+a*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:419
  public void test0686() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "-1/6*b*Cos[c+d*x]^6/d+a*Sin[c+d*x]/d-2/3*a*Sin[c+d*x]^3/d+1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:436
  public void test0687() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+b*Sin[c+d*x])^2, x]", //
        "5/128*(8*a^2+b^2)*x-9/56*a*b*Cos[c+d*x]^7/d+5/128*(8*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d+5/192*(8*a^2+b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/48*(8*a^2+b^2)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*b*Cos[c+d*x]^7*(a+b*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:502
  public void test0688() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+b*Sin[c+d*x])^3, x]", //
        "-3*a*x/b^4-1/2*Cos[c+d*x]^3/(b*d*(a+b*Sin[c+d*x])^2)-3/2*Cos[c+d*x]*(2*a+b*Sin[c+d*x])/(b^3*d*(a+b*Sin[c+d*x]))+3*(2*a^2-b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^4*d*Sqrt[a^2-b^2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:608
  public void test0689() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^2/(e*Cos[c+d*x])^(5/2), x]", //
        "2/3*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*(a^2-2*b^2)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])+2/3*a*b*Sqrt[e*Cos[c+d*x]]/(d*e^3)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:624
  public void test0690() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4/(e*Cos[c+d*x])^(3/2), x]", //
        "2/15*a*b*(15*a^2+62*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e^3)+2/5*b*(5*a^2+6*b^2)*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e^3)+2*a*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^2/(d*e^3)+2*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^3/(d*e*Sqrt[e*Cos[c+d*x]])-2/5*(5*a^4+60*a^2*b^2+12*b^4)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:35
  public void test0691() {
    check( //
        "Integrate[Cot[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*a^2*x+3*a^2*ArcTanh[Cos[c+d*x]]/d-2*a^2*Cos[c+d*x]/d-1/3*a^2*Cot[c+d*x]^3/d-a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:51
  public void test0692() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4, x]", //
        "35/8*a^4*x-8*a^4*Cos[c+d*x]/d+4/3*a^4*Cos[c+d*x]^3/d-27/8*a^4*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:70
  public void test0693() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "ArcTanh[Cos[c+d*x]]/(a*d)-Cot[c+d*x]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:129
  public void test0694() {
    check( //
        "Integrate[Cot[e+f*x]^4/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/8*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(a^(3/2)*f)-1/8*Cot[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])+11/12*Cot[e+f*x]*Csc[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])-1/3*Cot[e+f*x]*Csc[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:23
  public void test0695() {
    check( //
        "Integrate[Sin[a+b*x]^2/(c+d*x), x]", //
        "-1/2*CosIntegral[2*b*c/d+2*b*x]*Cos[2*a-2*b*c/d]/d+1/2*Log[c+d*x]/d+1/2*SinIntegral[2*b*c/d+2*b*x]*Sin[2*a-2*b*c/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:172
  public void test0696() {
    check( //
        "Integrate[Sqrt[a+a*Sin[c+d*x]]/x, x]", //
        "Cos[1/4*(Pi+2*c)]*Csc[1/4*Pi+1/2*c+1/2*d*x]*SinIntegral[1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]+CosIntegral[1/2*d*x]*Csc[1/4*Pi+1/2*c+1/2*d*x]*Sin[1/4*(Pi+2*c)]*Sqrt[a+a*Sin[c+d*x]]");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:228
  public void test0697() {
    check( //
        "Integrate[(c+d*x)/(a+b*Sin[e+f*x]), x]", //
        "-I*(c+d*x)*Log[1-I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/(f*Sqrt[a^2-b^2])+I*(c+d*x)*Log[1-I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/(f*Sqrt[a^2-b^2])-d*PolyLog[2,I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/(f^2*Sqrt[a^2-b^2])+d*PolyLog[2,I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/(f^2*Sqrt[a^2-b^2])");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:16
  public void test0698() {
    check( //
        "Integrate[(a+b*x)*Sin[c+d*x]/x^3, x]", //
        "b*d*CosIntegral[d*x]*Cos[c]-1/2*a*d*Cos[c+d*x]/x-1/2*a*d^2*Cos[c]*SinIntegral[d*x]-1/2*a*d^2*CosIntegral[d*x]*Sin[c]-b*d*SinIntegral[d*x]*Sin[c]-1/2*a*Sin[c+d*x]/x^2-b*Sin[c+d*x]/x");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:34
  public void test0699() {
    check( //
        "Integrate[Sin[c+d*x]/(x*(a+b*x)), x]", //
        "Cos[c]*SinIntegral[d*x]/a-Cos[c-a*d/b]*SinIntegral[a*d/b+d*x]/a+CosIntegral[d*x]*Sin[c]/a-CosIntegral[a*d/b+d*x]*Sin[c-a*d/b]/a");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:70
  public void test0700() {
    check( //
        "Integrate[(a+b*x^2)^2*Sin[c+d*x]/x^4, x]", //
        "2*a*b*d*CosIntegral[d*x]*Cos[c]-1/6*a^2*d^3*CosIntegral[d*x]*Cos[c]-b^2*Cos[c+d*x]/d-1/6*a^2*d*Cos[c+d*x]/x^2-2*a*b*d*SinIntegral[d*x]*Sin[c]+1/6*a^2*d^3*SinIntegral[d*x]*Sin[c]-1/3*a^2*Sin[c+d*x]/x^3-2*a*b*Sin[c+d*x]/x+1/6*a^2*d^2*Sin[c+d*x]/x");
  }
}
