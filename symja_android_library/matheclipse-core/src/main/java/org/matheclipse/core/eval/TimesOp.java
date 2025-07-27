package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

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
  private INumber numberValue;

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

  /**
   * Multiply this {@link S#Times} expression with the expression <code>expr</code>.
   *
   * @param expr the expression to multiply with
   */
  public void appendRecursive(IExpr expr) {
    if (expr.isNumber()) {
      if (numberValue == null) {
        numberValue = (INumber) expr;
        return;
      }
      evaled = true;
      IExpr temp = numberValue.timesExpr((INumber) expr);
      if (temp.isNumber()) {
        numberValue = (INumber) temp;
        return;
      }
      expr = temp;
    } else {
      if (expr.isAST()) {
        final IExpr head = expr.head();
        if (head == S.Power) {
          final IAST ast = (IAST) expr;
          if (ast.size() == 3 && ast.exponent().isNumber()) {
            mergePower(ast.base(), (INumber) ast.exponent());
            return;
          }
        } else if (head == S.Times && expr.size() > 1) {
          final IAST ast = (IAST) expr;
          for (int i = 1; i < ast.size(); i++) {
            appendRecursive(ast.get(i));
          }
          return;
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
}
