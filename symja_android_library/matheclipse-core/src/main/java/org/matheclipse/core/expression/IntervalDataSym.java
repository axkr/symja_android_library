package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * See: <a href=
 * "https://mathematica.stackexchange.com/questions/162486/operating-with-real-intervals/162505#162505">162486/operating-with-real-intervals/162505#162505</a>
 *
 */
public class IntervalDataSym {

  public static IExpr plus(final IAST ast1, final IAST ast2) {
    IAST interval1 = ast1;
    IAST interval2 = ast2;
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
          Pair pairLeft = F.pair(left1, list2.arg2());
          Pair pairRight = F.pair(right1, list2.arg3());
          pairLeft.sortInplace();
          pairRight.sortInplace();

          IAST list = F.List(min1.plus(min2), //
              pairLeft.arg1(), //
              pairRight.arg1(), //
              max1.plus(max2));
          result.append(list);
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr plus(final IExpr scalar, final IAST ast2) {
    IAST interval2 = ast2;
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

  public static IExpr times(final IAST ast1, final IAST ast2) {
    IAST interval1 = ast1;
    IAST interval2 = ast2;
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
          Pair pairLeft = F.pair(left1, list2.arg2());
          Pair pairRight = F.pair(right1, list2.arg3());

          IAST list = F.List( //
              F.Min(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)), //
              pairLeft.arg2(), //
              pairRight.arg2(), //
              F.Max(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)));
          result.append(list);
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr times(final IExpr scalar, final IAST ast2) {
    IAST interval2 = ast2;
    if (scalar.isRealResult() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalDataAlloc(interval2.size());
      for (int j = 1; j < interval2.size(); j++) {
        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IExpr max2 = list2.arg4();

        IAST list = F.List( //
            F.Min(scalar.times(min2), scalar.times(max2), scalar.times(min2), scalar.times(max2)), //
            list2.arg2(), //
            list2.arg3(), //
            F.Max(scalar.times(min2), scalar.times(max2), scalar.times(min2), scalar.times(max2)));
        result.append(list);
      }
      return result;
    }
    return F.NIL;
  }

}
