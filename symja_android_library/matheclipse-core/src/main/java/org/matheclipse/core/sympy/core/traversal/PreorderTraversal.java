package org.matheclipse.core.sympy.core.traversal;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Pre-order traversal of an expression tree, a port of the sympy class
 * <code>preorder_traversal</code>. A node is returned before its arguments, and {@link #skip()}
 * leaves out the subtree of the node returned last.
 *
 * <h3>Compared with Symja</h3>
 * <ul>
 * <li>{@link IExpr#has(java.util.function.Predicate, boolean)} visits the nodes in this order, and
 * stops when its predicate answers <code>true</code>. It is the faster choice for a yes/no question
 * because it needs no frames, but it recurses, cannot leave out a subtree, and hands the nodes to a
 * callback instead of to a loop.
 * <li>{@link org.matheclipse.core.visit.VisitorReplaceAll} also descends from the top, but rewrites
 * and doesn't visit into a node it replaced. Leaving out a subtree here corresponds to the
 * predicate of {@link org.matheclipse.core.visit.VisitorPredicateReplaceAll}.
 * <li>{@link org.matheclipse.core.visit.VisitorPredicate} is the same walk as a visitor.
 * <li>The arguments of an association are its values, as in <code>has()</code>; the keys are not
 * visited.
 * </ul>
 *
 * <p>
 * The walk uses an explicit stack, so the depth of the tree is limited by the heap and not by the
 * thread's stack. Memory is proportional to the depth and not to the width of the tree.
 */
public final class PreorderTraversal extends AbstractTraversal {
  private IExpr last;

  /** <code>true</code> until the root was returned */
  private boolean atRoot = true;

  private boolean skipFlag = false;

  /**
   * Iterate in the order of the arguments of the expression, without the heads.
   *
   * @param node the expression to traverse
   */
  public PreorderTraversal(IExpr node) {
    this(node, null, false);
  }

  /**
   * @param node the expression to traverse
   * @param keys sorts the arguments of each node, or <code>null</code> to keep their order. sympy's
   *        <code>keys=True</code> is {@link Comparator#naturalOrder()}, which makes the result
   *        independent of the arguments' order.
   * @param heads if <code>true</code>, visit the head of each node before its arguments
   */
  public PreorderTraversal(IExpr node, Comparator<? super IExpr> keys, boolean heads) {
    super(keys, heads);
    last = node;
  }

  /**
   * Skip the subtree of the node returned last by {@link #next()}.
   */
  public void skip() {
    skipFlag = true;
  }

  @Override
  public boolean hasNext() {
    return atRoot || !frames.isEmpty() || (!skipFlag && hasChildren(last));
  }

  @Override
  public IExpr next() {
    if (atRoot) {
      atRoot = false;
      return last;
    }
    // the descent waits for this call, because skip() is called after a node was returned
    if (skipFlag) {
      skipFlag = false;
    } else if (hasChildren(last)) {
      descend(last);
    }
    if (frames.isEmpty()) {
      throw new NoSuchElementException();
    }
    final int top = frames.top;
    last = frames.aux[top].get(frames.index[top]++);
    // no exhausted frame stays on the stack, so hasNext() needs no search
    while (!frames.isEmpty() && frames.index[frames.top] >= frames.aux[frames.top].size()) {
      frames.pop();
    }
    return last;
  }
}
