package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Two different symbols with the same name are two variables. Polynomial rings know their variables
 * by name, so a dummy symbol of an algorithm could become one variable with the symbol of the user
 * which has its name: <code>Root(#^6+(x^2-4)*#^4+x^4&amp;,1)</code> factored a polynomial with both
 * and gave <code>-Sqrt(3/2)</code>.
 */
public class SameNameVariablesTest extends ExprEvaluatorTestCase {

  /** The call with a dummy named like the symbol has the result of the call with another name. */
  private static void assertTwoVariables(String name, BiFunction<IExpr, IExpr, IExpr> call) {
    EvalEngine engine = EvalEngine.get();
    ISymbol symbol = F.symbol("t");
    ISymbol dummy = F.Dummy("t");
    ISymbol other = F.symbol("s");
    IExpr expected = engine.evaluate(call.apply(other, symbol));
    IExpr result = engine.evaluate(call.apply(dummy, symbol));
    assertEquals(expected.toString(),
        engine.evaluate(F.subst(result, dummy, other)).toString(), name);
  }

  private static IExpr quadratic(IExpr u, IExpr v) {
    return F.Plus(F.Sqr(u), F.Times(F.C3, u, v), F.Times(F.C2, F.Sqr(v)));
  }

  @Test
  public void testRationalCoefficients() {
    assertTwoVariables("Factor", (u, v) -> F.Factor(quadratic(u, v)));
    assertTwoVariables("FactorList", (u, v) -> F.unaryAST1(S.FactorList, quadratic(u, v)));
    assertTwoVariables("FactorSquareFree", (u, v) -> F.FactorSquareFree(F.Sqr(F.Plus(u, v))));
    assertTwoVariables("Together", (u, v) -> F.Together(F.Plus(F.Divide(F.C1, u), F.Divide(F.C1, v))));
    assertTwoVariables("Cancel", (u, v) -> F.Cancel(F.Divide(quadratic(u, v), F.Plus(u, v))));
    assertTwoVariables("Apart",
        (u, v) -> F.binaryAST2(S.Apart, F.Divide(F.C1, F.Times(u, F.Plus(u, v))), u));
    assertTwoVariables("PolynomialGCD", (u, v) -> F.PolynomialGCD(quadratic(u, v), F.Plus(u, v)));
    assertTwoVariables("PolynomialLCM", (u, v) -> F.binaryAST2(S.PolynomialLCM, quadratic(u, v), F.Plus(u, v)));
    assertTwoVariables("PolynomialQuotient",
        (u, v) -> F.PolynomialQuotient(quadratic(u, v), F.Plus(u, v), u));
    assertTwoVariables("Resultant",
        (u, v) -> F.ternaryAST3(S.Resultant, quadratic(u, v), F.Subtract(u, F.C2), u));
    assertTwoVariables("Discriminant", (u, v) -> F.Discriminant(quadratic(u, v), u));
    assertTwoVariables("Solve", (u, v) -> F.Solve(F.Equal(quadratic(u, v), F.C0), u));
  }

  @Test
  public void testGroebnerBasis() {
    assertTwoVariables("GroebnerBasis", (u, v) -> F.GroebnerBasis(
        F.List(F.Subtract(F.Sqr(u), v), F.Subtract(F.Sqr(v), F.C2)), F.List(u, v)));
    assertTwoVariables("GroebnerBasis Modulus",
        (u, v) -> F.ternaryAST3(S.GroebnerBasis,
            F.List(F.Subtract(F.Sqr(u), v), F.Subtract(F.Sqr(v), F.C2)), F.List(u, v),
            F.Rule(S.Modulus, F.ZZ(7))));
    assertTwoVariables("GroebnerBasis parameter", (u, v) -> F.GroebnerBasis(
        F.List(F.Subtract(F.Sqr(u), v), F.Plus(u, F.C1)), F.List(u)));
    assertTwoVariables("Eliminate", (u, v) -> F.Eliminate(
        F.List(F.Equal(v, F.Sqr(u)), F.Equal(F.symbol("z"), F.Power(u, F.C3))), u));
  }

  @Test
  public void testOtherCoefficientDomains() {
    assertTwoVariables("Factor Modulus",
        (u, v) -> F.binaryAST2(S.Factor, quadratic(u, v), F.Rule(S.Modulus, F.ZZ(7))));
    assertTwoVariables("PolynomialGCD Modulus",
        (u, v) -> F.ternaryAST3(S.PolynomialGCD, quadratic(u, v), F.Plus(u, v),
            F.Rule(S.Modulus, F.ZZ(7))));
    assertTwoVariables("Factor Gaussian",
        (u, v) -> F.binaryAST2(S.Factor, F.Plus(F.Sqr(u), F.Sqr(v)),
            F.Rule(S.GaussianIntegers, S.True)));
    assertTwoVariables("Factor Extension", (u, v) -> F.binaryAST2(S.Factor,
        F.Subtract(F.Sqr(u), F.Times(F.C2, F.Sqr(v))), F.Rule(S.Extension, S.Automatic)));
  }

  @Test
  public void testRootOfAParameterWithTheNameOfItsDummy() {
    check("Root(#^6 + (x^2 - 4)*#^4 + x^4 &, 1)", //
        "Root(#1^6+x^4+#1^4*(-4+x^2)&,1,0)");
    check("Root(#^4 + x*#^2 + x^2 &, 1)", //
        "Root(#1^4+#1^2*x+x^2&,1,0)");
  }

  @Test
  public void testVariablesWhichPrintAlike() {
    // no name is taken twice, and a name which is used stays out of the way
    IAST list = F.List(F.symbol("t"), F.Dummy("t"), F.Dummy("t"), F.symbol("t$1"));
    String[] names = org.matheclipse.core.convert.JASConvert.variableNames(list);
    assertEquals(4, new java.util.HashSet<String>(java.util.Arrays.asList(names)).size());
  }
}
