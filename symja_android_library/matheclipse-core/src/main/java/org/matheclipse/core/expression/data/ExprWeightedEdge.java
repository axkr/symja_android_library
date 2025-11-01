package org.matheclipse.core.expression.data;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * DefaultWeightedEdge implementation for use with the JGraphT library that represents a weighted
 * edge connecting two {@link IExpr} vertices.
 *
 * <p>
 * This class adapts {@link DefaultWeightedEdge} to the project's expression types by providing
 * typed accessors {@link #lhs()} and {@link #rhs()} which return the source and target vertices
 * cast to {@link IExpr}. The {@link #weight()} accessor exposes the edge weight from
 * {@link DefaultWeightedEdge}.
 * </p>
 *
 * <p>
 * The {@link #equals(Object)} implementation compares both endpoints and the edge weight using a
 * fuzzy double equality test (see {@link Config#DOUBLE_TOLERANCE}).
 * </p>
 */
public class ExprWeightedEdge extends DefaultWeightedEdge implements IExprEdge {
  /** */
  private static final long serialVersionUID = -2672050124122743886L;

  public ExprWeightedEdge() {
    super();
  }

  /**
   * Compare this edge with another object for equality.
   *
   * <p>
   * Two {@code ExprWeightedEdge} instances are considered equal when their left-hand side (source)
   * and right-hand side (target) expressions are equal and their weights are equal within the
   * configured fuzzy tolerance. A reference equality check is performed first.
   * </p>
   *
   * @param obj the object to compare with
   * @return {@code true} if {@code obj} is an {@code ExprWeightedEdge} with equal endpoints and
   *         weight
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ExprWeightedEdge) {
      ExprWeightedEdge edge = (ExprWeightedEdge) obj;
      return lhs().equals(edge.lhs()) && rhs().equals(edge.rhs())
          && F.isFuzzyEquals(weight(), edge.weight(), Config.DOUBLE_TOLERANCE);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs().hashCode() ^ rhs().hashCode();
  }

  @Override
  public IExpr lhs() {
    return (IExpr) getSource();
  }

  @Override
  public IExpr rhs() {
    return (IExpr) getTarget();
  }


  /**
   * Return the numeric weight associated with this edge.
   *
   * @return the edge weight as a {@code double}
   */
  public double weight() {
    return getWeight();
  }
}
