package org.matheclipse.core.sympy.core.strategies.branch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.Traversal;
import org.matheclipse.core.sympy.core.traversal.Traversal.Fns;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Port of <code>sympy.strategies.branch.traverse</code>, the branching strategies to traverse a
 * tree. The number of results is the product of the results of the arguments, and the recursion
 * follows the depth of the tree, see {@link BranchCore}.
 */
public final class BranchTraverse {

  private BranchTraverse() {}

  /**
   * Apply a branching rule down a tree, running it on the top nodes first.
   *
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> topDown(Function<IExpr, Iterator<IExpr>> brule) {
    return topDown(brule, Traversal.BASIC_FNS);
  }

  /**
   * Apply a branching rule down a tree, running it on the top nodes first.
   *
   * @param brule
   * @param fns
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Function<IExpr, Iterator<IExpr>> topDown(Function<IExpr, Iterator<IExpr>> brule,
      Fns fns) {
    return BranchCore.chain(BranchCore.doOne(brule, BranchCore.identity()),
        expr -> sall(topDown(brule, fns), fns).apply(expr));
  }

  /**
   * Strategic all: apply a branching rule to the arguments of a node, and yield one node for each
   * combination of the results. The last argument changes fastest, as in
   * <code>itertools.product</code>.
   *
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> sall(Function<IExpr, Iterator<IExpr>> brule) {
    return sall(brule, Traversal.BASIC_FNS);
  }

  /**
   * Strategic all: apply a branching rule to the arguments of a node.
   *
   * @param brule
   * @param fns
   * @return
   * @see #sall(Function)
   */
  public static Function<IExpr, Iterator<IExpr>> sall(Function<IExpr, Iterator<IExpr>> brule,
      Fns fns) {
    return expr -> {
      if (fns.isLeaf(expr)) {
        return Iterators.singletonIterator(expr);
      }
      final IAST ast = (IAST) expr;
      final List<List<IExpr>> choices = new ArrayList<List<IExpr>>(ast.argSize());
      for (int i = 1; i < ast.size(); i++) {
        // like itertools.product, which reads its inputs completely
        choices.add(Lists.newArrayList(brule.apply(ast.get(i))));
      }
      return new AbstractIterator<IExpr>() {
        // an odometer: the number of combinations has no limit, only the combinations asked for
        // are built
        private final int[] positions = new int[choices.size()];
        private boolean exhausted = choices.stream().anyMatch(List::isEmpty);

        @Override
        protected IExpr computeNext() {
          if (exhausted) {
            return endOfData();
          }
          IASTMutable result = F.NIL;
          for (int i = 0; i < positions.length; i++) {
            result = Traversal.setArgument(ast, result, i + 1, choices.get(i).get(positions[i]));
          }
          advance();
          return fns.rebuild(result);
        }

        private void advance() {
          int i = positions.length - 1;
          while (i >= 0 && ++positions[i] == choices.get(i).size()) {
            positions[i--] = 0;
          }
          exhausted = i < 0;
        }
      };
    };
  }
}
