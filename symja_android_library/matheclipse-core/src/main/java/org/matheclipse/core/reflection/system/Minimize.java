package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.optimization.OptimizationMethod;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

/**
 *
 *
 * <pre>
 * <code>Minimize(unary-function, variable)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the minimum of the unary function for the given <code>variable</code>.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Derivative_test">Wikipedia - Derivative test</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; Minimize(x^4+7*x^3-2*x^2 + 42, x)
 * {42+7*(-21/8-Sqrt(505)/8)^3-2*(21/8+Sqrt(505)/8)^2+(21/8+Sqrt(505)/8)^4,{x-&gt;-21/8-Sqrt(505)/8}}
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Maximize.md">Maximize</a>
 */
public class Minimize extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST plain = Maximize.withoutMethod(ast, false, engine);
    if (plain.isPresent()) {
      return plain.isList() ? plain : evaluate(plain, engine);
    }
    return Maximize.withNumericFallback(ast, S.NMinimize, engine, () -> evaluateExact(ast, engine));
  }

  private IExpr evaluateExact(final IAST ast, EvalEngine engine) {
    IExpr integerLinear = Maximize.integerLinearExtremum(ast.topHead(), ast, false, engine);
    if (integerLinear.isPresent()) {
      return integerLinear;
    }
    IExpr plain = Maximize.plainForm(ast);
    if (plain.isPresent()) {
      return plain;
    }
    IExpr function = ast.arg1();
    IExpr x = ast.arg2();
    ISymbol head = ast.topHead();
    if (ast.isAST3()) {
      IExpr domain = ast.arg3();
      if (domain == S.Integers) {
        return Maximize.integerExtremum(head, function, x, false, engine);
      }
      if (domain != S.Reals) {
        // only the Reals and Integers domains are supported
        return F.NIL;
      }
    }
    if (x.isList()) {
      if (x.isList1()) {
        x = x.first();
      } else {
        IExpr result = Maximize.multivariateExtremum(head, function, (IAST) x, false, engine);
        if (result.isPresent()) {
          return result;
        }
        // `1` currently not supported in `2`.
        return Errors.printMessage(S.Minimize, "unsupported",
            F.List("Multiple variables", "Minimize"), engine);
      }
    }
    if (x.isSymbol() || (x.isAST() && !x.isList())) {
      if (function.isList2()) {
        // single variable with a constraint: optimize over the feasible real interval
        IExpr result = Maximize.univariateConstrainedExtremum(head, function.first(),
            function.second(), x, false, engine);
        // a constrained problem which isn't decided stays unevaluated; the unconstrained method
        // cannot read the {objective, constraint} list
        return result;
      }
      return minimize(head, function, x, engine);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // a trailing Method -> ... rule is not counted
    return OptimizationMethod.trailingMethod(ast, 2).isPresent() ? ARGS_3_4 : ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  /**
   * The coefficients <code>{a, b, c, d, e}</code> of <code>a*x^4 + b*x^3 + c*x^2 + d*x + e</code>,
   * zero where a power is missing.
   *
   * @param functionName the name the exception for a higher exponent gives
   * @throws ArithmeticException for an exponent above 4
   */
  static IExpr[] polynomialCoefficients(ExprPolynomial polynomial, String functionName) {
    IExpr[] coefficients = {F.C0, F.C0, F.C0, F.C0, F.C0};
    for (ExprMonomial monomial : polynomial) {
      long lExp = monomial.exponent().getVal(0);
      if (lExp < 0 || lExp > 4) {
        throw new ArithmeticException(functionName + "::Unexpected exponent value: " + lExp);
      }
      coefficients[4 - (int) lExp] = monomial.coefficient();
    }
    return coefficients;
  }

  public static IAST minimizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    return Maximize.cubicPolynomialExtremum(polynomial, x, false);
  }

  public static IAST minimizeExprPolynomial(final IExpr expr, IAST varList) {
    IAST result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = Minimize.minimizeCubicPolynomial(ePoly, varList.arg1());

      // result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (ArithmeticException | JASConversionException e2) {
      // not a polynomial of this ring: the other methods are tried
      return F.NIL;
    }
  }

  public static final IExpr minimize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    return Maximize.extremum(head, function, x, false, engine);
  }
}
