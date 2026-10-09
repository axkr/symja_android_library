package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
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
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  public static IAST minimizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    long varDegree = polynomial.degree(0);
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 3) {
      // solve cubic or quadratic minimize:
      a = F.C0;
      b = F.C0;
      c = F.C0;
      d = F.C0;
      e = F.C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Minimize::Unexpected exponent value: " + lExp);
        }
      }
      if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // quadratic
          if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              // The `1` is not attained at any point satisfying the constraints.
              return Errors.printMessage(S.Minimize, "natt", F.List("minimum"));
            } else {
              // linear
              return F.list(F.Piecewise(F.list(F.list(e, F.Equal(d, F.C0))), F.CNInfinity), F.list(
                  F.Rule(x, F.Piecewise(F.list(F.list(F.C0, F.Equal(d, F.C0))), S.Indeterminate))));
            }
          } else {
            return F
                .List(
                    F.Piecewise(
                        F.list(
                            F.list(e, F.And(F.Equal(d, 0),
                                F.GreaterEqual(c, 0))),
                            F.list(
                                F.Times(
                                    F.C1D4, F.Power(c, -1), F.Plus(F.Times(-1, F.Power(d, 2)),
                                        F.Times(4, c, e))),
                                F.Or(F.And(F.Greater(d, 0), F.Greater(c, 0)),
                                    F.And(F.Less(d, 0), F.Greater(c, 0))))),
                        F.CNInfinity),
                    F.list(F.Rule(x,
                        F.Piecewise(
                            F.list(
                                F.list(F.Times(F.CN1D2, F.Power(c, -1), d),
                                    F.Or(F.And(F.Greater(d, 0), F.Greater(c, 0)),
                                        F.And(F.Less(d, 0), F.Greater(c, 0)))),
                                F.list(F.C0, F.And(F.Equal(d, 0), F.GreaterEqual(c, 0)))),
                            S.Indeterminate))));
          }
        } else {
          // cubic
          return F.list(
              F.Piecewise(
                  F.list(
                      F.list(e,
                          F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Equal(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)))),
                      F.list(
                          F.Times(F.C1D4, F.Power(c, F.CN1),
                              F.Plus(F.Negate(F.Sqr(d)), F.Times(F.C4, c, e))),
                          F.Or(F.And(F.Greater(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Less(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0))))),
                  F.Noo),
              F.list(F.Rule(x,
                  F.Piecewise(
                      F.list(
                          F.list(F.Times(F.CN1D2, F.Power(c, F.CN1), d),
                              F.Or(F.And(F.Greater(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Less(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)))),
                          F.list(F.C0,
                              F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Equal(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0))))),
                      F.Indeterminate))));
        }
      }
    }

    return F.NIL;
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
    try {
      if (function.isFree(x) && function.isRealResult()) {
        // a constant: attained everywhere
        return F.list(function, F.list(F.Rule(x, F.C0)));
      }
      IExpr piecewise = Maximize.absoluteValueExtremum(function, x, false, engine);
      if (piecewise.isPresent()) {
        return piecewise;
      }
      // bounded linear-trigonometric objective (a*Sin(x) + b*Cos(x) + c)
      IExpr trig = Maximize.linearTrigExtremum(function, x, false, engine);
      if (trig.isPresent()) {
        return trig;
      }
      if (function.isPolynomial(x)) {
        // the objective of `x+1/x` must not be multiplied by `x`
        IExpr temp = minimizeExprPolynomial(function, F.list(x));
        if (temp.isPresent()) {
          return temp;
        }
      }
      IExpr quotient = Maximize.quotientExtremum(head, function, x, false, engine);
      if (quotient.isPresent()) {
        return quotient;
      }

      IExpr yNInf = S.Limit.of(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isNegativeInfinity()) {
        return F.list(F.CNInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.of(function, F.Rule(x, F.CInfinity));
      if (yInf.isNegativeInfinity()) {
        return F.list(F.CNInfinity, F.list(F.Rule(x, F.CInfinity)));
      }

      if (function.isPolynomial(x)) {
        // bounded below, as the limits show: the least value at a stationary point
        IExpr polynomial = Maximize.polynomialExtremum(function, x, false, engine);
        if (polynomial.isPresent()) {
          return polynomial;
        }
      }
      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.of(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr minCandidate = F.NIL;
        IExpr minValue = F.CInfinity;
        IExpr minSolution = F.NIL;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr solution = ((IAST) candidates).get(i).first().second();
            IExpr candidate = Maximize.familyMember(function, x, solution, engine);
            if (candidate.isNIL()) {
              // a family of stationary points along which the function changes decides nothing
              return F.NIL;
            }
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isPositiveResult()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (S.Less.ofQ(functionValue, minValue)) {
                minValue = functionValue;
                minCandidate = candidate;
                minSolution = solution;
              }
            }
          }
          if (minCandidate.isPresent()) {
            if (!Maximize.holdsOverPeriod(function, x, minSolution, minValue, false, engine)) {
              // only a local minimum: the function is smaller elsewhere in the period
              return F.NIL;
            }
            return F.list(minValue, F.list(F.Rule(x, minCandidate)));
          }
        }
        // no stationary point is recognized as a minimum: nothing is decided
        return F.NIL;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Minimize, rex);
    }
    return F.NIL;
  }
}
