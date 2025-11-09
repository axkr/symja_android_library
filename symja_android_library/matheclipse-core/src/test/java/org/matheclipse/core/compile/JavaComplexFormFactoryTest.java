package org.matheclipse.core.compile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.interfaces.IAST;

public class JavaComplexFormFactoryTest {

  private String convert(IAST ast) {
    JavaComplexFormFactory factory = JavaComplexFormFactory.get(true);
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
}
