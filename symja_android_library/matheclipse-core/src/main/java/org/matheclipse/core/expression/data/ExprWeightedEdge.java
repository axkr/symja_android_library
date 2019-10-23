package org.matheclipse.core.expression.data;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a DefaultWeightedEdge for usage in jgrapht.org library
 *
 */
public class ExprWeightedEdge extends DefaultWeightedEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2672050124122743886L;

	public ExprWeightedEdge() {
		super();
	}

	public IExpr lhs() {
		return (IExpr) getSource();
	}

	public IExpr rhs() {
		return (IExpr) getTarget();
	}

	public double weight() {
		return getWeight();
	}
}
