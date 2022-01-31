package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class JeffreyProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public JeffreyProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Jeffrey");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[3/(5-4*Cos[x]), x]", //
        "x+2*ArcTan[Sin[x]/(2-Cos[x])]" //
    );
  }

  public void test2() {
    check( //
        "Integrate[(Cos[x]+2*Sin[x]+1)/(Cos[x]^2-2*Sin[x]*Cos[x]+2*Sin[x]+3), x]", //
        "-ArcTan[(2*Cos[x]-Sin[x])/(2+Sin[x])]" //
    );
  }

  public void test3() {
    check( //
        "Integrate[(2+5*Sin[x]+Cos[x])/(4*Cos[x]+Sin[x]*Cos[x]-2*Sin[x]-2*Sin[x]^2), x]", //
        "-Log[1-3*Cos[x]+Sin[x]]+Log[3+Cos[x]+Sin[x]]" //
    );
  }

  public void test4() {
    check( //
        "Integrate[(7*Cos[x]+2*Sin[x]+3)/(3*Cos[x]^2-Sin[x]*Cos[x]+4*Cos[x]-5*Sin[x]+1), x]", //
        "-Log[1+Cos[x]-2*Sin[x]]+Log[3+Cos[x]+Sin[x]]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[(5*Cos[x]^2+4*Cos[x]+(-1)*1)/(4*Cos[x]^3-3*Cos[x]^2-4*Cos[x]+(-1)*1), x]", //
        "x-2*ArcTan[Sin[x]/(3+Cos[x])]-2*ArcTan[(3*Sin[x]+7*Cos[x]*Sin[x])/(1+2*Cos[x]+5*Cos[x]^2)]" //
    );
  }

  public void test6() {
    check( //
        "Integrate[(7*Cos[x]^2+2*Cos[x]+(-1)*5)/(4*Cos[x]^3-9*Cos[x]^2+2*Cos[x]+(-1)*1), x]", //
        "x-2*ArcTan[(2*Cos[x]*Sin[x])/(1-Cos[x]+2*Cos[x]^2)]" //
    );
  }

  public void test7() {
    check( //
        "Integrate[3/(5+4*Sin[x]), x]", //
        "x+2*ArcTan[Cos[x]/(2+Sin[x])]" //
    );
  }

  public void test8() {
    check( //
        "Integrate[2/(1+Cos[x]^2), x]", //
        "Sqrt[2]*x-Sqrt[2]*ArcTan[(Cos[x]*Sin[x])/(1+Sqrt[2]+Cos[x]^2)]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[1/(p+q*Cos[x]+r*Sin[x]), x]", //
        "(2*ArcTan[(r+(p-q)*Tan[x/2])/Sqrt[p^2-q^2-r^2]])/Sqrt[p^2-q^2-r^2]" //
    );
  }
}
