package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import static org.matheclipse.core.expression.F.Power;

public class Root extends AbstractArg2 implements INumeric {
	@Override public IExpr e2ObjArg(final IExpr o,final IExpr r) {return Power(o,FractionSym.valueOf(1,Long.parseLong(r.toString())));}
	@Override public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
