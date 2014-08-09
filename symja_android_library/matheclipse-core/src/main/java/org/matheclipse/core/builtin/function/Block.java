package org.matheclipse.core.builtin.function;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Block extends AbstractCoreFunctionEvaluator {
	public Block() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList()) {
			final EvalEngine engine = EvalEngine.get();
			final IAST blockVariablesList = (IAST) ast.arg1();
			final List<ISymbol> variables = new ArrayList<ISymbol>();
			IExpr result;
			IExpr temp;

			try {
				// remember which local variables we use:
				ISymbol blockVariableSymbol;

				for (int i = 1; i < blockVariablesList.size(); i++) {
					if (blockVariablesList.get(i).isSymbol()) {
						blockVariableSymbol = (ISymbol) blockVariablesList.get(i);
						blockVariableSymbol.pushLocalVariable();
						variables.add(blockVariableSymbol);
					} else {
						if (blockVariablesList.get(i).isAST(F.Set, 3)) {
							// lhs = rhs
							final IAST setFun = (IAST) blockVariablesList.get(i);
							if (setFun.arg1().isSymbol()) {
								blockVariableSymbol = (ISymbol) setFun.arg1();
								blockVariableSymbol.pushLocalVariable();
								// this evaluation step may throw an exception
								temp = engine.evaluate(setFun.arg2());
								blockVariableSymbol.set(temp);
								variables.add(blockVariableSymbol);
							}
						}
					}
				}

				result = engine.evaluate(ast.get(2));
			} finally {
				// pop all local variables from local variable stack
				for (int i = 0; i < variables.size(); i++) {
					variables.get(i).popLocalVariable();
				}
			}

			return result;
		}

		return null;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
