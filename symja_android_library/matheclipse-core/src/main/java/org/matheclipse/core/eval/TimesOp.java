package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.tensor.qty.IQuantity;

/**
 * {@link S#Times} operator for adding multiple arguments with the
 * <code>appendRecursive(expr)</code> method and returning the result, with the
 * <code>getProduct()</code> method.
 * <p>
 * The <code>TimesOp</code> class is used to simplify the multiplication of numbers and reduce the
 * number of terms in a multiplication expression. The <code>TimesOp</code> class is <b>not used</b>
 * to fully evaluate a multiplication expression.
 */
public final class TimesOp {
  /**
   * Multiply the two expressions <code>a1</code> and <code>a2</code> and return the result.
   * 
   *
   * @param a1 the first expression
   * @param a2 the second expression
   * @param returnNilIfUnevaluated TODO check if this parameter can be removed
   * @return the product of the two expressions or {@link F#NIL}
   */
  public static org.matheclipse.core.interfaces.IExpr numberLikeTimes(
      org.matheclipse.core.interfaces.IExpr a1, final org.matheclipse.core.interfaces.IExpr a2,
      boolean returnNilIfUnevaluated) {
    if (a1.isNumber()) {
      if (a2.isInterval()) {
        return IntervalSym.times(a1, (org.matheclipse.core.interfaces.IAST) a2);
      }
      if (a2.isIntervalData()) {
        return IntervalDataSym.times(a1, (org.matheclipse.core.interfaces.IAST) a2);
      }
      if (a2.isQuantity()) {
        IQuantity q = (IQuantity) a2;
        return q.times(a1, returnNilIfUnevaluated);
      }
    } else if (a1.isIntervalData()) {
      if (a2.isAST()) {
        org.matheclipse.core.interfaces.IAST interval = (org.matheclipse.core.interfaces.IAST) a2;
        if (a2.isInterval()) {
          interval = IntervalDataSym.intervalToIntervalSet(interval);
        }
        if (interval.isIntervalData()) {
          return IntervalDataSym.times((org.matheclipse.core.interfaces.IAST) a1, interval);
        }
      }
    } else if (a1.isInterval()) {
      if (a2.isAST()) {
        org.matheclipse.core.interfaces.IAST interval = (org.matheclipse.core.interfaces.IAST) a2;
        if (a2.isInterval()) {
          return IntervalSym.times((org.matheclipse.core.interfaces.IAST) a1, interval);
        }
      }
    } else if (a1.isQuantity()) {
      IQuantity q = (IQuantity) a1;
      return q.times(a2, returnNilIfUnevaluated);
    }

    if (a2.isNumber()) {
      if (a1.isInterval()) {
        return IntervalSym.times(a2, (org.matheclipse.core.interfaces.IAST) a1);
      }
      if (a1.isIntervalData()) {
        return IntervalDataSym.times(a2, (org.matheclipse.core.interfaces.IAST) a1);
      }
    } else if (a2.isIntervalData()) {
      if (a1.isAST()) {
        org.matheclipse.core.interfaces.IAST interval = (org.matheclipse.core.interfaces.IAST) a1;
        if (a1.isInterval()) {
          interval = IntervalDataSym.intervalToIntervalSet(interval);
        }
        if (interval.isIntervalData()) {
          return IntervalDataSym.times(interval, (org.matheclipse.core.interfaces.IAST) a2);
        }
      }
    } else if (a2.isQuantity()) {
      IQuantity q = (IQuantity) a2;
      return q.times(a1, returnNilIfUnevaluated);
    }
    return F.NIL;
  }

  public static IExpr getProduct(IAST timesAST) {
    TimesOp timesOp = new TimesOp(timesAST.size());
    timesOp.appendRecursive(timesAST);
    return timesOp.getProduct();
  }

  public static IExpr getProductNIL(IAST timesAST) {
    TimesOp timesOp = new TimesOp(timesAST.size());
    timesOp.appendRecursive(timesAST);
    if (!timesOp.isEvaled()) {
      return F.NIL;
    }
    return timesOp.getProduct();
  }

  private final int capacity;

  private boolean evaled;

  /** The value of the multiplication of numbers. */
  private IExpr numberValue;

  /** This map contains <code>base ^ exponent</code> as <code>(key, value></code> pairs. */
  private Map<IExpr, INumber> powerMap;

  /**
   * Constructor.
   *
   * @param capacity the approximated size of the resulting <code>Times()</code> AST.
   */
  public TimesOp(final int capacity) {
    this.capacity = capacity;
    clear();
  }

  /**
   * Multiply this {@link S#Times} expression with the expression <code>expr</code>.
   *
   * @param expr the expression to multiply with
   */
  public void appendRecursive(IExpr expr) {
    if (expr.isNumberLike()) {
      if (numberValue == null) {
        numberValue = expr;
        return;
      }
      if (numberValue.isNumber() && expr.isNumber()) {
        evaled = true;
        IExpr temp = ((INumber) numberValue).timesExpr((INumber) expr);
        if (temp.isNumber()) {
          evaled = true;
          numberValue = temp;
          return;
        }
        expr = temp;
      } else {
        IExpr temp = numberValue.times(expr, true);
        if (temp.isPresent()) {
          evaled = true;
          if (temp.isNumberLike()) {
            numberValue = temp;
            return;
          }
          expr = temp;
        }
      }
    } else {
      if (expr.isAST()) {
        final IAST ast = (IAST) expr;
        final IExpr head = expr.head();
        final int headID = expr.headID();
        switch (headID) {
          case ID.Power:
            if (ast.size() == 3 && ast.exponent().isNumber()) {
              mergePower(ast.base(), (INumber) ast.exponent());
              return;
            }
            break;
          case ID.Times: {
            if (expr.size() > 1) {
              for (int i = 1; i < ast.size(); i++) {
                appendRecursive(ast.get(i));
              }

              return;
            }
          }
            break;

          // case ID.Interval:
          // if (expr.isInterval()) {
          // if (numberValue == null) {
          // numberValue = expr;
          // return;
          // }
          // IExpr temp;
          // if (numberValue.isInterval()) {
          // temp = IntervalSym.times((IAST) numberValue, (IAST) expr);
          // } else {
          // temp = IntervalSym.times(numberValue, (IAST) expr);
          // }
          // if (temp.isPresent()) {
          // numberValue = temp;
          // evaled = true;
          // } else {
          // // if (addMerge(expr, F.C1)) {
          // // evaled = true;
          // // }
          // }
          // return;
          // }
          // break;
          // case ID.IntervalData:
          // if (expr.isIntervalData()) {
          // if (numberValue == null) {
          // numberValue = expr;
          // return;
          // }
          // IExpr temp = F.NIL;
          // if (numberValue.isIntervalData()) {
          // temp = IntervalDataSym.times((IAST) numberValue, (IAST) expr);
          // } else {
          // if (!numberValue.isInterval()) {
          // temp = IntervalDataSym.times(numberValue, (IAST) expr);
          // }
          // }
          // if (temp.isPresent()) {
          // numberValue = temp;
          // evaled = true;
          // } else {
          // // if (addMerge(expr, F.C1)) {
          // // evaled = true;
          // // }
          // }
          // return;
          // }
          // break;
        }
      }
    }
    mergePower(expr, F.C1);
  }

  /**
   * Multiply this {@link S#Times} expression with the expressions generated by the
   * <code>function</code>.
   *
   * @param function the generated expression from this {@link IntFunction#apply(int)} to multiply
   *        with
   */
  public void appendValues(final int start, final int end, IntFunction<IExpr> function) {
    for (int i = start; i < end; i++) {
      appendRecursive(function.apply(i));
    }
  }

  public void clear() {
    this.evaled = false;
    // if (powerMap != null) {
    // powerMap.clear();
    // } else {
    this.powerMap = null;
    // }
    this.numberValue = null;
  }

  private Map<IExpr, INumber> getMap() {
    if (powerMap == null) {
      powerMap = new HashMap<IExpr, INumber>(capacity + 5 + capacity / 10);
    }
    return powerMap;
  }

  /**
   * Get the current evaluated result of the product as a {@link S#Times} expression with respecting
   * the <code>OneIdentity</code> attribute.
   *
   * @return the product of the arguments as a {@link S#Times} expression or the number value if the
   *         internal power map is empty.
   */
  public IExpr getProduct() {
    if (powerMap == null || powerMap.isEmpty()) {
      if (numberValue != null) {
        return numberValue;
      }
      return F.C1;
    }
    IASTAppendable result = F.TimesAlloc(powerMap.size() + 1);
    if (numberValue != null) {
      result.append(numberValue);
    }
    for (Map.Entry<IExpr, INumber> power : powerMap.entrySet()) {
      final IExpr base = power.getKey();
      final INumber exponent = power.getValue();
      result.append(exponent.isOne() ? base : F.Power(base, exponent));
    }
    IExpr temp = result.oneIdentity1();
    if (temp.isTimes()) {
      EvalAttributes.sortWithFlags((IASTMutable) temp);
    }
    return temp;
  }

  private boolean isEvaled() {
    return evaled;
  }

  /**
   * Add or merge the <code>base ^ exponent</code> pair into the given <code>timesMap</code>.
   *
   * @param base the base expression
   * @param exponent the exponent expression
   */
  private void mergePower(final IExpr base, final INumber exponent) {
    final Map<IExpr, INumber> map = getMap();
    INumber oldExponent = map.get(base);
    if (oldExponent == null) {
      map.put(base, exponent);
      return;
    }
    final IExpr newBase;
    if (base.isInterval() && exponent.equals(oldExponent)) {
      newBase = IntervalSym.times((IAST) base, (IAST) base);
    } else if (base.isIntervalData() && exponent.equals(oldExponent)) {
      newBase = IntervalDataSym.times((IAST) base, (IAST) base);
    } else {
      newBase = F.NIL;
    }
    evaled = true;
    if (newBase.isPresent()) {
      map.put(newBase, oldExponent);
      return;
    }
    oldExponent = oldExponent.plus(exponent);
    if (oldExponent.isZero()) {
      map.remove(base);
      return;
    }
    map.put(base, oldExponent);

  }
}
