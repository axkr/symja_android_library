package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

/**
 * Rubi's "0 Independent test suites" corpus section. 7 integrals.
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
public class HebischProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public HebischProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Hebisch");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[(x^6-x^5+x^4-x^3+1)*E^x, x]", //
        "871*E^x-870*E^x*x+435*E^x*x^2-145*E^x*x^3+36*E^x*x^4-7*E^x*x^5+E^x*x^6" //
    );
  }

  public void test2() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja solves only part of it and leaves two unevaluated Integrate terms.
    check( //
        "Integrate[(2-x^2)*E^(x/(x^2+2))/(x^3+2*x), x]", //
        "ExpIntegralEi[x/(2+x^2)]" //
    );
  }

  public void test3() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja solves only part of it and leaves several unevaluated Integrate
    // terms.
    check( //
        "Integrate[(2+2*x+3*x^2-x^3+2*x^4)*E^(x/(2+x^2))/(x^3+2*x), x]", //
        "E^(x/(2+x^2))*(2+x^2)+ExpIntegralEi[x/(2+x^2)]" //
    );
  }

  public void test4() {
    check( //
        "Integrate[(E^x+1)*E^(E^x+x)/(E^x+x), x]", //
        "ExpIntegralEi[E^x+x]" //
    );
  }

  public void test5() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja solves only part of it and leaves several unevaluated Integrate
    // terms.
    check( //
        "Integrate[(x^3-x^2-3*x+1)*E^(1/(x^2+(-1)*1))/(x^3-x^2-x+1), x]", //
        "E^(1/(-1+x^2))*(1+x)" //
    );
  }

  public void test6() {
    check( //
        "Integrate[(Log[x]^2+(-1)*1)*E^(1+1/Log[x])/Log[x]^2, x]", //
        "x*E^(1+1/Log[x])" //
    );
  }

  public void test7() {
    // KNOWN GAP - this test does not pass. The expected value is Rubi's reference, not a form Symja
    // has ever produced. Symja leaves this as a sum of unevaluated Integrate terms.
    check( //
        "Integrate[((x+1)*Log[x]^2+(-1)*1)*E^(x+1/Log[x])/Log[x]^2, x]", //
        "E^(x+1/Log[x])*x" //
    );
  }
}
