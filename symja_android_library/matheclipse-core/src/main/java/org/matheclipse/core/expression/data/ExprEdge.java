package org.matheclipse.core.expression.data;

import org.jgrapht.graph.DefaultEdge;
import org.matheclipse.core.interfaces.IExpr;

/** Create a DefaultEdge for usage in jgrapht.org library */
public class ExprEdge extends DefaultEdge implements IExprEdge {

  private static final long serialVersionUID = -38022260879220117L;

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
