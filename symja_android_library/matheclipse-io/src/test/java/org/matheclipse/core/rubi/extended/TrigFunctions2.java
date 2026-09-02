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
public class TrigFunctions2 extends AbstractRubiTestCase {
  static boolean init = true;

  public TrigFunctions2(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("TrigFunctions2");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:268
  public void test0001() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "a*C*x+a*(B+C)*ArcTanh[Sin[c+d*x]]/d+a*B*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:284
  public void test0002() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "1/2*a^3*(7*B+5*C)*x+a^3*B*ArcTanh[Sin[c+d*x]]/d+5/2*a^3*(B+C)*Sin[c+d*x]/d+1/3*a*C*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/6*(3*B+5*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:322
  public void test0003() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/105*a*(15*B+13*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/63*(9*B-2*C)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/9*C*(a+a*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(a*d)+64/315*a^3*(15*B+13*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+16/315*a^2*(15*B+13*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:354
  public void test0004() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "B*x+A*ArcTanh[Sin[c+d*x]]/d+C*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:370
  public void test0005() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/8*a^2*(12*A+8*B+7*C)*x+1/6*a^2*(12*A+8*B+7*C)*Sin[c+d*x]/d+1/24*a^2*(12*A+8*B+7*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*(4*B-C)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:386
  public void test0006() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^7, x]", //
        "1/16*a^3*(23*A+26*B+30*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^3*(34*A+38*B+45*C)*Tan[c+d*x]/d+1/16*a^3*(23*A+26*B+30*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/120*a^3*(73*A+86*B+90*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/120*(31*A+42*B+30*C)*(a^3+a^3*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/10*(A+2*B)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/(a*d)+1/6*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^5*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:404
  public void test0007() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x]), x]", //
        "C*x/a+A*ArcTanh[Sin[c+d*x]]/(a*d)-(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:420
  public void test0008() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "1/5*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(2*A+3*B-8*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/15*(2*A+3*B+7*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:440
  public void test0009() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/35*(7*B+C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+2/105*a*(35*A+49*B+27*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*(35*A-14*B+18*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/7*C*Cos[c+d*x]^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:456
  public void test0010() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/15015*a*(10439*A+9230*B+8368*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/143*a*(13*B+5*C)*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/13*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/6435*a^3*(10439*A+9230*B+8368*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/9009*a^3*(2717*A+2522*B+2224*C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-4/45045*a^2*(10439*A+9230*B+8368*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/1287*a^2*(143*A+182*B+136*C)*Cos[c+d*x]^3*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:474
  public void test0011() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^(1/2), x]", //
        "1/4*(7*A-4*B+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-(A-B+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-1/4*(A-4*B)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*A*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:490
  public void test0012() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-(5*A-2*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)+1/16*(115*A-43*B+3*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(15*A-7*B-C)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+1/16*(35*A-11*B+3*C)*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:510
  public void test0013() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "4/15*a^2*(9*A+8*B+7*C)*EllipticE[1/2*(c+d*x),2]/d+4/231*a^2*(66*A+55*B+50*C)*EllipticF[1/2*(c+d*x),2]/d+4/45*a^2*(9*A+8*B+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/693*a^2*(99*A+121*B+89*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/11*C*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+2/99*(11*B+4*C)*Cos[c+d*x]^(5/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+4/231*a^2*(66*A+55*B+50*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:544
  public void test0014() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "-1/10*(A+9*B-49*C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(A+3*B-13*C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A-B+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(2*A+3*B-8*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/6*(A+3*B-13*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:564
  public void test0015() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "1/4*a^(3/2)*(8*A+12*B+7*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-1/4*a^2*(8*A-4*B-5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])-1/2*a*(4*A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:582
  public void test0016() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[a+a*Cos[c+d*x]], x]", //
        "-1/8*(8*A-14*B+9*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A-B+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/12*(6*B-C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/8*(8*A-2*B+7*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:598
  public void test0017() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "1/16*(19*A+5*B+3*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(9*A-B-7*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:656
  public void test0018() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+b*Cos[c+d*x]), x]", //
        "-A*b*ArcTanh[Sin[c+d*x]]/(a^2*d)+2*(A*b^2+a^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^2*d*Sqrt[a-b]*Sqrt[a+b])+A*Tan[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:688
  public void test0019() {
    check( //
        "Integrate[(1-Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+b*Cos[c+d*x]), x]", //
        "-1/2*(a^2-2*b^2)*ArcTanh[Sin[c+d*x]]/(a^3*d)+2*b*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/(a^3*d)-b*Tan[c+d*x]/(a^2*d)+1/2*Sec[c+d*x]*Tan[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:704
  public void test0020() {
    check( //
        "Integrate[(1-Cos[c+d*x]^2)*Sec[c+d*x]/(a+b*Cos[c+d*x])^3, x]", //
        "-b*(3*a^2-2*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^3*(a-b)^(3/2)*(a+b)^(3/2)*d)+ArcTanh[Sin[c+d*x]]/(a^3*d)-1/2*Sin[c+d*x]/(a*d*(a+b*Cos[c+d*x])^2)-1/2*(a^2-2*b^2)*Sin[c+d*x]/(a^2*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:765
  public void test0021() {
    check( //
        "Integrate[(a^2-b^2*Cos[c+d*x]^2)/Sqrt[a+b*Cos[c+d*x]], x]", //
        "-2/3*b*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+4/3*a*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/3*(a^2-b^2)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:944
  public void test0022() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(3/2)*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/35*(7*b*B-2*a*C)*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/7*C*(a+b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b*d)+2/105*(21*a*b*B-6*a^2*C+25*b^2*C)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)+2/105*(21*a^2*b*B+63*b^3*B-6*a^3*C+82*a*b^2*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/105*(a^2-b^2)*(21*a*b*B-6*a^2*C+25*b^2*C)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:982
  public void test0023() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/15*(9*a*B+7*b*C)*EllipticE[1/2*(c+d*x),2]/d+10/21*(b*B+a*C)*EllipticF[1/2*(c+d*x),2]/d+2/45*(9*a*B+7*b*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*(b*B+a*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*b*C*Cos[c+d*x]^(7/2)*Sin[c+d*x]/d+10/21*(b*B+a*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:998
  public void test0024() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "2/15*(15*a^3*B+27*a*b^2*B+27*a^2*b*C+7*b^3*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*(21*a^2*b*B+5*b^3*B+7*a^3*C+15*a*b^2*C)*EllipticF[1/2*(c+d*x),2]/d+2/45*b*(27*a*b*B+22*a^2*C+7*b^2*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/63*b^2*(9*b*B+13*a*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*b*C*Cos[c+d*x]^(3/2)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+2/21*(21*a^2*b*B+5*b^3*B+7*a^3*C+15*a*b^2*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1156
  public void test0025() {
    check( //
        "Integrate[(a*b*B-a^2*C+b^2*B*Cos[c+d*x]+b^2*C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^3, x]", //
        "2*(a*b*B-a^2*C-b^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2)*d)-b*(b*B-2*a*C)*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1223
  public void test0026() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "2*(b*B-a*(A-C))*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*A*b+3*a*B+b*C)*EllipticF[1/2*(c+d*x),2]/d+2*a*A*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])+2/3*b*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.8 (a+b cos)^m (c+d trig)^n.input:49
  public void test0027() {
    check( //
        "Integrate[(A+B*Cos[d+e*x]+C*Sin[d+e*x])/(a+b*Cos[d+e*x]), x]", //
        "B*x/b-C*Log[a+b*Cos[d+e*x]]/(b*e)+2*(A*b-a*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(d+e*x)]/Sqrt[a+b]]/(b*e*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:45
  public void test0028() {
    check( //
        "Integrate[(b*Tan[c+d*x]^2)^(1/2), x]", //
        "-Cot[c+d*x]*Log[Cos[c+d*x]]*Sqrt[b*Tan[c+d*x]^2]/d");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:73
  public void test0029() {
    check( //
        "Integrate[(b*Tan[c+d*x]^p)^(1/p), x]", //
        "-Cot[c+d*x]*Log[Cos[c+d*x]]*(b*Tan[c+d*x]^p)^(1/p)/d");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:102
  public void test0030() {
    check( //
        "Integrate[Sin[a+b*x]^3*(d*Tan[a+b*x])^(3/2), x]", //
        "-7/2*d^2*EllipticE[-1/4*Pi+a+b*x,2]*Sin[a+b*x]/(b*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]])+2*d*Sin[a+b*x]^3*Sqrt[d*Tan[a+b*x]]/b+7/3*d^3*Sin[a+b*x]^3/(b*(d*Tan[a+b*x])^(3/2))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:136
  public void test0031() {
    check( //
        "Integrate[Csc[a+b*x]/(d*Tan[a+b*x])^(3/2), x]", //
        "-2/3*Csc[a+b*x]/(b*d*Sqrt[d*Tan[a+b*x]])-1/3*EllipticF[-1/4*Pi+a+b*x,2]*Csc[a+b*x]*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]]/(b*d^2)");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:227
  public void test0032() {
    check( //
        "Integrate[Csc[e+f*x]^4*(b*Tan[e+f*x])^n, x]", //
        "-b^3*(b*Tan[e+f*x])^(-3+n)/(f*(3-n))-b*(b*Tan[e+f*x])^(-1+n)/(f*(1-n))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:314
  public void test0033() {
    check( //
        "Integrate[Sec[a+b*x]^5*(d*Tan[a+b*x])^(3/2), x]", //
        "-4/77*d^2*EllipticF[-1/4*Pi+a+b*x,2]*Sec[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Tan[a+b*x]])-4/77*d*Sec[a+b*x]*Sqrt[d*Tan[a+b*x]]/b-2/77*d*Sec[a+b*x]^3*Sqrt[d*Tan[a+b*x]]/b+2/11*d*Sec[a+b*x]^5*Sqrt[d*Tan[a+b*x]]/b");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:332
  public void test0034() {
    check( //
        "Integrate[Cos[e+f*x]^3/Sqrt[d*Tan[e+f*x]], x]", //
        "5/12*EllipticF[-1/4*Pi+e+f*x,2]*Sec[e+f*x]*Sqrt[Sin[2*e+2*f*x]]/(f*Sqrt[d*Tan[e+f*x]])+5/6*Cos[e+f*x]*Sqrt[d*Tan[e+f*x]]/(d*f)+1/3*Cos[e+f*x]^3*Sqrt[d*Tan[e+f*x]]/(d*f)");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:478
  public void test0035() {
    check( //
        "Integrate[Cot[e+f*x]^3*(b*Csc[e+f*x])^m, x]", //
        "(b*Csc[e+f*x])^m/(f*m)-(b*Csc[e+f*x])^(2+m)/(b^2*f*(2+m))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:27
  public void test0036() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+I*a*Tan[c+d*x]), x]", //
        "-1/3*I*a*Cos[c+d*x]^3/d+a*Sin[c+d*x]/d-1/3*a*Sin[c+d*x]^3/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:75
  public void test0037() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+I*a*Tan[c+d*x])^5, x]", //
        "-12*a^5*x+12*I*a^5*Log[Cos[c+d*x]]/d+5*a^5*Tan[c+d*x]/d+1/2*I*a^5*Tan[c+d*x]^2/d-8*I*a^6/(d*(a-I*a*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:107
  public void test0038() {
    check( //
        "Integrate[Cos[c+d*x]^11*(a+I*a*Tan[c+d*x])^8, x]", //
        "-2/1155*I*a^3*Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^5/d-2/231*I*a^2*Cos[c+d*x]^7*(a+I*a*Tan[c+d*x])^6/d-1/33*I*a*Cos[c+d*x]^9*(a+I*a*Tan[c+d*x])^7/d-1/11*I*Cos[c+d*x]^11*(a+I*a*Tan[c+d*x])^8/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:125
  public void test0039() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+I*a*Tan[c+d*x]), x]", //
        "4/5*Sin[c+d*x]/(a*d)-4/15*Sin[c+d*x]^3/(a*d)+1/5*I*Cos[c+d*x]^3/(d*(a+I*a*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:141
  public void test0040() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+I*a*Tan[c+d*x])^2, x]", //
        "5/7*Sin[c+d*x]/(a^2*d)-10/21*Sin[c+d*x]^3/(a^2*d)+1/7*Sin[c+d*x]^5/(a^2*d)+2/7*I*Cos[c+d*x]^5/(d*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:157
  public void test0041() {
    check( //
        "Integrate[Sec[c+d*x]/(a+I*a*Tan[c+d*x])^3, x]", //
        "1/5*I*Sec[c+d*x]/(d*(a+I*a*Tan[c+d*x])^3)+2/15*I*Sec[c+d*x]/(a*d*(a+I*a*Tan[c+d*x])^2)+2/15*I*Sec[c+d*x]/(d*(a^3+I*a^3*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:173
  public void test0042() {
    check( //
        "Integrate[Sec[c+d*x]^5/(a+I*a*Tan[c+d*x])^4, x]", //
        "ArcTanh[Sin[c+d*x]]/(a^4*d)+2/3*I*Sec[c+d*x]^3/(a*d*(a+I*a*Tan[c+d*x])^3)-2*I*Sec[c+d*x]/(d*(a^4+I*a^4*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:189
  public void test0043() {
    check( //
        "Integrate[Sec[c+d*x]^13/(a+I*a*Tan[c+d*x])^8, x]", //
        "1155/8*ArcTanh[Sin[c+d*x]]/(a^8*d)+1155/8*Sec[c+d*x]*Tan[c+d*x]/(a^8*d)+385/4*Sec[c+d*x]^3*Tan[c+d*x]/(a^8*d)+2/3*I*Sec[c+d*x]^11/(a*d*(a+I*a*Tan[c+d*x])^7)-22/3*I*Sec[c+d*x]^9/(a^3*d*(a+I*a*Tan[c+d*x])^5)-66*I*Sec[c+d*x]^7/(a^2*d*(a^2+I*a^2*Tan[c+d*x])^3)-154*I*Sec[c+d*x]^5/(d*(a^8+I*a^8*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:209
  public void test0044() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])/(e*Sec[c+d*x])^(7/2), x]", //
        "-2/7*I*a/(d*(e*Sec[c+d*x])^(7/2))+2/7*a*Sin[c+d*x]/(d*e*(e*Sec[c+d*x])^(5/2))+10/21*a*Sin[c+d*x]/(d*e^3*Sqrt[e*Sec[c+d*x]])+10/21*a*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(d*e^4)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:226
  public void test0045() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^3/(e*Sec[c+d*x])^(9/2), x]", //
        "2/15*a^3*EllipticE[1/2*(c+d*x),2]/(d*e^4*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])-2/9*I*(a+I*a*Tan[c+d*x])^3/(d*(e*Sec[c+d*x])^(9/2))-4/15*I*(a^3+I*a^3*Tan[c+d*x])/(d*e^2*(e*Sec[c+d*x])^(5/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:244
  public void test0046() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(7/2)/(a+I*a*Tan[c+d*x]), x]", //
        "-2/3*I*e^2*(e*Sec[c+d*x])^(3/2)/(a*d)-2*e^4*EllipticE[1/2*(c+d*x),2]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])+2*e^3*Sin[c+d*x]*Sqrt[e*Sec[c+d*x]]/(a*d)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:260
  public void test0047() {
    check( //
        "Integrate[1/((e*Sec[c+d*x])^(1/2)*(a+I*a*Tan[c+d*x])^2), x]", //
        "2/9*e*Sin[c+d*x]/(a^2*d*(e*Sec[c+d*x])^(3/2))+2/3*EllipticE[1/2*(c+d*x),2]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])+4/9*I*e^2/(d*(e*Sec[c+d*x])^(5/2)*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:276
  public void test0048() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(11/2)/(a+I*a*Tan[c+d*x])^4, x]", //
        "-42/5*e^6*EllipticE[1/2*(c+d*x),2]/(a^4*d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])+42/5*e^5*Sin[c+d*x]*Sqrt[e*Sec[c+d*x]]/(a^4*d)+4/5*I*e^2*(e*Sec[c+d*x])^(7/2)/(a*d*(a+I*a*Tan[c+d*x])^3)-28/5*I*e^4*(e*Sec[c+d*x])^(3/2)/(d*(a^4+I*a^4*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:334
  public void test0049() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "7/16*I*a^(3/2)*ArcTanh[Sec[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Tan[c+d*x]])]/(d*Sqrt[2])+7/24*I*a^2*Cos[c+d*x]/(d*Sqrt[a+I*a*Tan[c+d*x]])-7/16*I*a*Cos[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]]/d-7/30*I*a*Cos[c+d*x]^3*Sqrt[a+I*a*Tan[c+d*x]]/d-1/5*I*Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^(3/2)/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:400
  public void test0050() {
    check( //
        "Integrate[Sec[c+d*x]^13/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "256/20995*I*a^4*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(13/2))+64/1615*I*a^3*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(11/2))+24/323*I*a^2*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(9/2))+2/19*I*a*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(7/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:416
  public void test0051() {
    check( //
        "Integrate[Sec[c+d*x]^13/(a+I*a*Tan[c+d*x])^(7/2), x]", //
        "64/3315*I*a^3*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(13/2))+16/255*I*a^2*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(11/2))+2/17*I*a*Sec[c+d*x]^13/(d*(a+I*a*Tan[c+d*x])^(9/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:436
  public void test0052() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(3/2)*(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "5/4*I*a^2*(e*Sec[c+d*x])^(3/2)/(d*Sqrt[a+I*a*Tan[c+d*x]])-5/4*I*a^(5/2)*e^(3/2)*ArcTan[1-Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+5/4*I*a^(5/2)*e^(3/2)*ArcTan[1+Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+5/8*I*a^(5/2)*e^(3/2)*Log[a-Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])-5/8*I*a^(5/2)*e^(3/2)*Log[a+Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+1/2*I*a*(e*Sec[c+d*x])^(3/2)*Sqrt[a+I*a*Tan[c+d*x]]/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:454
  public void test0053() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(3/2)/Sqrt[a+I*a*Tan[c+d*x]], x]", //
        "I*e^(3/2)*Log[a-Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]*Sqrt[a]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])-I*e^(3/2)*Log[a+Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a-I*a*Tan[c+d*x])]*Sec[c+d*x]*Sqrt[a]/(d*Sqrt[2]*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])-I*e^(3/2)*ArcTan[1-Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]*Sqrt[2]*Sqrt[a]/(d*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])+I*e^(3/2)*ArcTan[1+Sqrt[2]*Sqrt[e]*Sqrt[a-I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sec[c+d*x]*Sqrt[2]*Sqrt[a]/(d*Sqrt[a-I*a*Tan[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]])");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:534
  public void test0054() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(-2-n)*(a+I*a*Tan[c+d*x])^n, x]", //
        "I*(e*Sec[c+d*x])^(-2-n)*(a+I*a*Tan[c+d*x])^n/(d*(2-n))-2*I*(e*Sec[c+d*x])^(-2-n)*(a+I*a*Tan[c+d*x])^(1+n)/(a*d*(2-n)*n)+2*I*(e*Sec[c+d*x])^(-2-n)*(a+I*a*Tan[c+d*x])^(2+n)/(a^2*d*n*(4-n^2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:569
  public void test0055() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Tan[c+d*x]), x]", //
        "1/2*a*x-1/2*b*Cos[c+d*x]^2/d+1/2*a*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:648
  public void test0056() {
    check( //
        "Integrate[(d*Sec[e+f*x])^(3/2)*(a+b*Tan[e+f*x])^2, x]", //
        "14/15*a*b*(d*Sec[e+f*x])^(3/2)/f-2/5*(5*a^2-2*b^2)*d^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[d*Sec[e+f*x]])+2/5*(5*a^2-2*b^2)*d*Sin[e+f*x]*Sqrt[d*Sec[e+f*x]]/f+2/5*b*(d*Sec[e+f*x])^(3/2)*(a+b*Tan[e+f*x])/f");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:719
  public void test0057() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+b*Tan[c+d*x])^n, x]", //
        "(a^2+b^2)^2*(a+b*Tan[c+d*x])^(1+n)/(b^5*d*(1+n))-4*a*(a^2+b^2)*(a+b*Tan[c+d*x])^(2+n)/(b^5*d*(2+n))+2*(3*a^2+b^2)*(a+b*Tan[c+d*x])^(3+n)/(b^5*d*(3+n))-4*a*(a+b*Tan[c+d*x])^(4+n)/(b^5*d*(4+n))+(a+b*Tan[c+d*x])^(5+n)/(b^5*d*(5+n))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:754
  public void test0058() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(9/2)*(a+I*a*Tan[c+d*x])^2), x]", //
        "10/3*EllipticF[1/2*(c+d*x),2]*Cos[c+d*x]^(9/2)/(a^2*d*(e*Cos[c+d*x])^(9/2))+10/3*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d*(e*Cos[c+d*x])^(9/2))-4*I*Cos[c+d*x]^2/(d*(e*Cos[c+d*x])^(9/2)*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:15
  public void test0059() {
    check( //
        "Integrate[Sin[x]^3/(I+Tan[x]), x]", //
        "1/3*I*Cos[x]^3-1/5*I*Cos[x]^5+1/5*Sin[x]^5");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:15
  public void test0060() {
    check( //
        "Integrate[x^3*Tan[a+b*x]^2, x]", //
        "-I*x^3/b-1/4*x^4+3*x^2*Log[1+E^(2*I*(a+b*x))]/b^2-3*I*x*PolyLog[2,-E^(2*I*(a+b*x))]/b^3+3/2*PolyLog[3,-E^(2*I*(a+b*x))]/b^4+x^3*Tan[a+b*x]/b");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:45
  public void test0061() {
    check( //
        "Integrate[(c+d*x)/(a+I*a*Tan[e+f*x])^2, x]", //
        "-3/16*I*d*x/(a^2*f)-1/8*d*x^2/a^2+1/4*x*(c+d*x)/a^2+1/16*d/(f^2*(a+I*a*Tan[e+f*x])^2)+1/4*I*(c+d*x)/(f*(a+I*a*Tan[e+f*x])^2)+3/16*d/(f^2*(a^2+I*a^2*Tan[e+f*x]))+1/4*I*(c+d*x)/(f*(a^2+I*a^2*Tan[e+f*x]))");
  }

  // 4.3.11 (e x)^m (a+b tan(c+d x^n))^p.input:30
  public void test0062() {
    check( //
        "Integrate[x^3/(a+b*Tan[c+d*x^2])^2, x]", //
        "-1/4*x^4/(a^2+b^2)+1/8*(b+2*a*d*x^2)^2/(a*(a+I*b)*(a^2+b^2)*d^2)+1/2*b*(b+2*a*d*x^2)*Log[1+E^(2*I*(c+d*x^2))*(a^2+b^2)/(a+I*b)^2]/((a^2+b^2)^2*d^2)-1/2*I*a*b*PolyLog[2,-E^(2*I*(c+d*x^2))*(a^2+b^2)/(a+I*b)^2]/((a^2+b^2)^2*d^2)-1/2*b*x^2/((a^2+b^2)*d*(a+b*Tan[c+d*x^2]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:32
  public void test0063() {
    check( //
        "Integrate[Cot[c+d*x]^4*(a+I*a*Tan[c+d*x])^2, x]", //
        "2*a^2*x+2*a^2*Cot[c+d*x]/d-I*a^2*Cot[c+d*x]^2/d-1/3*a^2*Cot[c+d*x]^3/d-2*I*a^2*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:48
  public void test0064() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^4, x]", //
        "8*a^4*x-8*I*a^4*Log[Cos[c+d*x]]/d-4*a^4*Tan[c+d*x]/d+1/3*I*a*(a+I*a*Tan[c+d*x])^3/d+I*(a^2+I*a^2*Tan[c+d*x])^2/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:66
  public void test0065() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+I*a*Tan[c+d*x]), x]", //
        "-3/2*x/a-3/2*Cot[c+d*x]/(a*d)-I*Log[Sin[c+d*x]]/(a*d)+1/2*Cot[c+d*x]/(d*(a+I*a*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:82
  public void test0066() {
    check( //
        "Integrate[Tan[c+d*x]^3/(a+I*a*Tan[c+d*x])^3, x]", //
        "1/8*I*x/a^3+3/8/(a^3*d*(1+I*Tan[c+d*x]))+1/6*I*Tan[c+d*x]^3/(d*(a+I*a*Tan[c+d*x])^3)+(-1/8)/(a*d*(a+I*a*Tan[c+d*x])^2)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:102
  public void test0067() {
    check( //
        "Integrate[Sqrt[a+I*a*Tan[c+d*x]]*Tan[c+d*x]^3, x]", //
        "ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]*Sqrt[a]/d-8/5*Sqrt[a+I*a*Tan[c+d*x]]/d+2/5*Sqrt[a+I*a*Tan[c+d*x]]*Tan[c+d*x]^2/d-2/15*(a+I*a*Tan[c+d*x])^(3/2)/(a*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:118
  public void test0068() {
    check( //
        "Integrate[Tan[c+d*x]*(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "-4*a^(5/2)*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]/d+4*a^2*Sqrt[a+I*a*Tan[c+d*x]]/d+2/3*a*(a+I*a*Tan[c+d*x])^(3/2)/d+2/5*(a+I*a*Tan[c+d*x])^(5/2)/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:136
  public void test0069() {
    check( //
        "Integrate[Tan[c+d*x]^5/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "-1/2*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(3/2)*d*Sqrt[2])+78/5*Sqrt[a+I*a*Tan[c+d*x]]/(a^2*d)-39/10*Sqrt[a+I*a*Tan[c+d*x]]*Tan[c+d*x]^2/(a^2*d)+19/6*I*Tan[c+d*x]^3/(a*d*Sqrt[a+I*a*Tan[c+d*x]])-1/3*Tan[c+d*x]^4/(d*(a+I*a*Tan[c+d*x])^(3/2))-151/30*(a+I*a*Tan[c+d*x])^(3/2)/(a^3*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:152
  public void test0070() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "5*I*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[a]]/(a^(5/2)*d)+1/4*I*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(5/2)*d*Sqrt[2])+41/12*Cot[c+d*x]/(a^2*d*Sqrt[a+I*a*Tan[c+d*x]])-21/4*Cot[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]]/(a^3*d)+1/5*Cot[c+d*x]/(d*(a+I*a*Tan[c+d*x])^(5/2))+19/30*Cot[c+d*x]/(a*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:172
  public void test0071() {
    check( //
        "Integrate[(d*Tan[e+f*x])^(5/2)*(a+I*a*Tan[e+f*x])^2, x]", //
        "-4*(-1)^(3/4)*a^2*d^(5/2)*ArcTan[(-1)^(3/4)*Sqrt[d*Tan[e+f*x]]/Sqrt[d]]/f-4*I*a^2*d^2*Sqrt[d*Tan[e+f*x]]/f+4/3*a^2*d*(d*Tan[e+f*x])^(3/2)/f+4/5*I*a^2*(d*Tan[e+f*x])^(5/2)/f-2/7*a^2*(d*Tan[e+f*x])^(7/2)/(d*f)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:408
  public void test0072() {
    check( //
        "Integrate[(d*Tan[e+f*x])^(7/2)*(a+a*Tan[e+f*x])^3, x]", //
        "-2*a^3*d^(7/2)*ArcTanh[(Sqrt[d]+Sqrt[d]*Tan[e+f*x])/(Sqrt[2]*Sqrt[d*Tan[e+f*x]])]*Sqrt[2]/f+4*a^3*d^3*Sqrt[d*Tan[e+f*x]]/f-4/3*a^3*d^2*(d*Tan[e+f*x])^(3/2)/f-4/5*a^3*d*(d*Tan[e+f*x])^(5/2)/f+4/7*a^3*(d*Tan[e+f*x])^(7/2)/f+16/33*a^3*(d*Tan[e+f*x])^(9/2)/(d*f)+2/11*(d*Tan[e+f*x])^(9/2)*(a^3+a^3*Tan[e+f*x])/(d*f)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:491
  public void test0073() {
    check( //
        "Integrate[Tan[c+d*x]^3*(a+b*Tan[c+d*x]), x]", //
        "b*x+a*Log[Cos[c+d*x]]/d-b*Tan[c+d*x]/d+1/2*a*Tan[c+d*x]^2/d+1/3*b*Tan[c+d*x]^3/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:507
  public void test0074() {
    check( //
        "Integrate[Cot[c+d*x]^2*(a+b*Tan[c+d*x])^2, x]", //
        "-(a^2-b^2)*x-a^2*Cot[c+d*x]/d+2*a*b*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:542
  public void test0075() {
    check( //
        "Integrate[Cot[c+d*x]/(a+b*Tan[c+d*x]), x]", //
        "-b*x/(a^2+b^2)+Log[Sin[c+d*x]]/(a*d)-b^2*Log[a*Cos[c+d*x]+b*Sin[c+d*x]]/(a*(a^2+b^2)*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:559
  public void test0076() {
    check( //
        "Integrate[Tan[c+d*x]^3/(a+b*Tan[c+d*x])^3, x]", //
        "-b*(3*a^2-b^2)*x/(a^2+b^2)^3+a*(a^2-3*b^2)*Log[a*Cos[c+d*x]+b*Sin[c+d*x]]/((a^2+b^2)^3*d)-1/2*a^2*Tan[c+d*x]/(b*(a^2+b^2)*d*(a+b*Tan[c+d*x])^2)-1/2*a^2*(a^2+5*b^2)/(b^2*(a^2+b^2)^2*d*(a+b*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:575
  public void test0077() {
    check( //
        "Integrate[1/(3+5*Tan[c+d*x])^2, x]", //
        "-4/289*x+15/578*Log[3*Cos[c+d*x]+5*Sin[c+d*x]]/d+(-5/34)/(d*(3+5*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:595
  public void test0078() {
    check( //
        "Integrate[Tan[c+d*x]^3*(a+b*Tan[c+d*x])^(3/2), x]", //
        "(a-I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/d+(a+I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/d-2*a*Sqrt[a+b*Tan[c+d*x]]/d-2/3*(a+b*Tan[c+d*x])^(3/2)/d-4/35*a*(a+b*Tan[c+d*x])^(5/2)/(b^2*d)+2/7*Tan[c+d*x]*(a+b*Tan[c+d*x])^(5/2)/(b*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:613
  public void test0079() {
    check( //
        "Integrate[Tan[c+d*x]^5/Sqrt[a+b*Tan[c+d*x]], x]", //
        "-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/(d*Sqrt[a-I*b])-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/(d*Sqrt[a+I*b])-4/105*a*(24*a^2-35*b^2)*Sqrt[a+b*Tan[c+d*x]]/(b^4*d)+2/105*(24*a^2-35*b^2)*Sqrt[a+b*Tan[c+d*x]]*Tan[c+d*x]/(b^3*d)-12/35*a*Sqrt[a+b*Tan[c+d*x]]*Tan[c+d*x]^2/(b^2*d)+2/7*Sqrt[a+b*Tan[c+d*x]]*Tan[c+d*x]^3/(b*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:629
  public void test0080() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+b*Tan[c+d*x])^(3/2), x]", //
        "3*b*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a]]/(a^(5/2)*d)+I*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/((a-I*b)^(3/2)*d)-I*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/((a+I*b)^(3/2)*d)-b*(a^2+3*b^2)/(a^2*(a^2+b^2)*d*Sqrt[a+b*Tan[c+d*x]])-Cot[c+d*x]/(a*d*Sqrt[a+b*Tan[c+d*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:649
  public void test0081() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])/Tan[c+d*x]^(5/2), x]", //
        "(a+b)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-(a+b)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])+1/2*(a-b)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])-1/2*(a-b)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])-2*b/(d*Sqrt[Tan[c+d*x]])-2/3*a/(d*Tan[c+d*x]^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:665
  public void test0082() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])^3/Tan[c+d*x]^(9/2), x]", //
        "-(a-b)*(a^2+4*a*b+b^2)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])+(a-b)*(a^2+4*a*b+b^2)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-1/2*(a+b)*(a^2-4*a*b+b^2)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])+1/2*(a+b)*(a^2-4*a*b+b^2)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])+2*b*(3*a^2-b^2)/(d*Sqrt[Tan[c+d*x]])-32/35*a^2*b/(d*Tan[c+d*x]^(5/2))+2/3*a*(a^2-3*b^2)/(d*Tan[c+d*x]^(3/2))-2/7*a^2*(a+b*Tan[c+d*x])/(d*Tan[c+d*x]^(7/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:753
  public void test0083() {
    check( //
        "Integrate[1/(Sqrt[Tan[c+d*x]]*Sqrt[-2+3*Tan[c+d*x]]), x]", //
        "ArcTanh[Sqrt[3-2*I]*Sqrt[Tan[c+d*x]]/Sqrt[-2+3*Tan[c+d*x]]]/(d*Sqrt[3-2*I])+ArcTanh[Sqrt[3+2*I]*Sqrt[Tan[c+d*x]]/Sqrt[-2+3*Tan[c+d*x]]]/(d*Sqrt[3+2*I])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:954
  public void test0084() {
    check( //
        "Integrate[Cot[c+d*x]^(5/2)*(a+b*Tan[c+d*x])^2, x]", //
        "-2/3*a^2*Cot[c+d*x]^(3/2)/d-(a^2+2*a*b-b^2)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+(a^2+2*a*b-b^2)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+1/2*(a^2-2*a*b-b^2)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-1/2*(a^2-2*a*b-b^2)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-4*a*b*Sqrt[Cot[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:972
  public void test0085() {
    check( //
        "Integrate[1/(Sqrt[Cot[c+d*x]]*(a+b*Tan[c+d*x])), x]", //
        "(a+b)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])-(a+b)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+1/2*(a-b)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])-1/2*(a-b)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+2*ArcTan[Sqrt[a]*Sqrt[Cot[c+d*x]]/Sqrt[b]]*Sqrt[a]*Sqrt[b]/((a^2+b^2)*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:95
  public void test0086() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[a+b*x]^3, x]", //
        "1/4*Sin[a+b*x]^4/b-1/6*Sin[a+b*x]^6/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:127
  public void test0087() {
    check( //
        "Integrate[Cos[a+b*x]^5*Sin[a+b*x]^5, x]", //
        "1/6*Sin[a+b*x]^6/b-1/4*Sin[a+b*x]^8/b+1/10*Sin[a+b*x]^10/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:143
  public void test0088() {
    check( //
        "Integrate[Sec[a+b*x]^12*Sin[a+b*x]^5, x]", //
        "1/7*Sec[a+b*x]^7/b-2/9*Sec[a+b*x]^9/b+1/11*Sec[a+b*x]^11/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:161
  public void test0089() {
    check( //
        "Integrate[Sec[a+b*x]^7/Sin[a+b*x], x]", //
        "Log[Tan[a+b*x]]/b+3/2*Tan[a+b*x]^2/b+3/4*Tan[a+b*x]^4/b+1/6*Tan[a+b*x]^6/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:193
  public void test0090() {
    check( //
        "Integrate[Cos[a+b*x]^2/Sin[a+b*x]^4, x]", //
        "-1/3*Cot[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:229
  public void test0091() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]^2, x]", //
        "4/45*d*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/b-2/9*(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]/(b*d)+4/15*d^2*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:245
  public void test0092() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]^4, x]", //
        "8/385*d*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]/b-4/55*(d*Cos[a+b*x])^(9/2)*Sin[a+b*x]/(b*d)-2/15*(d*Cos[a+b*x])^(9/2)*Sin[a+b*x]^3/(b*d)+8/231*d^4*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])+8/231*d^3*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:307
  public void test0093() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(3/2), x]", //
        "2*(c*Sin[a+b*x])^(3/2)/(b*c*d*Sqrt[d*Cos[a+b*x]])-2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d^2*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:323
  public void test0094() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(9/2)*(c*Sin[a+b*x])^(5/2), x]", //
        "1/20*c*d^3*(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2)/b+3/70*c*d*(d*Cos[a+b*x])^(7/2)*(c*Sin[a+b*x])^(3/2)/b-1/7*c*(d*Cos[a+b*x])^(11/2)*(c*Sin[a+b*x])^(3/2)/(b*d)+3/40*c^2*d^4*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:341
  public void test0095() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(3/2)/(c*Sin[a+b*x])^(1/2), x]", //
        "d*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*c)+1/2*d^2*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:476
  public void test0096() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(5/2)*Sin[e+f*x]^4, x]", //
        "2/3*b*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^3/f+4/3*b^3*Sin[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])-8/3*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:494
  public void test0097() {
    check( //
        "Integrate[Csc[e+f*x]^2/Sqrt[b*Sec[e+f*x]], x]", //
        "-b*Csc[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:548
  public void test0098() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(9/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2/7*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(7/2))-8/21*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(3/2))");
  }

  // 4.1.1.1 (a+b sin)^n.input:12
  public void test0099() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(7/2), x]", //
        "-24/35*a^2*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/7*a*Cos[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/d-256/35*a^4*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-64/35*a^3*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:13
  public void test0100() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x]), x]", //
        "5/16*a*x-1/7*a*Cos[c+d*x]^7/d+5/16*a*Cos[c+d*x]*Sin[c+d*x]/d+5/24*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*a*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:30
  public void test0101() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sin[c+d*x])^2, x]", //
        "-2*a^2*Log[1-Sin[c+d*x]]/d-a^2*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:46
  public void test0102() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*Log[1-Sin[c+d*x]]/d+2*a^4/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:64
  public void test0103() {
    check( //
        "Integrate[Cos[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "3/8*x/a+1/5*Cos[c+d*x]^5/(a*d)+3/8*Cos[c+d*x]*Sin[c+d*x]/(a*d)+1/4*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:80
  public void test0104() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "2*Log[1+Sin[c+d*x]]/(a^2*d)-Sin[c+d*x]/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:112
  public void test0105() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x])^8, x]", //
        "-1/19*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^8)-11/323*Sec[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^7)-22/969*Sec[c+d*x]^3/(a^2*d*(a+a*Sin[c+d*x])^6)-66/4199*Sec[c+d*x]^3/(a^3*d*(a+a*Sin[c+d*x])^5)-48/4199*Sec[c+d*x]^3/(d*(a^2+a^2*Sin[c+d*x])^4)-112/12597*Sec[c+d*x]^3/(a^2*d*(a^2+a^2*Sin[c+d*x])^3)-32/4199*Sec[c+d*x]^3/(d*(a^4+a^4*Sin[c+d*x])^2)-32/4199*Sec[c+d*x]^3/(d*(a^8+a^8*Sin[c+d*x]))+128/4199*Tan[c+d*x]/(a^8*d)+128/12597*Tan[c+d*x]^3/(a^8*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:132
  public void test0106() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-4096/45045*a^5*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(7/2))-1024/6435*a^4*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(5/2))-128/715*a^3*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(3/2))-32/195*a^2*Cos[c+d*x]^7/(d*Sqrt[a+a*Sin[c+d*x]])-2/15*a*Cos[c+d*x]^7*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:164
  public void test0107() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-16/3*a^2*Sec[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/3*a*Sec[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/d+64/3*a^3*Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:198
  public void test0108() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-105/256*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-7/32*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-1/6*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-105/256*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+35/64*Sec[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])+1/4*Sec[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:214
  public void test0109() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-1/8*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(5/2))-1155/4096*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-77/512*Sec[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-11/96*Sec[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^(3/2))-1155/4096*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+385/1024*Sec[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+11/64*Sec[c+d*x]^3/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:234
  public void test0110() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*a^4*(e*Cos[c+d*x])^(3/2)/(d*e^5*(a-a*Sin[c+d*x])^2)+2/5*a^4*(e*Cos[c+d*x])^(3/2)/(d*e^5*(a^2-a^2*Sin[c+d*x]))-2/5*a^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:250
  public void test0111() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(3/2), x]", //
        "-154/15*a^4*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*e^3)+4*a^7*(e*Cos[c+d*x])^(11/2)/(d*e^7*(a-a*Sin[c+d*x])^3)+44/3*a^8*(e*Cos[c+d*x])^(7/2)/(d*e^5*(a^4-a^4*Sin[c+d*x]))-154/5*a^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:268
  public void test0112() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(11/2)/(a+a*Sin[c+d*x])^2, x]", //
        "18/35*e^3*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a^2*d)+4/5*e*(e*Cos[c+d*x])^(9/2)/(d*(a^2+a^2*Sin[c+d*x]))+6/7*e^6*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^2*d*Sqrt[e*Cos[c+d*x]])+6/7*e^5*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:284
  public void test0113() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/(a+a*Sin[c+d*x])^3, x]", //
        "-2/21*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[e*Cos[c+d*x]])-4/7*e*Sqrt[e*Cos[c+d*x]]/(a*d*(a+a*Sin[c+d*x])^2)+2/21*e*Sqrt[e*Cos[c+d*x]]/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:304
  public void test0114() {
    check( //
        "Integrate[Sqrt[a+a*Sin[c+d*x]]/Sqrt[e*Cos[c+d*x]], x]", //
        "-2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])+2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:320
  public void test0115() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)/Sqrt[e*Cos[c+d*x]], x]", //
        "-1/2*a*(a+a*Sin[c+d*x])^(3/2)*Sqrt[e*Cos[c+d*x]]/(d*e)-7/4*a^2*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e)-21/4*a^2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])+21/4*a^2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*(1+Cos[c+d*x]+Sin[c+d*x])*Sqrt[e])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:338
  public void test0116() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*(e*Cos[c+d*x])^(5/2)/(d*e*(a+a*Sin[c+d*x])^(3/2))-2*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)+2*e^(3/2)*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*(1+Cos[c+d*x]+Sin[c+d*x]))-2*e^(3/2)*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:382
  public void test0117() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^m, x]", //
        "2*(a+a*Sin[c+d*x])^(2+m)/(a^2*d*(2+m))-(a+a*Sin[c+d*x])^(3+m)/(a^3*d*(3+m))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:427
  public void test0118() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+b*Sin[c+d*x]), x]", //
        "b*Sec[c+d*x]/d+a*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:443
  public void test0119() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+b*Sin[c+d*x])^3, x]", //
        "1/4*(a^2-b^2)^2*(a+b*Sin[c+d*x])^4/(b^5*d)-4/5*a*(a^2-b^2)*(a+b*Sin[c+d*x])^5/(b^5*d)+1/3*(3*a^2-b^2)*(a+b*Sin[c+d*x])^6/(b^5*d)-4/7*a*(a+b*Sin[c+d*x])^7/(b^5*d)+1/8*(a+b*Sin[c+d*x])^8/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:477
  public void test0120() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "-1/2*a*(2*a^2-3*b^2)*x/b^4+2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^4*d)+1/3*Cos[c+d*x]^3/(b*d)-1/2*Cos[c+d*x]*(2*(a^2-b^2)-a*b*Sin[c+d*x])/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:493
  public void test0121() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Sin[c+d*x])^2, x]", //
        "10*a*b^4*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(7/2)*d)+b*Sec[c+d*x]^3/((a^2-b^2)*d*(a+b*Sin[c+d*x]))-1/3*Sec[c+d*x]^3*(5*a*b-(a^2+4*b^2)*Sin[c+d*x])/((a^2-b^2)^2*d)+1/3*Sec[c+d*x]*(15*a*b^3+(2*a^4-9*a^2*b^2-8*b^4)*Sin[c+d*x])/((a^2-b^2)^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:579
  public void test0122() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Sin[c+d*x])^(5/2), x]", //
        "2/3*(a^2-b^2)/(b^3*d*(a+b*Sin[c+d*x])^(3/2))-4*a/(b^3*d*Sqrt[a+b*Sin[c+d*x]])-2*Sqrt[a+b*Sin[c+d*x]]/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:599
  public void test0123() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])/(e*Cos[c+d*x])^(3/2), x]", //
        "2*b/(d*e*Sqrt[e*Cos[c+d*x]])+2*a*Sin[c+d*x]/(d*e*Sqrt[e*Cos[c+d*x]])-2*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:615
  public void test0124() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3/(e*Cos[c+d*x])^(3/2), x]", //
        "2/3*b*(3*a^2+4*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e^3)+2*a*b*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e^3)+2*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/(d*e*Sqrt[e*Cos[c+d*x]])-2*a*(a^2+6*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:755
  public void test0125() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x])^m, x]", //
        "-(a^2-b^2)*(a+b*Sin[c+d*x])^(1+m)/(b^3*d*(1+m))+2*a*(a+b*Sin[c+d*x])^(2+m)/(b^3*d*(2+m))-(a+b*Sin[c+d*x])^(3+m)/(b^3*d*(3+m))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:26
  public void test0126() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2*Tan[c+d*x]^3, x]", //
        "3*a^2*Log[1-Sin[c+d*x]]/d+2*a^2*Sin[c+d*x]/d+1/2*a^2*Sin[c+d*x]^2/d+a^3/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:61
  public void test0127() {
    check( //
        "Integrate[Cot[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "Log[Sin[c+d*x]]/(a*d)-Log[1+Sin[c+d*x]]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:93
  public void test0128() {
    check( //
        "Integrate[Cot[c+d*x]^11/(a+a*Sin[c+d*x])^3, x]", //
        "1/3*Csc[c+d*x]^3/(a^3*d)-3/4*Csc[c+d*x]^4/(a^3*d)+1/5*Csc[c+d*x]^5/(a^3*d)+5/6*Csc[c+d*x]^6/(a^3*d)-5/7*Csc[c+d*x]^7/(a^3*d)-1/8*Csc[c+d*x]^8/(a^3*d)+1/3*Csc[c+d*x]^9/(a^3*d)-1/10*Csc[c+d*x]^10/(a^3*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:115
  public void test0129() {
    check( //
        "Integrate[Cot[e+f*x]^4*(a+a*Sin[e+f*x])^(3/2), x]", //
        "37/8*a^(3/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/f-1/3*Cot[e+f*x]*Csc[e+f*x]^2*(a+a*Sin[e+f*x])^(3/2)/f-8/3*a^2*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+29/24*a^2*Cot[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/3*a*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f-1/4*a*Cot[e+f*x]*Csc[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:210
  public void test0130() {
    check( //
        "Integrate[Cot[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "Log[Sin[c+d*x]]/(a*d)-Log[a+b*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:226
  public void test0131() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+b*Sin[c+d*x])^2, x]", //
        "2*b*ArcTanh[Cos[c+d*x]]/(a^3*d)-2*Cot[c+d*x]/(a^2*d)+Cot[c+d*x]/(a*d*(a+b*Sin[c+d*x]))-2*(a^2-2*b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^3*d*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:14
  public void test0132() {
    check( //
        "Integrate[(c+d*x)^2*Sin[a+b*x], x]", //
        "2*d^2*Cos[a+b*x]/b^3-(c+d*x)^2*Cos[a+b*x]/b+2*d*(c+d*x)*Sin[a+b*x]/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:30
  public void test0133() {
    check( //
        "Integrate[(c+d*x)*Sin[a+b*x]^3, x]", //
        "-2/3*(c+d*x)*Cos[a+b*x]/b+2/3*d*Sin[a+b*x]/b^2-1/3*(c+d*x)*Cos[a+b*x]*Sin[a+b*x]^2/b+1/9*d*Sin[a+b*x]^3/b^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:217
  public void test0134() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])/(c+d*x)^3, x]", //
        "-1/2*a/(d*(c+d*x)^2)-1/2*b*f*Cos[e+f*x]/(d^2*(c+d*x))-1/2*b*f^2*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d^3-1/2*b*f^2*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d^3-1/2*b*Sin[e+f*x]/(d*(c+d*x)^2)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:41
  public void test0135() {
    check( //
        "Integrate[Sin[c+d*x]/(a+b*x)^2, x]", //
        "d*CosIntegral[a*d/b+d*x]*Cos[c-a*d/b]/b^2-d*SinIntegral[a*d/b+d*x]*Sin[c-a*d/b]/b^2-Sin[c+d*x]/(b*(a+b*x))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:83
  public void test0136() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^3])/x^4, x]", //
        "-1/3*a/x^3+1/3*b*d*CosIntegral[d*x^3]*Cos[c]-1/3*b*d*SinIntegral[d*x^3]*Sin[c]-1/3*b*Sin[c+d*x^3]/x^3");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:162
  public void test0137() {
    check( //
        "Integrate[Sin[a+b/x^2]/x^4, x]", //
        "1/2*Cos[a+b/x^2]/(b*x)-1/2*Cos[a]*FresnelC[Sqrt[2/Pi]*Sqrt[b]/x]*Sqrt[1/2*Pi]/b^(3/2)+1/2*FresnelS[Sqrt[2/Pi]*Sqrt[b]/x]*Sin[a]*Sqrt[1/2*Pi]/b^(3/2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:190
  public void test0138() {
    check( //
        "Integrate[x^(-1+2*n)*Sin[a+b*x^n], x]", //
        "-x^n*Cos[a+b*x^n]/(b*n)+Sin[a+b*x^n]/(b^2*n)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:280
  public void test0139() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(1/3)], x]", //
        "6*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d)-3*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d)+6*(c+d*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:306
  public void test0140() {
    check( //
        "Integrate[(c*e+d*e*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)], x]", //
        "18*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b^3*d)-3*(c+d*x)^(2/3)*(e*(c+d*x))^(1/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d)-18*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^4*d*(c+d*x)^(1/3))+9*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:324
  public void test0141() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(4/3), x]", //
        "3*(c+d*x)^(1/3)*Cos[a+b/(c+d*x)^(1/3)]/(b*d*e*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:343
  public void test0142() {
    check( //
        "Integrate[x*Sin[a+b*(c+d*x)^n], x]", //
        "-1/2*I*E^(I*a)*c*(c+d*x)*Gamma[1/n,-I*b*(c+d*x)^n]/(d^2*n*(-I*b*(c+d*x)^n)^(1/n))+1/2*I*c*(c+d*x)*Gamma[1/n,I*b*(c+d*x)^n]/(E^(I*a)*d^2*n*(I*b*(c+d*x)^n)^(1/n))+1/2*I*E^(I*a)*(c+d*x)^2*Gamma[2/n,-I*b*(c+d*x)^n]/(d^2*n*(-I*b*(c+d*x)^n)^(2/n))-1/2*I*(c+d*x)^2*Gamma[2/n,I*b*(c+d*x)^n]/(E^(I*a)*d^2*n*(I*b*(c+d*x)^n)^(2/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:433
  public void test0143() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(1/3), x]", //
        "Cos[a]*Csc[a+b*x^2]*FresnelS[x*Sqrt[2/Pi]*Sqrt[b]]*(c*Sin[a+b*x^2]^3)^(1/3)*Sqrt[1/2*Pi]/Sqrt[b]+Csc[a+b*x^2]*FresnelC[x*Sqrt[2/Pi]*Sqrt[b]]*Sin[a]*(c*Sin[a+b*x^2]^3)^(1/3)*Sqrt[1/2*Pi]/Sqrt[b]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:457
  public void test0144() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(2/3), x]", //
        "-1/2*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(2/3)/b+1/2*x*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:24
  public void test0145() {
    check( //
        "Integrate[x*Sin[a+b*x+c*x^2]^2, x]", //
        "1/4*x^2-1/8*Sin[2*a+2*b*x+2*c*x^2]/c+1/8*b*Cos[2*a-1/2*b^2/c]*FresnelC[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sqrt[Pi]/c^(3/2)-1/8*b*FresnelS[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sin[2*a-1/2*b^2/c]*Sqrt[Pi]/c^(3/2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:16
  public void test0146() {
    check( //
        "Integrate[Sin[x]^4/(a+a*Sin[x]), x]", //
        "-3/2*x/a-4*Cos[x]/a+4/3*Cos[x]^3/a+3/2*Cos[x]*Sin[x]/a+Cos[x]*Sin[x]^3/(a+a*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:32
  public void test0147() {
    check( //
        "Integrate[Csc[x]^3/(a+a*Sin[x])^2, x]", //
        "-7/2*ArcTanh[Cos[x]]/a^2+16/3*Cot[x]/a^2-7/2*Cot[x]*Csc[x]/a^2+8/3*Cot[x]*Csc[x]/(a^2*(1+Sin[x]))+1/3*Cot[x]*Csc[x]/(a+a*Sin[x])^2");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:69
  public void test0148() {
    check( //
        "Integrate[Sin[c+d*x]^3*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-284/231*a*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-284/99*a^3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-710/693*a^3*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-46/99*a^3*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+568/693*a^2*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d-2/11*a^2*Cos[c+d*x]*Sin[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:87
  public void test0149() {
    check( //
        "Integrate[Sin[c+d*x]^4/(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/2*Cos[c+d*x]*Sin[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))+15/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-31/5*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-9/10*Cos[c+d*x]*Sin[c+d*x]^2/(a*d*Sqrt[a+a*Sin[c+d*x]])+13/10*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:103
  public void test0150() {
    check( //
        "Integrate[Csc[c+d*x]^3/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-39/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)+1/4*Cot[c+d*x]*Csc[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))+19/16*Cot[c+d*x]*Csc[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))+219/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+63/16*Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-31/16*Cot[c+d*x]*Csc[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:201
  public void test0151() {
    check( //
        "Integrate[Sin[e+f*x]^2*(a+b*Sin[e+f*x])^2, x]", //
        "1/8*(4*a^2+3*b^2)*x-2*a*b*Cos[e+f*x]/f+2/3*a*b*Cos[e+f*x]^3/f-1/8*(4*a^2+3*b^2)*Cos[e+f*x]*Sin[e+f*x]/f-1/4*b^2*Cos[e+f*x]*Sin[e+f*x]^3/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:294
  public void test0152() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^4, x]", //
        "7/8*a*c^4*x+7/12*a*c^4*Cos[e+f*x]^3/f+7/8*a*c^4*Cos[e+f*x]*Sin[e+f*x]/f+1/5*a*Cos[e+f*x]^3*(c^2-c^2*Sin[e+f*x])^2/f+7/20*a*Cos[e+f*x]^3*(c^4-c^4*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:326
  public void test0153() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^7, x]", //
        "1/13*a^3*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^10)+3/143*a^3*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+2/429*a^3*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+2/3003*a^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:344
  public void test0154() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^2), x]", //
        "Tan[e+f*x]/(a^2*c^2*f)+1/3*Tan[e+f*x]^3/(a^2*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:364
  public void test0155() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/105*a*c^4*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+16/35*a*c^3*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])+2/7*a*c^2*Cos[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:380
  public void test0156() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/3003*a^3*c^7*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+64/429*a^3*c^6*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))+24/143*a^3*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(3/2))+2/13*a^3*c^4*Cos[e+f*x]^7/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:398
  public void test0157() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "15/32*Cos[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2))+1/4*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2))+15/32*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*c^(5/2)*f*Sqrt[2])-5/8*Sec[e+f*x]/(a*c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:414
  public void test0158() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "63/128*Cos[e+f*x]/(a^3*c*f*(c-c*Sin[e+f*x])^(3/2))+21/80*Sec[e+f*x]/(a^3*c*f*(c-c*Sin[e+f*x])^(3/2))+63/128*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*c^(5/2)*f*Sqrt[2])-21/32*Sec[e+f*x]/(a^3*c^2*f*Sqrt[c-c*Sin[e+f*x]])-3/10*Sec[e+f*x]^3/(a^3*c^2*f*Sqrt[c-c*Sin[e+f*x]])-1/5*Sec[e+f*x]^5*Sqrt[c-c*Sin[e+f*x]]/(a^3*c^3*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:450
  public void test0159() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/6*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(3/2)/f+1/15*c^3*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])+2/15*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:468
  public void test0160() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "1/2*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:484
  public void test0161() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-1/4*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2))-1/2*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2))+3/8*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*Cos[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:563
  public void test0162() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^6/(a+a*Sin[e+f*x])^3, x]", //
        "1/2*d^3*(40*c^3-90*c^2*d+78*c*d^2-23*d^3)*x/a^3+2/15*d*(2*c^5+18*c^4*d+107*c^3*d^2-472*c^2*d^3+456*c*d^4-136*d^5)*Cos[e+f*x]/(a^3*f)+1/30*d^2*(4*c^4+36*c^3*d+216*c^2*d^2-626*c*d^3+345*d^4)*Cos[e+f*x]*Sin[e+f*x]/(a^3*f)+1/15*d*(2*c^3+18*c^2*d+111*c*d^2-136*d^3)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(a^3*f)-1/15*(c-d)*(2*c^2+18*c*d+115*d^2)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(f*(a^3+a^3*Sin[e+f*x]))-1/15*(c-d)*(2*c+13*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^4/(a*f*(a+a*Sin[e+f*x])^2)-1/5*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^5/(f*(a+a*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:637
  public void test0163() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^2, x]", //
        "-a^(3/2)*(c+3*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(3/2)*(c+d)^(3/2)*f)+a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:655
  public void test0164() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(c-d)^2*(c+11*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])+1/3*d*(3*c^2-24*c*d+13*d^2)*Cos[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])+1/6*(3*c-7*d)*d^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:691
  public void test0165() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^(1/2), x]", //
        "-1/4*a^(5/2)*(3*c^2-10*c*d+19*d^2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(5/2)*f)+3/4*a^3*(c-3*d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d^2*f*Sqrt[a+a*Sin[e+f*x]])-1/2*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]]/(d*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:709
  public void test0166() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(3/2)), x]", //
        "-1/2*(c-7*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(3/2)*(c-d)^(5/2)*f*Sqrt[2])-1/2*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c+d*Sin[e+f*x]])-1/2*d*(c+5*d)*Cos[e+f*x]/(a*(c-d)^2*(c+d)*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:58
  public void test0167() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(17/2), x]", //
        "1/14*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c*f*(c-c*Sin[e+f*x])^(15/2))+1/84*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c^2*f*(c-c*Sin[e+f*x])^(13/2))+1/840*Cos[e+f*x]*(a+a*Sin[e+f*x])^(9/2)/(a*c^3*f*(c-c*Sin[e+f*x])^(11/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:76
  public void test0168() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-3/2*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-12*c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-6*c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:120
  public void test0169() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "2/21*a*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-2/9*a*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])+22/45*a*c^3*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/15*a*c^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/105*a*c^2*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:136
  public void test0170() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)/(c-c*Sin[e+f*x])^(11/2), x]", //
        "-28/221*a^2*(g*Cos[e+f*x])^(5/2)/(c*f*g*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]])+14/663*a^2*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])+14/1105*a^2*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+14/1105*a^2*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+4/17*a*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*(c-c*Sin[e+f*x])^(11/2))-14/1105*a^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^5*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:152
  public void test0171() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "4/5*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(5/2))-12*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(3/2))-154/5*a^4*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+462/5*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-66/5*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:170
  public void test0172() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]]/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-4*c*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])-6*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:250
  public void test0173() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]*(a+a*Sin[c+d*x])^2, x]", //
        "a^2*Log[Sin[c+d*x]]/d+2*a^2*Sin[c+d*x]/d+1/2*a^2*Sin[c+d*x]^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:334
  public void test0174() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "1/8*a*x-1/3*a*Cos[c+d*x]^3/d+1/5*a*Cos[c+d*x]^5/d+1/8*a*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:351
  public void test0175() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^2, x]", //
        "3/16*a^2*ArcTanh[Cos[c+d*x]]/d-2/3*a^2*Cot[c+d*x]^3/d-2/5*a^2*Cot[c+d*x]^5/d+3/16*a^2*Cot[c+d*x]*Csc[c+d*x]/d-5/24*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:370
  public void test0176() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-x/a-ArcTanh[Cos[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:386
  public void test0177() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "-x/a^3-7/3*Cos[c+d*x]/(a^3*d*(1+Sin[c+d*x]))+2/3*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:406
  public void test0178() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-Cot[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d+11/3*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+5/3*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:424
  public void test0179() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^(3/2), x]", //
        "23/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)-9/8*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])+7/12*Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:456
  public void test0180() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "3/128*a*x-1/5*a*Cos[c+d*x]^5/d+2/7*a*Cos[c+d*x]^7/d-1/9*a*Cos[c+d*x]^9/d+3/128*a*Cos[c+d*x]*Sin[c+d*x]/d+1/64*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/16*a*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*a*Cos[c+d*x]^5*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:472
  public void test0181() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+a*Sin[c+d*x])^2, x]", //
        "1/8*a^2*x-1/15*a^2*Cos[c+d*x]^5/d+1/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/12*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-1/7*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2/d-1/21*Cos[c+d*x]^5*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:488
  public void test0182() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "-3/8*a^3*x-3*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d+a^3*Cos[c+d*x]^3/d-1/5*a^3*Cos[c+d*x]^5/d-a^3*Cot[c+d*x]/d+11/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d-3/4*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:522
  public void test0183() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-7/8*ArcTanh[Cos[c+d*x]]/(a^2*d)+2*Cot[c+d*x]/(a^2*d)+2/3*Cot[c+d*x]^3/(a^2*d)-7/8*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:543
  public void test0184() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-2/5*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+61/15*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+4/15*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d-Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:559
  public void test0185() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^8*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-171/1024*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/7*Cot[c+d*x]*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^(3/2)/d-171/1024*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-57/512*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+199/640*a^2*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+1237/2240*a^2*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+9/40*a^2*Cot[c+d*x]*Csc[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])-1/28*a*Cot[c+d*x]*Csc[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:577
  public void test0186() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-1/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-1/8*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])+11/12*Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:605
  public void test0187() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cos[c+d*x]^6/d+1/3*a*Sin[c+d*x]^3/d-2/5*a*Sin[c+d*x]^5/d+1/7*a*Sin[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:715
  public void test0188() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "5*a^2*x+5/8*a^2*ArcTanh[Cos[c+d*x]]/d-a^2*Cos[c+d*x]/d-1/3*a^2*Cos[c+d*x]^3/d+4*a^2*Cot[c+d*x]/d-2/3*a^2*Cot[c+d*x]^3/d+5/8*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:731
  public void test0189() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "-25/8*a^3*x+13/2*a^3*ArcTanh[Cos[c+d*x]]/d-5*a^3*Cos[c+d*x]/d-2/3*a^3*Cos[c+d*x]^3/d+1/5*a^3*Cos[c+d*x]^5/d-a^3*Cot[c+d*x]/d-1/3*a^3*Cot[c+d*x]^3/d-3/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d-23/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d+3/4*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:765
  public void test0190() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "-23/16*x/a^3-4*Cos[c+d*x]/(a^3*d)+7/3*Cos[c+d*x]^3/(a^3*d)-3/5*Cos[c+d*x]^5/(a^3*d)+23/16*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+23/24*Cos[c+d*x]*Sin[c+d*x]^3/(a^3*d)+1/6*Cos[c+d*x]*Sin[c+d*x]^5/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:815
  public void test0191() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-1/6*Cos[c+d*x]^6/(a*d)+1/8*Cos[c+d*x]^8/(a*d)-1/5*Sin[c+d*x]^5/(a*d)+2/7*Sin[c+d*x]^7/(a*d)-1/9*Sin[c+d*x]^9/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:877
  public void test0192() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*x/a^2-3*ArcTanh[Cos[c+d*x]]/(a^2*d)+2*Cos[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]^3/(a^2*d)+Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/2*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:893
  public void test0193() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "x/a^3+13/8*ArcTanh[Cos[c+d*x]]/(a^3*d)+Cot[c+d*x]/(a^3*d)+Cot[c+d*x]^3/(a^3*d)-11/8*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1050
  public void test0194() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "3/5*Sec[c+d*x]^5/(a^3*d)-Sec[c+d*x]^7/(a^3*d)+4/9*Sec[c+d*x]^9/(a^3*d)-3/5*Tan[c+d*x]^5/(a^3*d)-Tan[c+d*x]^7/(a^3*d)-4/9*Tan[c+d*x]^9/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1200
  public void test0195() {
    check( //
        "Integrate[Cos[e+f*x]^2/((a+a*Sin[e+f*x])^(3/2)*Sqrt[c+d*Sin[e+f*x]]), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/(a^(3/2)*f*Sqrt[c-d])+2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[d])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1243
  public void test0196() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "1/9*(A+B)*Sec[c+d*x]^9*(a+a*Sin[c+d*x])/d+1/9*a*(8*A-B)*Tan[c+d*x]/d+1/9*a*(8*A-B)*Tan[c+d*x]^3/d+1/15*a*(8*A-B)*Tan[c+d*x]^5/d+1/63*a*(8*A-B)*Tan[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1259
  public void test0197() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/63*a^2*(7*A-2*B)*Sec[c+d*x]^7/d+1/9*(A+B)*Sec[c+d*x]^9*(a+a*Sin[c+d*x])^2/d+1/9*a^2*(7*A-2*B)*Tan[c+d*x]/d+1/9*a^2*(7*A-2*B)*Tan[c+d*x]^3/d+1/15*a^2*(7*A-2*B)*Tan[c+d*x]^5/d+1/63*a^2*(7*A-2*B)*Tan[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1275
  public void test0198() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "1/15*a^5*(2*A-3*B)*Cos[c+d*x]/(d*(a-a*Sin[c+d*x])^2)+1/5*(A+B)*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^3/d+1/15*a^5*(2*A-3*B)*Cos[c+d*x]/(d*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1371
  public void test0199() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]*(a+b*Sin[c+d*x])^3, x]", //
        "1/16*b*(6*a^2+b^2)*x-1/120*a*(2*a^2+33*b^2)*Cos[c+d*x]^3/d+1/16*b*(6*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d-1/40*(2*a^2+5*b^2)*Cos[c+d*x]^3*(a+b*Sin[c+d*x])/d-1/10*a*Cos[c+d*x]^3*(a+b*Sin[c+d*x])^2/d-1/6*Cos[c+d*x]^3*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1417
  public void test0200() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+b*Sin[c+d*x]), x]", //
        "-1/16*a*ArcTanh[Cos[c+d*x]]/d-1/5*b*Cot[c+d*x]^5/d-1/16*a*Cot[c+d*x]*Csc[c+d*x]/d+1/8*a*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a*Cot[c+d*x]^3*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1434
  public void test0201() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^3, x]", //
        "-3/8*a*(4*a^2-3*b^2)*x-3*a^2*b*ArcTanh[Cos[c+d*x]]/d+1/10*(a^4+56*a^2*b^2-2*b^4)*Cos[c+d*x]/(b*d)+1/40*a*(2*a^2+83*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/20*(a^2+28*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/(b*d)+1/20*(a^2+20*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/(a*b*d)-1/5*Cos[c+d*x]*(a+b*Sin[c+d*x])^4/(b*d)-Cot[c+d*x]*(a+b*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1589
  public void test0202() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2*(a+b*Sin[c+d*x])^2, x]", //
        "1/256*(10*a^2+3*b^2)*x-2/7*a*b*Cos[c+d*x]^7/d+2/9*a*b*Cos[c+d*x]^9/d+1/256*(10*a^2+3*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/384*(10*a^2+3*b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/480*(10*a^2+3*b^2)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/80*(10*a^2+11*b^2)*Cos[c+d*x]^7*Sin[c+d*x]/d+1/10*b^2*Cos[c+d*x]^9*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1656
  public void test0203() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "1/2*(a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^3*d)+b*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)+2*b*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1672
  public void test0204() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^4*d)-1/2*b*(3*a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^4*d)+1/3*(4*a^2-3*b^2)*Cot[c+d*x]/(a^3*d)+1/2*b*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1708
  public void test0205() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "-2*a^2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)-b*Sec[c+d*x]/((a^2-b^2)*d)+a*Tan[c+d*x]/((a^2-b^2)*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1868
  public void test0206() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]/(a+b*Sin[c+d*x])^2, x]", //
        "2*b*(2*a^2+b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)-a*Sec[c+d*x]/((a^2-b^2)*d*(a+b*Sin[c+d*x]))+Sec[c+d*x]*(2*a^2+b^2-3*a*b*Sin[c+d*x])/((a^2-b^2)^2*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:64
  public void test0207() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]/(Sin[e+f*x]*(c+d*Sin[e+f*x])), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]*Sqrt[a]/(c*f)+2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]*Sqrt[d]/(c*f*Sqrt[c+d])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:31
  public void test0208() {
    check( //
        "Integrate[Sin[e+f*x]^2*(A+B*Sin[e+f*x])/(a+b*Sin[e+f*x])^2, x]", //
        "(A*b-2*a*B)*x/b^3-2*a*(a^2*A*b-2*A*b^3-2*a^3*B+3*a*b^2*B)*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/(b^3*(a^2-b^2)^(3/2)*f)-B*Cos[e+f*x]/(b^2*f)+a^2*(A*b-a*B)*Cos[e+f*x]/(b^2*(a^2-b^2)*f*(a+b*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:56
  public void test0209() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^3, x]", //
        "-a^2*B*x/c^3+1/5*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)-2/3*a^2*B*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^3)+2*a^2*B*Cos[e+f*x]/(f*(c^3-c^3*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:72
  public void test0210() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^6, x]", //
        "1/11*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+1/99*a^3*(2*A-9*B)*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+1/693*a^3*(2*A-9*B)*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:90
  public void test0211() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])), x]", //
        "-1/3*(A-B)*Sec[e+f*x]/(c*f*(a^2+a^2*Sin[e+f*x]))+1/3*(2*A+B)*Tan[e+f*x]/(a^2*c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:110
  public void test0212() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/3465*a*(11*A-5*B)*c^5*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+2/99*a*(11*A-5*B)*c^2*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/f-2/11*a*B*c*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/f+64/1155*a*(11*A-5*B)*c^4*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])+8/231*a*(11*A-5*B)*c^3*Cos[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:126
  public void test0213() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/8*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(13/2))+1/48*a^2*(3*A-13*B)*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(9/2))-1/64*a^2*(3*A-13*B)*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(5/2))+1/256*a^2*(3*A-13*B)*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(3/2))+1/256*a^2*(3*A-13*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(9/2)*f*Sqrt[2])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:144
  public void test0214() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "1/4*(3*A-B)*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(3/2))+1/4*(3*A-B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*c^(3/2)*f*Sqrt[2])-(A-B)*Sec[e+f*x]/(a*c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:160
  public void test0215() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "1/16*(7*A+3*B)*Cos[e+f*x]/(a^3*f*(c-c*Sin[e+f*x])^(3/2))-1/5*(A-B)*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(3/2)/(a^3*c^3*f)+1/16*(7*A+3*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*c^(3/2)*f*Sqrt[2])-1/12*(7*A+3*B)*Sec[e+f*x]/(a^3*c*f*Sqrt[c-c*Sin[e+f*x]])-1/30*(7*A+3*B)*Sec[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/(a^3*c^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:180
  public void test0216() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/4*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(5/2))-a*B*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*(c-c*Sin[e+f*x])^(3/2))-a^2*B*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:196
  public void test0217() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/7*a^2*A*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/7*a*A*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/8*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(7/2)/f-2/35*a^4*A*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])-4/35*a^3*A*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:214
  public void test0218() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((c-c*Sin[e+f*x])^(1/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "-1/2*(A+B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+1/2*(A-B)*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:230
  public void test0219() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-1/4*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2))-1/8*(3*A+B)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+1/8*(3*A+B)*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/8*(3*A+B)*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:274
  public void test0220() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "5/8*a^3*A*x-5/12*a^3*A*Cos[c+d*x]^3/d+5/8*a^3*A*Cos[c+d*x]*Sin[c+d*x]/d-1/4*A*Cos[c+d*x]^3*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:385
  public void test0221() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/4*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*A+5*B)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/16*(3*A+5*B)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.4.1 (a+b sin)^m (A+B sin+C sin^2).input:14
  public void test0222() {
    check( //
        "Integrate[1-2*Sin[e+f*x]^2, x]", //
        "Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:10
  public void test0223() {
    check( //
        "Integrate[(a*Sin[x]^2)^(5/2), x]", //
        "-4/15*a*Cot[x]*(a*Sin[x]^2)^(3/2)-1/5*Cot[x]*(a*Sin[x]^2)^(5/2)-8/15*a^2*Cot[x]*Sqrt[a*Sin[x]^2]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:30
  public void test0224() {
    check( //
        "Integrate[1/(a*Sin[x]^4)^(3/2), x]", //
        "-2/3*Cos[x]^2*Cot[x]/(a*Sqrt[a*Sin[x]^4])-1/5*Cos[x]^2*Cot[x]^3/(a*Sqrt[a*Sin[x]^4])-Cos[x]*Sin[x]/(a*Sqrt[a*Sin[x]^4])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:73
  public void test0225() {
    check( //
        "Integrate[Sin[c+d*x]^2/(a-a*Sin[c+d*x]^2), x]", //
        "-x/a+Tan[c+d*x]/(a*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:144
  public void test0226() {
    check( //
        "Integrate[1/(a+b*Sin[c+d*x]^2)^3, x]", //
        "1/8*(8*a^2+8*a*b+3*b^2)*ArcTan[Sqrt[a+b]*Tan[c+d*x]/Sqrt[a]]/(a^(5/2)*(a+b)^(5/2)*d)+1/4*b*Cos[c+d*x]*Sin[c+d*x]/(a*(a+b)*d*(a+b*Sin[c+d*x]^2)^2)+3/8*b*(2*a+b)*Cos[c+d*x]*Sin[c+d*x]/(a^2*(a+b)^2*d*(a+b*Sin[c+d*x]^2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:237
  public void test0227() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a+b*Sin[c+d*x]^3), x]", //
        "x/b-2/3*a^(1/3)*ArcTan[(b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-b^(2/3)]]/(b*d*Sqrt[a^(2/3)-b^(2/3)])-2/3*a^(1/3)*ArcTan[((-1)^(2/3)*b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)]]/(b*d*Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)])+2/3*a^(1/3)*ArcTan[(-1)^(1/3)*(b^(1/3)+(-1)^(2/3)*a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)]]/(b*d*Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:263
  public void test0228() {
    check( //
        "Integrate[Csc[c+d*x]/(a-b*Sin[c+d*x]^4), x]", //
        "-ArcTanh[Cos[c+d*x]]/(a*d)-1/2*b^(1/4)*ArcTan[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]-Sqrt[b]]]/(a*d*Sqrt[Sqrt[a]-Sqrt[b]])+1/2*b^(1/4)*ArcTanh[b^(1/4)*Cos[c+d*x]/Sqrt[Sqrt[a]+Sqrt[b]]]/(a*d*Sqrt[Sqrt[a]+Sqrt[b]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:324
  public void test0229() {
    check( //
        "Integrate[1/(a-b*Sin[x]^6), x]", //
        "1/3*ArcTan[Sqrt[a^(1/3)-b^(1/3)]*Tan[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)-b^(1/3)])+1/3*ArcTan[Sqrt[a^(1/3)+(-1)^(1/3)*b^(1/3)]*Tan[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)+(-1)^(1/3)*b^(1/3)])+1/3*ArcTan[Sqrt[a^(1/3)-(-1)^(2/3)*b^(1/3)]*Tan[x]/a^(1/6)]/(a^(5/6)*Sqrt[a^(1/3)-(-1)^(2/3)*b^(1/3)])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:351
  public void test0230() {
    check( //
        "Integrate[Sec[x]/(a-a*Sin[x]^2), x]", //
        "1/2*ArcTanh[Sin[x]]/a+1/2*Sec[x]*Tan[x]/a");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:371
  public void test0231() {
    check( //
        "Integrate[Cos[e+f*x]^4*(a+b*Sin[e+f*x]^2), x]", //
        "1/16*(6*a+b)*x+1/16*(6*a+b)*Cos[e+f*x]*Sin[e+f*x]/f+1/24*(6*a+b)*Cos[e+f*x]^3*Sin[e+f*x]/f-1/6*b*Cos[e+f*x]^5*Sin[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:405
  public void test0232() {
    check( //
        "Integrate[Cos[x]^2/(a+b*Sin[x]^2)^2, x]", //
        "1/2*ArcTan[Sqrt[a+b]*Tan[x]/Sqrt[a]]/(a^(3/2)*Sqrt[a+b])+1/2*Tan[x]/(a*(a+(a+b)*Tan[x]^2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:431
  public void test0233() {
    check( //
        "Integrate[Sec[e+f*x]^5*(a+b*Sin[e+f*x]^2)^(3/2), x]", //
        "3/8*a^2*ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/(f*Sqrt[a+b])+1/4*Sec[e+f*x]^3*(a+b*Sin[e+f*x]^2)^(3/2)*Tan[e+f*x]/f+3/8*a*Sec[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]*Tan[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:494
  public void test0234() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+b*Sin[c+d*x]^3), x]", //
        "2/3*ArcTan[(b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)-b^(2/3)])-2/3*ArcTan[(b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-b^(2/3)]]/(b^(2/3)*d*Sqrt[a^(2/3)-b^(2/3)])+2/3*ArcTanh[(b^(1/3)+(-1)^(2/3)*a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[(-1)^(1/3)*a^(2/3)+b^(2/3)]]/(b^(2/3)*d*Sqrt[(-1)^(1/3)*a^(2/3)+b^(2/3)])+2/3*ArcTanh[(b^(1/3)-(-1)^(1/3)*a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[-(-1)^(2/3)*a^(2/3)+b^(2/3)]]/(b^(2/3)*d*Sqrt[-(-1)^(2/3)*a^(2/3)+b^(2/3)])+2/3*ArcTan[((-1)^(2/3)*b^(1/3)+a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)+(-1)^(1/3)*b^(2/3)])-2/3*ArcTan[(-1)^(1/3)*(b^(1/3)+(-1)^(2/3)*a^(1/3)*Tan[1/2*(c+d*x)])/Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)]]/(a^(2/3)*d*Sqrt[a^(2/3)-(-1)^(2/3)*b^(2/3)])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:613
  public void test0235() {
    check( //
        "Integrate[Tan[e+f*x]/(a-a*Sin[e+f*x]^2)^(3/2), x]", //
        "1/3/(f*(a*Cos[e+f*x]^2)^(3/2))");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:651
  public void test0236() {
    check( //
        "Integrate[Tan[e+f*x]/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "ArcTanh[Sqrt[a+b*Sin[e+f*x]^2]/Sqrt[a+b]]/(f*Sqrt[a+b])");
  }

  // 4.1.8 (a+b sin)^m (c+d trig)^n.input:9
  public void test0237() {
    check( //
        "Integrate[(A+B*Cos[x])/(1+Sin[x]), x]", //
        "B*Log[1+Sin[x]]-A*Cos[x]/(1+Sin[x])");
  }

  // 4.1.9 trig^m (a+b sin^n+c sin^(2 n))^p.input:32
  public void test0238() {
    check( //
        "Integrate[Cos[x]^3/(a+b*Sin[x]+c*Sin[x]^2), x]", //
        "1/2*b*Log[a+b*Sin[x]+c*Sin[x]^2]/c^2-Sin[x]/c+(b^2-2*c*(a+c))*ArcTanh[(b+2*c*Sin[x])/Sqrt[b^2-4*a*c]]/(c^2*Sqrt[b^2-4*a*c])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:15
  public void test0239() {
    check( //
        "Integrate[Cos[a+b*x]^6, x]", //
        "5/16*x+5/16*Cos[a+b*x]*Sin[a+b*x]/b+5/24*Cos[a+b*x]^3*Sin[a+b*x]/b+1/6*Cos[a+b*x]^5*Sin[a+b*x]/b");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:33
  public void test0240() {
    check( //
        "Integrate[1/(c*Cos[a+b*x])^(3/2), x]", //
        "2*Sin[a+b*x]/(b*c*Sqrt[c*Cos[a+b*x]])-2*EllipticE[1/2*(a+b*x),2]*Sqrt[c*Cos[a+b*x]]/(b*c^2*Sqrt[Cos[a+b*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:71
  public void test0241() {
    check( //
        "Integrate[(a*Cos[x]^4)^(3/2), x]", //
        "5/16*a*x*Sec[x]^2*Sqrt[a*Cos[x]^4]+5/24*a*Cos[x]*Sin[x]*Sqrt[a*Cos[x]^4]+1/6*a*Cos[x]^3*Sin[x]*Sqrt[a*Cos[x]^4]+5/16*a*Sqrt[a*Cos[x]^4]*Tan[x]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:109
  public void test0242() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sqrt[b*Cos[c+d*x]], x]", //
        "2/7*b^4*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(7/2))+10/21*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+10/21*b*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:125
  public void test0243() {
    check( //
        "Integrate[Cos[c+d*x]*(b*Cos[c+d*x])^(5/2), x]", //
        "2/7*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+10/21*b^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*b^2*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:143
  public void test0244() {
    check( //
        "Integrate[Cos[c+d*x]/Sqrt[b*Cos[c+d*x]], x]", //
        "2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:159
  public void test0245() {
    check( //
        "Integrate[Sec[c+d*x]^2/(b*Cos[c+d*x])^(3/2), x]", //
        "2/5*b*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+6/5*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])-6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:179
  public void test0246() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(b*Cos[c+d*x])^(1/2), x]", //
        "1/4*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+3/8*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:213
  public void test0247() {
    check( //
        "Integrate[Cos[c+d*x]^(9/2)/(b*Cos[c+d*x])^(1/2), x]", //
        "3/8*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+1/4*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+3/8*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]");
  }

  // 4.2.1.1 (a+b cos)^n.input:12
  public void test0248() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(7/2), x]", //
        "24/35*a^2*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*a*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+256/35*a^4*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+64/35*a^3*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.1.2 (g sin)^p (a+b cos)^m.input:31
  public void test0249() {
    check( //
        "Integrate[Sin[x]^3/(1+Cos[x])^3, x]", //
        "2/(1+Cos[x])+Log[1+Cos[x]]");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:42
  public void test0250() {
    check( //
        "Integrate[Tan[x]/(a+b*Cos[x]), x]", //
        "-Log[Cos[x]]/a+Log[a+b*Cos[x]]/a");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:20
  public void test0251() {
    check( //
        "Integrate[(c+d*x)^4*Cos[a+b*x]^2, x]", //
        "3/4*d^4*x/b^4-1/2*d*(c+d*x)^3/b^2+1/10*(c+d*x)^5/d-3/2*d^3*(c+d*x)*Cos[a+b*x]^2/b^4+d*(c+d*x)^3*Cos[a+b*x]^2/b^2+3/4*d^4*Cos[a+b*x]*Sin[a+b*x]/b^5-3/2*d^2*(c+d*x)^2*Cos[a+b*x]*Sin[a+b*x]/b^3+1/2*(c+d*x)^4*Cos[a+b*x]*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:36
  public void test0252() {
    check( //
        "Integrate[x*Cos[a+b*x]^4, x]", //
        "3/16*x^2+3/16*Cos[a+b*x]^2/b^2+1/16*Cos[a+b*x]^4/b^2+3/8*x*Cos[a+b*x]*Sin[a+b*x]/b+1/4*x*Cos[a+b*x]^3*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:135
  public void test0253() {
    check( //
        "Integrate[x^(3+m)*Cos[a+b*x], x]", //
        "-1/2*E^(I*a)*x^m*Gamma[4+m,-I*b*x]/(b^4*(-I*b*x)^m)-1/2*x^m*Gamma[4+m,I*b*x]/(E^(I*a)*b^4*(I*b*x)^m)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:159
  public void test0254() {
    check( //
        "Integrate[(c+d*x)*(a+a*Cos[e+f*x]), x]", //
        "1/2*a*(c+d*x)^2/d+a*d*Cos[e+f*x]/f^2+a*(c+d*x)*Sin[e+f*x]/f");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:203
  public void test0255() {
    check( //
        "Integrate[x^2*Sqrt[a-a*Cos[x]], x]", //
        "8*x*Sqrt[a-a*Cos[x]]+16*Cot[1/2*x]*Sqrt[a-a*Cos[x]]-2*x^2*Cot[1/2*x]*Sqrt[a-a*Cos[x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:222
  public void test0256() {
    check( //
        "Integrate[x^3/Sqrt[a-a*Cos[x]], x]", //
        "-4*x^3*ArcTanh[E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]+12*I*x^2*PolyLog[2,-E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]-12*I*x^2*PolyLog[2,E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]-48*x*PolyLog[3,-E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]+48*x*PolyLog[3,E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]-96*I*PolyLog[4,-E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]+96*I*PolyLog[4,E^(1/2*I*x)]*Sin[1/2*x]/Sqrt[a-a*Cos[x]]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:31
  public void test0257() {
    check( //
        "Integrate[x*Cos[a+b*x^2]^7, x]", //
        "1/2*Sin[a+b*x^2]/b-1/2*Sin[a+b*x^2]^3/b+3/10*Sin[a+b*x^2]^5/b-1/14*Sin[a+b*x^2]^7/b");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:57
  public void test0258() {
    check( //
        "Integrate[Cos[a+b/x]/x^3, x]", //
        "-Cos[a+b/x]/b^2-Sin[a+b/x]/(b*x)");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:105
  public void test0259() {
    check( //
        "Integrate[x^m*Cos[a+b*x^n], x]", //
        "-1/2*E^(I*a)*x^(1+m)*Gamma[(1+m)/n,-I*b*x^n]/(n*(-I*b*x^n)^((1+m)/n))-1/2*x^(1+m)*Gamma[(1+m)/n,I*b*x^n]/(E^(I*a)*n*(I*b*x^n)^((1+m)/n))");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:129
  public void test0260() {
    check( //
        "Integrate[Cos[a+b*Sqrt[c+d*x]]/x^2, x]", //
        "-Cos[a+b*Sqrt[c+d*x]]/x+1/2*b*d*Cos[a-b*Sqrt[c]]*SinIntegral[b*(Sqrt[c]+Sqrt[c+d*x])]/Sqrt[c]+1/2*b*d*Cos[a+b*Sqrt[c]]*SinIntegral[b*Sqrt[c]-b*Sqrt[c+d*x]]/Sqrt[c]+1/2*b*d*CosIntegral[b*(Sqrt[c]+Sqrt[c+d*x])]*Sin[a-b*Sqrt[c]]/Sqrt[c]-1/2*b*d*CosIntegral[b*Sqrt[c]-b*Sqrt[c+d*x]]*Sin[a+b*Sqrt[c]]/Sqrt[c]");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:14
  public void test0261() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Cos[c+d*x]), x]", //
        "3/8*a*x+a*Sin[c+d*x]/d+3/8*a*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/3*a*Sin[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:31
  public void test0262() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*Sec[c+d*x]^3, x]", //
        "3/2*a^2*ArcTanh[Sin[c+d*x]]/d+2*a^2*Tan[c+d*x]/d+1/2*a^2*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:49
  public void test0263() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*Sec[c+d*x]^3, x]", //
        "4*a^4*x+13/2*a^4*ArcTanh[Sin[c+d*x]]/d+a^4*Sin[c+d*x]/d+4*a^4*Tan[c+d*x]/d+1/2*a^4*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:67
  public void test0264() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Cos[c+d*x])^2, x]", //
        "7/2*x/a^2-16/3*Sin[c+d*x]/(a^2*d)+7/2*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-8/3*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:83
  public void test0265() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Cos[c+d*x])^3, x]", //
        "-3*ArcTanh[Sin[c+d*x]]/(a^3*d)+24/5*Tan[c+d*x]/(a^3*d)-1/5*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-3/5*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-3*Tan[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:99
  public void test0266() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Cos[c+d*x])^5, x]", //
        "-1/9*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^5)+1/7*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^4)-17/63*Sin[c+d*x]/(a^2*d*(a+a*Cos[c+d*x])^3)+5/63*Sin[c+d*x]/(a^3*d*(a+a*Cos[c+d*x])^2)+5/63*Sin[c+d*x]/(d*(a^5+a^5*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:119
  public void test0267() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*Sec[c+d*x]^3, x]", //
        "3/4*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+3/4*a*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*a*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:135
  public void test0268() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]^3, x]", //
        "19/4*a^(5/2)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+9/4*a^3*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*a^2*Sec[c+d*x]*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:153
  public void test0269() {
    check( //
        "Integrate[Cos[c+d*x]/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+3/2*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:173
  public void test0270() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])/Cos[c+d*x]^(3/2), x]", //
        "-2*a*EllipticE[1/2*(c+d*x),2]/d+2*a*EllipticF[1/2*(c+d*x),2]/d+2*a*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:189
  public void test0271() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3/Cos[c+d*x]^(9/2), x]", //
        "-28/5*a^3*EllipticE[1/2*(c+d*x),2]/d+52/21*a^3*EllipticF[1/2*(c+d*x),2]/d+2/7*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+6/5*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+52/21*a^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+28/5*a^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:207
  public void test0272() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)/(a+a*Cos[c+d*x])^2, x]", //
        "-7*EllipticE[1/2*(c+d*x),2]/(a^2*d)+10/3*EllipticF[1/2*(c+d*x),2]/(a^2*d)-7/3*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)+10/3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:227
  public void test0273() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(1/2), x]", //
        "5/8*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+5/12*a*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*a*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+5/8*a*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:243
  public void test0274() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(5/2), x]", //
        "25/8*a^(5/2)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+13/12*a^3*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+25/8*a^3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*a^2*Cos[c+d*x]^(3/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:261
  public void test0275() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(7/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "-ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/5*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])-2/15*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+26/15*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:277
  public void test0276() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "3/16*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(5/2))+7/16*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:315
  public void test0277() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(1/2)*(a-a*Cos[c+d*x])^(1/2)), x]", //
        "-ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a-a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:360
  public void test0278() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*Sec[c+d*x]^(5/2), x]", //
        "2/3*a^4*Sec[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*a^4*Sin[c+d*x]/(d*Sqrt[Sec[c+d*x]])+8*a^4*Sin[c+d*x]*Sqrt[Sec[c+d*x]]/d+40/3*a^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:482
  public void test0279() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Cos[c+d*x])^2, x]", //
        "1/16*(6*a^2+5*b^2)*x+2*a*b*Sin[c+d*x]/d+1/16*(6*a^2+5*b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(6*a^2+5*b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*b^2*Cos[c+d*x]^5*Sin[c+d*x]/d-4/3*a*b*Sin[c+d*x]^3/d+2/5*a*b*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:500
  public void test0280() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*Sec[c+d*x]^4, x]", //
        "1/2*b*(3*a^2+2*b^2)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(2*a^2+9*b^2)*Tan[c+d*x]/d+7/6*a^2*b*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a^2*(a+b*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:518
  public void test0281() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Cos[c+d*x]), x]", //
        "1/2*(2*a^2+b^2)*x/b^3-a*Sin[c+d*x]/(b^2*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(b*d)-2*a^3*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b^3*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:535
  public void test0282() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Cos[c+d*x])^2, x]", //
        "2*b^4*(5*a^2-4*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^5*(a-b)^(3/2)*(a+b)^(3/2)*d)-b*(a^2+4*b^2)*ArcTanh[Sin[c+d*x]]/(a^5*d)+1/3*(2*a^4+7*a^2*b^2-12*b^4)*Tan[c+d*x]/(a^4*(a^2-b^2)*d)-b*(a^2-2*b^2)*Sec[c+d*x]*Tan[c+d*x]/(a^3*(a^2-b^2)*d)+1/3*(a^2-4*b^2)*Sec[c+d*x]^2*Tan[c+d*x]/(a^2*(a^2-b^2)*d)+b^2*Sec[c+d*x]^2*Tan[c+d*x]/(a*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:581
  public void test0283() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sqrt[3+4*Cos[c+d*x]], x]", //
        "1/10*(3+4*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+21/20*EllipticE[1/2*(c+d*x),8/7]*Sqrt[7]/d-1/20*EllipticF[1/2*(c+d*x),8/7]*Sqrt[7]/d-1/5*Sin[c+d*x]*Sqrt[3+4*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:639
  public void test0284() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "6/5*B*EllipticE[1/2*(c+d*x),2]/d+2/3*A*EllipticF[1/2*(c+d*x),2]/d+2/5*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*A*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:655
  public void test0285() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3/Cos[c+d*x]^(3/2), x]", //
        "-2*a*(a^2-3*b^2)*EllipticE[1/2*(c+d*x),2]/d+2/3*b*(9*a^2+b^2)*EllipticF[1/2*(c+d*x),2]/d+2*a^2*(a+b*Cos[c+d*x])*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-2/3*b*(3*a^2-b^2)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:905
  public void test0286() {
    check( //
        "Integrate[(-5/3*B+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-2/3*B*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:945
  public void test0287() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "2*A*b^2*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2*b^2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*A*b*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:963
  public void test0288() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^3/(b*Cos[c+d*x])^(1/2), x]", //
        "2/5*A*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/3*b*B*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+6/5*A*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-6/5*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:983
  public void test0289() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]), x]", //
        "1/4*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*A*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+3/8*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:999
  public void test0290() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]), x]", //
        "1/4*b^2*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*b^2*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*b^2*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*A*b^2*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+3/8*b^2*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1017
  public void test0291() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(3/2), x]", //
        "1/2*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/2*B*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])+A*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:14
  public void test0292() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]), x]", //
        "1/2*a*(A+B)*x+1/3*a*(3*A+2*B)*Sin[c+d*x]/d+1/2*a*(A+B)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*a*B*Cos[c+d*x]^2*Sin[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:30
  public void test0293() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]), x]", //
        "1/16*a^3*(26*A+23*B)*x+1/5*a^3*(19*A+17*B)*Sin[c+d*x]/d+1/16*a^3*(26*A+23*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/40*a^3*(22*A+21*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*a*B*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/15*(3*A+4*B)*Cos[c+d*x]^3*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d-1/15*a^3*(19*A+17*B)*Sin[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:46
  public void test0294() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "a^4*B*x+1/8*a^4*(35*A+48*B)*ArcTanh[Sin[c+d*x]]/d+5/8*a^4*(7*A+8*B)*Tan[c+d*x]/d+1/24*(35*A+32*B)*(a^4+a^4*Cos[c+d*x])*Sec[c+d*x]*Tan[c+d*x]/d+1/12*(7*A+4*B)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:65
  public void test0295() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+a*Cos[c+d*x])^2, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^2*d)-1/3*(4*A-B)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:81
  public void test0296() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^4, x]", //
        "-2/105*(A+27*B)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)+1/105*(13*A+36*B)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))+1/7*(A-B)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/35*(A-8*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:101
  public void test0297() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "2/35*(7*A-2*B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*B*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a*d)+8/105*a^2*(21*A+19*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*a*(21*A+19*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:119
  public void test0298() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-(A-B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+4/105*(49*A-37*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/35*(7*A-B)*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/7*B*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-2/105*(7*A-31*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:135
  public void test0299() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(5/2), x]", //
        "1/4*(A-B)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))+1/16*(9*A-17*B)*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(75*A-163*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+1/24*(93*A-197*B)*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])-1/48*(39*A-95*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:155
  public void test0300() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x])/Sqrt[Cos[c+d*x]], x]", //
        "4/5*a^2*(5*A+4*B)*EllipticE[1/2*(c+d*x),2]/d+4/3*a^2*(2*A+B)*EllipticF[1/2*(c+d*x),2]/d+2/15*a^2*(5*A+7*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/5*B*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:173
  public void test0301() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/((a+a*Cos[c+d*x])*Sqrt[Cos[c+d*x]]), x]", //
        "(A-B)*EllipticE[1/2*(c+d*x),2]/(a*d)+(A+B)*EllipticF[1/2*(c+d*x),2]/(a*d)-(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:189
  public void test0302() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^3), x]", //
        "-1/10*(49*A-9*B)*EllipticE[1/2*(c+d*x),2]/(a^3*d)-1/6*(13*A-3*B)*EllipticF[1/2*(c+d*x),2]/(a^3*d)+1/10*(49*A-9*B)*Sin[c+d*x]/(a^3*d*Sqrt[Cos[c+d*x]])-1/5*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3*Sqrt[Cos[c+d*x]])-1/15*(8*A-3*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]])-1/6*(13*A-3*B)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x])*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:209
  public void test0303() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(9/2), x]", //
        "2/35*a^2*(8*A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*a^2*(52*A+63*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+4/105*a^2*(52*A+63*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/7*a*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(7/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:227
  public void test0304() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(7/2)*(a+a*Cos[c+d*x])^(1/2)), x]", //
        "-(A-B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/5*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])-2/15*(A-5*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+2/15*(13*A-5*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:243
  public void test0305() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(7/2)), x]", //
        "1/64*(63*A+13*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])-1/6*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(7/2))-1/16*(5*A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(5/2))-1/192*(103*A+5*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:281
  public void test0306() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "1/2*b*(12*a^2*A*b+A*b^3+8*a^3*B+4*a*b^2*B)*x+a^3*(4*A*b+a*B)*ArcTanh[Sin[c+d*x]]/d-1/3*b*(6*a^3*A-12*a*A*b^2-17*a^2*b*B-2*b^3*B)*Sin[c+d*x]/d-1/6*b^2*(6*a^2*A-3*A*b^2-8*a*b*B)*Cos[c+d*x]*Sin[c+d*x]/d-1/3*b*(3*a*A-b*B)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+a*A*(a+b*Cos[c+d*x])^3*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:299
  public void test0307() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^2, x]", //
        "B*x/b^2-2*(A*b^3+a^3*B-2*a*b^2*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^2*(a+b)^(3/2)*d)+a*(A*b-a*B)*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:315
  public void test0308() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^4, x]", //
        "-(4*a^2*A*b+A*b^3-a^3*B-4*a*b^2*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(7/2)*(a+b)^(7/2)*d)+1/3*a*(A*b-a*B)*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x])^3)+1/6*(2*a^2*A*b+3*A*b^3+a^3*B-6*a*b^2*B)*Sin[c+d*x]/(b*(a^2-b^2)^2*d*(a+b*Cos[c+d*x])^2)+1/6*(2*a^3*A*b+13*a*A*b^3+a^4*B-10*a^2*b^2*B-6*b^4*B)*Sin[c+d*x]/(b*(a^2-b^2)^3*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:331
  public void test0309() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])/(a+b*Cos[c+d*x])^2, x]", //
        "2*B*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:397
  public void test0310() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x])/Cos[c+d*x]^(1/2), x]", //
        "2*(A*b+a*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*a*A+b*B)*EllipticF[1/2*(c+d*x),2]/d+2/3*b*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:413
  public void test0311() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "-2/5*(3*a^3*A+15*a*A*b^2+15*a^2*b*B-5*b^3*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*a^2*A*b+3*A*b^3+a^3*B+9*a*b^2*B)*EllipticF[1/2*(c+d*x),2]/d+2/15*a^2*(9*A*b+5*a*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*a*A*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/5*a*(3*a^2*A+14*A*b^2+15*a*b*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:667
  public void test0312() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])*Sec[c+d*x]^(5/2)/(a+b*Cos[c+d*x]), x]", //
        "2/3*B*Sec[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[Sec[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:33
  public void test0313() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(7/2), x]", //
        "2/5*A*Sin[c+d*x]/(b*d*(b*Cos[c+d*x])^(5/2))+2/5*(3*A+5*C)*Sin[c+d*x]/(b^3*d*Sqrt[b*Cos[c+d*x]])-2/5*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:60
  public void test0314() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:76
  public void test0315() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "2/5*b*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/5*b^2*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:94
  public void test0316() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "2/45*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^3*d)+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b^5*d)+2/15*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:148
  public void test0317() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]), x]", //
        "A*ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:164
  public void test0318() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "A*ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:357
  public void test0319() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "2/3*b^2*(3*A+C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+2*b*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:375
  public void test0320() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[b*Cos[c+d*x]], x]", //
        "2/3*(3*A+C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)+2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:391
  public void test0321() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/3*(3*A+C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/3*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)+2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:411
  public void test0322() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "1/4*b*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+1/8*b*(4*A+3*C)*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+b*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*b*B*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/8*b*(4*A+3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:19
  public void test0323() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "1/8*a*(3*A+4*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(2*A+3*C)*Tan[c+d*x]/d+1/8*a*(3*A+4*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a*A*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:35
  public void test0324() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "3*a^3*C*x+1/2*a^3*(5*A+6*C)*ArcTanh[Sin[c+d*x]]/d-5/2*a^3*A*Sin[c+d*x]/d+1/3*(5*A+3*C)*(a^3+a^3*Cos[c+d*x])*Tan[c+d*x]/d+1/2*A*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/3*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:53
  public void test0325() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "-1/2*(2*A+3*C)*x/a+(3*A+4*C)*Sin[c+d*x]/(a*d)-1/2*(2*A+3*C)*Cos[c+d*x]*Sin[c+d*x]/(a*d)-(A+C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))-1/3*(3*A+4*C)*Sin[c+d*x]^3/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:69
  public void test0326() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^3, x]", //
        "-1/2*(6*A+23*C)*x/a^3+4/5*(9*A+34*C)*Sin[c+d*x]/(a^3*d)-1/2*(6*A+23*C)*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-1/5*(A+C)*Cos[c+d*x]^5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(3*A+13*C)*Cos[c+d*x]^4*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-1/3*(6*A+23*C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))-4/15*(9*A+34*C)*Sin[c+d*x]^3/(a^3*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:85
  public void test0327() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^4, x]", //
        "1/2*(21*A+2*C)*ArcTanh[Sin[c+d*x]]/(a^4*d)-32/105*(54*A+5*C)*Tan[c+d*x]/(a^4*d)+1/2*(21*A+2*C)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d)-1/105*(129*A+10*C)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-16/105*(54*A+5*C)*Sec[c+d*x]*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/5*A*Sec[c+d*x]*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:105
  public void test0328() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/4*a^(3/2)*(7*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/4*a^2*(5*A-8*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/2*A*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]*Tan[c+d*x]/d+3/4*a*A*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:139
  public void test0329() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-1/4*(A+C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(A+17*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+1/16*(19*A+163*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/24*(21*A+197*C)*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+5/48*(3*A+19*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:159
  public void test0330() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "4/5*a^2*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]/d+8/21*a^2*(7*A+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/105*a^2*(35*A+33*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/7*C*(a+a*Cos[c+d*x])^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+8/35*C*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:177
  public void test0331() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "3/5*(5*A+7*C)*EllipticE[1/2*(c+d*x),2]/(a*d)-1/3*(3*A+5*C)*EllipticF[1/2*(c+d*x),2]/(a*d)+1/5*(5*A+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d)-(A+C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))-1/3*(3*A+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:193
  public void test0332() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/((a+a*Cos[c+d*x])^3*Sqrt[Cos[c+d*x]]), x]", //
        "1/10*(9*A-C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(3*A+C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^3)-2/15*(3*A-2*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)-1/10*(9*A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:213
  public void test0333() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(7/2), x]", //
        "2*a^(3/2)*C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/5*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/5*a^2*(4*A+5*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/5*a*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(3/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:231
  public void test0334() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]), x]", //
        "-C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:253
  public void test0335() {
    check( //
        "Integrate[Cos[c+d*x]^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "3/8*B*x+C*Sin[c+d*x]/d+3/8*B*Cos[c+d*x]*Sin[c+d*x]/d+1/4*B*Cos[c+d*x]^3*Sin[c+d*x]/d-2/3*C*Sin[c+d*x]^3/d+1/5*C*Sin[c+d*x]^5/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:269
  public void test0336() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "1/2*a*(B+2*C)*ArcTanh[Sin[c+d*x]]/d+a*(B+C)*Tan[c+d*x]/d+1/2*a*B*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:285
  public void test0337() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/2*a^3*(6*B+7*C)*x+a^3*(3*B+C)*ArcTanh[Sin[c+d*x]]/d+5/2*a^3*C*Sin[c+d*x]/d-1/2*(2*B-C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+a*B*(a+a*Cos[c+d*x])^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:303
  public void test0338() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^2, x]", //
        "C*x/a^2+1/3*(2*B-5*C)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(B-C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:325
  public void test0339() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-(B-C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+2/3*(3*B-2*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/3*C*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:355
  public void test0340() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "C*x+B*ArcTanh[Sin[c+d*x]]/d+A*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:371
  public void test0341() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/2*a^2*(4*A+3*B+2*C)*x+a^2*A*ArcTanh[Sin[c+d*x]]/d+1/2*a^2*(2*A+3*B+2*C)*Sin[c+d*x]/d+1/3*C*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/6*(3*B+2*C)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:387
  public void test0342() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/128*a^4*(392*A+352*B+323*C)*x+1/35*a^4*(252*A+227*B+208*C)*Sin[c+d*x]/d+1/128*a^4*(392*A+352*B+323*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/2240*a^4*(2408*A+2208*B+2007*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/14*a*(2*B+C)*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d+1/8*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^4*Sin[c+d*x]/d+1/336*(56*A+80*B+61*C)*Cos[c+d*x]^3*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d+7/120*(8*A+8*B+7*C)*Cos[c+d*x]^3*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d-1/105*a^4*(252*A+227*B+208*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:405
  public void test0343() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x]), x]", //
        "-(A-B)*ArcTanh[Sin[c+d*x]]/(a*d)+(2*A-B+C)*Tan[c+d*x]/(a*d)-(A-B+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:421
  public void test0344() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^3, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^3*d)-1/5*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(7*A-2*B-3*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-1/15*(22*A-2*B-3*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:441
  public void test0345() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/5*C*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+2/15*a*(15*A+5*B+7*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/15*(5*B-2*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:457
  public void test0346() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "2/1155*a*(165*A+143*B+125*C)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/693*(99*A-22*B+26*C)*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/11*C*Cos[c+d*x]^2*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/99*(11*B+5*C)*(a+a*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(a*d)+64/3465*a^3*(165*A+143*B+125*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+16/3465*a^2*(165*A+143*B+125*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:475
  public void test0347() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(a+a*Cos[c+d*x])^(1/2), x]", //
        "-1/8*(9*A-14*B+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+(A-B+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/8*(7*A-2*B+8*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/12*(A-6*B)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*A*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:491
  public void test0348() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^(5/2), x]", //
        "1/4*(39*A-20*B+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/16*(219*A-115*B+43*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(19*A-11*B+3*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(63*A-35*B+11*C)*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+1/16*(31*A-15*B+7*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:511
  public void test0349() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "4/15*a^2*(12*A+9*B+8*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^2*(7*A+6*B+5*C)*EllipticF[1/2*(c+d*x),2]/d+2/105*a^2*(21*A+27*B+19*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/9*C*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+2/63*(9*B+4*C)*Cos[c+d*x]^(3/2)*(a^2+a^2*Cos[c+d*x])*Sin[c+d*x]/d+4/21*a^2*(7*A+6*B+5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:529
  public void test0350() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x]), x]", //
        "-3/5*(5*A-7*B+7*C)*EllipticE[1/2*(c+d*x),2]/(a*d)+5/21*(7*A-7*B+9*C)*EllipticF[1/2*(c+d*x),2]/(a*d)-1/5*(5*A-7*B+7*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d)+1/7*(7*A-7*B+9*C)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(a*d)-(A-B+C)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+5/21*(7*A-7*B+9*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:545
  public void test0351() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]/(a+a*Cos[c+d*x])^3, x]", //
        "1/10*(A-B-9*C)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/6*(A+B+3*C)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*(A-B+C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(4*A+B-6*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)-1/10*(A-B-9*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:565
  public void test0352() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "a^(3/2)*(2*B+3*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))-1/3*a^2*(8*A+6*B-3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+2*a*(A+B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:583
  public void test0353() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/4*(8*A-4*B+7*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-(A-B+C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/2*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*(4*B-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:599
  public void test0354() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "-1/16*(75*A-19*B-5*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2)*Sqrt[Cos[c+d*x]])-1/16*(13*A-5*B-3*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2)*Sqrt[Cos[c+d*x]])+1/16*(49*A-9*B+C)*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:623
  public void test0355() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "2*a*b*C*x+1/2*(2*A*b^2+a^2*(A+2*C))*ArcTanh[Sin[c+d*x]]/d-1/2*b^2*(A-2*C)*Sin[c+d*x]/d+a*A*b*Tan[c+d*x]/d+1/2*A*(a+b*Cos[c+d*x])^2*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:657
  public void test0356() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+b*Cos[c+d*x]), x]", //
        "1/2*(2*A*b^2+a^2*(A+2*C))*ArcTanh[Sin[c+d*x]]/(a^3*d)-2*b*(A*b^2+a^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^3*d*Sqrt[a-b]*Sqrt[a+b])-A*b*Tan[c+d*x]/(a^2*d)+1/2*A*Sec[c+d*x]*Tan[c+d*x]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:739
  public void test0357() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(3/2)*(a^2-b^2*Cos[c+d*x]^2), x]", //
        "4/35*a*b*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-2/7*b*(a+b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+2/105*b*(41*a^2-25*b^2)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+4/105*a*(73*a^2-41*b^2)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/105*(41*a^4-66*a^2*b^2+25*b^4)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:766
  public void test0358() {
    check( //
        "Integrate[(a^2-b^2*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^(3/2), x]", //
        "-2*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+4*a*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:887
  public void test0359() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/8*(4*a*B+3*b*C)*x+(b*B+a*C)*Sin[c+d*x]/d+1/8*(4*a*B+3*b*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*b*C*Cos[c+d*x]^3*Sin[c+d*x]/d-1/3*(b*B+a*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:904
  public void test0360() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/8*(8*a^3*B+12*a*b^2*B+12*a^2*b*C+3*b^3*C)*x+1/6*(16*a^2*b*B+4*b^3*B+3*a^3*C+12*a*b^2*C)*Sin[c+d*x]/d+1/24*b*(20*a*b*B+6*a^2*C+9*b^2*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*(4*b*B+3*a*C)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*C*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:922
  public void test0361() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^2, x]", //
        "C*x/b^2-2*(b^3*B+a^3*C-2*a*b^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^2*(a+b)^(3/2)*d)+a*(b*B-a*C)*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:945
  public void test0362() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(3/2)*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "2/5*C*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/15*(5*b*B+3*a*C)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+2/15*(20*a*b*B+3*a^2*C+9*b^2*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/15*(a^2-b^2)*(5*b*B+3*a*C)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:983
  public void test0363() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]], x]", //
        "6/5*(b*B+a*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*(7*a*B+5*b*C)*EllipticF[1/2*(c+d*x),2]/d+2/5*(b*B+a*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/7*b*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/21*(7*a*B+5*b*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:999
  public void test0364() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "2/5*(15*a^2*b*B+3*b^3*B+5*a^3*C+9*a*b^2*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*(21*a^3*B+21*a*b^2*B+21*a^2*b*C+5*b^3*C)*EllipticF[1/2*(c+d*x),2]/d+2/35*b^2*(7*b*B+11*a*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/21*b*(21*a*b*B+18*a^2*C+5*b^2*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/7*b*C*(a+b*Cos[c+d*x])^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1125
  public void test0365() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x]), x]", //
        "(b*B-a*C)*x/b^2+C*Sin[c+d*x]/(b*d)+2*(A*b^2-a*(b*B-a*C))*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b^2*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1157
  public void test0366() {
    check( //
        "Integrate[(a*b*B-a^2*C+b^2*B*Cos[c+d*x]+b^2*C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^4, x]", //
        "(2*a^2*b*B+b^3*B-2*a^3*C-4*a*b^2*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(5/2)*(a+b)^(5/2)*d)-1/2*b*(b*B-2*a*C)*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^2)-1/2*b*(3*a*b*B-4*a^2*C-2*b^2*C)*Sin[c+d*x]/((a^2-b^2)^2*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1224
  public void test0367() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "-2*(A*b+a*B-b*C)*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*b*B+a*(A+3*C))*EllipticF[1/2*(c+d*x),2]/d+2/3*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2*(A*b+a*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:15
  public void test0368() {
    check( //
        "Integrate[Sin[x]^5/(a-a*Cos[x]^2), x]", //
        "-Cos[x]/a+1/3*Cos[x]^3/a");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:101
  public void test0369() {
    check( //
        "Integrate[(-1+Cos[x]^2)^(3/2), x]", //
        "-1/3*Cot[x]*(-Sin[x]^2)^(3/2)+2/3*Cot[x]*Sqrt[-Sin[x]^2]");
  }

  // 4.2.7 (d trig)^m (a+b (c cos)^n)^p.input:125
  public void test0370() {
    check( //
        "Integrate[1/(a+b*Cos[x]^2)^(3/2), x]", //
        "-b*Cos[x]*Sin[x]/(a*(a+b)*Sqrt[a+b*Cos[x]^2])+EllipticE[1/2*Pi+x,-b/a]*Sqrt[a+b*Cos[x]^2]/(a*(a+b)*Sqrt[1+b*Cos[x]^2/a])");
  }

  // 4.2.8 (a+b cos)^m (c+d trig)^n.input:50
  public void test0371() {
    check( //
        "Integrate[(A+B*Cos[d+e*x]+C*Sin[d+e*x])/(a+b*Cos[d+e*x])^2, x]", //
        "2*(a*A-b*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(d+e*x)]/Sqrt[a+b]]/((a-b)^(3/2)*(a+b)^(3/2)*e)+C/(b*e*(a+b*Cos[d+e*x]))-(A*b-a*B)*Sin[d+e*x]/((a^2-b^2)*e*(a+b*Cos[d+e*x]))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:46
  public void test0372() {
    check( //
        "Integrate[1/(b*Tan[c+d*x]^2)^(1/2), x]", //
        "Log[Sin[c+d*x]]*Tan[c+d*x]/(d*Sqrt[b*Tan[c+d*x]^2])");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:103
  public void test0373() {
    check( //
        "Integrate[Sin[a+b*x]*(d*Tan[a+b*x])^(3/2), x]", //
        "-3*d^2*EllipticE[-1/4*Pi+a+b*x,2]*Sin[a+b*x]/(b*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]])+2*d*Sin[a+b*x]*Sqrt[d*Tan[a+b*x]]/b");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:121
  public void test0374() {
    check( //
        "Integrate[Csc[a+b*x]^2/(d*Tan[a+b*x])^(1/2), x]", //
        "-2/3*d/(b*(d*Tan[a+b*x])^(3/2))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:137
  public void test0375() {
    check( //
        "Integrate[Csc[a+b*x]^3/(d*Tan[a+b*x])^(3/2), x]", //
        "2/21*Csc[a+b*x]/(b*d*Sqrt[d*Tan[a+b*x]])-2/7*Csc[a+b*x]^3/(b*d*Sqrt[d*Tan[a+b*x]])-2/21*EllipticF[-1/4*Pi+a+b*x,2]*Csc[a+b*x]*Sqrt[Sin[2*a+2*b*x]]*Sqrt[d*Tan[a+b*x]]/(b*d^2)");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:175
  public void test0376() {
    check( //
        "Integrate[(a*Sin[e+f*x])^(13/2)/(b*Tan[e+f*x])^(3/2), x]", //
        "-16/585*a^4*(a*Sin[e+f*x])^(5/2)/(b*f*Sqrt[b*Tan[e+f*x]])-2/117*a^2*(a*Sin[e+f*x])^(9/2)/(b*f*Sqrt[b*Tan[e+f*x]])+2/13*(a*Sin[e+f*x])^(13/2)/(b*f*Sqrt[b*Tan[e+f*x]])-64/585*a^6*Sqrt[a*Sin[e+f*x]]/(b*f*Sqrt[b*Tan[e+f*x]])");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:228
  public void test0377() {
    check( //
        "Integrate[Csc[e+f*x]^6*(b*Tan[e+f*x])^n, x]", //
        "-b^5*(b*Tan[e+f*x])^(-5+n)/(f*(5-n))-2*b^3*(b*Tan[e+f*x])^(-3+n)/(f*(3-n))-b*(b*Tan[e+f*x])^(-1+n)/(f*(1-n))");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:315
  public void test0378() {
    check( //
        "Integrate[Sec[a+b*x]^3*(d*Tan[a+b*x])^(3/2), x]", //
        "-2/21*d^2*EllipticF[-1/4*Pi+a+b*x,2]*Sec[a+b*x]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Tan[a+b*x]])-2/21*d*Sec[a+b*x]*Sqrt[d*Tan[a+b*x]]/b+2/7*d*Sec[a+b*x]^3*Sqrt[d*Tan[a+b*x]]/b");
  }

  // 4.3.0 (a trg)^m (b tan)^n.input:479
  public void test0379() {
    check( //
        "Integrate[Cot[e+f*x]^5*(b*Csc[e+f*x])^m, x]", //
        "-(b*Csc[e+f*x])^m/(f*m)+2*(b*Csc[e+f*x])^(2+m)/(b^2*f*(2+m))-(b*Csc[e+f*x])^(4+m)/(b^4*f*(4+m))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:28
  public void test0380() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+I*a*Tan[c+d*x]), x]", //
        "-1/5*I*a*Cos[c+d*x]^5/d+a*Sin[c+d*x]/d-2/3*a*Sin[c+d*x]^3/d+1/5*a*Sin[c+d*x]^5/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:44
  public void test0381() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^2, x]", //
        "3/5*a^2*Sin[c+d*x]/d-1/5*a^2*Sin[c+d*x]^3/d-2/5*I*Cos[c+d*x]^5*(a^2+I*a^2*Tan[c+d*x])/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:60
  public void test0382() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^3, x]", //
        "-1/15*I*a^3*Cos[c+d*x]^3/d+1/5*a^3*Sin[c+d*x]/d-1/15*a^3*Sin[c+d*x]^3/d-2/5*I*a*Cos[c+d*x]^5*(a+I*a*Tan[c+d*x])^2/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:76
  public void test0383() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+I*a*Tan[c+d*x])^5, x]", //
        "a^5*x-I*a^5*Log[Cos[c+d*x]]/d-2*I*a^7/(d*(a-I*a*Tan[c+d*x])^2)+4*I*a^6/(d*(a-I*a*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:92
  public void test0384() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^8, x]", //
        "128*a^8*x-128*I*a^8*Log[Cos[c+d*x]]/d-64*a^8*Tan[c+d*x]/d+4/5*I*a^3*(a+I*a*Tan[c+d*x])^5/d+1/3*I*a^2*(a+I*a*Tan[c+d*x])^6/d+1/7*I*a*(a+I*a*Tan[c+d*x])^7/d+16/3*I*a^2*(a^2+I*a^2*Tan[c+d*x])^3/d+2*I*(a^2+I*a^2*Tan[c+d*x])^4/d+16*I*(a^4+I*a^4*Tan[c+d*x])^2/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:108
  public void test0385() {
    check( //
        "Integrate[Cos[c+d*x]^13*(a+I*a*Tan[c+d*x])^8, x]", //
        "-20/3003*I*a^3*Cos[c+d*x]^7*(a+I*a*Tan[c+d*x])^5/d-20/1287*I*a^2*Cos[c+d*x]^9*(a+I*a*Tan[c+d*x])^6/d-5/143*I*a*Cos[c+d*x]^11*(a+I*a*Tan[c+d*x])^7/d-1/13*I*Cos[c+d*x]^13*(a+I*a*Tan[c+d*x])^8/d-8/9009*I*a^2*Cos[c+d*x]^3*(a^2+I*a^2*Tan[c+d*x])^3/d-8/3003*I*Cos[c+d*x]^5*(a^2+I*a^2*Tan[c+d*x])^4/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:126
  public void test0386() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+I*a*Tan[c+d*x]), x]", //
        "6/7*Sin[c+d*x]/(a*d)-4/7*Sin[c+d*x]^3/(a*d)+6/35*Sin[c+d*x]^5/(a*d)+1/7*I*Cos[c+d*x]^5/(d*(a+I*a*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:142
  public void test0387() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+I*a*Tan[c+d*x])^2, x]", //
        "7/9*Sin[c+d*x]/(a^2*d)-7/9*Sin[c+d*x]^3/(a^2*d)+7/15*Sin[c+d*x]^5/(a^2*d)-1/9*Sin[c+d*x]^7/(a^2*d)+2/9*I*Cos[c+d*x]^7/(d*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:158
  public void test0388() {
    check( //
        "Integrate[Cos[c+d*x]/(a+I*a*Tan[c+d*x])^3, x]", //
        "12/35*Sin[c+d*x]/(a^3*d)-4/35*Sin[c+d*x]^3/(a^3*d)+1/7*I*Cos[c+d*x]/(d*(a+I*a*Tan[c+d*x])^3)+8/35*I*Cos[c+d*x]^3/(d*(a^3+I*a^3*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:190
  public void test0389() {
    check( //
        "Integrate[Sec[c+d*x]^11/(a+I*a*Tan[c+d*x])^8, x]", //
        "-63/2*ArcTanh[Sin[c+d*x]]/(a^8*d)-63/2*Sec[c+d*x]*Tan[c+d*x]/(a^8*d)+2/5*I*Sec[c+d*x]^9/(a*d*(a+I*a*Tan[c+d*x])^7)-6/5*I*Sec[c+d*x]^7/(a^3*d*(a+I*a*Tan[c+d*x])^5)+42/5*I*Sec[c+d*x]^5/(a^2*d*(a^2+I*a^2*Tan[c+d*x])^3)+42*I*Sec[c+d*x]^3/(d*(a^8+I*a^8*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:210
  public void test0390() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(3/2)*(a+I*a*Tan[c+d*x])^2, x]", //
        "14/15*I*a^2*(e*Sec[c+d*x])^(3/2)/d-14/5*a^2*e^2*EllipticE[1/2*(c+d*x),2]/(d*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]])+14/5*a^2*e*Sin[c+d*x]*Sqrt[e*Sec[c+d*x]]/d+2/5*I*(e*Sec[c+d*x])^(3/2)*(a^2+I*a^2*Tan[c+d*x])/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:227
  public void test0391() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^3/(e*Sec[c+d*x])^(11/2), x]", //
        "10/77*a^3*Sin[c+d*x]/(d*e^5*Sqrt[e*Sec[c+d*x]])+10/77*a^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(d*e^6)-2/11*I*(a+I*a*Tan[c+d*x])^3/(d*(e*Sec[c+d*x])^(11/2))-20/77*I*(a^3+I*a^3*Tan[c+d*x])/(d*e^2*(e*Sec[c+d*x])^(7/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:245
  public void test0392() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(5/2)/(a+I*a*Tan[c+d*x]), x]", //
        "-2*I*e^2*Sqrt[e*Sec[c+d*x]]/(a*d)+2*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(a*d)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:261
  public void test0393() {
    check( //
        "Integrate[1/((e*Sec[c+d*x])^(3/2)*(a+I*a*Tan[c+d*x])^2), x]", //
        "2/11*e*Sin[c+d*x]/(a^2*d*(e*Sec[c+d*x])^(5/2))+10/33*Sin[c+d*x]/(a^2*d*e*Sqrt[e*Sec[c+d*x]])+10/33*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(a^2*d*e^2)+4/11*I*e^2/(d*(e*Sec[c+d*x])^(7/2)*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:277
  public void test0394() {
    check( //
        "Integrate[(e*Sec[c+d*x])^(9/2)/(a+I*a*Tan[c+d*x])^4, x]", //
        "10/21*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[e*Sec[c+d*x]]/(a^4*d)+4/7*I*e^2*(e*Sec[c+d*x])^(5/2)/(a*d*(a+I*a*Tan[c+d*x])^3)-20/21*I*e^4*Sqrt[e*Sec[c+d*x]]/(d*(a^4+I*a^4*Tan[c+d*x]))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:319
  public void test0395() {
    check( //
        "Integrate[Cos[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]], x]", //
        "I*ArcTanh[Sec[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+I*a*Tan[c+d*x]])]*Sqrt[a]/(d*Sqrt[2])-I*Cos[c+d*x]*Sqrt[a+I*a*Tan[c+d*x]]/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:385
  public void test0396() {
    check( //
        "Integrate[Sec[c+d*x]^11/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "256/12155*I*a^4*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(11/2))+64/1105*I*a^3*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(9/2))+8/85*I*a^2*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(7/2))+2/17*I*a*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(5/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:401
  public void test0397() {
    check( //
        "Integrate[Sec[c+d*x]^11/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "64/2145*I*a^3*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(11/2))+16/195*I*a^2*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(9/2))+2/15*I*a*Sec[c+d*x]^11/(d*(a+I*a*Tan[c+d*x])^(7/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:437
  public void test0398() {
    check( //
        "Integrate[Sqrt[e*Sec[c+d*x]]*(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "3*I*a^(3/2)*ArcTan[1-Sqrt[2]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sqrt[e]/(d*Sqrt[2])-3*I*a^(3/2)*ArcTan[1+Sqrt[2]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[a]*Sqrt[e*Sec[c+d*x]])]*Sqrt[e]/(d*Sqrt[2])-3/2*I*a^(3/2)*Log[a-Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a+I*a*Tan[c+d*x])]*Sqrt[e]/(d*Sqrt[2])+3/2*I*a^(3/2)*Log[a+Sqrt[2]*Sqrt[a]*Sqrt[e]*Sqrt[a+I*a*Tan[c+d*x]]/Sqrt[e*Sec[c+d*x]]+Cos[c+d*x]*(a+I*a*Tan[c+d*x])]*Sqrt[e]/(d*Sqrt[2])+I*a*Sqrt[e*Sec[c+d*x]]*Sqrt[a+I*a*Tan[c+d*x]]/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:471
  public void test0399() {
    check( //
        "Integrate[Sqrt[e*Sec[c+d*x]]/(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "16/45*I*Sqrt[e*Sec[c+d*x]]/(a^2*d*Sqrt[a+I*a*Tan[c+d*x]])+2/9*I*Sqrt[e*Sec[c+d*x]]/(d*(a+I*a*Tan[c+d*x])^(5/2))+8/45*I*Sqrt[e*Sec[c+d*x]]/(a*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:570
  public void test0400() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Tan[c+d*x]), x]", //
        "-1/3*b*Cos[c+d*x]^3/d+a*Sin[c+d*x]/d-1/3*a*Sin[c+d*x]^3/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:586
  public void test0401() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+b*Tan[c+d*x])^3, x]", //
        "3/8*a^2*b*Sec[c+d*x]^8/d+a^3*Tan[c+d*x]/d+a*(a^2+b^2)*Tan[c+d*x]^3/d+1/4*b^3*Tan[c+d*x]^4/d+3/5*a*(a^2+3*b^2)*Tan[c+d*x]^5/d+1/2*b^3*Tan[c+d*x]^6/d+1/7*a*(a^2+9*b^2)*Tan[c+d*x]^7/d+3/8*b^3*Tan[c+d*x]^8/d+1/3*a*b^2*Tan[c+d*x]^9/d+1/10*b^3*Tan[c+d*x]^10/d");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:609
  public void test0402() {
    check( //
        "Integrate[Cos[c+d*x]/(a+b*Tan[c+d*x]), x]", //
        "-b^2*ArcTanh[Cos[c+d*x]*(b-a*Tan[c+d*x])/Sqrt[a^2+b^2]]/((a^2+b^2)^(3/2)*d)+b*Cos[c+d*x]/((a^2+b^2)*d)+a*Sin[c+d*x]/((a^2+b^2)*d)");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:649
  public void test0403() {
    check( //
        "Integrate[(d*Sec[e+f*x])^(1/2)*(a+b*Tan[e+f*x])^2, x]", //
        "10/3*a*b*Sqrt[d*Sec[e+f*x]]/f+2/3*(3*a^2-2*b^2)*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[d*Sec[e+f*x]]/f+2/3*b*Sqrt[d*Sec[e+f*x]]*(a+b*Tan[e+f*x])/f");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:720
  public void test0404() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+b*Tan[c+d*x])^n, x]", //
        "(a^2+b^2)*(a+b*Tan[c+d*x])^(1+n)/(b^3*d*(1+n))-2*a*(a+b*Tan[c+d*x])^(2+n)/(b^3*d*(2+n))+(a+b*Tan[c+d*x])^(3+n)/(b^3*d*(3+n))");
  }

  // 4.3.1.2 (d sec)^m (a+b tan)^n.input:755
  public void test0405() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(11/2)*(a+I*a*Tan[c+d*x])^2), x]", //
        "-14/5*EllipticE[1/2*(c+d*x),2]*Cos[c+d*x]^(11/2)/(a^2*d*(e*Cos[c+d*x])^(11/2))+14/15*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d*(e*Cos[c+d*x])^(11/2))+14/5*Cos[c+d*x]^5*Sin[c+d*x]/(a^2*d*(e*Cos[c+d*x])^(11/2))-4/3*I*Cos[c+d*x]^2/(d*(e*Cos[c+d*x])^(11/2)*(a^2+I*a^2*Tan[c+d*x]))");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:16
  public void test0406() {
    check( //
        "Integrate[Sin[x]^2/(I+Tan[x]), x]", //
        "-1/8*I*x+(-1/8*I)/(I-Tan[x])+(-1/8)/(I+Tan[x])^2+(-1/4*I)/(I+Tan[x])");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:38
  public void test0407() {
    check( //
        "Integrate[Csc[c+d*x]^4*(a+b*Tan[c+d*x]), x]", //
        "-a*Cot[c+d*x]/d-1/2*b*Cot[c+d*x]^2/d-1/3*a*Cot[c+d*x]^3/d+b*Log[Tan[c+d*x]]/d");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:54
  public void test0408() {
    check( //
        "Integrate[Csc[c+d*x]*(a+b*Tan[c+d*x])^3, x]", //
        "-a^3*ArcTanh[Cos[c+d*x]]/d+3*a^2*b*ArcTanh[Sin[c+d*x]]/d-1/2*b^3*ArcTanh[Sin[c+d*x]]/d+3*a*b^2*Sec[c+d*x]/d+1/2*b^3*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.3.1.3 (d sin)^m (a+b tan)^n.input:72
  public void test0409() {
    check( //
        "Integrate[Sin[c+d*x]^5/(a+b*Tan[c+d*x]), x]", //
        "a^5*b*ArcTanh[(b*Cos[c+d*x]-a*Sin[c+d*x])/Sqrt[a^2+b^2]]/((a^2+b^2)^(7/2)*d)+a^3*b^2*Cos[c+d*x]/((a^2+b^2)^3*d)+a*b^2*Cos[c+d*x]/((a^2+b^2)^2*d)-a*Cos[c+d*x]/((a^2+b^2)*d)-1/3*a*b^2*Cos[c+d*x]^3/((a^2+b^2)^2*d)+2/3*a*Cos[c+d*x]^3/((a^2+b^2)*d)-1/5*a*Cos[c+d*x]^5/((a^2+b^2)*d)+a^4*b*Sin[c+d*x]/((a^2+b^2)^3*d)+1/3*a^2*b*Sin[c+d*x]^3/((a^2+b^2)^2*d)+1/5*b*Sin[c+d*x]^5/((a^2+b^2)*d)");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:16
  public void test0410() {
    check( //
        "Integrate[x^2*Tan[a+b*x]^2, x]", //
        "-I*x^2/b-1/3*x^3+2*x*Log[1+E^(2*I*(a+b*x))]/b^2-I*PolyLog[2,-E^(2*I*(a+b*x))]/b^3+x^2*Tan[a+b*x]/b");
  }

  // 4.3.10 (c+d x)^m (a+b tan)^n.input:76
  public void test0411() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*Tan[e+f*x])^3, x]", //
        "3/2*I*b^3*d*(c+d*x)^2/f^2-3*I*a*b^2*(c+d*x)^3/f+1/2*b^3*(c+d*x)^3/f+1/4*a^3*(c+d*x)^4/d+3/4*I*a^2*b*(c+d*x)^4/d-3/4*a*b^2*(c+d*x)^4/d-1/4*I*b^3*(c+d*x)^4/d-3*b^3*d^2*(c+d*x)*Log[1+E^(2*I*(e+f*x))]/f^3+9*a*b^2*d*(c+d*x)^2*Log[1+E^(2*I*(e+f*x))]/f^2-3*a^2*b*(c+d*x)^3*Log[1+E^(2*I*(e+f*x))]/f+b^3*(c+d*x)^3*Log[1+E^(2*I*(e+f*x))]/f+3/2*I*b^3*d^3*PolyLog[2,-E^(2*I*(e+f*x))]/f^4-9*I*a*b^2*d^2*(c+d*x)*PolyLog[2,-E^(2*I*(e+f*x))]/f^3+9/2*I*a^2*b*d*(c+d*x)^2*PolyLog[2,-E^(2*I*(e+f*x))]/f^2-3/2*I*b^3*d*(c+d*x)^2*PolyLog[2,-E^(2*I*(e+f*x))]/f^2+9/2*a*b^2*d^3*PolyLog[3,-E^(2*I*(e+f*x))]/f^4-9/2*a^2*b*d^2*(c+d*x)*PolyLog[3,-E^(2*I*(e+f*x))]/f^3+3/2*b^3*d^2*(c+d*x)*PolyLog[3,-E^(2*I*(e+f*x))]/f^3-9/4*I*a^2*b*d^3*PolyLog[4,-E^(2*I*(e+f*x))]/f^4+3/4*I*b^3*d^3*PolyLog[4,-E^(2*I*(e+f*x))]/f^4-3/2*b^3*d*(c+d*x)^2*Tan[e+f*x]/f^2+3*a*b^2*(c+d*x)^3*Tan[e+f*x]/f+1/2*b^3*(c+d*x)^3*Tan[e+f*x]^2/f");
  }

  // 4.3.11 (e x)^m (a+b tan(c+d x^n))^p.input:32
  public void test0412() {
    check( //
        "Integrate[x/(a+b*Tan[c+d*x^2])^2, x]", //
        "1/2*(a^2-b^2)*x^2/(a^2+b^2)^2+a*b*Log[a*Cos[c+d*x^2]+b*Sin[c+d*x^2]]/((a^2+b^2)^2*d)-1/2*b/((a^2+b^2)*d*(a+b*Tan[c+d*x^2]))");
  }

  // 4.3.11 (e x)^m (a+b tan(c+d x^n))^p.input:69
  public void test0413() {
    check( //
        "Integrate[x*(a+b*Tan[c+d*x^(1/3)]), x]", //
        "1/2*a*x^2+1/2*I*b*x^2-3*b*x^(5/3)*Log[1+E^(2*I*(c+d*x^(1/3)))]/d+15/2*I*b*x^(4/3)*PolyLog[2,-E^(2*I*(c+d*x^(1/3)))]/d^2-15*b*x*PolyLog[3,-E^(2*I*(c+d*x^(1/3)))]/d^3-45/2*I*b*x^(2/3)*PolyLog[4,-E^(2*I*(c+d*x^(1/3)))]/d^4+45/2*b*x^(1/3)*PolyLog[5,-E^(2*I*(c+d*x^(1/3)))]/d^5+45/4*I*b*PolyLog[6,-E^(2*I*(c+d*x^(1/3)))]/d^6");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:33
  public void test0414() {
    check( //
        "Integrate[Cot[c+d*x]^5*(a+I*a*Tan[c+d*x])^2, x]", //
        "2*I*a^2*x+2*I*a^2*Cot[c+d*x]/d+a^2*Cot[c+d*x]^2/d-2/3*I*a^2*Cot[c+d*x]^3/d-1/4*a^2*Cot[c+d*x]^4/d+2*a^2*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:49
  public void test0415() {
    check( //
        "Integrate[Cot[c+d*x]*(a+I*a*Tan[c+d*x])^4, x]", //
        "8*I*a^4*x+7*a^4*Log[Cos[c+d*x]]/d+a^4*Log[Sin[c+d*x]]/d-1/2*(a^2+I*a^2*Tan[c+d*x])^2/d-3*(a^4+I*a^4*Tan[c+d*x])/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:67
  public void test0416() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+I*a*Tan[c+d*x]), x]", //
        "3/2*I*x/a+3/2*I*Cot[c+d*x]/(a*d)-Cot[c+d*x]^2/(a*d)-2*Log[Sin[c+d*x]]/(a*d)+1/2*Cot[c+d*x]^2/(d*(a+I*a*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:83
  public void test0417() {
    check( //
        "Integrate[Tan[c+d*x]^2/(a+I*a*Tan[c+d*x])^3, x]", //
        "-1/8*x/a^3+(-1/6*I)/(d*(a+I*a*Tan[c+d*x])^3)+3/8*I/(a*d*(a+I*a*Tan[c+d*x])^2)+(-1/8*I)/(d*(a^3+I*a^3*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:103
  public void test0418() {
    check( //
        "Integrate[Sqrt[a+I*a*Tan[c+d*x]]*Tan[c+d*x]^2, x]", //
        "I*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]*Sqrt[a]/d-2/3*I*(a+I*a*Tan[c+d*x])^(3/2)/(a*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:119
  public void test0419() {
    check( //
        "Integrate[(a+I*a*Tan[c+d*x])^(5/2), x]", //
        "-4*I*a^(5/2)*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]*Sqrt[2]/d+4*I*a^2*Sqrt[a+I*a*Tan[c+d*x]]/d+2/3*I*a*(a+I*a*Tan[c+d*x])^(3/2)/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:137
  public void test0420() {
    check( //
        "Integrate[Tan[c+d*x]^4/(a+I*a*Tan[c+d*x])^(3/2), x]", //
        "-1/2*I*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(3/2)*d*Sqrt[2])-10*I*Sqrt[a+I*a*Tan[c+d*x]]/(a^2*d)+5/2*I*Tan[c+d*x]^2/(a*d*Sqrt[a+I*a*Tan[c+d*x]])-1/3*Tan[c+d*x]^3/(d*(a+I*a*Tan[c+d*x])^(3/2))+7/2*I*(a+I*a*Tan[c+d*x])^(3/2)/(a^3*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:153
  public void test0421() {
    check( //
        "Integrate[1/(a+I*a*Tan[c+d*x])^(7/2), x]", //
        "-1/8*I*ArcTanh[Sqrt[a+I*a*Tan[c+d*x]]/(Sqrt[2]*Sqrt[a])]/(a^(7/2)*d*Sqrt[2])+1/8*I/(a^3*d*Sqrt[a+I*a*Tan[c+d*x]])+1/7*I/(d*(a+I*a*Tan[c+d*x])^(7/2))+1/10*I/(a*d*(a+I*a*Tan[c+d*x])^(5/2))+1/12*I/(a^2*d*(a+I*a*Tan[c+d*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:173
  public void test0422() {
    check( //
        "Integrate[(d*Tan[e+f*x])^(3/2)*(a+I*a*Tan[e+f*x])^2, x]", //
        "4*(-1)^(1/4)*a^2*d^(3/2)*ArcTan[(-1)^(3/4)*Sqrt[d*Tan[e+f*x]]/Sqrt[d]]/f+4*a^2*d*Sqrt[d*Tan[e+f*x]]/f+4/3*I*a^2*(d*Tan[e+f*x])^(3/2)/f-2/5*a^2*(d*Tan[e+f*x])^(5/2)/(d*f)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:409
  public void test0423() {
    check( //
        "Integrate[(d*Tan[e+f*x])^(5/2)*(a+a*Tan[e+f*x])^3, x]", //
        "-2*a^3*d^(5/2)*ArcTan[(Sqrt[d]-Sqrt[d]*Tan[e+f*x])/(Sqrt[2]*Sqrt[d*Tan[e+f*x]])]*Sqrt[2]/f-4*a^3*d^2*Sqrt[d*Tan[e+f*x]]/f-4/3*a^3*d*(d*Tan[e+f*x])^(3/2)/f+4/5*a^3*(d*Tan[e+f*x])^(5/2)/f+40/63*a^3*(d*Tan[e+f*x])^(7/2)/(d*f)+2/9*(d*Tan[e+f*x])^(7/2)*(a^3+a^3*Tan[e+f*x])/(d*f)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:492
  public void test0424() {
    check( //
        "Integrate[Tan[c+d*x]^2*(a+b*Tan[c+d*x]), x]", //
        "-a*x+b*Log[Cos[c+d*x]]/d+a*Tan[c+d*x]/d+1/2*b*Tan[c+d*x]^2/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:508
  public void test0425() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Tan[c+d*x])^2, x]", //
        "-2*a*b*x-2*a*b*Cot[c+d*x]/d-1/2*a^2*Cot[c+d*x]^2/d-(a^2-b^2)*Log[Sin[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:524
  public void test0426() {
    check( //
        "Integrate[Tan[c+d*x]*(a+b*Tan[c+d*x])^4, x]", //
        "-4*a*b*(a^2-b^2)*x-(a^4-6*a^2*b^2+b^4)*Log[Cos[c+d*x]]/d+a*b*(a^2-3*b^2)*Tan[c+d*x]/d+1/2*(a^2-b^2)*(a+b*Tan[c+d*x])^2/d+1/3*a*(a+b*Tan[c+d*x])^3/d+1/4*(a+b*Tan[c+d*x])^4/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:543
  public void test0427() {
    check( //
        "Integrate[Cot[c+d*x]^2/(a+b*Tan[c+d*x]), x]", //
        "-a*x/(a^2+b^2)-Cot[c+d*x]/(a*d)-b*Log[Sin[c+d*x]]/(a^2*d)+b^3*Log[a*Cos[c+d*x]+b*Sin[c+d*x]]/(a^2*(a^2+b^2)*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:560
  public void test0428() {
    check( //
        "Integrate[Tan[c+d*x]^2/(a+b*Tan[c+d*x])^3, x]", //
        "-a*(a^2-3*b^2)*x/(a^2+b^2)^3-b*(3*a^2-b^2)*Log[a*Cos[c+d*x]+b*Sin[c+d*x]]/((a^2+b^2)^3*d)-1/2*a^2/(b*(a^2+b^2)*d*(a+b*Tan[c+d*x])^2)+2*a*b/((a^2+b^2)^2*d*(a+b*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:576
  public void test0429() {
    check( //
        "Integrate[1/(3+5*Tan[c+d*x])^3, x]", //
        "-99/19652*x+5/19652*Log[3*Cos[c+d*x]+5*Sin[c+d*x]]/d+(-5/68)/(d*(3+5*Tan[c+d*x])^2)+(-15/578)/(d*(3+5*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:596
  public void test0430() {
    check( //
        "Integrate[Tan[c+d*x]^2*(a+b*Tan[c+d*x])^(3/2), x]", //
        "I*(a-I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/d-I*(a+I*b)^(3/2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/d-2*b*Sqrt[a+b*Tan[c+d*x]]/d+2/5*(a+b*Tan[c+d*x])^(5/2)/(b*d)");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:630
  public void test0431() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+b*Tan[c+d*x])^(3/2), x]", //
        "1/4*(8*a^2-15*b^2)*ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a]]/(a^(7/2)*d)-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a-I*b]]/((a-I*b)^(3/2)*d)-ArcTanh[Sqrt[a+b*Tan[c+d*x]]/Sqrt[a+I*b]]/((a+I*b)^(3/2)*d)+1/4*b^2*(7*a^2+15*b^2)/(a^3*(a^2+b^2)*d*Sqrt[a+b*Tan[c+d*x]])+5/4*b*Cot[c+d*x]/(a^2*d*Sqrt[a+b*Tan[c+d*x]])-1/2*Cot[c+d*x]^2/(a*d*Sqrt[a+b*Tan[c+d*x]])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:650
  public void test0432() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])/Tan[c+d*x]^(7/2), x]", //
        "-(a-b)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])+(a-b)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])+1/2*(a+b)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])-1/2*(a+b)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])+2*a/(d*Sqrt[Tan[c+d*x]])-2/5*a/(d*Tan[c+d*x]^(5/2))-2/3*b/(d*Tan[c+d*x]^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:666
  public void test0433() {
    check( //
        "Integrate[(a+b*Tan[c+d*x])^3/Tan[c+d*x]^(11/2), x]", //
        "(a+b)*(a^2-4*a*b+b^2)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-(a+b)*(a^2-4*a*b+b^2)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/(d*Sqrt[2])-1/2*(a-b)*(a^2+4*a*b+b^2)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])+1/2*(a-b)*(a^2+4*a*b+b^2)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/(d*Sqrt[2])-2*a*(a^2-3*b^2)/(d*Sqrt[Tan[c+d*x]])-40/63*a^2*b/(d*Tan[c+d*x]^(7/2))+2/5*a*(a^2-3*b^2)/(d*Tan[c+d*x]^(5/2))+2/3*b*(3*a^2-b^2)/(d*Tan[c+d*x]^(3/2))-2/9*a^2*(a+b*Tan[c+d*x])/(d*Tan[c+d*x]^(9/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:684
  public void test0434() {
    check( //
        "Integrate[Tan[c+d*x]^(5/2)/(a+b*Tan[c+d*x])^2, x]", //
        "a^(3/2)*(a^2+5*b^2)*ArcTan[Sqrt[b]*Sqrt[Tan[c+d*x]]/Sqrt[a]]/(b^(3/2)*(a^2+b^2)^2*d)+(a^2+2*a*b-b^2)*ArcTan[1-Sqrt[2]*Sqrt[Tan[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])-(a^2+2*a*b-b^2)*ArcTan[1+Sqrt[2]*Sqrt[Tan[c+d*x]]]/((a^2+b^2)^2*d*Sqrt[2])-1/2*(a^2-2*a*b-b^2)*Log[1-Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/((a^2+b^2)^2*d*Sqrt[2])+1/2*(a^2-2*a*b-b^2)*Log[1+Sqrt[2]*Sqrt[Tan[c+d*x]]+Tan[c+d*x]]/((a^2+b^2)^2*d*Sqrt[2])-a^2*Sqrt[Tan[c+d*x]]/(b*(a^2+b^2)*d*(a+b*Tan[c+d*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:754
  public void test0435() {
    check( //
        "Integrate[1/(Sqrt[2-3*Tan[c+d*x]]*Sqrt[Tan[c+d*x]]), x]", //
        "ArcTan[Sqrt[3-2*I]*Sqrt[Tan[c+d*x]]/Sqrt[2-3*Tan[c+d*x]]]/(d*Sqrt[3-2*I])+ArcTan[Sqrt[3+2*I]*Sqrt[Tan[c+d*x]]/Sqrt[2-3*Tan[c+d*x]]]/(d*Sqrt[3+2*I])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:955
  public void test0436() {
    check( //
        "Integrate[Cot[c+d*x]^(3/2)*(a+b*Tan[c+d*x])^2, x]", //
        "-(a^2-2*a*b-b^2)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+(a^2-2*a*b-b^2)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-1/2*(a^2+2*a*b-b^2)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])+1/2*(a^2+2*a*b-b^2)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/(d*Sqrt[2])-2*a^2*Sqrt[Cot[c+d*x]]/d");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:973
  public void test0437() {
    check( //
        "Integrate[1/(Cot[c+d*x]^(3/2)*(a+b*Tan[c+d*x])), x]", //
        "-(a-b)*ArcTan[1-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+(a-b)*ArcTan[1+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])+1/2*(a+b)*Log[1+Cot[c+d*x]-Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])-1/2*(a+b)*Log[1+Cot[c+d*x]+Sqrt[2]*Sqrt[Cot[c+d*x]]]/((a^2+b^2)*d*Sqrt[2])-2*a^(3/2)*ArcTan[Sqrt[a]*Sqrt[Cot[c+d*x]]/Sqrt[b]]/((a^2+b^2)*d*Sqrt[b])");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1067
  public void test0438() {
    check( //
        "Integrate[(c-I*c*Tan[e+f*x])^2/(a+I*a*Tan[e+f*x]), x]", //
        "-c^2*x/a-I*c^2*Log[Cos[e+f*x]]/(a*f)+2*I*c^2/(f*(a+I*a*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1083
  public void test0439() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^3*(c-I*c*Tan[e+f*x])^4, x]", //
        "-1/6*I*a^3*c^4*Sec[e+f*x]^6/f+a^3*c^4*Tan[e+f*x]/f+2/3*a^3*c^4*Tan[e+f*x]^3/f+1/5*a^3*c^4*Tan[e+f*x]^5/f");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1101
  public void test0440() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^3/(c-I*c*Tan[e+f*x])^2, x]", //
        "a^3*x/c^2-I*a^3*Log[Cos[e+f*x]]/(c^2*f)-2*I*a^3/(f*(c-I*c*Tan[e+f*x])^2)+4*I*a^3/(f*(c^2-I*c^2*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1117
  public void test0441() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^5/(c-I*c*Tan[e+f*x])^4, x]", //
        "a^5*x/c^4-I*a^5*Log[Cos[e+f*x]]/(c^4*f)-4*I*a^5/(f*(c-I*c*Tan[e+f*x])^4)-12*I*a^5/(f*(c^2-I*c^2*Tan[e+f*x])^2)+32/3*I*a^5*c^5/(f*(c^3-I*c^3*Tan[e+f*x])^3)+8*I*a^5/(f*(c^4-I*c^4*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1137
  public void test0442() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])*(c-I*c*Tan[e+f*x])^(3/2), x]", //
        "2/3*I*a*(c-I*c*Tan[e+f*x])^(3/2)/f");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1155
  public void test0443() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^3/(c-I*c*Tan[e+f*x])^(3/2), x]", //
        "8*I*a^3/(c*f*Sqrt[c-I*c*Tan[e+f*x]])+2*I*a^3*Sqrt[c-I*c*Tan[e+f*x]]/(c^2*f)-8/3*I*a^3/(f*(c-I*c*Tan[e+f*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1175
  public void test0444() {
    check( //
        "Integrate[Sqrt[c-I*c*Tan[e+f*x]]/(a+I*a*Tan[e+f*x])^(3/2), x]", //
        "1/3*I*Sqrt[c-I*c*Tan[e+f*x]]/(a*f*Sqrt[a+I*a*Tan[e+f*x]])+1/3*I*Sqrt[c-I*c*Tan[e+f*x]]/(f*(a+I*a*Tan[e+f*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1209
  public void test0445() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^(1/2)/(c-I*c*Tan[e+f*x])^(3/2), x]", //
        "-1/3*I*Sqrt[a+I*a*Tan[e+f*x]]/(c*f*Sqrt[c-I*c*Tan[e+f*x]])-1/3*I*Sqrt[a+I*a*Tan[e+f*x]]/(f*(c-I*c*Tan[e+f*x])^(3/2))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1227
  public void test0446() {
    check( //
        "Integrate[(a+I*a*Tan[e+f*x])^3*(c-I*c*Tan[e+f*x])^n, x]", //
        "4*I*a^3*(c-I*c*Tan[e+f*x])^n/(f*n)-4*I*a^3*(c-I*c*Tan[e+f*x])^(1+n)/(c*f*(1+n))+I*a^3*(c-I*c*Tan[e+f*x])^(2+n)/(c^2*f*(2+n))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1321
  public void test0447() {
    check( //
        "Integrate[1/(Sqrt[c+d*Tan[e+f*x]]*(a+I*a*Tan[e+f*x])), x]", //
        "1/2*(I*c-2*d)*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c+I*d]]/(a*(c+I*d)^(3/2)*f)-1/2*I*ArcTanh[Sqrt[c+d*Tan[e+f*x]]/Sqrt[c-I*d]]/(a*f*Sqrt[c-I*d])-1/2*Sqrt[c+d*Tan[e+f*x]]/((I*c-d)*f*(a+I*a*Tan[e+f*x]))");
  }

  // 4.3.2.1 (a+b tan)^m (c+d tan)^n.input:1341
  public void test0448() {
    check( //
        "Integrate[Sqrt[c+d*Tan[e+f*x]]*(a+I*a*Tan[e+f*x])^(3/2), x]", //
        "-2*I*a^(3/2)*ArcTanh[Sqrt[2]*Sqrt[a]*Sqrt[c+d*Tan[e+f*x]]/(Sqrt[c-I*d]*Sqrt[a+I*a*Tan[e+f*x]])]*Sqrt[2]*Sqrt[c-I*d]/f-(-1)^(1/4)*a^(3/2)*(I*c+3*d)*ArcTanh[(-1)^(3/4)*Sqrt[d]*Sqrt[a+I*a*Tan[e+f*x]]/(Sqrt[a]*Sqrt[c+d*Tan[e+f*x]])]/(f*Sqrt[d])+a^2*(c+I*d)*Sqrt[c+d*Tan[e+f*x]]/(d*f*Sqrt[a+I*a*Tan[e+f*x]])-a^2*(c+d*Tan[e+f*x])^(3/2)/(d*f*Sqrt[a+I*a*Tan[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:23
  public void test0449() {
    check( //
        "Integrate[Sin[b*x]^(1/2), x]", //
        "-2*EllipticE[1/4*Pi-1/2*b*x,2]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:80
  public void test0450() {
    check( //
        "Integrate[Sec[a+b*x]^2*Sin[a+b*x]^2, x]", //
        "-x+Tan[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:96
  public void test0451() {
    check( //
        "Integrate[Cos[a+b*x]^2*Sin[a+b*x]^3, x]", //
        "-1/3*Cos[a+b*x]^3/b+1/5*Cos[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:112
  public void test0452() {
    check( //
        "Integrate[Sec[a+b*x]^4*Sin[a+b*x]^4, x]", //
        "x-Tan[a+b*x]/b+1/3*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:128
  public void test0453() {
    check( //
        "Integrate[Cos[a+b*x]^4*Sin[a+b*x]^5, x]", //
        "-1/5*Cos[a+b*x]^5/b+2/7*Cos[a+b*x]^7/b-1/9*Cos[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:144
  public void test0454() {
    check( //
        "Integrate[Sec[a+b*x]^13*Sin[a+b*x]^5, x]", //
        "1/8*Sec[a+b*x]^8/b-1/5*Sec[a+b*x]^10/b+1/12*Sec[a+b*x]^12/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:162
  public void test0455() {
    check( //
        "Integrate[Cos[a+b*x]^7/Sin[a+b*x]^2, x]", //
        "-Csc[a+b*x]/b-3*Sin[a+b*x]/b+Sin[a+b*x]^3/b-1/5*Sin[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:178
  public void test0456() {
    check( //
        "Integrate[Cos[a+b*x]^3/Sin[a+b*x]^3, x]", //
        "-1/2*Cot[a+b*x]^2/b-Log[Sin[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:194
  public void test0457() {
    check( //
        "Integrate[Cos[a+b*x]/Sin[a+b*x]^4, x]", //
        "-1/3*Csc[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:230
  public void test0458() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]^2, x]", //
        "-2/7*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]/(b*d)+4/21*d^2*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])+4/21*d*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:246
  public void test0459() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]^4, x]", //
        "8/195*d*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/b-4/39*(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]/(b*d)-2/13*(d*Cos[a+b*x])^(7/2)*Sin[a+b*x]^3/(b*d)+8/65*d^2*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:308
  public void test0460() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(1/2)/(d*Cos[a+b*x])^(7/2), x]", //
        "2/5*(c*Sin[a+b*x])^(3/2)/(b*c*d*(d*Cos[a+b*x])^(5/2))+4/5*(c*Sin[a+b*x])^(3/2)/(b*c*d^3*Sqrt[d*Cos[a+b*x]])-4/5*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*d^4*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:324
  public void test0461() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(5/2)*(c*Sin[a+b*x])^(5/2), x]", //
        "1/10*c*d*(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2)/b-1/5*c*(d*Cos[a+b*x])^(7/2)*(c*Sin[a+b*x])^(3/2)/(b*d)+3/20*c^2*d^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:342
  public void test0462() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(1/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:445
  public void test0463() {
    check( //
        "Integrate[Sin[e+f*x]*Sqrt[b*Sec[e+f*x]], x]", //
        "-2*b/(f*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:477
  public void test0464() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(5/2)*Sin[e+f*x]^2, x]", //
        "2/3*b*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]/f-4/3*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:495
  public void test0465() {
    check( //
        "Integrate[Csc[e+f*x]^4/Sqrt[b*Sec[e+f*x]], x]", //
        "-1/2*b*Csc[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-1/3*b*Csc[e+f*x]^3/(f*(b*Sec[e+f*x])^(3/2))-1/2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:549
  public void test0466() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(13/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2/11*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(11/2))-16/77*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(7/2))-64/231*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(3/2))");
  }

  // 4.1.1.1 (a+b sin)^n.input:13
  public void test0467() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2), x]", //
        "-2/5*a*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-64/15*a^3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-16/15*a^2*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:14
  public void test0468() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "(a+a*Sin[c+d*x])^4/(a^3*d)-4/5*(a+a*Sin[c+d*x])^5/(a^4*d)+1/6*(a+a*Sin[c+d*x])^6/(a^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:31
  public void test0469() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "-a^2*x+2*a^4*Cos[c+d*x]/(d*(a^2-a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:65
  public void test0470() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "-2/3*(a-a*Sin[c+d*x])^3/(a^4*d)+1/4*(a-a*Sin[c+d*x])^4/(a^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:97
  public void test0471() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "-1/7*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^3)-4/35*Sec[c+d*x]/(a*d*(a+a*Sin[c+d*x])^2)-4/35*Sec[c+d*x]/(d*(a^3+a^3*Sin[c+d*x]))+8/35*Tan[c+d*x]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:183
  public void test0472() {
    check( //
        "Integrate[Sec[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-3/4*a*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-3/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2]*Sqrt[a])+Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:219
  public void test0473() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+a*Sin[c+d*x]), x]", //
        "-2/9*a*(e*Cos[c+d*x])^(9/2)/(d*e)+2/7*a*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+10/21*a*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+10/21*a*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:235
  public void test0474() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/(e*Cos[c+d*x])^(9/2), x]", //
        "2/7*a^2*Sin[c+d*x]/(d*e^3*(e*Cos[c+d*x])^(3/2))+4/7*(a^2+a^2*Sin[c+d*x])/(d*e*(e*Cos[c+d*x])^(7/2))+2/7*a^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^4*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:251
  public void test0475() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(5/2), x]", //
        "4/3*a^7*(e*Cos[c+d*x])^(9/2)/(d*e^7*(a-a*Sin[c+d*x])^3)+12*a^8*(e*Cos[c+d*x])^(5/2)/(d*e^5*(a^4-a^4*Sin[c+d*x]))-10*a^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])-10*a^4*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(d*e^3)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:269
  public void test0476() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(9/2)/(a+a*Sin[c+d*x])^2, x]", //
        "14/15*e^3*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a^2*d)+4/3*e*(e*Cos[c+d*x])^(7/2)/(d*(a^2+a^2*Sin[c+d*x]))+14/5*e^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:285
  public void test0477() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]/(a+a*Sin[c+d*x])^3, x]", //
        "-2/9*(e*Cos[c+d*x])^(3/2)/(d*e*(a+a*Sin[c+d*x])^3)-2/15*(e*Cos[c+d*x])^(3/2)/(a*d*e*(a+a*Sin[c+d*x])^2)-2/15*(e*Cos[c+d*x])^(3/2)/(d*e*(a^3+a^3*Sin[c+d*x]))-2/15*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:321
  public void test0478() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)/(e*Cos[c+d*x])^(3/2), x]", //
        "4*a*(a+a*Sin[c+d*x])^(3/2)/(d*e*Sqrt[e*Cos[c+d*x]])+5*a^3*(e*Cos[c+d*x])^(3/2)/(d*e^3*Sqrt[a+a*Sin[c+d*x]])-5*a^2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(3/2)*(1+Cos[c+d*x]+Sin[c+d*x]))-5*a^2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(3/2)*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:428
  public void test0479() {
    check( //
        "Integrate[Sec[c+d*x]^4*(a+b*Sin[c+d*x]), x]", //
        "1/3*b*Sec[c+d*x]^3/d+a*Tan[c+d*x]/d+1/3*a*Tan[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:444
  public void test0480() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x])^3, x]", //
        "-1/4*(a^2-b^2)*(a+b*Sin[c+d*x])^4/(b^3*d)+2/5*a*(a+b*Sin[c+d*x])^5/(b^3*d)-1/6*(a+b*Sin[c+d*x])^6/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:478
  public void test0481() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "a*x/b^2+Cos[c+d*x]/(b*d)-2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(b^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:600
  public void test0482() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])/(e*Cos[c+d*x])^(5/2), x]", //
        "2/3*b/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*a*Sin[c+d*x]/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*a*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:616
  public void test0483() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3/(e*Cos[c+d*x])^(5/2), x]", //
        "2/3*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*a*(a^2-6*b^2)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])+2/3*b*(a^2+4*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e^3)+2/3*a*b*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e^3)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:27
  public void test0484() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2*Tan[c+d*x], x]", //
        "-2*a^2*Log[1-Sin[c+d*x]]/d-2*a^2*Sin[c+d*x]/d-1/2*a^2*Sin[c+d*x]^2/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:43
  public void test0485() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3, x]", //
        "5/2*a^3*x-4*a^3*Cos[c+d*x]/d+1/3*a^3*Cos[c+d*x]^3/d-3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:62
  public void test0486() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "Csc[c+d*x]/(a*d)-1/2*Csc[c+d*x]^2/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:78
  public void test0487() {
    check( //
        "Integrate[Cot[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "Log[Sin[c+d*x]]/(a^2*d)-Log[1+Sin[c+d*x]]/(a^2*d)+1/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:94
  public void test0488() {
    check( //
        "Integrate[Cot[c+d*x]^13/(a+a*Sin[c+d*x])^3, x]", //
        "-1/3*Csc[c+d*x]^3/(a^3*d)+3/4*Csc[c+d*x]^4/(a^3*d)-4/3*Csc[c+d*x]^6/(a^3*d)+6/7*Csc[c+d*x]^7/(a^3*d)+3/4*Csc[c+d*x]^8/(a^3*d)-8/9*Csc[c+d*x]^9/(a^3*d)+3/11*Csc[c+d*x]^11/(a^3*d)-1/12*Csc[c+d*x]^12/(a^3*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:117
  public void test0489() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*Tan[e+f*x]^2, x]", //
        "9/5*Sec[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/f-2/5*Sec[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*f)+124/15*a^3*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+31/15*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:177
  public void test0490() {
    check( //
        "Integrate[Cot[c+d*x]*(a+b*Sin[c+d*x]), x]", //
        "a*Log[Sin[c+d*x]]/d+b*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:227
  public void test0491() {
    check( //
        "Integrate[Cot[c+d*x]^4/(a+b*Sin[c+d*x])^2, x]", //
        "-b*(3*a^2-4*b^2)*ArcTanh[Cos[c+d*x]]/(a^5*d)+1/3*(7*a^2-12*b^2)*Cot[c+d*x]/(a^4*d)-(a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a^3*b*d)+1/3*(3*a^2-4*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a^2*b*d*(a+b*Sin[c+d*x]))-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a*d*(a+b*Sin[c+d*x]))+2*(a^4-5*a^2*b^2+4*b^4)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^5*d*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:31
  public void test0492() {
    check( //
        "Integrate[Sin[a+b*x]^3/(c+d*x), x]", //
        "3/4*Cos[a-b*c/d]*SinIntegral[b*c/d+b*x]/d-1/4*Cos[3*a-3*b*c/d]*SinIntegral[3*b*c/d+3*b*x]/d-1/4*CosIntegral[3*b*c/d+3*b*x]*Sin[3*a-3*b*c/d]/d+3/4*CosIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:84
  public void test0493() {
    check( //
        "Integrate[x^4*(a+b*Sin[c+d*x^3]), x]", //
        "1/5*a*x^5-1/3*b*x^2*Cos[c+d*x^3]/d-1/9*E^(I*c)*b*x^2*Gamma[2/3,-I*d*x^3]/(d*(-I*d*x^3)^(2/3))-1/9*b*x^2*Gamma[2/3,I*d*x^3]/(E^(I*c)*d*(I*d*x^3)^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:143
  public void test0494() {
    check( //
        "Integrate[Sin[a+b/x]/x^3, x]", //
        "Cos[a+b/x]/(b*x)-Sin[a+b/x]/b^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:191
  public void test0495() {
    check( //
        "Integrate[x^(-1+2*n)*Cos[a+b*x^n], x]", //
        "Cos[a+b*x^n]/(b^2*n)+x^n*Sin[a+b*x^n]/(b*n)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:255
  public void test0496() {
    check( //
        "Integrate[Sin[a+b*Sqrt[c+d*x]]/(e+f*x), x]", //
        "-Cos[a+b*Sqrt[-d*e+c*f]/Sqrt[f]]*SinIntegral[b*Sqrt[-d*e+c*f]/Sqrt[f]-b*Sqrt[c+d*x]]/f+Cos[a-b*Sqrt[-d*e+c*f]/Sqrt[f]]*SinIntegral[b*Sqrt[-d*e+c*f]/Sqrt[f]+b*Sqrt[c+d*x]]/f+CosIntegral[b*Sqrt[-d*e+c*f]/Sqrt[f]+b*Sqrt[c+d*x]]*Sin[a-b*Sqrt[-d*e+c*f]/Sqrt[f]]/f+CosIntegral[b*Sqrt[-d*e+c*f]/Sqrt[f]-b*Sqrt[c+d*x]]*Sin[a+b*Sqrt[-d*e+c*f]/Sqrt[f]]/f");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:307
  public void test0497() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(1/3)]/(c*e+d*e*x)^(1/3), x]", //
        "-3*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d*(e*(c+d*x))^(1/3))+3*(c+d*x)^(1/3)*Sin[a+b*(c+d*x)^(1/3)]/(b^2*d*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:325
  public void test0498() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(5/3), x]", //
        "3*(c+d*x)^(1/3)*Cos[a+b/(c+d*x)^(1/3)]/(b*d*e*(e*(c+d*x))^(2/3))-3*(c+d*x)^(2/3)*Sin[a+b/(c+d*x)^(1/3)]/(b^2*d*e*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:344
  public void test0499() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^n], x]", //
        "1/2*I*E^(I*a)*(c+d*x)*Gamma[1/n,-I*b*(c+d*x)^n]/(d*n*(-I*b*(c+d*x)^n)^(1/n))-1/2*I*(c+d*x)*Gamma[1/n,I*b*(c+d*x)^n]/(E^(I*a)*d*n*(I*b*(c+d*x)^n)^(1/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:458
  public void test0500() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(2/3)/x, x]", //
        "-1/2*CosIntegral[2*b*x]*Cos[2*a]*Csc[a+b*x]^2*(c*Sin[a+b*x]^3)^(2/3)+1/2*Csc[a+b*x]^2*Log[x]*(c*Sin[a+b*x]^3)^(2/3)+1/2*Csc[a+b*x]^2*SinIntegral[2*b*x]*Sin[2*a]*(c*Sin[a+b*x]^3)^(2/3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:478
  public void test0501() {
    check( //
        "Integrate[(c*Sin[a+b*x^n]^3)^(2/3)/x, x]", //
        "-1/2*CosIntegral[2*b*x^n]*Cos[2*a]*Csc[a+b*x^n]^2*(c*Sin[a+b*x^n]^3)^(2/3)/n+1/2*Csc[a+b*x^n]^2*Log[x]*(c*Sin[a+b*x^n]^3)^(2/3)+1/2*Csc[a+b*x^n]^2*SinIntegral[2*b*x^n]*Sin[2*a]*(c*Sin[a+b*x^n]^3)^(2/3)/n");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:25
  public void test0502() {
    check( //
        "Integrate[Sin[a+b*x+c*x^2]^2, x]", //
        "1/2*x-1/4*Cos[2*a-1/2*b^2/c]*FresnelC[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sqrt[Pi]/Sqrt[c]+1/4*FresnelS[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sin[2*a-1/2*b^2/c]*Sqrt[Pi]/Sqrt[c]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:34
  public void test0503() {
    check( //
        "Integrate[Sin[x]^6/(a+a*Sin[x])^3, x]", //
        "-23/2*x/a^3-136/5*Cos[x]/a^3+136/15*Cos[x]^3/a^3+23/2*Cos[x]*Sin[x]/a^3+1/5*Cos[x]*Sin[x]^5/(a+a*Sin[x])^3+13/15*Cos[x]*Sin[x]^4/(a*(a+a*Sin[x])^2)+23/3*Cos[x]*Sin[x]^3/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:70
  public void test0504() {
    check( //
        "Integrate[Sin[c+d*x]^2*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-26/105*a*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d+4/63*Cos[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/d-2/9*Cos[c+d*x]*(a+a*Sin[c+d*x])^(7/2)/(a*d)-832/315*a^3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-208/315*a^2*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:88
  public void test0505() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/2*Cos[c+d*x]*Sin[c+d*x]^2/(d*(a+a*Sin[c+d*x])^(3/2))-11/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+13/3*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-7/6*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:222
  public void test0506() {
    check( //
        "Integrate[Sin[x]^3/(a+b*Sin[x]), x]", //
        "1/2*(2*a^2+b^2)*x/b^3+a*Cos[x]/b^2-1/2*Cos[x]*Sin[x]/b-2*a^3*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(b^3*Sqrt[a^2-b^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:238
  public void test0507() {
    check( //
        "Integrate[Sin[x]^5/(a+b*Sin[x])^3, x]", //
        "1/2*(12*a^2+b^2)*x/b^5-a^3*(12*a^4-29*a^2*b^2+20*b^4)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(b^5*(a^2-b^2)^(5/2))+3/2*a*(4*a^4-7*a^2*b^2+2*b^4)*Cos[x]/(b^4*(a^2-b^2)^2)-1/2*(6*a^4-10*a^2*b^2+b^4)*Cos[x]*Sin[x]/(b^3*(a^2-b^2)^2)+1/2*a^2*Cos[x]*Sin[x]^3/(b*(a^2-b^2)*(a+b*Sin[x])^2)+1/2*a^2*(4*a^2-7*b^2)*Cos[x]*Sin[x]^2/(b^2*(a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:295
  public void test0508() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^3, x]", //
        "5/8*a*c^3*x+5/12*a*c^3*Cos[e+f*x]^3/f+5/8*a*c^3*Cos[e+f*x]*Sin[e+f*x]/f+1/4*a*Cos[e+f*x]^3*(c^3-c^3*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:311
  public void test0509() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^4, x]", //
        "1/7*a^2*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+1/35*a^2*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:327
  public void test0510() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^8, x]", //
        "1/15*a^3*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^11)+4/195*a^3*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^10)+4/715*a^3*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+8/6435*a^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+8/45045*a^3*Cos[e+f*x]^7/(c*f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:345
  public void test0511() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^3), x]", //
        "1/5*Sec[e+f*x]^3/(a^2*f*(c^3-c^3*Sin[e+f*x]))+4/5*Tan[e+f*x]/(a^2*c^3*f)+4/15*Tan[e+f*x]^3/(a^2*c^3*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:365
  public void test0512() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/15*a*c^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+2/5*a*c^2*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:381
  public void test0513() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/693*a^3*c^6*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+16/99*a^3*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))+2/11*a^3*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:399
  public void test0514() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^2, x]", //
        "4096/15*c^3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-1024/5*c^2*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)+128/5*c*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(7/2)/(a^2*f)+32/15*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(9/2)/(a^2*f)+2/5*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(11/2)/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:419
  public void test0515() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/4*a*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:469
  public void test0516() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "1/4*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+1/4*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/4*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:494
  public void test0517() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(5/2), x]", //
        "2*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^(3/2)/(f*(5+2*m))+64*c^3*Cos[e+f*x]*(a+a*Sin[e+f*x])^m/(f*(5+2*m)*(3+8*m+4*m^2)*Sqrt[c-c*Sin[e+f*x]])+16*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^m*Sqrt[c-c*Sin[e+f*x]]/(f*(15+16*m+4*m^2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:548
  public void test0518() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x]), x]", //
        "(2*c-d)*d*x/a-d^2*Cos[e+f*x]/(a*f)-(c-d)^2*Cos[e+f*x]/(a*f*(1+Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:638
  public void test0519() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^3, x]", //
        "-1/4*a^(3/2)*(c+7*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(d^(3/2)*(c+d)^(5/2)*f)+1/2*a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*(c+d*Sin[e+f*x])^2*Sqrt[a+a*Sin[e+f*x]])-1/4*a^2*(c+7*d)*Cos[e+f*x]/(d*(c+d)^2*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:656
  public void test0520() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(c-d)*(c+7*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])+1/2*(c-5*d)*d*Cos[e+f*x]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:676
  public void test0521() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^(1/2), x]", //
        "-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[d])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:692
  public void test0522() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^(3/2), x]", //
        "a^(5/2)*(3*c-5*d)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(5/2)*f)+2*a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*Sqrt[c+d*Sin[e+f*x]])-a^3*(3*c-d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(d^2*(c+d)*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:710
  public void test0523() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(5/2)), x]", //
        "-1/2*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^(3/2))-1/2*(c-11*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(3/2)*(c-d)^(7/2)*f*Sqrt[2])-1/6*d*(3*c+7*d)*Cos[e+f*x]/(a*(c-d)^2*(c+d)*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-1/6*d*(3*c^2+38*c*d+19*d^2)*Cos[e+f*x]/(a*(c-d)^3*(c+d)^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:27
  public void test0524() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]], x]", //
        "1/6*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])+1/4*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:43
  public void test0525() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/8*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*c*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:61
  public void test0526() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(5/2)/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/4*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:77
  public void test0527() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-4*c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2*c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:121
  public void test0528() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]], x]", //
        "-2/7*a*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])+2/5*a*c^2*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6/5*a*c^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6/35*a*c*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:137
  public void test0529() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "-2/13*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)/(f*g)+2/39*a^3*c*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-14/117*a^3*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-2/13*a^2*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g)+154/585*a^3*c^3*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+154/195*a^3*c^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+22/195*a^3*c^2*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:153
  public void test0530() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "4/9*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(7/2))-4/3*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(5/2))+44/3*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*(c-c*Sin[e+f*x])^(3/2))+154/9*a^4*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-154/3*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:171
  public void test0531() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]]), x]", //
        "-2*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])-2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:335
  public void test0532() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "1/8*a*x-1/3*a*Cos[c+d*x]^3/d+1/8*a*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:352
  public void test0533() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "5/16*a^3*x-4/3*a^3*Cos[c+d*x]^3/d+a^3*Cos[c+d*x]^5/d-1/7*a^3*Cos[c+d*x]^7/d+5/16*a^3*Cos[c+d*x]*Sin[c+d*x]/d-5/8*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d-1/2*a^3*Cos[c+d*x]^3*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:371
  public void test0534() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "ArcTanh[Cos[c+d*x]]/(a*d)-Cot[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:387
  public void test0535() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "-ArcTanh[Cos[c+d*x]]/(a^3*d)+2/3*Cos[c+d*x]/(a^3*d*(1+Sin[c+d*x])^2)+5/3*Cos[c+d*x]/(a^3*d*(1+Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:407
  public void test0536() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2), x]", //
        "1/4*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/2*Cot[c+d*x]*Csc[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d+13/4*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-3/4*a*Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:457
  public void test0537() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "3/128*a*x-1/5*a*Cos[c+d*x]^5/d+1/7*a*Cos[c+d*x]^7/d+3/128*a*Cos[c+d*x]*Sin[c+d*x]/d+1/64*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/16*a*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*a*Cos[c+d*x]^5*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:489
  public void test0538() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "-33/8*a^3*x-3/2*a^3*ArcTanh[Cos[c+d*x]]/d+2*a^3*Cos[c+d*x]/d+a^3*Cos[c+d*x]^3/d-3*a^3*Cot[c+d*x]/d-1/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d-7/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:523
  public void test0539() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6/(a+a*Sin[c+d*x])^2, x]", //
        "3/4*ArcTanh[Cos[c+d*x]]/(a^2*d)-2*Cot[c+d*x]/(a^2*d)-Cot[c+d*x]^3/(a^2*d)-1/5*Cot[c+d*x]^5/(a^2*d)+3/4*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+1/2*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:544
  public void test0540() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "13/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-2/3*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/4*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/3*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d-1/2*Cot[c+d*x]*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:560
  public void test0541() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^9*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-1587/16384*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/8*Cot[c+d*x]*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^(3/2)/d-1587/16384*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-529/8192*a^2*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-529/10240*a^2*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+8653/35840*a^2*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])+1957/4480*a^2*Cot[c+d*x]*Csc[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+83/448*a^2*Cot[c+d*x]*Csc[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-3/112*a*Cot[c+d*x]*Csc[c+d*x]^6*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:578
  public void test0542() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3/64*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-3/64*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/32*Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])+5/8*Cot[c+d*x]*Csc[c+d*x]^2/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/4*Cot[c+d*x]*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:638
  public void test0543() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^4, x]", //
        "4*a^4*Csc[c+d*x]/d-2*a^4*Csc[c+d*x]^2/d-4/3*a^4*Csc[c+d*x]^3/d-1/4*a^4*Csc[c+d*x]^4/d-10*a^4*Log[Sin[c+d*x]]/d-4*a^4*Sin[c+d*x]/d+2*a^4*Sin[c+d*x]^2/d+4/3*a^4*Sin[c+d*x]^3/d+1/4*a^4*Sin[c+d*x]^4/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:672
  public void test0544() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^4, x]", //
        "12*Csc[c+d*x]/(a^4*d)-4*Csc[c+d*x]^2/(a^4*d)+4/3*Csc[c+d*x]^3/(a^4*d)-1/4*Csc[c+d*x]^4/(a^4*d)+16*Log[Sin[c+d*x]]/(a^4*d)-16*Log[1+Sin[c+d*x]]/(a^4*d)+4/(d*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:716
  public void test0545() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^2, x]", //
        "3/2*a^2*x-15/4*a^2*ArcTanh[Cos[c+d*x]]/d+2*a^2*Cos[c+d*x]/d+a^2*Cot[c+d*x]/d-1/5*a^2*Cot[c+d*x]^5/d+9/4*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/2*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+1/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:732
  public void test0546() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^5*(a+a*Sin[c+d*x])^3, x]", //
        "45/8*a^3*x+45/8*a^3*ArcTanh[Cos[c+d*x]]/d-5*a^3*Cos[c+d*x]/d-a^3*Cos[c+d*x]^3/d+5*a^3*Cot[c+d*x]/d-a^3*Cot[c+d*x]^3/d-3/8*a^3*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d+3/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a^3*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:766
  public void test0547() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "13/8*x/a^3+4*Cos[c+d*x]/(a^3*d)-5/3*Cos[c+d*x]^3/(a^3*d)+1/5*Cos[c+d*x]^5/(a^3*d)-13/8*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-3/4*Cos[c+d*x]*Sin[c+d*x]^3/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:816
  public void test0548() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "1/6*Cos[c+d*x]^6/(a*d)-1/8*Cos[c+d*x]^8/(a*d)+1/3*Sin[c+d*x]^3/(a*d)-2/5*Sin[c+d*x]^5/(a*d)+1/7*Sin[c+d*x]^7/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:836
  public void test0549() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^n*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*Sin[c+d*x]^(1+n)/(d*(1+n))+3*a^3*Sin[c+d*x]^(2+n)/(d*(2+n))-8*a^3*Sin[c+d*x]^(4+n)/(d*(4+n))-6*a^3*Sin[c+d*x]^(5+n)/(d*(5+n))+6*a^3*Sin[c+d*x]^(6+n)/(d*(6+n))+8*a^3*Sin[c+d*x]^(7+n)/(d*(7+n))-3*a^3*Sin[c+d*x]^(9+n)/(d*(9+n))-a^3*Sin[c+d*x]^(10+n)/(d*(10+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:878
  public void test0550() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^2, x]", //
        "-2*x/a^2+9/8*ArcTanh[Cos[c+d*x]]/(a^2*d)-Cos[c+d*x]/(a^2*d)-2*Cot[c+d*x]/(a^2*d)+2/3*Cot[c+d*x]^3/(a^2*d)+1/8*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:894
  public void test0551() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^6/(a+a*Sin[c+d*x])^3, x]", //
        "-7/8*ArcTanh[Cos[c+d*x]]/(a^3*d)-4/3*Cot[c+d*x]^3/(a^3*d)-1/5*Cot[c+d*x]^5/(a^3*d)+1/8*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)+3/4*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1051
  public void test0552() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "-1/5*Sec[c+d*x]^5/(a^3*d)+5/7*Sec[c+d*x]^7/(a^3*d)-4/9*Sec[c+d*x]^9/(a^3*d)+1/3*Tan[c+d*x]^3/(a^3*d)+6/5*Tan[c+d*x]^5/(a^3*d)+9/7*Tan[c+d*x]^7/(a^3*d)+4/9*Tan[c+d*x]^9/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1076
  public void test0553() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "-31/8*a^2*Log[1-Sin[c+d*x]]/d-1/8*a^2*Log[1+Sin[c+d*x]]/d-2*a^2*Sin[c+d*x]/d-1/2*a^2*Sin[c+d*x]^2/d+1/4*a^4/(d*(a-a*Sin[c+d*x])^2)-9/4*a^3/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1228
  public void test0554() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "8/5*(A-B)*(a+a*Sin[c+d*x])^5/(a^4*d)-2/3*(3*A-5*B)*(a+a*Sin[c+d*x])^6/(a^5*d)+6/7*(A-3*B)*(a+a*Sin[c+d*x])^7/(a^6*d)-1/8*(A-7*B)*(a+a*Sin[c+d*x])^8/(a^7*d)-1/9*B*(a+a*Sin[c+d*x])^9/(a^8*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1244
  public void test0555() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "4/3*(A-B)*(a+a*Sin[c+d*x])^6/(a^4*d)-4/7*(3*A-5*B)*(a+a*Sin[c+d*x])^7/(a^5*d)+3/4*(A-3*B)*(a+a*Sin[c+d*x])^8/(a^6*d)-1/9*(A-7*B)*(a+a*Sin[c+d*x])^9/(a^7*d)-1/10*B*(a+a*Sin[c+d*x])^10/(a^8*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1260
  public void test0556() {
    check( //
        "Integrate[Sec[c+d*x]^12*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/99*a^2*(9*A-2*B)*Sec[c+d*x]^9/d+1/11*(A+B)*Sec[c+d*x]^11*(a+a*Sin[c+d*x])^2/d+1/11*a^2*(9*A-2*B)*Tan[c+d*x]/d+4/33*a^2*(9*A-2*B)*Tan[c+d*x]^3/d+6/55*a^2*(9*A-2*B)*Tan[c+d*x]^5/d+4/77*a^2*(9*A-2*B)*Tan[c+d*x]^7/d+1/99*a^2*(9*A-2*B)*Tan[c+d*x]^9/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1276
  public void test0557() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "1/7*(A+B)*Sec[c+d*x]^7*(a+a*Sin[c+d*x])^3/d+2/35*(4*A-3*B)*Sec[c+d*x]^5*(a^3+a^3*Sin[c+d*x])/d+3/35*a^3*(4*A-3*B)*Tan[c+d*x]/d+1/35*a^3*(4*A-3*B)*Tan[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1356
  public void test0558() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "-b*x+1/2*a*ArcTanh[Cos[c+d*x]]/d-b*Cot[c+d*x]/d-1/2*a*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1372
  public void test0559() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]*(a+b*Sin[c+d*x])^3, x]", //
        "1/8*b*(12*a^2+b^2)*x-a^3*ArcTanh[Cos[c+d*x]]/d+1/2*a*(a^2-2*b^2)*Cos[c+d*x]/d+1/8*b*(2*a^2-b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/d+1/4*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1390
  public void test0560() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/(a+b*Sin[c+d*x])^3, x]", //
        "-x/b^3+a*(2*a^2-3*b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^3*(a^2-b^2)^(3/2)*d)-1/2*a*Cos[c+d*x]^3/((a^2-b^2)*d*(a+b*Sin[c+d*x])^2)-1/2*Cos[c+d*x]*(2*(a^2-b^2)+a*b*Sin[c+d*x])/(b^2*(a^2-b^2)*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1418
  public void test0561() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^8*(a+b*Sin[c+d*x]), x]", //
        "-1/16*b*ArcTanh[Cos[c+d*x]]/d-1/5*a*Cot[c+d*x]^5/d-1/7*a*Cot[c+d*x]^7/d-1/16*b*Cot[c+d*x]*Csc[c+d*x]/d+1/8*b*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*b*Cot[c+d*x]^3*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1435
  public void test0562() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3, x]", //
        "-3/8*b*(12*a^2-b^2)*x+3/2*a*(a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/d-1/2*a*(a^2-17*b^2)*Cos[c+d*x]/d-1/8*b*(2*a^2-21*b^2)*Cos[c+d*x]*Sin[c+d*x]/d-1/4*(a^2-6*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^2/(a*d)-1/4*(a^2-4*b^2)*Cos[c+d*x]*(a+b*Sin[c+d*x])^3/(a^2*d)-b*Cot[c+d*x]*(a+b*Sin[c+d*x])^4/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^4/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1544
  public void test0563() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^7*(a+b*Sin[c+d*x]), x]", //
        "-1/6*a*Cot[c+d*x]^6/d-b*Csc[c+d*x]/d+2/3*b*Csc[c+d*x]^3/d-1/5*b*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1590
  public void test0564() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]*(a+b*Sin[c+d*x])^2, x]", //
        "5/64*a*b*x-1/252*(a^2+8*b^2)*Cos[c+d*x]^7/d+5/64*a*b*Cos[c+d*x]*Sin[c+d*x]/d+5/96*a*b*Cos[c+d*x]^3*Sin[c+d*x]/d+1/24*a*b*Cos[c+d*x]^5*Sin[c+d*x]/d-1/36*a*Cos[c+d*x]^7*(a+b*Sin[c+d*x])/d-1/9*Cos[c+d*x]^7*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1657
  public void test0565() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "-1/2*b*(a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/(a^4*d)+1/3*(a^2-3*b^2)*Cot[c+d*x]/(a^3*d)+1/2*b*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a*d)-2*b^2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a^4*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1673
  public void test0566() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/(a+b*Sin[c+d*x]), x]", //
        "-2*b*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^5*d)-1/8*(3*a^4-12*a^2*b^2+8*b^4)*ArcTanh[Cos[c+d*x]]/(a^5*d)-1/3*b*(4*a^2-3*b^2)*Cot[c+d*x]/(a^4*d)+1/8*(5*a^2-4*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)+1/3*b*Cot[c+d*x]*Csc[c+d*x]^2/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1709
  public void test0567() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "2*a*b*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)+Sec[c+d*x]*(a-b*Sin[c+d*x])/((a^2-b^2)*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1905
  public void test0568() {
    check( //
        "Integrate[Sec[c+d*x]^5*Sin[c+d*x]^4*(a+b*Sin[c+d*x]), x]", //
        "3/8*a*ArcTanh[Sin[c+d*x]]/d-b*Log[Cos[c+d*x]]/d-3/8*a*Sec[c+d*x]*Tan[c+d*x]/d-1/2*b*Tan[c+d*x]^2/d+1/4*a*Sec[c+d*x]*Tan[c+d*x]^3/d+1/4*b*Tan[c+d*x]^4/d");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:16
  public void test0569() {
    check( //
        "Integrate[Csc[e+f*x]^3*(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x]), x]", //
        "-a^2*c*x+1/2*a^2*c*ArcTanh[Cos[e+f*x]]/f-a^2*c*Cot[e+f*x]/f-1/2*a^2*c*Cot[e+f*x]*Csc[e+f*x]/f");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:67
  public void test0570() {
    check( //
        "Integrate[1/(Sin[e+f*x]*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(c*f*Sqrt[a])+ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/((c-d)*f*Sqrt[a])-2*d^(3/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(c*(c-d)*f*Sqrt[a]*Sqrt[c+d])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:40
  public void test0571() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^4, x]", //
        "7/16*a*(2*A-B)*c^4*x+7/24*a*(2*A-B)*c^4*Cos[e+f*x]^3/f+7/16*a*(2*A-B)*c^4*Cos[e+f*x]*Sin[e+f*x]/f-1/6*a*B*c*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^3/f+1/10*a*(2*A-B)*Cos[e+f*x]^3*(c^2-c^2*Sin[e+f*x])^2/f+7/40*a*(2*A-B)*Cos[e+f*x]^3*(c^4-c^4*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:57
  public void test0572() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^4, x]", //
        "1/7*a^2*(A+B)*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+1/35*a^2*(A-6*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:73
  public void test0573() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^7, x]", //
        "1/13*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^10)+1/143*a^3*(3*A-10*B)*c^2*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^9)+2/1287*a^3*(3*A-10*B)*c*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^8)+2/9009*a^3*(3*A-10*B)*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^7)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:91
  public void test0574() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^2), x]", //
        "1/3*B*Sec[e+f*x]^3/(a^2*c^2*f)+A*Tan[e+f*x]/(a^2*c^2*f)+1/3*A*Tan[e+f*x]^3/(a^2*c^2*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:111
  public void test0575() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/315*a*(3*A-B)*c^4*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))-2/9*a*B*c*Cos[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/f+16/105*a*(3*A-B)*c^3*Cos[e+f*x]^3/(f*Sqrt[c-c*Sin[e+f*x]])+2/21*a*(3*A-B)*c^2*Cos[e+f*x]^3*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:127
  public void test0576() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "256/45045*a^3*(15*A-B)*c^7*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+64/6435*a^3*(15*A-B)*c^6*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))+8/715*a^3*(15*A-B)*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(3/2))+2/195*a^3*(15*A-B)*c^4*Cos[e+f*x]^7/(f*Sqrt[c-c*Sin[e+f*x]])-2/15*a^3*B*c^3*Cos[e+f*x]^7*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:145
  public void test0577() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "3/32*(5*A-3*B)*Cos[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2))+1/4*(A+B)*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2))+3/32*(5*A-3*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a*c^(5/2)*f*Sqrt[2])-1/8*(5*A-3*B)*Sec[e+f*x]/(a*c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:161
  public void test0578() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "7/128*(9*A+B)*Cos[e+f*x]/(a^3*c*f*(c-c*Sin[e+f*x])^(3/2))+7/240*(9*A+B)*Sec[e+f*x]/(a^3*c*f*(c-c*Sin[e+f*x])^(3/2))+7/128*(9*A+B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^3*c^(5/2)*f*Sqrt[2])-7/96*(9*A+B)*Sec[e+f*x]/(a^3*c^2*f*Sqrt[c-c*Sin[e+f*x]])-1/30*(9*A+B)*Sec[e+f*x]^3/(a^3*c^2*f*Sqrt[c-c*Sin[e+f*x]])-1/5*(A-B)*Sec[e+f*x]^5*Sqrt[c-c*Sin[e+f*x]]/(a^3*c^3*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:181
  public void test0579() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/6*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(7/2))+1/24*(A-5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(5/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:197
  public void test0580() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/42*(7*A+B)*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(3/2)/f-1/7*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(5/2)/f+1/105*(7*A+B)*c^3*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])+2/105*(7*A+B)*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:215
  public void test0581() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "1/2*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/2*(A-B)*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:231
  public void test0582() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-1/4*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2))-1/2*A*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2))+3/8*A*Cos[e+f*x]/(a^2*f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*A*Cos[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+3/8*A*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a^2*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:275
  public void test0583() {
    check( //
        "Integrate[Csc[c+d*x]*(a+a*Sin[c+d*x])^3*(A-A*Sin[c+d*x]), x]", //
        "a^3*A*x-a^3*A*ArcTanh[Cos[c+d*x]]/d+a^3*A*Cos[c+d*x]/d-1/3*a^3*A*Cos[c+d*x]^3/d+a^3*A*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:313
  public void test0584() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x]), x]", //
        "1/8*a^3*(20*A*c+15*B*c+15*A*d+13*B*d)*x-1/5*a^3*(20*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]/f+1/60*a^3*(20*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]^3/f-3/40*a^3*(20*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]*Sin[e+f*x]/f-1/20*(5*B*c+5*A*d-B*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^3/f-1/5*B*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^4/(a*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:370
  public void test0585() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x])/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-(A-B)*(c-d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-2/3*(3*B*c+3*A*d-2*B*d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/3*B*d*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a*f)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:120
  public void test0586() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*Sec[c+d*x]^4, x]", //
        "5/8*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+5/8*a*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+5/12*a*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*a*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:24
  public void test0587() {
    check( //
        "Integrate[1/Sin[b*x]^(1/2), x]", //
        "-2*EllipticF[1/4*Pi-1/2*b*x,2]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:81
  public void test0588() {
    check( //
        "Integrate[Sec[a+b*x]^4*Sin[a+b*x]^2, x]", //
        "1/3*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:113
  public void test0589() {
    check( //
        "Integrate[Sec[a+b*x]^6*Sin[a+b*x]^4, x]", //
        "1/5*Tan[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:129
  public void test0590() {
    check( //
        "Integrate[Cos[a+b*x]^3*Sin[a+b*x]^5, x]", //
        "1/6*Sin[a+b*x]^6/b-1/8*Sin[a+b*x]^8/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:179
  public void test0591() {
    check( //
        "Integrate[Cos[a+b*x]^2/Sin[a+b*x]^3, x]", //
        "1/2*ArcTanh[Cos[a+b*x]]/b-1/2*Cot[a+b*x]*Csc[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:231
  public void test0592() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*Sin[a+b*x]^2, x]", //
        "-2/5*(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]/(b*d)+4/5*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:247
  public void test0593() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(3/2)*Sin[a+b*x]^4, x]", //
        "-12/77*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]/(b*d)-2/11*(d*Cos[a+b*x])^(5/2)*Sin[a+b*x]^3/(b*d)+8/77*d^2*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])+8/77*d*Sin[a+b*x]*Sqrt[d*Cos[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:325
  public void test0594() {
    check( //
        "Integrate[(d*Cos[a+b*x])^(1/2)*(c*Sin[a+b*x])^(5/2), x]", //
        "-1/3*c*(d*Cos[a+b*x])^(3/2)*(c*Sin[a+b*x])^(3/2)/(b*d)+1/2*c^2*EllipticE[-1/4*Pi+a+b*x,2]*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]]/(b*Sqrt[Sin[2*a+2*b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:343
  public void test0595() {
    check( //
        "Integrate[1/((d*Cos[a+b*x])^(5/2)*(c*Sin[a+b*x])^(1/2)), x]", //
        "2/3*Sqrt[c*Sin[a+b*x]]/(b*c*d*(d*Cos[a+b*x])^(3/2))+2/3*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*d^2*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:462
  public void test0596() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^6, x]", //
        "8/3*b^3*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))+20/9*b^3*Sin[e+f*x]^3/(f*(b*Sec[e+f*x])^(3/2))-16/3*b^2*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])+2*b*Sin[e+f*x]^5*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:478
  public void test0597() {
    check( //
        "Integrate[(b*Sec[e+f*x])^(5/2), x]", //
        "2/3*b*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]/f+2/3*b^2*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:496
  public void test0598() {
    check( //
        "Integrate[Csc[e+f*x]^6/Sqrt[b*Sec[e+f*x]], x]", //
        "-7/20*b*Csc[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-7/30*b*Csc[e+f*x]^3/(f*(b*Sec[e+f*x])^(3/2))-1/5*b*Csc[e+f*x]^5/(f*(b*Sec[e+f*x])^(3/2))-7/20*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:532
  public void test0599() {
    check( //
        "Integrate[Sqrt[b*Sec[e+f*x]]/(a*Sin[e+f*x])^(11/2), x]", //
        "-2/9*b/(a*f*(a*Sin[e+f*x])^(9/2)*Sqrt[b*Sec[e+f*x]])-16/45*b/(a^3*f*(a*Sin[e+f*x])^(5/2)*Sqrt[b*Sec[e+f*x]])-64/45*b/(a^5*f*Sqrt[b*Sec[e+f*x]]*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:550
  public void test0600() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(17/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2/15*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(15/2))-8/55*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(11/2))-64/385*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(7/2))-256/1155*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(3/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:15
  public void test0601() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "3/8*a*x-1/5*a*Cos[c+d*x]^5/d+3/8*a*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:66
  public void test0602() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "1/2*x/a+1/3*Cos[c+d*x]^3/(a*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:134
  public void test0603() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-256/1155*a^4*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-64/231*a^3*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-8/33*a^2*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-2/11*a*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:200
  public void test0604() {
    check( //
        "Integrate[Sec[c+d*x]^6/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3003/8192*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-1001/5120*Sec[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-143/960*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(3/2))-1/8*Sec[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-3003/8192*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1001/2048*Sec[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])+143/640*Sec[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])+13/80*Sec[c+d*x]^5/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:220
  public void test0605() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x]), x]", //
        "-2/7*a*(e*Cos[c+d*x])^(7/2)/(d*e)+2/5*a*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+6/5*a*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:236
  public void test0606() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2/(e*Cos[c+d*x])^(11/2), x]", //
        "2/9*a^2*Sin[c+d*x]/(d*e^3*(e*Cos[c+d*x])^(5/2))+4/9*(a^2+a^2*Sin[c+d*x])/(d*e*(e*Cos[c+d*x])^(9/2))+2/3*a^2*Sin[c+d*x]/(d*e^5*Sqrt[e*Cos[c+d*x]])-2/3*a^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^6*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:252
  public void test0607() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^4/(e*Cos[c+d*x])^(7/2), x]", //
        "4/5*a^7*(e*Cos[c+d*x])^(7/2)/(d*e^7*(a-a*Sin[c+d*x])^3)-28/5*a^8*(e*Cos[c+d*x])^(3/2)/(d*e^5*(a^4-a^4*Sin[c+d*x]))+42/5*a^4*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:270
  public void test0608() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)/(a+a*Sin[c+d*x])^2, x]", //
        "4*e*(e*Cos[c+d*x])^(5/2)/(d*(a^2+a^2*Sin[c+d*x]))+10/3*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^2*d*Sqrt[e*Cos[c+d*x]])+10/3*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:286
  public void test0609() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])^3*Sqrt[e*Cos[c+d*x]]), x]", //
        "10/77*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[e*Cos[c+d*x]])-2/11*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x])^3)-10/77*Sqrt[e*Cos[c+d*x]]/(a*d*e*(a+a*Sin[c+d*x])^2)-10/77*Sqrt[e*Cos[c+d*x]]/(d*e*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:322
  public void test0610() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(5/2)/(e*Cos[c+d*x])^(5/2), x]", //
        "4/3*a*(a+a*Sin[c+d*x])^(3/2)/(d*e*(e*Cos[c+d*x])^(3/2))+2*a^2*ArcSinh[Sqrt[e*Cos[c+d*x]]/Sqrt[e]]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(5/2)*(1+Cos[c+d*x]+Sin[c+d*x]))-2*a^2*ArcTan[Sin[c+d*x]*Sqrt[e]/(Sqrt[e*Cos[c+d*x]]*Sqrt[1+Cos[c+d*x]])]*Sqrt[1+Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]]/(d*e^(5/2)*(1+Cos[c+d*x]+Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:340
  public void test0611() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])^(3/2)*Sqrt[e*Cos[c+d*x]]), x]", //
        "-2/5*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x])^(3/2))-4/5*Sqrt[e*Cos[c+d*x]]/(a*d*e*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:397
  public void test0612() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(-4-m)*(a+a*Sin[c+d*x])^m, x]", //
        "-(e*Cos[c+d*x])^(-3-m)*(a+a*Sin[c+d*x])^m/(d*e*(3-m))-3*(e*Cos[c+d*x])^(-3-m)*(a+a*Sin[c+d*x])^(1+m)/(a*d*e*(1-m)*(3-m))+6*(e*Cos[c+d*x])^(-3-m)*(a+a*Sin[c+d*x])^(2+m)/(a^2*d*e*(3-m)*(1-m^2))-6*(e*Cos[c+d*x])^(-3-m)*(a+a*Sin[c+d*x])^(3+m)/(a^3*d*e*(9-10*m^2+m^4))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:429
  public void test0613() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+b*Sin[c+d*x]), x]", //
        "1/5*b*Sec[c+d*x]^5/d+a*Tan[c+d*x]/d+2/3*a*Tan[c+d*x]^3/d+1/5*a*Tan[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:479
  public void test0614() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+b*Sin[c+d*x]), x]", //
        "-2*b^2*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)-Sec[c+d*x]*(b-a*Sin[c+d*x])/((a^2-b^2)*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:495
  public void test0615() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Sin[c+d*x])^3, x]", //
        "2*(3*a^2-b^2)*Log[a+b*Sin[c+d*x]]/(b^5*d)-3*a*Sin[c+d*x]/(b^4*d)+1/2*Sin[c+d*x]^2/(b^3*d)-1/2*(a^2-b^2)^2/(b^5*d*(a+b*Sin[c+d*x])^2)+4*a*(a^2-b^2)/(b^5*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:601
  public void test0616() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*b/(d*e*(e*Cos[c+d*x])^(5/2))+2/5*a*Sin[c+d*x]/(d*e*(e*Cos[c+d*x])^(5/2))+6/5*a*Sin[c+d*x]/(d*e^3*Sqrt[e*Cos[c+d*x]])-6/5*a*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:617
  public void test0617() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^3/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*b*(3*a^2-4*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e^5)+2/5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/(d*e*(e*Cos[c+d*x])^(5/2))-2/5*(a+b*Sin[c+d*x])*(a*b-(3*a^2-4*b^2)*Sin[c+d*x])/(d*e^3*Sqrt[e*Cos[c+d*x]])-6/5*a*(a^2-2*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:12
  public void test0618() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])*Tan[c+d*x]^5, x]", //
        "-23/16*a*Log[1-Sin[c+d*x]]/d+7/16*a*Log[1+Sin[c+d*x]]/d-a*Sin[c+d*x]/d+1/8*a^3/(d*(a-a*Sin[c+d*x])^2)-a^2/(d*(a-a*Sin[c+d*x]))+1/8*a^2/(d*(a+a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:44
  public void test0619() {
    check( //
        "Integrate[Cot[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "1/2*a^3*x-3*a^3*ArcTanh[Cos[c+d*x]]/d+3*a^3*Cos[c+d*x]/d-1/3*a^3*Cos[c+d*x]^3/d-a^3*Cot[c+d*x]/d+3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:63
  public void test0620() {
    check( //
        "Integrate[Cot[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "-1/4*Cot[c+d*x]^4/(a*d)-Csc[c+d*x]/(a*d)+1/3*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:79
  public void test0621() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "2*Csc[c+d*x]/(a^2*d)-1/2*Csc[c+d*x]^2/(a^2*d)+2*Log[Sin[c+d*x]]/(a^2*d)-2*Log[1+Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:118
  public void test0622() {
    check( //
        "Integrate[Cot[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2), x]", //
        "-5*a^(5/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/f+7/5*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-Cot[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/f+49/15*a^3*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])+31/15*a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:178
  public void test0623() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "-b*Csc[c+d*x]/d-1/2*a*Csc[c+d*x]^2/d-a*Log[Sin[c+d*x]]/d-b*Sin[c+d*x]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:16
  public void test0624() {
    check( //
        "Integrate[Sin[a+b*x]/(c+d*x), x]", //
        "Cos[a-b*c/d]*SinIntegral[b*c/d+b*x]/d+CosIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:32
  public void test0625() {
    check( //
        "Integrate[Sin[a+b*x]^3/(c+d*x)^2, x]", //
        "-3/4*b*CosIntegral[3*b*c/d+3*b*x]*Cos[3*a-3*b*c/d]/d^2+3/4*b*CosIntegral[b*c/d+b*x]*Cos[a-b*c/d]/d^2+3/4*b*SinIntegral[3*b*c/d+3*b*x]*Sin[3*a-3*b*c/d]/d^2-3/4*b*SinIntegral[b*c/d+b*x]*Sin[a-b*c/d]/d^2-Sin[a+b*x]^3/(d*(c+d*x))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:183
  public void test0626() {
    check( //
        "Integrate[x^3/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-4*x^3*ArcTanh[E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+12*I*x^2*PolyLog[2,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])-12*I*x^2*PolyLog[2,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^2*Sqrt[a+a*Sin[c+d*x]])-48*x*PolyLog[3,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^3*Sqrt[a+a*Sin[c+d*x]])+48*x*PolyLog[3,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^3*Sqrt[a+a*Sin[c+d*x]])-96*I*PolyLog[4,-E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^4*Sqrt[a+a*Sin[c+d*x]])+96*I*PolyLog[4,E^(1/4*I*(Pi+2*c+2*d*x))]*Sin[1/4*Pi+1/2*c+1/2*d*x]/(d^4*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:219
  public void test0627() {
    check( //
        "Integrate[(c+d*x)^2*(a+b*Sin[e+f*x])^2, x]", //
        "-1/4*b^2*d^2*x/f^2+1/3*a^2*(c+d*x)^3/d+1/6*b^2*(c+d*x)^3/d+4*a*b*d^2*Cos[e+f*x]/f^3-2*a*b*(c+d*x)^2*Cos[e+f*x]/f+4*a*b*d*(c+d*x)*Sin[e+f*x]/f^2+1/4*b^2*d^2*Cos[e+f*x]*Sin[e+f*x]/f^3-1/2*b^2*(c+d*x)^2*Cos[e+f*x]*Sin[e+f*x]/f+1/2*b^2*d*(c+d*x)*Sin[e+f*x]^2/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:280
  public void test0628() {
    check( //
        "Integrate[(e+f*x)*Csc[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "2*(e+f*x)*ArcTanh[E^(I*(c+d*x))]/(a*d)-(e+f*x)*Cot[1/4*Pi+1/2*c+1/2*d*x]/(a*d)-(e+f*x)*Cot[c+d*x]/(a*d)+2*f*Log[Sin[1/4*Pi+1/2*c+1/2*d*x]]/(a*d^2)+f*Log[Sin[c+d*x]]/(a*d^2)-I*f*PolyLog[2,-E^(I*(c+d*x))]/(a*d^2)+I*f*PolyLog[2,E^(I*(c+d*x))]/(a*d^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:350
  public void test0629() {
    check( //
        "Integrate[(e+f*x)^3*Cos[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/4*I*(e+f*x)^4/(a*f)+2*(e+f*x)^3*Log[1-I*E^(I*(c+d*x))]/(a*d)-6*I*f*(e+f*x)^2*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)+12*f^2*(e+f*x)*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)+12*I*f^3*PolyLog[4,I*E^(I*(c+d*x))]/(a*d^4)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:370
  public void test0630() {
    check( //
        "Integrate[(e+f*x)^3*Sec[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-3/2*I*f*(e+f*x)^2/(a*d^2)-6*I*f^2*(e+f*x)*ArcTan[E^(I*(c+d*x))]/(a*d^3)-I*(e+f*x)^3*ArcTan[E^(I*(c+d*x))]/(a*d)+3*f^2*(e+f*x)*Log[1+E^(2*I*(c+d*x))]/(a*d^3)+3*I*f^3*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^4)+3/2*I*f*(e+f*x)^2*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-3*I*f^3*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^4)-3/2*I*f*(e+f*x)^2*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-3/2*I*f^3*PolyLog[2,-E^(2*I*(c+d*x))]/(a*d^4)-3*f^2*(e+f*x)*PolyLog[3,-I*E^(I*(c+d*x))]/(a*d^3)+3*f^2*(e+f*x)*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)-3*I*f^3*PolyLog[4,-I*E^(I*(c+d*x))]/(a*d^4)+3*I*f^3*PolyLog[4,I*E^(I*(c+d*x))]/(a*d^4)-3/2*f*(e+f*x)^2*Sec[c+d*x]/(a*d^2)-1/2*(e+f*x)^3*Sec[c+d*x]^2/(a*d)+3/2*f*(e+f*x)^2*Tan[c+d*x]/(a*d^2)+1/2*(e+f*x)^3*Sec[c+d*x]*Tan[c+d*x]/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:404
  public void test0631() {
    check( //
        "Integrate[(e+f*x)^2*Cos[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "-1/3*I*(e+f*x)^3/(b*f)+(e+f*x)^2*Log[1-I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d)+(e+f*x)^2*Log[1-I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d)-2*I*f*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d^2)-2*I*f*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d^2)+2*f^2*PolyLog[3,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b*d^3)+2*f^2*PolyLog[3,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b*d^3)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:63
  public void test0632() {
    check( //
        "Integrate[(a+b*x^2)*Sin[c+d*x]/x^5, x]", //
        "-1/12*a*d*Cos[c+d*x]/x^3-1/2*b*d*Cos[c+d*x]/x+1/24*a*d^3*Cos[c+d*x]/x-1/2*b*d^2*Cos[c]*SinIntegral[d*x]+1/24*a*d^4*Cos[c]*SinIntegral[d*x]-1/2*b*d^2*CosIntegral[d*x]*Sin[c]+1/24*a*d^4*CosIntegral[d*x]*Sin[c]-1/4*a*Sin[c+d*x]/x^4-1/2*b*Sin[c+d*x]/x^2+1/24*a*d^2*Sin[c+d*x]/x^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:81
  public void test0633() {
    check( //
        "Integrate[Sin[c+d*x]/(x^3*(a+b*x^2)), x]", //
        "-1/2*d*Cos[c+d*x]/(a*x)-b*Cos[c]*SinIntegral[d*x]/a^2-1/2*d^2*Cos[c]*SinIntegral[d*x]/a-1/2*b*Cos[c+d*Sqrt[-a]/Sqrt[b]]*SinIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]/a^2+1/2*b*Cos[c-d*Sqrt[-a]/Sqrt[b]]*SinIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]/a^2-b*CosIntegral[d*x]*Sin[c]/a^2-1/2*d^2*CosIntegral[d*x]*Sin[c]/a-1/2*Sin[c+d*x]/(a*x^2)+1/2*b*CosIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c-d*Sqrt[-a]/Sqrt[b]]/a^2+1/2*b*CosIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c+d*Sqrt[-a]/Sqrt[b]]/a^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:101
  public void test0634() {
    check( //
        "Integrate[x^2*(a+b*x^3)*Sin[c+d*x], x]", //
        "2*a*Cos[c+d*x]/d^3-120*b*x*Cos[c+d*x]/d^5-a*x^2*Cos[c+d*x]/d+20*b*x^3*Cos[c+d*x]/d^3-b*x^5*Cos[c+d*x]/d+120*b*Sin[c+d*x]/d^6+2*a*x*Sin[c+d*x]/d^2-60*b*x^2*Sin[c+d*x]/d^4+5*b*x^4*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:119
  public void test0635() {
    check( //
        "Integrate[x^2*Sin[c+d*x]/(a+b*x^3), x]", //
        "-1/3*Cos[c+(-1)^(1/3)*a^(1/3)*d/b^(1/3)]*SinIntegral[(-1)^(1/3)*a^(1/3)*d/b^(1/3)-d*x]/b+1/3*Cos[c-a^(1/3)*d/b^(1/3)]*SinIntegral[a^(1/3)*d/b^(1/3)+d*x]/b+1/3*Cos[c-(-1)^(2/3)*a^(1/3)*d/b^(1/3)]*SinIntegral[(-1)^(2/3)*a^(1/3)*d/b^(1/3)+d*x]/b+1/3*CosIntegral[a^(1/3)*d/b^(1/3)+d*x]*Sin[c-a^(1/3)*d/b^(1/3)]/b+1/3*CosIntegral[(-1)^(1/3)*a^(1/3)*d/b^(1/3)-d*x]*Sin[c+(-1)^(1/3)*a^(1/3)*d/b^(1/3)]/b+1/3*CosIntegral[(-1)^(2/3)*a^(1/3)*d/b^(1/3)+d*x]*Sin[c-(-1)^(2/3)*a^(1/3)*d/b^(1/3)]/b");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:308
  public void test0636() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(1/3)]/(c*e+d*e*x)^(2/3), x]", //
        "-3*(c+d*x)^(2/3)*Cos[a+b*(c+d*x)^(1/3)]/(b*d*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:326
  public void test0637() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(1/3)]/(c*e+d*e*x)^(7/3), x]", //
        "-18*Cos[a+b/(c+d*x)^(1/3)]/(b^3*d*e^2*(e*(c+d*x))^(1/3))+3*Cos[a+b/(c+d*x)^(1/3)]/(b*d*e^2*(c+d*x)^(2/3)*(e*(c+d*x))^(1/3))-9*Sin[a+b/(c+d*x)^(1/3)]/(b^2*d*e^2*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3))+18*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(1/3)]/(b^4*d*e^2*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:353
  public void test0638() {
    check( //
        "Integrate[x^3*(a+b*Sin[c+d*(f+g*x)^n]), x]", //
        "1/4*a*x^4-1/2*I*E^(I*c)*b*f^3*(f+g*x)*Gamma[1/n,-I*d*(f+g*x)^n]/(g^4*n*(-I*d*(f+g*x)^n)^(1/n))+1/2*I*b*f^3*(f+g*x)*Gamma[1/n,I*d*(f+g*x)^n]/(E^(I*c)*g^4*n*(I*d*(f+g*x)^n)^(1/n))+3/2*I*E^(I*c)*b*f^2*(f+g*x)^2*Gamma[2/n,-I*d*(f+g*x)^n]/(g^4*n*(-I*d*(f+g*x)^n)^(2/n))-3/2*I*b*f^2*(f+g*x)^2*Gamma[2/n,I*d*(f+g*x)^n]/(E^(I*c)*g^4*n*(I*d*(f+g*x)^n)^(2/n))-3/2*I*E^(I*c)*b*f*(f+g*x)^3*Gamma[3/n,-I*d*(f+g*x)^n]/(g^4*n*(-I*d*(f+g*x)^n)^(3/n))+3/2*I*b*f*(f+g*x)^3*Gamma[3/n,I*d*(f+g*x)^n]/(E^(I*c)*g^4*n*(I*d*(f+g*x)^n)^(3/n))+1/2*I*E^(I*c)*b*(f+g*x)^4*Gamma[4/n,-I*d*(f+g*x)^n]/(g^4*n*(-I*d*(f+g*x)^n)^(4/n))-1/2*I*b*(f+g*x)^4*Gamma[4/n,I*d*(f+g*x)^n]/(E^(I*c)*g^4*n*(I*d*(f+g*x)^n)^(4/n))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:35
  public void test0639() {
    check( //
        "Integrate[Sin[x]^5/(a+a*Sin[x])^3, x]", //
        "13/2*x/a^3+152/15*Cos[x]/a^3-13/2*Cos[x]*Sin[x]/a^3+1/5*Cos[x]*Sin[x]^4/(a+a*Sin[x])^3+11/15*Cos[x]*Sin[x]^3/(a*(a+a*Sin[x])^2)+76/15*Cos[x]*Sin[x]^2/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:55
  public void test0640() {
    check( //
        "Integrate[Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:71
  public void test0641() {
    check( //
        "Integrate[Sin[c+d*x]*(a+a*Sin[c+d*x])^(5/2), x]", //
        "-2/7*a*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/7*Cos[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/d-64/21*a^3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-16/21*a^2*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:89
  public void test0642() {
    check( //
        "Integrate[Sin[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-1/2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))+7/2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-2*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:296
  public void test0643() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^2, x]", //
        "1/2*a*c^2*x+1/3*a*c^2*Cos[e+f*x]^3/f+1/2*a*c^2*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:312
  public void test0644() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^5, x]", //
        "1/9*a^2*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^7)+2/63*a^2*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^6)+2/315*a^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:330
  public void test0645() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^4/(a+a*Sin[e+f*x]), x]", //
        "-35/2*c^4*x/a-35/3*c^4*Cos[e+f*x]^3/(a*f)-35/2*c^4*Cos[e+f*x]*Sin[e+f*x]/(a*f)-2*a^3*c^4*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^4)-14*a*c^4*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:346
  public void test0646() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^4), x]", //
        "1/7*Sec[e+f*x]^3/(a^2*f*(c^2-c^2*Sin[e+f*x])^2)+1/7*Sec[e+f*x]^3/(a^2*f*(c^4-c^4*Sin[e+f*x]))+4/7*Tan[e+f*x]/(a^2*c^4*f)+4/21*Tan[e+f*x]^3/(a^2*c^4*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:366
  public void test0647() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2/3*a*c^2*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:382
  public void test0648() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/63*a^3*c^5*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(7/2))+2/9*a^3*c^4*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(5/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:400
  public void test0649() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^2, x]", //
        "256/3*c^2*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^2*f)-64*c*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/(a^2*f)+8*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(7/2)/(a^2*f)+2/3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(9/2)/(a^2*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:420
  public void test0650() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "-1/3*a*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:452
  public void test0651() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(1/2), x]", //
        "1/4*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:470
  public void test0652() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*(a+a*Sin[e+f*x])^(3/2))-3/2*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])-12*c^4*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-6*c^3*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:527
  public void test0653() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c+d*Sin[e+f*x])^2, x]", //
        "1/8*a^2*(12*c^2+16*c*d+7*d^2)*x-1/6*a^2*(12*c^2+16*c*d+7*d^2)*Cos[e+f*x]/f-1/24*a^2*(12*c^2+16*c*d+7*d^2)*Cos[e+f*x]*Sin[e+f*x]/f-1/12*(8*c-d)*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^2/f-1/4*d^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^3/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:565
  public void test0654() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^4/(a+a*Sin[e+f*x])^3, x]", //
        "(4*c-3*d)*d^3*x/a^3+1/15*d^2*(2*c^2+10*c*d-27*d^2)*Cos[e+f*x]/(a^3*f)-1/15*(c-d)^2*(2*c^2+12*c*d+45*d^2)*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))-1/15*(c-d)*(2*c+9*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(a*f*(a+a*Sin[e+f*x])^2)-1/5*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(f*(a+a*Sin[e+f*x])^3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:657
  public void test0655() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(c+3*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:677
  public void test0656() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^(3/2), x]", //
        "-2*a*Cos[e+f*x]/((c+d)*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:693
  public void test0657() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c+d*Sin[e+f*x])^(5/2), x]", //
        "-2*a^(5/2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(5/2)*f)+2/3*a^2*(c-d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(d*(c+d)*f*(c+d*Sin[e+f*x])^(3/2))+2/3*a^3*(c-d)*(3*c+7*d)*Cos[e+f*x]/(d^2*(c+d)^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:711
  public void test0658() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-2*d^(5/2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(a^(5/2)*f)-1/4*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^(3/2)/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c^2+14*c*d+43*d^2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[c-d]/(a^(5/2)*f*Sqrt[2])-1/16*(c-d)*(3*c+11*d)*Cos[e+f*x]*Sqrt[c+d*Sin[e+f*x]]/(a*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:794
  public void test0659() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])/(c+d*Sin[e+f*x]), x]", //
        "b*x/d-2*(b*c-a*d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(d*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:44
  public void test0660() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/10*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*c*f*(c-c*Sin[e+f*x])^(11/2))+1/80*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*c^2*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:62
  public void test0661() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(3/2)/Sqrt[a+a*Sin[e+f*x]], x]", //
        "-1/3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:78
  public void test0662() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sqrt[c-c*Sin[e+f*x]]/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-c*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:122
  public void test0663() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]], x]", //
        "2/5*a*c*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6/5*a*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2/5*a*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:138
  public void test0664() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2), x]", //
        "-2/33*a*c^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])+14/99*c^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-14/45*a^3*c^2*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+14/15*a^3*c^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2/15*a^2*c^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])+2/11*c*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:154
  public void test0665() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(9/2), x]", //
        "4/13*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(9/2))-20/39*a^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(c*f*g*(c-c*Sin[e+f*x])^(7/2))-308/39*a^4*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+44/39*a^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*g*(c-c*Sin[e+f*x])^(5/2))+154/13*a^4*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^4*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:172
  public void test0666() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "-2*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2))+2*(g*Cos[e+f*x])^(5/2)/(a*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:220
  public void test0667() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(-1-2*m)*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^m, x]", //
        "ArcTanh[Sin[e+f*x]]*(a+a*Sin[e+f*x])^m*(c-c*Sin[e+f*x])^m/(f*g*(g*Cos[e+f*x])^(2*m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:372
  public void test0668() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-1/2*ArcTanh[Cos[c+d*x]]/(a*d)+Cot[c+d*x]/(a*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:408
  public void test0669() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^(3/2), x]", //
        "13/8*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-1/3*Cot[c+d*x]*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2)/d+5/24*a^2*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/4*a*Cot[c+d*x]*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:458
  public void test0670() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "1/16*a*x-1/5*a*Cos[c+d*x]^5/d+1/7*a*Cos[c+d*x]^7/d+1/16*a*Cos[c+d*x]*Sin[c+d*x]/d+1/24*a*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*a*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:474
  public void test0671() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "-9/8*a^2*x-2*a^2*ArcTanh[Cos[c+d*x]]/d+2*a^2*Cos[c+d*x]/d+2/3*a^2*Cos[c+d*x]^3/d-a^2*Cot[c+d*x]/d+1/8*a^2*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^2*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:490
  public void test0672() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "-7/2*a^3*x+7/2*a^3*ArcTanh[Cos[c+d*x]]/d-2*a^3*Cos[c+d*x]/d+1/3*a^3*Cos[c+d*x]^3/d-2*a^3*Cot[c+d*x]/d-1/3*a^3*Cot[c+d*x]^3/d-3/2*a^3*Cot[c+d*x]*Csc[c+d*x]/d-3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:508
  public void test0673() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "x/a+1/2*ArcTanh[Cos[c+d*x]]/(a*d)+Cot[c+d*x]/(a*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:524
  public void test0674() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7/(a+a*Sin[c+d*x])^2, x]", //
        "-11/16*ArcTanh[Cos[c+d*x]]/(a^2*d)+2*Cot[c+d*x]/(a^2*d)+4/3*Cot[c+d*x]^3/(a^2*d)+2/5*Cot[c+d*x]^5/(a^2*d)-11/16*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)-11/24*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:545
  public void test0675() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]], x]", //
        "11/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-2*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+11/8*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/12*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:563
  public void test0676() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-152/3465*a^2*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-38/693*a*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))+20/99*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-2/11*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:579
  public void test0677() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3/128*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(3/2)*d)-3/128*Cot[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/64*Cot[c+d*x]*Csc[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/80*Cot[c+d*x]*Csc[c+d*x]^2/(a*d*Sqrt[a+a*Sin[c+d*x]])+19/40*Cot[c+d*x]*Csc[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])-1/5*Cot[c+d*x]*Csc[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:701
  public void test0678() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^7*(a+a*Sin[c+d*x]), x]", //
        "-a*x+5/16*a*ArcTanh[Cos[c+d*x]]/d-a*Cot[c+d*x]/d+1/3*a*Cot[c+d*x]^3/d-1/5*a*Cot[c+d*x]^5/d-5/16*a*Cot[c+d*x]*Csc[c+d*x]/d+5/24*a*Cot[c+d*x]^3*Csc[c+d*x]/d-1/6*a*Cot[c+d*x]^5*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:717
  public void test0679() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^7*(a+a*Sin[c+d*x])^2, x]", //
        "-2*a^2*x-25/16*a^2*ArcTanh[Cos[c+d*x]]/d+a^2*Cos[c+d*x]/d-2*a^2*Cot[c+d*x]/d+2/3*a^2*Cot[c+d*x]^3/d-2/5*a^2*Cot[c+d*x]^5/d+7/16*a^2*Cot[c+d*x]*Csc[c+d*x]/d+7/24*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:733
  public void test0680() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^6*(a+a*Sin[c+d*x])^3, x]", //
        "13/2*a^3*x-25/8*a^3*ArcTanh[Cos[c+d*x]]/d+a^3*Cos[c+d*x]/d-1/3*a^3*Cos[c+d*x]^3/d+5*a^3*Cot[c+d*x]/d-2/3*a^3*Cot[c+d*x]^3/d-1/5*a^3*Cot[c+d*x]^5/d+23/8*a^3*Cot[c+d*x]*Csc[c+d*x]/d-3/4*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d+3/2*a^3*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:768
  public void test0681() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "-7/2*x/a^3-ArcTanh[Cos[c+d*x]]/(a^3*d)-3*Cos[c+d*x]/(a^3*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:817
  public void test0682() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-1/6*Cos[c+d*x]^6/(a*d)-1/3*Sin[c+d*x]^3/(a*d)+2/5*Sin[c+d*x]^5/(a*d)-1/7*Sin[c+d*x]^7/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:837
  public void test0683() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^n*(a+a*Sin[c+d*x])^2, x]", //
        "a^2*Sin[c+d*x]^(1+n)/(d*(1+n))+2*a^2*Sin[c+d*x]^(2+n)/(d*(2+n))-2*a^2*Sin[c+d*x]^(3+n)/(d*(3+n))-6*a^2*Sin[c+d*x]^(4+n)/(d*(4+n))+6*a^2*Sin[c+d*x]^(6+n)/(d*(6+n))+2*a^2*Sin[c+d*x]^(7+n)/(d*(7+n))-2*a^2*Sin[c+d*x]^(8+n)/(d*(8+n))-a^2*Sin[c+d*x]^(9+n)/(d*(9+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:863
  public void test0684() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "x/a+5/16*ArcTanh[Cos[c+d*x]]/(a*d)+Cot[c+d*x]/(a*d)-1/3*Cot[c+d*x]^3/(a*d)+1/5*Cot[c+d*x]^5/(a*d)-5/16*Cot[c+d*x]*Csc[c+d*x]/(a*d)+5/24*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)-1/6*Cot[c+d*x]^5*Csc[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:879
  public void test0685() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^6/(a+a*Sin[c+d*x])^2, x]", //
        "x/a^2+3/4*ArcTanh[Cos[c+d*x]]/(a^2*d)+Cot[c+d*x]/(a^2*d)-1/3*Cot[c+d*x]^3/(a^2*d)-1/5*Cot[c+d*x]^5/(a^2*d)-3/4*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+1/2*Cot[c+d*x]^3*Csc[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:895
  public void test0686() {
    check( //
        "Integrate[Cos[c+d*x]^8*Csc[c+d*x]^7/(a+a*Sin[c+d*x])^3, x]", //
        "7/16*ArcTanh[Cos[c+d*x]]/(a^3*d)+4/3*Cot[c+d*x]^3/(a^3*d)+3/5*Cot[c+d*x]^5/(a^3*d)+7/16*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)-17/24*Cot[c+d*x]*Csc[c+d*x]^3/(a^3*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:939
  public void test0687() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "-x/a^2-2*Sec[c+d*x]/(a^2*d)+4/3*Sec[c+d*x]^3/(a^2*d)-2/5*Sec[c+d*x]^5/(a^2*d)+Tan[c+d*x]/(a^2*d)-1/3*Tan[c+d*x]^3/(a^2*d)+2/5*Tan[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1018
  public void test0688() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "3*a^3*x-3*a^3*Cos[c+d*x]/d-2*a^5*Cos[c+d*x]^3/(d*(a-a*Sin[c+d*x])^2)+1/3*Sec[c+d*x]^3*(a+a*Sin[c+d*x])^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1052
  public void test0689() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "1/9*Sec[c+d*x]^3/(d*(a+a*Sin[c+d*x])^3)-1/21*Sec[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^2)-1/21*Sec[c+d*x]^3/(d*(a^3+a^3*Sin[c+d*x]))+4/21*Tan[c+d*x]/(a^3*d)+4/63*Tan[c+d*x]^3/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1131
  public void test0690() {
    check( //
        "Integrate[Sec[c+d*x]^8*Sin[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "1/3*a^2*Sec[c+d*x]^3/d-3/5*a^2*Sec[c+d*x]^5/d+2/7*a^2*Sec[c+d*x]^7/d+2/5*a^2*Tan[c+d*x]^5/d+2/7*a^2*Tan[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1229
  public void test0691() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "(A-B)*(a+a*Sin[c+d*x])^4/(a^3*d)-4/5*(A-2*B)*(a+a*Sin[c+d*x])^5/(a^4*d)+1/6*(A-5*B)*(a+a*Sin[c+d*x])^6/(a^5*d)+1/7*B*(a+a*Sin[c+d*x])^7/(a^6*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1245
  public void test0692() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "4/5*(A-B)*(a+a*Sin[c+d*x])^5/(a^3*d)-2/3*(A-2*B)*(a+a*Sin[c+d*x])^6/(a^4*d)+1/7*(A-5*B)*(a+a*Sin[c+d*x])^7/(a^5*d)+1/8*B*(a+a*Sin[c+d*x])^8/(a^6*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1261
  public void test0693() {
    check( //
        "Integrate[Cos[c+d*x]^7*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "8/7*(A-B)*(a+a*Sin[c+d*x])^7/(a^4*d)-1/2*(3*A-5*B)*(a+a*Sin[c+d*x])^8/(a^5*d)+2/3*(A-3*B)*(a+a*Sin[c+d*x])^9/(a^6*d)-1/10*(A-7*B)*(a+a*Sin[c+d*x])^10/(a^7*d)-1/11*B*(a+a*Sin[c+d*x])^11/(a^8*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1277
  public void test0694() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "1/9*(A+B)*Sec[c+d*x]^9*(a+a*Sin[c+d*x])^3/d+2/21*(2*A-B)*Sec[c+d*x]^7*(a^3+a^3*Sin[c+d*x])/d+5/21*a^3*(2*A-B)*Tan[c+d*x]/d+10/63*a^3*(2*A-B)*Tan[c+d*x]^3/d+1/21*a^3*(2*A-B)*Tan[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1357
  public void test0695() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+b*Sin[c+d*x]), x]", //
        "1/2*b*ArcTanh[Cos[c+d*x]]/d-1/3*a*Cot[c+d*x]^3/d-1/2*b*Cot[c+d*x]*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1391
  public void test0696() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]/(a+b*Sin[c+d*x])^3, x]", //
        "-b*(3*a^2-2*b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^3*(a^2-b^2)^(3/2)*d)-ArcTanh[Cos[c+d*x]]/(a^3*d)+1/2*Cos[c+d*x]/(a*d*(a+b*Sin[c+d*x])^2)+1/2*(a^2-2*b^2)*Cos[c+d*x]/(a^2*(a^2-b^2)*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1419
  public void test0697() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^9*(a+b*Sin[c+d*x]), x]", //
        "-3/128*a*ArcTanh[Cos[c+d*x]]/d-1/5*b*Cot[c+d*x]^5/d-1/7*b*Cot[c+d*x]^7/d-3/128*a*Cot[c+d*x]*Csc[c+d*x]/d-1/64*a*Cot[c+d*x]*Csc[c+d*x]^3/d+1/16*a*Cot[c+d*x]*Csc[c+d*x]^5/d-1/8*a*Cot[c+d*x]^3*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1545
  public void test0698() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^8*(a+b*Sin[c+d*x]), x]", //
        "-1/6*b*Cot[c+d*x]^6/d-1/3*a*Csc[c+d*x]^3/d+2/5*a*Csc[c+d*x]^5/d-1/7*a*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1658
  public void test0699() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^5/(a+b*Sin[c+d*x]), x]", //
        "1/8*(a^4+4*a^2*b^2-8*b^4)*ArcTanh[Cos[c+d*x]]/(a^5*d)-1/3*b*(a^2-3*b^2)*Cot[c+d*x]/(a^4*d)+1/8*(a^2-4*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a^3*d)+1/3*b*Cot[c+d*x]*Csc[c+d*x]^2/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a*d)+2*b^3*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]*Sqrt[a^2-b^2]/(a^5*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1674
  public void test0700() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6/(a+b*Sin[c+d*x]), x]", //
        "2*b^2*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(a^6*d)+1/8*b*(3*a^4-12*a^2*b^2+8*b^4)*ArcTanh[Cos[c+d*x]]/(a^6*d)-1/15*(3*a^4-20*a^2*b^2+15*b^4)*Cot[c+d*x]/(a^5*d)-1/8*b*(5*a^2-4*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a^4*d)+1/15*(6*a^2-5*b^2)*Cot[c+d*x]*Csc[c+d*x]^2/(a^3*d)+1/4*b*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d)-1/5*Cot[c+d*x]*Csc[c+d*x]^4/(a*d)");
  }
}
