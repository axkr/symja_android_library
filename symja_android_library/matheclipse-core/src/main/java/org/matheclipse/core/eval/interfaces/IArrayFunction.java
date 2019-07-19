package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public interface IArrayFunction {
	IExpr evaluate(IExpr[] index);
}
