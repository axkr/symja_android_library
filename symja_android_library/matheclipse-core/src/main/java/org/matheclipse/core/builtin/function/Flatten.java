package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * TODO implement &quot;Flatten&quot; function (especially Flatten(list, 1) )
 * 
 */
public class Flatten extends AbstractCoreFunctionEvaluator {

	public Flatten() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2);

		IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			IAST arg1AST = (IAST) arg1;
			if (ast.isAST1()) {
				IAST resultList = EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1);
				if (resultList.isPresent()) {
					return resultList;
				}
				return arg1AST;
			} else if (ast.isAST2()) {
				IExpr arg2 = engine.evaluate(ast.arg2());

				int level = Validate.checkIntLevelType(arg2);
				if (level > 0) {
					IAST resultList = F.ast(arg1AST.topHead());
					if (EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1, resultList, 0, level)) {
						return resultList;
					}
				}
				return arg1;
			} else if (ast.isAST3() && ast.arg3().isSymbol()) {
				IExpr arg2 = engine.evaluate(ast.arg2());

				int level = Validate.checkIntLevelType(arg2);
				if (level > 0) {
					IAST resultList = F.ast(arg1AST.topHead());
					if (EvalAttributes.flatten((ISymbol) ast.arg3(), (IAST) arg1, resultList, 0, level)) {
						return resultList;
					}
				}
				return arg1;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
