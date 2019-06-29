package org.matheclipse.core.expression;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a DefaultWeightedEdge for usage in jgrapht.org library
 *
 */
public class IExprWeightedEdge extends DefaultWeightedEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2672050124122743886L;

	public IExprWeightedEdge() {
		super();
	}

	public IExpr lhs() {
		return (IExpr) getSource();
	}

	public IExpr rhs() {
		return (IExpr) getTarget();
	}

	protected double weight() {
		return getWeight();
	}
}
