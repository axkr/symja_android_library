package org.matheclipse.core.rubi.independent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Rubi's "0 Independent test suites" corpus section. 14 integrals.
 *
 * <p>
 * <b>Not part of a normal build.</b> {@code AbstractRubiTestCase} is {@code @Tag("corpus")},
 * which the default surefire run excludes. That is deliberate - the section is slow - but it
 * means drift goes unnoticed, so run it after any change to the integrator:
 *
 * <pre>
 * mvn -o -pl matheclipse-io test -Prubi-corpus -Dsurefire.timeout=5400
 * </pre>
 *
 * <p>
 * The profile sets {@code reuseForks=false}, which is required rather than an optimisation:
 * {@code AbstractRubiTestCase} sets the global {@code ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS},
 * so sharing one JVM across classes makes every test fail. It also sets
 * {@code testFailureIgnore}, because this corpus is a scoreboard rather than a gate.
 *
 * <p>
 * Tests marked {@code KNOWN GAP} are expected to fail: their expected value is Rubi's reference
 * and Symja has no answer for that integral yet.
 */
public class BronsteinProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public BronsteinProblemsTests() {
    super(false);
  }

  @BeforeEach
  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Bronstein");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test1() {
    check( //
        "Integrate[(2*x^8+1)*Sqrt[x^8+1]/(x^17+2*x^9+x), x]", //
        "-1/(4*Sqrt[1+x^8])-ArcTanh[Sqrt[1+x^8]]/4" //
    );
  }

  @Test
  public void test2() {
    check( //
        "Integrate[1/(1+x^2), x]", //
        "ArcTan[x]" //
    );
  }

  @Test
  public void test3() {
    check( //
        "Integrate[Sqrt[x^8+1]/(x*(x^8+1)), x]", //
        "-ArcTanh[Sqrt[1+x^8]]/4" //
    );
  }

  @Test
  public void test4() {
    check( //
        "Integrate[x/Sqrt[1-x^3], x]", //
        "(2*Sqrt[1-x^3])/(1+Sqrt[3]-x)-(3^(1/4)*Sqrt[2-Sqrt[3]]*(1-x)*Sqrt[(1+x+x^2)/(1+Sqrt[3]-x)^2]*EllipticE[ArcSin[(1-Sqrt[3]-x)/(1+Sqrt[3]-x)],-7-4*Sqrt[3]])/(Sqrt[(1-x)/(1+Sqrt[3]-x)^2]*Sqrt[1-x^3])+(2*Sqrt[2]*(1-x)*Sqrt[(1+x+x^2)/(1+Sqrt[3]-x)^2]*EllipticF[ArcSin[(1-Sqrt[3]-x)/(1+Sqrt[3]-x)],-7-4*Sqrt[3]])/(3^(1/4)*Sqrt[(1-x)/(1+Sqrt[3]-x)^2]*Sqrt[1-x^3])" //
    );
  }

  @Test
  public void test5() {
    check( //
        "Integrate[1/(x*Sqrt[1-x^3]), x]", //
        "-2/3*ArcTanh[Sqrt[1-x^3]]" //
    );
  }

  @Test
  public void test6() {
    check( //
        "Integrate[x/Sqrt[x^4+10*x^2-96*x+(-1)*71], x]", //
        "Log[10001+3124*x^2-1408*x^3+54*x^4-128*x^5+20*x^6+x^8+Sqrt[-71-96*x+10*x^2+x^4]*(781-528*x+27*x^2-80*x^3+15*x^4+x^6)]/8" //
    );
  }

  @Test
  public void test7() {
    check( //
        "Integrate[(x-Tan[x])/Tan[x]^2, x]", //
        "-x^2/2-x*Cot[x]" //
    );
  }

  @Test
  public void test8() {
    check( //
        "Integrate[1+x*Tan[x]+Tan[x]^2, x]", //
        "1/2*I*x^2-x*Log[1+E^(2*I*x)]+1/2*I*PolyLog[2,-E^(2*I*x)]+Tan[x]" //
    );
  }

  @Test
  public void test9() {
    check( //
        "Integrate[Sin[x]/x, x]", //
        "SinIntegral[x]" //
    );
  }

  @Test
  public void test10() {
    check( //
        "Integrate[(3*(x+E^x)^(1/3)+(2*x^2+3*x)*E^x+5*x^2)/(x*(x+E^x)^(1/3)), x]", //
        "3*x*(E^x+x)^(2/3)+3*Log[x]" //
    );
  }

  @Test
  public void test11() {
    check( //
        "Integrate[1/x+(1+1/x)/(x+Log[x])^(3/2), x]", //
        "Log[x]-2/Sqrt[x+Log[x]]" //
    );
  }

  @Test
  public void test12() {
    check( //
        "Integrate[(Log[x]^2+2*x*Log[x]+x^2+(x+1)*Sqrt[x+Log[x]])/(x*Log[x]^2+2*x^2*Log[x]+x^3), x]", //
        "Log[x]-2/Sqrt[x+Log[x]]" //
    );
  }

  @Test
  public void test13() {
    check( //
        "Integrate[(2*Log[x]^2-Log[x]-x^2)/(Log[x]^3-x^2*Log[x]), x]", //
        "-Log[x-Log[x]]/2+Log[x+Log[x]]/2+LogIntegral[x]" //
    );
  }

  @Test
  public void test14() {
    check( //
        "Integrate[(x^4-3*x^2+6)/(x^6-5*x^4+5*x^2+4), x]", //
        "-ArcTan[Sqrt[3]-2*x]+ArcTan[Sqrt[3]+2*x]+ArcTan[1/2*x*(1-3*x^2+x^4)]" //
    );
  }
}
