package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.StieltjesGammaRules;

public class StieltjesGamma extends AbstractFunctionEvaluator implements StieltjesGammaRules {

	public StieltjesGamma() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		if (ast.isAST1()) {

		} else {
			IExpr arg2 = ast.arg2();
		}
		return F.NIL;
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}

}
