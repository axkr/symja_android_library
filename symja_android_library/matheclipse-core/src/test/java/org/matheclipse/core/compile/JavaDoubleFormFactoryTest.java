package org.matheclipse.core.compile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;

class JavaDoubleFormFactoryTest {

  private String convert(IAST ast) {
    JavaDoubleFormFactory factory = JavaDoubleFormFactory.get(true);
    StringBuilder buf = new StringBuilder();
    factory.convertAST(buf, ast, 0, false);
    return buf.toString();
  }

  @Test
  void shouldConvertBasicFunctions() {
    assertEquals("Math.cos(x)", convert(F.Cos(F.x)));
    assertEquals("Math.sin(y)", convert(F.Sin(F.y)));
    assertEquals("Math.log(z)", convert(F.Log(F.z)));
    assertEquals("Math.abs(a)", convert(F.Abs(F.a)));
  }

  @Test
  void shouldConvertPowerToReciprocal() {
    assertEquals("1.0/(x)", convert(F.Power(F.x, F.CN1)));
  }

  @Test
  void shouldConvertPowerToSqrt() {
    assertEquals("Math.sqrt(x)", convert(F.Power(F.x, F.C1D2)));
  }

  @Test
  void shouldConvertPowerToReciprocalSqrt() {
    assertEquals("Math.pow(x,-0.5)", convert(F.Power(F.x, F.CN1D2)));
  }

  @Test
  void shouldConvertPowerToPowMethod() {
    assertEquals("Math.pow(x,y)", convert(F.Power(F.x, F.y)));
  }

  @Test
  void shouldConvertArcTanWithTwoArgumentsToAtan2() {
    assertEquals("Math.atan2(x,y)", convert(F.ArcTan(F.x, F.y)));
  }


  @Test
  void shouldConvertPowerWithNumericExponentToMathPow() {
    assertEquals("Math.pow(a,2)", convert(F.Power(F.a, F.C2)));
  }

  @Test
  void shouldConvertPowerWithFractionalExponentToMathSqrt() {
    // This test assumes the expression is not considered a numeric function returning a complex
    // number
    // which would trigger the complex method calls like .sqrt()
    IAST power = F.Power(F.symbol("a"), F.C1D2);
    assertEquals("Math.sqrt(a)", convert(power));
  }

  @Test
  void shouldConvertPowerWithCubicRootExponentToMathCbrt() {
    IAST power = F.Power(F.symbol("a"), F.C1D3);
    assertEquals("Math.cbrt(a)", convert(power));
  }

  @Test
  void shouldConvertInfinityToDoubleConstant() {
    assertEquals("Double.POSITIVE_INFINITY", convert(F.CInfinity));
  }

  @Test
  void shouldConvertNegativeInfinityToDoubleConstant() {
    assertEquals("Double.NEGATIVE_INFINITY", convert(F.CNInfinity));
  }

  @Test
  void shouldConvertUnevaluatedExpression() {
    assertEquals("a", convert(F.Unevaluated(F.a)));
  }
}
