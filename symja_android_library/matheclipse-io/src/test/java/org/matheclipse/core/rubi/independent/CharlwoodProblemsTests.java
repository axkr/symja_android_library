package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Rubi's "0 Independent test suites" corpus section. 50 integrals.
 *
 * <p>
 * <b>Not part of a normal build.</b> The surefire {@code <includes>} in the parent pom match
 * {@code Test*.java}, {@code *Test.java} and {@code *TestCase.java}, so nothing here runs
 * unless it is asked for. That is deliberate - the section is slow - but it means drift goes
 * unnoticed, so run it after any change to the integrator:
 *
 * <pre>
 * mvn -o -pl matheclipse-io test -DreuseForks=false -DforkCount=5 -DfailIfNoTests=false \
 *     -Dtest='org.matheclipse.core.rubi.independent.**'
 * </pre>
 *
 * <p>
 * {@code -DreuseForks=false} is required, not an optimisation: {@code AbstractRubiTestCase}'s
 * constructor sets the global {@code ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS}, so sharing one
 * JVM across classes makes every test fail.
 *
 * <p>
 * Tests marked {@code KNOWN GAP} are expected to fail: their expected value is Rubi's reference
 * and Symja has no answer for that integral yet.
 */
public class CharlwoodProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public CharlwoodProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("Charlwood");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[ArcSin[x]*Log[x], x]", //
        "-2*Sqrt[1-x^2]+ArcTanh[Sqrt[1-x^2]]-x*ArcSin[x]*(1-Log[x])+Sqrt[1-x^2]*Log[x]" //
    );
  }

  public void test2() {
    check( //
        "Integrate[x*ArcSin[x]/Sqrt[1-x^2], x]", //
        "x-Sqrt[1-x^2]*ArcSin[x]" //
    );
  }

  public void test3() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja returns only a partial result. It used to leak Rubi`subst into the
    // answer; that leak is fixed, so the integral now comes back unevaluated instead.
    check( //
        "Integrate[ArcSin[Sqrt[x+1]-Sqrt[x]], x]", //
        "((Sqrt[x]+3*Sqrt[1+x])*Sqrt[-x+Sqrt[x]*Sqrt[1+x]])/(4*Sqrt[2])-(3/8+x)*ArcSin[Sqrt[x]-Sqrt[1+x]]" //
    );
  }

  public void test4() {
    check( //
        "Integrate[Log[1+x*Sqrt[1+x^2]], x]", //
        "-2*x+Sqrt[2*(1+Sqrt[5])]*ArcTan[Sqrt[-2+Sqrt[5]]*(x+Sqrt[1+x^2])]-Sqrt[2*(-1+Sqrt[5])]*ArcTanh[Sqrt[2+Sqrt[5]]*(x+Sqrt[1+x^2])]+x*Log[1+x*Sqrt[1+x^2]]" //
    );
  }

  public void test5() {
    // KNOWN GAP - this test does not pass. Symja's answer is correct (verified by differentiating
    // it back) but is a bulky EllipticF/EllipticPi form rather than the compact ArcTan reference,
    // and takes ~12s, so it also exceeds this class's 20s budget once the machine is busy. Do not
    // raise fSeconds to "fix" it: the budget decides which strategy wins, and raising it to 60
    // makes test4/test47/test50 return a different form and fail.
    check( //
        "Integrate[Cos[x]^2/Sqrt[Cos[x]^4+Cos[x]^2+1], x]", //
        "x/3+ArcTan[(Cos[x]*(1+Cos[x]^2)*Sin[x])/(1+Cos[x]^2*Sqrt[1+Cos[x]^2+Cos[x]^4])]/3" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Tan[x]*Sqrt[1+Tan[x]^4], x]", //
        "-ArcSinh[Tan[x]^2]/2-ArcTanh[(1-Tan[x]^2)/(Sqrt[2]*Sqrt[1+Tan[x]^4])]/Sqrt[2]+Sqrt[1+Tan[x]^4]/2" //
    );
  }

  public void test7() {
    check( //
        "Integrate[Tan[x]/Sqrt[1+Sec[x]^3], x]", //
        "-2/3*ArcTanh[Sqrt[1+Sec[x]^3]]" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Sqrt[Tan[x]^2+2*Tan[x]+2], x]", //
        "ArcSinh[1+Tan[x]]-Sqrt[1/2*(1+Sqrt[5])]*ArcTan[(2*Sqrt[5]-(5+Sqrt[5])*Tan[x])/(Sqrt[10*(1+Sqrt[5])]*Sqrt[2+2*Tan[x]+Tan[x]^2])]-Sqrt[1/2*(-1+Sqrt[5])]*ArcTanh[(2*Sqrt[5]+(5-Sqrt[5])*Tan[x])/(Sqrt[10*(-1+Sqrt[5])]*Sqrt[2+2*Tan[x]+Tan[x]^2])]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[Sin[x]*ArcTan[Sqrt[Sec[x]+(-1)*1]], x]", //
        "ArcTan[Sqrt[-1+Sec[x]]]/2-ArcTan[Sqrt[-1+Sec[x]]]*Cos[x]+1/2*Cos[x]*Sqrt[-1+Sec[x]]" //
    );
  }

  public void test10() {
    check( //
        "Integrate[x^3*E^ArcSin[x]/Sqrt[1-x^2], x]", //
        "1/10*E^ArcSin[x]*(3*x+x^3-3*Sqrt[1-x^2]-3*x^2*Sqrt[1-x^2])" //
    );
  }

  public void test11() {
    check( //
        "Integrate[(x*Log[1+x^2]*Log[x+Sqrt[1+x^2]])/Sqrt[1+x^2], x]", //
        "4*x-2*ArcTan[x]-x*Log[1+x^2]-2*Sqrt[1+x^2]*Log[x+Sqrt[1+x^2]]+Sqrt[1+x^2]*Log[1+x^2]*Log[x+Sqrt[1+x^2]]" //
    );
  }

  public void test12() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja returns a partial result containing an unevaluated Integrate.
    check( //
        "Integrate[ArcTan[x+Sqrt[1-x^2]], x]", //
        "-ArcSin[x]/2+1/4*Sqrt[3]*ArcTan[(-1+Sqrt[3]*x)/Sqrt[1-x^2]]+1/4*Sqrt[3]*ArcTan[(1+Sqrt[3]*x)/Sqrt[1-x^2]]-1/4*Sqrt[3]*ArcTan[(-1+2*x^2)/Sqrt[3]]+x*ArcTan[x+Sqrt[1-x^2]]-ArcTanh[x*Sqrt[1-x^2]]/4-Log[1-x^2+x^4]/8" //
    );
  }

  public void test13() {
    check( //
        "Integrate[x*ArcTan[x+Sqrt[1-x^2]]/Sqrt[1-x^2], x]", //
        "-ArcSin[x]/2+1/4*Sqrt[3]*ArcTan[(-1+Sqrt[3]*x)/Sqrt[1-x^2]]+1/4*Sqrt[3]*ArcTan[(1+Sqrt[3]*x)/Sqrt[1-x^2]]-1/4*Sqrt[3]*ArcTan[(-1+2*x^2)/Sqrt[3]]-Sqrt[1-x^2]*ArcTan[x+Sqrt[1-x^2]]+ArcTanh[x*Sqrt[1-x^2]]/4+Log[1-x^2+x^4]/8" //
    );
  }

  public void test14() {
    check( //
        "Integrate[ArcSin[x]/(1+Sqrt[1-x^2]), x]", //
        "-(x*ArcSin[x])/(1+Sqrt[1-x^2])+ArcSin[x]^2/2-Log[1+Sqrt[1-x^2]]" //
    );
  }

  public void test15() {
    check( //
        "Integrate[Log[x+Sqrt[1+x^2]]/(1-x^2)^(3/2), x]", //
        "-ArcSin[x^2]/2+(x*Log[x+Sqrt[1+x^2]])/Sqrt[1-x^2]" //
    );
  }

  public void test16() {
    check( //
        "Integrate[ArcSin[x]/(1+x^2)^(3/2), x]", //
        "(x*ArcSin[x])/Sqrt[1+x^2]-ArcSin[x^2]/2" //
    );
  }

  public void test17() {
    check( //
        "Integrate[Log[x+Sqrt[x^2+(-1)*1]]/(1+x^2)^(3/2), x]", //
        "-ArcCosh[x^2]/2+(x*Log[x+Sqrt[-1+x^2]])/Sqrt[1+x^2]" //
    );
  }

  public void test18() {
    check( //
        "Integrate[Log[x]/(x^2*Sqrt[x^2+(-1)*1]), x]", //
        "Sqrt[-1+x^2]/x-ArcTanh[x/Sqrt[-1+x^2]]+(Sqrt[-1+x^2]*Log[x])/x" //
    );
  }

  public void test19() {
    check( //
        "Integrate[Sqrt[1+x^3]/x, x]", //
        "1/3*2*Sqrt[1+x^3]-2/3*ArcTanh[Sqrt[1+x^3]]" //
    );
  }

  public void test20() {
    check( //
        "Integrate[x*Log[x+Sqrt[x^2+(-1)*1]]/Sqrt[x^2+(-1)*1], x]", //
        "-x+Sqrt[-1+x^2]*Log[x+Sqrt[-1+x^2]]" //
    );
  }

  public void test21() {
    check( //
        "Integrate[x^3*ArcSin[x]/Sqrt[1-x^4], x]", //
        "1/4*x*Sqrt[1+x^2]-1/2*Sqrt[1-x^4]*ArcSin[x]+ArcSinh[x]/4" //
    );
  }

  public void test22() {
    check( //
        "Integrate[x^3*ArcSec[x]/Sqrt[x^4+(-1)*1], x]", //
        "-Sqrt[-1+x^4]/(2*Sqrt[1-1/x^2]*x)+1/2*Sqrt[-1+x^4]*ArcSec[x]+ArcTanh[(Sqrt[1-1/x^2]*x)/Sqrt[-1+x^4]]/2" //
    );
  }

  public void test23() {
    check( //
        "Integrate[x*ArcTan[x]*Log[x+Sqrt[1+x^2]]/Sqrt[1+x^2], x]", //
        "-x*ArcTan[x]+Log[1+x^2]/2+Sqrt[1+x^2]*ArcTan[x]*Log[x+Sqrt[1+x^2]]-Log[x+Sqrt[1+x^2]]^2/2" //
    );
  }

  public void test24() {
    check( //
        "Integrate[x*Log[1+Sqrt[1-x^2]]/Sqrt[1-x^2], x]", //
        "Sqrt[1-x^2]-Log[1+Sqrt[1-x^2]]-Sqrt[1-x^2]*Log[1+Sqrt[1-x^2]]" //
    );
  }

  public void test25() {
    check( //
        "Integrate[x*Log[x+Sqrt[1+x^2]]/Sqrt[1+x^2], x]", //
        "-x+Sqrt[1+x^2]*Log[x+Sqrt[1+x^2]]" //
    );
  }

  public void test26() {
    check( //
        "Integrate[x*Log[x+Sqrt[1-x^2]]/Sqrt[1-x^2], x]", //
        "Sqrt[1-x^2]+ArcTanh[Sqrt[2]*x]/Sqrt[2]-ArcTanh[Sqrt[2]*Sqrt[1-x^2]]/Sqrt[2]-Sqrt[1-x^2]*Log[x+Sqrt[1-x^2]]" //
    );
  }

  public void test27() {
    check( //
        "Integrate[Log[x]/(x^2*Sqrt[1-x^2]), x]", //
        "-Sqrt[1-x^2]/x-ArcSin[x]-(Sqrt[1-x^2]*Log[x])/x" //
    );
  }

  public void test28() {
    check( //
        "Integrate[x*ArcTan[x]/Sqrt[1+x^2], x]", //
        "-ArcSinh[x]+Sqrt[1+x^2]*ArcTan[x]" //
    );
  }

  public void test29() {
    check( //
        "Integrate[ArcTan[x]/(x^2*Sqrt[1-x^2]), x]", //
        "-(Sqrt[1-x^2]*ArcTan[x])/x-ArcTanh[Sqrt[1-x^2]]+Sqrt[2]*ArcTanh[Sqrt[1-x^2]/Sqrt[2]]" //
    );
  }

  public void test30() {
    check( //
        "Integrate[x*ArcTan[x]/Sqrt[1-x^2], x]", //
        "-ArcSin[x]-Sqrt[1-x^2]*ArcTan[x]+Sqrt[2]*ArcTan[(Sqrt[2]*x)/Sqrt[1-x^2]]" //
    );
  }

  public void test31() {
    check( //
        "Integrate[ArcTan[x]/(x^2*Sqrt[1+x^2]), x]", //
        "-(Sqrt[1+x^2]*ArcTan[x])/x-ArcTanh[Sqrt[1+x^2]]" //
    );
  }

  public void test32() {
    check( //
        "Integrate[ArcSin[x]/(x^2*Sqrt[1-x^2]), x]", //
        "-(Sqrt[1-x^2]*ArcSin[x])/x+Log[x]" //
    );
  }

  public void test33() {
    check( //
        "Integrate[x*Log[x]/Sqrt[x^2+(-1)*1], x]", //
        "-Sqrt[-1+x^2]+ArcTan[Sqrt[-1+x^2]]+Sqrt[-1+x^2]*Log[x]" //
    );
  }

  public void test34() {
    check( //
        "Integrate[Log[x]/(x^2*Sqrt[1+x^2]), x]", //
        "-Sqrt[1+x^2]/x+ArcSinh[x]-(Sqrt[1+x^2]*Log[x])/x" //
    );
  }

  public void test35() {
    check( //
        "Integrate[x*ArcSec[x]/Sqrt[x^2+(-1)*1], x]", //
        "Sqrt[-1+x^2]*ArcSec[x]-(x*Log[x])/Sqrt[x^2]" //
    );
  }

  public void test36() {
    check( //
        "Integrate[x*Log[x]/Sqrt[1+x^2], x]", //
        "-Sqrt[1+x^2]+ArcTanh[Sqrt[1+x^2]]+Sqrt[1+x^2]*Log[x]" //
    );
  }

  public void test37() {
    check( //
        "Integrate[Sin[x]/(1+Sin[x]^2), x]", //
        "-ArcTanh[Cos[x]/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test38() {
    check( //
        "Integrate[(1+x^2)/((1-x^2)*Sqrt[1+x^4]), x]", //
        "ArcTanh[Sqrt[2]*x/Sqrt[1+x^4]]/Sqrt[2]" //
    );
  }

  public void test39() {
    check( //
        "Integrate[(1-x^2)/((1+x^2)*Sqrt[1+x^4]), x]", //
        "ArcTan[(Sqrt[2]*x)/Sqrt[1+x^4]]/Sqrt[2]" //
    );
  }

  public void test40() {
    check( //
        "Integrate[Log[Sin[x]]/(1+Sin[x]), x]", //
        "-x-ArcTanh[Cos[x]]-(Cos[x]*Log[Sin[x]])/(1+Sin[x])" //
    );
  }

  public void test41() {
    check( //
        "Integrate[Log[Sin[x]]*Sqrt[1+Sin[x]], x]", //
        "(4*Cos[x])/Sqrt[1+Sin[x]]-(2*Cos[x]*Log[Sin[x]])/Sqrt[1+Sin[x]]-4*ArcTanh[Cos[x]/Sqrt[1+Sin[x]]]" //
    );
  }

  public void test42() {
    check( //
        "Integrate[Sec[x]/Sqrt[Sec[x]^4+(-1)*1], x]", //
        "-ArcTanh[(Cos[x]*Cot[x]*Sqrt[Sec[x]^4+(-1)*1])/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test43() {
    check( //
        "Integrate[Tan[x]/Sqrt[1+Tan[x]^4], x]", //
        "-ArcTanh[(1-Tan[x]^2)/(Sqrt[2]*Sqrt[1+Tan[x]^4])]/(2*Sqrt[2])" //
    );
  }

  public void test44() {
    check( //
        "Integrate[Sin[x]/Sqrt[1-Sin[x]^6], x]", //
        "ArcTanh[(Sqrt[3]*Cos[x]*(1+Sin[x]^2))/(2*Sqrt[1-Sin[x]^6])]/(2*Sqrt[3])" //
    );
  }

  public void test45() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja leaves this unevaluated: rule 3054 reports 'Endless iteration
    // detected' on it.
    check( //
        "Integrate[Sqrt[Sqrt[Sec[x]+1]-Sqrt[Sec[x]+(-1)*1]], x]", //
        "Sqrt[2]*(Sqrt[-1+Sqrt[2]]*ArcTan[(Sqrt[-2+2*Sqrt[2]]*(-Sqrt[2]-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]))/(2*Sqrt[-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]])]-Sqrt[1+Sqrt[2]]*ArcTan[(Sqrt[2+2*Sqrt[2]]*(-Sqrt[2]-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]))/(2*Sqrt[-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]])]-Sqrt[1+Sqrt[2]]*ArcTanh[(Sqrt[-2+2*Sqrt[2]]*Sqrt[-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]])/(Sqrt[2]-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]])]+Sqrt[-1+Sqrt[2]]*ArcTanh[(Sqrt[2+2*Sqrt[2]]*Sqrt[-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]]])/(Sqrt[2]-Sqrt[-1+Sec[x]]+Sqrt[1+Sec[x]])])*Cot[x]*Sqrt[-1+Sec[x]]*Sqrt[1+Sec[x]]" //
    );
  }

  public void test46() {
    check( //
        "Integrate[x*Log[x^2+1]*ArcTan[x]^2, x]", //
        "3*x*ArcTan[x]-1/2*3*ArcTan[x]^2-1/2*x^2*ArcTan[x]^2-3/2*Log[1+x^2]-x*ArcTan[x]*Log[1+x^2]+1/2*(1+x^2)*ArcTan[x]^2*Log[1+x^2]+Log[1+x^2]^2/4" //
    );
  }

  public void test47() {
    check( //
        "Integrate[ArcTan[x*Sqrt[1+x^2]], x]", //
        "x*ArcTan[x*Sqrt[1+x^2]]+ArcTan[Sqrt[3]-2*Sqrt[1+x^2]]/2-ArcTan[Sqrt[3]+2*Sqrt[1+x^2]]/2-1/4*Sqrt[3]*Log[2+x^2-Sqrt[3]*Sqrt[1+x^2]]+1/4*Sqrt[3]*Log[2+x^2+Sqrt[3]*Sqrt[1+x^2]]" //
    );
  }

  public void test48() {
    check( //
        "Integrate[ArcTan[Sqrt[x+1]-Sqrt[x]], x]", //
        "Sqrt[x]/2+(1+x)*ArcTan[Sqrt[1+x]-Sqrt[x]]" //
    );
  }

  public void test49() {
    check( //
        "Integrate[ArcSin[x/Sqrt[1-x^2]], x]", //
        "x*ArcSin[x/Sqrt[1-x^2]]+ArcTan[Sqrt[1-2*x^2]]" //
    );
  }

  public void test50() {
    check( //
        "Integrate[ArcTan[x*Sqrt[1-x^2]], x]", //
        "x*ArcTan[x*Sqrt[1-x^2]]-Sqrt[1/2*(1+Sqrt[5])]*ArcTan[Sqrt[1/2*(1+Sqrt[5])]*Sqrt[1-x^2]]+Sqrt[1/2*(-1+Sqrt[5])]*ArcTanh[Sqrt[1/2*(-1+Sqrt[5])]*Sqrt[1-x^2]]" //
    );
  }
}
