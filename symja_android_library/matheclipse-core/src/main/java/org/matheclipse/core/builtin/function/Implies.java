package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 * 
 */
public class Implies extends AbstractFunctionEvaluator {
	public Implies() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		
		IExpr arg1=F.eval(ast.arg1());
		if (arg1.isTrue()){
			return ast.arg2();
		}
		if (arg1.isFalse()){
			return F.True;
		}
		IExpr arg2=F.eval(ast.arg2());
		if (arg2.isTrue()){
			return F.True;
		}
		if (arg2.isFalse()){
			return F.Not(arg1);
		}
		if (arg1.equals(arg2)){
			return F.True;
		}
		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
