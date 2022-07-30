package org.matheclipse.core.expression.data;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** Create a DefaultWeightedEdge for usage in jgrapht.org library */
public class ExprWeightedEdge extends DefaultWeightedEdge implements IExprEdge {
  /** */
  private static final long serialVersionUID = -2672050124122743886L;

  public ExprWeightedEdge() {
    super();
  }

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

  public double weight() {
    return getWeight();
  }
}
