package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the <code>CForm</code> and <code>FortranForm</code> display wrappers. */
public class CodeFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testCFormOperators() {
    check("ToString(CForm(a^4 + h(a, b) + 2*I + c/40))", //
        "Complex(0,2) + Power(a,4) + c/40. + h(a,b)");
    check("ToString(CForm(a != b))", //
        "a != b");
    check("ToString(CForm(-5*a/9))", //
        "(-5*a)/9.");
    check("ToString(CForm(b/a^5 + 7*b/a^2))", //
        "b/Power(a,5) + (7*b)/Power(a,2)");
    check("ToString(CForm(Sqrt(b) + Exp(a) + Cos(b)^3))", //
        "Sqrt(b) + Power(E,a) + Power(Cos(b),3)");
    check("ToString(CForm(a^(2/3) + 1/a + 1/Sqrt(b)))", //
        "1/a + Power(a,0.6666666666666666) + 1/Sqrt(b)");
    check("ToString(CForm(Mod(a, b) + 3/2))", //
        "1.5 + a % b");
    check("ToString(CForm((a || b) && !c))", //
        "(a || b) && !c");
    check("ToString(CForm(a - b))", //
        "a - b");
    // ! binds tighter than the relations and the arithmetic of C
    check("ToString(CForm(!(a + b)))", //
        "!(a + b)");
    check("ToString(CForm(!f(a)[[2]] || c))", //
        "!f(a)[2] || c");
    check("ToString(CForm(!(a*b) && c))", //
        "!(a*b) && c");
    // a*b % c would group as (a*b) % c
    check("ToString(CForm(a*Mod(b, c)))", //
        "a*(b % c)");
    check("ToString(CForm(f(\"q\\\"r\")))", //
        "f(\"q\\\"r\")");
  }

  @Test
  public void testCFormReals() {
    // the shortest text which reads back as the machine number
    check("ToString(CForm(3.*^8))", //
        "3.e8");
    check("ToString(CForm(4.5*^-9))", //
        "4.5e-9");
    check("ToString(CForm(250.))", //
        "250.");
    check("ToString(CForm(0.0625))", //
        "0.0625");
    check("ToString(CForm(-1.75))", //
        "-1.75");
  }

  @Test
  public void testFortranFormOperators() {
    check("ToString(FortranForm(a^4 + h(a, b) + 2*I + c/40))", //
        "(0,2) + a**4 + c/40. + h(a,b)");
    check("ToString(FortranForm((a < b) || (a >= c) && !d))", //
        "a.lt.b.or.(a.ge.c.and..not.d)");
    check("ToString(FortranForm(b/a^5 + 7*b/a^2))", //
        "b/a**5 + (7*b)/a**2");
    check("ToString(FortranForm((1 + a)^2 + a^(b + c)))", //
        "a**(b + c) + (1 + a)**2");
    check("ToString(FortranForm((3 - 2*I)*b + Mod(a, c)))", //
        "(3,-2)*b + Mod(a,c)");
    check("ToString(FortranForm(f(4) + 5/8*a))", //
        "(5*a)/8. + f(4)");
  }

  @Test
  public void testFortranFormPageWidth() {
    // continuation lines follow an empty line and start with >
    check("ToString(FortranForm(Sum(a^k*k, {k, 1, 9})), PageWidth -> 30)", //
        "a + 2*a**2 + 3*a**3 + \n" //
            + " \n" //
            + ">   4*a**4 + 5*a**5 + \n" //
            + " \n" //
            + ">   6*a**6 + 7*a**7 + \n" //
            + " \n" //
            + ">   8*a**8 + 9*a**9");
  }

  @Test
  public void testCodeFormIsADisplayWrapper() {
    check("FortranForm(a^2 + b) // FullForm", //
        "FortranForm(Plus(Power(a, 2), b))");
    check("Head(CForm(a^2))", //
        "CForm");
    check("CForm(a^2 + b)", //
        "Power(a,2) + b");
    check("ToString(a^2 - b, FortranForm)", //
        "a**2 - b");
  }
}
