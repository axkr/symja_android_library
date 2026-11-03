package org.matheclipse.core.sympy.core.strategies.branch;

import java.util.Iterator;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Port of <code>sympy.strategies.branch.tools</code>.
 */
public final class BranchTools {

  private BranchTools() {}

  /**
   * Strategy for canonicalization. Apply each branching rule in a top-down fashion through the tree,
   * multiplex through all of these traversals, and keep doing this until there is no change.
   *
   * @param rules
   * @return
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  public static Function<IExpr, Iterator<IExpr>> canon(Function<IExpr, Iterator<IExpr>>... rules) {
    final Function<IExpr, Iterator<IExpr>>[] traversals = new Function[rules.length];
    for (int i = 0; i < rules.length; i++) {
      traversals[i] = BranchTraverse.topDown(rules[i]);
    }
    return BranchCore.exhaust(BranchCore.multiplex(traversals));
  }
}
