package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IAST;
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
 */
public interface IVisitorBoolean {

	public abstract boolean visit(IInteger element);

	public abstract boolean visit(IFraction element);
 
	public abstract boolean visit(IComplex element);

	public abstract boolean visit(INum element);

	public abstract boolean visit(IComplexNum element);

	public abstract boolean visit(ISymbol element);

	public abstract boolean visit(IPattern element);
	
	public abstract boolean visit(IPatternSequence element);
 
	public abstract boolean visit(IStringX element);
 
	public abstract boolean visit(IAST list);

}