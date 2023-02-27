package org.matheclipse.core.expression;

import java.util.Comparator;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
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
        return (S.Greater.ofQ(list1.arg4(), list2.arg4())) ? 1 : -1;
      }
      return (S.Greater.ofQ(list1.arg1(), list2.arg1())) ? 1 : -1;
    }
  };

  private static boolean isNormalized(final IAST interval) {
    return interval.isEvalFlagOn(IAST.BUILT_IN_EVALED);
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
    for (int i = 1; i < intervalList.size(); i++) {
      IAST temp = normalizeArgument(intervalList.get(i), engine);
      if (temp.isInvalid()) {
        // The expression `1` is not a valid interval.
        IOFunctions.printMessage(S.IntervalData, "nvld", F.list(intervalList.get(i)), engine);
        return temp;
      }
      if (temp.isPresent()) {
        evaled = true;
        result.set(i, temp);
      }
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
      int i = 2;
      while (i < result.size()) {
        IAST list2 = (IAST) result.get(i);
        IExpr min2 = list2.arg1();
        IBuiltInSymbol left2 = (IBuiltInSymbol) list2.arg2();
        IBuiltInSymbol right2 = (IBuiltInSymbol) list2.arg3();
        IExpr max2 = list2.arg4();
        if (min1.less(min2).isTrue()) {
          if (max2.less(max1).isTrue()) {
            // (min1<min2<max2<max1)
            evaled = true;
            result.remove(i);
            continue;
          } else if (min2.lessEqual(max1).isTrue() && max1.less(max2).isTrue()) {
            evaled = true;
            result.remove(i);
            list1 = F.List(min1, left1, right2, max2);
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            continue;
          } else if (S.Equal.ofQ(max1, max2)) {
            // (min1<min2<max1|max2)
            evaled = true;
            result.remove(i);
            if (right1 == S.Less && right2 == S.Less) {
              list1 = F.List(min1, left1, S.Less, max2);
            } else if (right1 == S.LessEqual) {
              list1 = F.List(min1, left1, S.LessEqual, max1);
            } else {
              list1 = F.List(min1, left1, S.LessEqual, max2);
            }
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            continue;
          }

        } else if (S.Equal.ofQ(min1, min2)) {
          IExpr newMin = min1;
          IBuiltInSymbol newLeft = S.Less;
          IBuiltInSymbol newRight = S.Less;
          IExpr newMax = max1;
          if (left1 == S.Less && left2 == S.Less) {
            newLeft = S.Less;
            newMin = min1;
          } else if (left1 == S.LessEqual) {
            newLeft = S.LessEqual;
            newMin = min1;
          } else {
            newLeft = S.LessEqual;
            newMin = min2;
          }
          if (S.Equal.ofQ(max1, max2)) {
            evaled = true;
            result.remove(i);
            if (right1 == S.Less && right2 == S.Less) {
              newRight = S.Less;
              newMax = max1;
            } else if (right1 == S.LessEqual) {
              newRight = S.LessEqual;
              newMax = max1;
            } else {
              newRight = S.LessEqual;
              newMax = max2;
            }
            list1 = F.List(newMin, newLeft, newRight, newMax);
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            continue;
          }
          if (max1.less(max2).isTrue()) {
            evaled = true;
            result.remove(i);
            newRight = right2;
            newMax = max2;
            list1 = F.List(newMin, newLeft, newRight, newMax);
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            continue;
          }
          if (max2.less(max1).isTrue()) {
            // (min1|min2<max2<max1)
            evaled = true;
            result.remove(i);
            list1 = F.List(newMin, newLeft, right2, max2);
            min1 = list1.arg1();
            left1 = (IBuiltInSymbol) list1.arg2();
            right1 = (IBuiltInSymbol) list1.arg3();
            max1 = list1.arg4();
            continue;
          }
        }
        result.set(j++, list1);
        list1 = list2;
        min1 = list1.arg1();
        left1 = (IBuiltInSymbol) list1.arg2();
        right1 = (IBuiltInSymbol) list1.arg3();
        max1 = list1.arg4();
        i++;
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

  private static Apfloat[] interval(Apfloat x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return new Apfloat[] {h.nextDown(x), h.nextUp(x)};
  }

  /**
   * If the argument is a list of 2 elements, try sorting the elements. If the argument is not a
   * list return a new <code>{argOfIntervalList, argOfIntervalList]</code>
   *
   * @param arg
   * @param engine
   * @return {@link F#INVALID} if the interval could not be normalized. {@link F#NIL} if the
   *         interval doesn't need to be normalized.
   */
  private static IAST normalizeArgument(final IExpr arg, final EvalEngine engine) {
    if (arg.isList()) {
      if (arg.argSize() == 4) {
        IAST list = (IAST) arg;
        IExpr arg1 = list.arg1();
        IExpr arg4 = list.arg4();
        if (arg1.isReal() && arg4.isReal()) {
          if (arg1.greaterThan(arg4).isTrue()) {
            return F.List(arg4, //
                list.arg3(), //
                list.arg2(), //
                arg1);
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
        return F.NIL;
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
      double value = ((ISignedNumber) arg).doubleValue();
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

  public static IExpr plus(final IAST ast1, final IAST ast2) {
    IAST interval1 = normalize(ast1);
    IAST interval2 = normalize(ast2);
    if (interval1.isPresent() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval1.size() * interval2.size());
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg4();
        IBuiltInSymbol left1 = ((IBuiltInSymbol) list1.arg2());
        IBuiltInSymbol right1 = ((IBuiltInSymbol) list1.arg3());

        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg4();
          IAST list = F.List(min1.plus(min2), //
              timesPrecedence(left1, (IBuiltInSymbol) list2.arg2()), //
              timesPrecedence(right1, (IBuiltInSymbol) list2.arg3()), //
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
        left = timesPrecedence(symbols[0], symbols[1]);
        break;
      case 1:
        left = timesPrecedence(symbols[0], symbols[3]);
        break;
      case 2:
        left = timesPrecedence(symbols[1], symbols[2]);
        break;
      case 3:
        left = timesPrecedence(symbols[1], symbols[3]);
        break;
    }
    IBuiltInSymbol right = S.LessEqual;
    switch (index[1]) {
      case 0:
        right = timesPrecedence(symbols[0], symbols[1]);
        break;
      case 1:
        right = timesPrecedence(symbols[0], symbols[3]);
        break;
      case 2:
        right = timesPrecedence(symbols[1], symbols[2]);
        break;
      case 3:
        right = timesPrecedence(symbols[1], symbols[3]);
        break;
    }
    IAST list = F.List(//
        min, //
        left, //
        right, //
        max);
    return list;
  }

  private static IBuiltInSymbol timesPrecedence(IBuiltInSymbol s1, IBuiltInSymbol s2) {
    if (s1 == S.Less || s2 == S.Less) {
      return S.Less;
    }
    return S.LessEqual;
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

}
