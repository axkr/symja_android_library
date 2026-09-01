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
public class AlgebraicFunctions4 extends AbstractRubiTestCase {
  static boolean init = true;

  public AlgebraicFunctions4(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("AlgebraicFunctions4");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:534
  public void test0001() {
    check( //
        "Integrate[1/((-a+b*x)*Sqrt[x]), x]", //
        "-2*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[a]]/(Sqrt[a]*Sqrt[b])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:694
  public void test0002() {
    check( //
        "Integrate[1/((2+b*x)^(5/2)*Sqrt[x]), x]", //
        "1/3*Sqrt[x]/(2+b*x)^(3/2)+1/3*Sqrt[x]/Sqrt[2+b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:708
  public void test0003() {
    check( //
        "Integrate[1/(x^(5/2)*(2-b*x)^(3/2)), x]", //
        "1/(x^(3/2)*Sqrt[2-b*x])-2/3*Sqrt[2-b*x]/x^(3/2)-2/3*b*Sqrt[2-b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:854
  public void test0004() {
    check( //
        "Integrate[x*(c*x^2)^(3/2)*(a+b*x), x]", //
        "1/5*a*c*x^4*Sqrt[c*x^2]+1/6*b*c*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:870
  public void test0005() {
    check( //
        "Integrate[x^3*(a+b*x)/Sqrt[c*x^2], x]", //
        "1/3*a*x^4/Sqrt[c*x^2]+1/4*b*x^5/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:884
  public void test0006() {
    check( //
        "Integrate[(a+b*x)/(x^3*(c*x^2)^(3/2)), x]", //
        "-1/5*a/(c*x^4*Sqrt[c*x^2])-1/4*b/(c*x^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:902
  public void test0007() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[c*x^2]/x, x]", //
        "1/3*(a+b*x)^3*Sqrt[c*x^2]/(b*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:916
  public void test0008() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x, x]", //
        "1/5*a^2*c^2*x^4*Sqrt[c*x^2]+1/3*a*b*c^2*x^5*Sqrt[c*x^2]+1/7*b^2*c^2*x^6*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:932
  public void test0009() {
    check( //
        "Integrate[x^3*(a+b*x)^2/(c*x^2)^(3/2), x]", //
        "1/3*x*(a+b*x)^3/(b*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:946
  public void test0010() {
    check( //
        "Integrate[(a+b*x)^2/(x^3*(c*x^2)^(5/2)), x]", //
        "-1/7*a^2/(c^2*x^6*Sqrt[c*x^2])-1/3*a*b/(c^2*x^5*Sqrt[c*x^2])-1/5*b^2/(c^2*x^4*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:994
  public void test0011() {
    check( //
        "Integrate[1/(x*(c*x^2)^(3/2)*(a+b*x)), x]", //
        "-b^2/(a^3*c*Sqrt[c*x^2])+(-1/3)/(a*c*x^2*Sqrt[c*x^2])+1/2*b/(a^2*c*x*Sqrt[c*x^2])-b^3*x*Log[x]/(a^4*c*Sqrt[c*x^2])+b^3*x*Log[a+b*x]/(a^4*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1012
  public void test0012() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^4*(a+b*x)^2), x]", //
        "c*Sqrt[c*x^2]/(a*x*(a+b*x))+c*Log[x]*Sqrt[c*x^2]/(a^2*x)-c*Log[a+b*x]*Sqrt[c*x^2]/(a^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1028
  public void test0013() {
    check( //
        "Integrate[x^2/((c*x^2)^(3/2)*(a+b*x)^2), x]", //
        "x/(a*c*(a+b*x)*Sqrt[c*x^2])+x*Log[x]/(a^2*c*Sqrt[c*x^2])-x*Log[a+b*x]/(a^2*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1052
  public void test0014() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n/x^2, x]", //
        "-a^3*c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^4*(1+n)*x)+3*a^2*c^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^4*(2+n)*x)-3*a*c^2*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^4*(3+n)*x)+c^2*(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^4*(4+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1139
  public void test0015() {
    check( //
        "Integrate[(a+b*x)/(a*d/b+d*x)^3, x]", //
        "-b^2/(d^3*(a+b*x))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1223
  public void test0016() {
    check( //
        "Integrate[(1+x)^(1/2)/(1-x)^(11/2), x]", //
        "1/9*(1+x)^(3/2)/(1-x)^(9/2)+1/21*(1+x)^(3/2)/(1-x)^(7/2)+2/105*(1+x)^(3/2)/(1-x)^(5/2)+2/315*(1+x)^(3/2)/(1-x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1237
  public void test0017() {
    check( //
        "Integrate[(1+x)^(3/2)/(1-x)^(15/2), x]", //
        "1/13*(1+x)^(5/2)/(1-x)^(13/2)+4/143*(1+x)^(5/2)/(1-x)^(11/2)+4/429*(1+x)^(5/2)/(1-x)^(9/2)+8/3003*(1+x)^(5/2)/(1-x)^(7/2)+8/15015*(1+x)^(5/2)/(1-x)^(5/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1251
  public void test0018() {
    check( //
        "Integrate[(1+x)^(5/2)/(1-x)^(15/2), x]", //
        "1/13*(1+x)^(7/2)/(1-x)^(13/2)+3/143*(1+x)^(7/2)/(1-x)^(11/2)+2/429*(1+x)^(7/2)/(1-x)^(9/2)+2/3003*(1+x)^(7/2)/(1-x)^(7/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1269
  public void test0019() {
    check( //
        "Integrate[1/((1-x)^(11/2)*(1+x)^(1/2)), x]", //
        "1/9*Sqrt[1+x]/(1-x)^(9/2)+4/63*Sqrt[1+x]/(1-x)^(7/2)+4/105*Sqrt[1+x]/(1-x)^(5/2)+8/315*Sqrt[1+x]/(1-x)^(3/2)+8/315*Sqrt[1+x]/Sqrt[1-x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1299
  public void test0020() {
    check( //
        "Integrate[1/((a+a*x)^(7/2)*(c-c*x)^(7/2)), x]", //
        "1/5*x/(a*c*(a+a*x)^(5/2)*(c-c*x)^(5/2))+4/15*x/(a^2*c^2*(a+a*x)^(3/2)*(c-c*x)^(3/2))+8/15*x/(a^3*c^3*Sqrt[a+a*x]*Sqrt[c-c*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1391
  public void test0021() {
    check( //
        "Integrate[1/((a-I*a*x)^(7/4)*(a+I*a*x)^(9/4)), x]", //
        "(-2/3*I)/(a^2*(a-I*a*x)^(3/4)*(a+I*a*x)^(5/4))+8/15*I*(a-I*a*x)^(1/4)/(a^3*(a+I*a*x)^(5/4))+16/15*I*(a-I*a*x)^(1/4)/(a^4*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1502
  public void test0022() {
    check( //
        "Integrate[(c+d*x)^10/(a+b*x)^19, x]", //
        "-1/18*(c+d*x)^11/((b*c-a*d)*(a+b*x)^18)+7/306*d*(c+d*x)^11/((b*c-a*d)^2*(a+b*x)^17)-7/816*d^2*(c+d*x)^11/((b*c-a*d)^3*(a+b*x)^16)+7/2448*d^3*(c+d*x)^11/((b*c-a*d)^4*(a+b*x)^15)-1/1224*d^4*(c+d*x)^11/((b*c-a*d)^5*(a+b*x)^14)+1/5304*d^5*(c+d*x)^11/((b*c-a*d)^6*(a+b*x)^13)-1/31824*d^6*(c+d*x)^11/((b*c-a*d)^7*(a+b*x)^12)+1/350064*d^7*(c+d*x)^11/((b*c-a*d)^8*(a+b*x)^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1583
  public void test0023() {
    check( //
        "Integrate[1/(c+d*x)^3, x]", //
        "(-1/2)/(d*(c+d*x)^2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1707
  public void test0024() {
    check( //
        "Integrate[(c+d*x)^(1/2)/(a+b*x)^(13/2), x]", //
        "-2/11*(c+d*x)^(3/2)/((b*c-a*d)*(a+b*x)^(11/2))+16/99*d*(c+d*x)^(3/2)/((b*c-a*d)^2*(a+b*x)^(9/2))-32/231*d^2*(c+d*x)^(3/2)/((b*c-a*d)^3*(a+b*x)^(7/2))+128/1155*d^3*(c+d*x)^(3/2)/((b*c-a*d)^4*(a+b*x)^(5/2))-256/3465*d^4*(c+d*x)^(3/2)/((b*c-a*d)^5*(a+b*x)^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1737
  public void test0025() {
    check( //
        "Integrate[1/((a+b*x)^(5/2)*(c+d*x)^(1/2)), x]", //
        "-2/3*Sqrt[c+d*x]/((b*c-a*d)*(a+b*x)^(3/2))+4/3*d*Sqrt[c+d*x]/((b*c-a*d)^2*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1779
  public void test0026() {
    check( //
        "Integrate[1/(Sqrt[-1-b*x]*Sqrt[2+b*x]), x]", //
        "ArcSin[3+2*b*x]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1985
  public void test0027() {
    check( //
        "Integrate[1/((a+b*x)^(15/4)*(c+d*x)^(5/4)), x]", //
        "(-4/11)/((b*c-a*d)*(a+b*x)^(11/4)*(c+d*x)^(1/4))+48/77*d/((b*c-a*d)^2*(a+b*x)^(7/4)*(c+d*x)^(1/4))-128/77*d^2/((b*c-a*d)^3*(a+b*x)^(3/4)*(c+d*x)^(1/4))-512/77*d^3*(a+b*x)^(1/4)/((b*c-a*d)^4*(c+d*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2155
  public void test0028() {
    check( //
        "Integrate[(a+b*x)^n*(c+d*x)^(-4-n), x]", //
        "(a+b*x)^(1+n)*(c+d*x)^(-3-n)/((b*c-a*d)*(3+n))+2*b*(a+b*x)^(1+n)*(c+d*x)^(-2-n)/((b*c-a*d)^2*(2+n)*(3+n))+2*b^2*(a+b*x)^(1+n)*(c+d*x)^(-1-n)/((b*c-a*d)^3*(1+n)*(2+n)*(3+n))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2200
  public void test0029() {
    check( //
        "Integrate[(-1)/x^2+10/x+6*Sqrt[x], x]", //
        "1/x+4*x^(3/2)+10*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:23
  public void test0030() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^6, x]", //
        "-1/5*a^4*c^3/x^5+1/2*a^3*b*c^3/x^4-a*b^3*c^3/x^2+b^4*c^3/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:37
  public void test0031() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^7, x]", //
        "-1/6*c^4*(a-b*x)^5/x^6-7/30*b*c^4*(a-b*x)^5/(a*x^5)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:51
  public void test0032() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^7, x]", //
        "-1/6*a^6*c^5/x^6+4/5*a^5*b*c^5/x^5-5/4*a^4*b^2*c^5/x^4+5/2*a^2*b^4*c^5/x^2-4*a*b^5*c^5/x-b^6*c^5*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:165
  public void test0033() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^12, x]", //
        "-1/11*a^5*A/x^11-1/10*a^4*(5*A*b+a*B)/x^10-5/9*a^3*b*(2*A*b+a*B)/x^9-5/4*a^2*b^2*(A*b+a*B)/x^8-5/7*a*b^3*(A*b+2*a*B)/x^7-1/6*b^4*(A*b+5*a*B)/x^6-1/5*b^5*B/x^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:179
  public void test0034() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^3, x]", //
        "-1/2*a^10*A/x^2-a^9*(10*A*b+a*B)/x+15*a^7*b^2*(8*A*b+3*a*B)*x+15*a^6*b^3*(7*A*b+4*a*B)*x^2+14*a^5*b^4*(6*A*b+5*a*B)*x^3+21/2*a^4*b^5*(5*A*b+6*a*B)*x^4+6*a^3*b^6*(4*A*b+7*a*B)*x^5+5/2*a^2*b^7*(3*A*b+8*a*B)*x^6+5/7*a*b^8*(2*A*b+9*a*B)*x^7+1/8*b^9*(A*b+10*a*B)*x^8+1/9*b^10*B*x^9+5*a^8*b*(9*A*b+2*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:193
  public void test0035() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^17, x]", //
        "-1/16*A*(a+b*x)^11/(a*x^16)+1/240*(5*A*b-16*a*B)*(a+b*x)^11/(a^2*x^15)-1/840*b*(5*A*b-16*a*B)*(a+b*x)^11/(a^3*x^14)+1/3640*b^2*(5*A*b-16*a*B)*(a+b*x)^11/(a^4*x^13)-1/21840*b^3*(5*A*b-16*a*B)*(a+b*x)^11/(a^5*x^12)+1/240240*b^4*(5*A*b-16*a*B)*(a+b*x)^11/(a^6*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:437
  public void test0036() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^(3/2), x]", //
        "2/3*b*(A*b+2*a*B)*x^(3/2)+2/5*b^2*B*x^(5/2)-2*a^2*A/Sqrt[x]+2*a*(2*A*b+a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:454
  public void test0037() {
    check( //
        "Integrate[(A+B*x)*Sqrt[x]/(a+b*x), x]", //
        "2/3*B*x^(3/2)/b-2*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]*Sqrt[a]/b^(5/2)+2*(A*b-a*B)*Sqrt[x]/b^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:585
  public void test0038() {
    check( //
        "Integrate[x^2*(c+d*x)^(5/2)/(a+b*x), x]", //
        "2/3*a^2*(b*c-a*d)*(c+d*x)^(3/2)/b^4+2/5*a^2*(c+d*x)^(5/2)/b^3-2/7*(b*c+a*d)*(c+d*x)^(7/2)/(b^2*d^2)+2/9*(c+d*x)^(9/2)/(b*d^2)-2*a^2*(b*c-a*d)^(5/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x]/Sqrt[b*c-a*d]]/b^(11/2)+2*a^2*(b*c-a*d)^2*Sqrt[c+d*x]/b^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:626
  public void test0039() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/x^(7/2), x]", //
        "-2/5*A*(a+b*x)^(3/2)/(a*x^(5/2))+2/15*(2*A*b-5*a*B)*(a+b*x)^(3/2)/(a^2*x^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:640
  public void test0040() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/x^(13/2), x]", //
        "-2/11*A*(a+b*x)^(5/2)/(a*x^(11/2))+2/99*(6*A*b-11*a*B)*(a+b*x)^(5/2)/(a^2*x^(9/2))-8/693*b*(6*A*b-11*a*B)*(a+b*x)^(5/2)/(a^3*x^(7/2))+16/3465*b^2*(6*A*b-11*a*B)*(a+b*x)^(5/2)/(a^4*x^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:654
  public void test0041() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/x^(19/2), x]", //
        "-2/17*A*(a+b*x)^(7/2)/(a*x^(17/2))+2/255*(10*A*b-17*a*B)*(a+b*x)^(7/2)/(a^2*x^(15/2))-16/3315*b*(10*A*b-17*a*B)*(a+b*x)^(7/2)/(a^3*x^(13/2))+32/12155*b^2*(10*A*b-17*a*B)*(a+b*x)^(7/2)/(a^4*x^(11/2))-128/109395*b^3*(10*A*b-17*a*B)*(a+b*x)^(7/2)/(a^5*x^(9/2))+256/765765*b^4*(10*A*b-17*a*B)*(a+b*x)^(7/2)/(a^6*x^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:718
  public void test0042() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Sqrt[a+b*x]/x, x]", //
        "-2*c^(5/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]+1/8*(5*b^3*c^3+15*a*b^2*c^2*d-5*a^2*b*c*d^2+a^3*d^3)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*Sqrt[d])+1/12*(5*b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/b+1/3*(c+d*x)^(5/2)*Sqrt[a+b*x]+1/8*(5*b*c-a*d)*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/b^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:748
  public void test0043() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x^3*(c+d*x)^(5/2)), x]", //
        "1/4*(b^2*c^2+10*a*b*c*d-35*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(9/2))-1/12*d*(3*b*c-35*a*d)*Sqrt[a+b*x]/(a*c^3*(c+d*x)^(3/2))-1/2*Sqrt[a+b*x]/(c*x^2*(c+d*x)^(3/2))-1/4*(b*c-7*a*d)*Sqrt[a+b*x]/(a*c^2*x*(c+d*x)^(3/2))-1/12*d*(3*b^2*c^2-100*a*b*c*d+105*a^2*d^2)*Sqrt[a+b*x]/(a*c^4*(b*c-a*d)*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:766
  public void test0044() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^2, x]", //
        "-(a+b*x)^(3/2)*(c+d*x)^(3/2)/x-3*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]*Sqrt[c]+3/4*(b^2*c^2+6*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])+3/2*b*(c+d*x)^(3/2)*Sqrt[a+b*x]+3/4*(b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:780
  public void test0045() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(5/2)/x^7, x]", //
        "1/60*(7*b*c+5*a*d)*(a+b*x)^(3/2)*(c+d*x)^(7/2)/(a*c^2*x^5)-1/6*(a+b*x)^(5/2)*(c+d*x)^(7/2)/(a*c*x^6)-1/512*(b*c-a*d)^5*(7*b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(7/2))-1/768*(b*c-a*d)^3*(7*b*c+5*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^3*c^3*x^2)+1/960*(b*c-a*d)^2*(7*b*c+5*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a^2*c^3*x^3)+1/160*(b*c-a*d)*(7*b*c+5*a*d)*(c+d*x)^(7/2)*Sqrt[a+b*x]/(a*c^3*x^4)+1/512*(b*c-a*d)^4*(7*b*c+5*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:796
  public void test0046() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^3*(c+d*x)^(3/2)), x]", //
        "-3/4*(b*c-5*a*d)*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(7/2)*Sqrt[a])-1/4*(b*c-5*a*d)*(a+b*x)^(3/2)/(a*c^2*x*Sqrt[c+d*x])-1/2*(a+b*x)^(5/2)/(a*c*x^2*Sqrt[c+d*x])+3/4*(b*c-5*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(a*c^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:814
  public void test0047() {
    check( //
        "Integrate[(a+b*x)^(5/2)*Sqrt[c+d*x]/x^4, x]", //
        "-1/8*(5*b^3*c^3+15*a*b^2*c^2*d-5*a^2*b*c*d^2+a^3*d^3)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(5/2)*Sqrt[a])+2*b^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]-1/12*(5*b*c+a*d)*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c*x^2)-1/3*(a+b*x)^(5/2)*Sqrt[c+d*x]/x^3-1/8*(5*b*c-a*d)*(b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:828
  public void test0048() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x, x]", //
        "1/8*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/d+1/5*(a+b*x)^(5/2)*(c+d*x)^(5/2)-2*a^(5/2)*c^(5/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]+1/128*(b*c+a*d)*(3*b^4*c^4-28*a*b^3*c^3*d+178*a^2*b^2*c^2*d^2-28*a^3*b*c*d^3+3*a^4*d^4)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(5/2)*d^(5/2))+1/192*(3*b^3*c^3-19*a*b^2*c^2*d+109*a^2*b*c*d^2+3*a^3*d^3)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(b*d^2)-1/48*(3*b^2*c^2-16*a*b*c*d-3*a^2*d^2)*(c+d*x)^(5/2)*Sqrt[a+b*x]/d^2+1/128*(3*b^4*c^4-22*a*b^3*c^3*d+128*a^2*b^2*c^2*d^2+22*a^3*b*c*d^3-3*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^2*d^2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:844
  public void test0049() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^5*Sqrt[c+d*x]), x]", //
        "5/64*(b*c-a*d)^3*(b*c+7*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(9/2))+5/96*(b*c-a*d)*(b*c+7*a*d)*(a+b*x)^(3/2)*Sqrt[c+d*x]/(a*c^3*x^2)+1/24*(b*c+7*a*d)*(a+b*x)^(5/2)*Sqrt[c+d*x]/(a*c^2*x^3)-1/4*(a+b*x)^(7/2)*Sqrt[c+d*x]/(a*c*x^4)+5/64*(b*c-a*d)^2*(b*c+7*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^4*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:858
  public void test0050() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^2*(c+d*x)^(5/2)), x]", //
        "5/3*(b*c-a*d)*(a+b*x)^(3/2)/(c^2*(c+d*x)^(3/2))-(a+b*x)^(5/2)/(c*x*(c+d*x)^(3/2))-5*a^(3/2)*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/c^(7/2)+5*a*(b*c-a*d)*Sqrt[a+b*x]/(c^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:876
  public void test0051() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x*Sqrt[a+b*x]), x]", //
        "-2*c^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[a]+(3*b*c-a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]/b^(3/2)+d*Sqrt[a+b*x]*Sqrt[c+d*x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:920
  public void test0052() {
    check( //
        "Integrate[x/((c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "-2/3*c*Sqrt[a+b*x]/(d*(b*c-a*d)*(c+d*x)^(3/2))+2/3*(b*c-3*a*d)*Sqrt[a+b*x]/(d*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:938
  public void test0053() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^3*(a+b*x)^(3/2)), x]", //
        "-3/4*(b*c-a*d)*(5*b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*Sqrt[c])+1/4*(5*b*c-a*d)*(c+d*x)^(3/2)/(a^2*c*x*Sqrt[a+b*x])-1/2*(c+d*x)^(5/2)/(a*c*x^2*Sqrt[a+b*x])+3/4*(b*c-a*d)*(5*b*c-a*d)*Sqrt[c+d*x]/(a^3*c*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:988
  public void test0054() {
    check( //
        "Integrate[x^3/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "-2/3*x^3/((b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))-4*a^2*c/(b^2*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])-4/3*c*(b^2*c^2+3*a^2*d^2)*Sqrt[a+b*x]/(b^2*d*(b*c-a*d)^3*(c+d*x)^(3/2))+4/3*c*(b^2*c^2-6*a*b*c*d-3*a^2*d^2)*Sqrt[a+b*x]/(b*d*(b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1006
  public void test0055() {
    check( //
        "Integrate[x^2*(a+b*x)^n/(-a-b*x)^n, x]", //
        "1/3*x^3*(a+b*x)^n/(-a-b*x)^n");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1271
  public void test0056() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^2, x]", //
        "3*b*(b*d-a*e)*(2*b*B*d-A*b*e-a*B*e)*x/e^4-(b*d-a*e)^3*(B*d-A*e)/(e^5*(d+e*x))-1/2*b^2*(4*b*B*d-A*b*e-3*a*B*e)*(d+e*x)^2/e^5+1/3*b^3*B*(d+e*x)^3/e^5-(b*d-a*e)^2*(4*b*B*d-3*A*b*e-a*B*e)*Log[d+e*x]/e^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1299
  public void test0057() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^11, x]", //
        "-1/10*(B*d-A*e)*(a+b*x)^7/(e*(b*d-a*e)*(d+e*x)^10)+1/90*(7*b*B*d+3*A*b*e-10*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^2*(d+e*x)^9)+1/360*b*(7*b*B*d+3*A*b*e-10*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^3*(d+e*x)^8)+1/2520*b^2*(7*b*B*d+3*A*b*e-10*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^4*(d+e*x)^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1343
  public void test0058() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^4/(a+b*x), x]", //
        "(A*b-a*B)*e*(b*d-a*e)^3*x/b^5+1/2*(A*b-a*B)*(b*d-a*e)^2*(d+e*x)^2/b^4+1/3*(A*b-a*B)*(b*d-a*e)*(d+e*x)^3/b^3+1/4*(A*b-a*B)*(d+e*x)^4/b^2+1/5*B*(d+e*x)^5/(b*e)+(A*b-a*B)*(b*d-a*e)^4*Log[a+b*x]/b^6");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1357
  public void test0059() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)/(a+b*x)^2, x]", //
        "B*e*x/b^2-(A*b-a*B)*(b*d-a*e)/(b^3*(a+b*x))+(b*B*d+A*b*e-2*a*B*e)*Log[a+b*x]/b^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1895
  public void test0060() {
    check( //
        "Integrate[(2+3*x)^8*(3+5*x)/(1-2*x)^3, x]", //
        "63412811/2048/(1-2*x)^2+(-246239357/1024)/(1-2*x)-120864213/256*x-118841283/512*x^2-16042509/128*x^3-7568235/128*x^4-213597/10*x^5-162567/32*x^6-32805/56*x^7-106237047/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2188
  public void test0061() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^4/(3+5*x), x]", //
        "2/9375*(1-2*x)^(3/2)-136419/25000*(1-2*x)^(5/2)+34371/7000*(1-2*x)^(7/2)-321/200*(1-2*x)^(9/2)+81/440*(1-2*x)^(11/2)-22/15625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/15625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2540
  public void test0062() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/(d+e*x)^(11/2), x]", //
        "-2/9*(B*d-A*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)*(d+e*x)^(9/2))+2/63*(5*b*B*d+4*A*b*e-9*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^2*(d+e*x)^(7/2))+4/315*b*(5*b*B*d+4*A*b*e-9*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^3*(d+e*x)^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2554
  public void test0063() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(A+B*x)/(d+e*x)^(15/2), x]", //
        "-2/13*(B*d-A*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)*(d+e*x)^(13/2))+2/143*(7*b*B*d+6*A*b*e-13*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^2*(d+e*x)^(11/2))+8/1287*b*(7*b*B*d+6*A*b*e-13*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^3*(d+e*x)^(9/2))+16/9009*b^2*(7*b*B*d+6*A*b*e-13*a*B*e)*(a+b*x)^(7/2)/(e*(b*d-a*e)^4*(d+e*x)^(7/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2646
  public void test0064() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^4*(3+5*x)^(3/2)), x]", //
        "1463447/392*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-639565/1176*Sqrt[1-2*x]/Sqrt[3+5*x]+1/3*Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x])+81/28*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+14101/392*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2664
  public void test0065() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x), x]", //
        "14/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+4091/540*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1/6*(1-2*x)^(3/2)*Sqrt[3+5*x]+107/180*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2678
  public void test0066() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^5, x]", //
        "1/4*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-43923/3136*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-121/224*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+11/8*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-3993/3136*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2692
  public void test0067() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^7, x]", //
        "1/14*(1-2*x)^(5/2)*(3+5*x)^(7/2)/(2+3*x)^6+9/20*(1-2*x)^(3/2)*(3+5*x)^(7/2)/(2+3*x)^5-4348377/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-11979/12544*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1089/2240*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3+297/160*(3+5*x)^(7/2)*Sqrt[1-2*x]/(2+3*x)^4-395307/175616*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2722
  public void test0068() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^4*(3+5*x)^(5/2)), x]", //
        "-4246733/56*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-204595/168*Sqrt[1-2*x]/(3+5*x)^(3/2)+7/9*Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2))+301/36*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+24469/168*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+618645/56*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2754
  public void test0069() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x), x]", //
        "37/360*(1-2*x)^(3/2)*(3+5*x)^(5/2)+1/15*(1-2*x)^(5/2)*(3+5*x)^(5/2)+98/729*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+109715471/9331200*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-14557/28800*(3+5*x)^(3/2)*Sqrt[1-2*x]+4783/32400*(3+5*x)^(5/2)*Sqrt[1-2*x]-1994287/3110400*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2770
  public void test0070() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)*Sqrt[3+5*x]), x]", //
        "-98/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-17687/1350*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-1/15*(1-2*x)^(3/2)*Sqrt[3+5*x]-239/450*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2784
  public void test0071() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^3*(3+5*x)^(3/2)), x]", //
        "1815/4*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+1/2*(1-2*x)^(5/2)/((2+3*x)^2*Sqrt[3+5*x])+55/4*(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-1815/4*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2798
  public void test0072() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^6*(3+5*x)^(5/2)), x]", //
        "7/15*(1-2*x)^(3/2)/((2+3*x)^5*(3+5*x)^(3/2))-46975917593/6272*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-754386765/6272*Sqrt[1-2*x]/(3+5*x)^(3/2)+1001/120*Sqrt[1-2*x]/((2+3*x)^4*(3+5*x)^(3/2))+53009/720*Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2))+3329689/4032*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+270667969/18816*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+20529722435/18816*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2830
  public void test0073() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^4*Sqrt[1-2*x]), x]", //
        "-6655/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-55/588*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1/21*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-605/2744*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2846
  public void test0074() {
    check( //
        "Integrate[(2+3*x)^3/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "2493/400*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/55*(2+3*x)^2*Sqrt[1-2*x]/Sqrt[3+5*x]-3/4400*(979+300*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2860
  public void test0075() {
    check( //
        "Integrate[1/((2+3*x)*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-18*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-10/33*Sqrt[1-2*x]/(3+5*x)^(3/2)+950/363*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2878
  public void test0076() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^4), x]", //
        "-7435/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-1/7*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3-5/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+565/2744*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2922
  public void test0077() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^3*(3+5*x)^(3/2)), x]", //
        "177255/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-6205/7546)/(Sqrt[1-2*x]*Sqrt[3+5*x])+3/14/((2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x])+555/196/((2+3*x)*Sqrt[1-2*x]*Sqrt[3+5*x])-3125575/166012*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2954
  public void test0078() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(5/2)*(2+3*x)^4), x]", //
        "-9395/19208*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^3)+465/9604*Sqrt[3+5*x]/Sqrt[1-2*x]-32/147*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-23/196*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])-85/2744*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2970
  public void test0079() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "9/2*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+49/66*Sqrt[3+5*x]/(1-2*x)^(3/2)-448/363*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2984
  public void test0080() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^2*(3+5*x)^(3/2)), x]", //
        "3105/343*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-190/1617)/((1-2*x)^(3/2)*Sqrt[3+5*x])+3/7/((1-2*x)^(3/2)*(2+3*x)*Sqrt[3+5*x])+(-3830/124509)/(Sqrt[1-2*x]*Sqrt[3+5*x])-1840225/1369599*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3000
  public void test0081() {
    check( //
        "Integrate[1/(Sqrt[a+b*x]*Sqrt[c+d*x]*Sqrt[e+f*x]), x]", //
        "2*EllipticF[ArcSin[Sqrt[d]*Sqrt[a+b*x]/Sqrt[-b*c+a*d]],(b*c-a*d)*f/(d*(b*e-a*f))]*Sqrt[-b*c+a*d]*Sqrt[b*(c+d*x)/(b*c-a*d)]*Sqrt[b*(e+f*x)/(b*e-a*f)]/(b*Sqrt[d]*Sqrt[c+d*x]*Sqrt[e+f*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3020
  public void test0082() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x], x]", //
        "-1508889271/7087500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-11346991/1771875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-23/2475*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+2/55*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-342971/866250*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-543/9625*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-11346991/3898125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3034
  public void test0083() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(5/2), x]", //
        "-2209/567*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+494/567*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/9*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)-118/63*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+2470/567*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3050
  public void test0084() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(7/2)*Sqrt[3+5*x]), x]", //
        "-6388/245*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-64/245*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+2/5*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+92/35*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+6388/245*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3064
  public void test0085() {
    check( //
        "Integrate[Sqrt[1-2*x]/((3+5*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-136/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-4/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/3*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+136/33*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3082
  public void test0086() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2), x]", //
        "2/55*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(5/2)-90397364/1771875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-5442127/3543750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+62/2475*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-40703/433125*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-23/9625*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-5442127/7796250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3096
  public void test0087() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(5/2), x]", //
        "-2/9*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(3/2)-9587/1215*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2632/1215*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+362/27*(3+5*x)^(5/2)*Sqrt[1-2*x]/Sqrt[2+3*x]-614/27*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+2632/243*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3112
  public void test0088() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(7/2)/(3+5*x)^(3/2), x]", //
        "-1473539/1968750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-31288/984375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(3/2)*(2+3*x)^(7/2)/Sqrt[3+5*x]+5153/39375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+958/1575*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-8/45*(2+3*x)^(7/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-12601/196875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3126
  public void test0089() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-556/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-184/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+14/3*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-92/3*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+556/3*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3144
  public void test0090() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2), x]", //
        "106/3575*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(5/2)+2/65*(1-2*x)^(5/2)*(2+3*x)^(3/2)*(3+5*x)^(5/2)-30660308017/691031250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-923943703/691031250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+8318/482625*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-6794792/84459375*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+25603/1876875*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-923943703/1520268750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3158
  public void test0091() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(5/2), x]", //
        "-2/9*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(3/2)-452399/25515*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+135334/25515*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+370/27*(1-2*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[2+3*x]-31298/567*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+5260/567*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+135334/5103*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3174
  public void test0092() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(9/2)*Sqrt[3+5*x]), x]", //
        "-703480/1323*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-21160/1323*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(7/2)+76/9*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+10124/189*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+703480/1323*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3188
  public void test0093() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(5/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(5/2)*(2+3*x)^(5/2)/(3+5*x)^(3/2)+49321/109375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-32836/109375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-62/15*(1-2*x)^(3/2)*(2+3*x)^(5/2)/Sqrt[3+5*x]+22866/4375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-284/175*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+33778/21875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3206
  public void test0094() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)^(7/2)*Sqrt[1-2*x]), x]", //
        "-68/1715*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-584/1715*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-2/35*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+18/245*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+1752/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3220
  public void test0095() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-974/189*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-41/189*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/21*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]-205/189*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3252
  public void test0096() {
    check( //
        "Integrate[1/((2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-148/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-52/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/7*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+148/49*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3266
  public void test0097() {
    check( //
        "Integrate[Sqrt[2+3*x]/((3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "74/55*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-4/55*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/33*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-74/363*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3290
  public void test0098() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "139/10*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+23/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+(2+3*x)^(3/2)*Sqrt[3+5*x]/Sqrt[1-2*x]+2*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3304
  public void test0099() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(9/2)), x]", //
        "-189368/588245*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-23012/588245*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*Sqrt[3+5*x]/((2+3*x)^(7/2)*Sqrt[1-2*x])-229/343*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-2818/12005*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-5438/84035*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+189368/588245*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3320
  public void test0100() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+2/11*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3334
  public void test0101() {
    check( //
        "Integrate[(2+3*x)^(11/2)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "604915631/3781250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+18177329/3781250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/11*(2+3*x)^(9/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-4553/99825*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+380188/831875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+17427983/8318750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3352
  public void test0102() {
    check( //
        "Integrate[Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(5/2), x]", //
        "-34/7*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1/7*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+1/3*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)-68/231*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3366
  public void test0103() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(5/2)/(1-2*x)^(5/2), x]", //
        "1/3*(2+3*x)^(5/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2)-12601/140*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-69819/70*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-170/33*(2+3*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[1-2*x]-28283/462*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1355/154*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-12601/28*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3382
  public void test0104() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-5594/3773*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1196/3773*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[3+5*x]/((1-2*x)^(3/2)*Sqrt[2+3*x])+808/17787*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])+5594/41503*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3396
  public void test0105() {
    check( //
        "Integrate[(2+3*x)^(11/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^(9/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2))-90397364/831875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-5442127/1663750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-217/121*(2+3*x)^(7/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])+3218/19965*(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)+110519/1098075*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-5199979/3660250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:60
  public void test0106() {
    check( //
        "Integrate[(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x], x]", //
        "5/28*(-5+2*x)^(3/2)*(1+4*x)^(3/2)*Sqrt[2-3*x]+72479/756*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+136/105*(1+4*x)^(3/2)*Sqrt[2-3*x]*Sqrt[-5+2*x]-954811/22680*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-20911/3780*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:856
  public void test0107() {
    check( //
        "Integrate[(a+b*x^2)^(1/4), x]", //
        "2/3*x*(a+b*x^2)^(1/4)+2/3*a^(3/2)*(1+b*x^2/a)^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:914
  public void test0108() {
    check( //
        "Integrate[x^2/(a+b*x^2)^(5/4), x]", //
        "2*x/(b*(a+b*x^2)^(1/4))-4*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/(b^(3/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:928
  public void test0109() {
    check( //
        "Integrate[1/(a+b*x^2)^(11/4), x]", //
        "2/7*x/(a*(a+b*x^2)^(7/4))+10/21*x/(a^2*(a+b*x^2)^(3/4))+10/21*(1+b*x^2/a)^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(a^(3/2)*(a+b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:942
  public void test0110() {
    check( //
        "Integrate[1/(2-3*x^2)^(1/4), x]", //
        "2*2^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[3/2]],2]/Sqrt[3]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:956
  public void test0111() {
    check( //
        "Integrate[1/(2-3*x^2)^(3/4), x]", //
        "2^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[3/2]],2]/Sqrt[3]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1016
  public void test0112() {
    check( //
        "Integrate[(a-b*x^2)^(1/4)/(c*x)^(19/2), x]", //
        "-2/5*(a-b*x^2)^(5/4)/(a*c*(c*x)^(17/2))+8/15*(a-b*x^2)^(9/4)/(a^2*c*(c*x)^(17/2))-64/195*(a-b*x^2)^(13/4)/(a^3*c*(c*x)^(17/2))+256/3315*(a-b*x^2)^(17/4)/(a^4*c*(c*x)^(17/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1132
  public void test0113() {
    check( //
        "Integrate[x^7*(a+b*x^2)^p, x]", //
        "-1/2*a^3*(a+b*x^2)^(1+p)/(b^4*(1+p))+3/2*a^2*(a+b*x^2)^(2+p)/(b^4*(2+p))-3/2*a*(a+b*x^2)^(3+p)/(b^4*(3+p))+1/2*(a+b*x^2)^(4+p)/(b^4*(4+p))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:47
  public void test0114() {
    check( //
        "Integrate[(c+d*x^2)^4/(a+b*x^2)^3, x]", //
        "d^3*(4*b*c-3*a*d)*x/b^4+1/3*d^4*x^3/b^3+1/4*(b*c-a*d)^4*x/(a*b^4*(a+b*x^2)^2)+1/8*(b*c-a*d)^3*(3*b*c+13*a*d)*x/(a^2*b^4*(a+b*x^2))+1/8*(b*c-a*d)^2*(3*b^2*c^2+10*a*b*c*d+35*a^2*d^2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*b^(9/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:65
  public void test0115() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(c+d*x^2)^2, x]", //
        "1/2*a*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(3/2)*Sqrt[b*c-a*d])+1/2*x*Sqrt[a+b*x^2]/(c*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:79
  public void test0116() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)*(c+d*x^2), x]", //
        "5/192*a*(8*b*c-a*d)*x*(a+b*x^2)^(3/2)/b+1/48*(8*b*c-a*d)*x*(a+b*x^2)^(5/2)/b+1/8*d*x*(a+b*x^2)^(7/2)/b+5/128*a^3*(8*b*c-a*d)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)+5/128*a^2*(8*b*c-a*d)*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:95
  public void test0117() {
    check( //
        "Integrate[1/((a+b*x^2)^(1/2)*(c+d*x^2)), x]", //
        "ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(Sqrt[c]*Sqrt[b*c-a*d])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:229
  public void test0118() {
    check( //
        "Integrate[Sqrt[1-4*x^2]/Sqrt[2+3*x^2], x]", //
        "11/6*EllipticF[ArcSin[2*x],-3/8]/Sqrt[2]-2/3*EllipticE[ArcSin[2*x],-3/8]*Sqrt[2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:302
  public void test0119() {
    check( //
        "Integrate[Sqrt[-a+b*x^2]/Sqrt[-c+d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],b*c/(a*d)]*Sqrt[c]*Sqrt[-a+b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1-b*x^2/a]*Sqrt[-c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:316
  public void test0120() {
    check( //
        "Integrate[Sqrt[-c+d*x^2]/Sqrt[a-b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[-c+d*x^2]/(Sqrt[b]*Sqrt[a-b*x^2]*Sqrt[1-d*x^2/c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:26
  public void test0121() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^2, x]", //
        "-a^2*A/x+a*(2*A*b+a*B)*x+1/3*b*(A*b+2*a*B)*x^3+1/5*b^2*B*x^5");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:40
  public void test0122() {
    check( //
        "Integrate[x^3*(a+b*x^2)^5*(A+B*x^2), x]", //
        "-1/12*a*(A*b-a*B)*(a+b*x^2)^6/b^3+1/14*(A*b-2*a*B)*(a+b*x^2)^7/b^3+1/16*B*(a+b*x^2)^8/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:54
  public void test0123() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^11, x]", //
        "-1/10*a^5*A/x^10-1/8*a^4*(5*A*b+a*B)/x^8-5/6*a^3*b*(2*A*b+a*B)/x^6-5/2*a^2*b^2*(A*b+a*B)/x^4-5/2*a*b^3*(A*b+2*a*B)/x^2+1/2*b^5*B*x^2+b^4*(A*b+5*a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:112
  public void test0124() {
    check( //
        "Integrate[x^8*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "-3*a*(A*b-2*a*B)*x/b^5+1/3*(A*b-3*a*B)*x^3/b^4+1/5*B*x^5/b^3+1/4*a^3*(A*b-a*B)*x/(b^5*(a+b*x^2)^2)-1/8*a^2*(13*A*b-17*a*B)*x/(b^5*(a+b*x^2))+7/8*a^(3/2)*(5*A*b-9*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(11/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:140
  public void test0125() {
    check( //
        "Integrate[x^3*(a*c+b*c*x^2)/(a+b*x^2)^2, x]", //
        "1/2*c*x^2/b-1/2*a*c*Log[a+b*x^2]/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:172
  public void test0126() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x, x]", //
        "a*c*(b*c+a*d)*x^2+1/4*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^4+1/3*b*d*(b*c+a*d)*x^6+1/8*b^2*d^2*x^8+a^2*c^2*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:202
  public void test0127() {
    check( //
        "Integrate[x*(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "1/2*b^2*x^2/d^2-1/2*(b*c-a*d)^2/(d^3*(c+d*x^2))-b*(b*c-a*d)*Log[c+d*x^2]/d^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:236
  public void test0128() {
    check( //
        "Integrate[x^2*(c+d*x^2)^2/(a+b*x^2), x]", //
        "(b*c-a*d)^2*x/b^3+1/3*d*(2*b*c-a*d)*x^3/b^2+1/5*d^2*x^5/b-(b*c-a*d)^2*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:402
  public void test0129() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/Sqrt[x], x]", //
        "2/5*a*(2*A*b+a*B)*x^(5/2)+2/9*b*(A*b+2*a*B)*x^(9/2)+2/13*b^2*B*x^(13/2)+2*a^2*A*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:418
  public void test0130() {
    check( //
        "Integrate[x^(3/2)*(A+B*x^2)/(a+b*x^2), x]", //
        "2/5*B*x^(5/2)/b+a^(1/4)*(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(9/4)*Sqrt[2])-a^(1/4)*(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(b^(9/4)*Sqrt[2])+1/2*a^(1/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(9/4)*Sqrt[2])-1/2*a^(1/4)*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(b^(9/4)*Sqrt[2])+2*(A*b-a*B)*Sqrt[x]/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:450
  public void test0131() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^(5/2), x]", //
        "-2/3*a^2*c/x^(3/2)+2/5*b*(b*c+2*a*d)*x^(5/2)+2/9*b^2*d*x^(9/2)+2*a*(2*b*c+a*d)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:464
  public void test0132() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/Sqrt[x], x]", //
        "2/5*a*c^2*(2*b*c+3*a*d)*x^(5/2)+2/9*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^(9/2)+2/13*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^(13/2)+2/17*b*d^2*(3*b*c+2*a*d)*x^(17/2)+2/21*b^2*d^3*x^(21/2)+2*a^2*c^3*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:584
  public void test0133() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[a+b*x^2], x]", //
        "1/4*B*x*(a+b*x^2)^(3/2)/b+1/8*a*(4*A*b-a*B)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(3/2)+1/8*(4*A*b-a*B)*x*Sqrt[a+b*x^2]/b");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:626
  public void test0134() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)*(A+B*x^2)/x^8, x]", //
        "-1/3*b*B*(a+b*x^2)^(3/2)/x^3-1/5*B*(a+b*x^2)^(5/2)/x^5-1/7*A*(a+b*x^2)^(7/2)/(a*x^7)+b^(5/2)*B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]-b^2*B*Sqrt[a+b*x^2]/x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:656
  public void test0135() {
    check( //
        "Integrate[(A+B*x^2)/(x^4*(a+b*x^2)^(3/2)), x]", //
        "-1/3*A/(a*x^3*Sqrt[a+b*x^2])+1/3*(4*A*b-3*a*B)/(a^2*x*Sqrt[a+b*x^2])+2/3*b*(4*A*b-3*a*B)*x/(a^3*Sqrt[a+b*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:670
  public void test0136() {
    check( //
        "Integrate[(A+B*x^2)/(x^2*(a+b*x^2)^(5/2)), x]", //
        "-A/(a*x*(a+b*x^2)^(3/2))-1/3*(4*A*b-a*B)*x/(a^2*(a+b*x^2)^(3/2))-2/3*(4*A*b-a*B)*x/(a^3*Sqrt[a+b*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:688
  public void test0137() {
    check( //
        "Integrate[(a+b*x^2)^2*Sqrt[c+d*x^2]/x^2, x]", //
        "-a^2*(c+d*x^2)^(3/2)/(c*x)+1/4*b^2*x*(c+d*x^2)^(3/2)/d-1/8*(b^2*c^2-8*a*d*(b*c+a*d))*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(3/2)-1/8*(b^2*c^2-8*a*d*(b*c+a*d))*x*Sqrt[c+d*x^2]/(c*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:702
  public void test0138() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^(3/2)/x^4, x]", //
        "1/12*(3*b^2*c^2+8*a*d*(3*b*c+a*d))*x*(c+d*x^2)^(3/2)/c^2-1/3*a^2*(c+d*x^2)^(5/2)/(c*x^3)-2/3*a*(3*b*c+a*d)*(c+d*x^2)^(5/2)/(c^2*x)+1/8*(3*b^2*c^2+8*a*d*(3*b*c+a*d))*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/Sqrt[d]+1/8*(3*b^2*c^2+8*a*d*(3*b*c+a*d))*x*Sqrt[c+d*x^2]/c");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:768
  public void test0139() {
    check( //
        "Integrate[x^3*Sqrt[c+d*x^2]/(a+b*x^2), x]", //
        "1/3*(c+d*x^2)^(3/2)/(b*d)+a*ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]*Sqrt[b*c-a*d]/b^(5/2)-a*Sqrt[c+d*x^2]/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:798
  public void test0140() {
    check( //
        "Integrate[x/((a+b*x^2)*Sqrt[c+d*x^2]), x]", //
        "-ArcTanh[Sqrt[b]*Sqrt[c+d*x^2]/Sqrt[b*c-a*d]]/(Sqrt[b]*Sqrt[b*c-a*d])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1117
  public void test0141() {
    check( //
        "Integrate[x^3/((a+b*x^2)^(1/2)*Sqrt[c+d*x^2]), x]", //
        "-1/2*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]/(b^(3/2)*d^(3/2))+1/2*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/(b*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1131
  public void test0142() {
    check( //
        "Integrate[x^3/((a+b*x^2)^(5/2)*Sqrt[c+d*x^2]), x]", //
        "1/3*a*Sqrt[c+d*x^2]/(b*(b*c-a*d)*(a+b*x^2)^(3/2))-1/3*(3*b*c-a*d)*Sqrt[c+d*x^2]/(b*(b*c-a*d)^2*Sqrt[a+b*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1145
  public void test0143() {
    check( //
        "Integrate[x^2/(Sqrt[2-3*x^2]*Sqrt[4-x^2]), x]", //
        "-1/3*EllipticE[ArcSin[1/2*x],6]*Sqrt[2]+1/3*EllipticF[ArcSin[1/2*x],6]*Sqrt[2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1303
  public void test0144() {
    check( //
        "Integrate[(e*x)^(9/2)*(c+d*x^2)/(a+b*x^2)^(5/4), x]", //
        "-7/60*a*(10*b*c-11*a*d)*e^3*(e*x)^(3/2)/(b^3*(a+b*x^2)^(1/4))+1/30*(10*b*c-11*a*d)*e*(e*x)^(7/2)/(b^2*(a+b*x^2)^(1/4))+1/5*d*(e*x)^(11/2)/(b*e*(a+b*x^2)^(1/4))-7/20*a^(3/2)*(10*b*c-11*a*d)*e^4*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[e*x]/(b^(7/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:75
  public void test0145() {
    check( //
        "Integrate[(b+2*c*x^2-Sqrt[b^2-4*a*c])/(Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]*Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])]), x]", //
        "x*(b-Sqrt[b^2-4*a*c])*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]/Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])]-EllipticE[ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]],-2*Sqrt[b^2-4*a*c]/(b-Sqrt[b^2-4*a*c])]*(b-Sqrt[b^2-4*a*c])*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[(1+2*c*x^2/(b-Sqrt[b^2-4*a*c]))/(1+2*c*x^2/(b+Sqrt[b^2-4*a*c]))]*Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])])+EllipticF[ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]],-2*Sqrt[b^2-4*a*c]/(b-Sqrt[b^2-4*a*c])]*(b-Sqrt[b^2-4*a*c])*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[(1+2*c*x^2/(b-Sqrt[b^2-4*a*c]))/(1+2*c*x^2/(b+Sqrt[b^2-4*a*c]))]*Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:119
  public void test0146() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x+C*x^2+D*x^3)/x^3, x]", //
        "-1/2*a^2*A/x^2-a^2*B/x+a*(2*b*B+a*D)*x+1/2*b*(A*b+2*a*C)*x^2+1/3*b*(b*B+2*a*D)*x^3+1/4*b^2*C*x^4+1/5*b^2*D*x^5+a*(2*A*b+a*C)*Log[x]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:135
  public void test0147() {
    check( //
        "Integrate[(A+B*x+C*x^2+D*x^3)/(a+b*x^2), x]", //
        "C*x/b+1/2*D*x^2/b+1/2*(b*B-a*D)*Log[a+b*x^2]/b^2+(A*b-a*C)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(3/2)*Sqrt[a])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:149
  public void test0148() {
    check( //
        "Integrate[x^2*(A+B*x+C*x^2+D*x^3)/(a+b*x^2)^3, x]", //
        "-1/4*x^2*(a*(B-a*D/b)-(A*b-a*C)*x)/(a*b*(a+b*x^2)^2)-1/8*x*(A*b+3*a*C-2*(b*B-3*a*D)*x)/(a*b^2*(a+b*x^2))+1/8*(A*b+3*a*C)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(5/2))+1/2*D*Log[a+b*x^2]/b^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:284
  public void test0149() {
    check( //
        "Integrate[(a+b*x^3)^2/x^7, x]", //
        "-1/6*a^2/x^6-2/3*a*b/x^3+b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:298
  public void test0150() {
    check( //
        "Integrate[(a+b*x^3)^3/x^7, x]", //
        "-1/6*a^3/x^6-a^2*b/x^3+1/3*b^3*x^3+3*a*b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:326
  public void test0151() {
    check( //
        "Integrate[(a+b*x^3)^5/x^22, x]", //
        "-1/21*(a+b*x^3)^6/(a*x^21)+1/126*b*(a+b*x^3)^6/(a^2*x^18)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:340
  public void test0152() {
    check( //
        "Integrate[x^20*(a+b*x^3)^8, x]", //
        "1/27*a^6*(a+b*x^3)^9/b^7-1/5*a^5*(a+b*x^3)^10/b^7+5/11*a^4*(a+b*x^3)^11/b^7-5/9*a^3*(a+b*x^3)^12/b^7+5/13*a^2*(a+b*x^3)^13/b^7-1/7*a*(a+b*x^3)^14/b^7+1/45*(a+b*x^3)^15/b^7");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:354
  public void test0153() {
    check( //
        "Integrate[(a+b*x^3)^8/x^22, x]", //
        "-1/21*a^8/x^21-4/9*a^7*b/x^18-28/15*a^6*b^2/x^15-14/3*a^5*b^3/x^12-70/9*a^4*b^4/x^9-28/3*a^3*b^5/x^6-28/3*a^2*b^6/x^3+1/3*b^8*x^3+8*a*b^7*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:384
  public void test0154() {
    check( //
        "Integrate[1/(x^2*(a+b*x^3)), x]", //
        "(-1)/(a*x)+1/3*b^(1/3)*Log[a^(1/3)+b^(1/3)*x]/a^(4/3)-1/6*b^(1/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(4/3)+b^(1/3)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(4/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:398
  public void test0155() {
    check( //
        "Integrate[x^8/(a+b*x^3)^3, x]", //
        "-1/6*a^2/(b^3*(a+b*x^3)^2)+2/3*a/(b^3*(a+b*x^3))+1/3*Log[a+b*x^3]/b^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:412
  public void test0156() {
    check( //
        "Integrate[1/(x*(a-b*x^3)), x]", //
        "Log[x]/a-1/3*Log[a-b*x^3]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:522
  public void test0157() {
    check( //
        "Integrate[x/Sqrt[1+x^3], x]", //
        "2*Sqrt[1+x^3]/(1+x+Sqrt[3])+2*(1+x)*EllipticF[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(3^(1/4)*Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])-3^(1/4)*(1+x)*EllipticE[ArcSin[(1+x-Sqrt[3])/(1+x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1-x+x^2)/(1+x+Sqrt[3])^2]/(Sqrt[1+x^3]*Sqrt[(1+x)/(1+x+Sqrt[3])^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:741
  public void test0158() {
    check( //
        "Integrate[1/(x^3*(a+c*x^4)), x]", //
        "(-1/2)/(a*x^2)-1/2*ArcTan[x^2*Sqrt[c]/Sqrt[a]]*Sqrt[c]/a^(3/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:755
  public void test0159() {
    check( //
        "Integrate[1/(x*(a+c*x^4)^2), x]", //
        "1/4/(a*(a+c*x^4))+Log[x]/a^2-1/4*Log[a+c*x^4]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:769
  public void test0160() {
    check( //
        "Integrate[1/(x*(a+c*x^4)^3), x]", //
        "1/8/(a*(a+c*x^4)^2)+1/4/(a^2*(a+c*x^4))+Log[x]/a^3-1/4*Log[a+c*x^4]/a^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:783
  public void test0161() {
    check( //
        "Integrate[1/(x*(2+3*x^4)), x]", //
        "1/2*Log[x]-1/8*Log[2+3*x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:893
  public void test0162() {
    check( //
        "Integrate[x*(a+c*x^4)^(3/2), x]", //
        "1/8*x^2*(a+c*x^4)^(3/2)+3/16*a^2*ArcTanh[x^2*Sqrt[c]/Sqrt[a+c*x^4]]/Sqrt[c]+3/16*a*x^2*Sqrt[a+c*x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:907
  public void test0163() {
    check( //
        "Integrate[(1-x^4)^(3/2), x]", //
        "1/7*x*(1-x^4)^(3/2)+4/7*EllipticF[ArcSin[x],-1]+2/7*x*Sqrt[1-x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:965
  public void test0164() {
    check( //
        "Integrate[1/(x^7*(a+b*x^4)^(3/2)), x]", //
        "(-1/6)/(a*x^6*Sqrt[a+b*x^4])+2/3*b/(a^2*x^2*Sqrt[a+b*x^4])+4/3*b^2*x^2/(a^3*Sqrt[a+b*x^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1021
  public void test0165() {
    check( //
        "Integrate[1/(1-x^4)^(5/2), x]", //
        "1/6*x/(1-x^4)^(3/2)+5/12*EllipticF[ArcSin[x],-1]+5/12*x/Sqrt[1-x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1183
  public void test0166() {
    check( //
        "Integrate[(a+b*x^4)^(5/4)/x^22, x]", //
        "-1/21*(a+b*x^4)^(9/4)/(a*x^21)+4/119*b*(a+b*x^4)^(9/4)/(a^2*x^17)-32/1547*b^2*(a+b*x^4)^(9/4)/(a^3*x^13)+128/13923*b^3*(a+b*x^4)^(9/4)/(a^4*x^9)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1283
  public void test0167() {
    check( //
        "Integrate[1/(x^10*(a+b*x^4)^(5/4)), x]", //
        "(-1/9)/(a*x^9*(a+b*x^4)^(1/4))+2/9*b/(a^2*x^5*(a+b*x^4)^(1/4))-4/3*b^2/(a^3*x*(a+b*x^4)^(1/4))+8/3*b^(5/2)*(1+a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]/(a^(7/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1345
  public void test0168() {
    check( //
        "Integrate[1/(x^12*(a-b*x^4)^(1/4)), x]", //
        "-1/11*(a-b*x^4)^(3/4)/(a*x^11)-8/77*b*(a-b*x^4)^(3/4)/(a^2*x^7)-32/231*b^2*(a-b*x^4)^(3/4)/(a^3*x^3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1413
  public void test0169() {
    check( //
        "Integrate[1/(x*(a+b*x^5)^2), x]", //
        "1/5/(a*(a+b*x^5))+Log[x]/a^2-1/5*Log[a+b*x^5]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1427
  public void test0170() {
    check( //
        "Integrate[x^9/(1+x^5), x]", //
        "1/5*x^5-1/5*Log[1+x^5]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1486
  public void test0171() {
    check( //
        "Integrate[x/(a+b*x^6), x]", //
        "1/6*Log[a^(1/3)+b^(1/3)*x^2]/(a^(2/3)*b^(1/3))-1/12*Log[a^(2/3)-a^(1/3)*b^(1/3)*x^2+b^(2/3)*x^4]/(a^(2/3)*b^(1/3))-1/2*ArcTan[(a^(1/3)-2*b^(1/3)*x^2)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2655
  public void test0172() {
    check( //
        "Integrate[1/(Sqrt[x]*(1+Sqrt[x])^2), x]", //
        "(-2)/(1+Sqrt[x])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:295
  public void test0173() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/Sqrt[c-d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[d]/Sqrt[c]],-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]*Sqrt[1-d*x^2/c]/(Sqrt[d]*Sqrt[1+b*x^2/a]*Sqrt[c-d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:305
  public void test0174() {
    check( //
        "Integrate[x*(c+d*x^2)^2/(a+b*x^2)^2, x]", //
        "1/2*d^2*x^2/b^2-1/2*(b*c-a*d)^2/(b^3*(a+b*x^2))+d*(b*c-a*d)*Log[a+b*x^2]/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:457
  public void test0175() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^(3/2), x]", //
        "4/3*a*c*(b*c+a*d)*x^(3/2)+2/7*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^(7/2)+4/11*b*d*(b*c+a*d)*x^(11/2)+2/15*b^2*d^2*x^(15/2)-2*a^2*c^2/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1212
  public void test0176() {
    check( //
        "Integrate[x^3/((2-3*x^2)^(1/4)*(4-3*x^2)), x]", //
        "2/27*(2-3*x^2)^(3/4)+2/9*2^(1/4)*ArcTan[(Sqrt[2]-Sqrt[2-3*x^2])/(2^(3/4)*(2-3*x^2)^(1/4))]+2/9*2^(1/4)*ArcTanh[(Sqrt[2]+Sqrt[2-3*x^2])/(2^(3/4)*(2-3*x^2)^(1/4))]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:228
  public void test0177() {
    check( //
        "Integrate[(c+d*x^2+e*x^4+f*x^6)/Sqrt[a+b*x^2], x]", //
        "1/16*(16*b^3*c-8*a*b^2*d+6*a^2*b*e-5*a^3*f)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/b^(7/2)+1/16*(8*b^2*d-6*a*b*e+5*a^2*f)*x*Sqrt[a+b*x^2]/b^3+1/24*(6*b*e-5*a*f)*x^3*Sqrt[a+b*x^2]/b^2+1/6*f*x^5*Sqrt[a+b*x^2]/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:776
  public void test0178() {
    check( //
        "Integrate[1/(a+c*x^4)^3, x]", //
        "1/8*x/(a*(a+c*x^4)^2)+7/32*x/(a^2*(a+c*x^4))-21/64*ArcTan[1-c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(11/4)*c^(1/4)*Sqrt[2])+21/64*ArcTan[1+c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(11/4)*c^(1/4)*Sqrt[2])-21/128*Log[-a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(11/4)*c^(1/4)*Sqrt[2])+21/128*Log[a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(11/4)*c^(1/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:900
  public void test0179() {
    check( //
        "Integrate[(a+c*x^4)^(3/2), x]", //
        "1/7*x*(a+c*x^4)^(3/2)+2/7*a*x*Sqrt[a+c*x^4]+2/7*a^(7/4)*EllipticF[2*ArcTan[c^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[c])*Sqrt[(a+c*x^4)/(Sqrt[a]+x^2*Sqrt[c])^2]/(c^(1/4)*Sqrt[a+c*x^4])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1366
  public void test0180() {
    check( //
        "Integrate[x/(a-b*x^4)^(3/4), x]", //
        "(1-b*x^4/a)^(3/4)*EllipticF[1/2*ArcSin[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a-b*x^4)^(3/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1870
  public void test0181() {
    check( //
        "Integrate[(a+b/x)^8*x^3, x]", //
        "-1/4*b^8/x^4-8/3*a*b^7/x^3-14*a^2*b^6/x^2-56*a^3*b^5/x+56*a^5*b^3*x+14*a^6*b^2*x^2+8/3*a^7*b*x^3+1/4*a^8*x^4+70*a^4*b^4*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2046
  public void test0182() {
    check( //
        "Integrate[(a+b/x)^(1/2)*x^(5/2), x]", //
        "16/105*b^2*(a+b/x)^(3/2)*x^(3/2)/a^3-8/35*b*(a+b/x)^(3/2)*x^(5/2)/a^2+2/7*(a+b/x)^(3/2)*x^(7/2)/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2132
  public void test0183() {
    check( //
        "Integrate[(a+b/x^2)^3*x^5, x]", //
        "3/2*a*b^2*x^2+3/4*a^2*b*x^4+1/6*a^3*x^6+b^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2210
  public void test0184() {
    check( //
        "Integrate[(a+b/x^2)^(3/2)/x, x]", //
        "-1/3*(a+b/x^2)^(3/2)+a^(3/2)*ArcTanh[Sqrt[a+b/x^2]/Sqrt[a]]-a*Sqrt[a+b/x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2402
  public void test0185() {
    check( //
        "Integrate[Sqrt[a+b/x^4]/x^3, x]", //
        "-1/4*a*ArcTanh[Sqrt[b]/(x^2*Sqrt[a+b/x^4])]/Sqrt[b]-1/4*Sqrt[a+b/x^4]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2661
  public void test0186() {
    check( //
        "Integrate[Sqrt[x]*Sqrt[1+Sqrt[x]], x]", //
        "4/3*(1+Sqrt[x])^(3/2)-8/5*(1+Sqrt[x])^(5/2)+4/7*(1+Sqrt[x])^(7/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3122
  public void test0187() {
    check( //
        "Integrate[x^(-1+4*n)*(a+b*x^n)^8, x]", //
        "-1/9*a^3*(a+b*x^n)^9/(b^4*n)+3/10*a^2*(a+b*x^n)^10/(b^4*n)-3/11*a*(a+b*x^n)^11/(b^4*n)+1/12*(a+b*x^n)^12/(b^4*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3180
  public void test0188() {
    check( //
        "Integrate[x^(-1-3*n)/(2+b*x^n), x]", //
        "(-1/6)/(n*x^(3*n))+1/8*b/(n*x^(2*n))-1/8*b^2/(n*x^n)-1/16*b^3*Log[x]+1/16*b^3*Log[2+b*x^n]/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3291
  public void test0189() {
    check( //
        "Integrate[x^(-1+3*n)*(a+b*x^n)^p, x]", //
        "a^2*(a+b*x^n)^(1+p)/(b^3*n*(1+p))-2*a*(a+b*x^n)^(2+p)/(b^3*n*(2+p))+(a+b*x^n)^(3+p)/(b^3*n*(3+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3371
  public void test0190() {
    check( //
        "Integrate[(c*x)^(-1+1/2*n)/Sqrt[a+b*x^n], x]", //
        "2*(c*x)^(1/2*n)*ArcTanh[x^(1/2*n)*Sqrt[b]/Sqrt[a+b*x^n]]/(c*n*x^(1/2*n)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3450
  public void test0191() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*(c+d*x)^2), x]", //
        "1/4*a*(c+d*x)^4/d+1/6*b*(c+d*x)^6/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3732
  public void test0192() {
    check( //
        "Integrate[1/(a+b*(c*x^n)^(2/n)), x]", //
        "x*ArcTan[(c*x^n)^(1/n)*Sqrt[b]/Sqrt[a]]/((c*x^n)^(1/n)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:16
  public void test0193() {
    check( //
        "Integrate[(a+b*x^3)/(c+d*x^3), x]", //
        "b*x/d-1/3*(b*c-a*d)*Log[c^(1/3)+d^(1/3)*x]/(c^(2/3)*d^(4/3))+1/6*(b*c-a*d)*Log[c^(2/3)-c^(1/3)*d^(1/3)*x+d^(2/3)*x^2]/(c^(2/3)*d^(4/3))+(b*c-a*d)*ArcTan[(c^(1/3)-2*d^(1/3)*x)/(c^(1/3)*Sqrt[3])]/(c^(2/3)*d^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:48
  public void test0194() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(4/3), x]", //
        "2*x/(a+b*x^3)^(1/3)+1/2*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/b^(1/3)-ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:28
  public void test0195() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^4, x]", //
        "-1/3*a^2*A/x^3+1/3*b*(A*b+2*a*B)*x^3+1/6*b^2*B*x^6+a*(2*A*b+a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:48
  public void test0196() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^5, x]", //
        "-1/4*a^5*A/x^4-a^4*(5*A*b+a*B)/x+5/2*a^3*b*(2*A*b+a*B)*x^2+2*a^2*b^2*(A*b+a*B)*x^5+5/8*a*b^3*(A*b+2*a*B)*x^8+1/11*b^4*(A*b+5*a*B)*x^11+1/14*b^5*B*x^14");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:74
  public void test0197() {
    check( //
        "Integrate[x*(A+B*x^3)/(a+b*x^3), x]", //
        "1/2*B*x^2/b-1/3*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/(a^(1/3)*b^(5/3))+1/6*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(1/3)*b^(5/3))-(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(1/3)*b^(5/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:180
  public void test0198() {
    check( //
        "Integrate[(a+b*x^3)^3*(A+B*x^3)/x^(5/2), x]", //
        "-2/3*a^3*A/x^(3/2)+2/3*a^2*(3*A*b+a*B)*x^(3/2)+2/3*a*b*(A*b+a*B)*x^(9/2)+2/15*b^2*(A*b+3*a*B)*x^(15/2)+2/21*b^3*B*x^(21/2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:340
  public void test0199() {
    check( //
        "Integrate[x^11*(c+d*x^3)^(3/2)/(8*c-d*x^3), x]", //
        "-1024/9*c^3*(c+d*x^3)^(3/2)/d^4-38/5*c^2*(c+d*x^3)^(5/2)/d^4-4/7*c*(c+d*x^3)^(7/2)/d^4-2/27*(c+d*x^3)^(9/2)/d^4+9216*c^(9/2)*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]/d^4-3072*c^4*Sqrt[c+d*x^3]/d^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:364
  public void test0200() {
    check( //
        "Integrate[x/((8*c-d*x^3)*Sqrt[c+d*x^3]), x]", //
        "1/18*ArcTanh[1/3*(c^(1/3)+d^(1/3)*x)^2/(c^(1/6)*Sqrt[c+d*x^3])]/(c^(5/6)*d^(2/3))-1/18*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]/(c^(5/6)*d^(2/3))-1/6*ArcTan[c^(1/6)*(c^(1/3)+d^(1/3)*x)*Sqrt[3]/Sqrt[c+d*x^3]]/(c^(5/6)*d^(2/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:435
  public void test0201() {
    check( //
        "Integrate[x^2/((a+b*x^3)*Sqrt[c+d*x^3]), x]", //
        "-2/3*ArcTanh[Sqrt[b]*Sqrt[c+d*x^3]/Sqrt[b*c-a*d]]/(Sqrt[b]*Sqrt[b*c-a*d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:581
  public void test0202() {
    check( //
        "Integrate[x^5/(Sqrt[a+b*x^3]*Sqrt[c+d*x^3]), x]", //
        "-1/3*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x^3]/(Sqrt[b]*Sqrt[c+d*x^3])]/(b^(3/2)*d^(3/2))+1/3*Sqrt[a+b*x^3]*Sqrt[c+d*x^3]/(b*d)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:739
  public void test0203() {
    check( //
        "Integrate[1/((1-x^3)^(4/3)*(1+x^3)), x]", //
        "1/2*x/(1-x^3)^(1/3)-1/12*Log[1+x^3]/2^(1/3)+1/4*Log[-2^(1/3)*x-(1-x^3)^(1/3)]/2^(1/3)-1/2*ArcTan[(1-2*2^(1/3)*x/(1-x^3)^(1/3))/Sqrt[3]]/(2^(1/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:900
  public void test0204() {
    check( //
        "Integrate[x^7*Sqrt[c+d*x^4]/(a+b*x^4), x]", //
        "1/6*(c+d*x^4)^(3/2)/(b*d)+1/2*a*ArcTanh[Sqrt[b]*Sqrt[c+d*x^4]/Sqrt[b*c-a*d]]*Sqrt[b*c-a*d]/b^(5/2)-1/2*a*Sqrt[c+d*x^4]/b^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:949
  public void test0205() {
    check( //
        "Integrate[x^7/((a+b*x^4)^2*Sqrt[c+d*x^4]), x]", //
        "-1/4*(2*b*c-a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^4]/Sqrt[b*c-a*d]]/(b^(3/2)*(b*c-a*d)^(3/2))+1/4*a*Sqrt[c+d*x^4]/(b*(b*c-a*d)*(a+b*x^4))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1014
  public void test0206() {
    check( //
        "Integrate[x^11/((a+b*x^6)^2*Sqrt[c+d*x^6]), x]", //
        "-1/6*(2*b*c-a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^6]/Sqrt[b*c-a*d]]/(b^(3/2)*(b*c-a*d)^(3/2))+1/6*a*Sqrt[c+d*x^6]/(b*(b*c-a*d)*(a+b*x^6))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1064
  public void test0207() {
    check( //
        "Integrate[x^15/((a+b*x^8)^2*Sqrt[c+d*x^8]), x]", //
        "-1/8*(2*b*c-a*d)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^8]/Sqrt[b*c-a*d]]/(b^(3/2)*(b*c-a*d)^(3/2))+1/8*a*Sqrt[c+d*x^8]/(b*(b*c-a*d)*(a+b*x^8))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1102
  public void test0208() {
    check( //
        "Integrate[(a+b/x^2)*x^6*Sqrt[c+d/x^2], x]", //
        "-2/105*d*(7*b*c-4*a*d)*(c+d/x^2)^(3/2)*x^3/c^3+1/35*(7*b*c-4*a*d)*(c+d/x^2)^(3/2)*x^5/c^2+1/7*a*(c+d/x^2)^(3/2)*x^7/c");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1208
  public void test0209() {
    check( //
        "Integrate[(1+x^6)/(x*(1-x^6)), x]", //
        "Log[x]-1/3*Log[1-x^6]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1274
  public void test0210() {
    check( //
        "Integrate[x^(-1+3*n)/((a+b*x^n)^(5/2)*Sqrt[c+d*x^n]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x^n]/(Sqrt[b]*Sqrt[c+d*x^n])]/(b^(5/2)*n*Sqrt[d])-2/3*a^2*Sqrt[c+d*x^n]/(b^2*(b*c-a*d)*n*(a+b*x^n)^(3/2))+4/3*a*(3*b*c-2*a*d)*Sqrt[c+d*x^n]/(b^2*(b*c-a*d)^2*n*Sqrt[a+b*x^n])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:88
  public void test0211() {
    check( //
        "Integrate[(a*c+a*d*x+b*c*x^3+b*d*x^4)/(a+b*x^3)^(9/2), x]", //
        "2/15*x*(c+d*x)/(a*(a+b*x^3)^(5/2))+2/135*x*(13*c+11*d*x)/(a^2*(a+b*x^3)^(3/2))+2/405*x*(91*c+55*d*x)/(a^3*Sqrt[a+b*x^3])-22/81*d*Sqrt[a+b*x^3]/(a^3*b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+11/27*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(3/4)*a^(8/3)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+2/405*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(91*b^(1/3)*c+55*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a^3*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:187
  public void test0212() {
    check( //
        "Integrate[(c+d*x+e*x^2)/(a+b*x^4), x]", //
        "1/2*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(Sqrt[a]*Sqrt[b])-1/4*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-e*Sqrt[a]+c*Sqrt[b])/(a^(3/4)*b^(3/4)*Sqrt[2])+1/4*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]*(-e*Sqrt[a]+c*Sqrt[b])/(a^(3/4)*b^(3/4)*Sqrt[2])-1/2*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]*(e*Sqrt[a]+c*Sqrt[b])/(a^(3/4)*b^(3/4)*Sqrt[2])+1/2*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]*(e*Sqrt[a]+c*Sqrt[b])/(a^(3/4)*b^(3/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:210
  public void test0213() {
    check( //
        "Integrate[(a+c*x^2+d*x^3)*(e+f*x^4)^2, x]", //
        "a*e^2*x+1/3*c*e^2*x^3+2/5*a*e*f*x^5+2/7*c*e*f*x^7+1/9*a*f^2*x^9+1/11*c*f^2*x^11+1/12*d*(e+f*x^4)^3/f");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:350
  public void test0214() {
    check( //
        "Integrate[(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3), x]", //
        "(b^2*d-a*b*e+a^2*f)*x/b^3+1/4*(b*e-a*f)*x^4/b^2+1/7*f*x^7/b+1/3*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(10/3))-1/6*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(10/3))-(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(10/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:453
  public void test0215() {
    check( //
        "Integrate[x*(c+d*x+e*x^2)*(a+b*x^3)^4, x]", //
        "1/2*a^4*c*x^2+1/4*a^4*e*x^4+4/5*a^3*b*c*x^5+4/7*a^3*b*e*x^7+3/4*a^2*b^2*c*x^8+3/5*a^2*b^2*e*x^10+4/11*a*b^3*c*x^11+4/13*a*b^3*e*x^13+1/14*b^4*c*x^14+1/16*b^4*e*x^16+1/15*d*(a+b*x^3)^5/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:492
  public void test0216() {
    check( //
        "Integrate[x*(-2*(a/b)^(1/3)*C+C*x)/(a+b*x^3), x]", //
        "C*Log[(a/b)^(1/3)+x]/b+2*C*ArcTan[(1-2*x/(a/b)^(1/3))/Sqrt[3]]/(b*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:515
  public void test0217() {
    check( //
        "Integrate[(a+b*x^3)^2*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/x, x]", //
        "a^2*d*x+1/2*a^2*e*x^2+2/3*a*b*c*x^3+1/4*a*(2*b*d+a*g)*x^4+1/5*a*(2*b*e+a*h)*x^5+1/6*b^2*c*x^6+1/7*b*(b*d+2*a*g)*x^7+1/8*b*(b*e+2*a*h)*x^8+1/10*b^2*g*x^10+1/11*b^2*h*x^11+1/9*f*(a+b*x^3)^3/b+a^2*c*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:536
  public void test0218() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3), x]", //
        "f*x/b+1/2*g*x^2/b+1/3*h*x^3/b+1/3*(b^(1/3)*(b*c-a*f)-a^(1/3)*(b*d-a*g))*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(5/3))-1/6*(b^(1/3)*(b*c-a*f)-a^(1/3)*(b*d-a*g))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(5/3))+1/3*(b*e-a*h)*Log[a+b*x^3]/b^2-(b^(4/3)*c+a^(1/3)*b*d-a*b^(1/3)*f-a^(4/3)*g)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(5/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:658
  public void test0219() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*Sqrt[a+b*x^4]/x^4, x]", //
        "-1/2*f*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]*Sqrt[a]+1/2*d*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]*Sqrt[b]-2*e*Sqrt[a+b*x^4]/x-1/3*(c-3*e*x^2)*Sqrt[a+b*x^4]/x^3-1/2*(d-f*x^2)*Sqrt[a+b*x^4]/x^2+2*e*x*Sqrt[b]*Sqrt[a+b*x^4]/(Sqrt[a]+x^2*Sqrt[b])-2*a^(1/4)*b^(1/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]+1/3*b^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(3*e*Sqrt[a]+c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(1/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:677
  public void test0220() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^8, x]", //
        "-1/420*(60*c/x^7+70*d/x^6+84*e/x^5+105*f/x^4)*(a+b*x^4)^(3/2)+1/2*b^(3/2)*d*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]-3/4*b*f*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]*Sqrt[a]-12/5*b*e*Sqrt[a+b*x^4]/x-2/35*b*(5*c-21*e*x^2)*Sqrt[a+b*x^4]/x^3-1/4*b*(2*d-3*f*x^2)*Sqrt[a+b*x^4]/x^2+12/5*b^(3/2)*e*x*Sqrt[a+b*x^4]/(Sqrt[a]+x^2*Sqrt[b])-12/5*a^(1/4)*b^(5/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]+2/35*b^(5/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(21*e*Sqrt[a]+5*c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(1/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:26
  public void test0221() {
    check( //
        "Integrate[x^3/(a*x+b*x^3), x]", //
        "x/b-ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(3/2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:116
  public void test0222() {
    check( //
        "Integrate[x^(3/2)/(a*x+b*x^3)^(9/2), x]", //
        "1/7*x^(3/2)/(a*(a*x+b*x^3)^(7/2))+9/2*b*ArcTanh[Sqrt[a]*Sqrt[x]/Sqrt[a*x+b*x^3]]/a^(11/2)+3/5/(a^3*(a*x+b*x^3)^(3/2)*Sqrt[x])+9/35*Sqrt[x]/(a^2*(a*x+b*x^3)^(5/2))+3/(a^4*x^(3/2)*Sqrt[a*x+b*x^3])-9/2*Sqrt[a*x+b*x^3]/(a^5*x^(5/2))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:155
  public void test0223() {
    check( //
        "Integrate[1/(x^2*(b*x^(1/2)+a*x)^(1/2)), x]", //
        "-4/5*Sqrt[a*x+b*Sqrt[x]]/(b*x^(3/2))+16/15*a*Sqrt[a*x+b*Sqrt[x]]/(b^2*x)-32/15*a^2*Sqrt[a*x+b*Sqrt[x]]/(b^3*Sqrt[x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:247
  public void test0224() {
    check( //
        "Integrate[(b*x^(2/3)+a*x)^(3/2)/x^3, x]", //
        "-(b*x^(2/3)+a*x)^(3/2)/x^2+3/8*a^3*ArcTanh[x^(1/3)*Sqrt[b]/Sqrt[b*x^(2/3)+a*x]]/b^(3/2)-3/4*a*Sqrt[b*x^(2/3)+a*x]/x-3/8*a^2*Sqrt[b*x^(2/3)+a*x]/(b*x^(2/3))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:268
  public void test0225() {
    check( //
        "Integrate[1/(x^2*(b*x^(2/3)+a*x)^(3/2)), x]", //
        "-9009/512*a^6*ArcTanh[x^(1/3)*Sqrt[b]/Sqrt[b*x^(2/3)+a*x]]/b^(15/2)+6/(b*x^(5/3)*Sqrt[b*x^(2/3)+a*x])-13/2*Sqrt[b*x^(2/3)+a*x]/(b^2*x^(7/3))+143/20*a*Sqrt[b*x^(2/3)+a*x]/(b^3*x^2)-1287/160*a^2*Sqrt[b*x^(2/3)+a*x]/(b^4*x^(5/3))+3003/320*a^3*Sqrt[b*x^(2/3)+a*x]/(b^5*x^(4/3))-3003/256*a^4*Sqrt[b*x^(2/3)+a*x]/(b^6*x)+9009/512*a^5*Sqrt[b*x^(2/3)+a*x]/(b^7*x^(2/3))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:297
  public void test0226() {
    check( //
        "Integrate[1/(a*x^2+b*x^3), x]", //
        "(-1)/(a*x)-b*Log[x]/a^2+b*Log[a+b*x]/a^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:319
  public void test0227() {
    check( //
        "Integrate[Sqrt[a*x^2+b*x^3]/x^3, x]", //
        "-b*ArcTanh[x*Sqrt[a]/Sqrt[a*x^2+b*x^3]]/Sqrt[a]-Sqrt[a*x^2+b*x^3]/x^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:367
  public void test0228() {
    check( //
        "Integrate[1/(x^(5/2)*Sqrt[a*x^2+b*x^3]), x]", //
        "-2/5*Sqrt[a*x^2+b*x^3]/(a*x^(7/2))+8/15*b*Sqrt[a*x^2+b*x^3]/(a^2*x^(5/2))-16/15*b^2*Sqrt[a*x^2+b*x^3]/(a^3*x^(3/2))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:509
  public void test0229() {
    check( //
        "Integrate[Sqrt[a/x^2+b*x^n], x]", //
        "-2*ArcTanh[Sqrt[a]/(x*Sqrt[a/x^2+b*x^n])]*Sqrt[a]/(2+n)+2*x*Sqrt[a/x^2+b*x^n]/(2+n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:530
  public void test0230() {
    check( //
        "Integrate[(c*x)^(-1+1/2*j)/Sqrt[a*x^j+b*x^n], x]", //
        "2*(c*x)^(1/2*j)*ArcTanh[x^(1/2*j)*Sqrt[a]/Sqrt[a*x^j+b*x^n]]/(c*(j-n)*x^(1/2*j)*Sqrt[a])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:548
  public void test0231() {
    check( //
        "Integrate[1/Sqrt[(a+b*x^4)/x^2], x]", //
        "1/2*ArcTanh[x*Sqrt[b]/Sqrt[a/x^2+b*x^2]]/Sqrt[b]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:18
  public void test0232() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)/x^4, x]", //
        "-A*b/x+(b*B+A*c)*x+1/3*B*c*x^3");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:36
  public void test0233() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^2, x]", //
        "1/5*A*b^3*x^5+1/7*b^2*(b*B+3*A*c)*x^7+1/3*b*c*(b*B+A*c)*x^9+1/11*c^2*(3*b*B+A*c)*x^11+1/13*B*c^3*x^13");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:117
  public void test0234() {
    check( //
        "Integrate[x^4*(A+B*x^2)*Sqrt[b*x^2+c*x^4], x]", //
        "-8/315*b^2*(2*b*B-3*A*c)*(b*x^2+c*x^4)^(3/2)/(c^4*x^3)+4/105*b*(2*b*B-3*A*c)*(b*x^2+c*x^4)^(3/2)/(c^3*x)-1/21*(2*b*B-3*A*c)*x*(b*x^2+c*x^4)^(3/2)/c^2+1/9*B*x^3*(b*x^2+c*x^4)^(3/2)/c");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:136
  public void test0235() {
    check( //
        "Integrate[x^4*(A+B*x^2)*(b*x^2+c*x^4)^(3/2), x]", //
        "16/15015*b^3*(8*b*B-13*A*c)*(b*x^2+c*x^4)^(5/2)/(c^5*x^5)-8/3003*b^2*(8*b*B-13*A*c)*(b*x^2+c*x^4)^(5/2)/(c^4*x^3)+2/429*b*(8*b*B-13*A*c)*(b*x^2+c*x^4)^(5/2)/(c^3*x)-1/143*(8*b*B-13*A*c)*x*(b*x^2+c*x^4)^(5/2)/c^2+1/13*B*x^3*(b*x^2+c*x^4)^(5/2)/c");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:156
  public void test0236() {
    check( //
        "Integrate[(A+B*x^2)/(x^7*Sqrt[b*x^2+c*x^4]), x]", //
        "-1/7*A*Sqrt[b*x^2+c*x^4]/(b*x^8)-1/35*(7*b*B-6*A*c)*Sqrt[b*x^2+c*x^4]/(b^2*x^6)+4/105*c*(7*b*B-6*A*c)*Sqrt[b*x^2+c*x^4]/(b^3*x^4)-8/105*c^2*(7*b*B-6*A*c)*Sqrt[b*x^2+c*x^4]/(b^4*x^2)");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:21
  public void test0237() {
    check( //
        "Integrate[Sqrt[6*x-x^2], x]", //
        "-9/2*ArcSin[1-1/3*x]-1/2*(3-x)*Sqrt[6*x-x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:64
  public void test0238() {
    check( //
        "Integrate[1/(b*x+c*x^2)^(13/4), x]", //
        "-4/9*(b+2*c*x)/(b^2*(b*x+c*x^2)^(9/4))+112/45*c*(b+2*c*x)/(b^4*(b*x+c*x^2)^(5/4))-448/15*c^2*(b+2*c*x)/(b^6*(b*x+c*x^2)^(1/4))+448/15*c^2*(-c*(b*x+c*x^2)/b^2)^(1/4)*EllipticE[1/2*ArcSin[1+2*c*x/b],2]*Sqrt[2]/(b^5*(b*x+c*x^2)^(1/4))");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:94
  public void test0239() {
    check( //
        "Integrate[1/(4+12*x+9*x^2)^(3/2), x]", //
        "(-1/6)/((2+3*x)*Sqrt[4+12*x+9*x^2])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:121
  public void test0240() {
    check( //
        "Integrate[1/(1+Pi*x+3*x^2), x]", //
        "2*ArcTan[(Pi+6*x)/Sqrt[12-Pi^2]]/Sqrt[12-Pi^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:146
  public void test0241() {
    check( //
        "Integrate[Sqrt[2+4*x-3*x^2], x]", //
        "-5/3*ArcSin[(2-3*x)/Sqrt[10]]/Sqrt[3]-1/6*(2-3*x)*Sqrt[2+4*x-3*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:167
  public void test0242() {
    check( //
        "Integrate[1/Sqrt[1/4*(-b^2+4*c)/c+b*x-c*x^2], x]", //
        "-ArcSin[1/2*(b-2*c*x)/Sqrt[c]]/Sqrt[c]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:23
  public void test0243() {
    check( //
        "Integrate[x^2*(b*x+c*x^2)^(3/2), x]", //
        "7/192*b^2*(b+2*c*x)*(b*x+c*x^2)^(3/2)/c^3-7/60*b*(b*x+c*x^2)^(5/2)/c^2+1/6*x*(b*x+c*x^2)^(5/2)/c+7/512*b^6*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/c^(9/2)-7/512*b^4*(b+2*c*x)*Sqrt[b*x+c*x^2]/c^4");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:63
  public void test0244() {
    check( //
        "Integrate[1/(x^4*(b*x+c*x^2)^(1/2)), x]", //
        "-2/7*Sqrt[b*x+c*x^2]/(b*x^4)+12/35*c*Sqrt[b*x+c*x^2]/(b^2*x^3)-16/35*c^2*Sqrt[b*x+c*x^2]/(b^3*x^2)+32/35*c^3*Sqrt[b*x+c*x^2]/(b^4*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:81
  public void test0245() {
    check( //
        "Integrate[1/(x^2*(a*x+b*x^2)^(5/2)), x]", //
        "(-2/7)/(a*x^2*(a*x+b*x^2)^(3/2))+4/7*b/(a^2*x*(a*x+b*x^2)^(3/2))-32/21*b^2*(a+2*b*x)/(a^4*(a*x+b*x^2)^(3/2))+256/21*b^3*(a+2*b*x)/(a^6*Sqrt[a*x+b*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:177
  public void test0246() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x+b^2*x^2]/x^5, x]", //
        "-1/4*a*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^4*(a+b*x))-1/3*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^3*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:197
  public void test0247() {
    check( //
        "Integrate[x^2*(a^2+2*a*b*x+b^2*x^2)^(5/2), x]", //
        "1/6*a^2*(a+b*x)^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^3-2/7*a*(a+b*x)^6*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^3+1/8*(a+b*x)^7*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:352
  public void test0248() {
    check( //
        "Integrate[(d+e*x)*(b*x+c*x^2)^(3/2), x]", //
        "1/16*(2*c*d-b*e)*(b+2*c*x)*(b*x+c*x^2)^(3/2)/c^2+1/5*e*(b*x+c*x^2)^(5/2)/c+3/128*b^4*(2*c*d-b*e)*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/c^(7/2)-3/128*b^2*(2*c*d-b*e)*(b+2*c*x)*Sqrt[b*x+c*x^2]/c^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:480
  public void test0249() {
    check( //
        "Integrate[Sqrt[d+e*x]/Sqrt[b*x+c*x^2], x]", //
        "2*EllipticE[ArcSin[Sqrt[c]*Sqrt[x]/Sqrt[-b]],b*e/(c*d)]*Sqrt[-b]*Sqrt[x]*Sqrt[1+c*x/b]*Sqrt[d+e*x]/(Sqrt[c]*Sqrt[1+e*x/d]*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:560
  public void test0250() {
    check( //
        "Integrate[(d+e*x)^3*(a+c*x^2)^3, x]", //
        "a^3*d^3*x+a^2*d*(c*d^2+a*e^2)*x^3+1/4*a^3*e^3*x^4+3/5*a*c*d*(c*d^2+3*a*e^2)*x^5+1/2*a^2*c*e^3*x^6+1/7*c^2*d*(c*d^2+9*a*e^2)*x^7+3/8*a*c^2*e^3*x^8+1/3*c^3*d*e^2*x^9+1/10*c^3*e^3*x^10+3/8*d^2*e*(a+c*x^2)^4/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:578
  public void test0251() {
    check( //
        "Integrate[(d+e*x)^2*(a+c*x^2)^4, x]", //
        "a^4*d^2*x+1/3*a^3*(4*c*d^2+a*e^2)*x^3+2/5*a^2*c*(3*c*d^2+2*a*e^2)*x^5+2/7*a*c^2*(2*c*d^2+3*a*e^2)*x^7+1/9*c^3*(c*d^2+4*a*e^2)*x^9+1/11*c^4*e^2*x^11+1/5*d*e*(a+c*x^2)^5/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:929
  public void test0252() {
    check( //
        "Integrate[(d+e*x)^3*(d^2-e^2*x^2)^(7/2), x]", //
        "91/384*d^7*x*(d^2-e^2*x^2)^(3/2)+91/480*d^5*x*(d^2-e^2*x^2)^(5/2)+13/80*d^3*x*(d^2-e^2*x^2)^(7/2)-13/90*d^2*(d^2-e^2*x^2)^(9/2)/e-13/110*d*(d+e*x)*(d^2-e^2*x^2)^(9/2)/e-1/11*(d+e*x)^2*(d^2-e^2*x^2)^(9/2)/e+91/256*d^11*ArcTan[e*x/Sqrt[d^2-e^2*x^2]]/e+91/256*d^9*x*Sqrt[d^2-e^2*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:988
  public void test0253() {
    check( //
        "Integrate[1/((d+e*x)^3*(d^2-e^2*x^2)^(7/2)), x]", //
        "16/165*x/(d^5*(d^2-e^2*x^2)^(5/2))+(-1/11)/(d*e*(d+e*x)^3*(d^2-e^2*x^2)^(5/2))+(-8/99)/(d^2*e*(d+e*x)^2*(d^2-e^2*x^2)^(5/2))+(-8/99)/(d^3*e*(d+e*x)*(d^2-e^2*x^2)^(5/2))+64/495*x/(d^7*(d^2-e^2*x^2)^(3/2))+128/495*x/(d^9*Sqrt[d^2-e^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1011
  public void test0254() {
    check( //
        "Integrate[(c*d^2-c*e^2*x^2)^(3/2)/(d+e*x)^(9/2), x]", //
        "-1/2*(c*d^2-c*e^2*x^2)^(3/2)/(e*(d+e*x)^(7/2))-3/4*c^(3/2)*ArcTanh[Sqrt[c*d^2-c*e^2*x^2]/(Sqrt[2]*Sqrt[c]*Sqrt[d]*Sqrt[d+e*x])]/(e*Sqrt[2]*Sqrt[d])+3/4*c*Sqrt[c*d^2-c*e^2*x^2]/(e*(d+e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1161
  public void test0255() {
    check( //
        "Integrate[(d+e*x)^5/(c*d^2+2*c*d*e*x+c*e^2*x^2), x]", //
        "1/4*(d+e*x)^4/(c*e)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1180
  public void test0256() {
    check( //
        "Integrate[(d+e*x)^9/(c*d^2+2*c*d*e*x+c*e^2*x^2)^3, x]", //
        "1/4*(d+e*x)^4/(c^3*e)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1242
  public void test0257() {
    check( //
        "Integrate[(d+e*x)^2/(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x]", //
        "(d+e*x)*Log[d+e*x]/(c*e*Sqrt[c*d^2+2*c*d*e*x+c*e^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1263
  public void test0258() {
    check( //
        "Integrate[(d+e*x)^m/(c*d^2+2*c*d*e*x+c*e^2*x^2)^2, x]", //
        "-(d+e*x)^(-3+m)/(c^2*e*(3-m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1347
  public void test0259() {
    check( //
        "Integrate[(b*d+2*c*d*x)^8/(a+b*x+c*x^2)^2, x]", //
        "28*c*(b^2-4*a*c)^2*d^8*(b+2*c*x)+28/3*c*(b^2-4*a*c)*d^8*(b+2*c*x)^3+28/5*c*d^8*(b+2*c*x)^5-d^8*(b+2*c*x)^7/(a+b*x+c*x^2)-28*c*(b^2-4*a*c)^(5/2)*d^8*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1366
  public void test0260() {
    check( //
        "Integrate[(b*d+2*c*d*x)^2/(a+b*x+c*x^2)^3, x]", //
        "-1/2*d^2*(b+2*c*x)/(a+b*x+c*x^2)^2-c*d^2*(b+2*c*x)/((b^2-4*a*c)*(a+b*x+c*x^2))+4*c^2*d^2*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(3/2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1407
  public void test0261() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x), x]", //
        "-1/24*(b^2-4*a*c)*(a+b*x+c*x^2)^(3/2)/(c^2*d)+1/10*(a+b*x+c*x^2)^(5/2)/(c*d)-1/64*(b^2-4*a*c)^(5/2)*ArcTan[2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c]]/(c^(7/2)*d)+1/32*(b^2-4*a*c)^2*Sqrt[a+b*x+c*x^2]/(c^3*d)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1428
  public void test0262() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)^4*(a+b*x+c*x^2)^(1/2)), x]", //
        "2/3*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)*d^4*(b+2*c*x)^3)+4/3*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)^2*d^4*(b+2*c*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1531
  public void test0263() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(1/2)*(a+b*x+c*x^2)^(1/2), x]", //
        "1/5*(b*d+2*c*d*x)^(3/2)*Sqrt[a+b*x+c*x^2]/(c*d)-1/5*(b^2-4*a*c)^(7/4)*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^2*Sqrt[a+b*x+c*x^2])+1/5*(b^2-4*a*c)^(7/4)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^2*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1550
  public void test0264() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x)^(9/2), x]", //
        "-5/42*(a+b*x+c*x^2)^(3/2)/(c^2*d^3*(b*d+2*c*d*x)^(3/2))-1/7*(a+b*x+c*x^2)^(5/2)/(c*d*(b*d+2*c*d*x)^(7/2))+5/84*Sqrt[b*d+2*c*d*x]*Sqrt[a+b*x+c*x^2]/(c^3*d^5)-5/84*(b^2-4*a*c)^(5/4)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^4*d^(9/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1571
  public void test0265() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)^(7/2)*(a+b*x+c*x^2)^(1/2)), x]", //
        "4/5*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)*d*(b*d+2*c*d*x)^(5/2))+12/5*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)^2*d^3*Sqrt[b*d+2*c*d*x])-6/5*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c*(b^2-4*a*c)^(5/4)*d^(7/2)*Sqrt[a+b*x+c*x^2])+6/5*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c*(b^2-4*a*c)^(5/4)*d^(7/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1589
  public void test0266() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(11/2)/(a+b*x+c*x^2)^(5/2), x]", //
        "-2/3*d*(b*d+2*c*d*x)^(9/2)/(a+b*x+c*x^2)^(3/2)-12*c*d^3*(b*d+2*c*d*x)^(5/2)/Sqrt[a+b*x+c*x^2]+80*c^2*d^5*Sqrt[b*d+2*c*d*x]*Sqrt[a+b*x+c*x^2]+40*c*(b^2-4*a*c)^(5/4)*d^(11/2)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/Sqrt[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1608
  public void test0267() {
    check( //
        "Integrate[(c*e+d*e*x)^(1/2)/Sqrt[1-c^2-2*c*d*x-d^2*x^2], x]", //
        "2*EllipticE[ArcSin[Sqrt[c*e+d*e*x]/Sqrt[e]],-1]*Sqrt[e]/d-2*EllipticF[ArcSin[Sqrt[c*e+d*e*x]/Sqrt[e]],-1]*Sqrt[e]/d");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1678
  public void test0268() {
    check( //
        "Integrate[(d+e*x)^2*(a^2+2*a*b*x+b^2*x^2)^2, x]", //
        "1/5*(b*d-a*e)^2*(a+b*x)^5/b^3+1/3*e*(b*d-a*e)*(a+b*x)^6/b^3+1/7*e^2*(a+b*x)^7/b^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1697
  public void test0269() {
    check( //
        "Integrate[(d+e*x)^3*(a^2+2*a*b*x+b^2*x^2)^3, x]", //
        "1/7*(b*d-a*e)^3*(a+b*x)^7/b^4+3/8*e*(b*d-a*e)^2*(a+b*x)^8/b^4+1/3*e^2*(b*d-a*e)*(a+b*x)^9/b^4+1/10*e^3*(a+b*x)^10/b^4");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1718
  public void test0270() {
    check( //
        "Integrate[(d+e*x)^5/(a^2+2*a*b*x+b^2*x^2), x]", //
        "10*e^2*(b*d-a*e)^3*x/b^5-(b*d-a*e)^5/(b^6*(a+b*x))+5*e^3*(b*d-a*e)^2*(a+b*x)^2/b^6+5/3*e^4*(b*d-a*e)*(a+b*x)^3/b^6+1/4*e^5*(a+b*x)^4/b^6+5*e*(b*d-a*e)^4*Log[a+b*x]/b^6");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1779
  public void test0271() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(3/2)/(d+e*x)^3, x]", //
        "b^3*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^3*(a+b*x))+1/2*(b*d-a*e)^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^4*(a+b*x)*(d+e*x)^2)-3*b*(b*d-a*e)^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^4*(a+b*x)*(d+e*x))-3*b^2*(b*d-a*e)*Log[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^4*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1819
  public void test0272() {
    check( //
        "Integrate[1/(a^2+2*a*b*x+b^2*x^2)^(3/2), x]", //
        "(-1/2)/(b*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.2.3 (d+e x^2)^m (a+b x^2+c x^4)^p.input:71
  public void test0273() {
    check( //
        "Integrate[(1+2*x^2)/(1+5*x^2+4*x^4), x]", //
        "1/3*ArcTan[x]+1/3*ArcTan[2*x]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:181
  public void test0274() {
    check( //
        "Integrate[1/((2+b*x^2)^(1/3)*(18*d/b+d*x^2)), x]", //
        "1/12*ArcTan[1/3*(2^(1/3)-(2+b*x^2)^(1/3))^2/(2^(1/6)*x*Sqrt[b])]*Sqrt[b]/(2^(5/6)*d)+1/12*ArcTan[1/3*x*Sqrt[b]/Sqrt[2]]*Sqrt[b]/(2^(5/6)*d)-1/4*ArcTanh[2^(1/6)*(2^(1/3)-(2+b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(2^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:603
  public void test0275() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)*(A+B*x^2)/x^2, x]", //
        "1/4*(4*A*b+a*B)*x*(a+b*x^2)^(3/2)/a-A*(a+b*x^2)^(5/2)/(a*x)+3/8*a*(4*A*b+a*B)*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+3/8*(4*A*b+a*B)*x*Sqrt[a+b*x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:389
  public void test0276() {
    check( //
        "Integrate[1/(x*(a+b*x^3)^2), x]", //
        "1/3/(a*(a+b*x^3))+Log[x]/a^2-1/3*Log[a+b*x^3]/a^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:646
  public void test0277() {
    check( //
        "Integrate[x/(a+b*x^3)^(2/3), x]", //
        "-1/2*Log[b^(1/3)*x-(a+b*x^3)^(1/3)]/b^(2/3)-ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(2/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:912
  public void test0278() {
    check( //
        "Integrate[(1+x^4)^(1/2), x]", //
        "1/3*x*Sqrt[1+x^4]+1/3*(1+x^2)*EllipticF[2*ArcTan[x],1/2]*Sqrt[(1+x^4)/(1+x^2)^2]/Sqrt[1+x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1537
  public void test0279() {
    check( //
        "Integrate[1/(x^7*(1+x^6)), x]", //
        "(-1/6)/x^6-Log[x]+1/6*Log[1+x^6]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2054
  public void test0280() {
    check( //
        "Integrate[(a+b/x)^(3/2)*x^(7/2), x]", //
        "16/315*b^2*(a+b/x)^(5/2)*x^(5/2)/a^3-8/63*b*(a+b/x)^(5/2)*x^(7/2)/a^2+2/9*(a+b/x)^(5/2)*x^(9/2)/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2134
  public void test0281() {
    check( //
        "Integrate[(a+b/x^2)^3*x^3, x]", //
        "-1/2*b^3/x^2+3/2*a^2*b*x^2+1/4*a^3*x^4+3*a*b^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2924
  public void test0282() {
    check( //
        "Integrate[(a+b/x^(1/3))^3*x^3, x]", //
        "1/3*b^3*x^3+9/10*a*b^2*x^(10/3)+9/11*a^2*b*x^(11/3)+1/4*a^3*x^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3102
  public void test0283() {
    check( //
        "Integrate[x^(-1+4*n)*(a+b*x^n)^5, x]", //
        "-1/6*a^3*(a+b*x^n)^6/(b^4*n)+3/7*a^2*(a+b*x^n)^7/(b^4*n)-3/8*a*(a+b*x^n)^8/(b^4*n)+1/9*(a+b*x^n)^9/(b^4*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3123
  public void test0284() {
    check( //
        "Integrate[x^(-1+3*n)*(a+b*x^n)^8, x]", //
        "1/9*a^2*(a+b*x^n)^9/(b^3*n)-1/5*a*(a+b*x^n)^10/(b^3*n)+1/11*(a+b*x^n)^11/(b^3*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3255
  public void test0285() {
    check( //
        "Integrate[x^(-1+1/2*n)/Sqrt[a+b*x^n], x]", //
        "2*ArcTanh[x^(1/2*n)*Sqrt[b]/Sqrt[a+b*x^n]]/(n*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3292
  public void test0286() {
    check( //
        "Integrate[x^(-1+4*n)*(a+b*x^n)^p, x]", //
        "-a^3*(a+b*x^n)^(1+p)/(b^4*n*(1+p))+3*a^2*(a+b*x^n)^(2+p)/(b^4*n*(2+p))-3*a*(a+b*x^n)^(3+p)/(b^4*n*(3+p))+(a+b*x^n)^(4+p)/(b^4*n*(4+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3325
  public void test0287() {
    check( //
        "Integrate[x^m/(a+b*x^(2+2*m))^(7/2), x]", //
        "x^(1+m)/(a*(1+m)*(a+b*x^(2*(1+m)))^(5/2))+4/3*b*x^(3*(1+m))/(a^2*(1+m)*(a+b*x^(2*(1+m)))^(5/2))+8/15*b^2*x^(5*(1+m))/(a^3*(1+m)*(a+b*x^(2*(1+m)))^(5/2))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3372
  public void test0288() {
    check( //
        "Integrate[(c*x)^(-1-1/2*n)/Sqrt[a+b*x^n], x]", //
        "-2*Sqrt[a+b*x^n]/(a*c*n*(c*x)^(1/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3451
  public void test0289() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*(c+d*x)^2)^2, x]", //
        "1/4*a^2*(c+d*x)^4/d+1/3*a*b*(c+d*x)^6/d+1/8*b^2*(c+d*x)^8/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3698
  public void test0290() {
    check( //
        "Integrate[a+b*(c*x^n)^(1/n), x]", //
        "a*x+1/2*b*x*(c*x^n)^(1/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3733
  public void test0291() {
    check( //
        "Integrate[1/(a+b*(c*x^n)^(2/n))^2, x]", //
        "1/2*x/(a*(a+b*(c*x^n)^(2/n)))+1/2*x*ArcTan[(c*x^n)^(1/n)*Sqrt[b]/Sqrt[a]]/(a^(3/2)*(c*x^n)^(1/n)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3771
  public void test0292() {
    check( //
        "Integrate[1/(x*Sqrt[a+c/x+b*Sqrt[d/x]]), x]", //
        "2*ArcTanh[1/2*(2*a+b*Sqrt[d/x])/(Sqrt[a]*Sqrt[a+c/x+b*Sqrt[d/x]])]/Sqrt[a]");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:49
  public void test0293() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(7/3), x]", //
        "1/4*x*(a-b*x^3)/(a*(a+b*x^3)^(4/3))+3/4*x/(a*(a+b*x^3)^(1/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:198
  public void test0294() {
    check( //
        "Integrate[(c+d*x^4)^3/(a+b*x^4), x]", //
        "d*(3*b^2*c^2-3*a*b*c*d+a^2*d^2)*x/b^3+1/5*d^2*(3*b*c-a*d)*x^5/b^2+1/9*d^3*x^9/b-1/2*(b*c-a*d)^3*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(13/4)*Sqrt[2])+1/2*(b*c-a*d)^3*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(13/4)*Sqrt[2])-1/4*(b*c-a*d)^3*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(13/4)*Sqrt[2])+1/4*(b*c-a*d)^3*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(13/4)*Sqrt[2])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:508
  public void test0295() {
    check( //
        "Integrate[(a+b*x^2)/(x^3*Sqrt[-1+c*x]*Sqrt[1+c*x]), x]", //
        "1/2*(2*b+a*c^2)*ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]+1/2*a*Sqrt[-1+c*x]*Sqrt[1+c*x]/x^2");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:528
  public void test0296() {
    check( //
        "Integrate[(a+b*x^2)/(x^3*(-c+d*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "-1/2*(2*b*c^2+3*a*d^2)*ArcTan[Sqrt[-c+d*x]*Sqrt[c+d*x]/c]/c^5+1/2*(-2*b*c^2-3*a*d^2)/(c^4*Sqrt[-c+d*x]*Sqrt[c+d*x])+1/2*a/(c^2*x^2*Sqrt[-c+d*x]*Sqrt[c+d*x])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:29
  public void test0297() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^5, x]", //
        "-1/4*a^2*A/x^4-a*(2*A*b+a*B)/x+1/2*b*(A*b+2*a*B)*x^2+1/5*b^2*B*x^5");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:51
  public void test0298() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^8, x]", //
        "-1/7*a^5*A/x^7-1/4*a^4*(5*A*b+a*B)/x^4-5*a^3*b*(2*A*b+a*B)/x+5*a^2*b^2*(A*b+a*B)*x^2+a*b^3*(A*b+2*a*B)*x^5+1/8*b^4*(A*b+5*a*B)*x^8+1/11*b^5*B*x^11");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:75
  public void test0299() {
    check( //
        "Integrate[(A+B*x^3)/(a+b*x^3), x]", //
        "B*x/b+1/3*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(4/3))-1/6*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(4/3))-(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(4/3)*Sqrt[3])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2555
  public void test0300() {
    check( //
        "Integrate[x^2/(2-3*x+x^2), x]", //
        "x-Log[1-x]+4*Log[2-x]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:117
  public void test0301() {
    check( //
        "Integrate[1/(c+d*x^2)^(5/2), x]", //
        "1/3*x/(c*(c+d*x^2)^(3/2))+2/3*x/(c^2*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:252
  public void test0302() {
    check( //
        "Integrate[1/(Sqrt[a-b*x^2]*Sqrt[c+d*x^2]), x]", //
        "EllipticF[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[1+d*x^2/c]/(Sqrt[b]*Sqrt[a-b*x^2]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:19
  public void test0303() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/x^5, x]", //
        "-1/4*a*A/x^4+1/2*(-A*b-a*B)/x^2+b*B*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:133
  public void test0304() {
    check( //
        "Integrate[x^3*(a*c+b*c*x^2)/(a+b*x^2), x]", //
        "1/4*c*x^4");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:259
  public void test0305() {
    check( //
        "Integrate[x/((a+b*x^2)*(c+d*x^2)), x]", //
        "1/2*Log[a+b*x^2]/(b*c-a*d)-1/2*Log[c+d*x^2]/(b*c-a*d)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1138
  public void test0306() {
    check( //
        "Integrate[x/(Sqrt[a-b*x^2]*Sqrt[c-d*x^2]), x]", //
        "-ArcTanh[Sqrt[d]*Sqrt[a-b*x^2]/(Sqrt[b]*Sqrt[c-d*x^2])]/(Sqrt[b]*Sqrt[d])");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:162
  public void test0307() {
    check( //
        "Integrate[(-x+x^3)/Sqrt[-2+x^2], x]", //
        "1/3*(-2+x^2)^(3/2)+Sqrt[-2+x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:732
  public void test0308() {
    check( //
        "Integrate[(a+b*x^4)^3/x^5, x]", //
        "-1/4*a^3/x^4+3/4*a*b^2*x^4+1/8*b^3*x^8+3*a^2*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1206
  public void test0309() {
    check( //
        "Integrate[x/(a+b*x^4)^(1/4), x]", //
        "x^2/(a+b*x^4)^(1/4)-(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a+b*x^4)^(1/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1479
  public void test0310() {
    check( //
        "Integrate[x^8/(a+b*x^6), x]", //
        "1/3*x^3/b-1/3*ArcTan[x^3*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(3/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2362
  public void test0311() {
    check( //
        "Integrate[1/(x^3*Sqrt[a+b/x^3]), x]", //
        "-2*Sqrt[a+b/x^3]/(b^(2/3)*(b^(1/3)/x+a^(1/3)*(1+Sqrt[3])))-2*a^(1/3)*(a^(1/3)+b^(1/3)/x)*EllipticF[ArcSin[(b^(1/3)/x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(a^(2/3)+b^(2/3)/x^2-a^(1/3)*b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(2/3)*Sqrt[a+b/x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2])+3^(1/4)*a^(1/3)*(a^(1/3)+b^(1/3)/x)*EllipticE[ArcSin[(b^(1/3)/x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)+b^(2/3)/x^2-a^(1/3)*b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b/x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2707
  public void test0312() {
    check( //
        "Integrate[1/(x^6*(a+b*x^(3/2))^(2/3)), x]", //
        "-1/5*(a+b*x^(3/2))^(1/3)/(a*x^5)+9/35*b*(a+b*x^(3/2))^(1/3)/(a^2*x^(7/2))-27/70*b^2*(a+b*x^(3/2))^(1/3)/(a^3*x^2)+81/70*b^3*(a+b*x^(3/2))^(1/3)/(a^4*Sqrt[x])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2915
  public void test0313() {
    check( //
        "Integrate[(a+b/x^(1/3))^2*x^3, x]", //
        "3/10*b^2*x^(10/3)+6/11*a*b*x^(11/3)+1/4*a^2*x^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3090
  public void test0314() {
    check( //
        "Integrate[x^(-1+4*n)*(a+b*x^n)^3, x]", //
        "1/4*a^3*x^(4*n)/n+3/5*a^2*b*x^(5*n)/n+1/2*a*b^2*x^(6*n)/n+1/7*b^3*x^(7*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3116
  public void test0315() {
    check( //
        "Integrate[x^(-1-10*n)*(a+b*x^n)^5, x]", //
        "-1/10*a^5/(n*x^(10*n))-5/9*a^4*b/(n*x^(9*n))-5/4*a^3*b^2/(n*x^(8*n))-10/7*a^2*b^3/(n*x^(7*n))-5/6*a*b^4/(n*x^(6*n))-1/5*b^5/(n*x^(5*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3137
  public void test0316() {
    check( //
        "Integrate[x^(-1-11*n)*(a+b*x^n)^8, x]", //
        "-1/11*(a+b*x^n)^9/(a*n*x^(11*n))+1/55*b*(a+b*x^n)^9/(a^2*n*x^(10*n))-1/495*b^2*(a+b*x^n)^9/(a^3*n*x^(9*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3169
  public void test0317() {
    check( //
        "Integrate[x^(-1-n)/(a+b*x^n), x]", //
        "(-1)/(a*n*x^n)-b*Log[x]/a^2+b*Log[a+b*x^n]/(a^2*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3195
  public void test0318() {
    check( //
        "Integrate[x^(-1-2*n)/(a+b*x^n)^3, x]", //
        "(-1/2)/(a^3*n*x^(2*n))+3*b/(a^4*n*x^n)+1/2*b^2/(a^3*n*(a+b*x^n)^2)+3*b^2/(a^4*n*(a+b*x^n))+6*b^2*Log[x]/a^5-6*b^2*Log[a+b*x^n]/(a^5*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3347
  public void test0319() {
    check( //
        "Integrate[(c*x)^(-1-1/2*n)/(a+b*x^n), x]", //
        "(-2)/(a*c*n*(c*x)^(1/2*n))+2*x^(1/2*n)*ArcTan[Sqrt[a]/(x^(1/2*n)*Sqrt[b])]*Sqrt[b]/(a^(3/2)*c*n*(c*x)^(1/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3401
  public void test0320() {
    check( //
        "Integrate[Sqrt[(3+5*x)^2], x]", //
        "1/10*(3+5*x)*Sqrt[(3+5*x)^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3478
  public void test0321() {
    check( //
        "Integrate[(c+d*x)^4/(a+b*(c+d*x)^3), x]", //
        "1/2*(c+d*x)^2/(b*d)+1/3*a^(2/3)*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(b^(5/3)*d)-1/6*a^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(b^(5/3)*d)+a^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(b^(5/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3504
  public void test0322() {
    check( //
        "Integrate[1/((c+d*x)^4*(a+b*(c+d*x)^3)^3), x]", //
        "(-1/3)/(a^3*d*(c+d*x)^3)-1/6*b/(a^2*d*(a+b*(c+d*x)^3)^2)-2/3*b/(a^3*d*(a+b*(c+d*x)^3))-3*b*Log[c+d*x]/(a^4*d)+b*Log[a+b*(c+d*x)^3]/(a^4*d)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3715
  public void test0323() {
    check( //
        "Integrate[1/(x^2*(a+b*(c*x^n)^(1/n))^2), x]", //
        "(-1)/(a^2*x)-b*(c*x^n)^(1/n)/(a^2*x*(a+b*(c*x^n)^(1/n)))-2*b*(c*x^n)^(1/n)*Log[x]/(a^3*x)+2*b*(c*x^n)^(1/n)*Log[a+b*(c*x^n)^(1/n)]/(a^3*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3748
  public void test0324() {
    check( //
        "Integrate[1/(a+b*(c*x^n)^(3/n))^3, x]", //
        "1/6*x/(a*(a+b*(c*x^n)^(3/n))^2)+5/18*x/(a^2*(a+b*(c*x^n)^(3/n)))+5/27*x*Log[a^(1/3)+b^(1/3)*(c*x^n)^(1/n)]/(a^(8/3)*b^(1/3)*(c*x^n)^(1/n))-5/54*x*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c*x^n)^(1/n)+b^(2/3)*(c*x^n)^(2/n)]/(a^(8/3)*b^(1/3)*(c*x^n)^(1/n))-5/9*x*ArcTan[(a^(1/3)-2*b^(1/3)*(c*x^n)^(1/n))/(a^(1/3)*Sqrt[3])]/(a^(8/3)*b^(1/3)*(c*x^n)^(1/n)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3784
  public void test0325() {
    check( //
        "Integrate[(c*x^n)^(1/n)/(a+b*(c*x^n)^(1/n))^2, x]", //
        "a*x/(b^2*(c*x^n)^(1/n)*(a+b*(c*x^n)^(1/n)))+x*Log[a+b*(c*x^n)^(1/n)]/(b^2*(c*x^n)^(1/n))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:79
  public void test0326() {
    check( //
        "Integrate[(a+b*x^3)^(5/3)*(c+d*x^3), x]", //
        "5/162*a*(9*b*c-a*d)*x*(a+b*x^3)^(2/3)/b+1/54*(9*b*c-a*d)*x*(a+b*x^3)^(5/3)/b+1/9*d*x*(a+b*x^3)^(8/3)/b-5/162*a^2*(9*b*c-a*d)*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/b^(4/3)+5/81*a^2*(9*b*c-a*d)*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:113
  public void test0327() {
    check( //
        "Integrate[(a+b*x^3)^(2/3)/(c+d*x^3), x]", //
        "-1/6*(b*c-a*d)^(2/3)*Log[c+d*x^3]/(c^(2/3)*d)+1/2*(b*c-a*d)^(2/3)*Log[(b*c-a*d)^(1/3)*x/c^(1/3)-(a+b*x^3)^(1/3)]/(c^(2/3)*d)-1/2*b^(2/3)*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/d+b^(2/3)*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(d*Sqrt[3])-(b*c-a*d)^(2/3)*ArcTan[(1+2*(b*c-a*d)^(1/3)*x/(c^(1/3)*(a+b*x^3)^(1/3)))/Sqrt[3]]/(c^(2/3)*d*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:185
  public void test0328() {
    check( //
        "Integrate[(a+b*x^4)/(c+d*x^4), x]", //
        "b*x/d+1/2*(b*c-a*d)*ArcTan[1-d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*d^(5/4)*Sqrt[2])-1/2*(b*c-a*d)*ArcTan[1+d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*d^(5/4)*Sqrt[2])+1/4*(b*c-a*d)*Log[-c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*d^(5/4)*Sqrt[2])-1/4*(b*c-a*d)*Log[c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*d^(5/4)*Sqrt[2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:33
  public void test0329() {
    check( //
        "Integrate[(1-x)/(1+x^3), x]", //
        "2/3*Log[1+x]-1/3*Log[1-x+x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1341
  public void test0330() {
    check( //
        "Integrate[(b*d+2*c*d*x)^2/(a+b*x+c*x^2), x]", //
        "2*d^2*(b+2*c*x)-2*d^2*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]*Sqrt[b^2-4*a*c]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1359
  public void test0331() {
    check( //
        "Integrate[(b*d+2*c*d*x)^9/(a+b*x+c*x^2)^3, x]", //
        "96*c^2*(b^2-4*a*c)*d^9*(b+2*c*x)^2+48*c^2*d^9*(b+2*c*x)^4-1/2*d^9*(b+2*c*x)^8/(a+b*x+c*x^2)^2-8*c*d^9*(b+2*c*x)^6/(a+b*x+c*x^2)+96*c^2*(b^2-4*a*c)^2*d^9*Log[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1381
  public void test0332() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(1/2)/(b*d+2*c*d*x)^2, x]", //
        "1/4*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/(c^(3/2)*d^2)-1/2*Sqrt[a+b*x+c*x^2]/(c*d^2*(b+2*c*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1401
  public void test0333() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^10, x]", //
        "2/9*(a+b*x+c*x^2)^(5/2)/((b^2-4*a*c)*d^10*(b+2*c*x)^9)+8/63*(a+b*x+c*x^2)^(5/2)/((b^2-4*a*c)^2*d^10*(b+2*c*x)^7)+16/315*(a+b*x+c*x^2)^(5/2)/((b^2-4*a*c)^3*d^10*(b+2*c*x)^5)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1421
  public void test0334() {
    check( //
        "Integrate[(b*d+2*c*d*x)^4/(a+b*x+c*x^2)^(1/2), x]", //
        "3/8*(b^2-4*a*c)^2*d^4*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/Sqrt[c]+3/4*(b^2-4*a*c)*d^4*(b+2*c*x)*Sqrt[a+b*x+c*x^2]+1/2*d^4*(b+2*c*x)^3*Sqrt[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1439
  public void test0335() {
    check( //
        "Integrate[(b*d+2*c*d*x)^4/(a+b*x+c*x^2)^(5/2), x]", //
        "-2/3*d^4*(b+2*c*x)^3/(a+b*x+c*x^2)^(3/2)+16*c^(3/2)*d^4*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]-8*c*d^4*(b+2*c*x)/Sqrt[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1525
  public void test0336() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(3/2)*(a+b*x+c*x^2)^(1/2), x]", //
        "1/7*(b*d+2*c*d*x)^(5/2)*Sqrt[a+b*x+c*x^2]/(c*d)-2/21*(b^2-4*a*c)*d*Sqrt[b*d+2*c*d*x]*Sqrt[a+b*x+c*x^2]/c-1/21*(b^2-4*a*c)^(9/4)*d^(3/2)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^2*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1543
  public void test0337() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^(3/2), x]", //
        "-(a+b*x+c*x^2)^(3/2)/(c*d*Sqrt[b*d+2*c*d*x])+3/10*(b*d+2*c*d*x)^(3/2)*Sqrt[a+b*x+c*x^2]/(c^2*d^3)-3/10*(b^2-4*a*c)^(7/4)*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^3*d^(3/2)*Sqrt[a+b*x+c*x^2])+3/10*(b^2-4*a*c)^(7/4)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^3*d^(3/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1563
  public void test0338() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(3/2)/(a+b*x+c*x^2)^(1/2), x]", //
        "4/3*d*Sqrt[b*d+2*c*d*x]*Sqrt[a+b*x+c*x^2]+2/3*(b^2-4*a*c)^(5/4)*d^(3/2)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1583
  public void test0339() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(9/2)/(a+b*x+c*x^2)^(3/2), x]", //
        "-2*d*(b*d+2*c*d*x)^(7/2)/Sqrt[a+b*x+c*x^2]+56/5*c*d^3*(b*d+2*c*d*x)^(3/2)*Sqrt[a+b*x+c*x^2]+84/5*(b^2-4*a*c)^(7/4)*d^(9/2)*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/Sqrt[a+b*x+c*x^2]-84/5*(b^2-4*a*c)^(7/4)*d^(9/2)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/Sqrt[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1601
  public void test0340() {
    check( //
        "Integrate[(c*e+d*e*x)^(3/2)/Sqrt[1-c^2-2*c*d*x-d^2*x^2], x]", //
        "2/3*e^(3/2)*EllipticF[ArcSin[Sqrt[c*e+d*e*x]/Sqrt[e]],-1]/d-2/3*e*Sqrt[c*e+d*e*x]*Sqrt[1-c^2-2*c*d*x-d^2*x^2]/d");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1690
  public void test0341() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^2/(d+e*x)^10, x]", //
        "-1/9*(b*d-a*e)^4/(e^5*(d+e*x)^9)+1/2*b*(b*d-a*e)^3/(e^5*(d+e*x)^8)-6/7*b^2*(b*d-a*e)^2/(e^5*(d+e*x)^7)+2/3*b^3*(b*d-a*e)/(e^5*(d+e*x)^6)-1/5*b^4/(e^5*(d+e*x)^5)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1730
  public void test0342() {
    check( //
        "Integrate[(d+e*x)^4/(a^2+2*a*b*x+b^2*x^2)^2, x]", //
        "e^4*x/b^4-1/3*(b*d-a*e)^4/(b^5*(a+b*x)^3)-2*e*(b*d-a*e)^3/(b^5*(a+b*x)^2)-6*e^2*(b*d-a*e)^2/(b^5*(a+b*x))+4*e^3*(b*d-a*e)*Log[a+b*x]/b^5");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1792
  public void test0343() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(5/2)/(d+e*x), x]", //
        "b*(b*d-a*e)^4*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^5*(a+b*x))-1/2*(b*d-a*e)^3*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2]/e^4+1/3*(b*d-a*e)^2*(a+b*x)^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/e^3-1/4*(b*d-a*e)*(a+b*x)^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/e^2+1/5*(a+b*x)^4*Sqrt[a^2+2*a*b*x+b^2*x^2]/e-(b*d-a*e)^5*Log[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1812
  public void test0344() {
    check( //
        "Integrate[1/((d+e*x)^2*Sqrt[a^2+2*a*b*x+b^2*x^2]), x]", //
        "(a+b*x)/((b*d-a*e)*(d+e*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])+b*(a+b*x)*Log[a+b*x]/((b*d-a*e)^2*Sqrt[a^2+2*a*b*x+b^2*x^2])-b*(a+b*x)*Log[d+e*x]/((b*d-a*e)^2*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2107
  public void test0345() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^3/(d+e*x)^7, x]", //
        "1/3*(c*d^2-a*e^2)^3/(e^4*(d+e*x)^3)-3/2*c*d*(c*d^2-a*e^2)^2/(e^4*(d+e*x)^2)+3*c^2*d^2*(c*d^2-a*e^2)/(e^4*(d+e*x))+c^3*d^3*Log[d+e*x]/e^4");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2165
  public void test0346() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2)/(d+e*x), x]", //
        "-1/2*(c*d^2-a*e^2)*ArcTanh[1/2*(c*d^2+a*e^2+2*c*d*e*x)/(Sqrt[c]*Sqrt[d]*Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])]/(e^(3/2)*Sqrt[c]*Sqrt[d])+Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/e");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2182
  public void test0347() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^7, x]", //
        "2/9*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/((c*d^2-a*e^2)*(d+e*x)^7)+8/63*c*d*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/((c*d^2-a*e^2)^2*(d+e*x)^6)+16/315*c^2*d^2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/((c*d^2-a*e^2)^3*(d+e*x)^5)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2201
  public void test0348() {
    check( //
        "Integrate[(d+e*x)^3/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2), x]", //
        "5/16*(c*d^2-a*e^2)^3*ArcTanh[1/2*(c*d^2+a*e^2+2*c*d*e*x)/(Sqrt[c]*Sqrt[d]*Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])]/(c^(7/2)*d^(7/2)*Sqrt[e])+5/8*(c*d^2-a*e^2)^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(c^3*d^3)+5/12*(c*d^2-a*e^2)*(d+e*x)*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(c^2*d^2)+1/3*(d+e*x)^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(c*d)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2218
  public void test0349() {
    check( //
        "Integrate[1/((d+e*x)^4*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)), x]", //
        "2/9/((c*d^2-a*e^2)*(d+e*x)^4*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])+20/63*c*d/((c*d^2-a*e^2)^2*(d+e*x)^3*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])+32/63*c^2*d^2/((c*d^2-a*e^2)^3*(d+e*x)^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])+64/63*c^3*d^3/((c*d^2-a*e^2)^4*(d+e*x)*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])-256/63*c^4*d^4*(c*d^2+a*e^2+2*c*d*e*x)/((c*d^2-a*e^2)^6*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2311
  public void test0350() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2)/(d+e*x)^(3/2), x]", //
        "-2*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]*Sqrt[c*d^2-a*e^2]/e^(3/2)+2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e*Sqrt[d+e*x])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2369
  public void test0351() {
    check( //
        "Integrate[(d+e*x)^m*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^3, x]", //
        "-(c*d^2-a*e^2)^3*(d+e*x)^(4+m)/(e^4*(4+m))+3*c*d*(c*d^2-a*e^2)^2*(d+e*x)^(5+m)/(e^4*(5+m))-3*c^2*d^2*(c*d^2-a*e^2)*(d+e*x)^(6+m)/(e^4*(6+m))+c^3*d^3*(d+e*x)^(7+m)/(e^4*(7+m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2513
  public void test0352() {
    check( //
        "Integrate[1/(a+b*x+c*x^2)^4, x]", //
        "1/3*(-b-2*c*x)/((b^2-4*a*c)*(a+b*x+c*x^2)^3)+5/3*c*(b+2*c*x)/((b^2-4*a*c)^2*(a+b*x+c*x^2)^2)-10*c^2*(b+2*c*x)/((b^2-4*a*c)^3*(a+b*x+c*x^2))+40*c^3*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(7/2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2666
  public void test0353() {
    check( //
        "Integrate[(d+e*x)*(a+b*x+c*x^2)^(5/2), x]", //
        "-5/384*(b^2-4*a*c)*(2*c*d-b*e)*(b+2*c*x)*(a+b*x+c*x^2)^(3/2)/c^3+1/24*(2*c*d-b*e)*(b+2*c*x)*(a+b*x+c*x^2)^(5/2)/c^2+1/7*e*(a+b*x+c*x^2)^(7/2)/c-5/2048*(b^2-4*a*c)^3*(2*c*d-b*e)*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/c^(9/2)+5/1024*(b^2-4*a*c)^2*(2*c*d-b*e)*(b+2*c*x)*Sqrt[a+b*x+c*x^2]/c^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:804
  public void test0354() {
    check( //
        "Integrate[(a+b*x^2)^(1/3)/(c*x)^(29/3), x]", //
        "-3/8*(a+b*x^2)^(4/3)/(a*c*(c*x)^(26/3))+27/56*(a+b*x^2)^(7/3)/(a^2*c*(c*x)^(26/3))-81/280*(a+b*x^2)^(10/3)/(a^3*c*(c*x)^(26/3))+243/3640*(a+b*x^2)^(13/3)/(a^4*c*(c*x)^(26/3))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:31
  public void test0355() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^7, x]", //
        "-1/6*a^2*A/x^6-1/4*a*(2*A*b+a*B)/x^4-1/2*b*(A*b+2*a*B)/x^2+b^2*B*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:145
  public void test0356() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(x^2*(a+b*x^2)^2), x]", //
        "-c/(a*x)-c*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/a^(3/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1150
  public void test0357() {
    check( //
        "Integrate[x^2/(Sqrt[2-3*x^2]*Sqrt[1+4*x^2]), x]", //
        "1/4*EllipticE[ArcSin[x*Sqrt[3/2]],-8/3]/Sqrt[3]-1/4*EllipticF[ArcSin[x*Sqrt[3/2]],-8/3]/Sqrt[3]");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:20
  public void test0358() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x^2]/x^3, x]", //
        "-1/2*A*b*ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/Sqrt[a]+B*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]*Sqrt[b]-1/2*(A+2*B*x)*Sqrt[a+b*x^2]/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:82
  public void test0359() {
    check( //
        "Integrate[(c*x)^m*(b*x^2)^(1/2), x]", //
        "(c*x)^(2+m)*Sqrt[b*x^2]/(c^2*(2+m)*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:345
  public void test0360() {
    check( //
        "Integrate[x^5*(a+b*x^3)^8, x]", //
        "-1/27*a*(a+b*x^3)^9/b^2+1/30*(a+b*x^3)^10/b^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:746
  public void test0361() {
    check( //
        "Integrate[1/(a+c*x^4), x]", //
        "-1/2*ArcTan[1-c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*c^(1/4)*Sqrt[2])+1/2*ArcTan[1+c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*c^(1/4)*Sqrt[2])-1/4*Log[-a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(3/4)*c^(1/4)*Sqrt[2])+1/4*Log[a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(3/4)*c^(1/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1336
  public void test0362() {
    check( //
        "Integrate[x/(a-b*x^4)^(1/4), x]", //
        "(1-b*x^4/a)^(1/4)*EllipticE[1/2*ArcSin[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a-b*x^4)^(1/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1491
  public void test0363() {
    check( //
        "Integrate[1/(x^4*(a+b*x^6)), x]", //
        "(-1/3)/(a*x^3)-1/3*ArcTan[x^3*Sqrt[b]/Sqrt[a]]*Sqrt[b]/a^(3/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1631
  public void test0364() {
    check( //
        "Integrate[1/(x*(a-b*x^7)), x]", //
        "Log[x]/a-1/7*Log[a-b*x^7]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1840
  public void test0365() {
    check( //
        "Integrate[(a+b/x)^2/x, x]", //
        "-1/2*b^2/x^2-2*a*b/x+a^2*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2268
  public void test0366() {
    check( //
        "Integrate[(1+1/x^2)^(1/3)/x^3, x]", //
        "-3/8*(1+1/x^2)^(4/3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2442
  public void test0367() {
    check( //
        "Integrate[x/(a+b/x^4)^(5/2), x]", //
        "-1/6*x^2/(a*(a+b/x^4)^(3/2))-2/3*x^2/(a^2*Sqrt[a+b/x^4])+4/3*x^2*Sqrt[a+b/x^4]/a^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2708
  public void test0368() {
    check( //
        "Integrate[1/(x^9*(a+b*x^(3/2))^(2/3)), x]", //
        "-1/8*(a+b*x^(3/2))^(1/3)/(a*x^8)+15/104*b*(a+b*x^(3/2))^(1/3)/(a^2*x^(13/2))-9/52*b^2*(a+b*x^(3/2))^(1/3)/(a^3*x^5)+81/364*b^3*(a+b*x^(3/2))^(1/3)/(a^4*x^(7/2))-243/728*b^4*(a+b*x^(3/2))^(1/3)/(a^5*x^2)+729/728*b^5*(a+b*x^(3/2))^(1/3)/(a^6*Sqrt[x])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2916
  public void test0369() {
    check( //
        "Integrate[(a+b/x^(1/3))^2*x^2, x]", //
        "3/7*b^2*x^(7/3)+3/4*a*b*x^(8/3)+1/3*a^2*x^3");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3045
  public void test0370() {
    check( //
        "Integrate[1/(x*(a+b*x^n)^(1/2)), x]", //
        "-2*ArcTanh[Sqrt[a+b*x^n]/Sqrt[a]]/(n*Sqrt[a])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3091
  public void test0371() {
    check( //
        "Integrate[x^(-1+3*n)*(a+b*x^n)^3, x]", //
        "1/3*a^3*x^(3*n)/n+3/4*a^2*b*x^(4*n)/n+3/5*a*b^2*x^(5*n)/n+1/6*b^3*x^(6*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3117
  public void test0372() {
    check( //
        "Integrate[x^(-1+9*n)*(a+b*x^n)^8, x]", //
        "1/9*a^8*x^(9*n)/n+4/5*a^7*b*x^(10*n)/n+28/11*a^6*b^2*x^(11*n)/n+14/3*a^5*b^3*x^(12*n)/n+70/13*a^4*b^4*x^(13*n)/n+4*a^3*b^5*x^(14*n)/n+28/15*a^2*b^6*x^(15*n)/n+1/2*a*b^7*x^(16*n)/n+1/17*b^8*x^(17*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3140
  public void test0373() {
    check( //
        "Integrate[x^(-1-14*n)*(a+b*x^n)^8, x]", //
        "-1/14*a^8/(n*x^(14*n))-8/13*a^7*b/(n*x^(13*n))-7/3*a^6*b^2/(n*x^(12*n))-56/11*a^5*b^3/(n*x^(11*n))-7*a^4*b^4/(n*x^(10*n))-56/9*a^3*b^5/(n*x^(9*n))-7/2*a^2*b^6/(n*x^(8*n))-8/7*a*b^7/(n*x^(7*n))-1/6*b^8/(n*x^(6*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3170
  public void test0374() {
    check( //
        "Integrate[x^(-3-2*(-1+n))/(a+b*x^n), x]", //
        "(-1/2)/(a*n*x^(2*n))+b/(a^2*n*x^n)+b^2*Log[x]/a^3-b^2*Log[a+b*x^n]/(a^3*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3196
  public void test0375() {
    check( //
        "Integrate[x^(-1-1/2*n)/(a+b*x^n), x]", //
        "(-2)/(a*n*x^(1/2*n))+2*ArcTan[Sqrt[a]/(x^(1/2*n)*Sqrt[b])]*Sqrt[b]/(a^(3/2)*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3402
  public void test0376() {
    check( //
        "Integrate[Sqrt[(6+10*x)^2], x]", //
        "1/5*(3+5*x)*Sqrt[(3+5*x)^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3481
  public void test0377() {
    check( //
        "Integrate[(c+d*x)/(a+b*(c+d*x)^3), x]", //
        "-1/3*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(1/3)*b^(2/3)*d)+1/6*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(1/3)*b^(2/3)*d)-ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(1/3)*b^(2/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3505
  public void test0378() {
    check( //
        "Integrate[(c*e+d*e*x)^4/(a+b*(c+d*x)^3), x]", //
        "1/2*e^4*(c+d*x)^2/(b*d)+1/3*a^(2/3)*e^4*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(b^(5/3)*d)-1/6*a^(2/3)*e^4*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(b^(5/3)*d)+a^(2/3)*e^4*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(b^(5/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3716
  public void test0379() {
    check( //
        "Integrate[1/(x^3*(a+b*(c*x^n)^(1/n))^2), x]", //
        "(-1/2)/(a^2*x^2)+2*b*(c*x^n)^(1/n)/(a^3*x^2)+b^2*(c*x^n)^(2/n)/(a^3*x^2*(a+b*(c*x^n)^(1/n)))+3*b^2*(c*x^n)^(2/n)*Log[x]/(a^4*x^2)-3*b^2*(c*x^n)^(2/n)*Log[a+b*(c*x^n)^(1/n)]/(a^4*x^2)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:80
  public void test0380() {
    check( //
        "Integrate[(a+b*x^3)^(2/3)*(c+d*x^3), x]", //
        "1/18*(6*b*c-a*d)*x*(a+b*x^3)^(2/3)/b+1/6*d*x*(a+b*x^3)^(5/3)/b-1/18*a*(6*b*c-a*d)*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/b^(4/3)+1/9*a*(6*b*c-a*d)*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:400
  public void test0381() {
    check( //
        "Integrate[(-1+x^(1/3))/(1+x^(1/3)), x]", //
        "6*x^(1/3)-3*x^(2/3)+x-6*Log[1+x^(1/3)]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:65
  public void test0382() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^22, x]", //
        "-1/21*A*(a+b*x^3)^6/(a*x^21)+1/126*(A*b-7*a*B)*(a+b*x^3)^6/(a^2*x^18)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1071
  public void test0383() {
    check( //
        "Integrate[1/((1+x)*Sqrt[1-x]), x]", //
        "-ArcTanh[Sqrt[1-x]/Sqrt[2]]*Sqrt[2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:33
  public void test0384() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^9, x]", //
        "-1/8*A*(a+b*x^2)^3/(a*x^8)+1/24*(A*b-4*a*B)*(a+b*x^2)^3/(a^2*x^6)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:147
  public void test0385() {
    check( //
        "Integrate[x^3*(a*c+b*c*x^2)/(a+b*x^2)^3, x]", //
        "1/2*a*c/(b^2*(a+b*x^2))+1/2*c*Log[a+b*x^2]/b^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:84
  public void test0386() {
    check( //
        "Integrate[(c*x)^m/(b*x^2)^(3/2), x]", //
        "-c^2*x*(c*x)^(-2+m)/(b*(2-m)*Sqrt[b*x^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:225
  public void test0387() {
    check( //
        "Integrate[x^m/(a*x^n)^(1/n), x]", //
        "x^(1+m)/(m*(a*x^n)^(1/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:347
  public void test0388() {
    check( //
        "Integrate[(a+b*x^3)^8/x, x]", //
        "8/3*a^7*b*x^3+14/3*a^6*b^2*x^6+56/9*a^5*b^3*x^9+35/6*a^4*b^4*x^12+56/15*a^3*b^5*x^15+14/9*a^2*b^6*x^18+8/21*a*b^7*x^21+1/24*b^8*x^24+a^8*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:748
  public void test0389() {
    check( //
        "Integrate[1/(x^4*(a+c*x^4)), x]", //
        "(-1/3)/(a*x^3)+1/2*c^(3/4)*ArcTan[1-c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(7/4)*Sqrt[2])-1/2*c^(3/4)*ArcTan[1+c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(7/4)*Sqrt[2])+1/4*c^(3/4)*Log[-a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(7/4)*Sqrt[2])-1/4*c^(3/4)*Log[a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(7/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1106
  public void test0390() {
    check( //
        "Integrate[(a+b*x^4)^(1/4)/x, x]", //
        "(a+b*x^4)^(1/4)-1/2*a^(1/4)*ArcTan[(a+b*x^4)^(1/4)/a^(1/4)]-1/2*a^(1/4)*ArcTanh[(a+b*x^4)^(1/4)/a^(1/4)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1220
  public void test0391() {
    check( //
        "Integrate[x^2/(a+b*x^4)^(1/4), x]", //
        "1/2*x^3/(a+b*x^4)^(1/4)+1/2*(1+a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCot[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a+b*x^4)^(1/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1842
  public void test0392() {
    check( //
        "Integrate[(a+b/x)^2/x^3, x]", //
        "-1/4*b^2/x^4-2/3*a*b/x^3-1/2*a^2/x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2646
  public void test0393() {
    check( //
        "Integrate[Sqrt[x]*(a+b*Sqrt[x])^n, x]", //
        "2*a^2*(a+b*Sqrt[x])^(1+n)/(b^3*(1+n))-4*a*(a+b*Sqrt[x])^(2+n)/(b^3*(2+n))+2*(a+b*Sqrt[x])^(3+n)/(b^3*(3+n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2917
  public void test0394() {
    check( //
        "Integrate[(a+b/x^(1/3))^2*x, x]", //
        "3/4*b^2*x^(4/3)+6/5*a*b*x^(5/3)+1/2*a^2*x^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3092
  public void test0395() {
    check( //
        "Integrate[x^(-1+2*n)*(a+b*x^n)^3, x]", //
        "-1/4*a*(a+b*x^n)^4/(b^2*n)+1/5*(a+b*x^n)^5/(b^2*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3118
  public void test0396() {
    check( //
        "Integrate[x^(-1+8*n)*(a+b*x^n)^8, x]", //
        "1/8*a^8*x^(8*n)/n+8/9*a^7*b*x^(9*n)/n+14/5*a^6*b^2*x^(10*n)/n+56/11*a^5*b^3*x^(11*n)/n+35/6*a^4*b^4*x^(12*n)/n+56/13*a^3*b^5*x^(13*n)/n+2*a^2*b^6*x^(14*n)/n+8/15*a*b^7*x^(15*n)/n+1/16*b^8*x^(16*n)/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3171
  public void test0397() {
    check( //
        "Integrate[x^(-4-3*(-1+n))/(a+b*x^n), x]", //
        "(-1/3)/(a*n*x^(3*n))+1/2*b/(a^2*n*x^(2*n))-b^2/(a^3*n*x^n)-b^3*Log[x]/a^4+b^3*Log[a+b*x^n]/(a^4*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3317
  public void test0398() {
    check( //
        "Integrate[x^m/(a+b*x^(2+2*m))^2, x]", //
        "1/2*x^(1+m)/(a*(1+m)*(a+b*x^(2*(1+m))))+1/2*ArcTan[x^(1+m)*Sqrt[b]/Sqrt[a]]/(a^(3/2)*(1+m)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3403
  public void test0399() {
    check( //
        "Integrate[1/Sqrt[(3+5*x)^2], x]", //
        "1/5*(3+5*x)*Log[3+5*x]/Sqrt[(3+5*x)^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3440
  public void test0400() {
    check( //
        "Integrate[(c*(a+b*x)^(3/2))^(2/3), x]", //
        "1/2*(a+b*x)*(c*(a+b*x)^(3/2))^(2/3)/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3485
  public void test0401() {
    check( //
        "Integrate[1/((c+d*x)^3*(a+b*(c+d*x)^3)), x]", //
        "(-1/2)/(a*d*(c+d*x)^2)-1/3*b^(2/3)*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(5/3)*d)+1/6*b^(2/3)*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(5/3)*d)+b^(2/3)*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(5/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3506
  public void test0402() {
    check( //
        "Integrate[(c*e+d*e*x)^3/(a+b*(c+d*x)^3), x]", //
        "e^3*x/b-1/3*a^(1/3)*e^3*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(b^(4/3)*d)+1/6*a^(1/3)*e^3*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(b^(4/3)*d)+a^(1/3)*e^3*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(b^(4/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3721
  public void test0403() {
    check( //
        "Integrate[x^3*(a+b*(c*x^n)^(1/n))^p, x]", //
        "-a^3*x^4*(a+b*(c*x^n)^(1/n))^(1+p)/(b^4*(1+p)*(c*x^n)^(4/n))+3*a^2*x^4*(a+b*(c*x^n)^(1/n))^(2+p)/(b^4*(2+p)*(c*x^n)^(4/n))-3*a*x^4*(a+b*(c*x^n)^(1/n))^(3+p)/(b^4*(3+p)*(c*x^n)^(4/n))+x^4*(a+b*(c*x^n)^(1/n))^(4+p)/(b^4*(4+p)*(c*x^n)^(4/n))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:34
  public void test0404() {
    check( //
        "Integrate[(c+d*x^3)^4/(a+b*x^3)^2, x]", //
        "d^2*(6*b^2*c^2-8*a*b*c*d+3*a^2*d^2)*x/b^4+1/2*d^3*(2*b*c-a*d)*x^4/b^3+1/7*d^4*x^7/b^2+1/3*(b*c-a*d)^4*x/(a*b^4*(a+b*x^3))+2/9*(b*c-a*d)^3*(b*c+5*a*d)*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(13/3))-1/9*(b*c-a*d)^3*(b*c+5*a*d)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(13/3))-2/3*(b*c-a*d)^3*(b*c+5*a*d)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(13/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:81
  public void test0405() {
    check( //
        "Integrate[(c+d*x^3)/(a+b*x^3)^(1/3), x]", //
        "1/3*d*x*(a+b*x^3)^(2/3)/b-1/6*(3*b*c-a*d)*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/b^(4/3)+1/3*(3*b*c-a*d)*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:255
  public void test0406() {
    check( //
        "Integrate[(a+b*x^4)^(3/4)/(c+d*x^4), x]", //
        "1/2*b^(3/4)*ArcTan[b^(1/4)*x/(a+b*x^4)^(1/4)]/d-1/2*(b*c-a*d)^(3/4)*ArcTan[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*d)+1/2*b^(3/4)*ArcTanh[b^(1/4)*x/(a+b*x^4)^(1/4)]/d-1/2*(b*c-a*d)^(3/4)*ArcTanh[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*d)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:20
  public void test0407() {
    check( //
        "Integrate[(a+b*x^3)*(A+B*x^3)/x^6, x]", //
        "-1/5*a*A/x^5+1/2*(-A*b-a*B)/x^2+b*B*x");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:44
  public void test0408() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x, x]", //
        "5/3*a^4*A*b*x^3+5/3*a^3*A*b^2*x^6+10/9*a^2*A*b^3*x^9+5/12*a*A*b^4*x^12+1/15*A*b^5*x^15+1/18*B*(a+b*x^3)^6/b+a^5*A*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:66
  public void test0409() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^23, x]", //
        "-1/22*a^5*A/x^22-1/19*a^4*(5*A*b+a*B)/x^19-5/16*a^3*b*(2*A*b+a*B)/x^16-10/13*a^2*b^2*(A*b+a*B)/x^13-1/2*a*b^3*(A*b+2*a*B)/x^10-1/7*b^4*(A*b+5*a*B)/x^7-1/4*b^5*B/x^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:254
  public void test0410() {
    check( //
        "Integrate[(A+B*x^3)/(x^4*Sqrt[a+b*x^3]), x]", //
        "1/3*(A*b-2*a*B)*ArcTanh[Sqrt[a+b*x^3]/Sqrt[a]]/a^(3/2)-1/3*A*Sqrt[a+b*x^3]/(a*x^3)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:274
  public void test0411() {
    check( //
        "Integrate[(A+B*x^3)/(x^3*(a+b*x^3)^(3/2)), x]", //
        "-1/2*A/(a*x^2*Sqrt[a+b*x^3])-1/6*(7*A*b-4*a*B)*x/(a^2*Sqrt[a+b*x^3])-1/6*(7*A*b-4*a*B)*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a^2*b^(1/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:358
  public void test0412() {
    check( //
        "Integrate[x^2/((8*c-d*x^3)*Sqrt[c+d*x^3]), x]", //
        "2/9*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]/(d*Sqrt[c])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:421
  public void test0413() {
    check( //
        "Integrate[x^8*(c+d*x^3)^(3/2)/(a+b*x^3), x]", //
        "2/9*a^2*(c+d*x^3)^(3/2)/b^3-2/15*(b*c+a*d)*(c+d*x^3)^(5/2)/(b^2*d^2)+2/21*(c+d*x^3)^(7/2)/(b*d^2)-2/3*a^2*(b*c-a*d)^(3/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x^3]/Sqrt[b*c-a*d]]/b^(9/2)+2/3*a^2*(b*c-a*d)*Sqrt[c+d*x^3]/b^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:886
  public void test0414() {
    check( //
        "Integrate[x^4/((a+b*x^4)*(c+d*x^4)), x]", //
        "1/2*a^(1/4)*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(b^(1/4)*(b*c-a*d)*Sqrt[2])-1/2*a^(1/4)*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(b^(1/4)*(b*c-a*d)*Sqrt[2])-1/2*c^(1/4)*ArcTan[1-d^(1/4)*x*Sqrt[2]/c^(1/4)]/(d^(1/4)*(b*c-a*d)*Sqrt[2])+1/2*c^(1/4)*ArcTan[1+d^(1/4)*x*Sqrt[2]/c^(1/4)]/(d^(1/4)*(b*c-a*d)*Sqrt[2])+1/4*a^(1/4)*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(b^(1/4)*(b*c-a*d)*Sqrt[2])-1/4*a^(1/4)*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(b^(1/4)*(b*c-a*d)*Sqrt[2])-1/4*c^(1/4)*Log[-c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(d^(1/4)*(b*c-a*d)*Sqrt[2])+1/4*c^(1/4)*Log[c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(d^(1/4)*(b*c-a*d)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:938
  public void test0415() {
    check( //
        "Integrate[1/(x^3*(a+b*x^4)*Sqrt[c+d*x^4]), x]", //
        "-1/2*b*ArcTan[x^2*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^4])]/(a^(3/2)*Sqrt[b*c-a*d])-1/2*Sqrt[c+d*x^4]/(a*c*x^2)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1048
  public void test0416() {
    check( //
        "Integrate[x^11/((a+b*x^8)*Sqrt[c+d*x^8]), x]", //
        "1/4*ArcTanh[x^4*Sqrt[d]/Sqrt[c+d*x^8]]/(b*Sqrt[d])-1/4*ArcTan[x^4*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^8])]*Sqrt[a]/(b*Sqrt[b*c-a*d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1267
  public void test0417() {
    check( //
        "Integrate[x^(-1+2*n)/((a+b*x^n)^(3/2)*Sqrt[c+d*x^n]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x^n]/(Sqrt[b]*Sqrt[c+d*x^n])]/(b^(3/2)*n*Sqrt[d])+2*a*Sqrt[c+d*x^n]/(b*(b*c-a*d)*n*Sqrt[a+b*x^n])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:35
  public void test0418() {
    check( //
        "Integrate[(c+d*x)/(c^3+d^3*x^3), x]", //
        "-2*ArcTan[(c-2*d*x)/(c*Sqrt[3])]/(c*d*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:59
  public void test0419() {
    check( //
        "Integrate[(a^(1/3)*b^(1/3)*B+2*a^(2/3)*C+b^(2/3)*B*x+b^(2/3)*C*x^2)/(a+b*x^3), x]", //
        "C*Log[a^(1/3)+b^(1/3)*x]/b^(1/3)-2*(B/a^(1/3)+C/b^(1/3))*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/Sqrt[3]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:82
  public void test0420() {
    check( //
        "Integrate[(a+b*x^3)^(3/2)*(a*c+a*d*x+b*c*x^3+b*d*x^4), x]", //
        "30/46189*a*(247*c*x+187*d*x^2)*(a+b*x^3)^(3/2)+2/323*(19*c*x+17*d*x^2)*(a+b*x^3)^(5/2)+54/323323*a^2*(1729*c*x+935*d*x^2)*Sqrt[a+b*x^3]+810/1729*a^3*d*Sqrt[a+b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-405/1729*3^(1/4)*a^(10/3)*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+54/323323*3^(3/4)*a^3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(1729*b^(1/3)*c-935*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:115
  public void test0421() {
    check( //
        "Integrate[(1-x+Sqrt[3])/Sqrt[1-x^3], x]", //
        "-2*Sqrt[1-x^3]/(1-x+Sqrt[3])+3^(1/4)*(1-x)*EllipticE[ArcSin[(1-x-Sqrt[3])/(1-x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x+Sqrt[3])^2]/(Sqrt[1-x^3]*Sqrt[(1-x)/(1-x+Sqrt[3])^2])-4*3^(1/4)*(1-x)*EllipticF[ArcSin[(1-x-Sqrt[3])/(1-x+Sqrt[3])],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(1+x+x^2)/(1-x+Sqrt[3])^2]/(Sqrt[1-x^3]*Sqrt[(1-x)/(1-x+Sqrt[3])^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:141
  public void test0422() {
    check( //
        "Integrate[(-b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/Sqrt[a-b*x^3], x]", //
        "-2*Sqrt[a-b*x^3]/(b^(1/3)*(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+3^(1/4)*a^(1/3)*(a^(1/3)-b^(1/3)*x)*EllipticE[ArcSin[(-b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(1/3)*Sqrt[a-b*x^3]*Sqrt[a^(1/3)*(a^(1/3)-b^(1/3)*x)/(-b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:176
  public void test0423() {
    check( //
        "Integrate[(c+d*x)/(a-b*x^4)^4, x]", //
        "1/12*x*(c+d*x)/(a*(a-b*x^4)^3)+1/96*x*(11*c+10*d*x)/(a^2*(a-b*x^4)^2)+1/384*x*(77*c+60*d*x)/(a^3*(a-b*x^4))+77/256*c*ArcTan[b^(1/4)*x/a^(1/4)]/(a^(15/4)*b^(1/4))+77/256*c*ArcTanh[b^(1/4)*x/a^(1/4)]/(a^(15/4)*b^(1/4))+5/32*d*ArcTanh[x^2*Sqrt[b]/Sqrt[a]]/(a^(7/2)*Sqrt[b])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:226
  public void test0424() {
    check( //
        "Integrate[d*x^3/(2+3*x^4), x]", //
        "1/12*d*Log[2+3*x^4]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:345
  public void test0425() {
    check( //
        "Integrate[x^7*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3), x]", //
        "-1/2*a*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^2/b^5+1/5*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^5/b^4+1/8*(b^2*d-a*b*e+a^2*f)*x^8/b^3+1/11*(b*e-a*f)*x^11/b^2+1/14*f*x^14/b-1/3*a^(5/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(17/3)+1/6*a^(5/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(17/3)-a^(5/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(17/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:401
  public void test0426() {
    check( //
        "Integrate[x^6*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^3, x]", //
        "(b^2*d-3*a*b*e+6*a^2*f)*x/b^5+1/4*(b*e-3*a*f)*x^4/b^4+1/7*f*x^7/b^3+1/6*a*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/(b^5*(a+b*x^3)^2)-1/18*(7*b^3*c-13*a*b^2*d+19*a^2*b*e-25*a^3*f)*x/(b^5*(a+b*x^3))+1/27*(2*b^3*c-14*a*b^2*d+35*a^2*b*e-65*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/(a^(2/3)*b^(16/3))-1/54*(2*b^3*c-14*a*b^2*d+35*a^2*b*e-65*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(2/3)*b^(16/3))-1/9*(2*b^3*c-14*a*b^2*d+35*a^2*b*e-65*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(16/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:448
  public void test0427() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)^3, x]", //
        "a^3*c*x+1/2*a^3*d*x^2+3/4*a^2*b*c*x^4+3/5*a^2*b*d*x^5+3/7*a*b^2*c*x^7+3/8*a*b^2*d*x^8+1/10*b^3*c*x^10+1/11*b^3*d*x^11+1/12*e*(a+b*x^3)^4/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:488
  public void test0428() {
    check( //
        "Integrate[(2*a*x-x^2)/(a^3+x^3), x]", //
        "-Log[a+x]-2*ArcTan[(a-2*x)/(a*Sqrt[3])]/Sqrt[3]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:529
  public void test0429() {
    check( //
        "Integrate[(a+b*x^3)^3*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/x^5, x]", //
        "-1/4*a^3*c/x^4-1/3*a^3*d/x^3-1/2*a^3*e/x^2-a^2*(3*b*c+a*f)/x+a^2*(3*b*e+a*h)*x+3/2*a*b*(b*c+a*f)*x^2+a*b*(b*d+a*g)*x^3+3/4*a*b*(b*e+a*h)*x^4+1/5*b^2*(b*c+3*a*f)*x^5+1/6*b^2*(b*d+3*a*g)*x^6+1/7*b^2*(b*e+3*a*h)*x^7+1/8*b^3*f*x^8+1/9*b^3*g*x^9+1/10*b^3*h*x^10+a^2*(3*b*d+a*g)*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:550
  public void test0430() {
    check( //
        "Integrate[x^4*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3)^3, x]", //
        "h*x/b^3+1/6*x*(a*(b*e-a*h)-b*(b*c-a*f)*x-b*(b*d-a*g)*x^2)/(b^3*(a+b*x^3)^2)-1/18*x*(a*(7*b*e-13*a*h)-2*b*(b*c-4*a*f)*x-3*b*(b*d-3*a*g)*x^2)/(a*b^3*(a+b*x^3))-1/27*(b^(2/3)*(b*c+5*a*f)-2*a^(2/3)*(b*e-7*a*h))*Log[a^(1/3)+b^(1/3)*x]/(a^(4/3)*b^(10/3))+1/54*(b^(2/3)*(b*c+5*a*f)-2*a^(2/3)*(b*e-7*a*h))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(4/3)*b^(10/3))+1/3*g*Log[a+b*x^3]/b^3-1/9*(b^(5/3)*c+2*a^(2/3)*b*e+5*a*b^(2/3)*f-14*a^(5/3)*h)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(4/3)*b^(10/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:574
  public void test0431() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2)/(a+b*x^3)^(3/2), x]", //
        "-2/3*x*(c+d*x+e*x^2)/(b*Sqrt[a+b*x^3])+4/3*e*Sqrt[a+b*x^3]/b^2+8/3*d*Sqrt[a+b*x^3]/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-4*a^(1/3)*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(3/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+4/3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(b^(1/3)*c-2*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:629
  public void test0432() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^2, x]", //
        "1/5*a^2*d*x^5+1/6*a^2*e*x^6+1/7*a^2*f*x^7+2/9*a*b*d*x^9+1/5*a*b*e*x^10+2/11*a*b*f*x^11+1/13*b^2*d*x^13+1/14*b^2*e*x^14+1/15*b^2*f*x^15+1/12*c*(a+b*x^4)^3/b");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:653
  public void test0433() {
    check( //
        "Integrate[x*(c+d*x+e*x^2+f*x^3)*Sqrt[a+b*x^4], x]", //
        "1/6*e*(a+b*x^4)^(3/2)/b+1/4*a*c*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]/Sqrt[b]+2/21*a*f*x*Sqrt[a+b*x^4]/b+1/4*c*x^2*Sqrt[a+b*x^4]+1/35*x^3*(7*d+5*f*x^2)*Sqrt[a+b*x^4]+2/5*a*d*x*Sqrt[a+b*x^4]/(Sqrt[b]*(Sqrt[a]+x^2*Sqrt[b]))-2/5*a^(5/4)*d*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])+1/105*a^(5/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-5*f*Sqrt[a]+21*d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:672
  public void test0434() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^3, x]", //
        "-1/6*(3*c-e*x^2)*(a+b*x^4)^(3/2)/x^2-1/7*(7*d-f*x^2)*(a+b*x^4)^(3/2)/x-1/2*a^(3/2)*e*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]+3/4*a*c*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]*Sqrt[b]+1/4*(2*a*e+3*b*c*x^2)*Sqrt[a+b*x^4]+2/35*x*(5*a*f+21*b*d*x^2)*Sqrt[a+b*x^4]+12/5*a*d*x*Sqrt[b]*Sqrt[a+b*x^4]/(Sqrt[a]+x^2*Sqrt[b])-12/5*a^(5/4)*b^(1/4)*d*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]+2/35*a^(5/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(5*f*Sqrt[a]+21*d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(1/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:693
  public void test0435() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)/(x^3*Sqrt[a+b*x^4]), x]", //
        "-1/2*e*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/Sqrt[a]-1/2*c*Sqrt[a+b*x^4]/(a*x^2)-d*Sqrt[a+b*x^4]/(a*x)+d*x*Sqrt[b]*Sqrt[a+b*x^4]/(a*(Sqrt[a]+x^2*Sqrt[b]))-b^(1/4)*d*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*Sqrt[a+b*x^4])+1/2*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(f*Sqrt[a]+d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*b^(1/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:760
  public void test0436() {
    check( //
        "Integrate[(c+d*x^(-1+n))*(a+b*x^n)^3, x]", //
        "a^3*c*x+3*a^2*b*c*x^(1+n)/(1+n)+3*a*b^2*c*x^(1+2*n)/(1+2*n)+b^3*c*x^(1+3*n)/(1+3*n)+1/4*d*(a+b*x^n)^4/(b*n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:43
  public void test0437() {
    check( //
        "Integrate[x^4/(x-x^3), x]", //
        "-1/2*x^2-1/2*Log[1-x^2]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:111
  public void test0438() {
    check( //
        "Integrate[x^(13/2)/(a*x+b*x^3)^(9/2), x]", //
        "1/7*x^(13/2)/(a*(a*x+b*x^3)^(7/2))+4/35*x^(11/2)/(a^2*(a*x+b*x^3)^(5/2))+8/105*x^(9/2)/(a^3*(a*x+b*x^3)^(3/2))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:175
  public void test0439() {
    check( //
        "Integrate[1/(x^(3/2)*(b*x^(1/2)+a*x)^(1/2)), x]", //
        "-4/3*Sqrt[a*x+b*Sqrt[x]]/(b*x)+8/3*a*Sqrt[a*x+b*Sqrt[x]]/(b^2*Sqrt[x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:243
  public void test0440() {
    check( //
        "Integrate[x*(b*x^(2/3)+a*x)^(3/2), x]", //
        "-256/1615*b^3*(b*x^(2/3)+a*x)^(5/2)/a^4+65536/4849845*b^8*(b*x^(2/3)+a*x)^(5/2)/(a^9*x^(5/3))-32768/969969*b^7*(b*x^(2/3)+a*x)^(5/2)/(a^8*x^(4/3))+8192/138567*b^6*(b*x^(2/3)+a*x)^(5/2)/(a^7*x)-4096/46189*b^5*(b*x^(2/3)+a*x)^(5/2)/(a^6*x^(2/3))+512/4199*b^4*(b*x^(2/3)+a*x)^(5/2)/(a^5*x^(1/3))+64/323*b^2*x^(1/3)*(b*x^(2/3)+a*x)^(5/2)/a^3-32/133*b*x^(2/3)*(b*x^(2/3)+a*x)^(5/2)/a^2+2/7*x*(b*x^(2/3)+a*x)^(5/2)/a");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:263
  public void test0441() {
    check( //
        "Integrate[x^3/(b*x^(2/3)+a*x)^(3/2), x]", //
        "-6*x^3/(a*Sqrt[b*x^(2/3)+a*x])+32768/2145*b^6*Sqrt[b*x^(2/3)+a*x]/a^8-65536/2145*b^7*Sqrt[b*x^(2/3)+a*x]/(a^9*x^(1/3))-8192/715*b^5*x^(1/3)*Sqrt[b*x^(2/3)+a*x]/a^7+4096/429*b^4*x^(2/3)*Sqrt[b*x^(2/3)+a*x]/a^6-3584/429*b^3*x*Sqrt[b*x^(2/3)+a*x]/a^5+5376/715*b^2*x^(4/3)*Sqrt[b*x^(2/3)+a*x]/a^4-448/65*b*x^(5/3)*Sqrt[b*x^(2/3)+a*x]/a^3+32/5*x^2*Sqrt[b*x^(2/3)+a*x]/a^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:315
  public void test0442() {
    check( //
        "Integrate[x*Sqrt[a*x^2+b*x^3], x]", //
        "16/105*a^2*(a*x^2+b*x^3)^(3/2)/(b^3*x^3)-8/35*a*(a*x^2+b*x^3)^(3/2)/(b^2*x^2)+2/7*(a*x^2+b*x^3)^(3/2)/(b*x)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:333
  public void test0443() {
    check( //
        "Integrate[(a*x^2+b*x^3)^(3/2)/x^9, x]", //
        "-1/5*(a*x^2+b*x^3)^(3/2)/x^8+3/128*b^5*ArcTanh[x*Sqrt[a]/Sqrt[a*x^2+b*x^3]]/a^(7/2)-3/40*b*Sqrt[a*x^2+b*x^3]/x^5-1/80*b^2*Sqrt[a*x^2+b*x^3]/(a*x^4)+1/64*b^3*Sqrt[a*x^2+b*x^3]/(a^2*x^3)-3/128*b^4*Sqrt[a*x^2+b*x^3]/(a^3*x^2)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:362
  public void test0444() {
    check( //
        "Integrate[x^(5/2)/Sqrt[a*x^2+b*x^3], x]", //
        "3/4*a^2*ArcTanh[x^(3/2)*Sqrt[b]/Sqrt[a*x^2+b*x^3]]/b^(5/2)-3/4*a*Sqrt[a*x^2+b*x^3]/(b^2*Sqrt[x])+1/2*Sqrt[x]*Sqrt[a*x^2+b*x^3]/b");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:396
  public void test0445() {
    check( //
        "Integrate[x^5/Sqrt[a*x^2+b*x^5], x]", //
        "-8/7*a*x*(a+b*x^3)/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))*Sqrt[a*x^2+b*x^5])+2/7*x*Sqrt[a*x^2+b*x^5]/b-8/7*a^(4/3)*x*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(5/3)*Sqrt[a*x^2+b*x^5]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+4/7*3^(1/4)*a^(4/3)*x*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(5/3)*Sqrt[a*x^2+b*x^5]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:428
  public void test0446() {
    check( //
        "Integrate[x^4/Sqrt[a*x^3+b*x^4], x]", //
        "-5/8*a^3*ArcTanh[x^2*Sqrt[b]/Sqrt[a*x^3+b*x^4]]/b^(7/2)-5/12*a*Sqrt[a*x^3+b*x^4]/b^2+5/8*a^2*Sqrt[a*x^3+b*x^4]/(b^3*x)+1/3*x*Sqrt[a*x^3+b*x^4]/b");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:504
  public void test0447() {
    check( //
        "Integrate[Sqrt[a*x^3+b*x^n]/(c*x)^(5/2), x]", //
        "2*ArcTanh[x^(3/2)*Sqrt[a]/Sqrt[a*x^3+b*x^n]]*Sqrt[a]*Sqrt[c*x]/(c^3*(3-n)*Sqrt[x])-2*Sqrt[a*x^3+b*x^n]/(c*(3-n)*(c*x)^(3/2))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:523
  public void test0448() {
    check( //
        "Integrate[Sqrt[(a+b*x^n)/x^2], x]", //
        "-2*ArcTanh[Sqrt[a]/(x*Sqrt[a/x^2+b*x^(-2+n)])]*Sqrt[a]/n+2*x*Sqrt[a/x^2+b*x^(-2+n)]/n");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:13
  public void test0449() {
    check( //
        "Integrate[x*(A+B*x^2)*(b*x^2+c*x^4), x]", //
        "1/4*A*b*x^4+1/6*(b*B+A*c)*x^6+1/8*B*c*x^8");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:32
  public void test0450() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/x^9, x]", //
        "-1/4*A*b^2/x^4-1/2*b*(b*B+2*A*c)/x^2+1/2*B*c^2*x^2+c*(2*b*B+A*c)*Log[x]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:50
  public void test0451() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^16, x]", //
        "-1/9*A*b^3/x^9-1/7*b^2*(b*B+3*A*c)/x^7-3/5*b*c*(b*B+A*c)/x^5-1/3*c^2*(3*b*B+A*c)/x^3-B*c^3/x");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:71
  public void test0452() {
    check( //
        "Integrate[x^12*(A+B*x^2)/(b*x^2+c*x^4)^2, x]", //
        "-b^2*(4*b*B-3*A*c)*x/c^5+1/3*b*(3*b*B-2*A*c)*x^3/c^4-1/5*(2*b*B-A*c)*x^5/c^3+1/7*B*x^7/c^2-1/2*b^3*(b*B-A*c)*x/(c^5*(b+c*x^2))+1/2*b^(5/2)*(9*b*B-7*A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/c^(11/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:90
  public void test0453() {
    check( //
        "Integrate[x^10*(A+B*x^2)/(b*x^2+c*x^4)^3, x]", //
        "B*x/c^3-1/4*b*(b*B-A*c)*x/(c^3*(b+c*x^2)^2)+1/8*(9*b*B-5*A*c)*x/(c^3*(b+c*x^2))-3/8*(5*b*B-A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/(c^(7/2)*Sqrt[b])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:112
  public void test0454() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[b*x^2+c*x^4]/x^5, x]", //
        "-1/3*A*(b*x^2+c*x^4)^(3/2)/(b*x^6)+B*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]*Sqrt[c]-B*Sqrt[b*x^2+c*x^4]/x^2");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:131
  public void test0455() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^(3/2)/x^11, x]", //
        "-1/7*A*(b*x^2+c*x^4)^(5/2)/(b*x^12)-1/35*(7*b*B-2*A*c)*(b*x^2+c*x^4)^(5/2)/(b^2*x^10)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:152
  public void test0456() {
    check( //
        "Integrate[x*(A+B*x^2)/Sqrt[b*x^2+c*x^4], x]", //
        "-1/2*(b*B-2*A*c)*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]/c^(3/2)+1/2*B*Sqrt[b*x^2+c*x^4]/c");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:170
  public void test0457() {
    check( //
        "Integrate[(A+B*x^2)/(x^3*(b*x^2+c*x^4)^(3/2)), x]", //
        "-1/5*A/(b*x^4*Sqrt[b*x^2+c*x^4])+1/15*(-5*b*B+6*A*c)/(b^2*x^2*Sqrt[b*x^2+c*x^4])+4/15*c*(5*b*B-6*A*c)*(b+2*c*x^2)/(b^4*Sqrt[b*x^2+c*x^4])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:16
  public void test0458() {
    check( //
        "Integrate[(3*I*x+4*x^2)^(1/2), x]", //
        "9/64*I*ArcSin[1-8/3*I*x]+1/16*(3*I+8*x)*Sqrt[3*I*x+4*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:37
  public void test0459() {
    check( //
        "Integrate[1/(3*x-4*x^2)^(7/2), x]", //
        "-2/45*(3-8*x)/(3*x-4*x^2)^(5/2)-128/1215*(3-8*x)/(3*x-4*x^2)^(3/2)-4096/10935*(3-8*x)/Sqrt[3*x-4*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:59
  public void test0460() {
    check( //
        "Integrate[(b*x+c*x^2)^(1/4), x]", //
        "1/3*(b+2*c*x)*(b*x+c*x^2)^(1/4)/c-1/3*b^3*(-c*(b*x+c*x^2)/b^2)^(3/4)*EllipticF[1/2*ArcSin[1+2*c*x/b],2]/(c^2*(b*x+c*x^2)^(3/4)*Sqrt[2])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:87
  public void test0461() {
    check( //
        "Integrate[1/(a+c*x^2)^(7/2), x]", //
        "1/5*x/(a*(a+c*x^2)^(5/2))+4/15*x/(a^2*(a+c*x^2)^(3/2))+8/15*x/(a^3*Sqrt[a+c*x^2])");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:137
  public void test0462() {
    check( //
        "Integrate[1/(1+x^2+2*x*Cos[1/7*Pi]), x]", //
        "ArcTan[Cot[1/7*Pi]+x*Csc[1/7*Pi]]*Csc[1/7*Pi]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:19
  public void test0463() {
    check( //
        "Integrate[(b*x+c*x^2)^(1/2)/x^4, x]", //
        "-2/5*(b*x+c*x^2)^(3/2)/(b*x^4)+4/15*c*(b*x+c*x^2)^(3/2)/(b^2*x^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:37
  public void test0464() {
    check( //
        "Integrate[(a*x+b*x^2)^(5/2), x]", //
        "-5/192*a^2*(a+2*b*x)*(a*x+b*x^2)^(3/2)/b^2+1/12*(a+2*b*x)*(a*x+b*x^2)^(5/2)/b-5/512*a^6*ArcTanh[x*Sqrt[b]/Sqrt[a*x+b*x^2]]/b^(7/2)+5/512*a^4*(a+2*b*x)*Sqrt[a*x+b*x^2]/b^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:58
  public void test0465() {
    check( //
        "Integrate[x/(b*x+c*x^2)^(1/2), x]", //
        "-b*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/c^(3/2)+Sqrt[b*x+c*x^2]/c");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:99
  public void test0466() {
    check( //
        "Integrate[x^(7/2)*(b*x+c*x^2)^(3/2), x]", //
        "-512/45045*b^5*(b*x+c*x^2)^(5/2)/(c^6*x^(5/2))+256/9009*b^4*(b*x+c*x^2)^(5/2)/(c^5*x^(3/2))-4/39*b*x^(3/2)*(b*x+c*x^2)^(5/2)/c^2+2/15*x^(5/2)*(b*x+c*x^2)^(5/2)/c-64/1287*b^3*(b*x+c*x^2)^(5/2)/(c^4*Sqrt[x])+32/429*b^2*(b*x+c*x^2)^(5/2)*Sqrt[x]/c^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:120
  public void test0467() {
    check( //
        "Integrate[1/(x^(7/2)*(b*x+c*x^2)^(1/2)), x]", //
        "5/8*c^3*ArcTanh[Sqrt[b*x+c*x^2]/(Sqrt[b]*Sqrt[x])]/b^(7/2)-1/3*Sqrt[b*x+c*x^2]/(b*x^(7/2))+5/12*c*Sqrt[b*x+c*x^2]/(b^2*x^(5/2))-5/8*c^2*Sqrt[b*x+c*x^2]/(b^3*x^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:173
  public void test0468() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x+b^2*x^2]/x, x]", //
        "b*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)+a*Log[x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:192
  public void test0469() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(3/2)/x^8, x]", //
        "-1/7*a^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^7*(a+b*x))-1/2*a^2*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^6*(a+b*x))-3/5*a*b^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^5*(a+b*x))-1/4*b^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^4*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:211
  public void test0470() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(5/2)/x^12, x]", //
        "-1/11*a^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^11*(a+b*x))-1/2*a^4*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^10*(a+b*x))-10/9*a^3*b^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^9*(a+b*x))-5/4*a^2*b^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^8*(a+b*x))-5/7*a*b^4*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^7*(a+b*x))-1/6*b^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^6*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:232
  public void test0471() {
    check( //
        "Integrate[x^5/(a^2+2*a*b*x+b^2*x^2)^(5/2), x]", //
        "-10*a^2/(b^6*Sqrt[a^2+2*a*b*x+b^2*x^2])+1/4*a^5/(b^6*(a+b*x)^3*Sqrt[a^2+2*a*b*x+b^2*x^2])-5/3*a^4/(b^6*(a+b*x)^2*Sqrt[a^2+2*a*b*x+b^2*x^2])+5*a^3/(b^6*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])+x*(a+b*x)/(b^5*Sqrt[a^2+2*a*b*x+b^2*x^2])-5*a*(a+b*x)*Log[a+b*x]/(b^6*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:943
  public void test0472() {
    check( //
        "Integrate[(d^2-e^2*x^2)^(7/2)/(d+e*x)^12, x]", //
        "-1/15*(d^2-e^2*x^2)^(9/2)/(d*e*(d+e*x)^12)-1/65*(d^2-e^2*x^2)^(9/2)/(d^2*e*(d+e*x)^11)-2/715*(d^2-e^2*x^2)^(9/2)/(d^3*e*(d+e*x)^10)-2/6435*(d^2-e^2*x^2)^(9/2)/(d^4*e*(d+e*x)^9)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:965
  public void test0473() {
    check( //
        "Integrate[1/((d+e*x)^4*Sqrt[d^2-e^2*x^2]), x]", //
        "-1/7*Sqrt[d^2-e^2*x^2]/(d*e*(d+e*x)^4)-3/35*Sqrt[d^2-e^2*x^2]/(d^2*e*(d+e*x)^3)-2/35*Sqrt[d^2-e^2*x^2]/(d^3*e*(d+e*x)^2)-2/35*Sqrt[d^2-e^2*x^2]/(d^4*e*(d+e*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:984
  public void test0474() {
    check( //
        "Integrate[(d+e*x)^2/(d^2-e^2*x^2)^(7/2), x]", //
        "2/5*(d+e*x)/(e*(d^2-e^2*x^2)^(5/2))+1/5*x/(d^2*(d^2-e^2*x^2)^(3/2))+2/5*x/(d^4*Sqrt[d^2-e^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1006
  public void test0475() {
    check( //
        "Integrate[(d+e*x)^(1/2)*(c*d^2-c*e^2*x^2)^(3/2), x]", //
        "-64/315*d^2*(c*d^2-c*e^2*x^2)^(5/2)/(c*e*(d+e*x)^(5/2))-16/63*d*(c*d^2-c*e^2*x^2)^(5/2)/(c*e*(d+e*x)^(3/2))-2/9*(c*d^2-c*e^2*x^2)^(5/2)/(c*e*Sqrt[d+e*x])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1027
  public void test0476() {
    check( //
        "Integrate[(d+e*x)^(1/2)/(c*d^2-c*e^2*x^2)^(3/2), x]", //
        "-ArcTanh[Sqrt[c*d^2-c*e^2*x^2]/(Sqrt[2]*Sqrt[c]*Sqrt[d]*Sqrt[d+e*x])]/(c^(3/2)*d^(3/2)*e*Sqrt[2])+Sqrt[d+e*x]/(c*d*e*Sqrt[c*d^2-c*e^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1072
  public void test0477() {
    check( //
        "Integrate[1/(Sqrt[1+x]*Sqrt[1-x^2]), x]", //
        "-ArcTanh[Sqrt[1-x]/Sqrt[2]]*Sqrt[2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1099
  public void test0478() {
    check( //
        "Integrate[(a+b*x)^m*(a^2-b^2*x^2)^2, x]", //
        "4*a^2*(a+b*x)^(3+m)/(b*(3+m))-4*a*(a+b*x)^(4+m)/(b*(4+m))+(a+b*x)^(5+m)/(b*(5+m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1155
  public void test0479() {
    check( //
        "Integrate[(c*d^2+2*c*d*e*x+c*e^2*x^2)^2/(d+e*x)^5, x]", //
        "c^2*Log[d+e*x]/e");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1175
  public void test0480() {
    check( //
        "Integrate[(d+e*x)^2/(c*d^2+2*c*d*e*x+c*e^2*x^2)^2, x]", //
        "(-1)/(c^2*e*(d+e*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1343
  public void test0481() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)*(a+b*x+c*x^2)), x]", //
        "-2*Log[b+2*c*x]/((b^2-4*a*c)*d)+Log[a+b*x+c*x^2]/((b^2-4*a*c)*d)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1361
  public void test0482() {
    check( //
        "Integrate[(b*d+2*c*d*x)^7/(a+b*x+c*x^2)^3, x]", //
        "48*c^2*d^7*(b+2*c*x)^2-1/2*d^7*(b+2*c*x)^6/(a+b*x+c*x^2)^2-6*c*d^7*(b+2*c*x)^4/(a+b*x+c*x^2)+48*c^2*(b^2-4*a*c)*d^7*Log[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1384
  public void test0483() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(1/2)/(b*d+2*c*d*x)^5, x]", //
        "1/32*ArcTan[2*Sqrt[c]*Sqrt[a+b*x+c*x^2]/Sqrt[b^2-4*a*c]]/(c^(3/2)*(b^2-4*a*c)^(3/2)*d^5)-1/8*Sqrt[a+b*x+c*x^2]/(c*d^5*(b+2*c*x)^4)+1/16*Sqrt[a+b*x+c*x^2]/(c*(b^2-4*a*c)*d^5*(b+2*c*x)^2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1403
  public void test0484() {
    check( //
        "Integrate[(b*d+2*c*d*x)^4*(a+b*x+c*x^2)^(5/2), x]", //
        "-1/128*(b^2-4*a*c)*d^4*(b+2*c*x)^5*(a+b*x+c*x^2)^(3/2)/c^2+1/20*d^4*(b+2*c*x)^5*(a+b*x+c*x^2)^(5/2)/c-3/16384*(b^2-4*a*c)^5*d^4*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/c^(7/2)-3/8192*(b^2-4*a*c)^4*d^4*(b+2*c*x)*Sqrt[a+b*x+c*x^2]/c^3-1/4096*(b^2-4*a*c)^3*d^4*(b+2*c*x)^3*Sqrt[a+b*x+c*x^2]/c^3+1/1024*(b^2-4*a*c)^2*d^4*(b+2*c*x)^5*Sqrt[a+b*x+c*x^2]/c^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1423
  public void test0485() {
    check( //
        "Integrate[(b*d+2*c*d*x)^2/(a+b*x+c*x^2)^(1/2), x]", //
        "1/2*(b^2-4*a*c)*d^2*ArcTanh[1/2*(b+2*c*x)/(Sqrt[c]*Sqrt[a+b*x+c*x^2])]/Sqrt[c]+d^2*(b+2*c*x)*Sqrt[a+b*x+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1527
  public void test0486() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(1/2)/(b*d+2*c*d*x)^(5/2), x]", //
        "-1/3*Sqrt[a+b*x+c*x^2]/(c*d*(b*d+2*c*d*x)^(3/2))+1/3*(b^2-4*a*c)^(1/4)*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^2*d^(5/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1545
  public void test0487() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^(11/2), x]", //
        "-1/9*(a+b*x+c*x^2)^(3/2)/(c*d*(b*d+2*c*d*x)^(9/2))-1/30*Sqrt[a+b*x+c*x^2]/(c^2*d^3*(b*d+2*c*d*x)^(5/2))+1/15*Sqrt[a+b*x+c*x^2]/(c^2*(b^2-4*a*c)*d^5*Sqrt[b*d+2*c*d*x])-1/30*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^3*(b^2-4*a*c)^(1/4)*d^(11/2)*Sqrt[a+b*x+c*x^2])+1/30*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c^3*(b^2-4*a*c)^(1/4)*d^(11/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1566
  public void test0488() {
    check( //
        "Integrate[1/((b*d+2*c*d*x)^(9/2)*(a+b*x+c*x^2)^(1/2)), x]", //
        "4/7*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)*d*(b*d+2*c*d*x)^(7/2))+20/21*Sqrt[a+b*x+c*x^2]/((b^2-4*a*c)^2*d^3*(b*d+2*c*d*x)^(3/2))+10/21*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/(c*(b^2-4*a*c)^(7/4)*d^(9/2)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1585
  public void test0489() {
    check( //
        "Integrate[(b*d+2*c*d*x)^(1/2)/(a+b*x+c*x^2)^(3/2), x]", //
        "-2*(b*d+2*c*d*x)^(3/2)/((b^2-4*a*c)*d*Sqrt[a+b*x+c*x^2])+4*EllipticE[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/((b^2-4*a*c)^(1/4)*Sqrt[a+b*x+c*x^2])-4*EllipticF[ArcSin[Sqrt[b*d+2*c*d*x]/((b^2-4*a*c)^(1/4)*Sqrt[d])],-1]*Sqrt[d]*Sqrt[-c*(a+b*x+c*x^2)/(b^2-4*a*c)]/((b^2-4*a*c)^(1/4)*Sqrt[a+b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1603
  public void test0490() {
    check( //
        "Integrate[1/((c*e+d*e*x)^(5/2)*Sqrt[1-c^2-2*c*d*x-d^2*x^2]), x]", //
        "2/3*EllipticF[ArcSin[Sqrt[c*e+d*e*x]/Sqrt[e]],-1]/(d*e^(5/2))-2/3*Sqrt[1-c^2-2*c*d*x-d^2*x^2]/(d*e*(c*e+d*e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1674
  public void test0491() {
    check( //
        "Integrate[(d+e*x)^6*(a^2+2*a*b*x+b^2*x^2)^2, x]", //
        "1/7*(b*d-a*e)^4*(d+e*x)^7/e^5-1/2*b*(b*d-a*e)^3*(d+e*x)^8/e^5+2/3*b^2*(b*d-a*e)^2*(d+e*x)^9/e^5-2/5*b^3*(b*d-a*e)*(d+e*x)^10/e^5+1/11*b^4*(d+e*x)^11/e^5");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1711
  public void test0492() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^3/(d+e*x)^11, x]", //
        "1/10*(a+b*x)^7/((b*d-a*e)*(d+e*x)^10)+1/30*b*(a+b*x)^7/((b*d-a*e)^2*(d+e*x)^9)+1/120*b^2*(a+b*x)^7/((b*d-a*e)^3*(d+e*x)^8)+1/840*b^3*(a+b*x)^7/((b*d-a*e)^4*(d+e*x)^7)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1750
  public void test0493() {
    check( //
        "Integrate[(d+e*x)*(9+12*x+4*x^2)^3, x]", //
        "1/28*(2*d-3*e)*(3+2*x)^7+1/32*e*(3+2*x)^8");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1773
  public void test0494() {
    check( //
        "Integrate[(d+e*x)^3*(a^2+2*a*b*x+b^2*x^2)^(3/2), x]", //
        "1/4*(b*d-a*e)^3*(a+b*x)^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^4+3/5*e*(b*d-a*e)^2*(a+b*x)^4*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^4+1/2*e^2*(b*d-a*e)*(a+b*x)^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^4+1/7*e^3*(a+b*x)^6*Sqrt[a^2+2*a*b*x+b^2*x^2]/b^4");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1794
  public void test0495() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(5/2)/(d+e*x)^3, x]", //
        "10*b^3*(b*d-a*e)^2*x*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^5*(a+b*x))+1/2*(b*d-a*e)^5*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x)*(d+e*x)^2)-5*b*(b*d-a*e)^4*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x)*(d+e*x))-5/2*b^4*(b*d-a*e)*(d+e*x)^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x))+1/3*b^5*(d+e*x)^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x))-10*b^2*(b*d-a*e)^3*Log[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(e^6*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:1938
  public void test0496() {
    check( //
        "Integrate[1/(Sqrt[d+e*x]*Sqrt[a^2+2*a*b*x+b^2*x^2]), x]", //
        "-2*(a+b*x)*ArcTanh[Sqrt[b]*Sqrt[d+e*x]/Sqrt[b*d-a*e]]/(Sqrt[b]*Sqrt[b*d-a*e]*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2000
  public void test0497() {
    check( //
        "Integrate[(a+b*x)^3*(a*c+(b*c+a*d)*x+b*d*x^2), x]", //
        "1/5*(b*c-a*d)*(a+b*x)^5/b^2+1/6*d*(a+b*x)^6/b^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2017
  public void test0498() {
    check( //
        "Integrate[(a*c+(b*c+a*d)*x+b*d*x^2)^2/(a+b*x)^4, x]", //
        "d^2*x/b^2-(b*c-a*d)^2/(b^3*(a+b*x))+2*d*(b*c-a*d)*Log[a+b*x]/b^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2034
  public void test0499() {
    check( //
        "Integrate[(a*c+(b*c+a*d)*x+b*d*x^2)^3/(a+b*x)^7, x]", //
        "-1/3*(b*c-a*d)^3/(b^4*(a+b*x)^3)-3/2*d*(b*c-a*d)^2/(b^4*(a+b*x)^2)-3*d^2*(b*c-a*d)/(b^4*(a+b*x))+d^3*Log[a+b*x]/b^4");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2053
  public void test0500() {
    check( //
        "Integrate[(a+b*x)^6/(a*c+(b*c+a*d)*x+b*d*x^2)^2, x]", //
        "6*b^2*(b*c-a*d)^2*x/d^4-(b*c-a*d)^4/(d^5*(c+d*x))-2*b^3*(b*c-a*d)*(c+d*x)^2/d^5+1/3*b^4*(c+d*x)^3/d^5-4*b*(b*c-a*d)^3*Log[c+d*x]/d^5");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2075
  public void test0501() {
    check( //
        "Integrate[(d+e*x)^4*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x]", //
        "1/6*(a-c*d^2/e^2)*(d+e*x)^6+1/7*c*d*(d+e*x)^7/e^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2092
  public void test0502() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^2/(d+e*x)^4, x]", //
        "c^2*d^2*x/e^2-(c*d^2-a*e^2)^2/(e^3*(d+e*x))-2*c*d*(c*d^2-a*e^2)*Log[d+e*x]/e^3");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2167
  public void test0503() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2)/(d+e*x)^3, x]", //
        "2/3*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/((c*d^2-a*e^2)*(d+e*x)^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2203
  public void test0504() {
    check( //
        "Integrate[(d+e*x)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2), x]", //
        "1/2*(c*d^2-a*e^2)*ArcTanh[1/2*(c*d^2+a*e^2+2*c*d*e*x)/(Sqrt[c]*Sqrt[d]*Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])]/(c^(3/2)*d^(3/2)*Sqrt[e])+Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(c*d)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2313
  public void test0505() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1/2)/(d+e*x)^(7/2), x]", //
        "1/4*c^2*d^2*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]/(e^(3/2)*(c*d^2-a*e^2)^(3/2))-1/2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e*(d+e*x)^(5/2))+1/4*c*d*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e*(c*d^2-a*e^2)*(d+e*x)^(3/2))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2331
  public void test0506() {
    check( //
        "Integrate[(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^(9/2), x]", //
        "5/3*c*d*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(e^2*(d+e*x)^(3/2))-(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(e*(d+e*x)^(7/2))+5*c*d*(c*d^2-a*e^2)^(3/2)*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]/e^(7/2)+5*c*d*(a-c*d^2/e^2)*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(e*Sqrt[d+e*x])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2350
  public void test0507() {
    check( //
        "Integrate[1/((d+e*x)^(1/2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)), x]", //
        "-3*c*d*ArcTan[Sqrt[e]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2]/(Sqrt[c*d^2-a*e^2]*Sqrt[d+e*x])]*Sqrt[e]/(c*d^2-a*e^2)^(5/2)+1/((c*d^2-a*e^2)*Sqrt[d+e*x]*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])-3*c*d*Sqrt[d+e*x]/((c*d^2-a*e^2)^2*Sqrt[a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2371
  public void test0508() {
    check( //
        "Integrate[(d+e*x)^m*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x]", //
        "-(c*d^2-a*e^2)*(d+e*x)^(2+m)/(e^2*(2+m))+c*d*(d+e*x)^(3+m)/(e^2*(3+m))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2481
  public void test0509() {
    check( //
        "Integrate[1/(a+b*x+c*x^2), x]", //
        "-2*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/Sqrt[b^2-4*a*c]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2498
  public void test0510() {
    check( //
        "Integrate[(d+e*x)^3/(a+b*x+c*x^2)^3, x]", //
        "-1/2*(b+2*c*x)*(d+e*x)^3/((b^2-4*a*c)*(a+b*x+c*x^2)^2)+3/2*(2*c*d-b*e)*(d+e*x)*(b*d-2*a*e+(2*c*d-b*e)*x)/((b^2-4*a*c)^2*(a+b*x+c*x^2))-6*(2*c*d-b*e)*(c*d^2-b*d*e+a*e^2)*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]/(b^2-4*a*c)^(5/2)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2550
  public void test0511() {
    check( //
        "Integrate[x/(5+2*x+x^2), x]", //
        "-1/2*ArcTan[1/2*(1+x)]+1/2*Log[5+2*x+x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2722
  public void test0512() {
    check( //
        "Integrate[x/Sqrt[2+4*x+3*x^2], x]", //
        "-2/3*ArcSinh[(2+3*x)/Sqrt[2]]/Sqrt[3]+1/3*Sqrt[2+4*x+3*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2806
  public void test0513() {
    check( //
        "Integrate[(a+b*x+c*x^2)^(4/3), x]", //
        "-3/55*(b^2-4*a*c)*(b+2*c*x)*(a+b*x+c*x^2)^(1/3)/c^2+3/22*(b+2*c*x)*(a+b*x+c*x^2)^(4/3)/c+1/55*2^(1/3)*3^(3/4)*(b^2-4*a*c)^2*((b^2-4*a*c)^(1/3)+2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3))*EllipticF[ArcSin[(2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3)+(b^2-4*a*c)^(1/3)*(1-Sqrt[3]))/(2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3)+(b^2-4*a*c)^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[((b^2-4*a*c)^(2/3)-2^(2/3)*c^(1/3)*(b^2-4*a*c)^(1/3)*(a+b*x+c*x^2)^(1/3)+2*2^(1/3)*c^(2/3)*(a+b*x+c*x^2)^(2/3))/(2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3)+(b^2-4*a*c)^(1/3)*(1+Sqrt[3]))^2]/(c^(7/3)*(b+2*c*x)*Sqrt[(b^2-4*a*c)^(1/3)*((b^2-4*a*c)^(1/3)+2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3))/(2^(2/3)*c^(1/3)*(a+b*x+c*x^2)^(1/3)+(b^2-4*a*c)^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:48
  public void test0514() {
    check( //
        "Integrate[(A+B*x)*(b*x+c*x^2)^3/x^6, x]", //
        "-1/2*A*b^3/x^2-b^2*(b*B+3*A*c)/x+c^2*(3*b*B+A*c)*x+1/2*B*c^3*x^2+3*b*c*(b*B+A*c)*Log[x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:89
  public void test0515() {
    check( //
        "Integrate[(A+B*x)*Sqrt[b*x+c*x^2]/x^2, x]", //
        "-2*A*(b*x+c*x^2)^(3/2)/(b*x^2)+(b*B+2*A*c)*ArcTanh[x*Sqrt[c]/Sqrt[b*x+c*x^2]]/Sqrt[c]+(b*B+2*A*c)*Sqrt[b*x+c*x^2]/b");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:106
  public void test0516() {
    check( //
        "Integrate[(A+B*x)*(b*x+c*x^2)^(3/2)/x^7, x]", //
        "-2/9*A*(b*x+c*x^2)^(5/2)/(b*x^7)-2/63*(9*b*B-4*A*c)*(b*x+c*x^2)^(5/2)/(b^2*x^6)+4/315*c*(9*b*B-4*A*c)*(b*x+c*x^2)^(5/2)/(b^3*x^5)");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:123
  public void test0517() {
    check( //
        "Integrate[(A+B*x)*(b*x+c*x^2)^(5/2)/x^10, x]", //
        "-2/13*A*(b*x+c*x^2)^(7/2)/(b*x^10)-2/143*(13*b*B-6*A*c)*(b*x+c*x^2)^(7/2)/(b^2*x^9)+8/1287*c*(13*b*B-6*A*c)*(b*x+c*x^2)^(7/2)/(b^3*x^8)-16/9009*c^2*(13*b*B-6*A*c)*(b*x+c*x^2)^(7/2)/(b^4*x^7)");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:200
  public void test0518() {
    check( //
        "Integrate[(A+B*x)/(x^(7/2)*(b*x+c*x^2)), x]", //
        "-2/7*A/(b*x^(7/2))-2/5*(b*B-A*c)/(b^2*x^(5/2))+2/3*c*(b*B-A*c)/(b^3*x^(3/2))-2*c^(5/2)*(b*B-A*c)*ArcTan[Sqrt[c]*Sqrt[x]/Sqrt[b]]/b^(9/2)-2*c^2*(b*B-A*c)/(b^4*Sqrt[x])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:163
  public void test0519() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x, x]", //
        "a*b*c*x^2+1/4*b^2*c*x^4+1/6*d*(a+b*x^2)^3/b+a^2*c*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:693
  public void test0520() {
    check( //
        "Integrate[(a+b*x^2)^2*Sqrt[c+d*x^2]/x^12, x]", //
        "-1/11*a^2*(c+d*x^2)^(3/2)/(c*x^11)-2/99*a*(11*b*c-4*a*d)*(c+d*x^2)^(3/2)/(c^2*x^9)-1/231*(33*b^2*c^2-4*a*d*(11*b*c-4*a*d))*(c+d*x^2)^(3/2)/(c^3*x^7)+4/1155*d*(33*b^2*c^2-4*a*d*(11*b*c-4*a*d))*(c+d*x^2)^(3/2)/(c^4*x^5)-8/3465*d^2*(33*b^2*c^2-4*a*d*(11*b*c-4*a*d))*(c+d*x^2)^(3/2)/(c^5*x^3)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1322
  public void test0521() {
    check( //
        "Integrate[(c+d*x^2)/((e*x)^(5/2)*(a+b*x^2)^(9/4)), x]", //
        "-2/3*c/(a*e*(e*x)^(3/2)*(a+b*x^2)^(5/4))-2/15*(8*b*c-3*a*d)*Sqrt[e*x]/(a^2*e^3*(a+b*x^2)^(5/4))-8/15*(8*b*c-3*a*d)*Sqrt[e*x]/(a^3*e^3*(a+b*x^2)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:359
  public void test0522() {
    check( //
        "Integrate[(a+b*x^3)^8/x^37, x]", //
        "-1/36*(a+b*x^3)^9/(a*x^36)+1/132*b*(a+b*x^3)^9/(a^2*x^33)-1/660*b^2*(a+b*x^3)^9/(a^3*x^30)+1/5940*b^3*(a+b*x^3)^9/(a^4*x^27)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:760
  public void test0523() {
    check( //
        "Integrate[1/(a+c*x^4)^2, x]", //
        "1/4*x/(a*(a+c*x^4))-3/8*ArcTan[1-c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(7/4)*c^(1/4)*Sqrt[2])+3/8*ArcTan[1+c^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(7/4)*c^(1/4)*Sqrt[2])-3/16*Log[-a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(7/4)*c^(1/4)*Sqrt[2])+3/16*Log[a^(1/4)*c^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[c]]/(a^(7/4)*c^(1/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1350
  public void test0524() {
    check( //
        "Integrate[x^2/(a-b*x^4)^(1/4), x]", //
        "-1/2*(a-b*x^4)^(3/4)/(b*x)+1/2*(1-a/(b*x^4))^(1/4)*x*EllipticE[1/2*ArcCsc[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a-b*x^4)^(1/4)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1659
  public void test0525() {
    check( //
        "Integrate[x^6/(a+b*x^8), x]", //
        "1/4*ArcTan[b^(1/8)*x/(-a)^(1/8)]/((-a)^(1/8)*b^(7/8))-1/4*ArcTanh[b^(1/8)*x/(-a)^(1/8)]/((-a)^(1/8)*b^(7/8))-1/4*ArcTan[1-b^(1/8)*x*Sqrt[2]/(-a)^(1/8)]/((-a)^(1/8)*b^(7/8)*Sqrt[2])+1/4*ArcTan[1+b^(1/8)*x*Sqrt[2]/(-a)^(1/8)]/((-a)^(1/8)*b^(7/8)*Sqrt[2])+1/8*Log[(-a)^(1/4)+b^(1/4)*x^2-(-a)^(1/8)*b^(1/8)*x*Sqrt[2]]/((-a)^(1/8)*b^(7/8)*Sqrt[2])-1/8*Log[(-a)^(1/4)+b^(1/4)*x^2+(-a)^(1/8)*b^(1/8)*x*Sqrt[2]]/((-a)^(1/8)*b^(7/8)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1854
  public void test0526() {
    check( //
        "Integrate[(a+b/x)^3/x^3, x]", //
        "-1/5*(b+a*x)^4/(b*x^5)+1/20*a*(b+a*x)^4/(b^2*x^4)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2118
  public void test0527() {
    check( //
        "Integrate[(a+b/x^2)^2*x^6, x]", //
        "1/3*b^2*x^3+2/5*a*b*x^5+1/7*a^2*x^7");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2292
  public void test0528() {
    check( //
        "Integrate[x/(a+b/x^3), x]", //
        "1/2*x^2/a+1/3*b^(2/3)*Log[b^(1/3)+a^(1/3)*x]/a^(5/3)-1/6*b^(2/3)*Log[b^(2/3)-a^(1/3)*b^(1/3)*x+a^(2/3)*x^2]/a^(5/3)+b^(2/3)*ArcTan[(b^(1/3)-2*a^(1/3)*x)/(b^(1/3)*Sqrt[3])]/(a^(5/3)*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2376
  public void test0529() {
    check( //
        "Integrate[1/((a+b/x^3)^(3/2)*x^2), x]", //
        "(-2/3)/(a*x*Sqrt[a+b/x^3])-2/3*(a^(1/3)+b^(1/3)/x)*EllipticF[ArcSin[(b^(1/3)/x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)+b^(2/3)/x^2-a^(1/3)*b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a*b^(1/3)*Sqrt[a+b/x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)/x)/(b^(1/3)/x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3119
  public void test0530() {
    check( //
        "Integrate[x^(-1+7*n)*(a+b*x^n)^8, x]", //
        "1/9*a^6*(a+b*x^n)^9/(b^7*n)-3/5*a^5*(a+b*x^n)^10/(b^7*n)+15/11*a^4*(a+b*x^n)^11/(b^7*n)-5/3*a^3*(a+b*x^n)^12/(b^7*n)+15/13*a^2*(a+b*x^n)^13/(b^7*n)-3/7*a*(a+b*x^n)^14/(b^7*n)+1/15*(a+b*x^n)^15/(b^7*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3198
  public void test0531() {
    check( //
        "Integrate[x^(-1-3/4*n)/(a+b*x^n), x]", //
        "(-4/3)/(a*n*x^(3/4*n))+b^(3/4)*Log[-a^(1/4)*b^(1/4)*x^(1/4*n)*Sqrt[2]+Sqrt[a]+x^(1/2*n)*Sqrt[b]]/(a^(7/4)*n*Sqrt[2])-b^(3/4)*Log[a^(1/4)*b^(1/4)*x^(1/4*n)*Sqrt[2]+Sqrt[a]+x^(1/2*n)*Sqrt[b]]/(a^(7/4)*n*Sqrt[2])+b^(3/4)*ArcTan[1-b^(1/4)*x^(1/4*n)*Sqrt[2]/a^(1/4)]*Sqrt[2]/(a^(7/4)*n)-b^(3/4)*ArcTan[1+b^(1/4)*x^(1/4*n)*Sqrt[2]/a^(1/4)]*Sqrt[2]/(a^(7/4)*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3321
  public void test0532() {
    check( //
        "Integrate[x^m*(a+b*x^(2+2*m))^(1/2), x]", //
        "1/2*a*ArcTanh[x^(1+m)*Sqrt[b]/Sqrt[a+b*x^(2*(1+m))]]/((1+m)*Sqrt[b])+1/2*x^(1+m)*Sqrt[a+b*x^(2*(1+m))]/(1+m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3350
  public void test0533() {
    check( //
        "Integrate[(c*x)^(-1-3/2*n)/(a+b*x^n), x]", //
        "(-2/3)/(a*c*n*(c*x)^(3/2*n))+2*b*x^n/(a^2*c*n*(c*x)^(3/2*n))-2*b^(3/2)*x^(3/2*n)*ArcTan[Sqrt[a]/(x^(1/2*n)*Sqrt[b])]/(a^(5/2)*c*n*(c*x)^(3/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3405
  public void test0534() {
    check( //
        "Integrate[1/Sqrt[-(2+3*x)^2], x]", //
        "1/3*(2+3*x)*Log[2+3*x]/Sqrt[-(2+3*x)^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3486
  public void test0535() {
    check( //
        "Integrate[1/((c+d*x)^4*(a+b*(c+d*x)^3)), x]", //
        "(-1/3)/(a*d*(c+d*x)^3)-b*Log[c+d*x]/(a^2*d)+1/3*b*Log[a+b*(c+d*x)^3]/(a^2*d)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3509
  public void test0536() {
    check( //
        "Integrate[1/((c*e+d*e*x)*(a+b*(c+d*x)^3)), x]", //
        "Log[c+d*x]/(a*d*e)-1/3*Log[a+b*(c+d*x)^3]/(a*d*e)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3729
  public void test0537() {
    check( //
        "Integrate[(a+b*(c*x^n)^(2/n))^3, x]", //
        "a^3*x+a^2*b*x*(c*x^n)^(2/n)+3/5*a*b^2*x*(c*x^n)^(4/n)+1/7*b^3*x*(c*x^n)^(6/n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3762
  public void test0538() {
    check( //
        "Integrate[Sqrt[a+c/x+b*Sqrt[d/x]], x]", //
        "1/4*(4*a*c-b^2*d)*ArcTanh[1/2*(2*a+b*Sqrt[d/x])/(Sqrt[a]*Sqrt[a+c/x+b*Sqrt[d/x]])]/a^(3/2)+1/2*x*(2*a+b*Sqrt[d/x])*Sqrt[a+c/x+b*Sqrt[d/x]]/a");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:125
  public void test0539() {
    check( //
        "Integrate[(a+b*x^3)^(2/3)/(c+d*x^3)^2, x]", //
        "1/3*x*(a+b*x^3)^(2/3)/(c*(c+d*x^3))+1/9*a*Log[c+d*x^3]/(c^(5/3)*(b*c-a*d)^(1/3))-1/3*a*Log[(b*c-a*d)^(1/3)*x/c^(1/3)-(a+b*x^3)^(1/3)]/(c^(5/3)*(b*c-a*d)^(1/3))+2/3*a*ArcTan[(1+2*(b*c-a*d)^(1/3)*x/(c^(1/3)*(a+b*x^3)^(1/3)))/Sqrt[3]]/(c^(5/3)*(b*c-a*d)^(1/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:256
  public void test0540() {
    check( //
        "Integrate[1/((a+b*x^4)^(1/4)*(c+d*x^4)), x]", //
        "1/2*ArcTan[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*(b*c-a*d)^(1/4))+1/2*ArcTanh[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*(b*c-a*d)^(1/4))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:498
  public void test0541() {
    check( //
        "Integrate[(a+b*x^2)*Sqrt[-c+d*x]*Sqrt[c+d*x]/x^4, x]", //
        "1/3*a*(-c+d*x)^(3/2)*(c+d*x)^(3/2)/(c^2*x^3)+2*b*d*ArcTanh[Sqrt[-c+d*x]/Sqrt[c+d*x]]-b*Sqrt[-c+d*x]*Sqrt[c+d*x]/x");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:45
  public void test0542() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^2, x]", //
        "-a^5*A/x+1/2*a^4*(5*A*b+a*B)*x^2+a^3*b*(2*A*b+a*B)*x^5+5/4*a^2*b^2*(A*b+a*B)*x^8+5/11*a*b^3*(A*b+2*a*B)*x^11+1/14*b^4*(A*b+5*a*B)*x^14+1/17*b^5*B*x^17");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:71
  public void test0543() {
    check( //
        "Integrate[x^4*(A+B*x^3)/(a+b*x^3), x]", //
        "1/2*(A*b-a*B)*x^2/b^2+1/5*B*x^5/b+1/3*a^(2/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/b^(8/3)-1/6*a^(2/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(8/3)+a^(2/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(8/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:486
  public void test0544() {
    check( //
        "Integrate[x^8/((8*c-d*x^3)^2*Sqrt[c+d*x^3]), x]", //
        "-224/81*ArcTanh[1/3*Sqrt[c+d*x^3]/Sqrt[c]]*Sqrt[c]/d^3+2/3*Sqrt[c+d*x^3]/d^3+64/27*c*Sqrt[c+d*x^3]/(d^3*(8*c-d*x^3))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:887
  public void test0545() {
    check( //
        "Integrate[x^2/((a+b*x^4)*(c+d*x^4)), x]", //
        "-1/2*b^(1/4)*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(1/4)*(b*c-a*d)*Sqrt[2])+1/2*b^(1/4)*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(1/4)*(b*c-a*d)*Sqrt[2])+1/2*d^(1/4)*ArcTan[1-d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(1/4)*(b*c-a*d)*Sqrt[2])-1/2*d^(1/4)*ArcTan[1+d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(1/4)*(b*c-a*d)*Sqrt[2])+1/4*b^(1/4)*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(1/4)*(b*c-a*d)*Sqrt[2])-1/4*b^(1/4)*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(1/4)*(b*c-a*d)*Sqrt[2])-1/4*d^(1/4)*Log[-c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(1/4)*(b*c-a*d)*Sqrt[2])+1/4*d^(1/4)*Log[c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(1/4)*(b*c-a*d)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1002
  public void test0546() {
    check( //
        "Integrate[x^8/((a+b*x^6)*Sqrt[c+d*x^6]), x]", //
        "1/3*ArcTanh[x^3*Sqrt[d]/Sqrt[c+d*x^6]]/(b*Sqrt[d])-1/3*ArcTan[x^3*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^6])]*Sqrt[a]/(b*Sqrt[b*c-a*d])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1050
  public void test0547() {
    check( //
        "Integrate[1/(x^5*(a+b*x^8)*Sqrt[c+d*x^8]), x]", //
        "-1/4*b*ArcTan[x^4*Sqrt[b*c-a*d]/(Sqrt[a]*Sqrt[c+d*x^8])]/(a^(3/2)*Sqrt[b*c-a*d])-1/4*Sqrt[c+d*x^8]/(a*c*x^4)");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1120
  public void test0548() {
    check( //
        "Integrate[(a+b/x^2)*(c+d/x^2)^(3/2)*x^4, x]", //
        "1/3*b*(c+d/x^2)^(3/2)*x^3+1/5*a*(c+d/x^2)^(5/2)*x^5/c-b*d^(3/2)*ArcTanh[Sqrt[d]/(x*Sqrt[c+d/x^2])]+b*d*x*Sqrt[c+d/x^2]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:1268
  public void test0549() {
    check( //
        "Integrate[x^(-1+2*n)/((a+b*x^n)^(5/2)*Sqrt[c+d*x^n]), x]", //
        "2/3*a*Sqrt[c+d*x^n]/(b*(b*c-a*d)*n*(a+b*x^n)^(3/2))-2/3*(3*b*c-a*d)*Sqrt[c+d*x^n]/(b*(b*c-a*d)^2*n*Sqrt[a+b*x^n])");
  }

  // 1.1.3.6 (g x)^m (a+b x^n)^p (c+d x^n)^q (e+f x^n)^r.input:29
  public void test0550() {
    check( //
        "Integrate[(e*x)^m*(A+B*x^n)*(c+d*x^n)^3, x]", //
        "c^2*(B*c+3*A*d)*x^(1+n)*(e*x)^m/(1+m+n)+3*c*d*(B*c+A*d)*x^(1+2*n)*(e*x)^m/(1+m+2*n)+d^2*(3*B*c+A*d)*x^(1+3*n)*(e*x)^m/(1+m+3*n)+B*d^3*x^(1+4*n)*(e*x)^m/(1+m+4*n)+A*c^3*(e*x)^(1+m)/(e*(1+m))");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:36
  public void test0551() {
    check( //
        "Integrate[(c-d*x)/(c^3-d^3*x^3), x]", //
        "2*ArcTan[(c+2*d*x)/(c*Sqrt[3])]/(c*d*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:83
  public void test0552() {
    check( //
        "Integrate[(a+b*x^3)^(1/2)*(a*c+a*d*x+b*c*x^3+b*d*x^4), x]", //
        "2/143*(13*c*x+11*d*x^2)*(a+b*x^3)^(3/2)+18/5005*a*(91*c*x+55*d*x^2)*Sqrt[a+b*x^3]+54/91*a^2*d*Sqrt[a+b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-27/91*3^(1/4)*a^(7/3)*d*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+18/5005*3^(3/4)*a^2*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(91*b^(1/3)*c-55*a^(1/3)*d*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:177
  public void test0553() {
    check( //
        "Integrate[(c+d*x)/(a+b*x^4)^4, x]", //
        "1/12*x*(c+d*x)/(a*(a+b*x^4)^3)+1/96*x*(11*c+10*d*x)/(a^2*(a+b*x^4)^2)+1/384*x*(77*c+60*d*x)/(a^3*(a+b*x^4))-77/256*c*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(15/4)*b^(1/4)*Sqrt[2])+77/256*c*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(15/4)*b^(1/4)*Sqrt[2])-77/512*c*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(15/4)*b^(1/4)*Sqrt[2])+77/512*c*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(15/4)*b^(1/4)*Sqrt[2])+5/32*d*ArcTan[x^2*Sqrt[b]/Sqrt[a]]/(a^(7/2)*Sqrt[b])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:347
  public void test0554() {
    check( //
        "Integrate[x^4*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3), x]", //
        "1/2*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x^2/b^4+1/5*(b^2*d-a*b*e+a^2*f)*x^5/b^3+1/8*(b*e-a*f)*x^8/b^2+1/11*f*x^11/b+1/3*a^(2/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(1/3)+b^(1/3)*x]/b^(14/3)-1/6*a^(2/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(14/3)+a^(2/3)*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(14/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:403
  public void test0555() {
    check( //
        "Integrate[x^3*(c+d*x^3+e*x^6+f*x^9)/(a+b*x^3)^3, x]", //
        "(b*e-3*a*f)*x/b^4+1/4*f*x^4/b^3-1/6*(b^3*c-a*b^2*d+a^2*b*e-a^3*f)*x/(b^4*(a+b*x^3)^2)+1/18*(b^3*c-7*a*b^2*d+13*a^2*b*e-19*a^3*f)*x/(a*b^4*(a+b*x^3))+1/27*(b^3*c+2*a*b^2*d-14*a^2*b*e+35*a^3*f)*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(13/3))-1/54*(b^3*c+2*a*b^2*d-14*a^2*b*e+35*a^3*f)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(13/3))-1/9*(b^3*c+2*a*b^2*d-14*a^2*b*e+35*a^3*f)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(13/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:449
  public void test0556() {
    check( //
        "Integrate[(c+d*x+e*x^2)*(a+b*x^3)^3/x, x]", //
        "a^3*d*x+1/2*a^3*e*x^2+a^2*b*c*x^3+3/4*a^2*b*d*x^4+3/5*a^2*b*e*x^5+1/2*a*b^2*c*x^6+3/7*a*b^2*d*x^7+3/8*a*b^2*e*x^8+1/9*b^3*c*x^9+1/10*b^3*d*x^10+1/11*b^3*e*x^11+a^3*c*Log[x]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:489
  public void test0557() {
    check( //
        "Integrate[(2*a-x)*x/(a^3+x^3), x]", //
        "-Log[a+x]-2*ArcTan[(a-2*x)/(a*Sqrt[3])]/Sqrt[3]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:551
  public void test0558() {
    check( //
        "Integrate[x^3*(c+d*x+e*x^2+f*x^3+g*x^4+h*x^5)/(a+b*x^3)^3, x]", //
        "-1/6*x*(b*c-a*f+(b*d-a*g)*x+(b*e-a*h)*x^2)/(b^2*(a+b*x^3)^2)+1/18*x*(b*c-7*a*f+2*(b*d-4*a*g)*x+3*(b*e-3*a*h)*x^2)/(a*b^2*(a+b*x^3))+1/27*(b^(1/3)*(b*c+2*a*f)-a^(1/3)*(b*d+5*a*g))*Log[a^(1/3)+b^(1/3)*x]/(a^(5/3)*b^(8/3))-1/54*(b^(1/3)*(b*c+2*a*f)-a^(1/3)*(b*d+5*a*g))*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(5/3)*b^(8/3))+1/3*h*Log[a+b*x^3]/b^3-1/9*(b^(4/3)*c+a^(1/3)*b*d+2*a*b^(1/3)*f+5*a^(4/3)*g)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(8/3)*Sqrt[3])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:575
  public void test0559() {
    check( //
        "Integrate[x^2*(c+d*x+e*x^2)/(a+b*x^3)^(3/2), x]", //
        "-2/3*(c+d*x+e*x^2)/(b*Sqrt[a+b*x^3])+8/3*e*Sqrt[a+b*x^3]/(b^(5/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))-4*a^(1/3)*e*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(3/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])+4/3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*(b^(1/3)*d-2*a^(1/3)*e*(1-Sqrt[3]))*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(5/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:654
  public void test0560() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*Sqrt[a+b*x^4], x]", //
        "1/6*f*(a+b*x^4)^(3/2)/b+1/4*a*d*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]/Sqrt[b]+1/4*d*x^2*Sqrt[a+b*x^4]+1/15*x*(5*c+3*e*x^2)*Sqrt[a+b*x^4]+2/5*a*e*x*Sqrt[a+b*x^4]/(Sqrt[b]*(Sqrt[a]+x^2*Sqrt[b]))-2/5*a^(5/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])+1/15*a^(3/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(3*e*Sqrt[a]+5*c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(b^(3/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:674
  public void test0561() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)*(a+b*x^4)^(3/2)/x^5, x]", //
        "-1/12*(3*c/x^4+4*d/x^3+6*e/x^2+12*f/x)*(a+b*x^4)^(3/2)-3/4*b*c*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]*Sqrt[a]+3/4*a*e*ArcTanh[x^2*Sqrt[b]/Sqrt[a+b*x^4]]*Sqrt[b]+3/4*b*(c+e*x^2)*Sqrt[a+b*x^4]+2/15*b*x*(5*d+9*f*x^2)*Sqrt[a+b*x^4]+12/5*a*f*x*Sqrt[b]*Sqrt[a+b*x^4]/(Sqrt[a]+x^2*Sqrt[b])-12/5*a^(5/4)*b^(1/4)*f*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]+2/15*a^(3/4)*b^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(9*f*Sqrt[a]+5*d*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/Sqrt[a+b*x^4]");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:694
  public void test0562() {
    check( //
        "Integrate[(c+d*x+e*x^2+f*x^3)/(x^4*Sqrt[a+b*x^4]), x]", //
        "-1/2*f*ArcTanh[Sqrt[a+b*x^4]/Sqrt[a]]/Sqrt[a]-1/3*c*Sqrt[a+b*x^4]/(a*x^3)-1/2*d*Sqrt[a+b*x^4]/(a*x^2)-e*Sqrt[a+b*x^4]/(a*x)+e*x*Sqrt[b]*Sqrt[a+b*x^4]/(a*(Sqrt[a]+x^2*Sqrt[b]))-b^(1/4)*e*EllipticE[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(3/4)*Sqrt[a+b*x^4])-1/6*b^(1/4)*EllipticF[2*ArcTan[b^(1/4)*x/a^(1/4)],1/2]*(-3*e*Sqrt[a]+c*Sqrt[b])*(Sqrt[a]+x^2*Sqrt[b])*Sqrt[(a+b*x^4)/(Sqrt[a]+x^2*Sqrt[b])^2]/(a^(5/4)*Sqrt[a+b*x^4])");
  }

  // 1.1.3.8 P(x) (c x)^m (a+b x^n)^p.input:762
  public void test0563() {
    check( //
        "Integrate[(c+d*x^(-1+n))*(a+b*x^n), x]", //
        "a*c*x+a*d*x^n/n+1/2*b*d*x^(2*n)/n+b*c*x^(1+n)/(1+n)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:21
  public void test0564() {
    check( //
        "Integrate[(a*x+b*x^3)^2/x^2, x]", //
        "a^2*x+2/3*a*b*x^3+1/5*b^2*x^5");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:152
  public void test0565() {
    check( //
        "Integrate[x/(b*x^(1/2)+a*x)^(1/2), x]", //
        "-5/4*b^3*ArcTanh[Sqrt[a]*Sqrt[x]/Sqrt[a*x+b*Sqrt[x]]]/a^(7/2)+5/4*b^2*Sqrt[a*x+b*Sqrt[x]]/a^3+2/3*x*Sqrt[a*x+b*Sqrt[x]]/a-5/6*b*Sqrt[x]*Sqrt[a*x+b*Sqrt[x]]/a^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:176
  public void test0566() {
    check( //
        "Integrate[1/(x^(5/2)*(b*x^(1/2)+a*x)^(1/2)), x]", //
        "-4/7*Sqrt[a*x+b*Sqrt[x]]/(b*x^2)+24/35*a*Sqrt[a*x+b*Sqrt[x]]/(b^2*x^(3/2))-32/35*a^2*Sqrt[a*x+b*Sqrt[x]]/(b^3*x)+64/35*a^3*Sqrt[a*x+b*Sqrt[x]]/(b^4*Sqrt[x])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:244
  public void test0567() {
    check( //
        "Integrate[(b*x^(2/3)+a*x)^(3/2), x]", //
        "2/5*(b*x^(2/3)+a*x)^(5/2)/a-512/15015*b^5*(b*x^(2/3)+a*x)^(5/2)/(a^6*x^(5/3))+256/3003*b^4*(b*x^(2/3)+a*x)^(5/2)/(a^5*x^(4/3))-64/429*b^3*(b*x^(2/3)+a*x)^(5/2)/(a^4*x)+32/143*b^2*(b*x^(2/3)+a*x)^(5/2)/(a^3*x^(2/3))-4/13*b*(b*x^(2/3)+a*x)^(5/2)/(a^2*x^(1/3))");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:264
  public void test0568() {
    check( //
        "Integrate[x^2/(b*x^(2/3)+a*x)^(3/2), x]", //
        "-6*x^2/(a*Sqrt[b*x^(2/3)+a*x])-256/21*b^3*Sqrt[b*x^(2/3)+a*x]/a^5+512/21*b^4*Sqrt[b*x^(2/3)+a*x]/(a^6*x^(1/3))+64/7*b^2*x^(1/3)*Sqrt[b*x^(2/3)+a*x]/a^4-160/21*b*x^(2/3)*Sqrt[b*x^(2/3)+a*x]/a^3+20/3*x*Sqrt[b*x^(2/3)+a*x]/a^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:294
  public void test0569() {
    check( //
        "Integrate[x^3/(a*x^2+b*x^3), x]", //
        "x/b-a*Log[a+b*x]/b^2");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:336
  public void test0570() {
    check( //
        "Integrate[x^4/Sqrt[a*x^2+b*x^3], x]", //
        "16/35*a^2*Sqrt[a*x^2+b*x^3]/b^3-32/35*a^3*Sqrt[a*x^2+b*x^3]/(b^4*x)-12/35*a*x*Sqrt[a*x^2+b*x^3]/b^2+2/7*x^2*Sqrt[a*x^2+b*x^3]/b");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:364
  public void test0571() {
    check( //
        "Integrate[x^(1/2)/Sqrt[a*x^2+b*x^3], x]", //
        "2*ArcTanh[x^(3/2)*Sqrt[b]/Sqrt[a*x^2+b*x^3]]/Sqrt[b]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:397
  public void test0572() {
    check( //
        "Integrate[x^2/Sqrt[a*x^2+b*x^5], x]", //
        "2*x*(a+b*x^3)/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))*Sqrt[a*x^2+b*x^5])+2*a^(1/3)*x*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(2/3)*Sqrt[a*x^2+b*x^5]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-3^(1/4)*a^(1/3)*x*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a*x^2+b*x^5]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:429
  public void test0573() {
    check( //
        "Integrate[x^3/Sqrt[a*x^3+b*x^4], x]", //
        "3/4*a^2*ArcTanh[x^2*Sqrt[b]/Sqrt[a*x^3+b*x^4]]/b^(5/2)+1/2*Sqrt[a*x^3+b*x^4]/b-3/4*a*Sqrt[a*x^3+b*x^4]/(b^2*x)");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:525
  public void test0574() {
    check( //
        "Integrate[Sqrt[(-a+b*x^2)/x^2], x]", //
        "ArcTan[Sqrt[a]/(x*Sqrt[b-a/x^2])]*Sqrt[a]+x*Sqrt[b-a/x^2]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:545
  public void test0575() {
    check( //
        "Integrate[1/((c*x)^(11/2)*(a/x^3+b*x^n)^(3/2)), x]", //
        "-2*ArcTanh[Sqrt[a]/(x^(3/2)*Sqrt[a/x^3+b*x^n])]*Sqrt[x]/(a^(3/2)*c^5*(3+n)*Sqrt[c*x])+2/(a*c^4*(3+n)*(c*x)^(3/2)*Sqrt[a/x^3+b*x^n])");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:575
  public void test0576() {
    check( //
        "Integrate[1/(x^(3/2)+Sqrt[x]), x]", //
        "2*ArcTan[Sqrt[x]]");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:15
  public void test0577() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)/x, x]", //
        "1/2*A*b*x^2+1/4*(b*B+A*c)*x^4+1/6*B*c*x^6");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:33
  public void test0578() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/x^10, x]", //
        "-1/5*A*b^2/x^5-1/3*b*(b*B+2*A*c)/x^3-c*(2*b*B+A*c)/x+B*c^2*x");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:51
  public void test0579() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^3/x^17, x]", //
        "-1/10*A*(b+c*x^2)^4/(b*x^10)-1/40*(5*b*B-A*c)*(b+c*x^2)^4/(b^2*x^8)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:73
  public void test0580() {
    check( //
        "Integrate[x^10*(A+B*x^2)/(b*x^2+c*x^4)^2, x]", //
        "b*(3*b*B-2*A*c)*x/c^4-1/3*(2*b*B-A*c)*x^3/c^3+1/5*B*x^5/c^2+1/2*b^2*(b*B-A*c)*x/(c^4*(b+c*x^2))-1/2*b^(3/2)*(7*b*B-5*A*c)*ArcTan[x*Sqrt[c]/Sqrt[b]]/c^(9/2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:91
  public void test0581() {
    check( //
        "Integrate[x^9*(A+B*x^2)/(b*x^2+c*x^4)^3, x]", //
        "-1/4*b*(b*B-A*c)/(c^3*(b+c*x^2)^2)+1/2*(2*b*B-A*c)/(c^3*(b+c*x^2))+1/2*B*Log[b+c*x^2]/c^3");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:113
  public void test0582() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[b*x^2+c*x^4]/x^7, x]", //
        "-1/5*A*(b*x^2+c*x^4)^(3/2)/(b*x^8)-1/15*(5*b*B-2*A*c)*(b*x^2+c*x^4)^(3/2)/(b^2*x^6)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:133
  public void test0583() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^(3/2)/x^15, x]", //
        "-1/11*A*(b*x^2+c*x^4)^(5/2)/(b*x^16)-1/99*(11*b*B-6*A*c)*(b*x^2+c*x^4)^(5/2)/(b^2*x^14)+4/693*c*(11*b*B-6*A*c)*(b*x^2+c*x^4)^(5/2)/(b^3*x^12)-8/3465*c^2*(11*b*B-6*A*c)*(b*x^2+c*x^4)^(5/2)/(b^4*x^10)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:153
  public void test0584() {
    check( //
        "Integrate[(A+B*x^2)/(x*Sqrt[b*x^2+c*x^4]), x]", //
        "B*ArcTanh[x^2*Sqrt[c]/Sqrt[b*x^2+c*x^4]]/Sqrt[c]-A*Sqrt[b*x^2+c*x^4]/(b*x^2)");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:171
  public void test0585() {
    check( //
        "Integrate[(A+B*x^2)/(x^5*(b*x^2+c*x^4)^(3/2)), x]", //
        "-1/7*A/(b*x^6*Sqrt[b*x^2+c*x^4])+1/35*(-7*b*B+8*A*c)/(b^2*x^4*Sqrt[b*x^2+c*x^4])+2/35*c*(7*b*B-8*A*c)/(b^3*x^2*Sqrt[b*x^2+c*x^4])-8/35*c^2*(7*b*B-8*A*c)*(b+2*c*x^2)/(b^5*Sqrt[b*x^2+c*x^4])");
  }

  // 1.1.4.3 (e x)^m (a x^j+b x^k)^p (c+d x^n)^q.input:195
  public void test0586() {
    check( //
        "Integrate[(A+B*x^2)*(b*x^2+c*x^4)^2/x^(3/2), x]", //
        "2/7*A*b^2*x^(7/2)+2/11*b*(b*B+2*A*c)*x^(11/2)+2/15*c*(2*b*B+A*c)*x^(15/2)+2/19*B*c^2*x^(19/2)");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:18
  public void test0587() {
    check( //
        "Integrate[(3*x-4*x^2)^(5/2), x]", //
        "-15/1024*(3-8*x)*(3*x-4*x^2)^(3/2)-1/48*(3-8*x)*(3*x-4*x^2)^(5/2)-3645/131072*ArcSin[1-8/3*x]-405/32768*(3-8*x)*Sqrt[3*x-4*x^2]");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:60
  public void test0588() {
    check( //
        "Integrate[1/(b*x+c*x^2)^(1/4), x]", //
        "b*(-c*(b*x+c*x^2)/b^2)^(1/4)*EllipticE[1/2*ArcSin[1+2*c*x/b],2]*Sqrt[2]/(c*(b*x+c*x^2)^(1/4))");
  }

  // 1.2.1.1 (a+b x+c x^2)^p.input:142
  public void test0589() {
    check( //
        "Integrate[Sqrt[5-6*x+9*x^2], x]", //
        "2/3*ArcSinh[1/2*(-1+3*x)]-1/6*(1-3*x)*Sqrt[5-6*x+9*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:20
  public void test0590() {
    check( //
        "Integrate[(b*x+c*x^2)^(1/2)/x^5, x]", //
        "-2/7*(b*x+c*x^2)^(3/2)/(b*x^5)+8/35*c*(b*x+c*x^2)^(3/2)/(b^2*x^4)-16/105*c^2*(b*x+c*x^2)^(3/2)/(b^3*x^3)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:38
  public void test0591() {
    check( //
        "Integrate[(a*x+b*x^2)^(5/2)/x, x]", //
        "1/16*a*(a+2*b*x)*(a*x+b*x^2)^(3/2)/b+1/5*(a*x+b*x^2)^(5/2)+3/128*a^5*ArcTanh[x*Sqrt[b]/Sqrt[a*x+b*x^2]]/b^(5/2)-3/128*a^3*(a+2*b*x)*Sqrt[a*x+b*x^2]/b^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:60
  public void test0592() {
    check( //
        "Integrate[1/(x*(b*x+c*x^2)^(1/2)), x]", //
        "-2*Sqrt[b*x+c*x^2]/(b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:100
  public void test0593() {
    check( //
        "Integrate[x^(5/2)*(b*x+c*x^2)^(3/2), x]", //
        "256/15015*b^4*(b*x+c*x^2)^(5/2)/(c^5*x^(5/2))-128/3003*b^3*(b*x+c*x^2)^(5/2)/(c^4*x^(3/2))+2/13*x^(3/2)*(b*x+c*x^2)^(5/2)/c+32/429*b^2*(b*x+c*x^2)^(5/2)/(c^3*Sqrt[x])-16/143*b*(b*x+c*x^2)^(5/2)*Sqrt[x]/c^2");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:122
  public void test0594() {
    check( //
        "Integrate[x^(11/2)/(b*x+c*x^2)^(3/2), x]", //
        "-128/35*b^3*x^(3/2)/(c^4*Sqrt[b*x+c*x^2])+32/35*b^2*x^(5/2)/(c^3*Sqrt[b*x+c*x^2])-16/35*b*x^(7/2)/(c^2*Sqrt[b*x+c*x^2])+2/7*x^(9/2)/(c*Sqrt[b*x+c*x^2])-256/35*b^4*Sqrt[x]/(c^5*Sqrt[b*x+c*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:174
  public void test0595() {
    check( //
        "Integrate[Sqrt[a^2+2*a*b*x+b^2*x^2]/x^2, x]", //
        "-a*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x*(a+b*x))+b*Log[x]*Sqrt[a^2+2*a*b*x+b^2*x^2]/(a+b*x)");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:193
  public void test0596() {
    check( //
        "Integrate[(a^2+2*a*b*x+b^2*x^2)^(3/2)/x^9, x]", //
        "-1/8*a^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^8*(a+b*x))-3/7*a^2*b*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^7*(a+b*x))-1/2*a*b^2*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^6*(a+b*x))-1/5*b^3*Sqrt[a^2+2*a*b*x+b^2*x^2]/(x^5*(a+b*x))");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:233
  public void test0597() {
    check( //
        "Integrate[x^4/(a^2+2*a*b*x+b^2*x^2)^(5/2), x]", //
        "4*a/(b^5*Sqrt[a^2+2*a*b*x+b^2*x^2])-1/4*a^4/(b^5*(a+b*x)^3*Sqrt[a^2+2*a*b*x+b^2*x^2])+4/3*a^3/(b^5*(a+b*x)^2*Sqrt[a^2+2*a*b*x+b^2*x^2])-3*a^2/(b^5*(a+b*x)*Sqrt[a^2+2*a*b*x+b^2*x^2])+(a+b*x)*Log[a+b*x]/(b^5*Sqrt[a^2+2*a*b*x+b^2*x^2])");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:619
  public void test0598() {
    check( //
        "Integrate[(d+e*x)*Sqrt[a+c*x^2], x]", //
        "1/3*e*(a+c*x^2)^(3/2)/c+1/2*a*d*ArcTanh[x*Sqrt[c]/Sqrt[a+c*x^2]]/Sqrt[c]+1/2*d*x*Sqrt[a+c*x^2]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2551
  public void test0599() {
    check( //
        "Integrate[x/(6-5*x+x^2), x]", //
        "-2*Log[2-x]+3*Log[3-x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:2896
  public void test0600() {
    check( //
        "Integrate[(5-x)*(2+5*x+3*x^2)^2*Sqrt[3+2*x], x]", //
        "325/96*(3+2*x)^(3/2)-213/32*(3+2*x)^(5/2)+93/16*(3+2*x)^(7/2)-359/144*(3+2*x)^(9/2)+15/32*(3+2*x)^(11/2)-9/416*(3+2*x)^(13/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:165
  public void test0601() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^3, x]", //
        "-1/2*a^2*c/x^2+1/2*b*(b*c+2*a*d)*x^2+1/4*b^2*d*x^4+a*(2*b*c+a*d)*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:361
  public void test0602() {
    check( //
        "Integrate[(a+b*x^3)^8/x^43, x]", //
        "-1/42*a^8/x^42-8/39*a^7*b/x^39-7/9*a^6*b^2/x^36-56/33*a^5*b^3/x^33-7/3*a^4*b^4/x^30-56/27*a^3*b^5/x^27-7/6*a^2*b^6/x^24-8/21*a*b^7/x^21-1/18*b^8/x^18");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:487
  public void test0603() {
    check( //
        "Integrate[x/Sqrt[a+b*x^3], x]", //
        "2*Sqrt[a+b*x^3]/(b^(2/3)*(b^(1/3)*x+a^(1/3)*(1+Sqrt[3])))+2*a^(1/3)*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])-3^(1/4)*a^(1/3)*(a^(1/3)+b^(1/3)*x)*EllipticE[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(b^(2/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1120
  public void test0604() {
    check( //
        "Integrate[(a+b*x^4)^(1/4)/x^14, x]", //
        "-1/13*(a+b*x^4)^(5/4)/(a*x^13)+8/117*b*(a+b*x^4)^(5/4)/(a^2*x^9)-32/585*b^2*(a+b*x^4)^(5/4)/(a^3*x^5)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1856
  public void test0605() {
    check( //
        "Integrate[(a+b/x)^3/x^5, x]", //
        "-1/7*b^3/x^7-1/2*a*b^2/x^6-3/5*a^2*b/x^5-1/4*a^3/x^4");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1952
  public void test0606() {
    check( //
        "Integrate[1/((a+b/x)*x^(3/2)), x]", //
        "2*ArcTan[Sqrt[a]*Sqrt[x]/Sqrt[b]]/(Sqrt[a]*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2120
  public void test0607() {
    check( //
        "Integrate[(a+b/x^2)^2*x^4, x]", //
        "b^2*x+2/3*a*b*x^3+1/5*a^2*x^5");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2300
  public void test0608() {
    check( //
        "Integrate[1/((a+b/x^3)*x^7), x]", //
        "(-1/3)/(b*x^3)-a*Log[x]/b^2+1/3*a*Log[b+a*x^3]/b^2");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2828
  public void test0609() {
    check( //
        "Integrate[1/Sqrt[1+x^(1/3)], x]", //
        "-4*(1+x^(1/3))^(3/2)+6/5*(1+x^(1/3))^(5/2)+6*Sqrt[1+x^(1/3)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3094
  public void test0610() {
    check( //
        "Integrate[(a+b*x^n)^3/x, x]", //
        "3*a^2*b*x^n/n+3/2*a*b^2*x^(2*n)/n+1/3*b^3*x^(3*n)/n+a^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3120
  public void test0611() {
    check( //
        "Integrate[x^(-1+6*n)*(a+b*x^n)^8, x]", //
        "-1/9*a^5*(a+b*x^n)^9/(b^6*n)+1/2*a^4*(a+b*x^n)^10/(b^6*n)-10/11*a^3*(a+b*x^n)^11/(b^6*n)+5/6*a^2*(a+b*x^n)^12/(b^6*n)-5/13*a*(a+b*x^n)^13/(b^6*n)+1/14*(a+b*x^n)^14/(b^6*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3178
  public void test0612() {
    check( //
        "Integrate[x^(-1-n)/(2+b*x^n), x]", //
        "(-1/2)/(n*x^n)-1/4*b*Log[x]+1/4*b*Log[2+b*x^n]/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3322
  public void test0613() {
    check( //
        "Integrate[x^m/(a+b*x^(2+2*m))^(1/2), x]", //
        "ArcTanh[x^(1+m)*Sqrt[b]/Sqrt[a+b*x^(2*(1+m))]]/((1+m)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3442
  public void test0614() {
    check( //
        "Integrate[1/(c/(a+b*x)^(3/2))^(2/3), x]", //
        "1/2*(a+b*x)/(b*(c/(a+b*x)^(3/2))^(2/3))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3730
  public void test0615() {
    check( //
        "Integrate[(a+b*(c*x^n)^(2/n))^2, x]", //
        "a^2*x+2/3*a*b*x*(c*x^n)^(2/n)+1/5*b^2*x*(c*x^n)^(4/n)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:83
  public void test0616() {
    check( //
        "Integrate[(c+d*x^3)/(a+b*x^3)^(7/3), x]", //
        "3/4*c*x/(a^2*(a+b*x^3)^(1/3))+1/4*x*(c+d*x^3)/(a*(a+b*x^3)^(4/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:126
  public void test0617() {
    check( //
        "Integrate[1/((a+b*x^3)^(1/3)*(c+d*x^3)^2), x]", //
        "-1/3*d*x*(a+b*x^3)^(2/3)/(c*(b*c-a*d)*(c+d*x^3))+1/18*(3*b*c-2*a*d)*Log[c+d*x^3]/(c^(5/3)*(b*c-a*d)^(4/3))-1/6*(3*b*c-2*a*d)*Log[(b*c-a*d)^(1/3)*x/c^(1/3)-(a+b*x^3)^(1/3)]/(c^(5/3)*(b*c-a*d)^(4/3))+1/3*(3*b*c-2*a*d)*ArcTan[(1+2*(b*c-a*d)^(1/3)*x/(c^(1/3)*(a+b*x^3)^(1/3)))/Sqrt[3]]/(c^(5/3)*(b*c-a*d)^(4/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:257
  public void test0618() {
    check( //
        "Integrate[1/((a+b*x^4)^(5/4)*(c+d*x^4)), x]", //
        "b*x/(a*(b*c-a*d)*(a+b*x^4)^(1/4))-1/2*d*ArcTan[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*(b*c-a*d)^(5/4))-1/2*d*ArcTanh[(b*c-a*d)^(1/4)*x/(c^(1/4)*(a+b*x^4)^(1/4))]/(c^(3/4)*(b*c-a*d)^(5/4))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:525
  public void test0619() {
    check( //
        "Integrate[(a+b*x^2)/((-c+d*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "2*b*ArcTanh[Sqrt[-c+d*x]/Sqrt[c+d*x]]/d^3-(a/c^2+b/d^2)*x/(Sqrt[-c+d*x]*Sqrt[c+d*x])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:46
  public void test0620() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^3, x]", //
        "-1/2*a^5*A/x^2+a^4*(5*A*b+a*B)*x+5/4*a^3*b*(2*A*b+a*B)*x^4+10/7*a^2*b^2*(A*b+a*B)*x^7+1/2*a*b^3*(A*b+2*a*B)*x^10+1/13*b^4*(A*b+5*a*B)*x^13+1/16*b^5*B*x^16");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:72
  public void test0621() {
    check( //
        "Integrate[x^3*(A+B*x^3)/(a+b*x^3), x]", //
        "(A*b-a*B)*x/b^2+1/4*B*x^4/b-1/3*a^(1/3)*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/b^(7/3)+1/6*a^(1/3)*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(7/3)+a^(1/3)*(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(b^(7/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:178
  public void test0622() {
    check( //
        "Integrate[(a+b*x^3)^3*(A+B*x^3)/Sqrt[x], x]", //
        "2/7*a^2*(3*A*b+a*B)*x^(7/2)+6/13*a*b*(A*b+a*B)*x^(13/2)+2/19*b^2*(A*b+3*a*B)*x^(19/2)+2/25*b^3*B*x^(25/2)+2*a^3*A*Sqrt[x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:1000
  public void test0623() {
    check( //
        "Integrate[(7+3*x)/(8+6*x+x^2), x]", //
        "1/2*Log[2+x]+5/2*Log[4+x]");
  }

  // 1.1.2.5 (a+b x^2)^p (c+d x^2)^q (e+f x^2)^r.input:16
  public void test0624() {
    check( //
        "Integrate[(a+b*x^2)*(c+d*x^2)/(e+f*x^2), x]", //
        "-1/3*(3*b*d*e-3*b*c*f-2*a*d*f)*x/f^2+1/3*d*x*(a+b*x^2)/f+(b*e-a*f)*(d*e-c*f)*ArcTan[x*Sqrt[f]/Sqrt[e]]/(f^(5/2)*Sqrt[e])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:499
  public void test0625() {
    check( //
        "Integrate[1/(a+b*x^3)^(3/2), x]", //
        "2/3*x/(a*Sqrt[a+b*x^3])+2/3*(a^(1/3)+b^(1/3)*x)*EllipticF[ArcSin[(b^(1/3)*x+a^(1/3)*(1-Sqrt[3]))/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))],-7-4*Sqrt[3]]*Sqrt[2+Sqrt[3]]*Sqrt[(a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2]/(3^(1/4)*a*b^(1/3)*Sqrt[a+b*x^3]*Sqrt[a^(1/3)*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*x+a^(1/3)*(1+Sqrt[3]))^2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:898
  public void test0626() {
    check( //
        "Integrate[(a+c*x^4)^(3/2)/x^19, x]", //
        "-1/18*(a+c*x^4)^(5/2)/(a*x^18)+2/63*c*(a+c*x^4)^(5/2)/(a^2*x^14)-4/315*c^2*(a+c*x^4)^(5/2)/(a^3*x^10)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1012
  public void test0627() {
    check( //
        "Integrate[1/(1-x^4)^(3/2), x]", //
        "1/2*EllipticF[ArcSin[x],-1]+1/2*x/Sqrt[1-x^4]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1246
  public void test0628() {
    check( //
        "Integrate[1/(x^14*(a+b*x^4)^(3/4)), x]", //
        "-1/13*(a+b*x^4)^(1/4)/(a*x^13)+4/39*b*(a+b*x^4)^(1/4)/(a^2*x^9)-32/195*b^2*(a+b*x^4)^(1/4)/(a^3*x^5)+128/195*b^3*(a+b*x^4)^(1/4)/(a^4*x)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1673
  public void test0629() {
    check( //
        "Integrate[1/(x*(1-x^8)), x]", //
        "Log[x]-1/8*Log[1-x^8]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1868
  public void test0630() {
    check( //
        "Integrate[(a+b/x)^8*x^5, x]", //
        "-1/2*b^8/x^2-8*a*b^7/x+56*a^3*b^5*x+35*a^4*b^4*x^2+56/3*a^5*b^3*x^3+7*a^6*b^2*x^4+8/5*a^7*b*x^5+1/6*a^8*x^6+28*a^2*b^6*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2124
  public void test0631() {
    check( //
        "Integrate[(a+b/x^2)^2, x]", //
        "-1/3*b^2/x^3-2*a*b/x+a^2*x");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2851
  public void test0632() {
    check( //
        "Integrate[1/(1+x^(2/3)), x]", //
        "3*x^(1/3)-3*ArcTan[x^(1/3)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3095
  public void test0633() {
    check( //
        "Integrate[x^(-1-n)*(a+b*x^n)^3, x]", //
        "-a^3/(n*x^n)+3*a*b^2*x^n/n+1/2*b^3*x^(2*n)/n+3*a^2*b*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3121
  public void test0634() {
    check( //
        "Integrate[x^(-1+5*n)*(a+b*x^n)^8, x]", //
        "1/9*a^4*(a+b*x^n)^9/(b^5*n)-2/5*a^3*(a+b*x^n)^10/(b^5*n)+6/11*a^2*(a+b*x^n)^11/(b^5*n)-1/3*a*(a+b*x^n)^12/(b^5*n)+1/13*(a+b*x^n)^13/(b^5*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3179
  public void test0635() {
    check( //
        "Integrate[x^(-1-2*n)/(2+b*x^n), x]", //
        "(-1/4)/(n*x^(2*n))+1/4*b/(n*x^n)+1/8*b^2*Log[x]-1/8*b^2*Log[2+b*x^n]/n");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3290
  public void test0636() {
    check( //
        "Integrate[x^(-1+2*n)*(a+b*x^n)^p, x]", //
        "-a*(a+b*x^n)^(1+p)/(b^2*n*(1+p))+(a+b*x^n)^(2+p)/(b^2*n*(2+p))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3731
  public void test0637() {
    check( //
        "Integrate[a+b*(c*x^n)^(2/n), x]", //
        "a*x+1/3*b*x*(c*x^n)^(2/n)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:47
  public void test0638() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(1/3), x]", //
        "-1/3*x*(a+b*x^3)^(2/3)-2/3*a*Log[-b^(1/3)*x+(a+b*x^3)^(1/3)]/b^(1/3)+4/3*a*ArcTan[(1+2*b^(1/3)*x/(a+b*x^3)^(1/3))/Sqrt[3]]/(b^(1/3)*Sqrt[3])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:192
  public void test0639() {
    check( //
        "Integrate[(a+b*x^4)^2/(c+d*x^4), x]", //
        "-b*(b*c-2*a*d)*x/d^2+1/5*b^2*x^5/d-1/2*(b*c-a*d)^2*ArcTan[1-d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*d^(9/4)*Sqrt[2])+1/2*(b*c-a*d)^2*ArcTan[1+d^(1/4)*x*Sqrt[2]/c^(1/4)]/(c^(3/4)*d^(9/4)*Sqrt[2])-1/4*(b*c-a*d)^2*Log[-c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*d^(9/4)*Sqrt[2])+1/4*(b*c-a*d)^2*Log[c^(1/4)*d^(1/4)*x*Sqrt[2]+Sqrt[c]+x^2*Sqrt[d]]/(c^(3/4)*d^(9/4)*Sqrt[2])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:27
  public void test0640() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^3, x]", //
        "-1/2*a^2*A/x^2+a*(2*A*b+a*B)*x+1/4*b*(A*b+2*a*B)*x^4+1/7*b^2*B*x^7");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:47
  public void test0641() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^4, x]", //
        "-1/3*a^5*A/x^3+5/3*a^3*b*(2*A*b+a*B)*x^3+5/3*a^2*b^2*(A*b+a*B)*x^6+5/9*a*b^3*(A*b+2*a*B)*x^9+1/12*b^4*(A*b+5*a*B)*x^12+1/15*b^5*B*x^15+a^4*(5*A*b+a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:179
  public void test0642() {
    check( //
        "Integrate[(a+b*x^3)^3*(A+B*x^3)/x^(3/2), x]", //
        "2/5*a^2*(3*A*b+a*B)*x^(5/2)+6/11*a*b*(A*b+a*B)*x^(11/2)+2/17*b^2*(A*b+3*a*B)*x^(17/2)+2/23*b^3*B*x^(23/2)-2*a^3*A/Sqrt[x]");
  }

  // 1.1.4.2 (c x)^m (a x^j+b x^n)^p.input:47
  public void test0643() {
    check( //
        "Integrate[1/(x-x^3), x]", //
        "Log[x]-1/2*Log[1-x^2]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:1001
  public void test0644() {
    check( //
        "Integrate[(5+2*x)/(5+4*x+x^2), x]", //
        "ArcTan[2+x]+Log[5+4*x+x^2]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:725
  public void test0645() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^2*Sqrt[c+d*x^2]), x]", //
        "-1/2*b*(b*c-4*a*d)*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(3/2)-a^2*Sqrt[c+d*x^2]/(c*x)+1/2*b^2*x*Sqrt[c+d*x^2]/d");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1148
  public void test0646() {
    check( //
        "Integrate[(a+b*x^4)^(3/4)/x^4, x]", //
        "-1/3*(a+b*x^4)^(3/4)/x^3+1/2*b^(3/4)*ArcTan[b^(1/4)*x/(a+b*x^4)^(1/4)]+1/2*b^(3/4)*ArcTanh[b^(1/4)*x/(a+b*x^4)^(1/4)]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1262
  public void test0647() {
    check( //
        "Integrate[x^13/(a+b*x^4)^(5/4), x]", //
        "4/3*a^2*x^2/(b^3*(a+b*x^4)^(1/4))-2/9*a*x^6/(b^2*(a+b*x^4)^(1/4))+1/9*x^10/(b*(a+b*x^4)^(1/4))-8/3*a^(5/2)*(1+b*x^4/a)^(1/4)*EllipticE[1/2*ArcTan[x^2*Sqrt[b]/Sqrt[a]],2]/(b^(7/2)*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1380
  public void test0648() {
    check( //
        "Integrate[1/(a-b*x^4)^(3/4), x]", //
        "-(1-a/(b*x^4))^(3/4)*x^3*EllipticF[1/2*ArcCsc[x^2*Sqrt[b]/Sqrt[a]],2]*Sqrt[b]/((a-b*x^4)^(3/4)*Sqrt[a])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1689
  public void test0649() {
    check( //
        "Integrate[x^5/(1+x^8), x]", //
        "-1/4*ArcTan[1-x^2*Sqrt[2]]/Sqrt[2]+1/4*ArcTan[1+x^2*Sqrt[2]]/Sqrt[2]+1/8*Log[1+x^4-x^2*Sqrt[2]]/Sqrt[2]-1/8*Log[1+x^4+x^2*Sqrt[2]]/Sqrt[2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2138
  public void test0650() {
    check( //
        "Integrate[(a+b/x^2)^3/x, x]", //
        "-1/6*b^3/x^6-3/4*a*b^2/x^4-3/2*a^2*b/x^2+a^3*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3103
  public void test0651() {
    check( //
        "Integrate[x^(-1+3*n)*(a+b*x^n)^5, x]", //
        "1/6*a^2*(a+b*x^n)^6/(b^3*n)-2/7*a*(a+b*x^n)^7/(b^3*n)+1/8*(a+b*x^n)^8/(b^3*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3126
  public void test0652() {
    check( //
        "Integrate[(a+b*x^n)^8/x, x]", //
        "8*a^7*b*x^n/n+14*a^6*b^2*x^(2*n)/n+56/3*a^5*b^3*x^(3*n)/n+35/2*a^4*b^4*x^(4*n)/n+56/5*a^3*b^5*x^(5*n)/n+14/3*a^2*b^6*x^(6*n)/n+8/7*a*b^7*x^(7*n)/n+1/8*b^8*x^(8*n)/n+a^8*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3182
  public void test0653() {
    check( //
        "Integrate[x^(-1+3*n)/(a+b*x^n)^2, x]", //
        "x^n/(b^2*n)-a^2/(b^3*n*(a+b*x^n))-2*a*Log[a+b*x^n]/(b^3*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3259
  public void test0654() {
    check( //
        "Integrate[x^(-1-7/2*n)/Sqrt[a+b*x^n], x]", //
        "-2/7*Sqrt[a+b*x^n]/(a*n*x^(7/2*n))+12/35*b*Sqrt[a+b*x^n]/(a^2*n*x^(5/2*n))-16/35*b^2*Sqrt[a+b*x^n]/(a^3*n*x^(3/2*n))+32/35*b^3*Sqrt[a+b*x^n]/(a^4*n*x^(1/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3373
  public void test0655() {
    check( //
        "Integrate[(c*x)^(-1-3/2*n)/Sqrt[a+b*x^n], x]", //
        "4/3*(a+b*x^n)^(3/2)/(a^2*c*n*(c*x)^(3/2*n))-2*Sqrt[a+b*x^n]/(a*c*n*(c*x)^(3/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3491
  public void test0656() {
    check( //
        "Integrate[1/(a+b*(c+d*x)^3)^2, x]", //
        "1/3*(c+d*x)/(a*d*(a+b*(c+d*x)^3))+2/9*Log[a^(1/3)+b^(1/3)*(c+d*x)]/(a^(5/3)*b^(1/3)*d)-1/9*Log[a^(2/3)-a^(1/3)*b^(1/3)*(c+d*x)+b^(2/3)*(c+d*x)^2]/(a^(5/3)*b^(1/3)*d)-2/3*ArcTan[(a^(1/3)-2*b^(1/3)*(c+d*x))/(a^(1/3)*Sqrt[3])]/(a^(5/3)*b^(1/3)*d*Sqrt[3])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3517
  public void test0657() {
    check( //
        "Integrate[1/((c*e+d*e*x)*(a+b*(c+d*x)^3)^2), x]", //
        "1/3/(a*d*e*(a+b*(c+d*x)^3))+Log[c+d*x]/(a^2*d*e)-1/3*Log[a+b*(c+d*x)^3]/(a^2*d*e)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3699
  public void test0658() {
    check( //
        "Integrate[(a+b*(c*x^n)^(1/n))^2, x]", //
        "1/3*x*(a+b*(c*x^n)^(1/n))^3/(b*(c*x^n)^(1/n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3734
  public void test0659() {
    check( //
        "Integrate[1/(a+b*(c*x^n)^(2/n))^3, x]", //
        "1/4*x/(a*(a+b*(c*x^n)^(2/n))^2)+3/8*x/(a^2*(a+b*(c*x^n)^(2/n)))+3/8*x*ArcTan[(c*x^n)^(1/n)*Sqrt[b]/Sqrt[a]]/(a^(5/2)*(c*x^n)^(1/n)*Sqrt[b])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3772
  public void test0660() {
    check( //
        "Integrate[1/(x^2*Sqrt[a+c/x+b*Sqrt[d/x]]), x]", //
        "b*ArcTanh[1/2*(b*d+2*c*Sqrt[d/x])/(Sqrt[c]*Sqrt[d]*Sqrt[a+c/x+b*Sqrt[d/x]])]*Sqrt[d]/c^(3/2)-2*Sqrt[a+c/x+b*Sqrt[d/x]]/c");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:50
  public void test0661() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(10/3), x]", //
        "2/7*x/(a+b*x^3)^(7/3)+5/28*x/(a*(a+b*x^3)^(4/3))+15/28*x/(a^2*(a+b*x^3)^(1/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:199
  public void test0662() {
    check( //
        "Integrate[(c+d*x^4)^2/(a+b*x^4), x]", //
        "d*(2*b*c-a*d)*x/b^2+1/5*d^2*x^5/b-1/2*(b*c-a*d)^2*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(9/4)*Sqrt[2])+1/2*(b*c-a*d)^2*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(9/4)*Sqrt[2])-1/4*(b*c-a*d)^2*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(9/4)*Sqrt[2])+1/4*(b*c-a*d)^2*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(9/4)*Sqrt[2])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:529
  public void test0663() {
    check( //
        "Integrate[(a+b*x^2)/(x^4*(-c+d*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "1/3*a/(c^2*x^3*Sqrt[-c+d*x]*Sqrt[c+d*x])+1/3*(3*b*c^2+4*a*d^2)/(c^4*x*Sqrt[-c+d*x]*Sqrt[c+d*x])-2/3*d^2*(3*b*c^2+4*a*d^2)*x/(c^6*Sqrt[-c+d*x]*Sqrt[c+d*x])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:30
  public void test0664() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^6, x]", //
        "-1/5*a^2*A/x^5-1/2*a*(2*A*b+a*B)/x^2+b*(A*b+2*a*B)*x+1/4*b^2*B*x^4");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:52
  public void test0665() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^9, x]", //
        "-1/8*a^5*A/x^8-1/5*a^4*(5*A*b+a*B)/x^5-5/2*a^3*b*(2*A*b+a*B)/x^2+10*a^2*b^2*(A*b+a*B)*x+5/4*a*b^3*(A*b+2*a*B)*x^4+1/7*b^4*(A*b+5*a*B)*x^7+1/10*b^5*B*x^10");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:126
  public void test0666() {
    check( //
        "Integrate[x^8/((a+b*x^3)*(c+d*x^3)), x]", //
        "1/3*x^3/(b*d)+1/3*a^2*Log[a+b*x^3]/(b^2*(b*c-a*d))-1/3*c^2*Log[c+d*x^3]/(d^2*(b*c-a*d))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:162
  public void test0667() {
    check( //
        "Integrate[(a+b*x^3)*(A+B*x^3)/Sqrt[x], x]", //
        "2/7*(A*b+a*B)*x^(7/2)+2/13*b*B*x^(13/2)+2*a*A*Sqrt[x]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2558
  public void test0668() {
    check( //
        "Integrate[x^3/(2-3*x+x^2), x]", //
        "3*x+1/2*x^2-Log[1-x]+8*Log[2-x]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:321
  public void test0669() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/Sqrt[-a-b*x^2], x]", //
        "-d*x*Sqrt[-a-b*x^2]/(b*Sqrt[c+d*x^2])-c^(3/2)*EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[-a-b*x^2]/(a*Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])+EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[-a-b*x^2]/(b*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:89
  public void test0670() {
    check( //
        "Integrate[x^4*(A+B*x^2)/(a+b*x^2)^2, x]", //
        "(A*b-2*a*B)*x/b^3+1/3*B*x^3/b^2+1/2*a*(A*b-a*B)*x/(b^3*(a+b*x^2))-1/2*(3*A*b-5*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:737
  public void test0671() {
    check( //
        "Integrate[(a+b*x^2)^2/(x^2*(c+d*x^2)^(3/2)), x]", //
        "b^2*ArcTanh[x*Sqrt[d]/Sqrt[c+d*x^2]]/d^(3/2)-a^2/(c*x*Sqrt[c+d*x^2])-(b^2*c^2-2*a*d*(b*c-a*d))*x/(c^2*d*Sqrt[c+d*x^2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:1092
  public void test0672() {
    check( //
        "Integrate[(a+b*x^2)^(3/2)/(x*Sqrt[c+d*x^2]), x]", //
        "-1/2*(b*c-3*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x^2]/(Sqrt[b]*Sqrt[c+d*x^2])]*Sqrt[b]/d^(3/2)-a^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[a]*Sqrt[c+d*x^2])]/Sqrt[c]+1/2*b*Sqrt[a+b*x^2]*Sqrt[c+d*x^2]/d");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:110
  public void test0673() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x+C*x^2+D*x^3)/x^2, x]", //
        "-a*A/x+(A*b+a*C)*x+1/2*(b*B+a*D)*x^2+1/3*b*C*x^3+1/4*b*D*x^4+a*B*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:154
  public void test0674() {
    check( //
        "Integrate[1/(b*x^2)^(1/3), x]", //
        "3*x/(b*x^2)^(1/3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1274
  public void test0675() {
    check( //
        "Integrate[1/(x^8*(a+b*x^4)^(5/4)), x]", //
        "(-1/7)/(a*x^7*(a+b*x^4)^(1/4))+8/21*b/(a^2*x^3*(a+b*x^4)^(1/4))+32/21*b^2*x/(a^3*(a+b*x^4)^(1/4))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1403
  public void test0676() {
    check( //
        "Integrate[1/(x*(a+b*x^5)), x]", //
        "Log[x]/a-1/5*Log[a+b*x^5]/a");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2148
  public void test0677() {
    check( //
        "Integrate[x^4/(a+b/x^2), x]", //
        "b^2*x/a^3-1/3*b*x^3/a^2+1/5*x^5/a-b^(5/2)*ArcTan[x*Sqrt[a]/Sqrt[b]]/a^(7/2)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2226
  public void test0678() {
    check( //
        "Integrate[1/((a+b/x^2)^(1/2)*x), x]", //
        "ArcTanh[Sqrt[a+b/x^2]/Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2674
  public void test0679() {
    check( //
        "Integrate[x^m*(a+b*Sqrt[x])^4, x]", //
        "a^4*x^(1+m)/(1+m)+8*a^3*b*x^(3/2+m)/(3+2*m)+6*a^2*b^2*x^(2+m)/(2+m)+8*a*b^3*x^(5/2+m)/(5+2*m)+b^4*x^(3+m)/(3+m)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3104
  public void test0680() {
    check( //
        "Integrate[x^(-1+2*n)*(a+b*x^n)^5, x]", //
        "-1/6*a*(a+b*x^n)^6/(b^2*n)+1/7*(a+b*x^n)^7/(b^2*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3130
  public void test0681() {
    check( //
        "Integrate[x^(-1-4*n)*(a+b*x^n)^8, x]", //
        "-1/4*a^8/(n*x^(4*n))-8/3*a^7*b/(n*x^(3*n))-14*a^6*b^2/(n*x^(2*n))-56*a^5*b^3/(n*x^n)+56*a^3*b^5*x^n/n+14*a^2*b^6*x^(2*n)/n+8/3*a*b^7*x^(3*n)/n+1/4*b^8*x^(4*n)/n+70*a^4*b^4*Log[x]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3155
  public void test0682() {
    check( //
        "Integrate[x^(-1+2*n)/(a+b*x^n), x]", //
        "x^n/(b*n)-a*Log[a+b*x^n]/(b^2*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3183
  public void test0683() {
    check( //
        "Integrate[x^(-1+2*n)/(a+b*x^n)^2, x]", //
        "a/(b^2*n*(a+b*x^n))+Log[a+b*x^n]/(b^2*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3213
  public void test0684() {
    check( //
        "Integrate[x^(-1+n)*(a+b*x^n)^(1/2), x]", //
        "2/3*(a+b*x^n)^(3/2)/(b*n)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3263
  public void test0685() {
    check( //
        "Integrate[1/2*x^(-1+m)*(2*a*m+b*(2*m-n)*x^n)/(a+b*x^n)^(3/2), x]", //
        "x^m/Sqrt[a+b*x^n]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3374
  public void test0686() {
    check( //
        "Integrate[(c*x)^(-1-5/2*n)/Sqrt[a+b*x^n], x]", //
        "8/3*(a+b*x^n)^(3/2)/(a^2*c*n*(c*x)^(5/2*n))-16/15*(a+b*x^n)^(5/2)/(a^3*c*n*(c*x)^(5/2*n))-2*Sqrt[a+b*x^n]/(a*c*n*(c*x)^(5/2*n))");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3492
  public void test0687() {
    check( //
        "Integrate[1/((c+d*x)*(a+b*(c+d*x)^3)^2), x]", //
        "1/3/(a*d*(a+b*(c+d*x)^3))+Log[c+d*x]/(a^2*d)-1/3*Log[a+b*(c+d*x)^3]/(a^2*d)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:3735
  public void test0688() {
    check( //
        "Integrate[1/(1+4*Sqrt[x^4]), x]", //
        "1/2*x*ArcTan[2*(x^4)^(1/4)]/(x^4)^(1/4)");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:51
  public void test0689() {
    check( //
        "Integrate[(a-b*x^3)/(a+b*x^3)^(13/3), x]", //
        "1/5*x/(a+b*x^3)^(10/3)+4/35*x/(a*(a+b*x^3)^(7/3))+6/35*x/(a^2*(a+b*x^3)^(4/3))+18/35*x/(a^3*(a+b*x^3)^(1/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:98
  public void test0690() {
    check( //
        "Integrate[(c+d*x^3)^2/(a+b*x^3)^(10/3), x]", //
        "9/14*c^2*x/(a^3*(a+b*x^3)^(1/3))+3/14*c*x*(c+d*x^3)/(a^2*(a+b*x^3)^(4/3))+1/7*x*(c+d*x^3)^2/(a*(a+b*x^3)^(7/3))");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:200
  public void test0691() {
    check( //
        "Integrate[(c+d*x^4)/(a+b*x^4), x]", //
        "d*x/b-1/2*(b*c-a*d)*ArcTan[1-b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(5/4)*Sqrt[2])+1/2*(b*c-a*d)*ArcTan[1+b^(1/4)*x*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(5/4)*Sqrt[2])-1/4*(b*c-a*d)*Log[-a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(5/4)*Sqrt[2])+1/4*(b*c-a*d)*Log[a^(1/4)*b^(1/4)*x*Sqrt[2]+Sqrt[a]+x^2*Sqrt[b]]/(a^(3/4)*b^(5/4)*Sqrt[2])");
  }

  // 1.1.3.3 (a+b x^n)^p (c+d x^n)^q.input:510
  public void test0692() {
    check( //
        "Integrate[(a+b*x^2)/(x^5*Sqrt[-1+c*x]*Sqrt[1+c*x]), x]", //
        "1/8*c^2*(4*b+3*a*c^2)*ArcTan[Sqrt[-1+c*x]*Sqrt[1+c*x]]+1/4*a*Sqrt[-1+c*x]*Sqrt[1+c*x]/x^4+1/8*(4*b+3*a*c^2)*Sqrt[-1+c*x]*Sqrt[1+c*x]/x^2");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:31
  public void test0693() {
    check( //
        "Integrate[(a+b*x^3)^2*(A+B*x^3)/x^7, x]", //
        "-1/6*a^2*A/x^6-1/3*a*(2*A*b+a*B)/x^3+1/3*b^2*B*x^3+b*(A*b+2*a*B)*Log[x]");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:55
  public void test0694() {
    check( //
        "Integrate[(a+b*x^3)^5*(A+B*x^3)/x^12, x]", //
        "-1/11*a^5*A/x^11-1/8*a^4*(5*A*b+a*B)/x^8-a^3*b*(2*A*b+a*B)/x^5-5*a^2*b^2*(A*b+a*B)/x^2+5*a*b^3*(A*b+2*a*B)*x+1/4*b^4*(A*b+5*a*B)*x^4+1/7*b^5*B*x^7");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:77
  public void test0695() {
    check( //
        "Integrate[(A+B*x^3)/(x^2*(a+b*x^3)), x]", //
        "-A/(a*x)+1/3*(A*b-a*B)*Log[a^(1/3)+b^(1/3)*x]/(a^(4/3)*b^(2/3))-1/6*(A*b-a*B)*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(a^(4/3)*b^(2/3))+(A*b-a*B)*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]/(a^(4/3)*b^(2/3)*Sqrt[3])");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:129
  public void test0696() {
    check( //
        "Integrate[x^5/((a+b*x^3)*(c+d*x^3)), x]", //
        "-1/3*a*Log[a+b*x^3]/(b*(b*c-a*d))+1/3*c*Log[c+d*x^3]/(d*(b*c-a*d))");
  }

  // 1.1.3.4 (e x)^m (a+b x^n)^p (c+d x^n)^q.input:163
  public void test0697() {
    check( //
        "Integrate[(a+b*x^3)*(A+B*x^3)/x^(3/2), x]", //
        "2/5*(A*b+a*B)*x^(5/2)+2/11*b*B*x^(11/2)-2*a*A/Sqrt[x]");
  }

  // 1.2.1.2 (d+e x)^m (a+b x+c x^2)^p.input:2559
  public void test0698() {
    check( //
        "Integrate[x^3/(1+2*x+x^2), x]", //
        "-2*x+1/2*x^2+1/(1+x)+3*Log[1+x]");
  }

  // 1.2.1.3 (d+e x)^m (f+g x) (a+b x+c x^2)^p.input:671
  public void test0699() {
    check( //
        "Integrate[(1+x)*(1+2*x+x^2)^5/x, x]", //
        "11*x+55/2*x^2+55*x^3+165/2*x^4+462/5*x^5+77*x^6+330/7*x^7+165/8*x^8+55/9*x^9+11/10*x^10+1/11*x^11+Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:91
  public void test0700() {
    check( //
        "Integrate[x^2*(A+B*x^2)/(a+b*x^2)^2, x]", //
        "B*x/b^2-1/2*(A*b-a*B)*x/(b^2*(a+b*x^2))+1/2*(A*b-3*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(5/2)*Sqrt[a])");
  }
}
