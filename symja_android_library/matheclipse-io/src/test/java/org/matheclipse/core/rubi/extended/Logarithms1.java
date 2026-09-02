package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 3 Logarithms of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class Logarithms1 extends AbstractRubiTestCase {
  static boolean init = true;

  public Logarithms1(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("Logarithms1");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:29
  public void test0001() {
    check( //
        "Integrate[Log[c*x]^3/x^2, x]", //
        "(-6)/x-6*Log[c*x]/x-3*Log[c*x]^2/x-Log[c*x]^3/x");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:47
  public void test0002() {
    check( //
        "Integrate[x^3/Log[c*x]^3, x]", //
        "8*ExpIntegralEi[4*Log[c*x]]/c^4-1/2*x^4/Log[c*x]^2-2*x^4/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:75
  public void test0003() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3, x]", //
        "6*a*b^2*n^2*x-6*b^3*n^3*x+6*b^3*n^2*x*Log[c*x^n]-3*b*n*x*(a+b*Log[c*x^n])^2+x*(a+b*Log[c*x^n])^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:85
  public void test0004() {
    check( //
        "Integrate[1/(a+b*Log[c*x^n]), x]", //
        "x*ExpIntegralEi[(a+b*Log[c*x^n])/(b*n)]/(E^(a/(b*n))*b*n*(c*x^n)^(1/n))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:93
  public void test0005() {
    check( //
        "Integrate[1/(a+b*Log[c*x^n])^2, x]", //
        "x*ExpIntegralEi[(a+b*Log[c*x^n])/(b*n)]/(E^(a/(b*n))*b^2*n^2*(c*x^n)^(1/n))-x/(b*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:101
  public void test0006() {
    check( //
        "Integrate[1/(a+b*Log[c*x^n])^3, x]", //
        "1/2*x*ExpIntegralEi[(a+b*Log[c*x^n])/(b*n)]/(E^(a/(b*n))*b^3*n^3*(c*x^n)^(1/n))-1/2*x/(b*n*(a+b*Log[c*x^n])^2)-1/2*x/(b^2*n^2*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:113
  public void test0007() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d*x)^(1/2), x]", //
        "-4*b*n*Sqrt[d*x]/d+2*(a+b*Log[c*x^n])*Sqrt[d*x]/d");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:131
  public void test0008() {
    check( //
        "Integrate[(d*x)^(3/2)/(a+b*Log[c*x^n])^2, x]", //
        "5/2*(d*x)^(5/2)*ExpIntegralEi[5/2*(a+b*Log[c*x^n])/(b*n)]/(E^(5/2*a/(b*n))*b^2*d*n^2*(c*x^n)^(5/2/n))-(d*x)^(5/2)/(b*d*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:151
  public void test0009() {
    check( //
        "Integrate[Log[a*x^n]^(3/2), x]", //
        "x*Log[a*x^n]^(3/2)+3/4*n^(3/2)*x*Erfi[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(a*x^n)^(1/n)-3/2*n*x*Sqrt[Log[a*x^n]]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:169
  public void test0010() {
    check( //
        "Integrate[1/(x^2*Log[a*x^n]^(3/2)), x]", //
        "-2*(a*x^n)^(1/n)*Erf[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(n^(3/2)*x)+(-2)/(n*x*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:177
  public void test0011() {
    check( //
        "Integrate[1/(x^3*Log[a*x^n]^(5/2)), x]", //
        "(-2/3)/(n*x^2*Log[a*x^n]^(3/2))+8/3*(a*x^n)^(2/n)*Erf[Sqrt[2]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[2*Pi]/(n^(5/2)*x^2)+8/3/(n^2*x^2*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:187
  public void test0012() {
    check( //
        "Integrate[(d*x)^(-1+n)*Log[c*x^n]^3, x]", //
        "-6*(d*x)^n/(d*n)+6*(d*x)^n*Log[c*x^n]/(d*n)-3*(d*x)^n*Log[c*x^n]^2/(d*n)+(d*x)^n*Log[c*x^n]^3/(d*n)");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:195
  public void test0013() {
    check( //
        "Integrate[x^m/Log[a*x^n]^(1/2), x]", //
        "x^(1+m)*Erfi[Sqrt[1+m]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/((a*x^n)^((1+m)/n)*Sqrt[1+m]*Sqrt[n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:31
  public void test0014() {
    check( //
        "Integrate[x^2*(d+e*x)^3*(a+b*Log[c*x^n]), x]", //
        "-1/9*b*d^3*n*x^3-3/16*b*d^2*e*n*x^4-3/25*b*d*e^2*n*x^5-1/36*b*e^3*n*x^6+1/60*(20*d^3*x^3+45*d^2*e*x^4+36*d*e^2*x^5+10*e^3*x^6)*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:94
  public void test0015() {
    check( //
        "Integrate[x*(d+e*x)*(a+b*Log[c*x^n])^2, x]", //
        "1/4*b^2*d*n^2*x^2+2/27*b^2*e*n^2*x^3-1/2*b*d*n*x^2*(a+b*Log[c*x^n])-2/9*b*e*n*x^3*(a+b*Log[c*x^n])+1/2*d*x^2*(a+b*Log[c*x^n])^2+1/3*e*x^3*(a+b*Log[c*x^n])^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:102
  public void test0016() {
    check( //
        "Integrate[x*(d+e*x)^2*(a+b*Log[c*x^n])^2, x]", //
        "1/4*b^2*d^2*n^2*x^2+4/27*b^2*d*e*n^2*x^3+1/32*b^2*e^2*n^2*x^4-1/2*b*d^2*n*x^2*(a+b*Log[c*x^n])-4/9*b*d*e*n*x^3*(a+b*Log[c*x^n])-1/8*b*e^2*n*x^4*(a+b*Log[c*x^n])+1/2*d^2*x^2*(a+b*Log[c*x^n])^2+2/3*d*e*x^3*(a+b*Log[c*x^n])^2+1/4*e^2*x^4*(a+b*Log[c*x^n])^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:112
  public void test0017() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])^2/(d+e*x), x]", //
        "2*a*b*d*n*x/e^2-2*b^2*d*n^2*x/e^2+1/4*b^2*n^2*x^2/e+2*b^2*d*n*x*Log[c*x^n]/e^2-1/2*b*n*x^2*(a+b*Log[c*x^n])/e-d*x*(a+b*Log[c*x^n])^2/e^2+1/2*x^2*(a+b*Log[c*x^n])^2/e+d^2*(a+b*Log[c*x^n])^2*Log[1+e*x/d]/e^3+2*b*d^2*n*(a+b*Log[c*x^n])*PolyLog[2,-e*x/d]/e^3-2*b^2*d^2*n^2*PolyLog[3,-e*x/d]/e^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:120
  public void test0018() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])^2/(d+e*x)^2, x]", //
        "-2*a*b*n*x/e^2+2*b^2*n^2*x/e^2-2*b^2*n*x*Log[c*x^n]/e^2+x*(a+b*Log[c*x^n])^2/e^2+d*x*(a+b*Log[c*x^n])^2/(e^2*(d+e*x))-2*b*d*n*(a+b*Log[c*x^n])*Log[1+e*x/d]/e^3-2*d*(a+b*Log[c*x^n])^2*Log[1+e*x/d]/e^3-2*b^2*d*n^2*PolyLog[2,-e*x/d]/e^3-4*b*d*n*(a+b*Log[c*x^n])*PolyLog[2,-e*x/d]/e^3+4*b^2*d*n^2*PolyLog[3,-e*x/d]/e^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:134
  public void test0019() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])^2/(d+e*x)^4, x]", //
        "1/3*b*n*x^2*(a+b*Log[c*x^n])/(d*e*(d+e*x)^2)+1/3*x^3*(a+b*Log[c*x^n])^2/(d*(d+e*x)^3)+1/3*b*n*x*(2*a+b*n+2*b*Log[c*x^n])/(d*e^2*(d+e*x))-1/3*b*n*(2*a+3*b*n+2*b*Log[c*x^n])*Log[1+e*x/d]/(d*e^3)-2/3*b^2*n^2*PolyLog[2,-e*x/d]/(d*e^3)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:235
  public void test0020() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d*n/x^5-1/9*b*e*n/x^3-1/5*d*(a+b*Log[c*x^n])/x^5-1/3*e*(a+b*Log[c*x^n])/x^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:269
  public void test0021() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^3*(d+e*x^2)), x]", //
        "-1/4*b*n/(d*x^2)+1/2*(-a-b*Log[c*x^n])/(d*x^2)+1/2*e*Log[1+d/(e*x^2)]*(a+b*Log[c*x^n])/d^2-1/4*b*e*n*PolyLog[2,-d/(e*x^2)]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:349
  public void test0022() {
    check( //
        "Integrate[(d+e*x^2)^(3/2)*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/15*b*e*n*(d+e*x^2)^(3/2)/(d*x^3)-1/25*b*n*(d+e*x^2)^(5/2)/(d*x^5)+1/5*b*e^(5/2)*n*ArcTanh[x*Sqrt[e]/Sqrt[d+e*x^2]]/d-1/5*(d+e*x^2)^(5/2)*(a+b*Log[c*x^n])/(d*x^5)-1/5*b*e^2*n*Sqrt[d+e*x^2]/(d*x)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:425
  public void test0023() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d+e/x), x]", //
        "a*x/d-b*n*x/d+b*x*Log[c*x^n]/d-e*(a+b*Log[c*x^n])*Log[1+d*x/e]/d^2-b*e*n*PolyLog[2,-d*x/e]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:433
  public void test0024() {
    check( //
        "Integrate[(a+b*Log[c*x])/(d+e/x), x]", //
        "a*x/d-b*x/d+b*x*Log[c*x]/d-e*(a+b*Log[c*x])*Log[1+d*x/e]/d^2-b*e*PolyLog[2,-d*x/e]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:443
  public void test0025() {
    check( //
        "Integrate[Log[a/x]/(a*x-x^2), x]", //
        "PolyLog[2,1-a/x]/a");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:464
  public void test0026() {
    check( //
        "Integrate[(f*x)^(-1+m)*(a+b*Log[c*x^n])^2/(d+e*x^m)^3, x]", //
        "-b*n*x*(f*x)^(-1+m)*(a+b*Log[c*x^n])/(d^2*m^2*(d+e*x^m))-1/2*x^(1-m)*(f*x)^(-1+m)*(a+b*Log[c*x^n])^2/(e*m*(d+e*x^m)^2)-b*n*x^(1-m)*(f*x)^(-1+m)*(a+b*Log[c*x^n])*Log[1+d/(e*x^m)]/(d^2*e*m^2)+b^2*n^2*x^(1-m)*(f*x)^(-1+m)*Log[d+e*x^m]/(d^2*e*m^3)+b^2*n^2*x^(1-m)*(f*x)^(-1+m)*PolyLog[2,-d/(e*x^m)]/(d^2*e*m^3)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:478
  public void test0027() {
    check( //
        "Integrate[x^4*(d+e*x^r)*(a+b*Log[c*x^n]), x]", //
        "-1/25*b*d*n*x^5-b*e*n*x^(5+r)/(5+r)^2+1/5*(d*x^5+5*e*x^(5+r)/(5+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:486
  public void test0028() {
    check( //
        "Integrate[x*(d+e*x^r)^2*(a+b*Log[c*x^n]), x]", //
        "-1/4*b*d^2*n*x^2-1/4*b*e^2*n*x^(2*(1+r))/(1+r)^2-2*b*d*e*n*x^(2+r)/(2+r)^2+1/2*(d^2*x^2+e^2*x^(2*(1+r))/(1+r)+4*d*e*x^(2+r)/(2+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:494
  public void test0029() {
    check( //
        "Integrate[(d+e*x^r)^2*(a+b*Log[c*x^n])/x^4, x]", //
        "-1/9*b*d^2*n/x^3-2*b*d*e*n*x^(-3+r)/(3-r)^2-b*e^2*n*x^(-3+2*r)/(3-2*r)^2-1/3*d^2*(a+b*Log[c*x^n])/x^3-2*d*e*x^(-3+r)*(a+b*Log[c*x^n])/(3-r)-e^2*x^(-3+2*r)*(a+b*Log[c*x^n])/(3-2*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:502
  public void test0030() {
    check( //
        "Integrate[(d+e*x^r)^3*(a+b*Log[c*x^n])/x^5, x]", //
        "-1/16*b*d^3*n/x^4-3/4*b*d*e^2*n/((2-r)^2*x^(2*(2-r)))-3*b*d^2*e*n*x^(-4+r)/(4-r)^2-b*e^3*n*x^(-4+3*r)/(4-3*r)^2-1/4*d^3*(a+b*Log[c*x^n])/x^4-3/2*d*e^2*(a+b*Log[c*x^n])/((2-r)*x^(2*(2-r)))-3*d^2*e*x^(-4+r)*(a+b*Log[c*x^n])/(4-r)-e^3*x^(-4+3*r)*(a+b*Log[c*x^n])/(4-3*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:510
  public void test0031() {
    check( //
        "Integrate[(d+e*x^r)^3*(a+b*Log[c*x^n])/x^10, x]", //
        "-1/81*b*d^3*n/x^9-1/9*b*e^3*n/((3-r)^2*x^(3*(3-r)))-3*b*d^2*e*n*x^(-9+r)/(9-r)^2-3*b*d*e^2*n*x^(-9+2*r)/(9-2*r)^2-1/9*d^3*(a+b*Log[c*x^n])/x^9-1/3*e^3*(a+b*Log[c*x^n])/((3-r)*x^(3*(3-r)))-3*d^2*e*x^(-9+r)*(a+b*Log[c*x^n])/(9-r)-3*d*e^2*x^(-9+2*r)*(a+b*Log[c*x^n])/(9-2*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:536
  public void test0032() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x^r)^2), x]", //
        "-e*x^r*(a+b*Log[c*x^n])/(d^2*r*(d+e*x^r))-(a+b*Log[c*x^n])*Log[1+d/(e*x^r)]/(d^2*r)+b*n*Log[d+e*x^r]/(d^2*r^2)+b*n*PolyLog[2,-d/(e*x^r)]/(d^2*r^2)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:558
  public void test0033() {
    check( //
        "Integrate[(f*x)^m*(d+e*x^r)^2*(a+b*Log[c*x^n]), x]", //
        "-2*b*d*e*n*x^(1+r)*(f*x)^m/(1+m+r)^2-b*e^2*n*x^(1+2*r)*(f*x)^m/(1+m+2*r)^2-b*d^2*n*(f*x)^(1+m)/(f*(1+m)^2)+2*d*e*x^(1+r)*(f*x)^m*(a+b*Log[c*x^n])/(1+m+r)+e^2*x^(1+2*r)*(f*x)^m*(a+b*Log[c*x^n])/(1+m+2*r)+d^2*(f*x)^(1+m)*(a+b*Log[c*x^n])/(f*(1+m))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:20
  public void test0034() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[1+e*x]/x^2, x]", //
        "b*e*n*Log[x]-1/2*b*e*n*Log[x]^2+e*Log[x]*(a+b*Log[c*x^n])-b*e*n*Log[1+e*x]-b*n*Log[1+e*x]/x-e*(a+b*Log[c*x^n])*Log[1+e*x]-(a+b*Log[c*x^n])*Log[1+e*x]/x-b*e*n*PolyLog[2,-e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:28
  public void test0035() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[1+e*x]/x^2, x]", //
        "2*b^2*e*n^2*Log[x]-2*b*e*n*Log[1+1/(e*x)]*(a+b*Log[c*x^n])-e*Log[1+1/(e*x)]*(a+b*Log[c*x^n])^2-2*b^2*e*n^2*Log[1+e*x]-2*b^2*n^2*Log[1+e*x]/x-2*b*n*(a+b*Log[c*x^n])*Log[1+e*x]/x-(a+b*Log[c*x^n])^2*Log[1+e*x]/x+2*b^2*e*n^2*PolyLog[2,(-1)/(e*x)]+2*b*e*n*(a+b*Log[c*x^n])*PolyLog[2,(-1)/(e*x)]+2*b^2*e*n^2*PolyLog[3,(-1)/(e*x)]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:36
  public void test0036() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[1+e*x]/x^3, x]", //
        "-45/8*b^3*e*n^3/x-3/8*b^3*e^2*n^3*Log[x]-21/4*b^2*e*n^2*(a+b*Log[c*x^n])/x+3/4*b^2*e^2*n^2*Log[1+1/(e*x)]*(a+b*Log[c*x^n])-9/4*b*e*n*(a+b*Log[c*x^n])^2/x+3/4*b*e^2*n*Log[1+1/(e*x)]*(a+b*Log[c*x^n])^2-1/2*e*(a+b*Log[c*x^n])^3/x+1/2*e^2*Log[1+1/(e*x)]*(a+b*Log[c*x^n])^3+3/8*b^3*e^2*n^3*Log[1+e*x]-3/8*b^3*n^3*Log[1+e*x]/x^2-3/4*b^2*n^2*(a+b*Log[c*x^n])*Log[1+e*x]/x^2-3/4*b*n*(a+b*Log[c*x^n])^2*Log[1+e*x]/x^2-1/2*(a+b*Log[c*x^n])^3*Log[1+e*x]/x^2-3/4*b^3*e^2*n^3*PolyLog[2,(-1)/(e*x)]-3/2*b^2*e^2*n^2*(a+b*Log[c*x^n])*PolyLog[2,(-1)/(e*x)]-3/2*b*e^2*n*(a+b*Log[c*x^n])^2*PolyLog[2,(-1)/(e*x)]-3/2*b^3*e^2*n^3*PolyLog[3,(-1)/(e*x)]-3*b^2*e^2*n^2*(a+b*Log[c*x^n])*PolyLog[3,(-1)/(e*x)]-3*b^3*e^2*n^3*PolyLog[4,(-1)/(e*x)]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:46
  public void test0037() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*x^2)]/x^4, x]", //
        "-8/9*b*d*f*n/x-2/9*b*d^(3/2)*f^(3/2)*n*ArcTan[x*Sqrt[d]*Sqrt[f]]-2/3*d*f*(a+b*Log[c*x^n])/x-2/3*d^(3/2)*f^(3/2)*ArcTan[x*Sqrt[d]*Sqrt[f]]*(a+b*Log[c*x^n])-1/9*b*n*Log[1+d*f*x^2]/x^3-1/3*(a+b*Log[c*x^n])*Log[1+d*f*x^2]/x^3+1/3*I*b*d^(3/2)*f^(3/2)*n*PolyLog[2,-I*x*Sqrt[d]*Sqrt[f]]-1/3*I*b*d^(3/2)*f^(3/2)*n*PolyLog[2,I*x*Sqrt[d]*Sqrt[f]]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:96
  public void test0038() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x)^m], x]", //
        "2*b*m*n*x-m*x*(a+b*Log[c*x^n])-b*n*(e+f*x)*Log[d*(e+f*x)^m]/f-b*e*n*Log[-f*x/e]*Log[d*(e+f*x)^m]/f+(e+f*x)*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/f-b*e*m*n*PolyLog[2,1+f*x/e]/f");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:104
  public void test0039() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x, x]", //
        "1/3*(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m]/(b*n)-1/3*m*(a+b*Log[c*x^n])^3*Log[1+f*x/e]/(b*n)-m*(a+b*Log[c*x^n])^2*PolyLog[2,-f*x/e]+2*b*m*n*(a+b*Log[c*x^n])*PolyLog[3,-f*x/e]-2*b^2*m*n^2*PolyLog[4,-f*x/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:112
  public void test0040() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m]/x^3, x]", //
        "-45/8*b^3*f*m*n^3/(e*x)-3/8*b^3*f^2*m*n^3*Log[x]/e^2-21/4*b^2*f*m*n^2*(a+b*Log[c*x^n])/(e*x)+3/4*b^2*f^2*m*n^2*Log[1+e/(f*x)]*(a+b*Log[c*x^n])/e^2-9/4*b*f*m*n*(a+b*Log[c*x^n])^2/(e*x)+3/4*b*f^2*m*n*Log[1+e/(f*x)]*(a+b*Log[c*x^n])^2/e^2-1/2*f*m*(a+b*Log[c*x^n])^3/(e*x)+1/2*f^2*m*Log[1+e/(f*x)]*(a+b*Log[c*x^n])^3/e^2+3/8*b^3*f^2*m*n^3*Log[e+f*x]/e^2-3/8*b^3*n^3*Log[d*(e+f*x)^m]/x^2-3/4*b^2*n^2*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^2-3/4*b*n*(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x^2-1/2*(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m]/x^2-3/4*b^3*f^2*m*n^3*PolyLog[2,-e/(f*x)]/e^2-3/2*b^2*f^2*m*n^2*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x)]/e^2-3/2*b*f^2*m*n*(a+b*Log[c*x^n])^2*PolyLog[2,-e/(f*x)]/e^2-3/2*b^3*f^2*m*n^3*PolyLog[3,-e/(f*x)]/e^2-3*b^2*f^2*m*n^2*(a+b*Log[c*x^n])*PolyLog[3,-e/(f*x)]/e^2-3*b^3*f^2*m*n^3*PolyLog[4,-e/(f*x)]/e^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:122
  public void test0041() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^2, x]", //
        "-b*n*Log[d*(e+f*x^2)^m]/x-(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x+2*b*m*n*ArcTan[x*Sqrt[f]/Sqrt[e]]*Sqrt[f]/Sqrt[e]+2*m*ArcTan[x*Sqrt[f]/Sqrt[e]]*(a+b*Log[c*x^n])*Sqrt[f]/Sqrt[e]-I*b*m*n*PolyLog[2,-I*x*Sqrt[f]/Sqrt[e]]*Sqrt[f]/Sqrt[e]+I*b*m*n*PolyLog[2,I*x*Sqrt[f]/Sqrt[e]]*Sqrt[f]/Sqrt[e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:148
  public void test0042() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^4, x]", //
        "-11/225*b*f*k*n/(e*x^(5/2))+5/72*b*f^2*k*n/(e^2*x^2)-1/9*b*f^3*k*n/(e^3*x^(3/2))+2/9*b*f^4*k*n/(e^4*x)-1/18*b*f^6*k*n*Log[x]/e^6+1/12*b*f^6*k*n*Log[x]^2/e^6-1/15*f*k*(a+b*Log[c*x^n])/(e*x^(5/2))+1/12*f^2*k*(a+b*Log[c*x^n])/(e^2*x^2)-1/9*f^3*k*(a+b*Log[c*x^n])/(e^3*x^(3/2))+1/6*f^4*k*(a+b*Log[c*x^n])/(e^4*x)-1/6*f^6*k*Log[x]*(a+b*Log[c*x^n])/e^6+1/9*b*f^6*k*n*Log[e+f*Sqrt[x]]/e^6+1/3*f^6*k*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e^6-2/3*b*f^6*k*n*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e^6-1/9*b*n*Log[d*(e+f*Sqrt[x])^k]/x^3-1/3*(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^3-2/3*b*f^6*k*n*PolyLog[2,1+f*Sqrt[x]/e]/e^6-7/9*b*f^5*k*n/(e^5*Sqrt[x])-1/3*f^5*k*(a+b*Log[c*x^n])/(e^5*Sqrt[x])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:166
  public void test0043() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^(7/2), x]", //
        "-9/100*b*f*k*n/(e*x^2)+32/225*b*f^2*k*n/(e^2*x^(3/2))-7/25*b*f^3*k*n/(e^3*x)+2/25*b*f^5*k*n*Log[x]/e^5-1/10*b*f^5*k*n*Log[x]^2/e^5-1/10*f*k*(a+b*Log[c*x^n])/(e*x^2)+2/15*f^2*k*(a+b*Log[c*x^n])/(e^2*x^(3/2))-1/5*f^3*k*(a+b*Log[c*x^n])/(e^3*x)+1/5*f^5*k*Log[x]*(a+b*Log[c*x^n])/e^5-4/25*b*f^5*k*n*Log[e+f*Sqrt[x]]/e^5-2/5*f^5*k*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e^5+4/5*b*f^5*k*n*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e^5-4/25*b*n*Log[d*(e+f*Sqrt[x])^k]/x^(5/2)-2/5*(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^(5/2)+4/5*b*f^5*k*n*PolyLog[2,1+f*Sqrt[x]/e]/e^5+24/25*b*f^4*k*n/(e^4*Sqrt[x])+2/5*f^4*k*(a+b*Log[c*x^n])/(e^4*Sqrt[x])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:184
  public void test0044() {
    check( //
        "Integrate[(g*x)^(-1-m)*(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^k], x]", //
        "b*f*k*n*x^m*Log[x]/(e*g*m*(g*x)^m)-1/2*b*f*k*n*x^m*Log[x]^2/(e*g*(g*x)^m)+f*k*x^m*Log[x]*(a+b*Log[c*x^n])/(e*g*(g*x)^m)-b*f*k*n*x^m*Log[e+f*x^m]/(e*g*m^2*(g*x)^m)+b*f*k*n*x^m*Log[-f*x^m/e]*Log[e+f*x^m]/(e*g*m^2*(g*x)^m)-f*k*x^m*(a+b*Log[c*x^n])*Log[e+f*x^m]/(e*g*m*(g*x)^m)-b*n*Log[d*(e+f*x^m)^k]/(g*m^2*(g*x)^m)-(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^k]/(g*m*(g*x)^m)+b*f*k*n*x^m*PolyLog[2,1+f*x^m/e]/(e*g*m^2*(g*x)^m)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:206
  public void test0045() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/x^4, x]", //
        "-2/81*b^2*e*n^2*r/x^3-2/81*b*e*n*(3*a+b*n)*r/x^3-1/81*e*(9*a^2+6*a*b*n+2*b^2*n^2)*r/x^3-2/27*b^2*e*n*r*Log[c*x^n]/x^3-2/27*b*e*(3*a+b*n)*r*Log[c*x^n]/x^3-1/9*b^2*e*r*Log[c*x^n]^2/x^3-2/27*b^2*n^2*(d+e*Log[f*x^r])/x^3-2/9*b*n*(a+b*Log[c*x^n])*(d+e*Log[f*x^r])/x^3-1/3*(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/x^3");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:253
  public void test0046() {
    check( //
        "Integrate[Log[x]^2*PolyLog[n,a*x]/x, x]", //
        "Log[x]^2*PolyLog[1+n,a*x]-2*Log[x]*PolyLog[2+n,a*x]+2*PolyLog[3+n,a*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:290
  public void test0047() {
    check( //
        "Integrate[Log[c*(b*x^n)^p]^2/x, x]", //
        "1/3*Log[c*(b*x^n)^p]^3/(n*p)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:302
  public void test0048() {
    check( //
        "Integrate[(e*x)^q/(a+b*Log[c*(d*x^m)^n])^2, x]", //
        "(1+q)*(e*x)^(1+q)*ExpIntegralEi[(1+q)*(a+b*Log[c*(d*x^m)^n])/(b*m*n)]/(E^(a*(1+q)/(b*m*n))*b^2*e*m^2*n^2*(c*(d*x^m)^n)^((1+q)/(m*n)))-(e*x)^(1+q)/(b*e*m*n*(a+b*Log[c*(d*x^m)^n]))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:28
  public void test0049() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(a*g+b*g*x)^4, x]", //
        "-2*B^2*d^2*n^2*(c+d*x)/((b*c-a*d)^3*g^4*(a+b*x))+1/2*b*B^2*d*n^2*(c+d*x)^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-2/27*b^2*B^2*n^2*(c+d*x)^3/((b*c-a*d)^3*g^4*(a+b*x)^3)-2*B*d^2*n*(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^3*g^4*(a+b*x))+b*B*d*n*(c+d*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^3*g^4*(a+b*x)^2)-2/9*b^2*B*n*(c+d*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^3*g^4*(a+b*x)^3)-d^2*(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^3*g^4*(a+b*x))+b*d*(c+d*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-1/3*b^2*(c+d*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^3*g^4*(a+b*x)^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:50
  public void test0050() {
    check( //
        "Integrate[(c*g+d*g*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "-1/3*B*(b*c-a*d)^2*g^2*n*x/b^2-1/6*B*(b*c-a*d)*g^2*n*(c+d*x)^2/(b*d)-1/3*B*(b*c-a*d)^3*g^2*n*Log[a+b*x]/(b^3*d)+1/3*g^2*(c+d*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/d");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:71
  public void test0051() {
    check( //
        "Integrate[1/((c*g+d*g*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])), x]", //
        "(a+b*x)*ExpIntegralEi[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(B*n)]/(E^(A/(B*n))*B*(b*c-a*d)*g^2*n*(e*((a+b*x)/(c+d*x))^n)^(1/n)*(c+d*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:88
  public void test0052() {
    check( //
        "Integrate[A+B*Log[e*((a+b*x)/(c+d*x))^n], x]", //
        "A*x+B*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/b-B*(b*c-a*d)*n*Log[c+d*x]/(b*d)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:126
  public void test0053() {
    check( //
        "Integrate[(a*g+b*g*x)^3*(A+B*Log[e*(a+b*x)/(c+d*x)]), x]", //
        "-1/4*B*(b*c-a*d)^3*g^3*x/d^3+1/8*B*(b*c-a*d)^2*g^3*(a+b*x)^2/(b*d^2)-1/12*B*(b*c-a*d)*g^3*(a+b*x)^3/(b*d)+1/4*g^3*(a+b*x)^4*(A+B*Log[e*(a+b*x)/(c+d*x)])/b+1/4*B*(b*c-a*d)^4*g^3*Log[c+d*x]/(b*d^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:144
  public void test0054() {
    check( //
        "Integrate[Log[1+1/(a+b*x)]/(a+b*x), x]", //
        "PolyLog[2,(-1)/(a+b*x)]/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:164
  public void test0055() {
    check( //
        "Integrate[(a*g+b*g*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2]), x]", //
        "2/3*B*(b*c-a*d)^2*g^2*x/d^2-1/3*B*(b*c-a*d)*g^2*(a+b*x)^2/(b*d)+1/3*g^2*(a+b*x)^3*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/b-2/3*B*(b*c-a*d)^3*g^2*Log[c+d*x]/(b*d^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:209
  public void test0056() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/(a+b*x)^2, x]", //
        "-2*B^2*n^2*(c+d*x)/((b*c-a*d)*(a+b*x))-2*B*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)*(a+b*x))-(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)*(a+b*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:217
  public void test0057() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/(a+b*x)^2, x]", //
        "-6*B^3*n^3*(c+d*x)/((b*c-a*d)*(a+b*x))-6*B^2*n^2*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)*(a+b*x))-3*B*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)*(a+b*x))-(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)*(a+b*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:278
  public void test0058() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])^2/(a*g+b*g*x)^2, x]", //
        "4*A*B*(c+d*x)/((b*c-a*d)*g^2*(a+b*x))-8*B^2*(c+d*x)/((b*c-a*d)*g^2*(a+b*x))+4*B^2*(c+d*x)*Log[e*(c+d*x)^2/(a+b*x)^2]/((b*c-a*d)*g^2*(a+b*x))-(c+d*x)*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])^2/((b*c-a*d)*g^2*(a+b*x))");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:280
  public void test0059() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^p/((a*f+b*f*x)*(c*g+d*g*x)), x]", //
        "(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^(1+p)/(B*(b*c-a*d)*f*g*n*(1+p))");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:23
  public void test0060() {
    check( //
        "Integrate[(a+b*x)^3*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r], x]", //
        "1/4*(b*c-a*d)^3*q*r*x/d^3-1/8*(b*c-a*d)^2*q*r*(a+b*x)^2/(b*d^2)+1/12*(b*c-a*d)*q*r*(a+b*x)^3/(b*d)-1/16*p*r*(a+b*x)^4/b-1/16*q*r*(a+b*x)^4/b-1/4*(b*c-a*d)^4*q*r*Log[c+d*x]/(b*d^4)+1/4*(a+b*x)^4*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/b");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:49
  public void test0061() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(g+h*x)^3, x]", //
        "1/2*b*p*r/(h*(b*g-a*h)*(g+h*x))+1/2*d*q*r/(h*(d*g-c*h)*(g+h*x))+1/2*b^2*p*r*Log[a+b*x]/(h*(b*g-a*h)^2)+1/2*d^2*q*r*Log[c+d*x]/(h*(d*g-c*h)^2)-1/2*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(h*(g+h*x)^2)-1/2*b^2*p*r*Log[g+h*x]/(h*(b*g-a*h)^2)-1/2*d^2*q*r*Log[g+h*x]/(h*(d*g-c*h)^2)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:57
  public void test0062() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]^2/(g+h*x)^2, x]", //
        "2*b*p*q*r^2*Log[-d*(a+b*x)/(b*c-a*d)]*Log[c+d*x]/(h*(b*g-a*h))+2*d*p*q*r^2*Log[a+b*x]*Log[b*(c+d*x)/(b*c-a*d)]/(h*(d*g-c*h))-2*b*p*r*Log[a+b*x]*(p*r*Log[a+b*x]+q*r*Log[c+d*x]-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r])/(h*(b*g-a*h))-2*d*q*r*Log[c+d*x]*(p*r*Log[a+b*x]+q*r*Log[c+d*x]-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r])/(h*(d*g-c*h))-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]^2/(h*(g+h*x))+2*b*p*r*(p*r*Log[a+b*x]+q*r*Log[c+d*x]-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r])*Log[g+h*x]/(h*(b*g-a*h))+2*d*q*r*(p*r*Log[a+b*x]+q*r*Log[c+d*x]-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r])*Log[g+h*x]/(h*(d*g-c*h))-2*d*p*q*r^2*Log[a+b*x]*Log[b*(g+h*x)/(b*g-a*h)]/(h*(d*g-c*h))-2*b*p*q*r^2*Log[c+d*x]*Log[d*(g+h*x)/(d*g-c*h)]/(h*(b*g-a*h))-2*b*p^2*r^2*Log[a+b*x]*Log[1+(b*g-a*h)/(h*(a+b*x))]/(h*(b*g-a*h))-2*d*q^2*r^2*Log[c+d*x]*Log[1+(d*g-c*h)/(h*(c+d*x))]/(h*(d*g-c*h))+2*b*p^2*r^2*PolyLog[2,(-b*g+a*h)/(h*(a+b*x))]/(h*(b*g-a*h))+2*d*p*q*r^2*PolyLog[2,-d*(a+b*x)/(b*c-a*d)]/(h*(d*g-c*h))-2*d*p*q*r^2*PolyLog[2,-h*(a+b*x)/(b*g-a*h)]/(h*(d*g-c*h))+2*d*q^2*r^2*PolyLog[2,(-d*g+c*h)/(h*(c+d*x))]/(h*(d*g-c*h))+2*b*p*q*r^2*PolyLog[2,b*(c+d*x)/(b*c-a*d)]/(h*(b*g-a*h))-2*b*p*q*r^2*PolyLog[2,-h*(c+d*x)/(d*g-c*h)]/(h*(b*g-a*h))");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:125
  public void test0063() {
    check( //
        "Integrate[Log[e*((a+b*x)/(c+d*x))^n]^2*Log[(b*c-a*d)/(b*(c+d*x))]/((c+d*x)*(a*g+b*g*x)), x]", //
        "-Log[e*((a+b*x)/(c+d*x))^n]^2*PolyLog[2,1+(-b*c+a*d)/(b*(c+d*x))]/((b*c-a*d)*g)+2*n*Log[e*((a+b*x)/(c+d*x))^n]*PolyLog[3,1+(-b*c+a*d)/(b*(c+d*x))]/((b*c-a*d)*g)-2*n^2*PolyLog[4,1+(-b*c+a*d)/(b*(c+d*x))]/((b*c-a*d)*g)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:133
  public void test0064() {
    check( //
        "Integrate[Log[c*x^2/(b+a*x)^2]^2, x]", //
        "x*Log[c*x^2/(b+a*x)^2]^2+4*b*Log[c*x^2/(b+a*x)^2]*Log[b/(b+a*x)]/a+8*b*PolyLog[2,1-b/(b+a*x)]/a");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:17
  public void test0065() {
    check( //
        "Integrate[1/Log[c*(d+e*x)]^4, x]", //
        "1/6*LogIntegral[c*(d+e*x)]/(c*e)+1/3*(-d-e*x)/(e*Log[c*(d+e*x)]^3)+1/6*(-d-e*x)/(e*Log[c*(d+e*x)]^2)+1/6*(-d-e*x)/(e*Log[c*(d+e*x)])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:29
  public void test0066() {
    check( //
        "Integrate[Log[c*(d+e*x)]^p, x]", //
        "Gamma[1+p,-Log[c*(d+e*x)]]*Log[c*(d+e*x)]^p/(c*e*(-Log[c*(d+e*x)])^p)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:43
  public void test0067() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^(5/2), x]", //
        "-5/2*b*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^(3/2)/e+(d+e*x)*(a+b*Log[c*(d+e*x)^n])^(5/2)/e-15/8*b^(5/2)*n^(5/2)*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*e*(c*(d+e*x)^n)^(1/n))+15/4*b^2*n^2*(d+e*x)*Sqrt[a+b*Log[c*(d+e*x)^n]]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:69
  public void test0068() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/(f+g*x), x]", //
        "(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/g+b*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:77
  public void test0069() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^2/(f+g*x), x]", //
        "(a+b*Log[c*(d+e*x)^n])^2*Log[e*(f+g*x)/(e*f-d*g)]/g+2*b*n*(a+b*Log[c*(d+e*x)^n])*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g-2*b^2*n^2*PolyLog[3,-g*(d+e*x)/(e*f-d*g)]/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:85
  public void test0070() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^3/(f+g*x), x]", //
        "(a+b*Log[c*(d+e*x)^n])^3*Log[e*(f+g*x)/(e*f-d*g)]/g+3*b*n*(a+b*Log[c*(d+e*x)^n])^2*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g-6*b^2*n^2*(a+b*Log[c*(d+e*x)^n])*PolyLog[3,-g*(d+e*x)/(e*f-d*g)]/g+6*b^3*n^3*PolyLog[4,-g*(d+e*x)/(e*f-d*g)]/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:101
  public void test0071() {
    check( //
        "Integrate[(a+b*Log[c*(1/c+e*x)])/x, x]", //
        "a*Log[x]-b*PolyLog[2,-c*e*x]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:119
  public void test0072() {
    check( //
        "Integrate[(f+g*x)^3/(a+b*Log[c*(d+e*x)^n]), x]", //
        "(e*f-d*g)^3*(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b*e^4*n*(c*(d+e*x)^n)^(1/n))+3*g*(e*f-d*g)^2*(d+e*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(2*a/(b*n))*b*e^4*n*(c*(d+e*x)^n)^(2/n))+3*g^2*(e*f-d*g)*(d+e*x)^3*ExpIntegralEi[3*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(3*a/(b*n))*b*e^4*n*(c*(d+e*x)^n)^(3/n))+g^3*(d+e*x)^4*ExpIntegralEi[4*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(4*a/(b*n))*b*e^4*n*(c*(d+e*x)^n)^(4/n))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:131
  public void test0073() {
    check( //
        "Integrate[(f+g*x)^2/(a+b*Log[c*(d+e*x)^n])^3, x]", //
        "1/2*(e*f-d*g)^2*(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b^3*e^3*n^3*(c*(d+e*x)^n)^(1/n))+4*g*(e*f-d*g)*(d+e*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(2*a/(b*n))*b^3*e^3*n^3*(c*(d+e*x)^n)^(2/n))+9/2*g^2*(d+e*x)^3*ExpIntegralEi[3*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(3*a/(b*n))*b^3*e^3*n^3*(c*(d+e*x)^n)^(3/n))-1/2*(d+e*x)*(f+g*x)^2/(b*e*n*(a+b*Log[c*(d+e*x)^n])^2)+(e*f-d*g)*(d+e*x)*(f+g*x)/(b^2*e^2*n^2*(a+b*Log[c*(d+e*x)^n]))-3/2*(d+e*x)*(f+g*x)^2/(b^2*e*n^2*(a+b*Log[c*(d+e*x)^n]))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:148
  public void test0074() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^(3/2), x]", //
        "(d+e*x)*(a+b*Log[c*(d+e*x)^n])^(3/2)/e+3/4*b^(3/2)*n^(3/2)*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*e*(c*(d+e*x)^n)^(1/n))-3/2*b*n*(d+e*x)*Sqrt[a+b*Log[c*(d+e*x)^n]]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:344
  public void test0075() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e*x)^n])/(f+g*x)^2, x]", //
        "-b*e*f*n*Log[d+e*x]/(g^2*(e*f-d*g))+f*(a+b*Log[c*(d+e*x)^n])/(g^2*(f+g*x))+b*e*f*n*Log[f+g*x]/(g^2*(e*f-d*g))+(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/g^2+b*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:511
  public void test0076() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])/x^5, x]", //
        "-7/144*b*e*m*n/(d*x^3)+3/32*b*e^2*m*n/(d^2*x^2)-5/16*b*e^3*m*n/(d^3*x)-1/16*b*e^4*m*n*Log[x]/d^4-1/12*b*e*n*Log[f*x^m]/(d*x^3)+1/8*b*e^2*n*Log[f*x^m]/(d^2*x^2)-1/4*b*e^3*n*Log[f*x^m]/(d^3*x)+1/4*b*e^4*n*Log[1+d/(e*x)]*Log[f*x^m]/d^4+1/16*b*e^4*m*n*Log[d+e*x]/d^4-1/16*(m/x^4+4*Log[f*x^m]/x^4)*(a+b*Log[c*(d+e*x)^n])-1/4*b*e^4*m*n*PolyLog[2,-d/(e*x)]/d^4");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:529
  public void test0077() {
    check( //
        "Integrate[Log[a+b*x]*Log[c+d*x]/x, x]", //
        "Log[-b*x/a]*Log[a+b*x]*Log[c+d*x]+1/2*(Log[-b*x/a]+Log[(b*c-a*d)/(b*(c+d*x))]-Log[-(b*c-a*d)*x/(a*(c+d*x))])*Log[a*(c+d*x)/(c*(a+b*x))]^2-1/2*(Log[-b*x/a]-Log[-d*x/c])*(Log[a+b*x]+Log[a*(c+d*x)/(c*(a+b*x))])^2+(Log[c+d*x]-Log[a*(c+d*x)/(c*(a+b*x))])*PolyLog[2,1+b*x/a]+Log[a*(c+d*x)/(c*(a+b*x))]*PolyLog[2,c*(a+b*x)/(a*(c+d*x))]-Log[a*(c+d*x)/(c*(a+b*x))]*PolyLog[2,d*(a+b*x)/(b*(c+d*x))]+(Log[a+b*x]+Log[a*(c+d*x)/(c*(a+b*x))])*PolyLog[2,1+d*x/c]-PolyLog[3,1+b*x/a]+PolyLog[3,c*(a+b*x)/(a*(c+d*x))]-PolyLog[3,d*(a+b*x)/(b*(c+d*x))]-PolyLog[3,1+d*x/c]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:581
  public void test0078() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^m)^n])^2, x]", //
        "-2*a*b*m*n*x+2*b^2*m^2*n^2*x-2*b^2*m*n*(e+f*x)*Log[c*(d*(e+f*x)^m)^n]/f+(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^2/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:591
  public void test0079() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^m)^n])^(1/2), x]", //
        "(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/(Sqrt[b]*Sqrt[m]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*m*n))*f*(c*(d*(e+f*x)^m)^n)^(1/(m*n))*Sqrt[b]*Sqrt[m]*Sqrt[n])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:607
  public void test0080() {
    check( //
        "Integrate[(g+h*x)*(a+b*Log[c*(d*(e+f*x)^p)^q]), x]", //
        "-1/2*b*(f*g-e*h)*p*q*x/f-1/4*b*p*q*(g+h*x)^2/h-1/2*b*(f*g-e*h)^2*p*q*Log[e+f*x]/(f^2*h)+1/2*(g+h*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])/h");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:615
  public void test0081() {
    check( //
        "Integrate[(g+h*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2, x]", //
        "-2*a*b*(f*g-e*h)*p*q*x/f+2*b^2*(f*g-e*h)*p^2*q^2*x/f+1/4*b^2*h*p^2*q^2*(e+f*x)^2/f^2-2*b^2*(f*g-e*h)*p*q*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f^2-1/2*b*h*p*q*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])/f^2+(f*g-e*h)*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^2+1/2*h*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:623
  public void test0082() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^3/(g+h*x), x]", //
        "(a+b*Log[c*(d*(e+f*x)^p)^q])^3*Log[f*(g+h*x)/(f*g-e*h)]/h+3*b*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])^2*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/h-6*b^2*p^2*q^2*(a+b*Log[c*(d*(e+f*x)^p)^q])*PolyLog[3,-h*(e+f*x)/(f*g-e*h)]/h+6*b^3*p^3*q^3*PolyLog[4,-h*(e+f*x)/(f*g-e*h)]/h");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:633
  public void test0083() {
    check( //
        "Integrate[(g+h*x)/(a+b*Log[c*(d*(e+f*x)^p)^q]), x]", //
        "(f*g-e*h)*(e+f*x)*ExpIntegralEi[(a+b*Log[c*(d*(e+f*x)^p)^q])/(b*p*q)]/(E^(a/(b*p*q))*b*f^2*p*q*(c*(d*(e+f*x)^p)^q)^(1/(p*q)))+h*(e+f*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d*(e+f*x)^p)^q])/(b*p*q)]/(E^(2*a/(b*p*q))*b*f^2*p*q*(c*(d*(e+f*x)^p)^q)^(2/(p*q)))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:665
  public void test0084() {
    check( //
        "Integrate[1/Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]], x]", //
        "(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]]/(Sqrt[b]*Sqrt[p]*Sqrt[q])]*Sqrt[Pi]/(E^(a/(b*p*q))*f*(c*(d*(e+f*x)^p)^q)^(1/(p*q))*Sqrt[b]*Sqrt[p]*Sqrt[q])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:748
  public void test0085() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])/(g+h*x), x]", //
        "(a+b*Log[c*(d*(e+f*x)^p)^q])*Log[f*(g+h*x)/(f*g-e*h)]/h+b*p*q*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/h");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:14
  public void test0086() {
    check( //
        "Integrate[x^2*Log[c*(a+b*x^2)^p], x]", //
        "2/3*a*p*x/b-2/9*p*x^3-2/3*a^(3/2)*p*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(3/2)+1/3*x^3*Log[c*(a+b*x^2)^p]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:23
  public void test0087() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]/x^7, x]", //
        "-1/12*b*p/(a*x^4)+1/6*b^2*p/(a^2*x^2)+1/3*b^3*p*Log[x]/a^3-1/6*b^3*p*Log[a+b*x^2]/a^3-1/6*Log[c*(a+b*x^2)^p]/x^6");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:31
  public void test0088() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/x^2, x]", //
        "-b^(1/3)*p*Log[a^(1/3)+b^(1/3)*x]/a^(1/3)+1/2*b^(1/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(1/3)-Log[c*(a+b*x^3)^p]/x-b^(1/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/a^(1/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:49
  public void test0089() {
    check( //
        "Integrate[x^4*Log[c*(a+b/x^2)^p], x]", //
        "-2/5*b^2*p*x/a^2+2/15*b*p*x^3/a+2/5*b^(5/2)*p*ArcTan[x*Sqrt[a]/Sqrt[b]]/a^(5/2)+1/5*x^5*Log[c*(a+b/x^2)^p]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:57
  public void test0090() {
    check( //
        "Integrate[Log[c*(a+b/x^2)^p]/x^4, x]", //
        "2/9*p/x^3-2/3*a*p/(b*x)-2/3*a^(3/2)*p*ArcTan[x*Sqrt[a]/Sqrt[b]]/b^(3/2)-1/3*Log[c*(a+b/x^2)^p]/x^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:69
  public void test0091() {
    check( //
        "Integrate[Log[c*(a+b*Sqrt[x])^p]/x^3, x]", //
        "-1/6*b*p/(a*x^(3/2))+1/4*b^2*p/(a^2*x)-1/4*b^4*p*Log[x]/a^4+1/2*b^4*p*Log[a+b*Sqrt[x]]/a^4-1/2*Log[c*(a+b*Sqrt[x])^p]/x^2-1/2*b^3*p/(a^3*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:98
  public void test0092() {
    check( //
        "Integrate[(f*x)^(-1-2*n)*Log[c*(d+e*x^n)^p], x]", //
        "-1/2*e*p*x^n/(d*f*n*(f*x)^(2*n))-1/2*e^2*p*x^(2*n)*Log[x]/(d^2*f*(f*x)^(2*n))+1/2*e^2*p*x^(2*n)*Log[d+e*x^n]/(d^2*f*n*(f*x)^(2*n))-1/2*Log[c*(d+e*x^n)^p]/(f*n*(f*x)^(2*n))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:118
  public void test0093() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^2/x^7, x]", //
        "-1/6*b^2*p^2/(a^2*x^2)-b^3*p^2*Log[x]/a^3+1/6*b^3*p^2*Log[a+b*x^2]/a^3-1/6*b*p*Log[c*(a+b*x^2)^p]/(a*x^4)+1/3*b^2*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]/(a^3*x^2)-1/6*Log[c*(a+b*x^2)^p]^2/x^6+1/3*b^3*p*Log[c*(a+b*x^2)^p]*Log[1-a/(a+b*x^2)]/a^3-1/3*b^3*p^2*PolyLog[2,a/(a+b*x^2)]/a^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:126
  public void test0094() {
    check( //
        "Integrate[x^5*Log[c*(a+b*x^2)^p]^3, x]", //
        "-3*a^2*p^3*x^2/b^2+3/8*a*p^3*(a+b*x^2)^2/b^3-1/27*p^3*(a+b*x^2)^3/b^3+3*a^2*p^2*(a+b*x^2)*Log[c*(a+b*x^2)^p]/b^3-3/4*a*p^2*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]/b^3+1/9*p^2*(a+b*x^2)^3*Log[c*(a+b*x^2)^p]/b^3-3/2*a^2*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/b^3+3/4*a*p*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]^2/b^3-1/6*p*(a+b*x^2)^3*Log[c*(a+b*x^2)^p]^2/b^3+1/2*a^2*(a+b*x^2)*Log[c*(a+b*x^2)^p]^3/b^3-1/2*a*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]^3/b^3+1/6*(a+b*x^2)^3*Log[c*(a+b*x^2)^p]^3/b^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:140
  public void test0095() {
    check( //
        "Integrate[x/Log[c*(a+b*x^2)^p], x]", //
        "1/2*(a+b*x^2)*ExpIntegralEi[Log[c*(a+b*x^2)^p]/p]/(b*p*(c*(a+b*x^2)^p)^(1/p))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:163
  public void test0096() {
    check( //
        "Integrate[x/Log[c*(a+b*x^2)]^2, x]", //
        "1/2*LogIntegral[c*(a+b*x^2)]/(b*c)+1/2*(-a-b*x^2)/(b*Log[c*(a+b*x^2)])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:192
  public void test0097() {
    check( //
        "Integrate[x^5/Log[c*(d+e*x^3)^p]^2, x]", //
        "-1/3*d*(d+e*x^3)*ExpIntegralEi[Log[c*(d+e*x^3)^p]/p]/(e^2*p^2*(c*(d+e*x^3)^p)^(1/p))+2/3*(d+e*x^3)^2*ExpIntegralEi[2*Log[c*(d+e*x^3)^p]/p]/(e^2*p^2*(c*(d+e*x^3)^p)^(2/p))-1/3*x^3*(d+e*x^3)/(e*p*Log[c*(d+e*x^3)^p])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:230
  public void test0098() {
    check( //
        "Integrate[(d+e*x)^2*Log[c*(a+b*x)^p], x]", //
        "-1/3*(b*d-a*e)^2*p*x/b^2-1/6*(b*d-a*e)*p*(d+e*x)^2/(b*e)-1/9*p*(d+e*x)^3/e-1/3*(b*d-a*e)^3*p*Log[a+b*x]/(b^3*e)+1/3*(d+e*x)^3*Log[c*(a+b*x)^p]/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:256
  public void test0099() {
    check( //
        "Integrate[Log[c*(a+b/x)^p]/(d+e*x), x]", //
        "Log[c*(a+b/x)^p]*Log[d+e*x]/e+p*Log[-e*x/d]*Log[d+e*x]/e-p*Log[-e*(b+a*x)/(a*d-b*e)]*Log[d+e*x]/e-p*PolyLog[2,a*(d+e*x)/(a*d-b*e)]/e+p*PolyLog[2,1+e*x/d]/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:287
  public void test0100() {
    check( //
        "Integrate[Log[c*(a+b*x)^p]/(d+e*x), x]", //
        "Log[c*(a+b*x)^p]*Log[b*(d+e*x)/(b*d-a*e)]/e+p*PolyLog[2,-e*(a+b*x)/(b*d-a*e)]/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:412
  public void test0101() {
    check( //
        "Integrate[(f+g*x^2)*Log[c*(d+e*x^2)^p]/x, x]", //
        "-1/2*g*p*x^2+1/2*g*(d+e*x^2)*Log[c*(d+e*x^2)^p]/e+1/2*f*Log[-e*x^2/d]*Log[c*(d+e*x^2)^p]+1/2*f*p*PolyLog[2,1+e*x^2/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:421
  public void test0102() {
    check( //
        "Integrate[(f+g*x^2)*Log[c*(d+e*x^2)^p]/x^6, x]", //
        "-2/15*e*f*p/(d*x^3)+2/5*e^2*f*p/(d^2*x)-2/3*e*g*p/(d*x)+2/5*e^(5/2)*f*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/d^(5/2)-2/3*e^(3/2)*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/d^(3/2)-1/5*f*Log[c*(d+e*x^2)^p]/x^5-1/3*g*Log[c*(d+e*x^2)^p]/x^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:467
  public void test0103() {
    check( //
        "Integrate[(f+g*x^n)*Log[c*(d+e*x^n)^p]/x, x]", //
        "-g*p*x^n/n+g*(d+e*x^n)*Log[c*(d+e*x^n)^p]/(e*n)+f*Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/n+f*p*PolyLog[2,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:512
  public void test0104() {
    check( //
        "Integrate[Log[(a+b*x^n)/x^n]/x, x]", //
        "-Log[-a/(b*x^n)]*Log[b+a/x^n]/n-PolyLog[2,1+a/(b*x^n)]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:542
  public void test0105() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^2/x^3, x]", //
        "-1/6*b^2*e^2*n^2/(d^2*x)+11/12*b^2*e^4*n^2*Log[x]/d^4-5/6*b^2*e^4*n^2*Log[d+e*Sqrt[x]]/d^4-1/3*b*e*n*(a+b*Log[c*(d+e*Sqrt[x])^n])/(d*x^(3/2))+1/2*b*e^2*n*(a+b*Log[c*(d+e*Sqrt[x])^n])/(d^2*x)-1/2*(a+b*Log[c*(d+e*Sqrt[x])^n])^2/x^2-b*e^4*n*(a+b*Log[c*(d+e*Sqrt[x])^n])*Log[1-d/(d+e*Sqrt[x])]/d^4+b^2*e^4*n^2*PolyLog[2,d/(d+e*Sqrt[x])]/d^4+5/6*b^2*e^3*n^2/(d^3*Sqrt[x])-b*e^3*n*(a+b*Log[c*(d+e*Sqrt[x])^n])*(d+e*Sqrt[x])/(d^4*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:552
  public void test0106() {
    check( //
        "Integrate[x^3*(a+b*Log[c*(d+e/Sqrt[x])^n]), x]", //
        "-1/8*b*e^6*n*x/d^6+1/12*b*e^5*n*x^(3/2)/d^5-1/16*b*e^4*n*x^2/d^4+1/20*b*e^3*n*x^(5/2)/d^3-1/24*b*e^2*n*x^3/d^2+1/28*b*e*n*x^(7/2)/d-1/8*b*e^8*n*Log[x]/d^8-1/4*b*e^8*n*Log[d+e/Sqrt[x]]/d^8+1/4*x^4*(a+b*Log[c*(d+e/Sqrt[x])^n])+1/4*b*e^7*n*Sqrt[x]/d^7");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:568
  public void test0107() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/Sqrt[x])^n])^3, x]", //
        "3*b*e^2*n*Log[1-d/(d+e/Sqrt[x])]*(a+b*Log[c*(d+e/Sqrt[x])^n])^2/d^2+x*(a+b*Log[c*(d+e/Sqrt[x])^n])^3-6*b^2*e^2*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*Log[-e/(d*Sqrt[x])]/d^2-6*b^2*e^2*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*PolyLog[2,d/(d+e/Sqrt[x])]/d^2-6*b^3*e^2*n^3*PolyLog[2,1+e/(d*Sqrt[x])]/d^2-6*b^3*e^2*n^3*PolyLog[3,d/(d+e/Sqrt[x])]/d^2+3*b*e*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])*Sqrt[x]/d^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:588
  public void test0108() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])^2/x, x]", //
        "3*(a+b*Log[c*(d+e*x^(1/3))^n])^2*Log[-e*x^(1/3)/d]+6*b*n*(a+b*Log[c*(d+e*x^(1/3))^n])*PolyLog[2,1+e*x^(1/3)/d]-6*b^2*n^2*PolyLog[3,1+e*x^(1/3)/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:596
  public void test0109() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])^3/x^2, x]", //
        "-3*b^2*e^2*n^2*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])/(d^3*x^(1/3))-3*b^2*e^3*n^2*Log[1-d/(d+e*x^(1/3))]*(a+b*Log[c*(d+e*x^(1/3))^n])/d^3-3/2*b*e*n*(a+b*Log[c*(d+e*x^(1/3))^n])^2/(d*x^(2/3))+3*b*e^2*n*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^2/(d^3*x^(1/3))+3*b*e^3*n*Log[1-d/(d+e*x^(1/3))]*(a+b*Log[c*(d+e*x^(1/3))^n])^2/d^3-(a+b*Log[c*(d+e*x^(1/3))^n])^3/x-6*b^2*e^3*n^2*(a+b*Log[c*(d+e*x^(1/3))^n])*Log[-e*x^(1/3)/d]/d^3+b^3*e^3*n^3*Log[x]/d^3+3*b^3*e^3*n^3*PolyLog[2,d/(d+e*x^(1/3))]/d^3-6*b^2*e^3*n^2*(a+b*Log[c*(d+e*x^(1/3))^n])*PolyLog[2,d/(d+e*x^(1/3))]/d^3-6*b^3*e^3*n^3*PolyLog[2,1+e*x^(1/3)/d]/d^3-6*b^3*e^3*n^3*PolyLog[3,d/(d+e*x^(1/3))]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:606
  public void test0110() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(2/3))^n])/x^3, x]", //
        "-1/4*b*e*n/(d*x^(4/3))+1/2*b*e^2*n/(d^2*x^(2/3))-1/2*b*e^3*n*Log[d+e*x^(2/3)]/d^3+1/2*(-a-b*Log[c*(d+e*x^(2/3))^n])/x^2+1/3*b*e^3*n*Log[x]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:628
  public void test0111() {
    check( //
        "Integrate[x^3*(a+b*Log[c*(d+e/x^(1/3))^n]), x]", //
        "1/4*b*e^11*n*x^(1/3)/d^11-1/8*b*e^10*n*x^(2/3)/d^10+1/12*b*e^9*n*x/d^9-1/16*b*e^8*n*x^(4/3)/d^8+1/20*b*e^7*n*x^(5/3)/d^7-1/24*b*e^6*n*x^2/d^6+1/28*b*e^5*n*x^(7/3)/d^5-1/32*b*e^4*n*x^(8/3)/d^4+1/36*b*e^3*n*x^3/d^3-1/40*b*e^2*n*x^(10/3)/d^2+1/44*b*e*n*x^(11/3)/d-1/4*b*e^12*n*Log[d+e/x^(1/3)]/d^12+1/4*x^4*(a+b*Log[c*(d+e/x^(1/3))^n])-1/12*b*e^12*n*Log[x]/d^12");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:644
  public void test0112() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(1/3))^n])^3/x, x]", //
        "-3*(a+b*Log[c*(d+e/x^(1/3))^n])^3*Log[-e/(d*x^(1/3))]-9*b*n*(a+b*Log[c*(d+e/x^(1/3))^n])^2*PolyLog[2,1+e/(d*x^(1/3))]+18*b^2*n^2*(a+b*Log[c*(d+e/x^(1/3))^n])*PolyLog[3,1+e/(d*x^(1/3))]-18*b^3*n^3*PolyLog[4,1+e/(d*x^(1/3))]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:772
  public void test0113() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x^2)^p])/(h*x)^(3/2), x]", //
        "-2*b*e^(1/4)*f*p*ArcTan[1-e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(1/4)*h^(3/2))-2*b*d^(1/4)*g*p*ArcTan[1-e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(e^(1/4)*h^(3/2))+2*b*e^(1/4)*f*p*ArcTan[1+e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(1/4)*h^(3/2))+2*b*d^(1/4)*g*p*ArcTan[1+e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(e^(1/4)*h^(3/2))+b*e^(1/4)*f*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]-d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(1/4)*h^(3/2))-b*d^(1/4)*g*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]-d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(e^(1/4)*h^(3/2))-b*e^(1/4)*f*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]+d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(1/4)*h^(3/2))+b*d^(1/4)*g*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]+d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(e^(1/4)*h^(3/2))-2*f*(a+b*Log[c*(d+e*x^2)^p])/(h*Sqrt[h*x])+2*a*g*Sqrt[h*x]/h^2-8*b*g*p*Sqrt[h*x]/h^2+2*b*g*Log[c*(d+e*x^2)^p]*Sqrt[h*x]/h^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:794
  public void test0114() {
    check( //
        "Integrate[Log[f*x^p]*(a+b*Log[c*(d+e*x^m)^n])/x, x]", //
        "1/2*Log[f*x^p]^2*(a+b*Log[c*(d+e*x^m)^n])/p-1/2*b*n*Log[f*x^p]^2*Log[1+e*x^m/d]/p-b*n*Log[f*x^p]*PolyLog[2,-e*x^m/d]/m+b*n*p*PolyLog[3,-e*x^m/d]/m^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:815
  public void test0115() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/(f+g*x))^p])^4, x]", //
        "-4*b*e*p*Log[-e/(d*(f+g*x))]*(a+b*Log[c*(d+e/(f+g*x))^p])^3/(d*g)+(e+d*(f+g*x))*(a+b*Log[c*(d+e/(f+g*x))^p])^4/(d*g)-12*b^2*e*p^2*(a+b*Log[c*(d+e/(f+g*x))^p])^2*PolyLog[2,1+e/(d*(f+g*x))]/(d*g)+24*b^3*e*p^3*(a+b*Log[c*(d+e/(f+g*x))^p])*PolyLog[3,1+e/(d*(f+g*x))]/(d*g)-24*b^4*e*p^4*PolyLog[4,1+e/(d*(f+g*x))]/(d*g)");
  }

  // 3.5 Logarithm functions.input:18
  public void test0116() {
    check( //
        "Integrate[Log[c*x^n]*(a*x^m+b*Log[c*x^n]^2)^3/x, x]", //
        "-360*a*b^2*n^5*x^m/m^6-9/8*a^2*b*n^3*x^(2*m)/m^4-1/9*a^3*n*x^(3*m)/m^2+360*a*b^2*n^4*x^m*Log[c*x^n]/m^5+9/4*a^2*b*n^2*x^(2*m)*Log[c*x^n]/m^3+1/3*a^3*x^(3*m)*Log[c*x^n]/m-180*a*b^2*n^3*x^m*Log[c*x^n]^2/m^4-9/4*a^2*b*n*x^(2*m)*Log[c*x^n]^2/m^2+60*a*b^2*n^2*x^m*Log[c*x^n]^3/m^3+3/2*a^2*b*x^(2*m)*Log[c*x^n]^3/m-15*a*b^2*n*x^m*Log[c*x^n]^4/m^2+3*a*b^2*x^m*Log[c*x^n]^5/m+1/8*b^3*Log[c*x^n]^8/n");
  }

  // 3.5 Logarithm functions.input:31
  public void test0117() {
    check( //
        "Integrate[(a*m*x^m+b*n*q*Log[c*x^n]^(-1+q))/(x*(a*x^m+b*Log[c*x^n]^q)), x]", //
        "Log[a*x^m+b*Log[c*x^n]^q]");
  }

  // 3.5 Logarithm functions.input:39
  public void test0118() {
    check( //
        "Integrate[(a*x^3+2*b*n*x^2*Log[c*x^n])/(a*x^2+b*x*Log[c*x^n]^2)^3, x]", //
        "(-1/2)/(a*x+b*Log[c*x^n]^2)^2");
  }

  // 3.5 Logarithm functions.input:70
  public void test0119() {
    check( //
        "Integrate[x*(a+b*Log[c*Log[d*x^n]^p]), x]", //
        "-1/2*b*p*x^2*ExpIntegralEi[2*Log[d*x^n]/n]/(d*x^n)^(2/n)+1/2*x^2*(a+b*Log[c*Log[d*x^n]^p])");
  }

  // 3.5 Logarithm functions.input:78
  public void test0120() {
    check( //
        "Integrate[Log[c*Log[d*x^n]^p], x]", //
        "-p*x*ExpIntegralEi[Log[d*x^n]/n]/(d*x^n)^(1/n)+x*Log[c*Log[d*x^n]^p]");
  }

  // 3.5 Logarithm functions.input:91
  public void test0121() {
    check( //
        "Integrate[Log[d*(b*x+c*x^2)^n]/x^2, x]", //
        "-n/x+c*n*Log[x]/b-c*n*Log[b+c*x]/b-Log[d*(b*x+c*x^2)^n]/x");
  }

  // 3.5 Logarithm functions.input:100
  public void test0122() {
    check( //
        "Integrate[Log[d*(a+b*x+c*x^2)^n], x]", //
        "-2*n*x+1/2*b*n*Log[a+b*x+c*x^2]/c+x*Log[d*(a+b*x+c*x^2)^n]+n*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]*Sqrt[b^2-4*a*c]/c");
  }

  // 3.5 Logarithm functions.input:153
  public void test0123() {
    check( //
        "Integrate[Log[a+E^x*b], x]", //
        "x*Log[a+E^x*b]-x*Log[1+E^x*b/a]-PolyLog[2,-E^x*b/a]");
  }

  // 3.5 Logarithm functions.input:163
  public void test0124() {
    check( //
        "Integrate[Log[d+e*(f^(c*(a+b*x)))^n], x]", //
        "x*Log[d+e*(f^(c*(a+b*x)))^n]-x*Log[1+e*(f^(c*(a+b*x)))^n/d]-PolyLog[2,-e*(f^(c*(a+b*x)))^n/d]/(b*c*n*Log[f])");
  }

  // 3.5 Logarithm functions.input:183
  public void test0125() {
    check( //
        "Integrate[(1+Log[x])/(x*(3+2*Log[x])^2), x]", //
        "1/4/(3+2*Log[x])+1/4*Log[3+2*Log[x]]");
  }

  // 3.5 Logarithm functions.input:191
  public void test0126() {
    check( //
        "Integrate[(b*Log[a*x^n]^m)^p/x, x]", //
        "Log[a*x^n]*(b*Log[a*x^n]^m)^p/(n*(1+m*p))");
  }

  // 3.5 Logarithm functions.input:203
  public void test0127() {
    check( //
        "Integrate[Cos[x]*Log[x]+Sin[x]/x, x]", //
        "Log[x]*Sin[x]");
  }

  // 3.5 Logarithm functions.input:221
  public void test0128() {
    check( //
        "Integrate[Log[a*Csc[x]], x]", //
        "-1/2*I*x^2+x*Log[1-E^(2*I*x)]+x*Log[a*Csc[x]]-1/2*I*PolyLog[2,E^(2*I*x)]");
  }

  // 3.5 Logarithm functions.input:334
  public void test0129() {
    check( //
        "Integrate[1/Sqrt[-Log[a*x^n]], x]", //
        "-x*Erf[Sqrt[-Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/((a*x^n)^(1/n)*Sqrt[n])");
  }

  // 3.5 Logarithm functions.input:342
  public void test0130() {
    check( //
        "Integrate[Log[x+Sqrt[x]], x]", //
        "-x-Log[1+Sqrt[x]]+x*Log[x+Sqrt[x]]+Sqrt[x]");
  }

  // 3.5 Logarithm functions.input:350
  public void test0131() {
    check( //
        "Integrate[Log[1-I*Sqrt[1-a*x]/Sqrt[1+a*x]]/(1-a^2*x^2), x]", //
        "PolyLog[2,I*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 3.5 Logarithm functions.input:378
  public void test0132() {
    check( //
        "Integrate[(A+B*Log[x])/Sqrt[a+b*Log[x]], x]", //
        "1/2*(2*A*b-(2*a+b)*B)*Erfi[Sqrt[a+b*Log[x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*b^(3/2))+B*x*Sqrt[a+b*Log[x]]/b");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:30
  public void test0133() {
    check( //
        "Integrate[Log[c*x]^3/x^3, x]", //
        "(-3/8)/x^2-3/4*Log[c*x]/x^2-3/4*Log[c*x]^2/x^2-1/2*Log[c*x]^3/x^2");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:40
  public void test0134() {
    check( //
        "Integrate[x^3/Log[c*x]^2, x]", //
        "4*ExpIntegralEi[4*Log[c*x]]/c^4-x^4/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:48
  public void test0135() {
    check( //
        "Integrate[x^2/Log[c*x]^3, x]", //
        "9/2*ExpIntegralEi[3*Log[c*x]]/c^3-1/2*x^3/Log[c*x]^2-3/2*x^3/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:68
  public void test0136() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2, x]", //
        "-2*a*b*n*x+2*b^2*n^2*x-2*b^2*n*x*Log[c*x^n]+x*(a+b*Log[c*x^n])^2");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:132
  public void test0137() {
    check( //
        "Integrate[(d*x)^(1/2)/(a+b*Log[c*x^n])^2, x]", //
        "3/2*(d*x)^(3/2)*ExpIntegralEi[3/2*(a+b*Log[c*x^n])/(b*n)]/(E^(3/2*a/(b*n))*b^2*d*n^2*(c*x^n)^(3/2/n))-(d*x)^(3/2)/(b*d*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:144
  public void test0138() {
    check( //
        "Integrate[Sqrt[Log[a*x^n]], x]", //
        "-1/2*x*Erfi[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]*Sqrt[n]/(a*x^n)^(1/n)+x*Sqrt[Log[a*x^n]]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:162
  public void test0139() {
    check( //
        "Integrate[1/(x^2*Sqrt[Log[a*x^n]]), x]", //
        "(a*x^n)^(1/n)*Erf[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(x*Sqrt[n])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:170
  public void test0140() {
    check( //
        "Integrate[1/(x^3*Log[a*x^n]^(3/2)), x]", //
        "-2*(a*x^n)^(2/n)*Erf[Sqrt[2]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[2*Pi]/(n^(3/2)*x^2)+(-2)/(n*x^2*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:180
  public void test0141() {
    check( //
        "Integrate[(d*x)^m*(a+a*(1+m)*Log[c*x^n]/n), x]", //
        "a*(d*x)^(1+m)*Log[c*x^n]/(d*n)");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:196
  public void test0142() {
    check( //
        "Integrate[x^m/Log[a*x^n]^(3/2), x]", //
        "2*x^(1+m)*Erfi[Sqrt[1+m]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]*Sqrt[1+m]/(n^(3/2)*(a*x^n)^((1+m)/n))-2*x^(1+m)/(n*Sqrt[Log[a*x^n]])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:50
  public void test0143() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^3*(d+e*x)), x]", //
        "-1/4*b*n/(d*x^2)+b*e*n/(d^2*x)+1/2*(-a-b*Log[c*x^n])/(d*x^2)+e*(a+b*Log[c*x^n])/(d^2*x)-e^2*Log[1+d/(e*x)]*(a+b*Log[c*x^n])/d^3+b*e^2*n*PolyLog[2,-d/(e*x)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:59
  public void test0144() {
    check( //
        "Integrate[x^3*(a+b*Log[c*x^n])/(d+e*x)^3, x]", //
        "-3*b*n*x/e^3+1/2*(6*a+5*b*n)*x/e^3+3*b*x*Log[c*x^n]/e^3-1/2*x^3*(a+b*Log[c*x^n])/(e*(d+e*x)^2)-1/2*x^2*(3*a+b*n+3*b*Log[c*x^n])/(e^2*(d+e*x))-1/2*d*(6*a+5*b*n+6*b*Log[c*x^n])*Log[1+e*x/d]/e^4-3*b*d*n*PolyLog[2,-e*x/d]/e^4");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:67
  public void test0145() {
    check( //
        "Integrate[x^4*(a+b*Log[c*x^n])/(d+e*x)^4, x]", //
        "-4*b*n*x/e^4+1/3*(12*a+13*b*n)*x/e^4+4*b*x*Log[c*x^n]/e^4-1/3*x^4*(a+b*Log[c*x^n])/(e*(d+e*x)^3)-1/6*x^3*(4*a+b*n+4*b*Log[c*x^n])/(e^2*(d+e*x)^2)-1/6*x^2*(12*a+7*b*n+12*b*Log[c*x^n])/(e^3*(d+e*x))-1/3*d*(12*a+13*b*n+12*b*Log[c*x^n])*Log[1+e*x/d]/e^5-4*b*d*n*PolyLog[2,-e*x/d]/e^5");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:83
  public void test0146() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d+e*x)^7, x]", //
        "1/30*b*n/(d*e*(d+e*x)^5)+1/24*b*n/(d^2*e*(d+e*x)^4)+1/18*b*n/(d^3*e*(d+e*x)^3)+1/12*b*n/(d^4*e*(d+e*x)^2)+1/6*b*n/(d^5*e*(d+e*x))+1/6*b*n*Log[x]/(d^6*e)+1/6*(-a-b*Log[c*x^n])/(e*(d+e*x)^6)-1/6*b*n*Log[d+e*x]/(d^6*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:95
  public void test0147() {
    check( //
        "Integrate[(d+e*x)*(a+b*Log[c*x^n])^2, x]", //
        "-2*a*b*d*n*x+2*b^2*d*n^2*x+1/4*b^2*e*n^2*x^2-2*b^2*d*n*x*Log[c*x^n]-1/2*b*e*n*x^2*(a+b*Log[c*x^n])+d*x*(a+b*Log[c*x^n])^2+1/2*e*x^2*(a+b*Log[c*x^n])^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:103
  public void test0148() {
    check( //
        "Integrate[(d+e*x)^2*(a+b*Log[c*x^n])^2, x]", //
        "2*b^2*d^2*n^2*x+1/2*b^2*d*e*n^2*x^2+2/27*b^2*e^2*n^2*x^3+1/3*b^2*d^3*n^2*Log[x]^2/e-2*b*d^2*n*x*(a+b*Log[c*x^n])-b*d*e*n*x^2*(a+b*Log[c*x^n])-2/9*b*e^2*n*x^3*(a+b*Log[c*x^n])-2/3*b*d^3*n*Log[x]*(a+b*Log[c*x^n])/e+1/3*(d+e*x)^3*(a+b*Log[c*x^n])^2/e");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:113
  public void test0149() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^2/(d+e*x), x]", //
        "-2*a*b*n*x/e+2*b^2*n^2*x/e-2*b^2*n*x*Log[c*x^n]/e+x*(a+b*Log[c*x^n])^2/e-d*(a+b*Log[c*x^n])^2*Log[1+e*x/d]/e^2-2*b*d*n*(a+b*Log[c*x^n])*PolyLog[2,-e*x/d]/e^2+2*b^2*d*n^2*PolyLog[3,-e*x/d]/e^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:121
  public void test0150() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^2/(d+e*x)^2, x]", //
        "-x*(a+b*Log[c*x^n])^2/(e*(d+e*x))+2*b*n*(a+b*Log[c*x^n])*Log[1+e*x/d]/e^2+(a+b*Log[c*x^n])^2*Log[1+e*x/d]/e^2+2*b^2*n^2*PolyLog[2,-e*x/d]/e^2+2*b*n*(a+b*Log[c*x^n])*PolyLog[2,-e*x/d]/e^2-2*b^2*n^2*PolyLog[3,-e*x/d]/e^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:136
  public void test0151() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(d+e*x)^4, x]", //
        "-1/3*b^2*n^2/(d^2*e*(d+e*x))-1/3*b^2*n^2*Log[x]/(d^3*e)+1/3*b*n*(a+b*Log[c*x^n])/(d*e*(d+e*x)^2)-2/3*b*n*x*(a+b*Log[c*x^n])/(d^3*(d+e*x))-2/3*b*n*Log[1+d/(e*x)]*(a+b*Log[c*x^n])/(d^3*e)-1/3*(a+b*Log[c*x^n])^2/(e*(d+e*x)^3)+b^2*n^2*Log[d+e*x]/(d^3*e)+2/3*b^2*n^2*PolyLog[2,-d/(e*x)]/(d^3*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:227
  public void test0152() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*Log[c*x^n])/x, x]", //
        "-1/4*b*e*n*x^2+1/2*e*x^2*(a+b*Log[c*x^n])+1/2*d*(a+b*Log[c*x^n])^2/(b*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:252
  public void test0153() {
    check( //
        "Integrate[(d+e*x^2)^3*(a+b*Log[c*x^n])/x, x]", //
        "-3/4*b*d^2*e*n*x^2-3/16*b*d*e^2*n*x^4-1/36*b*e^3*n*x^6-1/2*b*d^3*n*Log[x]^2+3/2*d^2*e*x^2*(a+b*Log[c*x^n])+3/4*d*e^2*x^4*(a+b*Log[c*x^n])+1/6*e^3*x^6*(a+b*Log[c*x^n])+d^3*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:260
  public void test0154() {
    check( //
        "Integrate[(d+e*x^2)^3*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d^3*n/x^5-1/3*b*d^2*e*n/x^3-3*b*d*e^2*n/x-b*e^3*n*x-1/5*d^3*(a+b*Log[c*x^n])/x^5-d^2*e*(a+b*Log[c*x^n])/x^3-3*d*e^2*(a+b*Log[c*x^n])/x+e^3*x*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:270
  public void test0155() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^5*(d+e*x^2)), x]", //
        "-1/16*b*n/(d*x^4)+1/4*b*e*n/(d^2*x^2)+1/4*(-a-b*Log[c*x^n])/(d*x^4)+1/2*e*(a+b*Log[c*x^n])/(d^2*x^2)-1/2*e^2*Log[1+d/(e*x^2)]*(a+b*Log[c*x^n])/d^3+1/4*b*e^2*n*PolyLog[2,-d/(e*x^2)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:278
  public void test0156() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x^2)^2, x]", //
        "1/2*x^2*(a+b*Log[c*x^n])/(d*(d+e*x^2))-1/4*b*n*Log[d+e*x^2]/(d*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:426
  public void test0157() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/((d+e/x)*x), x]", //
        "(a+b*Log[c*x^n])*Log[1+d*x/e]/d+b*n*PolyLog[2,-d*x/e]/d");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:434
  public void test0158() {
    check( //
        "Integrate[(a+b*Log[c*x])/((d+e/x)*x), x]", //
        "(a+b*Log[c*x])*Log[1+d*x/e]/d+b*PolyLog[2,-d*x/e]/d");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:444
  public void test0159() {
    check( //
        "Integrate[Log[a/x^2]/(a*x-x^3), x]", //
        "1/2*PolyLog[2,1-a/x^2]/a");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:479
  public void test0160() {
    check( //
        "Integrate[x^2*(d+e*x^r)*(a+b*Log[c*x^n]), x]", //
        "-1/9*b*d*n*x^3-b*e*n*x^(3+r)/(3+r)^2+1/3*(d*x^3+3*e*x^(3+r)/(3+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:495
  public void test0161() {
    check( //
        "Integrate[(d+e*x^r)^2*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d^2*n/x^5-2*b*d*e*n*x^(-5+r)/(5-r)^2-b*e^2*n*x^(-5+2*r)/(5-2*r)^2-1/5*d^2*(a+b*Log[c*x^n])/x^5-2*d*e*x^(-5+r)*(a+b*Log[c*x^n])/(5-r)-e^2*x^(-5+2*r)*(a+b*Log[c*x^n])/(5-2*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:503
  public void test0162() {
    check( //
        "Integrate[x^4*(d+e*x^r)^3*(a+b*Log[c*x^n]), x]", //
        "-1/25*b*d^3*n*x^5-3*b*d^2*e*n*x^(5+r)/(5+r)^2-3*b*d*e^2*n*x^(5+2*r)/(5+2*r)^2-b*e^3*n*x^(5+3*r)/(5+3*r)^2+1/5*(d^3*x^5+15*d^2*e*x^(5+r)/(5+r)+15*d*e^2*x^(5+2*r)/(5+2*r)+5*e^3*x^(5+3*r)/(5+3*r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:559
  public void test0163() {
    check( //
        "Integrate[(f*x)^m*(d+e*x^r)*(a+b*Log[c*x^n]), x]", //
        "-b*e*n*x^(1+r)*(f*x)^m/(1+m+r)^2-b*d*n*(f*x)^(1+m)/(f*(1+m)^2)+e*x^(1+r)*(f*x)^m*(a+b*Log[c*x^n])/(1+m+r)+d*(f*x)^(1+m)*(a+b*Log[c*x^n])/(f*(1+m))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:21
  public void test0164() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[1+e*x]/x^3, x]", //
        "-3/4*b*e*n/x-1/4*b*e^2*n*Log[x]+1/4*b*e^2*n*Log[x]^2-1/2*e*(a+b*Log[c*x^n])/x-1/2*e^2*Log[x]*(a+b*Log[c*x^n])+1/4*b*e^2*n*Log[1+e*x]-1/4*b*n*Log[1+e*x]/x^2+1/2*e^2*(a+b*Log[c*x^n])*Log[1+e*x]-1/2*(a+b*Log[c*x^n])*Log[1+e*x]/x^2+1/2*b*e^2*n*PolyLog[2,-e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:29
  public void test0165() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[1+e*x]/x^3, x]", //
        "-7/4*b^2*e*n^2/x-1/4*b^2*e^2*n^2*Log[x]-3/2*b*e*n*(a+b*Log[c*x^n])/x+1/2*b*e^2*n*Log[1+1/(e*x)]*(a+b*Log[c*x^n])-1/2*e*(a+b*Log[c*x^n])^2/x+1/2*e^2*Log[1+1/(e*x)]*(a+b*Log[c*x^n])^2+1/4*b^2*e^2*n^2*Log[1+e*x]-1/4*b^2*n^2*Log[1+e*x]/x^2-1/2*b*n*(a+b*Log[c*x^n])*Log[1+e*x]/x^2-1/2*(a+b*Log[c*x^n])^2*Log[1+e*x]/x^2-1/2*b^2*e^2*n^2*PolyLog[2,(-1)/(e*x)]-b*e^2*n*(a+b*Log[c*x^n])*PolyLog[2,(-1)/(e*x)]-b^2*e^2*n^2*PolyLog[3,(-1)/(e*x)]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:73
  public void test0166() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(1/d+f*Sqrt[x])]/x, x]", //
        "-2*(a+b*Log[c*x^n])^2*PolyLog[2,-d*f*Sqrt[x]]+8*b*n*(a+b*Log[c*x^n])*PolyLog[3,-d*f*Sqrt[x]]-16*b^2*n^2*PolyLog[4,-d*f*Sqrt[x]]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:83
  public void test0167() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^4*Log[d*(1/d+f*x^m)]/x, x]", //
        "-(a+b*Log[c*x^n])^4*PolyLog[2,-d*f*x^m]/m+4*b*n*(a+b*Log[c*x^n])^3*PolyLog[3,-d*f*x^m]/m^2-12*b^2*n^2*(a+b*Log[c*x^n])^2*PolyLog[4,-d*f*x^m]/m^3+24*b^3*n^3*(a+b*Log[c*x^n])*PolyLog[5,-d*f*x^m]/m^4-24*b^4*n^4*PolyLog[6,-d*f*x^m]/m^5");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:97
  public void test0168() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x, x]", //
        "1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/(b*n)-1/2*m*(a+b*Log[c*x^n])^2*Log[1+f*x/e]/(b*n)-m*(a+b*Log[c*x^n])*PolyLog[2,-f*x/e]+b*m*n*PolyLog[3,-f*x/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:105
  public void test0169() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x^2, x]", //
        "2*b^2*f*m*n^2*Log[x]/e-2*b*f*m*n*Log[1+e/(f*x)]*(a+b*Log[c*x^n])/e-f*m*Log[1+e/(f*x)]*(a+b*Log[c*x^n])^2/e-2*b^2*f*m*n^2*Log[e+f*x]/e-2*b^2*n^2*Log[d*(e+f*x)^m]/x-2*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x-(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x+2*b^2*f*m*n^2*PolyLog[2,-e/(f*x)]/e+2*b*f*m*n*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x)]/e+2*b^2*f*m*n^2*PolyLog[3,-e/(f*x)]/e");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:123
  public void test0170() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^4, x]", //
        "-8/9*b*f*m*n/(e*x)-2/9*b*f^(3/2)*m*n*ArcTan[x*Sqrt[f]/Sqrt[e]]/e^(3/2)-2/3*f*m*(a+b*Log[c*x^n])/(e*x)-2/3*f^(3/2)*m*ArcTan[x*Sqrt[f]/Sqrt[e]]*(a+b*Log[c*x^n])/e^(3/2)-1/9*b*n*Log[d*(e+f*x^2)^m]/x^3-1/3*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^3+1/3*I*b*f^(3/2)*m*n*PolyLog[2,-I*x*Sqrt[f]/Sqrt[e]]/e^(3/2)-1/3*I*b*f^(3/2)*m*n*PolyLog[2,I*x*Sqrt[f]/Sqrt[e]]/e^(3/2)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:157
  public void test0171() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*Sqrt[x])]/x, x]", //
        "1/4*(a+b*Log[c*x^n])^4*Log[d*(e+f*Sqrt[x])]/(b*n)-1/4*(a+b*Log[c*x^n])^4*Log[1+f*Sqrt[x]/e]/(b*n)-2*(a+b*Log[c*x^n])^3*PolyLog[2,-f*Sqrt[x]/e]+12*b*n*(a+b*Log[c*x^n])^2*PolyLog[3,-f*Sqrt[x]/e]-48*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[4,-f*Sqrt[x]/e]+96*b^3*n^3*PolyLog[5,-f*Sqrt[x]/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:170
  public void test0172() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x^m)^r]/x, x]", //
        "1/4*(a+b*Log[c*x^n])^4*Log[d*(e+f*x^m)^r]/(b*n)-1/4*r*(a+b*Log[c*x^n])^4*Log[1+f*x^m/e]/(b*n)-r*(a+b*Log[c*x^n])^3*PolyLog[2,-f*x^m/e]/m+3*b*n*r*(a+b*Log[c*x^n])^2*PolyLog[3,-f*x^m/e]/m^2-6*b^2*n^2*r*(a+b*Log[c*x^n])*PolyLog[4,-f*x^m/e]/m^3+6*b^3*n^3*r*PolyLog[5,-f*x^m/e]/m^4");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:185
  public void test0173() {
    check( //
        "Integrate[(g*x)^(-1-2*m)*(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^k], x]", //
        "-3/4*b*f*k*n*x^m/(e*g*m^2*(g*x)^(2*m))-1/4*b*f^2*k*n*x^(2*m)*Log[x]/(e^2*g*m*(g*x)^(2*m))+1/4*b*f^2*k*n*x^(2*m)*Log[x]^2/(e^2*g*(g*x)^(2*m))-1/2*f*k*x^m*(a+b*Log[c*x^n])/(e*g*m*(g*x)^(2*m))-1/2*f^2*k*x^(2*m)*Log[x]*(a+b*Log[c*x^n])/(e^2*g*(g*x)^(2*m))+1/4*b*f^2*k*n*x^(2*m)*Log[e+f*x^m]/(e^2*g*m^2*(g*x)^(2*m))-1/2*b*f^2*k*n*x^(2*m)*Log[-f*x^m/e]*Log[e+f*x^m]/(e^2*g*m^2*(g*x)^(2*m))+1/2*f^2*k*x^(2*m)*(a+b*Log[c*x^n])*Log[e+f*x^m]/(e^2*g*m*(g*x)^(2*m))-1/4*b*n*Log[d*(e+f*x^m)^k]/(g*m^2*(g*x)^(2*m))-1/2*(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^k]/(g*m*(g*x)^(2*m))-1/2*b*f^2*k*n*x^(2*m)*PolyLog[2,1+f*x^m/e]/(e^2*g*m^2*(g*x)^(2*m))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:254
  public void test0174() {
    check( //
        "Integrate[q*PolyLog[-1+k,e*x^q]/(b*n*x*(a+b*Log[c*x^n]))-PolyLog[k,e*x^q]/(x*(a+b*Log[c*x^n])^2), x]", //
        "PolyLog[k,e*x^q]/(b*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:291
  public void test0175() {
    check( //
        "Integrate[Log[c*(b*x^n)^p]^2/x^2, x]", //
        "-2*n^2*p^2/x-2*n*p*Log[c*(b*x^n)^p]/x-Log[c*(b*x^n)^p]^2/x");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:12
  public void test0176() {
    check( //
        "Integrate[(a*g+b*g*x)^4*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "1/5*B*(b*c-a*d)^4*g^4*n*x/d^4-1/10*B*(b*c-a*d)^3*g^4*n*(a+b*x)^2/(b*d^3)+1/15*B*(b*c-a*d)^2*g^4*n*(a+b*x)^3/(b*d^2)-1/20*B*(b*c-a*d)*g^4*n*(a+b*x)^4/(b*d)+1/5*g^4*(a+b*x)^5*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/b-1/5*B*(b*c-a*d)^5*g^4*n*Log[c+d*x]/(b*d^5)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:29
  public void test0177() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(a*g+b*g*x)^5, x]", //
        "2*B^2*d^3*n^2*(c+d*x)/((b*c-a*d)^4*g^5*(a+b*x))-3/4*b*B^2*d^2*n^2*(c+d*x)^2/((b*c-a*d)^4*g^5*(a+b*x)^2)+2/9*b^2*B^2*d*n^2*(c+d*x)^3/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/32*b^3*B^2*n^2*(c+d*x)^4/((b*c-a*d)^4*g^5*(a+b*x)^4)+2*B*d^3*n*(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^4*g^5*(a+b*x))-3/2*b*B*d^2*n*(c+d*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^4*g^5*(a+b*x)^2)+2/3*b^2*B*d*n*(c+d*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/8*b^3*B*n*(c+d*x)^4*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^4*g^5*(a+b*x)^4)+d^3*(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^4*g^5*(a+b*x))-3/2*b*d^2*(c+d*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^4*g^5*(a+b*x)^2)+b^2*d*(c+d*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/4*b^3*(c+d*x)^4*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^4*g^5*(a+b*x)^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:72
  public void test0178() {
    check( //
        "Integrate[1/((c*g+d*g*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])), x]", //
        "b*(a+b*x)*ExpIntegralEi[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(B*n)]/(E^(A/(B*n))*B*(b*c-a*d)^2*g^3*n*(e*((a+b*x)/(c+d*x))^n)^(1/n)*(c+d*x))-d*(a+b*x)^2*ExpIntegralEi[2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(B*n)]/(E^(2*A/(B*n))*B*(b*c-a*d)^2*g^3*n*(e*((a+b*x)/(c+d*x))^n)^(2/n)*(c+d*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:89
  public void test0179() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(f+g*x), x]", //
        "-B*n*Log[-g*(a+b*x)/(b*f-a*g)]*Log[f+g*x]/g+(A+B*Log[e*((a+b*x)/(c+d*x))^n])*Log[f+g*x]/g+B*n*Log[-g*(c+d*x)/(d*f-c*g)]*Log[f+g*x]/g-B*n*PolyLog[2,b*(f+g*x)/(b*f-a*g)]/g+B*n*PolyLog[2,d*(f+g*x)/(d*f-c*g)]/g");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:127
  public void test0180() {
    check( //
        "Integrate[(a*g+b*g*x)^2*(A+B*Log[e*(a+b*x)/(c+d*x)]), x]", //
        "1/3*B*(b*c-a*d)^2*g^2*x/d^2-1/6*B*(b*c-a*d)*g^2*(a+b*x)^2/(b*d)+1/3*g^2*(a+b*x)^3*(A+B*Log[e*(a+b*x)/(c+d*x)])/b-1/3*B*(b*c-a*d)^3*g^2*Log[c+d*x]/(b*d^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:145
  public void test0181() {
    check( //
        "Integrate[Log[1+(-1)/(a+b*x)]/(a+b*x), x]", //
        "PolyLog[2,1/(a+b*x)]/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:210
  public void test0182() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/(a+b*x)^3, x]", //
        "2*B^2*d*n^2*(c+d*x)/((b*c-a*d)^2*(a+b*x))-1/4*b*B^2*n^2*(c+d*x)^2/((b*c-a*d)^2*(a+b*x)^2)+2*B*d*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^2*(a+b*x))-1/2*b*B*n*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^2*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^2*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^2*(a+b*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:218
  public void test0183() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/(a+b*x)^3, x]", //
        "6*B^3*d*n^3*(c+d*x)/((b*c-a*d)^2*(a+b*x))-3/8*b*B^3*n^3*(c+d*x)^2/((b*c-a*d)^2*(a+b*x)^2)+6*B^2*d*n^2*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^2*(a+b*x))-3/4*b*B^2*n^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^2*(a+b*x)^2)+3*B*d*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^2*(a+b*x))-3/4*b*B*n*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^2*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^2*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^2*(a+b*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:234
  public void test0184() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)/(a+b*x)])/(a*g+b*g*x), x]", //
        "-Log[(-b*c+a*d)/(d*(a+b*x))]*(A+B*Log[e*(c+d*x)/(a+b*x)])/(b*g)-B*PolyLog[2,1+(b*c-a*d)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:271
  public void test0185() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/(a*g+b*g*x)^4, x]", //
        "2/9*B/(b*g^4*(a+b*x)^3)-1/3*B*d/(b*(b*c-a*d)*g^4*(a+b*x)^2)+2/3*B*d^2/(b*(b*c-a*d)^2*g^4*(a+b*x))+2/3*B*d^3*Log[a+b*x]/(b*(b*c-a*d)^3*g^4)-2/3*B*d^3*Log[c+d*x]/(b*(b*c-a*d)^3*g^4)+1/3*(-A-B*Log[e*(c+d*x)^2/(a+b*x)^2])/(b*g^4*(a+b*x)^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:279
  public void test0186() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])^2/(a*g+b*g*x)^3, x]", //
        "-4*A*B*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))+8*B^2*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))-b*B^2*(c+d*x)^2/((b*c-a*d)^2*g^3*(a+b*x)^2)-4*B^2*d*(c+d*x)*Log[e*(c+d*x)^2/(a+b*x)^2]/((b*c-a*d)^2*g^3*(a+b*x))+b*B*(c+d*x)^2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/((b*c-a*d)^2*g^3*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])^2/((b*c-a*d)^2*g^3*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])^2/((b*c-a*d)^2*g^3*(a+b*x)^2)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:273
  public void test0187() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((a+b*x)*(c+d*x)), x]", //
        "1/4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^4/(B*(b*c-a*d)*n)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:281
  public void test0188() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^p/(a*c*f+(b*c+a*d)*f*x+b*d*f*x^2), x]", //
        "(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^(1+p)/(B*(b*c-a*d)*f*n*(1+p))");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:10
  public void test0189() {
    check( //
        "Integrate[(f+g/x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "-1/2*B*(b*c-a*d)*g^3*n/(a*c*x)+A*f^3*x-1/2*B*(b^2/a^2-d^2/c^2)*g^3*n*Log[x]+1/2*b^2*B*g^3*n*Log[a+b*x]/a^2-3*B*f^2*g*n*Log[x]*Log[1+b*x/a]+B*f^3*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/b-1/2*g^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/x^2+3*(b*c-a*d)*f*g^2*(a+b*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(a*(c+d*x)*(a-c*(a+b*x)/(c+d*x)))+3*f^2*g*Log[x]*(A+B*Log[e*((a+b*x)/(c+d*x))^n])-B*(b*c-a*d)*f^3*n*Log[c+d*x]/(b*d)-1/2*B*d^2*g^3*n*Log[c+d*x]/c^2+3*B*f^2*g*n*Log[x]*Log[1+d*x/c]+3*B*(b*c-a*d)*f*g^2*n*Log[a-c*(a+b*x)/(c+d*x)]/(a*c)-3*B*f^2*g*n*PolyLog[2,-b*x/a]+3*B*f^2*g*n*PolyLog[2,-d*x/c]");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:24
  public void test0190() {
    check( //
        "Integrate[(a+b*x)^2*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r], x]", //
        "-1/3*(b*c-a*d)^2*q*r*x/d^2+1/6*(b*c-a*d)*q*r*(a+b*x)^2/(b*d)-1/9*p*r*(a+b*x)^3/b-1/9*q*r*(a+b*x)^3/b+1/3*(b*c-a*d)^3*q*r*Log[c+d*x]/(b*d^3)+1/3*(a+b*x)^3*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/b");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:42
  public void test0191() {
    check( //
        "Integrate[(g+h*x)^4*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r], x]", //
        "-1/5*(b*g-a*h)^4*p*r*x/b^4-1/5*(d*g-c*h)^4*q*r*x/d^4-1/10*(b*g-a*h)^3*p*r*(g+h*x)^2/(b^3*h)-1/10*(d*g-c*h)^3*q*r*(g+h*x)^2/(d^3*h)-1/15*(b*g-a*h)^2*p*r*(g+h*x)^3/(b^2*h)-1/15*(d*g-c*h)^2*q*r*(g+h*x)^3/(d^2*h)-1/20*(b*g-a*h)*p*r*(g+h*x)^4/(b*h)-1/20*(d*g-c*h)*q*r*(g+h*x)^4/(d*h)-1/25*p*r*(g+h*x)^5/h-1/25*q*r*(g+h*x)^5/h-1/5*(b*g-a*h)^5*p*r*Log[a+b*x]/(b^5*h)-1/5*(d*g-c*h)^5*q*r*Log[c+d*x]/(d^5*h)+1/5*(g+h*x)^5*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/h");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:82
  public void test0192() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/x, x]", //
        "-p*r*Log[x]*Log[1+b*x/a]+Log[x]*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]-q*r*Log[x]*Log[1+d*x/c]-p*r*PolyLog[2,-b*x/a]-q*r*PolyLog[2,-d*x/c]");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:134
  public void test0193() {
    check( //
        "Integrate[Log[c*x^2/(b+a*x)^2]^3, x]", //
        "x*Log[c*x^2/(b+a*x)^2]^3+6*b*Log[c*x^2/(b+a*x)^2]^2*Log[b/(b+a*x)]/a+24*b*Log[c*x^2/(b+a*x)^2]*PolyLog[2,a*x/(b+a*x)]/a-48*b*PolyLog[3,a*x/(b+a*x)]/a");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:10
  public void test0194() {
    check( //
        "Integrate[Log[c*(d+e*x)]^4, x]", //
        "24*x-24*(d+e*x)*Log[c*(d+e*x)]/e+12*(d+e*x)*Log[c*(d+e*x)]^2/e-4*(d+e*x)*Log[c*(d+e*x)]^3/e+(d+e*x)*Log[c*(d+e*x)]^4/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:20
  public void test0195() {
    check( //
        "Integrate[Log[c*(d+e*x)]^(5/2), x]", //
        "-5/2*(d+e*x)*Log[c*(d+e*x)]^(3/2)/e+(d+e*x)*Log[c*(d+e*x)]^(5/2)/e-15/8*Erfi[Sqrt[Log[c*(d+e*x)]]]*Sqrt[Pi]/(c*e)+15/4*(d+e*x)*Sqrt[Log[c*(d+e*x)]]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:34
  public void test0196() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^4, x]", //
        "-24*a*b^3*n^3*x+24*b^4*n^4*x-24*b^4*n^3*(d+e*x)*Log[c*(d+e*x)^n]/e+12*b^2*n^2*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e-4*b*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e+(d+e*x)*(a+b*Log[c*(d+e*x)^n])^4/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:58
  public void test0197() {
    check( //
        "Integrate[(e+f*x)^(-1+p)/Log[d*(e+f*x)^p], x]", //
        "LogIntegral[d*(e+f*x)^p]/(d*f*p)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:78
  public void test0198() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^2/(f+g*x)^2, x]", //
        "(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/((e*f-d*g)*(f+g*x))-2*b*e*n*(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/(g*(e*f-d*g))-2*b^2*e*n^2*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/(g*(e*f-d*g))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:86
  public void test0199() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^3/(f+g*x)^2, x]", //
        "(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/((e*f-d*g)*(f+g*x))-3*b*e*n*(a+b*Log[c*(d+e*x)^n])^2*Log[e*(f+g*x)/(e*f-d*g)]/(g*(e*f-d*g))-6*b^2*e*n^2*(a+b*Log[c*(d+e*x)^n])*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/(g*(e*f-d*g))+6*b^3*e*n^3*PolyLog[3,-g*(d+e*x)/(e*f-d*g)]/(g*(e*f-d*g))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:94
  public void test0200() {
    check( //
        "Integrate[Log[a+b*x]^2, x]", //
        "2*x-2*(a+b*x)*Log[a+b*x]/b+(a+b*x)*Log[a+b*x]^2/b");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:120
  public void test0201() {
    check( //
        "Integrate[(f+g*x)^2/(a+b*Log[c*(d+e*x)^n]), x]", //
        "(e*f-d*g)^2*(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b*e^3*n*(c*(d+e*x)^n)^(1/n))+2*g*(e*f-d*g)*(d+e*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(2*a/(b*n))*b*e^3*n*(c*(d+e*x)^n)^(2/n))+g^2*(d+e*x)^3*ExpIntegralEi[3*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(3*a/(b*n))*b*e^3*n*(c*(d+e*x)^n)^(3/n))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:337
  public void test0202() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e*x)^n])/(f+g*x), x]", //
        "a*x/g-b*n*x/g+b*(d+e*x)*Log[c*(d+e*x)^n]/(e*g)-f*(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/g^2-b*f*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:367
  public void test0203() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e*x)^n])/(f+g*x^2)^2, x]", //
        "1/2*b*e^2*n*Log[d+e*x]/(g*(e^2*f+d^2*g))+1/2*(-a-b*Log[c*(d+e*x)^n])/(g*(f+g*x^2))-1/4*b*e^2*n*Log[f+g*x^2]/(g*(e^2*f+d^2*g))+1/2*b*d*e*n*ArcTan[x*Sqrt[g]/Sqrt[f]]/((e^2*f+d^2*g)*Sqrt[f]*Sqrt[g])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:389
  public void test0204() {
    check( //
        "Integrate[x^2*Log[c+d*x]/(a+b*x^3), x]", //
        "1/3*Log[-d*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*c-a^(1/3)*d)]*Log[c+d*x]/b+1/3*Log[-d*((-1)^(2/3)*a^(1/3)+b^(1/3)*x)/(b^(1/3)*c-(-1)^(2/3)*a^(1/3)*d)]*Log[c+d*x]/b+1/3*Log[(-1)^(1/3)*d*(a^(1/3)+(-1)^(2/3)*b^(1/3)*x)/(b^(1/3)*c+(-1)^(1/3)*a^(1/3)*d)]*Log[c+d*x]/b+1/3*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c-a^(1/3)*d)]/b+1/3*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c+(-1)^(1/3)*a^(1/3)*d)]/b+1/3*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c-(-1)^(2/3)*a^(1/3)*d)]/b");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:425
  public void test0205() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/((f+g/x)^3*x^3), x]", //
        "-1/2*b*e*n/(f*(d*f-e*g)*(g+f*x))+1/2*b*e^2*n*Log[d+e*x]/(f*(d*f-e*g)^2)+1/2*(-a-b*Log[c*(d+e*x)^n])/(f*(g+f*x)^2)-1/2*b*e^2*n*Log[g+f*x]/(f*(d*f-e*g)^2)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:466
  public void test0206() {
    check( //
        "Integrate[Log[c-a*(1-c)/(b*x^m)]/(x*(a+b*x^m)), x]", //
        "PolyLog[2,(1-c)*(b+a/x^m)/b]/(a*m)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:474
  public void test0207() {
    check( //
        "Integrate[Log[1-c*(a-b*x)/(a+b*x)]/(a^2-b^2*x^2), x]", //
        "1/2*PolyLog[2,c*(a-b*x)/(a+b*x)]/(a*b)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:582
  public void test0208() {
    check( //
        "Integrate[a+b*Log[c*(d*(e+f*x)^m)^n], x]", //
        "a*x-b*m*n*x+b*(e+f*x)*Log[c*(d*(e+f*x)^m)^n]/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:592
  public void test0209() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^m)^n])^(3/2), x]", //
        "2*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/(Sqrt[b]*Sqrt[m]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*m*n))*b^(3/2)*f*m^(3/2)*n^(3/2)*(c*(d*(e+f*x)^m)^n)^(1/(m*n)))-2*(e+f*x)/(b*f*m*n*Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:608
  public void test0210() {
    check( //
        "Integrate[a+b*Log[c*(d*(e+f*x)^p)^q], x]", //
        "a*x-b*p*q*x+b*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:616
  public void test0211() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^2, x]", //
        "-2*a*b*p*q*x+2*b^2*p^2*q^2*x-2*b^2*p*q*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f+(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:624
  public void test0212() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^3/(g+h*x)^2, x]", //
        "(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/((f*g-e*h)*(g+h*x))-3*b*f*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])^2*Log[f*(g+h*x)/(f*g-e*h)]/(h*(f*g-e*h))-6*b^2*f*p^2*q^2*(a+b*Log[c*(d*(e+f*x)^p)^q])*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))+6*b^3*f*p^3*q^3*PolyLog[3,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:634
  public void test0213() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^p)^q]), x]", //
        "(e+f*x)*ExpIntegralEi[(a+b*Log[c*(d*(e+f*x)^p)^q])/(b*p*q)]/(E^(a/(b*p*q))*b*f*p*q*(c*(d*(e+f*x)^p)^q)^(1/(p*q)))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:15
  public void test0214() {
    check( //
        "Integrate[x*Log[c*(a+b*x^2)^p], x]", //
        "-1/2*p*x^2+1/2*(a+b*x^2)*Log[c*(a+b*x^2)^p]/b");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:32
  public void test0215() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/x^3, x]", //
        "1/2*b^(2/3)*p*Log[a^(1/3)+b^(1/3)*x]/a^(2/3)-1/4*b^(2/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(2/3)-1/2*Log[c*(a+b*x^3)^p]/x^2-1/2*b^(2/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/a^(2/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:42
  public void test0216() {
    check( //
        "Integrate[x*Log[c*(a+b/x)^p], x]", //
        "1/2*b*p*x/a+1/2*x^2*Log[c*(a+b/x)^p]-1/2*b^2*p*Log[b+a*x]/a^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:50
  public void test0217() {
    check( //
        "Integrate[x^3*Log[c*(a+b/x^2)^p], x]", //
        "1/4*b*p*x^2/a+1/4*x^4*Log[c*(a+b/x^2)^p]-1/4*b^2*p*Log[b+a*x^2]/a^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:70
  public void test0218() {
    check( //
        "Integrate[Log[c*(a+b*Sqrt[x])^p]/x^4, x]", //
        "-1/15*b*p/(a*x^(5/2))+1/12*b^2*p/(a^2*x^2)-1/9*b^3*p/(a^3*x^(3/2))+1/6*b^4*p/(a^4*x)-1/6*b^6*p*Log[x]/a^6+1/3*b^6*p*Log[a+b*Sqrt[x]]/a^6-1/3*Log[c*(a+b*Sqrt[x])^p]/x^3-1/3*b^5*p/(a^5*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:102
  public void test0219() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]/x, x]", //
        "Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/n+p*PolyLog[2,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:127
  public void test0220() {
    check( //
        "Integrate[x^3*Log[c*(a+b*x^2)^p]^3, x]", //
        "3*a*p^3*x^2/b-3/16*p^3*(a+b*x^2)^2/b^2-3*a*p^2*(a+b*x^2)*Log[c*(a+b*x^2)^p]/b^2+3/8*p^2*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]/b^2+3/2*a*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/b^2-3/8*p*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]^2/b^2-1/2*a*(a+b*x^2)*Log[c*(a+b*x^2)^p]^3/b^2+1/4*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]^3/b^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:146
  public void test0221() {
    check( //
        "Integrate[x^3/Log[c*(a+b*x^2)^p]^2, x]", //
        "-1/2*a*(a+b*x^2)*ExpIntegralEi[Log[c*(a+b*x^2)^p]/p]/(b^2*p^2*(c*(a+b*x^2)^p)^(1/p))+(a+b*x^2)^2*ExpIntegralEi[2*Log[c*(a+b*x^2)^p]/p]/(b^2*p^2*(c*(a+b*x^2)^p)^(2/p))-1/2*x^2*(a+b*x^2)/(b*p*Log[c*(a+b*x^2)^p])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:164
  public void test0222() {
    check( //
        "Integrate[x^3/Log[c*(a+b*x^2)]^3, x]", //
        "ExpIntegralEi[2*Log[c*(a+b*x^2)]]/(b^2*c^2)-1/4*a*LogIntegral[c*(a+b*x^2)]/(b^2*c)-1/4*x^2*(a+b*x^2)/(b*Log[c*(a+b*x^2)]^2)-1/4*a*(a+b*x^2)/(b^2*Log[c*(a+b*x^2)])-1/2*x^2*(a+b*x^2)/(b*Log[c*(a+b*x^2)])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:193
  public void test0223() {
    check( //
        "Integrate[x^2/Log[c*(d+e*x^3)^p]^2, x]", //
        "1/3*(d+e*x^3)*ExpIntegralEi[Log[c*(d+e*x^3)^p]/p]/(e*p^2*(c*(d+e*x^3)^p)^(1/p))+1/3*(-d-e*x^3)/(e*p*Log[c*(d+e*x^3)^p])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:217
  public void test0224() {
    check( //
        "Integrate[Log[2+e*x^n]/x, x]", //
        "Log[2]*Log[x]-PolyLog[2,-1/2*e*x^n]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:231
  public void test0225() {
    check( //
        "Integrate[(d+e*x)*Log[c*(a+b*x)^p], x]", //
        "-1/2*(b*d-a*e)*p*x/b-1/4*p*(d+e*x)^2/e-1/2*(b*d-a*e)^2*p*Log[a+b*x]/(b^2*e)+1/2*(d+e*x)^2*Log[c*(a+b*x)^p]/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:247
  public void test0226() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p], x]", //
        "-3*p*x+a^(1/3)*p*Log[a^(1/3)+b^(1/3)*x]/b^(1/3)-1/2*a^(1/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(1/3)+x*Log[c*(a+b*x^3)^p]-a^(1/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/b^(1/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:257
  public void test0227() {
    check( //
        "Integrate[Log[c*(a+b/x)^p]/(d+e*x)^2, x]", //
        "-Log[c*(a+b/x)^p]/(e*(d+e*x))-p*Log[x]/(d*e)+a*p*Log[b+a*x]/(e*(a*d-b*e))-b*p*Log[d+e*x]/(d*(a*d-b*e))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:288
  public void test0228() {
    check( //
        "Integrate[Log[c*(a+b*x)^p]/(x*(d+e*x)), x]", //
        "Log[-b*x/a]*Log[c*(a+b*x)^p]/d-Log[c*(a+b*x)^p]*Log[b*(d+e*x)/(b*d-a*e)]/d-p*PolyLog[2,-e*(a+b*x)/(b*d-a*e)]/d+p*PolyLog[2,1+b*x/a]/d");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:413
  public void test0229() {
    check( //
        "Integrate[(f+g*x^2)*Log[c*(d+e*x^2)^p]/x^3, x]", //
        "e*f*p*Log[x]/d-1/2*e*f*p*Log[d+e*x^2]/d-1/2*f*Log[c*(d+e*x^2)^p]/x^2+1/2*g*Log[-e*x^2/d]*Log[c*(d+e*x^2)^p]+1/2*g*p*PolyLog[2,1+e*x^2/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:440
  public void test0230() {
    check( //
        "Integrate[x^3*Log[c*(d+e*x^2)^p]/(f+g*x^2), x]", //
        "-1/2*p*x^2/g+1/2*(d+e*x^2)*Log[c*(d+e*x^2)^p]/(e*g)-1/2*f*Log[c*(d+e*x^2)^p]*Log[e*(f+g*x^2)/(e*f-d*g)]/g^2-1/2*f*p*PolyLog[2,-g*(d+e*x^2)/(e*f-d*g)]/g^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:468
  public void test0231() {
    check( //
        "Integrate[(f+g/x^n)*Log[c*(d+e*x^n)^p]/x, x]", //
        "e*g*p*Log[x]/d-e*g*p*Log[d+e*x^n]/(d*n)-g*Log[c*(d+e*x^n)^p]/(n*x^n)+f*Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/n+f*p*PolyLog[2,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:478
  public void test0232() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]/(x*(f+g*x^n)), x]", //
        "Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/(f*n)-Log[c*(d+e*x^n)^p]*Log[e*(f+g*x^n)/(e*f-d*g)]/(f*n)-p*PolyLog[2,-g*(d+e*x^n)/(e*f-d*g)]/(f*n)+p*PolyLog[2,1+e*x^n/d]/(f*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:513
  public void test0233() {
    check( //
        "Integrate[Log[(a+b*x)/x]/(c+d*x), x]", //
        "Log[b+a/x]*Log[c+d*x]/d+Log[-d*x/c]*Log[c+d*x]/d-Log[-d*(a+b*x)/(b*c-a*d)]*Log[c+d*x]/d-PolyLog[2,b*(c+d*x)/(b*c-a*d)]/d+PolyLog[2,1+d*x/c]/d");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:535
  public void test0234() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])/x^3, x]", //
        "-1/6*b*e*n/(d*x^(3/2))+1/4*b*e^2*n/(d^2*x)-1/4*b*e^4*n*Log[x]/d^4+1/2*b*e^4*n*Log[d+e*Sqrt[x]]/d^4+1/2*(-a-b*Log[c*(d+e*Sqrt[x])^n])/x^2-1/2*b*e^3*n/(d^3*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:553
  public void test0235() {
    check( //
        "Integrate[x^2*(a+b*Log[c*(d+e/Sqrt[x])^n]), x]", //
        "-1/6*b*e^4*n*x/d^4+1/9*b*e^3*n*x^(3/2)/d^3-1/12*b*e^2*n*x^2/d^2+1/15*b*e*n*x^(5/2)/d-1/6*b*e^6*n*Log[x]/d^6-1/3*b*e^6*n*Log[d+e/Sqrt[x]]/d^6+1/3*x^3*(a+b*Log[c*(d+e/Sqrt[x])^n])+1/3*b*e^5*n*Sqrt[x]/d^5");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:561
  public void test0236() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e/Sqrt[x])^n])^2, x]", //
        "1/6*b^2*e^2*n^2*x/d^2+11/12*b^2*e^4*n^2*Log[x]/d^4+5/6*b^2*e^4*n^2*Log[d+e/Sqrt[x]]/d^4-1/2*b*e^2*n*x*(a+b*Log[c*(d+e/Sqrt[x])^n])/d^2+1/3*b*e*n*x^(3/2)*(a+b*Log[c*(d+e/Sqrt[x])^n])/d+b*e^4*n*Log[1-d/(d+e/Sqrt[x])]*(a+b*Log[c*(d+e/Sqrt[x])^n])/d^4+1/2*x^2*(a+b*Log[c*(d+e/Sqrt[x])^n])^2-b^2*e^4*n^2*PolyLog[2,d/(d+e/Sqrt[x])]/d^4-5/6*b^2*e^3*n^2*Sqrt[x]/d^3+b*e^3*n*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])*Sqrt[x]/d^4");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:569
  public void test0237() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/Sqrt[x])^n])^3/x, x]", //
        "-2*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*Log[-e/(d*Sqrt[x])]-6*b*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*PolyLog[2,1+e/(d*Sqrt[x])]+12*b^2*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*PolyLog[3,1+e/(d*Sqrt[x])]-12*b^3*n^3*PolyLog[4,1+e/(d*Sqrt[x])]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:581
  public void test0238() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])/x, x]", //
        "3*(a+b*Log[c*(d+e*x^(1/3))^n])*Log[-e*x^(1/3)/d]+3*b*n*PolyLog[2,1+e*x^(1/3)/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:589
  public void test0239() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])^2/x^2, x]", //
        "-b^2*e^2*n^2/(d^2*x^(1/3))+b^2*e^3*n^2*Log[d+e*x^(1/3)]/d^3-b*e*n*(a+b*Log[c*(d+e*x^(1/3))^n])/(d*x^(2/3))+2*b*e^2*n*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])/(d^3*x^(1/3))+2*b*e^3*n*Log[1-d/(d+e*x^(1/3))]*(a+b*Log[c*(d+e*x^(1/3))^n])/d^3-(a+b*Log[c*(d+e*x^(1/3))^n])^2/x-b^2*e^3*n^2*Log[x]/d^3-2*b^2*e^3*n^2*PolyLog[2,d/(d+e*x^(1/3))]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:629
  public void test0240() {
    check( //
        "Integrate[x^2*(a+b*Log[c*(d+e/x^(1/3))^n]), x]", //
        "-1/3*b*e^8*n*x^(1/3)/d^8+1/6*b*e^7*n*x^(2/3)/d^7-1/9*b*e^6*n*x/d^6+1/12*b*e^5*n*x^(4/3)/d^5-1/15*b*e^4*n*x^(5/3)/d^4+1/18*b*e^3*n*x^2/d^3-1/21*b*e^2*n*x^(7/3)/d^2+1/24*b*e*n*x^(8/3)/d+1/3*b*e^9*n*Log[d+e/x^(1/3)]/d^9+1/3*x^3*(a+b*Log[c*(d+e/x^(1/3))^n])+1/9*b*e^9*n*Log[x]/d^9");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:645
  public void test0241() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(1/3))^n])^3/x^2, x]", //
        "-9/4*b^3*d*n^3*(d+e/x^(1/3))^2/e^3+2/9*b^3*n^3*(d+e/x^(1/3))^3/e^3-18*a*b^2*d^2*n^2/(e^2*x^(1/3))+18*b^3*d^2*n^3/(e^2*x^(1/3))-18*b^3*d^2*n^2*(d+e/x^(1/3))*Log[c*(d+e/x^(1/3))^n]/e^3+9/2*b^2*d*n^2*(d+e/x^(1/3))^2*(a+b*Log[c*(d+e/x^(1/3))^n])/e^3-2/3*b^2*n^2*(d+e/x^(1/3))^3*(a+b*Log[c*(d+e/x^(1/3))^n])/e^3+9*b*d^2*n*(d+e/x^(1/3))*(a+b*Log[c*(d+e/x^(1/3))^n])^2/e^3-9/2*b*d*n*(d+e/x^(1/3))^2*(a+b*Log[c*(d+e/x^(1/3))^n])^2/e^3+b*n*(d+e/x^(1/3))^3*(a+b*Log[c*(d+e/x^(1/3))^n])^2/e^3-3*d^2*(d+e/x^(1/3))*(a+b*Log[c*(d+e/x^(1/3))^n])^3/e^3+3*d*(d+e/x^(1/3))^2*(a+b*Log[c*(d+e/x^(1/3))^n])^3/e^3-(d+e/x^(1/3))^3*(a+b*Log[c*(d+e/x^(1/3))^n])^3/e^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:773
  public void test0242() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x^2)^p])/(h*x)^(5/2), x]", //
        "-2/3*f*(a+b*Log[c*(d+e*x^2)^p])/(h*(h*x)^(3/2))-2/3*b*e^(3/4)*f*p*ArcTan[1-e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(3/4)*h^(5/2))-2*b*e^(1/4)*g*p*ArcTan[1-e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(1/4)*h^(5/2))+2/3*b*e^(3/4)*f*p*ArcTan[1+e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(3/4)*h^(5/2))+2*b*e^(1/4)*g*p*ArcTan[1+e^(1/4)*Sqrt[2]*Sqrt[h*x]/(d^(1/4)*Sqrt[h])]*Sqrt[2]/(d^(1/4)*h^(5/2))-1/3*b*e^(3/4)*f*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]-d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(3/4)*h^(5/2))+b*e^(1/4)*g*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]-d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(1/4)*h^(5/2))+1/3*b*e^(3/4)*f*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]+d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(3/4)*h^(5/2))-b*e^(1/4)*g*p*Log[Sqrt[d]*Sqrt[h]+x*Sqrt[e]*Sqrt[h]+d^(1/4)*e^(1/4)*Sqrt[2]*Sqrt[h*x]]*Sqrt[2]/(d^(1/4)*h^(5/2))-2*g*(a+b*Log[c*(d+e*x^2)^p])/(h^2*Sqrt[h*x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:795
  public void test0243() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^m)^n])/x, x]", //
        "Log[-e*x^m/d]*(a+b*Log[c*(d+e*x^m)^n])/m+b*n*PolyLog[2,1+e*x^m/d]/m");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:816
  public void test0244() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/(f+g*x))^p])^3, x]", //
        "-3*b*e*p*Log[-e/(d*(f+g*x))]*(a+b*Log[c*(d+e/(f+g*x))^p])^2/(d*g)+(e+d*(f+g*x))*(a+b*Log[c*(d+e/(f+g*x))^p])^3/(d*g)-6*b^2*e*p^2*(a+b*Log[c*(d+e/(f+g*x))^p])*PolyLog[2,1+e/(d*(f+g*x))]/(d*g)+6*b^3*e*p^3*PolyLog[3,1+e/(d*(f+g*x))]/(d*g)");
  }

  // 3.5 Logarithm functions.input:19
  public void test0245() {
    check( //
        "Integrate[Log[c*x^n]*(a*x^m+b*Log[c*x^n]^2)^2/x, x]", //
        "-12*a*b*n^3*x^m/m^4-1/4*a^2*n*x^(2*m)/m^2+12*a*b*n^2*x^m*Log[c*x^n]/m^3+1/2*a^2*x^(2*m)*Log[c*x^n]/m-6*a*b*n*x^m*Log[c*x^n]^2/m^2+2*a*b*x^m*Log[c*x^n]^3/m+1/6*b^2*Log[c*x^n]^6/n");
  }

  // 3.5 Logarithm functions.input:32
  public void test0246() {
    check( //
        "Integrate[(a*m*x^m+b*n*q*Log[c*x^n]^(-1+q))/(x*(a*x^m+b*Log[c*x^n]^q)^2), x]", //
        "(-1)/(a*x^m+b*Log[c*x^n]^q)");
  }

  // 3.5 Logarithm functions.input:61
  public void test0247() {
    check( //
        "Integrate[Log[2*x*(e*x+d*Sqrt[e]/Sqrt[-d])/(d+e*x^2)]/(d+e*x^2), x]", //
        "-1/2*PolyLog[2,1+2*x*Sqrt[e]*(Sqrt[-d]-x*Sqrt[e])/(d+e*x^2)]/(Sqrt[-d]*Sqrt[e])");
  }

  // 3.5 Logarithm functions.input:71
  public void test0248() {
    check( //
        "Integrate[a+b*Log[c*Log[d*x^n]^p], x]", //
        "a*x-b*p*x*ExpIntegralEi[Log[d*x^n]/n]/(d*x^n)^(1/n)+b*x*Log[c*Log[d*x^n]^p]");
  }

  // 3.5 Logarithm functions.input:92
  public void test0249() {
    check( //
        "Integrate[Log[d*(b*x+c*x^2)^n]/x^3, x]", //
        "-1/4*n/x^2-1/2*c*n/(b*x)-1/2*c^2*n*Log[x]/b^2+1/2*c^2*n*Log[b+c*x]/b^2-1/2*Log[d*(b*x+c*x^2)^n]/x^2");
  }

  // 3.5 Logarithm functions.input:101
  public void test0250() {
    check( //
        "Integrate[Log[d*(a+b*x+c*x^2)^n]/x, x]", //
        "Log[x]*Log[d*(a+b*x+c*x^2)^n]-n*Log[x]*Log[1+2*c*x/(b-Sqrt[b^2-4*a*c])]-n*Log[x]*Log[1+2*c*x/(b+Sqrt[b^2-4*a*c])]-n*PolyLog[2,-2*c*x/(b-Sqrt[b^2-4*a*c])]-n*PolyLog[2,-2*c*x/(b+Sqrt[b^2-4*a*c])]");
  }

  // 3.5 Logarithm functions.input:155
  public void test0251() {
    check( //
        "Integrate[x^3*Log[1+e*(f^(c*(a+b*x)))^n], x]", //
        "-x^3*PolyLog[2,-e*(f^(c*(a+b*x)))^n]/(b*c*n*Log[f])+3*x^2*PolyLog[3,-e*(f^(c*(a+b*x)))^n]/(b^2*c^2*n^2*Log[f]^2)-6*x*PolyLog[4,-e*(f^(c*(a+b*x)))^n]/(b^3*c^3*n^3*Log[f]^3)+6*PolyLog[5,-e*(f^(c*(a+b*x)))^n]/(b^4*c^4*n^4*Log[f]^4)");
  }

  // 3.5 Logarithm functions.input:165
  public void test0252() {
    check( //
        "Integrate[Log[Pi+b*(F^(e*(c+d*x)))^n], x]", //
        "x*Log[Pi]-PolyLog[2,-b*(F^(e*(c+d*x)))^n/Pi]/(d*e*n*Log[F])");
  }

  // 3.5 Logarithm functions.input:206
  public void test0253() {
    check( //
        "Integrate[Log[a*Sin[x]], x]", //
        "1/2*I*x^2-x*Log[1-E^(2*I*x)]+x*Log[a*Sin[x]]+1/2*I*PolyLog[2,E^(2*I*x)]");
  }

  // 3.5 Logarithm functions.input:231
  public void test0254() {
    check( //
        "Integrate[Tan[x]/Log[Cos[x]], x]", //
        "-Log[Log[Cos[x]]]");
  }

  // 3.5 Logarithm functions.input:239
  public void test0255() {
    check( //
        "Integrate[Csc[x]^2*Log[Sin[x]], x]", //
        "-x-Cot[x]-Cot[x]*Log[Sin[x]]");
  }

  // 3.5 Logarithm functions.input:309
  public void test0256() {
    check( //
        "Integrate[Log[1/(13+x)], x]", //
        "x+(13+x)*Log[1/(13+x)]");
  }

  // 3.5 Logarithm functions.input:319
  public void test0257() {
    check( //
        "Integrate[1/(a*x+b*x*Log[c*x^n]), x]", //
        "Log[a+b*Log[c*x^n]]/(b*n)");
  }

  // 3.5 Logarithm functions.input:327
  public void test0258() {
    check( //
        "Integrate[1/(x+x*Log[7*x]+x*Log[7*x]^2), x]", //
        "2*ArcTan[(1+2*Log[7*x])/Sqrt[3]]/Sqrt[3]");
  }

  // 3.5 Logarithm functions.input:343
  public void test0259() {
    check( //
        "Integrate[Log[-x/(1+x)], x]", //
        "x*Log[-x/(1+x)]-Log[1+x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:24
  public void test0260() {
    check( //
        "Integrate[x^3*Log[c*x]^3, x]", //
        "-3/128*x^4+3/32*x^4*Log[c*x]-3/16*x^4*Log[c*x]^2+1/4*x^4*Log[c*x]^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:42
  public void test0261() {
    check( //
        "Integrate[x/Log[c*x]^2, x]", //
        "2*ExpIntegralEi[2*Log[c*x]]/c^2-x^2/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:50
  public void test0262() {
    check( //
        "Integrate[1/Log[c*x]^3, x]", //
        "1/2*LogIntegral[c*x]/c-1/2*x/Log[c*x]^2-1/2*x/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:78
  public void test0263() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3/x^3, x]", //
        "-3/8*b^3*n^3/x^2-3/4*b^2*n^2*(a+b*Log[c*x^n])/x^2-3/4*b*n*(a+b*Log[c*x^n])^2/x^2-1/2*(a+b*Log[c*x^n])^3/x^2");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:96
  public void test0264() {
    check( //
        "Integrate[1/(x^3*(a+b*Log[c*x^n])^2), x]", //
        "-2*E^(2*a/(b*n))*(c*x^n)^(2/n)*ExpIntegralEi[-2*(a+b*Log[c*x^n])/(b*n)]/(b^2*n^2*x^2)+(-1)/(b*n*x^2*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:104
  public void test0265() {
    check( //
        "Integrate[1/(x^3*(a+b*Log[c*x^n])^3), x]", //
        "2*E^(2*a/(b*n))*(c*x^n)^(2/n)*ExpIntegralEi[-2*(a+b*Log[c*x^n])/(b*n)]/(b^3*n^3*x^2)+(-1/2)/(b*n*x^2*(a+b*Log[c*x^n])^2)+1/(b^2*n^2*x^2*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:126
  public void test0266() {
    check( //
        "Integrate[(d*x)^(1/2)/(a+b*Log[c*x^n]), x]", //
        "(d*x)^(3/2)*ExpIntegralEi[3/2*(a+b*Log[c*x^n])/(b*n)]/(E^(3/2*a/(b*n))*b*d*n*(c*x^n)^(3/2/n))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:134
  public void test0267() {
    check( //
        "Integrate[1/((d*x)^(3/2)*(a+b*Log[c*x^n])^2), x]", //
        "-1/2*E^(1/2*a/(b*n))*(c*x^n)^(1/2/n)*ExpIntegralEi[1/2*(-a-b*Log[c*x^n])/(b*n)]/(b^2*d*n^2*Sqrt[d*x])+(-1)/(b*d*n*(a+b*Log[c*x^n])*Sqrt[d*x])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:146
  public void test0268() {
    check( //
        "Integrate[Sqrt[Log[a*x^n]]/x^2, x]", //
        "1/2*(a*x^n)^(1/n)*Erf[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]*Sqrt[n]/x-Sqrt[Log[a*x^n]]/x");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:164
  public void test0269() {
    check( //
        "Integrate[x^3/Log[a*x^n]^(3/2), x]", //
        "4*x^4*Erfi[2*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(n^(3/2)*(a*x^n)^(4/n))-2*x^4/(n*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:172
  public void test0270() {
    check( //
        "Integrate[x^2/Log[a*x^n]^(5/2), x]", //
        "-2/3*x^3/(n*Log[a*x^n]^(3/2))+4*x^3*Erfi[Sqrt[3]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[3*Pi]/(n^(5/2)*(a*x^n)^(3/n))-4*x^3/(n^2*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:190
  public void test0271() {
    check( //
        "Integrate[(d*x)^(-1+n)/Log[c*x^n], x]", //
        "x^(1-n)*(d*x)^(-1+n)*LogIntegral[c*x^n]/(c*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:34
  public void test0272() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*Log[c*x^n])/x, x]", //
        "-3*b*d^2*e*n*x-3/4*b*d*e^2*n*x^2-1/9*b*e^3*n*x^3-1/2*b*d^3*n*Log[x]^2+3*d^2*e*x*(a+b*Log[c*x^n])+3/2*d*e^2*x^2*(a+b*Log[c*x^n])+1/3*e^3*x^3*(a+b*Log[c*x^n])+d^3*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:61
  public void test0273() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x)^3, x]", //
        "-1/2*b*n/(e^2*(d+e*x))+1/2*x^2*(a+b*Log[c*x^n])/(d*(d+e*x)^2)-1/2*b*n*Log[d+e*x]/(d*e^2)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:69
  public void test0274() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])/(d+e*x)^4, x]", //
        "1/6*b*d*n/(e^3*(d+e*x)^2)-2/3*b*n/(e^3*(d+e*x))+1/3*x^3*(a+b*Log[c*x^n])/(d*(d+e*x)^3)-1/3*b*n*Log[d+e*x]/(d*e^3)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:77
  public void test0275() {
    check( //
        "Integrate[x^6*(a+b*Log[c*x^n])/(d+e*x)^7, x]", //
        "-1/6*x^6*(a+b*Log[c*x^n])/(e*(d+e*x)^6)-1/30*x^5*(6*a+b*n+6*b*Log[c*x^n])/(e^2*(d+e*x)^5)-1/40*x^2*(20*a+19*b*n+20*b*Log[c*x^n])/(e^5*(d+e*x)^2)-1/20*x*(20*a+29*b*n+20*b*Log[c*x^n])/(e^6*(d+e*x))-1/120*x^4*(30*a+11*b*n+30*b*Log[c*x^n])/(e^3*(d+e*x)^4)-1/180*x^3*(60*a+37*b*n+60*b*Log[c*x^n])/(e^4*(d+e*x)^3)+1/20*(20*a+49*b*n+20*b*Log[c*x^n])*Log[1+e*x/d]/e^7+b*n*PolyLog[2,-e*x/d]/e^7");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:97
  public void test0276() {
    check( //
        "Integrate[(d+e*x)*(a+b*Log[c*x^n])^2/x^2, x]", //
        "-2*b^2*d*n^2/x-2*b*d*n*(a+b*Log[c*x^n])/x-d*(a+b*Log[c*x^n])^2/x+1/3*e*(a+b*Log[c*x^n])^3/(b*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:105
  public void test0277() {
    check( //
        "Integrate[(d+e*x)^2*(a+b*Log[c*x^n])^2/x^2, x]", //
        "-2*b^2*d^2*n^2/x-2*a*b*e^2*n*x+2*b^2*e^2*n^2*x-2*b^2*e^2*n*x*Log[c*x^n]-2*b*d^2*n*(a+b*Log[c*x^n])/x-d^2*(a+b*Log[c*x^n])^2/x+e^2*x*(a+b*Log[c*x^n])^2+2/3*d*e*(a+b*Log[c*x^n])^3/(b*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:115
  public void test0278() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x*(d+e*x)), x]", //
        "-Log[1+d/(e*x)]*(a+b*Log[c*x^n])^2/d+2*b*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x)]/d+2*b^2*n^2*PolyLog[3,-d/(e*x)]/d");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:123
  public void test0279() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x*(d+e*x)^2), x]", //
        "-e*x*(a+b*Log[c*x^n])^2/(d^2*(d+e*x))-Log[1+d/(e*x)]*(a+b*Log[c*x^n])^2/d^2+2*b*n*(a+b*Log[c*x^n])*Log[1+e*x/d]/d^2+2*b*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x)]/d^2+2*b^2*n^2*PolyLog[2,-e*x/d]/d^2+2*b^2*n^2*PolyLog[3,-d/(e*x)]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:147
  public void test0280() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3/(x*(d+e*x)^2), x]", //
        "-e*x*(a+b*Log[c*x^n])^3/(d^2*(d+e*x))-Log[1+d/(e*x)]*(a+b*Log[c*x^n])^3/d^2+3*b*n*(a+b*Log[c*x^n])^2*Log[1+e*x/d]/d^2+3*b*n*(a+b*Log[c*x^n])^2*PolyLog[2,-d/(e*x)]/d^2+6*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[2,-e*x/d]/d^2+6*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[3,-d/(e*x)]/d^2-6*b^3*n^3*PolyLog[3,-e*x/d]/d^2+6*b^3*n^3*PolyLog[4,-d/(e*x)]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:208
  public void test0281() {
    check( //
        "Integrate[(f*x)^m*(d+e*x)^2*(a+b*Log[c*x^n]), x]", //
        "-b*d^2*n*(f*x)^(1+m)/(f*(1+m)^2)-2*b*d*e*n*(f*x)^(2+m)/(f^2*(2+m)^2)-b*e^2*n*(f*x)^(3+m)/(f^3*(3+m)^2)+d^2*(f*x)^(1+m)*(a+b*Log[c*x^n])/(f*(1+m))+2*d*e*(f*x)^(2+m)*(a+b*Log[c*x^n])/(f^2*(2+m))+e^2*(f*x)^(3+m)*(a+b*Log[c*x^n])/(f^3*(3+m))");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:246
  public void test0282() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*Log[c*x^n])/x^4, x]", //
        "-1/9*b*d^2*n/x^3-2*b*d*e*n/x-b*e^2*n*x-1/3*d^2*(a+b*Log[c*x^n])/x^3-2*d*e*(a+b*Log[c*x^n])/x+e^2*x*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:262
  public void test0283() {
    check( //
        "Integrate[(d+e*x^2)^3*(a+b*Log[c*x^n])/x^10, x]", //
        "-1/81*b*d^3*n/x^9-3/49*b*d^2*e*n/x^7-3/25*b*d*e^2*n/x^5-1/9*b*e^3*n/x^3-1/9*d^3*(a+b*Log[c*x^n])/x^9-3/7*d^2*e*(a+b*Log[c*x^n])/x^7-3/5*d*e^2*(a+b*Log[c*x^n])/x^5-1/3*e^3*(a+b*Log[c*x^n])/x^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:280
  public void test0284() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^3*(d+e*x^2)^2), x]", //
        "-1/2*b*n/(d^2*x^2)+1/2*(a+b*Log[c*x^n])/(d*x^2*(d+e*x^2))+1/4*(-4*a+b*n-4*b*Log[c*x^n])/(d^2*x^2)+1/4*e*Log[1+d/(e*x^2)]*(4*a-b*n+4*b*Log[c*x^n])/d^3-1/2*b*e*n*PolyLog[2,-d/(e*x^2)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:288
  public void test0285() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x^2)^3, x]", //
        "1/8*b*n/(d*e*(d+e*x^2))+1/4*b*n*Log[x]/(d^2*e)+1/4*(-a-b*Log[c*x^n])/(e*(d+e*x^2)^2)-1/8*b*n*Log[d+e*x^2]/(d^2*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:296
  public void test0286() {
    check( //
        "Integrate[x*Log[c*x^2]/(1-c*x^2), x]", //
        "1/2*PolyLog[2,1-c*x^2]/c");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:362
  public void test0287() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^2*Sqrt[d+e*x^2]), x]", //
        "b*n*ArcTanh[x*Sqrt[e]/Sqrt[d+e*x^2]]*Sqrt[e]/d-b*n*Sqrt[d+e*x^2]/(d*x)-(a+b*Log[c*x^n])*Sqrt[d+e*x^2]/(d*x)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:394
  public void test0288() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^2*Sqrt[d-e*x]*Sqrt[d+e*x]), x]", //
        "-b*n*(d^2-e^2*x^2)/(d^2*x*Sqrt[d-e*x]*Sqrt[d+e*x])-(d^2-e^2*x^2)*(a+b*Log[c*x^n])/(d^2*x*Sqrt[d-e*x]*Sqrt[d+e*x])-b*e*n*ArcSin[e*x/d]*Sqrt[1-e^2*x^2/d^2]/(d*Sqrt[d-e*x]*Sqrt[d+e*x])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:450
  public void test0289() {
    check( //
        "Integrate[(f*x)^(-1+m)*(d+e*x^m)^3*(a+b*Log[c*x^n]), x]", //
        "-b*d^3*n*x*(f*x)^(-1+m)/m^2-3/4*b*d^2*e*n*x^(1+m)*(f*x)^(-1+m)/m^2-1/3*b*d*e^2*n*x^(1+2*m)*(f*x)^(-1+m)/m^2-1/16*b*e^3*n*x^(1+3*m)*(f*x)^(-1+m)/m^2-1/4*b*d^4*n*x^(1-m)*(f*x)^(-1+m)*Log[x]/(e*m)+1/4*x^(1-m)*(f*x)^(-1+m)*(d+e*x^m)^4*(a+b*Log[c*x^n])/(e*m)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:473
  public void test0290() {
    check( //
        "Integrate[x^3*(d+e*x^r)*(a+b*Log[c*x^n]), x]", //
        "-1/16*b*d*n*x^4-b*e*n*x^(4+r)/(4+r)^2+1/4*(d*x^4+4*e*x^(4+r)/(4+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:481
  public void test0291() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])/x^2, x]", //
        "-b*d*n/x-b*e*n*x^(-1+r)/(1-r)^2-d*(a+b*Log[c*x^n])/x-e*x^(-1+r)*(a+b*Log[c*x^n])/(1-r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:489
  public void test0292() {
    check( //
        "Integrate[(d+e*x^r)^2*(a+b*Log[c*x^n])/x^5, x]", //
        "-1/16*b*d^2*n/x^4-1/4*b*e^2*n/((2-r)^2*x^(2*(2-r)))-2*b*d*e*n*x^(-4+r)/(4-r)^2-1/4*d^2*(a+b*Log[c*x^n])/x^4-1/2*e^2*(a+b*Log[c*x^n])/((2-r)*x^(2*(2-r)))-2*d*e*x^(-4+r)*(a+b*Log[c*x^n])/(4-r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:497
  public void test0293() {
    check( //
        "Integrate[x^5*(d+e*x^r)^3*(a+b*Log[c*x^n]), x]", //
        "-1/36*b*d^3*n*x^6-1/9*b*e^3*n*x^(3*(2+r))/(2+r)^2-3/4*b*d*e^2*n*x^(2*(3+r))/(3+r)^2-3*b*d^2*e*n*x^(6+r)/(6+r)^2+1/6*(d^3*x^6+2*e^3*x^(3*(2+r))/(2+r)+9*d*e^2*x^(2*(3+r))/(3+r)+18*d^2*e*x^(6+r)/(6+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:527
  public void test0294() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(c+(-1)/x^n)), x]", //
        "a*Log[1-c*x^n]/(c*n)-b*PolyLog[2,1-c*x^n]/(c*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:539
  public void test0295() {
    check( //
        "Integrate[(d+e*x^r)^2*(a+b*Log[c*x^n])^2/x, x]", //
        "4*b^2*d*e*n^2*x^r/r^3+1/4*b^2*e^2*n^2*x^(2*r)/r^3-4*b*d*e*n*x^r*(a+b*Log[c*x^n])/r^2-1/2*b*e^2*n*x^(2*r)*(a+b*Log[c*x^n])/r^2+2*d*e*x^r*(a+b*Log[c*x^n])^2/r+1/2*e^2*x^(2*r)*(a+b*Log[c*x^n])^2/r+1/3*d^2*(a+b*Log[c*x^n])^3/(b*n)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:41
  public void test0296() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*x^2)]/x, x]", //
        "-1/2*(a+b*Log[c*x^n])*PolyLog[2,-d*f*x^2]+1/4*b*n*PolyLog[3,-d*f*x^2]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:49
  public void test0297() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(1/d+f*x^2)]/x, x]", //
        "-1/2*(a+b*Log[c*x^n])^2*PolyLog[2,-d*f*x^2]+1/2*b*n*(a+b*Log[c*x^n])*PolyLog[3,-d*f*x^2]-1/4*b^2*n^2*PolyLog[4,-d*f*x^2]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:57
  public void test0298() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(1/d+f*x^2)]/x, x]", //
        "-1/2*(a+b*Log[c*x^n])^3*PolyLog[2,-d*f*x^2]+3/4*b*n*(a+b*Log[c*x^n])^2*PolyLog[3,-d*f*x^2]-3/4*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[4,-d*f*x^2]+3/8*b^3*n^3*PolyLog[5,-d*f*x^2]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:67
  public void test0299() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*Sqrt[x])]/x^2, x]", //
        "-1/2*b*d^2*f^2*n*Log[x]+1/4*b*d^2*f^2*n*Log[x]^2-1/2*d^2*f^2*Log[x]*(a+b*Log[c*x^n])+b*d^2*f^2*n*Log[1+d*f*Sqrt[x]]-b*n*Log[1+d*f*Sqrt[x]]/x+d^2*f^2*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]-(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]/x+2*b*d^2*f^2*n*PolyLog[2,-d*f*Sqrt[x]]-3*b*d*f*n/Sqrt[x]-d*f*(a+b*Log[c*x^n])/Sqrt[x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:75
  public void test0300() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(1/d+f*Sqrt[x])]/x^3, x]", //
        "-37/108*b^2*d*f*n^2/x^(3/2)+7/8*b^2*d^2*f^2*n^2/x-1/8*b^2*d^4*f^4*n^2*Log[x]+1/8*b^2*d^4*f^4*n^2*Log[x]^2-7/18*b*d*f*n*(a+b*Log[c*x^n])/x^(3/2)+3/4*b*d^2*f^2*n*(a+b*Log[c*x^n])/x-1/4*b*d^4*f^4*n*Log[x]*(a+b*Log[c*x^n])-1/6*d*f*(a+b*Log[c*x^n])^2/x^(3/2)+1/4*d^2*f^2*(a+b*Log[c*x^n])^2/x-1/12*d^4*f^4*(a+b*Log[c*x^n])^3/(b*n)+1/4*b^2*d^4*f^4*n^2*Log[1+d*f*Sqrt[x]]-1/4*b^2*n^2*Log[1+d*f*Sqrt[x]]/x^2+1/2*b*d^4*f^4*n*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]-1/2*b*n*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]/x^2+1/2*d^4*f^4*(a+b*Log[c*x^n])^2*Log[1+d*f*Sqrt[x]]-1/2*(a+b*Log[c*x^n])^2*Log[1+d*f*Sqrt[x]]/x^2+b^2*d^4*f^4*n^2*PolyLog[2,-d*f*Sqrt[x]]+2*b*d^4*f^4*n*(a+b*Log[c*x^n])*PolyLog[2,-d*f*Sqrt[x]]-4*b^2*d^4*f^4*n^2*PolyLog[3,-d*f*Sqrt[x]]-21/4*b^2*d^3*f^3*n^2/Sqrt[x]-5/2*b*d^3*f^3*n*(a+b*Log[c*x^n])/Sqrt[x]-1/2*d^3*f^3*(a+b*Log[c*x^n])^2/Sqrt[x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:85
  public void test0301() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(1/d+f*x^m)]/x, x]", //
        "-(a+b*Log[c*x^n])^2*PolyLog[2,-d*f*x^m]/m+2*b*n*(a+b*Log[c*x^n])*PolyLog[3,-d*f*x^m]/m^2-2*b^2*n^2*PolyLog[4,-d*f*x^m]/m^3");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:99
  public void test0302() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^3, x]", //
        "-3/4*b*f*m*n/(e*x)-1/4*b*f^2*m*n*Log[x]/e^2+1/4*b*f^2*m*n*Log[x]^2/e^2-1/2*f*m*(a+b*Log[c*x^n])/(e*x)-1/2*f^2*m*Log[x]*(a+b*Log[c*x^n])/e^2+1/4*b*f^2*m*n*Log[e+f*x]/e^2-1/2*b*f^2*m*n*Log[-f*x/e]*Log[e+f*x]/e^2+1/2*f^2*m*(a+b*Log[c*x^n])*Log[e+f*x]/e^2-1/4*b*n*Log[d*(e+f*x)^m]/x^2-1/2*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^2-1/2*b*f^2*m*n*PolyLog[2,1+f*x/e]/e^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:107
  public void test0303() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x^4, x]", //
        "-19/108*b^2*f*m*n^2/(e*x^2)+26/27*b^2*f^2*m*n^2/(e^2*x)+2/27*b^2*f^3*m*n^2*Log[x]/e^3-5/18*b*f*m*n*(a+b*Log[c*x^n])/(e*x^2)+8/9*b*f^2*m*n*(a+b*Log[c*x^n])/(e^2*x)-2/9*b*f^3*m*n*Log[1+e/(f*x)]*(a+b*Log[c*x^n])/e^3-1/6*f*m*(a+b*Log[c*x^n])^2/(e*x^2)+1/3*f^2*m*(a+b*Log[c*x^n])^2/(e^2*x)-1/3*f^3*m*Log[1+e/(f*x)]*(a+b*Log[c*x^n])^2/e^3-2/27*b^2*f^3*m*n^2*Log[e+f*x]/e^3-2/27*b^2*n^2*Log[d*(e+f*x)^m]/x^3-2/9*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^3-1/3*(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]/x^3+2/9*b^2*f^3*m*n^2*PolyLog[2,-e/(f*x)]/e^3+2/3*b*f^3*m*n*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x)]/e^3+2/3*b^2*f^3*m*n^2*PolyLog[3,-e/(f*x)]/e^3");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:117
  public void test0304() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x, x]", //
        "1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/(b*n)-1/2*m*(a+b*Log[c*x^n])^2*Log[1+f*x^2/e]/(b*n)-1/2*m*(a+b*Log[c*x^n])*PolyLog[2,-f*x^2/e]+1/4*b*m*n*PolyLog[3,-f*x^2/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:125
  public void test0305() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m], x]", //
        "-3/4*b^2*m*n^2*x^2+b*m*n*x^2*(a+b*Log[c*x^n])-1/2*m*x^2*(a+b*Log[c*x^n])^2+1/4*b^2*e*m*n^2*Log[e+f*x^2]/f+1/4*b^2*n^2*x^2*Log[d*(e+f*x^2)^m]-1/2*b*n*x^2*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]+1/2*x^2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]-1/2*b*e*m*n*(a+b*Log[c*x^n])*Log[1+f*x^2/e]/f+1/2*e*m*(a+b*Log[c*x^n])^2*Log[1+f*x^2/e]/f-1/4*b^2*e*m*n^2*PolyLog[2,-f*x^2/e]/f+1/2*b*e*m*n*(a+b*Log[c*x^n])*PolyLog[2,-f*x^2/e]/f-1/4*b^2*e*m*n^2*PolyLog[3,-f*x^2/e]/f");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:133
  public void test0306() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m], x]", //
        "3/2*b^3*m*n^3*x^2-9/4*b^2*m*n^2*x^2*(a+b*Log[c*x^n])+3/2*b*m*n*x^2*(a+b*Log[c*x^n])^2-1/2*m*x^2*(a+b*Log[c*x^n])^3-3/8*b^3*e*m*n^3*Log[e+f*x^2]/f-3/8*b^3*n^3*x^2*Log[d*(e+f*x^2)^m]+3/4*b^2*n^2*x^2*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]-3/4*b*n*x^2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]+1/2*x^2*(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m]+3/4*b^2*e*m*n^2*(a+b*Log[c*x^n])*Log[1+f*x^2/e]/f-3/4*b*e*m*n*(a+b*Log[c*x^n])^2*Log[1+f*x^2/e]/f+1/2*e*m*(a+b*Log[c*x^n])^3*Log[1+f*x^2/e]/f+3/8*b^3*e*m*n^3*PolyLog[2,-f*x^2/e]/f-3/4*b^2*e*m*n^2*(a+b*Log[c*x^n])*PolyLog[2,-f*x^2/e]/f+3/4*b*e*m*n*(a+b*Log[c*x^n])^2*PolyLog[2,-f*x^2/e]/f+3/8*b^3*e*m*n^3*PolyLog[3,-f*x^2/e]/f-3/4*b^2*e*m*n^2*(a+b*Log[c*x^n])*PolyLog[3,-f*x^2/e]/f+3/8*b^3*e*m*n^3*PolyLog[4,-f*x^2/e]/f");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:172
  public void test0307() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^r]/x, x]", //
        "1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^m)^r]/(b*n)-1/2*r*(a+b*Log[c*x^n])^2*Log[1+f*x^m/e]/(b*n)-r*(a+b*Log[c*x^n])*PolyLog[2,-f*x^m/e]/m+b*n*r*PolyLog[3,-f*x^m/e]/m^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:201
  public void test0308() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r]), x]", //
        "-1/8*b^2*e*n^2*r*x^2+1/8*b*e*n*(2*a-b*n)*r*x^2-1/8*e*(2*a^2-2*a*b*n+b^2*n^2)*r*x^2+1/4*b^2*e*n*r*x^2*Log[c*x^n]-1/4*b*e*(2*a-b*n)*r*x^2*Log[c*x^n]-1/4*b^2*e*r*x^2*Log[c*x^n]^2+1/4*b^2*n^2*x^2*(d+e*Log[f*x^r])-1/2*b*n*x^2*(a+b*Log[c*x^n])*(d+e*Log[f*x^r])+1/2*x^2*(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:266
  public void test0309() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[3,e*x]/x, x]", //
        "(a+b*Log[c*x^n])*PolyLog[4,e*x]-b*n*PolyLog[5,e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:293
  public void test0310() {
    check( //
        "Integrate[Log[c*(b*x^n)^p]^2/x^4, x]", //
        "-2/27*n^2*p^2/x^3-2/9*n*p*Log[c*(b*x^n)^p]/x^3-1/3*Log[c*(b*x^n)^p]^2/x^3");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:14
  public void test0311() {
    check( //
        "Integrate[(a*g+b*g*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "1/3*B*(b*c-a*d)^2*g^2*n*x/d^2-1/6*B*(b*c-a*d)*g^2*n*(a+b*x)^2/(b*d)+1/3*g^2*(a+b*x)^3*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/b-1/3*B*(b*c-a*d)^3*g^2*n*Log[c+d*x]/(b*d^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:53
  public void test0312() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(c*g+d*g*x)^2, x]", //
        "A*(a+b*x)/((b*c-a*d)*g^2*(c+d*x))-B*n*(a+b*x)/((b*c-a*d)*g^2*(c+d*x))+B*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/((b*c-a*d)*g^2*(c+d*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:129
  public void test0313() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])/(a*g+b*g*x), x]", //
        "-Log[(-b*c+a*d)/(d*(a+b*x))]*(A+B*Log[e*(a+b*x)/(c+d*x)])/(b*g)+B*PolyLog[2,1+(b*c-a*d)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:138
  public void test0314() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])^2/(a*g+b*g*x), x]", //
        "-(A+B*Log[e*(a+b*x)/(c+d*x)])^2*Log[1-b*(c+d*x)/(d*(a+b*x))]/(b*g)+2*B*(A+B*Log[e*(a+b*x)/(c+d*x)])*PolyLog[2,b*(c+d*x)/(d*(a+b*x))]/(b*g)+2*B^2*PolyLog[3,b*(c+d*x)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:176
  public void test0315() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/(a*g+b*g*x)^2, x]", //
        "-8*B^2*(c+d*x)/((b*c-a*d)*g^2*(a+b*x))-4*B*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)*g^2*(a+b*x))-(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)*g^2*(a+b*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:196
  public void test0316() {
    check( //
        "Integrate[(a+b*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n]), x]", //
        "1/5*B*(b*c-a*d)^4*n*x/d^4-1/10*B*(b*c-a*d)^3*n*(a+b*x)^2/(b*d^3)+1/15*B*(b*c-a*d)^2*n*(a+b*x)^3/(b*d^2)-1/20*B*(b*c-a*d)*n*(a+b*x)^4/(b*d)-1/5*B*(b*c-a*d)^5*n*Log[c+d*x]/(b*d^5)+1/5*(a+b*x)^5*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:212
  public void test0317() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/(a+b*x)^5, x]", //
        "2*B^2*d^3*n^2*(c+d*x)/((b*c-a*d)^4*(a+b*x))-3/4*b*B^2*d^2*n^2*(c+d*x)^2/((b*c-a*d)^4*(a+b*x)^2)+2/9*b^2*B^2*d*n^2*(c+d*x)^3/((b*c-a*d)^4*(a+b*x)^3)-1/32*b^3*B^2*n^2*(c+d*x)^4/((b*c-a*d)^4*(a+b*x)^4)+2*B*d^3*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x))-3/2*b*B*d^2*n*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^2)+2/3*b^2*B*d*n*(c+d*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^3)-1/8*b^3*B*n*(c+d*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^4)+d^3*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x))-3/2*b*d^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^2)+b^2*d*(c+d*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^3)-1/4*b^3*(c+d*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:220
  public void test0318() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/(a+b*x)^5, x]", //
        "6*B^3*d^3*n^3*(c+d*x)/((b*c-a*d)^4*(a+b*x))-9/8*b*B^3*d^2*n^3*(c+d*x)^2/((b*c-a*d)^4*(a+b*x)^2)+2/9*b^2*B^3*d*n^3*(c+d*x)^3/((b*c-a*d)^4*(a+b*x)^3)-3/128*b^3*B^3*n^3*(c+d*x)^4/((b*c-a*d)^4*(a+b*x)^4)+6*B^2*d^3*n^2*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x))-9/4*b*B^2*d^2*n^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^2)+2/3*b^2*B^2*d*n^2*(c+d*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^3)-3/32*b^3*B^2*n^2*(c+d*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((b*c-a*d)^4*(a+b*x)^4)+3*B*d^3*n*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x))-9/4*b*B*d^2*n*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^2)+b^2*B*d*n*(c+d*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^3)-3/16*b^3*B*n*(c+d*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/((b*c-a*d)^4*(a+b*x)^4)+d^3*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^4*(a+b*x))-3/2*b*d^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^4*(a+b*x)^2)+b^2*d*(c+d*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^4*(a+b*x)^3)-1/4*b^3*(c+d*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/((b*c-a*d)^4*(a+b*x)^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:237
  public void test0319() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)/(a+b*x)])/(a*g+b*g*x)^4, x]", //
        "1/9*B/(b*g^4*(a+b*x)^3)-1/6*B*d/(b*(b*c-a*d)*g^4*(a+b*x)^2)+1/3*B*d^2/(b*(b*c-a*d)^2*g^4*(a+b*x))+1/3*B*d^3*Log[a+b*x]/(b*(b*c-a*d)^3*g^4)-1/3*B*d^3*Log[c+d*x]/(b*(b*c-a*d)^3*g^4)+1/3*(-A-B*Log[e*(c+d*x)/(a+b*x)])/(b*g^4*(a+b*x)^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:245
  public void test0320() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)/(a+b*x)])^2/(a*g+b*g*x)^3, x]", //
        "-2*A*B*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))+2*B^2*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))-1/4*b*B^2*(c+d*x)^2/((b*c-a*d)^2*g^3*(a+b*x)^2)-2*B^2*d*(c+d*x)*Log[e*(c+d*x)/(a+b*x)]/((b*c-a*d)^2*g^3*(a+b*x))+1/2*b*B*(c+d*x)^2*(A+B*Log[e*(c+d*x)/(a+b*x)])/((b*c-a*d)^2*g^3*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(c+d*x)/(a+b*x)])^2/((b*c-a*d)^2*g^3*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(c+d*x)/(a+b*x)])^2/((b*c-a*d)^2*g^3*(a+b*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:265
  public void test0321() {
    check( //
        "Integrate[(a*g+b*g*x)^3*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2]), x]", //
        "1/2*B*(b*c-a*d)^3*g^3*x/d^3-1/4*B*(b*c-a*d)^2*g^3*(a+b*x)^2/(b*d^2)+1/6*B*(b*c-a*d)*g^3*(a+b*x)^3/(b*d)-1/2*B*(b*c-a*d)^4*g^3*Log[c+d*x]/(b*d^4)+1/4*g^3*(a+b*x)^4*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/b");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:275
  public void test0322() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/((a+b*x)*(c+d*x)), x]", //
        "1/2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2/(B*(b*c-a*d)*n)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:283
  public void test0323() {
    check( //
        "Integrate[1/((a*f+b*f*x)*(c*g+d*g*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])), x]", //
        "Log[A+B*Log[e*(a+b*x)^n/(c+d*x)^n]]/(B*(b*c-a*d)*f*g*n)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:12
  public void test0324() {
    check( //
        "Integrate[(f+g/x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "A*f*x-B*g*n*Log[x]*Log[1+b*x/a]+B*f*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/b+g*Log[x]*(A+B*Log[e*((a+b*x)/(c+d*x))^n])-B*(b*c-a*d)*f*n*Log[c+d*x]/(b*d)+B*g*n*Log[x]*Log[1+d*x/c]-B*g*n*PolyLog[2,-b*x/a]+B*g*n*PolyLog[2,-d*x/c]");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:26
  public void test0325() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(a+b*x), x]", //
        "-1/2*p*r*Log[a+b*x]^2/b-q*r*Log[a+b*x]*Log[b*(c+d*x)/(b*c-a*d)]/b+Log[a+b*x]*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/b-q*r*PolyLog[2,-d*(a+b*x)/(b*c-a*d)]/b");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:44
  public void test0326() {
    check( //
        "Integrate[(g+h*x)^2*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r], x]", //
        "-1/3*(b*g-a*h)^2*p*r*x/b^2-1/3*(d*g-c*h)^2*q*r*x/d^2-1/6*(b*g-a*h)*p*r*(g+h*x)^2/(b*h)-1/6*(d*g-c*h)*q*r*(g+h*x)^2/(d*h)-1/9*p*r*(g+h*x)^3/h-1/9*q*r*(g+h*x)^3/h-1/3*(b*g-a*h)^3*p*r*Log[a+b*x]/(b^3*h)-1/3*(d*g-c*h)^3*q*r*Log[c+d*x]/(d^3*h)+1/3*(g+h*x)^3*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/h");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:136
  public void test0327() {
    check( //
        "Integrate[Log[(-b*c+a*d)/(d*(a+b*x))]*Log[e*(c+d*x)/(a+b*x)]/((a+b*x)*(c+d*x)), x]", //
        "Log[e*(c+d*x)/(a+b*x)]*PolyLog[2,1+(b*c-a*d)/(d*(a+b*x))]/(b*c-a*d)-PolyLog[3,1+(b*c-a*d)/(d*(a+b*x))]/(b*c-a*d)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:12
  public void test0328() {
    check( //
        "Integrate[Log[c*(d+e*x)]^2, x]", //
        "2*x-2*(d+e*x)*Log[c*(d+e*x)]/e+(d+e*x)*Log[c*(d+e*x)]^2/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:22
  public void test0329() {
    check( //
        "Integrate[Log[c*(d+e*x)]^(1/2), x]", //
        "-1/2*Erfi[Sqrt[Log[c*(d+e*x)]]]*Sqrt[Pi]/(c*e)+(d+e*x)*Sqrt[Log[c*(d+e*x)]]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:36
  public void test0330() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^2, x]", //
        "-2*a*b*n*x+2*b^2*n^2*x-2*b^2*n*(d+e*x)*Log[c*(d+e*x)^n]/e+(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:46
  public void test0331() {
    check( //
        "Integrate[1/(a+b*Log[c*(d+e*x)^n])^(1/2), x]", //
        "(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*e*(c*(d+e*x)^n)^(1/n)*Sqrt[b]*Sqrt[n])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:64
  public void test0332() {
    check( //
        "Integrate[(f+g*x)^4*(a+b*Log[c*(d+e*x)^n]), x]", //
        "-1/5*b*(e*f-d*g)^4*n*x/e^4-1/10*b*(e*f-d*g)^3*n*(f+g*x)^2/(e^3*g)-1/15*b*(e*f-d*g)^2*n*(f+g*x)^3/(e^2*g)-1/20*b*(e*f-d*g)*n*(f+g*x)^4/(e*g)-1/25*b*n*(f+g*x)^5/g-1/5*b*(e*f-d*g)^5*n*Log[d+e*x]/(e^5*g)+1/5*(f+g*x)^5*(a+b*Log[c*(d+e*x)^n])/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:80
  public void test0333() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^2/(f+g*x)^4, x]", //
        "-1/3*b^2*e^2*n^2/(g*(e*f-d*g)^2*(f+g*x))-1/3*b^2*e^3*n^2*Log[d+e*x]/(g*(e*f-d*g)^3)+1/3*b*e*n*(a+b*Log[c*(d+e*x)^n])/(g*(e*f-d*g)*(f+g*x)^2)-2/3*b*e^2*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])/((e*f-d*g)^3*(f+g*x))-1/3*(a+b*Log[c*(d+e*x)^n])^2/(g*(f+g*x)^3)+b^2*e^3*n^2*Log[f+g*x]/(g*(e*f-d*g)^3)-2/3*b*e^3*n*(a+b*Log[c*(d+e*x)^n])*Log[1+(e*f-d*g)/(g*(d+e*x))]/(g*(e*f-d*g)^3)+2/3*b^2*e^3*n^2*PolyLog[2,(-e*f+d*g)/(g*(d+e*x))]/(g*(e*f-d*g)^3)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:96
  public void test0334() {
    check( //
        "Integrate[Log[a+b*x+c*x], x]", //
        "-x+(a+(b+c)*x)*Log[a+(b+c)*x]/(b+c)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:122
  public void test0335() {
    check( //
        "Integrate[1/(a+b*Log[c*(d+e*x)^n]), x]", //
        "(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b*e*n*(c*(d+e*x)^n)^(1/n))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:168
  public void test0336() {
    check( //
        "Integrate[1/(a+b*Log[c*(d+e*x)^n])^(3/2), x]", //
        "2*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*b^(3/2)*e*n^(3/2)*(c*(d+e*x)^n)^(1/n))-2*(d+e*x)/(b*e*n*Sqrt[a+b*Log[c*(d+e*x)^n]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:182
  public void test0337() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/(f+g*x)^(3/2), x]", //
        "-4*b*n*ArcTanh[Sqrt[e]*Sqrt[f+g*x]/Sqrt[e*f-d*g]]*Sqrt[e]/(g*Sqrt[e*f-d*g])-2*(a+b*Log[c*(d+e*x)^n])/(g*Sqrt[f+g*x])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:339
  public void test0338() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/(x*(f+g*x)), x]", //
        "Log[-e*x/d]*(a+b*Log[c*(d+e*x)^n])/f-(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/f-b*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/f+b*n*PolyLog[2,1+e*x/d]/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:391
  public void test0339() {
    check( //
        "Integrate[Log[c+d*x]/(x^4*(a+b*x^3)), x]", //
        "-1/6*d/(a*c*x^2)+1/3*d^2/(a*c^2*x)+1/3*d^3*Log[x]/(a*c^3)-1/3*d^3*Log[c+d*x]/(a*c^3)-1/3*Log[c+d*x]/(a*x^3)-b*Log[-d*x/c]*Log[c+d*x]/a^2+1/3*b*Log[-d*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*c-a^(1/3)*d)]*Log[c+d*x]/a^2+1/3*b*Log[-d*((-1)^(2/3)*a^(1/3)+b^(1/3)*x)/(b^(1/3)*c-(-1)^(2/3)*a^(1/3)*d)]*Log[c+d*x]/a^2+1/3*b*Log[(-1)^(1/3)*d*(a^(1/3)+(-1)^(2/3)*b^(1/3)*x)/(b^(1/3)*c+(-1)^(1/3)*a^(1/3)*d)]*Log[c+d*x]/a^2+1/3*b*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c-a^(1/3)*d)]/a^2+1/3*b*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c+(-1)^(1/3)*a^(1/3)*d)]/a^2+1/3*b*PolyLog[2,b^(1/3)*(c+d*x)/(b^(1/3)*c-(-1)^(2/3)*a^(1/3)*d)]/a^2-b*PolyLog[2,1+d*x/c]/a^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:468
  public void test0340() {
    check( //
        "Integrate[Log[c*(a+(-d+a*c*d)/(c*e*x^m))]/(x*(d+e*x^m)), x]", //
        "PolyLog[2,(1-a*c)*(e+d/x^m)/e]/(d*m)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:480
  public void test0341() {
    check( //
        "Integrate[Log[c*(a+b*x)^n]^3/(d*x+e*x^2), x]", //
        "Log[-b*x/a]*Log[c*(a+b*x)^n]^3/d-Log[c*(a+b*x)^n]^3*Log[b*(d+e*x)/(b*d-a*e)]/d-3*n*Log[c*(a+b*x)^n]^2*PolyLog[2,-e*(a+b*x)/(b*d-a*e)]/d+3*n*Log[c*(a+b*x)^n]^2*PolyLog[2,1+b*x/a]/d+6*n^2*Log[c*(a+b*x)^n]*PolyLog[3,-e*(a+b*x)/(b*d-a*e)]/d-6*n^2*Log[c*(a+b*x)^n]*PolyLog[3,1+b*x/a]/d-6*n^3*PolyLog[4,-e*(a+b*x)/(b*d-a*e)]/d+6*n^3*PolyLog[4,1+b*x/a]/d");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:490
  public void test0342() {
    check( //
        "Integrate[x*Log[x]/(a+b*x+c*x^2), x]", //
        "1/2*Log[x]*Log[1+2*c*x/(b-Sqrt[b^2-4*a*c])]*(1-b/Sqrt[b^2-4*a*c])/c+1/2*PolyLog[2,-2*c*x/(b-Sqrt[b^2-4*a*c])]*(1-b/Sqrt[b^2-4*a*c])/c+1/2*Log[x]*Log[1+2*c*x/(b+Sqrt[b^2-4*a*c])]*(1+b/Sqrt[b^2-4*a*c])/c+1/2*PolyLog[2,-2*c*x/(b+Sqrt[b^2-4*a*c])]*(1+b/Sqrt[b^2-4*a*c])/c");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:506
  public void test0343() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n]), x]", //
        "2*b*m*n*x-b*n*x*Log[f*x^m]-b*d*m*n*Log[d+e*x]/e-x*(m-Log[f*x^m])*(a+b*Log[c*(d+e*x)^n])+b*d*n*Log[f*x^m]*Log[1+e*x/d]/e+b*d*m*n*PolyLog[2,-e*x/d]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:594
  public void test0344() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^m)^n])^(7/2), x]", //
        "-2/5*(e+f*x)/(b*f*m*n*(a+b*Log[c*(d*(e+f*x)^m)^n])^(5/2))-4/15*(e+f*x)/(b^2*f*m^2*n^2*(a+b*Log[c*(d*(e+f*x)^m)^n])^(3/2))+8/15*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/(Sqrt[b]*Sqrt[m]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*m*n))*b^(7/2)*f*m^(7/2)*n^(7/2)*(c*(d*(e+f*x)^m)^n)^(1/(m*n)))-8/15*(e+f*x)/(b^3*f*m^3*n^3*Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:618
  public void test0345() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^2/(g+h*x)^2, x]", //
        "(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/((f*g-e*h)*(g+h*x))-2*b*f*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])*Log[f*(g+h*x)/(f*g-e*h)]/(h*(f*g-e*h))-2*b^2*f*p^2*q^2*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:626
  public void test0346() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^4, x]", //
        "-24*a*b^3*p^3*q^3*x+24*b^4*p^4*q^4*x-24*b^4*p^3*q^3*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f+12*b^2*p^2*q^2*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f-4*b*p*q*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f+(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^4/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:669
  public void test0347() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^p)^q])^(3/2), x]", //
        "2*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]]/(Sqrt[b]*Sqrt[p]*Sqrt[q])]*Sqrt[Pi]/(E^(a/(b*p*q))*b^(3/2)*f*p^(3/2)*q^(3/2)*(c*(d*(e+f*x)^p)^q)^(1/(p*q)))-2*(e+f*x)/(b*f*p*q*Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:737
  public void test0348() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])/(Sqrt[2-h*x]*Sqrt[2+h*x]), x]", //
        "1/2*I*b*p*q*ArcSin[1/2*h*x]^2/h+ArcSin[1/2*h*x]*(a+b*Log[c*(d*(e+f*x)^p)^q])/h-b*p*q*ArcSin[1/2*h*x]*Log[1+2*E^(I*ArcSin[1/2*h*x])*f/(I*e*h-Sqrt[4*f^2-e^2*h^2])]/h-b*p*q*ArcSin[1/2*h*x]*Log[1+2*E^(I*ArcSin[1/2*h*x])*f/(I*e*h+Sqrt[4*f^2-e^2*h^2])]/h+I*b*p*q*PolyLog[2,-2*E^(I*ArcSin[1/2*h*x])*f/(I*e*h-Sqrt[4*f^2-e^2*h^2])]/h+I*b*p*q*PolyLog[2,-2*E^(I*ArcSin[1/2*h*x])*f/(I*e*h+Sqrt[4*f^2-e^2*h^2])]/h");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:17
  public void test0349() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]/x, x]", //
        "1/2*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]+1/2*p*PolyLog[2,1+b*x^2/a]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:26
  public void test0350() {
    check( //
        "Integrate[x^3*Log[c*(a+b*x^3)^p], x]", //
        "3/4*a*p*x/b-3/16*p*x^4-1/4*a^(4/3)*p*Log[a^(1/3)+b^(1/3)*x]/b^(4/3)+1/8*a^(4/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(4/3)+1/4*x^4*Log[c*(a+b*x^3)^p]+1/4*a^(4/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/b^(4/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:34
  public void test0351() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/x^5, x]", //
        "-3/4*b*p/(a*x)+1/4*b^(4/3)*p*Log[a^(1/3)+b^(1/3)*x]/a^(4/3)-1/8*b^(4/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(4/3)-1/4*Log[c*(a+b*x^3)^p]/x^4+1/4*b^(4/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/a^(4/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:44
  public void test0352() {
    check( //
        "Integrate[Log[c*(a+b/x)^p]/x, x]", //
        "-Log[c*(a+b/x)^p]*Log[-b/(a*x)]-p*PolyLog[2,1+b/(a*x)]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:52
  public void test0353() {
    check( //
        "Integrate[x*Log[c*(a+b/x^2)^p], x]", //
        "1/2*x^2*Log[c*(a+b/x^2)^p]+1/2*b*p*Log[b+a*x^2]/a");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:113
  public void test0354() {
    check( //
        "Integrate[x^3*Log[c*(a+b*x^2)^p]^2, x]", //
        "-a*p^2*x^2/b+1/8*p^2*(a+b*x^2)^2/b^2+a*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]/b^2-1/4*p*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]/b^2-1/2*a*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/b^2+1/4*(a+b*x^2)^2*Log[c*(a+b*x^2)^p]^2/b^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:129
  public void test0355() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^3/x, x]", //
        "1/2*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]^3+3/2*p*Log[c*(a+b*x^2)^p]^2*PolyLog[2,1+b*x^2/a]-3*p^2*Log[c*(a+b*x^2)^p]*PolyLog[3,1+b*x^2/a]+3*p^3*PolyLog[4,1+b*x^2/a]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:153
  public void test0356() {
    check( //
        "Integrate[x^3/Log[c*(a+b*x^2)^p]^3, x]", //
        "-1/4*a*(a+b*x^2)*ExpIntegralEi[Log[c*(a+b*x^2)^p]/p]/(b^2*p^3*(c*(a+b*x^2)^p)^(1/p))+(a+b*x^2)^2*ExpIntegralEi[2*Log[c*(a+b*x^2)^p]/p]/(b^2*p^3*(c*(a+b*x^2)^p)^(2/p))-1/4*x^2*(a+b*x^2)/(b*p*Log[c*(a+b*x^2)^p]^2)-1/4*a*(a+b*x^2)/(b^2*p^2*Log[c*(a+b*x^2)^p])-1/2*x^2*(a+b*x^2)/(b*p^2*Log[c*(a+b*x^2)^p])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:170
  public void test0357() {
    check( //
        "Integrate[x^5*Log[c*(d+e*x^3)^p]^2, x]", //
        "-2/3*d*p^2*x^3/e+1/12*p^2*(d+e*x^3)^2/e^2+2/3*d*p*(d+e*x^3)*Log[c*(d+e*x^3)^p]/e^2-1/6*p*(d+e*x^3)^2*Log[c*(d+e*x^3)^p]/e^2-1/3*d*(d+e*x^3)*Log[c*(d+e*x^3)^p]^2/e^2+1/6*(d+e*x^3)^2*Log[c*(d+e*x^3)^p]^2/e^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:209
  public void test0358() {
    check( //
        "Integrate[(f*x)^(-1+2*n)*Log[c*(d+e*x^n)^p]^2, x]", //
        "-2*d*p^2*x^(1-n)*(f*x)^(-1+2*n)/(e*n)+1/4*p^2*x^(1-2*n)*(f*x)^(-1+2*n)*(d+e*x^n)^2/(e^2*n)+2*d*p*x^(1-2*n)*(f*x)^(-1+2*n)*(d+e*x^n)*Log[c*(d+e*x^n)^p]/(e^2*n)-1/2*p*x^(1-2*n)*(f*x)^(-1+2*n)*(d+e*x^n)^2*Log[c*(d+e*x^n)^p]/(e^2*n)-d*x^(1-2*n)*(f*x)^(-1+2*n)*(d+e*x^n)*Log[c*(d+e*x^n)^p]^2/(e^2*n)+1/2*x^(1-2*n)*(f*x)^(-1+2*n)*(d+e*x^n)^2*Log[c*(d+e*x^n)^p]^2/(e^2*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:219
  public void test0359() {
    check( //
        "Integrate[Log[c*(d+e*x^n)]/x, x]", //
        "Log[-e*x^n/d]*Log[c*(d+e*x^n)]/n+PolyLog[2,1+e*x^n/d]/n");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:25
  public void test0360() {
    check( //
        "Integrate[x^2*Log[c*x]^3, x]", //
        "-2/27*x^3+2/9*x^3*Log[c*x]-1/3*x^3*Log[c*x]^2+1/3*x^3*Log[c*x]^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:43
  public void test0361() {
    check( //
        "Integrate[1/Log[c*x]^2, x]", //
        "LogIntegral[c*x]/c-x/Log[c*x]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:79
  public void test0362() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3/x^4, x]", //
        "-2/27*b^3*n^3/x^3-2/9*b^2*n^2*(a+b*Log[c*x^n])/x^3-1/3*b*n*(a+b*Log[c*x^n])^2/x^3-1/3*(a+b*Log[c*x^n])^3/x^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:97
  public void test0363() {
    check( //
        "Integrate[1/(x^4*(a+b*Log[c*x^n])^2), x]", //
        "-3*E^(3*a/(b*n))*(c*x^n)^(3/n)*ExpIntegralEi[-3*(a+b*Log[c*x^n])/(b*n)]/(b^2*n^2*x^3)+(-1)/(b*n*x^3*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:105
  public void test0364() {
    check( //
        "Integrate[1/(x^4*(a+b*Log[c*x^n])^3), x]", //
        "9/2*E^(3*a/(b*n))*(c*x^n)^(3/n)*ExpIntegralEi[-3*(a+b*Log[c*x^n])/(b*n)]/(b^3*n^3*x^3)+(-1/2)/(b*n*x^3*(a+b*Log[c*x^n])^2)+3/2/(b^2*n^2*x^3*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:127
  public void test0365() {
    check( //
        "Integrate[1/((d*x)^(1/2)*(a+b*Log[c*x^n])), x]", //
        "ExpIntegralEi[1/2*(a+b*Log[c*x^n])/(b*n)]*Sqrt[d*x]/(E^(1/2*a/(b*n))*b*d*n*(c*x^n)^(1/2/n))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:135
  public void test0366() {
    check( //
        "Integrate[1/((d*x)^(5/2)*(a+b*Log[c*x^n])^2), x]", //
        "-3/2*E^(3/2*a/(b*n))*(c*x^n)^(3/2/n)*ExpIntegralEi[-3/2*(a+b*Log[c*x^n])/(b*n)]/(b^2*d*n^2*(d*x)^(3/2))+(-1)/(b*d*n*(d*x)^(3/2)*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:157
  public void test0367() {
    check( //
        "Integrate[x^3/Sqrt[Log[a*x^n]], x]", //
        "1/2*x^4*Erfi[2*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/((a*x^n)^(4/n)*Sqrt[n])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:165
  public void test0368() {
    check( //
        "Integrate[x^2/Log[a*x^n]^(3/2), x]", //
        "2*x^3*Erfi[Sqrt[3]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[3*Pi]/(n^(3/2)*(a*x^n)^(3/n))-2*x^3/(n*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:173
  public void test0369() {
    check( //
        "Integrate[x/Log[a*x^n]^(5/2), x]", //
        "-2/3*x^2/(n*Log[a*x^n]^(3/2))+8/3*x^2*Erfi[Sqrt[2]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[2*Pi]/(n^(5/2)*(a*x^n)^(2/n))-8/3*x^2/(n^2*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:191
  public void test0370() {
    check( //
        "Integrate[(d*x)^(-1+n)/Log[c*x^n]^2, x]", //
        "x^(1-n)*(d*x)^(-1+n)*LogIntegral[c*x^n]/(c*n)-(d*x)^n/(d*n*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:35
  public void test0371() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*Log[c*x^n])/x^2, x]", //
        "-b*d^3*n/x-3*b*d*e^2*n*x-1/4*b*e^3*n*x^2-3/2*b*d^2*e*n*Log[x]^2-d^3*(a+b*Log[c*x^n])/x+3*d*e^2*x*(a+b*Log[c*x^n])+1/2*e^3*x^2*(a+b*Log[c*x^n])+3*d^2*e*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:54
  public void test0372() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x)^2, x]", //
        "-x*(a+b*Log[c*x^n])/(e*(d+e*x))+(a+b*n+b*Log[c*x^n])*Log[1+e*x/d]/e^2+b*n*PolyLog[2,-e*x/d]/e^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:62
  public void test0373() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d+e*x)^3, x]", //
        "1/2*b*n/(d*e*(d+e*x))+1/2*b*n*Log[x]/(d^2*e)+1/2*(-a-b*Log[c*x^n])/(e*(d+e*x)^2)-1/2*b*n*Log[d+e*x]/(d^2*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:78
  public void test0374() {
    check( //
        "Integrate[x^5*(a+b*Log[c*x^n])/(d+e*x)^7, x]", //
        "-1/30*b*d^4*n/(e^6*(d+e*x)^5)+5/24*b*d^3*n/(e^6*(d+e*x)^4)-5/9*b*d^2*n/(e^6*(d+e*x)^3)+5/6*b*d*n/(e^6*(d+e*x)^2)-5/6*b*n/(e^6*(d+e*x))+1/6*x^6*(a+b*Log[c*x^n])/(d*(d+e*x)^6)-1/6*b*n*Log[d+e*x]/(d*e^6)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:98
  public void test0375() {
    check( //
        "Integrate[(d+e*x)*(a+b*Log[c*x^n])^2/x^3, x]", //
        "-1/4*b^2*d*n^2/x^2-2*b^2*e*n^2/x-1/2*b*d*n*(a+b*Log[c*x^n])/x^2-2*b*e*n*(a+b*Log[c*x^n])/x-1/2*d*(a+b*Log[c*x^n])^2/x^2-e*(a+b*Log[c*x^n])^2/x");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:106
  public void test0376() {
    check( //
        "Integrate[(d+e*x)^2*(a+b*Log[c*x^n])^2/x^3, x]", //
        "-1/4*b^2*d^2*n^2/x^2-4*b^2*d*e*n^2/x-1/2*b*d^2*n*(a+b*Log[c*x^n])/x^2-4*b*d*e*n*(a+b*Log[c*x^n])/x-1/2*d^2*(a+b*Log[c*x^n])^2/x^2-2*d*e*(a+b*Log[c*x^n])^2/x+1/3*e^2*(a+b*Log[c*x^n])^3/(b*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:116
  public void test0377() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x^2*(d+e*x)), x]", //
        "-2*b^2*n^2/(d*x)-2*b*n*(a+b*Log[c*x^n])/(d*x)-(a+b*Log[c*x^n])^2/(d*x)+e*Log[1+d/(e*x)]*(a+b*Log[c*x^n])^2/d^2-2*b*e*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x)]/d^2-2*b^2*e*n^2*PolyLog[3,-d/(e*x)]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:239
  public void test0378() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*Log[c*x^n])/x, x]", //
        "-1/2*b*d*e*n*x^2-1/16*b*e^2*n*x^4-1/2*b*d^2*n*Log[x]^2+d*e*x^2*(a+b*Log[c*x^n])+1/4*e^2*x^4*(a+b*Log[c*x^n])+d^2*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:247
  public void test0379() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d^2*n/x^5-2/9*b*d*e*n/x^3-b*e^2*n/x-1/5*d^2*(a+b*Log[c*x^n])/x^5-2/3*d*e*(a+b*Log[c*x^n])/x^3-e^2*(a+b*Log[c*x^n])/x");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:265
  public void test0380() {
    check( //
        "Integrate[x^5*(a+b*Log[c*x^n])/(d+e*x^2), x]", //
        "1/4*b*d*n*x^2/e^2-1/16*b*n*x^4/e-1/2*d*x^2*(a+b*Log[c*x^n])/e^2+1/4*x^4*(a+b*Log[c*x^n])/e+1/2*d^2*(a+b*Log[c*x^n])*Log[1+e*x^2/d]/e^3+1/4*b*d^2*n*PolyLog[2,-e*x^2/d]/e^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:289
  public void test0381() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x^2)^3), x]", //
        "1/4*(a+b*Log[c*x^n])/(d*(d+e*x^2)^2)-1/8*Log[1+d/(e*x^2)]*(4*a-3*b*n+4*b*Log[c*x^n])/d^3+1/8*(4*a-b*n+4*b*Log[c*x^n])/(d^2*(d+e*x^2))+1/4*b*n*PolyLog[2,-d/(e*x^2)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:297
  public void test0382() {
    check( //
        "Integrate[x*Log[x^2/c]/(c-x^2), x]", //
        "1/2*PolyLog[2,1-x^2/c]");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:337
  public void test0383() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Sqrt[d+e*x^2]/x^4, x]", //
        "-1/9*b*n*(d+e*x^2)^(3/2)/(d*x^3)+1/3*b*e^(3/2)*n*ArcTanh[x*Sqrt[e]/Sqrt[d+e*x^2]]/d-1/3*(d+e*x^2)^(3/2)*(a+b*Log[c*x^n])/(d*x^3)-1/3*b*e*n*Sqrt[d+e*x^2]/(d*x)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:379
  public void test0384() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x^2)^(5/2), x]", //
        "-1/3*b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]/(d^(3/2)*e)+1/3*(-a-b*Log[c*x^n])/(e*(d+e*x^2)^(3/2))+1/3*b*n/(d*e*Sqrt[d+e*x^2])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:395
  public void test0385() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^4*Sqrt[d-e*x]*Sqrt[d+e*x]), x]", //
        "-2/3*b*e^2*n*(d^2-e^2*x^2)/(d^4*x*Sqrt[d-e*x]*Sqrt[d+e*x])-1/9*b*n*(d^2-e^2*x^2)^2/(d^4*x^3*Sqrt[d-e*x]*Sqrt[d+e*x])-1/3*(d^2-e^2*x^2)*(a+b*Log[c*x^n])/(d^2*x^3*Sqrt[d-e*x]*Sqrt[d+e*x])-2/3*e^2*(d^2-e^2*x^2)*(a+b*Log[c*x^n])/(d^4*x*Sqrt[d-e*x]*Sqrt[d+e*x])-2/3*b*e^3*n*ArcSin[e*x/d]*Sqrt[1-e^2*x^2/d^2]/(d^3*Sqrt[d-e*x]*Sqrt[d+e*x])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:451
  public void test0386() {
    check( //
        "Integrate[(f*x)^(-1+m)*(d+e*x^m)^2*(a+b*Log[c*x^n]), x]", //
        "-b*d^2*n*x*(f*x)^(-1+m)/m^2-1/2*b*d*e*n*x^(1+m)*(f*x)^(-1+m)/m^2-1/9*b*e^2*n*x^(1+2*m)*(f*x)^(-1+m)/m^2-1/3*b*d^3*n*x^(1-m)*(f*x)^(-1+m)*Log[x]/(e*m)+1/3*x^(1-m)*(f*x)^(-1+m)*(d+e*x^m)^3*(a+b*Log[c*x^n])/(e*m)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:474
  public void test0387() {
    check( //
        "Integrate[x*(d+e*x^r)*(a+b*Log[c*x^n]), x]", //
        "-1/4*b*d*n*x^2-b*e*n*x^(2+r)/(2+r)^2+1/2*(d*x^2+2*e*x^(2+r)/(2+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:482
  public void test0388() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])/x^4, x]", //
        "-1/9*b*d*n/x^3-b*e*n*x^(-3+r)/(3-r)^2-1/3*d*(a+b*Log[c*x^n])/x^3-e*x^(-3+r)*(a+b*Log[c*x^n])/(3-r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:490
  public void test0389() {
    check( //
        "Integrate[x^4*(d+e*x^r)^2*(a+b*Log[c*x^n]), x]", //
        "-1/25*b*d^2*n*x^5-2*b*d*e*n*x^(5+r)/(5+r)^2-b*e^2*n*x^(5+2*r)/(5+2*r)^2+1/5*(d^2*x^5+10*d*e*x^(5+r)/(5+r)+5*e^2*x^(5+2*r)/(5+2*r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:498
  public void test0390() {
    check( //
        "Integrate[x^3*(d+e*x^r)^3*(a+b*Log[c*x^n]), x]", //
        "-1/16*b*d^3*n*x^4-3/4*b*d*e^2*n*x^(2*(2+r))/(2+r)^2-3*b*d^2*e*n*x^(4+r)/(4+r)^2-b*e^3*n*x^(4+3*r)/(4+3*r)^2+1/4*(d^3*x^4+6*d*e^2*x^(2*(2+r))/(2+r)+12*d^2*e*x^(4+r)/(4+r)+4*e^3*x^(4+3*r)/(4+3*r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:506
  public void test0391() {
    check( //
        "Integrate[(d+e*x^r)^3*(a+b*Log[c*x^n])/x^2, x]", //
        "-b*d^3*n/x-3*b*d^2*e*n*x^(-1+r)/(1-r)^2-3*b*d*e^2*n*x^(-1+2*r)/(1-2*r)^2-b*e^3*n*x^(-1+3*r)/(1-3*r)^2-d^3*(a+b*Log[c*x^n])/x-3*d^2*e*x^(-1+r)*(a+b*Log[c*x^n])/(1-r)-3*d*e^2*x^(-1+2*r)*(a+b*Log[c*x^n])/(1-2*r)-e^3*x^(-1+3*r)*(a+b*Log[c*x^n])/(1-3*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:540
  public void test0392() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])^2/x, x]", //
        "2*b^2*e*n^2*x^r/r^3-2*b*e*n*x^r*(a+b*Log[c*x^n])/r^2+e*x^r*(a+b*Log[c*x^n])^2/r+1/3*d*(a+b*Log[c*x^n])^3/(b*n)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:42
  public void test0393() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*x^2)]/x^3, x]", //
        "1/2*b*d*f*n*Log[x]-1/2*b*d*f*n*Log[x]^2+d*f*Log[x]*(a+b*Log[c*x^n])-1/4*b*d*f*n*Log[1+d*f*x^2]-1/4*b*n*Log[1+d*f*x^2]/x^2-1/2*d*f*(a+b*Log[c*x^n])*Log[1+d*f*x^2]-1/2*(a+b*Log[c*x^n])*Log[1+d*f*x^2]/x^2-1/4*b*d*f*n*PolyLog[2,-d*f*x^2]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:50
  public void test0394() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(1/d+f*x^2)]/x^3, x]", //
        "1/2*b^2*d*f*n^2*Log[x]-1/2*b*d*f*n*Log[1+1/(d*f*x^2)]*(a+b*Log[c*x^n])-1/2*d*f*Log[1+1/(d*f*x^2)]*(a+b*Log[c*x^n])^2-1/4*b^2*d*f*n^2*Log[1+d*f*x^2]-1/4*b^2*n^2*Log[1+d*f*x^2]/x^2-1/2*b*n*(a+b*Log[c*x^n])*Log[1+d*f*x^2]/x^2-1/2*(a+b*Log[c*x^n])^2*Log[1+d*f*x^2]/x^2+1/4*b^2*d*f*n^2*PolyLog[2,(-1)/(d*f*x^2)]+1/2*b*d*f*n*(a+b*Log[c*x^n])*PolyLog[2,(-1)/(d*f*x^2)]+1/4*b^2*d*f*n^2*PolyLog[3,(-1)/(d*f*x^2)]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:58
  public void test0395() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(1/d+f*x^2)]/x^3, x]", //
        "3/4*b^3*d*f*n^3*Log[x]-3/4*b^2*d*f*n^2*Log[1+1/(d*f*x^2)]*(a+b*Log[c*x^n])-3/4*b*d*f*n*Log[1+1/(d*f*x^2)]*(a+b*Log[c*x^n])^2-1/2*d*f*Log[1+1/(d*f*x^2)]*(a+b*Log[c*x^n])^3-3/8*b^3*d*f*n^3*Log[1+d*f*x^2]-3/8*b^3*n^3*Log[1+d*f*x^2]/x^2-3/4*b^2*n^2*(a+b*Log[c*x^n])*Log[1+d*f*x^2]/x^2-3/4*b*n*(a+b*Log[c*x^n])^2*Log[1+d*f*x^2]/x^2-1/2*(a+b*Log[c*x^n])^3*Log[1+d*f*x^2]/x^2+3/8*b^3*d*f*n^3*PolyLog[2,(-1)/(d*f*x^2)]+3/4*b^2*d*f*n^2*(a+b*Log[c*x^n])*PolyLog[2,(-1)/(d*f*x^2)]+3/4*b*d*f*n*(a+b*Log[c*x^n])^2*PolyLog[2,(-1)/(d*f*x^2)]+3/8*b^3*d*f*n^3*PolyLog[3,(-1)/(d*f*x^2)]+3/4*b^2*d*f*n^2*(a+b*Log[c*x^n])*PolyLog[3,(-1)/(d*f*x^2)]+3/8*b^3*d*f*n^3*PolyLog[4,(-1)/(d*f*x^2)]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:68
  public void test0396() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*Sqrt[x])]/x^3, x]", //
        "-7/36*b*d*f*n/x^(3/2)+3/8*b*d^2*f^2*n/x-1/8*b*d^4*f^4*n*Log[x]+1/8*b*d^4*f^4*n*Log[x]^2-1/6*d*f*(a+b*Log[c*x^n])/x^(3/2)+1/4*d^2*f^2*(a+b*Log[c*x^n])/x-1/4*d^4*f^4*Log[x]*(a+b*Log[c*x^n])+1/4*b*d^4*f^4*n*Log[1+d*f*Sqrt[x]]-1/4*b*n*Log[1+d*f*Sqrt[x]]/x^2+1/2*d^4*f^4*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]-1/2*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]/x^2+b*d^4*f^4*n*PolyLog[2,-d*f*Sqrt[x]]-5/4*b*d^3*f^3*n/Sqrt[x]-1/2*d^3*f^3*(a+b*Log[c*x^n])/Sqrt[x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:100
  public void test0397() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^4, x]", //
        "-5/36*b*f*m*n/(e*x^2)+4/9*b*f^2*m*n/(e^2*x)+1/9*b*f^3*m*n*Log[x]/e^3-1/6*b*f^3*m*n*Log[x]^2/e^3-1/6*f*m*(a+b*Log[c*x^n])/(e*x^2)+1/3*f^2*m*(a+b*Log[c*x^n])/(e^2*x)+1/3*f^3*m*Log[x]*(a+b*Log[c*x^n])/e^3-1/9*b*f^3*m*n*Log[e+f*x]/e^3+1/3*b*f^3*m*n*Log[-f*x/e]*Log[e+f*x]/e^3-1/3*f^3*m*(a+b*Log[c*x^n])*Log[e+f*x]/e^3-1/9*b*n*Log[d*(e+f*x)^m]/x^3-1/3*(a+b*Log[c*x^n])*Log[d*(e+f*x)^m]/x^3+1/3*b*f^3*m*n*PolyLog[2,1+f*x/e]/e^3");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:118
  public void test0398() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^3, x]", //
        "1/2*b*f*m*n*Log[x]/e-1/2*b*f*m*n*Log[x]^2/e+f*m*Log[x]*(a+b*Log[c*x^n])/e-1/4*b*f*m*n*Log[e+f*x^2]/e+1/4*b*f*m*n*Log[-f*x^2/e]*Log[e+f*x^2]/e-1/2*f*m*(a+b*Log[c*x^n])*Log[e+f*x^2]/e-1/4*b*n*Log[d*(e+f*x^2)^m]/x^2-1/2*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^2+1/4*b*f*m*n*PolyLog[2,1+f*x^2/e]/e");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:126
  public void test0399() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x, x]", //
        "1/3*(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m]/(b*n)-1/3*m*(a+b*Log[c*x^n])^3*Log[1+f*x^2/e]/(b*n)-1/2*m*(a+b*Log[c*x^n])^2*PolyLog[2,-f*x^2/e]+1/2*b*m*n*(a+b*Log[c*x^n])*PolyLog[3,-f*x^2/e]-1/4*b^2*m*n^2*PolyLog[4,-f*x^2/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:134
  public void test0400() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m]/x, x]", //
        "1/4*(a+b*Log[c*x^n])^4*Log[d*(e+f*x^2)^m]/(b*n)-1/4*m*(a+b*Log[c*x^n])^4*Log[1+f*x^2/e]/(b*n)-1/2*m*(a+b*Log[c*x^n])^3*PolyLog[2,-f*x^2/e]+3/4*b*m*n*(a+b*Log[c*x^n])^2*PolyLog[3,-f*x^2/e]-3/4*b^2*m*n^2*(a+b*Log[c*x^n])*PolyLog[4,-f*x^2/e]+3/8*b^3*m*n^3*PolyLog[5,-f*x^2/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:152
  public void test0401() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])]/x, x]", //
        "1/3*(a+b*Log[c*x^n])^3*Log[d*(e+f*Sqrt[x])]/(b*n)-1/3*(a+b*Log[c*x^n])^3*Log[1+f*Sqrt[x]/e]/(b*n)-2*(a+b*Log[c*x^n])^2*PolyLog[2,-f*Sqrt[x]/e]+8*b*n*(a+b*Log[c*x^n])*PolyLog[3,-f*Sqrt[x]/e]-16*b^2*n^2*PolyLog[4,-f*Sqrt[x]/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:178
  public void test0402() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^m)^k]/x, x]", //
        "1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^m)^k]/(b*n)-1/2*k*(a+b*Log[c*x^n])^2*Log[1+f*x^m/e]/(b*n)-k*(a+b*Log[c*x^n])*PolyLog[2,-f*x^m/e]/m+b*k*n*PolyLog[3,-f*x^m/e]/m^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:202
  public void test0403() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r]), x]", //
        "2*a*b*e*n*r*x-4*b^2*e*n^2*r*x+2*b*e*n*(a-b*n)*r*x+4*b^2*e*n*r*x*Log[c*x^n]-e*r*x*(a+b*Log[c*x^n])^2-2*a*b*n*x*(d+e*Log[f*x^r])+2*b^2*n^2*x*(d+e*Log[f*x^r])-2*b^2*n*x*Log[c*x^n]*(d+e*Log[f*x^r])+x*(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:223
  public void test0404() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^p*(d+e*Log[f*x^r])/x, x]", //
        "-e*r*(a+b*Log[c*x^n])^(2+p)/(b^2*n^2*(1+p)*(2+p))+(a+b*Log[c*x^n])^(1+p)*(d+e*Log[f*x^r])/(b*n*(1+p))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:246
  public void test0405() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*PolyLog[k,e*x^q]/x, x]", //
        "(a+b*Log[c*x^n])^3*PolyLog[1+k,e*x^q]/q-3*b*n*(a+b*Log[c*x^n])^2*PolyLog[2+k,e*x^q]/q^2+6*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[3+k,e*x^q]/q^3-6*b^3*n^3*PolyLog[4+k,e*x^q]/q^4");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:259
  public void test0406() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[2,e*x], x]", //
        "3*b*n*x-x*(a+b*Log[c*x^n])+2*b*n*(1-e*x)*Log[1-e*x]/e-(1-e*x)*(a+b*Log[c*x^n])*Log[1-e*x]/e-b*n*PolyLog[2,e*x]/e-b*n*x*PolyLog[2,e*x]+x*(a+b*Log[c*x^n])*PolyLog[2,e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:267
  public void test0407() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[3,e*x]/x^2, x]", //
        "3*b*e*n*Log[x]-1/2*b*e*n*Log[x]^2+e*Log[x]*(a+b*Log[c*x^n])-3*b*e*n*Log[1-e*x]+3*b*n*Log[1-e*x]/x-e*(a+b*Log[c*x^n])*Log[1-e*x]+(a+b*Log[c*x^n])*Log[1-e*x]/x-b*e*n*PolyLog[2,e*x]-2*b*n*PolyLog[2,e*x]/x-(a+b*Log[c*x^n])*PolyLog[2,e*x]/x-b*n*PolyLog[3,e*x]/x-(a+b*Log[c*x^n])*PolyLog[3,e*x]/x");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:298
  public void test0408() {
    check( //
        "Integrate[(e*x)^q*(a+b*Log[c*(d*x^m)^n])^3, x]", //
        "-6*b^3*m^3*n^3*(e*x)^(1+q)/(e*(1+q)^4)+6*b^2*m^2*n^2*(e*x)^(1+q)*(a+b*Log[c*(d*x^m)^n])/(e*(1+q)^3)-3*b*m*n*(e*x)^(1+q)*(a+b*Log[c*(d*x^m)^n])^2/(e*(1+q)^2)+(e*x)^(1+q)*(a+b*Log[c*(d*x^m)^n])^3/(e*(1+q))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:62
  public void test0409() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(c*g+d*g*x)^2, x]", //
        "-2*A*B*n*(a+b*x)/((b*c-a*d)*g^2*(c+d*x))+2*B^2*n^2*(a+b*x)/((b*c-a*d)*g^2*(c+d*x))-2*B^2*n*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/((b*c-a*d)*g^2*(c+d*x))+(a+b*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)*g^2*(c+d*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:139
  public void test0410() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])^2/(a*g+b*g*x)^2, x]", //
        "-2*B^2*(c+d*x)/((b*c-a*d)*g^2*(a+b*x))-2*B*(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)*g^2*(a+b*x))-(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)*g^2*(a+b*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:169
  public void test0411() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/(a*g+b*g*x)^4, x]", //
        "-2/9*B/(b*g^4*(a+b*x)^3)+1/3*B*d/(b*(b*c-a*d)*g^4*(a+b*x)^2)-2/3*B*d^2/(b*(b*c-a*d)^2*g^4*(a+b*x))-2/3*B*d^3*Log[a+b*x]/(b*(b*c-a*d)^3*g^4)+1/3*(-A-B*Log[e*(a+b*x)^2/(c+d*x)^2])/(b*g^4*(a+b*x)^3)+2/3*B*d^3*Log[c+d*x]/(b*(b*c-a*d)^3*g^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:177
  public void test0412() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/(a*g+b*g*x)^3, x]", //
        "8*B^2*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))-b*B^2*(c+d*x)^2/((b*c-a*d)^2*g^3*(a+b*x)^2)+4*B*d*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^2*g^3*(a+b*x))-b*B*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^2*g^3*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^2*g^3*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^2*g^3*(a+b*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:197
  public void test0413() {
    check( //
        "Integrate[(a+b*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n]), x]", //
        "-1/4*B*(b*c-a*d)^3*n*x/d^3+1/8*B*(b*c-a*d)^2*n*(a+b*x)^2/(b*d^2)-1/12*B*(b*c-a*d)*n*(a+b*x)^3/(b*d)+1/4*B*(b*c-a*d)^4*n*Log[c+d*x]/(b*d^4)+1/4*(a+b*x)^4*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:266
  public void test0414() {
    check( //
        "Integrate[(a*g+b*g*x)^2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2]), x]", //
        "-2/3*B*(b*c-a*d)^2*g^2*x/d^2+1/3*B*(b*c-a*d)*g^2*(a+b*x)^2/(b*d)+2/3*B*(b*c-a*d)^3*g^2*Log[c+d*x]/(b*d^3)+1/3*g^2*(a+b*x)^3*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:287
  public void test0415() {
    check( //
        "Integrate[1/((a*g+b*g*x)^2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])), x]", //
        "-1/2*(c+d*x)*ExpIntegralEi[1/2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/B]/(E^(1/2*A/B)*B*(b*c-a*d)*g^2*(a+b*x)*Sqrt[e*(c+d*x)^2/(a+b*x)^2])");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:386
  public void test0416() {
    check( //
        "Integrate[A+B*Log[e*(a+b*x)^n/(c+d*x)^n], x]", //
        "A*x-B*(b*c-a*d)*n*Log[c+d*x]/(b*d)+B*(a+b*x)*Log[e*(a+b*x)^n/(c+d*x)^n]/b");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:276
  public void test0417() {
    check( //
        "Integrate[1/((a+b*x)*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])), x]", //
        "Log[A+B*Log[e*(a+b*x)^n/(c+d*x)^n]]/(B*(b*c-a*d)*n)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:284
  public void test0418() {
    check( //
        "Integrate[1/((a*c*f+(b*c+a*d)*f*x+b*d*f*x^2)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])), x]", //
        "Log[A+B*Log[e*(a+b*x)^n/(c+d*x)^n]]/(B*(b*c-a*d)*f*n)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:308
  public void test0419() {
    check( //
        "Integrate[Log[1-g*(c+d*x)/(a+b*x)]/((a+b*x)*(c+d*x)), x]", //
        "PolyLog[2,g*(c+d*x)/(a+b*x)]/(b*c-a*d)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:13
  public void test0420() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(f+g/x), x]", //
        "A*x/f+B*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/(b*f)-B*(b*c-a*d)*n*Log[c+d*x]/(b*d*f)+B*g*n*Log[f*(a+b*x)/(a*f-b*g)]*Log[g+f*x]/f^2-g*(A+B*Log[e*((a+b*x)/(c+d*x))^n])*Log[g+f*x]/f^2-B*g*n*Log[f*(c+d*x)/(c*f-d*g)]*Log[g+f*x]/f^2+B*g*n*PolyLog[2,-b*(g+f*x)/(a*f-b*g)]/f^2-B*g*n*PolyLog[2,-d*(g+f*x)/(c*f-d*g)]/f^2");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:27
  public void test0421() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(a+b*x)^2, x]", //
        "-p*r/(b*(a+b*x))+d*q*r*Log[a+b*x]/(b*(b*c-a*d))-d*q*r*Log[c+d*x]/(b*(b*c-a*d))-Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(b*(a+b*x))");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:45
  public void test0422() {
    check( //
        "Integrate[(g+h*x)*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r], x]", //
        "-1/2*(b*g-a*h)*p*r*x/b-1/2*(d*g-c*h)*q*r*x/d-1/4*p*r*(g+h*x)^2/h-1/4*q*r*(g+h*x)^2/h-1/2*(b*g-a*h)^2*p*r*Log[a+b*x]/(b^2*h)-1/2*(d*g-c*h)^2*q*r*Log[c+d*x]/(d^2*h)+1/2*(g+h*x)^2*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/h");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:63
  public void test0423() {
    check( //
        "Integrate[(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])^3/(1-c^2*x^2), x]", //
        "-1/4*(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])^4/(b*c)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:121
  public void test0424() {
    check( //
        "Integrate[Log[c*x/(a+b*x)]/(a+b*x), x]", //
        "-Log[a/(a+b*x)]*Log[c*x/(a+b*x)]/b-PolyLog[2,1-a/(a+b*x)]/b");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:129
  public void test0425() {
    check( //
        "Integrate[Log[c*(b+a*x)^2/x^2], x]", //
        "2*b*Log[b+a*x]/a+x*Log[c*(b+a*x)^2/x^2]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:23
  public void test0426() {
    check( //
        "Integrate[1/Log[c*(d+e*x)]^(1/2), x]", //
        "Erfi[Sqrt[Log[c*(d+e*x)]]]*Sqrt[Pi]/(c*e)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:37
  public void test0427() {
    check( //
        "Integrate[a+b*Log[c*(d+e*x)^n], x]", //
        "a*x-b*n*x+b*(d+e*x)*Log[c*(d+e*x)^n]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:65
  public void test0428() {
    check( //
        "Integrate[(f+g*x)^3*(a+b*Log[c*(d+e*x)^n]), x]", //
        "-1/4*b*(e*f-d*g)^3*n*x/e^3-1/8*b*(e*f-d*g)^2*n*(f+g*x)^2/(e^2*g)-1/12*b*(e*f-d*g)*n*(f+g*x)^3/(e*g)-1/16*b*n*(f+g*x)^4/g-1/4*b*(e*f-d*g)^4*n*Log[d+e*x]/(e^4*g)+1/4*(f+g*x)^4*(a+b*Log[c*(d+e*x)^n])/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:81
  public void test0429() {
    check( //
        "Integrate[(f+g*x)^3*(a+b*Log[c*(d+e*x)^n])^3, x]", //
        "6*a*b^2*(e*f-d*g)^3*n^2*x/e^3-6*b^3*(e*f-d*g)^3*n^3*x/e^3-9/8*b^3*g*(e*f-d*g)^2*n^3*(d+e*x)^2/e^4-2/9*b^3*g^2*(e*f-d*g)*n^3*(d+e*x)^3/e^4-3/128*b^3*g^3*n^3*(d+e*x)^4/e^4+6*b^3*(e*f-d*g)^3*n^2*(d+e*x)*Log[c*(d+e*x)^n]/e^4+9/4*b^2*g*(e*f-d*g)^2*n^2*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])/e^4+2/3*b^2*g^2*(e*f-d*g)*n^2*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])/e^4+3/32*b^2*g^3*n^2*(d+e*x)^4*(a+b*Log[c*(d+e*x)^n])/e^4-3*b*(e*f-d*g)^3*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e^4-9/4*b*g*(e*f-d*g)^2*n*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^2/e^4-b*g^2*(e*f-d*g)*n*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])^2/e^4-3/16*b*g^3*n*(d+e*x)^4*(a+b*Log[c*(d+e*x)^n])^2/e^4+(e*f-d*g)^3*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e^4+3/2*g*(e*f-d*g)^2*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^3/e^4+g^2*(e*f-d*g)*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])^3/e^4+1/4*g^3*(d+e*x)^4*(a+b*Log[c*(d+e*x)^n])^3/e^4");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:89
  public void test0430() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x)^n])^4, x]", //
        "-24*a*b^3*(e*f-d*g)*n^3*x/e+24*b^4*(e*f-d*g)*n^4*x/e+3/4*b^4*g*n^4*(d+e*x)^2/e^2-24*b^4*(e*f-d*g)*n^3*(d+e*x)*Log[c*(d+e*x)^n]/e^2-3/2*b^3*g*n^3*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])/e^2+12*b^2*(e*f-d*g)*n^2*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e^2+3/2*b^2*g*n^2*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^2/e^2-4*b*(e*f-d*g)*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e^2-b*g*n*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^3/e^2+(e*f-d*g)*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^4/e^2+1/2*g*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^4/e^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:97
  public void test0431() {
    check( //
        "Integrate[Log[a+b*x+c*x]^2, x]", //
        "2*x-2*(a+(b+c)*x)*Log[a+(b+c)*x]/(b+c)+(a+(b+c)*x)*Log[a+(b+c)*x]^2/(b+c)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:125
  public void test0432() {
    check( //
        "Integrate[(f+g*x)^3/(a+b*Log[c*(d+e*x)^n])^2, x]", //
        "(e*f-d*g)^3*(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b^2*e^4*n^2*(c*(d+e*x)^n)^(1/n))+6*g*(e*f-d*g)^2*(d+e*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(2*a/(b*n))*b^2*e^4*n^2*(c*(d+e*x)^n)^(2/n))+9*g^2*(e*f-d*g)*(d+e*x)^3*ExpIntegralEi[3*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(3*a/(b*n))*b^2*e^4*n^2*(c*(d+e*x)^n)^(3/n))+4*g^3*(d+e*x)^4*ExpIntegralEi[4*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(4*a/(b*n))*b^2*e^4*n^2*(c*(d+e*x)^n)^(4/n))-(d+e*x)*(f+g*x)^3/(b*e*n*(a+b*Log[c*(d+e*x)^n]))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:348
  public void test0433() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/(x^3*(f+g*x)^2), x]", //
        "-1/2*b*e*n/(d*f^2*x)-1/2*b*e^2*n*Log[x]/(d^2*f^2)-2*b*e*g*n*Log[x]/(d*f^3)+1/2*b*e^2*n*Log[d+e*x]/(d^2*f^2)+2*b*e*g*n*Log[d+e*x]/(d*f^3)-b*e*g^2*n*Log[d+e*x]/(f^3*(e*f-d*g))+1/2*(-a-b*Log[c*(d+e*x)^n])/(f^2*x^2)+2*g*(a+b*Log[c*(d+e*x)^n])/(f^3*x)+g^2*(a+b*Log[c*(d+e*x)^n])/(f^3*(f+g*x))+3*g^2*Log[-e*x/d]*(a+b*Log[c*(d+e*x)^n])/f^4+b*e*g^2*n*Log[f+g*x]/(f^3*(e*f-d*g))-3*g^2*(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/f^4-3*b*g^2*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/f^4+3*b*g^2*n*PolyLog[2,1+e*x/d]/f^4");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:378
  public void test0434() {
    check( //
        "Integrate[Log[2*e/(e+f*x)]/(e^2-f^2*x^2), x]", //
        "1/2*PolyLog[2,1-2*e/(e+f*x)]/(e*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:418
  public void test0435() {
    check( //
        "Integrate[(f+g/x)*x*(a+b*Log[c*(d+e*x)^n]), x]", //
        "1/2*b*(d*f-e*g)*n*x/e-1/4*b*n*(g+f*x)^2/f-1/2*b*(d*f-e*g)^2*n*Log[d+e*x]/(e^2*f)+1/2*(g+f*x)^2*(a+b*Log[c*(d+e*x)^n])/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:469
  public void test0436() {
    check( //
        "Integrate[Log[(-d+a*c*d+a*c*e*x^m)/(e*x^m)]/(x*(d+e*x^m)), x]", //
        "PolyLog[2,(1-a*c)*(e+d/x^m)/e]/(d*m)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:481
  public void test0437() {
    check( //
        "Integrate[Log[c*(a+b*x)^n]^2/(d*x+e*x^2), x]", //
        "Log[-b*x/a]*Log[c*(a+b*x)^n]^2/d-Log[c*(a+b*x)^n]^2*Log[b*(d+e*x)/(b*d-a*e)]/d-2*n*Log[c*(a+b*x)^n]*PolyLog[2,-e*(a+b*x)/(b*d-a*e)]/d+2*n*Log[c*(a+b*x)^n]*PolyLog[2,1+b*x/a]/d+2*n^2*PolyLog[3,-e*(a+b*x)/(b*d-a*e)]/d-2*n^2*PolyLog[3,1+b*x/a]/d");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:491
  public void test0438() {
    check( //
        "Integrate[Log[x]/(a+b*x+c*x^2), x]", //
        "Log[x]*Log[1+2*c*x/(b-Sqrt[b^2-4*a*c])]/Sqrt[b^2-4*a*c]-Log[x]*Log[1+2*c*x/(b+Sqrt[b^2-4*a*c])]/Sqrt[b^2-4*a*c]+PolyLog[2,-2*c*x/(b-Sqrt[b^2-4*a*c])]/Sqrt[b^2-4*a*c]-PolyLog[2,-2*c*x/(b+Sqrt[b^2-4*a*c])]/Sqrt[b^2-4*a*c]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:507
  public void test0439() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])/x, x]", //
        "1/2*Log[f*x^m]^2*(a+b*Log[c*(d+e*x)^n])/m-1/2*b*n*Log[f*x^m]^2*Log[1+e*x/d]/m-b*n*Log[f*x^m]*PolyLog[2,-e*x/d]+b*m*n*PolyLog[3,-e*x/d]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:537
  public void test0440() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])*(f+g*Log[c*(d+e*x)^n])/x, x]", //
        "Log[x]*(a+b*Log[c*(d+e*x)^n])*(f+g*Log[c*(d+e*x)^n])-1/4*Log[x]*(b*f+a*g+2*b*g*Log[c*(d+e*x)^n])^2/(b*g)+1/4*Log[-e*x/d]*(b*f+a*g+2*b*g*Log[c*(d+e*x)^n])^2/(b*g)+n*(b*f+a*g+2*b*g*Log[c*(d+e*x)^n])*PolyLog[2,1+e*x/d]-2*b*g*n^2*PolyLog[3,1+e*x/d]");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:585
  public void test0441() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^m)^n])^3, x]", //
        "1/2*(e+f*x)*ExpIntegralEi[(a+b*Log[c*(d*(e+f*x)^m)^n])/(b*m*n)]/(E^(a/(b*m*n))*b^3*f*m^3*n^3*(c*(d*(e+f*x)^m)^n)^(1/(m*n)))+1/2*(-e-f*x)/(b*f*m*n*(a+b*Log[c*(d*(e+f*x)^m)^n])^2)+1/2*(-e-f*x)/(b^2*f*m^2*n^2*(a+b*Log[c*(d*(e+f*x)^m)^n]))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:611
  public void test0442() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])/(g+h*x)^3, x]", //
        "1/2*b*f*p*q/(h*(f*g-e*h)*(g+h*x))+1/2*b*f^2*p*q*Log[e+f*x]/(h*(f*g-e*h)^2)+1/2*(-a-b*Log[c*(d*(e+f*x)^p)^q])/(h*(g+h*x)^2)-1/2*b*f^2*p*q*Log[g+h*x]/(h*(f*g-e*h)^2)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:619
  public void test0443() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^2/(g+h*x)^3, x]", //
        "-b*f*p*q*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])/((f*g-e*h)^2*(g+h*x))-1/2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/(h*(g+h*x)^2)+b^2*f^2*p^2*q^2*Log[g+h*x]/(h*(f*g-e*h)^2)-b*f^2*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])*Log[1+(f*g-e*h)/(h*(e+f*x))]/(h*(f*g-e*h)^2)+b^2*f^2*p^2*q^2*PolyLog[2,(-f*g+e*h)/(h*(e+f*x))]/(h*(f*g-e*h)^2)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:627
  public void test0444() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^4/(g+h*x), x]", //
        "(a+b*Log[c*(d*(e+f*x)^p)^q])^4*Log[f*(g+h*x)/(f*g-e*h)]/h+4*b*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])^3*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/h-12*b^2*p^2*q^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2*PolyLog[3,-h*(e+f*x)/(f*g-e*h)]/h+24*b^3*p^3*q^3*(a+b*Log[c*(d*(e+f*x)^p)^q])*PolyLog[4,-h*(e+f*x)/(f*g-e*h)]/h-24*b^4*p^4*q^4*PolyLog[5,-h*(e+f*x)/(f*g-e*h)]/h");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:27
  public void test0445() {
    check( //
        "Integrate[x^2*Log[c*(a+b*x^3)^p], x]", //
        "-1/3*p*x^3+1/3*(a+b*x^3)*Log[c*(a+b*x^3)^p]/b");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:35
  public void test0446() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/x^6, x]", //
        "-3/10*b*p/(a*x^2)-1/5*b^(5/3)*p*Log[a^(1/3)+b^(1/3)*x]/a^(5/3)+1/10*b^(5/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/a^(5/3)-1/5*Log[c*(a+b*x^3)^p]/x^5+1/5*b^(5/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/a^(5/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:45
  public void test0447() {
    check( //
        "Integrate[Log[c*(a+b/x)^p]/x^2, x]", //
        "p/x-(a+b/x)*Log[c*(a+b/x)^p]/b");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:53
  public void test0448() {
    check( //
        "Integrate[Log[c*(a+b/x^2)^p], x]", //
        "x*Log[c*(a+b/x^2)^p]+2*p*ArcTan[x*Sqrt[a]/Sqrt[b]]*Sqrt[b]/Sqrt[a]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:114
  public void test0449() {
    check( //
        "Integrate[x*Log[c*(a+b*x^2)^p]^2, x]", //
        "p^2*x^2-p*(a+b*x^2)*Log[c*(a+b*x^2)^p]/b+1/2*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/b");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:130
  public void test0450() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^3/x^3, x]", //
        "3/2*b*p*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]^2/a-1/2*(a+b*x^2)*Log[c*(a+b*x^2)^p]^3/(a*x^2)+3*b*p^2*Log[c*(a+b*x^2)^p]*PolyLog[2,1+b*x^2/a]/a-3*b*p^3*PolyLog[3,1+b*x^2/a]/a");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:154
  public void test0451() {
    check( //
        "Integrate[x/Log[c*(a+b*x^2)^p]^3, x]", //
        "1/4*(a+b*x^2)*ExpIntegralEi[Log[c*(a+b*x^2)^p]/p]/(b*p^3*(c*(a+b*x^2)^p)^(1/p))+1/4*(-a-b*x^2)/(b*p*Log[c*(a+b*x^2)^p]^2)+1/4*(-a-b*x^2)/(b*p^2*Log[c*(a+b*x^2)^p])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:171
  public void test0452() {
    check( //
        "Integrate[x^2*Log[c*(d+e*x^3)^p]^2, x]", //
        "2/3*p^2*x^3-2/3*p*(d+e*x^3)*Log[c*(d+e*x^3)^p]/e+1/3*(d+e*x^3)*Log[c*(d+e*x^3)^p]^2/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:181
  public void test0453() {
    check( //
        "Integrate[x^8/Log[c*(d+e*x^3)^p], x]", //
        "1/3*d^2*(d+e*x^3)*ExpIntegralEi[Log[c*(d+e*x^3)^p]/p]/(e^3*p*(c*(d+e*x^3)^p)^(1/p))-2/3*d*(d+e*x^3)^2*ExpIntegralEi[2*Log[c*(d+e*x^3)^p]/p]/(e^3*p*(c*(d+e*x^3)^p)^(2/p))+1/3*(d+e*x^3)^3*ExpIntegralEi[3*Log[c*(d+e*x^3)^p]/p]/(e^3*p*(c*(d+e*x^3)^p)^(3/p))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:210
  public void test0454() {
    check( //
        "Integrate[(f*x)^(-1+n)*Log[c*(d+e*x^n)^p]^2, x]", //
        "2*p^2*x*(f*x)^(-1+n)/n-2*p*x^(1-n)*(f*x)^(-1+n)*(d+e*x^n)*Log[c*(d+e*x^n)^p]/(e*n)+x^(1-n)*(f*x)^(-1+n)*(d+e*x^n)*Log[c*(d+e*x^n)^p]^2/(e*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:234
  public void test0455() {
    check( //
        "Integrate[Log[c*(a+b*x)^p]/(d+e*x)^2, x]", //
        "b*p*Log[a+b*x]/(e*(b*d-a*e))-Log[c*(a+b*x)^p]/(e*(d+e*x))-b*p*Log[d+e*x]/(e*(b*d-a*e))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:242
  public void test0456() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]/(d+e*x)^2, x]", //
        "-2*b*d*p*Log[d+e*x]/(e*(b*d^2+a*e^2))+b*d*p*Log[a+b*x^2]/(e*(b*d^2+a*e^2))-Log[c*(a+b*x^2)^p]/(e*(d+e*x))+2*p*ArcTan[x*Sqrt[b]/Sqrt[a]]*Sqrt[a]*Sqrt[b]/(b*d^2+a*e^2)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:260
  public void test0457() {
    check( //
        "Integrate[Log[a+b/x]/(c+d*x), x]", //
        "Log[a+b/x]*Log[c+d*x]/d+Log[-d*x/c]*Log[c+d*x]/d-Log[-d*(b+a*x)/(a*c-b*d)]*Log[c+d*x]/d-PolyLog[2,a*(c+d*x)/(a*c-b*d)]/d+PolyLog[2,1+d*x/c]/d");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:309
  public void test0458() {
    check( //
        "Integrate[x*Log[c*(a+b/x)^p]/(d+e*x), x]", //
        "x*Log[c*(a+b/x)^p]/e+b*p*Log[b+a*x]/(a*e)-d*Log[c*(a+b/x)^p]*Log[d+e*x]/e^2-d*p*Log[-e*x/d]*Log[d+e*x]/e^2+d*p*Log[-e*(b+a*x)/(a*d-b*e)]*Log[d+e*x]/e^2+d*p*PolyLog[2,a*(d+e*x)/(a*d-b*e)]/e^2-d*p*PolyLog[2,1+e*x/d]/e^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:433
  public void test0459() {
    check( //
        "Integrate[(f+g*x^2)^2*Log[c*(d+e*x^2)^p]/x^2, x]", //
        "-4*f*g*p*x+2/3*d*g^2*p*x/e-2/9*g^2*p*x^3-2/3*d^(3/2)*g^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(3/2)-f^2*Log[c*(d+e*x^2)^p]/x+2*f*g*x*Log[c*(d+e*x^2)^p]+1/3*g^2*x^3*Log[c*(d+e*x^2)^p]+4*f*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[d]/Sqrt[e]+2*f^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[e]/Sqrt[d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:443
  public void test0460() {
    check( //
        "Integrate[Log[c*(d+e*x^2)^p]/(x^3*(f+g*x^2)), x]", //
        "e*p*Log[x]/(d*f)-1/2*e*p*Log[d+e*x^2]/(d*f)-1/2*Log[c*(d+e*x^2)^p]/(f*x^2)-1/2*g*Log[-e*x^2/d]*Log[c*(d+e*x^2)^p]/f^2+1/2*g*Log[c*(d+e*x^2)^p]*Log[e*(f+g*x^2)/(e*f-d*g)]/f^2+1/2*g*p*PolyLog[2,-g*(d+e*x^2)/(e*f-d*g)]/f^2-1/2*g*p*PolyLog[2,1+e*x^2/d]/f^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:451
  public void test0461() {
    check( //
        "Integrate[x*Log[c*(d+e*x^2)^p]/(f+g*x^2)^2, x]", //
        "1/2*e*p*Log[d+e*x^2]/(g*(e*f-d*g))-1/2*Log[c*(d+e*x^2)^p]/(g*(f+g*x^2))-1/2*e*p*Log[f+g*x^2]/(g*(e*f-d*g))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:546
  public void test0462() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^3, x]", //
        "-12*a*b^2*d*n^2*Sqrt[x]/e+12*b^3*d*n^3*Sqrt[x]/e-12*b^3*d*n^2*Log[c*(d+e*Sqrt[x])^n]*(d+e*Sqrt[x])/e^2+6*b*d*n*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*(d+e*Sqrt[x])/e^2-2*d*(a+b*Log[c*(d+e*Sqrt[x])^n])^3*(d+e*Sqrt[x])/e^2-3/4*b^3*n^3*(d+e*Sqrt[x])^2/e^2+3/2*b^2*n^2*(a+b*Log[c*(d+e*Sqrt[x])^n])*(d+e*Sqrt[x])^2/e^2-3/2*b*n*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*(d+e*Sqrt[x])^2/e^2+(a+b*Log[c*(d+e*Sqrt[x])^n])^3*(d+e*Sqrt[x])^2/e^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:556
  public void test0463() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/Sqrt[x])^n])/x, x]", //
        "-2*(a+b*Log[c*(d+e/Sqrt[x])^n])*Log[-e/(d*Sqrt[x])]-2*b*n*PolyLog[2,1+e/(d*Sqrt[x])]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:564
  public void test0464() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/Sqrt[x])^n])^2/x^2, x]", //
        "-4*b^2*d*n*Log[c*(d+e/Sqrt[x])^n]*(d+e/Sqrt[x])/e^2+2*d*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])/e^2-1/2*b^2*n^2*(d+e/Sqrt[x])^2/e^2+b*n*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^2/e^2-(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^2/e^2-4*a*b*d*n/(e*Sqrt[x])+4*b^2*d*n^2/(e*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:572
  public void test0465() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/Sqrt[x])^n])^3/x^4, x]", //
        "12*b^3*d^5*n^2*Log[c*(d+e/Sqrt[x])^n]*(d+e/Sqrt[x])/e^6-6*b*d^5*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])/e^6+2*d^5*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])/e^6+15/4*b^3*d^4*n^3*(d+e/Sqrt[x])^2/e^6-15/2*b^2*d^4*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^2/e^6+15/2*b*d^4*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^2/e^6-5*d^4*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])^2/e^6-40/27*b^3*d^3*n^3*(d+e/Sqrt[x])^3/e^6+40/9*b^2*d^3*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^3/e^6-20/3*b*d^3*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^3/e^6+20/3*d^3*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])^3/e^6+15/32*b^3*d^2*n^3*(d+e/Sqrt[x])^4/e^6-15/8*b^2*d^2*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^4/e^6+15/4*b*d^2*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^4/e^6-5*d^2*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])^4/e^6-12/125*b^3*d*n^3*(d+e/Sqrt[x])^5/e^6+12/25*b^2*d*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^5/e^6-6/5*b*d*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^5/e^6+2*d*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])^5/e^6+1/108*b^3*n^3*(d+e/Sqrt[x])^6/e^6-1/18*b^2*n^2*(a+b*Log[c*(d+e/Sqrt[x])^n])*(d+e/Sqrt[x])^6/e^6+1/6*b*n*(a+b*Log[c*(d+e/Sqrt[x])^n])^2*(d+e/Sqrt[x])^6/e^6-1/3*(a+b*Log[c*(d+e/Sqrt[x])^n])^3*(d+e/Sqrt[x])^6/e^6+12*a*b^2*d^5*n^2/(e^5*Sqrt[x])-12*b^3*d^5*n^3/(e^5*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:584
  public void test0466() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])/x^4, x]", //
        "-1/24*b*e*n/(d*x^(8/3))+1/21*b*e^2*n/(d^2*x^(7/3))-1/18*b*e^3*n/(d^3*x^2)+1/15*b*e^4*n/(d^4*x^(5/3))-1/12*b*e^5*n/(d^5*x^(4/3))+1/9*b*e^6*n/(d^6*x)-1/6*b*e^7*n/(d^7*x^(2/3))+1/3*b*e^8*n/(d^8*x^(1/3))-1/3*b*e^9*n*Log[d+e*x^(1/3)]/d^9+1/3*(-a-b*Log[c*(d+e*x^(1/3))^n])/x^3+1/9*b*e^9*n*Log[x]/d^9");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:592
  public void test0467() {
    check( //
        "Integrate[x^2*(a+b*Log[c*(d+e*x^(1/3))^n])^3, x]", //
        "9*b^3*d^7*n^3*(d+e*x^(1/3))^2/e^9-56/9*b^3*d^6*n^3*(d+e*x^(1/3))^3/e^9+63/16*b^3*d^5*n^3*(d+e*x^(1/3))^4/e^9-252/125*b^3*d^4*n^3*(d+e*x^(1/3))^5/e^9+7/9*b^3*d^3*n^3*(d+e*x^(1/3))^6/e^9-72/343*b^3*d^2*n^3*(d+e*x^(1/3))^7/e^9+9/256*b^3*d*n^3*(d+e*x^(1/3))^8/e^9-2/729*b^3*n^3*(d+e*x^(1/3))^9/e^9+18*a*b^2*d^8*n^2*x^(1/3)/e^8-18*b^3*d^8*n^3*x^(1/3)/e^8+18*b^3*d^8*n^2*(d+e*x^(1/3))*Log[c*(d+e*x^(1/3))^n]/e^9-18*b^2*d^7*n^2*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9+56/3*b^2*d^6*n^2*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9-63/4*b^2*d^5*n^2*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9+252/25*b^2*d^4*n^2*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9-14/3*b^2*d^3*n^2*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9+72/49*b^2*d^2*n^2*(d+e*x^(1/3))^7*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9-9/32*b^2*d*n^2*(d+e*x^(1/3))^8*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9+2/81*b^2*n^2*(d+e*x^(1/3))^9*(a+b*Log[c*(d+e*x^(1/3))^n])/e^9-9*b*d^8*n*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9+18*b*d^7*n*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9-28*b*d^6*n*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9+63/2*b*d^5*n*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9-126/5*b*d^4*n*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9+14*b*d^3*n*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9-36/7*b*d^2*n*(d+e*x^(1/3))^7*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9+9/8*b*d*n*(d+e*x^(1/3))^8*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9-1/9*b*n*(d+e*x^(1/3))^9*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^9+3*d^8*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9-12*d^7*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9+28*d^6*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9-42*d^5*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9+42*d^4*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9-28*d^3*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9+12*d^2*(d+e*x^(1/3))^7*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9-3*d*(d+e*x^(1/3))^8*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9+1/3*(d+e*x^(1/3))^9*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^9");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:610
  public void test0468() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(2/3))^n])^2/x, x]", //
        "3/2*(a+b*Log[c*(d+e*x^(2/3))^n])^2*Log[-e*x^(2/3)/d]+3*b*n*(a+b*Log[c*(d+e*x^(2/3))^n])*PolyLog[2,1+e*x^(2/3)/d]-3*b^2*n^2*PolyLog[3,1+e*x^(2/3)/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:618
  public void test0469() {
    check( //
        "Integrate[x^3*(a+b*Log[c*(d+e*x^(2/3))^n])^3, x]", //
        "-45/16*b^3*d^4*n^3*(d+e*x^(2/3))^2/e^6+10/9*b^3*d^3*n^3*(d+e*x^(2/3))^3/e^6-45/128*b^3*d^2*n^3*(d+e*x^(2/3))^4/e^6+9/125*b^3*d*n^3*(d+e*x^(2/3))^5/e^6-1/144*b^3*n^3*(d+e*x^(2/3))^6/e^6-9*a*b^2*d^5*n^2*x^(2/3)/e^5+9*b^3*d^5*n^3*x^(2/3)/e^5-9*b^3*d^5*n^2*(d+e*x^(2/3))*Log[c*(d+e*x^(2/3))^n]/e^6+45/8*b^2*d^4*n^2*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])/e^6-10/3*b^2*d^3*n^2*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])/e^6+45/32*b^2*d^2*n^2*(d+e*x^(2/3))^4*(a+b*Log[c*(d+e*x^(2/3))^n])/e^6-9/25*b^2*d*n^2*(d+e*x^(2/3))^5*(a+b*Log[c*(d+e*x^(2/3))^n])/e^6+1/24*b^2*n^2*(d+e*x^(2/3))^6*(a+b*Log[c*(d+e*x^(2/3))^n])/e^6+9/2*b*d^5*n*(d+e*x^(2/3))*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6-45/8*b*d^4*n*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6+5*b*d^3*n*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6-45/16*b*d^2*n*(d+e*x^(2/3))^4*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6+9/10*b*d*n*(d+e*x^(2/3))^5*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6-1/8*b*n*(d+e*x^(2/3))^6*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^6-3/2*d^5*(d+e*x^(2/3))*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6+15/4*d^4*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6-5*d^3*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6+15/4*d^2*(d+e*x^(2/3))^4*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6-3/2*d*(d+e*x^(2/3))^5*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6+1/4*(d+e*x^(2/3))^6*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^6");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:632
  public void test0470() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(1/3))^n])/x, x]", //
        "-3*(a+b*Log[c*(d+e/x^(1/3))^n])*Log[-e/(d*x^(1/3))]-3*b*n*PolyLog[2,1+e/(d*x^(1/3))]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:658
  public void test0471() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e/x^(2/3))^n])^2, x]", //
        "1/2*b^2*e^2*n^2*x^(2/3)/d^2-1/2*b^2*e^3*n^2*Log[d+e/x^(2/3)]/d^3-b*e^2*n*(d+e/x^(2/3))*x^(2/3)*(a+b*Log[c*(d+e/x^(2/3))^n])/d^3+1/2*b*e*n*x^(4/3)*(a+b*Log[c*(d+e/x^(2/3))^n])/d-b*e^3*n*Log[1-d/(d+e/x^(2/3))]*(a+b*Log[c*(d+e/x^(2/3))^n])/d^3+1/2*x^2*(a+b*Log[c*(d+e/x^(2/3))^n])^2-b^2*e^3*n^2*Log[x]/d^3+b^2*e^3*n^2*PolyLog[2,d/(d+e/x^(2/3))]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:668
  public void test0472() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e/x^(2/3))^n])^3, x]", //
        "3/2*b^2*e^2*n^2*(d+e/x^(2/3))*x^(2/3)*(a+b*Log[c*(d+e/x^(2/3))^n])/d^3+3/2*b^2*e^3*n^2*Log[1-d/(d+e/x^(2/3))]*(a+b*Log[c*(d+e/x^(2/3))^n])/d^3-3/2*b*e^2*n*(d+e/x^(2/3))*x^(2/3)*(a+b*Log[c*(d+e/x^(2/3))^n])^2/d^3+3/4*b*e*n*x^(4/3)*(a+b*Log[c*(d+e/x^(2/3))^n])^2/d-3/2*b*e^3*n*Log[1-d/(d+e/x^(2/3))]*(a+b*Log[c*(d+e/x^(2/3))^n])^2/d^3+1/2*x^2*(a+b*Log[c*(d+e/x^(2/3))^n])^3+3*b^2*e^3*n^2*(a+b*Log[c*(d+e/x^(2/3))^n])*Log[-e/(d*x^(2/3))]/d^3+b^3*e^3*n^3*Log[x]/d^3-3/2*b^3*e^3*n^3*PolyLog[2,d/(d+e/x^(2/3))]/d^3+3*b^2*e^3*n^2*(a+b*Log[c*(d+e/x^(2/3))^n])*PolyLog[2,d/(d+e/x^(2/3))]/d^3+3*b^3*e^3*n^3*PolyLog[2,1+e/(d*x^(2/3))]/d^3+3*b^3*e^3*n^3*PolyLog[3,d/(d+e/x^(2/3))]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:790
  public void test0473() {
    check( //
        "Integrate[Log[f*x^p]*Log[1+e*x^m]/x, x]", //
        "-Log[f*x^p]*PolyLog[2,-e*x^m]/m+p*PolyLog[3,-e*x^m]/m^2");
  }

  // 3.5 Logarithm functions.input:27
  public void test0474() {
    check( //
        "Integrate[(a*m*x^m+b*n*q*Log[c*x^n]^(-1+q))*(a*x^m+b*Log[c*x^n]^q)^p/x, x]", //
        "(a*x^m+b*Log[c*x^n]^q)^(1+p)/(1+p)");
  }

  // 3.5 Logarithm functions.input:35
  public void test0475() {
    check( //
        "Integrate[(a/x+2*b*n*Log[c*x^n]/x^2)*(a*x^2+b*x*Log[c*x^n]^2), x]", //
        "1/2*(a*x+b*Log[c*x^n]^2)^2");
  }

  // 3.5 Logarithm functions.input:74
  public void test0476() {
    check( //
        "Integrate[(a+b*Log[c*Log[d*x^n]^p])/x^3, x]", //
        "1/2*b*p*(d*x^n)^(2/n)*ExpIntegralEi[-2*Log[d*x^n]/n]/x^2+1/2*(-a-b*Log[c*Log[d*x^n]^p])/x^2");
  }

  // 3.5 Logarithm functions.input:96
  public void test0477() {
    check( //
        "Integrate[x^4*Log[d*(a+b*x+c*x^2)^n], x]", //
        "-1/5*(b^4-4*a*b^2*c+2*a^2*c^2)*n*x/c^4+1/10*b*(b^2-3*a*c)*n*x^2/c^3-1/15*(b^2-2*a*c)*n*x^3/c^2+1/20*b*n*x^4/c-2/25*n*x^5+1/10*b*(b^4-5*a*b^2*c+5*a^2*c^2)*n*Log[a+b*x+c*x^2]/c^5+1/5*x^5*Log[d*(a+b*x+c*x^2)^n]+1/5*(b^4-3*a*b^2*c+a^2*c^2)*n*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]*Sqrt[b^2-4*a*c]/c^5");
  }

  // 3.5 Logarithm functions.input:179
  public void test0478() {
    check( //
        "Integrate[Sin[Log[x]]^2/x, x]", //
        "1/2*Log[x]-1/2*Cos[Log[x]]*Sin[Log[x]]");
  }

  // 3.5 Logarithm functions.input:209
  public void test0479() {
    check( //
        "Integrate[Log[a*Cos[x]], x]", //
        "1/2*I*x^2-x*Log[1+E^(2*I*x)]+x*Log[a*Cos[x]]+1/2*I*PolyLog[2,-E^(2*I*x)]");
  }

  // 3.5 Logarithm functions.input:322
  public void test0480() {
    check( //
        "Integrate[1/(a*x+b*x*Log[c*x^n]^4), x]", //
        "-1/2*ArcTan[1-b^(1/4)*Log[c*x^n]*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(1/4)*n*Sqrt[2])+1/2*ArcTan[1+b^(1/4)*Log[c*x^n]*Sqrt[2]/a^(1/4)]/(a^(3/4)*b^(1/4)*n*Sqrt[2])-1/4*Log[-a^(1/4)*b^(1/4)*Log[c*x^n]*Sqrt[2]+Sqrt[a]+Log[c*x^n]^2*Sqrt[b]]/(a^(3/4)*b^(1/4)*n*Sqrt[2])+1/4*Log[a^(1/4)*b^(1/4)*Log[c*x^n]*Sqrt[2]+Sqrt[a]+Log[c*x^n]^2*Sqrt[b]]/(a^(3/4)*b^(1/4)*n*Sqrt[2])");
  }

  // 3.5 Logarithm functions.input:330
  public void test0481() {
    check( //
        "Integrate[(-1+Log[3*x]^2)/(x+x*Log[3*x]+x*Log[3*x]^2), x]", //
        "Log[x]-1/2*Log[1+Log[3*x]+Log[3*x]^2]-ArcTan[(1+2*Log[3*x])/Sqrt[3]]*Sqrt[3]");
  }

  // 3.5 Logarithm functions.input:338
  public void test0482() {
    check( //
        "Integrate[x*Log[1-a-b*x]/(a+b*x), x]", //
        "-x/b-(1-a-b*x)*Log[1-a-b*x]/b^2+a*PolyLog[2,a+b*x]/b^2");
  }

  // 3.5 Logarithm functions.input:346
  public void test0483() {
    check( //
        "Integrate[Log[c*(1+x^2)^n]/(1+x^2), x]", //
        "I*n*ArcTan[x]^2+2*n*ArcTan[x]*Log[2/(1+I*x)]+ArcTan[x]*Log[c*(1+x^2)^n]+I*n*PolyLog[2,1+(-2)/(1+I*x)]");
  }

  // 3.5 Logarithm functions.input:382
  public void test0484() {
    check( //
        "Integrate[Log[Log[x]*Sin[x]], x]", //
        "1/2*I*x^2-LogIntegral[x]-x*Log[1-E^(2*I*x)]+x*Log[Log[x]*Sin[x]]+1/2*I*PolyLog[2,E^(2*I*x)]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:26
  public void test0485() {
    check( //
        "Integrate[x*Log[c*x]^3, x]", //
        "-3/8*x^2+3/4*x^2*Log[c*x]-3/4*x^2*Log[c*x]^2+1/2*x^2*Log[c*x]^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:36
  public void test0486() {
    check( //
        "Integrate[1/Log[c*x], x]", //
        "LogIntegral[c*x]/c");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:52
  public void test0487() {
    check( //
        "Integrate[1/(x^2*Log[c*x]^3), x]", //
        "1/2*c*ExpIntegralEi[-Log[c*x]]+(-1/2)/(x*Log[c*x]^2)+1/2/(x*Log[c*x])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:72
  public void test0488() {
    check( //
        "Integrate[x^3*(a+b*Log[c*x^n])^3, x]", //
        "-3/128*b^3*n^3*x^4+3/32*b^2*n^2*x^4*(a+b*Log[c*x^n])-3/16*b*n*x^4*(a+b*Log[c*x^n])^2+1/4*x^4*(a+b*Log[c*x^n])^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:90
  public void test0489() {
    check( //
        "Integrate[x^3/(a+b*Log[c*x^n])^2, x]", //
        "4*x^4*ExpIntegralEi[4*(a+b*Log[c*x^n])/(b*n)]/(E^(4*a/(b*n))*b^2*n^2*(c*x^n)^(4/n))-x^4/(b*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:98
  public void test0490() {
    check( //
        "Integrate[x^3/(a+b*Log[c*x^n])^3, x]", //
        "8*x^4*ExpIntegralEi[4*(a+b*Log[c*x^n])/(b*n)]/(E^(4*a/(b*n))*b^3*n^3*(c*x^n)^(4/n))-1/2*x^4/(b*n*(a+b*Log[c*x^n])^2)-2*x^4/(b^2*n^2*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:118
  public void test0491() {
    check( //
        "Integrate[(d*x)^(1/2)*(a+b*Log[c*x^n])^2, x]", //
        "16/27*b^2*n^2*(d*x)^(3/2)/d-8/9*b*n*(d*x)^(3/2)*(a+b*Log[c*x^n])/d+2/3*(d*x)^(3/2)*(a+b*Log[c*x^n])^2/d");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:140
  public void test0492() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^(1/2), x]", //
        "-1/2*x*Erfi[Sqrt[a+b*Log[c*x^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]*Sqrt[b]*Sqrt[n]/(E^(a/(b*n))*(c*x^n)^(1/n))+x*Sqrt[a+b*Log[c*x^n]]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:148
  public void test0493() {
    check( //
        "Integrate[x^3*Log[a*x^n]^(3/2), x]", //
        "1/4*x^4*Log[a*x^n]^(3/2)+3/128*n^(3/2)*x^4*Erfi[2*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(a*x^n)^(4/n)-3/32*n*x^4*Sqrt[Log[a*x^n]]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:166
  public void test0494() {
    check( //
        "Integrate[x/Log[a*x^n]^(3/2), x]", //
        "2*x^2*Erfi[Sqrt[2]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[2*Pi]/(n^(3/2)*(a*x^n)^(2/n))-2*x^2/(n*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:174
  public void test0495() {
    check( //
        "Integrate[1/Log[a*x^n]^(5/2), x]", //
        "-2/3*x/(n*Log[a*x^n]^(3/2))+4/3*x*Erfi[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(n^(5/2)*(a*x^n)^(1/n))-4/3*x/(n^2*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:192
  public void test0496() {
    check( //
        "Integrate[(d*x)^(-1+n)/Log[c*x^n]^3, x]", //
        "1/2*x^(1-n)*(d*x)^(-1+n)*LogIntegral[c*x^n]/(c*n)-1/2*(d*x)^n/(d*n*Log[c*x^n]^2)-1/2*(d*x)^n/(d*n*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:20
  public void test0497() {
    check( //
        "Integrate[x^3*(d+e*x)^2*(a+b*Log[c*x^n]), x]", //
        "-1/16*b*d^2*n*x^4-2/25*b*d*e*n*x^5-1/36*b*e^2*n*x^6+1/60*(15*d^2*x^4+24*d*e*x^5+10*e^2*x^6)*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:36
  public void test0498() {
    check( //
        "Integrate[(d+e*x)^3*(a+b*Log[c*x^n])/x^3, x]", //
        "-1/4*b*d^3*n/x^2-3*b*d^2*e*n/x-b*e^3*n*x-3/2*b*d*e^2*n*Log[x]^2-1/2*d^3*(a+b*Log[c*x^n])/x^2-3*d^2*e*(a+b*Log[c*x^n])/x+e^3*x*(a+b*Log[c*x^n])+3*d*e^2*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:46
  public void test0499() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x), x]", //
        "a*x/e-b*n*x/e+b*x*Log[c*x^n]/e-d*(a+b*Log[c*x^n])*Log[1+e*x/d]/e^2-b*d*n*PolyLog[2,-e*x/d]/e^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:63
  public void test0500() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x)^3), x]", //
        "-1/2*b*n/(d^2*(d+e*x))-1/2*b*n*Log[x]/d^3+1/2*(a+b*Log[c*x^n])/(d*(d+e*x)^2)-e*x*(a+b*Log[c*x^n])/(d^3*(d+e*x))-Log[1+d/(e*x)]*(a+b*Log[c*x^n])/d^3+3/2*b*n*Log[d+e*x]/d^3+b*n*PolyLog[2,-d/(e*x)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:71
  public void test0501() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d+e*x)^4, x]", //
        "1/6*b*n/(d*e*(d+e*x)^2)+1/3*b*n/(d^2*e*(d+e*x))+1/3*b*n*Log[x]/(d^3*e)+1/3*(-a-b*Log[c*x^n])/(e*(d+e*x)^3)-1/3*b*n*Log[d+e*x]/(d^3*e)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:99
  public void test0502() {
    check( //
        "Integrate[(d+e*x)*(a+b*Log[c*x^n])^2/x^4, x]", //
        "-2/27*b^2*d*n^2/x^3-1/4*b^2*e*n^2/x^2-2/9*b*d*n*(a+b*Log[c*x^n])/x^3-1/2*b*e*n*(a+b*Log[c*x^n])/x^2-1/3*d*(a+b*Log[c*x^n])^2/x^3-1/2*e*(a+b*Log[c*x^n])^2/x^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:107
  public void test0503() {
    check( //
        "Integrate[(d+e*x)^2*(a+b*Log[c*x^n])^2/x^4, x]", //
        "-2/27*b^2*d^2*n^2/x^3-1/2*b^2*d*e*n^2/x^2-2*b^2*e^2*n^2/x-2/9*b*d^2*n*(a+b*Log[c*x^n])/x^3-b*d*e*n*(a+b*Log[c*x^n])/x^2-2*b*e^2*n*(a+b*Log[c*x^n])/x-1/3*d^2*(a+b*Log[c*x^n])^2/x^3-d*e*(a+b*Log[c*x^n])^2/x^2-e^2*(a+b*Log[c*x^n])^2/x");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:117
  public void test0504() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x^3*(d+e*x)), x]", //
        "-1/4*b^2*n^2/(d*x^2)+2*b^2*e*n^2/(d^2*x)-1/2*b*n*(a+b*Log[c*x^n])/(d*x^2)+2*b*e*n*(a+b*Log[c*x^n])/(d^2*x)-1/2*(a+b*Log[c*x^n])^2/(d*x^2)+e*(a+b*Log[c*x^n])^2/(d^2*x)-e^2*Log[1+d/(e*x)]*(a+b*Log[c*x^n])^2/d^3+2*b*e^2*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x)]/d^3+2*b^2*e^2*n^2*PolyLog[3,-d/(e*x)]/d^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:248
  public void test0505() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*Log[c*x^n])/x^8, x]", //
        "-1/49*b*d^2*n/x^7-2/25*b*d*e*n/x^5-1/9*b*e^2*n/x^3-1/7*d^2*(a+b*Log[c*x^n])/x^7-2/5*d*e*(a+b*Log[c*x^n])/x^5-1/3*e^2*(a+b*Log[c*x^n])/x^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:266
  public void test0506() {
    check( //
        "Integrate[x^3*(a+b*Log[c*x^n])/(d+e*x^2), x]", //
        "-1/4*b*n*x^2/e+1/2*x^2*(a+b*Log[c*x^n])/e-1/2*d*(a+b*Log[c*x^n])*Log[1+e*x^2/d]/e^2-1/4*b*d*n*PolyLog[2,-e*x^2/d]/e^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:290
  public void test0507() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x^3*(d+e*x^2)^3), x]", //
        "-3/4*b*n/(d^3*x^2)+1/4*(a+b*Log[c*x^n])/(d*x^2*(d+e*x^2)^2)+1/8*(6*a-b*n+6*b*Log[c*x^n])/(d^2*x^2*(d+e*x^2))+1/8*(-12*a+5*b*n-12*b*Log[c*x^n])/(d^3*x^2)+1/8*e*Log[1+d/(e*x^2)]*(12*a-5*b*n+12*b*Log[c*x^n])/d^4-3/4*b*e*n*PolyLog[2,-d/(e*x^2)]/d^4");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:330
  public void test0508() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])*Sqrt[d+e*x^2], x]", //
        "-1/9*b*n*(d+e*x^2)^(3/2)/e+1/3*b*d^(3/2)*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]/e+1/3*(d+e*x^2)^(3/2)*(a+b*Log[c*x^n])/e-1/3*b*d*n*Sqrt[d+e*x^2]/e");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:372
  public void test0509() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(d+e*x^2)^(3/2), x]", //
        "-b*n*ArcTanh[x*Sqrt[e]/Sqrt[d+e*x^2]]/(d*Sqrt[e])+x*(a+b*Log[c*x^n])/(d*Sqrt[d+e*x^2])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:380
  public void test0510() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x^2)^(5/2)), x]", //
        "4/3*b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]/d^(5/2)+1/2*b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]^2/d^(5/2)-b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]*Log[2*Sqrt[d]/(Sqrt[d]-Sqrt[d+e*x^2])]/d^(5/2)-1/2*b*n*PolyLog[2,1-2*Sqrt[d]/(Sqrt[d]-Sqrt[d+e*x^2])]/d^(5/2)+1/3*(a+b*Log[c*x^n])*(1/(d*(d+e*x^2)^(3/2))-3*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]/d^(5/2)+3/(d^2*Sqrt[d+e*x^2]))-1/3*b*n/(d^2*Sqrt[d+e*x^2])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:440
  public void test0511() {
    check( //
        "Integrate[x^(-1+n)*Log[e*x^n]/(1-e*x^n), x]", //
        "PolyLog[2,1-e*x^n]/(e*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:475
  public void test0512() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])/x, x]", //
        "-b*e*n*x^r/r^2+e*x^r*(a+b*Log[c*x^n])/r+1/2*d*(a+b*Log[c*x^n])^2/(b*n)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:483
  public void test0513() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d*n/x^5-b*e*n*x^(-5+r)/(5-r)^2-1/5*d*(a+b*Log[c*x^n])/x^5-e*x^(-5+r)*(a+b*Log[c*x^n])/(5-r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:491
  public void test0514() {
    check( //
        "Integrate[x^2*(d+e*x^r)^2*(a+b*Log[c*x^n]), x]", //
        "-1/9*b*d^2*n*x^3-2*b*d*e*n*x^(3+r)/(3+r)^2-b*e^2*n*x^(3+2*r)/(3+2*r)^2+1/3*(d^2*x^3+6*d*e*x^(3+r)/(3+r)+3*e^2*x^(3+2*r)/(3+2*r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:499
  public void test0515() {
    check( //
        "Integrate[x*(d+e*x^r)^3*(a+b*Log[c*x^n]), x]", //
        "-1/4*b*d^3*n*x^2-3/4*b*d*e^2*n*x^(2*(1+r))/(1+r)^2-3*b*d^2*e*n*x^(2+r)/(2+r)^2-b*e^3*n*x^(2+3*r)/(2+3*r)^2+1/2*(d^3*x^2+3*d*e^2*x^(2*(1+r))/(1+r)+6*d^2*e*x^(2+r)/(2+r)+2*e^3*x^(2+3*r)/(2+3*r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:507
  public void test0516() {
    check( //
        "Integrate[(d+e*x^r)^3*(a+b*Log[c*x^n])/x^4, x]", //
        "-1/9*b*d^3*n/x^3-1/9*b*e^3*n/((1-r)^2*x^(3*(1-r)))-3*b*d^2*e*n*x^(-3+r)/(3-r)^2-3*b*d*e^2*n*x^(-3+2*r)/(3-2*r)^2-1/3*d^3*(a+b*Log[c*x^n])/x^3-1/3*e^3*(a+b*Log[c*x^n])/((1-r)*x^(3*(1-r)))-3*d^2*e*x^(-3+r)*(a+b*Log[c*x^n])/(3-r)-3*d*e^2*x^(-3+2*r)*(a+b*Log[c*x^n])/(3-2*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:541
  public void test0517() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x*(d+e*x^r)), x]", //
        "-(a+b*Log[c*x^n])^2*Log[1+d/(e*x^r)]/(d*r)+2*b*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x^r)]/(d*r^2)+2*b^2*n^2*PolyLog[3,-d/(e*x^r)]/(d*r^3)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:43
  public void test0518() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])*Log[d*(1/d+f*x^2)], x]", //
        "-8/9*b*n*x/(d*f)+4/27*b*n*x^3+2/9*b*n*ArcTan[x*Sqrt[d]*Sqrt[f]]/(d^(3/2)*f^(3/2))+2/3*x*(a+b*Log[c*x^n])/(d*f)-2/9*x^3*(a+b*Log[c*x^n])-2/3*ArcTan[x*Sqrt[d]*Sqrt[f]]*(a+b*Log[c*x^n])/(d^(3/2)*f^(3/2))-1/9*b*n*x^3*Log[1+d*f*x^2]+1/3*x^3*(a+b*Log[c*x^n])*Log[1+d*f*x^2]+1/3*I*b*n*PolyLog[2,-I*x*Sqrt[d]*Sqrt[f]]/(d^(3/2)*f^(3/2))-1/3*I*b*n*PolyLog[2,I*x*Sqrt[d]*Sqrt[f]]/(d^(3/2)*f^(3/2))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:69
  public void test0519() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*Sqrt[x])]/x^4, x]", //
        "-11/225*b*d*f*n/x^(5/2)+5/72*b*d^2*f^2*n/x^2-1/9*b*d^3*f^3*n/x^(3/2)+2/9*b*d^4*f^4*n/x-1/18*b*d^6*f^6*n*Log[x]+1/12*b*d^6*f^6*n*Log[x]^2-1/15*d*f*(a+b*Log[c*x^n])/x^(5/2)+1/12*d^2*f^2*(a+b*Log[c*x^n])/x^2-1/9*d^3*f^3*(a+b*Log[c*x^n])/x^(3/2)+1/6*d^4*f^4*(a+b*Log[c*x^n])/x-1/6*d^6*f^6*Log[x]*(a+b*Log[c*x^n])+1/9*b*d^6*f^6*n*Log[1+d*f*Sqrt[x]]-1/9*b*n*Log[1+d*f*Sqrt[x]]/x^3+1/3*d^6*f^6*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]-1/3*(a+b*Log[c*x^n])*Log[1+d*f*Sqrt[x]]/x^3+2/3*b*d^6*f^6*n*PolyLog[2,-d*f*Sqrt[x]]-7/9*b*d^5*f^5*n/Sqrt[x]-1/3*d^5*f^5*(a+b*Log[c*x^n])/Sqrt[x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:109
  public void test0520() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m], x]", //
        "-12*a*b^2*m*n^2*x+18*b^3*m*n^3*x-6*b^2*m*n^2*(a-b*n)*x-18*b^3*m*n^2*x*Log[c*x^n]+6*b*m*n*x*(a+b*Log[c*x^n])^2-m*x*(a+b*Log[c*x^n])^3+6*b^2*e*m*n^2*(a-b*n)*Log[e+f*x]/f+6*a*b^2*n^2*x*Log[d*(e+f*x)^m]-6*b^3*n^3*x*Log[d*(e+f*x)^m]+6*b^3*n^2*x*Log[c*x^n]*Log[d*(e+f*x)^m]-3*b*n*x*(a+b*Log[c*x^n])^2*Log[d*(e+f*x)^m]+x*(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m]+6*b^3*e*m*n^2*Log[c*x^n]*Log[1+f*x/e]/f-3*b*e*m*n*(a+b*Log[c*x^n])^2*Log[1+f*x/e]/f+e*m*(a+b*Log[c*x^n])^3*Log[1+f*x/e]/f+6*b^3*e*m*n^3*PolyLog[2,-f*x/e]/f-6*b^2*e*m*n^2*(a+b*Log[c*x^n])*PolyLog[2,-f*x/e]/f+3*b*e*m*n*(a+b*Log[c*x^n])^2*PolyLog[2,-f*x/e]/f+6*b^3*e*m*n^3*PolyLog[3,-f*x/e]/f-6*b^2*e*m*n^2*(a+b*Log[c*x^n])*PolyLog[3,-f*x/e]/f+6*b^3*e*m*n^3*PolyLog[4,-f*x/e]/f");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:119
  public void test0521() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^5, x]", //
        "-3/16*b*f*m*n/(e*x^2)-1/8*b*f^2*m*n*Log[x]/e^2+1/4*b*f^2*m*n*Log[x]^2/e^2-1/4*f*m*(a+b*Log[c*x^n])/(e*x^2)-1/2*f^2*m*Log[x]*(a+b*Log[c*x^n])/e^2+1/16*b*f^2*m*n*Log[e+f*x^2]/e^2-1/8*b*f^2*m*n*Log[-f*x^2/e]*Log[e+f*x^2]/e^2+1/4*f^2*m*(a+b*Log[c*x^n])*Log[e+f*x^2]/e^2-1/16*b*n*Log[d*(e+f*x^2)^m]/x^4-1/4*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^4-1/8*b*f^2*m*n*PolyLog[2,1+f*x^2/e]/e^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:127
  public void test0522() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x^3, x]", //
        "1/2*b^2*f*m*n^2*Log[x]/e-1/2*b*f*m*n*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])/e-1/2*f*m*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])^2/e-1/4*b^2*f*m*n^2*Log[e+f*x^2]/e-1/4*b^2*n^2*Log[d*(e+f*x^2)^m]/x^2-1/2*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^2-1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x^2+1/4*b^2*f*m*n^2*PolyLog[2,-e/(f*x^2)]/e+1/2*b*f*m*n*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x^2)]/e+1/4*b^2*f*m*n^2*PolyLog[3,-e/(f*x^2)]/e");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:135
  public void test0523() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m]/x^3, x]", //
        "3/4*b^3*f*m*n^3*Log[x]/e-3/4*b^2*f*m*n^2*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])/e-3/4*b*f*m*n*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])^2/e-1/2*f*m*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])^3/e-3/8*b^3*f*m*n^3*Log[e+f*x^2]/e-3/8*b^3*n^3*Log[d*(e+f*x^2)^m]/x^2-3/4*b^2*n^2*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^2-3/4*b*n*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x^2-1/2*(a+b*Log[c*x^n])^3*Log[d*(e+f*x^2)^m]/x^2+3/8*b^3*f*m*n^3*PolyLog[2,-e/(f*x^2)]/e+3/4*b^2*f*m*n^2*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x^2)]/e+3/4*b*f*m*n*(a+b*Log[c*x^n])^2*PolyLog[2,-e/(f*x^2)]/e+3/8*b^3*f*m*n^3*PolyLog[3,-e/(f*x^2)]/e+3/4*b^2*f*m*n^2*(a+b*Log[c*x^n])*PolyLog[3,-e/(f*x^2)]/e+3/8*b^3*f*m*n^3*PolyLog[4,-e/(f*x^2)]/e");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:145
  public void test0524() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x, x]", //
        "1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])^k]/(b*n)-1/2*k*(a+b*Log[c*x^n])^2*Log[1+f*Sqrt[x]/e]/(b*n)-2*k*(a+b*Log[c*x^n])*PolyLog[2,-f*Sqrt[x]/e]+4*b*k*n*PolyLog[3,-f*Sqrt[x]/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:153
  public void test0525() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])]/x^2, x]", //
        "-b^2*f^2*n^2*Log[x]/e^2+1/2*b^2*f^2*n^2*Log[x]^2/e^2-b*f^2*n*Log[x]*(a+b*Log[c*x^n])/e^2-1/6*f^2*(a+b*Log[c*x^n])^3/(b*e^2*n)+2*b^2*f^2*n^2*Log[e+f*Sqrt[x]]/e^2+2*b*f^2*n*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e^2-4*b^2*f^2*n^2*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e^2-2*b^2*n^2*Log[d*(e+f*Sqrt[x])]/x-2*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])]/x-(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])]/x+f^2*(a+b*Log[c*x^n])^2*Log[1+f*Sqrt[x]/e]/e^2+4*b*f^2*n*(a+b*Log[c*x^n])*PolyLog[2,-f*Sqrt[x]/e]/e^2-4*b^2*f^2*n^2*PolyLog[2,1+f*Sqrt[x]/e]/e^2-8*b^2*f^2*n^2*PolyLog[3,-f*Sqrt[x]/e]/e^2-14*b^2*f*n^2/(e*Sqrt[x])-6*b*f*n*(a+b*Log[c*x^n])/(e*Sqrt[x])-f*(a+b*Log[c*x^n])^2/(e*Sqrt[x])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:195
  public void test0526() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*(d+e*Log[f*x^r]), x]", //
        "b*e*n*r*x-e*(a-b*n)*r*x-b*e*r*x*Log[c*x^n]+a*x*(d+e*Log[f*x^r])-b*n*x*(d+e*Log[f*x^r])+b*x*Log[c*x^n]*(d+e*Log[f*x^r])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:203
  public void test0527() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/x, x]", //
        "-1/12*e*r*(a+b*Log[c*x^n])^4/(b^2*n^2)+1/3*(a+b*Log[c*x^n])^3*(d+e*Log[f*x^r])/(b*n)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:247
  public void test0528() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*PolyLog[k,e*x^q]/x, x]", //
        "(a+b*Log[c*x^n])^2*PolyLog[1+k,e*x^q]/q-2*b*n*(a+b*Log[c*x^n])*PolyLog[2+k,e*x^q]/q^2+2*b^2*n^2*PolyLog[3+k,e*x^q]/q^3");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:260
  public void test0529() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[2,e*x]/x, x]", //
        "(a+b*Log[c*x^n])*PolyLog[3,e*x]-b*n*PolyLog[4,e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:268
  public void test0530() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[3,e*x]/x^3, x]", //
        "-5/16*b*e*n/x+3/16*b*e^2*n*Log[x]-1/16*b*e^2*n*Log[x]^2-1/8*e*(a+b*Log[c*x^n])/x+1/8*e^2*Log[x]*(a+b*Log[c*x^n])-3/16*b*e^2*n*Log[1-e*x]+3/16*b*n*Log[1-e*x]/x^2-1/8*e^2*(a+b*Log[c*x^n])*Log[1-e*x]+1/8*(a+b*Log[c*x^n])*Log[1-e*x]/x^2-1/8*b*e^2*n*PolyLog[2,e*x]-1/4*b*n*PolyLog[2,e*x]/x^2-1/4*(a+b*Log[c*x^n])*PolyLog[2,e*x]/x^2-1/4*b*n*PolyLog[3,e*x]/x^2-1/2*(a+b*Log[c*x^n])*PolyLog[3,e*x]/x^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:287
  public void test0531() {
    check( //
        "Integrate[x^2*Log[c*(b*x^n)^p]^2, x]", //
        "2/27*n^2*p^2*x^3-2/9*n*p*x^3*Log[c*(b*x^n)^p]+1/3*x^3*Log[c*(b*x^n)^p]^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:299
  public void test0532() {
    check( //
        "Integrate[(e*x)^q*(a+b*Log[c*(d*x^m)^n])^2, x]", //
        "2*b^2*m^2*n^2*(e*x)^(1+q)/(e*(1+q)^3)-2*b*m*n*(e*x)^(1+q)*(a+b*Log[c*(d*x^m)^n])/(e*(1+q)^2)+(e*x)^(1+q)*(a+b*Log[c*(d*x^m)^n])^2/(e*(1+q))");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:309
  public void test0533() {
    check( //
        "Integrate[(a+b*Log[c*(d*x^m)^n])^p/x, x]", //
        "(a+b*Log[c*(d*x^m)^n])^(1+p)/(b*m*n*(1+p))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:16
  public void test0534() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(a*g+b*g*x), x]", //
        "-Log[(-b*c+a*d)/(d*(a+b*x))]*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(b*g)+B*n*PolyLog[2,1+(b*c-a*d)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:25
  public void test0535() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(a*g+b*g*x), x]", //
        "-(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2*Log[1-b*(c+d*x)/(d*(a+b*x))]/(b*g)+2*B*n*(A+B*Log[e*((a+b*x)/(c+d*x))^n])*PolyLog[2,b*(c+d*x)/(d*(a+b*x))]/(b*g)+2*B^2*n^2*PolyLog[3,b*(c+d*x)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:55
  public void test0536() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])/(c*g+d*g*x)^4, x]", //
        "1/9*B*n/(d*g^4*(c+d*x)^3)+1/6*b*B*n/(d*(b*c-a*d)*g^4*(c+d*x)^2)+1/3*b^2*B*n/(d*(b*c-a*d)^2*g^4*(c+d*x))+1/3*b^3*B*n*Log[a+b*x]/(d*(b*c-a*d)^3*g^4)+1/3*(-A-B*Log[e*((a+b*x)/(c+d*x))^n])/(d*g^4*(c+d*x)^3)-1/3*b^3*B*n*Log[c+d*x]/(d*(b*c-a*d)^3*g^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:63
  public void test0537() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(c*g+d*g*x)^3, x]", //
        "-1/4*B^2*d*n^2*(a+b*x)^2/((b*c-a*d)^2*g^3*(c+d*x)^2)-2*A*b*B*n*(a+b*x)/((b*c-a*d)^2*g^3*(c+d*x))+2*b*B^2*n^2*(a+b*x)/((b*c-a*d)^2*g^3*(c+d*x))-2*b*B^2*n*(a+b*x)*Log[e*((a+b*x)/(c+d*x))^n]/((b*c-a*d)^2*g^3*(c+d*x))+1/2*B*d*n*(a+b*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)^2*g^3*(c+d*x)^2)-1/2*d*(a+b*x)^2*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^2*g^3*(c+d*x)^2)+b*(a+b*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)^2*g^3*(c+d*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:132
  public void test0538() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])/(a*g+b*g*x)^4, x]", //
        "-1/9*B/(b*g^4*(a+b*x)^3)+1/6*B*d/(b*(b*c-a*d)*g^4*(a+b*x)^2)-1/3*B*d^2/(b*(b*c-a*d)^2*g^4*(a+b*x))-1/3*B*d^3*Log[a+b*x]/(b*(b*c-a*d)^3*g^4)+1/3*(-A-B*Log[e*(a+b*x)/(c+d*x)])/(b*g^4*(a+b*x)^3)+1/3*B*d^3*Log[c+d*x]/(b*(b*c-a*d)^3*g^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:140
  public void test0539() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])^2/(a*g+b*g*x)^3, x]", //
        "2*B^2*d*(c+d*x)/((b*c-a*d)^2*g^3*(a+b*x))-1/4*b*B^2*(c+d*x)^2/((b*c-a*d)^2*g^3*(a+b*x)^2)+2*B*d*(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)^2*g^3*(a+b*x))-1/2*b*B*(c+d*x)^2*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)^2*g^3*(a+b*x)^2)+d*(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)^2*g^3*(a+b*x))-1/2*b*(c+d*x)^2*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)^2*g^3*(a+b*x)^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:178
  public void test0540() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/(a*g+b*g*x)^4, x]", //
        "-8*B^2*d^2*(c+d*x)/((b*c-a*d)^3*g^4*(a+b*x))+2*b*B^2*d*(c+d*x)^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-8/27*b^2*B^2*(c+d*x)^3/((b*c-a*d)^3*g^4*(a+b*x)^3)-4*B*d^2*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^3*g^4*(a+b*x))+2*b*B*d*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^3*g^4*(a+b*x)^2)-4/9*b^2*B*(c+d*x)^3*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^3*g^4*(a+b*x)^3)-d^2*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^3*g^4*(a+b*x))+b*d*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-1/3*b^2*(c+d*x)^3*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^3*g^4*(a+b*x)^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:198
  public void test0541() {
    check( //
        "Integrate[(a+b*x)^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n]), x]", //
        "1/3*B*(b*c-a*d)^2*n*x/d^2-1/6*B*(b*c-a*d)*n*(a+b*x)^2/(b*d)-1/3*B*(b*c-a*d)^3*n*Log[c+d*x]/(b*d^3)+1/3*(a+b*x)^3*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:230
  public void test0542() {
    check( //
        "Integrate[(a*g+b*g*x)^4*(A+B*Log[e*(c+d*x)/(a+b*x)]), x]", //
        "-1/5*B*(b*c-a*d)^4*g^4*x/d^4+1/10*B*(b*c-a*d)^3*g^4*(a+b*x)^2/(b*d^3)-1/15*B*(b*c-a*d)^2*g^4*(a+b*x)^3/(b*d^2)+1/20*B*(b*c-a*d)*g^4*(a+b*x)^4/(b*d)+1/5*B*(b*c-a*d)^5*g^4*Log[c+d*x]/(b*d^5)+1/5*g^4*(a+b*x)^5*(A+B*Log[e*(c+d*x)/(a+b*x)])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:288
  public void test0543() {
    check( //
        "Integrate[1/((a*g+b*g*x)^3*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])), x]", //
        "-1/2*b*ExpIntegralEi[(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/B]/(E^(A/B)*B*(b*c-a*d)^2*e*g^3)+1/2*d*(c+d*x)*ExpIntegralEi[1/2*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/B]/(E^(1/2*A/B)*B*(b*c-a*d)^2*g^3*(a+b*x)*Sqrt[e*(c+d*x)^2/(a+b*x)^2])");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:387
  public void test0544() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])/(g+h*x), x]", //
        "-B*n*Log[-h*(a+b*x)/(b*g-a*h)]*Log[g+h*x]/h+B*n*Log[-h*(c+d*x)/(d*g-c*h)]*Log[g+h*x]/h+(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])*Log[g+h*x]/h-B*n*PolyLog[2,b*(g+h*x)/(b*g-a*h)]/h+B*n*PolyLog[2,d*(g+h*x)/(d*g-c*h)]/h");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:277
  public void test0545() {
    check( //
        "Integrate[1/((a+b*x)*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2), x]", //
        "(-1)/(B*(b*c-a*d)*n*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n]))");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:287
  public void test0546() {
    check( //
        "Integrate[(a+b*x)^m*(c+d*x)^(-2-m)/Log[e*(a+b*x)^n/(c+d*x)^n], x]", //
        "(a+b*x)^(1+m)*(c+d*x)^(-1-m)*ExpIntegralEi[(1+m)*Log[e*(a+b*x)^n/(c+d*x)^n]/n]/((b*c-a*d)*n*(e*(a+b*x)^n/(c+d*x)^n)^((1+m)/n))");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:309
  public void test0547() {
    check( //
        "Integrate[Log[(a-c*g+b*x-d*g*x)/(a+b*x)]/((a+b*x)*(c+d*x)), x]", //
        "PolyLog[2,g*(c+d*x)/(a+b*x)]/(b*c-a*d)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:28
  public void test0548() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(a+b*x)^3, x]", //
        "-1/4*p*r/(b*(a+b*x)^2)-1/2*d*q*r/(b*(b*c-a*d)*(a+b*x))-1/2*d^2*q*r*Log[a+b*x]/(b*(b*c-a*d)^2)+1/2*d^2*q*r*Log[c+d*x]/(b*(b*c-a*d)^2)-1/2*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(b*(a+b*x)^2)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:64
  public void test0549() {
    check( //
        "Integrate[(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])^2/(1-c^2*x^2), x]", //
        "-1/3*(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])^3/(b*c)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:74
  public void test0550() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(g*k+h*k*x), x]", //
        "-p*r*Log[-h*(a+b*x)/(b*g-a*h)]*Log[g*k+h*k*x]/(h*k)-q*r*Log[-h*(c+d*x)/(d*g-c*h)]*Log[g*k+h*k*x]/(h*k)+Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]*Log[g*k+h*k*x]/(h*k)-p*r*PolyLog[2,b*(g+h*x)/(b*g-a*h)]/(h*k)-q*r*PolyLog[2,d*(g+h*x)/(d*g-c*h)]/(h*k)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:122
  public void test0551() {
    check( //
        "Integrate[Log[c*x/(a+b*x)]^2/(x*(a+b*x)), x]", //
        "1/3*Log[c*x/(a+b*x)]^3/a");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:130
  public void test0552() {
    check( //
        "Integrate[Log[c*(b+a*x)^2/x^2]^2, x]", //
        "-4*b*Log[b/(b+a*x)]*Log[c*(b+a*x)^2/x^2]/a+x*Log[c*(b+a*x)^2/x^2]^2+8*b*PolyLog[2,1-b/(b+a*x)]/a");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:14
  public void test0553() {
    check( //
        "Integrate[1/Log[c*(d+e*x)], x]", //
        "LogIntegral[c*(d+e*x)]/(c*e)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:24
  public void test0554() {
    check( //
        "Integrate[1/Log[c*(d+e*x)]^(3/2), x]", //
        "2*Erfi[Sqrt[Log[c*(d+e*x)]]]*Sqrt[Pi]/(c*e)-2*(d+e*x)/(e*Sqrt[Log[c*(d+e*x)]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:48
  public void test0555() {
    check( //
        "Integrate[1/(a+b*Log[c*(d+e*x)^n])^(5/2), x]", //
        "-2/3*(d+e*x)/(b*e*n*(a+b*Log[c*(d+e*x)^n])^(3/2))+4/3*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*b^(5/2)*e*n^(5/2)*(c*(d+e*x)^n)^(1/n))-4/3*(d+e*x)/(b^2*e*n^2*Sqrt[a+b*Log[c*(d+e*x)^n]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:66
  public void test0556() {
    check( //
        "Integrate[(f+g*x)^2*(a+b*Log[c*(d+e*x)^n]), x]", //
        "-1/3*b*(e*f-d*g)^2*n*x/e^2-1/6*b*(e*f-d*g)*n*(f+g*x)^2/(e*g)-1/9*b*n*(f+g*x)^3/g-1/3*b*(e*f-d*g)^3*n*Log[d+e*x]/(e^3*g)+1/3*(f+g*x)^3*(a+b*Log[c*(d+e*x)^n])/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:82
  public void test0557() {
    check( //
        "Integrate[(f+g*x)^2*(a+b*Log[c*(d+e*x)^n])^3, x]", //
        "6*a*b^2*(e*f-d*g)^2*n^2*x/e^2-6*b^3*(e*f-d*g)^2*n^3*x/e^2-3/4*b^3*g*(e*f-d*g)*n^3*(d+e*x)^2/e^3-2/27*b^3*g^2*n^3*(d+e*x)^3/e^3+6*b^3*(e*f-d*g)^2*n^2*(d+e*x)*Log[c*(d+e*x)^n]/e^3+3/2*b^2*g*(e*f-d*g)*n^2*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])/e^3+2/9*b^2*g^2*n^2*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])/e^3-3*b*(e*f-d*g)^2*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e^3-3/2*b*g*(e*f-d*g)*n*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^2/e^3-1/3*b*g^2*n*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])^2/e^3+(e*f-d*g)^2*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e^3+g*(e*f-d*g)*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^3/e^3+1/3*g^2*(d+e*x)^3*(a+b*Log[c*(d+e*x)^n])^3/e^3");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:98
  public void test0558() {
    check( //
        "Integrate[Log[a+b*x+c*x]^3, x]", //
        "-6*x+6*(a+(b+c)*x)*Log[a+(b+c)*x]/(b+c)-3*(a+(b+c)*x)*Log[a+(b+c)*x]^2/(b+c)+(a+(b+c)*x)*Log[a+(b+c)*x]^3/(b+c)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:126
  public void test0559() {
    check( //
        "Integrate[(f+g*x)^2/(a+b*Log[c*(d+e*x)^n])^2, x]", //
        "(e*f-d*g)^2*(d+e*x)*ExpIntegralEi[(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(a/(b*n))*b^2*e^3*n^2*(c*(d+e*x)^n)^(1/n))+4*g*(e*f-d*g)*(d+e*x)^2*ExpIntegralEi[2*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(2*a/(b*n))*b^2*e^3*n^2*(c*(d+e*x)^n)^(2/n))+3*g^2*(d+e*x)^3*ExpIntegralEi[3*(a+b*Log[c*(d+e*x)^n])/(b*n)]/(E^(3*a/(b*n))*b^2*e^3*n^2*(c*(d+e*x)^n)^(3/n))-(d+e*x)*(f+g*x)^2/(b*e*n*(a+b*Log[c*(d+e*x)^n]))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:142
  public void test0560() {
    check( //
        "Integrate[Sqrt[a+b*Log[c*(d+e*x)^n]], x]", //
        "-1/2*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]*Sqrt[b]*Sqrt[n]/(E^(a/(b*n))*e*(c*(d+e*x)^n)^(1/n))+(d+e*x)*Sqrt[a+b*Log[c*(d+e*x)^n]]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:288
  public void test0561() {
    check( //
        "Integrate[(a+b*Log[c*(e+f*x)])^p/(d*e+d*f*x), x]", //
        "(a+b*Log[c*(e+f*x)])^(1+p)/(b*d*f*(1+p))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:341
  public void test0562() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])/(x^3*(f+g*x)), x]", //
        "-1/2*b*e*n/(d*f*x)-1/2*b*e^2*n*Log[x]/(d^2*f)-b*e*g*n*Log[x]/(d*f^2)+1/2*b*e^2*n*Log[d+e*x]/(d^2*f)+b*e*g*n*Log[d+e*x]/(d*f^2)+1/2*(-a-b*Log[c*(d+e*x)^n])/(f*x^2)+g*(a+b*Log[c*(d+e*x)^n])/(f^2*x)+g^2*Log[-e*x/d]*(a+b*Log[c*(d+e*x)^n])/f^3-g^2*(a+b*Log[c*(d+e*x)^n])*Log[e*(f+g*x)/(e*f-d*g)]/f^3-b*g^2*n*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/f^3+b*g^2*n*PolyLog[2,1+e*x/d]/f^3");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:379
  public void test0563() {
    check( //
        "Integrate[Log[e/(e+f*x)]/(e^2-f^2*x^2), x]", //
        "-ArcTanh[f*x/e]*Log[2]/(e*f)+1/2*PolyLog[2,1-2*e/(e+f*x)]/(e*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:419
  public void test0564() {
    check( //
        "Integrate[(f+g/x)^2*x^2*(a+b*Log[c*(d+e*x)^n]), x]", //
        "-1/3*b*(d*f-e*g)^2*n*x/e^2+1/6*b*(d*f-e*g)*n*(g+f*x)^2/(e*f)-1/9*b*n*(g+f*x)^3/f+1/3*b*(d*f-e*g)^3*n*Log[d+e*x]/(e^3*f)+1/3*(g+f*x)^3*(a+b*Log[c*(d+e*x)^n])/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:470
  public void test0565() {
    check( //
        "Integrate[Log[2*a/(a+b*x)]/(a^2-b^2*x^2), x]", //
        "1/2*PolyLog[2,1-2*a/(a+b*x)]/(a*b)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:482
  public void test0566() {
    check( //
        "Integrate[Log[c*(a+b*x)^n]/(d*x+e*x^2), x]", //
        "Log[-b*x/a]*Log[c*(a+b*x)^n]/d-Log[c*(a+b*x)^n]*Log[b*(d+e*x)/(b*d-a*e)]/d-n*PolyLog[2,-e*(a+b*x)/(b*d-a*e)]/d+n*PolyLog[2,1+b*x/a]/d");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:492
  public void test0567() {
    check( //
        "Integrate[Log[x]/(x*(a+b*x+c*x^2)), x]", //
        "1/2*Log[x]^2/a-1/2*Log[x]*Log[1+2*c*x/(b+Sqrt[b^2-4*a*c])]*(1-b/Sqrt[b^2-4*a*c])/a-1/2*PolyLog[2,-2*c*x/(b+Sqrt[b^2-4*a*c])]*(1-b/Sqrt[b^2-4*a*c])/a-1/2*Log[x]*Log[1+2*c*x/(b-Sqrt[b^2-4*a*c])]*(1+b/Sqrt[b^2-4*a*c])/a-1/2*PolyLog[2,-2*c*x/(b-Sqrt[b^2-4*a*c])]*(1+b/Sqrt[b^2-4*a*c])/a");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:508
  public void test0568() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])/x^2, x]", //
        "b*e*m*n*Log[x]/d-b*e*n*Log[1+d/(e*x)]*Log[f*x^m]/d-b*e*m*n*Log[d+e*x]/d-(m/x+Log[f*x^m]/x)*(a+b*Log[c*(d+e*x)^n])+b*e*m*n*PolyLog[2,-d/(e*x)]/d");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:588
  public void test0569() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^m)^n])^(5/2), x]", //
        "-5/2*b*m*n*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^(3/2)/f+(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^(5/2)/f-15/8*b^(5/2)*m^(5/2)*n^(5/2)*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/(Sqrt[b]*Sqrt[m]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*m*n))*f*(c*(d*(e+f*x)^m)^n)^(1/(m*n)))+15/4*b^2*m^2*n^2*(e+f*x)*Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:620
  public void test0570() {
    check( //
        "Integrate[(g+h*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^3, x]", //
        "6*a*b^2*(f*g-e*h)^2*p^2*q^2*x/f^2-6*b^3*(f*g-e*h)^2*p^3*q^3*x/f^2-3/4*b^3*h*(f*g-e*h)*p^3*q^3*(e+f*x)^2/f^3-2/27*b^3*h^2*p^3*q^3*(e+f*x)^3/f^3+6*b^3*(f*g-e*h)^2*p^2*q^2*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f^3+3/2*b^2*h*(f*g-e*h)*p^2*q^2*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])/f^3+2/9*b^2*h^2*p^2*q^2*(e+f*x)^3*(a+b*Log[c*(d*(e+f*x)^p)^q])/f^3-3*b*(f*g-e*h)^2*p*q*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^3-3/2*b*h*(f*g-e*h)*p*q*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^3-1/3*b*h^2*p*q*(e+f*x)^3*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^3+(f*g-e*h)^2*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f^3+h*(f*g-e*h)*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f^3+1/3*h^2*(e+f*x)^3*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f^3");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:628
  public void test0571() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^4/(g+h*x)^2, x]", //
        "(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^4/((f*g-e*h)*(g+h*x))-4*b*f*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])^3*Log[f*(g+h*x)/(f*g-e*h)]/(h*(f*g-e*h))-12*b^2*f*p^2*q^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))+24*b^3*f*p^3*q^3*(a+b*Log[c*(d*(e+f*x)^p)^q])*PolyLog[3,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))-24*b^4*f*p^4*q^4*PolyLog[4,-h*(e+f*x)/(f*g-e*h)]/(h*(f*g-e*h))");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:658
  public void test0572() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^(3/2), x]", //
        "(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^(3/2)/f+3/4*b^(3/2)*p^(3/2)*q^(3/2)*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]]/(Sqrt[b]*Sqrt[p]*Sqrt[q])]*Sqrt[Pi]/(E^(a/(b*p*q))*f*(c*(d*(e+f*x)^p)^q)^(1/(p*q)))-3/2*b*p*q*(e+f*x)*Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]]/f");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:28
  public void test0573() {
    check( //
        "Integrate[x*Log[c*(a+b*x^3)^p], x]", //
        "-3/4*p*x^2-1/2*a^(2/3)*p*Log[a^(1/3)+b^(1/3)*x]/b^(2/3)+1/4*a^(2/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(2/3)+1/2*x^2*Log[c*(a+b*x^3)^p]-1/2*a^(2/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/b^(2/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:36
  public void test0574() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/x^7, x]", //
        "-1/6*b*p/(a*x^3)-1/2*b^2*p*Log[x]/a^2+1/6*b^2*p*Log[a+b*x^3]/a^2-1/6*Log[c*(a+b*x^3)^p]/x^6");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:54
  public void test0575() {
    check( //
        "Integrate[Log[c*(a+b/x^2)^p]/x, x]", //
        "-1/2*Log[c*(a+b/x^2)^p]*Log[-b/(a*x^2)]-1/2*p*PolyLog[2,1+b/(a*x^2)]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:95
  public void test0576() {
    check( //
        "Integrate[(f*x)^(-1+n)*Log[c*(d+e*x^n)^p], x]", //
        "-p*(f*x)^n/(f*n)+d*p*(f*x)^n*Log[d+e*x^n]/(e*f*n*x^n)+(f*x)^n*Log[c*(d+e*x^n)^p]/(f*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:115
  public void test0577() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^2/x, x]", //
        "1/2*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]^2+p*Log[c*(a+b*x^2)^p]*PolyLog[2,1+b*x^2/a]-p^2*PolyLog[3,1+b*x^2/a]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:131
  public void test0578() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^3/x^5, x]", //
        "3/2*b^2*p^2*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]/a^2-3/4*b*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/(a^2*x^2)-1/4*Log[c*(a+b*x^2)^p]^3/x^4-3/4*b^2*p*Log[c*(a+b*x^2)^p]^2*Log[1-a/(a+b*x^2)]/a^2+3/2*b^2*p^2*Log[c*(a+b*x^2)^p]*PolyLog[2,a/(a+b*x^2)]/a^2+3/2*b^2*p^3*PolyLog[2,1+b*x^2/a]/a^2+3/2*b^2*p^3*PolyLog[3,a/(a+b*x^2)]/a^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:160
  public void test0579() {
    check( //
        "Integrate[x^3/Log[c*(a+b*x^2)], x]", //
        "1/2*ExpIntegralEi[2*Log[c*(a+b*x^2)]]/(b^2*c^2)-1/2*a*LogIntegral[c*(a+b*x^2)]/(b^2*c)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:172
  public void test0580() {
    check( //
        "Integrate[Log[c*(d+e*x^3)^p]^2/x, x]", //
        "1/3*Log[-e*x^3/d]*Log[c*(d+e*x^3)^p]^2+2/3*p*Log[c*(d+e*x^3)^p]*PolyLog[2,1+e*x^3/d]-2/3*p^2*PolyLog[3,1+e*x^3/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:182
  public void test0581() {
    check( //
        "Integrate[x^5/Log[c*(d+e*x^3)^p], x]", //
        "-1/3*d*(d+e*x^3)*ExpIntegralEi[Log[c*(d+e*x^3)^p]/p]/(e^2*p*(c*(d+e*x^3)^p)^(1/p))+1/3*(d+e*x^3)^2*ExpIntegralEi[2*Log[c*(d+e*x^3)^p]/p]/(e^2*p*(c*(d+e*x^3)^p)^(2/p))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:221
  public void test0582() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]^2/x, x]", //
        "Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]^2/n+2*p*Log[c*(d+e*x^n)^p]*PolyLog[2,1+e*x^n/d]/n-2*p^2*PolyLog[3,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:235
  public void test0583() {
    check( //
        "Integrate[Log[c*(a+b*x)^p]/(d+e*x)^3, x]", //
        "1/2*b*p/(e*(b*d-a*e)*(d+e*x))+1/2*b^2*p*Log[a+b*x]/(e*(b*d-a*e)^2)-1/2*Log[c*(a+b*x)^p]/(e*(d+e*x)^2)-1/2*b^2*p*Log[d+e*x]/(e*(b*d-a*e)^2)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:300
  public void test0584() {
    check( //
        "Integrate[x*Log[c*(a+b*x^3)^p]/(d+e*x), x]", //
        "-3*p*x/e+a^(1/3)*p*Log[a^(1/3)+b^(1/3)*x]/(b^(1/3)*e)+d*p*Log[-e*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*d-a^(1/3)*e)]*Log[d+e*x]/e^2+d*p*Log[-e*((-1)^(2/3)*a^(1/3)+b^(1/3)*x)/(b^(1/3)*d-(-1)^(2/3)*a^(1/3)*e)]*Log[d+e*x]/e^2+d*p*Log[(-1)^(1/3)*e*(a^(1/3)+(-1)^(2/3)*b^(1/3)*x)/(b^(1/3)*d+(-1)^(1/3)*a^(1/3)*e)]*Log[d+e*x]/e^2-1/2*a^(1/3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/(b^(1/3)*e)+x*Log[c*(a+b*x^3)^p]/e-d*Log[d+e*x]*Log[c*(a+b*x^3)^p]/e^2+d*p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d-a^(1/3)*e)]/e^2+d*p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d+(-1)^(1/3)*a^(1/3)*e)]/e^2+d*p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d-(-1)^(2/3)*a^(1/3)*e)]/e^2-a^(1/3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/(b^(1/3)*e)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:417
  public void test0585() {
    check( //
        "Integrate[x^2*(f+g*x^2)*Log[c*(d+e*x^2)^p], x]", //
        "2/3*d*f*p*x/e-2/5*d^2*g*p*x/e^2-2/9*f*p*x^3+2/15*d*g*p*x^3/e-2/25*g*p*x^5-2/3*d^(3/2)*f*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(3/2)+2/5*d^(5/2)*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(5/2)+1/3*f*x^3*Log[c*(d+e*x^2)^p]+1/5*g*x^5*Log[c*(d+e*x^2)^p]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:426
  public void test0586() {
    check( //
        "Integrate[(f+g*x^2)^2*Log[c*(d+e*x^2)^p]/x^3, x]", //
        "-1/2*g^2*p*x^2+e*f^2*p*Log[x]/d-1/2*e*f^2*p*Log[d+e*x^2]/d-1/2*f^2*Log[c*(d+e*x^2)^p]/x^2+1/2*g^2*(d+e*x^2)*Log[c*(d+e*x^2)^p]/e+f*g*Log[-e*x^2/d]*Log[c*(d+e*x^2)^p]+f*g*p*PolyLog[2,1+e*x^2/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:434
  public void test0587() {
    check( //
        "Integrate[(f+g*x^2)^2*Log[c*(d+e*x^2)^p]/x^4, x]", //
        "-2/3*e*f^2*p/(d*x)-2*g^2*p*x-2/3*e^(3/2)*f^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/d^(3/2)-1/3*f^2*Log[c*(d+e*x^2)^p]/x^3-2*f*g*Log[c*(d+e*x^2)^p]/x+g^2*x*Log[c*(d+e*x^2)^p]+2*g^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[d]/Sqrt[e]+4*f*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[e]/Sqrt[d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:482
  public void test0588() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]/(x*(f+g*x^n)^2), x]", //
        "-e*p*Log[d+e*x^n]/(f*(e*f-d*g)*n)+Log[c*(d+e*x^n)^p]/(f*n*(f+g*x^n))+Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/(f^2*n)+e*p*Log[f+g*x^n]/(f*(e*f-d*g)*n)-Log[c*(d+e*x^n)^p]*Log[e*(f+g*x^n)/(e*f-d*g)]/(f^2*n)-p*PolyLog[2,-g*(d+e*x^n)/(e*f-d*g)]/(f^2*n)+p*PolyLog[2,1+e*x^n/d]/(f^2*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:539
  public void test0589() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^2, x]", //
        "4*a*b*d*n*Sqrt[x]/e-4*b^2*d*n^2*Sqrt[x]/e+4*b^2*d*n*Log[c*(d+e*Sqrt[x])^n]*(d+e*Sqrt[x])/e^2-2*d*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*(d+e*Sqrt[x])/e^2+1/2*b^2*n^2*(d+e*Sqrt[x])^2/e^2-b*n*(a+b*Log[c*(d+e*Sqrt[x])^n])*(d+e*Sqrt[x])^2/e^2+(a+b*Log[c*(d+e*Sqrt[x])^n])^2*(d+e*Sqrt[x])^2/e^2");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:547
  public void test0590() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^3/x, x]", //
        "2*Log[-e*Sqrt[x]/d]*(a+b*Log[c*(d+e*Sqrt[x])^n])^3+6*b*n*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*PolyLog[2,1+e*Sqrt[x]/d]-12*b^2*n^2*(a+b*Log[c*(d+e*Sqrt[x])^n])*PolyLog[3,1+e*Sqrt[x]/d]+12*b^3*n^3*PolyLog[4,1+e*Sqrt[x]/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:593
  public void test0591() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e*x^(1/3))^n])^3, x]", //
        "-45/8*b^3*d^4*n^3*(d+e*x^(1/3))^2/e^6+20/9*b^3*d^3*n^3*(d+e*x^(1/3))^3/e^6-45/64*b^3*d^2*n^3*(d+e*x^(1/3))^4/e^6+18/125*b^3*d*n^3*(d+e*x^(1/3))^5/e^6-1/72*b^3*n^3*(d+e*x^(1/3))^6/e^6-18*a*b^2*d^5*n^2*x^(1/3)/e^5+18*b^3*d^5*n^3*x^(1/3)/e^5-18*b^3*d^5*n^2*(d+e*x^(1/3))*Log[c*(d+e*x^(1/3))^n]/e^6+45/4*b^2*d^4*n^2*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])/e^6-20/3*b^2*d^3*n^2*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])/e^6+45/16*b^2*d^2*n^2*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])/e^6-18/25*b^2*d*n^2*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])/e^6+1/12*b^2*n^2*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])/e^6+9*b*d^5*n*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6-45/4*b*d^4*n*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6+10*b*d^3*n*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6-45/8*b*d^2*n*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6+9/5*b*d*n*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6-1/4*b*n*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^6-3*d^5*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6+15/2*d^4*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6-10*d^3*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6+15/2*d^2*(d+e*x^(1/3))^4*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6-3*d*(d+e*x^(1/3))^5*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6+1/2*(d+e*x^(1/3))^6*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^6");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:611
  public void test0592() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(2/3))^n])^2/x^3, x]", //
        "-1/2*b^2*e^2*n^2/(d^2*x^(2/3))+1/2*b^2*e^3*n^2*Log[d+e*x^(2/3)]/d^3-1/2*b*e*n*(a+b*Log[c*(d+e*x^(2/3))^n])/(d*x^(4/3))+b*e^2*n*(d+e*x^(2/3))*(a+b*Log[c*(d+e*x^(2/3))^n])/(d^3*x^(2/3))+b*e^3*n*Log[1-d/(d+e*x^(2/3))]*(a+b*Log[c*(d+e*x^(2/3))^n])/d^3-1/2*(a+b*Log[c*(d+e*x^(2/3))^n])^2/x^2-b^2*e^3*n^2*Log[x]/d^3-b^2*e^3*n^2*PolyLog[2,d/(d+e*x^(2/3))]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:619
  public void test0593() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e*x^(2/3))^n])^3, x]", //
        "9/8*b^3*d*n^3*(d+e*x^(2/3))^2/e^3-1/9*b^3*n^3*(d+e*x^(2/3))^3/e^3+9*a*b^2*d^2*n^2*x^(2/3)/e^2-9*b^3*d^2*n^3*x^(2/3)/e^2+9*b^3*d^2*n^2*(d+e*x^(2/3))*Log[c*(d+e*x^(2/3))^n]/e^3-9/4*b^2*d*n^2*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])/e^3+1/3*b^2*n^2*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])/e^3-9/2*b*d^2*n*(d+e*x^(2/3))*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^3+9/4*b*d*n*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^3-1/2*b*n*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])^2/e^3+3/2*d^2*(d+e*x^(2/3))*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^3-3/2*d*(d+e*x^(2/3))^2*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^3+1/2*(d+e*x^(2/3))^3*(a+b*Log[c*(d+e*x^(2/3))^n])^3/e^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:651
  public void test0594() {
    check( //
        "Integrate[x*(a+b*Log[c*(d+e/x^(2/3))^n]), x]", //
        "-1/2*b*e^2*n*x^(2/3)/d^2+1/4*b*e*n*x^(4/3)/d+1/2*b*e^3*n*Log[d+e/x^(2/3)]/d^3+1/2*x^2*(a+b*Log[c*(d+e/x^(2/3))^n])+1/3*b*e^3*n*Log[x]/d^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:659
  public void test0595() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(2/3))^n])^2/x, x]", //
        "-3/2*(a+b*Log[c*(d+e/x^(2/3))^n])^2*Log[-e/(d*x^(2/3))]-3*b*n*(a+b*Log[c*(d+e/x^(2/3))^n])*PolyLog[2,1+e/(d*x^(2/3))]+3*b^2*n^2*PolyLog[3,1+e/(d*x^(2/3))]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:669
  public void test0596() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(2/3))^n])^3/x, x]", //
        "-3/2*(a+b*Log[c*(d+e/x^(2/3))^n])^3*Log[-e/(d*x^(2/3))]-9/2*b*n*(a+b*Log[c*(d+e/x^(2/3))^n])^2*PolyLog[2,1+e/(d*x^(2/3))]+9*b^2*n^2*(a+b*Log[c*(d+e/x^(2/3))^n])*PolyLog[3,1+e/(d*x^(2/3))]-9*b^3*n^3*PolyLog[4,1+e/(d*x^(2/3))]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:791
  public void test0597() {
    check( //
        "Integrate[x^(-1+m)*Log[f*x^p]^2/(d+e*x^m), x]", //
        "Log[f*x^p]^2*Log[1+e*x^m/d]/(e*m)+2*p*Log[f*x^p]*PolyLog[2,-e*x^m/d]/(e*m^2)-2*p^2*PolyLog[3,-e*x^m/d]/(e*m^3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:809
  public void test0598() {
    check( //
        "Integrate[Log[c*(d+e/(f+g*x))^q], x]", //
        "(f+g*x)*Log[c*(d+e/(f+g*x))^q]/g+e*q*Log[e+d*(f+g*x)]/(d*g)");
  }

  // 3.5 Logarithm functions.input:12
  public void test0599() {
    check( //
        "Integrate[Log[c*x^n]^(-1+q)*(a*x^m+b*Log[c*x^n]^q)^2/x, x]", //
        "1/3*b^2*Log[c*x^n]^(3*q)/(n*q)-2*a*b*x^m*Gamma[2*q,-m*Log[c*x^n]/n]*Log[c*x^n]^(2*q)/(n*(c*x^n)^(m/n)*(-m*Log[c*x^n]/n)^(2*q))-a^2*x^(2*m)*Gamma[q,-2*m*Log[c*x^n]/n]*Log[c*x^n]^q/(2^q*n*(c*x^n)^(2*m/n)*(-m*Log[c*x^n]/n)^q)");
  }

  // 3.5 Logarithm functions.input:28
  public void test0600() {
    check( //
        "Integrate[(a*m*x^m+b*n*q*Log[c*x^n]^(-1+q))*(a*x^m+b*Log[c*x^n]^q)^2/x, x]", //
        "1/3*(a*x^m+b*Log[c*x^n]^q)^3");
  }

  // 3.5 Logarithm functions.input:36
  public void test0601() {
    check( //
        "Integrate[a+2*b*n*Log[c*x^n]/x, x]", //
        "a*x+b*Log[c*x^n]^2");
  }

  // 3.5 Logarithm functions.input:47
  public void test0602() {
    check( //
        "Integrate[(d*x^m+e*Log[c*x^n]^(-1+q))/x, x]", //
        "d*x^m/m+e*Log[c*x^n]^q/(n*q)");
  }

  // 3.5 Logarithm functions.input:67
  public void test0603() {
    check( //
        "Integrate[(e*x)^m*(a+b*Log[c*Log[d*x]^p]), x]", //
        "-b*p*(d*x)^(-1-m)*(e*x)^(1+m)*ExpIntegralEi[(1+m)*Log[d*x]]/(e*(1+m))+(e*x)^(1+m)*(a+b*Log[c*Log[d*x]^p])/(e*(1+m))");
  }

  // 3.5 Logarithm functions.input:75
  public void test0604() {
    check( //
        "Integrate[(a+b*Log[c*Log[d*x^n]^p])/x^4, x]", //
        "1/3*b*p*(d*x^n)^(3/n)*ExpIntegralEi[-3*Log[d*x^n]/n]/x^3+1/3*(-a-b*Log[c*Log[d*x^n]^p])/x^3");
  }

  // 3.5 Logarithm functions.input:150
  public void test0605() {
    check( //
        "Integrate[x^3*Log[a+E^x*b], x]", //
        "1/4*x^4*Log[a+E^x*b]-1/4*x^4*Log[1+E^x*b/a]-x^3*PolyLog[2,-E^x*b/a]+3*x^2*PolyLog[3,-E^x*b/a]-6*x*PolyLog[4,-E^x*b/a]+6*PolyLog[5,-E^x*b/a]");
  }

  // 3.5 Logarithm functions.input:160
  public void test0606() {
    check( //
        "Integrate[x^3*Log[d+e*(f^(c*(a+b*x)))^n], x]", //
        "1/4*x^4*Log[d+e*(f^(c*(a+b*x)))^n]-1/4*x^4*Log[1+e*(f^(c*(a+b*x)))^n/d]-x^3*PolyLog[2,-e*(f^(c*(a+b*x)))^n/d]/(b*c*n*Log[f])+3*x^2*PolyLog[3,-e*(f^(c*(a+b*x)))^n/d]/(b^2*c^2*n^2*Log[f]^2)-6*x*PolyLog[4,-e*(f^(c*(a+b*x)))^n/d]/(b^3*c^3*n^3*Log[f]^3)+6*PolyLog[5,-e*(f^(c*(a+b*x)))^n/d]/(b^4*c^4*n^4*Log[f]^4)");
  }

  // 3.5 Logarithm functions.input:180
  public void test0607() {
    check( //
        "Integrate[(7-Log[x])/(x*(3+Log[x])), x]", //
        "-Log[x]+10*Log[3+Log[x]]");
  }

  // 3.5 Logarithm functions.input:188
  public void test0608() {
    check( //
        "Integrate[(Log[a*x^n]^2)^p/x, x]", //
        "Log[a*x^n]*(Log[a*x^n]^2)^p/(n*(1+2*p))");
  }

  // 3.5 Logarithm functions.input:218
  public void test0609() {
    check( //
        "Integrate[Log[a*Sec[x]], x]", //
        "-1/2*I*x^2+x*Log[1+E^(2*I*x)]+x*Log[a*Sec[x]]-1/2*I*PolyLog[2,-E^(2*I*x)]");
  }

  // 3.5 Logarithm functions.input:273
  public void test0610() {
    check( //
        "Integrate[Log[Cosh[x]^2]*Sinh[x], x]", //
        "-2*Cosh[x]+Cosh[x]*Log[Cosh[x]^2]");
  }

  // 3.5 Logarithm functions.input:289
  public void test0611() {
    check( //
        "Integrate[x*Log[x+x^3], x]", //
        "-3/4*x^2+1/2*Log[1+x^2]+1/2*x^2*Log[x+x^3]");
  }

  // 3.5 Logarithm functions.input:323
  public void test0612() {
    check( //
        "Integrate[1/(a*x+b*x/Log[c*x^n]), x]", //
        "Log[x]/a-b*Log[b+a*Log[c*x^n]]/(a^2*n)");
  }

  // 3.5 Logarithm functions.input:331
  public void test0613() {
    check( //
        "Integrate[Log[1/x]^2/x^5, x]", //
        "(-1/32)/x^4+1/8*Log[1/x]/x^4-1/4*Log[1/x]^2/x^4");
  }

  // 3.5 Logarithm functions.input:347
  public void test0614() {
    check( //
        "Integrate[Log[x^2/(1+x^2)]/(1+x^2), x]", //
        "I*ArcTan[x]^2-2*ArcTan[x]*Log[2+(-2)/(1-I*x)]+ArcTan[x]*Log[x^2/(1+x^2)]+I*PolyLog[2,-1+2/(1-I*x)]");
  }

  // 3.5 Logarithm functions.input:373
  public void test0615() {
    check( //
        "Integrate[(Log[x]^m)^p, x]", //
        "Gamma[1+m*p,-Log[x]]*(Log[x]^m)^p/(-Log[x])^(m*p)");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:27
  public void test0616() {
    check( //
        "Integrate[Log[c*x]^3, x]", //
        "-6*x+6*x*Log[c*x]-3*x*Log[c*x]^2+x*Log[c*x]^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:45
  public void test0617() {
    check( //
        "Integrate[1/(x^2*Log[c*x]^2), x]", //
        "-c*ExpIntegralEi[-Log[c*x]]+(-1)/(x*Log[c*x])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:53
  public void test0618() {
    check( //
        "Integrate[1/(x^3*Log[c*x]^3), x]", //
        "2*c^2*ExpIntegralEi[-2*Log[c*x]]+(-1/2)/(x^2*Log[c*x]^2)+1/(x^2*Log[c*x])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:73
  public void test0619() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])^3, x]", //
        "-2/27*b^3*n^3*x^3+2/9*b^2*n^2*x^3*(a+b*Log[c*x^n])-1/3*b*n*x^3*(a+b*Log[c*x^n])^2+1/3*x^3*(a+b*Log[c*x^n])^3");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:91
  public void test0620() {
    check( //
        "Integrate[x^2/(a+b*Log[c*x^n])^2, x]", //
        "3*x^3*ExpIntegralEi[3*(a+b*Log[c*x^n])/(b*n)]/(E^(3*a/(b*n))*b^2*n^2*(c*x^n)^(3/n))-x^3/(b*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:99
  public void test0621() {
    check( //
        "Integrate[x^2/(a+b*Log[c*x^n])^3, x]", //
        "9/2*x^3*ExpIntegralEi[3*(a+b*Log[c*x^n])/(b*n)]/(E^(3*a/(b*n))*b^3*n^3*(c*x^n)^(3/n))-1/2*x^3/(b*n*(a+b*Log[c*x^n])^2)-3/2*x^3/(b^2*n^2*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:119
  public void test0622() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(d*x)^(1/2), x]", //
        "16*b^2*n^2*Sqrt[d*x]/d-8*b*n*(a+b*Log[c*x^n])*Sqrt[d*x]/d+2*(a+b*Log[c*x^n])^2*Sqrt[d*x]/d");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:141
  public void test0623() {
    check( //
        "Integrate[x^3*Sqrt[Log[a*x^n]], x]", //
        "-1/16*x^4*Erfi[2*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]*Sqrt[n]/(a*x^n)^(4/n)+1/4*x^4*Sqrt[Log[a*x^n]]");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:167
  public void test0624() {
    check( //
        "Integrate[1/Log[a*x^n]^(3/2), x]", //
        "2*x*Erfi[Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/(n^(3/2)*(a*x^n)^(1/n))-2*x/(n*Sqrt[Log[a*x^n]])");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:185
  public void test0625() {
    check( //
        "Integrate[(d*x)^m/(a+b*Log[c*x^n])^2, x]", //
        "(1+m)*(d*x)^(1+m)*ExpIntegralEi[(1+m)*(a+b*Log[c*x^n])/(b*n)]/(E^(a*(1+m)/(b*n))*b^2*d*n^2*(c*x^n)^((1+m)/n))-(d*x)^(1+m)/(b*d*n*(a+b*Log[c*x^n]))");
  }

  // 3.1.2 (d x)^m (a+b log(c x^n))^p.input:193
  public void test0626() {
    check( //
        "Integrate[x^m*Log[a*x^n]^(3/2), x]", //
        "x^(1+m)*Log[a*x^n]^(3/2)/(1+m)+3/4*n^(3/2)*x^(1+m)*Erfi[Sqrt[1+m]*Sqrt[Log[a*x^n]]/Sqrt[n]]*Sqrt[Pi]/((1+m)^(5/2)*(a*x^n)^((1+m)/n))-3/2*n*x^(1+m)*Sqrt[Log[a*x^n]]/(1+m)^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:12
  public void test0627() {
    check( //
        "Integrate[x^3*(d+e*x)*(a+b*Log[c*x^n]), x]", //
        "-1/16*b*d*n*x^4-1/25*b*e*n*x^5+1/20*(5*d*x^4+4*e*x^5)*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:21
  public void test0628() {
    check( //
        "Integrate[x^2*(d+e*x)^2*(a+b*Log[c*x^n]), x]", //
        "-1/9*b*d^2*n*x^3-1/8*b*d*e*n*x^4-1/25*b*e^2*n*x^5+1/30*(10*d^2*x^3+15*d*e*x^4+6*e^2*x^5)*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:56
  public void test0629() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x)^2), x]", //
        "-e*x*(a+b*Log[c*x^n])/(d^2*(d+e*x))-Log[1+d/(e*x)]*(a+b*Log[c*x^n])/d^2+b*n*Log[d+e*x]/d^2+b*n*PolyLog[2,-d/(e*x)]/d^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:72
  public void test0630() {
    check( //
        "Integrate[(a+b*Log[c*x^n])/(x*(d+e*x)^4), x]", //
        "-1/6*b*n/(d^2*(d+e*x)^2)-5/6*b*n/(d^3*(d+e*x))-5/6*b*n*Log[x]/d^4+1/3*(a+b*Log[c*x^n])/(d*(d+e*x)^3)+1/2*(a+b*Log[c*x^n])/(d^2*(d+e*x)^2)-e*x*(a+b*Log[c*x^n])/(d^4*(d+e*x))-Log[1+d/(e*x)]*(a+b*Log[c*x^n])/d^4+11/6*b*n*Log[d+e*x]/d^4+b*n*PolyLog[2,-d/(e*x)]/d^4");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:100
  public void test0631() {
    check( //
        "Integrate[(d+e*x)*(a+b*Log[c*x^n])^2/x^5, x]", //
        "-1/32*b^2*d*n^2/x^4-2/27*b^2*e*n^2/x^3-1/8*b*d*n*(a+b*Log[c*x^n])/x^4-2/9*b*e*n*(a+b*Log[c*x^n])/x^3-1/4*d*(a+b*Log[c*x^n])^2/x^4-1/3*e*(a+b*Log[c*x^n])^2/x^3");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:108
  public void test0632() {
    check( //
        "Integrate[(d+e*x)^2*(a+b*Log[c*x^n])^2/x^5, x]", //
        "-1/32*b^2*d^2*n^2/x^4-4/27*b^2*d*e*n^2/x^3-1/4*b^2*e^2*n^2/x^2-1/8*b*d^2*n*(a+b*Log[c*x^n])/x^4-4/9*b*d*e*n*(a+b*Log[c*x^n])/x^3-1/2*b*e^2*n*(a+b*Log[c*x^n])/x^2-1/4*d^2*(a+b*Log[c*x^n])^2/x^4-2/3*d*e*(a+b*Log[c*x^n])^2/x^3-1/2*e^2*(a+b*Log[c*x^n])^2/x^2");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:118
  public void test0633() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x^4*(d+e*x)), x]", //
        "-2/27*b^2*n^2/(d*x^3)+1/4*b^2*e*n^2/(d^2*x^2)-2*b^2*e^2*n^2/(d^3*x)-2/9*b*n*(a+b*Log[c*x^n])/(d*x^3)+1/2*b*e*n*(a+b*Log[c*x^n])/(d^2*x^2)-2*b*e^2*n*(a+b*Log[c*x^n])/(d^3*x)-1/3*(a+b*Log[c*x^n])^2/(d*x^3)+1/2*e*(a+b*Log[c*x^n])^2/(d^2*x^2)-e^2*(a+b*Log[c*x^n])^2/(d^3*x)+e^3*Log[1+d/(e*x)]*(a+b*Log[c*x^n])^2/d^4-2*b*e^3*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x)]/d^4-2*b^2*e^3*n^2*PolyLog[3,-d/(e*x)]/d^4");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:128
  public void test0634() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])^2/(d+e*x)^3, x]", //
        "b*n*x*(a+b*Log[c*x^n])/(d*e*(d+e*x))+1/2*x^2*(a+b*Log[c*x^n])^2/(d*(d+e*x)^2)-b*n*(a+b*n+b*Log[c*x^n])*Log[1+e*x/d]/(d*e^2)-b^2*n^2*PolyLog[2,-e*x/d]/(d*e^2)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:233
  public void test0635() {
    check( //
        "Integrate[(d+e*x^2)*(a+b*Log[c*x^n])/x^2, x]", //
        "-b*d*n/x-b*e*n*x-d*(a+b*Log[c*x^n])/x+e*x*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:241
  public void test0636() {
    check( //
        "Integrate[(d+e*x^2)^2*(a+b*Log[c*x^n])/x^5, x]", //
        "-1/16*b*d^2*n/x^4-1/2*b*d*e*n/x^2-1/2*b*e^2*n*Log[x]^2-1/4*d^2*(a+b*Log[c*x^n])/x^4-d*e*(a+b*Log[c*x^n])/x^2+e^2*Log[x]*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:267
  public void test0637() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/(d+e*x^2), x]", //
        "1/2*(a+b*Log[c*x^n])*Log[1+e*x^2/d]/e+1/4*b*n*PolyLog[2,-e*x^2/d]/e");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:299
  public void test0638() {
    check( //
        "Integrate[Log[x]/(1+x^2), x]", //
        "ArcTan[x]*Log[x]-1/2*I*PolyLog[2,-I*x]+1/2*I*PolyLog[2,I*x]");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:331
  public void test0639() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Sqrt[d+e*x^2]/x, x]", //
        "b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]*Sqrt[d]+1/2*b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]^2*Sqrt[d]-b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]*Log[2*Sqrt[d]/(Sqrt[d]-Sqrt[d+e*x^2])]*Sqrt[d]-1/2*b*n*PolyLog[2,1-2*Sqrt[d]/(Sqrt[d]-Sqrt[d+e*x^2])]*Sqrt[d]-b*n*Sqrt[d+e*x^2]+(a+b*Log[c*x^n])*(-ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]*Sqrt[d]+Sqrt[d+e*x^2])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:357
  public void test0640() {
    check( //
        "Integrate[x*(a+b*Log[c*x^n])/Sqrt[d+e*x^2], x]", //
        "b*n*ArcTanh[Sqrt[d+e*x^2]/Sqrt[d]]*Sqrt[d]/e-b*n*Sqrt[d+e*x^2]/e+(a+b*Log[c*x^n])*Sqrt[d+e*x^2]/e");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:399
  public void test0641() {
    check( //
        "Integrate[(f*x)^m*(d+e*x^2)^3*(a+b*Log[c*x^n]), x]", //
        "-b*d^3*n*(f*x)^(1+m)/(f*(1+m)^2)-3*b*d^2*e*n*(f*x)^(3+m)/(f^3*(3+m)^2)-3*b*d*e^2*n*(f*x)^(5+m)/(f^5*(5+m)^2)-b*e^3*n*(f*x)^(7+m)/(f^7*(7+m)^2)+d^3*(f*x)^(1+m)*(a+b*Log[c*x^n])/(f*(1+m))+3*d^2*e*(f*x)^(3+m)*(a+b*Log[c*x^n])/(f^3*(3+m))+3*d*e^2*(f*x)^(5+m)*(a+b*Log[c*x^n])/(f^5*(5+m))+e^3*(f*x)^(7+m)*(a+b*Log[c*x^n])/(f^7*(7+m))");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:441
  public void test0642() {
    check( //
        "Integrate[x^(-1+n)*Log[x^n/d]/(d-x^n), x]", //
        "PolyLog[2,1-x^n/d]/n");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:454
  public void test0643() {
    check( //
        "Integrate[(f*x)^(-1+m)*(a+b*Log[c*x^n])/(d+e*x^m), x]", //
        "x^(1-m)*(f*x)^(-1+m)*(a+b*Log[c*x^n])*Log[1+e*x^m/d]/(e*m)+b*n*x^(1-m)*(f*x)^(-1+m)*PolyLog[2,-e*x^m/d]/(e*m^2)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:462
  public void test0644() {
    check( //
        "Integrate[(f*x)^(-1+m)*(a+b*Log[c*x^n])^2/(d+e*x^m), x]", //
        "x^(1-m)*(f*x)^(-1+m)*(a+b*Log[c*x^n])^2*Log[1+e*x^m/d]/(e*m)+2*b*n*x^(1-m)*(f*x)^(-1+m)*(a+b*Log[c*x^n])*PolyLog[2,-e*x^m/d]/(e*m^2)-2*b^2*n^2*x^(1-m)*(f*x)^(-1+m)*PolyLog[3,-e*x^m/d]/(e*m^3)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:476
  public void test0645() {
    check( //
        "Integrate[(d+e*x^r)*(a+b*Log[c*x^n])/x^3, x]", //
        "-1/4*b*d*n/x^2-b*e*n*x^(-2+r)/(2-r)^2-1/2*d*(a+b*Log[c*x^n])/x^2-e*x^(-2+r)*(a+b*Log[c*x^n])/(2-r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:484
  public void test0646() {
    check( //
        "Integrate[x^5*(d+e*x^r)^2*(a+b*Log[c*x^n]), x]", //
        "-1/36*b*d^2*n*x^6-1/4*b*e^2*n*x^(2*(3+r))/(3+r)^2-2*b*d*e*n*x^(6+r)/(6+r)^2+1/6*(d^2*x^6+3*e^2*x^(2*(3+r))/(3+r)+12*d*e*x^(6+r)/(6+r))*(a+b*Log[c*x^n])");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:508
  public void test0647() {
    check( //
        "Integrate[(d+e*x^r)^3*(a+b*Log[c*x^n])/x^6, x]", //
        "-1/25*b*d^3*n/x^5-3*b*d^2*e*n*x^(-5+r)/(5-r)^2-3*b*d*e^2*n*x^(-5+2*r)/(5-2*r)^2-b*e^3*n*x^(-5+3*r)/(5-3*r)^2-1/5*d^3*(a+b*Log[c*x^n])/x^5-3*d^2*e*x^(-5+r)*(a+b*Log[c*x^n])/(5-r)-3*d*e^2*x^(-5+2*r)*(a+b*Log[c*x^n])/(5-2*r)-e^3*x^(-5+3*r)*(a+b*Log[c*x^n])/(5-3*r)");
  }

  // 3.1.4 (f x)^m (d+e x^r)^q (a+b log(c x^n))^p.input:542
  public void test0648() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2/(x*(d+e*x^r)^2), x]", //
        "(a+b*Log[c*x^n])^2/(d*r*(d+e*x^r))+2*b*n*(a+b*Log[c*x^n])*Log[1+d/(e*x^r)]/(d^2*r^2)-(a+b*Log[c*x^n])^2*Log[1+d/(e*x^r)]/(d^2*r)-2*b^2*n^2*PolyLog[2,-d/(e*x^r)]/(d^2*r^3)+2*b*n*(a+b*Log[c*x^n])*PolyLog[2,-d/(e*x^r)]/(d^2*r^2)+2*b^2*n^2*PolyLog[3,-d/(e*x^r)]/(d^2*r^3)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:18
  public void test0649() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[1+e*x], x]", //
        "2*b*n*x-x*(a+b*Log[c*x^n])-b*n*(1+e*x)*Log[1+e*x]/e+(1+e*x)*(a+b*Log[c*x^n])*Log[1+e*x]/e+b*n*PolyLog[2,-e*x]/e");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:34
  public void test0650() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[1+e*x]/x, x]", //
        "-(a+b*Log[c*x^n])^3*PolyLog[2,-e*x]+3*b*n*(a+b*Log[c*x^n])^2*PolyLog[3,-e*x]-6*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[4,-e*x]+6*b^3*n^3*PolyLog[5,-e*x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:44
  public void test0651() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(1/d+f*x^2)], x]", //
        "4*b*n*x-2*x*(a+b*Log[c*x^n])-b*n*x*Log[1+d*f*x^2]+x*(a+b*Log[c*x^n])*Log[1+d*f*x^2]-2*b*n*ArcTan[x*Sqrt[d]*Sqrt[f]]/(Sqrt[d]*Sqrt[f])+2*ArcTan[x*Sqrt[d]*Sqrt[f]]*(a+b*Log[c*x^n])/(Sqrt[d]*Sqrt[f])-I*b*n*PolyLog[2,-I*x*Sqrt[d]*Sqrt[f]]/(Sqrt[d]*Sqrt[f])+I*b*n*PolyLog[2,I*x*Sqrt[d]*Sqrt[f]]/(Sqrt[d]*Sqrt[f])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:78
  public void test0652() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(1/d+f*Sqrt[x])]/x, x]", //
        "-2*(a+b*Log[c*x^n])^3*PolyLog[2,-d*f*Sqrt[x]]+12*b*n*(a+b*Log[c*x^n])^2*PolyLog[3,-d*f*Sqrt[x]]-48*b^2*n^2*(a+b*Log[c*x^n])*PolyLog[4,-d*f*Sqrt[x]]+96*b^3*n^3*PolyLog[5,-d*f*Sqrt[x]]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:110
  public void test0653() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^3*Log[d*(e+f*x)^m]/x, x]", //
        "1/4*(a+b*Log[c*x^n])^4*Log[d*(e+f*x)^m]/(b*n)-1/4*m*(a+b*Log[c*x^n])^4*Log[1+f*x/e]/(b*n)-m*(a+b*Log[c*x^n])^3*PolyLog[2,-f*x/e]+3*b*m*n*(a+b*Log[c*x^n])^2*PolyLog[3,-f*x/e]-6*b^2*m*n^2*(a+b*Log[c*x^n])*PolyLog[4,-f*x/e]+6*b^3*m*n^3*PolyLog[5,-f*x/e]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:120
  public void test0654() {
    check( //
        "Integrate[x^2*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m], x]", //
        "-8/9*b*e*m*n*x/f+4/27*b*m*n*x^3+2/9*b*e^(3/2)*m*n*ArcTan[x*Sqrt[f]/Sqrt[e]]/f^(3/2)+2/3*e*m*x*(a+b*Log[c*x^n])/f-2/9*m*x^3*(a+b*Log[c*x^n])-2/3*e^(3/2)*m*ArcTan[x*Sqrt[f]/Sqrt[e]]*(a+b*Log[c*x^n])/f^(3/2)-1/9*b*n*x^3*Log[d*(e+f*x^2)^m]+1/3*x^3*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]+1/3*I*b*e^(3/2)*m*n*PolyLog[2,-I*x*Sqrt[f]/Sqrt[e]]/f^(3/2)-1/3*I*b*e^(3/2)*m*n*PolyLog[2,I*x*Sqrt[f]/Sqrt[e]]/f^(3/2)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:128
  public void test0655() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x^5, x]", //
        "-7/32*b^2*f*m*n^2/(e*x^2)-1/16*b^2*f^2*m*n^2*Log[x]/e^2-3/8*b*f*m*n*(a+b*Log[c*x^n])/(e*x^2)+1/8*b*f^2*m*n*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])/e^2-1/4*f*m*(a+b*Log[c*x^n])^2/(e*x^2)+1/4*f^2*m*Log[1+e/(f*x^2)]*(a+b*Log[c*x^n])^2/e^2+1/32*b^2*f^2*m*n^2*Log[e+f*x^2]/e^2-1/32*b^2*n^2*Log[d*(e+f*x^2)^m]/x^4-1/8*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*x^2)^m]/x^4-1/4*(a+b*Log[c*x^n])^2*Log[d*(e+f*x^2)^m]/x^4-1/16*b^2*f^2*m*n^2*PolyLog[2,-e/(f*x^2)]/e^2-1/4*b*f^2*m*n*(a+b*Log[c*x^n])*PolyLog[2,-e/(f*x^2)]/e^2-1/8*b^2*f^2*m*n^2*PolyLog[3,-e/(f*x^2)]/e^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:146
  public void test0656() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^2, x]", //
        "-1/2*b*f^2*k*n*Log[x]/e^2+1/4*b*f^2*k*n*Log[x]^2/e^2-1/2*f^2*k*Log[x]*(a+b*Log[c*x^n])/e^2+b*f^2*k*n*Log[e+f*Sqrt[x]]/e^2+f^2*k*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e^2-2*b*f^2*k*n*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e^2-b*n*Log[d*(e+f*Sqrt[x])^k]/x-(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x-2*b*f^2*k*n*PolyLog[2,1+f*Sqrt[x]/e]/e^2-3*b*f*k*n/(e*Sqrt[x])-f*k*(a+b*Log[c*x^n])/(e*Sqrt[x])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:154
  public void test0657() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])]/x^3, x]", //
        "-37/108*b^2*f*n^2/(e*x^(3/2))+7/8*b^2*f^2*n^2/(e^2*x)-1/8*b^2*f^4*n^2*Log[x]/e^4+1/8*b^2*f^4*n^2*Log[x]^2/e^4-7/18*b*f*n*(a+b*Log[c*x^n])/(e*x^(3/2))+3/4*b*f^2*n*(a+b*Log[c*x^n])/(e^2*x)-1/4*b*f^4*n*Log[x]*(a+b*Log[c*x^n])/e^4-1/6*f*(a+b*Log[c*x^n])^2/(e*x^(3/2))+1/4*f^2*(a+b*Log[c*x^n])^2/(e^2*x)-1/12*f^4*(a+b*Log[c*x^n])^3/(b*e^4*n)+1/4*b^2*f^4*n^2*Log[e+f*Sqrt[x]]/e^4+1/2*b*f^4*n*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e^4-b^2*f^4*n^2*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e^4-1/4*b^2*n^2*Log[d*(e+f*Sqrt[x])]/x^2-1/2*b*n*(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])]/x^2-1/2*(a+b*Log[c*x^n])^2*Log[d*(e+f*Sqrt[x])]/x^2+1/2*f^4*(a+b*Log[c*x^n])^2*Log[1+f*Sqrt[x]/e]/e^4+2*b*f^4*n*(a+b*Log[c*x^n])*PolyLog[2,-f*Sqrt[x]/e]/e^4-b^2*f^4*n^2*PolyLog[2,1+f*Sqrt[x]/e]/e^4-4*b^2*f^4*n^2*PolyLog[3,-f*Sqrt[x]/e]/e^4-21/4*b^2*f^3*n^2/(e^3*Sqrt[x])-5/2*b*f^3*n*(a+b*Log[c*x^n])/(e^3*Sqrt[x])-1/2*f^3*(a+b*Log[c*x^n])^2/(e^3*Sqrt[x])");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:164
  public void test0658() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/x^(3/2), x]", //
        "2*b*f*k*n*Log[x]/e-1/2*b*f*k*n*Log[x]^2/e+f*k*Log[x]*(a+b*Log[c*x^n])/e-4*b*f*k*n*Log[e+f*Sqrt[x]]/e-2*f*k*(a+b*Log[c*x^n])*Log[e+f*Sqrt[x]]/e+4*b*f*k*n*Log[-f*Sqrt[x]/e]*Log[e+f*Sqrt[x]]/e+4*b*f*k*n*PolyLog[2,1+f*Sqrt[x]/e]/e-4*b*n*Log[d*(e+f*Sqrt[x])^k]/Sqrt[x]-2*(a+b*Log[c*x^n])*Log[d*(e+f*Sqrt[x])^k]/Sqrt[x]");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:196
  public void test0659() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*(d+e*Log[f*x^r])/x, x]", //
        "-1/6*e*r*(a+b*Log[c*x^n])^3/(b^2*n^2)+1/2*(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/(b*n)");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:204
  public void test0660() {
    check( //
        "Integrate[(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/x^2, x]", //
        "-2*b^2*e*n^2*r/x-2*b*e*n*(a+b*n)*r/x-e*(a^2+2*a*b*n+2*b^2*n^2)*r/x-2*b^2*e*n*r*Log[c*x^n]/x-2*b*e*(a+b*n)*r*Log[c*x^n]/x-b^2*e*r*Log[c*x^n]^2/x-2*b^2*n^2*(d+e*Log[f*x^r])/x-2*b*n*(a+b*Log[c*x^n])*(d+e*Log[f*x^r])/x-(a+b*Log[c*x^n])^2*(d+e*Log[f*x^r])/x");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:248
  public void test0661() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[k,e*x^q]/x, x]", //
        "(a+b*Log[c*x^n])*PolyLog[1+k,e*x^q]/q-b*n*PolyLog[2+k,e*x^q]/q^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:261
  public void test0662() {
    check( //
        "Integrate[(a+b*Log[c*x^n])*PolyLog[2,e*x]/x^2, x]", //
        "2*b*e*n*Log[x]-1/2*b*e*n*Log[x]^2+e*Log[x]*(a+b*Log[c*x^n])-2*b*e*n*Log[1-e*x]+2*b*n*Log[1-e*x]/x-e*(a+b*Log[c*x^n])*Log[1-e*x]+(a+b*Log[c*x^n])*Log[1-e*x]/x-b*e*n*PolyLog[2,e*x]-b*n*PolyLog[2,e*x]/x-(a+b*Log[c*x^n])*PolyLog[2,e*x]/x");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:288
  public void test0663() {
    check( //
        "Integrate[x*Log[c*(b*x^n)^p]^2, x]", //
        "1/4*n^2*p^2*x^2-1/2*n*p*x^2*Log[c*(b*x^n)^p]+1/2*x^2*Log[c*(b*x^n)^p]^2");
  }

  // 3.1.5 u (a+b log(c x^n))^p.input:310
  public void test0664() {
    check( //
        "Integrate[(a+b*Log[c*(d*x^m)^n])^p/x^2, x]", //
        "-E^(a/(b*m*n))*(c*(d*x^m)^n)^(1/(m*n))*Gamma[1+p,(a+b*Log[c*(d*x^m)^n])/(b*m*n)]*(a+b*Log[c*(d*x^m)^n])^p/(x*((a+b*Log[c*(d*x^m)^n])/(b*m*n))^p)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:26
  public void test0665() {
    check( //
        "Integrate[(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/(a*g+b*g*x)^2, x]", //
        "-2*B^2*n^2*(c+d*x)/((b*c-a*d)*g^2*(a+b*x))-2*B*n*(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/((b*c-a*d)*g^2*(a+b*x))-(c+d*x)*(A+B*Log[e*((a+b*x)/(c+d*x))^n])^2/((b*c-a*d)*g^2*(a+b*x))");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:48
  public void test0666() {
    check( //
        "Integrate[(c*g+d*g*x)^4*(A+B*Log[e*((a+b*x)/(c+d*x))^n]), x]", //
        "-1/5*B*(b*c-a*d)^4*g^4*n*x/b^4-1/10*B*(b*c-a*d)^3*g^4*n*(c+d*x)^2/(b^3*d)-1/15*B*(b*c-a*d)^2*g^4*n*(c+d*x)^3/(b^2*d)-1/20*B*(b*c-a*d)*g^4*n*(c+d*x)^4/(b*d)-1/5*B*(b*c-a*d)^5*g^4*n*Log[a+b*x]/(b^5*d)+1/5*g^4*(c+d*x)^5*(A+B*Log[e*((a+b*x)/(c+d*x))^n])/d");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:141
  public void test0667() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])^2/(a*g+b*g*x)^4, x]", //
        "-2*B^2*d^2*(c+d*x)/((b*c-a*d)^3*g^4*(a+b*x))+1/2*b*B^2*d*(c+d*x)^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-2/27*b^2*B^2*(c+d*x)^3/((b*c-a*d)^3*g^4*(a+b*x)^3)-2*B*d^2*(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)^3*g^4*(a+b*x))+b*B*d*(c+d*x)^2*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)^3*g^4*(a+b*x)^2)-2/9*b^2*B*(c+d*x)^3*(A+B*Log[e*(a+b*x)/(c+d*x)])/((b*c-a*d)^3*g^4*(a+b*x)^3)-d^2*(c+d*x)*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)^3*g^4*(a+b*x))+b*d*(c+d*x)^2*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)^3*g^4*(a+b*x)^2)-1/3*b^2*(c+d*x)^3*(A+B*Log[e*(a+b*x)/(c+d*x)])^2/((b*c-a*d)^3*g^4*(a+b*x)^3)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:162
  public void test0668() {
    check( //
        "Integrate[(a*g+b*g*x)^4*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2]), x]", //
        "2/5*B*(b*c-a*d)^4*g^4*x/d^4-1/5*B*(b*c-a*d)^3*g^4*(a+b*x)^2/(b*d^3)+2/15*B*(b*c-a*d)^2*g^4*(a+b*x)^3/(b*d^2)-1/10*B*(b*c-a*d)*g^4*(a+b*x)^4/(b*d)+1/5*g^4*(a+b*x)^5*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/b-2/5*B*(b*c-a*d)^5*g^4*Log[c+d*x]/(b*d^5)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:179
  public void test0669() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/(a*g+b*g*x)^5, x]", //
        "8*B^2*d^3*(c+d*x)/((b*c-a*d)^4*g^5*(a+b*x))-3*b*B^2*d^2*(c+d*x)^2/((b*c-a*d)^4*g^5*(a+b*x)^2)+8/9*b^2*B^2*d*(c+d*x)^3/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/8*b^3*B^2*(c+d*x)^4/((b*c-a*d)^4*g^5*(a+b*x)^4)+4*B*d^3*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^4*g^5*(a+b*x))-3*b*B*d^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^4*g^5*(a+b*x)^2)+4/3*b^2*B*d*(c+d*x)^3*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/4*b^3*B*(c+d*x)^4*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])/((b*c-a*d)^4*g^5*(a+b*x)^4)+d^3*(c+d*x)*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^4*g^5*(a+b*x))-3/2*b*d^2*(c+d*x)^2*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^4*g^5*(a+b*x)^2)+b^2*d*(c+d*x)^3*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^4*g^5*(a+b*x)^3)-1/4*b^3*(c+d*x)^4*(A+B*Log[e*(a+b*x)^2/(c+d*x)^2])^2/((b*c-a*d)^4*g^5*(a+b*x)^4)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:231
  public void test0670() {
    check( //
        "Integrate[(a*g+b*g*x)^3*(A+B*Log[e*(c+d*x)/(a+b*x)]), x]", //
        "1/4*B*(b*c-a*d)^3*g^3*x/d^3-1/8*B*(b*c-a*d)^2*g^3*(a+b*x)^2/(b*d^2)+1/12*B*(b*c-a*d)*g^3*(a+b*x)^3/(b*d)-1/4*B*(b*c-a*d)^4*g^3*Log[c+d*x]/(b*d^4)+1/4*g^3*(a+b*x)^4*(A+B*Log[e*(c+d*x)/(a+b*x)])/b");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:253
  public void test0671() {
    check( //
        "Integrate[1/((a*g+b*g*x)^2*(A+B*Log[e*(c+d*x)/(a+b*x)])), x]", //
        "-ExpIntegralEi[(A+B*Log[e*(c+d*x)/(a+b*x)])/B]/(E^(A/B)*B*(b*c-a*d)*e*g^2)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:268
  public void test0672() {
    check( //
        "Integrate[(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/(a*g+b*g*x), x]", //
        "-Log[(-b*c+a*d)/(d*(a+b*x))]*(A+B*Log[e*(c+d*x)^2/(a+b*x)^2])/(b*g)-2*B*PolyLog[2,1+(b*c-a*d)/(d*(a+b*x))]/(b*g)");
  }

  // 3.2.1 (f+g x)^m (A+B log(e ((a+b x) over (c+d x))^n))^p.input:312
  public void test0673() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)/(c+d*x)])/(f+g*x), x]", //
        "-B*Log[-g*(a+b*x)/(b*f-a*g)]*Log[f+g*x]/g+(A+B*Log[e*(a+b*x)/(c+d*x)])*Log[f+g*x]/g+B*Log[-g*(c+d*x)/(d*f-c*g)]*Log[f+g*x]/g-B*PolyLog[2,b*(f+g*x)/(b*f-a*g)]/g+B*PolyLog[2,d*(f+g*x)/(d*f-c*g)]/g");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:278
  public void test0674() {
    check( //
        "Integrate[1/((a+b*x)*(c+d*x)*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3), x]", //
        "(-1/2)/(B*(b*c-a*d)*n*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2)");
  }

  // 3.2.2 (f+g x)^m (h+i x)^q (A+B log(e ((a+b x) over (c+d x))^n))^p.input:312
  public void test0675() {
    check( //
        "Integrate[(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3/(a*f*h+b*g*h*x^2+h*(b*f*x+a*g*x)), x]", //
        "-(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^3*Log[1-(b*f-a*g)*(c+d*x)/((d*f-c*g)*(a+b*x))]/((b*f-a*g)*h)+3*B*n*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])^2*PolyLog[2,(b*f-a*g)*(c+d*x)/((d*f-c*g)*(a+b*x))]/((b*f-a*g)*h)+6*B^2*n^2*(A+B*Log[e*(a+b*x)^n/(c+d*x)^n])*PolyLog[3,(b*f-a*g)*(c+d*x)/((d*f-c*g)*(a+b*x))]/((b*f-a*g)*h)+6*B^3*n^3*PolyLog[4,(b*f-a*g)*(c+d*x)/((d*f-c*g)*(a+b*x))]/((b*f-a*g)*h)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:47
  public void test0676() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(g+h*x), x]", //
        "-p*r*Log[-h*(a+b*x)/(b*g-a*h)]*Log[g+h*x]/h-q*r*Log[-h*(c+d*x)/(d*g-c*h)]*Log[g+h*x]/h+Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]*Log[g+h*x]/h-p*r*PolyLog[2,b*(g+h*x)/(b*g-a*h)]/h-q*r*PolyLog[2,d*(g+h*x)/(d*g-c*h)]/h");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:55
  public void test0677() {
    check( //
        "Integrate[Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]^2, x]", //
        "2*(p+q)^2*r^2*x-2*(b*c-a*d)*q*(p+q)*r^2*Log[c+d*x]/(b*d)-2*(b*c-a*d)*p*q*r^2*Log[-d*(a+b*x)/(b*c-a*d)]*Log[c+d*x]/(b*d)-(b*c-a*d)*q^2*r^2*Log[c+d*x]^2/(b*d)-2*(p+q)*r*(a+b*x)*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/b+2*(b*c-a*d)*q*r*Log[c+d*x]*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]/(b*d)+(a+b*x)*Log[e*(f*(a+b*x)^p*(c+d*x)^q)^r]^2/b-2*(b*c-a*d)*p*q*r^2*PolyLog[2,b*(c+d*x)/(b*c-a*d)]/(b*d)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:65
  public void test0678() {
    check( //
        "Integrate[(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])/(1-c^2*x^2), x]", //
        "-1/2*(a+b*Log[Sqrt[1-c*x]/Sqrt[1+c*x]])^2/(b*c)");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:123
  public void test0679() {
    check( //
        "Integrate[Log[a/(a+b*x)]*Log[c*x/(a+b*x)]^2/(x*(a+b*x)), x]", //
        "-Log[c*x/(a+b*x)]^2*PolyLog[2,1-a/(a+b*x)]/a+2*Log[c*x/(a+b*x)]*PolyLog[3,1-a/(a+b*x)]/a-2*PolyLog[4,1-a/(a+b*x)]/a");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:131
  public void test0680() {
    check( //
        "Integrate[Log[c*(b+a*x)^2/x^2]^3, x]", //
        "x*Log[c*(b+a*x)^2/x^2]^3-6*b*Log[c*(b+a*x)^2/x^2]^2*Log[1-a*x/(b+a*x)]/a+24*b*Log[c*(b+a*x)^2/x^2]*PolyLog[2,a*x/(b+a*x)]/a+48*b*PolyLog[3,a*x/(b+a*x)]/a");
  }

  // 3.2.3 u log(e (f (a+b x)^p (c+d x)^q)^r)^s.input:139
  public void test0681() {
    check( //
        "Integrate[Log[(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]^2/((a+b*x)*(e+f*x)), x]", //
        "-Log[(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]^2*Log[1-(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]/(b*e-a*f)-2*Log[(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]*PolyLog[2,(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]/(b*e-a*f)+2*PolyLog[3,(b*e-a*f)*(c+d*x)/((d*e-c*f)*(a+b*x))]/(b*e-a*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:25
  public void test0682() {
    check( //
        "Integrate[1/Log[c*(d+e*x)]^(5/2), x]", //
        "-2/3*(d+e*x)/(e*Log[c*(d+e*x)]^(3/2))+4/3*Erfi[Sqrt[Log[c*(d+e*x)]]]*Sqrt[Pi]/(c*e)-4/3*(d+e*x)/(e*Sqrt[Log[c*(d+e*x)]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:49
  public void test0683() {
    check( //
        "Integrate[1/(a+b*Log[c*(d+e*x)^n])^(7/2), x]", //
        "-2/5*(d+e*x)/(b*e*n*(a+b*Log[c*(d+e*x)^n])^(5/2))-4/15*(d+e*x)/(b^2*e*n^2*(a+b*Log[c*(d+e*x)^n])^(3/2))+8/15*(d+e*x)*Erfi[Sqrt[a+b*Log[c*(d+e*x)^n]]/(Sqrt[b]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*n))*b^(7/2)*e*n^(7/2)*(c*(d+e*x)^n)^(1/n))-8/15*(d+e*x)/(b^3*e*n^3*Sqrt[a+b*Log[c*(d+e*x)^n]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:67
  public void test0684() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x)^n]), x]", //
        "-1/2*b*(e*f-d*g)*n*x/e-1/4*b*n*(f+g*x)^2/g-1/2*b*(e*f-d*g)^2*n*Log[d+e*x]/(e^2*g)+1/2*(f+g*x)^2*(a+b*Log[c*(d+e*x)^n])/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:75
  public void test0685() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x)^n])^2, x]", //
        "-2*a*b*(e*f-d*g)*n*x/e+2*b^2*(e*f-d*g)*n^2*x/e+1/4*b^2*g*n^2*(d+e*x)^2/e^2-2*b^2*(e*f-d*g)*n*(d+e*x)*Log[c*(d+e*x)^n]/e^2-1/2*b*g*n*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])/e^2+(e*f-d*g)*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e^2+1/2*g*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^2/e^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:83
  public void test0686() {
    check( //
        "Integrate[(f+g*x)*(a+b*Log[c*(d+e*x)^n])^3, x]", //
        "6*a*b^2*(e*f-d*g)*n^2*x/e-6*b^3*(e*f-d*g)*n^3*x/e-3/8*b^3*g*n^3*(d+e*x)^2/e^2+6*b^3*(e*f-d*g)*n^2*(d+e*x)*Log[c*(d+e*x)^n]/e^2+3/4*b^2*g*n^2*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])/e^2-3*b*(e*f-d*g)*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e^2-3/4*b*g*n*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^2/e^2+(e*f-d*g)*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e^2+1/2*g*(d+e*x)^2*(a+b*Log[c*(d+e*x)^n])^3/e^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:91
  public void test0687() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])^4/(f+g*x), x]", //
        "(a+b*Log[c*(d+e*x)^n])^4*Log[e*(f+g*x)/(e*f-d*g)]/g+4*b*n*(a+b*Log[c*(d+e*x)^n])^3*PolyLog[2,-g*(d+e*x)/(e*f-d*g)]/g-12*b^2*n^2*(a+b*Log[c*(d+e*x)^n])^2*PolyLog[3,-g*(d+e*x)/(e*f-d*g)]/g+24*b^3*n^3*(a+b*Log[c*(d+e*x)^n])*PolyLog[4,-g*(d+e*x)/(e*f-d*g)]/g-24*b^4*n^4*PolyLog[5,-g*(d+e*x)/(e*f-d*g)]/g");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:115
  public void test0688() {
    check( //
        "Integrate[Log[c*(a+b*x)^n]^2/x^4, x]", //
        "-1/3*b^2*n^2/(a^2*x)-b^3*n^2*Log[x]/a^3+1/3*b^3*n^2*Log[a+b*x]/a^3-1/3*b*n*Log[c*(a+b*x)^n]/(a*x^2)+2/3*b^2*n*(a+b*x)*Log[c*(a+b*x)^n]/(a^3*x)-1/3*Log[c*(a+b*x)^n]^2/x^3+2/3*b^3*n*Log[c*(a+b*x)^n]*Log[1-a/(a+b*x)]/a^3-2/3*b^3*n^2*PolyLog[2,a/(a+b*x)]/a^3");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:241
  public void test0689() {
    check( //
        "Integrate[(a+b*Log[c*(e+f*x)])/(d*e+d*f*x), x]", //
        "1/2*(a+b*Log[c*(e+f*x)])^2/(b*d*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:249
  public void test0690() {
    check( //
        "Integrate[(a+b*Log[c*(e+f*x)])^2/(d*e+d*f*x), x]", //
        "1/3*(a+b*Log[c*(e+f*x)])^3/(b*d*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:259
  public void test0691() {
    check( //
        "Integrate[1/((d*e+d*f*x)*(a+b*Log[c*(e+f*x)])), x]", //
        "Log[a+b*Log[c*(e+f*x)]]/(b*d*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:380
  public void test0692() {
    check( //
        "Integrate[(a+b*Log[2*e/(e+f*x)])/(e^2-f^2*x^2), x]", //
        "a*ArcTanh[f*x/e]/(e*f)+1/2*b*PolyLog[2,1-2*e/(e+f*x)]/(e*f)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:420
  public void test0693() {
    check( //
        "Integrate[(f+g/x)^3*x^3*(a+b*Log[c*(d+e*x)^n]), x]", //
        "1/4*b*(d*f-e*g)^3*n*x/e^3-1/8*b*(d*f-e*g)^2*n*(g+f*x)^2/(e^2*f)+1/12*b*(d*f-e*g)*n*(g+f*x)^3/(e*f)-1/16*b*n*(g+f*x)^4/f-1/4*b*(d*f-e*g)^4*n*Log[d+e*x]/(e^4*f)+1/4*(g+f*x)^4*(a+b*Log[c*(d+e*x)^n])/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:471
  public void test0694() {
    check( //
        "Integrate[Log[2*a/(a+b*x)]/((a-b*x)*(a+b*x)), x]", //
        "1/2*PolyLog[2,1-2*a/(a+b*x)]/(a*b)");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:493
  public void test0695() {
    check( //
        "Integrate[Log[x]/(x^2*(a+b*x+c*x^2)), x]", //
        "(-1)/(a*x)-Log[x]/(a*x)-1/2*b*Log[x]^2/a^2+1/2*Log[x]*Log[1+2*c*x/(b+Sqrt[b^2-4*a*c])]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/a^2+1/2*PolyLog[2,-2*c*x/(b+Sqrt[b^2-4*a*c])]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/a^2+1/2*Log[x]*Log[1+2*c*x/(b-Sqrt[b^2-4*a*c])]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/a^2+1/2*PolyLog[2,-2*c*x/(b-Sqrt[b^2-4*a*c])]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/a^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:509
  public void test0696() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])/x^3, x]", //
        "-3/4*b*e*m*n/(d*x)-1/4*b*e^2*m*n*Log[x]/d^2-1/2*b*e*n*Log[f*x^m]/(d*x)+1/2*b*e^2*n*Log[1+d/(e*x)]*Log[f*x^m]/d^2+1/4*b*e^2*m*n*Log[d+e*x]/d^2-1/4*(m/x^2+2*Log[f*x^m]/x^2)*(a+b*Log[c*(d+e*x)^n])-1/2*b*e^2*m*n*PolyLog[2,-d/(e*x)]/d^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:518
  public void test0697() {
    check( //
        "Integrate[Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])^3, x]", //
        "-12*a*b^2*m*n^2*x+18*b^3*m*n^3*x-6*b^2*m*n^2*(a-b*n)*x+6*a*b^2*n^2*x*Log[f*x^m]-6*b^3*n^3*x*Log[f*x^m]-18*b^3*m*n^2*(d+e*x)*Log[c*(d+e*x)^n]/e-6*b^3*d*m*n^2*Log[-e*x/d]*Log[c*(d+e*x)^n]/e+6*b^3*n^2*(d+e*x)*Log[f*x^m]*Log[c*(d+e*x)^n]/e+6*b*m*n*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^2/e+3*b*d*m*n*Log[-e*x/d]*(a+b*Log[c*(d+e*x)^n])^2/e-3*b*n*(d+e*x)*Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])^2/e-m*(d+e*x)*(a+b*Log[c*(d+e*x)^n])^3/e-d*m*Log[-e*x/d]*(a+b*Log[c*(d+e*x)^n])^3/e+(d+e*x)*Log[f*x^m]*(a+b*Log[c*(d+e*x)^n])^3/e-6*b^3*d*m*n^3*PolyLog[2,1+e*x/d]/e+6*b^2*d*m*n^2*(a+b*Log[c*(d+e*x)^n])*PolyLog[2,1+e*x/d]/e-3*b*d*m*n*(a+b*Log[c*(d+e*x)^n])^2*PolyLog[2,1+e*x/d]/e-6*b^3*d*m*n^3*PolyLog[3,1+e*x/d]/e+6*b^2*d*m*n^2*(a+b*Log[c*(d+e*x)^n])*PolyLog[3,1+e*x/d]/e-6*b^3*d*m*n^3*PolyLog[4,1+e*x/d]/e");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:539
  public void test0698() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x)^n])*(f+g*Log[c*(d+e*x)^n])/x^3, x]", //
        "b*e^2*g*n^2*Log[x]/d^2-1/2*(a+b*Log[c*(d+e*x)^n])*(f+g*Log[c*(d+e*x)^n])/x^2-1/2*e*n*(d+e*x)*(b*f+a*g+2*b*g*Log[c*(d+e*x)^n])/(d^2*x)-1/2*e^2*n*(b*f+a*g+2*b*g*Log[c*(d+e*x)^n])*Log[1-d/(d+e*x)]/d^2+b*e^2*g*n^2*PolyLog[2,d/(d+e*x)]/d^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:579
  public void test0699() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^m)^n])^4, x]", //
        "-24*a*b^3*m^3*n^3*x+24*b^4*m^4*n^4*x-24*b^4*m^3*n^3*(e+f*x)*Log[c*(d*(e+f*x)^m)^n]/f+12*b^2*m^2*n^2*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^2/f-4*b*m*n*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^3/f+(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^4/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:589
  public void test0700() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^m)^n])^(3/2), x]", //
        "(e+f*x)*(a+b*Log[c*(d*(e+f*x)^m)^n])^(3/2)/f+3/4*b^(3/2)*m^(3/2)*n^(3/2)*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/(Sqrt[b]*Sqrt[m]*Sqrt[n])]*Sqrt[Pi]/(E^(a/(b*m*n))*f*(c*(d*(e+f*x)^m)^n)^(1/(m*n)))-3/2*b*m*n*(e+f*x)*Sqrt[a+b*Log[c*(d*(e+f*x)^m)^n]]/f");
  }
}
