package org.matheclipse.core.sympy.core.traversal;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Post-order traversal of an expression tree, a port of the sympy function
 * <code>postorder_traversal</code>. A node is returned after all of its arguments.
 *
 * <h3>Compared with Symja</h3>
 * <ul>
 * <li>{@link org.matheclipse.core.visit.VisitorLevelSpecification} with a collecting function and
 * the level specification <code>{0,Infinity}</code> visits in this order. It is the visitor behind
 * <code>Map</code>, <code>MapAll</code> and <code>Level</code>, it recurses, and it is the only
 * visitor which is limited by <code>$RecursionLimit</code>. It keeps its level in the instance, so
 * one instance can't be used re-entrantly.
 * <li>{@link org.matheclipse.core.visit.VisitorReplaceAllDFS} and
 * {@link Traversal#bottomUp(IExpr, java.util.function.Function)} rewrite in this order.
 * </ul>
 *
 * <p>
 * The walk uses an explicit stack, see {@link FrameStack}. The arguments of an association are its
 * values.
 */
public final class PostorderTraversal extends AbstractTraversal {
  /** The root if it has no children, otherwise <code>null</code>. */
  private IExpr leafRoot;

  /**
   * Iterate in the order of the arguments of the expression, without the heads.
   *
   * @param node the expression to traverse
   */
  public PostorderTraversal(IExpr node) {
    this(node, null, false);
  }

  /**
   * @param node the expression to traverse
   * @param keys sorts the arguments of each node, or <code>null</code> to keep their order. sympy's
   *        <code>keys=True</code> is {@link Comparator#naturalOrder()}.
   * @param heads if <code>true</code>, visit the head of each node before its arguments
   */
  public PostorderTraversal(IExpr node, Comparator<? super IExpr> keys, boolean heads) {
    super(keys, heads);
    if (hasChildren(node)) {
      descend(node);
    } else {
      leafRoot = node;
    }
  }

  @Override
  public boolean hasNext() {
    // every frame still has to return its own node
    return leafRoot != null || !frames.isEmpty();
  }

  @Override
  public IExpr next() {
    if (leafRoot != null) {
      final IExpr result = leafRoot;
      leafRoot = null;
      return result;
    }
    while (!frames.isEmpty()) {
      final int top = frames.top;
      final IAST children = frames.aux[top];
      if (frames.index[top] < children.size()) {
        final IExpr child = children.get(frames.index[top]++);
        if (!hasChildren(child)) {
          return child;
        }
        descend(child);
      } else {
        final IExpr result = frames.nodes[top];
        frames.pop();
        return result;
      }
    }
    throw new NoSuchElementException();
  }
}
