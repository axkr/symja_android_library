package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

  private static int actualMinExponent(ASTSeriesData result) {
    int actualMinExp = Config.INVALID_INT;
    for (int i = result.minExponent(); i < result.truncateOrder(); i++) {
      if (!result.coefficient(i).isZero()) {
        actualMinExp = i;
        break;
      }
    }
    return actualMinExp;
  }

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
    }
    if (listOfVariables.argSize() > 0) {
      return F.Nest(S.List, polynomialExpr, listOfVariables.argSize());
    }
    return F.NIL;
  }

  private static ASTSeriesData compositionSeries(final IAST function, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    if (function.argSize() == 1) {
      IExpr arg = function.arg1();
      if (!arg.isFree(x) && !arg.equals(x)) {

        IExpr u0 = engine.evalQuiet(F.subst(arg, x, x0));
        if (u0.isIndeterminate() || u0.isDirectedInfinity()) {
          IExpr dirExpr = direction == 0 ? S.Reals : F.ZZ(direction);
          u0 = engine.evalQuiet(F.Limit(arg, F.Rule(x, x0), F.Rule(S.Direction, dirExpr)));
        }

        if (u0.isSpecialsFree()) {
          int currentN = n;
          ASTSeriesData innerSeries = seriesDataRecursive(arg, x, x0, currentN, direction, engine);

          int probeLimit = Math.max(n, 0) + 12;
          while (innerSeries != null && innerSeries.isOrder() && currentN < probeLimit) {
            currentN += 3;
            innerSeries = seriesDataRecursive(arg, x, x0, currentN, direction, engine);
          }

          if (innerSeries != null) {
            int k = innerSeries.minExponent();
            if (k <= 0) {
              k = 1;
              while (k < innerSeries.truncateOrder() && innerSeries.coefficient(k).isZero()) {
                k++;
              }
            }

            if (k >= innerSeries.truncateOrder()) {
              IExpr f0 = engine.evalQuiet(F.subst(function, x, x0));
              ASTSeriesData constSeries =
                  new ASTSeriesData(x, x0, 0, n, innerSeries.puiseuxDenominator());
              constSeries.setCoeff(0, f0);
              return constSeries;
            }

            if (k > 0) {
              int den = innerSeries.puiseuxDenominator();
              int outerN = (n * den + k - 1) / k;
              outerN = Math.max(outerN, 1) + 2;

              IExpr y = F.Dummy("y");
              IExpr outerFunc = function.setAtCopy(1, y);

              ASTSeriesData outerSeries =
                  seriesDataRecursive(outerFunc, y, u0, outerN, direction, engine);

              if (outerSeries != null) {
                return outerSeries.compose(innerSeries);
              }
            }
          }
        }
      }
    }
    return null;
  }

  private static IExpr extractFromSeries(IExpr expr, IExpr x, IExpr x0, EvalEngine engine) {
    int n = 1;
    ASTSeriesData sd = null;

    IExpr seriesFunction = expr;
    IExpr seriesX0 = x0;
    boolean isInfinity = x0.isInfinity() || x0.isNegativeInfinity();

    if (isInfinity) {
      seriesX0 = F.C0;
      seriesFunction = engine.evaluate(F.subst(expr, x, F.Power(x, F.CN1)));
    }

    while (n < 20) {
      sd = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, n, engine);
      if (sd != null && !sd.isOrder()) {
        int nMin = sd.minExponent();
        while (nMin < sd.truncateOrder() && sd.coefficient(nMin).isZero()) {
          nMin++;
        }
        if (nMin < sd.truncateOrder()) {
          IExpr coeff = sd.coefficient(nMin);
          IExpr varPart;

          int den = sd.puiseuxDenominator();
          IExpr pow;
          if (den == 1) {
            pow = F.ZZ(nMin);
          } else {
            pow = F.QQ(nMin, den).normalize();
          }

          if (isInfinity) {
            return engine.evaluate(F.Times(coeff, F.Power(x, F.Negate(pow))));
          } else if (x0.isZero()) {
            varPart = x;
          } else {
            varPart = F.Subtract(x, x0);
          }
          return engine.evaluate(F.Times(coeff, F.Power(varPart, pow)));
        }
      }
      n += 2;
    }
    return F.NIL;
  }

  public static ASTSeriesData fromCoefficientMap(IExpr x, IExpr x0, final int n,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    int maxExponent = Config.INVALID_INT;
    for (IExpr exp : coefficientMap.keySet()) {
      int exponent = exp.toIntDefault();
      if (F.isPresent(exponent) && exponent > maxExponent) {
        maxExponent = exponent;
      }
    }

    int truncate = n + 1;
    if (F.isPresent(maxExponent)) {
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

  // private static ASTSeriesData plusSeriesDataRecursive(final IAST plusAST, IExpr x, IExpr x0,
  // final int n, EvalEngine engine) {
  // Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
  // IASTAppendable rest = F.PlusAlloc(4);
  //
  // ASTSeriesData temp = null;
  // if (x0.isZero()) {
  // temp = ASTSeriesData.polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
  // } else {
  // rest.appendArgs(plusAST);
  // }
  //
  // if (temp != null) {
  // if (rest.size() <= 1) {
  // return temp;
  // }
  // }
  //
  // ASTSeriesData series = null;
  // int start = 1;
  // if (temp != null) {
  // series = temp;
  // } else {
  // if (rest.size() > 1) {
  // series = seriesDataRecursive(rest.arg1(), x, x0, n, engine);
  // if (series == null) {
  // return null;
  // }
  // start = 2;
  // }
  // }
  //
  // if (series != null) {
  // for (int i = start; i < rest.size(); i++) {
  // ASTSeriesData arg = seriesDataRecursive(rest.get(i), x, x0, n, engine);
  // if (arg == null) {
  // return null;
  // }
  // series = series.plusPS(arg);
  // }
  // return series;
  // }
  // return null;
  // }

  /**
   * Calculate the n-th coefficient of the inverse function f^(-1)(x) using the Lagrange-Bürmann
   * inversion theorem. [x^n] f^(-1)(x) = (1/n) * [x^(n-1)] (f(x) / x)^(-n)
   */
  public static IExpr lagrangeBurmannCoefficient(IExpr f, IExpr x, IExpr x0, IExpr n,
      EvalEngine engine) {
    if (!n.isInteger() || !n.isPositive()) {
      return F.NIL;
    }
    int nInt = n.toIntDefault();
    if (nInt == 0) {
      return F.C0;
    }

    if (x0.isZero()) {
      IExpr f0 = engine.evaluate(F.subst(f, x, F.C0));

      // condition 1: f(0) = 0
      if (f0.isZero()) {
        // condition 2: f'(0) != 0
        IExpr df0 = engine.evaluate(F.subst(F.D(f, x), x, F.C0));
        if (df0.isZero()) {
          return F.NIL;
        }

        int targetOrder = nInt - 1;

        // FAST-PATH: Use ASTSeriesData for exact algebraic manipulation
        ASTSeriesData fSeries = ASTSeriesData.seriesDataRecursive(f, x, F.C0, nInt + 1, engine);

        if (fSeries != null) {
          // Clean encapsulation: Shift the series mathematically by -puiseuxDenominator
          ASTSeriesData fDivX = fSeries.shift(-fSeries.puiseuxDenominator());

          // Evaluate (f(x)/x)^(-n)
          IExpr innerSeriesExpr = fDivX.powerSeries(F.QQ(-nInt, 1), engine);

          if (innerSeriesExpr instanceof ASTSeriesData) {
            ASTSeriesData innerSeries = (ASTSeriesData) innerSeriesExpr;
            IExpr innerCoeff = innerSeries.coefficient(targetOrder);
            if (innerCoeff != null) {
              return engine.evaluate(F.Together(F.Times(F.Power(F.ZZ(nInt), F.CN1), innerCoeff)));
            }
          }
        }

        // FALLBACK: If the series generator fails
        IExpr innerFunc = engine.evaluate(F.Power(F.Divide(f, x), F.ZZ(-nInt)));
        IExpr fallbackCoeff =
            engine.evaluate(F.SeriesCoefficient(innerFunc, F.List(x, F.C0, F.ZZ(targetOrder))));
        if (fallbackCoeff.isPresent() && !fallbackCoeff.isAST(S.SeriesCoefficient)) {
          return engine.evaluate(F.Together(F.Times(F.Power(F.ZZ(nInt), F.CN1), fallbackCoeff)));
        }
      }
    }
    return F.NIL;
  }

  public static IExpr leadingTerm(IExpr expr, IExpr x, IExpr x0, EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }
    if (expr.equals(x)) {
      if (x0.isZero() || x0.isInfinity() || x0.isNegativeInfinity()) {
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
            return extractFromSeries(expr, x, x0, engine);
          }
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
            if (baseLt.isPresent() && expLimit.isSpecialsFree() && !expLimit.isZero()) {
              return engine.evaluate(F.Power(baseLt, expLimit));
            }
            break;
          }

          case ID.ArcCos: {
            IExpr arg = ast.arg1();
            IExpr argLimit = engine.evaluate(F.subst(arg, x, x0));
            if (argLimit.isZero()) {
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

    IExpr limit = engine.evaluate(F.subst(expr, x, x0));
    if (limit.isPresent() && !limit.isZero() && !limit.isIndeterminate()
        && !limit.isDirectedInfinity()) {
      return limit;
    }
    return F.NIL;
  }

  private static ASTSeriesData plusSeriesDataRecursive(final IAST plusAST, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) {
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
    IASTAppendable rest = F.PlusAlloc(4);

    ASTSeriesData temp = null;
    if (x0.isZero()) {
      try {
        temp = ASTSeriesData.polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
      } catch (RuntimeException rex) {
        // Exception caught: expression contains non-polynomial terms.
        // The 'rest' appendable is corrupted by the aborted loop, so we must reset it.
        coefficientMap.clear();
        rest = F.PlusAlloc(plusAST.size());
        rest.appendArgs(plusAST);
      }
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

  private static ASTSeriesData polynomialSeries(final IExpr function, IExpr x, IExpr x0,
      final int n, Map<IExpr, IExpr> coefficientMap) {
    IASTAppendable rest = F.PlusAlloc(4);
    return polynomialSeries(function, x, x0, n, coefficientMap, rest);
  }

  private static ASTSeriesData polynomialSeries(final IExpr function, IExpr x, IExpr x0,
      final int n, Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    coefficientMap = ExprPolynomialRing.create(function, x, coefficientMap, rest);
    if (coefficientMap.size() > 0) {
      return fromCoefficientMap(x, x0, n, coefficientMap, rest);
    }
    return null;
  }

  private static ASTSeriesData powerSeriesDataRecursive(final IExpr powerAST, IExpr x, IExpr x0,
      final int n, final int direction, EvalEngine engine) throws ArithmeticException {
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
          throw new ArithmeticException("Denominator too large for series expansion");
        }
      }
    } else if (!(base instanceof ASTSeriesData)) {
      if (x0.isZero()) {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        ASTSeriesData temp = polynomialSeries(powerAST, x, x0, n, coefficientMap);
        if (temp != null) {
          return temp;
        }
      }
    }

    int currentN = n;
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

            int loopCount = 0;
            while (pTruncate < targetTruncate && loopCount++ < 4) {
              int b = p.denominator().toIntDefault();
              if (b != Config.INVALID_INT && b > 0) {
                int extraBaseTerms = (targetTruncate - pTruncate + b - 1) / b;
                currentN += extraBaseTerms + 2;

                ASTSeriesData series2 =
                    seriesDataRecursive(base, x, x0, currentN, direction, engine);

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

  private static ASTSeriesData seriesCoefficient(final IExpr function, IExpr x, IExpr x0,
      final int n, final int denominator, VariablesSet varSet, EvalEngine engine) {
    int startN = Math.min(n, -10);
    int maxProbe = Math.max(n, 0) + 10;
    int targetN = n;
    ISymbol power = F.Dummy("$$$n");
    IExpr temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, power)));

    if (temp.isPresent() && temp.isFree(S.SeriesCoefficient, true)) {
      boolean foundNonZero = false;
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
      ASTSeriesData ps = new ASTSeriesData(x, x0, startN, maxProbe + denominator, denominator);

      for (int i = startN; i <= Math.max(targetN, maxProbe); i++) {
        temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, F.ZZ(i))));

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
        result = polynomialSeries(function, x, x0, n, coefficientMap);
      } else {
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
      IExpr arg = function.first();
      IExpr limit = engine.evalQuiet(F.subst(arg, x, x0));
      if (limit.isIndeterminate() || limit.isDirectedInfinity()) {
        limit = engine.evalQuiet(F.Limit(arg, F.Rule(x, x0)));
      }

      if (limit.isInteger() && (limit.isNegativeResult() || limit.isZero())) {
        int k = limit.toIntDefault();
        if (F.isPresent(k)) {
          int absK = Math.abs(k);

          IASTAppendable denom = F.TimesAlloc(absK + 1);
          for (int i = 0; i <= absK; i++) {
            denom.append(F.Plus(arg, F.ZZ(i)));
          }

          IExpr rewritten = engine.evaluate(F.Divide(F.Gamma(F.Plus(arg, F.ZZ(absK + 1))), denom));
          result = seriesDataRecursive(rewritten, x, x0, n, direction, engine);
        }
      }

      if (result == null && !arg.isFree(x) && !arg.equals(x)) {
        ASTSeriesData compSeries = compositionSeries((IAST) function, x, x0, n, direction, engine);
        if (compSeries != null) {
          result = compSeries;
        }
      }
    } else if (function.isLog() && function.first().equals(x) && x0.isZero() && n >= 0) {
      result = new ASTSeriesData(x, x0, F.list(function), 0, n + 1, 1);
    } else if (function.isAST1() && !function.first().isFree(x) && !function.first().equals(x)) {
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

      int actualMinExp = actualMinExponent(result);
      if (F.isPresent(actualMinExp)) {
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

      if (!functionPart.isSpecialsFree()) {
        IExpr dirExpr = direction == 0 ? S.Reals : F.ZZ(direction);
        IExpr limitPart =
            engine.evalQuiet(F.Limit(derivedFunction, F.Rule(x, x0), F.Rule(S.Direction, dirExpr)));

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
    Map<IExpr, IExpr> coefficientMap = new LinkedHashMap<IExpr, IExpr>();

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
      return ASTSeriesData.fromCoefficientMap(x, x0, n, coefficientMap, rest);
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
      int indexShift = shift * series.puiseuxDenominator();
      series = series.shift(indexShift, coefficient, series.truncateOrder() + indexShift);
    }

    return series;
  }

  OpenIntToIExprHashMap<IExpr> coefficientMap;
  private int truncateOrder;
  private IExpr expansionVariable;
  private IExpr expansionPoint;
  private int minExponent;
  private int exponentBound;
  private int puiseuxDenominator;

  public ASTSeriesData() {
    super();
    truncateOrder = 0;
    puiseuxDenominator = 1;
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

  @Override
  public IAST clone() {
    return new ASTSeriesData(expansionVariable, expansionPoint, minExponent, exponentBound,
        truncateOrder, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>(coefficientMap));
  }

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

  /**
   * 
   * @return
   */
  public IAST coefficientList() {
    if (expansionPoint.isZero()) {
      int startIndex = Math.min(0, minExponent);
      int endIndex = exponentBound;
      int capacity = endIndex - startIndex;
      if (capacity < 4) {
        capacity = 4;
      }
      IASTAppendable coefficientList = F.ListAlloc(capacity);

      for (int i = startIndex; i < endIndex; i++) {
        coefficientList.append(coefficient(i));
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

  public ASTSeriesData compose(ASTSeriesData series2) {
    EvalEngine engine = EvalEngine.get();
    IExpr coeff0 = series2.coefficient(0);
    if (!coeff0.equals(expansionPoint)) {
      Errors.printMessage(S.SeriesData, "error", F.List("Constant " + coeff0 + " of series "
          + series2 + " unequals point " + expansionPoint + " of series " + this));
      return null;
    }
    ASTSeriesData series = new ASTSeriesData(series2.expansionVariable, series2.expansionPoint, 0,
        series2.truncateOrder, series2.puiseuxDenominator);
    ASTSeriesData x0Term;
    if (expansionPoint.isZero()) {
      x0Term = series2;
    } else {
      x0Term = series2.subtract(expansionPoint);
    }
    for (int n = minExponent; n < exponentBound; n++) {
      IExpr temp = coefficient(n);
      if (!temp.isZero()) {
        IRational p =
            puiseuxDenominator == 1 ? F.QQ(n, 1) : F.QQ(n, puiseuxDenominator).normalize();
        IExpr sExpr = x0Term.powerSeries(p, engine);
        if (sExpr instanceof ASTSeriesData) {
          ASTSeriesData s = (ASTSeriesData) sExpr;
          s = s.times(temp);
          series = series.plusPS(s);
        } else if (sExpr.isPresent()) {
          ASTSeriesData constSeries = new ASTSeriesData(series2.expansionVariable,
              series2.expansionPoint, 0, series2.truncateOrder, series2.puiseuxDenominator);
          constSeries.setCoeff(0, engine.evaluate(F.Times(sExpr, temp)));
          series = series.plusPS(constSeries);
        }
      }
    }
    return series;
  }

  private IExpr computeGeneralLagrangeCoefficient(int n, ASTSeriesData fDivX, EvalEngine engine) {
    ASTSeriesData fDivXPowerNeg = fDivX.powerSeries(-n);
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
   * @param x the variable
   */
  public ASTSeriesData derive(IExpr x) {
    EvalEngine engine = EvalEngine.get();
    if (this.expansionVariable.equals(x)) {
      if (isProbableZero()) {
        return this;
      }
      if (truncateOrder - puiseuxDenominator >= minExponent - puiseuxDenominator) {
        ASTSeriesData series =
            new ASTSeriesData(x, expansionPoint, minExponent - puiseuxDenominator,
                minExponent - puiseuxDenominator, truncateOrder - puiseuxDenominator,
                puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>());
        for (int i = minExponent; i < exponentBound; i++) {
          IExpr coeff = this.coefficient(i);
          if (coeff != null && !coeff.isZero()) {
            IExpr multiplier =
                puiseuxDenominator == 1 ? F.ZZ(i) : F.QQ(i, puiseuxDenominator).normalize();
            series.setCoeff(i - puiseuxDenominator, engine.evaluate(F.Times(coeff, multiplier)));
          }
        }
        return series;
      }
      return new ASTSeriesData(x, expansionPoint, 0, 0, puiseuxDenominator);
    } else {
      ASTSeriesData result = new ASTSeriesData(expansionVariable, expansionPoint, minExponent,
          exponentBound, truncateOrder, puiseuxDenominator, new OpenIntToIExprHashMap<IExpr>());
      boolean hasNonZero = false;
      for (int i = minExponent; i < exponentBound; i++) {
        IExpr coeff = this.coefficient(i);
        if (coeff != null && !coeff.isZero()) {
          IExpr dCoeff = engine.evaluate(F.D(coeff, x));
          result.setCoeff(i, dCoeff);
          if (!dCoeff.isZero()) {
            hasNonZero = true;
          }
        }
      }
      if (!hasNonZero) {
        ASTSeriesData zeroSeries = new ASTSeriesData(expansionVariable, expansionPoint, 0,
            truncateOrder, puiseuxDenominator);
        zeroSeries.setCoeff(0, F.C0);
        return zeroSeries;
      }
      return result;
    }
  }

  /**
   * Division of two power series, with the assumption that the second one is invertible. If the
   * second series is not invertible, it tries to find the leading exponent of each series and
   * perform the division accordingly, which may still succeed if the leading coefficient of the
   * divisor is a unit.
   * 
   * @param ps the divisor power series
   * @return the quotient power series, or throws ArithmeticException if the division cannot be
   *         performed due to non-unit
   */
  public ASTSeriesData dividePS(ASTSeriesData ps) throws ArithmeticException {
    if (ps.isNonZero()) {
      ASTSeriesData inverse = timesPS(ps.inverse());
      if (inverse != null) {
        return inverse;
      }
    }

    // Find the actual leading (minimum non-zero) exponent of each series
    int m = minExponent;
    while (m < exponentBound && coefficient(m).isZero()) {
      m++;
    }
    int n = ps.minExponent;
    while (n < ps.exponentBound && ps.coefficient(n).isZero()) {
      n++;
    }

    if (n >= ps.exponentBound) {
      throw new ArithmeticException("division by zero series");
    }
    if (m >= exponentBound) {
      return new ASTSeriesData(expansionVariable, expansionPoint, 0, 1, puiseuxDenominator);
    }
    if (!ps.coefficient(n).isUnit()) {
      throw new ArithmeticException(
          "division by non unit coefficient " + ps.coefficient(n) + ", n = " + n);
    }
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

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  public IExpr expansionPoint() {
    return expansionPoint;
  }

  public IExpr expansionVariable() {
    return expansionVariable;
  }

  public int exponentBound() {
    return exponentBound;
  }

  @Override
  public String fullFormString() {
    // Explicitly build the string to guarantee strict InputForm/FullForm propagation,
    // avoiding the intermediate toSeriesData() AST which can be intercepted by OutputForm rules.
    StringBuilder buf = new StringBuilder();
    buf.append("SeriesData(");
    buf.append(expansionVariable.fullFormString()).append(",");
    buf.append(expansionPoint.fullFormString()).append(",");
    buf.append(arg3().fullFormString()).append(",");
    buf.append(minExponent).append(",");
    buf.append(truncateOrder).append(",");
    buf.append(puiseuxDenominator).append(")");
    return buf.toString();
  }


  @Override
  public IExpr get(int location) {
    if (location >= 0 && location <= 7) {
      switch (location) {
        case 0:
          return head();
        case 1:
          return arg1();
        case 2:
          return arg2();
        case 3:
          return arg3();
        case 4:
          return arg4();
        case 5:
          return arg5();
        case 6:
          return F.ZZ(puiseuxDenominator);
      }
    }
    throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 1");
  }

  private IExpr getCoeffSafe(int index) {
    IExpr c = coefficient(index);
    return c == null ? F.C0 : c;
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    IAST result = normal(false);
    return result.getItems(items, length, offset);
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

  @Override
  public int hierarchy() {
    return SERIESID;
  }

  public ASTSeriesData integrate(IExpr x) {
    EvalEngine engine = EvalEngine.get();
    if (this.expansionVariable.equals(x)) {
      if (isProbableZero()) {
        return this;
      }
      ASTSeriesData series = new ASTSeriesData(x, expansionPoint, minExponent + puiseuxDenominator,
          minExponent + puiseuxDenominator, truncateOrder + puiseuxDenominator, puiseuxDenominator,
          new OpenIntToIExprHashMap<IExpr>());
      for (int i = minExponent; i < exponentBound; i++) {
        if (i + puiseuxDenominator != 0) {
          IExpr coeff = this.coefficient(i);
          if (coeff != null && !coeff.isZero()) {
            IExpr multiplier = puiseuxDenominator == 1 ? F.QQ(1, i + 1)
                : F.QQ(puiseuxDenominator, i + puiseuxDenominator).normalize();
            series.setCoeff(i + puiseuxDenominator, engine.evaluate(F.Times(coeff, multiplier)));
          }
        }
      }
      return series;
    }
    return null;
  }

  @Override
  public ASTSeriesData inverse() {
    int actualNMin = minExponent;
    while (actualNMin < exponentBound && coefficient(actualNMin).isZero()) {
      actualNMin++;
    }

    if (actualNMin >= exponentBound) {
      throw new ArgumentTypeException("infy", F.List(F.C0));
    }

    IExpr leadingCoeff = coefficient(actualNMin);
    IExpr d = leadingCoeff.inverse();

    int newNMin = -actualNMin;
    int newTruncate = truncateOrder - 2 * actualNMin;
    int numTerms = newTruncate - newNMin; // = truncateOrder - actualNMin

    ASTSeriesData result = new ASTSeriesData(expansionVariable, expansionPoint, newNMin,
        newTruncate, puiseuxDenominator);

    for (int i = 0; i < numTerms; i++) {
      if (i == 0) {
        result.setCoeff(newNMin, d);
      } else {
        IExpr c = F.C0;
        for (int k = 0; k < i; k++) {
          IExpr coeffK = result.coefficient(newNMin + k);
          int sourceIndex = actualNMin + i - k;
          if (sourceIndex < truncateOrder) {
            IExpr m = coeffK.multiply(coefficient(sourceIndex));
            c = c.sum(m);
          }
        }
        c = c.multiply(d.negate());
        int targetIndex = newNMin + i;
        if (targetIndex < newTruncate) {
          result.setCoeff(targetIndex, c);
        }
      }
    }
    return result;
  }

  @Override
  public boolean isAST0() {
    return false;
  }

  @Override
  public boolean isAST1() {
    return false;
  }

  @Override
  public boolean isAST2() {
    return false;
  }

  @Override
  public boolean isAST3() {
    return false;
  }

  public boolean isNonZero() {
    for (int i = minExponent; i < exponentBound; i++) {
      if (!coefficient(i).isZero()) {
        return true;
      }
    }
    return false;
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

  public int minExponent() {
    return minExponent;
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
   * Normalizes the series data to a polynomial-like expression.
   * 
   * @param nilIfUnevaluated parameter has no effect for this class
   */
  @Override
  public IASTMutable normal(boolean nilIfUnevaluated) {
    IExpr x = expansionVariable();
    IExpr x0 = expansionPoint();
    int nMin = minExponent();
    int nMax = truncateOrder();
    int denominator = puiseuxDenominator();
    int size = nMax - nMin;
    if (size < 4) {
      size = 4;
    }
    IExpr base;
    if (x0.isInfinity() || x0.isNegativeInfinity()) {
      base = F.Power(x, F.CN1);
    } else if (x0.isZero()) {
      base = x;
    } else {
      base = F.Subtract(x, x0);
    }

    IASTAppendable result = F.PlusAlloc(size);
    EvalEngine engine = EvalEngine.get();

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

        if (expr instanceof ASTSeriesData) {
          expr = ((ASTSeriesData) expr).normal(false);
        } else if (expr.isAST()) {
          expr = engine.evaluate(expr.normal(nilIfUnevaluated));
        }

        result.append(F.Times(expr, pow));
      }
    }
    return result;
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
    IExpr result = powerSeries(F.ZZ(n), EvalEngine.get());
    if (result instanceof ASTSeriesData) {
      return (ASTSeriesData) result;
    }
    return new ASTSeriesData(expansionVariable, expansionPoint, 0, truncateOrder,
        puiseuxDenominator);
  }

  public int puiseuxDenominator() {
    return puiseuxDenominator;
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
      }
    }
  }

  public void setDenominator(int denominator) {
    this.puiseuxDenominator = denominator;
  }

  /**
   * Sets the coefficient of the k-th term to zero, and updates the minExponent and exponentBound
   * accordingly.
   * 
   * @param k the index of the term to set to zero
   */
  public void setZero(int k) {
    if (coefficientMap.containsKey(k)) {
      coefficientMap.remove(k);
      if (coefficientMap.isEmpty()) {
        minExponent = truncateOrder;
        exponentBound = truncateOrder;
      } else {
        if (k == minExponent) {
          int newMin = minExponent + 1;
          while (newMin < exponentBound && !coefficientMap.containsKey(newMin)) {
            newMin++;
          }
          minExponent = newMin;
        }
        if (k == exponentBound - 1) {
          int newMax = exponentBound - 2;
          while (newMax >= minExponent && !coefficientMap.containsKey(newMax)) {
            newMax--;
          }
          exponentBound = newMax + 1;
        }
      }
    }
  }

  public ASTSeriesData shift(int shift) {
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        this.minExponent + shift, truncateOrder + shift, puiseuxDenominator);
    for (int i = this.minExponent; i < this.exponentBound; i++) {
      series.setCoeff(i + shift, this.coefficient(i));
    }
    return series;
  }

  public ASTSeriesData shift(int shift, IExpr coefficient, int power) {
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        this.minExponent + shift, power, puiseuxDenominator);
    for (int i = this.minExponent; i < this.exponentBound; i++) {
      series.setCoeff(i + shift, this.coefficient(i).times(coefficient));
    }
    return series;
  }

  public ASTSeriesData shiftTimes(int shift, IExpr coefficient, int power) {
    int newMinExponent = this.minExponent * shift;
    int newPower = power * shift;
    ASTSeriesData series = new ASTSeriesData(this.expansionVariable, this.expansionPoint,
        newMinExponent, newPower, puiseuxDenominator);
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
    int capacity = exponentBound - minExponent;
    IASTAppendable coefficientList = F.ListAlloc(capacity > 0 ? capacity : 4);

    for (int i = minExponent; i < exponentBound; i++) {
      IExpr coeff = coefficient(i);
      // Recursively convert inner multivariate coefficients to pure AST nodes
      if (coeff instanceof ASTSeriesData) {
        coefficientList.append(((ASTSeriesData) coeff).toSeriesData());
      } else {
        coefficientList.append(coeff);
      }
    }

    return F.SeriesData(expansionVariable, expansionPoint, coefficientList, F.ZZ(minExponent),
        F.ZZ(truncateOrder), F.ZZ(puiseuxDenominator));
  }

  public int truncateOrder() {
    return truncateOrder;
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
