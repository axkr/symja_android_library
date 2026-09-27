package org.matheclipse.core.generic;

import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Sorting with an ordering function <code>p</code>, as <code>Sort(list, p)</code>: a merge sort
 * which puts <code>a</code> before <code>b</code> unless <code>p(a, b)</code> is <code>False</code>
 * or <code>-1</code>.
 *
 * <p>
 * Equal elements under a strict order therefore come out reversed:
 * <code>Sort({1,...,10}, Mod(#1,3) &gt; Mod(#2,3) &amp;)</code> is
 * <code>{8,5,2,10,7,4,1,9,6,3}</code>, and
 * <code>Sort(&lt;|a-&gt;2,b-&gt;2,c-&gt;1|&gt;, Greater)</code> is
 * <code>&lt;|b-&gt;2,a-&gt;2,c-&gt;1|&gt;</code>. An ordering function which answers <code>0</code>
 * for equal elements, like <code>Order</code>, keeps them in place. A {@link java.util.Comparator}
 * can't express this, since it has to answer consistently for <code>(a,b)</code> and
 * <code>(b,a)</code>.
 */
public final class PredicateSort {

  private PredicateSort() {}

  /**
   * The permutation which sorts <code>elements</code> with the ordering function <code>p</code>.
   *
   * @return the 0-based index of the element at each position of the sorted result
   */
  public static int[] permutation(IExpr[] elements, IExpr p, EvalEngine engine) {
    final int n = elements.length;
    int[] index = new int[n];
    for (int i = 0; i < n; i++) {
      index[i] = i;
    }
    int[] buffer = new int[n];
    sort(index, buffer, 0, n, elements, p, engine);
    return index;
  }

  private static void sort(int[] index, int[] buffer, int from, int to, IExpr[] elements, IExpr p,
      EvalEngine engine) {
    if (to - from < 2) {
      return;
    }
    final int middle = (from + to) >>> 1;
    sort(index, buffer, from, middle, elements, p, engine);
    sort(index, buffer, middle, to, elements, p, engine);
    int left = from;
    int right = middle;
    int k = from;
    while (left < middle && right < to) {
      if (leftFirst(elements[index[left]], elements[index[right]], p, engine)) {
        buffer[k++] = index[left++];
      } else {
        buffer[k++] = index[right++];
      }
    }
    while (left < middle) {
      buffer[k++] = index[left++];
    }
    while (right < to) {
      buffer[k++] = index[right++];
    }
    System.arraycopy(buffer, from, index, from, to - from);
  }

  private static boolean leftFirst(IExpr a, IExpr b, IExpr p, EvalEngine engine) {
    OperationSystem.checkInterrupt();
    IExpr result = engine.evaluate(F.binaryAST2(p, a, b));
    return !(result.isFalse() || result.isMinusOne());
  }
}
