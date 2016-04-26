package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See <a href="https://en.wikipedia.org/wiki/Tautology_%28logic%29">Wikipedia:
 * Tautology_</a>
 * 
 */
public class TautologyQ extends AbstractFunctionEvaluator {

	public TautologyQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IAST variables;
		if (ast.size() == 3) {
			if (ast.arg2().isList()) {
				variables = (IAST) ast.arg2();
			} else {
				variables = List(ast.arg2());
			}
		} else {
			VariablesSet vSet = new VariablesSet(ast.arg1());
			variables = vSet.getVarList();

		}

		return tautologyQ(ast.arg1(), variables, 1) ? F.True : F.False;
	}

	private boolean tautologyQ(IExpr expr, IAST variables, int position) {
		if (variables.size() <= position) {
			return EvalEngine.get().evalTrue(expr);
		}
		IExpr sym = variables.get(position);
		if (sym.isSymbol()) {
			try {
				((ISymbol) sym).pushLocalVariable(F.False);
				if (!tautologyQ(expr, variables, position + 1)) {
					return false;
				}
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
			try {
				((ISymbol) sym).pushLocalVariable(F.True);
				if (!tautologyQ(expr, variables, position + 1)) {
					return false;
				}
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
		}
		return true;
	}
}