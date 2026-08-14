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
 *
 * <p>
 * A graph which contains both {@code DirectedEdge} and {@code UndirectedEdge} entries ("mixed
 * graph") has no counterpart in the JGraphT library. Such a graph is stored as a directed
 * pseudograph in which the edges which were entered as undirected carry the {@link #isUndirected()}
 * flag. Parallel edges of a multigraph are distinguished by {@link #id()}. Both properties take
 * part in {@link #equals(Object)} so that they don't collapse in the edge maps of JGraphT. For a
 * plain simple graph both properties keep their default values and this class behaves exactly like
 * before.
 * </p>
 */
public class ExprEdge extends DefaultEdge implements IExprEdge {

  private static final long serialVersionUID = -38022260879220117L;

  /** {@code true} if this edge was entered as an {@code UndirectedEdge} of a mixed graph. */
  private final boolean undirected;

  /** Distinguishes parallel edges of a multigraph. {@code 0} for a simple graph. */
  private final int id;

  /**
   * Compares this edge to another object for equality.
   *
   * <p>
   * Two {@code ExprEdge} instances are considered equal if their left-hand side (source) and
   * right-hand side (target) expressions are equal and they agree in {@link #isUndirected()} and
   * {@link #id()}. A reference equality check is performed first for efficiency.
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
      return undirected == edge.undirected && id == edge.id && lhs().equals(edge.lhs())
          && rhs().equals(edge.rhs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs().hashCode() ^ rhs().hashCode() ^ (id * 31) ^ (undirected ? 0x5bf03635 : 0);
  }

  public ExprEdge() {
    this(false, 0);
  }

  /**
   * @param undirected {@code true} if this edge of a mixed graph was entered as an
   *        {@code UndirectedEdge}
   * @param id distinguishes parallel edges of a multigraph
   */
  public ExprEdge(boolean undirected, int id) {
    super();
    this.undirected = undirected;
    this.id = id;
  }

  /**
   * Test if this edge of a mixed graph was entered as an {@code UndirectedEdge}. Always
   * {@code false} for the edges of a purely directed or purely undirected graph.
   */
  public boolean isUndirected() {
    return undirected;
  }

  /** The number which distinguishes parallel edges of a multigraph. */
  public int id() {
    return id;
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
