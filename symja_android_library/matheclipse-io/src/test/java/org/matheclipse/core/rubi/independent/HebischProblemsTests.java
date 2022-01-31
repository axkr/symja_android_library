package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

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
    check( //
        "Integrate[(2-x^2)*E^(x/(x^2+2))/(x^3+2*x), x]", //
        "ExpIntegralEi[x/(2+x^2)]" //
    );
  }

  public void test3() {
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
    check( //
        "Integrate[((x+1)*Log[x]^2+(-1)*1)*E^(x+1/Log[x])/Log[x]^2, x]", //
        "E^(x+1/Log[x])*x" //
    );
  }
}
