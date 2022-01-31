package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public final class MutableInt {
  int value;

  public MutableInt(int value) {
    this.value = value;
  }

  public MutableInt increment() {
    value++;
    return this;
  }

  public MutableInt decrement() {
    value--;
    return this;
  }

  public int inc() {
    return ++value;
  }

  public int dec() {
    return --value;
  }

  public int value() {
    return value;
  }

  /**
   * Create a new <code>histogramMap</code> from the elements of <code>ast</code> for method
   * {@link #isPermutable(IAST, Map)}.
   *
   * @param ast
   * @return
   */
  public static Map<IExpr, MutableInt> createHistogram(IAST ast) {
    Map<IExpr, MutableInt> histogramMap =
        new HashMap<IExpr, MutableInt>(ast.size() + ast.size() / 10 + 6);
    for (int i = 1; i < ast.size(); i++) {
      IExpr key = ast.get(i);
      histogramMap.compute(key, (k, v) -> (v == null) ? new MutableInt(1) : v.increment());
    }
    return histogramMap;
  }

  /**
   * Return <code>true</code> if all values in <code>histogramMap</code> could be reduced to zero
   * for the number of elements of <code>ast</code>
   *
   * @param ast
   * @param histogram the map where all values should be reduced to zero
   * @return <code>true</code> if all values in <code>histogramMap</code> could be reduced to zero
   *         for all elements of <code>ast</code>
   */
  public static boolean isEqualPermutable(IAST ast, Map<IExpr, MutableInt> histogram) {
    for (int i = 1; i < ast.size(); i++) {
      final MutableInt value = histogram.get(ast.get(i));
      if (value == null || value.dec() < 0) {
        return false;
      }
    }
    return true;
  }
}
