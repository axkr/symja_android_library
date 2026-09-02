package org.matheclipse.core.rubi.extended;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Integration test cases from Rubi's official test suite that Symja solves to Rubi's own reference
 * antiderivative, and that the hand-curated {@code org.matheclipse.core.rubi} classes do not
 * already cover.
 *
 * <p>
 * Source: 2 Exponentials of the test archive published at
 * <a href="https://rulebasedintegration.org/testProblems.html">rulebasedintegration.org</a>. Each
 * test names the source file and line it came from. Cases Rubi itself leaves unintegrated, and
 * cases whose reference uses functions Symja does not implement, are not included.
 */
public class Exponentials extends AbstractRubiTestCase {
  static boolean init = true;

  public Exponentials(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("Exponentials");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 2.1 u (F^(c (a+b x)))^n.input:25
  public void test0001() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d^2+2*d*e*x+e^2*x^2), x]", //
        "2*e^2*F^(c*(a+b*x))/(b^3*c^3*Log[F]^3)-2*e*F^(c*(a+b*x))*(d+e*x)/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^2/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:43
  public void test0002() {
    check( //
        "Integrate[F^(a+b*x)*x^(7/2), x]", //
        "35/4*F^(a+b*x)*x^(3/2)/(b^3*Log[F]^3)-7/2*F^(a+b*x)*x^(5/2)/(b^2*Log[F]^2)+F^(a+b*x)*x^(7/2)/(b*Log[F])+105/16*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(9/2)*Log[F]^(9/2))-105/8*F^(a+b*x)*Sqrt[x]/(b^4*Log[F]^4)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:51
  public void test0003() {
    check( //
        "Integrate[F^(a+b*x)/x^(9/2), x]", //
        "-2/7*F^(a+b*x)/x^(7/2)-4/35*b*F^(a+b*x)*Log[F]/x^(5/2)-8/105*b^2*F^(a+b*x)*Log[F]^2/x^(3/2)+16/105*b^(7/2)*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Log[F]^(7/2)*Sqrt[Pi]-16/105*b^3*F^(a+b*x)*Log[F]^3/Sqrt[x]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:59
  public void test0004() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^(7/2), x]", //
        "-2/5*F^(c*(a+b*x))/(e*(d+e*x)^(5/2))-4/15*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^(3/2))+8/15*b^(5/2)*c^(5/2)*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Log[F]^(5/2)*Sqrt[Pi]/e^(7/2)-8/15*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*Sqrt[d+e*x])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:71
  public void test0005() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x+f*x^2+g*x^3+h*x^4), x]", //
        "24*F^(c*(a+b*x))*h/(b^5*c^5*Log[F]^5)-6*F^(c*(a+b*x))*g/(b^4*c^4*Log[F]^4)-24*F^(c*(a+b*x))*h*x/(b^4*c^4*Log[F]^4)+2*f*F^(c*(a+b*x))/(b^3*c^3*Log[F]^3)+6*F^(c*(a+b*x))*g*x/(b^3*c^3*Log[F]^3)+12*F^(c*(a+b*x))*h*x^2/(b^3*c^3*Log[F]^3)-e*F^(c*(a+b*x))/(b^2*c^2*Log[F]^2)-2*f*F^(c*(a+b*x))*x/(b^2*c^2*Log[F]^2)-3*F^(c*(a+b*x))*g*x^2/(b^2*c^2*Log[F]^2)-4*F^(c*(a+b*x))*h*x^3/(b^2*c^2*Log[F]^2)+d*F^(c*(a+b*x))/(b*c*Log[F])+e*F^(c*(a+b*x))*x/(b*c*Log[F])+f*F^(c*(a+b*x))*x^2/(b*c*Log[F])+F^(c*(a+b*x))*g*x^3/(b*c*Log[F])+F^(c*(a+b*x))*h*x^4/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:81
  public void test0006() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^3/x^3, x]", //
        "-E^(-a-b*x)*b^2-1/2*E^(-a-b*x)*a^3/x^2-3*E^(-a-b*x)*a^2*b/x+1/2*E^(-a-b*x)*a^3*b/x+3*a*b^2*ExpIntegralEi[-b*x]/E^a-3*a^2*b^2*ExpIntegralEi[-b*x]/E^a+1/2*a^3*b^2*ExpIntegralEi[-b*x]/E^a");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:99
  public void test0007() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4/(c+d*x), x]", //
        "-6*E^(-a-b*x)/d+2*E^(-a-b*x)*(b*c-a*d)/d^2-E^(-a-b*x)*(b*c-a*d)^2/d^3+E^(-a-b*x)*(b*c-a*d)^3/d^4-6*E^(-a-b*x)*(a+b*x)/d+2*E^(-a-b*x)*(b*c-a*d)*(a+b*x)/d^2-E^(-a-b*x)*(b*c-a*d)^2*(a+b*x)/d^3-3*E^(-a-b*x)*(a+b*x)^2/d+E^(-a-b*x)*(b*c-a*d)*(a+b*x)^2/d^2-E^(-a-b*x)*(a+b*x)^3/d+E^(-a+b*c/d)*(b*c-a*d)^4*ExpIntegralEi[-b*(c+d*x)/d]/d^5");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:15
  public void test0008() {
    check( //
        "Integrate[1/(a+E^(c+d*x)*b), x]", //
        "x/a-Log[a+E^(c+d*x)*b]/(a*d)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:41
  public void test0009() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)*(c+d*x)^2, x]", //
        "1/3*a*(c+d*x)^3/d+2*b*d^2*(F^(e*g+f*g*x))^n/(f^3*g^3*n^3*Log[F]^3)-2*b*d*(F^(e*g+f*g*x))^n*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)+b*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:49
  public void test0010() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x), x]", //
        "1/2*a^2*(c+d*x)^2/d-2*a*b*d*(F^(e*g+f*g*x))^n/(f^2*g^2*n^2*Log[F]^2)-1/4*b^2*d*(F^(e*g+f*g*x))^(2*n)/(f^2*g^2*n^2*Log[F]^2)+2*a*b*(F^(e*g+f*g*x))^n*(c+d*x)/(f*g*n*Log[F])+1/2*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:57
  public void test0011() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3, x]", //
        "a^3*x+3*a^2*b*(F^(g*(e+f*x)))^n/(f*g*n*Log[F])+3/2*a*b^2*(F^(g*(e+f*x)))^(2*n)/(f*g*n*Log[F])+1/3*b^3*(F^(g*(e+f*x)))^(3*n)/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:69
  public void test0012() {
    check( //
        "Integrate[(c+d*x)^3/(a+b*(F^(g*(e+f*x)))^n)^2, x]", //
        "1/4*(c+d*x)^4/(a^2*d)-(c+d*x)^3/(a^2*f*g*n*Log[F])+(c+d*x)^3/(a*f*(a+b*(F^(g*(e+f*x)))^n)*g*n*Log[F])+3*d*(c+d*x)^2*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a^2*f^2*g^2*n^2*Log[F]^2)-(c+d*x)^3*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a^2*f*g*n*Log[F])+6*d^2*(c+d*x)*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^3*g^3*n^3*Log[F]^3)-3*d*(c+d*x)^2*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^2*g^2*n^2*Log[F]^2)-6*d^3*PolyLog[3,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^4*g^4*n^4*Log[F]^4)+6*d^2*(c+d*x)*PolyLog[3,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^3*g^3*n^3*Log[F]^3)-6*d^3*PolyLog[4,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^4*g^4*n^4*Log[F]^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:85
  public void test0013() {
    check( //
        "Integrate[(a+E^x*b)*Sqrt[c+d*x], x]", //
        "2/3*a*(c+d*x)^(3/2)/d-1/2*b*Erfi[Sqrt[c+d*x]/Sqrt[d]]*Sqrt[Pi]*Sqrt[d]/E^(c/d)+E^x*b*Sqrt[c+d*x]");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:109
  public void test0014() {
    check( //
        "Integrate[F^(c+d*x)*x/(a+b*F^(c+d*x)), x]", //
        "x*Log[1+b*F^(c+d*x)/a]/(b*d*Log[F])+PolyLog[2,-b*F^(c+d*x)/a]/(b*d^2*Log[F]^2)");
  }

  // 2.3 Exponential functions.input:36
  public void test0015() {
    check( //
        "Integrate[E^(4*x)/(a+E^(2*x)*b)^2, x]", //
        "1/2*a/(b^2*(a+E^(2*x)*b))+1/2*Log[a+E^(2*x)*b]/b^2");
  }

  // 2.3 Exponential functions.input:44
  public void test0016() {
    check( //
        "Integrate[1/(E^(n*x)*(a+E^(n*x)*b)^2), x]", //
        "(-1)/(E^(n*x)*a^2*n)-b/(a^2*(a+E^(n*x)*b)*n)-2*b*x/a^3+2*b*Log[a+E^(n*x)*b]/(a^3*n)");
  }

  // 2.3 Exponential functions.input:60
  public void test0017() {
    check( //
        "Integrate[f^x/(a+b*f^(2*x))^2, x]", //
        "1/2*f^x/(a*(a+b*f^(2*x))*Log[f])+1/2*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Log[f]*Sqrt[b])");
  }

  // 2.3 Exponential functions.input:111
  public void test0018() {
    check( //
        "Integrate[f^(a+b*x^2)/x^4, x]", //
        "-1/3*f^(a+b*x^2)/x^3-2/3*b*f^(a+b*x^2)*Log[f]/x+2/3*b^(3/2)*f^a*Erfi[x*Sqrt[b]*Sqrt[Log[f]]]*Log[f]^(3/2)*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:119
  public void test0019() {
    check( //
        "Integrate[f^(a+b*x^3)*x^11, x]", //
        "-2*f^(a+b*x^3)/(b^4*Log[f]^4)+2*f^(a+b*x^3)*x^3/(b^3*Log[f]^3)-f^(a+b*x^3)*x^6/(b^2*Log[f]^2)+1/3*f^(a+b*x^3)*x^9/(b*Log[f])");
  }

  // 2.3 Exponential functions.input:161
  public void test0020() {
    check( //
        "Integrate[f^(a+b/x^2)/x^9, x]", //
        "3*f^(a+b/x^2)/(b^4*Log[f]^4)-3*f^(a+b/x^2)/(b^3*x^2*Log[f]^3)+3/2*f^(a+b/x^2)/(b^2*x^4*Log[f]^2)-1/2*f^(a+b/x^2)/(b*x^6*Log[f])");
  }

  // 2.3 Exponential functions.input:169
  public void test0021() {
    check( //
        "Integrate[f^(a+b/x^2), x]", //
        "f^(a+b/x^2)*x-f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Sqrt[Pi]*Sqrt[b]*Sqrt[Log[f]]");
  }

  // 2.3 Exponential functions.input:251
  public void test0022() {
    check( //
        "Integrate[f^(c/(a+b*x))*x^2, x]", //
        "a^2*f^(c/(a+b*x))*(a+b*x)/b^3-a*f^(c/(a+b*x))*(a+b*x)^2/b^3+1/3*f^(c/(a+b*x))*(a+b*x)^3/b^3-a*c*f^(c/(a+b*x))*(a+b*x)*Log[f]/b^3+1/6*c*f^(c/(a+b*x))*(a+b*x)^2*Log[f]/b^3-a^2*c*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]/b^3+1/6*c^2*f^(c/(a+b*x))*(a+b*x)*Log[f]^2/b^3+a*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^2/b^3-1/6*c^3*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^3/b^3");
  }

  // 2.3 Exponential functions.input:259
  public void test0023() {
    check( //
        "Integrate[f^(c/(a+b*x)^2)*x^2, x]", //
        "a^2*f^(c/(a+b*x)^2)*(a+b*x)/b^3-a*f^(c/(a+b*x)^2)*(a+b*x)^2/b^3+1/3*f^(c/(a+b*x)^2)*(a+b*x)^3/b^3+2/3*c*f^(c/(a+b*x)^2)*(a+b*x)*Log[f]/b^3+a*c*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]/b^3-2/3*c^(3/2)*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Log[f]^(3/2)*Sqrt[Pi]/b^3-a^2*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Sqrt[Pi]*Sqrt[c]*Sqrt[Log[f]]/b^3");
  }

  // 2.3 Exponential functions.input:298
  public void test0024() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(c+d*x)^7, x]", //
        "-3*F^(a+b*(c+d*x)^2)/(b^4*d*Log[F]^4)+3*F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b^3*d*Log[F]^3)-3/2*F^(a+b*(c+d*x)^2)*(c+d*x)^4/(b^2*d*Log[F]^2)+1/2*F^(a+b*(c+d*x)^2)*(c+d*x)^6/(b*d*Log[F])");
  }

  // 2.3 Exponential functions.input:330
  public void test0025() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^3)/(c+d*x)^7, x]", //
        "-1/6*F^(a+b*(c+d*x)^3)/(d*(c+d*x)^6)-1/6*b*F^(a+b*(c+d*x)^3)*Log[F]/(d*(c+d*x)^3)+1/6*b^2*F^a*ExpIntegralEi[b*(c+d*x)^3*Log[F]]*Log[F]^2/d");
  }

  // 2.3 Exponential functions.input:348
  public void test0026() {
    check( //
        "Integrate[F^(a+b/(c+d*x))*(c+d*x), x]", //
        "1/2*F^(a+b/(c+d*x))*(c+d*x)^2/d+1/2*b*F^(a+b/(c+d*x))*(c+d*x)*Log[F]/d-1/2*b^2*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)]*Log[F]^2/d");
  }

  // 2.3 Exponential functions.input:372
  public void test0027() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)*(c+d*x)^6, x]", //
        "1/7*F^(a+b/(c+d*x)^2)*(c+d*x)^7/d+2/35*b*F^(a+b/(c+d*x)^2)*(c+d*x)^5*Log[F]/d+4/105*b^2*F^(a+b/(c+d*x)^2)*(c+d*x)^3*Log[F]^2/d+8/105*b^3*F^(a+b/(c+d*x)^2)*(c+d*x)*Log[F]^3/d-8/105*b^(7/2)*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Log[F]^(7/2)*Sqrt[Pi]/d");
  }

  // 2.3 Exponential functions.input:380
  public void test0028() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)/(c+d*x)^10, x]", //
        "105/16*F^(a+b/(c+d*x)^2)/(b^4*d*(c+d*x)*Log[F]^4)-35/8*F^(a+b/(c+d*x)^2)/(b^3*d*(c+d*x)^3*Log[F]^3)+7/4*F^(a+b/(c+d*x)^2)/(b^2*d*(c+d*x)^5*Log[F]^2)-1/2*F^(a+b/(c+d*x)^2)/(b*d*(c+d*x)^7*Log[F])-105/32*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Sqrt[Pi]/(b^(9/2)*d*Log[F]^(9/2))");
  }

  // 2.3 Exponential functions.input:422
  public void test0029() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-3*n), x]", //
        "-1/3*F^(a+b*(c+d*x)^n)/(d*n*(c+d*x)^(3*n))-1/6*b*F^(a+b*(c+d*x)^n)*Log[F]/(d*n*(c+d*x)^(2*n))-1/6*b^2*F^(a+b*(c+d*x)^n)*Log[F]^2/(d*n*(c+d*x)^n)+1/6*b^3*F^a*ExpIntegralEi[b*(c+d*x)^n*Log[F]]*Log[F]^3/(d*n)");
  }

  // 2.3 Exponential functions.input:434
  public void test0030() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(e+f*x)^2, x]", //
        "f*(d*e-c*f)*F^(a+b*(c+d*x)^2)/(b*d^3*Log[F])+1/2*f^2*F^(a+b*(c+d*x)^2)*(c+d*x)/(b*d^3*Log[F])-1/4*f^2*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(3/2)*d^3*Log[F]^(3/2))+1/2*(d*e-c*f)^2*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(d^3*Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:457
  public void test0031() {
    check( //
        "Integrate[E^(e/(c+d*x))/(a+b*x), x]", //
        "-ExpIntegralEi[e/(c+d*x)]/b+E^(b*e/(b*c-a*d))*ExpIntegralEi[-d*e*(a+b*x)/((b*c-a*d)*(c+d*x))]/b");
  }

  // 2.3 Exponential functions.input:468
  public void test0032() {
    check( //
        "Integrate[E^(e/(c+d*x)^3)*(a+b*x)^2, x]", //
        "1/3*E^(e/(c+d*x)^3)*b^2*(c+d*x)^3/d^3-1/3*b^2*e*ExpIntegralEi[e/(c+d*x)^3]/d^3-2/3*b*(b*c-a*d)*(-e/(c+d*x)^3)^(2/3)*(c+d*x)^2*Gamma[-2/3,-e/(c+d*x)^3]/d^3+1/3*(b*c-a*d)^2*(-e/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,-e/(c+d*x)^3]/d^3");
  }

  // 2.3 Exponential functions.input:482
  public void test0033() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*x^2, x]", //
        "-1/4*b*f^(a+b*x+c*x^2)/(c^2*Log[f])+1/2*f^(a+b*x+c*x^2)*x/(c*Log[f])-1/4*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(3/2)*Log[f]^(3/2))+1/8*b^2*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(5/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:517
  public void test0034() {
    check( //
        "Integrate[f^(b*x+c*x^2)/(b+2*c*x)^2, x]", //
        "-1/2*f^(b*x+c*x^2)/(c*(b+2*c*x))+1/4*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[Log[f]]/(c^(3/2)*f^(1/4*b^2/c))");
  }

  // 2.3 Exponential functions.input:537
  public void test0035() {
    check( //
        "Integrate[2^(2*x)/(a-2^x*b), x]", //
        "-2^x/(b*Log[2])-a*Log[a-2^x*b]/(b^2*Log[2])");
  }

  // 2.3 Exponential functions.input:575
  public void test0036() {
    check( //
        "Integrate[1/(3+3*E^x+E^(2*x)), x]", //
        "1/3*x-1/6*Log[3+3*E^x+E^(2*x)]-ArcTan[(3+2*E^x)/Sqrt[3]]/Sqrt[3]");
  }

  // 2.3 Exponential functions.input:583
  public void test0037() {
    check( //
        "Integrate[x^2/(2+3*E^x+E^(2*x)), x]", //
        "1/6*x^3+1/2*x^2*Log[1+1/2*E^x]-x^2*Log[1+E^x]-2*x*PolyLog[2,-E^x]+x*PolyLog[2,-1/2*E^x]+2*PolyLog[3,-E^x]-PolyLog[3,-1/2*E^x]");
  }

  // 2.3 Exponential functions.input:599
  public void test0038() {
    check( //
        "Integrate[1/(2+f^(-c-d*x)+f^(c+d*x)), x]", //
        "(-1)/(d*(1+f^(c+d*x))*Log[f])");
  }

  // 2.3 Exponential functions.input:607
  public void test0039() {
    check( //
        "Integrate[1/(a+b*f^(-c-d*x)+c*f^(c+d*x)), x]", //
        "-2*ArcTanh[(a+2*c*f^(c+d*x))/Sqrt[a^2-4*b*c]]/(d*Log[f]*Sqrt[a^2-4*b*c])");
  }

  // 2.3 Exponential functions.input:621
  public void test0040() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]))^2/(d^2-e^2*x^2), x]", //
        "2*a*b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+b^2*ExpIntegralEi[2*c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+a^2*Log[Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)");
  }

  // 2.3 Exponential functions.input:631
  public void test0041() {
    check( //
        "Integrate[1/(F^(2*Sqrt[1-a*x]/Sqrt[1+a*x])*(1-a^2*x^2)), x]", //
        "-ExpIntegralEi[-2*Log[F]*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 2.3 Exponential functions.input:746
  public void test0042() {
    check( //
        "Integrate[E^x*(-2+E^x)/(1+E^x), x]", //
        "E^x-3*Log[1+E^x]");
  }

  // 2.3 Exponential functions.input:758
  public void test0043() {
    check( //
        "Integrate[E^x*Sqrt[1+E^(2*x)], x]", //
        "1/2*ArcSinh[E^x]+1/2*E^x*Sqrt[1+E^(2*x)]");
  }

  // 2.3 Exponential functions.input:792
  public void test0044() {
    check( //
        "Integrate[(1/E^x+E^x)*x, x]", //
        "(-1)/E^x-E^x-x/E^x+E^x*x");
  }

  // 2.3 Exponential functions.input:806
  public void test0045() {
    check( //
        "Integrate[(E^(5*x)+E^(7*x))/(1/E^x+E^x), x]", //
        "1/6*E^(6*x)");
  }

  // 2.3 Exponential functions.input:826
  public void test0046() {
    check( //
        "Integrate[E^x*Sqrt[9-E^(2*x)], x]", //
        "9/2*ArcSin[1/3*E^x]+1/2*E^x*Sqrt[9-E^(2*x)]");
  }

  // 2.3 Exponential functions.input:834
  public void test0047() {
    check( //
        "Integrate[(1/E^x+E^x)^2, x]", //
        "(-1/2)/E^(2*x)+1/2*E^(2*x)+2*x");
  }

  // 2.3 Exponential functions.input:842
  public void test0048() {
    check( //
        "Integrate[(1+4^x)/(1+1/2^x), x]", //
        "-2^x/Log[2]+2^(-1+2*x)/Log[2]+2*Log[1+2^x]/Log[2]");
  }

  // 2.3 Exponential functions.input:860
  public void test0049() {
    check( //
        "Integrate[(2-3*x+x^2)/E^(4*x), x]", //
        "(-11/32)/E^(4*x)+5/8*x/E^(4*x)-1/4*x^2/E^(4*x)");
  }

  // 2.3 Exponential functions.input:874
  public void test0050() {
    check( //
        "Integrate[(-1)/(E^x+x)^(1/3)+x/(E^x+x)^(1/3)-(E^x+x)^(2/3), x]", //
        "-3/2*(E^x+x)^(2/3)");
  }

  // 2.3 Exponential functions.input:893
  public void test0051() {
    check( //
        "Integrate[f^(x^n)*x^m, x]", //
        "-x^(1+m)*Gamma[(1+m)/n,-x^n*Log[f]]/(n*(-x^n*Log[f])^((1+m)/n))");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:26
  public void test0052() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d^2+2*d*e*x+e^2*x^2), x]", //
        "-F^(c*(a+b*x))/(e*(d+e*x))+b*c*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]/e^2");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:44
  public void test0053() {
    check( //
        "Integrate[F^(a+b*x)*x^(5/2), x]", //
        "-5/2*F^(a+b*x)*x^(3/2)/(b^2*Log[F]^2)+F^(a+b*x)*x^(5/2)/(b*Log[F])-15/8*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(7/2)*Log[F]^(7/2))+15/4*F^(a+b*x)*Sqrt[x]/(b^3*Log[F]^3)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:52
  public void test0054() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^(7/2), x]", //
        "35/4*e^2*F^(c*(a+b*x))*(d+e*x)^(3/2)/(b^3*c^3*Log[F]^3)-7/2*e*F^(c*(a+b*x))*(d+e*x)^(5/2)/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^(7/2)/(b*c*Log[F])+105/16*e^(7/2)*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Sqrt[Pi]/(b^(9/2)*c^(9/2)*Log[F]^(9/2))-105/8*e^3*F^(c*(a+b*x))*Sqrt[d+e*x]/(b^4*c^4*Log[F]^4)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:60
  public void test0055() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^(9/2), x]", //
        "-2/7*F^(c*(a+b*x))/(e*(d+e*x)^(7/2))-4/35*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^(5/2))-8/105*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*(d+e*x)^(3/2))+16/105*b^(7/2)*c^(7/2)*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Log[F]^(7/2)*Sqrt[Pi]/e^(9/2)-16/105*b^3*c^3*F^(c*(a+b*x))*Log[F]^3/(e^4*Sqrt[d+e*x])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:74
  public void test0056() {
    check( //
        "Integrate[E^(-a-b*x)*x^m*(a+b*x)^3, x]", //
        "-a^3*x^m*Gamma[1+m,b*x]/(E^a*b*(b*x)^m)-3*a^2*x^m*Gamma[2+m,b*x]/(E^a*b*(b*x)^m)-3*a*x^m*Gamma[3+m,b*x]/(E^a*b*(b*x)^m)-x^m*Gamma[4+m,b*x]/(E^a*b*(b*x)^m)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:82
  public void test0057() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^3/x^4, x]", //
        "-1/3*E^(-a-b*x)*a^3/x^3-3/2*E^(-a-b*x)*a^2*b/x^2+1/6*E^(-a-b*x)*a^3*b/x^2-3*E^(-a-b*x)*a*b^2/x+3/2*E^(-a-b*x)*a^2*b^2/x-1/6*E^(-a-b*x)*a^3*b^2/x+b^3*ExpIntegralEi[-b*x]/E^a-3*a*b^3*ExpIntegralEi[-b*x]/E^a+3/2*a^2*b^3*ExpIntegralEi[-b*x]/E^a-1/6*a^3*b^3*ExpIntegralEi[-b*x]/E^a");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:90
  public void test0058() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*(e+f*x)^2/x^3, x]", //
        "-1/2*e^2*F^(a+b*c+b*d*x)/x^2-2*e*f*F^(a+b*c+b*d*x)/x+f^2*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]-1/2*b*d*e^2*F^(a+b*c+b*d*x)*Log[F]/x+2*b*d*e*f*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]*Log[F]+1/2*b^2*d^2*e^2*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]*Log[F]^2");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:100
  public void test0059() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4/(c+d*x)^2, x]", //
        "-2*E^(-a-b*x)*b/d^2+4*E^(-a-b*x)*b*(b*c-a*d)/d^3-6*E^(-a-b*x)*b*(b*c-a*d)^2/d^4-E^(-a-b*x)*(b*c-a*d)^4/(d^5*(c+d*x))-2*E^(-a-b*x)*b^2*(c+d*x)/d^3+4*E^(-a-b*x)*b^2*(b*c-a*d)*(c+d*x)/d^4-E^(-a-b*x)*b^3*(c+d*x)^2/d^4-4*E^(-a+b*c/d)*b*(b*c-a*d)^3*ExpIntegralEi[-b*(c+d*x)/d]/d^5-E^(-a+b*c/d)*b*(b*c-a*d)^4*ExpIntegralEi[-b*(c+d*x)/d]/d^6");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:110
  public void test0060() {
    check( //
        "Integrate[F^(c*(a+b*x))*Log[d*x]^n*(e+e*n+b*c*e*x*Log[F]*Log[d*x])/x, x]", //
        "e*F^(c*(a+b*x))*Log[d*x]^(1+n)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:18
  public void test0061() {
    check( //
        "Integrate[1/(a+E^(c-d*x)*b), x]", //
        "x/a+Log[a+E^(c-d*x)*b]/(a*d)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:28
  public void test0062() {
    check( //
        "Integrate[x^3/(a+E^(c+d*x)*b)^3, x]", //
        "3/2*x^2/(a^3*d^2)-3/2*x^2/(a^2*(a+E^(c+d*x)*b)*d^2)-3/2*x^3/(a^3*d)+1/2*x^3/(a*(a+E^(c+d*x)*b)^2*d)+x^3/(a^2*(a+E^(c+d*x)*b)*d)+1/4*x^4/a^3-3*x*Log[1+E^(c+d*x)*b/a]/(a^3*d^3)+9/2*x^2*Log[1+E^(c+d*x)*b/a]/(a^3*d^2)-x^3*Log[1+E^(c+d*x)*b/a]/(a^3*d)-3*PolyLog[2,-E^(c+d*x)*b/a]/(a^3*d^4)+9*x*PolyLog[2,-E^(c+d*x)*b/a]/(a^3*d^3)-3*x^2*PolyLog[2,-E^(c+d*x)*b/a]/(a^3*d^2)-9*PolyLog[3,-E^(c+d*x)*b/a]/(a^3*d^4)+6*x*PolyLog[3,-E^(c+d*x)*b/a]/(a^3*d^3)-6*PolyLog[4,-E^(c+d*x)*b/a]/(a^3*d^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:42
  public void test0063() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)*(c+d*x), x]", //
        "1/2*a*(c+d*x)^2/d-b*d*(F^(e*g+f*g*x))^n/(f^2*g^2*n^2*Log[F]^2)+b*(F^(e*g+f*g*x))^n*(c+d*x)/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:50
  public void test0064() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2, x]", //
        "a^2*x+2*a*b*(F^(g*(e+f*x)))^n/(f*g*n*Log[F])+1/2*b^2*(F^(g*(e+f*x)))^(2*n)/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:58
  public void test0065() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3/(c+d*x), x]", //
        "3*a^2*b*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]/d+3*a*b^2*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*ExpIntegralEi[2*f*g*n*(c+d*x)*Log[F]/d]/d+b^3*F^(3*(e-c*f/d)*g*n-3*g*n*(e+f*x))*(F^(e*g+f*g*x))^(3*n)*ExpIntegralEi[3*f*g*n*(c+d*x)*Log[F]/d]/d+a^3*Log[c+d*x]/d");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:70
  public void test0066() {
    check( //
        "Integrate[(c+d*x)^2/(a+b*(F^(g*(e+f*x)))^n)^2, x]", //
        "1/3*(c+d*x)^3/(a^2*d)-(c+d*x)^2/(a^2*f*g*n*Log[F])+(c+d*x)^2/(a*f*(a+b*(F^(g*(e+f*x)))^n)*g*n*Log[F])+2*d*(c+d*x)*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a^2*f^2*g^2*n^2*Log[F]^2)-(c+d*x)^2*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a^2*f*g*n*Log[F])+2*d^2*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^3*g^3*n^3*Log[F]^3)-2*d*(c+d*x)*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^2*g^2*n^2*Log[F]^2)+2*d^2*PolyLog[3,-b*(F^(g*(e+f*x)))^n/a]/(a^2*f^3*g^3*n^3*Log[F]^3)");
  }

  // 2.3 Exponential functions.input:37
  public void test0067() {
    check( //
        "Integrate[E^(4*x)/(a+E^(2*x)*b)^3, x]", //
        "1/4*E^(4*x)/(a*(a+E^(2*x)*b)^2)");
  }

  // 2.3 Exponential functions.input:45
  public void test0068() {
    check( //
        "Integrate[1/(E^(n*x)*(a+E^(n*x)*b)^3), x]", //
        "(-1)/(E^(n*x)*a^3*n)-1/2*b/(a^2*(a+E^(n*x)*b)^2*n)-2*b/(a^3*(a+E^(n*x)*b)*n)-3*b*x/a^4+3*b*Log[a+E^(n*x)*b]/(a^4*n)");
  }

  // 2.3 Exponential functions.input:61
  public void test0069() {
    check( //
        "Integrate[f^x*x/(a+b*f^(2*x))^2, x]", //
        "1/2*f^x*x/(a*(a+b*f^(2*x))*Log[f])-1/2*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Log[f]^2*Sqrt[b])+1/2*x*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Log[f]*Sqrt[b])-1/4*I*PolyLog[2,-I*f^x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Log[f]^2*Sqrt[b])+1/4*I*PolyLog[2,I*f^x*Sqrt[b]/Sqrt[a]]/(a^(3/2)*Log[f]^2*Sqrt[b])");
  }

  // 2.3 Exponential functions.input:112
  public void test0070() {
    check( //
        "Integrate[f^(a+b*x^2)/x^6, x]", //
        "-1/5*f^(a+b*x^2)/x^5-2/15*b*f^(a+b*x^2)*Log[f]/x^3-4/15*b^2*f^(a+b*x^2)*Log[f]^2/x+4/15*b^(5/2)*f^a*Erfi[x*Sqrt[b]*Sqrt[Log[f]]]*Log[f]^(5/2)*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:120
  public void test0071() {
    check( //
        "Integrate[f^(a+b*x^3)*x^8, x]", //
        "2/3*f^(a+b*x^3)/(b^3*Log[f]^3)-2/3*f^(a+b*x^3)*x^3/(b^2*Log[f]^2)+1/3*f^(a+b*x^3)*x^6/(b*Log[f])");
  }

  // 2.3 Exponential functions.input:154
  public void test0072() {
    check( //
        "Integrate[f^(a+b/x^2)*x^5, x]", //
        "1/6*f^(a+b/x^2)*x^6+1/12*b*f^(a+b/x^2)*x^4*Log[f]+1/12*b^2*f^(a+b/x^2)*x^2*Log[f]^2-1/12*b^3*f^a*ExpIntegralEi[b*Log[f]/x^2]*Log[f]^3");
  }

  // 2.3 Exponential functions.input:186
  public void test0073() {
    check( //
        "Integrate[f^(a+b/x^3)/x^10, x]", //
        "-2/3*f^(a+b/x^3)/(b^3*Log[f]^3)+2/3*f^(a+b/x^3)/(b^2*x^3*Log[f]^2)-1/3*f^(a+b/x^3)/(b*x^6*Log[f])");
  }

  // 2.3 Exponential functions.input:226
  public void test0074() {
    check( //
        "Integrate[f^(c*(a+b*x)^2)*x^3, x]", //
        "-1/2*f^(c*(a+b*x)^2)/(b^4*c^2*Log[f]^2)+3/2*a^2*f^(c*(a+b*x)^2)/(b^4*c*Log[f])-3/2*a*f^(c*(a+b*x)^2)*(a+b*x)/(b^4*c*Log[f])+1/2*f^(c*(a+b*x)^2)*(a+b*x)^2/(b^4*c*Log[f])+3/4*a*Erfi[(a+b*x)*Sqrt[c]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^4*c^(3/2)*Log[f]^(3/2))-1/2*a^3*Erfi[(a+b*x)*Sqrt[c]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^4*Sqrt[c]*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:252
  public void test0075() {
    check( //
        "Integrate[f^(c/(a+b*x))*x, x]", //
        "-a*f^(c/(a+b*x))*(a+b*x)/b^2+1/2*f^(c/(a+b*x))*(a+b*x)^2/b^2+1/2*c*f^(c/(a+b*x))*(a+b*x)*Log[f]/b^2+a*c*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]/b^2-1/2*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^2/b^2");
  }

  // 2.3 Exponential functions.input:260
  public void test0076() {
    check( //
        "Integrate[f^(c/(a+b*x)^2)*x, x]", //
        "-a*f^(c/(a+b*x)^2)*(a+b*x)/b^2+1/2*f^(c/(a+b*x)^2)*(a+b*x)^2/b^2-1/2*c*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]/b^2+a*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Sqrt[Pi]*Sqrt[c]*Sqrt[Log[f]]/b^2");
  }

  // 2.3 Exponential functions.input:284
  public void test0077() {
    check( //
        "Integrate[f^(c*(a+b*x)^n)*x^3, x]", //
        "-(a+b*x)^4*Gamma[4/n,-c*(a+b*x)^n*Log[f]]/(b^4*n*(-c*(a+b*x)^n*Log[f])^(4/n))+3*a*(a+b*x)^3*Gamma[3/n,-c*(a+b*x)^n*Log[f]]/(b^4*n*(-c*(a+b*x)^n*Log[f])^(3/n))-3*a^2*(a+b*x)^2*Gamma[2/n,-c*(a+b*x)^n*Log[f]]/(b^4*n*(-c*(a+b*x)^n*Log[f])^(2/n))+a^3*(a+b*x)*Gamma[1/n,-c*(a+b*x)^n*Log[f]]/(b^4*n*(-c*(a+b*x)^n*Log[f])^(1/n))");
  }

  // 2.3 Exponential functions.input:299
  public void test0078() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(c+d*x)^5, x]", //
        "F^(a+b*(c+d*x)^2)/(b^3*d*Log[F]^3)-F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b^2*d*Log[F]^2)+1/2*F^(a+b*(c+d*x)^2)*(c+d*x)^4/(b*d*Log[F])");
  }

  // 2.3 Exponential functions.input:331
  public void test0079() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^3)/(c+d*x)^10, x]", //
        "-1/9*F^(a+b*(c+d*x)^3)/(d*(c+d*x)^9)-1/18*b*F^(a+b*(c+d*x)^3)*Log[F]/(d*(c+d*x)^6)-1/18*b^2*F^(a+b*(c+d*x)^3)*Log[F]^2/(d*(c+d*x)^3)+1/18*b^3*F^a*ExpIntegralEi[b*(c+d*x)^3*Log[F]]*Log[F]^3/d");
  }

  // 2.3 Exponential functions.input:373
  public void test0080() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)*(c+d*x)^4, x]", //
        "1/5*F^(a+b/(c+d*x)^2)*(c+d*x)^5/d+2/15*b*F^(a+b/(c+d*x)^2)*(c+d*x)^3*Log[F]/d+4/15*b^2*F^(a+b/(c+d*x)^2)*(c+d*x)*Log[F]^2/d-4/15*b^(5/2)*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Log[F]^(5/2)*Sqrt[Pi]/d");
  }

  // 2.3 Exponential functions.input:415
  public void test0081() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+4*n), x]", //
        "-6*F^(a+b*(c+d*x)^n)/(b^4*d*n*Log[F]^4)+6*F^(a+b*(c+d*x)^n)*(c+d*x)^n/(b^3*d*n*Log[F]^3)-3*F^(a+b*(c+d*x)^n)*(c+d*x)^(2*n)/(b^2*d*n*Log[F]^2)+F^(a+b*(c+d*x)^n)*(c+d*x)^(3*n)/(b*d*n*Log[F])");
  }

  // 2.3 Exponential functions.input:435
  public void test0082() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(e+f*x), x]", //
        "1/2*f*F^(a+b*(c+d*x)^2)/(b*d^2*Log[F])+1/2*(d*e-c*f)*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(d^2*Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:469
  public void test0083() {
    check( //
        "Integrate[E^(e/(c+d*x)^3)*(a+b*x), x]", //
        "1/3*b*(-e/(c+d*x)^3)^(2/3)*(c+d*x)^2*Gamma[-2/3,-e/(c+d*x)^3]/d^2-1/3*(b*c-a*d)*(-e/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,-e/(c+d*x)^3]/d^2");
  }

  // 2.3 Exponential functions.input:483
  public void test0084() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*x, x]", //
        "1/2*f^(a+b*x+c*x^2)/(c*Log[f])-1/4*b*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(3/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:538
  public void test0085() {
    check( //
        "Integrate[4^x/(a+b/2^x), x]", //
        "b^2*x/a^3+2^(-1+2*x)/(a*Log[2])-2^x*b/(a^2*Log[2])+b^2*Log[a+b/2^x]/(a^3*Log[2])");
  }

  // 2.3 Exponential functions.input:546
  public void test0086() {
    check( //
        "Integrate[2^x/(a+b/4^x), x]", //
        "2^x/(a*Log[2])-ArcTan[2^x*Sqrt[a]/Sqrt[b]]*Sqrt[b]/(a^(3/2)*Log[2])");
  }

  // 2.3 Exponential functions.input:608
  public void test0087() {
    check( //
        "Integrate[x/(a+b*f^(-c-d*x)+c*f^(c+d*x)), x]", //
        "x*Log[1+2*c*f^(c+d*x)/(a-Sqrt[a^2-4*b*c])]/(d*Log[f]*Sqrt[a^2-4*b*c])-x*Log[1+2*c*f^(c+d*x)/(a+Sqrt[a^2-4*b*c])]/(d*Log[f]*Sqrt[a^2-4*b*c])+PolyLog[2,-2*c*f^(c+d*x)/(a-Sqrt[a^2-4*b*c])]/(d^2*Log[f]^2*Sqrt[a^2-4*b*c])-PolyLog[2,-2*c*f^(c+d*x)/(a+Sqrt[a^2-4*b*c])]/(d^2*Log[f]^2*Sqrt[a^2-4*b*c])");
  }

  // 2.3 Exponential functions.input:622
  public void test0088() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]))/(d^2-e^2*x^2), x]", //
        "b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+a*Log[Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)");
  }

  // 2.3 Exponential functions.input:634
  public void test0089() {
    check( //
        "Integrate[a^x*b^x*x^2, x]", //
        "2*a^x*b^x/(Log[a]+Log[b])^3-2*a^x*b^x*x/(Log[a]+Log[b])^2+a^x*b^x*x^2/(Log[a]+Log[b])");
  }

  // 2.3 Exponential functions.input:642
  public void test0090() {
    check( //
        "Integrate[a^x*x^2/b^x, x]", //
        "2*a^x/(b^x*(Log[a]-Log[b])^3)-2*a^x*x/(b^x*(Log[a]-Log[b])^2)+a^x*x^2/(b^x*(Log[a]-Log[b]))");
  }

  // 2.3 Exponential functions.input:667
  public void test0091() {
    check( //
        "Integrate[F^(f*(a+b*Log[c*(d+e*x)^n]^2))*(d*g+e*g*x)^2, x]", //
        "1/2*F^(a*f)*g^2*(d+e*x)^3*Erfi[1/2*(3+2*b*f*n*Log[F]*Log[c*(d+e*x)^n])/(n*Sqrt[b]*Sqrt[f]*Sqrt[Log[F]])]*Sqrt[Pi]/(E^(9/4/(b*f*n^2*Log[F]))*e*n*(c*(d+e*x)^n)^(3/n)*Sqrt[b]*Sqrt[f]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:709
  public void test0092() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)*(a+b*x+c*x^2)^3, x]", //
        "-6*E^(a+b*x+c*x^2)+6*E^(a+b*x+c*x^2)*(a+b*x+c*x^2)-3*E^(a+b*x+c*x^2)*(a+b*x+c*x^2)^2+E^(a+b*x+c*x^2)*(a+b*x+c*x^2)^3");
  }

  // 2.3 Exponential functions.input:717
  public void test0093() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)*(a+b*x+c*x^2)^(5/2), x]", //
        "-5/2*E^(a+b*x+c*x^2)*(a+b*x+c*x^2)^(3/2)+E^(a+b*x+c*x^2)*(a+b*x+c*x^2)^(5/2)-15/8*Erfi[Sqrt[a+b*x+c*x^2]]*Sqrt[Pi]+15/4*E^(a+b*x+c*x^2)*Sqrt[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:729
  public void test0094() {
    check( //
        "Integrate[1/(E^x*Sqrt[1+(-1)/E^(2*x)]), x]", //
        "-ArcSin[1/E^x]");
  }

  // 2.3 Exponential functions.input:759
  public void test0095() {
    check( //
        "Integrate[E^(x^2)*x/(1+E^(2*x^2)), x]", //
        "1/2*ArcTan[E^(x^2)]");
  }

  // 2.3 Exponential functions.input:793
  public void test0096() {
    check( //
        "Integrate[E^x/(2+3*E^x+E^(2*x)), x]", //
        "Log[1+E^x]-Log[2+E^x]");
  }

  // 2.3 Exponential functions.input:827
  public void test0097() {
    check( //
        "Integrate[E^(6*x)*Sqrt[9-E^(2*x)], x]", //
        "-27*(9-E^(2*x))^(3/2)+18/5*(9-E^(2*x))^(5/2)-1/7*(9-E^(2*x))^(7/2)");
  }

  // 2.3 Exponential functions.input:835
  public void test0098() {
    check( //
        "Integrate[1/(1/E^x+E^x), x]", //
        "ArcTan[E^x]");
  }

  // 2.3 Exponential functions.input:843
  public void test0099() {
    check( //
        "Integrate[E^((a+x)^2)/x^2-2*E^((a+x)^2)*a/x, x]", //
        "-E^((a+x)^2)/x+Erfi[a+x]*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:853
  public void test0100() {
    check( //
        "Integrate[E^(x^2)/x^2, x]", //
        "-E^(x^2)/x+Erfi[x]*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:861
  public void test0101() {
    check( //
        "Integrate[k^(1/2*x)+x^Sqrt[k], x]", //
        "2*k^(1/2*x)/Log[k]+x^(1+Sqrt[k])/(1+Sqrt[k])");
  }

  // 2.3 Exponential functions.input:876
  public void test0102() {
    check( //
        "Integrate[(5*x+E^x*(3+2*x))/(E^x+x)^(1/3), x]", //
        "3*x*(E^x+x)^(2/3)");
  }

  // 2.3 Exponential functions.input:894
  public void test0103() {
    check( //
        "Integrate[E^((a+b*x)^n)*(a+b*x)^m, x]", //
        "-(a+b*x)^(1+m)*Gamma[(1+m)/n,-(a+b*x)^n]/(b*n*(-(a+b*x)^n)^((1+m)/n))");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:20
  public void test0104() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^3, x]", //
        "-1/2*F^(c*(a+b*x))/(e*(d+e*x)^2)-1/2*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x))+1/2*b^2*c^2*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]^2/e^3");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:28
  public void test0105() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d^4+4*d^3*e*x+6*d^2*e^2*x^2+4*d*e^3*x^3+e^4*x^4), x]", //
        "-1/3*F^(c*(a+b*x))/(e*(d+e*x)^3)-1/6*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^2)-1/6*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*(d+e*x))+1/6*b^3*c^3*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]^3/e^4");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:46
  public void test0106() {
    check( //
        "Integrate[F^(a+b*x)*x^(1/2), x]", //
        "-1/2*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(3/2)*Log[F]^(3/2))+F^(a+b*x)*Sqrt[x]/(b*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:54
  public void test0107() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^(3/2), x]", //
        "F^(c*(a+b*x))*(d+e*x)^(3/2)/(b*c*Log[F])+3/4*e^(3/2)*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Sqrt[Pi]/(b^(5/2)*c^(5/2)*Log[F]^(5/2))-3/2*e*F^(c*(a+b*x))*Sqrt[d+e*x]/(b^2*c^2*Log[F]^2)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:76
  public void test0108() {
    check( //
        "Integrate[E^(-a-b*x)*x^2*(a+b*x)^3, x]", //
        "-120*E^(-a-b*x)/b^3-72*E^(-a-b*x)*a/b^3-18*E^(-a-b*x)*a^2/b^3-2*E^(-a-b*x)*a^3/b^3-120*E^(-a-b*x)*x/b^2-72*E^(-a-b*x)*a*x/b^2-18*E^(-a-b*x)*a^2*x/b^2-2*E^(-a-b*x)*a^3*x/b^2-60*E^(-a-b*x)*x^2/b-36*E^(-a-b*x)*a*x^2/b-9*E^(-a-b*x)*a^2*x^2/b-E^(-a-b*x)*a^3*x^2/b-20*E^(-a-b*x)*x^3-12*E^(-a-b*x)*a*x^3-3*E^(-a-b*x)*a^2*x^3-5*E^(-a-b*x)*b*x^4-3*E^(-a-b*x)*a*b*x^4-E^(-a-b*x)*b^2*x^5");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:84
  public void test0109() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*x^3*(e+f*x)^2, x]", //
        "-120*f^2*F^(a+b*c+b*d*x)/(b^6*d^6*Log[F]^6)+48*e*f*F^(a+b*c+b*d*x)/(b^5*d^5*Log[F]^5)+120*f^2*F^(a+b*c+b*d*x)*x/(b^5*d^5*Log[F]^5)-6*e^2*F^(a+b*c+b*d*x)/(b^4*d^4*Log[F]^4)-48*e*f*F^(a+b*c+b*d*x)*x/(b^4*d^4*Log[F]^4)-60*f^2*F^(a+b*c+b*d*x)*x^2/(b^4*d^4*Log[F]^4)+6*e^2*F^(a+b*c+b*d*x)*x/(b^3*d^3*Log[F]^3)+24*e*f*F^(a+b*c+b*d*x)*x^2/(b^3*d^3*Log[F]^3)+20*f^2*F^(a+b*c+b*d*x)*x^3/(b^3*d^3*Log[F]^3)-3*e^2*F^(a+b*c+b*d*x)*x^2/(b^2*d^2*Log[F]^2)-8*e*f*F^(a+b*c+b*d*x)*x^3/(b^2*d^2*Log[F]^2)-5*f^2*F^(a+b*c+b*d*x)*x^4/(b^2*d^2*Log[F]^2)+e^2*F^(a+b*c+b*d*x)*x^3/(b*d*Log[F])+2*e*f*F^(a+b*c+b*d*x)*x^4/(b*d*Log[F])+f^2*F^(a+b*c+b*d*x)*x^5/(b*d*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:92
  public void test0110() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*(e+f*x)^2/x^5, x]", //
        "-1/4*e^2*F^(a+b*c+b*d*x)/x^4-2/3*e*f*F^(a+b*c+b*d*x)/x^3-1/2*f^2*F^(a+b*c+b*d*x)/x^2-1/12*b*d*e^2*F^(a+b*c+b*d*x)*Log[F]/x^3-1/3*b*d*e*f*F^(a+b*c+b*d*x)*Log[F]/x^2-1/2*b*d*f^2*F^(a+b*c+b*d*x)*Log[F]/x-1/24*b^2*d^2*e^2*F^(a+b*c+b*d*x)*Log[F]^2/x^2-1/3*b^2*d^2*e*f*F^(a+b*c+b*d*x)*Log[F]^2/x+1/2*b^2*d^2*f^2*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]*Log[F]^2-1/24*b^3*d^3*e^2*F^(a+b*c+b*d*x)*Log[F]^3/x+1/3*b^3*d^3*e*f*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]*Log[F]^3+1/24*b^4*d^4*e^2*F^(a+b*c)*ExpIntegralEi[b*d*x*Log[F]]*Log[F]^4");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:102
  public void test0111() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4/(c+d*x)^4, x]", //
        "-E^(-a-b*x)*b^3/d^4-1/3*E^(-a-b*x)*(b*c-a*d)^4/(d^5*(c+d*x)^3)+2*E^(-a-b*x)*b*(b*c-a*d)^3/(d^5*(c+d*x)^2)+1/6*E^(-a-b*x)*b*(b*c-a*d)^4/(d^6*(c+d*x)^2)-6*E^(-a-b*x)*b^2*(b*c-a*d)^2/(d^5*(c+d*x))-2*E^(-a-b*x)*b^2*(b*c-a*d)^3/(d^6*(c+d*x))-1/6*E^(-a-b*x)*b^2*(b*c-a*d)^4/(d^7*(c+d*x))-4*E^(-a+b*c/d)*b^3*(b*c-a*d)*ExpIntegralEi[-b*(c+d*x)/d]/d^5-6*E^(-a+b*c/d)*b^3*(b*c-a*d)^2*ExpIntegralEi[-b*(c+d*x)/d]/d^6-2*E^(-a+b*c/d)*b^3*(b*c-a*d)^3*ExpIntegralEi[-b*(c+d*x)/d]/d^7-1/6*E^(-a+b*c/d)*b^3*(b*c-a*d)^4*ExpIntegralEi[-b*(c+d*x)/d]/d^8");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:112
  public void test0112() {
    check( //
        "Integrate[F^(c*(a+b*x))*Log[d*x]^n*(e+e*n+e*(-2+b*c*x*Log[F])*Log[d*x])/x^3, x]", //
        "e*F^(c*(a+b*x))*Log[d*x]^(1+n)/x^2");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:122
  public void test0113() {
    check( //
        "Integrate[Sqrt[E^(a+b*x)]/x^3, x]", //
        "-1/2*Sqrt[E^(a+b*x)]/x^2-1/4*b*Sqrt[E^(a+b*x)]/x+1/8*b^2*ExpIntegralEi[1/2*b*x]*Sqrt[E^(a+b*x)]/E^(1/2*b*x)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:20
  public void test0114() {
    check( //
        "Integrate[x^3/(a+E^(c+d*x)*b)^2, x]", //
        "-x^3/(a^2*d)+x^3/(a*(a+E^(c+d*x)*b)*d)+1/4*x^4/a^2+3*x^2*Log[1+E^(c+d*x)*b/a]/(a^2*d^2)-x^3*Log[1+E^(c+d*x)*b/a]/(a^2*d)+6*x*PolyLog[2,-E^(c+d*x)*b/a]/(a^2*d^3)-3*x^2*PolyLog[2,-E^(c+d*x)*b/a]/(a^2*d^2)-6*PolyLog[3,-E^(c+d*x)*b/a]/(a^2*d^4)+6*x*PolyLog[3,-E^(c+d*x)*b/a]/(a^2*d^3)-6*PolyLog[4,-E^(c+d*x)*b/a]/(a^2*d^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:44
  public void test0115() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)/(c+d*x), x]", //
        "b*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]/d+a*Log[c+d*x]/d");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:52
  public void test0116() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2/(c+d*x)^2, x]", //
        "-a^2/(d*(c+d*x))-2*a*b*(F^(e*g+f*g*x))^n/(d*(c+d*x))-b^2*(F^(e*g+f*g*x))^(2*n)/(d*(c+d*x))+2*a*b*f*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*g*n*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]*Log[F]/d^2+2*b^2*f*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*g*n*ExpIntegralEi[2*f*g*n*(c+d*x)*Log[F]/d]*Log[F]/d^2");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:60
  public void test0117() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3/(c+d*x)^3, x]", //
        "-1/2*a^3/(d*(c+d*x)^2)-3/2*a^2*b*(F^(e*g+f*g*x))^n/(d*(c+d*x)^2)-3/2*a*b^2*(F^(e*g+f*g*x))^(2*n)/(d*(c+d*x)^2)-1/2*b^3*(F^(e*g+f*g*x))^(3*n)/(d*(c+d*x)^2)-3/2*a^2*b*f*(F^(e*g+f*g*x))^n*g*n*Log[F]/(d^2*(c+d*x))-3*a*b^2*f*(F^(e*g+f*g*x))^(2*n)*g*n*Log[F]/(d^2*(c+d*x))-3/2*b^3*f*(F^(e*g+f*g*x))^(3*n)*g*n*Log[F]/(d^2*(c+d*x))+3/2*a^2*b*f^2*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*g^2*n^2*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3+6*a*b^2*f^2*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*g^2*n^2*ExpIntegralEi[2*f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3+9/2*b^3*f^2*F^(3*(e-c*f/d)*g*n-3*g*n*(e+f*x))*(F^(e*g+f*g*x))^(3*n)*g^2*n^2*ExpIntegralEi[3*f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:95
  public void test0118() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)^m, x]", //
        "a^3*(c+d*x)^(1+m)/(d*(1+m))+3^(-1-m)*b^3*F^(3*(e-c*f/d)*g*n-3*g*n*(e+f*x))*(F^(e*g+f*g*x))^(3*n)*(c+d*x)^m*Gamma[1+m,-3*f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)+3*2^(-1-m)*a*b^2*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^m*Gamma[1+m,-2*f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)+3*a^2*b*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*(c+d*x)^m*Gamma[1+m,-f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:114
  public void test0119() {
    check( //
        "Integrate[F^(c+d*x)*x^2/(a+b*F^(c+d*x))^2, x]", //
        "x^2/(a*b*d*Log[F])-x^2/(b*d*(a+b*F^(c+d*x))*Log[F])-2*x*Log[1+b*F^(c+d*x)/a]/(a*b*d^2*Log[F]^2)-2*PolyLog[2,-b*F^(c+d*x)/a]/(a*b*d^3*Log[F]^3)");
  }

  // 2.3 Exponential functions.input:11
  public void test0120() {
    check( //
        "Integrate[E^x/(a+E^x*b), x]", //
        "Log[a+E^x*b]/b");
  }

  // 2.3 Exponential functions.input:31
  public void test0121() {
    check( //
        "Integrate[E^(2*x)/(a+E^x*b), x]", //
        "E^x/b-a*Log[a+E^x*b]/b^2");
  }

  // 2.3 Exponential functions.input:47
  public void test0122() {
    check( //
        "Integrate[f^(a+2*b*x)/(c+d*f^(e+2*b*x)), x]", //
        "1/2*f^(a-e)*Log[c+d*f^(e+2*b*x)]/(b*d*Log[f])");
  }

  // 2.3 Exponential functions.input:73
  public void test0123() {
    check( //
        "Integrate[1/(b/f^x+a*f^x)^2, x]", //
        "(-1/2)/(a*(b+a*f^(2*x))*Log[f])");
  }

  // 2.3 Exponential functions.input:106
  public void test0124() {
    check( //
        "Integrate[f^(a+b*x^2)*x^6, x]", //
        "15/8*f^(a+b*x^2)*x/(b^3*Log[f]^3)-5/4*f^(a+b*x^2)*x^3/(b^2*Log[f]^2)+1/2*f^(a+b*x^2)*x^5/(b*Log[f])-15/16*f^a*Erfi[x*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^(7/2)*Log[f]^(7/2))");
  }

  // 2.3 Exponential functions.input:148
  public void test0125() {
    check( //
        "Integrate[f^(a+b/x)/x^5, x]", //
        "6*f^(a+b/x)/(b^4*Log[f]^4)-6*f^(a+b/x)/(b^3*x*Log[f]^3)+3*f^(a+b/x)/(b^2*x^2*Log[f]^2)-f^(a+b/x)/(b*x^3*Log[f])");
  }

  // 2.3 Exponential functions.input:172
  public void test0126() {
    check( //
        "Integrate[f^(a+b/x^2)/x^6, x]", //
        "3/4*f^(a+b/x^2)/(b^2*x*Log[f]^2)-1/2*f^(a+b/x^2)/(b*x^3*Log[f])-3/8*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Sqrt[Pi]/(b^(5/2)*Log[f]^(5/2))");
  }

  // 2.3 Exponential functions.input:180
  public void test0127() {
    check( //
        "Integrate[f^(a+b/x^3)*x^8, x]", //
        "1/9*f^(a+b/x^3)*x^9+1/18*b*f^(a+b/x^3)*x^6*Log[f]+1/18*b^2*f^(a+b/x^3)*x^3*Log[f]^2-1/18*b^3*f^a*ExpIntegralEi[b*Log[f]/x^3]*Log[f]^3");
  }

  // 2.3 Exponential functions.input:214
  public void test0128() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1+5/2*n), x]", //
        "-3/2*f^(a+b*x^n)*x^(1/2*n)/(b^2*n*Log[f]^2)+f^(a+b*x^n)*x^(3/2*n)/(b*n*Log[f])+3/4*f^a*Erfi[x^(1/2*n)*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^(5/2)*n*Log[f]^(5/2))");
  }

  // 2.3 Exponential functions.input:228
  public void test0129() {
    check( //
        "Integrate[f^(c*(a+b*x)^2)*x, x]", //
        "1/2*f^(c*(a+b*x)^2)/(b^2*c*Log[f])-1/2*a*Erfi[(a+b*x)*Sqrt[c]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^2*Sqrt[c]*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:254
  public void test0130() {
    check( //
        "Integrate[f^(c/(a+b*x))/x, x]", //
        "-ExpIntegralEi[c*Log[f]/(a+b*x)]+f^(c/a)*ExpIntegralEi[-b*c*x*Log[f]/(a*(a+b*x))]");
  }

  // 2.3 Exponential functions.input:265
  public void test0131() {
    check( //
        "Integrate[f^(c/(a+b*x)^3)*x^4, x]", //
        "2*a^2*f^(c/(a+b*x)^3)*(a+b*x)^3/b^5-2*a^2*c*ExpIntegralEi[c*Log[f]/(a+b*x)^3]*Log[f]/b^5+1/3*a^4*(a+b*x)*Gamma[-1/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(1/3)/b^5-4/3*a^3*(a+b*x)^2*Gamma[-2/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(2/3)/b^5-4/3*a*(a+b*x)^4*Gamma[-4/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(4/3)/b^5+1/3*(a+b*x)^5*Gamma[-5/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(5/3)/b^5");
  }

  // 2.3 Exponential functions.input:286
  public void test0132() {
    check( //
        "Integrate[f^(c*(a+b*x)^n)*x, x]", //
        "-(a+b*x)^2*Gamma[2/n,-c*(a+b*x)^n*Log[f]]/(b^2*n*(-c*(a+b*x)^n*Log[f])^(2/n))+a*(a+b*x)*Gamma[1/n,-c*(a+b*x)^n*Log[f]]/(b^2*n*(-c*(a+b*x)^n*Log[f])^(1/n))");
  }

  // 2.3 Exponential functions.input:317
  public void test0133() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)/(c+d*x)^6, x]", //
        "-1/5*F^(a+b*(c+d*x)^2)/(d*(c+d*x)^5)-2/15*b*F^(a+b*(c+d*x)^2)*Log[F]/(d*(c+d*x)^3)-4/15*b^2*F^(a+b*(c+d*x)^2)*Log[F]^2/(d*(c+d*x))+4/15*b^(5/2)*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Log[F]^(5/2)*Sqrt[Pi]/d");
  }

  // 2.3 Exponential functions.input:325
  public void test0134() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^3)*(c+d*x)^8, x]", //
        "2/3*F^(a+b*(c+d*x)^3)/(b^3*d*Log[F]^3)-2/3*F^(a+b*(c+d*x)^3)*(c+d*x)^3/(b^2*d*Log[F]^2)+1/3*F^(a+b*(c+d*x)^3)*(c+d*x)^6/(b*d*Log[F])");
  }

  // 2.3 Exponential functions.input:341
  public void test0135() {
    check( //
        "Integrate[f^(a+b*(c+d*x)^(1/3)), x]", //
        "6*f^(a+b*(c+d*x)^(1/3))/(b^3*d*Log[f]^3)-6*f^(a+b*(c+d*x)^(1/3))*(c+d*x)^(1/3)/(b^2*d*Log[f]^2)+3*f^(a+b*(c+d*x)^(1/3))*(c+d*x)^(2/3)/(b*d*Log[f])");
  }

  // 2.3 Exponential functions.input:367
  public void test0136() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)/(c+d*x)^9, x]", //
        "3*F^(a+b/(c+d*x)^2)/(b^4*d*Log[F]^4)-3*F^(a+b/(c+d*x)^2)/(b^3*d*(c+d*x)^2*Log[F]^3)+3/2*F^(a+b/(c+d*x)^2)/(b^2*d*(c+d*x)^4*Log[F]^2)-1/2*F^(a+b/(c+d*x)^2)/(b*d*(c+d*x)^6*Log[F])");
  }

  // 2.3 Exponential functions.input:375
  public void test0137() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2), x]", //
        "F^(a+b/(c+d*x)^2)*(c+d*x)/d-F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Sqrt[Pi]*Sqrt[b]*Sqrt[Log[F]]/d");
  }

  // 2.3 Exponential functions.input:425
  public void test0138() {
    check( //
        "Integrate[F^(c*(a+b*x)^n)*(a+b*x)^(-1+1/2*n), x]", //
        "Erfi[(a+b*x)^(1/2*n)*Sqrt[c]*Sqrt[Log[F]]]*Sqrt[Pi]/(b*n*Sqrt[c]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:440
  public void test0139() {
    check( //
        "Integrate[E^(e*(c+d*x)^3)*(a+b*x)^3, x]", //
        "-E^(e*(c+d*x)^3)*b^2*(b*c-a*d)/(d^4*e)+1/3*(b*c-a*d)^3*(c+d*x)*Gamma[1/3,-e*(c+d*x)^3]/(d^4*(-e*(c+d*x)^3)^(1/3))-b*(b*c-a*d)^2*(c+d*x)^2*Gamma[2/3,-e*(c+d*x)^3]/(d^4*(-e*(c+d*x)^3)^(2/3))-1/3*b^3*(c+d*x)^4*Gamma[4/3,-e*(c+d*x)^3]/(d^4*(-e*(c+d*x)^3)^(4/3))");
  }

  // 2.3 Exponential functions.input:452
  public void test0140() {
    check( //
        "Integrate[E^(e/(c+d*x))*(a+b*x)^4, x]", //
        "E^(e/(c+d*x))*(b*c-a*d)^4*(c+d*x)/d^5-2*E^(e/(c+d*x))*b*(b*c-a*d)^3*e*(c+d*x)/d^5+E^(e/(c+d*x))*b^2*(b*c-a*d)^2*e^2*(c+d*x)/d^5-2*E^(e/(c+d*x))*b*(b*c-a*d)^3*(c+d*x)^2/d^5+E^(e/(c+d*x))*b^2*(b*c-a*d)^2*e*(c+d*x)^2/d^5+2*E^(e/(c+d*x))*b^2*(b*c-a*d)^2*(c+d*x)^3/d^5-(b*c-a*d)^4*e*ExpIntegralEi[e/(c+d*x)]/d^5+2*b*(b*c-a*d)^3*e^2*ExpIntegralEi[e/(c+d*x)]/d^5-b^2*(b*c-a*d)^2*e^3*ExpIntegralEi[e/(c+d*x)]/d^5-b^4*e^5*Gamma[-5,-e/(c+d*x)]/d^5-4*b^3*(b*c-a*d)*e^4*Gamma[-4,-e/(c+d*x)]/d^5");
  }

  // 2.3 Exponential functions.input:460
  public void test0141() {
    check( //
        "Integrate[E^(e/(c+d*x)^2)*(a+b*x)^3, x]", //
        "-E^(e/(c+d*x)^2)*(b*c-a*d)^3*(c+d*x)/d^4-2*E^(e/(c+d*x)^2)*b^2*(b*c-a*d)*e*(c+d*x)/d^4+3/2*E^(e/(c+d*x)^2)*b*(b*c-a*d)^2*(c+d*x)^2/d^4+1/4*E^(e/(c+d*x)^2)*b^3*e*(c+d*x)^2/d^4-E^(e/(c+d*x)^2)*b^2*(b*c-a*d)*(c+d*x)^3/d^4+1/4*E^(e/(c+d*x)^2)*b^3*(c+d*x)^4/d^4-3/2*b*(b*c-a*d)^2*e*ExpIntegralEi[e/(c+d*x)^2]/d^4-1/4*b^3*e^2*ExpIntegralEi[e/(c+d*x)^2]/d^4+2*b^2*(b*c-a*d)*e^(3/2)*Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]/d^4+(b*c-a*d)^3*Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]*Sqrt[e]/d^4");
  }

  // 2.3 Exponential functions.input:475
  public void test0142() {
    check( //
        "Integrate[F^(e+f*(a+b*x)/(c+d*x))/(g+h*x), x]", //
        "-F^(e+b*f/d)*ExpIntegralEi[-(b*c-a*d)*f*Log[F]/(d*(c+d*x))]/h+F^(e+f*(b*g-a*h)/(d*g-c*h))*ExpIntegralEi[-(b*c-a*d)*f*(g+h*x)*Log[F]/((d*g-c*h)*(c+d*x))]/h");
  }

  // 2.3 Exponential functions.input:487
  public void test0143() {
    check( //
        "Integrate[E^(a+b*x-c*x^2)*x^3, x]", //
        "-1/8*E^(a+b*x-c*x^2)*b^2/c^3-1/2*E^(a+b*x-c*x^2)/c^2-1/4*E^(a+b*x-c*x^2)*b*x/c^2-1/2*E^(a+b*x-c*x^2)*x^2/c-1/16*E^(a+1/4*b^2/c)*b^3*Erf[1/2*(b-2*c*x)/Sqrt[c]]*Sqrt[Pi]/c^(7/2)-3/8*E^(a+1/4*b^2/c)*b*Erf[1/2*(b-2*c*x)/Sqrt[c]]*Sqrt[Pi]/c^(5/2)");
  }

  // 2.3 Exponential functions.input:501
  public void test0144() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*(d+e*x)^3, x]", //
        "-1/2*e^3*f^(a+b*x+c*x^2)/(c^2*Log[f]^2)+1/8*e*(2*c*d-b*e)^2*f^(a+b*x+c*x^2)/(c^3*Log[f])+1/4*e*(2*c*d-b*e)*f^(a+b*x+c*x^2)*(d+e*x)/(c^2*Log[f])+1/2*e*f^(a+b*x+c*x^2)*(d+e*x)^2/(c*Log[f])-3/8*e^2*(2*c*d-b*e)*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(5/2)*Log[f]^(3/2))+1/16*(2*c*d-b*e)^3*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(7/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:530
  public void test0145() {
    check( //
        "Integrate[E^(d+e*x)*x^2/(a+b*x+c*x^2), x]", //
        "E^(d+e*x)/(c*e)-1/2*E^(d-1/2*e*(b-Sqrt[b^2-4*a*c])/c)*ExpIntegralEi[1/2*e*(b+2*c*x-Sqrt[b^2-4*a*c])/c]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/c^2-1/2*E^(d-1/2*e*(b+Sqrt[b^2-4*a*c])/c)*ExpIntegralEi[1/2*e*(b+2*c*x+Sqrt[b^2-4*a*c])/c]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/c^2");
  }

  // 2.3 Exponential functions.input:540
  public void test0146() {
    check( //
        "Integrate[4^x/(a-b/2^x), x]", //
        "b^2*x/a^3+2^(-1+2*x)/(a*Log[2])+2^x*b/(a^2*Log[2])+b^2*Log[a-b/2^x]/(a^3*Log[2])");
  }

  // 2.3 Exponential functions.input:548
  public void test0147() {
    check( //
        "Integrate[2^x/(a-b/4^x), x]", //
        "2^x/(a*Log[2])-ArcTanh[2^x*Sqrt[a]/Sqrt[b]]*Sqrt[b]/(a^(3/2)*Log[2])");
  }

  // 2.3 Exponential functions.input:578
  public void test0148() {
    check( //
        "Integrate[x/(2+3*E^x+E^(2*x)), x]", //
        "1/4*x^2+1/2*x*Log[1+1/2*E^x]-x*Log[1+E^x]-PolyLog[2,-E^x]+1/2*PolyLog[2,-1/2*E^x]");
  }

  // 2.3 Exponential functions.input:602
  public void test0149() {
    check( //
        "Integrate[1/(2+1/3^x+3^x), x]", //
        "(-1)/((1+3^x)*Log[3])");
  }

  // 2.3 Exponential functions.input:613
  public void test0150() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[f+g*x]))^3/(d*f+(e*f+d*g)*x+e*g*x^2), x]", //
        "6*a^2*b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+6*a*b^2*ExpIntegralEi[2*c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+2*b^3*ExpIntegralEi[3*c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+2*a^3*Log[Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)");
  }

  // 2.3 Exponential functions.input:626
  public void test0151() {
    check( //
        "Integrate[(F^(Sqrt[1-a*x]/Sqrt[1+a*x]))^n/(1-a^2*x^2), x]", //
        "-(F^(Sqrt[1-a*x]/Sqrt[1+a*x]))^n*ExpIntegralEi[n*Log[F]*Sqrt[1-a*x]/Sqrt[1+a*x]]/(a*F^(n*Sqrt[1-a*x]/Sqrt[1+a*x]))");
  }

  // 2.3 Exponential functions.input:669
  public void test0152() {
    check( //
        "Integrate[F^(f*(a+b*Log[c*(d+e*x)^n]^2)), x]", //
        "1/2*F^(a*f)*(d+e*x)*Erfi[1/2*(1+2*b*f*n*Log[F]*Log[c*(d+e*x)^n])/(n*Sqrt[b]*Sqrt[f]*Sqrt[Log[F]])]*Sqrt[Pi]/(E^(1/4/(b*f*n^2*Log[F]))*e*n*(c*(d+e*x)^n)^(1/n)*Sqrt[b]*Sqrt[f]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:719
  public void test0153() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)*(a+b*x+c*x^2)^(1/2), x]", //
        "-1/2*Erfi[Sqrt[a+b*x+c*x^2]]*Sqrt[Pi]+E^(a+b*x+c*x^2)*Sqrt[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:753
  public void test0154() {
    check( //
        "Integrate[(1/E^(2*x)+E^(2*x))/((-1)/E^(2*x)+E^(2*x)), x]", //
        "-x+1/2*Log[1-E^(4*x)]");
  }

  // 2.3 Exponential functions.input:773
  public void test0155() {
    check( //
        "Integrate[E^(x^(1/3))/x^(2/3), x]", //
        "3*E^(x^(1/3))");
  }

  // 2.3 Exponential functions.input:795
  public void test0156() {
    check( //
        "Integrate[E^(2*x)/(1+E^x)^(1/4), x]", //
        "-4/3*(1+E^x)^(3/4)+4/7*(1+E^x)^(7/4)");
  }

  // 2.3 Exponential functions.input:813
  public void test0157() {
    check( //
        "Integrate[(a+E^x*b)^3, x]", //
        "3*E^x*a^2*b+3/2*E^(2*x)*a*b^2+1/3*E^(3*x)*b^3+a^3*x");
  }

  // 2.3 Exponential functions.input:829
  public void test0158() {
    check( //
        "Integrate[(2-7*E^(x^4))^5*x^3, x]", //
        "-140*E^(x^4)+490*E^(2*x^4)-3430/3*E^(3*x^4)+12005/8*E^(4*x^4)-16807/20*E^(5*x^4)+8*x^4");
  }

  // 2.3 Exponential functions.input:855
  public void test0159() {
    check( //
        "Integrate[(a+b*x)^2*Sqrt[f^x], x]", //
        "16*b^2*Sqrt[f^x]/Log[f]^3-8*b*(a+b*x)*Sqrt[f^x]/Log[f]^2+2*(a+b*x)^2*Sqrt[f^x]/Log[f]");
  }

  // 2.3 Exponential functions.input:865
  public void test0160() {
    check( //
        "Integrate[E^x/(E^x+x)^(1/2)+1/Sqrt[E^x+x], x]", //
        "2*Sqrt[E^x+x]");
  }

  // 2.3 Exponential functions.input:878
  public void test0161() {
    check( //
        "Integrate[E^x*((-1)/E^x+E^x)*(1/E^x+E^x)^2, x]", //
        "1/2/E^(2*x)+1/2*E^(2*x)+1/4*E^(4*x)-x");
  }

  // 2.3 Exponential functions.input:898
  public void test0162() {
    check( //
        "Integrate[E^((a+b*x)^3)*x, x]", //
        "1/3*a*(a+b*x)*Gamma[1/3,-(a+b*x)^3]/(b^2*(-(a+b*x)^3)^(1/3))-1/3*(a+b*x)^2*Gamma[2/3,-(a+b*x)^3]/(b^2*(-(a+b*x)^3)^(2/3))");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:13
  public void test0163() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^4, x]", //
        "24*e^4*F^(c*(a+b*x))/(b^5*c^5*Log[F]^5)-24*e^3*F^(c*(a+b*x))*(d+e*x)/(b^4*c^4*Log[F]^4)+12*e^2*F^(c*(a+b*x))*(d+e*x)^2/(b^3*c^3*Log[F]^3)-4*e*F^(c*(a+b*x))*(d+e*x)^3/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^4/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:21
  public void test0164() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^4, x]", //
        "-1/3*F^(c*(a+b*x))/(e*(d+e*x)^3)-1/6*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^2)-1/6*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*(d+e*x))+1/6*b^3*c^3*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]^3/e^4");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:29
  public void test0165() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d^5+5*d^4*e*x+10*d^3*e^2*x^2+10*d^2*e^3*x^3+5*d*e^4*x^4+e^5*x^5), x]", //
        "-1/4*F^(c*(a+b*x))/(e*(d+e*x)^4)-1/12*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^3)-1/24*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*(d+e*x)^2)-1/24*b^3*c^3*F^(c*(a+b*x))*Log[F]^3/(e^4*(d+e*x))+1/24*b^4*c^4*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]^4/e^5");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:47
  public void test0166() {
    check( //
        "Integrate[F^(a+b*x)/x^(1/2), x]", //
        "F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Sqrt[Pi]/(Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:55
  public void test0167() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^(1/2), x]", //
        "-1/2*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Sqrt[Pi]*Sqrt[e]/(b^(3/2)*c^(3/2)*Log[F]^(3/2))+F^(c*(a+b*x))*Sqrt[d+e*x]/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:77
  public void test0168() {
    check( //
        "Integrate[E^(-a-b*x)*x*(a+b*x)^3, x]", //
        "-24*E^(-a-b*x)/b^2+6*E^(-a-b*x)*a/b^2-24*E^(-a-b*x)*(a+b*x)/b^2+6*E^(-a-b*x)*a*(a+b*x)/b^2-12*E^(-a-b*x)*(a+b*x)^2/b^2+3*E^(-a-b*x)*a*(a+b*x)^2/b^2-4*E^(-a-b*x)*(a+b*x)^3/b^2+E^(-a-b*x)*a*(a+b*x)^3/b^2-E^(-a-b*x)*(a+b*x)^4/b^2");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:85
  public void test0169() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*x^2*(e+f*x)^2, x]", //
        "24*f^2*F^(a+b*c+b*d*x)/(b^5*d^5*Log[F]^5)-12*e*f*F^(a+b*c+b*d*x)/(b^4*d^4*Log[F]^4)-24*f^2*F^(a+b*c+b*d*x)*x/(b^4*d^4*Log[F]^4)+2*e^2*F^(a+b*c+b*d*x)/(b^3*d^3*Log[F]^3)+12*e*f*F^(a+b*c+b*d*x)*x/(b^3*d^3*Log[F]^3)+12*f^2*F^(a+b*c+b*d*x)*x^2/(b^3*d^3*Log[F]^3)-2*e^2*F^(a+b*c+b*d*x)*x/(b^2*d^2*Log[F]^2)-6*e*f*F^(a+b*c+b*d*x)*x^2/(b^2*d^2*Log[F]^2)-4*f^2*F^(a+b*c+b*d*x)*x^3/(b^2*d^2*Log[F]^2)+e^2*F^(a+b*c+b*d*x)*x^2/(b*d*Log[F])+2*e*f*F^(a+b*c+b*d*x)*x^3/(b*d*Log[F])+f^2*F^(a+b*c+b*d*x)*x^4/(b*d*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:95
  public void test0170() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4*(c+d*x)^3, x]", //
        "-5040*E^(-a-b*x)*d^3/b^4-2160*E^(-a-b*x)*d^2*(b*c-a*d)/b^4-360*E^(-a-b*x)*d*(b*c-a*d)^2/b^4-24*E^(-a-b*x)*(b*c-a*d)^3/b^4-5040*E^(-a-b*x)*d^3*(a+b*x)/b^4-2160*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)/b^4-360*E^(-a-b*x)*d*(b*c-a*d)^2*(a+b*x)/b^4-24*E^(-a-b*x)*(b*c-a*d)^3*(a+b*x)/b^4-2520*E^(-a-b*x)*d^3*(a+b*x)^2/b^4-1080*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)^2/b^4-180*E^(-a-b*x)*d*(b*c-a*d)^2*(a+b*x)^2/b^4-12*E^(-a-b*x)*(b*c-a*d)^3*(a+b*x)^2/b^4-840*E^(-a-b*x)*d^3*(a+b*x)^3/b^4-360*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)^3/b^4-60*E^(-a-b*x)*d*(b*c-a*d)^2*(a+b*x)^3/b^4-4*E^(-a-b*x)*(b*c-a*d)^3*(a+b*x)^3/b^4-210*E^(-a-b*x)*d^3*(a+b*x)^4/b^4-90*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)^4/b^4-15*E^(-a-b*x)*d*(b*c-a*d)^2*(a+b*x)^4/b^4-E^(-a-b*x)*(b*c-a*d)^3*(a+b*x)^4/b^4-42*E^(-a-b*x)*d^3*(a+b*x)^5/b^4-18*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)^5/b^4-3*E^(-a-b*x)*d*(b*c-a*d)^2*(a+b*x)^5/b^4-7*E^(-a-b*x)*d^3*(a+b*x)^6/b^4-3*E^(-a-b*x)*d^2*(b*c-a*d)*(a+b*x)^6/b^4-E^(-a-b*x)*d^3*(a+b*x)^7/b^4");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:115
  public void test0171() {
    check( //
        "Integrate[x^4*Sqrt[E^(a+b*x)], x]", //
        "768*Sqrt[E^(a+b*x)]/b^5-384*x*Sqrt[E^(a+b*x)]/b^4+96*x^2*Sqrt[E^(a+b*x)]/b^3-16*x^3*Sqrt[E^(a+b*x)]/b^2+2*x^4*Sqrt[E^(a+b*x)]/b");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:21
  public void test0172() {
    check( //
        "Integrate[x^2/(a+E^(c+d*x)*b)^2, x]", //
        "-x^2/(a^2*d)+x^2/(a*(a+E^(c+d*x)*b)*d)+1/3*x^3/a^2+2*x*Log[1+E^(c+d*x)*b/a]/(a^2*d^2)-x^2*Log[1+E^(c+d*x)*b/a]/(a^2*d)+2*PolyLog[2,-E^(c+d*x)*b/a]/(a^2*d^3)-2*x*PolyLog[2,-E^(c+d*x)*b/a]/(a^2*d^2)+2*PolyLog[3,-E^(c+d*x)*b/a]/(a^2*d^3)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:45
  public void test0173() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)/(c+d*x)^2, x]", //
        "-a/(d*(c+d*x))-b*(F^(e*g+f*g*x))^n/(d*(c+d*x))+b*f*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*g*n*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]*Log[F]/d^2");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:53
  public void test0174() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2/(c+d*x)^3, x]", //
        "-1/2*a^2/(d*(c+d*x)^2)-a*b*(F^(e*g+f*g*x))^n/(d*(c+d*x)^2)-1/2*b^2*(F^(e*g+f*g*x))^(2*n)/(d*(c+d*x)^2)-a*b*f*(F^(e*g+f*g*x))^n*g*n*Log[F]/(d^2*(c+d*x))-b^2*f*(F^(e*g+f*g*x))^(2*n)*g*n*Log[F]/(d^2*(c+d*x))+a*b*f^2*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*g^2*n^2*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3+2*b^2*f^2*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*g^2*n^2*ExpIntegralEi[2*f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:63
  public void test0175() {
    check( //
        "Integrate[(c+d*x)^3/(a+b*(F^(g*(e+f*x)))^n), x]", //
        "1/4*(c+d*x)^4/(a*d)-(c+d*x)^3*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a*f*g*n*Log[F])-3*d*(c+d*x)^2*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a*f^2*g^2*n^2*Log[F]^2)+6*d^2*(c+d*x)*PolyLog[3,-b*(F^(g*(e+f*x)))^n/a]/(a*f^3*g^3*n^3*Log[F]^3)-6*d^3*PolyLog[4,-b*(F^(g*(e+f*x)))^n/a]/(a*f^4*g^4*n^4*Log[F]^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:96
  public void test0176() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)^m, x]", //
        "a^2*(c+d*x)^(1+m)/(d*(1+m))+2^(-1-m)*b^2*F^(2*(e-c*f/d)*g*n-2*g*n*(e+f*x))*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^m*Gamma[1+m,-2*f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)+2*a*b*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*(c+d*x)^m*Gamma[1+m,-f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:115
  public void test0177() {
    check( //
        "Integrate[F^(c+d*x)*x/(a+b*F^(c+d*x))^2, x]", //
        "x/(a*b*d*Log[F])-x/(b*d*(a+b*F^(c+d*x))*Log[F])-Log[a+b*F^(c+d*x)]/(a*b*d^2*Log[F]^2)");
  }

  // 2.3 Exponential functions.input:12
  public void test0178() {
    check( //
        "Integrate[E^(d*x)/(a+E^(c+d*x)*b), x]", //
        "Log[a+E^(c+d*x)*b]/(E^c*b*d)");
  }

  // 2.3 Exponential functions.input:32
  public void test0179() {
    check( //
        "Integrate[E^(2*x)/(a+E^x*b)^2, x]", //
        "a/(b^2*(a+E^x*b))+Log[a+E^x*b]/b^2");
  }

  // 2.3 Exponential functions.input:40
  public void test0180() {
    check( //
        "Integrate[(a+E^(n*x)*b)/E^(n*x), x]", //
        "-a/(E^(n*x)*n)+b*x");
  }

  // 2.3 Exponential functions.input:48
  public void test0181() {
    check( //
        "Integrate[f^(a+3*b*x)/(c+d*f^(e+2*b*x)), x]", //
        "f^(1/2*(2*a-3*e)+1/2*(e+2*b*x))/(b*d*Log[f])-f^(a-3/2*e)*ArcTan[f^(1/2*(e+2*b*x))*Sqrt[d]/Sqrt[c]]*Sqrt[c]/(b*d^(3/2)*Log[f])");
  }

  // 2.3 Exponential functions.input:64
  public void test0182() {
    check( //
        "Integrate[f^x/(a+b*f^(2*x))^3, x]", //
        "1/4*f^x/(a*(a+b*f^(2*x))^2*Log[f])+3/8*f^x/(a^2*(a+b*f^(2*x))*Log[f])+3/8*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Log[f]*Sqrt[b])");
  }

  // 2.3 Exponential functions.input:74
  public void test0183() {
    check( //
        "Integrate[x/(b/f^x+a*f^x)^2, x]", //
        "1/2*x/(a*b*Log[f])-1/2*x/(a*(b+a*f^(2*x))*Log[f])-1/4*Log[b+a*f^(2*x)]/(a*b*Log[f]^2)");
  }

  // 2.3 Exponential functions.input:99
  public void test0184() {
    check( //
        "Integrate[f^(a+b*x^2)/x^5, x]", //
        "-1/4*f^(a+b*x^2)/x^4-1/4*b*f^(a+b*x^2)*Log[f]/x^2+1/4*b^2*f^a*ExpIntegralEi[b*x^2*Log[f]]*Log[f]^2");
  }

  // 2.3 Exponential functions.input:107
  public void test0185() {
    check( //
        "Integrate[f^(a+b*x^2)*x^4, x]", //
        "-3/4*f^(a+b*x^2)*x/(b^2*Log[f]^2)+1/2*f^(a+b*x^2)*x^3/(b*Log[f])+3/8*f^a*Erfi[x*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^(5/2)*Log[f]^(5/2))");
  }

  // 2.3 Exponential functions.input:141
  public void test0186() {
    check( //
        "Integrate[f^(a+b/x)*x^2, x]", //
        "1/3*f^(a+b/x)*x^3+1/6*b*f^(a+b/x)*x^2*Log[f]+1/6*b^2*f^(a+b/x)*x*Log[f]^2-1/6*b^3*f^a*ExpIntegralEi[b*Log[f]/x]*Log[f]^3");
  }

  // 2.3 Exponential functions.input:173
  public void test0187() {
    check( //
        "Integrate[f^(a+b/x^2)/x^8, x]", //
        "-15/8*f^(a+b/x^2)/(b^3*x*Log[f]^3)+5/4*f^(a+b/x^2)/(b^2*x^3*Log[f]^2)-1/2*f^(a+b/x^2)/(b*x^5*Log[f])+15/16*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Sqrt[Pi]/(b^(7/2)*Log[f]^(7/2))");
  }

  // 2.3 Exponential functions.input:181
  public void test0188() {
    check( //
        "Integrate[f^(a+b/x^3)*x^5, x]", //
        "1/6*f^(a+b/x^3)*x^6+1/6*b*f^(a+b/x^3)*x^3*Log[f]-1/6*b^2*f^a*ExpIntegralEi[b*Log[f]/x^3]*Log[f]^2");
  }

  // 2.3 Exponential functions.input:215
  public void test0189() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1+3/2*n), x]", //
        "f^(a+b*x^n)*x^(1/2*n)/(b*n*Log[f])-1/2*f^a*Erfi[x^(1/2*n)*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]/(b^(3/2)*n*Log[f]^(3/2))");
  }

  // 2.3 Exponential functions.input:255
  public void test0190() {
    check( //
        "Integrate[f^(c/(a+b*x))/x^2, x]", //
        "-b*f^(c/(a+b*x))/a-f^(c/(a+b*x))/x-b*c*f^(c/a)*ExpIntegralEi[-b*c*x*Log[f]/(a*(a+b*x))]*Log[f]/a^2");
  }

  // 2.3 Exponential functions.input:266
  public void test0191() {
    check( //
        "Integrate[f^(c/(a+b*x)^3)*x^3, x]", //
        "-a*f^(c/(a+b*x)^3)*(a+b*x)^3/b^4+a*c*ExpIntegralEi[c*Log[f]/(a+b*x)^3]*Log[f]/b^4-1/3*a^3*(a+b*x)*Gamma[-1/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(1/3)/b^4+a^2*(a+b*x)^2*Gamma[-2/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(2/3)/b^4+1/3*(a+b*x)^4*Gamma[-4/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(4/3)/b^4");
  }

  // 2.3 Exponential functions.input:310
  public void test0192() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(c+d*x)^8, x]", //
        "-105/16*F^(a+b*(c+d*x)^2)*(c+d*x)/(b^4*d*Log[F]^4)+35/8*F^(a+b*(c+d*x)^2)*(c+d*x)^3/(b^3*d*Log[F]^3)-7/4*F^(a+b*(c+d*x)^2)*(c+d*x)^5/(b^2*d*Log[F]^2)+1/2*F^(a+b*(c+d*x)^2)*(c+d*x)^7/(b*d*Log[F])+105/32*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(9/2)*d*Log[F]^(9/2))");
  }

  // 2.3 Exponential functions.input:318
  public void test0193() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)/(c+d*x)^8, x]", //
        "-1/7*F^(a+b*(c+d*x)^2)/(d*(c+d*x)^7)-2/35*b*F^(a+b*(c+d*x)^2)*Log[F]/(d*(c+d*x)^5)-4/105*b^2*F^(a+b*(c+d*x)^2)*Log[F]^2/(d*(c+d*x)^3)-8/105*b^3*F^(a+b*(c+d*x)^2)*Log[F]^3/(d*(c+d*x))+8/105*b^(7/2)*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Log[F]^(7/2)*Sqrt[Pi]/d");
  }

  // 2.3 Exponential functions.input:360
  public void test0194() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)*(c+d*x)^5, x]", //
        "1/6*F^(a+b/(c+d*x)^2)*(c+d*x)^6/d+1/12*b*F^(a+b/(c+d*x)^2)*(c+d*x)^4*Log[F]/d+1/12*b^2*F^(a+b/(c+d*x)^2)*(c+d*x)^2*Log[F]^2/d-1/12*b^3*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)^2]*Log[F]^3/d");
  }

  // 2.3 Exponential functions.input:392
  public void test0195() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^3)/(c+d*x)^10, x]", //
        "-2/3*F^(a+b/(c+d*x)^3)/(b^3*d*Log[F]^3)+2/3*F^(a+b/(c+d*x)^3)/(b^2*d*(c+d*x)^3*Log[F]^2)-1/3*F^(a+b/(c+d*x)^3)/(b*d*(c+d*x)^6*Log[F])");
  }

  // 2.3 Exponential functions.input:426
  public void test0196() {
    check( //
        "Integrate[(a+b*x)^(-1+1/2*n)/F^(c*(a+b*x)^n), x]", //
        "Erf[(a+b*x)^(1/2*n)*Sqrt[c]*Sqrt[Log[F]]]*Sqrt[Pi]/(b*n*Sqrt[c]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:441
  public void test0197() {
    check( //
        "Integrate[E^(e*(c+d*x)^3)*(a+b*x)^2, x]", //
        "1/3*E^(e*(c+d*x)^3)*b^2/(d^3*e)-1/3*(b*c-a*d)^2*(c+d*x)*Gamma[1/3,-e*(c+d*x)^3]/(d^3*(-e*(c+d*x)^3)^(1/3))+2/3*b*(b*c-a*d)*(c+d*x)^2*Gamma[2/3,-e*(c+d*x)^3]/(d^3*(-e*(c+d*x)^3)^(2/3))");
  }

  // 2.3 Exponential functions.input:453
  public void test0198() {
    check( //
        "Integrate[E^(e/(c+d*x))*(a+b*x)^3, x]", //
        "-E^(e/(c+d*x))*(b*c-a*d)^3*(c+d*x)/d^4+3/2*E^(e/(c+d*x))*b*(b*c-a*d)^2*e*(c+d*x)/d^4-1/2*E^(e/(c+d*x))*b^2*(b*c-a*d)*e^2*(c+d*x)/d^4+3/2*E^(e/(c+d*x))*b*(b*c-a*d)^2*(c+d*x)^2/d^4-1/2*E^(e/(c+d*x))*b^2*(b*c-a*d)*e*(c+d*x)^2/d^4-E^(e/(c+d*x))*b^2*(b*c-a*d)*(c+d*x)^3/d^4+(b*c-a*d)^3*e*ExpIntegralEi[e/(c+d*x)]/d^4-3/2*b*(b*c-a*d)^2*e^2*ExpIntegralEi[e/(c+d*x)]/d^4+1/2*b^2*(b*c-a*d)*e^3*ExpIntegralEi[e/(c+d*x)]/d^4+b^3*e^4*Gamma[-4,-e/(c+d*x)]/d^4");
  }

  // 2.3 Exponential functions.input:461
  public void test0199() {
    check( //
        "Integrate[E^(e/(c+d*x)^2)*(a+b*x)^2, x]", //
        "E^(e/(c+d*x)^2)*(b*c-a*d)^2*(c+d*x)/d^3+2/3*E^(e/(c+d*x)^2)*b^2*e*(c+d*x)/d^3-E^(e/(c+d*x)^2)*b*(b*c-a*d)*(c+d*x)^2/d^3+1/3*E^(e/(c+d*x)^2)*b^2*(c+d*x)^3/d^3+b*(b*c-a*d)*e*ExpIntegralEi[e/(c+d*x)^2]/d^3-2/3*b^2*e^(3/2)*Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]/d^3-(b*c-a*d)^2*Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]*Sqrt[e]/d^3");
  }

  // 2.3 Exponential functions.input:488
  public void test0200() {
    check( //
        "Integrate[E^(a+b*x-c*x^2)*x^2, x]", //
        "-1/4*E^(a+b*x-c*x^2)*b/c^2-1/2*E^(a+b*x-c*x^2)*x/c-1/8*E^(a+1/4*b^2/c)*b^2*Erf[1/2*(b-2*c*x)/Sqrt[c]]*Sqrt[Pi]/c^(5/2)-1/4*E^(a+1/4*b^2/c)*Erf[1/2*(b-2*c*x)/Sqrt[c]]*Sqrt[Pi]/c^(3/2)");
  }

  // 2.3 Exponential functions.input:502
  public void test0201() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*(d+e*x)^2, x]", //
        "1/4*e*(2*c*d-b*e)*f^(a+b*x+c*x^2)/(c^2*Log[f])+1/2*e*f^(a+b*x+c*x^2)*(d+e*x)/(c*Log[f])-1/4*e^2*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(3/2)*Log[f]^(3/2))+1/8*(2*c*d-b*e)^2*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(5/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:541
  public void test0202() {
    check( //
        "Integrate[2^(2*x)/(a-b/2^x), x]", //
        "b^2*x/a^3+2^(-1+2*x)/(a*Log[2])+2^x*b/(a^2*Log[2])+b^2*Log[a-b/2^x]/(a^3*Log[2])");
  }

  // 2.3 Exponential functions.input:549
  public void test0203() {
    check( //
        "Integrate[2^x/(a-b/2^(2*x)), x]", //
        "2^x/(a*Log[2])-ArcTanh[2^x*Sqrt[a]/Sqrt[b]]*Sqrt[b]/(a^(3/2)*Log[2])");
  }

  // 2.3 Exponential functions.input:587
  public void test0204() {
    check( //
        "Integrate[1/(1+2*f^(c+d*x)+f^(2*c+2*d*x)), x]", //
        "x+1/(d*(1+f^(c+d*x))*Log[f])-Log[1+f^(c+d*x)]/(d*Log[f])");
  }

  // 2.3 Exponential functions.input:603
  public void test0205() {
    check( //
        "Integrate[1/(1+(-1)/E^x+2*E^x), x]", //
        "1/3*Log[1-2*E^x]-1/3*Log[1+E^x]");
  }

  // 2.3 Exponential functions.input:614
  public void test0206() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[f+g*x]))^2/(d*f+(e*f+d*g)*x+e*g*x^2), x]", //
        "4*a*b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+2*b^2*ExpIntegralEi[2*c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+2*a^2*Log[Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)");
  }

  // 2.3 Exponential functions.input:627
  public void test0207() {
    check( //
        "Integrate[F^(3*Sqrt[1-a*x]/Sqrt[1+a*x])/(1-a^2*x^2), x]", //
        "-ExpIntegralEi[3*Log[F]*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 2.3 Exponential functions.input:637
  public void test0208() {
    check( //
        "Integrate[a^x*b^x/x, x]", //
        "ExpIntegralEi[x*(Log[a]+Log[b])]");
  }

  // 2.3 Exponential functions.input:688
  public void test0209() {
    check( //
        "Integrate[F^(f*(a+b*Log[c*(d+e*x)^n])^2)*(d*g+e*g*x)^2, x]", //
        "1/2*g^2*(d+e*x)^3*Erfi[1/2*(3/n+2*a*b*f*Log[F]+2*b^2*f*Log[F]*Log[c*(d+e*x)^n])/(b*Sqrt[f]*Sqrt[Log[F]])]*Sqrt[Pi]/(E^(3/4*(3+4*a*b*f*n*Log[F])/(b^2*f*n^2*Log[F]))*b*e*n*(c*(d+e*x)^n)^(3/n)*Sqrt[f]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:720
  public void test0210() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)/(a+b*x+c*x^2)^(1/2), x]", //
        "Erfi[Sqrt[a+b*x+c*x^2]]*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:742
  public void test0211() {
    check( //
        "Integrate[(-1+E^(2*x))/(3+E^(2*x)), x]", //
        "-1/3*x+2/3*Log[3+E^(2*x)]");
  }

  // 2.3 Exponential functions.input:774
  public void test0212() {
    check( //
        "Integrate[E^(3*x)*(-8+2*x^3+x^5), x]", //
        "-724/243*E^(3*x)+76/81*E^(3*x)*x-38/27*E^(3*x)*x^2+38/27*E^(3*x)*x^3-5/9*E^(3*x)*x^4+1/3*E^(3*x)*x^5");
  }

  // 2.3 Exponential functions.input:814
  public void test0213() {
    check( //
        "Integrate[(a+E^x*b)^4, x]", //
        "4*E^x*a^3*b+3*E^(2*x)*a^2*b^2+4/3*E^(3*x)*a*b^3+1/4*E^(4*x)*b^4+a^4*x");
  }

  // 2.3 Exponential functions.input:830
  public void test0214() {
    check( //
        "Integrate[E^(x^2)*x*Sqrt[1-E^(2*x^2)], x]", //
        "1/4*ArcSin[E^(x^2)]+1/4*E^(x^2)*Sqrt[1-E^(2*x^2)]");
  }

  // 2.3 Exponential functions.input:838
  public void test0215() {
    check( //
        "Integrate[1/((-1)/E^x+E^x)^2, x]", //
        "1/2/(1-E^(2*x))");
  }

  // 2.3 Exponential functions.input:846
  public void test0216() {
    check( //
        "Integrate[E^x*(-5+x+x^2)/(-1+x)^2, x]", //
        "E^x-3*E^x/(1-x)");
  }

  // 2.3 Exponential functions.input:866
  public void test0217() {
    check( //
        "Integrate[(1+E^x)*x/Sqrt[E^x+x]+2*Sqrt[E^x+x], x]", //
        "2*x*Sqrt[E^x+x]");
  }

  // 2.3 Exponential functions.input:889
  public void test0218() {
    check( //
        "Integrate[(a*F^(c+d*x))^m*(b*F^(e+f*x))^n, x]", //
        "(a*F^(c+d*x))^m*(b*F^(e+f*x))^n/((d*m+f*n)*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:14
  public void test0219() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^3, x]", //
        "-6*e^3*F^(c*(a+b*x))/(b^4*c^4*Log[F]^4)+6*e^2*F^(c*(a+b*x))*(d+e*x)/(b^3*c^3*Log[F]^3)-3*e*F^(c*(a+b*x))*(d+e*x)^2/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^3/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:22
  public void test0220() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^5, x]", //
        "-1/4*F^(c*(a+b*x))/(e*(d+e*x)^4)-1/12*b*c*F^(c*(a+b*x))*Log[F]/(e^2*(d+e*x)^3)-1/24*b^2*c^2*F^(c*(a+b*x))*Log[F]^2/(e^3*(d+e*x)^2)-1/24*b^3*c^3*F^(c*(a+b*x))*Log[F]^3/(e^4*(d+e*x))+1/24*b^4*c^4*F^(c*(a-b*d/e))*ExpIntegralEi[b*c*(d+e*x)*Log[F]/e]*Log[F]^4/e^5");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:48
  public void test0221() {
    check( //
        "Integrate[F^(a+b*x)/x^(3/2), x]", //
        "-2*F^(a+b*x)/Sqrt[x]+2*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Sqrt[Pi]*Sqrt[b]*Sqrt[Log[F]]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:56
  public void test0222() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^(1/2), x]", //
        "F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Sqrt[Pi]/(Sqrt[b]*Sqrt[c]*Sqrt[e]*Sqrt[Log[F]])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:78
  public void test0223() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^3, x]", //
        "-6*E^(-a-b*x)/b-6*E^(-a-b*x)*(a+b*x)/b-3*E^(-a-b*x)*(a+b*x)^2/b-E^(-a-b*x)*(a+b*x)^3/b");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:86
  public void test0224() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*x*(e+f*x)^2, x]", //
        "-6*f^2*F^(a+b*c+b*d*x)/(b^4*d^4*Log[F]^4)+4*e*f*F^(a+b*c+b*d*x)/(b^3*d^3*Log[F]^3)+6*f^2*F^(a+b*c+b*d*x)*x/(b^3*d^3*Log[F]^3)-e^2*F^(a+b*c+b*d*x)/(b^2*d^2*Log[F]^2)-4*e*f*F^(a+b*c+b*d*x)*x/(b^2*d^2*Log[F]^2)-3*f^2*F^(a+b*c+b*d*x)*x^2/(b^2*d^2*Log[F]^2)+e^2*F^(a+b*c+b*d*x)*x/(b*d*Log[F])+2*e*f*F^(a+b*c+b*d*x)*x^2/(b*d*Log[F])+f^2*F^(a+b*c+b*d*x)*x^3/(b*d*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:96
  public void test0225() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4*(c+d*x)^2, x]", //
        "-720*E^(-a-b*x)*d^2/b^3-240*E^(-a-b*x)*d*(b*c-a*d)/b^3-24*E^(-a-b*x)*(b*c-a*d)^2/b^3-720*E^(-a-b*x)*d^2*(a+b*x)/b^3-240*E^(-a-b*x)*d*(b*c-a*d)*(a+b*x)/b^3-24*E^(-a-b*x)*(b*c-a*d)^2*(a+b*x)/b^3-360*E^(-a-b*x)*d^2*(a+b*x)^2/b^3-120*E^(-a-b*x)*d*(b*c-a*d)*(a+b*x)^2/b^3-12*E^(-a-b*x)*(b*c-a*d)^2*(a+b*x)^2/b^3-120*E^(-a-b*x)*d^2*(a+b*x)^3/b^3-40*E^(-a-b*x)*d*(b*c-a*d)*(a+b*x)^3/b^3-4*E^(-a-b*x)*(b*c-a*d)^2*(a+b*x)^3/b^3-30*E^(-a-b*x)*d^2*(a+b*x)^4/b^3-10*E^(-a-b*x)*d*(b*c-a*d)*(a+b*x)^4/b^3-E^(-a-b*x)*(b*c-a*d)^2*(a+b*x)^4/b^3-6*E^(-a-b*x)*d^2*(a+b*x)^5/b^3-2*E^(-a-b*x)*d*(b*c-a*d)*(a+b*x)^5/b^3-E^(-a-b*x)*d^2*(a+b*x)^6/b^3");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:116
  public void test0226() {
    check( //
        "Integrate[x^3*Sqrt[E^(a+b*x)], x]", //
        "-96*Sqrt[E^(a+b*x)]/b^4+48*x*Sqrt[E^(a+b*x)]/b^3-12*x^2*Sqrt[E^(a+b*x)]/b^2+2*x^3*Sqrt[E^(a+b*x)]/b");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:12
  public void test0227() {
    check( //
        "Integrate[x^3/(a+E^(c+d*x)*b), x]", //
        "1/4*x^4/a-x^3*Log[1+E^(c+d*x)*b/a]/(a*d)-3*x^2*PolyLog[2,-E^(c+d*x)*b/a]/(a*d^2)+6*x*PolyLog[3,-E^(c+d*x)*b/a]/(a*d^3)-6*PolyLog[4,-E^(c+d*x)*b/a]/(a*d^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:22
  public void test0228() {
    check( //
        "Integrate[x/(a+E^(c+d*x)*b)^2, x]", //
        "-x/(a^2*d)+x/(a*(a+E^(c+d*x)*b)*d)+1/2*x^2/a^2+Log[a+E^(c+d*x)*b]/(a^2*d^2)-x*Log[1+E^(c+d*x)*b/a]/(a^2*d)-PolyLog[2,-E^(c+d*x)*b/a]/(a^2*d^2)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:46
  public void test0229() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)/(c+d*x)^3, x]", //
        "-1/2*a/(d*(c+d*x)^2)-1/2*b*(F^(e*g+f*g*x))^n/(d*(c+d*x)^2)-1/2*b*f*(F^(e*g+f*g*x))^n*g*n*Log[F]/(d^2*(c+d*x))+1/2*b*f^2*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*g^2*n^2*ExpIntegralEi[f*g*n*(c+d*x)*Log[F]/d]*Log[F]^2/d^3");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:54
  public void test0230() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)^3, x]", //
        "1/4*a^3*(c+d*x)^4/d-18*a^2*b*d^3*(F^(e*g+f*g*x))^n/(f^4*g^4*n^4*Log[F]^4)-9/8*a*b^2*d^3*(F^(e*g+f*g*x))^(2*n)/(f^4*g^4*n^4*Log[F]^4)-2/27*b^3*d^3*(F^(e*g+f*g*x))^(3*n)/(f^4*g^4*n^4*Log[F]^4)+18*a^2*b*d^2*(F^(e*g+f*g*x))^n*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)+9/4*a*b^2*d^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)+2/9*b^3*d^2*(F^(e*g+f*g*x))^(3*n)*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)-9*a^2*b*d*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)-9/4*a*b^2*d*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)-1/3*b^3*d*(F^(e*g+f*g*x))^(3*n)*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)+3*a^2*b*(F^(e*g+f*g*x))^n*(c+d*x)^3/(f*g*n*Log[F])+3/2*a*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^3/(f*g*n*Log[F])+1/3*b^3*(F^(e*g+f*g*x))^(3*n)*(c+d*x)^3/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:64
  public void test0231() {
    check( //
        "Integrate[(c+d*x)^2/(a+b*(F^(g*(e+f*x)))^n), x]", //
        "1/3*(c+d*x)^3/(a*d)-(c+d*x)^2*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a*f*g*n*Log[F])-2*d*(c+d*x)*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a*f^2*g^2*n^2*Log[F]^2)+2*d^2*PolyLog[3,-b*(F^(g*(e+f*x)))^n/a]/(a*f^3*g^3*n^3*Log[F]^3)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:97
  public void test0232() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)*(c+d*x)^m, x]", //
        "a*(c+d*x)^(1+m)/(d*(1+m))+b*F^((e-c*f/d)*g*n-g*n*(e+f*x))*(F^(e*g+f*g*x))^n*(c+d*x)^m*Gamma[1+m,-f*g*n*(c+d*x)*Log[F]/d]/(f*g*n*Log[F]*(-f*g*n*(c+d*x)*Log[F]/d)^m)");
  }

  // 2.3 Exponential functions.input:13
  public void test0233() {
    check( //
        "Integrate[E^(c+d*x)/(a+E^(c+d*x)*b), x]", //
        "Log[a+E^(c+d*x)*b]/(b*d)");
  }

  // 2.3 Exponential functions.input:21
  public void test0234() {
    check( //
        "Integrate[F^(d*x)*(a+b*F^(c+d*x))^n, x]", //
        "(a+b*F^(c+d*x))^(1+n)/(b*d*F^c*(1+n)*Log[F])");
  }

  // 2.3 Exponential functions.input:33
  public void test0235() {
    check( //
        "Integrate[E^(2*x)/(a+E^x*b)^3, x]", //
        "1/2*E^(2*x)/(a*(a+E^x*b)^2)");
  }

  // 2.3 Exponential functions.input:41
  public void test0236() {
    check( //
        "Integrate[(a+E^(n*x)*b)^2/E^(n*x), x]", //
        "-a^2/(E^(n*x)*n)+E^(n*x)*b^2/n+2*a*b*x");
  }

  // 2.3 Exponential functions.input:49
  public void test0237() {
    check( //
        "Integrate[f^(a+4*b*x)/(c+d*f^(e+2*b*x)), x]", //
        "1/2*f^(a-e+2*b*x)/(b*d*Log[f])-1/2*c*f^(a-2*e)*Log[c+d*f^(e+2*b*x)]/(b*d^2*Log[f])");
  }

  // 2.3 Exponential functions.input:65
  public void test0238() {
    check( //
        "Integrate[f^x*x/(a+b*f^(2*x))^3, x]", //
        "-1/8*f^x/(a^2*(a+b*f^(2*x))*Log[f]^2)+1/4*f^x*x/(a*(a+b*f^(2*x))^2*Log[f])+3/8*f^x*x/(a^2*(a+b*f^(2*x))*Log[f])-1/2*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Log[f]^2*Sqrt[b])+3/8*x*ArcTan[f^x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Log[f]*Sqrt[b])-3/16*I*PolyLog[2,-I*f^x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Log[f]^2*Sqrt[b])+3/16*I*PolyLog[2,I*f^x*Sqrt[b]/Sqrt[a]]/(a^(5/2)*Log[f]^2*Sqrt[b])");
  }

  // 2.3 Exponential functions.input:75
  public void test0239() {
    check( //
        "Integrate[x^2/(b/f^x+a*f^x)^2, x]", //
        "1/2*x^2/(a*b*Log[f])-1/2*x^2/(a*(b+a*f^(2*x))*Log[f])-1/2*x*Log[1+a*f^(2*x)/b]/(a*b*Log[f]^2)-1/4*PolyLog[2,-a*f^(2*x)/b]/(a*b*Log[f]^3)");
  }

  // 2.3 Exponential functions.input:100
  public void test0240() {
    check( //
        "Integrate[f^(a+b*x^2)/x^7, x]", //
        "-1/6*f^(a+b*x^2)/x^6-1/12*b*f^(a+b*x^2)*Log[f]/x^4-1/12*b^2*f^(a+b*x^2)*Log[f]^2/x^2+1/12*b^3*f^a*ExpIntegralEi[b*x^2*Log[f]]*Log[f]^3");
  }

  // 2.3 Exponential functions.input:142
  public void test0241() {
    check( //
        "Integrate[f^(a+b/x)*x, x]", //
        "1/2*f^(a+b/x)*x^2+1/2*b*f^(a+b/x)*x*Log[f]-1/2*b^2*f^a*ExpIntegralEi[b*Log[f]/x]*Log[f]^2");
  }

  // 2.3 Exponential functions.input:166
  public void test0242() {
    check( //
        "Integrate[f^(a+b/x^2)*x^6, x]", //
        "1/7*f^(a+b/x^2)*x^7+2/35*b*f^(a+b/x^2)*x^5*Log[f]+4/105*b^2*f^(a+b/x^2)*x^3*Log[f]^2+8/105*b^3*f^(a+b/x^2)*x*Log[f]^3-8/105*b^(7/2)*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Log[f]^(7/2)*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:174
  public void test0243() {
    check( //
        "Integrate[f^(a+b/x^2)/x^10, x]", //
        "105/16*f^(a+b/x^2)/(b^4*x*Log[f]^4)-35/8*f^(a+b/x^2)/(b^3*x^3*Log[f]^3)+7/4*f^(a+b/x^2)/(b^2*x^5*Log[f]^2)-1/2*f^(a+b/x^2)/(b*x^7*Log[f])-105/32*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Sqrt[Pi]/(b^(9/2)*Log[f]^(9/2))");
  }

  // 2.3 Exponential functions.input:208
  public void test0244() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1+3*n), x]", //
        "2*f^(a+b*x^n)/(b^3*n*Log[f]^3)-2*f^(a+b*x^n)*x^n/(b^2*n*Log[f]^2)+f^(a+b*x^n)*x^(2*n)/(b*n*Log[f])");
  }

  // 2.3 Exponential functions.input:216
  public void test0245() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1+1/2*n), x]", //
        "f^a*Erfi[x^(1/2*n)*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]/(n*Sqrt[b]*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:233
  public void test0246() {
    check( //
        "Integrate[f^(c*(a+b*x)^3)*x^2, x]", //
        "1/3*f^(c*(a+b*x)^3)/(b^3*c*Log[f])+2/3*a*(a+b*x)^2*Gamma[2/3,-c*(a+b*x)^3*Log[f]]/(b^3*(-c*(a+b*x)^3*Log[f])^(2/3))-1/3*a^2*(a+b*x)*Gamma[1/3,-c*(a+b*x)^3*Log[f]]/(b^3*(-c*(a+b*x)^3*Log[f])^(1/3))");
  }

  // 2.3 Exponential functions.input:246
  public void test0247() {
    check( //
        "Integrate[E^Sqrt[5+3*x], x]", //
        "-2/3*E^Sqrt[5+3*x]+2/3*E^Sqrt[5+3*x]*Sqrt[5+3*x]");
  }

  // 2.3 Exponential functions.input:267
  public void test0248() {
    check( //
        "Integrate[f^(c/(a+b*x)^3)*x^2, x]", //
        "1/3*f^(c/(a+b*x)^3)*(a+b*x)^3/b^3-1/3*c*ExpIntegralEi[c*Log[f]/(a+b*x)^3]*Log[f]/b^3+1/3*a^2*(a+b*x)*Gamma[-1/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(1/3)/b^3-2/3*a*(a+b*x)^2*Gamma[-2/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(2/3)/b^3");
  }

  // 2.3 Exponential functions.input:311
  public void test0249() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(c+d*x)^6, x]", //
        "15/8*F^(a+b*(c+d*x)^2)*(c+d*x)/(b^3*d*Log[F]^3)-5/4*F^(a+b*(c+d*x)^2)*(c+d*x)^3/(b^2*d*Log[F]^2)+1/2*F^(a+b*(c+d*x)^2)*(c+d*x)^5/(b*d*Log[F])-15/16*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(7/2)*d*Log[F]^(7/2))");
  }

  // 2.3 Exponential functions.input:353
  public void test0250() {
    check( //
        "Integrate[F^(a+b/(c+d*x))/(c+d*x)^4, x]", //
        "-2*F^(a+b/(c+d*x))/(b^3*d*Log[F]^3)+2*F^(a+b/(c+d*x))/(b^2*d*(c+d*x)*Log[F]^2)-F^(a+b/(c+d*x))/(b*d*(c+d*x)^2*Log[F])");
  }

  // 2.3 Exponential functions.input:361
  public void test0251() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)*(c+d*x)^3, x]", //
        "1/4*F^(a+b/(c+d*x)^2)*(c+d*x)^4/d+1/4*b*F^(a+b/(c+d*x)^2)*(c+d*x)^2*Log[F]/d-1/4*b^2*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)^2]*Log[F]^2/d");
  }

  // 2.3 Exponential functions.input:377
  public void test0252() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)/(c+d*x)^4, x]", //
        "-1/2*F^(a+b/(c+d*x)^2)/(b*d*(c+d*x)*Log[F])+1/4*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Sqrt[Pi]/(b^(3/2)*d*Log[F]^(3/2))");
  }

  // 2.3 Exponential functions.input:393
  public void test0253() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^3)/(c+d*x)^13, x]", //
        "2*F^(a+b/(c+d*x)^3)/(b^4*d*Log[F]^4)-2*F^(a+b/(c+d*x)^3)/(b^3*d*(c+d*x)^3*Log[F]^3)+F^(a+b/(c+d*x)^3)/(b^2*d*(c+d*x)^6*Log[F]^2)-1/3*F^(a+b/(c+d*x)^3)/(b*d*(c+d*x)^9*Log[F])");
  }

  // 2.3 Exponential functions.input:431
  public void test0254() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(e+f*x)^5, x]", //
        "f^5*F^(a+b*(c+d*x)^2)/(b^3*d^6*Log[F]^3)-5*f^3*(d*e-c*f)^2*F^(a+b*(c+d*x)^2)/(b^2*d^6*Log[F]^2)-15/4*f^4*(d*e-c*f)*F^(a+b*(c+d*x)^2)*(c+d*x)/(b^2*d^6*Log[F]^2)-f^5*F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b^2*d^6*Log[F]^2)+5/2*f*(d*e-c*f)^4*F^(a+b*(c+d*x)^2)/(b*d^6*Log[F])+5*f^2*(d*e-c*f)^3*F^(a+b*(c+d*x)^2)*(c+d*x)/(b*d^6*Log[F])+5*f^3*(d*e-c*f)^2*F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b*d^6*Log[F])+5/2*f^4*(d*e-c*f)*F^(a+b*(c+d*x)^2)*(c+d*x)^3/(b*d^6*Log[F])+1/2*f^5*F^(a+b*(c+d*x)^2)*(c+d*x)^4/(b*d^6*Log[F])+15/8*f^4*(d*e-c*f)*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(5/2)*d^6*Log[F]^(5/2))-5/2*f^2*(d*e-c*f)^3*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(3/2)*d^6*Log[F]^(3/2))+1/2*(d*e-c*f)^5*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(d^6*Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:442
  public void test0255() {
    check( //
        "Integrate[E^(e*(c+d*x)^3)*(a+b*x), x]", //
        "1/3*(b*c-a*d)*(c+d*x)*Gamma[1/3,-e*(c+d*x)^3]/(d^2*(-e*(c+d*x)^3)^(1/3))-1/3*b*(c+d*x)^2*Gamma[2/3,-e*(c+d*x)^3]/(d^2*(-e*(c+d*x)^3)^(2/3))");
  }

  // 2.3 Exponential functions.input:454
  public void test0256() {
    check( //
        "Integrate[E^(e/(c+d*x))*(a+b*x)^2, x]", //
        "E^(e/(c+d*x))*(b*c-a*d)^2*(c+d*x)/d^3-E^(e/(c+d*x))*b*(b*c-a*d)*e*(c+d*x)/d^3+1/6*E^(e/(c+d*x))*b^2*e^2*(c+d*x)/d^3-E^(e/(c+d*x))*b*(b*c-a*d)*(c+d*x)^2/d^3+1/6*E^(e/(c+d*x))*b^2*e*(c+d*x)^2/d^3+1/3*E^(e/(c+d*x))*b^2*(c+d*x)^3/d^3-(b*c-a*d)^2*e*ExpIntegralEi[e/(c+d*x)]/d^3+b*(b*c-a*d)*e^2*ExpIntegralEi[e/(c+d*x)]/d^3-1/6*b^2*e^3*ExpIntegralEi[e/(c+d*x)]/d^3");
  }

  // 2.3 Exponential functions.input:462
  public void test0257() {
    check( //
        "Integrate[E^(e/(c+d*x)^2)*(a+b*x), x]", //
        "-E^(e/(c+d*x)^2)*(b*c-a*d)*(c+d*x)/d^2+1/2*E^(e/(c+d*x)^2)*b*(c+d*x)^2/d^2-1/2*b*e*ExpIntegralEi[e/(c+d*x)^2]/d^2+(b*c-a*d)*Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]*Sqrt[e]/d^2");
  }

  // 2.3 Exponential functions.input:489
  public void test0258() {
    check( //
        "Integrate[E^(a+b*x-c*x^2)*x, x]", //
        "-1/2*E^(a+b*x-c*x^2)/c-1/4*E^(a+1/4*b^2/c)*b*Erf[1/2*(b-2*c*x)/Sqrt[c]]*Sqrt[Pi]/c^(3/2)");
  }

  // 2.3 Exponential functions.input:503
  public void test0259() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*(d+e*x), x]", //
        "1/2*e*f^(a+b*x+c*x^2)/(c*Log[f])+1/4*(2*c*d-b*e)*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(3/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:514
  public void test0260() {
    check( //
        "Integrate[f^(b*x+c*x^2)*(b+2*c*x)^2, x]", //
        "f^(b*x+c*x^2)*(b+2*c*x)/Log[f]-Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/(f^(1/4*b^2/c)*Log[f]^(3/2))");
  }

  // 2.3 Exponential functions.input:534
  public void test0261() {
    check( //
        "Integrate[4^x/(a+2^x*b), x]", //
        "2^x/(b*Log[2])-a*Log[a+2^x*b]/(b^2*Log[2])");
  }

  // 2.3 Exponential functions.input:572
  public void test0262() {
    check( //
        "Integrate[1/(1+2*E^x+E^(2*x)), x]", //
        "1/(1+E^x)+x-Log[1+E^x]");
  }

  // 2.3 Exponential functions.input:596
  public void test0263() {
    check( //
        "Integrate[1/(2+1/E^x+E^x), x]", //
        "(-1)/(1+E^x)");
  }

  // 2.3 Exponential functions.input:615
  public void test0264() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[f+g*x]))/(d*f+(e*f+d*g)*x+e*g*x^2), x]", //
        "2*b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)+2*a*Log[Sqrt[d+e*x]/Sqrt[f+g*x]]/(e*f-d*g)");
  }

  // 2.3 Exponential functions.input:628
  public void test0265() {
    check( //
        "Integrate[F^(2*Sqrt[1-a*x]/Sqrt[1+a*x])/(1-a^2*x^2), x]", //
        "-ExpIntegralEi[2*Log[F]*Sqrt[1-a*x]/Sqrt[1+a*x]]/a");
  }

  // 2.3 Exponential functions.input:689
  public void test0266() {
    check( //
        "Integrate[F^(f*(a+b*Log[c*(d+e*x)^n])^2)*(d*g+e*g*x), x]", //
        "1/2*g*(d+e*x)^2*Erfi[(1/n+a*b*f*Log[F]+b^2*f*Log[F]*Log[c*(d+e*x)^n])/(b*Sqrt[f]*Sqrt[Log[F]])]*Sqrt[Pi]/(E^((1+2*a*b*f*n*Log[F])/(b^2*f*n^2*Log[F]))*b*e*n*(c*(d+e*x)^n)^(2/n)*Sqrt[f]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:700
  public void test0267() {
    check( //
        "Integrate[F^(f*(a+b*Log[c*(d+e*x)^n])^2), x]", //
        "1/2*(d+e*x)*Erfi[1/2*(1/n+2*a*b*f*Log[F]+2*b^2*f*Log[F]*Log[c*(d+e*x)^n])/(b*Sqrt[f]*Sqrt[Log[F]])]*Sqrt[Pi]/(E^(1/4*(1+4*a*b*f*n*Log[F])/(b^2*f*n^2*Log[F]))*b*e*n*(c*(d+e*x)^n)^(1/n)*Sqrt[f]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:713
  public void test0268() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)/(a+b*x+c*x^2), x]", //
        "ExpIntegralEi[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:721
  public void test0269() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)/(a+b*x+c*x^2)^(3/2), x]", //
        "2*Erfi[Sqrt[a+b*x+c*x^2]]*Sqrt[Pi]-2*E^(a+b*x+c*x^2)/Sqrt[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:733
  public void test0270() {
    check( //
        "Integrate[E^x*Sqrt[3-4*E^(2*x)], x]", //
        "3/4*ArcSin[2*E^x/Sqrt[3]]+1/2*E^x*Sqrt[3-4*E^(2*x)]");
  }

  // 2.3 Exponential functions.input:775
  public void test0271() {
    check( //
        "Integrate[(E^x+x)^2, x]", //
        "-2*E^x+1/2*E^(2*x)+2*E^x*x+1/3*x^3");
  }

  // 2.3 Exponential functions.input:801
  public void test0272() {
    check( //
        "Integrate[E^x*(-5*x+x^2), x]", //
        "7*E^x-7*E^x*x+E^x*x^2");
  }

  // 2.3 Exponential functions.input:815
  public void test0273() {
    check( //
        "Integrate[1/Sqrt[a+E^(c+d*x)*b], x]", //
        "-2*ArcTanh[Sqrt[a+E^(c+d*x)*b]/Sqrt[a]]/(d*Sqrt[a])");
  }

  // 2.3 Exponential functions.input:831
  public void test0274() {
    check( //
        "Integrate[E^(x^3)*(1-E^(4*x^3))^2*x^2, x]", //
        "1/3*E^(x^3)-2/15*E^(5*x^3)+1/27*E^(9*x^3)");
  }

  // 2.3 Exponential functions.input:839
  public void test0275() {
    check( //
        "Integrate[E^x*((-1)/E^x+E^x)^2, x]", //
        "(-1)/E^x-2*E^x+1/3*E^(3*x)");
  }

  // 2.3 Exponential functions.input:847
  public void test0276() {
    check( //
        "Integrate[E^(x^2)*x^3/(1+x^2)^2, x]", //
        "1/2*E^(x^2)/(1+x^2)");
  }

  // 2.3 Exponential functions.input:867
  public void test0277() {
    check( //
        "Integrate[x/Sqrt[E^x+x]+E^x*x/Sqrt[E^x+x]+2*Sqrt[E^x+x], x]", //
        "2*x*Sqrt[E^x+x]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:15
  public void test0278() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x)^2, x]", //
        "2*e^2*F^(c*(a+b*x))/(b^3*c^3*Log[F]^3)-2*e*F^(c*(a+b*x))*(d+e*x)/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^2/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:23
  public void test0279() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d^4+4*d^3*e*x+6*d^2*e^2*x^2+4*d*e^3*x^3+e^4*x^4), x]", //
        "24*e^4*F^(c*(a+b*x))/(b^5*c^5*Log[F]^5)-24*e^3*F^(c*(a+b*x))*(d+e*x)/(b^4*c^4*Log[F]^4)+12*e^2*F^(c*(a+b*x))*(d+e*x)^2/(b^3*c^3*Log[F]^3)-4*e*F^(c*(a+b*x))*(d+e*x)^3/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^4/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:49
  public void test0280() {
    check( //
        "Integrate[F^(a+b*x)/x^(5/2), x]", //
        "-2/3*F^(a+b*x)/x^(3/2)+4/3*b^(3/2)*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Log[F]^(3/2)*Sqrt[Pi]-4/3*b*F^(a+b*x)*Log[F]/Sqrt[x]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:57
  public void test0281() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^(3/2), x]", //
        "-2*F^(c*(a+b*x))/(e*Sqrt[d+e*x])+2*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Sqrt[Pi]*Sqrt[b]*Sqrt[c]*Sqrt[Log[F]]/e^(3/2)");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:69
  public void test0282() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x+f*x^2), x]", //
        "2*f*F^(c*(a+b*x))/(b^3*c^3*Log[F]^3)-e*F^(c*(a+b*x))/(b^2*c^2*Log[F]^2)-2*f*F^(c*(a+b*x))*x/(b^2*c^2*Log[F]^2)+d*F^(c*(a+b*x))/(b*c*Log[F])+e*F^(c*(a+b*x))*x/(b*c*Log[F])+f*F^(c*(a+b*x))*x^2/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:79
  public void test0283() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^3/x, x]", //
        "-2*E^(-a-b*x)-3*E^(-a-b*x)*a-3*E^(-a-b*x)*a^2-2*E^(-a-b*x)*b*x-3*E^(-a-b*x)*a*b*x-E^(-a-b*x)*b^2*x^2+a^3*ExpIntegralEi[-b*x]/E^a");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:87
  public void test0284() {
    check( //
        "Integrate[F^(a+b*(c+d*x))*(e+f*x)^2, x]", //
        "2*f^2*F^(a+b*c+b*d*x)/(b^3*d^3*Log[F]^3)-2*f*F^(a+b*c+b*d*x)*(e+f*x)/(b^2*d^2*Log[F]^2)+F^(a+b*c+b*d*x)*(e+f*x)^2/(b*d*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:97
  public void test0285() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4*(c+d*x), x]", //
        "-120*E^(-a-b*x)*d/b^2-24*E^(-a-b*x)*(b*c-a*d)/b^2-120*E^(-a-b*x)*d*(a+b*x)/b^2-24*E^(-a-b*x)*(b*c-a*d)*(a+b*x)/b^2-60*E^(-a-b*x)*d*(a+b*x)^2/b^2-12*E^(-a-b*x)*(b*c-a*d)*(a+b*x)^2/b^2-20*E^(-a-b*x)*d*(a+b*x)^3/b^2-4*E^(-a-b*x)*(b*c-a*d)*(a+b*x)^3/b^2-5*E^(-a-b*x)*d*(a+b*x)^4/b^2-E^(-a-b*x)*(b*c-a*d)*(a+b*x)^4/b^2-E^(-a-b*x)*d*(a+b*x)^5/b^2");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:117
  public void test0286() {
    check( //
        "Integrate[x^2*Sqrt[E^(a+b*x)], x]", //
        "16*Sqrt[E^(a+b*x)]/b^3-8*x*Sqrt[E^(a+b*x)]/b^2+2*x^2*Sqrt[E^(a+b*x)]/b");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:13
  public void test0287() {
    check( //
        "Integrate[x^2/(a+E^(c+d*x)*b), x]", //
        "1/3*x^3/a-x^2*Log[1+E^(c+d*x)*b/a]/(a*d)-2*x*PolyLog[2,-E^(c+d*x)*b/a]/(a*d^2)+2*PolyLog[3,-E^(c+d*x)*b/a]/(a*d^3)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:47
  public void test0288() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)^3, x]", //
        "1/4*a^2*(c+d*x)^4/d-12*a*b*d^3*(F^(e*g+f*g*x))^n/(f^4*g^4*n^4*Log[F]^4)-3/8*b^2*d^3*(F^(e*g+f*g*x))^(2*n)/(f^4*g^4*n^4*Log[F]^4)+12*a*b*d^2*(F^(e*g+f*g*x))^n*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)+3/4*b^2*d^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)-6*a*b*d*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)-3/4*b^2*d*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)+2*a*b*(F^(e*g+f*g*x))^n*(c+d*x)^3/(f*g*n*Log[F])+1/2*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^3/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:55
  public void test0289() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)^2, x]", //
        "1/3*a^3*(c+d*x)^3/d+6*a^2*b*d^2*(F^(e*g+f*g*x))^n/(f^3*g^3*n^3*Log[F]^3)+3/4*a*b^2*d^2*(F^(e*g+f*g*x))^(2*n)/(f^3*g^3*n^3*Log[F]^3)+2/27*b^3*d^2*(F^(e*g+f*g*x))^(3*n)/(f^3*g^3*n^3*Log[F]^3)-6*a^2*b*d*(F^(e*g+f*g*x))^n*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)-3/2*a*b^2*d*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)-2/9*b^3*d*(F^(e*g+f*g*x))^(3*n)*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)+3*a^2*b*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f*g*n*Log[F])+3/2*a*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^2/(f*g*n*Log[F])+1/3*b^3*(F^(e*g+f*g*x))^(3*n)*(c+d*x)^2/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:65
  public void test0290() {
    check( //
        "Integrate[(c+d*x)/(a+b*(F^(g*(e+f*x)))^n), x]", //
        "1/2*(c+d*x)^2/(a*d)-(c+d*x)*Log[1+b*(F^(g*(e+f*x)))^n/a]/(a*f*g*n*Log[F])-d*PolyLog[2,-b*(F^(g*(e+f*x)))^n/a]/(a*f^2*g^2*n^2*Log[F]^2)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:107
  public void test0291() {
    check( //
        "Integrate[F^(c+d*x)*x^3/(a+b*F^(c+d*x)), x]", //
        "x^3*Log[1+b*F^(c+d*x)/a]/(b*d*Log[F])+3*x^2*PolyLog[2,-b*F^(c+d*x)/a]/(b*d^2*Log[F]^2)-6*x*PolyLog[3,-b*F^(c+d*x)/a]/(b*d^3*Log[F]^3)+6*PolyLog[4,-b*F^(c+d*x)/a]/(b*d^4*Log[F]^4)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:119
  public void test0292() {
    check( //
        "Integrate[F^(c+d*x)*x^3/(a+b*F^(c+d*x))^3, x]", //
        "-3/2*x^2/(a^2*b*d^2*Log[F]^2)+3/2*x^2/(a*b*d^2*(a+b*F^(c+d*x))*Log[F]^2)+1/2*x^3/(a^2*b*d*Log[F])-1/2*x^3/(b*d*(a+b*F^(c+d*x))^2*Log[F])+3*x*Log[1+b*F^(c+d*x)/a]/(a^2*b*d^3*Log[F]^3)-3/2*x^2*Log[1+b*F^(c+d*x)/a]/(a^2*b*d^2*Log[F]^2)+3*PolyLog[2,-b*F^(c+d*x)/a]/(a^2*b*d^4*Log[F]^4)-3*x*PolyLog[2,-b*F^(c+d*x)/a]/(a^2*b*d^3*Log[F]^3)+3*PolyLog[3,-b*F^(c+d*x)/a]/(a^2*b*d^4*Log[F]^4)");
  }

  // 2.3 Exponential functions.input:34
  public void test0293() {
    check( //
        "Integrate[E^(2*x)/(a+E^x*b)^4, x]", //
        "1/3*a/(b^2*(a+E^x*b)^3)+(-1/2)/(b^2*(a+E^x*b)^2)");
  }

  // 2.3 Exponential functions.input:42
  public void test0294() {
    check( //
        "Integrate[(a+E^(n*x)*b)^3/E^(n*x), x]", //
        "-a^3/(E^(n*x)*n)+3*E^(n*x)*a*b^2/n+1/2*E^(2*n*x)*b^3/n+3*a^2*b*x");
  }

  // 2.3 Exponential functions.input:50
  public void test0295() {
    check( //
        "Integrate[f^(a+5*b*x)/(c+d*f^(e+2*b*x)), x]", //
        "-c*f^(1/2*(2*a-5*e)+1/2*(e+2*b*x))/(b*d^2*Log[f])+1/3*f^(1/2*(2*a-5*e)+3/2*(e+2*b*x))/(b*d*Log[f])+c^(3/2)*f^(a-5/2*e)*ArcTan[f^(1/2*(e+2*b*x))*Sqrt[d]/Sqrt[c]]/(b*d^(5/2)*Log[f])");
  }

  // 2.3 Exponential functions.input:76
  public void test0296() {
    check( //
        "Integrate[x^3/(b/f^x+a*f^x)^2, x]", //
        "1/2*x^3/(a*b*Log[f])-1/2*x^3/(a*(b+a*f^(2*x))*Log[f])-3/4*x^2*Log[1+a*f^(2*x)/b]/(a*b*Log[f]^2)-3/4*x*PolyLog[2,-a*f^(2*x)/b]/(a*b*Log[f]^3)+3/8*PolyLog[3,-a*f^(2*x)/b]/(a*b*Log[f]^4)");
  }

  // 2.3 Exponential functions.input:93
  public void test0297() {
    check( //
        "Integrate[f^(a+b*x^2)*x^7, x]", //
        "-3*f^(a+b*x^2)/(b^4*Log[f]^4)+3*f^(a+b*x^2)*x^2/(b^3*Log[f]^3)-3/2*f^(a+b*x^2)*x^4/(b^2*Log[f]^2)+1/2*f^(a+b*x^2)*x^6/(b*Log[f])");
  }

  // 2.3 Exponential functions.input:125
  public void test0298() {
    check( //
        "Integrate[f^(a+b*x^3)/x^7, x]", //
        "-1/6*f^(a+b*x^3)/x^6-1/6*b*f^(a+b*x^3)*Log[f]/x^3+1/6*b^2*f^a*ExpIntegralEi[b*x^3*Log[f]]*Log[f]^2");
  }

  // 2.3 Exponential functions.input:167
  public void test0299() {
    check( //
        "Integrate[f^(a+b/x^2)*x^4, x]", //
        "1/5*f^(a+b/x^2)*x^5+2/15*b*f^(a+b/x^2)*x^3*Log[f]+4/15*b^2*f^(a+b/x^2)*x*Log[f]^2-4/15*b^(5/2)*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Log[f]^(5/2)*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:217
  public void test0300() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1-1/2*n), x]", //
        "-2*f^(a+b*x^n)/(n*x^(1/2*n))+2*f^a*Erfi[x^(1/2*n)*Sqrt[b]*Sqrt[Log[f]]]*Sqrt[Pi]*Sqrt[b]*Sqrt[Log[f]]/n");
  }

  // 2.3 Exponential functions.input:234
  public void test0301() {
    check( //
        "Integrate[f^(c*(a+b*x)^3)*x, x]", //
        "-1/3*(a+b*x)^2*Gamma[2/3,-c*(a+b*x)^3*Log[f]]/(b^2*(-c*(a+b*x)^3*Log[f])^(2/3))+1/3*a*(a+b*x)*Gamma[1/3,-c*(a+b*x)^3*Log[f]]/(b^2*(-c*(a+b*x)^3*Log[f])^(1/3))");
  }

  // 2.3 Exponential functions.input:249
  public void test0302() {
    check( //
        "Integrate[f^(c/(a+b*x))*x^4, x]", //
        "a^4*f^(c/(a+b*x))*(a+b*x)/b^5-2*a^3*f^(c/(a+b*x))*(a+b*x)^2/b^5+2*a^2*f^(c/(a+b*x))*(a+b*x)^3/b^5-2*a^3*c*f^(c/(a+b*x))*(a+b*x)*Log[f]/b^5+a^2*c*f^(c/(a+b*x))*(a+b*x)^2*Log[f]/b^5-a^4*c*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]/b^5+a^2*c^2*f^(c/(a+b*x))*(a+b*x)*Log[f]^2/b^5+2*a^3*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^2/b^5-a^2*c^3*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^3/b^5-4*a*c^4*Gamma[-4,-c*Log[f]/(a+b*x)]*Log[f]^4/b^5-c^5*Gamma[-5,-c*Log[f]/(a+b*x)]*Log[f]^5/b^5");
  }

  // 2.3 Exponential functions.input:257
  public void test0303() {
    check( //
        "Integrate[f^(c/(a+b*x)^2)*x^4, x]", //
        "a^4*f^(c/(a+b*x)^2)*(a+b*x)/b^5-2*a^3*f^(c/(a+b*x)^2)*(a+b*x)^2/b^5+2*a^2*f^(c/(a+b*x)^2)*(a+b*x)^3/b^5-a*f^(c/(a+b*x)^2)*(a+b*x)^4/b^5+1/5*f^(c/(a+b*x)^2)*(a+b*x)^5/b^5+4*a^2*c*f^(c/(a+b*x)^2)*(a+b*x)*Log[f]/b^5-a*c*f^(c/(a+b*x)^2)*(a+b*x)^2*Log[f]/b^5+2/15*c*f^(c/(a+b*x)^2)*(a+b*x)^3*Log[f]/b^5+2*a^3*c*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]/b^5+4/15*c^2*f^(c/(a+b*x)^2)*(a+b*x)*Log[f]^2/b^5+a*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]^2/b^5-4*a^2*c^(3/2)*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Log[f]^(3/2)*Sqrt[Pi]/b^5-4/15*c^(5/2)*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Log[f]^(5/2)*Sqrt[Pi]/b^5-a^4*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Sqrt[Pi]*Sqrt[c]*Sqrt[Log[f]]/b^5");
  }

  // 2.3 Exponential functions.input:268
  public void test0304() {
    check( //
        "Integrate[f^(c/(a+b*x)^3)*x, x]", //
        "-1/3*a*(a+b*x)*Gamma[-1/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(1/3)/b^2+1/3*(a+b*x)^2*Gamma[-2/3,-c*Log[f]/(a+b*x)^3]*(-c*Log[f]/(a+b*x)^3)^(2/3)/b^2");
  }

  // 2.3 Exponential functions.input:304
  public void test0305() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)/(c+d*x)^5, x]", //
        "-1/4*F^(a+b*(c+d*x)^2)/(d*(c+d*x)^4)-1/4*b*F^(a+b*(c+d*x)^2)*Log[F]/(d*(c+d*x)^2)+1/4*b^2*F^a*ExpIntegralEi[b*(c+d*x)^2*Log[F]]*Log[F]^2/d");
  }

  // 2.3 Exponential functions.input:312
  public void test0306() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(c+d*x)^4, x]", //
        "-3/4*F^(a+b*(c+d*x)^2)*(c+d*x)/(b^2*d*Log[F]^2)+1/2*F^(a+b*(c+d*x)^2)*(c+d*x)^3/(b*d*Log[F])+3/8*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(5/2)*d*Log[F]^(5/2))");
  }

  // 2.3 Exponential functions.input:354
  public void test0307() {
    check( //
        "Integrate[F^(a+b/(c+d*x))/(c+d*x)^5, x]", //
        "6*F^(a+b/(c+d*x))/(b^4*d*Log[F]^4)-6*F^(a+b/(c+d*x))/(b^3*d*(c+d*x)*Log[F]^3)+3*F^(a+b/(c+d*x))/(b^2*d*(c+d*x)^2*Log[F]^2)-F^(a+b/(c+d*x))/(b*d*(c+d*x)^3*Log[F])");
  }

  // 2.3 Exponential functions.input:378
  public void test0308() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)/(c+d*x)^6, x]", //
        "3/4*F^(a+b/(c+d*x)^2)/(b^2*d*(c+d*x)*Log[F]^2)-1/2*F^(a+b/(c+d*x)^2)/(b*d*(c+d*x)^3*Log[F])-3/8*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Sqrt[Pi]/(b^(5/2)*d*Log[F]^(5/2))");
  }

  // 2.3 Exponential functions.input:386
  public void test0309() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^3)*(c+d*x)^8, x]", //
        "1/9*F^(a+b/(c+d*x)^3)*(c+d*x)^9/d+1/18*b*F^(a+b/(c+d*x)^3)*(c+d*x)^6*Log[F]/d+1/18*b^2*F^(a+b/(c+d*x)^3)*(c+d*x)^3*Log[F]^2/d-1/18*b^3*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)^3]*Log[F]^3/d");
  }

  // 2.3 Exponential functions.input:432
  public void test0310() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(e+f*x)^4, x]", //
        "-2*f^3*(d*e-c*f)*F^(a+b*(c+d*x)^2)/(b^2*d^5*Log[F]^2)-3/4*f^4*F^(a+b*(c+d*x)^2)*(c+d*x)/(b^2*d^5*Log[F]^2)+2*f*(d*e-c*f)^3*F^(a+b*(c+d*x)^2)/(b*d^5*Log[F])+3*f^2*(d*e-c*f)^2*F^(a+b*(c+d*x)^2)*(c+d*x)/(b*d^5*Log[F])+2*f^3*(d*e-c*f)*F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b*d^5*Log[F])+1/2*f^4*F^(a+b*(c+d*x)^2)*(c+d*x)^3/(b*d^5*Log[F])+3/8*f^4*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(5/2)*d^5*Log[F]^(5/2))-3/2*f^2*(d*e-c*f)^2*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(3/2)*d^5*Log[F]^(3/2))+1/2*(d*e-c*f)^4*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(d^5*Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:455
  public void test0311() {
    check( //
        "Integrate[E^(e/(c+d*x))*(a+b*x), x]", //
        "-E^(e/(c+d*x))*(b*c-a*d)*(c+d*x)/d^2+1/2*E^(e/(c+d*x))*b*e*(c+d*x)/d^2+1/2*E^(e/(c+d*x))*b*(c+d*x)^2/d^2+(b*c-a*d)*e*ExpIntegralEi[e/(c+d*x)]/d^2-1/2*b*e^2*ExpIntegralEi[e/(c+d*x)]/d^2");
  }

  // 2.3 Exponential functions.input:463
  public void test0312() {
    check( //
        "Integrate[E^(e/(c+d*x)^2), x]", //
        "E^(e/(c+d*x)^2)*(c+d*x)/d-Erfi[Sqrt[e]/(c+d*x)]*Sqrt[Pi]*Sqrt[e]/d");
  }

  // 2.3 Exponential functions.input:535
  public void test0313() {
    check( //
        "Integrate[2^(2*x)/(a+2^x*b), x]", //
        "2^x/(b*Log[2])-a*Log[a+2^x*b]/(b^2*Log[2])");
  }

  // 2.3 Exponential functions.input:605
  public void test0314() {
    check( //
        "Integrate[x/(a+b/E^x+E^x*c), x]", //
        "x*Log[1+2*E^x*c/(a-Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]-x*Log[1+2*E^x*c/(a+Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]+PolyLog[2,-2*E^x*c/(a-Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]-PolyLog[2,-2*E^x*c/(a+Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]");
  }

  // 2.3 Exponential functions.input:651
  public void test0315() {
    check( //
        "Integrate[(-E^(c+d*x)*a*e+b*e)*x/(-2*E^(c+d*x)*a*e+b*e-E^(2*(c+d*x))*b*e), x]", //
        "1/2*x^2-1/2*x*Log[1+E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/d-1/2*x*Log[1+E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/d-1/2*PolyLog[2,-E^(c+d*x)*b/(a-Sqrt[a^2+b^2])]/d^2-1/2*PolyLog[2,-E^(c+d*x)*b/(a+Sqrt[a^2+b^2])]/d^2");
  }

  // 2.3 Exponential functions.input:714
  public void test0316() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)/(a+b*x+c*x^2)^2, x]", //
        "-E^(a+b*x+c*x^2)/(a+b*x+c*x^2)+ExpIntegralEi[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:722
  public void test0317() {
    check( //
        "Integrate[E^(a+b*x+c*x^2)*(b+2*c*x)/(a+b*x+c*x^2)^(5/2), x]", //
        "-2/3*E^(a+b*x+c*x^2)/(a+b*x+c*x^2)^(3/2)+4/3*Erfi[Sqrt[a+b*x+c*x^2]]*Sqrt[Pi]-4/3*E^(a+b*x+c*x^2)/Sqrt[a+b*x+c*x^2]");
  }

  // 2.3 Exponential functions.input:756
  public void test0318() {
    check( //
        "Integrate[x/Sqrt[-1+E^(2*x^2)], x]", //
        "1/2*ArcTan[Sqrt[-1+E^(2*x^2)]]");
  }

  // 2.3 Exponential functions.input:802
  public void test0319() {
    check( //
        "Integrate[E^(3*x)*(-x+x^2), x]", //
        "5/27*E^(3*x)-5/9*E^(3*x)*x+1/3*E^(3*x)*x^2");
  }

  // 2.3 Exponential functions.input:816
  public void test0320() {
    check( //
        "Integrate[1/Sqrt[-a+E^(c+d*x)*b], x]", //
        "2*ArcTan[Sqrt[-a+E^(c+d*x)*b]/Sqrt[a]]/(d*Sqrt[a])");
  }

  // 2.3 Exponential functions.input:824
  public void test0321() {
    check( //
        "Integrate[E^(7*x)*x^3, x]", //
        "-6/2401*E^(7*x)+6/343*E^(7*x)*x-3/49*E^(7*x)*x^2+1/7*E^(7*x)*x^3");
  }

  // 2.3 Exponential functions.input:840
  public void test0322() {
    check( //
        "Integrate[E^x*((-1)/E^x+E^x)^3, x]", //
        "1/2/E^(2*x)-3/2*E^(2*x)+1/4*E^(4*x)+3*x");
  }

  // 2.3 Exponential functions.input:858
  public void test0323() {
    check( //
        "Integrate[2^(1/x)/x^2, x]", //
        "-2^(1/x)/Log[2]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:24
  public void test0324() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d^3+3*d^2*e*x+3*d*e^2*x^2+e^3*x^3), x]", //
        "-6*e^3*F^(c*(a+b*x))/(b^4*c^4*Log[F]^4)+6*e^2*F^(c*(a+b*x))*(d+e*x)/(b^3*c^3*Log[F]^3)-3*e*F^(c*(a+b*x))*(d+e*x)^2/(b^2*c^2*Log[F]^2)+F^(c*(a+b*x))*(d+e*x)^3/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:50
  public void test0325() {
    check( //
        "Integrate[F^(a+b*x)/x^(7/2), x]", //
        "-2/5*F^(a+b*x)/x^(5/2)-4/15*b*F^(a+b*x)*Log[F]/x^(3/2)+8/15*b^(5/2)*F^a*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]]*Log[F]^(5/2)*Sqrt[Pi]-8/15*b^2*F^(a+b*x)*Log[F]^2/Sqrt[x]");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:58
  public void test0326() {
    check( //
        "Integrate[F^(c*(a+b*x))/(d+e*x)^(5/2), x]", //
        "-2/3*F^(c*(a+b*x))/(e*(d+e*x)^(3/2))+4/3*b^(3/2)*c^(3/2)*F^(c*(a-b*d/e))*Erfi[Sqrt[b]*Sqrt[c]*Sqrt[d+e*x]*Sqrt[Log[F]]/Sqrt[e]]*Log[F]^(3/2)*Sqrt[Pi]/e^(5/2)-4/3*b*c*F^(c*(a+b*x))*Log[F]/(e^2*Sqrt[d+e*x])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:70
  public void test0327() {
    check( //
        "Integrate[F^(c*(a+b*x))*(d+e*x+f*x^2+g*x^3), x]", //
        "-6*F^(c*(a+b*x))*g/(b^4*c^4*Log[F]^4)+2*f*F^(c*(a+b*x))/(b^3*c^3*Log[F]^3)+6*F^(c*(a+b*x))*g*x/(b^3*c^3*Log[F]^3)-e*F^(c*(a+b*x))/(b^2*c^2*Log[F]^2)-2*f*F^(c*(a+b*x))*x/(b^2*c^2*Log[F]^2)-3*F^(c*(a+b*x))*g*x^2/(b^2*c^2*Log[F]^2)+d*F^(c*(a+b*x))/(b*c*Log[F])+e*F^(c*(a+b*x))*x/(b*c*Log[F])+f*F^(c*(a+b*x))*x^2/(b*c*Log[F])+F^(c*(a+b*x))*g*x^3/(b*c*Log[F])");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:80
  public void test0328() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^3/x^2, x]", //
        "-E^(-a-b*x)*b-3*E^(-a-b*x)*a*b-E^(-a-b*x)*a^3/x-E^(-a-b*x)*b^2*x+3*a^2*b*ExpIntegralEi[-b*x]/E^a-a^3*b*ExpIntegralEi[-b*x]/E^a");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:98
  public void test0329() {
    check( //
        "Integrate[E^(-a-b*x)*(a+b*x)^4, x]", //
        "-24*E^(-a-b*x)/b-24*E^(-a-b*x)*(a+b*x)/b-12*E^(-a-b*x)*(a+b*x)^2/b-4*E^(-a-b*x)*(a+b*x)^3/b-E^(-a-b*x)*(a+b*x)^4/b");
  }

  // 2.1 u (F^(c (a+b x)))^n.input:118
  public void test0330() {
    check( //
        "Integrate[x*Sqrt[E^(a+b*x)], x]", //
        "-4*Sqrt[E^(a+b*x)]/b^2+2*x*Sqrt[E^(a+b*x)]/b");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:14
  public void test0331() {
    check( //
        "Integrate[x/(a+E^(c+d*x)*b), x]", //
        "1/2*x^2/a-x*Log[1+E^(c+d*x)*b/a]/(a*d)-PolyLog[2,-E^(c+d*x)*b/a]/(a*d^2)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:40
  public void test0332() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)*(c+d*x)^3, x]", //
        "1/4*a*(c+d*x)^4/d-6*b*d^3*(F^(e*g+f*g*x))^n/(f^4*g^4*n^4*Log[F]^4)+6*b*d^2*(F^(e*g+f*g*x))^n*(c+d*x)/(f^3*g^3*n^3*Log[F]^3)-3*b*d*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f^2*g^2*n^2*Log[F]^2)+b*(F^(e*g+f*g*x))^n*(c+d*x)^3/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:48
  public void test0333() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)^2, x]", //
        "1/3*a^2*(c+d*x)^3/d+4*a*b*d^2*(F^(e*g+f*g*x))^n/(f^3*g^3*n^3*Log[F]^3)+1/4*b^2*d^2*(F^(e*g+f*g*x))^(2*n)/(f^3*g^3*n^3*Log[F]^3)-4*a*b*d*(F^(e*g+f*g*x))^n*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)-1/2*b^2*d*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f^2*g^2*n^2*Log[F]^2)+2*a*b*(F^(e*g+f*g*x))^n*(c+d*x)^2/(f*g*n*Log[F])+1/2*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)^2/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:56
  public void test0334() {
    check( //
        "Integrate[(a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x), x]", //
        "1/2*a^3*(c+d*x)^2/d-3*a^2*b*d*(F^(e*g+f*g*x))^n/(f^2*g^2*n^2*Log[F]^2)-3/4*a*b^2*d*(F^(e*g+f*g*x))^(2*n)/(f^2*g^2*n^2*Log[F]^2)-1/9*b^3*d*(F^(e*g+f*g*x))^(3*n)/(f^2*g^2*n^2*Log[F]^2)+3*a^2*b*(F^(e*g+f*g*x))^n*(c+d*x)/(f*g*n*Log[F])+3/2*a*b^2*(F^(e*g+f*g*x))^(2*n)*(c+d*x)/(f*g*n*Log[F])+1/3*b^3*(F^(e*g+f*g*x))^(3*n)*(c+d*x)/(f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:66
  public void test0335() {
    check( //
        "Integrate[1/(a+b*(F^(g*(e+f*x)))^n), x]", //
        "x/a-Log[a+b*(F^(g*(e+f*x)))^n]/(a*f*g*n*Log[F])");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:108
  public void test0336() {
    check( //
        "Integrate[F^(c+d*x)*x^2/(a+b*F^(c+d*x)), x]", //
        "x^2*Log[1+b*F^(c+d*x)/a]/(b*d*Log[F])+2*x*PolyLog[2,-b*F^(c+d*x)/a]/(b*d^2*Log[F]^2)-2*PolyLog[3,-b*F^(c+d*x)/a]/(b*d^3*Log[F]^3)");
  }

  // 2.2 (c+d x)^m (F^(g (e+f x)))^n (a+b (F^(g (e+f x)))^n)^p.input:120
  public void test0337() {
    check( //
        "Integrate[F^(c+d*x)*x^2/(a+b*F^(c+d*x))^3, x]", //
        "-x/(a^2*b*d^2*Log[F]^2)+x/(a*b*d^2*(a+b*F^(c+d*x))*Log[F]^2)+1/2*x^2/(a^2*b*d*Log[F])-1/2*x^2/(b*d*(a+b*F^(c+d*x))^2*Log[F])+Log[a+b*F^(c+d*x)]/(a^2*b*d^3*Log[F]^3)-x*Log[1+b*F^(c+d*x)/a]/(a^2*b*d^2*Log[F]^2)-PolyLog[2,-b*F^(c+d*x)/a]/(a^2*b*d^3*Log[F]^3)");
  }

  // 2.3 Exponential functions.input:35
  public void test0338() {
    check( //
        "Integrate[E^(4*x)/(a+E^(2*x)*b), x]", //
        "1/2*E^(2*x)/b-1/2*a*Log[a+E^(2*x)*b]/b^2");
  }

  // 2.3 Exponential functions.input:43
  public void test0339() {
    check( //
        "Integrate[1/(E^(n*x)*(a+E^(n*x)*b)), x]", //
        "(-1)/(E^(n*x)*a*n)-b*x/a^2+b*Log[a+E^(n*x)*b]/(a^2*n)");
  }

  // 2.3 Exponential functions.input:69
  public void test0340() {
    check( //
        "Integrate[1/(b/f^x+a*f^x), x]", //
        "ArcTan[f^x*Sqrt[a]/Sqrt[b]]/(Log[f]*Sqrt[a]*Sqrt[b])");
  }

  // 2.3 Exponential functions.input:94
  public void test0341() {
    check( //
        "Integrate[f^(a+b*x^2)*x^5, x]", //
        "f^(a+b*x^2)/(b^3*Log[f]^3)-f^(a+b*x^2)*x^2/(b^2*Log[f]^2)+1/2*f^(a+b*x^2)*x^4/(b*Log[f])");
  }

  // 2.3 Exponential functions.input:126
  public void test0342() {
    check( //
        "Integrate[f^(a+b*x^3)/x^10, x]", //
        "-1/9*f^(a+b*x^3)/x^9-1/18*b*f^(a+b*x^3)*Log[f]/x^6-1/18*b^2*f^(a+b*x^3)*Log[f]^2/x^3+1/18*b^3*f^a*ExpIntegralEi[b*x^3*Log[f]]*Log[f]^3");
  }

  // 2.3 Exponential functions.input:160
  public void test0343() {
    check( //
        "Integrate[f^(a+b/x^2)/x^7, x]", //
        "-f^(a+b/x^2)/(b^3*Log[f]^3)+f^(a+b/x^2)/(b^2*x^2*Log[f]^2)-1/2*f^(a+b/x^2)/(b*x^4*Log[f])");
  }

  // 2.3 Exponential functions.input:168
  public void test0344() {
    check( //
        "Integrate[f^(a+b/x^2)*x^2, x]", //
        "1/3*f^(a+b/x^2)*x^3+2/3*b*f^(a+b/x^2)*x*Log[f]-2/3*b^(3/2)*f^a*Erfi[Sqrt[b]*Sqrt[Log[f]]/x]*Log[f]^(3/2)*Sqrt[Pi]");
  }

  // 2.3 Exponential functions.input:218
  public void test0345() {
    check( //
        "Integrate[f^(a+b*x^n)*x^(-1-3/2*n), x]", //
        "-2/3*f^(a+b*x^n)/(n*x^(3/2*n))-4/3*b*f^(a+b*x^n)*Log[f]/(n*x^(1/2*n))+4/3*b^(3/2)*f^a*Erfi[x^(1/2*n)*Sqrt[b]*Sqrt[Log[f]]]*Log[f]^(3/2)*Sqrt[Pi]/n");
  }

  // 2.3 Exponential functions.input:250
  public void test0346() {
    check( //
        "Integrate[f^(c/(a+b*x))*x^3, x]", //
        "-a^3*f^(c/(a+b*x))*(a+b*x)/b^4+3/2*a^2*f^(c/(a+b*x))*(a+b*x)^2/b^4-a*f^(c/(a+b*x))*(a+b*x)^3/b^4+3/2*a^2*c*f^(c/(a+b*x))*(a+b*x)*Log[f]/b^4-1/2*a*c*f^(c/(a+b*x))*(a+b*x)^2*Log[f]/b^4+a^3*c*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]/b^4-1/2*a*c^2*f^(c/(a+b*x))*(a+b*x)*Log[f]^2/b^4-3/2*a^2*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^2/b^4+1/2*a*c^3*ExpIntegralEi[c*Log[f]/(a+b*x)]*Log[f]^3/b^4+c^4*Gamma[-4,-c*Log[f]/(a+b*x)]*Log[f]^4/b^4");
  }

  // 2.3 Exponential functions.input:258
  public void test0347() {
    check( //
        "Integrate[f^(c/(a+b*x)^2)*x^3, x]", //
        "-a^3*f^(c/(a+b*x)^2)*(a+b*x)/b^4+3/2*a^2*f^(c/(a+b*x)^2)*(a+b*x)^2/b^4-a*f^(c/(a+b*x)^2)*(a+b*x)^3/b^4+1/4*f^(c/(a+b*x)^2)*(a+b*x)^4/b^4-2*a*c*f^(c/(a+b*x)^2)*(a+b*x)*Log[f]/b^4+1/4*c*f^(c/(a+b*x)^2)*(a+b*x)^2*Log[f]/b^4-3/2*a^2*c*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]/b^4-1/4*c^2*ExpIntegralEi[c*Log[f]/(a+b*x)^2]*Log[f]^2/b^4+2*a*c^(3/2)*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Log[f]^(3/2)*Sqrt[Pi]/b^4+a^3*Erfi[Sqrt[c]*Sqrt[Log[f]]/(a+b*x)]*Sqrt[Pi]*Sqrt[c]*Sqrt[Log[f]]/b^4");
  }

  // 2.3 Exponential functions.input:305
  public void test0348() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)/(c+d*x)^7, x]", //
        "-1/6*F^(a+b*(c+d*x)^2)/(d*(c+d*x)^6)-1/12*b*F^(a+b*(c+d*x)^2)*Log[F]/(d*(c+d*x)^4)-1/12*b^2*F^(a+b*(c+d*x)^2)*Log[F]^2/(d*(c+d*x)^2)+1/12*b^3*F^a*ExpIntegralEi[b*(c+d*x)^2*Log[F]]*Log[F]^3/d");
  }

  // 2.3 Exponential functions.input:347
  public void test0349() {
    check( //
        "Integrate[F^(a+b/(c+d*x))*(c+d*x)^2, x]", //
        "1/3*F^(a+b/(c+d*x))*(c+d*x)^3/d+1/6*b*F^(a+b/(c+d*x))*(c+d*x)^2*Log[F]/d+1/6*b^2*F^(a+b/(c+d*x))*(c+d*x)*Log[F]^2/d-1/6*b^3*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)]*Log[F]^3/d");
  }

  // 2.3 Exponential functions.input:379
  public void test0350() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^2)/(c+d*x)^8, x]", //
        "-15/8*F^(a+b/(c+d*x)^2)/(b^3*d*(c+d*x)*Log[F]^3)+5/4*F^(a+b/(c+d*x)^2)/(b^2*d*(c+d*x)^3*Log[F]^2)-1/2*F^(a+b/(c+d*x)^2)/(b*d*(c+d*x)^5*Log[F])+15/16*F^a*Erfi[Sqrt[b]*Sqrt[Log[F]]/(c+d*x)]*Sqrt[Pi]/(b^(7/2)*d*Log[F]^(7/2))");
  }

  // 2.3 Exponential functions.input:387
  public void test0351() {
    check( //
        "Integrate[F^(a+b/(c+d*x)^3)*(c+d*x)^5, x]", //
        "1/6*F^(a+b/(c+d*x)^3)*(c+d*x)^6/d+1/6*b*F^(a+b/(c+d*x)^3)*(c+d*x)^3*Log[F]/d-1/6*b^2*F^a*ExpIntegralEi[b*Log[F]/(c+d*x)^3]*Log[F]^2/d");
  }

  // 2.3 Exponential functions.input:421
  public void test0352() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-2*n), x]", //
        "-1/2*F^(a+b*(c+d*x)^n)/(d*n*(c+d*x)^(2*n))-1/2*b*F^(a+b*(c+d*x)^n)*Log[F]/(d*n*(c+d*x)^n)+1/2*b^2*F^a*ExpIntegralEi[b*(c+d*x)^n*Log[F]]*Log[F]^2/(d*n)");
  }

  // 2.3 Exponential functions.input:433
  public void test0353() {
    check( //
        "Integrate[F^(a+b*(c+d*x)^2)*(e+f*x)^3, x]", //
        "-1/2*f^3*F^(a+b*(c+d*x)^2)/(b^2*d^4*Log[F]^2)+3/2*f*(d*e-c*f)^2*F^(a+b*(c+d*x)^2)/(b*d^4*Log[F])+3/2*f^2*(d*e-c*f)*F^(a+b*(c+d*x)^2)*(c+d*x)/(b*d^4*Log[F])+1/2*f^3*F^(a+b*(c+d*x)^2)*(c+d*x)^2/(b*d^4*Log[F])-3/4*f^2*(d*e-c*f)*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(b^(3/2)*d^4*Log[F]^(3/2))+1/2*(d*e-c*f)^3*F^a*Erfi[(c+d*x)*Sqrt[b]*Sqrt[Log[F]]]*Sqrt[Pi]/(d^4*Sqrt[b]*Sqrt[Log[F]])");
  }

  // 2.3 Exponential functions.input:448
  public void test0354() {
    check( //
        "Integrate[F^(a+b/(c+d*x))/(e+f*x), x]", //
        "-F^a*ExpIntegralEi[b*Log[F]/(c+d*x)]/f+F^(a-b*f/(d*e-c*f))*ExpIntegralEi[b*d*(e+f*x)*Log[F]/((d*e-c*f)*(c+d*x))]/f");
  }

  // 2.3 Exponential functions.input:467
  public void test0355() {
    check( //
        "Integrate[E^(e/(c+d*x)^3)*(a+b*x)^3, x]", //
        "-E^(e/(c+d*x)^3)*b^2*(b*c-a*d)*(c+d*x)^3/d^4+b^2*(b*c-a*d)*e*ExpIntegralEi[e/(c+d*x)^3]/d^4+1/3*b^3*(-e/(c+d*x)^3)^(4/3)*(c+d*x)^4*Gamma[-4/3,-e/(c+d*x)^3]/d^4+b*(b*c-a*d)^2*(-e/(c+d*x)^3)^(2/3)*(c+d*x)^2*Gamma[-2/3,-e/(c+d*x)^3]/d^4-1/3*(b*c-a*d)^3*(-e/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,-e/(c+d*x)^3]/d^4");
  }

  // 2.3 Exponential functions.input:481
  public void test0356() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*x^3, x]", //
        "-1/2*f^(a+b*x+c*x^2)/(c^2*Log[f]^2)+1/8*b^2*f^(a+b*x+c*x^2)/(c^3*Log[f])-1/4*b*f^(a+b*x+c*x^2)*x/(c^2*Log[f])+1/2*f^(a+b*x+c*x^2)*x^2/(c*Log[f])+3/8*b*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(5/2)*Log[f]^(3/2))-1/16*b^3*f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]/(c^(7/2)*Sqrt[Log[f]])");
  }

  // 2.3 Exponential functions.input:508
  public void test0357() {
    check( //
        "Integrate[f^(a+b*x+c*x^2)*(b+2*c*x)^2, x]", //
        "f^(a+b*x+c*x^2)*(b+2*c*x)/Log[f]-f^(a-1/4*b^2/c)*Erfi[1/2*(b+2*c*x)*Sqrt[Log[f]]/Sqrt[c]]*Sqrt[Pi]*Sqrt[c]/Log[f]^(3/2)");
  }

  // 2.3 Exponential functions.input:526
  public void test0358() {
    check( //
        "Integrate[E^(d+e*x)/(x^2*(a+b*x+c*x^2)), x]", //
        "-E^(d+e*x)/(a*x)-E^d*b*ExpIntegralEi[e*x]/a^2+E^d*e*ExpIntegralEi[e*x]/a+1/2*E^(d-1/2*e*(b+Sqrt[b^2-4*a*c])/c)*ExpIntegralEi[1/2*e*(b+2*c*x+Sqrt[b^2-4*a*c])/c]*(b+(-b^2+2*a*c)/Sqrt[b^2-4*a*c])/a^2+1/2*E^(d-1/2*e*(b-Sqrt[b^2-4*a*c])/c)*ExpIntegralEi[1/2*e*(b+2*c*x-Sqrt[b^2-4*a*c])/c]*(b+(b^2-2*a*c)/Sqrt[b^2-4*a*c])/a^2");
  }

  // 2.3 Exponential functions.input:536
  public void test0359() {
    check( //
        "Integrate[4^x/(a-2^x*b), x]", //
        "-2^x/(b*Log[2])-a*Log[a-2^x*b]/(b^2*Log[2])");
  }

  // 2.3 Exponential functions.input:606
  public void test0360() {
    check( //
        "Integrate[x^2/(a+b/E^x+E^x*c), x]", //
        "x^2*Log[1+2*E^x*c/(a-Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]-x^2*Log[1+2*E^x*c/(a+Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]+2*x*PolyLog[2,-2*E^x*c/(a-Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]-2*x*PolyLog[2,-2*E^x*c/(a+Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]-2*PolyLog[3,-2*E^x*c/(a-Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]+2*PolyLog[3,-2*E^x*c/(a+Sqrt[a^2-4*b*c])]/Sqrt[a^2-4*b*c]");
  }

  // 2.3 Exponential functions.input:620
  public void test0361() {
    check( //
        "Integrate[(a+b*F^(c*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]))^3/(d^2-e^2*x^2), x]", //
        "3*a^2*b*ExpIntegralEi[c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+3*a*b^2*ExpIntegralEi[2*c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+b^3*ExpIntegralEi[3*c*Log[F]*Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)+a^3*Log[Sqrt[d+e*x]/Sqrt[d*f-e*f*x]]/(d*e)");
  }

  // 2.3 Exponential functions.input:640
  public void test0362() {
    check( //
        "Integrate[a^x*b^x*c^x, x]", //
        "a^x*b^x*c^x/(Log[a]+Log[b]+Log[c])");
  }

  // 2.3 Exponential functions.input:665
  public void test0363() {
    check( //
        "Integrate[E^(Log[(d+e*x)^n]^2)*(d+e*x)^m, x]", //
        "1/2*(d+e*x)^(1+m)*Erfi[1/2*(1+m+2*n*Log[(d+e*x)^n])/n]*Sqrt[Pi]/(E^(1/4*(1+m)^2/n^2)*e*n*((d+e*x)^n)^((1+m)/n))");
  }

  // 2.3 Exponential functions.input:791
  public void test0364() {
    check( //
        "Integrate[E^x*Sec[1-E^x]^3, x]", //
        "-1/2*ArcTanh[Sin[1-E^x]]-1/2*Sec[1-E^x]*Tan[1-E^x]");
  }

  // 2.3 Exponential functions.input:833
  public void test0365() {
    check( //
        "Integrate[E^(E^(E^x)+E^x+x), x]", //
        "E^(E^(E^x))");
  }
}
