package org.matheclipse.core.expression.data;

import org.matheclipse.core.interfaces.IExpr;

/**
 * A lightweight interface representing an edge-like pair of expressions for using in the
 * jgrapht.org library.
 */
public interface IExprEdge {
  /**
   * Returns the left-hand side expression of this edge.
   *
   * @return the {@link IExpr} representing the left-hand side
   */
  public IExpr lhs();

  /**
   * Returns the right-hand side expression of this edge.
   *
   * @return the {@link IExpr} representing the right-hand side
   */
  public IExpr rhs();
}
