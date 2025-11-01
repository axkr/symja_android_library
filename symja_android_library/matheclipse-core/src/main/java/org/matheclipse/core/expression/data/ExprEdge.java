package org.matheclipse.core.expression.data;

import org.jgrapht.graph.DefaultEdge;
import org.matheclipse.core.interfaces.IExpr;

/**
 * DefaultEdge implementation for use with the JGraphT library that represents an edge connecting
 * two {@link IExpr} vertices.
 *
 * <p>
 * This class adapts {@link DefaultEdge} to the project's expression types by providing typed
 * accessors {@link #lhs()} and {@link #rhs()} which return the source and target vertices cast to
 * {@link IExpr}.
 * </p>
 */
public class ExprEdge extends DefaultEdge implements IExprEdge {

  private static final long serialVersionUID = -38022260879220117L;

  /**
   * Compares this edge to another object for equality.
   *
   * <p>
   * Two {@code ExprEdge} instances are considered equal if their left-hand side (source) and
   * right-hand side (target) expressions are equal. A reference equality check is performed first
   * for efficiency.
   * </p>
   *
   * @param obj the object to compare with
   * @return {@code true} if {@code obj} is an {@code ExprEdge} with equal endpoints, otherwise
   *         {@code false}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ExprEdge) {
      ExprEdge edge = (ExprEdge) obj;
      return lhs().equals(edge.lhs()) && rhs().equals(edge.rhs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs().hashCode() ^ rhs().hashCode();
  }

  public ExprEdge() {
    super();
  }

  @Override
  public IExpr lhs() {
    return (IExpr) getSource();
  }

  @Override
  public IExpr rhs() {
    return (IExpr) getTarget();
  }
}
