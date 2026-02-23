package org.matheclipse.core.polynomials;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D4;
import static org.matheclipse.core.expression.F.CN3;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.S.Times;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Solve polynomial equations up to fourth degree ( <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>
 * ).
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
 * See <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia -
 * Cubic function</a> See <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia -
 * Quartic equation</a> <br>
 * TODO not completly tested. Especially if a division through zero occurs.
 */
public class QuarticSolver {
  private final static int QUADRATIC_A = 2;
  private final static int QUADRATIC_B = 1;
  private final static int QUADRATIC_C = 0;
  private final static int QUADRATIC_DISCRIMINANT = 3;
  private final static int QUADRATIC_ROOTS = 4;
  private final static int QUADRATIC_INTERVAL_DATA = 5;

  public static IASTMutable solve(IExpr exprPoly, IExpr x) throws ArithmeticException {
    IExpr[] coefficients = new IExpr[] {F.C0, F.C0, F.C0, F.C0, F.C0};
    if (convert2Coefficients(exprPoly, x, coefficients)) {
      return quarticSolve(coefficients[4], coefficients[3], coefficients[2], coefficients[1],
          coefficients[0], true, true);
    }
    return F.NIL;
  }

  public static IAST quadraticIntervalData(IBuiltInSymbol symbol, IExpr exprPoly, IExpr x) {
    IExpr[] parameters = quadraticParameters(symbol, exprPoly, x);
    return (parameters != null) ? (IAST) parameters[QUADRATIC_INTERVAL_DATA] : F.NIL;
  }

  private static IExpr[] quadraticParameters(IBuiltInSymbol symbol, IExpr exprPoly, IExpr x) {
    IExpr[] parameters = new IExpr[] {F.C0, F.C0, F.C0, F.C0, F.C0, F.CEmptyIntervalData};
    if (convert2Coefficients(exprPoly, x, parameters) //
        && !parameters[2].isPossibleZero(true)) {
      // a^2+b*x+c
      IExpr c = parameters[QUADRATIC_C];
      IExpr b = parameters[QUADRATIC_B];
      IExpr a = parameters[QUADRATIC_A];
      IExpr discriminant = quadraticDiscriminant(a, b, c);
      parameters[QUADRATIC_DISCRIMINANT] = discriminant;
      IAST roots = quadraticSolve(a, b, c);
      parameters[QUADRATIC_ROOTS] = roots;

      if (a.greater(F.C0).isTrue()) {
        // a > 0 - parabola opens upwards - function has minimum value

        if (discriminant.greater(F.C0).isTrue()) {
          // discriminant > 0 - two distinct real roots - the parabola intersects the x-axis at two
          // points
          if (symbol == S.Less) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.Less, S.Less, roots.second()));
          } else if (symbol == S.LessEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.LessEqual, S.LessEqual, roots.second()));
          } else if (symbol == S.Greater) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.IntervalData(//
                F.List(F.CNIInfinity, S.Less, S.Less, roots.first()), //
                F.List(roots.second(), S.Less, S.Less, F.CInfinity));
          } else if (symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.IntervalData(//
                F.List(F.CNIInfinity, S.Less, S.LessEqual, roots.first()), //
                F.List(roots.second(), S.LessEqual, S.Less, F.CInfinity));
          }
        } else if (discriminant.less(F.C0).isTrue()) {
          // discriminant < 0 - there are no real roots. The parabola does not intersect the x-axis
          // The entire parabola is above the x-axis.
          if (symbol == S.Less || symbol == S.LessEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CEmptyIntervalData;
          } else if (symbol == S.Greater || symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CRealsIntervalData;
          }

        } else if (discriminant.isPossibleZero(true)) {
          // discriminant == 0 - exactly one real root. The parabola touches the x-axis at one point
          // (the vertex is on the x-axis)
          if (symbol == S.Less || symbol == S.Greater) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CEmptyIntervalData;
          } else if (symbol == S.LessEqual || symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.LessEqual, S.LessEqual, roots.first()));
          }
        } else {
          return null;
        }
      } else if (a.less(F.C0).isTrue()) {
        // a < 0 - parabola opens downwards - function has maximum value

        if (discriminant.greater(F.C0).isTrue()) {
          // discriminant > 0 - two distinct real roots - the parabola intersects the x-axis at two
          // points
          if (symbol == S.Less) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.IntervalData(//
                F.List(F.CNIInfinity, S.Less, S.Less, roots.first()), //
                F.List(roots.second(), S.Less, S.Less, F.CInfinity));
          } else if (symbol == S.LessEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.IntervalData(//
                F.List(F.CNIInfinity, S.Less, S.LessEqual, roots.first()), //
                F.List(roots.second(), S.LessEqual, S.Less, F.CInfinity));
          } else if (symbol == S.Greater) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.Less, S.Less, roots.second()));
          } else if (symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.LessEqual, S.LessEqual, roots.second()));
          }
        } else if (discriminant.less(F.C0).isTrue()) {
          // discriminant < 0 - there are no real roots. The parabola does not intersect the x-axis
          // The entire parabola is below the x-axis.
          if (symbol == S.Less || symbol == S.LessEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CRealsIntervalData;
          } else if (symbol == S.Greater || symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CEmptyIntervalData;
          }

        } else if (discriminant.isPossibleZero(true)) {
          // discriminant == 0 - exactly one real root. The parabola touches the x-axis at one point
          // (the vertex is on the x-axis)
          if (symbol == S.Less || symbol == S.Greater) {
            parameters[QUADRATIC_INTERVAL_DATA] = F.CEmptyIntervalData;
          } else if (symbol == S.LessEqual || symbol == S.GreaterEqual) {
            parameters[QUADRATIC_INTERVAL_DATA] =
                F.IntervalData(F.List(roots.first(), S.LessEqual, S.LessEqual, roots.first()));
          }
        } else {
          return null;
        }
      } else {
        return null;
      }
      return parameters;
    }
    return null;
  }

  public static IASTMutable solve(IExpr exprPoly, IExpr x, boolean createSet, boolean sort)
      throws ArithmeticException {
    IExpr[] coefficients = new IExpr[] {F.C0, F.C0, F.C0, F.C0, F.C0};
    if (convert2Coefficients(exprPoly, x, coefficients)) {
      return quarticSolve(coefficients[4], coefficients[3], coefficients[2], coefficients[1],
          coefficients[0], createSet, sort);
    }
    return F.NIL;
  }

  private static boolean convert2Coefficients(IExpr exprPoly, IExpr x, IExpr[] coefficients) {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isPlus()) {
        for (int i = 1; i < ast.size(); i++) {
          if (!convertTerm2Coefficients(ast.get(i), x, coefficients)) {
            return false;
          }
        }
        return true;
      }
      return convertTerm2Coefficients(ast, x, coefficients);
    }
    return convertTerm2Coefficients(exprPoly, x, coefficients);
  }

  private static boolean convertTerm2Coefficients(final IExpr exprPoly, IExpr x,
      IExpr[] coefficients) {
    if (exprPoly.isFree(x, true)) {
      coefficients[0] = F.eval(F.Plus(coefficients[0], exprPoly));
      return true;
    }
    if (exprPoly instanceof IAST) {
      IAST ast = (IAST) exprPoly;
      if (ast.isTimes()) {
        int exponent = -1;
        IASTAppendable coeff = ast.copyAppendable();
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (arg.isPower()) {
            final IExpr base = arg.base();
            if (x.equals(base)) {
              exponent = arg.exponent().toIntDefault();
              if (exponent < 0 || exponent > 4) {
                return false;
              }
              coeff.remove(i);
              coefficients[exponent] = F.eval(F.Plus(coefficients[exponent], coeff));
              return true;
            }
          } else if (x.equals(arg)) {
            coeff.remove(i);
            coefficients[1] = F.eval(F.Plus(coefficients[1], coeff));
            return true;
          }
        }
        return true;
      } else if (ast.isPower()) {
        final IExpr temp = ast.arg1();
        if (x.equals(temp)) {
          int exponent = ast.exponent().toIntDefault();
          if (exponent < 0 || exponent > 4) {
            return false;
          }
          coefficients[exponent] = F.eval(F.Plus(coefficients[exponent], F.C1));
          return true;
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      if (x.equals(exprPoly)) {
        coefficients[1] = F.eval(F.Plus(coefficients[1], F.C1));
        return true;
      }
    }
    return false;
  }

  public static IAST quarticSolveN(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
    return (IAST) EvalEngine.get().evalN(quarticSolve(a, b, c, d, e));
  }

  /**
   * <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
   *
   * @param a coefficient for <code>x^4</code>
   * @param b coefficient for <code>x^3</code>
   * @param c coefficient for <code>x^2</code>
   * @param d coefficient for <code>x</code>
   * @param e coefficient for <code>1</code>
   * @return
   */
  public static IASTAppendable quarticSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e) {
    return quarticSolve(a, b, c, d, e, true, true);
  }

  /**
   * <code>Solve(a*x^4+b*x^3+c*x^2+d*x+e==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>
   *
   * @param a coefficient for <code>x^4</code>
   * @param b coefficient for <code>x^3</code>
   * @param c coefficient for <code>x^2</code>
   * @param d coefficient for <code>x</code>
   * @param e coefficient for <code>1</code>
   * @param createSet delete duplicates from result list
   * @return
   */
  public static IASTAppendable quarticSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e,
      boolean createSet, boolean sort) {
    if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return cubicSolve(b, c, d, e, null, createSet, sort);
    } else {
      if (e.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return cubicSolve(a, b, c, d, C0, createSet, sort);
      }
      if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)
          && d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return biQuadraticSolve(a, c, e, null, createSet, sort);
      }
      IExpr temp = a.subtract(e);
      if (temp.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        temp = b.subtract(d);
        if (temp.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          return quasiSymmetricQuarticSolve(a, b, c, createSet, sort);
        }
      }
      // -3*b^2/(8*a^2) + c/a
      IExpr alpha = F.eval(Plus(Times(CN3, Power(b, C2), Power(Times(ZZ(8L), Power(a, C2)), CN1)),
          Times(c, Power(a, CN1))));
      // b^3/(8*a^3) - b*c/(2*a^2) + d/a
      IExpr beta = F.eval(Plus(Times(Power(b, C3), Power(Times(ZZ(8L), Power(a, C3)), CN1)),
          Times(CN1, b, c, Power(Times(C2, Power(a, C2)), CN1)), Times(d, Power(a, CN1))));
      // -3*b^4/(256*a^4) + b^2*c/(16*a^3) - b*d/(4*a^2) + e/a
      IExpr gamma = F.eval(Plus(Times(CN3, Power(b, C4), Power(Times(ZZ(256L), Power(a, C4)), CN1)),
          Times(Power(b, C2), c, Power(Times(ZZ(16L), Power(a, C3)), CN1)),
          Times(CN1, b, d, Power(Times(C4, Power(a, C2)), CN1)), Times(e, Power(a, CN1))));
      if (beta.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        // -1/4 * b/a
        return biQuadraticSolve(C1, alpha, gamma, Times(CN1D4, b, Power(a, CN1)), createSet, sort);
      }

      // return depressedQuarticSolve(a, b, alpha, beta, gamma);
      IASTAppendable result = F.ListAlloc(6);

      // c^2 - 3*b*d + 12*a*e
      IExpr delta0 = F.eval(Plus(Power(c, C2), Times(CN1, C3, b, d), Times(ZZ(12L), a, e)));
      // 2*c^3 - 9*b*c*d + 27*a*d^2 + 27*b^2*e - 72*a*c*e
      IExpr delta1 = F.eval(Plus(Times(C2, Power(c, C3)), Times(CN1, ZZ(9L), b, c, d),
          Times(ZZ(27L), a, Power(d, C2)), Times(ZZ(27L), Power(b, C2), e),
          Times(CN1, ZZ(72L), a, c, e)));
      // (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
      IExpr delta3 = F.eval(Power(
          Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))), C1D3));

      // -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
      // a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
      // a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
      // 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
      // Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
      // + delta3/(3 2^(1/3) a)])]/2
      result
          .append(
              Plus(
                  Times(CN1, b, Power(Times(C4, a),
                      CN1)),
                  Times(CN1, C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                              Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3),
                                  CN1)),
                              Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                  Times(CN1, C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
                              Times(CN1, C4, c, Power(Times(C3, a),
                                  CN1)),
                              Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
                              Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
                              Times(CN1, Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
                                  Times(C4, b, c,
                                      Power(Power(a, C2), CN1)),
                                  Times(CN1, ZZ(8L), d, Power(a, CN1))),
                                  Power(
                                      Times(C4,
                                          Sqrt(Plus(
                                              Times(Power(b, C2),
                                                  Power(Times(C4, Power(a, C2)), CN1)),
                                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                                              Times(
                                                  Power(C2, C1D3), delta0,
                                                  Power(Times(C3, a, delta3), CN1)),
                                              Times(delta3,
                                                  Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                                      CN1)))))));

      // -b/(4 a) - Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
      // a delta3) + delta3/(3 2^(1/3) a)]/2 + Sqrt[b^2/(2
      // a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
      // 2^(1/3) a) - (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
      // Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
      // + delta3/(3 2^(1/3) a)])]/2
      result
          .append(
              Plus(
                  Times(CN1, b, Power(Times(C4, a),
                      CN1)),
                  Times(CN1, C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                              Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3),
                                  CN1)),
                              Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                  Times(C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
                              Times(CN1, C4, c, Power(Times(C3, a),
                                  CN1)),
                              Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
                              Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
                              Times(CN1, Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
                                  Times(C4, b, c,
                                      Power(Power(a, C2), CN1)),
                                  Times(CN1, ZZ(8L), d, Power(a, CN1))),
                                  Power(
                                      Times(C4,
                                          Sqrt(Plus(
                                              Times(Power(b, C2),
                                                  Power(Times(C4, Power(a, C2)), CN1)),
                                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                                              Times(
                                                  Power(C2, C1D3), delta0,
                                                  Power(Times(C3, a, delta3), CN1)),
                                              Times(delta3,
                                                  Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                                      CN1)))))));

      // -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
      // a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
      // a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
      // 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
      // Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
      // + delta3/(3 2^(1/3) a)])]/2
      result
          .append(
              Plus(
                  Times(CN1, b, Power(Times(C4, a),
                      CN1)),
                  Times(C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                              Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3),
                                  CN1)),
                              Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                  Times(CN1, C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
                              Times(CN1, C4, c, Power(Times(C3, a),
                                  CN1)),
                              Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
                              Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
                              Times(Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
                                  Times(C4, b, c, Power(Power(a, C2), CN1)),
                                  Times(CN1, ZZ(8L), d, Power(a, CN1))),
                                  Power(
                                      Times(C4,
                                          Sqrt(Plus(
                                              Times(Power(b, C2),
                                                  Power(Times(C4, Power(a, C2)), CN1)),
                                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                                              Times(
                                                  Power(C2, C1D3), delta0,
                                                  Power(Times(C3, a, delta3), CN1)),
                                              Times(delta3,
                                                  Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                                      CN1)))))));

      // -b/(4 a) + Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3
      // a delta3) + delta3/(3 2^(1/3) a)]/2 - Sqrt[b^2/(2
      // a^2) - (4 c)/(3 a) - (2^(1/3) (delta0))/(3 a delta3) - delta3/(3
      // 2^(1/3) a) + (-(b^3/a^3) + (4 b c)/a^2 - (8 d)/a)/(4
      // Sqrt[b^2/(4 a^2) - (2 c)/(3 a) + (2^(1/3) (delta0))/(3 a delta3)
      // + delta3/(3 2^(1/3) a)])]/2
      result
          .append(
              Plus(
                  Times(CN1, b, Power(Times(C4, a),
                      CN1)),
                  Times(C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C4, Power(a, C2)), CN1)),
                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                              Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3),
                                  CN1)),
                              Times(delta3, Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                  Times(C1D2,
                      Sqrt(
                          Plus(Times(Power(b, C2), Power(Times(C2, Power(a, C2)), CN1)),
                              Times(CN1, C4, c, Power(Times(C3, a),
                                  CN1)),
                              Times(CN1, Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
                              Times(CN1, delta3, Power(Times(C3, Power(C2, C1D3), a), CN1)),
                              Times(Plus(Times(CN1, Power(b, C3), Power(Power(a, C3), CN1)),
                                  Times(C4, b, c, Power(Power(a, C2), CN1)),
                                  Times(CN1, ZZ(8L), d, Power(a, CN1))),
                                  Power(
                                      Times(C4,
                                          Sqrt(Plus(
                                              Times(Power(b, C2),
                                                  Power(Times(C4, Power(a, C2)), CN1)),
                                              Times(CN1, C2, c, Power(Times(C3, a), CN1)),
                                              Times(
                                                  Power(C2, C1D3), delta0,
                                                  Power(Times(C3, a, delta3), CN1)),
                                              Times(delta3,
                                                  Power(Times(C3, Power(C2, C1D3), a), CN1))))),
                                      CN1)))))));
      if (createSet) {
        return createSet(result);
      }
      return evalAndSort(result, sort);
    }
  }

  /**
   * <code>Solve(a*x^3+b*x^2+c*x+d==0,x)</code>. See
   * <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia -
   * Cubic function</a>
   *
   * @param a coefficient for <code>x^3</code>
   * @param b coefficient for <code>x^2</code>
   * @param c coefficient for <code>x</code>
   * @param d coefficient for <code>1</code>
   * @param additionalSolution ann additional solution, which should be appended to the result
   * @return
   */
  public static IASTAppendable cubicSolve(IExpr a, IExpr b, IExpr c, IExpr d,
      IExpr additionalSolution) {
    return cubicSolve(a, b, c, d, additionalSolution, true, true);
  }

  /**
   * <code>Solve(a*x^3+b*x^2+c*x+d==0,x)</code>. See
   * <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia -
   * Cubic function</a>
   *
   * @param a coefficient for <code>x^3</code>
   * @param b coefficient for <code>x^2</code>
   * @param c coefficient for <code>x</code>
   * @param d coefficient for <code>1</code>
   * @param additionalSolution ann additional solution, which should be appended to the result
   * @return
   */
  public static IASTAppendable cubicSolve(IExpr a, IExpr b, IExpr c, IExpr d,
      IExpr additionalSolution, boolean createSet, boolean sort) {
    if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return quadraticSolve(b, c, d, additionalSolution, null, createSet, sort);
    } else {
      if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return quadraticSolve(a, b, c, additionalSolution, C0, createSet, sort);
      }
      IASTAppendable result = F.ListAlloc(4);
      if (additionalSolution != null) {
        result.append(additionalSolution);
      }
      // 18*a*b*c*d-4*b^3*d+b^2*c^2-4*a*c^3-27*a^2*d^2
      IExpr discriminant = F.eval(Plus(Times(ZZ(18L), a, b, c, d), Times(CN4, Power(b, C3), d),
          Times(Power(b, C2), Power(c, C2)), Times(CN4, a, Power(c, C3)),
          Times(ZZ(-27L), Power(a, C2), Power(d, C2))));
      // b^2 - 3*a*c
      IExpr delta0 = F.eval(Plus(Power(b, C2), Times(CN1, C3, a, c)));
      // (-2)*b^3 + 9*a*b*c - 27*a^2*d
      IExpr delta1 = F.eval(Plus(Times(ZZ(-2L), Power(b, C3)), Times(ZZ(9L), a, b, c),
          Times(CN1, ZZ(27L), Power(a, C2), d)));

      // Extract the shared discriminant term inside the square root
      IExpr discriminantTerm = Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3)));

      // (delta1 + Sqrt[delta1^2-4*delta0^3])
      IExpr argDelta3 = F.eval(Plus(delta1, Sqrt(discriminantTerm)));

      // Prevent cancellation or selecting the zero-root.
      // If the positive branch of the resolvent evaluates to 0, use the negative branch.
      if (argDelta3.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        argDelta3 = F.eval(Plus(delta1, Times(CN1, Sqrt(discriminantTerm))));
      }

      IExpr delta3 = F.eval(Power(argDelta3, C1D3));

      IAST value = Times(CN1, b, Power(Times(C3, a), CN1));
      if (discriminant.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (delta0.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // the three roots are equal
          // (-b)/(3*a)
          result.append(value);
          result.append(value);
          result.append(value);
        } else {
          // the double root
          // (9*a*d-b*c)/(2*delta0)
          IASTMutable doubledRoot =
              Times(Plus(Times(ZZ(9L), a, d), Times(CN1, b, c)), Power(Times(C2, delta0), CN1));
          result.append(doubledRoot);
          result.append(doubledRoot);
          // and a simple root
          // (4*a*b*c-9*a^2*d-b^3)/(a*delta0)
          result.append(Times(Plus(Times(C4, a, b, c), Times(CN1, ZZ(9L), Power(a, C2), d),
              Times(CN1, Power(b, C3))), Power(Times(a, delta0), CN1)));
        }
      } else {
        // -(b/(3*a)) - (2^(1/3) (-delta0))/(3*a*delta3) +
        // delta3/(3*2^(1/3)*a)
        result.append(Plus(b.negate().times(C3.times(a).power(CN1)),
            Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
            Times(Power(argDelta3.timesDistributed(C1D2), C1D3), C3.times(a).power(CN1))));

        // -(b/(3*a)) + ((1 + I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
        // - ((1 - I Sqrt[3]) delta3)/(6*2^(1/3)*a)
        result.append(Plus(value,
            Times(Plus(C1, Times(CI, Sqrt(C3))), CN1, delta0,
                Power(Times(C3, Power(C2, F.QQ(2L, 3L)), a, delta3), CN1)),
            Times(CN1, Plus(C1, Times(CN1, CI, Sqrt(C3))), delta3,
                Power(Times(ZZ(6L), Power(C2, C1D3), a), CN1))));

        // -(b/(3*a)) + ((1 - I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
        // - ((1 + I Sqrt[3]) delta3)/(6*2^(1/3)*a)
        result.append(Plus(value,
            Times(Plus(C1, Times(CN1, CI, Sqrt(C3))), CN1, delta0,
                Power(Times(C3, Power(C2, F.QQ(2L, 3L)), a, delta3), CN1)),
            Times(CN1, Plus(C1, Times(CI, Sqrt(C3))), delta3,
                Power(Times(ZZ(6L), Power(C2, C1D3), a), CN1))));
      }
      if (createSet) {
        return createSet(result);
      }
      return evalAndSort(result, sort);
    }
  }

  public static IASTAppendable createSet(IASTAppendable result) {
    Set<IExpr> set1 = new TreeSet<IExpr>();
    for (int i = 1; i < result.size(); i++) {
      IExpr temp = result.get(i);
      temp = F.eval(temp);
      if (!temp.isIndeterminate()) {
        set1.add(temp);
        continue;
      }
    }
    result = F.ListAlloc(set1.size());
    for (IExpr e : set1) {
      result.append(e);
    }
    return result;
  }

  public static IASTAppendable evalAndSort(IASTAppendable result, boolean sort) {
    int i = 1;
    while (i < result.size()) {
      IExpr temp = result.get(i);
      if (temp.isTimes2() && temp.second().isPlus()) {
        IExpr f = F.FactorTerms.ofNIL(EvalEngine.get(), temp.second());
        if (f.isPresent()) {
          temp = F.Times(temp.first(), f);
        }
      }
      temp = F.eval(temp);
      if (temp.isIndeterminate()) {
        result.remove(i);
        continue;
      }
      result.set(i, temp);
      i++;
    }
    if (sort) {
      result.sortInplace();
    }
    return result;
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a
   * @param b
   * @param c
   * @return
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c) {
    return quadraticSolve(a, b, c, null, null, true, false);
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a
   * @param b
   * @param c
   * @param createSet
   * @return
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, boolean createSet) {
    return quadraticSolve(a, b, c, null, null, createSet, true);
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a
   * @param b
   * @param c
   * @param createSet
   * @param sort the result
   * @return
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, boolean createSet,
      boolean sort) {
    return quadraticSolve(a, b, c, null, null, createSet, sort);
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a coefficient for <code>x^2</code>
   * @param b coefficient for <code>x</code>
   * @param c coefficient for <code>1</code>
   * @param solution1 possible first solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @param solution2 possible second solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @return
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, IExpr solution1,
      IExpr solution2) {
    return quadraticSolve(a, b, c, solution1, solution2, true, true);
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a coefficient for <code>x^2</code>
   * @param b coefficient for <code>x</code>
   * @param c coefficient for <code>1</code>
   * @param solution1 possible first solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @param solution2 possible second solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @param createSet delete duplicate values in the result
   * @param sortResult sort the result values
   * @return
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, IExpr solution1,
      IExpr solution2, boolean createSet, boolean sort) {
    IASTAppendable result = F.ListAlloc(5);
    if (solution1 != null) {
      result.append(solution1);
    }
    if (solution2 != null) {
      result.append(solution2);
    }
    if (!a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        result.append(F.C0);
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          result.append(F.C0);
        } else {
          result.append(F.Times(F.CN1, b, Power(a, -1L)));
        }
      } else {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // a*x^2 + c == 0
          IExpr rhs = S.Divide.of(F.Negate(c), a);
          IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(rhs);
          if (negExpr.isPresent()) {
            IExpr numerator = surdSqrt(S.Numerator.of(negExpr));
            IExpr denominator = surdSqrt(S.Denominator.of(negExpr));
            result.append(Times.of(F.CNI, F.Divide(numerator, denominator)));
            result.append(Times.of(F.CI, F.Divide(numerator, denominator)));
          } else {
            IExpr numerator = surdSqrt(S.Numerator.of(rhs));
            IExpr denominator = surdSqrt(S.Denominator.of(rhs));
            result.append(Times.of(F.CN1, F.Divide(numerator, denominator)));
            result.append(S.Divide.of(numerator, denominator));
          }
        } else {
          IExpr discriminant = quadraticDiscriminant(a, b, c);
          discriminant = discriminant.sqrt();
          result.append(Times(Plus(b.negate(), discriminant), Power(a.times(F.C2), -1L)));
          result.append(Times(Plus(b.negate(), discriminant.negate()), Power(a.times(F.C2), -1L)));
        }
        if (createSet) {
          return createSet(result);
        }
        return evalAndSort(result, sort);
      }
    } else {
      if (!b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        result.append(Times(CN1, c, Power(b, -1L)));
      }
    }
    if (createSet) {
      return createSet(result);
    }
    return evalAndSort(result, sort);
  }

  private static IExpr quadraticDiscriminant(IExpr a, IExpr b, IExpr c) {
    return F.evalExpand(Plus(F.Sqr(b), a.times(c).times(F.C4).negate()));
  }

  private static IExpr surdSqrt(IExpr arg) {
    if (arg.isTimes()) {
      IAST times = (IAST) arg;
      for (int i = 1; i < times.size(); i++) {
        IExpr x = times.get(i);
        if (x.isPower() && x.exponent().isReal()) {
          if (x.exponent().isEvenResult()) {
            IASTAppendable res1 = F.TimesAlloc(times.size());
            res1.appendArgs(times, i);
            IASTAppendable res2 = F.Times();
            res2.append(F.Power(x.base(), F.Divide(x.exponent(), F.C2)));
            for (int j = i + 1; j < times.size(); j++) {
              x = times.get(j);
              if (x.isPower() && x.exponent().isReal() && x.exponent().isEvenResult()) {
                res2.append(F.Power(x.base(), F.Divide(x.exponent(), F.C2)));
              } else {
                res1.append(x);
              }
            }
            return F.Times(res2, F.Sqrt(res1));
          }
        }
      }
    } else if (arg.isPower() && arg.exponent().isReal()) {
      IAST x = (IAST) arg;
      if (x.exponent().isEvenResult()) {
        return F.Power(x.base(), F.Divide(x.exponent(), F.C2));
      }
    }
    return F.Sqrt(arg);
  }

  /**
   * Solve the bi-quadratic expression. <code>Solve(a*x^4+bc*x^2+e==0,x)</code>.
   *
   * <p>
   * See Bronstein 1.6.2.4
   *
   * @param a
   * @param c
   * @param e
   * @param sum
   * @return
   */
  public static IASTAppendable biQuadraticSolve(IExpr a, IExpr c, IExpr e, IExpr sum) {
    return biQuadraticSolve(a, c, e, sum, true, true);
  }

  /**
   * Solve the bi-quadratic expression. <code>Solve(a*x^4+bc*x^2+e==0,x)</code>.
   *
   * <p>
   * See Bronstein 1.6.2.4
   *
   * @param a
   * @param c
   * @param e
   * @param sum
   * @param createSet
   * @return
   */
  public static IASTAppendable biQuadraticSolve(IExpr a, IExpr c, IExpr e, IExpr sum,
      boolean createSet, boolean sort) {
    IASTAppendable result = F.ListAlloc(4);
    // Sqrt[c^2-4*a*e]
    IExpr sqrt = F.eval(Sqrt(Plus(Power(c, C2), Times(CN1, C4, a, e))));

    // (-c+sqrt)/(2*a)
    IExpr y1 = Times(Plus(Times(CN1, c), sqrt), Power(Times(C2, a), CN1));

    // -(c+sqrt)/(2*a)
    IExpr y2 = Times(CN1, Plus(c, sqrt), Power(Times(C2, a), CN1));
    if (sum == null) {
      result.append(Sqrt(y1));
      result.append(Times(CN1, Sqrt(y1)));
      result.append(Sqrt(y2));
      result.append(Times(CN1, Sqrt(y2)));
    } else {
      result.append(Plus(sum, Sqrt(y1)));
      result.append(Plus(sum, Times(CN1, Sqrt(y1))));
      result.append(Plus(sum, Sqrt(y2)));
      result.append(Plus(sum, Times(CN1, Sqrt(y2))));
    }
    if (createSet) {
      return createSet(result);
    }
    return evalAndSort(result, sort);
  }

  /**
   * Solve the special case of a "Quasi-symmetric equation" <code>
   * Solve(a*x^4+b*x^3+c*x^2+b*x+a==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>. See
   * Bronstein 1.6.2.4
   *
   * @param a coefficient for <code>x^4</code> and <code>x</code>
   * @param b coefficient for <code>x^3</code> and <code>x</code>
   * @param c coefficient for <code>x^2</code>
   * @return
   */
  public static IASTAppendable quasiSymmetricQuarticSolve(IExpr a, IExpr b, IExpr c) {
    return quasiSymmetricQuarticSolve(a, b, c, true, true);
  }

  /**
   * Solve the special case of a "Quasi-symmetric equation" <code>
   * Solve(a*x^4+b*x^3+c*x^2+b*x+a==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quartic_equation">Wikipedia - Quartic equation</a>. See
   * Bronstein 1.6.2.4
   *
   * @param a coefficient for <code>x^4</code> and <code>x</code>
   * @param b coefficient for <code>x^3</code> and <code>x</code>
   * @param c coefficient for <code>x^2</code>
   * @return
   */
  public static IASTAppendable quasiSymmetricQuarticSolve(IExpr a, IExpr b, IExpr c,
      boolean createSet, boolean sort) {
    IASTAppendable result = F.ListAlloc(4);
    // Sqrt[b^2-4*a*c+8*a^2]
    IExpr sqrt =
        F.eval(Sqrt(Plus(Power(b, C2), Times(CN1, C4, a, c), Times(ZZ(8L), Power(a, C2)))));

    // (-b+sqrt)/(2*a)
    IExpr y1 = Times(Plus(Times(CN1, b), sqrt), Power(Times(C2, a), CN1));
    // -(b+sqrt)/(2*a)
    IExpr y2 = Times(CN1, Plus(b, sqrt), Power(Times(C2, a), CN1));

    // (y1+Sqrt[y1^2-4])/2
    result.append(Times(C1D2, Plus(y1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4))))));
    // (y1-Sqrt[y1^2-4])/2
    result.append(Times(C1D2, Plus(y1, Times(CN1, Sqrt(Plus(Power(y1, C2), Times(CN1, C4)))))));
    // (y2+Sqrt[y2^2-4])/2
    result.append(Times(C1D2, Plus(y2, Sqrt(Plus(Power(y2, C2), Times(CN1, C4))))));
    // (y2-Sqrt[y2^2-4])/2
    result.append(Times(C1D2, Plus(y2, Times(CN1, Sqrt(Plus(Power(y2, C2), Times(CN1, C4)))))));
    if (createSet) {
      return createSet(result);
    }
    return evalAndSort(result, sort);
  }

  /**
   * Sort the arguments of a list.
   *
   * @param resultList assumed to be of type <code>List()</code>
   * @return sorted <code>resultList</code>
   */
  public static IASTMutable sortASTArguments(IASTMutable resultList) {
    if (resultList.isList()) {
      EvalEngine engine = EvalEngine.get();
      IASTAppendable result = F.ListAlloc(resultList.size());
      for (int i = 1; i < resultList.size(); i++) {
        IExpr temp = resultList.get(i);
        // if (temp.isList()) {
        temp = engine.evaluate(temp);
        if (temp.isList()) {
          EvalAttributes.sort((IASTMutable) temp);
          result.append(temp);
        } else {
          result.append(resultList.get(i));
        }
      }
      EvalAttributes.sort(result);
      return result;
    }
    return resultList;
  }
}
