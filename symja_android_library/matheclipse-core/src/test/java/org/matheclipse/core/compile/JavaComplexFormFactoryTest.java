package org.matheclipse.core.compile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class JavaComplexFormFactoryTest {
  private static EvalEngine engine;
  private static JavaComplexFormFactory factory;

  @BeforeAll
  public static void setUp() {
    engine = new EvalEngine(true);
    factory = JavaComplexFormFactory.get(true);
  }

  private String convert(IAST ast) {
    StringBuilder buf = new StringBuilder();
    factory.convertAST(buf, ast);
    return buf.toString();
  }

  @Test
  void shouldConvertBasicFunctions() {
    assertEquals("(x).cos()", convert(F.Cos(F.x)));
    assertEquals("(y).sin()", convert(F.Sin(F.y)));
    assertEquals("(z).log()", convert(F.Log(F.z)));
    assertEquals("(a).abs()", convert(F.Abs(F.a)));
  }

  @Test
  void shouldConvertPowerToReciprocal() {
    assertEquals("(x).reciprocal()", convert(F.Power(F.x, F.CN1)));
  }

  @Test
  void shouldConvertPowerToSqrt() {
    assertEquals("(x).sqrt()", convert(F.Power(F.x, F.C1D2)));
  }

  @Test
  void shouldConvertPowerToReciprocalSqrt() {
    assertEquals("(x).reciprocal().sqrt()", convert(F.Power(F.x, F.CN1D2)));
  }

  @Test
  void shouldConvertPowerToPowMethod() {
    assertEquals("(x).pow(y)", convert(F.Power(F.x, F.y)));
  }

  @Test
  void shouldConvertArcTanWithTwoArgumentsToAtan2() {
    assertEquals("(x).atan2(y)", convert(F.ArcTan(F.x, F.y)));
  }

  @Test
  void shouldEvaluateNumericFunctionToComplexValueOf() {
    IAST ast = F.Plus(F.C1, F.Times(F.C2, F.CI)); // 1 + 2*I
    assertEquals("Complex.valueOf(1.0,2.0)", convert(ast));
  }

  @Test
  void shouldConvertPowerWithNumericExponentToMathPow() {
    assertEquals("(a).pow(Complex.valueOf(2.0))", convert(F.Power(F.a, F.C2)));
  }

  @Test
  void shouldConvertPowerWithFractionalExponentToMathSqrt() {
    // This test assumes the expression is not considered a numeric function returning a complex
    // number
    // which would trigger the complex method calls like .sqrt()
    IAST power = F.Power(F.a, F.C1D2);
    assertEquals("(a).sqrt()", convert(power));
  }

  @Test
  void shouldConvertPowerWithCubicRootExponentToMathCbrt() {
    IAST power = F.Power(F.a, F.C1D3);
    assertEquals("(a).cbrt()", convert(power));
  }

  @Test
  void shouldConvertInfinityToComplexConstant() {
    assertEquals("Complex.INF", convert(F.CInfinity));
  }

  @Test
  void shouldConvertNegativeInfinityToComplexConstant() {
    assertEquals("(-Complex.INF)", convert(F.CNInfinity));
  }

  @Test
  void shouldConvertUnevaluatedExpression() {
    assertEquals("a", convert(F.Unevaluated(F.a)));
  }

  /**
   * Helper method to parse a mathematical expression and assert its generated Java string.
   */
  private void check(String mathExpression, String expectedJava) {
    IExpr expr = engine.parse(mathExpression);
    StringBuilder buf = new StringBuilder();
    factory.convert(buf, expr);
    Assertions.assertEquals(expectedJava, buf.toString());
  }

  @Test
  public void testNativeHipparchusMethods() {
    // Basic trigonometric and algebraic functions mapped to native instance methods
    check("Sin(x)", //
        "(x).sin()");
    check("Cos(x)", //
        "(x).cos()");
    check("Abs(x)", //
        "(x).abs()");
    check("Log(x)", //
        "(x).log()");
    check("Sinh(x)", //
        "(x).sinh()");
    check("Ceiling(x)", //
        "(x).ceil()");

    // ArcTan with 1 argument
    check("ArcTan(x)", "(x).atan()");

    // ArcTan with 2 arguments should map to atan2
    check("ArcTan(x, y)", "(x).atan2(y)");
  }

  @Test
  public void testCMathStaticMethods() {
    // Functions delegated to the static CMath wrapper methods
    check("Gamma(x)", //
        "CMath.gamma(x)");
    check("BesselJ(v, z)", //
        "CMath.besselJ(v,z)");
    check("AiryAi(z)", //
        "CMath.airyAi(z)");
    check("Erf(z)", //
        "CMath.erf(z)");
    check("Hypergeometric2F1(a, b, c, z)", //
        "CMath.hypergeometric2F1(a,b,c,z)");
  }

  @Test
  public void testPowerOptimizations() {
    // Standard power
    check("Power(x, y)", //
        "(x).pow(y)");
    check("x^y", //
        "(x).pow(y)");

    // Fractional and negative power optimizations
    check("x^(1/2)", //
        "(x).sqrt()");
    check("x^(1/3)", //
        "(x).cbrt()");
    check("x^(-1)", //
        "(x).reciprocal()");
    check("x^(-1/2)", //
        "(x).reciprocal().sqrt()");
  }

  @Test
  public void testNumericEvaluation() {
    // Purely numeric expressions should evaluate immediately and output Complex.valueOf
    check("1.5 + 2.5 * I", //
        "Complex.valueOf(1.5, 2.5)");
    check("3.0", //
        "Complex.valueOf(3.0)");
  }

  @Test
  public void testInfinities() {
    // Directed infinities mapped to Hipparchus Complex.INF
    check("Infinity", //
        "Complex.INF");
    check("-Infinity", //
        "(-Complex.INF)");
  }

  @Test
  public void testEvaluationBypasses() {
    // ASTs that hold evaluation should simply output their inner argument
    check("Hold(x)", //
        "x");
    check("Defer(x)", //
        "x");
    check("Evaluate(x)", //
        "x");
  }

  @Test
  public void testFallbackOfN() {
    // Built-in symbols not defined in FUNCTIONS_STR should fallback to F.Function.ofN()
    check("Zeta(x)", //
        "F.Zeta.ofN(x)");
    check("PrimePi(x)", //
        "F.PrimePi.ofN(x)");
  }
}
