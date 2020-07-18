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
public interface IVisitorInt {
	public int visit(IInteger element);

	public int visit(IFraction element);

	public int visit(IComplex element);

	public int visit(INum element);

	public int visit(IComplexNum element);

	public int visit(ISymbol element);

	public int visit(IPattern element);

	public int visit(IPatternSequence element); 
	
	public int visit(IStringX element);

	public int visit(IAST list);
}