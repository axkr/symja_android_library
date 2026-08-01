package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class RootTest extends ExprEvaluatorTestCase {

  @Test
  public void testRoot() {
    // Numeric-mode auto-evaluation: c is an inexact number, so Root[{f, c}] refines
    // c via FindRoot and returns a numeric value (matches WMA's behavior on
    // 4.60421677720057651458449514482636628606`20.6008566975056).
    check("Root({2*#1-Tan(#1) & , 4.60421677720057651458449514482636628606`20.6008566975056})", //
        "Root({2*#1-Tan(#1)&,4.6042167772005765145})");

    // Inexact c with no closed-form Solve result → numeric refinement via FindRoot.
    check("Root({Exp(#) - 2 &, 0.6931471805599453})", //
        "Root({-2+E^#1&,0.693147})");

    check("Root({#^2 - 2 &, 1.4142135623730951})", //
        "Sqrt(2)");
    check("Root({Sin(#) &, 3.1415926535897932385})", //
        "Pi");
    check("Root(EvenQ(#1)&,1009)", //
        "Root(EvenQ(#1)&,1009)");
    check("Root((#^2 - 3*# - 1)&, 1)", //
        "3/2-Sqrt(13)/2");
    check("Root((#^2 - 3*# - 1)&, 2)", //
        "3/2+Sqrt(13)/2");
    check("Root((-3*#-1)&, 1)", //
        "-1/3");

    // 3-argument form Root[f, k, 0] (only the real-first ordering is implemented).
    check("Root((#^2 - 3*# - 1)&, 1, 0)", //
        "3/2-Sqrt(13)/2");
    check("Root((#^2 - 3*# - 1)&, 2, 0)", //
        "3/2+Sqrt(13)/2");
    // ToRadicals on the 3-argument form for a cubic
    check("ToRadicals(Root(#^3 - 2 &, 1, 0))", //
        "2^(1/3)");

    // 3-argument form with n=1 (alternate ordering): Symja currently only
    // implements the real-first ordering, so Root[f, k, 1] is treated equivalently to
    // Root[f, k, 0] (both auto-evaluate for quadratics, and ToRadicals expands them).
    check("Root((#^2 - 2)&, 2, 1)", //
        "Sqrt(2)");
    check("ToRadicals(Root(#^3 - 2 &, 1, 1))", //
        "2^(1/3)");

    // Invalid third argument (n not in {0,1}): normalizes n to 0, so
    // Root[#^3 - 2 &, 1, 2] is rewritten as Root[#^3 - 2 &, 1, 0] and stays unevaluated
    // (cubic is not auto-expanded — use ToRadicals to get the radical form).
    check("Root(#^3 - 2 &, 1, 2)", //
        "Root(-2+#1^3&,1,0)");

  }

  @Test
  public void testRoots() {
    check("(-EulerGamma)^(1/3)", //
        "(-EulerGamma)^(1/3)");
    check("Roots(x^3==EulerGamma,x)", //
        "x==-(-EulerGamma)^(1/3)||x==EulerGamma^(1/3)||x==(-1)^(2/3)*EulerGamma^(1/3)");
    check("Roots(a*x^2+b*x+c==0,2)", //
        "Roots(c+b*x+a*x^2==0,2)");

    // check("Roots(a*x^3+b*x^2+c^2+d, x)",
    // "{(-b/2-Sqrt(b^2-4*a*c)/2)/a,(-b/2+Sqrt(b^2-4*a*c)/2)/a}");
    check("Roots(x^2-2*x-3==0,x)", //
        "x==-1||x==3");
    check("Roots(a*x^2+b*x+c==0, x)", //
        "x==-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)||x==-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)");
    check("Roots(3*x^3-8*x^2+-11*x+10==0,x)", //
        "x==2/3||x==1-Sqrt(6)||x==1+Sqrt(6)");
    check("Roots(3*x^3-5*x^2+5*x-2==0,x)", //
        "x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)");
    check("Roots(x^3 - 5*x + 4==0,x)", //
        "x==1||x==-1/2-Sqrt(17)/2||x==-1/2+Sqrt(17)/2");
  }


  @Test
  public void testRootReduce() {
    // TODO eliminate gcd
    check("RootReduce((-35-7*Sqrt(35))^-1)", //
        "1/490*(35-7*Sqrt(35))");
    check("RootReduce((35-7*Sqrt(35))^-1)", //
        "1/490*(-35-7*Sqrt(35))");
    check("RootReduce((35+7*Sqrt(35))^-1)", //
        "1/490*(-35+7*Sqrt(35))");
    check("RootReduce((-35+7*Sqrt(35))^-1)", //
        "1/490*(35+7*Sqrt(35))");

    check("RootReduce((-35-Sqrt(35))^-1)", //
        "1/1190*(-35+Sqrt(35))");
    check("RootReduce((35-Sqrt(35))^-1)", //
        "1/1190*(35+Sqrt(35))");
    check("RootReduce((35+Sqrt(35))^-1)", //
        "1/1190*(35-Sqrt(35))");
    check("RootReduce((-35+Sqrt(35))^-1)", //
        "1/1190*(-35-Sqrt(35))");

    check("MinimalPolynomial(Sqrt(2))", //
        "-2+#1^2&");
    // General path via MinimalPolynomial: simple sqrt expressions
    // Sqrt(2) has minimal polynomial x^2 - 2; it is the 2nd (larger) root
    check("RootReduce(Sqrt(2))", //
        "Sqrt(2)");
    // -Sqrt(2) is the 1st root
    check("RootReduce(-Sqrt(2))", //
        "-Sqrt(2)");

    // Sqrt(3): minimal poly x^2 - 3
    check("RootReduce(Sqrt(3))", //
        "Sqrt(3)");

    // Cube root: 2^(1/3) has minimal poly x^3 - 2. Real roots are indexed first, so the real cube
    // root 2^(1/3) is k=1. RootReduce now emits the 3-argument
    // form Root[f, k, 0] to match InputForm.
    check("RootReduce(2^(1/3)) // InputForm", //
        "Root(-2 + #1^3&,1,0)");
    // Rational: degree-1 minimal poly, returned as rational directly
    check("RootReduce(3/7)", //
        "3/7");
    check("RootReduce(5)", //
        "5");

    // Sum of surds: 1 + Sqrt(2)
    // Min poly of (1+Sqrt(2)) is x^2 - 2x - 1; Root[..., 2] auto-evaluates (quadratic)
    check("RootReduce(1+Sqrt(2))", //
        "1+Sqrt(2)");

    // 1 + Sqrt(2) + Sqrt(3) — degree 4 min poly (RootReduce emits 3-arg Root[f, k, 0])
    check("RootReduce(1+Sqrt(2)+Sqrt(3))", //
        "Root(-8+16*#1-4*#1^2-4*#1^3+#1^4&,4,0)");

    // Negative fractional powers (existing code rationalized; quadratic Root auto-evaluates)
    // 1/Sqrt(2): min poly 2x^2 - 1 (normalized: -1+2#1^2)
    check("RootReduce(1/Sqrt(2))", //
        "1/Sqrt(2)");

    // Already a Root expression — quadratic auto-evaluates to its radical form
    check("RootReduce(Root(-2+#1^2&,2))", //
        "Sqrt(2)");
  }


  @Test
  public void testToRadicals() {
    // Symbolic coefficient substituted with zero.
    // This evaluates root3 symbolically, creating an expression with division by 'a'.
    // Then we substitute 'a -> 0'. Symja should yield Indeterminate
    check("ToRadicals(Root(a * #^3 + #^2 - 2 &, 1)) /. a -> 0", // "
        "Indeterminate");

    // Explicit 0 coefficient in the cubic term.
    // Symja's polynomial parser should simplify this to a quadratic polynomial
    // BEFORE calling the root solvers, meaning it naturally routes to root2.
    check("ToRadicals(Root(0 * #^3 + #^2 - 2 &, 1))", //
        "-Sqrt(2)");

    // ToRadicals(Root(#^2 - 2 &, 1))
    check("ToRadicals(Root(#^2 - 2 &, 1))", //
        "-Sqrt(2)");

    // ToRadicals(Root(#^2 - 2 &, 2))
    check("ToRadicals(Root(#^2 - 2 &, 2))", //
        "Sqrt(2)");
    check("ToRadicals(Root(#^3 - 2 &, 1))", //
        "2^(1/3)");

    // Ensure that it handles an already evaluated cubic root ]
    check("ToRadicals(Root(#^3 - 8 &, 1))", //
        "2");

    check("ToRadicals(Root((#^2 - 3*# - 1)&, 2))", //
        "3/2+Sqrt(13)/2");
    check("ToRadicals(Root((-3*#-1)&, 1))", //
        "-1/3");
    check("ToRadicals(Sin(Root((#^7-#^2-#+a)&, 1)))", //
        "Sin(Root(-#1-#1^2+#1^7+a&,1))");
    check("ToRadicals(Root((#^7-#^2-#+a)&, 1)+Root((#^6-#^2-#+a)&, 1))", //
        "Root(-#1-#1^2+#1^6+a&,1)+Root(-#1-#1^2+#1^7+a&,1)");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 1))", //
        "1/3+4/3*2^(1/3)/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+(11+Sqrt(-256+(11-27*a)^2)-\n"
            + "27*a)^(1/3)/(3*2^(1/3))");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 2))", //
        "1/3+4/3*(2^(1/3)*(-1/2-I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((-\n"
            + "1/2+I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
    check("ToRadicals(Root((#^3-#^2-#+a)&, 3))", //
        "1/3+4/3*(2^(1/3)*(-1/2+I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((-\n"
            + "1/2-I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
  }

  /**
   * Quintic (and higher-degree) binomials are solvable in radicals via the {@code Solve} fallback.
   * The k-indexing follows convention (real roots first, then complex roots by ascending real part
   * and ascending imaginary part).
   */
  @Test
  public void testToRadicalsQuintic() {
    // Root[#^5 - 2 &, k] for k = 1..5. The real root is k=1; k=3 matches WMA's
    // (-1)^(4/5)*2^(1/5).
    check("ToRadicals(Root(#^5 - 2 &, 1))", //
        "2^(1/5)");
    check("ToRadicals(Root(#^5 - 2 &, 2))", //
        "-(-2)^(1/5)");
    check("ToRadicals(Root(#^5 - 2 &, 3))", //
        "(-1)^(4/5)*2^(1/5)");
    check("ToRadicals(Root(#^5 - 2 &, 4))", //
        "-(-1)^(3/5)*2^(1/5)");
    check("ToRadicals(Root(#^5 - 2 &, 5))", //
        "(-1)^(2/5)*2^(1/5)");

    // Non-solvable quintic: Solve cannot return explicit radicals, so the Root stays unevaluated.
    check("ToRadicals(Root(#^5 - # - 1 &, 1))", //
        "Root(-1-#1+#1^5&,1)");
  }

  @Test
  public void testToRadicalsRelational() {
    check("ToRadicals[Root[Function[#^2 - 2], 2] < 3] ", // "
        "True");
    check("ToRadicals[Root[Function[#^2 - 2], 2] > 3] ", // "
        "False");
  }

  /**
   * {@code N[Root[f, k]]} determines the k-th root of the polynomial numerically. This is the only
   * option for polynomials which are not solvable in radicals at all.
   */
  @Test
  public void testRootNumeric() {
    // The root index k and the ordering flag n are positions in the list of roots, not numbers to
    // approximate. Without NHoldRest N[] would rewrite Root[f, 1, 0] to Root[f, 1.0, 0.0].
    check("Attributes(Root)", //
        "{NHoldRest,Protected}");
    check("N(Root(1+2*#1+#1^5&,1,0)) // InputForm", //
        "-0.4863890359345431`");

    // The quintic 1 + 2*x + x^5 is not solvable in radicals, so ToRadicals leaves it alone ...
    check("ToRadicals(Root(1+2*#1+#1^5&,1,0))", //
        "Root(1+2*#1+#1^5&,1,0)");
    // ... but N() can still evaluate it.
    check("{{x->Root(1+2*#1+#1^5&,1,0)},{x->Root(1+2*#1+#1^5&,2,0)}} // N", //
        "{{x->-0.486389},{x->-0.701874+I*(-0.879697)}}");

    // k-indexing: the real root comes first, then the complex roots by ascending real part and
    // ascending imaginary part.
    check("Table(N(Root(1+2*#1+#1^5&,k,0)), {k,1,5})", //
        "{-0.486389,-0.701874+I*(-0.879697),-0.701874+I*0.879697,0.945068+I*(-0.854518),0.945068+I*0.854518}");

    // Degree 3 and 4 are not auto-expanded to radicals either, but N() resolves them and agrees
    // with the radical form.
    check("N(Root(#1^3-2&,1))", //
        "1.25992");
    check("N(2^(1/3))", //
        "1.25992");
    check("N(Root(#1^5-2&,3))", //
        "-0.929316+I*0.675188");
    check("N(ToRadicals(Root(#1^5-2&,3)))", //
        "-0.929316+I*0.675188");

    // A repeated root is returned once per multiplicity, matching the degree-many k-indexing:
    // #1^3-#1^2-#1+1 == (#1-1)^2*(#1+1)
    check("Table(N(Root(#1^3-#1^2-#1+1&,k)), {k,1,3})", //
        "{-1.0,1.0,1.0}");
  }

  /**
   * {@code N[Root[f, k], precision]} refines the machine-precision root with Newton's method in
   * arbitrary precision arithmetic.
   */
  @Test
  public void testRootNumericPrecision() {
    check("N(Root(1+2*#1+#1^5&,1,0), 30)", //
        "-0.486389035934543000016557253698");
    check("N(Root(1+2*#1+#1^5&,2,0), 30)", //
        "-0.701873568855861886306687517912+I*(-0.879697197929824022870267273817)");
    check("N({{x->Root(1+2*#1+#1^5&,1,0)},{x->Root(1+2*#1+#1^5&,2,0)}}, 25)", //
        "{{x->-0.4863890359345430000165572},{x->-0.7018735688558618863066875+I*(-0.8796971979298240228702672)}}");

    // cross check against the radical form of the same root
    check("N(Root(#1^3-2&,1), 30)", //
        "1.25992104989487316476721060727");
    check("N(2^(1/3), 30)", //
        "1.25992104989487316476721060727");

    // irrational coefficients are evaluated to the working precision as well
    check("N(Root(#1^3-Sqrt(2)&,1), 30)", //
        "1.12246204830937298143353304967");
    check("N(2^(1/6), 30)", //
        "1.12246204830937298143353304967");

    // Laguerre's method alone is only accurate to about 13 digits here; the Newton refinement
    // corrects the machine-precision approximation -0.8525507144946649 from the 14th digit on.
    check("N(Root(#1^12-#1-1&,1), 40)", //
        "-0.8525507144946671096505146748317735145462");
  }

  /**
   * {@code Root[f, k]} stays unevaluated when the k-th root cannot be determined numerically.
   */
  @Test
  public void testRootNumericUnevaluated() {
    // symbolic coefficient
    check("N(Root(#1^7-#1^2-#1+a&,1))", //
        "Root(-#1-#1^2+#1^7+a&,1)");
    // f is not a polynomial
    check("N(Root(Sin(#1)-#1/2&,1))", //
        "Root(Sin(#1)-#1/2&,1)");
    check("N(Root(EvenQ(#1)&,1009))", //
        "Root(EvenQ(#1)&,1009)");
    // k out of range: the quintic has 5 roots
    check("N(Root(#1^5-2&,7))", //
        "Root(-2+#1^5&,7)");
    check("N(Root(#1^5-2&,0))", //
        "Root(-2+#1^5&,0)");
  }

  /**
   * Tests for the symbolic-numerical {@code Root[{f, c}]} placeholder used for transcendental
   * equations whose roots have no closed-form algebraic representation (e.g. {@code 2 x - Tan[x]}).
   */
  @Test
  public void testRootNearFloat() {
    check("N(Root({2*#1-Tan(#1)&,4.6}))", //
        "4.60422");

    check("Root({2*#1-Tan(#1)&,1})", //
        "Root({2*#1-Tan(#1)&,1})");

    // Inexact c → auto-numeric refinement via FindRoot
    check("Root({2*#1-Tan(#1) &, 4.6042})", //
        "Root({2*#1-Tan(#1)&,4.6042})");

    // N[] also refines inert Root[{f, c}] forms.
    check("N(Root({2*#1-Tan(#1)&,4.6042}))", //
        "4.60422");
    check("N(Root({2*#1-Tan(#1)&,1.1656}))", //
        "1.16556");

    // Invalid c (non-numeric): emit Root::rapp message and stay inert.
    check("Root({Sin(#1)&,x})", //
        "Root({Sin(#1)&,x})");

    // style precision check: an inexact c that is *not* close enough to a
    // true root produces a "Root::invrt" message and the expression stays unevaluated.
    // For 2 x - Tan[x] the true root is ≈ 4.6042167..., so:
    // |4.60421 - 4.6042167| ≈ 6.7e-6 → just outside the 1e-5 relative tolerance → rejected
    // |4.604216 - 4.6042167| ≈ 7e-7 → comfortably inside → accepted (auto-numeric)
    check("Root({2*#1-Tan(#1)&,4.60421})", //
        "Root({2*#1-Tan(#1)&,4.60421})");
    check("Root({2*#1-Tan(#1)&,4.604216})", //
        "Root({2*#1-Tan(#1)&,4.60422})");
  }

}
