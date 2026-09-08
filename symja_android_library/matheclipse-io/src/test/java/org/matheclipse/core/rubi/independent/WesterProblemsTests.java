package org.matheclipse.core.rubi.independent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Rubi's "0 Independent test suites" corpus section. 8 integrals.
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
public class WesterProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public WesterProblemsTests() {
    super(false);
  }

  @BeforeEach
  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 20;
      if (init) {
        System.out.println("Wester");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test1() {
    check( //
        "Integrate[(-5+3*x)^2/(-1+2*x)^(7/2), x]", //
        "-49/(20*(-1+2*x)^(5/2))+7/(2*(-1+2*x)^(3/2))-9/(4*Sqrt[-1+2*x])" //
    );
  }

  @Test
  public void test2() {
    check( //
        "Integrate[1/(-5/E^(m*x)+2*E^(m*x)), x]", //
        "-ArcTanh[Sqrt[2/5]*E^(m*x)]/(Sqrt[10]*m)" //
    );
  }

  @Test
  public void test3() {
    check( //
        "Integrate[1/(a+b*Cos[x]), x]", //
        "(2*ArcTan[(Sqrt[a-b]*Tan[x/2])/Sqrt[a+b]])/(Sqrt[a-b]*Sqrt[a+b])" //
    );
  }

  @Test
  public void test4() {
    check( //
        "Integrate[1/(3+3*Cos[x]+4*Sin[x]), x]", //
        "Log[3+4*Tan[x/2]]/4" //
    );
  }

  @Test
  public void test5() {
    check( //
        "Integrate[1/(4+3*Cos[x]+4*Sin[x]), x]", //
        "-Log[4+3*Cot[Pi/4+x/2]]/3" //
    );
  }

  @Test
  public void test6() {
    check( //
        "Integrate[1/(5+3*Cos[x]+4*Sin[x]), x]", //
        "-1/(2+Tan[x/2])" //
    );
  }

  @Test
  public void test7() {
    check( //
        "Integrate[1/(6+3*Cos[x]+4*Sin[x]), x]", //
        "x/Sqrt[11]+(2*ArcTan[(4*Cos[x]-3*Sin[x])/(6+Sqrt[11]+3*Cos[x]+4*Sin[x])])/Sqrt[11]" //
    );
  }

  @Test
  public void test8() {
    check( //
        "Integrate[Log[(-a^2+x^2)^2]/2, x]", //
        "-2*x+2*a*ArcTanh[x/a]+1/2*x*Log[(-a^2+x^2)^2]" //
    );
  }
}
