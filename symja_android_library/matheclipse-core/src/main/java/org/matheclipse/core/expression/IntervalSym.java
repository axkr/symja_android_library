package org.matheclipse.core.expression;

import java.util.Comparator;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class IntervalSym {

  /**
   * IExprProcessor interface method boolean process (IExpr min, IExpr max, IASTAppendable result,
   * int index), return true or false;
   */
  @FunctionalInterface
  private interface IExprProcessor {
    /**
     * Append the transformed interval part <code>[min, max]</code> to the result.
     *
     * @param min minimum limit of a single interval part
     * @param max maximum limit of a single interval part
     * @param result the resulting list of interval parts
     * @param index
     * @return <code>true</code> if a new interval could be appended to result; <code>false</code>
     *         otherwise.
     */
    boolean apply(IExpr min, IExpr max, IASTAppendable result, int index);
  }

  private static final Comparator<IExpr> INTERVAL_COMPARATOR = new Comparator<IExpr>() {
    @Override
    public int compare(IExpr o1, IExpr o2) {
      if (o1.first().equals(o2.first())) {
        if (o1.second().equals(o2.second())) {
          return 0;
        }
        return (o1.second().greater(o2.second()).isTrue()) ? 1 : -1;
        // return (S.Greater.ofQ(o1.second(), o2.second())) ? 1 : -1;
      }
      return (o1.first().greater(o2.first()).isTrue()) ? 1 : -1;
      // return (S.Greater.ofQ(o1.first(), o2.first())) ? 1 : -1;
    }
  };

  public static IExpr abs(final IAST ast) {
    IAST interval = normalize(ast);
    if (interval.isPresent()) {
      IASTAppendable result = F.IntervalAlloc(interval.size());
      EvalEngine engine = EvalEngine.get();
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        IExpr min = list.arg1();
        IExpr max = list.arg2();
        if (min.isRealResult() && max.isRealResult()) {
          result.append(F.list(mig(min, max, engine), mag(min, max, engine)));
        } else {
          return F.NIL;
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr arccos(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.list(F.ArcCos(max), F.ArcCos(min)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arccosh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C1) && engine.evalLessEqual(max, F.CInfinity)) {
        result.append(index, F.list(F.ArcCosh(min), F.ArcCosh(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arccot(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.ArcCot(min), F.ArcCot(max)));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.CNPiHalf, F.ArcCot(min)));
        result.append(F.list(F.ArcCot(max), F.CPiHalf));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.list(F.ArcCot(min), F.ArcCot(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arcsin(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.list(F.ArcSin(min), F.ArcSin(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arcsinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.ArcSinh(min), F.ArcSinh(max)));
        return true;
      }
      return false;
    });
  }


  public static IExpr arctan(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.ArcTan(min), F.ArcTan(max)));
        return true;
      }
      return false;
    });
  }

  public static IExpr arctanh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.list(F.ArcTanh(min), F.ArcTanh(max)));
        return true;
      }
      return false;
    });
  }

  /**
   * Converts a potentially disjoint multi-part interval into a single bounding box (hull).
   * 
   * @param interval the IAST interval to bound
   * @return a single continuous Interval AST, or F.NIL
   */
  public static IAST boundingBox(IAST interval) {
    if (!interval.isPresent()) {
      return F.NIL;
    }
    if (interval.isInterval() && interval.argSize() > 1) {
      IExpr minVal = min(interval);
      IExpr maxVal = max(interval);
      if (minVal.isPresent() && maxVal.isPresent()) {
        return IntervalSym.of(F.eval(minVal), F.eval(maxVal));
      }
      return F.NIL;
    }
    IAST list = (IAST) interval.arg1();
    IExpr arg1 = list.arg1();
    IExpr arg2 = list.arg2();
    if (arg1.isReal() && arg2.isReal()) {
      if (((IReal) arg1).isGT((IReal) arg2)) {
        return IntervalSym.of(arg2, arg1);
      }
      return IntervalSym.of(arg1, arg2);
    }
    return IntervalSym.of(F.eval(arg1), F.eval(arg2));
  }

  /**
   * Converts a potentially disjoint multi-part interval into a single bounding box (hull).
   * 
   * @param expr the IExpr to bound
   * @return a single continuous Interval IExpr, or F.NIL
   */
  public static IExpr boundingBox(IExpr expr) {
    if (!expr.isPresent()) {
      return F.NIL;
    }
    if (expr.isInterval()) {
      return boundingBox((IAST) expr);
    }
    return expr;
  }

  public static IAST cos(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, F.C2Pi)) {
        // difference >= 2 * Pi
        result.append(index, F.list(F.CN1, F.C1));
      } else {
        // slope from 1st derivative
        double dMin = engine.evalDouble(F.Sin(min).negate());
        double dMax = engine.evalDouble(F.Sin(max).negate());
        if (engine.evalLessEqual(difference, S.Pi)) {
          if (dMin >= 0) {
            if (dMax >= 0) {
              result.append(index, F.list(F.Cos(min), F.Cos(max)));
            } else {
              IExpr minimum = minimum(F.Cos(min), F.Cos(max), engine);
              if (minimum.isNIL()) {
                return false;
              }
              result.append(index, F.list(minimum, F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.Cos(max), F.Cos(min)));
            } else {
              IExpr maximum = maximum(F.Cos(min), F.Cos(max), engine);
              if (maximum.isNIL()) {
                return false;
              }
              result.append(index, F.list(F.CN1, maximum));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              IExpr minimum = minimum(F.Cos(min), F.Cos(max), engine);
              if (minimum.isNIL()) {
                return false;
              }
              result.append(index, F.list(minimum, F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              IExpr maximum = maximum(F.Cos(min), F.Cos(max), engine);
              if (maximum.isNIL()) {
                return false;
              }
              result.append(index, F.list(F.CN1, maximum));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST cosh(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.list(F.Cosh(min), F.Cosh(max)));
        } else if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.list(F.C1, F.Max(F.Cosh(min), F.Cosh(max))));
        } else if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
          result.append(F.list(F.Cosh(min), F.Cosh(max)));
        }
        return true;
      }
      return false;
    });
  }

  public static IAST cot(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, S.Pi)) {
        result.append(F.list(F.CNInfinity, F.CInfinity));
      } else {
        double dMin = engine.evalDouble(F.Cot(min));
        double dMax = engine.evalDouble(F.Cot(max));
        if (engine.evalLessEqual(difference, F.CPiHalf)) {
          // difference <= 1/2*Pi
          if (dMin < 0) {
            if (dMax >= 0) {
              result.append(F.list(F.CNInfinity, F.Cot(min)));
              result.append(F.list(F.Cot(max), F.CInfinity));
            } else {
              result.append(F.list(F.Cot(max), F.Cot(min)));
            }
          } else {
            result.append(F.list(F.Cot(min), F.Cot(max)));
          }
        } else { // difference between {Pi/2, Pi}
          if (dMin >= 0) {
            if (dMax < 0) {
              result.append(F.list(F.CNInfinity, F.Cot(max)));
              result.append(F.list(F.Cot(min), F.CInfinity));
            } else {
              if (dMin < dMax) {
                result.append(F.list(F.CNInfinity, F.Cot(min)));
                result.append(F.list(F.Cot(max), F.CInfinity));
              } else {
                result.append(F.list(F.CNInfinity, F.CInfinity));
              }
            }
          } else {
            if (dMax < 0) {
              if (dMin < dMax) {
                result.append(F.list(F.Cot(max), F.CInfinity));
                result.append(F.list(F.CNInfinity, F.Cot(min)));
              } else {
                result.append(F.list(F.CNInfinity, F.CInfinity));
              }
            } else {
              result.append(F.list(F.CNInfinity, F.CInfinity));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST coth(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.Coth(max), F.Coth(min)));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.CNInfinity, F.Coth(min)));
        result.append(F.list(F.Coth(max), F.CInfinity));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.list(F.Coth(min), F.Coth(max)));
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
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.Csch(max), F.Csch(min)));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
        result.append(F.list(F.CNInfinity, F.Csch(min)));
        result.append(F.list(F.Csch(max), F.CInfinity));
        return true;
      }
      return false;
    }, (min, max, result, index) -> {
      if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
        result.append(F.list(F.Csch(min), F.Csch(max)));
        return true;
      }
      return false;
    });
  }

  private static Apfloat[] interval(Apfloat x) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    return new Apfloat[] {h.nextDown(x), h.nextUp(x)};
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
      IASTAppendable result = F.IntervalAlloc(normalizedInterval.size());
      for (int i = 1; i < normalizedInterval.size(); i++) {
        IAST list = (IAST) normalizedInterval.get(i);
        if (list.arg1().isRealResult() && list.arg2().isRealResult()) {
          if (list.arg1().isNegativeResult()) {
            if (list.arg2().isNegativeResult()) {
              result.append(F.list(list.arg1().inverse(), list.arg2().inverse()));
            } else {
              result.append(F.list(F.CNInfinity, list.arg1().inverse()));
              if (!list.arg2().isZero()) {
                result.append(F.list(list.arg2().inverse(), F.CInfinity));
              }
            }
          } else {
            if (list.arg1().isZero()) {
              if (list.arg2().isZero()) {
                result.append(F.list(F.CNInfinity, F.CInfinity));
              } else {
                result.append(F.list(list.arg2().inverse(), F.CInfinity));
              }
            } else {
              result.append(F.list(list.arg1().inverse(), list.arg2().inverse()));
            }
          }
        } else {
          return F.NIL;
        }
      }
      normalizedInterval = normalize(result);
      if (normalizedInterval.isPresent()) {
        return normalizedInterval;
      }
      return result;
    }
    return F.NIL;
  }

  private static boolean isNormalized(final IAST interval) {
    return interval.isEvalFlagOn(IAST.BUILT_IN_EVALED);
  }

  public static IAST log(final IAST ast) {
    if (ast.size() == 1 && ast.isInterval()) {
      return F.CEmptyIntervalData;
    }
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
        min = S.Log.of(engine, min);
        max = S.Log.of(engine, max);
        result.append(index, F.list(min, max));
        return true;
      }
      return false;
    });
  }

  /**
   * The absolute value of the magnitude of an interval.
   *
   * @param inf infimum
   * @param sup supremum
   * @param engine
   * @return
   */
  private static IExpr mag(IExpr inf, IExpr sup, EvalEngine engine) {
    if (engine.evalGreaterEqual(inf, F.C0)) {
      // inf >= 0
      return sup;
    } else if (engine.evalLessEqual(sup, F.C0)) {
      // sup <= 0
      return inf.negate();
    }
    return F.Max(inf.negate(), sup);
  }

  /**
   * @param symbol
   * @param ast
   * @return
   */
  public static IExpr mapSymbol(ISymbol symbol, final IAST ast) {
    IAST interval = normalize(ast);
    if (interval.isPresent()) {
      IASTAppendable result = F.IntervalAlloc(interval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        IExpr min = list.arg1();
        IExpr max = list.arg2();
        if (min.isRealResult() && max.isRealResult()) {
          result.append(F.list(F.unaryAST1(symbol, min), F.unaryAST1(symbol, max)));
        } else {
          return F.NIL;
        }
      }
      return result;
    }
    return F.NIL;
  }

  public static IExpr max(final IAST ast) {
    return normalize(ast).ifPresent(//
        interval -> F.eval(F.mapFunction(S.Max, (IAST) interval, list -> list.second())));
  }

  private static IExpr maximum(IExpr value1, IExpr value2, EvalEngine engine) {
    final IExpr v1 = engine.evaluate(value1);
    final IExpr v2 = engine.evaluate(value2);
    if (v1.greaterEqual(v2).isTrue()) {
      return v1;
    }
    if (v2.greater(v1).isTrue()) {
      return v2;
    }
    return F.NIL;
  }

  /**
   * The absolute value of the mignitude of an interval.
   *
   * @param inf infimum
   * @param sup supremum
   * @param engine
   * @return
   */
  private static IExpr mig(IExpr inf, IExpr sup, EvalEngine engine) {
    if (engine.evalGreater(inf, F.C0)) {
      // inf > 0
      return inf;
    } else if (engine.evalLess(sup, F.C0)) {
      // sup < 0
      return sup.negate();
    }
    return F.C0;
  }

  public static IExpr min(final IAST ast) {
    return normalize(ast).ifPresent(//
        interval -> F.eval(F.mapFunction(S.Min, (IAST) interval, list -> list.first())));
  }

  private static IExpr minimum(IExpr value1, IExpr value2, EvalEngine engine) {
    IExpr v1 = engine.evaluate(value1);
    IExpr v2 = engine.evaluate(value2);
    if (v1.lessEqual(v2).isTrue()) {
      return v1;
    }
    if (v2.less(v1).isTrue()) {
      return v2;
    }
    return F.NIL;
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
          IASTAppendable result = F.IntervalAlloc(interval.size());
          for (int i = 1; i < interval.size(); i++) {
            IAST list = (IAST) interval.get(i);
            IExpr min = list.arg1();
            IExpr max = list.arg2();

            boolean processed = false;
            for (IExprProcessor processor : processors) {
              processed = processor.apply(min, max, result, i);
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
   * The list of intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @param engine
   * @return <code>F.NIL</code> if the interval could not be normalized
   */
  public static IAST normalize(final IAST intervalList, EvalEngine engine) {
    try {
      if (!intervalList.isAST(S.Interval)) {
        return F.Interval(F.List(intervalList, intervalList));
      }
      IASTAppendable result = intervalList.copyAppendable();
      boolean evaled = false;
      for (int i = 1; i < intervalList.size(); i++) {
        IAST temp = normalizeArgument(intervalList.get(i), engine);
        if (temp.isPresent()) {
          evaled = true;
          result.set(i, temp);
        }
      }
      if (EvalAttributes.sort(result, INTERVAL_COMPARATOR)) {
        evaled = true;
      }
      if (result.size() > 2) {
        int j = 1;
        IAST list1 = (IAST) result.arg1();
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg2();
        int i = 2;
        while (i < result.size()) {
          IAST list2 = (IAST) result.get(i);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg2();
          if (min2.lessEqual(max1).isTrue()) {
            if (max2.lessEqual(max1).isTrue()) {
              evaled = true;
              result.remove(i);
            } else {
              evaled = true;
              result.remove(i);
              list1 = F.list(min1, max2);
              max1 = max2;
            }
          } else {
            result.set(j++, list1);
            list1 = list2;
            min1 = min2;
            max1 = max2;
            i++;
          }
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
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // The expression `1` is not a valid interval.
      String str = Errors.getMessage("nvld", F.list(intervalList), engine);
      throw new ArgumentTypeException(str);
    }
  }

  /**
   * If the argument is a list of 2 elements, try sorting the elements. If the argument is not a
   * list return a new <code>{argOfIntervalList, argOfIntervalList]</code>
   *
   * @param arg
   * @param engine
   * @return
   */
  private static IAST normalizeArgument(final IExpr arg, final EvalEngine engine)
      throws ArgumentTypeException {
    if (arg.isList()) {
      if (arg.size() == 3) {
        IAST list = (IAST) arg;
        IExpr arg1 = list.arg1();
        IExpr arg2 = list.arg2();
        if (arg1.isReal() && arg2.isReal()) {
          if (arg1.greaterThan(arg2).isTrue()) {
            return F.list(arg2, arg1);
          }
          return F.NIL;
        }
        IExpr min = arg1.isNumber() ? arg1 : engine.evaluate(arg1);
        IExpr max = arg2.isNumber() ? arg2 : engine.evaluate(arg2);
        if (min.isRealResult() && max.isRealResult()) {
          if (min.greaterThan(max).isTrue()) {
            return F.list(max, min);
          }
        }
        return F.NIL;
      }
      // The expression `1` is not a valid interval.
      String str = Errors.getMessage("nvld", F.list(arg), engine);
      throw new ArgumentTypeException(str);
    }
    if (arg instanceof INum) {
      if (arg instanceof ApfloatNum) {
        Apfloat apfloat = ((ApfloatNum) arg).fApfloat;
        Apfloat[] values = interval(apfloat);
        return F.list(F.num(values[0]), //
            F.num(values[1]));
      }
      double value = ((IReal) arg).doubleValue();
      return F.list(F.num(Math.nextDown(value)), //
          F.num(Math.nextUp(value)));
    }
    return F.list(arg, arg);
  }

  /**
   * Creates a Symja {@code Interval({min, max})} AST that backs an AccumBounds.
   *
   * @param min lower bound
   * @param max upper bound
   * @return {@code Interval({min, max})}
   */
  public static IAST of(IExpr min, IExpr max) {
    return F.Interval(F.list(min, max));
  }

  public static IExpr plus(final IAST ast1, final IAST ast2) {
    IAST interval1 = normalize(ast1);
    IAST interval2 = normalize(ast2);
    if (interval1.isPresent() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalAlloc(interval1.size() * interval2.size());
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg2();

        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg2();

          IAST list = F.list(min1.plus(min2), //
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
      IASTAppendable result = F.IntervalAlloc(interval2.size());
      for (int j = 1; j < interval2.size(); j++) {
        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IExpr max2 = list2.arg2();

        IAST list = F.list(scalar.plus(min2), //
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
      IASTAppendable result = F.IntervalAlloc(baseInterval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        if (list.arg1().isRealResult() && list.arg2().isRealResult()) {
          if (exponent.isEven()) {
            if (list.arg1().isNonNegativeResult()) {
              result.append(F.list(list.arg1().power(exponent), list.arg2().power(exponent)));
            } else {
              if (list.arg2().isNegativeResult()) {
                result.append(F.list(list.arg2().power(exponent), list.arg1().power(exponent)));
              } else {
                result.append(
                    F.list(F.C0, F.Max(list.arg1().power(exponent), list.arg2().power(exponent))));
              }
            }
          } else {
            result.append(F.list(list.arg1().power(exponent), list.arg2().power(exponent)));
          }
        } else {
          return F.NIL;
        }
      }
      if (negative) {
        return inverse(result);
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
      IASTAppendable result = F.IntervalAlloc(baseInterval.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        if (list.arg1().isNonNegativeResult() && list.arg2().isNonNegativeResult()) {
          result.append(F.list(list.arg1().power(exponent), list.arg2().power(exponent)));
        } else {
          return F.NIL;
        }
      }
      if (negative) {
        return inverse(result);
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
      IASTAppendable result = F.IntervalAlloc(intervalExponent.size());
      for (int i = 1; i < interval.size(); i++) {
        IAST list = (IAST) interval.get(i);
        if (base.isZero()) {
          if (list.arg1().isNegativeResult() || list.arg2().isNegativeResult()) {
            return S.Indeterminate;
          }
        }
        result.append(F.list(base.power(list.arg1()), base.power(list.arg2())));
      }
      return result;
    }
    return F.NIL;
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
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        if (engine.evalGreaterEqual(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.list(F.Sech(max), F.Sech(min)));
        } else if (engine.evalLess(min, F.C0) && engine.evalGreaterEqual(max, F.C0)) {
          result.append(F.list(F.Min(F.Sech(min), F.Sech(max)), F.C1));
        } else if (engine.evalLess(min, F.C0) && engine.evalLess(max, F.C0)) {
          result.append(F.list(F.Sech(min), F.Sech(max)));
        }
        return true;
      }
      return false;
    });
  }

  /**
   * Determine the sign of an interval. Return 1 if the interval is strictly positive, -1 if the
   * interval is strictly negative, and Integer.MIN_VALUE if the interval straddles zero or if the
   * sign is indeterminate.
   * 
   * @param intervalAST an interval represented as an AST, expected to be in the form of
   *        {@link F#Interval(IExpr)}
   * @param engine the evaluation engine used to evaluate the bounds of the interval
   * @return 1 if the interval is strictly positive, -1 if the interval is strictly negative, and
   *         Integer.MIN_VALUE if the interval straddles zero or if the sign is indeterminate.
   */
  public static int sign(IAST intervalAST, EvalEngine engine) {
    boolean allPos = true;
    boolean allNeg = true;
    for (int i = 1; i <= intervalAST.argSize(); i++) {
      IAST bounds = (IAST) intervalAST.get(i);
      if (bounds.isAST(S.List, 3)) {
        if (!engine.evaluate(F.Greater(bounds.arg1(), F.C0)).isTrue())
          allPos = false;
        if (!engine.evaluate(F.Less(bounds.arg2(), F.C0)).isTrue())
          allNeg = false;
      } else {
        // wrong interval format
        allPos = false;
        allNeg = false;
        break;
      }
    }
    if (allPos)
      return 1;
    if (allNeg)
      return -1;
    return Integer.MIN_VALUE;
  }

  public static IAST sin(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, F.C2Pi)) {
        // difference >= 2 * Pi
        result.append(index, F.list(F.CN1, F.C1));
      } else {
        // slope from 1st derivative
        double dMin = engine.evalDouble(F.Cos(min));
        double dMax = engine.evalDouble(F.Cos(max));
        if (engine.evalLessEqual(difference, S.Pi)) {
          if (dMin >= 0) {
            if (dMax >= 0) {
              result.append(index, F.list(F.Sin(min), F.Sin(max)));
            } else {
              IExpr minimum = minimum(F.Sin(min), F.Sin(max), engine);
              if (minimum.isNIL()) {
                return false;
              }
              result.append(index, F.list(minimum, F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.Sin(max), F.Sin(min)));
            } else {
              IExpr maximum = maximum(F.Sin(min), F.Sin(max), engine);
              if (maximum.isNIL()) {
                return false;
              }
              result.append(index, F.list(F.CN1, maximum));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              IExpr minimum = minimum(F.Sin(min), F.Sin(max), engine);
              if (minimum.isNIL()) {
                return false;
              }
              result.append(index, F.list(minimum, F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              IExpr maximum = maximum(F.Sin(min), F.Sin(max), engine);
              if (maximum.isNIL()) {
                return false;
              }
              result.append(index, F.list(F.CN1, maximum));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST sinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.Sinh(min), F.Sinh(max)));
        return true;
      }
      return false;
    });
  }

  public static IAST tan(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      IAST difference = F.Subtract(max, min);
      if (engine.evalGreaterEqual(difference, S.Pi)) {
        result.append(F.list(F.CNInfinity, F.CInfinity));
      } else {
        double dMin = engine.evalDouble(F.Tan(min));
        double dMax = engine.evalDouble(F.Tan(max));
        if (engine.evalLessEqual(difference, F.CPiHalf)) {
          // difference <= 1/2*Pi
          if (dMin >= 0) {
            if (dMax < 0) {
              result.append(F.list(F.CNInfinity, F.Tan(max)));
              result.append(F.list(F.Tan(min), F.CInfinity));
            } else {
              result.append(F.list(F.Tan(min), F.Tan(max)));
            }
          } else {
            result.append(F.list(F.Tan(min), F.Tan(max)));
          }
        } else { // difference between {Pi/2, Pi}
          if (dMin >= 0) {
            if ((dMax < 0) || (dMin > dMax)) {
              result.append(F.list(F.CNInfinity, F.Tan(max)));
              result.append(F.list(F.Tan(min), F.CInfinity));
            } else {
              result.append(F.list(F.CNInfinity, F.CInfinity));
            }
          } else {
            if (dMax < 0) {
              if (dMin <= dMax) {
                result.append(F.list(F.CNInfinity, F.CInfinity));
              } else {
                result.append(F.list(F.CNInfinity, F.Tan(max)));
                result.append(F.list(F.Tan(min), F.CInfinity));
              }
            } else {
              result.append(F.list(F.Tan(min), F.Tan(max)));
            }
          }
        }
      }
      return true;
    });
  }

  public static IAST tanh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.Tanh(min), F.Tanh(max)));
        return true;
      }
      return false;
    });
  }

  /**
   * Appends the given expression to the list, if it is not {@link S#Indeterminate} (NaN).
   * 
   * @param list the list to add the expression to
   * @param expr the expression to append
   */
  private static void appendWithoutNaN(IASTAppendable list, IExpr expr) {
    if (!expr.isIndeterminate() ) {
      list.append(expr);
    }  
  }
  public static IExpr times(final IAST ast1, final IAST ast2) {
    IAST interval1 = normalize(ast1);
    IAST interval2 = normalize(ast2);
    if (interval1.isPresent() && interval2.isPresent()) {
      IASTAppendable result = F.IntervalAlloc(interval1.size() * interval2.size());
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg2();

        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg2();

          IASTAppendable min = F.ast(S.Min, 4);
          appendWithoutNaN(min, min1.times(min2));
          appendWithoutNaN(min, min1.times(max2));
          appendWithoutNaN(min, max1.times(min2));
          appendWithoutNaN(min, max1.times(max2));
          IASTAppendable max = F.ast(S.Max, 4);
          appendWithoutNaN(max, min1.times(min2));
          appendWithoutNaN(max, min1.times(max2));
          appendWithoutNaN(max, max1.times(min2));
          appendWithoutNaN(max, max1.times(max2));
          IAST list = F.list(min, max);
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
      IASTAppendable result = F.IntervalAlloc(interval2.size());
      for (int j = 1; j < interval2.size(); j++) {
        IAST list2 = (IAST) interval2.get(j);
        IExpr min2 = list2.arg1();
        IExpr max2 = list2.arg2();

        IExpr min = scalar.times(min2);
        IExpr max = scalar.times(max2);
        IAST list = F.list( //
            F.Min(min, max), //
            F.Max(min, max));
        result.append(list);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Converts a potentially disjoint multi-part interval into a single bounding box (hull). If the
   * result is a single point, returns that point instead of an interval. Otherwise, returns the
   * input expression.
   *
   * @param expr the expression or interval to bound
   * @return a single point, or the input expression
   */
  public static IExpr toAccumBounds(IExpr expr) {
    if (expr.isInterval()) {
      IExpr result = IntervalSym.boundingBox(expr);
      IAST bounds = (IAST) ((IAST) result).arg1();
      IExpr lo = bounds.arg1();
      IExpr hi = bounds.arg2();
      IAST list2 = (IAST) expr.first();
      if (lo.equals(hi)) {
        return list2.arg1();
      }
      if (lo.isInfinity() && hi.isInfinity()) {
        return F.CInfinity;
      }
      if (lo.isNegativeInfinity() && hi.isNegativeInfinity()) {
        return F.CNInfinity;
      }
    }
    return expr;
  }

  /**
   * Converts a potentially disjoint multi-part interval into a single bounding box (hull). If the
   * result is a single point, returns that point instead of an interval. Otherwise, returns
   * {@link S#Indeterminate}.
   *
   * @param expr the expression or interval to bound
   * @return a single point, or {@link S#Indeterminate}
   */
  public static IExpr toAccumBoundsIndeterminate(IExpr expr) {
    if (expr.isInterval()) {
      IExpr result = IntervalSym.boundingBox(expr);
      IAST bounds = (IAST) ((IAST) result).arg1();
      IExpr lo = bounds.arg1();
      IExpr hi = bounds.arg2();
      IAST list2 = (IAST) expr.first();
      if (lo.equals(hi)) {
        return list2.arg1();
      }
      if (lo.isInfinity() && hi.isInfinity()) {
        return F.CInfinity;
      }
      if (lo.isNegativeInfinity() && hi.isNegativeInfinity()) {
        return F.CNInfinity;
      }
      return S.Indeterminate;
    }
    return expr;
  }

  /**
   * Convert an interval set with only one sub-interval to a single point if the first value of the
   * sub-interval list equals the fourth value of the sub-interval list.
   * 
   * @param interval the interval to convert
   * @return the single point if the interval is a single point, otherwise {@link F#NIL}
   */
  public static IExpr toSinglePoint(IExpr interval) {
    if (interval.argSize() == 1 && interval.isInterval()) {
      IAST list2 = (IAST) interval.first();
      if (list2.arg1().equals(list2.arg2())) {
        return list2.arg1();
      }
    }
    return F.NIL;
  }
}
