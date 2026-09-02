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
public class TrigFunctions1 extends AbstractRubiTestCase {
  static boolean init = true;

  public TrigFunctions1(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("TrigFunctions1");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 4.1.0 (a sin)^m (b trg)^n.input:27
  public void test0001() {
    check( //
        "Integrate[1/Sin[b*x]^(7/2), x]", //
        "6/5*EllipticE[1/4*Pi-1/2*b*x,2]/b-2/5*Cos[b*x]/(b*Sin[b*x]^(5/2))-6/5*Cos[b*x]/(b*Sqrt[Sin[b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:84
  public void test0002() {
    check( //
        "Integrate[Sec[a+b*x]^10*Sin[a+b*x]^2, x]", //
        "1/3*Tan[a+b*x]^3/b+3/5*Tan[a+b*x]^5/b+3/7*Tan[a+b*x]^7/b+1/9*Tan[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:100
  public void test0003() {
    check( //
        "Integrate[Sec[a+b*x]^3*Sin[a+b*x]^3, x]", //
        "Log[Cos[a+b*x]]/b+1/2*Tan[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:116
  public void test0004() {
    check( //
        "Integrate[Cos[a+b*x]^6*Sin[a+b*x]^4, x]", //
        "3/256*x+3/256*Cos[a+b*x]*Sin[a+b*x]/b+1/128*Cos[a+b*x]^3*Sin[a+b*x]/b+1/160*Cos[a+b*x]^5*Sin[a+b*x]/b-3/80*Cos[a+b*x]^7*Sin[a+b*x]/b-1/10*Cos[a+b*x]^7*Sin[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:132
  public void test0005() {
    check( //
        "Integrate[Sec[a+b*x]*Sin[a+b*x]^5, x]", //
        "Cos[a+b*x]^2/b-1/4*Cos[a+b*x]^4/b-Log[Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:150
  public void test0006() {
    check( //
        "Integrate[Cos[a+b*x]^5/Sin[a+b*x], x]", //
        "Log[Sin[a+b*x]]/b-Sin[a+b*x]^2/b+1/4*Sin[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:166
  public void test0007() {
    check( //
        "Integrate[Cos[a+b*x]^3/Sin[a+b*x]^2, x]", //
        "-Csc[a+b*x]/b-Sin[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:214
  public void test0008() {
    check( //
        "Integrate[Cos[x]^2/Sin[x]^6, x]", //
        "-1/3*Cot[x]^3-1/5*Cot[x]^5");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:234
  public void test0009() {
    check( //
        "Integrate[Sin[a+b*x]^2/(d*Cos[a+b*x])^(5/2), x]", //
        "2/3*Sin[a+b*x]/(b*d*(d*Cos[a+b*x])^(3/2))-4/3*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*d^2*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:250
  public void test0010() {
    check( //
        "Integrate[Sin[a+b*x]^4/(d*Cos[a+b*x])^(3/2), x]", //
        "12/5*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/(b*d^3)+2*Sin[a+b*x]^3/(b*d*Sqrt[d*Cos[a+b*x]])-24/5*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^2*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:268
  public void test0011() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(9/2)*Csc[a+b*x]^2, x]", //
        "-d*(d*Cos[a+b*x])^(7/2)*Csc[a+b*x]/b-7/5*d^3*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/b-21/5*d^4*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:312
  public void test0012() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(9/2), x]", //
        "2/7*(c*Sin[a+b*x])^(3/2)/(b*c*d*(d*Cos[a+b*x])^(7/2))+8/21*(c*Sin[a+b*x])^(3/2)/(b*c*d^3*(d*Cos[a+b*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:328
  public void test0013() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(5/2)/(d*Cos[a+b*x])^(11/2), x]", //
        "2/9*c*(c*Sin[a+b*x])^(3/2)/(b*d*(d*Cos[a+b*x])^(9/2))-2/15*c*(c*Sin[a+b*x])^(3/2)/(b*d^3*(d*Cos[a+b*x])^(5/2))-4/15*c*(c*Sin[a+b*x])^(3/2)/(b*d^5*Sqrt[d*Cos[a+b*x]])+4/15*c^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d^6*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:346
  public void test0014() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "2*Sqrt[c*Sin[a+b*x]]/(b*c*d*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:449
  public void test0015() {
    check( //
        "Integrate[Sin[e+f*x]^6*Sqrt[b*Sec[e+f*x]], x]", //
        "-40/77*b*Sin[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])-20/77*b*Sin[e+f*x]^3/(f*Sqrt[b*Sec[e+f*x]])-2/11*b*Sin[e+f*x]^5/(f*Sqrt[b*Sec[e+f*x]])+80/77*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:465
  public void test0016() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(3/2), x]", //
        "-2*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+2*b*Sin[e+f*x]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:535
  public void test0017() {
    check( //
        "Integrate[Sqrt[b*Sec[e+f*x]]/(a*Sin[e+f*x])^(1/2), x]", //
        "EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:17
  public void test0018() {
    check( //
        "Integrate[1/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-1/2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-1/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:35
  public void test0019() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^2, x]", //
        "2/5*Sec[c+d*x]^5*(a^2+a^2*Sin[c+d*x])/d+3/5*a^2*Tan[c+d*x]/d+1/5*a^2*Tan[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:51
  public void test0020() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])^3, x]", //
        "3/35*a^3*Sec[c+d*x]^5/d+2/7*a*Sec[c+d*x]^7*(a+a*Sin[c+d*x])^2/d+3/7*a^3*Tan[c+d*x]/d+2/7*a^3*Tan[c+d*x]^3/d+3/35*a^3*Tan[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:101
  public void test0021() {
    check( //
        "Integrate[Cos[c+d*x]^8/(a+a*Sin[c+d*x])^8, x]", //
        "x/a^8-2/7*Cos[c+d*x]^7/(a*d*(a+a*Sin[c+d*x])^7)+2/5*Cos[c+d*x]^5/(a^3*d*(a+a*Sin[c+d*x])^5)-2/3*Cos[c+d*x]^3/(a^2*d*(a^2+a^2*Sin[c+d*x])^3)+2*Cos[c+d*x]/(d*(a^8+a^8*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:121
  public void test0022() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-64/315*a^3*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-16/63*a^2*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/9*a*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:153
  public void test0023() {
    check( //
        "Integrate[Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(5/2), x]", //
        "3/16*a*Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d+1/4*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^(5/2)/d+3/16*a^(5/2)*ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:169
  public void test0024() {
    check( //
        "Integrate[Sec[c+d*x]^7*(a+a*Sin[c+d*x])^(7/2), x]", //
        "5/64*a^2*Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d+5/48*a*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^(5/2)/d+1/6*Sec[c+d*x]^6*(a+a*Sin[c+d*x])^(7/2)/d+5/64*a^(7/2)*ArcTanh[Sqrt[a+a*Sin[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:187
  public void test0025() {
    check( //
        "Integrate[Sec[c+d*x]^6/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-231/512*a*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-77/320*a*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-11/60*a*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-231/512*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2]*Sqrt[a])+77/128*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+11/40*Sec[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+1/5*Sec[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:223
  public void test0026() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])/Sqrt[e*Cos[c+d*x]], x]", //
        "2*a*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-2*a*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:239
  public void test0027() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^3, x]", //
        "-26/35*a^3*(e*Cos[c+d*x])^(5/2)/(d*e)-2/9*a*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^2/(d*e)-26/63*(e*Cos[c+d*x])^(5/2)*(a^3+a^3*Sin[c+d*x])/(d*e)+26/21*a^3*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+26/21*a^3*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:255
  public void test0028() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(13/2), x]", //
        "-2/77*a^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^6*Sqrt[e*Cos[c+d*x]])+4/11*a^7*Sqrt[e*Cos[c+d*x]]/(d*e^7*(a-a*Sin[c+d*x])^3)-2/77*a^8*Sqrt[e*Cos[c+d*x]]/(d*e^7*(a^2-a^2*Sin[c+d*x])^2)-2/77*a^8*Sqrt[e*Cos[c+d*x]]/(d*e^7*(a^4-a^4*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:273
  public void test0029() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]/(a+a*Sin[c+d*x])^2, x]", //
        "-2/5*(e*Cos[c+d*x])^(3/2)/(d*e*(a+a*Sin[c+d*x])^2)-2/5*(e*Cos[c+d*x])^(3/2)/(d*e*(a^2+a^2*Sin[c+d*x]))-2/5*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:289
  public void test0030() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(13/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-154/15*e^5*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a^4*d)-4*e*(e*Cos[c+d*x])^(11/2)/(a*d*(a+a*Sin[c+d*x])^3)-44/3*e^3*(e*Cos[c+d*x])^(7/2)/(d*(a^4+a^4*Sin[c+d*x]))-154/5*e^6*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:309
  public void test0031() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-15/32*a^3*(e*Cos[c+d*x])^(7/2)/(d*e*(a+a*Sin[c+d*x])^(3/2))+15/64*a^2*e*(e*Cos[c+d*x])^(3/2)/(d*Sqrt[a+a*Sin[c+d*x]])-3/8*a^2*(e*Cos[c+d*x])^(7/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])-1/4*a*(e*Cos[c+d*x])^(7/2)*Sqrt[a+a*Sin[c+d*x]]/(d*e)+45/64*a*e^(5/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+45/64*a*e^(5/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:325
  public void test0032() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)/(e*Cos[c+d*x])^(11/2), x]", //
        "2*(a+a*Sin[c+d*x])^(5/2)/(d*e*(e*Cos[c+d*x])^(9/2))-8/5*(a+a*Sin[c+d*x])^(7/2)/(a*d*e*(e*Cos[c+d*x])^(9/2))+16/45*(a+a*Sin[c+d*x])^(9/2)/(a^2*d*e*(e*Cos[c+d*x])^(9/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:343
  public void test0033() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])^(3/2)), x]", //
        "(-2/11)/(d*e*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^(3/2))+128/77*(a+a*Sin[c+d*x])^(3/2)/(a^3*d*e*(e*Cos[c+d*x])^(5/2))-256/385*(a+a*Sin[c+d*x])^(5/2)/(a^4*d*e*(e*Cos[c+d*x])^(5/2))+(-16/77)/(a*d*e*(e*Cos[c+d*x])^(5/2)*Sqrt[a+a*Sin[c+d*x]])-32/77*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*e*(e*Cos[c+d*x])^(5/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:448
  public void test0034() {
    check( //
        "Integrate[Sec[c+d*x]^5*(a+b*Sin[c+d*x])^3, x]", //
        "3/8*a*(a^2-b^2)*ArcTanh[Sin[c+d*x]]/d+3/8*a*Sec[c+d*x]^2*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d+1/4*Sec[c+d*x]^3*(a+b*Sin[c+d*x])^3*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:464
  public void test0035() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+b*Sin[c+d*x])^8, x]", //
        "35/8*b^4*(16*a^4+16*a^2*b^2+b^4)*x+1/6*a*b*(8*a^6-104*a^4*b^2-803*a^2*b^4-256*b^6)*Cos[c+d*x]/d+1/24*b^2*(16*a^6-200*a^4*b^2-866*a^2*b^4-105*b^6)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*a*b*(8*a^4-88*a^2*b^2-151*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d+1/12*b*(8*a^4-72*a^2*b^2-35*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/d+1/3*a*b*(2*a^2-13*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^4/d+1/3*b*(2*a^2-7*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^5/d+1/3*Sec[c+d*x]^3*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^7/d-1/3*Sec[c+d*x]*(a+b*Sin[c+d*x])^6*(5*a*b-(2*a^2-7*b^2)*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:568
  public void test0036() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Sin[c+d*x])^(3/2), x]", //
        "-2/3*(a+b*Sin[c+d*x])^(3/2)/(b^3*d)+2*(a^2-b^2)/(b^3*d*Sqrt[a+b*Sin[c+d*x]])+4*a*Sqrt[a+b*Sin[c+d*x]]/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:604
  public void test0037() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^2, x]", //
        "-18/35*a*b*(e*Cos[c+d*x])^(5/2)/(d*e)-2/7*b*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])/(d*e)+2/21*(7*a^2+2*b^2)*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/21*(7*a^2+2*b^2)*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:620
  public void test0038() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^4, x]", //
        "-10/3003*a*b*(115*a^2+94*b^2)*(e*Cos[c+d*x])^(7/2)/(d*e)+2/195*(39*a^4+52*a^2*b^2+4*b^4)*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-2/429*b*(73*a^2+22*b^2)*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])/(d*e)-38/143*a*b*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^2/(d*e)-2/13*b*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^3/(d*e)+2/65*(39*a^4+52*a^2*b^2+4*b^4)*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:15
  public void test0039() {
    check( //
        "Integrate[Cot[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "a*Log[Sin[c+d*x]]/d+a*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:31
  public void test0040() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2*Tan[c+d*x]^4, x]", //
        "7/2*a^2*x-16/3*a^2*Cos[c+d*x]/d-7/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d-8/3*a^2*Cos[c+d*x]*Sin[c+d*x]^2/(d*(1-Sin[c+d*x]))+1/3*a^4*Cos[c+d*x]*Sin[c+d*x]^3/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:66
  public void test0041() {
    check( //
        "Integrate[Tan[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "Sec[c+d*x]/(a*d)-Sec[c+d*x]^3/(a*d)+3/5*Sec[c+d*x]^5/(a*d)-1/7*Sec[c+d*x]^7/(a*d)+1/7*Tan[c+d*x]^7/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:82
  public void test0042() {
    check( //
        "Integrate[Cot[c+d*x]^9/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*Csc[c+d*x]^2/(a^2*d)+2/3*Csc[c+d*x]^3/(a^2*d)+1/4*Csc[c+d*x]^4/(a^2*d)-4/5*Csc[c+d*x]^5/(a^2*d)+1/6*Csc[c+d*x]^6/(a^2*d)+2/7*Csc[c+d*x]^7/(a^2*d)-1/8*Csc[c+d*x]^8/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:98
  public void test0043() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+a*Sin[c+d*x])^4, x]", //
        "4*Csc[c+d*x]/(a^4*d)-1/2*Csc[c+d*x]^2/(a^4*d)+9*Log[Sin[c+d*x]]/(a^4*d)-9*Log[1+Sin[c+d*x]]/(a^4*d)+1/(d*(a^2+a^2*Sin[c+d*x])^2)+5/(d*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:124
  public void test0044() {
    check( //
        "Integrate[Cot[e+f*x]^2/Sqrt[a+a*Sin[e+f*x]], x]", //
        "ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(f*Sqrt[a])-Cot[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:181
  public void test0045() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])*Tan[c+d*x]^2, x]", //
        "-a*x+b*Cos[c+d*x]/d+b*Sec[c+d*x]/d+a*Tan[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:197
  public void test0046() {
    check( //
        "Integrate[Cot[c+d*x]*(a+b*Sin[c+d*x])^3, x]", //
        "a^3*Log[Sin[c+d*x]]/d+3*a^2*b*Sin[c+d*x]/d+3/2*a*b^2*Sin[c+d*x]^2/d+1/3*b^3*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:215
  public void test0047() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "b*ArcTanh[Cos[c+d*x]]/(a^2*d)-Cot[c+d*x]/(a*d)-2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a^2*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:19
  public void test0048() {
    check( //
        "Integrate[(c+d*x)^4*Sin[a+b*x]^2, x]", //
        "3/4*d^4*x/b^4-1/2*d*(c+d*x)^3/b^2+1/10*(c+d*x)^5/d-3/4*d^4*Cos[a+b*x]*Sin[a+b*x]/b^5+3/2*d^2*(c+d*x)^2*Cos[a+b*x]*Sin[a+b*x]/b^3-1/2*(c+d*x)^4*Cos[a+b*x]*Sin[a+b*x]/b-3/2*d^3*(c+d*x)*Sin[a+b*x]^2/b^4+d*(c+d*x)^3*Sin[a+b*x]^2/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:37
  public void test0049() {
    check( //
        "Integrate[(c+d*x)^2*Csc[a+b*x], x]", //
        "-2*(c+d*x)^2*ArcTanh[E^(I*(a+b*x))]/b+2*I*d*(c+d*x)*PolyLog[2,-E^(I*(a+b*x))]/b^2-2*I*d*(c+d*x)*PolyLog[2,E^(I*(a+b*x))]/b^2-2*d^2*PolyLog[3,-E^(I*(a+b*x))]/b^3+2*d^2*PolyLog[3,E^(I*(a+b*x))]/b^3");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:110
  public void test0050() {
    check( //
        "Integrate[x^(-3+m)*Sin[a+b*x], x]", //
        "-1/2*I*E^(I*a)*b^2*x^m*Gamma[-2+m,-I*b*x]/(-I*b*x)^m+1/2*I*b^2*x^m*Gamma[-2+m,I*b*x]/(E^(I*a)*(I*b*x)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:140
  public void test0051() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*x)^2, x]", //
        "-a/(d*(c+d*x))+a*f*CosIntegral[c*f/d+f*x]*Cos[e-c*f/d]/d^2-a*f*SinIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d^2-a*Sin[e+f*x]/(d*(c+d*x))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:162
  public void test0052() {
    check( //
        "Integrate[(c+d*x)/(a-a*Sin[e+f*x]), x]", //
        "2*d*Log[Cos[1/4*Pi+1/2*e+1/2*f*x]]/(a*f^2)+(c+d*x)*Tan[1/4*Pi+1/2*e+1/2*f*x]/(a*f)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:188
  public void test0053() {
    check( //
        "Integrate[x^3/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-3*x^2/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-1/2*x^3*Cot[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])-24*x*ArcTanh[E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])-x^3*ArcTanh[E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])+24*I*PolyLog[2,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^4*Sqrt[a+a*Sin[e+f*x]])+3*I*x^2*PolyLog[2,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-24*I*PolyLog[2,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^4*Sqrt[a+a*Sin[e+f*x]])-3*I*x^2*PolyLog[2,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-12*x*PolyLog[3,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])+12*x*PolyLog[3,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])-24*I*PolyLog[4,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^4*Sqrt[a+a*Sin[e+f*x]])+24*I*PolyLog[4,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^4*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:12
  public void test0054() {
    check( //
        "Integrate[x*(a+b*x)*Sin[c+d*x], x]", //
        "2*b*Cos[c+d*x]/d^3-a*x*Cos[c+d*x]/d-b*x^2*Cos[c+d*x]/d+a*Sin[c+d*x]/d^2+2*b*x*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:66
  public void test0055() {
    check( //
        "Integrate[(a+b*x^2)^2*Sin[c+d*x], x]", //
        "-24*b^2*Cos[c+d*x]/d^5+4*a*b*Cos[c+d*x]/d^3-a^2*Cos[c+d*x]/d+12*b^2*x^2*Cos[c+d*x]/d^3-2*a*b*x^2*Cos[c+d*x]/d-b^2*x^4*Cos[c+d*x]/d-24*b^2*x*Sin[c+d*x]/d^4+4*a*b*x*Sin[c+d*x]/d^2+4*b^2*x^3*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:104
  public void test0056() {
    check( //
        "Integrate[(a+b*x^3)*Sin[c+d*x]/x, x]", //
        "2*b*Cos[c+d*x]/d^3-b*x^2*Cos[c+d*x]/d+a*Cos[c]*SinIntegral[d*x]+a*CosIntegral[d*x]*Sin[c]+2*b*x*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:13
  public void test0057() {
    check( //
        "Integrate[x^3*(a+b*Sin[c+d*x^2]), x]", //
        "1/4*a*x^4-1/2*b*x^2*Cos[c+d*x^2]/d+1/2*b*Sin[c+d*x^2]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:49
  public void test0058() {
    check( //
        "Integrate[x^3/(a+b*Sin[c+d*x^2]), x]", //
        "-1/2*I*x^2*Log[1-I*E^(I*(c+d*x^2))*b/(a-Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])+1/2*I*x^2*Log[1-I*E^(I*(c+d*x^2))*b/(a+Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])-1/2*PolyLog[2,I*E^(I*(c+d*x^2))*b/(a-Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])+1/2*PolyLog[2,I*E^(I*(c+d*x^2))*b/(a+Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:88
  public void test0059() {
    check( //
        "Integrate[x^3*(a+b*Sin[c+d*x^3]), x]", //
        "1/4*a*x^4-1/3*b*x*Cos[c+d*x^3]/d-1/18*E^(I*c)*b*x*Gamma[1/3,-I*d*x^3]/(d*(-I*d*x^3)^(1/3))-1/18*b*x*Gamma[1/3,I*d*x^3]/(E^(I*c)*d*(I*d*x^3)^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:106
  public void test0060() {
    check( //
        "Integrate[x^5/(a+b*Sin[c+d*x^3]), x]", //
        "-1/3*I*x^3*Log[1-I*E^(I*(c+d*x^3))*b/(a-Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])+1/3*I*x^3*Log[1-I*E^(I*(c+d*x^3))*b/(a+Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])-1/3*PolyLog[2,I*E^(I*(c+d*x^3))*b/(a-Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])+1/3*PolyLog[2,I*E^(I*(c+d*x^3))*b/(a+Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:147
  public void test0061() {
    check( //
        "Integrate[x*Sin[a+b/x]^2, x]", //
        "-b^2*CosIntegral[2*b/x]*Cos[2*a]+b^2*SinIntegral[2*b/x]*Sin[2*a]+1/2*x^2*Sin[a+b/x]^2+1/2*b*x*Sin[2*(a+b/x)]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:171
  public void test0062() {
    check( //
        "Integrate[Sin[x^(1/3)]^3, x]", //
        "14/3*Cos[x^(1/3)]-2*x^(2/3)*Cos[x^(1/3)]-2/9*Cos[x^(1/3)]^3+4*x^(1/3)*Sin[x^(1/3)]-x^(2/3)*Cos[x^(1/3)]*Sin[x^(1/3)]^2+2/3*x^(1/3)*Sin[x^(1/3)]^3");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:195
  public void test0063() {
    check( //
        "Integrate[x^(-1-2*n)*Sin[a+b*x^n], x]", //
        "-1/2*b*Cos[a+b*x^n]/(n*x^n)-1/2*b^2*Cos[a]*SinIntegral[b*x^n]/n-1/2*b^2*CosIntegral[b*x^n]*Sin[a]/n-1/2*Sin[a+b*x^n]/(n*x^(2*n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:231
  public void test0064() {
    check( //
        "Integrate[(e+f*x)^2*Sin[a+b*(c+d*x)^3], x]", //
        "-1/3*f^2*Cos[a+b*(c+d*x)^3]/(b*d^3)+1/6*I*E^(I*a)*(d*e-c*f)^2*(c+d*x)*Gamma[1/3,-I*b*(c+d*x)^3]/(d^3*(-I*b*(c+d*x)^3)^(1/3))-1/6*I*(d*e-c*f)^2*(c+d*x)*Gamma[1/3,I*b*(c+d*x)^3]/(E^(I*a)*d^3*(I*b*(c+d*x)^3)^(1/3))+1/3*I*E^(I*a)*f*(d*e-c*f)*(c+d*x)^2*Gamma[2/3,-I*b*(c+d*x)^3]/(d^3*(-I*b*(c+d*x)^3)^(2/3))-1/3*I*f*(d*e-c*f)*(c+d*x)^2*Gamma[2/3,I*b*(c+d*x)^3]/(E^(I*a)*d^3*(I*b*(c+d*x)^3)^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:259
  public void test0065() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(3/2)], x]", //
        "1/3*I*E^(I*a)*(c+d*x)*Gamma[2/3,-I*b*(c+d*x)^(3/2)]/(d*(-I*b*(c+d*x)^(3/2))^(2/3))-1/3*I*(c+d*x)*Gamma[2/3,I*b*(c+d*x)^(3/2)]/(E^(I*a)*d*(I*b*(c+d*x)^(3/2))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:285
  public void test0066() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(2/3)], x]", //
        "-3/2*(c+d*x)^(1/3)*Cos[a+b*(c+d*x)^(2/3)]/(b*d)+3/2*Cos[a]*FresnelC[(c+d*x)^(1/3)*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/(b^(3/2)*d)-3/2*FresnelS[(c+d*x)^(1/3)*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*Sqrt[1/2*Pi]/(b^(3/2)*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:311
  public void test0067() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(1/3)]/(c*e+d*e*x)^(7/3), x]", //
        "1/8*b^3*Cos[a+b*(c+d*x)^(1/3)]/(d*e^2*(e*(c+d*x))^(1/3))-1/4*b*Cos[a+b*(c+d*x)^(1/3)]/(d*e^2*(c+d*x)^(2/3)*(e*(c+d*x))^(1/3))+1/8*b^4*(c+d*x)^(1/3)*Cos[a]*SinIntegral[b*(c+d*x)^(1/3)]/(d*e^2*(e*(c+d*x))^(1/3))+1/8*b^4*(c+d*x)^(1/3)*CosIntegral[b*(c+d*x)^(1/3)]*Sin[a]/(d*e^2*(e*(c+d*x))^(1/3))-3/4*Sin[a+b*(c+d*x)^(1/3)]/(d*e^2*(c+d*x)*(e*(c+d*x))^(1/3))+1/8*b^2*Sin[a+b*(c+d*x)^(1/3)]/(d*e^2*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:356
  public void test0068() {
    check( //
        "Integrate[a+b*Sin[c+d*(f+g*x)^n], x]", //
        "a*x+1/2*I*E^(I*c)*b*(f+g*x)*Gamma[1/n,-I*d*(f+g*x)^n]/(g*n*(-I*d*(f+g*x)^n)^(1/n))-1/2*I*b*(f+g*x)*Gamma[1/n,I*d*(f+g*x)^n]/(E^(I*c)*g*n*(I*d*(f+g*x)^n)^(1/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:422
  public void test0069() {
    check( //
        "Integrate[x^3*(c*Sin[a+b*x]^3)^(1/3), x]", //
        "-6*(c*Sin[a+b*x]^3)^(1/3)/b^4+3*x^2*(c*Sin[a+b*x]^3)^(1/3)/b^2+6*x*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/b^3-x^3*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:442
  public void test0070() {
    check( //
        "Integrate[x^3*(c*Sin[a+b*x^n]^3)^(1/3), x]", //
        "1/2*I*E^(I*a)*x^4*Csc[a+b*x^n]*Gamma[4/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*(-I*b*x^n)^(4/n))-1/2*I*x^4*Csc[a+b*x^n]*Gamma[4/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*(I*b*x^n)^(4/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:462
  public void test0071() {
    check( //
        "Integrate[x^3*(c*Sin[a+b*x^2]^3)^(2/3), x]", //
        "1/8*(c*Sin[a+b*x^2]^3)^(2/3)/b^2-1/4*x^2*Cot[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(2/3)/b+1/8*x^4*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:31
  public void test0072() {
    check( //
        "Integrate[x^2*Sin[1/4+x+x^2]^2, x]", //
        "1/6*x^3+1/16*Sin[1/2+2*x+2*x^2]-1/8*x*Sin[1/2+2*x+2*x^2]-1/16*FresnelC[(1+2*x)/Sqrt[Pi]]*Sqrt[Pi]+1/16*FresnelS[(1+2*x)/Sqrt[Pi]]*Sqrt[Pi]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:21
  public void test0073() {
    check( //
        "Integrate[Csc[x]/(a+a*Sin[x]), x]", //
        "-ArcTanh[Cos[x]]/a+Cos[x]/(a+a*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:38
  public void test0074() {
    check( //
        "Integrate[Sin[x]^2/(a+a*Sin[x])^3, x]", //
        "-1/5*Cos[x]/(a+a*Sin[x])^3+8/15*Cos[x]/(a*(a+a*Sin[x])^2)-7/15*Cos[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:74
  public void test0075() {
    check( //
        "Integrate[Csc[c+d*x]^2*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-5*a^(5/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-a^3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-a^2*Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:92
  public void test0076() {
    check( //
        "Integrate[Csc[c+d*x]/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)+1/2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))+5/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:206
  public void test0077() {
    check( //
        "Integrate[Csc[e+f*x]^3*(a+b*Sin[e+f*x])^2, x]", //
        "-1/2*(a^2+2*b^2)*ArcTanh[Cos[e+f*x]]/f-2*a*b*Cot[e+f*x]/f-1/2*a^2*Cot[e+f*x]*Csc[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:226
  public void test0078() {
    check( //
        "Integrate[Csc[x]/(a+b*Sin[x]), x]", //
        "-ArcTanh[Cos[x]]/a-2*b*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a*Sqrt[a^2-b^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:242
  public void test0079() {
    check( //
        "Integrate[Sin[x]/(a+b*Sin[x])^3, x]", //
        "-3*a*b*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(5/2)-1/2*a*Cos[x]/((a^2-b^2)*(a+b*Sin[x])^2)-1/2*(a^2+2*b^2)*Cos[x]/((a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:315
  public void test0080() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^5, x]", //
        "45/128*a^3*c^5*x+9/56*a^3*c^5*Cos[e+f*x]^7/f+45/128*a^3*c^5*Cos[e+f*x]*Sin[e+f*x]/f+15/64*a^3*c^5*Cos[e+f*x]^3*Sin[e+f*x]/f+3/16*a^3*c^5*Cos[e+f*x]^5*Sin[e+f*x]/f+1/8*a^3*Cos[e+f*x]^7*(c^5-c^5*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:349
  public void test0081() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^4/(a+a*Sin[e+f*x])^3, x]", //
        "-7*c^4*x/a^3-7*c^4*Cos[e+f*x]/(a^3*f)-2/5*a^3*c^4*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^6)+14/15*a*c^4*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^4)-14/3*c^4*Cos[e+f*x]^3/(a*f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:369
  public void test0082() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(5/2))-1/8*a*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(3/2))-1/8*a*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(5/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:385
  public void test0083() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(7/2))+5/3*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))-10*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(c^(3/2)*f)+10*a^3*Cos[e+f*x]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:403
  public void test0084() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^2, x]", //
        "-2/3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:423
  public void test0085() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-a*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:455
  public void test0086() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(5/2))-3/2*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(3/2))-6*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-3*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:473
  public void test0087() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(1/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-c*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:530
  public void test0088() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c+d*Sin[e+f*x]), x]", //
        "-a^2*(c-2*d)*x/d^2-a^2*Cos[e+f*x]/(d*f)+2*a^2*(c-d)^2*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(d^2*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:552
  public void test0089() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c+d*Sin[e+f*x])^2), x]", //
        "-2*d*(2*c+d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(a*(c-d)*(c^2-d^2)^(3/2)*f)-d*(c+2*d)*Cos[e+f*x]/(a*(c-d)^2*(c+d)*f*(c+d*Sin[e+f*x]))-Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])*(c+d*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:568
  public void test0090() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^3, x]", //
        "-1/5*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^3)-1/15*(2*c+3*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^2)-1/15*(2*c+3*d)*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:626
  public void test0091() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^2, x]", //
        "-2/5*d^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(a*f)-2/15*a*(15*c^2+10*c*d+7*d^2)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-4/15*(5*c-d)*d*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:642
  public void test0092() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2), x]", //
        "-2/5*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-64/15*a^3*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-16/15*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:680
  public void test0093() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(5/2), x]", //
        "5/64*a^(3/2)*(c-15*d)*(c+d)^3*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(3/2)*f)+5/96*a^2*(c-15*d)*(c+d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])+1/24*a^2*(c-15*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(5/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])-1/4*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^(7/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])+5/64*a^2*(c-15*d)*(c+d)^2*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:814
  public void test0094() {
    check( //
        "Integrate[(a*B/b+B*Sin[x])/(a+b*Sin[x]), x]", //
        "B*x/b");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:832
  public void test0095() {
    check( //
        "Integrate[1/((a+b*Sin[e+f*x])^2*(c+d*Sin[e+f*x])), x]", //
        "2*b*(a*b*c-2*a^2*d+b^2*d)*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*(b*c-a*d)^2*f)+b^2*Cos[e+f*x]/((a^2-b^2)*(b*c-a*d)*f*(a+b*Sin[e+f*x]))+2*d^2*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((b*c-a*d)^2*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:31
  public void test0096() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(5/2))-a*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))-a^2*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:47
  public void test0097() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "3/28*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)*(c-c*Sin[e+f*x])^(3/2)/(a*f)+1/8*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)*(c-c*Sin[e+f*x])^(5/2)/(a*f)+1/35*c^3*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])+1/14*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:65
  public void test0098() {
    check( //
        "Integrate[Cos[e+f*x]^2/((c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "-Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:81
  public void test0099() {
    check( //
        "Integrate[Cos[e+f*x]^2/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-1/2*Cos[e+f*x]/(a*c*f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+1/2*Cos[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:125
  public void test0100() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x])^(5/2), x]", //
        "4/5*a*(g*Cos[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-6/5*a*(g*Cos[e+f*x])^(5/2)/(c*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+6/5*a*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:141
  public void test0101() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "4*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*(c-c*Sin[e+f*x])^(3/2))+154/15*a^3*(g*Cos[e+f*x])^(5/2)/(c*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-154/5*a^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/5*a^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:157
  public void test0102() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(15/2), x]", //
        "4/25*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(15/2))-4/35*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(13/2))-44/1105*a^4*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]])+22/3315*a^4*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+22/5525*a^4*(g*Cos[e+f*x])^(5/2)/(c^5*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+22/5525*a^4*(g*Cos[e+f*x])^(5/2)/(c^6*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+44/595*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*(c-c*Sin[e+f*x])^(11/2))-22/5525*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^7*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:175
  public void test0103() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "76/5*c^2*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/(a*f*g*(a+a*Sin[e+f*x])^(3/2))-4/5*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/(f*g*(a+a*Sin[e+f*x])^(5/2))+114/7*c^3*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*g*Sqrt[a+a*Sin[e+f*x]])+418/5*c^5*(g*Cos[e+f*x])^(5/2)/(a^2*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+1254/5*c^5*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+1254/35*c^4*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:226
  public void test0104() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(-1-m-n)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(-2+n), x]", //
        "(g*Cos[e+f*x])^(-m-n)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(-2+n)/(f*g*(4+m-n))+2*(g*Cos[e+f*x])^(-m-n)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(-1+n)/(c*f*g*(2+m-n)*(4+m-n))+2*(g*Cos[e+f*x])^(-m-n)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^n/(c^2*f*g*(m-n)*(2+m-n)*(4+m-n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:289
  public void test0105() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "Log[Sin[c+d*x]]/(a^2*d)-Log[1+Sin[c+d*x]]/(a^2*d)+1/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:305
  public void test0106() {
    check( //
        "Integrate[Cos[c+d*x]*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^4, x]", //
        "1/3*Sin[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^3)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:339
  public void test0107() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "1/2*a*ArcTanh[Cos[c+d*x]]/d-1/3*a*Cot[c+d*x]^3/d-1/2*a*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:357
  public void test0108() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "-3*a^3*x+1/2*a^3*ArcTanh[Cos[c+d*x]]/d+a^3*Cos[c+d*x]/d-3*a^3*Cot[c+d*x]/d-1/3*a^3*Cot[c+d*x]^3/d-3/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:375
  public void test0109() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "3/8*ArcTanh[Cos[c+d*x]]/(a*d)-Cot[c+d*x]/(a*d)-2/3*Cot[c+d*x]^3/(a*d)-1/5*Cot[c+d*x]^5/(a*d)+3/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:395
  public void test0110() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-76/1155*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-76/495*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-38/693*a*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+2/99*a*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+152/3465*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d+2/11*Cos[c+d*x]*Sin[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:413
  public void test0111() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "2/15*a*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-2/5*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:477
  public void test0112() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "2*a^2*x+9/8*a^2*ArcTanh[Cos[c+d*x]]/d-a^2*Cos[c+d*x]/d+2*a^2*Cot[c+d*x]/d-2/3*a^2*Cot[c+d*x]^3/d+1/8*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:493
  public void test0113() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*x-19/16*a^3*ArcTanh[Cos[c+d*x]]/d+a^3*Cot[c+d*x]/d-1/3*a^3*Cot[c+d*x]^3/d-3/5*a^3*Cot[c+d*x]^5/d+17/16*a^3*Cot[c+d*x]*Csc[c+d*x]/d-3/4*a^3*Cot[c+d*x]^3*Csc[c+d*x]/d+1/8*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^3*Cot[c+d*x]^3*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:511
  public void test0114() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "-1/8*ArcTanh[Cos[c+d*x]]/(a*d)-1/3*Cot[c+d*x]^3/(a*d)-1/5*Cot[c+d*x]^5/(a*d)-1/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:527
  public void test0115() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "9/2*x/a^3+9/2*Cos[c+d*x]/(a^3*d)+Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^3)+3/2*Cos[c+d*x]^3/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:548
  public void test0116() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-55/512*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-55/512*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-55/768*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+329/960*a*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+47/160*a*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-1/60*a*Cot[c+d*x]*Csc[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])-1/6*Cot[c+d*x]*Csc[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:566
  public void test0117() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+4/3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/3*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:582
  public void test0118() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "4/7*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))+2/3*Cos[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^(3/2))-2/7*Cos[c+d*x]^5/(a*d*(a+a*Sin[c+d*x])^(3/2))-4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)+4*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:610
  public void test0119() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "2*a*Csc[c+d*x]/d+a*Csc[c+d*x]^2/d-1/3*a*Csc[c+d*x]^3/d-1/4*a*Csc[c+d*x]^4/d+a*Log[Sin[c+d*x]]/d+a*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:644
  public void test0120() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/4*Cos[c+d*x]^4/(a*d)-1/3*Sin[c+d*x]^3/(a*d)+1/5*Sin[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:660
  public void test0121() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*Csc[c+d*x]^2/(a^2*d)+2/3*Csc[c+d*x]^3/(a^2*d)-1/4*Csc[c+d*x]^4/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:680
  public void test0122() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^n*(a+a*Sin[c+d*x]), x]", //
        "a*Sin[c+d*x]^(1+n)/(d*(1+n))+a*Sin[c+d*x]^(2+n)/(d*(2+n))-2*a*Sin[c+d*x]^(3+n)/(d*(3+n))-2*a*Sin[c+d*x]^(4+n)/(d*(4+n))+a*Sin[c+d*x]^(5+n)/(d*(5+n))+a*Sin[c+d*x]^(6+n)/(d*(6+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:704
  public void test0123() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^10*(a+a*Sin[c+d*x]), x]", //
        "5/128*a*ArcTanh[Cos[c+d*x]]/d-1/7*a*Cot[c+d*x]^7/d-1/9*a*Cot[c+d*x]^9/d+5/128*a*Cot[c+d*x]*Csc[c+d*x]/d-5/64*a*Cot[c+d*x]*Csc[c+d*x]^3/d+5/48*a*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/8*a*Cot[c+d*x]^5*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:720
  public void test0124() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^10*(a+a*Sin[c+d*x])^2, x]", //
        "5/64*a^2*ArcTanh[Cos[c+d*x]]/d-2/7*a^2*Cot[c+d*x]^7/d-1/9*a^2*Cot[c+d*x]^9/d+5/64*a^2*Cot[c+d*x]*Csc[c+d*x]/d-5/32*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+5/24*a^2*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/4*a^2*Cot[c+d*x]^5*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:736
  public void test0125() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^9*(a+a*Sin[c+d*x])^3, x]", //
        "-a^3*x+125/128*a^3*ArcTanh[Cos[c+d*x]]/d-a^3*Cot[c+d*x]/d+1/3*a^3*Cot[c+d*x]^3/d-1/5*a^3*Cot[c+d*x]^5/d-3/7*a^3*Cot[c+d*x]^7/d-115/128*a^3*Cot[c+d*x]*Csc[c+d*x]/d+5/8*a^3*Cot[c+d*x]^3*Csc[c+d*x]/d-1/2*a^3*Cot[c+d*x]^5*Csc[c+d*x]/d-5/64*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d+5/48*a^3*Cot[c+d*x]^3*Csc[c+d*x]^3/d-1/8*a^3*Cot[c+d*x]^5*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:754
  public void test0126() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "3/8*ArcTanh[Cos[c+d*x]]/(a*d)-1/5*Cot[c+d*x]^5/(a*d)-3/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/4*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:771
  public void test0127() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "5/2*ArcTanh[Cos[c+d*x]]/(a^3*d)-4*Cot[c+d*x]/(a^3*d)-1/3*Cot[c+d*x]^3/(a^3*d)+3/2*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:840
  public void test0128() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^n/(a+a*Sin[c+d*x])^2, x]", //
        "Sin[c+d*x]^(1+n)/(a^2*d*(1+n))-2*Sin[c+d*x]^(2+n)/(a^2*d*(2+n))+2*Sin[c+d*x]^(4+n)/(a^2*d*(4+n))-Sin[c+d*x]^(5+n)/(a^2*d*(5+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:866
  public void test0129() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^10/(a+a*Sin[c+d*x]), x]", //
        "-5/128*ArcTanh[Cos[c+d*x]]/(a*d)-1/7*Cot[c+d*x]^7/(a*d)-1/9*Cot[c+d*x]^9/(a*d)-5/128*Cot[c+d*x]*Csc[c+d*x]/(a*d)+5/64*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)-5/48*Cot[c+d*x]^3*Csc[c+d*x]^3/(a*d)+1/8*Cot[c+d*x]^5*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:882
  public void test0130() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^9/(a+a*Sin[c+d*x])^2, x]", //
        "-11/128*ArcTanh[Cos[c+d*x]]/(a^2*d)+2/5*Cot[c+d*x]^5/(a^2*d)+2/7*Cot[c+d*x]^7/(a^2*d)-11/128*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+7/64*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/6*Cot[c+d*x]^3*Csc[c+d*x]^3/(a^2*d)+1/16*Cot[c+d*x]*Csc[c+d*x]^5/(a^2*d)-1/8*Cot[c+d*x]^3*Csc[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:924
  public void test0131() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "-9/2*a^3*x+6*a^3*Cos[c+d*x]/d+3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d+Sec[c+d*x]*(a+a*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:942
  public void test0132() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "1/5*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^2)-2/15*Sec[c+d*x]/(d*(a^2+a^2*Sin[c+d*x]))+4/15*Tan[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1004
  public void test0133() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "-a*Sec[c+d*x]/d+1/3*a*Sec[c+d*x]^3/d+1/3*a*Tan[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1039
  public void test0134() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "-2/3*Sec[c+d*x]^3/(a^2*d)+4/5*Sec[c+d*x]^5/(a^2*d)-2/7*Sec[c+d*x]^7/(a^2*d)+1/5*Tan[c+d*x]^5/(a^2*d)+2/7*Tan[c+d*x]^7/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1055
  public void test0135() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^4, x]", //
        "4/5*Sec[c+d*x]^5/(a^4*d)-16/7*Sec[c+d*x]^7/(a^4*d)+20/9*Sec[c+d*x]^9/(a^4*d)-8/11*Sec[c+d*x]^11/(a^4*d)+1/5*Tan[c+d*x]^5/(a^4*d)+9/7*Tan[c+d*x]^7/(a^4*d)+16/9*Tan[c+d*x]^9/(a^4*d)+8/11*Tan[c+d*x]^11/(a^4*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1170
  public void test0136() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^2*(c+d*Sin[e+f*x])^n, x]", //
        "a^2*(c-d)^2*(c+d*Sin[e+f*x])^(1+n)/(d^3*f*(1+n))-2*a^2*(c-d)*(c+d*Sin[e+f*x])^(2+n)/(d^3*f*(2+n))+a^2*(c+d*Sin[e+f*x])^(3+n)/(d^3*f*(3+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1232
  public void test0137() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "-a*(A+B)*Log[1-Sin[c+d*x]]/d-a*B*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1248
  public void test0138() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "-2*a^2*(A+B)*Log[1-Sin[c+d*x]]/d-a^2*(A+B)*Sin[c+d*x]/d-1/2*B*(a+a*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1264
  public void test0139() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "1/4*(A-B)*(a+a*Sin[c+d*x])^4/(a*d)+1/5*B*(a+a*Sin[c+d*x])^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1282
  public void test0140() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Sin[c+d*x])/(a+a*Sin[c+d*x]), x]", //
        "A*Sin[c+d*x]/(a*d)-1/2*(A-B)*Sin[c+d*x]^2/(a*d)-1/3*B*Sin[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1307
  public void test0141() {
    check( //
        "Integrate[Cos[e+f*x]^3*(a+a*Sin[e+f*x])^m*(A+B*Sin[e+f*x]), x]", //
        "2*(A-B)*(a+a*Sin[e+f*x])^(2+m)/(a^2*f*(2+m))-(A-3*B)*(a+a*Sin[e+f*x])^(3+m)/(a^3*f*(3+m))-B*(a+a*Sin[e+f*x])^(4+m)/(a^4*f*(4+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1360
  public void test0142() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "1/8*a*b*x-1/35*(7*a^2+4*b^2)*Cos[c+d*x]/d+1/105*(7*a^2+4*b^2)*Cos[c+d*x]^3/d-1/8*a*b*Cos[c+d*x]*Sin[c+d*x]/d-1/12*a*b*Cos[c+d*x]*Sin[c+d*x]^3/d+1/35*(2*a^2-b^2)*Cos[c+d*x]*Sin[c+d*x]^4/d+1/21*a*b*Cos[c+d*x]*Sin[c+d*x]^5/d+1/7*Cos[c+d*x]*Sin[c+d*x]^4*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1376
  public void test0143() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^3, x]", //
        "-b^3*x+1/8*a*(a^2+12*b^2)*ArcTanh[Cos[c+d*x]]/d+1/2*b*(2*a^2-b^2)*Cot[c+d*x]/d+1/8*a*(a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]/d-1/4*b*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^2/d-1/4*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1422
  public void test0144() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+b*Sin[c+d*x])^2, x]", //
        "1/8*a*b*x-1/105*(a^2+6*b^2)*Cos[c+d*x]^5/d+1/8*a*b*Cos[c+d*x]*Sin[c+d*x]/d+1/12*a*b*Cos[c+d*x]^3*Sin[c+d*x]/d-1/21*a*Cos[c+d*x]^5*(a+b*Sin[c+d*x])/d-1/7*Cos[c+d*x]^5*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1439
  public void test0145() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+b*Sin[c+d*x])^3, x]", //
        "b^3*x-1/16*a*(a^2+18*b^2)*ArcTanh[Cos[c+d*x]]/d-1/60*b*(36*a^4-43*a^2*b^2+2*b^4)*Cot[c+d*x]/(a^2*d)-1/240*(15*a^4-84*a^2*b^2+4*b^4)*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/120*b*(39*a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^2/(a^2*d)+1/120*(35*a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3/(a^2*d)+1/15*b*Cot[c+d*x]*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^4/(a^2*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1909
  public void test0146() {
    check( //
        "Integrate[Csc[c+d*x]*Sec[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "3/8*b*ArcTanh[Sin[c+d*x]]/d+a*Log[Tan[c+d*x]]/d+3/8*b*Sec[c+d*x]*Tan[c+d*x]/d+1/4*b*Sec[c+d*x]^3*Tan[c+d*x]/d+a*Tan[c+d*x]^2/d+1/4*a*Tan[c+d*x]^4/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1989
  public void test0147() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "-1/3*(a^2-b^2)*(A*b-a*B)*(a+b*Sin[c+d*x])^3/(b^4*d)+1/4*(2*a*A*b-3*a^2*B+b^2*B)*(a+b*Sin[c+d*x])^4/(b^4*d)-1/5*(A*b-3*a*B)*(a+b*Sin[c+d*x])^5/(b^4*d)-1/6*B*(a+b*Sin[c+d*x])^6/(b^4*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:20
  public void test0148() {
    check( //
        "Integrate[Csc[e+f*x]^7*(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "1/16*a^2*c*ArcTanh[Cos[e+f*x]]/f-1/3*a^2*c*Cot[e+f*x]^3/f-1/5*a^2*c*Cot[e+f*x]^5/f+1/16*a^2*c*Cot[e+f*x]*Csc[e+f*x]/f+1/24*a^2*c*Cot[e+f*x]*Csc[e+f*x]^3/f-1/6*a^2*c*Cot[e+f*x]*Csc[e+f*x]^5/f");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:77
  public void test0149() {
    check( //
        "Integrate[1/((c+d*Sin[e+f*x])*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "-ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[g]/(Sqrt[2]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/((c-d)*f*Sqrt[a]*Sqrt[g])+2*d*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[c]*Sqrt[g]/(Sqrt[c+d]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]/((c-d)*f*Sqrt[a]*Sqrt[c]*Sqrt[c+d]*Sqrt[g])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:45
  public void test0150() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^2, x]", //
        "a*B*x/c^2-1/3*a*(A+7*B)*Cos[e+f*x]/(c^2*f*(1-Sin[e+f*x]))+2/3*a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:61
  public void test0151() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^6, x]", //
        "11/256*a^3*(10*A-3*B)*c^6*x+11/560*a^3*(10*A-3*B)*c^6*Cos[e+f*x]^7/f+11/256*a^3*(10*A-3*B)*c^6*Cos[e+f*x]*Sin[e+f*x]/f+11/384*a^3*(10*A-3*B)*c^6*Cos[e+f*x]^3*Sin[e+f*x]/f+11/480*a^3*(10*A-3*B)*c^6*Cos[e+f*x]^5*Sin[e+f*x]/f-1/10*a^3*B*Cos[e+f*x]^7*(c^2-c^2*Sin[e+f*x])^3/f+1/90*a^3*(10*A-3*B)*Cos[e+f*x]^7*(c^3-c^3*Sin[e+f*x])^2/f+11/720*a^3*(10*A-3*B)*Cos[e+f*x]^7*(c^6-c^6*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:79
  public void test0152() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^2/(a+a*Sin[e+f*x]), x]", //
        "-3/2*(2*A-3*B)*c^2*x/a-3/2*(2*A-3*B)*c^2*Cos[e+f*x]/(a*f)-a^2*(A-B)*c^2*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^3)-1/2*(2*A-3*B)*c^2*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:95
  public void test0153() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^5/(a+a*Sin[e+f*x])^3, x]", //
        "-21/2*(3*A-8*B)*c^5*x/a^3-7*(3*A-8*B)*c^5*Cos[e+f*x]^3/(a^3*f)-21/2*(3*A-8*B)*c^5*Cos[e+f*x]*Sin[e+f*x]/(a^3*f)-1/5*a^5*(A-B)*c^5*Cos[e+f*x]^11/(f*(a+a*Sin[e+f*x])^8)+2/15*a^3*(3*A-8*B)*c^5*Cos[e+f*x]^9/(f*(a+a*Sin[e+f*x])^6)-6/5*a^5*(3*A-8*B)*c^5*Cos[e+f*x]^7/(f*(a^2+a^2*Sin[e+f*x])^4)-42/5*a^5*(3*A-8*B)*c^5*Cos[e+f*x]^5/(f*(a^4+a^4*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:115
  public void test0154() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(3/2))-a*(A+5*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(3/2)*f*Sqrt[2])+2*a*B*Cos[e+f*x]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:131
  public void test0155() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-2/7*a^3*B*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))-2/5*a^3*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))-4/3*a^3*(A+B)*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+8*a^3*(A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[c])-8*a^3*(A+B)*Cos[e+f*x]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:149
  public void test0156() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^2, x]", //
        "-1/3*(A-7*B)*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-1/3*(A-B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(7/2)/(a^2*c^2*f)+4/3*(A-7*B)*c*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:169
  public void test0157() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "1/2*a*B*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])-a*(A+B)*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:185
  public void test0158() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "-1/5*a*A*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)/f-1/6*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/f-2/15*a^3*A*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/5*a^2*A*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:201
  public void test0159() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(3/2), x]", //
        "1/2*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(3/2))+1/2*a^2*(3*A+5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])+1/6*a*(3*A+5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])+4*a^4*(3*A+5*B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*a^3*(3*A+5*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:219
  public void test0160() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*(a+a*Sin[e+f*x])^(3/2))-(A-3*B)*c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-1/2*(A-3*B)*c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:279
  public void test0161() {
    check( //
        "Integrate[Csc[c+d*x]^5*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "5/8*a^3*A*ArcTanh[Cos[c+d*x]]/d-2/3*a^3*A*Cot[c+d*x]^3/d-3/8*a^3*A*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^3*A*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:336
  public void test0162() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^3, x]", //
        "-1/5*(A-B)*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^3)-1/15*(2*A*c+3*B*c+3*A*d-8*B*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^2)-1/15*(2*A*c+3*B*c+3*A*d+7*B*d)*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:356
  public void test0163() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c+d*Sin[e+f*x]), x]", //
        "-2*a^(3/2)*(c-d)*(B*c-A*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(5/2)*f*Sqrt[c+d])+2/3*a^2*(3*B*c-3*A*d-4*B*d)*Cos[e+f*x]/(d^2*f*Sqrt[a+a*Sin[e+f*x]])-2/3*a*B*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*f)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:15
  public void test0164() {
    check( //
        "Integrate[1/(a*Sin[x]^2)^(5/2), x]", //
        "-1/4*Cot[x]/(a*(a*Sin[x]^2)^(3/2))-3/8*Cot[x]/(a^2*Sqrt[a*Sin[x]^2])-3/8*ArcTanh[Cos[x]]*Sin[x]/(a^2*Sqrt[a*Sin[x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:60
  public void test0165() {
    check( //
        "Integrate[(a-a*Sin[x]^2)^3, x]", //
        "5/16*a^3*x+5/16*a^3*Cos[x]*Sin[x]+5/24*a^3*Cos[x]^3*Sin[x]+1/6*a^3*Cos[x]^5*Sin[x]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:268
  public void test0166() {
    check( //
        "Integrate[Sin[c+d*x]^4/(a-b*Sin[c+d*x]^4), x]", //
        "-x/b+1/2*a^(1/4)*ArcTan[Sqrt[Sqrt[a]-Sqrt[b]]*Tan[c+d*x]/a^(1/4)]/(b*d*Sqrt[Sqrt[a]-Sqrt[b]])+1/2*a^(1/4)*ArcTan[Sqrt[Sqrt[a]+Sqrt[b]]*Tan[c+d*x]/a^(1/4)]/(b*d*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:330
  public void test0167() {
    check( //
        "Integrate[1/(1-Sin[x]^6), x]", //
        "1/3*ArcTan[Sqrt[1+(-1)^(1/3)]*Tan[x]]/Sqrt[1+(-1)^(1/3)]+1/3*ArcTan[Sqrt[1-(-1)^(2/3)]*Tan[x]]/Sqrt[1-(-1)^(2/3)]+1/3*Tan[x]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:376
  public void test0168() {
    check( //
        "Integrate[Sec[e+f*x]^6*(a+b*Sin[e+f*x]^2), x]", //
        "a*Tan[e+f*x]/f+1/3*(2*a+b)*Tan[e+f*x]^3/f+1/5*(a+b)*Tan[e+f*x]^5/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:410
  public void test0169() {
    check( //
        "Integrate[Sec[x]^4/(a+b*Sin[x]^2)^2, x]", //
        "1/2*b^2*(6*a+b)*ArcTan[Sqrt[a+b]*Tan[x]/Sqrt[a]]/(a^(3/2)*(a+b)^(7/2))+(a+3*b)*Tan[x]/(a+b)^3+1/3*Tan[x]^3/(a+b)^2+1/2*b^3*Tan[x]/(a*(a+b)^3*(a+(a+b)*Tan[x]^2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:499
  public void test0170() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Sin[c+d*x]^3)^2, x]", //
        "-2/9*(a^(4/3)-b^(4/3))*Log[a^(1/3)+b^(1/3)*Sin[c+d*x]]/(a^(5/3)*b^(5/3)*d)+1/9*(a^(4/3)-b^(4/3))*Log[a^(2/3)-a^(1/3)*b^(1/3)*Sin[c+d*x]+b^(2/3)*Sin[c+d*x]^2]/(a^(5/3)*b^(5/3)*d)+1/3*Sin[c+d*x]*(b-a*Sin[c+d*x]-2*b*Sin[c+d*x]^2)/(a*b*d*(a+b*Sin[c+d*x]^3))-2/3*(a^(4/3)+b^(4/3))*ArcTan[(a^(1/3)-2*b^(1/3)*Sin[c+d*x])/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(5/3)*d*Sqrt[3])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:528
  public void test0171() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a-b*Sin[c+d*x]^4), x]", //
        "-1/2*ArcTan[Sqrt[Sqrt[a]-Sqrt[b]]*Tan[c+d*x]/a^(1/4)]*Sqrt[Sqrt[a]-Sqrt[b]]/(a^(3/4)*d*Sqrt[b])+1/2*ArcTan[Sqrt[Sqrt[a]+Sqrt[b]]*Tan[c+d*x]/a^(1/4)]*Sqrt[Sqrt[a]+Sqrt[b]]/(a^(3/4)*d*Sqrt[b])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:580
  public void test0172() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+b*Sin[c+d*x]^2), x]", //
        "-Cot[c+d*x]/(a*d)-ArcTan[Sqrt[a+b]*Tan[c+d*x]/Sqrt[a]]*Sqrt[a+b]/(a^(3/2)*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:602
  public void test0173() {
    check( //
        "Integrate[Tan[e+f*x]^3/Sqrt[a-a*Sin[e+f*x]^2], x]", //
        "1/3*a/(f*(a*Cos[e+f*x]^2)^(3/2))+(-1)/(f*Sqrt[a*Cos[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:618
  public void test0174() {
    check( //
        "Integrate[Cot[e+f*x]^4/(a-a*Sin[e+f*x]^2)^(3/2), x]", //
        "-1/3*Cot[e+f*x]*Csc[e+f*x]^2/(a*f*Sqrt[a*Cos[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:723
  public void test0175() {
    check( //
        "Integrate[Cot[c+d*x]/Sqrt[a+b*Sin[c+d*x]^4], x]", //
        "-1/2*ArcTanh[Sqrt[a+b*Sin[c+d*x]^4]/Sqrt[a]]/(d*Sqrt[a])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:60
  public void test0176() {
    check( //
        "Integrate[(a*Cos[x]^2)^(1/2), x]", //
        "Sqrt[a*Cos[x]^2]*Tan[x]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:85
  public void test0177() {
    check( //
        "Integrate[(c*Cos[a+b*x]^m)^(1/m), x]", //
        "(c*Cos[a+b*x]^m)^(1/m)*Tan[a+b*x]/b");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:114
  public void test0178() {
    check( //
        "Integrate[Cos[c+d*x]*(b*Cos[c+d*x])^(3/2), x]", //
        "2/5*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+6/5*b*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:130
  public void test0179() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*Sec[c+d*x]^4, x]", //
        "2*b^3*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2*b^2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:148
  public void test0180() {
    check( //
        "Integrate[Sec[c+d*x]^4/Sqrt[b*Cos[c+d*x]], x]", //
        "2/7*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+10/21*b*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+10/21*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:164
  public void test0181() {
    check( //
        "Integrate[Cos[c+d*x]^6/(b*Cos[c+d*x])^(5/2), x]", //
        "2/7*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^5*d)+10/21*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+10/21*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:184
  public void test0182() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(3/2), x]", //
        "ArcTanh[Sin[c+d*x]]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:200
  public void test0183() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(b*Cos[c+d*x])^(5/2), x]", //
        "1/4*b^2*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*b^2*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+3/8*b^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:218
  public void test0184() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(1/2)*(b*Cos[c+d*x])^(1/2)), x]", //
        "ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:234
  public void test0185() {
    check( //
        "Integrate[Cos[c+d*x]^(11/2)/(b*Cos[c+d*x])^(5/2), x]", //
        "Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])-1/3*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.1.1 (a+b cos)^n.input:17
  public void test0186() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])");
  }

  // 4.2.1.2 (g sin)^p (a+b cos)^m.input:18
  public void test0187() {
    check( //
        "Integrate[Csc[x]^4/(a+a*Cos[x]), x]", //
        "-4/5*Cot[x]/a-4/15*Cot[x]^3/a+1/5*Csc[x]^3/(a+a*Cos[x])");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:11
  public void test0188() {
    check( //
        "Integrate[Tan[x]^3/(a+a*Cos[x]), x]", //
        "-Sec[x]/a+1/2*Sec[x]^2/a");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:43
  public void test0189() {
    check( //
        "Integrate[(c+d*x)^2*Sec[a+b*x], x]", //
        "-2*I*(c+d*x)^2*ArcTan[E^(I*(a+b*x))]/b+2*I*d*(c+d*x)*PolyLog[2,-I*E^(I*(a+b*x))]/b^2-2*I*d*(c+d*x)*PolyLog[2,I*E^(I*(a+b*x))]/b^2-2*d^2*PolyLog[3,-I*E^(I*(a+b*x))]/b^3+2*d^2*PolyLog[3,I*E^(I*(a+b*x))]/b^3");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:140
  public void test0190() {
    check( //
        "Integrate[x^(-2+m)*Cos[a+b*x], x]", //
        "1/2*I*E^(I*a)*b*x^m*Gamma[-1+m,-I*b*x]/(-I*b*x)^m-1/2*I*b*x^m*Gamma[-1+m,I*b*x]/(E^(I*a)*(I*b*x)^m)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:192
  public void test0191() {
    check( //
        "Integrate[Sqrt[a+a*Cos[c+d*x]]/x, x]", //
        "CosIntegral[1/2*d*x]*Cos[1/2*c]*Sec[1/2*c+1/2*d*x]*Sqrt[a+a*Cos[c+d*x]]-Sec[1/2*c+1/2*d*x]*SinIntegral[1/2*d*x]*Sin[1/2*c]*Sqrt[a+a*Cos[c+d*x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:208
  public void test0192() {
    check( //
        "Integrate[Sqrt[a-a*Cos[x]]/x^3, x]", //
        "-1/2*Sqrt[a-a*Cos[x]]/x^2-1/4*Cot[1/2*x]*Sqrt[a-a*Cos[x]]/x-1/8*Csc[1/2*x]*SinIntegral[1/2*x]*Sqrt[a-a*Cos[x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:228
  public void test0193() {
    check( //
        "Integrate[x^2/(a+a*Cos[x])^(3/2), x]", //
        "-2*x/(a*Sqrt[a+a*Cos[x]])-I*x^2*ArcTan[E^(1/2*I*x)]*Cos[1/2*x]/(a*Sqrt[a+a*Cos[x]])+4*ArcTanh[Sin[1/2*x]]*Cos[1/2*x]/(a*Sqrt[a+a*Cos[x]])+2*I*x*Cos[1/2*x]*PolyLog[2,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-2*I*x*Cos[1/2*x]*PolyLog[2,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])-4*Cos[1/2*x]*PolyLog[3,-I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+4*Cos[1/2*x]*PolyLog[3,I*E^(1/2*I*x)]/(a*Sqrt[a+a*Cos[x]])+1/2*x^2*Tan[1/2*x]/(a*Sqrt[a+a*Cos[x]])");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:20
  public void test0194() {
    check( //
        "Integrate[Cos[a+b*x^2]^2, x]", //
        "1/2*x+1/4*Cos[2*a]*FresnelC[2*x*Sqrt[b]/Sqrt[Pi]]*Sqrt[Pi]/Sqrt[b]-1/4*FresnelS[2*x*Sqrt[b]/Sqrt[Pi]]*Sin[2*a]*Sqrt[Pi]/Sqrt[b]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:40
  public void test0195() {
    check( //
        "Integrate[Cos[a+b*x^2]/x^(3/2), x]", //
        "-I*E^(I*a)*b*x^(3/2)*Gamma[3/4,-I*b*x^2]/(-I*b*x^2)^(3/4)+I*b*x^(3/2)*Gamma[3/4,I*b*x^2]/(E^(I*a)*(I*b*x^2)^(3/4))-2*Cos[a+b*x^2]/Sqrt[x]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:110
  public void test0196() {
    check( //
        "Integrate[x^(-1-n)*Cos[a+b*x^n]^3, x]", //
        "-3/4*Cos[a+b*x^n]/(n*x^n)-1/4*Cos[3*(a+b*x^n)]/(n*x^n)-3/4*b*Cos[a]*SinIntegral[b*x^n]/n-3/4*b*Cos[3*a]*SinIntegral[3*b*x^n]/n-3/4*b*CosIntegral[b*x^n]*Sin[a]/n-3/4*b*CosIntegral[3*b*x^n]*Sin[3*a]/n");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:19
  public void test0197() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "a*ArcTanh[Sin[c+d*x]]/d+a*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:37
  public void test0198() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3, x]", //
        "5/2*a^3*x+4*a^3*Sin[c+d*x]/d+3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d-1/3*a^3*Sin[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:56
  public void test0199() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Cos[c+d*x]), x]", //
        "15/8*x/a-4*Sin[c+d*x]/(a*d)+15/8*Cos[c+d*x]*Sin[c+d*x]/(a*d)+5/4*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)-Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+4/3*Sin[c+d*x]^3/(a*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:72
  public void test0200() {
    check( //
        "Integrate[Sec[c+d*x]/(a+a*Cos[c+d*x])^2, x]", //
        "ArcTanh[Sin[c+d*x]]/(a^2*d)-4/3*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:88
  public void test0201() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Cos[c+d*x])^4, x]", //
        "-18/35*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)+12/35*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)+8/35*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:104
  public void test0202() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Cos[c+d*x])^5, x]", //
        "-5*ArcTanh[Sin[c+d*x]]/(a^5*d)+496/63*Tan[c+d*x]/(a^5*d)-1/9*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^5)-5/21*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^4)-29/63*Tan[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^3)-67/63*Tan[c+d*x]/(a^3*d*(a+a*Cos[c+d*x])^2)-5*Tan[c+d*x]/(d*(a^5+a^5*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:142
  public void test0203() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+28/15*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/5*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-2/15*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:158
  public void test0204() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-1/4*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-17/16*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+163/16*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-197/24*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+95/48*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:178
  public void test0205() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^2, x]", //
        "16/5*a^2*EllipticE[1/2*(c+d*x),2]/d+4/3*a^2*EllipticF[1/2*(c+d*x),2]/d+2/5*a^2*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+4/3*a^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:194
  public void test0206() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4/Cos[c+d*x]^(5/2), x]", //
        "40/3*a^4*EllipticF[1/2*(c+d*x),2]/d+2/3*a^4*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+8*a^4*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])+2/3*a^4*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:212
  public void test0207() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2), x]", //
        "-4*EllipticE[1/2*(c+d*x),2]/(a^2*d)-5/3*EllipticF[1/2*(c+d*x),2]/(a^2*d)+4*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]])-5/3*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x])*Sqrt[Cos[c+d*x]])-1/3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:232
  public void test0208() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(5/2), x]", //
        "2/3*a*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+4/3*a*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:248
  public void test0209() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)/Cos[c+d*x]^(9/2), x]", //
        "6/7*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+46/21*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+92/21*a^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/7*a^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(7/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:266
  public void test0210() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(3/2)*(1+Cos[c+d*x])^(1/2)), x]", //
        "-ArcSin[Sin[c+d*x]/(1+Cos[c+d*x])]*Sqrt[2]/d+2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:282
  public void test0211() {
    check( //
        "Integrate[Cos[c+d*x]^(9/2)/(a+a*Cos[c+d*x])^(7/2), x]", //
        "-7*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(7/2)*d)-1/6*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(7/2))-7/16*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(5/2))-259/192*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))+637/64*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])+189/64*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:302
  public void test0212() {
    check( //
        "Integrate[(a-a*Cos[c+d*x])^(1/2)/Cos[c+d*x]^(5/2), x]", //
        "2/3*a*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a-a*Cos[c+d*x]])-4/3*a*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:471
  public void test0213() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Cos[c+d*x]), x]", //
        "3/8*a*x+b*Sin[c+d*x]/d+3/8*a*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d-2/3*b*Sin[c+d*x]^3/d+1/5*b*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:487
  public void test0214() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*Sec[c+d*x], x]", //
        "2*a*b*x+a^2*ArcTanh[Sin[c+d*x]]/d+b^2*Sin[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:505
  public void test0215() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Cos[c+d*x])^4, x]", //
        "1/2*a*b*(4*a^2+3*b^2)*x+2/15*(3*a^4+28*a^2*b^2+4*b^4)*Sin[c+d*x]/d+1/30*a*b*(6*a^2+29*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/15*(3*a^2+4*b^2)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/5*a*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d+1/5*(a+b*Cos[c+d*x])^4*Sin[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:540
  public void test0216() {
    check( //
        "Integrate[Cos[c+d*x]/(a+b*Cos[c+d*x])^3, x]", //
        "-3*a*b*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(5/2)*(a+b)^(5/2)*d)+1/2*a*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^2)+1/2*(a^2+2*b^2)*Sin[c+d*x]/((a^2-b^2)^2*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:589
  public void test0217() {
    check( //
        "Integrate[Cos[c+d*x]*Sqrt[3-4*Cos[c+d*x]], x]", //
        "-1/2*EllipticE[1/2*(Pi+c+d*x),8/7]*Sqrt[7]/d-1/6*EllipticF[1/2*(Pi+c+d*x),8/7]*Sqrt[7]/d+2/3*Sin[c+d*x]*Sqrt[3-4*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:616
  public void test0218() {
    check( //
        "Integrate[1/(a+b*Cos[c+d*x])^(5/2), x]", //
        "-2/3*b*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^(3/2))-8/3*a*b*Sin[c+d*x]/((a^2-b^2)^2*d*Sqrt[a+b*Cos[c+d*x]])+8/3*a*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/((a^2-b^2)^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/3*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:644
  public void test0219() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "-6/5*A*EllipticE[1/2*(c+d*x),2]/d+2/3*B*EllipticF[1/2*(c+d*x),2]/d+2/5*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/3*B*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+6/5*A*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:934
  public void test0220() {
    check( //
        "Integrate[Cos[c+d*x]^2*(b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]), x]", //
        "2/5*A*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/7*B*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^2*d)+10/21*b*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+6/5*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:950
  public void test0221() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "2/3*b^3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b^2*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2*A*b^2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:968
  public void test0222() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(3/2), x]", //
        "2*A*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])-2*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:19
  public void test0223() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "1/2*a*(A+B)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(2*A+3*B)*Tan[c+d*x]/d+1/2*a*(A+B)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:35
  public void test0224() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "a^3*(A+3*B)*x+1/2*a^3*(7*A+6*B)*ArcTanh[Sin[c+d*x]]/d-5/2*a^3*A*Sin[c+d*x]/d+(2*A+B)*(a^3+a^3*Cos[c+d*x])*Tan[c+d*x]/d+1/2*a*A*(a+a*Cos[c+d*x])^2*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:70
  public void test0225() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^3, x]", //
        "-1/2*(6*A-13*B)*x/a^3+8/15*(9*A-19*B)*Sin[c+d*x]/(a^3*d)-1/2*(6*A-13*B)*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+1/5*(A-B)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(6*A-11*B)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+4/15*(9*A-19*B)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:86
  public void test0226() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^4, x]", //
        "1/2*(21*A-8*B)*ArcTanh[Sin[c+d*x]]/(a^4*d)-8/105*(216*A-83*B)*Tan[c+d*x]/(a^4*d)+1/2*(21*A-8*B)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d)-1/105*(129*A-52*B)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-4/105*(216*A-83*B)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/5*(2*A-B)*Sec[c+d*x]*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:106
  public void test0227() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "1/8*a^(3/2)*(11*A+14*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/8*a^2*(11*A+14*B)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/12*a^2*(7*A+6*B)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*a*A*Sec[c+d*x]^2*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:124
  public void test0228() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-(A-2*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A-B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+A*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:140
  public void test0229() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-(5*A-2*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)+1/16*(115*A-43*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(15*A-7*B)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+1/16*(35*A-11*B)*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:160
  public void test0230() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]), x]", //
        "4/15*a^3*(17*A+15*B)*EllipticE[1/2*(c+d*x),2]/d+4/231*a^3*(121*A+105*B)*EllipticF[1/2*(c+d*x),2]/d+4/45*a^3*(17*A+15*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+20/693*a^3*(22*A+21*B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/11*a*B*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+2/99*(11*A+15*B)*Cos[c+d*x]^(5/2)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+4/231*a^3*(121*A+105*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:178
  public void test0231() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^2, x]", //
        "-(A-4*B)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+1/3*(2*A-5*B)*EllipticF[1/2*(c+d*x),2]/(a^2*d)+1/3*(A-B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)+1/3*(2*A-5*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:198
  public void test0232() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(1/2), x]", //
        "(2*A+B)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+a*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:214
  public void test0233() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(3/2), x]", //
        "1/4*a^(5/2)*(20*A+19*B)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2*a*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-1/4*a^3*(4*A-9*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])-1/2*a^2*(4*A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:232
  public void test0234() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "-1/2*(A-B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2))+1/2*(11*A-7*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/6*(7*A-3*B)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-1/6*(19*A-15*B)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:270
  public void test0235() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x]), x]", //
        "1/8*(8*a^3*A+12*a*A*b^2+12*a^2*b*B+3*b^3*B)*x+1/6*(16*a^2*A*b+4*A*b^3+3*a^3*B+12*a*b^2*B)*Sin[c+d*x]/d+1/24*b*(20*a*A*b+6*a^2*B+9*b^2*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*(4*A*b+3*a*B)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*B*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:286
  public void test0236() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^7, x]", //
        "1/16*(5*a^4*A+36*a^2*A*b^2+8*A*b^4+24*a^3*b*B+32*a*b^3*B)*ArcTanh[Sin[c+d*x]]/d+1/15*(32*a^3*A*b+40*a*A*b^3+8*a^4*B+60*a^2*b^2*B+15*b^4*B)*Tan[c+d*x]/d+1/16*(5*a^4*A+36*a^2*A*b^2+8*A*b^4+24*a^3*b*B+32*a*b^3*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/15*a*(16*a^2*A*b+13*A*b^3+4*a^3*B+27*a*b^2*B)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/120*a^2*(25*a^2*A+48*A*b^2+72*a*b*B)*Sec[c+d*x]^3*Tan[c+d*x]/d+1/10*a*(3*A*b+2*a*B)*(a+b*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d+1/6*a*A*(a+b*Cos[c+d*x])^3*Sec[c+d*x]^5*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:340
  public void test0237() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]), x]", //
        "2/35*(7*A*b-4*a*B)*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/7*B*Cos[c+d*x]*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)-2/105*(14*a*A*b-8*a^2*B-25*b^2*B)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d)-2/105*(14*a^2*A*b-63*A*b^3-8*a^3*B-19*a*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^3*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/105*(a^2-b^2)*(14*a*A*b-8*a^2*B-25*b^2*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^3*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:374
  public void test0238() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(3/2), x]", //
        "-2*(A*b-a*B)*Sin[c+d*x]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])+2*(A*b-a*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2*B*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:402
  public void test0239() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])^2*(A+B*Cos[c+d*x]), x]", //
        "2/15*(7*b^2*B+9*a*(2*A*b+a*B))*EllipticE[1/2*(c+d*x),2]/d+2/21*(7*a^2*A+5*A*b^2+10*a*b*B)*EllipticF[1/2*(c+d*x),2]/d+2/45*(7*b^2*B+9*a*(2*A*b+a*B))*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/63*b*(9*A*b+11*a*B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*b*B*Cos[c+d*x]^(5/2)*(a+b*Cos[c+d*x])*Sin[c+d*x]/d+2/21*(7*a^2*A+5*A*b^2+10*a*b*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:438
  public void test0240() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])/(Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])), x]", //
        "-2*B*EllipticE[1/2*(c+d*x),2]/d+2*B*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:20
  public void test0241() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2), x]", //
        "1/8*(4*A+3*C)*x+1/8*(4*A+3*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*C*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:40
  public void test0242() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*(b*Sec[c+d*x])^(7/2), x]", //
        "-2/5*b^4*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]/(d*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]])+2/5*b^3*(3*A+5*C)*Sin[c+d*x]*Sqrt[b*Sec[c+d*x]]/d+2/5*A*b^2*(b*Sec[c+d*x])^(3/2)*Tan[c+d*x]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:65
  public void test0243() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^5*Sqrt[b*Cos[c+d*x]], x]", //
        "2/7*A*b^4*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/21*b^2*(5*A+7*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/21*b*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:81
  public void test0244() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^7, x]", //
        "2/7*A*b^6*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/21*b^4*(5*A+7*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/21*b^3*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:99
  public void test0245() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]/(b*Cos[c+d*x])^(3/2), x]", //
        "2/3*A*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/3*(A+3*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:119
  public void test0246() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]], x]", //
        "A*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/2*C*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/2*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:135
  public void test0247() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "1/4*b^2*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+1/8*b^2*(4*A+3*C)*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/8*b^2*(4*A+3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:153
  public void test0248() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "1/8*(4*A+3*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/4*C*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/8*(4*A+3*C)*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:346
  public void test0249() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]], x]", //
        "2/45*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/7*B*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^2*d)+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b^3*d)+10/21*b*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2/15*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:362
  public void test0250() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/45*b*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*B*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b*d)+10/21*b^3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*b^2*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2/15*b^2*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:380
  public void test0251() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "2/45*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^3*d)+2/7*B*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^4*d)+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b^5*d)+10/21*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])+10/21*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^2*d)+2/15*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:396
  public void test0252() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(7/2), x]", //
        "2/5*A*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(5/2))+2/3*B*Sin[c+d*x]/(b^2*d*(b*Cos[c+d*x])^(3/2))+2/5*(3*A+5*C)*Sin[c+d*x]/(b^3*d*Sqrt[b*Cos[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^3*d*Sqrt[b*Cos[c+d*x]])-2/5*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:24
  public void test0253() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "1/2*a^2*(2*A+3*C)*x+2*a^2*A*ArcTanh[Sin[c+d*x]]/d-1/2*a^2*(2*A-3*C)*Sin[c+d*x]/d-1/2*(2*A-C)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+A*(a+a*Cos[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:40
  public void test0254() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^4*(A+C*Cos[c+d*x]^2), x]", //
        "1/4*a^4*(14*A+11*C)*x+16/35*a^4*(14*A+11*C)*Sin[c+d*x]/d+27/140*a^4*(14*A+11*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/70*a^4*(14*A+11*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/105*(21*A+4*C)*(a+a*Cos[c+d*x])^4*Sin[c+d*x]/d+1/7*C*Cos[c+d*x]^2*(a+a*Cos[c+d*x])^4*Sin[c+d*x]/d+2/21*C*(a+a*Cos[c+d*x])^5*Sin[c+d*x]/(a*d)-8/105*a^4*(14*A+11*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:58
  public void test0255() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x]), x]", //
        "1/2*(3*A+2*C)*ArcTanh[Sin[c+d*x]]/(a*d)-(2*A+C)*Tan[c+d*x]/(a*d)+1/2*(3*A+2*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d)-(A+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:74
  public void test0256() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^3, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^3*d)-1/5*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(7*A-3*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-1/15*(22*A-3*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:94
  public void test0257() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]], x]", //
        "2/5*C*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+2/15*a*(15*A+7*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-4/15*C*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:110
  public void test0258() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2), x]", //
        "2/231*a*(33*A+25*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/693*(99*A+26*C)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/11*C*Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+10/99*C*(a+a*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(a*d)+64/693*a^3*(33*A+25*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+16/693*a^2*(33*A+25*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:128
  public void test0259() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/Sqrt[a+a*Cos[c+d*x]], x]", //
        "-1/8*(9*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/8*(7*A+8*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/12*A*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*A*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:144
  public void test0260() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^(5/2), x]", //
        "1/4*(39*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/16*(219*A+43*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(19*A+3*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(63*A+11*C)*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+1/16*(31*A+7*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:164
  public void test0261() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(11/2), x]", //
        "-16/15*a^2*(2*A+3*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]/d+2/105*a^2*(19*A+21*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+4/21*a^2*(5*A+7*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/9*A*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(9/2))+8/63*A*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+16/15*a^2*(2*A+3*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:182
  public void test0262() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(7/2)*(a+a*Cos[c+d*x])), x]", //
        "-3/5*(7*A+5*C)*EllipticE[1/2*(c+d*x),2]/(a*d)-1/3*(5*A+3*C)*EllipticF[1/2*(c+d*x),2]/(a*d)+1/5*(7*A+5*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(5/2))-1/3*(5*A+3*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))-(A+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x]))+3/5*(7*A+5*C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:202
  public void test0263() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]]/Sqrt[Cos[c+d*x]], x]", //
        "1/4*(8*A+3*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/4*a*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:218
  public void test0264() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "1/128*a^(5/2)*(400*A+283*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/8*a*C*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+1/5*C*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+1/960*a^3*(1040*A+787*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/128*a^3*(400*A+283*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/240*a^2*(80*A+79*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:236
  public void test0265() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/4*(8*A+19*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)-1/2*(A+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))-1/2*(5*A+13*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/2*(A+2*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])-1/4*(2*A+7*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:258
  public void test0266() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "C*x+B*ArcTanh[Sin[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:274
  public void test0267() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/2*a^2*(3*B+2*C)*x+2/3*a^2*(3*B+2*C)*Sin[c+d*x]/d+1/6*a^2*(3*B+2*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*C*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:292
  public void test0268() {
    check( //
        "Integrate[Cos[c+d*x]^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "3/2*(B-C)*x/a-(3*B-4*C)*Sin[c+d*x]/(a*d)+3/2*(B-C)*Cos[c+d*x]*Sin[c+d*x]/(a*d)+(B-C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+1/3*(3*B-4*C)*Sin[c+d*x]^3/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:308
  public void test0269() {
    check( //
        "Integrate[Cos[c+d*x]^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "-1/2*(6*B-13*C)*x/a^3+8/15*(9*B-19*C)*Sin[c+d*x]/(a^3*d)-1/2*(6*B-13*C)*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+1/5*(B-C)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(6*B-11*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+4/15*(9*B-19*C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:334
  public void test0270() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(1/2), x]", //
        "2*B*EllipticE[1/2*(c+d*x),2]/d+2/3*C*EllipticF[1/2*(c+d*x),2]/d+2/3*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:360
  public void test0271() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/8*a*(4*A+3*(B+C))*x+1/5*a*(5*A+5*B+4*C)*Sin[c+d*x]/d+1/8*a*(4*A+3*(B+C))*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*(B+C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a*C*Cos[c+d*x]^4*Sin[c+d*x]/d-1/15*a*(5*A+5*B+4*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:376
  public void test0272() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*a^2*(6*A+7*B+8*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^2*(18*A+20*B+25*C)*Tan[c+d*x]/d+1/8*a^2*(6*A+7*B+8*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/60*a^2*(18*A+25*B+20*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/20*(2*A+5*B)*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*A*(a+a*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:392
  public void test0273() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/2*a^4*(8*A+13*B+12*C)*x+1/2*a^4*(13*A+8*B+2*C)*ArcTanh[Sin[c+d*x]]/d-5/2*a^4*(A-B-2*C)*Sin[c+d*x]/d-1/6*(15*A+6*B-2*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d-1/6*(18*A+3*B-8*C)*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d+a*(2*A+B)*(a+a*Cos[c+d*x])^3*Tan[c+d*x]/d+1/2*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:446
  public void test0274() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "1/64*(35*A+40*B+48*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/64*a*(35*A+40*B+48*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/96*a*(35*A+40*B+48*C)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/24*a*(A+8*B)*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*A*Sec[c+d*x]^3*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:462
  public void test0275() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "1/8*a^(5/2)*(25*A+38*B+40*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/24*a^3*(49*A+54*B-24*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/12*a*(5*A+6*B)*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*A*(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/24*a^2*(31*A+42*B+24*C)*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:480
  public void test0276() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*(A+3*B-7*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+2*C*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:500
  public void test0277() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "-2*B*EllipticE[1/2*(c+d*x),2]/d+2/3*(A+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/3*A*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2*B*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:534
  public void test0278() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])), x]", //
        "(3*A-3*B+C)*EllipticE[1/2*(c+d*x),2]/(a*d)+1/3*(5*A-3*B+3*C)*EllipticF[1/2*(c+d*x),2]/(a*d)+1/3*(5*A-3*B+3*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2))-(A-B+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x]))-(3*A-3*B+C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:554
  public void test0279() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/8*(8*A+6*B+5*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/12*a*(6*B+C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/8*a*(8*A+6*B+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:570
  public void test0280() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/512*a^(5/2)*(1304*A+1132*B+1015*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/60*a*(12*B+5*C)*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+1/6*C*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+1/768*a^3*(1304*A+1132*B+1015*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/960*a^3*(680*A+628*B+545*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/512*a^3*(1304*A+1132*B+1015*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/480*a^2*(120*A+156*B+115*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:588
  public void test0281() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(9/2)*Sqrt[a+a*Cos[c+d*x]]), x]", //
        "(A-B+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/7*A*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])-2/35*(A-7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*(31*A-7*B+35*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-2/105*(43*A-91*B+35*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:612
  public void test0282() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/2*b*(2*A+C)*x+a*A*ArcTanh[Sin[c+d*x]]/d+a*C*Sin[c+d*x]/d+1/2*b*C*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:662
  public void test0283() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^2, x]", //
        "C*x/b^2+2*a*(A*b^2-a^2*C+2*b^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^2*(a+b)^(3/2)*d)-(A*b^2+a^2*C)*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:710
  public void test0284() {
    check( //
        "Integrate[(a^2-b^2*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^3, x]", //
        "2*(a^2+b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2)*d)-2*a*b*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:746
  public void test0285() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/Sqrt[a+b*Cos[c+d*x]], x]", //
        "2/3*C*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)-4/3*a*C*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/3*(2*a^2*C+b^2*(3*A+C))*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:775
  public void test0286() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "2/5*a*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*b*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*b*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/21*b*(7*A+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:20
  public void test0287() {
    check( //
        "Integrate[Csc[x]/(a-a*Cos[x]^2), x]", //
        "-1/2*ArcTanh[Cos[x]]/a-1/2*Cot[x]*Csc[x]/a");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:86
  public void test0288() {
    check( //
        "Integrate[Sec[x]^2/(a+b*Cos[x]^2), x]", //
        "b*ArcTan[Cot[x]*Sqrt[a+b]/Sqrt[a]]/(a^(3/2)*Sqrt[a+b])+Tan[x]/a");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:140
  public void test0289() {
    check( //
        "Integrate[1/(a-b*Cos[x]^4), x]", //
        "-1/2*ArcTan[Cot[x]*Sqrt[Sqrt[a]-Sqrt[b]]/a^(1/4)]/(a^(3/4)*Sqrt[Sqrt[a]-Sqrt[b]])-1/2*ArcTan[Cot[x]*Sqrt[Sqrt[a]+Sqrt[b]]/a^(1/4)]/(a^(3/4)*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 4.2.9 trig^m (a+b cos^n+c cos^(2 n))^p.input:16
  public void test0290() {
    check( //
        "Integrate[Sin[x]/(a+b*Cos[x]+c*Cos[x]^2), x]", //
        "2*ArcTanh[(b+2*c*Cos[x])/Sqrt[b^2-4*a*c]]/Sqrt[b^2-4*a*c]");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:92
  public void test0291() {
    check( //
        "Integrate[Sin[a+b*x]^3*(d*Tan[a+b*x])^(1/2), x]", //
        "-5/6*d*Sin[a+b*x]/(b*Sqrt[d*Tan[a+b*x]])-1/3*d*Sin[a+b*x]^3/(b*Sqrt[d*Tan[a+b*x]])+5/12*EllipticF[-1/4*Pi+a+b*x,2]*Csc[a+b*x]*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]]/b");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:126
  public void test0292() {
    check( //
        "Integrate[Sin[a+b*x]/(d*Tan[a+b*x])^(1/2), x]", //
        "EllipticE[-1/4*Pi+a+b*x,2]*Sin[a+b*x]/(b*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]])");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:142
  public void test0293() {
    check( //
        "Integrate[Csc[a+b*x]^6/(d*Tan[a+b*x])^(5/2), x]", //
        "-2/15*d^5/(b*(d*Tan[a+b*x])^(15/2))-4/11*d^3/(b*(d*Tan[a+b*x])^(11/2))-2/7*d/(b*(d*Tan[a+b*x])^(7/2))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:180
  public void test0294() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(11/2)/(b*Tan[e+f*x])^(3/2), x]", //
        "-4/77*a^4*(a*Sin[e+f*x])^(3/2)/(b*f*Sqrt[b*Tan[e+f*x]])-2/77*a^2*(a*Sin[e+f*x])^(7/2)/(b*f*Sqrt[b*Tan[e+f*x]])+2/11*(a*Sin[e+f*x])^(11/2)/(b*f*Sqrt[b*Tan[e+f*x]])+8/77*a^6*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Tan[e+f*x]]/(b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:304
  public void test0295() {
    check( //
        "Integrate[Sec[e+f*x]^3*Sqrt[d*Tan[e+f*x]], x]", //
        "-4/5*EllipticE[-1/4*Pi+e+f*x,2]*Cos[e+f*x]*Sqrt[d*Tan[e+f*x]]/(f*Sqrt[Sin[2*e+2*f*x]])+4/5*Cos[e+f*x]*(d*Tan[e+f*x])^(3/2)/(d*f)+2/5*Sec[e+f*x]*(d*Tan[e+f*x])^(3/2)/(d*f)");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:338
  public void test0296() {
    check( //
        "Integrate[Sec[a+b*x]^5/(d*Tan[a+b*x])^(3/2), x]", //
        "-2*Sec[a+b*x]^3/(b*d*Sqrt[d*Tan[a+b*x]])-24/5*EllipticE[-1/4*Pi+a+b*x,2]*Cos[a+b*x]*Sqrt[d*Tan[a+b*x]]/(b*d^2*Sqrt[Sin[2*a+2*b*x]])+24/5*Cos[a+b*x]*(d*Tan[a+b*x])^(3/2)/(b*d^3)+12/5*Sec[a+b*x]*(d*Tan[a+b*x])^(3/2)/(b*d^3)");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:419
  public void test0297() {
    check( //
        "Integrate[1/((d*Sec[e+f*x])^(1/2)*(b*Tan[e+f*x])^(5/2)), x]", //
        "-8/3*Sqrt[b*Tan[e+f*x]]/(b^3*f*Sqrt[d*Sec[e+f*x]])+(-2/3)/(b*f*Sqrt[d*Sec[e+f*x]]*(b*Tan[e+f*x])^(3/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:49
  public void test0298() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+I*a*Tan[c+d*x])^3, x]", //
        "-2/5*I*(a+I*a*Tan[c+d*x])^5/(a^2*d)+1/6*I*(a+I*a*Tan[c+d*x])^6/(a^3*d)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:65
  public void test0299() {
    check( //
        "Integrate[Cos[c+d*x]*(a+I*a*Tan[c+d*x])^4, x]", //
        "-15/2*a^4*ArcTanh[Sin[c+d*x]]/d-15/2*I*a^4*Sec[c+d*x]/d-2*I*a*Cos[c+d*x]*(a+I*a*Tan[c+d*x])^3/d-5/2*I*Sec[c+d*x]*(a^4+I*a^4*Tan[c+d*x])/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:81
  public void test0300() {
    check( //
        "Integrate[Sec[c+d*x]*(a+I*a*Tan[c+d*x])^5, x]", //
        "63/8*a^5*ArcTanh[Sin[c+d*x]]/d+63/8*I*a^5*Sec[c+d*x]/d+9/20*I*a^2*Sec[c+d*x]*(a+I*a*Tan[c+d*x])^3/d+1/5*I*a*Sec[c+d*x]*(a+I*a*Tan[c+d*x])^4/d+21/20*I*a*Sec[c+d*x]*(a^2+I*a^2*Tan[c+d*x])^2/d+21/8*I*Sec[c+d*x]*(a^5+I*a^5*Tan[c+d*x])/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:97
  public void test0301() {
    check( //
        "Integrate[Cos[c+d*x]^10*(a+I*a*Tan[c+d*x])^8, x]", //
        "-4/5*I*a^13/(d*(a-I*a*Tan[c+d*x])^5)+I*a^12/(d*(a-I*a*Tan[c+d*x])^4)-1/3*I*a^11/(d*(a-I*a*Tan[c+d*x])^3)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:147
  public void test0302() {
    check( //
        "Integrate[Sec[c+d*x]^6/(a+I*a*Tan[c+d*x])^3, x]", //
        "4*x/a^3+4*I*Log[Cos[c+d*x]]/(a^3*d)-3*Tan[c+d*x]/(a^3*d)+1/2*I*Tan[c+d*x]^2/(a^3*d)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:195
  public void test0303() {
    check( //
        "Integrate[Sec[c+d*x]/(a+I*a*Tan[c+d*x])^8, x]", //
        "1/15*I*Sec[c+d*x]/(d*(a+I*a*Tan[c+d*x])^8)+7/195*I*Sec[c+d*x]/(a*d*(a+I*a*Tan[c+d*x])^7)+14/715*I*Sec[c+d*x]/(a^2*d*(a+I*a*Tan[c+d*x])^6)+14/1287*I*Sec[c+d*x]/(a^3*d*(a+I*a*Tan[c+d*x])^5)+8/1287*I*Sec[c+d*x]/(d*(a^2+I*a^2*Tan[c+d*x])^4)+8/2145*I*Sec[c+d*x]/(a^2*d*(a^2+I*a^2*Tan[c+d*x])^3)+16/6435*I*Sec[c+d*x]/(d*(a^4+I*a^4*Tan[c+d*x])^2)+16/6435*I*Sec[c+d*x]/(d*(a^8+I*a^8*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:215
  public void test0304() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^2/(e*Sec[c+d*x])^(7/2), x]", //
        "2/7*a^2*Sin[c+d*x]/(d*e^3*Sqrt[e*Sec[c+d*x]])+2/7*a^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(d*e^4)-4/7*I*(a^2+I*a^2*Tan[c+d*x])/(d*(e*Sec[c+d*x])^(7/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:232
  public void test0305() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^4/(e*Sec[c+d*x])^(1/2), x]", //
        "-154/15*I*a^4*(e*Sec[c+d*x])^(3/2)/(d*e^2)+154/5*a^4*EllipticE[1/2*(c+d*x),2]/(d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])-154/5*a^4*Sin[c+d*x]*Sqrt[e*Sec[c+d*x]]/(d*e)-4*I*a*(a+I*a*Tan[c+d*x])^3/(d*Sqrt[e*Sec[c+d*x]])-22/5*I*(e*Sec[c+d*x])^(3/2)*(a^4+I*a^4*Tan[c+d*x])/(d*e^2)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:250
  public void test0306() {
    check( //
        "Integrate[1/((e*Sec[c+d*x])^(5/2)*(a+I*a*Tan[c+d*x])), x]", //
        "14/45*Sin[c+d*x]/(a*d*e*(e*Sec[c+d*x])^(3/2))+14/15*EllipticE[1/2*(c+d*x),2]/(a*d*e^2*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])+2/9*I/(d*(e*Sec[c+d*x])^(5/2)*(a+I*a*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:266
  public void test0307() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(11/2)/(a+I*a*Tan[c+d*x])^3, x]", //
        "14/3*I*e^4*(e*Sec[c+d*x])^(3/2)/(a^3*d)+14*e^6*EllipticE[1/2*(c+d*x),2]/(a^3*d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])-14*e^5*Sin[c+d*x]*Sqrt[e*Sec[c+d*x]]/(a^3*d)+4*I*e^2*(e*Sec[c+d*x])^(7/2)/(a*d*(a+I*a*Tan[c+d*x])^2)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:356
  public void test0308() {
    check( //
        "Integrate[Cos[c+d*x]*(a+I*a*Tan[c+d*x])^(7/2), x]", //
        "-64/3*I*a^3*Cos[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]]/d+16/3*I*a^2*Cos[c+d*x]*(a+I*a*Tan[c+d*x])^(3/2)/d+2/3*I*a*Cos[c+d*x]*(a+I*a*Tan[c+d*x])^(5/2)/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:390
  public void test0309() {
    check( //
        "Integrate[Sec[c+d*x]/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "1/2*I*ArcTanh[Sec[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Tan[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/2*I*Sec[c+d*x]/(d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:406
  public void test0310() {
    check( //
        "Integrate[Sec[c+d*x]/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "3/16*I*ArcTanh[Sec[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Tan[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+1/4*I*Sec[c+d*x]/(d*(a+I*a*Tan[c+d*x])^(5/2))+3/16*I*Sec[c+d*x]/(a*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:422
  public void test0311() {
    check( //
        "Integrate[Sec[c+d*x]/(a+I*a*Tan[c+d*x])^(7/2), x]", //
        "5/64*I*ArcTanh[Sec[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Tan[c+d*x]])]/(a^(7/2)*d*Sqrt[2])+1/6*I*Sec[c+d*x]/(d*(a+I*a*Tan[c+d*x])^(7/2))+5/48*I*Sec[c+d*x]/(a*d*(a+I*a*Tan[c+d*x])^(5/2))+5/64*I*Sec[c+d*x]/(a^2*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:442
  public void test0312() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^(3/2)/(e*Sec[c+d*x])^(9/2), x]", //
        "16/45*I*a^2/(d*e^4*Sqrt[e*Sec[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])-4/15*I*a*Sqrt[a+I*a*Tan[c+d*x]]/(d*e^2*(e*Sec[c+d*x])^(5/2))-32/45*I*a*Sqrt[a+I*a*Tan[c+d*x]]/(d*e^4*Sqrt[e*Sec[c+d*x]])-2/9*I*(a+I*a*Tan[c+d*x])^(3/2)/(d*(e*Sec[c+d*x])^(9/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:460
  public void test0313() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(7/2)/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "-I*e^2*(e*Sec[c+d*x])^(3/2)/(a*d*Sqrt[a+I*a*Tan[c+d*x]])-3*I*e^(7/2)*ArcTan[1-Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+3*I*e^(7/2)*ArcTan[1+Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+3/2*I*e^(7/2)*Log[a-Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])-3/2*I*e^(7/2)*Log[a+Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:591
  public void test0314() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Tan[c+d*x])^3, x]", //
        "3/8*a*(a^2+b^2)*x-3/8*a*Cos[c+d*x]^2*(b-a*Tan[c+d*x])*(a+b*Tan[c+d*x])/d+1/4*Cos[c+d*x]^3*Sin[c+d*x]*(a+b*Tan[c+d*x])^3/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:654
  public void test0315() {
    check( //
        "Integrate[(a+b*Tan[e+f*x])^2/(d*Sec[e+f*x])^(9/2), x]", //
        "-10/63*a*b/(f*(d*Sec[e+f*x])^(9/2))+2/63*(7*a^2+2*b^2)*Sin[e+f*x]/(d*f*(d*Sec[e+f*x])^(7/2))+2/45*(7*a^2+2*b^2)*Sin[e+f*x]/(d^3*f*(d*Sec[e+f*x])^(3/2))+2/15*(7*a^2+2*b^2)*EllipticE[1/2*(e+f*x),2]/(d^4*f*Sqrt[Cos[e+f*x]]*Sqrt[d*Sec[e+f*x]])-2/7*b*(a+b*Tan[e+f*x])/(f*(d*Sec[e+f*x])^(9/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:739
  public void test0316() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]*(a+I*a*Tan[c+d*x]), x]", //
        "-2*I*a*Sqrt[e*Cos[c+d*x]]/d+2*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:766
  public void test0317() {
    check( //
        "Integrate[Sqrt[a+I*a*Tan[c+d*x]]/(e*Cos[c+d*x])^(5/2), x]", //
        "3/4*I*e^(5/2)*ArcTan[1-Sqrt[2]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sqrt[a]/(d*(e*Cos[c+d*x])^(5/2)*(e*Sec[c+d*x])^(5/2)*Sqrt[2])-3/4*I*e^(5/2)*ArcTan[1+Sqrt[2]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sqrt[a]/(d*(e*Cos[c+d*x])^(5/2)*(e*Sec[c+d*x])^(5/2)*Sqrt[2])-3/8*I*e^(5/2)*Log[a-Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a+I*a*Tan[c+d*x])]*Sqrt[a]/(d*(e*Cos[c+d*x])^(5/2)*(e*Sec[c+d*x])^(5/2)*Sqrt[2])+3/8*I*e^(5/2)*Log[a+Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a+I*a*Tan[c+d*x])]*Sqrt[a]/(d*(e*Cos[c+d*x])^(5/2)*(e*Sec[c+d*x])^(5/2)*Sqrt[2])+1/2*I*a/(d*(e*Cos[c+d*x])^(5/2)*Sqrt[a+I*a*Tan[c+d*x]])-3/4*I*Cos[c+d*x]^2*Sqrt[a+I*a*Tan[c+d*x]]/(d*(e*Cos[c+d*x])^(5/2))");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:21
  public void test0318() {
    check( //
        "Integrate[Csc[x]^4/(I+Tan[x]), x]", //
        "-1/2*Cot[x]^2+1/3*I*Cot[x]^3");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:43
  public void test0319() {
    check( //
        "Integrate[Sin[c+d*x]^2*(a+b*Tan[c+d*x])^2, x]", //
        "1/2*(a^2-3*b^2)*x-2*a*b*Log[Cos[c+d*x]]/d+3/2*b^2*Tan[c+d*x]/d-1/2*Cos[c+d*x]*Sin[c+d*x]*(a+b*Tan[c+d*x])^2/d");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:59
  public void test0320() {
    check( //
        "Integrate[Csc[c+d*x]^6*(a+b*Tan[c+d*x])^3, x]", //
        "-a*(a^2+6*b^2)*Cot[c+d*x]/d-1/2*b*(6*a^2+b^2)*Cot[c+d*x]^2/d-1/3*a*(2*a^2+3*b^2)*Cot[c+d*x]^3/d-3/4*a^2*b*Cot[c+d*x]^4/d-1/5*a^3*Cot[c+d*x]^5/d+b*(3*a^2+2*b^2)*Log[Tan[c+d*x]]/d+3*a*b^2*Tan[c+d*x]/d+1/2*b^3*Tan[c+d*x]^2/d");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:27
  public void test0321() {
    check( //
        "Integrate[-4*x/(b*Sqrt[Tan[a+b*x]])+x^2*Sqrt[Tan[a+b*x]]+x^2/Tan[a+b*x]^(3/2), x]", //
        "-2*x^2/(b*Sqrt[Tan[a+b*x]])");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:85
  public void test0322() {
    check( //
        "Integrate[(c+d*x)/(a+b*Tan[e+f*x]), x]", //
        "1/2*(c+d*x)^2/((a+I*b)*d)+b*(c+d*x)*Log[1+E^(2*I*(e+f*x))*(a^2+b^2)/(a+I*b)^2]/((a^2+b^2)*f)-1/2*I*b*d*PolyLog[2,-E^(2*I*(e+f*x))*(a^2+b^2)/(a+I*b)^2]/((a^2+b^2)*f^2)");
  }

  // 4.3.11 (e x)^m (a+b tan(c+d x^n))^p.input:46
  public void test0323() {
    check( //
        "Integrate[x^2*(a+b*Tan[c+d*Sqrt[x]])^2, x]", //
        "-2*I*b^2*x^(5/2)/d+1/3*a^2*x^3+2/3*I*a*b*x^3-1/3*b^2*x^3+10*b^2*x^2*Log[1+E^(2*I*(c+d*Sqrt[x]))]/d^2-4*a*b*x^(5/2)*Log[1+E^(2*I*(c+d*Sqrt[x]))]/d-20*I*b^2*x^(3/2)*PolyLog[2,-E^(2*I*(c+d*Sqrt[x]))]/d^3+10*I*a*b*x^2*PolyLog[2,-E^(2*I*(c+d*Sqrt[x]))]/d^2+30*b^2*x*PolyLog[3,-E^(2*I*(c+d*Sqrt[x]))]/d^4-20*a*b*x^(3/2)*PolyLog[3,-E^(2*I*(c+d*Sqrt[x]))]/d^3-30*I*a*b*x*PolyLog[4,-E^(2*I*(c+d*Sqrt[x]))]/d^4-15*b^2*PolyLog[5,-E^(2*I*(c+d*Sqrt[x]))]/d^6+15*I*a*b*PolyLog[6,-E^(2*I*(c+d*Sqrt[x]))]/d^6+30*I*b^2*PolyLog[4,-E^(2*I*(c+d*Sqrt[x]))]*Sqrt[x]/d^5+30*a*b*PolyLog[5,-E^(2*I*(c+d*Sqrt[x]))]*Sqrt[x]/d^5+2*b^2*x^(5/2)*Tan[c+d*Sqrt[x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:22
  public void test0324() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+I*a*Tan[c+d*x]), x]", //
        "I*a*x+I*a*Cot[c+d*x]/d+1/2*a*Cot[c+d*x]^2/d-1/3*I*a*Cot[c+d*x]^3/d-1/4*a*Cot[c+d*x]^4/d+a*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:38
  public void test0325() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^3, x]", //
        "4*a^3*x-4*I*a^3*Log[Cos[c+d*x]]/d-2*a^3*Tan[c+d*x]/d+1/2*I*a*(a+I*a*Tan[c+d*x])^2/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:54
  public void test0326() {
    check( //
        "Integrate[Cot[c+d*x]^6*(a+I*a*Tan[c+d*x])^4, x]", //
        "-8*a^4*x-8*a^4*Cot[c+d*x]/d+4*I*a^4*Cot[c+d*x]^2/d+23/15*a^4*Cot[c+d*x]^3/d+8*I*a^4*Log[Sin[c+d*x]]/d-1/5*Cot[c+d*x]^5*(a^2+I*a^2*Tan[c+d*x])^2/d-3/5*I*Cot[c+d*x]^4*(a^4+I*a^4*Tan[c+d*x])/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:88
  public void test0327() {
    check( //
        "Integrate[Tan[c+d*x]^6/(a+I*a*Tan[c+d*x])^4, x]", //
        "-65/16*x/a^4-4*I*Log[Cos[c+d*x]]/(a^4*d)+65/16*Tan[c+d*x]/(a^4*d)-2*I*Tan[c+d*x]^2/(a^4*d*(1+I*Tan[c+d*x]))+31/48*Tan[c+d*x]^3/(a^4*d*(1+I*Tan[c+d*x])^2)-1/8*Tan[c+d*x]^5/(d*(a+I*a*Tan[c+d*x])^4)+7/24*I*Tan[c+d*x]^4/(a*d*(a+I*a*Tan[c+d*x])^3)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:108
  public void test0328() {
    check( //
        "Integrate[Cot[c+d*x]^3*Sqrt[a+I*a*Tan[c+d*x]], x]", //
        "7/4*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[a]]*Sqrt[a]/d-ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]*Sqrt[a]/d-1/4*I*Cot[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]]/d-1/2*Cot[c+d*x]^2*Sqrt[a+I*a*Tan[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:124
  public void test0329() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^(7/2), x]", //
        "-8*I*a^(7/2)*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]/d+8*I*a^3*Sqrt[a+I*a*Tan[c+d*x]]/d+4/3*I*a^2*(a+I*a*Tan[c+d*x])^(3/2)/d+2/5*I*a*(a+I*a*Tan[c+d*x])^(5/2)/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:142
  public void test0330() {
    check( //
        "Integrate[Cot[c+d*x]/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "-2*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[a]]/(a^(3/2)*d)+1/2*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(3/2)*d*Sqrt[2])+3/2/(a*d*Sqrt[a+I*a*Tan[c+d*x]])+1/3/(d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:162
  public void test0331() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])/(d*Tan[e+f*x])^(3/2), x]", //
        "-2*(-1)^(3/4)*a*ArcTan[(-1)^(3/4)*Sqrt[d*Tan[e+f*x]]/Sqrt[d]]/(d^(3/2)*f)-2*a/(d*f*Sqrt[d*Tan[e+f*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:178
  public void test0332() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^2/(d*Tan[e+f*x])^(7/2), x]", //
        "4*(-1)^(3/4)*a^2*ArcTan[(-1)^(3/4)*Sqrt[d*Tan[e+f*x]]/Sqrt[d]]/(d^(7/2)*f)+4*a^2/(d^3*f*Sqrt[d*Tan[e+f*x]])-2/5*a^2/(d*f*(d*Tan[e+f*x])^(5/2))-4/3*I*a^2/(d^2*f*(d*Tan[e+f*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:398
  public void test0333() {
    check( //
        "Integrate[(a+a*Tan[e+f*x])/(d*Tan[e+f*x])^(1/2), x]", //
        "-a*ArcTan[Sqrt[d]*(1-Tan[e+f*x])/(Sqrt[2]*Sqrt[d*Tan[e+f*x]])]*Sqrt[2]/(f*Sqrt[d])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:414
  public void test0334() {
    check( //
        "Integrate[(a+a*Tan[e+f*x])^3/(d*Tan[e+f*x])^(5/2), x]", //
        "2*a^3*ArcTanh[(Sqrt[d]+Sqrt[d]*Tan[e+f*x])/(Sqrt[2]*Sqrt[d*Tan[e+f*x]])]*Sqrt[2]/(d^(5/2)*f)-16/3*a^3/(d^2*f*Sqrt[d*Tan[e+f*x]])-2/3*(a^3+a^3*Tan[e+f*x])/(d*f*(d*Tan[e+f*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:497
  public void test0335() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Tan[c+d*x]), x]", //
        "-b*x-b*Cot[c+d*x]/d-1/2*a*Cot[c+d*x]^2/d-a*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:513
  public void test0336() {
    check( //
        "Integrate[Tan[c+d*x]^2*(a+b*Tan[c+d*x])^3, x]", //
        "-a*(a^2-3*b^2)*x+b*(3*a^2-b^2)*Log[Cos[c+d*x]]/d-2*a*b^2*Tan[c+d*x]/d-1/2*b*(a+b*Tan[c+d*x])^2/d+1/4*(a+b*Tan[c+d*x])^4/(b*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:529
  public void test0337() {
    check( //
        "Integrate[Cot[c+d*x]^4*(a+b*Tan[c+d*x])^4, x]", //
        "(a^4-6*a^2*b^2+b^4)*x+1/3*a^2*(3*a^2-17*b^2)*Cot[c+d*x]/d-4/3*a^3*b*Cot[c+d*x]^2/d-4*a*b*(a^2-b^2)*Log[Sin[c+d*x]]/d-1/3*a^2*Cot[c+d*x]^3*(a+b*Tan[c+d*x])^2/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:548
  public void test0338() {
    check( //
        "Integrate[Tan[c+d*x]^4/(a+b*Tan[c+d*x])^2, x]", //
        "(a^2-b^2)*x/(a^2+b^2)^2+2*a*b*Log[Cos[c+d*x]]/((a^2+b^2)^2*d)-2*a^3*(a^2+2*b^2)*Log[a+b*Tan[c+d*x]]/(b^3*(a^2+b^2)^2*d)+(2*a^2+b^2)*Tan[c+d*x]/(b^2*(a^2+b^2)*d)-a^2*Tan[c+d*x]^2/(b*(a^2+b^2)*d*(a+b*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:581
  public void test0339() {
    check( //
        "Integrate[1/(5+3*Tan[c+d*x])^4, x]", //
        "-161/334084*x+60/83521*Log[5*Cos[c+d*x]+3*Sin[c+d*x]]/d+(-1/34)/(d*(5+3*Tan[c+d*x])^3)+(-15/1156)/(d*(5+3*Tan[c+d*x])^2)+(-99/19652)/(d*(5+3*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:601
  public void test0340() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Tan[c+d*x])^(3/2), x]", //
        "-(a-I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/d-(a+I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/d+1/4*(8*a^2-3*b^2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a]]/(d*Sqrt[a])-5/4*b*Cot[c+d*x]*Sqrt[a+b*Tan[c+d*x]]/d-1/2*a*Cot[c+d*x]^2*Sqrt[a+b*Tan[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:619
  public void test0341() {
    check( //
        "Integrate[Cot[c+d*x]/Sqrt[a+b*Tan[c+d*x]], x]", //
        "-2*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a]]/(d*Sqrt[a])+ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/(d*Sqrt[a-I*b])+ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/(d*Sqrt[a+I*b])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:635
  public void test0342() {
    check( //
        "Integrate[Tan[c+d*x]/(a+b*Tan[c+d*x])^(5/2), x]", //
        "-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/((a-I*b)^(5/2)*d)-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/((a+I*b)^(5/2)*d)+2*(a^2-b^2)/((a^2+b^2)^2*d*Sqrt[a+b*Tan[c+d*x]])+2/3*a/((a^2+b^2)*d*(a+b*Tan[c+d*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:655
  public void test0343() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])^2/Tan[c+d*x]^(3/2), x]", //
        "(a^2-2*a*b-b^2)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-(a^2-2*a*b-b^2)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-1/2*(a^2+2*a*b-b^2)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])+1/2*(a^2+2*a*b-b^2)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])-2*a^2/(d*Sqrt[Tan[c+d*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:709
  public void test0344() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])^(1/2)/Tan[c+d*x]^(7/2), x]", //
        "ArcTan[Sqrt[I*a-b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]*Sqrt[I*a-b]/d-ArcTanh[Sqrt[I*a+b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]*Sqrt[I*a+b]/d+2/15*(15*a^2+2*b^2)*Sqrt[a+b*Tan[c+d*x]]/(a^2*d*Sqrt[Tan[c+d*x]])-2/5*Sqrt[a+b*Tan[c+d*x]]/(d*Tan[c+d*x]^(5/2))-2/15*b*Sqrt[a+b*Tan[c+d*x]]/(a*d*Tan[c+d*x]^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:725
  public void test0345() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])^(5/2)/Tan[c+d*x]^(9/2), x]", //
        "I*(I*a-b)^(5/2)*ArcTan[Sqrt[I*a-b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]/d+I*(I*a+b)^(5/2)*ArcTanh[Sqrt[I*a+b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]/d+2/21*b*(49*a^2-3*b^2)*Sqrt[a+b*Tan[c+d*x]]/(a*d*Sqrt[Tan[c+d*x]])-2/7*a^2*Sqrt[a+b*Tan[c+d*x]]/(d*Tan[c+d*x]^(7/2))-6/7*a*b*Sqrt[a+b*Tan[c+d*x]]/(d*Tan[c+d*x]^(5/2))+2/21*(7*a^2-9*b^2)*Sqrt[a+b*Tan[c+d*x]]/(d*Tan[c+d*x]^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:743
  public void test0346() {
    check( //
        "Integrate[1/(Tan[c+d*x]^(5/2)*(a+b*Tan[c+d*x])^(3/2)), x]", //
        "-I*ArcTan[Sqrt[I*a-b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]/((I*a-b)^(3/2)*d)-I*ArcTanh[Sqrt[I*a+b]*Sqrt[Tan[c+d*x]]/Sqrt[a+b*Tan[c+d*x]]]/((I*a+b)^(3/2)*d)+8/3*b/(a^2*d*Sqrt[Tan[c+d*x]]*Sqrt[a+b*Tan[c+d*x]])+2/3*b^2*(5*a^2+8*b^2)*Sqrt[Tan[c+d*x]]/(a^3*(a^2+b^2)*d*Sqrt[a+b*Tan[c+d*x]])+(-2/3)/(a*d*Sqrt[a+b*Tan[c+d*x]]*Tan[c+d*x]^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:759
  public void test0347() {
    check( //
        "Integrate[1/(Sqrt[-3-2*Tan[c+d*x]]*Sqrt[Tan[c+d*x]]), x]", //
        "ArcTan[Sqrt[2-3*I]*Sqrt[Tan[c+d*x]]/Sqrt[-3-2*Tan[c+d*x]]]/(d*Sqrt[2-3*I])+ArcTan[Sqrt[2+3*I]*Sqrt[Tan[c+d*x]]/Sqrt[-3-2*Tan[c+d*x]]]/(d*Sqrt[2+3*I])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:850
  public void test0348() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])/Cot[c+d*x]^(3/2), x]", //
        "-2*(-1)^(3/4)*a*ArcTanh[(-1)^(3/4)*Sqrt[Cot[c+d*x]]]/d+2/3*I*a/(d*Cot[c+d*x]^(3/2))+2*a/(d*Sqrt[Cot[c+d*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:960
  public void test0349() {
    check( //
        "Integrate[Cot[c+d*x]^(9/2)*(a+b*Tan[c+d*x])^3, x]", //
        "2/3*a*(a^2-3*b^2)*Cot[c+d*x]^(3/2)/d-32/35*a^2*b*Cot[c+d*x]^(5/2)/d-2/7*a^2*Cot[c+d*x]^(5/2)*(b+a*Cot[c+d*x])/d+(a-b)*(a^2+4*a*b+b^2)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-(a-b)*(a^2+4*a*b+b^2)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-1/2*(a+b)*(a^2-4*a*b+b^2)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+1/2*(a+b)*(a^2-4*a*b+b^2)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+2*b*(3*a^2-b^2)*Sqrt[Cot[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:978
  public void test0350() {
    check( //
        "Integrate[1/(Sqrt[Cot[c+d*x]]*(a+b*Tan[c+d*x])^2), x]", //
        "(a^2+2*a*b-b^2)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])-(a^2+2*a*b-b^2)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])+1/2*(a^2-2*a*b-b^2)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])-1/2*(a^2-2*a*b-b^2)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])+(3*a^2-b^2)*ArcTan[Sqrt[a]*Sqrt[Cot[c+d*x]]/Sqrt[b]]*Sqrt[b]/((a^2+b^2)^2*d*Sqrt[a])-b*Sqrt[Cot[c+d*x]]/((a^2+b^2)*d*(b+a*Cot[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1072
  public void test0351() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^4*(c-I*c*Tan[e+f*x])^3, x]", //
        "1/6*I*a^4*c^3*Sec[e+f*x]^6/f+a^4*c^3*Tan[e+f*x]/f+2/3*a^4*c^3*Tan[e+f*x]^3/f+1/5*a^4*c^3*Tan[e+f*x]^5/f");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1088
  public void test0352() {
    check( //
        "Integrate[(c-I*c*Tan[e+f*x])^4/(a+I*a*Tan[e+f*x])^3, x]", //
        "-c^4*x/a^3-I*c^4*Log[Cos[e+f*x]]/(a^3*f)+8/3*I*c^4/(f*(a+I*a*Tan[e+f*x])^3)-6*I*c^4/(a*f*(a+I*a*Tan[e+f*x])^2)+6*I*c^4/(f*(a^3+I*a^3*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1106
  public void test0353() {
    check( //
        "Integrate[1/((a+I*a*Tan[e+f*x])^3*(c-I*c*Tan[e+f*x])^2), x]", //
        "5/16*x/(a^3*c^2)+1/6*I*Cos[e+f*x]^6/(a^3*c^2*f)+5/16*Cos[e+f*x]*Sin[e+f*x]/(a^3*c^2*f)+5/24*Cos[e+f*x]^3*Sin[e+f*x]/(a^3*c^2*f)+1/6*Cos[e+f*x]^5*Sin[e+f*x]/(a^3*c^2*f)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1122
  public void test0354() {
    check( //
        "Integrate[1/((a+I*a*Tan[e+f*x])*(c-I*c*Tan[e+f*x])^4), x]", //
        "5/32*x/(a*c^4)+(-1/16*I)/(a*f*(c-I*c*Tan[e+f*x])^4)+(-1/12*I)/(a*c*f*(c-I*c*Tan[e+f*x])^3)+(-3/32*I)/(a*f*(c^2-I*c^2*Tan[e+f*x])^2)+(-1/8*I)/(a*f*(c^4-I*c^4*Tan[e+f*x]))+1/32*I/(a*f*(c^4+I*c^4*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1238
  public void test0355() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^m*(c-I*c*Tan[e+f*x])^2, x]", //
        "-2*I*c^2*(a+I*a*Tan[e+f*x])^m/(f*m)+I*c^2*(a+I*a*Tan[e+f*x])^(1+m)/(a*f*(1+m))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1326
  public void test0356() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])/(c+d*Tan[e+f*x])^(3/2), x]", //
        "-2*I*a*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c-I*d]]/((c-I*d)^(3/2)*f)-2*a/((I*c+d)*f*Sqrt[c+d*Tan[e+f*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1426
  public void test0357() {
    check( //
        "Integrate[(a+b*Tan[e+f*x])^4/(c+d*Tan[e+f*x]), x]", //
        "(a^4*c-6*a^2*b^2*c+b^4*c+4*a^3*b*d-4*a*b^3*d)*x/(c^2+d^2)-(4*a^3*b*c-4*a*b^3*c-a^4*d+6*a^2*b^2*d-b^4*d)*Log[Cos[e+f*x]]/((c^2+d^2)*f)+(b*c-a*d)^4*Log[c+d*Tan[e+f*x]]/(d^3*(c^2+d^2)*f)-b^3*(b*c-3*a*d)*Tan[e+f*x]/(d^2*f)+1/2*b^2*(a+b*Tan[e+f*x])^2/(d*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:85
  public void test0358() {
    check( //
        "Integrate[Cos[a+b*x]^6*Sin[a+b*x]^2, x]", //
        "5/128*x+5/128*Cos[a+b*x]*Sin[a+b*x]/b+5/192*Cos[a+b*x]^3*Sin[a+b*x]/b+1/48*Cos[a+b*x]^5*Sin[a+b*x]/b-1/8*Cos[a+b*x]^7*Sin[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:101
  public void test0359() {
    check( //
        "Integrate[Sec[a+b*x]^4*Sin[a+b*x]^3, x]", //
        "-Sec[a+b*x]/b+1/3*Sec[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:117
  public void test0360() {
    check( //
        "Integrate[Cos[a+b*x]^4*Sin[a+b*x]^4, x]", //
        "3/128*x+3/128*Cos[a+b*x]*Sin[a+b*x]/b+1/64*Cos[a+b*x]^3*Sin[a+b*x]/b-1/16*Cos[a+b*x]^5*Sin[a+b*x]/b-1/8*Cos[a+b*x]^5*Sin[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:133
  public void test0361() {
    check( //
        "Integrate[Sec[a+b*x]^2*Sin[a+b*x]^5, x]", //
        "2*Cos[a+b*x]/b-1/3*Cos[a+b*x]^3/b+Sec[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:167
  public void test0362() {
    check( //
        "Integrate[Cos[a+b*x]^2/Sin[a+b*x]^2, x]", //
        "-x-Cot[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:183
  public void test0363() {
    check( //
        "Integrate[Sec[a+b*x]^3/Sin[a+b*x]^3, x]", //
        "-1/2*Cot[a+b*x]^2/b+2*Log[Tan[a+b*x]]/b+1/2*Tan[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:235
  public void test0364() {
    check( //
        "Integrate[Sin[a+b*x]^2/(d*Cos[a+b*x])^(7/2), x]", //
        "2/5*Sin[a+b*x]/(b*d*(d*Cos[a+b*x])^(5/2))-4/5*Sin[a+b*x]/(b*d^3*Sqrt[d*Cos[a+b*x]])+4/5*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^4*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:251
  public void test0365() {
    check( //
        "Integrate[Sin[a+b*x]^4/(d*Cos[a+b*x])^(5/2), x]", //
        "2/3*Sin[a+b*x]^3/(b*d*(d*Cos[a+b*x])^(3/2))-8/3*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*d^2*Sqrt[d*Cos[a+b*x]])+4/3*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/(b*d^3)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:269
  public void test0366() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(7/2)*Csc[a+b*x]^2, x]", //
        "-d*(d*Cos[a+b*x])^(5/2)*Csc[a+b*x]/b-5/3*d^4*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-5/3*d^3*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:313
  public void test0367() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(13/2), x]", //
        "2/11*(c*Sin[a+b*x])^(3/2)/(b*c*d*(d*Cos[a+b*x])^(11/2))+16/77*(c*Sin[a+b*x])^(3/2)/(b*c*d^3*(d*Cos[a+b*x])^(7/2))+64/231*(c*Sin[a+b*x])^(3/2)/(b*c*d^5*(d*Cos[a+b*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:347
  public void test0368() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(7/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "2/5*Sqrt[c*Sin[a+b*x]]/(b*c*d*(d*Cos[a+b*x])^(5/2))+8/5*Sqrt[c*Sin[a+b*x]]/(b*c*d^3*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:450
  public void test0369() {
    check( //
        "Integrate[Sin[e+f*x]^4*Sqrt[b*Sec[e+f*x]], x]", //
        "-4/7*b*Sin[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])-2/7*b*Sin[e+f*x]^3/(f*Sqrt[b*Sec[e+f*x]])+8/7*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:466
  public void test0370() {
    check( //
        "Integrate[Csc[e+f*x]^2*(b*Sec[e+f*x])^(3/2), x]", //
        "-3*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])-b*Csc[e+f*x]*Sqrt[b*Sec[e+f*x]]/f+3*b*Sin[e+f*x]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:536
  public void test0371() {
    check( //
        "Integrate[Sqrt[b*Sec[e+f*x]]/(a*Sin[e+f*x])^(5/2), x]", //
        "-2/3*b/(a*f*(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]])+2/3*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(a^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:554
  public void test0372() {
    check( //
        "Integrate[1/((b*Sec[e+f*x])^(3/2)*(a*Sin[e+f*x])^(3/2)), x]", //
        "ArcTan[1-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(a^(3/2)*b^(5/2)*f*Sqrt[2])-ArcTan[1+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(a^(3/2)*b^(5/2)*f*Sqrt[2])-1/2*Log[Sqrt[a]-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(a^(3/2)*b^(5/2)*f*Sqrt[2])+1/2*Log[Sqrt[a]+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(a^(3/2)*b^(5/2)*f*Sqrt[2])+(-2)/(a*b*f*Sqrt[b*Sec[e+f*x]]*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:18
  public void test0373() {
    check( //
        "Integrate[1/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-1/4*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))-3/16*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-3/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:20
  public void test0374() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "a*Sec[c+d*x]/d+a*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:52
  public void test0375() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^8, x]", //
        "4/11*(a+a*Sin[c+d*x])^11/(a^3*d)-1/3*(a+a*Sin[c+d*x])^12/(a^4*d)+1/13*(a+a*Sin[c+d*x])^13/(a^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:86
  public void test0376() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "-1/7*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^2)-1/7*Sec[c+d*x]^3/(d*(a^2+a^2*Sin[c+d*x]))+4/7*Tan[c+d*x]/(a^2*d)+4/21*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:154
  public void test0377() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/6*a*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d+1/5*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(5/2)/d-1/4*a^(5/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2])+1/4*a^2*Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:170
  public void test0378() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])^(7/2), x]", //
        "1/12*a^2*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d+1/10*a*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(5/2)/d+1/7*Sec[c+d*x]^7*(a+a*Sin[c+d*x])^(7/2)/d-1/8*a^(7/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2])+1/8*a^3*Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:224
  public void test0379() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])/(e*Cos[c+d*x])^(3/2), x]", //
        "2*a/(d*e*Sqrt[e*Cos[c+d*x]])+2*a*Sin[c+d*x]/(d*e*Sqrt[e*Cos[c+d*x]])-2*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:240
  public void test0380() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]], x]", //
        "-22/15*a^3*(e*Cos[c+d*x])^(3/2)/(d*e)-2/7*a*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^2/(d*e)-22/35*(e*Cos[c+d*x])^(3/2)*(a^3+a^3*Sin[c+d*x])/(d*e)+22/5*a^3*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:258
  public void test0381() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(11/2)/(a+a*Sin[c+d*x]), x]", //
        "2/9*e*(e*Cos[c+d*x])^(9/2)/(a*d)+2/7*e^3*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a*d)+10/21*e^6*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a*d*Sqrt[e*Cos[c+d*x]])+10/21*e^5*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:274
  public void test0382() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]), x]", //
        "2/7*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^2*d*Sqrt[e*Cos[c+d*x]])-2/7*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x])^2)-2/7*Sqrt[e*Cos[c+d*x]]/(d*e*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:290
  public void test0383() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(11/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-4/3*e*(e*Cos[c+d*x])^(9/2)/(a*d*(a+a*Sin[c+d*x])^3)-12*e^3*(e*Cos[c+d*x])^(5/2)/(d*(a^4+a^4*Sin[c+d*x]))-10*e^6*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^4*d*Sqrt[e*Cos[c+d*x]])-10*e^5*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^4*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:310
  public void test0384() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-7/12*a^2*(e*Cos[c+d*x])^(5/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])-1/3*a*(e*Cos[c+d*x])^(5/2)*Sqrt[a+a*Sin[c+d*x]]/(d*e)+7/8*a*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/d-7/8*a*e^(3/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+7/8*a*e^(3/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:326
  public void test0385() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)/(e*Cos[c+d*x])^(13/2), x]", //
        "-2*(a+a*Sin[c+d*x])^(5/2)/(d*e*(e*Cos[c+d*x])^(11/2))+4*(a+a*Sin[c+d*x])^(7/2)/(a*d*e*(e*Cos[c+d*x])^(11/2))-16/7*(a+a*Sin[c+d*x])^(9/2)/(a^2*d*e*(e*Cos[c+d*x])^(11/2))+32/77*(a+a*Sin[c+d*x])^(11/2)/(a^3*d*e*(e*Cos[c+d*x])^(11/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:344
  public void test0386() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(9/2)/(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/2*e*(e*Cos[c+d*x])^(7/2)/(a*d*(a+a*Sin[c+d*x])^(3/2))+7/4*e^3*(e*Cos[c+d*x])^(3/2)/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+21/4*e^(9/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]+a^3*Sin[c+d*x]))+21/4*e^(9/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:404
  public void test0387() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5-2*m)*(a+a*Sin[c+d*x])^m, x]", //
        "-8*a^3*(e*Cos[c+d*x])^(6-2*m)*(a+a*Sin[c+d*x])^(-3+m)/(d*e*(60-47*m+12*m^2-m^3))-4*a^2*(e*Cos[c+d*x])^(6-2*m)*(a+a*Sin[c+d*x])^(-2+m)/(d*e*(4-m)*(5-m))-a*(e*Cos[c+d*x])^(6-2*m)*(a+a*Sin[c+d*x])^(-1+m)/(d*e*(5-m))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:449
  public void test0388() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Sin[c+d*x])^3, x]", //
        "3/16*a*(2*a^2+b^2)*x-1/70*b*(17*a^2+4*b^2)*Cos[c+d*x]^5/d+3/16*a*(2*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/8*a*(2*a^2+b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d-3/14*a*b*Cos[c+d*x]^5*(a+b*Sin[c+d*x])/d-1/7*b*Cos[c+d*x]^5*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:465
  public void test0389() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+b*Sin[c+d*x])^8, x]", //
        "-7/2*b^6*(8*a^2+b^2)*x+2/15*a*b*(8*a^6-48*a^4*b^2+163*a^2*b^4+192*b^6)*Cos[c+d*x]/d+1/30*b^2*(16*a^6-88*a^4*b^2+282*a^2*b^4+105*b^6)*Cos[c+d*x]*Sin[c+d*x]/d+1/15*a*b*(8*a^4-32*a^2*b^2+87*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d+1/15*b*(8*a^4-16*a^2*b^2+35*b^4)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/d+4/15*a*b*(2*a^2+b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^4/d+1/5*Sec[c+d*x]^5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^7/d-1/15*Sec[c+d*x]^3*(a+b*Sin[c+d*x])^6*(3*a*b-(4*a^2-7*b^2)*Sin[c+d*x])/d-4/15*Sec[c+d*x]*(a+b*Sin[c+d*x])^5*(b*(4*a^2-7*b^2)-a*(2*a^2+b^2)*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:605
  public void test0390() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]], x]", //
        "-14/15*a*b*(e*Cos[c+d*x])^(3/2)/(d*e)-2/5*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e)+2/5*(5*a^2+2*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:621
  public void test0391() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])^4, x]", //
        "-26/3465*a*b*(79*a^2+74*b^2)*(e*Cos[c+d*x])^(5/2)/(d*e)-2/693*b*(167*a^2+54*b^2)*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])/(d*e)-34/99*a*b*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^2/(d*e)-2/11*b*(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^3/(d*e)+2/231*(77*a^4+132*a^2*b^2+12*b^4)*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+2/231*(77*a^4+132*a^2*b^2+12*b^4)*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:16
  public void test0392() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "-a*Csc[c+d*x]/d-1/2*a*Csc[c+d*x]^2/d-a*Log[Sin[c+d*x]]/d-a*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:48
  public void test0393() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+a*Sin[c+d*x])^4, x]", //
        "-4*a^4*Csc[c+d*x]/d-1/2*a^4*Csc[c+d*x]^2/d+5*a^4*Log[Sin[c+d*x]]/d-5/2*a^4*Sin[c+d*x]^2/d-4/3*a^4*Sin[c+d*x]^3/d-1/4*a^4*Sin[c+d*x]^4/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:67
  public void test0394() {
    check( //
        "Integrate[Tan[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "-Sec[c+d*x]/(a*d)+2/3*Sec[c+d*x]^3/(a*d)-1/5*Sec[c+d*x]^5/(a*d)+1/5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:83
  public void test0395() {
    check( //
        "Integrate[Cot[c+d*x]^11/(a+a*Sin[c+d*x])^2, x]", //
        "1/2*Csc[c+d*x]^2/(a^2*d)-2/3*Csc[c+d*x]^3/(a^2*d)-1/2*Csc[c+d*x]^4/(a^2*d)+6/5*Csc[c+d*x]^5/(a^2*d)-6/7*Csc[c+d*x]^7/(a^2*d)+1/4*Csc[c+d*x]^8/(a^2*d)+2/9*Csc[c+d*x]^9/(a^2*d)-1/10*Csc[c+d*x]^10/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:99
  public void test0396() {
    check( //
        "Integrate[Cot[c+d*x]^7/(a+a*Sin[c+d*x])^4, x]", //
        "8*Csc[c+d*x]/(a^4*d)-4*Csc[c+d*x]^2/(a^4*d)+8/3*Csc[c+d*x]^3/(a^4*d)-7/4*Csc[c+d*x]^4/(a^4*d)+4/5*Csc[c+d*x]^5/(a^4*d)-1/6*Csc[c+d*x]^6/(a^4*d)+8*Log[Sin[c+d*x]]/(a^4*d)-8*Log[1+Sin[c+d*x]]/(a^4*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:125
  public void test0397() {
    check( //
        "Integrate[Cot[e+f*x]^4/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-7/8*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(f*Sqrt[a])+9/8*Cot[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+1/12*Cot[e+f*x]*Csc[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-1/3*Cot[e+f*x]*Csc[e+f*x]^2/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:198
  public void test0398() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Sin[c+d*x])^3, x]", //
        "-3*a^2*b*Csc[c+d*x]/d-1/2*a^3*Csc[c+d*x]^2/d-a*(a^2-3*b^2)*Log[Sin[c+d*x]]/d-b*(3*a^2-b^2)*Sin[c+d*x]/d-3/2*a*b^2*Sin[c+d*x]^2/d-1/3*b^3*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:216
  public void test0399() {
    check( //
        "Integrate[Cot[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^4*d)-1/2*b*(3*a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^4*d)+1/3*(4*a^2-3*b^2)*Cot[c+d*x]/(a^3*d)+1/2*b*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:38
  public void test0400() {
    check( //
        "Integrate[(c+d*x)*Csc[a+b*x], x]", //
        "-2*(c+d*x)*ArcTanh[E^(I*(a+b*x))]/b+I*d*PolyLog[2,-E^(I*(a+b*x))]/b^2-I*d*PolyLog[2,E^(I*(a+b*x))]/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:141
  public void test0401() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*x)^3, x]", //
        "-1/2*a/(d*(c+d*x)^2)-1/2*a*f*Cos[e+f*x]/(d^2*(c+d*x))-1/2*a*f^2*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d^3-1/2*a*f^2*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d^3-1/2*a*Sin[e+f*x]/(d*(c+d*x)^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:169
  public void test0402() {
    check( //
        "Integrate[x^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-96*Sqrt[a+a*Sin[c+d*x]]/d^4+12*x^2*Sqrt[a+a*Sin[c+d*x]]/d^2+48*x*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/d^3-2*x^3*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:189
  public void test0403() {
    check( //
        "Integrate[x^2/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-2*x/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-1/2*x^2*Cot[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])-x^2*ArcTanh[E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])-4*ArcTanh[Cos[1/4*Pi+1/2*e+1/2*f*x]]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])+2*I*x*PolyLog[2,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-2*I*x*PolyLog[2,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^2*Sqrt[a+a*Sin[e+f*x]])-4*PolyLog[3,-E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])+4*PolyLog[3,E^(1/4*I*(Pi+2*e+2*f*x))]*Sin[1/4*Pi+1/2*e+1/2*f*x]/(a*f^3*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:286
  public void test0404() {
    check( //
        "Integrate[(e+f*x)*Csc[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3*(e+f*x)*ArcTanh[E^(I*(c+d*x))]/(a*d)+(e+f*x)*Cot[1/4*Pi+1/2*c+1/2*d*x]/(a*d)+(e+f*x)*Cot[c+d*x]/(a*d)-1/2*f*Csc[c+d*x]/(a*d^2)-1/2*(e+f*x)*Cot[c+d*x]*Csc[c+d*x]/(a*d)-2*f*Log[Sin[1/4*Pi+1/2*c+1/2*d*x]]/(a*d^2)-f*Log[Sin[c+d*x]]/(a*d^2)+3/2*I*f*PolyLog[2,-E^(I*(c+d*x))]/(a*d^2)-3/2*I*f*PolyLog[2,E^(I*(c+d*x))]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:319
  public void test0405() {
    check( //
        "Integrate[(e+f*x)*Csc[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-2*(e+f*x)*ArcTanh[E^(I*(c+d*x))]/(a*d)+I*f*PolyLog[2,-E^(I*(c+d*x))]/(a*d^2)-I*f*PolyLog[2,E^(I*(c+d*x))]/(a*d^2)+I*b*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(a*d*Sqrt[a^2-b^2])-I*b*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(a*d*Sqrt[a^2-b^2])+b*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(a*d^2*Sqrt[a^2-b^2])-b*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(a*d^2*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:356
  public void test0406() {
    check( //
        "Integrate[(e+f*x)^3*Cos[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/4*(e+f*x)^4/(a*f)-6*f^2*(e+f*x)*Cos[c+d*x]/(a*d^3)+(e+f*x)^3*Cos[c+d*x]/(a*d)+6*f^3*Sin[c+d*x]/(a*d^4)-3*f*(e+f*x)^2*Sin[c+d*x]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:376
  public void test0407() {
    check( //
        "Integrate[(e+f*x)^3*Sec[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "-2/3*I*(e+f*x)^3/(a*d)-I*f*(e+f*x)^2*ArcTan[E^(I*(c+d*x))]/(a*d^2)+f^3*ArcTanh[Sin[c+d*x]]/(a*d^4)+2*f*(e+f*x)^2*Log[1+E^(2*I*(c+d*x))]/(a*d^2)+f^3*Log[Cos[c+d*x]]/(a*d^4)+I*f^2*(e+f*x)*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^3)-I*f^2*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^3)-2*I*f^2*(e+f*x)*PolyLog[2,-E^(2*I*(c+d*x))]/(a*d^3)-f^3*PolyLog[3,-I*E^(I*(c+d*x))]/(a*d^4)+f^3*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^4)+f^3*PolyLog[3,-E^(2*I*(c+d*x))]/(a*d^4)-f^2*(e+f*x)*Sec[c+d*x]/(a*d^3)-1/2*f*(e+f*x)^2*Sec[c+d*x]^2/(a*d^2)-1/3*(e+f*x)^3*Sec[c+d*x]^3/(a*d)+f^2*(e+f*x)*Tan[c+d*x]/(a*d^3)+2/3*(e+f*x)^3*Tan[c+d*x]/(a*d)+1/2*f*(e+f*x)^2*Sec[c+d*x]*Tan[c+d*x]/(a*d^2)+1/3*(e+f*x)^3*Sec[c+d*x]^2*Tan[c+d*x]/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:435
  public void test0408() {
    check( //
        "Integrate[(e+f*x)^2*Cos[c+d*x]/(a+b*Sin[c+d*x])^2, x]", //
        "-(e+f*x)^2/(b*d*(a+b*Sin[c+d*x]))-2*I*f*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d^2*Sqrt[a^2-b^2])+2*I*f*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d^2*Sqrt[a^2-b^2])-2*f^2*PolyLog[2,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d^3*Sqrt[a^2-b^2])+2*f^2*PolyLog[2,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d^3*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:459
  public void test0409() {
    check( //
        "Integrate[Cos[c+d*x]^3*Cot[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "1/2*(2*a^2-3*b^2)*x/b^3-2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a*b^3*d)-ArcTanh[Cos[c+d*x]]/(a*d)+a*Cos[c+d*x]/(b^2*d)-1/2*Cos[c+d*x]*Sin[c+d*x]/(b*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:47
  public void test0410() {
    check( //
        "Integrate[Sin[c+d*x]/(a+b*x)^3, x]", //
        "-1/2*d*Cos[c+d*x]/(b^2*(a+b*x))-1/2*d^2*Cos[c-a*d/b]*SinIntegral[a*d/b+d*x]/b^3-1/2*d^2*CosIntegral[a*d/b+d*x]*Sin[c-a*d/b]/b^3-1/2*Sin[c+d*x]/(b*(a+b*x)^2)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:67
  public void test0411() {
    check( //
        "Integrate[(a+b*x^2)^2*Sin[c+d*x]/x, x]", //
        "6*b^2*x*Cos[c+d*x]/d^3-2*a*b*x*Cos[c+d*x]/d-b^2*x^3*Cos[c+d*x]/d+a^2*Cos[c]*SinIntegral[d*x]+a^2*CosIntegral[d*x]*Sin[c]-6*b^2*Sin[c+d*x]/d^4+2*a*b*Sin[c+d*x]/d^2+3*b^2*x^2*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:105
  public void test0412() {
    check( //
        "Integrate[(a+b*x^3)*Sin[c+d*x]/x^2, x]", //
        "a*d*CosIntegral[d*x]*Cos[c]-b*x*Cos[c+d*x]/d-a*d*SinIntegral[d*x]*Sin[c]+b*Sin[c+d*x]/d^2-a*Sin[c+d*x]/x");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:30
  public void test0413() {
    check( //
        "Integrate[x^2*(a+b*Sin[c+d*x^2])^2, x]", //
        "1/6*(2*a^2+b^2)*x^3-a*b*x*Cos[c+d*x^2]/d-1/8*b^2*x*Sin[2*c+2*d*x^2]/d+a*b*Cos[c]*FresnelC[x*Sqrt[2/Pi]*Sqrt[d]]*Sqrt[1/2*Pi]/d^(3/2)-a*b*FresnelS[x*Sqrt[2/Pi]*Sqrt[d]]*Sin[c]*Sqrt[1/2*Pi]/d^(3/2)+1/16*b^2*Cos[2*c]*FresnelS[2*x*Sqrt[d]/Sqrt[Pi]]*Sqrt[Pi]/d^(3/2)+1/16*b^2*FresnelC[2*x*Sqrt[d]/Sqrt[Pi]]*Sin[2*c]*Sqrt[Pi]/d^(3/2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:50
  public void test0414() {
    check( //
        "Integrate[x/(a+b*Sin[c+d*x^2]), x]", //
        "ArcTan[(b+a*Tan[1/2*(c+d*x^2)])/Sqrt[a^2-b^2]]/(d*Sqrt[a^2-b^2])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:89
  public void test0415() {
    check( //
        "Integrate[a+b*Sin[c+d*x^3], x]", //
        "a*x+1/6*I*E^(I*c)*b*x*Gamma[1/3,-I*d*x^3]/(-I*d*x^3)^(1/3)-1/6*I*b*x*Gamma[1/3,I*d*x^3]/(E^(I*c)*(I*d*x^3)^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:107
  public void test0416() {
    check( //
        "Integrate[x^2/(a+b*Sin[c+d*x^3]), x]", //
        "2/3*ArcTan[(b+a*Tan[1/2*(c+d*x^3)])/Sqrt[a^2-b^2]]/(d*Sqrt[a^2-b^2])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:180
  public void test0417() {
    check( //
        "Integrate[Sin[a+b*x^n]/x, x]", //
        "Cos[a]*SinIntegral[b*x^n]/n+CosIntegral[b*x^n]*Sin[a]/n");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:196
  public void test0418() {
    check( //
        "Integrate[x^(-1-2*n)*Sin[a+b*x^n]^2, x]", //
        "(-1/4)/(n*x^(2*n))+b^2*CosIntegral[2*b*x^n]*Cos[2*a]/n+1/4*Cos[2*(a+b*x^n)]/(n*x^(2*n))-b^2*SinIntegral[2*b*x^n]*Sin[2*a]/n-1/2*b*Sin[2*(a+b*x^n)]/(n*x^n)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:232
  public void test0419() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b*(c+d*x)^3], x]", //
        "1/6*I*E^(I*a)*(d*e-c*f)*(c+d*x)*Gamma[1/3,-I*b*(c+d*x)^3]/(d^2*(-I*b*(c+d*x)^3)^(1/3))-1/6*I*(d*e-c*f)*(c+d*x)*Gamma[1/3,I*b*(c+d*x)^3]/(E^(I*a)*d^2*(I*b*(c+d*x)^3)^(1/3))+1/6*I*E^(I*a)*f*(c+d*x)^2*Gamma[2/3,-I*b*(c+d*x)^3]/(d^2*(-I*b*(c+d*x)^3)^(2/3))-1/6*I*f*(c+d*x)^2*Gamma[2/3,I*b*(c+d*x)^3]/(E^(I*a)*d^2*(I*b*(c+d*x)^3)^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:330
  public void test0420() {
    check( //
        "Integrate[(c*e+d*e*x)^(1/3)*Sin[a+b/(c+d*x)^(2/3)], x]", //
        "3/4*b*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3)*Cos[a+b/(c+d*x)^(2/3)]/d+3/4*b^2*(e*(c+d*x))^(1/3)*Cos[a]*SinIntegral[b/(c+d*x)^(2/3)]/(d*(c+d*x)^(1/3))+3/4*b^2*(e*(c+d*x))^(1/3)*CosIntegral[b/(c+d*x)^(2/3)]*Sin[a]/(d*(c+d*x)^(1/3))+3/4*(c+d*x)*(e*(c+d*x))^(1/3)*Sin[a+b/(c+d*x)^(2/3)]/d");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:423
  public void test0421() {
    check( //
        "Integrate[x^2*(c*Sin[a+b*x]^3)^(1/3), x]", //
        "2*x*(c*Sin[a+b*x]^3)^(1/3)/b^2+2*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/b^3-x^2*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:443
  public void test0422() {
    check( //
        "Integrate[x^2*(c*Sin[a+b*x^n]^3)^(1/3), x]", //
        "1/2*I*E^(I*a)*x^3*Csc[a+b*x^n]*Gamma[3/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*(-I*b*x^n)^(3/n))-1/2*I*x^3*Csc[a+b*x^n]*Gamma[3/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*(I*b*x^n)^(3/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:463
  public void test0423() {
    check( //
        "Integrate[x^2*(c*Sin[a+b*x^2]^3)^(2/3), x]", //
        "1/6*x^3*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)-1/8*x*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)*Sin[2*a+2*b*x^2]/b+1/16*Cos[2*a]*Csc[a+b*x^2]^2*FresnelS[2*x*Sqrt[b]/Sqrt[Pi]]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]/b^(3/2)+1/16*Csc[a+b*x^2]^2*FresnelC[2*x*Sqrt[b]/Sqrt[Pi]]*Sin[2*a]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]/b^(3/2)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:32
  public void test0424() {
    check( //
        "Integrate[x*Sin[1/4+x+x^2]^2, x]", //
        "1/4*x^2-1/8*Sin[1/2+2*x+2*x^2]+1/8*FresnelC[(1+2*x)/Sqrt[Pi]]*Sqrt[Pi]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:22
  public void test0425() {
    check( //
        "Integrate[Csc[x]^2/(a+a*Sin[x]), x]", //
        "ArcTanh[Cos[x]]/a-2*Cot[x]/a+Cot[x]/(a+a*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:39
  public void test0426() {
    check( //
        "Integrate[Sin[x]/(a+a*Sin[x])^3, x]", //
        "1/5*Cos[x]/(a+a*Sin[x])^3-1/5*Cos[x]/(a*(a+a*Sin[x])^2)-1/5*Cos[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:75
  public void test0427() {
    check( //
        "Integrate[Csc[c+d*x]^3*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-19/4*a^(5/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-9/4*a^3*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:93
  public void test0428() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "3*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)+1/2*Cot[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-9/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-3/2*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:207
  public void test0429() {
    check( //
        "Integrate[Csc[e+f*x]^4*(a+b*Sin[e+f*x])^2, x]", //
        "-a*b*ArcTanh[Cos[e+f*x]]/f-1/3*(2*a^2+3*b^2)*Cot[e+f*x]/f-a*b*Cot[e+f*x]*Csc[e+f*x]/f-1/3*a^2*Cot[e+f*x]*Csc[e+f*x]^2/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:243
  public void test0430() {
    check( //
        "Integrate[1/(a+b*Sin[x])^3, x]", //
        "(2*a^2+b^2)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(5/2)+1/2*b*Cos[x]/((a^2-b^2)*(a+b*Sin[x])^2)+3/2*a*b*Cos[x]/((a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:300
  public void test0431() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^3, x]", //
        "1/5*a*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^4)+1/15*a*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:316
  public void test0432() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^4, x]", //
        "5/16*a^3*c^4*x+1/7*a^3*c^4*Cos[e+f*x]^7/f+5/16*a^3*c^4*Cos[e+f*x]*Sin[e+f*x]/f+5/24*a^3*c^4*Cos[e+f*x]^3*Sin[e+f*x]/f+1/6*a^3*c^4*Cos[e+f*x]^5*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:334
  public void test0433() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])), x]", //
        "Tan[e+f*x]/(a*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:350
  public void test0434() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^3, x]", //
        "-c^3*x/a^3-2/5*a^2*c^3*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^5)+2/3*c^3*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^3)-2*c^3*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:370
  public void test0435() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(7/2))-1/24*a*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(5/2))-1/32*a*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))-1/32*a*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(7/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:386
  public void test0436() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(9/2))-5/4*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(5/2))+15/2*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(5/2)*f*Sqrt[2])-15/4*a^3*Cos[e+f*x]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:404
  public void test0437() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*c^2*f)+1/2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^2*f*Sqrt[2]*Sqrt[c])-1/2*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:424
  public void test0438() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:440
  public void test0439() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(1/2), x]", //
        "1/3*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:456
  public void test0440() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(7/2))-1/2*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(5/2))+a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))+a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:474
  public void test0441() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/2*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:532
  public void test0442() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c+d*Sin[e+f*x])^3, x]", //
        "1/2*a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*(c+d*Sin[e+f*x])^2)-1/2*a^2*(c+4*d)*Cos[e+f*x]/(d*(c+d)^2*f*(c+d*Sin[e+f*x]))+3*a^2*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((c+d)^2*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:569
  public void test0443() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x])^3, x]", //
        "-1/5*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^3)-2/15*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^2)-2/15*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:627
  public void test0444() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x]), x]", //
        "-2/3*a*(3*c+d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/3*d*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:643
  public void test0445() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x]), x]", //
        "-2*a^(5/2)*(c-d)^2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(5/2)*f*Sqrt[c+d])+2/3*a^3*(3*c-7*d)*Cos[e+f*x]/(d^2*f*Sqrt[a+a*Sin[e+f*x]])-2/3*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:681
  public void test0446() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(3/2), x]", //
        "1/8*a^(3/2)*(c-11*d)*(c+d)^2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(3/2)*f)+1/12*a^2*(c-11*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])-1/3*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^(5/2)/(d*f*Sqrt[a+a*Sin[e+f*x]])+1/8*a^2*(c-11*d)*(c+d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:699
  public void test0447() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-(c-d)^(5/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-1/4*(15*c^2-10*c*d+7*d^2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[d]/(f*Sqrt[a])-1/2*d*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/4*(7*c-d)*d*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:78
  public void test0448() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[a+b*x]^2, x]", //
        "1/3*Sin[a+b*x]^3/b-1/5*Sin[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:94
  public void test0449() {
    check( //
        "Integrate[Cos[a+b*x]^4*Sin[a+b*x]^3, x]", //
        "-1/5*Cos[a+b*x]^5/b+1/7*Cos[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:126
  public void test0450() {
    check( //
        "Integrate[Cos[a+b*x]^6*Sin[a+b*x]^5, x]", //
        "-1/7*Cos[a+b*x]^7/b+2/9*Cos[a+b*x]^9/b-1/11*Cos[a+b*x]^11/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:142
  public void test0451() {
    check( //
        "Integrate[Sec[a+b*x]^11*Sin[a+b*x]^5, x]", //
        "1/6*Sec[a+b*x]^6/b-1/4*Sec[a+b*x]^8/b+1/10*Sec[a+b*x]^10/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:176
  public void test0452() {
    check( //
        "Integrate[Cos[a+b*x]^5/Sin[a+b*x]^3, x]", //
        "-1/2*Csc[a+b*x]^2/b-2*Log[Sin[a+b*x]]/b+1/2*Sin[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:192
  public void test0453() {
    check( //
        "Integrate[Cos[a+b*x]^3/Sin[a+b*x]^4, x]", //
        "Csc[a+b*x]/b-1/3*Csc[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:208
  public void test0454() {
    check( //
        "Integrate[Cos[a+b*x]/Sin[a+b*x]^5, x]", //
        "-1/4*Csc[a+b*x]^4/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:228
  public void test0455() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]^2, x]", //
        "4/77*d*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]/b-2/11*(d*Cos[a+b*x])^(9/2)*Sin[a+b*x]/(b*d)+20/231*d^4*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])+20/231*d^3*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:244
  public void test0456() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(9/2)*Sin[a+b*x]^4, x]", //
        "56/3315*d^3*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/b+8/663*d*(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]/b-12/221*(d*Cos[a+b*x])^(11/2)*Sin[a+b*x]/(b*d)-2/17*(d*Cos[a+b*x])^(11/2)*Sin[a+b*x]^3/(b*d)+56/1105*d^4*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:306
  public void test0457() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*(c*Sin[a+b*x])^(1/2), x]", //
        "EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:322
  public void test0458() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(3/2)/(d*Cos[a+b*x])^(15/2), x]", //
        "2/13*c*Sqrt[c*Sin[a+b*x]]/(b*d*(d*Cos[a+b*x])^(13/2))-2/117*c*Sqrt[c*Sin[a+b*x]]/(b*d^3*(d*Cos[a+b*x])^(9/2))-16/585*c*Sqrt[c*Sin[a+b*x]]/(b*d^5*(d*Cos[a+b*x])^(5/2))-64/585*c*Sqrt[c*Sin[a+b*x]]/(b*d^7*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:340
  public void test0459() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(7/2)/(c*Sin[a+b*x])^(1/2), x]", //
        "1/3*d*(d*Cos[a+b*x])^(5/2)*Sqrt[c*Sin[a+b*x]]/(b*c)+5/6*d^3*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*c)+5/12*d^4*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:475
  public void test0460() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(5/2)*Sin[e+f*x]^6, x]", //
        "2/3*b*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^5/f+40/21*b^3*Sin[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])+20/21*b^3*Sin[e+f*x]^3/(f*Sqrt[b*Sec[e+f*x]])-80/21*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:509
  public void test0461() {
    check( //
        "Integrate[Csc[e+f*x]^6/(b*Sec[e+f*x])^(3/2), x]", //
        "1/12*Csc[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])+1/30*Csc[e+f*x]^3/(b*f*Sqrt[b*Sec[e+f*x]])-1/5*Csc[e+f*x]^5/(b*f*Sqrt[b*Sec[e+f*x]])-1/12*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:529
  public void test0462() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(1/2)*Sqrt[b*Sec[e+f*x]], x]", //
        "-ArcTan[1-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[a]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])+ArcTan[1+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/(Sqrt[a]*Sqrt[b*Cos[e+f*x]])]*Sqrt[a]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])+1/2*Log[Sqrt[a]-Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[a]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])-1/2*Log[Sqrt[a]+Sqrt[2]*Sqrt[b]*Sqrt[a*Sin[e+f*x]]/Sqrt[b*Cos[e+f*x]]+Sqrt[a]*Tan[e+f*x]]*Sqrt[a]*Sqrt[b*Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(f*Sqrt[2]*Sqrt[b])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:547
  public void test0463() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(5/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2/3*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:578
  public void test0464() {
    check( //
        "Integrate[(b*Sec[e+f*x])^n*Sin[e+f*x]^3, x]", //
        "b^3*(b*Sec[e+f*x])^(-3+n)/(f*(3-n))-b*(b*Sec[e+f*x])^(-1+n)/(f*(1-n))");
  }

  // 4.1.1.1 (a+b sin)^n.input:45
  public void test0465() {
    check( //
        "Integrate[1/(-5+3*Sin[c+d*x]), x]", //
        "-1/4*x+1/2*ArcTan[Cos[c+d*x]/(3-Sin[c+d*x])]/d");
  }

  // 4.1.1.1 (a+b sin)^n.input:61
  public void test0466() {
    check( //
        "Integrate[1/(-3+5*Sin[c+d*x]), x]", //
        "1/4*Log[Cos[1/2*(c+d*x)]-3*Sin[1/2*(c+d*x)]]/d-1/4*Log[3*Cos[1/2*(c+d*x)]-Sin[1/2*(c+d*x)]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:12
  public void test0467() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+a*Sin[c+d*x]), x]", //
        "8/5*(a+a*Sin[c+d*x])^5/(a^4*d)-2*(a+a*Sin[c+d*x])^6/(a^5*d)+6/7*(a+a*Sin[c+d*x])^7/(a^6*d)-1/8*(a+a*Sin[c+d*x])^8/(a^7*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:45
  public void test0468() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "-3*a^3*x+3*a^3*Cos[c+d*x]/d+2*a^5*Cos[c+d*x]^3/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:79
  public void test0469() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "3/2*x/a^2+3/2*Cos[c+d*x]/(a^2*d)+1/2*Cos[c+d*x]^3/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:147
  public void test0470() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-256/315*a^4*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-2/9*a*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2)/d-64/105*a^3*Cos[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-8/21*a^2*Cos[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:233
  public void test0471() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/(e*Cos[c+d*x])^(5/2), x]", //
        "-2/3*a^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])+4/3*a^4*Sqrt[e*Cos[c+d*x]]/(d*e^3*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:249
  public void test0472() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/Sqrt[e*Cos[c+d*x]], x]", //
        "78/7*a^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-78/7*a^4*Sqrt[e*Cos[c+d*x]]/(d*e)-2/7*a*(a+a*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]]/(d*e)-26/35*(a^2+a^2*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]/(d*e)-78/35*(a^4+a^4*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:267
  public void test0473() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x])), x]", //
        "14/45*Sin[c+d*x]/(a*d*e*(e*Cos[c+d*x])^(5/2))+(-2/9)/(d*e*(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x]))+14/15*Sin[c+d*x]/(a*d*e^3*Sqrt[e*Cos[c+d*x]])-14/15*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a*d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:283
  public void test0474() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x])^3, x]", //
        "-4/5*e*(e*Cos[c+d*x])^(3/2)/(a*d*(a+a*Sin[c+d*x])^2)+6/5*e*(e*Cos[c+d*x])^(3/2)/(d*(a^3+a^3*Sin[c+d*x]))+6/5*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:303
  public void test0475() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-a*(e*Cos[c+d*x])^(3/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])+ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:319
  public void test0476() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)*Sqrt[e*Cos[c+d*x]], x]", //
        "-1/3*a*(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(3/2)/(d*e)-15/8*a^3*(e*Cos[c+d*x])^(3/2)/(d*e*Sqrt[a+a*Sin[c+d*x]])-3/4*a^2*(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]]/(d*e)+15/8*a^2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))+15/8*a^2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[e]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:337
  public void test0477() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)/(a+a*Sin[c+d*x])^(3/2), x]", //
        "e*(e*Cos[c+d*x])^(3/2)/(a*d*Sqrt[a+a*Sin[c+d*x]])+3*e^(5/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^2+a^2*Cos[c+d*x]+a^2*Sin[c+d*x]))+3*e^(5/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(a^2+a^2*Cos[c+d*x]+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:381
  public void test0478() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^m, x]", //
        "4*(a+a*Sin[c+d*x])^(3+m)/(a^3*d*(3+m))-4*(a+a*Sin[c+d*x])^(4+m)/(a^4*d*(4+m))+(a+a*Sin[c+d*x])^(5+m)/(a^5*d*(5+m))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:426
  public void test0479() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Sin[c+d*x]), x]", //
        "1/2*a*x-1/3*b*Cos[c+d*x]^3/d+1/2*a*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:442
  public void test0480() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+b*Sin[c+d*x])^2, x]", //
        "1/7*a*b*Sec[c+d*x]^5/d+1/7*Sec[c+d*x]^7*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d+1/7*(6*a^2-b^2)*Tan[c+d*x]/d+2/21*(6*a^2-b^2)*Tan[c+d*x]^3/d+1/35*(6*a^2-b^2)*Tan[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:476
  public void test0481() {
    check( //
        "Integrate[Cos[c+d*x]^6/(a+b*Sin[c+d*x]), x]", //
        "1/8*a*(8*a^4-20*a^2*b^2+15*b^4)*x/b^6-2*(a^2-b^2)^(5/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^6*d)+1/5*Cos[c+d*x]^5/(b*d)-1/12*Cos[c+d*x]^3*(4*(a^2-b^2)-3*a*b*Sin[c+d*x])/(b^3*d)+1/8*Cos[c+d*x]*(8*(a^2-b^2)^2-a*b*(4*a^2-7*b^2)*Sin[c+d*x])/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:492
  public void test0482() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+b*Sin[c+d*x])^2, x]", //
        "-6*a*b^2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)+b*Sec[c+d*x]/((a^2-b^2)*d*(a+b*Sin[c+d*x]))-Sec[c+d*x]*(3*a*b-(a^2+2*b^2)*Sin[c+d*x])/((a^2-b^2)^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:578
  public void test0483() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Sin[c+d*x])^(5/2), x]", //
        "-2/3*(a^2-b^2)^2/(b^5*d*(a+b*Sin[c+d*x])^(3/2))-8/3*a*(a+b*Sin[c+d*x])^(3/2)/(b^5*d)+2/5*(a+b*Sin[c+d*x])^(5/2)/(b^5*d)+8*a*(a^2-b^2)/(b^5*d*Sqrt[a+b*Sin[c+d*x]])+4*(3*a^2-b^2)*Sqrt[a+b*Sin[c+d*x]]/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:598
  public void test0484() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])/Sqrt[e*Cos[c+d*x]], x]", //
        "2*a*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-2*b*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:614
  public void test0485() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3/Sqrt[e*Cos[c+d*x]], x]", //
        "2*a*(a^2+2*b^2)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])-2/5*b*(11*a^2+4*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e)-6/5*a*b*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e)-2/5*b*(a+b*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]/(d*e)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:754
  public void test0486() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x])^m, x]", //
        "(a^2-b^2)^2*(a+b*Sin[c+d*x])^(1+m)/(b^5*d*(1+m))-4*a*(a^2-b^2)*(a+b*Sin[c+d*x])^(2+m)/(b^5*d*(2+m))+2*(3*a^2-b^2)*(a+b*Sin[c+d*x])^(3+m)/(b^5*d*(3+m))-4*a*(a+b*Sin[c+d*x])^(4+m)/(b^5*d*(4+m))+(a+b*Sin[c+d*x])^(5+m)/(b^5*d*(5+m))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:25
  public void test0487() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2*Tan[c+d*x]^5, x]", //
        "-31/8*a^2*Log[1-Sin[c+d*x]]/d-1/8*a^2*Log[1+Sin[c+d*x]]/d-2*a^2*Sin[c+d*x]/d-1/2*a^2*Sin[c+d*x]^2/d+1/4*a^4/(d*(a-a*Sin[c+d*x])^2)-9/4*a^3/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:59
  public void test0488() {
    check( //
        "Integrate[Tan[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/8*ArcTanh[Sin[c+d*x]]/(a*d)+3/8*Sec[c+d*x]*Tan[c+d*x]/(a*d)-1/4*Sec[c+d*x]*Tan[c+d*x]^3/(a*d)+1/4*Tan[c+d*x]^4/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:92
  public void test0489() {
    check( //
        "Integrate[Cot[c+d*x]^9/(a+a*Sin[c+d*x])^3, x]", //
        "-1/3*Csc[c+d*x]^3/(a^3*d)+3/4*Csc[c+d*x]^4/(a^3*d)-2/5*Csc[c+d*x]^5/(a^3*d)-1/3*Csc[c+d*x]^6/(a^3*d)+3/7*Csc[c+d*x]^7/(a^3*d)-1/8*Csc[c+d*x]^8/(a^3*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:114
  public void test0490() {
    check( //
        "Integrate[Cot[e+f*x]^2*(a+a*Sin[e+f*x])^(3/2), x]", //
        "-3*a^(3/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/f-Cot[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f+11/3*a^2*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+5/3*a*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:13
  public void test0491() {
    check( //
        "Integrate[(c+d*x)^3*Sin[a+b*x], x]", //
        "6*d^2*(c+d*x)*Cos[a+b*x]/b^3-(c+d*x)^3*Cos[a+b*x]/b-6*d^3*Sin[a+b*x]/b^4+3*d*(c+d*x)^2*Sin[a+b*x]/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:29
  public void test0492() {
    check( //
        "Integrate[(c+d*x)^2*Sin[a+b*x]^3, x]", //
        "14/9*d^2*Cos[a+b*x]/b^3-2/3*(c+d*x)^2*Cos[a+b*x]/b-2/27*d^2*Cos[a+b*x]^3/b^3+4/3*d*(c+d*x)*Sin[a+b*x]/b^2-1/3*(c+d*x)^2*Cos[a+b*x]*Sin[a+b*x]^2/b+2/9*d*(c+d*x)*Sin[a+b*x]^3/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:104
  public void test0493() {
    check( //
        "Integrate[x^(3+m)*Sin[a+b*x], x]", //
        "1/2*I*E^(I*a)*x^m*Gamma[4+m,-I*b*x]/(b^4*(-I*b*x)^m)-1/2*I*x^m*Gamma[4+m,I*b*x]/(E^(I*a)*b^4*(I*b*x)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:128
  public void test0494() {
    check( //
        "Integrate[x/Csc[e+f*x]^(5/2)-3/5*x/Sqrt[Csc[e+f*x]], x]", //
        "4/25/(f^2*Csc[e+f*x]^(5/2))-2/5*x*Cos[e+f*x]/(f*Csc[e+f*x]^(3/2))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:216
  public void test0495() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])/(c+d*x)^2, x]", //
        "-a/(d*(c+d*x))+b*f*CosIntegral[c*f/d+f*x]*Cos[e-c*f/d]/d^2-b*f*SinIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d^2-b*Sin[e+f*x]/(d*(c+d*x))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:241
  public void test0496() {
    check( //
        "Integrate[(c+d*x)^m*(a+b*Sin[e+f*x]), x]", //
        "a*(c+d*x)^(1+m)/(d*(1+m))-1/2*E^(I*(e-c*f/d))*b*(c+d*x)^m*Gamma[1+m,-I*f*(c+d*x)/d]/(f*(-I*f*(c+d*x)/d)^m)-1/2*b*(c+d*x)^m*Gamma[1+m,I*f*(c+d*x)/d]/(E^(I*(e-c*f/d))*f*(I*f*(c+d*x)/d)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:275
  public void test0497() {
    check( //
        "Integrate[Csc[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-ArcTanh[Cos[c+d*x]]/(a*d)+Cos[c+d*x]/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:82
  public void test0498() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^3])/x, x]", //
        "a*Log[x]+1/3*b*Cos[c]*SinIntegral[d*x^3]+1/3*b*CosIntegral[d*x^3]*Sin[c]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:141
  public void test0499() {
    check( //
        "Integrate[Sin[a+b/x]/x, x]", //
        "-Cos[a]*SinIntegral[b/x]-CosIntegral[b/x]*Sin[a]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:253
  public void test0500() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b*Sqrt[c+d*x]], x]", //
        "-2*f*(c+d*x)^(3/2)*Cos[a+b*Sqrt[c+d*x]]/(b*d^2)-12*f*Sin[a+b*Sqrt[c+d*x]]/(b^4*d^2)+2*(d*e-c*f)*Sin[a+b*Sqrt[c+d*x]]/(b^2*d^2)+6*f*(c+d*x)*Sin[a+b*Sqrt[c+d*x]]/(b^2*d^2)+12*f*Cos[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b^3*d^2)-2*(d*e-c*f)*Cos[a+b*Sqrt[c+d*x]]*Sqrt[c+d*x]/(b*d^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:279
  public void test0501() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b*(c+d*x)^(1/3)], x]", //
        "6*(d*e-c*f)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d^2)-360*f*(c+d*x)^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^5*d^2)-3*(d*e-c*f)*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d^2)+60*f*(c+d*x)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d^2)-3*f*(c+d*x)^(5/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d^2)+360*f*Sin[a+b*(c+d*x)^(1/3)]/(b^6*d^2)+6*(d*e-c*f)*(c+d*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d^2)-180*f*(c+d*x)^(2/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d^2)+15*f*(c+d*x)^(4/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:305
  public void test0502() {
    check( //
        "Integrate[(c*e+d*e*x)^(2/3)*Sin[a+b*(c+d*x)^(1/3)], x]", //
        "36*(e*(c+d*x))^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d)-72*(e*(c+d*x))^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^5*d*(c+d*x)^(2/3))-3*(c+d*x)^(2/3)*(e*(c+d*x))^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d)-72*(e*(c+d*x))^(2/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d*(c+d*x)^(1/3))+12*(c+d*x)^(1/3)*(e*(c+d*x))^(2/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:323
  public void test0503() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(2/3), x]", //
        "-3*b*(c+d*x)^(2/3)*CosIntegral[b/(c+d*x)^(1/3)]*Cos[a]/(d*(e*(c+d*x))^(2/3))+3*b*(c+d*x)^(2/3)*SinIntegral[b/(c+d*x)^(1/3)]*Sin[a]/(d*(e*(c+d*x))^(2/3))+3*(c+d*x)*Sin[a+b/(c+d*x)^(1/3)]/(d*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:342
  public void test0504() {
    check( //
        "Integrate[x^2*Sin[a+b*(c+d*x)^n], x]", //
        "1/2*I*E^(I*a)*c^2*(c+d*x)*Gamma[1/n,-I*b*(c+d*x)^n]/(d^3*n*(-I*b*(c+d*x)^n)^(1/n))-1/2*I*c^2*(c+d*x)*Gamma[1/n,I*b*(c+d*x)^n]/(E^(I*a)*d^3*n*(I*b*(c+d*x)^n)^(1/n))-I*E^(I*a)*c*(c+d*x)^2*Gamma[2/n,-I*b*(c+d*x)^n]/(d^3*n*(-I*b*(c+d*x)^n)^(2/n))+I*c*(c+d*x)^2*Gamma[2/n,I*b*(c+d*x)^n]/(E^(I*a)*d^3*n*(I*b*(c+d*x)^n)^(2/n))+1/2*I*E^(I*a)*(c+d*x)^3*Gamma[3/n,-I*b*(c+d*x)^n]/(d^3*n*(-I*b*(c+d*x)^n)^(3/n))-1/2*I*(c+d*x)^3*Gamma[3/n,I*b*(c+d*x)^n]/(E^(I*a)*d^3*n*(I*b*(c+d*x)^n)^(3/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:432
  public void test0505() {
    check( //
        "Integrate[x*(c*Sin[a+b*x^2]^3)^(1/3), x]", //
        "-1/2*Cot[a+b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:456
  public void test0506() {
    check( //
        "Integrate[x*(c*Sin[a+b*x]^3)^(2/3), x]", //
        "1/4*(c*Sin[a+b*x]^3)^(2/3)/b^2-1/2*x*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b+1/4*x^2*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:13
  public void test0507() {
    check( //
        "Integrate[Sin[e+f*x]^3*(a+a*Sin[e+f*x])^3, x]", //
        "23/16*a^3*x-4*a^3*Cos[e+f*x]/f+7/3*a^3*Cos[e+f*x]^3/f-3/5*a^3*Cos[e+f*x]^5/f-23/16*a^3*Cos[e+f*x]*Sin[e+f*x]/f-23/24*a^3*Cos[e+f*x]*Sin[e+f*x]^3/f-1/6*a^3*Cos[e+f*x]*Sin[e+f*x]^5/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:31
  public void test0508() {
    check( //
        "Integrate[Csc[x]^2/(a+a*Sin[x])^2, x]", //
        "2*ArcTanh[Cos[x]]/a^2-10/3*Cot[x]/a^2+2*Cot[x]/(a^2*(1+Sin[x]))+1/3*Cot[x]/(a+a*Sin[x])^2");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:68
  public void test0509() {
    check( //
        "Integrate[Csc[c+d*x]^4*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-11/8*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-11/8*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-11/12*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/3*a^2*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:86
  public void test0510() {
    check( //
        "Integrate[Csc[c+d*x]^3/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-7/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/4*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:102
  public void test0511() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "5*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)+1/4*Cot[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))+15/16*Cot[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-115/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-35/16*Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:200
  public void test0512() {
    check( //
        "Integrate[Sin[e+f*x]^3*(a+b*Sin[e+f*x])^2, x]", //
        "3/4*a*b*x-(a^2+b^2)*Cos[e+f*x]/f+1/3*(a^2+2*b^2)*Cos[e+f*x]^3/f-1/5*b^2*Cos[e+f*x]^5/f-3/4*a*b*Cos[e+f*x]*Sin[e+f*x]/f-1/2*a*b*Cos[e+f*x]*Sin[e+f*x]^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:218
  public void test0513() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])^4, x]", //
        "1/8*(8*a^4+24*a^2*b^2+3*b^4)*x-1/6*a*b*(19*a^2+16*b^2)*Cos[e+f*x]/f-1/24*b^2*(26*a^2+9*b^2)*Cos[e+f*x]*Sin[e+f*x]/f-7/12*a*b*Cos[e+f*x]*(a+b*Sin[e+f*x])^2/f-1/4*b*Cos[e+f*x]*(a+b*Sin[e+f*x])^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:309
  public void test0514() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^2, x]", //
        "a^2*x/c^2+2/3*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^3)-2*a^2*Cos[e+f*x]/(f*(c^2-c^2*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:325
  public void test0515() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^6, x]", //
        "1/11*a^3*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+2/99*a^3*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+2/693*a^3*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:343
  public void test0516() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])), x]", //
        "-1/3*Sec[e+f*x]/(c*f*(a^2+a^2*Sin[e+f*x]))+2/3*Tan[e+f*x]/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:363
  public void test0517() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/315*a*c^5*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+2/9*a*c^2*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/f+64/105*a*c^4*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])+8/21*a*c^3*Cos[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:379
  public void test0518() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/4*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(11/2))-1/8*a^2*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(7/2))+1/64*a^2*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(5/2))+3/256*a^2*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(3/2))+3/256*a^2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(9/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:397
  public void test0519() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "3/4*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(3/2))+3/4*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*c^(3/2)*f*Sqrt[2])-Sec[e+f*x]/(a*c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:413
  public void test0520() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "7/16*Cos[e+f*x]/(a^3*f*(c-c*Sin[e+f*x])^(3/2))-1/5*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(3/2)/(a^3*c^3*f)+7/16*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*c^(3/2)*f*Sqrt[2])-7/12*Sec[e+f*x]/(a^3*c*f*Sqrt[c-c*Sin[e+f*x]])-7/30*Sec[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:449
  public void test0521() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/7*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/7*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/f-2/35*a^4*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])-4/35*a^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:467
  public void test0522() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:483
  public void test0523() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-1/4*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2))-3/8*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+3/8*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:524
  public void test0524() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*Sin[e+f*x])^4, x]", //
        "a*(2*c^2-2*c*d+d^2)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/((c+d)*(c^2-d^2)^(5/2)*f)-1/3*a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])^3)-1/6*a*(2*c-3*d)*Cos[e+f*x]/((c-d)*(c+d)^2*f*(c+d*Sin[e+f*x])^2)-1/6*a*(c-4*d)*(2*c-d)*Cos[e+f*x]/((c-d)^2*(c+d)^3*f*(c+d*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:546
  public void test0525() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^4/(a+a*Sin[e+f*x]), x]", //
        "1/2*d*(8*c^3-12*c^2*d+12*c*d^2-3*d^3)*x/a+2/3*d*(3*c^3-16*c^2*d+12*c*d^2-4*d^3)*Cos[e+f*x]/(a*f)+1/6*d^2*(6*c^2-20*c*d+9*d^2)*Cos[e+f*x]*Sin[e+f*x]/(a*f)+1/3*(3*c-4*d)*d*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(a*f)-(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(f*(a+a*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:636
  public void test0526() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x]), x]", //
        "2*a^(3/2)*(c-d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(3/2)*f*Sqrt[c+d])-2*a^2*Cos[e+f*x]/(d*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:674
  public void test0527() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(3/2), x]", //
        "-3/4*(c+d)^2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[d])-1/2*a*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])-3/4*a*(c+d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:690
  public void test0528() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(c+d*Sin[e+f*x])^(1/2), x]", //
        "-1/8*a^(5/2)*(c+d)*(c^2-6*c*d+25*d^2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(5/2)*f)+1/12*a^3*(3*c-13*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(d^2*f*Sqrt[a+a*Sin[e+f*x]])-1/3*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/(d*f)-1/8*a^3*(c^2-6*c*d+25*d^2)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:708
  public void test0529() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(1/2)), x]", //
        "-1/2*(c-3*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(3/2)*(c-d)^(3/2)*f*Sqrt[2])-1/2*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/((c-d)*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:41
  public void test0530() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*(c-c*Sin[e+f*x])^(5/2))-3/2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^2*f*(c-c*Sin[e+f*x])^(3/2))-6*a^3*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-3*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^3*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:57
  public void test0531() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(15/2), x]", //
        "1/12*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c*f*(c-c*Sin[e+f*x])^(13/2))+1/120*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c^2*f*(c-c*Sin[e+f*x])^(11/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:75
  public void test0532() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-4*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-4/3*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-32*c^4*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-16*c^3*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:119
  public void test0533() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "10/77*a*c^2*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])+2/33*a*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-2/11*a*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])+2/3*a*c^4*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*a*c^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2/7*a*c^3*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:135
  public void test0534() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(9/2), x]", //
        "-28/117*a^2*(g*Cos[e+f*x])^(5/2)/(c*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+14/195*a^2*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+14/195*a^2*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+4/13*a*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*(c-c*Sin[e+f*x])^(9/2))-14/195*a^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^4*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:151
  public void test0535() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "4*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(3/2))+30/7*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*Sqrt[c-c*Sin[e+f*x]])+22*a^4*(g*Cos[e+f*x])^(5/2)/(c*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-66*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+66/7*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:169
  public void test0536() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-14/3*c^2*(g*Cos[e+f*x])^(5/2)/(a*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-14*c^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-4*c*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:214
  public void test0537() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(5-2*m)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^n, x]", //
        "-8*a^3*(g*Cos[e+f*x])^(6-2*m)*(a+a*Sin[e+f*x])^(-3+m)*(c-c*Sin[e+f*x])^n/(f*g*(3-m+n)*(4-m+n)*(5-m+n))-4*a^2*(g*Cos[e+f*x])^(6-2*m)*(a+a*Sin[e+f*x])^(-2+m)*(c-c*Sin[e+f*x])^n/(f*g*(4-m+n)*(5-m+n))-a*(g*Cos[e+f*x])^(6-2*m)*(a+a*Sin[e+f*x])^(-1+m)*(c-c*Sin[e+f*x])^n/(f*g*(5-m+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:333
  public void test0538() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "1/16*a*x-1/3*a*Cos[c+d*x]^3/d+1/5*a*Cos[c+d*x]^5/d+1/16*a*Cos[c+d*x]*Sin[c+d*x]/d-1/8*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*a*Cos[c+d*x]^3*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:350
  public void test0539() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^2, x]", //
        "1/4*a^2*ArcTanh[Cos[c+d*x]]/d-2/3*a^2*Cot[c+d*x]^3/d-1/5*a^2*Cot[c+d*x]^5/d+1/4*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:369
  public void test0540() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/2*x/a-Cos[c+d*x]/(a*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:385
  public void test0541() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "3*x/a^3+3*Cos[c+d*x]/(a^3*d)-1/3*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^3)+2*Cos[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:405
  public void test0542() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d+2/5*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/5*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+2/5*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:423
  public void test0543() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-11/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)+2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)+5/4*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:471
  public void test0544() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "11/128*a^2*x-2/5*a^2*Cos[c+d*x]^5/d+2/7*a^2*Cos[c+d*x]^7/d+11/128*a^2*Cos[c+d*x]*Sin[c+d*x]/d+11/192*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-11/48*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*a^2*Cos[c+d*x]^5*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:505
  public void test0545() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/8*x/a-1/3*Cos[c+d*x]^3/(a*d)-1/8*Cos[c+d*x]*Sin[c+d*x]/(a*d)+1/4*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:521
  public void test0546() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "ArcTanh[Cos[c+d*x]]/(a^2*d)-2*Cot[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]^3/(a^2*d)+Cot[c+d*x]*Csc[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:542
  public void test0547() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-12/35*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+8/15*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/7*a*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+164/105*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:558
  public void test0548() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-179/512*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/6*Cot[c+d*x]*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d-179/512*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+111/256*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+239/320*a^2*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+137/480*a^2*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-1/20*a*Cot[c+d*x]*Csc[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:576
  public void test0549() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)+7/4*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:604
  public void test0550() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cos[c+d*x]^6/d+1/8*a*Cos[c+d*x]^8/d+1/3*a*Sin[c+d*x]^3/d-2/5*a*Sin[c+d*x]^5/d+1/7*a*Sin[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:670
  public void test0551() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "4*Csc[c+d*x]/(a^3*d)-2*Csc[c+d*x]^2/(a^3*d)+Csc[c+d*x]^3/(a^3*d)-1/4*Csc[c+d*x]^4/(a^3*d)+4*Log[Sin[c+d*x]]/(a^3*d)-4*Log[1+Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:714
  public void test0552() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "5/8*a^2*x+5*a^2*ArcTanh[Cos[c+d*x]]/d-4*a^2*Cos[c+d*x]/d-2/3*a^2*Cos[c+d*x]^3/d+a^2*Cot[c+d*x]/d-1/3*a^2*Cot[c+d*x]^3/d-a^2*Cot[c+d*x]*Csc[c+d*x]/d-5/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a^2*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:730
  public void test0553() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "-85/16*a^3*x-1/2*a^3*ArcTanh[Cos[c+d*x]]/d+a^3*Cos[c+d*x]/d+2/3*a^3*Cos[c+d*x]^3/d+3/5*a^3*Cos[c+d*x]^5/d-3*a^3*Cot[c+d*x]/d-1/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d-43/16*a^3*Cos[c+d*x]*Sin[c+d*x]/d+5/24*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d+1/6*a^3*Cos[c+d*x]*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:748
  public void test0554() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/16*x/a-1/5*Cos[c+d*x]^5/(a*d)-1/16*Cos[c+d*x]*Sin[c+d*x]/(a*d)-1/24*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)+1/6*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:764
  public void test0555() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^7/(a+a*Sin[c+d*x])^2, x]", //
        "3/16*ArcTanh[Cos[c+d*x]]/(a^2*d)+2/3*Cot[c+d*x]^3/(a^2*d)+2/5*Cot[c+d*x]^5/(a^2*d)+3/16*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-5/24*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:876
  public void test0556() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "3*x/a^2+1/2*ArcTanh[Cos[c+d*x]]/(a^2*d)+1/3*Cos[c+d*x]^3/(a^2*d)+2*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+Cos[c+d*x]*Sin[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:892
  public void test0557() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "-3*x/a^3-1/2*ArcTanh[Cos[c+d*x]]/(a^3*d)-Cos[c+d*x]/(a^3*d)-3*Cot[c+d*x]/(a^3*d)-1/3*Cot[c+d*x]^3/(a^3*d)+3/2*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:918
  public void test0558() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]*(a+a*Sin[c+d*x])^2, x]", //
        "-2*a^2*x+2*a^2*Cos[c+d*x]/d+Sec[c+d*x]*(a+a*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1015
  public void test0559() {
    check( //
        "Integrate[Csc[c+d*x]^3*Sec[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "-7/2*a^2*ArcTanh[Cos[c+d*x]]/d-16/3*a^2*Cot[c+d*x]/d-7/2*a^2*Cot[c+d*x]*Csc[c+d*x]/d+8/3*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*(1-Sin[c+d*x]))+1/3*a^4*Cot[c+d*x]*Csc[c+d*x]/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1033
  public void test0560() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "1/5*Sec[c+d*x]^5/(a*d)-1/3*Tan[c+d*x]^3/(a*d)-1/5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1049
  public void test0561() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "1/3*Sec[c+d*x]^3/(a^3*d)-6/5*Sec[c+d*x]^5/(a^3*d)+9/7*Sec[c+d*x]^7/(a^3*d)-4/9*Sec[c+d*x]^9/(a^3*d)+1/5*Tan[c+d*x]^5/(a^3*d)+5/7*Tan[c+d*x]^7/(a^3*d)+4/9*Tan[c+d*x]^9/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1197
  public void test0562() {
    check( //
        "Integrate[Cos[e+f*x]^2/((a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(a^(3/2)*(c-d)*f)+2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[c+d]/(a^(3/2)*(c-d)*f*Sqrt[d])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1242
  public void test0563() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "1/7*(A+B)*Sec[c+d*x]^7*(a+a*Sin[c+d*x])/d+1/7*a*(6*A-B)*Tan[c+d*x]/d+2/21*a*(6*A-B)*Tan[c+d*x]^3/d+1/35*a*(6*A-B)*Tan[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1258
  public void test0564() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/35*a^2*(5*A-2*B)*Sec[c+d*x]^5/d+1/7*(A+B)*Sec[c+d*x]^7*(a+a*Sin[c+d*x])^2/d+1/7*a^2*(5*A-2*B)*Tan[c+d*x]/d+2/21*a^2*(5*A-2*B)*Tan[c+d*x]^3/d+1/35*a^2*(5*A-2*B)*Tan[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1274
  public void test0565() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "a^3*B*x+1/3*(A+B)*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^3/d-2*a^5*B*Cos[c+d*x]/(d*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1370
  public void test0566() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2*(a+b*Sin[c+d*x])^3, x]", //
        "1/16*a*(2*a^2+3*b^2)*x-1/35*b*(21*a^2+4*b^2)*Cos[c+d*x]/d+1/105*b*(21*a^2+4*b^2)*Cos[c+d*x]^3/d-1/16*a*(2*a^2+3*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/56*a*(2*a^2-7*b^2)*Cos[c+d*x]*Sin[c+d*x]^3/d+1/35*b*(a^2-b^2)*Cos[c+d*x]*Sin[c+d*x]^4/d+1/14*a*Cos[c+d*x]*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^2/d+1/7*Cos[c+d*x]*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1416
  public void test0567() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+b*Sin[c+d*x]), x]", //
        "-3/8*b*ArcTanh[Cos[c+d*x]]/d-1/5*a*Cot[c+d*x]^5/d+3/8*b*Cot[c+d*x]*Csc[c+d*x]/d-1/4*b*Cot[c+d*x]^3*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1433
  public void test0568() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]*(a+b*Sin[c+d*x])^3, x]", //
        "1/16*b*(18*a^2+b^2)*x-a^3*ArcTanh[Cos[c+d*x]]/d-1/60*a*(2*a^4-43*a^2*b^2+36*b^4)*Cos[c+d*x]/(b^2*d)-1/240*(4*a^4-84*a^2*b^2+15*b^4)*Cos[c+d*x]*Sin[c+d*x]/(b*d)-1/120*a*(2*a^2-39*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/(b^2*d)-1/120*(2*a^2-35*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/(b^2*d)+1/15*a*Cos[c+d*x]*(a+b*Sin[c+d*x])^4/(b^2*d)-1/6*Cos[c+d*x]*Sin[c+d*x]*(a+b*Sin[c+d*x])^4/(b*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1542
  public void test0569() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5*(a+b*Sin[c+d*x]), x]", //
        "2*b*Csc[c+d*x]/d+a*Csc[c+d*x]^2/d-1/3*b*Csc[c+d*x]^3/d-1/4*a*Csc[c+d*x]^4/d+a*Log[Sin[c+d*x]]/d+b*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1588
  public void test0570() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "3/128*a*b*x-1/7*(a^2+b^2)*Cos[c+d*x]^7/d+1/9*(a^2+2*b^2)*Cos[c+d*x]^9/d-1/11*b^2*Cos[c+d*x]^11/d+3/128*a*b*Cos[c+d*x]*Sin[c+d*x]/d+1/64*a*b*Cos[c+d*x]^3*Sin[c+d*x]/d+1/80*a*b*Cos[c+d*x]^5*Sin[c+d*x]/d-3/40*a*b*Cos[c+d*x]^7*Sin[c+d*x]/d-1/5*a*b*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1655
  public void test0571() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "b*ArcTanh[Cos[c+d*x]]/(a^2*d)-Cot[c+d*x]/(a*d)-2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1671
  public void test0572() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "x/b-2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^3*b*d)+1/2*(3*a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^3*d)+b*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1687
  public void test0573() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-1/16*(16*a^6-40*a^4*b^2+30*a^2*b^4-5*b^6)*x/b^7+2*a*(a^2-b^2)^(5/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^7*d)-1/30*Cos[c+d*x]^5*(6*a-5*b*Sin[c+d*x])/(b^2*d)+1/24*Cos[c+d*x]^3*(8*a*(a^2-b^2)-b*(6*a^2-5*b^2)*Sin[c+d*x])/(b^4*d)-1/16*Cos[c+d*x]*(16*a*(a^2-b^2)^2-b*(8*a^4-14*a^2*b^2+5*b^4)*Sin[c+d*x])/(b^6*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1707
  public void test0574() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "-a^2*x/(b*(a^2-b^2))+b*x/(a^2-b^2)+2*a^3*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b*(a^2-b^2)^(3/2)*d)+a*Sec[c+d*x]/((a^2-b^2)*d)-b*Tan[c+d*x]/((a^2-b^2)*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:14
  public void test0575() {
    check( //
        "Integrate[Csc[e+f*x]*(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "1/2*a^2*c*x-a^2*c*ArcTanh[Cos[e+f*x]]/f+a^2*c*Cos[e+f*x]/f+1/2*a^2*c*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:55
  public void test0576() {
    check( //
        "Integrate[1/(Sin[e+f*x]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]]), x]", //
        "Cos[e+f*x]*Log[Tan[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:55
  public void test0577() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^2, x]", //
        "a^2*(A+4*B)*x/c^2-a^2*(A+4*B)*Cos[e+f*x]/(c^2*f)+1/3*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^4)-2/3*a^2*(A+4*B)*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:71
  public void test0578() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^5, x]", //
        "1/9*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+1/63*a^3*(A-8*B)*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:89
  public void test0579() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])/(a+a*Sin[e+f*x])^2, x]", //
        "-B*c*x/a^2+1/3*(A-7*B)*c*Cos[e+f*x]/(a^2*f*(1+Sin[e+f*x]))-2/3*(A-B)*c*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:105
  public void test0580() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^6), x]", //
        "1/11*(A+B)*Sec[e+f*x]^5/(a^3*f*(c^2-c^2*Sin[e+f*x])^3)+1/99*(8*A-3*B)*Sec[e+f*x]^5/(a^3*f*(c^3-c^3*Sin[e+f*x])^2)+1/99*(8*A-3*B)*Sec[e+f*x]^5/(a^3*f*(c^6-c^6*Sin[e+f*x]))+2/33*(8*A-3*B)*Tan[e+f*x]/(a^3*c^6*f)+4/99*(8*A-3*B)*Tan[e+f*x]^3/(a^3*c^6*f)+2/165*(8*A-3*B)*Tan[e+f*x]^5/(a^3*c^6*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:125
  public void test0581() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/6*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(11/2))+1/24*a^2*(A-11*B)*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(7/2))-1/16*a^2*(A-11*B)*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(3/2))+1/16*a^2*(A-11*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(7/2)*f*Sqrt[2])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:143
  public void test0582() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "(A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*f*Sqrt[2]*Sqrt[c])-(A-B)*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:159
  public void test0583() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/6*(A+B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^3*c^2*f)-1/5*(A-B)*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*c^3*f)+1/4*(A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*f*Sqrt[2]*Sqrt[c])-1/4*(A+B)*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^3*c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:179
  public void test0584() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(3/2), x]", //
        "1/2*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(3/2))+a^2*(A+3*B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+1/2*a*(A+3*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:195
  public void test0585() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(9/2), x]", //
        "-1/84*a^2*(9*A-B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(9/2)/f-1/72*a*(9*A-B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(9/2)/f-1/9*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(9/2)/f-1/315*a^4*(9*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/126*a^3*(9*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:213
  public void test0586() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2)/Sqrt[a+a*Sin[e+f*x]], x]", //
        "(A-B)*c*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-B*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:229
  public void test0587() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(1/2)), x]", //
        "-1/4*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])-1/4*(A+B)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+1/4*(A+B)*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:273
  public void test0588() {
    check( //
        "Integrate[Sin[c+d*x]*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "1/4*a^3*A*x-2/3*a^3*A*Cos[c+d*x]^3/d+1/5*a^3*A*Cos[c+d*x]^5/d-1/4*a^3*A*Cos[c+d*x]*Sin[c+d*x]/d+1/2*a^3*A*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:384
  public void test0589() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/4*(A-B)*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*A*c+5*B*c+5*A*d-13*B*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/16*(3*A*c+5*B*c+5*A*d+19*B*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:29
  public void test0590() {
    check( //
        "Integrate[1/(a*Sin[x]^4)^(1/2), x]", //
        "-Cos[x]*Sin[x]/Sqrt[a*Sin[x]^4]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:88
  public void test0591() {
    check( //
        "Integrate[Csc[c+d*x]^2/(a-a*Sin[c+d*x]^2)^2, x]", //
        "-Cot[c+d*x]/(a^2*d)+2*Tan[c+d*x]/(a^2*d)+1/3*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:143
  public void test0592() {
    check( //
        "Integrate[Sin[c+d*x]^2/(a+b*Sin[c+d*x]^2)^3, x]", //
        "1/8*(4*a+b)*ArcTan[Sqrt[a+b]*Tan[c+d*x]/Sqrt[a]]/(a^(3/2)*(a+b)^(5/2)*d)-1/4*Cos[c+d*x]*Sin[c+d*x]/((a+b)*d*(a+b*Sin[c+d*x]^2)^2)-1/8*(2*a-b)*Cos[c+d*x]*Sin[c+d*x]/(a*(a+b)^2*d*(a+b*Sin[c+d*x]^2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:181
  public void test0593() {
    check( //
        "Integrate[(a+b*Sin[e+f*x]^2)^(3/2), x]", //
        "-1/3*b*Cos[e+f*x]*Sin[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]/f+2/3*(2*a+b)*EllipticE[e+f*x,-b/a]*Sqrt[a+b*Sin[e+f*x]^2]/(f*Sqrt[1+b*Sin[e+f*x]^2/a])-1/3*a*(a+b)*EllipticF[e+f*x,-b/a]*Sqrt[1+b*Sin[e+f*x]^2/a]/(f*Sqrt[a+b*Sin[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:262
  public void test0594() {
    check( //
        "Integrate[Sin[c+d*x]/(a-b*Sin[c+d*x]^4), x]", //
        "-1/2*ArcTan[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]-Sqrt[b]]]/(b^(1/4)*d*Sqrt[a]*Sqrt[Sqrt[a]-Sqrt[b]])-1/2*ArcTanh[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]+Sqrt[b]]]/(b^(1/4)*d*Sqrt[a]*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:370
  public void test0595() {
    check( //
        "Integrate[Cos[e+f*x]^6*(a+b*Sin[e+f*x]^2), x]", //
        "5/128*(8*a+b)*x+5/128*(8*a+b)*Cos[e+f*x]*Sin[e+f*x]/f+5/192*(8*a+b)*Cos[e+f*x]^3*Sin[e+f*x]/f+1/48*(8*a+b)*Cos[e+f*x]^5*Sin[e+f*x]/f-1/8*b*Cos[e+f*x]^7*Sin[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:404
  public void test0596() {
    check( //
        "Integrate[Cos[x]^3/(a+b*Sin[x]^2)^2, x]", //
        "-1/2*(a-b)*ArcTan[Sin[x]*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(3/2))+1/2*(a+b)*Sin[x]/(a*b*(a+b*Sin[x]^2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:612
  public void test0597() {
    check( //
        "Integrate[Tan[e+f*x]^3/(a-a*Sin[e+f*x]^2)^(3/2), x]", //
        "1/5*a/(f*(a*Cos[e+f*x]^2)^(5/2))+(-1/3)/(f*(a*Cos[e+f*x]^2)^(3/2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:650
  public void test0598() {
    check( //
        "Integrate[Tan[e+f*x]^3/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "-1/2*(2*a+b)*ArcTanh[Sqrt[a+b*Sin[e+f*x]^2]/Sqrt[a+b]]/((a+b)^(3/2)*f)+1/2*Sec[e+f*x]^2*Sqrt[a+b*Sin[e+f*x]^2]/((a+b)*f)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:701
  public void test0599() {
    check( //
        "Integrate[Cot[x]^3/(a+b*Sin[x]^3), x]", //
        "-1/2*Csc[x]^2/a-Log[Sin[x]]/a-1/3*b^(2/3)*Log[a^(1/3)+b^(1/3)*Sin[x]]/a^(5/3)+1/6*b^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*Sin[x]+b^(2/3)*Sin[x]^2]/a^(5/3)+1/3*Log[a+b*Sin[x]^3]/a+b^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*Sin[x])/(a^(1/3)*Sqrt[3])]/(a^(5/3)*Sqrt[3])");
  }

  // 4.1.8 (a+b sin)^m (c+d trig)^n.input:8
  public void test0600() {
    check( //
        "Integrate[(A+B*Cos[x])/(a+b*Sin[x]), x]", //
        "B*Log[a+b*Sin[x]]/b+2*A*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/Sqrt[a^2-b^2]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:32
  public void test0601() {
    check( //
        "Integrate[1/(c*Cos[a+b*x])^(1/2), x]", //
        "2*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[c*Cos[a+b*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:70
  public void test0602() {
    check( //
        "Integrate[(a*Cos[x]^4)^(5/2), x]", //
        "63/256*a^2*x*Sec[x]^2*Sqrt[a*Cos[x]^4]+21/128*a^2*Cos[x]*Sin[x]*Sqrt[a*Cos[x]^4]+21/160*a^2*Cos[x]^3*Sin[x]*Sqrt[a*Cos[x]^4]+9/80*a^2*Cos[x]^5*Sin[x]*Sqrt[a*Cos[x]^4]+1/10*a^2*Cos[x]^7*Sin[x]*Sqrt[a*Cos[x]^4]+63/256*a^2*Sqrt[a*Cos[x]^4]*Tan[x]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:108
  public void test0603() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+6/5*b*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:124
  public void test0604() {
    check( //
        "Integrate[Cos[c+d*x]^2*(b*Cos[c+d*x])^(5/2), x]", //
        "14/45*b*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/9*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b*d)+14/15*b^2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:142
  public void test0605() {
    check( //
        "Integrate[Cos[c+d*x]^2/Sqrt[b*Cos[c+d*x]], x]", //
        "2/3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:158
  public void test0606() {
    check( //
        "Integrate[Sec[c+d*x]/(b*Cos[c+d*x])^(3/2), x]", //
        "2/3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:174
  public void test0607() {
    check( //
        "Integrate[1/(b*Cos[c+d*x])^(7/2), x]", //
        "2/5*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(5/2))+6/5*Sin[c+d*x]/(b^3*d*Sqrt[b*Cos[c+d*x]])-6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:212
  public void test0608() {
    check( //
        "Integrate[Cos[c+d*x]^(11/2)/(b*Cos[c+d*x])^(1/2), x]", //
        "Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2/3*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+1/5*Sin[c+d*x]^5*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:228
  public void test0609() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)/(b*Cos[c+d*x])^(3/2), x]", //
        "ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:319
  public void test0610() {
    check( //
        "Integrate[Cos[a+b*x]/Csc[a+b*x]^(1/2), x]", //
        "2/3/(b*Csc[a+b*x]^(3/2))");
  }

  // 4.2.1.1 (a+b cos)^n.input:45
  public void test0611() {
    check( //
        "Integrate[1/(-5+3*Cos[c+d*x]), x]", //
        "-1/4*x-1/2*ArcTan[Sin[c+d*x]/(3-Cos[c+d*x])]/d");
  }

  // 4.2.1.1 (a+b cos)^n.input:61
  public void test0612() {
    check( //
        "Integrate[1/(-3+5*Cos[c+d*x]), x]", //
        "-1/4*Log[Cos[1/2*(c+d*x)]-2*Sin[1/2*(c+d*x)]]/d+1/4*Log[Cos[1/2*(c+d*x)]+2*Sin[1/2*(c+d*x)]]/d");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:41
  public void test0613() {
    check( //
        "Integrate[Tan[x]^2/(a+b*Cos[x]), x]", //
        "-b*ArcTanh[Sin[x]]/a^2-2*ArcTan[Sqrt[a-b]*Tan[1/2*x]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/a^2+Tan[x]/a");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:19
  public void test0614() {
    check( //
        "Integrate[Cos[a+b*x]/(c+d*x)^4, x]", //
        "-1/3*Cos[a+b*x]/(d*(c+d*x)^3)+1/6*b^2*Cos[a+b*x]/(d^3*(c+d*x))+1/6*b^3*Cos[a-b*c/d]*SinIntegral[b*c/d+b*x]/d^4+1/6*b^3*CosIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^4+1/6*b*Sin[a+b*x]/(d^2*(c+d*x)^2)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:35
  public void test0615() {
    check( //
        "Integrate[x^2*Cos[a+b*x]^4, x]", //
        "-15/64*x/b^2+1/8*x^3+3/8*x*Cos[a+b*x]^2/b^2+1/8*x*Cos[a+b*x]^4/b^2-15/64*Cos[a+b*x]*Sin[a+b*x]/b^3+3/8*x^2*Cos[a+b*x]*Sin[a+b*x]/b-1/32*Cos[a+b*x]^3*Sin[a+b*x]/b^3+1/4*x^2*Cos[a+b*x]^3*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:60
  public void test0616() {
    check( //
        "Integrate[(c+d*x)^(1/2)*Cos[a+b*x], x]", //
        "-Cos[a-b*c/d]*FresnelS[Sqrt[2/Pi]*Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sqrt[1/2*Pi]*Sqrt[d]/b^(3/2)-FresnelC[Sqrt[2/Pi]*Sqrt[b]*Sqrt[c+d*x]/Sqrt[d]]*Sin[a-b*c/d]*Sqrt[1/2*Pi]*Sqrt[d]/b^(3/2)+Sin[a+b*x]*Sqrt[c+d*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:103
  public void test0617() {
    check( //
        "Integrate[Cos[a+b*x]^(1/2), x]", //
        "2*EllipticE[1/2*(a+b*x),2]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:132
  public void test0618() {
    check( //
        "Integrate[(c+d*x)^m*Cos[a+b*x], x]", //
        "-1/2*I*E^(I*(a-b*c/d))*(c+d*x)^m*Gamma[1+m,-I*b*(c+d*x)/d]/(b*(-I*b*(c+d*x)/d)^m)+1/2*I*(c+d*x)^m*Gamma[1+m,I*b*(c+d*x)/d]/(E^(I*(a-b*c/d))*b*(I*b*(c+d*x)/d)^m)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:158
  public void test0619() {
    check( //
        "Integrate[(c+d*x)^2*(a+a*Cos[e+f*x]), x]", //
        "1/3*a*(c+d*x)^3/d+2*a*d*(c+d*x)*Cos[e+f*x]/f^2-2*a*d^2*Sin[e+f*x]/f^3+a*(c+d*x)^2*Sin[e+f*x]/f");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:180
  public void test0620() {
    check( //
        "Integrate[(c+d*x)^2/(a-a*Cos[e+f*x]), x]", //
        "-I*(c+d*x)^2/(a*f)-(c+d*x)^2*Cot[1/2*e+1/2*f*x]/(a*f)+4*d*(c+d*x)*Log[1-E^(I*(e+f*x))]/(a*f^2)-4*I*d^2*PolyLog[2,E^(I*(e+f*x))]/(a*f^3)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:202
  public void test0621() {
    check( //
        "Integrate[x^3*Sqrt[a-a*Cos[x]], x]", //
        "-96*Sqrt[a-a*Cos[x]]+12*x^2*Sqrt[a-a*Cos[x]]+48*x*Cot[1/2*x]*Sqrt[a-a*Cos[x]]-2*x^3*Cot[1/2*x]*Sqrt[a-a*Cos[x]]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:14
  public void test0622() {
    check( //
        "Integrate[Cos[a+b*x^2]/x, x]", //
        "1/2*CosIntegral[b*x^2]*Cos[a]-1/2*SinIntegral[b*x^2]*Sin[a]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:30
  public void test0623() {
    check( //
        "Integrate[Cos[a+b*x^2]^3/x^3, x]", //
        "-3/8*Cos[a+b*x^2]/x^2-1/8*Cos[3*(a+b*x^2)]/x^2-3/8*b*Cos[a]*SinIntegral[b*x^2]-3/8*b*Cos[3*a]*SinIntegral[3*b*x^2]-3/8*b*CosIntegral[b*x^2]*Sin[a]-3/8*b*CosIntegral[3*b*x^2]*Sin[3*a]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:128
  public void test0624() {
    check( //
        "Integrate[Cos[a+b*Sqrt[c+d*x]]/x, x]", //
        "CosIntegral[b*(Sqrt[c]+Sqrt[c+d*x])]*Cos[a-b*Sqrt[c]]+CosIntegral[b*Sqrt[c]-b*Sqrt[c+d*x]]*Cos[a+b*Sqrt[c]]-SinIntegral[b*(Sqrt[c]+Sqrt[c+d*x])]*Sin[a-b*Sqrt[c]]+SinIntegral[b*Sqrt[c]-b*Sqrt[c+d*x]]*Sin[a+b*Sqrt[c]]");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:13
  public void test0625() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Cos[c+d*x]), x]", //
        "3/8*a*x+a*Sin[c+d*x]/d+3/8*a*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d-2/3*a*Sin[c+d*x]^3/d+1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:30
  public void test0626() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*Sec[c+d*x]^2, x]", //
        "a^2*x+2*a^2*ArcTanh[Sin[c+d*x]]/d+a^2*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:48
  public void test0627() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*Sec[c+d*x]^2, x]", //
        "13/2*a^4*x+4*a^4*ArcTanh[Sin[c+d*x]]/d+4*a^4*Sin[c+d*x]/d+1/2*a^4*Cos[c+d*x]*Sin[c+d*x]/d+a^4*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:66
  public void test0628() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Cos[c+d*x])^2, x]", //
        "-5*x/a^2+12*Sin[c+d*x]/(a^2*d)-5*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-10/3*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)-4*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:82
  public void test0629() {
    check( //
        "Integrate[Sec[c+d*x]/(a+a*Cos[c+d*x])^3, x]", //
        "ArcTanh[Sin[c+d*x]]/(a^3*d)-1/5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-7/15*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-22/15*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:98
  public void test0630() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Cos[c+d*x])^5, x]", //
        "-1/9*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^5)-11/63*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^4)+67/315*Sin[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^3)-142/315*Sin[c+d*x]/(a^3*d*(a+a*Cos[c+d*x])^2)+83/315*Sin[c+d*x]/(d*(a^5+a^5*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:118
  public void test0631() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*Sec[c+d*x]^2, x]", //
        "ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+a*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:134
  public void test0632() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]^2, x]", //
        "5*a^(5/2)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+a^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+a^2*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:152
  public void test0633() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))-7/2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+2*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:172
  public void test0634() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])/Cos[c+d*x]^(1/2), x]", //
        "2*a*EllipticE[1/2*(c+d*x),2]/d+2*a*EllipticF[1/2*(c+d*x),2]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:188
  public void test0635() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3/Cos[c+d*x]^(7/2), x]", //
        "-36/5*a^3*EllipticE[1/2*(c+d*x),2]/d+4*a^3*EllipticF[1/2*(c+d*x),2]/d+2/5*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+36/5*a^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:206
  public void test0636() {
    check( //
        "Integrate[Cos[c+d*x]^(9/2)/(a+a*Cos[c+d*x])^2, x]", //
        "56/5*EllipticE[1/2*(c+d*x),2]/(a^2*d)-5*EllipticF[1/2*(c+d*x),2]/(a^2*d)+56/15*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a^2*d)-3*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)-5*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:222
  public void test0637() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^3), x]", //
        "119/10*EllipticE[1/2*(c+d*x),2]/(a^3*d)+11/2*EllipticF[1/2*(c+d*x),2]/(a^3*d)+11/2*Sin[c+d*x]/(a^3*d*Cos[c+d*x]^(3/2))-1/5*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^3)-2/3*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2)-119/30*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a^3+a^3*Cos[c+d*x]))-119/10*Sin[c+d*x]/(a^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:242
  public void test0638() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2), x]", //
        "163/64*a^(5/2)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+163/96*a^3*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+17/24*a^3*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+163/64*a^3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*a^2*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:260
  public void test0639() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-2/3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:276
  public void test0640() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "2*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/4*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-43/16*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-11/16*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:314
  public void test0641() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)/(a-a*Cos[c+d*x])^(1/2), x]", //
        "2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])]/(d*Sqrt[a])-ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:481
  public void test0642() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*Sec[c+d*x]^6, x]", //
        "3/8*b*ArcTanh[Sin[c+d*x]]/d+a*Tan[c+d*x]/d+3/8*b*Sec[c+d*x]*Tan[c+d*x]/d+1/4*b*Sec[c+d*x]^3*Tan[c+d*x]/d+2/3*a*Tan[c+d*x]^3/d+1/5*a*Tan[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:499
  public void test0643() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*Sec[c+d*x]^3, x]", //
        "b^3*x+1/2*a*(a^2+6*b^2)*ArcTanh[Sin[c+d*x]]/d+5/2*a^2*b*Tan[c+d*x]/d+1/2*a^2*(a+b*Cos[c+d*x])*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:534
  public void test0644() {
    check( //
        "Integrate[Sec[c+d*x]^3/(a+b*Cos[c+d*x])^2, x]", //
        "-2*b^3*(4*a^2-3*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^4*(a-b)^(3/2)*(a+b)^(3/2)*d)+1/2*(a^2+6*b^2)*ArcTanh[Sin[c+d*x]]/(a^4*d)-b*(2*a^2-3*b^2)*Tan[c+d*x]/(a^3*(a^2-b^2)*d)+1/2*(a^2-3*b^2)*Sec[c+d*x]*Tan[c+d*x]/(a^2*(a^2-b^2)*d)+b^2*Sec[c+d*x]*Tan[c+d*x]/(a*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:550
  public void test0645() {
    check( //
        "Integrate[1/(a+b*Cos[c+d*x])^4, x]", //
        "a*(2*a^2+3*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(7/2)*(a+b)^(7/2)*d)-1/3*b*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^3)-5/6*a*b*Sin[c+d*x]/((a^2-b^2)^2*d*(a+b*Cos[c+d*x])^2)-1/6*b*(11*a^2+4*b^2)*Sin[c+d*x]/((a^2-b^2)^3*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:580
  public void test0646() {
    check( //
        "Integrate[Cos[c+d*x]^3*Sqrt[3+4*Cos[c+d*x]], x]", //
        "-3/70*(3+4*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+1/14*Cos[c+d*x]*(3+4*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+47/20*EllipticE[1/2*(c+d*x),8/7]/(d*Sqrt[7])+59/60*EllipticF[1/2*(c+d*x),8/7]/(d*Sqrt[7])+59/105*Sin[c+d*x]*Sqrt[3+4*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:607
  public void test0647() {
    check( //
        "Integrate[1/(a+b*Cos[c+d*x])^(3/2), x]", //
        "-2*b*Sin[c+d*x]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])+2*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/((a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:638
  public void test0648() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+B*Cos[c+d*x]), x]", //
        "6/5*A*EllipticE[1/2*(c+d*x),2]/d+10/21*B*EllipticF[1/2*(c+d*x),2]/d+2/5*A*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+10/21*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:654
  public void test0649() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3/Cos[c+d*x]^(1/2), x]", //
        "6/5*b*(5*a^2+b^2)*EllipticE[1/2*(c+d*x),2]/d+2*a*(a^2+b^2)*EllipticF[1/2*(c+d*x),2]/d+8/5*a*b^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/5*b^2*(a+b*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:904
  public void test0650() {
    check( //
        "Integrate[(B+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(1/2), x]", //
        "2*B*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:944
  public void test0651() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "2*A*b^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2*b*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:962
  public void test0652() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^2/(b*Cos[c+d*x])^(1/2), x]", //
        "2/3*A*b*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*A*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:978
  public void test0653() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(7/2), x]", //
        "2/5*A*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(5/2))+2/3*B*Sin[c+d*x]/(b^2*d*(b*Cos[c+d*x])^(3/2))+6/5*A*Sin[c+d*x]/(b^3*d*Sqrt[b*Cos[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^3*d*Sqrt[b*Cos[c+d*x]])-6/5*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1016
  public void test0654() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(3/2), x]", //
        "1/2*A*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/2*A*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])+B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])-1/3*B*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:13
  public void test0655() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "1/8*a*(4*A+3*B)*x+a*(A+B)*Sin[c+d*x]/d+1/8*a*(4*A+3*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*B*Cos[c+d*x]^3*Sin[c+d*x]/d-1/3*a*(A+B)*Sin[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:29
  public void test0656() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "1/8*a^2*(7*A+8*B)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(4*A+5*B)*Tan[c+d*x]/d+1/8*a^2*(7*A+8*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/12*a^2*(5*A+4*B)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*A*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:45
  public void test0657() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "a^4*(A+4*B)*x+1/2*a^4*(12*A+13*B)*ArcTanh[Sin[c+d*x]]/d-5/2*a^4*(2*A+B)*Sin[c+d*x]/d+1/3*(11*A+9*B)*(a^4+a^4*Cos[c+d*x])*Tan[c+d*x]/d+1/2*(2*A+B)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:80
  public void test0658() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^4, x]", //
        "B*x/a^4-1/105*(6*A-55*B)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)+1/105*(12*A-215*B)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))+1/7*(A-B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)+1/35*(3*A-10*B)*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:100
  public void test0659() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "2/105*(39*A+34*B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/45*a^2*(39*A+34*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/63*a^2*(9*A+10*B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-4/315*a*(39*A+34*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/9*a*B*Cos[c+d*x]^3*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:116
  public void test0660() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^6, x]", //
        "1/128*a^(5/2)*(283*A+326*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/5*a*A*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]^4*Tan[c+d*x]/d+1/128*a^3*(283*A+326*B)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/192*a^3*(283*A+326*B)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/240*a^3*(157*A+170*B)*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/40*a^2*(13*A+10*B)*Sec[c+d*x]^3*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:134
  public void test0661() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(5/2), x]", //
        "1/4*(A-B)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))+1/16*(13*A-21*B)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+1/16*(163*A-283*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/120*(985*A-1729*B)*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])-1/80*(85*A-157*B)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+1/240*(475*A-787*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:154
  public void test0662() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x])*Sqrt[Cos[c+d*x]], x]", //
        "4/5*a^2*(4*A+3*B)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(7*A+6*B)*EllipticF[1/2*(c+d*x),2]/d+2/35*a^2*(7*A+9*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*B*Cos[c+d*x]^(3/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+4/21*a^2*(7*A+6*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:172
  public void test0663() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sqrt[Cos[c+d*x]]/(a+a*Cos[c+d*x]), x]", //
        "-(A-3*B)*EllipticE[1/2*(c+d*x),2]/(a*d)+(A-B)*EllipticF[1/2*(c+d*x),2]/(a*d)+(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:188
  public void test0664() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/((a+a*Cos[c+d*x])^3*Sqrt[Cos[c+d*x]]), x]", //
        "1/10*(9*A+B)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(3*A+B)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^3)-1/15*(6*A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)-1/10*(9*A+B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:208
  public void test0665() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "2/15*a^2*(6*A+5*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+2/15*a^2*(18*A+25*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/5*a*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(5/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:226
  public void test0666() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "(A-B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/3*A*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-2/3*(A-3*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:242
  public void test0667() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(7/2), x]", //
        "1/64*(13*A+7*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])+1/6*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(7/2))+1/16*(A+3*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(5/2))-1/192*(5*A-17*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:264
  public void test0668() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "b*(A*b+2*a*B)*x+a*(2*A*b+a*B)*ArcTanh[Sin[c+d*x]]/d+b^2*B*Sin[c+d*x]/d+a^2*A*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:280
  public void test0669() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x], x]", //
        "1/8*(32*a^3*A*b+16*a*A*b^3+8*a^4*B+24*a^2*b^2*B+3*b^4*B)*x+a^4*A*ArcTanh[Sin[c+d*x]]/d+1/6*b*(34*a^2*A*b+4*A*b^3+19*a^3*B+16*a*b^2*B)*Sin[c+d*x]/d+1/24*b^2*(32*a*A*b+26*a^2*B+9*b^2*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*b*(4*A*b+7*a*B)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*b*B*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:298
  public void test0670() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^2, x]", //
        "(A*b-2*a*B)*x/b^3-2*a*(a^2*A*b-2*A*b^3-2*a^3*B+3*a*b^2*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^3*(a+b)^(3/2)*d)+B*Sin[c+d*x]/(b^2*d)-a^2*(A*b-a*B)*Sin[c+d*x]/(b^2*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:314
  public void test0671() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^4, x]", //
        "(a^3*A+4*a*A*b^2-3*a^2*b*B-2*b^3*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(7/2)*(a+b)^(7/2)*d)-1/3*a^2*(A*b-a*B)*Sin[c+d*x]/(b^2*(a^2-b^2)*d*(a+b*Cos[c+d*x])^3)+1/6*a*(a^2*A*b-6*A*b^3-4*a^3*B+9*a*b^2*B)*Sin[c+d*x]/(b^2*(a^2-b^2)^2*d*(a+b*Cos[c+d*x])^2)+1/6*(a^4*A*b-10*a^2*A*b^3-6*A*b^5+2*a^5*B-5*a^3*b^2*B+18*a*b^4*B)*Sin[c+d*x]/(b^2*(a^2-b^2)^3*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:330
  public void test0672() {
    check( //
        "Integrate[Cos[c+d*x]*(a*B+b*B*Cos[c+d*x])/(a+b*Cos[c+d*x])^2, x]", //
        "B*x/b-2*a*B*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:365
  public void test0673() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(1/2), x]", //
        "2/15*(5*A*b-4*a*B)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d)+2/5*B*Cos[c+d*x]*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)-2/15*(10*a*A*b-8*a^2*B-9*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^3*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/15*(10*a^2*A*b+5*A*b^3-8*a^3*B-7*a*b^2*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^3*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:396
  public void test0674() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "2/5*(5*a*A+3*b*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(A*b+a*B)*EllipticF[1/2*(c+d*x),2]/d+2/5*b*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*(A*b+a*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:412
  public void test0675() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x])/Cos[c+d*x]^(5/2), x]", //
        "-2*(3*a^2*A*b-A*b^3+a^3*B-3*a*b^2*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(a^3*A+9*a*A*b^2+9*a^2*b*B+b^3*B)*EllipticF[1/2*(c+d*x),2]/d+2/3*a*A*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/3*a^2*(7*A*b+3*a*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-2/3*b^2*(a*A-b*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:32
  public void test0676() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/3*A*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(3/2))+2/3*(A+3*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:59
  public void test0677() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]], x]", //
        "2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^2*d)+2/21*b*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/21*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:75
  public void test0678() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/21*b^3*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/21*b^2*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:93
  public void test0679() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^5/(b*Cos[c+d*x])^(1/2), x]", //
        "2/9*A*b^4*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(9/2))+2/45*b^2*(7*A+9*C)*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/15*(7*A+9*C)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2/15*(7*A+9*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:109
  public void test0680() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(b*Cos[c+d*x])^(5/2), x]", //
        "2/7*A*b*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+2/21*(5*A+7*C)*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(3/2))+2/21*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:129
  public void test0681() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "A*b*ArcTanh[Sin[c+d*x]]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+b*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:147
  public void test0682() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]], x]", //
        "1/2*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+A*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]+1/2*C*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:163
  public void test0683() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "1/2*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])+A*x*Sqrt[Cos[c+d*x]]/(b^2*Sqrt[b*Cos[c+d*x]])+1/2*C*x*Sqrt[Cos[c+d*x]]/(b^2*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:356
  public void test0684() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/3*b^2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2/5*b*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:374
  public void test0685() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)+2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:390
  public void test0686() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^4*d)+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/3*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)+2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:410
  public void test0687() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/4*b*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+1/5*b*C*Cos[c+d*x]^(7/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*b*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/5*b*(5*A+4*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/15*b*(5*A+4*C)*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+3/8*b*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:18
  public void test0688() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "1/2*a*(A+2*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(2*A+3*C)*Tan[c+d*x]/d+1/2*a*A*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:34
  public void test0689() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/2*a^3*(2*A+7*C)*x+1/2*a^3*(7*A+2*C)*ArcTanh[Sin[c+d*x]]/d-5/2*a^3*(A-C)*Sin[c+d*x]/d-1/2*(4*A-C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+3/2*A*(a^2+a^2*Cos[c+d*x])^2*Tan[c+d*x]/(a*d)+1/2*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:52
  public void test0690() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "3/8*(4*A+5*C)*x/a-(3*A+4*C)*Sin[c+d*x]/(a*d)+3/8*(4*A+5*C)*Cos[c+d*x]*Sin[c+d*x]/(a*d)+1/4*(4*A+5*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)-(A+C)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+1/3*(3*A+4*C)*Sin[c+d*x]^3/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:84
  public void test0691() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^4, x]", //
        "-4*A*ArcTanh[Sin[c+d*x]]/(a^4*d)+2/105*(332*A+3*C)*Tan[c+d*x]/(a^4*d)-1/105*(88*A-3*C)*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-4*A*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/35*(6*A-C)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:104
  public void test0692() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "3*a^(3/2)*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/3*a^2*(3*A-8*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/3*a*(3*A-2*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+A*(a+a*Cos[c+d*x])^(3/2)*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:122
  public void test0693() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2)/Sqrt[a+a*Cos[c+d*x]], x]", //
        "(A+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-4/105*(35*A+37*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-2/35*C*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/7*C*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*(35*A+31*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:138
  public void test0694() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-1/4*(A+C)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(5*A+21*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(75*A+283*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+1/120*(465*A+1729*C)*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+1/80*(45*A+157*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])-1/240*(195*A+787*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:158
  public void test0695() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "16/15*a^2*(3*A+2*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]/d+2/105*a^2*(21*A+19*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/9*C*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+8/63*C*Cos[c+d*x]^(3/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+4/21*a^2*(7*A+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:176
  public void test0696() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "-3/5*(5*A+7*C)*EllipticE[1/2*(c+d*x),2]/(a*d)+5/21*(7*A+9*C)*EllipticF[1/2*(c+d*x),2]/(a*d)-1/5*(5*A+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d)+1/7*(7*A+9*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(a*d)-(A+C)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+5/21*(7*A+9*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:192
  public void test0697() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]/(a+a*Cos[c+d*x])^3, x]", //
        "1/10*(A-9*C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(A+3*C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A+C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+2/15*(2*A-3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)-1/10*(A-9*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:212
  public void test0698() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "3*a^(3/2)*C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))-1/3*a^2*(8*A-3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+2*a*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:230
  public void test0699() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]/Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/4*(8*A+7*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-(A+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/2*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/4*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:246
  public void test0700() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "-1/4*(A+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2))-1/16*(17*A+C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2))+1/16*(163*A+19*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+5/48*(19*A+3*C)*Sin[c+d*x]/(a^2*d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-1/48*(299*A+27*C)*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }
}
