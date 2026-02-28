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
    IAST power = F.Power(F.a, F.C1D2);
    assertEquals("Math.sqrt(a)", convert(power));
  }

  @Test
  void shouldConvertPowerWithCubicRootExponentToMathCbrt() {
    IAST power = F.Power(F.a, F.C1D3);
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

  @Test
  void shouldConvertDMathSpecialFunctions() {
    assertEquals("DMath.agm(a,b)", convert(F.ArithmeticGeometricMean(F.a, F.b)));
    assertEquals("DMath.airyAi(x)", convert(F.AiryAi(F.x)));
    assertEquals("DMath.besselJ(v,x)", convert(F.BesselJ(F.v, F.x)));
    assertEquals("DMath.beta(a,b)", convert(F.Beta(F.a, F.b)));
    assertEquals("DMath.chebyshevT(n,x)", convert(F.ChebyshevT(F.n, F.x)));
    assertEquals("DMath.polyGamma(x)", convert(F.PolyGamma(F.x)));
    assertEquals("DMath.ellipticE(x)", convert(F.EllipticE(F.x)));
    assertEquals("DMath.erf(x)", convert(F.Erf(F.x)));
    assertEquals("DMath.fibonacci(n,x)", convert(F.Fibonacci(F.n, F.x)));
    assertEquals("DMath.gamma(x)", convert(F.Gamma(F.x)));
    assertEquals("DMath.harmonicNumber(n)", convert(F.HarmonicNumber(F.n)));
    assertEquals("DMath.inverseErf(x)", convert(F.InverseErf(F.x)));
    assertEquals("DMath.laguerreL(n,x)", convert(F.LaguerreL(F.n, F.x)));
    assertEquals("DMath.legendreP(n,x)", convert(F.LegendreP(F.n, F.x)));
    assertEquals("DMath.logGamma(x)", convert(F.LogGamma(F.x)));
    assertEquals("DMath.polyLog(n,x)", convert(F.PolyLog(F.n, F.x)));
  }

  @Test
  void shouldConvertDMathArcAndHyperbolicTrig() {
    assertEquals("DMath.acosh(x)", convert(F.ArcCosh(F.x)));
    assertEquals("DMath.asinh(x)", convert(F.ArcSinh(F.x)));
    assertEquals("DMath.atanh(x)", convert(F.ArcTanh(F.x)));

    assertEquals("DMath.csc(x)", convert(F.Csc(F.x)));
    assertEquals("DMath.sec(x)", convert(F.Sec(F.x)));
    assertEquals("DMath.cot(x)", convert(F.Cot(F.x)));

    assertEquals("DMath.csch(x)", convert(F.Csch(F.x)));
    assertEquals("DMath.sech(x)", convert(F.Sech(F.x)));
    assertEquals("DMath.coth(x)", convert(F.Coth(F.x)));

    assertEquals("DMath.acsc(x)", convert(F.ArcCsc(F.x)));
    assertEquals("DMath.asec(x)", convert(F.ArcSec(F.x)));
    assertEquals("DMath.acot(x)", convert(F.ArcCot(F.x)));
    assertEquals("DMath.acsch(x)", convert(F.ArcCsch(F.x)));
    assertEquals("DMath.asech(x)", convert(F.ArcSech(F.x)));
    assertEquals("DMath.acoth(x)", convert(F.ArcCoth(F.x)));
  }

  @Test
  void shouldConvertDMathHypergeometricFunctions() {
    assertEquals("DMath.hypergeometric0F1Regularized(a,x)",
        convert(F.Hypergeometric0F1Regularized(F.a, F.x)));
    assertEquals("DMath.hypergeometric2F1(a,b,c,x)",
        convert(F.Hypergeometric2F1(F.a, F.b, F.c, F.x)));
  }

  @Test
  void shouldConvertDMathJacobiAndGegenbauer() {
    assertEquals("DMath.jacobiP(n,a,b,x)", convert(F.JacobiP(F.n, F.a, F.b, F.x)));
    assertEquals("DMath.gegenbauerC(n,m,x)", convert(F.GegenbauerC(F.n, F.m, F.x)));
  }
}