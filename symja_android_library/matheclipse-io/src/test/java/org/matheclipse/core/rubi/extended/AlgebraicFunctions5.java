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
public class AlgebraicFunctions5 extends AbstractRubiTestCase {
  static boolean init = true;

  public AlgebraicFunctions5(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("AlgebraicFunctions5");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:291
  public void test0001() {
    check( //
        "Integrate[x^14*(a+b*x^3)^3, x]", //
        "1/15*a^3*x^15+1/6*a^2*b*x^18+1/7*a*b^2*x^21+1/24*b^3*x^24");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:684
  public void test0002() {
    check( //
        "Integrate[x^8*(a+b*x^3)^p, x]", //
        "1/3*a^2*(a+b*x^3)^(1+p)/(b^3*(1+p))-2/3*a*(a+b*x^3)^(2+p)/(b^3*(2+p))+1/3*(a+b*x^3)^(3+p)/(b^3*(3+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:806
  public void test0003() {
    check( //
        "Integrate[x^2/(2*a+2*b+x^4), x]", //
        "1/2*ArcTan[x/(2^(1/4)*(-a-b)^(1/4))]/(2^(1/4)*(-a-b)^(1/4))-1/2*ArcTanh[x/(2^(1/4)*(-a-b)^(1/4))]/(2^(1/4)*(-a-b)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1276
  public void test0004() {
    check( //
        "Integrate[1/(x^16*(a+b*x^4)^(5/4)), x]", //
        "(-1/15)/(a*x^15*(a+b*x^4)^(1/4))+16/165*b/(a^2*x^11*(a+b*x^4)^(1/4))-64/385*b^2/(a^3*x^7*(a+b*x^4)^(1/4))+512/1155*b^3/(a^4*x^3*(a+b*x^4)^(1/4))+2048/1155*b^4*x/(a^5*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1405
  public void test0005() {
    check( //
        "Integrate[1/(x^11*(a+b*x^5)), x]", //
        "(-1/10)/(a*x^10)+1/5*b/(a^2*x^5)+b^2*Log[x]/a^3-1/5*b^2*Log[a+b*x^5]/a^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2068
  public void test0006() {
    check( //
        "Integrate[(a+b/x)^(5/2)/x^(3/2), x]", //
        "-5/8*a^3*ArcTanh[Sqrt[b]/(Sqrt[a+b/x]*Sqrt[x])]/Sqrt[b]-5/12*a*(a+b/x)^(3/2)/Sqrt[x]-1/3*(a+b/x)^(5/2)/Sqrt[x]-5/8*a^2*Sqrt[a+b/x]/Sqrt[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2150
  public void test0007() {
    check( //
        "Integrate[x^2/(a+b/x^2), x]", //
        "-b*x/a^2+1/3*x^3/a+b^(3/2)*ArcTan[x*Sqrt[a]/Sqrt[b]]/a^(5/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2675
  public void test0008() {
    check( //
        "Integrate[x^m*(a+b*Sqrt[x])^3, x]", //
        "a^3*x^(1+m)/(1+m)+6*a^2*b*x^(3/2+m)/(3+2*m)+3*a*b^2*x^(2+m)/(2+m)+2*b^3*x^(5/2+m)/(5+2*m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2898
  public void test0009() {
    check( //
        "Integrate[(a+b/x^(3/2))^(2/3), x]", //
        "(a+b/x^(3/2))^(2/3)*x+b^(2/3)*Log[(a+b/x^(3/2))^(1/3)-b^(1/3)/Sqrt[x]]-2*b^(2/3)*ArcTan[(1+2*b^(1/3)/((a+b/x^(3/2))^(1/3)*Sqrt[x]))/Sqrt[3]]/Sqrt[3]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3079
  public void test0010() {
    check( //
        "Integrate[x^(-1+4*n)*(a+b*x^n)^2, x]", //
        "1/4*a^2*x^(4*n)/n+2/5*a*b*x^(5*n)/n+1/6*b^2*x^(6*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3131
  public void test0011() {
    check( //
        "Integrate[x^(-1-5*n)*(a+b*x^n)^8, x]", //
        "-1/5*a^8/(n*x^(5*n))-2*a^7*b/(n*x^(4*n))-28/3*a^6*b^2/(n*x^(3*n))-28*a^5*b^3/(n*x^(2*n))-70*a^4*b^4/(n*x^n)+28*a^2*b^6*x^n/n+4*a*b^7*x^(2*n)/n+1/3*b^8*x^(3*n)/n+56*a^3*b^5*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3334
  public void test0012() {
    check( //
        "Integrate[(c*x)^m*(a+b*x^n)^2, x]", //
        "2*a*b*x^(1+n)*(c*x)^m/(1+m+n)+b^2*x^(1+2*n)*(c*x)^m/(1+m+2*n)+a^2*(c*x)^(1+m)/(c*(1+m))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3375
  public void test0013() {
    check( //
        "Integrate[(c*x)^(-1-7/2*n)/Sqrt[a+b*x^n], x]", //
        "4*(a+b*x^n)^(3/2)/(a^2*c*n*(c*x)^(7/2*n))-16/5*(a+b*x^n)^(5/2)/(a^3*c*n*(c*x)^(7/2*n))+32/35*(a+b*x^n)^(7/2)/(a^4*c*n*(c*x)^(7/2*n))-2*Sqrt[a+b*x^n]/(a*c*n*(c*x)^(7/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3470
  public void test0014() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*(c+d*x)^3), x]", //
        "1/4*a*(c+d*x)^4/d+1/7*b*(c+d*x)^7/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3495
  public void test0015() {
    check( //
        "Integrate[1/((c+d*x)^4*(a+b*(c+d*x)^3)^2), x]", //
        "(-1/3)/(a^2*d*(c+d*x)^3)-1/3*b/(a^2*d*(a+b*(c+d*x)^3))-2*b*Log[c+d*x]/(a^3*d)+2/3*b*Log[a+b*(c+d*x)^3]/(a^3*d)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3708
  public void test0016() {
    check( //
        "Integrate[1/(x^2*(a+b*(c*x^n)^(1/n))), x]", //
        "(-1)/(a*x)-b*(c*x^n)^(1/n)*Log[x]/(a^2*x)+b*(c*x^n)^(1/n)*Log[a+b*(c*x^n)^(1/n)]/(a^2*x)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:22
  public void test0017() {
    check( //
        "Integrate[(a+b*x^3)^2/(c+d*x^3), x]", //
        "-b*(b*c-2*a*d)*x/d^2+1/4*b^2*x^4/d+1/3*(b*c-a*d)^2*Log[c^(1/3)+d^(1/3)*x]/(c^(2/3)*d^(7/3))-1/6*(b*c-a*d)^2*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(c^(2/3)*d^(7/3))-(b*c-a*d)^2*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(c^(2/3)*d^(7/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:52
  public void test0018() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(16/3), x]", //
        "2/13*x/(a+b*x^3)^(13/3)+11/130*x/(a*(a+b*x^3)^(10/3))+99/910*x/(a^2*(a+b*x^3)^(7/3))+297/1820*x/(a^3*(a+b*x^3)^(4/3))+891/1820*x/(a^4*(a+b*x^3)^(1/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:99
  public void test0019() {
    check( //
        "Integrate[(c+d*x^3)^2/(a+b*x^3)^(13/3), x]", //
        "9/140*c^2*(9*b*c-10*a*d)*x/(a^4*(b*c-a*d)*(a+b*x^3)^(1/3))+3/140*c*(9*b*c-10*a*d)*x*(c+d*x^3)/(a^3*(b*c-a*d)*(a+b*x^3)^(4/3))+1/70*(9*b*c-10*a*d)*x*(c+d*x^3)^2/(a^2*(b*c-a*d)*(a+b*x^3)^(7/3))+1/10*b*x*(c+d*x^3)^3/(a*(b*c-a*d)*(a+b*x^3)^(10/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:137
  public void test0020() {
    check( //
        "Integrate[(a+b*x^3)^(5/3)/(c+d*x^3)^3, x]", //
        "1/6*x*(a+b*x^3)^(5/3)/(c*(c+d*x^3)^2)+5/18*a*x*(a+b*x^3)^(2/3)/(c^2*(c+d*x^3))+5/54*a^2*Log[c+d*x^3]/(c^(8/3)*(b*c-a*d)^(1/3))-5/18*a^2*Log[(b*c-a*d)^(1/3)*x/c^(1/3)-(a+b*x^3)^(1/3)]/(c^(8/3)*(b*c-a*d)^(1/3))+5/9*a^2*ArcTan[(1+2*(b*c-a*d)^(1/3)*x/(c^(1/3)*(a+b*x^3)^(1/3)))/Sqrt[3]]/(c^(8/3)*(b*c-a*d)^(1/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:201
  public void test0021() {
    check( //
        "Integrate[1/((a+b*x^4)*(c+d*x^4)), x]", //
        "-1/2*b^(3/4)*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*(b*c-a*d)*Sqrt[2])+1/2*b^(3/4)*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*(b*c-a*d)*Sqrt[2])+1/2*d^(3/4)*ArcTan[1-d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*(b*c-a*d)*Sqrt[2])-1/2*d^(3/4)*ArcTan[1+d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*(b*c-a*d)*Sqrt[2])-1/4*b^(3/4)*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*(b*c-a*d)*Sqrt[2])+1/4*b^(3/4)*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*(b*c-a*d)*Sqrt[2])+1/4*d^(3/4)*Log[-c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*(b*c-a*d)*Sqrt[2])-1/4*d^(3/4)*Log[c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*(b*c-a*d)*Sqrt[2])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:351
  public void test0022() {
    check( //
        "Integrate[(c+d/x)^(1/2)*Sqrt[a+b/x], x]", //
        "(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b/x]/(Sqrt[a]*Sqrt[c+d/x])]/(Sqrt[a]*Sqrt[c])-2*ArcTanh[Sqrt[d]*Sqrt[a+b/x]/(Sqrt[b]*Sqrt[c+d/x])]*Sqrt[b]*Sqrt[d]+x*Sqrt[a+b/x]*Sqrt[c+d/x]");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:537
  public void test0023() {
    check( //
        "Integrate[1/(Sqrt[1+x]*Sqrt[-1-Sqrt[x]]*Sqrt[-1+Sqrt[x]]), x]", //
        "ArcSin[x]*Sqrt[1-x]/(Sqrt[-1-Sqrt[x]]*Sqrt[-1+Sqrt[x]])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:32
  public void test0024() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^8, x]", //
        "-1/7*a^2*A/x^7-1/4*a*(2*A*b+a*B)/x^4-b*(A*b+2*a*B)/x+1/2*b^2*B*x^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:56
  public void test0025() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^13, x]", //
        "-1/12*a^5*A/x^12-1/9*a^4*(5*A*b+a*B)/x^9-5/6*a^3*b*(2*A*b+a*B)/x^6-10/3*a^2*b^2*(A*b+a*B)/x^3+1/3*b^4*(A*b+5*a*B)*x^3+1/6*b^5*B*x^6+5*a*b^3*(A*b+2*a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:78
  public void test0026() {
    check( //
        "Integrate[(A+B*x^3)/(x^3*(a+b*x^3)), x]", //
        "-1/2*A/(a*x^2)-1/3*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(1/3))+1/6*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(1/3))+(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:130
  public void test0027() {
    check( //
        "Integrate[x^4/((a+b*x^3)*(c+d*x^3)), x]", //
        "1/3*a^(2/3)*Log[a^(1/3)+b^(1/3)*x]/(b^(2/3)*(b*c-a*d))-1/3*c^(2/3)*Log[c^(1/3)+d^(1/3)*x]/(d^(2/3)*(b*c-a*d))-1/6*a^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(b^(2/3)*(b*c-a*d))+1/6*c^(2/3)*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(d^(2/3)*(b*c-a*d))+a^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(2/3)*(b*c-a*d)*Sqrt[3])-c^(2/3)*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(d^(2/3)*(b*c-a*d)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:164
  public void test0028() {
    check( //
        "Integrate[(a+b*x^3)*(A+B*x^3)/x^(5/2), x]", //
        "-2/3*a*A/x^(3/2)+2/3*(A*b+a*B)*x^(3/2)+2/9*b*B*x^(9/2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:190
  public void test0029() {
    check( //
        "Integrate[(A+B*x^3)/(x^(5/2)*(a+b*x^3)), x]", //
        "-2/3*A/(a*x^(3/2))-2/3*(A*b-a*B)*ArcTan[x^(3/2)*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:319
  public void test0030() {
    check( //
        "Integrate[x/((4*c+d*x^3)*Sqrt[c+d*x^3]), x]", //
        "-1/3*ArcTanh[c^(1/6)*(c^(1/3)-2^(1/3)*d^(1/3)*x)/Sqrt[c+d*x^3]]/(2^(2/3)*c^(5/6)*d^(2/3))+1/9*ArcTanh[Sqrt[c+d*x^3]/Sqrt[c]]/(2^(2/3)*c^(5/6)*d^(2/3))-1/3*ArcTan[c^(1/6)*(c^(1/3)+2^(1/3)*d^(1/3)*x)*Sqrt[3]/Sqrt[c+d*x^3]]/(2^(2/3)*c^(5/6)*d^(2/3)*Sqrt[3])+1/3*ArcTan[Sqrt[c+d*x^3]/(Sqrt[3]*Sqrt[c])]/(2^(2/3)*c^(5/6)*d^(2/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:374
  public void test0031() {
    check( //
        "Integrate[x^5/((8*c-d*x^3)*(c+d*x^3)^(3/2)), x]", //
        "16/81*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]/(d^2*Sqrt[c])+2/27/(d^2*Sqrt[c+d*x^3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:404
  public void test0032() {
    check( //
        "Integrate[x/((-b*x^3-2*a*(5+3*Sqrt[3]))*Sqrt[-a-b*x^3]), x]", //
        "1/6*ArcTan[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2-Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])+1/3*ArcTan[3^(1/4)*a^(1/6)*(-2*b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2-Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])+1/2*ArcTanh[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2-Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/3*ArcTanh[(1-Sqrt[3])*Sqrt[-a-b*x^3]/(3^(3/4)*Sqrt[2]*Sqrt[a])]*(2-Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:444
  public void test0033() {
    check( //
        "Integrate[x^5/((a+b*x^3)*(c+d*x^3)^(3/2)), x]", //
        "2/3*a*ArcTanh[Sqrt[b]*Sqrt[c+d*x^3]/Sqrt[b*c-a*d]]/((b*c-a*d)^(3/2)*Sqrt[b])-2/3*c/(d*(b*c-a*d)*Sqrt[c+d*x^3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:533
  public void test0034() {
    check( //
        "Integrate[x^8*(c+d*x^3)^(3/2)/(a+b*x^3)^2, x]", //
        "-1/9*a*(4*b*c-7*a*d)*(c+d*x^3)^(3/2)/(b^3*(b*c-a*d))+2/15*(c+d*x^3)^(5/2)/(b^2*d)-1/3*a^2*(c+d*x^3)^(5/2)/(b^2*(b*c-a*d)*(a+b*x^3))+1/3*a*(4*b*c-7*a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^3]/Sqrt[b*c-a*d]]*Sqrt[b*c-a*d]/b^(9/2)-1/3*a*(4*b*c-7*a*d)*Sqrt[c+d*x^3]/b^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:722
  public void test0035() {
    check( //
        "Integrate[x/((1-x^3)^(2/3)*(1+x^3)), x]", //
        "1/6*Log[1+x^3]/2^(2/3)-1/2*Log[-2^(1/3)*x-(1-x^3)^(1/3)]/2^(2/3)-ArcTan[(1-2*2^(1/3)*x/(1-x^3)^(1/3))/Sqrt[3]]/(2^(2/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:877
  public void test0036() {
    check( //
        "Integrate[1/(x^5*(a+b*x^4)*(c+d*x^4)), x]", //
        "(-1/4)/(a*c*x^4)-(b*c+a*d)*Log[x]/(a^2*c^2)+1/4*b^2*Log[a+b*x^4]/(a^2*(b*c-a*d))-1/4*d^2*Log[c+d*x^4]/(c^2*(b*c-a*d))");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:48
  public void test0037() {
    check( //
        "Integrate[(8*C+b^(2/3)*C*x^2)/(8+b*x^3), x]", //
        "C*Log[2+b^(1/3)*x]/b^(1/3)-2*C*ArcTan[(1-b^(1/3)*x)/Sqrt[3]]/(b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:67
  public void test0038() {
    check( //
        "Integrate[(a+a*x+c*x^2)/(1-x^3), x]", //
        "-1/3*(2*a+c)*Log[1-x]+1/3*(a-c)*Log[1+x+x^2]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:158
  public void test0039() {
    check( //
        "Integrate[(c+d*x)/Sqrt[1+x^3], x]", //
        "2*d*Sqrt[1+x^3]/(1+x+Sqrt[3])-3^(1/4)*d*(1+x)*EllipticE[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])+2*(1+x)*EllipticF[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*(c-d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(3^(1/4)*Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:191
  public void test0040() {
    check( //
        "Integrate[(c+d*x+e*x^2)/(a+b*x^4)^3, x]", //
        "1/8*x*(c+d*x+e*x^2)/(a*(a+b*x^4)^2)+1/32*x*(7*c+6*d*x+5*e*x^2)/(a^2*(a+b*x^4))+3/16*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])-1/128*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-5*e*Sqrt[a]+21*c*Sqrt[b])/(a^(11/4)*b^(3/4)*Sqrt[2])+1/128*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-5*e*Sqrt[a]+21*c*Sqrt[b])/(a^(11/4)*b^(3/4)*Sqrt[2])-1/64*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(5*e*Sqrt[a]+21*c*Sqrt[b])/(a^(11/4)*b^(3/4)*Sqrt[2])+1/64*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(5*e*Sqrt[a]+21*c*Sqrt[b])/(a^(11/4)*b^(3/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:373
  public void test0041() {
    check( //
        "Integrate[x^6*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^2, x]", //
        "(b^3*c-2*a*b^2*d+3*a^2*b*e-4*a^3*f)*x/b^5+1/4*(b^2*d-2*a*b*e+3*a^2*f)*x^4/b^4+1/7*(b*e-2*a*f)*x^7/b^3+1/10*f*x^10/b^2+1/3*a*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/(b^5*(a+b*x^3))-1/9*a^(1/3)*(4*b^3*c-7*a*b^2*d+10*a^2*b*e-13*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(16/3)+1/18*a^(1/3)*(4*b^3*c-7*a*b^2*d+10*a^2*b*e-13*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(16/3)+1/3*a^(1/3)*(4*b^3*c-7*a*b^2*d+10*a^2*b*e-13*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(16/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:439
  public void test0042() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)/x^3, x]", //
        "-1/2*a*c/x^2-a*d/x+b*c*x+1/2*b*d*x^2+1/3*b*e*x^3+a*e*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:565
  public void test0043() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2)/Sqrt[a+b*x^3], x]", //
        "-4/9*a*e*Sqrt[a+b*x^3]/b^2+2/5*c*x*Sqrt[a+b*x^3]/b+2/7*d*x^2*Sqrt[a+b*x^3]/b+2/9*e*x^3*Sqrt[a+b*x^3]/b-8/7*a*d*Sqrt[a+b*x^3]/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+4/7*3^(1/4)*a^(4/3)*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-4/35*a*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(7*b^(1/3)*c-10*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:588
  public void test0044() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3+g*x^4)*Sqrt[a+b*x^3]/x, x]", //
        "-2/3*c*ArcTanh[Sqrt[a+b*x^3]/Sqrt[a]]*Sqrt[a]+2/9*a*f*Sqrt[a+b*x^3]/b+6/55*a*g*x*Sqrt[a+b*x^3]/b+2/3465*(1155*c*x+693*d*x^2+495*e*x^3+385*f*x^4+315*g*x^5)*Sqrt[a+b*x^3]/x+6/7*a*e*Sqrt[a+b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-3/7*3^(1/4)*a^(4/3)*e*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+2/385*3^(3/4)*a*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(77*b*d-14*a*g-55*a^(1/3)*b^(2/3)*e*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(4/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:663
  public void test0045() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*Sqrt[a+b*x^4]/x^9, x]", //
        "1/16*b^2*c*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/a^(3/2)-1/840*(105*c/x^8+120*d/x^7+140*e/x^6+168*f/x^5)*Sqrt[a+b*x^4]-1/16*b*c*Sqrt[a+b*x^4]/(a*x^4)-2/21*b*d*Sqrt[a+b*x^4]/(a*x^3)-1/6*b*e*Sqrt[a+b*x^4]/(a*x^2)-2/5*b*f*Sqrt[a+b*x^4]/(a*x)+2/5*b^(3/2)*f*x*Sqrt[a+b*x^4]/(a*(Sqrt[a]+x^2*Sqrt[b]))-2/5*b^(5/4)*f*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*Sqrt[a+b*x^4])-1/105*b^(5/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-21*f*Sqrt[a]+5*d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:681
  public void test0046() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^12, x]", //
        "-1/3960*(360*c/x^11+396*d/x^10+440*e/x^9+495*f/x^8)*(a+b*x^4)^(3/2)-3/16*b^2*f*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/Sqrt[a]-1/18480*b*(1440*c/x^7+1848*d/x^6+2464*e/x^5+3465*f/x^4)*Sqrt[a+b*x^4]-4/77*b^2*c*Sqrt[a+b*x^4]/(a*x^3)-1/10*b^2*d*Sqrt[a+b*x^4]/(a*x^2)-4/15*b^2*e*Sqrt[a+b*x^4]/(a*x)+4/15*b^(5/2)*e*x*Sqrt[a+b*x^4]/(a*(Sqrt[a]+x^2*Sqrt[b]))-4/15*b^(9/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*Sqrt[a+b*x^4])-2/1155*b^(9/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-77*e*Sqrt[a]+15*c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:702
  public void test0047() {
    check( //
        "Integrate[x*(c+d*x+e*x^2+f*x^3)/(a+b*x^4)^(3/2), x]", //
        "-1/2*x*(a*f-b*c*x-b*d*x^2-b*e*x^3)/(a*b*Sqrt[a+b*x^4])-1/2*e*Sqrt[a+b*x^4]/(a*b)-1/2*d*x*Sqrt[a+b*x^4]/(a*Sqrt[b]*(Sqrt[a]+x^2*Sqrt[b]))+1/2*d*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*b^(3/4)*Sqrt[a+b*x^4])-1/4*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-f*Sqrt[a]+d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*b^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:790
  public void test0048() {
    check( //
        "Integrate[(a+b*x^n)^p*(c+d*x^n)^p*(e+(b*c+a*d)*e*(1+n+n*p)*x^n/(a*c)+b*d*e*(1+2*n+2*n*p)*x^(2*n)/(a*c)), x]", //
        "e*x*(a+b*x^n)^(1+p)*(c+d*x^n)^(1+p)/(a*c)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:31
  public void test0049() {
    check( //
        "Integrate[1/(x^2*(a*x+b*x^3)), x]", //
        "(-1/2)/(a*x^2)-b*Log[x]/a^2+1/2*b*Log[a+b*x^2]/a^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:53
  public void test0050() {
    check( //
        "Integrate[1/(-x+b*x^3), x]", //
        "-Log[x]+1/2*Log[1-b*x^2]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:184
  public void test0051() {
    check( //
        "Integrate[1/(x^(7/2)*(b*x^(1/2)+a*x)^(3/2)), x]", //
        "4/(b*x^3*Sqrt[a*x+b*Sqrt[x]])-56/13*Sqrt[a*x+b*Sqrt[x]]/(b^2*x^(7/2))+672/143*a*Sqrt[a*x+b*Sqrt[x]]/(b^3*x^3)-2240/429*a^2*Sqrt[a*x+b*Sqrt[x]]/(b^4*x^(5/2))+2560/429*a^3*Sqrt[a*x+b*Sqrt[x]]/(b^5*x^2)-1024/143*a^4*Sqrt[a*x+b*Sqrt[x]]/(b^6*x^(3/2))+4096/429*a^5*Sqrt[a*x+b*Sqrt[x]]/(b^7*x)-8192/429*a^6*Sqrt[a*x+b*Sqrt[x]]/(b^8*Sqrt[x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:233
  public void test0052() {
    check( //
        "Integrate[x^3*Sqrt[b*x^(2/3)+a*x], x]", //
        "-524288/4345965*b^9*(b*x^(2/3)+a*x)^(3/2)/a^10+8388608/152108775*b^12*(b*x^(2/3)+a*x)^(3/2)/(a^13*x)-4194304/50702925*b^11*(b*x^(2/3)+a*x)^(3/2)/(a^12*x^(2/3))+1048576/10140585*b^10*(b*x^(2/3)+a*x)^(3/2)/(a^11*x^(1/3))+65536/482885*b^8*x^(1/3)*(b*x^(2/3)+a*x)^(3/2)/a^9-360448/2414425*b^7*x^(2/3)*(b*x^(2/3)+a*x)^(3/2)/a^8+90112/557175*b^6*x*(b*x^(2/3)+a*x)^(3/2)/a^7-45056/260015*b^5*x^(4/3)*(b*x^(2/3)+a*x)^(3/2)/a^6+2816/15295*b^4*x^(5/3)*(b*x^(2/3)+a*x)^(3/2)/a^5-1408/7245*b^3*x^2*(b*x^(2/3)+a*x)^(3/2)/a^4+352/1725*b^2*x^(7/3)*(b*x^(2/3)+a*x)^(3/2)/a^3-16/75*b*x^(8/3)*(b*x^(2/3)+a*x)^(3/2)/a^2+2/9*x^3*(b*x^(2/3)+a*x)^(3/2)/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:254
  public void test0053() {
    check( //
        "Integrate[x^3/Sqrt[b*x^(2/3)+a*x], x]", //
        "-262144/323323*b^9*Sqrt[b*x^(2/3)+a*x]/a^10+524288/323323*b^10*Sqrt[b*x^(2/3)+a*x]/(a^11*x^(1/3))+196608/323323*b^8*x^(1/3)*Sqrt[b*x^(2/3)+a*x]/a^9-163840/323323*b^7*x^(2/3)*Sqrt[b*x^(2/3)+a*x]/a^8+20480/46189*b^6*x*Sqrt[b*x^(2/3)+a*x]/a^7-18432/46189*b^5*x^(4/3)*Sqrt[b*x^(2/3)+a*x]/a^6+1536/4199*b^4*x^(5/3)*Sqrt[b*x^(2/3)+a*x]/a^5-768/2261*b^3*x^2*Sqrt[b*x^(2/3)+a*x]/a^4+720/2261*b^2*x^(7/3)*Sqrt[b*x^(2/3)+a*x]/a^3-40/133*b*x^(8/3)*Sqrt[b*x^(2/3)+a*x]/a^2+2/7*x^3*Sqrt[b*x^(2/3)+a*x]/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:281
  public void test0054() {
    check( //
        "Integrate[a*x^2+b*x^3, x]", //
        "1/3*a*x^3+1/4*b*x^4");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:324
  public void test0055() {
    check( //
        "Integrate[(a*x^2+b*x^3)^(3/2), x]", //
        "-32/1155*a^3*(a*x^2+b*x^3)^(5/2)/(b^4*x^5)+16/231*a^2*(a*x^2+b*x^3)^(5/2)/(b^3*x^4)-4/33*a*(a*x^2+b*x^3)^(5/2)/(b^2*x^3)+2/11*(a*x^2+b*x^3)^(5/2)/(b*x^2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:345
  public void test0056() {
    check( //
        "Integrate[x^5/(a*x^2+b*x^3)^(3/2), x]", //
        "-2*x^3/(b*Sqrt[a*x^2+b*x^3])+8/3*Sqrt[a*x^2+b*x^3]/b^2-16/3*a*Sqrt[a*x^2+b*x^3]/(b^3*x)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:442
  public void test0057() {
    check( //
        "Integrate[1/(-x^3+b*x^5), x]", //
        "1/2/x^2-b*Log[x]+1/2*b*Log[1-b*x^2]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:470
  public void test0058() {
    check( //
        "Integrate[1/(b/x+a*x), x]", //
        "1/2*Log[b+a*x^2]/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:491
  public void test0059() {
    check( //
        "Integrate[1/(a*x+b*x^(1-n)), x]", //
        "Log[b+a*x^n]/(a*n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:514
  public void test0060() {
    check( //
        "Integrate[(a*x+b*x^n)^(3/2)/(c*x)^(5/2), x]", //
        "-2/3*(a*x+b*x^n)^(3/2)/(c*(1-n)*(c*x)^(3/2))+2*a^(3/2)*ArcTanh[Sqrt[a]*Sqrt[x]/Sqrt[a*x+b*x^n]]*Sqrt[x]/(c^2*(1-n)*Sqrt[c*x])-2*a*Sqrt[a*x+b*x^n]/(c^2*(1-n)*Sqrt[c*x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:534
  public void test0061() {
    check( //
        "Integrate[1/(c*x*Sqrt[a+b*x^n]), x]", //
        "-2*ArcTanh[Sqrt[a+b*x^n]/Sqrt[a]]/(c*n*Sqrt[a])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:553
  public void test0062() {
    check( //
        "Integrate[1/Sqrt[(a-b*x^5)/x^3], x]", //
        "2/5*ArcTan[x*Sqrt[b]/Sqrt[a/x^3-b*x^2]]/Sqrt[b]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:22
  public void test0063() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)/x^8, x]", //
        "-1/5*A*b/x^5+1/3*(-b*B-A*c)/x^3-B*c/x");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:41
  public void test0064() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^7, x]", //
        "3/2*A*b^2*c*x^2+3/4*A*b*c^2*x^4+1/6*A*c^3*x^6+1/8*B*(b+c*x^2)^4/c+A*b^3*Log[x]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:62
  public void test0065() {
    check( //
        "Integrate[x^2*(A+B*x^2)/(b*x^2+c*x^4), x]", //
        "B*x/c-(b*B-A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/(c^(3/2)*Sqrt[b])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:122
  public void test0066() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[b*x^2+c*x^4]/x^6, x]", //
        "-1/4*A*(b*x^2+c*x^4)^(3/2)/(b*x^7)-1/8*c*(4*b*B-A*c)*ArcTanh[x*Sqrt[b]/Sqrt[b*x^2+c*x^4]]/b^(3/2)-1/8*(4*b*B-A*c)*Sqrt[b*x^2+c*x^4]/(b*x^3)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:140
  public void test0067() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^(3/2)/x^4, x]", //
        "1/3*A*(b*x^2+c*x^4)^(3/2)/x^3+1/5*B*(b*x^2+c*x^4)^(5/2)/(c*x^5)-A*b^(3/2)*ArcTanh[x*Sqrt[b]/Sqrt[b*x^2+c*x^4]]+A*b*Sqrt[b*x^2+c*x^4]/x");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:161
  public void test0068() {
    check( //
        "Integrate[(A+B*x^2)/Sqrt[b*x^2+c*x^4], x]", //
        "-A*ArcTanh[x*Sqrt[b]/Sqrt[b*x^2+c*x^4]]/Sqrt[b]+B*Sqrt[b*x^2+c*x^4]/(c*x)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:202
  public void test0069() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/Sqrt[x], x]", //
        "2/13*A*b^3*x^(13/2)+2/17*b^2*(b*B+3*A*c)*x^(17/2)+2/7*b*c*(b*B+A*c)*x^(21/2)+2/25*c^2*(3*b*B+A*c)*x^(25/2)+2/29*B*c^3*x^(29/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:317
  public void test0070() {
    check( //
        "Integrate[(4+3*x^4)/(5*x+2*x^5), x]", //
        "4/5*Log[x]+7/40*Log[5+2*x^4]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:76
  public void test0071() {
    check( //
        "Integrate[1/(a+c*x^2), x]", //
        "ArcTan[x*Sqrt[c]/Sqrt[a]]/(Sqrt[a]*Sqrt[c])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:126
  public void test0072() {
    check( //
        "Integrate[1/(2+4*x+3*x^2)^2, x]", //
        "1/4*(2+3*x)/(2+4*x+3*x^2)+3/4*ArcTan[(2+3*x)/Sqrt[2]]/Sqrt[2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:151
  public void test0073() {
    check( //
        "Integrate[Sqrt[-2+5*x+3*x^2], x]", //
        "-49/24*ArcTanh[1/2*(5+6*x)/(Sqrt[3]*Sqrt[-2+5*x+3*x^2])]/Sqrt[3]+1/12*(5+6*x)*Sqrt[-2+5*x+3*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:47
  public void test0074() {
    check( //
        "Integrate[(a*x+b*x^2)^(5/2)/x^10, x]", //
        "-2/13*(a*x+b*x^2)^(7/2)/(a*x^10)+12/143*b*(a*x+b*x^2)^(7/2)/(a^2*x^9)-16/429*b^2*(a*x+b*x^2)^(7/2)/(a^3*x^8)+32/3003*b^3*(a*x+b*x^2)^(7/2)/(a^4*x^7)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:67
  public void test0075() {
    check( //
        "Integrate[x^2/(b*x+c*x^2)^(3/2), x]", //
        "2*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/c^(3/2)-2*x/(c*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:90
  public void test0076() {
    check( //
        "Integrate[x^(5/2)*(b*x+c*x^2)^(1/2), x]", //
        "-32/315*b^3*(b*x+c*x^2)^(3/2)/(c^4*x^(3/2))+2/9*x^(3/2)*(b*x+c*x^2)^(3/2)/c+16/105*b^2*(b*x+c*x^2)^(3/2)/(c^3*Sqrt[x])-4/21*b*(b*x+c*x^2)^(3/2)*Sqrt[x]/c^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:109
  public void test0077() {
    check( //
        "Integrate[(b*x+c*x^2)^(3/2)/x^(13/2), x]", //
        "-1/4*(b*x+c*x^2)^(3/2)/x^(11/2)-3/64*c^4*ArcTanh[Sqrt[b*x+c*x^2]/(Sqrt[b]*Sqrt[x])]/b^(5/2)-1/8*c*Sqrt[b*x+c*x^2]/x^(7/2)-1/32*c^2*Sqrt[b*x+c*x^2]/(b*x^(5/2))+3/64*c^3*Sqrt[b*x+c*x^2]/(b^2*x^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:129
  public void test0078() {
    check( //
        "Integrate[1/(x^(3/2)*(b*x+c*x^2)^(3/2)), x]", //
        "-15/4*c^2*ArcTanh[Sqrt[b*x+c*x^2]/(Sqrt[b]*Sqrt[x])]/b^(7/2)+(-1/2)/(b*x^(3/2)*Sqrt[b*x+c*x^2])+5/4*c/(b^2*Sqrt[x]*Sqrt[b*x+c*x^2])+15/4*c^2*Sqrt[x]/(b^3*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:202
  public void test0079() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(5/2)/x^3, x]", //
        "-1/2*a^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^2*(a+b*x))-5*a^4*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x*(a+b*x))+10*a^2*b^3*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+5/2*a*b^4*x^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+1/3*b^5*x^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+10*a^3*b^2*Log[x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:222
  public void test0080() {
    check( //
        "Integrate[1/(x^4*Sqrt[a^2+2*a*b*x+b^2*x^2]), x]", //
        "1/3*(-a-b*x)/(a*x^3*Sqrt[a^2+2*a*b*x+b^2*x^2])+1/2*b*(a+b*x)/(a^2*x^2*Sqrt[a^2+2*a*b*x+b^2*x^2])-b^2*(a+b*x)/(a^3*x*Sqrt[a^2+2*a*b*x+b^2*x^2])-b^3*(a+b*x)*Log[x]/(a^4*Sqrt[a^2+2*a*b*x+b^2*x^2])+b^3*(a+b*x)*Log[a+b*x]/(a^4*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:585
  public void test0081() {
    check( //
        "Integrate[(d+e*x)^4/(a+c*x^2), x]", //
        "e^2*(6*c*d^2-a*e^2)*x/c^2+2*d*e^3*x^2/c+1/3*e^4*x^3/c+2*d*e*(c*d^2-a*e^2)*Log[a+c*x^2]/c^2+(c^2*d^4-6*a*c*d^2*e^2+a^2*e^4)*ArcTan[x*Sqrt[c]/Sqrt[a]]/(c^(5/2)*Sqrt[a])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:672
  public void test0082() {
    check( //
        "Integrate[(1+x)*(1+2*x+x^2)^5/x^2, x]", //
        "(-1)/x+55*x+165/2*x^2+110*x^3+231/2*x^4+462/5*x^5+55*x^6+165/7*x^7+55/8*x^8+11/9*x^9+1/10*x^10+11*Log[x]");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:94
  public void test0083() {
    check( //
        "Integrate[(1+x+x^2)/(x*(1+x^2)^2), x]", //
        "1/2*x/(1+x^2)+1/2*ArcTan[x]+Log[x]-1/2*Log[1+x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:949
  public void test0084() {
    check( //
        "Integrate[1/(2+3*x^2)^(3/4), x]", //
        "2^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[3/2]],2]/Sqrt[3]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:335
  public void test0085() {
    check( //
        "Integrate[Sqrt[1-2*c*x^2/(b-Sqrt[b^2-4*a*c])]/Sqrt[1-2*c*x^2/(b+Sqrt[b^2-4*a*c])], x]", //
        "EllipticE[ArcSin[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]],(b+Sqrt[b^2-4*a*c])/(b-Sqrt[b^2-4*a*c])]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:751
  public void test0086() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^4*(c+d*x^2)^(5/2)), x]", //
        "-1/3*a^2/(c*x^3*(c+d*x^2)^(3/2))-2*a*(b*c-a*d)/(c^2*x*(c+d*x^2)^(3/2))+1/3*(b^2*c^2-8*a*d*(b*c-a*d))*x/(c^3*(c+d*x^2)^(3/2))+2/3*(b^2*c^2-8*a*d*(b*c-a*d))*x/(c^4*Sqrt[c+d*x^2])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:124
  public void test0087() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x+C*x^2+D*x^3), x]", //
        "a^3*A*x+1/3*a^2*(3*A*b+a*C)*x^3+1/4*a^3*D*x^4+3/5*a*b*(A*b+a*C)*x^5+1/2*a^2*b*D*x^6+1/7*b^2*(A*b+3*a*C)*x^7+3/8*a*b^2*D*x^8+1/9*b^3*C*x^9+1/10*b^3*D*x^10+1/8*B*(a+b*x^2)^4/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:303
  public void test0088() {
    check( //
        "Integrate[(a+b*x^3)^3/x^22, x]", //
        "-1/21*a^3/x^21-1/6*a^2*b/x^18-1/5*a*b^2/x^15-1/12*b^3/x^12");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:417
  public void test0089() {
    check( //
        "Integrate[1/(a-b*x^3), x]", //
        "-1/3*Log[a^(1/3)-b^(1/3)*x]/(a^(2/3)*b^(1/3))+1/6*Log[a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(1/3))+ArcTan[(a^(1/3)+2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:942
  public void test0090() {
    check( //
        "Integrate[x/Sqrt[a-b*x^4], x]", //
        "1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a-b*x^4]]/Sqrt[b]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1288
  public void test0091() {
    check( //
        "Integrate[1/(a+b*x^4)^(13/4), x]", //
        "1/9*x/(a*(a+b*x^4)^(9/4))+8/45*x/(a^2*(a+b*x^4)^(5/4))+32/45*x/(a^3*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2072
  public void test0092() {
    check( //
        "Integrate[x^(7/2)/(a+b/x)^(1/2), x]", //
        "-128/315*b^3*x^(3/2)*Sqrt[a+b/x]/a^4+32/105*b^2*x^(5/2)*Sqrt[a+b/x]/a^3-16/63*b*x^(7/2)*Sqrt[a+b/x]/a^2+2/9*x^(9/2)*Sqrt[a+b/x]/a+256/315*b^4*Sqrt[a+b/x]*Sqrt[x]/a^5");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2238
  public void test0093() {
    check( //
        "Integrate[1/(x^2*Sqrt[2-b/x^2]), x]", //
        "-ArcCsc[x*Sqrt[2]/Sqrt[b]]/Sqrt[b]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2418
  public void test0094() {
    check( //
        "Integrate[(a+b/x^4)^(5/2)/x^3, x]", //
        "-5/48*a*(a+b/x^4)^(3/2)/x^2-1/12*(a+b/x^4)^(5/2)/x^2-5/32*a^3*ArcTanh[Sqrt[b]/(x^2*Sqrt[a+b/x^4])]/Sqrt[b]-5/32*a^2*Sqrt[a+b/x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2676
  public void test0095() {
    check( //
        "Integrate[x^m*(a+b*Sqrt[x])^2, x]", //
        "a^2*x^(1+m)/(1+m)+4*a*b*x^(3/2+m)/(3+2*m)+b^2*x^(2+m)/(2+m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3080
  public void test0096() {
    check( //
        "Integrate[x^(-1+3*n)*(a+b*x^n)^2, x]", //
        "1/3*a^2*x^(3*n)/n+1/2*a*b*x^(4*n)/n+1/5*b^2*x^(5*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3106
  public void test0097() {
    check( //
        "Integrate[(a+b*x^n)^5/x, x]", //
        "5*a^4*b*x^n/n+5*a^3*b^2*x^(2*n)/n+10/3*a^2*b^3*x^(3*n)/n+5/4*a*b^4*x^(4*n)/n+1/5*b^5*x^(5*n)/n+a^5*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3132
  public void test0098() {
    check( //
        "Integrate[x^(-1-6*n)*(a+b*x^n)^8, x]", //
        "-1/6*a^8/(n*x^(6*n))-8/5*a^7*b/(n*x^(5*n))-7*a^6*b^2/(n*x^(4*n))-56/3*a^5*b^3/(n*x^(3*n))-35*a^4*b^4/(n*x^(2*n))-56*a^3*b^5/(n*x^n)+8*a*b^7*x^n/n+1/2*b^8*x^(2*n)/n+28*a^2*b^6*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3269
  public void test0099() {
    check( //
        "Integrate[x^(-1+1/3*n)/(a+b*x^n)^(1/3), x]", //
        "-3/2*Log[b^(1/3)*x^(1/3*n)-(a+b*x^n)^(1/3)]/(b^(1/3)*n)+ArcTan[(1+2*b^(1/3)*x^(1/3*n)/(a+b*x^n)^(1/3))/Sqrt[3]]*Sqrt[3]/(b^(1/3)*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3471
  public void test0100() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*(c+d*x)^3)^2, x]", //
        "1/4*a^2*(c+d*x)^4/d+2/7*a*b*(c+d*x)^7/d+1/10*b^2*(c+d*x)^10/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3520
  public void test0101() {
    check( //
        "Integrate[1/((c*e+d*e*x)^4*(a+b*(c+d*x)^3)^2), x]", //
        "(-1/3)/(a^2*d*e^4*(c+d*x)^3)-1/3*b/(a^2*d*e^4*(a+b*(c+d*x)^3))-2*b*Log[c+d*x]/(a^3*d*e^4)+2/3*b*Log[a+b*(c+d*x)^3]/(a^3*d*e^4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3709
  public void test0102() {
    check( //
        "Integrate[1/(x^3*(a+b*(c*x^n)^(1/n))), x]", //
        "(-1/2)/(a*x^2)+b*(c*x^n)^(1/n)/(a^2*x^2)+b^2*(c*x^n)^(2/n)*Log[x]/(a^3*x^2)-b^2*(c*x^n)^(2/n)*Log[a+b*(c*x^n)^(1/n)]/(a^3*x^2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3739
  public void test0103() {
    check( //
        "Integrate[1/(1+4*(x^(2*n))^(1/n)), x]", //
        "1/2*x*ArcTan[2*(x^(2*n))^(1/2/n)]/(x^(2*n))^(1/2/n)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:27
  public void test0104() {
    check( //
        "Integrate[(c+d*x^3)^4/(a+b*x^3), x]", //
        "d*(2*b*c-a*d)*(2*b^2*c^2-2*a*b*c*d+a^2*d^2)*x/b^4+1/4*d^2*(6*b^2*c^2-4*a*b*c*d+a^2*d^2)*x^4/b^3+1/7*d^3*(4*b*c-a*d)*x^7/b^2+1/10*d^4*x^10/b+1/3*(b*c-a*d)^4*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(13/3))-1/6*(b*c-a*d)^4*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(13/3))-(b*c-a*d)^4*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(13/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:352
  public void test0105() {
    check( //
        "Integrate[Sqrt[a+b/x]/(c+d/x)^(1/2), x]", //
        "(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b/x]/(Sqrt[a]*Sqrt[c+d/x])]/(c^(3/2)*Sqrt[a])+x*Sqrt[a+b/x]*Sqrt[c+d/x]/c");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:467
  public void test0106() {
    check( //
        "Integrate[(c+d*x^n)^(-1+(-1)/n), x]", //
        "x/(c*(c+d*x^n)^(1/n))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:33
  public void test0107() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^9, x]", //
        "-1/8*a^2*A/x^8-1/5*a*(2*A*b+a*B)/x^5-1/2*b*(A*b+2*a*B)/x^2+b^2*B*x");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:57
  public void test0108() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^14, x]", //
        "-1/13*a^5*A/x^13-1/10*a^4*(5*A*b+a*B)/x^10-5/7*a^3*b*(2*A*b+a*B)/x^7-5/2*a^2*b^2*(A*b+a*B)/x^4-5*a*b^3*(A*b+2*a*B)/x+1/2*b^4*(A*b+5*a*B)*x^2+1/5*b^5*B*x^5");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:81
  public void test0109() {
    check( //
        "Integrate[(A+B*x^3)/(x^6*(a+b*x^3)), x]", //
        "-1/5*A/(a*x^5)+1/2*(A*b-a*B)/(a^2*x^2)+1/3*b^(2/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/a^(8/3)-1/6*b^(2/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(8/3)-b^(2/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(8/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:103
  public void test0110() {
    check( //
        "Integrate[x^5*(A+B*x^3)/(a+b*x^3)^3, x]", //
        "1/6*a*(A*b-a*B)/(b^3*(a+b*x^3)^2)+1/3*(-A*b+2*a*B)/(b^3*(a+b*x^3))+1/3*B*Log[a+b*x^3]/b^3");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:133
  public void test0111() {
    check( //
        "Integrate[x/((a+b*x^3)*(c+d*x^3)), x]", //
        "-1/3*b^(1/3)*Log[a^(1/3)+b^(1/3)*x]/(a^(1/3)*(b*c-a*d))+1/3*d^(1/3)*Log[c^(1/3)+d^(1/3)*x]/(c^(1/3)*(b*c-a*d))+1/6*b^(1/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(1/3)*(b*c-a*d))-1/6*d^(1/3)*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(c^(1/3)*(b*c-a*d))-b^(1/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(1/3)*(b*c-a*d)*Sqrt[3])+d^(1/3)*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(c^(1/3)*(b*c-a*d)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:165
  public void test0112() {
    check( //
        "Integrate[(a+b*x^3)*(A+B*x^3)/x^(7/2), x]", //
        "-2/5*a*A/x^(5/2)+2/7*b*B*x^(7/2)+2*(A*b+a*B)*Sqrt[x]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:318
  public void test0113() {
    check( //
        "Integrate[(1+x^6)/(x-x^7), x]", //
        "Log[x]-1/3*Log[1-x^6]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2561
  public void test0114() {
    check( //
        "Integrate[x^4/(4+4*x+x^2), x]", //
        "12*x-2*x^2+1/3*x^3+(-16)/(2+x)-32*Log[2+x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2888
  public void test0115() {
    check( //
        "Integrate[(5-x)*(2+5*x+3*x^2)*Sqrt[3+2*x], x]", //
        "65/24*(3+2*x)^(3/2)-109/40*(3+2*x)^(5/2)+47/56*(3+2*x)^(7/2)-1/24*(3+2*x)^(9/2)");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2904
  public void test0116() {
    check( //
        "Integrate[(5-x)*(2+5*x+3*x^2)^3*Sqrt[3+2*x], x]", //
        "1625/384*(3+2*x)^(3/2)-1585/128*(3+2*x)^(5/2)+16005/896*(3+2*x)^(7/2)-17201/1152*(3+2*x)^(9/2)+10475/1408*(3+2*x)^(11/2)-3519/1664*(3+2*x)^(13/2)+189/640*(3+2*x)^(15/2)-27/2176*(3+2*x)^(17/2)");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:86
  public void test0117() {
    check( //
        "Integrate[Sqrt[1-x^2]/(1+x^2), x]", //
        "-ArcSin[x]+ArcTan[x*Sqrt[2]/Sqrt[1-x^2]]*Sqrt[2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:105
  public void test0118() {
    check( //
        "Integrate[x^3*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "1/4*a*(A*b-a*B)/(b^3*(a+b*x^2)^2)+1/2*(-A*b+2*a*B)/(b^3*(a+b*x^2))+1/2*B*Log[a+b*x^2]/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:753
  public void test0119() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^6*(c+d*x^2)^(5/2)), x]", //
        "-1/5*a^2/(c*x^5*(c+d*x^2)^(3/2))-2/15*a*(5*b*c-4*a*d)/(c^2*x^3*(c+d*x^2)^(3/2))+1/5*(-5*b^2*c^2+4*a*d*(5*b*c-4*a*d))/(c^3*x*(c+d*x^2)^(3/2))-4/15*d*(5*b^2*c^2-4*a*d*(5*b*c-4*a*d))*x/(c^4*(c+d*x^2)^(3/2))-8/15*d*(5*b^2*c^2-4*a*d*(5*b*c-4*a*d))*x/(c^5*Sqrt[c+d*x^2])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:126
  public void test0120() {
    check( //
        "Integrate[(a+b*x^2)^3*(A+B*x+C*x^2+D*x^3)/x^2, x]", //
        "-a^3*A/x+a^2*(3*A*b+a*C)*x+3/2*a^2*b*B*x^2+a*b*(A*b+a*C)*x^3+3/4*a*b^2*B*x^4+1/5*b^2*(A*b+3*a*C)*x^5+1/6*b^3*B*x^6+1/7*b^3*C*x^7+1/8*D*(a+b*x^2)^4/b+a^3*B*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:419
  public void test0121() {
    check( //
        "Integrate[1/(x^3*(a-b*x^3)), x]", //
        "(-1/2)/(a*x^2)-1/3*b^(2/3)*Log[a^(1/3)-b^(1/3)*x]/a^(5/3)+1/6*b^(2/3)*Log[a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(5/3)+b^(2/3)*ArcTan[(a^(1/3)+2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1420
  public void test0122() {
    check( //
        "Integrate[1/(x^6*(2*b+b*x^5)), x]", //
        "(-1/10)/(b*x^5)-1/4*Log[x]/b+1/20*Log[2+x^5]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2076
  public void test0123() {
    check( //
        "Integrate[1/((a+b/x)^(1/2)*x^(1/2)), x]", //
        "2*Sqrt[a+b/x]*Sqrt[x]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2677
  public void test0124() {
    check( //
        "Integrate[x^m*(a+b*Sqrt[x]), x]", //
        "a*x^(1+m)/(1+m)+2*b*x^(3/2+m)/(3+2*m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3081
  public void test0125() {
    check( //
        "Integrate[x^(-1+2*n)*(a+b*x^n)^2, x]", //
        "1/2*a^2*x^(2*n)/n+2/3*a*b*x^(3*n)/n+1/4*b^2*x^(4*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3107
  public void test0126() {
    check( //
        "Integrate[x^(-1-n)*(a+b*x^n)^5, x]", //
        "-a^5/(n*x^n)+10*a^3*b^2*x^n/n+5*a^2*b^3*x^(2*n)/n+5/3*a*b^4*x^(3*n)/n+1/4*b^5*x^(4*n)/n+5*a^4*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3133
  public void test0127() {
    check( //
        "Integrate[x^(-1-7*n)*(a+b*x^n)^8, x]", //
        "-1/7*a^8/(n*x^(7*n))-4/3*a^7*b/(n*x^(6*n))-28/5*a^6*b^2/(n*x^(5*n))-14*a^5*b^3/(n*x^(4*n))-70/3*a^4*b^4/(n*x^(3*n))-28*a^3*b^5/(n*x^(2*n))-28*a^2*b^6/(n*x^n)+b^8*x^n/n+8*a*b^7*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3188
  public void test0128() {
    check( //
        "Integrate[x^(-1-3*n)/(a+b*x^n)^2, x]", //
        "(-1/3)/(a^2*n*x^(3*n))+b/(a^3*n*x^(2*n))-3*b^2/(a^4*n*x^n)-b^3/(a^4*n*(a+b*x^n))-4*b^3*Log[x]/a^5+4*b^3*Log[a+b*x^n]/(a^5*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3270
  public void test0129() {
    check( //
        "Integrate[x^(-1-2/3*n)*(a+b*x^n)^(2/3), x]", //
        "-3/2*(a+b*x^n)^(2/3)/(n*x^(2/3*n))-3/2*b^(2/3)*Log[b^(1/3)*x^(1/3*n)-(a+b*x^n)^(1/3)]/n+b^(2/3)*ArcTan[(1+2*b^(1/3)*x^(1/3*n)/(a+b*x^n)^(1/3))/Sqrt[3]]*Sqrt[3]/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3304
  public void test0130() {
    check( //
        "Integrate[1/(a+b*x^n)^((1+n)/n), x]", //
        "x/(a*(a+b*x^n)^(1/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3472
  public void test0131() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*(c+d*x)^3)^3, x]", //
        "1/4*a^3*(c+d*x)^4/d+3/7*a^2*b*(c+d*x)^7/d+3/10*a*b^2*(c+d*x)^10/d+1/13*b^3*(c+d*x)^13/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3500
  public void test0132() {
    check( //
        "Integrate[1/(a+b*(c+d*x)^3)^3, x]", //
        "1/6*(c+d*x)/(a*d*(a+b*(c+d*x)^3)^2)+5/18*(c+d*x)/(a^2*d*(a+b*(c+d*x)^3))+5/27*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(8/3)*b^(1/3)*d)-5/54*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(8/3)*b^(1/3)*d)-5/9*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(8/3)*b^(1/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3776
  public void test0133() {
    check( //
        "Integrate[Sqrt[2+1/x+Sqrt[1/x]], x]", //
        "7/8*ArcTanh[1/2*(4+Sqrt[1/x])/(Sqrt[2]*Sqrt[2+1/x+Sqrt[1/x]])]/Sqrt[2]+1/4*x*(4+Sqrt[1/x])*Sqrt[2+1/x+Sqrt[1/x]]");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:28
  public void test0134() {
    check( //
        "Integrate[(c+d*x^3)^3/(a+b*x^3), x]", //
        "d*(3*b^2*c^2-3*a*b*c*d+a^2*d^2)*x/b^3+1/4*d^2*(3*b*c-a*d)*x^4/b^2+1/7*d^3*x^7/b+1/3*(b*c-a*d)^3*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(10/3))-1/6*(b*c-a*d)^3*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(10/3))-(b*c-a*d)^3*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(10/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:353
  public void test0135() {
    check( //
        "Integrate[Sqrt[a+b/x]/(c+d/x)^(3/2), x]", //
        "(b*c-3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b/x]/(Sqrt[a]*Sqrt[c+d/x])]/(c^(5/2)*Sqrt[a])+(a+b/x)^(3/2)*x/(a*c*Sqrt[c+d/x])-(b*c-3*a*d)*Sqrt[a+b/x]/(a*c^2*Sqrt[c+d/x])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:513
  public void test0136() {
    check( //
        "Integrate[x^2*(a+b*x^2)/(Sqrt[-c+d*x]*Sqrt[c+d*x]), x]", //
        "1/4*c^2*(3*b*c^2+4*a*d^2)*ArcTanh[Sqrt[-c+d*x]/Sqrt[c+d*x]]/d^5+1/8*(3*b*c^2+4*a*d^2)*x*Sqrt[-c+d*x]*Sqrt[c+d*x]/d^4+1/4*b*x^3*Sqrt[-c+d*x]*Sqrt[c+d*x]/d^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:58
  public void test0137() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^15, x]", //
        "-1/14*a^5*A/x^14-1/11*a^4*(5*A*b+a*B)/x^11-5/8*a^3*b*(2*A*b+a*B)/x^8-2*a^2*b^2*(A*b+a*B)/x^5-5/2*a*b^3*(A*b+2*a*B)/x^2+b^4*(A*b+5*a*B)*x+1/4*b^5*B*x^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:104
  public void test0138() {
    check( //
        "Integrate[x^2*(A+B*x^3)/(a+b*x^3)^3, x]", //
        "-1/6*(A+B*x^3)^2/((A*b-a*B)*(a+b*x^3)^2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:134
  public void test0139() {
    check( //
        "Integrate[1/((a+b*x^3)*(c+d*x^3)), x]", //
        "1/3*b^(2/3)*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*(b*c-a*d))-1/3*d^(2/3)*Log[c^(1/3)+d^(1/3)*x]/(c^(2/3)*(b*c-a*d))-1/6*b^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*(b*c-a*d))+1/6*d^(2/3)*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(c^(2/3)*(b*c-a*d))-b^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*(b*c-a*d)*Sqrt[3])+d^(2/3)*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(c^(2/3)*(b*c-a*d)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:408
  public void test0140() {
    check( //
        "Integrate[x/((b*x^3+2*a*(5-3*Sqrt[3]))*Sqrt[-a-b*x^3]), x]", //
        "1/2*ArcTan[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1-Sqrt[3])/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2+Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/3*ArcTan[(1+Sqrt[3])*Sqrt[-a-b*x^3]/(3^(3/4)*Sqrt[2]*Sqrt[a])]*(2+Sqrt[3])/(3^(3/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/3*ArcTanh[3^(1/4)*a^(1/6)*(-2*b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2+Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])-1/6*ArcTanh[3^(1/4)*a^(1/6)*(a^(1/3)+b^(1/3)*x)*(1+Sqrt[3])/(Sqrt[2]*Sqrt[-a-b*x^3])]*(2+Sqrt[3])/(3^(1/4)*a^(5/6)*b^(2/3)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1259
  public void test0141() {
    check( //
        "Integrate[x^31*Sqrt[1+x^16]/(1-x^16), x]", //
        "-1/24*(1+x^16)^(3/2)+1/4*ArcTanh[Sqrt[1+x^16]/Sqrt[2]]/Sqrt[2]-1/8*Sqrt[1+x^16]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:50
  public void test0142() {
    check( //
        "Integrate[(8*C+(-b)^(2/3)*C*x^2)/(-8+b*x^3), x]", //
        "-C*Log[2+(-b)^(1/3)*x]/(-b)^(1/3)+2*C*ArcTan[(1-(-b)^(1/3)*x)/Sqrt[3]]/((-b)^(1/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:71
  public void test0143() {
    check( //
        "Integrate[(1+x+4*x^2)/(1-x^3), x]", //
        "-2*Log[1-x]-Log[1+x+x^2]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:134
  public void test0144() {
    check( //
        "Integrate[(1-x-Sqrt[3])/Sqrt[-1+x^3], x]", //
        "2*Sqrt[-1+x^3]/(1-x-Sqrt[3])+4*3^(1/4)*(1-x)*EllipticF[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]/(Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])-3^(1/4)*(1-x)*EllipticE[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]*Sqrt[2+Sqrt[3]]/(Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:160
  public void test0145() {
    check( //
        "Integrate[(c+d*x)/Sqrt[-1+x^3], x]", //
        "-2*d*Sqrt[-1+x^3]/(1-x-Sqrt[3])-2*(1-x)*EllipticF[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*(c+d+d*Sqrt[3])*Sqrt[2-Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]/(3^(1/4)*Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])+3^(1/4)*d*(1-x)*EllipticE[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]*Sqrt[2+Sqrt[3]]/(Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:193
  public void test0146() {
    check( //
        "Integrate[(c+d*x+e*x^2)/(a+b*x^4)^4, x]", //
        "1/12*x*(c+d*x+e*x^2)/(a*(a+b*x^4)^3)+1/96*x*(11*c+10*d*x+9*e*x^2)/(a^2*(a+b*x^4)^2)+1/384*x*(77*c+60*d*x+45*e*x^2)/(a^3*(a+b*x^4))+5/32*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(7/2)*Sqrt[b])-1/512*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-15*e*Sqrt[a]+77*c*Sqrt[b])/(a^(15/4)*b^(3/4)*Sqrt[2])+1/512*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-15*e*Sqrt[a]+77*c*Sqrt[b])/(a^(15/4)*b^(3/4)*Sqrt[2])-1/256*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(15*e*Sqrt[a]+77*c*Sqrt[b])/(a^(15/4)*b^(3/4)*Sqrt[2])+1/256*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(15*e*Sqrt[a]+77*c*Sqrt[b])/(a^(15/4)*b^(3/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:220
  public void test0147() {
    check( //
        "Integrate[b*x/(2+3*x^4), x]", //
        "1/2*b*ArcTan[x^2*Sqrt[3/2]]/Sqrt[6]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:522
  public void test0148() {
    check( //
        "Integrate[x^2*(a+b*x^3)^3*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5), x]", //
        "1/4*a^3*d*x^4+1/5*a^3*e*x^5+1/6*a^3*f*x^6+1/7*a^2*(3*b*d+a*g)*x^7+1/8*a^2*(3*b*e+a*h)*x^8+1/3*a^2*b*f*x^9+3/10*a*b*(b*d+a*g)*x^10+3/11*a*b*(b*e+a*h)*x^11+1/4*a*b^2*f*x^12+1/13*b^2*(b*d+3*a*g)*x^13+1/14*b^2*(b*e+3*a*h)*x^14+1/15*b^3*f*x^15+1/16*b^3*g*x^16+1/17*b^3*h*x^17+1/12*c*(a+b*x^3)^4/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:542
  public void test0149() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3)^2, x]", //
        "f*x/b^2+1/2*g*x^2/b^2+1/3*h*x^3/b^2-1/3*x*(b*c-a*f+(b*d-a*g)*x+(b*e-a*h)*x^2)/(b^2*(a+b*x^3))+1/9*(b^(1/3)*(b*c-4*a*f)-a^(1/3)*(2*b*d-5*a*g))*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(8/3))-1/18*(b^(1/3)*(b*c-4*a*f)-a^(1/3)*(2*b*d-5*a*g))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(8/3))+1/3*(b*e-2*a*h)*Log[a+b*x^3]/b^3-1/3*(b^(4/3)*c+2*a^(1/3)*b*d-4*a*b^(1/3)*f-5*a^(4/3)*g)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(8/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:568
  public void test0150() {
    check( //
        "Integrate[(c+d*x+e*x^2)/Sqrt[a+b*x^3], x]", //
        "2/3*e*Sqrt[a+b*x^3]/b+2*d*Sqrt[a+b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-3^(1/4)*a^(1/3)*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+2*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(b^(1/3)*c-a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:643
  public void test0151() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3)/(a+b*x^4)^3, x]", //
        "1/8*(-c-d*x-e*x^2-f*x^3)/(b*(a+b*x^4)^2)+1/32*x*(d+2*e*x+3*f*x^2)/(a*b*(a+b*x^4))+1/16*e*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(3/2))-3/128*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-f*Sqrt[a]+d*Sqrt[b])/(a^(7/4)*b^(7/4)*Sqrt[2])+3/128*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-f*Sqrt[a]+d*Sqrt[b])/(a^(7/4)*b^(7/4)*Sqrt[2])-3/64*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(f*Sqrt[a]+d*Sqrt[b])/(a^(7/4)*b^(7/4)*Sqrt[2])+3/64*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(f*Sqrt[a]+d*Sqrt[b])/(a^(7/4)*b^(7/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:683
  public void test0152() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^14, x]", //
        "-1/8580*(660*c/x^13+715*d/x^12+780*e/x^11+858*f/x^10)*(a+b*x^4)^(3/2)+1/32*b^3*d*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/a^(3/2)-1/240240*b*(12320*c/x^9+15015*d/x^8+18720*e/x^7+24024*f/x^6)*Sqrt[a+b*x^4]-4/195*b^2*c*Sqrt[a+b*x^4]/(a*x^5)-1/32*b^2*d*Sqrt[a+b*x^4]/(a*x^4)-4/77*b^2*e*Sqrt[a+b*x^4]/(a*x^3)-1/10*b^2*f*Sqrt[a+b*x^4]/(a*x^2)+4/65*b^3*c*Sqrt[a+b*x^4]/(a^2*x)-4/65*b^(7/2)*c*x*Sqrt[a+b*x^4]/(a^2*(Sqrt[a]+x^2*Sqrt[b]))+4/65*b^(13/4)*c*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(7/4)*Sqrt[a+b*x^4])-2/5005*b^(11/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(65*e*Sqrt[a]+77*c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(7/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:34
  public void test0153() {
    check( //
        "Integrate[x^2/(a*x+b*x^3)^2, x]", //
        "1/2*x/(a*(a+b*x^2))+1/2*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:105
  public void test0154() {
    check( //
        "Integrate[x^(25/2)/(a*x+b*x^3)^(9/2), x]", //
        "-1/7*x^(21/2)/(b*(a*x+b*x^3)^(7/2))-1/5*x^(15/2)/(b^2*(a*x+b*x^3)^(5/2))-1/3*x^(9/2)/(b^3*(a*x+b*x^3)^(3/2))+ArcTanh[x^(3/2)*Sqrt[b]/Sqrt[a*x+b*x^3]]/b^(9/2)-x^(3/2)/(b^4*Sqrt[a*x+b*x^3])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:161
  public void test0155() {
    check( //
        "Integrate[1/(b*x^(1/2)+a*x)^(3/2), x]", //
        "4*Sqrt[x]/(b*Sqrt[a*x+b*Sqrt[x]])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:235
  public void test0156() {
    check( //
        "Integrate[x*Sqrt[b*x^(2/3)+a*x], x]", //
        "-128/429*b^3*(b*x^(2/3)+a*x)^(3/2)/a^4+2048/15015*b^6*(b*x^(2/3)+a*x)^(3/2)/(a^7*x)-1024/5005*b^5*(b*x^(2/3)+a*x)^(3/2)/(a^6*x^(2/3))+256/1001*b^4*(b*x^(2/3)+a*x)^(3/2)/(a^5*x^(1/3))+48/143*b^2*x^(1/3)*(b*x^(2/3)+a*x)^(3/2)/a^3-24/65*b*x^(2/3)*(b*x^(2/3)+a*x)^(3/2)/a^2+2/5*x*(b*x^(2/3)+a*x)^(3/2)/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:303
  public void test0157() {
    check( //
        "Integrate[x^5/(a*x^2+b*x^3)^2, x]", //
        "a/(b^2*(a+b*x))+Log[a+b*x]/b^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:388
  public void test0158() {
    check( //
        "Integrate[x^9/Sqrt[a*x^2+b*x^5], x]", //
        "16/45*a^2*Sqrt[a*x^2+b*x^5]/(b^3*x)-8/45*a*x^2*Sqrt[a*x^2+b*x^5]/b^2+2/15*x^5*Sqrt[a*x^2+b*x^5]/b");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:450
  public void test0159() {
    check( //
        "Integrate[1/(a*x+b*x)^2, x]", //
        "(-1)/((a+b)^2*x)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:472
  public void test0160() {
    check( //
        "Integrate[1/(b/x^3+a*x), x]", //
        "1/4*Log[b+a*x^4]/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:494
  public void test0161() {
    check( //
        "Integrate[1/(x-Sqrt[x]), x]", //
        "2*Log[1-Sqrt[x]]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:516
  public void test0162() {
    check( //
        "Integrate[(c*x)^(1/2)*(a/x+b*x^n)^(3/2), x]", //
        "2/3*(c*x)^(3/2)*(a/x+b*x^n)^(3/2)/(c*(1+n))-2*a^(3/2)*c*ArcTanh[Sqrt[a]/(Sqrt[x]*Sqrt[a/x+b*x^n])]*Sqrt[x]/((1+n)*Sqrt[c*x])+2*a*Sqrt[c*x]*Sqrt[a/x+b*x^n]/(1+n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:536
  public void test0163() {
    check( //
        "Integrate[1/(c^2*x^2*Sqrt[a/x^2+b*x^n]), x]", //
        "-2*ArcTanh[Sqrt[a]/(x*Sqrt[a/x^2+b*x^n])]/(c^2*(2+n)*Sqrt[a])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:556
  public void test0164() {
    check( //
        "Integrate[1/Sqrt[x^2*(b+a*x^(-2+n))], x]", //
        "2*ArcTanh[x*Sqrt[b]/Sqrt[b*x^2+a*x^n]]/((2-n)*Sqrt[b])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:24
  public void test0165() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/x, x]", //
        "1/4*A*b^2*x^4+1/6*b*(b*B+2*A*c)*x^6+1/8*c*(2*b*B+A*c)*x^8+1/10*B*c^2*x^10");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:44
  public void test0166() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^10, x]", //
        "-1/3*A*b^3/x^3-b^2*(b*B+3*A*c)/x+3*b*c*(b*B+A*c)*x+1/3*c^2*(3*b*B+A*c)*x^3+1/5*B*c^3*x^5");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:64
  public void test0167() {
    check( //
        "Integrate[(A+B*x^2)/(b*x^2+c*x^4), x]", //
        "-A/(b*x)+(b*B-A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/(b^(3/2)*Sqrt[c])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:106
  public void test0168() {
    check( //
        "Integrate[x^7*(A+B*x^2)*Sqrt[b*x^2+c*x^4], x]", //
        "-7/384*b^2*(3*b*B-4*A*c)*(b*x^2+c*x^4)^(3/2)/c^4+7/320*b*(3*b*B-4*A*c)*x^2*(b*x^2+c*x^4)^(3/2)/c^3-1/40*(3*b*B-4*A*c)*x^4*(b*x^2+c*x^4)^(3/2)/c^2+1/12*B*x^6*(b*x^2+c*x^4)^(3/2)/c-7/1024*b^5*(3*b*B-4*A*c)*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]/c^(11/2)+7/1024*b^3*(3*b*B-4*A*c)*(b+2*c*x^2)*Sqrt[b*x^2+c*x^4]/c^5");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:124
  public void test0169() {
    check( //
        "Integrate[x^3*(A+B*x^2)*(b*x^2+c*x^4)^(3/2), x]", //
        "1/384*b*(7*b*B-12*A*c)*(b+2*c*x^2)*(b*x^2+c*x^4)^(3/2)/c^3-1/120*(7*b*B-12*A*c-10*B*c*x^2)*(b*x^2+c*x^4)^(5/2)/c^2+1/1024*b^5*(7*b*B-12*A*c)*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]/c^(9/2)-1/1024*b^3*(7*b*B-12*A*c)*(b+2*c*x^2)*Sqrt[b*x^2+c*x^4]/c^4");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:142
  public void test0170() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^(3/2)/x^8, x]", //
        "-1/8*(4*b*B+A*c)*(b*x^2+c*x^4)^(3/2)/(b*x^5)-1/4*A*(b*x^2+c*x^4)^(5/2)/(b*x^9)-3/8*c*(4*b*B+A*c)*ArcTanh[x*Sqrt[b]/Sqrt[b*x^2+c*x^4]]/Sqrt[b]+3/8*c*(4*b*B+A*c)*Sqrt[b*x^2+c*x^4]/(b*x)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:186
  public void test0171() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)/Sqrt[x], x]", //
        "2/5*A*b*x^(5/2)+2/9*(b*B+A*c)*x^(9/2)+2/13*B*c*x^(13/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:204
  public void test0172() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^(5/2), x]", //
        "2/9*A*b^3*x^(9/2)+2/13*b^2*(b*B+3*A*c)*x^(13/2)+6/17*b*c*(b*B+A*c)*x^(17/2)+2/21*c^2*(3*b*B+A*c)*x^(21/2)+2/25*B*c^3*x^(25/2)");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:29
  public void test0173() {
    check( //
        "Integrate[1/(b*x+c*x^2)^(7/2), x]", //
        "-2/5*(b+2*c*x)/(b^2*(b*x+c*x^2)^(5/2))+32/15*c*(b+2*c*x)/(b^4*(b*x+c*x^2)^(3/2))-256/15*c^2*(b+2*c*x)/(b^6*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:78
  public void test0174() {
    check( //
        "Integrate[1/(a+c*x^2)^3, x]", //
        "1/4*x/(a*(a+c*x^2)^2)+3/8*x/(a^2*(a+c*x^2))+3/8*ArcTan[x*Sqrt[c]/Sqrt[a]]/(a^(5/2)*Sqrt[c])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:49
  public void test0175() {
    check( //
        "Integrate[(a*x+b*x^2)^(5/2)/x^12, x]", //
        "-2/17*(a*x+b*x^2)^(7/2)/(a*x^12)+4/51*b*(a*x+b*x^2)^(7/2)/(a^2*x^11)-32/663*b^2*(a*x+b*x^2)^(7/2)/(a^3*x^10)+64/2431*b^3*(a*x+b*x^2)^(7/2)/(a^4*x^9)-256/21879*b^4*(a*x+b*x^2)^(7/2)/(a^5*x^8)+512/153153*b^5*(a*x+b*x^2)^(7/2)/(a^6*x^7)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:69
  public void test0176() {
    check( //
        "Integrate[1/(b*x+c*x^2)^(3/2), x]", //
        "-2*(b+2*c*x)/(b^2*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:93
  public void test0177() {
    check( //
        "Integrate[(b*x+c*x^2)^(1/2)/x^(1/2), x]", //
        "2/3*(b*x+c*x^2)^(3/2)/(c*x^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:113
  public void test0178() {
    check( //
        "Integrate[x^(7/2)/(b*x+c*x^2)^(1/2), x]", //
        "-12/35*b*x^(3/2)*Sqrt[b*x+c*x^2]/c^2+2/7*x^(5/2)*Sqrt[b*x+c*x^2]/c-32/35*b^3*Sqrt[b*x+c*x^2]/(c^4*Sqrt[x])+16/35*b^2*Sqrt[x]*Sqrt[b*x+c*x^2]/c^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:133
  public void test0179() {
    check( //
        "Integrate[(d*x)^m*(b*x+c*x^2)^3, x]", //
        "b^3*(d*x)^(4+m)/(d^4*(4+m))+3*b^2*c*(d*x)^(5+m)/(d^5*(5+m))+3*b*c^2*(d*x)^(6+m)/(d^6*(6+m))+c^3*(d*x)^(7+m)/(d^7*(7+m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:186
  public void test0180() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(3/2)/x^2, x]", //
        "-a^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x*(a+b*x))+3*a*b^2*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+1/2*b^3*x^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+3*a^2*b*Log[x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:204
  public void test0181() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(5/2)/x^5, x]", //
        "-1/4*a^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^4*(a+b*x))-5/3*a^4*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^3*(a+b*x))-5*a^3*b^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^2*(a+b*x))-10*a^2*b^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x*(a+b*x))+b^5*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+5*a*b^4*Log[x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:224
  public void test0182() {
    check( //
        "Integrate[x^3/(a^2+2*a*b*x+b^2*x^2)^(3/2), x]", //
        "-3*a^2/(b^4*Sqrt[a^2+2*a*b*x+b^2*x^2])+1/2*a^3/(b^4*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])+x*(a+b*x)/(b^3*Sqrt[a^2+2*a*b*x+b^2*x^2])-3*a*(a+b*x)*Log[a+b*x]/(b^4*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:510
  public void test0183() {
    check( //
        "Integrate[(d+e*x)^m*(c*d*x+c*e*x^2)^2, x]", //
        "c^2*d^2*(d+e*x)^(3+m)/(e^3*(3+m))-2*c^2*d*(d+e*x)^(4+m)/(e^3*(4+m))+c^2*(d+e*x)^(5+m)/(e^3*(5+m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:548
  public void test0184() {
    check( //
        "Integrate[(d+e*x)*(a+c*x^2)^2, x]", //
        "a^2*d*x+2/3*a*c*d*x^3+1/5*c^2*d*x^5+1/6*e*(a+c*x^2)^3/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:588
  public void test0185() {
    check( //
        "Integrate[(d+e*x)/(a+c*x^2), x]", //
        "1/2*e*Log[a+c*x^2]/c+d*ArcTan[x*Sqrt[c]/Sqrt[a]]/(Sqrt[a]*Sqrt[c])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:628
  public void test0186() {
    check( //
        "Integrate[(d+e*x)*(a+c*x^2)^(3/2), x]", //
        "1/4*d*x*(a+c*x^2)^(3/2)+1/5*e*(a+c*x^2)^(5/2)/c+3/8*a^2*d*ArcTanh[x*Sqrt[c]/Sqrt[a+c*x^2]]/Sqrt[c]+3/8*a*d*x*Sqrt[a+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:648
  public void test0187() {
    check( //
        "Integrate[(a+c*x^2)^(5/2)/(d+e*x)^9, x]", //
        "-5/192*a*c^2*(8*c*d^2-a*e^2)*(a*e-c*d*x)*(a+c*x^2)^(3/2)/((c*d^2+a*e^2)^4*(d+e*x)^4)-1/48*c*(8*c*d^2-a*e^2)*(a*e-c*d*x)*(a+c*x^2)^(5/2)/((c*d^2+a*e^2)^3*(d+e*x)^6)-1/8*e*(a+c*x^2)^(7/2)/((c*d^2+a*e^2)*(d+e*x)^8)-9/56*c*d*e*(a+c*x^2)^(7/2)/((c*d^2+a*e^2)^2*(d+e*x)^7)-5/128*a^3*c^4*(8*c*d^2-a*e^2)*ArcTanh[(a*e-c*d*x)/(Sqrt[c*d^2+a*e^2]*Sqrt[a+c*x^2])]/(c*d^2+a*e^2)^(11/2)-5/128*a^2*c^3*(8*c*d^2-a*e^2)*(a*e-c*d*x)*Sqrt[a+c*x^2]/((c*d^2+a*e^2)^5*(d+e*x)^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:895
  public void test0188() {
    check( //
        "Integrate[(a+b*x)^5/(a^2-b^2*x^2)^3, x]", //
        "2*a^2/(b*(a-b*x)^2)-4*a/(b*(a-b*x))-Log[a-b*x]/b");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:917
  public void test0189() {
    check( //
        "Integrate[(a+b*x)^3*(a^2-b^2*x^2)^(3/2), x]", //
        "3/8*a^3*x*(a^2-b^2*x^2)^(3/2)-3/10*a^2*(a^2-b^2*x^2)^(5/2)/b-3/14*a*(a+b*x)*(a^2-b^2*x^2)^(5/2)/b-1/7*(a+b*x)^2*(a^2-b^2*x^2)^(5/2)/b+9/16*a^7*ArcTan[b*x/Sqrt[a^2-b^2*x^2]]/b+9/16*a^5*x*Sqrt[a^2-b^2*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:957
  public void test0190() {
    check( //
        "Integrate[(d+e*x)^5/Sqrt[d^2-e^2*x^2], x]", //
        "63/8*d^5*ArcTan[e*x/Sqrt[d^2-e^2*x^2]]/e-63/8*d^4*Sqrt[d^2-e^2*x^2]/e-21/8*d^3*(d+e*x)*Sqrt[d^2-e^2*x^2]/e-21/20*d^2*(d+e*x)^2*Sqrt[d^2-e^2*x^2]/e-9/20*d*(d+e*x)^3*Sqrt[d^2-e^2*x^2]/e-1/5*(d+e*x)^4*Sqrt[d^2-e^2*x^2]/e");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:976
  public void test0191() {
    check( //
        "Integrate[1/((d+e*x)^4*(d^2-e^2*x^2)^(5/2)), x]", //
        "8/99*x/(d^6*(d^2-e^2*x^2)^(3/2))+(-1/11)/(d*e*(d+e*x)^4*(d^2-e^2*x^2)^(3/2))+(-7/99)/(d^2*e*(d+e*x)^3*(d^2-e^2*x^2)^(3/2))+(-2/33)/(d^3*e*(d+e*x)^2*(d^2-e^2*x^2)^(3/2))+(-2/33)/(d^4*e*(d+e*x)*(d^2-e^2*x^2)^(3/2))+16/99*x/(d^8*Sqrt[d^2-e^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1000
  public void test0192() {
    check( //
        "Integrate[(c*d^2-c*e^2*x^2)^(1/2)/(d+e*x)^(1/2), x]", //
        "-2/3*(c*d^2-c*e^2*x^2)^(3/2)/(c*e*(d+e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1020
  public void test0193() {
    check( //
        "Integrate[1/((d+e*x)^(1/2)*(c*d^2-c*e^2*x^2)^(1/2)), x]", //
        "-ArcTanh[Sqrt[c*d^2-c*e^2*x^2]/(Sqrt[2]*Sqrt[c]*Sqrt[d]*Sqrt[d+e*x])]*Sqrt[2]/(e*Sqrt[c]*Sqrt[d])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1147
  public void test0194() {
    check( //
        "Integrate[(c*d^2+2*c*d*e*x+c*e^2*x^2)/(d+e*x)^6, x]", //
        "-1/3*c/(e*(d+e*x)^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1169
  public void test0195() {
    check( //
        "Integrate[1/((d+e*x)^3*(c*d^2+2*c*d*e*x+c*e^2*x^2)), x]", //
        "(-1/4)/(c*e*(d+e*x)^4)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1187
  public void test0196() {
    check( //
        "Integrate[(d+e*x)^2/(c*d^2+2*c*d*e*x+c*e^2*x^2)^3, x]", //
        "(-1/3)/(c^3*e*(d+e*x)^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1335
  public void test0197() {
    check( //
        "Integrate[(b*d+2*c*d*x)^8/(a+b*x+c*x^2), x]", //
        "2*(b^2-4*a*c)^3*d^8*(b+2*c*x)+2/3*(b^2-4*a*c)^2*d^8*(b+2*c*x)^3+2/5*(b^2-4*a*c)*d^8*(b+2*c*x)^5+2/7*d^8*(b+2*c*x)^7-2*(b^2-4*a*c)^(7/2)*d^8*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1355
  public void test0198() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)*(a+b*x+c*x^2)^2), x]", //
        "(-1)/((b^2-4*a*c)*d*(a+b*x+c*x^2))+8*c*Log[b+2*c*x]/((b^2-4*a*c)^2*d)-4*c*Log[a+b*x+c*x^2]/((b^2-4*a*c)^2*d)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1377
  public void test0199() {
    check( //
        "Integrate[(b*d+2*c*d*x)^3*(a+b*x+c*x^2)^(1/2), x]", //
        "4/15*(b^2-4*a*c)*d^3*(a+b*x+c*x^2)^(3/2)+2/5*d^3*(b+2*c*x)^2*(a+b*x+c*x^2)^(3/2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1395
  public void test0200() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^4, x]", //
        "-1/6*(a+b*x+c*x^2)^(3/2)/(c*d^4*(b+2*c*x)^3)+1/16*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/(c^(5/2)*d^4)-1/8*Sqrt[a+b*x+c*x^2]/(c^2*d^4*(b+2*c*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1415
  public void test0201() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x)^9, x]", //
        "-5/384*(a+b*x+c*x^2)^(3/2)/(c^2*d^9*(b+2*c*x)^6)-1/16*(a+b*x+c*x^2)^(5/2)/(c*d^9*(b+2*c*x)^8)+5/8192*ArcTan[2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c]]/(c^(7/2)*(b^2-4*a*c)^(3/2)*d^9)-5/2048*Sqrt[a+b*x+c*x^2]/(c^3*d^9*(b+2*c*x)^4)+5/4096*Sqrt[a+b*x+c*x^2]/(c^3*(b^2-4*a*c)*d^9*(b+2*c*x)^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1435
  public void test0202() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)^3*(a+b*x+c*x^2)^(3/2)), x]", //
        "-6*ArcTan[2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c]]*Sqrt[c]/((b^2-4*a*c)^(5/2)*d^3)+(-2)/((b^2-4*a*c)*d^3*(b+2*c*x)^2*Sqrt[a+b*x+c*x^2])-12*c*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)^2*d^3*(b+2*c*x)^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1539
  public void test0203() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^(13/2), x]", //
        "-1/11*(a+b*x+c*x^2)^(3/2)/(c*d*(b*d+2*c*d*x)^(11/2))-3/154*Sqrt[a+b*x+c*x^2]/(c^2*d^3*(b*d+2*c*d*x)^(7/2))+1/77*Sqrt[a+b*x+c*x^2]/(c^2*(b^2-4*a*c)*d^5*(b*d+2*c*d*x)^(3/2))+1/154*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^3*(b^2-4*a*c)^(3/4)*d^(13/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1557
  public void test0204() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x)^(7/2), x]", //
        "-1/5*(a+b*x+c*x^2)^(5/2)/(c*d*(b*d+2*c*d*x)^(5/2))-1/2*(a+b*x+c*x^2)^(3/2)/(c^2*d^3*Sqrt[b*d+2*c*d*x])+3/20*(b*d+2*c*d*x)^(3/2)*Sqrt[a+b*x+c*x^2]/(c^3*d^5)-3/20*(b^2-4*a*c)^(7/4)*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^4*d^(7/2)*Sqrt[a+b*x+c*x^2])+3/20*(b^2-4*a*c)^(7/4)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^4*d^(7/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1577
  public void test0205() {
    check( //
        "Integrate[1/((3-2*x)^(3/2)*Sqrt[1-3*x+x^2]), x]", //
        "2*EllipticE[ArcSin[Sqrt[3-2*x]/5^(1/4)],-1]*Sqrt[-1+3*x-x^2]/(5^(3/4)*Sqrt[1-3*x+x^2])-2*EllipticF[ArcSin[Sqrt[3-2*x]/5^(1/4)],-1]*Sqrt[-1+3*x-x^2]/(5^(3/4)*Sqrt[1-3*x+x^2])-4/5*Sqrt[1-3*x+x^2]/Sqrt[3-2*x]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1597
  public void test0206() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(1/2)/(a+b*x+c*x^2)^(5/2), x]", //
        "-2/3*(b*d+2*c*d*x)^(3/2)/((b^2-4*a*c)*d*(a+b*x+c*x^2)^(3/2))+4*c*(b*d+2*c*d*x)^(3/2)/((b^2-4*a*c)^2*d*Sqrt[a+b*x+c*x^2])-8*c*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/((b^2-4*a*c)^(5/4)*Sqrt[a+b*x+c*x^2])+8*c*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/((b^2-4*a*c)^(5/4)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1704
  public void test0207() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^3/(d+e*x)^4, x]", //
        "15*b^4*(b*d-a*e)^2*x/e^6-1/3*(b*d-a*e)^6/(e^7*(d+e*x)^3)+3*b*(b*d-a*e)^5/(e^7*(d+e*x)^2)-15*b^2*(b*d-a*e)^4/(e^7*(d+e*x))-3*b^5*(b*d-a*e)*(d+e*x)^2/e^7+1/3*b^6*(d+e*x)^3/e^7-20*b^3*(b*d-a*e)^3*Log[d+e*x]/e^7");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1724
  public void test0208() {
    check( //
        "Integrate[1/((d+e*x)*(a^2+2*a*b*x+b^2*x^2)), x]", //
        "(-1)/((b*d-a*e)*(a+b*x))-e*Log[a+b*x]/(b*d-a*e)^2+e*Log[d+e*x]/(b*d-a*e)^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1744
  public void test0209() {
    check( //
        "Integrate[(d+e*x)^2/(a^2+2*a*b*x+b^2*x^2)^3, x]", //
        "-1/5*(b*d-a*e)^2/(b^3*(a+b*x)^5)-1/2*e*(b*d-a*e)/(b^3*(a+b*x)^4)-1/3*e^2/(b^3*(a+b*x)^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1766
  public void test0210() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x+b^2*x^2]/(d+e*x)^2, x]", //
        "(b*d-a*e)*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^2*(a+b*x)*(d+e*x))+b*Log[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^2*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1786
  public void test0211() {
    check( //
        "Integrate[(d+e*x)^5*(a^2+2*a*b*x+b^2*x^2)^(5/2), x]", //
        "1/6*(b*d-a*e)^5*(a+b*x)^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6+5/7*e*(b*d-a*e)^4*(a+b*x)^6*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6+5/4*e^2*(b*d-a*e)^3*(a+b*x)^7*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6+10/9*e^3*(b*d-a*e)^2*(a+b*x)^8*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6+1/2*e^4*(b*d-a*e)*(a+b*x)^9*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6+1/11*e^5*(a+b*x)^10*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^6");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1808
  public void test0212() {
    check( //
        "Integrate[(d+e*x)^2/Sqrt[a^2+2*a*b*x+b^2*x^2], x]", //
        "e*(b*d-a*e)*x*(a+b*x)/(b^2*Sqrt[a^2+2*a*b*x+b^2*x^2])+1/2*(a+b*x)*(d+e*x)^2/(b*Sqrt[a^2+2*a*b*x+b^2*x^2])+(b*d-a*e)^2*(a+b*x)*Log[a+b*x]/(b^3*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1972
  public void test0213() {
    check( //
        "Integrate[(d+e*x)^m*(a^2+2*a*b*x+b^2*x^2)^(1/2), x]", //
        "-(b*d-a*e)*(d+e*x)^(1+m)*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^2*(1+m)*(a+b*x))+b*(d+e*x)^(2+m)*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^2*(2+m)*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2010
  public void test0214() {
    check( //
        "Integrate[(a+b*x)^3*(a*c+(b*c+a*d)*x+b*d*x^2)^2, x]", //
        "1/6*(b*c-a*d)^2*(a+b*x)^6/b^3+2/7*d*(b*c-a*d)*(a+b*x)^7/b^3+1/8*d^2*(a+b*x)^8/b^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2028
  public void test0215() {
    check( //
        "Integrate[(a*c+(b*c+a*d)*x+b*d*x^2)^3/(a+b*x), x]", //
        "1/4*(b*c-a*d)^2*(c+d*x)^4/d^3-2/5*b*(b*c-a*d)*(c+d*x)^5/d^3+1/6*b^2*(c+d*x)^6/d^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2064
  public void test0216() {
    check( //
        "Integrate[(a+b*x)^5/(a*c+(b*c+a*d)*x+b*d*x^2)^3, x]", //
        "-1/2*(b*c-a*d)^2/(d^3*(c+d*x)^2)+2*b*(b*c-a*d)/(d^3*(c+d*x))+b^2*Log[c+d*x]/d^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2177
  public void test0217() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^2, x]", //
        "1/2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(e*(d+e*x))+3/8*(c*d^2-a*e^2)^2*ArcTanh[1/2*(c*d^2+a*e^2+2*c*d*e*x)/(Sqrt[c]*Sqrt[d]*Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])]/(e^(5/2)*Sqrt[c]*Sqrt[d])+3/4*(a-c*d^2/e^2)*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2307
  public void test0218() {
    check( //
        "Integrate[(d+e*x)^(5/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2), x]", //
        "32/315*(c*d^2-a*e^2)^3*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(c^4*d^4*(d+e*x)^(3/2))+2/9*(d+e*x)^(3/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(c*d)+16/105*(c*d^2-a*e^2)^2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(c^3*d^3*Sqrt[d+e*x])+4/21*(c*d^2-a*e^2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)*Sqrt[d+e*x]/(c^2*d^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2324
  public void test0219() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^(13/2), x]", //
        "-1/4*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(e*(d+e*x)^(11/2))+3/64*c^4*d^4*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]/(e^(5/2)*(c*d^2-a*e^2)^(5/2))-1/8*c*d*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e^2*(d+e*x)^(7/2))+1/32*c^2*d^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e^2*(c*d^2-a*e^2)*(d+e*x)^(5/2))+3/64*c^3*d^3*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e^2*(c*d^2-a*e^2)^2*(d+e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2343
  public void test0220() {
    check( //
        "Integrate[1/((d+e*x)^(3/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2)), x]", //
        "c*d*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]/((c*d^2-a*e^2)^(3/2)*Sqrt[e])+Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/((c*d^2-a*e^2)*(d+e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2361
  public void test0221() {
    check( //
        "Integrate[1/((d+e*x)^(7/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)), x]", //
        "1/5/((c*d^2-a*e^2)*(d+e*x)^(7/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))+13/40*c*d/((c*d^2-a*e^2)^2*(d+e*x)^(5/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))+143/240*c^2*d^2/((c*d^2-a*e^2)^3*(d+e*x)^(3/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))+3003/128*c^5*d^5*e^(3/2)*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]/(c*d^2-a*e^2)^(15/2)+429/320*c^3*d^3/((c*d^2-a*e^2)^4*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)*Sqrt[d+e*x])-1001/320*c^4*d^4*Sqrt[d+e*x]/((c*d^2-a*e^2)^5*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))-1001/128*c^4*d^4*e/((c*d^2-a*e^2)^6*Sqrt[d+e*x]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])+3003/128*c^5*d^5*e*Sqrt[d+e*x]/((c*d^2-a*e^2)^7*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2404
  public void test0222() {
    check( //
        "Integrate[a+b*x+c*x^2, x]", //
        "a*x+1/2*b*x^2+1/3*c*x^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2645
  public void test0223() {
    check( //
        "Integrate[Sqrt[a+b*x+c*x^2], x]", //
        "-1/8*(b^2-4*a*c)*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/c^(3/2)+1/4*(b+2*c*x)*Sqrt[a+b*x+c*x^2]/c");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:136
  public void test0224() {
    check( //
        "Integrate[(A+B*x)/(x^4*Sqrt[b*x+c*x^2]), x]", //
        "-2/7*A*Sqrt[b*x+c*x^2]/(b*x^4)-2/35*(7*b*B-6*A*c)*Sqrt[b*x+c*x^2]/(b^2*x^3)+8/105*c*(7*b*B-6*A*c)*Sqrt[b*x+c*x^2]/(b^3*x^2)-16/105*c^2*(7*b*B-6*A*c)*Sqrt[b*x+c*x^2]/(b^4*x)");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2889
  public void test0225() {
    check( //
        "Integrate[(5-x)*(2+5*x+3*x^2)/Sqrt[3+2*x], x]", //
        "-109/24*(3+2*x)^(3/2)+47/40*(3+2*x)^(5/2)-3/56*(3+2*x)^(7/2)+65/8*Sqrt[3+2*x]");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:97
  public void test0226() {
    check( //
        "Integrate[(1+x^2)*(1+2*x^2+x^4)^5/x, x]", //
        "11/2*x^2+55/4*x^4+55/2*x^6+165/4*x^8+231/5*x^10+77/2*x^12+165/7*x^14+165/16*x^16+55/18*x^18+11/20*x^20+1/22*x^22+Log[x]");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:185
  public void test0227() {
    check( //
        "Integrate[(-7*x+4*x^3)/(4-5*x^2+x^4), x]", //
        "1/2*Log[1-x^2]+3/2*Log[4-x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2973
  public void test0228() {
    check( //
        "Integrate[1/(1+x^(1/5)), x]", //
        "-5*x^(1/5)+5/2*x^(2/5)-5/3*x^(3/5)+5/4*x^(4/5)+5*Log[1+x^(1/5)]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:451
  public void test0229() {
    check( //
        "Integrate[1/(a*x+b*x)^3, x]", //
        "(-1/2)/((a+b)^3*x^2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:495
  public void test0230() {
    check( //
        "Integrate[1/(-x^(3/5)+x), x]", //
        "5/2*Log[1-x^(2/5)]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:320
  public void test0231() {
    check( //
        "Integrate[(-3+2*x)/(-x^2+x^3), x]", //
        "(-3)/x-Log[1-x]+Log[x]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:103
  public void test0232() {
    check( //
        "Integrate[1/((a+b*x^2)^(3/2)*(c+d*x^2)), x]", //
        "-d*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/((b*c-a*d)^(3/2)*Sqrt[c])+b*x/(a*(b*c-a*d)*Sqrt[a+b*x^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2974
  public void test0233() {
    check( //
        "Integrate[1/(x^(1/5)*Sqrt[1+x^(4/5)]), x]", //
        "5/2*Sqrt[1+x^(4/5)]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:659
  public void test0234() {
    check( //
        "Integrate[x^11*(1+x)*(1+2*x+x^2)^5, x]", //
        "1/12*x^12+11/13*x^13+55/14*x^14+11*x^15+165/8*x^16+462/17*x^17+77/3*x^18+330/19*x^19+33/4*x^20+55/21*x^21+1/2*x^22+1/23*x^23");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:17
  public void test0235() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^3, x]", //
        "-1/2*a*A/x^2+1/2*b*B*x^2+(A*b+a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:257
  public void test0236() {
    check( //
        "Integrate[x^3/((a+b*x^2)*(c+d*x^2)), x]", //
        "-1/2*a*Log[a+b*x^2]/(b*(b*c-a*d))+1/2*c*Log[c+d*x^2]/(d*(b*c-a*d))");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:32
  public void test0237() {
    check( //
        "Integrate[(1+x)/(1-x^3), x]", //
        "-2/3*Log[1-x]+1/3*Log[1+x+x^2]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:660
  public void test0238() {
    check( //
        "Integrate[x^10*(1+x)*(1+2*x+x^2)^5, x]", //
        "1/11*x^11+11/12*x^12+55/13*x^13+165/14*x^14+22*x^15+231/8*x^16+462/17*x^17+55/3*x^18+165/19*x^19+11/4*x^20+11/21*x^21+1/22*x^22");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:1643
  public void test0239() {
    check( //
        "Integrate[(2+x)/((1+x^2)*Sqrt[3+4*x]), x]", //
        "-ArcTan[2-Sqrt[3+4*x]]+ArcTan[2+Sqrt[3+4*x]]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:669
  public void test0240() {
    check( //
        "Integrate[(a^2+2*a*b*x^2+b^2*x^4)^(5/2)/x^21, x]", //
        "-1/20*a^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^20*(a+b*x^2))-5/18*a^4*b*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^18*(a+b*x^2))-5/8*a^3*b^2*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^16*(a+b*x^2))-5/7*a^2*b^3*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^14*(a+b*x^2))-5/12*a*b^4*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^12*(a+b*x^2))-1/10*b^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^10*(a+b*x^2))");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:943
  public void test0241() {
    check( //
        "Integrate[(a+b*x^2+c*x^4)^3/x^3, x]", //
        "-1/2*a^3/x^2+3/2*a*(b^2+a*c)*x^2+1/4*b*(b^2+6*a*c)*x^4+1/2*c*(b^2+a*c)*x^6+3/8*b*c^2*x^8+1/10*c^3*x^10+3*a^2*b*Log[x]");
  }

  // 1.3.2 Algebraic functions.input:1301
  public void test0242() {
    check( //
        "Integrate[Sqrt[(4-x)*x], x]", //
        "-2*ArcSin[1-1/2*x]-1/2*(2-x)*Sqrt[4*x-x^2]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:526
  public void test0243() {
    check( //
        "Integrate[(a^2+2*a*b*x^2+b^2*x^4)^3/x^13, x]", //
        "-1/12*a^6/x^12-3/5*a^5*b/x^10-15/8*a^4*b^2/x^8-10/3*a^3*b^3/x^6-15/4*a^2*b^4/x^4-3*a*b^5/x^2+b^6*Log[x]");
  }

  // 1.3.1 Rational functions.input:414
  public void test0244() {
    check( //
        "Integrate[(2+x)/((1+x^2)*(4+x^2)), x]", //
        "-1/3*ArcTan[1/2*x]+2/3*ArcTan[x]+1/6*Log[1+x^2]-1/6*Log[4+x^2]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:31
  public void test0245() {
    check( //
        "Integrate[1/(3+4*x^2+x^4), x]", //
        "1/2*ArcTan[x]-1/2*ArcTan[x/Sqrt[3]]/Sqrt[3]");
  }

  // 1.3.1 Rational functions.input:628
  public void test0246() {
    check( //
        "Integrate[1/((2+x)*(1+x^2)), x]", //
        "2/5*ArcTan[x]+1/5*Log[2+x]-1/10*Log[1+x^2]");
  }

  // 1.3.2 Algebraic functions.input:18
  public void test0247() {
    check( //
        "Integrate[1/((c+d*x)*Sqrt[c^3+4*d^3*x^3]), x]", //
        "2/3*ArcTan[(c+2*d*x)*Sqrt[3]*Sqrt[c]/Sqrt[c^3+4*d^3*x^3]]/(c^(3/2)*d*Sqrt[3])+2/3*2^(1/3)*(c+2^(2/3)*d*x)*EllipticF[ArcSin[(2^(2/3)*d*x+c*(1-Sqrt[3]))/(2^(2/3)*d*x+c*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(c^2-2^(2/3)*c*d*x+2*2^(1/3)*d^2*x^2)/(2^(2/3)*d*x+c*(1+Sqrt[3]))^2]/(3^(1/4)*c*d*Sqrt[c^3+4*d^3*x^3]*Sqrt[c*(c+2^(2/3)*d*x)/(2^(2/3)*d*x+c*(1+Sqrt[3]))^2])");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:131
  public void test0248() {
    check( //
        "Integrate[x^5*(a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^p, x]", //
        "1/4*a^2*(a+b*x^2)^2*(a^2+2*a*b*x^2+b^2*x^4)^p/(b^3*(1+p))-a*(a+b*x^2)^3*(a^2+2*a*b*x^2+b^2*x^4)^p/(b^3*(3+2*p))+1/4*(a+b*x^2)^4*(a^2+2*a*b*x^2+b^2*x^4)^p/(b^3*(2+p))");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:617
  public void test0249() {
    check( //
        "Integrate[(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p*x, x]", //
        "-3*a^6*(1+b*x^(1/3)/a)*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(1+2*p))+15/2*a^6*(1+b*x^(1/3)/a)^2*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(1+p))-30*a^6*(1+b*x^(1/3)/a)^3*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(3+2*p))+15*a^6*(1+b*x^(1/3)/a)^4*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(2+p))-15*a^6*(1+b*x^(1/3)/a)^5*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(5+2*p))+3/2*a^6*(1+b*x^(1/3)/a)^6*(a^2+2*a*b*x^(1/3)+b^2*x^(2/3))^p/(b^6*(3+p))");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:114
  public void test0250() {
    check( //
        "Integrate[(1-x^2)/(1+x^2+x^4), x]", //
        "-1/2*Log[1-x+x^2]+1/2*Log[1+x+x^2]");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:153
  public void test0251() {
    check( //
        "Integrate[(1+2*x)*(1+3*x+4*x^2)/(2+3*x^2)^(3/2), x]", //
        "10/3*ArcSinh[x*Sqrt[3/2]]/Sqrt[3]+1/18*(2-51*x)/Sqrt[2+3*x^2]+8/9*Sqrt[2+3*x^2]");
  }

  // 1.3.1 Rational functions.input:386
  public void test0252() {
    check( //
        "Integrate[1/(-18+27*x-7*x^2-3*x^3+x^4), x]", //
        "1/8*Log[1-x]-1/5*Log[2-x]+1/12*Log[3-x]-1/120*Log[3+x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:111
  public void test0253() {
    check( //
        "Integrate[1/(x*Sqrt[a^2+2*a*b*x^3+b^2*x^6]), x]", //
        "(a+b*x^3)*Log[x]/(a*Sqrt[a^2+2*a*b*x^3+b^2*x^6])-1/3*(a+b*x^3)*Log[a+b*x^3]/(a*Sqrt[a^2+2*a*b*x^3+b^2*x^6])");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:92
  public void test0254() {
    check( //
        "Integrate[x^4*(1+x^2)*(1+2*x^2+x^4)^5, x]", //
        "1/5*x^5+11/7*x^7+55/9*x^9+15*x^11+330/13*x^13+154/5*x^15+462/17*x^17+330/19*x^19+55/7*x^21+55/23*x^23+11/25*x^25+1/27*x^27");
  }

  // 1.3.2 Algebraic functions.input:85
  public void test0255() {
    check( //
        "Integrate[(2+3*x)/((2^(2/3)+x)*Sqrt[1+x^3]), x]", //
        "2/3*(2-3*2^(2/3))*ArcTan[(1+2^(1/3)*x)*Sqrt[3]/Sqrt[1+x^3]]/Sqrt[3]+2/3*(3+2*2^(1/3))*(1+x)*EllipticF[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(3^(1/4)*Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])");
  }

  // 1.3.1 Rational functions.input:484
  public void test0256() {
    check( //
        "Integrate[(3+x+x^2+x^3)/((1+x^2)*(3+x^2)), x]", //
        "ArcTan[x]+1/2*Log[3+x^2]");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:286
  public void test0257() {
    check( //
        "Integrate[1/((a+b*x^2)^(3/2)*Sqrt[a^2-b^2*x^4]), x]", //
        "1/4*x*(a-b*x^2)/(a^2*Sqrt[a+b*x^2]*Sqrt[a^2-b^2*x^4])+3/4*ArcTan[x*Sqrt[2]*Sqrt[b]/Sqrt[a-b*x^2]]*Sqrt[a-b*x^2]*Sqrt[a+b*x^2]/(a^2*Sqrt[2]*Sqrt[b]*Sqrt[a^2-b^2*x^4])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2165
  public void test0258() {
    check( //
        "Integrate[(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^2/(d+e*x)^3, x]", //
        "10*b^3*(b*d-a*e)^2*x/e^5+1/2*(b*d-a*e)^5/(e^6*(d+e*x)^2)-5*b*(b*d-a*e)^4/(e^6*(d+e*x))-5/2*b^4*(b*d-a*e)*(d+e*x)^2/e^6+1/3*b^5*(d+e*x)^3/e^6-10*b^2*(b*d-a*e)^3*Log[d+e*x]/e^6");
  }

  // 1.3.2 Algebraic functions.input:693
  public void test0259() {
    check( //
        "Integrate[(x-Sqrt[a+x^2])^n/(a+x^2)^(1/2), x]", //
        "-(x-Sqrt[a+x^2])^n/n");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:352
  public void test0260() {
    check( //
        "Integrate[(b*x^2+c*x^4)^2/x^(7/2), x]", //
        "2/3*b^2*x^(3/2)+4/7*b*c*x^(7/2)+2/11*c^2*x^(11/2)");
  }

  // 1.3.1 Rational functions.input:413
  public void test0261() {
    check( //
        "Integrate[(3+4*x)/((1+x^2)*(2+x^2)), x]", //
        "3*ArcTan[x]+2*Log[1+x^2]-2*Log[2+x^2]-3*ArcTan[x/Sqrt[2]]/Sqrt[2]");
  }

  // 1.2.1.4 (d+e x)^m (f+g x)^n (a+b x+c x^2)^p.input:261
  public void test0262() {
    check( //
        "Integrate[x^3/((d+e*x)^4*(d^2-e^2*x^2)^(7/2)), x]", //
        "-24/5005*x/(d^3*e^3*(d^2-e^2*x^2)^(5/2))+1/13*d^2/(e^4*(d+e*x)^4*(d^2-e^2*x^2)^(5/2))-30/143*d/(e^4*(d+e*x)^3*(d^2-e^2*x^2)^(5/2))+21/143/(e^4*(d+e*x)^2*(d^2-e^2*x^2)^(5/2))+4/1001/(d*e^4*(d+e*x)*(d^2-e^2*x^2)^(5/2))-32/5005*x/(d^5*e^3*(d^2-e^2*x^2)^(3/2))-64/5005*x/(d^7*e^3*Sqrt[d^2-e^2*x^2])");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:151
  public void test0263() {
    check( //
        "Integrate[(d*x)^m*(a^2+2*a*b*x^3+b^2*x^6)^(1/2), x]", //
        "a*(d*x)^(1+m)*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(d*(1+m)*(a+b*x^3))+b*(d*x)^(4+m)*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(d^4*(4+m)*(a+b*x^3))");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2219
  public void test0264() {
    check( //
        "Integrate[(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2]/(d+e*x)^3, x]", //
        "-1/2*(b*d-a*e)^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^3*(a+b*x)*(d+e*x)^2)+2*b*(b*d-a*e)*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^3*(a+b*x)*(d+e*x))+b^2*Log[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^3*(a+b*x))");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:1137
  public void test0265() {
    check( //
        "Integrate[x/Sqrt[2+2*a-2*(1+a)+c*x^4], x]", //
        "x^2*Log[x]/Sqrt[c*x^4]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:622
  public void test0266() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x^2+b^2*x^4]/x^6, x]", //
        "-1/5*a*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^5*(a+b*x^2))-1/3*b*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^3*(a+b*x^2))");
  }

  // 1.2.4.2 (d x)^m (a x^q+b x^n+c x^(2 n-q))^p.input:34
  public void test0267() {
    check( //
        "Integrate[x^6/(a*x^2+b*x^3+c*x^4)^2, x]", //
        "x*(2*a+b*x)/((b^2-4*a*c)*(a+b*x+c*x^2))+4*a*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(3/2)");
  }

  // 1.3.1 Rational functions.input:461
  public void test0268() {
    check( //
        "Integrate[(-1+x)^4*x^4/(1+x^2), x]", //
        "4*x-4/3*x^3+x^5-2/3*x^6+1/7*x^7-4*ArcTan[x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:50
  public void test0269() {
    check( //
        "Integrate[(a^2+2*a*b*x^3+b^2*x^6)^(3/2)/x^3, x]", //
        "-1/2*a^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^2*(a+b*x^3))+3*a^2*b*x*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)+3/4*a*b^2*x^4*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)+1/7*b^3*x^7*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)");
  }

  // 1.2.4.2 (d x)^m (a x^q+b x^n+c x^(2 n-q))^p.input:141
  public void test0270() {
    check( //
        "Integrate[Sqrt[a*x+b*x^3+c*x^5]/x^(3/2), x]", //
        "-1/2*ArcTanh[1/2*(2*a+b*x^2)/(Sqrt[a]*Sqrt[a+b*x^2+c*x^4])]*Sqrt[a]*Sqrt[x]*Sqrt[a+b*x^2+c*x^4]/Sqrt[a*x+b*x^3+c*x^5]+1/4*b*ArcTanh[1/2*(b+2*c*x^2)/(Sqrt[c]*Sqrt[a+b*x^2+c*x^4])]*Sqrt[x]*Sqrt[a+b*x^2+c*x^4]/(Sqrt[c]*Sqrt[a*x+b*x^3+c*x^5])+1/2*Sqrt[a*x+b*x^3+c*x^5]/Sqrt[x]");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:373
  public void test0271() {
    check( //
        "Integrate[(d+e*x^2)/(a+b*x^2+c*x^4), x]", //
        "ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]]*(e+(2*c*d-b*e)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b-Sqrt[b^2-4*a*c]])+ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]]*(e+(-2*c*d+b*e)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b+Sqrt[b^2-4*a*c]])");
  }

  // 1.2.3.4 (f x)^m (d+e x^n)^q (a+b x^n+c x^(2 n))^p.input:163
  public void test0272() {
    check( //
        "Integrate[x*(b+2*c*x^2)/(a+b*x^2+c*x^4), x]", //
        "1/2*Log[a+b*x^2+c*x^4]");
  }

  // 1.3.1 Rational functions.input:418
  public void test0273() {
    check( //
        "Integrate[(-1+x^5)/(-1+x^2), x]", //
        "1/2*x^2+1/4*x^4+Log[1+x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:509
  public void test0274() {
    check( //
        "Integrate[x^4*(a^2+2*a*b*x^2+b^2*x^4)^3, x]", //
        "1/5*a^6*x^5+6/7*a^5*b*x^7+5/3*a^4*b^2*x^9+20/11*a^3*b^3*x^11+15/13*a^2*b^4*x^13+2/5*a*b^5*x^15+1/17*b^6*x^17");
  }

  // 1.3.1 Rational functions.input:384
  public void test0275() {
    check( //
        "Integrate[(-3+2*x-3*x^2+x^3)/(1+x^2), x]", //
        "-3*x+1/2*x^2+1/2*Log[1+x^2]");
  }

  // 1.3.1 Rational functions.input:408
  public void test0276() {
    check( //
        "Integrate[(1+x^4)/(2+x^2), x]", //
        "-2*x+1/3*x^3+5*ArcTan[x/Sqrt[2]]/Sqrt[2]");
  }

  // 1.3.2 Algebraic functions.input:1259
  public void test0277() {
    check( //
        "Integrate[1/((1+x)^(1/4)+Sqrt[1+x]), x]", //
        "-4*(1+x)^(1/4)+4*Log[1+(1+x)^(1/4)]+2*Sqrt[1+x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:191
  public void test0278() {
    check( //
        "Integrate[(b*x^2+c*x^4)^3/x^2, x]", //
        "1/5*b^3*x^5+3/7*b^2*c*x^7+1/3*b*c^2*x^9+1/11*c^3*x^11");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:89
  public void test0279() {
    check( //
        "Integrate[(a^2+2*a*b*x^3+b^2*x^6)^(5/2)/x^11, x]", //
        "-1/10*a^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^10*(a+b*x^3))-5/7*a^4*b*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^7*(a+b*x^3))-5/2*a^3*b^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^4*(a+b*x^3))-10*a^2*b^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x*(a+b*x^3))+5/2*a*b^4*x^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)+1/5*b^5*x^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:91
  public void test0280() {
    check( //
        "Integrate[(a^2+2*a*b*x^3+b^2*x^6)^(5/2)/x^13, x]", //
        "-1/12*a^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^12*(a+b*x^3))-5/9*a^4*b*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^9*(a+b*x^3))-5/3*a^3*b^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^6*(a+b*x^3))-10/3*a^2*b^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^3*(a+b*x^3))+1/3*b^5*x^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)+5*a*b^4*Log[x]*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)");
  }

  // 1.3.2 Algebraic functions.input:1172
  public void test0281() {
    check( //
        "Integrate[Sqrt[1-x^4]/Sqrt[1-x^2], x]", //
        "1/2*ArcSinh[x]+1/2*x*Sqrt[1+x^2]");
  }

  // 1.2.3.4 (f x)^m (d+e x^n)^q (a+b x^n+c x^(2 n))^p.input:181
  public void test0282() {
    check( //
        "Integrate[x^(-1+n)*(b+2*c*x^n)/(b*x^n+c*x^(2*n)), x]", //
        "Log[x]+Log[b+c*x^n]/n");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:154
  public void test0283() {
    check( //
        "Integrate[(A+B*x)/(x*(b*x+c*x^2)^(5/2)), x]", //
        "-2/5*A/(b*x*(b*x+c*x^2)^(3/2))-2/15*(5*b*B-8*A*c)*(b+2*c*x)/(b^3*(b*x+c*x^2)^(3/2))+16/15*c*(5*b*B-8*A*c)*(b+2*c*x)/(b^5*Sqrt[b*x+c*x^2])");
  }

  // 1.3.1 Rational functions.input:346
  public void test0284() {
    check( //
        "Integrate[(a+b*x+c*x^2+d*x^3)^p*(-a+b*p*x+c*(1+2*p)*x^2+d*(2+3*p)*x^3)/x^2, x]", //
        "(a+b*x+c*x^2+d*x^3)^(1+p)/x");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:228
  public void test0285() {
    check( //
        "Integrate[(8+x^2)/(6-5*x+x^2), x]", //
        "x-12*Log[2-x]+17*Log[3-x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:617
  public void test0286() {
    check( //
        "Integrate[x^4*Sqrt[a^2+2*a*b*x^2+b^2*x^4], x]", //
        "1/5*a*x^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+1/7*b*x^7*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)");
  }

  // 1.3.2 Algebraic functions.input:789
  public void test0287() {
    check( //
        "Integrate[x^2*Sqrt[b-a/x]/Sqrt[a-b*x], x]", //
        "2/5*x^3*Sqrt[b-a/x]/Sqrt[a-b*x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:96
  public void test0288() {
    check( //
        "Integrate[(a^2+2*a*b*x^3+b^2*x^6)^(5/2)/x^18, x]", //
        "-1/17*a^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^17*(a+b*x^3))-5/14*a^4*b*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^14*(a+b*x^3))-10/11*a^3*b^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^11*(a+b*x^3))-5/4*a^2*b^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^8*(a+b*x^3))-a*b^4*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^5*(a+b*x^3))-1/2*b^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^2*(a+b*x^3))");
  }

  // 1.3.1 Rational functions.input:597
  public void test0289() {
    check( //
        "Integrate[(-2+4*x)/(-x+x^3), x]", //
        "Log[1-x]+2*Log[x]-3*Log[1+x]");
  }

  // 1.3.1 Rational functions.input:594
  public void test0290() {
    check( //
        "Integrate[(-1+x^2)/(-2*x+x^3), x]", //
        "1/2*Log[x]+1/4*Log[2-x^2]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:183
  public void test0291() {
    check( //
        "Integrate[(b*x^2+c*x^4)^2/x^5, x]", //
        "b*c*x^2+1/4*c^2*x^4+b^2*Log[x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:546
  public void test0292() {
    check( //
        "Integrate[1/((c+a/x^2+b/x)^3*x^2), x]", //
        "1/2*(b+2*a/x)/((b^2-4*a*c)*(c+a/x^2+b/x)^2)-3*a*(b+2*a/x)/((b^2-4*a*c)^2*(c+a/x^2+b/x))+12*a^2*ArcTanh[(b+2*a/x)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(5/2)");
  }

  // 1.2.2.7 P(x) (d+e x^2)^q (a+b x^2+c x^4)^p.input:19
  public void test0293() {
    check( //
        "Integrate[(A+B*x^2)/Sqrt[a+c*x^4], x]", //
        "B*x*Sqrt[a+c*x^4]/(Sqrt[c]*(Sqrt[a]+x^2*Sqrt[c]))-a^(1/4)*B*EllipticE[2*ArcTan[c^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[c])*Sqrt[(a+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(c^(3/4)*Sqrt[a+c*x^4])+1/2*a^(1/4)*EllipticF[2*ArcTan[c^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[c])*(B+A*Sqrt[c]/Sqrt[a])*Sqrt[(a+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(c^(3/4)*Sqrt[a+c*x^4])");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:577
  public void test0294() {
    check( //
        "Integrate[x^11/(a^2+2*a*b*x^2+b^2*x^4)^3, x]", //
        "1/10*a^5/(b^6*(a+b*x^2)^5)-5/8*a^4/(b^6*(a+b*x^2)^4)+5/3*a^3/(b^6*(a+b*x^2)^3)-5/2*a^2/(b^6*(a+b*x^2)^2)+5/2*a/(b^6*(a+b*x^2))+1/2*Log[a+b*x^2]/b^6");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:667
  public void test0295() {
    check( //
        "Integrate[(a^2+2*a*b*x^2+b^2*x^4)^(5/2)/x^17, x]", //
        "-1/16*(a+b*x^2)^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a*x^16)+1/56*b*(a+b*x^2)^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a^2*x^14)-1/336*b^2*(a+b*x^2)^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a^3*x^12)");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:539
  public void test0296() {
    check( //
        "Integrate[(1+4*x-7*x^2)*(2+5*x+x^2)/(3+2*x+5*x^2)^(3/2), x]", //
        "149/25*ArcSinh[(1+5*x)/Sqrt[14]]/Sqrt[5]-2/875*(2321+2449*x)/Sqrt[3+2*x+5*x^2]-261/250*Sqrt[3+2*x+5*x^2]-7/50*x*Sqrt[3+2*x+5*x^2]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:859
  public void test0297() {
    check( //
        "Integrate[(d*f+e*f*x)^2/(a+b*(d+e*x)^2+c*(d+e*x)^4), x]", //
        "-f^2*ArcTan[(d+e*x)*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]]*Sqrt[b-Sqrt[b^2-4*a*c]]/(e*Sqrt[2]*Sqrt[c]*Sqrt[b^2-4*a*c])+f^2*ArcTan[(d+e*x)*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]]*Sqrt[b+Sqrt[b^2-4*a*c]]/(e*Sqrt[2]*Sqrt[c]*Sqrt[b^2-4*a*c])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:1189
  public void test0298() {
    check( //
        "Integrate[(2-5*x)/((2+5*x+3*x^2)^(3/2)*Sqrt[x]), x]", //
        "-30*(2+3*x)*Sqrt[x]/Sqrt[2+5*x+3*x^2]+2*(38+45*x)*Sqrt[x]/Sqrt[2+5*x+3*x^2]+30*(1+x)*EllipticE[ArcTan[Sqrt[x]],-1/2]*Sqrt[2]*Sqrt[(2+3*x)/(1+x)]/Sqrt[2+5*x+3*x^2]-37*(1+x)*EllipticF[ArcTan[Sqrt[x]],-1/2]*Sqrt[2]*Sqrt[(2+3*x)/(1+x)]/Sqrt[2+5*x+3*x^2]");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:174
  public void test0299() {
    check( //
        "Integrate[x*(A+B*x^2)/(a+b*x^2+c*x^4)^3, x]", //
        "1/4*(-A*b+2*a*B+(b*B-2*A*c)*x^2)/((b^2-4*a*c)*(a+b*x^2+c*x^4)^2)-3/4*(b*B-2*A*c)*(b+2*c*x^2)/((b^2-4*a*c)^2*(a+b*x^2+c*x^4))+3*c*(b*B-2*A*c)*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(5/2)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:267
  public void test0300() {
    check( //
        "Integrate[Sqrt[b*x^2+c*x^4]/x^9, x]", //
        "-1/7*(b*x^2+c*x^4)^(3/2)/(b*x^10)+4/35*c*(b*x^2+c*x^4)^(3/2)/(b^2*x^8)-8/105*c^2*(b*x^2+c*x^4)^(3/2)/(b^3*x^6)");
  }

  // 1.3.1 Rational functions.input:403
  public void test0301() {
    check( //
        "Integrate[1/((1+x^2)*(4+x^2)), x]", //
        "-1/6*ArcTan[1/2*x]+1/3*ArcTan[x]");
  }

  // 1.3.2 Algebraic functions.input:15
  public void test0302() {
    check( //
        "Integrate[1/((2^(2/3)*a^(1/3)-b^(1/3)*x)*Sqrt[a-b*x^3]), x]", //
        "-2/3*ArcTan[a^(1/6)*(a^(1/3)-2^(1/3)*b^(1/3)*x)*Sqrt[3]/Sqrt[a-b*x^3]]/(b^(1/3)*Sqrt[3]*Sqrt[a])-2/3*2^(1/3)*(a^(1/3)-b^(1/3)*x)*EllipticF[ArcSin[(-b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a^(1/3)*b^(1/3)*Sqrt[a-b*x^3]*Sqrt[a^(1/3)*(a^(1/3)-b^(1/3)*x)/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:1065
  public void test0303() {
    check( //
        "Integrate[1/(x^3*Sqrt[a+b*x^2+c*x^4]), x]", //
        "1/4*b*ArcTanh[1/2*(2*a+b*x^2)/(Sqrt[a]*Sqrt[a+b*x^2+c*x^4])]/a^(3/2)-1/2*Sqrt[a+b*x^2+c*x^4]/(a*x^2)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:881
  public void test0304() {
    check( //
        "Integrate[(d*x)^m*(a^2+2*a*b*x^2+b^2*x^4)^(3/2), x]", //
        "a^3*(d*x)^(1+m)*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(d*(1+m)*(a+b*x^2))+3*a^2*b*(d*x)^(3+m)*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(d^3*(3+m)*(a+b*x^2))+3*a*b^2*(d*x)^(5+m)*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(d^5*(5+m)*(a+b*x^2))+b^3*(d*x)^(7+m)*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(d^7*(7+m)*(a+b*x^2))");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:562
  public void test0305() {
    check( //
        "Integrate[1/(x*(a^2+2*a*b*x^2+b^2*x^4)^2), x]", //
        "1/6/(a*(a+b*x^2)^3)+1/4/(a^2*(a+b*x^2)^2)+1/2/(a^3*(a+b*x^2))+Log[x]/a^4-1/2*Log[a+b*x^2]/a^4");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:582
  public void test0306() {
    check( //
        "Integrate[1/(c+a/x^6+b/x^3), x]", //
        "x/c-1/3*Log[2^(1/3)*c^(1/3)*x+(b-Sqrt[b^2-4*a*c])^(1/3)]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*(b-Sqrt[b^2-4*a*c])^(2/3))+1/6*Log[2^(2/3)*c^(2/3)*x^2-2^(1/3)*c^(1/3)*x*(b-Sqrt[b^2-4*a*c])^(1/3)+(b-Sqrt[b^2-4*a*c])^(2/3)]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*(b-Sqrt[b^2-4*a*c])^(2/3))+ArcTan[(1-2*2^(1/3)*c^(1/3)*x/(b-Sqrt[b^2-4*a*c])^(1/3))/Sqrt[3]]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*Sqrt[3]*(b-Sqrt[b^2-4*a*c])^(2/3))-1/3*Log[2^(1/3)*c^(1/3)*x+(b+Sqrt[b^2-4*a*c])^(1/3)]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*(b+Sqrt[b^2-4*a*c])^(2/3))+1/6*Log[2^(2/3)*c^(2/3)*x^2-2^(1/3)*c^(1/3)*x*(b+Sqrt[b^2-4*a*c])^(1/3)+(b+Sqrt[b^2-4*a*c])^(2/3)]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*(b+Sqrt[b^2-4*a*c])^(2/3))+ArcTan[(1-2*2^(1/3)*c^(1/3)*x/(b+Sqrt[b^2-4*a*c])^(1/3))/Sqrt[3]]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(4/3)*Sqrt[3]*(b+Sqrt[b^2-4*a*c])^(2/3))");
  }

  // 1.3.1 Rational functions.input:598
  public void test0307() {
    check( //
        "Integrate[(4+x)/(4*x+x^3), x]", //
        "1/2*ArcTan[1/2*x]+Log[x]-1/2*Log[4+x^2]");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:160
  public void test0308() {
    check( //
        "Integrate[x*(A+B*x^2)/(a+b*x^2+c*x^4)^2, x]", //
        "1/2*(-A*b+2*a*B+(b*B-2*A*c)*x^2)/((b^2-4*a*c)*(a+b*x^2+c*x^4))-(b*B-2*A*c)*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(3/2)");
  }

  // 1.2.4.2 (d x)^m (a x^q+b x^n+c x^(2 n-q))^p.input:110
  public void test0309() {
    check( //
        "Integrate[x^3/(a*x+b*x^3+c*x^5), x]", //
        "-ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]]*Sqrt[b-Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[b^2-4*a*c])+ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[b^2-4*a*c])");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:708
  public void test0310() {
    check( //
        "Integrate[1/(x^3*(a^2+2*a*b*x^2+b^2*x^4)^(3/2)), x]", //
        "-b/(a^3*Sqrt[a^2+2*a*b*x^2+b^2*x^4])-1/4*b/(a^2*(a+b*x^2)*Sqrt[a^2+2*a*b*x^2+b^2*x^4])+1/2*(-a-b*x^2)/(a^3*x^2*Sqrt[a^2+2*a*b*x^2+b^2*x^4])-3*b*(a+b*x^2)*Log[x]/(a^4*Sqrt[a^2+2*a*b*x^2+b^2*x^4])+3/2*b*(a+b*x^2)*Log[a+b*x^2]/(a^4*Sqrt[a^2+2*a*b*x^2+b^2*x^4])");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:835
  public void test0311() {
    check( //
        "Integrate[(d+e*x)/(a+b*(d+e*x)^2+c*(d+e*x)^4), x]", //
        "-ArcTanh[(b+2*c*(d+e*x)^2)/Sqrt[b^2-4*a*c]]/(e*Sqrt[b^2-4*a*c])");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:679
  public void test0312() {
    check( //
        "Integrate[x^(-1+1/2*n)/(b*x^n+c*x^(2*n)), x]", //
        "(-2)/(b*n*x^(1/2*n))+2*ArcTan[Sqrt[b]/(x^(1/2*n)*Sqrt[c])]*Sqrt[c]/(b^(3/2)*n)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:1047
  public void test0313() {
    check( //
        "Integrate[(a+b*x^2+c*x^4)^(3/2)/x^9, x]", //
        "-1/16*(2*a+b*x^2)*(a+b*x^2+c*x^4)^(3/2)/(a*x^8)-3/256*(b^2-4*a*c)^2*ArcTanh[1/2*(2*a+b*x^2)/(Sqrt[a]*Sqrt[a+b*x^2+c*x^4])]/a^(5/2)+3/128*(b^2-4*a*c)*(2*a+b*x^2)*Sqrt[a+b*x^2+c*x^4]/(a^2*x^4)");
  }

  // 1.2.3.4 (f x)^m (d+e x^n)^q (a+b x^n+c x^(2 n))^p.input:36
  public void test0314() {
    check( //
        "Integrate[(d+e*x^3)/(a+b*x^3+c*x^6), x]", //
        "1/3*Log[2^(1/3)*c^(1/3)*x+(b-Sqrt[b^2-4*a*c])^(1/3)]*(e+(2*c*d-b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*(b-Sqrt[b^2-4*a*c])^(2/3))-1/6*Log[2^(2/3)*c^(2/3)*x^2-2^(1/3)*c^(1/3)*x*(b-Sqrt[b^2-4*a*c])^(1/3)+(b-Sqrt[b^2-4*a*c])^(2/3)]*(e+(2*c*d-b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*(b-Sqrt[b^2-4*a*c])^(2/3))-ArcTan[(1-2*2^(1/3)*c^(1/3)*x/(b-Sqrt[b^2-4*a*c])^(1/3))/Sqrt[3]]*(e+(2*c*d-b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*Sqrt[3]*(b-Sqrt[b^2-4*a*c])^(2/3))+1/3*Log[2^(1/3)*c^(1/3)*x+(b+Sqrt[b^2-4*a*c])^(1/3)]*(e+(-2*c*d+b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*(b+Sqrt[b^2-4*a*c])^(2/3))-1/6*Log[2^(2/3)*c^(2/3)*x^2-2^(1/3)*c^(1/3)*x*(b+Sqrt[b^2-4*a*c])^(1/3)+(b+Sqrt[b^2-4*a*c])^(2/3)]*(e+(-2*c*d+b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*(b+Sqrt[b^2-4*a*c])^(2/3))-ArcTan[(1-2*2^(1/3)*c^(1/3)*x/(b+Sqrt[b^2-4*a*c])^(1/3))/Sqrt[3]]*(e+(-2*c*d+b*e)/Sqrt[b^2-4*a*c])/(2^(1/3)*c^(1/3)*Sqrt[3]*(b+Sqrt[b^2-4*a*c])^(2/3))");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2945
  public void test0315() {
    check( //
        "Integrate[(5-x)*Sqrt[2+5*x+3*x^2]/(3+2*x)^(3/2), x]", //
        "121/6*EllipticE[ArcSin[Sqrt[3]*Sqrt[1+x]],-2/3]*Sqrt[-2-5*x-3*x^2]/(Sqrt[3]*Sqrt[2+5*x+3*x^2])-161/6*EllipticF[ArcSin[Sqrt[3]*Sqrt[1+x]],-2/3]*Sqrt[-2-5*x-3*x^2]/(Sqrt[3]*Sqrt[2+5*x+3*x^2])-1/3*(21+x)*Sqrt[2+5*x+3*x^2]/Sqrt[3+2*x]");
  }

  // 1.2.1.5 (a+b x+c x^2)^p (d+e x+f x^2)^q.input:16
  public void test0316() {
    check( //
        "Integrate[1/((d+b*x+c*x^2)*Sqrt[a+b*x+c*x^2]), x]", //
        "-2*ArcTanh[(b+2*c*x)*Sqrt[a-d]/(Sqrt[b^2-4*c*d]*Sqrt[a+b*x+c*x^2])]/(Sqrt[a-d]*Sqrt[b^2-4*c*d])");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:123
  public void test0317() {
    check( //
        "Integrate[1/(x^4*(a^2+2*a*b*x^3+b^2*x^6)^(3/2)), x]", //
        "-2/3*b/(a^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6])-1/6*b/(a^2*(a+b*x^3)*Sqrt[a^2+2*a*b*x^3+b^2*x^6])+1/3*(-a-b*x^3)/(a^3*x^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6])-3*b*(a+b*x^3)*Log[x]/(a^4*Sqrt[a^2+2*a*b*x^3+b^2*x^6])+b*(a+b*x^3)*Log[a+b*x^3]/(a^4*Sqrt[a^2+2*a*b*x^3+b^2*x^6])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:406
  public void test0318() {
    check( //
        "Integrate[(A+B*x)/(x*Sqrt[a+c*x^2]), x]", //
        "-A*ArcTanh[Sqrt[a+c*x^2]/Sqrt[a]]/Sqrt[a]+B*ArcTanh[x*Sqrt[c]/Sqrt[a+c*x^2]]/Sqrt[c]");
  }

  // 1.3.2 Algebraic functions.input:421
  public void test0319() {
    check( //
        "Integrate[x*Sqrt[(5-7*x^2)/(7+5*x^2)], x]", //
        "-37/5*ArcTan[Sqrt[5/7]*Sqrt[(5-7*x^2)/(7+5*x^2)]]/Sqrt[35]+1/10*(7+5*x^2)*Sqrt[(5-7*x^2)/(7+5*x^2)]");
  }

  // 1.3.1 Rational functions.input:412
  public void test0320() {
    check( //
        "Integrate[(1+2*x+x^2+x^3)/(1+2*x^2+x^4), x]", //
        "(-1/2)/(1+x^2)+ArcTan[x]+1/2*Log[1+x^2]");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:226
  public void test0321() {
    check( //
        "Integrate[(A+B*x^2)/(x^3*Sqrt[a+b*x^2+c*x^4]), x]", //
        "1/4*(A*b-2*a*B)*ArcTanh[1/2*(2*a+b*x^2)/(Sqrt[a]*Sqrt[a+b*x^2+c*x^4])]/a^(3/2)-1/2*A*Sqrt[a+b*x^2+c*x^4]/(a*x^2)");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:158
  public void test0322() {
    check( //
        "Integrate[x^11*(a^2+2*a*b*x^3+b^2*x^6)^p, x]", //
        "-1/3*a^3*(a+b*x^3)*(a^2+2*a*b*x^3+b^2*x^6)^p/(b^4*(1+2*p))+1/2*a^2*(a+b*x^3)^2*(a^2+2*a*b*x^3+b^2*x^6)^p/(b^4*(1+p))-a*(a+b*x^3)^3*(a^2+2*a*b*x^3+b^2*x^6)^p/(b^4*(3+2*p))+1/6*(a+b*x^3)^4*(a^2+2*a*b*x^3+b^2*x^6)^p/(b^4*(2+p))");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:338
  public void test0323() {
    check( //
        "Integrate[x^2/((1+x^2)*Sqrt[-1-x^4]), x]", //
        "-1/2*ArcTanh[x*Sqrt[2]/Sqrt[-1-x^4]]/Sqrt[2]+1/4*(1+x^2)*EllipticF[2*ArcTan[x],1/2]*Sqrt[(1+x^4)/(1+x^2)^2]/Sqrt[-1-x^4]");
  }

  // 1.3.1 Rational functions.input:241
  public void test0324() {
    check( //
        "Integrate[(b+2*c*x^3)/(b*x+c*x^4), x]", //
        "Log[x]+1/3*Log[b+c*x^3]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:675
  public void test0325() {
    check( //
        "Integrate[x^6*(a^2+2*a*b*x^2+b^2*x^4)^(5/2), x]", //
        "1/7*a^5*x^7*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+5/9*a^4*b*x^9*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+10/11*a^3*b^2*x^11*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+10/13*a^2*b^3*x^13*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+1/3*a*b^4*x^15*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+1/17*b^5*x^17*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:92
  public void test0326() {
    check( //
        "Integrate[(a^2+2*a*b*x^3+b^2*x^6)^(5/2)/x^14, x]", //
        "-1/13*a^5*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^13*(a+b*x^3))-1/2*a^4*b*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^10*(a+b*x^3))-10/7*a^3*b^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^7*(a+b*x^3))-5/2*a^2*b^3*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x^4*(a+b*x^3))-5*a*b^4*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(x*(a+b*x^3))+1/2*b^5*x^2*Sqrt[a^2+2*a*b*x^3+b^2*x^6]/(a+b*x^3)");
  }

  // 1.3.2 Algebraic functions.input:643
  public void test0327() {
    check( //
        "Integrate[(d+e*x+f*Sqrt[a+e^2*x^2/f^2])^(5/2), x]", //
        "-5/2*a*d^(3/2)*f^2*ArcTanh[Sqrt[d+e*x+f*Sqrt[a+e^2*x^2/f^2]]/Sqrt[d]]/e+1/3*a*f^2*(d+e*x+f*Sqrt[a+e^2*x^2/f^2])^(3/2)/e+1/7*(d+e*x+f*Sqrt[a+e^2*x^2/f^2])^(7/2)/e+2*a*d*f^2*Sqrt[d+e*x+f*Sqrt[a+e^2*x^2/f^2]]/e-1/2*a*d^2*f^2*Sqrt[d+e*x+f*Sqrt[a+e^2*x^2/f^2]]/(e*(e*x+f*Sqrt[a+e^2*x^2/f^2]))");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:558
  public void test0328() {
    check( //
        "Integrate[x^7/(a^2+2*a*b*x^2+b^2*x^4)^2, x]", //
        "1/6*a^3/(b^4*(a+b*x^2)^3)-3/4*a^2/(b^4*(a+b*x^2)^2)+3/2*a/(b^4*(a+b*x^2))+1/2*Log[a+b*x^2]/b^4");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:205
  public void test0329() {
    check( //
        "Integrate[(b*x^2+c*x^4)^3/x^16, x]", //
        "-1/9*b^3/x^9-3/7*b^2*c/x^7-3/5*b*c^2/x^5-1/3*c^3/x^3");
  }

  // 1.3.1 Rational functions.input:380
  public void test0330() {
    check( //
        "Integrate[(-9-9*x+2*x^2)/(-9*x+x^3), x]", //
        "-Log[3-x]+Log[x]+2*Log[3+x]");
  }

  // 1.3.1 Rational functions.input:610
  public void test0331() {
    check( //
        "Integrate[1/((-3+x)*(4+x^2)), x]", //
        "-3/26*ArcTan[1/2*x]+1/13*Log[3-x]-1/26*Log[4+x^2]");
  }

  // 1.3.1 Rational functions.input:592
  public void test0332() {
    check( //
        "Integrate[(4+4*x)/(x^2*(1+x^2)), x]", //
        "(-4)/x-4*ArcTan[x]+4*Log[x]-2*Log[1+x^2]");
  }

  // 1.3.1 Rational functions.input:75
  public void test0333() {
    check( //
        "Integrate[8+8*x-x^3+8*x^4, x]", //
        "8*x+4*x^2-1/4*x^4+8/5*x^5");
  }

  // 1.3.1 Rational functions.input:8
  public void test0334() {
    check( //
        "Integrate[1/(-9*b*x+9*x^3+2*b^(3/2)*Sqrt[3]), x]", //
        "-1/27*Log[-x*Sqrt[3]+Sqrt[b]]/b+1/27*Log[x*Sqrt[3]+2*Sqrt[b]]/b+1/3/(Sqrt[3]*Sqrt[b]*(-3*x+Sqrt[3]*Sqrt[b]))");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:27
  public void test0335() {
    check( //
        "Integrate[x*(2+3*x^2)*Sqrt[5+x^4], x]", //
        "1/2*(5+x^4)^(3/2)+5/2*ArcSinh[x^2/Sqrt[5]]+1/2*x^2*Sqrt[5+x^4]");
  }

  // 1.3.2 Algebraic functions.input:994
  public void test0336() {
    check( //
        "Integrate[Sqrt[1+1/x]/(1+x)^2, x]", //
        "2/Sqrt[1+1/x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:270
  public void test0337() {
    check( //
        "Integrate[x^4*Sqrt[b*x^2+c*x^4], x]", //
        "8/105*b^2*(b*x^2+c*x^4)^(3/2)/(c^3*x^3)-4/35*b*(b*x^2+c*x^4)^(3/2)/(c^2*x)+1/7*x*(b*x^2+c*x^4)^(3/2)/c");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:231
  public void test0338() {
    check( //
        "Integrate[(A+B*x^2)/Sqrt[a+b*x^2+c*x^4], x]", //
        "B*x*Sqrt[a+b*x^2+c*x^4]/(Sqrt[c]*(Sqrt[a]+x^2*Sqrt[c]))-a^(1/4)*B*EllipticE[2*ArcTan[c^(1/4)*x/a^(1/4)],1/4*(2-b/(Sqrt[a]*Sqrt[c]))]*(Sqrt[a]+x^2*Sqrt[c])*Sqrt[(a+b*x^2+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(c^(3/4)*Sqrt[a+b*x^2+c*x^4])+1/2*a^(1/4)*EllipticF[2*ArcTan[c^(1/4)*x/a^(1/4)],1/4*(2-b/(Sqrt[a]*Sqrt[c]))]*(Sqrt[a]+x^2*Sqrt[c])*(B+A*Sqrt[c]/Sqrt[a])*Sqrt[(a+b*x^2+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(c^(3/4)*Sqrt[a+b*x^2+c*x^4])");
  }

  // 1.3.2 Algebraic functions.input:709
  public void test0339() {
    check( //
        "Integrate[(d+e*x+f*Sqrt[(a*f^2+e*x*(2*d+e*x))/f^2])^n/((a*f^2+e*x*(2*d+e*x))/f^2)^(1/2), x]", //
        "f*(d+e*x+f*Sqrt[a+2*d*e*x/f^2+e^2*x^2/f^2])^n/(e*n)");
  }

  // 1.2.1.4 (d+e x)^m (f+g x)^n (a+b x+c x^2)^p.input:875
  public void test0340() {
    check( //
        "Integrate[(d+e*x)^(5/2)/((f+g*x)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)), x]", //
        "-2/3*(d+e*x)^(3/2)/((c*d*f-a*e*g)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))+2*g^(3/2)*ArcTan[Sqrt[g]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d*f-a*e*g]*Sqrt[d+e*x])]/(c*d*f-a*e*g)^(5/2)+2*g*Sqrt[d+e*x]/((c*d*f-a*e*g)^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:949
  public void test0341() {
    check( //
        "Integrate[x^3/(a+b*x^2+c*x^4), x]", //
        "1/4*Log[a+b*x^2+c*x^4]/c+1/2*b*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(c*Sqrt[b^2-4*a*c])");
  }

  // 1.3.1 Rational functions.input:488
  public void test0342() {
    check( //
        "Integrate[(1+x+4*x^2)/(x+4*x^3), x]", //
        "1/2*ArcTan[2*x]+Log[x]");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:92
  public void test0343() {
    check( //
        "Integrate[x*(1+x+x^2)/(1+x^2)^2, x]", //
        "-1/2*x/(1+x^2)+1/2*ArcTan[x]+1/2*Log[1+x^2]");
  }

  // 1.3.1 Rational functions.input:602
  public void test0344() {
    check( //
        "Integrate[(1+x)/(-6*x+x^2+x^3), x]", //
        "3/10*Log[2-x]-1/6*Log[x]-2/15*Log[3+x]");
  }

  // 1.3.1 Rational functions.input:390
  public void test0345() {
    check( //
        "Integrate[(3*x-4*x^2+3*x^3)/(1+x^2), x]", //
        "-4*x+3/2*x^2+4*ArcTan[x]");
  }

  // 1.3.2 Algebraic functions.input:1288
  public void test0346() {
    check( //
        "Integrate[x/(x-Sqrt[1+2*x^2]), x]", //
        "-x+ArcTan[x]+ArcTan[Sqrt[1+2*x^2]]-Sqrt[1+2*x^2]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:662
  public void test0347() {
    check( //
        "Integrate[(a^2+2*a*b*x^2+b^2*x^4)^(5/2)/x^7, x]", //
        "-1/6*a^5*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^6*(a+b*x^2))-5/4*a^4*b*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^4*(a+b*x^2))-5*a^3*b^2*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x^2*(a+b*x^2))+5/2*a*b^4*x^2*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+1/4*b^5*x^4*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+10*a^2*b^3*Log[x]*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)");
  }

  // 1.3.2 Algebraic functions.input:456
  public void test0348() {
    check( //
        "Integrate[x^3*Sqrt[a+b/(c+d*x^2)], x]", //
        "-1/8*b*(b+4*a*c)*ArcTanh[Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/Sqrt[a]]/(a^(3/2)*d^2)+1/8*(b-4*a*c)*(c+d*x^2)*Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/(a*d^2)+1/4*(c+d*x^2)^2*Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/d^2");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:527
  public void test0349() {
    check( //
        "Integrate[x/(c+a/x^2+b/x), x]", //
        "-b*x/c^2+1/2*x^2/c+1/2*(b^2-a*c)*Log[a+b*x+c*x^2]/c^3+b*(b^2-3*a*c)*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/(c^3*Sqrt[b^2-4*a*c])");
  }

  // 1.2.2.5 P(x) (a+b x^2+c x^4)^p.input:33
  public void test0350() {
    check( //
        "Integrate[(d+e*x+f*x^2+g*x^3)/(a+b*x^2+c*x^4), x]", //
        "1/4*g*Log[a+b*x^2+c*x^4]/c-1/2*(2*c*e-b*g)*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(c*Sqrt[b^2-4*a*c])+ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]]*(f+(2*c*d-b*f)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b-Sqrt[b^2-4*a*c]])+ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]]*(f+(-2*c*d+b*f)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b+Sqrt[b^2-4*a*c]])");
  }

  // 1.2.3.5 P(x) (d x)^m (a+b x^n+c x^(2 n))^p.input:46
  public void test0351() {
    check( //
        "Integrate[(d*x)^(-1+1/4*n)*(-a*h+c*f*x^(1/4*n)+c*g*x^(3/4*n)+c*h*x^n)/(a+c*x^n)^(3/2), x]", //
        "-2*x^(1-1/4*n)*(d*x)^(1/4*(-4+n))*(a*g+2*a*h*x^(1/4*n)-c*f*x^(1/2*n))/(a*n*Sqrt[a+c*x^n])");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:154
  public void test0352() {
    check( //
        "Integrate[(A+B*x^2)/(a+b*x^2+c*x^4), x]", //
        "ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b-Sqrt[b^2-4*a*c]]]*(B+(-b*B+2*A*c)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b-Sqrt[b^2-4*a*c]])+ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]]*(B+(b*B-2*A*c)/Sqrt[b^2-4*a*c])/(Sqrt[2]*Sqrt[c]*Sqrt[b+Sqrt[b^2-4*a*c]])");
  }

  // 1.3.2 Algebraic functions.input:171
  public void test0353() {
    check( //
        "Integrate[(1+(b/a)^(1/3)*x-Sqrt[3])/((1+(b/a)^(1/3)*x+Sqrt[3])*Sqrt[-a-b*x^3]), x]", //
        "-2*ArcTanh[(1+(b/a)^(1/3)*x)*Sqrt[a]*Sqrt[3+2*Sqrt[3]]/Sqrt[-a-b*x^3]]/((b/a)^(1/3)*Sqrt[a]*Sqrt[3+2*Sqrt[3]])");
  }

  // 1.3.2 Algebraic functions.input:108
  public void test0354() {
    check( //
        "Integrate[x/((c+d*x)*Sqrt[c^3+4*d^3*x^3]), x]", //
        "-2/3*ArcTan[(c+2*d*x)*Sqrt[3]*Sqrt[c]/Sqrt[c^3+4*d^3*x^3]]/(d^2*Sqrt[3]*Sqrt[c])+1/3*2^(1/3)*(c+2^(2/3)*d*x)*EllipticF[ArcSin[(2^(2/3)*d*x+c*(1-Sqrt[3]))/(2^(2/3)*d*x+c*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(c^2-2^(2/3)*c*d*x+2*2^(1/3)*d^2*x^2)/(2^(2/3)*d*x+c*(1+Sqrt[3]))^2]/(3^(1/4)*d^2*Sqrt[c^3+4*d^3*x^3]*Sqrt[c*(c+2^(2/3)*d*x)/(2^(2/3)*d*x+c*(1+Sqrt[3]))^2])");
  }

  // 1.3.2 Algebraic functions.input:1229
  public void test0355() {
    check( //
        "Integrate[(1+Sqrt[x])/(x^(5/6)+x^(7/6)), x]", //
        "3*x^(1/3)+6*ArcTan[x^(1/6)]-3*Log[1+x^(1/3)]");
  }

  // 1.2.2.6 P(x) (d x)^m (a+b x^2+c x^4)^p.input:17
  public void test0356() {
    check( //
        "Integrate[(A+B*x+C*x^2)*(a+b*x^2+c*x^4)/x^3, x]", //
        "-1/2*a*A/x^2-a*B/x+b*B*x+1/2*(A*c+b*C)*x^2+1/3*B*c*x^3+1/4*c*C*x^4+(A*b+a*C)*Log[x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:711
  public void test0357() {
    check( //
        "Integrate[(a^2+2*a*b*x^n+b^2*x^(2*n))^(3/2)/x, x]", //
        "3*a^2*b^2*x^n*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/(n*(a*b+b^2*x^n))+3/2*a*b^3*x^(2*n)*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/(n*(a*b+b^2*x^n))+1/3*b^4*x^(3*n)*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/(n*(a*b+b^2*x^n))+a^3*Log[x]*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/(a+b*x^n)");
  }

  // 1.3.1 Rational functions.input:381
  public void test0358() {
    check( //
        "Integrate[(1+2*x^2+x^5)/(-x+x^3), x]", //
        "x+1/3*x^3+2*Log[1-x]-Log[x]+Log[1+x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:701
  public void test0359() {
    check( //
        "Integrate[x^2*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)], x]", //
        "1/3*a*x^3*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/(a+b*x^n)+b^2*x^(3+n)*Sqrt[a^2+2*a*b*x^n+b^2*x^(2*n)]/((3+n)*(a*b+b^2*x^n))");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:260
  public void test0360() {
    check( //
        "Integrate[x^5*Sqrt[b*x^2+c*x^4], x]", //
        "-5/48*b*(b*x^2+c*x^4)^(3/2)/c^2+1/8*x^2*(b*x^2+c*x^4)^(3/2)/c-5/128*b^4*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]/c^(7/2)+5/128*b^2*(b+2*c*x^2)*Sqrt[b*x^2+c*x^4]/c^3");
  }

  // 1.3.2 Algebraic functions.input:721
  public void test0361() {
    check( //
        "Integrate[(e-2*f*x^2)/(e^2+4*d*f*x^2+4*e*f*x^2+4*f^2*x^4), x]", //
        "-1/4*Log[e+2*f*x^2-2*x*Sqrt[-d]*Sqrt[f]]/(Sqrt[-d]*Sqrt[f])+1/4*Log[e+2*f*x^2+2*x*Sqrt[-d]*Sqrt[f]]/(Sqrt[-d]*Sqrt[f])");
  }

  // 1.3.2 Algebraic functions.input:91
  public void test0362() {
    check( //
        "Integrate[(e+f*x)/((2^(2/3)-x)*Sqrt[-1+x^3]), x]", //
        "-2/3*(e+2^(2/3)*f)*ArcTanh[(1-2^(1/3)*x)*Sqrt[3]/Sqrt[-1+x^3]]/Sqrt[3]-2/3*(2^(1/3)*e-f)*(1-x)*EllipticF[ArcSin[(1-x+Sqrt[3])/(1-x-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x-Sqrt[3])^2]/(3^(1/4)*Sqrt[-1+x^3]*Sqrt[(-1+x)/(1-x-Sqrt[3])^2])");
  }

  // 1.3.2 Algebraic functions.input:22
  public void test0363() {
    check( //
        "Integrate[1/((1-x+Sqrt[3])*Sqrt[1-x^3]), x]", //
        "-(1-x)*EllipticF[ArcSin[(1-x-Sqrt[3])/(1-x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x+Sqrt[3])^2]/(3^(3/4)*Sqrt[1-x^3]*Sqrt[(1-x)/(1-x+Sqrt[3])^2])-ArcTan[(1-x)*Sqrt[3+2*Sqrt[3]]/Sqrt[1-x^3]]/Sqrt[3*(3+2*Sqrt[3])]");
  }

  // 1.3.2 Algebraic functions.input:886
  public void test0364() {
    check( //
        "Integrate[1/(x*(a+b*(c*x)^n)^(1/2)), x]", //
        "-2*ArcTanh[Sqrt[a+b*(c*x)^n]/Sqrt[a]]/(n*Sqrt[a])");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:46
  public void test0365() {
    check( //
        "Integrate[(1-c^2*x^2)/Sqrt[1-c^4*x^4], x]", //
        "-EllipticE[ArcSin[c*x],-1]/c+2*EllipticF[ArcSin[c*x],-1]/c");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:72
  public void test0366() {
    check( //
        "Integrate[(1+2*x^2)/(1+4*x^2+4*x^4), x]", //
        "ArcTan[x*Sqrt[2]]/Sqrt[2]");
  }

  // 1.3.2 Algebraic functions.input:460
  public void test0367() {
    check( //
        "Integrate[Sqrt[a+b/(c+d*x^2)]/x^5, x]", //
        "-1/8*b*(3*b+4*a*c)*d^2*ArcTanh[Sqrt[c]*Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/Sqrt[b+a*c]]/(c^(5/2)*(b+a*c)^(3/2))+1/8*(5*b+4*a*c)*d*(c+d*x^2)*Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/(c^2*(b+a*c)*x^2)-1/4*(c+d*x^2)^2*Sqrt[(b+a*c+a*d*x^2)/(c+d*x^2)]/(c^2*x^4)");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2947
  public void test0368() {
    check( //
        "Integrate[(5-x)*Sqrt[2+5*x+3*x^2]/(3+2*x)^(7/2), x]", //
        "-49/250*EllipticE[ArcSin[Sqrt[3]*Sqrt[1+x]],-2/3]*Sqrt[3]*Sqrt[-2-5*x-3*x^2]/Sqrt[2+5*x+3*x^2]+9/50*EllipticF[ArcSin[Sqrt[3]*Sqrt[1+x]],-2/3]*Sqrt[3]*Sqrt[-2-5*x-3*x^2]/Sqrt[2+5*x+3*x^2]+1/25*(32+43*x)*Sqrt[2+5*x+3*x^2]/(3+2*x)^(5/2)+49/125*Sqrt[2+5*x+3*x^2]/Sqrt[3+2*x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2873
  public void test0369() {
    check( //
        "Integrate[(5-x)*(3+2*x)^2/(2+5*x+3*x^2)^(5/2), x]", //
        "-2/3*(3+2*x)^2*(29+35*x)/(2+5*x+3*x^2)^(3/2)+376/3*(7+8*x)/Sqrt[2+5*x+3*x^2]");
  }

  // 1.3.2 Algebraic functions.input:1142
  public void test0370() {
    check( //
        "Integrate[(1-x)/(1+Sqrt[x]), x]", //
        "x-2/3*x^(3/2)");
  }

  // 1.2.4.2 (d x)^m (a x^q+b x^n+c x^(2 n-q))^p.input:18
  public void test0371() {
    check( //
        "Integrate[x*(a*x^2+b*x^3+c*x^4)^2, x]", //
        "1/6*a^2*x^6+2/7*a*b*x^7+1/8*(b^2+2*a*c)*x^8+2/9*b*c*x^9+1/10*c^2*x^10");
  }

  // 1.3.2 Algebraic functions.input:672
  public void test0372() {
    check( //
        "Integrate[(a+x^2)^2*(x+Sqrt[a+x^2])^n, x]", //
        "-1/32*a^5*(x+Sqrt[a+x^2])^(-5+n)/(5-n)-5/32*a^4*(x+Sqrt[a+x^2])^(-3+n)/(3-n)-5/16*a^3*(x+Sqrt[a+x^2])^(-1+n)/(1-n)+5/16*a^2*(x+Sqrt[a+x^2])^(1+n)/(1+n)+5/32*a*(x+Sqrt[a+x^2])^(3+n)/(3+n)+1/32*(x+Sqrt[a+x^2])^(5+n)/(5+n)");
  }

  // 1.3.2 Algebraic functions.input:441
  public void test0373() {
    check( //
        "Integrate[x^3/(e*(a+b*x^2)/(c+d*x^2))^(3/2), x]", //
        "3/8*(b*c-5*a*d)*(b*c-a*d)*ArcTanh[Sqrt[d]*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(Sqrt[b]*Sqrt[e])]/(b^(7/2)*e^(3/2)*Sqrt[d])+a*(b*c-a*d)/(b^3*e*Sqrt[e*(a+b*x^2)/(c+d*x^2)])+1/8*(3*b*c-7*a*d)*(c+d*x^2)*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(b^3*e^2)+1/4*(c+d*x^2)^2*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(b^2*e^2)");
  }

  // 1.3.2 Algebraic functions.input:1223
  public void test0374() {
    check( //
        "Integrate[Sqrt[-b*x^2+Sqrt[a+b^2*x^4]]/Sqrt[a+b^2*x^4], x]", //
        "ArcTan[x*Sqrt[2]*Sqrt[b]/Sqrt[-b*x^2+Sqrt[a+b^2*x^4]]]/(Sqrt[2]*Sqrt[b])");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:467
  public void test0375() {
    check( //
        "Integrate[(2+x+3*x^2-x^3+5*x^4)/((5+2*x)^5*Sqrt[3-x+2*x^2]), x]", //
        "2053207/20639121408*ArcTanh[1/12*(17-22*x)/(Sqrt[2]*Sqrt[3-x+2*x^2])]/Sqrt[2]-3667/2304*Sqrt[3-x+2*x^2]/(5+2*x)^4+513097/497664*Sqrt[3-x+2*x^2]/(5+2*x)^3-16295969/71663616*Sqrt[3-x+2*x^2]/(5+2*x)^2+26800085/1719926784*Sqrt[3-x+2*x^2]/(5+2*x)");
  }

  // 1.3.1 Rational functions.input:564
  public void test0376() {
    check( //
        "Integrate[(d+e*x)^2/(a+c*x^4)^3, x]", //
        "1/8*x*(d+e*x)^2/(a*(a+c*x^4)^2)+1/32*x*(7*d^2+12*d*e*x+5*e^2*x^2)/(a^2*(a+c*x^4))+3/8*d*e*ArcTan[x^2*Sqrt[c]/Sqrt[a]]/(a^(5/2)*Sqrt[c])-1/128*Log[-a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]*(-5*e^2*Sqrt[a]+21*d^2*Sqrt[c])/(a^(11/4)*c^(3/4)*Sqrt[2])+1/128*Log[a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]*(-5*e^2*Sqrt[a]+21*d^2*Sqrt[c])/(a^(11/4)*c^(3/4)*Sqrt[2])-1/64*ArcTan[1-c^(1/4)*x*Sqrt[2]/a^(1/4)]*(5*e^2*Sqrt[a]+21*d^2*Sqrt[c])/(a^(11/4)*c^(3/4)*Sqrt[2])+1/64*ArcTan[1+c^(1/4)*x*Sqrt[2]/a^(1/4)]*(5*e^2*Sqrt[a]+21*d^2*Sqrt[c])/(a^(11/4)*c^(3/4)*Sqrt[2])");
  }

  // 1.3.1 Rational functions.input:124
  public void test0377() {
    check( //
        "Integrate[1/(c+(a+b*x)^2), x]", //
        "ArcTan[(a+b*x)/Sqrt[c]]/(b*Sqrt[c])");
  }

  // 1.3.2 Algebraic functions.input:768
  public void test0378() {
    check( //
        "Integrate[1/(4*x^(3/2)+Sqrt[x]), x]", //
        "ArcTan[2*Sqrt[x]]");
  }

  // 1.3.2 Algebraic functions.input:951
  public void test0379() {
    check( //
        "Integrate[1/Sqrt[2+Sqrt[1+Sqrt[x]]], x]", //
        "88/3*(2+Sqrt[1+Sqrt[x]])^(3/2)-48/5*(2+Sqrt[1+Sqrt[x]])^(5/2)+8/7*(2+Sqrt[1+Sqrt[x]])^(7/2)-48*Sqrt[2+Sqrt[1+Sqrt[x]]]");
  }

  // 1.3.2 Algebraic functions.input:906
  public void test0380() {
    check( //
        "Integrate[Sqrt[-1+1/x^2]/(x*(-1+x^2)^2), x]", //
        "1/Sqrt[-1+1/x^2]-Sqrt[-1+1/x^2]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:1037
  public void test0381() {
    check( //
        "Integrate[Sqrt[a+b*x^2+c*x^4]/x^4, x]", //
        "-1/3*Sqrt[a+b*x^2+c*x^4]/x^3-1/3*b*Sqrt[a+b*x^2+c*x^4]/(a*x)+1/3*b*x*Sqrt[c]*Sqrt[a+b*x^2+c*x^4]/(a*(Sqrt[a]+x^2*Sqrt[c]))-1/3*b*c^(1/4)*EllipticE[2*ArcTan[c^(1/4)*x/a^(1/4)],1/4*(2-b/(Sqrt[a]*Sqrt[c]))]*(Sqrt[a]+x^2*Sqrt[c])*Sqrt[(a+b*x^2+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(a^(3/4)*Sqrt[a+b*x^2+c*x^4])+1/6*c^(1/4)*EllipticF[2*ArcTan[c^(1/4)*x/a^(1/4)],1/4*(2-b/(Sqrt[a]*Sqrt[c]))]*(Sqrt[a]+x^2*Sqrt[c])*(b+2*Sqrt[a]*Sqrt[c])*Sqrt[(a+b*x^2+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(a^(3/4)*Sqrt[a+b*x^2+c*x^4])");
  }

  // 1.3.2 Algebraic functions.input:142
  public void test0382() {
    check( //
        "Integrate[x/((2*a^(1/3)-b^(1/3)*x)*Sqrt[-a-b*x^3]), x]", //
        "4/9*ArcTan[1/3*(a^(1/3)+b^(1/3)*x)^2/(a^(1/6)*Sqrt[-a-b*x^3])]/(a^(1/6)*b^(2/3))-2/3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2-Sqrt[3]]/(3^(1/4)*b^(2/3)*Sqrt[-a-b*x^3]*Sqrt[-a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))^2])");
  }

  // 1.3.2 Algebraic functions.input:711
  public void test0383() {
    check( //
        "Integrate[(d+e*x+f*Sqrt[a+2*d*e*x/f^2+e^2*x^2/f^2])^n/(a*g+2*d*e*g*x/f^2+e^2*g*x^2/f^2)^(1/2), x]", //
        "f*Sqrt[a+2*d*e*x/f^2+e^2*x^2/f^2]*(d+e*x+f*Sqrt[a+2*d*e*x/f^2+e^2*x^2/f^2])^n/(e*n*Sqrt[a*g+2*d*e*g*x/f^2+e^2*g*x^2/f^2])");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:735
  public void test0384() {
    check( //
        "Integrate[1/(a^2+2*a*b*x^2+b^2*x^4)^(1/3), x]", //
        "-3^(3/4)*a*(1+b*x^2/a)^(2/3)*(1-(1+b*x^2/a)^(1/3))*EllipticF[ArcSin[(1-(1+b*x^2/a)^(1/3)+Sqrt[3])/(1-(1+b*x^2/a)^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+(1+b*x^2/a)^(1/3)+(1+b*x^2/a)^(2/3))/(1-(1+b*x^2/a)^(1/3)-Sqrt[3])^2]/(b*x*(a^2+2*a*b*x^2+b^2*x^4)^(1/3)*Sqrt[(-1+(1+b*x^2/a)^(1/3))/(1-(1+b*x^2/a)^(1/3)-Sqrt[3])^2])");
  }

  // 1.3.2 Algebraic functions.input:912
  public void test0385() {
    check( //
        "Integrate[x/(a+b*x^2+Sqrt[a+b*x^2]), x]", //
        "Log[1+Sqrt[a+b*x^2]]/b");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:144
  public void test0386() {
    check( //
        "Integrate[(-x^2+2*Sqrt[a])/(a+x^4-x^2*Sqrt[a]), x]", //
        "-1/2*ArcTan[-2*x/a^(1/4)+Sqrt[3]]/a^(1/4)+1/2*ArcTan[2*x/a^(1/4)+Sqrt[3]]/a^(1/4)-1/4*Log[x^2-a^(1/4)*x*Sqrt[3]+Sqrt[a]]*Sqrt[3]/a^(1/4)+1/4*Log[x^2+a^(1/4)*x*Sqrt[3]+Sqrt[a]]*Sqrt[3]/a^(1/4)");
  }

  // 1.3.1 Rational functions.input:145
  public void test0387() {
    check( //
        "Integrate[x^2/Sqrt[1-(1+x)^2], x]", //
        "3/2*ArcSin[1+x]+3/2*Sqrt[1-(1+x)^2]-1/2*x*Sqrt[1-(1+x)^2]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:673
  public void test0388() {
    check( //
        "Integrate[x^(-1+n)/(b*x^n+c*x^(2*n)), x]", //
        "Log[x]/b-Log[b+c*x^n]/(b*n)");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:270
  public void test0389() {
    check( //
        "Integrate[x^2/Sqrt[a+b*x^3+c*x^6], x]", //
        "1/3*ArcTanh[1/2*(b+2*c*x^3)/(Sqrt[c]*Sqrt[a+b*x^3+c*x^6])]/Sqrt[c]");
  }

  // 1.2.1.9 P(x) (d+e x)^m (a+b x+c x^2)^p.input:26
  public void test0390() {
    check( //
        "Integrate[(A+B*x+C*x^2)/Sqrt[d^2-e^2*x^2], x]", //
        "1/2*(C*d^2+2*A*e^2)*ArcTan[e*x/Sqrt[d^2-e^2*x^2]]/e^3-B*Sqrt[d^2-e^2*x^2]/e^2-1/2*C*x*Sqrt[d^2-e^2*x^2]/e^2");
  }

  // 1.2.2.7 P(x) (d+e x^2)^q (a+b x^2+c x^4)^p.input:97
  public void test0391() {
    check( //
        "Integrate[Sqrt[c+e*x+d*x^2]*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/x^2, x]", //
        "-a*(c+e*x+d*x^2)^(3/2)*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(c*x*(a+b*x^2))+1/8*(4*b*c*d+8*a*d^2-b*e^2)*ArcTanh[1/2*(e+2*d*x)/(Sqrt[d]*Sqrt[c+e*x+d*x^2])]*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(d^(3/2)*(a+b*x^2))-1/2*a*e*ArcTanh[1/2*(2*c+e*x)/(Sqrt[c]*Sqrt[c+e*x+d*x^2])]*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/((a+b*x^2)*Sqrt[c])+1/4*((b*c+4*a*d)*e+2*d*(b*c+2*a*d)*x)*Sqrt[c+e*x+d*x^2]*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(c*d*(a+b*x^2))");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:108
  public void test0392() {
    check( //
        "Integrate[x^2/Sqrt[a^2+2*a*b*x^3+b^2*x^6], x]", //
        "1/3*(a+b*x^3)*Log[a+b*x^3]/(b*Sqrt[a^2+2*a*b*x^3+b^2*x^6])");
  }

  // 1.2.3.4 (f x)^m (d+e x^n)^q (a+b x^n+c x^(2 n))^p.input:58
  public void test0393() {
    check( //
        "Integrate[(d+e*x^3)^(1/2)*(a+b*x^3+c*x^6), x]", //
        "-2/187*(8*c*d-17*b*e)*x*(d+e*x^3)^(3/2)/e^2+2/17*c*x^4*(d+e*x^3)^(3/2)/e+2/935*(16*c*d^2-34*b*d*e+187*a*e^2)*x*Sqrt[d+e*x^3]/e^2+2/935*3^(3/4)*d*(16*c*d^2-34*b*d*e+187*a*e^2)*(d^(1/3)+e^(1/3)*x)*EllipticF[ArcSin[(e^(1/3)*x+d^(1/3)*(1-Sqrt[3]))/(e^(1/3)*x+d^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(d^(2/3)-d^(1/3)*e^(1/3)*x+e^(2/3)*x^2)/(e^(1/3)*x+d^(1/3)*(1+Sqrt[3]))^2]/(e^(7/3)*Sqrt[d+e*x^3]*Sqrt[d^(1/3)*(d^(1/3)+e^(1/3)*x)/(e^(1/3)*x+d^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.2.1.4 (d+e x)^m (f+g x)^n (a+b x+c x^2)^p.input:262
  public void test0394() {
    check( //
        "Integrate[x^2/((d+e*x)^4*(d^2-e^2*x^2)^(7/2)), x]", //
        "14/2145*x/(d^4*e^2*(d^2-e^2*x^2)^(5/2))-1/13*d/(e^3*(d+e*x)^4*(d^2-e^2*x^2)^(5/2))+17/143/(e^3*(d+e*x)^3*(d^2-e^2*x^2)^(5/2))+(-7/1287)/(d*e^3*(d+e*x)^2*(d^2-e^2*x^2)^(5/2))+(-7/1287)/(d^2*e^3*(d+e*x)*(d^2-e^2*x^2)^(5/2))+56/6435*x/(d^6*e^2*(d^2-e^2*x^2)^(3/2))+112/6435*x/(d^8*e^2*Sqrt[d^2-e^2*x^2])");
  }

  // 1.3.2 Algebraic functions.input:1244
  public void test0395() {
    check( //
        "Integrate[Sqrt[x+x^(3/2)], x]", //
        "32/105*(x+x^(3/2))^(3/2)/x^(3/2)-16/35*(x+x^(3/2))^(3/2)/x+4/7*(x+x^(3/2))^(3/2)/Sqrt[x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:620
  public void test0396() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x^2+b^2*x^4]/x^2, x]", //
        "-a*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(x*(a+b*x^2))+b*x*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:974
  public void test0397() {
    check( //
        "Integrate[x^7/(a+b*x^2+c*x^4)^3, x]", //
        "-1/4*x^6*(b+2*c*x^2)/((b^2-4*a*c)*(a+b*x^2+c*x^4)^2)+3/4*b*x^2*(2*a+b*x^2)/((b^2-4*a*c)^2*(a+b*x^2+c*x^4))+3*a*b*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(5/2)");
  }

  // 1.3.2 Algebraic functions.input:399
  public void test0398() {
    check( //
        "Integrate[Sqrt[e*(a+b*x^2)/(c+d*x^2)]/x^5, x]", //
        "1/8*(b*c-a*d)*(b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(Sqrt[a]*Sqrt[e])]*Sqrt[e]/(a^(3/2)*c^(5/2))-1/4*(b*c-a*d)^2*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(c^2*(a-c*(a+b*x^2)/(c+d*x^2))^2)+1/8*(b*c-5*a*d)*(b*c-a*d)*Sqrt[e*(a+b*x^2)/(c+d*x^2)]/(a*c^2*(a-c*(a+b*x^2)/(c+d*x^2)))");
  }

  // 1.3.2 Algebraic functions.input:846
  public void test0399() {
    check( //
        "Integrate[Sqrt[a+b*Sqrt[c+d*x]]/x, x]", //
        "-2*ArcTanh[Sqrt[a+b*Sqrt[c+d*x]]/Sqrt[a-b*Sqrt[c]]]*Sqrt[a-b*Sqrt[c]]-2*ArcTanh[Sqrt[a+b*Sqrt[c+d*x]]/Sqrt[a+b*Sqrt[c]]]*Sqrt[a+b*Sqrt[c]]+4*Sqrt[a+b*Sqrt[c+d*x]]");
  }

  // 1.3.1 Rational functions.input:392
  public void test0400() {
    check( //
        "Integrate[(-1-x-x^3+x^4)/(-x^2+x^3), x]", //
        "(-1)/x+1/2*x^2-2*Log[1-x]+2*Log[x]");
  }

  // 1.3.2 Algebraic functions.input:1258
  public void test0401() {
    check( //
        "Integrate[(x-2*x^3)/Sqrt[2+3*x], x]", //
        "-10/81*(2+3*x)^(3/2)+8/135*(2+3*x)^(5/2)-4/567*(2+3*x)^(7/2)-4/81*Sqrt[2+3*x]");
  }

  // 1.3.1 Rational functions.input:646
  public void test0402() {
    check( //
        "Integrate[x/((1+x)^2*(1+x^2)), x]", //
        "1/2/(1+x)+1/2*ArcTan[x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:327
  public void test0403() {
    check( //
        "Integrate[x^3/Sqrt[3*x^2-4*x^4], x]", //
        "-3/32*ArcSin[1-8/3*x^2]-1/8*Sqrt[3*x^2-4*x^4]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:17
  public void test0404() {
    check( //
        "Integrate[1/(a^2+2*a*b*x^2+b^2*x^4)^(3/4), x]", //
        "x*(a+b*x^2)/(a*(a^2+2*a*b*x^2+b^2*x^4)^(3/4))");
  }

  // 1.2.1.4 (d+e x)^m (f+g x)^n (a+b x+c x^2)^p.input:56
  public void test0405() {
    check( //
        "Integrate[(d+e*x)^2/Sqrt[d^2-e^2*x^2], x]", //
        "3/2*d^2*ArcTan[e*x/Sqrt[d^2-e^2*x^2]]/e-3/2*d*Sqrt[d^2-e^2*x^2]/e-1/2*(d+e*x)*Sqrt[d^2-e^2*x^2]/e");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:292
  public void test0406() {
    check( //
        "Integrate[1/((a-b*x^2)^(3/2)*Sqrt[a^2-b^2*x^4]), x]", //
        "1/4*x*(a+b*x^2)/(a^2*Sqrt[a-b*x^2]*Sqrt[a^2-b^2*x^4])+3/4*ArcTanh[x*Sqrt[2]*Sqrt[b]/Sqrt[a+b*x^2]]*Sqrt[a-b*x^2]*Sqrt[a+b*x^2]/(a^2*Sqrt[2]*Sqrt[b]*Sqrt[a^2-b^2*x^4])");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:406
  public void test0407() {
    check( //
        "Integrate[Sqrt[2+3*x^2+x^4], x]", //
        "x*(2+x^2)/Sqrt[2+3*x^2+x^4]-(1+x^2)*EllipticE[ArcTan[x],1/2]*Sqrt[2]*Sqrt[(2+x^2)/(1+x^2)]/Sqrt[2+3*x^2+x^4]+2/3*(1+x^2)*EllipticF[ArcTan[x],1/2]*Sqrt[2]*Sqrt[(2+x^2)/(1+x^2)]/Sqrt[2+3*x^2+x^4]+1/3*x*Sqrt[2+3*x^2+x^4]");
  }

  // 1.2.4.2 (d x)^m (a x^q+b x^n+c x^(2 n-q))^p.input:72
  public void test0408() {
    check( //
        "Integrate[1/(x*Sqrt[a*x^2+b*x^3+c*x^4]), x]", //
        "1/2*b*ArcTanh[1/2*x*(2*a+b*x)/(Sqrt[a]*Sqrt[a*x^2+b*x^3+c*x^4])]/a^(3/2)-Sqrt[a*x^2+b*x^3+c*x^4]/(a*x^2)");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:331
  public void test0409() {
    check( //
        "Integrate[(1+x^2)/Sqrt[1+x^2+x^4], x]", //
        "x*Sqrt[1+x^2+x^4]/(1+x^2)-(1+x^2)*EllipticE[2*ArcTan[x],1/4]*Sqrt[(1+x^2+x^4)/(1+x^2)^2]/Sqrt[1+x^2+x^4]+(1+x^2)*EllipticF[2*ArcTan[x],1/4]*Sqrt[(1+x^2+x^4)/(1+x^2)^2]/Sqrt[1+x^2+x^4]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:673
  public void test0410() {
    check( //
        "Integrate[x^10*(a^2+2*a*b*x^2+b^2*x^4)^(5/2), x]", //
        "1/11*a^5*x^11*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+5/13*a^4*b*x^13*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+2/3*a^3*b^2*x^15*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+10/17*a^2*b^3*x^17*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+5/19*a*b^4*x^19*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)+1/21*b^5*x^21*Sqrt[a^2+2*a*b*x^2+b^2*x^4]/(a+b*x^2)");
  }

  // 1.3.1 Rational functions.input:455
  public void test0411() {
    check( //
        "Integrate[(9+x^4)/(x^2*(9+x^2)), x]", //
        "(-1)/x+x-10/3*ArcTan[1/3*x]");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:977
  public void test0412() {
    check( //
        "Integrate[x/(a+b*x^2+c*x^4)^3, x]", //
        "1/4*(-b-2*c*x^2)/((b^2-4*a*c)*(a+b*x^2+c*x^4)^2)+3/2*c*(b+2*c*x^2)/((b^2-4*a*c)^2*(a+b*x^2+c*x^4))-6*c^2*ArcTanh[(b+2*c*x^2)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(5/2)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:285
  public void test0413() {
    check( //
        "Integrate[(b*x^2+c*x^4)^(3/2)/x^13, x]", //
        "-1/9*(b*x^2+c*x^4)^(5/2)/(b*x^14)+4/63*c*(b*x^2+c*x^4)^(5/2)/(b^2*x^12)-8/315*c^2*(b*x^2+c*x^4)^(5/2)/(b^3*x^10)");
  }

  // 1.3.1 Rational functions.input:514
  public void test0414() {
    check( //
        "Integrate[(-1+x^5)/(-x+x^3), x]", //
        "x+1/3*x^3+Log[x]-Log[1+x]");
  }

  // 1.3.1 Rational functions.input:490
  public void test0415() {
    check( //
        "Integrate[(4+3*x+x^2)/(x+x^2), x]", //
        "x+4*Log[x]-2*Log[1+x]");
  }

  // 1.3.2 Algebraic functions.input:773
  public void test0416() {
    check( //
        "Integrate[1/(-x^(1/3)+x^(2/3)), x]", //
        "3*x^(1/3)+3*Log[1-x^(1/3)]");
  }

  // 1.3.2 Algebraic functions.input:779
  public void test0417() {
    check( //
        "Integrate[x/(x+4*Sqrt[x]), x]", //
        "x+32*Log[4+Sqrt[x]]-8*Sqrt[x]");
  }

  // 1.3.2 Algebraic functions.input:898
  public void test0418() {
    check( //
        "Integrate[1/(x*Sqrt[a+b*(c*x)^m]), x]", //
        "-2*ArcTanh[Sqrt[a+b*(c*x)^m]/Sqrt[a]]/(m*Sqrt[a])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2199
  public void test0419() {
    check( //
        "Integrate[(a+b*x)*(d+e*x)^3/(a^2+2*a*b*x+b^2*x^2)^3, x]", //
        "-1/4*(d+e*x)^4/((b*d-a*e)*(a+b*x)^4)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:598
  public void test0420() {
    check( //
        "Integrate[1/(1+2*x^2+x^4), x]", //
        "1/2*x/(1+x^2)+1/2*ArcTan[x]");
  }

  // 1.2.3.2 (d x)^m (a+b x^n+c x^(2 n))^p.input:828
  public void test0421() {
    check( //
        "Integrate[(d*f+e*f*x)^3*(a+b*(d+e*x)^2+c*(d+e*x)^4)^2, x]", //
        "1/4*a^2*f^3*(d+e*x)^4/e+1/3*a*b*f^3*(d+e*x)^6/e+1/8*(b^2+2*a*c)*f^3*(d+e*x)^8/e+1/5*b*c*f^3*(d+e*x)^10/e+1/12*c^2*f^3*(d+e*x)^12/e");
  }

  // 1.2.2.4 (f x)^m (d+e x^2)^q (a+b x^2+c x^4)^p.input:46
  public void test0422() {
    check( //
        "Integrate[(2+3*x^2)*(5+x^4)^(3/2), x]", //
        "1/21*x*(6+7*x^2)*(5+x^4)^(3/2)+2/7*x*(10+7*x^2)*Sqrt[5+x^4]+20*x*Sqrt[5+x^4]/(x^2+Sqrt[5])-20*5^(1/4)*EllipticE[2*ArcTan[x/5^(1/4)],1/2]*(x^2+Sqrt[5])*Sqrt[(5+x^4)/(x^2+Sqrt[5])^2]/Sqrt[5+x^4]+10/7*5^(1/4)*EllipticF[2*ArcTan[x/5^(1/4)],1/2]*(x^2+Sqrt[5])*(7+2*Sqrt[5])*Sqrt[(5+x^4)/(x^2+Sqrt[5])^2]/Sqrt[5+x^4]");
  }

  // 1.3.2 Algebraic functions.input:747
  public void test0423() {
    check( //
        "Integrate[x/(a*c+b*c*x^2+d*Sqrt[a+b*x^2]), x]", //
        "Log[d+c*Sqrt[a+b*x^2]]/(b*c)");
  }

  // 1.2.2.2 (d x)^m (a+b x^2+c x^4)^p.input:519
  public void test0424() {
    check( //
        "Integrate[(a^2+2*a*b*x^2+b^2*x^4)^3/x^6, x]", //
        "-1/5*a^6/x^5-2*a^5*b/x^3-15*a^4*b^2/x+20*a^3*b^3*x+5*a^2*b^4*x^3+6/5*a*b^5*x^5+1/7*b^6*x^7");
  }

  // 1.2.1.4 (d+e x)^m (f+g x)^n (a+b x+c x^2)^p.input:1090
  public void test0425() {
    check( //
        "Integrate[(15*d^2+20*d*e*x+8*e^2*x^2)/((d+e*x)^(1/2)*Sqrt[a+b*x]), x]", //
        "2*(8*b^2*d^2-8*a*b*d*e+3*a^2*e^2)*ArcTanh[Sqrt[e]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[d+e*x])]/(b^(5/2)*Sqrt[e])+4*e*(a+b*x)^(3/2)*Sqrt[d+e*x]/b^2+2*(7*b*d-5*a*e)*Sqrt[a+b*x]*Sqrt[d+e*x]/b^2");
  }

  // 1.3.1 Rational functions.input:491
  public void test0426() {
    check( //
        "Integrate[(4+x+3*x^2)/(x+x^3), x]", //
        "ArcTan[x]+4*Log[x]-1/2*Log[1+x^2]");
  }

  // 1.3.1 Rational functions.input:406
  public void test0427() {
    check( //
        "Integrate[(4+x^2)/((1+x^2)*(2+x^2)), x]", //
        "3*ArcTan[x]-ArcTan[x/Sqrt[2]]*Sqrt[2]");
  }

  // 1.3.1 Rational functions.input:512
  public void test0428() {
    check( //
        "Integrate[(1+x^3)/(-x+x^3), x]", //
        "x+Log[1-x]-Log[x]");
  }
}
