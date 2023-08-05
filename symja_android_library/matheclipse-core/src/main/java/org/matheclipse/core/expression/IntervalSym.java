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

  private static final Comparator<IExpr> INTERVAL_COMPARATOR = new Comparator<IExpr>() {
    @Override
    public int compare(IExpr o1, IExpr o2) {
      if (o1.first().equals(o2.first())) {
        if (o1.second().equals(o2.second())) {
          return 0;
        }
        return (S.Greater.ofQ(o1.second(), o2.second())) ? 1 : -1;
      }
      return (S.Greater.ofQ(o1.first(), o2.first())) ? 1 : -1;
    }
  };

  /**
   * IExprProcessor interface method boolean process (IExpr min, IExpr max, IASTAppendable result,
   * int index), return true or false;
   */
  @FunctionalInterface
  public interface IExprProcessor {
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
   * The list of intervals are sorted and overlapping intervals are merged.
   *
   * @param intervalList
   * @param engine
   * @return <code>F.NIL</code> if the interval could not be normalized
   */
  public static IAST normalize(final IAST intervalList, EvalEngine engine) {
    try {
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
      result.addEvalFlags(IAST.BUILT_IN_EVALED);
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
        return result;
      }
      if (intervalList instanceof IASTMutable) {
        intervalList.addEvalFlags(IAST.BUILT_IN_EVALED);
        if (EvalAttributes.sort((IASTMutable) intervalList, INTERVAL_COMPARATOR)) {
          return intervalList;
        }
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.printMessage(S.Interval, rex, engine);
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
   * @return
   */
  private static IAST normalizeArgument(final IExpr arg, final EvalEngine engine) {
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

  public static IExpr max(final IAST ast) {
    return normalize(ast).ifPresent(//
        interval -> F.mapFunction(S.Max, (IAST) interval, list -> list.second()));
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
        interval -> F.mapFunction(S.Min, (IAST) interval, list -> list.first()));
  }

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
          //
        }
      }
    }

    return F.NIL;
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

  public static IExpr arcsinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.ArcSinh(min), F.ArcSinh(max)));
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

  public static IExpr arccos(final IAST ast) {
    EvalEngine engine = EvalEngine.get();
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (engine.evalGreaterEqual(min, F.CN1) && engine.evalLessEqual(max, F.C1)) {
        result.append(index, F.list(F.ArcCos(min), F.ArcCos(max)));
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

  public static IExpr arctan(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.ArcTan(min), F.ArcTan(max)));
        return true;
      }
      return false;
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

  public static IAST sinh(final IAST ast) {
    return mutableProcessorConditions(ast, (min, max, result, index) -> {
      if (min.isRealResult() && max.isRealResult()) {
        result.append(index, F.list(F.Sinh(min), F.Sinh(max)));
        return true;
      }
      return false;
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
      return result;
    }
    return F.NIL;
  }

  public static IAST csc(final IAST ast) {
    IAST interval = sin(ast);
    if (interval.isPresent()) {
      return inverse(interval);
    }
    return F.NIL;
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
              result.append(index, F.list(F.Min(F.Cos(min), F.Cos(max)), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.Cos(max), F.Cos(min)));
            } else {
              result.append(index, F.list(F.CN1, F.Max(F.Cos(min), F.Cos(max))));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              result.append(index, F.list(F.Min(F.Cos(min), F.Cos(max)), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              result.append(index, F.list(F.CN1, F.Max(F.Cos(min), F.Cos(max))));
            }
          }
        }
      }
      return true;
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

  public static IAST log(final IAST ast) {
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

  public static IAST sec(final IAST ast) {
    IAST interval = cos(ast);
    if (interval.isPresent()) {
      return inverse(interval);
    }
    return F.NIL;
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
              result.append(index, F.list(F.Min(F.Sin(min), F.Sin(max)), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.Sin(max), F.Sin(min)));
            } else {
              result.append(index, F.list(F.CN1, F.Max(F.Sin(min), F.Sin(max))));
            }
          }
        } else { // difference between {Pi, 2*Pi}
          if (dMin >= 0) {
            if (dMax > 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              result.append(index, F.list(F.Min(F.Sin(min), F.Sin(max)), F.C1));
            }
          } else {
            if (dMax < 0) {
              result.append(index, F.list(F.CN1, F.C1));
            } else {
              result.append(index, F.list(F.CN1, F.Max(F.Sin(min), F.Sin(max))));
            }
          }
        }
      }
      return true;
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

          IAST list = F.list( //
              F.Min(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)), //
              F.Max(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)));
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
        return F.Times(F.Power(F.CN1, intervalExponent), F.Power(base.negate(), intervalExponent));
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
}
