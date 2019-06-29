package org.matheclipse.core.expression;

import org.jgrapht.graph.DefaultEdge;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a DefaultEdge for usage in jgrapht.org library
 *
 */
public class IExprEdge extends DefaultEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2672050124122743886L;

	public IExprEdge() {
		super();
	}

	public IExpr lhs() {
		return (IExpr) getSource();
	}

	public IExpr rhs() {
		return (IExpr) getTarget();
	}
}
