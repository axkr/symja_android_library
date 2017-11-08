package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A visitor which could be used in the
 * <code>org.matheclipse.core.interfaces.IExpr#accept()</code> method.
 * 
 * @param <T>
 * 
 * @see org.matheclipse.core.interfaces.IExpr
 */
public interface IVisitor<T> {

	public abstract T visit(IInteger element);

	public abstract T visit(IFraction element);

	public abstract T visit(IComplex element);

	public abstract T visit(INum element);

	public abstract T visit(IComplexNum element);

	public abstract T visit(ISymbol element);

	public abstract T visit(IPattern element);

	public abstract T visit(IPatternSequence element);
	
	public abstract T visit(IStringX element);

	public abstract T visit(IASTMutable ast);

}