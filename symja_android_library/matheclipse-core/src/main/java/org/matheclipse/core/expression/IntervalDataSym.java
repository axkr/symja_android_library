package org.matheclipse.core.expression;

import java.util.Comparator;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ReduceVariableIntervalSet;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Interval sets will be represented by objects with head {@link S#IntervalData} wrapped around a
 * sequence of quadruples of the form, e.g., <code>{a,Less,LessEqual,b}</code> representing the half
 * open interval <code>(a,b]</code>. The empty interval set is represented by
 * <code>IntervalData()</code>.
 * 
 * <p>
 * See:
 * <ul>
 * <li><a href=
 * "https://mathematica.stackexchange.com/questions/162486/operating-with-real-intervals/162505#162505">162486/operating-with-real-intervals/162505#162505</a>
 * <li><a href= "https://en.wikipedia.org/wiki/Interval_%28mathematics%29">Wikipedia: Interval
 * mathematics</a>
 *
 */
public class IntervalDataSym {

  /**
   * IExprProcessor interface method boolean process (IExpr min, IExpr max, IASTAppendable result,
   * int index), return true or false;
   */
  @FunctionalInterface
  private static interface IExprProcessor {
    /**
     * Append the transformed interval part <code>[min, max]</code> to the result.
     *
     * @param min minimum limit of a single interval part
     * @param lessMin the left inequality symbol of the minimum limit
     * @param lessMax the right inequality symbol of the maximum limit
     * @param max maximum limit of a single interval part
     * @param result the resulting list of interval parts
     * @param index
     * @return <code>true</code> if a new interval could be appended to result; <code>false</code>
     *         otherwise.
     */
    boolean apply(IExpr min, IBuiltInSymbol lessMin, IBuiltInSymbol lessMax, IExpr max,
        IASTAppendable result, int index);
  }

  private static final Comparator<IExpr> INTERVAL_COMPARATOR = new Comparator<IExpr>() {
    @Override
    public int compare(IExpr o1, IExpr o2) {
      IAST list1 = (IAST) o1;
      IAST list2 = (IAST) o2;
      if (list1.arg1().equals(list2.arg1())) {
        if (list1.arg4().equals(list2.arg4())) {
          int cp2 = list1.arg2().compareTo(list2.arg2());
          if (cp2 == 0) {
            return list1.arg3().compareTo(list2.arg3());
          }
        }
        IExpr gt1 = list1.arg4().greater(list2.arg4());
        return gt1.isTrue() ? 1 : gt1.isFalse() ? -1 : list1.arg4().compareTo(list2.arg4());
      }
      IExpr gt2 = list1.arg1().greater(list2.arg1());
      return gt2.isTrue() ? 1 : gt2.isFalse() ? -1 : list1.arg1().compareTo(list2.arg1());
    }
  };

  public static IExpr arccos(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.List(F.ArcCos(min), lessMin, lessMax, F.ArcCos(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arccosh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C1) && engine.evalLessEqual(max, F.CInfinity)) {
        result.append(index, F.List(F.ArcCosh(min), lessMin, lessMax, F.ArcCosh(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arccot(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.ArcCot(min), lessMin, lessMax, F.ArcCot(max)));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.CNPiHalf, lessMin, lessMax, F.ArcCot(min)));
        result.append(F.List(F.ArcCot(max), lessMin, lessMax, F.CPiHalf));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.List(F.ArcCot(min), lessMin, lessMax, F.ArcCot(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arcsin(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.List(F.ArcSin(min), lessMin, lessMax, F.ArcSin(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arcsinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.List(F.ArcSinh(min), lessMin, lessMax, F.ArcSinh(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arctan(final IAST ast) {
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.List(F.ArcTan(min), lessMin, lessMax, F.ArcTan(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arctanh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.List(F.ArcTanh(min), lessMin, lessMax, F.ArcTanh(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr asRelational(IAST expr, IExpr x) {
    if (expr.isIntervalData()) {
      IAST interval = expr;
      if (interval.size() == 1) {
        return S.False;
      }
      IASTAppendable result = F.ast(S.Or, interval.argSize());
      for (int i = 1; i < interval.size(); i++) {
        IAST list1 = (IAST) interval.get(i);
        if (!list1.isList4()) {
          return F.NIL;
        }
        IExpr min = list1.arg1();
        IBuiltInSymbol minLess = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol maxLess = (IBuiltInSymbol) list1.arg3();
        IExpr max = list1.arg4();
        if (min.isNegativeInfinity()) {
          if (max.isInfinity()) {
            return S.True;
          }
          result.append(F.binaryAST2(maxLess, x, max));
          continue;
        }
        if (max.isInfinity()) {
          // let's write the variable on the left-hand-side for normalization
          result.append(F.binaryAST2(minLess == S.Less ? S.Greater : S.GreaterEqual, x, min));
          continue;
        }
        // let's write the variable on the left-hand-side for normalization
        result.append(//
            F.And(F.binaryAST2(minLess == S.Less ? S.Greater : S.GreaterEqual, x, min), //
                F.binaryAST2(maxLess, x, max)));
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Return an interval including each boundary.
   * 
   * @param lhs left boundary
   * @param rhs right boundary
   * @return <code>IntervalData({lhs, LessEqual, LessEqual, rhs}))</code>
   */
  public static IAST close(final IExpr lhs, final IExpr rhs) {
    return F.IntervalData(//
        F.List(lhs, //
            S.LessEqual, //
            S.LessEqual, //
            rhs));
  }

  public static IAST cos(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreater(difference, F.C2Pi)) {
        // difference >= 2 * Pi
        result.append(index, F.List(F.CN1, S.LessEqual, S.LessEqual, F.C1));
      } else {
        // slope from 1st derivative
        double dMin = engine.evalDouble(F.Sin(min).negate());
        double dMax = engine.evalDouble(F.Sin(max).negate());
        if (engine.evalLessEqual(difference, S.Pi)) {
          if (dMin >= 0) {
            if (dMax >= 0) {
              result.append(index, F.List(F.Cos(min), lessMin, lessMax, F.Cos(max)));
            } else {
              IAST list4 = minimum(F.Cos(min), lessMin, F.Cos(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(list4.arg1(), list4.arg2(), list4.arg4(), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.List(F.Cos(max), lessMax, lessMin, F.Cos(min)));
            } else {
              IAST list4 = maximum(F.Cos(min), lessMin, F.Cos(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(F.CN1, list4.arg4(), list4.arg2(), list4.arg1()));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.List(F.CN1, F.C1));
            } else {
              IAST list4 = minimum(min, lessMin, max, lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(list4.arg1(), list4.arg2(), list4.arg4(), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.List(F.CN1, lessMin, lessMax, F.C1));
            } else {
              IAST list4 = maximum(min, lessMin, max, lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(F.CN1, list4.arg4(), list4.arg2(), list4.arg1()));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST cosh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.List(F.Cosh(min), lessMin, lessMax, F.Cosh(max)));
        } else if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          IAST list4 = maximum(F.Cosh(min), lessMin, F.Cosh(max), lessMax, engine);
          if (list4.isNIL()) {
            return false;
          }
          result.append(F.List(F.C1, list4.arg4(), list4.arg2(), list4.arg1()));
        } else if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
          result.append(F.List(F.Cosh(min), lessMin, lessMax, F.Cosh(max)));
        }
        return true;
      }
      return false;
    });
  }

  public static IAST cot(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, S.Pi)) {
        result.append(F.List(F.CNInfinity, S.Less, S.Less, F.CInfinity));
      } else {
        double dMin = engine.evalDouble(F.Cot(min));
        double dMax = engine.evalDouble(F.Cot(max));
        if (engine.evalLessEqual(difference, F.CPiHalf)) {
          // difference <= 1/2*Pi
          if (dMin < 0) {
            if (dMax >= 0) {
              result.append(F.List(F.CNInfinity, S.Less, lessMin, F.Cot(min)));
              result.append(F.List(F.Cot(max), lessMax, S.Less, F.CInfinity));
            } else {
              result.append(F.List(F.Cot(max), lessMax, lessMin, F.Cot(min)));
            }
          } else {
            result.append(F.List(F.Cot(min), lessMin, lessMax, F.Cot(max)));
          }
        } else { // difference between {Pi/2, Pi}
          if (dMin >= 0) {
            if (dMax < 0) {
              result.append(F.List(F.CNInfinity, S.Less, lessMax, F.Cot(max)));
              result.append(F.List(F.Cot(min), lessMin, S.Less, F.CInfinity));
            } else {
              if (dMin < dMax) {
                result.append(F.List(F.CNInfinity, S.Less, lessMin, F.Cot(min)));
                result.append(F.List(F.Cot(max), lessMax, S.Less, F.CInfinity));
              } else {
                result.append(F.CRealsRange);
              }
            }
          } else {
            if (dMax < 0) {
              if (dMin < dMax) {
                result.append(F.List(F.Cot(max), lessMax, S.Less, F.CInfinity));
                result.append(F.List(F.CNInfinity, S.Less, lessMin, F.Cot(min)));
              } else {
                result.append(F.List(F.CNInfinity, S.Less, S.Less, F.CInfinity));
              }
            } else {
              result.append(F.CRealsRange);
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST coth(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.Coth(max), lessMin, lessMax, F.Coth(min)));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.CNInfinity, S.Less, lessMin, F.Coth(min)));
        result.append(F.List(F.Coth(max), lessMax, S.Less, F.CInfinity));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.List(F.Coth(min), lessMin, lessMax, F.Coth(max)));
        return true;
      }
      return false;
    });
  }

  public static IAST csc(final IAST ast) {
    IAST interval = sin(ast);
    if (interval.isPresent()) {
      return inverse(interval);
    }
    return F.NIL;
  }

  public static IAST csch(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.Csch(max), lessMin, lessMax, F.Csch(min)));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.List(F.CNInfinity, S.Less, lessMax, F.Csch(min)));
        result.append(F.List(F.Csch(max), lessMin, S.Less, F.CInfinity));
        return true;
      }
      return false;
    }, (min, lessMin, lessMax, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.List(F.Csch(min), lessMin, lessMax, F.Csch(max)));
        return true;
      }
      return false;
    });
  }

  /**
   * The empty interval set is represented by <code>IntervalData()</code>.
   * 
   * @return
   */
  public static IAST emptySet() {
    return F.IntervalData();
  }

  public static IAST intersection(final IAST interval1, final IAST interval2) {
    return intersection(interval1, interval2, EvalEngine.get());
  }

  /**
   * Returns the intersection of two intervals.
   * 
   * @param interval1 the first interval
   * @param interval2 the second interval
   * @param engine the evaluation engine
   * @return the intersection of the two intervals
   */
  public static IAST intersection(final IAST interval1, final IAST interval2, EvalEngine engine) {
    IASTAppendable result = F.ast(S.IntervalData, 3);
    for (int i = 1; i < interval1.size(); i++) {
      IAST list1 = (IAST) interval1.get(i);
      for (int j = 1; j < interval2.size(); j++) {
        IExpr min1 = list1.arg1();
        IBuiltInSymbol left1 = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol right1 = (IBuiltInSymbol) list1.arg3();
        IExpr max1 = list1.arg4();

        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IBuiltInSymbol left2 = (IBuiltInSymbol) list2.arg2();
        IBuiltInSymbol right2 = (IBuiltInSymbol) list2.arg3();
        IExpr max2 = list2.arg4();
        if (S.Less.ofQ(engine, max1, min2) || S.Less.ofQ(engine, max2, min1)) {
          continue;
        }
        if (S.Equal.ofQ(engine, max1, min2)) {
          if (right1 == S.Less || left2 == S.Less) {
            continue;
          }
        }
        if (S.Equal.ofQ(engine, max2, min1)) {
          if (right2 == S.Less || left1 == S.Less) {
            continue;
          }
        }
        if (min1.lessEqual(min2).isTrue()) {
          if (S.Equal.ofQ(engine, min1, min2)) {
            if (left2 == S.Less) {
              min1 = min2;
              left1 = left2;
            }
          } else {
            min1 = min2;
            left1 = left2;
          }
        }
        if (max1.greaterEqual(max2).isTrue()) {
          if (S.Equal.ofQ(engine, max1, max2)) {
            if (right2 == S.Less) {
              max1 = max2;
              right1 = right2;
            }
          } else {
            max1 = max2;
            right1 = right2;
          }
        }
        result.append(F.List(min1, left1, right1, max1));
      }
    }
    return result;
  }

  /**
   * Compute the complement of <code>interval2</code> in <code>interval1</code>. *
   * <p>
   * See:
   * <a href= "https://en.wikipedia.org/wiki/Complement_(set_theory)#Relative_complement">Complement
   * (set theory) - Relative complement</a>
   * 
   * @param interval1
   * @param interval2
   * @param engine
   * @return the complement of <code>interval2</code> in <code>interval1</code> or {@link F#NIL}
   */
  public static IAST intervalDataComplement(final IAST interval1, final IAST interval2,
      EvalEngine engine) {
    if (isEmptySet(interval1)) {
      return F.CEmptyIntervalData;
    }
    if (isEmptySet(interval2)) {
      return interval1;
    }
    // A \ B == A intersect (complement of B)
    IAST complementOf2 = complementOnReals(interval2, engine);
    if (complementOf2.isPresent()) {
      return intersection(interval1, complementOf2, engine);
    }
    return F.NIL;
  }

  /**
   * Compute the complement of an interval with respect to the real line.
   *
   * @param interval
   * @param engine
   * @return the complement of <code>interval</code> with respect to the real line
   */
  private static IAST complementOnReals(final IAST interval, EvalEngine engine) {
    IAST normalized = normalize(interval, engine);
    if (normalized.isNIL()) {
      normalized = interval;
    }
    if (isEmptySet(normalized)) {
      return reals();
    }

    IASTAppendable result = F.ast(S.IntervalData, normalized.size() + 1);
    IExpr current = F.CNInfinity;
    IBuiltInSymbol currentBoundary = S.Less;

    for (int i = 1; i < normalized.size(); i++) {
      IAST segment = (IAST) normalized.get(i);
      IExpr min2 = segment.arg1();
      IBuiltInSymbol minEnd2 = (IBuiltInSymbol) segment.arg2();

      if (S.Less.ofQ(engine, current, min2)) {
        result.append(F.List(current, currentBoundary, toggle(min2, minEnd2), min2));
      } else if (S.Equal.ofQ(engine, current, min2) && currentBoundary == S.Less
          && minEnd2 == S.Less) {
        // adjacent open intervals like (... a) and (a ...)
        // do nothing, the point 'a' is not in the original set, so it's in the complement
      }

      current = segment.arg4();
      currentBoundary = toggle(current, (IBuiltInSymbol) segment.arg3());
    }

    if (!(current.isInfinity() || current.isNegativeInfinity())) {
      result.append(F.List(current, currentBoundary, S.Less, F.CInfinity));
    }

    if (result.size() == 1) {
      return F.CEmptyIntervalData;
    }
    return result;
  }

  public static IExpr intervalDataIntersection(final IAST ast, EvalEngine engine) {
    for (int i = 1; i < ast.size(); i++) {
      if (!ast.get(i).isIntervalData()) {
        return F.NIL;
      }
      IAST interval = (IAST) ast.get(i);
      for (int j = 1; j < interval.size(); j++) {
        if (!interval.get(j).isList4()) {
          return F.NIL;
        }
        IAST list1 = (IAST) interval.get(j);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg4();
        if (!min1.isRealResult() || !max1.isRealResult()) {
          return F.NIL;
        }
      }
    }
    IAST result = (IAST) ast.arg1();
    result = normalize(result, engine);
    if (result.isNIL()) {
      result = (IAST) ast.arg1();
    }
    for (int i = 2; i < ast.size(); i++) {
      IAST interval = (IAST) ast.get(i);
      IAST normalizedArg = normalize(interval, engine);
      if (normalizedArg.isNIL()) {
        normalizedArg = interval;
      }
      result = intersection(result, normalizedArg, engine);
      if (result.size() == 1) {
        return result;
      }
    }
    IAST normalized = normalize(result, engine);
    return normalized.orElse(result);
  }

  public static IExpr intervalDataUnion(final IAST ast, EvalEngine engine) {
    int calculatedResultSize = 2;
    for (int i = 1; i < ast.size(); i++) {
      IAST interval;
      if (!ast.get(i).isIntervalData()) {
        return F.NIL;
      } else {
        interval = (IAST) ast.get(i);
      }
      calculatedResultSize += interval.argSize();
      for (int j = 1; j < interval.size(); j++) {
        if (!interval.get(j).isList4()) {
          return F.NIL;
        }
        IAST list1 = (IAST) interval.get(j);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg4();
        if (!min1.isRealResult() || !max1.isRealResult()) {
          return F.NIL;
        }
      }
    }
    IASTAppendable result = F.ast(S.IntervalData, calculatedResultSize);
    for (int i = 1; i < ast.size(); i++) {
      IAST interval = (IAST) ast.get(i);
      for (int j = 1; j < interval.size(); j++) {
        result.append(interval.get(j));
      }
    }
    IAST normalized = normalize(result, engine);

    return normalized.orElse(result);
  }


  private static IAST intervalList(ApfloatNum arg) {
    Apfloat apfloat = arg.fApfloat;
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return F.List(F.num(h.nextDown(apfloat)), //
        S.LessEqual, //
        S.LessEqual, //
        F.num(h.nextUp(apfloat)));
  }

  public static IExpr intervalToOr(IAST andTemplate, IAST interval, IExpr variable) {
    IASTAppendable orAST = F.ast(S.Or, interval.argSize());
    for (int i = 1; i < interval.size(); i++) {
      IAST list = (IAST) interval.get(i);
      if (list.isEmptyList() || list.argSize() != 4) {
        return S.False;
      }
      IASTAppendable andArg = andTemplate.copyAppendable();
      if (list.arg1().isNegativeInfinity()) {
        if (list.arg4().isInfinity()) {
          //
        } else if (list.arg3() == S.Less) {
          andArg.append(F.Less(variable, list.arg4()));
        } else if (list.arg3() == S.LessEqual) {
          andArg.append(F.LessEqual(variable, list.arg4()));
        }
      } else if (list.arg4().isInfinity()) {
        if (list.arg2() == S.Less) {
          andArg.append(F.Greater(variable, list.arg1()));
        } else if (list.arg2() == S.LessEqual) {
          andArg.append(F.GreaterEqual(variable, list.arg1()));
        }
      } else {
        if (list.arg1().equals(list.arg4())//
            && list.arg2() == S.LessEqual//
            && list.arg3() == S.LessEqual) {
          andArg.append(F.binaryAST2(S.Equal, variable, list.arg1()));
        } else {
          //
          // let's write the variable on the left-hand-side for normalization
          andArg.append(F.binaryAST2(list.arg2() == S.Less ? S.Greater : S.GreaterEqual, variable,
              list.arg1()));
          andArg.append(F.binaryAST2(list.arg3(), variable, list.arg4()));
        }
      }
      orAST.append(andArg);
    }
    return orAST;
  }

  public static IExpr intervalToOr(IAST interval, IExpr variable) {
    return intervalToOr(F.ast(S.And, 2), interval, variable);
  }

  /**
   * Compute <code>1 / interval(min,max)</code>.
   *
   * @param interval
   * @return
   */
  public static IAST inverse(final IAST interval) {
    IAST normalizedInterval = normalize(interval);
    if (normalizedInterval.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(normalizedInterval.size());
      for (int i = 1; i < normalizedInterval.size(); i++) {
        IAST list = (IAST) normalizedInterval.get(i);
        final IExpr min = list.arg1();
        final IExpr lessMin = list.arg2();
        final IExpr lessMax = list.arg3();
        final IExpr max = list.arg4();
        if (min.isRealResult() && max.isRealResult()) {
          if (min.isNegativeResult()) {
            if (max.isNegativeResult()) {
              result.append(F.List(min.inverse(), lessMin, lessMax, max.inverse()));
            } else {
              result.append(F.List(F.CNInfinity, S.Less, lessMin, min.inverse()));
              if (!max.isZero()) {
                result.append(F.List(max.inverse(), lessMax, S.Less, F.CInfinity));
              }
            }
          } else {
            if (min.isZero()) {
              if (max.isZero()) {
                result.append(F.CRealsIntervalData);
              } else {
                result.append(F.List(max.inverse(), lessMax, S.Less, F.CInfinity));
              }
            } else {
              result.append(F.List(min.inverse(), lessMin, lessMax, max.inverse()));
            }
          }
        } else {
          return F.NIL;
        }
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * 
   *
   * @param expr
   * @return
   */
  public static IAST inverseAbs(IAST expr) {
    if (expr.isIntervalData()) {
      IAST interval = expr;
      if (interval.size() == 1) {
        return interval;
      }
      IASTAppendable result = F.ast(S.IntervalData, interval.argSize());
      EvalEngine engine = EvalEngine.get();
      for (int i = 1; i < interval.size(); i++) {
        IAST list1 = (IAST) interval.get(i);
        if (!list1.isList4()) {
          return F.NIL;
        }
        IExpr min = list1.arg1();
        IBuiltInSymbol minLess = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol maxLess = (IBuiltInSymbol) list1.arg3();
        IExpr max = list1.arg4();
        if (max.isNonNegativeResult()) {
          if (min.isNonNegativeResult()) {
            IASTMutable copy = list1.copy();
            result.append(list1);
            copy.set(1, max.negate());
            copy.set(2, maxLess == S.Less ? S.Less : S.LessEqual);
            copy.set(3, minLess == S.Less ? S.Less : S.LessEqual);
            copy.set(4, min.negate());
            result.append(copy);
            continue;
          } else if (min.isNegativeResult()) {
            IExpr minNegate = min.negate();
            if (engine.evalLess(minNegate, max)) {
              result.append(list1);
              continue;
            }
            if (engine.evalLess(max, minNegate)) {
              IASTMutable copy = list1.copy();
              copy.set(1, max.negate());
              copy.set(2, maxLess == S.Less ? S.Less : S.LessEqual);
              result.append(copy);
              continue;
            }
          }
          return F.NIL;
        } else if (max.isNegativeResult() && min.isNegativeResult()) {
          return F.CEmptyIntervalData;
        } else {
          return F.NIL;
        }
      }
      return normalize(result, engine).orElse(result);
    }
    return F.NIL;
  }


  /**
   * Returns <code>true</code> if the given interval is an empty set, i.e., it contains no
   * intervals. The empty interval set is represented by <code>IntervalData()</code>.
   *
   * @param interval the interval to test
   * @return <code>true</code> if the given interval is an empty set, i.e., it contains no
   *         intervals.
   */
  public static boolean isEmptySet(final IExpr interval) {
    return interval.isAST(S.IntervalData, 1);
  }

  public static boolean isMember(final IAST intervalData, IReal num) {
    for (int i = 1; i < intervalData.size(); i++) {
      IAST intervalList = (IAST) intervalData.get(i);
      if (intervalList.isList4()) {
        IExpr min = intervalList.arg1();
        IBuiltInSymbol lessMin = (IBuiltInSymbol) intervalList.arg2();
        IBuiltInSymbol lessMax = (IBuiltInSymbol) intervalList.arg3();
        IExpr max = intervalList.arg4();
        if (lessMin == S.LessEqual && min.lessEqual(num).isTrue()) {
          if (lessMax == S.LessEqual && max.greaterEqual(num).isTrue()) {
            return true;
          } else if (lessMax == S.Less && max.greater(num).isTrue()) {
            return true;
          }
        } else if (lessMin == S.Less && min.less(num).isTrue()) {
          if (lessMax == S.LessEqual && max.greaterEqual(num).isTrue()) {
            return true;
          } else if (lessMax == S.Less && max.greater(num).isTrue()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * The method test all intervals if they are in the range
   * <code>{-Infinity, LessEqual, Less,0}</code>.
   *
   * @param intervalData the interval to test
   */
  public static boolean isNegativeResult(IAST intervalData) {
    for (int i = 1; i < intervalData.size(); i++) {
      IAST intervalList = (IAST) intervalData.get(i);
      if (intervalList.isList4()) {
        boolean isNegativePart = //
            intervalList.arg1().isNegativeResult() //
                && (intervalList.arg4().isNegativeResult()
                    || (intervalList.arg3() == S.Less && intervalList.arg4().isZero()));
        if (!isNegativePart) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * The method test all intervals if they are in the range
   * <code>{0, LessEqual, LessEqual,Infinity}</code>.
   *
   * @param intervalData the interval to test
   */
  public static boolean isNonNegativeResult(IAST intervalData) {
    for (int i = 1; i < intervalData.size(); i++) {
      IAST intervalList = (IAST) intervalData.get(i);
      if (intervalList.isList4()) {
        boolean isNonNegativePart =
            intervalList.arg1().isNonNegativeResult() && intervalList.arg4().isNonNegativeResult();
        if (!isNonNegativePart) {
          return false;
        }
      }
    }
    return true;
  }


  private static boolean isNormalized(final IAST interval) {
    return interval.isEvalFlagOn(IAST.BUILT_IN_EVALED);
  }

  public static boolean isPositiveResult(IAST intervalData) {
    for (int i = 1; i < intervalData.size(); i++) {
      IAST intervalList = (IAST) intervalData.get(i);
      if (intervalList.isList4()) {
        boolean isPositivePart = //
            intervalList.arg1().isPositiveResult() //
                && intervalList.arg4().isPositiveResult();
        if (!isPositivePart) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns <code>true</code> if the given interval is a union of intervals, i.e., it contains at
   * least two intervals. The method doesn't test if the intervals are normalized (Normalized
   * intervals are sorted and overlapping intervals are merged)
   *
   * @param interval the interval to test
   * @return <code>true</code> if the given interval is a union of intervals, i.e., it contains at
   *         least two intervals.
   * @see #normalize(IAST)
   */
  public static boolean isUnionSet(final IExpr interval) {
    return interval.isASTSizeGE(S.IntervalData, 2);
  }

  public static IAST log(final IAST ast) {
    if (ast.size() == 1 && ast.isIntervalData()) {
      return F.CEmptyIntervalData;
    }
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
        min = S.Log.of(engine, min);
        max = S.Log.of(engine, max);
        result.append(index, F.List(min, lessMin, lessMax, max));
        return true;
      }
      return false;
    });
  }

  /**
   * Return an interval not including the left boundary.
   * 
   * @param lhs left boundary
   * @param rhs right boundary
   * @return <code>IntervalData({lhs, Less, LessEqual, rhs}))</code>
   */
  public static IAST lOpen(final IExpr lhs, final IExpr rhs) {
    return F.IntervalData(//
        F.List(lhs, //
            S.Less, //
            S.LessEqual, //
            rhs));
  }

  /**
   * Return an interval with all interval ranges intersected with
   * <code>F.IntervalData(F.List(F.C0, S.LessEqual, S.Less, F.CInfinity))</code>.
   * 
   * @param expr
   * @return an interval data with positive or equal 0 minimum and positive or equal 0 maximum
   *         value. Otherwise {@link F#NIL} if the expression is not an interval data,
   */
  public static IAST makeNonnegative(IExpr expr) {
    if (expr.isIntervalData()) {
      IAST interval = (IAST) expr;
      if (interval.size() == 1) {
        return interval;
      }
      return intersection(interval, F.IntervalData(F.List(F.C0, S.LessEqual, S.Less, F.CInfinity)),
          EvalEngine.get());
      // IASTAppendable logArg = F.IntervalDataAlloc(interval.size());
      // boolean evaled = false;
      // for (int i = 1; i < interval.size(); i++) {
      // IAST list = (IAST) interval.get(i);
      // IExpr min = list.arg1();
      // // IBuiltInSymbol lessMin = (IBuiltInSymbol) list.arg2();
      // // IBuiltInSymbol lessMax = (IBuiltInSymbol) list.arg3();
      // IExpr max = list.arg4();
      // if (max.isNegativeResult()) {
      // // log of negative value is not defined; skip append
      // evaled = true;
      // continue;
      // }
      // if (min.isNegativeResult() && max.isNonNegativeResult()) {
      // IASTMutable copy = list.copy();
      // copy.set(1, F.C0);
      // copy.set(2, S.LessEqual);
      // logArg.append(copy);
      // evaled = true;
      // continue;
      // }
      // logArg.append(list);
      // }
      // return evaled ? logArg : expr;
    }
    return F.NIL;
  }

  /**
   * Return an interval with all interval ranges intersected with
   * <code>F.IntervalData(F.List(F.CNInfinity, S.Less, S.LessEqual, F.C0))</code>.
   * 
   * @param expr
   * @return an interval data with negative or equal 0 minimum and negative or equal 0 maximum
   *         value. Otherwise {@link F#NIL} if the expression is not an interval data,
   */
  public static IAST makeNonpositive(IExpr expr) {
    if (expr.isIntervalData()) {
      IAST interval = (IAST) expr;
      if (interval.size() == 1) {
        return interval;
      }
      return intersection(interval, F.IntervalData(F.List(F.CNInfinity, S.Less, S.LessEqual, F.C0)),
          EvalEngine.get());
    }
    return F.NIL;
  }

  public static IInteger mapIntegerFunction(ISymbol integerFunctionSymbol, final IAST interval,
      EvalEngine engine) {
    if (interval.isPresent()) {
      IInteger result = null;
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        IExpr minArg = list.arg1();
        IExpr maxArg = list.arg4();
        if (!minArg.isRealResult() || !maxArg.isRealResult()) {
          return null;
        }
        IExpr min = minArg;
        IExpr max = maxArg;

        IExpr lessMin = list.arg2();
        if (lessMin == S.Less && min.isInteger()) {
          min = min.plus(F.QQ(1, 10));
        }
        IExpr lessMax = list.arg3();
        if (lessMax == S.Less && max.isInteger()) {
          max = max.subtract(F.QQ(1, 10));
        }
        IExpr mappedMin = engine.evaluate(F.unaryAST1(integerFunctionSymbol, min));
        IExpr mappedMax = engine.evaluate(F.unaryAST1(integerFunctionSymbol, max));
        if (!mappedMin.equals(mappedMax) || !mappedMin.isInteger()) {
          return null;
        }
        if (result != null && !result.equals(mappedMin)) {
          return null;
        } else {
          result = (IInteger) mappedMin;
        }
      }
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns a quadruple list of the maximum value and the {@link S#Less} or {@link S#LessEqual}
   * symbol associated to the maximum value and the minimum value and the {@link S#Less} or
   * {@link S#LessEqual} symbol associated to the minimum value.
   * 
   * @param value1
   * @param s1
   * @param value2
   * @param s2
   * @param engine the evaluation engine
   * @return a pair of the maximum value and the symbol for the maximum value, or <code>NIL</code>
   *         if both values are equal.
   */
  private static IAST maximum(IExpr value1, IBuiltInSymbol s1, IExpr value2, IBuiltInSymbol s2,
      EvalEngine engine) {
    final IExpr v1 = engine.evaluate(value1);
    final IExpr v2 = engine.evaluate(value2);
    if (v1.greaterEqual(v2).isTrue()) {
      return F.List(v1, s1, v2, s2);
    }
    if (v2.greater(v1).isTrue()) {
      return F.List(v2, s2, v1, s1);
    }
    return F.NIL;
  }

  /**
   * Returns a quadruple list of the minimum value and the {@link S#Less} or {@link S#LessEqual}
   * symbol associated to the minimum value and the maximum value and the {@link S#Less} or
   * {@link S#LessEqual} symbol associated to the maximum value.
   * 
   * @param value1
   * @param s1
   * @param value2
   * @param s2
   * @param engine the evaluation engine
   * @return a pair of the minimum value and the symbol for the minimum value, or <code>NIL</code>
   *         if both values are equal.
   */
  private static IAST minimum(IExpr value1, IBuiltInSymbol s1, IExpr value2, IBuiltInSymbol s2,
      EvalEngine engine) {
    final IExpr v1 = engine.evaluate(value1);
    final IExpr v2 = engine.evaluate(value2);
    if (v1.lessEqual(v2).isTrue()) {
      return F.List(v1, s1, v2, s2);
    }
    if (v2.less(v1).isTrue()) {
      return F.List(v2, s2, v1, s1);
    }
    return F.NIL;
  }

  private static IAST minMax(IExpr min1Min2, IExpr min1Max2, IExpr max1Min2, IExpr max1Max2,
      IBuiltInSymbol[] symbols) {
    int[] index = new int[] {0, 1};
    IExpr min = min1Min2;
    IExpr max = min1Max2;
    if (min1Min2.greaterThan(min1Max2).isTrue()) {
      index[0] = 1;
      index[1] = 0;
      min = min1Max2;
      max = min1Min2;
    }
    if (max1Min2.greaterThan(max).isTrue()) {
      index[1] = 2;
      max = max1Min2;
    } else if (max1Min2.lessThan(min).isTrue()) {
      index[0] = 2;
      min = max1Min2;
    }
    if (max1Max2.greaterThan(max).isTrue()) {
      index[1] = 3;
      max = max1Max2;
    } else if (max1Max2.lessThan(min).isTrue()) {
      index[0] = 3;
      min = max1Max2;
    }
    IBuiltInSymbol left = S.LessEqual;
    switch (index[0]) {
      case 0:
        left = precedence(symbols[0], symbols[1]);
        break;
      case 1:
        left = precedence(symbols[0], symbols[3]);
        break;
      case 2:
        left = precedence(symbols[1], symbols[2]);
        break;
      case 3:
        left = precedence(symbols[1], symbols[3]);
        break;
      default:
        break;
    }
    IBuiltInSymbol right = S.LessEqual;
    switch (index[1]) {
      case 0:
        right = precedence(symbols[0], symbols[1]);
        break;
      case 1:
        right = precedence(symbols[0], symbols[3]);
        break;
      case 2:
        right = precedence(symbols[1], symbols[2]);
        break;
      case 3:
        right = precedence(symbols[1], symbols[3]);
        break;
      default:
        break;
    }
    return F.List(min, left, right, max);
  }

  /**
   * Replaces the most common code. Determines the result depending on the fulfillment of
   * conditions.
   *
   * @param ast
   * @param processors conditions to be met
   * @return IAST result, append value or F.NIL;
   */
  private static IAST mutableProcessorConditions(final IAST ast, IExprProcessor... processors) {
    if (processors != null && processors.length > 0) {
      IAST interval = normalize(ast);
      if (interval.isPresent()) {
        try {
          IASTAppendable result = F.IntervalDataAlloc(interval.size());
          for (int i = 1; i < interval.size(); i++) {
            IAST list = (IAST) interval.get(i);
            IExpr min = list.arg1();
            IBuiltInSymbol lessMin = (IBuiltInSymbol) list.arg2();
            IBuiltInSymbol lessMax = (IBuiltInSymbol) list.arg3();
            IExpr max = list.arg4();

            boolean processed = false;
            for (IExprProcessor processor : processors) {
              processed = processor.apply(min, lessMin, lessMax, max, result, i);
              if (processed) {
                break;
              }
            }

            if (!processed) {
              return F.NIL;
            }
          }
          return result;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          //
        }
      }
    }

    return F.NIL;
  }

  public static IExpr normalizeExpr(final IAST inequality, IExpr variable, EvalEngine engine) {
    IExpr intervalData = toIntervalData(inequality, variable, engine);
    if (intervalData.isIntervalData()) {
      return IntervalDataSym.intervalToOr((IAST) intervalData, variable);
    }
    return F.NIL;
  }

  /**
   * The list of intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @return throw ArgumentTypeException if the interval could not be normalized
   */
  public static IAST normalize(final IAST intervalList) throws ArgumentTypeException {
    if (isNormalized(intervalList)) {
      return intervalList;
    }
    IAST norm = normalize(intervalList, EvalEngine.get());
    if (norm.isPresent()) {
      return norm;
    }
    if (isNormalized(intervalList)) {
      return intervalList;
    }
    String str = Errors.getMessage("nvld", F.list(intervalList));
    throw new ArgumentTypeException(str);
  }


  /**
   * The list of open/close ended intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @param engine
   * @return {@link F#NIL} if the interval is already normalized.
   * @throws ArgumentTypeException if the interval could not be normalized.
   */
  private static IAST normalize(final IAST intervalList, EvalEngine engine)
      throws ArgumentTypeException {
    if (!intervalList.isAST(S.IntervalData)) {
      return F.IntervalData(F.List(intervalList, S.LessEqual, S.LessEqual, intervalList));
    }
    IASTAppendable result = intervalList.copyAppendable();
    boolean evaled = false;
    int i = 1;
    while (i < result.size()) {
      IAST temp = normalizeArgument(intervalList.get(i), engine);
      if (temp.isPresent()) {
        evaled = true;
        if (temp.isEmptyList()) {
          result.remove(i);
          continue;
        }
        result.set(i, temp);
      }
      i++;
    }
    if (EvalAttributes.sort(result, INTERVAL_COMPARATOR)) {
      evaled = true;
    }

    if (result.size() > 2) {
      int j = 1;
      IAST list1 = (IAST) result.arg1();
      IExpr min1 = list1.arg1();
      IBuiltInSymbol left1 = (IBuiltInSymbol) list1.arg2();
      IBuiltInSymbol right1 = (IBuiltInSymbol) list1.arg3();
      IExpr max1 = list1.arg4();
      i = 2;
      while (i < result.size()) {
        IAST list2 = (IAST) result.get(i);
        IExpr min2 = list2.arg1();
        IBuiltInSymbol left2 = (IBuiltInSymbol) list2.arg2();
        IBuiltInSymbol right2 = (IBuiltInSymbol) list2.arg3();
        IExpr max2 = list2.arg4();
        if (max1.lessEqual(min2).isTrue()) {
          if (S.Equal.ofQ(max1, min2)) {
            if (precedenceUnion(right1, left2) == S.Less) {
              result.set(j++, list1);
              list1 = list2;
              min1 = list1.arg1();
              left1 = (IBuiltInSymbol) list1.arg2();
              right1 = (IBuiltInSymbol) list1.arg3();
              max1 = list1.arg4();
              i++;
              continue;
            }
          } else {
            result.set(j++, list1);
            list1 = list2;
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            i++;
            continue;
          }
        }
        if (min2.lessEqual(max1).isTrue()) {
          if (min1.lessEqual(min2).isTrue()) {
            if (max1.lessEqual(max2).isTrue()) {
              evaled = true;
              result.remove(i);
              IBuiltInSymbol lNew = S.Equal.ofQ(min1, min2) ? precedenceUnion(left1, left2) : left1;
              IBuiltInSymbol rNew =
                  S.Equal.ofQ(max1, max2) ? precedenceUnion(right1, right2) : right2;
              list1 = F.List(min1, lNew, rNew, max2);
              min1 = list1.arg1();
              left1 = (IBuiltInSymbol) list1.arg2();
              right1 = (IBuiltInSymbol) list1.arg3();
              max1 = list1.arg4();
              continue;
            } else if (max2.less(max1).isTrue()) {
              evaled = true;
              result.remove(i);
              IBuiltInSymbol lNew = S.Equal.ofQ(min1, min2) ? precedenceUnion(left1, left2) : left1;
              list1 = F.List(min1, lNew, right1, max1);
              min1 = list1.arg1();
              left1 = (IBuiltInSymbol) list1.arg2();
              right1 = (IBuiltInSymbol) list1.arg3();
              max1 = list1.arg4();
              continue;
            }
          }
        }

        // The expression `1` is not a valid interval.
        throw new ArgumentTypeException("nvld", F.list(intervalList.get(i)));
      }
      result.set(j, list1);
    }
    if (evaled) {
      result.builtinEvaled();
      return result;
    }
    if (intervalList instanceof IASTMutable) {
      intervalList.builtinEvaled();
      if (EvalAttributes.sort((IASTMutable) intervalList, INTERVAL_COMPARATOR)) {
        return intervalList;
      }
    }
    return F.NIL;
  }

  /**
   * If the argument is a list of 2 elements, try sorting the elements. If the argument is not a
   * list return a new <code>{argOfIntervalList, argOfIntervalList]</code>
   *
   * @param arg
   * @param engine
   * @return throw ArgumentTypeException if the interval could not be normalized. {@link F#NIL} if
   *         the interval doesn't need to be normalized. {@link F#CEmptyList} if the interval is
   *         empty.
   */
  private static IAST normalizeArgument(final IExpr arg, final EvalEngine engine)
      throws ArgumentTypeException {
    if (arg.isList()) {
      if (arg.argSize() == 4) {
        try {
          IAST list = (IAST) arg;
          IExpr arg1 = list.arg1();
          IBuiltInSymbol left = (IBuiltInSymbol) list.arg2();
          if (left != S.Less && left != S.LessEqual) {
            String str = Errors.getMessage("nvld", F.list(arg), engine);
            throw new ArgumentTypeException(str);
          }
          IBuiltInSymbol right = (IBuiltInSymbol) list.arg3();
          if (right != S.Less && right != S.LessEqual) {
            String str = Errors.getMessage("nvld", F.list(arg), engine);
            throw new ArgumentTypeException(str);
          }
          IExpr arg4 = list.arg4();
          if (arg1.isReal() && arg4.isReal()) {
            if (arg1.greaterThan(arg4).isTrue()) {
              return F.List(arg4, //
                  list.arg3(), //
                  list.arg2(), //
                  arg1);
            }
            if (S.Equal.ofQ(arg1, arg4)) {
              if (left == S.Less && right == S.Less) {
                return F.CEmptyList;
              }
              if (left == S.Less || right == S.Less) {
                return F.List(arg1, //
                    S.LessEqual, //
                    S.LessEqual, //
                    arg4);
              }
            }
            return F.NIL;
          }
          IExpr min = arg1.isNumber() ? arg1 : engine.evaluate(arg1);
          IExpr max = arg4.isNumber() ? arg4 : engine.evaluate(arg4);
          if (min.isRealResult() && max.isRealResult()) {
            if (min.greaterThan(max).isTrue()) {
              return F.List(max, //
                  list.arg3(), //
                  list.arg2(), //
                  min);
            }
          }
          boolean evaled = false;
          if (min.isInfinity() || min.isNegativeInfinity()) {
            if (min.equals(max)) {
              left = S.Less;
              right = S.Less;
              evaled = true;
              // String str = Errors.getMessage("nvld", F.list(arg), engine);
              // throw new ArgumentTypeException(str);
            } else {
              left = S.Less;
              evaled = true;
            }
          }
          if (max.isInfinity() || max.isNegativeInfinity()) {
            right = S.Less;
            evaled = true;
          }
          if (evaled) {
            return F.List(min, //
                left, //
                right, //
                max);
          }
          return F.NIL;
        } catch (ClassCastException cce) {
          String str = Errors.getMessage("nvld", F.list(arg), engine);
          throw new ArgumentTypeException(str);
        }
      }
      // The expression `1` is not a valid interval.
      String str = Errors.getMessage("nvld", F.list(arg), engine);
      throw new ArgumentTypeException(str);
    }
    if (arg instanceof INum) {
      if (arg instanceof ApfloatNum) {
        return intervalList((ApfloatNum) arg);
      }
      double value = ((IReal) arg).doubleValue();
      return F.List(F.num(Math.nextDown(value)), //
          S.LessEqual, //
          S.LessEqual, //
          F.num(Math.nextUp(value)));
    }
    return F.List(arg, //
        S.LessEqual, //
        S.LessEqual, //
        arg);
  }

  public static IAST notInRange(final IExpr arg) {
    return F.IntervalData(//
        F.List(F.CNInfinity, //
            S.Less, //
            S.Less, //
            arg), //
        F.List(arg, //
            S.Less, //
            S.Less, //
            F.CInfinity));
  }

  /**
   * Return an interval including neither boundary.
   * 
   * @param lhs left boundary
   * @param rhs right boundary
   * @return <code>IntervalData({lhs, Less, Less, rhs}))</code>
   */
  public static IAST open(final IExpr lhs, final IExpr rhs) {
    return F.IntervalData(//
        F.List(lhs, //
            S.Less, //
            S.Less, //
            rhs));
  }

  public static IExpr plus(final IAST ast1, final IAST ast2) {
    IAST interval1 = normalize(ast1);
    IAST interval2 = normalize(ast2);
    if (interval1.isPresent() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval1.size() * interval2.size());
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg4();
        IBuiltInSymbol left1 = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol right1 = (IBuiltInSymbol) list1.arg3();

        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg4();
          IAST list = F.List(min1.plus(min2), //
              precedence(left1, (IBuiltInSymbol) list2.arg2()), //
              precedence(right1, (IBuiltInSymbol) list2.arg3()), //
              max1.plus(max2));
          result.append(list);
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr plus(final IExpr scalar, final IAST ast2) {
    IAST interval2 = normalize(ast2);
    if (scalar.isRealResult() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval2.size());
      for (int j = 1; j < interval2.size(); j++) {
        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IExpr max2 = list2.arg4();

        IAST list = F.List(scalar.plus(min2), //
            list2.arg2(), //
            list2.arg3(), //
            scalar.plus(max2));
        result.append(list);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Calculate <code>Interval({lower, upper},...,...) ^ exponent</code>.
   *
   * <p>
   * See: <a href= "https://en.wikipedia.org/wiki/Interval_arithmetic#Elementary_functions">Interval
   * arithmetic - Elementary functions</a>
   *
   * @param baseInterval
   * @param exponent
   * @return
   */
  public static IExpr power(final IAST baseInterval, IInteger exponent) {
    IAST interval = normalize(baseInterval);
    if (interval.isPresent()) {
      boolean negative = false;

      if (exponent.isNegative()) {
        negative = true;
        exponent = exponent.negate();
      }
      if (exponent.isOne()) {
        if (negative) {
          return inverse(interval);
        }
        return baseInterval;
      }
      IASTAppendable result = F.IntervalDataAlloc(baseInterval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        final IExpr min = list.arg1();
        final IBuiltInSymbol lessMin = (IBuiltInSymbol) list.arg2();
        final IBuiltInSymbol lessMax = (IBuiltInSymbol) list.arg3();
        final IExpr max = list.arg4();
        if (min.isRealResult() && max.isRealResult()) {
          final IExpr minPower = min.power(exponent);
          final IExpr maxPower = max.power(exponent);
          if (exponent.isEven()) {
            if (min.isNonNegativeResult()) {
              result.append(F.List(minPower, lessMin, lessMax, maxPower));
            } else {
              if (max.isNegativeResult()) {
                result.append(F.List(maxPower, lessMax, lessMin, minPower));
              } else {
                final IExpr isGreater = minPower.greater(maxPower);
                final IBuiltInSymbol newRelation =
                    isGreater.isTrue() ? lessMin : isGreater.isFalse() ? lessMax : null;
                if (newRelation == null) {
                  return F.NIL;
                }
                result.append(F.List(F.C0, S.LessEqual, newRelation,
                    isGreater.isTrue() ? minPower : maxPower));
              }
            }
          } else {
            result.append(F.List(minPower, lessMin, lessMax, maxPower));
          }
        } else {
          return F.NIL;
        }
      }
      if (negative) {
        return F.Power(result, F.CN1);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Calculate <code>Interval({lower, upper},...,...) ^ exponent</code>.
   *
   * <p>
   * See: <a href= "https://en.wikipedia.org/wiki/Interval_arithmetic#Elementary_functions">Interval
   * arithmetic - Elementary functions</a>
   *
   * @param baseInterval
   * @param exponent
   * @return
   */
  public static IExpr power(final IAST baseInterval, IReal exponent) {
    IAST interval = normalize(baseInterval);
    if (interval.isPresent()) {
      boolean negative = false;

      if (exponent.isNegative()) {
        negative = true;
        exponent = exponent.negate();
      }
      if (exponent.isOne()) {
        if (negative) {
          return inverse(interval);
        }
        return baseInterval;
      }
      IASTAppendable result = F.IntervalDataAlloc(baseInterval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        final IExpr min = list.arg1();
        final IExpr lessMin = list.arg2();
        final IExpr lessMax = list.arg3();
        final IExpr max = list.arg4();
        if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
          result.append(F.List(min.power(exponent), lessMin, lessMax, max.power(exponent)));
        } else {
          return F.NIL;
        }
      }
      if (negative) {
        return F.Power(result, F.CN1);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Calculate <code>base ^ intervalExponent</code>.
   *
   * @param base
   * @param intervalExponent
   * @return <code>F.NIL</code> if no evauation is possible.
   */
  public static IExpr power(IExpr base, final IAST intervalExponent) {
    IAST interval = normalize(intervalExponent);
    if (interval.isPresent()) {
      if (base.isNegative()) {
        if (base.isMinusOne()) {
          return F.NIL;
        }
        return F.Times(F.Power(-1, intervalExponent), F.Power(base.negate(), intervalExponent));
      }
      IASTAppendable result = F.IntervalDataAlloc(intervalExponent.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        IExpr min = list.arg1();
        final IExpr lessMin = list.arg2();
        final IExpr lessMax = list.arg3();
        IExpr max = list.arg4();
        if (base.isZero()) {
          if (min.isNegativeResult() || max.isNegativeResult()) {
            return S.Indeterminate;
          }
        }
        result.append(F.List(base.power(min), lessMin, lessMax, base.power(max)));
      }
      return result;
    }
    return F.NIL;
  }

  private static IBuiltInSymbol precedence(IBuiltInSymbol s1, IBuiltInSymbol s2) {
    return (s1 == S.Less || s2 == S.Less) ? S.Less : S.LessEqual;
  }

  private static IBuiltInSymbol precedenceUnion(IBuiltInSymbol s1, IBuiltInSymbol s2) {
    return (s1 == S.LessEqual || s2 == S.LessEqual) ? S.LessEqual : S.Less;
  }

  /**
   * The {@link S#Reals} domain is represented by <code>IntervalData({-oo, Less, Less, oo})</code>.
   * 
   * @return
   */
  public static IAST reals() {
    return F.IntervalData(//
        F.List(F.CNInfinity, //
            S.Less, //
            S.Less, //
            F.CInfinity));
  }

  public static IAST relationToIntervalSet(IAST relation, IExpr variable) {
    int headID = relation.headID();
    if (relation.isAST2()) {
      IExpr lhs = relation.arg1();
      IExpr rhs = relation.arg2();
      if (variable.equals(lhs) && !variable.equals(rhs)) {
        return relationToIntervalSet(headID, rhs);
      } else if (variable.equals(rhs) && !variable.equals(lhs)) {
        IExpr value = lhs;
        // value <relation> symbol
        switch (headID) {
          case ID.Greater:
            return open(F.CNInfinity, value);
          case ID.GreaterEqual:
            return lOpen(F.CNInfinity, value);
          case ID.Less:
            return open(value, F.CInfinity);
          case ID.LessEqual:
            return rOpen(value, F.CInfinity);
          case ID.Equal:
            return close(value, value);
          case ID.Unequal:
            return F.IntervalData(//
                F.List(F.CNInfinity, S.Less, S.Less, value), //
                F.List(value, S.Less, S.Less, F.CInfinity));
        }
      }
    } else if (relation.isAST3()) {
      IExpr lhs = relation.arg1();
      IExpr rhs = relation.arg3();
      if (variable.equals(relation.arg2()) && !variable.equals(lhs) && !variable.equals(rhs)) {
        switch (headID) {
          case ID.Greater:
            return F.IntervalData(//
                F.List(rhs, //
                    S.Less, //
                    S.Less, //
                    lhs));
          case ID.GreaterEqual:
            return F.IntervalData(//
                F.List(rhs, //
                    S.LessEqual, //
                    S.LessEqual, //
                    lhs));
          case ID.Less:
            return F.IntervalData(//
                F.List(lhs, //
                    S.Less, //
                    S.Less, //
                    rhs));
          case ID.LessEqual:
            return F.IntervalData(//
                F.List(lhs, //
                    S.LessEqual, //
                    S.LessEqual, //
                    rhs));
        }
      }
    } else if (relation.argSize() == 5) {
      if (headID == ID.Inequality) {
        IExpr lhs = relation.arg1();
        IExpr op1 = relation.arg2();
        IExpr v = relation.arg3();
        IExpr op2 = relation.arg4();
        IExpr rhs = relation.arg5();
        if (variable.equals(v) && !variable.equals(lhs) && !variable.equals(rhs)) {
          if ((op1 == S.Less || op1 == S.LessEqual) && (op2 == S.Less || op2 == S.LessEqual)) {
            return F.IntervalData(//
                F.List(lhs, //
                    op1, //
                    op2, //
                    rhs));
          }
        }
      }
    }
    return F.NIL;
  }

  public static IAST relationToIntervalSet(int headID, IExpr value) {
    switch (headID) {
      case ID.Greater:
        return open(value, F.CInfinity);
      case ID.GreaterEqual:
        return rOpen(value, F.CInfinity);
      case ID.Less:
        return open(F.CNInfinity, value);
      case ID.LessEqual:
        return lOpen(F.CNInfinity, value);
      case ID.Equal:
        return close(value, value);
      case ID.Unequal:
        return F.IntervalData(//
            F.List(F.CNInfinity, S.Less, S.Less, value), //
            F.List(value, S.Less, S.Less, F.CInfinity));
    }
    throw new ArgumentTypeStopException("Not implemented");
  }

  public static IAST intervalToIntervalSet(IAST interval) {
    if (interval.isInterval()) {
      IASTAppendable result = F.IntervalDataAlloc(interval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        result.append(F.List(list.arg1(), S.LessEqual, S.LessEqual, list.arg2()));
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Return an interval not including the right boundary.
   * 
   * @param lhs left boundary
   * @param rhs right boundary
   * @return <code>IntervalData({lhs, LessEqual, Less, rhs}))</code>
   */
  public static IAST rOpen(final IExpr lhs, final IExpr rhs) {
    return F.IntervalData(//
        F.List(lhs, //
            S.LessEqual, //
            S.Less, //
            rhs));
  }

  public static IAST sec(final IAST ast) {
    IAST interval = cos(ast);
    if (interval.isPresent()) {
      return inverse(interval);
    }
    return F.NIL;
  }

  public static IAST sech(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.List(F.Sech(max), lessMin, lessMax, F.Sech(min)));
        } else if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          IAST list4 = minimum(F.Sech(min), lessMin, F.Sech(max), lessMax, engine);
          if (list4.isNIL()) {
            return false;
          }
          result.append(F.List(list4.arg1(), list4.arg2(), list4.arg4(), F.C1));
        } else if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
          result.append(F.List(F.Sech(min), lessMin, lessMax, F.Sech(max)));
        }
        return true;
      }
      return false;
    });
  }

  /**
   * Create two intervals {@link F#IntervalData(IAST...)} from the relations
   * <code>F.binaryAST2(relationalSymbol1, expr, value1)</code> and from
   * <code>F.binaryAST2(relationalSymbol2, expr, value2)</code> and intersect the two intervals if
   * possible
   * 
   * @param lhs
   * @param relationalSymbol1
   * @param rhsValue1
   * @param relationalSymbol2
   * @param rhsValue2
   * @param variable
   * @param engine
   * @return
   */
  public static IAST simplifyRelationToInterval(IExpr lhs, IBuiltInSymbol relationalSymbol1,
      IExpr rhsValue1, IBuiltInSymbol relationalSymbol2, IExpr rhsValue2, IExpr variable,
      EvalEngine engine) throws ArgumentTypeStopException {
    IAST f1 = simplifyRelationToInterval(lhs, relationalSymbol1, rhsValue1, variable, engine);
    IAST f2 = simplifyRelationToInterval(lhs, relationalSymbol2, rhsValue2, variable, engine);
    if (f1.isPresent() && f2.isPresent()) {
      IExpr temp = F.IntervalIntersection.of(engine, f1, f2);
      if (temp.isIntervalData()) {
        return (IAST) temp;
      }
    }
    throw new ArgumentTypeStopException("IntervalIntersection failed");
  }

  /**
   * Create an {@link F#IntervalData(IAST...)} from the relation
   * <code>F.binaryAST2(relationalSymbol, expr, value)</code>.
   * 
   * @param lhs
   * @param relationalSymbol one of the symbols
   *        {@link S#Greater},{@link S#GreaterEqual}{@link S#Less}{@link S#LessEqual}{@link S#Unequal}
   * @param rhsValue
   * @param variable
   * @param engine
   * @return
   */
  public static IAST simplifyRelationToInterval(IExpr lhs, IBuiltInSymbol relationalSymbol,
      IExpr rhsValue, IExpr variable, EvalEngine engine) throws ArgumentTypeStopException {
    IExpr temp = engine.evaluate(F.Simplify(F.binaryAST2(relationalSymbol, lhs, rhsValue)));
    if (temp.isAST2() && temp.first().equals(variable)) {
      IExpr rhs = temp.second();
      int headID = temp.headID();
      switch (headID) {
        case ID.Greater:
          return open(rhs, F.CInfinity);
        case ID.GreaterEqual:
          return rOpen(rhs, F.CInfinity);
        case ID.Less:
          return open(F.CNInfinity, rhs);
        case ID.LessEqual:
          return lOpen(F.CNInfinity, rhs);
        case ID.Equal:
          return close(rhs, rhs);
        case ID.Unequal:
          return F.IntervalData(//
              F.List(F.CNInfinity, S.Less, S.Less, rhs), //
              F.List(rhs, S.Less, S.Less, F.CInfinity));
      }
    }
    throw new ArgumentTypeStopException("Not implemented");
  }

  public static IAST sin(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreater(difference, F.C2Pi)) {
        // difference >= 2 * Pi
        result.append(index, F.List(F.CN1, S.LessEqual, S.LessEqual, F.C1));
      } else {
        // slope from 1st derivative
        double dMin = engine.evalDouble(F.Cos(min));
        double dMax = engine.evalDouble(F.Cos(max));
        if (engine.evalLessEqual(difference, S.Pi)) {
          if (dMin >= 0) {
            if (dMax >= 0) {
              result.append(index, F.List(F.Sin(min), lessMin, lessMax, F.Sin(max)));
            } else {
              IAST list4 = minimum(F.Sin(min), lessMin, F.Sin(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(list4.arg1(), list4.arg2(), list4.arg4(), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.List(F.Sin(max), lessMax, lessMin, F.Sin(min)));
            } else {
              IAST list4 = maximum(F.Sin(min), lessMin, F.Sin(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(F.CN1, list4.arg4(), list4.arg2(), list4.arg1()));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.List(F.CN1, lessMin, lessMax, F.C1));
            } else {
              IAST list4 = minimum(F.Sin(min), lessMin, F.Sin(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(list4.arg1(), list4.arg2(), list4.arg4(), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.List(F.CN1, lessMin, lessMax, F.C1));
            } else {
              IAST list4 = maximum(F.Sin(min), lessMin, F.Sin(max), lessMax, engine);
              if (list4.isNIL()) {
                return false;
              }
              result.append(index, F.List(F.CN1, list4.arg4(), list4.arg2(), list4.arg1()));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST sinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.List(F.Sinh(min), lessMin, lessMax, F.Sinh(max)));
        return true;
      }
      return false;
    });
  }

  public static IAST tan(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, S.Pi)) {
        result.append(F.CRealsRange);
      } else {
        double dMin = engine.evalDouble(F.Tan(min));
        double dMax = engine.evalDouble(F.Tan(max));
        if (engine.evalLessEqual(difference, F.CPiHalf)) {
          // difference <= 1/2*Pi
          if (dMin >= 0) {
            if (dMax < 0) {
              result.append(F.List(F.CNInfinity, S.Less, lessMax, F.Tan(max)));
              result.append(F.List(F.Tan(min), lessMin, S.Less, F.CInfinity));
            } else {
              result.append(F.List(F.Tan(min), lessMin, lessMax, F.Tan(max)));
            }
          } else {
            result.append(F.List(F.Tan(min), lessMin, lessMax, F.Tan(max)));
          }
        } else { // difference between {Pi/2, Pi}
          if (dMin >= 0) {
            if ((dMax < 0) || (dMin > dMax)) {
              result.append(F.List(F.CNInfinity, S.Less, lessMax, F.Tan(max)));
              result.append(F.List(F.Tan(min), lessMin, S.Less, F.CInfinity));
            } else {
              result.append(F.CRealsRange);
            }
          } else {
            if (dMax < 0) {
              if (dMin <= dMax) {
                result.append(F.CRealsRange);
              } else {
                result.append(F.List(F.CNInfinity, S.Less, lessMax, F.Tan(max)));
                result.append(F.List(F.Tan(min), lessMin, S.Less, F.CInfinity));
              }
            } else {
              result.append(F.List(F.Tan(min), lessMin, lessMax, F.Tan(max)));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST tanh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, lessMin, lessMax, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.List(F.Tanh(min), lessMin, lessMax, F.Tanh(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr times(final IAST ast1, final IAST ast2) {
    IAST interval1 = normalize(ast1);
    IAST interval2 = normalize(ast2);
    if (interval1.isPresent() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval1.size() * interval2.size());
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IBuiltInSymbol sMin1 = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol sMax1 = (IBuiltInSymbol) list1.arg3();
        IExpr max1 = list1.arg4();

        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IBuiltInSymbol sMin2 = (IBuiltInSymbol) list2.arg2();
          IBuiltInSymbol sMax2 = (IBuiltInSymbol) list2.arg3();
          IExpr max2 = list2.arg4();

          IExpr min1Min2 = min1.times(min2);
          IExpr min1Max2 = min1.times(max2);
          IExpr max1Min2 = max1.times(min2);
          IExpr max1Max2 = max1.times(max2);
          IAST list = minMax(min1Min2, min1Max2, max1Min2, max1Max2,
              new IBuiltInSymbol[] {sMin1, sMax1, sMin2, sMax2});
          result.append(list);
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr times(final IExpr scalar, final IAST ast2) {
    IAST interval2 = normalize(ast2);
    if (scalar.isRealResult() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval2.size());
      for (int j = 1; j < interval2.size(); j++) {
        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IExpr max2 = list2.arg4();
        IExpr min = scalar.times(min2);
        IExpr max = scalar.times(max2);
        IAST list;
        if (min.greaterThan(max).isTrue()) {
          list = F.List( //
              max, //
              list2.arg3(), //
              list2.arg2(), //
              min);
        } else {
          list = F.List( //
              min, //
              list2.arg2(), //
              list2.arg3(), //
              max);
        }
        result.append(list);
      }
      return result;
    }
    return F.NIL;
  }

  private static IBuiltInSymbol toggle(IExpr value, IBuiltInSymbol symbol) {
    if (symbol == S.Less) {
      if (value.isInfinity() || value.isNegativeInfinity()) {
        return S.Less;
      }
      return S.LessEqual;
    }
    if (symbol == S.LessEqual) {
      return S.Less;
    }
    return symbol;
  }

  /**
   * Convert the <code>logicOrDomainExpr</code> to a {@link F#IntervalData(IAST...)} expression.
   * 
   * @param logicOrDomainExpr the logical expression or domain expression to convert
   * @param engine the evaluation engine
   * @return an {@link F#IntervalData(IAST...)} expression representing the interval data, or
   *         {@link F#NIL} if the conversion is not applicable or fails.
   */
  public static IExpr toIntervalData(IExpr logicOrDomainExpr, EvalEngine engine) {
    return toIntervalData(logicOrDomainExpr, S.Nonexistent, engine);
  }

  /**
   * Convert the <code>logicOrDomainExpr</code> to a {@link F#IntervalData(IAST...)} expression.
   * 
   * 
   * @param logicOrDomainExpr the logical expression or domain expression to convert
   * @param variable the variable to use in the conversion, if applicable
   * @param engine the evaluation engine
   * @return an {@link F#IntervalData(IAST...)} expression representing the interval data, or
   *         {@link F#NIL} if the conversion is not applicable or fails.
   */
  public static IExpr toIntervalData(final IExpr logicOrDomainExpr, final IExpr variable,
      final EvalEngine engine) {
    if (logicOrDomainExpr.isASTSizeGE(S.Or, 2)) {
      IAST orAST = (IAST) logicOrDomainExpr;
      if (orAST.argSize() == 1) {
        return toIntervalData(orAST.arg1(), variable, engine);
      }
      IExpr orInterval = orAST.mapThread(x -> toIntervalData(x, variable, engine));
      if (orInterval.isOr()) {
        return IntervalDataSym.intervalDataUnion((IAST) orInterval, engine);
      }
    } else if (logicOrDomainExpr.isASTSizeGE(S.And, 2)) {
      IAST andAST = (IAST) logicOrDomainExpr;
      if (andAST.argSize() == 1) {
        return toIntervalData(andAST.arg1(), variable, engine);
      }
      // IASTAppendable list = F.ast(S.IntervalData, andAST.argSize());
      // for (int i = 1; i < andAST.size(); i++) {
      // IExpr temp = toIntervalData(andAST.get(i), variable, engine);
      // if (temp.isNIL()) {
      // return F.NIL;
      // }
      // list.append(temp);
      // }
      IExpr andInterval = andAST.mapThread(x -> toIntervalData(x, variable, engine));
      if (andInterval.isAnd()) {
        return IntervalDataSym.intervalDataIntersection((IAST) andInterval, engine);
      }
    } else if (logicOrDomainExpr.isNot()) {
      IAST notAST = (IAST) logicOrDomainExpr;
      IExpr interval = toIntervalData(notAST.arg1(), variable, engine);
      if (interval.isIntervalData()) {
        return IntervalDataSym.intervalDataComplement(reals(), (IAST) interval, engine);
      }
    } else if (logicOrDomainExpr.isRelationalBinary()) {
      final IAST reducedIntervalData =
          ReduceVariableIntervalSet.reduce((IAST) logicOrDomainExpr, variable, false, engine);
      if (reducedIntervalData.isPresent()) {
        return reducedIntervalData;
      } else {
        return relationToIntervalSet((IAST) logicOrDomainExpr, variable);
      }
    } else if (logicOrDomainExpr.isRelational()) {
      return relationToIntervalSet((IAST) logicOrDomainExpr, variable);
    } else if (logicOrDomainExpr == S.Reals) {
      return F.CRealsIntervalData;
    }
    return F.NIL;
  }

  /**
   * Convert an interval set with only one sub-interval to a single point if the first value of the
   * sub-interval list equals the fourth value of the sub-interval list.
   * 
   * @param interval the interval to convert
   * @return the single point if the interval is a single point, otherwise {@link F#NIL}
   */
  public static IExpr toSinglePoint(IAST interval) {
    if (interval.isIntervalData() && interval.argSize() == 1) {
      IAST list4 = (IAST) interval.first();
      if (list4.arg1().equals(list4.arg4())) {
        return list4.arg1();
      }
    }
    return F.NIL;
  }



  public static IAST union(final IAST interval1, final IAST interval2) {
    return union(interval1, interval2, EvalEngine.get());
  }

  public static IAST union(final IAST interval1, final IAST interval2, EvalEngine engine) {
    IASTAppendable result = F.ast(S.IntervalData, interval1.size() + interval2.size());
    result.appendArgs(interval1);
    result.appendArgs(interval2);

    IAST normalized = normalize(result, engine);
    return normalized.orElse(result);
  }
}
