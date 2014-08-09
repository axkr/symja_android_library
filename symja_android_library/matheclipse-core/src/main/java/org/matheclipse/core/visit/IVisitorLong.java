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
public interface IVisitorLong {
	public long visit(IInteger element);

	public long visit(IFraction element);

	public long visit(IComplex element);

	public long visit(INum element);

	public long visit(IComplexNum element);

	public long visit(ISymbol element);

	public long visit(IPattern element);

	public long visit(IPatternSequence element); 
	
	public long visit(IStringX element);

	public long visit(IAST list);
}