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
public class TrigFunctions4 extends AbstractRubiTestCase {
  static boolean init = true;

  public TrigFunctions4(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("TrigFunctions4");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:387
  public void test0001() {
    check( //
        "Integrate[(e+f*x)^2*(a+b*Sin[c+d/x]), x]", //
        "a*e^2*x+a*e*f*x^2+1/3*a*f^2*x^3-b*d*e^2*CosIntegral[d/x]*Cos[c]+1/6*b*d^3*f^2*CosIntegral[d/x]*Cos[c]+b*d*e*f*x*Cos[c+d/x]+1/6*b*d*f^2*x^2*Cos[c+d/x]+b*d^2*e*f*Cos[c]*SinIntegral[d/x]+b*d^2*e*f*CosIntegral[d/x]*Sin[c]+b*d*e^2*SinIntegral[d/x]*Sin[c]-1/6*b*d^3*f^2*SinIntegral[d/x]*Sin[c]+b*e^2*x*Sin[c+d/x]-1/6*b*d^2*f^2*x*Sin[c+d/x]+b*e*f*x^2*Sin[c+d/x]+1/3*b*f^2*x^3*Sin[c+d/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:426
  public void test0002() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(1/3)/x, x]", //
        "Cos[a]*Csc[a+b*x]*SinIntegral[b*x]*(c*Sin[a+b*x]^3)^(1/3)+CosIntegral[b*x]*Csc[a+b*x]*Sin[a]*(c*Sin[a+b*x]^3)^(1/3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:446
  public void test0003() {
    check( //
        "Integrate[(c*Sin[a+b*x^n]^3)^(1/3)/x, x]", //
        "Cos[a]*Csc[a+b*x^n]*SinIntegral[b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/n+CosIntegral[b*x^n]*Csc[a+b*x^n]*Sin[a]*(c*Sin[a+b*x^n]^3)^(1/3)/n");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:466
  public void test0004() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(2/3)/x, x]", //
        "-1/4*CosIntegral[2*b*x^2]*Cos[2*a]*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)+1/2*Csc[a+b*x^2]^2*Log[x]*(c*Sin[a+b*x^2]^3)^(2/3)+1/4*Csc[a+b*x^2]^2*SinIntegral[2*b*x^2]*Sin[2*a]*(c*Sin[a+b*x^2]^3)^(2/3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:25
  public void test0005() {
    check( //
        "Integrate[Sin[x]^4/(a+a*Sin[x])^2, x]", //
        "7/2*x/a^2+16/3*Cos[x]/a^2-7/2*Cos[x]*Sin[x]/a^2+8/3*Cos[x]*Sin[x]^2/(a^2*(1+Sin[x]))+1/3*Cos[x]*Sin[x]^3/(a+a*Sin[x])^2");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:42
  public void test0006() {
    check( //
        "Integrate[Csc[x]^2/(a+a*Sin[x])^3, x]", //
        "3*ArcTanh[Cos[x]]/a^3-24/5*Cot[x]/a^3+1/5*Cot[x]/(a+a*Sin[x])^3+3/5*Cot[x]/(a*(a+a*Sin[x])^2)+3*Cot[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:62
  public void test0007() {
    check( //
        "Integrate[Sin[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2), x]", //
        "4/35*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/7*Cos[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/(a*d)-152/105*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-38/105*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:80
  public void test0008() {
    check( //
        "Integrate[Sin[c+d*x]^3/Sqrt[a+a*Sin[c+d*x]], x]", //
        "ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-28/15*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/5*Cos[c+d*x]*Sin[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+2/15*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:96
  public void test0009() {
    check( //
        "Integrate[Sin[c+d*x]^4/(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/4*Cos[c+d*x]*Sin[c+d*x]^3/(d*(a+a*Sin[c+d*x])^(5/2))+17/16*Cos[c+d*x]*Sin[c+d*x]^2/(a*d*(a+a*Sin[c+d*x])^(3/2))-163/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+197/24*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-95/48*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:246
  public void test0010() {
    check( //
        "Integrate[Csc[x]^3/(a+b*Sin[x])^3, x]", //
        "-b^3*(20*a^4-29*a^2*b^2+12*b^4)*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^5*(a^2-b^2)^(5/2))-1/2*(a^2+12*b^2)*ArcTanh[Cos[x]]/a^5+3/2*b*(2*a^4-7*a^2*b^2+4*b^4)*Cot[x]/(a^4*(a^2-b^2)^2)-1/2*(a^4-10*a^2*b^2+6*b^4)*Cot[x]*Csc[x]/(a^3*(a^2-b^2)^2)-1/2*b^2*Cot[x]*Csc[x]/(a*(a^2-b^2)*(a+b*Sin[x])^2)-1/2*b^2*(7*a^2-4*b^2)*Cot[x]*Csc[x]/(a^2*(a^2-b^2)^2*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:303
  public void test0011() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^5, x]", //
        "9/16*a^2*c^5*x+3/10*a^2*c^5*Cos[e+f*x]^5/f+9/16*a^2*c^5*Cos[e+f*x]*Sin[e+f*x]/f+3/8*a^2*c^5*Cos[e+f*x]^3*Sin[e+f*x]/f+1/7*a^2*c^3*Cos[e+f*x]^5*(c-c*Sin[e+f*x])^2/f+3/14*a^2*Cos[e+f*x]^5*(c^5-c^5*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:319
  public void test0012() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x]), x]", //
        "5/8*a^3*c*x-5/12*a^3*c*Cos[e+f*x]^3/f+5/8*a^3*c*Cos[e+f*x]*Sin[e+f*x]/f-1/4*c*Cos[e+f*x]^3*(a^3+a^3*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:337
  public void test0013() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^4), x]", //
        "1/7*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^3)+4/35*Sec[e+f*x]/(a*f*(c^2-c^2*Sin[e+f*x])^2)+4/35*Sec[e+f*x]/(a*f*(c^4-c^4*Sin[e+f*x]))+8/35*Tan[e+f*x]/(a*c^4*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:353
  public void test0014() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])), x]", //
        "-1/5*Sec[e+f*x]/(a*c*f*(a+a*Sin[e+f*x])^2)-1/5*Sec[e+f*x]/(c*f*(a^3+a^3*Sin[e+f*x]))+2/5*Tan[e+f*x]/(a^3*c*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:373
  public void test0015() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/35*a^2*c^4*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+2/7*a^2*c^3*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:389
  public void test0016() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/5*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(15/2))-1/8*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(11/2))+1/16*a^3*Cos[e+f*x]/(c^2*f*(c-c*Sin[e+f*x])^(7/2))-1/128*a^3*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(5/2))-3/512*a^3*Cos[e+f*x]/(c^4*f*(c-c*Sin[e+f*x])^(3/2))-3/512*a^3*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(11/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:407
  public void test0017() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-4096/15*c^2*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*f)+1024/3*c*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(7/2)/(a^3*f)-128*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(9/2)/(a^3*f)+32/3*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(11/2)/(a^3*c*f)+2/3*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(13/2)/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:443
  public void test0018() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*(c-c*Sin[e+f*x])^(5/2))-a^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*(c-c*Sin[e+f*x])^(3/2))-a^3*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:459
  public void test0019() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/12*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(13/2))+1/60*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(11/2))+1/480*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c^2*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:477
  public void test0020() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "2*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/2*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*(a+a*Sin[e+f*x])^(5/2))+3*c^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])+24*c^5*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+12*c^4*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:538
  public void test0021() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3, x]", //
        "5/2*a^3*x-4*a^3*Cos[e+f*x]/f+1/3*a^3*Cos[e+f*x]^3/f-3/2*a^3*Cos[e+f*x]*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:556
  public void test0022() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^2, x]", //
        "(3*c-2*d)*d^2*x/a^2+1/3*(c-4*d)*d^2*Cos[e+f*x]/(a^2*f)-1/3*(c-d)^2*(c+6*d)*Cos[e+f*x]/(a^2*f*(1+Sin[e+f*x]))-1/3*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:630
  public void test0023() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^2, x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/((c+d)^(3/2)*f*Sqrt[d])-a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:648
  public void test0024() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-(c-d)^3*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-4/15*d*(21*c^2-12*c*d+7*d^2)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/5*d*Cos[e+f*x]*(c+d*Sin[e+f*x])^2/(f*Sqrt[a+a*Sin[e+f*x]])-2/15*(9*c-d)*d^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:664
  public void test0025() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/4*(c-d)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c+5*d)*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/16*(3*c+5*d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:684
  public void test0026() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^(3/2), x]", //
        "-2*a^(3/2)*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]/(d^(3/2)*f)+2*a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:702
  public void test0027() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(1/2)), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a]*Sqrt[c-d])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:801
  public void test0028() {
    check( //
        "Integrate[(a+b*Sin[e+f*x])^2/(c+d*Sin[e+f*x]), x]", //
        "-b*(b*c-2*a*d)*x/d^2-b^2*Cos[e+f*x]/(d*f)+2*(b*c-a*d)^2*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(d^2*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:19
  public void test0029() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]], x]", //
        "-1/3*a*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])-1/3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:35
  public void test0030() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "-1/7*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/(c*f)-1/7*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(7/2)/(c*f)-2/35*a^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(c*f*Sqrt[a+a*Sin[e+f*x]])-4/35*a^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:51
  public void test0031() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(3/2), x]", //
        "-2*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])-2/3*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])-1/4*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])-16*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-8*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:69
  public void test0032() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^(3/2), x]", //
        "1/2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*Sqrt[a+a*Sin[e+f*x]])+4*c^2*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*c*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:95
  public void test0033() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^m/Sqrt[c-c*Sin[e+f*x]], x]", //
        "2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(1+m)/(a*f*(3+2*m)*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:129
  public void test0034() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(3/2), x]", //
        "-2/9*a^2*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g*Sqrt[a+a*Sin[e+f*x]])-2/9*a*(g*Cos[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g)+14/45*a^2*c^2*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+14/15*a^2*c^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2/15*a^2*c*(g*Cos[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:145
  public void test0035() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(11/2), x]", //
        "4/17*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*(c-c*Sin[e+f*x])^(11/2))+308/1989*a^3*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])-154/3315*a^3*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-154/3315*a^3*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-44/221*a^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f*g*(c-c*Sin[e+f*x])^(9/2))+154/3315*a^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^5*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:163
  public void test0036() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]]), x]", //
        "2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:179
  public void test0037() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]]/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-4/5*c*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])+6/5*c*(g*Cos[e+f*x])^(5/2)/(a*f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])+6/5*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:243
  public void test0038() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "a*Log[Sin[c+d*x]]/d+a*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:343
  public void test0039() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "3/16*a^2*x-1/10*a^2*Cos[c+d*x]^5/d+3/16*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/8*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:361
  public void test0040() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^4, x]", //
        "21/16*a^4*x-7/8*a^4*Cos[c+d*x]^3/d+21/16*a^4*Cos[c+d*x]*Sin[c+d*x]/d-1/6*a*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^3/d-3/10*Cos[c+d*x]^3*(a^2+a^2*Sin[c+d*x])^2/d-21/40*Cos[c+d*x]^3*(a^4+a^4*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:379
  public void test0041() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "2*x/a^2+Cos[c+d*x]/(a^2*d)+2*Cos[c+d*x]/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:399
  public void test0042() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+3*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:417
  public void test0043() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4/Sqrt[a+a*Sin[c+d*x]], x]", //
        "1/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])+1/8*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+1/12*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:441
  public void test0044() {
    check( //
        "Integrate[Cos[c+d*x]^3*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "1/2*Sin[c+d*x]^2/(a*d)-1/3*Sin[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:465
  public void test0045() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+a*Sin[c+d*x]), x]", //
        "-3/8*a*ArcTanh[Cos[c+d*x]]/d-1/5*a*Cot[c+d*x]^5/d+3/8*a*Cot[c+d*x]*Csc[c+d*x]/d-1/4*a*Cot[c+d*x]^3*Csc[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:481
  public void test0046() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^10*(a+a*Sin[c+d*x])^2, x]", //
        "-3/64*a^2*ArcTanh[Cos[c+d*x]]/d-2/5*a^2*Cot[c+d*x]^5/d-3/7*a^2*Cot[c+d*x]^7/d-1/9*a^2*Cot[c+d*x]^9/d-3/64*a^2*Cot[c+d*x]*Csc[c+d*x]/d-1/32*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+1/8*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d-1/4*a^2*Cot[c+d*x]^3*Csc[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:497
  public void test0047() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^11*(a+a*Sin[c+d*x])^3, x]", //
        "-21/256*a^3*ArcTanh[Cos[c+d*x]]/d-4/5*a^3*Cot[c+d*x]^5/d-a^3*Cot[c+d*x]^7/d-1/3*a^3*Cot[c+d*x]^9/d-21/256*a^3*Cot[c+d*x]*Csc[c+d*x]/d-7/128*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d+29/160*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d-3/8*a^3*Cot[c+d*x]^3*Csc[c+d*x]^5/d+3/80*a^3*Cot[c+d*x]*Csc[c+d*x]^7/d-1/10*a^3*Cot[c+d*x]^3*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:515
  public void test0048() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "-3/4*x/a^2-2*Cos[c+d*x]/(a^2*d)+Cos[c+d*x]^3/(a^2*d)-1/5*Cos[c+d*x]^5/(a^2*d)+3/4*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+1/2*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:552
  public void test0049() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d+16/105*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-14/45*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-34/63*a^2*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/9*a^2*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+388/315*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:570
  public void test0050() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-9/128*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(d*Sqrt[a])-9/128*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-3/64*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+29/80*Cot[c+d*x]*Csc[c+d*x]^2/(d*Sqrt[a+a*Sin[c+d*x]])+1/40*Cot[c+d*x]*Csc[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-1/5*Cot[c+d*x]*Csc[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:586
  public void test0051() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-23/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)+4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)+9/4*Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:614
  public void test0052() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^9*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cot[c+d*x]^6/d-1/8*a*Cot[c+d*x]^8/d-1/3*a*Csc[c+d*x]^3/d+2/5*a*Csc[c+d*x]^5/d-1/7*a*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:692
  public void test0053() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "3/256*a*x-1/7*a*Cos[c+d*x]^7/d+1/9*a*Cos[c+d*x]^9/d+3/256*a*Cos[c+d*x]*Sin[c+d*x]/d+1/128*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/160*a*Cos[c+d*x]^5*Sin[c+d*x]/d-3/80*a*Cos[c+d*x]^7*Sin[c+d*x]/d-1/10*a*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:708
  public void test0054() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^3*(a+a*Sin[c+d*x])^2, x]", //
        "3/128*a^2*x-2/7*a^2*Cos[c+d*x]^7/d+1/3*a^2*Cos[c+d*x]^9/d-1/11*a^2*Cos[c+d*x]^11/d+3/128*a^2*Cos[c+d*x]*Sin[c+d*x]/d+1/64*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+1/80*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-3/40*a^2*Cos[c+d*x]^7*Sin[c+d*x]/d-1/5*a^2*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:724
  public void test0055() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "27/1024*a^3*x-4/7*a^3*Cos[c+d*x]^7/d+a^3*Cos[c+d*x]^9/d-6/11*a^3*Cos[c+d*x]^11/d+1/13*a^3*Cos[c+d*x]^13/d+27/1024*a^3*Cos[c+d*x]*Sin[c+d*x]/d+9/512*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d+9/640*a^3*Cos[c+d*x]^5*Sin[c+d*x]/d-27/320*a^3*Cos[c+d*x]^7*Sin[c+d*x]/d-9/40*a^3*Cos[c+d*x]^7*Sin[c+d*x]^3/d-1/4*a^3*Cos[c+d*x]^7*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:740
  public void test0056() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^13*(a+a*Sin[c+d*x])^3, x]", //
        "41/1024*a^3*ArcTanh[Cos[c+d*x]]/d-4/7*a^3*Cot[c+d*x]^7/d-7/9*a^3*Cot[c+d*x]^9/d-3/11*a^3*Cot[c+d*x]^11/d+41/1024*a^3*Cot[c+d*x]*Csc[c+d*x]/d+41/1536*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-35/384*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d+3/16*a^3*Cot[c+d*x]^3*Csc[c+d*x]^5/d-3/10*a^3*Cot[c+d*x]^5*Csc[c+d*x]^5/d-1/64*a^3*Cot[c+d*x]*Csc[c+d*x]^7/d+1/24*a^3*Cot[c+d*x]^3*Csc[c+d*x]^7/d-1/12*a^3*Cot[c+d*x]^5*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:790
  public void test0057() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^5*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cos[c+d*x]^8/d+1/5*a*Cos[c+d*x]^10/d-1/12*a*Cos[c+d*x]^12/d+1/7*a*Sin[c+d*x]^7/d-1/3*a*Sin[c+d*x]^9/d+3/11*a*Sin[c+d*x]^11/d-1/13*a*Sin[c+d*x]^13/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:806
  public void test0058() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^12*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cot[c+d*x]^8/d-1/10*a*Cot[c+d*x]^10/d+1/5*a*Csc[c+d*x]^5/d-3/7*a*Csc[c+d*x]^7/d+1/3*a*Csc[c+d*x]^9/d-1/11*a*Csc[c+d*x]^11/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:854
  public void test0059() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/256*x/a-1/7*Cos[c+d*x]^7/(a*d)+1/9*Cos[c+d*x]^9/(a*d)-3/256*Cos[c+d*x]*Sin[c+d*x]/(a*d)-1/128*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)-1/160*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)+3/80*Cos[c+d*x]^7*Sin[c+d*x]/(a*d)+1/10*Cos[c+d*x]^7*Sin[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:870
  public void test0060() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^2, x]", //
        "9/256*x/a^2+2/5*Cos[c+d*x]^5/(a^2*d)-4/7*Cos[c+d*x]^7/(a^2*d)+2/9*Cos[c+d*x]^9/(a^2*d)+9/256*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+3/128*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)-3/32*Cos[c+d*x]^5*Sin[c+d*x]/(a^2*d)-3/16*Cos[c+d*x]^5*Sin[c+d*x]^3/(a^2*d)-1/10*Cos[c+d*x]^5*Sin[c+d*x]^5/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:886
  public void test0061() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "-29/128*x/a^3-4/3*Cos[c+d*x]^3/(a^3*d)+7/5*Cos[c+d*x]^5/(a^3*d)-3/7*Cos[c+d*x]^7/(a^3*d)-29/128*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+29/64*Cos[c+d*x]^3*Sin[c+d*x]/(a^3*d)+29/48*Cos[c+d*x]^3*Sin[c+d*x]^3/(a^3*d)+1/8*Cos[c+d*x]^3*Sin[c+d*x]^5/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1114
  public void test0062() {
    check( //
        "Integrate[Sec[c+d*x]^7*Sin[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "3/128*ArcTanh[Sin[c+d*x]]/(a*d)+3/128*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/64*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)-1/16*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)+1/8*Sec[c+d*x]^5*Tan[c+d*x]^3/(a*d)-1/6*Tan[c+d*x]^6/(a*d)-1/8*Tan[c+d*x]^8/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1148
  public void test0063() {
    check( //
        "Integrate[Sec[c+d*x]^9*Sin[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "-3/256*ArcTanh[Sin[c+d*x]]/(a*d)-3/256*Sec[c+d*x]*Tan[c+d*x]/(a*d)-1/128*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+1/32*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)-1/16*Sec[c+d*x]^5*Tan[c+d*x]^3/(a*d)+1/10*Sec[c+d*x]^5*Tan[c+d*x]^5/(a*d)-1/8*Tan[c+d*x]^8/(a*d)-1/10*Tan[c+d*x]^10/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1177
  public void test0064() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^m*(c+d*Sin[e+f*x])^2, x]", //
        "(c-d)^2*(a+a*Sin[e+f*x])^(1+m)/(a*f*(1+m))+2*(c-d)*d*(a+a*Sin[e+f*x])^(2+m)/(a^2*f*(2+m))+d^2*(a+a*Sin[e+f*x])^(3+m)/(a^3*f*(3+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1236
  public void test0065() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "5/128*a*(8*A+B)*x-1/56*a*(8*A+B)*Cos[c+d*x]^7/d+5/128*a*(8*A+B)*Cos[c+d*x]*Sin[c+d*x]/d+5/192*a*(8*A+B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/48*a*(8*A+B)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*B*Cos[c+d*x]^7*(a+a*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1252
  public void test0066() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "5/128*a^2*(9*A+2*B)*x-1/56*a^2*(9*A+2*B)*Cos[c+d*x]^7/d+5/128*a^2*(9*A+2*B)*Cos[c+d*x]*Sin[c+d*x]/d+5/192*a^2*(9*A+2*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/48*a^2*(9*A+2*B)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/9*B*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^2/d-1/72*(9*A+2*B)*Cos[c+d*x]^7*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1410
  public void test0067() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]*(a+b*Sin[c+d*x]), x]", //
        "1/16*b*x-1/5*a*Cos[c+d*x]^5/d+1/16*b*Cos[c+d*x]*Sin[c+d*x]/d+1/24*b*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*b*Cos[c+d*x]^5*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1427
  public void test0068() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^2, x]", //
        "2*a*b*x-3/8*(a^2-4*b^2)*ArcTanh[Cos[c+d*x]]/d-1/24*b^2*(39*a^2+2*b^2)*Cos[c+d*x]/(a^2*d)+17/12*a*b*Cot[c+d*x]/d+5/8*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^2/d+1/12*b*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^3/(a^2*d)-1/4*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1445
  public void test0069() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/(a+b*Sin[c+d*x])^2, x]", //
        "1/8*(40*a^4-36*a^2*b^2+3*b^4)*x/b^6+1/3*a*(15*a^2-11*b^2)*Cos[c+d*x]/(b^5*d)-1/8*(20*a^2-13*b^2)*Cos[c+d*x]*Sin[c+d*x]/(b^4*d)+1/3*(5*a^2-3*b^2)*Cos[c+d*x]*Sin[c+d*x]^2/(a*b^3*d)-1/4*Cos[c+d*x]*Sin[c+d*x]^3/(b^2*d)-(a^2-b^2)*Cos[c+d*x]*Sin[c+d*x]^3/(a*b^2*d*(a+b*Sin[c+d*x]))-2*a*(5*a^4-7*a^2*b^2+2*b^4)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^6*d*Sqrt[a^2-b^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1536
  public void test0070() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^2*(a+b*Sin[c+d*x]), x]", //
        "-1/6*b*Cos[c+d*x]^6/d+1/8*b*Cos[c+d*x]^8/d+1/3*a*Sin[c+d*x]^3/d-2/5*a*Sin[c+d*x]^5/d+1/7*a*Sin[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1719
  public void test0071() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^4/(a+b*Sin[c+d*x]), x]", //
        "2*a^4*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)+a^2*b*Sec[c+d*x]/((a^2-b^2)^2*d)+b*Sec[c+d*x]/((a^2-b^2)*d)-1/3*b*Sec[c+d*x]^3/((a^2-b^2)*d)-a^3*Tan[c+d*x]/((a^2-b^2)^2*d)+1/3*a*Tan[c+d*x]^3/((a^2-b^2)*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:39
  public void test0072() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]/((c-c*Sin[e+f*x])*Sqrt[g*Sin[e+f*x]]), x]", //
        "2*Sec[e+f*x]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]/(c*f*g)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:104
  public void test0073() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]]/Sin[e+f*x], x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]*Sqrt[c]/f-2*ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]*Sqrt[d]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:49
  public void test0074() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^5, x]", //
        "9/128*a^2*(8*A-3*B)*c^5*x+3/80*a^2*(8*A-3*B)*c^5*Cos[e+f*x]^5/f+9/128*a^2*(8*A-3*B)*c^5*Cos[e+f*x]*Sin[e+f*x]/f+3/64*a^2*(8*A-3*B)*c^5*Cos[e+f*x]^3*Sin[e+f*x]/f+1/56*a^2*(8*A-3*B)*c^3*Cos[e+f*x]^5*(c-c*Sin[e+f*x])^2/f-1/8*a^2*B*c^2*Cos[e+f*x]^5*(c-c*Sin[e+f*x])^3/f+3/112*a^2*(8*A-3*B)*Cos[e+f*x]^5*(c^5-c^5*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:65
  public void test0075() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^2, x]", //
        "1/16*a^3*(6*A+B)*c^2*x-1/30*a^3*(6*A+B)*c^2*Cos[e+f*x]^5/f+1/16*a^3*(6*A+B)*c^2*Cos[e+f*x]*Sin[e+f*x]/f+1/24*a^3*(6*A+B)*c^2*Cos[e+f*x]^3*Sin[e+f*x]/f-1/6*B*c^2*Cos[e+f*x]^5*(a^3+a^3*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:83
  public void test0076() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^3), x]", //
        "1/5*(A+B)*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^2)+1/15*(3*A-2*B)*Sec[e+f*x]/(a*f*(c^3-c^3*Sin[e+f*x]))+2/15*(3*A-2*B)*Tan[e+f*x]/(a*c^3*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:99
  public void test0077() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])/(a+a*Sin[e+f*x])^3, x]", //
        "-2/5*(A-B)*c*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^3)+1/15*a*(A-11*B)*c*Cos[e+f*x]/(f*(a^2+a^2*Sin[e+f*x])^2)+1/15*(A+4*B)*c*Cos[e+f*x]/(f*(a^3+a^3*Sin[e+f*x]))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:119
  public void test0078() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(5/2), x]", //
        "64/3465*a^2*(11*A-B)*c^5*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+16/693*a^2*(11*A-B)*c^4*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))+2/99*a^2*(11*A-B)*c^3*Cos[e+f*x]^5/(f*Sqrt[c-c*Sin[e+f*x]])-2/11*a^2*B*c^2*Cos[e+f*x]^5*Sqrt[c-c*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:135
  public void test0079() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(9/2), x]", //
        "1/8*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(15/2))+1/48*a^3*(A-15*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(11/2))-5/192*a^3*(A-15*B)*Cos[e+f*x]^3/(c*f*(c-c*Sin[e+f*x])^(7/2))+5/128*a^3*(A-15*B)*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(3/2))-5/128*a^3*(A-15*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(9/2)*f*Sqrt[2])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:153
  public void test0080() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "5/64*(7*A-B)*Cos[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2))+1/24*(7*A-B)*Sec[e+f*x]/(a^2*c*f*(c-c*Sin[e+f*x])^(3/2))+5/64*(7*A-B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(a^2*c^(5/2)*f*Sqrt[2])-5/48*(7*A-B)*Sec[e+f*x]/(a^2*c^2*f*Sqrt[c-c*Sin[e+f*x]])-1/3*(A-B)*Sec[e+f*x]^3/(a^2*c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:173
  public void test0081() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/3*a*(A+B)*Cos[e+f*x]/(f*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])-1/2*a*B*Cos[e+f*x]/(c*f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:189
  public void test0082() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(3/2), x]", //
        "1/2*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(3/2))+1/2*a*(A+2*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*Sqrt[c-c*Sin[e+f*x]])+4*a^3*(A+2*B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*a^2*(A+2*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:205
  public void test0083() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/10*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(11/2))+1/80*(A-9*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:223
  public void test0084() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2)), x]", //
        "-1/2*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(5/2))+1/8*(3*A-B)*Cos[e+f*x]/(a*f*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+1/8*(3*A-B)*Cos[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+1/8*(3*A-B)*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(a*c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:324
  public void test0085() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c+d*Sin[e+f*x])), x]", //
        "-(A-B)*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x]))+2*(B*c-A*d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(a*(c-d)*f*Sqrt[c^2-d^2])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:378
  public void test0086() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1/2*(A-B)*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(3/2))-1/2*(A+3*B)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(3/2)*f*Sqrt[2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:360
  public void test0087() {
    check( //
        "Integrate[Cos[x]^8/(a-a*Sin[x]^2)^2, x]", //
        "3/8*x/a^2+3/8*Cos[x]*Sin[x]/a^2+1/4*Cos[x]^3*Sin[x]/a^2");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:398
  public void test0088() {
    check( //
        "Integrate[Sec[x]^4/(a+b*Sin[x]^2), x]", //
        "b^2*ArcTan[Sqrt[a+b]*Tan[x]/Sqrt[a]]/((a+b)^(5/2)*Sqrt[a])+(a+2*b)*Tan[x]/(a+b)^2+1/3*Tan[x]^3/(a+b)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:420
  public void test0089() {
    check( //
        "Integrate[Sec[e+f*x]^3*Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "1/2*a*ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/(f*Sqrt[a+b])+1/2*Sec[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]*Tan[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:442
  public void test0090() {
    check( //
        "Integrate[Sec[e+f*x]/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/(f*Sqrt[a+b])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:465
  public void test0091() {
    check( //
        "Integrate[1/(a+b*Sin[e+f*x]^2)^(5/2), x]", //
        "1/3*b*Cos[e+f*x]*Sin[e+f*x]/(a*(a+b)*f*(a+b*Sin[e+f*x]^2)^(3/2))+2/3*b*(2*a+b)*Cos[e+f*x]*Sin[e+f*x]/(a^2*(a+b)^2*f*Sqrt[a+b*Sin[e+f*x]^2])+2/3*(2*a+b)*EllipticE[e+f*x,-b/a]*Sqrt[a+b*Sin[e+f*x]^2]/(a^2*(a+b)^2*f*Sqrt[1+b*Sin[e+f*x]^2/a])-1/3*EllipticF[e+f*x,-b/a]*Sqrt[1+b*Sin[e+f*x]^2/a]/(a*(a+b)*f*Sqrt[a+b*Sin[e+f*x]^2])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:568
  public void test0092() {
    check( //
        "Integrate[Tan[c+d*x]^7/(a+b*Sin[c+d*x]^2), x]", //
        "a^3*Log[Cos[c+d*x]]/((a+b)^4*d)-1/2*a^3*Log[a+b*Sin[c+d*x]^2]/((a+b)^4*d)+1/2*(3*a^2+3*a*b+b^2)*Sec[c+d*x]^2/((a+b)^3*d)-1/4*(3*a+2*b)*Sec[c+d*x]^4/((a+b)^2*d)+1/6*Sec[c+d*x]^6/((a+b)*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:588
  public void test0093() {
    check( //
        "Integrate[Sqrt[a-a*Sin[e+f*x]^2]*Tan[e+f*x]^5, x]", //
        "1/3*a^2/(f*(a*Cos[e+f*x]^2)^(3/2))-2*a/(f*Sqrt[a*Cos[e+f*x]^2])-Sqrt[a*Cos[e+f*x]^2]/f");
  }

  // 4.1.9 trig^m (a+b sin^n+c sin^(2 n))^p.input:41
  public void test0094() {
    check( //
        "Integrate[Cos[x]/(10-6*Sin[x]+Sin[x]^2), x]", //
        "-ArcTan[3-Sin[x]]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:26
  public void test0095() {
    check( //
        "Integrate[1/Cos[a+b*x]^(5/2), x]", //
        "2/3*EllipticF[1/2*(a+b*x),2]/b+2/3*Sin[a+b*x]/(b*Cos[a+b*x]^(3/2))");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:64
  public void test0096() {
    check( //
        "Integrate[(a*Cos[x]^3)^(5/2), x]", //
        "26/77*a^2*EllipticF[1/2*x,2]*Sqrt[a*Cos[x]^3]/Cos[x]^(3/2)+78/385*a^2*Cos[x]*Sin[x]*Sqrt[a*Cos[x]^3]+26/165*a^2*Cos[x]^3*Sin[x]*Sqrt[a*Cos[x]^3]+2/15*a^2*Cos[x]^5*Sin[x]*Sqrt[a*Cos[x]^3]+26/77*a^2*Sqrt[a*Cos[x]^3]*Tan[x]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:102
  public void test0097() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sqrt[b*Cos[c+d*x]], x]", //
        "2/5*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:118
  public void test0098() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*Sec[c+d*x]^3, x]", //
        "2*b^2*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2*b*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:134
  public void test0099() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*Sec[c+d*x]^8, x]", //
        "2/9*b^7*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(9/2))+14/45*b^5*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+14/15*b^3*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-14/15*b^2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:152
  public void test0100() {
    check( //
        "Integrate[Cos[c+d*x]^5/(b*Cos[c+d*x])^(3/2), x]", //
        "2/7*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^4*d)+10/21*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])+10/21*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^2*d)");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:168
  public void test0101() {
    check( //
        "Integrate[Cos[c+d*x]^2/(b*Cos[c+d*x])^(5/2), x]", //
        "2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.1.1 (a+b cos)^n.input:73
  public void test0102() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(1/2), x]", //
        "2*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:15
  public void test0103() {
    check( //
        "Integrate[Cot[x]^2/(a+a*Cos[x]), x]", //
        "-1/3*Cot[x]^3/a-Csc[x]/a+1/3*Csc[x]^3/a");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:13
  public void test0104() {
    check( //
        "Integrate[(c+d*x)^3*Cos[a+b*x], x]", //
        "-6*d^3*Cos[a+b*x]/b^4+3*d*(c+d*x)^2*Cos[a+b*x]/b^2-6*d^2*(c+d*x)*Sin[a+b*x]/b^3+(c+d*x)^3*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:29
  public void test0105() {
    check( //
        "Integrate[(c+d*x)^2*Cos[a+b*x]^3, x]", //
        "4/3*d*(c+d*x)*Cos[a+b*x]/b^2+2/9*d*(c+d*x)*Cos[a+b*x]^3/b^2-14/9*d^2*Sin[a+b*x]/b^3+2/3*(c+d*x)^2*Sin[a+b*x]/b+1/3*(c+d*x)^2*Cos[a+b*x]^2*Sin[a+b*x]/b+2/27*d^2*Sin[a+b*x]^3/b^3");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:92
  public void test0106() {
    check( //
        "Integrate[(c+d*x)^(1/3)*Cos[a+b*x], x]", //
        "1/6*E^(I*(a-b*c/d))*d*(-I*b*(c+d*x)/d)^(2/3)*Gamma[1/3,-I*b*(c+d*x)/d]/(b^2*(c+d*x)^(2/3))+1/6*d*(I*b*(c+d*x)/d)^(2/3)*Gamma[1/3,I*b*(c+d*x)/d]/(E^(I*(a-b*c/d))*b^2*(c+d*x)^(2/3))+(c+d*x)^(1/3)*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:123
  public void test0107() {
    check( //
        "Integrate[x/Sec[x]^(3/2)-1/3*x*Sqrt[Sec[x]], x]", //
        "4/9/Sec[x]^(3/2)+2/3*x*Sin[x]/Sqrt[Sec[x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:170
  public void test0108() {
    check( //
        "Integrate[(c+d*x)^2/(a+a*Cos[e+f*x]), x]", //
        "-I*(c+d*x)^2/(a*f)+4*d*(c+d*x)*Log[1+E^(I*(e+f*x))]/(a*f^2)-4*I*d^2*PolyLog[2,-E^(I*(e+f*x))]/(a*f^3)+(c+d*x)^2*Tan[1/2*e+1/2*f*x]/(a*f)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:196
  public void test0109() {
    check( //
        "Integrate[x^2*Sqrt[a+a*Cos[x]], x]", //
        "8*x*Sqrt[a+a*Cos[x]]-16*Sqrt[a+a*Cos[x]]*Tan[1/2*x]+2*x^2*Sqrt[a+a*Cos[x]]*Tan[1/2*x]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:212
  public void test0110() {
    check( //
        "Integrate[(a+a*Cos[x])^(3/2)/x, x]", //
        "3/2*a*CosIntegral[1/2*x]*Sec[1/2*x]*Sqrt[a+a*Cos[x]]+1/2*a*CosIntegral[3/2*x]*Sec[1/2*x]*Sqrt[a+a*Cos[x]]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:246
  public void test0111() {
    check( //
        "Integrate[x/(a+b*Cos[c+d*x]), x]", //
        "-I*x*Log[1+E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])+I*x*Log[1+E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(d*Sqrt[a^2-b^2])-PolyLog[2,-E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])+PolyLog[2,-E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(d^2*Sqrt[a^2-b^2])");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:24
  public void test0112() {
    check( //
        "Integrate[x^3*Cos[a+b*x^2]^3, x]", //
        "1/3*Cos[a+b*x^2]/b^2+1/18*Cos[a+b*x^2]^3/b^2+1/3*x^2*Sin[a+b*x^2]/b+1/6*x^2*Cos[a+b*x^2]^2*Sin[a+b*x^2]/b");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:98
  public void test0113() {
    check( //
        "Integrate[Cos[a+b*x^n]/x, x]", //
        "CosIntegral[b*x^n]*Cos[a]/n-SinIntegral[b*x^n]*Sin[a]/n");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:118
  public void test0114() {
    check( //
        "Integrate[x^2*Cos[(a+b*x)^2], x]", //
        "-a*Sin[(a+b*x)^2]/b^3+1/2*(a+b*x)*Sin[(a+b*x)^2]/b^3+a^2*FresnelC[(a+b*x)*Sqrt[2/Pi]]*Sqrt[1/2*Pi]/b^3-1/2*FresnelS[(a+b*x)*Sqrt[2/Pi]]*Sqrt[1/2*Pi]/b^3");
  }

  // 4.2.13 (d+e x)^m cos(a+b x+c x^2)^n.input:33
  public void test0115() {
    check( //
        "Integrate[Cos[1/4+x+x^2]^2, x]", //
        "1/2*x+1/4*FresnelC[(1+2*x)/Sqrt[Pi]]*Sqrt[Pi]");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:23
  public void test0116() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*Sec[c+d*x]^6, x]", //
        "3/8*a*ArcTanh[Sin[c+d*x]]/d+a*Tan[c+d*x]/d+3/8*a*Sec[c+d*x]*Tan[c+d*x]/d+1/4*a*Sec[c+d*x]^3*Tan[c+d*x]/d+2/3*a*Tan[c+d*x]^3/d+1/5*a*Tan[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:41
  public void test0117() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*Sec[c+d*x]^4, x]", //
        "5/2*a^3*ArcTanh[Sin[c+d*x]]/d+4*a^3*Tan[c+d*x]/d+3/2*a^3*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a^3*Tan[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:76
  public void test0118() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Cos[c+d*x])^3, x]", //
        "13/2*x/a^3-152/15*Sin[c+d*x]/(a^3*d)+13/2*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-1/5*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-11/15*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-76/15*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:92
  public void test0119() {
    check( //
        "Integrate[Sec[c+d*x]/(a+a*Cos[c+d*x])^4, x]", //
        "ArcTanh[Sin[c+d*x]]/(a^4*d)-11/21*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-32/21*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/7*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:112
  public void test0120() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Cos[c+d*x])^(1/2), x]", //
        "32/105*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+32/45*a*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+16/63*a*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/9*a*Cos[c+d*x]^4*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-64/315*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:128
  public void test0121() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]^4, x]", //
        "11/8*a^(3/2)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+11/8*a^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+11/12*a^2*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*a^2*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:146
  public void test0122() {
    check( //
        "Integrate[Sec[c+d*x]/Sqrt[a+a*Cos[c+d*x]], x]", //
        "2*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:162
  public void test0123() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x])^(5/2), x]", //
        "1/4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))+3/16*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))+3/16*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:182
  public void test0124() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2/Cos[c+d*x]^(7/2), x]", //
        "-16/5*a^2*EllipticE[1/2*(c+d*x),2]/d+4/3*a^2*EllipticF[1/2*(c+d*x),2]/d+2/5*a^2*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+4/3*a^2*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+16/5*a^2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:200
  public void test0125() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)/(a+a*Cos[c+d*x]), x]", //
        "-3*EllipticE[1/2*(c+d*x),2]/(a*d)+5/3*EllipticF[1/2*(c+d*x),2]/(a*d)-Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))+5/3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:216
  public void test0126() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)/(a+a*Cos[c+d*x])^3, x]", //
        "49/10*EllipticE[1/2*(c+d*x),2]/(a^3*d)-13/6*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-8/15*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-13/6*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:236
  public void test0127() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(3/2), x]", //
        "7/4*a^(3/2)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/2*a^2*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+7/4*a^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:270
  public void test0128() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "2*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)-5/2*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:286
  public void test0129() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)/(a+a*Cos[c+d*x])^(7/2), x]", //
        "13/64*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])+1/6*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(7/2))+1/16*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(5/2))-5/192*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:324
  public void test0130() {
    check( //
        "Integrate[1/((1-Cos[c+d*x])^(1/2)*Cos[c+d*x]^(5/2)), x]", //
        "-ArcTanh[Sin[c+d*x]/(Sqrt[2]*Sqrt[1-Cos[c+d*x]]*Sqrt[Cos[c+d*x]])]*Sqrt[2]/d+2/3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[1-Cos[c+d*x]])+2/3*Sin[c+d*x]/(d*Sqrt[1-Cos[c+d*x]]*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:491
  public void test0131() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*Sec[c+d*x]^5, x]", //
        "1/8*(3*a^2+4*b^2)*ArcTanh[Sin[c+d*x]]/d+2*a*b*Tan[c+d*x]/d+1/8*(3*a^2+4*b^2)*Sec[c+d*x]*Tan[c+d*x]/d+1/4*a^2*Sec[c+d*x]^3*Tan[c+d*x]/d+2/3*a*b*Tan[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:509
  public void test0132() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*Sec[c+d*x]^3, x]", //
        "4*a*b^3*x+1/2*a^2*(a^2+12*b^2)*ArcTanh[Sin[c+d*x]]/d-1/2*b^2*(a^2-2*b^2)*Sin[c+d*x]/d+3*a^3*b*Tan[c+d*x]/d+1/2*a^2*(a+b*Cos[c+d*x])^2*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:528
  public void test0133() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+b*Cos[c+d*x])^2, x]", //
        "-2*a*x/b^3+2*a^2*(2*a^2-3*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^3*(a+b)^(3/2)*d)+(2*a^2-b^2)*Sin[c+d*x]/(b^2*(a^2-b^2)*d)-a^2*Cos[c+d*x]*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:567
  public void test0134() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(3/2), x]", //
        "2/3*b*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+8/3*a*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/3*(a^2-b^2)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:598
  public void test0135() {
    check( //
        "Integrate[Cos[c+d*x]/Sqrt[a+b*Cos[c+d*x]], x]", //
        "2*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2*a*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:622
  public void test0136() {
    check( //
        "Integrate[Cos[c+d*x]/Sqrt[3+4*Cos[c+d*x]], x]", //
        "-3/2*EllipticF[1/2*(c+d*x),8/7]/(d*Sqrt[7])+1/2*EllipticE[1/2*(c+d*x),8/7]*Sqrt[7]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:648
  public void test0137() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2/Cos[c+d*x]^(1/2), x]", //
        "4*a*b*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*a^2+b^2)*EllipticF[1/2*(c+d*x),2]/d+2/3*b^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:898
  public void test0138() {
    check( //
        "Integrate[Sqrt[(1-Cos[x])/(a-Cos[x])], x]", //
        "-2*ArcTan[Sin[x]/(Sqrt[1-Cos[x]]*Sqrt[a-Cos[x]])]*Sqrt[(1-Cos[x])/(a-Cos[x])]*Sqrt[a-Cos[x]]/Sqrt[1-Cos[x]]");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:938
  public void test0139() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^2, x]", //
        "2*A*b*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2*b*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:954
  public void test0140() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^6, x]", //
        "2/5*A*b^5*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(5/2))+2/3*b^4*B*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+6/5*A*b^3*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b^3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-6/5*A*b^2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:972
  public void test0141() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*B*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^4*d)+2/3*A*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/3*A*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)+6/5*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:992
  public void test0142() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "1/2*A*b*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+b*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*b*B*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/2*A*b*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1010
  public void test0143() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(1/2), x]", //
        "1/2*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+1/2*B*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]+A*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1026
  public void test0144() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(5/2), x]", //
        "B*x*Sqrt[Cos[c+d*x]]/(b^2*Sqrt[b*Cos[c+d*x]])+A*ArcTanh[Sin[c+d*x]]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:23
  public void test0145() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^2*(A+B*Cos[c+d*x]), x]", //
        "1/8*a^2*(8*A+7*B)*x+1/6*a^2*(8*A+7*B)*Sin[c+d*x]/d+1/24*a^2*(8*A+7*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/12*(4*A-B)*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/d+1/4*B*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(a*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:39
  public void test0146() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]), x]", //
        "1/16*a^4*(49*A+44*B)*x+1/35*a^4*(252*A+227*B)*Sin[c+d*x]/d+1/16*a^4*(49*A+44*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/280*a^4*(301*A+276*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/7*a*B*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d+1/42*(7*A+10*B)*Cos[c+d*x]^3*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d+7/15*(A+B)*Cos[c+d*x]^3*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d-1/105*a^4*(252*A+227*B)*Sin[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:58
  public void test0147() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^3/(a+a*Cos[c+d*x]), x]", //
        "1/2*(3*A-2*B)*ArcTanh[Sin[c+d*x]]/(a*d)-2*(A-B)*Tan[c+d*x]/(a*d)+1/2*(3*A-2*B)*Sec[c+d*x]*Tan[c+d*x]/(a*d)-(A-B)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:74
  public void test0148() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^3, x]", //
        "1/5*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(2*A+3*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/15*(2*A+3*B)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:94
  public void test0149() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x]), x]", //
        "2/3*a*(3*A+B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/3*B*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:110
  public void test0150() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]), x]", //
        "2/35*a*(7*A+5*B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/7*B*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+64/105*a^3*(7*A+5*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+16/105*a^2*(7*A+5*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:128
  public void test0151() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*(A-B)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))-1/2*(7*A-11*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/3*(9*A-13*B)*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])-1/6*(3*A-7*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:148
  public void test0152() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x])*Sqrt[Cos[c+d*x]], x]", //
        "2/5*a*(5*A+3*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(A+B)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*a*(A+B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:164
  public void test0153() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x])/Cos[c+d*x]^(5/2), x]", //
        "-4*a^3*(A-B)*EllipticE[1/2*(c+d*x),2]/d+20/3*a^3*(A+B)*EllipticF[1/2*(c+d*x),2]/d+2/3*a*A*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/3*(7*A+3*B)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-4/3*a^3*(4*A+B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:182
  public void test0154() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^2), x]", //
        "(7*A-4*B)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+5/3*(2*A-B)*EllipticF[1/2*(c+d*x),2]/(a^2*d)+5/3*(2*A-B)*Sin[c+d*x]/(a^2*d*Cos[c+d*x]^(3/2))-1/3*(7*A-4*B)*Sin[c+d*x]/(a^2*d*Cos[c+d*x]^(3/2)*(1+Cos[c+d*x]))-1/3*(A-B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2)-(7*A-4*B)*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:202
  public void test0155() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(9/2), x]", //
        "2/7*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])+2/35*a*(6*A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+8/105*a*(6*A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+16/105*a*(6*A+7*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:218
  public void test0156() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(11/2), x]", //
        "2/9*a*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(9/2))+2/315*a^3*(124*A+135*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/315*a^3*(292*A+345*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+4/315*a^3*(292*A+345*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/21*a^2*(4*A+3*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(7/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:236
  public void test0157() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "1/16*(19*A+5*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(9*A-B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:258
  public void test0158() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "1/2*(A*b+a*B)*ArcTanh[Sin[c+d*x]]/d+1/3*(2*a*A+3*b*B)*Tan[c+d*x]/d+1/2*(A*b+a*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:274
  public void test0159() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x])*Sec[c+d*x]^4, x]", //
        "b^3*B*x+1/2*(3*a^2*A*b+2*A*b^3+a^3*B+6*a*b^2*B)*ArcTanh[Sin[c+d*x]]/d+1/3*a*(2*a^2*A+8*A*b^2+9*a*b*B)*Tan[c+d*x]/d+1/6*a^2*(5*A*b+3*a*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a*A*(a+b*Cos[c+d*x])^2*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:292
  public void test0160() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x]), x]", //
        "B*x/b+2*(A*b-a*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:308
  public void test0161() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^3, x]", //
        "(2*a^2*A+A*b^2-3*a*b*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(5/2)*(a+b)^(5/2)*d)-1/2*(A*b-a*B)*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^2)-1/2*(3*a*A*b-a^2*B-2*b^2*B)*Sin[c+d*x]/((a^2-b^2)^2*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:324
  public void test0162() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])*Sec[c+d*x]/(a+b*Cos[c+d*x]), x]", //
        "B*ArcTanh[Sin[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:348
  public void test0163() {
    check( //
        "Integrate[Cos[c+d*x]*(a+b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "2/35*(7*A*b-2*a*B)*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)+2/7*B*(a+b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b*d)+2/105*(21*a*A*b-6*a^2*B+25*b^2*B)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)+2/105*(21*a^2*A*b+63*A*b^3-6*a^3*B+82*a*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/105*(a^2-b^2)*(21*a*A*b-6*a^2*B+25*b^2*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:381
  public void test0164() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(5/2), x]", //
        "2/3*a*(A*b-a*B)*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x])^(3/2))+2/3*(a^2*A*b+3*A*b^3+2*a^3*B-6*a*b^2*B)*Sin[c+d*x]/(b*(a^2-b^2)^2*d*Sqrt[a+b*Cos[c+d*x]])-2/3*(a^2*A*b+3*A*b^3+2*a^3*B-6*a*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*(a^2-b^2)^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2/3*(a*A*b+2*a^2*B-3*b^2*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:24
  public void test0165() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^8, x]", //
        "1/7*(6*A+7*C)*Tan[c+d*x]/d+1/7*A*Sec[c+d*x]^6*Tan[c+d*x]/d+2/21*(6*A+7*C)*Tan[c+d*x]^3/d+1/35*(6*A+7*C)*Tan[c+d*x]^5/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:44
  public void test0166() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Sec[c+d*x])^(1/2), x]", //
        "2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]/(d*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]])+2/5*b^2*C*Tan[c+d*x]/(d*(b*Sec[c+d*x])^(5/2))");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:69
  public void test0167() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "2/3*b^2*(3*A+C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:87
  public void test0168() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(1/2), x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:103
  public void test0169() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/7*C*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^5*d)+2/21*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/21*(7*A+5*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:350
  public void test0170() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2*Sqrt[b*Cos[c+d*x]], x]", //
        "2*A*b*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2*b*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*(A-C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:366
  public void test0171() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "2*A*b^3*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2*b^3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*b^2*(A-C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:384
  public void test0172() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "2*A*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+2*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])-2*(A-C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:404
  public void test0173() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]], x]", //
        "A*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/2*C*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/2*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:420
  public void test0174() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "1/4*b^2*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+1/8*b^2*(4*A+3*C)*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+b^2*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])-1/3*b^2*B*Sin[c+d*x]^3*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/8*b^2*(4*A+3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:438
  public void test0175() {
    check( //
        "Integrate[Cos[c+d*x]^(7/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "1/8*(4*A+3*C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/4*C*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/8*(4*A+3*C)*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])+B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])-1/3*B*Sin[c+d*x]^3*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:12
  public void test0176() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2), x]", //
        "1/8*a*(4*A+3*C)*x+1/5*a*(5*A+4*C)*Sin[c+d*x]/d+1/8*a*(4*A+3*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*C*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a*C*Cos[c+d*x]^4*Sin[c+d*x]/d-1/15*a*(5*A+4*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:28
  public void test0177() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/4*a^2*(3*A+4*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^2*(18*A+25*C)*Tan[c+d*x]/d+1/4*a^2*(3*A+4*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/30*a^2*(9*A+10*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/10*A*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*A*(a+a*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:44
  public void test0178() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "2*a^4*(2*A+3*C)*x+1/2*a^4*(13*A+2*C)*ArcTanh[Sin[c+d*x]]/d-5/2*a^4*(A-2*C)*Sin[c+d*x]/d-1/6*(15*A-2*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/d-1/3*(9*A-4*C)*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d+2*a*A*(a+a*Cos[c+d*x])^3*Tan[c+d*x]/d+1/2*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:62
  public void test0179() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^2, x]", //
        "1/2*(2*A+7*C)*x/a^2-4/3*(A+4*C)*Sin[c+d*x]/(a^2*d)+1/2*(2*A+7*C)*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-2/3*(A+4*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A+C)*Cos[c+d*x]^3*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:78
  public void test0180() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^4, x]", //
        "1/2*(2*A+21*C)*x/a^4-32/105*(5*A+54*C)*Sin[c+d*x]/(a^4*d)+1/2*(2*A+21*C)*Cos[c+d*x]*Sin[c+d*x]/(a^4*d)-1/105*(10*A+129*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-16/105*(5*A+54*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A+C)*Cos[c+d*x]^5*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-2/5*C*Cos[c+d*x]^4*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:98
  public void test0181() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4*Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/8*(5*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/8*a*(5*A+8*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/12*a*A*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/3*A*Sec[c+d*x]^2*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:114
  public void test0182() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "1/4*a^(5/2)*(19*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/12*a^3*(27*A-56*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/12*a^2*(21*A-8*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+5/4*a*A*(a+a*Cos[c+d*x])^(3/2)*Tan[c+d*x]/d+1/2*A*(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:132
  public void test0183() {
    check( //
        "Integrate[Cos[c+d*x]*(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/2*(A+C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*(3*A+11*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/3*(3*A+13*C)*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+1/6*(3*A+7*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^2*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:152
  public void test0184() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "2/5*a*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(3*A+C)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*a*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:168
  public void test0185() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "4/5*a^3*(5*A+7*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^3*(35*A+13*C)*EllipticF[1/2*(c+d*x),2]/d+2*A*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-4/105*a^3*(35*A-41*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d-2/7*(7*A-C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)-2/35*(35*A-11*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:186
  public void test0186() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/((a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]]), x]", //
        "(A-C)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+2/3*(A+C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)-(A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:206
  public void test0187() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]]/Cos[c+d*x]^(9/2), x]", //
        "2/35*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*a*(24*A+35*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+4/105*a*(24*A+35*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/7*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(7/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:222
  public void test0188() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(7/2), x]", //
        "5*a^(5/2)*C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*a*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*A*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))-1/15*a^3*(64*A+15*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+2/5*a^2*(8*A+5*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:240
  public void test0189() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "-1/2*(A+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2))+1/2*(11*A+3*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/6*(7*A+3*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])-1/6*(19*A+3*C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:262
  public void test0190() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "3/8*B*ArcTanh[Sin[c+d*x]]/d+C*Tan[c+d*x]/d+3/8*B*Sec[c+d*x]*Tan[c+d*x]/d+1/4*B*Sec[c+d*x]^3*Tan[c+d*x]/d+1/3*C*Tan[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:278
  public void test0191() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "1/2*a^2*(2*B+3*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(5*B+6*C)*Tan[c+d*x]/d+1/6*a^2*(4*B+3*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*B*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:296
  public void test0192() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x]), x]", //
        "B*ArcTanh[Sin[c+d*x]]/(a*d)-(B-C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:312
  public void test0193() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^3, x]", //
        "1/5*(B-C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(2*B+3*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+1/15*(2*B+3*C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:338
  public void test0194() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(9/2), x]", //
        "-6/5*B*EllipticE[1/2*(c+d*x),2]/d+2/3*C*EllipticF[1/2*(c+d*x),2]/d+2/5*B*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/3*C*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+6/5*B*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:364
  public void test0195() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "a*(B+C)*x+a*(A+B)*ArcTanh[Sin[c+d*x]]/d+a*C*Sin[c+d*x]/d+a*A*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:380
  public void test0196() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/8*a^3*(28*A+20*B+15*C)*x+a^3*A*ArcTanh[Sin[c+d*x]]/d+5/8*a^3*(4*A+4*B+3*C)*Sin[c+d*x]/d+1/4*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d+1/12*(4*B+3*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d)+1/24*(12*A+20*B+15*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:396
  public void test0197() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^7, x]", //
        "7/16*a^4*(7*A+8*B+10*C)*ArcTanh[Sin[c+d*x]]/d+1/15*a^4*(72*A+83*B+100*C)*Tan[c+d*x]/d+1/240*a^4*(417*A+488*B+550*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/60*(43*A+52*B+50*C)*(a^4+a^4*Cos[c+d*x])*Sec[c+d*x]^2*Tan[c+d*x]/d+1/120*(37*A+48*B+30*C)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^3*Tan[c+d*x]/d+1/15*a*(2*A+3*B)*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^4*Tan[c+d*x]/d+1/6*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]^5*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:414
  public void test0198() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x])^2, x]", //
        "1/2*(7*A-4*B+2*C)*ArcTanh[Sin[c+d*x]]/(a^2*d)-2/3*(8*A-5*B+2*C)*Tan[c+d*x]/(a^2*d)+1/2*(7*A-4*B+2*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d)-1/3*(8*A-5*B+2*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B+C)*Sec[c+d*x]*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:430
  public void test0199() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+a*Cos[c+d*x])^4, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^4*d)-1/105*(55*A-6*B-8*C)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-2/105*(80*A-3*B-4*C)*Sin[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/35*(10*A-3*B-4*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:450
  public void test0200() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "2*a^(3/2)*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/5*C*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/15*a^2*(15*A+20*B+12*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/15*a*(5*B+3*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:484
  public void test0201() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/8*(47*A-38*B+24*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)+1/2*(17*A-13*B+9*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A-B+C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/8*(21*A-14*B+12*C)*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])-1/12*(13*A-12*B+6*C)*Sec[c+d*x]*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])+1/6*(5*A-3*B+3*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:520
  public void test0202() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "4/15*a^3*(27*A+21*B+17*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^3*(21*A+13*B+11*C)*EllipticF[1/2*(c+d*x),2]/d+4/105*a^3*(42*A+41*B+32*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/9*C*(a+a*Cos[c+d*x])^3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/21*(3*B+2*C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)+2/315*(63*A+99*B+73*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:538
  public void test0203() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[Cos[c+d*x]]/(a+a*Cos[c+d*x])^2, x]", //
        "-(B-4*C)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+1/3*(A+2*B-5*C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)-1/3*(A-B+C)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2)+1/3*(A+2*B-5*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:558
  public void test0204() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]]/Cos[c+d*x]^(7/2), x]", //
        "2/15*a*(A+5*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+2/15*a*(8*A+10*B+15*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/5*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(5/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:574
  public void test0205() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "1/4*a^(5/2)*(8*A+20*B+19*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*A*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/3*a*(5*A+3*B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-1/12*a^3*(56*A+12*B-27*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])-1/2*a^2*(8*A+4*B-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:592
  public void test0206() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "2*C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(3/2)*d)+1/2*(3*A+B-5*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A-B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:616
  public void test0207() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "1/8*a*(3*A+4*C)*ArcTanh[Sin[c+d*x]]/d+1/3*b*(2*A+3*C)*Tan[c+d*x]/d+1/8*a*(3*A+4*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*A*b*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a*A*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:648
  public void test0208() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(a^2-b^2*Cos[c+d*x]^2), x]", //
        "1/2*a*(2*a^2-b^2)*x+2/3*b*(2*a^2-b^2)*Sin[c+d*x]/d+1/6*a*b^2*Cos[c+d*x]*Sin[c+d*x]/d-1/3*b*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:682
  public void test0209() {
    check( //
        "Integrate[Cos[c+d*x]^3*(1-Cos[c+d*x]^2)/(a+b*Cos[c+d*x]), x]", //
        "-1/8*(8*a^4-4*a^2*b^2-b^4)*x/b^5+1/3*a*(3*a^2-b^2)*Sin[c+d*x]/(b^4*d)-1/8*(4*a^2-b^2)*Cos[c+d*x]*Sin[c+d*x]/(b^3*d)+1/3*a*Cos[c+d*x]^2*Sin[c+d*x]/(b^2*d)-1/4*Cos[c+d*x]^3*Sin[c+d*x]/(b*d)+2*a^3*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]*Sqrt[a-b]*Sqrt[a+b]/(b^5*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:718
  public void test0210() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[a+b*Cos[c+d*x]], x]", //
        "2/5*C*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b*d)-4/15*a*C*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/(b*d)-2/15*(2*a^2*C-3*b^2*(5*A+3*C))*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+4/15*a*(a^2-b^2)*C*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:754
  public void test0211() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^(3/2), x]", //
        "-2*(A*b^2+a^2*C)*Sin[c+d*x]/(b*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])+2*(A*b^2+2*a^2*C-b^2*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*(a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-4*a*C*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:779
  public void test0212() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(7/2), x]", //
        "-2/5*a*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]/d+2/3*b*(A+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/3*A*b*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*a*(3*A+5*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:897
  public void test0213() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x], x]", //
        "1/2*(2*a^2*B+b^2*B+2*a*b*C)*x+2/3*(3*a*b*B+a^2*C+b^2*C)*Sin[c+d*x]/d+1/6*b*(3*b*B+2*a*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*C*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:968
  public void test0214() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^(3/2), x]", //
        "2*a*(b*B-a*C)*Sin[c+d*x]/(b*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])-2*(a*b*B-2*a^2*C+b^2*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b^2*(a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2*(b*B-2*a*C)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b^2*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:992
  public void test0215() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "2/5*(3*b^2*C+5*a*(2*b*B+a*C))*EllipticE[1/2*(c+d*x),2]/d+2/3*(3*a^2*B+b^2*B+2*a*b*C)*EllipticF[1/2*(c+d*x),2]/d+2/15*b*(5*b*B+7*a*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d+2/5*b*C*(a+b*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:73
  public void test0216() {
    check( //
        "Integrate[Sec[a+b*x]^2*Sin[a+b*x], x]", //
        "Sec[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:105
  public void test0217() {
    check( //
        "Integrate[Sec[a+b*x]^8*Sin[a+b*x]^3, x]", //
        "-1/5*Sec[a+b*x]^5/b+1/7*Sec[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:137
  public void test0218() {
    check( //
        "Integrate[Sec[a+b*x]^6*Sin[a+b*x]^5, x]", //
        "Sec[a+b*x]/b-2/3*Sec[a+b*x]^3/b+1/5*Sec[a+b*x]^5/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:155
  public void test0219() {
    check( //
        "Integrate[Sec[a+b*x]/Sin[a+b*x], x]", //
        "Log[Tan[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:273
  public void test0220() {
    check( //
        "Integrate[Csc[a+b*x]^2/(d*Cos[a+b*x])^(1/2), x]", //
        "EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*Sqrt[d*Cos[a+b*x]])-Csc[a+b*x]*Sqrt[d*Cos[a+b*x]]/(b*d)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:295
  public void test0221() {
    check( //
        "Integrate[Cos[x]^3*Sin[x]^(3/2), x]", //
        "2/5*Sin[x]^(5/2)-2/9*Sin[x]^(9/2)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:317
  public void test0222() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(3/2)/(d*Cos[a+b*x])^(9/2), x]", //
        "2/7*c*Sqrt[c*Sin[a+b*x]]/(b*d*(d*Cos[a+b*x])^(7/2))-2/21*c*Sqrt[c*Sin[a+b*x]]/(b*d^3*(d*Cos[a+b*x])^(3/2))-2/21*c^2*EllipticF[-1/4*Pi+a+b*x,2]*Sqrt[Sin[2*a+2*b*x]]/(b*d^4*Sqrt[d*Cos[a+b*x]]*Sqrt[c*Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:333
  public void test0223() {
    check( //
        "Integrate[(c*Sin[a+b*x])^(5/2)/(d*Cos[a+b*x])^(17/2), x]", //
        "2/15*c*(c*Sin[a+b*x])^(3/2)/(b*d*(d*Cos[a+b*x])^(15/2))-2/55*c*(c*Sin[a+b*x])^(3/2)/(b*d^3*(d*Cos[a+b*x])^(11/2))-16/385*c*(c*Sin[a+b*x])^(3/2)/(b*d^5*(d*Cos[a+b*x])^(7/2))-64/1155*c*(c*Sin[a+b*x])^(3/2)/(b*d^7*(d*Cos[a+b*x])^(3/2))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:351
  public void test0224() {
    check( //
        "Integrate[Cos[a+b*x]^(5/2)/Sin[a+b*x]^(5/2), x]", //
        "-2/3*Cos[a+b*x]^(3/2)/(b*Sin[a+b*x]^(3/2))-ArcTan[1-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])+ArcTan[1+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])+1/2*Log[1+Cot[a+b*x]-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-1/2*Log[1+Cot[a+b*x]+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:454
  public void test0225() {
    check( //
        "Integrate[Csc[e+f*x]^4*Sqrt[b*Sec[e+f*x]], x]", //
        "-5/6*b*Csc[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])-1/3*b*Csc[e+f*x]^3/(f*Sqrt[b*Sec[e+f*x]])+5/6*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:504
  public void test0226() {
    check( //
        "Integrate[Sin[e+f*x]^4/(b*Sec[e+f*x])^(3/2), x]", //
        "-12/77*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(5/2))-2/11*b*Sin[e+f*x]^3/(f*(b*Sec[e+f*x])^(5/2))+8/77*Sin[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])+8/77*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:520
  public void test0227() {
    check( //
        "Integrate[Csc[e+f*x]^2/(b*Sec[e+f*x])^(5/2), x]", //
        "-Csc[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))-3*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:542
  public void test0228() {
    check( //
        "Integrate[Sin[e+f*x]^(1/2)/Sqrt[b*Sec[e+f*x]], x]", //
        "EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:558
  public void test0229() {
    check( //
        "Integrate[1/((b*Sec[e+f*x])^(3/2)*(a*Sin[e+f*x])^(1/2)), x]", //
        "Sqrt[a*Sin[e+f*x]]/(a*b*f*Sqrt[b*Sec[e+f*x]])+1/2*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:24
  public void test0230() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^2, x]", //
        "45/128*a^2*x-9/56*a^2*Cos[c+d*x]^7/d+45/128*a^2*Cos[c+d*x]*Sin[c+d*x]/d+15/64*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+3/16*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*Cos[c+d*x]^7*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:40
  public void test0231() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "9/16*a^3*x-3/10*a^3*Cos[c+d*x]^5/d+9/16*a^3*Cos[c+d*x]*Sin[c+d*x]/d+3/8*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d-1/7*a*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2/d-3/14*Cos[c+d*x]^5*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:90
  public void test0232() {
    check( //
        "Integrate[Cos[c+d*x]^6/(a+a*Sin[c+d*x])^3, x]", //
        "5/2*x/a^3+5/3*Cos[c+d*x]^3/(a^3*d)+5/2*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)+2*Cos[c+d*x]^5/(a*d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:106
  public void test0233() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Sin[c+d*x])^8, x]", //
        "(-1/3)/(a^2*d*(a+a*Sin[c+d*x])^6)+1/5/(a^3*d*(a+a*Sin[c+d*x])^5)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:126
  public void test0234() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[a]/(d*Sqrt[2])+Sec[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:176
  public void test0235() {
    check( //
        "Integrate[Cos[c+d*x]^6/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-64/693*a^3*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(7/2))-16/99*a^2*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(5/2))-2/11*a*Cos[c+d*x]^7/(d*(a+a*Sin[c+d*x])^(3/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:192
  public void test0236() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2/3*(a+a*Sin[c+d*x])^(3/2)/(a^3*d)+4*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:208
  public void test0237() {
    check( //
        "Integrate[Cos[c+d*x]^3/(a+a*Sin[c+d*x])^(5/2), x]", //
        "(-4)/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-2*Sqrt[a+a*Sin[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:228
  public void test0238() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+a*Sin[c+d*x])^2, x]", //
        "-22/63*a^2*(e*Cos[c+d*x])^(7/2)/(d*e)+22/45*a^2*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-2/9*(e*Cos[c+d*x])^(7/2)*(a^2+a^2*Sin[c+d*x])/(d*e)+22/15*a^2*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:244
  public void test0239() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/(e*Cos[c+d*x])^(7/2), x]", //
        "4/5*a^5*(e*Cos[c+d*x])^(3/2)/(d*e^5*(a-a*Sin[c+d*x])^2)-6/5*a^6*(e*Cos[c+d*x])^(3/2)/(d*e^5*(a^3-a^3*Sin[c+d*x]))+6/5*a^3*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:262
  public void test0240() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/(a+a*Sin[c+d*x]), x]", //
        "2*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a*d*Sqrt[e*Cos[c+d*x]])+2*e*Sqrt[e*Cos[c+d*x]]/(a*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:278
  public void test0241() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(15/2)/(a+a*Sin[c+d*x])^3, x]", //
        "26/45*e^3*(e*Cos[c+d*x])^(9/2)/(a^3*d)+26/35*e^5*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(a^3*d)+4/5*e*(e*Cos[c+d*x])^(13/2)/(a*d*(a+a*Sin[c+d*x])^2)+26/21*e^8*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[e*Cos[c+d*x]])+26/21*e^7*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:294
  public void test0242() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)/(a+a*Sin[c+d*x])^4, x]", //
        "-2/77*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^4*d*Sqrt[e*Cos[c+d*x]])-4/11*e*Sqrt[e*Cos[c+d*x]]/(a*d*(a+a*Sin[c+d*x])^3)+2/77*e*Sqrt[e*Cos[c+d*x]]/(d*(a^2+a^2*Sin[c+d*x])^2)+2/77*e*Sqrt[e*Cos[c+d*x]]/(d*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:420
  public void test0243() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "-1/4*b*Cos[c+d*x]^4/d+a*Sin[c+d*x]/d-1/3*a*Sin[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:437
  public void test0244() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+b*Sin[c+d*x])^2, x]", //
        "1/16*(6*a^2+b^2)*x-7/30*a*b*Cos[c+d*x]^5/d+1/16*(6*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(6*a^2+b^2)*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*b*Cos[c+d*x]^5*(a+b*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:453
  public void test0245() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+b*Sin[c+d*x])^3, x]", //
        "2/15*b*(2*a^2-b^2)*Sec[c+d*x]/d+1/5*Sec[c+d*x]^5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/d+2/15*Sec[c+d*x]^3*(a+b*Sin[c+d*x])*(a*b+(2*a^2-b^2)*Sin[c+d*x])/d+2/15*a*(4*a^2-3*b^2)*Tan[c+d*x]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:503
  public void test0246() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+b*Sin[c+d*x])^3, x]", //
        "ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(3/2)*d)-1/2*Cos[c+d*x]/(b*d*(a+b*Sin[c+d*x])^2)+1/2*a*Cos[c+d*x]/(b*(a^2-b^2)*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:557
  public void test0247() {
    check( //
        "Integrate[Cos[c+d*x]^5/Sqrt[a+b*Sin[c+d*x]], x]", //
        "-8/3*a*(a^2-b^2)*(a+b*Sin[c+d*x])^(3/2)/(b^5*d)+4/5*(3*a^2-b^2)*(a+b*Sin[c+d*x])^(5/2)/(b^5*d)-8/7*a*(a+b*Sin[c+d*x])^(7/2)/(b^5*d)+2/9*(a+b*Sin[c+d*x])^(9/2)/(b^5*d)+2*(a^2-b^2)^2*Sqrt[a+b*Sin[c+d*x]]/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:609
  public void test0248() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^2/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/(d*e*(e*Cos[c+d*x])^(5/2))+2/5*a*b/(d*e^3*Sqrt[e*Cos[c+d*x]])+2/5*(3*a^2-2*b^2)*Sin[c+d*x]/(d*e^3*Sqrt[e*Cos[c+d*x]])-2/5*(3*a^2-2*b^2)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:625
  public void test0249() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4/(e*Cos[c+d*x])^(5/2), x]", //
        "2/3*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^3/(d*e*(e*Cos[c+d*x])^(3/2))+2/3*(a^4-12*a^2*b^2-4*b^4)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^2*Sqrt[e*Cos[c+d*x]])+2/3*a*b*(a^2+14*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e^3)+2/3*b*(a^2+2*b^2)*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e^3)+2/3*a*b*(a+b*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]]/(d*e^3)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:36
  public void test0250() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3*Tan[c+d*x]^7, x]", //
        "209/16*a^3*Log[1-Sin[c+d*x]]/d-1/16*a^3*Log[1+Sin[c+d*x]]/d+7*a^3*Sin[c+d*x]/d+3/2*a^3*Sin[c+d*x]^2/d+1/3*a^3*Sin[c+d*x]^3/d+1/6*a^6/(d*(a-a*Sin[c+d*x])^3)-13/8*a^5/(d*(a-a*Sin[c+d*x])^2)+71/8*a^4/(d*(a-a*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:52
  public void test0251() {
    check( //
        "Integrate[Cot[c+d*x]^2*(a+a*Sin[c+d*x])^4, x]", //
        "17/8*a^4*x-4*a^4*ArcTanh[Cos[c+d*x]]/d+4*a^4*Cos[c+d*x]/d-4/3*a^4*Cos[c+d*x]^3/d-a^4*Cot[c+d*x]/d+23/8*a^4*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:71
  public void test0252() {
    check( //
        "Integrate[Cot[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "-1/2*ArcTanh[Cos[c+d*x]]/(a*d)-1/3*Cot[c+d*x]^3/(a*d)+1/2*Cot[c+d*x]*Csc[c+d*x]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:131
  public void test0253() {
    check( //
        "Integrate[Tan[e+f*x]^2/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/6*Sec[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2))-11/128*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))+17/48*Sec[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-11/128*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])+11/96*Sec[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:46
  public void test0254() {
    check( //
        "Integrate[(c+d*x)^3*Csc[a+b*x]^3, x]", //
        "-6*d^2*(c+d*x)*ArcTanh[E^(I*(a+b*x))]/b^3-(c+d*x)^3*ArcTanh[E^(I*(a+b*x))]/b-3/2*d*(c+d*x)^2*Csc[a+b*x]/b^2-1/2*(c+d*x)^3*Cot[a+b*x]*Csc[a+b*x]/b+3*I*d^3*PolyLog[2,-E^(I*(a+b*x))]/b^4+3/2*I*d*(c+d*x)^2*PolyLog[2,-E^(I*(a+b*x))]/b^2-3*I*d^3*PolyLog[2,E^(I*(a+b*x))]/b^4-3/2*I*d*(c+d*x)^2*PolyLog[2,E^(I*(a+b*x))]/b^2-3*d^2*(c+d*x)*PolyLog[3,-E^(I*(a+b*x))]/b^3+3*d^2*(c+d*x)*PolyLog[3,E^(I*(a+b*x))]/b^3-3*I*d^3*PolyLog[4,-E^(I*(a+b*x))]/b^4+3*I*d^3*PolyLog[4,E^(I*(a+b*x))]/b^4");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:94
  public void test0255() {
    check( //
        "Integrate[x/Sin[e+f*x]^(5/2)-1/3*x/Sqrt[Sin[e+f*x]], x]", //
        "-2/3*x*Cos[e+f*x]/(f*Sin[e+f*x]^(3/2))+(-4/3)/(f^2*Sqrt[Sin[e+f*x]])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:145
  public void test0256() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c+d*x), x]", //
        "-1/2*a^2*CosIntegral[2*c*f/d+2*f*x]*Cos[2*e-2*c*f/d]/d+3/2*a^2*Log[c+d*x]/d+2*a^2*Cos[e-c*f/d]*SinIntegral[c*f/d+f*x]/d+1/2*a^2*SinIntegral[2*c*f/d+2*f*x]*Sin[2*e-2*c*f/d]/d+2*a^2*CosIntegral[c*f/d+f*x]*Sin[e-c*f/d]/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:203
  public void test0257() {
    check( //
        "Integrate[(c+d*x)^m*(a+a*Sin[e+f*x]), x]", //
        "a*(c+d*x)^(1+m)/(d*(1+m))-1/2*E^(I*(e-c*f/d))*a*(c+d*x)^m*Gamma[1+m,-I*f*(c+d*x)/d]/(f*(-I*f*(c+d*x)/d)^m)-1/2*a*(c+d*x)^m*Gamma[1+m,I*f*(c+d*x)/d]/(E^(I*(e-c*f/d))*f*(I*f*(c+d*x)/d)^m)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:360
  public void test0258() {
    check( //
        "Integrate[Cos[c+d*x]^2/((e+f*x)*(a+a*Sin[c+d*x])), x]", //
        "Log[e+f*x]/(a*f)-Cos[c-d*e/f]*SinIntegral[d*e/f+d*x]/(a*f)-CosIntegral[d*e/f+d*x]*Sin[c-d*e/f]/(a*f)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:382
  public void test0259() {
    check( //
        "Integrate[(e+f*x)^3*Sec[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-1/2*I*f*(e+f*x)^2/(a*d^2)-5*I*f^2*(e+f*x)*ArcTan[E^(I*(c+d*x))]/(a*d^3)-3/4*I*(e+f*x)^3*ArcTan[E^(I*(c+d*x))]/(a*d)+f^2*(e+f*x)*Log[1+E^(2*I*(c+d*x))]/(a*d^3)+5/2*I*f^3*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^4)+9/8*I*f*(e+f*x)^2*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-5/2*I*f^3*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^4)-9/8*I*f*(e+f*x)^2*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-1/2*I*f^3*PolyLog[2,-E^(2*I*(c+d*x))]/(a*d^4)-9/4*f^2*(e+f*x)*PolyLog[3,-I*E^(I*(c+d*x))]/(a*d^3)+9/4*f^2*(e+f*x)*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)-9/4*I*f^3*PolyLog[4,-I*E^(I*(c+d*x))]/(a*d^4)+9/4*I*f^3*PolyLog[4,I*E^(I*(c+d*x))]/(a*d^4)-1/4*f^3*Sec[c+d*x]/(a*d^4)-9/8*f*(e+f*x)^2*Sec[c+d*x]/(a*d^2)-1/4*f^2*(e+f*x)*Sec[c+d*x]^2/(a*d^3)-1/4*f*(e+f*x)^2*Sec[c+d*x]^3/(a*d^2)-1/4*(e+f*x)^3*Sec[c+d*x]^4/(a*d)+1/4*f^3*Tan[c+d*x]/(a*d^4)+1/2*f*(e+f*x)^2*Tan[c+d*x]/(a*d^2)+1/4*f^2*(e+f*x)*Sec[c+d*x]*Tan[c+d*x]/(a*d^3)+3/8*(e+f*x)^3*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/4*f*(e+f*x)^2*Sec[c+d*x]^2*Tan[c+d*x]/(a*d^2)+1/4*(e+f*x)^3*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:55
  public void test0260() {
    check( //
        "Integrate[x^3*(a+b*x^2)*Sin[c+d*x], x]", //
        "-120*b*x*Cos[c+d*x]/d^5+6*a*x*Cos[c+d*x]/d^3+20*b*x^3*Cos[c+d*x]/d^3-a*x^3*Cos[c+d*x]/d-b*x^5*Cos[c+d*x]/d+120*b*Sin[c+d*x]/d^6-6*a*Sin[c+d*x]/d^4-60*b*x^2*Sin[c+d*x]/d^4+3*a*x^2*Sin[c+d*x]/d^2+5*b*x^4*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:71
  public void test0261() {
    check( //
        "Integrate[(a+b*x^2)^2*Sin[c+d*x]/x^5, x]", //
        "-1/12*a^2*d*Cos[c+d*x]/x^3-a*b*d*Cos[c+d*x]/x+1/24*a^2*d^3*Cos[c+d*x]/x+b^2*Cos[c]*SinIntegral[d*x]-a*b*d^2*Cos[c]*SinIntegral[d*x]+1/24*a^2*d^4*Cos[c]*SinIntegral[d*x]+b^2*CosIntegral[d*x]*Sin[c]-a*b*d^2*CosIntegral[d*x]*Sin[c]+1/24*a^2*d^4*CosIntegral[d*x]*Sin[c]-1/4*a^2*Sin[c+d*x]/x^4-a*b*Sin[c+d*x]/x^2+1/24*a^2*d^2*Sin[c+d*x]/x^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:34
  public void test0262() {
    check( //
        "Integrate[x^5*Sin[a+b*x^2]^3, x]", //
        "7/9*Cos[a+b*x^2]/b^3-1/3*x^4*Cos[a+b*x^2]/b-1/27*Cos[a+b*x^2]^3/b^3+2/3*x^2*Sin[a+b*x^2]/b^2-1/6*x^4*Cos[a+b*x^2]*Sin[a+b*x^2]^2/b+1/9*x^2*Sin[a+b*x^2]^3/b^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:152
  public void test0263() {
    check( //
        "Integrate[Sin[a+b/x]^2/x^4, x]", //
        "(-1/6)/x^3+1/4/(b^2*x)-1/4*Cos[a+b/x]*Sin[a+b/x]/b^3+1/2*Cos[a+b/x]*Sin[a+b/x]/(b*x^2)-1/2*Sin[a+b/x]^2/(b^2*x)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:184
  public void test0264() {
    check( //
        "Integrate[Sin[a+b*x^n], x]", //
        "1/2*I*E^(I*a)*x*Gamma[1/n,-I*b*x^n]/(n*(-I*b*x^n)^(1/n))-1/2*I*x*Gamma[1/n,I*b*x^n]/(E^(I*a)*n*(I*b*x^n)^(1/n))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:208
  public void test0265() {
    check( //
        "Integrate[(e+f*x)*Sin[b*(c+d*x)^2], x]", //
        "-1/2*f*Cos[b*(c+d*x)^2]/(b*d^2)+(d*e-c*f)*FresnelS[(c+d*x)*Sqrt[2/Pi]*Sqrt[b]]*Sqrt[1/2*Pi]/(d^2*Sqrt[b])");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:334
  public void test0266() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(2/3)]/(c*e+d*e*x)^(5/3), x]", //
        "3/2*(c+d*x)^(2/3)*Cos[a+b/(c+d*x)^(2/3)]/(b*d*e*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:388
  public void test0267() {
    check( //
        "Integrate[(e+f*x)*(a+b*Sin[c+d/x]), x]", //
        "a*e*x+1/2*a*f*x^2-b*d*e*CosIntegral[d/x]*Cos[c]+1/2*b*d*f*x*Cos[c+d/x]+1/2*b*d^2*f*Cos[c]*SinIntegral[d/x]+1/2*b*d^2*f*CosIntegral[d/x]*Sin[c]+b*d*e*SinIntegral[d/x]*Sin[c]+b*e*x*Sin[c+d/x]+1/2*b*f*x^2*Sin[c+d/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:427
  public void test0268() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(1/3)/x^2, x]", //
        "-(c*Sin[a+b*x]^3)^(1/3)/x+b*CosIntegral[b*x]*Cos[a]*Csc[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)-b*Csc[a+b*x]*SinIntegral[b*x]*Sin[a]*(c*Sin[a+b*x]^3)^(1/3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:447
  public void test0269() {
    check( //
        "Integrate[(c*Sin[a+b*x^n]^3)^(1/3)/x^2, x]", //
        "1/2*I*E^(I*a)*(-I*b*x^n)^(1/n)*Csc[a+b*x^n]*Gamma[(-1)/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*x)-1/2*I*(I*b*x^n)^(1/n)*Csc[a+b*x^n]*Gamma[(-1)/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*x)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:467
  public void test0270() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(2/3)/x^2, x]", //
        "-(c*Sin[a+b*x^2]^3)^(2/3)/x+Cos[2*a]*Csc[a+b*x^2]^2*FresnelS[2*x*Sqrt[b]/Sqrt[Pi]]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]*Sqrt[b]+Csc[a+b*x^2]^2*FresnelC[2*x*Sqrt[b]/Sqrt[Pi]]*Sin[2*a]*(c*Sin[a+b*x^2]^3)^(2/3)*Sqrt[Pi]*Sqrt[b]");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:43
  public void test0271() {
    check( //
        "Integrate[Csc[x]^3/(a+a*Sin[x])^3, x]", //
        "-13/2*ArcTanh[Cos[x]]/a^3+152/15*Cot[x]/a^3-13/2*Cot[x]*Csc[x]/a^3+1/5*Cot[x]*Csc[x]/(a+a*Sin[x])^3+11/15*Cot[x]*Csc[x]/(a*(a+a*Sin[x])^2)+76/15*Cot[x]*Csc[x]/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:63
  public void test0272() {
    check( //
        "Integrate[Sin[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2/5*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-8/5*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/5*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:81
  public void test0273() {
    check( //
        "Integrate[Sin[c+d*x]^2/Sqrt[a+a*Sin[c+d*x]], x]", //
        "-ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+4/3*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/3*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a*d)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:97
  public void test0274() {
    check( //
        "Integrate[Sin[c+d*x]^3/(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/4*Cos[c+d*x]*Sin[c+d*x]^2/(d*(a+a*Sin[c+d*x])^(5/2))-13/16*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))+75/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-9/4*Cos[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:213
  public void test0275() {
    check( //
        "Integrate[Csc[e+f*x]*(a+b*Sin[e+f*x])^3, x]", //
        "1/2*b*(6*a^2+b^2)*x-a^3*ArcTanh[Cos[e+f*x]]/f-5/2*a*b^2*Cos[e+f*x]/f-1/2*b^2*Cos[e+f*x]*(a+b*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:247
  public void test0276() {
    check( //
        "Integrate[1/(a+b*Sin[c+d*x])^4, x]", //
        "a*(2*a^2+3*b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(7/2)*d)+1/3*b*Cos[c+d*x]/((a^2-b^2)*d*(a+b*Sin[c+d*x])^3)+5/6*a*b*Cos[c+d*x]/((a^2-b^2)^2*d*(a+b*Sin[c+d*x])^2)+1/6*b*(11*a^2+4*b^2)*Cos[c+d*x]/((a^2-b^2)^3*d*(a+b*Sin[c+d*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:304
  public void test0277() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^4, x]", //
        "7/16*a^2*c^4*x+7/30*a^2*c^4*Cos[e+f*x]^5/f+7/16*a^2*c^4*Cos[e+f*x]*Sin[e+f*x]/f+7/24*a^2*c^4*Cos[e+f*x]^3*Sin[e+f*x]/f+1/6*a^2*Cos[e+f*x]^5*(c^4-c^4*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:320
  public void test0278() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x]), x]", //
        "-15/2*a^3*x/c+15/2*a^3*Cos[e+f*x]/(c*f)+2*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^3)+5/2*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:338
  public void test0279() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^5/(a+a*Sin[e+f*x])^2, x]", //
        "105/2*c^5*x/a^2+35*c^5*Cos[e+f*x]^3/(a^2*f)+105/2*c^5*Cos[e+f*x]*Sin[e+f*x]/(a^2*f)-2/3*a^4*c^5*Cos[e+f*x]^9/(f*(a+a*Sin[e+f*x])^6)+6*a^2*c^5*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^4)+42*c^5*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:354
  public void test0280() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^2), x]", //
        "-1/5*Sec[e+f*x]^3/(c^2*f*(a^3+a^3*Sin[e+f*x]))+4/5*Tan[e+f*x]/(a^3*c^2*f)+4/15*Tan[e+f*x]^3/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:374
  public void test0281() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^(1/2), x]", //
        "2/5*a^2*c^3*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:392
  public void test0282() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x]), x]", //
        "64/5*c^2*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f)+8/5*c*Sec[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f)+2/5*Sec[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a*f)-256/5*c^3*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:408
  public void test0283() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-256/5*c*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*f)+64*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(7/2)/(a^3*f)-24*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(9/2)/(a^3*c*f)+2*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(11/2)/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:460
  public void test0284() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(15/2), x]", //
        "1/14*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(15/2))+1/56*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(13/2))+1/280*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c^2*f*(c-c*Sin[e+f*x])^(11/2))+1/2240*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c^3*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:478
  public void test0285() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(7/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "3/2*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/2*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(f*(a+a*Sin[e+f*x])^(5/2))+6*c^4*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+3*c^3*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:539
  public void test0286() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c+d*Sin[e+f*x]), x]", //
        "1/2*a^3*(2*c^2-6*c*d+7*d^2)*x/d^3+1/2*a^3*(2*c-5*d)*Cos[e+f*x]/(d^2*f)-1/2*Cos[e+f*x]*(a^3+a^3*Sin[e+f*x])/(d*f)-2*a^3*(c-d)^3*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(d^3*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:557
  public void test0287() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^2, x]", //
        "d^2*x/a^2-1/3*(c-d)*(c+4*d)*Cos[e+f*x]/(a^2*f*(1+Sin[e+f*x]))-1/3*(c-d)*Cos[e+f*x]*(c+d*Sin[e+f*x])/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:573
  public void test0288() {
    check( //
        "Integrate[(A+B*Sin[x])/(1+Sin[x])^4, x]", //
        "-1/7*(A-B)*Cos[x]/(1+Sin[x])^4-1/35*(3*A+4*B)*Cos[x]/(1+Sin[x])^3-2/105*(3*A+4*B)*Cos[x]/(1+Sin[x])^2-2/105*(3*A+4*B)*Cos[x]/(1+Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:631
  public void test0289() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(1/2)/(c+d*Sin[e+f*x])^3, x]", //
        "-3/4*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/((c+d)^(5/2)*f*Sqrt[d])-1/2*a*Cos[e+f*x]/((c+d)*f*(c+d*Sin[e+f*x])^2*Sqrt[a+a*Sin[e+f*x]])-3/4*a*Cos[e+f*x]/((c+d)^2*f*(c+d*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:649
  public void test0290() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-(c-d)^2*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-4/3*(3*c-d)*d*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/3*d^2*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:665
  public void test0291() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/4*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^(5/2))-3/16*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-3/16*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:685
  public void test0292() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^(5/2), x]", //
        "2/3*a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-2/3*a^2*(c+5*d)*Cos[e+f*x]/(d*(c+d)^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:703
  public void test0293() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(3/2)), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/((c-d)^(3/2)*f*Sqrt[a])+2*d*Cos[e+f*x]/((c^2-d^2)*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:821
  public void test0294() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])^2/(a+b*Sin[e+f*x]), x]", //
        "d*(2*b*c-a*d)*x/b^2-d^2*Cos[e+f*x]/(b*f)+2*(b*c-a*d)^2*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/(b^2*f*Sqrt[a^2-b^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:921
  public void test0295() {
    check( //
        "Integrate[1/((a+b*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(1/2)), x]", //
        "2*EllipticF[ArcSin[Sqrt[c+d]*Sqrt[a+b*Sin[e+f*x]]/(Sqrt[a+b]*Sqrt[c+d*Sin[e+f*x]])],(a+b)*(c-d)/((a-b)*(c+d))]*Sec[e+f*x]*(c+d*Sin[e+f*x])*Sqrt[a+b]*Sqrt[(b*c-a*d)*(1-Sin[e+f*x])/((a+b)*(c+d*Sin[e+f*x]))]*Sqrt[-(b*c-a*d)*(1+Sin[e+f*x])/((a-b)*(c+d*Sin[e+f*x]))]/((b*c-a*d)*f*Sqrt[c+d])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:20
  public void test0296() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]]/Sqrt[c-c*Sin[e+f*x]], x]", //
        "1/2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:36
  public void test0297() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2), x]", //
        "1/6*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(3/2)/(a*f)+1/15*c^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])+2/15*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:52
  public void test0298() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(5/2), x]", //
        "Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(3/2))+4*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^2*f*Sqrt[c-c*Sin[e+f*x]])+4/3*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c^2*f*Sqrt[c-c*Sin[e+f*x]])+32*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+16*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:70
  public void test0299() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sqrt[c-c*Sin[e+f*x]]/(a+a*Sin[e+f*x])^(3/2), x]", //
        "2*c*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:130
  public void test0300() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]], x]", //
        "2/7*c*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-2/5*a^2*c*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+6/5*a^2*c*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-6/35*a*c*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:146
  public void test0301() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(13/2), x]", //
        "4/21*a*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*(c-c*Sin[e+f*x])^(13/2))+44/663*a^3*(g*Cos[e+f*x])^(5/2)/(c^2*f*g*(c-c*Sin[e+f*x])^(9/2)*Sqrt[a+a*Sin[e+f*x]])-22/1989*a^3*(g*Cos[e+f*x])^(5/2)/(c^3*f*g*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]])-22/3315*a^3*(g*Cos[e+f*x])^(5/2)/(c^4*f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-22/3315*a^3*(g*Cos[e+f*x])^(5/2)/(c^5*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-44/357*a^2*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(c*f*g*(c-c*Sin[e+f*x])^(11/2))+22/3315*a^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^6*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:164
  public void test0302() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "2*(g*Cos[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:180
  public void test0303() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]]), x]", //
        "-2/5*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])-2/5*(g*Cos[e+f*x])^(5/2)/(a*f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])-2/5*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:260
  public void test0304() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]*(a+a*Sin[c+d*x])^3, x]", //
        "a^3*Log[Sin[c+d*x]]/d+3*a^3*Sin[c+d*x]/d+3/2*a^3*Sin[c+d*x]^2/d+1/3*a^3*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:380
  public void test0305() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "-ArcTanh[Cos[c+d*x]]/(a^2*d)+2*Cos[c+d*x]/(a^2*d*(1+Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:400
  public void test0306() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]], x]", //
        "5/4*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d-1/4*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/2*Cot[c+d*x]*Csc[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:418
  public void test0307() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)-344/105*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-16/35*Cos[c+d*x]*Sin[c+d*x]^2/(a*d*Sqrt[a+a*Sin[c+d*x]])+2/7*Cos[c+d*x]*Sin[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])+76/105*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:466
  public void test0308() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+a*Sin[c+d*x]), x]", //
        "-1/16*a*ArcTanh[Cos[c+d*x]]/d-1/5*a*Cot[c+d*x]^5/d-1/16*a*Cot[c+d*x]*Csc[c+d*x]/d+1/8*a*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a*Cot[c+d*x]^3*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:482
  public void test0309() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^11*(a+a*Sin[c+d*x])^2, x]", //
        "-9/256*a^2*ArcTanh[Cos[c+d*x]]/d-2/5*a^2*Cot[c+d*x]^5/d-4/7*a^2*Cot[c+d*x]^7/d-2/9*a^2*Cot[c+d*x]^9/d-9/256*a^2*Cot[c+d*x]*Csc[c+d*x]/d-3/128*a^2*Cot[c+d*x]*Csc[c+d*x]^3/d+9/160*a^2*Cot[c+d*x]*Csc[c+d*x]^5/d-1/8*a^2*Cot[c+d*x]^3*Csc[c+d*x]^5/d+3/80*a^2*Cot[c+d*x]*Csc[c+d*x]^7/d-1/10*a^2*Cot[c+d*x]^3*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:498
  public void test0310() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^4, x]", //
        "55/256*a^4*x-11/112*a^4*Cos[c+d*x]^7/d+55/256*a^4*Cos[c+d*x]*Sin[c+d*x]/d+55/384*a^4*Cos[c+d*x]^3*Sin[c+d*x]/d+11/96*a^4*Cos[c+d*x]^5*Sin[c+d*x]/d-1/10*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^5/(a*d)-1/18*Cos[c+d*x]^7*(a^2+a^2*Sin[c+d*x])^2/d-11/144*Cos[c+d*x]^7*(a^4+a^4*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:516
  public void test0311() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "7/8*x/a^2+2*Cos[c+d*x]/(a^2*d)-2/3*Cos[c+d*x]^3/(a^2*d)-7/8*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/4*Cos[c+d*x]*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:553
  public void test0312() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-3*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d+4/35*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-Cot[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-2/7*Cos[c+d*x]*(a+a*Sin[c+d*x])^(5/2)/(a*d)+171/35*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+69/35*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:571
  public void test0313() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-4/385*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a^3*d)-4/165*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-2/231*Cos[c+d*x]*Sin[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])+14/33*Cos[c+d*x]*Sin[c+d*x]^4/(a*d*Sqrt[a+a*Sin[c+d*x]])+8/1155*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)-2/11*Cos[c+d*x]*Sin[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:587
  public void test0314() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4/(a+a*Sin[c+d*x])^(5/2), x]", //
        "45/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)-4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)-19/8*Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+13/12*Cot[c+d*x]*Csc[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:615
  public void test0315() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^10*(a+a*Sin[c+d*x]), x]", //
        "-1/6*a*Cot[c+d*x]^6/d-1/8*a*Cot[c+d*x]^8/d-1/5*a*Csc[c+d*x]^5/d+2/7*a*Csc[c+d*x]^7/d-1/9*a*Csc[c+d*x]^9/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:649
  public void test0316() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "-1/4*Cot[c+d*x]^4/(a*d)-Csc[c+d*x]/(a*d)+1/3*Csc[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:693
  public void test0317() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2*(a+a*Sin[c+d*x]), x]", //
        "5/128*a*x-1/7*a*Cos[c+d*x]^7/d+1/9*a*Cos[c+d*x]^9/d+5/128*a*Cos[c+d*x]*Sin[c+d*x]/d+5/192*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/48*a*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*a*Cos[c+d*x]^7*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:709
  public void test0318() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "13/256*a^2*x-2/7*a^2*Cos[c+d*x]^7/d+2/9*a^2*Cos[c+d*x]^9/d+13/256*a^2*Cos[c+d*x]*Sin[c+d*x]/d+13/384*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+13/480*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-13/80*a^2*Cos[c+d*x]^7*Sin[c+d*x]/d-1/10*a^2*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:725
  public void test0319() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "41/1024*a^3*x-4/7*a^3*Cos[c+d*x]^7/d+7/9*a^3*Cos[c+d*x]^9/d-3/11*a^3*Cos[c+d*x]^11/d+41/1024*a^3*Cos[c+d*x]*Sin[c+d*x]/d+41/1536*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d+41/1920*a^3*Cos[c+d*x]^5*Sin[c+d*x]/d-41/320*a^3*Cos[c+d*x]^7*Sin[c+d*x]/d-41/120*a^3*Cos[c+d*x]^7*Sin[c+d*x]^3/d-1/12*a^3*Cos[c+d*x]^7*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:741
  public void test0320() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^14*(a+a*Sin[c+d*x])^3, x]", //
        "27/1024*a^3*ArcTanh[Cos[c+d*x]]/d-4/7*a^3*Cot[c+d*x]^7/d-a^3*Cot[c+d*x]^9/d-6/11*a^3*Cot[c+d*x]^11/d-1/13*a^3*Cot[c+d*x]^13/d+27/1024*a^3*Cot[c+d*x]*Csc[c+d*x]/d+9/512*a^3*Cot[c+d*x]*Csc[c+d*x]^3/d-3/128*a^3*Cot[c+d*x]*Csc[c+d*x]^5/d+1/16*a^3*Cot[c+d*x]^3*Csc[c+d*x]^5/d-1/10*a^3*Cot[c+d*x]^5*Csc[c+d*x]^5/d-3/64*a^3*Cot[c+d*x]*Csc[c+d*x]^7/d+1/8*a^3*Cot[c+d*x]^3*Csc[c+d*x]^7/d-1/4*a^3*Cot[c+d*x]^5*Csc[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:759
  public void test0321() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*x/a^2+2*ArcTanh[Cos[c+d*x]]/(a^2*d)-2*Cos[c+d*x]/(a^2*d)-Cot[c+d*x]/(a^2*d)+1/2*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:791
  public void test0322() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^4*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cos[c+d*x]^8/d+1/5*a*Cos[c+d*x]^10/d-1/12*a*Cos[c+d*x]^12/d+1/5*a*Sin[c+d*x]^5/d-3/7*a*Sin[c+d*x]^7/d+1/3*a*Sin[c+d*x]^9/d-1/11*a*Sin[c+d*x]^11/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:807
  public void test0323() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^13*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cot[c+d*x]^8/d-1/5*a*Cot[c+d*x]^10/d-1/12*a*Cot[c+d*x]^12/d+1/5*a*Csc[c+d*x]^5/d-3/7*a*Csc[c+d*x]^7/d+1/3*a*Csc[c+d*x]^9/d-1/11*a*Csc[c+d*x]^11/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:825
  public void test0324() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^7/(a+a*Sin[c+d*x]), x]", //
        "-1/6*Cot[c+d*x]^6/(a*d)+Csc[c+d*x]/(a*d)-2/3*Csc[c+d*x]^3/(a*d)+1/5*Csc[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:855
  public void test0325() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "5/128*x/a+1/7*Cos[c+d*x]^7/(a*d)-1/9*Cos[c+d*x]^9/(a*d)+5/128*Cos[c+d*x]*Sin[c+d*x]/(a*d)+5/192*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)+1/48*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)-1/8*Cos[c+d*x]^7*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:871
  public void test0326() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "-3/64*x/a^2-2/5*Cos[c+d*x]^5/(a^2*d)+3/7*Cos[c+d*x]^7/(a^2*d)-1/9*Cos[c+d*x]^9/(a^2*d)-3/64*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-1/32*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)+1/8*Cos[c+d*x]^5*Sin[c+d*x]/(a^2*d)+1/4*Cos[c+d*x]^5*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:887
  public void test0327() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^3, x]", //
        "5/16*x/a^3+4/3*Cos[c+d*x]^3/(a^3*d)-Cos[c+d*x]^5/(a^3*d)+1/7*Cos[c+d*x]^7/(a^3*d)+5/16*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-5/8*Cos[c+d*x]^3*Sin[c+d*x]/(a^3*d)-1/2*Cos[c+d*x]^3*Sin[c+d*x]^3/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:947
  public void test0328() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "-x/a^3-3*Sec[c+d*x]/(a^3*d)+10/3*Sec[c+d*x]^3/(a^3*d)-11/5*Sec[c+d*x]^5/(a^3*d)+4/7*Sec[c+d*x]^7/(a^3*d)+Tan[c+d*x]/(a^3*d)-1/3*Tan[c+d*x]^3/(a^3*d)+1/5*Tan[c+d*x]^5/(a^3*d)-4/7*Tan[c+d*x]^7/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1115
  public void test0329() {
    check( //
        "Integrate[Sec[c+d*x]^7*Sin[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/128*ArcTanh[Sin[c+d*x]]/(a*d)-1/6*Sec[c+d*x]^6/(a*d)+1/8*Sec[c+d*x]^8/(a*d)-3/128*Sec[c+d*x]*Tan[c+d*x]/(a*d)-1/64*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+1/16*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)-1/8*Sec[c+d*x]^5*Tan[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1149
  public void test0330() {
    check( //
        "Integrate[Sec[c+d*x]^9*Sin[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "3/256*ArcTanh[Sin[c+d*x]]/(a*d)+1/6*Sec[c+d*x]^6/(a*d)-1/4*Sec[c+d*x]^8/(a*d)+1/10*Sec[c+d*x]^10/(a*d)+3/256*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/128*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)-1/32*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)+1/16*Sec[c+d*x]^5*Tan[c+d*x]^3/(a*d)-1/10*Sec[c+d*x]^5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1178
  public void test0331() {
    check( //
        "Integrate[Cos[e+f*x]*(a+a*Sin[e+f*x])^m*(c+d*Sin[e+f*x]), x]", //
        "(c-d)*(a+a*Sin[e+f*x])^(1+m)/(a*f*(1+m))+d*(a+a*Sin[e+f*x])^(2+m)/(a^2*f*(2+m))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1237
  public void test0332() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "1/16*a*(6*A+B)*x-1/30*a*(6*A+B)*Cos[c+d*x]^5/d+1/16*a*(6*A+B)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*a*(6*A+B)*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*B*Cos[c+d*x]^5*(a+a*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1253
  public void test0333() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/16*a^2*(7*A+2*B)*x-1/30*a^2*(7*A+2*B)*Cos[c+d*x]^5/d+1/16*a^2*(7*A+2*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*a^2*(7*A+2*B)*Cos[c+d*x]^3*Sin[c+d*x]/d-1/7*B*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2/d-1/42*(7*A+2*B)*Cos[c+d*x]^5*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1365
  public void test0334() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "-2*a*b*x+1/2*(a^2-2*b^2)*ArcTanh[Cos[c+d*x]]/d+3/2*b^2*Cos[c+d*x]/d-a*b*Cot[c+d*x]/d-1/2*Cot[c+d*x]*Csc[c+d*x]*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1383
  public void test0335() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]/(a+b*Sin[c+d*x])^2, x]", //
        "2*a*x/b^3+Cos[c+d*x]*(2*a+b*Sin[c+d*x])/(b^2*d*(a+b*Sin[c+d*x]))-2*(2*a^2-b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^3*d*Sqrt[a^2-b^2])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1428
  public void test0336() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^6*(a+b*Sin[c+d*x])^2, x]", //
        "b^2*x-3/4*a*b*ArcTanh[Cos[c+d*x]]/d-1/15*(3*a^4-14*a^2*b^2+b^4)*Cot[c+d*x]/(a^2*d)+1/60*b*(27*a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/30*(12*a^2-b^2)*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^2/(a^2*d)+1/10*b*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^3/(a^2*d)-1/5*Cot[c+d*x]*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1537
  public void test0337() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]*(a+b*Sin[c+d*x]), x]", //
        "-1/6*a*Cos[c+d*x]^6/d+1/3*b*Sin[c+d*x]^3/d-2/5*b*Sin[c+d*x]^5/d+1/7*b*Sin[c+d*x]^7/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1720
  public void test0338() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "-2*a^3*b*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(5/2)*d)+1/3*a*Sec[c+d*x]^3/((a^2-b^2)*d)-a^2*Sec[c+d*x]*(a-b*Sin[c+d*x])/((a^2-b^2)^2*d)-1/3*b*Tan[c+d*x]^3/((a^2-b^2)*d)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:42
  public void test0339() {
    check( //
        "Integrate[Sqrt[g*Sin[e+f*x]]/((c-c*Sin[e+f*x])*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "ArcTan[Cos[e+f*x]*Sqrt[a]*Sqrt[g]/(Sqrt[2]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[g]/(c*f*Sqrt[2]*Sqrt[a])+Sec[e+f*x]*Sqrt[g*Sin[e+f*x]]*Sqrt[a+a*Sin[e+f*x]]/(a*c*f)");
  }

  // 4.1.2.3 (g sin)^p (a+b sin)^m (c+d sin)^n.input:105
  public void test0340() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]/(Sin[e+f*x]*Sqrt[c+d*Sin[e+f*x]]), x]", //
        "-2*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c]/(Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[c])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:50
  public void test0341() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^4, x]", //
        "1/16*a^2*(7*A-2*B)*c^4*x+1/30*a^2*(7*A-2*B)*c^4*Cos[e+f*x]^5/f+1/16*a^2*(7*A-2*B)*c^4*Cos[e+f*x]*Sin[e+f*x]/f+1/24*a^2*(7*A-2*B)*c^4*Cos[e+f*x]^3*Sin[e+f*x]/f-1/7*a^2*B*Cos[e+f*x]^5*(c^2-c^2*Sin[e+f*x])^2/f+1/42*a^2*(7*A-2*B)*Cos[e+f*x]^5*(c^4-c^4*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:66
  public void test0342() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x]), x]", //
        "1/8*a^3*(5*A+2*B)*c*x-1/12*a^3*(5*A+2*B)*c*Cos[e+f*x]^3/f+1/8*a^3*(5*A+2*B)*c*Cos[e+f*x]*Sin[e+f*x]/f-1/5*a*B*c*Cos[e+f*x]^3*(a+a*Sin[e+f*x])^2/f-1/20*(5*A+2*B)*c*Cos[e+f*x]^3*(a^3+a^3*Sin[e+f*x])/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:84
  public void test0343() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])*(c-c*Sin[e+f*x])^4), x]", //
        "1/7*(A+B)*Sec[e+f*x]/(a*c*f*(c-c*Sin[e+f*x])^3)+1/35*(4*A-3*B)*Sec[e+f*x]/(a*f*(c^2-c^2*Sin[e+f*x])^2)+1/35*(4*A-3*B)*Sec[e+f*x]/(a*f*(c^4-c^4*Sin[e+f*x]))+2/35*(4*A-3*B)*Tan[e+f*x]/(a*c^4*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:100
  public void test0344() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])), x]", //
        "-1/5*(A-B)*Sec[e+f*x]/(a*c*f*(a+a*Sin[e+f*x])^2)-1/15*(3*A+2*B)*Sec[e+f*x]/(c*f*(a^3+a^3*Sin[e+f*x]))+2/15*(3*A+2*B)*Tan[e+f*x]/(a^3*c*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:120
  public void test0345() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(3/2), x]", //
        "8/315*a^2*(9*A+B)*c^4*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(5/2))+2/63*a^2*(9*A+B)*c^3*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(3/2))-2/9*a^2*B*c^2*Cos[e+f*x]^5/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:136
  public void test0346() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/10*a^3*(A+B)*c^3*Cos[e+f*x]^7/(f*(c-c*Sin[e+f*x])^(17/2))+1/80*a^3*(3*A-17*B)*c*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^(13/2))-1/96*a^3*(3*A-17*B)*Cos[e+f*x]^3/(c*f*(c-c*Sin[e+f*x])^(9/2))+1/128*a^3*(3*A-17*B)*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(5/2))-1/512*a^3*(3*A-17*B)*Cos[e+f*x]/(c^4*f*(c-c*Sin[e+f*x])^(3/2))-1/512*a^3*(3*A-17*B)*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]/(c^(11/2)*f*Sqrt[2])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:154
  public void test0347() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-2048/15*(A-3*B)*c^3*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(3/2)/(a^3*f)+512/5*(A-3*B)*c^2*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(5/2)/(a^3*f)-64/5*(A-3*B)*c*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(7/2)/(a^3*f)-16/15*(A-3*B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(9/2)/(a^3*f)-1/5*(A-3*B)*Sec[e+f*x]^3*(c-c*Sin[e+f*x])^(11/2)/(a^3*c*f)-1/5*(A-B)*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(15/2)/(a^3*c^3*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:174
  public void test0348() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(7/2), x]", //
        "-1/6*B*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(7/2)/f-1/30*a^2*(3*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(f*Sqrt[a+a*Sin[e+f*x]])-1/15*a*(3*A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:190
  public void test0349() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(5/2), x]", //
        "1/4*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(5/2))-1/4*a*(A+5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(3/2))-a^3*(A+5*B)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-1/2*a^2*(A+5*B)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:206
  public void test0350() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)*(A+B*Sin[e+f*x])/(c-c*Sin[e+f*x])^(13/2), x]", //
        "1/12*(A+B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(f*(c-c*Sin[e+f*x])^(13/2))+1/60*(A-5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(11/2))+1/480*(A-5*B)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c^2*f*(c-c*Sin[e+f*x])^(9/2))");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:224
  public void test0351() {
    check( //
        "Integrate[(A+B*Sin[e+f*x])*(c-c*Sin[e+f*x])^(9/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "1/4*(3*A-7*B)*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(7/2)/(a*f*(a+a*Sin[e+f*x])^(3/2))-1/4*(A-B)*Cos[e+f*x]*(c-c*Sin[e+f*x])^(9/2)/(f*(a+a*Sin[e+f*x])^(5/2))+(3*A-7*B)*c^3*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])+1/3*(3*A-7*B)*c^2*Cos[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a^2*f*Sqrt[a+a*Sin[e+f*x]])+8*(3*A-7*B)*c^5*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+4*(3*A-7*B)*c^4*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:306
  public void test0352() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x]), x]", //
        "1/8*a^2*(12*A*c+8*B*c+8*A*d+7*B*d)*x-1/6*a^2*(12*A*c+8*B*c+8*A*d+7*B*d)*Cos[e+f*x]/f-1/24*a^2*(12*A*c+8*B*c+8*A*d+7*B*d)*Cos[e+f*x]*Sin[e+f*x]/f-1/12*(4*B*c+4*A*d-B*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^2/f-1/4*B*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^3/(a*f)");
  }

  // 4.1.3.1 (a+b sin)^m (c+d sin)^n (A+B sin).input:361
  public void test0353() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)*(A+B*Sin[e+f*x])*(c+d*Sin[e+f*x]), x]", //
        "-2/105*a*(21*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-2/63*(9*B*c+9*A*d-2*B*d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/f-2/9*B*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*f)-64/315*a^3*(21*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-16/315*a^2*(21*A*c+15*B*c+15*A*d+13*B*d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.4.2 (a+b sin)^m (c+d sin)^n (A+B sin+C sin^2).input:20
  public void test0354() {
    check( //
        "Integrate[(A+C*Sin[e+f*x]^2)/((c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "1/4*(A+C)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(a*f*(c-c*Sin[e+f*x])^(3/2))-1/4*(A-3*C)*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+1/4*(A+C)*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:22
  public void test0355() {
    check( //
        "Integrate[1/(a*Sin[x]^3)^(3/2), x]", //
        "-10/21*Cos[x]/(a*Sqrt[a*Sin[x]^3])-2/7*Cot[x]*Csc[x]/(a*Sqrt[a*Sin[x]^3])-10/21*EllipticF[1/4*Pi-1/2*x,2]*Sin[x]^(3/2)/(a*Sqrt[a*Sin[x]^3])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:67
  public void test0356() {
    check( //
        "Integrate[Sin[c+d*x]/(a-a*Sin[c+d*x]^2), x]", //
        "Sec[c+d*x]/(a*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:103
  public void test0357() {
    check( //
        "Integrate[a+b*Sin[c+d*x]^2, x]", //
        "a*x+1/2*b*x-1/2*b*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:137
  public void test0358() {
    check( //
        "Integrate[Sin[c+d*x]^2/(a+b*Sin[c+d*x]^2)^2, x]", //
        "-1/2*Cos[c+d*x]*Sin[c+d*x]/((a+b)*d*(a+b*Sin[c+d*x]^2))+1/2*ArcTan[Sqrt[a+b]*Tan[c+d*x]/Sqrt[a]]/((a+b)^(3/2)*d*Sqrt[a])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:156
  public void test0359() {
    check( //
        "Integrate[1/(a-a*Sin[x]^2)^(1/2), x]", //
        "ArcTanh[Sin[x]]*Cos[x]/Sqrt[a*Cos[x]^2]");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:361
  public void test0360() {
    check( //
        "Integrate[Cos[x]^6/(a-a*Sin[x]^2)^2, x]", //
        "1/2*x/a^2+1/2*Cos[x]*Sin[x]/a^2");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:381
  public void test0361() {
    check( //
        "Integrate[Sec[e+f*x]^2*(a+b*Sin[e+f*x]^2)^2, x]", //
        "-1/2*b*(4*a+3*b)*x+1/2*b^2*Cos[e+f*x]*Sin[e+f*x]/f+(a+b)^2*Tan[e+f*x]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:421
  public void test0362() {
    check( //
        "Integrate[Sec[e+f*x]^5*Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "1/8*a*(3*a+4*b)*ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/((a+b)^(3/2)*f)+1/4*Sec[e+f*x]^3*(a+b*Sin[e+f*x]^2)^(3/2)*Tan[e+f*x]/((a+b)*f)+1/8*(3*a+4*b)*Sec[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]*Tan[e+f*x]/((a+b)*f)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:443
  public void test0363() {
    check( //
        "Integrate[Sec[e+f*x]^3/Sqrt[a+b*Sin[e+f*x]^2], x]", //
        "1/2*(a+2*b)*ArcTanh[Sin[e+f*x]*Sqrt[a+b]/Sqrt[a+b*Sin[e+f*x]^2]]/((a+b)^(3/2)*f)+1/2*Sec[e+f*x]*Sqrt[a+b*Sin[e+f*x]^2]*Tan[e+f*x]/((a+b)*f)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:488
  public void test0364() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+b*Sin[c+d*x]^3), x]", //
        "1/3*(a^(4/3)+b^(4/3))*Log[a^(1/3)+b^(1/3)*Sin[c+d*x]]/(a^(2/3)*b^(5/3)*d)-1/6*(a^(4/3)+b^(4/3))*Log[a^(2/3)-a^(1/3)*b^(1/3)*Sin[c+d*x]+b^(2/3)*Sin[c+d*x]^2]/(a^(2/3)*b^(5/3)*d)-2/3*Log[a+b*Sin[c+d*x]^3]/(b*d)+1/2*Sin[c+d*x]^2/(b*d)+(a^(4/3)-b^(4/3))*ArcTan[(a^(1/3)-2*b^(1/3)*Sin[c+d*x])/(a^(1/3)*Sqrt[3])]/(a^(2/3)*b^(5/3)*d*Sqrt[3])");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:517
  public void test0365() {
    check( //
        "Integrate[Cos[c+d*x]^7/(a-b*Sin[c+d*x]^4), x]", //
        "-3*Sin[c+d*x]/(b*d)+1/3*Sin[c+d*x]^3/(b*d)-1/2*ArcTanh[b^(1/4)*Sin[c+d*x]/a^(1/4)]*(Sqrt[a]-Sqrt[b])^3/(a^(3/4)*b^(7/4)*d)+1/2*ArcTan[b^(1/4)*Sin[c+d*x]/a^(1/4)]*(Sqrt[a]+Sqrt[b])^3/(a^(3/4)*b^(7/4)*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:569
  public void test0366() {
    check( //
        "Integrate[Tan[c+d*x]^5/(a+b*Sin[c+d*x]^2), x]", //
        "-a^2*Log[Cos[c+d*x]]/((a+b)^3*d)+1/2*a^2*Log[a+b*Sin[c+d*x]^2]/((a+b)^3*d)-1/2*(2*a+b)*Sec[c+d*x]^2/((a+b)^2*d)+1/4*Sec[c+d*x]^4/((a+b)*d)");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:589
  public void test0367() {
    check( //
        "Integrate[Sqrt[a-a*Sin[e+f*x]^2]*Tan[e+f*x]^3, x]", //
        "a/(f*Sqrt[a*Cos[e+f*x]^2])+Sqrt[a*Cos[e+f*x]^2]/f");
  }

  // 4.1.7 (d trig)^m (a+b (c sin)^n)^p.input:607
  public void test0368() {
    check( //
        "Integrate[Tan[e+f*x]^2/Sqrt[a-a*Sin[e+f*x]^2], x]", //
        "-1/2*ArcTanh[Sin[e+f*x]]*Cos[e+f*x]/(f*Sqrt[a*Cos[e+f*x]^2])+1/2*Tan[e+f*x]/(f*Sqrt[a*Cos[e+f*x]^2])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:27
  public void test0369() {
    check( //
        "Integrate[1/Cos[a+b*x]^(7/2), x]", //
        "-6/5*EllipticE[1/2*(a+b*x),2]/b+2/5*Sin[a+b*x]/(b*Cos[a+b*x]^(5/2))+6/5*Sin[a+b*x]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:65
  public void test0370() {
    check( //
        "Integrate[(a*Cos[x]^3)^(3/2), x]", //
        "14/15*a*EllipticE[1/2*x,2]*Sqrt[a*Cos[x]^3]/Cos[x]^(3/2)+14/45*a*Sin[x]*Sqrt[a*Cos[x]^3]+2/9*a*Cos[x]^2*Sin[x]*Sqrt[a*Cos[x]^3]");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:103
  public void test0371() {
    check( //
        "Integrate[Cos[c+d*x]*Sqrt[b*Cos[c+d*x]], x]", //
        "2/3*b*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:119
  public void test0372() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*Sec[c+d*x]^4, x]", //
        "2/3*b^3*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2/3*b^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:135
  public void test0373() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(7/2), x]", //
        "2/7*b*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+10/21*b^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*b^3*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:153
  public void test0374() {
    check( //
        "Integrate[Cos[c+d*x]^4/(b*Cos[c+d*x])^(3/2), x]", //
        "2/5*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^3*d)+6/5*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:169
  public void test0375() {
    check( //
        "Integrate[Cos[c+d*x]/(b*Cos[c+d*x])^(5/2), x]", //
        "2*Sin[c+d*x]/(b^2*d*Sqrt[b*Cos[c+d*x]])-2*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:189
  public void test0376() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(b*Cos[c+d*x])^(3/2), x]", //
        "1/4*b*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+3/8*b*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+3/8*b*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.0 (a cos)^m (b trg)^n.input:223
  public void test0377() {
    check( //
        "Integrate[Cos[c+d*x]^(11/2)/(b*Cos[c+d*x])^(3/2), x]", //
        "3/8*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/4*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+3/8*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.1.1 (a+b cos)^n.input:74
  public void test0378() {
    check( //
        "Integrate[1/(a+b*Cos[c+d*x])^(1/2), x]", //
        "2*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.1.2 (g sin)^p (a+b cos)^m.input:25
  public void test0379() {
    check( //
        "Integrate[Sin[x]^3/(1+Cos[x])^2, x]", //
        "Cos[x]-2*Log[1+Cos[x]]");
  }

  // 4.2.1.2 (g sin)^p (a+b cos)^m.input:55
  public void test0380() {
    check( //
        "Integrate[Csc[x]^4/(a+b*Cos[x]), x]", //
        "2*b^4*ArcTan[Sqrt[a-b]*Tan[1/2*x]/Sqrt[a+b]]/((a-b)^(5/2)*(a+b)^(5/2))-1/3*(3*b^3+a*(2*a^2-5*b^2)*Cos[x])*Csc[x]/(a^2-b^2)^2+1/3*(b-a*Cos[x])*Csc[x]^3/(a^2-b^2)");
  }

  // 4.2.1.3 (g tan)^p (a+b cos)^m.input:16
  public void test0381() {
    check( //
        "Integrate[Cot[x]^3/(a+a*Cos[x]), x]", //
        "3/8*ArcTanh[Cos[x]]/a-1/4*Cot[x]^4/a-3/8*Cot[x]*Csc[x]/a+1/4*Cot[x]^3*Csc[x]/a");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:14
  public void test0382() {
    check( //
        "Integrate[(c+d*x)^2*Cos[a+b*x], x]", //
        "2*d*(c+d*x)*Cos[a+b*x]/b^2-2*d^2*Sin[a+b*x]/b^3+(c+d*x)^2*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:30
  public void test0383() {
    check( //
        "Integrate[(c+d*x)*Cos[a+b*x]^3, x]", //
        "2/3*d*Cos[a+b*x]/b^2+1/9*d*Cos[a+b*x]^3/b^2+2/3*(c+d*x)*Sin[a+b*x]/b+1/3*(c+d*x)*Cos[a+b*x]^2*Sin[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:50
  public void test0384() {
    check( //
        "Integrate[(c+d*x)^3*Sec[a+b*x]^3, x]", //
        "-6*I*d^2*(c+d*x)*ArcTan[E^(I*(a+b*x))]/b^3-I*(c+d*x)^3*ArcTan[E^(I*(a+b*x))]/b+3*I*d^3*PolyLog[2,-I*E^(I*(a+b*x))]/b^4+3/2*I*d*(c+d*x)^2*PolyLog[2,-I*E^(I*(a+b*x))]/b^2-3*I*d^3*PolyLog[2,I*E^(I*(a+b*x))]/b^4-3/2*I*d*(c+d*x)^2*PolyLog[2,I*E^(I*(a+b*x))]/b^2-3*d^2*(c+d*x)*PolyLog[3,-I*E^(I*(a+b*x))]/b^3+3*d^2*(c+d*x)*PolyLog[3,I*E^(I*(a+b*x))]/b^3-3*I*d^3*PolyLog[4,-I*E^(I*(a+b*x))]/b^4+3*I*d^3*PolyLog[4,I*E^(I*(a+b*x))]/b^4-3/2*d*(c+d*x)^2*Sec[a+b*x]/b^2+1/2*(c+d*x)^3*Sec[a+b*x]*Tan[a+b*x]/b");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:93
  public void test0385() {
    check( //
        "Integrate[Cos[a+b*x]/(c+d*x)^(1/3), x]", //
        "-1/2*I*E^(I*(a-b*c/d))*(-I*b*(c+d*x)/d)^(1/3)*Gamma[2/3,-I*b*(c+d*x)/d]/(b*(c+d*x)^(1/3))+1/2*I*(I*b*(c+d*x)/d)^(1/3)*Gamma[2/3,I*b*(c+d*x)/d]/(E^(I*(a-b*c/d))*b*(c+d*x)^(1/3))");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:124
  public void test0386() {
    check( //
        "Integrate[x/Sec[x]^(5/2)-3/5*x/Sqrt[Sec[x]], x]", //
        "4/25/Sec[x]^(5/2)+2/5*x*Sin[x]/Sec[x]^(3/2)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:171
  public void test0387() {
    check( //
        "Integrate[(c+d*x)/(a+a*Cos[e+f*x]), x]", //
        "2*d*Log[Cos[1/2*e+1/2*f*x]]/(a*f^2)+(c+d*x)*Tan[1/2*e+1/2*f*x]/(a*f)");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:197
  public void test0388() {
    check( //
        "Integrate[x*Sqrt[a+a*Cos[x]], x]", //
        "4*Sqrt[a+a*Cos[x]]+2*x*Sqrt[a+a*Cos[x]]*Tan[1/2*x]");
  }

  // 4.2.10 (c+d x)^m (a+b cos)^n.input:213
  public void test0389() {
    check( //
        "Integrate[(a+a*Cos[x])^(3/2)/x^2, x]", //
        "-2*a*Cos[1/2*x]^2*Sqrt[a+a*Cos[x]]/x-3/4*a*Sec[1/2*x]*SinIntegral[1/2*x]*Sqrt[a+a*Cos[x]]-3/4*a*Sec[1/2*x]*SinIntegral[3/2*x]*Sqrt[a+a*Cos[x]]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:73
  public void test0390() {
    check( //
        "Integrate[Cos[Sqrt[x]]^2, x]", //
        "1/2*x+1/2*Cos[Sqrt[x]]^2+Cos[Sqrt[x]]*Sin[Sqrt[x]]*Sqrt[x]");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:99
  public void test0391() {
    check( //
        "Integrate[Cos[a+b*x^n]^2/x, x]", //
        "1/2*CosIntegral[2*b*x^n]*Cos[2*a]/n+1/2*Log[x]-1/2*SinIntegral[2*b*x^n]*Sin[2*a]/n");
  }

  // 4.2.12 (e x)^m (a+b cos(c+d x^n))^p.input:119
  public void test0392() {
    check( //
        "Integrate[x*Cos[(a+b*x)^2], x]", //
        "1/2*Sin[(a+b*x)^2]/b^2-a*FresnelC[(a+b*x)*Sqrt[2/Pi]]*Sqrt[1/2*Pi]/b^2");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:24
  public void test0393() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Cos[c+d*x])^2, x]", //
        "11/16*a^2*x+2*a^2*Sin[c+d*x]/d+11/16*a^2*Cos[c+d*x]*Sin[c+d*x]/d+11/24*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-4/3*a^2*Sin[c+d*x]^3/d+2/5*a^2*Sin[c+d*x]^5/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:42
  public void test0394() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*Sec[c+d*x]^5, x]", //
        "15/8*a^3*ArcTanh[Sin[c+d*x]]/d+4*a^3*Tan[c+d*x]/d+15/8*a^3*Sec[c+d*x]*Tan[c+d*x]/d+1/4*a^3*Sec[c+d*x]^3*Tan[c+d*x]/d+a^3*Tan[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:61
  public void test0395() {
    check( //
        "Integrate[1/(a+a*Cos[c+d*x]), x]", //
        "Sin[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:93
  public void test0396() {
    check( //
        "Integrate[Sec[c+d*x]^2/(a+a*Cos[c+d*x])^4, x]", //
        "-4*ArcTanh[Sin[c+d*x]]/(a^4*d)+664/105*Tan[c+d*x]/(a^4*d)-88/105*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-4*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-12/35*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:113
  public void test0397() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(1/2), x]", //
        "12/35*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a*d)+4/5*a*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/7*a*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-8/35*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:129
  public void test0398() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Cos[c+d*x])^(5/2), x]", //
        "284/231*a*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+284/99*a^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+710/693*a^3*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+46/99*a^3*Cos[c+d*x]^4*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-568/693*a^2*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+2/11*a^2*Cos[c+d*x]^4*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:147
  public void test0399() {
    check( //
        "Integrate[Sec[c+d*x]^2/Sqrt[a+a*Cos[c+d*x]], x]", //
        "-ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])+ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:163
  public void test0400() {
    check( //
        "Integrate[Sec[c+d*x]/(a+a*Cos[c+d*x])^(5/2), x]", //
        "2*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(a^(5/2)*d)-1/4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-11/16*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-43/16*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:183
  public void test0401() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^3, x]", //
        "68/15*a^3*EllipticE[1/2*(c+d*x),2]/d+44/21*a^3*EllipticF[1/2*(c+d*x),2]/d+68/45*a^3*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+6/7*a^3*Cos[c+d*x]^(5/2)*Sin[c+d*x]/d+2/9*a^3*Cos[c+d*x]^(7/2)*Sin[c+d*x]/d+44/21*a^3*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:201
  public void test0402() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)/(a+a*Cos[c+d*x]), x]", //
        "3*EllipticE[1/2*(c+d*x),2]/(a*d)-EllipticF[1/2*(c+d*x),2]/(a*d)-Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:217
  public void test0403() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)/(a+a*Cos[c+d*x])^3, x]", //
        "-9/10*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/2*EllipticF[1/2*(c+d*x),2]/(a^3*d)-1/5*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-2/5*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^2)+9/10*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:237
  public void test0404() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)/Cos[c+d*x]^(1/2), x]", //
        "3*a^(3/2)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+a^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:255
  public void test0405() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)/(a+a*Cos[c+d*x])^(1/2), x]", //
        "7/4*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/(d*Sqrt[a])-ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])+1/2*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/4*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:271
  public void test0406() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:287
  public void test0407() {
    check( //
        "Integrate[1/(Cos[c+d*x]^(1/2)*(a+a*Cos[c+d*x])^(7/2)), x]", //
        "63/64*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(7/2)*d*Sqrt[2])-1/6*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^(7/2))-5/16*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d*(a+a*Cos[c+d*x])^(5/2))-103/192*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(a+a*Cos[c+d*x])^(3/2))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:307
  public void test0408() {
    check( //
        "Integrate[(1-Cos[c+d*x])^(1/2)/Cos[c+d*x]^(3/2), x]", //
        "2*Sin[c+d*x]/(d*Sqrt[1-Cos[c+d*x]]*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:492
  public void test0409() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*Sec[c+d*x]^6, x]", //
        "3/4*a*b*ArcTanh[Sin[c+d*x]]/d+1/5*(4*a^2+5*b^2)*Tan[c+d*x]/d+3/4*a*b*Sec[c+d*x]*Tan[c+d*x]/d+1/2*a*b*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*a^2*Sec[c+d*x]^4*Tan[c+d*x]/d+1/15*(4*a^2+5*b^2)*Tan[c+d*x]^3/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:510
  public void test0410() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^4*Sec[c+d*x]^4, x]", //
        "b^4*x+2*a*b*(a^2+2*b^2)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(2*a^2+17*b^2)*Tan[c+d*x]/d+4/3*a^3*b*Sec[c+d*x]*Tan[c+d*x]/d+1/3*a^2*(a+b*Cos[c+d*x])^2*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:529
  public void test0411() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+b*Cos[c+d*x])^2, x]", //
        "x/b^2-2*a*(a^2-2*b^2)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/((a-b)^(3/2)*b^2*(a+b)^(3/2)*d)-a^2*Sin[c+d*x]/(b*(a^2-b^2)*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:649
  public void test0412() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2/Cos[c+d*x]^(3/2), x]", //
        "-2*(a^2-b^2)*EllipticE[1/2*(c+d*x),2]/d+4*a*b*EllipticF[1/2*(c+d*x),2]/d+2*a^2*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:899
  public void test0413() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(-1/2*B+B*Cos[c+d*x]), x]", //
        "1/2*a*B*Sin[c+d*x]/d+1/2*a*B*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:939
  public void test0414() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])*Sec[c+d*x]^3, x]", //
        "2/3*A*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2*b*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*A*b*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:957
  public void test0415() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(1/2), x]", //
        "2/5*A*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^2*d)+2/7*B*(b*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(b^3*d)+10/21*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+10/21*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)+6/5*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:973
  public void test0416() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(5/2), x]", //
        "2/3*B*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b^2*d*Sqrt[b*Cos[c+d*x]])+2/3*B*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b^3*d)+2*A*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:993
  public void test0417() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(1/2), x]", //
        "1/2*b*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*b*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/2*b*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.2.1 (a+b cos)^m (c+d cos)^n.input:1011
  public void test0418() {
    check( //
        "Integrate[Cos[c+d*x]^(1/2)*(A+B*Cos[c+d*x])/(b*Cos[c+d*x])^(1/2), x]", //
        "A*x*Sqrt[Cos[c+d*x]]/Sqrt[b*Cos[c+d*x]]+B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:40
  public void test0419() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]), x]", //
        "7/16*a^4*(8*A+7*B)*x+4/5*a^4*(8*A+7*B)*Sin[c+d*x]/d+27/80*a^4*(8*A+7*B)*Cos[c+d*x]*Sin[c+d*x]/d+1/40*a^4*(8*A+7*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/30*(6*A-B)*(a+a*Cos[c+d*x])^4*Sin[c+d*x]/d+1/6*B*(a+a*Cos[c+d*x])^5*Sin[c+d*x]/(a*d)-2/15*a^4*(8*A+7*B)*Sin[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:59
  public void test0420() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]^4/(a+a*Cos[c+d*x]), x]", //
        "-3/2*(A-B)*ArcTanh[Sin[c+d*x]]/(a*d)+(4*A-3*B)*Tan[c+d*x]/(a*d)-3/2*(A-B)*Sec[c+d*x]*Tan[c+d*x]/(a*d)-(A-B)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))+1/3*(4*A-3*B)*Tan[c+d*x]^3/(a*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:75
  public void test0421() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+a*Cos[c+d*x])^3, x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a^3*d)-1/5*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(7*A-2*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-2/15*(11*A-B)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:95
  public void test0422() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(1/2)*(A+B*Cos[c+d*x])*Sec[c+d*x], x]", //
        "2*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+2*a*B*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:111
  public void test0423() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])*Sec[c+d*x], x]", //
        "2*a^(5/2)*A*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/5*a*B*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/15*a^3*(35*A+32*B)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/15*a^2*(5*A+8*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:129
  public void test0424() {
    check( //
        "Integrate[Cos[c+d*x]*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^(3/2), x]", //
        "-1/2*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*(3*A-7*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+2*B*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:149
  public void test0425() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x])/Sqrt[Cos[c+d*x]], x]", //
        "2*a*(A+B)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(3*A+B)*EllipticF[1/2*(c+d*x),2]/d+2/3*a*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:165
  public void test0426() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "-4/5*a^3*(9*A+5*B)*EllipticE[1/2*(c+d*x),2]/d+4/3*a^3*(3*A+5*B)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*A*(a+a*Cos[c+d*x])^2*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/15*(9*A+5*B)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+4/15*a^3*(21*A+20*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:183
  public void test0427() {
    check( //
        "Integrate[Cos[c+d*x]^(9/2)*(A+B*Cos[c+d*x])/(a+a*Cos[c+d*x])^3, x]", //
        "-7/10*(17*A-33*B)*EllipticE[1/2*(c+d*x),2]/(a^3*d)+1/2*(11*A-21*B)*EllipticF[1/2*(c+d*x),2]/(a^3*d)-7/30*(17*A-33*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(a^3*d)+1/5*(A-B)*Cos[c+d*x]^(9/2)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)+1/15*(7*A-12*B)*Cos[c+d*x]^(7/2)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)+3/10*(11*A-21*B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))+1/2*(11*A-21*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:203
  public void test0428() {
    check( //
        "Integrate[Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "1/64*a^(3/2)*(88*A+75*B)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+1/96*a^2*(88*A+75*B)*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/24*a^2*(8*A+9*B)*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/64*a^2*(88*A+75*B)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*a*B*Cos[c+d*x]^(5/2)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:219
  public void test0429() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x])/Cos[c+d*x]^(13/2), x]", //
        "2/11*a*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(11/2))+2/693*a^3*(194*A+209*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])+2/1155*a^3*(710*A+803*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+8/3465*a^3*(710*A+803*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+16/3465*a^3*(710*A+803*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/99*a^2*(14*A+11*B)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(9/2))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:237
  public void test0430() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(5/2)), x]", //
        "-1/16*(75*A-19*B)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])-1/4*(A-B)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2)*Sqrt[Cos[c+d*x]])-1/16*(13*A-5*B)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2)*Sqrt[Cos[c+d*x]])+1/16*(49*A-9*B)*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:259
  public void test0431() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "1/8*(3*a*A+4*b*B)*ArcTanh[Sin[c+d*x]]/d+(A*b+a*B)*Tan[c+d*x]/d+1/8*(3*a*A+4*b*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/4*a*A*Sec[c+d*x]^3*Tan[c+d*x]/d+1/3*(A*b+a*B)*Tan[c+d*x]^3/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:275
  public void test0432() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(A+B*Cos[c+d*x])*Sec[c+d*x]^5, x]", //
        "1/8*(3*a^3*A+12*a*A*b^2+12*a^2*b*B+8*b^3*B)*ArcTanh[Sin[c+d*x]]/d+1/3*(6*a^2*A*b+3*A*b^3+2*a^3*B+9*a*b^2*B)*Tan[c+d*x]/d+1/8*a*(3*a^2*A+10*A*b^2+12*a*b*B)*Sec[c+d*x]*Tan[c+d*x]/d+1/6*a^2*(3*A*b+2*a*B)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*a*A*(a+b*Cos[c+d*x])^2*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:293
  public void test0433() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+b*Cos[c+d*x]), x]", //
        "A*ArcTanh[Sin[c+d*x]]/(a*d)-2*(A*b-a*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:309
  public void test0434() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])*Sec[c+d*x]/(a+b*Cos[c+d*x])^3, x]", //
        "-(6*a^4*A*b-5*a^2*A*b^3+2*A*b^5-2*a^5*B-a^3*b^2*B)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(a^3*(a-b)^(5/2)*(a+b)^(5/2)*d)+A*ArcTanh[Sin[c+d*x]]/(a^3*d)+1/2*b*(A*b-a*B)*Sin[c+d*x]/(a*(a^2-b^2)*d*(a+b*Cos[c+d*x])^2)+1/2*b*(5*a^2*A*b-2*A*b^3-3*a^3*B)*Sin[c+d*x]/(a^2*(a^2-b^2)^2*d*(a+b*Cos[c+d*x]))");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:325
  public void test0435() {
    check( //
        "Integrate[(a*B+b*B*Cos[c+d*x])*Sec[c+d*x]^2/(a+b*Cos[c+d*x]), x]", //
        "B*Tan[c+d*x]/d");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:349
  public void test0436() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]), x]", //
        "2/5*B*(a+b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/15*(5*A*b+3*a*B)*Sin[c+d*x]*Sqrt[a+b*Cos[c+d*x]]/d+2/15*(20*a*A*b+3*a^2*B+9*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/15*(a^2-b^2)*(5*A*b+3*a*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:382
  public void test0437() {
    check( //
        "Integrate[(A+B*Cos[c+d*x])/(a+b*Cos[c+d*x])^(5/2), x]", //
        "-2/3*(A*b-a*B)*Sin[c+d*x]/((a^2-b^2)*d*(a+b*Cos[c+d*x])^(3/2))-2/3*(4*a*A*b-a^2*B-3*b^2*B)*Sin[c+d*x]/((a^2-b^2)^2*d*Sqrt[a+b*Cos[c+d*x]])+2/3*(4*a*A*b-a^2*B-3*b^2*B)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)^2*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])-2/3*(A*b-a*B)*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*(a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.3.1 (a+b cos)^m (c+d cos)^n (A+B cos).input:407
  public void test0438() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^2*(A+B*Cos[c+d*x])/Cos[c+d*x]^(7/2), x]", //
        "-2/5*(3*a^2*A+5*A*b^2+10*a*b*B)*EllipticE[1/2*(c+d*x),2]/d+2/3*(2*a*A*b+a^2*B+3*b^2*B)*EllipticF[1/2*(c+d*x),2]/d+2/5*a^2*A*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/3*a*(2*A*b+a*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*(3*a^2*A+5*A*b^2+10*a*b*B)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:27
  public void test0439() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2), x]", //
        "2/45*b*(9*A+7*C)*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+2/9*C*(b*Cos[c+d*x])^(7/2)*Sin[c+d*x]/(b*d)+2/15*b^2*(9*A+7*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:45
  public void test0440() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Sec[c+d*x])^(3/2), x]", //
        "2/21*(7*A+5*C)*Sin[c+d*x]/(b*d*Sqrt[b*Sec[c+d*x]])+2/21*(7*A+5*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]*Sqrt[b*Sec[c+d*x]]/(b^2*d)+2/7*b^2*C*Tan[c+d*x]/(d*(b*Sec[c+d*x])^(7/2))");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:70
  public void test0441() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(3/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^3, x]", //
        "2*A*b^2*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])-2*b*(A-C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:88
  public void test0442() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(1/2), x]", //
        "2/3*(3*A+C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])+2/3*C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(b*d)");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:104
  public void test0443() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(5/2), x]", //
        "2/5*C*(b*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(b^4*d)+2/5*(5*A+3*C)*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:351
  public void test0444() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3*Sqrt[b*Cos[c+d*x]], x]", //
        "2/3*A*b^2*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2*b*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b*(A+3*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:367
  public void test0445() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^5, x]", //
        "2/3*A*b^4*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2*b^3*B*Sin[c+d*x]/(d*Sqrt[b*Cos[c+d*x]])+2/3*b^3*(A+3*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[b*Cos[c+d*x]])-2*b^2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:385
  public void test0446() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(b*Cos[c+d*x])^(3/2), x]", //
        "2/3*A*Sin[c+d*x]/(d*(b*Cos[c+d*x])^(3/2))+2*B*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+2/3*(A+3*C)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])-2*B*EllipticE[1/2*(c+d*x),2]*Sqrt[b*Cos[c+d*x]]/(b^2*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:405
  public void test0447() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[b*Cos[c+d*x]]/Cos[c+d*x]^(3/2), x]", //
        "B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+A*ArcTanh[Sin[c+d*x]]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+C*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:421
  public void test0448() {
    check( //
        "Integrate[(b*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "1/3*b^2*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/d+1/2*b^2*B*x*Sqrt[b*Cos[c+d*x]]/Sqrt[Cos[c+d*x]]+1/3*b^2*(3*A+2*C)*Sin[c+d*x]*Sqrt[b*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])+1/2*b^2*B*Sin[c+d*x]*Sqrt[Cos[c+d*x]]*Sqrt[b*Cos[c+d*x]]/d");
  }

  // 4.2.4.1 (a+b cos)^m (A+B cos+C cos^2).input:439
  public void test0449() {
    check( //
        "Integrate[Cos[c+d*x]^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(b*Cos[c+d*x])^(3/2), x]", //
        "1/2*B*Cos[c+d*x]^(3/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/3*C*Cos[c+d*x]^(5/2)*Sin[c+d*x]/(b*d*Sqrt[b*Cos[c+d*x]])+1/2*B*x*Sqrt[Cos[c+d*x]]/(b*Sqrt[b*Cos[c+d*x]])+1/3*(3*A+2*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(b*d*Sqrt[b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:13
  public void test0450() {
    check( //
        "Integrate[Cos[c+d*x]*(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2), x]", //
        "1/8*a*(4*A+3*C)*x+1/3*a*(3*A+2*C)*Sin[c+d*x]/d+1/8*a*(4*A+3*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/3*a*C*Cos[c+d*x]^2*Sin[c+d*x]/d+1/4*a*C*Cos[c+d*x]^3*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:29
  public void test0451() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2), x]", //
        "1/16*a^3*(26*A+21*C)*x+1/35*a^3*(133*A+108*C)*Sin[c+d*x]/d+1/16*a^3*(26*A+21*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/280*a^3*(154*A+129*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/7*C*Cos[c+d*x]^3*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/d+1/14*C*Cos[c+d*x]^3*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d)+1/5*(A+C)*Cos[c+d*x]^3*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d-1/105*a^3*(133*A+108*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:45
  public void test0452() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "1/2*a^4*(2*A+13*C)*x+2*a^4*(3*A+2*C)*ArcTanh[Sin[c+d*x]]/d-5/2*a^4*(2*A-C)*Sin[c+d*x]/d-1/6*(22*A+3*C)*(a^4+a^4*Cos[c+d*x])*Sin[c+d*x]/d+1/3*(8*A+3*C)*(a^2+a^2*Cos[c+d*x])^2*Tan[c+d*x]/d+2/3*a*A*(a+a*Cos[c+d*x])^3*Sec[c+d*x]*Tan[c+d*x]/d+1/3*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]^2*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:99
  public void test0453() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^5*Sqrt[a+a*Cos[c+d*x]], x]", //
        "1/64*(35*A+48*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]*Sqrt[a]/d+1/64*a*(35*A+48*C)*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/96*a*(35*A+48*C)*Sec[c+d*x]*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/24*a*A*Sec[c+d*x]^2*Tan[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+1/4*A*Sec[c+d*x]^3*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:115
  public void test0454() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^4, x]", //
        "5/8*a^(5/2)*(5*A+8*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/24*a^3*(49*A-24*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+5/12*a*A*(a+a*Cos[c+d*x])^(3/2)*Sec[c+d*x]*Tan[c+d*x]/d+1/3*A*(a+a*Cos[c+d*x])^(5/2)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/24*a^2*(31*A+24*C)*Sqrt[a+a*Cos[c+d*x]]*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:133
  public void test0455() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(3/2), x]", //
        "1/2*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2))+1/2*(A-7*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+2*C*Sin[c+d*x]/(a*d*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:153
  public void test0456() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "-2*a*(A-C)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(3*A+C)*EllipticF[1/2*(c+d*x),2]/d+2*a*A*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])+2/3*a*C*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:169
  public void test0457() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(5/2), x]", //
        "-4/5*a^3*(5*A-9*C)*EllipticE[1/2*(c+d*x),2]/d+4/3*a^3*(5*A+3*C)*EllipticF[1/2*(c+d*x),2]/d+2/3*A*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+4*A*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]])-8/15*a^3*(10*A-3*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d-2/15*(35*A-3*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:187
  public void test0458() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^2), x]", //
        "-4*A*EllipticE[1/2*(c+d*x),2]/(a^2*d)-1/3*(5*A-C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)+4*A*Sin[c+d*x]/(a^2*d*Sqrt[Cos[c+d*x]])-1/3*(5*A-C)*Sin[c+d*x]/(a^2*d*(1+Cos[c+d*x])*Sqrt[Cos[c+d*x]])-1/3*(A+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:207
  public void test0459() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]]/Cos[c+d*x]^(11/2), x]", //
        "2/63*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*a*(16*A+21*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+8/315*a*(16*A+21*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+16/315*a*(16*A+21*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/9*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(9/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:223
  public void test0460() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(9/2), x]", //
        "2*a^(5/2)*C*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/7*a*A*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/7*A*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+2/21*a^3*(32*A+49*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/21*a^2*(8*A+7*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(3/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:241
  public void test0461() {
    check( //
        "Integrate[(A+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(7/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "-1/2*(A+C)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*(a+a*Cos[c+d*x])^(3/2))-1/2*(15*A+7*C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])+1/10*(9*A+5*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])-1/10*(13*A+5*C)*Sin[c+d*x]/(a*d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+1/10*(49*A+25*C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:263
  public void test0462() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Cos[c+d*x])*(B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "3/8*a*(B+C)*x+1/5*a*(5*B+4*C)*Sin[c+d*x]/d+3/8*a*(B+C)*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a*(B+C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/5*a*C*Cos[c+d*x]^4*Sin[c+d*x]/d-1/15*a*(5*B+4*C)*Sin[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:279
  public void test0463() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^2*(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*a^2*(7*B+8*C)*ArcTanh[Sin[c+d*x]]/d+1/3*a^2*(4*B+5*C)*Tan[c+d*x]/d+1/8*a^2*(7*B+8*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/12*a^2*(5*B+4*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/4*B*(a^2+a^2*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:297
  public void test0464() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^3/(a+a*Cos[c+d*x]), x]", //
        "-(B-C)*ArcTanh[Sin[c+d*x]]/(a*d)+(2*B-C)*Tan[c+d*x]/(a*d)-(B-C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:313
  public void test0465() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^3, x]", //
        "B*ArcTanh[Sin[c+d*x]]/(a^3*d)-1/5*(B-C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^3)-1/15*(7*B-2*C)*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^2)-2/15*(11*B-C)*Sin[c+d*x]/(d*(a^3+a^3*Cos[c+d*x]))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:349
  public void test0466() {
    check( //
        "Integrate[Cos[c+d*x]^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2), x]", //
        "1/16*(6*A+5*C)*x+B*Sin[c+d*x]/d+1/16*(6*A+5*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/24*(6*A+5*C)*Cos[c+d*x]^3*Sin[c+d*x]/d+1/6*C*Cos[c+d*x]^5*Sin[c+d*x]/d-2/3*B*Sin[c+d*x]^3/d+1/5*B*Sin[c+d*x]^5/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:381
  public void test0467() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "1/2*a^3*(6*A+7*B+5*C)*x+a^3*(3*A+B)*ArcTanh[Sin[c+d*x]]/d+5/2*a^3*(B+C)*Sin[c+d*x]/d-1/3*(3*A-C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]/(a*d)-1/6*(6*A-3*B-5*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]/d+A*(a+a*Cos[c+d*x])^3*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:397
  public void test0468() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^4*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^8, x]", //
        "1/16*a^4*(44*A+49*B+56*C)*ArcTanh[Sin[c+d*x]]/d+1/105*a^4*(454*A+504*B+581*C)*Tan[c+d*x]/d+1/16*a^4*(44*A+49*B+56*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/840*a^4*(988*A+1113*B+1232*C)*Sec[c+d*x]^2*Tan[c+d*x]/d+1/840*(436*A+511*B+504*C)*(a^4+a^4*Cos[c+d*x])*Sec[c+d*x]^3*Tan[c+d*x]/d+1/70*(16*A+21*B+14*C)*(a^2+a^2*Cos[c+d*x])^2*Sec[c+d*x]^4*Tan[c+d*x]/d+1/42*a*(4*A+7*B)*(a+a*Cos[c+d*x])^3*Sec[c+d*x]^5*Tan[c+d*x]/d+1/7*A*(a+a*Cos[c+d*x])^4*Sec[c+d*x]^6*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:415
  public void test0469() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^4/(a+a*Cos[c+d*x])^2, x]", //
        "-1/2*(10*A-7*B+4*C)*ArcTanh[Sin[c+d*x]]/(a^2*d)+(12*A-8*B+5*C)*Tan[c+d*x]/(a^2*d)-1/2*(10*A-7*B+4*C)*Sec[c+d*x]*Tan[c+d*x]/(a^2*d)-1/3*(10*A-7*B+4*C)*Sec[c+d*x]^2*Tan[c+d*x]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B+C)*Sec[c+d*x]^2*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^2)+1/3*(12*A-8*B+5*C)*Tan[c+d*x]^3/(a^2*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:431
  public void test0470() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2/(a+a*Cos[c+d*x])^4, x]", //
        "-(4*A-B)*ArcTanh[Sin[c+d*x]]/(a^4*d)+2/105*(332*A-80*B+3*C)*Tan[c+d*x]/(a^4*d)-1/105*(88*A-25*B-3*C)*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x])^2)-(4*A-B)*Tan[c+d*x]/(a^4*d*(1+Cos[c+d*x]))-1/7*(A-B+C)*Tan[c+d*x]/(d*(a+a*Cos[c+d*x])^4)-1/35*(12*A-5*B-2*C)*Tan[c+d*x]/(a*d*(a+a*Cos[c+d*x])^3)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:451
  public void test0471() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(3/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "a^(3/2)*(3*A+2*B)*ArcTanh[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d-1/3*a^2*(3*A-6*B-8*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])-1/3*a*(3*A-2*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/d+A*(a+a*Cos[c+d*x])^(3/2)*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:469
  public void test0472() {
    check( //
        "Integrate[Cos[c+d*x]^2*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(1/2), x]", //
        "(A-B+C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-4/105*(35*A-49*B+37*C)*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/35*(7*B-C)*Cos[c+d*x]^2*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/7*C*Cos[c+d*x]^3*Sin[c+d*x]/(d*Sqrt[a+a*Cos[c+d*x]])+2/105*(35*A-7*B+31*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:485
  public void test0473() {
    check( //
        "Integrate[Cos[c+d*x]^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(a+a*Cos[c+d*x])^(5/2), x]", //
        "-1/4*(A-B+C)*Cos[c+d*x]^4*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(5/2))-1/16*(5*A-13*B+21*C)*Cos[c+d*x]^3*Sin[c+d*x]/(a*d*(a+a*Cos[c+d*x])^(3/2))-1/16*(75*A-163*B+283*C)*ArcTanh[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Cos[c+d*x]])]/(a^(5/2)*d*Sqrt[2])+1/120*(465*A-985*B+1729*C)*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])+1/80*(45*A-85*B+157*C)*Cos[c+d*x]^2*Sin[c+d*x]/(a^2*d*Sqrt[a+a*Cos[c+d*x]])-1/240*(195*A-475*B+787*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:505
  public void test0474() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Sqrt[Cos[c+d*x]], x]", //
        "2/5*a*(5*A+5*B+3*C)*EllipticE[1/2*(c+d*x),2]/d+2/3*a*(3*A+B+C)*EllipticF[1/2*(c+d*x),2]/d+2/5*a*C*Cos[c+d*x]^(3/2)*Sin[c+d*x]/d+2/3*a*(B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:521
  public void test0475() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^3*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(3/2), x]", //
        "4/5*a^3*(5*A+9*B+7*C)*EllipticE[1/2*(c+d*x),2]/d+4/21*a^3*(35*A+21*B+13*C)*EllipticF[1/2*(c+d*x),2]/d+2*A*(a+a*Cos[c+d*x])^3*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])-4/105*a^3*(35*A-42*B-41*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d-2/7*(7*A-C)*(a^2+a^2*Cos[c+d*x])^2*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a*d)-2/35*(35*A-7*B-11*C)*(a^3+a^3*Cos[c+d*x])*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:539
  public void test0476() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/((a+a*Cos[c+d*x])^2*Sqrt[Cos[c+d*x]]), x]", //
        "(A-C)*EllipticE[1/2*(c+d*x),2]/(a^2*d)+1/3*(2*A+B+2*C)*EllipticF[1/2*(c+d*x),2]/(a^2*d)-(A-C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(a^2*d*(1+Cos[c+d*x]))-1/3*(A-B+C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*(a+a*Cos[c+d*x])^2)");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:559
  public void test0477() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sqrt[a+a*Cos[c+d*x]]/Cos[c+d*x]^(9/2), x]", //
        "2/35*a*(A+7*B)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2)*Sqrt[a+a*Cos[c+d*x]])+2/105*a*(24*A+28*B+35*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2)*Sqrt[a+a*Cos[c+d*x]])+4/105*a*(24*A+28*B+35*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])+2/7*A*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Cos[c+d*x]^(7/2))");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:575
  public void test0478() {
    check( //
        "Integrate[(a+a*Cos[c+d*x])^(5/2)*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/Cos[c+d*x]^(7/2), x]", //
        "a^(5/2)*(2*B+5*C)*ArcSin[Sin[c+d*x]*Sqrt[a]/Sqrt[a+a*Cos[c+d*x]]]/d+2/3*a*(A+B)*(a+a*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*A*(a+a*Cos[c+d*x])^(5/2)*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))-1/15*a^3*(64*A+70*B+15*C)*Sin[c+d*x]*Sqrt[Cos[c+d*x]]/(d*Sqrt[a+a*Cos[c+d*x]])+2/5*a^2*(8*A+10*B+5*C)*Sin[c+d*x]*Sqrt[a+a*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:593
  public void test0479() {
    check( //
        "Integrate[(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)/(Cos[c+d*x]^(3/2)*(a+a*Cos[c+d*x])^(3/2)), x]", //
        "-1/2*(7*A-3*B-C)*ArcTan[Sin[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])]/(a^(3/2)*d*Sqrt[2])-1/2*(A-B+C)*Sin[c+d*x]/(d*(a+a*Cos[c+d*x])^(3/2)*Sqrt[Cos[c+d*x]])+1/2*(5*A-B+C)*Sin[c+d*x]/(a*d*Sqrt[Cos[c+d*x]]*Sqrt[a+a*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:617
  public void test0480() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)*Sec[c+d*x]^6, x]", //
        "1/8*b*(3*A+4*C)*ArcTanh[Sin[c+d*x]]/d+1/5*a*(4*A+5*C)*Tan[c+d*x]/d+1/8*b*(3*A+4*C)*Sec[c+d*x]*Tan[c+d*x]/d+1/4*A*b*Sec[c+d*x]^3*Tan[c+d*x]/d+1/5*a*A*Sec[c+d*x]^4*Tan[c+d*x]/d+1/15*a*(4*A+5*C)*Tan[c+d*x]^3/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:780
  public void test0481() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+C*Cos[c+d*x]^2)/Cos[c+d*x]^(9/2), x]", //
        "-2/5*b*(3*A+5*C)*EllipticE[1/2*(c+d*x),2]/d+2/21*a*(5*A+7*C)*EllipticF[1/2*(c+d*x),2]/d+2/7*a*A*Sin[c+d*x]/(d*Cos[c+d*x]^(7/2))+2/5*A*b*Sin[c+d*x]/(d*Cos[c+d*x]^(5/2))+2/21*a*(5*A+7*C)*Sin[c+d*x]/(d*Cos[c+d*x]^(3/2))+2/5*b*(3*A+5*C)*Sin[c+d*x]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:916
  public void test0482() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+b*Cos[c+d*x]), x]", //
        "C*x/b+2*(b*B-a*C)*ArcTan[Sqrt[a-b]*Tan[1/2*(c+d*x)]/Sqrt[a+b]]/(b*d*Sqrt[a-b]*Sqrt[a+b])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:969
  public void test0483() {
    check( //
        "Integrate[(B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]/(a+b*Cos[c+d*x])^(3/2), x]", //
        "-2*(b*B-a*C)*Sin[c+d*x]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])+2*(b*B-a*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/(b*(a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2*C*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(b*d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1085
  public void test0484() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])*(A+B*Cos[c+d*x]+C*Cos[c+d*x]^2)*Sec[c+d*x]^2, x]", //
        "(b*B+a*C)*x+(A*b+a*B)*ArcTanh[Sin[c+d*x]]/d+b*C*Sin[c+d*x]/d+a*A*Tan[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1117
  public void test0485() {
    check( //
        "Integrate[(a+b*Cos[c+d*x])^3*(a*b*B-a^2*C+b^2*B*Cos[c+d*x]+b^2*C*Cos[c+d*x]^2), x]", //
        "1/8*(8*a^4*b*B+24*a^2*b^3*B+3*b^5*B-8*a^5*C-8*a^3*b^2*C+9*a*b^4*C)*x+1/30*b*(95*a^3*b*B+80*a*b^3*B-83*a^4*C+32*a^2*b^2*C+16*b^4*C)*Sin[c+d*x]/d+1/120*b^2*(130*a^2*b*B+45*b^3*B-106*a^3*C+71*a*b^2*C)*Cos[c+d*x]*Sin[c+d*x]/d+1/60*b*(35*a*b*B-23*a^2*C+16*b^2*C)*(a+b*Cos[c+d*x])^2*Sin[c+d*x]/d+1/20*b*(5*b*B-a*C)*(a+b*Cos[c+d*x])^3*Sin[c+d*x]/d+1/5*b*C*(a+b*Cos[c+d*x])^4*Sin[c+d*x]/d");
  }

  // 4.2.4.2 (a+b cos)^m (c+d cos)^n (A+B cos+C cos^2).input:1214
  public void test0486() {
    check( //
        "Integrate[(a*b*B-a^2*C+b^2*B*Cos[c+d*x]+b^2*C*Cos[c+d*x]^2)/(a+b*Cos[c+d*x])^(5/2), x]", //
        "-2*b*(b*B-2*a*C)*Sin[c+d*x]/((a^2-b^2)*d*Sqrt[a+b*Cos[c+d*x]])+2*(b*B-2*a*C)*EllipticE[1/2*(c+d*x),2*b/(a+b)]*Sqrt[a+b*Cos[c+d*x]]/((a^2-b^2)*d*Sqrt[(a+b*Cos[c+d*x])/(a+b)])+2*C*EllipticF[1/2*(c+d*x),2*b/(a+b)]*Sqrt[(a+b*Cos[c+d*x])/(a+b)]/(d*Sqrt[a+b*Cos[c+d*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:74
  public void test0487() {
    check( //
        "Integrate[Sec[a+b*x]^3*Sin[a+b*x], x]", //
        "1/2*Sec[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:90
  public void test0488() {
    check( //
        "Integrate[Sec[a+b*x]^3*Sin[a+b*x]^2, x]", //
        "-1/2*ArcTanh[Sin[a+b*x]]/b+1/2*Sec[a+b*x]*Tan[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:106
  public void test0489() {
    check( //
        "Integrate[Sec[a+b*x]^9*Sin[a+b*x]^3, x]", //
        "-1/6*Sec[a+b*x]^6/b+1/8*Sec[a+b*x]^8/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:122
  public void test0490() {
    check( //
        "Integrate[Sec[a+b*x]^5*Sin[a+b*x]^4, x]", //
        "3/8*ArcTanh[Sin[a+b*x]]/b-3/8*Sec[a+b*x]*Tan[a+b*x]/b+1/4*Sec[a+b*x]*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:138
  public void test0491() {
    check( //
        "Integrate[Sec[a+b*x]^7*Sin[a+b*x]^5, x]", //
        "1/6*Tan[a+b*x]^6/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:172
  public void test0492() {
    check( //
        "Integrate[Sec[a+b*x]^4/Sin[a+b*x]^2, x]", //
        "-Cot[a+b*x]/b+2*Tan[a+b*x]/b+1/3*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:204
  public void test0493() {
    check( //
        "Integrate[Cos[a+b*x]^5/Sin[a+b*x]^5, x]", //
        "1/2*Cot[a+b*x]^2/b-1/4*Cot[a+b*x]^4/b+Log[Sin[a+b*x]]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:274
  public void test0494() {
    check( //
        "Integrate[Csc[a+b*x]^2/(d*Cos[a+b*x])^(3/2), x]", //
        "-Csc[a+b*x]/(b*d*Sqrt[d*Cos[a+b*x]])+3*Sin[a+b*x]/(b*d*Sqrt[d*Cos[a+b*x]])-3*EllipticE[1/2*(a+b*x),2]*Sqrt[d*Cos[a+b*x]]/(b*d^2*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:296
  public void test0495() {
    check( //
        "Integrate[Cos[x]^3*Sin[x]^(5/2), x]", //
        "2/7*Sin[x]^(7/2)-2/11*Sin[x]^(11/2)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:334
  public void test0496() {
    check( //
        "Integrate[Sin[a+b*x]^(7/2)/Cos[a+b*x]^(7/2), x]", //
        "2/5*Sin[a+b*x]^(5/2)/(b*Cos[a+b*x]^(5/2))+ArcTan[1-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-ArcTan[1+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-1/2*Log[1+Cot[a+b*x]-Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])+1/2*Log[1+Cot[a+b*x]+Sqrt[2]*Sqrt[Cos[a+b*x]]/Sqrt[Sin[a+b*x]]]/(b*Sqrt[2])-2*Sqrt[Sin[a+b*x]]/(b*Sqrt[Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:352
  public void test0497() {
    check( //
        "Integrate[Cos[a+b*x]^(7/2)/Sin[a+b*x]^(7/2), x]", //
        "-2/5*Cos[a+b*x]^(5/2)/(b*Sin[a+b*x]^(5/2))-ArcTan[1-Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]]/(b*Sqrt[2])+ArcTan[1+Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]]/(b*Sqrt[2])+1/2*Log[1-Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]+Tan[a+b*x]]/(b*Sqrt[2])-1/2*Log[1+Sqrt[2]*Sqrt[Sin[a+b*x]]/Sqrt[Cos[a+b*x]]+Tan[a+b*x]]/(b*Sqrt[2])+2*Sqrt[Cos[a+b*x]]/(b*Sqrt[Sin[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:420
  public void test0498() {
    check( //
        "Integrate[(d*Cos[a+b*x])^n*Sin[a+b*x]^5, x]", //
        "-(d*Cos[a+b*x])^(1+n)/(b*d*(1+n))+2*(d*Cos[a+b*x])^(3+n)/(b*d^3*(3+n))-(d*Cos[a+b*x])^(5+n)/(b*d^5*(5+n))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:455
  public void test0499() {
    check( //
        "Integrate[Csc[e+f*x]^6*Sqrt[b*Sec[e+f*x]], x]", //
        "-3/4*b*Csc[e+f*x]/(f*Sqrt[b*Sec[e+f*x]])-3/10*b*Csc[e+f*x]^3/(f*Sqrt[b*Sec[e+f*x]])-1/5*b*Csc[e+f*x]^5/(f*Sqrt[b*Sec[e+f*x]])+3/4*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/f");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:505
  public void test0500() {
    check( //
        "Integrate[Sin[e+f*x]^2/(b*Sec[e+f*x])^(3/2), x]", //
        "-2/7*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(5/2))+4/21*Sin[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])+4/21*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:521
  public void test0501() {
    check( //
        "Integrate[Csc[e+f*x]^4/(b*Sec[e+f*x])^(5/2), x]", //
        "1/2*Csc[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))-1/3*Csc[e+f*x]^3/(b*f*(b*Sec[e+f*x])^(3/2))+1/2*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:543
  public void test0502() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(3/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2*b/(f*(b*Sec[e+f*x])^(3/2)*Sqrt[Sin[e+f*x]])-2*EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:559
  public void test0503() {
    check( //
        "Integrate[1/((b*Sec[e+f*x])^(3/2)*(a*Sin[e+f*x])^(5/2)), x]", //
        "(-2/3)/(a*b*f*(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]])-1/3*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(a^2*b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.1 (a+b sin)^n.input:41
  public void test0504() {
    check( //
        "Integrate[1/(5-3*Sin[c+d*x]), x]", //
        "1/4*x-1/2*ArcTan[Cos[c+d*x]/(3-Sin[c+d*x])]/d");
  }

  // 4.1.1.1 (a+b sin)^n.input:57
  public void test0505() {
    check( //
        "Integrate[1/(3-5*Sin[c+d*x]), x]", //
        "-1/4*Log[Cos[1/2*(c+d*x)]-3*Sin[1/2*(c+d*x)]]/d+1/4*Log[3*Cos[1/2*(c+d*x)]-Sin[1/2*(c+d*x)]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:25
  public void test0506() {
    check( //
        "Integrate[Cos[c+d*x]^5*(a+a*Sin[c+d*x])^2, x]", //
        "4/5*(a+a*Sin[c+d*x])^5/(a^3*d)-2/3*(a+a*Sin[c+d*x])^6/(a^4*d)+1/7*(a+a*Sin[c+d*x])^7/(a^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:41
  public void test0507() {
    check( //
        "Integrate[Cos[c+d*x]^3*(a+a*Sin[c+d*x])^3, x]", //
        "2/5*(a+a*Sin[c+d*x])^5/(a^2*d)-1/6*(a+a*Sin[c+d*x])^6/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:57
  public void test0508() {
    check( //
        "Integrate[Sec[c+d*x]*(a+a*Sin[c+d*x])^8, x]", //
        "-128*a^8*Log[1-Sin[c+d*x]]/d-64*a^8*Sin[c+d*x]/d-16/3*a^5*(a+a*Sin[c+d*x])^3/d-4/5*a^3*(a+a*Sin[c+d*x])^5/d-1/3*a^2*(a+a*Sin[c+d*x])^6/d-1/7*a*(a+a*Sin[c+d*x])^7/d-2*(a^2+a^2*Sin[c+d*x])^4/d-16*(a^4+a^4*Sin[c+d*x])^2/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:75
  public void test0509() {
    check( //
        "Integrate[Cos[c+d*x]^8/(a+a*Sin[c+d*x])^2, x]", //
        "7/16*x/a^2+7/30*Cos[c+d*x]^5/(a^2*d)+7/16*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+7/24*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)+1/6*Cos[c+d*x]^7/(d*(a^2+a^2*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:91
  public void test0510() {
    check( //
        "Integrate[Cos[c+d*x]^5/(a+a*Sin[c+d*x])^3, x]", //
        "4*Log[1+Sin[c+d*x]]/(a^3*d)-3*Sin[c+d*x]/(a^3*d)+1/2*Sin[c+d*x]^2/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:107
  public void test0511() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+a*Sin[c+d*x])^8, x]", //
        "-1/13*Cos[c+d*x]^3/(d*(a+a*Sin[c+d*x])^8)-5/143*Cos[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^7)-20/1287*Cos[c+d*x]^3/(a^2*d*(a+a*Sin[c+d*x])^6)-20/3003*Cos[c+d*x]^3/(a^3*d*(a+a*Sin[c+d*x])^5)-8/3003*Cos[c+d*x]^3/(d*(a^2+a^2*Sin[c+d*x])^4)-8/9009*Cos[c+d*x]^3/(a^2*d*(a^2+a^2*Sin[c+d*x])^3)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:143
  public void test0512() {
    check( //
        "Integrate[Sec[c+d*x]^6*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-7/16*a^3*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))+1/5*Sec[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d-7/16*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(d*Sqrt[2])+7/12*a^2*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+7/30*a*Sec[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:159
  public void test0513() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^(7/2), x]", //
        "-16384/45045*a^6*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))-4096/9009*a^5*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-8/39*a^2*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^(3/2)/d-2/15*a*Cos[c+d*x]^5*(a+a*Sin[c+d*x])^(5/2)/d-512/1287*a^4*Cos[c+d*x]^5/(d*Sqrt[a+a*Sin[c+d*x]])-128/429*a^3*Cos[c+d*x]^5*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:193
  public void test0514() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)+2*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:209
  public void test0515() {
    check( //
        "Integrate[Cos[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))+ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:229
  public void test0516() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^2, x]", //
        "-18/35*a^2*(e*Cos[c+d*x])^(5/2)/(d*e)-2/7*(e*Cos[c+d*x])^(5/2)*(a^2+a^2*Sin[c+d*x])/(d*e)+6/7*a^2*e^2*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+6/7*a^2*e*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:245
  public void test0517() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/(e*Cos[c+d*x])^(9/2), x]", //
        "-2/21*a^3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^4*Sqrt[e*Cos[c+d*x]])+4/7*a^5*Sqrt[e*Cos[c+d*x]]/(d*e^5*(a-a*Sin[c+d*x])^2)-2/21*a^6*Sqrt[e*Cos[c+d*x]]/(d*e^5*(a^3-a^3*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:263
  public void test0518() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]/(a+a*Sin[c+d*x]), x]", //
        "-2*(e*Cos[c+d*x])^(3/2)/(d*e*(a+a*Sin[c+d*x]))-2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:279
  public void test0519() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(13/2)/(a+a*Sin[c+d*x])^3, x]", //
        "22/21*e^3*(e*Cos[c+d*x])^(7/2)/(a^3*d)+22/15*e^5*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/(a^3*d)+4/3*e*(e*Cos[c+d*x])^(11/2)/(a*d*(a+a*Sin[c+d*x])^2)+22/5*e^6*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^3*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:295
  public void test0520() {
    check( //
        "Integrate[Sqrt[e*Cos[c+d*x]]/(a+a*Sin[c+d*x])^4, x]", //
        "-2/13*(e*Cos[c+d*x])^(3/2)/(d*e*(a+a*Sin[c+d*x])^4)-10/117*(e*Cos[c+d*x])^(3/2)/(a*d*e*(a+a*Sin[c+d*x])^3)-2/39*(e*Cos[c+d*x])^(3/2)/(d*e*(a^2+a^2*Sin[c+d*x])^2)-2/39*(e*Cos[c+d*x])^(3/2)/(d*e*(a^4+a^4*Sin[c+d*x]))-2/39*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(a^4*d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:349
  public void test0521() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])^(5/2)*Sqrt[e*Cos[c+d*x]]), x]", //
        "-2/9*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x])^(5/2))-8/45*Sqrt[e*Cos[c+d*x]]/(a*d*e*(a+a*Sin[c+d*x])^(3/2))-16/45*Sqrt[e*Cos[c+d*x]]/(a^2*d*e*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:438
  public void test0522() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+b*Sin[c+d*x])^2, x]", //
        "1/8*(4*a^2+b^2)*x-5/12*a*b*Cos[c+d*x]^3/d+1/8*(4*a^2+b^2)*Cos[c+d*x]*Sin[c+d*x]/d-1/4*b*Cos[c+d*x]^3*(a+b*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:454
  public void test0523() {
    check( //
        "Integrate[Sec[c+d*x]^8*(a+b*Sin[c+d*x])^3, x]", //
        "2/35*b*(3*a^2-b^2)*Sec[c+d*x]^3/d+1/7*Sec[c+d*x]^7*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/d+2/35*Sec[c+d*x]^5*(a+b*Sin[c+d*x])*(2*a*b+(3*a^2-b^2)*Sin[c+d*x])/d+12/35*a*(2*a^2-b^2)*Tan[c+d*x]/d+4/35*a*(2*a^2-b^2)*Tan[c+d*x]^3/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:558
  public void test0524() {
    check( //
        "Integrate[Cos[c+d*x]^3/Sqrt[a+b*Sin[c+d*x]], x]", //
        "4/3*a*(a+b*Sin[c+d*x])^(3/2)/(b^3*d)-2/5*(a+b*Sin[c+d*x])^(5/2)/(b^3*d)-2*(a^2-b^2)*Sqrt[a+b*Sin[c+d*x]]/(b^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:594
  public void test0525() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x]), x]", //
        "-2/9*b*(e*Cos[c+d*x])^(9/2)/(d*e)+2/7*a*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d+10/21*a*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+10/21*a*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:610
  public void test0526() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^3, x]", //
        "-2/1287*b*(177*a^2+44*b^2)*(e*Cos[c+d*x])^(9/2)/(d*e)+2/77*a*(11*a^2+6*b^2)*e*(e*Cos[c+d*x])^(5/2)*Sin[c+d*x]/d-34/143*a*b*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])/(d*e)-2/13*b*(e*Cos[c+d*x])^(9/2)*(a+b*Sin[c+d*x])^2/(d*e)+10/231*a*(11*a^2+6*b^2)*e^4*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*Sqrt[e*Cos[c+d*x]])+10/231*a*(11*a^2+6*b^2)*e^3*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:626
  public void test0527() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4/(e*Cos[c+d*x])^(7/2), x]", //
        "2/5*a*b*(3*a^2-10*b^2)*(e*Cos[c+d*x])^(3/2)/(d*e^5)+6/5*b*(a^2-2*b^2)*(e*Cos[c+d*x])^(3/2)*(a+b*Sin[c+d*x])/(d*e^5)+2/5*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^3/(d*e*(e*Cos[c+d*x])^(5/2))-6/5*(a+b*Sin[c+d*x])^2*(a*b-(a^2-2*b^2)*Sin[c+d*x])/(d*e^3*Sqrt[e*Cos[c+d*x]])-6/5*(a^4-4*a^2*b^2-4*b^4)*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^4*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:53
  public void test0528() {
    check( //
        "Integrate[Cot[c+d*x]^4*(a+a*Sin[c+d*x])^4, x]", //
        "-61/8*a^4*x+2*a^4*ArcTanh[Cos[c+d*x]]/d+4/3*a^4*Cos[c+d*x]^3/d-5*a^4*Cot[c+d*x]/d-1/3*a^4*Cot[c+d*x]^3/d-2*a^4*Cot[c+d*x]*Csc[c+d*x]/d-19/8*a^4*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:72
  public void test0529() {
    check( //
        "Integrate[Cot[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "3/8*ArcTanh[Cos[c+d*x]]/(a*d)-1/5*Cot[c+d*x]^5/(a*d)-3/8*Cot[c+d*x]*Csc[c+d*x]/(a*d)+1/4*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:88
  public void test0530() {
    check( //
        "Integrate[Cot[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "Log[Sin[c+d*x]]/(a^3*d)-Log[1+Sin[c+d*x]]/(a^3*d)+1/2/(a*d*(a+a*Sin[c+d*x])^2)+1/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:109
  public void test0531() {
    check( //
        "Integrate[Sqrt[a+a*Sin[e+f*x]]*Tan[e+f*x]^2, x]", //
        "-2*Sec[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(a*f)-ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[a]/(f*Sqrt[2])+5*Sec[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:132
  public void test0532() {
    check( //
        "Integrate[Cot[e+f*x]^2/(a+a*Sin[e+f*x])^(5/2), x]", //
        "5*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(a^(5/2)*f)-2*Cos[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-Cot[e+f*x]/(a*f*(a+a*Sin[e+f*x])^(3/2))-7*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*f*Sqrt[2])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:187
  public void test0533() {
    check( //
        "Integrate[Cot[c+d*x]*(a+b*Sin[c+d*x])^2, x]", //
        "a^2*Log[Sin[c+d*x]]/d+2*a*b*Sin[c+d*x]/d+1/2*b^2*Sin[c+d*x]^2/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:25
  public void test0534() {
    check( //
        "Integrate[Sin[a+b*x]^2/(c+d*x)^3, x]", //
        "b^2*CosIntegral[2*b*c/d+2*b*x]*Cos[2*a-2*b*c/d]/d^3-b^2*SinIntegral[2*b*c/d+2*b*x]*Sin[2*a-2*b*c/d]/d^3-b*Cos[a+b*x]*Sin[a+b*x]/(d^2*(c+d*x))-1/2*Sin[a+b*x]^2/(d*(c+d*x)^2)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:47
  public void test0535() {
    check( //
        "Integrate[(c+d*x)^2*Csc[a+b*x]^3, x]", //
        "-(c+d*x)^2*ArcTanh[E^(I*(a+b*x))]/b-d^2*ArcTanh[Cos[a+b*x]]/b^3-d*(c+d*x)*Csc[a+b*x]/b^2-1/2*(c+d*x)^2*Cot[a+b*x]*Csc[a+b*x]/b+I*d*(c+d*x)*PolyLog[2,-E^(I*(a+b*x))]/b^2-I*d*(c+d*x)*PolyLog[2,E^(I*(a+b*x))]/b^2-d^2*PolyLog[3,-E^(I*(a+b*x))]/b^3+d^2*PolyLog[3,E^(I*(a+b*x))]/b^3");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:95
  public void test0536() {
    check( //
        "Integrate[x/Sin[e+f*x]^(7/2)+3/5*x*Sqrt[Sin[e+f*x]], x]", //
        "-2/5*x*Cos[e+f*x]/(f*Sin[e+f*x]^(5/2))+(-4/15)/(f^2*Sin[e+f*x]^(3/2))-6/5*x*Cos[e+f*x]/(f*Sqrt[Sin[e+f*x]])+12/5*Sqrt[Sin[e+f*x]]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:174
  public void test0537() {
    check( //
        "Integrate[Sqrt[a+a*Sin[c+d*x]]/x^3, x]", //
        "-1/2*Sqrt[a+a*Sin[c+d*x]]/x^2-1/4*d*Cot[1/4*Pi+1/2*c+1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]/x-1/8*d^2*Cos[1/4*(Pi+2*c)]*Csc[1/4*Pi+1/2*c+1/2*d*x]*SinIntegral[1/2*d*x]*Sqrt[a+a*Sin[c+d*x]]-1/8*d^2*CosIntegral[1/2*d*x]*Csc[1/4*Pi+1/2*c+1/2*d*x]*Sin[1/4*(Pi+2*c)]*Sqrt[a+a*Sin[c+d*x]]");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:212
  public void test0538() {
    check( //
        "Integrate[(c+d*x)^3*(a+b*Sin[e+f*x]), x]", //
        "1/4*a*(c+d*x)^4/d+6*b*d^2*(c+d*x)*Cos[e+f*x]/f^3-b*(c+d*x)^3*Cos[e+f*x]/f-6*b*d^3*Sin[e+f*x]/f^4+3*b*d*(c+d*x)^2*Sin[e+f*x]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:306
  public void test0539() {
    check( //
        "Integrate[Sin[c+d*x]/(a+b*Sin[c+d*x]), x]", //
        "x/b-2*a*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b*d*Sqrt[a^2-b^2])");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:361
  public void test0540() {
    check( //
        "Integrate[Cos[c+d*x]^2/((e+f*x)^2*(a+a*Sin[c+d*x])), x]", //
        "(-1)/(a*f*(e+f*x))-d*CosIntegral[d*e/f+d*x]*Cos[c-d*e/f]/(a*f^2)+d*SinIntegral[d*e/f+d*x]*Sin[c-d*e/f]/(a*f^2)+Sin[c+d*x]/(a*f*(e+f*x))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:383
  public void test0541() {
    check( //
        "Integrate[(e+f*x)^2*Sec[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/4*I*(e+f*x)^2*ArcTan[E^(I*(c+d*x))]/(a*d)+5/6*f^2*ArcTanh[Sin[c+d*x]]/(a*d^3)+1/3*f^2*Log[Cos[c+d*x]]/(a*d^3)+3/4*I*f*(e+f*x)*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-3/4*I*f*(e+f*x)*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-3/4*f^2*PolyLog[3,-I*E^(I*(c+d*x))]/(a*d^3)+3/4*f^2*PolyLog[3,I*E^(I*(c+d*x))]/(a*d^3)-3/4*f*(e+f*x)*Sec[c+d*x]/(a*d^2)-1/12*f^2*Sec[c+d*x]^2/(a*d^3)-1/6*f*(e+f*x)*Sec[c+d*x]^3/(a*d^2)-1/4*(e+f*x)^2*Sec[c+d*x]^4/(a*d)+1/3*f*(e+f*x)*Tan[c+d*x]/(a*d^2)+1/12*f^2*Sec[c+d*x]*Tan[c+d*x]/(a*d^3)+3/8*(e+f*x)^2*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/6*f*(e+f*x)*Sec[c+d*x]^2*Tan[c+d*x]/(a*d^2)+1/4*(e+f*x)^2*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:413
  public void test0542() {
    check( //
        "Integrate[(e+f*x)*Cos[c+d*x]^3/(a+b*Sin[c+d*x]), x]", //
        "1/4*f*x/(b*d)+1/2*I*(a^2-b^2)*(e+f*x)^2/(b^3*f)+a*f*Cos[c+d*x]/(b^2*d^2)-(a^2-b^2)*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b^3*d)-(a^2-b^2)*(e+f*x)*Log[1-I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b^3*d)+I*(a^2-b^2)*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a-Sqrt[a^2-b^2])]/(b^3*d^2)+I*(a^2-b^2)*f*PolyLog[2,I*E^(I*(c+d*x))*b/(a+Sqrt[a^2-b^2])]/(b^3*d^2)+a*(e+f*x)*Sin[c+d*x]/(b^2*d)-1/4*f*Cos[c+d*x]*Sin[c+d*x]/(b*d^2)-1/2*(e+f*x)*Sin[c+d*x]^2/(b*d)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:18
  public void test0543() {
    check( //
        "Integrate[(a+b*x)*Sin[c+d*x]/x^5, x]", //
        "-1/6*b*d^3*CosIntegral[d*x]*Cos[c]-1/12*a*d*Cos[c+d*x]/x^3-1/6*b*d*Cos[c+d*x]/x^2+1/24*a*d^3*Cos[c+d*x]/x+1/24*a*d^4*Cos[c]*SinIntegral[d*x]+1/24*a*d^4*CosIntegral[d*x]*Sin[c]+1/6*b*d^3*SinIntegral[d*x]*Sin[c]-1/4*a*Sin[c+d*x]/x^4-1/3*b*Sin[c+d*x]/x^3+1/24*a*d^2*Sin[c+d*x]/x^2+1/6*b*d^2*Sin[c+d*x]/x");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:56
  public void test0544() {
    check( //
        "Integrate[x^2*(a+b*x^2)*Sin[c+d*x], x]", //
        "-24*b*Cos[c+d*x]/d^5+2*a*Cos[c+d*x]/d^3+12*b*x^2*Cos[c+d*x]/d^3-a*x^2*Cos[c+d*x]/d-b*x^4*Cos[c+d*x]/d-24*b*x*Sin[c+d*x]/d^4+2*a*x*Sin[c+d*x]/d^2+4*b*x^3*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:129
  public void test0545() {
    check( //
        "Integrate[(e*x)^m*(a+b*Sin[c+d*x^3]), x]", //
        "a*(e*x)^(1+m)/(e*(1+m))+1/6*I*E^(I*c)*b*(e*x)^(1+m)*(-I*d*x^3)^(1/3*(-1-m))*Gamma[1/3*(1+m),-I*d*x^3]/e-1/6*I*b*(e*x)^(1+m)*(I*d*x^3)^(1/3*(-1-m))*Gamma[1/3*(1+m),I*d*x^3]/(E^(I*c)*e)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:335
  public void test0546() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(2/3)]/(c*e+d*e*x)^(7/3), x]", //
        "3/2*Cos[a+b/(c+d*x)^(2/3)]/(b*d*e^2*(c+d*x)^(1/3)*(e*(c+d*x))^(1/3))-3/2*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(2/3)]/(b^2*d*e^2*(e*(c+d*x))^(1/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:389
  public void test0547() {
    check( //
        "Integrate[a+b*Sin[c+d/x], x]", //
        "a*x-b*d*CosIntegral[d/x]*Cos[c]+b*d*SinIntegral[d/x]*Sin[c]+b*x*Sin[c+d/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:428
  public void test0548() {
    check( //
        "Integrate[(c*Sin[a+b*x]^3)^(1/3)/x^3, x]", //
        "-1/2*(c*Sin[a+b*x]^3)^(1/3)/x^2-1/2*b*Cot[a+b*x]*(c*Sin[a+b*x]^3)^(1/3)/x-1/2*b^2*Cos[a]*Csc[a+b*x]*SinIntegral[b*x]*(c*Sin[a+b*x]^3)^(1/3)-1/2*b^2*CosIntegral[b*x]*Csc[a+b*x]*Sin[a]*(c*Sin[a+b*x]^3)^(1/3)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:448
  public void test0549() {
    check( //
        "Integrate[(c*Sin[a+b*x^n]^3)^(1/3)/x^3, x]", //
        "1/2*I*E^(I*a)*(-I*b*x^n)^(2/n)*Csc[a+b*x^n]*Gamma[(-2)/n,-I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(n*x^2)-1/2*I*(I*b*x^n)^(2/n)*Csc[a+b*x^n]*Gamma[(-2)/n,I*b*x^n]*(c*Sin[a+b*x^n]^3)^(1/3)/(E^(I*a)*n*x^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:468
  public void test0550() {
    check( //
        "Integrate[(c*Sin[a+b*x^2]^3)^(2/3)/x^3, x]", //
        "-1/4*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)/x^2+1/4*Cos[2*(a+b*x^2)]*Csc[a+b*x^2]^2*(c*Sin[a+b*x^2]^3)^(2/3)/x^2+1/2*b*Cos[2*a]*Csc[a+b*x^2]^2*SinIntegral[2*b*x^2]*(c*Sin[a+b*x^2]^3)^(2/3)+1/2*b*CosIntegral[2*b*x^2]*Csc[a+b*x^2]^2*Sin[2*a]*(c*Sin[a+b*x^2]^3)^(2/3)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:44
  public void test0551() {
    check( //
        "Integrate[Csc[x]^4/(a+a*Sin[x])^3, x]", //
        "23/2*ArcTanh[Cos[x]]/a^3-136/5*Cot[x]/a^3-136/15*Cot[x]^3/a^3+23/2*Cot[x]*Csc[x]/a^3+1/5*Cot[x]*Csc[x]^2/(a+a*Sin[x])^3+13/15*Cot[x]*Csc[x]^2/(a*(a+a*Sin[x])^2)+23/3*Cot[x]*Csc[x]^2/(a^3+a^3*Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:82
  public void test0552() {
    check( //
        "Integrate[Sin[c+d*x]/Sqrt[a+a*Sin[c+d*x]], x]", //
        "ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(d*Sqrt[a])-2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:98
  public void test0553() {
    check( //
        "Integrate[Sin[c+d*x]^2/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-1/4*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))+13/16*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-19/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:214
  public void test0554() {
    check( //
        "Integrate[Csc[e+f*x]^2*(a+b*Sin[e+f*x])^3, x]", //
        "3*a*b^2*x-3*a^2*b*ArcTanh[Cos[e+f*x]]/f+b*(a^2-b^2)*Cos[e+f*x]/f-a^2*Cot[e+f*x]*(a+b*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:305
  public void test0555() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^3, x]", //
        "3/8*a^2*c^3*x+1/5*a^2*c^3*Cos[e+f*x]^5/f+3/8*a^2*c^3*Cos[e+f*x]*Sin[e+f*x]/f+1/4*a^2*c^3*Cos[e+f*x]^3*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:321
  public void test0556() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^2, x]", //
        "5*a^3*x/c^2-5*a^3*Cos[e+f*x]/(c^2*f)+2/3*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^4)-10/3*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:339
  public void test0557() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^4/(a+a*Sin[e+f*x])^2, x]", //
        "35/2*c^4*x/a^2+35/2*c^4*Cos[e+f*x]/(a^2*f)-2/3*a^3*c^4*Cos[e+f*x]^7/(f*(a+a*Sin[e+f*x])^5)+14/3*a^4*c^4*Cos[e+f*x]^5/(f*(a^2+a^2*Sin[e+f*x])^3)+35/6*c^4*Cos[e+f*x]^3/(f*(a^2+a^2*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:355
  public void test0558() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^3), x]", //
        "Tan[e+f*x]/(a^3*c^3*f)+2/3*Tan[e+f*x]^3/(a^3*c^3*f)+1/5*Tan[e+f*x]^5/(a^3*c^3*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:375
  public void test0559() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^(1/2), x]", //
        "-2/3*a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(3/2))+4*a^2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[c])-4*a^2*Cos[e+f*x]/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:393
  public void test0560() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x]), x]", //
        "16/3*c*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f)+2/3*Sec[e+f*x]*(c-c*Sin[e+f*x])^(5/2)/(a*f)-64/3*c^2*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:409
  public void test0561() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^3, x]", //
        "-64/15*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*f)+16/3*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(7/2)/(a^3*c*f)-2*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(9/2)/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:461
  public void test0562() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(17/2), x]", //
        "1/8*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(17/2))-3/56*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c*f*(c-c*Sin[e+f*x])^(15/2))-1/280*a^4*Cos[e+f*x]/(c^3*f*(c-c*Sin[e+f*x])^(11/2)*Sqrt[a+a*Sin[e+f*x]])+1/56*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^2*f*(c-c*Sin[e+f*x])^(13/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:479
  public void test0563() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(5/2), x]", //
        "-1/2*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*(a+a*Sin[e+f*x])^(5/2))+c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f*(a+a*Sin[e+f*x])^(3/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:574
  public void test0564() {
    check( //
        "Integrate[(A+B*Sin[x])/(1-Sin[x])^4, x]", //
        "1/7*(A+B)*Cos[x]/(1-Sin[x])^4+1/35*(3*A-4*B)*Cos[x]/(1-Sin[x])^3+2/105*(3*A-4*B)*Cos[x]/(1-Sin[x])^2+2/105*(3*A-4*B)*Cos[x]/(1-Sin[x])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:632
  public void test0565() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^3, x]", //
        "4/105*(c-17*d)*d*(c+d)*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f+4/315*a^2*(c-17*d)*(c+d)*(15*c^2+10*c*d+7*d^2)*Cos[e+f*x]/(d*f*Sqrt[a+a*Sin[e+f*x]])+2/63*a^2*(c-17*d)*Cos[e+f*x]*(c+d*Sin[e+f*x])^3/(d*f*Sqrt[a+a*Sin[e+f*x]])-2/9*a^2*Cos[e+f*x]*(c+d*Sin[e+f*x])^4/(d*f*Sqrt[a+a*Sin[e+f*x]])+8/315*a*(c-17*d)*(5*c-d)*(c+d)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:650
  public void test0566() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-(c-d)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])-2*d*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:666
  public void test0567() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(5/2)*(c+d*Sin[e+f*x])), x]", //
        "-1/4*Cos[e+f*x]/((c-d)*f*(a+a*Sin[e+f*x])^(5/2))-1/16*(3*c-11*d)*Cos[e+f*x]/(a*(c-d)^2*f*(a+a*Sin[e+f*x])^(3/2))-1/16*(3*c^2-14*c*d+43*d^2)*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*(c-d)^3*f*Sqrt[2])+2*d^(5/2)*ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[d]/(Sqrt[c+d]*Sqrt[a+a*Sin[e+f*x]])]/(a^(5/2)*(c-d)^3*f*Sqrt[c+d])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:686
  public void test0568() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)/(c+d*Sin[e+f*x])^(7/2), x]", //
        "2/5*a^2*(c-d)*Cos[e+f*x]/(d*(c+d)*f*(c+d*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])-2/15*a^2*(c+9*d)*Cos[e+f*x]/(d*(c+d)^2*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-4/15*a^2*(c+9*d)*Cos[e+f*x]/(d*(c+d)^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:704
  public void test0569() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(5/2)), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]*Sqrt[c-d]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])]*Sqrt[2]/((c-d)^(5/2)*f*Sqrt[a])+2/3*d*Cos[e+f*x]/((c^2-d^2)*f*(c+d*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])+2/3*d*(5*c+d)*Cos[e+f*x]/((c^2-d^2)^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c+d*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:822
  public void test0570() {
    check( //
        "Integrate[(c+d*Sin[e+f*x])/(a+b*Sin[e+f*x]), x]", //
        "d*x/b+2*(b*c-a*d)*ArcTan[(b+a*Tan[1/2*(e+f*x)])/Sqrt[a^2-b^2]]/(b*f*Sqrt[a^2-b^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:922
  public void test0571() {
    check( //
        "Integrate[1/((a+b*Sin[e+f*x])^(1/2)*(c+d*Sin[e+f*x])^(3/2)), x]", //
        "2*(a-b)*d*EllipticE[ArcSin[Sqrt[c+d]*Sqrt[a+b*Sin[e+f*x]]/(Sqrt[a+b]*Sqrt[c+d*Sin[e+f*x]])],(a+b)*(c-d)/((a-b)*(c+d))]*Sec[e+f*x]*(c+d*Sin[e+f*x])*Sqrt[a+b]*Sqrt[(b*c-a*d)*(1-Sin[e+f*x])/((a+b)*(c+d*Sin[e+f*x]))]*Sqrt[-(b*c-a*d)*(1+Sin[e+f*x])/((a-b)*(c+d*Sin[e+f*x]))]/((c-d)*(b*c-a*d)^2*f*Sqrt[c+d])+2*EllipticF[ArcSin[Sqrt[c+d]*Sqrt[a+b*Sin[e+f*x]]/(Sqrt[a+b]*Sqrt[c+d*Sin[e+f*x]])],(a+b)*(c-d)/((a-b)*(c+d))]*Sec[e+f*x]*(c+d*Sin[e+f*x])*Sqrt[a+b]*Sqrt[(b*c-a*d)*(1-Sin[e+f*x])/((a+b)*(c+d*Sin[e+f*x]))]*Sqrt[-(b*c-a*d)*(1+Sin[e+f*x])/((a-b)*(c+d*Sin[e+f*x]))]/((c-d)*(b*c-a*d)*f*Sqrt[c+d])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:21
  public void test0572() {
    check( //
        "Integrate[Cos[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]]/(c-c*Sin[e+f*x])^(3/2), x]", //
        "-2*a*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:37
  public void test0573() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]], x]", //
        "1/10*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(a*f*Sqrt[c-c*Sin[e+f*x]])+1/5*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:53
  public void test0574() {
    check( //
        "Integrate[Cos[e+f*x]^2*(a+a*Sin[e+f*x])^(7/2)/(c-c*Sin[e+f*x])^(7/2), x]", //
        "1/2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(7/2)/(c*f*(c-c*Sin[e+f*x])^(5/2))-2*a*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c^2*f*(c-c*Sin[e+f*x])^(3/2))-3*a^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(c^3*f*Sqrt[c-c*Sin[e+f*x]])-24*a^4*Cos[e+f*x]*Log[1-Sin[e+f*x]]/(c^3*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-12*a^3*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/(c^3*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:71
  public void test0575() {
    check( //
        "Integrate[Cos[e+f*x]^2/((a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]]), x]", //
        "Cos[e+f*x]*Log[1+Sin[e+f*x]]/(a*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:101
  public void test0576() {
    check( //
        "Integrate[Cos[e+f*x]^2*(c+c*Sin[e+f*x])^m/Sqrt[a-a*Sin[e+f*x]], x]", //
        "2*Cos[e+f*x]*(c+c*Sin[e+f*x])^(1+m)/(c*f*(3+2*m)*Sqrt[a-a*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:131
  public void test0577() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(3/2)/Sqrt[c-c*Sin[e+f*x]], x]", //
        "-14/15*a^2*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+14/5*a^2*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-2/5*a*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:147
  public void test0578() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(5/2), x]", //
        "2/15*c*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(7/2)*(c-c*Sin[e+f*x])^(3/2)/(f*g)-2/39*a^2*c^3*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(3/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-14/585*a*c^3*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(5/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])+14/195*c^3*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(7/2)/(f*g*Sqrt[c-c*Sin[e+f*x]])-154/585*a^4*c^3*(g*Cos[e+f*x])^(5/2)/(f*g*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+154/195*a^4*c^3*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])-22/195*a^3*c^3*(g*Cos[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]/(f*g*Sqrt[c-c*Sin[e+f*x]])+22/195*c^2*(g*Cos[e+f*x])^(5/2)*(a+a*Sin[e+f*x])^(7/2)*Sqrt[c-c*Sin[e+f*x]]/(f*g)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:165
  public void test0579() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]]), x]", //
        "2/5*(g*Cos[e+f*x])^(5/2)/(f*g*(c-c*Sin[e+f*x])^(5/2)*Sqrt[a+a*Sin[e+f*x]])+2/5*(g*Cos[e+f*x])^(5/2)/(c*f*g*(c-c*Sin[e+f*x])^(3/2)*Sqrt[a+a*Sin[e+f*x]])-2/5*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(c^2*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:181
  public void test0580() {
    check( //
        "Integrate[(g*Cos[e+f*x])^(3/2)/((a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2)), x]", //
        "2*(g*Cos[e+f*x])^(5/2)/(f*g*(a+a*Sin[e+f*x])^(5/2)*(c-c*Sin[e+f*x])^(3/2))-6/5*(g*Cos[e+f*x])^(5/2)/(c*f*g*(a+a*Sin[e+f*x])^(5/2)*Sqrt[c-c*Sin[e+f*x]])-6/5*(g*Cos[e+f*x])^(5/2)/(a*c*f*g*(a+a*Sin[e+f*x])^(3/2)*Sqrt[c-c*Sin[e+f*x]])-6/5*g*EllipticE[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[g*Cos[e+f*x]]/(a^2*c*f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:245
  public void test0581() {
    check( //
        "Integrate[Cos[c+d*x]*Csc[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "-1/2*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^2/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:319
  public void test0582() {
    check( //
        "Integrate[Cos[c+d*x]*Sin[c+d*x]^n*(a+a*Sin[c+d*x])^4, x]", //
        "a^4*Sin[c+d*x]^(1+n)/(d*(1+n))+4*a^4*Sin[c+d*x]^(2+n)/(d*(2+n))+6*a^4*Sin[c+d*x]^(3+n)/(d*(3+n))+4*a^4*Sin[c+d*x]^(4+n)/(d*(4+n))+a^4*Sin[c+d*x]^(5+n)/(d*(5+n))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:346
  public void test0583() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*a^2*x-2*a^2*ArcTanh[Cos[c+d*x]]/d+2*a^2*Cos[c+d*x]/d-a^2*Cot[c+d*x]/d+1/2*a^2*Cos[c+d*x]*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:363
  public void test0584() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^2*(a+a*Sin[c+d*x])^4, x]", //
        "17/8*a^4*x-4*a^4*ArcTanh[Cos[c+d*x]]/d+4*a^4*Cos[c+d*x]/d-4/3*a^4*Cos[c+d*x]^3/d-a^4*Cot[c+d*x]/d+23/8*a^4*Cos[c+d*x]*Sin[c+d*x]/d+1/4*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:401
  public void test0585() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]], x]", //
        "3/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]*Sqrt[a]/d+3/8*a*Cot[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/12*a*Cot[c+d*x]*Csc[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-1/3*Cot[c+d*x]*Csc[c+d*x]^2*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:419
  public void test0586() {
    check( //
        "Integrate[Cos[c+d*x]^2*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(3/2)*d)+18/5*Cos[c+d*x]/(a*d*Sqrt[a+a*Sin[c+d*x]])-2/5*Cos[c+d*x]^3/(a*d*Sqrt[a+a*Sin[c+d*x]])-4/5*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:467
  public void test0587() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^8*(a+a*Sin[c+d*x]), x]", //
        "-1/16*a*ArcTanh[Cos[c+d*x]]/d-1/5*a*Cot[c+d*x]^5/d-1/7*a*Cot[c+d*x]^7/d-1/16*a*Cot[c+d*x]*Csc[c+d*x]/d+1/8*a*Cot[c+d*x]*Csc[c+d*x]^3/d-1/6*a*Cot[c+d*x]^3*Csc[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:483
  public void test0588() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^4*(a+a*Sin[c+d*x])^3, x]", //
        "15/256*a^3*x-4/5*a^3*Cos[c+d*x]^5/d+9/7*a^3*Cos[c+d*x]^7/d-2/3*a^3*Cos[c+d*x]^9/d+1/11*a^3*Cos[c+d*x]^11/d+15/256*a^3*Cos[c+d*x]*Sin[c+d*x]/d+5/128*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d-5/32*a^3*Cos[c+d*x]^5*Sin[c+d*x]/d-5/16*a^3*Cos[c+d*x]^5*Sin[c+d*x]^3/d-3/10*a^3*Cos[c+d*x]^5*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:499
  public void test0589() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^4, x]", //
        "-61/8*a^4*x+2*a^4*ArcTanh[Cos[c+d*x]]/d+4/3*a^4*Cos[c+d*x]^3/d-5*a^4*Cot[c+d*x]/d-1/3*a^4*Cot[c+d*x]^3/d-2*a^4*Cot[c+d*x]*Csc[c+d*x]/d-19/8*a^4*Cos[c+d*x]*Sin[c+d*x]/d-1/4*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:517
  public void test0590() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]/(a+a*Sin[c+d*x])^2, x]", //
        "-x/a^2-2/3*Cos[c+d*x]^3/(a^2*d)-Cos[c+d*x]*Sin[c+d*x]/(a^2*d)-Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:533
  public void test0591() {
    check( //
        "Integrate[Cos[e+f*x]^4*Sin[e+f*x]/(a+a*Sin[e+f*x])^6, x]", //
        "1/7*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^6)-6/35*Cos[e+f*x]^5/(a*f*(a+a*Sin[e+f*x])^5)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:554
  public void test0592() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^3*(a+a*Sin[c+d*x])^(3/2), x]", //
        "9/4*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-2/5*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d-1/2*Cot[c+d*x]*Csc[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/d+73/20*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-2/5*a*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d-3/4*a*Cot[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:572
  public void test0593() {
    check( //
        "Integrate[Cos[c+d*x]^4*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^(3/2), x]", //
        "-46/315*a*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(5/2))+20/63*Cos[c+d*x]^5/(d*(a+a*Sin[c+d*x])^(3/2))-2/9*Cos[c+d*x]^5/(a*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:588
  public void test0594() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^5/(a+a*Sin[c+d*x])^(5/2), x]", //
        "-363/64*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/(a^(5/2)*d)+4*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[2]/(a^(5/2)*d)+149/64*Cot[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-107/96*Cot[c+d*x]*Csc[c+d*x]/(a^2*d*Sqrt[a+a*Sin[c+d*x]])+17/24*Cot[c+d*x]*Csc[c+d*x]^2/(a^2*d*Sqrt[a+a*Sin[c+d*x]])-1/4*Cot[c+d*x]*Csc[c+d*x]^3/(a^2*d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:650
  public void test0595() {
    check( //
        "Integrate[Cos[c+d*x]^5*Csc[c+d*x]^6/(a+a*Sin[c+d*x]), x]", //
        "1/4*Cot[c+d*x]^4/(a*d)+1/3*Csc[c+d*x]^3/(a*d)-1/5*Csc[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:694
  public void test0596() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]*(a+a*Sin[c+d*x]), x]", //
        "5/128*a*x-1/7*a*Cos[c+d*x]^7/d+5/128*a*Cos[c+d*x]*Sin[c+d*x]/d+5/192*a*Cos[c+d*x]^3*Sin[c+d*x]/d+1/48*a*Cos[c+d*x]^5*Sin[c+d*x]/d-1/8*a*Cos[c+d*x]^7*Sin[c+d*x]/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:710
  public void test0597() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]*(a+a*Sin[c+d*x])^2, x]", //
        "5/64*a^2*x-1/28*a^2*Cos[c+d*x]^7/d+5/64*a^2*Cos[c+d*x]*Sin[c+d*x]/d+5/96*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d+1/24*a^2*Cos[c+d*x]^5*Sin[c+d*x]/d-1/9*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^2/d-1/36*Cos[c+d*x]^7*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:726
  public void test0598() {
    check( //
        "Integrate[Cos[c+d*x]^6*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "19/256*a^3*x-4/7*a^3*Cos[c+d*x]^7/d+5/9*a^3*Cos[c+d*x]^9/d-1/11*a^3*Cos[c+d*x]^11/d+19/256*a^3*Cos[c+d*x]*Sin[c+d*x]/d+19/384*a^3*Cos[c+d*x]^3*Sin[c+d*x]/d+19/480*a^3*Cos[c+d*x]^5*Sin[c+d*x]/d-19/80*a^3*Cos[c+d*x]^7*Sin[c+d*x]/d-3/10*a^3*Cos[c+d*x]^7*Sin[c+d*x]^3/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:742
  public void test0599() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^4*(a+a*Sin[c+d*x])^4, x]", //
        "-135/16*a^4*x+6*a^4*ArcTanh[Cos[c+d*x]]/d-4*a^4*Cos[c+d*x]/d+4/5*a^4*Cos[c+d*x]^5/d-4*a^4*Cot[c+d*x]/d-1/3*a^4*Cot[c+d*x]^3/d-2*a^4*Cot[c+d*x]*Csc[c+d*x]/d-89/16*a^4*Cos[c+d*x]*Sin[c+d*x]/d+23/24*a^4*Cos[c+d*x]*Sin[c+d*x]^3/d+1/6*a^4*Cos[c+d*x]*Sin[c+d*x]^5/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:760
  public void test0600() {
    check( //
        "Integrate[Cos[c+d*x]^6*Csc[c+d*x]^3/(a+a*Sin[c+d*x])^2, x]", //
        "2*x/a^2-1/2*ArcTanh[Cos[c+d*x]]/(a^2*d)+Cos[c+d*x]/(a^2*d)+2*Cot[c+d*x]/(a^2*d)-1/2*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:792
  public void test0601() {
    check( //
        "Integrate[Cos[c+d*x]^7*Sin[c+d*x]^3*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cos[c+d*x]^8/d+1/10*a*Cos[c+d*x]^10/d+1/5*a*Sin[c+d*x]^5/d-3/7*a*Sin[c+d*x]^7/d+1/3*a*Sin[c+d*x]^9/d-1/11*a*Sin[c+d*x]^11/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:808
  public void test0602() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^14*(a+a*Sin[c+d*x]), x]", //
        "-1/8*a*Cot[c+d*x]^8/d-1/5*a*Cot[c+d*x]^10/d-1/12*a*Cot[c+d*x]^12/d+1/7*a*Csc[c+d*x]^7/d-1/3*a*Csc[c+d*x]^9/d+3/11*a*Csc[c+d*x]^11/d-1/13*a*Csc[c+d*x]^13/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:826
  public void test0603() {
    check( //
        "Integrate[Cos[c+d*x]^7*Csc[c+d*x]^8/(a+a*Sin[c+d*x]), x]", //
        "1/6*Cot[c+d*x]^6/(a*d)-1/3*Csc[c+d*x]^3/(a*d)+2/5*Csc[c+d*x]^5/(a*d)-1/7*Csc[c+d*x]^7/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:856
  public void test0604() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]/(a+a*Sin[c+d*x]), x]", //
        "-5/128*x/a-1/7*Cos[c+d*x]^7/(a*d)-5/128*Cos[c+d*x]*Sin[c+d*x]/(a*d)-5/192*Cos[c+d*x]^3*Sin[c+d*x]/(a*d)-1/48*Cos[c+d*x]^5*Sin[c+d*x]/(a*d)+1/8*Cos[c+d*x]^7*Sin[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:872
  public void test0605() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]^2/(a+a*Sin[c+d*x])^2, x]", //
        "11/128*x/a^2+2/5*Cos[c+d*x]^5/(a^2*d)-2/7*Cos[c+d*x]^7/(a^2*d)+11/128*Cos[c+d*x]*Sin[c+d*x]/(a^2*d)+11/192*Cos[c+d*x]^3*Sin[c+d*x]/(a^2*d)-11/48*Cos[c+d*x]^5*Sin[c+d*x]/(a^2*d)-1/8*Cos[c+d*x]^5*Sin[c+d*x]^3/(a^2*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:888
  public void test0606() {
    check( //
        "Integrate[Cos[c+d*x]^8*Sin[c+d*x]/(a+a*Sin[c+d*x])^3, x]", //
        "-7/16*x/a^3-7/30*Cos[c+d*x]^5/(a^3*d)-7/16*Cos[c+d*x]*Sin[c+d*x]/(a^3*d)-7/24*Cos[c+d*x]^3*Sin[c+d*x]/(a^3*d)-1/3*Cos[c+d*x]^9/(d*(a+a*Sin[c+d*x])^3)-1/6*Cos[c+d*x]^7/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:932
  public void test0607() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-x/a-Sec[c+d*x]/(a*d)+1/3*Sec[c+d*x]^3/(a*d)+Tan[c+d*x]/(a*d)-1/3*Tan[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:948
  public void test0608() {
    check( //
        "Integrate[Sec[c+d*x]^2*Sin[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "Sec[c+d*x]/(a^3*d)-2*Sec[c+d*x]^3/(a^3*d)+9/5*Sec[c+d*x]^5/(a^3*d)-4/7*Sec[c+d*x]^7/(a^3*d)+1/5*Tan[c+d*x]^5/(a^3*d)+4/7*Tan[c+d*x]^7/(a^3*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1011
  public void test0609() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^2*(a+a*Sin[c+d*x])^2, x]", //
        "a^2*x-5/3*a^2*Cos[c+d*x]/(d*(1-Sin[c+d*x]))+1/3*a^4*Cos[c+d*x]/(d*(a-a*Sin[c+d*x])^2)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1029
  public void test0610() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sin[c+d*x]^5/(a+a*Sin[c+d*x]), x]", //
        "x/a+Sec[c+d*x]/(a*d)-2/3*Sec[c+d*x]^3/(a*d)+1/5*Sec[c+d*x]^5/(a*d)-Tan[c+d*x]/(a*d)+1/3*Tan[c+d*x]^3/(a*d)-1/5*Tan[c+d*x]^5/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1116
  public void test0611() {
    check( //
        "Integrate[Sec[c+d*x]^7*Sin[c+d*x]^2/(a+a*Sin[c+d*x]), x]", //
        "-5/128*ArcTanh[Sin[c+d*x]]/(a*d)+1/6*Sec[c+d*x]^6/(a*d)-1/8*Sec[c+d*x]^8/(a*d)-5/128*Sec[c+d*x]*Tan[c+d*x]/(a*d)-5/192*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)-1/48*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)+1/8*Sec[c+d*x]^7*Tan[c+d*x]/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1150
  public void test0612() {
    check( //
        "Integrate[Sec[c+d*x]^9*Sin[c+d*x]^4/(a+a*Sin[c+d*x]), x]", //
        "3/256*ArcTanh[Sin[c+d*x]]/(a*d)-1/6*Sec[c+d*x]^6/(a*d)+1/4*Sec[c+d*x]^8/(a*d)-1/10*Sec[c+d*x]^10/(a*d)+3/256*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/128*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+1/160*Sec[c+d*x]^5*Tan[c+d*x]/(a*d)-3/80*Sec[c+d*x]^7*Tan[c+d*x]/(a*d)+1/10*Sec[c+d*x]^7*Tan[c+d*x]^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1238
  public void test0613() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])*(A+B*Sin[c+d*x]), x]", //
        "1/8*a*(4*A+B)*x-1/12*a*(4*A+B)*Cos[c+d*x]^3/d+1/8*a*(4*A+B)*Cos[c+d*x]*Sin[c+d*x]/d-1/4*B*Cos[c+d*x]^3*(a+a*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1254
  public void test0614() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^2*(A+B*Sin[c+d*x]), x]", //
        "1/8*a^2*(5*A+2*B)*x-1/12*a^2*(5*A+2*B)*Cos[c+d*x]^3/d+1/8*a^2*(5*A+2*B)*Cos[c+d*x]*Sin[c+d*x]/d-1/5*B*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^2/d-1/20*(5*A+2*B)*Cos[c+d*x]^3*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1270
  public void test0615() {
    check( //
        "Integrate[Cos[c+d*x]^6*(a+a*Sin[c+d*x])^3*(A+B*Sin[c+d*x]), x]", //
        "11/256*a^3*(10*A+3*B)*x-11/560*a^3*(10*A+3*B)*Cos[c+d*x]^7/d+11/256*a^3*(10*A+3*B)*Cos[c+d*x]*Sin[c+d*x]/d+11/384*a^3*(10*A+3*B)*Cos[c+d*x]^3*Sin[c+d*x]/d+11/480*a^3*(10*A+3*B)*Cos[c+d*x]^5*Sin[c+d*x]/d-1/90*a*(10*A+3*B)*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^2/d-1/10*B*Cos[c+d*x]^7*(a+a*Sin[c+d*x])^3/d-11/720*(10*A+3*B)*Cos[c+d*x]^7*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1288
  public void test0616() {
    check( //
        "Integrate[Cos[c+d*x]^7*(A+B*Sin[c+d*x])/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*(A+B)*(a-a*Sin[c+d*x])^4/(a^6*d)+1/5*(A+3*B)*(a-a*Sin[c+d*x])^5/(a^7*d)-1/6*B*(a-a*Sin[c+d*x])^6/(a^8*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1366
  public void test0617() {
    check( //
        "Integrate[Cos[c+d*x]^2*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^2, x]", //
        "-b^2*x+a*b*ArcTanh[Cos[c+d*x]]/d+1/3*(a^2-2*b^2)*Cot[c+d*x]/d-1/3*a*b*Cot[c+d*x]*Csc[c+d*x]/d-1/3*Cot[c+d*x]*Csc[c+d*x]^2*(a+b*Sin[c+d*x])^2/d");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1429
  public void test0618() {
    check( //
        "Integrate[Cos[c+d*x]^4*Csc[c+d*x]^7*(a+b*Sin[c+d*x])^2, x]", //
        "-1/16*(a^2+6*b^2)*ArcTanh[Cos[c+d*x]]/d-2/5*a*b*Cot[c+d*x]/d-1/240*(15*a^4-80*a^2*b^2+12*b^4)*Cot[c+d*x]*Csc[c+d*x]/(a^2*d)+1/60*b*(13*a^2-2*b^2)*Cot[c+d*x]*Csc[c+d*x]^2/(a*d)+1/120*(35*a^2-6*b^2)*Cot[c+d*x]*Csc[c+d*x]^3*(a+b*Sin[c+d*x])^2/(a^2*d)+1/10*b*Cot[c+d*x]*Csc[c+d*x]^4*(a+b*Sin[c+d*x])^3/(a^2*d)-1/6*Cot[c+d*x]*Csc[c+d*x]^5*(a+b*Sin[c+d*x])^3/(a*d)");
  }

  // 4.1.2.2 (g cos)^p (a+b sin)^m (c+d sin)^n.input:1576
  public void test0619() {
    check( //
        "Integrate[Cos[c+d*x]^5*Sin[c+d*x]^n*(a+b*Sin[c+d*x])^2, x]", //
        "a^2*Sin[c+d*x]^(1+n)/(d*(1+n))+2*a*b*Sin[c+d*x]^(2+n)/(d*(2+n))-(2*a^2-b^2)*Sin[c+d*x]^(3+n)/(d*(3+n))-4*a*b*Sin[c+d*x]^(4+n)/(d*(4+n))+(a^2-2*b^2)*Sin[c+d*x]^(5+n)/(d*(5+n))+2*a*b*Sin[c+d*x]^(6+n)/(d*(6+n))+b^2*Sin[c+d*x]^(7+n)/(d*(7+n))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:75
  public void test0620() {
    check( //
        "Integrate[Sec[a+b*x]^4*Sin[a+b*x], x]", //
        "1/3*Sec[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:91
  public void test0621() {
    check( //
        "Integrate[Sec[a+b*x]^5*Sin[a+b*x]^2, x]", //
        "-1/8*ArcTanh[Sin[a+b*x]]/b-1/8*Sec[a+b*x]*Tan[a+b*x]/b+1/4*Sec[a+b*x]^3*Tan[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:107
  public void test0622() {
    check( //
        "Integrate[Cos[a+b*x]^7*Sin[a+b*x]^4, x]", //
        "1/5*Sin[a+b*x]^5/b-3/7*Sin[a+b*x]^7/b+1/3*Sin[a+b*x]^9/b-1/11*Sin[a+b*x]^11/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:123
  public void test0623() {
    check( //
        "Integrate[Sec[a+b*x]^7*Sin[a+b*x]^4, x]", //
        "1/16*ArcTanh[Sin[a+b*x]]/b+1/16*Sec[a+b*x]*Tan[a+b*x]/b-1/8*Sec[a+b*x]^3*Tan[a+b*x]/b+1/6*Sec[a+b*x]^3*Tan[a+b*x]^3/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:139
  public void test0624() {
    check( //
        "Integrate[Sec[a+b*x]^8*Sin[a+b*x]^5, x]", //
        "1/3*Sec[a+b*x]^3/b-2/5*Sec[a+b*x]^5/b+1/7*Sec[a+b*x]^7/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:157
  public void test0625() {
    check( //
        "Integrate[Sec[a+b*x]^3/Sin[a+b*x], x]", //
        "Log[Tan[a+b*x]]/b+1/2*Tan[a+b*x]^2/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:205
  public void test0626() {
    check( //
        "Integrate[Cos[a+b*x]^4/Sin[a+b*x]^5, x]", //
        "-3/8*ArcTanh[Cos[a+b*x]]/b+3/8*Cot[a+b*x]*Csc[a+b*x]/b-1/4*Cot[a+b*x]^3*Csc[a+b*x]/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:275
  public void test0627() {
    check( //
        "Integrate[Csc[a+b*x]^2/(d*Cos[a+b*x])^(5/2), x]", //
        "-Csc[a+b*x]/(b*d*(d*Cos[a+b*x])^(3/2))+5/3*Sin[a+b*x]/(b*d*(d*Cos[a+b*x])^(3/2))+5/3*EllipticF[1/2*(a+b*x),2]*Sqrt[Cos[a+b*x]]/(b*d^2*Sqrt[d*Cos[a+b*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:299
  public void test0628() {
    check( //
        "Integrate[Cos[x]^3/Sqrt[Sin[x]], x]", //
        "-2/5*Sin[x]^(5/2)+2*Sqrt[Sin[x]]");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:421
  public void test0629() {
    check( //
        "Integrate[(d*Cos[a+b*x])^n*Sin[a+b*x]^3, x]", //
        "-(d*Cos[a+b*x])^(1+n)/(b*d*(1+n))+(d*Cos[a+b*x])^(3+n)/(b*d^3*(3+n))");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:490
  public void test0630() {
    check( //
        "Integrate[Sin[e+f*x]^6/Sqrt[b*Sec[e+f*x]], x]", //
        "-8/39*b*Sin[e+f*x]/(f*(b*Sec[e+f*x])^(3/2))-20/117*b*Sin[e+f*x]^3/(f*(b*Sec[e+f*x])^(3/2))-2/13*b*Sin[e+f*x]^5/(f*(b*Sec[e+f*x])^(3/2))+16/39*EllipticE[1/2*(e+f*x),2]/(f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:506
  public void test0631() {
    check( //
        "Integrate[1/(b*Sec[e+f*x])^(3/2), x]", //
        "2/3*Sin[e+f*x]/(b*f*Sqrt[b*Sec[e+f*x]])+2/3*EllipticF[1/2*(e+f*x),2]*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]]/(b^2*f)");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:522
  public void test0632() {
    check( //
        "Integrate[Csc[e+f*x]^6/(b*Sec[e+f*x])^(5/2), x]", //
        "3/20*Csc[e+f*x]/(b*f*(b*Sec[e+f*x])^(3/2))+1/10*Csc[e+f*x]^3/(b*f*(b*Sec[e+f*x])^(3/2))-1/5*Csc[e+f*x]^5/(b*f*(b*Sec[e+f*x])^(3/2))+3/20*EllipticE[1/2*(e+f*x),2]/(b^2*f*Sqrt[Cos[e+f*x]]*Sqrt[b*Sec[e+f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:544
  public void test0633() {
    check( //
        "Integrate[1/(Sin[e+f*x]^(7/2)*Sqrt[b*Sec[e+f*x]]), x]", //
        "-2/5*b/(f*(b*Sec[e+f*x])^(3/2)*Sin[e+f*x]^(5/2))-4/5*b/(f*(b*Sec[e+f*x])^(3/2)*Sqrt[Sin[e+f*x]])-4/5*EllipticE[-1/4*Pi+e+f*x,2]*Sqrt[Sin[e+f*x]]/(f*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:560
  public void test0634() {
    check( //
        "Integrate[1/((b*Sec[e+f*x])^(3/2)*(a*Sin[e+f*x])^(9/2)), x]", //
        "(-2/7)/(a*b*f*(a*Sin[e+f*x])^(7/2)*Sqrt[b*Sec[e+f*x]])+2/21/(a^3*b*f*(a*Sin[e+f*x])^(3/2)*Sqrt[b*Sec[e+f*x]])-2/21*EllipticF[-1/4*Pi+e+f*x,2]*Sqrt[b*Sec[e+f*x]]*Sqrt[Sin[2*e+2*f*x]]/(a^4*b^2*f*Sqrt[a*Sin[e+f*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:26
  public void test0635() {
    check( //
        "Integrate[Cos[c+d*x]^4*(a+a*Sin[c+d*x])^2, x]", //
        "7/16*a^2*x-7/30*a^2*Cos[c+d*x]^5/d+7/16*a^2*Cos[c+d*x]*Sin[c+d*x]/d+7/24*a^2*Cos[c+d*x]^3*Sin[c+d*x]/d-1/6*Cos[c+d*x]^5*(a^2+a^2*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:42
  public void test0636() {
    check( //
        "Integrate[Cos[c+d*x]^2*(a+a*Sin[c+d*x])^3, x]", //
        "7/8*a^3*x-7/12*a^3*Cos[c+d*x]^3/d+7/8*a^3*Cos[c+d*x]*Sin[c+d*x]/d-1/5*a*Cos[c+d*x]^3*(a+a*Sin[c+d*x])^2/d-7/20*Cos[c+d*x]^3*(a^3+a^3*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:58
  public void test0637() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+a*Sin[c+d*x])^8, x]", //
        "-3003/16*a^8*x+1001/10*a^8*Cos[c+d*x]^5/d-3003/16*a^8*Cos[c+d*x]*Sin[c+d*x]/d-1001/8*a^8*Cos[c+d*x]^3*Sin[c+d*x]/d+2*a^15*Cos[c+d*x]^13/(d*(a-a*Sin[c+d*x])^7)+26*a^13*Cos[c+d*x]^11/(d*(a-a*Sin[c+d*x])^5)+286/3*a^14*Cos[c+d*x]^9/(d*(a^2-a^2*Sin[c+d*x])^3)+143/2*a^16*Cos[c+d*x]^7/(d*(a^8-a^8*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:76
  public void test0638() {
    check( //
        "Integrate[Cos[c+d*x]^7/(a+a*Sin[c+d*x])^2, x]", //
        "-1/2*(a-a*Sin[c+d*x])^4/(a^6*d)+1/5*(a-a*Sin[c+d*x])^5/(a^7*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:92
  public void test0639() {
    check( //
        "Integrate[Cos[c+d*x]^4/(a+a*Sin[c+d*x])^3, x]", //
        "-3*x/a^3-3*Cos[c+d*x]/(a^3*d)-2*Cos[c+d*x]^3/(a*d*(a+a*Sin[c+d*x])^2)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:128
  public void test0640() {
    check( //
        "Integrate[Sec[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-5/8*a^2*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(3/2))-5/8*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]*Sqrt[a]/(d*Sqrt[2])+5/6*a*Sec[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])+1/3*Sec[c+d*x]^3*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:230
  public void test0641() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^2*Sqrt[e*Cos[c+d*x]], x]", //
        "-14/15*a^2*(e*Cos[c+d*x])^(3/2)/(d*e)-2/5*(e*Cos[c+d*x])^(3/2)*(a^2+a^2*Sin[c+d*x])/(d*e)+14/5*a^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:246
  public void test0642() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^3/(e*Cos[c+d*x])^(11/2), x]", //
        "2/9*a^6*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a-a*Sin[c+d*x])^3)+2/15*a^5*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a-a*Sin[c+d*x])^2)+2/15*a^6*(e*Cos[c+d*x])^(3/2)/(d*e^7*(a^3-a^3*Sin[c+d*x]))-2/15*a^3*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*e^6*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:264
  public void test0643() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]), x]", //
        "2/3*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a*d*Sqrt[e*Cos[c+d*x]])-2/3*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:280
  public void test0644() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(11/2)/(a+a*Sin[c+d*x])^3, x]", //
        "18/5*e^3*(e*Cos[c+d*x])^(5/2)/(a^3*d)+4*e*(e*Cos[c+d*x])^(9/2)/(a*d*(a+a*Sin[c+d*x])^2)+6*e^6*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^3*d*Sqrt[e*Cos[c+d*x]])+6*e^5*Sin[c+d*x]*Sqrt[e*Cos[c+d*x]]/(a^3*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:296
  public void test0645() {
    check( //
        "Integrate[1/((a+a*Sin[c+d*x])^4*Sqrt[e*Cos[c+d*x]]), x]", //
        "2/33*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(a^4*d*Sqrt[e*Cos[c+d*x]])-2/15*Sqrt[e*Cos[c+d*x]]/(d*e*(a+a*Sin[c+d*x])^4)-14/165*Sqrt[e*Cos[c+d*x]]/(a*d*e*(a+a*Sin[c+d*x])^3)-2/33*Sqrt[e*Cos[c+d*x]]/(d*e*(a^2+a^2*Sin[c+d*x])^2)-2/33*Sqrt[e*Cos[c+d*x]]/(d*e*(a^4+a^4*Sin[c+d*x]))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:316
  public void test0646() {
    check( //
        "Integrate[(a+a*Sin[c+d*x])^(3/2)/(e*Cos[c+d*x])^(9/2), x]", //
        "-2*(a+a*Sin[c+d*x])^(3/2)/(d*e*(e*Cos[c+d*x])^(7/2))+8/3*(a+a*Sin[c+d*x])^(5/2)/(a*d*e*(e*Cos[c+d*x])^(7/2))-16/21*(a+a*Sin[c+d*x])^(7/2)/(a^2*d*e*(e*Cos[c+d*x])^(7/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:334
  public void test0647() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(5/2)*Sqrt[a+a*Sin[c+d*x]]), x]", //
        "16/15*(a+a*Sin[c+d*x])^(3/2)/(a^2*d*e*(e*Cos[c+d*x])^(3/2))+(-2/5)/(d*e*(e*Cos[c+d*x])^(3/2)*Sqrt[a+a*Sin[c+d*x]])-8/5*Sqrt[a+a*Sin[c+d*x]]/(a*d*e*(e*Cos[c+d*x])^(3/2))");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:350
  public void test0648() {
    check( //
        "Integrate[1/((e*Cos[c+d*x])^(3/2)*(a+a*Sin[c+d*x])^(5/2)), x]", //
        "(-2/11)/(d*e*(a+a*Sin[c+d*x])^(5/2)*Sqrt[e*Cos[c+d*x]])+(-12/77)/(a*d*e*(a+a*Sin[c+d*x])^(3/2)*Sqrt[e*Cos[c+d*x]])+(-16/77)/(a^2*d*e*Sqrt[e*Cos[c+d*x]]*Sqrt[a+a*Sin[c+d*x]])+32/77*Sqrt[a+a*Sin[c+d*x]]/(a^3*d*e*Sqrt[e*Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:423
  public void test0649() {
    check( //
        "Integrate[Sec[c+d*x]^3*(a+b*Sin[c+d*x]), x]", //
        "1/2*a*ArcTanh[Sin[c+d*x]]/d+1/2*Sec[c+d*x]^2*(b+a*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:439
  public void test0650() {
    check( //
        "Integrate[Sec[c+d*x]^2*(a+b*Sin[c+d*x])^2, x]", //
        "-b^2*x+a*b*Cos[c+d*x]/d+Sec[c+d*x]*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:455
  public void test0651() {
    check( //
        "Integrate[Sec[c+d*x]^10*(a+b*Sin[c+d*x])^3, x]", //
        "2/63*b*(4*a^2-b^2)*Sec[c+d*x]^5/d+1/9*Sec[c+d*x]^9*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^2/d+2/63*Sec[c+d*x]^7*(a+b*Sin[c+d*x])*(3*a*b+(4*a^2-b^2)*Sin[c+d*x])/d+2/21*a*(8*a^2-3*b^2)*Tan[c+d*x]/d+4/63*a*(8*a^2-3*b^2)*Tan[c+d*x]^3/d+2/105*a*(8*a^2-3*b^2)*Tan[c+d*x]^5/d");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:489
  public void test0652() {
    check( //
        "Integrate[Cos[c+d*x]^6/(a+b*Sin[c+d*x])^2, x]", //
        "-5/8*(8*a^4-12*a^2*b^2+3*b^4)*x/b^6+10*a*(a^2-b^2)^(3/2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/(b^6*d)+5/12*Cos[c+d*x]^3*(4*a-3*b*Sin[c+d*x])/(b^3*d)-Cos[c+d*x]^5/(b*d*(a+b*Sin[c+d*x]))-5/8*Cos[c+d*x]*(8*a*(a^2-b^2)-b*(4*a^2-3*b^2)*Sin[c+d*x])/(b^5*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:505
  public void test0653() {
    check( //
        "Integrate[Sec[c+d*x]^4/(a+b*Sin[c+d*x])^3, x]", //
        "5*b^4*(6*a^2+b^2)*ArcTan[(b+a*Tan[1/2*(c+d*x)])/Sqrt[a^2-b^2]]/((a^2-b^2)^(9/2)*d)+1/2*b*Sec[c+d*x]^3/((a^2-b^2)*d*(a+b*Sin[c+d*x])^2)+7/2*a*b*Sec[c+d*x]^3/((a^2-b^2)^2*d*(a+b*Sin[c+d*x]))-1/6*Sec[c+d*x]^3*(5*b*(6*a^2+b^2)-a*(2*a^2+33*b^2)*Sin[c+d*x])/((a^2-b^2)^3*d)+1/6*Sec[c+d*x]*(15*b^3*(6*a^2+b^2)+a*(4*a^4-28*a^2*b^2-81*b^4)*Sin[c+d*x])/((a^2-b^2)^4*d)");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:595
  public void test0654() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x]), x]", //
        "-2/7*b*(e*Cos[c+d*x])^(7/2)/(d*e)+2/5*a*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d+6/5*a*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:611
  public void test0655() {
    check( //
        "Integrate[(e*Cos[c+d*x])^(5/2)*(a+b*Sin[c+d*x])^3, x]", //
        "-2/231*b*(43*a^2+12*b^2)*(e*Cos[c+d*x])^(7/2)/(d*e)+2/15*a*(3*a^2+2*b^2)*e*(e*Cos[c+d*x])^(3/2)*Sin[c+d*x]/d-10/33*a*b*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])/(d*e)-2/11*b*(e*Cos[c+d*x])^(7/2)*(a+b*Sin[c+d*x])^2/(d*e)+2/5*a*(3*a^2+2*b^2)*e^2*EllipticE[1/2*(c+d*x),2]*Sqrt[e*Cos[c+d*x]]/(d*Sqrt[Cos[c+d*x]])");
  }

  // 4.1.1.2 (g cos)^p (a+b sin)^m.input:627
  public void test0656() {
    check( //
        "Integrate[(a+b*Sin[c+d*x])^4/(e*Cos[c+d*x])^(9/2), x]", //
        "2/7*(b+a*Sin[c+d*x])*(a+b*Sin[c+d*x])^3/(d*e*(e*Cos[c+d*x])^(7/2))-2/21*(a+b*Sin[c+d*x])^2*(a*b-(5*a^2-6*b^2)*Sin[c+d*x])/(d*e^3*(e*Cos[c+d*x])^(3/2))+2/21*(5*a^4-12*a^2*b^2+12*b^4)*EllipticF[1/2*(c+d*x),2]*Sqrt[Cos[c+d*x]]/(d*e^4*Sqrt[e*Cos[c+d*x]])+10/21*a*b*(a^2-2*b^2)*Sqrt[e*Cos[c+d*x]]/(d*e^5)+2/21*b*(5*a^2-6*b^2)*(a+b*Sin[c+d*x])*Sqrt[e*Cos[c+d*x]]/(d*e^5)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:73
  public void test0657() {
    check( //
        "Integrate[Cot[c+d*x]^8/(a+a*Sin[c+d*x]), x]", //
        "-5/16*ArcTanh[Cos[c+d*x]]/(a*d)-1/7*Cot[c+d*x]^7/(a*d)+5/16*Cot[c+d*x]*Csc[c+d*x]/(a*d)-5/24*Cot[c+d*x]^3*Csc[c+d*x]/(a*d)+1/6*Cot[c+d*x]^5*Csc[c+d*x]/(a*d)");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:89
  public void test0658() {
    check( //
        "Integrate[Cot[c+d*x]^3/(a+a*Sin[c+d*x])^3, x]", //
        "3*Csc[c+d*x]/(a^3*d)-1/2*Csc[c+d*x]^2/(a^3*d)+5*Log[Sin[c+d*x]]/(a^3*d)-5*Log[1+Sin[c+d*x]]/(a^3*d)+2/(d*(a^3+a^3*Sin[c+d*x]))");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:110
  public void test0659() {
    check( //
        "Integrate[Cot[e+f*x]^2*Sqrt[a+a*Sin[e+f*x]], x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]*Sqrt[a]/f+3*a*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-Cot[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:133
  public void test0660() {
    check( //
        "Integrate[Cot[e+f*x]^4/(a+a*Sin[e+f*x])^(5/2), x]", //
        "45/8*ArcTanh[Cos[e+f*x]*Sqrt[a]/Sqrt[a+a*Sin[e+f*x]]]/(a^(5/2)*f)-4*ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(a^(5/2)*f)-19/8*Cot[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])+13/12*Cot[e+f*x]*Csc[e+f*x]/(a^2*f*Sqrt[a+a*Sin[e+f*x]])-1/3*Cot[e+f*x]*Csc[e+f*x]^2/(a^2*f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.1.3 (g tan)^p (a+b sin)^m.input:188
  public void test0661() {
    check( //
        "Integrate[Cot[c+d*x]^3*(a+b*Sin[c+d*x])^2, x]", //
        "-2*a*b*Csc[c+d*x]/d-1/2*a^2*Csc[c+d*x]^2/d-(a^2-b^2)*Log[Sin[c+d*x]]/d-2*a*b*Sin[c+d*x]/d-1/2*b^2*Sin[c+d*x]^2/d");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:175
  public void test0662() {
    check( //
        "Integrate[x^3*(a+a*Sin[e+f*x])^(3/2), x]", //
        "-1280/9*a*Sqrt[a+a*Sin[e+f*x]]/f^4+16*a*x^2*Sqrt[a+a*Sin[e+f*x]]/f^2+640/9*a*x*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f^3-8/3*a*x^3*Cot[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f+32/9*a*x*Cos[1/4*Pi+1/2*e+1/2*f*x]*Sin[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f^3-4/3*a*x^3*Cos[1/4*Pi+1/2*e+1/2*f*x]*Sin[1/4*Pi+1/2*e+1/2*f*x]*Sqrt[a+a*Sin[e+f*x]]/f-64/27*a*Sin[1/4*Pi+1/2*e+1/2*f*x]^2*Sqrt[a+a*Sin[e+f*x]]/f^4+8/3*a*x^2*Sin[1/4*Pi+1/2*e+1/2*f*x]^2*Sqrt[a+a*Sin[e+f*x]]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:213
  public void test0663() {
    check( //
        "Integrate[(c+d*x)^2*(a+b*Sin[e+f*x]), x]", //
        "1/3*a*(c+d*x)^3/d+2*b*d^2*Cos[e+f*x]/f^3-b*(c+d*x)^2*Cos[e+f*x]/f+2*b*d*(c+d*x)*Sin[e+f*x]/f^2");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:233
  public void test0664() {
    check( //
        "Integrate[(c+d*x)/(a+b*Sin[e+f*x])^2, x]", //
        "-d*Log[a+b*Sin[e+f*x]]/((a^2-b^2)*f^2)-I*a*(c+d*x)*Log[1-I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/((a^2-b^2)^(3/2)*f)+I*a*(c+d*x)*Log[1-I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/((a^2-b^2)^(3/2)*f)-a*d*PolyLog[2,I*E^(I*(e+f*x))*b/(a-Sqrt[a^2-b^2])]/((a^2-b^2)^(3/2)*f^2)+a*d*PolyLog[2,I*E^(I*(e+f*x))*b/(a+Sqrt[a^2-b^2])]/((a^2-b^2)^(3/2)*f^2)+b*(c+d*x)*Cos[e+f*x]/((a^2-b^2)*f*(a+b*Sin[e+f*x]))");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:362
  public void test0665() {
    check( //
        "Integrate[(e+f*x)^3*Cos[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/8*f^3*x/(a*d^3)+1/4*(e+f*x)^3/(a*d)-6*f^3*Cos[c+d*x]/(a*d^4)+3*f*(e+f*x)^2*Cos[c+d*x]/(a*d^2)-6*f^2*(e+f*x)*Sin[c+d*x]/(a*d^3)+(e+f*x)^3*Sin[c+d*x]/(a*d)+3/8*f^3*Cos[c+d*x]*Sin[c+d*x]/(a*d^4)-3/4*f*(e+f*x)^2*Cos[c+d*x]*Sin[c+d*x]/(a*d^2)+3/4*f^2*(e+f*x)*Sin[c+d*x]^2/(a*d^3)-1/2*(e+f*x)^3*Sin[c+d*x]^2/(a*d)");
  }

  // 4.1.10 (c+d x)^m (a+b sin)^n.input:384
  public void test0666() {
    check( //
        "Integrate[(e+f*x)*Sec[c+d*x]^3/(a+a*Sin[c+d*x]), x]", //
        "-3/4*I*(e+f*x)*ArcTan[E^(I*(c+d*x))]/(a*d)+3/8*I*f*PolyLog[2,-I*E^(I*(c+d*x))]/(a*d^2)-3/8*I*f*PolyLog[2,I*E^(I*(c+d*x))]/(a*d^2)-3/8*f*Sec[c+d*x]/(a*d^2)-1/12*f*Sec[c+d*x]^3/(a*d^2)-1/4*(e+f*x)*Sec[c+d*x]^4/(a*d)+1/4*f*Tan[c+d*x]/(a*d^2)+3/8*(e+f*x)*Sec[c+d*x]*Tan[c+d*x]/(a*d)+1/4*(e+f*x)*Sec[c+d*x]^3*Tan[c+d*x]/(a*d)+1/12*f*Tan[c+d*x]^3/(a*d^2)");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:19
  public void test0667() {
    check( //
        "Integrate[x^2*(a+b*x)^2*Sin[c+d*x], x]", //
        "-24*b^2*Cos[c+d*x]/d^5+2*a^2*Cos[c+d*x]/d^3+12*a*b*x*Cos[c+d*x]/d^3+12*b^2*x^2*Cos[c+d*x]/d^3-a^2*x^2*Cos[c+d*x]/d-2*a*b*x^3*Cos[c+d*x]/d-b^2*x^4*Cos[c+d*x]/d-12*a*b*Sin[c+d*x]/d^4-24*b^2*x*Sin[c+d*x]/d^4+2*a^2*x*Sin[c+d*x]/d^2+6*a*b*x^2*Sin[c+d*x]/d^2+4*b^2*x^3*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:57
  public void test0668() {
    check( //
        "Integrate[x*(a+b*x^2)*Sin[c+d*x], x]", //
        "6*b*x*Cos[c+d*x]/d^3-a*x*Cos[c+d*x]/d-b*x^3*Cos[c+d*x]/d-6*b*Sin[c+d*x]/d^4+a*Sin[c+d*x]/d^2+3*b*x^2*Sin[c+d*x]/d^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:75
  public void test0669() {
    check( //
        "Integrate[x^3*Sin[c+d*x]/(a+b*x^2), x]", //
        "-x*Cos[c+d*x]/(b*d)+1/2*a*Cos[c+d*Sqrt[-a]/Sqrt[b]]*SinIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]/b^2-1/2*a*Cos[c-d*Sqrt[-a]/Sqrt[b]]*SinIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]/b^2+Sin[c+d*x]/(b*d^2)-1/2*a*CosIntegral[d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c-d*Sqrt[-a]/Sqrt[b]]/b^2-1/2*a*CosIntegral[-d*x+d*Sqrt[-a]/Sqrt[b]]*Sin[c+d*Sqrt[-a]/Sqrt[b]]/b^2");
  }

  // 4.1.11 (e x)^m (a+b x^n)^p sin.input:111
  public void test0670() {
    check( //
        "Integrate[(a+b*x^3)^2*Sin[c+d*x]/x^2, x]", //
        "a^2*d*CosIntegral[d*x]*Cos[c]-24*b^2*Cos[c+d*x]/d^5-2*a*b*x*Cos[c+d*x]/d+12*b^2*x^2*Cos[c+d*x]/d^3-b^2*x^4*Cos[c+d*x]/d-a^2*d*SinIntegral[d*x]*Sin[c]+2*a*b*Sin[c+d*x]/d^2-a^2*Sin[c+d*x]/x-24*b^2*x*Sin[c+d*x]/d^4+4*b^2*x^3*Sin[c+d*x]/d^2");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:95
  public void test0671() {
    check( //
        "Integrate[(a+b*Sin[c+d*x^3])^2/x^4, x]", //
        "1/6*(-2*a^2-b^2)/x^3+2/3*a*b*d*CosIntegral[d*x^3]*Cos[c]+1/6*b^2*Cos[2*(c+d*x^3)]/x^3+1/3*b^2*d*Cos[2*c]*SinIntegral[2*d*x^3]-2/3*a*b*d*SinIntegral[d*x^3]*Sin[c]+1/3*b^2*d*CosIntegral[2*d*x^3]*Sin[2*c]-2/3*a*b*Sin[c+d*x^3]/x^3");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:138
  public void test0672() {
    check( //
        "Integrate[x^2*Sin[a+b/x], x]", //
        "1/6*b^3*CosIntegral[b/x]*Cos[a]+1/6*b*x^2*Cos[a+b/x]-1/6*b^3*SinIntegral[b/x]*Sin[a]-1/6*b^2*x*Sin[a+b/x]+1/3*x^3*Sin[a+b/x]");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:244
  public void test0673() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b/(c+d*x)^3], x]", //
        "-1/6*I*E^(I*a)*f*(-I*b/(c+d*x)^3)^(2/3)*(c+d*x)^2*Gamma[-2/3,-I*b/(c+d*x)^3]/d^2+1/6*I*f*(I*b/(c+d*x)^3)^(2/3)*(c+d*x)^2*Gamma[-2/3,I*b/(c+d*x)^3]/(E^(I*a)*d^2)-1/6*I*E^(I*a)*(d*e-c*f)*(-I*b/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,-I*b/(c+d*x)^3]/d^2+1/6*I*(d*e-c*f)*(I*b/(c+d*x)^3)^(1/3)*(c+d*x)*Gamma[-1/3,I*b/(c+d*x)^3]/(E^(I*a)*d^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:270
  public void test0674() {
    check( //
        "Integrate[(e+f*x)*Sin[a+b/(c+d*x)^(3/2)], x]", //
        "-1/3*I*E^(I*a)*f*(-I*b/(c+d*x)^(3/2))^(4/3)*(c+d*x)^2*Gamma[-4/3,-I*b/(c+d*x)^(3/2)]/d^2+1/3*I*f*(I*b/(c+d*x)^(3/2))^(4/3)*(c+d*x)^2*Gamma[-4/3,I*b/(c+d*x)^(3/2)]/(E^(I*a)*d^2)-1/3*I*E^(I*a)*(d*e-c*f)*(-I*b/(c+d*x)^(3/2))^(2/3)*(c+d*x)*Gamma[-2/3,-I*b/(c+d*x)^(3/2)]/d^2+1/3*I*(d*e-c*f)*(I*b/(c+d*x)^(3/2))^(2/3)*(c+d*x)*Gamma[-2/3,I*b/(c+d*x)^(3/2)]/(E^(I*a)*d^2)");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:318
  public void test0675() {
    check( //
        "Integrate[Sin[a+b*(c+d*x)^(2/3)]/(c*e+d*e*x)^(5/3), x]", //
        "3/2*b*(c+d*x)^(2/3)*CosIntegral[b*(c+d*x)^(2/3)]*Cos[a]/(d*e*(e*(c+d*x))^(2/3))-3/2*b*(c+d*x)^(2/3)*SinIntegral[b*(c+d*x)^(2/3)]*Sin[a]/(d*e*(e*(c+d*x))^(2/3))-3/2*Sin[a+b*(c+d*x)^(2/3)]/(d*e*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:336
  public void test0676() {
    check( //
        "Integrate[Sin[a+b/(c+d*x)^(2/3)]/(c*e+d*e*x)^(8/3), x]", //
        "3/2*Cos[a+b/(c+d*x)^(2/3)]/(b*d*e^2*(c+d*x)^(1/3)*(e*(c+d*x))^(2/3))-9/4*(c+d*x)^(1/3)*Sin[a+b/(c+d*x)^(2/3)]/(b^2*d*e^2*(e*(c+d*x))^(2/3))+9/4*(c+d*x)^(2/3)*Cos[a]*FresnelS[Sqrt[2/Pi]*Sqrt[b]/(c+d*x)^(1/3)]*Sqrt[1/2*Pi]/(b^(5/2)*d*e^2*(e*(c+d*x))^(2/3))+9/4*(c+d*x)^(2/3)*FresnelC[Sqrt[2/Pi]*Sqrt[b]/(c+d*x)^(1/3)]*Sin[a]*Sqrt[1/2*Pi]/(b^(5/2)*d*e^2*(e*(c+d*x))^(2/3))");
  }

  // 4.1.12 (e x)^m (a+b sin(c+d x^n))^p.input:429
  public void test0677() {
    check( //
        "Integrate[x^m*(c*Sin[a+b*x^2]^3)^(1/3), x]", //
        "1/4*I*E^(I*a)*x^(1+m)*(-I*b*x^2)^(1/2*(-1-m))*Csc[a+b*x^2]*Gamma[1/2*(1+m),-I*b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)-1/4*I*x^(1+m)*(I*b*x^2)^(1/2*(-1-m))*Csc[a+b*x^2]*Gamma[1/2*(1+m),I*b*x^2]*(c*Sin[a+b*x^2]^3)^(1/3)/E^(I*a)");
  }

  // 4.1.13 (d+e x)^m sin(a+b x+c x^2)^n.input:43
  public void test0678() {
    check( //
        "Integrate[(d+e*x)*Sin[a+b*x+c*x^2]^2, x]", //
        "1/4*(d+e*x)^2/e-1/8*e*Sin[2*a+2*b*x+2*c*x^2]/c-1/8*(2*c*d-b*e)*Cos[2*a-1/2*b^2/c]*FresnelC[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sqrt[Pi]/c^(3/2)+1/8*(2*c*d-b*e)*FresnelS[(b+2*c*x)/(Sqrt[Pi]*Sqrt[c])]*Sin[2*a-1/2*b^2/c]*Sqrt[Pi]/c^(3/2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:49
  public void test0679() {
    check( //
        "Integrate[Sin[c+d*x]^4*Sqrt[a+a*Sin[c+d*x]], x]", //
        "-32/105*Cos[c+d*x]*(a+a*Sin[c+d*x])^(3/2)/(a*d)-32/45*a*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])-16/63*a*Cos[c+d*x]*Sin[c+d*x]^3/(d*Sqrt[a+a*Sin[c+d*x]])-2/9*a*Cos[c+d*x]*Sin[c+d*x]^4/(d*Sqrt[a+a*Sin[c+d*x]])+64/315*Cos[c+d*x]*Sqrt[a+a*Sin[c+d*x]]/d");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:65
  public void test0680() {
    check( //
        "Integrate[Csc[c+d*x]*(a+a*Sin[c+d*x])^(3/2), x]", //
        "-2*a^(3/2)*ArcTanh[Cos[c+d*x]*Sqrt[a]/Sqrt[a+a*Sin[c+d*x]]]/d-2*a^2*Cos[c+d*x]/(d*Sqrt[a+a*Sin[c+d*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:99
  public void test0681() {
    check( //
        "Integrate[Sin[c+d*x]/(a+a*Sin[c+d*x])^(5/2), x]", //
        "1/4*Cos[c+d*x]/(d*(a+a*Sin[c+d*x])^(5/2))-5/16*Cos[c+d*x]/(a*d*(a+a*Sin[c+d*x])^(3/2))-5/16*ArcTanh[Cos[c+d*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[c+d*x]])]/(a^(5/2)*d*Sqrt[2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:197
  public void test0682() {
    check( //
        "Integrate[Csc[e+f*x]^2*(a+b*Sin[e+f*x]), x]", //
        "-b*ArcTanh[Cos[e+f*x]]/f-a*Cot[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:215
  public void test0683() {
    check( //
        "Integrate[Csc[e+f*x]^3*(a+b*Sin[e+f*x])^3, x]", //
        "b^3*x-1/2*a*(a^2+6*b^2)*ArcTanh[Cos[e+f*x]]/f-5/2*a^2*b*Cot[e+f*x]/f-1/2*a^2*Cot[e+f*x]*Csc[e+f*x]*(a+b*Sin[e+f*x])/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:233
  public void test0684() {
    check( //
        "Integrate[Sin[x]/(a+b*Sin[x])^2, x]", //
        "-2*b*ArcTan[(b+a*Tan[1/2*x])/Sqrt[a^2-b^2]]/(a^2-b^2)^(3/2)-a*Cos[x]/((a^2-b^2)*(a+b*Sin[x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:306
  public void test0685() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2*(c-c*Sin[e+f*x])^2, x]", //
        "3/8*a^2*c^2*x+3/8*a^2*c^2*Cos[e+f*x]*Sin[e+f*x]/f+1/4*a^2*c^2*Cos[e+f*x]^3*Sin[e+f*x]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:322
  public void test0686() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^3/(c-c*Sin[e+f*x])^3, x]", //
        "-a^3*x/c^3+2/5*a^3*c^2*Cos[e+f*x]^5/(f*(c-c*Sin[e+f*x])^5)-2/3*a^3*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^3)+2*a^3*Cos[e+f*x]/(f*(c^3-c^3*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:340
  public void test0687() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^3/(a+a*Sin[e+f*x])^2, x]", //
        "5*c^3*x/a^2+5*c^3*Cos[e+f*x]/(a^2*f)-2/3*a^2*c^3*Cos[e+f*x]^5/(f*(a+a*Sin[e+f*x])^4)+10/3*c^3*Cos[e+f*x]^3/(f*(a+a*Sin[e+f*x])^2)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:356
  public void test0688() {
    check( //
        "Integrate[1/((a+a*Sin[e+f*x])^3*(c-c*Sin[e+f*x])^4), x]", //
        "1/7*Sec[e+f*x]^5/(a^3*f*(c^4-c^4*Sin[e+f*x]))+6/7*Tan[e+f*x]/(a^3*c^4*f)+4/7*Tan[e+f*x]^3/(a^3*c^4*f)+6/35*Tan[e+f*x]^5/(a^3*c^4*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:376
  public void test0689() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^2/(c-c*Sin[e+f*x])^(3/2), x]", //
        "a^2*c*Cos[e+f*x]^3/(f*(c-c*Sin[e+f*x])^(5/2))-3*a^2*ArcTanh[Cos[e+f*x]*Sqrt[c]/(Sqrt[2]*Sqrt[c-c*Sin[e+f*x]])]*Sqrt[2]/(c^(3/2)*f)+3*a^2*Cos[e+f*x]/(c*f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:394
  public void test0690() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x]), x]", //
        "2*Sec[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(a*f)-8*c*Sec[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(a*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:410
  public void test0691() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(3/2)/(a+a*Sin[e+f*x])^3, x]", //
        "8/15*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(5/2)/(a^3*c*f)-2/3*Sec[e+f*x]^5*(c-c*Sin[e+f*x])^(7/2)/(a^3*c^2*f)");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:430
  public void test0692() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c-c*Sin[e+f*x])^(1/2), x]", //
        "1/2*c*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/(f*Sqrt[c-c*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:446
  public void test0693() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(5/2)/(c-c*Sin[e+f*x])^(11/2), x]", //
        "1/10*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(f*(c-c*Sin[e+f*x])^(11/2))+1/40*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c*f*(c-c*Sin[e+f*x])^(9/2))+1/240*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(c^2*f*(c-c*Sin[e+f*x])^(7/2))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:464
  public void test0694() {
    check( //
        "Integrate[(c-c*Sin[e+f*x])^(5/2)/(a+a*Sin[e+f*x])^(1/2), x]", //
        "1/2*c*Cos[e+f*x]*(c-c*Sin[e+f*x])^(3/2)/(f*Sqrt[a+a*Sin[e+f*x]])+4*c^3*Cos[e+f*x]*Log[1+Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]]*Sqrt[c-c*Sin[e+f*x]])+2*c^2*Cos[e+f*x]*Sqrt[c-c*Sin[e+f*x]]/(f*Sqrt[a+a*Sin[e+f*x]])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:521
  public void test0695() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])/(c+d*Sin[e+f*x]), x]", //
        "a*x/d-2*a*(c-d)*ArcTan[(d+c*Tan[1/2*(e+f*x)])/Sqrt[c^2-d^2]]/(d*f*Sqrt[c^2-d^2])");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:559
  public void test0696() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x])^2, x]", //
        "-1/3*Cos[e+f*x]/(f*(a+a*Sin[e+f*x])^2)-1/3*Cos[e+f*x]/(f*(a^2+a^2*Sin[e+f*x]))");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:633
  public void test0697() {
    check( //
        "Integrate[(a+a*Sin[e+f*x])^(3/2)*(c+d*Sin[e+f*x])^2, x]", //
        "-4/35*(7*c-d)*d*Cos[e+f*x]*(a+a*Sin[e+f*x])^(3/2)/f-2/7*d^2*Cos[e+f*x]*(a+a*Sin[e+f*x])^(5/2)/(a*f)-8/105*a^2*(35*c^2+42*c*d+19*d^2)*Cos[e+f*x]/(f*Sqrt[a+a*Sin[e+f*x]])-2/105*a*(35*c^2+42*c*d+19*d^2)*Cos[e+f*x]*Sqrt[a+a*Sin[e+f*x]]/f");
  }

  // 4.1.2.1 (a+b sin)^m (c+d sin)^n.input:651
  public void test0698() {
    check( //
        "Integrate[1/(a+a*Sin[e+f*x])^(1/2), x]", //
        "-ArcTanh[Cos[e+f*x]*Sqrt[a]/(Sqrt[2]*Sqrt[a+a*Sin[e+f*x]])]*Sqrt[2]/(f*Sqrt[a])");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:76
  public void test0699() {
    check( //
        "Integrate[Cos[a+b*x]^7*Sin[a+b*x]^2, x]", //
        "1/3*Sin[a+b*x]^3/b-3/5*Sin[a+b*x]^5/b+3/7*Sin[a+b*x]^7/b-1/9*Sin[a+b*x]^9/b");
  }

  // 4.1.0 (a sin)^m (b trg)^n.input:92
  public void test0700() {
    check( //
        "Integrate[Sec[a+b*x]^7*Sin[a+b*x]^2, x]", //
        "-1/16*ArcTanh[Sin[a+b*x]]/b-1/16*Sec[a+b*x]*Tan[a+b*x]/b-1/24*Sec[a+b*x]^3*Tan[a+b*x]/b+1/6*Sec[a+b*x]^5*Tan[a+b*x]/b");
  }
}
