package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ReplaceAll extends AbstractEvaluator {

	public ReplaceAll() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST1()) {
			return F.operatorFormAST1(ast);
		}
		Validate.checkSize(ast, 3);
		try {
			if (ast.arg2().isListOfLists()) {
				IAST list = (IAST) ast.arg2();
				IAST result = F.ListAlloc(list.size());
				for (IExpr subList : list) {
					IExpr temp = engine.evaluate(subList);
					if (temp.isAST()) {
						result.append(F.subst(ast.arg1(), (IAST) temp));
					}
				}
				return result;
			}
			if (ast.arg2().isAST()) {
				IExpr temp = engine.evaluate(ast.arg2());
				if (temp.isAST()) {
					return F.subst(ast.arg1(), (IAST) temp);
				}
			} else {
				WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
				engine.printMessage(wat.getMessage());
			}
		} catch (WrongArgumentType wat) {
			engine.printMessage(wat.getMessage());
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDREST);
	}
}
