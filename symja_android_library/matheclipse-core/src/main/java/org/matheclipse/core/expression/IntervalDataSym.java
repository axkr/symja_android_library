package org.matheclipse.core.expression;

import java.util.Comparator;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IReal;

/**
 * <p>
 * Intervals will be represented by objects with head {@link S#IntervalData} wrapped around a
 * sequence of quadruples of the form, e.g., <code>{a,Less,LessEqual,b}</code> representing the half
 * open interval <code>(a,b]</code>. The empty interval set is represented by
 * <code>IntervalData()</code>.
 * 
 * <p>
 * See: <a href=
 * "https://mathematica.stackexchange.com/questions/162486/operating-with-real-intervals/162505#162505">162486/operating-with-real-intervals/162505#162505</a>
 *
 */
public class IntervalDataSym {

  private static final Comparator<IExpr> INTERVAL_COMPARATOR = new Comparator<IExpr>() {
    @Override
    public int compare(IExpr o1, IExpr o2) {
      IAST list1 = (IAST) o1;
      IAST list2 = (IAST) o2;
      if (list1.arg1().equals(list2.arg1())) {
        if (list1.arg4().equals(list2.arg4())) {
          return 0;
        }

        return (list1.arg4().greater(list2.arg4()).isTrue()) ? 1 : -1;
        // return (S.Greater.ofQ(list1.arg4(), list2.arg4())) ? 1 : -1;
      }
      return (list1.arg1().greater(list2.arg1()).isTrue()) ? 1 : -1;
      // return (S.Greater.ofQ(list1.arg1(), list2.arg1())) ? 1 : -1;
    }
  };

  /**
   * Returns the intersection of two intervals.
   * 
   * @param interval1 the first interval
   * @param interval2 the second interval
   * @param engine the evaluation engine
   * @return the intersection of the two intervals
   */
  public static IAST intersectionIntervalData(final IAST interval1, final IAST interval2,
      EvalEngine engine) {
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

  private static Apfloat[] interval(Apfloat x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return new Apfloat[] {h.nextDown(x), h.nextUp(x)};
  }

  /**
   * Rewrite an interval in terms of inequalities and logic operators.
   * 
   * @param andCopy
   * @param interval
   * @param variable
   * @return
   */
  public static IExpr intervalToOr(IAST andCopy, IAST interval, IExpr variable) {
    IASTAppendable orAST = F.ast(S.Or, interval.argSize());
    for (int i = 1; i < interval.size(); i++) {
      IAST list = (IAST) interval.get(i);
      if (list.isEmptyList() || list.argSize() != 4) {
        return S.False;
      }
      IASTAppendable andArg = andCopy.copyAppendable();
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
          andArg.append(F.binaryAST2(list.arg2(), list.arg1(), variable));
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

  private static boolean isNormalized(final IAST interval) {
    return interval.isEvalFlagOn(IAST.BUILT_IN_EVALED);
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
   * The list of intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @return <code>F.NIL</code> if the interval could not be normalized
   */
  public static IAST normalize(final IAST intervalList) {
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
    return F.NIL;
  }

  /**
   * The list of open/close ended intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @param engine
   * @return {@link F#INVALID} if the interval could not be normalized. {@link F#NIL} if the
   *         interval could not be normalized.
   */
  public static IAST normalize(final IAST intervalList, EvalEngine engine) {
    IASTAppendable result = intervalList.copyAppendable();
    boolean evaled = false;
    int i = 1;
    while (i < result.size()) {
      IAST temp = normalizeArgument(intervalList.get(i), engine);
      if (temp.isInvalid()) {
        // The expression `1` is not a valid interval.
        Errors.printMessage(S.IntervalData, "nvld", F.list(intervalList.get(i)), engine);
        return temp;
      }
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
    result.addEvalFlags(IAST.BUILT_IN_EVALED);
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
        Errors.printMessage(S.IntervalData, "nvld", F.list(intervalList.get(i)), engine);
        return F.INVALID;
      }
      result.set(j, list1);
    }
    if (evaled) {
      return result;
    }
    if (intervalList instanceof IASTMutable) {
      intervalList.addEvalFlags(IAST.BUILT_IN_EVALED);
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
   * @return {@link F#INVALID} if the interval could not be normalized. {@link F#NIL} if the
   *         interval doesn't need to be normalized. {@link F#CEmptyList} if the interval is empty.
   */
  private static IAST normalizeArgument(final IExpr arg, final EvalEngine engine) {
    if (arg.isList()) {
      if (arg.argSize() == 4) {
        try {
          IAST list = (IAST) arg;
          IExpr arg1 = list.arg1();
          IBuiltInSymbol left = (IBuiltInSymbol) list.arg2();
          if (left != S.Less && left != S.LessEqual) {
            return F.INVALID;
          }
          IBuiltInSymbol right = (IBuiltInSymbol) list.arg3();
          if (right != S.Less && right != S.LessEqual) {
            return F.INVALID;
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
              return F.INVALID;
            }
            left = S.Less;
            evaled = true;
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
          // wrong IntervalData
        }
      }
      // The expression `1` is not a valid interval.
      return F.INVALID;
    }
    if (arg instanceof INum) {
      if (arg instanceof ApfloatNum) {
        Apfloat apfloat = ((ApfloatNum) arg).fApfloat;
        Apfloat[] values = interval(apfloat);
        return F.List(F.num(values[0]), //
            S.LessEqual, //
            S.LessEqual, //
            F.num(values[1]));
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

  /**
   * The empty interval set is represented by <code>IntervalData()</code>.
   * 
   * @return
   */
  public static IAST emptySet() {
    return F.IntervalData();
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

  private static IBuiltInSymbol precedence(IBuiltInSymbol s1, IBuiltInSymbol s2) {
    return (s1 == S.Less || s2 == S.Less) ? S.Less : S.LessEqual;
  }

  private static IBuiltInSymbol precedenceUnion(IBuiltInSymbol s1, IBuiltInSymbol s2) {
    return (s1 == S.LessEqual || s2 == S.LessEqual) ? S.LessEqual : S.Less;
  }

  public static IAST relationToInterval(int headID, IExpr value) {
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

  public static IAST union(final IAST interval1, final IAST interval2, EvalEngine engine) {
    IASTAppendable result = F.ast(S.IntervalData, interval1.size() + interval2.size());
    result.appendArgs(interval1);
    result.appendArgs(interval2);

    IAST normalized = normalize(result, engine);
    if (normalized.isInvalid()) {
      return F.NIL;
    }
    return normalized.orElse(result);
  }

}
