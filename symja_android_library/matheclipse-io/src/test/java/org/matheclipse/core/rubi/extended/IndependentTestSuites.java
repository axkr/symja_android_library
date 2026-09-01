package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 0 Independent test suites of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class IndependentTestSuites extends AbstractRubiTestCase {
  static boolean init = true;

  public IndependentTestSuites(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("IndependentTestSuites");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // Apostol Problems.input:27
  public void test0001() {
    check( //
        "Integrate[1/(1+x^2)^(3/2), x]", //
        "x/Sqrt[1+x^2]");
  }

  // Apostol Problems.input:157
  public void test0002() {
    check( //
        "Integrate[ArcTan[Sqrt[x]]/((1+x)*Sqrt[x]), x]", //
        "ArcTan[Sqrt[x]]^2");
  }

  // Apostol Problems.input:205
  public void test0003() {
    check( //
        "Integrate[(1+x+4*x^2)/(-1+x^3), x]", //
        "2*Log[1-x]+Log[1+x+x^2]");
  }

  // Charlwood Problems.input:65
  public void test0004() {
    check( //
        "Integrate[x*Log[x+Sqrt[-1+x^2]]/Sqrt[-1+x^2], x]", //
        "-x+Log[x+Sqrt[-1+x^2]]*Sqrt[-1+x^2]");
  }

  // Charlwood Problems.input:143
  public void test0005() {
    check( //
        "Integrate[x*ArcTan[x]^2*Log[1+x^2], x]", //
        "3*x*ArcTan[x]-3/2*ArcTan[x]^2-1/2*x^2*ArcTan[x]^2-3/2*Log[1+x^2]-x*ArcTan[x]*Log[1+x^2]+1/2*(1+x^2)*ArcTan[x]^2*Log[1+x^2]+1/4*Log[1+x^2]^2");
  }

  // Hearn Problems.input:23
  public void test0006() {
    check( //
        "Integrate[x^2/((a^2+x^2)*(b^2+x^2)), x]", //
        "a*ArcTan[x/a]/(a^2-b^2)-b*ArcTan[x/b]/(a^2-b^2)");
  }

  // Hearn Problems.input:87
  public void test0007() {
    check( //
        "Integrate[Log[a^2+x^2], x]", //
        "-2*x+2*a*ArcTan[x/a]+x*Log[a^2+x^2]");
  }

  // Hearn Problems.input:155
  public void test0008() {
    check( //
        "Integrate[d^x*x*Sin[x], x]", //
        "2*d^x*Cos[x]*Log[d]/(1+Log[d]^2)^2-d^x*x*Cos[x]/(1+Log[d]^2)+d^x*Sin[x]/(1+Log[d]^2)^2-d^x*Log[d]^2*Sin[x]/(1+Log[d]^2)^2+d^x*x*Log[d]*Sin[x]/(1+Log[d]^2)");
  }

  // Hearn Problems.input:163
  public void test0009() {
    check( //
        "Integrate[x^2*Sin[k*x]^3, x]", //
        "14/9*Cos[k*x]/k^3-2/3*x^2*Cos[k*x]/k-2/27*Cos[k*x]^3/k^3+4/3*x*Sin[k*x]/k^2-1/3*x^2*Cos[k*x]*Sin[k*x]^2/k+2/9*x*Sin[k*x]^3/k^2");
  }

  // Hearn Problems.input:283
  public void test0010() {
    check( //
        "Integrate[Cos[a+x]*Sin[x], x]", //
        "-1/4*Cos[a+2*x]-1/2*x*Sin[a]");
  }

  // Hearn Problems.input:291
  public void test0011() {
    check( //
        "Integrate[1/(x^(1/3)+x^(1/2)), x]", //
        "6*x^(1/6)-3*x^(1/3)-6*Log[1+x^(1/6)]+2*Sqrt[x]");
  }

  // Hearn Problems.input:309
  public void test0012() {
    check( //
        "Integrate[Log[x]+Log[1+x]+Log[2+x], x]", //
        "-3*x+x*Log[x]+(1+x)*Log[1+x]+(2+x)*Log[2+x]");
  }

  // Moses Problems.input:139
  public void test0013() {
    check( //
        "Integrate[x/Sqrt[5+2*x+x^2], x]", //
        "-ArcSinh[1/2*(1+x)]+Sqrt[5+2*x+x^2]");
  }

  // Moses Problems.input:147
  public void test0014() {
    check( //
        "Integrate[(1+x)/Sqrt[2*x-x^2], x]", //
        "-2*ArcSin[1-x]-Sqrt[2*x-x^2]");
  }

  // Stewart Problems.input:24
  public void test0015() {
    check( //
        "Integrate[E^x*x^2, x]", //
        "2*E^x-2*E^x*x+E^x*x^2");
  }

  // Stewart Problems.input:64
  public void test0016() {
    check( //
        "Integrate[Log[x]*Sqrt[x], x]", //
        "-4/9*x^(3/2)+2/3*x^(3/2)*Log[x]");
  }

  // Stewart Problems.input:82
  public void test0017() {
    check( //
        "Integrate[Cos[x]^4*Sin[x]^4, x]", //
        "3/128*x+3/128*Cos[x]*Sin[x]+1/64*Cos[x]^3*Sin[x]-1/16*Cos[x]^5*Sin[x]-1/8*Cos[x]^5*Sin[x]^3");
  }

  // Stewart Problems.input:140
  public void test0018() {
    check( //
        "Integrate[x^3/Sqrt[4+x^2], x]", //
        "1/3*(4+x^2)^(3/2)-4*Sqrt[4+x^2]");
  }

  // Stewart Problems.input:174
  public void test0019() {
    check( //
        "Integrate[1/(1+x^2)^2, x]", //
        "1/2*x/(1+x^2)+1/2*ArcTan[x]");
  }

  // Stewart Problems.input:338
  public void test0020() {
    check( //
        "Integrate[Log[1/2*x], x]", //
        "-x+x*Log[1/2*x]");
  }

  // Timofeev Problems.input:55
  public void test0021() {
    check( //
        "Integrate[1/(Cos[x]^2*Sin[x]^2), x]", //
        "-Cot[x]+Tan[x]");
  }

  // Timofeev Problems.input:173
  public void test0022() {
    check( //
        "Integrate[(3+2*x^3)/(-9*x+x^5), x]", //
        "-1/3*Log[x]+1/12*Log[9-x^4]+ArcTan[x/Sqrt[3]]/Sqrt[3]-ArcTanh[x/Sqrt[3]]/Sqrt[3]");
  }

  // Timofeev Problems.input:214
  public void test0023() {
    check( //
        "Integrate[(B+A*x)/(c+2*b*x+a*x^2)^2, x]", //
        "1/2*(-b*B+A*c+(A*b-a*B)*x)/((b^2-a*c)*(c+2*b*x+a*x^2))-1/2*(A*b-a*B)*ArcTanh[(b+a*x)/Sqrt[b^2-a*c]]/(b^2-a*c)^(3/2)");
  }

  // Timofeev Problems.input:313
  public void test0024() {
    check( //
        "Integrate[Sqrt[-5+x]*Sqrt[3+x]/((-1+x)*(-25+x^2)), x]", //
        "1/6*ArcTan[1/4*Sqrt[-5+x]*Sqrt[3+x]]+1/3*ArcTanh[Sqrt[5]*Sqrt[3+x]/Sqrt[-5+x]]/Sqrt[5]");
  }

  // Timofeev Problems.input:330
  public void test0025() {
    check( //
        "Integrate[1/(9+3*x-5*x^2+x^3)^(2/3), x]", //
        "3/4*(3-x)*(1+x)/(9+3*x-5*x^2+x^3)^(2/3)");
  }

  // Timofeev Problems.input:363
  public void test0026() {
    check( //
        "Integrate[(5+x^2)/((1+x^2)^2*Sqrt[1-x^2]), x]", //
        "2*ArcTan[x*Sqrt[2]/Sqrt[1-x^2]]*Sqrt[2]+x*Sqrt[1-x^2]/(1+x^2)");
  }

  // Timofeev Problems.input:391
  public void test0027() {
    check( //
        "Integrate[(3*x^2+2*x^3)/((-3+x+2*x^2)*Sqrt[-3+2*x+x^2]), x]", //
        "Sqrt[-3+2*x+x^2]+1/2*Sqrt[-3+2*x+x^2]/(1-x)");
  }

  // Timofeev Problems.input:425
  public void test0028() {
    check( //
        "Integrate[x^3/((-1+x^4)*Sqrt[1+2*x^8]), x]", //
        "-1/4*ArcTanh[(1+2*x^4)/(Sqrt[3]*Sqrt[1+2*x^8])]/Sqrt[3]");
  }

  // Timofeev Problems.input:461
  public void test0029() {
    check( //
        "Integrate[Sin[-1/12*Pi+3*x]^3, x]", //
        "-1/3*Cos[1/12*Pi-3*x]+1/9*Cos[1/12*Pi-3*x]^3");
  }

  // Timofeev Problems.input:475
  public void test0030() {
    check( //
        "Integrate[Cos[x]^6*Sin[x]^4, x]", //
        "3/256*x+3/256*Cos[x]*Sin[x]+1/128*Cos[x]^3*Sin[x]+1/160*Cos[x]^5*Sin[x]-3/80*Cos[x]^7*Sin[x]-1/10*Cos[x]^7*Sin[x]^3");
  }

  // Timofeev Problems.input:484
  public void test0031() {
    check( //
        "Integrate[1/(Cos[1/4*Pi+2*x]*Sin[1/4*Pi+2*x]^3), x]", //
        "-1/4*Cot[1/4*Pi+2*x]^2+1/2*Log[Tan[1/4*Pi+2*x]]");
  }

  // Timofeev Problems.input:509
  public void test0032() {
    check( //
        "Integrate[Cos[x]^4*Cos[4*x], x]", //
        "1/16*x+1/8*Sin[2*x]+3/32*Sin[4*x]+1/24*Sin[6*x]+1/128*Sin[8*x]");
  }

  // Timofeev Problems.input:574
  public void test0033() {
    check( //
        "Integrate[Cos[x]*(-Cos[x]^2-5*Sin[x]^2)^(3/2), x]", //
        "3/16*ArcTan[2*Sin[x]/Sqrt[-1-4*Sin[x]^2]]+1/4*Sin[x]*(-1-4*Sin[x]^2)^(3/2)-3/8*Sin[x]*Sqrt[-1-4*Sin[x]^2]");
  }

  // Timofeev Problems.input:629
  public void test0034() {
    check( //
        "Integrate[(-10+x^2)^(5/2)/x, x]", //
        "-10/3*(-10+x^2)^(3/2)+1/5*(-10+x^2)^(5/2)-100*ArcTan[Sqrt[-10+x^2]/Sqrt[10]]*Sqrt[10]+100*Sqrt[-10+x^2]");
  }

  // Timofeev Problems.input:639
  public void test0035() {
    check( //
        "Integrate[1/(x^4*(-8+x^2)^(3/2)), x]", //
        "1/24/(x^3*Sqrt[-8+x^2])+1/48/(x*Sqrt[-8+x^2])-1/192*x/Sqrt[-8+x^2]");
  }

  // Timofeev Problems.input:679
  public void test0036() {
    check( //
        "Integrate[(-1)/E^x+E^x, x]", //
        "1/E^x+E^x");
  }

  // Timofeev Problems.input:713
  public void test0037() {
    check( //
        "Integrate[(E^x+E^(5*x))/(-1+E^x-E^(2*x)+E^(3*x)), x]", //
        "E^x+1/2*E^(2*x)-ArcTan[E^x]+Log[1-E^x]-1/2*Log[1+E^(2*x)]");
  }

  // Timofeev Problems.input:727
  public void test0038() {
    check( //
        "Integrate[1/(E^(1/2*x)*x^3), x]", //
        "(-1/2)/(E^(1/2*x)*x^2)+1/4/(E^(1/2*x)*x)+1/8*ExpIntegralEi[-1/2*x]");
  }

  // Timofeev Problems.input:831
  public void test0039() {
    check( //
        "Integrate[(1+x^4)*(1-2*Log[x]+Log[x]^3), x]", //
        "-3*x+169/625*x^5+4*x*Log[x]-44/125*x^5*Log[x]-3*x*Log[x]^2-3/25*x^5*Log[x]^2+x*Log[x]^3+1/5*x^5*Log[x]^3");
  }

  // Timofeev Problems.input:861
  public void test0040() {
    check( //
        "Integrate[Log[-1+x]/x^3, x]", //
        "1/2/x+1/2*Log[1-x]-1/2*Log[-1+x]/x^2-1/2*Log[x]");
  }

  // Timofeev Problems.input:885
  public void test0041() {
    check( //
        "Integrate[(1-x^2)^(3/2)*ArcSin[x], x]", //
        "-5/16*x^2+1/16*x^4+1/4*x*(1-x^2)^(3/2)*ArcSin[x]+3/16*ArcSin[x]^2+3/8*x*ArcSin[x]*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:905
  public void test0042() {
    check( //
        "Integrate[x*ArcTan[x]/(1+x^2)^3, x]", //
        "1/16*x/(1+x^2)^2+3/32*x/(1+x^2)+3/32*ArcTan[x]-1/4*ArcTan[x]/(1+x^2)^2");
  }

  // Welz Problems.input:24
  public void test0043() {
    check( //
        "Integrate[1/(2*x+Sqrt[1+x^2])^2, x]", //
        "4/3*x/(1-3*x^2)-1/3*ArcTanh[x*Sqrt[3]]/Sqrt[3]+1/3*ArcTanh[1/2*Sqrt[3]*Sqrt[1+x^2]]/Sqrt[3]-2/3*Sqrt[1+x^2]/(1-3*x^2)");
  }

  // Apostol Problems.input:166
  public void test0044() {
    check( //
        "Integrate[1/Sqrt[(b-x)*(-a+x)], x]", //
        "-ArcTan[1/2*(a+b-2*x)/Sqrt[-a*b+(a+b)*x-x^2]]");
  }

  // Apostol Problems.input:206
  public void test0045() {
    check( //
        "Integrate[x^4/(4+5*x^2+x^4), x]", //
        "x-8/3*ArcTan[1/2*x]+1/3*ArcTan[x]");
  }

  // Apostol Problems.input:244
  public void test0046() {
    check( //
        "Integrate[x/Sqrt[1+x+x^2], x]", //
        "-1/2*ArcSinh[(1+2*x)/Sqrt[3]]+Sqrt[1+x+x^2]");
  }

  // Bondarenko Problems.input:36
  public void test0047() {
    check( //
        "Integrate[Log[1+Exp[x]]/(1+Exp[2*x]), x]", //
        "-1/2*Log[(1/2-1/2*I)*(I-E^x)]*Log[1+E^x]-1/2*Log[(-1/2-1/2*I)*(I+E^x)]*Log[1+E^x]-PolyLog[2,-E^x]-1/2*PolyLog[2,(1/2-1/2*I)*(1+E^x)]-1/2*PolyLog[2,(1/2+1/2*I)*(1+E^x)]");
  }

  // Hearn Problems.input:24
  public void test0048() {
    check( //
        "Integrate[x/((-1+x)*(1+x^2)), x]", //
        "1/2*ArcTan[x]+1/2*Log[1-x]-1/4*Log[1+x^2]");
  }

  // Hearn Problems.input:62
  public void test0049() {
    check( //
        "Integrate[1/(-2+x^6), x]", //
        "-1/3*ArcTanh[x/2^(1/6)]/2^(5/6)+1/12*Log[2^(1/3)-2^(1/6)*x+x^2]/2^(5/6)-1/12*Log[2^(1/3)+2^(1/6)*x+x^2]/2^(5/6)+1/2*ArcTan[1/Sqrt[3]-2^(5/6)*x/Sqrt[3]]/(2^(5/6)*Sqrt[3])-1/2*ArcTan[1/Sqrt[3]+2^(5/6)*x/Sqrt[3]]/(2^(5/6)*Sqrt[3])");
  }

  // Hearn Problems.input:80
  public void test0050() {
    check( //
        "Integrate[1/(x^2*Log[x]^2), x]", //
        "-ExpIntegralEi[-Log[x]]+(-1)/(x*Log[x])");
  }

  // Hearn Problems.input:88
  public void test0051() {
    check( //
        "Integrate[x*Log[a^2+x^2], x]", //
        "-1/2*x^2+1/2*(a^2+x^2)*Log[a^2+x^2]");
  }

  // Hearn Problems.input:156
  public void test0052() {
    check( //
        "Integrate[d^x*x*Cos[x], x]", //
        "d^x*Cos[x]/(1+Log[d]^2)^2-d^x*Cos[x]*Log[d]^2/(1+Log[d]^2)^2+d^x*x*Cos[x]*Log[d]/(1+Log[d]^2)-2*d^x*Log[d]*Sin[x]/(1+Log[d]^2)^2+d^x*x*Sin[x]/(1+Log[d]^2)");
  }

  // Hearn Problems.input:167
  public void test0053() {
    check( //
        "Integrate[Cos[x]/(Sin[x]*Tan[1/2*x]), x]", //
        "-x-Cot[1/2*x]");
  }

  // Hearn Problems.input:179
  public void test0054() {
    check( //
        "Integrate[1/(a+E^(m*x)*b), x]", //
        "x/a-Log[a+E^(m*x)*b]/(a*m)");
  }

  // Hearn Problems.input:236
  public void test0055() {
    check( //
        "Integrate[Sqrt[x+Sqrt[a^2+x^2]]/x, x]", //
        "-2*ArcTan[Sqrt[x+Sqrt[a^2+x^2]]/Sqrt[a]]*Sqrt[a]-2*ArcTanh[Sqrt[x+Sqrt[a^2+x^2]]/Sqrt[a]]*Sqrt[a]+2*Sqrt[x+Sqrt[a^2+x^2]]");
  }

  // Hearn Problems.input:252
  public void test0056() {
    check( //
        "Integrate[r/Sqrt[-alpha^2+2*e*r^2-2*k*r^4], r]", //
        "-1/2*ArcTan[(e-2*k*r^2)/(Sqrt[2]*Sqrt[k]*Sqrt[-alpha^2+2*e*r^2-2*k*r^4])]/(Sqrt[2]*Sqrt[k])");
  }

  // Hearn Problems.input:268
  public void test0057() {
    check( //
        "Integrate[x^3/(b+a*x^2), x]", //
        "1/2*x^2/a-1/2*b*Log[b+a*x^2]/a^2");
  }

  // Hearn Problems.input:284
  public void test0058() {
    check( //
        "Integrate[(1+Sin[x])^(1/2), x]", //
        "-2*Cos[x]/Sqrt[1+Sin[x]]");
  }

  // Hearn Problems.input:310
  public void test0059() {
    check( //
        "Integrate[1/(5+x^3), x]", //
        "1/3*Log[5^(1/3)+x]/5^(2/3)-1/6*Log[5^(2/3)-5^(1/3)*x+x^2]/5^(2/3)-ArcTan[(5^(1/3)-2*x)/(5^(1/3)*Sqrt[3])]/(5^(2/3)*Sqrt[3])");
  }

  // Hebisch Problems.input:8
  public void test0060() {
    check( //
        "Integrate[(1-x^3+x^4-x^5+x^6)*Exp[x], x]", //
        "871*E^x-870*E^x*x+435*E^x*x^2-145*E^x*x^3+36*E^x*x^4-7*E^x*x^5+E^x*x^6");
  }

  // Moses Problems.input:18
  public void test0061() {
    check( //
        "Integrate[Cos[x]*Csc[x]^2/Sin[x]^2, x]", //
        "-1/3*Csc[x]^3");
  }

  // Moses Problems.input:28
  public void test0062() {
    check( //
        "Integrate[E^(x^2)*x, x]", //
        "1/2*E^(x^2)");
  }

  // Moses Problems.input:80
  public void test0063() {
    check( //
        "Integrate[1/(A^4-A^2*B^2+(-A^2+B^2)*x^2), x]", //
        "ArcTanh[x/A]/(A*(A^2-B^2))");
  }

  // Moses Problems.input:94
  public void test0064() {
    check( //
        "Integrate[(E^x+x)/E^x, x]", //
        "(-1)/E^x+x-x/E^x");
  }

  // Moses Problems.input:108
  public void test0065() {
    check( //
        "Integrate[1/((1+ArcSin[x]^2)*Sqrt[1-x^2]), x]", //
        "ArcTan[ArcSin[x]]");
  }

  // Stewart Problems.input:175
  public void test0066() {
    check( //
        "Integrate[1/((-1+x)*(2+x)), x]", //
        "1/3*Log[1-x]-1/3*Log[2+x]");
  }

  // Stewart Problems.input:373
  public void test0067() {
    check( //
        "Integrate[x^5/(x^2+Sqrt[2]), x]", //
        "1/4*x^4+Log[x^2+Sqrt[2]]-x^2/Sqrt[2]");
  }

  // Timofeev Problems.input:48
  public void test0068() {
    check( //
        "Integrate[Sin[1/4*x]*Sin[x], x]", //
        "2/3*Sin[3/4*x]-2/5*Sin[5/4*x]");
  }

  // Timofeev Problems.input:58
  public void test0069() {
    check( //
        "Integrate[Cot[3/4*x]^2, x]", //
        "-x-4/3*Cot[3/4*x]");
  }

  // Timofeev Problems.input:80
  public void test0070() {
    check( //
        "Integrate[1/(x*Sqrt[a^2+x^2]), x]", //
        "-ArcTanh[Sqrt[a^2+x^2]/a]/a");
  }

  // Timofeev Problems.input:120
  public void test0071() {
    check( //
        "Integrate[Log[Cos[x]]*Sec[x]^2, x]", //
        "-x+Tan[x]+Log[Cos[x]]*Tan[x]");
  }

  // Timofeev Problems.input:148
  public void test0072() {
    check( //
        "Integrate[(-5+2*x^2)/(6-5*x^2+x^4), x]", //
        "-ArcTanh[x/Sqrt[2]]/Sqrt[2]-ArcTanh[x/Sqrt[3]]/Sqrt[3]");
  }

  // Timofeev Problems.input:162
  public void test0073() {
    check( //
        "Integrate[(1+x^4)/(-1+x-x^2+x^3), x]", //
        "x+1/2*x^2-ArcTan[x]+Log[1-x]-1/2*Log[1+x^2]");
  }

  // Timofeev Problems.input:215
  public void test0074() {
    check( //
        "Integrate[(-41+55*x-27*x^2+5*x^3)/(5-4*x+x^2)^2, x]", //
        "(1-x)/(5-4*x+x^2)-2*ArcTan[2-x]+5/2*Log[5-4*x+x^2]");
  }

  // Timofeev Problems.input:341
  public void test0075() {
    check( //
        "Integrate[1/((-1+x^4)*Sqrt[2+x^2]), x]", //
        "-1/2*ArcTan[x/Sqrt[2+x^2]]-1/2*ArcTanh[x*Sqrt[3]/Sqrt[2+x^2]]/Sqrt[3]");
  }

  // Timofeev Problems.input:354
  public void test0076() {
    check( //
        "Integrate[1/(x^6*Sqrt[2+x^2]), x]", //
        "-1/10*Sqrt[2+x^2]/x^5+1/15*Sqrt[2+x^2]/x^3-1/15*Sqrt[2+x^2]/x");
  }

  // Timofeev Problems.input:434
  public void test0077() {
    check( //
        "Integrate[(1+x^4)^(3/4)/(2+x^4)^2, x]", //
        "1/8*x*(1+x^4)^(3/4)/(2+x^4)+3/16*ArcTan[x/(2^(1/4)*(1+x^4)^(1/4))]/2^(3/4)+3/16*ArcTanh[x/(2^(1/4)*(1+x^4)^(1/4))]/2^(3/4)");
  }

  // Timofeev Problems.input:476
  public void test0078() {
    check( //
        "Integrate[Cos[x]^6*Sin[x]^7, x]", //
        "-1/7*Cos[x]^7+1/3*Cos[x]^9-3/11*Cos[x]^11+1/13*Cos[x]^13");
  }

  // Timofeev Problems.input:495
  public void test0079() {
    check( //
        "Integrate[Sec[1/4*Pi+1/2*x]^3*Tan[1/4*Pi+1/2*x]^2, x]", //
        "-1/4*ArcTanh[Sin[1/4*Pi+1/2*x]]-1/4*Sec[1/4*Pi+1/2*x]*Tan[1/4*Pi+1/2*x]+1/2*Sec[1/4*Pi+1/2*x]^3*Tan[1/4*Pi+1/2*x]");
  }

  // Timofeev Problems.input:547
  public void test0080() {
    check( //
        "Integrate[(-Sqrt[4-3*Tan[x]]+3*Tan[x])/(Cos[x]^2*(4-3*Tan[x])^(3/2)), x]", //
        "1/3*Log[4-3*Tan[x]]+8/3/Sqrt[4-3*Tan[x]]+2/3*Sqrt[4-3*Tan[x]]");
  }

  // Timofeev Problems.input:680
  public void test0081() {
    check( //
        "Integrate[((-1)/E^x+E^x)^2, x]", //
        "(-1/2)/E^(2*x)+1/2*E^(2*x)-2*x");
  }

  // Timofeev Problems.input:690
  public void test0082() {
    check( //
        "Integrate[a^(k*x)-a^(l*x), x]", //
        "a^(k*x)/(k*Log[a])-a^(l*x)/(l*Log[a])");
  }

  // Timofeev Problems.input:728
  public void test0083() {
    check( //
        "Integrate[a^(3*x)*x^2, x]", //
        "2/27*a^(3*x)/Log[a]^3-2/9*a^(3*x)*x/Log[a]^2+1/3*a^(3*x)*x^2/Log[a]");
  }

  // Timofeev Problems.input:763
  public void test0084() {
    check( //
        "Integrate[E^x*x*Cos[x], x]", //
        "1/2*E^x*x*Cos[x]-1/2*E^x*Sin[x]+1/2*E^x*x*Sin[x]");
  }

  // Timofeev Problems.input:864
  public void test0085() {
    check( //
        "Integrate[((-1)/E^x+E^x)*Log[1+E^(2*x)], x]", //
        "-2*E^x+Log[1+E^(2*x)]/E^x+E^x*Log[1+E^(2*x)]");
  }

  // Timofeev Problems.input:886
  public void test0086() {
    check( //
        "Integrate[x*(1-x^2)^(3/2)*ArcSin[x], x]", //
        "1/5*x-2/15*x^3+1/25*x^5-1/5*(1-x^2)^(5/2)*ArcSin[x]");
  }

  // Timofeev Problems.input:906
  public void test0087() {
    check( //
        "Integrate[x^2*ArcTan[x]/(1+x^2), x]", //
        "x*ArcTan[x]-1/2*ArcTan[x]^2-1/2*Log[1+x^2]");
  }

  // Timofeev Problems.input:943
  public void test0088() {
    check( //
        "Integrate[ArcTan[-a+x]/(a+x), x]", //
        "ArcTan[a-x]*Log[2/(1-I*(a-x))]-ArcTan[a-x]*Log[-2*(a+x)/((I-2*a)*(1-I*(a-x)))]-1/2*I*PolyLog[2,1+(-2)/(1-I*(a-x))]+1/2*I*PolyLog[2,1+2*(a+x)/((I-2*a)*(1-I*(a-x)))]");
  }

  // Welz Problems.input:130
  public void test0089() {
    check( //
        "Integrate[(-84-576*x-400*x^2+2560*x^3)/(9+24*x-12*x^2+80*x^3+320*x^4), x]", //
        "2*Log[9+24*x-12*x^2+80*x^3+320*x^4]+2*ArcTan[1/5*(7-40*x)/Sqrt[11]]*Sqrt[11]-2*ArcTan[1/6*(57+30*x-40*x^2+800*x^3)/Sqrt[11]]*Sqrt[11]");
  }

  // Welz Problems.input:147
  public void test0090() {
    check( //
        "Integrate[(a+b*x)/((1+x^2)^(1/4)*(2+x^2)), x]", //
        "-1/2*a*ArcTan[(1+Sqrt[1+x^2])/(x*(1+x^2)^(1/4))]-1/2*a*ArcTanh[(1-Sqrt[1+x^2])/(x*(1+x^2)^(1/4))]-b*ArcTan[(1-Sqrt[1+x^2])/((1+x^2)^(1/4)*Sqrt[2])]/Sqrt[2]-b*ArcTanh[(1+Sqrt[1+x^2])/((1+x^2)^(1/4)*Sqrt[2])]/Sqrt[2]");
  }

  // Welz Problems.input:175
  public void test0091() {
    check( //
        "Integrate[x/((-10+x^3-6*Sqrt[3])*Sqrt[-1+x^3]), x]", //
        "1/6*ArcTan[3^(1/4)*(1-x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[-1+x^3])]*(2-Sqrt[3])/(3^(1/4)*Sqrt[2])+1/3*ArcTan[3^(1/4)*(1+2*x+Sqrt[3])/(Sqrt[2]*Sqrt[-1+x^3])]*(2-Sqrt[3])/(3^(1/4)*Sqrt[2])+1/2*ArcTanh[3^(1/4)*(1-x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[-1+x^3])]*(2-Sqrt[3])/(3^(3/4)*Sqrt[2])-1/3*ArcTanh[(1-Sqrt[3])*Sqrt[-1+x^3]/(3^(3/4)*Sqrt[2])]*(2-Sqrt[3])/(3^(3/4)*Sqrt[2])");
  }

  // Apostol Problems.input:159
  public void test0092() {
    check( //
        "Integrate[E^ArcTan[x]*x/(1+x^2)^(3/2), x]", //
        "-1/2*E^ArcTan[x]*(1-x)/Sqrt[1+x^2]");
  }

  // Apostol Problems.input:171
  public void test0093() {
    check( //
        "Integrate[(3+5*x)/(-3+2*x+x^2), x]", //
        "2*Log[1-x]+3*Log[3+x]");
  }

  // Apostol Problems.input:217
  public void test0094() {
    check( //
        "Integrate[(-3+x)/(2*x+3*x^2+x^3), x]", //
        "-3/2*Log[x]+4*Log[1+x]-5/2*Log[2+x]");
  }

  // Apostol Problems.input:237
  public void test0095() {
    check( //
        "Integrate[1/(b*Cos[x]+a*Sin[x])^2, x]", //
        "Sin[x]/(b*(b*Cos[x]+a*Sin[x]))");
  }

  // Bondarenko Problems.input:28
  public void test0096() {
    check( //
        "Integrate[Sqrt[1+Exp[-x]]/(-Exp[-x]+Exp[x]), x]", //
        "-ArcTanh[Sqrt[1+1/E^x]/Sqrt[2]]*Sqrt[2]");
  }

  // Hearn Problems.input:15
  public void test0097() {
    check( //
        "Integrate[1/(c+b*x+a*x^2), x]", //
        "-2*ArcTanh[(b+2*a*x)/Sqrt[b^2-4*a*c]]/Sqrt[b^2-4*a*c]");
  }

  // Timofeev Problems.input:175
  public void test0098() {
    check( //
        "Integrate[1/((1+x^2)*(2+x^2)*(3+x^2)*(4+x^2)), x]", //
        "-1/12*ArcTan[1/2*x]+1/6*ArcTan[x]-1/2*ArcTan[x/Sqrt[2]]/Sqrt[2]+1/2*ArcTan[x/Sqrt[3]]/Sqrt[3]");
  }

  // Apostol Problems.input:42
  public void test0099() {
    check( //
        "Integrate[x*Cos[x]*Sin[x], x]", //
        "-1/4*x+1/4*Cos[x]*Sin[x]+1/2*x*Sin[x]^2");
  }

  // Apostol Problems.input:174
  public void test0100() {
    check( //
        "Integrate[(5+2*x)/(-3+2*x+x^2), x]", //
        "7/4*Log[1-x]+1/4*Log[3+x]");
  }

  // Apostol Problems.input:208
  public void test0101() {
    check( //
        "Integrate[1/(x*(1+x^2)^2), x]", //
        "1/2/(1+x^2)+Log[x]-1/2*Log[1+x^2]");
  }

  // Apostol Problems.input:238
  public void test0102() {
    check( //
        "Integrate[Sin[x]/(1+Cos[x]+Sin[x]), x]", //
        "1/2*x-1/2*Log[1+Cos[x]+Sin[x]]-1/2*Log[1+Tan[1/2*x]]");
  }

  // Apostol Problems.input:264
  public void test0103() {
    check( //
        "Integrate[E^(t^2)*t/(1+t^2), t]", //
        "1/2*ExpIntegralEi[1+t^2]/E");
  }

  // Bondarenko Problems.input:29
  public void test0104() {
    check( //
        "Integrate[Sqrt[1+Exp[-x]]/Sinh[x], x]", //
        "-2*ArcTanh[Sqrt[1+1/E^x]/Sqrt[2]]*Sqrt[2]");
  }

  // Bronstein Problems.input:19
  public void test0105() {
    check( //
        "Integrate[(5*x^2+3*(E^x+x)^(1/3)+E^x*(3*x+2*x^2))/(x*(E^x+x)^(1/3)), x]", //
        "3*x*(E^x+x)^(2/3)+3*Log[x]");
  }

  // Hearn Problems.input:16
  public void test0106() {
    check( //
        "Integrate[(b+a*x)/(1+x^2), x]", //
        "b*ArcTan[x]+1/2*a*Log[1+x^2]");
  }

  // Hearn Problems.input:48
  public void test0107() {
    check( //
        "Integrate[1/(-1+2*x^3), x]", //
        "1/3*Log[1-2^(1/3)*x]/2^(1/3)-1/6*Log[1+2^(1/3)*x+2^(2/3)*x^2]/2^(1/3)-ArcTan[(1+2*2^(1/3)*x)/Sqrt[3]]/(2^(1/3)*Sqrt[3])");
  }

  // Hearn Problems.input:90
  public void test0108() {
    check( //
        "Integrate[x^4*Log[a^2+x^2], x]", //
        "-2/5*a^4*x+2/15*a^2*x^3-2/25*x^5+2/5*a^5*ArcTan[x/a]+1/5*x^5*Log[a^2+x^2]");
  }

  // Hearn Problems.input:158
  public void test0109() {
    check( //
        "Integrate[d^x*x^2*Cos[x], x]", //
        "-6*d^x*Cos[x]*Log[d]/(1+Log[d]^2)^3+2*d^x*Cos[x]*Log[d]^3/(1+Log[d]^2)^3+2*d^x*x*Cos[x]/(1+Log[d]^2)^2-2*d^x*x*Cos[x]*Log[d]^2/(1+Log[d]^2)^2+d^x*x^2*Cos[x]*Log[d]/(1+Log[d]^2)-2*d^x*Sin[x]/(1+Log[d]^2)^3+6*d^x*Log[d]^2*Sin[x]/(1+Log[d]^2)^3-4*d^x*x*Log[d]*Sin[x]/(1+Log[d]^2)^2+d^x*x^2*Sin[x]/(1+Log[d]^2)");
  }

  // Hearn Problems.input:189
  public void test0110() {
    check( //
        "Integrate[E^(a*x)*x/(1+a*x)^2, x]", //
        "E^(a*x)/(a^2*(1+a*x))");
  }

  // Hearn Problems.input:270
  public void test0111() {
    check( //
        "Integrate[1/(x*(1+x)), x]", //
        "Log[x]-Log[1+x]");
  }

  // Hearn Problems.input:278
  public void test0112() {
    check( //
        "Integrate[Cos[x]^2*Sin[3+2*x], x]", //
        "-1/4*Cos[3+2*x]-1/16*Cos[3+4*x]+1/4*x*Sin[3]");
  }

  // Hearn Problems.input:286
  public void test0113() {
    check( //
        "Integrate[(1+Cos[x])^(1/2), x]", //
        "2*Sin[x]/Sqrt[1+Cos[x]]");
  }

  // Hearn Problems.input:322
  public void test0114() {
    check( //
        "Integrate[1/(4+x^2)^(1/2), x]", //
        "ArcSinh[1/2*x]");
  }

  // Moses Problems.input:42
  public void test0115() {
    check( //
        "Integrate[E^(2*x)/(A+E^(4*x)*B), x]", //
        "1/2*ArcTan[E^(2*x)*Sqrt[B]/Sqrt[A]]/(Sqrt[A]*Sqrt[B])");
  }

  // Moses Problems.input:124
  public void test0116() {
    check( //
        "Integrate[E^(x^2)*(1+4*x^2+x^3+5*x^4+2*x^6)/(1+x^2)^2, x]", //
        "E^(x^2)*x+1/2*E^(x^2)/(1+x^2)");
  }

  // Moses Problems.input:150
  public void test0117() {
    check( //
        "Integrate[E^(6*x)/(1+E^(4*x)), x]", //
        "1/2*E^(2*x)-1/2*ArcTan[E^(2*x)]");
  }

  // Stewart Problems.input:101
  public void test0118() {
    check( //
        "Integrate[Sec[x]*Tan[x]^5, x]", //
        "Sec[x]-2/3*Sec[x]^3+1/5*Sec[x]^5");
  }

  // Stewart Problems.input:143
  public void test0119() {
    check( //
        "Integrate[1/(x^3*Sqrt[-16+x^2]), x]", //
        "1/128*ArcTan[1/4*Sqrt[-16+x^2]]+1/32*Sqrt[-16+x^2]/x^2");
  }

  // Stewart Problems.input:225
  public void test0120() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]/(5+Cos[x]^2), x]", //
        "-Cos[x]+ArcTan[Cos[x]/Sqrt[5]]*Sqrt[5]");
  }

  // Stewart Problems.input:367
  public void test0121() {
    check( //
        "Integrate[Cos[x]*Sin[x]/Sqrt[1+Sin[x]], x]", //
        "2/3*(1+Sin[x])^(3/2)-2*Sqrt[1+Sin[x]]");
  }

  // Timofeev Problems.input:14
  public void test0122() {
    check( //
        "Integrate[1/Cos[3/4*Pi-2*x], x]", //
        "-1/2*ArcTanh[Sin[1/4*Pi+2*x]]");
  }

  // Timofeev Problems.input:50
  public void test0123() {
    check( //
        "Integrate[Tan[x]*Tan[-a+x], x]", //
        "-x-Cot[a]*Log[Cos[x]]+Cot[a]*Log[Cos[-a+x]]");
  }

  // Timofeev Problems.input:60
  public void test0124() {
    check( //
        "Integrate[(-Cot[x]+Tan[x])^2, x]", //
        "-4*x-Cot[x]+Tan[x]");
  }

  // Timofeev Problems.input:99
  public void test0125() {
    check( //
        "Integrate[1/Sqrt[-1+a^(2*x)], x]", //
        "ArcTan[Sqrt[-1+a^(2*x)]]/Log[a]");
  }

  // Timofeev Problems.input:176
  public void test0126() {
    check( //
        "Integrate[x/((1+x^2)*(2+x^2)*(3+x^2)*(4+x^2)), x]", //
        "1/12*Log[1+x^2]-1/4*Log[2+x^2]+1/4*Log[3+x^2]-1/12*Log[4+x^2]");
  }

  // Timofeev Problems.input:394
  public void test0127() {
    check( //
        "Integrate[1/(1+8*x+3*x^2)^(5/2), x]", //
        "1/39*(-4-3*x)/(1+8*x+3*x^2)^(3/2)+2/169*(4+3*x)/Sqrt[1+8*x+3*x^2]");
  }

  // Timofeev Problems.input:406
  public void test0128() {
    check( //
        "Integrate[1/((1+(-3)/x)^(4/3)*x^2), x]", //
        "(-1)/(1+(-3)/x)^(1/3)");
  }

  // Timofeev Problems.input:500
  public void test0129() {
    check( //
        "Integrate[(1/2-3*Cot[x])*(3-2*Cot[x])^3, x]", //
        "-285/2*x+5*(3-2*Cot[x])^2+(3-2*Cot[x])^3-42*Cot[x]+4*Log[Sin[x]]");
  }

  // Timofeev Problems.input:634
  public void test0130() {
    check( //
        "Integrate[(-4*x^3+3*x^5)/(-1+x^2)^5, x]", //
        "1/8/(1-x^2)^4+1/3/(1-x^2)^3+(-3/4)/(1-x^2)^2");
  }

  // Timofeev Problems.input:730
  public void test0131() {
    check( //
        "Integrate[x/(1/E^x+E^x)^2, x]", //
        "1/2*x-1/2*x/(1+E^(2*x))-1/4*Log[1+E^(2*x)]");
  }

  // Timofeev Problems.input:740
  public void test0132() {
    check( //
        "Integrate[E^(2*x)*Cos[x]^2*Sin[x]^2, x]", //
        "1/16*E^(2*x)-1/80*E^(2*x)*Cos[4*x]-1/40*E^(2*x)*Sin[4*x]");
  }

  // Timofeev Problems.input:800
  public void test0133() {
    check( //
        "Integrate[Cosh[3/2*x]*Sinh[x]*Sinh[5/2*x], x]", //
        "-1/4*x+1/8*Sinh[2*x]-1/12*Sinh[3*x]+1/20*Sinh[5*x]");
  }

  // Timofeev Problems.input:878
  public void test0134() {
    check( //
        "Integrate[x^3*ArcCsc[x]^2, x]", //
        "1/12*x^2+1/4*x^4*ArcCsc[x]^2+1/3*Log[x]+1/3*x*ArcCsc[x]*Sqrt[1+(-1)/x^2]+1/6*x^3*ArcCsc[x]*Sqrt[1+(-1)/x^2]");
  }

  // Timofeev Problems.input:888
  public void test0135() {
    check( //
        "Integrate[(1-x^2)^(3/2)*ArcCos[x]/x, x]", //
        "4/3*x-1/9*x^3+1/3*(1-x^2)^(3/2)*ArcCos[x]+2*I*ArcCos[x]*ArcTan[E^(I*ArcCos[x])]-I*PolyLog[2,-I*E^(I*ArcCos[x])]+I*PolyLog[2,I*E^(I*ArcCos[x])]+ArcCos[x]*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:910
  public void test0136() {
    check( //
        "Integrate[x^2*ArcTan[x]/(1+x^2)^2, x]", //
        "(-1/4)/(1+x^2)-1/2*x*ArcTan[x]/(1+x^2)+1/4*ArcTan[x]^2");
  }

  // Timofeev Problems.input:945
  public void test0137() {
    check( //
        "Integrate[x*ArcTan[Sqrt[1+x^2]]/Sqrt[1+x^2], x]", //
        "-1/2*Log[2+x^2]+ArcTan[Sqrt[1+x^2]]*Sqrt[1+x^2]");
  }

  // Welz Problems.input:61
  public void test0138() {
    check( //
        "Integrate[1/(E^(p*x)*a+b/E^(p*x))^2, x]", //
        "(-1/2)/(a*(E^(2*p*x)*a+b)*p)");
  }

  // Welz Problems.input:114
  public void test0139() {
    check( //
        "Integrate[1/(x*(4-6*x+3*x^2)^(1/3)), x]", //
        "-1/2*Log[x]/2^(2/3)+1/2*Log[6-3*x-3*2^(1/3)*(4-6*x+3*x^2)^(1/3)]/2^(2/3)-ArcTan[1/Sqrt[3]+2^(2/3)*(2-x)/((4-6*x+3*x^2)^(1/3)*Sqrt[3])]/(2^(2/3)*Sqrt[3])");
  }

  // Apostol Problems.input:23
  public void test0140() {
    check( //
        "Integrate[Sin[Sqrt[1+x]]/Sqrt[1+x], x]", //
        "-2*Cos[Sqrt[1+x]]");
  }

  // Apostol Problems.input:107
  public void test0141() {
    check( //
        "Integrate[E^(x^3)*x^2, x]", //
        "1/3*E^(x^3)");
  }

  // Apostol Problems.input:175
  public void test0142() {
    check( //
        "Integrate[(3*x+x^3)/(-3-2*x+x^2), x]", //
        "2*x+1/2*x^2+9*Log[3-x]+Log[1+x]");
  }

  // Apostol Problems.input:219
  public void test0143() {
    check( //
        "Integrate[(1+x)/(-1+x^3), x]", //
        "2/3*Log[1-x]-1/3*Log[1+x+x^2]");
  }

  // Apostol Problems.input:229
  public void test0144() {
    check( //
        "Integrate[1/(5-Cos[x]+2*Sin[x]), x]", //
        "1/2*x/Sqrt[5]+ArcTan[(2*Cos[x]+Sin[x])/(5-Cos[x]+2*Sin[x]+2*Sqrt[5])]/Sqrt[5]");
  }

  // Charlwood Problems.input:104
  public void test0145() {
    check( //
        "Integrate[x*Log[x]/Sqrt[-1+x^2], x]", //
        "ArcTan[Sqrt[-1+x^2]]-Sqrt[-1+x^2]+Log[x]*Sqrt[-1+x^2]");
  }

  // Hearn Problems.input:9
  public void test0146() {
    check( //
        "Integrate[x^2*(x+2*x^2)^2, x]", //
        "1/5*x^5+2/3*x^6+4/7*x^7");
  }

  // Hearn Problems.input:49
  public void test0147() {
    check( //
        "Integrate[1/(-2+x^3), x]", //
        "1/3*Log[2^(1/3)-x]/2^(2/3)-1/6*Log[2^(2/3)+2^(1/3)*x+x^2]/2^(2/3)-ArcTan[(1+2^(2/3)*x)/Sqrt[3]]/(2^(2/3)*Sqrt[3])");
  }

  // Hearn Problems.input:57
  public void test0148() {
    check( //
        "Integrate[1/(1-4*x^2+x^4), x]", //
        "1/2*ArcTanh[x/Sqrt[2-Sqrt[3]]]/Sqrt[3*(2-Sqrt[3])]-1/2*ArcTanh[x/Sqrt[2+Sqrt[3]]]/Sqrt[3*(2+Sqrt[3])]");
  }

  // Hearn Problems.input:91
  public void test0149() {
    check( //
        "Integrate[Log[-a^2+x^2], x]", //
        "-2*x+2*a*ArcTanh[x/a]+x*Log[-a^2+x^2]");
  }

  // Hearn Problems.input:159
  public void test0150() {
    check( //
        "Integrate[d^x*x^3*Sin[x], x]", //
        "-24*d^x*Cos[x]*Log[d]/(1+Log[d]^2)^4+24*d^x*Cos[x]*Log[d]^3/(1+Log[d]^2)^4+6*d^x*x*Cos[x]/(1+Log[d]^2)^3-18*d^x*x*Cos[x]*Log[d]^2/(1+Log[d]^2)^3+6*d^x*x^2*Cos[x]*Log[d]/(1+Log[d]^2)^2-d^x*x^3*Cos[x]/(1+Log[d]^2)-6*d^x*Sin[x]/(1+Log[d]^2)^4+36*d^x*Log[d]^2*Sin[x]/(1+Log[d]^2)^4-6*d^x*Log[d]^4*Sin[x]/(1+Log[d]^2)^4-18*d^x*x*Log[d]*Sin[x]/(1+Log[d]^2)^3+6*d^x*x*Log[d]^3*Sin[x]/(1+Log[d]^2)^3+3*d^x*x^2*Sin[x]/(1+Log[d]^2)^2-3*d^x*x^2*Log[d]^2*Sin[x]/(1+Log[d]^2)^2+d^x*x^3*Log[d]*Sin[x]/(1+Log[d]^2)");
  }

  // Hearn Problems.input:182
  public void test0151() {
    check( //
        "Integrate[1/(E^(m*x)*a+b/E^(m*x)), x]", //
        "ArcTan[E^(m*x)*Sqrt[a]/Sqrt[b]]/(m*Sqrt[a]*Sqrt[b])");
  }

  // Hearn Problems.input:190
  public void test0152() {
    check( //
        "Integrate[k^(x^2)*x, x]", //
        "1/2*k^(x^2)/Log[k]");
  }

  // Hearn Problems.input:203
  public void test0153() {
    check( //
        "Integrate[2*x+x^2*Sqrt[2], x]", //
        "x^2+1/3*x^3*Sqrt[2]");
  }

  // Hearn Problems.input:247
  public void test0154() {
    check( //
        "Integrate[1/(r*Sqrt[-alpha^2-epsilon^2+2*h*r^2]), r]", //
        "ArcTan[Sqrt[-alpha^2-epsilon^2+2*h*r^2]/Sqrt[alpha^2+epsilon^2]]/Sqrt[alpha^2+epsilon^2]");
  }

  // Hearn Problems.input:255
  public void test0155() {
    check( //
        "Integrate[1/(r*Sqrt[-alpha^2-epsilon^2+2*h*r^2-2*k*r^4]), r]", //
        "-1/2*ArcTan[(alpha^2+epsilon^2-h*r^2)/(Sqrt[alpha^2+epsilon^2]*Sqrt[-alpha^2-epsilon^2+2*h*r^2-2*k*r^4])]/Sqrt[alpha^2+epsilon^2]");
  }

  // Hearn Problems.input:271
  public void test0156() {
    check( //
        "Integrate[1/(x^(1/2)*(-1+2*x)), x]", //
        "-ArcTanh[Sqrt[2]*Sqrt[x]]*Sqrt[2]");
  }

  // Hearn Problems.input:287
  public void test0157() {
    check( //
        "Integrate[(1-Cos[x])^(1/2), x]", //
        "-2*Sin[x]/Sqrt[1-Cos[x]]");
  }

  // Hearn Problems.input:305
  public void test0158() {
    check( //
        "Integrate[1/(-3+x)^4, x]", //
        "1/3/(3-x)^3");
  }

  // Hebisch Problems.input:15
  public void test0159() {
    check( //
        "Integrate[(1+Exp[x])*Exp[x+Exp[x]]/(x+Exp[x]), x]", //
        "ExpIntegralEi[E^x+x]");
  }

  // Moses Problems.input:72
  public void test0160() {
    check( //
        "Integrate[E^x*x/(1+x)^2, x]", //
        "E^x/(1+x)");
  }

  // Moses Problems.input:85
  public void test0161() {
    check( //
        "Integrate[1/(1+2*x+x^2), x]", //
        "(-1)/(1+x)");
  }

  // Stewart Problems.input:44
  public void test0162() {
    check( //
        "Integrate[Log[t]*Sqrt[t], t]", //
        "-4/9*t^(3/2)+2/3*t^(3/2)*Log[t]");
  }

  // Stewart Problems.input:52
  public void test0163() {
    check( //
        "Integrate[E^(x^2)*x^3, x]", //
        "-1/2*E^(x^2)+1/2*E^(x^2)*x^2");
  }

  // Stewart Problems.input:102
  public void test0164() {
    check( //
        "Integrate[Sec[x]^3*Tan[x]^5, x]", //
        "1/3*Sec[x]^3-2/5*Sec[x]^5+1/7*Sec[x]^7");
  }

  // Stewart Problems.input:226
  public void test0165() {
    check( //
        "Integrate[1/(-3+2*x+x^2), x]", //
        "1/4*Log[1-x]-1/4*Log[3+x]");
  }

  // Stewart Problems.input:270
  public void test0166() {
    check( //
        "Integrate[Sqrt[x]*(1+Sqrt[x]), x]", //
        "2/3*x^(3/2)+1/2*x^2");
  }

  // Timofeev Problems.input:61
  public void test0167() {
    check( //
        "Integrate[(-Sec[x]+Tan[x])^2, x]", //
        "-x-2*Cos[x]/(1+Sin[x])");
  }

  // Timofeev Problems.input:75
  public void test0168() {
    check( //
        "Integrate[1/((b+a*x)*Sqrt[x]), x]", //
        "2*ArcTan[Sqrt[a]*Sqrt[x]/Sqrt[b]]/(Sqrt[a]*Sqrt[b])");
  }

  // Timofeev Problems.input:165
  public void test0169() {
    check( //
        "Integrate[(6*x+4*x^2+x^3)/(2+4*x+3*x^2+2*x^3+x^4), x]", //
        "1/(1+x)-1/3*Log[1+x]+2/3*Log[2+x^2]+4/3*ArcTan[x/Sqrt[2]]*Sqrt[2]");
  }

  // Timofeev Problems.input:326
  public void test0170() {
    check( //
        "Integrate[1/(-3-2*x+x^2)^(5/2), x]", //
        "1/12*(1-x)/(-3-2*x+x^2)^(3/2)+1/24*(-1+x)/Sqrt[-3-2*x+x^2]");
  }

  // Timofeev Problems.input:346
  public void test0171() {
    check( //
        "Integrate[x/((4+x+x^2)*Sqrt[5+4*x+4*x^2]), x]", //
        "ArcTan[Sqrt[5+4*x+4*x^2]/Sqrt[11]]/Sqrt[11]-ArcTanh[(1+2*x)*Sqrt[11/15]/Sqrt[5+4*x+4*x^2]]/Sqrt[165]");
  }

  // Timofeev Problems.input:357
  public void test0172() {
    check( //
        "Integrate[(1-x+x^2)/((1+x^2)*Sqrt[1+x^2]), x]", //
        "ArcSinh[x]+1/Sqrt[1+x^2]");
  }

  // Timofeev Problems.input:467
  public void test0173() {
    check( //
        "Integrate[1/Cos[1/4*Pi+3*x]^3, x]", //
        "1/6*ArcTanh[Sin[1/4*Pi+3*x]]+1/6*Sec[1/4*Pi+3*x]*Tan[1/4*Pi+3*x]");
  }

  // Timofeev Problems.input:731
  public void test0174() {
    check( //
        "Integrate[E^x*(1-x-x^2)/Sqrt[1-x^2], x]", //
        "E^x*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:741
  public void test0175() {
    check( //
        "Integrate[E^(3*x)*Cos[3/2*x]^2*Sin[3/2*x]^2, x]", //
        "1/24*E^(3*x)-1/120*E^(3*x)*Cos[6*x]-1/60*E^(3*x)*Sin[6*x]");
  }

  // Timofeev Problems.input:857
  public void test0176() {
    check( //
        "Integrate[(1/E^Log[Cos[x]]+E^Log[Cos[x]])*Tan[x], x]", //
        "-Cos[x]+Sec[x]");
  }

  // Timofeev Problems.input:911
  public void test0177() {
    check( //
        "Integrate[x^3*ArcTan[x]/(1+x^2)^2, x]", //
        "-1/4*x/(1+x^2)-1/4*ArcTan[x]+1/2*ArcTan[x]/(1+x^2)-1/2*I*ArcTan[x]^2-ArcTan[x]*Log[2/(1+I*x)]-1/2*I*PolyLog[2,1+(-2)/(1+I*x)]");
  }

  // Welz Problems.input:36
  public void test0178() {
    check( //
        "Integrate[1/((1+x^2)^2*Sqrt[-1+x^2]), x]", //
        "3/4*ArcTanh[x*Sqrt[2]/Sqrt[-1+x^2]]/Sqrt[2]-1/4*x*Sqrt[-1+x^2]/(1+x^2)");
  }

  // Welz Problems.input:82
  public void test0179() {
    check( //
        "Integrate[(x+Sqrt[b+x^2])^a, x]", //
        "-1/2*b*(x+Sqrt[b+x^2])^(-1+a)/(1-a)+1/2*(x+Sqrt[b+x^2])^(1+a)/(1+a)");
  }

  // Welz Problems.input:152
  public void test0180() {
    check( //
        "Integrate[x/((8+x^3)*Sqrt[-1+x^3]), x]", //
        "1/18*ArcTan[1/3*(1-x)^2/Sqrt[-1+x^3]]+1/18*ArcTan[1/3*Sqrt[-1+x^3]]-1/6*ArcTanh[(1-x)*Sqrt[3]/Sqrt[-1+x^3]]/Sqrt[3]");
  }

  // Apostol Problems.input:82
  public void test0181() {
    check( //
        "Integrate[Sin[(-1+x)^(1/4)], x]", //
        "24*(-1+x)^(1/4)*Cos[(-1+x)^(1/4)]-4*(-1+x)^(3/4)*Cos[(-1+x)^(1/4)]-24*Sin[(-1+x)^(1/4)]+12*Sin[(-1+x)^(1/4)]*Sqrt[-1+x]");
  }

  // Apostol Problems.input:178
  public void test0182() {
    check( //
        "Integrate[(-1+5*x+2*x^2)/(-2*x+x^2+x^3), x]", //
        "2*Log[1-x]+1/2*Log[x]-1/2*Log[2+x]");
  }

  // Bondarenko Problems.input:31
  public void test0183() {
    check( //
        "Integrate[1/(1+Cos[x]+Sin[x])^2, x]", //
        "-Log[1+Tan[1/2*x]]+(-Cos[x]+Sin[x])/(1+Cos[x]+Sin[x])");
  }

  // Bronstein Problems.input:21
  public void test0184() {
    check( //
        "Integrate[(x^2+2*x*Log[x]+Log[x]^2+(1+x)*Sqrt[x+Log[x]])/(x^3+2*x^2*Log[x]+x*Log[x]^2), x]", //
        "Log[x]+(-2)/Sqrt[x+Log[x]]");
  }

  // Charlwood Problems.input:56
  public void test0185() {
    check( //
        "Integrate[Log[x+Sqrt[-1+x^2]]/(1+x^2)^(3/2), x]", //
        "-1/2*ArcCosh[x^2]+x*Log[x+Sqrt[-1+x^2]]/Sqrt[1+x^2]");
  }

  // Hearn Problems.input:50
  public void test0186() {
    check( //
        "Integrate[1/(-b+a*x^3), x]", //
        "1/3*Log[b^(1/3)-a^(1/3)*x]/(a^(1/3)*b^(2/3))-1/6*Log[b^(2/3)+a^(1/3)*b^(1/3)*x+a^(2/3)*x^2]/(a^(1/3)*b^(2/3))-ArcTan[(b^(1/3)+2*a^(1/3)*x)/(b^(1/3)*Sqrt[3])]/(a^(1/3)*b^(2/3)*Sqrt[3])");
  }

  // Hearn Problems.input:58
  public void test0187() {
    check( //
        "Integrate[1/(1+4*x^2+x^4), x]", //
        "1/2*ArcTan[x/Sqrt[2-Sqrt[3]]]/Sqrt[3*(2-Sqrt[3])]-1/2*ArcTan[x/Sqrt[2+Sqrt[3]]]/Sqrt[3*(2+Sqrt[3])]");
  }

  // Hearn Problems.input:160
  public void test0188() {
    check( //
        "Integrate[d^x*x^3*Cos[x], x]", //
        "-6*d^x*Cos[x]/(1+Log[d]^2)^4+36*d^x*Cos[x]*Log[d]^2/(1+Log[d]^2)^4-6*d^x*Cos[x]*Log[d]^4/(1+Log[d]^2)^4-18*d^x*x*Cos[x]*Log[d]/(1+Log[d]^2)^3+6*d^x*x*Cos[x]*Log[d]^3/(1+Log[d]^2)^3+3*d^x*x^2*Cos[x]/(1+Log[d]^2)^2-3*d^x*x^2*Cos[x]*Log[d]^2/(1+Log[d]^2)^2+d^x*x^3*Cos[x]*Log[d]/(1+Log[d]^2)+24*d^x*Log[d]*Sin[x]/(1+Log[d]^2)^4-24*d^x*Log[d]^3*Sin[x]/(1+Log[d]^2)^4-6*d^x*x*Sin[x]/(1+Log[d]^2)^3+18*d^x*x*Log[d]^2*Sin[x]/(1+Log[d]^2)^3-6*d^x*x^2*Log[d]*Sin[x]/(1+Log[d]^2)^2+d^x*x^3*Sin[x]/(1+Log[d]^2)");
  }

  // Hearn Problems.input:191
  public void test0189() {
    check( //
        "Integrate[E^(x^2), x]", //
        "1/2*Erfi[x]*Sqrt[Pi]");
  }

  // Hearn Problems.input:264
  public void test0190() {
    check( //
        "Integrate[a*Cos[5+3*x]*Sin[5+3*x]^2, x]", //
        "1/9*a*Sin[5+3*x]^3");
  }

  // Hearn Problems.input:272
  public void test0191() {
    check( //
        "Integrate[x^(1/2)*(1+x^2), x]", //
        "2/3*x^(3/2)+2/7*x^(7/2)");
  }

  // Hearn Problems.input:288
  public void test0192() {
    check( //
        "Integrate[1/(-(-1+x)^(1/2)+x^(1/2)), x]", //
        "2/3*(-1+x)^(3/2)+2/3*x^(3/2)");
  }

  // Hearn Problems.input:349
  public void test0193() {
    check( //
        "Integrate[x*(-Sqrt[-4+x^2]+x^2*Sqrt[-4+x^2]-4*Sqrt[-1+x^2]+x^2*Sqrt[-1+x^2])/((4-5*x^2+x^4)*(1+Sqrt[-4+x^2]+Sqrt[-1+x^2])), x]", //
        "Log[1+Sqrt[-4+x^2]+Sqrt[-1+x^2]]");
  }

  // Moses Problems.input:24
  public void test0194() {
    check( //
        "Integrate[E^(x^2)+2*E^(x^2)*x^2, x]", //
        "E^(x^2)*x");
  }

  // Moses Problems.input:73
  public void test0195() {
    check( //
        "Integrate[E^(x^2)*(1+2*x^2), x]", //
        "E^(x^2)*x");
  }

  // Moses Problems.input:88
  public void test0196() {
    check( //
        "Integrate[Log[x]/(1+Log[x])^2, x]", //
        "x/(1+Log[x])");
  }

  // Moses Problems.input:126
  public void test0197() {
    check( //
        "Integrate[(1/x+x)*Log[x], x]", //
        "-1/4*x^2+1/2*x^2*Log[x]+1/2*Log[x]^2");
  }

  // Stewart Problems.input:61
  public void test0198() {
    check( //
        "Integrate[E^(x^2)*x^5, x]", //
        "E^(x^2)-E^(x^2)*x^2+1/2*E^(x^2)*x^4");
  }

  // Stewart Problems.input:71
  public void test0199() {
    check( //
        "Integrate[Cos[x]^4*Sin[x]^3, x]", //
        "-1/5*Cos[x]^5+1/7*Cos[x]^7");
  }

  // Stewart Problems.input:145
  public void test0200() {
    check( //
        "Integrate[Sqrt[-4+9*x^2]/x, x]", //
        "-2*ArcTan[1/2*Sqrt[-4+9*x^2]]+Sqrt[-4+9*x^2]");
  }

  // Stewart Problems.input:227
  public void test0201() {
    check( //
        "Integrate[1/(-2*x+x^2), x]", //
        "1/2*Log[2-x]-1/2*Log[x]");
  }

  // Stewart Problems.input:303
  public void test0202() {
    check( //
        "Integrate[x^5/E^(x^3), x]", //
        "(-1/3)/E^(x^3)-1/3*x^3/E^(x^3)");
  }

  // Timofeev Problems.input:211
  public void test0203() {
    check( //
        "Integrate[(1+x^4)/(1+x^6), x]", //
        "2/3*ArcTan[x]-1/3*ArcTan[-2*x+Sqrt[3]]+1/3*ArcTan[2*x+Sqrt[3]]");
  }

  // Timofeev Problems.input:232
  public void test0204() {
    check( //
        "Integrate[1/(-3-2*x+x^2)^3, x]", //
        "1/16*(1-x)/(3+2*x-x^2)^2+3/128*(1-x)/(3+2*x-x^2)+3/512*Log[3-x]-3/512*Log[1+x]");
  }

  // Timofeev Problems.input:297
  public void test0205() {
    check( //
        "Integrate[x^(3/2)*(1+x^2)*(-x+2*Sqrt[x])^2, x]", //
        "8/7*x^(7/2)-x^4+2/9*x^(9/2)+8/11*x^(11/2)-2/3*x^6+2/13*x^(13/2)");
  }

  // Timofeev Problems.input:337
  public void test0206() {
    check( //
        "Integrate[1/((4+x^2)*Sqrt[1-x^2]), x]", //
        "1/2*ArcTan[1/2*x*Sqrt[5]/Sqrt[1-x^2]]/Sqrt[5]");
  }

  // Timofeev Problems.input:398
  public void test0207() {
    check( //
        "Integrate[1/(1+Sqrt[2+2*x+x^2]), x]", //
        "1/(1+x)+ArcSinh[1+x]-Sqrt[2+2*x+x^2]/(1+x)");
  }

  // Timofeev Problems.input:520
  public void test0208() {
    check( //
        "Integrate[1/(4+4*Cot[x]+Tan[x]), x]", //
        "4/25*x-3/25*Log[2*Cos[x]+Sin[x]]+2/5/(2+Tan[x])");
  }

  // Timofeev Problems.input:540
  public void test0209() {
    check( //
        "Integrate[(1-Sin[2/3*x])^(5/2), x]", //
        "3/5*Cos[2/3*x]*(1-Sin[2/3*x])^(3/2)+32/5*Cos[2/3*x]/Sqrt[1-Sin[2/3*x]]+8/5*Cos[2/3*x]*Sqrt[1-Sin[2/3*x]]");
  }

  // Timofeev Problems.input:595
  public void test0210() {
    check( //
        "Integrate[(3+Sin[x]^2)*Tan[x]^3/((-2+Cos[x]^2)*(5-4*Sec[x]^2)^(3/2)), x]", //
        "-1/6*ArcTanh[Sqrt[5-4*Sec[x]^2]/Sqrt[3]]/Sqrt[3]-1/5*ArcTanh[Sqrt[5-4*Sec[x]^2]/Sqrt[5]]/Sqrt[5]+(-2/15)/Sqrt[5-4*Sec[x]^2]");
  }

  // Timofeev Problems.input:685
  public void test0211() {
    check( //
        "Integrate[a^(k*x)+a^(l*x), x]", //
        "a^(k*x)/(k*Log[a])+a^(l*x)/(l*Log[a])");
  }

  // Timofeev Problems.input:707
  public void test0212() {
    check( //
        "Integrate[1/(E^(n*x)*a+b), x]", //
        "x/b-Log[E^(n*x)*a+b]/(b*n)");
  }

  // Timofeev Problems.input:848
  public void test0213() {
    check( //
        "Integrate[1/(x*Log[x]*Sqrt[-a^2+Log[x]^2]), x]", //
        "ArcTan[Sqrt[-a^2+Log[x]^2]/a]/a");
  }

  // Timofeev Problems.input:890
  public void test0214() {
    check( //
        "Integrate[x^2*ArcSin[x]/Sqrt[1-x^2], x]", //
        "1/4*x^2+1/4*ArcSin[x]^2-1/2*x*ArcSin[x]*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:900
  public void test0215() {
    check( //
        "Integrate[x*ArcCos[x]^2*Sqrt[1-x^2], x]", //
        "2/27*(1-x^2)^(3/2)-2/3*x*ArcCos[x]+2/9*x^3*ArcCos[x]-1/3*(1-x^2)^(3/2)*ArcCos[x]^2+4/9*Sqrt[1-x^2]");
  }

  // Welz Problems.input:39
  public void test0216() {
    check( //
        "Integrate[1/(Sqrt[-1+x]*(Sqrt[-1+x]+Sqrt[x])^2), x]", //
        "4/3*(-1+x)^(3/2)-4/3*x^(3/2)+2*Sqrt[-1+x]");
  }

  // Welz Problems.input:83
  public void test0217() {
    check( //
        "Integrate[(6+3*x^a+2*x^(2*a))^(1/a)*(x^a+x^(2*a)+x^(3*a)), x]", //
        "1/6*x^(1+a)*(6+3*x^a+2*x^(2*a))^(1+1/a)/(1+a)");
  }

  // Wester Problems.input:12
  public void test0218() {
    check( //
        "Integrate[1/((-5)/E^(m*x)+2*E^(m*x)), x]", //
        "-ArcTanh[E^(m*x)*Sqrt[2/5]]/(m*Sqrt[10])");
  }

  // Apostol Problems.input:203
  public void test0219() {
    check( //
        "Integrate[(-6+2*x+x^4)/(-2*x+x^2+x^3), x]", //
        "-x+1/2*x^2-Log[1-x]+3*Log[x]+Log[2+x]");
  }

  // Apostol Problems.input:213
  public void test0220() {
    check( //
        "Integrate[1/(-x+x^3), x]", //
        "-Log[x]+1/2*Log[1-x^2]");
  }

  // Bondarenko Problems.input:24
  public void test0221() {
    check( //
        "Integrate[Sqrt[1+Sqrt[x]+Sqrt[1+2*x+2*Sqrt[x]]], x]", //
        "2/15*(2+6*x^(3/2)+Sqrt[x]-(2-Sqrt[x])*Sqrt[1+2*x+2*Sqrt[x]])*Sqrt[1+Sqrt[x]+Sqrt[1+2*x+2*Sqrt[x]]]/Sqrt[x]");
  }

  // Charlwood Problems.input:110
  public void test0222() {
    check( //
        "Integrate[x*ArcSec[x]/Sqrt[-1+x^2], x]", //
        "-x*Log[x]/Sqrt[x^2]+ArcSec[x]*Sqrt[-1+x^2]");
  }

  // Hearn Problems.input:51
  public void test0223() {
    check( //
        "Integrate[1/(-2+x^4), x]", //
        "-1/2*ArcTan[x/2^(1/4)]/2^(3/4)-1/2*ArcTanh[x/2^(1/4)]/2^(3/4)");
  }

  // Hearn Problems.input:281
  public void test0224() {
    check( //
        "Integrate[x*Log[a+x^2], x]", //
        "-1/2*x^2+1/2*(a+x^2)*Log[a+x^2]");
  }

  // Hearn Problems.input:289
  public void test0225() {
    check( //
        "Integrate[1/(1-(1+x)^(1/2)), x]", //
        "-2*Log[1-Sqrt[1+x]]-2*Sqrt[1+x]");
  }

  // Hebisch Problems.input:21
  public void test0226() {
    check( //
        "Integrate[Exp[1+1/Log[x]]*(-1+Log[x]^2)/Log[x]^2, x]", //
        "E^(1+1/Log[x])*x");
  }

  // Stewart Problems.input:38
  public void test0227() {
    check( //
        "Integrate[E^t*t^3, t]", //
        "-6*E^t+6*E^t*t-3*E^t*t^2+E^t*t^3");
  }

  // Stewart Problems.input:72
  public void test0228() {
    check( //
        "Integrate[Cos[x]^3*Sin[x]^4, x]", //
        "1/5*Sin[x]^5-1/7*Sin[x]^7");
  }

  // Stewart Problems.input:80
  public void test0229() {
    check( //
        "Integrate[Cos[2*x]^4*Sin[2*x]^2, x]", //
        "1/16*x+1/32*Cos[2*x]*Sin[2*x]+1/48*Cos[2*x]^3*Sin[2*x]-1/12*Cos[2*x]^5*Sin[2*x]");
  }

  // Stewart Problems.input:104
  public void test0230() {
    check( //
        "Integrate[Sec[x]^6*Tan[x]^3, x]", //
        "-1/6*Sec[x]^6+1/8*Sec[x]^8");
  }

  // Stewart Problems.input:154
  public void test0231() {
    check( //
        "Integrate[1/(-25+4*x^2)^(3/2), x]", //
        "-1/25*x/Sqrt[-25+4*x^2]");
  }

  // Stewart Problems.input:162
  public void test0232() {
    check( //
        "Integrate[Sqrt[-9+E^(2*t)], t]", //
        "-3*ArcTan[1/3*Sqrt[-9+E^(2*t)]]+Sqrt[-9+E^(2*t)]");
  }

  // Timofeev Problems.input:169
  public void test0233() {
    check( //
        "Integrate[(-2+x+3*x^2)/((-1+x)^3*(1+x^2)), x]", //
        "(-1/2)/(1-x)^2+5/2/(1-x)-ArcTan[x]-3/2*Log[1-x]+3/4*Log[1+x^2]");
  }

  // Timofeev Problems.input:212
  public void test0234() {
    check( //
        "Integrate[1/(5+3*x+x^2)^3, x]", //
        "1/22*(3+2*x)/(5+3*x+x^2)^2+3/121*(3+2*x)/(5+3*x+x^2)+12/121*ArcTan[(3+2*x)/Sqrt[11]]/Sqrt[11]");
  }

  // Timofeev Problems.input:233
  public void test0235() {
    check( //
        "Integrate[1/(13-4*x+x^2)^3, x]", //
        "1/36*(-2+x)/(13-4*x+x^2)^2+1/216*(-2+x)/(13-4*x+x^2)+1/648*ArcTan[1/3*(-2+x)]");
  }

  // Timofeev Problems.input:348
  public void test0236() {
    check( //
        "Integrate[(1+2*x)/((4+4*x+3*x^2)*Sqrt[-1+6*x+x^2]), x]", //
        "-1/3*ArcTanh[(1+x)*Sqrt[7]/Sqrt[-1+6*x+x^2]]/Sqrt[7]-5/6*ArcTan[1/2*(2-x)*Sqrt[7]/(Sqrt[2]*Sqrt[-1+6*x+x^2])]/Sqrt[14]");
  }

  // Timofeev Problems.input:369
  public void test0237() {
    check( //
        "Integrate[1/((-1+x^2)*Sqrt[2*x+x^2]), x]", //
        "-1/2*ArcTan[Sqrt[2*x+x^2]]-1/2*ArcTanh[(1+2*x)/(Sqrt[3]*Sqrt[2*x+x^2])]/Sqrt[3]");
  }

  // Timofeev Problems.input:431
  public void test0238() {
    check( //
        "Integrate[1/((-1+x^3)*(2+x^3)^(1/3)), x]", //
        "-ArcTan[(1+2*3^(1/3)*x/(2+x^3)^(1/3))/Sqrt[3]]/3^(5/6)-1/6*Log[-1+x^3]/3^(1/3)+1/2*Log[3^(1/3)*x-(2+x^3)^(1/3)]/3^(1/3)");
  }

  // Timofeev Problems.input:481
  public void test0239() {
    check( //
        "Integrate[Cos[x]^6*Sin[x]^6, x]", //
        "5/1024*x+5/1024*Cos[x]*Sin[x]+5/1536*Cos[x]^3*Sin[x]+1/384*Cos[x]^5*Sin[x]-1/64*Cos[x]^7*Sin[x]-1/24*Cos[x]^7*Sin[x]^3-1/12*Cos[x]^7*Sin[x]^5");
  }

  // Timofeev Problems.input:492
  public void test0240() {
    check( //
        "Integrate[Sec[x]^(3/2)*Tan[x]^5, x]", //
        "2/3*Sec[x]^(3/2)-4/7*Sec[x]^(7/2)+2/11*Sec[x]^(11/2)");
  }

  // Timofeev Problems.input:521
  public void test0241() {
    check( //
        "Integrate[1/(2*Sec[x]+Sin[x])^2, x]", //
        "8/15*x/Sqrt[15]-8/15*ArcTan[(1-2*Cos[x]^2)/(4+2*Cos[x]*Sin[x]+Sqrt[15])]/Sqrt[15]+1/15*(1+4*Tan[x])/(2+Tan[x]+2*Tan[x]^2)");
  }

  // Timofeev Problems.input:584
  public void test0242() {
    check( //
        "Integrate[Cos[x]*Sqrt[Cos[2*x]], x]", //
        "1/2*ArcSin[Sin[x]*Sqrt[2]]/Sqrt[2]+1/2*Sin[x]*Sqrt[Cos[2*x]]");
  }

  // Timofeev Problems.input:596
  public void test0243() {
    check( //
        "Integrate[(Sec[x]^2-3*Sqrt[4*Sec[x]^2+5*Tan[x]^2]*Tan[x])/(Sin[x]^2*(4*Sec[x]^2+5*Tan[x]^2)^(3/2)), x]", //
        "-3/4*Log[Tan[x]]+3/8*Log[4+9*Tan[x]^2]-1/4*Cot[x]/Sqrt[4+9*Tan[x]^2]-7/8*Tan[x]/Sqrt[4+9*Tan[x]^2]");
  }

  // Timofeev Problems.input:672
  public void test0244() {
    check( //
        "Integrate[x^2/(x*Cos[x]-Sin[x])^2, x]", //
        "-Cot[x]+x*Csc[x]/(x*Cos[x]-Sin[x])");
  }

  // Timofeev Problems.input:709
  public void test0245() {
    check( //
        "Integrate[(-1+E^x)/(1+E^x), x]", //
        "-x+2*Log[1+E^x]");
  }

  // Timofeev Problems.input:723
  public void test0246() {
    check( //
        "Integrate[E^(2*x)/(3-E^(1/2*x))^(3/4), x]", //
        "-216*(3-E^(1/2*x))^(1/4)+216/5*(3-E^(1/2*x))^(5/4)-8*(3-E^(1/2*x))^(9/4)+8/13*(3-E^(1/2*x))^(13/4)");
  }

  // Timofeev Problems.input:735
  public void test0247() {
    check( //
        "Integrate[(Cos[1/2*x]+Sin[1/2*x])/(E^x)^(1/3), x]", //
        "-30/13*Cos[1/2*x]/(E^x)^(1/3)+6/13*Sin[1/2*x]/(E^x)^(1/3)");
  }

  // Timofeev Problems.input:770
  public void test0248() {
    check( //
        "Integrate[E^(1/2*x)*x^2*Cos[x]*Sin[x]^2, x]", //
        "-44/125*E^(1/2*x)*Cos[x]+6/25*E^(1/2*x)*x*Cos[x]+1/10*E^(1/2*x)*x^2*Cos[x]+428/50653*E^(1/2*x)*Cos[3*x]-70/1369*E^(1/2*x)*x*Cos[3*x]-1/74*E^(1/2*x)*x^2*Cos[3*x]-8/125*E^(1/2*x)*Sin[x]-8/25*E^(1/2*x)*x*Sin[x]+1/5*E^(1/2*x)*x^2*Sin[x]+792/50653*E^(1/2*x)*Sin[3*x]+24/1369*E^(1/2*x)*x*Sin[3*x]-3/37*E^(1/2*x)*x^2*Sin[3*x]");
  }

  // Timofeev Problems.input:891
  public void test0249() {
    check( //
        "Integrate[x^4*ArcSin[x]/Sqrt[1-x^2], x]", //
        "3/16*x^2+1/16*x^4+3/16*ArcSin[x]^2-3/8*x*ArcSin[x]*Sqrt[1-x^2]-1/4*x^3*ArcSin[x]*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:901
  public void test0250() {
    check( //
        "Integrate[x^2*ArcSin[x]^3/Sqrt[1-x^2], x]", //
        "-3/8*x^2-3/8*ArcSin[x]^2+3/4*x^2*ArcSin[x]^2+1/8*ArcSin[x]^4+3/4*x*ArcSin[x]*Sqrt[1-x^2]-1/2*x*ArcSin[x]^3*Sqrt[1-x^2]");
  }

  // Timofeev Problems.input:913
  public void test0251() {
    check( //
        "Integrate[(1+x^2)*ArcTan[x]/x^2, x]", //
        "-ArcTan[x]/x+x*ArcTan[x]+Log[x]-Log[1+x^2]");
  }

  // Welz Problems.input:56
  public void test0252() {
    check( //
        "Integrate[(x+Sqrt[a+x^2])^b, x]", //
        "-1/2*a*(x+Sqrt[a+x^2])^(-1+b)/(1-b)+1/2*(x+Sqrt[a+x^2])^(1+b)/(1+b)");
  }

  // Welz Problems.input:184
  public void test0253() {
    check( //
        "Integrate[1/((1+x)*(2+x^3)^(1/3)), x]", //
        "-1/2*Log[1+x]+3/4*Log[2+x-(2+x^3)^(1/3)]-1/4*Log[-x+(2+x^3)^(1/3)]+1/2*ArcTan[(1+2*x/(2+x^3)^(1/3))/Sqrt[3]]/Sqrt[3]-1/2*ArcTan[(1+2*(2+x)/(2+x^3)^(1/3))/Sqrt[3]]*Sqrt[3]");
  }

  // Apostol Problems.input:84
  public void test0254() {
    check( //
        "Integrate[Sin[2*x]*Sqrt[1+3*Cos[x]^2], x]", //
        "-2/9*(4-3*Sin[x]^2)^(3/2)");
  }

  // Apostol Problems.input:130
  public void test0255() {
    check( //
        "Integrate[x^3/E^(x^2), x]", //
        "(-1/2)/E^(x^2)-1/2*x^2/E^(x^2)");
  }

  // Apostol Problems.input:164
  public void test0256() {
    check( //
        "Integrate[((a+x)/(a-x))^(1/2), x]", //
        "2*a*ArcTan[Sqrt[(a+x)/(a-x)]]-(a-x)*Sqrt[(a+x)/(a-x)]");
  }

  // Apostol Problems.input:214
  public void test0257() {
    check( //
        "Integrate[x^2/(-6+x+x^2), x]", //
        "x+4/5*Log[2-x]-9/5*Log[3+x]");
  }

  // Apostol Problems.input:224
  public void test0258() {
    check( //
        "Integrate[(1-x^3)/(x*(1+x^2)), x]", //
        "-x+ArcTan[x]+Log[x]-1/2*Log[1+x^2]");
  }

  // Apostol Problems.input:232
  public void test0259() {
    check( //
        "Integrate[1/(1+1/2*Cos[x]), x]", //
        "2*x/Sqrt[3]-4*ArcTan[Sin[x]/(2+Cos[x]+Sqrt[3])]/Sqrt[3]");
  }

  // Bronstein Problems.input:13
  public void test0260() {
    check( //
        "Integrate[x/Sqrt[-71-96*x+10*x^2+x^4], x]", //
        "1/8*Log[10001+3124*x^2-1408*x^3+54*x^4-128*x^5+20*x^6+x^8+(781-528*x+27*x^2-80*x^3+15*x^4+x^6)*Sqrt[-71-96*x+10*x^2+x^4]]");
  }

  // Charlwood Problems.input:32
  public void test0261() {
    check( //
        "Integrate[ArcTan[Sqrt[-1+Sec[x]]]*Sin[x], x]", //
        "1/2*ArcTan[Sqrt[-1+Sec[x]]]-ArcTan[Sqrt[-1+Sec[x]]]*Cos[x]+1/2*Cos[x]*Sqrt[-1+Sec[x]]");
  }

  // Hearn Problems.input:22
  public void test0262() {
    check( //
        "Integrate[x/((a^2+x^2)*(b^2+x^2)), x]", //
        "-1/2*Log[a^2+x^2]/(a^2-b^2)+1/2*Log[b^2+x^2]/(a^2-b^2)");
  }

  // Hearn Problems.input:52
  public void test0263() {
    check( //
        "Integrate[1/(-1+5*x^4), x]", //
        "-1/2*ArcTan[5^(1/4)*x]/5^(1/4)-1/2*ArcTanh[5^(1/4)*x]/5^(1/4)");
  }

  // Hearn Problems.input:78
  public void test0264() {
    check( //
        "Integrate[1/Log[1+x], x]", //
        "LogIntegral[1+x]");
  }

  // Hearn Problems.input:106
  public void test0265() {
    check( //
        "Integrate[Cos[x]*(1+Sin[x]^2)^2, x]", //
        "Sin[x]+2/3*Sin[x]^3+1/5*Sin[x]^5");
  }

  // Hearn Problems.input:193
  public void test0266() {
    check( //
        "Integrate[E^(1/x)*(1+x)/x^4, x]", //
        "-E^(1/x)-E^(1/x)/x^2+E^(1/x)/x");
  }

  // Hearn Problems.input:222
  public void test0267() {
    check( //
        "Integrate[x/Sqrt[1-x^2]^(9/4), x]", //
        "4/(1-x^2)^(1/8)");
  }

  // Hearn Problems.input:282
  public void test0268() {
    check( //
        "Integrate[Cos[x]*Sin[a+x], x]", //
        "-1/4*Cos[a+2*x]+1/2*x*Sin[a]");
  }

  // Hearn Problems.input:290
  public void test0269() {
    check( //
        "Integrate[x/(36+x^4)^(1/2), x]", //
        "1/2*ArcSinh[1/6*x^2]");
  }

  // Hearn Problems.input:298
  public void test0270() {
    check( //
        "Integrate[(2+1/x^4+x^4)^(1/2), x]", //
        "-x*Sqrt[2+1/x^4+x^4]/(1+x^4)+1/3*x^5*Sqrt[2+1/x^4+x^4]/(1+x^4)");
  }

  // Hearn Problems.input:331
  public void test0271() {
    check( //
        "Integrate[E^(x^2)/x+2*E^(x^2)*x*Log[x]+(-2+Log[x])/(x+Log[x]^2)^2+(1+1/x+2*Log[x]/x)/(x+Log[x]^2), x]", //
        "E^(x^2)*Log[x]-Log[x]/(x+Log[x]^2)+Log[x+Log[x]^2]");
  }

  // Moses Problems.input:26
  public void test0272() {
    check( //
        "Integrate[2*E^x+E^(2*x)+x^2, x]", //
        "2*E^x+1/2*E^(2*x)+1/3*x^3");
  }

  // Moses Problems.input:34
  public void test0273() {
    check( //
        "Integrate[E^x*Cos[E^x]^2*Sin[E^x], x]", //
        "-1/3*Cos[E^x]^3");
  }

  // Stewart Problems.input:73
  public void test0274() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]^4, x]", //
        "1/16*x+1/16*Cos[x]*Sin[x]-1/8*Cos[x]^3*Sin[x]-1/6*Cos[x]^3*Sin[x]^3");
  }

  // Stewart Problems.input:181
  public void test0275() {
    check( //
        "Integrate[(-2+x^2)/(x*(2+x^2)), x]", //
        "-Log[x]+Log[2+x^2]");
  }

  // Stewart Problems.input:363
  public void test0276() {
    check( //
        "Integrate[Csc[1/2*x]^3, x]", //
        "-ArcTanh[Cos[1/2*x]]-Cot[1/2*x]*Csc[1/2*x]");
  }

  // Timofeev Problems.input:66
  public void test0277() {
    check( //
        "Integrate[(-1+E^(1/2*x))^3/E^(1/2*x), x]", //
        "2/E^(1/2*x)-6*E^(1/2*x)+E^x+3*x");
  }

  // Timofeev Problems.input:78
  public void test0278() {
    check( //
        "Integrate[1/(x*Sqrt[-a^2+x^2]), x]", //
        "ArcTan[Sqrt[-a^2+x^2]/a]/a");
  }

  // Timofeev Problems.input:145
  public void test0279() {
    check( //
        "Integrate[(-1+x+x^2)/(-6*x+x^2+x^3), x]", //
        "1/2*Log[2-x]+1/6*Log[x]+1/3*Log[3+x]");
  }

  // Timofeev Problems.input:283
  public void test0280() {
    check( //
        "Integrate[1/(2+3*x+x^2)^5, x]", //
        "1/4*(-3-2*x)/(2+3*x+x^2)^4+7/6*(3+2*x)/(2+3*x+x^2)^3-35/6*(3+2*x)/(2+3*x+x^2)^2+35*(3+2*x)/(2+3*x+x^2)+70*Log[1+x]-70*Log[2+x]");
  }

  // Timofeev Problems.input:432
  public void test0281() {
    check( //
        "Integrate[1/((1+x^4)*(2+x^4)^(1/4)), x]", //
        "-1/2*ArcTan[1-x*Sqrt[2]/(2+x^4)^(1/4)]/Sqrt[2]+1/2*ArcTan[1+x*Sqrt[2]/(2+x^4)^(1/4)]/Sqrt[2]-1/4*Log[1-x*Sqrt[2]/(2+x^4)^(1/4)+x^2/Sqrt[2+x^4]]/Sqrt[2]+1/4*Log[1+x*Sqrt[2]/(2+x^4)^(1/4)+x^2/Sqrt[2+x^4]]/Sqrt[2]");
  }

  // Timofeev Problems.input:472
  public void test0282() {
    check( //
        "Integrate[Cot[-3/4*Pi+1/3*x]^4, x]", //
        "x+3*Cot[1/4*Pi+1/3*x]-Cot[1/4*Pi+1/3*x]^3");
  }

  // Timofeev Problems.input:482
  public void test0283() {
    check( //
        "Integrate[Cos[x]^8*Sin[x]^8, x]", //
        "35/32768*x+35/32768*Cos[x]*Sin[x]+35/49152*Cos[x]^3*Sin[x]+7/12288*Cos[x]^5*Sin[x]+1/2048*Cos[x]^7*Sin[x]-1/256*Cos[x]^9*Sin[x]-5/384*Cos[x]^9*Sin[x]^3-1/32*Cos[x]^9*Sin[x]^5-1/16*Cos[x]^9*Sin[x]^7");
  }

  // Timofeev Problems.input:493
  public void test0284() {
    check( //
        "Integrate[Sec[x]^4*Tan[x]^(3/2), x]", //
        "2/5*Tan[x]^(5/2)+2/9*Tan[x]^(9/2)");
  }

  // Timofeev Problems.input:573
  public void test0285() {
    check( //
        "Integrate[Cos[x]*(5*Cos[x]^2+Sin[x]^2)^(5/2), x]", //
        "625/32*ArcSin[2*Sin[x]/Sqrt[5]]+25/24*Sin[x]*(5-4*Sin[x]^2)^(3/2)+1/6*Sin[x]*(5-4*Sin[x]^2)^(5/2)+125/16*Sin[x]*Sqrt[5-4*Sin[x]^2]");
  }

  // Timofeev Problems.input:599
  public void test0286() {
    check( //
        "Integrate[Tan[x]*(1+5*Tan[x]^2)^(5/2), x]", //
        "-32*ArcTan[1/2*Sqrt[1+5*Tan[x]^2]]+16*Sqrt[1+5*Tan[x]^2]-4/3*(1+5*Tan[x]^2)^(3/2)+1/5*(1+5*Tan[x]^2)^(5/2)");
  }

  // Timofeev Problems.input:638
  public void test0287() {
    check( //
        "Integrate[1/(-1-2*x+x^2)^(5/2), x]", //
        "1/6*(1-x)/(-1-2*x+x^2)^(3/2)+1/6*(-1+x)/Sqrt[-1-2*x+x^2]");
  }

  // Timofeev Problems.input:647
  public void test0288() {
    check( //
        "Integrate[1/(-7+6*x-x^2)^(5/2), x]", //
        "1/6*(-3+x)/(-7+6*x-x^2)^(3/2)+1/6*(-3+x)/Sqrt[-7+6*x-x^2]");
  }

  // Timofeev Problems.input:661
  public void test0289() {
    check( //
        "Integrate[x^2*Cos[x]*Sin[x]^2, x]", //
        "4/9*x*Cos[x]-4/9*Sin[x]+2/9*x*Cos[x]*Sin[x]^2-2/27*Sin[x]^3+1/3*x^2*Sin[x]^3");
  }

  // Timofeev Problems.input:726
  public void test0290() {
    check( //
        "Integrate[x^3/E^(1/2*x), x]", //
        "(-96)/E^(1/2*x)-48*x/E^(1/2*x)-12*x^2/E^(1/2*x)-2*x^3/E^(1/2*x)");
  }

  // Timofeev Problems.input:736
  public void test0291() {
    check( //
        "Integrate[Cos[3/2*x]/(3^(3*x))^(1/4), x]", //
        "-4/3*Cos[3/2*x]*Log[3]/((3^(3*x))^(1/4)*(4+Log[3]^2))+8/3*Sin[3/2*x]/((3^(3*x))^(1/4)*(4+Log[3]^2))");
  }

  // Timofeev Problems.input:830
  public void test0292() {
    check( //
        "Integrate[-1-8*Log[x]^2+3*Log[x]^3, x]", //
        "-35*x+34*x*Log[x]-17*x*Log[x]^2+3*x*Log[x]^3");
  }

  // Timofeev Problems.input:884
  public void test0293() {
    check( //
        "Integrate[x*ArcCos[x]*Sqrt[1-x^2], x]", //
        "-1/3*x+1/9*x^3-1/3*(1-x^2)^(3/2)*ArcCos[x]");
  }

  // Timofeev Problems.input:904
  public void test0294() {
    check( //
        "Integrate[x*ArcTan[x]/(1+x^2)^2, x]", //
        "1/4*x/(1+x^2)+1/4*ArcTan[x]-1/2*ArcTan[x]/(1+x^2)");
  }

  // Timofeev Problems.input:941
  public void test0295() {
    check( //
        "Integrate[ArcTan[Sqrt[(-a+x)/(a+x)]], x]", //
        "x*ArcTan[Sqrt[(-a+x)/(a+x)]]-a*ArcTanh[Sqrt[(-a+x)/(a+x)]]");
  }

  // Welz Problems.input:57
  public void test0296() {
    check( //
        "Integrate[(x-Sqrt[a+x^2])^b, x]", //
        "-1/2*a*(x-Sqrt[a+x^2])^(-1+b)/(1-b)+1/2*(x-Sqrt[a+x^2])^(1+b)/(1+b)");
  }

  // Welz Problems.input:173
  public void test0297() {
    check( //
        "Integrate[x/((10+x^3+6*Sqrt[3])*Sqrt[1+x^3]), x]", //
        "-1/2*ArcTan[3^(1/4)*(1+x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[1+x^3])]*(2-Sqrt[3])/(3^(3/4)*Sqrt[2])-1/3*ArcTan[(1-Sqrt[3])*Sqrt[1+x^3]/(3^(3/4)*Sqrt[2])]*(2-Sqrt[3])/(3^(3/4)*Sqrt[2])-1/6*ArcTanh[3^(1/4)*(1+x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[1+x^3])]*(2-Sqrt[3])/(3^(1/4)*Sqrt[2])-1/3*ArcTanh[3^(1/4)*(1-2*x+Sqrt[3])/(Sqrt[2]*Sqrt[1+x^3])]*(2-Sqrt[3])/(3^(1/4)*Sqrt[2])");
  }
}
