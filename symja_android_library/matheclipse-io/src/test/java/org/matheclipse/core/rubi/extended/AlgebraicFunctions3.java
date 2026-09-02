package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 1 Algebraic functions of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class AlgebraicFunctions3 extends AbstractRubiTestCase {
  static boolean init = true;

  public AlgebraicFunctions3(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("AlgebraicFunctions3");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:25
  public void test0001() {
    check( //
        "Integrate[(A+B*x)*(a+b*x^2)^(3/2)/x, x]", //
        "1/12*(4*A+3*B*x)*(a+b*x^2)^(3/2)-a^(3/2)*A*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]+3/8*a^2*B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+1/8*a*(8*A+3*B*x)*Sqrt[a+b*x^2]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:41
  public void test0002() {
    check( //
        "Integrate[(A+B*x)/(x*Sqrt[a+b*x^2]), x]", //
        "-A*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/Sqrt[a]+B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:115
  public void test0003() {
    check( //
        "Integrate[x*(a+b*x^2)^2*(A+B*x+C*x^2+D*x^3), x]", //
        "1/3*a^2*B*x^3+1/4*a^2*C*x^4+1/5*a*(2*b*B+a*D)*x^5+1/3*a*b*C*x^6+1/7*b*(b*B+2*a*D)*x^7+1/8*b^2*C*x^8+1/9*b^2*D*x^9+1/6*A*(a+b*x^2)^3/b");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:131
  public void test0004() {
    check( //
        "Integrate[x^4*(A+B*x+C*x^2+D*x^3)/(a+b*x^2), x]", //
        "-a*(A*b-a*C)*x/b^3-1/2*a*(b*B-a*D)*x^2/b^3+1/3*(A*b-a*C)*x^3/b^2+1/4*(b*B-a*D)*x^4/b^2+1/5*C*x^5/b+1/6*D*x^6/b+a^(3/2)*(A*b-a*C)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(7/2)+1/2*a^2*(b*B-a*D)*Log[a+b*x^2]/b^4");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:183
  public void test0005() {
    check( //
        "Integrate[x^6*(c+d*x^2+e*x^4+f*x^6)/(a+b*x^2), x]", //
        "a^2*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/b^6-1/3*a*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^3/b^5+1/5*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^5/b^4+1/7*(b^2*d-a*b*e+a^2*f)*x^7/b^3+1/9*(b*e-a*f)*x^9/b^2+1/11*f*x^11/b-a^(5/2)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(13/2)");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:231
  public void test0006() {
    check( //
        "Integrate[(c+d*x^2+e*x^4+f*x^6)/(x^6*Sqrt[a+b*x^2]), x]", //
        "f*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]-1/5*c*Sqrt[a+b*x^2]/(a*x^5)+1/15*(4*b*c-5*a*d)*Sqrt[a+b*x^2]/(a^2*x^3)-1/15*(8*b^2*c-10*a*b*d+15*a^2*e)*Sqrt[a+b*x^2]/(a^3*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:159
  public void test0007() {
    check( //
        "Integrate[1/(b*x^n)^(2/3), x]", //
        "3*x/((3-2*n)*(b*x^n)^(2/3))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:228
  public void test0008() {
    check( //
        "Integrate[x/(a*x^n)^(1/n), x]", //
        "x^2/(a*x^n)^(1/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:294
  public void test0009() {
    check( //
        "Integrate[x^5*(a+b*x^3)^3, x]", //
        "-1/12*a*(a+b*x^3)^4/b^2+1/15*(a+b*x^3)^5/b^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:322
  public void test0010() {
    check( //
        "Integrate[(a+b*x^3)^5/x^10, x]", //
        "-1/9*a^5/x^9-5/6*a^4*b/x^6-10/3*a^3*b^2/x^3+5/3*a*b^4*x^3+1/6*b^5*x^6+10*a^2*b^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:350
  public void test0011() {
    check( //
        "Integrate[(a+b*x^3)^8/x^10, x]", //
        "-1/9*a^8/x^9-4/3*a^7*b/x^6-28/3*a^6*b^2/x^3+70/3*a^4*b^4*x^3+28/3*a^3*b^5*x^6+28/9*a^2*b^6*x^9+2/3*a*b^7*x^12+1/15*b^8*x^15+56*a^5*b^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:380
  public void test0012() {
    check( //
        "Integrate[x^4/(a+b*x^3), x]", //
        "1/2*x^2/b+1/3*a^(2/3)*Log[a^(1/3)+b^(1/3)*x]/b^(5/3)-1/6*a^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(5/3)+a^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(5/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:394
  public void test0013() {
    check( //
        "Integrate[1/(a+b*x^3)^2, x]", //
        "1/3*x/(a*(a+b*x^3))+2/9*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(1/3))-1/9*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(1/3))-2/3*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:408
  public void test0014() {
    check( //
        "Integrate[1/(a+b*x^3)^3, x]", //
        "1/6*x/(a*(a+b*x^3)^2)+5/18*x/(a^2*(a+b*x^3))+5/27*Log[a^(1/3)+b^(1/3)*x]/(a^(8/3)*b^(1/3))-5/54*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(8/3)*b^(1/3))-5/9*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(8/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:422
  public void test0015() {
    check( //
        "Integrate[1/(-1+a+b*x^3), x]", //
        "1/3*Log[(1-a)^(1/3)-b^(1/3)*x]/((1-a)^(2/3)*b^(1/3))-1/6*Log[(1-a)^(2/3)+(1-a)^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/((1-a)^(2/3)*b^(1/3))-ArcTan[(1+2*b^(1/3)*x/(1-a)^(1/3))/Sqrt[3]]/((1-a)^(2/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:637
  public void test0016() {
    check( //
        "Integrate[1/(x^12*(a+b*x^3)^(1/3)), x]", //
        "-1/11*(a+b*x^3)^(2/3)/(a*x^11)+9/88*b*(a+b*x^3)^(2/3)/(a^2*x^8)-27/220*b^2*(a+b*x^3)^(2/3)/(a^3*x^5)+81/440*b^3*(a+b*x^3)^(2/3)/(a^4*x^2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:656
  public void test0017() {
    check( //
        "Integrate[1/(a-b*x^3)^(1/3), x]", //
        "1/2*Log[b^(1/3)*x+(a-b*x^3)^(1/3)]/b^(1/3)-ArcTan[(1-2*b^(1/3)*x/(a-b*x^3)^(1/3))/Sqrt[3]]/(b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:737
  public void test0018() {
    check( //
        "Integrate[x^5/(a+c*x^4), x]", //
        "1/2*x^2/c-1/2*ArcTan[x^2*Sqrt[c]/Sqrt[a]]*Sqrt[a]/c^(3/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:751
  public void test0019() {
    check( //
        "Integrate[x^7/(a+c*x^4)^2, x]", //
        "1/4*a/(c^2*(a+c*x^4))+1/4*Log[a+c*x^4]/c^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:779
  public void test0020() {
    check( //
        "Integrate[x^7/(2+3*x^4), x]", //
        "1/12*x^4-1/18*Log[2+3*x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:875
  public void test0021() {
    check( //
        "Integrate[Sqrt[a+c*x^4]/x^3, x]", //
        "1/2*ArcTanh[x^2*Sqrt[c]/Sqrt[a+c*x^4]]*Sqrt[c]-1/2*Sqrt[a+c*x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:919
  public void test0022() {
    check( //
        "Integrate[1/(x*Sqrt[a+b*x^4]), x]", //
        "-1/2*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:933
  public void test0023() {
    check( //
        "Integrate[x^2/Sqrt[a+b*x^4], x]", //
        "x*Sqrt[a+b*x^4]/(Sqrt[b]*(Sqrt[a]+x^2*Sqrt[b]))-a^(1/4)*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])+1/2*a^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1031
  public void test0024() {
    check( //
        "Integrate[1/(x^11*Sqrt[1+x^4]), x]", //
        "-1/10*Sqrt[1+x^4]/x^10+2/15*Sqrt[1+x^4]/x^6-4/15*Sqrt[1+x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1151
  public void test0025() {
    check( //
        "Integrate[(a+b*x^4)^(3/4)/x^16, x]", //
        "-1/15*(a+b*x^4)^(7/4)/(a*x^15)+8/165*b*(a+b*x^4)^(7/4)/(a^2*x^11)-32/1155*b^2*(a+b*x^4)^(7/4)/(a^3*x^7)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1165
  public void test0026() {
    check( //
        "Integrate[(a+b*x^4)^(5/4)/x, x]", //
        "a*(a+b*x^4)^(1/4)+1/5*(a+b*x^4)^(5/4)-1/2*a^(5/4)*ArcTan[(a+b*x^4)^(1/4)/a^(1/4)]-1/2*a^(5/4)*ArcTanh[(a+b*x^4)^(1/4)/a^(1/4)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1179
  public void test0027() {
    check( //
        "Integrate[(a+b*x^4)^(5/4)/x^6, x]", //
        "-b*(a+b*x^4)^(1/4)/x-1/5*(a+b*x^4)^(5/4)/x^5-1/2*b^(5/4)*ArcTan[b^(1/4)*x/(a+b*x^4)^(1/4)]+1/2*b^(5/4)*ArcTanh[b^(1/4)*x/(a+b*x^4)^(1/4)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1265
  public void test0028() {
    check( //
        "Integrate[x/(a+b*x^4)^(5/4), x]", //
        "(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]/((a+b*x^4)^(1/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1279
  public void test0029() {
    check( //
        "Integrate[x^6/(a+b*x^4)^(5/4), x]", //
        "1/2*x^3/(b*(a+b*x^4)^(1/4))+3/2*(1+a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/(b^(3/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1568
  public void test0030() {
    check( //
        "Integrate[1/(x^16*Sqrt[2+x^6]), x]", //
        "-1/30*Sqrt[2+x^6]/x^15+1/45*Sqrt[2+x^6]/x^9-1/45*Sqrt[2+x^6]/x^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1650
  public void test0031() {
    check( //
        "Integrate[x^5/(a+b*x^8), x]", //
        "-1/4*ArcTan[1-b^(1/4)*x^2*Sqrt[2]/a^(1/4)]/(a^(1/4)*b^(3/4)*Sqrt[2])+1/4*ArcTan[1+b^(1/4)*x^2*Sqrt[2]/a^(1/4)]/(a^(1/4)*b^(3/4)*Sqrt[2])+1/8*Log[-a^(1/4)*b^(1/4)*x^2*Sqrt[2]+Sqrt[a]+x^4*Sqrt[b]]/(a^(1/4)*b^(3/4)*Sqrt[2])-1/8*Log[a^(1/4)*b^(1/4)*x^2*Sqrt[2]+Sqrt[a]+x^4*Sqrt[b]]/(a^(1/4)*b^(3/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1664
  public void test0032() {
    check( //
        "Integrate[1/(x^4*(a+b*x^8)), x]", //
        "(-1/3)/(a*x^3)-1/4*b^(3/8)*ArcTan[b^(1/8)*x/(-a)^(1/8)]/(-a)^(11/8)-1/4*b^(3/8)*ArcTanh[b^(1/8)*x/(-a)^(1/8)]/(-a)^(11/8)-1/4*b^(3/8)*ArcTan[1-b^(1/8)*x*Sqrt[2]/(-a)^(1/8)]/((-a)^(11/8)*Sqrt[2])+1/4*b^(3/8)*ArcTan[1+b^(1/8)*x*Sqrt[2]/(-a)^(1/8)]/((-a)^(11/8)*Sqrt[2])-1/8*b^(3/8)*Log[(-a)^(1/4)+b^(1/4)*x^2-(-a)^(1/8)*b^(1/8)*x*Sqrt[2]]/((-a)^(11/8)*Sqrt[2])+1/8*b^(3/8)*Log[(-a)^(1/4)+b^(1/4)*x^2+(-a)^(1/8)*b^(1/8)*x*Sqrt[2]]/((-a)^(11/8)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1716
  public void test0033() {
    check( //
        "Integrate[x^3*Sqrt[1+x^8], x]", //
        "1/8*ArcSinh[x^4]+1/8*x^4*Sqrt[1+x^8]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1845
  public void test0034() {
    check( //
        "Integrate[(a+b/x)^3*x^6, x]", //
        "1/4*b^3*x^4+3/5*a*b^2*x^5+1/2*a^2*b*x^6+1/7*a^3*x^7");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1859
  public void test0035() {
    check( //
        "Integrate[(a+b/x)^8*x^15, x]", //
        "1/8*b^8*x^8+8/9*a*b^7*x^9+14/5*a^2*b^6*x^10+56/11*a^3*b^5*x^11+35/6*a^4*b^4*x^12+56/13*a^5*b^3*x^13+2*a^6*b^2*x^14+8/15*a^7*b*x^15+1/16*a^8*x^16");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1873
  public void test0036() {
    check( //
        "Integrate[(a+b/x)^8, x]", //
        "-1/7*b^8/x^7-4/3*a*b^7/x^6-28/5*a^2*b^6/x^5-14*a^3*b^5/x^4-70/3*a^4*b^4/x^3-28*a^5*b^3/x^2-28*a^6*b^2/x+a^8*x+8*a^7*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2045
  public void test0037() {
    check( //
        "Integrate[(a+b/x)^(1/2)*x^(7/2), x]", //
        "-32/315*b^3*(a+b/x)^(3/2)*x^(3/2)/a^4+16/105*b^2*(a+b/x)^(3/2)*x^(5/2)/a^3-4/21*b*(a+b/x)^(3/2)*x^(7/2)/a^2+2/9*(a+b/x)^(3/2)*x^(9/2)/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2059
  public void test0038() {
    check( //
        "Integrate[(a+b/x)^(3/2)/x^(3/2), x]", //
        "-3/4*a^2*ArcTanh[Sqrt[b]/(Sqrt[a+b/x]*Sqrt[x])]/Sqrt[b]-1/2*(a+b/x)^(3/2)/Sqrt[x]-3/4*a*Sqrt[a+b/x]/Sqrt[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2075
  public void test0039() {
    check( //
        "Integrate[x^(1/2)/(a+b/x)^(1/2), x]", //
        "2/3*x^(3/2)*Sqrt[a+b/x]/a-4/3*b*Sqrt[a+b/x]*Sqrt[x]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2123
  public void test0040() {
    check( //
        "Integrate[(a+b/x^2)^2*x, x]", //
        "-1/2*b^2/x^2+1/2*a^2*x^2+2*a*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2137
  public void test0041() {
    check( //
        "Integrate[(a+b/x^2)^3, x]", //
        "-1/5*b^3/x^5-a*b^2/x^3-3*a^2*b/x+a^3*x");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2181
  public void test0042() {
    check( //
        "Integrate[x/(a+b/x^2)^3, x]", //
        "1/2*x^2/a^3+1/4*b^3/(a^4*(b+a*x^2)^2)-3/2*b^2/(a^4*(b+a*x^2))-3/2*b*Log[b+a*x^2]/a^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2199
  public void test0043() {
    check( //
        "Integrate[(a+b/x^2)^(1/2)*x^2, x]", //
        "1/3*(a+b/x^2)^(3/2)*x^3/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2291
  public void test0044() {
    check( //
        "Integrate[x^2/(a+b/x^3), x]", //
        "1/3*x^3/a-1/3*b*Log[b+a*x^3]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2421
  public void test0045() {
    check( //
        "Integrate[(a+b/x^4)^(5/2)/x^2, x]", //
        "-10/77*a*(a+b/x^4)^(3/2)/x-1/11*(a+b/x^4)^(5/2)/x-20/77*a^2*Sqrt[a+b/x^4]/x-20/77*a^(11/4)*EllipticF[2*ArcCot[a^(1/4)*x/b^(1/4)],1/2]*(Sqrt[a]+Sqrt[b]/x^2)*Sqrt[(a+b/x^4)/(Sqrt[a]+Sqrt[b]/x^2)^2]/(b^(1/4)*Sqrt[a+b/x^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2465
  public void test0046() {
    check( //
        "Integrate[1/(x*Sqrt[-a+b/x^5]), x]", //
        "-2/5*ArcTan[Sqrt[-a+b/x^5]/Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2684
  public void test0047() {
    check( //
        "Integrate[x^2*(a+b*Sqrt[x])^p, x]", //
        "-2*a^5*(a+b*Sqrt[x])^(1+p)/(b^6*(1+p))+10*a^4*(a+b*Sqrt[x])^(2+p)/(b^6*(2+p))-20*a^3*(a+b*Sqrt[x])^(3+p)/(b^6*(3+p))+20*a^2*(a+b*Sqrt[x])^(4+p)/(b^6*(4+p))-10*a*(a+b*Sqrt[x])^(5+p)/(b^6*(5+p))+2*(a+b*Sqrt[x])^(6+p)/(b^6*(6+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2842
  public void test0048() {
    check( //
        "Integrate[x^(2/3)/(1+x^(1/3)), x]", //
        "-3*x^(1/3)+3/2*x^(2/3)-x+3/4*x^(4/3)+3*Log[1+x^(1/3)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2909
  public void test0049() {
    check( //
        "Integrate[a+b/x^(1/3), x]", //
        "3/2*b*x^(2/3)+a*x");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2923
  public void test0050() {
    check( //
        "Integrate[(a+b/x^(1/3))^3*x^4, x]", //
        "1/4*b^3*x^4+9/13*a*b^2*x^(13/3)+9/14*a^2*b*x^(14/3)+1/5*a^3*x^5");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2987
  public void test0051() {
    check( //
        "Integrate[a+b*x^n, x]", //
        "a*x+b*x^(1+n)/(1+n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3083
  public void test0052() {
    check( //
        "Integrate[(a+b*x^n)^2/x, x]", //
        "2*a*b*x^n/n+1/2*b^2*x^(2*n)/n+a^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3097
  public void test0053() {
    check( //
        "Integrate[x^(-1-3*n)*(a+b*x^n)^3, x]", //
        "-1/3*a^3/(n*x^(3*n))-3/2*a^2*b/(n*x^(2*n))-3*a*b^2/(n*x^n)+b^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3111
  public void test0054() {
    check( //
        "Integrate[x^(-1-5*n)*(a+b*x^n)^5, x]", //
        "-1/5*a^5/(n*x^(5*n))-5/4*a^4*b/(n*x^(4*n))-10/3*a^3*b^2/(n*x^(3*n))-5*a^2*b^3/(n*x^(2*n))-5*a*b^4/(n*x^n)+b^5*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3139
  public void test0055() {
    check( //
        "Integrate[x^(-1-13*n)*(a+b*x^n)^8, x]", //
        "-1/13*(a+b*x^n)^9/(a*n*x^(13*n))+1/39*b*(a+b*x^n)^9/(a^2*n*x^(12*n))-1/143*b^2*(a+b*x^n)^9/(a^3*n*x^(11*n))+1/715*b^3*(a+b*x^n)^9/(a^4*n*x^(10*n))-1/6435*b^4*(a+b*x^n)^9/(a^5*n*x^(9*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3157
  public void test0056() {
    check( //
        "Integrate[1/(x*(a+b*x^n)), x]", //
        "Log[x]/a-Log[a+b*x^n]/(a*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3187
  public void test0057() {
    check( //
        "Integrate[x^(-1-2*n)/(a+b*x^n)^2, x]", //
        "(-1/2)/(a^2*n*x^(2*n))+2*b/(a^3*n*x^n)+b^2/(a^3*n*(a+b*x^n))+3*b^2*Log[x]/a^4-3*b^2*Log[a+b*x^n]/(a^4*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3411
  public void test0058() {
    check( //
        "Integrate[1/(c*(a+b*x)^3)^(1/2), x]", //
        "-2*(a+b*x)/(b*Sqrt[c*(a+b*x)^3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3508
  public void test0059() {
    check( //
        "Integrate[(c*e+d*e*x)/(a+b*(c+d*x)^3), x]", //
        "-1/3*e*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(1/3)*b^(2/3)*d)+1/6*e*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(1/3)*b^(2/3)*d)-e*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(1/3)*b^(2/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3543
  public void test0060() {
    check( //
        "Integrate[x/Sqrt[a+b*(c+d*x)^4], x]", //
        "1/2*ArcTanh[(c+d*x)^2*Sqrt[b]/Sqrt[a+b*(c+d*x)^4]]/(d^2*Sqrt[b])-1/2*c*EllipticF[2*ArcTan[b^(1/4)*(c+d*x)/a^(1/4)],1/2]*(Sqrt[a]+(c+d*x)^2*Sqrt[b])*Sqrt[(a+b*(c+d*x)^4)/(Sqrt[a]+(c+d*x)^2*Sqrt[b])^2]/(a^(1/4)*b^(1/4)*d^2*Sqrt[a+b*(c+d*x)^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3718
  public void test0061() {
    check( //
        "Integrate[x/(1+(x^n)^(1/n))^2, x]", //
        "x^2/((x^n)^(2/n)*(1+(x^n)^(1/n)))+x^2*Log[1+(x^n)^(1/n)]/(x^n)^(2/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3782
  public void test0062() {
    check( //
        "Integrate[(c*x^n)^(1/n)*(a+b*(c*x^n)^(1/n)), x]", //
        "1/2*a*x*(c*x^n)^(1/n)+1/3*b*x*(c*x^n)^(2/n)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:36
  public void test0063() {
    check( //
        "Integrate[(c+d*x^3)^2/(a+b*x^3)^2, x]", //
        "d^2*x/b^2+1/3*(b*c-a*d)^2*x/(a*b^2*(a+b*x^3))+2/9*(b*c-a*d)*(b*c+2*a*d)*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(7/3))-1/9*(b*c-a*d)*(b*c+2*a*d)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(7/3))-2/3*(b*c-a*d)*(b*c+2*a*d)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(7/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:115
  public void test0064() {
    check( //
        "Integrate[1/((a+b*x^3)^(4/3)*(c+d*x^3)), x]", //
        "b*x/(a*(b*c-a*d)*(a+b*x^3)^(1/3))-1/6*d*Log[c+d*x^3]/(c^(2/3)*(b*c-a*d)^(4/3))+1/2*d*Log[(b*c-a*d)^(1/3)*x/c^(1/3)-(a+b*x^3)^(1/3)]/(c^(2/3)*(b*c-a*d)^(4/3))-d*ArcTan[(1+2*(b*c-a*d)^(1/3)*x/(c^(1/3)*(a+b*x^3)^(1/3)))/Sqrt[3]]/(c^(2/3)*(b*c-a*d)^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:271
  public void test0065() {
    check( //
        "Integrate[1/((a+b*x^4)^(1/4)*(c+d*x^4)^2), x]", //
        "-1/4*d*x*(a+b*x^4)^(3/4)/(c*(b*c-a*d)*(c+d*x^4))+1/8*(4*b*c-3*a*d)*ArcTan[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(7/4)*(b*c-a*d)^(5/4))+1/8*(4*b*c-3*a*d)*ArcTanh[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(7/4)*(b*c-a*d)^(5/4))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:466
  public void test0066() {
    check( //
        "Integrate[(a+b*x^n)*(c+d*x^n)^(-2+(-1)/n), x]", //
        "x*(a+b*x^n)*(c+d*x^n)^(-1+(-1)/n)/(c*(1+n))+a*n*x/(c^2*(1+n)*(c+d*x^n)^(1/n))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:503
  public void test0067() {
    check( //
        "Integrate[x^2*(a+b*x^2)/(Sqrt[-1+c*x]*Sqrt[1+c*x]), x]", //
        "1/8*(3*b+4*a*c^2)*ArcCosh[c*x]/c^5+1/8*(3*b+4*a*c^2)*x*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^4+1/4*b*x^3*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^2");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:517
  public void test0068() {
    check( //
        "Integrate[(a+b*x^2)/(x^2*Sqrt[-c+d*x]*Sqrt[c+d*x]), x]", //
        "2*b*ArcTanh[Sqrt[-c+d*x]/Sqrt[c+d*x]]/d+a*Sqrt[-c+d*x]*Sqrt[c+d*x]/(c^2*x)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:531
  public void test0069() {
    check( //
        "Integrate[(1+c^2*x^2)/(x*Sqrt[-1+c*x]*Sqrt[1+c*x]), x]", //
        "ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]+Sqrt[-1+c*x]*Sqrt[1+c*x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:22
  public void test0070() {
    check( //
        "Integrate[x^2*(a+b*x^3)^2*(A+B*x^3), x]", //
        "1/9*(A*b-a*B)*(a+b*x^3)^3/b^2+1/12*B*(a+b*x^3)^4/b^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:50
  public void test0071() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^7, x]", //
        "-1/6*a^5*A/x^6-1/3*a^4*(5*A*b+a*B)/x^3+10/3*a^2*b^2*(A*b+a*B)*x^3+5/6*a*b^3*(A*b+2*a*B)*x^6+1/9*b^4*(A*b+5*a*B)*x^9+1/12*b^5*B*x^12+5*a^3*b*(2*A*b+a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:64
  public void test0072() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^21, x]", //
        "-1/20*a^5*A/x^20-1/17*a^4*(5*A*b+a*B)/x^17-5/14*a^3*b*(2*A*b+a*B)/x^14-10/11*a^2*b^2*(A*b+a*B)/x^11-5/8*a*b^3*(A*b+2*a*B)/x^8-1/5*b^4*(A*b+5*a*B)/x^5-1/2*b^5*B/x^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:80
  public void test0073() {
    check( //
        "Integrate[(A+B*x^3)/(x^5*(a+b*x^3)), x]", //
        "-1/4*A/(a*x^4)+(A*b-a*B)/(a^2*x)-1/3*b^(1/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/a^(7/3)+1/6*b^(1/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(7/3)-b^(1/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(7/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:184
  public void test0074() {
    check( //
        "Integrate[x^(7/2)*(A+B*x^3)/(a+b*x^3), x]", //
        "2/3*(A*b-a*B)*x^(3/2)/b^2+2/9*B*x^(9/2)/b-2/3*(A*b-a*B)*ArcTan[x^(3/2)*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(5/2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:262
  public void test0075() {
    check( //
        "Integrate[(A+B*x^3)/(x^2*Sqrt[a+b*x^3]), x]", //
        "-A*Sqrt[a+b*x^3]/(a*x)+(A*b+2*a*B)*Sqrt[a+b*x^3]/(a*b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+(A*b+2*a*B)*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a^(2/3)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-1/2*3^(1/4)*(A*b+2*a*B)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(a^(2/3)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:313
  public void test0076() {
    check( //
        "Integrate[x^8/((4*c+d*x^3)*Sqrt[c+d*x^3]), x]", //
        "2/9*(c+d*x^3)^(3/2)/d^3+32/3*c^(3/2)*ArcTan[Sqrt[c+d*x^3]/(Sqrt[3]*Sqrt[c])]/(d^3*Sqrt[3])-10/3*c*Sqrt[c+d*x^3]/d^3");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:402
  public void test0077() {
    check( //
        "Integrate[x/((-b*x^3+2*a*(5+3*Sqrt[3]))*Sqrt[a-b*x^3]), x]", //
        "-1/2*ArcTan[3^(1/4)*a^(1/6)*(a^(1/3)-b^(1/3)*x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[a-b*x^3])]*(2-Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/3*ArcTan[(1-Sqrt[3])*Sqrt[a-b*x^3]/(3^(3/4)*Sqrt[2]*Sqrt[a])]*(2-Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/6*ArcTanh[3^(1/4)*a^(1/6)*(a^(1/3)-b^(1/3)*x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[a-b*x^3])]*(2-Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/3*ArcTanh[3^(1/4)*a^(1/6)*(2*b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))/(Sqrt[2]*Sqrt[a-b*x^3])]*(2-Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:458
  public void test0078() {
    check( //
        "Integrate[x^8*Sqrt[c+d*x^3]/(8*c-d*x^3)^2, x]", //
        "2/9*(c+d*x^3)^(3/2)/d^3+64/27*c*(c+d*x^3)^(3/2)/(d^3*(8*c-d*x^3))-352/9*c^(3/2)*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]/d^3+352/27*c*Sqrt[c+d*x^3]/d^3");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:876
  public void test0079() {
    check( //
        "Integrate[1/(x*(a+b*x^4)*(c+d*x^4)), x]", //
        "Log[x]/(a*c)-1/4*b*Log[a+b*x^4]/(a*(b*c-a*d))+1/4*d*Log[c+d*x^4]/(c*(b*c-a*d))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1013
  public void test0080() {
    check( //
        "Integrate[x^17/((a+b*x^6)^2*Sqrt[c+d*x^6]), x]", //
        "1/6*a*(4*b*c-3*a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^6]/Sqrt[b*c-a*d]]/(b^(5/2)*(b*c-a*d)^(3/2))+1/3*Sqrt[c+d*x^6]/(b^2*d)-1/6*a^2*Sqrt[c+d*x^6]/(b^2*(b*c-a*d)*(a+b*x^6))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1104
  public void test0081() {
    check( //
        "Integrate[(a+b/x^2)*x^2*Sqrt[c+d/x^2], x]", //
        "1/3*a*(c+d/x^2)^(3/2)*x^3/c-b*ArcTanh[Sqrt[d]/(x*Sqrt[c+d/x^2])]*Sqrt[d]+b*x*Sqrt[c+d/x^2]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1118
  public void test0082() {
    check( //
        "Integrate[(a+b/x^2)*(c+d/x^2)^(3/2)*x^8, x]", //
        "-2/315*d*(9*b*c-4*a*d)*(c+d/x^2)^(5/2)*x^5/c^3+1/63*(9*b*c-4*a*d)*(c+d/x^2)^(5/2)*x^7/c^2+1/9*a*(c+d/x^2)^(5/2)*x^9/c");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1134
  public void test0083() {
    check( //
        "Integrate[(a+b/x^2)*x^2/Sqrt[c+d/x^2], x]", //
        "1/3*(3*b*c-2*a*d)*x*Sqrt[c+d/x^2]/c^2+1/3*a*x^3*Sqrt[c+d/x^2]/c");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1252
  public void test0084() {
    check( //
        "Integrate[(b+2*c*x^n)/(x*(b+c*x^n)), x]", //
        "Log[x]+Log[b+c*x^n]/n");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:55
  public void test0085() {
    check( //
        "Integrate[(2*(a/b)^(2/3)*C+C*x^2)/(a-b*x^3), x]", //
        "-C*Log[(a/b)^(1/3)-x]/b+2*C*ArcTan[(1+2*x/(a/b)^(1/3))/Sqrt[3]]/(b*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:87
  public void test0086() {
    check( //
        "Integrate[(a*c+a*d*x+b*c*x^3+b*d*x^4)/(a+b*x^3)^(7/2), x]", //
        "2/9*x*(c+d*x)/(a*(a+b*x^3)^(3/2))+2/27*x*(7*c+5*d*x)/(a^2*Sqrt[a+b*x^3])-10/27*d*Sqrt[a+b*x^3]/(a^2*b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+5/9*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(3/4)*a^(5/3)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+2/27*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(7*b^(1/3)*c+5*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a^2*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:133
  public void test0087() {
    check( //
        "Integrate[(1-x-Sqrt[3])/Sqrt[1-x^3], x]", //
        "-2*Sqrt[1-x^3]/(1-x+Sqrt[3])+3^(1/4)*(1-x)*EllipticE[ArcSin[(1-x-Sqrt[3])/(1-x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x+Sqrt[3])^2]/(Sqrt[1-x^3]*Sqrt[(1-x)/(1-x+Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:147
  public void test0088() {
    check( //
        "Integrate[(1+(b/a)^(1/3)*x-Sqrt[3])/Sqrt[-a-b*x^3], x]", //
        "-2*(b/a)^(1/3)*Sqrt[-a-b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1-Sqrt[3])))+2*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*(b^(1/3)*(1-Sqrt[3])-a^(1/3)*(b/a)^(1/3)*(1+Sqrt[3]))*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2-Sqrt[3]]/(3^(1/4)*b^(2/3)*Sqrt[-a-b*x^3]*Sqrt[-a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2])+3^(1/4)*a^(1/3)*(b/a)^(1/3)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2+Sqrt[3]]/(b^(2/3)*Sqrt[-a-b*x^3]*Sqrt[-a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:175
  public void test0089() {
    check( //
        "Integrate[(c+d*x)/(a+b*x^4)^3, x]", //
        "1/8*x*(c+d*x)/(a*(a+b*x^4)^2)+1/32*x*(7*c+6*d*x)/(a^2*(a+b*x^4))-21/64*c*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(11/4)*b^(1/4)*Sqrt[2])+21/64*c*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(11/4)*b^(1/4)*Sqrt[2])-21/128*c*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(11/4)*b^(1/4)*Sqrt[2])+21/128*c*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(11/4)*b^(1/4)*Sqrt[2])+3/16*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:213
  public void test0090() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^3, x]", //
        "a^3*c*x+1/2*a^3*d*x^2+1/3*a^3*e*x^3+3/5*a^2*b*c*x^5+1/2*a^2*b*d*x^6+3/7*a^2*b*e*x^7+1/3*a*b^2*c*x^9+3/10*a*b^2*d*x^10+3/11*a*b^2*e*x^11+1/13*b^3*c*x^13+1/14*b^3*d*x^14+1/15*b^3*e*x^15+1/16*f*(a+b*x^4)^4/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:249
  public void test0091() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3+g*x^4)/(a+b*x^4)^2, x]", //
        "1/4*x*(b*c-a*g+b*d*x+b*e*x^2+b*f*x^3)/(a*b*(a+b*x^4))+1/4*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])-1/16*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(3*b*c+a*g-e*Sqrt[a]*Sqrt[b])/(a^(7/4)*b^(5/4)*Sqrt[2])+1/16*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(3*b*c+a*g-e*Sqrt[a]*Sqrt[b])/(a^(7/4)*b^(5/4)*Sqrt[2])-1/8*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(3*b*c+a*g+e*Sqrt[a]*Sqrt[b])/(a^(7/4)*b^(5/4)*Sqrt[2])+1/8*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(3*b*c+a*g+e*Sqrt[a]*Sqrt[b])/(a^(7/4)*b^(5/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:371
  public void test0092() {
    check( //
        "Integrate[x^9*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^2, x]", //
        "-a*(2*b^3*c-3*a*b^2*d+4*a^2*b*e-5*a^3*f)*x/b^6+1/4*(b^3*c-2*a*b^2*d+3*a^2*b*e-4*a^3*f)*x^4/b^5+1/7*(b^2*d-2*a*b*e+3*a^2*f)*x^7/b^4+1/10*(b*e-2*a*f)*x^10/b^3+1/13*f*x^13/b^2-1/3*a^2*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/(b^6*(a+b*x^3))+1/9*a^(4/3)*(7*b^3*c-10*a*b^2*d+13*a^2*b*e-16*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(19/3)-1/18*a^(4/3)*(7*b^3*c-10*a*b^2*d+13*a^2*b*e-16*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(19/3)-1/3*a^(4/3)*(7*b^3*c-10*a*b^2*d+13*a^2*b*e-16*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(19/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:399
  public void test0093() {
    check( //
        "Integrate[x^9*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^3, x]", //
        "(b^3*c-3*a*b^2*d+6*a^2*b*e-10*a^3*f)*x/b^6+1/4*(b^2*d-3*a*b*e+6*a^2*f)*x^4/b^5+1/7*(b*e-3*a*f)*x^7/b^4+1/10*f*x^10/b^3-1/6*a^2*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/(b^6*(a+b*x^3)^2)+1/18*a*(13*b^3*c-19*a*b^2*d+25*a^2*b*e-31*a^3*f)*x/(b^6*(a+b*x^3))-1/27*a^(1/3)*(14*b^3*c-35*a*b^2*d+65*a^2*b*e-104*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(19/3)+1/54*a^(1/3)*(14*b^3*c-35*a*b^2*d+65*a^2*b*e-104*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(19/3)+1/9*a^(1/3)*(14*b^3*c-35*a*b^2*d+65*a^2*b*e-104*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(19/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:437
  public void test0094() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)/x, x]", //
        "a*d*x+1/2*a*e*x^2+1/3*b*c*x^3+1/4*b*d*x^4+1/5*b*e*x^5+a*c*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:451
  public void test0095() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)^3/x^3, x]", //
        "-1/2*a^3*c/x^2-a^3*d/x+3*a^2*b*c*x+3/2*a^2*b*d*x^2+a^2*b*e*x^3+3/4*a*b^2*c*x^4+3/5*a*b^2*d*x^5+1/2*a*b^2*e*x^6+1/7*b^3*c*x^7+1/8*b^3*d*x^8+1/9*b^3*e*x^9+a^3*e*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:495
  public void test0096() {
    check( //
        "Integrate[x*(2*(a/b)^(1/3)*C+C*x)/(a-b*x^3), x]", //
        "-C*Log[(a/b)^(1/3)-x]/b-2*C*ArcTan[(1+2*x/(a/b)^(1/3))/Sqrt[3]]/(b*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:513
  public void test0097() {
    check( //
        "Integrate[x*(a+b*x^3)^2*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5), x]", //
        "1/2*a^2*c*x^2+1/4*a^2*e*x^4+1/5*a*(2*b*c+a*f)*x^5+1/6*a^2*g*x^6+1/7*a*(2*b*e+a*h)*x^7+1/8*b*(b*c+2*a*f)*x^8+2/9*a*b*g*x^9+1/10*b*(b*e+2*a*h)*x^10+1/11*b^2*f*x^11+1/12*b^2*g*x^12+1/13*b^2*h*x^13+1/9*d*(a+b*x^3)^3/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:527
  public void test0098() {
    check( //
        "Integrate[(a+b*x^3)^3*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/x^3, x]", //
        "-1/2*a^3*c/x^2-a^3*d/x+a^2*(3*b*c+a*f)*x+1/2*a^2*(3*b*d+a*g)*x^2+a^2*b*e*x^3+3/4*a*b*(b*c+a*f)*x^4+3/5*a*b*(b*d+a*g)*x^5+1/2*a*b^2*e*x^6+1/7*b^2*(b*c+3*a*f)*x^7+1/8*b^2*(b*d+3*a*g)*x^8+1/9*b^3*e*x^9+1/10*b^3*f*x^10+1/11*b^3*g*x^11+1/12*h*(a+b*x^3)^4/b+a^3*e*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:543
  public void test0099() {
    check( //
        "Integrate[x^2*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3)^2, x]", //
        "4/3*g*x/b^2+5/6*h*x^2/b^2+1/3*(-c-d*x-e*x^2-f*x^3-g*x^4-h*x^5)/(b*(a+b*x^3))+1/9*(b^(1/3)*(b*d-4*a*g)-a^(1/3)*(2*b*e-5*a*h))*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(8/3))-1/18*(b^(1/3)*(b*d-4*a*g)-a^(1/3)*(2*b*e-5*a*h))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(8/3))+1/3*f*Log[a+b*x^3]/b^2-1/3*(b^(4/3)*d+2*a^(1/3)*b*e-4*a*b^(1/3)*g-5*a^(4/3)*h)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(8/3)*Sqrt[3])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:749
  public void test0100() {
    check( //
        "Integrate[Sqrt[2+3*x]/(1-x^2), x]", //
        "-ArcTan[Sqrt[2+3*x]]+ArcTanh[Sqrt[2+3*x]/Sqrt[5]]*Sqrt[5]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:147
  public void test0101() {
    check( //
        "Integrate[(a+b*x)^7/x^12, x]", //
        "-1/11*(a+b*x)^8/(a*x^11)+3/110*b*(a+b*x)^8/(a^2*x^10)-1/165*b^2*(a+b*x)^8/(a^3*x^9)+1/1320*b^3*(a+b*x)^8/(a^4*x^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:255
  public void test0102() {
    check( //
        "Integrate[1/(a+b*x)^7, x]", //
        "(-1/6)/(b*(a+b*x)^6)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:283
  public void test0103() {
    check( //
        "Integrate[(a+b*x)^6/x^10, x]", //
        "-1/9*(a+b*x)^7/(a*x^9)+1/36*b*(a+b*x)^7/(a^2*x^8)-1/252*b^2*(a+b*x)^7/(a^3*x^7)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:313
  public void test0104() {
    check( //
        "Integrate[1/(a+x*Sqrt[a]), x]", //
        "Log[x+Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:851
  public void test0105() {
    check( //
        "Integrate[(a+b*x)*Sqrt[c*x^2]/x^4, x]", //
        "-1/2*(a+b*x)^2*Sqrt[c*x^2]/(a*x^3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:865
  public void test0106() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)/x^2, x]", //
        "1/4*a*c^2*x^3*Sqrt[c*x^2]+1/5*b*c^2*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:899
  public void test0107() {
    check( //
        "Integrate[x^2*(a+b*x)^2*Sqrt[c*x^2], x]", //
        "1/4*a^2*x^3*Sqrt[c*x^2]+2/5*a*b*x^4*Sqrt[c*x^2]+1/6*b^2*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:913
  public void test0108() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^2/x^4, x]", //
        "2*a*b*c*Sqrt[c*x^2]+1/2*b^2*c*x*Sqrt[c*x^2]+a^2*c*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:929
  public void test0109() {
    check( //
        "Integrate[(a+b*x)^2/(x^2*Sqrt[c*x^2]), x]", //
        "-2*a*b/Sqrt[c*x^2]-1/2*a^2/(x*Sqrt[c*x^2])+b^2*x*Log[x]/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:943
  public void test0110() {
    check( //
        "Integrate[(a+b*x)^2/(c*x^2)^(5/2), x]", //
        "-1/4*a^2/(c^2*x^3*Sqrt[c*x^2])-2/3*a*b/(c^2*x^2*Sqrt[c*x^2])-1/2*b^2/(c^2*x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:975
  public void test0111() {
    check( //
        "Integrate[(c*x^2)^(5/2)/(x^6*(a+b*x)), x]", //
        "c^2*Log[x]*Sqrt[c*x^2]/(a*x)-c^2*Log[a+b*x]*Sqrt[c*x^2]/(a*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:991
  public void test0112() {
    check( //
        "Integrate[x^2/((c*x^2)^(3/2)*(a+b*x)), x]", //
        "x*Log[x]/(a*c*Sqrt[c*x^2])-x*Log[a+b*x]/(a*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1009
  public void test0113() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x*(a+b*x)^2), x]", //
        "c*Sqrt[c*x^2]/b^2-a^2*c*Sqrt[c*x^2]/(b^3*x*(a+b*x))-2*a*c*Log[a+b*x]*Sqrt[c*x^2]/(b^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1025
  public void test0114() {
    check( //
        "Integrate[x^5/((c*x^2)^(3/2)*(a+b*x)^2), x]", //
        "x^2/(b^2*c*Sqrt[c*x^2])-a^2*x/(b^3*c*(a+b*x)*Sqrt[c*x^2])-2*a*x*Log[a+b*x]/(b^3*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1046
  public void test0115() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^n/x^3, x]", //
        "c*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b*(1+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1096
  public void test0116() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)^2/(c*x^2)^(1/2), x]", //
        "a^2*x*(d*x)^m/(m*Sqrt[c*x^2])+2*a*b*x*(d*x)^(1+m)/(d*(1+m)*Sqrt[c*x^2])+b^2*x*(d*x)^(2+m)/(d^2*(2+m)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1150
  public void test0117() {
    check( //
        "Integrate[1/((b*c/d+b*x)^3*(c+d*x)^3), x]", //
        "-1/5*d^2/(b^3*(c+d*x)^5)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1220
  public void test0118() {
    check( //
        "Integrate[(1+x)^(1/2)/(1-x)^(5/2), x]", //
        "1/3*(1+x)^(3/2)/(1-x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1266
  public void test0119() {
    check( //
        "Integrate[1/((1-x)^(5/2)*(1+x)^(1/2)), x]", //
        "1/3*Sqrt[1+x]/(1-x)^(3/2)+1/3*Sqrt[1+x]/Sqrt[1-x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1296
  public void test0120() {
    check( //
        "Integrate[1/((a+a*x)^(1/2)*(c-c*x)^(1/2)), x]", //
        "2*ArcTan[Sqrt[c]*Sqrt[a+a*x]/(Sqrt[a]*Sqrt[c-c*x])]/(Sqrt[a]*Sqrt[c])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1346
  public void test0121() {
    check( //
        "Integrate[1/((a-I*a*x)^(15/4)*(a+I*a*x)^(1/4)), x]", //
        "-2/11*I*(a+I*a*x)^(3/4)/(a^2*(a-I*a*x)^(11/4))-8/77*I*(a+I*a*x)^(3/4)/(a^3*(a-I*a*x)^(7/4))-16/231*I*(a+I*a*x)^(3/4)/(a^4*(a-I*a*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1499
  public void test0122() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^16, x]", //
        "-1/15*(c+d*x)^11/((b*c-a*d)*(a+b*x)^15)+2/105*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^14)-2/455*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^13)+1/1365*d^3*(c+d*x)^11/((b*c-a*d)^4*(a+b*x)^12)-1/15015*d^4*(c+d*x)^11/((b*c-a*d)^5*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1640
  public void test0123() {
    check( //
        "Integrate[Sqrt[-1+x]/(1+x)^2, x]", //
        "ArcTan[Sqrt[-1+x]/Sqrt[2]]/Sqrt[2]-Sqrt[-1+x]/(1+x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1670
  public void test0124() {
    check( //
        "Integrate[1/(c+d*x)^(5/2), x]", //
        "(-2/3)/(d*(c+d*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1704
  public void test0125() {
    check( //
        "Integrate[(c+d*x)^(1/2)/(a+b*x)^(7/2), x]", //
        "-2/5*(c+d*x)^(3/2)/((b*c-a*d)*(a+b*x)^(5/2))+4/15*d*(c+d*x)^(3/2)/((b*c-a*d)^2*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1748
  public void test0126() {
    check( //
        "Integrate[1/((a+b*x)^(7/2)*(c+d*x)^(3/2)), x]", //
        "(-2/5)/((b*c-a*d)*(a+b*x)^(5/2)*Sqrt[c+d*x])+4/5*d/((b*c-a*d)^2*(a+b*x)^(3/2)*Sqrt[c+d*x])-16/5*d^2/((b*c-a*d)^3*Sqrt[a+b*x]*Sqrt[c+d*x])-32/5*d^3*Sqrt[a+b*x]/((b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1844
  public void test0127() {
    check( //
        "Integrate[1/((a+b*x)^(14/3)*(c+d*x)^(1/3)), x]", //
        "-3/11*(c+d*x)^(2/3)/((b*c-a*d)*(a+b*x)^(11/3))+27/88*d*(c+d*x)^(2/3)/((b*c-a*d)^2*(a+b*x)^(8/3))-81/220*d^2*(c+d*x)^(2/3)/((b*c-a*d)^3*(a+b*x)^(5/3))+243/440*d^3*(c+d*x)^(2/3)/((b*c-a*d)^4*(a+b*x)^(2/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1858
  public void test0128() {
    check( //
        "Integrate[1/((a+b*x)^(13/3)*(c+d*x)^(2/3)), x]", //
        "-3/10*(c+d*x)^(1/3)/((b*c-a*d)*(a+b*x)^(10/3))+27/70*d*(c+d*x)^(1/3)/((b*c-a*d)^2*(a+b*x)^(7/3))-81/140*d^2*(c+d*x)^(1/3)/((b*c-a*d)^3*(a+b*x)^(4/3))+243/140*d^3*(c+d*x)^(1/3)/((b*c-a*d)^4*(a+b*x)^(1/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1872
  public void test0129() {
    check( //
        "Integrate[1/((a+b*x)^(11/3)*(c+d*x)^(4/3)), x]", //
        "(-3/8)/((b*c-a*d)*(a+b*x)^(8/3)*(c+d*x)^(1/3))+27/40*d/((b*c-a*d)^2*(a+b*x)^(5/3)*(c+d*x)^(1/3))-81/40*d^2/((b*c-a*d)^3*(a+b*x)^(2/3)*(c+d*x)^(1/3))-243/40*d^3*(a+b*x)^(1/3)/((b*c-a*d)^4*(c+d*x)^(1/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2123
  public void test0130() {
    check( //
        "Integrate[(a+b*x)^m*(a+b*(2+m)*x), x]", //
        "x*(a+b*x)^(1+m)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2150
  public void test0131() {
    check( //
        "Integrate[(a+b*x)^(-5-n)*(c+d*x)^n, x]", //
        "-(a+b*x)^(-4-n)*(c+d*x)^(1+n)/((b*c-a*d)*(4+n))+3*d*(a+b*x)^(-3-n)*(c+d*x)^(1+n)/((b*c-a*d)^2*(3+n)*(4+n))-6*d^2*(a+b*x)^(-2-n)*(c+d*x)^(1+n)/((b*c-a*d)^3*(2+n)*(3+n)*(4+n))+6*d^3*(a+b*x)^(-1-n)*(c+d*x)^(1+n)/((b*c-a*d)^4*(1+n)*(2+n)*(3+n)*(4+n))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2181
  public void test0132() {
    check( //
        "Integrate[2*x+5*x^2, x]", //
        "x^2+5/3*x^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2197
  public void test0133() {
    check( //
        "Integrate[x^(5/6)-x^3, x]", //
        "6/11*x^(11/6)-1/4*x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:20
  public void test0134() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^3, x]", //
        "-1/2*c^3*(a-b*x)^4/x^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:34
  public void test0135() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^4, x]", //
        "-1/3*a^5*c^4/x^3+3/2*a^4*b*c^4/x^2-2*a^3*b^2*c^4/x-3*a*b^4*c^4*x+1/2*b^5*c^4*x^2+2*a^2*b^3*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:48
  public void test0136() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^4, x]", //
        "-1/3*c^5*(a-b*x)^6/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:62
  public void test0137() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^6/x^13, x]", //
        "-1/12*a^7*c^6/x^12+5/11*a^6*b*c^6/x^11-9/10*a^5*b^2*c^6/x^10+5/9*a^4*b^3*c^6/x^9+5/8*a^3*b^4*c^6/x^8-9/7*a^2*b^5*c^6/x^7+5/6*a*b^6*c^6/x^6-1/5*b^7*c^6/x^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:162
  public void test0138() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^9, x]", //
        "-1/8*A*(a+b*x)^6/(a*x^8)+1/28*(A*b-4*a*B)*(a+b*x)^6/(a^2*x^7)-1/168*b*(A*b-4*a*B)*(a+b*x)^6/(a^3*x^6)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:190
  public void test0139() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^14, x]", //
        "-1/13*A*(a+b*x)^11/(a*x^13)+1/156*(2*A*b-13*a*B)*(a+b*x)^11/(a^2*x^12)-1/1716*b*(2*A*b-13*a*B)*(a+b*x)^11/(a^3*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:451
  public void test0140() {
    check( //
        "Integrate[x^(7/2)*(A+B*x)/(a+b*x), x]", //
        "2/3*a^2*(A*b-a*B)*x^(3/2)/b^4-2/5*a*(A*b-a*B)*x^(5/2)/b^3+2/7*(A*b-a*B)*x^(7/2)/b^2+2/9*B*x^(9/2)/b+2*a^(7/2)*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/b^(11/2)-2*a^3*(A*b-a*B)*Sqrt[x]/b^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:651
  public void test0141() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/x^(13/2), x]", //
        "-2/11*A*(a+b*x)^(7/2)/(a*x^(11/2))+2/99*(4*A*b-11*a*B)*(a+b*x)^(7/2)/(a^2*x^(9/2))-4/693*b*(4*A*b-11*a*B)*(a+b*x)^(7/2)/(a^3*x^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:667
  public void test0142() {
    check( //
        "Integrate[(A+B*x)/(x^(13/2)*Sqrt[a+b*x]), x]", //
        "-2/11*A*Sqrt[a+b*x]/(a*x^(11/2))+2/99*(10*A*b-11*a*B)*Sqrt[a+b*x]/(a^2*x^(9/2))-16/693*b*(10*A*b-11*a*B)*Sqrt[a+b*x]/(a^3*x^(7/2))+32/1155*b^2*(10*A*b-11*a*B)*Sqrt[a+b*x]/(a^4*x^(5/2))-128/3465*b^3*(10*A*b-11*a*B)*Sqrt[a+b*x]/(a^5*x^(3/2))+256/3465*b^4*(10*A*b-11*a*B)*Sqrt[a+b*x]/(a^6*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:731
  public void test0143() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x*Sqrt[c+d*x]), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/Sqrt[c]+2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]/Sqrt[d]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:777
  public void test0144() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^4, x]", //
        "-1/3*(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^3+d^(3/2)*(5*b*c+3*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]+1/8*(b^3*c^3-15*a*b^2*c^2*d-45*a^2*b*c*d^2-5*a^3*d^3)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*Sqrt[c])-1/24*(3*b^2*c/a+40*b*d+5*a*d^2/c)*(c+d*x)^(3/2)*Sqrt[a+b*x]/x-1/12*(3*b*c+5*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c*x^2)+1/8*d*(b^2*c^2+26*a*b*c*d+5*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:825
  public void test0145() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^6, x]", //
        "-1/8*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c^2*x^4)-1/5*(a+b*x)^(5/2)*(c+d*x)^(5/2)/(c*x^5)-3/128*(b*c-a*d)^5*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(7/2))-1/64*(b*c-a*d)^3*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^3*x^2)-1/16*(b*c-a*d)^2*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c^3*x^3)+3/128*(b*c-a*d)^4*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:841
  public void test0146() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^2*Sqrt[c+d*x]), x]", //
        "-a^(3/2)*(5*b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/c^(3/2)-b^(3/2)*(b*c-5*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/d^(3/2)-a*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c*x)+b*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*d)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:887
  public void test0147() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^4*Sqrt[a+b*x]), x]", //
        "5/8*(b*c-a*d)^3*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*Sqrt[c])+5/12*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*x^2)-1/3*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*x^3)-5/8*(b*c-a*d)^2*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:903
  public void test0148() {
    check( //
        "Integrate[x^2/(Sqrt[a+b*x]*Sqrt[c+d*x]), x]", //
        "-1/4*(4*a*b*c*d-3*(b*c+a*d)^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(5/2))-3/4*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^2*d^2)+1/2*x*Sqrt[a+b*x]*Sqrt[c+d*x]/(b*d)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:917
  public void test0149() {
    check( //
        "Integrate[x^4/((c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "1/4*(35*b^2*c^2+10*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(9/2))-2/3*c*x^3*Sqrt[a+b*x]/(d*(b*c-a*d)*(c+d*x)^(3/2))-2/3*c*(7*b*c-9*a*d)*x^2*Sqrt[a+b*x]/(d^2*(b*c-a*d)^2*Sqrt[c+d*x])-1/12*(105*b^3*c^3-145*a*b^2*c^2*d+15*a^2*b*c*d^2+9*a^3*d^3-2*b*d*(35*b^2*c^2-46*a*b*c*d+3*a^2*d^2)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^2*d^4*(b*c-a*d)^2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:951
  public void test0150() {
    check( //
        "Integrate[x^4/((a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "3/4*(5*b^2*c^2+6*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(7/2)*d^(7/2))+2*a*x^3/(b*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])-2*c*(b*c+a*d)*x^2*Sqrt[a+b*x]/(b*d*(b*c-a*d)^2*Sqrt[c+d*x])-1/4*((b*c+a*d)*(15*b^2*c^2-22*a*b*c*d+15*a^2*d^2)-2*b*d*(5*b^2*c^2-2*a*b*c*d+5*a^2*d^2)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^3*d^3*(b*c-a*d)^2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:965
  public void test0151() {
    check( //
        "Integrate[1/(x*(a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(5/2))+2*b/(a*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])+2/3*d*(3*b*c+a*d)*Sqrt[a+b*x]/(a*c*(b*c-a*d)^2*(c+d*x)^(3/2))+2/3*d*(3*b*c-a*d)*(b*c+3*a*d)*Sqrt[a+b*x]/(a*c^2*(b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:985
  public void test0152() {
    check( //
        "Integrate[x^6/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "2/3*a*x^5/(b*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+5/4*(7*b^2*c^2+10*a*b*c*d+7*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(9/2)*d^(9/2))+2/3*a*(13*b*c-7*a*d)*x^4/(b^2*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])-2/3*c*(b^2*c^2+14*a*b*c*d-7*a^2*d^2)*x^3*Sqrt[a+b*x]/(b^2*d*(b*c-a*d)^3*(c+d*x)^(3/2))-2/3*c*(b*c+a*d)*(7*b^2*c^2-22*a*b*c*d+7*a^2*d^2)*x^2*Sqrt[a+b*x]/(b^2*d^2*(b*c-a*d)^4*Sqrt[c+d*x])-1/12*((b*c+a*d)*(105*b^4*c^4-340*a*b^3*c^3*d+406*a^2*b^2*c^2*d^2-340*a^3*b*c*d^3+105*a^4*d^4)-2*b*d*(35*b^4*c^4-76*a*b^3*c^3*d+18*a^2*b^2*c^2*d^2-76*a^3*b*c*d^3+35*a^4*d^4)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^4*d^4*(b*c-a*d)^4)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1105
  public void test0153() {
    check( //
        "Integrate[1/(x*(a+b*x)^(3/4)*(c+d*x)^(1/4)), x]", //
        "-2*ArcTan[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(3/4)*c^(1/4))-2*ArcTanh[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(3/4)*c^(1/4))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1354
  public void test0154() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^4/(a+b*x)^2, x]", //
        "2*e*(b*d-a*e)^2*(2*b*B*d+3*A*b*e-5*a*B*e)*x/b^5-(A*b-a*B)*(b*d-a*e)^4/(b^6*(a+b*x))+e^2*(b*d-a*e)*(3*b*B*d+2*A*b*e-5*a*B*e)*(a+b*x)^2/b^6+1/3*e^3*(4*b*B*d+A*b*e-5*a*B*e)*(a+b*x)^3/b^6+1/4*B*e^4*(a+b*x)^4/b^6+(b*d-a*e)^3*(b*B*d+4*A*b*e-5*a*B*e)*Log[a+b*x]/b^6");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2079
  public void test0155() {
    check( //
        "Integrate[(3+5*x)*Sqrt[1-2*x], x]", //
        "-11/6*(1-2*x)^(3/2)+1/2*(1-2*x)^(5/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2581
  public void test0156() {
    check( //
        "Integrate[(A+B*x)/((a+b*x)^(5/2)*(d+e*x)^(3/2)), x]", //
        "-2*(B*d-A*e)/(e*(b*d-a*e)*(a+b*x)^(3/2)*Sqrt[d+e*x])+2/3*(3*b*B*d-4*A*b*e+a*B*e)*Sqrt[d+e*x]/(e*(b*d-a*e)^2*(a+b*x)^(3/2))-4/3*(3*b*B*d-4*A*b*e+a*B*e)*Sqrt[d+e*x]/((b*d-a*e)^3*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2599
  public void test0157() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^6, x]", //
        "-5591773/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/15*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+37/840*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+403/1680*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+14023/9408*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+1466281/131712*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2643
  public void test0158() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2)), x]", //
        "2*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-2*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2675
  public void test0159() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^2, x]", //
        "-1/3*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)+37/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+1649/108*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-1/3*(3+5*x)^(3/2)*Sqrt[1-2*x]+107/36*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2689
  public void test0160() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4, x]", //
        "-1/9*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^3-326717/13608*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-575/243*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+331/168*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)+181/108*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^2-39745/4536*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2705
  public void test0161() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^6*Sqrt[3+5*x]), x]", //
        "-333216939/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/15*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+293/120*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+23909/1680*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+835409/9408*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+87374783/131712*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2719
  public void test0162() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)*(3+5*x)^(5/2)), x]", //
        "-2/3*(1-2*x)^(3/2)/(3+5*x)^(3/2)-14*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+14*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2737
  public void test0163() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^7, x]", //
        "-391280725/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/18*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^6+1/12*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^5+647/864*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+151621/36288*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+26486645/1016064*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+2770202075/14224896*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2795
  public void test0164() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^3*(3+5*x)^(5/2)), x]", //
        "-13145/84*(1-2*x)^(3/2)/(3+5*x)^(3/2)+3/14*(1-2*x)^(7/2)/((2+3*x)^2*(3+5*x)^(3/2))+239/28*(1-2*x)^(5/2)/((2+3*x)*(3+5*x)^(3/2))-13145/4*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+13145/4*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2827
  public void test0165() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)*Sqrt[1-2*x]), x]", //
        "3035/432*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+2/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-5/12*(3+5*x)^(3/2)*Sqrt[1-2*x]-455/144*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2843
  public void test0166() {
    check( //
        "Integrate[1/((2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-3827/196*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/14*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+333/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2857
  public void test0167() {
    check( //
        "Integrate[(2+3*x)^2/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "9/25*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-2/825*Sqrt[1-2*x]/(3+5*x)^(3/2)-404/9075*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2875
  public void test0168() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)), x]", //
        "2/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2889
  public void test0169() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^5), x]", //
        "-279015/153664*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/7*Sqrt[3+5*x]/((2+3*x)^4*Sqrt[1-2*x])-131/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-89/392*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3-745/10976*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+16985/153664*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2905
  public void test0170() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-56421/800*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^2*Sqrt[3+5*x]/Sqrt[1-2*x]+3/8800*(25003+10380*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2937
  public void test0171() {
    check( //
        "Integrate[(2+3*x)^4*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "13246251/6400*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1/3*(2+3*x)^4*Sqrt[3+5*x]/(1-2*x)^(3/2)-299/66*(2+3*x)^3*Sqrt[3+5*x]/Sqrt[1-2*x]-697/88*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-1/70400*(17606479+7306140*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2951
  public void test0172() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)), x]", //
        "2/21*(3+5*x)^(3/2)/(1-2*x)^(3/2)-2/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-2/49*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2967
  public void test0173() {
    check( //
        "Integrate[(2+3*x)^5/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "8261577/6400*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/33*(2+3*x)^4*Sqrt[3+5*x]/(1-2*x)^(3/2)-2051/726*(2+3*x)^3*Sqrt[3+5*x]/Sqrt[1-2*x]-23909/4840*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-1/774400*(120791143+50124540*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2981
  public void test0174() {
    check( //
        "Integrate[(2+3*x)/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "(-2/55)/((1-2*x)^(3/2)*Sqrt[3+5*x])+82/1815*Sqrt[3+5*x]/(1-2*x)^(3/2)+164/3993*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2995
  public void test0175() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^3*(3+5*x)^(5/2)), x]", //
        "(-3715/3234)/((1-2*x)^(3/2)*(3+5*x)^(3/2))+3/14/((1-2*x)^(3/2)*(2+3*x)^2*(3+5*x)^(3/2))+111/28/((1-2*x)^(3/2)*(2+3*x)*(3+5*x)^(3/2))-538245/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-40765/83006)/((3+5*x)^(3/2)*Sqrt[1-2*x])-34551425/5478396*Sqrt[1-2*x]/(3+5*x)^(3/2)+3443814775/60262356*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3017
  public void test0176() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2), x]", //
        "-74/63*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+4/63*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/9*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+74/63*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3031
  public void test0177() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x], x]", //
        "-488149/42525*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-29357/85050*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-223/945*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-31/945*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+2/45*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-29357/17010*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3061
  public void test0178() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "-169/625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-496/625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-326/825*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+458/1375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3079
  public void test0179() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(9/2), x]", //
        "-595324/46305*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-18016/46305*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/21*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(7/2)+82/315*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+8516/6615*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+595324/46305*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3093
  public void test0180() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x], x]", //
        "-97540001/1275750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2930159/1275750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/55*(1-2*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[2+3*x]-22576/155925*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2377/155925*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+194/7425*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2930159/2806650*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3109
  public void test0181() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-124/9*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4/9*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/9*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+124/9*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3123
  public void test0182() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(3/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(3/2)*(2+3*x)^(3/2)/(3+5*x)^(3/2)-582/625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+496/625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-62/25*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+178/125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3141
  public void test0183() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(11/2), x]", //
        "-7810384/83349*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-234856/83349*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/27*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(9/2)+10/63*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(7/2)+832/567*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+112436/11907*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+7810384/83349*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3155
  public void test0184() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[2+3*x], x]", //
        "-8120161139/124385625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-486785077/248771250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+326/10725*(1-2*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[2+3*x]+2/65*(1-2*x)^(5/2)*(3+5*x)^(7/2)*Sqrt[2+3*x]-3872003/30405375*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-121031/30405375*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+2314/111375*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-486785077/547296750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3171
  public void test0185() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-8314/675*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+824/675*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/Sqrt[2+3*x]+428/135*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3185
  public void test0186() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(9/2)*(3+5*x)^(3/2)), x]", //
        "1959032/147*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+58928/147*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*(1-2*x)^(3/2)/((2+3*x)^(7/2)*Sqrt[3+5*x])+104/9*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+2332/21*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+324104/147*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-9795160/441*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3217
  public void test0187() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[1-2*x], x]", //
        "-8256877/56700*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-62092/14175*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/9*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-1877/630*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-3/7*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-62092/2835*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3263
  public void test0188() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-46159/6875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2281/6875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/165*(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-536/9075*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-487/15125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3301
  public void test0189() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(3/2)), x]", //
        "31/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+4/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])-31/49*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3317
  public void test0190() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "168123/1250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+5057/1250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*(2+3*x)^(5/2)*Sqrt[3+5*x]/Sqrt[1-2*x]+312/275*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+14517/2750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3331
  public void test0191() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "4636/539*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+124/539*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77/(Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x])+186/539*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-23180/5929*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3349
  public void test0192() {
    check( //
        "Integrate[(2+3*x)^(7/2)*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "-1289089/500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-9694/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*(2+3*x)^(7/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-133/33*(2+3*x)^(5/2)*Sqrt[3+5*x]/Sqrt[1-2*x]-797/110*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-18551/550*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3363
  public void test0193() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^(5/2)), x]", //
        "-582/2401*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+496/2401*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(3/2))+58/147*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])-89/343*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-496/2401*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3379
  public void test0194() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-31/11*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1/11*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)-62/363*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3393
  public void test0195() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "22738708/290521*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+673072/290521*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231/((1-2*x)^(3/2)*(2+3*x)^(3/2)*Sqrt[3+5*x])+1352/17787/((2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x])+694/41503*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+336536/290521*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-113693540/9587193*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3606
  public void test0196() {
    check( //
        "Integrate[(a+b*x)^m*(c+d*x)^(-1-d*(b*e-a*f)*(1+m)/(b*(d*e-c*f)))*(e+f*x)^(-1+(b*c-a*d)*f*(1+m)/(b*(d*e-c*f))), x]", //
        "b*(a+b*x)^(1+m)*(e+f*x)^((b*c-a*d)*f*(1+m)/(b*(d*e-c*f)))/((b*c-a*d)*(b*e-a*f)*(1+m)*(c+d*x)^(d*(b*e-a*f)*(1+m)/(b*(d*e-c*f))))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:881
  public void test0197() {
    check( //
        "Integrate[(a-b*x^2)^(7/4), x]", //
        "14/45*a*x*(a-b*x^2)^(3/4)+2/9*x*(a-b*x^2)^(7/4)+14/15*a^(5/2)*(1-b*x^2/a)^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1071
  public void test0198() {
    check( //
        "Integrate[1/((c*x)^(7/2)*(a+b*x^2)^(5/4)), x]", //
        "(-2/5)/(a*c*(c*x)^(5/2)*(a+b*x^2)^(1/4))+12/5*b/(a^2*c^3*(a+b*x^2)^(1/4)*Sqrt[c*x])-24/5*b^(3/2)*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[c*x]/(a^(5/2)*c^4*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:28
  public void test0199() {
    check( //
        "Integrate[(a+b*x^2)^3/(c+d*x^2)^3, x]", //
        "b^3*x/d^3-1/4*(b*c-a*d)^3*x/(c*d^3*(c+d*x^2)^2)+3/8*(b*c-a*d)^2*(3*b*c+a*d)*x/(c^2*d^3*(c+d*x^2))-3/8*(b*c-a*d)*(4*b^2*c^2+(b*c+a*d)^2)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(c^(5/2)*d^(7/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:62
  public void test0200() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)*(c+d*x^2), x]", //
        "1/4*d*x*(a+b*x^2)^(3/2)/b+1/8*a*(4*b*c-a*d)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)+1/8*(4*b*c-a*d)*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:121
  public void test0201() {
    check( //
        "Integrate[(c+d*x^2)^(3/2)/(a+b*x^2)^4, x]", //
        "1/24*(5*b*c-6*a*d)*x*(c+d*x^2)^(3/2)/(a^2*(b*c-a*d)*(a+b*x^2)^2)+1/6*b*x*(c+d*x^2)^(5/2)/(a*(b*c-a*d)*(a+b*x^2)^3)+1/16*c^2*(5*b*c-6*a*d)*ArcTan[x*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^2])]/(a^(7/2)*(b*c-a*d)^(3/2))+1/16*c*(5*b*c-6*a*d)*x*Sqrt[c+d*x^2]/(a^3*(b*c-a*d)*(a+b*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:226
  public void test0202() {
    check( //
        "Integrate[Sqrt[1-x^2]/Sqrt[1+x^2], x]", //
        "-EllipticE[ArcSin[x],-1]+2*EllipticF[ArcSin[x],-1]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:299
  public void test0203() {
    check( //
        "Integrate[Sqrt[a-b*x^2]/Sqrt[c-d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],b*c/(a*d)]*Sqrt[c]*Sqrt[a-b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1-b*x^2/a]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:313
  public void test0204() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/Sqrt[-a+b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[c+d*x^2]/(Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[1+d*x^2/c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:23
  public void test0205() {
    check( //
        "Integrate[x*(a+b*x^2)^2*(A+B*x^2), x]", //
        "1/6*(A*b-a*B)*(a+b*x^2)^3/b^2+1/8*B*(a+b*x^2)^4/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:51
  public void test0206() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^8, x]", //
        "-1/7*a^5*A/x^7-1/5*a^4*(5*A*b+a*B)/x^5-5/3*a^3*b*(2*A*b+a*B)/x^3-10*a^2*b^2*(A*b+a*B)/x+5*a*b^3*(A*b+2*a*B)*x+1/3*b^4*(A*b+5*a*B)*x^3+1/5*b^5*B*x^5");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:65
  public void test0207() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^22, x]", //
        "-1/21*a^5*A/x^21-1/19*a^4*(5*A*b+a*B)/x^19-5/17*a^3*b*(2*A*b+a*B)/x^17-2/3*a^2*b^2*(A*b+a*B)/x^15-5/13*a*b^3*(A*b+2*a*B)/x^13-1/11*b^4*(A*b+5*a*B)/x^11-1/9*b^5*B/x^9");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:81
  public void test0208() {
    check( //
        "Integrate[(A+B*x^2)/(x^6*(a+b*x^2)), x]", //
        "-1/5*A/(a*x^5)+1/3*(A*b-a*B)/(a^2*x^3)-b*(A*b-a*B)/(a^3*x)-b^(3/2)*(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/a^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:95
  public void test0209() {
    check( //
        "Integrate[(A+B*x^2)/(x^2*(a+b*x^2)^2), x]", //
        "-A/(a^2*x)-1/2*(A*b-a*B)*x/(a^2*(a+b*x^2))-1/2*(3*A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:151
  public void test0210() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(x*(a+b*x^2)^3), x]", //
        "1/2*c/(a*(a+b*x^2))+c*Log[x]/a^2-1/2*c*Log[a+b*x^2]/a^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:183
  public void test0211() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^3, x]", //
        "-1/2*a^2*c^3/x^2+1/2*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^2+1/4*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^4+1/6*b*d^2*(3*b*c+2*a*d)*x^6+1/8*b^2*d^3*x^8+a*c^2*(2*b*c+3*a*d)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:199
  public void test0212() {
    check( //
        "Integrate[x^4*(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "1/2*(7*b*c-3*a*d)*(b*c-a*d)*x/d^4-1/6*(7*b*c-3*a*d)*(b*c-a*d)*x^3/(c*d^3)+1/5*b^2*x^5/d^2+1/2*(b*c-a*d)^2*x^5/(c*d^2*(c+d*x^2))-1/2*(7*b*c-3*a*d)*(b*c-a*d)*ArcTan[x*Sqrt[d]/Sqrt[c]]*Sqrt[c]/d^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:247
  public void test0213() {
    check( //
        "Integrate[x*(c+d*x^2)^3/(a+b*x^2), x]", //
        "1/2*d*(b*c-a*d)^2*x^2/b^3+1/4*(b*c-a*d)*(c+d*x^2)^2/b^2+1/6*(c+d*x^2)^3/b+1/2*(b*c-a*d)^3*Log[a+b*x^2]/b^4");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:263
  public void test0214() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)*(c+d*x^2)), x]", //
        "(-1/2)/(a*c*x^2)-(b*c+a*d)*Log[x]/(a^2*c^2)+1/2*b^2*Log[a+b*x^2]/(a^2*(b*c-a*d))-1/2*d^2*Log[c+d*x^2]/(c^2*(b*c-a*d))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:295
  public void test0215() {
    check( //
        "Integrate[x^2*(c+d*x^2)/(a+b*x^2)^2, x]", //
        "d*x/b^2-1/2*(b*c-a*d)*x/(b^2*(a+b*x^2))+1/2*(b*c-3*a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(5/2)*Sqrt[a])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:325
  public void test0216() {
    check( //
        "Integrate[x/((a+b*x^2)^2*(c+d*x^2)), x]", //
        "(-1/2)/((b*c-a*d)*(a+b*x^2))-1/2*d*Log[a+b*x^2]/(b*c-a*d)^2+1/2*d*Log[c+d*x^2]/(b*c-a*d)^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:339
  public void test0217() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^2*(c+d*x^2)^2), x]", //
        "1/2*b^2/(a*(b*c-a*d)^2*(a+b*x^2))+1/2*d^2/(c*(b*c-a*d)^2*(c+d*x^2))+Log[x]/(a^2*c^2)-1/2*b^2*(b*c-3*a*d)*Log[a+b*x^2]/(a^2*(b*c-a*d)^3)-1/2*d^2*(3*b*c-a*d)*Log[c+d*x^2]/(c^2*(b*c-a*d)^3)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:413
  public void test0218() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x^2)/x^(7/2), x]", //
        "-2/5*a^3*A/x^(5/2)+2*a*b*(A*b+a*B)*x^(3/2)+2/7*b^2*(A*b+3*a*B)*x^(7/2)+2/11*b^3*B*x^(11/2)-2*a^2*(3*A*b+a*B)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:609
  public void test0219() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)*(A+B*x^2)/x^8, x]", //
        "-1/7*A*(a+b*x^2)^(5/2)/(a*x^7)+1/35*(2*A*b-7*a*B)*(a+b*x^2)^(5/2)/(a^2*x^5)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:639
  public void test0220() {
    check( //
        "Integrate[(A+B*x^2)/(x^2*Sqrt[a+b*x^2]), x]", //
        "B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]-A*Sqrt[a+b*x^2]/(a*x)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:713
  public void test0221() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^(5/2)/x^4, x]", //
        "5/24*(b^2*c^2+4*a*d*(3*b*c+2*a*d))*x*(c+d*x^2)^(3/2)/c+1/6*(b^2*c^2+4*a*d*(3*b*c+2*a*d))*x*(c+d*x^2)^(5/2)/c^2-1/3*a^2*(c+d*x^2)^(7/2)/(c*x^3)-2/3*a*(3*b*c+2*a*d)*(c+d*x^2)^(7/2)/(c^2*x)+5/16*c*(b^2*c^2+4*a*d*(3*b*c+2*a*d))*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/Sqrt[d]+5/16*(b^2*c^2+4*a*d*(3*b*c+2*a*d))*x*Sqrt[c+d*x^2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:729
  public void test0222() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^6*Sqrt[c+d*x^2]), x]", //
        "-1/5*a^2*Sqrt[c+d*x^2]/(c*x^5)-2/15*a*(5*b*c-2*a*d)*Sqrt[c+d*x^2]/(c^2*x^3)-1/15*(15*b^2*c^2-4*a*d*(5*b*c-2*a*d))*Sqrt[c+d*x^2]/(c^3*x)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:761
  public void test0223() {
    check( //
        "Integrate[1/(x*(a+b*x^2)*Sqrt[d*x^2]), x]", //
        "(-1)/(a*Sqrt[d*x^2])-x*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/(a^(3/2)*Sqrt[d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:779
  public void test0224() {
    check( //
        "Integrate[x*(c+d*x^2)^(3/2)/(a+b*x^2), x]", //
        "1/3*(c+d*x^2)^(3/2)/b-(b*c-a*d)^(3/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/b^(5/2)+(b*c-a*d)*Sqrt[c+d*x^2]/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:809
  public void test0225() {
    check( //
        "Integrate[x/((a+b*x^2)*(c+d*x^2)^(3/2)), x]", //
        "-ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]*Sqrt[b]/(b*c-a*d)^(3/2)+1/((b*c-a*d)*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1084
  public void test0226() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(x^5*Sqrt[c+d*x^2]), x]", //
        "1/8*(b*c-a*d)*(b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[a]*Sqrt[c+d*x^2])]/(a^(3/2)*c^(5/2))-1/4*(a+b*x^2)^(3/2)*Sqrt[c+d*x^2]/(a*c*x^4)+1/8*(b*c+3*a*d)*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(a*c^2*x^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1128
  public void test0227() {
    check( //
        "Integrate[x^3/((a+b*x^2)^(3/2)*Sqrt[c+d*x^2]), x]", //
        "ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]/(b^(3/2)*Sqrt[d])+a*Sqrt[c+d*x^2]/(b*(b*c-a*d)*Sqrt[a+b*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1142
  public void test0228() {
    check( //
        "Integrate[x^2/(Sqrt[1-x^2]*Sqrt[2+3*x^2]), x]", //
        "1/3*EllipticE[ArcSin[x],-3/2]*Sqrt[2]-1/3*EllipticF[ArcSin[x],-3/2]*Sqrt[2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1328
  public void test0229() {
    check( //
        "Integrate[(e*x)^(1/2)*(c+d*x^2)/(a+b*x^2)^(9/4), x]", //
        "2/5*(b*c-a*d)*(e*x)^(3/2)/(a*b*e*(a+b*x^2)^(5/4))-2/5*(2*b*c+3*a*d)*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[e*x]/(a^(3/2)*b^(3/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:58
  public void test0230() {
    check( //
        "Integrate[(a+b*x^2)/((c+d*x^2)^(3/2)*(e+f*x^2)^(1/2)), x]", //
        "(b*e-a*f)*EllipticF[ArcTan[x*Sqrt[f]/Sqrt[e]],1-d*e/(c*f)]*Sqrt[e]*Sqrt[c+d*x^2]/(c*(d*e-c*f)*Sqrt[f]*Sqrt[e*(c+d*x^2)/(c*(e+f*x^2))]*Sqrt[e+f*x^2])-(b*c-a*d)*EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-c*f/(d*e)]*Sqrt[e+f*x^2]/((d*e-c*f)*Sqrt[c]*Sqrt[d]*Sqrt[c+d*x^2]*Sqrt[c*(e+f*x^2)/(e*(c+d*x^2))])");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:72
  public void test0231() {
    check( //
        "Integrate[(a+b*x^2)*Sqrt[2+d*x^2]/Sqrt[3+f*x^2], x]", //
        "-1/3*(6*b*d-2*b*f-3*a*d*f)*x*Sqrt[2+d*x^2]/(d*f*Sqrt[3+f*x^2])+1/3*(6*b*d-2*b*f-3*a*d*f)*EllipticE[ArcTan[x*Sqrt[f]/Sqrt[3]],1-3/2*d/f]*Sqrt[2]*Sqrt[2+d*x^2]/(d*f^(3/2)*Sqrt[(2+d*x^2)/(3+f*x^2)]*Sqrt[3+f*x^2])-(b-a*f)*EllipticF[ArcTan[x*Sqrt[f]/Sqrt[3]],1-3/2*d/f]*Sqrt[2]*Sqrt[2+d*x^2]/(f^(3/2)*Sqrt[(2+d*x^2)/(3+f*x^2)]*Sqrt[3+f*x^2])+1/3*b*x*Sqrt[2+d*x^2]*Sqrt[3+f*x^2]/f");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:172
  public void test0232() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/((a+b*x^2)^(1/2)*(e+f*x^2)^(3/2)), x]", //
        "c^(3/2)*EllipticF[ArcTan[x*Sqrt[d*e-c*f]/(Sqrt[c]*Sqrt[e+f*x^2])],-(b*c-a*d)*e/(a*(d*e-c*f))]*Sqrt[a+b*x^2]/(a*e*Sqrt[d*e-c*f]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])-EllipticE[ArcTan[x*Sqrt[d*e-c*f]/(Sqrt[c]*Sqrt[e+f*x^2])],-(b*c-a*d)*e/(a*(d*e-c*f))]*Sqrt[c]*Sqrt[d*e-c*f]*Sqrt[a+b*x^2]/(e*(b*e-a*f)*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])+(d*e-c*f)*x*Sqrt[a+b*x^2]/(e*(b*e-a*f)*Sqrt[c+d*x^2]*Sqrt[e+f*x^2])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:26
  public void test0233() {
    check( //
        "Integrate[(A+B*x)*(a+b*x^2)^(3/2)/x^2, x]", //
        "-1/3*(3*A-B*x)*(a+b*x^2)^(3/2)/x-a^(3/2)*B*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]+3/2*a*A*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]*Sqrt[b]+1/2*(2*a*B+3*A*b*x)*Sqrt[a+b*x^2]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:42
  public void test0234() {
    check( //
        "Integrate[(A+B*x)/(x^2*Sqrt[a+b*x^2]), x]", //
        "-B*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/Sqrt[a]-A*Sqrt[a+b*x^2]/(a*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:126
  public void test0235() {
    check( //
        "Integrate[1/(b*x)^(3/2), x]", //
        "(-2)/(b*Sqrt[b*x])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:547
  public void test0236() {
    check( //
        "Integrate[1/(x*Sqrt[-1+x^3]), x]", //
        "2/3*ArcTan[Sqrt[-1+x^3]]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1514
  public void test0237() {
    check( //
        "Integrate[1/(x*(1-x^6)), x]", //
        "Log[x]-1/6*Log[1-x^6]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:148
  public void test0238() {
    check( //
        "Integrate[(a+b*x)^7/x^13, x]", //
        "-1/12*(a+b*x)^8/(a*x^12)+1/33*b*(a+b*x)^8/(a^2*x^11)-1/110*b^2*(a+b*x)^8/(a^3*x^10)+1/495*b^3*(a+b*x)^8/(a^4*x^9)-1/3960*b^4*(a+b*x)^8/(a^5*x^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:314
  public void test0239() {
    check( //
        "Integrate[1/(a+x*Sqrt[-a]), x]", //
        "Log[a+x*Sqrt[-a]]/Sqrt[-a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:578
  public void test0240() {
    check( //
        "Integrate[Sqrt[2+b*x]/x^(9/2), x]", //
        "-1/7*(2+b*x)^(3/2)/x^(7/2)+2/35*b*(2+b*x)^(3/2)/x^(5/2)-2/105*b^2*(2+b*x)^(3/2)/x^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:650
  public void test0241() {
    check( //
        "Integrate[1/(x^(5/2)*(a+b*x)^(3/2)), x]", //
        "2/(a*x^(3/2)*Sqrt[a+b*x])-8/3*Sqrt[a+b*x]/(a^2*x^(3/2))+16/3*b*Sqrt[a+b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:706
  public void test0242() {
    check( //
        "Integrate[1/((2-b*x)^(3/2)*Sqrt[x]), x]", //
        "Sqrt[x]/Sqrt[2-b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:852
  public void test0243() {
    check( //
        "Integrate[x^3*(c*x^2)^(3/2)*(a+b*x), x]", //
        "1/7*a*c*x^6*Sqrt[c*x^2]+1/8*b*c*x^7*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:866
  public void test0244() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)/x^3, x]", //
        "1/3*a*c^2*x^2*Sqrt[c*x^2]+1/4*b*c^2*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:882
  public void test0245() {
    check( //
        "Integrate[(a+b*x)/(x*(c*x^2)^(3/2)), x]", //
        "-1/3*a/(c*x^2*Sqrt[c*x^2])-1/2*b/(c*x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:900
  public void test0246() {
    check( //
        "Integrate[x*(a+b*x)^2*Sqrt[c*x^2], x]", //
        "1/3*a^2*x^2*Sqrt[c*x^2]+1/2*a*b*x^3*Sqrt[c*x^2]+1/5*b^2*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:914
  public void test0247() {
    check( //
        "Integrate[x*(c*x^2)^(5/2)*(a+b*x)^2, x]", //
        "1/7*a^2*c^2*x^6*Sqrt[c*x^2]+1/4*a*b*c^2*x^7*Sqrt[c*x^2]+1/9*b^2*c^2*x^8*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:944
  public void test0248() {
    check( //
        "Integrate[(a+b*x)^2/(x*(c*x^2)^(5/2)), x]", //
        "-1/5*a^2/(c^2*x^4*Sqrt[c*x^2])-1/2*a*b/(c^2*x^3*Sqrt[c*x^2])-1/3*b^2/(c^2*x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:976
  public void test0249() {
    check( //
        "Integrate[(c*x^2)^(5/2)/(x^7*(a+b*x)), x]", //
        "-c^2*Sqrt[c*x^2]/(a*x^2)-b*c^2*Log[x]*Sqrt[c*x^2]/(a^2*x)+b*c^2*Log[a+b*x]*Sqrt[c*x^2]/(a^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:992
  public void test0250() {
    check( //
        "Integrate[x/((c*x^2)^(3/2)*(a+b*x)), x]", //
        "(-1)/(a*c*Sqrt[c*x^2])-b*x*Log[x]/(a^2*c*Sqrt[c*x^2])+b*x*Log[a+b*x]/(a^2*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1010
  public void test0251() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^2*(a+b*x)^2), x]", //
        "a*c*Sqrt[c*x^2]/(b^2*x*(a+b*x))+c*Log[a+b*x]*Sqrt[c*x^2]/(b^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1026
  public void test0252() {
    check( //
        "Integrate[x^4/((c*x^2)^(3/2)*(a+b*x)^2), x]", //
        "a*x/(b^2*c*(a+b*x)*Sqrt[c*x^2])+x*Log[a+b*x]/(b^2*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1050
  public void test0253() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n, x]", //
        "-a^5*c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^6*(1+n)*x)+5*a^4*c^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^6*(2+n)*x)-10*a^3*c^2*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^6*(3+n)*x)+10*a^2*c^2*(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^6*(4+n)*x)-5*a*c^2*(a+b*x)^(5+n)*Sqrt[c*x^2]/(b^6*(5+n)*x)+c^2*(a+b*x)^(6+n)*Sqrt[c*x^2]/(b^6*(6+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1097
  public void test0254() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)^2/(c*x^2)^(3/2), x]", //
        "-a^2*d^2*x*(d*x)^(-2+m)/(c*(2-m)*Sqrt[c*x^2])-2*a*b*d*x*(d*x)^(-1+m)/(c*(1-m)*Sqrt[c*x^2])+b^2*x*(d*x)^m/(c*m*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1137
  public void test0255() {
    check( //
        "Integrate[(a+b*x)^3/(a*d/b+d*x)^3, x]", //
        "b^3*x/d^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1221
  public void test0256() {
    check( //
        "Integrate[(1+x)^(1/2)/(1-x)^(7/2), x]", //
        "1/5*(1+x)^(3/2)/(1-x)^(5/2)+1/15*(1+x)^(3/2)/(1-x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1235
  public void test0257() {
    check( //
        "Integrate[(1+x)^(3/2)/(1-x)^(11/2), x]", //
        "1/9*(1+x)^(5/2)/(1-x)^(9/2)+2/63*(1+x)^(5/2)/(1-x)^(7/2)+2/315*(1+x)^(5/2)/(1-x)^(5/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1267
  public void test0258() {
    check( //
        "Integrate[1/((1-x)^(7/2)*(1+x)^(1/2)), x]", //
        "1/5*Sqrt[1+x]/(1-x)^(5/2)+2/15*Sqrt[1+x]/(1-x)^(3/2)+2/15*Sqrt[1+x]/Sqrt[1-x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1327
  public void test0259() {
    check( //
        "Integrate[1/(Sqrt[a+b*x]*Sqrt[-a*d+b*d*x]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/Sqrt[-a*d+b*d*x]]/(b*Sqrt[d])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1347
  public void test0260() {
    check( //
        "Integrate[1/((a-I*a*x)^(19/4)*(a+I*a*x)^(1/4)), x]", //
        "-2/15*I*(a+I*a*x)^(3/4)/(a^2*(a-I*a*x)^(15/4))-4/55*I*(a+I*a*x)^(3/4)/(a^3*(a-I*a*x)^(11/4))-16/385*I*(a+I*a*x)^(3/4)/(a^4*(a-I*a*x)^(7/4))-32/1155*I*(a+I*a*x)^(3/4)/(a^5*(a-I*a*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1500
  public void test0261() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^17, x]", //
        "-1/16*(c+d*x)^11/((b*c-a*d)*(a+b*x)^16)+1/48*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^15)-1/168*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^14)+1/728*d^3*(c+d*x)^11/((b*c-a*d)^4*(a+b*x)^13)-1/4368*d^4*(c+d*x)^11/((b*c-a*d)^5*(a+b*x)^12)+1/48048*d^5*(c+d*x)^11/((b*c-a*d)^6*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1705
  public void test0262() {
    check( //
        "Integrate[(c+d*x)^(1/2)/(a+b*x)^(9/2), x]", //
        "-2/7*(c+d*x)^(3/2)/((b*c-a*d)*(a+b*x)^(7/2))+8/35*d*(c+d*x)^(3/2)/((b*c-a*d)^2*(a+b*x)^(5/2))-16/105*d^2*(c+d*x)^(3/2)/((b*c-a*d)^3*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1735
  public void test0263() {
    check( //
        "Integrate[1/((a+b*x)^(1/2)*(c+d*x)^(1/2)), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1749
  public void test0264() {
    check( //
        "Integrate[1/((a+b*x)^(9/2)*(c+d*x)^(3/2)), x]", //
        "(-2/7)/((b*c-a*d)*(a+b*x)^(7/2)*Sqrt[c+d*x])+16/35*d/((b*c-a*d)^2*(a+b*x)^(5/2)*Sqrt[c+d*x])-32/35*d^2/((b*c-a*d)^3*(a+b*x)^(3/2)*Sqrt[c+d*x])+128/35*d^3/((b*c-a*d)^4*Sqrt[a+b*x]*Sqrt[c+d*x])+256/35*d^4*Sqrt[a+b*x]/((b*c-a*d)^5*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1777
  public void test0265() {
    check( //
        "Integrate[1/(Sqrt[1-b*x]*Sqrt[2+b*x]), x]", //
        "-ArcSin[1/3*(-1-2*b*x)]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1829
  public void test0266() {
    check( //
        "Integrate[(c+d*x)^(1/3)/(a+b*x)^(13/3), x]", //
        "-3/10*(c+d*x)^(4/3)/((b*c-a*d)*(a+b*x)^(10/3))+9/35*d*(c+d*x)^(4/3)/((b*c-a*d)^2*(a+b*x)^(7/3))-27/140*d^2*(c+d*x)^(4/3)/((b*c-a*d)^3*(a+b*x)^(4/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2182
  public void test0267() {
    check( //
        "Integrate[1/2*x^2+1/3*x^3, x]", //
        "1/6*x^3+1/12*x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:21
  public void test0268() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^4, x]", //
        "-1/3*a^4*c^3/x^3+a^3*b*c^3/x^2-b^4*c^3*x+2*a*b^3*c^3*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:35
  public void test0269() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^5, x]", //
        "-1/4*a^5*c^4/x^4+a^4*b*c^4/x^3-a^3*b^2*c^4/x^2-2*a^2*b^3*c^4/x+b^5*c^4*x-3*a*b^4*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:49
  public void test0270() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^5, x]", //
        "-1/4*a^6*c^5/x^4+4/3*a^5*b*c^5/x^3-5/2*a^4*b^2*c^5/x^2+4*a*b^5*c^5*x-1/2*b^6*c^5*x^2-5*a^2*b^4*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:163
  public void test0271() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^10, x]", //
        "-1/9*a^5*A/x^9-1/8*a^4*(5*A*b+a*B)/x^8-5/7*a^3*b*(2*A*b+a*B)/x^7-5/3*a^2*b^2*(A*b+a*B)/x^6-a*b^3*(A*b+2*a*B)/x^5-1/4*b^4*(A*b+5*a*B)/x^4-1/3*b^5*B/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:177
  public void test0272() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x, x]", //
        "10*a^9*A*b*x+45/2*a^8*A*b^2*x^2+40*a^7*A*b^3*x^3+105/2*a^6*A*b^4*x^4+252/5*a^5*A*b^5*x^5+35*a^4*A*b^6*x^6+120/7*a^3*A*b^7*x^7+45/8*a^2*A*b^8*x^8+10/9*a*A*b^9*x^9+1/10*A*b^10*x^10+1/11*B*(a+b*x)^11/b+a^10*A*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:191
  public void test0273() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^15, x]", //
        "-1/14*A*(a+b*x)^11/(a*x^14)+1/182*(3*A*b-14*a*B)*(a+b*x)^11/(a^2*x^13)-1/1092*b*(3*A*b-14*a*B)*(a+b*x)^11/(a^3*x^12)+1/12012*b^2*(3*A*b-14*a*B)*(a+b*x)^11/(a^4*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:452
  public void test0274() {
    check( //
        "Integrate[x^(5/2)*(A+B*x)/(a+b*x), x]", //
        "-2/3*a*(A*b-a*B)*x^(3/2)/b^3+2/5*(A*b-a*B)*x^(5/2)/b^2+2/7*B*x^(7/2)/b-2*a^(5/2)*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/b^(9/2)+2*a^2*(A*b-a*B)*Sqrt[x]/b^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:638
  public void test0275() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/x^(9/2), x]", //
        "-2/7*A*(a+b*x)^(5/2)/(a*x^(7/2))+2/35*(2*A*b-7*a*B)*(a+b*x)^(5/2)/(a^2*x^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:652
  public void test0276() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/x^(15/2), x]", //
        "-2/13*A*(a+b*x)^(7/2)/(a*x^(13/2))+2/143*(6*A*b-13*a*B)*(a+b*x)^(7/2)/(a^2*x^(11/2))-8/1287*b*(6*A*b-13*a*B)*(a+b*x)^(7/2)/(a^3*x^(9/2))+16/9009*b^2*(6*A*b-13*a*B)*(a+b*x)^(7/2)/(a^4*x^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:668
  public void test0277() {
    check( //
        "Integrate[(A+B*x)/(x^(15/2)*Sqrt[a+b*x]), x]", //
        "-2/13*A*Sqrt[a+b*x]/(a*x^(13/2))+2/143*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^2*x^(11/2))-20/1287*b*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^3*x^(9/2))+160/9009*b^2*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^4*x^(7/2))-64/3003*b^3*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^5*x^(5/2))+256/9009*b^4*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^6*x^(3/2))-512/9009*b^5*(12*A*b-13*a*B)*Sqrt[a+b*x]/(a^7*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:732
  public void test0278() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x^2*Sqrt[c+d*x]), x]", //
        "-(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(3/2)*Sqrt[a])-Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:746
  public void test0279() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x*(c+d*x)^(5/2)), x]", //
        "-2/3*d*(a+b*x)^(3/2)/(c*(b*c-a*d)*(c+d*x)^(3/2))-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(5/2)+2*Sqrt[a+b*x]/(c^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:778
  public void test0280() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^5, x]", //
        "-1/4*(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^4-1/64*(3*b^4*c^4-20*a*b^3*c^3*d+90*a^2*b^2*c^2*d^2+60*a^3*b*c*d^3-5*a^4*d^4)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(3/2))+2*b^(3/2)*d^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]-1/96*(3*b^2*c/a+50*b*d-5*a*d^2/c)*(c+d*x)^(3/2)*Sqrt[a+b*x]/x^2-1/24*(3*b*c+5*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c*x^3)+1/64*(3*b^3*c^3-17*a*b^2*c^2*d-55*a^2*b*c*d^2+5*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:812
  public void test0281() {
    check( //
        "Integrate[(a+b*x)^(5/2)*Sqrt[c+d*x]/x^2, x]", //
        "-1/4*(b^2*c^2-10*a*b*c*d-15*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]/d^(3/2)-a^(3/2)*(5*b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[c]+3/2*b*(a+b*x)^(3/2)*Sqrt[c+d*x]-(a+b*x)^(5/2)*Sqrt[c+d*x]/x+1/4*b*(b*c+11*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/d");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:842
  public void test0282() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^3*Sqrt[c+d*x]), x]", //
        "-1/4*(15*b^2*c^2-10*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(5/2)+2*b^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[d]-1/2*a*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c*x^2)-1/4*a*(7*b*c-3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:888
  public void test0283() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^5*Sqrt[a+b*x]), x]", //
        "-5/64*(b*c-a*d)^3*(7*b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(3/2))-5/96*(b*c-a*d)*(7*b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^3*c*x^2)+1/24*(7*b*c+a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a^2*c*x^3)-1/4*(c+d*x)^(7/2)*Sqrt[a+b*x]/(a*c*x^4)+5/64*(b*c-a*d)^2*(7*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:952
  public void test0284() {
    check( //
        "Integrate[x^3/((a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "-3*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(5/2))+2*a*x^2/(b*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])+(c*(3*b^2*c^2-2*a*b*c*d+3*a^2*d^2)+d*(b*c-3*a*d)*(b*c-a*d)*x)*Sqrt[a+b*x]/(b^2*d^2*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:966
  public void test0285() {
    check( //
        "Integrate[1/(x^2*(a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "(3*b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(7/2))-b*(3*b*c-a*d)/(a^2*c*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])+(-1)/(a*c*x*(c+d*x)^(3/2)*Sqrt[a+b*x])-1/3*d*(9*b^2*c^2-6*a*b*c*d+5*a^2*d^2)*Sqrt[a+b*x]/(a^2*c^2*(b*c-a*d)^2*(c+d*x)^(3/2))-1/3*d*(9*b^3*c^3-9*a*b^2*c^2*d+31*a^2*b*c*d^2-15*a^3*d^3)*Sqrt[a+b*x]/(a^2*c^3*(b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:986
  public void test0286() {
    check( //
        "Integrate[x^5/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "2/3*a*x^4/(b*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))-5*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(7/2)*d^(7/2))+2/3*a*(11*b*c-5*a*d)*x^3/(b^2*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])-2/3*c*(b^2*c^2+12*a*b*c*d-5*a^2*d^2)*x^2*Sqrt[a+b*x]/(b^2*d*(b*c-a*d)^3*(c+d*x)^(3/2))+1/3*(c*(15*b^4*c^4-40*a*b^3*c^3*d+18*a^2*b^2*c^2*d^2-40*a^3*b*c*d^3+15*a^4*d^4)+d*(b*c-a*d)*(5*b^3*c^3-9*a*b^2*c^2*d+35*a^2*b*c*d^2-15*a^3*d^3)*x)*Sqrt[a+b*x]/(b^3*d^3*(b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1033
  public void test0287() {
    check( //
        "Integrate[1/((-1+x)^(3/2)*x*(1+x)^(3/2)), x]", //
        "-ArcTan[Sqrt[-1+x]*Sqrt[1+x]]+(-1)/(Sqrt[-1+x]*Sqrt[1+x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1106
  public void test0288() {
    check( //
        "Integrate[1/(x^2*(a+b*x)^(3/4)*(c+d*x)^(1/4)), x]", //
        "-(a+b*x)^(1/4)*(c+d*x)^(3/4)/(a*c*x)+1/2*(3*b*c+a*d)*ArcTan[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(7/4)*c^(5/4))+1/2*(3*b*c+a*d)*ArcTanh[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(7/4)*c^(5/4))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1297
  public void test0289() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^9, x]", //
        "-1/8*(B*d-A*e)*(a+b*x)^7/(e*(b*d-a*e)*(d+e*x)^8)+1/56*(7*b*B*d+A*b*e-8*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^2*(d+e*x)^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1355
  public void test0290() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^3/(a+b*x)^2, x]", //
        "3*e*(b*d-a*e)*(b*B*d+A*b*e-2*a*B*e)*x/b^4-(A*b-a*B)*(b*d-a*e)^3/(b^5*(a+b*x))+1/2*e^2*(3*b*B*d+A*b*e-4*a*B*e)*(a+b*x)^2/b^5+1/3*B*e^3*(a+b*x)^3/b^5+(b*d-a*e)^2*(b*B*d+3*A*b*e-4*a*B*e)*Log[a+b*x]/b^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2080
  public void test0291() {
    check( //
        "Integrate[(3+5*x)*Sqrt[1-2*x]/(2+3*x), x]", //
        "-5/9*(1-2*x)^(3/2)+2/9*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-2/9*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2186
  public void test0292() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^6/(3+5*x), x]", //
        "2/234375*(1-2*x)^(3/2)-167115051/2500000*(1-2*x)^(5/2)+70752609/700000*(1-2*x)^(7/2)-665817/10000*(1-2*x)^(9/2)+507627/22000*(1-2*x)^(11/2)-43011/10400*(1-2*x)^(13/2)+243/800*(1-2*x)^(15/2)-22/390625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/390625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2308
  public void test0293() {
    check( //
        "Integrate[(3+5*x)/((2+3*x)*Sqrt[1-2*x]), x]", //
        "2/3*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]/Sqrt[21]-5/3*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2384
  public void test0294() {
    check( //
        "Integrate[(3+5*x)/((1-2*x)^(3/2)*(2+3*x)), x]", //
        "2/7*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]/Sqrt[21]+11/7/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2552
  public void test0295() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/(d+e*x)^(11/2), x]", //
        "-2/9*(B*d-A*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)*(d+e*x)^(9/2))+2/63*(7*b*B*d+2*A*b*e-9*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^2*(d+e*x)^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2600
  public void test0296() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^7, x]", //
        "-588912203/1229312*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/18*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^6+37/1260*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+10921/70560*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+126799/141120*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+4429459/790272*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+463266973/11063808*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2644
  public void test0297() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2)), x]", //
        "103*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/7*(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-103/7*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2676
  public void test0298() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3, x]", //
        "-1/6*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^2-1649/108*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-37/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+37/12*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)-205/36*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2690
  public void test0299() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^5, x]", //
        "-1/12*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-1922677/762048*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+100/243*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-871/6048*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+181/216*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-77269/254016*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2720
  public void test0300() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^2*(3+5*x)^(5/2)), x]", //
        "-169/21*(1-2*x)^(3/2)/(3+5*x)^(3/2)+3/7*(1-2*x)^(5/2)/((2+3*x)*(3+5*x)^(3/2))-169*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+169*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2782
  public void test0301() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)*(3+5*x)^(3/2)), x]", //
        "338/225*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]+98/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-22/5*(1-2*x)^(3/2)/Sqrt[3+5*x]-128/75*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2796
  public void test0302() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^4*(3+5*x)^(5/2)), x]", //
        "7/9*(1-2*x)^(3/2)/((2+3*x)^3*(3+5*x)^(3/2))-1361195/8*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-196735/72*Sqrt[1-2*x]/(3+5*x)^(3/2)+77/4*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+7843/24*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+1784635/72*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2828
  public void test0303() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^2*Sqrt[1-2*x]), x]", //
        "125/54*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-173/189*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/21*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)-185/126*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2844
  public void test0304() {
    check( //
        "Integrate[1/((2+3*x)^4*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-222185/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/7*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+185/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+19415/2744*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2858
  public void test0305() {
    check( //
        "Integrate[(2+3*x)/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-2/165*Sqrt[1-2*x]/(3+5*x)^(3/2)-206/1815*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2876
  public void test0306() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^2), x]", //
        "-29/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77*(3+5*x)^(3/2)/((2+3*x)*Sqrt[1-2*x])-29/539*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2906
  public void test0307() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-321/20*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+49/22*Sqrt[3+5*x]/Sqrt[1-2*x]+9/20*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2920
  public void test0308() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)*(3+5*x)^(3/2)), x]", //
        "18/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77/(Sqrt[1-2*x]*Sqrt[3+5*x])-370/847*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2938
  public void test0309() {
    check( //
        "Integrate[(2+3*x)^3*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "126513/320*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1/3*(2+3*x)^3*Sqrt[3+5*x]/(1-2*x)^(3/2)-233/66*(2+3*x)^2*Sqrt[3+5*x]/Sqrt[1-2*x]-1/3520*(168157+69780*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2952
  public void test0310() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^2), x]", //
        "4/231*(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x))+95/343*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+190/1617*(3+5*x)^(3/2)/((2+3*x)*Sqrt[1-2*x])+95/3773*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2968
  public void test0311() {
    check( //
        "Integrate[(2+3*x)^4/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "392283/1600*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/33*(2+3*x)^3*Sqrt[3+5*x]/(1-2*x)^(3/2)-1589/726*(2+3*x)^2*Sqrt[3+5*x]/Sqrt[1-2*x]-1/193600*(5735477+2380020*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2982
  public void test0312() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "2/33/((1-2*x)^(3/2)*Sqrt[3+5*x])+40/363/(Sqrt[1-2*x]*Sqrt[3+5*x])-400/3993*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2998
  public void test0313() {
    check( //
        "Integrate[1/(Sqrt[a+b*x]*Sqrt[c+b*(-1+c)*x/a]*Sqrt[e+b*(-1+e)*x/a]), x]", //
        "2*EllipticF[ArcSin[Sqrt[1-c]*Sqrt[a+b*x]/Sqrt[a]],(1-e)/(1-c)]*Sqrt[a]/(b*Sqrt[1-c])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3018
  public void test0314() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2), x]", //
        "-4636/2205*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-124/2205*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/15*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+74/315*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+4636/2205*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3032
  public void test0315() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/Sqrt[2+3*x], x]", //
        "-9013/1890*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-131/945*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/7*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+2/21*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-131/189*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3048
  public void test0316() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-2*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3062
  public void test0317() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "458/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-178/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-194/825*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3080
  public void test0318() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(11/2), x]", //
        "-42623864/972405*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1282376/972405*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/27*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(9/2)+82/567*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+13136/19845*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+613276/138915*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+42623864/972405*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3094
  public void test0319() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[2+3*x], x]", //
        "-886499/255150*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-11908/127575*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/27*(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]-499/2835*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+46/567*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-11908/25515*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3110
  public void test0320() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(7/2)*Sqrt[3+5*x]), x]", //
        "-17804/315*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-536/315*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/15*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+256/45*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+17804/315*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3124
  public void test0321() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[2+3*x]/(3+5*x)^(5/2), x]", //
        "38/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+212/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*(1-2*x)^(3/2)*Sqrt[2+3*x]/(3+5*x)^(3/2)-18/25*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3142
  public void test0322() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(13/2), x]", //
        "-247408648/64827*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-7442032/64827*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/33*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(11/2)+10/99*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(9/2)+1900/2079*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+76492/14553*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+3560432/101871*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+247408648/713097*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3156
  public void test0323() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/Sqrt[2+3*x], x]", //
        "-146222113/3827250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1654421/1913625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+74/891*(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]+2/33*(1-2*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]-146963/467775*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+9698/93555*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1654421/4209975*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3172
  public void test0324() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-3896/135*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-164/135*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/9*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(3/2)+812/27*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3186
  public void test0325() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(11/2)*(3+5*x)^(3/2)), x]", //
        "683150096/9261*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+20549264/9261*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/27*(1-2*x)^(3/2)/((2+3*x)^(9/2)*Sqrt[3+5*x])+652/81*Sqrt[1-2*x]/((2+3*x)^(7/2)*Sqrt[3+5*x])+11660/189*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+813208/1323*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+113020952/9261*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-3415750480/27783*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3204
  public void test0326() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "2/3*EllipticE[ArcSin[Sqrt[5]*Sqrt[2+3*x]],2/35]*Sqrt[5/7]*Sqrt[-3-5*x]/Sqrt[3+5*x]-2/7*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3218
  public void test0327() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[2+3*x]/Sqrt[1-2*x], x]", //
        "-17587/378*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-529/378*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-20/21*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1/7*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2645/378*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3264
  public void test0328() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-2797/1375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-598/1375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/165*(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-404/9075*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3288
  public void test0329() {
    check( //
        "Integrate[(2+3*x)^(7/2)*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "4071079/17500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+673523/8750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+(2+3*x)^(7/2)*Sqrt[3+5*x]/Sqrt[1-2*x]+2517/350*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+12/7*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+29293/875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3302
  public void test0330() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(5/2)), x]", //
        "458/1029*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-178/1029*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])-97/147*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-458/1029*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3318
  public void test0331() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "1597/50*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+24/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*(2+3*x)^(3/2)*Sqrt[3+5*x]/Sqrt[1-2*x]+69/55*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3332
  public void test0332() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "220076/3773*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+6584/3773*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77/((2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x])+54/539*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+9876/3773*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-1100380/41503*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3350
  public void test0333() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "-4621/10*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-139/10*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*(2+3*x)^(5/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-100/33*(2+3*x)^(3/2)*Sqrt[3+5*x]/Sqrt[1-2*x]-133/22*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3364
  public void test0334() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^(7/2)), x]", //
        "-16732/84035*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+3946/84035*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(5/2))+124/147*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-779/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-2264/12005*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-3946/84035*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3380
  public void test0335() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "37/77*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/77*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/33*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)+74/2541*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3394
  public void test0336() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(7/2)*(3+5*x)^(3/2)), x]", //
        "4839325048/10168235*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+145418632/10168235*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231/((1-2*x)^(3/2)*(2+3*x)^(5/2)*Sqrt[3+5*x])+1616/17787/((2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x])-2206/207515*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+499564/1452605*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+72709316/10168235*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-4839325048/67110351*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:58
  public void test0337() {
    check( //
        "Integrate[(7+5*x)^3*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x], x]", //
        "522167393/23328*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]-6489123157/699840*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-1182926269/1603800*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-12243139/356400*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-17561/8910*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-427/2970*(7+5*x)^3*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+2/55*(7+5*x)^4*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:93
  public void test0338() {
    check( //
        "Integrate[(7+5*x)^2/(Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "24353/36*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[5-2*x]/(Sqrt[66]*Sqrt[-5+2*x])-2135/108*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-25/36*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.7 P(x) (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:16
  public void test0339() {
    check( //
        "Integrate[(A+B*x)/(Sqrt[c+d*x]*Sqrt[e+f*x]*Sqrt[g+h*x]), x]", //
        "2*B*EllipticE[ArcSin[Sqrt[f]*Sqrt[c+d*x]/Sqrt[-d*e+c*f]],(d*e-c*f)*h/(f*(d*g-c*h))]*Sqrt[-d*e+c*f]*Sqrt[d*(e+f*x)/(d*e-c*f)]*Sqrt[g+h*x]/(d*h*Sqrt[f]*Sqrt[e+f*x]*Sqrt[d*(g+h*x)/(d*g-c*h)])-2*(B*g-A*h)*EllipticF[ArcSin[Sqrt[f]*Sqrt[c+d*x]/Sqrt[-d*e+c*f]],(d*e-c*f)*h/(f*(d*g-c*h))]*Sqrt[-d*e+c*f]*Sqrt[d*(e+f*x)/(d*e-c*f)]*Sqrt[d*(g+h*x)/(d*g-c*h)]/(d*h*Sqrt[f]*Sqrt[e+f*x]*Sqrt[g+h*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:26
  public void test0340() {
    check( //
        "Integrate[x^3*(a+b*x^2)^2, x]", //
        "1/4*a^2*x^4+1/3*a*b*x^6+1/8*b^2*x^8");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:40
  public void test0341() {
    check( //
        "Integrate[x^9*(a+b*x^2)^3, x]", //
        "1/10*a^3*x^10+1/4*a^2*b*x^12+3/14*a*b^2*x^14+1/16*b^3*x^16");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:68
  public void test0342() {
    check( //
        "Integrate[x^3*(a+b*x^2)^5, x]", //
        "-1/12*a*(a+b*x^2)^6/b^2+1/14*(a+b*x^2)^7/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:96
  public void test0343() {
    check( //
        "Integrate[x^13*(a+b*x^2)^8, x]", //
        "1/18*a^6*(a+b*x^2)^9/b^7-3/10*a^5*(a+b*x^2)^10/b^7+15/22*a^4*(a+b*x^2)^11/b^7-5/6*a^3*(a+b*x^2)^12/b^7+15/26*a^2*(a+b*x^2)^13/b^7-3/14*a*(a+b*x^2)^14/b^7+1/30*(a+b*x^2)^15/b^7");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:110
  public void test0344() {
    check( //
        "Integrate[(a+b*x^2)^8/x^15, x]", //
        "-1/14*a^8/x^14-2/3*a^7*b/x^12-14/5*a^6*b^2/x^10-7*a^5*b^3/x^8-35/3*a^4*b^4/x^6-14*a^3*b^5/x^4-14*a^2*b^6/x^2+1/2*b^8*x^2+8*a*b^7*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:140
  public void test0345() {
    check( //
        "Integrate[x^8/(a+b*x^2), x]", //
        "-a^3*x/b^4+1/3*a^2*x^3/b^3-1/5*a*x^5/b^2+1/7*x^7/b+a^(7/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(9/2)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:154
  public void test0346() {
    check( //
        "Integrate[1/(x^6*(a+b*x^2)), x]", //
        "(-1/5)/(a*x^5)+1/3*b/(a^2*x^3)-b^2/(a^3*x)-b^(5/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/a^(7/2)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:168
  public void test0347() {
    check( //
        "Integrate[x^3/(a+b*x^2)^2, x]", //
        "1/2*a/(b^2*(a+b*x^2))+1/2*Log[a+b*x^2]/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:210
  public void test0348() {
    check( //
        "Integrate[x^15/(a+b*x^2)^10, x]", //
        "1/18*x^16/(a*(a+b*x^2)^9)+1/144*x^16/(a^2*(a+b*x^2)^8)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:238
  public void test0349() {
    check( //
        "Integrate[x^3/(a-b*x^2), x]", //
        "-1/2*x^2/b-1/2*a*Log[a-b*x^2]/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:266
  public void test0350() {
    check( //
        "Integrate[1/(x*(1+b*x^2)), x]", //
        "Log[x]-1/2*Log[1+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:452
  public void test0351() {
    check( //
        "Integrate[(a+b*x^2)^(9/2)/x, x]", //
        "1/3*a^3*(a+b*x^2)^(3/2)+1/5*a^2*(a+b*x^2)^(5/2)+1/7*a*(a+b*x^2)^(7/2)+1/9*(a+b*x^2)^(9/2)-a^(9/2)*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]+a^4*Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:566
  public void test0352() {
    check( //
        "Integrate[1/(x^2*(a+b*x^2)^(9/2)), x]", //
        "(-1)/(a*x*(a+b*x^2)^(7/2))-8/7*b*x/(a^2*(a+b*x^2)^(7/2))-48/35*b*x/(a^3*(a+b*x^2)^(5/2))-64/35*b*x/(a^4*(a+b*x^2)^(3/2))-128/35*b*x/(a^5*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:608
  public void test0353() {
    check( //
        "Integrate[1/(x*Sqrt[-9-4*x^2]), x]", //
        "1/3*ArcTan[1/3*Sqrt[-9-4*x^2]]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:868
  public void test0354() {
    check( //
        "Integrate[(a+b*x^2)^(3/4), x]", //
        "6/5*a*x/(a+b*x^2)^(1/4)+2/5*x*(a+b*x^2)^(3/4)-6/5*a^(3/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:912
  public void test0355() {
    check( //
        "Integrate[x^6/(a+b*x^2)^(5/4), x]", //
        "8/3*a^2*x/(b^3*(a+b*x^2)^(1/4))-4/9*a*x^3/(b^2*(a+b*x^2)^(1/4))+2/9*x^5/(b*(a+b*x^2)^(1/4))-16/3*a^(5/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(b^(7/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:926
  public void test0356() {
    check( //
        "Integrate[1/(a+b*x^2)^(7/4), x]", //
        "2/3*x/(a*(a+b*x^2)^(3/4))+2/3*(1+b*x^2/a)^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(3/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1072
  public void test0357() {
    check( //
        "Integrate[1/((c*x)^(11/2)*(a+b*x^2)^(5/4)), x]", //
        "(-2/9)/(a*c*(c*x)^(9/2)*(a+b*x^2)^(1/4))+4/9*b/(a^2*c^3*(c*x)^(5/2)*(a+b*x^2)^(1/4))-8/3*b^2/(a^3*c^5*(a+b*x^2)^(1/4)*Sqrt[c*x])+16/3*b^(5/2)*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[c*x]/(a^(7/2)*c^6*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:31
  public void test0358() {
    check( //
        "Integrate[(c+d*x^2)^4/(a+b*x^2), x]", //
        "d*(2*b*c-a*d)*(2*b^2*c^2-2*a*b*c*d+a^2*d^2)*x/b^4+1/3*d^2*(6*b^2*c^2-4*a*b*c*d+a^2*d^2)*x^3/b^3+1/5*d^3*(4*b*c-a*d)*x^5/b^2+1/7*d^4*x^7/b+(b*c-a*d)^4*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(9/2)*Sqrt[a])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:63
  public void test0359() {
    check( //
        "Integrate[(a+b*x^2)^(1/2), x]", //
        "1/2*a*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+1/2*x*Sqrt[a+b*x^2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:93
  public void test0360() {
    check( //
        "Integrate[(c+d*x^2)/(a+b*x^2)^(1/2), x]", //
        "1/2*(2*b*c-a*d)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)+1/2*d*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:122
  public void test0361() {
    check( //
        "Integrate[1/((b*c/d+b*x^2)*Sqrt[c+d*x^2]), x]", //
        "d*x/(b*c*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:227
  public void test0362() {
    check( //
        "Integrate[Sqrt[1-x^2]/Sqrt[2+3*x^2], x]", //
        "5/3*EllipticF[ArcSin[x],-3/2]/Sqrt[2]-1/3*EllipticE[ArcSin[x],-3/2]*Sqrt[2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:300
  public void test0363() {
    check( //
        "Integrate[Sqrt[-a+b*x^2]/Sqrt[c-d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],b*c/(a*d)]*Sqrt[c]*Sqrt[-a+b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1-b*x^2/a]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:314
  public void test0364() {
    check( //
        "Integrate[Sqrt[-c-d*x^2]/Sqrt[-a+b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[-c-d*x^2]/(Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[1+d*x^2/c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:38
  public void test0365() {
    check( //
        "Integrate[x^5*(a+b*x^2)^5*(A+B*x^2), x]", //
        "1/12*a^2*(A*b-a*B)*(a+b*x^2)^6/b^4-1/14*a*(2*A*b-3*a*B)*(a+b*x^2)^7/b^4+1/16*(A*b-3*a*B)*(a+b*x^2)^8/b^4+1/18*B*(a+b*x^2)^9/b^4");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:52
  public void test0366() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^9, x]", //
        "-1/8*a^5*A/x^8-1/6*a^4*(5*A*b+a*B)/x^6-5/4*a^3*b*(2*A*b+a*B)/x^4-5*a^2*b^2*(A*b+a*B)/x^2+1/2*b^4*(A*b+5*a*B)*x^2+1/4*b^5*B*x^4+5*a*b^3*(A*b+2*a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:66
  public void test0367() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^23, x]", //
        "-1/22*a^5*A/x^22-1/20*a^4*(5*A*b+a*B)/x^20-5/18*a^3*b*(2*A*b+a*B)/x^18-5/8*a^2*b^2*(A*b+a*B)/x^16-5/14*a*b^3*(A*b+2*a*B)/x^14-1/12*b^4*(A*b+5*a*B)/x^12-1/10*b^5*B/x^10");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:170
  public void test0368() {
    check( //
        "Integrate[x*(a+b*x^2)^2*(c+d*x^2)^2, x]", //
        "1/6*(b*c-a*d)^2*(a+b*x^2)^3/b^3+1/4*d*(b*c-a*d)*(a+b*x^2)^4/b^3+1/10*d^2*(a+b*x^2)^5/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:184
  public void test0369() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^4, x]", //
        "-1/3*a^2*c^3/x^3-a*c^2*(2*b*c+3*a*d)/x+c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x+1/3*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^3+1/5*b*d^2*(3*b*c+2*a*d)*x^5+1/7*b^2*d^3*x^7");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:234
  public void test0370() {
    check( //
        "Integrate[x^4*(c+d*x^2)^2/(a+b*x^2), x]", //
        "-a*(b*c-a*d)^2*x/b^4+1/3*(b*c-a*d)^2*x^3/b^3+1/5*d*(2*b*c-a*d)*x^5/b^2+1/7*d^2*x^7/b+a^(3/2)*(b*c-a*d)^2*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:248
  public void test0371() {
    check( //
        "Integrate[(c+d*x^2)^3/(a+b*x^2), x]", //
        "d*(3*b^2*c^2-3*a*b*c*d+a^2*d^2)*x/b^3+1/3*d^2*(3*b*c-a*d)*x^3/b^2+1/5*d^3*x^5/b+(b*c-a*d)^3*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(7/2)*Sqrt[a])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:416
  public void test0372() {
    check( //
        "Integrate[x^(7/2)*(A+B*x^2)/(a+b*x^2), x]", //
        "2/5*(A*b-a*B)*x^(5/2)/b^2+2/9*B*x^(9/2)/b-a^(5/4)*(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(13/4)*Sqrt[2])+a^(5/4)*(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(13/4)*Sqrt[2])-1/2*a^(5/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(13/4)*Sqrt[2])+1/2*a^(5/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(13/4)*Sqrt[2])-2*a*(A*b-a*B)*Sqrt[x]/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:448
  public void test0373() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/Sqrt[x], x]", //
        "2/5*a*(2*b*c+a*d)*x^(5/2)+2/9*b*(b*c+2*a*d)*x^(9/2)+2/13*b^2*d*x^(13/2)+2*a^2*c*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:640
  public void test0374() {
    check( //
        "Integrate[(A+B*x^2)/(x^3*Sqrt[a+b*x^2]), x]", //
        "1/2*(A*b-2*a*B)*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/a^(3/2)-1/2*A*Sqrt[a+b*x^2]/(a*x^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:700
  public void test0375() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^(3/2)/x^2, x]", //
        "-1/24*(b^2*c^2-12*a*d*(b*c+2*a*d))*x*(c+d*x^2)^(3/2)/(c*d)-a^2*(c+d*x^2)^(5/2)/(c*x)+1/6*b^2*x*(c+d*x^2)^(5/2)/d-1/16*c*(b^2*c^2-12*a*d*(b*c+2*a*d))*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(3/2)-1/16*(b^2*c^2-12*a*d*(b*c+2*a*d))*x*Sqrt[c+d*x^2]/d");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:762
  public void test0376() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)*Sqrt[d*x^2]), x]", //
        "b/(a^2*Sqrt[d*x^2])+(-1/3)/(a*x^2*Sqrt[d*x^2])+b^(3/2)*x*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:796
  public void test0377() {
    check( //
        "Integrate[x^5/((a+b*x^2)*Sqrt[c+d*x^2]), x]", //
        "1/3*(c+d*x^2)^(3/2)/(b*d^2)-a^2*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/(b^(5/2)*Sqrt[b*c-a*d])-(b*c+a*d)*Sqrt[c+d*x^2]/(b^2*d^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:858
  public void test0378() {
    check( //
        "Integrate[x^3/((a+b*x^2)^2*Sqrt[c+d*x^2]), x]", //
        "-1/2*(2*b*c-a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/(b^(3/2)*(b*c-a*d)^(3/2))+1/2*a*Sqrt[c+d*x^2]/(b*(b*c-a*d)*(a+b*x^2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1143
  public void test0379() {
    check( //
        "Integrate[x^2/(Sqrt[2-3*x^2]*Sqrt[1-x^2]), x]", //
        "-1/3*EllipticE[ArcSin[x],3/2]*Sqrt[2]+1/3*EllipticF[ArcSin[x],3/2]*Sqrt[2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1301
  public void test0380() {
    check( //
        "Integrate[(c+d*x^2)/((e*x)^(9/2)*(a+b*x^2)^(5/4)), x]", //
        "-2/7*c/(a*e*(e*x)^(7/2)*(a+b*x^2)^(1/4))-2/7*(8*b*c-7*a*d)/(a^2*e^3*(e*x)^(3/2)*(a+b*x^2)^(1/4))+8/21*(8*b*c-7*a*d)*(a+b*x^2)^(3/4)/(a^3*e^3*(e*x)^(3/2))");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:181
  public void test0381() {
    check( //
        "Integrate[1/((a+b*x^2)^(1/2)*Sqrt[c+d*x^2]*Sqrt[e+f*x^2]), x]", //
        "EllipticF[ArcSin[x*Sqrt[b*e-a*f]/(Sqrt[e]*Sqrt[a+b*x^2])],(b*c-a*d)*e/(c*(b*e-a*f))]*Sqrt[e]*Sqrt[c+d*x^2]*Sqrt[a*(e+f*x^2)/(e*(a+b*x^2))]/(c*Sqrt[b*e-a*f]*Sqrt[a*(c+d*x^2)/(c*(a+b*x^2))]*Sqrt[e+f*x^2])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:43
  public void test0382() {
    check( //
        "Integrate[(A+B*x)/(x^3*Sqrt[a+b*x^2]), x]", //
        "1/2*A*b*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/a^(3/2)-1/2*A*Sqrt[a+b*x^2]/(a*x^2)-B*Sqrt[a+b*x^2]/(a*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2653
  public void test0383() {
    check( //
        "Integrate[Sqrt[x]/(1+Sqrt[x]), x]", //
        "x+2*Log[1+Sqrt[x]]-2*Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:35
  public void test0384() {
    check( //
        "Integrate[x^(1/2), x]", //
        "2/3*x^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:121
  public void test0385() {
    check( //
        "Integrate[(a+b*x)^5/x^9, x]", //
        "-1/8*(a+b*x)^6/(a*x^8)+1/28*b*(a+b*x)^6/(a^2*x^7)-1/168*b^2*(a+b*x)^6/(a^3*x^6)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:177
  public void test0386() {
    check( //
        "Integrate[(a+b*x)^10/x^14, x]", //
        "-1/13*(a+b*x)^11/(a*x^13)+1/78*b*(a+b*x)^11/(a^2*x^12)-1/858*b^2*(a+b*x)^11/(a^3*x^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:315
  public void test0387() {
    check( //
        "Integrate[1/(a^2+x*Sqrt[-a]), x]", //
        "Log[a^2+x*Sqrt[-a]]/Sqrt[-a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:651
  public void test0388() {
    check( //
        "Integrate[1/(x^(7/2)*(a+b*x)^(3/2)), x]", //
        "2/(a*x^(5/2)*Sqrt[a+b*x])-12/5*Sqrt[a+b*x]/(a^2*x^(5/2))+16/5*b*Sqrt[a+b*x]/(a^3*x^(3/2))-32/5*b^2*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:853
  public void test0389() {
    check( //
        "Integrate[x^2*(c*x^2)^(3/2)*(a+b*x), x]", //
        "1/6*a*c*x^5*Sqrt[c*x^2]+1/7*b*c*x^6*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:867
  public void test0390() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)/x^4, x]", //
        "1/2*a*c^2*x*Sqrt[c*x^2]+1/3*b*c^2*x^2*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:883
  public void test0391() {
    check( //
        "Integrate[(a+b*x)/(x^2*(c*x^2)^(3/2)), x]", //
        "-1/4*a/(c*x^3*Sqrt[c*x^2])-1/3*b/(c*x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:901
  public void test0392() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[c*x^2], x]", //
        "1/2*a^2*x*Sqrt[c*x^2]+2/3*a*b*x^2*Sqrt[c*x^2]+1/4*b^2*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:915
  public void test0393() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2, x]", //
        "1/6*a^2*c^2*x^5*Sqrt[c*x^2]+2/7*a*b*c^2*x^6*Sqrt[c*x^2]+1/8*b^2*c^2*x^7*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:931
  public void test0394() {
    check( //
        "Integrate[(a+b*x)^2/(x^4*Sqrt[c*x^2]), x]", //
        "-1/4*a^2/(x^3*Sqrt[c*x^2])-2/3*a*b/(x^2*Sqrt[c*x^2])-1/2*b^2/(x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:945
  public void test0395() {
    check( //
        "Integrate[(a+b*x)^2/(x^2*(c*x^2)^(5/2)), x]", //
        "-1/6*a^2/(c^2*x^5*Sqrt[c*x^2])-2/5*a*b/(c^2*x^4*Sqrt[c*x^2])-1/4*b^2/(c^2*x^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:963
  public void test0396() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^2*(a+b*x)), x]", //
        "c*Sqrt[c*x^2]/b-a*c*Log[a+b*x]*Sqrt[c*x^2]/(b^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:993
  public void test0397() {
    check( //
        "Integrate[1/((c*x^2)^(3/2)*(a+b*x)), x]", //
        "b/(a^2*c*Sqrt[c*x^2])+(-1/2)/(a*c*x*Sqrt[c*x^2])+b^2*x*Log[x]/(a^3*c*Sqrt[c*x^2])-b^2*x*Log[a+b*x]/(a^3*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1051
  public void test0398() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n/x, x]", //
        "a^4*c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^5*(1+n)*x)-4*a^3*c^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^5*(2+n)*x)+6*a^2*c^2*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^5*(3+n)*x)-4*a*c^2*(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^5*(4+n)*x)+c^2*(a+b*x)^(5+n)*Sqrt[c*x^2]/(b^5*(5+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1098
  public void test0399() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)^2/(c*x^2)^(5/2), x]", //
        "-a^2*d^4*x*(d*x)^(-4+m)/(c^2*(4-m)*Sqrt[c*x^2])-2*a*b*d^3*x*(d*x)^(-3+m)/(c^2*(3-m)*Sqrt[c*x^2])-b^2*d^2*x*(d*x)^(-2+m)/(c^2*(2-m)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1138
  public void test0400() {
    check( //
        "Integrate[(a+b*x)^2/(a*d/b+d*x)^3, x]", //
        "b^2*Log[a+b*x]/d^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1222
  public void test0401() {
    check( //
        "Integrate[(1+x)^(1/2)/(1-x)^(9/2), x]", //
        "1/7*(1+x)^(3/2)/(1-x)^(7/2)+2/35*(1+x)^(3/2)/(1-x)^(5/2)+2/105*(1+x)^(3/2)/(1-x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1236
  public void test0402() {
    check( //
        "Integrate[(1+x)^(3/2)/(1-x)^(13/2), x]", //
        "1/11*(1+x)^(5/2)/(1-x)^(11/2)+1/33*(1+x)^(5/2)/(1-x)^(9/2)+2/231*(1+x)^(5/2)/(1-x)^(7/2)+2/1155*(1+x)^(5/2)/(1-x)^(5/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1250
  public void test0403() {
    check( //
        "Integrate[(1+x)^(5/2)/(1-x)^(13/2), x]", //
        "1/11*(1+x)^(7/2)/(1-x)^(11/2)+2/99*(1+x)^(7/2)/(1-x)^(9/2)+2/693*(1+x)^(7/2)/(1-x)^(7/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1268
  public void test0404() {
    check( //
        "Integrate[1/((1-x)^(9/2)*(1+x)^(1/2)), x]", //
        "1/7*Sqrt[1+x]/(1-x)^(7/2)+3/35*Sqrt[1+x]/(1-x)^(5/2)+2/35*Sqrt[1+x]/(1-x)^(3/2)+2/35*Sqrt[1+x]/Sqrt[1-x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1312
  public void test0405() {
    check( //
        "Integrate[1/((3-6*x)^(1/2)*(2+4*x)^(1/2)), x]", //
        "1/2*ArcSin[2*x]/Sqrt[6]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1362
  public void test0406() {
    check( //
        "Integrate[1/((a-I*a*x)^(9/4)*(a+I*a*x)^(7/4)), x]", //
        "(-2/5*I)/(a^2*(a-I*a*x)^(5/4)*(a+I*a*x)^(3/4))+(-8/5*I)/(a^3*(a-I*a*x)^(1/4)*(a+I*a*x)^(3/4))+16/15*I*(a-I*a*x)^(3/4)/(a^4*(a+I*a*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1501
  public void test0407() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^18, x]", //
        "-1/17*(c+d*x)^11/((b*c-a*d)*(a+b*x)^17)+3/136*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^16)-1/136*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^15)+1/476*d^3*(c+d*x)^11/((b*c-a*d)^4*(a+b*x)^14)-3/6188*d^4*(c+d*x)^11/((b*c-a*d)^5*(a+b*x)^13)+1/12376*d^5*(c+d*x)^11/((b*c-a*d)^6*(a+b*x)^12)-1/136136*d^6*(c+d*x)^11/((b*c-a*d)^7*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1596
  public void test0408() {
    check( //
        "Integrate[1/(c+d*x)^8, x]", //
        "(-1/7)/(d*(c+d*x)^7)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1706
  public void test0409() {
    check( //
        "Integrate[(c+d*x)^(1/2)/(a+b*x)^(11/2), x]", //
        "-2/9*(c+d*x)^(3/2)/((b*c-a*d)*(a+b*x)^(9/2))+4/21*d*(c+d*x)^(3/2)/((b*c-a*d)^2*(a+b*x)^(7/2))-16/105*d^2*(c+d*x)^(3/2)/((b*c-a*d)^3*(a+b*x)^(5/2))+32/315*d^3*(c+d*x)^(3/2)/((b*c-a*d)^4*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1736
  public void test0410() {
    check( //
        "Integrate[1/((a+b*x)^(3/2)*(c+d*x)^(1/2)), x]", //
        "-2*Sqrt[c+d*x]/((b*c-a*d)*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1750
  public void test0411() {
    check( //
        "Integrate[1/((a+b*x)^(11/2)*(c+d*x)^(3/2)), x]", //
        "(-2/9)/((b*c-a*d)*(a+b*x)^(9/2)*Sqrt[c+d*x])+20/63*d/((b*c-a*d)^2*(a+b*x)^(7/2)*Sqrt[c+d*x])-32/63*d^2/((b*c-a*d)^3*(a+b*x)^(5/2)*Sqrt[c+d*x])+64/63*d^3/((b*c-a*d)^4*(a+b*x)^(3/2)*Sqrt[c+d*x])-256/63*d^4/((b*c-a*d)^5*Sqrt[a+b*x]*Sqrt[c+d*x])-512/63*d^5*Sqrt[a+b*x]/((b*c-a*d)^6*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1778
  public void test0412() {
    check( //
        "Integrate[1/(Sqrt[-b*x]*Sqrt[2+b*x]), x]", //
        "ArcSin[1+b*x]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1830
  public void test0413() {
    check( //
        "Integrate[(c+d*x)^(1/3)/(a+b*x)^(16/3), x]", //
        "-3/13*(c+d*x)^(4/3)/((b*c-a*d)*(a+b*x)^(13/3))+27/130*d*(c+d*x)^(4/3)/((b*c-a*d)^2*(a+b*x)^(10/3))-81/455*d^2*(c+d*x)^(4/3)/((b*c-a*d)^3*(a+b*x)^(7/3))+243/1820*d^3*(c+d*x)^(4/3)/((b*c-a*d)^4*(a+b*x)^(4/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1984
  public void test0414() {
    check( //
        "Integrate[1/((a+b*x)^(11/4)*(c+d*x)^(5/4)), x]", //
        "(-4/7)/((b*c-a*d)*(a+b*x)^(7/4)*(c+d*x)^(1/4))+32/21*d/((b*c-a*d)^2*(a+b*x)^(3/4)*(c+d*x)^(1/4))+128/21*d^2*(a+b*x)^(1/4)/((b*c-a*d)^3*(c+d*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2183
  public void test0415() {
    check( //
        "Integrate[3-5*x+2*x^2, x]", //
        "3*x-5/2*x^2+2/3*x^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2199
  public void test0416() {
    check( //
        "Integrate[1/2/Sqrt[x]+2*Sqrt[x], x]", //
        "4/3*x^(3/2)+Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:22
  public void test0417() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^5, x]", //
        "-1/4*a^4*c^3/x^4+2/3*a^3*b*c^3/x^3-2*a*b^3*c^3/x-b^4*c^3*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:36
  public void test0418() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^6, x]", //
        "-1/5*a^5*c^4/x^5+3/4*a^4*b*c^4/x^4-2/3*a^3*b^2*c^4/x^3-a^2*b^3*c^4/x^2+3*a*b^4*c^4/x+b^5*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:50
  public void test0419() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^6, x]", //
        "-1/5*a^6*c^5/x^5+a^5*b*c^5/x^4-5/3*a^4*b^2*c^5/x^3+5*a^2*b^4*c^5/x-b^6*c^5*x+4*a*b^5*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:92
  public void test0420() {
    check( //
        "Integrate[(e*x)^m*(a+b*x)*(a*c-b*c*x), x]", //
        "a^2*c*(e*x)^(1+m)/(e*(1+m))-b^2*c*(e*x)^(3+m)/(e^3*(3+m))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:164
  public void test0421() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^11, x]", //
        "-1/10*a^5*A/x^10-1/9*a^4*(5*A*b+a*B)/x^9-5/8*a^3*b*(2*A*b+a*B)/x^8-10/7*a^2*b^2*(A*b+a*B)/x^7-5/6*a*b^3*(A*b+2*a*B)/x^6-1/5*b^4*(A*b+5*a*B)/x^5-1/4*b^5*B/x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:178
  public void test0422() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^2, x]", //
        "-a^10*A/x+5*a^8*b*(9*A*b+2*a*B)*x+15/2*a^7*b^2*(8*A*b+3*a*B)*x^2+10*a^6*b^3*(7*A*b+4*a*B)*x^3+21/2*a^5*b^4*(6*A*b+5*a*B)*x^4+42/5*a^4*b^5*(5*A*b+6*a*B)*x^5+5*a^3*b^6*(4*A*b+7*a*B)*x^6+15/7*a^2*b^7*(3*A*b+8*a*B)*x^7+5/8*a*b^8*(2*A*b+9*a*B)*x^8+1/9*b^9*(A*b+10*a*B)*x^9+1/10*b^10*B*x^10+a^9*(10*A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:192
  public void test0423() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^16, x]", //
        "-1/15*A*(a+b*x)^11/(a*x^15)+1/210*(4*A*b-15*a*B)*(a+b*x)^11/(a^2*x^14)-1/910*b*(4*A*b-15*a*B)*(a+b*x)^11/(a^3*x^13)+1/5460*b^2*(4*A*b-15*a*B)*(a+b*x)^11/(a^4*x^12)-1/60060*b^3*(4*A*b-15*a*B)*(a+b*x)^11/(a^5*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:436
  public void test0424() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/Sqrt[x], x]", //
        "2/3*a*(2*A*b+a*B)*x^(3/2)+2/5*b*(A*b+2*a*B)*x^(5/2)+2/7*b^2*B*x^(7/2)+2*a^2*A*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:453
  public void test0425() {
    check( //
        "Integrate[x^(3/2)*(A+B*x)/(a+b*x), x]", //
        "2/3*(A*b-a*B)*x^(3/2)/b^2+2/5*B*x^(5/2)/b+2*a^(3/2)*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/b^(7/2)-2*a*(A*b-a*B)*Sqrt[x]/b^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:584
  public void test0426() {
    check( //
        "Integrate[x^3*(c+d*x)^(5/2)/(a+b*x), x]", //
        "-2/3*a^3*(b*c-a*d)*(c+d*x)^(3/2)/b^5-2/5*a^3*(c+d*x)^(5/2)/b^4+2/7*(b^2*c^2+a*b*c*d+a^2*d^2)*(c+d*x)^(7/2)/(b^3*d^3)-2/9*(2*b*c+a*d)*(c+d*x)^(9/2)/(b^2*d^3)+2/11*(c+d*x)^(11/2)/(b*d^3)+2*a^3*(b*c-a*d)^(5/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x]/Sqrt[b*c-a*d]]/b^(13/2)-2*a^3*(b*c-a*d)^2*Sqrt[c+d*x]/b^6");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:639
  public void test0427() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/x^(11/2), x]", //
        "-2/9*A*(a+b*x)^(5/2)/(a*x^(9/2))+2/63*(4*A*b-9*a*B)*(a+b*x)^(5/2)/(a^2*x^(7/2))-4/315*b*(4*A*b-9*a*B)*(a+b*x)^(5/2)/(a^3*x^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:653
  public void test0428() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/x^(17/2), x]", //
        "-2/15*A*(a+b*x)^(7/2)/(a*x^(15/2))+2/195*(8*A*b-15*a*B)*(a+b*x)^(7/2)/(a^2*x^(13/2))-4/715*b*(8*A*b-15*a*B)*(a+b*x)^(7/2)/(a^3*x^(11/2))+16/6435*b^2*(8*A*b-15*a*B)*(a+b*x)^(7/2)/(a^4*x^(9/2))-32/45045*b^3*(8*A*b-15*a*B)*(a+b*x)^(7/2)/(a^5*x^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:703
  public void test0429() {
    check( //
        "Integrate[Sqrt[a+b*x]*Sqrt[c+d*x]/x^4, x]", //
        "-1/3*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(a*c*x^3)-1/8*(b*c-a*d)^2*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(5/2))+1/4*(b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^2*x^2)+1/8*(b^2/a^2-d^2/c^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:733
  public void test0430() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x^3*Sqrt[c+d*x]), x]", //
        "1/4*(b*c-a*d)*(b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(5/2))-1/2*(a+b*x)^(3/2)*Sqrt[c+d*x]/(a*c*x^2)+1/4*(b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:747
  public void test0431() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x^2*(c+d*x)^(5/2)), x]", //
        "-(b*c-5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(7/2)*Sqrt[a])-5/3*d*Sqrt[a+b*x]/(c^2*(c+d*x)^(3/2))-Sqrt[a+b*x]/(c*x*(c+d*x)^(3/2))-1/3*d*(13*b*c-15*a*d)*Sqrt[a+b*x]/(c^3*(b*c-a*d)*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:765
  public void test0432() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x, x]", //
        "1/3*(a+b*x)^(3/2)*(c+d*x)^(3/2)-2*a^(3/2)*c^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]-1/8*(b*c+a*d)*(b^2*c^2-10*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*d^(3/2))+1/4*(b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/d+1/8*(8*a*c-b*c^2/d+a^2*d/b)*Sqrt[a+b*x]*Sqrt[c+d*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:779
  public void test0433() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^6, x]", //
        "-1/5*(a+b*x)^(3/2)*(c+d*x)^(7/2)/(c*x^5)+3/128*(b*c-a*d)^5*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(5/2))+1/64*(b*c-a*d)^3*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*c^2*x^2)-1/80*(b*c-a*d)^2*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*c^2*x^3)-3/40*(b*c-a*d)*(c+d*x)^(7/2)*Sqrt[a+b*x]/(c^2*x^4)-3/128*(b*c-a*d)^4*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:795
  public void test0434() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^2*(c+d*x)^(3/2)), x]", //
        "-3*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(5/2)-(a+b*x)^(3/2)/(c*x*Sqrt[c+d*x])+3*(b*c-a*d)*Sqrt[a+b*x]/(c^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:813
  public void test0435() {
    check( //
        "Integrate[(a+b*x)^(5/2)*Sqrt[c+d*x]/x^3, x]", //
        "-1/4*(15*b^2*c^2+10*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(3/2)+b^(3/2)*(b*c+5*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[d]-1/4*(5*b*c+a*d)*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c*x)-1/2*(a+b*x)^(5/2)*Sqrt[c+d*x]/x^2+1/4*b*(11*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/c");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:843
  public void test0436() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^4*Sqrt[c+d*x]), x]", //
        "-5/8*(b*c-a*d)^3*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(7/2)*Sqrt[a])-5/12*(b*c-a*d)*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c^2*x^2)-1/3*(a+b*x)^(5/2)*Sqrt[c+d*x]/(c*x^3)-5/8*(b*c-a*d)^2*Sqrt[a+b*x]*Sqrt[c+d*x]/(c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:889
  public void test0437() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^6*Sqrt[a+b*x]), x]", //
        "1/128*(b*c-a*d)^3*(63*b^2*c^2+14*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(11/2)*c^(5/2))-1/5*c*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x^5)+1/40*c*(9*b*c-13*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*x^4)-1/240*(63*b^2*c^2-148*a*b*c*d+93*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*x^3)+1/960*(315*b^3*c^3-749*a*b^2*c^2*d+481*a^2*b*c*d^2-15*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c*x^2)-1/1920*(945*b^4*c^4-2310*a*b^3*c^3*d+1564*a^2*b^2*c^2*d^2-90*a^3*b*c*d^3-45*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^5*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:905
  public void test0438() {
    check( //
        "Integrate[1/(Sqrt[a+b*x]*Sqrt[c+d*x]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:919
  public void test0439() {
    check( //
        "Integrate[x^2/((c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(d^(5/2)*Sqrt[b])+2/3*c^2*Sqrt[a+b*x]/(d^2*(b*c-a*d)*(c+d*x)^(3/2))-4/3*c*(2*b*c-3*a*d)*Sqrt[a+b*x]/(d^2*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:937
  public void test0440() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^2*(a+b*x)^(3/2)), x]", //
        "3*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/a^(5/2)-(c+d*x)^(3/2)/(a*x*Sqrt[a+b*x])-3*(b*c-a*d)*Sqrt[c+d*x]/(a^2*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:953
  public void test0441() {
    check( //
        "Integrate[x^2/((a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*d^(3/2))-2*a^2/(b^2*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])-2*(b^2*c^2+a^2*d^2)*Sqrt[a+b*x]/(b^2*d*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:967
  public void test0442() {
    check( //
        "Integrate[1/(x^3*(a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "-5/4*(3*b^2*c^2+6*a*b*c*d+7*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(9/2))+1/4*b*(15*b^2*c^2-7*a^2*d^2)/(a^3*c^2*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])+(-1/2)/(a*c*x^2*(c+d*x)^(3/2)*Sqrt[a+b*x])+1/4*(5*b*c+7*a*d)/(a^2*c^2*x*(c+d*x)^(3/2)*Sqrt[a+b*x])+1/12*d*(45*b^3*c^3-15*a*b^2*c^2*d-33*a^2*b*c*d^2+35*a^3*d^3)*Sqrt[a+b*x]/(a^3*c^3*(b*c-a*d)^2*(c+d*x)^(3/2))+1/12*d*(45*b^4*c^4-30*a*b^3*c^3*d-36*a^2*b^2*c^2*d^2+190*a^3*b*c*d^3-105*a^4*d^4)*Sqrt[a+b*x]/(a^3*c^4*(b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:987
  public void test0443() {
    check( //
        "Integrate[x^4/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "2/3*a*x^3/(b*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(5/2))+2*a*(3*b*c-a*d)*x^2/(b^2*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])-2/3*c*(c*(b*c+a*d)*(3*b^2*c^2-14*a*b*c*d+3*a^2*d^2)+2*d*(2*b^3*c^3-a*b^2*c^2*d-12*a^2*b*c*d^2+3*a^3*d^3)*x)*Sqrt[a+b*x]/(b^2*d^2*(b*c-a*d)^4*(c+d*x)^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1034
  public void test0444() {
    check( //
        "Integrate[x*Sqrt[1-x]*Sqrt[1+x], x]", //
        "-1/3*(1-x)^(3/2)*(1+x)^(3/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1256
  public void test0445() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/(d+e*x), x]", //
        "b*(b*d-a*e)*(B*d-A*e)*x/e^3-1/2*(B*d-A*e)*(a+b*x)^2/e^2+1/3*B*(a+b*x)^3/(b*e)-(b*d-a*e)^2*(B*d-A*e)*Log[d+e*x]/e^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1270
  public void test0446() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x), x]", //
        "-b*(b*d-a*e)^2*(B*d-A*e)*x/e^4+1/2*(b*d-a*e)*(B*d-A*e)*(a+b*x)^2/e^3-1/3*(B*d-A*e)*(a+b*x)^3/e^2+1/4*B*(a+b*x)^4/(b*e)+(b*d-a*e)^3*(B*d-A*e)*Log[d+e*x]/e^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1298
  public void test0447() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^10, x]", //
        "-1/9*(B*d-A*e)*(a+b*x)^7/(e*(b*d-a*e)*(d+e*x)^9)+1/72*(7*b*B*d+2*A*b*e-9*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^2*(d+e*x)^8)+1/504*b*(7*b*B*d+2*A*b*e-9*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^3*(d+e*x)^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1326
  public void test0448() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^9, x]", //
        "5*b^8*(b*d-a*e)*(11*b*B*d-2*A*b*e-9*a*B*e)*x/e^11+1/8*(b*d-a*e)^10*(B*d-A*e)/(e^12*(d+e*x)^8)-1/7*(b*d-a*e)^9*(11*b*B*d-10*A*b*e-a*B*e)/(e^12*(d+e*x)^7)+5/6*b*(b*d-a*e)^8*(11*b*B*d-9*A*b*e-2*a*B*e)/(e^12*(d+e*x)^6)-3*b^2*(b*d-a*e)^7*(11*b*B*d-8*A*b*e-3*a*B*e)/(e^12*(d+e*x)^5)+15/2*b^3*(b*d-a*e)^6*(11*b*B*d-7*A*b*e-4*a*B*e)/(e^12*(d+e*x)^4)-14*b^4*(b*d-a*e)^5*(11*b*B*d-6*A*b*e-5*a*B*e)/(e^12*(d+e*x)^3)+21*b^5*(b*d-a*e)^4*(11*b*B*d-5*A*b*e-6*a*B*e)/(e^12*(d+e*x)^2)-30*b^6*(b*d-a*e)^3*(11*b*B*d-4*A*b*e-7*a*B*e)/(e^12*(d+e*x))-1/2*b^9*(11*b*B*d-A*b*e-10*a*B*e)*(d+e*x)^2/e^12+1/3*b^10*B*(d+e*x)^3/e^12-15*b^7*(b*d-a*e)^2*(11*b*B*d-3*A*b*e-8*a*B*e)*Log[d+e*x]/e^12");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1342
  public void test0449() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^5/(a+b*x), x]", //
        "(A*b-a*B)*e*(b*d-a*e)^4*x/b^6+1/2*(A*b-a*B)*(b*d-a*e)^3*(d+e*x)^2/b^5+1/3*(A*b-a*B)*(b*d-a*e)^2*(d+e*x)^3/b^4+1/4*(A*b-a*B)*(b*d-a*e)*(d+e*x)^4/b^3+1/5*(A*b-a*B)*(d+e*x)^5/b^2+1/6*B*(d+e*x)^6/(b*e)+(A*b-a*B)*(b*d-a*e)^5*Log[a+b*x]/b^7");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2187
  public void test0450() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^5/(3+5*x), x]", //
        "2/46875*(1-2*x)^(3/2)-4774713/250000*(1-2*x)^(5/2)+806121/35000*(1-2*x)^(7/2)-5673/500*(1-2*x)^(9/2)+5751/2200*(1-2*x)^(11/2)-243/1040*(1-2*x)^(13/2)-22/78125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/78125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2539
  public void test0451() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/(d+e*x)^(9/2), x]", //
        "-2/7*(B*d-A*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)*(d+e*x)^(7/2))+2/35*(5*b*B*d+2*A*b*e-7*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^2*(d+e*x)^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2553
  public void test0452() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/(d+e*x)^(13/2), x]", //
        "-2/11*(B*d-A*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)*(d+e*x)^(11/2))+2/99*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^2*(d+e*x)^(9/2))+4/693*b*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^3*(d+e*x)^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2645
  public void test0453() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2)), x]", //
        "17951/28*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-2615/28*Sqrt[1-2*x]/Sqrt[3+5*x]+1/2*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+173/28*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2677
  public void test0454() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^4, x]", //
        "-1/9*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-19573/4536*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+20/81*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+37/36*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-661/1512*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2691
  public void test0455() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^6, x]", //
        "1/5*(1-2*x)^(3/2)*(3+5*x)^(7/2)/(2+3*x)^5-483153/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1331/3136*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-121/560*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3+33/40*(3+5*x)^(7/2)*Sqrt[1-2*x]/(2+3*x)^4-43923/43904*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2721
  public void test0456() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^3*(3+5*x)^(5/2)), x]", //
        "-40787/4*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-655/4*Sqrt[1-2*x]/(3+5*x)^(3/2)+7/6*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+235/12*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+17825/12*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2783
  public void test0457() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^2*(3+5*x)^(3/2)), x]", //
        "-8/45*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]+665/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+7/3*(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-1111/15*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2797
  public void test0458() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^5*(3+5*x)^(5/2)), x]", //
        "7/12*(1-2*x)^(3/2)/((2+3*x)^4*(3+5*x)^(3/2))-519421265/448*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-25024175/1344*Sqrt[1-2*x]/(3+5*x)^(3/2)+847/72*Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2))+36817/288*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+2992825/1344*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+227000875/1344*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2829
  public void test0459() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^3*Sqrt[1-2*x]), x]", //
        "17687/5292*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+25/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+1/42*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+239/1764*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2845
  public void test0460() {
    check( //
        "Integrate[(2+3*x)^4/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "143283/8000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/55*(2+3*x)^3*Sqrt[1-2*x]/Sqrt[3+5*x]-21/550*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-21/88000*(8987+3660*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2859
  public void test0461() {
    check( //
        "Integrate[1/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-2/33*Sqrt[1-2*x]/(3+5*x)^(3/2)-8/363*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2877
  public void test0462() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^3), x]", //
        "-1585/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])-15/98*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+15/1372*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2907
  public void test0463() {
    check( //
        "Integrate[(2+3*x)/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-3*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2921
  public void test0464() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^2*(3+5*x)^(3/2)), x]", //
        "999/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-58/539)/(Sqrt[1-2*x]*Sqrt[3+5*x])+3/7/((2+3*x)*Sqrt[1-2*x]*Sqrt[3+5*x])-17735/5929*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2953
  public void test0465() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^3), x]", //
        "-5/28*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^2)+5/42*Sqrt[3+5*x]/Sqrt[1-2*x]-3/14*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])-5/28*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2969
  public void test0466() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "1593/40*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/33*(2+3*x)^2*Sqrt[3+5*x]/(1-2*x)^(3/2)-1/14520*(95621-33462*x)*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2983
  public void test0467() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)*(3+5*x)^(3/2)), x]", //
        "54/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/231/((1-2*x)^(3/2)*Sqrt[3+5*x])+956/17787/(Sqrt[1-2*x]*Sqrt[3+5*x])-42230/195657*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2999
  public void test0468() {
    check( //
        "Integrate[1/(Sqrt[a+b*x]*Sqrt[c+d*x]*Sqrt[e+b*(-1+e)*x/a]), x]", //
        "2*EllipticF[ArcSin[Sqrt[1-e]*Sqrt[a+b*x]/Sqrt[a]],-a*d/((b*c-a*d)*(1-e))]*Sqrt[a]*Sqrt[b*(c+d*x)/(b*c-a*d)]/(b*Sqrt[1-e]*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3019
  public void test0469() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(9/2), x]", //
        "-220076/36015*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-6584/36015*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/21*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+74/735*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+3184/5145*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+220076/36015*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3033
  public void test0470() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(3/2), x]", //
        "-1/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-3/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-2/3*(3+5*x)^(5/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+4/3*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3049
  public void test0471() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-136/21*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4/21*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+136/21*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3063
  public void test0472() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(5/2), x]", //
        "62/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+8/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-62/165*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3081
  public void test0473() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2), x]", //
        "2/65*(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2)-51601293223/460687500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-776112041/230343750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+601/160875*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+178/10725*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-11725073/56306250*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-18034/625625*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-776112041/506756250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3095
  public void test0474() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(3/2), x]", //
        "-2894/2835*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1061/2835*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(1-2*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[2+3*x]+202/63*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-32/63*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1061/567*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3111
  public void test0475() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(9/2)*Sqrt[3+5*x]), x]", //
        "-1255552/5145*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-37768/5145*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+388/105*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+18068/735*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+1255552/5145*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3125
  public void test0476() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((3+5*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-148/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-52/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-22/15*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+148/15*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3143
  public void test0477() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2), x]", //
        "62/2925*(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2)+2/75*(1-2*x)^(5/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2)-1764163292393/20730937500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-13267820528/5182734375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+142391/7239375*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+3698/482625*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-400516993/2533781250*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-569519/28153125*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-13267820528/11402015625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3157
  public void test0478() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(3/2), x]", //
        "-25111/382725*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-310399/382725*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(1-2*x)^(5/2)*(3+5*x)^(5/2)/Sqrt[2+3*x]-40/81*(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]+64628/8505*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2108/1701*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-310399/76545*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3173
  public void test0479() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(7/2)*Sqrt[3+5*x]), x]", //
        "-16564/135*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-496/135*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/15*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(5/2)+1736/135*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+16564/135*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3187
  public void test0480() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(7/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(5/2)*(2+3*x)^(7/2)/(3+5*x)^(3/2)-1065118/4921875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-595387/4921875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-442/75*(1-2*x)^(3/2)*(2+3*x)^(7/2)/Sqrt[3+5*x]+373022/196875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+59662/7875*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-524/225*(2+3*x)^(7/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+500501/984375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3205
  public void test0481() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-62/147*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-8/147*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/21*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+62/147*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3219
  public void test0482() {
    check( //
        "Integrate[(3+5*x)^(5/2)/(Sqrt[1-2*x]*Sqrt[2+3*x]), x]", //
        "-4141/270*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-62/135*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/3*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-62/27*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3251
  public void test0483() {
    check( //
        "Integrate[1/((2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-2*EllipticE[ArcSin[Sqrt[5/11]*Sqrt[1-2*x]],33/35]*Sqrt[5/7]+6/7*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3265
  public void test0484() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "272/275*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-202/275*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/165*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-272/1815*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3289
  public void test0485() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "7279/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+4817/250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+(2+3*x)^(5/2)*Sqrt[3+5*x]/Sqrt[1-2*x]+9/5*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+419/50*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3303
  public void test0486() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(7/2)), x]", //
        "-338/12005*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-992/12005*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-163/245*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-458/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+338/12005*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3319
  public void test0487() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "34/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+1/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3333
  public void test0488() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(7/2)*(3+5*x)^(3/2)), x]", //
        "46585232/132055*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+1400888/132055*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77/((2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x])+138/2695*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+14928/18865*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+2101332/132055*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-46585232/290521*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3351
  public void test0489() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "-133/2*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*(2+3*x)^(3/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-67/33*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3365
  public void test0490() {
    check( //
        "Integrate[(2+3*x)^(7/2)*(3+5*x)^(5/2)/(1-2*x)^(5/2), x]", //
        "1/3*(2+3*x)^(7/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2)-174654791/12600*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1313411/3150*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-203/33*(2+3*x)^(5/2)*(3+5*x)^(5/2)/Sqrt[1-2*x]-225/22*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-1310203/4620*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-6277/154*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1313411/630*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3381
  public void test0491() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*Sqrt[2+3*x]*Sqrt[3+5*x]), x]", //
        "272/539*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-202/539*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)+544/17787*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3395
  public void test0492() {
    check( //
        "Integrate[(2+3*x)^(13/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^(11/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2))-51601293223/83187500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-776112041/41593750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-294/121*(2+3*x)^(9/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])+4373/19965*(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)+150812/1098075*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-31887029/18301250*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-371279941/45753125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:59
  public void test0493() {
    check( //
        "Integrate[(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x], x]", //
        "5592499/3888*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]-17746949/29160*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-5256763/97200*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-8141/2700*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-61/270*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+2/45*(7+5*x)^3*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:94
  public void test0494() {
    check( //
        "Integrate[(7+5*x)/(Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "13*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[3/22]*Sqrt[5-2*x]/Sqrt[-5+2*x]-5/6*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]");
  }

  // 1.1.1.7 P(x) (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:30
  public void test0495() {
    check( //
        "Integrate[(A+B*x)/((a+b*x)^(3/2)*Sqrt[c+d*x]*Sqrt[e+f*x]*Sqrt[g+h*x]), x]", //
        "2*(A*b-a*B)*d*Sqrt[a+b*x]*Sqrt[e+f*x]*Sqrt[g+h*x]/((b*c-a*d)*(b*e-a*f)*(b*g-a*h)*Sqrt[c+d*x])-2*b*(A*b-a*B)*Sqrt[c+d*x]*Sqrt[e+f*x]*Sqrt[g+h*x]/((b*c-a*d)*(b*e-a*f)*(b*g-a*h)*Sqrt[a+b*x])+2*(B*c-A*d)*EllipticF[ArcSin[Sqrt[b*g-a*h]*Sqrt[e+f*x]/(Sqrt[f*g-e*h]*Sqrt[a+b*x])],-(b*c-a*d)*(f*g-e*h)/((d*e-c*f)*(b*g-a*h))]*Sqrt[(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]*Sqrt[g+h*x]/((b*c-a*d)*Sqrt[b*g-a*h]*Sqrt[f*g-e*h]*Sqrt[c+d*x]*Sqrt[-(b*e-a*f)*(g+h*x)/((f*g-e*h)*(a+b*x))])-2*(A*b-a*B)*EllipticE[ArcSin[Sqrt[d*g-c*h]*Sqrt[e+f*x]/(Sqrt[f*g-e*h]*Sqrt[c+d*x])],(b*c-a*d)*(f*g-e*h)/((b*e-a*f)*(d*g-c*h))]*Sqrt[d*g-c*h]*Sqrt[f*g-e*h]*Sqrt[a+b*x]*Sqrt[-(d*e-c*f)*(g+h*x)/((f*g-e*h)*(c+d*x))]/((b*c-a*d)*(b*e-a*f)*(b*g-a*h)*Sqrt[(d*e-c*f)*(a+b*x)/((b*e-a*f)*(c+d*x))]*Sqrt[g+h*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:41
  public void test0496() {
    check( //
        "Integrate[x^7*(a+b*x^2)^3, x]", //
        "1/8*a^3*x^8+3/10*a^2*b*x^10+1/4*a*b^2*x^12+1/14*b^3*x^14");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:97
  public void test0497() {
    check( //
        "Integrate[x^11*(a+b*x^2)^8, x]", //
        "-1/18*a^5*(a+b*x^2)^9/b^6+1/4*a^4*(a+b*x^2)^10/b^6-5/11*a^3*(a+b*x^2)^11/b^6+5/12*a^2*(a+b*x^2)^12/b^6-5/26*a*(a+b*x^2)^13/b^6+1/28*(a+b*x^2)^14/b^6");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:111
  public void test0498() {
    check( //
        "Integrate[(a+b*x^2)^8/x^17, x]", //
        "-1/16*a^8/x^16-4/7*a^7*b/x^14-7/3*a^6*b^2/x^12-28/5*a^5*b^3/x^10-35/4*a^4*b^4/x^8-28/3*a^3*b^5/x^6-7*a^2*b^6/x^4-4*a*b^7/x^2+b^8*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:155
  public void test0499() {
    check( //
        "Integrate[1/(x^7*(a+b*x^2)), x]", //
        "(-1/6)/(a*x^6)+1/4*b/(a^2*x^4)-1/2*b^2/(a^3*x^2)-b^3*Log[x]/a^4+1/2*b^3*Log[a+b*x^2]/a^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:211
  public void test0500() {
    check( //
        "Integrate[x^13/(a+b*x^2)^10, x]", //
        "1/18*x^14/(a*(a+b*x^2)^9)+1/72*x^14/(a^2*(a+b*x^2)^8)+1/504*x^14/(a^3*(a+b*x^2)^7)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:267
  public void test0501() {
    check( //
        "Integrate[1/(x*(-1+b*x^2)), x]", //
        "-Log[x]+1/2*Log[1-b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:539
  public void test0502() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^(3/2)), x]", //
        "-ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/a^(3/2)+1/(a*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:553
  public void test0503() {
    check( //
        "Integrate[1/(x^4*(a+b*x^2)^(5/2)), x]", //
        "(-1/3)/(a*x^3*(a+b*x^2)^(3/2))+2*b/(a^2*x*(a+b*x^2)^(3/2))+8/3*b^2*x/(a^3*(a+b*x^2)^(3/2))+16/3*b^2*x/(a^4*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:778
  public void test0504() {
    check( //
        "Integrate[1/(a+b*x^2)^(2/3), x]", //
        "-3^(3/4)*(a^(1/3)-(a+b*x^2)^(1/3))*EllipticF[ArcSin[(-(a+b*x^2)^(1/3)+a^(1/3)*(1+Sqrt[3]))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*Sqrt[(a^(2/3)+a^(1/3)*(a+b*x^2)^(1/3)+(a+b*x^2)^(2/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2-Sqrt[3]]/(b*x*Sqrt[-a^(1/3)*(a^(1/3)-(a+b*x^2)^(1/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:913
  public void test0505() {
    check( //
        "Integrate[x^4/(a+b*x^2)^(5/4), x]", //
        "-12/5*a*x/(b^2*(a+b*x^2)^(1/4))+2/5*x^3/(b*(a+b*x^2)^(1/4))+24/5*a^(3/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(b^(5/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:927
  public void test0506() {
    check( //
        "Integrate[1/(a+b*x^2)^(9/4), x]", //
        "2/5*x/(a*(a+b*x^2)^(5/4))+6/5*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(a^(3/2)*(a+b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1015
  public void test0507() {
    check( //
        "Integrate[(a-b*x^2)^(1/4)/(c*x)^(15/2), x]", //
        "-2/5*(a-b*x^2)^(5/4)/(a*c*(c*x)^(13/2))+16/45*(a-b*x^2)^(9/4)/(a^2*c*(c*x)^(13/2))-64/585*(a-b*x^2)^(13/4)/(a^3*c*(c*x)^(13/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1059
  public void test0508() {
    check( //
        "Integrate[1/((c*x)^(11/2)*(a-b*x^2)^(3/4)), x]", //
        "-2*(a-b*x^2)^(1/4)/(a*c*(c*x)^(9/2))+16/5*(a-b*x^2)^(5/4)/(a^2*c*(c*x)^(9/2))-64/45*(a-b*x^2)^(9/4)/(a^3*c*(c*x)^(9/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:64
  public void test0509() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(c+d*x^2), x]", //
        "ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]*Sqrt[b]/d-ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]*Sqrt[b*c-a*d]/(d*Sqrt[c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:94
  public void test0510() {
    check( //
        "Integrate[1/(a+b*x^2)^(1/2), x]", //
        "ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:123
  public void test0511() {
    check( //
        "Integrate[1/((1+x^2)*Sqrt[1-x^2]), x]", //
        "ArcTan[x*Sqrt[2]/Sqrt[1-x^2]]/Sqrt[2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:214
  public void test0512() {
    check( //
        "Integrate[Sqrt[3-6*x^2]*Sqrt[2+4*x^2], x]", //
        "2*EllipticF[ArcSin[x*Sqrt[2]],-1]/Sqrt[3]+x*Sqrt[2/3]*Sqrt[1-4*x^4]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:228
  public void test0513() {
    check( //
        "Integrate[Sqrt[4-x^2]/Sqrt[2+3*x^2], x]", //
        "-1/3*EllipticE[ArcSin[1/2*x],-6]*Sqrt[2]+7/3*EllipticF[ArcSin[1/2*x],-6]*Sqrt[2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:301
  public void test0514() {
    check( //
        "Integrate[Sqrt[a-b*x^2]/Sqrt[-c+d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],b*c/(a*d)]*Sqrt[c]*Sqrt[a-b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1-b*x^2/a]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:315
  public void test0515() {
    check( //
        "Integrate[Sqrt[c-d*x^2]/Sqrt[a-b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[c-d*x^2]/(Sqrt[b]*Sqrt[a-b*x^2]*Sqrt[1-d*x^2/c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:25
  public void test0516() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x, x]", //
        "a*A*b*x^2+1/4*A*b^2*x^4+1/6*B*(a+b*x^2)^3/b+a^2*A*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:53
  public void test0517() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^10, x]", //
        "-1/9*a^5*A/x^9-1/7*a^4*(5*A*b+a*B)/x^7-a^3*b*(2*A*b+a*B)/x^5-10/3*a^2*b^2*(A*b+a*B)/x^3-5*a*b^3*(A*b+2*a*B)/x+b^4*(A*b+5*a*B)*x+1/3*b^5*B*x^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:69
  public void test0518() {
    check( //
        "Integrate[x^6*(A+B*x^2)/(a+b*x^2), x]", //
        "a^2*(A*b-a*B)*x/b^4-1/3*a*(A*b-a*B)*x^3/b^3+1/5*(A*b-a*B)*x^5/b^2+1/7*B*x^7/b-a^(5/2)*(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:83
  public void test0519() {
    check( //
        "Integrate[(A+B*x^2)/(x^8*(a+b*x^2)), x]", //
        "-1/7*A/(a*x^7)+1/5*(A*b-a*B)/(a^2*x^5)-1/3*b*(A*b-a*B)/(a^3*x^3)+b^2*(A*b-a*B)/(a^4*x)+b^(5/2)*(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/a^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:111
  public void test0520() {
    check( //
        "Integrate[x^10*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "2*a^2*(3*A*b-5*a*B)*x/b^6-a*(A*b-2*a*B)*x^3/b^5+1/5*(A*b-3*a*B)*x^5/b^4+1/7*B*x^7/b^3-1/4*a^4*(A*b-a*B)*x/(b^6*(a+b*x^2)^2)+1/8*a^3*(17*A*b-21*a*B)*x/(b^6*(a+b*x^2))-9/8*a^(5/2)*(7*A*b-11*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(13/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:153
  public void test0521() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(x^3*(a+b*x^2)^3), x]", //
        "-1/2*c/(a^2*x^2)-1/2*b*c/(a^2*(a+b*x^2))-2*b*c*Log[x]/a^3+b*c*Log[a+b*x^2]/a^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:187
  public void test0522() {
    check( //
        "Integrate[x^4*(a+b*x^2)^2/(c+d*x^2), x]", //
        "-c*(b*c-a*d)^2*x/d^4+1/3*(b*c-a*d)^2*x^3/d^3-1/5*b*(b*c-2*a*d)*x^5/d^2+1/7*b^2*x^7/d+c^(3/2)*(b*c-a*d)^2*ArcTan[x*Sqrt[d]/Sqrt[c]]/d^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:201
  public void test0523() {
    check( //
        "Integrate[x^2*(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "-1/2*(b*c-a*d)*(5*b*c-a*d)*x/(c*d^3)+1/3*b^2*x^3/d^2+1/2*(b*c-a*d)^2*x^3/(c*d^2*(c+d*x^2))+1/2*(b*c-a*d)*(5*b*c-a*d)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(d^(7/2)*Sqrt[c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:265
  public void test0524() {
    check( //
        "Integrate[1/(x^5*(a+b*x^2)*(c+d*x^2)), x]", //
        "(-1/4)/(a*c*x^4)+1/2*(b*c+a*d)/(a^2*c^2*x^2)+(b^2*c^2+a*b*c*d+a^2*d^2)*Log[x]/(a^3*c^3)-1/2*b^3*Log[a+b*x^2]/(a^3*(b*c-a*d))+1/2*d^3*Log[c+d*x^2]/(c^3*(b*c-a*d))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:327
  public void test0525() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^2*(c+d*x^2)), x]", //
        "1/2*b/(a*(b*c-a*d)*(a+b*x^2))+Log[x]/(a^2*c)-1/2*b*(b*c-2*a*d)*Log[a+b*x^2]/(a^2*(b*c-a*d)^2)-1/2*d^2*Log[c+d*x^2]/(c*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:341
  public void test0526() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)^2*(c+d*x^2)^2), x]", //
        "(-1/2)/(a^2*c^2*x^2)-1/2*b^3/(a^2*(b*c-a*d)^2*(a+b*x^2))-1/2*d^3/(c^2*(b*c-a*d)^2*(c+d*x^2))-2*(b*c+a*d)*Log[x]/(a^3*c^3)+b^3*(b*c-2*a*d)*Log[a+b*x^2]/(a^3*(b*c-a*d)^3)+d^3*(2*b*c-a*d)*Log[c+d*x^2]/(c^3*(b*c-a*d)^3)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:417
  public void test0527() {
    check( //
        "Integrate[x^(5/2)*(A+B*x^2)/(a+b*x^2), x]", //
        "2/3*(A*b-a*B)*x^(3/2)/b^2+2/7*B*x^(7/2)/b+a^(3/4)*(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(11/4)*Sqrt[2])-a^(3/4)*(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(11/4)*Sqrt[2])-1/2*a^(3/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(11/4)*Sqrt[2])+1/2*a^(3/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(11/4)*Sqrt[2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:449
  public void test0528() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^(3/2), x]", //
        "2/3*a*(2*b*c+a*d)*x^(3/2)+2/7*b*(b*c+2*a*d)*x^(7/2)+2/11*b^2*d*x^(11/2)-2*a^2*c/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:611
  public void test0529() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)*(A+B*x^2)/x^10, x]", //
        "-1/9*A*(a+b*x^2)^(5/2)/(a*x^9)+1/63*(4*A*b-9*a*B)*(a+b*x^2)^(5/2)/(a^2*x^7)-2/315*b*(4*A*b-9*a*B)*(a+b*x^2)^(5/2)/(a^3*x^5)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:745
  public void test0530() {
    check( //
        "Integrate[x^2*(a+b*x^2)^2/(c+d*x^2)^(5/2), x]", //
        "1/3*(b*c-a*d)^2*x^3/(c*d^2*(c+d*x^2)^(3/2))-1/2*b*(5*b*c-4*a*d)*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(7/2)+2*b*(b*c-a*d)*x/(d^3*Sqrt[c+d*x^2])+1/2*b^2*x*Sqrt[c+d*x^2]/d^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:797
  public void test0531() {
    check( //
        "Integrate[x^3/((a+b*x^2)*Sqrt[c+d*x^2]), x]", //
        "a*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/(b^(3/2)*Sqrt[b*c-a*d])+Sqrt[c+d*x^2]/(b*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1116
  public void test0532() {
    check( //
        "Integrate[x^5/((a+b*x^2)^(1/2)*Sqrt[c+d*x^2]), x]", //
        "-1/8*(4*a*b*c*d-3*(b*c+a*d)^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]/(b^(5/2)*d^(5/2))-3/8*(b*c+a*d)*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(b^2*d^2)+1/4*x^2*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(b*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1130
  public void test0533() {
    check( //
        "Integrate[x^5/((a+b*x^2)^(5/2)*Sqrt[c+d*x^2]), x]", //
        "ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]/(b^(5/2)*Sqrt[d])-1/3*a^2*Sqrt[c+d*x^2]/(b^2*(b*c-a*d)*(a+b*x^2)^(3/2))+2/3*a*(3*b*c-2*a*d)*Sqrt[c+d*x^2]/(b^2*(b*c-a*d)^2*Sqrt[a+b*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1144
  public void test0534() {
    check( //
        "Integrate[x^2/(Sqrt[4-x^2]*Sqrt[2+3*x^2]), x]", //
        "1/3*EllipticE[ArcSin[1/2*x],-6]*Sqrt[2]-1/3*EllipticF[ArcSin[1/2*x],-6]*Sqrt[2]");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:74
  public void test0535() {
    check( //
        "Integrate[(-b+2*c*x^2-Sqrt[b^2-4*a*c])/(Sqrt[1+2*c*x^2/(-b-Sqrt[b^2-4*a*c])]*Sqrt[1+2*c*x^2/(-b+Sqrt[b^2-4*a*c])]), x]", //
        "-EllipticE[ArcSin[x*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]],(b-Sqrt[b^2-4*a*c])/(b+Sqrt[b^2-4*a*c])]*(b+Sqrt[b^2-4*a*c])*Sqrt[b-Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:118
  public void test0536() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x+C*x^2+D*x^3)/x^2, x]", //
        "-a^2*A/x+a*(2*A*b+a*C)*x+a*b*B*x^2+1/3*b*(A*b+2*a*C)*x^3+1/4*b^2*B*x^4+1/5*b^2*C*x^5+1/6*D*(a+b*x^2)^3/b+a^2*B*Log[x]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:134
  public void test0537() {
    check( //
        "Integrate[x*(A+B*x+C*x^2+D*x^3)/(a+b*x^2), x]", //
        "(b*B-a*D)*x/b^2+1/2*C*x^2/b+1/3*D*x^3/b+1/2*(A*b-a*C)*Log[a+b*x^2]/b^2-(b*B-a*D)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(5/2)");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:148
  public void test0538() {
    check( //
        "Integrate[x^3*(A+B*x+C*x^2+D*x^3)/(a+b*x^2)^3, x]", //
        "-3/8*(b*B-5*a*D)*x/(a*b^3)-1/4*x^3*(a*(B-a*D/b)-(A*b-a*C)*x)/(a*b*(a+b*x^2)^2)-1/8*x^2*(4*a*C-(3*b*B-7*a*D)*x)/(a*b^2*(a+b*x^2))+1/2*C*Log[a+b*x^2]/b^3+3/8*(b*B-5*a*D)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(7/2)*Sqrt[a])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:186
  public void test0539() {
    check( //
        "Integrate[(c+d*x^2+e*x^4+f*x^6)/(a+b*x^2), x]", //
        "(b^2*d-a*b*e+a^2*f)*x/b^3+1/3*(b*e-a*f)*x^3/b^2+1/5*f*x^5/b+(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(7/2)*Sqrt[a])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:162
  public void test0540() {
    check( //
        "Integrate[1/(b*x)^(2/3), x]", //
        "3*(b*x)^(1/3)/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:231
  public void test0541() {
    check( //
        "Integrate[1/(x^2*(a*x^n)^(1/n)), x]", //
        "(-1/2)/(x*(a*x^n)^(1/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:297
  public void test0542() {
    check( //
        "Integrate[(a+b*x^3)^3/x^4, x]", //
        "-1/3*a^3/x^3+a*b^2*x^3+1/6*b^3*x^6+3*a^2*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:353
  public void test0543() {
    check( //
        "Integrate[(a+b*x^3)^8/x^19, x]", //
        "-1/18*a^8/x^18-8/15*a^7*b/x^15-7/3*a^6*b^2/x^12-56/9*a^5*b^3/x^9-35/3*a^4*b^4/x^6-56/3*a^3*b^5/x^3+8/3*a*b^7*x^3+1/6*b^8*x^6+28*a^2*b^6*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:383
  public void test0544() {
    check( //
        "Integrate[1/(a+b*x^3), x]", //
        "1/3*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(1/3))-1/6*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(1/3))-ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:397
  public void test0545() {
    check( //
        "Integrate[x^11/(a+b*x^3)^3, x]", //
        "1/3*x^3/b^3+1/6*a^3/(b^4*(a+b*x^3)^2)-a^2/(b^4*(a+b*x^3))-a*Log[a+b*x^3]/b^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:463
  public void test0546() {
    check( //
        "Integrate[(a+b*x^3)^(3/2), x]", //
        "2/11*x*(a+b*x^3)^(3/2)+18/55*a*x*Sqrt[a+b*x^3]+18/55*3^(3/4)*a^2*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(1/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:597
  public void test0547() {
    check( //
        "Integrate[(a+b*x^3)^(1/3)/x^14, x]", //
        "-1/13*(a+b*x^3)^(4/3)/(a*x^13)+9/130*b*(a+b*x^3)^(4/3)/(a^2*x^10)-27/455*b^2*(a+b*x^3)^(4/3)/(a^3*x^7)+81/1820*b^3*(a+b*x^3)^(4/3)/(a^4*x^4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:740
  public void test0548() {
    check( //
        "Integrate[1/(x*(a+c*x^4)), x]", //
        "Log[x]/a-1/4*Log[a+c*x^4]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:754
  public void test0549() {
    check( //
        "Integrate[x/(a+c*x^4)^2, x]", //
        "1/4*x^2/(a*(a+c*x^4))+1/4*ArcTan[x^2*Sqrt[c]/Sqrt[a]]/(a^(3/2)*Sqrt[c])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:768
  public void test0550() {
    check( //
        "Integrate[x/(a+c*x^4)^3, x]", //
        "1/8*x^2/(a*(a+c*x^4)^2)+3/16*x^2/(a^2*(a+c*x^4))+3/16*ArcTan[x^2*Sqrt[c]/Sqrt[a]]/(a^(5/2)*Sqrt[c])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:878
  public void test0551() {
    check( //
        "Integrate[Sqrt[a+c*x^4]/x^15, x]", //
        "-1/14*(a+c*x^4)^(3/2)/(a*x^14)+2/35*c*(a+c*x^4)^(3/2)/(a^2*x^10)-4/105*c^2*(a+c*x^4)^(3/2)/(a^3*x^6)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:906
  public void test0552() {
    check( //
        "Integrate[(1+x^4)^(3/2), x]", //
        "1/7*x*(1+x^4)^(3/2)+2/7*x*Sqrt[1+x^4]+2/7*(1+x^2)*EllipticF[2*ArcTan[x],1/2]*Sqrt[(1+x^4)/(1+x^2)^2]/Sqrt[1+x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:922
  public void test0553() {
    check( //
        "Integrate[x/Sqrt[a+b*x^4], x]", //
        "1/2*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]/Sqrt[b]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:978
  public void test0554() {
    check( //
        "Integrate[1/(a+b*x^4)^(5/2), x]", //
        "1/6*x/(a*(a+b*x^4)^(3/2))+5/12*x/(a^2*Sqrt[a+b*x^4])+5/24*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(9/4)*b^(1/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1140
  public void test0555() {
    check( //
        "Integrate[x*(a+b*x^4)^(3/4), x]", //
        "3/5*a*x^2/(a+b*x^4)^(1/4)+1/5*x^2*(a+b*x^4)^(3/4)-3/5*a^(3/2)*(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]/((a+b*x^4)^(1/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1182
  public void test0556() {
    check( //
        "Integrate[(a+b*x^4)^(5/4)/x^18, x]", //
        "-1/17*(a+b*x^4)^(9/4)/(a*x^17)+8/221*b*(a+b*x^4)^(9/4)/(a^2*x^13)-32/1989*b^2*(a+b*x^4)^(9/4)/(a^3*x^9)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1212
  public void test0557() {
    check( //
        "Integrate[1/(a+b*x^4)^(1/4), x]", //
        "1/2*ArcTan[b^(1/4)*x/(a+b*x^4)^(1/4)]/b^(1/4)+1/2*ArcTanh[b^(1/4)*x/(a+b*x^4)^(1/4)]/b^(1/4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1268
  public void test0558() {
    check( //
        "Integrate[1/(x^11*(a+b*x^4)^(5/4)), x]", //
        "(-1/10)/(a*x^10*(a+b*x^4)^(1/4))+11/60*b/(a^2*x^6*(a+b*x^4)^(1/4))-77/120*b^2/(a^3*x^2*(a+b*x^4)^(1/4))-77/40*b^(5/2)*(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]/(a^(7/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1282
  public void test0559() {
    check( //
        "Integrate[1/(x^6*(a+b*x^4)^(5/4)), x]", //
        "(-1/5)/(a*x^5*(a+b*x^4)^(1/4))+6/5*b/(a^2*x*(a+b*x^4)^(1/4))-12/5*b^(3/2)*(1+a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]/(a^(5/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1314
  public void test0560() {
    check( //
        "Integrate[(a-b*x^4)^(1/4)/x^18, x]", //
        "-1/17*(a-b*x^4)^(5/4)/(a*x^17)-12/221*b*(a-b*x^4)^(5/4)/(a^2*x^13)-32/663*b^2*(a-b*x^4)^(5/4)/(a^3*x^9)-128/3315*b^3*(a-b*x^4)^(5/4)/(a^4*x^5)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1330
  public void test0561() {
    check( //
        "Integrate[1/(x*(a-b*x^4)^(1/4)), x]", //
        "1/2*ArcTan[(a-b*x^4)^(1/4)/a^(1/4)]/a^(1/4)-1/2*ArcTanh[(a-b*x^4)^(1/4)/a^(1/4)]/a^(1/4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1372
  public void test0562() {
    check( //
        "Integrate[x^2/(a-b*x^4)^(3/4), x]", //
        "-1/2*ArcTan[1-b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)]/(b^(3/4)*Sqrt[2])+1/2*ArcTan[1+b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)]/(b^(3/4)*Sqrt[2])+1/4*Log[1-b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)+x^2*Sqrt[b]/Sqrt[a-b*x^4]]/(b^(3/4)*Sqrt[2])-1/4*Log[1+b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)+x^2*Sqrt[b]/Sqrt[a-b*x^4]]/(b^(3/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1426
  public void test0563() {
    check( //
        "Integrate[x^14/(1+x^5), x]", //
        "-1/5*x^5+1/10*x^10+1/5*Log[1+x^5]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1461
  public void test0564() {
    check( //
        "Integrate[x^(3/2)/Sqrt[a+b*x^5], x]", //
        "2/5*ArcTanh[x^(5/2)*Sqrt[b]/Sqrt[a+b*x^5]]/Sqrt[b]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1499
  public void test0565() {
    check( //
        "Integrate[x/(a+b*x^6)^2, x]", //
        "1/6*x^2/(a*(a+b*x^6))+1/9*Log[a^(1/3)+b^(1/3)*x^2]/(a^(5/3)*b^(1/3))-1/18*Log[a^(2/3)-a^(1/3)*b^(1/3)*x^2+b^(2/3)*x^4]/(a^(5/3)*b^(1/3))-1/3*ArcTan[(a^(1/3)-2*b^(1/3)*x^2)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1531
  public void test0566() {
    check( //
        "Integrate[1/(x*(1+x^6)), x]", //
        "Log[x]-1/6*Log[1+x^6]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1621
  public void test0567() {
    check( //
        "Integrate[(a+b*x^7)^2/x^8, x]", //
        "-1/7*a^2/x^7+1/7*b^2*x^7+2*a*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1653
  public void test0568() {
    check( //
        "Integrate[1/(x*(a+b*x^8)), x]", //
        "Log[x]/a-1/8*Log[a+b*x^8]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1667
  public void test0569() {
    check( //
        "Integrate[1/(a-b*x^8), x]", //
        "1/4*ArcTan[b^(1/8)*x/a^(1/8)]/(a^(7/8)*b^(1/8))+1/4*ArcTanh[b^(1/8)*x/a^(1/8)]/(a^(7/8)*b^(1/8))-1/4*ArcTan[1-b^(1/8)*x*Sqrt[2]/a^(1/8)]/(a^(7/8)*b^(1/8)*Sqrt[2])+1/4*ArcTan[1+b^(1/8)*x*Sqrt[2]/a^(1/8)]/(a^(7/8)*b^(1/8)*Sqrt[2])-1/8*Log[a^(1/4)+b^(1/4)*x^2-a^(1/8)*b^(1/8)*x*Sqrt[2]]/(a^(7/8)*b^(1/8)*Sqrt[2])+1/8*Log[a^(1/4)+b^(1/4)*x^2+a^(1/8)*b^(1/8)*x*Sqrt[2]]/(a^(7/8)*b^(1/8)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1735
  public void test0570() {
    check( //
        "Integrate[x^5/Sqrt[1+x^8], x]", //
        "1/2*x^2*Sqrt[1+x^8]/(1+x^4)-1/2*(1+x^4)*EllipticE[2*ArcTan[x^2],1/2]*Sqrt[(1+x^8)/(1+x^4)^2]/Sqrt[1+x^8]+1/4*(1+x^4)*EllipticF[2*ArcTan[x^2],1/2]*Sqrt[(1+x^8)/(1+x^4)^2]/Sqrt[1+x^8]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1804
  public void test0571() {
    check( //
        "Integrate[x^5*Sqrt[9+x^12], x]", //
        "3/4*ArcSinh[1/3*x^6]+1/12*x^6*Sqrt[9+x^12]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1834
  public void test0572() {
    check( //
        "Integrate[(a+b/x)^2*x^5, x]", //
        "1/4*b^2*x^4+2/5*a*b*x^5+1/6*a^2*x^6");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1862
  public void test0573() {
    check( //
        "Integrate[(a+b/x)^8*x^11, x]", //
        "-1/9*b^3*(b+a*x)^9/a^4+3/10*b^2*(b+a*x)^10/a^4-3/11*b*(b+a*x)^11/a^4+1/12*(b+a*x)^12/a^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1876
  public void test0574() {
    check( //
        "Integrate[(a+b/x)^8/x^3, x]", //
        "-1/10*(b+a*x)^9/(b*x^10)+1/90*a*(b+a*x)^9/(b^2*x^9)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1892
  public void test0575() {
    check( //
        "Integrate[1/((a+b/x)*x^4), x]", //
        "(-1/2)/(b*x^2)+a/(b^2*x)+a^2*Log[x]/b^3-a^2*Log[b+a*x]/b^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1906
  public void test0576() {
    check( //
        "Integrate[1/((a+b/x)^2*x^5), x]", //
        "(-1/2)/(b^2*x^2)+2*a/(b^3*x)+a^2/(b^3*(b+a*x))+3*a^2*Log[x]/b^4-3*a^2*Log[b+a*x]/b^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1920
  public void test0577() {
    check( //
        "Integrate[1/((a+b/x)^3*x^6), x]", //
        "(-1/2)/(b^3*x^2)+3*a/(b^4*x)+1/2*a^2/(b^3*(b+a*x)^2)+3*a^2/(b^4*(b+a*x))+6*a^2*Log[x]/b^5-6*a^2*Log[b+a*x]/b^5");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2016
  public void test0578() {
    check( //
        "Integrate[1/(x^2*Sqrt[a+b/x]), x]", //
        "-2*Sqrt[a+b/x]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2048
  public void test0579() {
    check( //
        "Integrate[(a+b/x)^(1/2)*x^(1/2), x]", //
        "2/3*(a+b/x)^(3/2)*x^(3/2)/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2062
  public void test0580() {
    check( //
        "Integrate[(a+b/x)^(5/2)*x^(9/2), x]", //
        "16/693*b^2*(a+b/x)^(7/2)*x^(7/2)/a^3-8/99*b*(a+b/x)^(7/2)*x^(9/2)/a^2+2/11*(a+b/x)^(7/2)*x^(11/2)/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2092
  public void test0581() {
    check( //
        "Integrate[x^(1/2)/(a+b/x)^(5/2), x]", //
        "-32/3*b^3/(a^4*(a+b/x)^(3/2)*x^(3/2))+2/3*x^(3/2)/(a*(a+b/x)^(3/2))-16*b^2/(a^3*(a+b/x)^(3/2)*Sqrt[x])-4*b*Sqrt[x]/(a^2*(a+b/x)^(3/2))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2126
  public void test0582() {
    check( //
        "Integrate[(a+b/x^2)^2/x^2, x]", //
        "-1/5*b^2/x^5-2/3*a*b/x^3-a^2/x");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2156
  public void test0583() {
    check( //
        "Integrate[1/((a+b/x^2)*x^4), x]", //
        "(-1)/(b*x)-ArcTan[x*Sqrt[a]/Sqrt[b]]*Sqrt[a]/b^(3/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2202
  public void test0584() {
    check( //
        "Integrate[(a+b/x^2)^(1/2)/x, x]", //
        "ArcTanh[Sqrt[a+b/x^2]/Sqrt[a]]*Sqrt[a]-Sqrt[a+b/x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2232
  public void test0585() {
    check( //
        "Integrate[x^2/(a+b/x^2)^(1/2), x]", //
        "-2/3*b*x*Sqrt[a+b/x^2]/a^2+1/3*x^3*Sqrt[a+b/x^2]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2356
  public void test0586() {
    check( //
        "Integrate[1/(x^2*Sqrt[a+b/x^3]), x]", //
        "-2*(a^(1/3)+b^(1/3)/x)*EllipticF[ArcSin[(b^(1/3)/x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)+b^(2/3)/x^2-a^(1/3)*b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(1/3)*Sqrt[a+b/x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2410
  public void test0587() {
    check( //
        "Integrate[(a+b/x^4)^(3/2)/x^3, x]", //
        "-1/8*(a+b/x^4)^(3/2)/x^2-3/16*a^2*ArcTanh[Sqrt[b]/(x^2*Sqrt[a+b/x^4])]/Sqrt[b]-3/16*a*Sqrt[a+b/x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2654
  public void test0588() {
    check( //
        "Integrate[1/(Sqrt[x]*(1+Sqrt[x])), x]", //
        "2*Log[1+Sqrt[x]]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2926
  public void test0589() {
    check( //
        "Integrate[(a+b/x^(1/3))^3*x, x]", //
        "b^3*x+9/4*a*b^2*x^(4/3)+9/5*a^2*b*x^(5/3)+1/2*a^3*x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3100
  public void test0590() {
    check( //
        "Integrate[x^(-1-6*n)*(a+b*x^n)^3, x]", //
        "-1/6*a^3/(n*x^(6*n))-3/5*a^2*b/(n*x^(5*n))-3/4*a*b^2/(n*x^(4*n))-1/3*b^3/(n*x^(3*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3114
  public void test0591() {
    check( //
        "Integrate[x^(-1-8*n)*(a+b*x^n)^5, x]", //
        "-1/8*(a+b*x^n)^6/(a*n*x^(8*n))+1/28*b*(a+b*x^n)^6/(a^2*n*x^(7*n))-1/168*b^2*(a+b*x^n)^6/(a^3*n*x^(6*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3128
  public void test0592() {
    check( //
        "Integrate[x^(-1-2*n)*(a+b*x^n)^8, x]", //
        "-1/2*a^8/(n*x^(2*n))-8*a^7*b/(n*x^n)+56*a^5*b^3*x^n/n+35*a^4*b^4*x^(2*n)/n+56/3*a^3*b^5*x^(3*n)/n+7*a^2*b^6*x^(4*n)/n+8/5*a*b^7*x^(5*n)/n+1/6*b^8*x^(6*n)/n+28*a^6*b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3160
  public void test0593() {
    check( //
        "Integrate[x^(-1-3*n)/(a+b*x^n), x]", //
        "(-1/3)/(a*n*x^(3*n))+1/2*b/(a^2*n*x^(2*n))-b^2/(a^3*n*x^n)-b^3*Log[x]/a^4+b^3*Log[a+b*x^n]/(a^4*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3190
  public void test0594() {
    check( //
        "Integrate[x^(-1+3*n)/(a+b*x^n)^3, x]", //
        "-1/2*a^2/(b^3*n*(a+b*x^n)^2)+2*a/(b^3*n*(a+b*x^n))+Log[a+b*x^n]/(b^3*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3204
  public void test0595() {
    check( //
        "Integrate[x^(-1-4/3*n)/(a+b*x^n), x]", //
        "(-3/4)/(a*n*x^(4/3*n))+3*b/(a^2*n*x^(1/3*n))-b^(4/3)*Log[b^(1/3)+a^(1/3)/x^(1/3*n)]/(a^(7/3)*n)+1/2*b^(4/3)*Log[b^(2/3)+a^(2/3)/x^(2/3*n)-a^(1/3)*b^(1/3)/x^(1/3*n)]/(a^(7/3)*n)+b^(4/3)*ArcTan[(1-2*a^(1/3)/(b^(1/3)*x^(1/3*n)))/Sqrt[3]]*Sqrt[3]/(a^(7/3)*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3224
  public void test0596() {
    check( //
        "Integrate[x^(-1+n)/(a+b*x^n)^(1/2), x]", //
        "2*Sqrt[a+b*x^n]/(b*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3257
  public void test0597() {
    check( //
        "Integrate[x^(-1-3/2*n)/Sqrt[a+b*x^n], x]", //
        "-2/3*Sqrt[a+b*x^n]/(a*n*x^(3/2*n))+4/3*b*Sqrt[a+b*x^n]/(a^2*n*x^(1/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3300
  public void test0598() {
    check( //
        "Integrate[1/(x*(a+b*x^3)), x]", //
        "Log[x]/a-1/3*Log[a+b*x^3]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3319
  public void test0599() {
    check( //
        "Integrate[x^m*(a+b*x^(2+2*m))^(5/2), x]", //
        "5/24*a*x^(1+m)*(a+b*x^(2*(1+m)))^(3/2)/(1+m)+1/6*x^(1+m)*(a+b*x^(2*(1+m)))^(5/2)/(1+m)+5/16*a^3*ArcTanh[x^(1+m)*Sqrt[b]/Sqrt[a+b*x^(2*(1+m))]]/((1+m)*Sqrt[b])+5/16*a^2*x^(1+m)*Sqrt[a+b*x^(2*(1+m))]/(1+m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3397
  public void test0600() {
    check( //
        "Integrate[(c*(a+b*x)^2)^(1/2), x]", //
        "1/2*(a+b*x)*Sqrt[c*(a+b*x)^2]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3434
  public void test0601() {
    check( //
        "Integrate[(c/(a+b*x)^3)^(1/2), x]", //
        "-2*(a+b*x)*Sqrt[c/(a+b*x)^3]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3460
  public void test0602() {
    check( //
        "Integrate[(c+d*x)^5*(a+b*(c+d*x)^2)^p, x]", //
        "1/2*a^2*(a+b*(c+d*x)^2)^(1+p)/(b^3*d*(1+p))-a*(a+b*(c+d*x)^2)^(2+p)/(b^3*d*(2+p))+1/2*(a+b*(c+d*x)^2)^(3+p)/(b^3*d*(3+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3483
  public void test0603() {
    check( //
        "Integrate[1/((c+d*x)*(a+b*(c+d*x)^3)), x]", //
        "Log[c+d*x]/(a*d)-1/3*Log[a+b*(c+d*x)^3]/(a*d)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3511
  public void test0604() {
    check( //
        "Integrate[1/((c*e+d*e*x)^3*(a+b*(c+d*x)^3)), x]", //
        "(-1/2)/(a*d*e^3*(c+d*x)^2)-1/3*b^(2/3)*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(5/3)*d*e^3)+1/6*b^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(5/3)*d*e^3)+b^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(5/3)*d*e^3*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3525
  public void test0605() {
    check( //
        "Integrate[1/((c*e+d*e*x)*(a+b*(c+d*x)^3)^3), x]", //
        "1/6/(a*d*e*(a+b*(c+d*x)^3)^2)+1/3/(a^2*d*e*(a+b*(c+d*x)^3))+Log[c+d*x]/(a^3*d*e)-1/3*Log[a+b*(c+d*x)^3]/(a^3*d*e)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3706
  public void test0606() {
    check( //
        "Integrate[1/(a+b*(c*x^n)^(1/n)), x]", //
        "x*Log[a+b*(c*x^n)^(1/n)]/(b*(c*x^n)^(1/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3723
  public void test0607() {
    check( //
        "Integrate[x*(a+b*(c*x^n)^(1/n))^p, x]", //
        "-a*x^2*(a+b*(c*x^n)^(1/n))^(1+p)/(b^2*(1+p)*(c*x^n)^(2/n))+x^2*(a+b*(c*x^n)^(1/n))^(2+p)/(b^2*(2+p)*(c*x^n)^(2/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3743
  public void test0608() {
    check( //
        "Integrate[(a+b*(c*x^n)^(3/n))^3, x]", //
        "a^3*x+3/4*a^2*b*x*(c*x^n)^(3/n)+3/7*a*b^2*x*(c*x^n)^(6/n)+1/10*b^3*x*(c*x^n)^(9/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3769
  public void test0609() {
    check( //
        "Integrate[x/Sqrt[a+c/x+b*Sqrt[d/x]], x]", //
        "1/64*(48*a^2*c^2-120*a*b^2*c*d+35*b^4*d^2)*ArcTanh[1/2*(2*a+b*Sqrt[d/x])/(Sqrt[a]*Sqrt[a+c/x+b*Sqrt[d/x]])]/a^(9/2)-7/12*b*d^2*Sqrt[a+c/x+b*Sqrt[d/x]]/(a^2*(d/x)^(3/2))-1/48*(36*a*c-35*b^2*d)*x*Sqrt[a+c/x+b*Sqrt[d/x]]/a^3+1/2*x^2*Sqrt[a+c/x+b*Sqrt[d/x]]/a+5/96*b*d*(44*a*c-21*b^2*d)*Sqrt[a+c/x+b*Sqrt[d/x]]/(a^4*Sqrt[d/x])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3785
  public void test0610() {
    check( //
        "Integrate[(c*x^n)^(1/n)/(a+b*(c*x^n)^(1/n))^3, x]", //
        "1/2*x*(c*x^n)^(1/n)/(a*(a+b*(c*x^n)^(1/n))^2)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:64
  public void test0611() {
    check( //
        "Integrate[(a-b*x^3)^2/(a+b*x^3)^(13/3), x]", //
        "1/20*x*(a-b*x^3)^3/(a^2*(a+b*x^3)^(10/3))+19/140*x*(a-b*x^3)^2/(a^2*(a+b*x^3)^(7/3))+57/280*x*(a-b*x^3)/(a^2*(a+b*x^3)^(4/3))+171/280*x/(a^2*(a+b*x^3)^(1/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:174
  public void test0612() {
    check( //
        "Integrate[(a+b*x^3)^(-1-b*c/(3*b*c-3*a*d))*(c+d*x^3)^(-1+a*d/(3*b*c-3*a*d)), x]", //
        "x*(c+d*x^3)^(a*d/(3*b*c-3*a*d))/(a*c*(a+b*x^3)^(b*c/(3*b*c-3*a*d)))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:244
  public void test0613() {
    check( //
        "Integrate[Sqrt[a+b*x^4]/(a*c-b*c*x^4), x]", //
        "1/2*ArcTan[a^(1/4)*b^(1/4)*x*Sqrt[2]/Sqrt[a+b*x^4]]/(a^(1/4)*b^(1/4)*c*Sqrt[2])+1/2*ArcTanh[a^(1/4)*b^(1/4)*x*Sqrt[2]/Sqrt[a+b*x^4]]/(a^(1/4)*b^(1/4)*c*Sqrt[2])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:279
  public void test0614() {
    check( //
        "Integrate[1/((1+x^4)^(1/4)*(2+x^4)), x]", //
        "1/2*ArcTan[x/(2^(1/4)*(1+x^4)^(1/4))]/2^(3/4)+1/2*ArcTanh[x/(2^(1/4)*(1+x^4)^(1/4))]/2^(3/4)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:506
  public void test0615() {
    check( //
        "Integrate[(a+b*x^2)/(x*Sqrt[-1+c*x]*Sqrt[1+c*x]), x]", //
        "a*ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]+b*Sqrt[-1+c*x]*Sqrt[1+c*x]/c^2");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:520
  public void test0616() {
    check( //
        "Integrate[(a+b*x^2)/(x^5*Sqrt[-c+d*x]*Sqrt[c+d*x]), x]", //
        "1/8*d^2*(4*b*c^2+3*a*d^2)*ArcTan[Sqrt[-c+d*x]*Sqrt[c+d*x]/c]/c^5+1/4*a*Sqrt[-c+d*x]*Sqrt[c+d*x]/(c^2*x^4)+1/8*(4*b*c^2+3*a*d^2)*Sqrt[-c+d*x]*Sqrt[c+d*x]/(c^4*x^2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:25
  public void test0617() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x, x]", //
        "2/3*a*A*b*x^3+1/6*A*b^2*x^6+1/9*B*(a+b*x^3)^3/b+a^2*A*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:53
  public void test0618() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^10, x]", //
        "-1/9*a^5*A/x^9-1/6*a^4*(5*A*b+a*B)/x^6-5/3*a^3*b*(2*A*b+a*B)/x^3+5/3*a*b^3*(A*b+2*a*B)*x^3+1/6*b^4*(A*b+5*a*B)*x^6+1/9*b^5*B*x^9+10*a^2*b^2*(A*b+a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:69
  public void test0619() {
    check( //
        "Integrate[x^6*(A+B*x^3)/(a+b*x^3), x]", //
        "-a*(A*b-a*B)*x/b^3+1/4*(A*b-a*B)*x^4/b^2+1/7*B*x^7/b+1/3*a^(4/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/b^(10/3)-1/6*a^(4/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(10/3)-a^(4/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(10/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:83
  public void test0620() {
    check( //
        "Integrate[(A+B*x^3)/(x^8*(a+b*x^3)), x]", //
        "-1/7*A/(a*x^7)+1/4*(A*b-a*B)/(a^2*x^4)-b*(A*b-a*B)/(a^3*x)+1/3*b^(4/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/a^(10/3)-1/6*b^(4/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(10/3)+b^(4/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(10/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:131
  public void test0621() {
    check( //
        "Integrate[x^3/((a+b*x^3)*(c+d*x^3)), x]", //
        "-1/3*a^(1/3)*Log[a^(1/3)+b^(1/3)*x]/(b^(1/3)*(b*c-a*d))+1/3*c^(1/3)*Log[c^(1/3)+d^(1/3)*x]/(d^(1/3)*(b*c-a*d))+1/6*a^(1/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(b^(1/3)*(b*c-a*d))-1/6*c^(1/3)*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(d^(1/3)*(b*c-a*d))+a^(1/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(1/3)*(b*c-a*d)*Sqrt[3])-c^(1/3)*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(d^(1/3)*(b*c-a*d)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:171
  public void test0622() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^(3/2), x]", //
        "2/5*a*(2*A*b+a*B)*x^(5/2)+2/11*b*(A*b+2*a*B)*x^(11/2)+2/17*b^2*B*x^(17/2)-2*a^2*A/Sqrt[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:187
  public void test0623() {
    check( //
        "Integrate[(A+B*x^3)*Sqrt[x]/(a+b*x^3), x]", //
        "2/3*B*x^(3/2)/b+2/3*(A*b-a*B)*ArcTan[x^(3/2)*Sqrt[b]/Sqrt[a]]/(b^(3/2)*Sqrt[a])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:221
  public void test0624() {
    check( //
        "Integrate[(A+B*x^3)*Sqrt[a+b*x^3], x]", //
        "2/11*B*x*(a+b*x^3)^(3/2)/b+2/55*(11*A*b-2*a*B)*x*Sqrt[a+b*x^3]/b+2/55*3^(3/4)*a*(11*A*b-2*a*B)*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(4/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:405
  public void test0625() {
    check( //
        "Integrate[x/((b*x^3+2*a*(5-3*Sqrt[3]))*Sqrt[a+b*x^3]), x]", //
        "-1/3*ArcTan[3^(1/4)*a^(1/6)*(-2*b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(Sqrt[2]*Sqrt[a+b*x^3])]*(2+Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/6*ArcTan[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[a+b*x^3])]*(2+Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])+1/2*ArcTanh[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[a+b*x^3])]*(2+Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])+1/3*ArcTanh[(1+Sqrt[3])*Sqrt[a+b*x^3]/(3^(3/4)*Sqrt[2]*Sqrt[a])]*(2+Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:433
  public void test0626() {
    check( //
        "Integrate[x^8/((a+b*x^3)*Sqrt[c+d*x^3]), x]", //
        "2/9*(c+d*x^3)^(3/2)/(b*d^2)-2/3*a^2*ArcTanh[Sqrt[b]*Sqrt[c+d*x^3]/Sqrt[b*c-a*d]]/(b^(5/2)*Sqrt[b*c-a*d])-2/3*(b*c+a*d)*Sqrt[c+d*x^3]/(b^2*d^2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:582
  public void test0627() {
    check( //
        "Integrate[x^2/(Sqrt[a+b*x^3]*Sqrt[c+d*x^3]), x]", //
        "2/3*ArcTanh[Sqrt[d]*Sqrt[a+b*x^3]/(Sqrt[b]*Sqrt[c+d*x^3])]/(Sqrt[b]*Sqrt[d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:937
  public void test0628() {
    check( //
        "Integrate[x/((a+b*x^4)*Sqrt[c+d*x^4]), x]", //
        "1/2*ArcTan[x^2*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^4])]/(Sqrt[a]*Sqrt[b*c-a*d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1049
  public void test0629() {
    check( //
        "Integrate[x^3/((a+b*x^8)*Sqrt[c+d*x^8]), x]", //
        "1/4*ArcTan[x^4*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^8])]/(Sqrt[a]*Sqrt[b*c-a*d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1195
  public void test0630() {
    check( //
        "Integrate[1/(x^(5/2)*Sqrt[-1+Sqrt[x]]*Sqrt[1+Sqrt[x]]), x]", //
        "2/3*Sqrt[-1+Sqrt[x]]*Sqrt[1+Sqrt[x]]/x^(3/2)+4/3*Sqrt[-1+Sqrt[x]]*Sqrt[1+Sqrt[x]]/Sqrt[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:90
  public void test0631() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3+g*x^4)/(a+b*x^3)^(3/2), x]", //
        "2/3*x*(b*c-a*f+(b*d-a*g)*x+b*e*x^2)/(a*b*Sqrt[a+b*x^3])-2/3*e*Sqrt[a+b*x^3]/(a*b)-2/3*(b*d-4*a*g)*Sqrt[a+b*x^3]/(a*b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+(b*d-4*a*g)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(3/4)*a^(2/3)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+2/3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(b^(1/3)*(b*c+2*a*f)+a^(1/3)*(b*d-4*a*g)*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:116
  public void test0632() {
    check( //
        "Integrate[(1-x+Sqrt[3])/Sqrt[-1+x^3], x]", //
        "2*Sqrt[-1+x^3]/(1-x-Sqrt[3])-3^(1/4)*(1-x)*EllipticE[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]*Sqrt[2+Sqrt[3]]/(Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:136
  public void test0633() {
    check( //
        "Integrate[(-1-x+Sqrt[3])/Sqrt[1+x^3], x]", //
        "-2*Sqrt[1+x^3]/(1+x+Sqrt[3])+3^(1/4)*(1+x)*EllipticE[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:346
  public void test0634() {
    check( //
        "Integrate[x^6*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3), x]", //
        "-a*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/b^5+1/4*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^4/b^4+1/7*(b^2*d-a*b*e+a^2*f)*x^7/b^3+1/10*(b*e-a*f)*x^10/b^2+1/13*f*x^13/b+1/3*a^(4/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(16/3)-1/6*a^(4/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(16/3)-a^(4/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(16/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:374
  public void test0635() {
    check( //
        "Integrate[x^4*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^2, x]", //
        "1/2*(b^2*d-2*a*b*e+3*a^2*f)*x^2/b^4+1/5*(b*e-2*a*f)*x^5/b^3+1/8*f*x^8/b^2-1/3*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^2/(b^4*(a+b*x^3))-1/9*(2*b^3*c-5*a*b^2*d+8*a^2*b*e-11*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/(a^(1/3)*b^(14/3))+1/18*(2*b^3*c-5*a*b^2*d+8*a^2*b*e-11*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(1/3)*b^(14/3))-1/3*(2*b^3*c-5*a*b^2*d+8*a^2*b*e-11*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(1/3)*b^(14/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:402
  public void test0636() {
    check( //
        "Integrate[x^4*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^3, x]", //
        "1/2*(b*e-3*a*f)*x^2/b^4+1/5*f*x^5/b^3-1/6*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^2/(b^4*(a+b*x^3)^2)+1/9*(b^3*c-4*a*b^2*d+7*a^2*b*e-10*a^3*f)*x^2/(a*b^4*(a+b*x^3))-1/27*(b^3*c+5*a*b^2*d-20*a^2*b*e+44*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/(a^(4/3)*b^(14/3))+1/54*(b^3*c+5*a*b^2*d-20*a^2*b*e+44*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(4/3)*b^(14/3))-1/9*(b^3*c+5*a*b^2*d-20*a^2*b*e+44*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(4/3)*b^(14/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:422
  public void test0637() {
    check( //
        "Integrate[(1-x)*x^3/(1+x^3), x]", //
        "x-1/2*x^2-2/3*Log[1+x]+1/3*Log[1-x+x^2]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:440
  public void test0638() {
    check( //
        "Integrate[x^2*(c+d*x+e*x^2)*(a+b*x^3)^2, x]", //
        "1/4*a^2*d*x^4+1/5*a^2*e*x^5+2/7*a*b*d*x^7+1/4*a*b*e*x^8+1/10*b^2*d*x^10+1/11*b^2*e*x^11+1/9*c*(a+b*x^3)^3/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:454
  public void test0639() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)^4, x]", //
        "a^4*c*x+1/2*a^4*d*x^2+a^3*b*c*x^4+4/5*a^3*b*d*x^5+6/7*a^2*b^2*c*x^7+3/4*a^2*b^2*d*x^8+2/5*a*b^3*c*x^10+4/11*a*b^3*d*x^11+1/13*b^4*c*x^13+1/14*b^4*d*x^14+1/15*e*(a+b*x^3)^5/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:516
  public void test0640() {
    check( //
        "Integrate[(a+b*x^3)^2*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/x^2, x]", //
        "-a^2*c/x+a^2*e*x+1/2*a*(2*b*c+a*f)*x^2+2/3*a*b*d*x^3+1/4*a*(2*b*e+a*h)*x^4+1/5*b*(b*c+2*a*f)*x^5+1/6*b^2*d*x^6+1/7*b*(b*e+2*a*h)*x^7+1/8*b^2*f*x^8+1/10*b^2*h*x^10+1/9*g*(a+b*x^3)^3/b+a^2*d*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:532
  public void test0641() {
    check( //
        "Integrate[x^4*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3), x]", //
        "-a*(b*e-a*h)*x/b^3+1/2*(b*c-a*f)*x^2/b^2+1/3*(b*d-a*g)*x^3/b^2+1/4*(b*e-a*h)*x^4/b^2+1/5*f*x^5/b+1/6*g*x^6/b+1/7*h*x^7/b+1/3*a^(2/3)*(b^(2/3)*(b*c-a*f)+a^(2/3)*(b*e-a*h))*Log[a^(1/3)+b^(1/3)*x]/b^(10/3)-1/6*a^(2/3)*(b^(2/3)*(b*c-a*f)+a^(2/3)*(b*e-a*h))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(10/3)-1/3*a*(b*d-a*g)*Log[a+b*x^3]/b^3+a^(2/3)*(b^(5/3)*c-a^(2/3)*b*e-a*b^(2/3)*f+a^(5/3)*h)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(10/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:566
  public void test0642() {
    check( //
        "Integrate[x^2*(c+d*x+e*x^2)/Sqrt[a+b*x^3], x]", //
        "2/3*c*Sqrt[a+b*x^3]/b+2/5*d*x*Sqrt[a+b*x^3]/b+2/7*e*x^2*Sqrt[a+b*x^3]/b-8/7*a*e*Sqrt[a+b*x^3]/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+4/7*3^(1/4)*a^(4/3)*e*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-4/35*a*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(7*b^(1/3)*d-10*a^(1/3)*e*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:584
  public void test0643() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3+g*x^4)*Sqrt[a+b*x^3], x]", //
        "-4/45*a^2*e*Sqrt[a+b*x^3]/b^2+6/935*a*(17*b*c-8*a*f)*x*Sqrt[a+b*x^3]/b^2+6/1729*a*(19*b*d-10*a*g)*x^2*Sqrt[a+b*x^3]/b^2+2/45*a*e*x^3*Sqrt[a+b*x^3]/b+6/187*a*f*x^4*Sqrt[a+b*x^3]/b+6/247*a*g*x^5*Sqrt[a+b*x^3]/b+2/692835*x^3*(62985*c*x+53295*d*x^2+46189*e*x^3+40755*f*x^4+36465*g*x^5)*Sqrt[a+b*x^3]-24/1729*a^2*(19*b*d-10*a*g)*Sqrt[a+b*x^3]/(b^(8/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+12/1729*3^(1/4)*a^(7/3)*(19*b*d-10*a*g)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(8/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-4/1616615*3^(3/4)*a^2*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(1729*b^(1/3)*(17*b*c-8*a*f)-1870*a^(1/3)*(19*b*d-10*a*g)*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(8/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:598
  public void test0644() {
    check( //
        "Integrate[x^2*(a+b*x^3)^(3/2)*(c+d*x+e*x^2+f*x^3+g*x^4), x]", //
        "2/780045*x^2*(a+b*x^3)^(3/2)*(52003*c*x+45885*d*x^2+41055*e*x^3+37145*f*x^4+33915*g*x^5)+2/105*a^2*(7*b*c-2*a*f)*Sqrt[a+b*x^3]/b^2+54/21505*a^2*(23*b*d-8*a*g)*x*Sqrt[a+b*x^3]/b^2+54/1729*a^2*e*x^2*Sqrt[a+b*x^3]/b+2/105*a^2*f*x^3*Sqrt[a+b*x^3]/b+54/4301*a^2*g*x^4*Sqrt[a+b*x^3]/b+2/111546435*a*x^2*(7436429*c*x+5368545*d*x^2+4064445*e*x^3+3187041*f*x^4+2567565*g*x^5)*Sqrt[a+b*x^3]-216/1729*a^3*e*Sqrt[a+b*x^3]/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+108/1729*3^(1/4)*a^(10/3)*e*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+36/37182145*3^(3/4)*a^3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(-1729*(23*b*d-8*a*g)+43010*a^(1/3)*b^(2/3)*e*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(7/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:612
  public void test0645() {
    check( //
        "Integrate[(a+b*x^3)^(3/2)*(c+d*x+e*x^2+f*x^3+g*x^4)/x^12, x]", //
        "-1/27720*(2520*c/x^11+2772*d/x^10+3080*e/x^9+3465*f/x^8+3960*g/x^7)*(a+b*x^3)^(3/2)+1/24*b^3*e*ArcTanh[Sqrt[a+b*x^3]/Sqrt[a]]/a^(3/2)-1/18480*b*(945*c/x^8+1188*d/x^7+1540*e/x^6+2079*f/x^5+2970*g/x^4)*Sqrt[a+b*x^3]-27/1760*b^2*c*Sqrt[a+b*x^3]/(a*x^5)-27/1120*b^2*d*Sqrt[a+b*x^3]/(a*x^4)-1/24*b^2*e*Sqrt[a+b*x^3]/(a*x^3)+27/7040*b^2*(7*b*c-22*a*f)*Sqrt[a+b*x^3]/(a^2*x^2)+27/448*b^2*(b*d-4*a*g)*Sqrt[a+b*x^3]/(a^2*x)-27/448*b^(7/3)*(b*d-4*a*g)*Sqrt[a+b*x^3]/(a^2*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+27/896*3^(1/4)*b^(7/3)*(b*d-4*a*g)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(a^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+9/49280*3^(3/4)*b^(7/3)*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(7*b^(1/3)*(7*b*c-22*a*f)+110*a^(1/3)*(b*d-4*a*g)*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(a^2*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:641
  public void test0646() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3)/(a+b*x^4)^2, x]", //
        "1/4*(-c-d*x-e*x^2-f*x^3)/(b*(a+b*x^4))+1/4*e*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(b^(3/2)*Sqrt[a])-1/16*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-3*f*Sqrt[a]+d*Sqrt[b])/(a^(3/4)*b^(7/4)*Sqrt[2])+1/16*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-3*f*Sqrt[a]+d*Sqrt[b])/(a^(3/4)*b^(7/4)*Sqrt[2])-1/8*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(3*f*Sqrt[a]+d*Sqrt[b])/(a^(3/4)*b^(7/4)*Sqrt[2])+1/8*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(3*f*Sqrt[a]+d*Sqrt[b])/(a^(3/4)*b^(7/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:673
  public void test0647() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^4, x]", //
        "-1/15*(5*c-3*e*x^2)*(a+b*x^4)^(3/2)/x^3-1/6*(3*d-f*x^2)*(a+b*x^4)^(3/2)/x^2-1/2*a^(3/2)*f*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]+3/4*a*d*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]*Sqrt[b]-2/15*(9*a*e-5*b*c*x^2)*Sqrt[a+b*x^4]/x+1/4*(2*a*f+3*b*d*x^2)*Sqrt[a+b*x^4]+12/5*a*e*x*Sqrt[b]*Sqrt[a+b*x^4]/(Sqrt[a]+x^2*Sqrt[b])-12/5*a^(5/4)*b^(1/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]+2/15*a^(3/4)*b^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(9*e*Sqrt[a]+5*c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:689
  public void test0648() {
    check( //
        "Integrate[x*(c+d*x+e*x^2+f*x^3)/Sqrt[a+b*x^4], x]", //
        "1/2*c*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]/Sqrt[b]+1/2*e*Sqrt[a+b*x^4]/b+1/3*f*x*Sqrt[a+b*x^4]/b+d*x*Sqrt[a+b*x^4]/(Sqrt[b]*(Sqrt[a]+x^2*Sqrt[b]))-a^(1/4)*d*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])+1/6*a^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-f*Sqrt[a]+3*d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:761
  public void test0649() {
    check( //
        "Integrate[(c+d*x^(-1+n))*(a+b*x^n)^2, x]", //
        "a^2*c*x+2*a*b*c*x^(1+n)/(1+n)+b^2*c*x^(1+2*n)/(1+2*n)+1/3*d*(a+b*x^n)^3/(b*n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:32
  public void test0650() {
    check( //
        "Integrate[1/(x^3*(a*x+b*x^3)), x]", //
        "(-1/3)/(a*x^3)+b/(a^2*x)+b^(3/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/a^(5/2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:49
  public void test0651() {
    check( //
        "Integrate[1/(x^2*(x-x^3)), x]", //
        "(-1/2)/x^2+Log[x]-1/2*Log[1-x^2]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:103
  public void test0652() {
    check( //
        "Integrate[x^(29/2)/(a*x+b*x^3)^(9/2), x]", //
        "-1/7*x^(25/2)/(b*(a*x+b*x^3)^(7/2))-9/35*x^(19/2)/(b^2*(a*x+b*x^3)^(5/2))-3/5*x^(13/2)/(b^3*(a*x+b*x^3)^(3/2))-9/2*a*ArcTanh[x^(3/2)*Sqrt[b]/Sqrt[a*x+b*x^3]]/b^(11/2)-3*x^(7/2)/(b^4*Sqrt[a*x+b*x^3])+9/2*Sqrt[x]*Sqrt[a*x+b*x^3]/b^5");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:117
  public void test0653() {
    check( //
        "Integrate[x^(1/2)/(a*x+b*x^3)^(9/2), x]", //
        "16/21/(a^3*x^(3/2)*(a*x+b*x^3)^(3/2))+2/7/(a^2*(a*x+b*x^3)^(5/2)*Sqrt[x])+1/7*Sqrt[x]/(a*(a*x+b*x^3)^(7/2))+32/7/(a^4*x^(5/2)*Sqrt[a*x+b*x^3])-128/21*Sqrt[a*x+b*x^3]/(a^5*x^(7/2))+256/21*b*Sqrt[a*x+b*x^3]/(a^6*x^(3/2))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:151
  public void test0654() {
    check( //
        "Integrate[x^2/(b*x^(1/2)+a*x)^(1/2), x]", //
        "-63/64*b^5*ArcTanh[Sqrt[a]*Sqrt[x]/Sqrt[a*x+b*Sqrt[x]]]/a^(11/2)+63/64*b^4*Sqrt[a*x+b*Sqrt[x]]/a^5+21/40*b^2*x*Sqrt[a*x+b*Sqrt[x]]/a^3-9/20*b*x^(3/2)*Sqrt[a*x+b*Sqrt[x]]/a^2+2/5*x^2*Sqrt[a*x+b*Sqrt[x]]/a-21/32*b^3*Sqrt[x]*Sqrt[a*x+b*Sqrt[x]]/a^4");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:171
  public void test0655() {
    check( //
        "Integrate[x^(5/2)/(b*x^(1/2)+a*x)^(1/2), x]", //
        "231/256*b^6*ArcTanh[Sqrt[a]*Sqrt[x]/Sqrt[a*x+b*Sqrt[x]]]/a^(13/2)-231/256*b^5*Sqrt[a*x+b*Sqrt[x]]/a^6-77/160*b^3*x*Sqrt[a*x+b*Sqrt[x]]/a^4+33/80*b^2*x^(3/2)*Sqrt[a*x+b*Sqrt[x]]/a^3-11/30*b*x^2*Sqrt[a*x+b*Sqrt[x]]/a^2+1/3*x^(5/2)*Sqrt[a*x+b*Sqrt[x]]/a+77/128*b^4*Sqrt[x]*Sqrt[a*x+b*Sqrt[x]]/a^5");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:239
  public void test0656() {
    check( //
        "Integrate[Sqrt[b*x^(2/3)+a*x]/x^3, x]", //
        "-21/128*a^5*ArcTanh[x^(1/3)*Sqrt[b]/Sqrt[b*x^(2/3)+a*x]]/b^(9/2)-3/5*Sqrt[b*x^(2/3)+a*x]/x^2-3/40*a*Sqrt[b*x^(2/3)+a*x]/(b*x^(5/3))+7/80*a^2*Sqrt[b*x^(2/3)+a*x]/(b^2*x^(4/3))-7/64*a^3*Sqrt[b*x^(2/3)+a*x]/(b^3*x)+21/128*a^4*Sqrt[b*x^(2/3)+a*x]/(b^4*x^(2/3))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:255
  public void test0657() {
    check( //
        "Integrate[x^2/Sqrt[b*x^(2/3)+a*x], x]", //
        "2048/2145*b^6*Sqrt[b*x^(2/3)+a*x]/a^7-4096/2145*b^7*Sqrt[b*x^(2/3)+a*x]/(a^8*x^(1/3))-512/715*b^5*x^(1/3)*Sqrt[b*x^(2/3)+a*x]/a^6+256/429*b^4*x^(2/3)*Sqrt[b*x^(2/3)+a*x]/a^5-224/429*b^3*x*Sqrt[b*x^(2/3)+a*x]/a^4+336/715*b^2*x^(4/3)*Sqrt[b*x^(2/3)+a*x]/a^3-28/65*b*x^(5/3)*Sqrt[b*x^(2/3)+a*x]/a^2+2/5*x^2*Sqrt[b*x^(2/3)+a*x]/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:269
  public void test0658() {
    check( //
        "Integrate[1/(x^3*(b*x^(2/3)+a*x)^(3/2)), x]", //
        "692835/32768*a^9*ArcTanh[x^(1/3)*Sqrt[b]/Sqrt[b*x^(2/3)+a*x]]/b^(21/2)+6/(b*x^(8/3)*Sqrt[b*x^(2/3)+a*x])-19/3*Sqrt[b*x^(2/3)+a*x]/(b^2*x^(10/3))+323/48*a*Sqrt[b*x^(2/3)+a*x]/(b^3*x^3)-1615/224*a^2*Sqrt[b*x^(2/3)+a*x]/(b^4*x^(8/3))+20995/2688*a^3*Sqrt[b*x^(2/3)+a*x]/(b^5*x^(7/3))-46189/5376*a^4*Sqrt[b*x^(2/3)+a*x]/(b^6*x^2)+138567/14336*a^5*Sqrt[b*x^(2/3)+a*x]/(b^7*x^(5/3))-46189/4096*a^6*Sqrt[b*x^(2/3)+a*x]/(b^8*x^(4/3))+230945/16384*a^7*Sqrt[b*x^(2/3)+a*x]/(b^9*x)-692835/32768*a^8*Sqrt[b*x^(2/3)+a*x]/(b^10*x^(2/3))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:307
  public void test0659() {
    check( //
        "Integrate[x/(a*x^2+b*x^3)^2, x]", //
        "(-1/2)/(a^2*x^2)+2*b/(a^3*x)+b^2/(a^3*(a+b*x))+3*b^2*Log[x]/a^4-3*b^2*Log[a+b*x]/a^4");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:325
  public void test0660() {
    check( //
        "Integrate[(a*x^2+b*x^3)^(3/2)/x, x]", //
        "16/315*a^2*(a*x^2+b*x^3)^(5/2)/(b^3*x^5)-8/63*a*(a*x^2+b*x^3)^(5/2)/(b^2*x^4)+2/9*(a*x^2+b*x^3)^(5/2)/(b*x^3)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:341
  public void test0661() {
    check( //
        "Integrate[1/(x*Sqrt[a*x^2+b*x^3]), x]", //
        "b*ArcTanh[x*Sqrt[a]/Sqrt[a*x^2+b*x^3]]/a^(3/2)-Sqrt[a*x^2+b*x^3]/(a*x^2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:363
  public void test0662() {
    check( //
        "Integrate[x^(3/2)/Sqrt[a*x^2+b*x^3], x]", //
        "-a*ArcTanh[x^(3/2)*Sqrt[b]/Sqrt[a*x^2+b*x^3]]/b^(3/2)+Sqrt[a*x^2+b*x^3]/(b*Sqrt[x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:392
  public void test0663() {
    check( //
        "Integrate[1/(x^3*Sqrt[a*x^2+b*x^5]), x]", //
        "1/3*b*ArcTanh[x*Sqrt[a]/Sqrt[a*x^2+b*x^5]]/a^(3/2)-1/3*Sqrt[a*x^2+b*x^5]/(a*x^4)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:434
  public void test0664() {
    check( //
        "Integrate[1/(x^2*Sqrt[a*x^3+b*x^4]), x]", //
        "-2/5*Sqrt[a*x^3+b*x^4]/(a*x^4)+8/15*b*Sqrt[a*x^3+b*x^4]/(a^2*x^3)-16/15*b^2*Sqrt[a*x^3+b*x^4]/(a^3*x^2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:476
  public void test0665() {
    check( //
        "Integrate[(a/x+b*x)^2, x]", //
        "-a^2/x+2*a*b*x+1/3*b^2*x^3");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:492
  public void test0666() {
    check( //
        "Integrate[1/(2*x+3*x^(1+n)), x]", //
        "1/2*Log[x]-1/2*Log[2+3*x^n]/n");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:510
  public void test0667() {
    check( //
        "Integrate[(c*x)^(1/2)*Sqrt[a/x^3+b*x^n], x]", //
        "-2*c*ArcTanh[Sqrt[a]/(x^(3/2)*Sqrt[a/x^3+b*x^n])]*Sqrt[a]*Sqrt[x]/((3+n)*Sqrt[c*x])+2*(c*x)^(3/2)*Sqrt[a/x^3+b*x^n]/(c*(3+n))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:524
  public void test0668() {
    check( //
        "Integrate[Sqrt[(-a+b*x)/x^2], x]", //
        "2*ArcTan[Sqrt[a]/(x*Sqrt[-a/x^2+b/x])]*Sqrt[a]+2*x*Sqrt[-a/x^2+b/x]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:554
  public void test0669() {
    check( //
        "Integrate[1/Sqrt[x^(2-n)*(a-b*x^n)], x]", //
        "2*ArcTan[x*Sqrt[b]/Sqrt[-b*x^2+a*x^(2-n)]]/(n*Sqrt[b])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:14
  public void test0670() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4), x]", //
        "1/3*A*b*x^3+1/5*(b*B+A*c)*x^5+1/7*B*c*x^7");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:28
  public void test0671() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/x^5, x]", //
        "A*b*c*x^2+1/4*A*c^2*x^4+1/6*B*(b+c*x^2)^3/c+A*b^2*Log[x]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:42
  public void test0672() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^8, x]", //
        "-A*b^3/x+b^2*(b*B+3*A*c)*x+b*c*(b*B+A*c)*x^3+1/5*c^2*(3*b*B+A*c)*x^5+1/7*B*c^3*x^7");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:58
  public void test0673() {
    check( //
        "Integrate[x^6*(A+B*x^2)/(b*x^2+c*x^4), x]", //
        "b*(b*B-A*c)*x/c^3-1/3*(b*B-A*c)*x^3/c^2+1/5*B*x^5/c-b^(3/2)*(b*B-A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/c^(7/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:86
  public void test0674() {
    check( //
        "Integrate[x^14*(A+B*x^2)/(b*x^2+c*x^4)^3, x]", //
        "3*b*(2*b*B-A*c)*x/c^5-1/3*(3*b*B-A*c)*x^3/c^4+1/5*B*x^5/c^3-1/4*b^3*(b*B-A*c)*x/(c^5*(b+c*x^2)^2)+1/8*b^2*(17*b*B-13*A*c)*x/(c^5*(b+c*x^2))-7/8*b^(3/2)*(9*b*B-5*A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/c^(11/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:194
  public void test0675() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/Sqrt[x], x]", //
        "2/9*A*b^2*x^(9/2)+2/13*b*(b*B+2*A*c)*x^(13/2)+2/17*c*(2*b*B+A*c)*x^(17/2)+2/21*B*c^2*x^(21/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:302
  public void test0676() {
    check( //
        "Integrate[x^m*(A+B*x^2)*(b*x^2+c*x^4)^3, x]", //
        "A*b^3*x^(7+m)/(7+m)+b^2*(b*B+3*A*c)*x^(9+m)/(9+m)+3*b*c*(b*B+A*c)*x^(11+m)/(11+m)+c^2*(3*b*B+A*c)*x^(13+m)/(13+m)+B*c^3*x^(15+m)/(15+m)");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:17
  public void test0677() {
    check( //
        "Integrate[(3*x-4*x^2)^(7/2), x]", //
        "-945/131072*(3-8*x)*(3*x-4*x^2)^(3/2)-21/2048*(3-8*x)*(3*x-4*x^2)^(5/2)-1/64*(3-8*x)*(3*x-4*x^2)^(7/2)-229635/16777216*ArcSin[1-8/3*x]-25515/4194304*(3-8*x)*Sqrt[3*x-4*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:33
  public void test0678() {
    check( //
        "Integrate[1/(3*I*x+4*x^2)^(7/2), x]", //
        "2/45*(3*I+8*x)/(3*I*x+4*x^2)^(5/2)+128/1215*(3*I+8*x)/(3*I*x+4*x^2)^(3/2)+4096/10935*(3*I+8*x)/Sqrt[3*I*x+4*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:88
  public void test0679() {
    check( //
        "Integrate[1/(a+c*x^2)^(9/2), x]", //
        "1/7*x/(a*(a+c*x^2)^(7/2))+6/35*x/(a^2*(a+c*x^2)^(5/2))+8/35*x/(a^3*(a+c*x^2)^(3/2))+16/35*x/(a^4*Sqrt[a+c*x^2])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:147
  public void test0680() {
    check( //
        "Integrate[Sqrt[2+5*x+3*x^2], x]", //
        "-1/24*ArcTanh[1/2*(5+6*x)/(Sqrt[3]*Sqrt[2+5*x+3*x^2])]/Sqrt[3]+1/12*(5+6*x)*Sqrt[2+5*x+3*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:15
  public void test0681() {
    check( //
        "Integrate[(b*x+c*x^2)^(1/2), x]", //
        "-1/4*b^2*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/c^(3/2)+1/4*(b+2*c*x)*Sqrt[b*x+c*x^2]/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:29
  public void test0682() {
    check( //
        "Integrate[(b*x+c*x^2)^(3/2)/x^4, x]", //
        "-2/3*(b*x+c*x^2)^(3/2)/x^3+2*c^(3/2)*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]-2*c*Sqrt[b*x+c*x^2]/x");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:43
  public void test0683() {
    check( //
        "Integrate[(a*x+b*x^2)^(5/2)/x^6, x]", //
        "-2/3*b*(a*x+b*x^2)^(3/2)/x^3-2/5*(a*x+b*x^2)^(5/2)/x^5+2*b^(5/2)*ArcTanh[x*Sqrt[b]/Sqrt[a*x+b*x^2]]-2*b^2*Sqrt[a*x+b*x^2]/x");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:59
  public void test0684() {
    check( //
        "Integrate[1/(b*x+c*x^2)^(1/2), x]", //
        "2*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/Sqrt[c]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:91
  public void test0685() {
    check( //
        "Integrate[x^(3/2)*(b*x+c*x^2)^(1/2), x]", //
        "16/105*b^2*(b*x+c*x^2)^(3/2)/(c^3*x^(3/2))-8/35*b*(b*x+c*x^2)^(3/2)/(c^2*Sqrt[x])+2/7*(b*x+c*x^2)^(3/2)*Sqrt[x]/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:105
  public void test0686() {
    check( //
        "Integrate[(b*x+c*x^2)^(3/2)/x^(5/2), x]", //
        "2/3*(b*x+c*x^2)^(3/2)/x^(3/2)-2*b^(3/2)*ArcTanh[Sqrt[b*x+c*x^2]/(Sqrt[b]*Sqrt[x])]+2*b*Sqrt[b*x+c*x^2]/Sqrt[x]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:121
  public void test0687() {
    check( //
        "Integrate[x^(13/2)/(b*x+c*x^2)^(3/2), x]", //
        "256/63*b^4*x^(3/2)/(c^5*Sqrt[b*x+c*x^2])-64/63*b^3*x^(5/2)/(c^4*Sqrt[b*x+c*x^2])+32/63*b^2*x^(7/2)/(c^3*Sqrt[b*x+c*x^2])-20/63*b*x^(9/2)/(c^2*Sqrt[b*x+c*x^2])+2/9*x^(11/2)/(c*Sqrt[b*x+c*x^2])+512/63*b^5*Sqrt[x]/(c^6*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:169
  public void test0688() {
    check( //
        "Integrate[x^3*Sqrt[a^2+2*a*b*x+b^2*x^2], x]", //
        "1/4*a*x^4*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+1/5*b*x^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:228
  public void test0689() {
    check( //
        "Integrate[1/(x*(a^2+2*a*b*x+b^2*x^2)^(3/2)), x]", //
        "1/(a^2*Sqrt[a^2+2*a*b*x+b^2*x^2])+1/2/(a*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])+(a+b*x)*Log[x]/(a^3*Sqrt[a^2+2*a*b*x+b^2*x^2])-(a+b*x)*Log[a+b*x]/(a^3*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:586
  public void test0690() {
    check( //
        "Integrate[(d+e*x)^3/(a+c*x^2), x]", //
        "3*d*e^2*x/c+1/2*e^3*x^2/c+1/2*e*(3*c*d^2-a*e^2)*Log[a+c*x^2]/c^2+d*(c*d^2-3*a*e^2)*ArcTan[x*Sqrt[c]/Sqrt[a]]/(c^(3/2)*Sqrt[a])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:600
  public void test0691() {
    check( //
        "Integrate[(d+e*x)^4/(a+c*x^2)^3, x]", //
        "-1/4*(a*e-c*d*x)*(d+e*x)^3/(a*c*(a+c*x^2)^2)-3/8*(c*d^2+a*e^2)*(a*e-c*d*x)*(d+e*x)/(a^2*c^2*(a+c*x^2))+3/8*(c*d^2+a*e^2)^2*ArcTan[x*Sqrt[c]/Sqrt[a]]/(a^(5/2)*c^(5/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:646
  public void test0692() {
    check( //
        "Integrate[(a+c*x^2)^(5/2)/(d+e*x)^7, x]", //
        "-5/24*a*c*(a*e-c*d*x)*(a+c*x^2)^(3/2)/((c*d^2+a*e^2)^2*(d+e*x)^4)-1/6*(a*e-c*d*x)*(a+c*x^2)^(5/2)/((c*d^2+a*e^2)*(d+e*x)^6)-5/16*a^3*c^3*ArcTanh[(a*e-c*d*x)/(Sqrt[c*d^2+a*e^2]*Sqrt[a+c*x^2])]/(c*d^2+a*e^2)^(7/2)-5/16*a^2*c^2*(a*e-c*d*x)*Sqrt[a+c*x^2]/((c*d^2+a*e^2)^3*(d+e*x)^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:921
  public void test0693() {
    check( //
        "Integrate[(a^2-b^2*x^2)^(3/2)/(a+b*x)^2, x]", //
        "1/2*(a^2-b^2*x^2)^(3/2)/(b*(a+b*x))+3/2*a^2*ArcTan[b*x/Sqrt[a^2-b^2*x^2]]/b+3/2*a*Sqrt[a^2-b^2*x^2]/b");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2862
  public void test0694() {
    check( //
        "Integrate[1/(a+b*x+c*x^2)^(1/4), x]", //
        "2*(b+2*c*x)*(a+b*x+c*x^2)^(1/4)/(Sqrt[c]*Sqrt[b^2-4*a*c]*(1+2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c]))+(b^2-4*a*c)^(3/4)*EllipticF[2*ArcTan[c^(1/4)*(a+b*x+c*x^2)^(1/4)*Sqrt[2]/(b^2-4*a*c)^(1/4)],1/2]*(1+2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c])*Sqrt[(b+2*c*x)^2/((b^2-4*a*c)*(1+2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c])^2)]/(c^(3/4)*(b+2*c*x)*Sqrt[2])-(b^2-4*a*c)^(3/4)*EllipticE[2*ArcTan[c^(1/4)*(a+b*x+c*x^2)^(1/4)*Sqrt[2]/(b^2-4*a*c)^(1/4)],1/2]*Sqrt[2]*(1+2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c])*Sqrt[(b+2*c*x)^2/((b^2-4*a*c)*(1+2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c])^2)]/(c^(3/4)*(b+2*c*x))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:36
  public void test0695() {
    check( //
        "Integrate[1/x^(1/2), x]", //
        "2*Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:178
  public void test0696() {
    check( //
        "Integrate[(a+b*x)^10/x^15, x]", //
        "-1/14*(a+b*x)^11/(a*x^14)+3/182*b*(a+b*x)^11/(a^2*x^13)-1/364*b^2*(a+b*x)^11/(a^3*x^12)+1/4004*b^3*(a+b*x)^11/(a^4*x^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:272
  public void test0697() {
    check( //
        "Integrate[1/(a+b*x)^10, x]", //
        "(-1/9)/(b*(a+b*x)^9)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:316
  public void test0698() {
    check( //
        "Integrate[1/(a^3+x*Sqrt[-a]), x]", //
        "Log[a^3+x*Sqrt[-a]]/Sqrt[-a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:458
  public void test0699() {
    check( //
        "Integrate[1/(-a+b*x)^(1/3), x]", //
        "3/2*(-a+b*x)^(2/3)/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:472
  public void test0700() {
    check( //
        "Integrate[1/(a+b*x)^(4/3), x]", //
        "(-3)/(b*(a+b*x)^(1/3))");
  }
}
