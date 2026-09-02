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
public class AlgebraicFunctions2 extends AbstractRubiTestCase {
  static boolean init = true;

  public AlgebraicFunctions2(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("AlgebraicFunctions2");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:305
  public void test0001() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/Sqrt[-c-d*x^2], x]", //
        "x*Sqrt[a+b*x^2]/Sqrt[-c-d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:319
  public void test0002() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/Sqrt[a+b*x^2], x]", //
        "d*x*Sqrt[a+b*x^2]/(b*Sqrt[c+d*x^2])+c^(3/2)*EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[a+b*x^2]/(a*Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[a+b*x^2]/(b*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:15
  public void test0003() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x, x]", //
        "1/2*(A*b+a*B)*x^2+1/4*b*B*x^4+a*A*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:29
  public void test0004() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^5, x]", //
        "-1/4*a^2*A/x^4-1/2*a*(2*A*b+a*B)/x^2+1/2*b^2*B*x^2+b*(A*b+2*a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:57
  public void test0005() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^14, x]", //
        "-1/13*a^5*A/x^13-1/11*a^4*(5*A*b+a*B)/x^11-5/9*a^3*b*(2*A*b+a*B)/x^9-10/7*a^2*b^2*(A*b+a*B)/x^7-a*b^3*(A*b+2*a*B)/x^5-1/3*b^4*(A*b+5*a*B)/x^3-b^5*B/x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:73
  public void test0006() {
    check( //
        "Integrate[x^2*(A+B*x^2)/(a+b*x^2), x]", //
        "(A*b-a*B)*x/b^2+1/3*B*x^3/b-(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(5/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:87
  public void test0007() {
    check( //
        "Integrate[x^6*(A+B*x^2)/(a+b*x^2)^2, x]", //
        "-a*(2*A*b-3*a*B)*x/b^4+1/3*(A*b-2*a*B)*x^3/b^3+1/5*B*x^5/b^2-1/2*a^2*(A*b-a*B)*x/(b^4*(a+b*x^2))+1/2*a^(3/2)*(5*A*b-7*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:115
  public void test0008() {
    check( //
        "Integrate[x^2*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "-1/4*(A*b-a*B)*x/(b^2*(a+b*x^2)^2)+1/8*(A*b-5*a*B)*x/(a*b^2*(a+b*x^2))+1/8*(A*b+3*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(5/2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:161
  public void test0009() {
    check( //
        "Integrate[x*(a+b*x^2)^2*(c+d*x^2), x]", //
        "1/6*(b*c-a*d)*(a+b*x^2)^3/b^2+1/8*d*(a+b*x^2)^4/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:175
  public void test0010() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^4, x]", //
        "-1/3*a^2*c^2/x^3-2*a*c*(b*c+a*d)/x+(b^2*c^2+4*a*b*c*d+a^2*d^2)*x+2/3*b*d*(b*c+a*d)*x^3+1/5*b^2*d^2*x^5");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:255
  public void test0011() {
    check( //
        "Integrate[x^5/((a+b*x^2)*(c+d*x^2)), x]", //
        "1/2*x^2/(b*d)+1/2*a^2*Log[a+b*x^2]/(b^2*(b*c-a*d))-1/2*c^2*Log[c+d*x^2]/(d^2*(b*c-a*d))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:315
  public void test0012() {
    check( //
        "Integrate[(c+d*x^2)^3/(a+b*x^2)^2, x]", //
        "d^2*(3*b*c-2*a*d)*x/b^3+1/3*d^3*x^3/b^2+1/2*(b*c-a*d)^3*x/(a*b^3*(a+b*x^2))+1/2*(b*c-a*d)^2*(b*c+5*a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(7/2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:331
  public void test0013() {
    check( //
        "Integrate[1/(x^5*(a+b*x^2)^2*(c+d*x^2)), x]", //
        "(-1/4)/(a^2*c*x^4)+1/2*(2*b*c+a*d)/(a^3*c^2*x^2)+1/2*b^3/(a^3*(b*c-a*d)*(a+b*x^2))+(3*b^2*c^2+2*a*b*c*d+a^2*d^2)*Log[x]/(a^4*c^3)-1/2*b^3*(3*b*c-4*a*d)*Log[a+b*x^2]/(a^4*(b*c-a*d)^2)-1/2*d^4*Log[c+d*x^2]/(c^3*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:405
  public void test0014() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^(7/2), x]", //
        "-2/5*a^2*A/x^(5/2)+2/3*b*(A*b+2*a*B)*x^(3/2)+2/7*b^2*B*x^(7/2)-2*a*(2*A*b+a*B)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:421
  public void test0015() {
    check( //
        "Integrate[(A+B*x^2)/(x^(3/2)*(a+b*x^2)), x]", //
        "(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(5/4)*b^(3/4)*Sqrt[2])-(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(5/4)*b^(3/4)*Sqrt[2])-1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(5/4)*b^(3/4)*Sqrt[2])+1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(5/4)*b^(3/4)*Sqrt[2])-2*A/(a*Sqrt[x])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:467
  public void test0016() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^(7/2), x]", //
        "-2/5*a^2*c^3/x^(5/2)+2/3*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^(3/2)+2/7*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^(7/2)+2/11*b*d^2*(3*b*c+2*a*d)*x^(11/2)+2/15*b^2*d^3*x^(15/2)-2*a*c^2*(2*b*c+3*a*d)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:517
  public void test0017() {
    check( //
        "Integrate[(c+d*x^2)^3/((a+b*x^2)^2*Sqrt[x]), x]", //
        "2/5*d^3*x^(5/2)/b^2-3/4*(b*c-a*d)^2*(b*c+3*a*d)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(7/4)*b^(13/4)*Sqrt[2])+3/4*(b*c-a*d)^2*(b*c+3*a*d)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(7/4)*b^(13/4)*Sqrt[2])-3/8*(b*c-a*d)^2*(b*c+3*a*d)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(7/4)*b^(13/4)*Sqrt[2])+3/8*(b*c-a*d)^2*(b*c+3*a*d)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(7/4)*b^(13/4)*Sqrt[2])+2*d^2*(3*b*c-2*a*d)*Sqrt[x]/b^3+1/2*(b*c-a*d)^3*Sqrt[x]/(a*b^3*(a+b*x^2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:64
  public void test0018() {
    check( //
        "Integrate[1/(c+d*(a+b*x))^(3/2), x]", //
        "(-2)/(b*d*Sqrt[c+d*(a+b*x)])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:322
  public void test0019() {
    check( //
        "Integrate[1/(x*(-1+b*x)), x]", //
        "-Log[x]+Log[1-b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:570
  public void test0020() {
    check( //
        "Integrate[Sqrt[a-b*x]/x^(9/2), x]", //
        "-2/7*(a-b*x)^(3/2)/(a*x^(7/2))-8/35*b*(a-b*x)^(3/2)/(a^2*x^(5/2))-16/105*b^2*(a-b*x)^(3/2)/(a^3*x^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:656
  public void test0021() {
    check( //
        "Integrate[1/(x^(3/2)*(a+b*x)^(5/2)), x]", //
        "2/3/(a*(a+b*x)^(3/2)*Sqrt[x])+8/3/(a^2*Sqrt[x]*Sqrt[a+b*x])-16/3*Sqrt[a+b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:712
  public void test0022() {
    check( //
        "Integrate[1/((2-b*x)^(5/2)*Sqrt[x]), x]", //
        "1/3*Sqrt[x]/(2-b*x)^(3/2)+1/3*Sqrt[x]/Sqrt[2-b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:844
  public void test0023() {
    check( //
        "Integrate[x^3*(a+b*x)*Sqrt[c*x^2], x]", //
        "1/5*a*x^4*Sqrt[c*x^2]+1/6*b*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:874
  public void test0024() {
    check( //
        "Integrate[(a+b*x)/(x*Sqrt[c*x^2]), x]", //
        "-a/Sqrt[c*x^2]+b*x*Log[x]/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:888
  public void test0025() {
    check( //
        "Integrate[x*(a+b*x)/(c*x^2)^(5/2), x]", //
        "-1/3*a/(c^2*x^2*Sqrt[c*x^2])-1/2*b/(c^2*x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:906
  public void test0026() {
    check( //
        "Integrate[x^3*(c*x^2)^(3/2)*(a+b*x)^2, x]", //
        "1/7*a^2*c*x^6*Sqrt[c*x^2]+1/4*a*b*c*x^7*Sqrt[c*x^2]+1/9*b^2*c*x^8*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:920
  public void test0027() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x^5, x]", //
        "1/3*c^2*(a+b*x)^3*Sqrt[c*x^2]/(b*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:968
  public void test0028() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^7*(a+b*x)), x]", //
        "-1/3*c*Sqrt[c*x^2]/(a*x^4)+1/2*b*c*Sqrt[c*x^2]/(a^2*x^3)-b^2*c*Sqrt[c*x^2]/(a^3*x^2)-b^3*c*Log[x]*Sqrt[c*x^2]/(a^4*x)+b^3*c*Log[a+b*x]*Sqrt[c*x^2]/(a^4*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:984
  public void test0029() {
    check( //
        "Integrate[1/(x*(a+b*x)*Sqrt[c*x^2]), x]", //
        "(-1)/(a*Sqrt[c*x^2])-b*x*Log[x]/(a^2*Sqrt[c*x^2])+b*x*Log[a+b*x]/(a^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1002
  public void test0030() {
    check( //
        "Integrate[Sqrt[c*x^2]/(a+b*x)^2, x]", //
        "a*Sqrt[c*x^2]/(b^2*x*(a+b*x))+Log[a+b*x]*Sqrt[c*x^2]/(b^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1036
  public void test0031() {
    check( //
        "Integrate[x*(a+b*x)^n*Sqrt[c*x^2], x]", //
        "a^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^3*(1+n)*x)-2*a*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^3*(2+n)*x)+(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^3*(3+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1060
  public void test0032() {
    check( //
        "Integrate[x^4*(a+b*x)^n/Sqrt[c*x^2], x]", //
        "-a^3*x*(a+b*x)^(1+n)/(b^4*(1+n)*Sqrt[c*x^2])+3*a^2*x*(a+b*x)^(2+n)/(b^4*(2+n)*Sqrt[c*x^2])-3*a*x*(a+b*x)^(3+n)/(b^4*(3+n)*Sqrt[c*x^2])+x*(a+b*x)^(4+n)/(b^4*(4+n)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1089
  public void test0033() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(1/2)*(a+b*x), x]", //
        "a*(d*x)^(2+m)*Sqrt[c*x^2]/(d^2*(2+m)*x)+b*(d*x)^(3+m)*Sqrt[c*x^2]/(d^3*(3+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1117
  public void test0034() {
    check( //
        "Integrate[(c*x^2)^p*(a+b*x)^(-1-2*p)/x, x]", //
        "1/2*(c*x^2)^p/(a*p*(a+b*x)^(2*p))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1143
  public void test0035() {
    check( //
        "Integrate[(b*c/d+b*x)^5/(c+d*x)^3, x]", //
        "1/3*b^5*(c+d*x)^3/d^6");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1339
  public void test0036() {
    check( //
        "Integrate[1/((a-I*a*x)^(9/4)*(a+I*a*x)^(1/4)), x]", //
        "(-4/5*I)/(a*(a-I*a*x)^(5/4)*(a+I*a*x)^(1/4))+2/5*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a^2*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1367
  public void test0037() {
    check( //
        "Integrate[1/((a-I*a*x)^(7/4)*(a+I*a*x)^(7/4)), x]", //
        "2/3*x/(a^2*(a-I*a*x)^(3/4)*(a+I*a*x)^(3/4))+2/3*(1+x^2)^(3/4)*EllipticF[1/2*ArcTan[x],2]/(a^2*(a-I*a*x)^(3/4)*(a+I*a*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1573
  public void test0038() {
    check( //
        "Integrate[1/(c+d*x)^2, x]", //
        "(-1)/(d*(c+d*x))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1649
  public void test0039() {
    check( //
        "Integrate[1/(c+d*x)^(1/2), x]", //
        "2*Sqrt[c+d*x]/d");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1677
  public void test0040() {
    check( //
        "Integrate[(a+b*x)^5/(a*c+b*c*x)^(1/2), x]", //
        "2/11*(a*c+b*c*x)^(11/2)/(b*c^6)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1755
  public void test0041() {
    check( //
        "Integrate[(a+b*x)^(1/2)/(c+d*x)^(5/2), x]", //
        "2/3*(a+b*x)^(3/2)/((b*c-a*d)*(c+d*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1769
  public void test0042() {
    check( //
        "Integrate[1/Sqrt[2+b*x]^2, x]", //
        "Log[2+b*x]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1783
  public void test0043() {
    check( //
        "Integrate[1/Sqrt[2-b*x]^2, x]", //
        "-Log[2-b*x]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1797
  public void test0044() {
    check( //
        "Integrate[1/(Sqrt[a-b*x]*Sqrt[c+d*x]), x]", //
        "-2*ArcTan[Sqrt[d]*Sqrt[a-b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1945
  public void test0045() {
    check( //
        "Integrate[(c+d*x)^(5/4)/(a+b*x)^(21/4), x]", //
        "-4/17*(c+d*x)^(9/4)/((b*c-a*d)*(a+b*x)^(17/4))+32/221*d*(c+d*x)^(9/4)/((b*c-a*d)^2*(a+b*x)^(13/4))-128/1989*d^2*(c+d*x)^(9/4)/((b*c-a*d)^3*(a+b*x)^(9/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1961
  public void test0046() {
    check( //
        "Integrate[1/((a+b*x)^(15/4)*(c+d*x)^(1/4)), x]", //
        "-4/11*(c+d*x)^(3/4)/((b*c-a*d)*(a+b*x)^(11/4))+32/77*d*(c+d*x)^(3/4)/((b*c-a*d)^2*(a+b*x)^(7/4))-128/231*d^2*(c+d*x)^(3/4)/((b*c-a*d)^3*(a+b*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2164
  public void test0047() {
    check( //
        "Integrate[(a+b*x)^(-1-b*c/(b*c-a*d))*(c+d*x)^(-1+a*d/(b*c-a*d)), x]", //
        "-(c+d*x)^(a*d/(b*c-a*d))/(b*c*(a+b*x)^(b*c/(b*c-a*d)))+(c+d*x)^(a*d/(b*c-a*d))/(a*b*c*(a+b*x)^(a*d/(b*c-a*d)))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2190
  public void test0048() {
    check( //
        "Integrate[1/x^5+x+x^5, x]", //
        "(-1/4)/x^4+1/2*x^2+1/6*x^6");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:55
  public void test0049() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^11, x]", //
        "-1/10*a^6*c^5/x^10+4/9*a^5*b*c^5/x^9-5/8*a^4*b^2*c^5/x^8+5/6*a^2*b^4*c^5/x^6-4/5*a*b^5*c^5/x^5+1/4*b^6*c^5/x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:127
  public void test0050() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^3, x]", //
        "-1/2*a^2*A/x^2-a*(2*A*b+a*B)/x+b^2*B*x+b*(A*b+2*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:141
  public void test0051() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^4, x]", //
        "-1/3*a^3*A/x^3-1/2*a^2*(3*A*b+a*B)/x^2-3*a*b*(A*b+a*B)/x+b^3*B*x+b^2*(A*b+3*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:155
  public void test0052() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^2, x]", //
        "-a^5*A/x+5*a^3*b*(2*A*b+a*B)*x+5*a^2*b^2*(A*b+a*B)*x^2+5/3*a*b^3*(A*b+2*a*B)*x^3+1/4*b^4*(A*b+5*a*B)*x^4+1/5*b^5*B*x^5+a^4*(5*A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:183
  public void test0053() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^7, x]", //
        "-1/6*a^10*A/x^6-1/5*a^9*(10*A*b+a*B)/x^5-5/4*a^8*b*(9*A*b+2*a*B)/x^4-5*a^7*b^2*(8*A*b+3*a*B)/x^3-15*a^6*b^3*(7*A*b+4*a*B)/x^2-42*a^5*b^4*(6*A*b+5*a*B)/x+30*a^3*b^6*(4*A*b+7*a*B)*x+15/2*a^2*b^7*(3*A*b+8*a*B)*x^2+5/3*a*b^8*(2*A*b+9*a*B)*x^3+1/4*b^9*(A*b+10*a*B)*x^4+1/5*b^10*B*x^5+42*a^4*b^5*(5*A*b+6*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:197
  public void test0054() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^21, x]", //
        "-1/20*a^10*A/x^20-1/19*a^9*(10*A*b+a*B)/x^19-5/18*a^8*b*(9*A*b+2*a*B)/x^18-15/17*a^7*b^2*(8*A*b+3*a*B)/x^17-15/8*a^6*b^3*(7*A*b+4*a*B)/x^16-14/5*a^5*b^4*(6*A*b+5*a*B)/x^15-3*a^4*b^5*(5*A*b+6*a*B)/x^14-30/13*a^3*b^6*(4*A*b+7*a*B)/x^13-5/4*a^2*b^7*(3*A*b+8*a*B)/x^12-5/11*a*b^8*(2*A*b+9*a*B)/x^11-1/10*b^9*(A*b+10*a*B)/x^10-1/9*b^10*B/x^9");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:273
  public void test0055() {
    check( //
        "Integrate[x*(A+B*x)/(a+b*x)^3, x]", //
        "1/2*a*(A*b-a*B)/(b^3*(a+b*x)^2)+(-A*b+2*a*B)/(b^3*(a+b*x))+B*Log[a+b*x]/b^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:458
  public void test0056() {
    check( //
        "Integrate[(A+B*x)/(x^(7/2)*(a+b*x)), x]", //
        "-2/5*A/(a*x^(5/2))+2/3*(A*b-a*B)/(a^2*x^(3/2))-2*b^(3/2)*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/a^(7/2)-2*b*(A*b-a*B)/(a^3*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:630
  public void test0057() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/x^(15/2), x]", //
        "-2/13*A*(a+b*x)^(3/2)/(a*x^(13/2))+2/143*(10*A*b-13*a*B)*(a+b*x)^(3/2)/(a^2*x^(11/2))-16/1287*b*(10*A*b-13*a*B)*(a+b*x)^(3/2)/(a^3*x^(9/2))+32/3003*b^2*(10*A*b-13*a*B)*(a+b*x)^(3/2)/(a^4*x^(7/2))-128/15015*b^3*(10*A*b-13*a*B)*(a+b*x)^(3/2)/(a^5*x^(5/2))+256/45045*b^4*(10*A*b-13*a*B)*(a+b*x)^(3/2)/(a^6*x^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:688
  public void test0058() {
    check( //
        "Integrate[(A+B*x)/(x^(9/2)*(a+b*x)^(5/2)), x]", //
        "-2/7*A/(a*x^(7/2)*(a+b*x)^(3/2))-2/21*(10*A*b-7*a*B)/(a^2*x^(5/2)*(a+b*x)^(3/2))-16/21*(10*A*b-7*a*B)/(a^3*x^(5/2)*Sqrt[a+b*x])+32/35*(10*A*b-7*a*B)*Sqrt[a+b*x]/(a^4*x^(5/2))-128/105*b*(10*A*b-7*a*B)*Sqrt[a+b*x]/(a^5*x^(3/2))+256/105*b^2*(10*A*b-7*a*B)*Sqrt[a+b*x]/(a^6*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:756
  public void test0059() {
    check( //
        "Integrate[(a+b*x)^(3/2)*Sqrt[c+d*x]/x, x]", //
        "-1/4*(b^2*c^2-6*a*b*c*d-3*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(d^(3/2)*Sqrt[b])-2*a^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]+1/2*(a+b*x)^(3/2)*Sqrt[c+d*x]+1/4*(b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/d");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:770
  public void test0060() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^6, x]", //
        "1/8*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(a*c^2*x^4)-1/5*(a+b*x)^(5/2)*(c+d*x)^(5/2)/(a*c*x^5)+3/128*(b*c-a*d)^4*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(7/2))+1/64*(b*c-a*d)^2*(b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*c^3*x^2)+1/16*(b*c-a*d)*(b*c+a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*c^3*x^3)-3/128*(b*c-a*d)^3*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:786
  public void test0061() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x*Sqrt[c+d*x]), x]", //
        "-(b*c-3*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]/d^(3/2)-2*a^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[c]+b*Sqrt[a+b*x]*Sqrt[c+d*x]/d");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:832
  public void test0062() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^5, x]", //
        "-5/24*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c*x^3)-1/4*(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^4+5/64*(b^4*c^4-20*a*b^3*c^3*d-90*a^2*b^2*c^2*d^2-20*a^3*b*c*d^3+a^4*d^4)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(3/2))+5*b^(3/2)*d^(3/2)*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]-5/192*(3*b*c+a*d)*(b^2*c^2+24*a*b*c*d-a^2*d^2)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^2*x)-5/96*(3*b^2*c^2+14*a*b*c*d-a^2*d^2)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c^2*x^2)+5/64*d*(b^3*c^3+45*a*b^2*c^2*d+19*a^2*b*c*d^2-a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:880
  public void test0063() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^5*Sqrt[a+b*x]), x]", //
        "-1/64*(b*c-a*d)^2*(35*b^2*c^2+10*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(5/2))-1/4*c*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x^4)+1/24*(7*b*c-9*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*x^3)-1/96*(35*b^2*c^2-46*a*b*c*d+3*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c*x^2)+1/192*(105*b^3*c^3-145*a*b^2*c^2*d+15*a^2*b*c*d^2+9*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:910
  public void test0064() {
    check( //
        "Integrate[x^3/((c+d*x)^(3/2)*Sqrt[a+b*x]), x]", //
        "3/4*(5*b^2*c^2+2*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(7/2))-2*c*x^2*Sqrt[a+b*x]/(d*(b*c-a*d)*Sqrt[c+d*x])-1/4*((5*b*c-3*a*d)*(3*b*c+a*d)-2*b*d*(5*b*c-a*d)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^2*d^3*(b*c-a*d))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:924
  public void test0065() {
    check( //
        "Integrate[1/(x^3*(c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "-1/4*(3*b^2*c^2+10*a*b*c*d+35*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(9/2))+1/12*d*(9*b^2*c^2+18*a*b*c*d-35*a^2*d^2)*Sqrt[a+b*x]/(a^2*c^3*(b*c-a*d)*(c+d*x)^(3/2))-1/2*Sqrt[a+b*x]/(a*c*x^2*(c+d*x)^(3/2))+1/4*(3*b*c+7*a*d)*Sqrt[a+b*x]/(a^2*c^2*x*(c+d*x)^(3/2))+1/12*d*(9*b^3*c^3+15*a*b^2*c^2*d-145*a^2*b*c*d^2+105*a^3*d^3)*Sqrt[a+b*x]/(a^2*c^4*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:958
  public void test0066() {
    check( //
        "Integrate[1/(x^3*(a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "-3/4*(5*b^2*c^2+6*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(7/2))+1/4*b*(15*b^2*c^2-2*a*b*c*d-5*a^2*d^2)/(a^3*c^2*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])+(-1/2)/(a*c*x^2*Sqrt[a+b*x]*Sqrt[c+d*x])+5/4*(b*c+a*d)/(a^2*c^2*x*Sqrt[a+b*x]*Sqrt[c+d*x])+1/4*d*(b*c+a*d)*(15*b^2*c^2-22*a*b*c*d+15*a^2*d^2)*Sqrt[a+b*x]/(a^3*c^3*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:992
  public void test0067() {
    check( //
        "Integrate[1/(x*(a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "2/3*b/(a*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(5/2))+2*b*(b*c-3*a*d)/(a^2*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])+2/3*d*(3*b^2*c^2-10*a*b*c*d-a^2*d^2)*Sqrt[a+b*x]/(a^2*c*(b*c-a*d)^3*(c+d*x)^(3/2))+2/3*d*(b*c+a*d)*(3*b^2*c^2-14*a*b*c*d+3*a^2*d^2)*Sqrt[a+b*x]/(a^2*c^2*(b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1025
  public void test0068() {
    check( //
        "Integrate[1/(x^2*Sqrt[-1+x]*Sqrt[1+x]), x]", //
        "Sqrt[-1+x]*Sqrt[1+x]/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1114
  public void test0069() {
    check( //
        "Integrate[1/((1-x)^(1/4)*(e*x)^(9/2)*(1+x)^(1/4)), x]", //
        "-2/3*(1-x^2)^(3/4)/(e*(e*x)^(7/2))+8/21*(1-x^2)^(7/4)/(e*(e*x)^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1247
  public void test0070() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/(d+e*x)^3, x]", //
        "-1/2*(b*d-a*e)*(B*d-A*e)/(e^3*(d+e*x)^2)+(2*b*B*d-A*b*e-a*B*e)/(e^3*(d+e*x))+b*B*Log[d+e*x]/e^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1275
  public void test0071() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^6, x]", //
        "-1/5*(B*d-A*e)*(a+b*x)^4/(e*(b*d-a*e)*(d+e*x)^5)+1/20*(4*b*B*d+A*b*e-5*a*B*e)*(a+b*x)^4/(e*(b*d-a*e)^2*(d+e*x)^4)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1303
  public void test0072() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^15, x]", //
        "1/14*(b*d-a*e)^6*(B*d-A*e)/(e^8*(d+e*x)^14)-1/13*(b*d-a*e)^5*(7*b*B*d-6*A*b*e-a*B*e)/(e^8*(d+e*x)^13)+1/4*b*(b*d-a*e)^4*(7*b*B*d-5*A*b*e-2*a*B*e)/(e^8*(d+e*x)^12)-5/11*b^2*(b*d-a*e)^3*(7*b*B*d-4*A*b*e-3*a*B*e)/(e^8*(d+e*x)^11)+1/2*b^3*(b*d-a*e)^2*(7*b*B*d-3*A*b*e-4*a*B*e)/(e^8*(d+e*x)^10)-1/3*b^4*(b*d-a*e)*(7*b*B*d-2*A*b*e-5*a*B*e)/(e^8*(d+e*x)^9)+1/8*b^5*(7*b*B*d-A*b*e-6*a*B*e)/(e^8*(d+e*x)^8)-1/7*b^6*B/(e^8*(d+e*x)^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1331
  public void test0073() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^14, x]", //
        "-1/13*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^13)+1/156*(11*b*B*d+2*A*b*e-13*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^12)+1/1716*b*(11*b*B*d+2*A*b*e-13*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^3*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1899
  public void test0074() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)/(1-2*x)^3, x]", //
        "26411/128/(1-2*x)^2+(-57281/64)/(1-2*x)-540*x-3861/32*x^2-135/8*x^3-24843/32*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1913
  public void test0075() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)^2/(1-2*x)^3, x]", //
        "290521/256/(1-2*x)^2+(-381073/64)/(1-2*x)-176055/32*x-54783/32*x^2-7245/16*x^3-2025/32*x^4-832951/128*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1927
  public void test0076() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)^3/(1-2*x)^3, x]", //
        "456533/256/(1-2*x)^2+(-302379/32)/(1-2*x)-284071/32*x-44595/16*x^2-11925/16*x^3-3375/32*x^4-1334949/128*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2086
  public void test0077() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)^2*Sqrt[1-2*x], x]", //
        "-290521/192*(1-2*x)^(3/2)+381073/160*(1-2*x)^(5/2)-118993/64*(1-2*x)^(7/2)+40453/48*(1-2*x)^(9/2)-159111/704*(1-2*x)^(11/2)+13905/416*(1-2*x)^(13/2)-135/64*(1-2*x)^(15/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2100
  public void test0078() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)^3*Sqrt[1-2*x], x]", //
        "-65219/96*(1-2*x)^(3/2)+144837/160*(1-2*x)^(5/2)-64317/112*(1-2*x)^(7/2)+28555/144*(1-2*x)^(9/2)-12675/352*(1-2*x)^(11/2)+1125/416*(1-2*x)^(13/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2116
  public void test0079() {
    check( //
        "Integrate[(2+3*x)*Sqrt[1-2*x]/(3+5*x), x]", //
        "-1/5*(1-2*x)^(3/2)-2/25*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2/25*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2176
  public void test0080() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^3/(2+3*x), x]", //
        "-2/243*(1-2*x)^(3/2)-1027/108*(1-2*x)^(5/2)+400/63*(1-2*x)^(7/2)-125/108*(1-2*x)^(9/2)+14/243*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-14/243*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2192
  public void test0081() {
    check( //
        "Integrate[(1-2*x)^(3/2)/(3+5*x), x]", //
        "2/15*(1-2*x)^(3/2)-22/25*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/25*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2268
  public void test0082() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)/(3+5*x), x]", //
        "22/375*(1-2*x)^(3/2)+2/125*(1-2*x)^(5/2)-3/35*(1-2*x)^(7/2)-242/625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+242/625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2344
  public void test0083() {
    check( //
        "Integrate[1/((3+5*x)*Sqrt[1-2*x]), x]", //
        "-2*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]/Sqrt[55]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2420
  public void test0084() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)*(3+5*x)), x]", //
        "6/7*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[3/7]-10/11*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[5/11]+4/77/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2606
  public void test0085() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x), x]", //
        "-2/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+793/216*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1/6*(3+5*x)^(3/2)*Sqrt[1-2*x]-41/72*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2620
  public void test0086() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3, x]", //
        "25/9*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+2119/252*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-59/84*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)-1/6*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^2+215/84*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2636
  public void test0087() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x]), x]", //
        "-1177/28*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/14*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+107/28*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2668
  public void test0088() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^5, x]", //
        "3/28*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^4+181/168*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-240911/3136*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1991/224*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-21901/3136*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2712
  public void test0089() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^3*(3+5*x)^(3/2)), x]", //
        "5709/4*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/14*(1-2*x)^(5/2)/((2+3*x)^2*Sqrt[3+5*x])+173/28*(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-5709/28*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2744
  public void test0090() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^3, x]", //
        "-1/6*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^2+115/36*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)-1945/324*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-6829/162*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+41/18*(3+5*x)^(3/2)*Sqrt[1-2*x]-1649/108*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2758
  public void test0091() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^5, x]", //
        "-1/12*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^4+185/216*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^3+3304795/326592*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1850/729*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-3485/4032*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)+1165/2592*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^2+249575/108864*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2774
  public void test0092() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^5*Sqrt[3+5*x]), x]", //
        "-1643785/448*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/28*(1-2*x)^(7/2)*Sqrt[3+5*x]/(2+3*x)^4+247/168*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^3+13585/672*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+149435/448*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2820
  public void test0093() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^4*Sqrt[1-2*x]), x]", //
        "-5445/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-15/196*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+1/7*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-495/2744*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2836
  public void test0094() {
    check( //
        "Integrate[(2+3*x)^4/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "10866247/128000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-259/800*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-3/40*(2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x]-7/128000*(187559+77820*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2850
  public void test0095() {
    check( //
        "Integrate[1/((2+3*x)*(3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "6*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-10/11*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2896
  public void test0096() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^2), x]", //
        "-25/9*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-169/441*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/7*(3+5*x)^(3/2)/((2+3*x)*Sqrt[1-2*x])+32/147*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2912
  public void test0097() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^4*Sqrt[3+5*x]), x]", //
        "-102345/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-32735/15092*Sqrt[3+5*x]/Sqrt[1-2*x]+1/7*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])+27/28*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])+2865/392*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2926
  public void test0098() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-27/25*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^2/((3+5*x)^(3/2)*Sqrt[1-2*x])-1/99825*(24439+38770*x)*Sqrt[1-2*x]/(3+5*x)^(3/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2944
  public void test0099() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^3), x]", //
        "-765/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^2)+415/22638*Sqrt[3+5*x]/Sqrt[1-2*x]-1/14*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])+5/196*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2974
  public void test0100() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^2*Sqrt[3+5*x]), x]", //
        "-405/343*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-190/1617*Sqrt[3+5*x]/(1-2*x)^(3/2)+3/7*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x))-4390/124509*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2988
  public void test0101() {
    check( //
        "Integrate[(2+3*x)^4/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^3/((1-2*x)^(3/2)*(3+5*x)^(3/2))+81/50*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-203/242*(2+3*x)^2/((3+5*x)^(3/2)*Sqrt[1-2*x])+1/2196150*(627287+991010*x)*Sqrt[1-2*x]/(3+5*x)^(3/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3024
  public void test0102() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2), x]", //
        "-49/27*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+8/27*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+40/27*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3038
  public void test0103() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(13/2), x]", //
        "-1305025844/47647845*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-37904696/47647845*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-118/2079*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-2/33*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(11/2)-13022/305613*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+627806/10696455*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+19417096/74875185*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+1305025844/524126295*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3054
  public void test0104() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2), x]", //
        "-62/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-2/5*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3072
  public void test0105() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)*Sqrt[3+5*x], x]", //
        "2/55*(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2)-604915631/17718750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-18177329/17718750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1103/259875*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+178/7425*(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-124891/2165625*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-18177329/38981250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3086
  public void test0106() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(5/2), x]", //
        "-2/9*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(3/2)+592/81*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-230/81*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+74/9*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]-1150/81*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3100
  public void test0107() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(13/2), x]", //
        "-2/33*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(11/2)-3316711588/61261515*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-103970992/61261515*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-13292/43659*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)+362/891*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-1366496/4584195*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+45748292/96268095*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+3316711588/673876665*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3116
  public void test0108() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((3+5*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "62/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+8/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-22/5*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3134
  public void test0109() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)*Sqrt[3+5*x], x]", //
        "106/2475*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2)+2/55*(1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2)-326256461/17718750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-4738087/8859375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2866/86625*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+38729/2165625*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-4738087/19490625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3148
  public void test0110() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(5/2), x]", //
        "-2/9*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(3/2)+116854/6075*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-43214/6075*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+230/27*(1-2*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[2+3*x]+788/135*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-43214/1215*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3162
  public void test0111() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(13/2), x]", //
        "-2/33*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(11/2)+370/891*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(9/2)-584888452/5250987*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-13235368/5250987*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-55772/43659*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)+36980/18711*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)-17089252/8251551*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+584888452/57760857*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3178
  public void test0112() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(5/2)/(3+5*x)^(3/2), x]", //
        "-1509007/2953125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-299863/2953125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(5/2)*(2+3*x)^(5/2)/Sqrt[3+5*x]-8/45*(1-2*x)^(3/2)*(2+3*x)^(5/2)*Sqrt[3+5*x]+167228/118125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-1972/4725*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+196499/590625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3192
  public void test0113() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-6388/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-64/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+14/3*(1-2*x)^(3/2)/((3+5*x)^(3/2)*Sqrt[2+3*x])-1012/15*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+6388/15*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3210
  public void test0114() {
    check( //
        "Integrate[(3+5*x)^(3/2)/(Sqrt[1-2*x]*Sqrt[2+3*x]), x]", //
        "-31/9*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/9*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-5/9*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3224
  public void test0115() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(11/2)*Sqrt[1-2*x]), x]", //
        "-32098184/47647845*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2036756/47647845*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/189*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)+808/27783*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-168034/972405*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-43094/6806835*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+32098184/47647845*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3242
  public void test0116() {
    check( //
        "Integrate[1/(Sqrt[-2-x]*Sqrt[-1-x]*Sqrt[-3+x]), x]", //
        "-2*EllipticF[ArcSin[1/Sqrt[2/5+1/5*x]],1/5]*Sqrt[1+x]*Sqrt[2+x]/(Sqrt[5]*Sqrt[-2-x]*Sqrt[-1-x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3256
  public void test0117() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-31/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-4/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-2/55*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3270
  public void test0118() {
    check( //
        "Integrate[1/((2+3*x)^(7/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-352875016/18865*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-10614544/18865*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+6/35*Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(3/2))+576/245*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+120324/1715*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-5307272/11319*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+352875016/124509*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3294
  public void test0119() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(5/2)), x]", //
        "-38/343*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-212/343*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/7*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])-8/49*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+38/343*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3308
  public void test0120() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[2+3*x]/(1-2*x)^(3/2), x]", //
        "6599/45*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+397/90*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(3+5*x)^(5/2)*Sqrt[2+3*x]/Sqrt[1-2*x]+3*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+397/18*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3324
  public void test0121() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(7/2)*Sqrt[3+5*x]), x]", //
        "-244604/12005*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-7536/12005*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])+138/2695*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+10308/18865*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+733812/132055*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3338
  public void test0122() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "494/605*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-214/605*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/11*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/363*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-494/3993*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3356
  public void test0123() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^(7/2)), x]", //
        "-2092/84035*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-189368/84035*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(5/2))+458/1617*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-2818/18865*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-5438/132055*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+189368/924385*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3370
  public void test0124() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^(3/2)), x]", //
        "-17/343*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-146/343*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+11/21*(3+5*x)^(3/2)/((1-2*x)^(3/2)*Sqrt[2+3*x])-143/49*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])+438/343*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3386
  public void test0125() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "-5327983/30250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-160297/30250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(7/2)/((1-2*x)^(3/2)*Sqrt[3+5*x])-665/363*(2+3*x)^(5/2)/(Sqrt[1-2*x]*Sqrt[3+5*x])+3284/19965*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-153319/66550*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3400
  public void test0126() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "592/1331*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-230/1331*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*Sqrt[2+3*x]/((1-2*x)^(3/2)*(3+5*x)^(3/2))+26/121*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-575/3993*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-2960/43923*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3471
  public void test0127() {
    check( //
        "Integrate[(a+b*x)^(4/3)/((c+d*x)^(4/3)*(e+f*x)), x]", //
        "3*(b*c-a*d)*(a+b*x)^(1/3)/(d*(d*e-c*f)*(c+d*x)^(1/3))-1/2*b^(4/3)*Log[a+b*x]/(d^(4/3)*f)-1/2*(b*e-a*f)^(4/3)*Log[e+f*x]/(f*(d*e-c*f)^(4/3))+3/2*(b*e-a*f)^(4/3)*Log[-(a+b*x)^(1/3)+(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/(d*e-c*f)^(1/3)]/(f*(d*e-c*f)^(4/3))-3/2*b^(4/3)*Log[-1+b^(1/3)*(c+d*x)^(1/3)/(d^(1/3)*(a+b*x)^(1/3))]/(d^(4/3)*f)-b^(4/3)*ArcTan[1/Sqrt[3]+2*b^(1/3)*(c+d*x)^(1/3)/(d^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]*Sqrt[3]/(d^(4/3)*f)+(b*e-a*f)^(4/3)*ArcTan[1/Sqrt[3]+2*(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/((d*e-c*f)^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]*Sqrt[3]/(f*(d*e-c*f)^(4/3))");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:42
  public void test0128() {
    check( //
        "Integrate[(1+a*x)/(x^2*Sqrt[a*x]*Sqrt[1-a*x]), x]", //
        "-2/3*a*Sqrt[1-a*x]/(a*x)^(3/2)-10/3*a*Sqrt[1-a*x]/Sqrt[a*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:71
  public void test0129() {
    check( //
        "Integrate[(7+5*x)*Sqrt[2-3*x]*Sqrt[1+4*x]/Sqrt[-5+2*x], x]", //
        "-4543/36*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+1/4*(1+4*x)^(3/2)*Sqrt[2-3*x]*Sqrt[-5+2*x]+1397/27*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+95/18*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:123
  public void test0130() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(9/2), x]", //
        "-2/35*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(7/2)+2558/695175*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(5/2)+23758016/57992193675*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)+32843987836/451524900265803*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]-65687975672/2257624501329015*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]-1212290288/1867348636335*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[11/23]*Sqrt[7+5*x]/(Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])+32843987836/57887807726385*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:32
  public void test0131() {
    check( //
        "Integrate[(a+b*x^2)^2/x^3, x]", //
        "-1/2*a^2/x^2+1/2*b^2*x^2+2*a*b*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:46
  public void test0132() {
    check( //
        "Integrate[(a+b*x^2)^3/x^3, x]", //
        "-1/2*a^3/x^2+3/2*a*b^2*x^2+1/4*b^3*x^4+3*a^2*b*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:74
  public void test0133() {
    check( //
        "Integrate[(a+b*x^2)^5/x^9, x]", //
        "-1/8*a^5/x^8-5/6*a^4*b/x^6-5/2*a^3*b^2/x^4-5*a^2*b^3/x^2+1/2*b^5*x^2+5*a*b^4*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:116
  public void test0134() {
    check( //
        "Integrate[(a+b*x^2)^8/x^27, x]", //
        "-1/26*(a+b*x^2)^9/(a*x^26)+1/78*b*(a+b*x^2)^9/(a^2*x^24)-1/286*b^2*(a+b*x^2)^9/(a^3*x^22)+1/1430*b^3*(a+b*x^2)^9/(a^4*x^20)-1/12870*b^4*(a+b*x^2)^9/(a^5*x^18)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:174
  public void test0135() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)^2), x]", //
        "(-1/2)/(a^2*x^2)-1/2*b/(a^2*(a+b*x^2))-2*b*Log[x]/a^3+b*Log[a+b*x^2]/a^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:216
  public void test0136() {
    check( //
        "Integrate[x^3/(a+b*x^2)^10, x]", //
        "1/18*a/(b^2*(a+b*x^2)^9)+(-1/16)/(b^2*(a+b*x^2)^8)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:244
  public void test0137() {
    check( //
        "Integrate[1/(x^3*(a-b*x^2)), x]", //
        "(-1/2)/(a*x^2)+b*Log[x]/a^2-1/2*b*Log[a-b*x^2]/a^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:258
  public void test0138() {
    check( //
        "Integrate[1/(x^3*(a-b*x^2)^3), x]", //
        "(-1/2)/(a^3*x^2)+1/4*b/(a^2*(a-b*x^2)^2)+b/(a^3*(a-b*x^2))+3*b*Log[x]/a^4-3/2*b*Log[a-b*x^2]/a^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:274
  public void test0139() {
    check( //
        "Integrate[1/(x*(1+b*x^2)^2), x]", //
        "1/2/(1+b*x^2)+Log[x]-1/2*Log[1+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:402
  public void test0140() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/x^8, x]", //
        "-1/7*(a+b*x^2)^(3/2)/(a*x^7)+4/35*b*(a+b*x^2)^(3/2)/(a^2*x^5)-8/105*b^2*(a+b*x^2)^(3/2)/(a^3*x^3)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:472
  public void test0141() {
    check( //
        "Integrate[(a+b*x^2)^(9/2)/x^18, x]", //
        "-1/17*(a+b*x^2)^(11/2)/(a*x^17)+2/85*b*(a+b*x^2)^(11/2)/(a^2*x^15)-8/1105*b^2*(a+b*x^2)^(11/2)/(a^3*x^13)+16/12155*b^3*(a+b*x^2)^(11/2)/(a^4*x^11)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:514
  public void test0142() {
    check( //
        "Integrate[Sqrt[-9-4*x^2], x]", //
        "-9/4*ArcTan[2*x/Sqrt[-9-4*x^2]]+1/2*x*Sqrt[-9-4*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:874
  public void test0143() {
    check( //
        "Integrate[(a-b*x^2)^(3/4), x]", //
        "2/5*x*(a-b*x^2)^(3/4)+6/5*a^(3/2)*(1-b*x^2/a)^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:918
  public void test0144() {
    check( //
        "Integrate[1/(x^6*(a+b*x^2)^(5/4)), x]", //
        "(-1/5)/(a*x^5*(a+b*x^2)^(1/4))+11/30*b/(a^2*x^3*(a+b*x^2)^(1/4))-77/60*b^2/(a^3*x*(a+b*x^2)^(1/4))-77/20*b^(5/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(a^(7/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1036
  public void test0145() {
    check( //
        "Integrate[(c*x)^(1/2)/(a-b*x^2)^(1/4), x]", //
        "-c*(a-b*x^2)^(3/4)/(b*Sqrt[c*x])+(1-a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCsc[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]*Sqrt[c*x]/((a-b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1064
  public void test0146() {
    check( //
        "Integrate[1/((c*x)^(9/2)*(a+b*x^2)^(5/4)), x]", //
        "2/(a*c*(c*x)^(7/2)*(a+b*x^2)^(1/4))-16/3*(a+b*x^2)^(3/4)/(a^2*c*(c*x)^(7/2))+64/21*(a+b*x^2)^(7/4)/(a^3*c*(c*x)^(7/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:114
  public void test0147() {
    check( //
        "Integrate[(a+b*x^2)^3/(c+d*x^2)^(11/2), x]", //
        "-1/9*d*x*(a+b*x^2)^4/(c*(b*c-a*d)*(c+d*x^2)^(9/2))+1/63*(9*b*c-8*a*d)*x*(a+b*x^2)^3/(c^2*(b*c-a*d)*(c+d*x^2)^(7/2))+2/105*a*(9*b*c-8*a*d)*x*(a+b*x^2)^2/(c^3*(b*c-a*d)*(c+d*x^2)^(5/2))+8/315*a^2*(9*b*c-8*a*d)*x*(a+b*x^2)/(c^4*(b*c-a*d)*(c+d*x^2)^(3/2))+16/315*a^3*(9*b*c-8*a*d)*x/(c^5*(b*c-a*d)*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:180
  public void test0148() {
    check( //
        "Integrate[1/((-a-b*x^2)^(1/3)*(9*a*d/b+d*x^2)), x]", //
        "-1/12*ArcTan[1/3*(a^(1/3)+(-a-b*x^2)^(1/3))^2/(a^(1/6)*x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d)-1/12*ArcTan[1/3*x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/(a^(5/6)*d)+1/4*ArcTanh[a^(1/6)*(a^(1/3)+(-a-b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:306
  public void test0149() {
    check( //
        "Integrate[Sqrt[-a-b*x^2]/Sqrt[-c-d*x^2], x]", //
        "x*Sqrt[-a-b*x^2]/Sqrt[-c-d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]/(Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]/(Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:320
  public void test0150() {
    check( //
        "Integrate[Sqrt[-c-d*x^2]/Sqrt[a+b*x^2], x]", //
        "-d*x*Sqrt[a+b*x^2]/(b*Sqrt[-c-d*x^2])-c^(3/2)*EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[a+b*x^2]/(a*Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])+EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[a+b*x^2]/(b*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:16
  public void test0151() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^2, x]", //
        "-a*A/x+(A*b+a*B)*x+1/3*b*B*x^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:30
  public void test0152() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^6, x]", //
        "-1/5*a^2*A/x^5-1/3*a*(2*A*b+a*B)/x^3-b*(A*b+2*a*B)/x+b^2*B*x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:44
  public void test0153() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x, x]", //
        "5/2*a^4*A*b*x^2+5/2*a^3*A*b^2*x^4+5/3*a^2*A*b^3*x^6+5/8*a*A*b^4*x^8+1/10*A*b^5*x^10+1/12*B*(a+b*x^2)^6/b+a^5*A*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:58
  public void test0154() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^15, x]", //
        "-1/14*A*(a+b*x^2)^6/(a*x^14)+1/84*(A*b-7*a*B)*(a+b*x^2)^6/(a^2*x^12)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:144
  public void test0155() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(x*(a+b*x^2)^2), x]", //
        "c*Log[x]/a-1/2*c*Log[a+b*x^2]/a");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:226
  public void test0156() {
    check( //
        "Integrate[x^2*(c+d*x^2)/(a+b*x^2), x]", //
        "(b*c-a*d)*x/b^2+1/3*d*x^3/b-(b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(5/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:270
  public void test0157() {
    check( //
        "Integrate[x^3/((a+b*x^2)*(c+d*x^2)^2), x]", //
        "-1/2*c/(d*(b*c-a*d)*(c+d*x^2))-1/2*a*Log[a+b*x^2]/(b*c-a*d)^2+1/2*a*Log[c+d*x^2]/(b*c-a*d)^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:302
  public void test0158() {
    check( //
        "Integrate[x^4*(c+d*x^2)^2/(a+b*x^2)^2, x]", //
        "1/2*(3*b*c-7*a*d)*(b*c-a*d)*x/b^4-1/6*(3*b*c-7*a*d)*(b*c-a*d)*x^3/(a*b^3)+1/5*d^2*x^5/b^2+1/2*(b*c-a*d)^2*x^5/(a*b^2*(a+b*x^2))-1/2*(3*b*c-7*a*d)*(b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:346
  public void test0159() {
    check( //
        "Integrate[x/((a+b*x^2)^2*(c+d*x^2)^3), x]", //
        "-1/2*b^2/((b*c-a*d)^3*(a+b*x^2))-1/4*d/((b*c-a*d)^2*(c+d*x^2)^2)-b*d/((b*c-a*d)^3*(c+d*x^2))-3/2*b^2*d*Log[a+b*x^2]/(b*c-a*d)^4+3/2*b^2*d*Log[c+d*x^2]/(b*c-a*d)^4");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:422
  public void test0160() {
    check( //
        "Integrate[(A+B*x^2)/(x^(5/2)*(a+b*x^2)), x]", //
        "-2/3*A/(a*x^(3/2))+(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(7/4)*b^(1/4)*Sqrt[2])-(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(7/4)*b^(1/4)*Sqrt[2])+1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(7/4)*b^(1/4)*Sqrt[2])-1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(7/4)*b^(1/4)*Sqrt[2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:470
  public void test0161() {
    check( //
        "Integrate[x^(7/2)*(a+b*x^2)^2/(c+d*x^2), x]", //
        "2/5*(b*c-a*d)^2*x^(5/2)/d^3-2/9*b*(b*c-2*a*d)*x^(9/2)/d^2+2/13*b^2*x^(13/2)/d-c^(5/4)*(b*c-a*d)^2*ArcTan[1-d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(d^(17/4)*Sqrt[2])+c^(5/4)*(b*c-a*d)^2*ArcTan[1+d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(d^(17/4)*Sqrt[2])-1/2*c^(5/4)*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]-c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(d^(17/4)*Sqrt[2])+1/2*c^(5/4)*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]+c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(d^(17/4)*Sqrt[2])-2*c*(b*c-a*d)^2*Sqrt[x]/d^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1081
  public void test0162() {
    check( //
        "Integrate[1/(x*Sqrt[-1+x^4]), x]", //
        "1/2*ArcTan[Sqrt[-1+x^4]]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:27
  public void test0163() {
    check( //
        "Integrate[1/x^2, x]", //
        "(-1)/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:45
  public void test0164() {
    check( //
        "Integrate[1/x^(1/3), x]", //
        "3/2*x^(2/3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:192
  public void test0165() {
    check( //
        "Integrate[(c+d)*(a+b*x)/e, x]", //
        "1/2*(c+d)*(a+b*x)^2/(b*e)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:327
  public void test0166() {
    check( //
        "Integrate[b/x+1/(x^2*(1+b*x)), x]", //
        "(-1)/x+b*Log[1+b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:451
  public void test0167() {
    check( //
        "Integrate[1/(a+b*x)^(1/3), x]", //
        "3/2*(a+b*x)^(2/3)/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:465
  public void test0168() {
    check( //
        "Integrate[1/(a+b*x)^(2/3), x]", //
        "3*(a+b*x)^(1/3)/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:673
  public void test0169() {
    check( //
        "Integrate[1/((a-b*x)^(5/2)*Sqrt[x]), x]", //
        "2/3*Sqrt[x]/(a*(a-b*x)^(3/2))+4/3*Sqrt[x]/(a^2*Sqrt[a-b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:687
  public void test0170() {
    check( //
        "Integrate[1/((2+b*x)^(3/2)*Sqrt[x]), x]", //
        "Sqrt[x]/Sqrt[2+b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:847
  public void test0171() {
    check( //
        "Integrate[(a+b*x)*Sqrt[c*x^2], x]", //
        "1/2*a*x*Sqrt[c*x^2]+1/3*b*x^2*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:861
  public void test0172() {
    check( //
        "Integrate[x^2*(c*x^2)^(5/2)*(a+b*x), x]", //
        "1/8*a*c^2*x^7*Sqrt[c*x^2]+1/9*b*c^2*x^8*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:877
  public void test0173() {
    check( //
        "Integrate[(a+b*x)/(x^4*Sqrt[c*x^2]), x]", //
        "-1/4*a/(x^3*Sqrt[c*x^2])-1/3*b/(x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:891
  public void test0174() {
    check( //
        "Integrate[(a+b*x)/(x^2*(c*x^2)^(5/2)), x]", //
        "-1/6*a/(c^2*x^5*Sqrt[c*x^2])-1/5*b/(c^2*x^4*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:909
  public void test0175() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^2, x]", //
        "1/4*a^2*c*x^3*Sqrt[c*x^2]+2/5*a*b*c*x^4*Sqrt[c*x^2]+1/6*b^2*c*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:925
  public void test0176() {
    check( //
        "Integrate[x^2*(a+b*x)^2/Sqrt[c*x^2], x]", //
        "1/2*a^2*x^3/Sqrt[c*x^2]+2/3*a*b*x^4/Sqrt[c*x^2]+1/4*b^2*x^5/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:939
  public void test0177() {
    check( //
        "Integrate[(a+b*x)^2/(x^4*(c*x^2)^(3/2)), x]", //
        "-1/6*a^2/(c*x^5*Sqrt[c*x^2])-2/5*a*b/(c*x^4*Sqrt[c*x^2])-1/4*b^2/(c*x^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:957
  public void test0178() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^2*(a+b*x)), x]", //
        "Log[x]*Sqrt[c*x^2]/(a*x)-Log[a+b*x]*Sqrt[c*x^2]/(a*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1005
  public void test0179() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^3*(a+b*x)^2), x]", //
        "-Sqrt[c*x^2]/(a^2*x^2)-b*Sqrt[c*x^2]/(a^2*x*(a+b*x))-2*b*Log[x]*Sqrt[c*x^2]/(a^3*x)+2*b*Log[a+b*x]*Sqrt[c*x^2]/(a^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1021
  public void test0180() {
    check( //
        "Integrate[x/((a+b*x)^2*Sqrt[c*x^2]), x]", //
        "-x/(b*(a+b*x)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1042
  public void test0181() {
    check( //
        "Integrate[x*(c*x^2)^(3/2)*(a+b*x)^n, x]", //
        "a^4*c*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^5*(1+n)*x)-4*a^3*c*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^5*(2+n)*x)+6*a^2*c*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^5*(3+n)*x)-4*a*c*(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^5*(4+n)*x)+c*(a+b*x)^(5+n)*Sqrt[c*x^2]/(b^5*(5+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1063
  public void test0182() {
    check( //
        "Integrate[x*(a+b*x)^n/Sqrt[c*x^2], x]", //
        "x*(a+b*x)^(1+n)/(b*(1+n)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1092
  public void test0183() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)/(c*x^2)^(5/2), x]", //
        "-a*d^4*x*(d*x)^(-4+m)/(c^2*(4-m)*Sqrt[c*x^2])-b*d^3*x*(d*x)^(-3+m)/(c^2*(3-m)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1120
  public void test0184() {
    check( //
        "Integrate[(c*x^2)^p*(a+b*x)^(2-2*p)/x^4, x]", //
        "-(c*x^2)^p*(a+b*x)^(3-2*p)/(a*(3-2*p)*x^3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1146
  public void test0185() {
    check( //
        "Integrate[(b*c/d+b*x)^2/(c+d*x)^3, x]", //
        "b^2*Log[c+d*x]/d^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1322
  public void test0186() {
    check( //
        "Integrate[1/((3-x)^(5/2)*(-2+x)^(5/2)), x]", //
        "2/3/((3-x)^(3/2)*(-2+x)^(3/2))+4/((-2+x)^(3/2)*Sqrt[3-x])-16/3*Sqrt[3-x]/(-2+x)^(3/2)-32/3*Sqrt[3-x]/Sqrt[-2+x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1467
  public void test0187() {
    check( //
        "Integrate[(c+d*x)^7/(a+b*x)^13, x]", //
        "-1/12*(c+d*x)^8/((b*c-a*d)*(a+b*x)^12)+1/33*d*(c+d*x)^8/((b*c-a*d)^2*(a+b*x)^11)-1/110*d^2*(c+d*x)^8/((b*c-a*d)^3*(a+b*x)^10)+1/495*d^3*(c+d*x)^8/((b*c-a*d)^4*(a+b*x)^9)-1/3960*d^4*(c+d*x)^8/((b*c-a*d)^5*(a+b*x)^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2177
  public void test0188() {
    check( //
        "Integrate[-x^3+x^4, x]", //
        "-1/4*x^4+1/5*x^5");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2193
  public void test0189() {
    check( //
        "Integrate[(-1/7)/x^6+x^6, x]", //
        "1/35/x^5+1/7*x^7");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2075
  public void test0190() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)*Sqrt[1-2*x], x]", //
        "-26411/96*(1-2*x)^(3/2)+57281/160*(1-2*x)^(5/2)-3549/16*(1-2*x)^(7/2)+1197/16*(1-2*x)^(9/2)-4671/352*(1-2*x)^(11/2)+405/416*(1-2*x)^(13/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2089
  public void test0191() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)^2*Sqrt[1-2*x], x]", //
        "-847/24*(1-2*x)^(3/2)+1133/40*(1-2*x)^(5/2)-505/56*(1-2*x)^(7/2)+25/24*(1-2*x)^(9/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2103
  public void test0192() {
    check( //
        "Integrate[(3+5*x)^3*Sqrt[1-2*x]/(2+3*x), x]", //
        "-5135/324*(1-2*x)^(3/2)+80/9*(1-2*x)^(5/2)-125/84*(1-2*x)^(7/2)+2/81*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-2/81*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2255
  public void test0193() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^3/(2+3*x), x]", //
        "-14/729*(1-2*x)^(3/2)-2/405*(1-2*x)^(5/2)-5135/756*(1-2*x)^(7/2)+400/81*(1-2*x)^(9/2)-125/132*(1-2*x)^(11/2)+98/729*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-98/729*Sqrt[1-2*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:28
  public void test0194() {
    check( //
        "Integrate[1/x^3, x]", //
        "(-1/2)/x^2");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:46
  public void test0195() {
    check( //
        "Integrate[1/x^(2/3), x]", //
        "3*x^(1/3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:266
  public void test0196() {
    check( //
        "Integrate[x^6/(a+b*x)^10, x]", //
        "1/9*x^7/(a*(a+b*x)^9)+1/36*x^7/(a^2*(a+b*x)^8)+1/252*x^7/(a^3*(a+b*x)^7)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:418
  public void test0197() {
    check( //
        "Integrate[1/2*x^(-1+m)*(2*a*m+b*(-1+2*m)*x)/(a+b*x)^(3/2), x]", //
        "x^m/Sqrt[a+b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:514
  public void test0198() {
    check( //
        "Integrate[1/((a+b*x)*Sqrt[x]), x]", //
        "2*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/(Sqrt[a]*Sqrt[b])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:674
  public void test0199() {
    check( //
        "Integrate[1/(x^(3/2)*(a-b*x)^(5/2)), x]", //
        "2/3/(a*(a-b*x)^(3/2)*Sqrt[x])+8/3/(a^2*Sqrt[x]*Sqrt[a-b*x])-16/3*Sqrt[a-b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:716
  public void test0200() {
    check( //
        "Integrate[1/(Sqrt[1-x]*Sqrt[x]), x]", //
        "-ArcSin[1-2*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:862
  public void test0201() {
    check( //
        "Integrate[x*(c*x^2)^(5/2)*(a+b*x), x]", //
        "1/7*a*c^2*x^6*Sqrt[c*x^2]+1/8*b*c^2*x^7*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:892
  public void test0202() {
    check( //
        "Integrate[(a+b*x)/(x^3*(c*x^2)^(5/2)), x]", //
        "-1/7*a/(c^2*x^6*Sqrt[c*x^2])-1/6*b/(c^2*x^5*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:910
  public void test0203() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^2/x, x]", //
        "1/3*a^2*c*x^2*Sqrt[c*x^2]+1/2*a*b*c*x^3*Sqrt[c*x^2]+1/5*b^2*c*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:926
  public void test0204() {
    check( //
        "Integrate[x*(a+b*x)^2/Sqrt[c*x^2], x]", //
        "1/3*x*(a+b*x)^3/(b*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:940
  public void test0205() {
    check( //
        "Integrate[x^3*(a+b*x)^2/(c*x^2)^(5/2), x]", //
        "-a^2/(c^2*Sqrt[c*x^2])+b^2*x^2/(c^2*Sqrt[c*x^2])+2*a*b*x*Log[x]/(c^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:958
  public void test0206() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^3*(a+b*x)), x]", //
        "-Sqrt[c*x^2]/(a*x^2)-b*Log[x]*Sqrt[c*x^2]/(a^2*x)+b*Log[a+b*x]*Sqrt[c*x^2]/(a^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1006
  public void test0207() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^4*(a+b*x)^2), x]", //
        "-1/2*Sqrt[c*x^2]/(a^2*x^3)+2*b*Sqrt[c*x^2]/(a^3*x^2)+b^2*Sqrt[c*x^2]/(a^3*x*(a+b*x))+3*b^2*Log[x]*Sqrt[c*x^2]/(a^4*x)-3*b^2*Log[a+b*x]*Sqrt[c*x^2]/(a^4*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1022
  public void test0208() {
    check( //
        "Integrate[1/((a+b*x)^2*Sqrt[c*x^2]), x]", //
        "x/(a*(a+b*x)*Sqrt[c*x^2])+x*Log[x]/(a^2*Sqrt[c*x^2])-x*Log[a+b*x]/(a^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1043
  public void test0209() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^n, x]", //
        "-a^3*c*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^4*(1+n)*x)+3*a^2*c*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^4*(2+n)*x)-3*a*c*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^4*(3+n)*x)+c*(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^4*(4+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1093
  public void test0210() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(5/2)*(a+b*x)^2, x]", //
        "a^2*c^2*(d*x)^(6+m)*Sqrt[c*x^2]/(d^6*(6+m)*x)+2*a*b*c^2*(d*x)^(7+m)*Sqrt[c*x^2]/(d^7*(7+m)*x)+b^2*c^2*(d*x)^(8+m)*Sqrt[c*x^2]/(d^8*(8+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1147
  public void test0211() {
    check( //
        "Integrate[(b*c/d+b*x)/(c+d*x)^3, x]", //
        "-b/(d^2*(c+d*x))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1181
  public void test0212() {
    check( //
        "Integrate[(a+b*x)^2*(a*c-b*c*x)^2, x]", //
        "a^4*c^2*x-2/3*a^2*b^2*c^2*x^3+1/5*b^4*c^2*x^5");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1293
  public void test0213() {
    check( //
        "Integrate[(a+a*x)^(5/2)*(c-c*x)^(5/2), x]", //
        "5/24*a*c*x*(a+a*x)^(3/2)*(c-c*x)^(3/2)+1/6*x*(a+a*x)^(5/2)*(c-c*x)^(5/2)+5/8*a^(5/2)*c^(5/2)*ArcTan[Sqrt[c]*Sqrt[a+a*x]/(Sqrt[a]*Sqrt[c-c*x])]+5/16*a^2*c^2*x*Sqrt[a+a*x]*Sqrt[c-c*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1307
  public void test0214() {
    check( //
        "Integrate[1/((a+b*x)^(7/2)*(a*c-b*c*x)^(7/2)), x]", //
        "1/5*x/(a^2*c*(a+b*x)^(5/2)*(a*c-b*c*x)^(5/2))+4/15*x/(a^4*c^2*(a+b*x)^(3/2)*(a*c-b*c*x)^(3/2))+8/15*x/(a^6*c^3*Sqrt[a+b*x]*Sqrt[a*c-b*c*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1385
  public void test0215() {
    check( //
        "Integrate[1/((a-I*a*x)^(9/4)*(a+I*a*x)^(9/4)), x]", //
        "2/5*x/(a^4*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4)*(1+x^2))+6/5*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a^4*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1609
  public void test0216() {
    check( //
        "Integrate[(c+d*x)^(1/2), x]", //
        "2/3*(c+d*x)^(3/2)/d");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1745
  public void test0217() {
    check( //
        "Integrate[1/((a+b*x)^(1/2)*(c+d*x)^(3/2)), x]", //
        "2*Sqrt[a+b*x]/((b*c-a*d)*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1759
  public void test0218() {
    check( //
        "Integrate[1/((a+b*x)^(7/2)*(c+d*x)^(5/2)), x]", //
        "(-2/5)/((b*c-a*d)*(a+b*x)^(5/2)*(c+d*x)^(3/2))+16/15*d/((b*c-a*d)^2*(a+b*x)^(3/2)*(c+d*x)^(3/2))-32/5*d^2/((b*c-a*d)^3*(c+d*x)^(3/2)*Sqrt[a+b*x])-128/15*d^3*Sqrt[a+b*x]/((b*c-a*d)^4*(c+d*x)^(3/2))-256/15*b*d^3*Sqrt[a+b*x]/((b*c-a*d)^5*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2178
  public void test0219() {
    check( //
        "Integrate[-1+x^5, x]", //
        "-x+1/6*x^6");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2194
  public void test0220() {
    check( //
        "Integrate[1+1/x+x, x]", //
        "x+1/2*x^2+Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:31
  public void test0221() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x, x]", //
        "-3*a^4*b*c^4*x+a^3*b^2*c^4*x^2+2/3*a^2*b^3*c^4*x^3-3/4*a*b^4*c^4*x^4+1/5*b^5*c^4*x^5+a^5*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:45
  public void test0222() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x, x]", //
        "-4*a^5*b*c^5*x+5/2*a^4*b^2*c^5*x^2-5/4*a^2*b^4*c^5*x^4+4/5*a*b^5*c^5*x^5-1/6*b^6*c^5*x^6+a^6*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:59
  public void test0223() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^6/x^10, x]", //
        "-1/9*c^6*(a-b*x)^7/x^9-11/72*b*c^6*(a-b*x)^7/(a*x^8)-11/504*b^2*c^6*(a-b*x)^7/(a^2*x^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:117
  public void test0224() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^4, x]", //
        "-1/3*a*A/x^3+1/2*(-A*b-a*B)/x^2-b*B/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:131
  public void test0225() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^7, x]", //
        "-1/6*a^2*A/x^6-1/5*a*(2*A*b+a*B)/x^5-1/4*b*(A*b+2*a*B)/x^4-1/3*b^2*B/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:145
  public void test0226() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^8, x]", //
        "-1/7*a^3*A/x^7-1/6*a^2*(3*A*b+a*B)/x^6-3/5*a*b*(A*b+a*B)/x^5-1/4*b^2*(A*b+3*a*B)/x^4-1/3*b^3*B/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:159
  public void test0227() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^6, x]", //
        "-1/5*a^5*A/x^5-1/4*a^4*(5*A*b+a*B)/x^4-5/3*a^3*b*(2*A*b+a*B)/x^3-5*a^2*b^2*(A*b+a*B)/x^2-5*a*b^3*(A*b+2*a*B)/x+b^5*B*x+b^4*(A*b+5*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:187
  public void test0228() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^11, x]", //
        "-1/10*a^10*A/x^10-1/9*a^9*(10*A*b+a*B)/x^9-5/8*a^8*b*(9*A*b+2*a*B)/x^8-15/7*a^7*b^2*(8*A*b+3*a*B)/x^7-5*a^6*b^3*(7*A*b+4*a*B)/x^6-42/5*a^5*b^4*(6*A*b+5*a*B)/x^5-21/2*a^4*b^5*(5*A*b+6*a*B)/x^4-10*a^3*b^6*(4*A*b+7*a*B)/x^3-15/2*a^2*b^7*(3*A*b+8*a*B)/x^2-5*a*b^8*(2*A*b+9*a*B)/x+b^10*B*x+b^9*(A*b+10*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:431
  public void test0229() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^(7/2), x]", //
        "-2/5*a*A/x^(5/2)-2/3*(A*b+a*B)/x^(3/2)-2*b*B/Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:445
  public void test0230() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^(3/2), x]", //
        "2*a*b*(A*b+a*B)*x^(3/2)+2/5*b^2*(A*b+3*a*B)*x^(5/2)+2/7*b^3*B*x^(7/2)-2*a^3*A/Sqrt[x]+2*a^2*(3*A*b+a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:595
  public void test0231() {
    check( //
        "Integrate[1/(x^(1/3)*(8*c-d*x)*Sqrt[c+d*x]), x]", //
        "1/6*ArcTanh[1/3*(c^(1/3)+d^(1/3)*x^(1/3))^2/(c^(1/6)*Sqrt[c+d*x])]/(c^(5/6)*d^(2/3))-1/6*ArcTanh[1/3*Sqrt[c+d*x]/Sqrt[c]]/(c^(5/6)*d^(2/3))-1/2*ArcTan[c^(1/6)*(c^(1/3)+d^(1/3)*x^(1/3))*Sqrt[3]/Sqrt[c+d*x]]/(c^(5/6)*d^(2/3)*Sqrt[3])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:664
  public void test0232() {
    check( //
        "Integrate[(A+B*x)/(x^(7/2)*Sqrt[a+b*x]), x]", //
        "-2/5*A*Sqrt[a+b*x]/(a*x^(5/2))+2/15*(4*A*b-5*a*B)*Sqrt[a+b*x]/(a^2*x^(3/2))-4/15*b*(4*A*b-5*a*B)*Sqrt[a+b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:678
  public void test0233() {
    check( //
        "Integrate[(A+B*x)/(x^(11/2)*(a+b*x)^(3/2)), x]", //
        "-2/9*A/(a*x^(9/2)*Sqrt[a+b*x])-2/9*(10*A*b-9*a*B)/(a^2*x^(7/2)*Sqrt[a+b*x])+16/63*(10*A*b-9*a*B)*Sqrt[a+b*x]/(a^3*x^(7/2))-32/105*b*(10*A*b-9*a*B)*Sqrt[a+b*x]/(a^4*x^(5/2))+128/315*b^2*(10*A*b-9*a*B)*Sqrt[a+b*x]/(a^5*x^(3/2))-256/315*b^3*(10*A*b-9*a*B)*Sqrt[a+b*x]/(a^6*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:742
  public void test0234() {
    check( //
        "Integrate[x^3*Sqrt[a+b*x]/(c+d*x)^(5/2), x]", //
        "1/4*(35*b^2*c^2-10*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*d^(9/2))-2/3*x^3*Sqrt[a+b*x]/(d*(c+d*x)^(3/2))-2/3*(7*b*c-6*a*d)*x^2*Sqrt[a+b*x]/(d^2*(b*c-a*d)*Sqrt[c+d*x])-1/12*(105*b^2*c^2-100*a*b*c*d+3*a^2*d^2-2*b*d*(35*b*c-31*a*d)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b*d^4*(b*c-a*d))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:760
  public void test0235() {
    check( //
        "Integrate[(a+b*x)^(3/2)*Sqrt[c+d*x]/x^5, x]", //
        "1/24*(3*b*c+5*a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(a*c^2*x^3)-1/4*(a+b*x)^(5/2)*(c+d*x)^(3/2)/(a*c*x^4)-1/64*(b*c-a*d)^3*(3*b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(7/2))+1/32*(b*c-a*d)*(3*b*c+5*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^3*x^2)+1/64*(b*c-a*d)^2*(3*b*c+5*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:774
  public void test0236() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x, x]", //
        "1/4*(a+b*x)^(3/2)*(c+d*x)^(5/2)-2*a^(3/2)*c^(5/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]-1/64*(5*b^4*c^4-60*a*b^3*c^3*d-90*a^2*b^2*c^2*d^2+20*a^3*b*c*d^3-3*a^4*d^4)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(3/2))+1/96*(50*a*c-5*b*c^2/d+3*a^2*d/b)*(c+d*x)^(3/2)*Sqrt[a+b*x]+1/24*(5*b*c+3*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/d-1/64*(5*b^3*c^3-55*a*b^2*c^2*d-17*a^2*b*c*d^2+3*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^2*d)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:790
  public void test0237() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^5*Sqrt[c+d*x]), x]", //
        "-1/64*(b*c-a*d)^2*(3*b^2*c^2+10*a*b*c*d+35*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(9/2))-1/4*a*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x^4)-1/24*(9*b*c-7*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c^2*x^3)-1/96*(3*b^2*c^2-46*a*b*c*d+35*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^3*x^2)+1/192*(9*b^3*c^3+15*a*b^2*c^2*d-145*a^2*b*c*d^2+105*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^4*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:822
  public void test0238() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^3, x]", //
        "-1/4*(5*b*c+3*a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(c*x)-1/2*(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^2-3/4*(5*b^2*c^2+10*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/Sqrt[c]+3/4*(b^2*c^2+10*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]/Sqrt[d]+3/4*b*(3*b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/c+3/4*(b^2*c^2+6*a*b*c*d+a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/c");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:852
  public void test0239() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^5*(c+d*x)^(3/2)), x]", //
        "5/64*(b*c-a*d)^2*(b^2*c^2+14*a*b*c*d-63*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(11/2))-1/4*a*(a+b*x)^(3/2)/(c*x^4*Sqrt[c+d*x])-1/192*d*(15*b^3*c^3-839*a*b^2*c^2*d+1785*a^2*b*c*d^2-945*a^3*d^3)*Sqrt[a+b*x]/(a*c^5*Sqrt[c+d*x])-1/24*a*(11*b*c-9*a*d)*Sqrt[a+b*x]/(c^2*x^3*Sqrt[c+d*x])-1/96*(59*b*c-63*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^3*x^2*Sqrt[c+d*x])-1/192*(b*c-a*d)*(15*b^2*c^2-322*a*b*c*d+315*a^2*d^2)*Sqrt[a+b*x]/(a*c^4*x*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:870
  public void test0240() {
    check( //
        "Integrate[Sqrt[c+d*x]/(x^2*Sqrt[a+b*x]), x]", //
        "(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*Sqrt[c])-Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:884
  public void test0241() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x*Sqrt[a+b*x]), x]", //
        "-2*c^(5/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[a]+1/4*(15*b^2*c^2-10*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]/b^(5/2)+1/2*d*(c+d*x)^(3/2)*Sqrt[a+b*x]/b+1/4*d*(7*b*c-3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/b^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:914
  public void test0242() {
    check( //
        "Integrate[1/(x*(c+d*x)^(3/2)*Sqrt[a+b*x]), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(3/2)*Sqrt[a])-2*d*Sqrt[a+b*x]/(c*(b*c-a*d)*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:946
  public void test0243() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^3*(a+b*x)^(3/2)), x]", //
        "-15/4*(b*c-a*d)^2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/a^(7/2)+5/4*(b*c-a*d)*(c+d*x)^(3/2)/(a^2*x*Sqrt[a+b*x])-1/2*(c+d*x)^(5/2)/(a*x^2*Sqrt[a+b*x])+15/4*(b*c-a*d)^2*Sqrt[c+d*x]/(a^3*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:962
  public void test0244() {
    check( //
        "Integrate[x^2/((a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "-2*a^2/(b^2*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])-2/3*(b^2*c^2+3*a^2*d^2)*Sqrt[a+b*x]/(b^2*d*(b*c-a*d)^2*(c+d*x)^(3/2))+2/3*(b^2*c^2-6*a*b*c*d-3*a^2*d^2)*Sqrt[a+b*x]/(b*d*(b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:980
  public void test0245() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^4*(a+b*x)^(5/2)), x]", //
        "-1/3*c*(c+d*x)^(3/2)/(a*x^3*(a+b*x)^(3/2))+5/8*(b*c-a*d)*(21*b^2*c^2-14*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(11/2)*Sqrt[c])-7/24*b*(15*b*c-7*a*d)*(b*c-a*d)*Sqrt[c+d*x]/(a^4*(a+b*x)^(3/2))+3/4*c*(b*c-a*d)*Sqrt[c+d*x]/(a^2*x^2*(a+b*x)^(3/2))-1/8*(21*b*c-11*a*d)*(b*c-a*d)*Sqrt[c+d*x]/(a^3*x*(a+b*x)^(3/2))-1/24*b*(315*b^2*c^2-420*a*b*c*d+113*a^2*d^2)*Sqrt[c+d*x]/(a^5*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1000
  public void test0246() {
    check( //
        "Integrate[x*Sqrt[a+b*x]/Sqrt[-a-b*x], x]", //
        "1/2*x^2*Sqrt[a+b*x]/Sqrt[-a-b*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1029
  public void test0247() {
    check( //
        "Integrate[Sqrt[-1+x]*Sqrt[1+x]/x, x]", //
        "-ArcTan[Sqrt[-1+x]*Sqrt[1+x]]+Sqrt[-1+x]*Sqrt[1+x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1118
  public void test0248() {
    check( //
        "Integrate[1/((1-x)^(1/4)*(e*x)^(3/2)*(1+x)^(1/4)), x]", //
        "-2*(1+(-1)/x^2)^(1/4)*EllipticE[1/2*ArcCsc[x],2]*Sqrt[e*x]/(e^2*(1-x^2)^(1/4))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1279
  public void test0249() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^10, x]", //
        "-1/9*(b*d-a*e)^3*(B*d-A*e)/(e^5*(d+e*x)^9)+1/8*(b*d-a*e)^2*(4*b*B*d-3*A*b*e-a*B*e)/(e^5*(d+e*x)^8)-3/7*b*(b*d-a*e)*(2*b*B*d-A*b*e-a*B*e)/(e^5*(d+e*x)^7)+1/6*b^2*(4*b*B*d-A*b*e-3*a*B*e)/(e^5*(d+e*x)^6)-1/5*b^3*B/(e^5*(d+e*x)^5)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1321
  public void test0250() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^4, x]", //
        "-30*b^3*(b*d-a*e)^6*(11*b*B*d-7*A*b*e-4*a*B*e)*x/e^11+1/3*(b*d-a*e)^10*(B*d-A*e)/(e^12*(d+e*x)^3)-1/2*(b*d-a*e)^9*(11*b*B*d-10*A*b*e-a*B*e)/(e^12*(d+e*x)^2)+5*b*(b*d-a*e)^8*(11*b*B*d-9*A*b*e-2*a*B*e)/(e^12*(d+e*x))+21*b^4*(b*d-a*e)^5*(11*b*B*d-6*A*b*e-5*a*B*e)*(d+e*x)^2/e^12-14*b^5*(b*d-a*e)^4*(11*b*B*d-5*A*b*e-6*a*B*e)*(d+e*x)^3/e^12+15/2*b^6*(b*d-a*e)^3*(11*b*B*d-4*A*b*e-7*a*B*e)*(d+e*x)^4/e^12-3*b^7*(b*d-a*e)^2*(11*b*B*d-3*A*b*e-8*a*B*e)*(d+e*x)^5/e^12+5/6*b^8*(b*d-a*e)*(11*b*B*d-2*A*b*e-9*a*B*e)*(d+e*x)^6/e^12-1/7*b^9*(11*b*B*d-A*b*e-10*a*B*e)*(d+e*x)^7/e^12+1/8*b^10*B*(d+e*x)^8/e^12+15*b^2*(b*d-a*e)^7*(11*b*B*d-8*A*b*e-3*a*B*e)*Log[d+e*x]/e^12");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1335
  public void test0251() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^18, x]", //
        "-1/17*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^17)+1/272*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^16)+1/816*b*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^3*(d+e*x)^15)+1/2856*b^2*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^4*(d+e*x)^14)+1/12376*b^3*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^5*(d+e*x)^13)+1/74256*b^4*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^6*(d+e*x)^12)+1/816816*b^5*(11*b*B*d+6*A*b*e-17*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^7*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1365
  public void test0252() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^4/(a+b*x)^3, x]", //
        "2*e^2*(b*d-a*e)*(3*b*B*d+2*A*b*e-5*a*B*e)*x/b^5-1/2*(A*b-a*B)*(b*d-a*e)^4/(b^6*(a+b*x)^2)-(b*d-a*e)^3*(b*B*d+4*A*b*e-5*a*B*e)/(b^6*(a+b*x))+1/2*e^3*(4*b*B*d+A*b*e-5*a*B*e)*(a+b*x)^2/b^6+1/3*B*e^4*(a+b*x)^3/b^6+2*e*(b*d-a*e)^2*(2*b*B*d+3*A*b*e-5*a*B*e)*Log[a+b*x]/b^6");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2076
  public void test0253() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)*Sqrt[1-2*x], x]", //
        "-3773/48*(1-2*x)^(3/2)+3283/40*(1-2*x)^(5/2)-153/4*(1-2*x)^(7/2)+69/8*(1-2*x)^(9/2)-135/176*(1-2*x)^(11/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2090
  public void test0254() {
    check( //
        "Integrate[(3+5*x)^2*Sqrt[1-2*x], x]", //
        "-121/12*(1-2*x)^(3/2)+11/2*(1-2*x)^(5/2)-25/28*(1-2*x)^(7/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2242
  public void test0255() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^2/(2+3*x), x]", //
        "14/243*(1-2*x)^(3/2)+2/135*(1-2*x)^(5/2)-155/126*(1-2*x)^(7/2)+25/54*(1-2*x)^(9/2)-98/243*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]+98/243*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2564
  public void test0256() {
    check( //
        "Integrate[(A+B*x)/((d+e*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "-2/3*(B*d-A*e)*Sqrt[a+b*x]/(e*(b*d-a*e)*(d+e*x)^(3/2))+2/3*(b*B*d+2*A*b*e-3*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^2*Sqrt[d+e*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2596
  public void test0257() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3, x]", //
        "-121/28*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/2*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-11/28*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2610
  public void test0258() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5, x]", //
        "3/28*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-153065/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1265/4704*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+115/168*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-13915/21952*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2624
  public void test0259() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^7, x]", //
        "-15036307/1229312*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-59/1260*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5-1/18*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^6-6533/211680*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+47279/1270080*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+1057139/7112448*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+106751933/99574272*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2654
  public void test0260() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(5/2)), x]", //
        "-126513/28*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-6095/84*Sqrt[1-2*x]/(3+5*x)^(3/2)+1/2*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+243/28*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+608185/924*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2686
  public void test0261() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x), x]", //
        "1/12*(1-2*x)^(3/2)*(3+5*x)^(5/2)+14/243*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+648919/62208*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-53/192*(3+5*x)^(3/2)*Sqrt[1-2*x]+23/216*(3+5*x)^(5/2)*Sqrt[1-2*x]-15863/20736*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2702
  public void test0262() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^3*Sqrt[3+5*x]), x]", //
        "-363/4*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/2*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+33/4*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2734
  public void test0263() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^4, x]", //
        "-32765/648*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-8/81*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-1/9*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^3+5/12*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+925/216*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2748
  public void test0264() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^7, x]", //
        "1/14*(1-2*x)^(7/2)*(3+5*x)^(5/2)/(2+3*x)^6+17/28*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^5+935/224*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-41068005/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-113135/12544*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+10285/448*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-3733455/175616*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2762
  public void test0265() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^9, x]", //
        "-1/24*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^8+185/1008*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^7-106656830005/275365888*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-720833/508032*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5+47365/36288*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^6-75045071/85349376*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+372439373/512096256*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+64983635965/14338695168*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+6796051494355/200741732352*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2810
  public void test0266() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x]), x]", //
        "-451/196*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/14*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-41/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2854
  public void test0267() {
    check( //
        "Integrate[(2+3*x)^5/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "462357/40000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/165*(2+3*x)^4*Sqrt[1-2*x]/(3+5*x)^(3/2)-734/9075*(2+3*x)^3*Sqrt[1-2*x]/Sqrt[3+5*x]+511/30250*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-7/4840000*(938509+366420*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2886
  public void test0268() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^2), x]", //
        "33/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*(3+5*x)^(3/2)/((2+3*x)*Sqrt[1-2*x])+3/49*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2900
  public void test0269() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^6), x]", //
        "-3474273/2151296*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/7*(3+5*x)^(3/2)/((2+3*x)^5*Sqrt[1-2*x])+164/735*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5-42863/41160*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-29297/82320*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3-55277/460992*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+426781/6453888*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2916
  public void test0270() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "-999/100*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^2/(Sqrt[1-2*x]*Sqrt[3+5*x])+1/12100*(30443+50985*x)*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2930
  public void test0271() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)*(3+5*x)^(5/2)), x]", //
        "-54/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77/((3+5*x)^(3/2)*Sqrt[1-2*x])-410/2541*Sqrt[1-2*x]/(3+5*x)^(3/2)+31030/27951*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2962
  public void test0272() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^3), x]", //
        "4/231*(3+5*x)^(7/2)/((1-2*x)^(3/2)*(2+3*x)^2)+715/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+26/231*(3+5*x)^(5/2)/((2+3*x)^2*Sqrt[1-2*x])+65/3234*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+65/1372*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2978
  public void test0273() {
    check( //
        "Integrate[(2+3*x)^4/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "4887/200*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/33*(2+3*x)^3/((1-2*x)^(3/2)*Sqrt[3+5*x])-1099/726*(2+3*x)^2/(Sqrt[1-2*x]*Sqrt[3+5*x])-1/798600*(4898747+8200665*x)*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2992
  public void test0274() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "2/33/((1-2*x)^(3/2)*(3+5*x)^(3/2))+20/121/((3+5*x)^(3/2)*Sqrt[1-2*x])-400/3993*Sqrt[1-2*x]/(3+5*x)^(3/2)-1600/43923*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3014
  public void test0275() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x], x]", //
        "-1159/1125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-31/1125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/25*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-31/225*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3028
  public void test0276() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(11/2), x]", //
        "-22738708/6806835*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-673072/6806835*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/27*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-214/3969*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+8842/138915*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+332372/972405*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+22738708/6806835*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3044
  public void test0277() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x], x]", //
        "-61151/43750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-314/21875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-23/875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+2/35*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-859/4375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3058
  public void test0278() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(7/2)*(3+5*x)^(3/2)), x]", //
        "116464/245*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+38536/245*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/5*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+416/105*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+19268/245*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-116464/147*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3076
  public void test0279() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(3/2), x]", //
        "494/135*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-214/135*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/Sqrt[2+3*x]-16/27*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3090
  public void test0280() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(13/2), x]", //
        "-2/33*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(11/2)-1446357824/6806835*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-43537016/6806835*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+74/297*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-12872/43659*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+442076/1528065*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+20799916/10696455*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+1446357824/74875185*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3106
  public void test0281() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[2+3*x]/Sqrt[3+5*x], x]", //
        "-2797/5625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-598/5625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/25*(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]+194/1125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3120
  public void test0282() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(9/2)*(3+5*x)^(3/2)), x]", //
        "10312712/1715*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+310208/1715*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*Sqrt[1-2*x]/((2+3*x)^(7/2)*Sqrt[3+5*x])+176/35*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+12276/245*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+1706144/1715*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-10312712/1029*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3138
  public void test0283() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(5/2), x]", //
        "-4418/405*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+988/405*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/9*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(3/2)+10/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/Sqrt[2+3*x]+196/81*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3152
  public void test0284() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(13/2), x]", //
        "-2/33*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(11/2)+230/891*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(9/2)-780320008/1750329*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-23441272/1750329*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+12280/6237*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)-325796/130977*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+11243972/2750517*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+780320008/19253619*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3168
  public void test0285() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)/Sqrt[3+5*x], x]", //
        "-8024546/8859375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-509189/8859375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+106/1575*(1-2*x)^(3/2)*(2+3*x)^(3/2)*Sqrt[3+5*x]+2/45*(1-2*x)^(5/2)*(2+3*x)^(3/2)*Sqrt[3+5*x]+8878/118125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+21547/1771875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3182
  public void test0286() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "4636/75*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+124/75*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/3*(1-2*x)^(3/2)/(Sqrt[2+3*x]*Sqrt[3+5*x])-1496/15*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3200
  public void test0287() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[3+5*x]/Sqrt[1-2*x], x]", //
        "-5057/8750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-56041/8750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-104/175*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-1/7*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-4839/1750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3214
  public void test0288() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^(9/2)*Sqrt[1-2*x]), x]", //
        "-184636/252105*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-9124/252105*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/147*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-536/5145*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+974/36015*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+184636/252105*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3246
  public void test0289() {
    check( //
        "Integrate[(2+3*x)^(7/2)/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-270248/21875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-178879/43750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-333/875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-3/35*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-15553/8750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3260
  public void test0290() {
    check( //
        "Integrate[1/((2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "6388/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+192/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+2/7*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+288/49*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-31940/539*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3298
  public void test0291() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(3/2)/(1-2*x)^(3/2), x]", //
        "4621/50*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+139/50*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(2+3*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[1-2*x]+9/5*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+139/10*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3312
  public void test0292() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^(7/2)), x]", //
        "81164/108045*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-28174/108045*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*(3+5*x)^(3/2)/((2+3*x)^(5/2)*Sqrt[1-2*x])+163/735*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-15601/15435*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-81164/108045*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3328
  public void test0293() {
    check( //
        "Integrate[(2+3*x)^(3/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "37/55*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-2/55*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])-37/121*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3342
  public void test0294() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "-42623864/41503*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1282376/41503*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/77/((2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x])+54/539*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+14496/3773*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-3205940/124509*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+213119320/1369599*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3360
  public void test0295() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[2+3*x]/(1-2*x)^(5/2), x]", //
        "-139/14*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-23/7*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*(3+5*x)^(3/2)*Sqrt[2+3*x]/(1-2*x)^(3/2)-23/7*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3376
  public void test0296() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-44109377/27500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-663409/13750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(7/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-910/363*(2+3*x)^(5/2)*Sqrt[3+5*x]/Sqrt[1-2*x]-27271/6050*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-317384/15125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3390
  public void test0297() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "494/847*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-214/847*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/33*Sqrt[2+3*x]/((1-2*x)^(3/2)*Sqrt[3+5*x])+214/2541*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])-2470/27951*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3404
  public void test0298() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "4/231/((1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2))-1446357824/3195731*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-43537016/3195731*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+544/5929/((2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x])+414/41503*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+488436/290521*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-108842540/9587193*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+7231789120/105459123*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3426
  public void test0299() {
    check( //
        "Integrate[(a+b*x)^(1/3)/((c+d*x)^(1/3)*(e+f*x)^3), x]", //
        "-1/2*f*(a+b*x)^(4/3)*(c+d*x)^(2/3)/((b*e-a*f)*(d*e-c*f)*(e+f*x)^2)+1/3*(3*b*d*e-b*c*f-2*a*d*f)*(a+b*x)^(1/3)*(c+d*x)^(2/3)/((b*e-a*f)*(d*e-c*f)^2*(e+f*x))-1/18*(b*c-a*d)*(3*b*d*e-b*c*f-2*a*d*f)*Log[e+f*x]/((b*e-a*f)^(5/3)*(d*e-c*f)^(7/3))+1/6*(b*c-a*d)*(3*b*d*e-b*c*f-2*a*d*f)*Log[-(a+b*x)^(1/3)+(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/(d*e-c*f)^(1/3)]/((b*e-a*f)^(5/3)*(d*e-c*f)^(7/3))+1/3*(b*c-a*d)*(3*b*d*e-b*c*f-2*a*d*f)*ArcTan[1/Sqrt[3]+2*(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/((d*e-c*f)^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]/((b*e-a*f)^(5/3)*(d*e-c*f)^(7/3)*Sqrt[3])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3487
  public void test0300() {
    check( //
        "Integrate[(a+b*x)*(c+d*x)^(-3+n)/(e+f*x)^n, x]", //
        "(b*c-a*d)*(c+d*x)^(-2+n)*(e+f*x)^(1-n)/(d*(d*e-c*f)*(2-n))+(a*d*f+b*(c*f*(1-n)-d*e*(2-n)))*(c+d*x)^(-1+n)*(e+f*x)^(1-n)/(d*(d*e-c*f)^2*(1-n)*(2-n))");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:48
  public void test0301() {
    check( //
        "Integrate[(-1+2*a*x)/(x^2*Sqrt[-1+x]*Sqrt[1+x]), x]", //
        "2*a*ArcTan[Sqrt[-1+x]*Sqrt[1+x]]-Sqrt[-1+x]*Sqrt[1+x]/x");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:82
  public void test0302() {
    check( //
        "Integrate[(7+5*x)*Sqrt[2-3*x]/(Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "-179/12*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+241/36*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+5/12*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:143
  public void test0303() {
    check( //
        "Integrate[Sqrt[2-3*x]/((7+5*x)^(5/2)*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "-10/2691*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)-98330/74828637*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]+39332/74828637*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]+716/61893*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[11/23]*Sqrt[7+5*x]/(Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])-19666/1918683*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:50
  public void test0304() {
    check( //
        "Integrate[(a+b*x^2)^3/x^11, x]", //
        "-1/10*(a+b*x^2)^4/(a*x^10)+1/40*b*(a+b*x^2)^4/(a^2*x^8)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:64
  public void test0305() {
    check( //
        "Integrate[x^11*(a+b*x^2)^5, x]", //
        "1/12*a^5*x^12+5/14*a^4*b*x^14+5/8*a^3*b^2*x^16+5/9*a^2*b^3*x^18+1/4*a*b^4*x^20+1/22*b^5*x^22");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:78
  public void test0306() {
    check( //
        "Integrate[(a+b*x^2)^5/x^17, x]", //
        "-1/16*(a+b*x^2)^6/(a*x^16)+1/56*b*(a+b*x^2)^6/(a^2*x^14)-1/336*b^2*(a+b*x^2)^6/(a^3*x^12)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:106
  public void test0307() {
    check( //
        "Integrate[(a+b*x^2)^8/x^7, x]", //
        "-1/6*a^8/x^6-2*a^7*b/x^4-14*a^6*b^2/x^2+35*a^4*b^4*x^2+14*a^3*b^5*x^4+14/3*a^2*b^6*x^6+a*b^7*x^8+1/10*b^8*x^10+56*a^5*b^3*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:178
  public void test0308() {
    check( //
        "Integrate[1/(x^7*(a+b*x^2)^2), x]", //
        "(-1/6)/(a^2*x^6)+1/2*b/(a^3*x^4)-3/2*b^2/(a^4*x^2)-1/2*b^3/(a^4*(a+b*x^2))-4*b^3*Log[x]/a^5+2*b^3*Log[a+b*x^2]/a^5");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:192
  public void test0309() {
    check( //
        "Integrate[1/(x^7*(a+b*x^2)^3), x]", //
        "(-1/6)/(a^3*x^6)+3/4*b/(a^4*x^4)-3*b^2/(a^5*x^2)-1/4*b^3/(a^4*(a+b*x^2)^2)-2*b^3/(a^5*(a+b*x^2))-10*b^3*Log[x]/a^6+5*b^3*Log[a+b*x^2]/a^6");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:234
  public void test0310() {
    check( //
        "Integrate[1/(a+b*x^2)^10, x]", //
        "1/18*x/(a*(a+b*x^2)^9)+17/288*x/(a^2*(a+b*x^2)^8)+85/1344*x/(a^3*(a+b*x^2)^7)+1105/16128*x/(a^4*(a+b*x^2)^6)+2431/32256*x/(a^5*(a+b*x^2)^5)+2431/28672*x/(a^6*(a+b*x^2)^4)+2431/24576*x/(a^7*(a+b*x^2)^3)+12155/98304*x/(a^8*(a+b*x^2)^2)+12155/65536*x/(a^9*(a+b*x^2))+12155/65536*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(19/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:248
  public void test0311() {
    check( //
        "Integrate[1/(a-b*x^2)^2, x]", //
        "1/2*x/(a*(a-b*x^2))+1/2*ArcTanh[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:262
  public void test0312() {
    check( //
        "Integrate[1/(a-b*x^2)^5, x]", //
        "1/8*x/(a*(a-b*x^2)^4)+7/48*x/(a^2*(a-b*x^2)^3)+35/192*x/(a^3*(a-b*x^2)^2)+35/128*x/(a^4*(a-b*x^2))+35/128*ArcTanh[x*Sqrt[b]/Sqrt[a]]/(a^(9/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:282
  public void test0313() {
    check( //
        "Integrate[1/(c*(a-d)-(b-c)*x^2), x]", //
        "ArcTanh[x*Sqrt[b-c]/(Sqrt[c]*Sqrt[a-d])]/(Sqrt[b-c]*Sqrt[c]*Sqrt[a-d])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:392
  public void test0314() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/x, x]", //
        "-ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]*Sqrt[a]+Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:420
  public void test0315() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/x^10, x]", //
        "-1/9*(a+b*x^2)^(5/2)/(a*x^9)+4/63*b*(a+b*x^2)^(5/2)/(a^2*x^7)-8/315*b^2*(a+b*x^2)^(5/2)/(a^3*x^5)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:504
  public void test0316() {
    check( //
        "Integrate[Sqrt[-9+4*x^2]/x, x]", //
        "-3*ArcTan[1/3*Sqrt[-9+4*x^2]]+Sqrt[-9+4*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:562
  public void test0317() {
    check( //
        "Integrate[x^2/(a+b*x^2)^(9/2), x]", //
        "1/3*x^3/(a*(a+b*x^2)^(7/2))+4/15*b*x^5/(a^2*(a+b*x^2)^(7/2))+8/105*b^2*x^7/(a^3*(a+b*x^2)^(7/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:708
  public void test0318() {
    check( //
        "Integrate[x^(1+m)*(a*(2+m)+b*(3+m)*x^2)/Sqrt[a+b*x^2], x]", //
        "x^(2+m)*Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:878
  public void test0319() {
    check( //
        "Integrate[(a+b*x^2)^(5/4), x]", //
        "10/21*a*x*(a+b*x^2)^(1/4)+2/7*x*(a+b*x^2)^(5/4)+10/21*a^(5/2)*(1+b*x^2/a)^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:894
  public void test0320() {
    check( //
        "Integrate[1/(a-b*x^2)^(1/4), x]", //
        "2*(1-b*x^2/a)^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a-b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:908
  public void test0321() {
    check( //
        "Integrate[1/(a-b*x^2)^(3/4), x]", //
        "2*(1-b*x^2/a)^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a-b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:922
  public void test0322() {
    check( //
        "Integrate[1/(a-b*x^2)^(5/4), x]", //
        "2*x/(a*(a-b*x^2)^(1/4))-2*(1-b*x^2/a)^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(1/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1026
  public void test0323() {
    check( //
        "Integrate[(c*x)^(1/2)/(a+b*x^2)^(1/4), x]", //
        "x*Sqrt[c*x]/(a+b*x^2)^(1/4)+(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]*Sqrt[c*x]/((a+b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1068
  public void test0324() {
    check( //
        "Integrate[(c*x)^(5/2)/(a+b*x^2)^(5/4), x]", //
        "c*(c*x)^(3/2)/(b*(a+b*x^2)^(1/4))+3*c^2*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]*Sqrt[c*x]/(b^(3/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:41
  public void test0325() {
    check( //
        "Integrate[(c+d*x^2)^2/(a+b*x^2)^2, x]", //
        "d^2*x/b^2+1/2*(b*c-a*d)^2*x/(a*b^2*(a+b*x^2))+1/2*(b*c-a*d)*(b*c+3*a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(5/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:87
  public void test0326() {
    check( //
        "Integrate[Sqrt[1+x^2]/(-1+x^2), x]", //
        "ArcSinh[x]-ArcTanh[x*Sqrt[2]/Sqrt[1+x^2]]*Sqrt[2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:118
  public void test0327() {
    check( //
        "Integrate[1/((a+b*x^2)*(c+d*x^2)^(3/2)), x]", //
        "b*ArcTan[x*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^2])]/((b*c-a*d)^(3/2)*Sqrt[a])-d*x/(c*(b*c-a*d)*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:239
  public void test0328() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/Sqrt[c+d*x^2], x]", //
        "x*Sqrt[a+b*x^2]/Sqrt[c+d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:253
  public void test0329() {
    check( //
        "Integrate[1/(Sqrt[a+b*x^2]*Sqrt[c-d*x^2]), x]", //
        "EllipticF[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[1+b*x^2/a]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[a+b*x^2]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:296
  public void test0330() {
    check( //
        "Integrate[Sqrt[-a-b*x^2]/Sqrt[c-d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1+b*x^2/a]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:310
  public void test0331() {
    check( //
        "Integrate[Sqrt[-a+b*x^2]/Sqrt[-c-d*x^2], x]", //
        "-EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[b]*Sqrt[1-b*x^2/a]*Sqrt[-c-d*x^2]/(d*Sqrt[-a+b*x^2]*Sqrt[1+d*x^2/c])-(b*c+a*d)*EllipticF[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[1+d*x^2/c]/(d*Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[-c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:324
  public void test0332() {
    check( //
        "Integrate[Sqrt[-c+d*x^2]/Sqrt[a+b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[a+b*x^2]*Sqrt[1-d*x^2/c]/(b*Sqrt[1+b*x^2/a]*Sqrt[-c+d*x^2])-(b*c+a*d)*EllipticF[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[1+b*x^2/a]*Sqrt[1-d*x^2/c]/(b*Sqrt[d]*Sqrt[a+b*x^2]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:339
  public void test0333() {
    check( //
        "Integrate[1/(Sqrt[-1+x^2]*Sqrt[7+x^2-4*Sqrt[3]]), x]", //
        "EllipticF[ArcSin[x],-7-4*Sqrt[3]]*Sqrt[1-x^2]/(Sqrt[-1+x^2]*Sqrt[7-4*Sqrt[3]])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:20
  public void test0334() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^6, x]", //
        "-1/5*a*A/x^5+1/3*(-A*b-a*B)/x^3-b*B/x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:34
  public void test0335() {
    check( //
        "Integrate[x^9*(a+b*x^2)^5*(A+B*x^2), x]", //
        "1/10*a^5*A*x^10+1/12*a^4*(5*A*b+a*B)*x^12+5/14*a^3*b*(2*A*b+a*B)*x^14+5/8*a^2*b^2*(A*b+a*B)*x^16+5/18*a*b^3*(A*b+2*a*B)*x^18+1/20*b^4*(A*b+5*a*B)*x^20+1/22*b^5*B*x^22");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:48
  public void test0336() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^5, x]", //
        "-1/4*a^5*A/x^4-1/2*a^4*(5*A*b+a*B)/x^2+5*a^2*b^2*(A*b+a*B)*x^2+5/4*a*b^3*(A*b+2*a*B)*x^4+1/6*b^4*(A*b+5*a*B)*x^6+1/8*b^5*B*x^8+5*a^3*b*(2*A*b+a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:62
  public void test0337() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^19, x]", //
        "-1/18*a^5*A/x^18-1/16*a^4*(5*A*b+a*B)/x^16-5/14*a^3*b*(2*A*b+a*B)/x^14-5/6*a^2*b^2*(A*b+a*B)/x^12-1/2*a*b^3*(A*b+2*a*B)/x^10-1/8*b^4*(A*b+5*a*B)/x^8-1/6*b^5*B/x^6");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:106
  public void test0338() {
    check( //
        "Integrate[x*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "-1/4*(A+B*x^2)^2/((A*b-a*B)*(a+b*x^2)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:134
  public void test0339() {
    check( //
        "Integrate[x^2*(a*c+b*c*x^2)/(a+b*x^2), x]", //
        "1/3*c*x^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:166
  public void test0340() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^4, x]", //
        "-1/3*a^2*c/x^3-a*(2*b*c+a*d)/x+b*(b*c+2*a*d)*x+1/3*b^2*d*x^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:210
  public void test0341() {
    check( //
        "Integrate[x^2*(a+b*x^2)^2/(c+d*x^2)^3, x]", //
        "b^2*x/d^3+1/4*(b*c-a*d)^2*x^3/(c*d^2*(c+d*x^2)^2)+1/8*(b*c-a*d)*(7*b*c+a*d)*x/(c*d^3*(c+d*x^2))-1/8*(15*b^2*c^2-6*a*b*c*d-a^2*d^2)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(c^(3/2)*d^(7/2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:244
  public void test0342() {
    check( //
        "Integrate[x^4*(c+d*x^2)^3/(a+b*x^2), x]", //
        "-a*(b*c-a*d)^3*x/b^5+1/3*(b*c-a*d)^3*x^3/b^4+1/5*d*(3*b^2*c^2-3*a*b*c*d+a^2*d^2)*x^5/b^3+1/7*d^2*(3*b*c-a*d)*x^7/b^2+1/9*d^3*x^9/b+a^(3/2)*(b*c-a*d)^3*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(11/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:274
  public void test0343() {
    check( //
        "Integrate[1/(x*(a+b*x^2)*(c+d*x^2)^2), x]", //
        "-1/2*d/(c*(b*c-a*d)*(c+d*x^2))+Log[x]/(a*c^2)-1/2*b^2*Log[a+b*x^2]/(a*(b*c-a*d)^2)+1/2*d*(2*b*c-a*d)*Log[c+d*x^2]/(c^2*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:350
  public void test0344() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)^2*(c+d*x^2)^3), x]", //
        "(-1/2)/(a^2*c^3*x^2)-1/2*b^4/(a^2*(b*c-a*d)^3*(a+b*x^2))-1/4*d^3/(c^2*(b*c-a*d)^2*(c+d*x^2)^2)-d^3*(2*b*c-a*d)/(c^3*(b*c-a*d)^3*(c+d*x^2))-(2*b*c+3*a*d)*Log[x]/(a^3*c^4)+1/2*b^4*(2*b*c-5*a*d)*Log[a+b*x^2]/(a^3*(b*c-a*d)^4)+1/2*d^3*(10*b^2*c^2-10*a*b*c*d+3*a^2*d^2)*Log[c+d*x^2]/(c^4*(b*c-a*d)^4)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:396
  public void test0345() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^(5/2), x]", //
        "-2/3*a*A/x^(3/2)+2/5*b*B*x^(5/2)+2*(A*b+a*B)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:410
  public void test0346() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x^2)/Sqrt[x], x]", //
        "2/5*a^2*(3*A*b+a*B)*x^(5/2)+2/3*a*b*(A*b+a*B)*x^(9/2)+2/13*b^2*(A*b+3*a*B)*x^(13/2)+2/17*b^3*B*x^(17/2)+2*a^3*A*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:458
  public void test0347() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^(5/2), x]", //
        "-2/3*a^2*c^2/x^(3/2)+2/5*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^(5/2)+4/9*b*d*(b*c+a*d)*x^(9/2)+2/13*b^2*d^2*x^(13/2)+4*a*c*(b*c+a*d)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:474
  public void test0348() {
    check( //
        "Integrate[(a+b*x^2)^2/((c+d*x^2)*Sqrt[x]), x]", //
        "2/5*b^2*x^(5/2)/d-(b*c-a*d)^2*ArcTan[1-d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(3/4)*d^(9/4)*Sqrt[2])+(b*c-a*d)^2*ArcTan[1+d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(3/4)*d^(9/4)*Sqrt[2])-1/2*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]-c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(3/4)*d^(9/4)*Sqrt[2])+1/2*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]+c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(3/4)*d^(9/4)*Sqrt[2])-2*b*(b*c-2*a*d)*Sqrt[x]/d^2");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:171
  public void test0349() {
    check( //
        "Integrate[(-x^2+2*x^4)/(1+2*x^2), x]", //
        "-x+1/3*x^3+ArcTan[x*Sqrt[2]]/Sqrt[2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2227
  public void test0350() {
    check( //
        "Integrate[1/((a+b/x^2)^(1/2)*x^3), x]", //
        "-Sqrt[a+b/x^2]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:29
  public void test0351() {
    check( //
        "Integrate[1/x^4, x]", //
        "(-1/3)/x^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:47
  public void test0352() {
    check( //
        "Integrate[1/x^(4/3), x]", //
        "(-3)/x^(1/3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:267
  public void test0353() {
    check( //
        "Integrate[x^5/(a+b*x)^10, x]", //
        "1/9*x^6/(a*(a+b*x)^9)+1/24*x^6/(a^2*(a+b*x)^8)+1/84*x^6/(a^3*(a+b*x)^7)+1/504*x^6/(a^4*(a+b*x)^6)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:311
  public void test0354() {
    check( //
        "Integrate[1/(2+2*x), x]", //
        "1/2*Log[1+x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:405
  public void test0355() {
    check( //
        "Integrate[1/(a+b*x)^(5/2), x]", //
        "(-2/3)/(b*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:661
  public void test0356() {
    check( //
        "Integrate[1/(Sqrt[x]*Sqrt[a-b*x]), x]", //
        "2*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a-b*x]]/Sqrt[b]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:675
  public void test0357() {
    check( //
        "Integrate[1/(x^(5/2)*(a-b*x)^(5/2)), x]", //
        "2/3/(a*x^(3/2)*(a-b*x)^(3/2))+4/(a^2*x^(3/2)*Sqrt[a-b*x])-16/3*Sqrt[a-b*x]/(a^3*x^(3/2))-32/3*b*Sqrt[a-b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:689
  public void test0358() {
    check( //
        "Integrate[1/(x^(5/2)*(2+b*x)^(3/2)), x]", //
        "1/(x^(3/2)*Sqrt[2+b*x])-2/3*Sqrt[2+b*x]/x^(3/2)+2/3*b*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:849
  public void test0359() {
    check( //
        "Integrate[(a+b*x)*Sqrt[c*x^2]/x^2, x]", //
        "b*Sqrt[c*x^2]+a*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:863
  public void test0360() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x), x]", //
        "1/6*a*c^2*x^5*Sqrt[c*x^2]+1/7*b*c^2*x^6*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:879
  public void test0361() {
    check( //
        "Integrate[x^2*(a+b*x)/(c*x^2)^(3/2), x]", //
        "b*x^2/(c*Sqrt[c*x^2])+a*x*Log[x]/(c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:893
  public void test0362() {
    check( //
        "Integrate[(a+b*x)/(x^4*(c*x^2)^(5/2)), x]", //
        "-1/8*a/(c^2*x^7*Sqrt[c*x^2])-1/7*b/(c^2*x^6*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:911
  public void test0363() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^2/x^2, x]", //
        "1/2*a^2*c*x*Sqrt[c*x^2]+2/3*a*b*c*x^2*Sqrt[c*x^2]+1/4*b^2*c*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:927
  public void test0364() {
    check( //
        "Integrate[(a+b*x)^2/Sqrt[c*x^2], x]", //
        "2*a*b*x^2/Sqrt[c*x^2]+1/2*b^2*x^3/Sqrt[c*x^2]+a^2*x*Log[x]/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:941
  public void test0365() {
    check( //
        "Integrate[x^2*(a+b*x)^2/(c*x^2)^(5/2), x]", //
        "-2*a*b/(c^2*Sqrt[c*x^2])-1/2*a^2/(c^2*x*Sqrt[c*x^2])+b^2*x*Log[x]/(c^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:959
  public void test0366() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^4*(a+b*x)), x]", //
        "-1/2*Sqrt[c*x^2]/(a*x^3)+b*Sqrt[c*x^2]/(a^2*x^2)+b^2*Log[x]*Sqrt[c*x^2]/(a^3*x)-b^2*Log[a+b*x]*Sqrt[c*x^2]/(a^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:973
  public void test0367() {
    check( //
        "Integrate[(c*x^2)^(5/2)/(x^4*(a+b*x)), x]", //
        "c^2*Sqrt[c*x^2]/b-a*c^2*Log[a+b*x]*Sqrt[c*x^2]/(b^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:989
  public void test0368() {
    check( //
        "Integrate[x^4/((c*x^2)^(3/2)*(a+b*x)), x]", //
        "x^2/(b*c*Sqrt[c*x^2])-a*x*Log[a+b*x]/(b^2*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1023
  public void test0369() {
    check( //
        "Integrate[1/(x*(a+b*x)^2*Sqrt[c*x^2]), x]", //
        "(-1)/(a^2*Sqrt[c*x^2])-b*x/(a^2*(a+b*x)*Sqrt[c*x^2])-2*b*x*Log[x]/(a^3*Sqrt[c*x^2])+2*b*x*Log[a+b*x]/(a^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1044
  public void test0370() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^n/x, x]", //
        "a^2*c*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^3*(1+n)*x)-2*a*c*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^3*(2+n)*x)+c*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^3*(3+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1094
  public void test0371() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(3/2)*(a+b*x)^2, x]", //
        "a^2*c*(d*x)^(4+m)*Sqrt[c*x^2]/(d^4*(4+m)*x)+2*a*b*c*(d*x)^(5+m)*Sqrt[c*x^2]/(d^5*(5+m)*x)+b^2*c*(d*x)^(6+m)*Sqrt[c*x^2]/(d^6*(6+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1148
  public void test0372() {
    check( //
        "Integrate[1/((b*c/d+b*x)*(c+d*x)^3), x]", //
        "(-1/3)/(b*(c+d*x)^3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1264
  public void test0373() {
    check( //
        "Integrate[1/((1-x)^(1/2)*(1+x)^(1/2)), x]", //
        "ArcSin[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1294
  public void test0374() {
    check( //
        "Integrate[(a+a*x)^(3/2)*(c-c*x)^(3/2), x]", //
        "1/4*x*(a+a*x)^(3/2)*(c-c*x)^(3/2)+3/4*a^(3/2)*c^(3/2)*ArcTan[Sqrt[c]*Sqrt[a+a*x]/(Sqrt[a]*Sqrt[c-c*x])]+3/8*a*c*x*Sqrt[a+a*x]*Sqrt[c-c*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1308
  public void test0375() {
    check( //
        "Integrate[1/((a+b*x)^(9/2)*(a*c-b*c*x)^(9/2)), x]", //
        "1/7*x/(a^2*c*(a+b*x)^(7/2)*(a*c-b*c*x)^(7/2))+6/35*x/(a^4*c^2*(a+b*x)^(5/2)*(a*c-b*c*x)^(5/2))+8/35*x/(a^6*c^3*(a+b*x)^(3/2)*(a*c-b*c*x)^(3/2))+16/35*x/(a^8*c^4*Sqrt[a+b*x]*Sqrt[a*c-b*c*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1372
  public void test0376() {
    check( //
        "Integrate[1/((a-I*a*x)^(1/4)*(a+I*a*x)^(5/4)), x]", //
        "2*I/(a*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))+2*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1497
  public void test0377() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^14, x]", //
        "-1/13*(c+d*x)^11/((b*c-a*d)*(a+b*x)^13)+1/78*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^12)-1/858*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1564
  public void test0378() {
    check( //
        "Integrate[1/(c+d*x), x]", //
        "Log[c+d*x]/d");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1592
  public void test0379() {
    check( //
        "Integrate[(a+b*x)^4/(c+d*x)^8, x]", //
        "1/7*(a+b*x)^5/((b*c-a*d)*(c+d*x)^7)+1/21*b*(a+b*x)^5/((b*c-a*d)^2*(c+d*x)^6)+1/105*b^2*(a+b*x)^5/((b*c-a*d)^3*(c+d*x)^5)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1716
  public void test0380() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(a+b*x)^(11/2), x]", //
        "-2/9*(c+d*x)^(5/2)/((b*c-a*d)*(a+b*x)^(9/2))+8/63*d*(c+d*x)^(5/2)/((b*c-a*d)^2*(a+b*x)^(7/2))-16/315*d^2*(c+d*x)^(5/2)/((b*c-a*d)^3*(a+b*x)^(5/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1760
  public void test0381() {
    check( //
        "Integrate[1/((a+b*x)^(9/2)*(c+d*x)^(5/2)), x]", //
        "(-2/7)/((b*c-a*d)*(a+b*x)^(7/2)*(c+d*x)^(3/2))+4/7*d/((b*c-a*d)^2*(a+b*x)^(5/2)*(c+d*x)^(3/2))-32/21*d^2/((b*c-a*d)^3*(a+b*x)^(3/2)*(c+d*x)^(3/2))+64/7*d^3/((b*c-a*d)^4*(c+d*x)^(3/2)*Sqrt[a+b*x])+256/21*d^4*Sqrt[a+b*x]/((b*c-a*d)^5*(c+d*x)^(3/2))+512/21*b*d^4*Sqrt[a+b*x]/((b*c-a*d)^6*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2059
  public void test0382() {
    check( //
        "Integrate[(a+b*x)^(5/6)/(c+d*x)^(29/6), x]", //
        "6/23*(a+b*x)^(11/6)/((b*c-a*d)*(c+d*x)^(23/6))+72/391*b*(a+b*x)^(11/6)/((b*c-a*d)^2*(c+d*x)^(17/6))+432/4301*b^2*(a+b*x)^(11/6)/((b*c-a*d)^3*(c+d*x)^(11/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2087
  public void test0383() {
    check( //
        "Integrate[1/((a+b*x)^(1/6)*(c+d*x)^(23/6)), x]", //
        "6/17*(a+b*x)^(5/6)/((b*c-a*d)*(c+d*x)^(17/6))+72/187*b*(a+b*x)^(5/6)/((b*c-a*d)^2*(c+d*x)^(11/6))+432/935*b^2*(a+b*x)^(5/6)/((b*c-a*d)^3*(c+d*x)^(5/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2113
  public void test0384() {
    check( //
        "Integrate[1/((a+b*x)^(7/6)*(c+d*x)^(17/6)), x]", //
        "(-6)/((b*c-a*d)*(a+b*x)^(1/6)*(c+d*x)^(11/6))-72/11*d*(a+b*x)^(5/6)/((b*c-a*d)^2*(c+d*x)^(11/6))-432/55*b*d*(a+b*x)^(5/6)/((b*c-a*d)^3*(c+d*x)^(5/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2179
  public void test0385() {
    check( //
        "Integrate[7+4*x, x]", //
        "7*x+2*x^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:18
  public void test0386() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x, x]", //
        "-2*a^3*b*c^3*x+2/3*a*b^3*c^3*x^3-1/4*b^4*c^3*x^4+a^4*c^3*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:32
  public void test0387() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^2, x]", //
        "-a^5*c^4/x+2*a^3*b^2*c^4*x+a^2*b^3*c^4*x^2-a*b^4*c^4*x^3+1/4*b^5*c^4*x^4-3*a^4*b*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:46
  public void test0388() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^2, x]", //
        "-a^6*c^5/x+5*a^4*b^2*c^5*x-5/3*a^2*b^4*c^5*x^3+a*b^5*c^5*x^4-1/5*b^6*c^5*x^5-4*a^5*b*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:60
  public void test0389() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^6/x^11, x]", //
        "-1/10*c^6*(a-b*x)^7/x^10-13/90*b*c^6*(a-b*x)^7/(a*x^9)-13/360*b^2*c^6*(a-b*x)^7/(a^2*x^8)-13/2520*b^3*c^6*(a-b*x)^7/(a^3*x^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:118
  public void test0390() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^5, x]", //
        "-1/4*a*A/x^4+1/3*(-A*b-a*B)/x^3-1/2*b*B/x^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:132
  public void test0391() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^8, x]", //
        "-1/7*a^2*A/x^7-1/6*a*(2*A*b+a*B)/x^6-1/5*b*(A*b+2*a*B)/x^5-1/4*b^2*B/x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:146
  public void test0392() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^9, x]", //
        "-1/8*a^3*A/x^8-1/7*a^2*(3*A*b+a*B)/x^7-1/2*a*b*(A*b+a*B)/x^6-1/5*b^2*(A*b+3*a*B)/x^5-1/4*b^3*B/x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:160
  public void test0393() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^7, x]", //
        "-1/5*a^5*B/x^5-5/4*a^4*b*B/x^4-10/3*a^3*b^2*B/x^3-5*a^2*b^3*B/x^2-5*a*b^4*B/x-1/6*A*(a+b*x)^6/(a*x^6)+b^5*B*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:188
  public void test0394() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^12, x]", //
        "-1/10*a^10*B/x^10-10/9*a^9*b*B/x^9-45/8*a^8*b^2*B/x^8-120/7*a^7*b^3*B/x^7-35*a^6*b^4*B/x^6-252/5*a^5*b^5*B/x^5-105/2*a^4*b^6*B/x^4-40*a^3*b^7*B/x^3-45/2*a^2*b^8*B/x^2-10*a*b^9*B/x-1/11*A*(a+b*x)^11/(a*x^11)+b^10*B*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:446
  public void test0395() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^(5/2), x]", //
        "-2/3*a^3*A/x^(3/2)+2/3*b^2*(A*b+3*a*B)*x^(3/2)+2/5*b^3*B*x^(5/2)-2*a^2*(3*A*b+a*B)/Sqrt[x]+6*a*b*(A*b+a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:665
  public void test0396() {
    check( //
        "Integrate[(A+B*x)/(x^(9/2)*Sqrt[a+b*x]), x]", //
        "-2/7*A*Sqrt[a+b*x]/(a*x^(7/2))+2/35*(6*A*b-7*a*B)*Sqrt[a+b*x]/(a^2*x^(5/2))-8/105*b*(6*A*b-7*a*B)*Sqrt[a+b*x]/(a^3*x^(3/2))+16/105*b^2*(6*A*b-7*a*B)*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:679
  public void test0397() {
    check( //
        "Integrate[(A+B*x)/(x^(13/2)*(a+b*x)^(3/2)), x]", //
        "-2/11*A/(a*x^(11/2)*Sqrt[a+b*x])-2/11*(12*A*b-11*a*B)/(a^2*x^(9/2)*Sqrt[a+b*x])+20/99*(12*A*b-11*a*B)*Sqrt[a+b*x]/(a^3*x^(9/2))-160/693*b*(12*A*b-11*a*B)*Sqrt[a+b*x]/(a^4*x^(7/2))+64/231*b^2*(12*A*b-11*a*B)*Sqrt[a+b*x]/(a^5*x^(5/2))-256/693*b^3*(12*A*b-11*a*B)*Sqrt[a+b*x]/(a^6*x^(3/2))+512/693*b^4*(12*A*b-11*a*B)*Sqrt[a+b*x]/(a^7*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:713
  public void test0398() {
    check( //
        "Integrate[(c+d*x)^(3/2)*Sqrt[a+b*x]/x^5, x]", //
        "-1/4*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(a*c*x^4)+1/64*(b*c-a*d)^3*(5*b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(5/2))+1/96*(b*c-a*d)*(5*b*c+3*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*c^2*x^2)+1/24*(5*b*c+3*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*c^2*x^3)-1/64*(b*c-a*d)^2*(5*b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:761
  public void test0399() {
    check( //
        "Integrate[(a+b*x)^(3/2)*Sqrt[c+d*x]/x^6, x]", //
        "1/128*(b*c-a*d)^3*(3*b^2*c^2+6*a*b*c*d+7*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(9/2))-1/5*(a+b*x)^(3/2)*Sqrt[c+d*x]/x^5-1/40*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x^4)-1/240*(3*b^2*c/a+12*b*d-7*a*d^2/c)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x^3)+1/960*(15*b^3*c^3-9*a*b^2*c^2*d+61*a^2*b*c*d^2-35*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^3*x^2)-1/1920*(45*b^4*c^4-30*a*b^3*c^3*d-36*a^2*b^2*c^2*d^2+190*a^3*b*c*d^3-105*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^4*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:775
  public void test0400() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^2, x]", //
        "-(a+b*x)^(3/2)*(c+d*x)^(5/2)/x-c^(3/2)*(3*b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]+1/8*(5*b^3*c^3+45*a*b^2*c^2*d+15*a^2*b*c*d^2-a^3*d^3)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*Sqrt[d])+1/12*(5*b*c+19*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]+4/3*b*(c+d*x)^(5/2)*Sqrt[a+b*x]+1/8*(5*b^2*c^2+26*a*b*c*d+a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:823
  public void test0401() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^4, x]", //
        "-1/12*(5*b*c+3*a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(c*x^2)-1/3*(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^3-1/8*(5*b^3*c^3+45*a*b^2*c^2*d+15*a^2*b*c*d^2-a^3*d^3)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(3/2)*Sqrt[a])+b^(3/2)*(3*b*c+5*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]-1/8*(5*b^2*c^2+12*a*b*c*d-a^2*d^2)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(c^2*x)+1/8*d*(19*b^2*c^2+14*a*b*c*d-a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/c^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:871
  public void test0402() {
    check( //
        "Integrate[Sqrt[c+d*x]/(x^3*Sqrt[a+b*x]), x]", //
        "-1/4*(b*c-a*d)*(3*b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(3/2))-1/2*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c*x^2)+1/4*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:885
  public void test0403() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^2*Sqrt[a+b*x]), x]", //
        "c^(3/2)*(b*c-5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/a^(3/2)+d^(3/2)*(5*b*c-a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/b^(3/2)-c*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x)+d*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*b)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:915
  public void test0404() {
    check( //
        "Integrate[1/(x^2*(c+d*x)^(3/2)*Sqrt[a+b*x]), x]", //
        "(b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(5/2))-d*(b*c-3*a*d)*Sqrt[a+b*x]/(a*c^2*(b*c-a*d)*Sqrt[c+d*x])-Sqrt[a+b*x]/(a*c*x*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:947
  public void test0405() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^4*(a+b*x)^(3/2)), x]", //
        "5/8*(b*c-a*d)^2*(7*b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*Sqrt[c])-5/24*(b*c-a*d)*(7*b*c-a*d)*(c+d*x)^(3/2)/(a^3*c*x*Sqrt[a+b*x])+1/12*(7*b*c-a*d)*(c+d*x)^(5/2)/(a^2*c*x^2*Sqrt[a+b*x])-1/3*(c+d*x)^(7/2)/(a*c*x^3*Sqrt[a+b*x])-5/8*(b*c-a*d)^2*(7*b*c-a*d)*Sqrt[c+d*x]/(a^4*c*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:963
  public void test0406() {
    check( //
        "Integrate[x/((a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "2*a/(b*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])+2/3*(b*c+3*a*d)*Sqrt[a+b*x]/(b*(b*c-a*d)^2*(c+d*x)^(3/2))+4/3*(b*c+3*a*d)*Sqrt[a+b*x]/((b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:981
  public void test0407() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^5*(a+b*x)^(5/2)), x]", //
        "-1/4*c*(c+d*x)^(3/2)/(a*x^4*(a+b*x)^(3/2))-5/64*(b*c-a*d)*(231*b^3*c^3-189*a*b^2*c^2*d+21*a^2*b*c*d^2+a^3*d^3)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(13/2)*c^(3/2))+1/64*b*(b*c-a*d)*(385*b^2*c^2-238*a*b*c*d+5*a^2*d^2)*Sqrt[c+d*x]/(a^5*c*(a+b*x)^(3/2))+11/24*c*(b*c-a*d)*Sqrt[c+d*x]/(a^2*x^3*(a+b*x)^(3/2))-1/96*(99*b*c-59*a*d)*(b*c-a*d)*Sqrt[c+d*x]/(a^3*x^2*(a+b*x)^(3/2))+1/64*(b*c-a*d)*(231*b^2*c^2-156*a*b*c*d+5*a^2*d^2)*Sqrt[c+d*x]/(a^4*c*x*(a+b*x)^(3/2))+1/64*b*(1155*b^3*c^3-1715*a*b^2*c^2*d+581*a^2*b*c*d^2-5*a^3*d^3)*Sqrt[c+d*x]/(a^6*c*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1030
  public void test0408() {
    check( //
        "Integrate[Sqrt[-1+x]*Sqrt[1+x]/x^2, x]", //
        "ArcCosh[x]-Sqrt[-1+x]*Sqrt[1+x]/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1087
  public void test0409() {
    check( //
        "Integrate[(a+b*x)^(1/4)/(x^2*(c+d*x)^(1/4)), x]", //
        "-(a+b*x)^(1/4)*(c+d*x)^(3/4)/(c*x)-1/2*(b*c-a*d)*ArcTan[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(3/4)*c^(5/4))-1/2*(b*c-a*d)*ArcTanh[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(3/4)*c^(5/4))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1336
  public void test0410() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^19, x]", //
        "-1/18*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^18)+1/306*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^17)+1/816*b*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^3*(d+e*x)^16)+1/2448*b^2*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^4*(d+e*x)^15)+1/8568*b^3*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^5*(d+e*x)^14)+1/37128*b^4*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^6*(d+e*x)^13)+1/222768*b^5*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^7*(d+e*x)^12)+1/2450448*b^6*(11*b*B*d+7*A*b*e-18*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^8*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2077
  public void test0411() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)*Sqrt[1-2*x], x]", //
        "-539/24*(1-2*x)^(3/2)+707/40*(1-2*x)^(5/2)-309/56*(1-2*x)^(7/2)+5/8*(1-2*x)^(9/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2091
  public void test0412() {
    check( //
        "Integrate[(3+5*x)^2*Sqrt[1-2*x]/(2+3*x), x]", //
        "-155/54*(1-2*x)^(3/2)+5/6*(1-2*x)^(5/2)-2/27*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]+2/27*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2153
  public void test0413() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)/(2+3*x), x]", //
        "-2/27*(1-2*x)^(3/2)-1/3*(1-2*x)^(5/2)+14/27*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-14/27*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2565
  public void test0414() {
    check( //
        "Integrate[(A+B*x)/((d+e*x)^(7/2)*Sqrt[a+b*x]), x]", //
        "-2/5*(B*d-A*e)*Sqrt[a+b*x]/(e*(b*d-a*e)*(d+e*x)^(5/2))+2/15*(b*B*d+4*A*b*e-5*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^2*(d+e*x)^(3/2))+4/15*b*(b*B*d+4*A*b*e-5*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^3*Sqrt[d+e*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2597
  public void test0415() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4, x]", //
        "1/7*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-4477/392*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+37/28*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-407/392*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2611
  public void test0416() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^6, x]", //
        "-783959/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/15*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5-107/2520*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+641/15120*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+17981/84672*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+1852307/1185408*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2625
  public void test0417() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^8, x]", //
        "-3735929329/120472576*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-59/1764*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^6-1/21*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^7-6577/370440*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+369409/20744640*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+2524471/41489280*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+84539611/232339968*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+8818415317/3252759552*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2655
  public void test0418() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^4*(3+5*x)^(5/2)), x]", //
        "-13246251/392*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-638165/1176*Sqrt[1-2*x]/(3+5*x)^(3/2)+1/3*Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2))+313/84*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+25441/392*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+63678595/12936*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2687
  public void test0419() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^2, x]", //
        "-1/3*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)-43/3888*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-181/243*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+25/12*(3+5*x)^(3/2)*Sqrt[1-2*x]-8/27*(3+5*x)^(5/2)*Sqrt[1-2*x]-3065/1296*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2703
  public void test0420() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^4*Sqrt[3+5*x]), x]", //
        "-21417/56*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/7*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^3+59/28*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+1947/56*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2735
  public void test0421() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^5, x]", //
        "1/4*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^4+55/24*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-73205/448*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+605/32*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-6655/448*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2749
  public void test0422() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^8, x]", //
        "-1/21*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^7+115/756*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^6-1891543995/2458624*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1921/1512*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5-443563/254016*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+2199649/1524096*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+384136145/42674688*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+40175505215/597445632*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2793
  public void test0423() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)*(3+5*x)^(5/2)), x]", //
        "-22/15*(1-2*x)^(3/2)/(3+5*x)^(3/2)-8/75*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-98/3*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+814/25*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2811
  public void test0424() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^4*Sqrt[1-2*x]), x]", //
        "-15235/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/21*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+25/588*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+3895/8232*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2841
  public void test0425() {
    check( //
        "Integrate[1/((2+3*x)*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-2*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2855
  public void test0426() {
    check( //
        "Integrate[(2+3*x)^4/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "8127/2000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/165*(2+3*x)^3*Sqrt[1-2*x]/(3+5*x)^(3/2)-602/9075*(2+3*x)^2*Sqrt[1-2*x]/Sqrt[3+5*x]-7/242000*(12199+1020*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2887
  public void test0427() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^3), x]", //
        "-825/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77*(3+5*x)^(5/2)/((2+3*x)^2*Sqrt[1-2*x])-25/1078*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-75/1372*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2903
  public void test0428() {
    check( //
        "Integrate[(2+3*x)^5/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-291096141/256000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^4*Sqrt[3+5*x]/Sqrt[1-2*x]+76587/17600*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+939/880*(2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x]+21/2816000*(18424549+7645620*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2917
  public void test0429() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "-9/5*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+49/22/(Sqrt[1-2*x]*Sqrt[3+5*x])-1229/1210*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2931
  public void test0430() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^2*(3+5*x)^(5/2)), x]", //
        "-4887/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-58/539)/((3+5*x)^(3/2)*Sqrt[1-2*x])+3/7/((2+3*x)*(3+5*x)^(3/2)*Sqrt[1-2*x])-28705/17787*Sqrt[1-2*x]/(3+5*x)^(3/2)+2841815/195657*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2963
  public void test0431() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^4), x]", //
        "11/21*(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^3)-2585/19208*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+15755/86436*Sqrt[3+5*x]/Sqrt[1-2*x]+32/441*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-187/588*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])-2365/8232*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2979
  public void test0432() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "27/10*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/33*(2+3*x)^2/((1-2*x)^(3/2)*Sqrt[3+5*x])+1/39930*(-66967-111311*x)/(Sqrt[1-2*x]*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2993
  public void test0433() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)*(3+5*x)^(5/2)), x]", //
        "4/231/((1-2*x)^(3/2)*(3+5*x)^(3/2))-162/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+412/5929/((3+5*x)^(3/2)*Sqrt[1-2*x])-19130/195657*Sqrt[1-2*x]/(3+5*x)^(3/2)+1001590/2152227*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3015
  public void test0434() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x], x]", //
        "-37/45*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/45*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/9*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3029
  public void test0435() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x], x]", //
        "-16416987253/18427500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-493825477/18427500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-23/3575*(2+3*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]+2/65*(2+3*x)^(5/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]-1865989/1126125*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-564731/2252250*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2014/53625*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-493825477/40540500*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3045
  public void test0436() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x], x]", //
        "-17/625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-146/625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+2/25*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-9/125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3059
  public void test0437() {
    check( //
        "Integrate[(2+3*x)^(9/2)*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "-1473539/218750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-31288/109375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*(2+3*x)^(9/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-118/165*(2+3*x)^(7/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+5153/48125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+958/1925*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-12601/240625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3077
  public void test0438() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(5/2), x]", //
        "-98/27*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+16/27*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/9*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(3/2)+82/27*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3091
  public void test0439() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(5/2), x]", //
        "2/75*(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(7/2)-836091184171/2073093750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-50299451003/4146187500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2503/804375*(2+3*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]+178/14625*(2+3*x)^(5/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]-380132617/506756250*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-57509209/506756250*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-199721/12065625*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-50299451003/9121612500*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3107
  public void test0440() {
    check( //
        "Integrate[(1-2*x)^(3/2)/(Sqrt[2+3*x]*Sqrt[3+5*x]), x]", //
        "272/225*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-202/225*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4/45*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3121
  public void test0441() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(7/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(3/2)*(2+3*x)^(7/2)/(3+5*x)^(3/2)-24369/109375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-25643/109375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-6*(2+3*x)^(7/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+3872/4375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+622/175*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+4801/21875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3139
  public void test0442() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(7/2), x]", //
        "-4/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-12/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-2/15*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(5/2)+2/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(3/2)+8*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3153
  public void test0443() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(15/2), x]", //
        "-2/39*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(13/2)+230/1287*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(11/2)-75041008472/53093313*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2257166048/53093313*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1300/891*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-3347620/1702701*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+23210828/11918907*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+1079936248/83432349*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+75041008472/584026443*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3169
  public void test0444() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[2+3*x]/Sqrt[3+5*x], x]", //
        "-408311/590625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-132824/590625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+326/2625*(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]+2/35*(1-2*x)^(5/2)*Sqrt[2+3*x]*Sqrt[3+5*x]+30922/118125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3183
  public void test0445() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "17804/45*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+536/45*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/9*(1-2*x)^(3/2)/((2+3*x)^(3/2)*Sqrt[3+5*x])+1792/27*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-17804/27*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3201
  public void test0446() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[3+5*x]/Sqrt[1-2*x], x]", //
        "-1597/250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-8/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-1/5*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-23/25*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3215
  public void test0447() {
    check( //
        "Integrate[(2+3*x)^(7/2)*(3+5*x)^(5/2)/Sqrt[1-2*x], x]", //
        "-610627101631/36855000*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2295970088/4606875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-14303/12870*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-41/143*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-1/13*(2+3*x)^(7/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-138809831/4504500*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-221673/50050*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2295970088/10135125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3247
  public void test0448() {
    check( //
        "Integrate[(2+3*x)^(5/2)/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-5161/1250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-857/625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-3/25*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-74/125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3261
  public void test0449() {
    check( //
        "Integrate[1/((2+3*x)^(7/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "1344984/1715*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+40456/1715*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+6/35*Sqrt[1-2*x]/((2+3*x)^(5/2)*Sqrt[3+5*x])+436/245*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+60684/1715*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-1344984/3773*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3299
  public void test0450() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[2+3*x]/(1-2*x)^(3/2), x]", //
        "133/6*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(3+5*x)^(3/2)*Sqrt[2+3*x]/Sqrt[1-2*x]+10/3*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3313
  public void test0451() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^(9/2)), x]", //
        "106558/1764735*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-220028/1764735*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*(3+5*x)^(3/2)/((2+3*x)^(7/2)*Sqrt[1-2*x])+229/1029*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-37117/36015*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-106772/252105*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-106558/1764735*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3329
  public void test0452() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "4/11*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-2/11*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+2/11*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])-20/121*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3343
  public void test0453() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(7/2)*(3+5*x)^(5/2)), x]", //
        "-12071114168/1452605*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-363103712/1452605*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/77/((2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x])+138/2695*Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(3/2))+19548/18865*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+4115652/132055*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-181551856/871563*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+12071114168/9587193*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3361
  public void test0454() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-37/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-13/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+11/21*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)-74/147*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3377
  public void test0455() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-78472/275*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-4721/550*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(5/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-679/363*(2+3*x)^(3/2)*Sqrt[3+5*x]/Sqrt[1-2*x]-4517/1210*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3391
  public void test0456() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "8314/5929*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-824/5929*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[2+3*x]/((1-2*x)^(3/2)*Sqrt[3+5*x])+824/17787*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])-41570/195657*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3405
  public void test0457() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(7/2)*(3+5*x)^(5/2)), x]", //
        "4/231/((1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2))-412810345784/111850585*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-12417792656/111850585*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+632/5929/((2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x])-3606/207515*Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(3/2))+649224/1452605*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+140700876/10168235*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-6208896328/67110351*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+412810345784/738213861*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3488
  public void test0458() {
    check( //
        "Integrate[(a+b*x)*(c+d*x)^(-4+n)/(e+f*x)^n, x]", //
        "(b*c-a*d)*(c+d*x)^(-3+n)*(e+f*x)^(1-n)/(d*(d*e-c*f)*(3-n))+(2*a*d*f+b*(c*f*(1-n)-d*e*(3-n)))*(c+d*x)^(-2+n)*(e+f*x)^(1-n)/(d*(d*e-c*f)^2*(2-n)*(3-n))-f*(2*a*d*f+b*(c*f*(1-n)-d*e*(3-n)))*(c+d*x)^(-1+n)*(e+f*x)^(1-n)/(d*(d*e-c*f)^3*(1-n)*(2-n)*(3-n))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3552
  public void test0459() {
    check( //
        "Integrate[(a+b*x)^m*(c+d*x)^(-5-m), x]", //
        "(a+b*x)^(1+m)*(c+d*x)^(-4-m)/((b*c-a*d)*(4+m))+3*b*(a+b*x)^(1+m)*(c+d*x)^(-3-m)/((b*c-a*d)^2*(3+m)*(4+m))+6*b^2*(a+b*x)^(1+m)*(c+d*x)^(-2-m)/((b*c-a*d)^3*(2+m)*(3+m)*(4+m))+6*b^3*(a+b*x)^(1+m)*(c+d*x)^(-1-m)/((b*c-a*d)^4*(1+m)*(2+m)*(3+m)*(4+m))");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:49
  public void test0460() {
    check( //
        "Integrate[(a^2*x^2-(1-a*x)^2)/(x^2*Sqrt[-1+x]*Sqrt[1+x]), x]", //
        "2*a*ArcTan[Sqrt[-1+x]*Sqrt[1+x]]-Sqrt[-1+x]*Sqrt[1+x]/x");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:151
  public void test0461() {
    check( //
        "Integrate[1/((7+5*x)^(1/2)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "2*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[7+5*x]/(Sqrt[253]*Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:51
  public void test0462() {
    check( //
        "Integrate[(a+b*x^2)^3/x^13, x]", //
        "-1/12*a^3/x^12-3/10*a^2*b/x^10-3/8*a*b^2/x^8-1/6*b^3/x^6");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:65
  public void test0463() {
    check( //
        "Integrate[x^9*(a+b*x^2)^5, x]", //
        "1/10*a^5*x^10+5/12*a^4*b*x^12+5/7*a^3*b^2*x^14+5/8*a^2*b^3*x^16+5/18*a*b^4*x^18+1/20*b^5*x^20");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:79
  public void test0464() {
    check( //
        "Integrate[(a+b*x^2)^5/x^19, x]", //
        "-1/18*a^5/x^18-5/16*a^4*b/x^16-5/7*a^3*b^2/x^14-5/6*a^2*b^3/x^12-1/2*a*b^4/x^10-1/8*b^5/x^8");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:107
  public void test0465() {
    check( //
        "Integrate[(a+b*x^2)^8/x^9, x]", //
        "-1/8*a^8/x^8-4/3*a^7*b/x^6-7*a^6*b^2/x^4-28*a^5*b^3/x^2+28*a^3*b^5*x^2+7*a^2*b^6*x^4+4/3*a*b^7*x^6+1/8*b^8*x^8+70*a^4*b^4*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:151
  public void test0466() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)), x]", //
        "(-1/2)/(a*x^2)-b*Log[x]/a^2+1/2*b*Log[a+b*x^2]/a^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:193
  public void test0467() {
    check( //
        "Integrate[1/(x^9*(a+b*x^2)^3), x]", //
        "(-1/8)/(a^3*x^8)+1/2*b/(a^4*x^6)-3/2*b^2/(a^5*x^4)+5*b^3/(a^6*x^2)+1/4*b^4/(a^5*(a+b*x^2)^2)+5/2*b^4/(a^6*(a+b*x^2))+15*b^4*Log[x]/a^7-15/2*b^4*Log[a+b*x^2]/a^7");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:207
  public void test0468() {
    check( //
        "Integrate[x^21/(a+b*x^2)^10, x]", //
        "1/2*x^2/b^10-1/18*a^10/(b^11*(a+b*x^2)^9)+5/8*a^9/(b^11*(a+b*x^2)^8)-45/14*a^8/(b^11*(a+b*x^2)^7)+10*a^7/(b^11*(a+b*x^2)^6)-21*a^6/(b^11*(a+b*x^2)^5)+63/2*a^5/(b^11*(a+b*x^2)^4)-35*a^4/(b^11*(a+b*x^2)^3)+30*a^3/(b^11*(a+b*x^2)^2)-45/2*a^2/(b^11*(a+b*x^2))-5*a*Log[a+b*x^2]/b^11");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:249
  public void test0469() {
    check( //
        "Integrate[1/(x*(a-b*x^2)^2), x]", //
        "1/2/(a*(a-b*x^2))+Log[x]/a^2-1/2*Log[a-b*x^2]/a^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:263
  public void test0470() {
    check( //
        "Integrate[1/(x*(a-b*x^2)^5), x]", //
        "1/8/(a*(a-b*x^2)^4)+1/6/(a^2*(a-b*x^2)^3)+1/4/(a^3*(a-b*x^2)^2)+1/2/(a^4*(a-b*x^2))+Log[x]/a^5-1/2*Log[a-b*x^2]/a^5");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:421
  public void test0471() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/x^12, x]", //
        "-1/11*(a+b*x^2)^(5/2)/(a*x^11)+2/33*b*(a+b*x^2)^(5/2)/(a^2*x^9)-8/231*b^2*(a+b*x^2)^(5/2)/(a^3*x^7)+16/1155*b^3*(a+b*x^2)^(5/2)/(a^4*x^5)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:463
  public void test0472() {
    check( //
        "Integrate[(a+b*x^2)^(9/2), x]", //
        "21/128*a^3*x*(a+b*x^2)^(3/2)+21/160*a^2*x*(a+b*x^2)^(5/2)+9/80*a*x*(a+b*x^2)^(7/2)+1/10*x*(a+b*x^2)^(9/2)+63/256*a^5*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+63/256*a^4*x*Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:549
  public void test0473() {
    check( //
        "Integrate[1/(a+b*x^2)^(5/2), x]", //
        "1/3*x/(a*(a+b*x^2)^(3/2))+2/3*x/(a^2*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:605
  public void test0474() {
    check( //
        "Integrate[x^2/Sqrt[-9-4*x^2], x]", //
        "-9/16*ArcTan[2*x/Sqrt[-9-4*x^2]]-1/8*x*Sqrt[-9-4*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:879
  public void test0475() {
    check( //
        "Integrate[(a-b*x^2)^(5/4), x]", //
        "10/21*a*x*(a-b*x^2)^(1/4)+2/7*x*(a-b*x^2)^(5/4)+10/21*a^(5/2)*(1-b*x^2/a)^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1027
  public void test0476() {
    check( //
        "Integrate[1/((c*x)^(3/2)*(a+b*x^2)^(1/4)), x]", //
        "(-2)/(c*(a+b*x^2)^(1/4)*Sqrt[c*x])+2*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[b]*Sqrt[c*x]/(c^2*(a+b*x^2)^(1/4)*Sqrt[a])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1069
  public void test0477() {
    check( //
        "Integrate[(c*x)^(1/2)/(a+b*x^2)^(5/4), x]", //
        "-2*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[c*x]/((a+b*x^2)^(1/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:26
  public void test0478() {
    check( //
        "Integrate[(a+b*x^2)^3/(c+d*x^2), x]", //
        "b*(b^2*c^2-3*a*b*c*d+3*a^2*d^2)*x/d^3-1/3*b^2*(b*c-3*a*d)*x^3/d^2+1/5*b^3*x^5/d-(b*c-a*d)^3*ArcTan[x*Sqrt[d]/Sqrt[c]]/(d^(7/2)*Sqrt[c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:74
  public void test0479() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/(c+d*x^2)^3, x]", //
        "1/4*x*(a+b*x^2)^(3/2)/(c*(c+d*x^2)^2)+3/8*a^2*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(5/2)*Sqrt[b*c-a*d])+3/8*a*x*Sqrt[a+b*x^2]/(c^2*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:119
  public void test0480() {
    check( //
        "Integrate[1/((a+b*x^2)^2*(c+d*x^2)^(1/2)), x]", //
        "1/2*(b*c-2*a*d)*ArcTan[x*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^2])]/(a^(3/2)*(b*c-a*d)^(3/2))+1/2*b*x*Sqrt[c+d*x^2]/(a*(b*c-a*d)*(a+b*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:240
  public void test0481() {
    check( //
        "Integrate[1/((a+b*x^2)^(1/2)*Sqrt[c+d*x^2]), x]", //
        "EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(a*Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:254
  public void test0482() {
    check( //
        "Integrate[1/(Sqrt[a-b*x^2]*Sqrt[c-d*x^2]), x]", //
        "EllipticF[ArcSin[x*Sqrt[d]/Sqrt[c]],b*c/(a*d)]*Sqrt[c]*Sqrt[1-b*x^2/a]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[a-b*x^2]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:297
  public void test0483() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/Sqrt[-c+d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1+b*x^2/a]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:311
  public void test0484() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/Sqrt[a-b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[c+d*x^2]/(Sqrt[b]*Sqrt[a-b*x^2]*Sqrt[1+d*x^2/c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:325
  public void test0485() {
    check( //
        "Integrate[Sqrt[c-d*x^2]/Sqrt[-a-b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[-a-b*x^2]*Sqrt[1-d*x^2/c]/(b*Sqrt[1+b*x^2/a]*Sqrt[c-d*x^2])+(b*c+a*d)*EllipticF[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[1+b*x^2/a]*Sqrt[1-d*x^2/c]/(b*Sqrt[d]*Sqrt[-a-b*x^2]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:21
  public void test0486() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^7, x]", //
        "-1/6*a*A/x^6+1/4*(-A*b-a*B)/x^4-1/2*b*B/x^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:49
  public void test0487() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^6, x]", //
        "-1/5*a^5*A/x^5-1/3*a^4*(5*A*b+a*B)/x^3-5*a^3*b*(2*A*b+a*B)/x+10*a^2*b^2*(A*b+a*B)*x+5/3*a*b^3*(A*b+2*a*B)*x^3+1/5*b^4*(A*b+5*a*B)*x^5+1/7*b^5*B*x^7");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:63
  public void test0488() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^20, x]", //
        "-1/19*a^5*A/x^19-1/17*a^4*(5*A*b+a*B)/x^17-1/3*a^3*b*(2*A*b+a*B)/x^15-10/13*a^2*b^2*(A*b+a*B)/x^13-5/11*a*b^3*(A*b+2*a*B)/x^11-1/9*b^4*(A*b+5*a*B)/x^9-1/7*b^5*B/x^7");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:79
  public void test0489() {
    check( //
        "Integrate[(A+B*x^2)/(x^4*(a+b*x^2)), x]", //
        "-1/3*A/(a*x^3)+(A*b-a*B)/(a^2*x)+(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/a^(5/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:135
  public void test0490() {
    check( //
        "Integrate[x*(a*c+b*c*x^2)/(a+b*x^2), x]", //
        "1/2*c*x^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:149
  public void test0491() {
    check( //
        "Integrate[x*(a*c+b*c*x^2)/(a+b*x^2)^3, x]", //
        "-1/2*c/(b*(a+b*x^2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:181
  public void test0492() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x, x]", //
        "1/2*a*c^2*(2*b*c+3*a*d)*x^2+1/4*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^4+1/6*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^6+1/8*b*d^2*(3*b*c+2*a*d)*x^8+1/10*b^2*d^3*x^10+a^2*c^3*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:211
  public void test0493() {
    check( //
        "Integrate[x*(a+b*x^2)^2/(c+d*x^2)^3, x]", //
        "-1/4*(b*c-a*d)^2/(d^3*(c+d*x^2)^2)+b*(b*c-a*d)/(d^3*(c+d*x^2))+1/2*b^2*Log[c+d*x^2]/d^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:261
  public void test0494() {
    check( //
        "Integrate[1/(x*(a+b*x^2)*(c+d*x^2)), x]", //
        "Log[x]/(a*c)-1/2*b*Log[a+b*x^2]/(a*(b*c-a*d))+1/2*d*Log[c+d*x^2]/(c*(b*c-a*d))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:293
  public void test0495() {
    check( //
        "Integrate[x^4*(c+d*x^2)/(a+b*x^2)^2, x]", //
        "(b*c-2*a*d)*x/b^3+1/3*d*x^3/b^2+1/2*a*(b*c-a*d)*x/(b^3*(a+b*x^2))-1/2*(3*b*c-5*a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:323
  public void test0496() {
    check( //
        "Integrate[x^3/((a+b*x^2)^2*(c+d*x^2)), x]", //
        "1/2*a/(b*(b*c-a*d)*(a+b*x^2))+1/2*c*Log[a+b*x^2]/(b*c-a*d)^2-1/2*c*Log[c+d*x^2]/(b*c-a*d)^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:337
  public void test0497() {
    check( //
        "Integrate[x/((a+b*x^2)^2*(c+d*x^2)^2), x]", //
        "-1/2*b/((b*c-a*d)^2*(a+b*x^2))-1/2*d/((b*c-a*d)^2*(c+d*x^2))-b*d*Log[a+b*x^2]/(b*c-a*d)^3+b*d*Log[c+d*x^2]/(b*c-a*d)^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:397
  public void test0498() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^(7/2), x]", //
        "-2/5*a*A/x^(5/2)+2/3*b*B*x^(3/2)-2*(A*b+a*B)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:411
  public void test0499() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x^2)/x^(3/2), x]", //
        "2/3*a^2*(3*A*b+a*B)*x^(3/2)+6/7*a*b*(A*b+a*B)*x^(7/2)+2/11*b^2*(A*b+3*a*B)*x^(11/2)+2/15*b^3*B*x^(15/2)-2*a^3*A/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:459
  public void test0500() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^(7/2), x]", //
        "-2/5*a^2*c^2/x^(5/2)+2/3*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^(3/2)+4/7*b*d*(b*c+a*d)*x^(7/2)+2/11*b^2*d^2*x^(11/2)-4*a*c*(b*c+a*d)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:607
  public void test0501() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)*(A+B*x^2)/x^6, x]", //
        "-1/3*B*(a+b*x^2)^(3/2)/x^3-1/5*A*(a+b*x^2)^(5/2)/(a*x^5)+b^(3/2)*B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]-b*B*Sqrt[a+b*x^2]/x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:637
  public void test0502() {
    check( //
        "Integrate[(A+B*x^2)/Sqrt[a+b*x^2], x]", //
        "1/2*(2*A*b-a*B)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)+1/2*B*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:711
  public void test0503() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^(5/2)/x^2, x]", //
        "-5/192*(b^2*c^2-16*a*d*(b*c+3*a*d))*x*(c+d*x^2)^(3/2)/d-1/48*(b^2*c^2-16*a*d*(b*c+3*a*d))*x*(c+d*x^2)^(5/2)/(c*d)-a^2*(c+d*x^2)^(7/2)/(c*x)+1/8*b^2*x*(c+d*x^2)^(7/2)/d-5/128*c^2*(b^2*c^2-16*a*d*(b*c+3*a*d))*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(3/2)-5/128*c*(b^2*c^2-16*a*d*(b*c+3*a*d))*x*Sqrt[c+d*x^2]/d");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:727
  public void test0504() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^4*Sqrt[c+d*x^2]), x]", //
        "b^2*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/Sqrt[d]-1/3*a^2*Sqrt[c+d*x^2]/(c*x^3)-2/3*a*(3*b*c-a*d)*Sqrt[c+d*x^2]/(c^2*x)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:741
  public void test0505() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^6*(c+d*x^2)^(3/2)), x]", //
        "-1/5*a^2/(c*x^5*Sqrt[c+d*x^2])-2/15*a*(5*b*c-3*a*d)/(c^2*x^3*Sqrt[c+d*x^2])+1/15*(-15*b^2*c^2+8*a*d*(5*b*c-3*a*d))/(c^3*x*Sqrt[c+d*x^2])-2/15*d*(15*b^2*c^2-8*a*d*(5*b*c-3*a*d))*x/(c^4*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:759
  public void test0506() {
    check( //
        "Integrate[x^3/((a+b*x^2)*Sqrt[d*x^2]), x]", //
        "x^2/(b*Sqrt[d*x^2])-x*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/(b^(3/2)*Sqrt[d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:777
  public void test0507() {
    check( //
        "Integrate[x^3*(c+d*x^2)^(3/2)/(a+b*x^2), x]", //
        "-1/3*a*(c+d*x^2)^(3/2)/b^2+1/5*(c+d*x^2)^(5/2)/(b*d)+a*(b*c-a*d)^(3/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/b^(7/2)-a*(b*c-a*d)*Sqrt[c+d*x^2]/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:807
  public void test0508() {
    check( //
        "Integrate[x^3/((a+b*x^2)*(c+d*x^2)^(3/2)), x]", //
        "a*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/((b*c-a*d)^(3/2)*Sqrt[b])-c/(d*(b*c-a*d)*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1082
  public void test0509() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(x*Sqrt[c+d*x^2]), x]", //
        "-ArcTanh[Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[a]*Sqrt[c+d*x^2])]*Sqrt[a]/Sqrt[c]+ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]*Sqrt[b]/Sqrt[d]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1140
  public void test0510() {
    check( //
        "Integrate[x^2/(Sqrt[4-x^2]*Sqrt[c+d*x^2]), x]", //
        "EllipticE[ArcSin[1/2*x],-4*d/c]*Sqrt[c+d*x^2]/(d*Sqrt[1+d*x^2/c])-c*EllipticF[ArcSin[1/2*x],-4*d/c]*Sqrt[1+d*x^2/c]/(d*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1154
  public void test0511() {
    check( //
        "Integrate[x^2/(Sqrt[1-x^2]*Sqrt[-1+2*x^2]), x]", //
        "-1/2*EllipticE[ArcCos[x],2]-1/2*EllipticF[ArcCos[x],2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1230
  public void test0512() {
    check( //
        "Integrate[x^2/((-2+3*x^2)*(-1+3*x^2)^(1/4)), x]", //
        "-1/3*ArcTan[x*Sqrt[3/2]/(-1+3*x^2)^(1/4)]/Sqrt[6]-1/3*ArcTanh[x*Sqrt[3/2]/(-1+3*x^2)^(1/4)]/Sqrt[6]+2/3*x*(-1+3*x^2)^(1/4)/(1+Sqrt[-1+3*x^2])-2/3*EllipticE[2*ArcTan[(-1+3*x^2)^(1/4)],1/2]*(1+Sqrt[-1+3*x^2])*Sqrt[x^2/(1+Sqrt[-1+3*x^2])^2]/(x*Sqrt[3])+1/3*EllipticF[2*ArcTan[(-1+3*x^2)^(1/4)],1/2]*(1+Sqrt[-1+3*x^2])*Sqrt[x^2/(1+Sqrt[-1+3*x^2])^2]/(x*Sqrt[3])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1312
  public void test0513() {
    check( //
        "Integrate[(c+d*x^2)/((e*x)^(7/2)*(a+b*x^2)^(7/4)), x]", //
        "-2/5*c/(a*e*(e*x)^(5/2)*(a+b*x^2)^(3/4))-2/15*(8*b*c-5*a*d)/(a^2*e^3*(a+b*x^2)^(3/4)*Sqrt[e*x])+8/15*(8*b*c-5*a*d)*(a+b*x^2)^(1/4)/(a^3*e^3*Sqrt[e*x])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1326
  public void test0514() {
    check( //
        "Integrate[(e*x)^(9/2)*(c+d*x^2)/(a+b*x^2)^(9/4), x]", //
        "2/5*(b*c-a*d)*(e*x)^(11/2)/(a*b*e*(a+b*x^2)^(5/4))+7/30*(6*b*c-11*a*d)*e^3*(e*x)^(3/2)/(b^3*(a+b*x^2)^(1/4))-1/15*(6*b*c-11*a*d)*e*(e*x)^(7/2)/(a*b^2*(a+b*x^2)^(1/4))+7/10*(6*b*c-11*a*d)*e^4*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]*Sqrt[e*x]/(b^(7/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:56
  public void test0515() {
    check( //
        "Integrate[(a+b*x^2)*(c+d*x^2)^(1/2)/(e+f*x^2)^(1/2), x]", //
        "-1/3*(2*b*d*e-b*c*f-3*a*d*f)*x*Sqrt[c+d*x^2]/(d*f*Sqrt[e+f*x^2])+1/3*(2*b*d*e-b*c*f-3*a*d*f)*EllipticE[ArcTan[x*Sqrt[f]/Sqrt[e]],1-d*e/(c*f)]*Sqrt[e]*Sqrt[c+d*x^2]/(d*f^(3/2)*Sqrt[e*(c+d*x^2)/(c*(e+f*x^2))]*Sqrt[e+f*x^2])-1/3*(b*e-3*a*f)*EllipticF[ArcTan[x*Sqrt[f]/Sqrt[e]],1-d*e/(c*f)]*Sqrt[e]*Sqrt[c+d*x^2]/(f^(3/2)*Sqrt[e*(c+d*x^2)/(c*(e+f*x^2))]*Sqrt[e+f*x^2])+1/3*b*x*Sqrt[c+d*x^2]*Sqrt[e+f*x^2]/f");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:143
  public void test0516() {
    check( //
        "Integrate[(a+b*x^2)/((e+f*x^2)^2*Sqrt[c+d*x^2]), x]", //
        "-1/2*(b*c*e-2*a*d*e+a*c*f)*ArcTanh[x*Sqrt[d*e-c*f]/(Sqrt[e]*Sqrt[c+d*x^2])]/(e^(3/2)*(d*e-c*f)^(3/2))+1/2*(b*e-a*f)*x*Sqrt[c+d*x^2]/(e*(d*e-c*f)*(e+f*x^2))");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:24
  public void test0517() {
    check( //
        "Integrate[(A+B*x)*(a+b*x^2)^(3/2), x]", //
        "1/4*A*x*(a+b*x^2)^(3/2)+1/5*B*(a+b*x^2)^(5/2)/b+3/8*a^2*A*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+3/8*a*A*x*Sqrt[a+b*x^2]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:40
  public void test0518() {
    check( //
        "Integrate[(A+B*x)/Sqrt[a+b*x^2], x]", //
        "A*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+B*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:84
  public void test0519() {
    check( //
        "Integrate[x*(A+B*x+C*x^2)/(a+b*x^2)^(9/2), x]", //
        "-1/7*x*(a*B-(A*b-a*C)*x)/(a*b*(a+b*x^2)^(7/2))+1/35*(-5*A*b-2*a*C+b*B*x)/(a*b^2*(a+b*x^2)^(5/2))+4/105*B*x/(a^2*b*(a+b*x^2)^(3/2))+8/105*B*x/(a^3*b*Sqrt[a+b*x^2])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:128
  public void test0520() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x+C*x^2+D*x^3)/x^4, x]", //
        "-1/3*a^3*A/x^3-1/2*a^3*B/x^2-a^2*(3*A*b+a*C)/x+3*a*b*(A*b+a*C)*x+3/2*a*b*(b*B+a*D)*x^2+1/3*b^2*(A*b+3*a*C)*x^3+1/4*b^2*(b*B+3*a*D)*x^4+1/5*b^3*C*x^5+1/6*b^3*D*x^6+a^2*(3*b*B+a*D)*Log[x]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:172
  public void test0521() {
    check( //
        "Integrate[(x^3+x^4)/(1+x^2), x]", //
        "-x+1/2*x^2+1/3*x^3+ArcTan[x]-1/2*Log[1+x^2]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:230
  public void test0522() {
    check( //
        "Integrate[(c+d*x^2+e*x^4+f*x^6)/(x^4*Sqrt[a+b*x^2]), x]", //
        "1/2*(2*b*e-a*f)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)-1/3*c*Sqrt[a+b*x^2]/(a*x^3)+1/3*(2*b*c-3*a*d)*Sqrt[a+b*x^2]/(a^2*x)+1/2*f*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:158
  public void test0523() {
    check( //
        "Integrate[1/(b/x^3)^(1/3), x]", //
        "1/2*x/(b/x^3)^(1/3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:227
  public void test0524() {
    check( //
        "Integrate[x^2/(a*x^n)^(1/n), x]", //
        "1/2*x^3/(a*x^n)^(1/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:293
  public void test0525() {
    check( //
        "Integrate[x^8*(a+b*x^3)^3, x]", //
        "1/9*a^3*x^9+1/4*a^2*b*x^12+1/5*a*b^2*x^15+1/18*b^3*x^18");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:321
  public void test0526() {
    check( //
        "Integrate[(a+b*x^3)^5/x^7, x]", //
        "-1/6*a^5/x^6-5/3*a^4*b/x^3+10/3*a^2*b^3*x^3+5/6*a*b^4*x^6+1/9*b^5*x^9+10*a^3*b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:349
  public void test0527() {
    check( //
        "Integrate[(a+b*x^3)^8/x^7, x]", //
        "-1/6*a^8/x^6-8/3*a^7*b/x^3+56/3*a^5*b^3*x^3+35/3*a^4*b^4*x^6+56/9*a^3*b^5*x^9+7/3*a^2*b^6*x^12+8/15*a*b^7*x^15+1/18*b^8*x^18+28*a^6*b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:379
  public void test0528() {
    check( //
        "Integrate[1/(x^4*(a+b*x^3)), x]", //
        "(-1/3)/(a*x^3)-b*Log[x]/a^2+1/3*b*Log[a+b*x^3]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:421
  public void test0529() {
    check( //
        "Integrate[1/(1+a-b*x^3), x]", //
        "-1/3*Log[(1+a)^(1/3)-b^(1/3)*x]/((1+a)^(2/3)*b^(1/3))+1/6*Log[(1+a)^(2/3)+(1+a)^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/((1+a)^(2/3)*b^(1/3))+ArcTan[(1+2*b^(1/3)*x/(1+a)^(1/3))/Sqrt[3]]/((1+a)^(2/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:593
  public void test0530() {
    check( //
        "Integrate[(a+b*x^3)^(1/3)/x^2, x]", //
        "-(a+b*x^3)^(1/3)/x-1/2*b^(1/3)*Log[b^(1/3)*x-(a+b*x^3)^(1/3)]-b^(1/3)*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/Sqrt[3]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:636
  public void test0531() {
    check( //
        "Integrate[1/(x^9*(a+b*x^3)^(1/3)), x]", //
        "-1/8*(a+b*x^3)^(2/3)/(a*x^8)+3/20*b*(a+b*x^3)^(2/3)/(a^2*x^5)-9/40*b^2*(a+b*x^3)^(2/3)/(a^3*x^2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:650
  public void test0532() {
    check( //
        "Integrate[1/(x^11*(a+b*x^3)^(2/3)), x]", //
        "-1/10*(a+b*x^3)^(1/3)/(a*x^10)+9/70*b*(a+b*x^3)^(1/3)/(a^2*x^7)-27/140*b^2*(a+b*x^3)^(1/3)/(a^3*x^4)+81/140*b^3*(a+b*x^3)^(1/3)/(a^4*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:720
  public void test0533() {
    check( //
        "Integrate[(a+b*x^4)^2/x^5, x]", //
        "-1/4*a^2/x^4+1/4*b^2*x^4+2*a*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:736
  public void test0534() {
    check( //
        "Integrate[x^7/(a+c*x^4), x]", //
        "1/4*x^4/c-1/4*a*Log[a+c*x^4]/c^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:778
  public void test0535() {
    check( //
        "Integrate[x^9/(2+3*x^4), x]", //
        "-1/9*x^2+1/18*x^6+1/9*ArcTan[x^2*Sqrt[3/2]]*Sqrt[2/3]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:792
  public void test0536() {
    check( //
        "Integrate[1/(x*(2+3*x^4)^2), x]", //
        "1/8/(2+3*x^4)+1/4*Log[x]-1/16*Log[2+3*x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:874
  public void test0537() {
    check( //
        "Integrate[x*Sqrt[a+c*x^4], x]", //
        "1/4*a*ArcTanh[x^2*Sqrt[c]/Sqrt[a+c*x^4]]/Sqrt[c]+1/4*x^2*Sqrt[a+c*x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:988
  public void test0538() {
    check( //
        "Integrate[1/(x^11*Sqrt[1-x^4]), x]", //
        "-1/10*Sqrt[1-x^4]/x^10-2/15*Sqrt[1-x^4]/x^6-4/15*Sqrt[1-x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1086
  public void test0539() {
    check( //
        "Integrate[x^2/Sqrt[-1+x^4], x]", //
        "x*(1+x^2)/Sqrt[-1+x^4]+EllipticF[ArcSin[x*Sqrt[2]/Sqrt[-1+x^2]],1/2]*Sqrt[-1+x^2]*Sqrt[1+x^2]/(Sqrt[2]*Sqrt[-1+x^4])-EllipticE[ArcSin[x*Sqrt[2]/Sqrt[-1+x^2]],1/2]*Sqrt[2]*Sqrt[-1+x^2]*Sqrt[1+x^2]/Sqrt[-1+x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1192
  public void test0540() {
    check( //
        "Integrate[(a+b*x^4)^(7/4), x]", //
        "7/32*a*x*(a+b*x^4)^(3/4)+1/8*x*(a+b*x^4)^(7/4)+21/64*a^2*ArcTan[b^(1/4)*x/(a+b*x^4)^(1/4)]/b^(1/4)+21/64*a^2*ArcTanh[b^(1/4)*x/(a+b*x^4)^(1/4)]/b^(1/4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1236
  public void test0541() {
    check( //
        "Integrate[x/(a+b*x^4)^(3/4), x]", //
        "(1+b*x^4/a)^(3/4)*EllipticF[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a+b*x^4)^(3/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1250
  public void test0542() {
    check( //
        "Integrate[1/(a+b*x^4)^(3/4), x]", //
        "-(1+a/(b*x^4))^(3/4)*x^3*EllipticF[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[b]/((a+b*x^4)^(3/4)*Sqrt[a])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1264
  public void test0543() {
    check( //
        "Integrate[x^5/(a+b*x^4)^(5/4), x]", //
        "x^2/(b*(a+b*x^4)^(1/4))-2*(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/(b^(3/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1278
  public void test0544() {
    check( //
        "Integrate[x^10/(a+b*x^4)^(5/4), x]", //
        "-7/12*a*x^3/(b^2*(a+b*x^4)^(1/4))+1/6*x^7/(b*(a+b*x^4)^(1/4))-7/4*a^(3/2)*(1+a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]/(b^(5/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1310
  public void test0545() {
    check( //
        "Integrate[(a-b*x^4)^(1/4)/x^2, x]", //
        "-(a-b*x^4)^(1/4)/x+1/2*b^(1/4)*ArcTan[1-b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)]/Sqrt[2]-1/2*b^(1/4)*ArcTan[1+b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)]/Sqrt[2]-1/4*b^(1/4)*Log[1-b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)+x^2*Sqrt[b]/Sqrt[a-b*x^4]]/Sqrt[2]+1/4*b^(1/4)*Log[1+b^(1/4)*x*Sqrt[2]/(a-b*x^4)^(1/4)+x^2*Sqrt[b]/Sqrt[a-b*x^4]]/Sqrt[2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1422
  public void test0546() {
    check( //
        "Integrate[x^9/(3+b*x^5), x]", //
        "1/5*x^5/b-3/5*Log[3+b*x^5]/b^2");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:30
  public void test0547() {
    check( //
        "Integrate[1/x^100, x]", //
        "(-1/99)/x^99");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:48
  public void test0548() {
    check( //
        "Integrate[1/x^(5/3), x]", //
        "(-3/2)/x^(2/3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:146
  public void test0549() {
    check( //
        "Integrate[(a+b*x)^7/x^11, x]", //
        "-1/10*(a+b*x)^8/(a*x^10)+1/45*b*(a+b*x)^8/(a^2*x^9)-1/360*b^2*(a+b*x)^8/(a^3*x^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:225
  public void test0550() {
    check( //
        "Integrate[1/(a+b*x)^3, x]", //
        "(-1/2)/(b*(a+b*x)^2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:239
  public void test0551() {
    check( //
        "Integrate[1/(a+b*x)^4, x]", //
        "(-1/3)/(b*(a+b*x)^3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:420
  public void test0552() {
    check( //
        "Integrate[x^(1/2*(1-n)+1/2*(-3+n))/Sqrt[a+b*x], x]", //
        "-2*ArcTanh[Sqrt[a+b*x]/Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:562
  public void test0553() {
    check( //
        "Integrate[Sqrt[a+b*x]/x^(9/2), x]", //
        "-2/7*(a+b*x)^(3/2)/(a*x^(7/2))+8/35*b*(a+b*x)^(3/2)/(a^2*x^(5/2))-16/105*b^2*(a+b*x)^(3/2)/(a^3*x^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:648
  public void test0554() {
    check( //
        "Integrate[1/((a+b*x)^(3/2)*Sqrt[x]), x]", //
        "2*Sqrt[x]/(a*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:690
  public void test0555() {
    check( //
        "Integrate[1/(x^(7/2)*(2+b*x)^(3/2)), x]", //
        "1/(x^(5/2)*Sqrt[2+b*x])-3/5*Sqrt[2+b*x]/x^(5/2)+2/5*b*Sqrt[2+b*x]/x^(3/2)-2/5*b^2*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:850
  public void test0556() {
    check( //
        "Integrate[(a+b*x)*Sqrt[c*x^2]/x^3, x]", //
        "-a*Sqrt[c*x^2]/x^2+b*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:864
  public void test0557() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)/x, x]", //
        "1/5*a*c^2*x^4*Sqrt[c*x^2]+1/6*b*c^2*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:880
  public void test0558() {
    check( //
        "Integrate[x*(a+b*x)/(c*x^2)^(3/2), x]", //
        "-a/(c*Sqrt[c*x^2])+b*x*Log[x]/(c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:898
  public void test0559() {
    check( //
        "Integrate[x^3*(a+b*x)^2*Sqrt[c*x^2], x]", //
        "1/5*a^2*x^4*Sqrt[c*x^2]+1/3*a*b*x^5*Sqrt[c*x^2]+1/7*b^2*x^6*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:912
  public void test0560() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^2/x^3, x]", //
        "1/3*c*(a+b*x)^3*Sqrt[c*x^2]/(b*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:928
  public void test0561() {
    check( //
        "Integrate[(a+b*x)^2/(x*Sqrt[c*x^2]), x]", //
        "-a^2/Sqrt[c*x^2]+b^2*x^2/Sqrt[c*x^2]+2*a*b*x*Log[x]/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:942
  public void test0562() {
    check( //
        "Integrate[x*(a+b*x)^2/(c*x^2)^(5/2), x]", //
        "-1/3*(a+b*x)^3/(a*c^2*x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1024
  public void test0563() {
    check( //
        "Integrate[1/(x^2*(a+b*x)^2*Sqrt[c*x^2]), x]", //
        "2*b/(a^3*Sqrt[c*x^2])+(-1/2)/(a^2*x*Sqrt[c*x^2])+b^2*x/(a^3*(a+b*x)*Sqrt[c*x^2])+3*b^2*x*Log[x]/(a^4*Sqrt[c*x^2])-3*b^2*x*Log[a+b*x]/(a^4*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1045
  public void test0564() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)^n/x^2, x]", //
        "-a*c*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^2*(1+n)*x)+c*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^2*(2+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1095
  public void test0565() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(1/2)*(a+b*x)^2, x]", //
        "a^2*(d*x)^(2+m)*Sqrt[c*x^2]/(d^2*(2+m)*x)+2*a*b*(d*x)^(3+m)*Sqrt[c*x^2]/(d^3*(3+m)*x)+b^2*(d*x)^(4+m)*Sqrt[c*x^2]/(d^4*(4+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1135
  public void test0566() {
    check( //
        "Integrate[(a+b*x)^5/(a*d/b+d*x)^3, x]", //
        "1/3*b^2*(a+b*x)^3/d^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1149
  public void test0567() {
    check( //
        "Integrate[1/((b*c/d+b*x)^2*(c+d*x)^3), x]", //
        "-1/4*d/(b^2*(c+d*x)^4)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1265
  public void test0568() {
    check( //
        "Integrate[1/((1-x)^(3/2)*(1+x)^(1/2)), x]", //
        "Sqrt[1+x]/Sqrt[1-x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1295
  public void test0569() {
    check( //
        "Integrate[(a+a*x)^(1/2)*(c-c*x)^(1/2), x]", //
        "ArcTan[Sqrt[c]*Sqrt[a+a*x]/(Sqrt[a]*Sqrt[c-c*x])]*Sqrt[a]*Sqrt[c]+1/2*x*Sqrt[a+a*x]*Sqrt[c-c*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1373
  public void test0570() {
    check( //
        "Integrate[1/((a-I*a*x)^(5/4)*(a+I*a*x)^(5/4)), x]", //
        "2*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a^2*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1498
  public void test0571() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^15, x]", //
        "-1/14*(c+d*x)^11/((b*c-a*d)*(a+b*x)^14)+3/182*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^13)-1/364*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^12)+1/4004*d^3*(c+d*x)^11/((b*c-a*d)^4*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1703
  public void test0572() {
    check( //
        "Integrate[(c+d*x)^(1/2)/(a+b*x)^(5/2), x]", //
        "-2/3*(c+d*x)^(3/2)/((b*c-a*d)*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1717
  public void test0573() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(a+b*x)^(13/2), x]", //
        "-2/11*(c+d*x)^(5/2)/((b*c-a*d)*(a+b*x)^(11/2))+4/33*d*(c+d*x)^(5/2)/((b*c-a*d)^2*(a+b*x)^(9/2))-16/231*d^2*(c+d*x)^(5/2)/((b*c-a*d)^3*(a+b*x)^(7/2))+32/1155*d^3*(c+d*x)^(5/2)/((b*c-a*d)^4*(a+b*x)^(5/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1747
  public void test0574() {
    check( //
        "Integrate[1/((a+b*x)^(5/2)*(c+d*x)^(3/2)), x]", //
        "(-2/3)/((b*c-a*d)*(a+b*x)^(3/2)*Sqrt[c+d*x])+8/3*d/((b*c-a*d)^2*Sqrt[a+b*x]*Sqrt[c+d*x])+16/3*d^2*Sqrt[a+b*x]/((b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1775
  public void test0575() {
    check( //
        "Integrate[1/(Sqrt[3-b*x]*Sqrt[2+b*x]), x]", //
        "-ArcSin[1/5*(1-2*b*x)]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1843
  public void test0576() {
    check( //
        "Integrate[1/((a+b*x)^(11/3)*(c+d*x)^(1/3)), x]", //
        "-3/8*(c+d*x)^(2/3)/((b*c-a*d)*(a+b*x)^(8/3))+9/20*d*(c+d*x)^(2/3)/((b*c-a*d)^2*(a+b*x)^(5/3))-27/40*d^2*(c+d*x)^(2/3)/((b*c-a*d)^3*(a+b*x)^(2/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1857
  public void test0577() {
    check( //
        "Integrate[1/((a+b*x)^(10/3)*(c+d*x)^(2/3)), x]", //
        "-3/7*(c+d*x)^(1/3)/((b*c-a*d)*(a+b*x)^(7/3))+9/14*d*(c+d*x)^(1/3)/((b*c-a*d)^2*(a+b*x)^(4/3))-27/14*d^2*(c+d*x)^(1/3)/((b*c-a*d)^3*(a+b*x)^(1/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1871
  public void test0578() {
    check( //
        "Integrate[1/((a+b*x)^(8/3)*(c+d*x)^(4/3)), x]", //
        "(-3/5)/((b*c-a*d)*(a+b*x)^(5/3)*(c+d*x)^(1/3))+9/5*d/((b*c-a*d)^2*(a+b*x)^(2/3)*(c+d*x)^(1/3))+27/5*d^2*(a+b*x)^(1/3)/((b*c-a*d)^3*(c+d*x)^(1/3))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2060
  public void test0579() {
    check( //
        "Integrate[(a+b*x)^(5/6)/(c+d*x)^(35/6), x]", //
        "6/29*(a+b*x)^(11/6)/((b*c-a*d)*(c+d*x)^(29/6))+108/667*b*(a+b*x)^(11/6)/((b*c-a*d)^2*(c+d*x)^(23/6))+1296/11339*b^2*(a+b*x)^(11/6)/((b*c-a*d)^3*(c+d*x)^(17/6))+7776/124729*b^3*(a+b*x)^(11/6)/((b*c-a*d)^4*(c+d*x)^(11/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2088
  public void test0580() {
    check( //
        "Integrate[1/((a+b*x)^(1/6)*(c+d*x)^(29/6)), x]", //
        "6/23*(a+b*x)^(5/6)/((b*c-a*d)*(c+d*x)^(23/6))+108/391*b*(a+b*x)^(5/6)/((b*c-a*d)^2*(c+d*x)^(17/6))+1296/4301*b^2*(a+b*x)^(5/6)/((b*c-a*d)^3*(c+d*x)^(11/6))+7776/21505*b^3*(a+b*x)^(5/6)/((b*c-a*d)^4*(c+d*x)^(5/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2114
  public void test0581() {
    check( //
        "Integrate[1/((a+b*x)^(7/6)*(c+d*x)^(23/6)), x]", //
        "(-6)/((b*c-a*d)*(a+b*x)^(1/6)*(c+d*x)^(17/6))-108/17*d*(a+b*x)^(5/6)/((b*c-a*d)^2*(c+d*x)^(17/6))-1296/187*b*d*(a+b*x)^(5/6)/((b*c-a*d)^3*(c+d*x)^(11/6))-7776/935*b^2*d*(a+b*x)^(5/6)/((b*c-a*d)^4*(c+d*x)^(5/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2149
  public void test0582() {
    check( //
        "Integrate[(a+b*x)^(-4-n)*(c+d*x)^n, x]", //
        "-(a+b*x)^(-3-n)*(c+d*x)^(1+n)/((b*c-a*d)*(3+n))+2*d*(a+b*x)^(-2-n)*(c+d*x)^(1+n)/((b*c-a*d)^2*(2+n)*(3+n))-2*d^2*(a+b*x)^(-1-n)*(c+d*x)^(1+n)/((b*c-a*d)^3*(1+n)*(2+n)*(3+n))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2180
  public void test0583() {
    check( //
        "Integrate[4*x+Pi*x^3, x]", //
        "2*x^2+1/4*Pi*x^4");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2196
  public void test0584() {
    check( //
        "Integrate[1/x+2*x+x^2, x]", //
        "x^2+1/3*x^3+Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:19
  public void test0585() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^2, x]", //
        "-a^4*c^3/x+a*b^3*c^3*x^2-1/3*b^4*c^3*x^3-2*a^3*b*c^3*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:33
  public void test0586() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^3, x]", //
        "-1/2*a^5*c^4/x^2+3*a^4*b*c^4/x+2*a^2*b^3*c^4*x-3/2*a*b^4*c^4*x^2+1/3*b^5*c^4*x^3+2*a^3*b^2*c^4*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:47
  public void test0587() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^3, x]", //
        "-1/2*a^6*c^5/x^2+4*a^5*b*c^5/x-5/2*a^2*b^4*c^5*x^2+4/3*a*b^5*c^5*x^3-1/4*b^6*c^5*x^4+5*a^4*b^2*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:61
  public void test0588() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^6/x^12, x]", //
        "-1/11*a^7*c^6/x^11+1/2*a^6*b*c^6/x^10-a^5*b^2*c^6/x^9+5/8*a^4*b^3*c^6/x^8+5/7*a^3*b^4*c^6/x^7-3/2*a^2*b^5*c^6/x^6+a*b^6*c^6/x^5-1/4*b^7*c^6/x^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:119
  public void test0589() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^6, x]", //
        "-1/5*a*A/x^5+1/4*(-A*b-a*B)/x^4-1/3*b*B/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:147
  public void test0590() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^10, x]", //
        "-1/9*a^3*A/x^9-1/8*a^2*(3*A*b+a*B)/x^8-3/7*a*b*(A*b+a*B)/x^7-1/6*b^2*(A*b+3*a*B)/x^6-1/5*b^3*B/x^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:161
  public void test0591() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^8, x]", //
        "-1/7*A*(a+b*x)^6/(a*x^7)+1/42*(A*b-7*a*B)*(a+b*x)^6/(a^2*x^6)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:189
  public void test0592() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^13, x]", //
        "-1/12*A*(a+b*x)^11/(a*x^12)+1/132*(A*b-12*a*B)*(a+b*x)^11/(a^2*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:447
  public void test0593() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^(7/2), x]", //
        "-2/5*a^3*A/x^(5/2)-2/3*a^2*(3*A*b+a*B)/x^(3/2)+2/3*b^3*B*x^(3/2)-6*a*b*(A*b+a*B)/Sqrt[x]+2*b^2*(A*b+3*a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:650
  public void test0594() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/x^(11/2), x]", //
        "-2/9*A*(a+b*x)^(7/2)/(a*x^(9/2))+2/63*(2*A*b-9*a*B)*(a+b*x)^(7/2)/(a^2*x^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:666
  public void test0595() {
    check( //
        "Integrate[(A+B*x)/(x^(11/2)*Sqrt[a+b*x]), x]", //
        "-2/9*A*Sqrt[a+b*x]/(a*x^(9/2))+2/63*(8*A*b-9*a*B)*Sqrt[a+b*x]/(a^2*x^(7/2))-4/105*b*(8*A*b-9*a*B)*Sqrt[a+b*x]/(a^3*x^(5/2))+16/315*b^2*(8*A*b-9*a*B)*Sqrt[a+b*x]/(a^4*x^(3/2))-32/315*b^3*(8*A*b-9*a*B)*Sqrt[a+b*x]/(a^5*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:700
  public void test0596() {
    check( //
        "Integrate[Sqrt[a+b*x]*Sqrt[c+d*x]/x, x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]*Sqrt[c]+(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])+Sqrt[a+b*x]*Sqrt[c+d*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:714
  public void test0597() {
    check( //
        "Integrate[(c+d*x)^(3/2)*Sqrt[a+b*x]/x^6, x]", //
        "-1/128*(b*c-a*d)^3*(7*b^2*c^2+6*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(7/2))-1/5*(c+d*x)^(3/2)*Sqrt[a+b*x]/x^5-1/40*(b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x^4)+1/240*(7*b^2*c/a-12*b*d-3*a*d^2/c)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x^3)-1/960*(35*b^3*c^3-61*a*b^2*c^2*d+9*a^2*b*c*d^2-15*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^2*x^2)+1/1920*(105*b^4*c^4-190*a*b^3*c^3*d+36*a^2*b^2*c^2*d^2+30*a^3*b*c*d^3-45*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:776
  public void test0598() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^3, x]", //
        "-1/2*(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^2-3/4*(b^2*c^2+10*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/Sqrt[a]+3/4*(5*b^2*c^2+10*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]/Sqrt[b]+1/4*d*(7*b*c+5*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/c-1/4*(3*b*c+5*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c*x)+3*d*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:824
  public void test0599() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^5, x]", //
        "-1/24*(5*b*c+3*a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(c*x^3)-1/4*(a+b*x)^(5/2)*(c+d*x)^(3/2)/x^4+1/64*(5*b^4*c^4-60*a*b^3*c^3*d-90*a^2*b^2*c^2*d^2+20*a^3*b*c*d^3-3*a^4*d^4)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(5/2))+2*b^(5/2)*d^(3/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]-1/32*(5*b*c-a*d)*(b*c+3*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(c^2*x^2)-1/64*(5*b^3*c^3+73*a*b^2*c^2*d-17*a^2*b*c*d^2+3*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:840
  public void test0600() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x*Sqrt[c+d*x]), x]", //
        "1/4*(3*b^2*c^2-10*a*b*c*d+15*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]/d^(5/2)-2*a^(5/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[c]+1/2*b*(a+b*x)^(3/2)*Sqrt[c+d*x]/d-1/4*b*(3*b*c-7*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/d^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:872
  public void test0601() {
    check( //
        "Integrate[Sqrt[c+d*x]/(x^4*Sqrt[a+b*x]), x]", //
        "1/8*(b*c-a*d)*(5*b^2*c^2+2*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(5/2))-1/3*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x^3)+1/12*(5*b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c*x^2)-1/24*(5*b*c-3*a*d)*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:886
  public void test0602() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^3*Sqrt[a+b*x]), x]", //
        "2*d^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[b]-1/4*(3*b^2*c^2-10*a*b*c*d+15*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/a^(5/2)-1/2*c*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x^2)+1/4*c*(3*b*c-7*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:902
  public void test0603() {
    check( //
        "Integrate[x^3/(Sqrt[a+b*x]*Sqrt[c+d*x]), x]", //
        "-1/8*(b*c+a*d)*(5*b^2*c^2-2*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(7/2)*d^(7/2))+1/3*x^2*Sqrt[a+b*x]*Sqrt[c+d*x]/(b*d)+1/24*(15*b^2*c^2+14*a*b*c*d+15*a^2*d^2-10*b*d*(b*c+a*d)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^3*d^3)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:916
  public void test0604() {
    check( //
        "Integrate[1/(x^3*(c+d*x)^(3/2)*Sqrt[a+b*x]), x]", //
        "-3/4*(b^2*c^2+2*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(7/2))+1/4*d*(3*b*c-5*a*d)*(b*c+3*a*d)*Sqrt[a+b*x]/(a^2*c^3*(b*c-a*d)*Sqrt[c+d*x])-1/2*Sqrt[a+b*x]/(a*c*x^2*Sqrt[c+d*x])+1/4*(3*b*c+5*a*d)*Sqrt[a+b*x]/(a^2*c^2*x*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:948
  public void test0605() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^5*(a+b*x)^(3/2)), x]", //
        "-5/64*(b*c-a*d)^2*(63*b^2*c^2-14*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(11/2)*c^(3/2))-1/4*c*(c+d*x)^(3/2)/(a*x^4*Sqrt[a+b*x])+1/192*b*(945*b^3*c^3-1785*a*b^2*c^2*d+839*a^2*b*c*d^2-15*a^3*d^3)*Sqrt[c+d*x]/(a^5*c*Sqrt[a+b*x])+1/24*c*(9*b*c-11*a*d)*Sqrt[c+d*x]/(a^2*x^3*Sqrt[a+b*x])-1/96*(63*b*c-59*a*d)*(b*c-a*d)*Sqrt[c+d*x]/(a^3*x^2*Sqrt[a+b*x])+1/192*(b*c-a*d)*(315*b^2*c^2-322*a*b*c*d+15*a^2*d^2)*Sqrt[c+d*x]/(a^4*c*x*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:984
  public void test0606() {
    check( //
        "Integrate[x^2/((a+b*x)^(5/2)*(c+d*x)^(1/2)), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*Sqrt[d])-2/3*a^2*Sqrt[c+d*x]/(b^2*(b*c-a*d)*(a+b*x)^(3/2))+4/3*a*(3*b*c-2*a*d)*Sqrt[c+d*x]/(b^2*(b*c-a*d)^2*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1088
  public void test0607() {
    check( //
        "Integrate[(a+b*x)^(1/4)/(x^3*(c+d*x)^(1/4)), x]", //
        "1/8*(3*b*c+5*a*d)*(a+b*x)^(1/4)*(c+d*x)^(3/4)/(a*c^2*x)-1/2*(a+b*x)^(5/4)*(c+d*x)^(3/4)/(a*c*x^2)+1/16*(b*c-a*d)*(3*b*c+5*a*d)*ArcTan[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(7/4)*c^(9/4))+1/16*(b*c-a*d)*(3*b*c+5*a*d)*ArcTanh[c^(1/4)*(a+b*x)^(1/4)/(a^(1/4)*(c+d*x)^(1/4))]/(a^(7/4)*c^(9/4))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1353
  public void test0608() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^5/(a+b*x)^2, x]", //
        "5*e*(b*d-a*e)^3*(b*B*d+2*A*b*e-3*a*B*e)*x/b^6-(A*b-a*B)*(b*d-a*e)^5/(b^7*(a+b*x))+5*e^2*(b*d-a*e)^2*(b*B*d+A*b*e-2*a*B*e)*(a+b*x)^2/b^7+5/3*e^3*(b*d-a*e)*(2*b*B*d+A*b*e-3*a*B*e)*(a+b*x)^3/b^7+1/4*e^4*(5*b*B*d+A*b*e-6*a*B*e)*(a+b*x)^4/b^7+1/5*B*e^5*(a+b*x)^5/b^7+(b*d-a*e)^4*(b*B*d+5*A*b*e-6*a*B*e)*Log[a+b*x]/b^7");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1367
  public void test0609() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^2/(a+b*x)^3, x]", //
        "B*e^2*x/b^3-1/2*(A*b-a*B)*(b*d-a*e)^2/(b^4*(a+b*x)^2)-(b*d-a*e)*(b*B*d+2*A*b*e-3*a*B*e)/(b^4*(a+b*x))+e*(2*b*B*d+A*b*e-3*a*B*e)*Log[a+b*x]/b^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2078
  public void test0610() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)*Sqrt[1-2*x], x]", //
        "-77/12*(1-2*x)^(3/2)+17/5*(1-2*x)^(5/2)-15/28*(1-2*x)^(7/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2230
  public void test0611() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)/(2+3*x), x]", //
        "-14/81*(1-2*x)^(3/2)-2/45*(1-2*x)^(5/2)-5/21*(1-2*x)^(7/2)+98/81*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-98/81*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2306
  public void test0612() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)/Sqrt[1-2*x], x]", //
        "17/3*(1-2*x)^(3/2)-3/4*(1-2*x)^(5/2)-77/4*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2382
  public void test0613() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)/(1-2*x)^(3/2), x]", //
        "-5/4*(1-2*x)^(3/2)+77/4/Sqrt[1-2*x]+17*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2566
  public void test0614() {
    check( //
        "Integrate[(A+B*x)/((d+e*x)^(9/2)*Sqrt[a+b*x]), x]", //
        "-2/7*(B*d-A*e)*Sqrt[a+b*x]/(e*(b*d-a*e)*(d+e*x)^(7/2))+2/35*(b*B*d+6*A*b*e-7*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^2*(d+e*x)^(5/2))+8/105*b*(b*B*d+6*A*b*e-7*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^3*(d+e*x)^(3/2))+16/105*b^2*(b*B*d+6*A*b*e-7*a*B*e)*Sqrt[a+b*x]/(e*(b*d-a*e)^4*Sqrt[d+e*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2598
  public void test0615() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5, x]", //
        "-794365/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/12*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+37/504*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+6005/14112*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+625115/197568*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2612
  public void test0616() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^7, x]", //
        "-64645339/1229312*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/18*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^6-107/3780*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+4619/211680*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+42461/423360*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+1460201/2370816*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+152571047/33191424*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2674
  public void test0617() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x), x]", //
        "1/9*(1-2*x)^(3/2)*(3+5*x)^(3/2)-14/81*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+19573/6480*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+37/180*(3+5*x)^(3/2)*Sqrt[1-2*x]-1781/2160*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2688
  public void test0618() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^3, x]", //
        "-1/6*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^2+1945/324*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+6829/324*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-35/4*(3+5*x)^(3/2)*Sqrt[1-2*x]+181/36*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)+185/27*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2704
  public void test0619() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^5*Sqrt[3+5*x]), x]", //
        "-5274027/3136*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/12*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+227/72*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+39667/2016*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+4148797/28224*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2736
  public void test0620() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^6, x]", //
        "3/35*(1-2*x)^(7/2)*(3+5*x)^(3/2)/(2+3*x)^5+251/280*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^4+2761/336*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-3674891/6272*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+30371/448*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-334081/6272*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2794
  public void test0621() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^2*(3+5*x)^(5/2)), x]", //
        "-55/3*(1-2*x)^(3/2)/(3+5*x)^(3/2)+(1-2*x)^(5/2)/((2+3*x)*(3+5*x)^(3/2))-385*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+385*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2812
  public void test0622() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^5*Sqrt[1-2*x]), x]", //
        "-375265/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/28*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+1/56*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+305/1568*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+32735/21952*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2842
  public void test0623() {
    check( //
        "Integrate[1/((2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-37/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+3/7*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2856
  public void test0624() {
    check( //
        "Integrate[(2+3*x)^3/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "81/50*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/165*(2+3*x)^2*Sqrt[1-2*x]/(3+5*x)^(3/2)-1/18150*(5831+9405*x)*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2888
  public void test0625() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^4), x]", //
        "-2805/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/7*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-2/3*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3-145/588*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2-415/8232*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2904
  public void test0626() {
    check( //
        "Integrate[(2+3*x)^4/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-184641/640*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^3*Sqrt[3+5*x]/Sqrt[1-2*x]+243/220*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+9/7040*(27269+11316*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2932
  public void test0627() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^3*(3+5*x)^(5/2)), x]", //
        "-1215945/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-8515/7546)/((3+5*x)^(3/2)*Sqrt[1-2*x])+3/14/((2+3*x)^2*(3+5*x)^(3/2)*Sqrt[1-2*x])+765/196/((2+3*x)*(3+5*x)^(3/2)*Sqrt[1-2*x])-7090175/498036*Sqrt[1-2*x]/(3+5*x)^(3/2)+707286025/5478396*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2964
  public void test0628() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^5), x]", //
        "11/21*(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^4)-547745/1075648*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+139745/1613472*Sqrt[3+5*x]/Sqrt[1-2*x]+43/588*Sqrt[3+5*x]/((2+3*x)^4*Sqrt[1-2*x])-2717/8232*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-2013/10976*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])-14135/153664*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2980
  public void test0629() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "49/66/((1-2*x)^(3/2)*Sqrt[3+5*x])+(-1237/3630)/(Sqrt[1-2*x]*Sqrt[3+5*x])-793/19965*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2994
  public void test0630() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^2*(3+5*x)^(5/2)), x]", //
        "(-190/1617)/((1-2*x)^(3/2)*(3+5*x)^(3/2))+3/7/((1-2*x)^(3/2)*(2+3*x)*(3+5*x)^(3/2))-14985/343*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-1090/41503)/((3+5*x)^(3/2)*Sqrt[1-2*x])-985525/1369599*Sqrt[1-2*x]/(3+5*x)^(3/2)+95783075/15065589*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3016
  public void test0631() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2), x]", //
        "4/3*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3030
  public void test0632() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x], x]", //
        "-30926081/94500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-465127/47250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/55*(2+3*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]-7031/11550*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-177/1925*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-3/275*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-465127/103950*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3046
  public void test0633() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x], x]", //
        "-31/75*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4/75*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/15*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3060
  public void test0634() {
    check( //
        "Integrate[(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "-523/15625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-47342/15625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/15*(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-458/825*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+2818/6875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+2719/34375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3078
  public void test0635() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(7/2), x]", //
        "-3896/945*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-164/945*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/15*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(5/2)+82/135*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+3896/945*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3092
  public void test0636() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(5/2), x]", //
        "2/65*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(7/2)-9380126059/55282500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-70536439/13820625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+62/3575*(2+3*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]-2133359/6756750*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-160084/3378375*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-67/160875*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-70536439/30405375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3108
  public void test0637() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-74/15*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+4/15*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/3*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3122
  public void test0638() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(3/2)*(2+3*x)^(5/2)/(3+5*x)^(3/2)-8366/15625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+1973/15625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-106/25*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+1558/625*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+2264/3125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3140
  public void test0639() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(9/2), x]", //
        "-36052/1323*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1048/1323*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/21*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^(7/2)+2/7*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(5/2)+524/189*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+36052/1323*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3154
  public void test0640() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(5/2), x]", //
        "106/4875*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(7/2)+2/75*(1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(7/2)-1580201444291/12438562500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-23763809947/6219281250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+8038/804375*(2+3*x)^(3/2)*(3+5*x)^(7/2)*Sqrt[1-2*x]-359748241/1520268750*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-26534891/760134375*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+364267/36196875*(3+5*x)^(7/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-23763809947/13682418750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3170
  public void test0641() {
    check( //
        "Integrate[(1-2*x)^(5/2)/(Sqrt[2+3*x]*Sqrt[3+5*x]), x]", //
        "53194/16875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-34154/16875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4/75*(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]-1088/3375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3184
  public void test0642() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(7/2)*(3+5*x)^(3/2)), x]", //
        "105584/45*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+3176/45*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/15*(1-2*x)^(3/2)/((2+3*x)^(5/2)*Sqrt[3+5*x])+2716/135*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+17468/45*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-105584/27*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3202
  public void test0643() {
    check( //
        "Integrate[Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x], x]", //
        "-34/15*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/15*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/3*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3216
  public void test0644() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(5/2)/Sqrt[1-2*x], x]", //
        "-725140729/141750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-43624697/283500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-34/99*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-1/11*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-329683/34650*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1053/770*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-43624697/623700*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3248
  public void test0645() {
    check( //
        "Integrate[(2+3*x)^(3/2)/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-37/25*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-13/25*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1/5*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3262
  public void test0646() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-6515539/343750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-104663/171875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/165*(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-668/9075*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+403/75625*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-87476/378125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3282
  public void test0647() {
    check( //
        "Integrate[1/(Sqrt[(6-x)*(-2+x)]*Sqrt[-1+x]), x]", //
        "-2*EllipticF[ArcSin[1/2*Sqrt[6-x]],4/5]/Sqrt[5]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3300
  public void test0648() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "34/7*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+1/7*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3314
  public void test0649() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^(11/2)), x]", //
        "-6036028/22235661*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1199452/22235661*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*(3+5*x)^(3/2)/((2+3*x)^(9/2)*Sqrt[1-2*x])+295/1323*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(9/2)-67345/64827*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-167228/453789*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-392998/3176523*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+6036028/22235661*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3330
  public void test0650() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "74/77*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-4/77*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])-370/847*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3348
  public void test0651() {
    check( //
        "Integrate[(2+3*x)^(9/2)*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "-112543103/8750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-6770629/17500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*(2+3*x)^(9/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-166/33*(2+3*x)^(7/2)*Sqrt[3+5*x]/Sqrt[1-2*x]-139163/3850*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-1327/154*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-6478333/38500*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3362
  public void test0652() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^(3/2)), x]", //
        "19/343*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+106/343*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+11/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*Sqrt[2+3*x])-8/147*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])-19/343*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3378
  public void test0653() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-4451/110*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-67/55*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(3/2)*Sqrt[3+5*x]/(1-2*x)^(3/2)-448/363*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3392
  public void test0654() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "475592/41503*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+10628/41503*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231/((1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x])+1088/17787/(Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x])+5314/41503*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-2377960/1369599*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:30
  public void test0655() {
    check( //
        "Integrate[(e+f*x)*Sqrt[a+b*x]/(x*(c+d*x)), x]", //
        "-2*e*ArcTanh[Sqrt[a+b*x]/Sqrt[a]]*Sqrt[a]/c+2*(d*e-c*f)*ArcTan[Sqrt[d]*Sqrt[a+b*x]/Sqrt[b*c-a*d]]*Sqrt[b*c-a*d]/(c*d^(3/2))+2*f*Sqrt[a+b*x]/d");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:91
  public void test0656() {
    check( //
        "Integrate[(7+5*x)^4/(Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "392989907/2016*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[5-2*x]/(Sqrt[66]*Sqrt[-5+2*x])-5109835/756*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-120355/288*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-305/24*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]-25/84*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:153
  public void test0657() {
    check( //
        "Integrate[1/((7+5*x)^(5/2)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "-50/83421*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)-895300/2319687747*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]+358120/2319687747*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]+103964/1918683*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[7+5*x]/(Sqrt[253]*Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])-179060/59479173*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:24
  public void test0658() {
    check( //
        "Integrate[x^5*(a+b*x^2)^2, x]", //
        "1/6*a^2*x^6+1/4*a*b*x^8+1/10*b^2*x^10");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:38
  public void test0659() {
    check( //
        "Integrate[(a+b*x^2)^2/x^9, x]", //
        "-1/8*a^2/x^8-1/3*a*b/x^6-1/4*b^2/x^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:52
  public void test0660() {
    check( //
        "Integrate[(a+b*x^2)^3/x^15, x]", //
        "-1/14*a^3/x^14-1/4*a^2*b/x^12-3/10*a*b^2/x^10-1/8*b^3/x^8");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:66
  public void test0661() {
    check( //
        "Integrate[x^7*(a+b*x^2)^5, x]", //
        "-1/12*a^3*(a+b*x^2)^6/b^4+3/14*a^2*(a+b*x^2)^7/b^4-3/16*a*(a+b*x^2)^8/b^4+1/18*(a+b*x^2)^9/b^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:80
  public void test0662() {
    check( //
        "Integrate[(a+b*x^2)^5/x^21, x]", //
        "-1/20*a^5/x^20-5/18*a^4*b/x^18-5/8*a^3*b^2/x^16-5/7*a^2*b^3/x^14-5/12*a*b^4/x^12-1/10*b^5/x^10");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:108
  public void test0663() {
    check( //
        "Integrate[(a+b*x^2)^8/x^11, x]", //
        "-1/10*a^8/x^10-a^7*b/x^8-14/3*a^6*b^2/x^6-14*a^5*b^3/x^4-35*a^4*b^4/x^2+14*a^2*b^6*x^2+2*a*b^7*x^4+1/6*b^8*x^6+56*a^3*b^5*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:138
  public void test0664() {
    check( //
        "Integrate[x^10/(a+b*x^2), x]", //
        "a^4*x/b^5-1/3*a^3*x^3/b^4+1/5*a^2*x^5/b^3-1/7*a*x^7/b^2+1/9*x^9/b-a^(9/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(11/2)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:152
  public void test0665() {
    check( //
        "Integrate[1/(x^4*(a+b*x^2)), x]", //
        "(-1/3)/(a*x^3)+b/(a^2*x)+b^(3/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/a^(5/2)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:166
  public void test0666() {
    check( //
        "Integrate[x^5/(a+b*x^2)^2, x]", //
        "1/2*x^2/b^2-1/2*a^2/(b^3*(a+b*x^2))-a*Log[a+b*x^2]/b^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:180
  public void test0667() {
    check( //
        "Integrate[1/(x^9*(a+b*x^2)^2), x]", //
        "(-1/8)/(a^2*x^8)+1/3*b/(a^3*x^6)-3/4*b^2/(a^4*x^4)+2*b^3/(a^5*x^2)+1/2*b^4/(a^5*(a+b*x^2))+5*b^4*Log[x]/a^6-5/2*b^4*Log[a+b*x^2]/a^6");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:208
  public void test0668() {
    check( //
        "Integrate[x^19/(a+b*x^2)^10, x]", //
        "1/18*a^9/(b^10*(a+b*x^2)^9)-9/16*a^8/(b^10*(a+b*x^2)^8)+18/7*a^7/(b^10*(a+b*x^2)^7)-7*a^6/(b^10*(a+b*x^2)^6)+63/5*a^5/(b^10*(a+b*x^2)^5)-63/4*a^4/(b^10*(a+b*x^2)^4)+14*a^3/(b^10*(a+b*x^2)^3)-9*a^2/(b^10*(a+b*x^2)^2)+9/2*a/(b^10*(a+b*x^2))+1/2*Log[a+b*x^2]/b^10");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:408
  public void test0669() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/x, x]", //
        "1/3*(a+b*x^2)^(3/2)-a^(3/2)*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]+a*Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:550
  public void test0670() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^(5/2)), x]", //
        "1/3/(a*(a+b*x^2)^(3/2))-ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/a^(5/2)+1/(a^2*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:564
  public void test0671() {
    check( //
        "Integrate[1/(a+b*x^2)^(9/2), x]", //
        "1/7*x/(a*(a+b*x^2)^(7/2))+6/35*x/(a^2*(a+b*x^2)^(5/2))+8/35*x/(a^3*(a+b*x^2)^(3/2))+16/35*x/(a^4*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:682
  public void test0672() {
    check( //
        "Integrate[(c*x)^(1/2)/Sqrt[3*a-2*a*x^2], x]", //
        "-6^(1/4)*EllipticE[ArcSin[Sqrt[3-x*Sqrt[6]]/Sqrt[6]],2]*Sqrt[c*x]*Sqrt[3-2*x^2]/(Sqrt[x]*Sqrt[3*a-2*a*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:710
  public void test0673() {
    check( //
        "Integrate[x^(-1+m)*(a*m+b*(-1+m)*x^2)/(a+b*x^2)^(3/2), x]", //
        "x^m/Sqrt[a+b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:880
  public void test0674() {
    check( //
        "Integrate[(a+b*x^2)^(7/4), x]", //
        "14/15*a^2*x/(a+b*x^2)^(1/4)+14/45*a*x*(a+b*x^2)^(3/4)+2/9*x*(a+b*x^2)^(7/4)-14/15*a^(5/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1070
  public void test0675() {
    check( //
        "Integrate[1/((c*x)^(3/2)*(a+b*x^2)^(5/4)), x]", //
        "(-2)/(a*c*(a+b*x^2)^(1/4)*Sqrt[c*x])+4*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[b]*Sqrt[c*x]/(a^(3/2)*c^2*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1110
  public void test0676() {
    check( //
        "Integrate[1/(a+b*x^2)^(1/6), x]", //
        "3/2*x/(a+b*x^2)^(1/6)+3/2*a*x/((a/(a+b*x^2))^(2/3)*(a+b*x^2)^(7/6)*(1-(a/(a+b*x^2))^(1/3)-Sqrt[3]))-3^(3/4)*a*(1-(a/(a+b*x^2))^(1/3))*EllipticF[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]/(b*x*(a/(a+b*x^2))^(2/3)*(a+b*x^2)^(1/6)*Sqrt[2]*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])+3/4*3^(1/4)*a*(1-(a/(a+b*x^2))^(1/3))*EllipticE[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]*Sqrt[2+Sqrt[3]]/(b*x*(a/(a+b*x^2))^(2/3)*(a+b*x^2)^(1/6)*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1124
  public void test0677() {
    check( //
        "Integrate[1/(a+b*x^2)^(7/6), x]", //
        "-3*x/((a/(a+b*x^2))^(2/3)*(a+b*x^2)^(7/6)*(1-(a/(a+b*x^2))^(1/3)-Sqrt[3]))+3^(3/4)*(1-(a/(a+b*x^2))^(1/3))*EllipticF[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]/(b*x*(a/(a+b*x^2))^(2/3)*(a+b*x^2)^(1/6)*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])-3/2*3^(1/4)*(1-(a/(a+b*x^2))^(1/3))*EllipticE[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]*Sqrt[2+Sqrt[3]]/(b*x*(a/(a+b*x^2))^(2/3)*(a+b*x^2)^(1/6)*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:27
  public void test0678() {
    check( //
        "Integrate[(a+b*x^2)^3/(c+d*x^2)^2, x]", //
        "-b^2*(2*b*c-3*a*d)*x/d^3+1/3*b^3*x^3/d^2-1/2*(b*c-a*d)^3*x/(c*d^3*(c+d*x^2))+1/2*(b*c-a*d)^2*(5*b*c+a*d)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(c^(3/2)*d^(7/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:75
  public void test0679() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/(c+d*x^2)^4, x]", //
        "-1/6*d*x*(a+b*x^2)^(5/2)/(c*(b*c-a*d)*(c+d*x^2)^3)+1/24*(6*b*c-5*a*d)*x*(a+b*x^2)^(3/2)/(c^2*(b*c-a*d)*(c+d*x^2)^2)+1/16*a^2*(6*b*c-5*a*d)*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(7/2)*(b*c-a*d)^(3/2))+1/16*a*(6*b*c-5*a*d)*x*Sqrt[a+b*x^2]/(c^3*(b*c-a*d)*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:120
  public void test0680() {
    check( //
        "Integrate[(c+d*x^2)^(1/2)/(a+b*x^2)^3, x]", //
        "1/4*b*x*(c+d*x^2)^(3/2)/(a*(b*c-a*d)*(a+b*x^2)^2)+1/8*c*(3*b*c-4*a*d)*ArcTan[x*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^2])]/(a^(5/2)*(b*c-a*d)^(3/2))+1/8*(3*b*c-4*a*d)*x*Sqrt[c+d*x^2]/(a^2*(b*c-a*d)*(a+b*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:298
  public void test0681() {
    check( //
        "Integrate[Sqrt[-a-b*x^2]/Sqrt[-c+d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1+b*x^2/a]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:312
  public void test0682() {
    check( //
        "Integrate[Sqrt[-c-d*x^2]/Sqrt[a-b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[-c-d*x^2]/(Sqrt[b]*Sqrt[a-b*x^2]*Sqrt[1+d*x^2/c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:326
  public void test0683() {
    check( //
        "Integrate[Sqrt[-c+d*x^2]/Sqrt[-a-b*x^2], x]", //
        "-EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[-a-b*x^2]*Sqrt[1-d*x^2/c]/(b*Sqrt[1+b*x^2/a]*Sqrt[-c+d*x^2])-(b*c+a*d)*EllipticF[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[1+b*x^2/a]*Sqrt[1-d*x^2/c]/(b*Sqrt[d]*Sqrt[-a-b*x^2]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:36
  public void test0684() {
    check( //
        "Integrate[x^7*(a+b*x^2)^5*(A+B*x^2), x]", //
        "-1/12*a^3*(A*b-a*B)*(a+b*x^2)^6/b^5+1/14*a^2*(3*A*b-4*a*B)*(a+b*x^2)^7/b^5-3/16*a*(A*b-2*a*B)*(a+b*x^2)^8/b^5+1/18*(A*b-4*a*B)*(a+b*x^2)^9/b^5+1/20*B*(a+b*x^2)^10/b^5");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:50
  public void test0685() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^7, x]", //
        "-1/6*a^5*A/x^6-1/4*a^4*(5*A*b+a*B)/x^4-5/2*a^3*b*(2*A*b+a*B)/x^2+5/2*a*b^3*(A*b+2*a*B)*x^2+1/4*b^4*(A*b+5*a*B)*x^4+1/6*b^5*B*x^6+10*a^2*b^2*(A*b+a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:64
  public void test0686() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^21, x]", //
        "-1/20*a^5*A/x^20-1/18*a^4*(5*A*b+a*B)/x^18-5/16*a^3*b*(2*A*b+a*B)/x^16-5/7*a^2*b^2*(A*b+a*B)/x^14-5/12*a*b^3*(A*b+2*a*B)/x^12-1/10*b^4*(A*b+5*a*B)/x^10-1/8*b^5*B/x^8");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:150
  public void test0687() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(a+b*x^2)^3, x]", //
        "1/2*c*x/(a*(a+b*x^2))+1/2*c*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:168
  public void test0688() {
    check( //
        "Integrate[x^3*(a+b*x^2)^2*(c+d*x^2)^2, x]", //
        "1/4*a^2*c^2*x^4+1/3*a*c*(b*c+a*d)*x^6+1/8*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^8+1/5*b*d*(b*c+a*d)*x^10+1/12*b^2*d^2*x^12");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:182
  public void test0689() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^2, x]", //
        "-a^2*c^3/x+a*c^2*(2*b*c+3*a*d)*x+1/3*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^3+1/5*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^5+1/7*b*d^2*(3*b*c+2*a*d)*x^7+1/9*b^2*d^3*x^9");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:232
  public void test0690() {
    check( //
        "Integrate[(c+d*x^2)/(x^4*(a+b*x^2)), x]", //
        "-1/3*c/(a*x^3)+(b*c-a*d)/(a^2*x)+(b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/a^(5/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:246
  public void test0691() {
    check( //
        "Integrate[x^2*(c+d*x^2)^3/(a+b*x^2), x]", //
        "(b*c-a*d)^3*x/b^4+1/3*d*(3*b^2*c^2-3*a*b*c*d+a^2*d^2)*x^3/b^3+1/5*d^2*(3*b*c-a*d)*x^5/b^2+1/7*d^3*x^7/b-(b*c-a*d)^3*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:276
  public void test0692() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)*(c+d*x^2)^2), x]", //
        "(-1/2)/(a*c^2*x^2)+1/2*d^2/(c^2*(b*c-a*d)*(c+d*x^2))-(b*c+2*a*d)*Log[x]/(a^2*c^3)+1/2*b^3*Log[a+b*x^2]/(a^2*(b*c-a*d)^2)-1/2*d^2*(3*b*c-2*a*d)*Log[c+d*x^2]/(c^3*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:412
  public void test0693() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x^2)/x^(5/2), x]", //
        "-2/3*a^3*A/x^(3/2)+6/5*a*b*(A*b+a*B)*x^(5/2)+2/9*b^2*(A*b+3*a*B)*x^(9/2)+2/13*b^3*B*x^(13/2)+2*a^2*(3*A*b+a*B)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:594
  public void test0694() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[a+b*x^2]/x^10, x]", //
        "-1/9*A*(a+b*x^2)^(3/2)/(a*x^9)+1/21*(2*A*b-3*a*B)*(a+b*x^2)^(3/2)/(a^2*x^7)-4/105*b*(2*A*b-3*a*B)*(a+b*x^2)^(3/2)/(a^3*x^5)+8/315*b^2*(2*A*b-3*a*B)*(a+b*x^2)^(3/2)/(a^4*x^3)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:760
  public void test0695() {
    check( //
        "Integrate[x/((a+b*x^2)*Sqrt[d*x^2]), x]", //
        "x*ArcTan[x*Sqrt[b]/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Sqrt[d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1083
  public void test0696() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(x^3*Sqrt[c+d*x^2]), x]", //
        "-1/2*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[a]*Sqrt[c+d*x^2])]/(c^(3/2)*Sqrt[a])-1/2*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(c*x^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1127
  public void test0697() {
    check( //
        "Integrate[x^5/((a+b*x^2)^(3/2)*Sqrt[c+d*x^2]), x]", //
        "-1/2*(b*c+3*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]/(b^(5/2)*d^(3/2))-a^2*Sqrt[c+d*x^2]/(b^2*(b*c-a*d)*Sqrt[a+b*x^2])+1/2*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(b^2*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1327
  public void test0698() {
    check( //
        "Integrate[(e*x)^(5/2)*(c+d*x^2)/(a+b*x^2)^(9/4), x]", //
        "2/5*(b*c-a*d)*(e*x)^(7/2)/(a*b*e*(a+b*x^2)^(5/4))-1/5*(2*b*c-7*a*d)*e*(e*x)^(3/2)/(a*b^2*(a+b*x^2)^(1/4))-3/5*(2*b*c-7*a*d)*e^2*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[e*x]/(b^(5/2)*(a+b*x^2)^(1/4)*Sqrt[a])");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:57
  public void test0699() {
    check( //
        "Integrate[(a+b*x^2)/((c+d*x^2)^(1/2)*(e+f*x^2)^(1/2)), x]", //
        "b*x*Sqrt[c+d*x^2]/(d*Sqrt[e+f*x^2])-b*EllipticE[ArcTan[x*Sqrt[f]/Sqrt[e]],1-d*e/(c*f)]*Sqrt[e]*Sqrt[c+d*x^2]/(d*Sqrt[f]*Sqrt[e*(c+d*x^2)/(c*(e+f*x^2))]*Sqrt[e+f*x^2])+a*EllipticF[ArcTan[x*Sqrt[f]/Sqrt[e]],1-d*e/(c*f)]*Sqrt[e]*Sqrt[c+d*x^2]/(c*Sqrt[f]*Sqrt[e*(c+d*x^2)/(c*(e+f*x^2))]*Sqrt[e+f*x^2])");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:71
  public void test0700() {
    check( //
        "Integrate[(a+b*x^2)/(Sqrt[2+d*x^2]*Sqrt[3+f*x^2]), x]", //
        "b*x*Sqrt[2+d*x^2]/(d*Sqrt[3+f*x^2])+a*EllipticF[ArcTan[x*Sqrt[f]/Sqrt[3]],1-3/2*d/f]*Sqrt[2+d*x^2]/(Sqrt[2]*Sqrt[f]*Sqrt[(2+d*x^2)/(3+f*x^2)]*Sqrt[3+f*x^2])-b*EllipticE[ArcTan[x*Sqrt[f]/Sqrt[3]],1-3/2*d/f]*Sqrt[2]*Sqrt[2+d*x^2]/(d*Sqrt[f]*Sqrt[(2+d*x^2)/(3+f*x^2)]*Sqrt[3+f*x^2])");
  }
}
