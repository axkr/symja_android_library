package org.matheclipse.core.sympy.core.strategies;

import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.Traversal.Fns;

/**
 * Port of <code>sympy.strategies.tools</code>, conglomerate strategies built from the other
 * classes of this package.
 */
public final class Tools {

  private Tools() {}

  /**
   * Full simultaneous exact substitution.
   *
   * <p>
   * sympy tries one rule per entry at each node. A map lookup gives the same result, because the
   * keys are different from each other, and costs one <code>hashCode()</code> per node instead of
   * one comparison per entry. As in {@link Traverse#topDown(Function)} the replacements are visited
   * too, unlike in {@link org.matheclipse.core.interfaces.IExpr#replaceAll(java.util.Map)}, which
   * uses the visitor that stops at a replacement.
   *
   * @param map
   * @return
   */
  public static Function<IExpr, IExpr> subs(Map<? extends IExpr, ? extends IExpr> map) {
    return subs(map, Util.BASIC_FNS);
  }

  /**
   * Full simultaneous exact substitution.
   *
   * @param map
   * @param fns
   * @return
   * @see #subs(Map)
   */
  public static Function<IExpr, IExpr> subs(Map<? extends IExpr, ? extends IExpr> map, Fns fns) {
    if (map.isEmpty()) {
      return Core.identity();
    }
    return Traverse.topDown(expr -> {
      final IExpr result = map.get(expr);
      return result == null ? F.NIL : result;
    }, fns);
  }

  /**
   * Strategy for canonicalization: apply the first rule which works to each node, as long as one
   * works, from the top of the tree down. Keep doing this until there is no change. The docstring
   * of the sympy function says bottom-up, the code is top-down.
   *
   * <p>
   * This is <code>exhaust(topDown(exhaust(doOne(rules))))</code>, the normal form loop which the
   * evaluator runs on each expression through its down rules. Use it on structures which aren't
   * evaluated. The outer loop rewalks the whole tree, so a change in one leaf costs a full pass.
   *
   * @param rules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, IExpr> canon(Function<IExpr, IExpr>... rules) {
    return canon(Util.BASIC_FNS, rules);
  }

  /**
   * Strategy for canonicalization.
   *
   * @param fns
   * @param rules
   * @return
   * @see #canon(Function...)
   */
  @SafeVarargs
  public static Function<IExpr, IExpr> canon(Fns fns, Function<IExpr, IExpr>... rules) {
    return Core.exhaust(Traverse.topDown(Core.exhaust(Core.doOne(rules)), fns));
  }

  /**
   * Apply rules based on the head of the expression. sympy dispatches on the type.
   *
   * @param ruletypes a map from head to rule
   * @return
   */
  public static Function<IExpr, IExpr> typed(
      Map<? extends IExpr, Function<IExpr, IExpr>> ruletypes) {
    return Core.switchRule(IExpr::head, ruletypes);
  }
}
