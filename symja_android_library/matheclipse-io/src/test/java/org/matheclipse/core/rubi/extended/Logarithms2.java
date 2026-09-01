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
public class Logarithms2 extends AbstractRubiTestCase {
  static boolean init = true;

  public Logarithms2(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("Logarithms2");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 3.3 u (a+b log(c (d+e x)^n))^p.input:605
  public void test0001() {
    check( //
        "Integrate[(g+h*x)^3*(a+b*Log[c*(d*(e+f*x)^p)^q]), x]", //
        "-1/4*b*(f*g-e*h)^3*p*q*x/f^3-1/8*b*(f*g-e*h)^2*p*q*(g+h*x)^2/(f^2*h)-1/12*b*(f*g-e*h)*p*q*(g+h*x)^3/(f*h)-1/16*b*p*q*(g+h*x)^4/h-1/4*b*(f*g-e*h)^4*p*q*Log[e+f*x]/(f^4*h)+1/4*(g+h*x)^4*(a+b*Log[c*(d*(e+f*x)^p)^q])/h");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:621
  public void test0002() {
    check( //
        "Integrate[(g+h*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^3, x]", //
        "6*a*b^2*(f*g-e*h)*p^2*q^2*x/f-6*b^3*(f*g-e*h)*p^3*q^3*x/f-3/8*b^3*h*p^3*q^3*(e+f*x)^2/f^2+6*b^3*(f*g-e*h)*p^2*q^2*(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f^2+3/4*b^2*h*p^2*q^2*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])/f^2-3*b*(f*g-e*h)*p*q*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^2-3/4*b*h*p*q*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^2/f^2+(f*g-e*h)*(e+f*x)*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f^2+1/2*h*(e+f*x)^2*(a+b*Log[c*(d*(e+f*x)^p)^q])^3/f^2");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:629
  public void test0003() {
    check( //
        "Integrate[Log[c*(d*(e+f*x)^p)^q], x]", //
        "-p*q*x+(e+f*x)*Log[c*(d*(e+f*x)^p)^q]/f");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:673
  public void test0004() {
    check( //
        "Integrate[1/(a+b*Log[c*(d*(e+f*x)^p)^q])^(5/2), x]", //
        "-2/3*(e+f*x)/(b*f*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])^(3/2))+4/3*(e+f*x)*Erfi[Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]]/(Sqrt[b]*Sqrt[p]*Sqrt[q])]*Sqrt[Pi]/(E^(a/(b*p*q))*b^(5/2)*f*p^(5/2)*q^(5/2)*(c*(d*(e+f*x)^p)^q)^(1/(p*q)))-4/3*(e+f*x)/(b^2*f*p^2*q^2*Sqrt[a+b*Log[c*(d*(e+f*x)^p)^q]])");
  }

  // 3.3 u (a+b log(c (d+e x)^n))^p.input:754
  public void test0005() {
    check( //
        "Integrate[(a+b*Log[c*(d*(e+f*x)^p)^q])^2/(g+h*x), x]", //
        "(a+b*Log[c*(d*(e+f*x)^p)^q])^2*Log[f*(g+h*x)/(f*g-e*h)]/h+2*b*p*q*(a+b*Log[c*(d*(e+f*x)^p)^q])*PolyLog[2,-h*(e+f*x)/(f*g-e*h)]/h-2*b^2*p^2*q^2*PolyLog[3,-h*(e+f*x)/(f*g-e*h)]/h");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:12
  public void test0006() {
    check( //
        "Integrate[x^4*Log[c*(a+b*x^2)^p], x]", //
        "-2/5*a^2*p*x/b^2+2/15*a*p*x^3/b-2/25*p*x^5+2/5*a^(5/2)*p*ArcTan[x*Sqrt[b]/Sqrt[a]]/b^(5/2)+1/5*x^5*Log[c*(a+b*x^2)^p]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:21
  public void test0007() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]/x^5, x]", //
        "-1/4*b*p/(a*x^2)-1/2*b^2*p*Log[x]/a^2+1/4*b^2*p*Log[a+b*x^2]/a^2-1/4*Log[c*(a+b*x^2)^p]/x^4");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:55
  public void test0008() {
    check( //
        "Integrate[Log[c*(a+b/x^2)^p]/x^2, x]", //
        "2*p/x-Log[c*(a+b/x^2)^p]/x+2*p*ArcTan[x*Sqrt[a]/Sqrt[b]]*Sqrt[a]/Sqrt[b]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:67
  public void test0009() {
    check( //
        "Integrate[Log[c*(a+b*Sqrt[x])^p]/x, x]", //
        "2*Log[-b*Sqrt[x]/a]*Log[c*(a+b*Sqrt[x])^p]+2*p*PolyLog[2,1+b*Sqrt[x]/a]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:116
  public void test0010() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^2/x^3, x]", //
        "b*p*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]/a-1/2*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/(a*x^2)+b*p^2*PolyLog[2,1+b*x^2/a]/a");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:132
  public void test0011() {
    check( //
        "Integrate[Log[c*(a+b*x^2)^p]^3/x^7, x]", //
        "b^3*p^3*Log[x]/a^3-1/2*b^2*p^2*(a+b*x^2)*Log[c*(a+b*x^2)^p]/(a^3*x^2)-b^3*p^2*Log[-b*x^2/a]*Log[c*(a+b*x^2)^p]/a^3-1/4*b*p*Log[c*(a+b*x^2)^p]^2/(a*x^4)+1/2*b^2*p*(a+b*x^2)*Log[c*(a+b*x^2)^p]^2/(a^3*x^2)-1/6*Log[c*(a+b*x^2)^p]^3/x^6-1/2*b^3*p^2*Log[c*(a+b*x^2)^p]*Log[1-a/(a+b*x^2)]/a^3+1/2*b^3*p*Log[c*(a+b*x^2)^p]^2*Log[1-a/(a+b*x^2)]/a^3+1/2*b^3*p^3*PolyLog[2,a/(a+b*x^2)]/a^3-b^3*p^2*Log[c*(a+b*x^2)^p]*PolyLog[2,a/(a+b*x^2)]/a^3-b^3*p^3*PolyLog[2,1+b*x^2/a]/a^3-b^3*p^3*PolyLog[3,a/(a+b*x^2)]/a^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:161
  public void test0012() {
    check( //
        "Integrate[x/Log[c*(a+b*x^2)], x]", //
        "1/2*LogIntegral[c*(a+b*x^2)]/(b*c)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:173
  public void test0013() {
    check( //
        "Integrate[Log[c*(d+e*x^3)^p]^2/x^4, x]", //
        "2/3*e*p*Log[-e*x^3/d]*Log[c*(d+e*x^3)^p]/d-1/3*(d+e*x^3)*Log[c*(d+e*x^3)^p]^2/(d*x^3)+2/3*e*p^2*PolyLog[2,1+e*x^3/d]/d");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:183
  public void test0014() {
    check( //
        "Integrate[x^2/Log[c*(d+e*x^3)^p], x]", //
        "1/3*(d+e*x^3)*ExpIntegralEi[Log[c*(d+e*x^3)^p]/p]/(e*p*(c*(d+e*x^3)^p)^(1/p))");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:212
  public void test0015() {
    check( //
        "Integrate[(f*x)^(-1-n)*Log[c*(d+e*x^n)^p]^2, x]", //
        "2*e*p*x^(1+n)*(f*x)^(-1-n)*Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/(d*n)-x*(f*x)^(-1-n)*(d+e*x^n)*Log[c*(d+e*x^n)^p]^2/(d*n)+2*e*p^2*x^(1+n)*(f*x)^(-1-n)*PolyLog[2,1+e*x^n/d]/(d*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:222
  public void test0016() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]^3/x, x]", //
        "Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]^3/n+3*p*Log[c*(d+e*x^n)^p]^2*PolyLog[2,1+e*x^n/d]/n-6*p^2*Log[c*(d+e*x^n)^p]*PolyLog[3,1+e*x^n/d]/n+6*p^3*PolyLog[4,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:244
  public void test0017() {
    check( //
        "Integrate[(d+e*x)^3*Log[c*(a+b*x^3)^p], x]", //
        "-3/4*(4*b*d^3-a*e^3)*p*x/b-9/4*d^2*e*p*x^2-d*e^2*p*x^3-3/16*e^3*p*x^4+1/4*a^(1/3)*(4*b*d^3-6*a^(1/3)*b^(2/3)*d^2*e-a*e^3)*p*Log[a^(1/3)+b^(1/3)*x]/b^(4/3)-1/8*a^(1/3)*(4*b*d^3-6*a^(1/3)*b^(2/3)*d^2*e-a*e^3)*p*Log[a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2]/b^(4/3)-1/4*d*(b*d^3-4*a*e^3)*p*Log[a+b*x^3]/(b*e)+1/4*(d+e*x)^4*Log[c*(a+b*x^3)^p]/e-1/4*a^(1/3)*(4*b*d^3+6*a^(1/3)*b^(2/3)*d^2*e-a*e^3)*p*ArcTan[(a^(1/3)-2*b^(1/3)*x)/(a^(1/3)*Sqrt[3])]*Sqrt[3]/b^(4/3)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:301
  public void test0018() {
    check( //
        "Integrate[Log[c*(a+b*x^3)^p]/(d+e*x), x]", //
        "-p*Log[-e*(a^(1/3)+b^(1/3)*x)/(b^(1/3)*d-a^(1/3)*e)]*Log[d+e*x]/e-p*Log[-e*((-1)^(2/3)*a^(1/3)+b^(1/3)*x)/(b^(1/3)*d-(-1)^(2/3)*a^(1/3)*e)]*Log[d+e*x]/e-p*Log[(-1)^(1/3)*e*(a^(1/3)+(-1)^(2/3)*b^(1/3)*x)/(b^(1/3)*d+(-1)^(1/3)*a^(1/3)*e)]*Log[d+e*x]/e+Log[d+e*x]*Log[c*(a+b*x^3)^p]/e-p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d-a^(1/3)*e)]/e-p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d+(-1)^(1/3)*a^(1/3)*e)]/e-p*PolyLog[2,b^(1/3)*(d+e*x)/(b^(1/3)*d-(-1)^(2/3)*a^(1/3)*e)]/e");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:353
  public void test0019() {
    check( //
        "Integrate[(f+g*x^2)^3*Log[c*(d+e*x^2)^p], x]", //
        "-2*f^3*p*x+2*d*f^2*g*p*x/e-6/5*d^2*f*g^2*p*x/e^2+2/7*d^3*g^3*p*x/e^3-2/3*f^2*g*p*x^3+2/5*d*f*g^2*p*x^3/e-2/21*d^2*g^3*p*x^3/e^2-6/25*f*g^2*p*x^5+2/35*d*g^3*p*x^5/e-2/49*g^3*p*x^7-2*d^(3/2)*f^2*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(3/2)+6/5*d^(5/2)*f*g^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(5/2)-2/7*d^(7/2)*g^3*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(7/2)+f^3*x*Log[c*(d+e*x^2)^p]+f^2*g*x^3*Log[c*(d+e*x^2)^p]+3/5*f*g^2*x^5*Log[c*(d+e*x^2)^p]+1/7*g^3*x^7*Log[c*(d+e*x^2)^p]+2*f^3*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[d]/Sqrt[e]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:418
  public void test0020() {
    check( //
        "Integrate[(f+g*x^2)*Log[c*(d+e*x^2)^p], x]", //
        "-2*f*p*x+2/3*d*g*p*x/e-2/9*g*p*x^3-2/3*d^(3/2)*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/e^(3/2)+f*x*Log[c*(d+e*x^2)^p]+1/3*g*x^3*Log[c*(d+e*x^2)^p]+2*f*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[d]/Sqrt[e]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:427
  public void test0021() {
    check( //
        "Integrate[(f+g*x^2)^2*Log[c*(d+e*x^2)^p]/x^5, x]", //
        "-1/4*e*f^2*p/(d*x^2)-1/2*e^2*f^2*p*Log[x]/d^2+2*e*f*g*p*Log[x]/d+1/4*e^2*f^2*p*Log[d+e*x^2]/d^2-e*f*g*p*Log[d+e*x^2]/d-1/4*f^2*Log[c*(d+e*x^2)^p]/x^4-f*g*Log[c*(d+e*x^2)^p]/x^2+1/2*g^2*Log[-e*x^2/d]*Log[c*(d+e*x^2)^p]+1/2*g^2*p*PolyLog[2,1+e*x^2/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:435
  public void test0022() {
    check( //
        "Integrate[(f+g*x^2)^2*Log[c*(d+e*x^2)^p]/x^6, x]", //
        "-2/15*e*f^2*p/(d*x^3)+2/5*e^2*f^2*p/(d^2*x)-4/3*e*f*g*p/(d*x)+2/5*e^(5/2)*f^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/d^(5/2)-4/3*e^(3/2)*f*g*p*ArcTan[x*Sqrt[e]/Sqrt[d]]/d^(3/2)-1/5*f^2*Log[c*(d+e*x^2)^p]/x^5-2/3*f*g*Log[c*(d+e*x^2)^p]/x^3-g^2*Log[c*(d+e*x^2)^p]/x+2*g^2*p*ArcTan[x*Sqrt[e]/Sqrt[d]]*Sqrt[e]/Sqrt[d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:473
  public void test0023() {
    check( //
        "Integrate[(f+g/x^n)^2*Log[c*(d+e*x^n)^p]/x, x]", //
        "-1/2*e*g^2*p/(d*n*x^n)+2*e*f*g*p*Log[x]/d-1/2*e^2*g^2*p*Log[x]/d^2-2*e*f*g*p*Log[d+e*x^n]/(d*n)+1/2*e^2*g^2*p*Log[d+e*x^n]/(d^2*n)-1/2*g^2*Log[c*(d+e*x^n)^p]/(n*x^(2*n))-2*f*g*Log[c*(d+e*x^n)^p]/(n*x^n)+f^2*Log[-e*x^n/d]*Log[c*(d+e*x^n)^p]/n+f^2*p*PolyLog[2,1+e*x^n/d]/n");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:483
  public void test0024() {
    check( //
        "Integrate[Log[c*(d+e*x^n)^p]/(x*(f+g/x^n)^2), x]", //
        "e*g*p*Log[d+e*x^n]/(f^2*(d*f-e*g)*n)+g*Log[c*(d+e*x^n)^p]/(f^2*n*(g+f*x^n))-e*g*p*Log[g+f*x^n]/(f^2*(d*f-e*g)*n)+Log[c*(d+e*x^n)^p]*Log[-e*(g+f*x^n)/(d*f-e*g)]/(f^2*n)+p*PolyLog[2,f*(d+e*x^n)/(d*f-e*g)]/(f^2*n)");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:510
  public void test0025() {
    check( //
        "Integrate[Log[(a+b*x)/x]/x, x]", //
        "-Log[b+a/x]*Log[-a/(b*x)]-PolyLog[2,1+a/(b*x)]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:540
  public void test0026() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^2/x, x]", //
        "2*Log[-e*Sqrt[x]/d]*(a+b*Log[c*(d+e*Sqrt[x])^n])^2+4*b*n*(a+b*Log[c*(d+e*Sqrt[x])^n])*PolyLog[2,1+e*Sqrt[x]/d]-4*b^2*n^2*PolyLog[3,1+e*Sqrt[x]/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:548
  public void test0027() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*Sqrt[x])^n])^3/x^2, x]", //
        "6*b^2*e^2*n^2*Log[-e*Sqrt[x]/d]*(a+b*Log[c*(d+e*Sqrt[x])^n])/d^2-(a+b*Log[c*(d+e*Sqrt[x])^n])^3/x-3*b*e^2*n*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*Log[1-d/(d+e*Sqrt[x])]/d^2+6*b^2*e^2*n^2*(a+b*Log[c*(d+e*Sqrt[x])^n])*PolyLog[2,d/(d+e*Sqrt[x])]/d^2+6*b^3*e^2*n^3*PolyLog[2,1+e*Sqrt[x]/d]/d^2+6*b^3*e^2*n^3*PolyLog[3,d/(d+e*Sqrt[x])]/d^2-3*b*e*n*(a+b*Log[c*(d+e*Sqrt[x])^n])^2*(d+e*Sqrt[x])/(d^2*Sqrt[x])");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:594
  public void test0028() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(1/3))^n])^3, x]", //
        "9/4*b^3*d*n^3*(d+e*x^(1/3))^2/e^3-2/9*b^3*n^3*(d+e*x^(1/3))^3/e^3+18*a*b^2*d^2*n^2*x^(1/3)/e^2-18*b^3*d^2*n^3*x^(1/3)/e^2+18*b^3*d^2*n^2*(d+e*x^(1/3))*Log[c*(d+e*x^(1/3))^n]/e^3-9/2*b^2*d*n^2*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])/e^3+2/3*b^2*n^2*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])/e^3-9*b*d^2*n*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^3+9/2*b*d*n*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^3-b*n*(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^2/e^3+3*d^2*(d+e*x^(1/3))*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^3-3*d*(d+e*x^(1/3))^2*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^3+(d+e*x^(1/3))^3*(a+b*Log[c*(d+e*x^(1/3))^n])^3/e^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:604
  public void test0029() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(2/3))^n])/x, x]", //
        "3/2*(a+b*Log[c*(d+e*x^(2/3))^n])*Log[-e*x^(2/3)/d]+3/2*b*n*PolyLog[2,1+e*x^(2/3)/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:620
  public void test0030() {
    check( //
        "Integrate[(a+b*Log[c*(d+e*x^(2/3))^n])^3/x, x]", //
        "3/2*(a+b*Log[c*(d+e*x^(2/3))^n])^3*Log[-e*x^(2/3)/d]+9/2*b*n*(a+b*Log[c*(d+e*x^(2/3))^n])^2*PolyLog[2,1+e*x^(2/3)/d]-9*b^2*n^2*(a+b*Log[c*(d+e*x^(2/3))^n])*PolyLog[3,1+e*x^(2/3)/d]+9*b^3*n^3*PolyLog[4,1+e*x^(2/3)/d]");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:670
  public void test0031() {
    check( //
        "Integrate[(a+b*Log[c*(d+e/x^(2/3))^n])^3/x^3, x]", //
        "-9/8*b^3*d*n^3*(d+e/x^(2/3))^2/e^3+1/9*b^3*n^3*(d+e/x^(2/3))^3/e^3-9*a*b^2*d^2*n^2/(e^2*x^(2/3))+9*b^3*d^2*n^3/(e^2*x^(2/3))-9*b^3*d^2*n^2*(d+e/x^(2/3))*Log[c*(d+e/x^(2/3))^n]/e^3+9/4*b^2*d*n^2*(d+e/x^(2/3))^2*(a+b*Log[c*(d+e/x^(2/3))^n])/e^3-1/3*b^2*n^2*(d+e/x^(2/3))^3*(a+b*Log[c*(d+e/x^(2/3))^n])/e^3+9/2*b*d^2*n*(d+e/x^(2/3))*(a+b*Log[c*(d+e/x^(2/3))^n])^2/e^3-9/4*b*d*n*(d+e/x^(2/3))^2*(a+b*Log[c*(d+e/x^(2/3))^n])^2/e^3+1/2*b*n*(d+e/x^(2/3))^3*(a+b*Log[c*(d+e/x^(2/3))^n])^2/e^3-3/2*d^2*(d+e/x^(2/3))*(a+b*Log[c*(d+e/x^(2/3))^n])^3/e^3+3/2*d*(d+e/x^(2/3))^2*(a+b*Log[c*(d+e/x^(2/3))^n])^3/e^3-1/2*(d+e/x^(2/3))^3*(a+b*Log[c*(d+e/x^(2/3))^n])^3/e^3");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:792
  public void test0032() {
    check( //
        "Integrate[Log[f*x^p]^3*(a+b*Log[c*(d+e*x^m)^n])/x, x]", //
        "1/4*Log[f*x^p]^4*(a+b*Log[c*(d+e*x^m)^n])/p-1/4*b*n*Log[f*x^p]^4*Log[1+e*x^m/d]/p-b*n*Log[f*x^p]^3*PolyLog[2,-e*x^m/d]/m+3*b*n*p*Log[f*x^p]^2*PolyLog[3,-e*x^m/d]/m^2-6*b*n*p^2*Log[f*x^p]*PolyLog[4,-e*x^m/d]/m^3+6*b*n*p^3*PolyLog[5,-e*x^m/d]/m^4");
  }

  // 3.4 u (a+b log(c (d+e x^m)^n))^p.input:810
  public void test0033() {
    check( //
        "Integrate[Log[c*(d+e/(f+g*x)^2)^q], x]", //
        "(f+g*x)*Log[c*(d+e/(f+g*x)^2)^q]/g+2*q*ArcTan[(f+g*x)*Sqrt[d]/Sqrt[e]]*Sqrt[e]/(g*Sqrt[d])");
  }

  // 3.5 Logarithm functions.input:13
  public void test0034() {
    check( //
        "Integrate[Log[c*x^n]^(-1+q)*(a*x^m+b*Log[c*x^n]^q)/x, x]", //
        "1/2*b*Log[c*x^n]^(2*q)/(n*q)-a*x^m*Gamma[q,-m*Log[c*x^n]/n]*Log[c*x^n]^q/(n*(c*x^n)^(m/n)*(-m*Log[c*x^n]/n)^q)");
  }

  // 3.5 Logarithm functions.input:29
  public void test0035() {
    check( //
        "Integrate[(a*m*x^m+b*n*q*Log[c*x^n]^(-1+q))*(a*x^m+b*Log[c*x^n]^q)/x, x]", //
        "1/2*(a*x^m+b*Log[c*x^n]^q)^2");
  }

  // 3.5 Logarithm functions.input:53
  public void test0036() {
    check( //
        "Integrate[(a*d*n*x^m-a*d*m*x^m*Log[c*x^n]-b*d*n*(-1+q)*Log[c*x^n]^q)/(x*(a*x^m+b*Log[c*x^n]^q)^2), x]", //
        "d*Log[c*x^n]/(a*x^m+b*Log[c*x^n]^q)");
  }

  // 3.5 Logarithm functions.input:68
  public void test0037() {
    check( //
        "Integrate[(e*x)^m*(a+b*Log[c*Log[d*x^n]^p]), x]", //
        "-b*p*(e*x)^(1+m)*ExpIntegralEi[(1+m)*Log[d*x^n]/n]/(e*(1+m)*(d*x^n)^((1+m)/n))+(e*x)^(1+m)*(a+b*Log[c*Log[d*x^n]^p])/(e*(1+m))");
  }

  // 3.5 Logarithm functions.input:89
  public void test0038() {
    check( //
        "Integrate[Log[d*(b*x+c*x^2)^n], x]", //
        "-2*n*x+b*n*Log[b+c*x]/c+x*Log[d*(b*x+c*x^2)^n]");
  }

  // 3.5 Logarithm functions.input:98
  public void test0039() {
    check( //
        "Integrate[x^2*Log[d*(a+b*x+c*x^2)^n], x]", //
        "-1/3*(b^2-2*a*c)*n*x/c^2+1/6*b*n*x^2/c-2/9*n*x^3+1/6*b*(b^2-3*a*c)*n*Log[a+b*x+c*x^2]/c^3+1/3*x^3*Log[d*(a+b*x+c*x^2)^n]+1/3*(b^2-a*c)*n*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]*Sqrt[b^2-4*a*c]/c^3");
  }

  // 3.5 Logarithm functions.input:106
  public void test0040() {
    check( //
        "Integrate[Log[1+x+x^2], x]", //
        "-2*x+1/2*Log[1+x+x^2]+x*Log[1+x+x^2]+ArcTan[(1+2*x)/Sqrt[3]]*Sqrt[3]");
  }

  // 3.5 Logarithm functions.input:130
  public void test0041() {
    check( //
        "Integrate[Log[d*(a+b*x+c*x^2)^n]^2, x]", //
        "8*n^2*x-2*b*n^2*Log[a+b*x+c*x^2]/c-4*n*x*Log[d*(a+b*x+c*x^2)^n]+x*Log[d*(a+b*x+c*x^2)^n]^2+n*Log[d*(a+b*x+c*x^2)^n]*Log[b+2*c*x-Sqrt[b^2-4*a*c]]*(b-Sqrt[b^2-4*a*c])/c-1/2*n^2*Log[b+2*c*x-Sqrt[b^2-4*a*c]]^2*(b-Sqrt[b^2-4*a*c])/c-n^2*Log[b+2*c*x-Sqrt[b^2-4*a*c]]*Log[1/2*(b+2*c*x+Sqrt[b^2-4*a*c])/Sqrt[b^2-4*a*c]]*(b-Sqrt[b^2-4*a*c])/c-n^2*PolyLog[2,1/2*(-b-2*c*x+Sqrt[b^2-4*a*c])/Sqrt[b^2-4*a*c]]*(b-Sqrt[b^2-4*a*c])/c-4*n^2*ArcTanh[(b+2*c*x)/Sqrt[b^2-4*a*c]]*Sqrt[b^2-4*a*c]/c+n*Log[d*(a+b*x+c*x^2)^n]*Log[b+2*c*x+Sqrt[b^2-4*a*c]]*(b+Sqrt[b^2-4*a*c])/c-n^2*Log[1/2*(-b-2*c*x+Sqrt[b^2-4*a*c])/Sqrt[b^2-4*a*c]]*Log[b+2*c*x+Sqrt[b^2-4*a*c]]*(b+Sqrt[b^2-4*a*c])/c-1/2*n^2*Log[b+2*c*x+Sqrt[b^2-4*a*c]]^2*(b+Sqrt[b^2-4*a*c])/c-n^2*PolyLog[2,1/2*(b+2*c*x+Sqrt[b^2-4*a*c])/Sqrt[b^2-4*a*c]]*(b+Sqrt[b^2-4*a*c])/c");
  }

  // 3.5 Logarithm functions.input:151
  public void test0042() {
    check( //
        "Integrate[x^2*Log[a+E^x*b], x]", //
        "1/3*x^3*Log[a+E^x*b]-1/3*x^3*Log[1+E^x*b/a]-x^2*PolyLog[2,-E^x*b/a]+2*x*PolyLog[3,-E^x*b/a]-2*PolyLog[4,-E^x*b/a]");
  }

  // 3.5 Logarithm functions.input:161
  public void test0043() {
    check( //
        "Integrate[x^2*Log[d+e*(f^(c*(a+b*x)))^n], x]", //
        "1/3*x^3*Log[d+e*(f^(c*(a+b*x)))^n]-1/3*x^3*Log[1+e*(f^(c*(a+b*x)))^n/d]-x^2*PolyLog[2,-e*(f^(c*(a+b*x)))^n/d]/(b*c*n*Log[f])+2*x*PolyLog[3,-e*(f^(c*(a+b*x)))^n/d]/(b^2*c^2*n^2*Log[f]^2)-2*PolyLog[4,-e*(f^(c*(a+b*x)))^n/d]/(b^3*c^3*n^3*Log[f]^3)");
  }

  // 3.5 Logarithm functions.input:189
  public void test0044() {
    check( //
        "Integrate[(Log[a*x^n]^m)^p/x, x]", //
        "Log[a*x^n]*(Log[a*x^n]^m)^p/(n*(1+m*p))");
  }

  // 3.5 Logarithm functions.input:306
  public void test0045() {
    check( //
        "Integrate[Log[x+x^3], x]", //
        "-3*x+2*ArcTan[x]+x*Log[x+x^3]");
  }

  // 3.5 Logarithm functions.input:324
  public void test0046() {
    check( //
        "Integrate[1/(a*x+b*x/Log[c*x^n]^2), x]", //
        "Log[x]/a-ArcTan[Log[c*x^n]*Sqrt[a]/Sqrt[b]]*Sqrt[b]/(a^(3/2)*n)");
  }

  // 3.5 Logarithm functions.input:364
  public void test0047() {
    check( //
        "Integrate[(1+x)/(Log[x]*(x+Log[x])), x]", //
        "LogIntegral[x]+Log[Log[x]]-Log[x+Log[x]]");
  }

  // 3.5 Logarithm functions.input:376
  public void test0048() {
    check( //
        "Integrate[Log[x]/Sqrt[a+b*Log[x]], x]", //
        "-1/2*(2*a+b)*Erfi[Sqrt[a+b*Log[x]]/Sqrt[b]]*Sqrt[Pi]/(E^(a/b)*b^(3/2))+x*Sqrt[a+b*Log[x]]/b");
  }
}
