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
public class AlgebraicFunctions1 extends AbstractRubiTestCase {
  static boolean init = true;

  public AlgebraicFunctions1(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("AlgebraicFunctions1");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:65
  public void test0001() {
    check( //
        "Integrate[1/(c+d*(a+b*x))^(5/2), x]", //
        "(-2/3)/(b*d*(c+d*(a+b*x))^(3/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:643
  public void test0002() {
    check( //
        "Integrate[1/(x^(7/2)*Sqrt[a+b*x]), x]", //
        "-2/5*Sqrt[a+b*x]/(a*x^(5/2))+8/15*b*Sqrt[a+b*x]/(a^2*x^(3/2))-16/15*b^2*Sqrt[a+b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:657
  public void test0003() {
    check( //
        "Integrate[1/(x^(5/2)*(a+b*x)^(5/2)), x]", //
        "2/3/(a*x^(3/2)*(a+b*x)^(3/2))+4/(a^2*x^(3/2)*Sqrt[a+b*x])-16/3*Sqrt[a+b*x]/(a^3*x^(3/2))+32/3*b*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:713
  public void test0004() {
    check( //
        "Integrate[1/(x^(3/2)*(2-b*x)^(5/2)), x]", //
        "1/3/((2-b*x)^(3/2)*Sqrt[x])+2/3/(Sqrt[x]*Sqrt[2-b*x])-2/3*Sqrt[2-b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:845
  public void test0005() {
    check( //
        "Integrate[x^2*(a+b*x)*Sqrt[c*x^2], x]", //
        "1/4*a*x^3*Sqrt[c*x^2]+1/5*b*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:859
  public void test0006() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)/x^4, x]", //
        "b*c*Sqrt[c*x^2]+a*c*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:889
  public void test0007() {
    check( //
        "Integrate[(a+b*x)/(c*x^2)^(5/2), x]", //
        "-1/4*a/(c^2*x^3*Sqrt[c*x^2])-1/3*b/(c^2*x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:907
  public void test0008() {
    check( //
        "Integrate[x^2*(c*x^2)^(3/2)*(a+b*x)^2, x]", //
        "1/6*a^2*c*x^5*Sqrt[c*x^2]+2/7*a*b*c*x^6*Sqrt[c*x^2]+1/8*b^2*c*x^7*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:921
  public void test0009() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x^6, x]", //
        "2*a*b*c^2*Sqrt[c*x^2]+1/2*b^2*c^2*x*Sqrt[c*x^2]+a^2*c^2*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:937
  public void test0010() {
    check( //
        "Integrate[(a+b*x)^2/(x^2*(c*x^2)^(3/2)), x]", //
        "-1/4*a^2/(c*x^3*Sqrt[c*x^2])-2/3*a*b/(c*x^2*Sqrt[c*x^2])-1/2*b^2/(c*x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:955
  public void test0011() {
    check( //
        "Integrate[Sqrt[c*x^2]/(a+b*x), x]", //
        "Sqrt[c*x^2]/b-a*Log[a+b*x]*Sqrt[c*x^2]/(b^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:985
  public void test0012() {
    check( //
        "Integrate[1/(x^2*(a+b*x)*Sqrt[c*x^2]), x]", //
        "b/(a^2*Sqrt[c*x^2])+(-1/2)/(a*x*Sqrt[c*x^2])+b^2*x*Log[x]/(a^3*Sqrt[c*x^2])-b^2*x*Log[a+b*x]/(a^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1019
  public void test0013() {
    check( //
        "Integrate[x^3/((a+b*x)^2*Sqrt[c*x^2]), x]", //
        "x^2/(b^2*Sqrt[c*x^2])-a^2*x/(b^3*(a+b*x)*Sqrt[c*x^2])-2*a*x*Log[a+b*x]/(b^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1037
  public void test0014() {
    check( //
        "Integrate[(a+b*x)^n*Sqrt[c*x^2], x]", //
        "-a*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^2*(1+n)*x)+(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^2*(2+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1061
  public void test0015() {
    check( //
        "Integrate[x^3*(a+b*x)^n/Sqrt[c*x^2], x]", //
        "a^2*x*(a+b*x)^(1+n)/(b^3*(1+n)*Sqrt[c*x^2])-2*a*x*(a+b*x)^(2+n)/(b^3*(2+n)*Sqrt[c*x^2])+x*(a+b*x)^(3+n)/(b^3*(3+n)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1090
  public void test0016() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)/(c*x^2)^(1/2), x]", //
        "a*x*(d*x)^m/(m*Sqrt[c*x^2])+b*x*(d*x)^(1+m)/(d*(1+m)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1274
  public void test0017() {
    check( //
        "Integrate[1/((1-x)^(1/2)*(1+x)^(3/2)), x]", //
        "-Sqrt[1-x]/Sqrt[1+x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1320
  public void test0018() {
    check( //
        "Integrate[1/((3-x)^(1/2)*(-2+x)^(1/2)), x]", //
        "-ArcSin[5-2*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1465
  public void test0019() {
    check( //
        "Integrate[(c+d*x)^7/(a+b*x)^11, x]", //
        "-1/10*(c+d*x)^8/((b*c-a*d)*(a+b*x)^10)+1/45*d*(c+d*x)^8/((b*c-a*d)^2*(a+b*x)^9)-1/360*d^2*(c+d*x)^8/((b*c-a*d)^3*(a+b*x)^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1650
  public void test0020() {
    check( //
        "Integrate[1/((a+b*x)*(c+d*x)^(1/2)), x]", //
        "-2*ArcTanh[Sqrt[b]*Sqrt[c+d*x]/Sqrt[b*c-a*d]]/(Sqrt[b]*Sqrt[b*c-a*d])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:661
  public void test0021() {
    check( //
        "Integrate[(A+B*x)/(Sqrt[x]*Sqrt[a+b*x]), x]", //
        "(2*A*b-a*B)*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[a+b*x]]/b^(3/2)+B*Sqrt[x]*Sqrt[a+b*x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:675
  public void test0022() {
    check( //
        "Integrate[(A+B*x)/(x^(5/2)*(a+b*x)^(3/2)), x]", //
        "-2/3*A/(a*x^(3/2)*Sqrt[a+b*x])-2/3*(4*A*b-3*a*B)/(a^2*Sqrt[x]*Sqrt[a+b*x])+4/3*(4*A*b-3*a*B)*Sqrt[a+b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:689
  public void test0023() {
    check( //
        "Integrate[(A+B*x)/(x^(11/2)*(a+b*x)^(5/2)), x]", //
        "-2/9*A/(a*x^(9/2)*(a+b*x)^(3/2))-2/9*(4*A*b-3*a*B)/(a^2*x^(7/2)*(a+b*x)^(3/2))-20/9*(4*A*b-3*a*B)/(a^3*x^(7/2)*Sqrt[a+b*x])+160/63*(4*A*b-3*a*B)*Sqrt[a+b*x]/(a^4*x^(7/2))-64/21*b*(4*A*b-3*a*B)*Sqrt[a+b*x]/(a^5*x^(5/2))+256/63*b^2*(4*A*b-3*a*B)*Sqrt[a+b*x]/(a^6*x^(3/2))-512/63*b^3*(4*A*b-3*a*B)*Sqrt[a+b*x]/(a^7*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:709
  public void test0024() {
    check( //
        "Integrate[(c+d*x)^(3/2)*Sqrt[a+b*x]/x, x]", //
        "-2*c^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]+1/4*(3*b^2*c^2+6*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*Sqrt[d])+1/2*(c+d*x)^(3/2)*Sqrt[a+b*x]+1/4*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:723
  public void test0025() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Sqrt[a+b*x]/x^6, x]", //
        "-1/5*(a+b*x)^(3/2)*(c+d*x)^(7/2)/(a*c*x^5)-1/128*(b*c-a*d)^4*(7*b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(5/2))-1/192*(b*c-a*d)^2*(7*b*c+3*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^3*c^2*x^2)+1/240*(b*c-a*d)*(7*b*c+3*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a^2*c^2*x^3)+1/40*(7*b*c+3*a*d)*(c+d*x)^(7/2)*Sqrt[a+b*x]/(a*c^2*x^4)+1/128*(b*c-a*d)^3*(7*b*c+3*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:739
  public void test0026() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x*(c+d*x)^(3/2)), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(3/2)+2*Sqrt[a+b*x]/(c*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:787
  public void test0027() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^2*Sqrt[c+d*x]), x]", //
        "-(3*b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(3/2)+2*b^(3/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[d]-a*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:801
  public void test0028() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x*(c+d*x)^(5/2)), x]", //
        "2/3*(a+b*x)^(3/2)/(c*(c+d*x)^(3/2))-2*a^(3/2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/c^(5/2)+2*a*Sqrt[a+b*x]/(c^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:833
  public void test0029() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^6, x]", //
        "-1/8*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c*x^4)-1/5*(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^5-1/128*(b*c+a*d)*(3*b^4*c^4-28*a*b^3*c^3*d+178*a^2*b^2*c^2*d^2-28*a^3*b*c*d^3+3*a^4*d^4)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(5/2))+2*b^(5/2)*d^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]-1/192*(3*b^3*c^3+109*a*b^2*c^2*d-19*a^2*b*c*d^2+3*a^3*d^3)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^2*x^2)-1/48*(3*b^2*c^2+16*a*b*c*d-3*a^2*d^2)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c^2*x^3)+1/128*(3*b^4*c^4-22*a*b^3*c^3*d-128*a^2*b^2*c^2*d^2+22*a^3*b*c*d^3-3*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:849
  public void test0030() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^2*(c+d*x)^(3/2)), x]", //
        "-a^(3/2)*(5*b*c-3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/c^(5/2)+2*b^(5/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/d^(3/2)-a*(a+b*x)^(3/2)/(c*x*Sqrt[c+d*x])-(2*b*c-3*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^2*d*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:959
  public void test0031() {
    check( //
        "Integrate[x^5/((a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "5/4*(7*b^2*c^2+6*a*b*c*d+3*a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(7/2)*d^(9/2))+2*a*x^4/(b*(b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])-2/3*c*(b*c+3*a*d)*x^3*Sqrt[a+b*x]/(b*d*(b*c-a*d)^2*(c+d*x)^(3/2))-2/3*c*(7*b^2*c^2-12*a*b*c*d-3*a^2*d^2)*x^2*Sqrt[a+b*x]/(b*d^2*(b*c-a*d)^3*Sqrt[c+d*x])-1/12*(105*b^4*c^4-190*a*b^3*c^3*d+36*a^2*b^2*c^2*d^2+30*a^3*b*c*d^3-45*a^4*d^4-2*b*d*(35*b^3*c^3-61*a*b^2*c^2*d+9*a^2*b*c*d^2-15*a^3*d^3)*x)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b^3*d^4*(b*c-a*d)^3)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:993
  public void test0032() {
    check( //
        "Integrate[1/(x^2*(a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "-1/3*b*(5*b*c-3*a*d)/(a^2*c*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+(-1)/(a*c*x*(a+b*x)^(3/2)*(c+d*x)^(3/2))+5*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(7/2))-b*(5*b^2*c^2-10*a*b*c*d+a^2*d^2)/(a^3*c*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])-1/3*d*(15*b^3*c^3-35*a*b^2*c^2*d+9*a^2*b*c*d^2-5*a^3*d^3)*Sqrt[a+b*x]/(a^3*c^2*(b*c-a*d)^3*(c+d*x)^(3/2))-1/3*d*(15*b^4*c^4-40*a*b^3*c^3*d+18*a^2*b^2*c^2*d^2-40*a^3*b*c*d^3+15*a^4*d^4)*Sqrt[a+b*x]/(a^3*c^3*(b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1026
  public void test0033() {
    check( //
        "Integrate[x^2*Sqrt[-1+x]*Sqrt[1+x], x]", //
        "1/4*(-1+x)^(3/2)*x*(1+x)^(3/2)-1/8*ArcCosh[x]+1/8*x*Sqrt[-1+x]*Sqrt[1+x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1115
  public void test0034() {
    check( //
        "Integrate[1/((1-x)^(1/4)*(e*x)^(13/2)*(1+x)^(1/4)), x]", //
        "-2/3*(1-x^2)^(3/4)/(e*(e*x)^(11/2))+16/21*(1-x^2)^(7/4)/(e*(e*x)^(11/2))-64/231*(1-x^2)^(11/4)/(e*(e*x)^(11/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1248
  public void test0035() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/(d+e*x)^4, x]", //
        "-1/3*(b*d-a*e)*(B*d-A*e)/(e^3*(d+e*x)^3)+1/2*(2*b*B*d-A*b*e-a*B*e)/(e^3*(d+e*x)^2)-b*B/(e^3*(d+e*x))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1276
  public void test0036() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^7, x]", //
        "-1/6*(B*d-A*e)*(a+b*x)^4/(e*(b*d-a*e)*(d+e*x)^6)+1/15*(2*b*B*d+A*b*e-3*a*B*e)*(a+b*x)^4/(e*(b*d-a*e)^2*(d+e*x)^5)+1/60*b*(2*b*B*d+A*b*e-3*a*B*e)*(a+b*x)^4/(e*(b*d-a*e)^3*(d+e*x)^4)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1290
  public void test0037() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^2, x]", //
        "-3*b*(b*d-a*e)^4*(7*b*B*d-5*A*b*e-2*a*B*e)*x/e^7+(b*d-a*e)^6*(B*d-A*e)/(e^8*(d+e*x))+5/2*b^2*(b*d-a*e)^3*(7*b*B*d-4*A*b*e-3*a*B*e)*(d+e*x)^2/e^8-5/3*b^3*(b*d-a*e)^2*(7*b*B*d-3*A*b*e-4*a*B*e)*(d+e*x)^3/e^8+3/4*b^4*(b*d-a*e)*(7*b*B*d-2*A*b*e-5*a*B*e)*(d+e*x)^4/e^8-1/5*b^5*(7*b*B*d-A*b*e-6*a*B*e)*(d+e*x)^5/e^8+1/6*b^6*B*(d+e*x)^6/e^8+(b*d-a*e)^5*(7*b*B*d-6*A*b*e-a*B*e)*Log[d+e*x]/e^8");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1332
  public void test0038() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^15, x]", //
        "-1/14*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^14)+1/182*(11*b*B*d+3*A*b*e-14*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^13)+1/1092*b*(11*b*B*d+3*A*b*e-14*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^3*(d+e*x)^12)+1/12012*b^2*(11*b*B*d+3*A*b*e-14*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^4*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1900
  public void test0039() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)/(1-2*x)^3, x]", //
        "3773/64/(1-2*x)^2+(-3283/16)/(1-2*x)-1107/16*x-135/16*x^2-1071/8*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1914
  public void test0040() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)^2/(1-2*x)^3, x]", //
        "41503/128/(1-2*x)^2+(-91091/64)/(1-2*x)-14031/16*x-6345/32*x^2-225/8*x^3-39977/32*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1928
  public void test0041() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)^3/(1-2*x)^3, x]", //
        "65219/128/(1-2*x)^2+(-144837/64)/(1-2*x)-5695/4*x-10425/32*x^2-375/8*x^3-64317/32*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2073
  public void test0042() {
    check( //
        "Integrate[(2+3*x)^6*(3+5*x)*Sqrt[1-2*x], x]", //
        "-1294139/384*(1-2*x)^(3/2)+3916031/640*(1-2*x)^(5/2)-725445/128*(1-2*x)^(7/2)+406455/128*(1-2*x)^(9/2)-1580985/1408*(1-2*x)^(11/2)+409941/1664*(1-2*x)^(13/2)-19683/640*(1-2*x)^(15/2)+3645/2176*(1-2*x)^(17/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2087
  public void test0043() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)^2*Sqrt[1-2*x], x]", //
        "-41503/96*(1-2*x)^(3/2)+91091/160*(1-2*x)^(5/2)-5711/16*(1-2*x)^(7/2)+1949/16*(1-2*x)^(9/2)-7695/352*(1-2*x)^(11/2)+675/416*(1-2*x)^(13/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2101
  public void test0044() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)^3*Sqrt[1-2*x], x]", //
        "-9317/48*(1-2*x)^(3/2)+8349/40*(1-2*x)^(5/2)-2805/28*(1-2*x)^(7/2)+1675/72*(1-2*x)^(9/2)-375/176*(1-2*x)^(11/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2117
  public void test0045() {
    check( //
        "Integrate[Sqrt[1-2*x]/(3+5*x), x]", //
        "-2/5*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2/5*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2193
  public void test0046() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)*(3+5*x)), x]", //
        "-22/5*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+14/3*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-4/15*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2269
  public void test0047() {
    check( //
        "Integrate[(1-2*x)^(5/2)/(3+5*x), x]", //
        "22/75*(1-2*x)^(3/2)+2/25*(1-2*x)^(5/2)-242/125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+242/125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2345
  public void test0048() {
    check( //
        "Integrate[1/((2+3*x)*(3+5*x)*Sqrt[1-2*x]), x]", //
        "2*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[3/7]-2*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[5/11]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2607
  public void test0049() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2, x]", //
        "41/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+107/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/3*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)+10/9*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2621
  public void test0050() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^4, x]", //
        "-250433/31752*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-50/81*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-59/252*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1/9*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-6401/10584*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2637
  public void test0051() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^4*Sqrt[3+5*x]), x]", //
        "-68959/392*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/3*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+173/84*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+18083/1176*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2669
  public void test0052() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^6, x]", //
        "-11988317/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/15*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^5+41/360*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+7723/15120*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+270463/84672*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+28291441/1185408*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2713
  public void test0053() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^4*(3+5*x)^(3/2)), x]", //
        "463881/56*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-608185/504*Sqrt[1-2*x]/Sqrt[3+5*x]+7/9*Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x])+77/12*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+13409/168*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2731
  public void test0054() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x), x]", //
        "98/81*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+250433/16200*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+59/180*(1-2*x)^(3/2)*Sqrt[3+5*x]+1/9*(1-2*x)^(5/2)*Sqrt[3+5*x]+6401/5400*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2745
  public void test0055() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^4, x]", //
        "-1/9*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^3+115/108*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^2+215/1944*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+362/243*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+365/216*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)-845/648*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2759
  public void test0056() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^6, x]", //
        "-1/15*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^5+37/72*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-109715471/4572288*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-200/729*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-32453/36288*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+2543/1296*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-3248687/1524096*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2775
  public void test0057() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^6*Sqrt[3+5*x]), x]", //
        "-104040277/6272*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/15*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^5+2023/360*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+67187/2160*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+2347559/12096*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+245529161/169344*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2821
  public void test0058() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^5*Sqrt[1-2*x]), x]", //
        "-78045/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/84*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-43/504*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+85/14112*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+57595/197568*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2837
  public void test0059() {
    check( //
        "Integrate[(2+3*x)^3/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "44437/1600*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-1/10*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]-1/1600*(5363+2220*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2851
  public void test0060() {
    check( //
        "Integrate[1/((2+3*x)^2*(3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "321/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-515/77*Sqrt[1-2*x]/Sqrt[3+5*x]+3/7*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2869
  public void test0061() {
    check( //
        "Integrate[(2+3*x)^5*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "-35439958001/5120000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+(2+3*x)^5*Sqrt[3+5*x]/Sqrt[1-2*x]+847637/32000*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+10389/1600*(2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x]+33/20*(2+3*x)^4*Sqrt[1-2*x]*Sqrt[3+5*x]+49/5120000*(87394471+36265980*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2897
  public void test0062() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^3), x]", //
        "1815/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*(3+5*x)^(5/2)/((2+3*x)^2*Sqrt[1-2*x])+5/98*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+165/1372*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2913
  public void test0063() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^5*Sqrt[3+5*x]), x]", //
        "-24922335/153664*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-7986105/845152*Sqrt[3+5*x]/Sqrt[1-2*x]+3/28*Sqrt[3+5*x]/((2+3*x)^4*Sqrt[1-2*x])+263/392*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])+6621/1568*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])+698295/21952*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2927
  public void test0064() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "49/22/((3+5*x)^(3/2)*Sqrt[1-2*x])-3679/3630*Sqrt[1-2*x]/(3+5*x)^(3/2)-4091/19965*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2945
  public void test0065() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^4), x]", //
        "-25365/19208*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^3)-16985/316932*Sqrt[3+5*x]/Sqrt[1-2*x]-3/49*Sqrt[3+5*x]/((2+3*x)^3*Sqrt[1-2*x])-1/196*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])+605/2744*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2975
  public void test0066() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^3*Sqrt[3+5*x]), x]", //
        "-5805/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1735/3234*Sqrt[3+5*x]/(1-2*x)^(3/2)+3/14*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^2)+51/28*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x))-57595/249018*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2989
  public void test0067() {
    check( //
        "Integrate[(2+3*x)^3/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "2/33*(2+3*x)^3/((1-2*x)^(3/2)*(3+5*x)^(3/2))+49/121/((3+5*x)^(3/2)*Sqrt[1-2*x])-3679/19965*Sqrt[1-2*x]/(3+5*x)^(3/2)-8182/219615*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3007
  public void test0068() {
    check( //
        "Integrate[1/(Sqrt[-c+d*x]*Sqrt[c+d*x]*Sqrt[e+f*x]), x]", //
        "2*EllipticF[ArcSin[Sqrt[c+d*x]/(Sqrt[2]*Sqrt[c])],-2*c*f/(d*e-c*f)]*Sqrt[c]*Sqrt[(c-d*x)/c]*Sqrt[d*(e+f*x)/(d*e-c*f)]/(d*Sqrt[-c+d*x]*Sqrt[e+f*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3025
  public void test0069() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2), x]", //
        "494/189*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-214/189*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/9*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)-214/189*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3055
  public void test0070() {
    check( //
        "Integrate[Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "2*EllipticE[ArcSin[Sqrt[5/11]*Sqrt[1-2*x]],33/35]*Sqrt[7/5]-2*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3073
  public void test0071() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(3/2)*Sqrt[3+5*x], x]", //
        "2/45*(1-2*x)^(3/2)*(2+3*x)^(3/2)*(3+5*x)^(3/2)-5684677/3543750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-84134/1771875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+62/1575*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-347/39375*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-84134/354375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3087
  public void test0072() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(7/2), x]", //
        "-2/15*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(5/2)-4418/945*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+988/945*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+74/45*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)+988/945*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3101
  public void test0073() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(15/2), x]", //
        "-2/39*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(13/2)-245282464136/1858265955*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-7391549624/1858265955*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-20992/81081*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)+362/1287*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(11/2)-2174468/11918907*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+73596464/417161745*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+3523482724/2920132215*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+245282464136/20440925505*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3117
  public void test0074() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "136/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+4/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/3*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-136/3*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3135
  public void test0075() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[2+3*x]*Sqrt[3+5*x], x]", //
        "-6799613/5315625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-110717/5315625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+326/4725*(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+2/45*(1-2*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+10214/118125*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-110717/1063125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3149
  public void test0076() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(7/2), x]", //
        "-2/15*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(5/2)+46/27*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(3/2)-19174/1215*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+5264/1215*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-316/27*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+5264/243*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3163
  public void test0077() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(15/2), x]", //
        "-2/39*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(13/2)+370/1287*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(11/2)-129922578224/477839817*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-3894280616/477839817*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2622980/1702701*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)+60080/34749*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-54281308/35756721*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+1876198516/750891141*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+129922578224/5256237987*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3179
  public void test0078() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)/(3+5*x)^(3/2), x]", //
        "53279/328125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-110014/328125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(5/2)*(2+3*x)^(3/2)/Sqrt[3+5*x]-32/175*(1-2*x)^(3/2)*(2+3*x)^(3/2)*Sqrt[3+5*x]-1972/4375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+106772/65625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3193
  public void test0079() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "14/9*(1-2*x)^(3/2)/((2+3*x)^(3/2)*(3+5*x)^(3/2))-36968/15*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1112/15*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+308/3*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-6116/9*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+36968/9*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3211
  public void test0080() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-37/21*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/21*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/21*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3225
  public void test0081() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(13/2)*Sqrt[1-2*x]), x]", //
        "-924247516/66706983*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-31704544/66706983*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/231*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(11/2)+940/43659*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(9/2)-251590/2139291*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)-362666/14975037*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+11460644/104825259*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+924247516/733776813*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3257
  public void test0082() {
    check( //
        "Integrate[Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "2/11*EllipticE[ArcSin[Sqrt[5]*Sqrt[2+3*x]],2/35]*Sqrt[7/5]*Sqrt[-3-5*x]/Sqrt[3+5*x]-2/11*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3295
  public void test0083() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(7/2)), x]", //
        "-5636/12005*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4364/12005*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/7*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-36/245*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-26/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+5636/12005*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3309
  public void test0084() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "4451/126*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+67/63*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*(3+5*x)^(3/2)*Sqrt[2+3*x]/Sqrt[1-2*x]+335/63*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3325
  public void test0085() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "2911577/34375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+175111/68750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*(2+3*x)^(7/2)/(Sqrt[1-2*x]*Sqrt[3+5*x])-37/605*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+10851/15125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+502941/151250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3339
  public void test0086() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "98/121*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-16/121*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/11*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-40/363*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-490/3993*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3357
  public void test0087() {
    check( //
        "Integrate[(2+3*x)^(7/2)*(3+5*x)^(3/2)/(1-2*x)^(5/2), x]", //
        "1/3*(2+3*x)^(7/2)*(3+5*x)^(3/2)/(1-2*x)^(3/2)-722133/3500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-6547351/3500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-56/11*(2+3*x)^(5/2)*(3+5*x)^(3/2)/Sqrt[1-2*x]-1341/154*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-140289/3850*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2166399/7700*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3371
  public void test0088() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^(5/2)), x]", //
        "11/21*(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(3/2))+169/7203*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+496/7203*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-22/49*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])+229/1029*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-169/7203*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3387
  public void test0089() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "-148831/6050*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2252/3025*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(5/2)/((1-2*x)^(3/2)*Sqrt[3+5*x])-434/363*(2+3*x)^(3/2)/(Sqrt[1-2*x]*Sqrt[3+5*x])+2129/19965*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3401
  public void test0090() {
    check( //
        "Integrate[Sqrt[2+3*x]/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "4418/9317*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-988/9317*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/33*Sqrt[2+3*x]/((1-2*x)^(3/2)*(3+5*x)^(3/2))+118/847*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-2470/27951*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-22090/307461*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3472
  public void test0091() {
    check( //
        "Integrate[(a+b*x)^(4/3)/((c+d*x)^(4/3)*(e+f*x)^2), x]", //
        "-3*(a+b*x)^(4/3)/((d*e-c*f)*(c+d*x)^(1/3)*(e+f*x))+4*(b*e-a*f)*(a+b*x)^(1/3)*(c+d*x)^(2/3)/((d*e-c*f)^2*(e+f*x))-2/3*(b*c-a*d)*(b*e-a*f)^(1/3)*Log[e+f*x]/(d*e-c*f)^(7/3)+2*(b*c-a*d)*(b*e-a*f)^(1/3)*Log[-(a+b*x)^(1/3)+(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/(d*e-c*f)^(1/3)]/(d*e-c*f)^(7/3)+4*(b*c-a*d)*(b*e-a*f)^(1/3)*ArcTan[1/Sqrt[3]+2*(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/((d*e-c*f)^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]/((d*e-c*f)^(7/3)*Sqrt[3])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3543
  public void test0092() {
    check( //
        "Integrate[(a+b*x)^m*(c+d*x)^(-4-m), x]", //
        "(a+b*x)^(1+m)*(c+d*x)^(-3-m)/((b*c-a*d)*(3+m))+2*b*(a+b*x)^(1+m)*(c+d*x)^(-2-m)/((b*c-a*d)^2*(2+m)*(3+m))+2*b^2*(a+b*x)^(1+m)*(c+d*x)^(-1-m)/((b*c-a*d)^3*(1+m)*(2+m)*(3+m))");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:43
  public void test0093() {
    check( //
        "Integrate[(1+a*x)/(x^3*Sqrt[a*x]*Sqrt[1-a*x]), x]", //
        "-2/5*a^2*Sqrt[1-a*x]/(a*x)^(5/2)-6/5*a^2*Sqrt[1-a*x]/(a*x)^(3/2)-12/5*a^2*Sqrt[1-a*x]/Sqrt[a*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:72
  public void test0094() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[1+4*x]/Sqrt[-5+2*x], x]", //
        "-11/3*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[22/3]*Sqrt[5-2*x]/Sqrt[-5+2*x]+55/18*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+1/3*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:131
  public void test0095() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[1+4*x]/((7+5*x)^(5/2)*Sqrt[-5+2*x]), x]", //
        "2/117*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)-9350/3253419*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]+3740/3253419*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]+44/2691*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[11/23]*Sqrt[7+5*x]/(Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])-1870/83421*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:215
  public void test0096() {
    check( //
        "Integrate[(a+b*x+c*x^2)/(Sqrt[1-d*x]*Sqrt[1+d*x]), x]", //
        "1/2*(c+2*a*d^2)*ArcSin[d*x]/d^3-b*Sqrt[1-d^2*x^2]/d^2-1/2*c*x*Sqrt[1-d^2*x^2]/d^2");
  }

  // 1.1.1.6 P(x) (a+b x)^m (c+d x)^n (e+f x)^p.input:24
  public void test0097() {
    check( //
        "Integrate[(A+B*x+C*x^2)/(Sqrt[1-d*x]*Sqrt[1+d*x]), x]", //
        "1/2*(C+2*A*d^2)*ArcSin[d*x]/d^3-B*Sqrt[1-d^2*x^2]/d^2-1/2*C*x*Sqrt[1-d^2*x^2]/d^2");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:586
  public void test0098() {
    check( //
        "Integrate[Sqrt[2-b*x]/x^(9/2), x]", //
        "-1/7*(2-b*x)^(3/2)/x^(7/2)-2/35*b*(2-b*x)^(3/2)/x^(5/2)-2/105*b^2*(2-b*x)^(3/2)/x^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:644
  public void test0099() {
    check( //
        "Integrate[1/(x^(9/2)*Sqrt[a+b*x]), x]", //
        "-2/7*Sqrt[a+b*x]/(a*x^(7/2))+12/35*b*Sqrt[a+b*x]/(a^2*x^(5/2))-16/35*b^2*Sqrt[a+b*x]/(a^3*x^(3/2))+32/35*b^3*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:714
  public void test0100() {
    check( //
        "Integrate[1/(x^(5/2)*(2-b*x)^(5/2)), x]", //
        "1/3/(x^(3/2)*(2-b*x)^(3/2))+1/(x^(3/2)*Sqrt[2-b*x])-2/3*Sqrt[2-b*x]/x^(3/2)-2/3*b*Sqrt[2-b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:846
  public void test0101() {
    check( //
        "Integrate[x*(a+b*x)*Sqrt[c*x^2], x]", //
        "1/3*a*x^2*Sqrt[c*x^2]+1/4*b*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:860
  public void test0102() {
    check( //
        "Integrate[x^3*(c*x^2)^(5/2)*(a+b*x), x]", //
        "1/9*a*c^2*x^8*Sqrt[c*x^2]+1/10*b*c^2*x^9*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:876
  public void test0103() {
    check( //
        "Integrate[(a+b*x)/(x^3*Sqrt[c*x^2]), x]", //
        "-1/3*a/(x^2*Sqrt[c*x^2])-1/2*b/(x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:890
  public void test0104() {
    check( //
        "Integrate[(a+b*x)/(x*(c*x^2)^(5/2)), x]", //
        "-1/5*a/(c^2*x^4*Sqrt[c*x^2])-1/4*b/(c^2*x^3*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:908
  public void test0105() {
    check( //
        "Integrate[x*(c*x^2)^(3/2)*(a+b*x)^2, x]", //
        "1/5*a^2*c*x^4*Sqrt[c*x^2]+1/3*a*b*c*x^5*Sqrt[c*x^2]+1/7*b^2*c*x^6*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:924
  public void test0106() {
    check( //
        "Integrate[x^3*(a+b*x)^2/Sqrt[c*x^2], x]", //
        "1/3*a^2*x^4/Sqrt[c*x^2]+1/2*a*b*x^5/Sqrt[c*x^2]+1/5*b^2*x^6/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:938
  public void test0107() {
    check( //
        "Integrate[(a+b*x)^2/(x^3*(c*x^2)^(3/2)), x]", //
        "-1/5*a^2/(c*x^4*Sqrt[c*x^2])-1/2*a*b/(c*x^3*Sqrt[c*x^2])-1/3*b^2/(c*x^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:986
  public void test0108() {
    check( //
        "Integrate[1/(x^3*(a+b*x)*Sqrt[c*x^2]), x]", //
        "-b^2/(a^3*Sqrt[c*x^2])+(-1/3)/(a*x^2*Sqrt[c*x^2])+1/2*b/(a^2*x*Sqrt[c*x^2])-b^3*x*Log[x]/(a^4*Sqrt[c*x^2])+b^3*x*Log[a+b*x]/(a^4*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1004
  public void test0109() {
    check( //
        "Integrate[Sqrt[c*x^2]/(x^2*(a+b*x)^2), x]", //
        "Sqrt[c*x^2]/(a*x*(a+b*x))+Log[x]*Sqrt[c*x^2]/(a^2*x)-Log[a+b*x]*Sqrt[c*x^2]/(a^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1020
  public void test0110() {
    check( //
        "Integrate[x^2/((a+b*x)^2*Sqrt[c*x^2]), x]", //
        "a*x/(b^2*(a+b*x)*Sqrt[c*x^2])+x*Log[a+b*x]/(b^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1038
  public void test0111() {
    check( //
        "Integrate[(a+b*x)^n*Sqrt[c*x^2]/x, x]", //
        "(a+b*x)^(1+n)*Sqrt[c*x^2]/(b*(1+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1062
  public void test0112() {
    check( //
        "Integrate[x^2*(a+b*x)^n/Sqrt[c*x^2], x]", //
        "-a*x*(a+b*x)^(1+n)/(b^2*(1+n)*Sqrt[c*x^2])+x*(a+b*x)^(2+n)/(b^2*(2+n)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1091
  public void test0113() {
    check( //
        "Integrate[(d*x)^m*(a+b*x)/(c*x^2)^(3/2), x]", //
        "-a*d^2*x*(d*x)^(-2+m)/(c*(2-m)*Sqrt[c*x^2])-b*d*x*(d*x)^(-1+m)/(c*(1-m)*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1119
  public void test0114() {
    check( //
        "Integrate[(c*x^2)^p*(a+b*x)^(1-2*p)/x^3, x]", //
        "-1/2*(c*x^2)^p*(a+b*x)^(2-2*p)/(a*(1-p)*x^2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1145
  public void test0115() {
    check( //
        "Integrate[(b*c/d+b*x)^3/(c+d*x)^3, x]", //
        "b^3*x/d^3");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1355
  public void test0116() {
    check( //
        "Integrate[1/((a-I*a*x)^(3/4)*(a+I*a*x)^(3/4)), x]", //
        "2*(1+x^2)^(3/4)*EllipticF[1/2*ArcTan[x],2]/((a-I*a*x)^(3/4)*(a+I*a*x)^(3/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1383
  public void test0117() {
    check( //
        "Integrate[1/((a-I*a*x)^(1/4)*(a+I*a*x)^(9/4)), x]", //
        "4/5*I/(a*(a-I*a*x)^(1/4)*(a+I*a*x)^(5/4))+2/5*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a^2*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1466
  public void test0118() {
    check( //
        "Integrate[(c+d*x)^7/(a+b*x)^12, x]", //
        "-1/11*(c+d*x)^8/((b*c-a*d)*(a+b*x)^11)+3/110*d*(c+d*x)^8/((b*c-a*d)^2*(a+b*x)^10)-1/165*d^2*(c+d*x)^8/((b*c-a*d)^3*(a+b*x)^9)+1/1320*d^3*(c+d*x)^8/((b*c-a*d)^4*(a+b*x)^8)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1727
  public void test0119() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(a+b*x)^(13/2), x]", //
        "-2/11*(c+d*x)^(7/2)/((b*c-a*d)*(a+b*x)^(11/2))+8/99*d*(c+d*x)^(7/2)/((b*c-a*d)^2*(a+b*x)^(9/2))-16/693*d^2*(c+d*x)^(7/2)/((b*c-a*d)^3*(a+b*x)^(7/2))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1757
  public void test0120() {
    check( //
        "Integrate[1/((a+b*x)^(3/2)*(c+d*x)^(5/2)), x]", //
        "(-2)/((b*c-a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x])-8/3*d*Sqrt[a+b*x]/((b*c-a*d)^2*(c+d*x)^(3/2))-16/3*b*d*Sqrt[a+b*x]/((b*c-a*d)^3*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1785
  public void test0121() {
    check( //
        "Integrate[1/(Sqrt[-b*x]*Sqrt[2-b*x]), x]", //
        "-2*ArcSinh[Sqrt[-b*x]/Sqrt[2]]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2176
  public void test0122() {
    check( //
        "Integrate[a+b*x+c*x^2+d*x^3, x]", //
        "a*x+1/2*b*x^2+1/3*c*x^3+1/4*d*x^4");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2192
  public void test0123() {
    check( //
        "Integrate[(-2)/x^2+3/x, x]", //
        "2/x+3*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:57
  public void test0124() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^6/x^8, x]", //
        "-1/7*a^7*c^6/x^7+5/6*a^6*b*c^6/x^6-9/5*a^5*b^2*c^6/x^5+5/4*a^4*b^3*c^6/x^4+5/3*a^3*b^4*c^6/x^3-9/2*a^2*b^5*c^6/x^2+5*a*b^6*c^6/x+b^7*c^6*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:115
  public void test0125() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^2, x]", //
        "-a*A/x+b*B*x+(A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:129
  public void test0126() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^5, x]", //
        "-1/4*A*(a+b*x)^3/(a*x^4)+1/12*(A*b-4*a*B)*(a+b*x)^3/(a^2*x^3)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:143
  public void test0127() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^6, x]", //
        "-1/5*A*(a+b*x)^4/(a*x^5)+1/20*(A*b-5*a*B)*(a+b*x)^4/(a^2*x^4)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:157
  public void test0128() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x^4, x]", //
        "-1/3*a^5*A/x^3-1/2*a^4*(5*A*b+a*B)/x^2-5*a^3*b*(2*A*b+a*B)/x+5*a*b^3*(A*b+2*a*B)*x+1/2*b^4*(A*b+5*a*B)*x^2+1/3*b^5*B*x^3+10*a^2*b^2*(A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:185
  public void test0129() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^9, x]", //
        "-1/8*a^10*A/x^8-1/7*a^9*(10*A*b+a*B)/x^7-5/6*a^8*b*(9*A*b+2*a*B)/x^6-3*a^7*b^2*(8*A*b+3*a*B)/x^5-15/2*a^6*b^3*(7*A*b+4*a*B)/x^4-14*a^5*b^4*(6*A*b+5*a*B)/x^3-21*a^4*b^5*(5*A*b+6*a*B)/x^2-30*a^3*b^6*(4*A*b+7*a*B)/x+5*a*b^8*(2*A*b+9*a*B)*x+1/2*b^9*(A*b+10*a*B)*x^2+1/3*b^10*B*x^3+15*a^2*b^7*(3*A*b+8*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:429
  public void test0130() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/x^(3/2), x]", //
        "2/3*b*B*x^(3/2)-2*a*A/Sqrt[x]+2*(A*b+a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:460
  public void test0131() {
    check( //
        "Integrate[(A+B*x)/(x^(11/2)*(a+b*x)), x]", //
        "-2/9*A/(a*x^(9/2))+2/7*(A*b-a*B)/(a^2*x^(7/2))-2/5*b*(A*b-a*B)/(a^3*x^(5/2))+2/3*b^2*(A*b-a*B)/(a^4*x^(3/2))-2*b^(7/2)*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/a^(11/2)-2*b^3*(A*b-a*B)/(a^5*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:662
  public void test0132() {
    check( //
        "Integrate[(A+B*x)/(x^(3/2)*Sqrt[a+b*x]), x]", //
        "2*B*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[a+b*x]]/Sqrt[b]-2*A*Sqrt[a+b*x]/(a*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:676
  public void test0133() {
    check( //
        "Integrate[(A+B*x)/(x^(7/2)*(a+b*x)^(3/2)), x]", //
        "-2/5*A/(a*x^(5/2)*Sqrt[a+b*x])-2/5*(6*A*b-5*a*B)/(a^2*x^(3/2)*Sqrt[a+b*x])+8/15*(6*A*b-5*a*B)*Sqrt[a+b*x]/(a^3*x^(3/2))-16/15*b*(6*A*b-5*a*B)*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:724
  public void test0134() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Sqrt[a+b*x]/x^7, x]", //
        "1/512*(b*c-a*d)^4*(21*b^2*c^2+14*a*b*c*d+5*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(11/2)*c^(7/2))-1/60*(b*c+5*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x^5)-1/6*(c+d*x)^(5/2)*Sqrt[a+b*x]/x^6+1/160*(3*b^2*c^2-6*a*b*c*d-5*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*x^4)-1/960*(21*b^3*c^3-61*a*b^2*c^2*d+51*a^2*b*c*d^2+5*a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c*x^3)+1/3840*(105*b^4*c^4-308*a*b^3*c^3*d+262*a^2*b^2*c^2*d^2-20*a^3*b*c*d^3+25*a^4*d^4)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^4*c^2*x^2)-1/7680*(315*b^5*c^5-945*a*b^4*c^4*d+838*a^2*b^3*c^3*d^2-90*a^3*b^2*c^2*d^3-65*a^4*b*c*d^4+75*a^5*d^5)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^5*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:740
  public void test0135() {
    check( //
        "Integrate[Sqrt[a+b*x]/(x^2*(c+d*x)^(3/2)), x]", //
        "-(b*c-3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(5/2)*Sqrt[a])-(a+b*x)^(3/2)/(a*c*x*Sqrt[c+d*x])+(b*c-3*a*d)*Sqrt[a+b*x]/(a*c^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:758
  public void test0136() {
    check( //
        "Integrate[(a+b*x)^(3/2)*Sqrt[c+d*x]/x^3, x]", //
        "-1/4*(3*b^2*c^2+6*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(3/2)*Sqrt[a])+2*b^(3/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]-1/2*(a+b*x)^(3/2)*Sqrt[c+d*x]/x^2-1/4*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:788
  public void test0137() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^3*Sqrt[c+d*x]), x]", //
        "-3/4*(b*c-a*d)^2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(5/2)*Sqrt[a])-1/2*(a+b*x)^(3/2)*Sqrt[c+d*x]/(c*x^2)-3/4*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:802
  public void test0138() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^2*(c+d*x)^(5/2)), x]", //
        "1/3*(3*b*c-5*a*d)*(a+b*x)^(3/2)/(a*c^2*(c+d*x)^(3/2))-(a+b*x)^(5/2)/(a*c*x*(c+d*x)^(3/2))-(3*b*c-5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(7/2)+(3*b*c-5*a*d)*Sqrt[a+b*x]/(c^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:834
  public void test0139() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^7, x]", //
        "-1/12*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(7/2)/(c^2*x^5)-1/6*(a+b*x)^(5/2)*(c+d*x)^(7/2)/(c*x^6)+5/512*(b*c-a*d)^6*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(7/2))+5/768*(b*c-a*d)^4*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*c^3*x^2)-1/192*(b*c-a*d)^3*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*c^3*x^3)-1/32*(b*c-a*d)^2*(c+d*x)^(7/2)*Sqrt[a+b*x]/(c^3*x^4)-5/512*(b*c-a*d)^5*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:850
  public void test0140() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^3*(c+d*x)^(3/2)), x]", //
        "-15/4*(b*c-a*d)^2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(7/2)-5/4*(b*c-a*d)*(a+b*x)^(3/2)/(c^2*x*Sqrt[c+d*x])-1/2*(a+b*x)^(5/2)/(c*x^2*Sqrt[c+d*x])+15/4*(b*c-a*d)^2*Sqrt[a+b*x]/(c^3*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:912
  public void test0141() {
    check( //
        "Integrate[x/((c+d*x)^(3/2)*Sqrt[a+b*x]), x]", //
        "2*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(d^(3/2)*Sqrt[b])-2*c*Sqrt[a+b*x]/(d*(b*c-a*d)*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:978
  public void test0142() {
    check( //
        "Integrate[(c+d*x)^(5/2)/(x^2*(a+b*x)^(5/2)), x]", //
        "-5/3*(b*c-a*d)*(c+d*x)^(3/2)/(a^2*(a+b*x)^(3/2))-(c+d*x)^(5/2)/(a*x*(a+b*x)^(3/2))+5*c^(3/2)*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/a^(7/2)-5*c*(b*c-a*d)*Sqrt[c+d*x]/(a^3*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1027
  public void test0143() {
    check( //
        "Integrate[x*Sqrt[-1+x]*Sqrt[1+x], x]", //
        "1/3*(-1+x)^(3/2)*(1+x)^(3/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1291
  public void test0144() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^3, x]", //
        "5*b^2*(b*d-a*e)^3*(7*b*B*d-4*A*b*e-3*a*B*e)*x/e^7+1/2*(b*d-a*e)^6*(B*d-A*e)/(e^8*(d+e*x)^2)-(b*d-a*e)^5*(7*b*B*d-6*A*b*e-a*B*e)/(e^8*(d+e*x))-5/2*b^3*(b*d-a*e)^2*(7*b*B*d-3*A*b*e-4*a*B*e)*(d+e*x)^2/e^8+b^4*(b*d-a*e)*(7*b*B*d-2*A*b*e-5*a*B*e)*(d+e*x)^3/e^8-1/4*b^5*(7*b*B*d-A*b*e-6*a*B*e)*(d+e*x)^4/e^8+1/5*b^6*B*(d+e*x)^5/e^8-3*b*(b*d-a*e)^4*(7*b*B*d-5*A*b*e-2*a*B*e)*Log[d+e*x]/e^8");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1319
  public void test0145() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^2, x]", //
        "-5*b*(b*d-a*e)^8*(11*b*B*d-9*A*b*e-2*a*B*e)*x/e^11+(b*d-a*e)^10*(B*d-A*e)/(e^12*(d+e*x))+15/2*b^2*(b*d-a*e)^7*(11*b*B*d-8*A*b*e-3*a*B*e)*(d+e*x)^2/e^12-10*b^3*(b*d-a*e)^6*(11*b*B*d-7*A*b*e-4*a*B*e)*(d+e*x)^3/e^12+21/2*b^4*(b*d-a*e)^5*(11*b*B*d-6*A*b*e-5*a*B*e)*(d+e*x)^4/e^12-42/5*b^5*(b*d-a*e)^4*(11*b*B*d-5*A*b*e-6*a*B*e)*(d+e*x)^5/e^12+5*b^6*(b*d-a*e)^3*(11*b*B*d-4*A*b*e-7*a*B*e)*(d+e*x)^6/e^12-15/7*b^7*(b*d-a*e)^2*(11*b*B*d-3*A*b*e-8*a*B*e)*(d+e*x)^7/e^12+5/8*b^8*(b*d-a*e)*(11*b*B*d-2*A*b*e-9*a*B*e)*(d+e*x)^8/e^12-1/9*b^9*(11*b*B*d-A*b*e-10*a*B*e)*(d+e*x)^9/e^12+1/10*b^10*B*(d+e*x)^10/e^12+(b*d-a*e)^9*(11*b*B*d-10*A*b*e-a*B*e)*Log[d+e*x]/e^12");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1333
  public void test0146() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^16, x]", //
        "-1/15*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^15)+1/210*(11*b*B*d+4*A*b*e-15*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^14)+1/910*b*(11*b*B*d+4*A*b*e-15*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^3*(d+e*x)^13)+1/5460*b^2*(11*b*B*d+4*A*b*e-15*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^4*(d+e*x)^12)+1/60060*b^3*(11*b*B*d+4*A*b*e-15*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^5*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1901
  public void test0147() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)/(1-2*x)^3, x]", //
        "539/32/(1-2*x)^2+(-707/16)/(1-2*x)-45/8*x-309/16*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1915
  public void test0148() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)^2/(1-2*x)^3, x]", //
        "5929/64/(1-2*x)^2+(-1309/4)/(1-2*x)-1815/16*x-225/16*x^2-3467/16*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1929
  public void test0149() {
    check( //
        "Integrate[(2+3*x)*(3+5*x)^3/(1-2*x)^3, x]", //
        "9317/64/(1-2*x)^2+(-8349/16)/(1-2*x)-2975/16*x-375/16*x^2-2805/8*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2074
  public void test0150() {
    check( //
        "Integrate[(2+3*x)^5*(3+5*x)*Sqrt[1-2*x], x]", //
        "-184877/192*(1-2*x)^(3/2)+12005/8*(1-2*x)^(5/2)-74235/64*(1-2*x)^(7/2)+4165/8*(1-2*x)^(9/2)-97335/704*(1-2*x)^(11/2)+81/4*(1-2*x)^(13/2)-81/64*(1-2*x)^(15/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2088
  public void test0151() {
    check( //
        "Integrate[(2+3*x)^2*(3+5*x)^2*Sqrt[1-2*x], x]", //
        "-5929/48*(1-2*x)^(3/2)+1309/10*(1-2*x)^(5/2)-3467/56*(1-2*x)^(7/2)+85/6*(1-2*x)^(9/2)-225/176*(1-2*x)^(11/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2102
  public void test0152() {
    check( //
        "Integrate[(3+5*x)^3*Sqrt[1-2*x], x]", //
        "-1331/24*(1-2*x)^(3/2)+363/8*(1-2*x)^(5/2)-825/56*(1-2*x)^(7/2)+125/72*(1-2*x)^(9/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2118
  public void test0153() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)*(3+5*x)), x]", //
        "-2*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2164
  public void test0154() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^2/(2+3*x), x]", //
        "2/81*(1-2*x)^(3/2)-31/18*(1-2*x)^(5/2)+25/42*(1-2*x)^(7/2)-14/81*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]+14/81*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2270
  public void test0155() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)*(3+5*x)), x]", //
        "-4/45*(1-2*x)^(3/2)-242/25*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+98/9*ArcTanh[Sqrt[3/7]*Sqrt[1-2*x]]*Sqrt[7/3]-272/225*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2562
  public void test0156() {
    check( //
        "Integrate[(A+B*x)/(Sqrt[a+b*x]*Sqrt[d+e*x]), x]", //
        "(2*A*b*e-B*(b*d+a*e))*ArcTanh[Sqrt[e]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[d+e*x])]/(b^(3/2)*e^(3/2))+B*Sqrt[a+b*x]*Sqrt[d+e*x]/(b*e)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2594
  public void test0157() {
    check( //
        "Integrate[Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x), x]", //
        "2/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+37/9*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1/3*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2608
  public void test0158() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^3, x]", //
        "-4091/756*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-10/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-1/6*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-107/252*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2622
  public void test0159() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^5, x]", //
        "-73205/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-605/4704*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-11/168*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3+1/4*(3+5*x)^(7/2)*Sqrt[1-2*x]/(2+3*x)^4-6655/21952*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2638
  public void test0160() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^5*Sqrt[3+5*x]), x]", //
        "-16925425/21952*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/4*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+81/56*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+14145/1568*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+1479375/21952*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2652
  public void test0161() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(5/2)), x]", //
        "-10/33*(1-2*x)^(3/2)/(3+5*x)^(3/2)-6*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+6*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2700
  public void test0162() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x]), x]", //
        "-103/45*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-14/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-2/15*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2714
  public void test0163() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^5*(3+5*x)^(3/2)), x]", //
        "145708761/3136*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-63678595/9408*Sqrt[1-2*x]/Sqrt[3+5*x]+7/12*Sqrt[1-2*x]/((2+3*x)^4*Sqrt[3+5*x])+33/8*Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x])+8063/224*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+1403963/3136*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2732
  public void test0164() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^2, x]", //
        "-35/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-2119/90*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-1/3*(1-2*x)^(3/2)*Sqrt[3+5*x]-1/3*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)-43/30*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2746
  public void test0165() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^5, x]", //
        "-1/12*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^4+115/216*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-3244595/108864*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-40/243*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+2675/864*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-97235/36288*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2760
  public void test0166() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^7, x]", //
        "1/6*(1-2*x)^(5/2)*(3+5*x)^(7/2)/(2+3*x)^6+11/12*(1-2*x)^(3/2)*(3+5*x)^(7/2)/(2+3*x)^5-8857805/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-73205/37632*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1331/1344*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3+121/32*(3+5*x)^(7/2)*Sqrt[1-2*x]/(2+3*x)^4-805255/175616*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2776
  public void test0167() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^7*Sqrt[3+5*x]), x]", //
        "-13391796605/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/18*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^6+497/108*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+21199/864*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+1729615/12096*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+302171615/338688*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+31603880465/4741632*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2808
  public void test0168() {
    check( //
        "Integrate[Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x]), x]", //
        "2/3*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/3*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2822
  public void test0169() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^6*Sqrt[1-2*x]), x]", //
        "-2664057/307328*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/105*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5-367/5880*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-73/11760*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+6107/65856*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+694229/921984*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2838
  public void test0170() {
    check( //
        "Integrate[(2+3*x)^2/(Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "3827/400*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-333/400*Sqrt[1-2*x]*Sqrt[3+5*x]-3/20*(2+3*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2852
  public void test0171() {
    check( //
        "Integrate[1/((2+3*x)^3*(3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "56421/196*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-90415/2156*Sqrt[1-2*x]/Sqrt[3+5*x]+3/14*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+543/196*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2870
  public void test0172() {
    check( //
        "Integrate[(2+3*x)^4*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "-92108287/51200*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+(2+3*x)^4*Sqrt[3+5*x]/Sqrt[1-2*x]+2203/320*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+27/16*(2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x]+1/51200*(11129753+4618500*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2898
  public void test0173() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^4), x]", //
        "-1815/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77*(3+5*x)^(7/2)/((2+3*x)^3*Sqrt[1-2*x])-5/196*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1/77*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-165/2744*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2914
  public void test0174() {
    check( //
        "Integrate[(2+3*x)^5/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "-2911419/16000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^4/(Sqrt[1-2*x]*Sqrt[3+5*x])-37/605*(2+3*x)^3*Sqrt[1-2*x]/Sqrt[3+5*x]+8463/12100*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+21/1936000*(2027201+841380*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2928
  public void test0175() {
    check( //
        "Integrate[(2+3*x)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "7/11/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/363*Sqrt[1-2*x]/(3+5*x)^(3/2)-428/3993*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2960
  public void test0176() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)), x]", //
        "11/21*(3+5*x)^(3/2)/(1-2*x)^(3/2)+25/6*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+2/147*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-407/98*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2976
  public void test0177() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^4*Sqrt[3+5*x]), x]", //
        "-330255/19208*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-101485/45276*Sqrt[3+5*x]/(1-2*x)^(3/2)+1/7*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^3)+193/196*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^2)+423/56*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x))-3471145/3486252*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2990
  public void test0178() {
    check( //
        "Integrate[(2+3*x)^2/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "49/66/((1-2*x)^(3/2)*(3+5*x)^(3/2))+14/121/((3+5*x)^(3/2)*Sqrt[1-2*x])-1649/7986*Sqrt[1-2*x]/(3+5*x)^(3/2)-3298/43923*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3012
  public void test0179() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x], x]", //
        "-2911577/590625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-175111/1181250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-23/1575*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+2/45*(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-1244/13125*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-175111/236250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3026
  public void test0180() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2), x]", //
        "-8314/6615*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+824/6615*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/15*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)-214/945*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+8314/6615*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3042
  public void test0181() {
    check( //
        "Integrate[Sqrt[e+f*x]/((a+b*x)^(3/2)*Sqrt[c+d*x]), x]", //
        "-2*Sqrt[c+d*x]*Sqrt[e+f*x]/((b*c-a*d)*Sqrt[a+b*x])+2*EllipticE[ArcSin[Sqrt[f]*Sqrt[a+b*x]/Sqrt[-b*e+a*f]],d*(b*e-a*f)/((b*c-a*d)*f)]*Sqrt[f]*Sqrt[-b*e+a*f]*Sqrt[c+d*x]*Sqrt[b*(e+f*x)/(b*e-a*f)]/(b*(b*c-a*d)*Sqrt[b*(c+d*x)/(b*c-a*d)]*Sqrt[e+f*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3056
  public void test0182() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "4*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+2*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-20*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3074
  public void test0183() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x], x]", //
        "-118898/118125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2657/118125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/35*(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+194/2625*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2657/23625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3088
  public void test0184() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(9/2), x]", //
        "-2/21*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(7/2)-119732/46305*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-7388/46305*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+74/105*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)-3632/6615*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+119732/46305*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3104
  public void test0185() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)/Sqrt[3+5*x], x]", //
        "-6515539/5906250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-104663/2953125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/45*(1-2*x)^(3/2)*(2+3*x)^(5/2)*Sqrt[3+5*x]+403/118125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+178/4725*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-87476/590625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3118
  public void test0186() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "532/3*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+16/3*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/9*Sqrt[1-2*x]/((2+3*x)^(3/2)*Sqrt[3+5*x])+88/3*Sqrt[1-2*x]/(Sqrt[2+3*x]*Sqrt[3+5*x])-2660/9*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3136
  public void test0187() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[3+5*x]/Sqrt[2+3*x], x]", //
        "-86741/39375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11806/39375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+118/525*(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]+2/21*(1-2*x)^(5/2)*Sqrt[2+3*x]*Sqrt[3+5*x]+4282/7875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3150
  public void test0188() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(9/2), x]", //
        "-2/21*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(7/2)+46/63*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(5/2)-11576/3969*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4244/3969*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+608/189*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)-4244/3969*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3164
  public void test0189() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(17/2), x]", //
        "-2/45*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(15/2)+74/351*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(13/2)-12641611554328/16724393595*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-380220959152/16724393595*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1085156/729729*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)+16636/11583*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(11/2)-112817764/107270163*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+3914701972/3754455705*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+181941877952/26281189935*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+12641611554328/183968329545*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3180
  public void test0190() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[2+3*x]/(3+5*x)^(3/2), x]", //
        "81164/28125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-28174/28125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(5/2)*Sqrt[2+3*x]/Sqrt[3+5*x]-24/125*(1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]-3028/5625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3194
  public void test0191() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(7/2)*(3+5*x)^(5/2)), x]", //
        "14/15*(1-2*x)^(3/2)/((2+3*x)^(5/2)*(3+5*x)^(3/2))-96808/5*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2912/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+1232/45*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+35948/45*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-16016/3*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+96808/3*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3212
  public void test0192() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "272/441*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-202/441*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/63*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-272/441*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3244
  public void test0193() {
    check( //
        "Integrate[1/((a+b*x)^(3/2)*Sqrt[c+d*x]*Sqrt[e+f*x]), x]", //
        "-2*b*Sqrt[c+d*x]*Sqrt[e+f*x]/((b*c-a*d)*(b*e-a*f)*Sqrt[a+b*x])+2*EllipticE[ArcSin[Sqrt[f]*Sqrt[c+d*x]/Sqrt[-d*e+c*f]],-b*(d*e-c*f)/((b*c-a*d)*f)]*Sqrt[f]*Sqrt[-d*e+c*f]*Sqrt[a+b*x]*Sqrt[d*(e+f*x)/(d*e-c*f)]/((b*c-a*d)*(b*e-a*f)*Sqrt[-d*(a+b*x)/(b*c-a*d)]*Sqrt[e+f*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3258
  public void test0194() {
    check( //
        "Integrate[1/((3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]), x]", //
        "2*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-10/11*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3296
  public void test0195() {
    check( //
        "Integrate[(2+3*x)^(7/2)*(3+5*x)^(3/2)/(1-2*x)^(3/2), x]", //
        "112543103/78750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+6770629/157500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(2+3*x)^(7/2)*(3+5*x)^(3/2)/Sqrt[1-2*x]+1397/210*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+5/3*(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+24358/875*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+6770629/31500*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3310
  public void test0196() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)^(3/2)), x]", //
        "1159/147*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+31/147*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+11/7*(3+5*x)^(3/2)/(Sqrt[1-2*x]*Sqrt[2+3*x])+31/147*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3326
  public void test0197() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2)), x]", //
        "55019/2750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+823/1375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+7/11*(2+3*x)^(5/2)/(Sqrt[1-2*x]*Sqrt[3+5*x])-37/605*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+2388/3025*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3340
  public void test0198() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-3896/847*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-164/847*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/77*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-410/2541*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+19480/27951*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3358
  public void test0199() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(3/2)/(1-2*x)^(5/2), x]", //
        "1/3*(2+3*x)^(5/2)*(3+5*x)^(3/2)/(1-2*x)^(3/2)-2077/50*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-37663/100*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-45/11*(2+3*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[1-2*x]-807/110*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-6231/110*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3372
  public void test0200() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*(2+3*x)^(7/2)), x]", //
        "11/21*(3+5*x)^(3/2)/((1-2*x)^(3/2)*(2+3*x)^(5/2))-7738/84035*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+9206/84035*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+99/49*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-1432/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-4437/12005*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-27618/84035*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3388
  public void test0201() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "-974/605*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-41/605*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(3/2)/((1-2*x)^(3/2)*Sqrt[3+5*x])-203/363*Sqrt[2+3*x]/(Sqrt[1-2*x]*Sqrt[3+5*x])+974/3993*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3402
  public void test0202() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-119732/65219*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-7388/65219*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[2+3*x]/((1-2*x)^(3/2)*(3+5*x)^(3/2))+368/5929*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-18470/195657*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+598660/2152227*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3473
  public void test0203() {
    check( //
        "Integrate[(a+b*x)^(4/3)/((c+d*x)^(4/3)*(e+f*x)^3), x]", //
        "3*d*(a+b*x)^(7/3)/((b*c-a*d)*(d*e-c*f)*(c+d*x)^(1/3)*(e+f*x)^2)-1/2*(6*b*d*e+b*c*f-7*a*d*f)*(a+b*x)^(4/3)*(c+d*x)^(2/3)/((b*c-a*d)*(d*e-c*f)^2*(e+f*x)^2)+2/3*(6*b*d*e+b*c*f-7*a*d*f)*(a+b*x)^(1/3)*(c+d*x)^(2/3)/((d*e-c*f)^3*(e+f*x))-1/9*(b*c-a*d)*(6*b*d*e+b*c*f-7*a*d*f)*Log[e+f*x]/((b*e-a*f)^(2/3)*(d*e-c*f)^(10/3))+1/3*(b*c-a*d)*(6*b*d*e+b*c*f-7*a*d*f)*Log[-(a+b*x)^(1/3)+(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/(d*e-c*f)^(1/3)]/((b*e-a*f)^(2/3)*(d*e-c*f)^(10/3))+2/3*(b*c-a*d)*(6*b*d*e+b*c*f-7*a*d*f)*ArcTan[1/Sqrt[3]+2*(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/((d*e-c*f)^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]/((b*e-a*f)^(2/3)*(d*e-c*f)^(10/3)*Sqrt[3])");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:44
  public void test0204() {
    check( //
        "Integrate[(1+a*x)/(x^4*Sqrt[a*x]*Sqrt[1-a*x]), x]", //
        "-2/7*a^3*Sqrt[1-a*x]/(a*x)^(7/2)-26/35*a^3*Sqrt[1-a*x]/(a*x)^(5/2)-104/105*a^3*Sqrt[1-a*x]/(a*x)^(3/2)-208/105*a^3*Sqrt[1-a*x]/Sqrt[a*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:80
  public void test0205() {
    check( //
        "Integrate[(7+5*x)^3*Sqrt[2-3*x]/(Sqrt[-5+2*x]*Sqrt[1+4*x]), x]", //
        "-25260049/6048*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+15629623/9072*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+110743/864*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+121/24*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+5/28*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:132
  public void test0206() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[1+4*x]/((7+5*x)^(7/2)*Sqrt[-5+2*x]), x]", //
        "2/195*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(5/2)-3646/16267095*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)-20464840/90467822133*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]+8185936/90467822133*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]+111628/74828637*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[11/23]*Sqrt[7+5*x]/(Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])-4092968/2319687747*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.1.6 P(x) (a+b x)^m (c+d x)^n (e+f x)^p.input:25
  public void test0207() {
    check( //
        "Integrate[(A+B*x+C*x^2)/((e+f*x)*Sqrt[1-d*x]*Sqrt[1+d*x]), x]", //
        "-(C*e-B*f)*ArcSin[d*x]/(d*f^2)+(C*e^2-B*e*f+A*f^2)*ArcTan[(f+d^2*e*x)/(Sqrt[d^2*e^2-f^2]*Sqrt[1-d^2*x^2])]/(f^2*Sqrt[d^2*e^2-f^2])-C*Sqrt[1-d^2*x^2]/(d^2*f)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:34
  public void test0208() {
    check( //
        "Integrate[(a+b*x^2)^2/x^5, x]", //
        "-1/4*a^2/x^4-a*b/x^2+b^2*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:48
  public void test0209() {
    check( //
        "Integrate[(a+b*x^2)^3/x^7, x]", //
        "-1/6*a^3/x^6-3/4*a^2*b/x^4-3/2*a*b^2/x^2+b^3*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:104
  public void test0210() {
    check( //
        "Integrate[(a+b*x^2)^8/x^3, x]", //
        "-1/2*a^8/x^2+14*a^6*b^2*x^2+14*a^5*b^3*x^4+35/3*a^4*b^4*x^6+7*a^3*b^5*x^8+14/5*a^2*b^6*x^10+2/3*a*b^7*x^12+1/14*b^8*x^14+8*a^7*b*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:118
  public void test0211() {
    check( //
        "Integrate[(a+b*x^2)^8/x^31, x]", //
        "-1/30*a^8/x^30-2/7*a^7*b/x^28-14/13*a^6*b^2/x^26-7/3*a^5*b^3/x^24-35/11*a^4*b^4/x^22-14/5*a^3*b^5/x^20-14/9*a^2*b^6/x^18-1/2*a*b^7/x^16-1/14*b^8/x^14");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:176
  public void test0212() {
    check( //
        "Integrate[1/(x^5*(a+b*x^2)^2), x]", //
        "(-1/4)/(a^2*x^4)+b/(a^3*x^2)+1/2*b^2/(a^3*(a+b*x^2))+3*b^2*Log[x]/a^4-3/2*b^2*Log[a+b*x^2]/a^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:190
  public void test0213() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)^3), x]", //
        "(-1/2)/(a^3*x^2)-1/4*b/(a^2*(a+b*x^2)^2)-b/(a^3*(a+b*x^2))-3*b*Log[x]/a^4+3/2*b*Log[a+b*x^2]/a^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:278
  public void test0214() {
    check( //
        "Integrate[1/(a+(b-a*c)*x^2), x]", //
        "ArcTan[x*Sqrt[b-a*c]/Sqrt[a]]/(Sqrt[a]*Sqrt[b-a*c])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:474
  public void test0215() {
    check( //
        "Integrate[(a+b*x^2)^(9/2)/x^22, x]", //
        "-1/21*(a+b*x^2)^(11/2)/(a*x^21)+10/399*b*(a+b*x^2)^(11/2)/(a^2*x^19)-80/6783*b^2*(a+b*x^2)^(11/2)/(a^3*x^17)+32/6783*b^3*(a+b*x^2)^(11/2)/(a^4*x^15)-128/88179*b^4*(a+b*x^2)^(11/2)/(a^5*x^13)+256/969969*b^5*(a+b*x^2)^(11/2)/(a^6*x^11)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:516
  public void test0216() {
    check( //
        "Integrate[Sqrt[-9-4*x^2]/x^2, x]", //
        "-2*ArcTan[2*x/Sqrt[-9-4*x^2]]-Sqrt[-9-4*x^2]/x");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:727
  public void test0217() {
    check( //
        "Integrate[(a+b*x^2)^(1/3), x]", //
        "3/5*x*(a+b*x^2)^(1/3)-2/5*3^(3/4)*a*(a^(1/3)-(a+b*x^2)^(1/3))*EllipticF[ArcSin[(-(a+b*x^2)^(1/3)+a^(1/3)*(1+Sqrt[3]))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*Sqrt[(a^(2/3)+a^(1/3)*(a+b*x^2)^(1/3)+(a+b*x^2)^(2/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2-Sqrt[3]]/(b*x*Sqrt[-a^(1/3)*(a^(1/3)-(a+b*x^2)^(1/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:803
  public void test0218() {
    check( //
        "Integrate[(a+b*x^2)^(1/3)/(c*x)^(23/3), x]", //
        "-3/8*(a+b*x^2)^(4/3)/(a*c*(c*x)^(20/3))+9/28*(a+b*x^2)^(7/3)/(a^2*c*(c*x)^(20/3))-27/280*(a+b*x^2)^(10/3)/(a^3*c*(c*x)^(20/3))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:820
  public void test0219() {
    check( //
        "Integrate[(a+b*x^2)^(4/3)/(c*x)^(29/3), x]", //
        "-3/14*(a+b*x^2)^(7/3)/(a*c*(c*x)^(26/3))+9/70*(a+b*x^2)^(10/3)/(a^2*c*(c*x)^(26/3))-27/910*(a+b*x^2)^(13/3)/(a^3*c*(c*x)^(26/3))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:839
  public void test0220() {
    check( //
        "Integrate[1/((c*x)^(23/3)*(a+b*x^2)^(2/3)), x]", //
        "-3/2*(a+b*x^2)^(1/3)/(a*c*(c*x)^(20/3))+27/8*(a+b*x^2)^(4/3)/(a^2*c*(c*x)^(20/3))-81/28*(a+b*x^2)^(7/3)/(a^3*c*(c*x)^(20/3))+243/280*(a+b*x^2)^(10/3)/(a^4*c*(c*x)^(20/3))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:862
  public void test0221() {
    check( //
        "Integrate[(a-b*x^2)^(1/4), x]", //
        "2/3*x*(a-b*x^2)^(1/4)+2/3*a^(3/2)*(1-b*x^2/a)^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1066
  public void test0222() {
    check( //
        "Integrate[(c*x)^(13/2)/(a+b*x^2)^(5/4), x]", //
        "77/60*a^2*c^5*(c*x)^(3/2)/(b^3*(a+b*x^2)^(1/4))-11/30*a*c^3*(c*x)^(7/2)/(b^2*(a+b*x^2)^(1/4))+1/5*c*(c*x)^(11/2)/(b*(a+b*x^2)^(1/4))+77/20*a^(5/2)*c^6*(1+a/(b*x^2))^(1/4)*EllipticE[1/2*ArcCot[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[c*x]/(b^(7/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:39
  public void test0223() {
    check( //
        "Integrate[(c+d*x^2)^4/(a+b*x^2)^2, x]", //
        "d^2*(6*b^2*c^2-8*a*b*c*d+3*a^2*d^2)*x/b^4+2/3*d^3*(2*b*c-a*d)*x^3/b^3+1/5*d^4*x^5/b^2+1/2*(b*c-a*d)^4*x/(a*b^4*(a+b*x^2))+1/2*(b*c-a*d)^3*(b*c+7*a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*b^(9/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:71
  public void test0224() {
    check( //
        "Integrate[(a+b*x^2)^(3/2), x]", //
        "1/4*x*(a+b*x^2)^(3/2)+3/8*a^2*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+3/8*a*x*Sqrt[a+b*x^2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:85
  public void test0225() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)/(c+d*x^2)^5, x]", //
        "-1/8*d*x*(a+b*x^2)^(7/2)/(c*(b*c-a*d)*(c+d*x^2)^4)+1/48*(8*b*c-7*a*d)*x*(a+b*x^2)^(5/2)/(c^2*(b*c-a*d)*(c+d*x^2)^3)+5/192*a*(8*b*c-7*a*d)*x*(a+b*x^2)^(3/2)/(c^3*(b*c-a*d)*(c+d*x^2)^2)+5/128*a^3*(8*b*c-7*a*d)*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(9/2)*(b*c-a*d)^(3/2))+5/128*a^2*(8*b*c-7*a*d)*x*Sqrt[a+b*x^2]/(c^4*(b*c-a*d)*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:102
  public void test0226() {
    check( //
        "Integrate[1/(a+b*x^2)^(3/2), x]", //
        "x/(a*Sqrt[a+b*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:182
  public void test0227() {
    check( //
        "Integrate[1/((-2+b*x^2)^(1/3)*(-18*d/b+d*x^2)), x]", //
        "-1/12*ArcTanh[1/3*(2^(1/3)+(-2+b*x^2)^(1/3))^2/(2^(1/6)*x*Sqrt[b])]*Sqrt[b]/(2^(5/6)*d)+1/12*ArcTanh[1/3*x*Sqrt[b]/Sqrt[2]]*Sqrt[b]/(2^(5/6)*d)+1/4*ArcTan[2^(1/6)*(2^(1/3)+(-2+b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(2^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:308
  public void test0228() {
    check( //
        "Integrate[Sqrt[-a+b*x^2]/Sqrt[c+d*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[b]*Sqrt[1-b*x^2/a]*Sqrt[c+d*x^2]/(d*Sqrt[-a+b*x^2]*Sqrt[1+d*x^2/c])-(b*c+a*d)*EllipticF[ArcSin[x*Sqrt[b]/Sqrt[a]],-a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[1+d*x^2/c]/(d*Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:322
  public void test0229() {
    check( //
        "Integrate[Sqrt[-c-d*x^2]/Sqrt[-a-b*x^2], x]", //
        "d*x*Sqrt[-a-b*x^2]/(b*Sqrt[-c-d*x^2])+c^(3/2)*EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[-a-b*x^2]/(a*Sqrt[d]*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[-a-b*x^2]/(b*Sqrt[-c-d*x^2]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:336
  public void test0230() {
    check( //
        "Integrate[Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]/Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])], x]", //
        "x*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]/Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])]-EllipticE[ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]],-2*Sqrt[b^2-4*a*c]/(b-Sqrt[b^2-4*a*c])]*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[(1+2*c*x^2/(b-Sqrt[b^2-4*a*c]))/(1+2*c*x^2/(b+Sqrt[b^2-4*a*c]))]*Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])])+EllipticF[ArcTan[x*Sqrt[2]*Sqrt[c]/Sqrt[b+Sqrt[b^2-4*a*c]]],-2*Sqrt[b^2-4*a*c]/(b-Sqrt[b^2-4*a*c])]*Sqrt[1+2*c*x^2/(b-Sqrt[b^2-4*a*c])]*Sqrt[b+Sqrt[b^2-4*a*c]]/(Sqrt[2]*Sqrt[c]*Sqrt[(1+2*c*x^2/(b-Sqrt[b^2-4*a*c]))/(1+2*c*x^2/(b+Sqrt[b^2-4*a*c]))]*Sqrt[1+2*c*x^2/(b+Sqrt[b^2-4*a*c])])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:32
  public void test0231() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^8, x]", //
        "-1/7*a^2*A/x^7-1/5*a*(2*A*b+a*B)/x^5-1/3*b*(A*b+2*a*B)/x^3-b^2*B/x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:46
  public void test0232() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^3, x]", //
        "-1/2*a^5*A/x^2+5/2*a^3*b*(2*A*b+a*B)*x^2+5/2*a^2*b^2*(A*b+a*B)*x^4+5/6*a*b^3*(A*b+2*a*B)*x^6+1/8*b^4*(A*b+5*a*B)*x^8+1/10*b^5*B*x^10+a^4*(5*A*b+a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:60
  public void test0233() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^17, x]", //
        "-1/16*A*(a+b*x^2)^6/(a*x^16)+1/56*(A*b-4*a*B)*(a+b*x^2)^6/(a^2*x^14)-1/336*b*(A*b-4*a*B)*(a+b*x^2)^6/(a^3*x^12)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:146
  public void test0234() {
    check( //
        "Integrate[(a*c+b*c*x^2)/(x^3*(a+b*x^2)^2), x]", //
        "-1/2*c/(a*x^2)-b*c*Log[x]/a^2+1/2*b*c*Log[a+b*x^2]/a^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:164
  public void test0235() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^2, x]", //
        "-a^2*c/x+a*(2*b*c+a*d)*x+1/3*b*(b*c+2*a*d)*x^3+1/5*b^2*d*x^5");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:208
  public void test0236() {
    check( //
        "Integrate[x^4*(a+b*x^2)^2/(c+d*x^2)^3, x]", //
        "-1/4*(13*b^2*c^2-10*a*b*c*d+a^2*d^2)*x/(c*d^4)+1/3*b^2*x^3/d^3+1/4*(b*c-a*d)^2*x^5/(c*d^2*(c+d*x^2)^2)-1/8*(b*c-a*d)*(9*b*c-a*d)*x/(d^4*(c+d*x^2))+1/8*(35*b^2*c^2-30*a*b*c*d+3*a^2*d^2)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(d^(9/2)*Sqrt[c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:258
  public void test0237() {
    check( //
        "Integrate[x^2/((a+b*x^2)*(c+d*x^2)), x]", //
        "-ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/((b*c-a*d)*Sqrt[b])+ArcTan[x*Sqrt[d]/Sqrt[c]]*Sqrt[c]/((b*c-a*d)*Sqrt[d])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:272
  public void test0238() {
    check( //
        "Integrate[x/((a+b*x^2)*(c+d*x^2)^2), x]", //
        "1/2/((b*c-a*d)*(c+d*x^2))+1/2*b*Log[a+b*x^2]/(b*c-a*d)^2-1/2*b*Log[c+d*x^2]/(b*c-a*d)^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:304
  public void test0239() {
    check( //
        "Integrate[x^2*(c+d*x^2)^2/(a+b*x^2)^2, x]", //
        "-1/2*(b*c-5*a*d)*(b*c-a*d)*x/(a*b^3)+1/3*d^2*x^3/b^2+1/2*(b*c-a*d)^2*x^3/(a*b^2*(a+b*x^2))+1/2*(b*c-5*a*d)*(b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(7/2)*Sqrt[a])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:348
  public void test0240() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^2*(c+d*x^2)^3), x]", //
        "1/2*b^3/(a*(b*c-a*d)^3*(a+b*x^2))+1/4*d^2/(c*(b*c-a*d)^2*(c+d*x^2)^2)+1/2*d^2*(3*b*c-a*d)/(c^2*(b*c-a*d)^3*(c+d*x^2))+Log[x]/(a^2*c^3)-1/2*b^3*(b*c-4*a*d)*Log[a+b*x^2]/(a^2*(b*c-a*d)^4)-1/2*d^2*(6*b^2*c^2-4*a*b*c*d+a^2*d^2)*Log[c+d*x^2]/(c^3*(b*c-a*d)^4)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:394
  public void test0241() {
    check( //
        "Integrate[(a+b*x^2)*(A+B*x^2)/Sqrt[x], x]", //
        "2/5*(A*b+a*B)*x^(5/2)+2/9*b*B*x^(9/2)+2*a*A*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:456
  public void test0242() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/Sqrt[x], x]", //
        "4/5*a*c*(b*c+a*d)*x^(5/2)+2/9*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^(9/2)+4/13*b*d*(b*c+a*d)*x^(13/2)+2/17*b^2*d^2*x^(17/2)+2*a^2*c^2*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:472
  public void test0243() {
    check( //
        "Integrate[x^(3/2)*(a+b*x^2)^2/(c+d*x^2), x]", //
        "-2/5*b*(b*c-2*a*d)*x^(5/2)/d^2+2/9*b^2*x^(9/2)/d+c^(1/4)*(b*c-a*d)^2*ArcTan[1-d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(d^(13/4)*Sqrt[2])-c^(1/4)*(b*c-a*d)^2*ArcTan[1+d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(d^(13/4)*Sqrt[2])+1/2*c^(1/4)*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]-c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(d^(13/4)*Sqrt[2])-1/2*c^(1/4)*(b*c-a*d)^2*Log[Sqrt[c]+x*Sqrt[d]+c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(d^(13/4)*Sqrt[2])+2*(b*c-a*d)^2*Sqrt[x]/d^3");
  }

  // 1.1.2.8 P(x) (c x)^m (a+b x^2)^p.input:155
  public void test0244() {
    check( //
        "Integrate[(-x+4*x^3)/(5+x^2)^2, x]", //
        "21/2/(5+x^2)+2*Log[5+x^2]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:155
  public void test0245() {
    check( //
        "Integrate[1/(b*x)^(1/3), x]", //
        "3/2*(b*x)^(2/3)/b");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2269
  public void test0246() {
    check( //
        "Integrate[(1+1/x^2)^(5/3)/x^3, x]", //
        "-3/16*(1+1/x^2)^(8/3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2349
  public void test0247() {
    check( //
        "Integrate[1/(x^4*Sqrt[a+b/x^3]), x]", //
        "-2/3*Sqrt[a+b/x^3]/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:37
  public void test0248() {
    check( //
        "Integrate[1/x^(3/2), x]", //
        "(-2)/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:179
  public void test0249() {
    check( //
        "Integrate[(a+b*x)^10/x^16, x]", //
        "-1/15*(a+b*x)^11/(a*x^15)+2/105*b*(a+b*x)^11/(a^2*x^14)-2/455*b^2*(a+b*x)^11/(a^3*x^13)+1/1365*b^3*(a+b*x)^11/(a^4*x^12)-1/15015*b^4*(a+b*x)^11/(a^5*x^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:317
  public void test0250() {
    check( //
        "Integrate[1/(1/a+x*Sqrt[-a]), x]", //
        "Log[1-(-a)^(3/2)*x]/Sqrt[-a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:397
  public void test0251() {
    check( //
        "Integrate[1/(a+b*x)^(3/2), x]", //
        "(-2)/(b*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:667
  public void test0252() {
    check( //
        "Integrate[1/((a-b*x)^(3/2)*Sqrt[x]), x]", //
        "2*Sqrt[x]/(a*Sqrt[a-b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:695
  public void test0253() {
    check( //
        "Integrate[1/(x^(3/2)*(2+b*x)^(5/2)), x]", //
        "1/3/((2+b*x)^(3/2)*Sqrt[x])+2/3/(Sqrt[x]*Sqrt[2+b*x])-2/3*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:855
  public void test0254() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x), x]", //
        "1/4*a*c*x^3*Sqrt[c*x^2]+1/5*b*c*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:871
  public void test0255() {
    check( //
        "Integrate[x^2*(a+b*x)/Sqrt[c*x^2], x]", //
        "1/2*a*x^3/Sqrt[c*x^2]+1/3*b*x^4/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:885
  public void test0256() {
    check( //
        "Integrate[(a+b*x)/(x^4*(c*x^2)^(3/2)), x]", //
        "-1/6*a/(c*x^5*Sqrt[c*x^2])-1/5*b/(c*x^4*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:903
  public void test0257() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[c*x^2]/x^2, x]", //
        "2*a*b*Sqrt[c*x^2]+1/2*b^2*x*Sqrt[c*x^2]+a^2*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:917
  public void test0258() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x^2, x]", //
        "1/4*a^2*c^2*x^3*Sqrt[c*x^2]+2/5*a*b*c^2*x^4*Sqrt[c*x^2]+1/6*b^2*c^2*x^5*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:933
  public void test0259() {
    check( //
        "Integrate[x^2*(a+b*x)^2/(c*x^2)^(3/2), x]", //
        "2*a*b*x^2/(c*Sqrt[c*x^2])+1/2*b^2*x^3/(c*Sqrt[c*x^2])+a^2*x*Log[x]/(c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:947
  public void test0260() {
    check( //
        "Integrate[(a+b*x)^2/(x^4*(c*x^2)^(5/2)), x]", //
        "-1/8*a^2/(c^2*x^7*Sqrt[c*x^2])-2/7*a*b/(c^2*x^6*Sqrt[c*x^2])-1/6*b^2/(c^2*x^5*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:965
  public void test0261() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^4*(a+b*x)), x]", //
        "c*Log[x]*Sqrt[c*x^2]/(a*x)-c*Log[a+b*x]*Sqrt[c*x^2]/(a*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:981
  public void test0262() {
    check( //
        "Integrate[x^2/((a+b*x)*Sqrt[c*x^2]), x]", //
        "x^2/(b*Sqrt[c*x^2])-a*x*Log[a+b*x]/(b^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1013
  public void test0263() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^5*(a+b*x)^2), x]", //
        "-c*Sqrt[c*x^2]/(a^2*x^2)-b*c*Sqrt[c*x^2]/(a^2*x*(a+b*x))-2*b*c*Log[x]*Sqrt[c*x^2]/(a^3*x)+2*b*c*Log[a+b*x]*Sqrt[c*x^2]/(a^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1029
  public void test0264() {
    check( //
        "Integrate[x/((c*x^2)^(3/2)*(a+b*x)^2), x]", //
        "(-1)/(a^2*c*Sqrt[c*x^2])-b*x/(a^2*c*(a+b*x)*Sqrt[c*x^2])-2*b*x*Log[x]/(a^3*c*Sqrt[c*x^2])+2*b*x*Log[a+b*x]/(a^3*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1053
  public void test0265() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n/x^3, x]", //
        "a^2*c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^3*(1+n)*x)-2*a*c^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^3*(2+n)*x)+c^2*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^3*(3+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1140
  public void test0266() {
    check( //
        "Integrate[1/((a+b*x)*(a*d/b+d*x)^3), x]", //
        "-1/3*b^2/(d^3*(a+b*x)^3)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1224
  public void test0267() {
    check( //
        "Integrate[(1+x)^(1/2)/(1-x)^(13/2), x]", //
        "1/11*(1+x)^(3/2)/(1-x)^(11/2)+4/99*(1+x)^(3/2)/(1-x)^(9/2)+4/231*(1+x)^(3/2)/(1-x)^(7/2)+8/1155*(1+x)^(3/2)/(1-x)^(5/2)+8/3465*(1+x)^(3/2)/(1-x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1252
  public void test0268() {
    check( //
        "Integrate[(1+x)^(5/2)/(1-x)^(17/2), x]", //
        "1/15*(1+x)^(7/2)/(1-x)^(15/2)+4/195*(1+x)^(7/2)/(1-x)^(13/2)+4/715*(1+x)^(7/2)/(1-x)^(11/2)+8/6435*(1+x)^(7/2)/(1-x)^(9/2)+8/45045*(1+x)^(7/2)/(1-x)^(7/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1284
  public void test0269() {
    check( //
        "Integrate[(1-x)^(1/2)/(1+x)^(5/2), x]", //
        "-1/3*(1-x)^(3/2)/(1+x)^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1300
  public void test0270() {
    check( //
        "Integrate[1/((a+a*x)^(9/2)*(c-c*x)^(9/2)), x]", //
        "1/7*x/(a*c*(a+a*x)^(7/2)*(c-c*x)^(7/2))+6/35*x/(a^2*c^2*(a+a*x)^(5/2)*(c-c*x)^(5/2))+8/35*x/(a^3*c^3*(a+a*x)^(3/2)*(c-c*x)^(3/2))+16/35*x/(a^4*c^4*Sqrt[a+a*x]*Sqrt[c-c*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1392
  public void test0271() {
    check( //
        "Integrate[1/((a-I*a*x)^(11/4)*(a+I*a*x)^(9/4)), x]", //
        "(-2/7*I)/(a^2*(a-I*a*x)^(7/4)*(a+I*a*x)^(5/4))+(-4/7*I)/(a^3*(a-I*a*x)^(3/4)*(a+I*a*x)^(5/4))+16/35*I*(a-I*a*x)^(1/4)/(a^4*(a+I*a*x)^(5/4))+32/35*I*(a-I*a*x)^(1/4)/(a^5*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1660
  public void test0272() {
    check( //
        "Integrate[1/(c+d*x)^(3/2), x]", //
        "(-2)/(d*Sqrt[c+d*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1738
  public void test0273() {
    check( //
        "Integrate[1/((a+b*x)^(7/2)*(c+d*x)^(1/2)), x]", //
        "-2/5*Sqrt[c+d*x]/((b*c-a*d)*(a+b*x)^(5/2))+8/15*d*Sqrt[c+d*x]/((b*c-a*d)^2*(a+b*x)^(3/2))-16/15*d^2*Sqrt[c+d*x]/((b*c-a*d)^3*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1794
  public void test0274() {
    check( //
        "Integrate[1/(Sqrt[4-x]*Sqrt[x]), x]", //
        "-ArcSin[1-1/2*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2156
  public void test0275() {
    check( //
        "Integrate[(a+b*x)^n*(c+d*x)^(-5-n), x]", //
        "(a+b*x)^(1+n)*(c+d*x)^(-4-n)/((b*c-a*d)*(4+n))+3*b*(a+b*x)^(1+n)*(c+d*x)^(-3-n)/((b*c-a*d)^2*(3+n)*(4+n))+6*b^2*(a+b*x)^(1+n)*(c+d*x)^(-2-n)/((b*c-a*d)^3*(2+n)*(3+n)*(4+n))+6*b^3*(a+b*x)^(1+n)*(c+d*x)^(-1-n)/((b*c-a*d)^4*(1+n)*(2+n)*(3+n)*(4+n))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2185
  public void test0276() {
    check( //
        "Integrate[1-x^2-3*x^5, x]", //
        "x-1/3*x^3-1/2*x^6");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2201
  public void test0277() {
    check( //
        "Integrate[1/x^(3/2)+x^(3/2), x]", //
        "2/5*x^(5/2)+(-2)/Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:24
  public void test0278() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^7, x]", //
        "-1/6*a^4*c^3/x^6+2/5*a^3*b*c^3/x^5-2/3*a*b^3*c^3/x^3+1/2*b^4*c^3/x^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:38
  public void test0279() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^8, x]", //
        "-1/7*a^5*c^4/x^7+1/2*a^4*b*c^4/x^6-2/5*a^3*b^2*c^4/x^5-1/2*a^2*b^3*c^4/x^4+a*b^4*c^4/x^3-1/2*b^5*c^4/x^2");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:52
  public void test0280() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^8, x]", //
        "-1/7*c^5*(a-b*x)^6/x^7-4/21*b*c^5*(a-b*x)^6/(a*x^6)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:138
  public void test0281() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x, x]", //
        "3*a^2*A*b*x+3/2*a*A*b^2*x^2+1/3*A*b^3*x^3+1/4*B*(a+b*x)^4/b+a^3*A*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:180
  public void test0282() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^4, x]", //
        "-1/3*a^10*A/x^3-1/2*a^9*(10*A*b+a*B)/x^2-5*a^8*b*(9*A*b+2*a*B)/x+30*a^6*b^3*(7*A*b+4*a*B)*x+21*a^5*b^4*(6*A*b+5*a*B)*x^2+14*a^4*b^5*(5*A*b+6*a*B)*x^3+15/2*a^3*b^6*(4*A*b+7*a*B)*x^4+3*a^2*b^7*(3*A*b+8*a*B)*x^5+5/6*a*b^8*(2*A*b+9*a*B)*x^6+1/7*b^9*(A*b+10*a*B)*x^7+1/8*b^10*B*x^8+15*a^7*b^2*(8*A*b+3*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:194
  public void test0283() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^18, x]", //
        "-1/17*A*(a+b*x)^11/(a*x^17)+1/272*(6*A*b-17*a*B)*(a+b*x)^11/(a^2*x^16)-1/816*b*(6*A*b-17*a*B)*(a+b*x)^11/(a^3*x^15)+1/2856*b^2*(6*A*b-17*a*B)*(a+b*x)^11/(a^4*x^14)-1/12376*b^3*(6*A*b-17*a*B)*(a+b*x)^11/(a^5*x^13)+1/74256*b^4*(6*A*b-17*a*B)*(a+b*x)^11/(a^6*x^12)-1/816816*b^5*(6*A*b-17*a*B)*(a+b*x)^11/(a^7*x^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:438
  public void test0284() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^(5/2), x]", //
        "-2/3*a^2*A/x^(3/2)+2/3*b^2*B*x^(3/2)-2*a*(2*A*b+a*B)/Sqrt[x]+2*b*(A*b+2*a*B)*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:455
  public void test0285() {
    check( //
        "Integrate[(A+B*x)/((a+b*x)*Sqrt[x]), x]", //
        "2*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/(b^(3/2)*Sqrt[a])+2*B*Sqrt[x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:552
  public void test0286() {
    check( //
        "Integrate[(A+B*x)/(x^2*Sqrt[a+b*x]), x]", //
        "(A*b-2*a*B)*ArcTanh[Sqrt[a+b*x]/Sqrt[a]]/a^(3/2)-A*Sqrt[a+b*x]/(a*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:586
  public void test0287() {
    check( //
        "Integrate[x*(c+d*x)^(5/2)/(a+b*x), x]", //
        "-2/3*a*(b*c-a*d)*(c+d*x)^(3/2)/b^3-2/5*a*(c+d*x)^(5/2)/b^2+2/7*(c+d*x)^(7/2)/(b*d)+2*a*(b*c-a*d)^(5/2)*ArcTanh[Sqrt[b]*Sqrt[c+d*x]/Sqrt[b*c-a*d]]/b^(9/2)-2*a*(b*c-a*d)^2*Sqrt[c+d*x]/b^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:627
  public void test0288() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/x^(9/2), x]", //
        "-2/7*A*(a+b*x)^(3/2)/(a*x^(7/2))+2/35*(4*A*b-7*a*B)*(a+b*x)^(3/2)/(a^2*x^(5/2))-4/105*b*(4*A*b-7*a*B)*(a+b*x)^(3/2)/(a^3*x^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:641
  public void test0289() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/x^(15/2), x]", //
        "-2/13*A*(a+b*x)^(5/2)/(a*x^(13/2))+2/143*(8*A*b-13*a*B)*(a+b*x)^(5/2)/(a^2*x^(11/2))-4/429*b*(8*A*b-13*a*B)*(a+b*x)^(5/2)/(a^3*x^(9/2))+16/3003*b^2*(8*A*b-13*a*B)*(a+b*x)^(5/2)/(a^4*x^(7/2))-32/15015*b^3*(8*A*b-13*a*B)*(a+b*x)^(5/2)/(a^5*x^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:685
  public void test0290() {
    check( //
        "Integrate[(A+B*x)/(x^(3/2)*(a+b*x)^(5/2)), x]", //
        "-2*A/(a*(a+b*x)^(3/2)*Sqrt[x])-2/3*(4*A*b-a*B)*Sqrt[x]/(a^2*(a+b*x)^(3/2))-4/3*(4*A*b-a*B)*Sqrt[x]/(a^3*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:719
  public void test0291() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Sqrt[a+b*x]/x^2, x]", //
        "-c^(3/2)*(b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/Sqrt[a]+1/4*(15*b^2*c^2+10*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[d]/b^(3/2)+3/2*d*(c+d*x)^(3/2)*Sqrt[a+b*x]-(c+d*x)^(5/2)*Sqrt[a+b*x]/x+1/4*d*(11*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/b");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:767
  public void test0292() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^3, x]", //
        "-1/2*(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^2-3/4*(b^2*c^2+6*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(Sqrt[a]*Sqrt[c])+3*(b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]*Sqrt[d]-3/4*(b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(c*x)+3/4*d*(3*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/c");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:797
  public void test0293() {
    check( //
        "Integrate[(a+b*x)^(3/2)/(x^4*(c+d*x)^(3/2)), x]", //
        "1/8*(b*c-a*d)*(b^2*c^2+10*a*b*c*d-35*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(9/2))-1/24*d*(3*b^2*c^2-100*a*b*c*d+105*a^2*d^2)*Sqrt[a+b*x]/(a*c^4*Sqrt[c+d*x])-1/3*a*Sqrt[a+b*x]/(c*x^3*Sqrt[c+d*x])-7/12*(b*c-a*d)*Sqrt[a+b*x]/(c^2*x^2*Sqrt[c+d*x])-1/24*(3*b*c-35*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(a*c^3*x*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:815
  public void test0294() {
    check( //
        "Integrate[(a+b*x)^(5/2)*Sqrt[c+d*x]/x^5, x]", //
        "-5/24*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(c^2*x^3)-1/4*(a+b*x)^(5/2)*(c+d*x)^(3/2)/(c*x^4)+5/64*(b*c-a*d)^4*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(7/2))-5/32*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x]/(c^3*x^2)-5/64*(b*c-a*d)^3*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c^3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:829
  public void test0295() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^2, x]", //
        "5/4*b*(a+b*x)^(3/2)*(c+d*x)^(5/2)-(a+b*x)^(5/2)*(c+d*x)^(5/2)/x-5*a^(3/2)*c^(3/2)*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]-5/64*(b^4*c^4-20*a*b^3*c^3*d-90*a^2*b^2*c^2*d^2-20*a^3*b*c*d^3+a^4*d^4)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(b^(3/2)*d^(3/2))-5/96*(b^2*c^2-18*a*b*c*d-31*a^2*d^2)*(c+d*x)^(3/2)*Sqrt[a+b*x]/d+5/24*b*(b*c+7*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/d-5/64*(b^3*c^3-19*a*b^2*c^2*d-45*a^2*b*c*d^2-a^3*d^3)*Sqrt[a+b*x]*Sqrt[c+d*x]/(b*d)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:859
  public void test0296() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^3*(c+d*x)^(5/2)), x]", //
        "5/12*(3*b*c-7*a*d)*(b*c-a*d)*(a+b*x)^(3/2)/(a*c^3*(c+d*x)^(3/2))-1/4*(3*b*c-7*a*d)*(a+b*x)^(5/2)/(a*c^2*x*(c+d*x)^(3/2))-1/2*(a+b*x)^(7/2)/(a*c*x^2*(c+d*x)^(3/2))-5/4*(3*b*c-7*a*d)*(b*c-a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]/c^(9/2)+5/4*(3*b*c-7*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:877
  public void test0297() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^2*Sqrt[a+b*x]), x]", //
        "2*d^(3/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[b]+(b*c-3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/a^(3/2)-c*Sqrt[a+b*x]*Sqrt[c+d*x]/(a*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:907
  public void test0298() {
    check( //
        "Integrate[1/(x^2*Sqrt[a+b*x]*Sqrt[c+d*x]), x]", //
        "(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(3/2))-Sqrt[a+b*x]*Sqrt[c+d*x]/(a*c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:921
  public void test0299() {
    check( //
        "Integrate[1/((c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "2/3*Sqrt[a+b*x]/((b*c-a*d)*(c+d*x)^(3/2))+4/3*b*Sqrt[a+b*x]/((b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:939
  public void test0300() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^4*(a+b*x)^(3/2)), x]", //
        "1/8*(b*c-a*d)*(35*b^2*c^2-10*a*b*c*d-a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(9/2)*c^(3/2))-1/24*b*(105*b^2*c^2-100*a*b*c*d+3*a^2*d^2)*Sqrt[c+d*x]/(a^4*c*Sqrt[a+b*x])-1/3*c*Sqrt[c+d*x]/(a*x^3*Sqrt[a+b*x])+7/12*(b*c-a*d)*Sqrt[c+d*x]/(a^2*x^2*Sqrt[a+b*x])-1/24*(35*b*c-3*a*d)*(b*c-a*d)*Sqrt[c+d*x]/(a^3*c*x*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:989
  public void test0301() {
    check( //
        "Integrate[x^2/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "-2/3*a^2/(b^2*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+4*a*c/(b*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])+2/3*(b^2*c^2+6*a*b*c*d+a^2*d^2)*Sqrt[a+b*x]/(b^2*(b*c-a*d)^3*(c+d*x)^(3/2))+4/3*(b^2*c^2+6*a*b*c*d+a^2*d^2)*Sqrt[a+b*x]/(b*(b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1007
  public void test0302() {
    check( //
        "Integrate[x*(a+b*x)^n/(-a-b*x)^n, x]", //
        "1/2*x^2*(a+b*x)^n/(-a-b*x)^n");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1258
  public void test0303() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/(d+e*x)^3, x]", //
        "b^2*B*x/e^3+1/2*(b*d-a*e)^2*(B*d-A*e)/(e^4*(d+e*x)^2)-(b*d-a*e)*(3*b*B*d-2*A*b*e-a*B*e)/(e^4*(d+e*x))-b*(3*b*B*d-A*b*e-2*a*B*e)*Log[d+e*x]/e^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1300
  public void test0304() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^12, x]", //
        "-1/11*(B*d-A*e)*(a+b*x)^7/(e*(b*d-a*e)*(d+e*x)^11)+1/110*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^2*(d+e*x)^10)+1/330*b*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^3*(d+e*x)^9)+1/1320*b^2*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^4*(d+e*x)^8)+1/9240*b^3*(7*b*B*d+4*A*b*e-11*a*B*e)*(a+b*x)^7/(e*(b*d-a*e)^5*(d+e*x)^7)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1328
  public void test0305() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^11, x]", //
        "b^10*B*x/e^11+1/10*(b*d-a*e)^10*(B*d-A*e)/(e^12*(d+e*x)^10)-1/9*(b*d-a*e)^9*(11*b*B*d-10*A*b*e-a*B*e)/(e^12*(d+e*x)^9)+5/8*b*(b*d-a*e)^8*(11*b*B*d-9*A*b*e-2*a*B*e)/(e^12*(d+e*x)^8)-15/7*b^2*(b*d-a*e)^7*(11*b*B*d-8*A*b*e-3*a*B*e)/(e^12*(d+e*x)^7)+5*b^3*(b*d-a*e)^6*(11*b*B*d-7*A*b*e-4*a*B*e)/(e^12*(d+e*x)^6)-42/5*b^4*(b*d-a*e)^5*(11*b*B*d-6*A*b*e-5*a*B*e)/(e^12*(d+e*x)^5)+21/2*b^5*(b*d-a*e)^4*(11*b*B*d-5*A*b*e-6*a*B*e)/(e^12*(d+e*x)^4)-10*b^6*(b*d-a*e)^3*(11*b*B*d-4*A*b*e-7*a*B*e)/(e^12*(d+e*x)^3)+15/2*b^7*(b*d-a*e)^2*(11*b*B*d-3*A*b*e-8*a*B*e)/(e^12*(d+e*x)^2)-5*b^8*(b*d-a*e)*(11*b*B*d-2*A*b*e-9*a*B*e)/(e^12*(d+e*x))-b^9*(11*b*B*d-A*b*e-10*a*B*e)*Log[d+e*x]/e^12");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1344
  public void test0306() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^3/(a+b*x), x]", //
        "(A*b-a*B)*e*(b*d-a*e)^2*x/b^4+1/2*(A*b-a*B)*(b*d-a*e)*(d+e*x)^2/b^3+1/3*(A*b-a*B)*(d+e*x)^3/b^2+1/4*B*(d+e*x)^4/(b*e)+(A*b-a*B)*(b*d-a*e)^3*Log[a+b*x]/b^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1896
  public void test0307() {
    check( //
        "Integrate[(2+3*x)^7*(3+5*x)/(1-2*x)^3, x]", //
        "9058973/1024/(1-2*x)^2+(-15647317/256)/(1-2*x)-24960933/256*x-10989621/256*x^2-631611/32*x^3-235467/32*x^4-147987/80*x^5-3645/16*x^6-23647449/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1910
  public void test0308() {
    check( //
        "Integrate[(2+3*x)^7*(3+5*x)^2/(1-2*x)^3, x]", //
        "99648703/2048/(1-2*x)^2+(-389535839/1024)/(1-2*x)-48280011/64*x-190742391/512*x^2-25895367/128*x^3-12299769/128*x^4-2798631/80*x^5-268515/32*x^6-54675/56*x^7-84589631/128*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1924
  public void test0309() {
    check( //
        "Integrate[(2+3*x)^6*(3+5*x)^3/(1-2*x)^3, x]", //
        "156590819/2048/(1-2*x)^2+(-616195041/1024)/(1-2*x)-308539921/256*x-306103815/512*x^2-41793093/128*x^3-19986237/128*x^4-229149/4*x^5-443475/32*x^6-91125/56*x^7-33674025/32*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2113
  public void test0310() {
    check( //
        "Integrate[(2+3*x)^4*Sqrt[1-2*x]/(3+5*x), x]", //
        "-45473/5000*(1-2*x)^(3/2)+34371/5000*(1-2*x)^(5/2)-2889/1400*(1-2*x)^(7/2)+9/40*(1-2*x)^(9/2)-2/3125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2/3125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2189
  public void test0311() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^3/(3+5*x), x]", //
        "2/1875*(1-2*x)^(3/2)-3897/2500*(1-2*x)^(5/2)+162/175*(1-2*x)^(7/2)-3/20*(1-2*x)^(9/2)-22/3125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/3125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2265
  public void test0312() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^4/(3+5*x), x]", //
        "22/46875*(1-2*x)^(3/2)+2/15625*(1-2*x)^(5/2)-136419/35000*(1-2*x)^(7/2)+3819/1000*(1-2*x)^(9/2)-2889/2200*(1-2*x)^(11/2)+81/520*(1-2*x)^(13/2)-242/78125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+242/78125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2527
  public void test0313() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/(d+e*x)^(7/2), x]", //
        "-2/5*(B*d-A*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)*(d+e*x)^(5/2))+2/15*(3*b*B*d+2*A*b*e-5*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^2*(d+e*x)^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2541
  public void test0314() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/(d+e*x)^(13/2), x]", //
        "-2/11*(B*d-A*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)*(d+e*x)^(11/2))+2/99*(5*b*B*d+6*A*b*e-11*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^2*(d+e*x)^(9/2))+8/693*b*(5*b*B*d+6*A*b*e-11*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^3*(d+e*x)^(7/2))+16/3465*b^2*(5*b*B*d+6*A*b*e-11*a*B*e)*(a+b*x)^(5/2)/(e*(b*d-a*e)^4*(d+e*x)^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2571
  public void test0315() {
    check( //
        "Integrate[(A+B*x)/((a+b*x)^(3/2)*Sqrt[d+e*x]), x]", //
        "2*B*ArcTanh[Sqrt[e]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[d+e*x])]/(b^(3/2)*Sqrt[e])-2*(A*b-a*B)*Sqrt[d+e*x]/(b*(b*d-a*e)*Sqrt[a+b*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2647
  public void test0316() {
    check( //
        "Integrate[(2+3*x)^4*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "35511/20000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/15*(2+3*x)^4*Sqrt[1-2*x]/(3+5*x)^(3/2)-524/825*(2+3*x)^3*Sqrt[1-2*x]/Sqrt[3+5*x]+623/1375*(2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x]+7/220000*(2563+8940*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2665
  public void test0317() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2, x]", //
        "-107/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-41/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-1/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)-4/9*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2679
  public void test0318() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^6, x]", //
        "3/35*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^5+37/56*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^4-1625151/43904*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-4477/3136*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2+407/112*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3-147741/43904*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2693
  public void test0319() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^8, x]", //
        "-1/21*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^7-1104970911/17210368*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-12421/52920*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5+181/756*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^6-1289227/8890560*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+6249601/53343360*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+224018941/298722816*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+23466191827/4182119424*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2755
  public void test0320() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^2, x]", //
        "-5/18*(1-2*x)^(3/2)*(3+5*x)^(5/2)-1/3*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)-1295/729*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-660959/93312*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+1453/288*(3+5*x)^(3/2)*Sqrt[1-2*x]-247/324*(3+5*x)^(5/2)*Sqrt[1-2*x]-155777/31104*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2771
  public void test0321() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^2*Sqrt[3+5*x]), x]", //
        "346/135*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-175/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+7/3*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)+74/45*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2785
  public void test0322() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^4*(3+5*x)^(3/2)), x]", //
        "147015/8*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/7*(1-2*x)^(7/2)/((2+3*x)^3*Sqrt[3+5*x])+81/28*(1-2*x)^(5/2)/((2+3*x)^2*Sqrt[3+5*x])+4455/56*(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-147015/56*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2817
  public void test0323() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)*Sqrt[1-2*x]), x]", //
        "29/18*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-2/9*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-5/6*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2831
  public void test0324() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^5*Sqrt[1-2*x]), x]", //
        "-6655/3136*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-55/672*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-1/24*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^3+3/28*(3+5*x)^(7/2)*Sqrt[1-2*x]/(2+3*x)^4-605/3136*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2847
  public void test0325() {
    check( //
        "Integrate[(2+3*x)^2/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "123/50*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/275*Sqrt[1-2*x]/Sqrt[3+5*x]-9/50*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2861
  public void test0326() {
    check( //
        "Integrate[1/((2+3*x)^2*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-1593/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-845/231*Sqrt[1-2*x]/(3+5*x)^(3/2)+3/7*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+84235/2541*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2879
  public void test0327() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^5), x]", //
        "-1244755/153664*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/7*Sqrt[3+5*x]/((2+3*x)^4*Sqrt[1-2*x])-27/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-13/392*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+835/10976*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+107245/153664*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2909
  public void test0328() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)*Sqrt[3+5*x]), x]", //
        "-6/7*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/77*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2923
  public void test0329() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^4*(3+5*x)^(3/2)), x]", //
        "2079585/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-73435/15092)/(Sqrt[1-2*x]*Sqrt[3+5*x])+1/7/((2+3*x)^3*Sqrt[1-2*x]*Sqrt[3+5*x])+37/28/((2+3*x)^2*Sqrt[1-2*x]*Sqrt[3+5*x])+6525/392/((2+3*x)*Sqrt[1-2*x]*Sqrt[3+5*x])-36657025/332024*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2985
  public void test0330() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^3*(3+5*x)^(3/2)), x]", //
        "79515/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+(-2725/3234)/((1-2*x)^(3/2)*Sqrt[3+5*x])+3/14/((1-2*x)^(3/2)*(2+3*x)^2*Sqrt[3+5*x])+81/28/((1-2*x)^(3/2)*(2+3*x)*Sqrt[3+5*x])+(-89945/249018)/(Sqrt[1-2*x]*Sqrt[3+5*x])-46307675/5478396*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3001
  public void test0331() {
    check( //
        "Integrate[Sqrt[e+b*(-1+e)*x/a]/(Sqrt[a+b*x]*Sqrt[c+b*(-1+c)*x/a]), x]", //
        "2*EllipticE[ArcSin[Sqrt[1-c]*Sqrt[a+b*x]/Sqrt[a]],(1-e)/(1-c)]*Sqrt[a]/(b*Sqrt[1-c])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3021
  public void test0332() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x], x]", //
        "-5327983/708750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-160297/708750*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/45*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]-1208/7875*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-3/175*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-160297/141750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3035
  public void test0333() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(7/2), x]", //
        "31588/6615*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-12758/6615*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-118/315*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)-2/15*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)-12758/6615*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3051
  public void test0334() {
    check( //
        "Integrate[(2+3*x)^(7/2)*Sqrt[1-2*x]/(3+5*x)^(3/2), x]", //
        "-203179/218750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-38723/109375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/5*(2+3*x)^(7/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+183/4375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+48/175*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-2486/21875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3065
  public void test0335() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-532*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-16*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-40/3*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+2660/33*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3083
  public void test0336() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x], x]", //
        "-4971289/2126250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-76163/1063125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/45*(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]-839/23625*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+194/4725*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-76163/212625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3097
  public void test0337() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(7/2), x]", //
        "-2/15*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(5/2)+116854/8505*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-43214/8505*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+362/135*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)+9808/945*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]-43214/1701*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3113
  public void test0338() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(5/2)/(3+5*x)^(3/2), x]", //
        "-47342/109375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-5753/109375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(3/2)*(2+3*x)^(5/2)/Sqrt[3+5*x]+2818/4375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-32/175*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+2719/21875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3127
  public void test0339() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "-120*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-1088*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/9*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+404/9*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-300*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+5440/3*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3145
  public void test0340() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[2+3*x], x]", //
        "-829177897/31893750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-12996374/15946875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+326/7425*(1-2*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]+2/55*(1-2*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[2+3*x]-78797/3898125*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+30362/779625*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-12996374/35083125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3159
  public void test0341() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(7/2), x]", //
        "-2/15*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(5/2)+74/27*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(3/2)+136028/3645*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-48478/3645*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-6464/81*(3+5*x)^(5/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+11036/81*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-48478/729*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3175
  public void test0342() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(11/2)*Sqrt[3+5*x]), x]", //
        "-66055016/27783*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1986944/27783*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/27*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(9/2)+512/81*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+20420/567*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+950584/3969*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+66055016/27783*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3189
  public void test0343() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(3/2)/(3+5*x)^(5/2), x]", //
        "-2/15*(1-2*x)^(5/2)*(2+3*x)^(3/2)/(3+5*x)^(3/2)-7738/15625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+9206/15625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-178/75*(1-2*x)^(3/2)*(2+3*x)^(3/2)/Sqrt[3+5*x]-572/625*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+8874/3125*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3207
  public void test0344() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(3/2)/Sqrt[1-2*x], x]", //
        "-44109377/472500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-663409/236250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-137/315*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-1/9*(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-9547/5250*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-663409/47250*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3221
  public void test0345() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-4157/1323*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+412/1323*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/63*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)+412/1323*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3253
  public void test0346() {
    check( //
        "Integrate[1/((2+3*x)^(7/2)*Sqrt[1-2*x]*Sqrt[3+5*x]), x]", //
        "-20644/1715*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-6856/1715*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+6/35*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+296/245*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+20644/1715*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3267
  public void test0347() {
    check( //
        "Integrate[1/((3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]), x]", //
        "-124/11*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-4/11*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-10/33*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+620/363*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3291
  public void test0348() {
    check( //
        "Integrate[Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2), x]", //
        "EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3305
  public void test0349() {
    check( //
        "Integrate[(2+3*x)^(7/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2), x]", //
        "17888580643/189000*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+269045681/94500*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+(2+3*x)^(7/2)*(3+5*x)^(5/2)/Sqrt[1-2*x]+419/66*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+18/11*(2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+4066493/23100*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+9741/385*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+269045681/207900*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3321
  public void test0350() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*Sqrt[2+3*x]*Sqrt[3+5*x]), x]", //
        "2/11*EllipticE[ArcSin[Sqrt[5]*Sqrt[2+3*x]],2/35]*Sqrt[5/7]*Sqrt[-3-5*x]/Sqrt[3+5*x]+4/77*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3335
  public void test0351() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "5684677/151250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+84134/75625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/11*(2+3*x)^(7/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-4421/99825*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+83093/166375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3353
  public void test0352() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "31/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/21*Sqrt[2+3*x]*Sqrt[3+5*x]/(1-2*x)^(3/2)+62/1617*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3367
  public void test0353() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(5/2)/(1-2*x)^(5/2), x]", //
        "1/3*(2+3*x)^(3/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2)-12101/20*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-91/5*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-137/33*(3+5*x)^(5/2)*Sqrt[2+3*x]/Sqrt[1-2*x]-817/66*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-91*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3383
  public void test0354() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-184636/26411*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-9124/26411*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(3/2))+1072/17787*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])+974/41503*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+184636/290521*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3397
  public void test0355() {
    check( //
        "Integrate[(2+3*x)^(9/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^(7/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2))-4971289/332750*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-76163/166375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-140/121*(2+3*x)^(5/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])+2063/19965*(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)+70226/1098075*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3417
  public void test0356() {
    check( //
        "Integrate[(a+b*x)^(1/3)*(c+d*x)^(2/3)/(e+f*x)^3, x]", //
        "1/2*(a+b*x)^(1/3)*(c+d*x)^(5/3)/((d*e-c*f)*(e+f*x)^2)-1/6*(b*c-a*d)*(a+b*x)^(1/3)*(c+d*x)^(2/3)/((b*e-a*f)*(d*e-c*f)*(e+f*x))-1/18*(b*c-a*d)^2*Log[e+f*x]/((b*e-a*f)^(5/3)*(d*e-c*f)^(4/3))+1/6*(b*c-a*d)^2*Log[-(a+b*x)^(1/3)+(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/(d*e-c*f)^(1/3)]/((b*e-a*f)^(5/3)*(d*e-c*f)^(4/3))+1/3*(b*c-a*d)^2*ArcTan[1/Sqrt[3]+2*(b*e-a*f)^(1/3)*(c+d*x)^(1/3)/((d*e-c*f)^(1/3)*(a+b*x)^(1/3)*Sqrt[3])]/((b*e-a*f)^(5/3)*(d*e-c*f)^(4/3)*Sqrt[3])");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:61
  public void test0357() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x], x]", //
        "121/18*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+1/10*(1+4*x)^(3/2)*Sqrt[2-3*x]*Sqrt[-5+2*x]-847/270*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]-22/45*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:43
  public void test0358() {
    check( //
        "Integrate[x^3*(a+b*x^2)^3, x]", //
        "-1/8*a*(a+b*x^2)^4/b^2+1/10*(a+b*x^2)^5/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:71
  public void test0359() {
    check( //
        "Integrate[(a+b*x^2)^5/x^3, x]", //
        "-1/2*a^5/x^2+5*a^3*b^2*x^2+5/2*a^2*b^3*x^4+5/6*a*b^4*x^6+1/8*b^5*x^8+5*a^4*b*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:99
  public void test0360() {
    check( //
        "Integrate[x^7*(a+b*x^2)^8, x]", //
        "-1/18*a^3*(a+b*x^2)^9/b^4+3/20*a^2*(a+b*x^2)^10/b^4-3/22*a*(a+b*x^2)^11/b^4+1/24*(a+b*x^2)^12/b^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:113
  public void test0361() {
    check( //
        "Integrate[(a+b*x^2)^8/x^21, x]", //
        "-1/20*(a+b*x^2)^9/(a*x^20)+1/180*b*(a+b*x^2)^9/(a^2*x^18)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:157
  public void test0362() {
    check( //
        "Integrate[1/(x^9*(a+b*x^2)), x]", //
        "(-1/8)/(a*x^8)+1/6*b/(a^2*x^6)-1/4*b^2/(a^3*x^4)+1/2*b^3/(a^4*x^2)+b^4*Log[x]/a^5-1/2*b^4*Log[a+b*x^2]/a^5");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:171
  public void test0363() {
    check( //
        "Integrate[1/(a+b*x^2)^2, x]", //
        "1/2*x/(a*(a+b*x^2))+1/2*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:185
  public void test0364() {
    check( //
        "Integrate[x^7/(a+b*x^2)^3, x]", //
        "1/2*x^2/b^3+1/4*a^3/(b^4*(a+b*x^2)^2)-3/2*a^2/(b^4*(a+b*x^2))-3/2*a*Log[a+b*x^2]/b^4");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:213
  public void test0365() {
    check( //
        "Integrate[x^9/(a+b*x^2)^10, x]", //
        "-1/18*a^4/(b^5*(a+b*x^2)^9)+1/4*a^3/(b^5*(a+b*x^2)^8)-3/7*a^2/(b^5*(a+b*x^2)^7)+1/3*a/(b^5*(a+b*x^2)^6)+(-1/10)/(b^5*(a+b*x^2)^5)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:241
  public void test0366() {
    check( //
        "Integrate[1/(a-b*x^2), x]", //
        "ArcTanh[x*Sqrt[b]/Sqrt[a]]/(Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:255
  public void test0367() {
    check( //
        "Integrate[1/(a-b*x^2)^3, x]", //
        "1/4*x/(a*(a-b*x^2)^2)+3/8*x/(a^2*(a-b*x^2))+3/8*ArcTanh[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:269
  public void test0368() {
    check( //
        "Integrate[1/(x^3*(-1+b*x^2)), x]", //
        "1/2/x^2-b*Log[x]+1/2*b*Log[1-b*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:399
  public void test0369() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/x^2, x]", //
        "ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]*Sqrt[b]-Sqrt[a+b*x^2]/x");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:441
  public void test0370() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)/x^14, x]", //
        "-1/13*(a+b*x^2)^(7/2)/(a*x^13)+6/143*b*(a+b*x^2)^(7/2)/(a^2*x^11)-8/429*b^2*(a+b*x^2)^(7/2)/(a^3*x^9)+16/3003*b^3*(a+b*x^2)^(7/2)/(a^4*x^7)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:597
  public void test0371() {
    check( //
        "Integrate[1/(x*Sqrt[-9+4*x^2]), x]", //
        "1/3*ArcTan[1/3*Sqrt[-9+4*x^2]]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:887
  public void test0372() {
    check( //
        "Integrate[1/(a+b*x^2)^(1/4), x]", //
        "2*x/(a+b*x^2)^(1/4)-2*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a+b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:901
  public void test0373() {
    check( //
        "Integrate[1/(a+b*x^2)^(3/4), x]", //
        "2*(1+b*x^2/a)^(3/4)*EllipticF[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[a]/((a+b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:915
  public void test0374() {
    check( //
        "Integrate[1/(a+b*x^2)^(5/4), x]", //
        "2*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/((a+b*x^2)^(1/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:929
  public void test0375() {
    check( //
        "Integrate[1/(a-b*x^2)^(7/4), x]", //
        "2/3*x/(a*(a-b*x^2)^(3/4))+2/3*(1-b*x^2/a)^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/((a-b*x^2)^(3/4)*Sqrt[a]*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1003
  public void test0376() {
    check( //
        "Integrate[(a+b*x^2)^(1/4)/(c*x)^(15/2), x]", //
        "-2/5*(a+b*x^2)^(5/4)/(a*c*(c*x)^(13/2))+16/45*(a+b*x^2)^(9/4)/(a^2*c*(c*x)^(13/2))-64/585*(a+b*x^2)^(13/4)/(a^3*c*(c*x)^(13/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1133
  public void test0377() {
    check( //
        "Integrate[x^5*(a+b*x^2)^p, x]", //
        "1/2*a^2*(a+b*x^2)^(1+p)/(b^3*(1+p))-a*(a+b*x^2)^(2+p)/(b^3*(2+p))+1/2*(a+b*x^2)^(3+p)/(b^3*(3+p))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:48
  public void test0378() {
    check( //
        "Integrate[(c+d*x^2)^3/(a+b*x^2)^3, x]", //
        "d^3*x/b^3+1/4*(b*c-a*d)^3*x/(a*b^3*(a+b*x^2)^2)+3/8*(b*c-a*d)^2*(b*c+3*a*d)*x/(a^2*b^3*(a+b*x^2))+3/8*(b*c-a*d)*(4*a^2*d^2+(b*c+a*d)^2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*b^(7/2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:66
  public void test0379() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(c+d*x^2)^3, x]", //
        "-1/4*d*x*(a+b*x^2)^(3/2)/(c*(b*c-a*d)*(c+d*x^2)^2)+1/8*a*(4*b*c-3*a*d)*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(5/2)*(b*c-a*d)^(3/2))+1/8*(4*b*c-3*a*d)*x*Sqrt[a+b*x^2]/(c^2*(b*c-a*d)*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:80
  public void test0380() {
    check( //
        "Integrate[(a+b*x^2)^(5/2), x]", //
        "5/24*a*x*(a+b*x^2)^(3/2)+1/6*x*(a+b*x^2)^(5/2)+5/16*a^3*ArcTanh[x*Sqrt[b]/Sqrt[a+b*x^2]]/Sqrt[b]+5/16*a^2*x*Sqrt[a+b*x^2]");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:96
  public void test0381() {
    check( //
        "Integrate[1/((a+b*x^2)^(1/2)*(c+d*x^2)^2), x]", //
        "1/2*(2*b*c-a*d)*ArcTanh[x*Sqrt[b*c-a*d]/(Sqrt[c]*Sqrt[a+b*x^2])]/(c^(3/2)*(b*c-a*d)^(3/2))-1/2*d*x*Sqrt[a+b*x^2]/(c*(b*c-a*d)*(c+d*x^2))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:177
  public void test0382() {
    check( //
        "Integrate[1/((a+b*x^2)^(1/3)*(9*a*d/b+d*x^2)), x]", //
        "1/12*ArcTan[1/3*(a^(1/3)-(a+b*x^2)^(1/3))^2/(a^(1/6)*x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d)+1/12*ArcTan[1/3*x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/(a^(5/6)*d)-1/4*ArcTanh[a^(1/6)*(a^(1/3)-(a+b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:216
  public void test0383() {
    check( //
        "Integrate[Sqrt[2+b*x^2]/Sqrt[3+d*x^2], x]", //
        "x*Sqrt[2+b*x^2]/Sqrt[3+d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[3]],1-3/2*b/d]*Sqrt[2]*Sqrt[2+b*x^2]/(Sqrt[d]*Sqrt[(2+b*x^2)/(3+d*x^2)]*Sqrt[3+d*x^2])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[3]],1-3/2*b/d]*Sqrt[2]*Sqrt[2+b*x^2]/(Sqrt[d]*Sqrt[(2+b*x^2)/(3+d*x^2)]*Sqrt[3+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:230
  public void test0384() {
    check( //
        "Integrate[Sqrt[1+x^2]/Sqrt[2+3*x^2], x]", //
        "1/3*x*Sqrt[2+3*x^2]/Sqrt[1+x^2]+EllipticF[ArcTan[x],-1/2]*Sqrt[2+3*x^2]/(Sqrt[2]*Sqrt[1+x^2]*Sqrt[(2+3*x^2)/(1+x^2)])-1/3*EllipticE[ArcTan[x],-1/2]*Sqrt[2]*Sqrt[2+3*x^2]/(Sqrt[1+x^2]*Sqrt[(2+3*x^2)/(1+x^2)])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:303
  public void test0385() {
    check( //
        "Integrate[Sqrt[a+b*x^2]/Sqrt[c+d*x^2], x]", //
        "x*Sqrt[a+b*x^2]/Sqrt[c+d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[a+b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:317
  public void test0386() {
    check( //
        "Integrate[Sqrt[c-d*x^2]/Sqrt[-a+b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[c-d*x^2]/(Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[1-d*x^2/c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:331
  public void test0387() {
    check( //
        "Integrate[Sqrt[1-c^2*x^2]/Sqrt[1+c^2*x^2], x]", //
        "-EllipticE[ArcSin[c*x],-1]/c+2*EllipticF[ArcSin[c*x],-1]/c");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:13
  public void test0388() {
    check( //
        "Integrate[x*(a+b*x^2)*(A+B*x^2), x]", //
        "1/2*a*A*x^2+1/4*(A*b+a*B)*x^4+1/6*b*B*x^6");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:27
  public void test0389() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^3, x]", //
        "-1/2*a^2*A/x^2+1/2*b*(A*b+2*a*B)*x^2+1/4*b^2*B*x^4+a*(2*A*b+a*B)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:55
  public void test0390() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^12, x]", //
        "-1/11*a^5*A/x^11-1/9*a^4*(5*A*b+a*B)/x^9-5/7*a^3*b*(2*A*b+a*B)/x^7-2*a^2*b^2*(A*b+a*B)/x^5-5/3*a*b^3*(A*b+2*a*B)/x^3-b^4*(A*b+5*a*B)/x+b^5*B*x");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:71
  public void test0391() {
    check( //
        "Integrate[x^4*(A+B*x^2)/(a+b*x^2), x]", //
        "-a*(A*b-a*B)*x/b^3+1/3*(A*b-a*B)*x^3/b^2+1/5*B*x^5/b+a^(3/2)*(A*b-a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:85
  public void test0392() {
    check( //
        "Integrate[x^8*(A+B*x^2)/(a+b*x^2)^2, x]", //
        "a^2*(3*A*b-4*a*B)*x/b^5-1/3*a*(2*A*b-3*a*B)*x^3/b^4+1/5*(A*b-2*a*B)*x^5/b^3+1/7*B*x^7/b^2+1/2*a^3*(A*b-a*B)*x/(b^5*(a+b*x^2))-1/2*a^(5/2)*(7*A*b-9*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(11/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:113
  public void test0393() {
    check( //
        "Integrate[x^6*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "(A*b-3*a*B)*x/b^4+1/3*B*x^3/b^3-1/4*a^2*(A*b-a*B)*x/(b^4*(a+b*x^2)^2)+1/8*a*(9*A*b-13*a*B)*x/(b^4*(a+b*x^2))-5/8*(3*A*b-7*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(9/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:141
  public void test0394() {
    check( //
        "Integrate[x^2*(a*c+b*c*x^2)/(a+b*x^2)^2, x]", //
        "c*x/b-c*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]/b^(3/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:159
  public void test0395() {
    check( //
        "Integrate[x^3*(a+b*x^2)^2*(c+d*x^2), x]", //
        "1/4*a^2*c*x^4+1/6*a*(2*b*c+a*d)*x^6+1/8*b*(b*c+2*a*d)*x^8+1/10*b^2*d*x^10");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:173
  public void test0396() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^2, x]", //
        "-a^2*c^2/x+2*a*c*(b*c+a*d)*x+1/3*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^3+2/5*b*d*(b*c+a*d)*x^5+1/7*b^2*d^2*x^7");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:189
  public void test0397() {
    check( //
        "Integrate[x^2*(a+b*x^2)^2/(c+d*x^2), x]", //
        "(b*c-a*d)^2*x/d^3-1/3*b*(b*c-2*a*d)*x^3/d^2+1/5*b^2*x^5/d-(b*c-a*d)^2*ArcTan[x*Sqrt[d]/Sqrt[c]]*Sqrt[c]/d^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:203
  public void test0398() {
    check( //
        "Integrate[(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "b^2*x/d^2+1/2*(b*c-a*d)^2*x/(c*d^2*(c+d*x^2))-1/2*(b*c-a*d)*(3*b*c+a*d)*ArcTan[x*Sqrt[d]/Sqrt[c]]/(c^(3/2)*d^(5/2))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:237
  public void test0399() {
    check( //
        "Integrate[x*(c+d*x^2)^2/(a+b*x^2), x]", //
        "1/2*d*(b*c-a*d)*x^2/b^2+1/4*(c+d*x^2)^2/b+1/2*(b*c-a*d)^2*Log[a+b*x^2]/b^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:267
  public void test0400() {
    check( //
        "Integrate[1/(x^7*(a+b*x^2)*(c+d*x^2)), x]", //
        "(-1/6)/(a*c*x^6)+1/4*(b*c+a*d)/(a^2*c^2*x^4)+1/2*(-b^2*c^2-a*b*c*d-a^2*d^2)/(a^3*c^3*x^2)-(b*c+a*d)*(b^2*c^2+a^2*d^2)*Log[x]/(a^4*c^4)+1/2*b^4*Log[a+b*x^2]/(a^4*(b*c-a*d))-1/2*d^4*Log[c+d*x^2]/(c^4*(b*c-a*d))");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:299
  public void test0401() {
    check( //
        "Integrate[(c+d*x^2)/(x^2*(a+b*x^2)^2), x]", //
        "-c/(a^2*x)-1/2*(b*c-a*d)*x/(a^2*(a+b*x^2))-1/2*(3*b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:329
  public void test0402() {
    check( //
        "Integrate[1/(x^3*(a+b*x^2)^2*(c+d*x^2)), x]", //
        "(-1/2)/(a^2*c*x^2)-1/2*b^2/(a^2*(b*c-a*d)*(a+b*x^2))-(2*b*c+a*d)*Log[x]/(a^3*c^2)+1/2*b^2*(2*b*c-3*a*d)*Log[a+b*x^2]/(a^3*(b*c-a*d)^2)+1/2*d^3*Log[c+d*x^2]/(c^2*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:403
  public void test0403() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^(3/2), x]", //
        "2/3*a*(2*A*b+a*B)*x^(3/2)+2/7*b*(A*b+2*a*B)*x^(7/2)+2/11*b^2*B*x^(11/2)-2*a^2*A/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:419
  public void test0404() {
    check( //
        "Integrate[(A+B*x^2)*Sqrt[x]/(a+b*x^2), x]", //
        "2/3*B*x^(3/2)/b-(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(1/4)*b^(7/4)*Sqrt[2])+(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(1/4)*b^(7/4)*Sqrt[2])+1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(1/4)*b^(7/4)*Sqrt[2])-1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(1/4)*b^(7/4)*Sqrt[2])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:451
  public void test0405() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)/x^(7/2), x]", //
        "-2/5*a^2*c/x^(5/2)+2/3*b*(b*c+2*a*d)*x^(3/2)+2/7*b^2*d*x^(7/2)-2*a*(2*b*c+a*d)/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:465
  public void test0406() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^(3/2), x]", //
        "2/3*a*c^2*(2*b*c+3*a*d)*x^(3/2)+2/7*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^(7/2)+2/11*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^(11/2)+2/15*b*d^2*(3*b*c+2*a*d)*x^(15/2)+2/19*b^2*d^3*x^(19/2)-2*a^2*c^3/Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:481
  public void test0407() {
    check( //
        "Integrate[x^(5/2)*(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "-1/6*(11*b*c-3*a*d)*(b*c-a*d)*x^(3/2)/(c*d^3)+2/7*b^2*x^(7/2)/d^2+1/2*(b*c-a*d)^2*x^(7/2)/(c*d^2*(c+d*x^2))-1/4*(11*b*c-3*a*d)*(b*c-a*d)*ArcTan[1-d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(1/4)*d^(15/4)*Sqrt[2])+1/4*(11*b*c-3*a*d)*(b*c-a*d)*ArcTan[1+d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(1/4)*d^(15/4)*Sqrt[2])+1/8*(11*b*c-3*a*d)*(b*c-a*d)*Log[Sqrt[c]+x*Sqrt[d]-c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(1/4)*d^(15/4)*Sqrt[2])-1/8*(11*b*c-3*a*d)*(b*c-a*d)*Log[Sqrt[c]+x*Sqrt[d]+c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(1/4)*d^(15/4)*Sqrt[2])");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:565
  public void test0408() {
    check( //
        "Integrate[1/(x*Sqrt[-1-x^3]), x]", //
        "2/3*ArcTan[Sqrt[-1-x^3]]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:2204
  public void test0409() {
    check( //
        "Integrate[(a+b/x^2)^(1/2)/x^3, x]", //
        "-1/3*(a+b/x^2)^(3/2)/b");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:38
  public void test0410() {
    check( //
        "Integrate[1/x^(5/2), x]", //
        "(-2/3)/x^(3/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:62
  public void test0411() {
    check( //
        "Integrate[(c+d*(a+b*x))^(1/2), x]", //
        "2/3*(c+d*(a+b*x))^(3/2)/(b*d)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:180
  public void test0412() {
    check( //
        "Integrate[(a+b*x)^10/x^17, x]", //
        "-1/16*(a+b*x)^11/(a*x^16)+1/48*b*(a+b*x)^11/(a^2*x^15)-1/168*b^2*(a+b*x)^11/(a^3*x^14)+1/728*b^3*(a+b*x)^11/(a^4*x^13)-1/4368*b^4*(a+b*x)^11/(a^5*x^12)+1/48048*b^5*(a+b*x)^11/(a^6*x^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:318
  public void test0413() {
    check( //
        "Integrate[1/(1/a^2+x*Sqrt[-a]), x]", //
        "Log[1+(-a)^(5/2)*x]/Sqrt[-a]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:640
  public void test0414() {
    check( //
        "Integrate[1/(Sqrt[x]*Sqrt[a+b*x]), x]", //
        "2*ArcTanh[Sqrt[b]*Sqrt[x]/Sqrt[a+b*x]]/Sqrt[b]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:682
  public void test0415() {
    check( //
        "Integrate[1/(x^(7/2)*Sqrt[2+b*x]), x]", //
        "-1/5*Sqrt[2+b*x]/x^(5/2)+2/15*b*Sqrt[2+b*x]/x^(3/2)-2/15*b^2*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:696
  public void test0416() {
    check( //
        "Integrate[1/(x^(5/2)*(2+b*x)^(5/2)), x]", //
        "1/3/(x^(3/2)*(2+b*x)^(3/2))+1/(x^(3/2)*Sqrt[2+b*x])-2/3*Sqrt[2+b*x]/x^(3/2)+2/3*b*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:856
  public void test0417() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)/x, x]", //
        "1/3*a*c*x^2*Sqrt[c*x^2]+1/4*b*c*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:886
  public void test0418() {
    check( //
        "Integrate[x^3*(a+b*x)/(c*x^2)^(5/2), x]", //
        "-a/(c^2*Sqrt[c*x^2])+b*x*Log[x]/(c^2*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:904
  public void test0419() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[c*x^2]/x^3, x]", //
        "b^2*Sqrt[c*x^2]-a^2*Sqrt[c*x^2]/x^2+2*a*b*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:918
  public void test0420() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x^3, x]", //
        "1/3*a^2*c^2*x^2*Sqrt[c*x^2]+1/2*a*b*c^2*x^3*Sqrt[c*x^2]+1/5*b^2*c^2*x^4*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:934
  public void test0421() {
    check( //
        "Integrate[x*(a+b*x)^2/(c*x^2)^(3/2), x]", //
        "-a^2/(c*Sqrt[c*x^2])+b^2*x^2/(c*Sqrt[c*x^2])+2*a*b*x*Log[x]/(c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:966
  public void test0422() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^5*(a+b*x)), x]", //
        "-c*Sqrt[c*x^2]/(a*x^2)-b*c*Log[x]*Sqrt[c*x^2]/(a^2*x)+b*c*Log[a+b*x]*Sqrt[c*x^2]/(a^2*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:982
  public void test0423() {
    check( //
        "Integrate[x/((a+b*x)*Sqrt[c*x^2]), x]", //
        "x*Log[a+b*x]/(b*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1014
  public void test0424() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^6*(a+b*x)^2), x]", //
        "-1/2*c*Sqrt[c*x^2]/(a^2*x^3)+2*b*c*Sqrt[c*x^2]/(a^3*x^2)+b^2*c*Sqrt[c*x^2]/(a^3*x*(a+b*x))+3*b^2*c*Log[x]*Sqrt[c*x^2]/(a^4*x)-3*b^2*c*Log[a+b*x]*Sqrt[c*x^2]/(a^4*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1030
  public void test0425() {
    check( //
        "Integrate[1/((c*x^2)^(3/2)*(a+b*x)^2), x]", //
        "2*b/(a^3*c*Sqrt[c*x^2])+(-1/2)/(a^2*c*x*Sqrt[c*x^2])+b^2*x/(a^3*c*(a+b*x)*Sqrt[c*x^2])+3*b^2*x*Log[x]/(a^4*c*Sqrt[c*x^2])-3*b^2*x*Log[a+b*x]/(a^4*c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1054
  public void test0426() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n/x^4, x]", //
        "-a*c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^2*(1+n)*x)+c^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^2*(2+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1087
  public void test0427() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(5/2)*(a+b*x), x]", //
        "a*c^2*(d*x)^(6+m)*Sqrt[c*x^2]/(d^6*(6+m)*x)+b*c^2*(d*x)^(7+m)*Sqrt[c*x^2]/(d^7*(7+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1141
  public void test0428() {
    check( //
        "Integrate[1/((a+b*x)^2*(a*d/b+d*x)^3), x]", //
        "-1/4*b^2/(d^3*(a+b*x)^4)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1205
  public void test0429() {
    check( //
        "Integrate[1/((a+b*x)^2*(a*c-b*c*x)^2), x]", //
        "1/2*x/(a^2*c^2*(a^2-b^2*x^2))+1/2*ArcTanh[b*x/a]/(a^3*b*c^2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1253
  public void test0430() {
    check( //
        "Integrate[(1+x)^(5/2)/(1-x)^(19/2), x]", //
        "1/17*(1+x)^(7/2)/(1-x)^(17/2)+1/51*(1+x)^(7/2)/(1-x)^(15/2)+4/663*(1+x)^(7/2)/(1-x)^(13/2)+4/2431*(1+x)^(7/2)/(1-x)^(11/2)+8/21879*(1+x)^(7/2)/(1-x)^(9/2)+8/153153*(1+x)^(7/2)/(1-x)^(7/2)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1285
  public void test0431() {
    check( //
        "Integrate[1/((1-x)^(1/2)*(1+x)^(5/2)), x]", //
        "-1/3*Sqrt[1-x]/(1+x)^(3/2)-1/3*Sqrt[1-x]/Sqrt[1+x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1337
  public void test0432() {
    check( //
        "Integrate[1/((a-I*a*x)^(1/4)*(a+I*a*x)^(1/4)), x]", //
        "2*x/((a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))-2*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/((a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1739
  public void test0433() {
    check( //
        "Integrate[1/((a+b*x)^(9/2)*(c+d*x)^(1/2)), x]", //
        "-2/7*Sqrt[c+d*x]/((b*c-a*d)*(a+b*x)^(7/2))+12/35*d*Sqrt[c+d*x]/((b*c-a*d)^2*(a+b*x)^(5/2))-16/35*d^2*Sqrt[c+d*x]/((b*c-a*d)^3*(a+b*x)^(3/2))+32/35*d^3*Sqrt[c+d*x]/((b*c-a*d)^4*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1973
  public void test0434() {
    check( //
        "Integrate[1/((a+b*x)^(13/4)*(c+d*x)^(3/4)), x]", //
        "-4/9*(c+d*x)^(1/4)/((b*c-a*d)*(a+b*x)^(9/4))+32/45*d*(c+d*x)^(1/4)/((b*c-a*d)^2*(a+b*x)^(5/4))-128/45*d^2*(c+d*x)^(1/4)/((b*c-a*d)^3*(a+b*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2052
  public void test0435() {
    check( //
        "Integrate[(a+b*x)^(1/6)/(c+d*x)^(25/6), x]", //
        "6/19*(a+b*x)^(7/6)/((b*c-a*d)*(c+d*x)^(19/6))+72/247*b*(a+b*x)^(7/6)/((b*c-a*d)^2*(c+d*x)^(13/6))+432/1729*b^2*(a+b*x)^(7/6)/((b*c-a*d)^3*(c+d*x)^(7/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2078
  public void test0436() {
    check( //
        "Integrate[(a+b*x)^(7/6)/(c+d*x)^(31/6), x]", //
        "6/25*(a+b*x)^(13/6)/((b*c-a*d)*(c+d*x)^(25/6))+72/475*b*(a+b*x)^(13/6)/((b*c-a*d)^2*(c+d*x)^(19/6))+432/6175*b^2*(a+b*x)^(13/6)/((b*c-a*d)^3*(c+d*x)^(13/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2106
  public void test0437() {
    check( //
        "Integrate[1/((a+b*x)^(5/6)*(c+d*x)^(19/6)), x]", //
        "6/13*(a+b*x)^(1/6)/((b*c-a*d)*(c+d*x)^(13/6))+72/91*b*(a+b*x)^(1/6)/((b*c-a*d)^2*(c+d*x)^(7/6))+432/91*b^2*(a+b*x)^(1/6)/((b*c-a*d)^3*(c+d*x)^(1/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2162
  public void test0438() {
    check( //
        "Integrate[(c+d*x)^(1+2*n-2*(1+n))/(a+b*x)^2, x]", //
        "(-1)/((b*c-a*d)*(a+b*x))-d*Log[a+b*x]/(b*c-a*d)^2+d*Log[c+d*x]/(b*c-a*d)^2");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2186
  public void test0439() {
    check( //
        "Integrate[5+2*x+3*x^2+4*x^3, x]", //
        "5*x+x^2+x^3+x^4");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2202
  public void test0440() {
    check( //
        "Integrate[-5*x^(3/2)+7*x^(5/2), x]", //
        "-2*x^(5/2)+2*x^(7/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:25
  public void test0441() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^3/x^8, x]", //
        "-1/7*a^4*c^3/x^7+1/3*a^3*b*c^3/x^6-1/2*a*b^3*c^3/x^4+1/3*b^4*c^3/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:39
  public void test0442() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^4/x^9, x]", //
        "-1/8*a^5*c^4/x^8+3/7*a^4*b*c^4/x^7-1/3*a^3*b^2*c^4/x^6-2/5*a^2*b^3*c^4/x^5+3/4*a*b^4*c^4/x^4-1/3*b^5*c^4/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:53
  public void test0443() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^9, x]", //
        "-1/8*c^5*(a-b*x)^6/x^8-5/28*b*c^5*(a-b*x)^6/(a*x^7)-5/168*b^2*c^5*(a-b*x)^6/(a^2*x^6)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:125
  public void test0444() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x, x]", //
        "2*a*A*b*x+1/2*A*b^2*x^2+1/3*B*(a+b*x)^3/b+a^2*A*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:139
  public void test0445() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^2, x]", //
        "-a^3*A/x+3*a*b*(A*b+a*B)*x+1/2*b^2*(A*b+3*a*B)*x^2+1/3*b^3*B*x^3+a^2*(3*A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:181
  public void test0446() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^5, x]", //
        "-1/4*a^10*A/x^4-1/3*a^9*(10*A*b+a*B)/x^3-5/2*a^8*b*(9*A*b+2*a*B)/x^2-15*a^7*b^2*(8*A*b+3*a*B)/x+42*a^5*b^4*(6*A*b+5*a*B)*x+21*a^4*b^5*(5*A*b+6*a*B)*x^2+10*a^3*b^6*(4*A*b+7*a*B)*x^3+15/4*a^2*b^7*(3*A*b+8*a*B)*x^4+a*b^8*(2*A*b+9*a*B)*x^5+1/6*b^9*(A*b+10*a*B)*x^6+1/7*b^10*B*x^7+30*a^6*b^3*(7*A*b+4*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:195
  public void test0447() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^19, x]", //
        "-1/18*a^10*A/x^18-1/17*a^9*(10*A*b+a*B)/x^17-5/16*a^8*b*(9*A*b+2*a*B)/x^16-a^7*b^2*(8*A*b+3*a*B)/x^15-15/7*a^6*b^3*(7*A*b+4*a*B)/x^14-42/13*a^5*b^4*(6*A*b+5*a*B)/x^13-7/2*a^4*b^5*(5*A*b+6*a*B)/x^12-30/11*a^3*b^6*(4*A*b+7*a*B)/x^11-3/2*a^2*b^7*(3*A*b+8*a*B)/x^10-5/9*a*b^8*(2*A*b+9*a*B)/x^9-1/8*b^9*(A*b+10*a*B)/x^8-1/7*b^10*B/x^7");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:439
  public void test0448() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^(7/2), x]", //
        "-2/5*a^2*A/x^(5/2)-2/3*a*(2*A*b+a*B)/x^(3/2)-2*b*(A*b+2*a*B)/Sqrt[x]+2*b^2*B*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:456
  public void test0449() {
    check( //
        "Integrate[(A+B*x)/(x^(3/2)*(a+b*x)), x]", //
        "-2*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]/(a^(3/2)*Sqrt[b])-2*A/(a*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:628
  public void test0450() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/x^(11/2), x]", //
        "-2/9*A*(a+b*x)^(3/2)/(a*x^(9/2))+2/21*(2*A*b-3*a*B)*(a+b*x)^(3/2)/(a^2*x^(7/2))-8/105*b*(2*A*b-3*a*B)*(a+b*x)^(3/2)/(a^3*x^(5/2))+16/315*b^2*(2*A*b-3*a*B)*(a+b*x)^(3/2)/(a^4*x^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:642
  public void test0451() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(A+B*x)/x^(17/2), x]", //
        "-2/15*A*(a+b*x)^(5/2)/(a*x^(15/2))+2/39*(2*A*b-3*a*B)*(a+b*x)^(5/2)/(a^2*x^(13/2))-16/429*b*(2*A*b-3*a*B)*(a+b*x)^(5/2)/(a^3*x^(11/2))+32/1287*b^2*(2*A*b-3*a*B)*(a+b*x)^(5/2)/(a^4*x^(9/2))-128/9009*b^3*(2*A*b-3*a*B)*(a+b*x)^(5/2)/(a^5*x^(7/2))+256/45045*b^4*(2*A*b-3*a*B)*(a+b*x)^(5/2)/(a^6*x^(5/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:686
  public void test0452() {
    check( //
        "Integrate[(A+B*x)/(x^(5/2)*(a+b*x)^(5/2)), x]", //
        "-2/3*A/(a*x^(3/2)*(a+b*x)^(3/2))-2/3*(2*A*b-a*B)/(a^2*(a+b*x)^(3/2)*Sqrt[x])-8/3*(2*A*b-a*B)/(a^3*Sqrt[x]*Sqrt[a+b*x])+16/3*(2*A*b-a*B)*Sqrt[a+b*x]/(a^4*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:720
  public void test0453() {
    check( //
        "Integrate[(c+d*x)^(5/2)*Sqrt[a+b*x]/x^3, x]", //
        "d^(3/2)*(5*b*c+a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/Sqrt[b]+1/4*(b^2*c^2-10*a*b*c*d-15*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[c]/a^(3/2)-1/4*(b*c+5*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x)-1/2*(c+d*x)^(5/2)*Sqrt[a+b*x]/x^2+1/4*d*(b*c+11*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/a");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:768
  public void test0454() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^4, x]", //
        "-1/3*(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^3+1/8*(b*c+a*d)*(b^2*c^2-10*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(3/2))+2*b^(3/2)*d^(3/2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]-1/4*(b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(c*x^2)-1/8*(b^2*c/a+8*b*d-a*d^2/c)*Sqrt[a+b*x]*Sqrt[c+d*x]/x");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:816
  public void test0455() {
    check( //
        "Integrate[(a+b*x)^(5/2)*Sqrt[c+d*x]/x^6, x]", //
        "1/48*(b*c-a*d)*(3*b*c+7*a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2)/(a*c^3*x^3)+1/40*(3*b*c+7*a*d)*(a+b*x)^(5/2)*(c+d*x)^(3/2)/(a*c^2*x^4)-1/5*(a+b*x)^(7/2)*(c+d*x)^(3/2)/(a*c*x^5)-1/128*(b*c-a*d)^4*(3*b*c+7*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(9/2))+1/64*(b*c-a*d)^2*(3*b*c+7*a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^4*x^2)+1/128*(b*c-a*d)^3*(3*b*c+7*a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^4*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:830
  public void test0456() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^3, x]", //
        "-5/4*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c*x)-1/2*(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^2-5/4*(3*b*c+a*d)*(b*c+3*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]*Sqrt[a]*Sqrt[c]+5/8*(b*c+a*d)*(b^2*c^2+14*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]/(Sqrt[b]*Sqrt[d])+5/12*(b^2*c^2+8*a*b*c*d+3*a^2*d^2)*(c+d*x)^(3/2)*Sqrt[a+b*x]/c+5/12*b*(5*b*c+3*a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/c+5/8*(b^2*c^2+10*a*b*c*d+5*a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:860
  public void test0457() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^4*(c+d*x)^(5/2)), x]", //
        "-1/3*a*(a+b*x)^(3/2)/(c*x^3*(c+d*x)^(3/2))-5/8*(b*c-a*d)*(b^2*c^2-14*a*b*c*d+21*a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(11/2)*Sqrt[a])-7/24*d*(7*b*c-15*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^4*(c+d*x)^(3/2))-3/4*a*(b*c-a*d)*Sqrt[a+b*x]/(c^2*x^2*(c+d*x)^(3/2))-1/8*(11*b*c-21*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^3*x*(c+d*x)^(3/2))-1/24*d*(113*b^2*c^2-420*a*b*c*d+315*a^2*d^2)*Sqrt[a+b*x]/(c^5*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:878
  public void test0458() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^3*Sqrt[a+b*x]), x]", //
        "-3/4*(b*c-a*d)^2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*Sqrt[c])-1/2*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*x^2)+3/4*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:922
  public void test0459() {
    check( //
        "Integrate[1/(x*(c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(c^(5/2)*Sqrt[a])-2/3*d*Sqrt[a+b*x]/(c*(b*c-a*d)*(c+d*x)^(3/2))-2/3*d*(5*b*c-3*a*d)*Sqrt[a+b*x]/(c^2*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:956
  public void test0460() {
    check( //
        "Integrate[1/(x*(a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "-2*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(3/2))+2*b/(a*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])+2*d*(b*c+a*d)*Sqrt[a+b*x]/(a*c*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:990
  public void test0461() {
    check( //
        "Integrate[x/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "-2/3*c/(d*(b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+2/3*(b*c+a*d)/(d*(b*c-a*d)^2*(a+b*x)^(3/2)*Sqrt[c+d*x])-8/3*(b*c+a*d)/((b*c-a*d)^3*Sqrt[a+b*x]*Sqrt[c+d*x])-16/3*d*(b*c+a*d)*Sqrt[a+b*x]/((b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1259
  public void test0462() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/(d+e*x)^4, x]", //
        "-1/3*(B*d-A*e)*(a+b*x)^3/(e*(b*d-a*e)*(d+e*x)^3)-1/2*B*(b*d-a*e)^2/(e^4*(d+e*x)^2)+2*b*B*(b*d-a*e)/(e^4*(d+e*x))+b^2*B*Log[d+e*x]/e^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1273
  public void test0463() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^4, x]", //
        "b^3*B*x/e^4-1/3*(b*d-a*e)^3*(B*d-A*e)/(e^5*(d+e*x)^3)+1/2*(b*d-a*e)^2*(4*b*B*d-3*A*b*e-a*B*e)/(e^5*(d+e*x)^2)-3*b*(b*d-a*e)*(2*b*B*d-A*b*e-a*B*e)/(e^5*(d+e*x))-b^2*(4*b*B*d-A*b*e-3*a*B*e)*Log[d+e*x]/e^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1301
  public void test0464() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^13, x]", //
        "1/12*(b*d-a*e)^6*(B*d-A*e)/(e^8*(d+e*x)^12)-1/11*(b*d-a*e)^5*(7*b*B*d-6*A*b*e-a*B*e)/(e^8*(d+e*x)^11)+3/10*b*(b*d-a*e)^4*(7*b*B*d-5*A*b*e-2*a*B*e)/(e^8*(d+e*x)^10)-5/9*b^2*(b*d-a*e)^3*(7*b*B*d-4*A*b*e-3*a*B*e)/(e^8*(d+e*x)^9)+5/8*b^3*(b*d-a*e)^2*(7*b*B*d-3*A*b*e-4*a*B*e)/(e^8*(d+e*x)^8)-3/7*b^4*(b*d-a*e)*(7*b*B*d-2*A*b*e-5*a*B*e)/(e^8*(d+e*x)^7)+1/6*b^5*(7*b*B*d-A*b*e-6*a*B*e)/(e^8*(d+e*x)^6)-1/5*b^6*B/(e^8*(d+e*x)^5)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1329
  public void test0465() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^12, x]", //
        "-1/11*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^11)-1/10*B*(b*d-a*e)^10/(e^12*(d+e*x)^10)+10/9*b*B*(b*d-a*e)^9/(e^12*(d+e*x)^9)-45/8*b^2*B*(b*d-a*e)^8/(e^12*(d+e*x)^8)+120/7*b^3*B*(b*d-a*e)^7/(e^12*(d+e*x)^7)-35*b^4*B*(b*d-a*e)^6/(e^12*(d+e*x)^6)+252/5*b^5*B*(b*d-a*e)^5/(e^12*(d+e*x)^5)-105/2*b^6*B*(b*d-a*e)^4/(e^12*(d+e*x)^4)+40*b^7*B*(b*d-a*e)^3/(e^12*(d+e*x)^3)-45/2*b^8*B*(b*d-a*e)^2/(e^12*(d+e*x)^2)+10*b^9*B*(b*d-a*e)/(e^12*(d+e*x))+b^10*B*Log[d+e*x]/e^12");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1345
  public void test0466() {
    check( //
        "Integrate[(A+B*x)*(d+e*x)^2/(a+b*x), x]", //
        "(A*b-a*B)*e*(b*d-a*e)*x/b^3+1/2*(A*b-a*B)*(d+e*x)^2/b^2+1/3*B*(d+e*x)^3/(b*e)+(A*b-a*B)*(b*d-a*e)^2*Log[a+b*x]/b^4");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1897
  public void test0467() {
    check( //
        "Integrate[(2+3*x)^6*(3+5*x)/(1-2*x)^3, x]", //
        "1294139/512/(1-2*x)^2+(-3916031/256)/(1-2*x)-2431647/128*x-461835/64*x^2-10611/4*x^3-44469/64*x^4-729/8*x^5-5078115/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1911
  public void test0468() {
    check( //
        "Integrate[(2+3*x)^6*(3+5*x)^2/(1-2*x)^3, x]", //
        "14235529/1024/(1-2*x)^2+(-12386759/128)/(1-2*x)-39980457/256*x-17700255/256*x^2-1024389/32*x^3-770067/64*x^4-48843/16*x^5-6075/16*x^6-18859855/128*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1925
  public void test0469() {
    check( //
        "Integrate[(2+3*x)^5*(3+5*x)^3/(1-2*x)^3, x]", //
        "22370117/1024/(1-2*x)^2+(-39220335/256)/(1-2*x)-64029233/256*x-28504029/256*x^2-1661133/32*x^3-629505/32*x^4-80595/16*x^5-10125/16*x^6-60160485/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2098
  public void test0470() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)^3*Sqrt[1-2*x], x]", //
        "-3195731/384*(1-2*x)^(3/2)+9836211/640*(1-2*x)^(5/2)-1853313/128*(1-2*x)^(7/2)+9504551/1152*(1-2*x)^(9/2)-4177401/1408*(1-2*x)^(11/2)+1101465/1664*(1-2*x)^(13/2)-10755/128*(1-2*x)^(15/2)+10125/2176*(1-2*x)^(17/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2114
  public void test0471() {
    check( //
        "Integrate[(2+3*x)^3*Sqrt[1-2*x]/(3+5*x), x]", //
        "-1299/500*(1-2*x)^(3/2)+162/125*(1-2*x)^(5/2)-27/140*(1-2*x)^(7/2)-2/625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2/625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2190
  public void test0472() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^2/(3+5*x), x]", //
        "2/375*(1-2*x)^(3/2)-111/250*(1-2*x)^(5/2)+9/70*(1-2*x)^(7/2)-22/625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2266
  public void test0473() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^3/(3+5*x), x]", //
        "22/9375*(1-2*x)^(3/2)+2/3125*(1-2*x)^(5/2)-3897/3500*(1-2*x)^(7/2)+18/25*(1-2*x)^(9/2)-27/220*(1-2*x)^(11/2)-242/15625*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+242/15625*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2418
  public void test0474() {
    check( //
        "Integrate[(2+3*x)/((1-2*x)^(3/2)*(3+5*x)), x]", //
        "-2/11*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]/Sqrt[55]+7/11/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2528
  public void test0475() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/(d+e*x)^(9/2), x]", //
        "-2/7*(B*d-A*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)*(d+e*x)^(7/2))+2/35*(3*b*B*d+4*A*b*e-7*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^2*(d+e*x)^(5/2))+4/105*b*(3*b*B*d+4*A*b*e-7*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^3*(d+e*x)^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2618
  public void test0476() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x), x]", //
        "6553/2592*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+2/81*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-5/24*(3+5*x)^(3/2)*Sqrt[1-2*x]+1/9*(3+5*x)^(5/2)*Sqrt[1-2*x]-925/864*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2634
  public void test0477() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x]), x]", //
        "-2/3*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-2/3*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2648
  public void test0478() {
    check( //
        "Integrate[(2+3*x)^3*Sqrt[1-2*x]/(3+5*x)^(5/2), x]", //
        "1071/1000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-2/15*(2+3*x)^3*Sqrt[1-2*x]/(3+5*x)^(3/2)-392/825*(2+3*x)^2*Sqrt[1-2*x]/Sqrt[3+5*x]+7/11000*(1243+1740*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2666
  public void test0479() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^3, x]", //
        "-793/108*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]-1/6*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+41/36*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2680
  public void test0480() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^7, x]", //
        "-1/18*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^6-19457889/175616*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+37/180*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5-7591/30240*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+37333/181440*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+1316353/1016064*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+137752591/14224896*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2710
  public void test0481() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)*(3+5*x)^(3/2)), x]", //
        "4/15*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]+14/3*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-22/5*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2742
  public void test0482() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x), x]", //
        "181/1080*(1-2*x)^(3/2)*(3+5*x)^(3/2)+1/12*(1-2*x)^(5/2)*(3+5*x)^(3/2)-98/243*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+1922677/777600*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7093/21600*(3+5*x)^(3/2)*Sqrt[1-2*x]-390869/259200*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2756
  public void test0483() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^3, x]", //
        "-1/6*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^2+185/36*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)+81733/5832*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+21935/2916*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]-785/36*(3+5*x)^(3/2)*Sqrt[1-2*x]+575/162*(3+5*x)^(5/2)*Sqrt[1-2*x]+34145/1944*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2772
  public void test0484() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^3*Sqrt[3+5*x]), x]", //
        "-8/27*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-3035/108*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+7/6*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+637/36*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2786
  public void test0485() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^5*(3+5*x)^(3/2)), x]", //
        "46095555/448*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/12*(1-2*x)^(3/2)/((2+3*x)^4*Sqrt[3+5*x])-181304825/12096*Sqrt[1-2*x]/Sqrt[3+5*x]+2051/216*Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x])+22957/288*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+3997345/4032*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2818
  public void test0486() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^2*Sqrt[1-2*x]), x]", //
        "103/63*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+5/9*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[10]+1/21*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2832
  public void test0487() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^6*Sqrt[1-2*x]), x]", //
        "-933031/307328*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/105*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^5+437/17640*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-14831/105840*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3-12371/592704*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+1948963/8297856*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2848
  public void test0488() {
    check( //
        "Integrate[(2+3*x)/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "3/5*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[2/5]-2/55*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2862
  public void test0489() {
    check( //
        "Integrate[1/((2+3*x)^3*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-392283/196*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-207895/6468*Sqrt[1-2*x]/(3+5*x)^(3/2)+3/14*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+753/196*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+20743985/71148*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2910
  public void test0490() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^2*Sqrt[3+5*x]), x]", //
        "-123/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-58/539*Sqrt[3+5*x]/Sqrt[1-2*x]+3/7*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2924
  public void test0491() {
    check( //
        "Integrate[(2+3*x)^5/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-111321/4000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^4/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*(2+3*x)^3*Sqrt[1-2*x]/(3+5*x)^(3/2)-4487/99825*(2+3*x)^2*Sqrt[1-2*x]/Sqrt[3+5*x]+7/5324000*(2571547+1078860*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2942
  public void test0492() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)), x]", //
        "4/231*(3+5*x)^(3/2)/(1-2*x)^(3/2)+6/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+6/49*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2986
  public void test0493() {
    check( //
        "Integrate[(2+3*x)^6/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^5/((1-2*x)^(3/2)*(3+5*x)^(3/2))+753543/8000*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-511/242*(2+3*x)^4/((3+5*x)^(3/2)*Sqrt[1-2*x])+7591/39930*(2+3*x)^3*Sqrt[1-2*x]/(3+5*x)^(3/2)+261331/2196150*(2+3*x)^2*Sqrt[1-2*x]/Sqrt[3+5*x]-7/117128000*(190406711+78981180*x)*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3022
  public void test0494() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x], x]", //
        "-148831/47250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2252/23625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-31/525*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+2/35*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-2252/4725*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3036
  public void test0495() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(9/2), x]", //
        "-173482/108045*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+23612/108045*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-118/735*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)-2/21*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)-4282/15435*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+173482/108045*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3052
  public void test0496() {
    check( //
        "Integrate[(2+3*x)^(5/2)*Sqrt[1-2*x]/(3+5*x)^(3/2), x]", //
        "-1409/3125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1091/3125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/5*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+36/125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+13/625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3066
  public void test0497() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "-36968/7*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1112/7*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/3*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+416/21*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-2780/21*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+184840/231*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3084
  public void test0498() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[2+3*x], x]", //
        "-29933/23625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1847/23625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/21*(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+74/525*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-1847/4725*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3098
  public void test0499() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(9/2), x]", //
        "-2/21*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(7/2)-962678/138915*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+249448/138915*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2108/6615*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)+362/315*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)+249448/138915*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3114
  public void test0500() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)^(3/2)/(3+5*x)^(3/2), x]", //
        "-169/3125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-496/3125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(3/2)*(2+3*x)^(3/2)/Sqrt[3+5*x]-24/125*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+458/625*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3128
  public void test0501() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^(7/2)*(3+5*x)^(5/2)), x]", //
        "-33232/35*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-301304/35*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+14/15*Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(3/2))+536/45*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+111884/315*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-16616/7*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+301304/21*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3146
  public void test0502() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/Sqrt[2+3*x], x]", //
        "-4457606/3189375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-429479/3189375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+362/2835*(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+2/27*(1-2*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]+14318/70875*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-429479/637875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3160
  public void test0503() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(9/2), x]", //
        "-2/21*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(7/2)+74/63*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(5/2)-904798/35721*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+270668/35721*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1844/567*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)-62596/3969*(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x]+1353340/35721*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3176
  public void test0504() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^(13/2)*Sqrt[3+5*x]), x]", //
        "-23204503328/194481*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-697995152/194481*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+14/33*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^(11/2)+4508/891*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(9/2)+171004/6237*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(7/2)+7173272/43659*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+333930952/305613*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+23204503328/2139291*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3190
  public void test0505() {
    check( //
        "Integrate[(1-2*x)^(5/2)*Sqrt[2+3*x]/(3+5*x)^(5/2), x]", //
        "338/1875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+992/1875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/15*(1-2*x)^(5/2)*Sqrt[2+3*x]/(3+5*x)^(3/2)-46/75*(1-2*x)^(3/2)*Sqrt[2+3*x]/Sqrt[3+5*x]-76/375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3208
  public void test0506() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[1-2*x], x]", //
        "-78472/2625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-4721/5250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/7*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-102/175*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-4721/1050*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3222
  public void test0507() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(7/2)*Sqrt[1-2*x]), x]", //
        "53194/46305*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-34154/46305*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/105*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)+544/6615*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)-53194/46305*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3254
  public void test0508() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-61151/6250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-942/3125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-2/55*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-69/1375*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-2577/6875*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3268
  public void test0509() {
    check( //
        "Integrate[1/((2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-17804/77*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-536/77*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+6/7*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-1340/231*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+89020/2541*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3292
  public void test0510() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*Sqrt[2+3*x]), x]", //
        "EllipticE[ArcSin[Sqrt[5/11]*Sqrt[1-2*x]],33/35]*Sqrt[5/7]+2/7*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3306
  public void test0511() {
    check( //
        "Integrate[(2+3*x)^(5/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2), x]", //
        "42696881/18900*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+1284329/18900*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(2+3*x)^(5/2)*(3+5*x)^(5/2)/Sqrt[1-2*x]+5/3*(2+3*x)^(3/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]+4853/105*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+93/14*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+1284329/3780*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3322
  public void test0512() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(3/2)*Sqrt[3+5*x]), x]", //
        "-62/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-8/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])+186/539*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3336
  public void test0513() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "118898/15125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2657/15125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/11*(2+3*x)^(5/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(3/2)-4289/99825*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3354
  public void test0514() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^(3/2)), x]", //
        "458/343*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-178/343*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*Sqrt[2+3*x])+194/1617*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])-458/3773*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3368
  public void test0515() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[2+3*x]/(1-2*x)^(5/2), x]", //
        "-4621/42*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-139/42*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+1/3*(3+5*x)^(5/2)*Sqrt[2+3*x]/(1-2*x)^(3/2)-104/21*(3+5*x)^(3/2)*Sqrt[2+3*x]/Sqrt[1-2*x]-695/42*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3384
  public void test0516() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)^(7/2)*Sqrt[3+5*x]), x]", //
        "-26062156/924385*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-837304/924385*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/231*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(5/2))+1336/17787*Sqrt[3+5*x]/((2+3*x)^(5/2)*Sqrt[1-2*x])-806/207515*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+349904/1452605*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+26062156/10168235*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3398
  public void test0517() {
    check( //
        "Integrate[(2+3*x)^(7/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^(5/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2))-29933/33275*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-1847/33275*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-63/121*(2+3*x)^(3/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])+908/19965*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+29933/219615*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:40
  public void test0518() {
    check( //
        "Integrate[(1+a*x)/(Sqrt[a*x]*Sqrt[1-a*x]), x]", //
        "-3/2*ArcSin[1-2*a*x]/a-Sqrt[a*x]*Sqrt[1-a*x]/a");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:69
  public void test0519() {
    check( //
        "Integrate[(7+5*x)^3*Sqrt[2-3*x]*Sqrt[1+4*x]/Sqrt[-5+2*x], x]", //
        "-2161804579/54432*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+2629157597/163296*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+46134551/38880*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+26291/540*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+1679/756*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+1/9*(7+5*x)^3*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:100
  public void test0520() {
    check( //
        "Integrate[(a+b*x)/(Sqrt[c+d*x]*Sqrt[e+f*x]*Sqrt[g+h*x]), x]", //
        "2*b*EllipticE[ArcSin[Sqrt[f]*Sqrt[c+d*x]/Sqrt[-d*e+c*f]],(d*e-c*f)*h/(f*(d*g-c*h))]*Sqrt[-d*e+c*f]*Sqrt[d*(e+f*x)/(d*e-c*f)]*Sqrt[g+h*x]/(d*h*Sqrt[f]*Sqrt[e+f*x]*Sqrt[d*(g+h*x)/(d*g-c*h)])-2*(b*g-a*h)*EllipticF[ArcSin[Sqrt[f]*Sqrt[c+d*x]/Sqrt[-d*e+c*f]],(d*e-c*f)*h/(f*(d*g-c*h))]*Sqrt[-d*e+c*f]*Sqrt[d*(e+f*x)/(d*e-c*f)]*Sqrt[d*(g+h*x)/(d*g-c*h)]/(d*h*Sqrt[f]*Sqrt[e+f*x]*Sqrt[g+h*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:16
  public void test0521() {
    check( //
        "Integrate[a+b*x^2, x]", //
        "a*x+1/3*b*x^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:30
  public void test0522() {
    check( //
        "Integrate[(a+b*x^2)^2/x, x]", //
        "a*b*x^2+1/4*b^2*x^4+a^2*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:72
  public void test0523() {
    check( //
        "Integrate[(a+b*x^2)^5/x^5, x]", //
        "-1/4*a^5/x^4-5/2*a^4*b/x^2+5*a^2*b^3*x^2+5/4*a*b^4*x^4+1/6*b^5*x^6+10*a^3*b^2*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:100
  public void test0524() {
    check( //
        "Integrate[x^5*(a+b*x^2)^8, x]", //
        "1/18*a^2*(a+b*x^2)^9/b^3-1/10*a*(a+b*x^2)^10/b^3+1/22*(a+b*x^2)^11/b^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:114
  public void test0525() {
    check( //
        "Integrate[(a+b*x^2)^8/x^23, x]", //
        "-1/22*(a+b*x^2)^9/(a*x^22)+1/110*b*(a+b*x^2)^9/(a^2*x^20)-1/990*b^2*(a+b*x^2)^9/(a^3*x^18)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:144
  public void test0526() {
    check( //
        "Integrate[x^4/(a+b*x^2), x]", //
        "-a*x/b^2+1/3*x^3/b+a^(3/2)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(5/2)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:172
  public void test0527() {
    check( //
        "Integrate[1/(x*(a+b*x^2)^2), x]", //
        "1/2/(a*(a+b*x^2))+Log[x]/a^2-1/2*Log[a+b*x^2]/a^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:186
  public void test0528() {
    check( //
        "Integrate[x^5/(a+b*x^2)^3, x]", //
        "-1/4*a^2/(b^3*(a+b*x^2)^2)+a/(b^3*(a+b*x^2))+1/2*Log[a+b*x^2]/b^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:200
  public void test0529() {
    check( //
        "Integrate[1/(a+b*x^2)^3, x]", //
        "1/4*x/(a*(a+b*x^2)^2)+3/8*x/(a^2*(a+b*x^2))+3/8*ArcTan[x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:214
  public void test0530() {
    check( //
        "Integrate[x^7/(a+b*x^2)^10, x]", //
        "1/18*a^3/(b^4*(a+b*x^2)^9)-3/16*a^2/(b^4*(a+b*x^2)^8)+3/14*a/(b^4*(a+b*x^2)^7)+(-1/12)/(b^4*(a+b*x^2)^6)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:242
  public void test0531() {
    check( //
        "Integrate[1/(x*(a-b*x^2)), x]", //
        "Log[x]/a-1/2*Log[a-b*x^2]/a");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:256
  public void test0532() {
    check( //
        "Integrate[1/(x*(a-b*x^2)^3), x]", //
        "1/4/(a*(a-b*x^2)^2)+1/2/(a^2*(a-b*x^2))+Log[x]/a^3-1/2*Log[a-b*x^2]/a^3");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:442
  public void test0533() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)/x^16, x]", //
        "-1/15*(a+b*x^2)^(7/2)/(a*x^15)+8/195*b*(a+b*x^2)^(7/2)/(a^2*x^13)-16/715*b^2*(a+b*x^2)^(7/2)/(a^3*x^11)+64/6435*b^3*(a+b*x^2)^(7/2)/(a^4*x^9)-128/45045*b^4*(a+b*x^2)^(7/2)/(a^5*x^7)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:512
  public void test0534() {
    check( //
        "Integrate[x^2*Sqrt[-9-4*x^2], x]", //
        "81/64*ArcTan[2*x/Sqrt[-9-4*x^2]]+9/32*x*Sqrt[-9-4*x^2]+1/4*x^3*Sqrt[-9-4*x^2]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:528
  public void test0535() {
    check( //
        "Integrate[1/(x*Sqrt[a+b*x^2]), x]", //
        "-ArcTanh[Sqrt[a+b*x^2]/Sqrt[a]]/Sqrt[a]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:542
  public void test0536() {
    check( //
        "Integrate[1/(x^4*(a+b*x^2)^(3/2)), x]", //
        "(-1/3)/(a*x^3*Sqrt[a+b*x^2])+4/3*b/(a^2*x*Sqrt[a+b*x^2])+8/3*b^2*x/(a^3*Sqrt[a+b*x^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:751
  public void test0537() {
    check( //
        "Integrate[(a+b*x^2)^(4/3), x]", //
        "24/55*a*x*(a+b*x^2)^(1/3)+3/11*x*(a+b*x^2)^(4/3)-16/55*3^(3/4)*a^2*(a^(1/3)-(a+b*x^2)^(1/3))*EllipticF[ArcSin[(-(a+b*x^2)^(1/3)+a^(1/3)*(1+Sqrt[3]))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))],-7+4*Sqrt[3]]*Sqrt[(a^(2/3)+a^(1/3)*(a+b*x^2)^(1/3)+(a+b*x^2)^(2/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2]*Sqrt[2-Sqrt[3]]/(b*x*Sqrt[-a^(1/3)*(a^(1/3)-(a+b*x^2)^(1/3))/(-(a+b*x^2)^(1/3)+a^(1/3)*(1-Sqrt[3]))^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:916
  public void test0538() {
    check( //
        "Integrate[1/(x^2*(a+b*x^2)^(5/4)), x]", //
        "(-1)/(a*x*(a+b*x^2)^(1/4))-3*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]*Sqrt[b]/(a^(3/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:930
  public void test0539() {
    check( //
        "Integrate[1/(a-b*x^2)^(9/4), x]", //
        "2/5*x/(a*(a-b*x^2)^(5/4))+6/5*x/(a^2*(a-b*x^2)^(1/4))-6/5*(1-b*x^2/a)^(1/4)*EllipticE[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/(a^(3/2)*(a-b*x^2)^(1/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1004
  public void test0540() {
    check( //
        "Integrate[(a+b*x^2)^(1/4)/(c*x)^(19/2), x]", //
        "-2/5*(a+b*x^2)^(5/4)/(a*c*(c*x)^(17/2))+8/15*(a+b*x^2)^(9/4)/(a^2*c*(c*x)^(17/2))-64/195*(a+b*x^2)^(13/4)/(a^3*c*(c*x)^(17/2))+256/3315*(a+b*x^2)^(17/4)/(a^4*c*(c*x)^(17/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1034
  public void test0541() {
    check( //
        "Integrate[1/((c*x)^(13/2)*(a-b*x^2)^(1/4)), x]", //
        "-2/3*(a-b*x^2)^(3/4)/(a*c*(c*x)^(11/2))+16/21*(a-b*x^2)^(7/4)/(a^2*c*(c*x)^(11/2))-64/231*(a-b*x^2)^(11/4)/(a^3*c*(c*x)^(11/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1062
  public void test0542() {
    check( //
        "Integrate[1/((c*x)^(1/2)*(a+b*x^2)^(5/4)), x]", //
        "2*Sqrt[c*x]/(a*c*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1100
  public void test0543() {
    check( //
        "Integrate[(a+b*x^2)^(1/6), x]", //
        "3/4*x*(a+b*x^2)^(1/6)+1/4*3^(3/4)*a*(a+b*x^2)^(1/6)*(1-(a/(a+b*x^2))^(1/3))*EllipticF[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]/(b*x*(a/(a+b*x^2))^(1/3)*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1134
  public void test0544() {
    check( //
        "Integrate[x^3*(a+b*x^2)^p, x]", //
        "-1/2*a*(a+b*x^2)^(1+p)/(b^2*(1+p))+1/2*(a+b*x^2)^(2+p)/(b^2*(2+p))");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:35
  public void test0545() {
    check( //
        "Integrate[1/((a+b*x^2)*(c+d*x^2)), x]", //
        "ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/((b*c-a*d)*Sqrt[a])-ArcTan[x*Sqrt[d]/Sqrt[c]]*Sqrt[d]/((b*c-a*d)*Sqrt[c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:178
  public void test0546() {
    check( //
        "Integrate[1/((a-b*x^2)^(1/3)*(-9*a*d/b+d*x^2)), x]", //
        "1/12*ArcTanh[1/3*(a^(1/3)-(a-b*x^2)^(1/3))^2/(a^(1/6)*x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d)-1/12*ArcTanh[1/3*x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/(a^(5/6)*d)-1/4*ArcTan[a^(1/6)*(a^(1/3)-(a-b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:203
  public void test0547() {
    check( //
        "Integrate[Sqrt[c+d*x^2]/(a+b*x^2)^(1/2), x]", //
        "d*x*Sqrt[a+b*x^2]/(b*Sqrt[c+d*x^2])+c^(3/2)*EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[a+b*x^2]/(a*Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[d]*Sqrt[a+b*x^2]/(b*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:217
  public void test0548() {
    check( //
        "Integrate[Sqrt[4-x^2]/Sqrt[c+d*x^2], x]", //
        "-EllipticE[ArcSin[1/2*x],-4*d/c]*Sqrt[c+d*x^2]/(d*Sqrt[1+d*x^2/c])+(c+4*d)*EllipticF[ArcSin[1/2*x],-4*d/c]*Sqrt[1+d*x^2/c]/(d*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:231
  public void test0549() {
    check( //
        "Integrate[Sqrt[4+x^2]/Sqrt[2+3*x^2], x]", //
        "1/3*x*Sqrt[2+3*x^2]/Sqrt[4+x^2]-1/3*EllipticE[ArcTan[1/2*x],-5]*Sqrt[2]*Sqrt[2+3*x^2]/(Sqrt[4+x^2]*Sqrt[(2+3*x^2)/(4+x^2)])+2*EllipticF[ArcTan[1/2*x],-5]*Sqrt[2]*Sqrt[2+3*x^2]/(Sqrt[4+x^2]*Sqrt[(2+3*x^2)/(4+x^2)])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:247
  public void test0550() {
    check( //
        "Integrate[(a+b*x^2)^(1/2)/(c+d*x^2)^(3/2), x]", //
        "EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[a+b*x^2]/(Sqrt[c]*Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:304
  public void test0551() {
    check( //
        "Integrate[Sqrt[-a-b*x^2]/Sqrt[c+d*x^2], x]", //
        "x*Sqrt[-a-b*x^2]/Sqrt[c+d*x^2]-EllipticE[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])+EllipticF[ArcTan[x*Sqrt[d]/Sqrt[c]],1-b*c/(a*d)]*Sqrt[c]*Sqrt[-a-b*x^2]/(Sqrt[d]*Sqrt[c*(a+b*x^2)/(a*(c+d*x^2))]*Sqrt[c+d*x^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:318
  public void test0552() {
    check( //
        "Integrate[Sqrt[-c+d*x^2]/Sqrt[-a+b*x^2], x]", //
        "EllipticE[ArcSin[x*Sqrt[b]/Sqrt[a]],a*d/(b*c)]*Sqrt[a]*Sqrt[1-b*x^2/a]*Sqrt[-c+d*x^2]/(Sqrt[b]*Sqrt[-a+b*x^2]*Sqrt[1-d*x^2/c])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:28
  public void test0553() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^4, x]", //
        "-1/3*a^2*A/x^3-a*(2*A*b+a*B)/x+b*(A*b+2*a*B)*x+1/3*b^2*B*x^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:42
  public void test0554() {
    check( //
        "Integrate[x*(a+b*x^2)^5*(A+B*x^2), x]", //
        "1/12*(A*b-a*B)*(a+b*x^2)^6/b^2+1/14*B*(a+b*x^2)^7/b^2");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:56
  public void test0555() {
    check( //
        "Integrate[(a+b*x^2)^5*(A+B*x^2)/x^13, x]", //
        "-1/10*a^5*B/x^10-5/8*a^4*b*B/x^8-5/3*a^3*b^2*B/x^6-5/2*a^2*b^3*B/x^4-5/2*a*b^4*B/x^2-1/12*A*(a+b*x^2)^6/(a*x^12)+b^5*B*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:114
  public void test0556() {
    check( //
        "Integrate[x^4*(A+B*x^2)/(a+b*x^2)^3, x]", //
        "B*x/b^3+1/4*a*(A*b-a*B)*x/(b^3*(a+b*x^2)^2)-1/8*(5*A*b-9*a*B)*x/(b^3*(a+b*x^2))+3/8*(A*b-5*a*B)*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(7/2)*Sqrt[a])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:142
  public void test0557() {
    check( //
        "Integrate[x*(a*c+b*c*x^2)/(a+b*x^2)^2, x]", //
        "1/2*c*Log[a+b*x^2]/b");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:174
  public void test0558() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^2/x^3, x]", //
        "-1/2*a^2*c^2/x^2+1/2*(b^2*c^2+4*a*b*c*d+a^2*d^2)*x^2+1/2*b*d*(b*c+a*d)*x^4+1/6*b^2*d^2*x^6+2*a*c*(b*c+a*d)*Log[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:190
  public void test0559() {
    check( //
        "Integrate[x*(a+b*x^2)^2/(c+d*x^2), x]", //
        "-1/2*b*(b*c-a*d)*x^2/d^2+1/4*(a+b*x^2)^2/d+1/2*(b*c-a*d)^2*Log[c+d*x^2]/d^3");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:224
  public void test0560() {
    check( //
        "Integrate[x^4*(c+d*x^2)/(a+b*x^2), x]", //
        "-a*(b*c-a*d)*x/b^3+1/3*(b*c-a*d)*x^3/b^2+1/5*d*x^5/b+a^(3/2)*(b*c-a*d)*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(7/2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:238
  public void test0561() {
    check( //
        "Integrate[(c+d*x^2)^2/(a+b*x^2), x]", //
        "d*(2*b*c-a*d)*x/b^2+1/3*d^2*x^3/b+(b*c-a*d)^2*ArcTan[x*Sqrt[b]/Sqrt[a]]/(b^(5/2)*Sqrt[a])");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:268
  public void test0562() {
    check( //
        "Integrate[x^5/((a+b*x^2)^2*(c+d*x^2)), x]", //
        "-1/2*a^2/(b^2*(b*c-a*d)*(a+b*x^2))-1/2*a*(2*b*c-a*d)*Log[a+b*x^2]/(b^2*(b*c-a*d)^2)+1/2*c^2*Log[c+d*x^2]/(d*(b*c-a*d)^2)");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:344
  public void test0563() {
    check( //
        "Integrate[x^3/((a+b*x^2)^2*(c+d*x^2)^3), x]", //
        "1/2*a*b/((b*c-a*d)^3*(a+b*x^2))+1/4*c/((b*c-a*d)^2*(c+d*x^2)^2)+1/2*(b*c+a*d)/((b*c-a*d)^3*(c+d*x^2))+1/2*b*(b*c+2*a*d)*Log[a+b*x^2]/(b*c-a*d)^4-1/2*b*(b*c+2*a*d)*Log[c+d*x^2]/(b*c-a*d)^4");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:404
  public void test0564() {
    check( //
        "Integrate[(a+b*x^2)^2*(A+B*x^2)/x^(5/2), x]", //
        "-2/3*a^2*A/x^(3/2)+2/5*b*(A*b+2*a*B)*x^(5/2)+2/9*b^2*B*x^(9/2)+2*a*(2*A*b+a*B)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:420
  public void test0565() {
    check( //
        "Integrate[(A+B*x^2)/((a+b*x^2)*Sqrt[x]), x]", //
        "-(A*b-a*B)*ArcTan[1-b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(3/4)*b^(5/4)*Sqrt[2])+(A*b-a*B)*ArcTan[1+b^(1/4)*Sqrt[2]*Sqrt[x]/a^(1/4)]/(a^(3/4)*b^(5/4)*Sqrt[2])-1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]-a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(3/4)*b^(5/4)*Sqrt[2])+1/2*(A*b-a*B)*Log[Sqrt[a]+x*Sqrt[b]+a^(1/4)*b^(1/4)*Sqrt[2]*Sqrt[x]]/(a^(3/4)*b^(5/4)*Sqrt[2])+2*B*Sqrt[x]/b");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:466
  public void test0566() {
    check( //
        "Integrate[(a+b*x^2)^2*(c+d*x^2)^3/x^(5/2), x]", //
        "-2/3*a^2*c^3/x^(3/2)+2/5*c*(b^2*c^2+6*a*b*c*d+3*a^2*d^2)*x^(5/2)+2/9*d*(3*b^2*c^2+6*a*b*c*d+a^2*d^2)*x^(9/2)+2/13*b*d^2*(3*b*c+2*a*d)*x^(13/2)+2/17*b^2*d^3*x^(17/2)+2*a*c^2*(2*b*c+3*a*d)*Sqrt[x]");
  }

  // 1.1.2.4 (e x)^m (a+b x^2)^p (c+d x^2)^q.input:482
  public void test0567() {
    check( //
        "Integrate[x^(3/2)*(a+b*x^2)^2/(c+d*x^2)^2, x]", //
        "2/5*b^2*x^(5/2)/d^2+1/2*(b*c-a*d)^2*x^(5/2)/(c*d^2*(c+d*x^2))-1/4*(b*c-a*d)*(9*b*c-a*d)*ArcTan[1-d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(3/4)*d^(13/4)*Sqrt[2])+1/4*(b*c-a*d)*(9*b*c-a*d)*ArcTan[1+d^(1/4)*Sqrt[2]*Sqrt[x]/c^(1/4)]/(c^(3/4)*d^(13/4)*Sqrt[2])-1/8*(b*c-a*d)*(9*b*c-a*d)*Log[Sqrt[c]+x*Sqrt[d]-c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(3/4)*d^(13/4)*Sqrt[2])+1/8*(b*c-a*d)*(9*b*c-a*d)*Log[Sqrt[c]+x*Sqrt[d]+c^(1/4)*d^(1/4)*Sqrt[2]*Sqrt[x]]/(c^(3/4)*d^(13/4)*Sqrt[2])-1/2*(b*c-a*d)*(9*b*c-a*d)*Sqrt[x]/(c*d^3)");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1520
  public void test0568() {
    check( //
        "Integrate[1/(x^7*(1-x^6)), x]", //
        "(-1/6)/x^6+Log[x]-1/6*Log[1-x^6]");
  }

  // 1.1.3.2 (c x)^m (a+b x^n)^p.input:1560
  public void test0569() {
    check( //
        "Integrate[1/(x*Sqrt[2+x^6]), x]", //
        "-1/3*ArcTanh[Sqrt[2+x^6]/Sqrt[2]]/Sqrt[2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:63
  public void test0570() {
    check( //
        "Integrate[1/(c+d*(a+b*x))^(1/2), x]", //
        "2*Sqrt[c+d*(a+b*x)]/(b*d)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:181
  public void test0571() {
    check( //
        "Integrate[(a+b*x)^10/x^18, x]", //
        "-1/17*(a+b*x)^11/(a*x^17)+3/136*b*(a+b*x)^11/(a^2*x^16)-1/136*b^2*(a+b*x)^11/(a^3*x^15)+1/476*b^3*(a+b*x)^11/(a^4*x^14)-3/6188*b^4*(a+b*x)^11/(a^5*x^13)+1/12376*b^5*(a+b*x)^11/(a^6*x^12)-1/136136*b^6*(a+b*x)^11/(a^7*x^11)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:289
  public void test0572() {
    check( //
        "Integrate[1/x^10, x]", //
        "(-1/9)/x^9");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:321
  public void test0573() {
    check( //
        "Integrate[1/(x*(1+b*x)), x]", //
        "Log[x]-Log[1+b*x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:655
  public void test0574() {
    check( //
        "Integrate[1/((a+b*x)^(5/2)*Sqrt[x]), x]", //
        "2/3*Sqrt[x]/(a*(a+b*x)^(3/2))+4/3*Sqrt[x]/(a^2*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:669
  public void test0575() {
    check( //
        "Integrate[1/(x^(5/2)*(a-b*x)^(3/2)), x]", //
        "2/(a*x^(3/2)*Sqrt[a-b*x])-8/3*Sqrt[a-b*x]/(a^2*x^(3/2))-16/3*b*Sqrt[a-b*x]/(a^3*Sqrt[x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:683
  public void test0576() {
    check( //
        "Integrate[1/(x^(9/2)*Sqrt[2+b*x]), x]", //
        "-1/7*Sqrt[2+b*x]/x^(7/2)+3/35*b*Sqrt[2+b*x]/x^(5/2)-2/35*b^2*Sqrt[2+b*x]/x^(3/2)+2/35*b^3*Sqrt[2+b*x]/Sqrt[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:857
  public void test0577() {
    check( //
        "Integrate[(c*x^2)^(3/2)*(a+b*x)/x^2, x]", //
        "1/2*a*c*x*Sqrt[c*x^2]+1/3*b*c*x^2*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:873
  public void test0578() {
    check( //
        "Integrate[(a+b*x)/Sqrt[c*x^2], x]", //
        "b*x^2/Sqrt[c*x^2]+a*x*Log[x]/Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:887
  public void test0579() {
    check( //
        "Integrate[x^2*(a+b*x)/(c*x^2)^(5/2), x]", //
        "-1/2*(a+b*x)^2/(a*c^2*x*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:905
  public void test0580() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[c*x^2]/x^4, x]", //
        "-1/2*a^2*Sqrt[c*x^2]/x^3-2*a*b*Sqrt[c*x^2]/x^2+b^2*Log[x]*Sqrt[c*x^2]/x");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:919
  public void test0581() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^2/x^4, x]", //
        "1/2*a^2*c^2*x*Sqrt[c*x^2]+2/3*a*b*c^2*x^2*Sqrt[c*x^2]+1/4*b^2*c^2*x^3*Sqrt[c*x^2]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:935
  public void test0582() {
    check( //
        "Integrate[(a+b*x)^2/(c*x^2)^(3/2), x]", //
        "-2*a*b/(c*Sqrt[c*x^2])-1/2*a^2/(c*x*Sqrt[c*x^2])+b^2*x*Log[x]/(c*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:967
  public void test0583() {
    check( //
        "Integrate[(c*x^2)^(3/2)/(x^6*(a+b*x)), x]", //
        "-1/2*c*Sqrt[c*x^2]/(a*x^3)+b*c*Sqrt[c*x^2]/(a^2*x^2)+b^2*c*Log[x]*Sqrt[c*x^2]/(a^3*x)-b^2*c*Log[a+b*x]*Sqrt[c*x^2]/(a^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:983
  public void test0584() {
    check( //
        "Integrate[1/((a+b*x)*Sqrt[c*x^2]), x]", //
        "x*Log[x]/(a*Sqrt[c*x^2])-x*Log[a+b*x]/(a*Sqrt[c*x^2])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1001
  public void test0585() {
    check( //
        "Integrate[x*Sqrt[c*x^2]/(a+b*x)^2, x]", //
        "Sqrt[c*x^2]/b^2-a^2*Sqrt[c*x^2]/(b^3*x*(a+b*x))-2*a*Log[a+b*x]*Sqrt[c*x^2]/(b^3*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1035
  public void test0586() {
    check( //
        "Integrate[x^2*(a+b*x)^n*Sqrt[c*x^2], x]", //
        "-a^3*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b^4*(1+n)*x)+3*a^2*(a+b*x)^(2+n)*Sqrt[c*x^2]/(b^4*(2+n)*x)-3*a*(a+b*x)^(3+n)*Sqrt[c*x^2]/(b^4*(3+n)*x)+(a+b*x)^(4+n)*Sqrt[c*x^2]/(b^4*(4+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1055
  public void test0587() {
    check( //
        "Integrate[(c*x^2)^(5/2)*(a+b*x)^n/x^5, x]", //
        "c^2*(a+b*x)^(1+n)*Sqrt[c*x^2]/(b*(1+n)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1088
  public void test0588() {
    check( //
        "Integrate[(d*x)^m*(c*x^2)^(3/2)*(a+b*x), x]", //
        "a*c*(d*x)^(4+m)*Sqrt[c*x^2]/(d^4*(4+m)*x)+b*c*(d*x)^(5+m)*Sqrt[c*x^2]/(d^5*(5+m)*x)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1142
  public void test0589() {
    check( //
        "Integrate[1/((a+b*x)^3*(a*d/b+d*x)^3), x]", //
        "-1/5*b^2/(d^3*(a+b*x)^5)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1286
  public void test0590() {
    check( //
        "Integrate[1/((1-x)^(3/2)*(1+x)^(5/2)), x]", //
        "1/((1+x)^(3/2)*Sqrt[1-x])-2/3*Sqrt[1-x]/(1+x)^(3/2)-2/3*Sqrt[1-x]/Sqrt[1+x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1338
  public void test0591() {
    check( //
        "Integrate[1/((a-I*a*x)^(5/4)*(a+I*a*x)^(1/4)), x]", //
        "(-2*I)/(a*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))+2*(1+x^2)^(1/4)*EllipticE[1/2*ArcTan[x],2]/(a*(a-I*a*x)^(1/4)*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1352
  public void test0592() {
    check( //
        "Integrate[1/((a-I*a*x)^(13/4)*(a+I*a*x)^(3/4)), x]", //
        "-2/9*I*(a+I*a*x)^(1/4)/(a^2*(a-I*a*x)^(9/4))-8/45*I*(a+I*a*x)^(1/4)/(a^3*(a-I*a*x)^(5/4))-16/45*I*(a+I*a*x)^(1/4)/(a^4*(a-I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1380
  public void test0593() {
    check( //
        "Integrate[1/((a-I*a*x)^(11/4)*(a+I*a*x)^(5/4)), x]", //
        "(-2/7*I)/(a^2*(a-I*a*x)^(7/4)*(a+I*a*x)^(1/4))+(-8/21*I)/(a^3*(a-I*a*x)^(3/4)*(a+I*a*x)^(1/4))+16/21*I*(a-I*a*x)^(1/4)/(a^4*(a+I*a*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1676
  public void test0594() {
    check( //
        "Integrate[(a+b*x)^5*(a*c+b*c*x)^(1/2), x]", //
        "2/13*(a*c+b*c*x)^(13/2)/(b*c^6)");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1740
  public void test0595() {
    check( //
        "Integrate[1/((a+b*x)^(11/2)*(c+d*x)^(1/2)), x]", //
        "-2/9*Sqrt[c+d*x]/((b*c-a*d)*(a+b*x)^(9/2))+16/63*d*Sqrt[c+d*x]/((b*c-a*d)^2*(a+b*x)^(7/2))-32/105*d^2*Sqrt[c+d*x]/((b*c-a*d)^3*(a+b*x)^(5/2))+128/315*d^3*Sqrt[c+d*x]/((b*c-a*d)^4*(a+b*x)^(3/2))-256/315*d^4*Sqrt[c+d*x]/((b*c-a*d)^5*Sqrt[a+b*x])");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:1974
  public void test0596() {
    check( //
        "Integrate[1/((a+b*x)^(17/4)*(c+d*x)^(3/4)), x]", //
        "-4/13*(c+d*x)^(1/4)/((b*c-a*d)*(a+b*x)^(13/4))+16/39*d*(c+d*x)^(1/4)/((b*c-a*d)^2*(a+b*x)^(9/4))-128/195*d^2*(c+d*x)^(1/4)/((b*c-a*d)^3*(a+b*x)^(5/4))+512/195*d^3*(c+d*x)^(1/4)/((b*c-a*d)^4*(a+b*x)^(1/4))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2053
  public void test0597() {
    check( //
        "Integrate[(a+b*x)^(1/6)/(c+d*x)^(31/6), x]", //
        "6/25*(a+b*x)^(7/6)/((b*c-a*d)*(c+d*x)^(25/6))+108/475*b*(a+b*x)^(7/6)/((b*c-a*d)^2*(c+d*x)^(19/6))+1296/6175*b^2*(a+b*x)^(7/6)/((b*c-a*d)^3*(c+d*x)^(13/6))+7776/43225*b^3*(a+b*x)^(7/6)/((b*c-a*d)^4*(c+d*x)^(7/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2079
  public void test0598() {
    check( //
        "Integrate[(a+b*x)^(7/6)/(c+d*x)^(37/6), x]", //
        "6/31*(a+b*x)^(13/6)/((b*c-a*d)*(c+d*x)^(31/6))+108/775*b*(a+b*x)^(13/6)/((b*c-a*d)^2*(c+d*x)^(25/6))+1296/14725*b^2*(a+b*x)^(13/6)/((b*c-a*d)^3*(c+d*x)^(19/6))+7776/191425*b^3*(a+b*x)^(13/6)/((b*c-a*d)^4*(c+d*x)^(13/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2107
  public void test0599() {
    check( //
        "Integrate[1/((a+b*x)^(5/6)*(c+d*x)^(25/6)), x]", //
        "6/19*(a+b*x)^(1/6)/((b*c-a*d)*(c+d*x)^(19/6))+108/247*b*(a+b*x)^(1/6)/((b*c-a*d)^2*(c+d*x)^(13/6))+1296/1729*b^2*(a+b*x)^(1/6)/((b*c-a*d)^3*(c+d*x)^(7/6))+7776/1729*b^3*(a+b*x)^(1/6)/((b*c-a*d)^4*(c+d*x)^(1/6))");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2189
  public void test0600() {
    check( //
        "Integrate[a+d/x^3+c/x^2+b/x, x]", //
        "-1/2*d/x^2-c/x+a*x+b*Log[x]");
  }

  // 1.1.1.2 (a+b x)^m (c+d x)^n.input:2203
  public void test0601() {
    check( //
        "Integrate[-1/2*x+2/Sqrt[x]+Sqrt[x], x]", //
        "2/3*x^(3/2)-1/4*x^2+4*Sqrt[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:54
  public void test0602() {
    check( //
        "Integrate[(a+b*x)*(a*c-b*c*x)^5/x^10, x]", //
        "-1/9*a^6*c^5/x^9+1/2*a^5*b*c^5/x^8-5/7*a^4*b^2*c^5/x^7+a^2*b^4*c^5/x^5-a*b^5*c^5/x^4+1/3*b^6*c^5/x^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:126
  public void test0603() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/x^2, x]", //
        "-a^2*A/x+b*(A*b+2*a*B)*x+1/2*b^2*B*x^2+a*(2*A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:140
  public void test0604() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/x^3, x]", //
        "-1/2*a^3*A/x^2-a^2*(3*A*b+a*B)/x+b^2*(A*b+3*a*B)*x+1/2*b^3*B*x^2+3*a*b*(A*b+a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:154
  public void test0605() {
    check( //
        "Integrate[(a+b*x)^5*(A+B*x)/x, x]", //
        "5*a^4*A*b*x+5*a^3*A*b^2*x^2+10/3*a^2*A*b^3*x^3+5/4*a*A*b^4*x^4+1/5*A*b^5*x^5+1/6*B*(a+b*x)^6/b+a^5*A*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:182
  public void test0606() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^6, x]", //
        "-1/5*a^10*A/x^5-1/4*a^9*(10*A*b+a*B)/x^4-5/3*a^8*b*(9*A*b+2*a*B)/x^3-15/2*a^7*b^2*(8*A*b+3*a*B)/x^2-30*a^6*b^3*(7*A*b+4*a*B)/x+42*a^4*b^5*(5*A*b+6*a*B)*x+15*a^3*b^6*(4*A*b+7*a*B)*x^2+5*a^2*b^7*(3*A*b+8*a*B)*x^3+5/4*a*b^8*(2*A*b+9*a*B)*x^4+1/5*b^9*(A*b+10*a*B)*x^5+1/6*b^10*B*x^6+42*a^5*b^4*(6*A*b+5*a*B)*Log[x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:196
  public void test0607() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/x^20, x]", //
        "-1/19*a^10*A/x^19-1/18*a^9*(10*A*b+a*B)/x^18-5/17*a^8*b*(9*A*b+2*a*B)/x^17-15/16*a^7*b^2*(8*A*b+3*a*B)/x^16-2*a^6*b^3*(7*A*b+4*a*B)/x^15-3*a^5*b^4*(6*A*b+5*a*B)/x^14-42/13*a^4*b^5*(5*A*b+6*a*B)/x^13-5/2*a^3*b^6*(4*A*b+7*a*B)/x^12-15/11*a^2*b^7*(3*A*b+8*a*B)/x^11-1/2*a*b^8*(2*A*b+9*a*B)/x^10-1/9*b^9*(A*b+10*a*B)/x^9-1/8*b^10*B/x^8");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:326
  public void test0608() {
    check( //
        "Integrate[1/((a+b*x)*(c+d*x)), x]", //
        "Log[a+b*x]/(b*c-a*d)-Log[c+d*x]/(b*c-a*d)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:457
  public void test0609() {
    check( //
        "Integrate[(A+B*x)/(x^(5/2)*(a+b*x)), x]", //
        "-2/3*A/(a*x^(3/2))+2*(A*b-a*B)*ArcTan[Sqrt[b]*Sqrt[x]/Sqrt[a]]*Sqrt[b]/a^(5/2)+2*(A*b-a*B)/(a^2*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:629
  public void test0610() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/x^(13/2), x]", //
        "-2/11*A*(a+b*x)^(3/2)/(a*x^(11/2))+2/99*(8*A*b-11*a*B)*(a+b*x)^(3/2)/(a^2*x^(9/2))-4/231*b*(8*A*b-11*a*B)*(a+b*x)^(3/2)/(a^3*x^(7/2))+16/1155*b^2*(8*A*b-11*a*B)*(a+b*x)^(3/2)/(a^4*x^(5/2))-32/3465*b^3*(8*A*b-11*a*B)*(a+b*x)^(3/2)/(a^5*x^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:687
  public void test0611() {
    check( //
        "Integrate[(A+B*x)/(x^(7/2)*(a+b*x)^(5/2)), x]", //
        "-2/5*A/(a*x^(5/2)*(a+b*x)^(3/2))-2/15*(8*A*b-5*a*B)/(a^2*x^(3/2)*(a+b*x)^(3/2))-4/5*(8*A*b-5*a*B)/(a^3*x^(3/2)*Sqrt[a+b*x])+16/15*(8*A*b-5*a*B)*Sqrt[a+b*x]/(a^4*x^(3/2))-32/15*b*(8*A*b-5*a*B)*Sqrt[a+b*x]/(a^5*Sqrt[x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:769
  public void test0612() {
    check( //
        "Integrate[(a+b*x)^(3/2)*(c+d*x)^(3/2)/x^5, x]", //
        "-1/4*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c*x^4)-3/64*(b*c-a*d)^4*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(5/2))-1/32*(b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a*c^2*x^2)-1/8*(b*c-a*d)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c^2*x^3)+3/64*(b*c-a*d)^3*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^2*c^2*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:831
  public void test0613() {
    check( //
        "Integrate[(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^4, x]", //
        "-5/12*(b*c+a*d)*(a+b*x)^(3/2)*(c+d*x)^(5/2)/(c*x^2)-1/3*(a+b*x)^(5/2)*(c+d*x)^(5/2)/x^3-5/8*(b*c+a*d)*(b^2*c^2+14*a*b*c*d+a^2*d^2)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(Sqrt[a]*Sqrt[c])+5/4*(3*b*c+a*d)*(b*c+3*a*d)*ArcTanh[Sqrt[d]*Sqrt[a+b*x]/(Sqrt[b]*Sqrt[c+d*x])]*Sqrt[b]*Sqrt[d]+5/24*d*(9*b^2*c^2+14*a*b*c*d+a^2*d^2)*(c+d*x)^(3/2)*Sqrt[a+b*x]/c^2-5/24*(3*b^2*c^2+12*a*b*c*d+a^2*d^2)*(c+d*x)^(5/2)*Sqrt[a+b*x]/(c^2*x)+5/8*d*(5*b^2*c^2+10*a*b*c*d+a^2*d^2)*Sqrt[a+b*x]*Sqrt[c+d*x]/c");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:861
  public void test0614() {
    check( //
        "Integrate[(a+b*x)^(5/2)/(x^5*(c+d*x)^(5/2)), x]", //
        "-1/4*a*(a+b*x)^(3/2)/(c*x^4*(c+d*x)^(3/2))+5/64*(b*c-a*d)*(b^3*c^3+21*a*b^2*c^2*d-189*a^2*b*c*d^2+231*a^3*d^3)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(13/2))-1/64*d*(b*c-a*d)*(5*b^2*c^2-238*a*b*c*d+385*a^2*d^2)*Sqrt[a+b*x]/(a*c^5*(c+d*x)^(3/2))-11/24*a*(b*c-a*d)*Sqrt[a+b*x]/(c^2*x^3*(c+d*x)^(3/2))-1/96*(59*b*c-99*a*d)*(b*c-a*d)*Sqrt[a+b*x]/(c^3*x^2*(c+d*x)^(3/2))-1/64*(b*c-a*d)*(5*b^2*c^2-156*a*b*c*d+231*a^2*d^2)*Sqrt[a+b*x]/(a*c^4*x*(c+d*x)^(3/2))-1/64*d*(5*b^3*c^3-581*a*b^2*c^2*d+1715*a^2*b*c*d^2-1155*a^3*d^3)*Sqrt[a+b*x]/(a*c^6*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:879
  public void test0615() {
    check( //
        "Integrate[(c+d*x)^(3/2)/(x^4*Sqrt[a+b*x]), x]", //
        "1/8*(b*c-a*d)^2*(5*b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(7/2)*c^(3/2))+1/12*(5*b*c+a*d)*(c+d*x)^(3/2)*Sqrt[a+b*x]/(a^2*c*x^2)-1/3*(c+d*x)^(5/2)*Sqrt[a+b*x]/(a*c*x^3)-1/8*(b*c-a*d)*(5*b*c+a*d)*Sqrt[a+b*x]*Sqrt[c+d*x]/(a^3*c*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:923
  public void test0616() {
    check( //
        "Integrate[1/(x^2*(c+d*x)^(5/2)*Sqrt[a+b*x]), x]", //
        "(b*c+5*a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(3/2)*c^(7/2))-1/3*d*(3*b*c-5*a*d)*Sqrt[a+b*x]/(a*c^2*(b*c-a*d)*(c+d*x)^(3/2))-Sqrt[a+b*x]/(a*c*x*(c+d*x)^(3/2))-1/3*d*(3*b^2*c^2-22*a*b*c*d+15*a^2*d^2)*Sqrt[a+b*x]/(a*c^3*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:957
  public void test0617() {
    check( //
        "Integrate[1/(x^2*(a+b*x)^(3/2)*(c+d*x)^(3/2)), x]", //
        "3*(b*c+a*d)*ArcTanh[Sqrt[c]*Sqrt[a+b*x]/(Sqrt[a]*Sqrt[c+d*x])]/(a^(5/2)*c^(5/2))-b*(3*b*c-a*d)/(a^2*c*(b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])+(-1)/(a*c*x*Sqrt[a+b*x]*Sqrt[c+d*x])-d*(3*b^2*c^2-2*a*b*c*d+3*a^2*d^2)*Sqrt[a+b*x]/(a^2*c^2*(b*c-a*d)^2*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:991
  public void test0618() {
    check( //
        "Integrate[1/((a+b*x)^(5/2)*(c+d*x)^(5/2)), x]", //
        "(-2/3)/((b*c-a*d)*(a+b*x)^(3/2)*(c+d*x)^(3/2))+4*d/((b*c-a*d)^2*(c+d*x)^(3/2)*Sqrt[a+b*x])+16/3*d^2*Sqrt[a+b*x]/((b*c-a*d)^3*(c+d*x)^(3/2))+32/3*b*d^2*Sqrt[a+b*x]/((b*c-a*d)^4*Sqrt[c+d*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1024
  public void test0619() {
    check( //
        "Integrate[1/(x*Sqrt[-1+x]*Sqrt[1+x]), x]", //
        "ArcTan[Sqrt[-1+x]*Sqrt[1+x]]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1246
  public void test0620() {
    check( //
        "Integrate[(a+b*x)*(A+B*x)/(d+e*x)^2, x]", //
        "b*B*x/e^2-(b*d-a*e)*(B*d-A*e)/(e^3*(d+e*x))-(2*b*B*d-A*b*e-a*B*e)*Log[d+e*x]/e^3");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1260
  public void test0621() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*x)/(d+e*x)^5, x]", //
        "-1/4*(B*d-A*e)*(a+b*x)^3/(e*(b*d-a*e)*(d+e*x)^4)+1/12*(3*b*B*d+A*b*e-4*a*B*e)*(a+b*x)^3/(e*(b*d-a*e)^2*(d+e*x)^3)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1274
  public void test0622() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*x)/(d+e*x)^5, x]", //
        "-1/4*(B*d-A*e)*(a+b*x)^4/(e*(b*d-a*e)*(d+e*x)^4)+1/3*B*(b*d-a*e)^3/(e^5*(d+e*x)^3)-3/2*b*B*(b*d-a*e)^2/(e^5*(d+e*x)^2)+3*b^2*B*(b*d-a*e)/(e^5*(d+e*x))+b^3*B*Log[d+e*x]/e^5");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1302
  public void test0623() {
    check( //
        "Integrate[(a+b*x)^6*(A+B*x)/(d+e*x)^14, x]", //
        "1/13*(b*d-a*e)^6*(B*d-A*e)/(e^8*(d+e*x)^13)-1/12*(b*d-a*e)^5*(7*b*B*d-6*A*b*e-a*B*e)/(e^8*(d+e*x)^12)+3/11*b*(b*d-a*e)^4*(7*b*B*d-5*A*b*e-2*a*B*e)/(e^8*(d+e*x)^11)-1/2*b^2*(b*d-a*e)^3*(7*b*B*d-4*A*b*e-3*a*B*e)/(e^8*(d+e*x)^10)+5/9*b^3*(b*d-a*e)^2*(7*b*B*d-3*A*b*e-4*a*B*e)/(e^8*(d+e*x)^9)-3/8*b^4*(b*d-a*e)*(7*b*B*d-2*A*b*e-5*a*B*e)/(e^8*(d+e*x)^8)+1/7*b^5*(7*b*B*d-A*b*e-6*a*B*e)/(e^8*(d+e*x)^7)-1/6*b^6*B/(e^8*(d+e*x)^6)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1330
  public void test0624() {
    check( //
        "Integrate[(a+b*x)^10*(A+B*x)/(d+e*x)^13, x]", //
        "-1/12*(B*d-A*e)*(a+b*x)^11/(e*(b*d-a*e)*(d+e*x)^12)+1/132*(11*b*B*d+A*b*e-12*a*B*e)*(a+b*x)^11/(e*(b*d-a*e)^2*(d+e*x)^11)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1898
  public void test0625() {
    check( //
        "Integrate[(2+3*x)^5*(3+5*x)/(1-2*x)^3, x]", //
        "184877/256/(1-2*x)^2+(-60025/16)/(1-2*x)-109089/32*x-16821/16*x^2-4401/16*x^3-1215/32*x^4-519645/128*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1912
  public void test0626() {
    check( //
        "Integrate[(2+3*x)^5*(3+5*x)^2/(1-2*x)^3, x]", //
        "2033647/512/(1-2*x)^2+(-6206585/256)/(1-2*x)-3907293/128*x-747297/64*x^2-69273/16*x^3-73305/64*x^4-1215/8*x^5-8117095/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:1926
  public void test0627() {
    check( //
        "Integrate[(2+3*x)^4*(3+5*x)^3/(1-2*x)^3, x]", //
        "3195731/512/(1-2*x)^2+(-9836211/256)/(1-2*x)-6277415/128*x-1208973/64*x^2-7065*x^3-120825/64*x^4-2025/8*x^5-12973191/256*Log[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2099
  public void test0628() {
    check( //
        "Integrate[(2+3*x)^3*(3+5*x)^3*Sqrt[1-2*x], x]", //
        "-456533/192*(1-2*x)^(3/2)+302379/80*(1-2*x)^(5/2)-190707/64*(1-2*x)^(7/2)+98209/72*(1-2*x)^(9/2)-260055/704*(1-2*x)^(11/2)+11475/208*(1-2*x)^(13/2)-225/64*(1-2*x)^(15/2)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2115
  public void test0629() {
    check( //
        "Integrate[(2+3*x)^2*Sqrt[1-2*x]/(3+5*x), x]", //
        "-37/50*(1-2*x)^(3/2)+9/50*(1-2*x)^(5/2)-2/125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+2/125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2191
  public void test0630() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(2+3*x)/(3+5*x), x]", //
        "2/75*(1-2*x)^(3/2)-3/25*(1-2*x)^(5/2)-22/125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+22/125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2267
  public void test0631() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^2/(3+5*x), x]", //
        "22/1875*(1-2*x)^(3/2)+2/625*(1-2*x)^(5/2)-111/350*(1-2*x)^(7/2)+1/10*(1-2*x)^(9/2)-242/3125*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[11/5]+242/3125*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2343
  public void test0632() {
    check( //
        "Integrate[(2+3*x)/((3+5*x)*Sqrt[1-2*x]), x]", //
        "-2/5*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]/Sqrt[55]-3/5*Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2419
  public void test0633() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(3+5*x)), x]", //
        "-2/11*ArcTanh[Sqrt[5/11]*Sqrt[1-2*x]]*Sqrt[5/11]+2/11/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2529
  public void test0634() {
    check( //
        "Integrate[(A+B*x)*Sqrt[a+b*x]/(d+e*x)^(11/2), x]", //
        "-2/9*(B*d-A*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)*(d+e*x)^(9/2))+2/21*(b*B*d+2*A*b*e-3*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^2*(d+e*x)^(7/2))+8/105*b*(b*B*d+2*A*b*e-3*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^3*(d+e*x)^(5/2))+16/315*b^2*(b*B*d+2*A*b*e-3*a*B*e)*(a+b*x)^(3/2)/(e*(b*d-a*e)^4*(d+e*x)^(3/2))");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2619
  public void test0635() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^2, x]", //
        "155/216*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-59/27*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+5/6*(3+5*x)^(3/2)*Sqrt[1-2*x]-1/3*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)-95/72*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2635
  public void test0636() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x]), x]", //
        "-11*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2667
  public void test0637() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^4, x]", //
        "1/3*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^3-1331/56*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/4*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-121/56*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2681
  public void test0638() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^8, x]", //
        "-1/21*(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^7-6219452877/17210368*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+37/252*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^6-9901/52920*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5+341917/2963520*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4+4014523/5927040*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+140331343/33191424*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+14677525921/464679936*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2711
  public void test0639() {
    check( //
        "Integrate[(1-2*x)^(3/2)/((2+3*x)^2*(3+5*x)^(3/2)), x]", //
        "33*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+(1-2*x)^(3/2)/((2+3*x)*Sqrt[3+5*x])-33*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2743
  public void test0640() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^2, x]", //
        "-8/27*(1-2*x)^(3/2)*(3+5*x)^(3/2)-1/3*(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)+805/243*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]*Sqrt[7]+326717/9720*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-247/270*(3+5*x)^(3/2)*Sqrt[1-2*x]+24251/3240*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2757
  public void test0641() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^4, x]", //
        "-1/9*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^3+185/108*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^2-21935/1458*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]-408665/5832*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2075/72*(3+5*x)^(3/2)*Sqrt[1-2*x]-10385/648*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)-48625/1944*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2773
  public void test0642() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^4*Sqrt[3+5*x]), x]", //
        "-6655/8*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/3*(1-2*x)^(5/2)*Sqrt[3+5*x]/(2+3*x)^3+55/12*(1-2*x)^(3/2)*Sqrt[3+5*x]/(2+3*x)^2+605/8*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2787
  public void test0643() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((2+3*x)^6*(3+5*x)^(3/2)), x]", //
        "3538809681/6272*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+7/15*(1-2*x)^(3/2)/((2+3*x)^5*Sqrt[3+5*x])-4639661185/56448*Sqrt[1-2*x]/Sqrt[3+5*x]+2513/360*Sqrt[1-2*x]/((2+3*x)^4*Sqrt[3+5*x])+12023/240*Sqrt[1-2*x]/((2+3*x)^3*Sqrt[3+5*x])+587477/1344*Sqrt[1-2*x]/((2+3*x)^2*Sqrt[3+5*x])+102293609/18816*Sqrt[1-2*x]/((2+3*x)*Sqrt[3+5*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2819
  public void test0644() {
    check( //
        "Integrate[(3+5*x)^(3/2)/((2+3*x)^3*Sqrt[1-2*x]), x]", //
        "-363/196*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-1/14*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^2-33/196*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2833
  public void test0645() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^7*Sqrt[1-2*x]), x]", //
        "-52573169/8605184*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+1/126*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^6+503/26460*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^5-149951/1481760*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^4-71369/2963520*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^3+958171/16595712*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^2+122343637/232339968*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2849
  public void test0646() {
    check( //
        "Integrate[1/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-2/11*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2863
  public void test0647() {
    check( //
        "Integrate[1/((2+3*x)^4*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-41307885/2744*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-21891025/90552*Sqrt[1-2*x]/(3+5*x)^(3/2)+1/7*Sqrt[1-2*x]/((2+3*x)^3*(3+5*x)^(3/2))+325/196*Sqrt[1-2*x]/((2+3*x)^2*(3+5*x)^(3/2))+79335/2744*Sqrt[1-2*x]/((2+3*x)*(3+5*x)^(3/2))+2184369575/996072*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2895
  public void test0648() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(3/2)*(2+3*x)), x]", //
        "-475/36*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]*Sqrt[5/2]+2/63*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+11/7*(3+5*x)^(3/2)/Sqrt[1-2*x]+505/84*Sqrt[1-2*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2911
  public void test0649() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^3*Sqrt[3+5*x]), x]", //
        "-12465/1372*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]-3895/7546*Sqrt[3+5*x]/Sqrt[1-2*x]+3/14*Sqrt[3+5*x]/((2+3*x)^2*Sqrt[1-2*x])+345/196*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2925
  public void test0650() {
    check( //
        "Integrate[(2+3*x)^4/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "-621/100*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]+7/11*(2+3*x)^3/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*(2+3*x)^2*Sqrt[1-2*x]/(3+5*x)^(3/2)+1/399300*(627641+1051875*x)*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2943
  public void test0651() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^2), x]", //
        "-75/343*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x))+850/11319*Sqrt[3+5*x]/Sqrt[1-2*x]-5/49*Sqrt[3+5*x]/((2+3*x)*Sqrt[1-2*x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2973
  public void test0652() {
    check( //
        "Integrate[1/((1-2*x)^(5/2)*(2+3*x)*Sqrt[3+5*x]), x]", //
        "-18/49*ArcTan[Sqrt[1-2*x]/(Sqrt[7]*Sqrt[3+5*x])]/Sqrt[7]+4/231*Sqrt[3+5*x]/(1-2*x)^(3/2)+676/17787*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:2987
  public void test0653() {
    check( //
        "Integrate[(2+3*x)^5/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^4/((1-2*x)^(3/2)*(3+5*x)^(3/2))+2997/200*ArcSin[Sqrt[2/11]*Sqrt[3+5*x]]/Sqrt[10]-357/242*(2+3*x)^3/((3+5*x)^(3/2)*Sqrt[1-2*x])+5281/39930*(2+3*x)^2*Sqrt[1-2*x]/(3+5*x)^(3/2)-1/8784600*(33035947+55300905*x)*Sqrt[1-2*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3003
  public void test0654() {
    check( //
        "Integrate[Sqrt[a+b*x]/(Sqrt[c+b*(-1+c)*x/a]*Sqrt[e+b*(-1+e)*x/a]), x]", //
        "-2*a*EllipticE[ArcSin[Sqrt[1-e]*Sqrt[c-b*(1-c)*x/a]/Sqrt[c-e]],(c-e)/(1-e)]*Sqrt[c-e]*Sqrt[a+b*x]*Sqrt[-(1-c)*(a*e-b*(1-e)*x)/(a*(c-e))]/(b*(1-c)*Sqrt[1-e]*Sqrt[(1-c)*(a+b*x)/a]*Sqrt[e-b*(1-e)*x/a])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3023
  public void test0655() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[1-2*x]/Sqrt[2+3*x], x]", //
        "-974/675*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-41/675*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/15*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-41/135*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3037
  public void test0656() {
    check( //
        "Integrate[(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(11/2), x]", //
        "-27198452/20420505*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-442868/20420505*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-118/1323*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)-2/27*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(9/2)-12934/138915*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)+568318/2917215*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+27198452/20420505*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3053
  public void test0657() {
    check( //
        "Integrate[(2+3*x)^(3/2)*Sqrt[1-2*x]/(3+5*x)^(3/2), x]", //
        "-19/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-106/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/5*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]+8/25*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3067
  public void test0658() {
    check( //
        "Integrate[Sqrt[1-2*x]/((2+3*x)^(7/2)*(3+5*x)^(5/2)), x]", //
        "-10312712/245*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-310208/245*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/5*Sqrt[1-2*x]/((2+3*x)^(5/2)*(3+5*x)^(3/2))+556/105*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+116044/735*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-155104/147*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+10312712/1617*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3085
  public void test0659() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(3/2)/(2+3*x)^(3/2), x]", //
        "-2209/675*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+494/675*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(1-2*x)^(3/2)*(3+5*x)^(3/2)/Sqrt[2+3*x]-8/15*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+494/135*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3099
  public void test0660() {
    check( //
        "Integrate[(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(11/2), x]", //
        "-2/27*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(9/2)-17830424/8751645*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1717916/8751645*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1864/6615*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)+362/567*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)-558524/1250235*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+17830424/8751645*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3115
  public void test0661() {
    check( //
        "Integrate[(1-2*x)^(3/2)*Sqrt[2+3*x]/(3+5*x)^(3/2), x]", //
        "458/375*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-178/375*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/5*(1-2*x)^(3/2)*Sqrt[2+3*x]/Sqrt[3+5*x]-16/75*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3133
  public void test0662() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(5/2)*Sqrt[3+5*x], x]", //
        "62/2145*(1-2*x)^(3/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2)+2/65*(1-2*x)^(5/2)*(2+3*x)^(5/2)*(3+5*x)^(3/2)-1163388067/38390625*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-69808931/76781250*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+32717/1126125*(2+3*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]+34/2475*(2+3*x)^(5/2)*(3+5*x)^(3/2)*Sqrt[1-2*x]-445024/9384375*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-69808931/168918750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3147
  public void test0663() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(3/2)/(2+3*x)^(3/2), x]", //
        "-481339/70875*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+124724/70875*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-2/3*(1-2*x)^(5/2)*(3+5*x)^(3/2)/Sqrt[2+3*x]-32/63*(1-2*x)^(3/2)*(3+5*x)^(3/2)*Sqrt[2+3*x]-2108/1575*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+124724/14175*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3161
  public void test0664() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(11/2), x]", //
        "-2/27*(1-2*x)^(5/2)*(3+5*x)^(5/2)/(2+3*x)^(9/2)+370/567*(1-2*x)^(3/2)*(3+5*x)^(5/2)/(2+3*x)^(7/2)-100444/750141*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1241596/750141*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-13316/35721*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(3/2)+2776/1701*(3+5*x)^(5/2)*Sqrt[1-2*x]/(2+3*x)^(5/2)-1241596/750141*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3177
  public void test0665() {
    check( //
        "Integrate[(1-2*x)^(5/2)*(2+3*x)^(7/2)/(3+5*x)^(3/2), x]", //
        "-264260033/29531250*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-7261561/14765625*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-2/5*(1-2*x)^(5/2)*(2+3*x)^(7/2)/Sqrt[3+5*x]-48/275*(1-2*x)^(3/2)*(2+3*x)^(7/2)*Sqrt[3+5*x]+2020841/6496875*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]+346636/259875*(2+3*x)^(5/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-2972/7425*(2+3*x)^(7/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-703672/32484375*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3191
  public void test0666() {
    check( //
        "Integrate[(1-2*x)^(5/2)/((3+5*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-68/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-584/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]-22/15*(1-2*x)^(3/2)*Sqrt[2+3*x]/(3+5*x)^(3/2)+572/25*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3209
  public void test0667() {
    check( //
        "Integrate[(3+5*x)^(3/2)*Sqrt[2+3*x]/Sqrt[1-2*x], x]", //
        "-4451/450*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-67/225*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-1/5*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]-67/45*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3223
  public void test0668() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((2+3*x)^(9/2)*Sqrt[1-2*x]), x]", //
        "-816622/2268945*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-265648/2268945*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+2/147*(3+5*x)^(3/2)*Sqrt[1-2*x]/(2+3*x)^(7/2)+676/15435*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(5/2)-101902/324135*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+816622/2268945*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3241
  public void test0669() {
    check( //
        "Integrate[1/(Sqrt[-3-x]*Sqrt[-1-x]*Sqrt[-2+x]), x]", //
        "-2*EllipticF[ArcSin[1/Sqrt[3/5+1/5*x]],2/5]*Sqrt[1+x]*Sqrt[3+x]/(Sqrt[5]*Sqrt[-3-x]*Sqrt[-1-x])");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3255
  public void test0670() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((3+5*x)^(3/2)*Sqrt[1-2*x]), x]", //
        "-438/125*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-17/125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-2/55*(2+3*x)^(3/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-27/275*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3269
  public void test0671() {
    check( //
        "Integrate[1/((2+3*x)^(5/2)*(3+5*x)^(5/2)*Sqrt[1-2*x]), x]", //
        "-1255552/539*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-37768/539*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/7*Sqrt[1-2*x]/((2+3*x)^(3/2)*(3+5*x)^(3/2))+428/49*Sqrt[1-2*x]/((3+5*x)^(3/2)*Sqrt[2+3*x])-94420/1617*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)+6277760/17787*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3293
  public void test0672() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(3/2)), x]", //
        "-62/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+4/49*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+2/7*Sqrt[3+5*x]/(Sqrt[1-2*x]*Sqrt[2+3*x])-12/49*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3307
  public void test0673() {
    check( //
        "Integrate[(2+3*x)^(3/2)*(3+5*x)^(5/2)/(1-2*x)^(3/2), x]", //
        "244879/420*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+3683/210*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]+(2+3*x)^(3/2)*(3+5*x)^(5/2)/Sqrt[1-2*x]+167/14*(3+5*x)^(3/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+12/7*(3+5*x)^(5/2)*Sqrt[1-2*x]*Sqrt[2+3*x]+3683/42*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3323
  public void test0674() {
    check( //
        "Integrate[1/((1-2*x)^(3/2)*(2+3*x)^(5/2)*Sqrt[3+5*x]), x]", //
        "-1752/343*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]-68/343*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[3/11]+4/77*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])+54/539*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+5256/3773*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3337
  public void test0675() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((1-2*x)^(3/2)*(3+5*x)^(5/2)), x]", //
        "4157/3025*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-412/3025*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/11*(2+3*x)^(3/2)/((3+5*x)^(3/2)*Sqrt[1-2*x])-107/1815*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-4157/19965*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3355
  public void test0676() {
    check( //
        "Integrate[Sqrt[3+5*x]/((1-2*x)^(5/2)*(2+3*x)^(5/2)), x]", //
        "-338/2401*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-992/2401*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+2/21*Sqrt[3+5*x]/((1-2*x)^(3/2)*(2+3*x)^(3/2))+326/1617*Sqrt[3+5*x]/((2+3*x)^(3/2)*Sqrt[1-2*x])-458/3773*Sqrt[1-2*x]*Sqrt[3+5*x]/(2+3*x)^(3/2)+338/26411*Sqrt[1-2*x]*Sqrt[3+5*x]/Sqrt[2+3*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3369
  public void test0677() {
    check( //
        "Integrate[(3+5*x)^(5/2)/((1-2*x)^(5/2)*Sqrt[2+3*x]), x]", //
        "-1597/98*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[11/3]-8/49*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]*Sqrt[33]+11/21*(3+5*x)^(3/2)*Sqrt[2+3*x]/(1-2*x)^(3/2)-264/49*Sqrt[2+3*x]*Sqrt[3+5*x]/Sqrt[1-2*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3385
  public void test0678() {
    check( //
        "Integrate[(2+3*x)^(11/2)/((1-2*x)^(5/2)*(3+5*x)^(3/2)), x]", //
        "-1508889271/1512500*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-11346991/378125*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+7/33*(2+3*x)^(9/2)/((1-2*x)^(3/2)*Sqrt[3+5*x])-896/363*(2+3*x)^(7/2)/(Sqrt[1-2*x]*Sqrt[3+5*x])+4439/19965*(2+3*x)^(5/2)*Sqrt[1-2*x]/Sqrt[3+5*x]-932783/332750*(2+3*x)^(3/2)*Sqrt[1-2*x]*Sqrt[3+5*x]-21713939/1663750*Sqrt[1-2*x]*Sqrt[2+3*x]*Sqrt[3+5*x]");
  }

  // 1.1.1.3 (a+b x)^m (c+d x)^n (e+f x)^p.input:3399
  public void test0679() {
    check( //
        "Integrate[(2+3*x)^(5/2)/((1-2*x)^(5/2)*(3+5*x)^(5/2)), x]", //
        "7/33*(2+3*x)^(3/2)/((1-2*x)^(3/2)*(3+5*x)^(3/2))+2209/6655*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]-494/6655*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1-2*x]],35/33]/Sqrt[33]+14/121*Sqrt[2+3*x]/((3+5*x)^(3/2)*Sqrt[1-2*x])-247/3993*Sqrt[1-2*x]*Sqrt[2+3*x]/(3+5*x)^(3/2)-2209/43923*Sqrt[1-2*x]*Sqrt[2+3*x]/Sqrt[3+5*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:23
  public void test0680() {
    check( //
        "Integrate[(e+f*x)*Sqrt[c+d*x]/(x*(a+b*x)), x]", //
        "-2*e*ArcTanh[Sqrt[c+d*x]/Sqrt[c]]*Sqrt[c]/a+2*(b*e-a*f)*ArcTanh[Sqrt[b]*Sqrt[c+d*x]/Sqrt[b*c-a*d]]*Sqrt[b*c-a*d]/(a*b^(3/2))+2*f*Sqrt[c+d*x]/b");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:41
  public void test0681() {
    check( //
        "Integrate[(1+a*x)/(x*Sqrt[a*x]*Sqrt[1-a*x]), x]", //
        "-ArcSin[1-2*a*x]-2*Sqrt[1-a*x]/Sqrt[a*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:70
  public void test0682() {
    check( //
        "Integrate[(7+5*x)^2*Sqrt[2-3*x]*Sqrt[1+4*x]/Sqrt[-5+2*x], x]", //
        "-1679161/756*EllipticF[ArcSin[Sqrt[3/11]*Sqrt[1+4*x]],1/3]*Sqrt[11/6]*Sqrt[5-2*x]/Sqrt[-5+2*x]+8198333/9072*EllipticE[ArcSin[2*Sqrt[2-3*x]/Sqrt[11]],-1/2]*Sqrt[11]*Sqrt[-5+2*x]/Sqrt[5-2*x]+73207/1080*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+173/60*(7+5*x)*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]+1/7*(7+5*x)^2*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]");
  }

  // 1.1.1.4 (a+b x)^m (c+d x)^n (e+f x)^p (g+h x)^q.input:122
  public void test0683() {
    check( //
        "Integrate[Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(7/2), x]", //
        "-2/25*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(5/2)+17906/2085525*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/(7+5*x)^(3/2)+1426348/2319687747*Sqrt[2-3*x]*Sqrt[-5+2*x]*Sqrt[1+4*x]/Sqrt[7+5*x]-2852696/11598438735*Sqrt[2-3*x]*Sqrt[1+4*x]*Sqrt[7+5*x]/Sqrt[-5+2*x]-48884/9593415*EllipticF[ArcTan[Sqrt[1+4*x]/(Sqrt[2]*Sqrt[2-3*x])],-39/23]*Sqrt[11/23]*Sqrt[7+5*x]/(Sqrt[-5+2*x]*Sqrt[(7+5*x)/(5-2*x)])+1426348/297395865*EllipticE[ArcSin[Sqrt[39/23]*Sqrt[1+4*x]/Sqrt[-5+2*x]],-23/39]*Sqrt[11/39]*Sqrt[2-3*x]*Sqrt[(7+5*x)/(5-2*x)]/(Sqrt[(2-3*x)/(5-2*x)]*Sqrt[7+5*x])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:45
  public void test0684() {
    check( //
        "Integrate[(a+b*x^2)^3/x, x]", //
        "3/2*a^2*b*x^2+3/4*a*b^2*x^4+1/6*b^3*x^6+a^3*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:73
  public void test0685() {
    check( //
        "Integrate[(a+b*x^2)^5/x^7, x]", //
        "-1/6*a^5/x^6-5/4*a^4*b/x^4-5*a^3*b^2/x^2+5/2*a*b^4*x^2+1/4*b^5*x^4+10*a^2*b^3*Log[x]");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:101
  public void test0686() {
    check( //
        "Integrate[x^3*(a+b*x^2)^8, x]", //
        "-1/18*a*(a+b*x^2)^9/b^2+1/20*(a+b*x^2)^10/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:115
  public void test0687() {
    check( //
        "Integrate[(a+b*x^2)^8/x^25, x]", //
        "-1/24*(a+b*x^2)^9/(a*x^24)+1/88*b*(a+b*x^2)^9/(a^2*x^22)-1/440*b^2*(a+b*x^2)^9/(a^3*x^20)+1/3960*b^3*(a+b*x^2)^9/(a^4*x^18)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:145
  public void test0688() {
    check( //
        "Integrate[x^3/(a+b*x^2), x]", //
        "1/2*x^2/b-1/2*a*Log[a+b*x^2]/b^2");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:215
  public void test0689() {
    check( //
        "Integrate[x^5/(a+b*x^2)^10, x]", //
        "-1/18*a^2/(b^3*(a+b*x^2)^9)+1/8*a/(b^3*(a+b*x^2)^8)+(-1/14)/(b^3*(a+b*x^2)^7)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:273
  public void test0690() {
    check( //
        "Integrate[1/(-c-d+(c-d)*x^2), x]", //
        "-ArcTanh[x*Sqrt[c-d]/Sqrt[c+d]]/(Sqrt[c-d]*Sqrt[c+d])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:443
  public void test0691() {
    check( //
        "Integrate[(a+b*x^2)^(5/2)/x^18, x]", //
        "-1/17*(a+b*x^2)^(7/2)/(a*x^17)+2/51*b*(a+b*x^2)^(7/2)/(a^2*x^15)-16/663*b^2*(a+b*x^2)^(7/2)/(a^3*x^13)+32/2431*b^3*(a+b*x^2)^(7/2)/(a^4*x^11)-128/21879*b^4*(a+b*x^2)^(7/2)/(a^5*x^9)+256/153153*b^5*(a+b*x^2)^(7/2)/(a^6*x^7)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:471
  public void test0692() {
    check( //
        "Integrate[(a+b*x^2)^(9/2)/x^16, x]", //
        "-1/15*(a+b*x^2)^(11/2)/(a*x^15)+4/195*b*(a+b*x^2)^(11/2)/(a^2*x^13)-8/2145*b^2*(a+b*x^2)^(11/2)/(a^3*x^11)");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:917
  public void test0693() {
    check( //
        "Integrate[1/(x^4*(a+b*x^2)^(5/4)), x]", //
        "(-1/3)/(a*x^3*(a+b*x^2)^(1/4))+7/6*b/(a^2*x*(a+b*x^2)^(1/4))+7/2*b^(3/2)*(1+b*x^2/a)^(1/4)*EllipticE[1/2*ArcTan[x*Sqrt[b]/Sqrt[a]],2]/(a^(5/2)*(a+b*x^2)^(1/4))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:931
  public void test0694() {
    check( //
        "Integrate[1/(a-b*x^2)^(11/4), x]", //
        "2/7*x/(a*(a-b*x^2)^(7/4))+10/21*x/(a^2*(a-b*x^2)^(3/4))+10/21*(1-b*x^2/a)^(3/4)*EllipticF[1/2*ArcSin[x*Sqrt[b]/Sqrt[a]],2]/(a^(3/2)*(a-b*x^2)^(3/4)*Sqrt[b])");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1049
  public void test0695() {
    check( //
        "Integrate[1/((c*x)^(11/2)*(a+b*x^2)^(3/4)), x]", //
        "-2*(a+b*x^2)^(1/4)/(a*c*(c*x)^(9/2))+16/5*(a+b*x^2)^(5/4)/(a^2*c*(c*x)^(9/2))-64/45*(a+b*x^2)^(9/4)/(a^3*c*(c*x)^(9/2))");
  }

  // 1.1.2.2 (c x)^m (a+b x^2)^p.input:1117
  public void test0696() {
    check( //
        "Integrate[1/(a+b*x^2)^(5/6), x]", //
        "3^(3/4)*(a+b*x^2)^(1/6)*(1-(a/(a+b*x^2))^(1/3))*EllipticF[ArcSin[(1-(a/(a+b*x^2))^(1/3)+Sqrt[3])/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])],-7+4*Sqrt[3]]*Sqrt[2-Sqrt[3]]*Sqrt[(1+(a/(a+b*x^2))^(1/3)+(a/(a+b*x^2))^(2/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2]/(b*x*(a/(a+b*x^2))^(1/3)*Sqrt[(-1+(a/(a+b*x^2))^(1/3))/(1-(a/(a+b*x^2))^(1/3)-Sqrt[3])^2])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:20
  public void test0697() {
    check( //
        "Integrate[(a+b*x^2)^2/(c+d*x^2), x]", //
        "-b*(b*c-2*a*d)*x/d^2+1/3*b^2*x^3/d+(b*c-a*d)^2*ArcTan[x*Sqrt[d]/Sqrt[c]]/(d^(5/2)*Sqrt[c])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:179
  public void test0698() {
    check( //
        "Integrate[1/((-a+b*x^2)^(1/3)*(-9*a*d/b+d*x^2)), x]", //
        "-1/12*ArcTanh[1/3*(a^(1/3)+(-a+b*x^2)^(1/3))^2/(a^(1/6)*x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d)+1/12*ArcTanh[1/3*x*Sqrt[b]/Sqrt[a]]*Sqrt[b]/(a^(5/6)*d)+1/4*ArcTan[a^(1/6)*(a^(1/3)+(-a+b*x^2)^(1/3))*Sqrt[3]/(x*Sqrt[b])]*Sqrt[b]/(a^(5/6)*d*Sqrt[3])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:218
  public void test0699() {
    check( //
        "Integrate[Sqrt[4+x^2]/Sqrt[c+d*x^2], x]", //
        "x*Sqrt[c+d*x^2]/(d*Sqrt[4+x^2])-EllipticE[ArcTan[1/2*x],1-4*d/c]*Sqrt[c+d*x^2]/(d*Sqrt[4+x^2]*Sqrt[(c+d*x^2)/(c*(4+x^2))])+4*EllipticF[ArcTan[1/2*x],1-4*d/c]*Sqrt[c+d*x^2]/(c*Sqrt[4+x^2]*Sqrt[(c+d*x^2)/(c*(4+x^2))])");
  }

  // 1.1.2.3 (a+b x^2)^p (c+d x^2)^q.input:232
  public void test0700() {
    check( //
        "Integrate[Sqrt[1+4*x^2]/Sqrt[2+3*x^2], x]", //
        "4/3*x*Sqrt[2+3*x^2]/Sqrt[1+4*x^2]+1/2*EllipticF[ArcTan[2*x],5/8]*Sqrt[2+3*x^2]/(Sqrt[2]*Sqrt[(2+3*x^2)/(1+4*x^2)]*Sqrt[1+4*x^2])-2/3*EllipticE[ArcTan[2*x],5/8]*Sqrt[2]*Sqrt[2+3*x^2]/(Sqrt[(2+3*x^2)/(1+4*x^2)]*Sqrt[1+4*x^2])");
  }
}
