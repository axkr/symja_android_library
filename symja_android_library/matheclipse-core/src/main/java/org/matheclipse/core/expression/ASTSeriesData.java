package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import jakarta.annotation.Nullable;

public class ASTSeriesData extends AbstractAST implements Externalizable {

  /**
   * Returns the list of coefficients of a polynomial expression with respect to the variables in
   * <code>listOfVariables</code>.
   *
   * @param polynomialExpr a polynomial expression
   * @param listOfVariables a list of variable symbols
   * @return the list of coefficients of the polynomial expression;or {@link F#NIL}
   */
  public static IAST coefficientList(IExpr polynomialExpr, IAST listOfVariables) {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(listOfVariables);
      ExprPolynomial poly = ring.create(polynomialExpr, true, false, true);
      if (poly.isZero()) {
        return F.CEmptyList;
      }
      return poly.coefficientList();
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      // org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing.create()
    }
    if (listOfVariables.argSize() > 0) {
      return F.Nest(S.List, polynomialExpr, listOfVariables.argSize());
    }
    return F.NIL;
  }

  /**
   * Evaluates the series expansion of a composite function f(g(x)) using ComposeSeries logic. This
   * bypasses direct Taylor expansion of nested structures, avoiding catastrophic symbolic
   * derivative explosions (e.g., trying to take 11 derivatives of Gamma(Sin(x)-x)).
   */
  private static ASTSeriesData compositionSeries(final IAST function, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    if (function.argSize() == 1) {
      IExpr arg = function.arg1();
      if (!arg.isFree(x) && !arg.equals(x)) {

        // Find the inner evaluation point u0
        IExpr u0 = engine.evalQuiet(F.subst(arg, x, x0));
        if (u0.isIndeterminate() || u0.isDirectedInfinity()) {
          IExpr dirExpr = direction == 0 ? S.Reals : F.ZZ(direction);
          u0 = engine.evalQuiet(F.Limit(arg, F.Rule(x, x0), F.Rule(S.Direction, dirExpr)));
        }

        if (u0.isSpecialsFree()) {
          // Generate the series for the inner argument
          int currentN = n;
          ASTSeriesData innerSeries = seriesDataRecursive(arg, x, x0, currentN, direction, engine);

          // Step up evaluation if the inner series mathematically cancels out
          // This prevents false constant evaluations that lead to ComplexInfinity poles
          int probeLimit = Math.max(n, 0) + 12;
          while (innerSeries != null && innerSeries.isOrder() && currentN < probeLimit) {
            currentN += 3;
            innerSeries = seriesDataRecursive(arg, x, x0, currentN, direction, engine);
          }

          if (innerSeries != null) {
            // Find the lowest non-constant mathematical growth rate (k)
            int k = innerSeries.minExponent();
            if (k <= 0) {
              k = 1;
              while (k < innerSeries.truncateOrder() && innerSeries.coefficient(k).isZero()) {
                k++;
              }
            }

            if (k >= innerSeries.truncateOrder()) {
              // The inner function evaluates to a constant up to the requested order
              IExpr f0 = engine.evalQuiet(F.subst(function, x, x0));
              ASTSeriesData constSeries =
                  new ASTSeriesData(x, x0, 0, n, innerSeries.puiseuxDenominator());
              constSeries.setCoeff(0, f0);
              return constSeries;
            }

            if (k > 0) {
              int den = innerSeries.puiseuxDenominator();
              // Scale the required outer boundary.
              // If inner grows as O(x^3) and target is O(x^11), outer only needs O(y^4).
              int outerN = (n * den + k - 1) / k;
              outerN = Math.max(outerN, 1) + 2; // Buffer terms for safety

              IExpr y = F.Dummy("y");
              IExpr outerFunc = function.setAtCopy(1, y);

              // Evaluate the fast Taylor series of the isolated outer function
              ASTSeriesData outerSeries =
                  seriesDataRecursive(outerFunc, y, u0, outerN, direction, engine);

              if (outerSeries != null) {
                // Compose them together algebraically
                return outerSeries.compose(innerSeries);
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Universal fallback to dynamically extract the mathematically dominant term of an arbitrary
   * expression by computing its series and isolating the lowest-order non-zero term.
   */
  private static IExpr extractFromSeries(IExpr expr, IExpr x, IExpr x0, EvalEngine engine) {
    int n = 1;
    ASTSeriesData sd = null;
    while (n < 20) {
      sd = ASTSeriesData.seriesDataRecursive(expr, x, x0, n, engine);
      if (sd != null && !sd.isOrder()) {
        int nMin = sd.minExponent();
        while (nMin < sd.truncateOrder() && sd.coefficient(nMin).isZero()) {
          nMin++;
        }
        if (nMin < sd.truncateOrder()) {
          IExpr coeff = sd.coefficient(nMin);
          IExpr varPart;

          if (x0.isInfinity() || x0.isNegativeInfinity()) {
            // SeriesData at infinity conceptually expands in (1/x).
            // The leading term is mathematically coeff * x^(-nMin).
            return engine.evaluate(F.Times(coeff, F.Power(x, F.ZZ(-nMin))));
          } else if (x0.isZero()) {
            varPart = x;
          } else {
            varPart = F.Subtract(x, x0);
          }
          return engine.evaluate(F.Times(coeff, F.Power(varPart, F.ZZ(nMin))));
        }
      }
      n += 2;
    }
    return F.NIL;
  }

  /**
   * Computes the leading term of an expression asymptotically approaching x0. Mimics SymPy's
   * as_leading_term function to evaluate the bottom-up dominant term.
   */
  public static IExpr leadingTerm(IExpr expr, IExpr x, IExpr x0, EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }
    if (expr.equals(x)) {
      if (x0.isZero()) {
        return x;
      }
      return F.Subtract(x, x0);
    }

    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      if (head.isBuiltInSymbol()) {

        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Plus: {
            // Defer immediately to the universal series extractor
            return extractFromSeries(expr, x, x0, engine);
          }
          // case ID.Plus: {
          // // Extract the series up to an order where terms do not completely cancel out.
          // // We step up the truncation boundary until a mathematically non-zero term survives.
          // int n = 1;
          // ASTSeriesData sd = null;
          // while (n < 20) {
          // sd = ASTSeriesData.seriesDataRecursive(expr, x, x0, n, engine);
          // if (sd != null && !sd.isOrder()) {
          // int nMin = sd.minExponent();
          // while (nMin < sd.truncateOrder() && sd.coefficient(nMin).isZero()) {
          // nMin++;
          // }
          // if (nMin < sd.truncateOrder()) {
          // IExpr coeff = sd.coefficient(nMin);
          // IExpr varPart = x0.isZero() ? x : F.Subtract(x, x0);
          // return engine.evaluate(F.Times(coeff, F.Power(varPart, F.ZZ(nMin))));
          // }
          // }
          // n += 2;
          // }
          // return F.NIL;
          // }
          case ID.Times: {
            IASTAppendable timesResult = F.TimesAlloc(ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              IExpr lt = leadingTerm(ast.get(i), x, x0, engine);
              if (lt.isNIL()) {
                return F.NIL;
              }
              timesResult.append(lt);
            }
            return engine.evaluate(timesResult);
          }
          case ID.Power: {
            IExpr baseLt = leadingTerm(ast.base(), x, x0, engine);
            IExpr expLimit = engine.evaluate(F.subst(ast.exponent(), x, x0));
            // Be strictly conservative: only apply baseLt^expLimit if the exponent limit is
            // completely free of mathematical singularities. Fallback to Series if 0^0 or
            // Infinity^0.
            if (baseLt.isPresent() && expLimit.isSpecialsFree() && !expLimit.isZero()) {
              return engine.evaluate(F.Power(baseLt, expLimit));
            }
            break;
          }

          case ID.ArcCos: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              // ArcCos(u) -> Pi/2 as u -> 0
              return engine.evaluate(F.Divide(S.Pi, F.C2));
            }
            break;
          }
          case ID.Sin:
          case ID.Tan:
          case ID.Sinh:
          case ID.Tanh:
          case ID.ArcSin:
          case ID.ArcTan:
          case ID.ArcSinh:
          case ID.ArcTanh: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              return leadingTerm(arg, x, x0, engine);
            }
            break;
          }
          case ID.Cos:
          case ID.Cosh:
          case ID.Exp: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              return F.C1;
            }
            break;
          }
          case ID.Gamma: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            // Intercept all poles for Gamma (0, -1, -2, ...)
            if (argLimit.isInteger() && (argLimit.isZero() || argLimit.isNegativeResult())) {
              IExpr ltArg = leadingTerm(F.Subtract(arg, argLimit), x, x0, engine);
              if (ltArg.isPresent()) {
                IExpr nFact = F.Factorial(argLimit.negate());
                IExpr sign = F.Power(F.CN1, argLimit.negate());
                IExpr coeff = engine.evaluate(F.Divide(sign, nFact));
                return engine.evaluate(F.Times(coeff, F.Power(ltArg, F.CN1)));
              }
            }
            break;
          }
          case ID.PolyGamma: {
            if (ast.argSize() == 2) {
              IExpr n = ast.arg1();
              IExpr z = ast.arg2();
              IExpr zLimit = engine.evaluate(F.subst(z, x, x0));
              // PolyGamma(n, z) ~ (-1)^(n+1) * n! * z^(-n-1) as z -> 0
              if (zLimit.isZero() && n.isInteger() && !n.isNegativeResult()) {
                IExpr ltZ = leadingTerm(z, x, x0, engine);
                if (ltZ.isPresent()) {
                  IExpr sign = F.Power(F.CN1, F.Plus(n, F.C1));
                  IExpr fact = F.Factorial(n);
                  IExpr power = F.Power(ltZ, F.Negate(F.Plus(n, F.C1)));
                  return engine.evaluate(F.Times(sign, fact, power));
                }
              }
            }
            break;
          }
          case ID.Log: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isOne()) {
              return leadingTerm(F.Subtract(arg, F.C1), x, x0, engine);
            } else if (argLimit.isZero()) {
              IExpr ltArg = leadingTerm(arg, x, x0, engine);
              if (ltArg.isPresent()) {
                return engine.evaluate(F.Log(ltArg));
              }
            }
            break;
          }
          case ID.Cot:
          case ID.Coth:
          case ID.Csc:
          case ID.Csch: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              IExpr ltArg = leadingTerm(arg, x, x0, engine);
              if (ltArg.isPresent()) {
                return engine.evaluate(F.Power(ltArg, F.CN1));
              }
            }
            break;
          }
          case ID.Sec:
          case ID.Sech:
          case ID.Erfc: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              return F.C1;
            }
            break;
          }
          case ID.Erf:
          case ID.Erfi: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              IExpr ltArg = leadingTerm(arg, x, x0, engine);
              if (ltArg.isPresent()) {
                return engine.evaluate(F.Times(F.C2, F.Power(S.Pi, F.CN1D2), ltArg));
              }
            }
            break;
          }
          case ID.Abs:
          case ID.Sign: {
            IExpr arg = ast.arg1();
            IExpr ltArg = leadingTerm(arg, x, x0, engine);
            if (ltArg.isPresent()) {
              return engine.evaluate(F.unaryAST1(head, ltArg));
            }
            return F.NIL;
          }
          case ID.InverseErf: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
              IExpr ltArg = leadingTerm(arg, x, x0, engine);
              if (ltArg.isPresent()) {
                return engine.evaluate(F.Times(F.Power(S.Pi, F.C1D2), F.C1D2, ltArg));
              }
            }
            break;
          }
          case ID.BesselJ:
          case ID.BesselI: {
            if (ast.argSize() == 2) {
              IExpr nu = ast.arg1();
              IExpr z = ast.arg2();
              IExpr zLimit = engine.evaluate(F.subst(z, x, x0));
              if (zLimit.isZero()) {
                IExpr ltZ = leadingTerm(z, x, x0, engine);
                if (ltZ.isPresent()) {
                  // Evaluate nu at the limit point to drop symbolic x-dependencies
                  IExpr nuLimit = engine.evaluate(F.subst(nu, x, x0));
                  IExpr halfZ = engine.evaluate(F.Times(F.C1D2, ltZ));
                  IExpr gammaTerm = engine.evaluate(F.Power(F.Gamma(F.Plus(nuLimit, F.C1)), F.CN1));
                  return engine.evaluate(F.Times(gammaTerm, F.Power(halfZ, nuLimit)));
                }
              }
            }
            break;
          }
          case ID.EllipticK:
          case ID.EllipticE: {
            if (ast.argSize() == 1) {
              IExpr m = ast.arg1();
              IExpr mLimit = engine.evaluate(F.subst(m, x, x0));
              if (mLimit.isZero()) {
                return engine.evaluate(F.Times(S.Pi, F.C1D2));
              }
            }
            break;
          }
        }
      }
    }

    // Attempt direct evaluation for non-zero constants
    IExpr limit = engine.evaluate(F.subst(expr, x, x0));
    if (limit.isPresent() && !limit.isZero() && !limit.isIndeterminate()
        && !limit.isDirectedInfinity()) {
      return limit;
    }
    return F.NIL;
    // UNIVERSAL FALLBACK: If limit is 0 (e.g. Sin(x) at Pi) or Indeterminate,
    // mathematically generate the generic Taylor/Laurent series to find the actual leading term.
    // return extractFromSeries(expr, x, x0, engine);
  }

  /**
   * Computes the power series expansion for an addition expression ({@code Plus[...]}).
   * <p>
   * This method separates exact polynomial terms from transcendental functions to prevent Big-O
   * truncation from prematurely swallowing exact terms. It recursively generates the series for the
   * transcendental components and combines them using exact fractional grid merging via
   * {@link ASTSeriesData#plusPS(ASTSeriesData)}.
   *
   * @param plusAST the addition expression to expand
   * @param x the expansion variable
   * @param x0 the point around which to expand
   * @param n the requested mathematical order of the expansion
   * @param engine the evaluation engine
   * @return the combined {@code ASTSeriesData}, or {@code null} if the series cannot be generated
   */
  private static ASTSeriesData plusSeriesDataRecursive(final IAST plusAST, IExpr x, IExpr x0,
      final int n, EvalEngine engine) {
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
    IASTAppendable rest = F.PlusAlloc(4);

    ASTSeriesData temp = null;
    if (x0.isZero()) {
      temp = ASTSeriesData.polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
    } else {
      rest.appendArgs(plusAST);
    }

    if (temp != null) {
      if (rest.size() <= 1) {
        return temp;
      }
    }

    ASTSeriesData series = null;
    int start = 1;
    if (temp != null) {
      series = temp;
    } else {
      if (rest.size() > 1) {
        series = seriesDataRecursive(rest.arg1(), x, x0, n, engine);
        if (series == null) {
          return null;
        }
        start = 2;
      }
    }

    if (series != null) {
      for (int i = start; i < rest.size(); i++) {
        ASTSeriesData arg = seriesDataRecursive(rest.get(i), x, x0, n, engine);
        if (arg == null) {
          return null;
        }
        series = series.plusPS(arg);
      }
      return series;
    }
    return null;
  }

  private static ASTSeriesData plusSeriesDataRecursive(final IAST plusAST, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
    IASTAppendable rest = F.PlusAlloc(4);

    ASTSeriesData temp = null;
    if (x0.isZero()) {
      temp = ASTSeriesData.polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
    } else {
      rest.appendArgs(plusAST);
    }

    if (temp != null) {
      if (rest.size() <= 1) {
        return temp;
      }
    }

    ASTSeriesData series = null;
    int start = 1;
    if (temp != null) {
      series = temp;
    } else {
      if (rest.size() > 1) {
        series = seriesDataRecursive(rest.arg1(), x, x0, n, direction, engine);
        if (series == null) {
          return null;
        }
        start = 2;
      }
    }

    if (series != null) {
      for (int i = start; i < rest.size(); i++) {
        ASTSeriesData arg = seriesDataRecursive(rest.get(i), x, x0, n, direction, engine);
        if (arg == null) {
          return null;
        }
        series = series.plusPS(arg);
      }
      return series;
    }
    return null;
  }

  /**
   * Attempts to extract exact polynomial terms from a given function and construct a power series.
   * <p>
   * This method delegates to the polynomial ring evaluator {@link ExprPolynomialRing} to separate
   * pure polynomial components from transcendental or unresolved components. Extracted polynomial
   * terms are populated into the {@code coefficientMap}, while leftover terms are appended to
   * {@code rest}. If any polynomial terms are found, it generates the corresponding
   * {@code ASTSeriesData}.
   *
   * @param function the mathematical expression to parse for polynomial terms
   * @param x the expansion variable
   * @param x0 the point around which to expand
   * @param n the requested mathematical order of the expansion
   * @param coefficientMap an empty map to be populated with extracted exponents and coefficients
   * @param rest an accumulator list to hold any non-polynomial or leftover terms
   * @return the resulting {@code ASTSeriesData} representing the polynomial portion, or
   *         {@code null} if no polynomial terms could be extracted
   */
  private static ASTSeriesData polynomialSeries(final IExpr function, IExpr x, IExpr x0,
      final int n, Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    coefficientMap = ExprPolynomialRing.create(function, x, coefficientMap, rest);
    if (coefficientMap.size() > 0) {
      return seriesFromMap(x, x0, n, coefficientMap, rest);
    }
    return null;
  }

  /**
   * Computes the power series expansion for an exponentiation expression ({@code Power[...]}). *
   * <p>
   * Generates the series for the base expression and raises it to the requested rational or integer
   * power using the J.C.P. Miller formula. <br>
   * If the requested power degrades the target boundary (e.g., when the base is a Laurent series
   * raised to a negative power), this method dynamically recalculates the required extra base
   * terms, steps up the base order {@code n}, and re-evaluates the power to perfectly hit the
   * target {@code O(x^n)} bound without dropping coefficients.
   *
   * @param powerAST the exponentiation expression to expand
   * @param x the expansion variable
   * @param x0 the point around which to expand
   * @param n the requested mathematical order of the expansion
   * @param engine the evaluation engine
   * @return the exponentiated {@code ASTSeriesData}, or {@code null} if the series cannot be
   *         generated
   */
  private static ASTSeriesData powerSeriesDataRecursive(final IExpr powerAST, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();

    if (base.isFree(x)) {
      if (exponent.isPower() && exponent.base().equals(x) && exponent.exponent().isRational()) {
        IRational rat = (IRational) exponent.exponent();
        if (rat.isPositive()) {
          int numerator = rat.numerator().toIntDefault();
          int denominator = rat.denominator().toIntDefault();
          if (F.isPresent(denominator)) {
            ASTSeriesData temp =
                seriesDataRecursive(F.Power(base, x), x, x0, n * denominator, direction, engine);
            if (temp != null) {
              ASTSeriesData series = temp;
              if (numerator != 1) {
                series = series.shiftTimes(numerator, F.C1, series.truncateOrder());
              }
              series.setDenominator(denominator);
              return series;
            }
          }
        }
      }
    } else if (!(base instanceof ASTSeriesData)) {
      if (x0.isZero()) {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        IASTAppendable rest = F.PlusAlloc(4);
        ASTSeriesData temp = polynomialSeries(powerAST, x, x0, n, coefficientMap, rest);
        if (temp != null) {
          return temp;
        }
      }
    }

    int currentN = n;
    // Extract the mathematically exact starting degree to prevent empty series generation
    IExpr lt = leadingTerm(base, x, x0, engine);
    if (lt.isPresent() && !lt.isZero()) {
      IExpr varPart = x0.isZero() ? x : F.Subtract(x, x0);
      int leadDegree = -1;

      if (lt.isFree(x)) {
        leadDegree = 0;
      } else if (lt.equals(varPart) || lt.equals(x)) {
        leadDegree = 1;
      } else if (lt.isPower() && (lt.base().equals(varPart) || lt.base().equals(x))) {
        IExpr exp = lt.exponent();
        if (exp.isInteger()) {
          leadDegree = exp.toIntDefault();
        }
      } else if (lt.isTimes()) {
        for (int i = 1; i <= lt.argSize(); i++) {
          IExpr arg = ((IAST) lt).get(i);
          if (arg.equals(varPart) || arg.equals(x)) {
            leadDegree = 1;
            break;
          } else if (arg.isPower() && (arg.base().equals(varPart) || arg.base().equals(x))) {
            IExpr exp = arg.exponent();
            if (exp.isInteger()) {
              leadDegree = exp.toIntDefault();
              break;
            }
          }
        }
      }

      // If we found the valid mathematical degree, ensure currentN is large enough to capture it
      if (leadDegree != -1 && currentN <= leadDegree) {
        currentN = leadDegree + 1;
      }
    }
    ASTSeriesData series = seriesDataRecursive(base, x, x0, currentN, direction, engine);

    int probeLimit = Math.max(n, 0) + 12;
    while (series != null && series.isOrder() && currentN < probeLimit) {
      currentN += 3;
      series = seriesDataRecursive(base, x, x0, currentN, direction, engine);
    }

    if (series != null) {
      IRational p = null;
      if (exponent.isRational()) {
        p = (IRational) exponent;
      } else if (exponent.isInteger()) {
        int expInt = exponent.toIntDefault();
        if (F.isPresent(expInt)) {
          p = F.QQ(expInt, 1);
        }
      }

      if (p != null) {
        // Dispatch to fractional/integer power generation
        IExpr pExpr = series.powerSeries(p, engine);

        if (pExpr.isPresent()) {
          if (pExpr instanceof ASTSeriesData) {
            ASTSeriesData pSeries = (ASTSeriesData) pExpr;

            int pNMin = pSeries.minExponent();
            int pTruncate = pSeries.truncateOrder();
            int pDen = pSeries.puiseuxDenominator();
            int targetTruncate = n * pDen + 1;

            int actualMinExp = Integer.MAX_VALUE;
            for (int i = pNMin; i < pTruncate; i++) {
              if (!pSeries.coefficient(i).isZero()) {
                actualMinExp = i;
                break;
              }
            }
            if (actualMinExp != Integer.MAX_VALUE) {
              targetTruncate = Math.max(targetTruncate, actualMinExp + 1);
            }

            // If exponentiation didn't yield enough precision, step up the base order
            // If exponentiation didn't yield enough precision to satisfy the target
            // error bound, aggressively step up the base order and re-evaluate!
            int loopCount = 0;
            while (pTruncate < targetTruncate && loopCount++ < 4) {
              int b = p.denominator().toIntDefault();
              if (b != Config.INVALID_INT && b > 0) {
                int extraBaseTerms = (targetTruncate - pTruncate + b - 1) / b;
                // Overcompensate slightly to guarantee we breach the boundary for nested degrading
                // chains
                currentN += extraBaseTerms + 2;

                // NOTE: Use the correct overload below. If you are in the method with 'direction',
                // pass 'direction' instead of omitting it.
                ASTSeriesData series2 = seriesDataRecursive(base, x, x0, currentN, engine);

                if (series2 != null) {
                  IExpr pExpr2 = series2.powerSeries(p, engine);
                  if (pExpr2.isPresent() && pExpr2 instanceof ASTSeriesData) {
                    pSeries = (ASTSeriesData) pExpr2;
                    pNMin = pSeries.minExponent();
                    pTruncate = pSeries.truncateOrder();
                    pDen = pSeries.puiseuxDenominator();
                  }
                } else {
                  break;
                }
              } else {
                break;
              }
            }

            // Truncate the final series so it precisely matches the requested O(x^n) boundary
            if (pTruncate > targetTruncate) {
              int newNMin = Math.min(pNMin, targetTruncate);
              int capacity = targetTruncate - newNMin;
              IASTAppendable newList = F.ListAlloc(capacity < 0 ? 1 : capacity + 1);

              for (int i = newNMin; i < targetTruncate; i++) {
                newList.append(pSeries.coefficient(i));
              }
              return new ASTSeriesData(x, x0, newList, newNMin, targetTruncate, pDen);
            }

            return pSeries;
          } else {
            // Catch ComplexInfinity or other constant IExpr evaluations
            // Package it as a constant term in the series to propagate the pole safely
            int targetTruncate = Math.max(n, 0) + 1;
            ASTSeriesData constSeries = new ASTSeriesData(x, x0, 0, targetTruncate, 1);
            constSeries.setCoeff(0, pExpr);
            return constSeries;
          }
        }
      }
    }
    return null;
  }

  /**
   * Try to find a series with function {@link S#SeriesCoefficient}
   *
   * @param function the function which should be generated as a power series
   * @param x the variable
   * @param x0 the point to do the power expansion for
   * @param n the order of the expansion
   * @param denominator
   * @param varSet the variables of the function (including x)
   * @param engine the evaluation engine
   * @return the <code>SeriesCoefficient()</code> series or <code>null</code> if the function is not
   *         numeric w.r.t the varSet
   */
  private static ASTSeriesData seriesCoefficient(final IExpr function, IExpr x, IExpr x0,
      final int n, final int denominator, VariablesSet varSet, EvalEngine engine) {
    // Start probing from negative indices to capture Laurent series poles correctly
    int startN = Math.min(n, -10);
    int maxProbe = Math.max(n, 0) + 10;
    int targetN = n;
    ISymbol power = F.Dummy("$$$n");
    IExpr temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, power)));

    // RELAXED CHECK: Only require that SeriesCoefficient successfully evaluated without returning
    // itself
    if (temp.isPresent() && temp.isFree(S.SeriesCoefficient, true)) {
      boolean foundNonZero = false;
      // FIX: Use maxProbe + denominator so setCoeff doesn't discard probed terms!
      ASTSeriesData ps = new ASTSeriesData(x, x0, startN, maxProbe + denominator, denominator);

      for (int i = startN; i <= Math.max(targetN, maxProbe); i++) {
        IExpr coeff = engine.evalQuiet(F.subst(temp, power, F.ZZ(i)));
        ps.setCoeff(i, coeff);
        if (!coeff.isZero()) {
          foundNonZero = true;
        }

        if (i >= targetN && (foundNonZero || i == maxProbe)) {
          targetN = i;
          break;
        }
      }

      if (!foundNonZero) {
        targetN = n;
      }

      // Re-bound the series safely
      int finalNMin = ps.coefficientMap.isEmpty() ? startN : ps.minExponent();
      ASTSeriesData result =
          new ASTSeriesData(x, x0, finalNMin, targetN + denominator, denominator);
      for (int i = finalNMin; i <= targetN; i++) {
        result.setCoeff(i, ps.coefficient(i));
      }
      return result;

    } else {
      boolean foundNonZero = false;
      boolean evaled = true;
      // Use maxProbe + denominator so setCoeff doesn't discard probed terms!
      ASTSeriesData ps = new ASTSeriesData(x, x0, startN, maxProbe + denominator, denominator);

      for (int i = startN; i <= Math.max(targetN, maxProbe); i++) {
        temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, F.ZZ(i))));

        // RELAXED CHECK
        if (temp.isPresent() && temp.isFree(S.SeriesCoefficient, true)) {
          ps.setCoeff(i, temp);
          if (!temp.isZero()) {
            foundNonZero = true;
          }

          if (i >= targetN && (foundNonZero || i == maxProbe)) {
            targetN = i;
            break;
          }
        } else {
          evaled = false;
          break;
        }
      }
      if (evaled) {
        if (!foundNonZero) {
          targetN = n;
        }
        int finalNMin = ps.coefficientMap.isEmpty() ? startN : ps.minExponent();
        ASTSeriesData result =
            new ASTSeriesData(x, x0, finalNMin, targetN + denominator, denominator);
        for (int i = finalNMin; i <= targetN; i++) {
          result.setCoeff(i, ps.coefficient(i));
        }
        return result;
      }
    }
    return null;
  }

  /**
   * The core recursive factory method for generating a power series expansion of a mathematical
   * expression.
   * <p>
   * This method acts as the central router for evaluating the equivalent of {@code Series(function,
   * {x, x0, n})}. It recursively analyzes the Abstract Syntax Tree (AST) of the given expression
   * and delegates to specialized structural handlers (e.g., for addition, multiplication, and
   * exponentiation) to construct the mathematical series. *
   * <p>
   * Key mathematical enforcements handled at this master level include:
   * <ul>
   * <li><b>Expansion Point Shifts:</b> Safely intercepts and maps expansions around non-zero points
   * ({@code x0 != 0}) to generate accurate Taylor and Laurent series without polynomial offset
   * degradation.</li>
   * <li><b>Universal Error Clamping:</b> Enforces the dynamic target truncation boundary. It
   * guarantees that regardless of the intermediate fractional grid scaling (Puiseux denominators)
   * or recursive depth, the final series strictly adheres to the mathematically requested
   * {@code O((x-x0)^n)} bound.</li>
   * <li><b>Exact Polynomial Preservation:</b> Ensures that exact polynomial terms (which inherently
   * possess infinite precision) are never artificially swallowed or clipped by smaller Big-O
   * truncation requests.</li>
   * </ul>
   *
   * @param function the function to be expanded
   * @param x the expansion variable
   * @param x0 the point around which the series is expanded
   * @param n the requested mathematical order of the expansion (the Big-O term)
   * @param engine the evaluation engine
   * @return the generated {@code ASTSeriesData} representing the power series, or {@code null} if
   *         the series cannot be mathematically evaluated
   */
  @Nullable
  public static ASTSeriesData seriesDataRecursive(final IExpr function, IExpr x, IExpr x0,
      final int n, EvalEngine engine) {
    return seriesDataRecursive(function, x, x0, n, 0, engine);
  }

  @Nullable
  public static ASTSeriesData seriesDataRecursive(final IExpr function, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    final int denominator = 1;
    ASTSeriesData result = null;

    if (function.isFree(x) || function.equals(x)) {
      if (x0.isZero() || function.isFree(x)) {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        IASTAppendable rest = F.PlusAlloc(4);
        result = polynomialSeries(function, x, x0, n, coefficientMap, rest);
      } else {
        // function equals x, x0 != 0. Expand as x0 + (x-x0)
        ASTSeriesData series = new ASTSeriesData(x, x0, 0, n + 1, 1);
        series.setCoeff(0, x0);
        series.setCoeff(1, F.C1);
        result = series;
      }
    } else if (function.isPlus()) {
      result = plusSeriesDataRecursive((IAST) function, x, x0, n, direction, engine);
    } else if (function.isTimes()) {
      Optional<IExpr[]> numeratorDenominatorParts = AlgebraUtil.fractionalParts(function, false);
      if (numeratorDenominatorParts.isPresent()) {
        IExpr[] parts = numeratorDenominatorParts.get();
        // PROTECT TRANSCENDENTALS: Only use fast-path if both parts are purely polynomial
        if (x0.isZero() && parts[0].isPolynomial(x) && parts[1].isPolynomial(x)) {
          IExpr sd = Algebra.polynomialTaylorSeries(parts, x, x0, n, denominator);
          if (sd.isPresent()) {
            result = (ASTSeriesData) sd;
          }
        }
      }
      if (result == null) {
        result = timesSeriesDataRecursive((IAST) function, x, x0, n, direction, engine);
      }
    } else if (function.isPower()) {
      result = powerSeriesDataRecursive(function, x, x0, n, direction, engine);
    } else if (function.isAST(S.Gamma, 2)) {
      // Evaluate the limit of the inner argument to detect Gamma poles
      IExpr arg = function.first();
      IExpr limit = engine.evalQuiet(F.subst(arg, x, x0));
      if (limit.isIndeterminate() || limit.isDirectedInfinity()) {
        limit = engine.evalQuiet(F.Limit(arg, F.Rule(x, x0)));
      }

      // If expanding around 0 or a negative integer pole, rewrite to bypass the singularity!
      // Gamma(z) -> Gamma(z + |k| + 1) / (z * (z+1) * ... * (z + |k|))
      if (limit.isInteger() && (limit.isNegativeResult() || limit.isZero())) {
        int k = limit.toIntDefault();
        if (F.isPresent(k)) {
          int absK = Math.abs(k);

          IASTAppendable denom = F.TimesAlloc(absK + 1);
          for (int i = 0; i <= absK; i++) {
            denom.append(F.Plus(arg, F.ZZ(i)));
          }

          // Do NOT use the Dummy variable Series[] expansion here!
          IExpr rewritten = engine.evaluate(F.Divide(F.Gamma(F.Plus(arg, F.ZZ(absK + 1))), denom));
          // Use a higher recursion depth (n + 10) to ensure leading terms of
          // vanishing denominators are found, preventing spurious ComplexInfinity results
          // when inversion occurs on small-order series.
          result = seriesDataRecursive(rewritten, x, x0, n, direction, engine);
        }
      }

      // Allow non-pole Gamma compositions to be resolved via compositionSeries
      // instead of dropping to pure Taylor series derivatives, which explode for nested functions.
      if (result == null && !arg.isFree(x) && !arg.equals(x)) {
        ASTSeriesData compSeries = compositionSeries((IAST) function, x, x0, n, direction, engine);
        if (compSeries != null) {
          result = compSeries;
        }
      }
    } else if (function.isLog() && function.first().equals(x) && x0.isZero() && n >= 0) {
      result = new ASTSeriesData(x, x0, F.list(function), 0, n + 1, 1);
    } else if (function.isAST1() && !function.first().isFree(x) && !function.first().equals(x)) {
      // Fast-path composition trap for standard 1-arg numeric functions
      // (like Gamma, Sin, Exp) to prevent Taylor derivative explosions!
      IExpr head = function.head();
      if (head.isBuiltInSymbol()) {
        ASTSeriesData compSeries = compositionSeries((IAST) function, x, x0, n, direction, engine);
        if (compSeries != null) {
          result = compSeries;
        }
      }
    }

    if (result == null) {
      result = simpleSeries(function, x, x0, n, denominator, direction, engine);
    }

    if (result != null) {
      int pDen = result.puiseuxDenominator();
      int targetTruncate = n * pDen + 1;

      int actualMinExp = Integer.MAX_VALUE;
      for (int i = result.minExponent(); i < result.truncateOrder(); i++) {
        if (!result.coefficient(i).isZero()) {
          actualMinExp = i;
          break;
        }
      }
      if (actualMinExp != Integer.MAX_VALUE) {
        targetTruncate = Math.max(targetTruncate, actualMinExp + 1);
      }

      if (result.truncateOrder() > targetTruncate) {
        int pNMin = result.minExponent();
        int newNMin = Math.min(pNMin, targetTruncate);
        int capacity = targetTruncate - newNMin;
        IASTAppendable newList = F.ListAlloc(capacity < 0 ? 1 : capacity + 1);
        for (int i = newNMin; i < targetTruncate; i++) {
          newList.append(result.coefficient(i));
        }
        return new ASTSeriesData(result.expansionVariable(), result.expansionPoint(), newList,
            newNMin, targetTruncate, pDen);
      }
    }
    return result;
  }

  /**
   * Constructs an {@code ASTSeriesData} object directly from a map of exponents and coefficients. *
   * <p>
   * This method dynamically enforces mathematical precision rules for exact polynomials. Because
   * exact polynomial terms (e.g., {@code b * x^5}) have infinite precision, their mathematical
   * Big-O truncation bound must never be forced below their actual maximum degree. This method
   * scans the highest exponent present in the map and safely steps up the truncation bound
   * ({@code truncate = Math.max(n + 1, maxExponent + 1)}) to ensure leading polynomial terms are
   * perfectly preserved and not clipped by a smaller requested order {@code n}. *
   * <p>
   * Any mapped exponents that cannot resolve to standard integers are safely rejected and pushed
   * back into the {@code rest} accumulator for secondary transcendental evaluation.
   *
   * @param x the expansion variable
   * @param x0 the point around which to expand
   * @param n the requested mathematical order of the expansion
   * @param coefficientMap the map containing parsed exponent keys and coefficient values
   * @param rest an accumulator list to catch terms with non-integer exponents
   * @return the generated {@code ASTSeriesData}, or {@code null} if no valid integer-exponent terms
   *         were processed
   */
  private static ASTSeriesData seriesFromMap(IExpr x, IExpr x0, final int n,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    int maxExponent = Integer.MIN_VALUE;
    for (IExpr exp : coefficientMap.keySet()) {
      int exponent = exp.toIntDefault();
      if (F.isPresent(exponent) && exponent > maxExponent) {
        maxExponent = exponent;
      }
    }

    int truncate = n + 1;
    if (maxExponent != Integer.MIN_VALUE) {
      truncate = Math.max(truncate, maxExponent + 1);
    }

    ASTSeriesData series = new ASTSeriesData(x, x0, 0, truncate, 1);
    boolean evaled = false;
    for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
      IExpr coefficient = entry.getValue();
      if (coefficient.isZero()) {
        continue;
      }
      IExpr exp = entry.getKey();
      int exponent = exp.toIntDefault();
      if (F.isNotPresent(exponent)) {
        rest.append(F.Times(coefficient, F.Power(x, exp)));
      } else {
        series.setCoeff(exponent, coefficient);
        evaled = true;
      }
    }
    if (evaled) {
      return series;
    }
    return null;
  }

  public static ASTSeriesData simpleSeries(final IExpr function, IExpr x, IExpr x0, final int n,
      final int denominator, EvalEngine engine) {
    return simpleSeries(function, x, x0, n, denominator, 0, engine);
  }

  public static ASTSeriesData simpleSeries(final IExpr function, IExpr x, IExpr x0, final int n,
      final int denominator, final int direction, EvalEngine engine) {
    VariablesSet varSet = new VariablesSet(function);
    varSet.add(x);
    varSet.addVarList(x0);
    ASTSeriesData sd = seriesCoefficient(function, x, x0, n, denominator, varSet, engine);
    if (sd != null) {
      return sd;
    }
    return taylorSeries(function, x, x0, n, denominator, varSet, direction, engine);
  }

  private static ASTSeriesData taylorSeries(final IExpr function, IExpr x, IExpr x0, final int n,
      int denominator, VariablesSet varSet, final int direction, EvalEngine engine) {
    int maxProbe = Math.max(n, 0) + 10;
    int targetN = n;
    java.util.List<IExpr> coeffs = new java.util.ArrayList<>();
    IExpr derivedFunction = function;
    boolean foundNonZero = false;

    for (int i = 0; i <= Math.max(targetN, maxProbe); i++) {
      IExpr functionPart = engine.evalQuiet(F.subst(derivedFunction, x, x0));

      // Attempt limit if substitution fails, yields a pole, or yields unevaluated complex forms
      if (!functionPart.isSpecialsFree()) {
        IExpr dirExpr = direction == 0 ? S.Reals : F.ZZ(direction);
        IExpr limitPart =
            engine.evalQuiet(F.Limit(derivedFunction, F.Rule(x, x0), F.Rule(S.Direction, dirExpr)));

        // Ensure the limit resolved the ambiguity cleanly
        if (limitPart.isSpecialsFree()) {
          functionPart = limitPart;
        } else {
          return null;
        }
      }

      IExpr coefficient =
          engine.evalQuiet(F.Times(F.Power(AbstractIntegerSym.factorial(i), F.CN1), functionPart));

      if (coefficient.isIndeterminate() || !coefficient.isFree(S.DirectedInfinity)
          || !coefficient.isFree(S.Indeterminate)) {
        return null;
      }

      coeffs.add(coefficient);
      if (!coefficient.isZero()) {
        foundNonZero = true;
      }

      if (i >= targetN && (foundNonZero || i == maxProbe)) {
        targetN = i;
        break;
      }

      derivedFunction = engine.evalQuiet(F.D(derivedFunction, x));
    }

    if (!foundNonZero) {
      targetN = n;
    }

    ASTSeriesData ps = new ASTSeriesData(x, x0, 0, targetN + denominator, denominator);
    for (int i = 0; i <= targetN && i < coeffs.size(); i++) {
      ps.setCoeff(i, coeffs.get(i));
    }
    return ps;
  }

  private static ASTSeriesData timesSeriesDataRecursive(IAST timesAST, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    IASTAppendable rest = F.TimesAlloc(4);
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();

    if (x0.isZero()) {
      coefficientMap = ExprPolynomialRing.createTimes(timesAST, x, coefficientMap, rest);
    } else {
      rest.appendArgs(timesAST);
    }

    int shift = 0;
    IExpr coefficient = F.C1;
    if (coefficientMap.size() == 1) {
      shift = coefficientMap.keySet().iterator().next().toIntDefault(0);
      if (shift != 0) {
        coefficient = coefficientMap.values().iterator().next();
        timesAST = rest;
      }
    }

    if (rest.size() <= 1) {
      return ASTSeriesData.seriesFromMap(x, x0, n, coefficientMap, rest);
    }

    int ni = n + Math.abs(shift);
    java.util.List<ASTSeriesData> factors = new java.util.ArrayList<>();
    int maxDen = 1;
    for (int i = 1; i < timesAST.size(); i++) {
      ASTSeriesData arg = seriesDataRecursive(timesAST.get(i), x, x0, ni, direction, engine);
      if (arg == null) {
        return null;
      }
      factors.add(arg);
      maxDen = ArithmeticUtils.lcm(maxDen, arg.puiseuxDenominator());
    }

    int totalNMin = 0;
    for (ASTSeriesData factor : factors) {
      totalNMin += factor.minExponent() * (maxDen / factor.puiseuxDenominator());
    }

    int targetTruncate = ni * maxDen + 1;
    java.util.List<ASTSeriesData> revalFactors = new java.util.ArrayList<>();

    for (int i = 0; i < factors.size(); i++) {
      ASTSeriesData arg = factors.get(i);
      int scale = maxDen / arg.puiseuxDenominator();
      int otherNMins = totalNMin - (arg.minExponent() * scale);

      int neededScaledTruncate = targetTruncate - otherNMins;
      int currentScaledTruncate = arg.truncateOrder() * scale;

      if (currentScaledTruncate < neededScaledTruncate) {
        int missing = neededScaledTruncate - currentScaledTruncate;
        int extraNInt = (missing + maxDen - 1) / maxDen;

        ASTSeriesData newArg =
            seriesDataRecursive(timesAST.get(i + 1), x, x0, ni + extraNInt, direction, engine);
        if (newArg != null) {
          revalFactors.add(newArg);
        } else {
          revalFactors.add(arg);
        }
      } else {
        revalFactors.add(arg);
      }
    }

    ASTSeriesData series = revalFactors.get(0);
    for (int i = 1; i < revalFactors.size(); i++) {
      series = series.timesPS(revalFactors.get(i));
    }

    if (shift != 0) {
      series = series.shift(shift, coefficient, series.truncateOrder() + shift);
    }

    return series;
  }

  /** A map of the truncated power series coefficients <code>value != 0</code> */
  OpenIntToIExprHashMap<IExpr> coefficientMap;

  /**
   * Truncation of computations. Represents the truncation bound or the order of the Big-O error
   * term.
   */
  private int truncateOrder;

  /** The expansion variable symbol of this series. */
  private IExpr expansionVariable;

  /** The point <code>expansionVariable = expansionPoint</code> of this series. */
  private IExpr expansionPoint;

  /**
   * The minimum exponent (inclusive) used in <code>coefficientValues</code>, where the coefficient
   * is not 0.
   */
  private int minExponent;

  /**
   * The maximum bound (exclusive) used in <code>coefficientValues</code>, where the coefficient is
   * not 0.
   */
  private int exponentBound;

  /** The common denominator for fractional exponents (used in Puiseux series) */
  private int puiseuxDenominator;

  public ASTSeriesData() {
    super();
    truncateOrder = 0;
    puiseuxDenominator = 1;
    // When Externalizable objects are deserialized, they first need to be constructed by invoking
    // the void constructor. Since this class does not have one, serialization and deserialization
    // will fail at runtime.
  }

  public ASTSeriesData(IExpr x, IExpr x0, IAST coefficients, final int nMin, final int truncate,
      final int denominator) {
    this(x, x0, nMin, nMin, truncate, denominator, new OpenIntToIExprHashMap<IExpr>());
    final int size = coefficients.size();
    for (int i = 0; i < size - 1; i++) {
      int index = nMin + i;
      if (index >= truncate) {
        break;
      }
      setCoeff(index, coefficients.get(i + 1));
    }
  }

  public ASTSeriesData(IExpr x, IExpr x0, int nMin, int truncate, int denominator) {
    this(x, x0, nMin, nMin, truncate, denominator, new OpenIntToIExprHashMap<IExpr>());
  }

  public ASTSeriesData(IExpr x, IExpr x0, int nMin, int nMax, int truncate, int denominator,
      OpenIntToIExprHashMap<IExpr> vals) {
    super();
    this.coefficientMap = vals;
    this.expansionVariable = x;
    this.expansionPoint = x0;
    this.minExponent = nMin;
    this.exponentBound = nMax;
    this.truncateOrder = truncate;

    // Allow negative truncation orders for Laurent series
    // Safely align empty series boundaries if truncate falls below nMin
    if (this.coefficientMap.isEmpty() && this.minExponent > this.truncateOrder) {
      this.minExponent = this.truncateOrder;
      this.exponentBound = this.truncateOrder;
    }

    this.puiseuxDenominator = denominator;
  }


  @Override
  public final IExpr arg1() {
    return expansionVariable;
  }

  @Override
  public final IExpr arg2() {
    return expansionPoint;
  }

  @Override
  public final IAST arg3() {
    int capacity = exponentBound - minExponent;
    if (capacity <= 0) {
      capacity = 4;
    }
    return F.mapRange(minExponent, exponentBound, i -> coefficient(i));
  }

  @Override
  public final IInteger arg4() {
    return F.ZZ(minExponent);
  }

  @Override
  public final IInteger arg5() {
    return F.ZZ(truncateOrder);
  }

  @Override
  public final IInteger arg6() {
    return F.ZZ(puiseuxDenominator);
  }

  @Override
  public int argSize() {
    return 6;
  }

  /**
   * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity
   * as this {@code HMArrayList}.
   *
   * @return a shallow copy of this {@code ArrayList}
   * @see java.lang.Cloneable
   */
  @Override
  public IAST clone() {
    return new ASTSeriesData(expansionVariable, expansionPoint, minExponent, exponentBound,
        truncateOrder, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>(coefficientMap));
  }

  /**
   * Get the coefficient for <code>(x-x0)^k</code>.
   *
   * @param k the coefficient index
   */
  public IExpr coefficient(int k) {
    if (k < minExponent || k >= exponentBound) {
      return F.C0;
    }
    IExpr coefficient = coefficientMap.get(k);
    if (coefficient == null) {
      return F.C0;
    }
    return coefficient;
  }

  public IAST coefficientList() {
    if (expansionPoint.isZero()) {
      // If x0 is zero, we can return the coefficients directly
      // as a list of the coefficients of the polynomial.

      // Safeguard against negative capacities for Laurent series bounds
      int capacity = truncateOrder > 0 ? truncateOrder : 4;
      IASTAppendable coefficientList = F.ListAlloc(capacity);

      for (int i = 0; i < truncateOrder; i++) {
        coefficientList.append(coefficient(i));
      }
      for (int i = coefficientList.argSize(); i > 2; i--) {
        if (coefficientList.get(i).isZero()) {
          coefficientList.remove(i);
        } else {
          break;
        }
      }
      return coefficientList;
    }
    IAST listOfVariables = F.List(expansionVariable);
    IExpr polynomialExpr = normal(false);
    if (polynomialExpr.isAST() && !polynomialExpr.isFree(expansionVariable, true)) {
      polynomialExpr = F.evalExpandAll(polynomialExpr);
    }
    return coefficientList(polynomialExpr, listOfVariables);
  }

  @Override
  public int compareTo(final IExpr rhsExpr) {
    if (rhsExpr instanceof ASTSeriesData) {
      ASTSeriesData rhs = (ASTSeriesData) rhsExpr;
      int cp = expansionVariable.compareTo(rhs.expansionVariable);
      if (cp != 0) {
        return cp;
      }
      cp = expansionPoint.compareTo(rhs.expansionPoint);
      if (cp != 0) {
        return cp;
      }
      cp = exponentBound - rhs.exponentBound;
      if (cp != 0) {
        if (cp < 0) {
          return -1;
        }
        return 1;
      }
      cp = minExponent - rhs.minExponent;
      if (cp != 0) {
        if (cp < 0) {
          return -1;
        }
        return 1;
      }
      cp = puiseuxDenominator - rhs.puiseuxDenominator;
      if (cp != 0) {
        if (cp < 0) {
          return -1;
        }
        return 1;
      }
      return super.compareTo(rhsExpr);
    }
    return IExpr.compareHierarchy(this, rhsExpr);
  }

  /**
   *
   *
   * <pre>
   * series1.compose(series2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * substitute <code>series2</code> into <code>series1</code>
   *
   * </p>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)
   * x^2+3*x^3+O(x)^4
   * </pre>
   *
   * @param series2
   * @return the composed series
   */
  public ASTSeriesData compose(ASTSeriesData series2) {
    IExpr coeff0 = series2.coefficient(0);
    if (!coeff0.equals(expansionPoint)) {
      // `1`.
      Errors.printMessage(S.SeriesData, "error", F.List("Constant " + coeff0 + " of series "
          + series2 + " unequals point " + expansionPoint + " of series " + this));
      return null;
    }
    ASTSeriesData series = new ASTSeriesData(series2.expansionVariable, series2.expansionPoint, 0,
        series2.truncateOrder, series2.puiseuxDenominator);
    ASTSeriesData s;
    ASTSeriesData x0Term;
    if (expansionPoint.isZero()) {
      x0Term = series2;
    } else {
      x0Term = series2.subtract(expansionPoint);
    }
    for (int n = minExponent; n < exponentBound; n++) {
      IExpr temp = coefficient(n);
      if (!temp.isZero()) {
        s = x0Term.powerSeries(n);
        s = s.times(temp);
        series = series.plusPS(s);
      }
    }
    return series;
  }

  /**
   * Computes the general case Lagrange inversion coefficient for higher orders.
   * 
   * @param n The power of the term
   * @return The coefficient for the nth term in the inverse series
   */
  private IExpr computeGeneralLagrangeCoefficient(int n, ASTSeriesData fDivX, EvalEngine engine) {
    // Compute (f(x)/x)^(-n)
    ASTSeriesData fDivXPowerNeg = fDivX.powerSeries(-n);

    // Extract the coefficient of x^(n-1) and divide by n
    IExpr lagrangeCoefficient =
        engine.evaluate(F.Together(F.ExpandAll(fDivXPowerNeg.coefficient(n - 1).divide(F.ZZ(n)))));
    return lagrangeCoefficient;
  }

  private IExpr computeLagrangeCoefficient(int n, ASTSeriesData fDivXAST, EvalEngine engine) {
    switch (n) {
      case 1:
        return getCoeffSafe(1).inverse();
      case 2: {
        IExpr a1 = getCoeffSafe(1);
        IExpr a2 = getCoeffSafe(2);
        return engine.evaluate(F.Divide(a2.negate(), F.Power(a1, F.C3)));
      }
      case 3: {
        IExpr a1 = getCoeffSafe(1);
        IExpr a2 = getCoeffSafe(2);
        IExpr a3 = getCoeffSafe(3);
        IExpr numerator = F.Plus(F.Times(F.C2, F.Sqr(a2)), F.Times(F.CN1, a1, a3));
        return engine.evaluate(F.Divide(numerator, F.Power(a1, F.C5)));
      }
      case 4: {
        IExpr a1 = getCoeffSafe(1);
        IExpr a2 = getCoeffSafe(2);
        IExpr a3 = getCoeffSafe(3);
        IExpr a4 = getCoeffSafe(4);
        IExpr numerator = F.Plus(F.Times(F.CN5, F.Power(a2, F.C3)), F.Times(F.C5, a1, a2, a3),
            F.Times(F.CN1, F.Sqr(a1), a4));
        return engine.evaluate(F.Divide(numerator, F.Power(a1, F.C7)));
      }
      case 5: {
        IExpr a1 = getCoeffSafe(1);
        IExpr a2 = getCoeffSafe(2);
        IExpr a3 = getCoeffSafe(3);
        IExpr a4 = getCoeffSafe(4);
        IExpr a5 = getCoeffSafe(5);
        IExpr numerator = F.Plus(F.Times(F.ZZ(14L), F.Power(a2, F.C4)),
            F.Times(F.ZZ(-21L), a1, F.Sqr(a2), a3), F.Times(F.C3, F.Sqr(a1), F.Sqr(a3)),
            F.Times(F.C6, F.Sqr(a1), a2, a4), F.Times(F.CN1, F.Power(a1, F.C3), a5));
        return engine.evaluate(F.Divide(numerator, F.Power(a1, F.C9)));
      }
    }
    return computeGeneralLagrangeCoefficient(n, fDivXAST, engine);
  }

  /** {@inheritDoc} */
  @Override
  public ASTSeriesData copy() {
    return new ASTSeriesData(expansionVariable, expansionPoint, minExponent, exponentBound,
        truncateOrder, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>(coefficientMap));
  }

  @Override
  public IASTAppendable copyAppendable() {
    return F.NIL;
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copyAppendable();
  }

  /**
   * Differentiation of a power series.
   *
   * <p>
   * See
   * <a href="https://en.wikipedia.org/wiki/Power_series#Differentiation_and_integration">Wikipedia:
   * Power series - Differentiation and integration</a>
   *
   * @param x the variable
   */
  public ASTSeriesData derive(IExpr x) {
    if (this.expansionVariable.equals(x)) {
      if (isProbableZero()) {
        return this;
      }
      if (truncateOrder > 0) {
        ASTSeriesData series = new ASTSeriesData(x, expansionPoint, minExponent, minExponent,
            truncateOrder - 1, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>());
        if (minExponent >= 0) {
          if (minExponent > 0) {
            series.setCoeff(minExponent - 1,
                this.coefficient(minExponent + 1).times(F.ZZ(minExponent + 1)));
          }
          for (int i = minExponent; i < exponentBound - 1; i++) {
            series.setCoeff(i, this.coefficient(i + 1).times(F.ZZ(i + 1)));
          }
          return series;
        }
      }
    }
    return null;
  }

  public ASTSeriesData dividePS(ASTSeriesData ps) {
    if (ps.isInvertible()) {
      ASTSeriesData inverse = timesPS(ps.inverse());
      if (inverse != null) {
        return inverse;
      }
    }
    int m = truncateOrder();
    int n = ps.truncateOrder();
    if (m < n) {
      return new ASTSeriesData(F.C0, expansionPoint, 0, 1, 1);
      // return ring.getZERO();
    }
    if (!ps.coefficient(n).isUnit()) {
      throw new ArithmeticException(
          "division by non unit coefficient " + ps.coefficient(n) + ", n = " + n);
    }
    // now m >= n
    ASTSeriesData st, sps, q, sq;
    if (m == 0) {
      st = this;
    } else {
      st = this.shift(-m);
    }
    if (n == 0) {
      sps = ps;
    } else {
      sps = ps.shift(-n);
    }
    q = st.timesPS(sps.inverse());
    if (m == n) {
      sq = q;
    } else {
      sq = q.shift(m - n);
    }
    return sq;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ASTSeriesData) {
      if (obj == this) {
        return true;
      }
      ASTSeriesData that = (ASTSeriesData) obj;
      if (!expansionVariable.equals(that.expansionVariable)) {
        return false;
      }
      if (!expansionPoint.equals(that.expansionPoint)) {
        return false;
      }
      if (minExponent != that.minExponent) {
        return false;
      }
      if (puiseuxDenominator != that.puiseuxDenominator) {
        return false;
      }
      if (truncateOrder != that.truncateOrder) {
        return false;
      }
      if (coefficientMap.equals(that.coefficientMap)) {
        return true;
      }
      for (int i = minExponent; i < exponentBound; i++) {
        if (!coefficient(i).equals(that.coefficient(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public IExpr evalEvaluate(EvalEngine engine) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    // if ((getEvalFlags() & IAST.DEFER_AST) == IAST.DEFER_AST) {
    // return F.NIL;
    // }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    IAST seriesData = toSeriesData();
    return seriesData.fullFormString();
  }

  // private IExpr inverseRecursion(int n) {
  // if (n == 0) {
  // // a1^(-1)
  // return coeff(1).inverse();
  // }
  // IExpr dn = F.C0;
  // for (int k = 0; k < n - 1; k++) {
  // dn = dn.plus(inverseRecursion(k).divide(coeff(1)).times(coeff(n - k)));
  // }
  // return dn.negate();
  // }

  @Override
  public IExpr get(int location) {
    if (location >= 0 && location <= 7) {
      switch (location) {
        case 0:
          return head();
        case 1:
          // x
          return arg1();
        case 2:
          // x0
          return arg2();
        case 3:
          // Coefficients
          return arg3();
        case 4:
          // nMin
          return arg4();
        case 5:
          // power
          return arg5();
        case 6:
          // denominator
          return F.ZZ(puiseuxDenominator);
      }
    }
    throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 1");
  }

  /**
   * Safely retrieves a coefficient, guaranteeing F.C0 is returned
   */
  private IExpr getCoeffSafe(int index) {
    IExpr c = coefficient(index);
    return c == null ? F.C0 : c;
  }

  public int puiseuxDenominator() {
    return puiseuxDenominator;
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    IAST result = normal(false);
    return result.getItems(items, length, offset);
  }

  public int exponentBound() {
    return exponentBound;
  }

  public int minExponent() {
    return minExponent;
  }

  public IExpr expansionVariable() {
    return expansionVariable;
  }

  public IExpr expansionPoint() {
    return expansionPoint;
  }

  @Override
  public int hashCode() {
    if (hashValue == 0 && expansionPoint != null) {
      if (coefficientMap != null) {
        hashValue = expansionPoint.hashCode() + truncateOrder * coefficientMap.hashCode();
      } else {
        hashValue = expansionPoint.hashCode() + truncateOrder;
      }
    }
    return hashValue;
  }

  @Override
  public IExpr head() {
    return S.SeriesData;
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
    return SERIESID;
  }

  /**
   * Integration of a power series.
   *
   * <p>
   * See
   * <a href="https://en.wikipedia.org/wiki/Power_series#Differentiation_and_integration">Wikipedia:
   * Power series - Differentiation and integration</a>
   *
   * @param x the variable
   */
  public ASTSeriesData integrate(IExpr x) {
    if (this.expansionVariable.equals(x)) {
      if (isProbableZero()) {
        return this;
      }
      if (truncateOrder > 0) {
        ASTSeriesData series = new ASTSeriesData(x, expansionPoint, minExponent, minExponent,
            truncateOrder + 1, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>());
        if (minExponent + 1 > 0) {
          for (int i = minExponent + 1; i <= exponentBound; i++) {
            series.setCoeff(i, this.coefficient(i - 1).times(F.QQ(1, i)));
          }
          return series;
        }
      }
    }
    return null;
  }

  // private ASTSeriesData internalTimes(ASTSeriesData b, int minSize, int newPower,
  // int newDenominator) {
  // ASTSeriesData series = new ASTSeriesData(expansionVariable, expansionPoint,
  // minExponent + b.minExponent, newPower, newDenominator);
  // int start = series.minExponent;
  // int end = exponentBound + b.exponentBound + 1;
  // for (int n = start; n < end; n++) {
  // if (n - start >= series.truncateOrder) {
  // continue;
  // }
  // final int iMax = n;
  // IASTAppendable sum = F.mapRange(S.Plus, minSize, iMax + 1,
  // i -> this.coefficient(i).times(b.coefficient(iMax - i)));
  //
  // // for (int i = minSize; i <= n; i++) {
  // // sum.append(this.coefficient(i).times(b.coefficient(n - i)));
  // // }
  //
  // IExpr value = F.eval(sum);
  // if (value.isZero()) {
  // continue;
  // }
  // series.setCoeff(n, value);
  // }
  // return series;
  // }

  /**
   *
   *
   * <pre>
   * series.inverse()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the inverse series.
   *
   * </p>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; InverseSeries(Series(Sin(x), {x, 0, 7}))
   * x+x^3/6+3/40*x^5+5/112*x^7+O(x)^8
   * </pre>
   *
   * @return the inverse series if possible
   */
  @Override
  public ASTSeriesData inverse() {
    // Find the true leading term (could be negative in a Laurent series)
    int actualNMin = minExponent;
    while (actualNMin < exponentBound && coefficient(actualNMin).isZero()) {
      actualNMin++;
    }

    if (actualNMin >= exponentBound) {
      // Series is effectively 0
      throw new ArgumentTypeException("infy", F.List(F.C0));
    }

    IExpr leadingCoeff = coefficient(actualNMin);
    IExpr d = leadingCoeff.inverse();

    // The new lowest power will be the negative of the old lowest power
    int newNMin = -actualNMin;
    int newTruncate = truncateOrder - 2 * actualNMin;

    ASTSeriesData result = new ASTSeriesData(expansionVariable, expansionPoint, newNMin,
        newTruncate, puiseuxDenominator);

    for (int i = 0; i < truncateOrder - actualNMin; i++) {
      if (i == 0) {
        result.setCoeff(newNMin, d);
      } else {
        IExpr c = F.C0;
        for (int k = 0; k < i; k++) {
          IExpr coeffK = result.coefficient(newNMin + k);
          IExpr m = coeffK.multiply(coefficient(actualNMin + i - k));
          c = c.sum(m);
        }
        c = c.multiply(d.negate());
        result.setCoeff(newNMin + i, c);
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST0() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST1() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST2() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST3() {
    return false;
  }

  public boolean isInvertible() {
    return !coefficient(0).isZero();
  }

  @Override
  public boolean isOrder() {
    for (int i = minExponent; i < exponentBound; i++) {
      IExpr expr = coefficient(i);
      if (!expr.isZero()) {
        return false;
      }
    }
    return true;
  }

  public boolean isProbableOne() {
    if (!coefficient(0).isOne()) {
      return false;
    }
    for (int i = minExponent; i < exponentBound; i++) {
      if (!coefficient(i).isZero()) {
        return false;
      }
    }
    return true;
  }

  public boolean isProbableZero() {
    if (coefficientMap.size() == 0) {
      return true;
    }
    for (int i = minExponent; i < exponentBound; i++) {
      if (!coefficient(i).isZero()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ASTSeriesData negate() {
    ASTSeriesData series = copy();
    for (int i = minExponent; i < exponentBound; i++) {
      series.setCoeff(i, coefficient(i).negate());
    }
    return series;
  }

  /**
   *
   *
   * <pre>
   * series.normal()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts a <code>series</code> expression into a standard expression.
   *
   * </p>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
   * 1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
   * </pre>
   *
   * @return the standard expression generated from this series <code>Plus(....)</code>.
   */
  @Override
  public IASTMutable normal(boolean nilIfUnevaluated) {
    IExpr x = expansionVariable();
    IExpr x0 = expansionPoint();
    int nMin = minExponent();
    int nMax = exponentBound();
    int denominator = puiseuxDenominator();
    int size = nMax - nMin;
    if (size < 4) {
      size = 4;
    }
    IExpr base;
    if (x0.isInfinity() || x0.isNegativeInfinity()) {
      // For expansions at Infinity or -Infinity, the base is always 1/x
      base = F.Power(x, F.CN1);
    } else if (x0.isZero()) {
      base = x;
    } else {
      base = F.Subtract(x, x0);
    }
    IASTAppendable result = F.PlusAlloc(size);
    for (int i = nMin; i < nMax; i++) {
      IExpr expr = coefficient(i);
      if (!expr.isZero()) {
        INumber exp;
        if (denominator == 1) {
          exp = F.ZZ(i);
        } else {
          exp = F.QQ(i, denominator).normalize();
        }
        IExpr pow = base.power(exp);
        result.append(F.Times(expr, pow));
      }
    }
    return result;
  }

  public int truncateOrder() {
    return truncateOrder;
  }

  @Override
  public ASTSeriesData plus(IExpr b) {
    if (b instanceof ASTSeriesData) {
      return plusPS((ASTSeriesData) b);
    }
    if (b.isZero()) {
      return this;
    }
    IExpr value = F.eval(coefficient(0).plus(b));
    ASTSeriesData series = copy();
    if (value.isZero()) {
      series.setZero(0);
    } else {
      series.setCoeff(0, value);
    }
    return series;
  }

  /**
   * Add two power series.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Power_series#Addition_and_subtraction">Wikipedia:
   * Power series - Addition and subtraction</a>
   *
   * @param that the other power series
   */
  public ASTSeriesData plusPS(ASTSeriesData that) {
    int newDenominator = ArithmeticUtils.lcm(puiseuxDenominator, that.puiseuxDenominator);
    int scaleA = newDenominator / puiseuxDenominator;
    int scaleB = newDenominator / that.puiseuxDenominator;

    int n1 = minExponent * scaleA;
    int n2 = that.minExponent * scaleB;
    int minSize = Math.min(n1, n2);

    int t1 = truncateOrder * scaleA;
    int t2 = that.truncateOrder * scaleB;
    int newPower = Math.min(t1, t2);

    ASTSeriesData series =
        new ASTSeriesData(expansionVariable, expansionPoint, minSize, newPower, newDenominator);

    for (int i = minSize; i < newPower; i++) {
      IExpr cA = F.C0;
      if (i % scaleA == 0) {
        cA = this.coefficient(i / scaleA);
      }
      IExpr cB = F.C0;
      if (i % scaleB == 0) {
        cB = that.coefficient(i / scaleB);
      }
      IExpr sum = F.eval(F.Plus(cA, cB));
      if (!sum.isZero()) {
        series.setCoeff(i, sum);
      }
    }
    return series;
  }

  /**
   * Compute the fractional power of a series using the J.C.P. Miller formula.
   *
   * @param p the rational power
   * @param engine the evaluation engine
   * @return the resulting series, or F.NIL if it cannot be computed
   */
  public IExpr powerSeries(IRational p, EvalEngine engine) {
    if (p.isZero()) {
      ASTSeriesData series = new ASTSeriesData(expansionVariable, expansionPoint, 0, truncateOrder,
          puiseuxDenominator);
      series.setCoeff(0, F.C1);
      return series;
    }
    if (p.isOne()) {
      return this;
    }

    int actualNMin = minExponent;
    while (actualNMin < truncateOrder) {
      IExpr c = coefficient(actualNMin);
      if (c != null && !c.isZero())
        break;
      actualNMin++;
    }

    if (actualNMin >= truncateOrder) {
      if (p.isPositive()) {
        return new ASTSeriesData(expansionVariable, expansionPoint, 0, truncateOrder,
            puiseuxDenominator);
      } else {
        // Correctly handle negative power on a zero series: 1/0 -> ComplexInfinity
        Errors.printMessage(S.Power, "infy", F.List(F.C0), engine);
        return S.ComplexInfinity;
      }
    }

    IExpr cMin = coefficient(actualNMin);
    int a = p.numerator().toIntDefault();
    int b = p.denominator().toIntDefault();
    if (!F.isPresent(a) || !F.isPresent(b)) {
      return F.NIL;
    }

    int newDenominator = puiseuxDenominator * b;
    int newNMin = actualNMin * a;
    int newTruncate = actualNMin * a + (truncateOrder - actualNMin) * b;

    ASTSeriesData result =
        new ASTSeriesData(expansionVariable, expansionPoint, newNMin, newTruncate, newDenominator);

    int numTerms = truncateOrder - actualNMin;
    if (numTerms <= 0) {
      return result;
    }

    IExpr[] c = new IExpr[numTerms];
    c[0] = F.C1;

    for (int k = 1; k < numTerms; k++) {
      IExpr ck = F.C0;
      for (int j = 1; j <= k; j++) {
        // Safe coefficient retrieval to prevent NPE and boundary overruns
        IExpr coeff = getCoeffSafe(actualNMin + j);
        if (!coeff.isZero()) {
          IExpr bj = engine.evaluate(F.Divide(coeff, cMin));
          IExpr termWeight = engine.evaluate(F.Subtract(F.Times(p, F.ZZ(j)), F.ZZ(k - j)));
          IExpr term = engine.evaluate(F.Times(termWeight, bj, c[k - j]));
          ck = engine.evaluate(F.Plus(ck, term));
        }
      }
      c[k] = engine.evaluate(F.Divide(ck, F.ZZ(k)));
    }

    IExpr cMinPower = engine.evaluate(F.Power(cMin, p));
    for (int k = 0; k < numTerms; k++) {
      if (c[k] == null || c[k].isZero()) {
        continue;
      }
      int newIndex = newNMin + k * b;
      result.setCoeff(newIndex, engine.evaluate(F.Times(cMinPower, c[k])));
    }

    return result;
  }

  public ASTSeriesData powerSeries(final long n) {
    return (ASTSeriesData) powerSeries(F.ZZ(n), EvalEngine.get());
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();

    int size = objectInput.readInt();
    IExpr[] array = new IExpr[size];
    for (int i = 0; i < size; i++) {
      array[i] = (IExpr) objectInput.readObject();
    }
    expansionVariable = array[1];
    expansionPoint = array[2];
    minExponent = array[4].toIntDefault(0);
    truncateOrder = array[5].toIntDefault(0);
    puiseuxDenominator = array[6].toIntDefault(0);
    coefficientMap = new OpenIntToIExprHashMap<IExpr>();
    IAST list = (IAST) array[3];
    int listSize = list.size();
    for (int i = 1; i < listSize; i++) {
      setCoeff(i + minExponent - 1, list.get(i));
    }
  }

  @Override
  public IAST removeO() {
    return normal(false);
  }

  /**
   * Computes the inverse series (reversion) of this series.
   * 
   * <p>
   * Implements the reversion of a power series following the Lagrange inversion formula. For a
   * series f(x) = a₀ + a₁(x-x₀) + a₂(x-x₀)² + ..., the reversion gives a series g such that g(f(x))
   * = x.
   * 
   * <p>
   * The method handles different cases:
   * <ul>
   * <li>When a₀ = x₀: Standard Lagrange inversion formula</li>
   * <li>When a₀ ≠ x₀: Reversion with shift to handle constant term</li>
   * </ul>
   * 
   * @param variable the variable to use in the resulting series
   *
   * @return the inverse series, or null if the inverse cannot be computed
   */
  /**
   * Computes the inverse series (reversion) of this series. Handles standard Taylor series, Puiseux
   * series (fractional powers), and series missing a linear term (where the first non-zero
   * coefficient is k > 1).
   *
   * @param variable the variable to use in the resulting series
   * @param engine the evaluation engine
   * @return the inverse series, or F.NIL if the inverse cannot be computed
   */
  public IExpr reversion(IExpr variable, EvalEngine engine) {
    IExpr constantTerm = getCoeffSafe(0);

    int k = 1;
    while (k < truncateOrder) {
      IExpr c = coefficient(k);
      if (c != null && !c.isZero())
        break;
      k++;
    }

    if (k >= truncateOrder)
      return F.NIL;

    int availableTerms = truncateOrder - k;
    if (availableTerms <= 0)
      return F.NIL;

    int gTruncate = availableTerms + 1;

    ASTSeriesData hSeries = new ASTSeriesData(expansionVariable, F.C0, k, truncateOrder, 1);
    for (int i = k; i < truncateOrder; i++) {
      hSeries.setCoeff(i, getCoeffSafe(i));
    }

    ASTSeriesData gSeries;
    if (k == 1) {
      gSeries = hSeries;
    } else {
      IExpr gExpr = hSeries.powerSeries(F.QQ(1, k), engine);
      if (!gExpr.isPresent() || !(gExpr instanceof ASTSeriesData)) {
        return F.NIL;
      }
      ASTSeriesData gPower = (ASTSeriesData) gExpr;

      gSeries = new ASTSeriesData(expansionVariable, F.C0, 1, gTruncate, 1);
      for (int i = 1; i < gTruncate; i++) {
        IExpr c = gPower.coefficient(i * k);
        gSeries.setCoeff(i, c == null ? F.C0 : c);
      }
    }

    ASTSeriesData gInv = new ASTSeriesData(expansionVariable, F.C0, 1, gTruncate, 1);
    gInv.setCoeff(0, F.C0);

    ASTSeriesData fDivX = new ASTSeriesData(expansionVariable, F.C0, 0, gTruncate, 1);
    for (int i = 1; i < gTruncate; i++) {
      IExpr c = gSeries.coefficient(i);
      fDivX.setCoeff(i - 1, c == null ? F.C0 : c);
    }

    for (int n = 1; n < gTruncate; n++) {
      IExpr lagrangeCoeff = gSeries.computeLagrangeCoefficient(n, fDivX, engine);
      gInv.setCoeff(n, lagrangeCoeff == null ? F.C0 : lagrangeCoeff);
    }

    ASTSeriesData pSeries;
    if (puiseuxDenominator == 1) {
      pSeries = gInv;
    } else {
      IExpr pExpr = gInv.powerSeries(puiseuxDenominator);
      if (!pExpr.isPresent() || !(pExpr instanceof ASTSeriesData)) {
        return F.NIL;
      }
      pSeries = (ASTSeriesData) pExpr;
    }

    IExpr newVar = variable;
    if (!constantTerm.isZero()) {
      newVar = F.Plus(variable, constantTerm.negate());
    }

    ASTSeriesData result =
        new ASTSeriesData(newVar, F.C0, pSeries.minExponent, pSeries.truncateOrder, k);
    for (int i = pSeries.minExponent; i < pSeries.truncateOrder; i++) {
      IExpr c = pSeries.coefficient(i);
      result.setCoeff(i, c == null ? F.C0 : c);
    }

    IExpr c0 = pSeries.coefficient(0);
    IExpr newConstant = engine.evaluate(F.Plus(expansionPoint, c0 == null ? F.C0 : c0));
    result.setCoeff(0, newConstant);

    return result;
  }

  @Override
  public IExpr set(int location, IExpr object) {
    hashValue = 0;
    IExpr result;
    switch (location) {
      case 1:
        result = expansionVariable;
        expansionVariable = object;
        return result;
      case 2:
        result = expansionPoint;
        expansionPoint = object;
        return result;
      case 3:
        if (!object.isList()) {
          throw new IndexOutOfBoundsException(
              "SeriesData: Index[" + Integer.valueOf(location) + "] expects list of data.");
        }
        return arg3();
      case 4:
        result = F.ZZ(minExponent);
        minExponent = object.toIntDefault();
        if (F.isNotPresent(minExponent)) {
          throw new IndexOutOfBoundsException(
              "SeriesData: Index[" + Integer.valueOf(location) + "] expects machine size integer.");
        }
        return result;
      case 5:
        result = F.ZZ(truncateOrder);
        truncateOrder = object.toIntDefault();
        if (F.isNotPresent(truncateOrder)) {
          throw new IndexOutOfBoundsException(
              "SeriesData: Index[" + Integer.valueOf(location) + "] expects machine size integer.");
        }
        return result;
      case 6:
        result = F.ZZ(puiseuxDenominator);
        puiseuxDenominator = object.toIntDefault();
        if (F.isNotPresent(puiseuxDenominator)) {
          throw new IndexOutOfBoundsException(
              "SeriesData: Index[" + Integer.valueOf(location) + "] expects machine size integer.");
        }
        return result;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location));
    }
  }

  public void setCoeff(int k, IExpr value) {
    if (value.isZero() || k >= truncateOrder) {
      return;
    }
    coefficientMap.put(k, value);
    if (coefficientMap.size() == 1) {
      minExponent = k;
      exponentBound = k + 1;
    } else {
      if (k < minExponent) {
        minExponent = k;
      } else if (k >= exponentBound) {
        exponentBound = k + 1;
        // if (k >= truncate) {
        // truncate = k + 1;
        // }
      }
    }
  }

  public void setDenominator(int denominator) {
    this.puiseuxDenominator = denominator;
  }

  public void setZero(int k) {
    if (coefficientMap.containsKey(k)) {
      coefficientMap.remove(k);
      if (k == minExponent) {
        minExponent = k + 1;
      }
      if (k == exponentBound) {
        exponentBound = k - 1;
      }
    }
  }

  public ASTSeriesData shift(int shift) {
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        this.minExponent, truncateOrder, puiseuxDenominator);
    for (int i = this.minExponent; i < this.exponentBound; i++) {
      series.setCoeff(i + shift, this.coefficient(i));
    }
    return series;
  }

  public ASTSeriesData shift(int shift, IExpr coefficient, int power) {
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        this.minExponent, power, puiseuxDenominator);
    for (int i = this.minExponent; i < this.exponentBound; i++) {
      series.setCoeff(i + shift, this.coefficient(i).times(coefficient));
    }
    return series;
  }

  public ASTSeriesData shiftTimes(int shift, IExpr coefficient, int power) {
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        this.minExponent, power, puiseuxDenominator);
    for (int i = this.minExponent; i < this.exponentBound; i++) {
      series.setCoeff(i * shift, this.coefficient(i).times(coefficient));
    }
    return series;
  }

  @Override
  public int size() {
    return 7;
  }

  public ASTSeriesData sqrPS() {
    int newPower = truncateOrder + minExponent;
    int minSize = minExponent + minExponent;
    ASTSeriesData series =
        new ASTSeriesData(expansionVariable, expansionPoint, minSize, newPower, puiseuxDenominator);
    for (int n = minSize; n < newPower; n++) {
      IASTAppendable sum = F.PlusAlloc(n - minSize + 1);
      for (int k = this.minExponent; k < this.exponentBound; k++) {
        int j = n - k;
        if (j >= this.minExponent && j < this.truncateOrder) {
          sum.append(F.Times(this.coefficient(k), this.coefficient(j)));
        }
      }
      IExpr value = F.eval(sum);
      if (!value.isZero()) {
        series.setCoeff(n, value);
      }
    }
    return series;
  }

  @Override
  public ASTSeriesData subtract(IExpr b) {
    if (b instanceof ASTSeriesData) {
      return subtractPS((ASTSeriesData) b);
    }
    if (b.isZero()) {
      return this;
    }
    IExpr value = F.eval(coefficient(0).subtract(b));
    ASTSeriesData series = copy();
    if (value.isZero()) {
      series.setZero(0);
    } else {
      series.setCoeff(0, value);
    }
    return series;
  }

  public ASTSeriesData subtractPS(ASTSeriesData b) {
    int newDenominator = ArithmeticUtils.lcm(puiseuxDenominator, b.puiseuxDenominator);
    int scaleA = newDenominator / puiseuxDenominator;
    int scaleB = newDenominator / b.puiseuxDenominator;

    int n1 = minExponent * scaleA;
    int n2 = b.minExponent * scaleB;
    int minSize = Math.min(n1, n2);

    int t1 = truncateOrder * scaleA;
    int t2 = b.truncateOrder * scaleB;
    int newPower = Math.min(t1, t2);

    ASTSeriesData series =
        new ASTSeriesData(expansionVariable, expansionPoint, minSize, newPower, newDenominator);

    for (int i = minSize; i < newPower; i++) {
      IExpr cA = F.C0;
      if (i % scaleA == 0) {
        cA = this.coefficient(i / scaleA);
      }
      IExpr cB = F.C0;
      if (i % scaleB == 0) {
        cB = b.coefficient(i / scaleB);
      }
      IExpr sum = F.eval(F.Subtract(cA, cB));
      if (!sum.isZero()) {
        series.setCoeff(i, sum);
      }
    }
    return series;
  }

  /** Multiply a power series with a scalar */
  @Override
  public ASTSeriesData times(IExpr b) {
    if (b instanceof ASTSeriesData) {
      return timesPS((ASTSeriesData) b);
    }
    if (b.isOne()) {
      return this;
    }
    ASTSeriesData series = copy();
    for (int i = minExponent; i < exponentBound; i++) {
      series.setCoeff(i, this.coefficient(i).times(b));
    }
    return series;
  }

  public ASTSeriesData timesPS(ASTSeriesData b) {
    if (this.equals(b)) {
      return sqrPS();
    }
    int newDenominator = ArithmeticUtils.lcm(puiseuxDenominator, b.puiseuxDenominator);
    int scaleA = newDenominator / puiseuxDenominator;
    int scaleB = newDenominator / b.puiseuxDenominator;

    int t1 = truncateOrder * scaleA;
    int t2 = b.truncateOrder * scaleB;
    int n1 = minExponent * scaleA;
    int n2 = b.minExponent * scaleB;

    // Mathematical error bound for O(x^{t1}) * O(x^{n2}) and O(x^{t2}) * O(x^{n1})
    int newPower = Math.min(t1 + n2, t2 + n1);
    int minSize = n1 + n2;

    ASTSeriesData series =
        new ASTSeriesData(expansionVariable, expansionPoint, minSize, newPower, newDenominator);

    for (int n = minSize; n < newPower; n++) {
      IASTAppendable sum = F.PlusAlloc(n - minSize + 1);
      for (int k = this.minExponent; k < this.exponentBound; k++) {
        int i = k * scaleA;
        int j = n - i;
        if (j >= n2 && j < t2 && (j % scaleB == 0)) {
          int kb = j / scaleB;
          sum.append(F.Times(this.coefficient(k), b.coefficient(kb)));
        }
      }
      IExpr value = F.eval(sum);
      if (!value.isZero()) {
        series.setCoeff(n, value);
      }
    }
    return series;
  }

  @Override
  public IExpr[] toArray() {
    IExpr[] result = new IExpr[7];
    result[0] = head();
    result[1] = arg1();
    result[2] = arg2();
    result[3] = arg3();
    result[4] = arg4();
    result[5] = arg5();
    result[6] = get(6);
    return result;
  }

  public IAST toSeriesData() {
    // list of coefficients
    IASTAppendable coefficientList = F.mapRange(minExponent, exponentBound, i -> coefficient(i));
    return F.SeriesData(expansionVariable, expansionPoint, coefficientList, F.ZZ(minExponent),
        F.ZZ(truncateOrder), F.ZZ(puiseuxDenominator));
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);
    int size = size();
    objectOutput.writeInt(size);
    for (int i = 0; i < size; i++) {
      objectOutput.writeObject(get(i));
    }
  }

  private Object writeReplace() {
    return optional();
  }
}
