package org.matheclipse.core.sympy.core.traversal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The common part of {@link PreorderTraversal} and {@link PostorderTraversal}: which nodes have
 * children, and in which order the children are visited.
 *
 * <p>
 * Like the generators of sympy an instance is an iterator and its own {@link Iterable}, so it can
 * be consumed only once.
 */
abstract class AbstractTraversal implements Iterator<IExpr>, Iterable<IExpr> {
  final FrameStack frames = new FrameStack();

  /** The order of the arguments, or <code>null</code> to keep the order of the expression. */
  private final Comparator<? super IExpr> keys;

  /** <code>0</code> if the heads are children too, <code>1</code> otherwise. */
  private final int start;

  AbstractTraversal(Comparator<? super IExpr> keys, boolean heads) {
    this.keys = keys;
    this.start = heads ? 0 : 1;
  }

  final boolean hasChildren(IExpr expr) {
    return expr.isASTOrAssociation() && expr.size() > start;
  }

  /** Push a node for which {@link #hasChildren(IExpr)} is <code>true</code>. */
  final void descend(IExpr expr) {
    final IAST node = (IAST) expr;
    frames.push(node, keys == null ? node : sorted(node), start);
  }

  private IAST sorted(IAST node) {
    final int size = node.size();
    final IExpr[] args = new IExpr[size - 1];
    for (int i = 1; i < size; i++) {
      args[i - 1] = node.get(i);
    }
    Arrays.sort(args, keys);
    final IASTAppendable result = F.ast(node.head(), size);
    result.appendAll(args, 0, args.length);
    return result;
  }

  @Override
  public final Iterator<IExpr> iterator() {
    return this;
  }
}
