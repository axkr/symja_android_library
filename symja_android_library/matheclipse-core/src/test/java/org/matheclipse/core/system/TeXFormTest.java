package org.matheclipse.core.system;

import java.io.StringWriter;
import org.junit.jupiter.api.Assertions;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class TeXFormTest extends ExprEvaluatorTestCase {
  public TeXFormTest(String name) {
    super(name);
  }

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  public void testBeta() {
    check("TeXForm(Beta(a,b))", //
        "B(a,b)");
    check("TeXForm(beta)", //
        "\\beta");
  }

  public void testBesselI() {
    check("TeXForm(BesselI(a,b))", //
        "I_a(b)");
  }

  public void testBesselJ() {
    check("TeXForm(BesselJ(a,b))", //
        "J_a(b)");
  }

  public void testBesselK() {
    check("TeXForm(BesselK(a,b))", //
        "K_a(b)");
  }

  public void testBesselY() {
    check("TeXForm(BesselY(a,b))", //
        "Y_a(b)");
  }

  public void testBetaRegularized() {
    check("TeXForm(BetaRegularized(a,b,c))", //
        "I_a(b,c)");
  }


  public void testCarlsonRC() {
    check("TeXForm(CarlsonRC(a,b))", //
        "R_C(a,b)");
  }

  public void testCarlsonRD() {
    check("TeXForm(CarlsonRD(a,b,c))", //
        "R_D(a,b,c)");
  }

  public void testCarlsonRG() {
    check("TeXForm(CarlsonRG(a,b,c))", //
        "R_G(a,b,c)");
  }

  public void testCarlsonRJ() {
    check("TeXForm(CarlsonRJ(a,b,c,d))", //
        "R_J(a,b,c,d)");
  }

  public void testChebyshevT() {
    check("TeXForm(ChebyshevT(a,b))", //
        "T_a(b)");
  }

  public void testChebyshevU() {
    check("TeXForm(ChebyshevU(a,b))", //
        "U_a(b)");
  }

  public void testCosIntegral() {
    check("TeXForm(CosIntegral(a))", //
        "\\text{Ci}(a)");
  }

  public void testCoshIntegral() {
    check("TeXForm(CoshIntegral(a))", //
        "\\text{Chi}(a)");
  }

  public void testEllipticE() {
    check("TeXForm(EllipticE(a))", //
        "E(a)");
    check("TeXForm(EllipticE(a,b))", //
        "E(a,b)");
  }

  public void testEllipticF() {
    check("TeXForm(EllipticF(a,b))", //
        "F(a|b)");
  }

  public void testEllipticK() {
    check("TeXForm(EllipticK(a))", //
        "K(a)");
  }

  public void testEllipticPi() {
    check("TeXForm(EllipticPi(a,b))", //
        "\\Pi (a|b)");
    check("TeXForm(EllipticPi(a,b,c))", //
        "\\Pi (a;b|c)");
  }

  public void testEllipticTheta() {
    check("TeXForm(EllipticTheta(a,b))", //
        "\\vartheta _a(b)");
    check("TeXForm(EllipticTheta(a,b,c))", //
        "\\vartheta _a(b,c)");
  }

  public void testErf() {
    check("TeXForm(Erf(a))", //
        "\\text{erf}(a)");
  }

  public void testErfc() {
    check("TeXForm(Erfc(a))", //
        "\\text{erfc}(a)");
  }

  public void testFactorialPower() {
    check("TeXForm(FactorialPower(a,b))", //
        "a^{(b)}");
    check("TeXForm(FactorialPower(a,b,c))", //
        "a^{(b,c)}");
  }

  public void testGammaRegularized() {
    check("TeXForm(GammaRegularized(a,b))", //
        "Q(a,b)");
    check("TeXForm(GammaRegularized(a,b,c))", //
        "Q(a,b,c)");
  }

  public void testGudermannian() {
    check("TeXForm(Gudermannian(a))", //
        "\\text{gd}(a)");
  }

  public void testHankelH1() {
    check("TeXForm(HankelH1(a,b))", //
        "H_a^{(1)}(b)");
  }

  public void testHankelH2() {
    check("TeXForm(HankelH2(a,b))", //
        "H_a^{(2)}(b)");
  }

  public void testHermiteH() {
    check("TeXForm(HermiteH(a,b))", //
        "H_a(b)");
  }

  public void testHypergeometric0F1() {
    check("TeXForm(f*g(Hypergeometric0F1(a,b)))", //
        "f \\cdot g(\\,_0F_1(;a;b))");
  }

  public void testHypergeometric1F1() {
    check("TeXForm(Hypergeometric1F1(a,b,c))", //
        "\\,_1F_1(a,b,c)");
  }

  public void testHypergeometricU() {
    check("TeXForm(HypergeometricU(a,b,c))", //
        "U(a,b,c)");
  }

  public void testIntervalData() {
    check("TeXForm(IntervalData())", //
        "\\emptyset ");

    check("TeXForm(IntervalData({1,LessEqual,LessEqual,2},{5/2,LessEqual,LessEqual,42}))", //
        "\\left[1, 2\\right] \\cup \\left[\\frac{5}{2}, 42\\right] ");
    check("TeXForm(IntervalData({-42,Less,Less,3/4}))", //
        "\\left(-42, \\frac{3}{4}\\right) ");
    check("TeXForm(IntervalData({-Infinity,LessEqual,LessEqual,Infinity}))", //
        "\\left(-\\infty, \\infty\\right) ");
  }

  public void testIntervalData001() {
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb,
        F.eval(F.IntervalIntersection(F.IntervalData(F.List(F.ZZ(0), F.Less, F.Less, F.ZZ(1))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1))))),
        0);
    Assertions.assertEquals("\\emptyset ", sb.toString());
  }

  public void testIntervalData002() {
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb,
        F.eval(
            F.IntervalComplement(F.IntervalData(F.List(F.ZZ(0), F.LessEqual, F.LessEqual, F.ZZ(2))),
                F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.LessEqual, F.ZZ(1))))),
        0);
    Assertions.assertEquals("\\left[0, 1\\right) \\cup \\left(1, 2\\right] ", sb.toString());
  }

  public void testIntervalData003() {
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb,
        F.eval(F.IntervalUnion(F.IntervalData(F.List(F.CNInfinity, F.Less, F.LessEqual, F.ZZ(-1))),
            F.IntervalData(F.List(F.ZZ(1), F.LessEqual, F.Less, F.ZZ(2))),
            F.IntervalData(F.List(F.ZZ(2), F.Less, F.Less, F.ZZ(4))),
            F.IntervalData(F.List(F.ZZ(6), F.LessEqual, F.LessEqual, F.ZZ(9))))),
        0);
    Assertions.assertEquals( //
        "\\left(-\\infty, -1\\right] \\cup \\left[1, 2\\right) \\cup \\left(2, 4\\right) \\cup \\left[6, 9\\right] ", //
        sb.toString());
  }

  public void testInverseBetaRegularized() {
    check("TeXForm(InverseBetaRegularized(a,b,c))", //
        "I_a^{-1}(b,c)");
  }

  public void testInverseErf() {
    check("TeXForm(InverseErf(a))", //
        "\\text{erf}^{-1}(a)");
  }

  public void testInverseErfc() {
    check("TeXForm(InverseErfc(a))", //
        "\\text{erfc}^{-1}(a)");
  }

  public void testInverseGammaRegularized() {
    check("TeXForm(InverseGammaRegularized(a,b))", //
        "Q^{-1}(a,b)");
  }

  public void testInverseGudermannian() {
    check("TeXForm(InverseGudermannian(a))", //
        "\\text{gd}^{-1}(a)");
  }

  public void testLegendreP() {
    check("TeXForm(LegendreP(a,b))", //
        "P_a(b)");
    check("TeXForm(LegendreP(a,b,c))", //
        "P_a^b(c)");
  }

  public void testLegendreQ() {
    check("TeXForm(LegendreQ(a,b))", //
        "Q_a(b)");
    check("TeXForm(LegendreQ(a,b,c))", //
        "Q_a^b(c)");
  }

  public void testPochhammer() {
    check("TeXForm(f*Pochhammer(a,b)*z(a,b))", //
        "f \\cdot (a)_b \\cdot z(a,b)");
  }

  public void testPlusReversed001() {
    TeXFormFactory fTeXFactory = new TeXFormFactory(true, -1, -1, " \\cdot ");
    IExpr expr = evaluator.eval("1+x-x^2");
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb, expr);
    Assertions.assertEquals(" - {x}^{2} + x + 1", sb.toString());
  }

  public void testPlusReversed002() {
    // issue #753
    TeXFormFactory teXFormFactory = new TeXFormFactory(true, -1, -1, " \\cdot ");
    IExpr term = F.Plus(F.ZZ(-6), S.a);
    IASTAppendable ast = F.PlusAlloc(2);
    ast.append(term);
    ast.append(F.Style(F.ZZ(4), S.Red));
    StringBuilder buffer = new StringBuilder();
    teXFormFactory.convert(buffer, ast);
    assertEquals(buffer.toString(), "\\textcolor{red}{4} + a-6");
  }

  public void testPlusReversed003() {
    // issue #753
    TeXFormFactory teXFormFactory = new TeXFormFactory(true, -1, -1, " \\cdot ");
    IExpr[] args = new IExpr[] {F.Style(F.ZZ(4), S.Red), F.ZZ(-6), S.a};
    IExpr term = F.ast(args, S.Plus);
    StringBuilder buffer = new StringBuilder();
    teXFormFactory.convert(buffer, term);
    assertEquals(buffer.toString(), //
        "a-6 + \\textcolor{red}{4}");
  }

  public void testSinIntegral() {
    check("TeXForm(SinIntegral(a))", //
        "\\text{Si}(a)");
  }

  public void testSinhIntegral() {
    check("TeXForm(SinhIntegral(a))", //
        "\\text{Shi}(a)");
  }

  public void testWhittakerM() {
    check("TeXForm(WhittakerM(a,b,c))", //
        "M_{a,b}(c)");
  }

  public void testWhittakerW() {
    check("TeXForm(WhittakerW(a,b,c))", //
        "W_{a,b}(c)");
  }

  public void testTeXForm1() {
    check("TeXForm /@ {Subscript(x, a), x^a, Subsuperscript(x,a,b)}", //
        "{{x}_{a},{x}^{a},{x}_{a}^{b}}");
    check("TeXForm /@ {Subscript(x, 2*k+1), x^(2*k+1)}", //
        "{{x}_{1+2 \\cdot k},{x}^{\\left( 1+2 \\cdot k\\right) }}");
    check("TeXForm({a,b,c})", //
        "\\{a,b,c\\}");
    check("TeXForm({{a, b}, {c}})", //
        "\\{\\{a,b\\},\\{c\\}\\}");
    check("TeXForm({{a, b, c}, {d, e, f}})", //
        "\\left(\n" //
            + "\\begin{array}{ccc}\n" //
            + "a & b & c \\\\\n" //
            + "d & e & f \\\\\n" //
            + "\\end{array}\n" //
            + "\\right) ");
    check("TeXForm(Expand((x + y)^3))", //
        "{x}^{3}+3 \\cdot {x}^{2} \\cdot y+3 \\cdot x \\cdot {y}^{2} + {y}^{3}");
  }

  public void testTeXForm2() {
    check("TeXForm(Hold(2^3*3*5*11))", //
        "\\text{Hold}({2}^{3} \\cdot 3 \\cdot 5 \\cdot 11)");
    check("TeXForm(Hold(D(Sin(x),{x,3})))", //
        "\\text{Hold}(\\frac{\\partial ^3 \\sin (x)}{\\partial x^3})");
    check("TeXForm(Hold(D(Sin(x),x)))", //
        "\\text{Hold}(\\frac{\\partial \\sin (x)}{\\partial x})");
    check("TeXForm(Hold(Integrate(Sin(x),x)))", //
        "\\text{Hold}(\\int  \\sin (x)\\,\\mathrm{d}x)");
    check("TeXForm(-2.12 * x )", //
        "-2.12 \\cdot x");
    check("TeXForm( f(#,#3)&  )", //
        "f(\\text{$\\#$1},\\text{$\\#$3})\\&");
    check("TeXForm( f(#,#3)*2&  )", //
        "f(\\text{$\\#$1},\\text{$\\#$3}) \\cdot 2\\&");
    check("TeXForm(N(1.1+Pi*I,30))", //
        "1.1 + 3.14159\\,i ");
    check("TeXForm(N(Pi,2))", //
        "3.1");
    check("TeXForm(N(Pi,30))", //
        "3.14159265358979323846264338327");
    check("TeXForm(Infinity)", //
        "\\infty");
    check("TeXForm(-Infinity)", //
        "-\\infty");
    check("TeXForm(Hold(GoldenRatio))", //
        "\\text{Hold}(\\phi)");
    check("TeXForm(GoldenRatio)", //
        "\\phi");
    check("TeXForm(2+I*3)", //
        "2 + 3\\,i ");
    check("TeXForm(a+b^2)", //
        "a + {b}^{2}");
    check("TeXForm(3*a+b^2)", //
        "3 \\cdot a + {b}^{2}");
    check("TeXForm(x/Sqrt(5))", //
        "\\frac{x}{\\sqrt{5}}");
    check("TeXForm(x^(1/3))", //
        "\\sqrt[3]{x}");
    check("TeXForm(alpha)", //
        "\\alpha");

    check("TeXForm(Integrate(f(x),x))", //
        "\\int  f(x)\\,\\mathrm{d}x");
    check("TeXForm(Limit(f(x), x ->Infinity))", //
        "\\lim_{x\\to {\\infty} }\\,{f(x)}");
    check("TeXForm(Sum(f(n), {n, 1, m}))", //
        "\\sum_{n = 1}^{m} {f(n)}");
    check("TeXForm(Product(f(n), {n, 1, m}))", //
        "\\prod_{n = 1}^{m} {f(n)}");
    check("TeXForm(Subscript(a,b))", //
        "{a}_{b}");
    check("TeXForm(Superscript(a,b))", //
        "{a}^{b}");
    check("TeXForm(Subscript(x,2*k+1))", //
        "{x}_{1+2 \\cdot k}");
    check("TeXForm(Subsuperscript(a,b,c))", //
        "{a}_{b}^{c}");
    check("TeXForm(HarmonicNumber(n))", //
        "H_n");
    check("TeXForm(HarmonicNumber(m,n))", //
        "H_m^{(n)}");
    check("TeXForm(HurwitzZeta(m,n))", //
        "\\zeta (m,n)");
    check("TeXForm(Zeta(m,n))", //
        "\\zeta (m,n)");

    check("TeXForm(fgh(a,b))", //
        "\\text{fgh}(a,b)");
  }

  public void testTeXFormNegativeFraction() {
    check("TeXForm(a/2+Tan(x)/4-Tan(x)^2/3+12)", //
        "12 + \\frac{a}{2} + \\frac{\\tan (x)}{4} - \\frac{{\\tan (x)}^{2}}{3}");
  }

  public void testWeierstrassHalfPeriods() {
    check("TeXForm(WeierstrassHalfPeriods(a))", //
        "\\text{WeierstrassHalfPeriods}(a)");
  }

  public void testTeXFormIntersection() {
    check("TeXForm(Intersection(a,b,c))", //
        "a \\cap b \\cap c");
  }

  public void testTeXFormUnion() {
    check("TeXForm(Union(a,b,c))", //
        "a \\cup b \\cup c");
  }

  public void testTeXFormLogisticSigmoid() {
    check("TeXForm(LogisticSigmoid(a+(b*c)))", //
        "\\sigma (a + b \\cdot c)");
  }

  public void testTeXFormPlusMinus() {
    check("TeXForm(PlusMinus(a,(b*c)))", //
        "a \\pm b \\cdot c");
    check("TeXForm(PlusMinus(a,b)*c)", //
        "c \\cdot \\left( a \\pm b\\right) ");
    check("TeXForm(PlusMinus(a)*c)", //
        "c \\cdot \\left( \\pm{a}\\right) ");
    check("TeXForm(PlusMinus(a)+c)", //
        "c + \\pm{a}");
  }

  public void testTeXDivide() {
    check("TeXForm((x+1)/( 3- 2x))", //
        "\\frac{1 + x}{3-2 \\cdot x}");
  }


  public void testMapIndexed() {
    String input = "MapIndexed(f, {{a, b, c}, {x, y, z}})";

    ExprEvaluator exprEvaluator = new ExprEvaluator();
    TeXUtilities teXUtilities = new TeXUtilities(exprEvaluator.getEvalEngine(), true);
    StringWriter buf = new StringWriter();
    teXUtilities.toTeX(exprEvaluator.parse(input), buf);
    String latex = buf.toString();
    assertEquals(latex, //
        "\\text{MapIndexed}(f,\\left(\n" //
            + "\\begin{array}{ccc}\n"//
            + "a & b & c \\\\\n" //
            + "x & y & z \\\\\n" //
            + "\\end{array}\n" + "\\right) )");
  }


  public void testNonNegativePower() {
    IExpr input = F.Times(F.a, F.Power(F.x, F.HoldForm(F.Plus(F.C1, F.CN1))));

    ExprEvaluator exprEvaluator = new ExprEvaluator();
    TeXUtilities teXUtilities = new TeXUtilities(exprEvaluator.getEvalEngine(), true);
    StringWriter buf = new StringWriter();
    teXUtilities.toTeX(input, buf);
    String latex = buf.toString();
    assertEquals(latex, //
        "a \\cdot {x}^{-1 + 1}");
  }

  public void testMinusOnePower() {
    // issue #770
    IExpr ast = F.Power(F.CN1, F.Plus(F.ZZ(1), F.ZZ(2)));
    TeXFormFactory teXFormFactory = new TeXFormFactory(false, -1, -1, " \\cdot ");
    StringBuilder buffer = new StringBuilder();
    teXFormFactory.convert(buffer, ast);
    assertEquals(buffer.toString(), //
        "{\\left( -1\\right) }^{\\left( 1 + 2\\right) }");
  }

  @Override
  protected void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }

}
