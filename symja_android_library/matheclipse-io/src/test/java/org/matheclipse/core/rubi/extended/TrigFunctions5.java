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
public class TrigFunctions5 extends AbstractRubiTestCase {
  static boolean init = true;

  public TrigFunctions5(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("TrigFunctions5");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 4.1.0 (a sin)^m (b trg)^n.input:108
  public void test0001() {
    check( //
        "Integrate[Cos[a+b*x]^5*Sin[a+b*x]^4, x]", //
        "1/5*Sin[a+b*x]^5/b-2/7*Sin[a+b*x]^7/b+1/9*Sin[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:124
  public void test0002() {
    check( //
        "Integrate[Sec[a+b*x]^9*Sin[a+b*x]^4, x]", //
        "3/128*ArcTanh[Sin[a+b*x]]/b+3/128*Sec[a+b*x]*Tan[a+b*x]/b+1/64*Sec[a+b*x]^3*Tan[a+b*x]/b-1/16*Sec[a+b*x]^5*Tan[a+b*x]/b+1/8*Sec[a+b*x]^5*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:140
  public void test0003() {
    check( //
        "Integrate[Sec[a+b*x]^9*Sin[a+b*x]^5, x]", //
        "1/6*Tan[a+b*x]^6/b+1/8*Tan[a+b*x]^8/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:174
  public void test0004() {
    check( //
        "Integrate[Cos[a+b*x]^7/Sin[a+b*x]^3, x]", //
        "-1/2*Csc[a+b*x]^2/b-3*Log[Sin[a+b*x]]/b+3/2*Sin[a+b*x]^2/b-1/4*Sin[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:206
  public void test0005() {
    check( //
        "Integrate[Cos[a+b*x]^3/Sin[a+b*x]^5, x]", //
        "-1/4*Cot[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:276
  public void test0006() {
    check( //
        "Integrate[Csc[a+b*x]^2/(d*Cos[a+b*x])^(7/2), x]", //
        "-Csc[a+b*x]/(b*d*(d*Cos[a+b*x])^(5/2))+7/5*Sin[a+b*x]/(b*d*(d*Cos[a+b*x])^(5/2))+21/5*Sin[a+b*x]/(b*d^3*Sqrt[d*Cos[a+b*x]])-21/5*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^4*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:304
  public void test0007() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(9/2)*(c*Sin[a+b*x])^(1/2), x]", //
        "7/30*d^3*(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2)/(b*c)+1/5*d*(d*Cos[a+b*x])^(7/2)*(c*Sin[a+b*x])^(3/2)/(b*c)+7/20*d^4*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:336
  public void test0008() {
    check( //
        "Integrate[Sqrt[Sin[x]]/Sqrt[Cos[x]], x]", //
        "-ArcTan[1-Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]]/Sqrt[2]+ArcTan[1+Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]]/Sqrt[2]+1/2*Log[1-Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]+Tan[x]]/Sqrt[2]-1/2*Log[1+Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]+Tan[x]]/Sqrt[2]");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:491
  public void test0009() {
    check( //
        "Integrate[Sin[e+f*x]^4/Sqrt[b*Sec[e+f*x]], x]", //
        "-4/15*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-2/9*b*Sin[e+f*x]^3/(f*(b*Sec[e+f*x])^(3/2))+8/15*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:507
  public void test0010() {
    check( //
        "Integrate[Csc[e+f*x]^2/(b*Sec[e+f*x])^(3/2), x]", //
        "-Csc[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])-EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:545
  public void test0011() {
    check( //
        "Integrate[Sin[e+f*x]^(3/2)/Sqrt[b*Sec[e+f*x]], x]", //
        "1/4*ArcTan[1-Sqrt[2]*Sqrt[b*Cos[e+f*x]]/(Sqrt[b]*Sqrt[Sin[e+f*x]])]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-1/4*ArcTan[1+Sqrt[2]*Sqrt[b*Cos[e+f*x]]/(Sqrt[b]*Sqrt[Sin[e+f*x]])]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-1/8*Log[Sqrt[b]+Cot[e+f*x]*Sqrt[b]-Sqrt[2]*Sqrt[b*Cos[e+f*x]]/Sqrt[Sin[e+f*x]]]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+1/8*Log[Sqrt[b]+Cot[e+f*x]*Sqrt[b]+Sqrt[2]*Sqrt[b*Cos[e+f*x]]/Sqrt[Sin[e+f*x]]]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-1/2*b*Sqrt[Sin[e+f*x]]/(f*(b*Sec[e+f*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:561
  public void test0012() {
    check( //
        "Integrate[1/((b*Sec[e+f*x])^(3/2)*(a*Sin[e+f*x])^(13/2)), x]", //
        "(-2/11)/(a*b*f*(a*Sin[e+f*x])^(11/2)*Sqrt[b*Sec[e+f*x]])+2/77/(a^3*b*f*(a*Sin[e+f*x])^(7/2)*Sqrt[b*Sec[e+f*x]])+4/77/(a^5*b*f*(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]])-4/77*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(a^6*b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:27
  public void test0013() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "1/2*(a+a*Sin[c+d*x])^4/(a^2*d)-1/5*(a+a*Sin[c+d*x])^5/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:77
  public void test0014() {
    check( //
        "Integrate[Cos[c+d*x]^6/(a+a*Sin[c+d*x])^2, x]", //
        "5/8*x/a^2+5/12*Cos[c+d*x]^3/(a^2*d)+5/8*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+1/4*Cos[c+d*x]^5/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:93
  public void test0015() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "-Log[1+Sin[c+d*x]]/(a^3*d)+(-2)/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:145
  public void test0016() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-4096/15015*a^5*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-1024/3003*a^4*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/13*a*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d-128/429*a^3*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-32/143*a^2*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:161
  public void test0017() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-4096/3465*a^5*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-32/99*a^2*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d-2/11*a*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^(5/2)/d-1024/1155*a^4*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-128/231*a^3*Cos[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:231
  public void test0018() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/Sqrt[e*Cos[c+d*x]], x]", //
        "10/3*a^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-10/3*a^2*Sqrt[e*Cos[c+d*x]]/(d*e)-2/3*(a^2+a^2*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:247
  public void test0019() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^4, x]", //
        "-442/385*a^4*(e*Cos[c+d*x])^(5/2)/(d*e)-2/11*a*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^3/(d*e)-34/99*(e*Cos[c+d*x])^(5/2)*(a^2+a^2*Sin[c+d*x])^2/(d*e)-442/693*(e*Cos[c+d*x])^(5/2)*(a^4+a^4*Sin[c+d*x])/(d*e)+442/231*a^4*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+442/231*a^4*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:265
  public void test0020() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])), x]", //
        "6/5*Sin[c+d*x]/(a*d*e*Sqrt[e*Cos[c+d*x]])+(-2/5)/(d*e*(a+a*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]])-6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a*d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:281
  public void test0021() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(9/2)/(a+a*Sin[c+d*x])^3, x]", //
        "-14/3*e^3*(e*Cos[c+d*x])^(3/2)/(a^3*d)-4*e*(e*Cos[c+d*x])^(7/2)/(a*d*(a+a*Sin[c+d*x])^2)-14*e^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:297
  public void test0022() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^4), x]", //
        "42/221*Sin[c+d*x]/(a^4*d*e*Sqrt[e*Cos[c+d*x]])+(-2/17)/(d*e*(a+a*Sin[c+d*x])^4*Sqrt[e*Cos[c+d*x]])+(-18/221)/(a*d*e*(a+a*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]])+(-14/221)/(d*e*(a^2+a^2*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]])+(-14/221)/(d*e*(a^4+a^4*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]])-42/221*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^4*d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:317
  public void test0023() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(3/2)/(e*Cos[c+d*x])^(11/2), x]", //
        "-2/3*(a+a*Sin[c+d*x])^(3/2)/(d*e*(e*Cos[c+d*x])^(9/2))+4*(a+a*Sin[c+d*x])^(5/2)/(a*d*e*(e*Cos[c+d*x])^(9/2))-16/5*(a+a*Sin[c+d*x])^(7/2)/(a^2*d*e*(e*Cos[c+d*x])^(9/2))+32/45*(a+a*Sin[c+d*x])^(9/2)/(a^3*d*e*(e*Cos[c+d*x])^(9/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:335
  public void test0024() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(7/2)*Sqrt[a+a*Sin[c+d*x]]), x]", //
        "16/7*(a+a*Sin[c+d*x])^(3/2)/(a^2*d*e*(e*Cos[c+d*x])^(5/2))-32/35*(a+a*Sin[c+d*x])^(5/2)/(a^3*d*e*(e*Cos[c+d*x])^(5/2))+(-2/7)/(d*e*(e*Cos[c+d*x])^(5/2)*Sqrt[a+a*Sin[c+d*x]])-4/7*Sqrt[a+a*Sin[c+d*x]]/(a*d*e*(e*Cos[c+d*x])^(5/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:351
  public void test0025() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^(5/2)), x]", //
        "(-2/13)/(d*e*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(5/2))+(-16/117)/(a*d*e*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(3/2))+256/585*(a+a*Sin[c+d*x])^(3/2)/(a^4*d*e*(e*Cos[c+d*x])^(3/2))+(-32/195)/(a^2*d*e*(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]])-128/195*Sqrt[a+a*Sin[c+d*x]]/(a^3*d*e*(e*Cos[c+d*x])^(3/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:424
  public void test0026() {
    check( //
        "Integrate[Sec[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "3/8*a*ArcTanh[Sin[c+d*x]]/d+1/4*Sec[c+d*x]^4*(b+a*Sin[c+d*x])/d+3/8*a*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:440
  public void test0027() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+b*Sin[c+d*x])^2, x]", //
        "1/3*a*b*Sec[c+d*x]/d+1/3*Sec[c+d*x]^3*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d+1/3*(2*a^2-b^2)*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:456
  public void test0028() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x])^8, x]", //
        "1/9*(a^2-b^2)^2*(a+b*Sin[c+d*x])^9/(b^5*d)-2/5*a*(a^2-b^2)*(a+b*Sin[c+d*x])^10/(b^5*d)+2/11*(3*a^2-b^2)*(a+b*Sin[c+d*x])^11/(b^5*d)-1/3*a*(a+b*Sin[c+d*x])^12/(b^5*d)+1/13*(a+b*Sin[c+d*x])^13/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:490
  public void test0029() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+b*Sin[c+d*x])^2, x]", //
        "3/2*(2*a^2-b^2)*x/b^4+3/2*Cos[c+d*x]*(2*a-b*Sin[c+d*x])/(b^3*d)-Cos[c+d*x]^3/(b*d*(a+b*Sin[c+d*x]))-6*a*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(b^4*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:596
  public void test0030() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x]), x]", //
        "-2/5*b*(e*Cos[c+d*x])^(5/2)/(d*e)+2/3*a*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/3*a*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:612
  public void test0031() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^3, x]", //
        "-2/315*b*(89*a^2+28*b^2)*(e*Cos[c+d*x])^(5/2)/(d*e)-26/63*a*b*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])/(d*e)-2/9*b*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^2/(d*e)+2/21*a*(7*a^2+6*b^2)*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/21*a*(7*a^2+6*b^2)*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:39
  public void test0032() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "-3*a^3*Csc[c+d*x]/d-1/2*a^3*Csc[c+d*x]^2/d+2*a^3*Log[Sin[c+d*x]]/d-2*a^3*Sin[c+d*x]/d-3/2*a^3*Sin[c+d*x]^2/d-1/3*a^3*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:57
  public void test0033() {
    check( //
        "Integrate[Tan[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "-35/128*ArcTanh[Sin[c+d*x]]/(a*d)+35/128*Sec[c+d*x]*Tan[c+d*x]/(a*d)-35/192*Sec[c+d*x]*Tan[c+d*x]^3/(a*d)+7/48*Sec[c+d*x]*Tan[c+d*x]^5/(a*d)-1/8*Sec[c+d*x]*Tan[c+d*x]^7/(a*d)+1/8*Tan[c+d*x]^8/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:90
  public void test0034() {
    check( //
        "Integrate[Cot[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "4*Csc[c+d*x]/(a^3*d)-2*Csc[c+d*x]^2/(a^3*d)+Csc[c+d*x]^3/(a^3*d)-1/4*Csc[c+d*x]^4/(a^3*d)+4*Log[Sin[c+d*x]]/(a^3*d)-4*Log[1+Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:111
  public void test0035() {
    check( //
        "Integrate[Cot[e+f*x]^4*Sqrt[a+a*Sin[e+f*x]], x]", //
        "11/8*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]*Sqrt[a]/f-2*a*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+11/8*a*Cot[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-1/12*a*Cot[e+f*x]*Csc[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-1/3*Cot[e+f*x]*Csc[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:189
  public void test0036() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+b*Sin[c+d*x])^2, x]", //
        "4*a*b*Csc[c+d*x]/d+1/2*(2*a^2-b^2)*Csc[c+d*x]^2/d-2/3*a*b*Csc[c+d*x]^3/d-1/4*a^2*Csc[c+d*x]^4/d+(a^2-2*b^2)*Log[Sin[c+d*x]]/d+2*a*b*Sin[c+d*x]/d+1/2*b^2*Sin[c+d*x]^2/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:176
  public void test0037() {
    check( //
        "Integrate[x^2*(a+a*Sin[e+f*x])^(3/2), x]", //
        "32/3*a*x*Sqrt[a+a*Sin[e+f*x]]/f^2+224/9*a*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f^3-8/3*a*x^2*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f-32/27*a*Cos[1/4*Pi+1/2*e+1/2*f*x]^2*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f^3-4/3*a*x^2*Cos[1/4*Pi+1/2*e+1/2*f*x]*Sin[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f+16/9*a*x*Sin[1/4*Pi+1/2*e+1/2*f*x]^2*Sqrt[a+a*Sin[e+f*x]]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:214
  public void test0038() {
    check( //
        "Integrate[(c+d*x)*(a+b*Sin[e+f*x]), x]", //
        "1/2*a*(c+d*x)^2/d-b*(c+d*x)*Cos[e+f*x]/f+b*d*Sin[e+f*x]/f^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:20
  public void test0039() {
    check( //
        "Integrate[x*(a+b*x)^2*Sin[c+d*x], x]", //
        "4*a*b*Cos[c+d*x]/d^3+6*b^2*x*Cos[c+d*x]/d^3-a^2*x*Cos[c+d*x]/d-2*a*b*x^2*Cos[c+d*x]/d-b^2*x^3*Cos[c+d*x]/d-6*b^2*Sin[c+d*x]/d^4+a^2*Sin[c+d*x]/d^2+4*a*b*x*Sin[c+d*x]/d^2+3*b^2*x^2*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:58
  public void test0040() {
    check( //
        "Integrate[(a+b*x^2)*Sin[c+d*x], x]", //
        "2*b*Cos[c+d*x]/d^3-a*Cos[c+d*x]/d-b*x^2*Cos[c+d*x]/d+2*b*x*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:112
  public void test0041() {
    check( //
        "Integrate[(a+b*x^3)^2*Sin[c+d*x]/x^3, x]", //
        "-2*a*b*Cos[c+d*x]/d-1/2*a^2*d*Cos[c+d*x]/x+6*b^2*x*Cos[c+d*x]/d^3-b^2*x^3*Cos[c+d*x]/d-1/2*a^2*d^2*Cos[c]*SinIntegral[d*x]-1/2*a^2*d^2*CosIntegral[d*x]*Sin[c]-6*b^2*Sin[c+d*x]/d^4-1/2*a^2*Sin[c+d*x]/x^2+3*b^2*x^2*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:80
  public void test0042() {
    check( //
        "Integrate[x^5*(a+b*Sin[c+d*x^3]), x]", //
        "1/6*a*x^6-1/3*b*x^3*Cos[c+d*x^3]/d+1/3*b*Sin[c+d*x^3]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:139
  public void test0043() {
    check( //
        "Integrate[x*Sin[a+b/x], x]", //
        "1/2*b*x*Cos[a+b/x]+1/2*b^2*Cos[a]*SinIntegral[b/x]+1/2*b^2*CosIntegral[b/x]*Sin[a]+1/2*x^2*Sin[a+b/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:159
  public void test0044() {
    check( //
        "Integrate[Sin[a+b/x^2]/x, x]", //
        "-1/2*Cos[a]*SinIntegral[b/x^2]-1/2*CosIntegral[b/x^2]*Sin[a]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:187
  public void test0045() {
    check( //
        "Integrate[x^m*Sin[a+b*x^n], x]", //
        "1/2*I*E^(I*a)*x^(1+m)*Gamma[(1+m)/n,-I*b*x^n]/(n*(-I*b*x^n)^((1+m)/n))-1/2*I*x^(1+m)*Gamma[(1+m)/n,I*b*x^n]/(E^(I*a)*n*(I*b*x^n)^((1+m)/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:245
  public void test0046() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^3], x]", //
        "-1/6*I*E^(I*a)*(-I*b/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,-I*b/(c+d*x)^3]/d+1/6*I*(I*b/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,I*b/(c+d*x)^3]/(E^(I*a)*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:271
  public void test0047() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(3/2)], x]", //
        "-1/3*I*E^(I*a)*(-I*b/(c+d*x)^(3/2))^(2/3)*(c+d*x)*Gamma[-2/3,-I*b/(c+d*x)^(3/2)]/d+1/3*I*(I*b/(c+d*x)^(3/2))^(2/3)*(c+d*x)*Gamma[-2/3,I*b/(c+d*x)^(3/2)]/(E^(I*a)*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:321
  public void test0048() {
    check( //
        "Integrate[(c*e+d*e*x)^(1/3)*Sin[a+b/(c+d*x)^(1/3)], x]", //
        "-1/8*b^3*(e*(c+d*x))^(1/3)*Cos[a+b/(c+d*x)^(1/3)]/d+1/4*b*(c+d*x)^(2/3)*(e*(c+d*x))^(1/3)*Cos[a+b/(c+d*x)^(1/3)]/d-1/8*b^4*(e*(c+d*x))^(1/3)*Cos[a]*SinIntegral[b/(c+d*x)^(1/3)]/(d*(c+d*x)^(1/3))-1/8*b^4*(e*(c+d*x))^(1/3)*CosIntegral[b/(c+d*x)^(1/3)]*Sin[a]/(d*(c+d*x)^(1/3))-1/8*b^2*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/d+3/4*(c+d*x)*(e*(c+d*x))^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/d");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:430
  public void test0049() {
    check( //
        "Integrate[x^3*(c*Sin[a+b*x^2]^3)^(1/3), x]", //
        "1/2*(c*Sin[a+b*x^2]^3)^(1/3)/b^2-1/2*x^2*Cot[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:454
  public void test0050() {
    check( //
        "Integrate[x^3*(c*Sin[a+b*x]^3)^(2/3), x]", //
        "-3/8*(c*Sin[a+b*x]^3)^(2/3)/b^4+3/4*x^2*(c*Sin[a+b*x]^3)^(2/3)/b^2+3/4*x*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b^3-1/2*x^3*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b-3/8*x^2*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)/b^2+1/8*x^4*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:29
  public void test0051() {
    check( //
        "Integrate[1/(a+a*Sin[x])^2, x]", //
        "-1/3*Cos[x]/(a+a*Sin[x])^2-1/3*Cos[x]/(a^2+a^2*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:50
  public void test0052() {
    check( //
        "Integrate[Sin[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-12/35*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-4/5*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/7*a*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+8/35*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:66
  public void test0053() {
    check( //
        "Integrate[Csc[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:84
  public void test0054() {
    check( //
        "Integrate[Csc[c+d*x]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:198
  public void test0055() {
    check( //
        "Integrate[Csc[e+f*x]^3*(a+b*Sin[e+f*x]), x]", //
        "-1/2*a*ArcTanh[Cos[e+f*x]]/f-b*Cot[e+f*x]/f-1/2*a*Cot[e+f*x]*Csc[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:216
  public void test0056() {
    check( //
        "Integrate[Csc[e+f*x]^4*(a+b*Sin[e+f*x])^3, x]", //
        "-1/2*b*(3*a^2+2*b^2)*ArcTanh[Cos[e+f*x]]/f-1/3*a*(2*a^2+9*b^2)*Cot[e+f*x]/f-7/6*a^2*b*Cot[e+f*x]*Csc[e+f*x]/f-1/3*a^2*Cot[e+f*x]*Csc[e+f*x]^2*(a+b*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:234
  public void test0057() {
    check( //
        "Integrate[1/(a+b*Sin[x])^2, x]", //
        "2*a*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(3/2)+b*Cos[x]/((a^2-b^2)*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:307
  public void test0058() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "1/2*a^2*c*x-1/3*a^2*c*Cos[e+f*x]^3/f+1/2*a^2*c*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:341
  public void test0059() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^2, x]", //
        "c^2*x/a^2-2/3*a*c^2*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^3)+2*c^2*Cos[e+f*x]/(f*(a^2+a^2*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:357
  public void test0060() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^5), x]", //
        "1/9*Sec[e+f*x]^5/(a^3*c^3*f*(c-c*Sin[e+f*x])^2)+1/9*Sec[e+f*x]^5/(a^3*f*(c^5-c^5*Sin[e+f*x]))+2/3*Tan[e+f*x]/(a^3*c^5*f)+4/9*Tan[e+f*x]^3/(a^3*c^5*f)+2/15*Tan[e+f*x]^5/(a^3*c^5*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:377
  public void test0061() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(7/2))-3/4*a^2*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(3/2))+3/4*a^2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(5/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:395
  public void test0062() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x]), x]", //
        "-2*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:411
  public void test0063() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-2/5*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:431
  public void test0064() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-2*a^2*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-a*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:447
  public void test0065() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/6*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(13/2))+1/60*a^3*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]])-1/15*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*(c-c*Sin[e+f*x])^(11/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:465
  public void test0066() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "2*c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:481
  public void test0067() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/2*c*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:522
  public void test0068() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*Sin[e+f*x])^2, x]", //
        "-a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x]))+2*a*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((c+d)*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:560
  public void test0069() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c+d*Sin[e+f*x])), x]", //
        "-1/3*(c-4*d)*Cos[e+f*x]/(a^2*(c-d)^2*f*(1+Sin[e+f*x]))-1/3*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])^2)+2*d^2*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(a^2*(c-d)^2*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:634
  public void test0070() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x]), x]", //
        "-2/5*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-8/15*a^2*(5*c+3*d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/15*a*(5*c+3*d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:652
  public void test0071() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/((c-d)*f*Sqrt[a])+2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[d]/((c-d)*f*Sqrt[a]*Sqrt[c+d])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:77
  public void test0072() {
    check( //
        "Integrate[Cos[a+b*x]^5*Sin[a+b*x]^2, x]", //
        "1/3*Sin[a+b*x]^3/b-2/5*Sin[a+b*x]^5/b+1/7*Sin[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:93
  public void test0073() {
    check( //
        "Integrate[Cos[a+b*x]^5*Sin[a+b*x]^3, x]", //
        "-1/6*Cos[a+b*x]^6/b+1/8*Cos[a+b*x]^8/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:109
  public void test0074() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[a+b*x]^4, x]", //
        "1/5*Sin[a+b*x]^5/b-1/7*Sin[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:125
  public void test0075() {
    check( //
        "Integrate[Cos[a+b*x]^7*Sin[a+b*x]^5, x]", //
        "-1/8*Cos[a+b*x]^8/b+1/5*Cos[a+b*x]^10/b-1/12*Cos[a+b*x]^12/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:141
  public void test0076() {
    check( //
        "Integrate[Sec[a+b*x]^10*Sin[a+b*x]^5, x]", //
        "1/5*Sec[a+b*x]^5/b-2/7*Sec[a+b*x]^7/b+1/9*Sec[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:159
  public void test0077() {
    check( //
        "Integrate[Sec[a+b*x]^5/Sin[a+b*x], x]", //
        "Log[Tan[a+b*x]]/b+Tan[a+b*x]^2/b+1/4*Tan[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:191
  public void test0078() {
    check( //
        "Integrate[Cos[a+b*x]^4/Sin[a+b*x]^4, x]", //
        "x+Cot[a+b*x]/b-1/3*Cot[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:207
  public void test0079() {
    check( //
        "Integrate[Cos[a+b*x]^2/Sin[a+b*x]^5, x]", //
        "1/8*ArcTanh[Cos[a+b*x]]/b+1/8*Cot[a+b*x]*Csc[a+b*x]/b-1/4*Cot[a+b*x]*Csc[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:227
  public void test0080() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(9/2)*Sin[a+b*x]^2, x]", //
        "28/585*d^3*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/b+4/117*d*(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]/b-2/13*(d*Cos[a+b*x])^(11/2)*Sin[a+b*x]/(b*d)+28/195*d^4*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:305
  public void test0081() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(5/2)*(c*Sin[a+b*x])^(1/2), x]", //
        "1/3*d*(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2)/(b*c)+1/2*d^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:321
  public void test0082() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(3/2)/(d*Cos[a+b*x])^(11/2), x]", //
        "2/9*c*Sqrt[c*Sin[a+b*x]]/(b*d*(d*Cos[a+b*x])^(9/2))-2/45*c*Sqrt[c*Sin[a+b*x]]/(b*d^3*(d*Cos[a+b*x])^(5/2))-8/45*c*Sqrt[c*Sin[a+b*x]]/(b*d^5*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:337
  public void test0083() {
    check( //
        "Integrate[Sin[x]^(5/2)/Sqrt[Cos[x]], x]", //
        "-3/4*ArcTan[1-Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]]/Sqrt[2]+3/4*ArcTan[1+Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]]/Sqrt[2]+3/8*Log[1-Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]+Tan[x]]/Sqrt[2]-3/8*Log[1+Sqrt[2]*Sqrt[Sin[x]]/Sqrt[Cos[x]]+Tan[x]]/Sqrt[2]-1/2*Sin[x]^(3/2)*Sqrt[Cos[x]]");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:492
  public void test0084() {
    check( //
        "Integrate[Sin[e+f*x]^2/Sqrt[b*Sec[e+f*x]], x]", //
        "-2/5*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))+4/5*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:508
  public void test0085() {
    check( //
        "Integrate[Csc[e+f*x]^4/(b*Sec[e+f*x])^(3/2), x]", //
        "1/6*Csc[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])-1/3*Csc[e+f*x]^3/(b*f*Sqrt[b*Sec[e+f*x]])-1/6*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:528
  public void test0086() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(5/2)*Sqrt[b*Sec[e+f*x]], x]", //
        "-1/2*a*b*(a*Sin[e+f*x])^(3/2)/(f*Sqrt[b*Sec[e+f*x]])-3/4*a^(5/2)*ArcTan[1-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])+3/4*a^(5/2)*ArcTan[1+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])+3/8*a^(5/2)*Log[Sqrt[a]-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])-3/8*a^(5/2)*Log[Sqrt[a]+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:546
  public void test0087() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(1/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "ArcTan[1-Sqrt[2]*Sqrt[b*Cos[e+f*x]]/(Sqrt[b]*Sqrt[Sin[e+f*x]])]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-ArcTan[1+Sqrt[2]*Sqrt[b*Cos[e+f*x]]/(Sqrt[b]*Sqrt[Sin[e+f*x]])]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-1/2*Log[Sqrt[b]+Cot[e+f*x]*Sqrt[b]-Sqrt[2]*Sqrt[b*Cos[e+f*x]]/Sqrt[Sin[e+f*x]]]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+1/2*Log[Sqrt[b]+Cot[e+f*x]*Sqrt[b]+Sqrt[2]*Sqrt[b*Cos[e+f*x]]/Sqrt[Sin[e+f*x]]]*Sqrt[b]/(f*Sqrt[2]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:577
  public void test0088() {
    check( //
        "Integrate[(b*Sec[e+f*x])^n*Sin[e+f*x]^5, x]", //
        "-b^5*(b*Sec[e+f*x])^(-5+n)/(f*(5-n))+2*b^3*(b*Sec[e+f*x])^(-3+n)/(f*(3-n))-b*(b*Sec[e+f*x])^(-1+n)/(f*(1-n))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:28
  public void test0089() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "5/8*a^2*x-5/12*a^2*Cos[c+d*x]^3/d+5/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d-1/4*Cos[c+d*x]^3*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:44
  public void test0090() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "-4*a^3*Log[1-Sin[c+d*x]]/d-3*a^3*Sin[c+d*x]/d-1/2*a^3*Sin[c+d*x]^2/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:60
  public void test0091() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+a*Sin[c+d*x])^8, x]", //
        "1155/8*a^8*x-385/4*a^8*Cos[c+d*x]^3/d+1155/8*a^8*Cos[c+d*x]*Sin[c+d*x]/d+2/3*a^15*Cos[c+d*x]^11/(d*(a-a*Sin[c+d*x])^7)-22/3*a^13*Cos[c+d*x]^9/(d*(a-a*Sin[c+d*x])^5)-66*a^14*Cos[c+d*x]^7/(d*(a^2-a^2*Sin[c+d*x])^3)-231/4*a^16*Cos[c+d*x]^5/(d*(a^8-a^8*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:110
  public void test0092() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x])^8, x]", //
        "-1/17*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^8)-3/85*Sec[c+d*x]/(a*d*(a+a*Sin[c+d*x])^7)-24/1105*Sec[c+d*x]/(a^2*d*(a+a*Sin[c+d*x])^6)-168/12155*Sec[c+d*x]/(a^3*d*(a+a*Sin[c+d*x])^5)-112/12155*Sec[c+d*x]/(d*(a^2+a^2*Sin[c+d*x])^4)-16/2431*Sec[c+d*x]/(a^2*d*(a^2+a^2*Sin[c+d*x])^3)-64/12155*Sec[c+d*x]/(d*(a^4+a^4*Sin[c+d*x])^2)-64/12155*Sec[c+d*x]/(d*(a^8+a^8*Sin[c+d*x]))+128/12155*Tan[c+d*x]/(a^8*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:130
  public void test0093() {
    check( //
        "Integrate[Sec[c+d*x]^6*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-63/128*a^2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-21/80*a^2*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-63/128*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[a]/(d*Sqrt[2])+21/32*a*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+3/10*a*Sec[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+1/5*Sec[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:196
  public void test0094() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-15/32*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-1/4*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-15/32*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+5/8*Sec[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:212
  public void test0095() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-1/6*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))-35/128*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-7/48*Sec[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-35/128*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+35/96*Sec[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:232
  public void test0096() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/(e*Cos[c+d*x])^(3/2), x]", //
        "4*a^4*(e*Cos[c+d*x])^(3/2)/(d*e^3*(a^2-a^2*Sin[c+d*x]))-6*a^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:248
  public void test0097() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4*Sqrt[e*Cos[c+d*x]], x]", //
        "-22/9*a^4*(e*Cos[c+d*x])^(3/2)/(d*e)-2/9*a*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^3/(d*e)-10/21*(e*Cos[c+d*x])^(3/2)*(a^2+a^2*Sin[c+d*x])^2/(d*e)-22/21*(e*Cos[c+d*x])^(3/2)*(a^4+a^4*Sin[c+d*x])/(d*e)+22/3*a^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:266
  public void test0098() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])), x]", //
        "10/21*Sin[c+d*x]/(a*d*e*(e*Cos[c+d*x])^(3/2))+(-2/7)/(d*e*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x]))+10/21*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a*d*e^2*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:282
  public void test0099() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x])^3, x]", //
        "-4/3*e*(e*Cos[c+d*x])^(5/2)/(a*d*(a+a*Sin[c+d*x])^2)-10/3*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[e*Cos[c+d*x]])-10/3*e^3*Sqrt[e*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:302
  public void test0100() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-1/2*a*(e*Cos[c+d*x])^(5/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])+3/4*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/d-3/4*e^(3/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+3/4*e^(3/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:318
  public void test0101() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-1/4*a*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^(3/2)/(d*e)-77/96*a^3*(e*Cos[c+d*x])^(5/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])-11/24*a^2*(e*Cos[c+d*x])^(5/2)*Sqrt[a+a*Sin[c+d*x]]/(d*e)+77/64*a^2*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/d-77/64*a^2*e^(3/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+77/64*a^2*e^(3/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:336
  public void test0102() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/2*e*(e*Cos[c+d*x])^(5/2)/(a*d*Sqrt[a+a*Sin[c+d*x]])+5/4*e^3*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)-5/4*e^(7/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*(1+Cos[c+d*x]+Sin[c+d*x]))+5/4*e^(7/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:380
  public void test0103() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+a*Sin[c+d*x])^m, x]", //
        "8*(a+a*Sin[c+d*x])^(4+m)/(a^4*d*(4+m))-12*(a+a*Sin[c+d*x])^(5+m)/(a^5*d*(5+m))+6*(a+a*Sin[c+d*x])^(6+m)/(a^6*d*(6+m))-(a+a*Sin[c+d*x])^(7+m)/(a^7*d*(7+m))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:425
  public void test0104() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Sin[c+d*x]), x]", //
        "3/8*a*x-1/5*b*Cos[c+d*x]^5/d+3/8*a*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:441
  public void test0105() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+b*Sin[c+d*x])^2, x]", //
        "1/5*a*b*Sec[c+d*x]^3/d+1/5*Sec[c+d*x]^5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d+1/5*(4*a^2-b^2)*Tan[c+d*x]/d+1/15*(4*a^2-b^2)*Tan[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:457
  public void test0106() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x])^8, x]", //
        "-1/9*(a^2-b^2)*(a+b*Sin[c+d*x])^9/(b^3*d)+1/5*a*(a+b*Sin[c+d*x])^10/(b^3*d)-1/11*(a+b*Sin[c+d*x])^11/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:491
  public void test0107() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+b*Sin[c+d*x])^2, x]", //
        "-x/b^2-Cos[c+d*x]/(b*d*(a+b*Sin[c+d*x]))+2*a*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^2*d*Sqrt[a^2-b^2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:597
  public void test0108() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]], x]", //
        "-2/3*b*(e*Cos[c+d*x])^(3/2)/(d*e)+2*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:613
  public void test0109() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]], x]", //
        "-2/105*b*(57*a^2+20*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e)-22/35*a*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e)-2/7*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^2/(d*e)+2/5*a*(5*a^2+6*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:753
  public void test0110() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+b*Sin[c+d*x])^m, x]", //
        "-(a^2-b^2)^3*(a+b*Sin[c+d*x])^(1+m)/(b^7*d*(1+m))+6*a*(a^2-b^2)^2*(a+b*Sin[c+d*x])^(2+m)/(b^7*d*(2+m))-3*(5*a^4-6*a^2*b^2+b^4)*(a+b*Sin[c+d*x])^(3+m)/(b^7*d*(3+m))+4*a*(5*a^2-3*b^2)*(a+b*Sin[c+d*x])^(4+m)/(b^7*d*(4+m))-3*(5*a^2-b^2)*(a+b*Sin[c+d*x])^(5+m)/(b^7*d*(5+m))+6*a*(a+b*Sin[c+d*x])^(6+m)/(b^7*d*(6+m))-(a+b*Sin[c+d*x])^(7+m)/(b^7*d*(7+m))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:40
  public void test0111() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3*Tan[c+d*x]^6, x]", //
        "-23/2*a^3*x+136/5*a^3*Cos[c+d*x]/d-136/15*a^3*Cos[c+d*x]^3/d+23/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d+1/5*a^6*Cos[c+d*x]*Sin[c+d*x]^5/(d*(a-a*Sin[c+d*x])^3)-13/15*a^5*Cos[c+d*x]*Sin[c+d*x]^4/(d*(a-a*Sin[c+d*x])^2)+23/3*a^6*Cos[c+d*x]*Sin[c+d*x]^3/(d*(a^3-a^3*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:58
  public void test0112() {
    check( //
        "Integrate[Tan[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "5/16*ArcTanh[Sin[c+d*x]]/(a*d)-5/16*Sec[c+d*x]*Tan[c+d*x]/(a*d)+5/24*Sec[c+d*x]*Tan[c+d*x]^3/(a*d)-1/6*Sec[c+d*x]*Tan[c+d*x]^5/(a*d)+1/6*Tan[c+d*x]^6/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:91
  public void test0113() {
    check( //
        "Integrate[Cot[c+d*x]^7/(a+a*Sin[c+d*x])^3, x]", //
        "1/3*Csc[c+d*x]^3/(a^3*d)-3/4*Csc[c+d*x]^4/(a^3*d)+3/5*Csc[c+d*x]^5/(a^3*d)-1/6*Csc[c+d*x]^6/(a^3*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:113
  public void test0114() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*Tan[e+f*x]^2, x]", //
        "7/3*Sec[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-2/3*Sec[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(a*f)+11/3*a^2*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:12
  public void test0115() {
    check( //
        "Integrate[(c+d*x)^4*Sin[a+b*x], x]", //
        "-24*d^4*Cos[a+b*x]/b^5+12*d^2*(c+d*x)^2*Cos[a+b*x]/b^3-(c+d*x)^4*Cos[a+b*x]/b-24*d^3*(c+d*x)*Sin[a+b*x]/b^4+4*d*(c+d*x)^3*Sin[a+b*x]/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:28
  public void test0116() {
    check( //
        "Integrate[(c+d*x)^3*Sin[a+b*x]^3, x]", //
        "40/9*d^2*(c+d*x)*Cos[a+b*x]/b^3-2/3*(c+d*x)^3*Cos[a+b*x]/b-40/9*d^3*Sin[a+b*x]/b^4+2*d*(c+d*x)^2*Sin[a+b*x]/b^2+2/9*d^2*(c+d*x)*Cos[a+b*x]*Sin[a+b*x]^2/b^3-1/3*(c+d*x)^3*Cos[a+b*x]*Sin[a+b*x]^2/b-2/27*d^3*Sin[a+b*x]^3/b^4+1/3*d*(c+d*x)^2*Sin[a+b*x]^3/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:177
  public void test0117() {
    check( //
        "Integrate[x*(a+a*Sin[e+f*x])^(3/2), x]", //
        "16/3*a*Sqrt[a+a*Sin[e+f*x]]/f^2-8/3*a*x*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f-4/3*a*x*Cos[1/4*Pi+1/2*e+1/2*f*x]*Sin[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f+8/9*a*Sin[1/4*Pi+1/2*e+1/2*f*x]^2*Sqrt[a+a*Sin[e+f*x]]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:215
  public void test0118() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])/(c+d*x), x]", //
        "a*Log[c+d*x]/d+b*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d+b*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:274
  public void test0119() {
    check( //
        "Integrate[(e+f*x)*Csc[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-2*(e+f*x)*ArcTanh[E^(I*(c+d*x))]/(a*d)+(e+f*x)*Cot[1/4*Pi+1/2*c+1/2*d*x]/(a*d)-2*f*Log[Sin[1/4*Pi+1/2*c+1/2*d*x]]/(a*d^2)+I*f*PolyLog[2,-E^(I*(c+d*x))]/(a*d^2)-I*f*PolyLog[2,E^(I*(c+d*x))]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:364
  public void test0120() {
    check( //
        "Integrate[(e+f*x)*Cos[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "1/4*f*x/(a*d)+f*Cos[c+d*x]/(a*d^2)+(e+f*x)*Sin[c+d*x]/(a*d)-1/4*f*Cos[c+d*x]*Sin[c+d*x]/(a*d^2)-1/2*(e+f*x)*Sin[c+d*x]^2/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:451
  public void test0121() {
    check( //
        "Integrate[Cos[c+d*x]*Cot[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-x/b-ArcTanh[Cos[c+d*x]]/(a*d)+2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a*b*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:469
  public void test0122() {
    check( //
        "Integrate[Cos[c+d*x]^2*Cot[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "-a*x/b^2+2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^2*b^2*d)+b*ArcTanh[Cos[c+d*x]]/(a^2*d)-Cos[c+d*x]/(b*d)-Cot[c+d*x]/(a*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:21
  public void test0123() {
    check( //
        "Integrate[(a+b*x)^2*Sin[c+d*x], x]", //
        "2*b^2*Cos[c+d*x]/d^3-(a+b*x)^2*Cos[c+d*x]/d+2*b*(a+b*x)*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:59
  public void test0124() {
    check( //
        "Integrate[(a+b*x^2)*Sin[c+d*x]/x, x]", //
        "-b*x*Cos[c+d*x]/d+a*Cos[c]*SinIntegral[d*x]+a*CosIntegral[d*x]*Sin[c]+b*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:77
  public void test0125() {
    check( //
        "Integrate[x*Sin[c+d*x]/(a+b*x^2), x]", //
        "-1/2*Cos[c+d*Sqrt[-a]/Sqrt[b]]*SinIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]/b+1/2*Cos[c-d*Sqrt[-a]/Sqrt[b]]*SinIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]/b+1/2*CosIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c-d*Sqrt[-a]/Sqrt[b]]/b+1/2*CosIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c+d*Sqrt[-a]/Sqrt[b]]/b");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:113
  public void test0126() {
    check( //
        "Integrate[(a+b*x^3)^2*Sin[c+d*x]/x^4, x]", //
        "-1/6*a^2*d^3*CosIntegral[d*x]*Cos[c]+2*b^2*Cos[c+d*x]/d^3-1/6*a^2*d*Cos[c+d*x]/x^2-b^2*x^2*Cos[c+d*x]/d+2*a*b*Cos[c]*SinIntegral[d*x]+2*a*b*CosIntegral[d*x]*Sin[c]+1/6*a^2*d^3*SinIntegral[d*x]*Sin[c]-1/3*a^2*Sin[c+d*x]/x^3+1/6*a^2*d^2*Sin[c+d*x]/x+2*b^2*x*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:140
  public void test0127() {
    check( //
        "Integrate[Sin[a+b/x], x]", //
        "-b*CosIntegral[b/x]*Cos[a]+b*SinIntegral[b/x]*Sin[a]+x*Sin[a+b/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:160
  public void test0128() {
    check( //
        "Integrate[Sin[a+b/x^2]/x^2, x]", //
        "-Cos[a]*FresnelS[Sqrt[2/Pi]*Sqrt[b]/x]*Sqrt[1/2*Pi]/Sqrt[b]-FresnelC[Sqrt[2/Pi]*Sqrt[b]/x]*Sin[a]*Sqrt[1/2*Pi]/Sqrt[b]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:252
  public void test0129() {
    check( //
        "Integrate[(e+f*x)^2*Sin[a+b*Sqrt[c+d*x]], x]", //
        "40*f^2*(c+d*x)^(3/2)*Cos[a+b*Sqrt[c+d*x]]/(b^3*d^3)-4*f*(d*e-c*f)*(c+d*x)^(3/2)*Cos[a+b*Sqrt[c+d*x]]/(b*d^3)-2*f^2*(c+d*x)^(5/2)*Cos[a+b*Sqrt[c+d*x]]/(b*d^3)+240*f^2*Sin[a+b*Sqrt[c+d*x]]/(b^6*d^3)-24*f*(d*e-c*f)*Sin[a+b*Sqrt[c+d*x]]/(b^4*d^3)+2*(d*e-c*f)^2*Sin[a+b*Sqrt[c+d*x]]/(b^2*d^3)-120*f^2*(c+d*x)*Sin[a+b*Sqrt[c+d*x]]/(b^4*d^3)+12*f*(d*e-c*f)*(c+d*x)*Sin[a+b*Sqrt[c+d*x]]/(b^2*d^3)+10*f^2*(c+d*x)^2*Sin[a+b*Sqrt[c+d*x]]/(b^2*d^3)-240*f^2*Cos[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b^5*d^3)+24*f*(d*e-c*f)*Cos[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b^3*d^3)-2*(d*e-c*f)^2*Cos[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b*d^3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:278
  public void test0130() {
    check( //
        "Integrate[(e+f*x)^2*Sin[a+b*(c+d*x)^(1/3)], x]", //
        "-120960*f^2*Cos[a+b*(c+d*x)^(1/3)]/(b^9*d^3)+6*(d*e-c*f)^2*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d^3)-720*f*(d*e-c*f)*(c+d*x)^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^5*d^3)+60480*f^2*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^7*d^3)-3*(d*e-c*f)^2*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d^3)+120*f*(d*e-c*f)*(c+d*x)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d^3)-5040*f^2*(c+d*x)^(4/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^5*d^3)-6*f*(d*e-c*f)*(c+d*x)^(5/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d^3)+168*f^2*(c+d*x)^2*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d^3)-3*f^2*(c+d*x)^(8/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d^3)+720*f*(d*e-c*f)*Sin[a+b*(c+d*x)^(1/3)]/(b^6*d^3)-120960*f^2*(c+d*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^8*d^3)+6*(d*e-c*f)^2*(c+d*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d^3)-360*f*(d*e-c*f)*(c+d*x)^(2/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d^3)+20160*f^2*(c+d*x)*Sin[a+b*(c+d*x)^(1/3)]/(b^6*d^3)+30*f*(d*e-c*f)*(c+d*x)^(4/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d^3)-1008*f^2*(c+d*x)^(5/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d^3)+24*f^2*(c+d*x)^(7/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d^3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:304
  public void test0131() {
    check( //
        "Integrate[(c*e+d*e*x)^(4/3)*Sin[a+b*(c+d*x)^(1/3)], x]", //
        "2160*e*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^7*d*(c+d*x)^(1/3))-1080*e*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^5*d)+90*e*(c+d*x)*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d)-3*e*(c+d*x)^(5/3)*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d)+2160*e*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^6*d)-360*e*(c+d*x)^(2/3)*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d)+18*e*(c+d*x)^(4/3)*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:322
  public void test0132() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(1/3), x]", //
        "3/2*b*(c+d*x)^(2/3)*Cos[a+b/(c+d*x)^(1/3)]/(d*(e*(c+d*x))^(1/3))+3/2*b^2*(c+d*x)^(1/3)*Cos[a]*SinIntegral[b/(c+d*x)^(1/3)]/(d*(e*(c+d*x))^(1/3))+3/2*b^2*(c+d*x)^(1/3)*CosIntegral[b/(c+d*x)^(1/3)]*Sin[a]/(d*(e*(c+d*x))^(1/3))+3/2*(c+d*x)*Sin[a+b/(c+d*x)^(1/3)]/(d*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:341
  public void test0133() {
    check( //
        "Integrate[x^3*Sin[a+b*(c+d*x)^n], x]", //
        "-1/2*I*E^(I*a)*c^3*(c+d*x)*Gamma[1/n,-I*b*(c+d*x)^n]/(d^4*n*(-I*b*(c+d*x)^n)^(1/n))+1/2*I*c^3*(c+d*x)*Gamma[1/n,I*b*(c+d*x)^n]/(E^(I*a)*d^4*n*(I*b*(c+d*x)^n)^(1/n))+3/2*I*E^(I*a)*c^2*(c+d*x)^2*Gamma[2/n,-I*b*(c+d*x)^n]/(d^4*n*(-I*b*(c+d*x)^n)^(2/n))-3/2*I*c^2*(c+d*x)^2*Gamma[2/n,I*b*(c+d*x)^n]/(E^(I*a)*d^4*n*(I*b*(c+d*x)^n)^(2/n))-3/2*I*E^(I*a)*c*(c+d*x)^3*Gamma[3/n,-I*b*(c+d*x)^n]/(d^4*n*(-I*b*(c+d*x)^n)^(3/n))+3/2*I*c*(c+d*x)^3*Gamma[3/n,I*b*(c+d*x)^n]/(E^(I*a)*d^4*n*(I*b*(c+d*x)^n)^(3/n))+1/2*I*E^(I*a)*(c+d*x)^4*Gamma[4/n,-I*b*(c+d*x)^n]/(d^4*n*(-I*b*(c+d*x)^n)^(4/n))-1/2*I*(c+d*x)^4*Gamma[4/n,I*b*(c+d*x)^n]/(E^(I*a)*d^4*n*(I*b*(c+d*x)^n)^(4/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:431
  public void test0134() {
    check( //
        "Integrate[x^2*(c*Sin[a+b*x^2]^3)^(1/3), x]", //
        "-1/2*x*Cot[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)/b+1/2*Cos[a]*Csc[a+b*x^2]*FresnelC[x*Sqrt[2/Pi]*Sqrt[b]]*(c*Sin[a+b*x^2]^3)^(1/3)*Sqrt[1/2*Pi]/b^(3/2)-1/2*Csc[a+b*x^2]*FresnelS[x*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*(c*Sin[a+b*x^2]^3)^(1/3)*Sqrt[1/2*Pi]/b^(3/2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:455
  public void test0135() {
    check( //
        "Integrate[x^2*(c*Sin[a+b*x]^3)^(2/3), x]", //
        "1/2*x*(c*Sin[a+b*x]^3)^(2/3)/b^2+1/4*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b^3-1/2*x^2*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b-1/4*x*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)/b^2+1/6*x^3*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:12
  public void test0136() {
    check( //
        "Integrate[Sin[e+f*x]^3*(a+a*Sin[e+f*x])^2, x]", //
        "3/4*a^2*x-2*a^2*Cos[e+f*x]/f+a^2*Cos[e+f*x]^3/f-1/5*a^2*Cos[e+f*x]^5/f-3/4*a^2*Cos[e+f*x]*Sin[e+f*x]/f-1/2*a^2*Cos[e+f*x]*Sin[e+f*x]^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:30
  public void test0137() {
    check( //
        "Integrate[Csc[x]/(a+a*Sin[x])^2, x]", //
        "-ArcTanh[Cos[x]]/a^2+4/3*Cos[x]/(a^2*(1+Sin[x]))+1/3*Cos[x]/(a+a*Sin[x])^2");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:51
  public void test0138() {
    check( //
        "Integrate[Sin[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-2/5*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-14/15*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+4/15*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:67
  public void test0139() {
    check( //
        "Integrate[Csc[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-7/4*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-7/4*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:85
  public void test0140() {
    check( //
        "Integrate[Csc[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])-ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:101
  public void test0141() {
    check( //
        "Integrate[Csc[c+d*x]/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)+1/4*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))+11/16*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))+43/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:199
  public void test0142() {
    check( //
        "Integrate[Csc[e+f*x]^4*(a+b*Sin[e+f*x]), x]", //
        "-1/2*b*ArcTanh[Cos[e+f*x]]/f-a*Cot[e+f*x]/f-1/3*a*Cot[e+f*x]^3/f-1/2*b*Cot[e+f*x]*Csc[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:217
  public void test0143() {
    check( //
        "Integrate[Csc[e+f*x]^5*(a+b*Sin[e+f*x])^3, x]", //
        "-3/8*a*(a^2+4*b^2)*ArcTanh[Cos[e+f*x]]/f-b*(2*a^2+b^2)*Cot[e+f*x]/f-3/8*a*(a^2+4*b^2)*Cot[e+f*x]*Csc[e+f*x]/f-3/4*a^2*b*Cot[e+f*x]*Csc[e+f*x]^2/f-1/4*a^2*Cot[e+f*x]*Csc[e+f*x]^3*(a+b*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:308
  public void test0144() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x]), x]", //
        "-3*a^2*x/c+3*a^2*Cos[e+f*x]/(c*f)+2*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:324
  public void test0145() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^5, x]", //
        "1/9*a^3*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+1/63*a^3*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:358
  public void test0146() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^6), x]", //
        "1/11*Sec[e+f*x]^5/(a^3*f*(c^2-c^2*Sin[e+f*x])^3)+8/99*Sec[e+f*x]^5/(a^3*f*(c^3-c^3*Sin[e+f*x])^2)+8/99*Sec[e+f*x]^5/(a^3*f*(c^6-c^6*Sin[e+f*x]))+16/33*Tan[e+f*x]/(a^3*c^6*f)+32/99*Tan[e+f*x]^3/(a^3*c^6*f)+16/165*Tan[e+f*x]^5/(a^3*c^6*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:378
  public void test0147() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(9/2))-1/4*a^2*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(5/2))+1/16*a^2*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))+1/16*a^2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(7/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:396
  public void test0148() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*f*Sqrt[2]*Sqrt[c])-Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:412
  public void test0149() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/6*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^3*c^2*f)-1/5*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*c^3*f)+1/4*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*f*Sqrt[2]*Sqrt[c])-1/4*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^3*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:432
  public void test0150() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*(c-c*Sin[e+f*x])^(3/2))+a^2*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:448
  public void test0151() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(9/2), x]", //
        "-3/28*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(9/2)/f-1/8*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(9/2)/f-1/35*a^4*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/14*a^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:466
  public void test0152() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "c*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:482
  public void test0153() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/4*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])-1/4*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+1/4*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:523
  public void test0154() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*Sin[e+f*x])^3, x]", //
        "a*(2*c-d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((c+d)*(c^2-d^2)^(3/2)*f)-1/2*a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])^2)-1/2*a*(c-2*d)*Cos[e+f*x]/((c-d)*(c+d)^2*f*(c+d*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:653
  public void test0155() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^2), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/((c-d)^2*f*Sqrt[a])+(3*c+d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[d]/((c-d)^2*(c+d)^(3/2)*f*Sqrt[a])+d*Cos[e+f*x]/((c^2-d^2)*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:673
  public void test0156() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(5/2), x]", //
        "-5/8*(c+d)^3*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[d])-5/12*a*(c+d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/3*a*Cos[e+f*x]*(c+d*Sin[e+f*x])^(5/2)/(f*Sqrt[a+a*Sin[e+f*x]])-5/8*a*(c+d)^2*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:689
  public void test0157() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c+d*Sin[e+f*x])^(3/2), x]", //
        "-1/64*a^(5/2)*(c+d)^2*(3*c^2-26*c*d+163*d^2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(5/2)*f)-1/96*a^3*(3*c^2-26*c*d+163*d^2)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(d^2*f*Sqrt[a+a*Sin[e+f*x]])+1/24*a^3*(3*c-17*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(5/2)/(d^2*f*Sqrt[a+a*Sin[e+f*x]])-1/4*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(d*f)-1/64*a^3*(c+d)*(3*c^2-26*c*d+163*d^2)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:707
  public void test0158() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(c+d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2]*Sqrt[c-d])-1/2*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:807
  public void test0159() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])^3*(c+d*Sin[e+f*x]), x]", //
        "1/8*(8*a^3*c+12*a*b^2*c+12*a^2*b*d+3*b^3*d)*x-1/6*(16*a^2*b*c+4*b^3*c+3*a^3*d+12*a*b^2*d)*Cos[e+f*x]/f-1/24*b*(20*a*b*c+6*a^2*d+9*b^2*d)*Cos[e+f*x]*Sin[e+f*x]/f-1/12*(4*b*c+3*a*d)*Cos[e+f*x]*(a+b*Sin[e+f*x])^2/f-1/4*d*Cos[e+f*x]*(a+b*Sin[e+f*x])^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:927
  public void test0160() {
    check( //
        "Integrate[1/((a+b*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(1/2)), x]", //
        "2*b*(c-d)*EllipticE[ArcSin[Sqrt[a+b]*Sqrt[c+d*Sin[e+f*x]]/(Sqrt[c+d]*Sqrt[a+b*Sin[e+f*x]])],(a-b)*(c+d)/((a+b)*(c-d))]*Sec[e+f*x]*(a+b*Sin[e+f*x])*Sqrt[c+d]*Sqrt[-(b*c-a*d)*(1-Sin[e+f*x])/((c+d)*(a+b*Sin[e+f*x]))]*Sqrt[(b*c-a*d)*(1+Sin[e+f*x])/((c-d)*(a+b*Sin[e+f*x]))]/((a-b)*(b*c-a*d)^2*f*Sqrt[a+b])+2*EllipticF[ArcSin[Sqrt[c+d]*Sqrt[a+b*Sin[e+f*x]]/(Sqrt[a+b]*Sqrt[c+d*Sin[e+f*x]])],(a+b)*(c-d)/((a-b)*(c+d))]*Sec[e+f*x]*(c+d*Sin[e+f*x])*Sqrt[a+b]*Sqrt[(b*c-a*d)*(1-Sin[e+f*x])/((a+b)*(c+d*Sin[e+f*x]))]*Sqrt[-(b*c-a*d)*(1+Sin[e+f*x])/((a-b)*(c+d*Sin[e+f*x]))]/((a-b)*(b*c-a*d)*f*Sqrt[c+d])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:24
  public void test0161() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/7*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(9/2)/(c*f)-4/105*a^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])-2/21*a*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:40
  public void test0162() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*(c-c*Sin[e+f*x])^(3/2))+3/2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^2*f*Sqrt[c-c*Sin[e+f*x]])+12*a^3*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:56
  public void test0163() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/10*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c*f*(c-c*Sin[e+f*x])^(11/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:74
  public void test0164() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-10*c^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-10/3*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-5/4*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-80*c^5*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-40*c^4*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:134
  public void test0165() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "-28/45*a^2*(g*Cos[e+f*x])^(5/2)/(c*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+14/15*a^2*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+4/9*a*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*(c-c*Sin[e+f*x])^(7/2))-14/15*a^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:150
  public void test0166() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/Sqrt[c-c*Sin[e+f*x]], x]", //
        "-10/21*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-2/9*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-22/9*a^4*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/3*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-22/21*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:168
  public void test0167() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-4*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*(a+a*Sin[e+f*x])^(3/2))-154/15*c^3*(g*Cos[e+f*x])^(5/2)/(a*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-154/5*c^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-22/5*c^2*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(a*f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:213
  public void test0168() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(1-2*m)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(-1+m), x]", //
        "-g*Log[1-Sin[e+f*x]]*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^m/(c*f*(g*Cos[e+f*x])^(2*m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:264
  public void test0169() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^3, x]", //
        "-1/4*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:298
  public void test0170() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "Log[Sin[c+d*x]]/(a^3*d)-Log[1+Sin[c+d*x]]/(a^3*d)+1/2/(a*d*(a+a*Sin[c+d*x])^2)+1/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:322
  public void test0171() {
    check( //
        "Integrate[Cos[c+d*x]*Sin[c+d*x]^n*(a+a*Sin[c+d*x]), x]", //
        "a*Sin[c+d*x]^(1+n)/(d*(1+n))+a*Sin[c+d*x]^(2+n)/(d*(2+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:349
  public void test0172() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "5/8*a^2*ArcTanh[Cos[c+d*x]]/d-2/3*a^2*Cot[c+d*x]^3/d-3/8*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:368
  public void test0173() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/2*x/a+Cos[c+d*x]/(a*d)-1/3*Cos[c+d*x]^3/(a*d)-1/2*Cos[c+d*x]*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:404
  public void test0174() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-64/315*a^3*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-2/9*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d-16/105*a^2*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/21*a*Cos[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:422
  public void test0175() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "3*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)-Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:470
  public void test0176() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "3/64*a^2*x-2/5*a^2*Cos[c+d*x]^5/d+3/7*a^2*Cos[c+d*x]^7/d-1/9*a^2*Cos[c+d*x]^9/d+3/64*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/32*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-1/8*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-1/4*a^2*Cos[c+d*x]^5*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:486
  public void test0177() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "27/128*a^3*x-9/80*a^3*Cos[c+d*x]^5/d+27/128*a^3*Cos[c+d*x]*Sin[c+d*x]/d+9/64*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d-3/56*a*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2/d-1/8*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^3/d-9/112*Cos[c+d*x]^5*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:504
  public void test0178() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/8*x/a+1/3*Cos[c+d*x]^3/(a*d)-1/5*Cos[c+d*x]^5/(a*d)+1/8*Cos[c+d*x]*Sin[c+d*x]/(a*d)-1/4*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:520
  public void test0179() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "-3/2*ArcTanh[Cos[c+d*x]]/(a^2*d)+2*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:541
  public void test0180() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-64/3465*a^3*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-16/693*a^2*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/99*a*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-2/11*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:557
  public void test0181() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-165/128*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/5*Cot[c+d*x]*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^(3/2)/d+91/128*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+73/64*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+31/80*a^2*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])-3/40*a*Cot[c+d*x]*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:575
  public void test0182() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "3*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:603
  public void test0183() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cos[c+d*x]^6/d+1/8*a*Cos[c+d*x]^8/d+1/5*a*Sin[c+d*x]^5/d-2/7*a*Sin[c+d*x]^7/d+1/9*a*Sin[c+d*x]^9/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:713
  public void test0184() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "-15/4*a^2*x+3/2*a^2*ArcTanh[Cos[c+d*x]]/d-a^2*Cos[c+d*x]/d+1/5*a^2*Cos[c+d*x]^5/d-2*a^2*Cot[c+d*x]/d-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]/d-9/4*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/2*a^2*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:729
  public void test0185() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "-15/16*a^3*x-3*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d+a^3*Cos[c+d*x]^3/d+3/5*a^3*Cos[c+d*x]^5/d-1/7*a^3*Cos[c+d*x]^7/d-a^3*Cot[c+d*x]/d+15/16*a^3*Cos[c+d*x]*Sin[c+d*x]/d-11/8*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d+1/2*a^3*Cos[c+d*x]*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:747
  public void test0186() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/16*x/a+1/5*Cos[c+d*x]^5/(a*d)-1/7*Cos[c+d*x]^7/(a*d)+1/16*Cos[c+d*x]*Sin[c+d*x]/(a*d)+1/24*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)-1/6*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:763
  public void test0187() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^6/(a+a*Sin[c+d*x])^2, x]", //
        "-1/4*ArcTanh[Cos[c+d*x]]/(a^2*d)-2/3*Cot[c+d*x]^3/(a^2*d)-1/5*Cot[c+d*x]^5/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+1/2*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:875
  public void test0188() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "-9/8*x/a^2+2*ArcTanh[Cos[c+d*x]]/(a^2*d)-2*Cos[c+d*x]/(a^2*d)-2/3*Cos[c+d*x]^3/(a^2*d)-Cot[c+d*x]/(a^2*d)+1/8*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/4*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:891
  public void test0189() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "5/2*x/a^3-5/2*ArcTanh[Cos[c+d*x]]/(a^3*d)+3*Cos[c+d*x]/(a^3*d)+3*Cot[c+d*x]/(a^3*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)-1/2*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:951
  public void test0190() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "1/7*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^3)-3/35*Sec[c+d*x]/(a*d*(a+a*Sin[c+d*x])^2)-3/35*Sec[c+d*x]/(d*(a^3+a^3*Sin[c+d*x]))+6/35*Tan[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1014
  public void test0191() {
    check( //
        "Integrate[Csc[c+d*x]^2*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "-2*a^2*ArcTanh[Cos[c+d*x]]/d-10/3*a^2*Cot[c+d*x]/d+2*a^2*Cot[c+d*x]/(d*(1-Sin[c+d*x]))+1/3*a^4*Cot[c+d*x]/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1032
  public void test0192() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/3*Sec[c+d*x]^3/(a*d)-1/5*Sec[c+d*x]^5/(a*d)+1/3*Tan[c+d*x]^3/(a*d)+1/5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1048
  public void test0193() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "-Sec[c+d*x]^3/(a^3*d)+2*Sec[c+d*x]^5/(a^3*d)-11/7*Sec[c+d*x]^7/(a^3*d)+4/9*Sec[c+d*x]^9/(a^3*d)-3/7*Tan[c+d*x]^7/(a^3*d)-4/9*Tan[c+d*x]^9/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1089
  public void test0194() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "1/2*a^5*Sin[c+d*x]^2/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1153
  public void test0195() {
    check( //
        "Integrate[Sec[c+d*x]^9*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "7/256*ArcTanh[Sin[c+d*x]]/(a*d)+1/10*Sec[c+d*x]^10/(a*d)+7/256*Sec[c+d*x]*Tan[c+d*x]/(a*d)+7/384*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+7/480*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)+1/80*Sec[c+d*x]^7*Tan[c+d*x]/(a*d)-1/10*Sec[c+d*x]^9*Tan[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1241
  public void test0196() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "1/5*(A+B)*Sec[c+d*x]^5*(a+a*Sin[c+d*x])/d+1/5*a*(4*A-B)*Tan[c+d*x]/d+1/15*a*(4*A-B)*Tan[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1257
  public void test0197() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/15*a^2*(3*A-2*B)*Sec[c+d*x]^3/d+1/5*(A+B)*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^2/d+1/5*a^2*(3*A-2*B)*Tan[c+d*x]/d+1/15*a^2*(3*A-2*B)*Tan[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1291
  public void test0198() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Sin[c+d*x])/(a+a*Sin[c+d*x])^2, x]", //
        "B*Log[1+Sin[c+d*x]]/(a^2*d)+(-A+B)/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1353
  public void test0199() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]*(a+b*Sin[c+d*x]), x]", //
        "1/8*b*x-1/3*a*Cos[c+d*x]^3/d+1/8*b*Cos[c+d*x]*Sin[c+d*x]/d-1/4*b*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1369
  public void test0200() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^7*(a+b*Sin[c+d*x])^2, x]", //
        "1/16*(a^2+2*b^2)*ArcTanh[Cos[c+d*x]]/d+2/5*a*b*Cot[c+d*x]/d+2/15*a*b*Cot[c+d*x]^3/d+1/16*(a^2+2*b^2)*Cot[c+d*x]*Csc[c+d*x]/d+1/24*(a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]^3/d-1/15*a*b*Cot[c+d*x]*Csc[c+d*x]^4/d-1/6*Cot[c+d*x]*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1415
  public void test0201() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "b*x-3/8*a*ArcTanh[Cos[c+d*x]]/d+b*Cot[c+d*x]/d-1/3*b*Cot[c+d*x]^3/d+3/8*a*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a*Cot[c+d*x]^3*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1432
  public void test0202() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+b*Sin[c+d*x])^3, x]", //
        "3/128*b*(8*a^2+b^2)*x-1/560*a*(2*a^2+61*b^2)*Cos[c+d*x]^5/d+3/128*b*(8*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/64*b*(8*a^2+b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d-1/112*(2*a^2+7*b^2)*Cos[c+d*x]^5*(a+b*Sin[c+d*x])/d-3/56*a*Cos[c+d*x]^5*(a+b*Sin[c+d*x])^2/d-1/8*Cos[c+d*x]^5*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1587
  public void test0203() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^4*(a+b*Sin[c+d*x])^2, x]", //
        "1/1024*(12*a^2+5*b^2)*x-2/7*a*b*Cos[c+d*x]^7/d+4/9*a*b*Cos[c+d*x]^9/d-2/11*a*b*Cos[c+d*x]^11/d+1/1024*(12*a^2+5*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/1536*(12*a^2+5*b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/1920*(12*a^2+5*b^2)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/320*(44*a^2+45*b^2)*Cos[c+d*x]^7*Sin[c+d*x]/d+1/120*(12*a^2+25*b^2)*Cos[c+d*x]^9*Sin[c+d*x]/d-1/12*b^2*Cos[c+d*x]^11*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1654
  public void test0204() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-x/b-ArcTanh[Cos[c+d*x]]/(a*d)+2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a*b*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1670
  public void test0205() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "-a*x/b^2+2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^2*b^2*d)+b*ArcTanh[Cos[c+d*x]]/(a^2*d)-Cos[c+d*x]/(b*d)-Cot[c+d*x]/(a*d)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:124
  public void test0206() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/4*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(9/2))-1/8*a^2*(A+9*B)*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(5/2))+3/4*a^2*(A+9*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(5/2)*f*Sqrt[2])-3/8*a^2*(A+9*B)*Cos[e+f*x]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:142
  public void test0207() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x]), x]", //
        "-(A-B)*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*c*f)-(A-3*B)*c*Cos[e+f*x]/(a*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:158
  public void test0208() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-1/15*(3*A+7*B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^3*c*f)-1/5*(A-B)*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(7/2)/(a^3*c^3*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:178
  public void test0209() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-1/2*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])-2*a^2*(A+B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-a*(A+B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:194
  public void test0210() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/12*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(13/2))+1/40*(A-3*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*(c-c*Sin[e+f*x])^(11/2))+1/160*(A-3*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c^2*f*(c-c*Sin[e+f*x])^(9/2))+1/960*(A-3*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c^3*f*(c-c*Sin[e+f*x])^(7/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:212
  public void test0211() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/2*B*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])+2*(A-B)*c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+(A-B)*c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:228
  public void test0212() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/2*(A-B)*c*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])-B*c*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:272
  public void test0213() {
    check( //
        "Integrate[Sin[c+d*x]^2*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "3/16*a^3*A*x-2/3*a^3*A*Cos[c+d*x]^3/d+2/5*a^3*A*Cos[c+d*x]^5/d-3/16*a^3*A*Cos[c+d*x]*Sin[c+d*x]/d+5/24*a^3*A*Cos[c+d*x]*Sin[c+d*x]^3/d+1/6*a^3*A*Cos[c+d*x]*Sin[c+d*x]^5/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:329
  public void test0214() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^2, x]", //
        "B*d*x/a^2-1/3*(A*c+2*B*c+2*A*d-5*B*d)*Cos[e+f*x]/(a^2*f*(1+Sin[e+f*x]))-1/3*(A-B)*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:349
  public void test0215() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]/(c+d*Sin[e+f*x]), x]", //
        "2*(B*c-A*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/(d^(3/2)*f*Sqrt[c+d])-2*a*B*Cos[e+f*x]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:28
  public void test0216() {
    check( //
        "Integrate[(a*Sin[x]^4)^(1/2), x]", //
        "-1/2*Cot[x]*Sqrt[a*Sin[x]^4]+1/2*x*Csc[x]^2*Sqrt[a*Sin[x]^4]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:87
  public void test0217() {
    check( //
        "Integrate[1/(a-a*Sin[c+d*x]^2)^2, x]", //
        "Tan[c+d*x]/(a^2*d)+1/3*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:107
  public void test0218() {
    check( //
        "Integrate[a+b*Sin[x]^2, x]", //
        "a*x+1/2*b*x-1/2*b*Cos[x]*Sin[x]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:125
  public void test0219() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a+b*Sin[c+d*x]^2), x]", //
        "-Cot[c+d*x]/(a*d)-b*ArcTan[Sqrt[a+b]*Tan[c+d*x]/Sqrt[a]]/(a^(3/2)*d*Sqrt[a+b])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:180
  public void test0220() {
    check( //
        "Integrate[Sin[e+f*x]^2*(a+b*Sin[e+f*x]^2)^(3/2), x]", //
        "-1/5*Cos[e+f*x]*Sin[e+f*x]*(a+b*Sin[e+f*x]^2)^(3/2)/f-1/15*(3*a+4*b)*Cos[e+f*x]*Sin[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]/f+1/15*(3*a^2+13*a*b+8*b^2)*EllipticE[e+f*x,-b/a]*Sqrt[a+b*Sin[e+f*x]^2]/(b*f*Sqrt[1+b*Sin[e+f*x]^2/a])-1/15*a*(a+b)*(3*a+4*b)*EllipticF[e+f*x,-b/a]*Sqrt[1+b*Sin[e+f*x]^2/a]/(b*f*Sqrt[a+b*Sin[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:261
  public void test0221() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a-b*Sin[c+d*x]^4), x]", //
        "-1/2*ArcTan[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]-Sqrt[b]]]/(b^(3/4)*d*Sqrt[Sqrt[a]-Sqrt[b]])+1/2*ArcTanh[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]+Sqrt[b]]]/(b^(3/4)*d*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:349
  public void test0222() {
    check( //
        "Integrate[Cos[x]^4/(a-a*Sin[x]^2), x]", //
        "1/2*x/a+1/2*Cos[x]*Sin[x]/a");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:365
  public void test0223() {
    check( //
        "Integrate[Sec[x]^4/(a-a*Sin[x]^2)^2, x]", //
        "Tan[x]/a^2+Tan[x]^3/a^2+3/5*Tan[x]^5/a^2+1/7*Tan[x]^7/a^2");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:451
  public void test0224() {
    check( //
        "Integrate[Sec[e+f*x]/(a+b*Sin[e+f*x]^2)^(3/2), x]", //
        "ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/((a+b)^(3/2)*f)+b*Sin[e+f*x]/(a*(a+b)*f*Sqrt[a+b*Sin[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:611
  public void test0225() {
    check( //
        "Integrate[Tan[e+f*x]^5/(a-a*Sin[e+f*x]^2)^(3/2), x]", //
        "1/7*a^2/(f*(a*Cos[e+f*x]^2)^(7/2))-2/5*a/(f*(a*Cos[e+f*x]^2)^(5/2))+1/3/(f*(a*Cos[e+f*x]^2)^(3/2))");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:13
  public void test0226() {
    check( //
        "Integrate[Cos[a+b*x]^4, x]", //
        "3/8*x+3/8*Cos[a+b*x]*Sin[a+b*x]/b+1/4*Cos[a+b*x]^3*Sin[a+b*x]/b");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:31
  public void test0227() {
    check( //
        "Integrate[(c*Cos[a+b*x])^(1/2), x]", //
        "2*EllipticE[1/2*(a+b*x),2]*Sqrt[c*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:69
  public void test0228() {
    check( //
        "Integrate[1/(a*Cos[x]^3)^(5/2), x]", //
        "-154/195*EllipticE[1/2*x,2]*Cos[x]^(3/2)/(a^2*Sqrt[a*Cos[x]^3])+154/195*Cos[x]*Sin[x]/(a^2*Sqrt[a*Cos[x]^3])+154/585*Tan[x]/(a^2*Sqrt[a*Cos[x]^3])+22/117*Sec[x]^2*Tan[x]/(a^2*Sqrt[a*Cos[x]^3])+2/13*Sec[x]^4*Tan[x]/(a^2*Sqrt[a*Cos[x]^3])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:107
  public void test0229() {
    check( //
        "Integrate[Sec[c+d*x]^3*Sqrt[b*Cos[c+d*x]], x]", //
        "2/3*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/3*b*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:123
  public void test0230() {
    check( //
        "Integrate[Cos[c+d*x]^3*(b*Cos[c+d*x])^(5/2), x]", //
        "18/77*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/11*(b*Cos[c+d*x])^(9/2)*Sin[c+d*x]/(b^2*d)+30/77*b^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+30/77*b^2*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:141
  public void test0231() {
    check( //
        "Integrate[Cos[c+d*x]^3/Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:157
  public void test0232() {
    check( //
        "Integrate[1/(b*Cos[c+d*x])^(3/2), x]", //
        "2*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])-2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:173
  public void test0233() {
    check( //
        "Integrate[Sec[c+d*x]^3/(b*Cos[c+d*x])^(5/2), x]", //
        "2/9*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(9/2))+14/45*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+14/15*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])-14/15*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:318
  public void test0234() {
    check( //
        "Integrate[Cos[a+b*x]*Csc[a+b*x]^(1/2), x]", //
        "2/(b*Sqrt[Csc[a+b*x]])");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:18
  public void test0235() {
    check( //
        "Integrate[Cos[a+b*x]/(c+d*x)^3, x]", //
        "-1/2*b^2*CosIntegral[b*c/d+b*x]*Cos[a-b*c/d]/d^3-1/2*Cos[a+b*x]/(d*(c+d*x)^2)+1/2*b^2*SinIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^3+1/2*b*Sin[a+b*x]/(d^2*(c+d*x))");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:34
  public void test0236() {
    check( //
        "Integrate[x^3*Cos[a+b*x]^4, x]", //
        "-45/128*x^2/b^2+3/32*x^4-45/128*Cos[a+b*x]^2/b^4+9/16*x^2*Cos[a+b*x]^2/b^2-3/128*Cos[a+b*x]^4/b^4+3/16*x^2*Cos[a+b*x]^4/b^2-45/64*x*Cos[a+b*x]*Sin[a+b*x]/b^3+3/8*x^3*Cos[a+b*x]*Sin[a+b*x]/b-3/32*x*Cos[a+b*x]^3*Sin[a+b*x]/b^3+1/4*x^3*Cos[a+b*x]^3*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:157
  public void test0237() {
    check( //
        "Integrate[(c+d*x)^3*(a+a*Cos[e+f*x]), x]", //
        "1/4*a*(c+d*x)^4/d-6*a*d^3*Cos[e+f*x]/f^4+3*a*d*(c+d*x)^2*Cos[e+f*x]/f^2-6*a*d^2*(c+d*x)*Sin[e+f*x]/f^3+a*(c+d*x)^3*Sin[e+f*x]/f");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:179
  public void test0238() {
    check( //
        "Integrate[(c+d*x)^3/(a-a*Cos[e+f*x]), x]", //
        "-I*(c+d*x)^3/(a*f)-(c+d*x)^3*Cot[1/2*e+1/2*f*x]/(a*f)+6*d*(c+d*x)^2*Log[1-E^(I*(e+f*x))]/(a*f^2)-12*I*d^2*(c+d*x)*PolyLog[2,E^(I*(e+f*x))]/(a*f^3)+12*d^3*PolyLog[3,E^(I*(e+f*x))]/(a*f^4)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:201
  public void test0239() {
    check( //
        "Integrate[Sqrt[a+a*Cos[x]]/x^3, x]", //
        "-1/2*Sqrt[a+a*Cos[x]]/x^2-1/8*CosIntegral[1/2*x]*Sec[1/2*x]*Sqrt[a+a*Cos[x]]+1/4*Sqrt[a+a*Cos[x]]*Tan[1/2*x]/x");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:219
  public void test0240() {
    check( //
        "Integrate[x/Sqrt[a+a*Cos[c+d*x]], x]", //
        "-4*I*x*ArcTan[E^(1/2*I*(c+d*x))]*Cos[1/2*c+1/2*d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+4*I*Cos[1/2*c+1/2*d*x]*PolyLog[2,-I*E^(1/2*I*(c+d*x))]/(d^2*Sqrt[a+a*Cos[c+d*x]])-4*I*Cos[1/2*c+1/2*d*x]*PolyLog[2,I*E^(1/2*I*(c+d*x))]/(d^2*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:13
  public void test0241() {
    check( //
        "Integrate[Cos[a+b*x^2], x]", //
        "Cos[a]*FresnelC[x*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/Sqrt[b]-FresnelS[x*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*Sqrt[1/2*Pi]/Sqrt[b]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:29
  public void test0242() {
    check( //
        "Integrate[Cos[a+b*x^2]^3/x^2, x]", //
        "-Cos[a+b*x^2]^3/x-3/2*Cos[a]*FresnelS[x*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]*Sqrt[b]-3/2*FresnelC[x*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*Sqrt[1/2*Pi]*Sqrt[b]-1/2*Cos[3*a]*FresnelS[x*Sqrt[6/Pi]*Sqrt[b]]*Sqrt[3/2*Pi]*Sqrt[b]-1/2*FresnelC[x*Sqrt[6/Pi]*Sqrt[b]]*Sin[3*a]*Sqrt[3/2*Pi]*Sqrt[b]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:55
  public void test0243() {
    check( //
        "Integrate[Cos[a+b/x]/x, x]", //
        "-CosIntegral[b/x]*Cos[a]+SinIntegral[b/x]*Sin[a]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:127
  public void test0244() {
    check( //
        "Integrate[Cos[a+b*Sqrt[c+d*x]], x]", //
        "2*Cos[a+b*Sqrt[c+d*x]]/(b^2*d)+2*Sin[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:12
  public void test0245() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Cos[c+d*x]), x]", //
        "5/16*a*x+a*Sin[c+d*x]/d+5/16*a*Cos[c+d*x]*Sin[c+d*x]/d+5/24*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*a*Cos[c+d*x]^5*Sin[c+d*x]/d-2/3*a*Sin[c+d*x]^3/d+1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:29
  public void test0246() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*Sec[c+d*x], x]", //
        "2*a^2*x+a^2*ArcTanh[Sin[c+d*x]]/d+a^2*Sin[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:47
  public void test0247() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*Sec[c+d*x], x]", //
        "6*a^4*x+a^4*ArcTanh[Sin[c+d*x]]/d+7*a^4*Sin[c+d*x]/d+2*a^4*Cos[c+d*x]*Sin[c+d*x]/d-1/3*a^4*Sin[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:65
  public void test0248() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Cos[c+d*x]), x]", //
        "-3/2*ArcTanh[Sin[c+d*x]]/(a*d)+4*Tan[c+d*x]/(a*d)-3/2*Sec[c+d*x]*Tan[c+d*x]/(a*d)-Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))+4/3*Tan[c+d*x]^3/(a*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:81
  public void test0249() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x])^3, x]", //
        "1/5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+2/15*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+2/15*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:97
  public void test0250() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Cos[c+d*x])^5, x]", //
        "x/a^5-1/9*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^5)-13/63*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^4)-34/105*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^3)+173/315*Sin[c+d*x]/(a^3*d*(a+a*Cos[c+d*x])^2)-661/315*Sin[c+d*x]/(d*(a^5+a^5*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:117
  public void test0251() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*Sec[c+d*x], x]", //
        "2*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:133
  public void test0252() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x], x]", //
        "2*a^(5/2)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+14/3*a^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/3*a^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:151
  public void test0253() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/2*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+11/2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-13/3*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+7/6*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:171
  public void test0254() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x]), x]", //
        "2*a*EllipticE[1/2*(c+d*x),2]/d+2/3*a*EllipticF[1/2*(c+d*x),2]/d+2/3*a*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:187
  public void test0255() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3/Cos[c+d*x]^(5/2), x]", //
        "-4*a^3*EllipticE[1/2*(c+d*x),2]/d+20/3*a^3*EllipticF[1/2*(c+d*x),2]/d+2/3*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+6*a^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:205
  public void test0256() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])), x]", //
        "3*EllipticE[1/2*(c+d*x),2]/(a*d)+5/3*EllipticF[1/2*(c+d*x),2]/(a*d)+5/3*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))-Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x]))-3*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:221
  public void test0257() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^3), x]", //
        "-49/10*EllipticE[1/2*(c+d*x),2]/(a^3*d)-13/6*EllipticF[1/2*(c+d*x),2]/(a^3*d)+49/10*Sin[c+d*x]/(a^3*d*Sqrt[Cos[c+d*x]])-1/5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3*Sqrt[Cos[c+d*x]])-8/15*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]])-13/6*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x])*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:241
  public void test0258() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)/Cos[c+d*x]^(9/2), x]", //
        "2/7*a^2*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])+26/35*a^2*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+104/105*a^2*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+208/105*a^2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:259
  public void test0259() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "-ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:275
  public void test0260() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-5*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/4*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-15/16*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+115/16*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+35/16*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:291
  public void test0261() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)/(a+a*Cos[c+d*x])^(9/2), x]", //
        "-1/8*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(9/2))+45/1024*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(9/2)*d*Sqrt[2])-5/32*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(7/2))+33/256*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(5/2))+73/1024*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^3*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:313
  public void test0262() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)/(a-a*Cos[c+d*x])^(1/2), x]", //
        "ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])]/(d*Sqrt[a])-ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a-a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:396
  public void test0263() {
    check( //
        "Integrate[Sec[c+d*x]^(1/2)*Sqrt[a+a*Cos[c+d*x]], x]", //
        "2*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]*Sqrt[Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:480
  public void test0264() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "3/8*a*ArcTanh[Sin[c+d*x]]/d+b*Tan[c+d*x]/d+3/8*a*Sec[c+d*x]*Tan[c+d*x]/d+1/4*a*Sec[c+d*x]^3*Tan[c+d*x]/d+1/3*b*Tan[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:498
  public void test0265() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*Sec[c+d*x]^2, x]", //
        "3*a*b^2*x+3*a^2*b*ArcTanh[Sin[c+d*x]]/d-b*(a^2-b^2)*Sin[c+d*x]/d+a^2*(a+b*Cos[c+d*x])*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:516
  public void test0266() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Cos[c+d*x]), x]", //
        "1/8*(8*a^4+4*a^2*b^2+3*b^4)*x/b^5-1/3*a*(3*a^2+2*b^2)*Sin[c+d*x]/(b^4*d)+1/8*(4*a^2+3*b^2)*Cos[c+d*x]*Sin[c+d*x]/(b^3*d)-1/3*a*Cos[c+d*x]^2*Sin[c+d*x]/(b^2*d)+1/4*Cos[c+d*x]^3*Sin[c+d*x]/(b*d)-2*a^5*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b^5*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:533
  public void test0267() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+b*Cos[c+d*x])^2, x]", //
        "2*b^2*(3*a^2-2*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^3*(a-b)^(3/2)*(a+b)^(3/2)*d)-2*b*ArcTanh[Sin[c+d*x]]/(a^3*d)+(a^2-2*b^2)*Tan[c+d*x]/(a^2*(a^2-b^2)*d)+b^2*Tan[c+d*x]/(a*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:549
  public void test0268() {
    check( //
        "Integrate[Cos[c+d*x]/(a+b*Cos[c+d*x])^4, x]", //
        "-b*(4*a^2+b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(7/2)*(a+b)^(7/2)*d)+1/3*a*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^3)+1/6*(2*a^2+3*b^2)*Sin[c+d*x]/((a^2-b^2)^2*d*(a+b*Cos[c+d*x])^2)+1/6*a*(2*a^2+13*b^2)*Sin[c+d*x]/((a^2-b^2)^3*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:579
  public void test0269() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(7/2), x]", //
        "24/35*a*b*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*b*(a+b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/105*b*(71*a^2+25*b^2)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+32/105*a*(11*a^2+13*b^2)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/105*(71*a^4-46*a^2*b^2-25*b^4)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:606
  public void test0270() {
    check( //
        "Integrate[Cos[c+d*x]/(a+b*Cos[c+d*x])^(3/2), x]", //
        "2*a*Sin[c+d*x]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])-2*a*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:653
  public void test0271() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+b*Cos[c+d*x])^3, x]", //
        "2/5*a*(5*a^2+9*b^2)*EllipticE[1/2*(c+d*x),2]/d+2/21*b*(21*a^2+5*b^2)*EllipticF[1/2*(c+d*x),2]/d+32/35*a*b^2*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*b^2*Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])*Sin[c+d*x]/d+2/21*b*(21*a^2+5*b^2)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:903
  public void test0272() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(-3/5*B+B*Cos[c+d*x]), x]", //
        "2/5*B*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:943
  public void test0273() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])*Sec[c+d*x], x]", //
        "2/3*b^2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2*A*b*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:961
  public void test0274() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(b*Cos[c+d*x])^(1/2), x]", //
        "2*A*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:977
  public void test0275() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*A*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/3*B*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(3/2))+6/5*A*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])-6/5*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:12
  public void test0276() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "3/8*a*(A+B)*x+1/5*a*(5*A+4*B)*Sin[c+d*x]/d+3/8*a*(A+B)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*(A+B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a*B*Cos[c+d*x]^4*Sin[c+d*x]/d-1/15*a*(5*A+4*B)*Sin[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:28
  public void test0277() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "1/2*a^2*(2*A+3*B)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(5*A+6*B)*Tan[c+d*x]/d+1/6*a^2*(4*A+3*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*A*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:44
  public void test0278() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "1/2*a^4*(8*A+13*B)*x+1/2*a^4*(13*A+8*B)*ArcTanh[Sin[c+d*x]]/d-5/2*a^4*(A-B)*Sin[c+d*x]/d-1/2*(6*A+B)*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d+1/2*(5*A+2*B)*(a^2+a^2*Cos[c+d*x])^2*Tan[c+d*x]/d+1/2*a*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:63
  public void test0279() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^2, x]", //
        "B*x/a^2+1/3*(2*A-5*B)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:99
  public void test0280() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "4/1155*(187*A+168*B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+4/495*a^2*(187*A+168*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/693*a^2*(187*A+168*B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/99*a^2*(11*A+12*B)*Cos[c+d*x]^4*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-8/3465*a*(187*A+168*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/11*a*B*Cos[c+d*x]^4*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:115
  public void test0281() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "1/64*a^(5/2)*(163*A+200*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/4*a*A*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]^3*Tan[c+d*x]/d+1/64*a^3*(163*A+200*B)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/96*a^3*(95*A+104*B)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/24*a^2*(11*A+8*B)*Sec[c+d*x]^2*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:133
  public void test0282() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/4*(19*A-12*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)-1/2*(13*A-9*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A-B)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))-1/4*(7*A-6*B)*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+1/2*(2*A-B)*Sec[c+d*x]*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:153
  public void test0283() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]), x]", //
        "4/15*a^2*(9*A+8*B)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(6*A+5*B)*EllipticF[1/2*(c+d*x),2]/d+4/45*a^2*(9*A+8*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/63*a^2*(9*A+11*B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*B*Cos[c+d*x]^(5/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+4/21*a^2*(6*A+5*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:171
  public void test0284() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x]), x]", //
        "3*(A-B)*EllipticE[1/2*(c+d*x),2]/(a*d)-1/3*(3*A-5*B)*EllipticF[1/2*(c+d*x),2]/(a*d)+(A-B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))-1/3*(3*A-5*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:187
  public void test0285() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sqrt[Cos[c+d*x]]/(a+a*Cos[c+d*x])^3, x]", //
        "1/10*(A-B)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(A+B)*EllipticF[1/2*(c+d*x),2]/(a^3*d)+1/5*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^3)+1/15*(A+4*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)-1/10*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:207
  public void test0286() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(5/2), x]", //
        "2*a^(3/2)*B*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*a^2*(4*A+3*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/3*a*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(3/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:225
  public void test0287() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "-(A-B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2*A*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:241
  public void test0288() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(7/2), x]", //
        "1/6*(A-B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(7/2))+1/64*(7*A+5*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])+1/48*(A-13*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(5/2))+1/192*(17*A+67*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:279
  public void test0289() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*(A+B*Cos[c+d*x]), x]", //
        "1/8*(8*a^4*A+24*a^2*A*b^2+3*A*b^4+16*a^3*b*B+12*a*b^3*B)*x+1/30*(95*a^3*A*b+80*a*A*b^3+12*a^4*B+112*a^2*b^2*B+16*b^4*B)*Sin[c+d*x]/d+1/120*b*(130*a^2*A*b+45*A*b^3+24*a^3*B+116*a*b^2*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/60*(35*a*A*b+12*a^2*B+16*b^2*B)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/20*(5*A*b+4*a*B)*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d+1/5*B*(a+b*Cos[c+d*x])^4*Sin[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:364
  public void test0290() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(1/2), x]", //
        "-2/105*(28*a*A*b-24*a^2*B-25*b^2*B)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b^3*d)+2/35*(7*A*b-6*a*B)*Cos[c+d*x]*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d)+2/7*B*Cos[c+d*x]^2*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)+2/105*(56*a^2*A*b+63*A*b^3-48*a^3*B-44*a*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^4*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/105*(56*a^3*A*b+49*a*A*b^3-48*a^4*B-32*a^2*b^2*B-25*b^4*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^4*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:395
  public void test0291() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "6/5*(A*b+a*B)*EllipticE[1/2*(c+d*x),2]/d+2/21*(7*a*A+5*b*B)*EllipticF[1/2*(c+d*x),2]/d+2/5*(A*b+a*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*b*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/21*(7*a*A+5*b*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:411
  public void test0292() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x])/Cos[c+d*x]^(3/2), x]", //
        "-2/5*(5*a^3*A-15*a*A*b^2-15*a^2*b*B-3*b^3*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(9*a^2*A*b+A*b^3+3*a^3*B+3*a*b^2*B)*EllipticF[1/2*(c+d*x),2]/d-2/5*b^2*(5*a*A-b*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2*a*A*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-2/3*b*(6*a^2*A-A*b^2-3*a*b*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:58
  public void test0293() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]], x]", //
        "2/45*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b^3*d)+2/15*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:92
  public void test0294() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(b*Cos[c+d*x])^(1/2), x]", //
        "2/7*A*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/21*b*(5*A+7*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/21*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:108
  public void test0295() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*A*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/5*(3*A+5*C)*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])-2/5*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:128
  public void test0296() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "A*b*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/2*b*C*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/2*b*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:146
  public void test0297() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/Sqrt[b*Cos[c+d*x]], x]", //
        "(A+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-1/3*C*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:162
  public void test0298() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "(A+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])-1/3*C*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:355
  public void test0299() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/5*B*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b*d)+2/21*b^2*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/21*b*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+6/5*b*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:373
  public void test0300() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*B*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^3*d)+2/21*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/21*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)+6/5*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:389
  public void test0301() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*B*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^4*d)+2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^5*d)+2/21*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/21*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)+6/5*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:17
  public void test0302() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "a*C*x+1/2*a*(A+2*C)*ArcTanh[Sin[c+d*x]]/d+a*A*Tan[c+d*x]/d+1/2*a*A*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:33
  public void test0303() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "1/2*a^3*(6*A+5*C)*x+3*a^3*A*ArcTanh[Sin[c+d*x]]/d+5/2*a^3*C*Sin[c+d*x]/d-1/3*(3*A-C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d)-1/6*(6*A-5*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+A*(a+a*Cos[c+d*x])^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:49
  public void test0304() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^8, x]", //
        "1/4*a^4*(11*A+14*C)*ArcTanh[Sin[c+d*x]]/d+1/105*a^4*(454*A+581*C)*Tan[c+d*x]/d+1/4*a^4*(11*A+14*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/210*a^4*(247*A+308*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/210*(109*A+126*C)*(a^4+a^4*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/35*(8*A+7*C)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d+2/21*a*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^5*Tan[c+d*x]/d+1/7*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]^6*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:67
  public void test0305() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^2, x]", //
        "1/2*(7*A+2*C)*ArcTanh[Sin[c+d*x]]/(a^2*d)-4/3*(4*A+C)*Tan[c+d*x]/(a^2*d)+1/2*(7*A+2*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d)-2/3*(4*A+C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:83
  public void test0306() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^4, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^4*d)-1/105*(55*A-8*C)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-8/105*(20*A-C)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/35*(5*A-2*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:103
  public void test0307() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "2*a^(3/2)*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/5*C*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/5*a^2*(5*A+4*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/5*a*C*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:137
  public void test0308() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/8*(47*A+24*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)+1/2*(17*A+9*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A+C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+3/8*(7*A+4*C)*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])-1/12*(13*A+6*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+1/6*(5*A+3*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:157
  public void test0309() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2), x]", //
        "4/15*a^2*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]/d+8/231*a^2*(33*A+25*C)*EllipticF[1/2*(c+d*x),2]/d+4/45*a^2*(9*A+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/693*a^2*(99*A+89*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/11*C*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+8/99*C*Cos[c+d*x]^(5/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+8/231*a^2*(33*A+25*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:173
  public void test0310() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(13/2), x]", //
        "-4/5*a^3*(5*A+7*C)*EllipticE[1/2*(c+d*x),2]/d+4/231*a^3*(105*A+143*C)*EllipticF[1/2*(c+d*x),2]/d+8/385*a^3*(35*A+44*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+4/231*a^3*(105*A+143*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/11*A*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(d*Cos[c+d*x]^(11/2))+4/33*A*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d*Cos[c+d*x]^(9/2))+2/231*(35*A+33*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+4/5*a^3*(5*A+7*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:191
  public void test0311() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "-1/10*(A-49*C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(A-13*C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+2/15*(A-4*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/6*(A-13*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:211
  public void test0312() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "1/4*a^(3/2)*(8*A+7*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-1/4*a^2*(8*A-5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])-1/2*a*(4*A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:229
  public void test0313() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/Sqrt[a+a*Cos[c+d*x]], x]", //
        "-1/8*(8*A+9*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-1/12*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/8*(8*A+7*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:245
  public void test0314() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "-5/16*(15*A-C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2)*Sqrt[Cos[c+d*x]])-1/16*(13*A-3*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2)*Sqrt[Cos[c+d*x]])+1/16*(49*A+C)*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:267
  public void test0315() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "a*(B+C)*x+a*B*ArcTanh[Sin[c+d*x]]/d+a*C*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:283
  public void test0316() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "5/8*a^3*(4*B+3*C)*x+a^3*(4*B+3*C)*Sin[c+d*x]/d+3/8*a^3*(4*B+3*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d-1/12*a^3*(4*B+3*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:301
  public void test0317() {
    check( //
        "Integrate[Cos[c+d*x]^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^2, x]", //
        "-1/2*(4*B-7*C)*x/a^2+2/3*(5*B-8*C)*Sin[c+d*x]/(a^2*d)-1/2*(4*B-7*C)*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+1/3*(5*B-8*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))+1/3*(B-C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:321
  public void test0318() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/35*(7*B-2*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*C*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a*d)+8/105*a^2*(21*B+19*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*a*(21*B+19*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:353
  public void test0319() {
    check( //
        "Integrate[A+B*Cos[c+d*x]+C*Cos[c+d*x]^2, x]", //
        "A*x+1/2*C*x+B*Sin[c+d*x]/d+1/2*C*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:369
  public void test0320() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/8*a^2*(8*A+7*B+6*C)*x+1/6*a^2*(8*A+7*B+6*C)*Sin[c+d*x]/d+1/24*a^2*(8*A+7*B+6*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/60*(20*A-5*B+6*C)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/5*C*Cos[c+d*x]^2*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/20*(5*B+2*C)*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:385
  public void test0321() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*a^3*(13*A+15*B+20*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^3*(38*A+45*B+55*C)*Tan[c+d*x]/d+1/120*a^3*(109*A+135*B+140*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/30*(11*A+15*B+10*C)*(a^3+a^3*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d+1/20*(3*A+5*B)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+1/5*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^4*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:403
  public void test0322() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "(B-C)*x/a+C*Sin[c+d*x]/(a*d)+(A-B+C)*Sin[c+d*x]/(a*d*(1+Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:419
  public void test0323() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "C*x/a^3-1/5*(A-B+C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(3*A+2*B-7*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/15*(6*A+4*B-29*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:439
  public void test0324() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/105*(21*A+18*B+16*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+2/45*a*(21*A+18*B+16*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/63*a*(9*B+C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-4/315*(21*A+18*B+16*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/9*C*Cos[c+d*x]^3*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:455
  public void test0325() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/128*a^(3/2)*(133*A+150*B+176*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/5*A*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]^4*Tan[c+d*x]/d+1/128*a^2*(133*A+150*B+176*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/192*a^2*(133*A+150*B+176*C)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/240*a^2*(67*A+90*B+80*C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/40*a*(3*A+10*B)*Sec[c+d*x]^3*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:473
  public void test0326() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-(A-2*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A-B+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+A*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:489
  public void test0327() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^(5/2), x]", //
        "2*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/4*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(11*A-3*B-5*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(43*A-3*B-5*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:655
  public void test0328() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+b*Cos[c+d*x]), x]", //
        "C*x/b+A*ArcTanh[Sin[c+d*x]]/(a*d)-2*(A*b^2+a^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a*b*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:687
  public void test0329() {
    check( //
        "Integrate[(1-Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+b*Cos[c+d*x]), x]", //
        "-b*ArcTanh[Sin[c+d*x]]/(a^2*d)-2*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/(a^2*d)+Tan[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:784
  public void test0330() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "-2/5*(5*a^2*(A-C)-b^2*(5*A+3*C))*EllipticE[1/2*(c+d*x),2]/d+4/3*a*b*(3*A+C)*EllipticF[1/2*(c+d*x),2]/d-2/5*b^2*(5*A-C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2*A*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-4/3*a*b*(3*A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:902
  public void test0331() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*(3*a^2*B+4*b^2*B+8*a*b*C)*ArcTanh[Sin[c+d*x]]/d+1/3*(4*a*b*B+2*a^2*C+3*b^2*C)*Tan[c+d*x]/d+1/8*(3*a^2*B+4*b^2*B+8*a*b*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*(2*b*B+a*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a^2*B*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:975
  public void test0332() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+b*Cos[c+d*x])^(5/2), x]", //
        "-2/3*(b*B-a*C)*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^(3/2))-2/3*(4*a*b*B-a^2*C-3*b^2*C)*Sin[c+d*x]/((a^2-b^2)^2*d*Sqrt[a+b*Cos[c+d*x]])+2/3*(4*a*b*B-a^2*C-3*b^2*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/3*(b*B-a*C)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:997
  public void test0333() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "2/15*(27*a^2*b*B+7*b^3*B+9*a^3*C+21*a*b^2*C)*EllipticE[1/2*(c+d*x),2]/d+2/231*(77*a^3*B+165*a*b^2*B+165*a^2*b*C+45*b^3*C)*EllipticF[1/2*(c+d*x),2]/d+2/45*(27*a^2*b*B+7*b^3*B+9*a^3*C+21*a*b^2*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/77*b*(33*a*b*B+26*a^2*C+9*b^2*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/99*b^2*(11*b*B+15*a*C)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/d+2/11*b*C*Cos[c+d*x]^(5/2)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+2/231*(77*a^3*B+165*a*b^2*B+165*a^2*b*C+45*b^3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1089
  public void test0334() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*(3*A*b+3*a*B+4*b*C)*ArcTanh[Sin[c+d*x]]/d+1/5*(4*a*A+5*b*B+5*a*C)*Tan[c+d*x]/d+1/8*(3*A*b+3*a*B+4*b*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/4*(A*b+a*B)*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*a*A*Sec[c+d*x]^4*Tan[c+d*x]/d+1/15*(4*a*A+5*b*B+5*a*C)*Tan[c+d*x]^3/d");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:99
  public void test0335() {
    check( //
        "Integrate[Sqrt[-1+Cos[x]^2], x]", //
        "-Cot[x]*Sqrt[-Sin[x]^2]");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:123
  public void test0336() {
    check( //
        "Integrate[1/(1+Cos[x]^2)^(3/2), x]", //
        "1/2*EllipticE[1/2*Pi+x,-1]-1/2*Cos[x]*Sin[x]/Sqrt[1+Cos[x]^2]");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:462
  public void test0337() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sec[c+d*x])^(3/2)*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/64*a^(3/2)*(75*B+88*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+1/64*a^2*(75*B+88*C)*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/96*a^2*(75*B+88*C)*Cos[c+d*x]*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/24*a^2*(9*B+8*C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/4*a*B*Cos[c+d*x]^3*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:142
  public void test0338() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a*Cos[c+d*x]+b*Sin[c+d*x]), x]", //
        "-b^2*ArcTanh[(b*Cos[c+d*x]-a*Sin[c+d*x])/Sqrt[a^2+b^2]]/((a^2+b^2)^(3/2)*d)+b*Cos[c+d*x]/((a^2+b^2)*d)+a*Sin[c+d*x]/((a^2+b^2)*d)");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:116
  public void test0339() {
    check( //
        "Integrate[Sec[e+f*x]/((a+a*Sec[e+f*x])^2*Sqrt[c-c*Sec[e+f*x]]), x]", //
        "-1/2*ArcTan[Sqrt[c]*Tan[e+f*x]/(Sqrt[2]*Sqrt[c-c*Sec[e+f*x]])]/(a^2*f*Sqrt[2]*Sqrt[c])+1/3*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^2*Sqrt[c-c*Sec[e+f*x]])+1/2*Tan[e+f*x]/(f*(a^2+a^2*Sec[e+f*x])*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:561
  public void test0340() {
    check( //
        "Integrate[Sec[c+d*x]^4*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^2, x]", //
        "-1/2*(4*A-7*B+10*C)*ArcTanh[Sin[c+d*x]]/(a^2*d)+(5*A-8*B+12*C)*Tan[c+d*x]/(a^2*d)-1/2*(4*A-7*B+10*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d)-1/3*(4*A-7*B+10*C)*Sec[c+d*x]^3*Tan[c+d*x]/(a^2*d*(1+Sec[c+d*x]))-1/3*(A-B+C)*Sec[c+d*x]^4*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^2)+1/3*(5*A-8*B+12*C)*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:528
  public void test0341() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+a*Sec[c+d*x])^3*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/16*a^3*(26*A+23*B+21*C)*ArcTanh[Sin[c+d*x]]/d+1/35*a^3*(133*A+119*B+108*C)*Tan[c+d*x]/d+1/16*a^3*(26*A+23*B+21*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/280*a^3*(154*A+147*B+129*C)*Sec[c+d*x]^3*Tan[c+d*x]/d+1/7*C*Sec[c+d*x]^3*(a+a*Sec[c+d*x])^3*Tan[c+d*x]/d+1/42*(7*B+3*C)*Sec[c+d*x]^3*(a^2+a^2*Sec[c+d*x])^2*Tan[c+d*x]/(a*d)+1/15*(3*A+4*B+3*C)*Sec[c+d*x]^3*(a^3+a^3*Sec[c+d*x])*Tan[c+d*x]/d+1/105*a^3*(133*A+119*B+108*C)*Tan[c+d*x]^3/d");
  }

  // 4.7.7 Trig functions.input:296
  public void test0342() {
    check( //
        "Integrate[(a*Cos[c+d*x]+b*Sin[c+d*x])^5, x]", //
        "-(a^2+b^2)^2*(b*Cos[c+d*x]-a*Sin[c+d*x])/d+2/3*(a^2+b^2)*(b*Cos[c+d*x]-a*Sin[c+d*x])^3/d-1/5*(b*Cos[c+d*x]-a*Sin[c+d*x])^5/d");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:15
  public void test0343() {
    check( //
        "Integrate[Sin[a+b*x]*Sin[2*a+2*b*x]^4, x]", //
        "-16/5*Cos[a+b*x]^5/b+32/7*Cos[a+b*x]^7/b-16/9*Cos[a+b*x]^9/b");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:95
  public void test0344() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a*Cos[c+d*x]+b*Sin[c+d*x])^3, x]", //
        "1/2*a^3*ArcTanh[Sin[c+d*x]]/d-3/8*a*b^2*ArcTanh[Sin[c+d*x]]/d+a^2*b*Sec[c+d*x]^3/d-1/3*b^3*Sec[c+d*x]^3/d+1/5*b^3*Sec[c+d*x]^5/d+1/2*a^3*Sec[c+d*x]*Tan[c+d*x]/d-3/8*a*b^2*Sec[c+d*x]*Tan[c+d*x]/d+3/4*a*b^2*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:342
  public void test0345() {
    check( //
        "Integrate[(c+d*x)^3*Csc[a+b*x]^2*Sec[a+b*x]^2, x]", //
        "-2*I*(c+d*x)^3/b-2*(c+d*x)^3*Cot[2*a+2*b*x]/b+3*d*(c+d*x)^2*Log[1-E^(4*I*(a+b*x))]/b^2-3/2*I*d^2*(c+d*x)*PolyLog[2,E^(4*I*(a+b*x))]/b^3+3/8*d^3*PolyLog[3,E^(4*I*(a+b*x))]/b^4");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:302
  public void test0346() {
    check( //
        "Integrate[(c+d*x)*Csc[a+b*x]^3*Sec[a+b*x], x]", //
        "-1/2*d*x/b-2*d*x*ArcTanh[E^(2*I*(a+b*x))]/b-1/2*d*Cot[a+b*x]/b^2-1/2*(c+d*x)*Cot[a+b*x]^2/b-d*x*Log[Tan[a+b*x]]/b+(c+d*x)*Log[Tan[a+b*x]]/b+1/2*I*d*PolyLog[2,-E^(2*I*(a+b*x))]/b^2-1/2*I*d*PolyLog[2,E^(2*I*(a+b*x))]/b^2");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:65
  public void test0347() {
    check( //
        "Integrate[Csc[c+d*x]^6*(a+a*Sec[c+d*x])^3, x]", //
        "13/2*a^3*ArcTanh[Sin[c+d*x]]/d+152/15*a^3*Tan[c+d*x]/d+13/2*a^3*Sec[c+d*x]*Tan[c+d*x]/d-1/5*a^6*Sec[c+d*x]*Tan[c+d*x]/(d*(a-a*Cos[c+d*x])^3)-11/15*a^5*Sec[c+d*x]*Tan[c+d*x]/(d*(a-a*Cos[c+d*x])^2)-76/15*a^6*Sec[c+d*x]*Tan[c+d*x]/(d*(a^3-a^3*Cos[c+d*x]))");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:120
  public void test0348() {
    check( //
        "Integrate[Csc[a+b*x]/Sin[2*a+2*b*x]^(1/2), x]", //
        "-Csc[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/b");
  }

  // 4.7.7 Trig functions.input:602
  public void test0349() {
    check( //
        "Integrate[1/(Cos[x]^2+Sin[x]^2), x]", //
        "x");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:582
  public void test0350() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Sec[c+d*x])*(A+B*Sec[c+d*x]), x]", //
        "2*a*(A+B)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(A+3*B)*EllipticF[1/2*(c+d*x),2]/d+2/3*a*A*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:260
  public void test0351() {
    check( //
        "Integrate[(a-a*Sec[c+d*x]^2)^(1/2), x]", //
        "-Cot[c+d*x]*Log[Cos[c+d*x]]*Sqrt[-a*Tan[c+d*x]^2]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:15
  public void test0352() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sec[c+d*x]), x]", //
        "a*ArcTanh[Sin[c+d*x]]/d+a*Tan[c+d*x]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1114
  public void test0353() {
    check( //
        "Integrate[1/((a+I*a*Tan[e+f*x])^2*(c-I*c*Tan[e+f*x])^3), x]", //
        "5/16*x/(a^2*c^3)+(-1/24*I)/(a^2*f*(c-I*c*Tan[e+f*x])^3)+(-3/32*I)/(a^2*c*f*(c-I*c*Tan[e+f*x])^2)+1/32*I/(a^2*c*f*(c+I*c*Tan[e+f*x])^2)+(-3/16*I)/(a^2*f*(c^3-I*c^3*Tan[e+f*x]))+1/8*I/(a^2*f*(c^3+I*c^3*Tan[e+f*x]))");
  }

  // 4.4.2.1 (a+b cot)^m (c+d cot)^n.input:20
  public void test0354() {
    check( //
        "Integrate[(a+a*Cot[c+d*x])/(e*Cot[c+d*x])^(1/2), x]", //
        "a*ArcTan[(1-Cot[c+d*x])*Sqrt[e]/(Sqrt[2]*Sqrt[e*Cot[c+d*x]])]*Sqrt[2]/(d*Sqrt[e])");
  }

  // 4.7.7 Trig functions.input:342
  public void test0355() {
    check( //
        "Integrate[1/(a*Cos[c+d*x]+I*a*Sin[c+d*x])^(3/2), x]", //
        "2/3*I/(d*(a*Cos[c+d*x]+I*a*Sin[c+d*x])^(3/2))");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:33
  public void test0356() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])^3*(c-c*Sec[e+f*x])^5, x]", //
        "45/128*a^3*c^5*ArcTanh[Sin[e+f*x]]/f-35/128*a^3*c^5*Sec[e+f*x]*Tan[e+f*x]/f-5/64*a^3*c^5*Sec[e+f*x]^3*Tan[e+f*x]/f+5/24*a^3*c^5*Sec[e+f*x]*Tan[e+f*x]^3/f+5/48*a^3*c^5*Sec[e+f*x]^3*Tan[e+f*x]^3/f-1/6*a^3*c^5*Sec[e+f*x]*Tan[e+f*x]^5/f-1/8*a^3*c^5*Sec[e+f*x]^3*Tan[e+f*x]^5/f+2/7*a^3*c^5*Tan[e+f*x]^7/f");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:613
  public void test0357() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sec[c+d*x])^(5/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/8*a^(5/2)*(25*A+38*B+40*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+1/12*a*(5*A+6*B)*Cos[c+d*x]*(a+a*Sec[c+d*x])^(3/2)*Sin[c+d*x]/d+1/3*A*Cos[c+d*x]^2*(a+a*Sec[c+d*x])^(5/2)*Sin[c+d*x]/d+1/24*a^3*(49*A+54*B-24*C)*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])-1/4*a^2*(3*A+2*B-8*C)*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:182
  public void test0358() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[2*a+2*b*x], x]", //
        "-2/5*Cos[a+b*x]^5/b");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:288
  public void test0359() {
    check( //
        "Integrate[Cot[a+b*Log[c*x^n]]^3/x, x]", //
        "-1/2*Cot[a+b*Log[c*x^n]]^2/(b*n)-Log[Sin[a+b*Log[c*x^n]]]/(b*n)");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:105
  public void test0360() {
    check( //
        "Integrate[Sec[e+f*x]*(c-c*Sec[e+f*x])^(7/2)/(a+a*Sec[e+f*x]), x]", //
        "12/5*c^2*(c-c*Sec[e+f*x])^(3/2)*Tan[e+f*x]/(a*f)+2*c*(c-c*Sec[e+f*x])^(5/2)*Tan[e+f*x]/(f*(a+a*Sec[e+f*x]))+128/5*c^4*Tan[e+f*x]/(a*f*Sqrt[c-c*Sec[e+f*x]])+32/5*c^3*Sqrt[c-c*Sec[e+f*x]]*Tan[e+f*x]/(a*f)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:554
  public void test0361() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])^4, x]", //
        "a^4*x+2*a*b*(2*a^2+b^2)*ArcTanh[Sin[c+d*x]]/d+1/3*b^2*(17*a^2+2*b^2)*Tan[c+d*x]/d+4/3*a*b^3*Sec[c+d*x]*Tan[c+d*x]/d+1/3*b^2*(a+b*Sec[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:183
  public void test0362() {
    check( //
        "Integrate[Cos[c-b*x]*Cos[a+b*x], x]", //
        "1/2*x*Cos[a+c]+1/4*Sin[a-c+2*b*x]/b");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:231
  public void test0363() {
    check( //
        "Integrate[Cot[x]/(Sec[x]+Tan[x]), x]", //
        "-x-ArcTanh[Cos[x]]");
  }

  // 4.7.7 Trig functions.input:556
  public void test0364() {
    check( //
        "Integrate[1/(b*Cos[d+e*x]+c*Sin[d+e*x]+Sqrt[b^2+c^2])^(5/2), x]", //
        "3/16*ArcTanh[(b^2+c^2)^(1/4)*Sin[d+e*x-ArcTan[b,c]]/(Sqrt[2]*Sqrt[Sqrt[b^2+c^2]+Cos[d+e*x-ArcTan[b,c]]*Sqrt[b^2+c^2]])]/((b^2+c^2)^(5/4)*e*Sqrt[2])+1/4*(-c*Cos[d+e*x]+b*Sin[d+e*x])/(e*Sqrt[b^2+c^2]*(b*Cos[d+e*x]+c*Sin[d+e*x]+Sqrt[b^2+c^2])^(5/2))-3/16*(c*Cos[d+e*x]-b*Sin[d+e*x])/((b^2+c^2)*e*(b*Cos[d+e*x]+c*Sin[d+e*x]+Sqrt[b^2+c^2])^(3/2))");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:145
  public void test0365() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])^(3/2)/(c-c*Sec[e+f*x])^(11/2), x]", //
        "1/20*a^2*Tan[e+f*x]/(c*f*(c-c*Sec[e+f*x])^(9/2)*Sqrt[a+a*Sec[e+f*x]])-1/5*a*Sqrt[a+a*Sec[e+f*x]]*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^(11/2))");
  }

  // 4.7.7 Trig functions.input:1063
  public void test0366() {
    check( //
        "Integrate[2*Cot[2*x]-3*Sin[3*x], x]", //
        "Cos[3*x]+Log[Sin[2*x]]");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:203
  public void test0367() {
    check( //
        "Integrate[Cos[e+f*x]^5*(a+b*Sec[e+f*x]^2)^2, x]", //
        "(a+b)^2*Sin[e+f*x]/f-2/3*a*(a+b)*Sin[e+f*x]^3/f+1/5*a^2*Sin[e+f*x]^5/f");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:320
  public void test0368() {
    check( //
        "Integrate[Sec[e+f*x]^4/(a+b*Sec[e+f*x]^2)^(3/2), x]", //
        "ArcTanh[Sqrt[b]*Tan[e+f*x]/Sqrt[a+b+b*Tan[e+f*x]^2]]/(b^(3/2)*f)-a*Tan[e+f*x]/(b*(a+b)*f*Sqrt[a+b+b*Tan[e+f*x]^2])");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:211
  public void test0369() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[2*a+2*b*x]^(1/2), x]", //
        "-5/32*ArcSin[Cos[a+b*x]-Sin[a+b*x]]/b-5/32*Log[Cos[a+b*x]+Sin[a+b*x]+Sqrt[Sin[2*a+2*b*x]]]/b+1/8*Cos[a+b*x]*Sin[2*a+2*b*x]^(3/2)/b+5/16*Sin[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/b");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:278
  public void test0370() {
    check( //
        "Integrate[(a+b*Tan[e+f*x]^2)^2, x]", //
        "(a-b)^2*x+(2*a-b)*b*Tan[e+f*x]/f+1/3*b^2*Tan[e+f*x]^3/f");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:138
  public void test0371() {
    check( //
        "Integrate[Sec[c+d*x]^3/Sqrt[b*Sec[c+d*x]], x]", //
        "2/3*(b*Sec[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]]/(b*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:612
  public void test0372() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sec[c+d*x])^(5/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/4*a^(5/2)*(19*A+20*B+8*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d-1/6*a*(3*A-4*C)*(a+a*Sec[c+d*x])^(3/2)*Sin[c+d*x]/d+1/2*A*Cos[c+d*x]*(a+a*Sec[c+d*x])^(5/2)*Sin[c+d*x]/d+1/12*a^3*(27*A-12*B-56*C)*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])-1/2*a^2*(A-4*B-8*C)*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:88
  public void test0373() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sec[c+d*x])^4, x]", //
        "-1/7*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^4)+4/35*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^3)+8/105*Tan[c+d*x]/(d*(a^2+a^2*Sec[c+d*x])^2)+8/105*Tan[c+d*x]/(d*(a^4+a^4*Sec[c+d*x]))");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:271
  public void test0374() {
    check( //
        "Integrate[Sec[c+b*x]^6*Sin[a+b*x], x]", //
        "1/5*Cos[a-c]*Sec[c+b*x]^5/b+3/8*ArcTanh[Sin[c+b*x]]*Sin[a-c]/b+3/8*Sec[c+b*x]*Sin[a-c]*Tan[c+b*x]/b+1/4*Sec[c+b*x]^3*Sin[a-c]*Tan[c+b*x]/b");
  }

  // 4.7.7 Trig functions.input:222
  public void test0375() {
    check( //
        "Integrate[x^3*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]], x]", //
        "-6*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]/f^4+3*x^2*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]/f^2-6*x*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]*Tan[e+f*x]/f^3+x^3*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]*Tan[e+f*x]/f");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:137
  public void test0376() {
    check( //
        "Integrate[Csc[e+f*x]^6/(a+b*Sec[e+f*x]^2)^(3/2), x]", //
        "-1/15*(15*a^2-10*a*b-b^2)*Cot[e+f*x]/((a+b)^3*f*Sqrt[a+b+b*Tan[e+f*x]^2])-2/15*(5*a+2*b)*Cot[e+f*x]^3/((a+b)^2*f*Sqrt[a+b+b*Tan[e+f*x]^2])-1/5*Cot[e+f*x]^5/((a+b)*f*Sqrt[a+b+b*Tan[e+f*x]^2])-2/15*b*(15*a^2-10*a*b-b^2)*Tan[e+f*x]/((a+b)^4*f*Sqrt[a+b+b*Tan[e+f*x]^2])");
  }

  // 4.6.1.2 (d csc)^n (a+b csc)^m.input:24
  public void test0377() {
    check( //
        "Integrate[(a+a*Csc[x])^(5/2), x]", //
        "-2*a^(5/2)*ArcTan[Cot[x]*Sqrt[a]/Sqrt[a+a*Csc[x]]]-14/3*a^3*Cot[x]/Sqrt[a+a*Csc[x]]-2/3*a^2*Cot[x]*Sqrt[a+a*Csc[x]]");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:172
  public void test0378() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[2*a+2*b*x], x]", //
        "-1/2*Cos[a+b*x]^4/b");
  }

  // 4.4.7 (d trig)^m (a+b (c cot)^n)^p.input:52
  public void test0379() {
    check( //
        "Integrate[Cot[x]^3*Sqrt[a+b*Cot[x]^2], x]", //
        "-1/3*(a+b*Cot[x]^2)^(3/2)/b-ArcTanh[Sqrt[a+b*Cot[x]^2]/Sqrt[a-b]]*Sqrt[a-b]+Sqrt[a+b*Cot[x]^2]");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:30
  public void test0380() {
    check( //
        "Integrate[(c*Sec[a+b*x])^(3/2), x]", //
        "-2*c^2*EllipticE[1/2*(a+b*x),2]/(b*Sqrt[Cos[a+b*x]]*Sqrt[c*Sec[a+b*x]])+2*c*Sin[a+b*x]*Sqrt[c*Sec[a+b*x]]/b");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:361
  public void test0381() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])^3*(A+B*Sec[c+d*x]), x]", //
        "a^3*A*x+1/2*(6*a^2*A*b+A*b^3+2*a^3*B+3*a*b^2*B)*ArcTanh[Sin[c+d*x]]/d+1/3*b*(9*a*A*b+8*a^2*B+2*b^2*B)*Tan[c+d*x]/d+1/6*b^2*(3*A*b+5*a*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*b*B*(a+b*Sec[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:1103
  public void test0382() {
    check( //
        "Integrate[Cos[x]^2/(a+b*Cos[2*x]), x]", //
        "1/2*x/b-1/2*ArcTan[Sqrt[a-b]*Tan[x]/Sqrt[a+b]]*Sqrt[a-b]/(b*Sqrt[a+b])");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:59
  public void test0383() {
    check( //
        "Integrate[(c+d*x)^4*Cos[a+b*x]*Csc[a+b*x]^3, x]", //
        "-2*I*d*(c+d*x)^3/b^2-2*d*(c+d*x)^3*Cot[a+b*x]/b^2-1/2*(c+d*x)^4*Csc[a+b*x]^2/b+6*d^2*(c+d*x)^2*Log[1-E^(2*I*(a+b*x))]/b^3-6*I*d^3*(c+d*x)*PolyLog[2,E^(2*I*(a+b*x))]/b^4+3*d^4*PolyLog[3,E^(2*I*(a+b*x))]/b^5");
  }

  // 4.7.7 Trig functions.input:173
  public void test0384() {
    check( //
        "Integrate[Cos[2*x]*Sin[6*x]^2, x]", //
        "1/4*Sin[2*x]-1/40*Sin[10*x]-1/56*Sin[14*x]");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:314
  public void test0385() {
    check( //
        "Integrate[(c+d*x)^4*Sec[a+b*x]*Tan[a+b*x], x]", //
        "8*I*d*(c+d*x)^3*ArcTan[E^(I*(a+b*x))]/b^2-12*I*d^2*(c+d*x)^2*PolyLog[2,-I*E^(I*(a+b*x))]/b^3+12*I*d^2*(c+d*x)^2*PolyLog[2,I*E^(I*(a+b*x))]/b^3+24*d^3*(c+d*x)*PolyLog[3,-I*E^(I*(a+b*x))]/b^4-24*d^3*(c+d*x)*PolyLog[3,I*E^(I*(a+b*x))]/b^4+24*I*d^4*PolyLog[4,-I*E^(I*(a+b*x))]/b^5-24*I*d^4*PolyLog[4,I*E^(I*(a+b*x))]/b^5+(c+d*x)^4*Sec[a+b*x]/b");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:474
  public void test0386() {
    check( //
        "Integrate[(c+d*x)^2*Csc[a+b*x]^2*Sin[3*a+3*b*x], x]", //
        "-6*(c+d*x)^2*ArcTanh[E^(I*(a+b*x))]/b-8*d^2*Cos[a+b*x]/b^3+4*(c+d*x)^2*Cos[a+b*x]/b+6*I*d*(c+d*x)*PolyLog[2,-E^(I*(a+b*x))]/b^2-6*I*d*(c+d*x)*PolyLog[2,E^(I*(a+b*x))]/b^2-6*d^2*PolyLog[3,-E^(I*(a+b*x))]/b^3+6*d^2*PolyLog[3,E^(I*(a+b*x))]/b^3-8*d*(c+d*x)*Sin[a+b*x]/b^2");
  }

  // 4.7.7 Trig functions.input:1060
  public void test0387() {
    check( //
        "Integrate[Cos[x]*(Sec[x]^3+Tan[x]), x]", //
        "-Cos[x]+Tan[x]");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:471
  public void test0388() {
    check( //
        "Integrate[Tan[e+f*x]/Sqrt[a+b*Sec[e+f*x]^2], x]", //
        "-ArcTanh[Sqrt[a+b*Sec[e+f*x]^2]/Sqrt[a]]/(f*Sqrt[a])");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:220
  public void test0389() {
    check( //
        "Integrate[Sec[c+d*x]^5/(a*Cos[c+d*x]+I*a*Sin[c+d*x])^3, x]", //
        "1/4*I*(I-Cot[c+d*x])^4*Tan[c+d*x]^4/(a^3*d)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:17
  public void test0390() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Sec[c+d*x]), x]", //
        "a*x+a*Sin[c+d*x]/d");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:160
  public void test0391() {
    check( //
        "Integrate[x^m*Cos[a+b*Log[c*x^n]]^4, x]", //
        "24*b^4*n^4*x^(1+m)/((1+m)*((1+m)^2+4*b^2*n^2)*((1+m)^2+16*b^2*n^2))+12*b^2*(1+m)*n^2*x^(1+m)*Cos[a+b*Log[c*x^n]]^2/(((1+m)^2+4*b^2*n^2)*((1+m)^2+16*b^2*n^2))+(1+m)*x^(1+m)*Cos[a+b*Log[c*x^n]]^4/((1+m)^2+16*b^2*n^2)+24*b^3*n^3*x^(1+m)*Cos[a+b*Log[c*x^n]]*Sin[a+b*Log[c*x^n]]/(((1+m)^2+4*b^2*n^2)*((1+m)^2+16*b^2*n^2))+4*b*n*x^(1+m)*Cos[a+b*Log[c*x^n]]^3*Sin[a+b*Log[c*x^n]]/((1+m)^2+16*b^2*n^2)");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:137
  public void test0392() {
    check( //
        "Integrate[Csc[a+b*x]^3*Sin[2*a+2*b*x]^(3/2), x]", //
        "2*ArcSin[Cos[a+b*x]-Sin[a+b*x]]/b+2*Log[Cos[a+b*x]+Sin[a+b*x]+Sqrt[Sin[2*a+2*b*x]]]/b-Csc[a+b*x]^3*Sin[2*a+2*b*x]^(5/2)/b-4*Sin[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/b");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:369
  public void test0393() {
    check( //
        "Integrate[(c+d*x)^3*Sec[a+b*x]^2*Tan[a+b*x], x]", //
        "3/2*I*d*(c+d*x)^2/b^2-3*d^2*(c+d*x)*Log[1+E^(2*I*(a+b*x))]/b^3+3/2*I*d^3*PolyLog[2,-E^(2*I*(a+b*x))]/b^4+1/2*(c+d*x)^3*Sec[a+b*x]^2/b-3/2*d*(c+d*x)^2*Tan[a+b*x]/b^2");
  }

  // 4.5.11 (e x)^m (a+b sec(c+d x^n))^p.input:57
  public void test0394() {
    check( //
        "Integrate[x*(a+b*Sec[c+d*Sqrt[x]])^2, x]", //
        "-2*I*b^2*x^(3/2)/d+1/2*a^2*x^2-8*I*a*b*x^(3/2)*ArcTan[E^(I*(c+d*Sqrt[x]))]/d+6*b^2*x*Log[1+E^(2*I*(c+d*Sqrt[x]))]/d^2+12*I*a*b*x*PolyLog[2,-I*E^(I*(c+d*Sqrt[x]))]/d^2-12*I*a*b*x*PolyLog[2,I*E^(I*(c+d*Sqrt[x]))]/d^2+3*b^2*PolyLog[3,-E^(2*I*(c+d*Sqrt[x]))]/d^4-24*I*a*b*PolyLog[4,-I*E^(I*(c+d*Sqrt[x]))]/d^4+24*I*a*b*PolyLog[4,I*E^(I*(c+d*Sqrt[x]))]/d^4-6*I*b^2*PolyLog[2,-E^(2*I*(c+d*Sqrt[x]))]*Sqrt[x]/d^3-24*a*b*PolyLog[3,-I*E^(I*(c+d*Sqrt[x]))]*Sqrt[x]/d^3+24*a*b*PolyLog[3,I*E^(I*(c+d*Sqrt[x]))]*Sqrt[x]/d^3+2*b^2*x^(3/2)*Tan[c+d*Sqrt[x]]/d");
  }

  // 4.7.7 Trig functions.input:759
  public void test0395() {
    check( //
        "Integrate[(a+b*Cos[c+d*x]*Sin[c+d*x])^3, x]", //
        "1/8*a*(8*a^2+3*b^2)*x-1/24*b*(16*a^2+b^2)*Cos[2*c+2*d*x]/d-5/48*a*b^2*Cos[2*c+2*d*x]*Sin[2*c+2*d*x]/d-1/48*b*Cos[2*c+2*d*x]*(2*a+b*Sin[2*c+2*d*x])^2/d");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:248
  public void test0396() {
    check( //
        "Integrate[Cot[a+I*Log[x]], x]", //
        "-I*x+2*I*E^(I*a)*ArcTanh[x/E^(I*a)]");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:395
  public void test0397() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sec[c+d*x])*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*a*(B+C)*x+1/3*a*(2*B+3*C)*Sin[c+d*x]/d+1/2*a*(B+C)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*a*B*Cos[c+d*x]^2*Sin[c+d*x]/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:166
  public void test0398() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])^4*(A+C*Sec[c+d*x]^2), x]", //
        "a^4*A*x+1/2*a^4*(12*A+7*C)*ArcTanh[Sin[c+d*x]]/d+1/2*a^4*(10*A+7*C)*Tan[c+d*x]/d+1/5*a*C*(a+a*Sec[c+d*x])^3*Tan[c+d*x]/d+1/5*C*(a+a*Sec[c+d*x])^4*Tan[c+d*x]/d+1/15*(5*A+7*C)*(a^2+a^2*Sec[c+d*x])^2*Tan[c+d*x]/d+1/6*(8*A+7*C)*(a^4+a^4*Sec[c+d*x])*Tan[c+d*x]/d");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:244
  public void test0399() {
    check( //
        "Integrate[Sin[a+b*x]*Sin[c+d*x]^2, x]", //
        "-1/2*Cos[a+b*x]/b+1/4*Cos[a-2*c+(b-2*d)*x]/(b-2*d)+1/4*Cos[a+2*c+(b+2*d)*x]/(b+2*d)");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:380
  public void test0400() {
    check( //
        "Integrate[Cot[e+f*x]^2*(a+b*Sec[e+f*x]^2), x]", //
        "-a*x-(a+b)*Cot[e+f*x]/f");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:927
  public void test0401() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+b*Sec[c+d*x])*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/8*(4*a*B+3*b*C)*ArcTanh[Sin[c+d*x]]/d+(b*B+a*C)*Tan[c+d*x]/d+1/8*(4*a*B+3*b*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/4*b*C*Sec[c+d*x]^3*Tan[c+d*x]/d+1/3*(b*B+a*C)*Tan[c+d*x]^3/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:194
  public void test0402() {
    check( //
        "Integrate[Sec[c+d*x]^3*(A+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^3, x]", //
        "-3*C*ArcTanh[Sin[c+d*x]]/(a^3*d)+1/15*(2*A+27*C)*Tan[c+d*x]/(a^3*d)-1/5*(A+C)*Sec[c+d*x]^3*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^3)+1/15*(A-9*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^2)+3*C*Tan[c+d*x]/(d*(a^3+a^3*Sec[c+d*x]))");
  }

  // 4.4.1.3 (d cos)^m (a+b cot)^n.input:16
  public void test0403() {
    check( //
        "Integrate[Cos[x]^2/(I+Cot[x]), x]", //
        "-1/8*I*x+1/8*I/(I-Cot[x])+1/8/(I+Cot[x])^2+1/4*I/(I+Cot[x])");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:255
  public void test0404() {
    check( //
        "Integrate[Sin[a+b*x]^2*Sin[c+d*x]^3, x]", //
        "1/16*Cos[2*a-3*c+(2*b-3*d)*x]/(2*b-3*d)-3/16*Cos[2*a-c+(2*b-d)*x]/(2*b-d)-3/8*Cos[c+d*x]/d+1/24*Cos[3*c+3*d*x]/d+3/16*Cos[2*a+c+(2*b+d)*x]/(2*b+d)-1/16*Cos[2*a+3*c+(2*b+3*d)*x]/(2*b+3*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1464
  public void test0405() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x]), x]", //
        "-3/5*(7*A-7*B+5*C)*EllipticE[1/2*(c+d*x),2]/(a*d)+5/21*(9*A-7*B+7*C)*EllipticF[1/2*(c+d*x),2]/(a*d)-1/5*(7*A-7*B+5*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d)+1/7*(9*A-7*B+7*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(a*d)-(A-B+C)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+5/21*(9*A-7*B+7*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.5.10 (c+d x)^m (a+b sec)^n.input:50
  public void test0406() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*Sec[e+f*x])^2, x]", //
        "-I*b^2*(c+d*x)^3/f+1/4*a^2*(c+d*x)^4/d-4*I*a*b*(c+d*x)^3*ArcTan[E^(I*(e+f*x))]/f+3*b^2*d*(c+d*x)^2*Log[1+E^(2*I*(e+f*x))]/f^2+6*I*a*b*d*(c+d*x)^2*PolyLog[2,-I*E^(I*(e+f*x))]/f^2-6*I*a*b*d*(c+d*x)^2*PolyLog[2,I*E^(I*(e+f*x))]/f^2-3*I*b^2*d^2*(c+d*x)*PolyLog[2,-E^(2*I*(e+f*x))]/f^3-12*a*b*d^2*(c+d*x)*PolyLog[3,-I*E^(I*(e+f*x))]/f^3+12*a*b*d^2*(c+d*x)*PolyLog[3,I*E^(I*(e+f*x))]/f^3+3/2*b^2*d^3*PolyLog[3,-E^(2*I*(e+f*x))]/f^4-12*I*a*b*d^3*PolyLog[4,-I*E^(I*(e+f*x))]/f^4+12*I*a*b*d^3*PolyLog[4,I*E^(I*(e+f*x))]/f^4+b^2*(c+d*x)^3*Tan[e+f*x]/f");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:179
  public void test0407() {
    check( //
        "Integrate[1/(a-a*Sec[c+d*x]^2)^3, x]", //
        "x/a^3+Cot[c+d*x]/(a^3*d)-1/3*Cot[c+d*x]^3/(a^3*d)+1/5*Cot[c+d*x]^5/(a^3*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:773
  public void test0408() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])*(A+C*Sec[c+d*x]^2), x]", //
        "a*A*x+1/2*b*(2*A+C)*ArcTanh[Sin[c+d*x]]/d+a*C*Tan[c+d*x]/d+1/2*b*C*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:263
  public void test0409() {
    check( //
        "Integrate[Sin[c+d*x]/(Csc[c+d*x]+Sin[c+d*x]), x]", //
        "x-x/Sqrt[2]-ArcTan[Cos[c+d*x]*Sin[c+d*x]/(1+Sin[c+d*x]^2+Sqrt[2])]/(d*Sqrt[2])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1333
  public void test0410() {
    check( //
        "Integrate[(A+C*Sec[c+d*x]^2)*Sqrt[Cos[c+d*x]]/(a+a*Sec[c+d*x]), x]", //
        "(3*A+C)*EllipticE[1/2*(c+d*x),2]/(a*d)-(A-C)*EllipticF[1/2*(c+d*x),2]/(a*d)-(A+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:186
  public void test0411() {
    check( //
        "Integrate[x^3*Tan[a+I*Log[x]]^2, x]", //
        "2*E^(2*I*a)*x^2-1/4*x^4-2*E^(6*I*a)/(E^(2*I*a)+x^2)-4*E^(4*I*a)*Log[E^(2*I*a)+x^2]");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:555
  public void test0412() {
    check( //
        "Integrate[Sec[c+d*x]*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x]), x]", //
        "(B-C)*ArcTanh[Sin[c+d*x]]/(a*d)+C*Tan[c+d*x]/(a*d)+(A-B+C)*Tan[c+d*x]/(a*d*(1+Sec[c+d*x]))");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:190
  public void test0413() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Sec[c+d*x])/(a+a*Sec[c+d*x])^(1/2), x]", //
        "-(A-2*B)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/(d*Sqrt[a])+(A-B)*ArcTan[Sqrt[a]*Tan[c+d*x]/(Sqrt[2]*Sqrt[a+a*Sec[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+A*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:512
  public void test0414() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "a*A*x+1/2*a*(2*A+2*B+C)*ArcTanh[Sin[c+d*x]]/d+a*(B+C)*Tan[c+d*x]/d+1/2*a*C*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.4.10 (c+d x)^m (a+b cot)^n.input:36
  public void test0415() {
    check( //
        "Integrate[(c+d*x)^2/(a+I*a*Cot[e+f*x]), x]", //
        "-1/4*d^2*x/(a*f^2)+1/4*I*(c+d*x)^2/(a*f)+1/6*(c+d*x)^3/(a*d)+1/4*I*d^2/(f^3*(a+I*a*Cot[e+f*x]))+1/2*d*(c+d*x)/(f^2*(a+I*a*Cot[e+f*x]))-1/2*I*(c+d*x)^2/(f*(a+I*a*Cot[e+f*x]))");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:125
  public void test0416() {
    check( //
        "Integrate[Tan[c+d*x]^2*(A+B*Tan[c+d*x])/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "1/4*(I*A+B)*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(5/2)*d*Sqrt[2])+1/20*(-I*A+31*B)/(a^2*d*Sqrt[a+I*a*Tan[c+d*x]])+1/5*(I*A-B)*Tan[c+d*x]^2/(d*(a+I*a*Tan[c+d*x])^(5/2))+1/30*(3*I*A-13*B)/(a*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:93
  public void test0417() {
    check( //
        "Integrate[Sec[c+d*x]^7/(a+a*Sec[c+d*x])^5, x]", //
        "-5*ArcTanh[Sin[c+d*x]]/(a^5*d)+181/63*Tan[c+d*x]/(a^5*d)-1/9*Sec[c+d*x]^5*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^5)-5/21*Sec[c+d*x]^4*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^4)-29/63*Sec[c+d*x]^3*Tan[c+d*x]/(a^2*d*(a+a*Sec[c+d*x])^3)-67/63*Sec[c+d*x]^2*Tan[c+d*x]/(a^3*d*(a+a*Sec[c+d*x])^2)+5*Tan[c+d*x]/(d*(a^5+a^5*Sec[c+d*x]))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:966
  public void test0418() {
    check( //
        "Integrate[Sec[c+d*x]*(B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+b*Sec[c+d*x])^2, x]", //
        "C*ArcTanh[Sin[c+d*x]]/(b^2*d)-2*(b^3*B+a^3*C-2*a*b^2*C)*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^2*(a+b)^(3/2)*d)+a*(b*B-a*C)*Tan[c+d*x]/(b*(a^2-b^2)*d*(a+b*Sec[c+d*x]))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:178
  public void test0419() {
    check( //
        "Integrate[Sec[c+d*x]^2*(A+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x]), x]", //
        "1/2*(2*A+3*C)*ArcTanh[Sin[c+d*x]]/(a*d)-(A+2*C)*Tan[c+d*x]/(a*d)+1/2*(2*A+3*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d)-(A+C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Sec[c+d*x]))");
  }

  // 4.7.7 Trig functions.input:767
  public void test0420() {
    check( //
        "Integrate[(a+b*Cos[c+d*x]*Sin[c+d*x])^(1/2), x]", //
        "EllipticE[-1/4*Pi+c+d*x,2*b/(2*a+b)]*Sqrt[2*a+b*Sin[2*c+2*d*x]]/(d*Sqrt[2]*Sqrt[(2*a+b*Sin[2*c+2*d*x])/(2*a+b)])");
  }

  // 4.7.7 Trig functions.input:184
  public void test0421() {
    check( //
        "Integrate[Tan[a+b*x]*Tan[c+b*x], x]", //
        "-x-Cot[a-c]*Log[Cos[a+b*x]]/b+Cot[a-c]*Log[Cos[c+b*x]]/b");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:378
  public void test0422() {
    check( //
        "Integrate[(a+b*Sec[e+f*x]^2)*Tan[e+f*x]^2, x]", //
        "-a*x+a*Tan[e+f*x]/f+1/3*b*Tan[e+f*x]^3/f");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:343
  public void test0423() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]^3/(a*Cos[x]+b*Sin[x]), x]", //
        "a^2*b^3*x/(a^2+b^2)^3-1/2*a^2*b*x/(a^2+b^2)^2+1/8*b*x/(a^2+b^2)-a^3*b^2*Log[a*Cos[x]+b*Sin[x]]/(a^2+b^2)^3+1/2*a^2*b*Cos[x]*Sin[x]/(a^2+b^2)^2+1/8*b*Cos[x]*Sin[x]/(a^2+b^2)-1/4*b*Cos[x]^3*Sin[x]/(a^2+b^2)-1/2*a*b^2*Sin[x]^2/(a^2+b^2)^2+1/4*a*Sin[x]^4/(a^2+b^2)");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:402
  public void test0424() {
    check( //
        "Integrate[(c+d*x)^3*Csc[a+b*x]^3*Sec[a+b*x]^3, x]", //
        "-6*d^2*(c+d*x)*ArcTanh[E^(2*I*(a+b*x))]/b^3-4*(c+d*x)^3*ArcTanh[E^(2*I*(a+b*x))]/b-3*d*(c+d*x)^2*Csc[2*a+2*b*x]/b^2-2*(c+d*x)^3*Cot[2*a+2*b*x]*Csc[2*a+2*b*x]/b+3/2*I*d^3*PolyLog[2,-E^(2*I*(a+b*x))]/b^4+3*I*d*(c+d*x)^2*PolyLog[2,-E^(2*I*(a+b*x))]/b^2-3/2*I*d^3*PolyLog[2,E^(2*I*(a+b*x))]/b^4-3*I*d*(c+d*x)^2*PolyLog[2,E^(2*I*(a+b*x))]/b^2-3*d^2*(c+d*x)*PolyLog[3,-E^(2*I*(a+b*x))]/b^3+3*d^2*(c+d*x)*PolyLog[3,E^(2*I*(a+b*x))]/b^3-3/2*I*d^3*PolyLog[4,-E^(2*I*(a+b*x))]/b^4+3/2*I*d^3*PolyLog[4,E^(2*I*(a+b*x))]/b^4");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:584
  public void test0425() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Sec[c+d*x])^3, x]", //
        "ArcTanh[Sin[c+d*x]]/(b^3*d)-a*(2*a^4-5*a^2*b^2+6*b^4)*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(5/2)*b^3*(a+b)^(5/2)*d)-1/2*a^2*Sec[c+d*x]*Tan[c+d*x]/(b*(a^2-b^2)*d*(a+b*Sec[c+d*x])^2)-1/2*a^2*(2*a^2-5*b^2)*Tan[c+d*x]/(b^2*(a^2-b^2)^2*d*(a+b*Sec[c+d*x]))");
  }

  // 4.4.9 trig^m (a+b cot^n+c cot^(2 n))^p.input:40
  public void test0426() {
    check( //
        "Integrate[Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4]*Tan[d+e*x], x]", //
        "1/2*ArcTanh[1/2*(2*a+b*Cot[d+e*x]^2)/(Sqrt[a]*Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4])]*Sqrt[a]/e-1/2*ArcTanh[1/2*(b+2*c*Cot[d+e*x]^2)/(Sqrt[c]*Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4])]*Sqrt[c]/e-1/2*ArcTanh[1/2*(2*a-b+(b-2*c)*Cot[d+e*x]^2)/(Sqrt[a-b+c]*Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4])]*Sqrt[a-b+c]/e");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:86
  public void test0427() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sec[c+d*x])^4, x]", //
        "1/7*Sec[c+d*x]^3*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^4)+3/35*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^3)-8/35*Tan[c+d*x]/(d*(a^2+a^2*Sec[c+d*x])^2)+1/5*Tan[c+d*x]/(d*(a^4+a^4*Sec[c+d*x]))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:511
  public void test0428() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sec[c+d*x])*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*a*(2*A+B+C)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(3*A+3*B+2*C)*Tan[c+d*x]/d+1/2*a*(B+C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*C*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:604
  public void test0429() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Tan[c+d*x]^2)^2, x]", //
        "1/2*(6*a-b)*b^2*ArcTanh[Sin[c+d*x]*Sqrt[a-b]/Sqrt[a]]/(a^(3/2)*(a-b)^(7/2)*d)+(a-3*b)*Sin[c+d*x]/((a-b)^3*d)-1/3*Sin[c+d*x]^3/((a-b)^2*d)-1/2*b^3*Sin[c+d*x]/(a*(a-b)^3*d*(a-(a-b)*Sin[c+d*x]^2))");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:657
  public void test0430() {
    check( //
        "Integrate[Sec[c+d*x]/(a+b*Sec[c+d*x])^(5/2), x]", //
        "-8/3*a*EllipticE[ArcSin[Sqrt[a+b*Sec[c+d*x]]/Sqrt[a+b]],(a+b)/(a-b)]*Cot[c+d*x]*Sqrt[b*(1-Sec[c+d*x])/(a+b)]*Sqrt[-b*(1+Sec[c+d*x])/(a-b)]/((a-b)*b*(a+b)^(3/2)*d)+2/3*(3*a-b)*EllipticF[ArcSin[Sqrt[a+b*Sec[c+d*x]]/Sqrt[a+b]],(a+b)/(a-b)]*Cot[c+d*x]*Sqrt[b*(1-Sec[c+d*x])/(a+b)]*Sqrt[-b*(1+Sec[c+d*x])/(a-b)]/((a-b)*b*(a+b)^(3/2)*d)-2/3*b*Tan[c+d*x]/((a^2-b^2)*d*(a+b*Sec[c+d*x])^(3/2))-8/3*a*b*Tan[c+d*x]/((a^2-b^2)^2*d*Sqrt[a+b*Sec[c+d*x]])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:580
  public void test0431() {
    check( //
        "Integrate[Sec[c+d*x]^2*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^4, x]", //
        "1/105*(23*A-2*B-54*C)*Tan[c+d*x]/(a^4*d*(1+Sec[c+d*x])^2)+1/105*(8*A+13*B+36*C)*Tan[c+d*x]/(a^4*d*(1+Sec[c+d*x]))-1/7*(A-B+C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^4)-1/35*(6*A+B-8*C)*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^3)");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:302
  public void test0432() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+b*Tan[c+d*x])^3*(A+B*Tan[c+d*x]), x]", //
        "(3*a^2*A*b-A*b^3+a^3*B-3*a*b^2*B)*x+(3*a^2*A*b-A*b^3+a^3*B-3*a*b^2*B)*Cot[c+d*x]/d+1/4*a*(2*a^2*A-5*A*b^2-6*a*b*B)*Cot[c+d*x]^2/d-1/6*a^2*(3*A*b+2*a*B)*Cot[c+d*x]^3/d+(a^3*A-3*a*A*b^2-3*a^2*b*B+b^3*B)*Log[Sin[c+d*x]]/d-1/4*a*A*Cot[c+d*x]^4*(a+b*Tan[c+d*x])^2/d");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:358
  public void test0433() {
    check( //
        "Integrate[1/(x*Sec[a+b*Log[c*x^n]]^(3/2)), x]", //
        "2/3*Sin[a+b*Log[c*x^n]]/(b*n*Sqrt[Sec[a+b*Log[c*x^n]]])+2/3*EllipticF[1/2*(a+b*Log[c*x^n]),2]*Sqrt[Cos[a+b*Log[c*x^n]]]*Sqrt[Sec[a+b*Log[c*x^n]]]/(b*n)");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:341
  public void test0434() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]/(a*Cos[x]+b*Sin[x]), x]", //
        "-a^2*b*x/(a^2+b^2)^2+1/2*b*x/(a^2+b^2)-a*b^2*Log[a*Cos[x]+b*Sin[x]]/(a^2+b^2)^2+1/2*b*Cos[x]*Sin[x]/(a^2+b^2)+1/2*a*Sin[x]^2/(a^2+b^2)");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:477
  public void test0435() {
    check( //
        "Integrate[(a+b*Tan[c+d*x]^4)^3, x]", //
        "(a+b)^3*x-b*(3*a^2+3*a*b+b^2)*Tan[c+d*x]/d+1/3*b*(3*a^2+3*a*b+b^2)*Tan[c+d*x]^3/d-1/5*b^2*(3*a+b)*Tan[c+d*x]^5/d+1/7*b^2*(3*a+b)*Tan[c+d*x]^7/d-1/9*b^3*Tan[c+d*x]^9/d+1/11*b^3*Tan[c+d*x]^11/d");
  }

  // 4.7.6 f^(a+b x+c x^2) trig(d+e x+f x^2)^n.input:18
  public void test0436() {
    check( //
        "Integrate[E^x*Sin[x]^4, x]", //
        "24/85*E^x-24/85*E^x*Cos[x]*Sin[x]+12/85*E^x*Sin[x]^2-4/17*E^x*Cos[x]*Sin[x]^3+1/17*E^x*Sin[x]^4");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1123
  public void test0437() {
    check( //
        "Integrate[1/((a+I*a*Tan[e+f*x])^2*(c-I*c*Tan[e+f*x])^4), x]", //
        "15/64*x/(a^2*c^4)+(-1/32*I)/(a^2*f*(c-I*c*Tan[e+f*x])^4)+(-1/16*I)/(a^2*c*f*(c-I*c*Tan[e+f*x])^3)+(-3/32*I)/(a^2*f*(c^2-I*c^2*Tan[e+f*x])^2)+1/64*I/(a^2*f*(c^2+I*c^2*Tan[e+f*x])^2)+(-5/32*I)/(a^2*f*(c^4-I*c^4*Tan[e+f*x]))+5/64*I/(a^2*f*(c^4+I*c^4*Tan[e+f*x]))");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:121
  public void test0438() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a*Cos[c+d*x]+b*Sin[c+d*x])^5, x]", //
        "35/128*a^5*x+25/64*a^3*b^2*x+15/128*a*b^4*x-5/3*a^2*b^3*Cos[c+d*x]^6/d-5/8*a^4*b*Cos[c+d*x]^8/d+5/4*a^2*b^3*Cos[c+d*x]^8/d+35/128*a^5*Cos[c+d*x]*Sin[c+d*x]/d+25/64*a^3*b^2*Cos[c+d*x]*Sin[c+d*x]/d+15/128*a*b^4*Cos[c+d*x]*Sin[c+d*x]/d+35/192*a^5*Cos[c+d*x]^3*Sin[c+d*x]/d+25/96*a^3*b^2*Cos[c+d*x]^3*Sin[c+d*x]/d+5/64*a*b^4*Cos[c+d*x]^3*Sin[c+d*x]/d+7/48*a^5*Cos[c+d*x]^5*Sin[c+d*x]/d+5/24*a^3*b^2*Cos[c+d*x]^5*Sin[c+d*x]/d-5/16*a*b^4*Cos[c+d*x]^5*Sin[c+d*x]/d+1/8*a^5*Cos[c+d*x]^7*Sin[c+d*x]/d-5/4*a^3*b^2*Cos[c+d*x]^7*Sin[c+d*x]/d-5/8*a*b^4*Cos[c+d*x]^5*Sin[c+d*x]^3/d+1/6*b^5*Sin[c+d*x]^6/d-1/8*b^5*Sin[c+d*x]^8/d");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:215
  public void test0439() {
    check( //
        "Integrate[1/(Sec[c+d*x]^(3/2)*(b*Sec[c+d*x])^(3/2)), x]", //
        "Sin[c+d*x]*Sqrt[Sec[c+d*x]]/(b*d*Sqrt[b*Sec[c+d*x]])-1/3*Sin[c+d*x]^3*Sqrt[Sec[c+d*x]]/(b*d*Sqrt[b*Sec[c+d*x]])");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:177
  public void test0440() {
    check( //
        "Integrate[(c+d*x)^3*Cos[a+b*x]^3*Sin[a+b*x], x]", //
        "-45/256*d^3*x/b^3+3/32*(c+d*x)^3/b+9/32*d^2*(c+d*x)*Cos[a+b*x]^2/b^3+3/32*d^2*(c+d*x)*Cos[a+b*x]^4/b^3-1/4*(c+d*x)^3*Cos[a+b*x]^4/b-45/256*d^3*Cos[a+b*x]*Sin[a+b*x]/b^4+9/32*d*(c+d*x)^2*Cos[a+b*x]*Sin[a+b*x]/b^2-3/128*d^3*Cos[a+b*x]^3*Sin[a+b*x]/b^4+3/16*d*(c+d*x)^2*Cos[a+b*x]^3*Sin[a+b*x]/b^2");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:213
  public void test0441() {
    check( //
        "Integrate[(c+d*x)^3*Cos[a+b*x]*Cot[a+b*x]^2, x]", //
        "-6*d*(c+d*x)^2*ArcTanh[E^(I*(a+b*x))]/b^2+6*d^3*Cos[a+b*x]/b^4-3*d*(c+d*x)^2*Cos[a+b*x]/b^2-(c+d*x)^3*Csc[a+b*x]/b+6*I*d^2*(c+d*x)*PolyLog[2,-E^(I*(a+b*x))]/b^3-6*I*d^2*(c+d*x)*PolyLog[2,E^(I*(a+b*x))]/b^3-6*d^3*PolyLog[3,-E^(I*(a+b*x))]/b^4+6*d^3*PolyLog[3,E^(I*(a+b*x))]/b^4+6*d^2*(c+d*x)*Sin[a+b*x]/b^3-(c+d*x)^3*Sin[a+b*x]/b");
  }

  // 4.5.4.1 (a+b sec)^m (A+B sec+C sec^2).input:22
  public void test0442() {
    check( //
        "Integrate[Cos[c+d*x]^6*(A+C*Sec[c+d*x]^2), x]", //
        "1/16*(5*A+6*C)*x+1/16*(5*A+6*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(5*A+6*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*A*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:456
  public void test0443() {
    check( //
        "Integrate[(a+b*Sec[e+f*x]^2)^(3/2)*Tan[e+f*x], x]", //
        "-a^(3/2)*ArcTanh[Sqrt[a+b*Sec[e+f*x]^2]/Sqrt[a]]/f+1/3*(a+b*Sec[e+f*x]^2)^(3/2)/f+a*Sqrt[a+b*Sec[e+f*x]^2]/f");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:172
  public void test0444() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sec[c+d*x])^(3/2)*(A+B*Sec[c+d*x]), x]", //
        "1/8*a^(3/2)*(11*A+14*B)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+1/8*a^2*(11*A+14*B)*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/12*a^2*(7*A+6*B)*Cos[c+d*x]*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/3*a*A*Cos[c+d*x]^2*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:193
  public void test0445() {
    check( //
        "Integrate[Sec[c+d*x]^3/(a*Cos[c+d*x]+I*a*Sin[c+d*x]), x]", //
        "-1/2*I*Sec[c+d*x]^2/(a*d)+Tan[c+d*x]/(a*d)");
  }

  // 4.7.7 Trig functions.input:620
  public void test0446() {
    check( //
        "Integrate[1/(Sec[x]^2-Tan[x]^2)^2, x]", //
        "x");
  }

  // 4.7.7 Trig functions.input:89
  public void test0447() {
    check( //
        "Integrate[Sqrt[x]*Tan[Sqrt[x]], x]", //
        "2/3*I*x^(3/2)-2*x*Log[1+E^(2*I*Sqrt[x])]-PolyLog[3,-E^(2*I*Sqrt[x])]+2*I*PolyLog[2,-E^(2*I*Sqrt[x])]*Sqrt[x]");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:218
  public void test0448() {
    check( //
        "Integrate[Tan[d*(a+b*Log[c*x^n])]^2/x, x]", //
        "-Log[x]+Tan[a*d+b*d*Log[c*x^n]]/(b*d*n)");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:264
  public void test0449() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[a+b*x], x]", //
        "-1/2*Cos[a+b*x]/b-1/4*Cos[a-2*c+(b-2*d)*x]/(b-2*d)-1/4*Cos[a+2*c+(b+2*d)*x]/(b+2*d)");
  }

  // 4.5.2.1 (a+b sec)^m (c+d sec)^n.input:19
  public void test0450() {
    check( //
        "Integrate[(a+a*Sec[e+f*x])^2/(c-c*Sec[e+f*x])^3, x]", //
        "a^2*x/c^3-4/5*a^2*Tan[e+f*x]/(c^3*f*(1-Sec[e+f*x])^3)-8/15*a^2*Tan[e+f*x]/(c^3*f*(1-Sec[e+f*x])^2)-23/15*a^2*Tan[e+f*x]/(c^3*f*(1-Sec[e+f*x]))");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:458
  public void test0451() {
    check( //
        "Integrate[(c+d*x)^4*Csc[x]*Sin[3*x], x]", //
        "3/2*d^4*x-d*(c+d*x)^3+1/5*(c+d*x)^5/d-9/2*d^3*(c+d*x)*Cos[x]^2+3*d*(c+d*x)^3*Cos[x]^2+3*d^4*Cos[x]*Sin[x]-6*d^2*(c+d*x)^2*Cos[x]*Sin[x]+2*(c+d*x)^4*Cos[x]*Sin[x]+3/2*d^3*(c+d*x)*Sin[x]^2-d*(c+d*x)^3*Sin[x]^2");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:62
  public void test0452() {
    check( //
        "Integrate[Sin[e+f*x]^4*(a+b*Tan[e+f*x]^2), x]", //
        "3/8*(a-5*b)*x-1/8*(5*a-9*b)*Cos[e+f*x]*Sin[e+f*x]/f+1/4*(a-b)*Cos[e+f*x]^3*Sin[e+f*x]/f+b*Tan[e+f*x]/f");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1481
  public void test0453() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^3, x]", //
        "1/10*(49*A-9*B-C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)-1/6*(13*A-3*B-C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A-B+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(8*A-3*B-2*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-1/6*(13*A-3*B-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.6.1.4 (d cot)^n (a+b csc)^m.input:15
  public void test0454() {
    check( //
        "Integrate[Cot[x]^2/(a+a*Csc[x]), x]", //
        "-x/a-ArcTanh[Cos[x]]/a");
  }

  // 4.7.7 Trig functions.input:597
  public void test0455() {
    check( //
        "Integrate[1/((a+c*Cot[d+e*x]+b*Csc[d+e*x])^(5/2)*Sin[d+e*x]^(5/2)), x]", //
        "-2/3*(b+c*Cos[d+e*x]+a*Sin[d+e*x])*(a*Cos[d+e*x]-c*Sin[d+e*x])/((a^2-b^2+c^2)*e*(a+c*Cot[d+e*x]+b*Csc[d+e*x])^(5/2)*Sin[d+e*x]^(5/2))+8/3*(b+c*Cos[d+e*x]+a*Sin[d+e*x])^2*(a*b*Cos[d+e*x]-b*c*Sin[d+e*x])/((a^2-b^2+c^2)^2*e*(a+c*Cot[d+e*x]+b*Csc[d+e*x])^(5/2)*Sin[d+e*x]^(5/2))+8/3*b*EllipticE[1/2*(d+e*x-ArcTan[c,a]),2*Sqrt[a^2+c^2]/(b+Sqrt[a^2+c^2])]*(b+c*Cos[d+e*x]+a*Sin[d+e*x])^3/((a^2-b^2+c^2)^2*e*(a+c*Cot[d+e*x]+b*Csc[d+e*x])^(5/2)*Sin[d+e*x]^(5/2)*Sqrt[(b+c*Cos[d+e*x]+a*Sin[d+e*x])/(b+Sqrt[a^2+c^2])])+2/3*EllipticF[1/2*(d+e*x-ArcTan[c,a]),2*Sqrt[a^2+c^2]/(b+Sqrt[a^2+c^2])]*(b+c*Cos[d+e*x]+a*Sin[d+e*x])^2*Sqrt[(b+c*Cos[d+e*x]+a*Sin[d+e*x])/(b+Sqrt[a^2+c^2])]/((a^2-b^2+c^2)*e*(a+c*Cot[d+e*x]+b*Csc[d+e*x])^(5/2)*Sin[d+e*x]^(5/2))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1088
  public void test0456() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])*(a*b*B-a^2*C+b^2*B*Sec[c+d*x]+b^2*C*Sec[c+d*x]^2), x]", //
        "a^2*(b*B-a*C)*x+1/2*b*(4*a*b*B-2*a^2*C+b^2*C)*ArcTanh[Sin[c+d*x]]/d+1/2*b^2*(2*b*B+a*C)*Tan[c+d*x]/d+1/2*b^2*C*(a+b*Sec[c+d*x])*Tan[c+d*x]/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:189
  public void test0457() {
    check( //
        "Integrate[(A+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^2, x]", //
        "A*x/a^2-2/3*(2*A-C)*Tan[c+d*x]/(a^2*d*(1+Sec[c+d*x]))-1/3*(A+C)*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^2)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1471
  public void test0458() {
    check( //
        "Integrate[(a+b*Tan[e+f*x])^3/Sqrt[c+d*Tan[e+f*x]], x]", //
        "(I*a+b)^3*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c-I*d]]/(f*Sqrt[c-I*d])-(I*a-b)^3*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c+I*d]]/(f*Sqrt[c+I*d])-4/3*b^2*(b*c-4*a*d)*Sqrt[c+d*Tan[e+f*x]]/(d^2*f)+2/3*b^2*Sqrt[c+d*Tan[e+f*x]]*(a+b*Tan[e+f*x])/(d*f)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1339
  public void test0459() {
    check( //
        "Integrate[(A+C*Sec[c+d*x]^2)*Sqrt[Cos[c+d*x]]/(a+a*Sec[c+d*x])^2, x]", //
        "4*A*EllipticE[1/2*(c+d*x),2]/(a^2*d)-1/3*(5*A-C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)-1/3*(A+C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)-1/3*(5*A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:168
  public void test0460() {
    check( //
        "Integrate[Sec[e+f*x]/((a+a*Sec[e+f*x])^(3/2)*(c-c*Sec[e+f*x])^(3/2)), x]", //
        "1/2*Csc[e+f*x]/(a*c*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])-1/2*ArcTanh[Cos[e+f*x]]*Tan[e+f*x]/(a*c*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.5.10 (c+d x)^m (a+b sec)^n.input:45
  public void test0461() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*Sec[e+f*x]), x]", //
        "1/4*a*(c+d*x)^4/d-2*I*b*(c+d*x)^3*ArcTan[E^(I*(e+f*x))]/f+3*I*b*d*(c+d*x)^2*PolyLog[2,-I*E^(I*(e+f*x))]/f^2-3*I*b*d*(c+d*x)^2*PolyLog[2,I*E^(I*(e+f*x))]/f^2-6*b*d^2*(c+d*x)*PolyLog[3,-I*E^(I*(e+f*x))]/f^3+6*b*d^2*(c+d*x)*PolyLog[3,I*E^(I*(e+f*x))]/f^3-6*I*b*d^3*PolyLog[4,-I*E^(I*(e+f*x))]/f^4+6*I*b*d^3*PolyLog[4,I*E^(I*(e+f*x))]/f^4");
  }

  // 4.7.7 Trig functions.input:993
  public void test0462() {
    check( //
        "Integrate[x*Sec[x^2]^2, x]", //
        "1/2*Tan[x^2]");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:227
  public void test0463() {
    check( //
        "Integrate[1/(Sec[x]+Tan[x]), x]", //
        "Log[1+Sin[x]]");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:815
  public void test0464() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Sec[c+d*x]^2)/(a+b*Sec[c+d*x]), x]", //
        "-A*b*x/a^2+A*Sin[c+d*x]/(a*d)+2*(A*b^2+a^2*C)*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^2*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:181
  public void test0465() {
    check( //
        "Integrate[Tan[a+I*Log[x]], x]", //
        "I*x-2*I*E^(I*a)*ArcTan[x/E^(I*a)]");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:180
  public void test0466() {
    check( //
        "Integrate[(A+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x]), x]", //
        "A*x/a+C*ArcTanh[Sin[c+d*x]]/(a*d)-(A+C)*Tan[c+d*x]/(a*d*(1+Sec[c+d*x]))");
  }

  // 4.7.7 Trig functions.input:449
  public void test0467() {
    check( //
        "Integrate[(A+B*Cos[x])/(b*Cos[x]+c*Sin[x]), x]", //
        "b*B*x/(b^2+c^2)+B*c*Log[b*Cos[x]+c*Sin[x]]/(b^2+c^2)-A*ArcTanh[(c*Cos[x]-b*Sin[x])/Sqrt[b^2+c^2]]/Sqrt[b^2+c^2]");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:164
  public void test0468() {
    check( //
        "Integrate[Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]^6, x]", //
        "-2*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]*Sqrt[a]/d+2*a*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])-2/3*a^2*Tan[c+d*x]^3/(d*(a+a*Sec[c+d*x])^(3/2))+2/5*a^3*Tan[c+d*x]^5/(d*(a+a*Sec[c+d*x])^(5/2))+2*a^4*Tan[c+d*x]^7/(d*(a+a*Sec[c+d*x])^(7/2))+10/9*a^5*Tan[c+d*x]^9/(d*(a+a*Sec[c+d*x])^(9/2))+2/11*a^6*Tan[c+d*x]^11/(d*(a+a*Sec[c+d*x])^(11/2))");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:44
  public void test0469() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])^3/(c-c*Sec[e+f*x])^7, x]", //
        "-1/13*(a+a*Sec[e+f*x])^3*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^7)-3/143*(a+a*Sec[e+f*x])^3*Tan[e+f*x]/(c*f*(c-c*Sec[e+f*x])^6)-2/429*(a+a*Sec[e+f*x])^3*Tan[e+f*x]/(c^2*f*(c-c*Sec[e+f*x])^5)-2/3003*(a+a*Sec[e+f*x])^3*Tan[e+f*x]/(c^3*f*(c-c*Sec[e+f*x])^4)");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:54
  public void test0470() {
    check( //
        "Integrate[(c+d*x)^2*Cos[a+b*x]*Csc[a+b*x]^2, x]", //
        "-4*d*(c+d*x)*ArcTanh[E^(I*(a+b*x))]/b^2-(c+d*x)^2*Csc[a+b*x]/b+2*I*d^2*PolyLog[2,-E^(I*(a+b*x))]/b^3-2*I*d^2*PolyLog[2,E^(I*(a+b*x))]/b^3");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:315
  public void test0471() {
    check( //
        "Integrate[(c+d*x)^3*Sec[a+b*x]*Tan[a+b*x], x]", //
        "6*I*d*(c+d*x)^2*ArcTan[E^(I*(a+b*x))]/b^2-6*I*d^2*(c+d*x)*PolyLog[2,-I*E^(I*(a+b*x))]/b^3+6*I*d^2*(c+d*x)*PolyLog[2,I*E^(I*(a+b*x))]/b^3+6*d^3*PolyLog[3,-I*E^(I*(a+b*x))]/b^4-6*d^3*PolyLog[3,I*E^(I*(a+b*x))]/b^4+(c+d*x)^3*Sec[a+b*x]/b");
  }

  // 4.7.7 Trig functions.input:1012
  public void test0472() {
    check( //
        "Integrate[x^14*Sin[x^3], x]", //
        "-8*Cos[x^3]+4*x^6*Cos[x^3]-1/3*x^12*Cos[x^3]-8*x^3*Sin[x^3]+4/3*x^9*Sin[x^3]");
  }

  // 4.7.7 Trig functions.input:460
  public void test0473() {
    check( //
        "Integrate[(b*Cos[d+e*x]+c*Sin[d+e*x]+Sqrt[b^2+c^2])^2, x]", //
        "3/2*(b^2+c^2)*x-3/2*c*Cos[d+e*x]*Sqrt[b^2+c^2]/e+3/2*b*Sin[d+e*x]*Sqrt[b^2+c^2]/e-1/2*(c*Cos[d+e*x]-b*Sin[d+e*x])*(b*Cos[d+e*x]+c*Sin[d+e*x]+Sqrt[b^2+c^2])/e");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:276
  public void test0474() {
    check( //
        "Integrate[Tan[e+f*x]^4*(a+b*Tan[e+f*x]^2)^2, x]", //
        "(a-b)^2*x-(a-b)^2*Tan[e+f*x]/f+1/3*(a-b)^2*Tan[e+f*x]^3/f+1/5*(2*a-b)*b*Tan[e+f*x]^5/f+1/7*b^2*Tan[e+f*x]^7/f");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:82
  public void test0475() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+a*Sec[c+d*x])^3, x]", //
        "13/2*x/a^3-152/15*Sin[c+d*x]/(a^3*d)+13/2*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-1/5*Cos[c+d*x]*Sin[c+d*x]/(d*(a+a*Sec[c+d*x])^3)-11/15*Cos[c+d*x]*Sin[c+d*x]/(a*d*(a+a*Sec[c+d*x])^2)-76/15*Cos[c+d*x]*Sin[c+d*x]/(d*(a^3+a^3*Sec[c+d*x]))");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:975
  public void test0476() {
    check( //
        "Integrate[(A+B*Tan[e+f*x])/((a+I*a*Tan[e+f*x])^(3/2)*(c-I*c*Tan[e+f*x])^(5/2)), x]", //
        "-2/15*(4*I*A-B)*Sqrt[a+I*a*Tan[e+f*x]]/(a^2*c^2*f*Sqrt[c-I*c*Tan[e+f*x]])+1/3*(4*I*A-B)/(a*f*Sqrt[a+I*a*Tan[e+f*x]]*(c-I*c*Tan[e+f*x])^(5/2))-1/5*(4*I*A-B)*Sqrt[a+I*a*Tan[e+f*x]]/(a^2*f*(c-I*c*Tan[e+f*x])^(5/2))+1/3*(I*A-B)/(f*(a+I*a*Tan[e+f*x])^(3/2)*(c-I*c*Tan[e+f*x])^(5/2))-2/15*(4*I*A-B)*Sqrt[a+I*a*Tan[e+f*x]]/(a^2*c*f*(c-I*c*Tan[e+f*x])^(3/2))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:607
  public void test0477() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+a*Sec[c+d*x])^(5/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "2/15015*a*(10439*A+9230*B+8368*C)*(a+a*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d+2/143*a*(13*B+5*C)*Sec[c+d*x]^3*(a+a*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d+2/13*C*Sec[c+d*x]^3*(a+a*Sec[c+d*x])^(5/2)*Tan[c+d*x]/d+2/6435*a^3*(10439*A+9230*B+8368*C)*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+2/9009*a^3*(2717*A+2522*B+2224*C)*Sec[c+d*x]^3*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])-4/45045*a^2*(10439*A+9230*B+8368*C)*Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]/d+2/1287*a^2*(143*A+182*B+136*C)*Sec[c+d*x]^3*Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.5.2.1 (a+b sec)^m (c+d sec)^n.input:71
  public void test0478() {
    check( //
        "Integrate[(a+a*Sec[e+f*x])^(3/2)/(c-c*Sec[e+f*x])^2, x]", //
        "2*a^(3/2)*ArcTan[Sqrt[a]*Tan[e+f*x]/Sqrt[a+a*Sec[e+f*x]]]/(c^2*f)-4/3*Cot[e+f*x]^3*(a+a*Sec[e+f*x])^(3/2)/(c^2*f)+2*a*Cot[e+f*x]*Sqrt[a+a*Sec[e+f*x]]/(c^2*f)");
  }

  // 4.7.7 Trig functions.input:501
  public void test0479() {
    check( //
        "Integrate[2*a+2*b*Cos[d+e*x]-2*a*Sin[d+e*x], x]", //
        "2*a*x+2*a*Cos[d+e*x]/e+2*b*Sin[d+e*x]/e");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:456
  public void test0480() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sec[c+d*x])^(3/2)*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "2/35*(7*B-2*C)*(a+a*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d+2/7*C*(a+a*Sec[c+d*x])^(5/2)*Tan[c+d*x]/(a*d)+8/105*a^2*(21*B+19*C)*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+2/105*a*(21*B+19*C)*Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:57
  public void test0481() {
    check( //
        "Integrate[Sec[e+f*x]*(c-c*Sec[e+f*x])^3/(a+a*Sec[e+f*x])^2, x]", //
        "5*c^3*ArcTanh[Sin[e+f*x]]/(a^2*f)-5*c^3*Tan[e+f*x]/(a^2*f)+2/3*c*(c-c*Sec[e+f*x])^2*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^2)-10/3*(c^3-c^3*Sec[e+f*x])*Tan[e+f*x]/(f*(a^2+a^2*Sec[e+f*x]))");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:301
  public void test0482() {
    check( //
        "Integrate[Cot[e+f*x]^3/(a+b*Tan[e+f*x]^2)^2, x]", //
        "-1/2*Cot[e+f*x]^2/(a^2*f)-Log[Cos[e+f*x]]/((a-b)^2*f)-(a+2*b)*Log[Tan[e+f*x]]/(a^3*f)-1/2*(3*a-2*b)*b^2*Log[a+b*Tan[e+f*x]^2]/(a^3*(a-b)^2*f)+1/2*b^2/(a^2*(a-b)*f*(a+b*Tan[e+f*x]^2))");
  }

  // 4.5.10 (c+d x)^m (a+b sec)^n.input:51
  public void test0483() {
    check( //
        "Integrate[(c+d*x)^2*(a+b*Sec[e+f*x])^2, x]", //
        "-I*b^2*(c+d*x)^2/f+1/3*a^2*(c+d*x)^3/d-4*I*a*b*(c+d*x)^2*ArcTan[E^(I*(e+f*x))]/f+2*b^2*d*(c+d*x)*Log[1+E^(2*I*(e+f*x))]/f^2+4*I*a*b*d*(c+d*x)*PolyLog[2,-I*E^(I*(e+f*x))]/f^2-4*I*a*b*d*(c+d*x)*PolyLog[2,I*E^(I*(e+f*x))]/f^2-I*b^2*d^2*PolyLog[2,-E^(2*I*(e+f*x))]/f^3-4*a*b*d^2*PolyLog[3,-I*E^(I*(e+f*x))]/f^3+4*a*b*d^2*PolyLog[3,I*E^(I*(e+f*x))]/f^3+b^2*(c+d*x)^2*Tan[e+f*x]/f");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:263
  public void test0484() {
    check( //
        "Integrate[Cos[c+d*x]^3*Sin[a+b*x], x]", //
        "-1/8*Cos[a-3*c+(b-3*d)*x]/(b-3*d)-3/8*Cos[a-c+(b-d)*x]/(b-d)-3/8*Cos[a+c+(b+d)*x]/(b+d)-1/8*Cos[a+3*c+(b+3*d)*x]/(b+3*d)");
  }

  // 4.7.7 Trig functions.input:207
  public void test0485() {
    check( //
        "Integrate[x*Sin[x]/(a+b*Cos[x])^3, x]", //
        "-a*ArcTan[Sqrt[a-b]*Tan[1/2*x]/Sqrt[a+b]]/((a-b)^(3/2)*b*(a+b)^(3/2))+1/2*x/(b*(a+b*Cos[x])^2)+1/2*Sin[x]/((a^2-b^2)*(a+b*Cos[x]))");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:73
  public void test0486() {
    check( //
        "Integrate[1/(a*Sec[x]^2)^(7/2), x]", //
        "1/7*Tan[x]/(a*Sec[x]^2)^(7/2)+6/35*Tan[x]/(a*(a*Sec[x]^2)^(5/2))+8/35*Tan[x]/(a^2*(a*Sec[x]^2)^(3/2))+16/35*Tan[x]/(a^3*Sqrt[a*Sec[x]^2])");
  }

  // 4.5.2.1 (a+b sec)^m (c+d sec)^n.input:144
  public void test0487() {
    check( //
        "Integrate[(c-c*Sec[e+f*x])^(3/2)/(a+a*Sec[e+f*x])^(3/2), x]", //
        "-2*c^2*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^(3/2)*Sqrt[c-c*Sec[e+f*x]])+c^2*Log[1+Cos[e+f*x]]*Tan[e+f*x]/(a*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1475
  public void test0488() {
    check( //
        "Integrate[(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(Cos[c+d*x]^(1/2)*(a+a*Sec[c+d*x])^2), x]", //
        "-(A-C)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+1/3*(2*A+B+2*C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)+(A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.7.7 Trig functions.input:977
  public void test0489() {
    check( //
        "Integrate[E^(Cos[x]^2+Sin[x]^2), x]", //
        "E*x");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:119
  public void test0490() {
    check( //
        "Integrate[Csc[c+d*x]^6/(a+a*Sec[c+d*x])^3, x]", //
        "3/5*Cot[c+d*x]^5/(a^3*d)+10/7*Cot[c+d*x]^7/(a^3*d)+11/9*Cot[c+d*x]^9/(a^3*d)+4/11*Cot[c+d*x]^11/(a^3*d)-3/7*Csc[c+d*x]^7/(a^3*d)+7/9*Csc[c+d*x]^9/(a^3*d)-4/11*Csc[c+d*x]^11/(a^3*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:929
  public void test0491() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*(2*a*B+b*C)*ArcTanh[Sin[c+d*x]]/d+(b*B+a*C)*Tan[c+d*x]/d+1/2*b*C*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:255
  public void test0492() {
    check( //
        "Integrate[Cos[x]/(-Cot[x]+Csc[x]), x]", //
        "Cos[x]+Log[1-Cos[x]]");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:798
  public void test0493() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^2*(A+B*Tan[e+f*x])*(c-I*c*Tan[e+f*x])^3, x]", //
        "2/3*a^2*(I*A+B)*c^3*(1-I*Tan[e+f*x])^3/f-1/4*a^2*(I*A+3*B)*c^3*(1-I*Tan[e+f*x])^4/f+1/5*a^2*B*c^3*(1-I*Tan[e+f*x])^5/f");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:122
  public void test0494() {
    check( //
        "Integrate[Sec[e+f*x]*Sqrt[c-c*Sec[e+f*x]]/(a+a*Sec[e+f*x])^3, x]", //
        "2/5*c*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^3*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.7.7 Trig functions.input:1209
  public void test0495() {
    check( //
        "Integrate[f^(a+b*x)*(Cos[c+d*x]-I*Sin[c+d*x])^n, x]", //
        "-(1/E^(I*(c+d*x)))^n*f^(a+b*x)/(I*d*n-b*Log[f])");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:220
  public void test0496() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+A*Sec[c+d*x])/(a-a*Sec[c+d*x])^(5/2), x]", //
        "59/4*A*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a-a*Sec[c+d*x]]]/(a^(5/2)*d)-1/2*A*Cos[c+d*x]*Sin[c+d*x]/(d*(a-a*Sec[c+d*x])^(5/2))-15/8*A*Cos[c+d*x]*Sin[c+d*x]/(a*d*(a-a*Sec[c+d*x])^(3/2))-167/8*A*ArcTan[Sqrt[a]*Tan[c+d*x]/(Sqrt[2]*Sqrt[a-a*Sec[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+49/8*A*Sin[c+d*x]/(a^2*d*Sqrt[a-a*Sec[c+d*x]])+23/8*A*Cos[c+d*x]*Sin[c+d*x]/(a^2*d*Sqrt[a-a*Sec[c+d*x]])");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:250
  public void test0497() {
    check( //
        "Integrate[Tan[x]/(Cot[x]+Csc[x]), x]", //
        "-x+ArcTanh[Sin[x]]");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:136
  public void test0498() {
    check( //
        "Integrate[Sec[c+d*x]^12*(a*Cos[c+d*x]+b*Sin[c+d*x])^5, x]", //
        "5/16*a^5*ArcTanh[Sin[c+d*x]]/d-25/64*a^3*b^2*ArcTanh[Sin[c+d*x]]/d+15/256*a*b^4*ArcTanh[Sin[c+d*x]]/d+5/7*a^4*b*Sec[c+d*x]^7/d-10/7*a^2*b^3*Sec[c+d*x]^7/d+1/7*b^5*Sec[c+d*x]^7/d+10/9*a^2*b^3*Sec[c+d*x]^9/d-2/9*b^5*Sec[c+d*x]^9/d+1/11*b^5*Sec[c+d*x]^11/d+5/16*a^5*Sec[c+d*x]*Tan[c+d*x]/d-25/64*a^3*b^2*Sec[c+d*x]*Tan[c+d*x]/d+15/256*a*b^4*Sec[c+d*x]*Tan[c+d*x]/d+5/24*a^5*Sec[c+d*x]^3*Tan[c+d*x]/d-25/96*a^3*b^2*Sec[c+d*x]^3*Tan[c+d*x]/d+5/128*a*b^4*Sec[c+d*x]^3*Tan[c+d*x]/d+1/6*a^5*Sec[c+d*x]^5*Tan[c+d*x]/d-5/24*a^3*b^2*Sec[c+d*x]^5*Tan[c+d*x]/d+1/32*a*b^4*Sec[c+d*x]^5*Tan[c+d*x]/d+5/4*a^3*b^2*Sec[c+d*x]^7*Tan[c+d*x]/d-3/16*a*b^4*Sec[c+d*x]^7*Tan[c+d*x]/d+1/2*a*b^4*Sec[c+d*x]^7*Tan[c+d*x]^3/d");
  }

  // 4.7.7 Trig functions.input:541
  public void test0499() {
    check( //
        "Integrate[1/(5+4*Cos[d+e*x]+3*Sin[d+e*x])^(3/2), x]", //
        "1/10*(-3*Cos[d+e*x]+4*Sin[d+e*x])/(e*(5+4*Cos[d+e*x]+3*Sin[d+e*x])^(3/2))+1/10*ArcTanh[Sin[d+e*x-ArcTan[3/4]]/(Sqrt[2]*Sqrt[1+Cos[d+e*x-ArcTan[3/4]]])]/(e*Sqrt[10])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:614
  public void test0500() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sec[c+d*x])^(5/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/64*a^(5/2)*(163*A+200*B+304*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+1/24*a*(5*A+8*B)*Cos[c+d*x]^2*(a+a*Sec[c+d*x])^(3/2)*Sin[c+d*x]/d+1/4*A*Cos[c+d*x]^3*(a+a*Sec[c+d*x])^(5/2)*Sin[c+d*x]/d+1/192*a^3*(299*A+392*B+432*C)*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/32*a^2*(17*A+24*B+16*C)*Cos[c+d*x]*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1490
  public void test0501() {
    check( //
        "Integrate[(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(Cos[c+d*x]^(5/2)*(a+a*Sec[c+d*x])^4), x]", //
        "1/10*(B+8*C)*EllipticE[1/2*(c+d*x),2]/(a^4*d)+1/42*(3*A+4*B+17*C)*EllipticF[1/2*(c+d*x),2]/(a^4*d)+1/210*(15*A-B-83*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^4*d*(1+Cos[c+d*x])^2)-1/10*(B+8*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^4)+1/35*(5*A+2*B-9*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:613
  public void test0502() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sqrt[a+b*Sec[c+d*x]], x]", //
        "-2/3*a*(a-b)*EllipticE[ArcSin[Sqrt[a+b*Sec[c+d*x]]/Sqrt[a+b]],(a+b)/(a-b)]*Cot[c+d*x]*Sqrt[a+b]*Sqrt[b*(1-Sec[c+d*x])/(a+b)]*Sqrt[-b*(1+Sec[c+d*x])/(a-b)]/(b^2*d)-2/3*(a-b)*EllipticF[ArcSin[Sqrt[a+b*Sec[c+d*x]]/Sqrt[a+b]],(a+b)/(a-b)]*Cot[c+d*x]*Sqrt[a+b]*Sqrt[b*(1-Sec[c+d*x])/(a+b)]*Sqrt[-b*(1+Sec[c+d*x])/(a-b)]/(b*d)+2/3*Sqrt[a+b*Sec[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:57
  public void test0503() {
    check( //
        "Integrate[Cot[c+d*x]^9*(a+a*Sec[c+d*x])^3, x]", //
        "-1/16*a^3/(d*(1-Cos[c+d*x])^4)+5/12*a^3/(d*(1-Cos[c+d*x])^3)-39/32*a^3/(d*(1-Cos[c+d*x])^2)+9/4*a^3/(d*(1-Cos[c+d*x]))+1/32*a^3/(d*(1+Cos[c+d*x]))+57/64*a^3*Log[1-Cos[c+d*x]]/d+7/64*a^3*Log[1+Cos[c+d*x]]/d");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:107
  public void test0504() {
    check( //
        "Integrate[Cot[c+d*x]^5/(a+a*Sec[c+d*x])^3, x]", //
        "(-1/128)/(a^3*d*(1-Cos[c+d*x])^2)+5/64/(a^3*d*(1-Cos[c+d*x]))+1/40/(a^3*d*(1+Cos[c+d*x])^5)+(-13/64)/(a^3*d*(1+Cos[c+d*x])^4)+35/48/(a^3*d*(1+Cos[c+d*x])^3)+(-99/64)/(a^3*d*(1+Cos[c+d*x])^2)+303/128/(a^3*d*(1+Cos[c+d*x]))+37/256*Log[1-Cos[c+d*x]]/(a^3*d)+219/256*Log[1+Cos[c+d*x]]/(a^3*d)");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:126
  public void test0505() {
    check( //
        "Integrate[Sin[e+f*x]^3/(a+b*Sec[e+f*x]^2)^(3/2), x]", //
        "-1/3*(3*a+4*b)*Cos[e+f*x]/(a^2*f*Sqrt[a+b*Sec[e+f*x]^2])+1/3*Cos[e+f*x]^3/(a*f*Sqrt[a+b*Sec[e+f*x]^2])-2/3*b*(3*a+4*b)*Sec[e+f*x]/(a^3*f*Sqrt[a+b*Sec[e+f*x]^2])");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:385
  public void test0506() {
    check( //
        "Integrate[Sqrt[a+b*Tan[e+f*x]^2], x]", //
        "ArcTan[Sqrt[a-b]*Tan[e+f*x]/Sqrt[a+b*Tan[e+f*x]^2]]*Sqrt[a-b]/f+ArcTanh[Sqrt[b]*Tan[e+f*x]/Sqrt[a+b*Tan[e+f*x]^2]]*Sqrt[b]/f");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:180
  public void test0507() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[2*a+2*b*x]^3, x]", //
        "-8/7*Cos[a+b*x]^7/b+8/9*Cos[a+b*x]^9/b");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:375
  public void test0508() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+b*Sec[c+d*x])^4*(A+B*Sec[c+d*x]), x]", //
        "1/16*(5*a^4*A+36*a^2*A*b^2+8*A*b^4+24*a^3*b*B+32*a*b^3*B)*x+1/15*(48*a^3*A*b+53*a*A*b^3+12*a^4*B+87*a^2*b^2*B+15*b^4*B)*Sin[c+d*x]/d+1/16*(5*a^4*A+36*a^2*A*b^2+8*A*b^4+24*a^3*b*B+32*a*b^3*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/120*a^2*(25*a^2*A+48*A*b^2+72*a*b*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/10*a*(3*A*b+2*a*B)*Cos[c+d*x]^4*(a+b*Sec[c+d*x])^2*Sin[c+d*x]/d+1/6*a*A*Cos[c+d*x]^5*(a+b*Sec[c+d*x])^3*Sin[c+d*x]/d-1/15*a*(16*a^2*A*b+13*A*b^3+4*a^3*B+27*a*b^2*B)*Sin[c+d*x]^3/d");
  }

  // 4.7.7 Trig functions.input:224
  public void test0509() {
    check( //
        "Integrate[x*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]], x]", //
        "Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]/f^2+x*Sqrt[a-a*Sin[e+f*x]]*Sqrt[c+c*Sin[e+f*x]]*Tan[e+f*x]/f");
  }

  // 4.6.7 (d trig)^m (a+b (c csc)^n)^p.input:24
  public void test0510() {
    check( //
        "Integrate[(a+b*Csc[c+d*x]^2)^(1/2), x]", //
        "-ArcTan[Cot[c+d*x]*Sqrt[a]/Sqrt[a+b+b*Cot[c+d*x]^2]]*Sqrt[a]/d-ArcTanh[Cot[c+d*x]*Sqrt[b]/Sqrt[a+b+b*Cot[c+d*x]^2]]*Sqrt[b]/d");
  }

  // 4.7.7 Trig functions.input:619
  public void test0511() {
    check( //
        "Integrate[1/(Sec[x]^2-Tan[x]^2), x]", //
        "x");
  }

  // 4.7.7 Trig functions.input:628
  public void test0512() {
    check( //
        "Integrate[1/(Cot[x]^2-Csc[x]^2)^2, x]", //
        "x");
  }

  // 4.6.3.1 (a+b csc)^m (d csc)^n (A+B csc).input:17
  public void test0513() {
    check( //
        "Integrate[(a+a*Csc[c+d*x])*(A+A*Csc[c+d*x])*Sin[c+d*x], x]", //
        "2*a*A*x-a*A*ArcTanh[Cos[c+d*x]]/d-a*A*Cos[c+d*x]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:123
  public void test0514() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sec[c+d*x])^(3/2), x]", //
        "11/8*a^(3/2)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+11/8*a^2*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+11/12*a^2*Cos[c+d*x]*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/3*a^2*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:178
  public void test0515() {
    check( //
        "Integrate[1/(a-a*Sec[c+d*x]^2)^2, x]", //
        "x/a^2+Cot[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]^3/(a^2*d)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:98
  public void test0516() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sec[c+d*x])^5, x]", //
        "-1/9*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^5)+5/63*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^4)+1/21*Tan[c+d*x]/(a^2*d*(a+a*Sec[c+d*x])^3)+2/63*Tan[c+d*x]/(a*d*(a^2+a^2*Sec[c+d*x])^2)+2/63*Tan[c+d*x]/(d*(a^5+a^5*Sec[c+d*x]))");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1056
  public void test0517() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sec[c+d*x])*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*(A*b+a*B+2*b*C)*x+1/3*(2*a*A+3*b*B+3*a*C)*Sin[c+d*x]/d+1/2*(A*b+a*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*a*A*Cos[c+d*x]^2*Sin[c+d*x]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:551
  public void test0518() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+b*Sec[c+d*x])^4, x]", //
        "1/16*(8*a^4+36*a^2*b^2+5*b^4)*ArcTanh[Sin[c+d*x]]/d-1/60*a*(4*a^4-121*a^2*b^2-128*b^4)*Tan[c+d*x]/(b*d)-1/240*(8*a^4-178*a^2*b^2-75*b^4)*Sec[c+d*x]*Tan[c+d*x]/d-1/120*a*(4*a^2-53*b^2)*(a+b*Sec[c+d*x])^2*Tan[c+d*x]/(b*d)-1/120*(4*a^2-25*b^2)*(a+b*Sec[c+d*x])^3*Tan[c+d*x]/(b*d)-1/30*a*(a+b*Sec[c+d*x])^4*Tan[c+d*x]/(b*d)+1/6*(a+b*Sec[c+d*x])^5*Tan[c+d*x]/(b*d)");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:238
  public void test0519() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])/(c+d*Sec[e+f*x]), x]", //
        "a*ArcTanh[Sin[e+f*x]]/(d*f)-2*a*ArcTanh[Sqrt[c-d]*Tan[1/2*(e+f*x)]/Sqrt[c+d]]*Sqrt[c-d]/(d*f*Sqrt[c+d])");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:106
  public void test0520() {
    check( //
        "Integrate[(c+d*x)^3*Cos[a+b*x]^2*Sin[a+b*x]^2, x]", //
        "1/32*(c+d*x)^4/d+3/1024*d^3*Cos[4*a+4*b*x]/b^4-3/128*d*(c+d*x)^2*Cos[4*a+4*b*x]/b^2+3/256*d^2*(c+d*x)*Sin[4*a+4*b*x]/b^3-1/32*(c+d*x)^3*Sin[4*a+4*b*x]/b");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:90
  public void test0521() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+a*Sec[c+d*x])^2, x]", //
        "(-1/16)/(a^2*d*(1-Cos[c+d*x]))+(-1/12)/(a^2*d*(1+Cos[c+d*x])^3)+1/2/(a^2*d*(1+Cos[c+d*x])^2)+(-23/16)/(a^2*d*(1+Cos[c+d*x]))-3/16*Log[1-Cos[c+d*x]]/(a^2*d)-13/16*Log[1+Cos[c+d*x]]/(a^2*d)");
  }

  // 4.4.10 (c+d x)^m (a+b cot)^n.input:66
  public void test0522() {
    check( //
        "Integrate[(c+d*x)*(a+b*Cot[e+f*x]), x]", //
        "1/2*a*(c+d*x)^2/d-1/2*I*b*(c+d*x)^2/d+b*(c+d*x)*Log[1-E^(2*I*(e+f*x))]/f-1/2*I*b*d*PolyLog[2,E^(2*I*(e+f*x))]/f^2");
  }

  // 4.7.7 Trig functions.input:1186
  public void test0523() {
    check( //
        "Integrate[(a+b*x+c*x^2)*Sin[x], x]", //
        "-a*Cos[x]+2*c*Cos[x]-b*x*Cos[x]-c*x^2*Cos[x]+b*Sin[x]+2*c*x*Sin[x]");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:21
  public void test0524() {
    check( //
        "Integrate[Cos[x]*Sin[x]/x, x]", //
        "1/2*SinIntegral[2*x]");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:78
  public void test0525() {
    check( //
        "Integrate[Csc[a+b*x]^3*Sin[2*a+2*b*x]^4, x]", //
        "-16/5*Cos[a+b*x]^5/b");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:98
  public void test0526() {
    check( //
        "Integrate[(c+d*x)^2*Cos[a+b*x]^2*Sin[a+b*x], x]", //
        "4/9*d^2*Cos[a+b*x]/b^3+2/27*d^2*Cos[a+b*x]^3/b^3-1/3*(c+d*x)^2*Cos[a+b*x]^3/b+4/9*d*(c+d*x)*Sin[a+b*x]/b^2+2/9*d*(c+d*x)*Cos[a+b*x]^2*Sin[a+b*x]/b^2");
  }

  // 4.7.7 Trig functions.input:175
  public void test0527() {
    check( //
        "Integrate[Cos[x]*Sin[6*x]^3, x]", //
        "-3/40*Cos[5*x]-3/56*Cos[7*x]+1/136*Cos[17*x]+1/152*Cos[19*x]");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:404
  public void test0528() {
    check( //
        "Integrate[(c+d*x)*Csc[a+b*x]^3*Sec[a+b*x]^3, x]", //
        "-4*(c+d*x)*ArcTanh[E^(2*I*(a+b*x))]/b-d*Csc[2*a+2*b*x]/b^2-2*(c+d*x)*Cot[2*a+2*b*x]*Csc[2*a+2*b*x]/b+I*d*PolyLog[2,-E^(2*I*(a+b*x))]/b^2-I*d*PolyLog[2,E^(2*I*(a+b*x))]/b^2");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:120
  public void test0529() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a*Cos[c+d*x]+b*Sin[c+d*x])^5, x]", //
        "-1/5*b^5*Cos[c+d*x]^5/d-10/7*a^2*b^3*Cos[c+d*x]^7/d+2/7*b^5*Cos[c+d*x]^7/d-5/9*a^4*b*Cos[c+d*x]^9/d+10/9*a^2*b^3*Cos[c+d*x]^9/d-1/9*b^5*Cos[c+d*x]^9/d+a^5*Sin[c+d*x]/d-4/3*a^5*Sin[c+d*x]^3/d+10/3*a^3*b^2*Sin[c+d*x]^3/d+6/5*a^5*Sin[c+d*x]^5/d-6*a^3*b^2*Sin[c+d*x]^5/d+a*b^4*Sin[c+d*x]^5/d-4/7*a^5*Sin[c+d*x]^7/d+30/7*a^3*b^2*Sin[c+d*x]^7/d-10/7*a*b^4*Sin[c+d*x]^7/d+1/9*a^5*Sin[c+d*x]^9/d-10/9*a^3*b^2*Sin[c+d*x]^9/d+5/9*a*b^4*Sin[c+d*x]^9/d");
  }

  // 4.7.7 Trig functions.input:379
  public void test0530() {
    check( //
        "Integrate[(Cot[x]+Csc[x])^4, x]", //
        "x+2*Sin[x]/(1-Cos[x])-2/3*Sin[x]^3/(1-Cos[x])^3");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:807
  public void test0531() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])^2*(a^2-b^2*Sec[c+d*x]^2), x]", //
        "a^4*x+a*b*(2*a^2-b^2)*ArcTanh[Sin[c+d*x]]/d+1/3*b^2*(a^2-2*b^2)*Tan[c+d*x]/d-1/3*a*b^3*Sec[c+d*x]*Tan[c+d*x]/d-1/3*b^2*(a+b*Sec[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:1017
  public void test0532() {
    check( //
        "Integrate[3*x^2*Cos[7+x^3], x]", //
        "Sin[7+x^3]");
  }

  // 4.4.7 (d trig)^m (a+b (c cot)^n)^p.input:37
  public void test0533() {
    check( //
        "Integrate[1/Sqrt[1+Cot[x]^2], x]", //
        "-Cot[x]/Sqrt[Csc[x]^2]");
  }

  // 4.7.7 Trig functions.input:560
  public void test0534() {
    check( //
        "Integrate[1/Sqrt[b*Cos[d+e*x]+c*Sin[d+e*x]-Sqrt[b^2+c^2]], x]", //
        "-ArcTan[(b^2+c^2)^(1/4)*Sin[d+e*x-ArcTan[b,c]]/(Sqrt[2]*Sqrt[-Sqrt[b^2+c^2]+Cos[d+e*x-ArcTan[b,c]]*Sqrt[b^2+c^2]])]*Sqrt[2]/((b^2+c^2)^(1/4)*e)");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:401
  public void test0535() {
    check( //
        "Integrate[Cot[e+f*x]/(a+b*Sec[e+f*x]^2), x]", //
        "1/2*b*Log[b+a*Cos[e+f*x]^2]/(a*(a+b)*f)+Log[Sin[e+f*x]]/((a+b)*f)");
  }

  // 4.4.7 (d trig)^m (a+b (c cot)^n)^p.input:86
  public void test0536() {
    check( //
        "Integrate[Cot[x]/(a+b*Cot[x]^2)^(3/2), x]", //
        "ArcTanh[Sqrt[a+b*Cot[x]^2]/Sqrt[a-b]]/(a-b)^(3/2)+(-1)/((a-b)*Sqrt[a+b*Cot[x]^2])");
  }

  // 4.4.10 (c+d x)^m (a+b cot)^n.input:65
  public void test0537() {
    check( //
        "Integrate[(c+d*x)^2*(a+b*Cot[e+f*x]), x]", //
        "1/3*a*(c+d*x)^3/d-1/3*I*b*(c+d*x)^3/d+b*(c+d*x)^2*Log[1-E^(2*I*(e+f*x))]/f-I*b*d*(c+d*x)*PolyLog[2,E^(2*I*(e+f*x))]/f^2+1/2*b*d^2*PolyLog[3,E^(2*I*(e+f*x))]/f^3");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:48
  public void test0538() {
    check( //
        "Integrate[Sin[a+Log[c*x^n]*Sqrt[0]]^2/x, x]", //
        "Log[x]*Sin[a]^2");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:31
  public void test0539() {
    check( //
        "Integrate[Cot[c+d*x]*(a+I*a*Tan[c+d*x])^3*(A+B*Tan[c+d*x]), x]", //
        "4*a^3*(I*A+B)*x+a^3*(3*A-4*I*B)*Log[Cos[c+d*x]]/d+a^3*A*Log[Sin[c+d*x]]/d+1/2*I*a*B*(a+I*a*Tan[c+d*x])^2/d-(A-2*I*B)*(a^3+I*a^3*Tan[c+d*x])/d");
  }

  // 4.7.7 Trig functions.input:428
  public void test0540() {
    check( //
        "Integrate[(-Cos[x]+Sec[x])^(3/2), x]", //
        "8/3*Csc[x]*Sqrt[Sin[x]*Tan[x]]-2/3*Sin[x]*Sqrt[Sin[x]*Tan[x]]");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:601
  public void test0541() {
    check( //
        "Integrate[Sec[c+d*x]^3/(a+b*Tan[c+d*x]^2)^2, x]", //
        "1/2*Sin[c+d*x]/(a*d*(a-(a-b)*Sin[c+d*x]^2))+1/2*ArcTanh[Sin[c+d*x]*Sqrt[a-b]/Sqrt[a]]/(a^(3/2)*d*Sqrt[a-b])");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:62
  public void test0542() {
    check( //
        "Integrate[Csc[a+b*x]^2*Sin[2*a+2*b*x]^4, x]", //
        "x+Cos[a+b*x]*Sin[a+b*x]/b+2/3*Cos[a+b*x]^3*Sin[a+b*x]/b-8/3*Cos[a+b*x]^5*Sin[a+b*x]/b");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:229
  public void test0543() {
    check( //
        "Integrate[Cos[x]/(Sec[x]+Tan[x]), x]", //
        "x+Cos[x]");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:67
  public void test0544() {
    check( //
        "Integrate[Sec[e+f*x]*(c-c*Sec[e+f*x])^4/(a+a*Sec[e+f*x])^3, x]", //
        "-7*c^4*ArcTanh[Sin[e+f*x]]/(a^3*f)+7*c^4*Tan[e+f*x]/(a^3*f)+2/5*c*(c-c*Sec[e+f*x])^3*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^3)-14/15*(c^2-c^2*Sec[e+f*x])^2*Tan[e+f*x]/(a*f*(a+a*Sec[e+f*x])^2)+14/3*(c^4-c^4*Sec[e+f*x])*Tan[e+f*x]/(f*(a^3+a^3*Sec[e+f*x]))");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:342
  public void test0545() {
    check( //
        "Integrate[(a+a*Tan[c+d*x]^2)^(1/2), x]", //
        "ArcTanh[Sqrt[a]*Tan[c+d*x]/Sqrt[a*Sec[c+d*x]^2]]*Sqrt[a]/d");
  }

  // 4.7.6 f^(a+b x+c x^2) trig(d+e x+f x^2)^n.input:153
  public void test0546() {
    check( //
        "Integrate[f^(a+c*x^2)*Cos[d+f*x^2], x]", //
        "1/4*f^a*Erf[x*Sqrt[I*f-c*Log[f]]]*Sqrt[Pi]/(E^(I*d)*Sqrt[I*f-c*Log[f]])+1/4*E^(I*d)*f^a*Erfi[x*Sqrt[I*f+c*Log[f]]]*Sqrt[Pi]/Sqrt[I*f+c*Log[f]]");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:239
  public void test0547() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a+b*Sec[c+d*x]), x]", //
        "-2*a*b*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2)*d)+(b-a*Cos[c+d*x])*Csc[c+d*x]/((a^2-b^2)*d)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:140
  public void test0548() {
    check( //
        "Integrate[Sec[c+d*x]^2/Sqrt[a+a*Sec[c+d*x]], x]", //
        "-ArcTan[Sqrt[a]*Tan[c+d*x]/(Sqrt[2]*Sqrt[a+a*Sec[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:471
  public void test0549() {
    check( //
        "Integrate[1/(1+Tan[x]^3), x]", //
        "1/2*x-1/2*Log[Cos[x]]+1/6*Log[1+Tan[x]]-1/3*Log[1-Tan[x]+Tan[x]^2]");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:255
  public void test0550() {
    check( //
        "Integrate[Sec[c+d*x]^(1/2)*Sqrt[a+a*Sec[c+d*x]], x]", //
        "2*ArcSinh[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]*Sqrt[a]/d");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:14
  public void test0551() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])*(c-c*Sec[e+f*x])^2, x]", //
        "1/2*a*c^2*ArcTanh[Sin[e+f*x]]/f-1/2*a*c^2*Sec[e+f*x]*Tan[e+f*x]/f+1/3*a*c^2*Tan[e+f*x]^3/f");
  }

  // 4.7.7 Trig functions.input:540
  public void test0552() {
    check( //
        "Integrate[1/Sqrt[5+4*Cos[d+e*x]+3*Sin[d+e*x]], x]", //
        "ArcTanh[Sin[d+e*x-ArcTan[3/4]]/(Sqrt[2]*Sqrt[1+Cos[d+e*x-ArcTan[3/4]]])]*Sqrt[2/5]/e");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:71
  public void test0553() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a*Cos[c+d*x]+b*Sin[c+d*x])^2, x]", //
        "5/16*a^2*x+1/16*b^2*x-1/3*a*b*Cos[c+d*x]^6/d+5/16*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/16*b^2*Cos[c+d*x]*Sin[c+d*x]/d+5/24*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+1/24*b^2*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-1/6*b^2*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:249
  public void test0554() {
    check( //
        "Integrate[Sec[e+f*x]^2/(a+b*Sec[e+f*x]^2)^3, x]", //
        "3/8*ArcTan[Sqrt[b]*Tan[e+f*x]/Sqrt[a+b]]/((a+b)^(5/2)*f*Sqrt[b])+1/4*Tan[e+f*x]/((a+b)*f*(a+b+b*Tan[e+f*x]^2)^2)+3/8*Tan[e+f*x]/((a+b)^2*f*(a+b+b*Tan[e+f*x]^2))");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:310
  public void test0555() {
    check( //
        "Integrate[Tan[e+f*x]^5/(a+b*Tan[e+f*x]^2)^3, x]", //
        "-1/2*Log[a*Cos[e+f*x]^2+b*Sin[e+f*x]^2]/((a-b)^3*f)+1/4*a^2/((a-b)*b^2*f*(a+b*Tan[e+f*x]^2)^2)-1/2*a*(a-2*b)/((a-b)^2*b^2*f*(a+b*Tan[e+f*x]^2))");
  }

  // 4.7.7 Trig functions.input:575
  public void test0556() {
    check( //
        "Integrate[Sec[d+e*x]^(3/2)/(a+b*Sec[d+e*x]+c*Tan[d+e*x])^(3/2), x]", //
        "-2*Sec[d+e*x]^(3/2)*(c*Cos[d+e*x]-a*Sin[d+e*x])*(b+a*Cos[d+e*x]+c*Sin[d+e*x])/((a^2-b^2+c^2)*e*(a+b*Sec[d+e*x]+c*Tan[d+e*x])^(3/2))-2*EllipticE[1/2*(d+e*x-ArcTan[a,c]),2*Sqrt[a^2+c^2]/(b+Sqrt[a^2+c^2])]*Sec[d+e*x]^(3/2)*(b+a*Cos[d+e*x]+c*Sin[d+e*x])^2/((a^2-b^2+c^2)*e*Sqrt[(b+a*Cos[d+e*x]+c*Sin[d+e*x])/(b+Sqrt[a^2+c^2])]*(a+b*Sec[d+e*x]+c*Tan[d+e*x])^(3/2))");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:914
  public void test0557() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(a+b*Sec[c+d*x]), x]", //
        "6/5*b*EllipticE[1/2*(c+d*x),2]/d+10/21*a*EllipticF[1/2*(c+d*x),2]/d+2/5*b*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*a*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+10/21*a*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:27
  public void test0558() {
    check( //
        "Integrate[(a+b*Sec[e+f*x]^2)^2*Sin[e+f*x]^3, x]", //
        "-a*(a-2*b)*Cos[e+f*x]/f+1/3*a^2*Cos[e+f*x]^3/f+(2*a-b)*b*Sec[e+f*x]/f+1/3*b^2*Sec[e+f*x]^3/f");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:341
  public void test0559() {
    check( //
        "Integrate[Sec[a+I*Log[c*x^n]/(n*(-2+p))]^p, x]", //
        "1/2*(2-p)*x*(1+E^(2*I*a)*(c*x^n)^(2/(n*(2-p))))*Sec[a-I*Log[c*x^n]/(n*(2-p))]^p/(E^(2*I*a)*(1-p)*(c*x^n)^(2/(n*(2-p))))");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:52
  public void test0560() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])^3*Sin[c+d*x]^3, x]", //
        "2*a^3*Cos[c+d*x]/d+3/2*a^3*Cos[c+d*x]^2/d+1/3*a^3*Cos[c+d*x]^3/d-2*a^3*Log[Cos[c+d*x]]/d+3*a^3*Sec[c+d*x]/d+1/2*a^3*Sec[c+d*x]^2/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1451
  public void test0561() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Sec[c+d*x])^3*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "4/5*a^3*(5*A-5*B-9*C)*EllipticE[1/2*(c+d*x),2]/d+4/3*a^3*(5*A+5*B+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/5*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/15*(5*B+6*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))+2/15*(15*A+35*B+33*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-4/15*a^3*(5*A+20*B+21*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:309
  public void test0562() {
    check( //
        "Integrate[Cot[c+d*x]^4*(a+b*Sec[c+d*x]), x]", //
        "a*x-1/3*Cot[c+d*x]^3*(a+b*Sec[c+d*x])/d+1/3*Cot[c+d*x]*(3*a+2*b*Sec[c+d*x])/d");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:327
  public void test0563() {
    check( //
        "Integrate[(c+d*x)^3*Sin[a+b*x]*Tan[a+b*x]^2, x]", //
        "6*I*d*(c+d*x)^2*ArcTan[E^(I*(a+b*x))]/b^2-6*d^2*(c+d*x)*Cos[a+b*x]/b^3+(c+d*x)^3*Cos[a+b*x]/b-6*I*d^2*(c+d*x)*PolyLog[2,-I*E^(I*(a+b*x))]/b^3+6*I*d^2*(c+d*x)*PolyLog[2,I*E^(I*(a+b*x))]/b^3+6*d^3*PolyLog[3,-I*E^(I*(a+b*x))]/b^4-6*d^3*PolyLog[3,I*E^(I*(a+b*x))]/b^4+(c+d*x)^3*Sec[a+b*x]/b+6*d^3*Sin[a+b*x]/b^4-3*d*(c+d*x)^2*Sin[a+b*x]/b^2");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1488
  public void test0564() {
    check( //
        "Integrate[(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(Cos[c+d*x]^(1/2)*(a+a*Sec[c+d*x])^4), x]", //
        "-1/10*(8*A+B)*EllipticE[1/2*(c+d*x),2]/(a^4*d)+1/42*(17*A+4*B+3*C)*EllipticF[1/2*(c+d*x),2]/(a^4*d)-1/7*(A-B+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/35*(9*A-2*B-5*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)-1/210*(83*A+B-15*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^4*d*(1+Cos[c+d*x])^2)+1/10*(8*A+B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^4*d*(1+Cos[c+d*x]))");
  }

  // 4.7.7 Trig functions.input:487
  public void test0565() {
    check( //
        "Integrate[1/(2*a-2*a*Cos[d+e*x]+2*c*Sin[d+e*x])^4, x]", //
        "1/32*a*(5*a^2+3*c^2)*Log[a+c*Cot[1/2*(d+e*x)]]/(c^7*e)+1/48*(-c*Cos[d+e*x]-a*Sin[d+e*x])/(c^2*e*(a-a*Cos[d+e*x]+c*Sin[d+e*x])^3)+5/96*(a*c*Cos[d+e*x]+a^2*Sin[d+e*x])/(c^4*e*(a-a*Cos[d+e*x]+c*Sin[d+e*x])^2)+1/96*(-c*(15*a^2+4*c^2)*Cos[d+e*x]-a*(15*a^2+4*c^2)*Sin[d+e*x])/(c^6*e*(a-a*Cos[d+e*x]+c*Sin[d+e*x]))");
  }

  // 4.7.7 Trig functions.input:378
  public void test0566() {
    check( //
        "Integrate[(Cot[x]+Csc[x])^5, x]", //
        "(-2)/(1-Cos[x])^2+4/(1-Cos[x])+Log[1-Cos[x]]");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:549
  public void test0567() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sec[c+d*x])^3, x]", //
        "1/8*b*(9*a^2+4*b^2)*x+1/5*a*(4*a^2+15*b^2)*Sin[c+d*x]/d+1/8*b*(9*a^2+4*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+11/20*a^2*b*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a^2*Cos[c+d*x]^4*(a+b*Sec[c+d*x])*Sin[c+d*x]/d-1/15*a*(4*a^2+15*b^2)*Sin[c+d*x]^3/d");
  }

  // 4.7.7 Trig functions.input:273
  public void test0568() {
    check( //
        "Integrate[(a+b*Sin[x]^2)/(c+d*Cos[x]), x]", //
        "b*c*x/d^2-b*Sin[x]/d+2*a*ArcTan[Sqrt[c-d]*Tan[1/2*x]/Sqrt[c+d]]/(Sqrt[c-d]*Sqrt[c+d])-2*b*ArcTan[Sqrt[c-d]*Tan[1/2*x]/Sqrt[c+d]]*Sqrt[c-d]*Sqrt[c+d]/d^2");
  }

  // 4.7.5 x^m trig(a+b log(c x^n))^p.input:121
  public void test0569() {
    check( //
        "Integrate[Cos[a+b*Log[c*x^n]]^2/x, x]", //
        "1/2*Log[x]+1/2*Cos[a+b*Log[c*x^n]]*Sin[a+b*Log[c*x^n]]/(b*n)");
  }

  // 4.5.2.1 (a+b sec)^m (c+d sec)^n.input:121
  public void test0570() {
    check( //
        "Integrate[(a+a*Sec[e+f*x])^(3/2)/(c-c*Sec[e+f*x])^(3/2), x]", //
        "-2*a^2*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^(3/2)*Sqrt[a+a*Sec[e+f*x]])+a^2*Log[1-Cos[e+f*x]]*Tan[e+f*x]/(c*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:203
  public void test0571() {
    check( //
        "Integrate[1/(a*Cos[c+d*x]+I*a*Sin[c+d*x])^2, x]", //
        "1/2*I/(d*(a*Cos[c+d*x]+I*a*Sin[c+d*x])^2)");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:134
  public void test0572() {
    check( //
        "Integrate[Sec[e+f*x]*Sqrt[a+a*Sec[e+f*x]]/(c-c*Sec[e+f*x])^(3/2), x]", //
        "-1/2*Sqrt[a+a*Sec[e+f*x]]*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^(3/2))");
  }

  // 4.7.6 f^(a+b x+c x^2) trig(d+e x+f x^2)^n.input:65
  public void test0573() {
    check( //
        "Integrate[E^(a+b*x)*Cos[c+d*x]*Sin[c+d*x]^2, x]", //
        "1/4*E^(a+b*x)*b*Cos[c+d*x]/(b^2+d^2)-1/4*E^(a+b*x)*b*Cos[3*c+3*d*x]/(b^2+9*d^2)+1/4*E^(a+b*x)*d*Sin[c+d*x]/(b^2+d^2)-3/4*E^(a+b*x)*d*Sin[3*c+3*d*x]/(b^2+9*d^2)");
  }

  // 4.7.7 Trig functions.input:1097
  public void test0574() {
    check( //
        "Integrate[Cot[Sqrt[x]]*Csc[Sqrt[x]]/Sqrt[x], x]", //
        "-2*Csc[Sqrt[x]]");
  }

  // 4.6.11 (e x)^m (a+b csc(c+d x^n))^p.input:91
  public void test0575() {
    check( //
        "Integrate[1/((a+b*Csc[c+d*Sqrt[x]])*Sqrt[x]), x]", //
        "4*b*ArcTanh[(a+b*Tan[1/2*(c+d*Sqrt[x])])/Sqrt[a^2-b^2]]/(a*d*Sqrt[a^2-b^2])+2*Sqrt[x]/a");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:599
  public void test0576() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sec[c+d*x])^(3/2)*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "2/315*(63*A-18*B+22*C)*(a+a*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d+2/9*C*Sec[c+d*x]^2*(a+a*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d+2/21*(3*B+C)*(a+a*Sec[c+d*x])^(5/2)*Tan[c+d*x]/(a*d)+8/315*a^2*(63*A+57*B+47*C)*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+2/315*a*(63*A+57*B+47*C)*Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:80
  public void test0577() {
    check( //
        "Integrate[Cos[Sqrt[1-a*x]/Sqrt[1+a*x]]^3/(1-a^2*x^2), x]", //
        "-3/4*CosIntegral[Sqrt[1-a*x]/Sqrt[1+a*x]]/a-1/4*CosIntegral[3*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:46
  public void test0578() {
    check( //
        "Integrate[Cot[c+d*x]^7*(a+I*a*Tan[c+d*x])^4*(A+B*Tan[c+d*x]), x]", //
        "-8*a^4*(I*A+B)*x-8*a^4*(I*A+B)*Cot[c+d*x]/d-4*a^4*(A-I*B)*Cot[c+d*x]^2/d+1/60*a^4*(93*I*A+92*B)*Cot[c+d*x]^3/d-8*a^4*(A-I*B)*Log[Sin[c+d*x]]/d-1/6*a*A*Cot[c+d*x]^6*(a+I*a*Tan[c+d*x])^3/d-1/10*(3*I*A+2*B)*Cot[c+d*x]^5*(a^2+I*a^2*Tan[c+d*x])^2/d+1/20*(13*A-12*I*B)*Cot[c+d*x]^4*(a^4+I*a^4*Tan[c+d*x])/d");
  }

  // 4.5.4.1 (a+b sec)^m (A+B sec+C sec^2).input:84
  public void test0579() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*B*x+(A+C)*Sin[c+d*x]/d+1/2*B*Cos[c+d*x]*Sin[c+d*x]/d-1/3*A*Sin[c+d*x]^3/d");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:12
  public void test0580() {
    check( //
        "Integrate[Sin[x]^3*(a*Cos[x]+b*Sin[x]), x]", //
        "3/8*b*x-3/8*b*Cos[x]*Sin[x]-1/4*b*Cos[x]*Sin[x]^3+1/4*a*Sin[x]^4");
  }

  // 4.7.7 Trig functions.input:484
  public void test0581() {
    check( //
        "Integrate[1/(2*a-2*a*Cos[d+e*x]+2*c*Sin[d+e*x]), x]", //
        "-1/2*Log[a+c*Cot[1/2*(d+e*x)]]/(c*e)");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:235
  public void test0582() {
    check( //
        "Integrate[Sin[x]/(Sec[x]-Tan[x]), x]", //
        "-Log[1-Sin[x]]-Sin[x]");
  }

  // 4.4.9 trig^m (a+b cot^n+c cot^(2 n))^p.input:35
  public void test0583() {
    check( //
        "Integrate[Tan[d+e*x]/Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4], x]", //
        "1/2*ArcTanh[1/2*(2*a+b*Cot[d+e*x]^2)/(Sqrt[a]*Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4])]/(e*Sqrt[a])-1/2*ArcTanh[1/2*(2*a-b+(b-2*c)*Cot[d+e*x]^2)/(Sqrt[a-b+c]*Sqrt[a+b*Cot[d+e*x]^2+c*Cot[d+e*x]^4])]/(e*Sqrt[a-b+c])");
  }

  // 4.7.7 Trig functions.input:667
  public void test0584() {
    check( //
        "Integrate[(a+b*Tan[d+e*x])*(b^2+2*a*b*Tan[d+e*x]+a^2*Tan[d+e*x]^2), x]", //
        "-a*(a^2+b^2)*x-b*(a^2+b^2)*Log[Cos[d+e*x]]/e+2*a*b^2*Tan[d+e*x]/e+1/2*a^2*(a+b*Tan[d+e*x])^2/(b*e)");
  }

  // 4.7.7 Trig functions.input:250
  public void test0585() {
    check( //
        "Integrate[(a+a*Cos[x])^3*(A+B*Sec[x]), x]", //
        "1/2*a^3*(5*A+7*B)*x+a^3*B*ArcTanh[Sin[x]]+5/2*a^3*(A+B)*Sin[x]+1/3*a*A*(a+a*Cos[x])^2*Sin[x]+1/6*(5*A+3*B)*(a^3+a^3*Cos[x])*Sin[x]");
  }

  // 4.7.7 Trig functions.input:481
  public void test0586() {
    check( //
        "Integrate[(2*a-2*a*Cos[d+e*x]+2*c*Sin[d+e*x])^3, x]", //
        "4*a*(5*a^2+3*c^2)*x-4/3*c*(15*a^2+4*c^2)*Cos[d+e*x]/e-4/3*a*(15*a^2+4*c^2)*Sin[d+e*x]/e-20/3*(a*c*Cos[d+e*x]+a^2*Sin[d+e*x])*(a-a*Cos[d+e*x]+c*Sin[d+e*x])/e-8/3*(c*Cos[d+e*x]+a*Sin[d+e*x])*(a-a*Cos[d+e*x]+c*Sin[d+e*x])^2/e");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:124
  public void test0587() {
    check( //
        "Integrate[Csc[a+b*x]^2*Sin[2*a+2*b*x]^(9/2), x]", //
        "6/5*EllipticE[-1/4*Pi+a+b*x,2]/b-2/5*Cos[2*a+2*b*x]*Sin[2*a+2*b*x]^(3/2)/b-2/7*Cos[2*a+2*b*x]*Sin[2*a+2*b*x]^(7/2)/b+1/7*Csc[a+b*x]^2*Sin[2*a+2*b*x]^(11/2)/b");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:398
  public void test0588() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sec[c+d*x])^2*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/8*a^2*(8*B+7*C)*ArcTanh[Sin[c+d*x]]/d+1/6*a^2*(8*B+7*C)*Tan[c+d*x]/d+1/24*a^2*(8*B+7*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/12*(4*B-C)*(a+a*Sec[c+d*x])^2*Tan[c+d*x]/d+1/4*C*(a+a*Sec[c+d*x])^3*Tan[c+d*x]/(a*d)");
  }

  // 4.7.7 Trig functions.input:1165
  public void test0589() {
    check( //
        "Integrate[Cos[x]*(9-7*Sin[x]^3)^2/(1-Sin[x]^2), x]", //
        "-2*Log[1-Sin[x]]+128*Log[1+Sin[x]]-49*Sin[x]+63*Sin[x]^2-49/3*Sin[x]^3-49/5*Sin[x]^5");
  }

  // 4.4.10 (c+d x)^m (a+b cot)^n.input:69
  public void test0590() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*Cot[e+f*x])^2, x]", //
        "-I*b^2*(c+d*x)^3/f+1/4*a^2*(c+d*x)^4/d-1/2*I*a*b*(c+d*x)^4/d-1/4*b^2*(c+d*x)^4/d-b^2*(c+d*x)^3*Cot[e+f*x]/f+3*b^2*d*(c+d*x)^2*Log[1-E^(2*I*(e+f*x))]/f^2+2*a*b*(c+d*x)^3*Log[1-E^(2*I*(e+f*x))]/f-3*I*b^2*d^2*(c+d*x)*PolyLog[2,E^(2*I*(e+f*x))]/f^3-3*I*a*b*d*(c+d*x)^2*PolyLog[2,E^(2*I*(e+f*x))]/f^2+3/2*b^2*d^3*PolyLog[3,E^(2*I*(e+f*x))]/f^4+3*a*b*d^2*(c+d*x)*PolyLog[3,E^(2*I*(e+f*x))]/f^3+3/2*I*a*b*d^3*PolyLog[4,E^(2*I*(e+f*x))]/f^4");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1427
  public void test0591() {
    check( //
        "Integrate[(a+b*Tan[e+f*x])^3/(c+d*Tan[e+f*x]), x]", //
        "(a^3*c-3*a*b^2*c+3*a^2*b*d-b^3*d)*x/(c^2+d^2)-(3*a^2*b*c-b^3*c-a^3*d+3*a*b^2*d)*Log[Cos[e+f*x]]/((c^2+d^2)*f)-(b*c-a*d)^3*Log[c+d*Tan[e+f*x]]/(d^2*(c^2+d^2)*f)+b^2*(a+b*Tan[e+f*x])/(d*f)");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:212
  public void test0592() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a*Cos[c+d*x]+I*a*Sin[c+d*x])^3, x]", //
        "1/8*x/a^3+1/6*I*Cos[c+d*x]^3/(d*(a*Cos[c+d*x]+I*a*Sin[c+d*x])^3)+1/8*I*Cos[c+d*x]^2/(a*d*(a*Cos[c+d*x]+I*a*Sin[c+d*x])^2)+1/8*I*Cos[c+d*x]/(d*(a^3*Cos[c+d*x]+I*a^3*Sin[c+d*x]))");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:20
  public void test0593() {
    check( //
        "Integrate[Tan[c+d*x]^2*(a+I*a*Tan[c+d*x])^2*(A+B*Tan[c+d*x]), x]", //
        "-2*a^2*(A-I*B)*x+2*a^2*(I*A+B)*Log[Cos[c+d*x]]/d+2*a^2*(A-I*B)*Tan[c+d*x]/d+a^2*(I*A+B)*Tan[c+d*x]^2/d-1/12*a^2*(4*A-5*I*B)*Tan[c+d*x]^3/d+1/4*I*B*Tan[c+d*x]^3*(a^2+I*a^2*Tan[c+d*x])/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:775
  public void test0594() {
    check( //
        "Integrate[Sqrt[Sec[c+d*x]]/Sqrt[-2-3*Sec[c+d*x]], x]", //
        "2*EllipticF[1/2*(c+d*x),4/5]*Sqrt[3+2*Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/(d*Sqrt[5]*Sqrt[-2-3*Sec[c+d*x]])");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:600
  public void test0595() {
    check( //
        "Integrate[(A+B*Sec[c+d*x])/(Cos[c+d*x]^(5/2)*(a+a*Sec[c+d*x])), x]", //
        "-3*(A-B)*EllipticE[1/2*(c+d*x),2]/(a*d)-1/3*(3*A-5*B)*EllipticF[1/2*(c+d*x),2]/(a*d)-1/3*(3*A-5*B)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))+(A-B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x]))+3*(A-B)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:965
  public void test0596() {
    check( //
        "Integrate[Sec[c+d*x]^2*(B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+b*Sec[c+d*x])^2, x]", //
        "(b*B-2*a*C)*ArcTanh[Sin[c+d*x]]/(b^3*d)-2*a*(a^2*b*B-2*b^3*B-2*a^3*C+3*a*b^2*C)*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^3*(a+b)^(3/2)*d)+C*Tan[c+d*x]/(b^2*d)-a^2*(b*B-a*C)*Tan[c+d*x]/(b^2*(a^2-b^2)*d*(a+b*Sec[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:951
  public void test0597() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])/Cot[c+d*x]^(5/2), x]", //
        "2/5*b/(d*Cot[c+d*x]^(5/2))+2/3*a/(d*Cot[c+d*x]^(3/2))-(a-b)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+(a-b)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-1/2*(a+b)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+1/2*(a+b)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-2*b/(d*Sqrt[Cot[c+d*x]])");
  }

  // 4.5.10 (c+d x)^m (a+b sec)^n.input:25
  public void test0598() {
    check( //
        "Integrate[(c+d*x)^2/(a+a*Sec[e+f*x]), x]", //
        "I*(c+d*x)^2/(a*f)+1/3*(c+d*x)^3/(a*d)-4*d*(c+d*x)*Log[1+E^(I*(e+f*x))]/(a*f^2)+4*I*d^2*PolyLog[2,-E^(I*(e+f*x))]/(a*f^3)-(c+d*x)^2*Tan[1/2*e+1/2*f*x]/(a*f)");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:82
  public void test0599() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sec[c+d*x])*(A+B*Sec[c+d*x]), x]", //
        "1/2*a*(A+2*B)*x+a*(A+B)*Sin[c+d*x]/d+1/2*a*A*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:257
  public void test0600() {
    check( //
        "Integrate[(a-a*Sec[c+d*x]^2)^(7/2), x]", //
        "-a^3*Cot[c+d*x]*Log[Cos[c+d*x]]*Sqrt[-a*Tan[c+d*x]^2]/d-1/2*a^3*Sqrt[-a*Tan[c+d*x]^2]*Tan[c+d*x]/d+1/4*a^3*Sqrt[-a*Tan[c+d*x]^2]*Tan[c+d*x]^3/d-1/6*a^3*Sqrt[-a*Tan[c+d*x]^2]*Tan[c+d*x]^5/d");
  }

  // 4.7.7 Trig functions.input:864
  public void test0601() {
    check( //
        "Integrate[Cos[x]^3*(a+b*Cos[x]^2)^3*Sin[x], x]", //
        "1/8*a*(a+b*Cos[x]^2)^4/b^2-1/10*(a+b*Cos[x]^2)^5/b^2");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:635
  public void test0602() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^(3/2), x]", //
        "-1/8*(47*A-38*B+24*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/(a^(3/2)*d)-1/2*(A-B+C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Sec[c+d*x])^(3/2))+1/2*(17*A-13*B+9*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/(Sqrt[2]*Sqrt[a+a*Sec[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/8*(21*A-14*B+12*C)*Sin[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])-1/12*(13*A-12*B+6*C)*Cos[c+d*x]*Sin[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])+1/6*(5*A-3*B+3*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:204
  public void test0603() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[2*a+2*b*x]^(3/2), x]", //
        "1/6*EllipticF[-1/4*Pi+a+b*x,2]/b+1/10*Sin[2*a+2*b*x]^(5/2)/b-1/6*Cos[2*a+2*b*x]*Sqrt[Sin[2*a+2*b*x]]/b");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:99
  public void test0604() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a*Cos[c+d*x]+b*Sin[c+d*x])^3, x]", //
        "5/16*a^3*ArcTanh[Sin[c+d*x]]/d-15/128*a*b^2*ArcTanh[Sin[c+d*x]]/d+3/7*a^2*b*Sec[c+d*x]^7/d-1/7*b^3*Sec[c+d*x]^7/d+1/9*b^3*Sec[c+d*x]^9/d+5/16*a^3*Sec[c+d*x]*Tan[c+d*x]/d-15/128*a*b^2*Sec[c+d*x]*Tan[c+d*x]/d+5/24*a^3*Sec[c+d*x]^3*Tan[c+d*x]/d-5/64*a*b^2*Sec[c+d*x]^3*Tan[c+d*x]/d+1/6*a^3*Sec[c+d*x]^5*Tan[c+d*x]/d-1/16*a*b^2*Sec[c+d*x]^5*Tan[c+d*x]/d+3/8*a*b^2*Sec[c+d*x]^7*Tan[c+d*x]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:771
  public void test0605() {
    check( //
        "Integrate[1/(Sqrt[-3-2*Sec[c+d*x]]*Sqrt[Sec[c+d*x]]), x]", //
        "-2/3*EllipticE[1/2*(Pi+c+d*x),6]*Sqrt[-3-2*Sec[c+d*x]]/(d*Sqrt[-2-3*Cos[c+d*x]]*Sqrt[Sec[c+d*x]])-4/3*EllipticF[1/2*(Pi+c+d*x),6]*Sqrt[-2-3*Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/(d*Sqrt[-3-2*Sec[c+d*x]])");
  }

  // 4.5.4.1 (a+b sec)^m (A+B sec+C sec^2).input:70
  public void test0606() {
    check( //
        "Integrate[(B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(b*Sec[c+d*x])^(5/2), x]", //
        "2/3*B*Sin[c+d*x]/(b^2*d*Sqrt[b*Sec[c+d*x]])+2*C*EllipticE[1/2*(c+d*x),2]/(b^2*d*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]]/(b^3*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:424
  public void test0607() {
    check( //
        "Integrate[Cos[c+d*x]^4*(B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x]), x]", //
        "-3/2*(B-C)*x/a+(4*B-3*C)*Sin[c+d*x]/(a*d)-3/2*(B-C)*Cos[c+d*x]*Sin[c+d*x]/(a*d)-(B-C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Sec[c+d*x]))-1/3*(4*B-3*C)*Sin[c+d*x]^3/(a*d)");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:18
  public void test0608() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])/(c-c*Sec[e+f*x])^3, x]", //
        "-1/5*(a+a*Sec[e+f*x])*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^3)-1/15*(a+a*Sec[e+f*x])*Tan[e+f*x]/(c*f*(c-c*Sec[e+f*x])^2)");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:593
  public void test0609() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Sec[c+d*x])^4, x]", //
        "-b*(3*a^2+2*b^2)*ArcTanh[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(7/2)*(a+b)^(7/2)*d)-1/3*a^2*Sec[c+d*x]*Tan[c+d*x]/(b*(a^2-b^2)*d*(a+b*Sec[c+d*x])^3)-1/6*a^2*(2*a^2-7*b^2)*Tan[c+d*x]/(b^2*(a^2-b^2)^2*d*(a+b*Sec[c+d*x])^2)+1/6*a*(2*a^4-5*a^2*b^2+18*b^4)*Tan[c+d*x]/(b^2*(a^2-b^2)^3*d*(a+b*Sec[c+d*x]))");
  }

  // 4.5.7 (d trig)^m (a+b (c sec)^n)^p.input:221
  public void test0610() {
    check( //
        "Integrate[Sec[e+f*x]^6/(a+b*Sec[e+f*x]^2), x]", //
        "a^2*ArcTan[Sqrt[b]*Tan[e+f*x]/Sqrt[a+b]]/(b^(5/2)*f*Sqrt[a+b])-(a-b)*Tan[e+f*x]/(b^2*f)+1/3*Tan[e+f*x]^3/(b*f)");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:85
  public void test0611() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sec[c+d*x])*(A+B*Sec[c+d*x]), x]", //
        "3/8*a*(A+B)*x+1/5*a*(4*A+5*B)*Sin[c+d*x]/d+3/8*a*(A+B)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*(A+B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a*A*Cos[c+d*x]^4*Sin[c+d*x]/d-1/15*a*(4*A+5*B)*Sin[c+d*x]^3/d");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:113
  public void test0612() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sec[c+d*x])^4*(A+B*Sec[c+d*x]), x]", //
        "7/16*a^4*(7*A+8*B)*x+1/15*a^4*(72*A+83*B)*Sin[c+d*x]/d+7/16*a^4*(7*A+8*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/120*a^4*(159*A+176*B)*Cos[c+d*x]^2*Sin[c+d*x]/d+1/6*a*A*Cos[c+d*x]^5*(a+a*Sec[c+d*x])^3*Sin[c+d*x]/d+1/10*(3*A+2*B)*Cos[c+d*x]^4*(a^2+a^2*Sec[c+d*x])^2*Sin[c+d*x]/d+1/120*(73*A+72*B)*Cos[c+d*x]^3*(a^4+a^4*Sec[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:46
  public void test0613() {
    check( //
        "Integrate[(b*Tan[e+f*x]^n)^(1/n), x]", //
        "-Cot[e+f*x]*Log[Cos[e+f*x]]*(b*Tan[e+f*x]^n)^(1/n)/f");
  }

  // 4.4.2.1 (a+b cot)^m (c+d cot)^n.input:169
  public void test0614() {
    check( //
        "Integrate[(-a+b*Cot[c+d*x])/(a+b*Cot[c+d*x])^(1/2), x]", //
        "-(I*a-b)*ArcTanh[Sqrt[a+b*Cot[c+d*x]]/Sqrt[a-I*b]]/(d*Sqrt[a-I*b])+(I*a+b)*ArcTanh[Sqrt[a+b*Cot[c+d*x]]/Sqrt[a+I*b]]/(d*Sqrt[a+I*b])");
  }

  // 4.7.7 Trig functions.input:472
  public void test0615() {
    check( //
        "Integrate[1/(2*a+2*a*Cos[d+e*x]+2*c*Sin[d+e*x])^2, x]", //
        "-1/4*a*Log[a+c*Tan[1/2*(d+e*x)]]/(c^3*e)+1/4*(-c*Cos[d+e*x]+a*Sin[d+e*x])/(c^2*e*(a+a*Cos[d+e*x]+c*Sin[d+e*x]))");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:116
  public void test0616() {
    check( //
        "Integrate[Csc[a+b*x]*Sin[2*a+2*b*x]^(7/2), x]", //
        "-5/16*ArcSin[Cos[a+b*x]-Sin[a+b*x]]/b-5/16*Log[Cos[a+b*x]+Sin[a+b*x]+Sqrt[Sin[2*a+2*b*x]]]/b-5/12*Cos[a+b*x]*Sin[2*a+2*b*x]^(3/2)/b+1/3*Sin[a+b*x]*Sin[2*a+2*b*x]^(5/2)/b+5/8*Sin[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/b");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:142
  public void test0617() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sec[c+d*x])*(A+C*Sec[c+d*x]^2), x]", //
        "1/2*a*(A+2*C)*x+a*C*ArcTanh[Sin[c+d*x]]/d+a*A*Sin[c+d*x]/d+1/2*a*A*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:138
  public void test0618() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sec[c+d*x])*(A+C*Sec[c+d*x]^2), x]", //
        "1/8*a*(4*A+3*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(3*A+2*C)*Tan[c+d*x]/d+1/8*a*(4*A+3*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*C*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a*C*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:1088
  public void test0619() {
    check( //
        "Integrate[Cos[x]^2+Sin[x]^2, x]", //
        "x");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:141
  public void test0620() {
    check( //
        "Integrate[Sec[e+f*x]*(a+a*Sec[e+f*x])^(3/2)/(c-c*Sec[e+f*x])^(3/2), x]", //
        "-a*Sqrt[a+a*Sec[e+f*x]]*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^(3/2))-a^2*Log[1-Sec[e+f*x]]*Tan[e+f*x]/(c*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:120
  public void test0621() {
    check( //
        "Integrate[Sec[c+d*x]*(A+B*Sec[c+d*x])/(a+a*Sec[c+d*x]), x]", //
        "B*ArcTanh[Sin[c+d*x]]/(a*d)+(A-B)*Tan[c+d*x]/(d*(a+a*Sec[c+d*x]))");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:266
  public void test0622() {
    check( //
        "Integrate[Sec[c+b*x]*Sin[a+b*x], x]", //
        "-Cos[a-c]*Log[Cos[c+b*x]]/b+x*Sin[a-c]");
  }

  // 4.5.1.4 (d tan)^n (a+b sec)^m.input:189
  public void test0623() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])^(5/2)*Tan[c+d*x]^4, x]", //
        "2*a^(5/2)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d-2*a^3*Tan[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+2/3*a^4*Tan[c+d*x]^3/(d*(a+a*Sec[c+d*x])^(3/2))+6*a^5*Tan[c+d*x]^5/(d*(a+a*Sec[c+d*x])^(5/2))+34/7*a^6*Tan[c+d*x]^7/(d*(a+a*Sec[c+d*x])^(7/2))+14/9*a^7*Tan[c+d*x]^9/(d*(a+a*Sec[c+d*x])^(9/2))+2/11*a^8*Tan[c+d*x]^11/(d*(a+a*Sec[c+d*x])^(11/2))");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:406
  public void test0624() {
    check( //
        "Integrate[Tan[c+d*x]^2*(A+B*Tan[c+d*x])/(a+b*Tan[c+d*x])^(3/2), x]", //
        "(I*A+B)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/((a-I*b)^(3/2)*d)-(I*A-B)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/((a+I*b)^(3/2)*d)-2*a^2*(A*b-a*B)/(b^2*(a^2+b^2)*d*Sqrt[a+b*Tan[c+d*x]])+2*B*Sqrt[a+b*Tan[c+d*x]]/(b^2*d)");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:12
  public void test0625() {
    check( //
        "Integrate[(b*Tan[e+f*x]^2)^(1/2), x]", //
        "-Cot[e+f*x]*Log[Cos[e+f*x]]*Sqrt[b*Tan[e+f*x]^2]/f");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:484
  public void test0626() {
    check( //
        "Integrate[Sec[c+d*x]^4*(B*Sec[c+d*x]+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^(3/2), x]", //
        "-1/2*(15*B-19*C)*ArcTan[Sqrt[a]*Tan[c+d*x]/(Sqrt[2]*Sqrt[a+a*Sec[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/2*(B-C)*Sec[c+d*x]^4*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^(3/2))+1/105*(651*B-799*C)*Tan[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])+1/70*(63*B-67*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])-1/14*(7*B-11*C)*Sec[c+d*x]^3*Tan[c+d*x]/(a*d*Sqrt[a+a*Sec[c+d*x]])-1/210*(273*B-397*C)*Sqrt[a+a*Sec[c+d*x]]*Tan[c+d*x]/(a^2*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:202
  public void test0627() {
    check( //
        "Integrate[Sec[c+d*x]^4*(A+C*Sec[c+d*x]^2)/(a+a*Sec[c+d*x])^4, x]", //
        "-4*C*ArcTanh[Sin[c+d*x]]/(a^4*d)+2/105*(3*A+122*C)*Tan[c+d*x]/(a^4*d)+1/105*(3*A-88*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a^4*d*(1+Sec[c+d*x])^2)+4*C*Tan[c+d*x]/(a^4*d*(1+Sec[c+d*x]))-1/7*(A+C)*Sec[c+d*x]^4*Tan[c+d*x]/(d*(a+a*Sec[c+d*x])^4)+2/35*(A-6*C)*Sec[c+d*x]^3*Tan[c+d*x]/(a*d*(a+a*Sec[c+d*x])^3)");
  }

  // 4.7.7 Trig functions.input:436
  public void test0628() {
    check( //
        "Integrate[(Sin[x]+Tan[x])^4, x]", //
        "-61/8*x-2*ArcTanh[Sin[x]]+19/8*Cos[x]*Sin[x]+1/4*Cos[x]^3*Sin[x]-4/3*Sin[x]^3+5*Tan[x]+2*Sec[x]*Tan[x]+1/3*Tan[x]^3");
  }

  // 4.6.1.4 (d cot)^n (a+b csc)^m.input:20
  public void test0629() {
    check( //
        "Integrate[Cot[x]^7/(a+a*Csc[x]), x]", //
        "-Csc[x]/a-Csc[x]^2/a+2/3*Csc[x]^3/a+1/4*Csc[x]^4/a-1/5*Csc[x]^5/a-Log[Sin[x]]/a");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:42
  public void test0630() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])^2*Sin[c+d*x]^4, x]", //
        "-9/8*a^2*x+2*a^2*ArcTanh[Sin[c+d*x]]/d-2*a^2*Sin[c+d*x]/d-1/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-2/3*a^2*Sin[c+d*x]^3/d+a^2*Tan[c+d*x]/d");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:484
  public void test0631() {
    check( //
        "Integrate[(c+d*x)*Sec[a+b*x]*Sin[3*a+3*b*x], x]", //
        "-d*x/b-1/2*I*(c+d*x)^2/d+(c+d*x)*Log[1+E^(2*I*(a+b*x))]/b-1/2*I*d*PolyLog[2,-E^(2*I*(a+b*x))]/b^2+d*Cos[a+b*x]*Sin[a+b*x]/b^2+2*(c+d*x)*Sin[a+b*x]^2/b");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:1062
  public void test0632() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Sec[c+d*x])^2*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "a*(2*A*b+a*B)*x+1/2*(2*A*b^2+4*a*b*B+2*a^2*C+b^2*C)*ArcTanh[Sin[c+d*x]]/d+A*(a+b*Sec[c+d*x])^2*Sin[c+d*x]/d-b*(2*a*A-b*B-2*a*C)*Tan[c+d*x]/d-1/2*b^2*(2*A-C)*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:541
  public void test0633() {
    check( //
        "Integrate[(a+a*Sec[c+d*x])^4*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "a^4*A*x+1/8*a^4*(48*A+35*B+28*C)*ArcTanh[Sin[c+d*x]]/d+1/8*a^4*(40*A+35*B+28*C)*Tan[c+d*x]/d+1/20*a*(5*B+4*C)*(a+a*Sec[c+d*x])^3*Tan[c+d*x]/d+1/5*C*(a+a*Sec[c+d*x])^4*Tan[c+d*x]/d+1/60*(20*A+35*B+28*C)*(a^2+a^2*Sec[c+d*x])^2*Tan[c+d*x]/d+1/24*(32*A+35*B+28*C)*(a^4+a^4*Sec[c+d*x])*Tan[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:958
  public void test0634() {
    check( //
        "Integrate[Cot[6*x]*Csc[6*x]/(5-11*Csc[6*x]^2)^2, x]", //
        "1/60*Sin[6*x]/(11-5*Sin[6*x]^2)-1/60*ArcTanh[Sin[6*x]*Sqrt[5/11]]/Sqrt[55]");
  }

  // 4.6.1.2 (d csc)^n (a+b csc)^m.input:15
  public void test0635() {
    check( //
        "Integrate[1/(a+a*Csc[c+d*x]), x]", //
        "x/a+Cot[c+d*x]/(d*(a+a*Csc[c+d*x]))");
  }

  // 4.7.2 trig^m (a trig+b trig)^n.input:197
  public void test0636() {
    check( //
        "Integrate[Sec[c+d*x]^7/(a*Cos[c+d*x]+I*a*Sin[c+d*x]), x]", //
        "-1/6*I*Sec[c+d*x]^6/(a*d)+Tan[c+d*x]/(a*d)+2/3*Tan[c+d*x]^3/(a*d)+1/5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:93
  public void test0637() {
    check( //
        "Integrate[Sin[a+b*x]/Sin[2*a+2*b*x]^(1/2), x]", //
        "-1/2*ArcSin[Cos[a+b*x]-Sin[a+b*x]]/b-1/2*Log[Cos[a+b*x]+Sin[a+b*x]+Sqrt[Sin[2*a+2*b*x]]]/b");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:140
  public void test0638() {
    check( //
        "Integrate[Sec[c+d*x]/Sqrt[b*Sec[c+d*x]], x]", //
        "2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]]/(b*d)");
  }

  // 4.7.3 (c+d x)^m trig^n trig^p.input:287
  public void test0639() {
    check( //
        "Integrate[(c+d*x)^4*Csc[a+b*x]*Sec[a+b*x], x]", //
        "-2*(c+d*x)^4*ArcTanh[E^(2*I*(a+b*x))]/b+2*I*d*(c+d*x)^3*PolyLog[2,-E^(2*I*(a+b*x))]/b^2-2*I*d*(c+d*x)^3*PolyLog[2,E^(2*I*(a+b*x))]/b^2-3*d^2*(c+d*x)^2*PolyLog[3,-E^(2*I*(a+b*x))]/b^3+3*d^2*(c+d*x)^2*PolyLog[3,E^(2*I*(a+b*x))]/b^3-3*I*d^3*(c+d*x)*PolyLog[4,-E^(2*I*(a+b*x))]/b^4+3*I*d^3*(c+d*x)*PolyLog[4,E^(2*I*(a+b*x))]/b^4+3/2*d^4*PolyLog[5,-E^(2*I*(a+b*x))]/b^5-3/2*d^4*PolyLog[5,E^(2*I*(a+b*x))]/b^5");
  }

  // 4.7.7 Trig functions.input:523
  public void test0640() {
    check( //
        "Integrate[(2+3*Cos[d+e*x]+5*Sin[d+e*x])^(5/2), x]", //
        "-2/5*(5*Cos[d+e*x]-3*Sin[d+e*x])*(2+3*Cos[d+e*x]+5*Sin[d+e*x])^(3/2)/e-32/15*(5*Cos[d+e*x]-3*Sin[d+e*x])*Sqrt[2+3*Cos[d+e*x]+5*Sin[d+e*x]]/e+64*EllipticF[1/2*(d+e*x-ArcTan[5/3]),2/15*(17-Sqrt[34])]/(e*Sqrt[2+Sqrt[34]])+796/15*EllipticE[1/2*(d+e*x-ArcTan[5/3]),2/15*(17-Sqrt[34])]*Sqrt[2+Sqrt[34]]/e");
  }

  // 4.6.3.1 (a+b csc)^m (d csc)^n (A+B csc).input:38
  public void test0641() {
    check( //
        "Integrate[(a-a*Csc[c+d*x])*(A-A*Csc[c+d*x])/Csc[c+d*x]^2, x]", //
        "3/2*a*A*x+2*a*A*Cos[c+d*x]/d-1/2*a*A*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.5.1.3 (d sin)^n (a+b sec)^m.input:222
  public void test0642() {
    check( //
        "Integrate[(a+b*Sec[c+d*x])^3*Sin[c+d*x]^4, x]", //
        "3/8*a*(a^2-12*b^2)*x+3/2*b*(2*a^2-b^2)*ArcTanh[Sin[c+d*x]]/d-1/2*b*(17*a^2-b^2)*Sin[c+d*x]/d-1/8*a*(21*a^2-2*b^2)*Cos[c+d*x]*Sin[c+d*x]/d-1/4*(6*a^2-b^2)*(b+a*Cos[c+d*x])^2*Sin[c+d*x]/(b*d)-1/4*(4*a^2-b^2)*(b+a*Cos[c+d*x])^3*Sin[c+d*x]/(b^2*d)+a*(b+a*Cos[c+d*x])^4*Tan[c+d*x]/(b^2*d)+1/2*(b+a*Cos[c+d*x])^4*Sec[c+d*x]*Tan[c+d*x]/(b*d)");
  }

  // 4.5.4.2 (a+b sec)^m (d sec)^n (A+B sec+C sec^2).input:537
  public void test0643() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sec[c+d*x])^3*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/16*a^3*(23*A+26*B+30*C)*x+1/15*a^3*(34*A+38*B+45*C)*Sin[c+d*x]/d+1/16*a^3*(23*A+26*B+30*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/120*a^3*(73*A+86*B+90*C)*Cos[c+d*x]^2*Sin[c+d*x]/d+1/6*A*Cos[c+d*x]^5*(a+a*Sec[c+d*x])^3*Sin[c+d*x]/d+1/10*(A+2*B)*Cos[c+d*x]^4*(a^2+a^2*Sec[c+d*x])^2*Sin[c+d*x]/(a*d)+1/120*(31*A+42*B+30*C)*Cos[c+d*x]^3*(a^3+a^3*Sec[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.7.7 Trig functions.input:524
  public void test0644() {
    check( //
        "Integrate[(2+3*Cos[d+e*x]+5*Sin[d+e*x])^(3/2), x]", //
        "-2/3*(5*Cos[d+e*x]-3*Sin[d+e*x])*Sqrt[2+3*Cos[d+e*x]+5*Sin[d+e*x]]/e+20*EllipticF[1/2*(d+e*x-ArcTan[5/3]),2/15*(17-Sqrt[34])]/(e*Sqrt[2+Sqrt[34]])+16/3*EllipticE[1/2*(d+e*x-ArcTan[5/3]),2/15*(17-Sqrt[34])]*Sqrt[2+Sqrt[34]]/e");
  }

  // 4.7.7 Trig functions.input:272
  public void test0645() {
    check( //
        "Integrate[(-1+c^2/d^2+Sin[x]^2)/(c+d*Cos[x]), x]", //
        "c*x/d^2-Sin[x]/d");
  }

  // 4.7.7 Trig functions.input:997
  public void test0646() {
    check( //
        "Integrate[Cos[x]*Sin[x]/(1-Cos[x]), x]", //
        "Cos[x]+Log[1-Cos[x]]");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:58
  public void test0647() {
    check( //
        "Integrate[Csc[a+b*x]^2*Sin[2*a+2*b*x]^8, x]", //
        "5/8*x+5/8*Cos[a+b*x]*Sin[a+b*x]/b+5/12*Cos[a+b*x]^3*Sin[a+b*x]/b+1/3*Cos[a+b*x]^5*Sin[a+b*x]/b+2/7*Cos[a+b*x]^7*Sin[a+b*x]/b-16/7*Cos[a+b*x]^9*Sin[a+b*x]/b-160/21*Cos[a+b*x]^9*Sin[a+b*x]^3/b-128/7*Cos[a+b*x]^9*Sin[a+b*x]^5/b");
  }

  // 4.5.4.1 (a+b sec)^m (A+B sec+C sec^2).input:87
  public void test0648() {
    check( //
        "Integrate[Cos[c+d*x]^6*(A+B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/16*(5*A+6*C)*x+B*Sin[c+d*x]/d+1/16*(5*A+6*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(5*A+6*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*A*Cos[c+d*x]^5*Sin[c+d*x]/d-2/3*B*Sin[c+d*x]^3/d+1/5*B*Sin[c+d*x]^5/d");
  }

  // 4.6.1.2 (d csc)^n (a+b csc)^m.input:17
  public void test0649() {
    check( //
        "Integrate[Sin[x]^2/(a+a*Csc[x]), x]", //
        "3/2*x/a+2*Cos[x]/a-3/2*Cos[x]*Sin[x]/a+Cos[x]*Sin[x]/(a+a*Csc[x])");
  }

  // 4.5.2.3 (g sec)^p (a+b sec)^m (c+d sec)^n.input:280
  public void test0650() {
    check( //
        "Integrate[Sec[e+f*x]*(c+d*Sec[e+f*x])^2/(a+a*Sec[e+f*x])^3, x]", //
        "1/5*(c-d)^2*Tan[e+f*x]/(f*(a+a*Sec[e+f*x])^3)+2/15*(c-d)*(c+4*d)*Tan[e+f*x]/(a*f*(a+a*Sec[e+f*x])^2)+1/15*(2*c^2+6*c*d+7*d^2)*Tan[e+f*x]/(f*(a^3+a^3*Sec[e+f*x]))");
  }

  // 4.3.3.1 (a+b tan)^m (c+d tan)^n (A+B tan).input:588
  public void test0651() {
    check( //
        "Integrate[Cot[c+d*x]^(5/2)*(a+I*a*Tan[c+d*x])*(A+B*Tan[c+d*x]), x]", //
        "-2*(-1)^(1/4)*a*(I*A+B)*ArcTanh[(-1)^(3/4)*Sqrt[Cot[c+d*x]]]/d-2/3*a*A*Cot[c+d*x]^(3/2)/d-2*a*(I*A+B)*Sqrt[Cot[c+d*x]]/d");
  }

  // 4.3.7 (d trig)^m (a+b (c tan)^n)^p.input:284
  public void test0652() {
    check( //
        "Integrate[Tan[e+f*x]^5/(a+b*Tan[e+f*x]^2), x]", //
        "-Log[Cos[e+f*x]]/((a-b)*f)-1/2*a^2*Log[a+b*Tan[e+f*x]^2]/((a-b)*b^2*f)+1/2*Tan[e+f*x]^2/(b*f)");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:360
  public void test0653() {
    check( //
        "Integrate[Sec[c+d*x]*(a+b*Sec[c+d*x])^3*(A+B*Sec[c+d*x]), x]", //
        "1/8*(8*a^3*A+12*a*A*b^2+12*a^2*b*B+3*b^3*B)*ArcTanh[Sin[c+d*x]]/d+1/6*(16*a^2*A*b+4*A*b^3+3*a^3*B+12*a*b^2*B)*Tan[c+d*x]/d+1/24*b*(20*a*A*b+6*a^2*B+9*b^2*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/12*(4*A*b+3*a*B)*(a+b*Sec[c+d*x])^2*Tan[c+d*x]/d+1/4*B*(a+b*Sec[c+d*x])^3*Tan[c+d*x]/d");
  }

  // 4.5.1.2 (d sec)^n (a+b sec)^m.input:130
  public void test0654() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sec[c+d*x])^(5/2), x]", //
        "19/4*a^(5/2)*ArcTan[Sqrt[a]*Tan[c+d*x]/Sqrt[a+a*Sec[c+d*x]]]/d+9/4*a^3*Sin[c+d*x]/(d*Sqrt[a+a*Sec[c+d*x]])+1/2*a^2*Cos[c+d*x]*Sin[c+d*x]*Sqrt[a+a*Sec[c+d*x]]/d");
  }

  // 4.5.2.1 (a+b sec)^m (c+d sec)^n.input:131
  public void test0655() {
    check( //
        "Integrate[(a+a*Sec[e+f*x])^(5/2)/(c-c*Sec[e+f*x])^(9/2), x]", //
        "-a^3*Tan[e+f*x]/(f*(c-c*Sec[e+f*x])^(9/2)*Sqrt[a+a*Sec[e+f*x]])-1/2*a^3*Tan[e+f*x]/(c^2*f*(c-c*Sec[e+f*x])^(5/2)*Sqrt[a+a*Sec[e+f*x]])-a^3*Tan[e+f*x]/(c^3*f*(c-c*Sec[e+f*x])^(3/2)*Sqrt[a+a*Sec[e+f*x]])+a^3*Log[1-Cos[e+f*x]]*Tan[e+f*x]/(c^4*f*Sqrt[a+a*Sec[e+f*x]]*Sqrt[c-c*Sec[e+f*x]])");
  }

  // 4.6.1.2 (d csc)^n (a+b csc)^m.input:25
  public void test0656() {
    check( //
        "Integrate[(a+a*Csc[x])^(3/2), x]", //
        "-2*a^(3/2)*ArcTan[Cot[x]*Sqrt[a]/Sqrt[a+a*Csc[x]]]-2*a^2*Cot[x]/Sqrt[a+a*Csc[x]]");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:93
  public void test0657() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sec[c+d*x])^2*(A+B*Sec[c+d*x]), x]", //
        "1/8*a^2*(7*A+8*B)*x+1/3*a^2*(4*A+5*B)*Sin[c+d*x]/d+1/8*a^2*(7*A+8*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*a^2*(5*A+4*B)*Cos[c+d*x]^2*Sin[c+d*x]/d+1/4*A*Cos[c+d*x]^3*(a^2+a^2*Sec[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.5.3.1 (a+b sec)^m (d sec)^n (A+B sec).input:609
  public void test0658() {
    check( //
        "Integrate[(A+B*Sec[c+d*x])*Sqrt[Cos[c+d*x]]/(a+a*Sec[c+d*x])^3, x]", //
        "1/10*(49*A-9*B)*EllipticE[1/2*(c+d*x),2]/(a^3*d)-1/6*(13*A-3*B)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A-B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(8*A-3*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-1/6*(13*A-3*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:969
  public void test0659() {
    check( //
        "Integrate[Cot[c+d*x]^(5/2)/(a+b*Tan[c+d*x]), x]", //
        "-2*b^(7/2)*ArcTan[Sqrt[a]*Sqrt[Cot[c+d*x]]/Sqrt[b]]/(a^(5/2)*(a^2+b^2)*d)-2/3*Cot[c+d*x]^(3/2)/(a*d)-(a-b)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+(a-b)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+1/2*(a+b)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])-1/2*(a+b)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+2*b*Sqrt[Cot[c+d*x]]/(a^2*d)");
  }

  // 4.7.7 Trig functions.input:259
  public void test0660() {
    check( //
        "Integrate[(A+B*Sec[x])/(a+a*Cos[x])^(1/2), x]", //
        "2*B*ArcTanh[Sin[x]*Sqrt[a]/Sqrt[a+a*Cos[x]]]/Sqrt[a]+(A-B)*ArcTanh[Sin[x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[x]])]*Sqrt[2]/Sqrt[a]");
  }

  // 4.6.0 (a csc)^m (b trg)^n.input:75
  public void test0661() {
    check( //
        "Integrate[(a*Csc[x]^3)^(3/2), x]", //
        "-10/21*a*Cos[x]*Sqrt[a*Csc[x]^3]-2/7*a*Cot[x]*Csc[x]*Sqrt[a*Csc[x]^3]-10/21*a*EllipticF[1/4*Pi-1/2*x,2]*Sin[x]^(3/2)*Sqrt[a*Csc[x]^3]");
  }

  // 4.5.4.1 (a+b sec)^m (A+B sec+C sec^2).input:60
  public void test0662() {
    check( //
        "Integrate[Cos[c+d*x]^3*(B*Sec[c+d*x]+C*Sec[c+d*x]^2), x]", //
        "1/2*B*x+C*Sin[c+d*x]/d+1/2*B*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.6.11 (e x)^m (a+b csc(c+d x^n))^p.input:12
  public void test0663() {
    check( //
        "Integrate[x^5*(a+b*Csc[c+d*x^2]), x]", //
        "1/6*a*x^6-b*x^4*ArcTanh[E^(I*(c+d*x^2))]/d+I*b*x^2*PolyLog[2,-E^(I*(c+d*x^2))]/d^2-I*b*x^2*PolyLog[2,E^(I*(c+d*x^2))]/d^2-b*PolyLog[3,-E^(I*(c+d*x^2))]/d^3+b*PolyLog[3,E^(I*(c+d*x^2))]/d^3");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1300
  public void test0664() {
    check( //
        "Integrate[Sqrt[c+d*Tan[e+f*x]]*(a+I*a*Tan[e+f*x]), x]", //
        "-2*I*a*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c-I*d]]*Sqrt[c-I*d]/f+2*I*a*Sqrt[c+d*Tan[e+f*x]]/f");
  }

  // 4.7.1 (c trig)^m (d trig)^n.input:181
  public void test0665() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[2*a+2*b*x]^2, x]", //
        "4/3*Sin[a+b*x]^3/b-8/5*Sin[a+b*x]^5/b+4/7*Sin[a+b*x]^7/b");
  }

  // 4.5.0 (a sec)^m (b trg)^n.input:67
  public void test0666() {
    check( //
        "Integrate[(a*Sec[x]^2)^(5/2), x]", //
        "3/8*a^(5/2)*ArcTanh[Sqrt[a]*Tan[x]/Sqrt[a*Sec[x]^2]]+1/4*a*(a*Sec[x]^2)^(3/2)*Tan[x]+3/8*a^2*Sqrt[a*Sec[x]^2]*Tan[x]");
  }
}
