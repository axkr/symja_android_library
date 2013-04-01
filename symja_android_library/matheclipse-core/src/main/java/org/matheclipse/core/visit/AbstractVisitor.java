package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractVisitor<T> implements IVisitor<T> {

	public AbstractVisitor() {
		super();
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IInteger element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IFraction element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IComplex element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(INum element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IComplexNum element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(ISymbol element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IPattern element) {
		return null;
	}

	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IPatternSequence element) {
		return null;
	}
	
	/**
	 * 
	 * @return <code>null</code>, if no evaluation is possible
	 */
	public T visit(IStringX element) {
		return null;
	}

}