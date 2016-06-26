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

public class SatisfiableQ extends AbstractFunctionEvaluator {

	public SatisfiableQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IAST variables;
		if (ast.isAST2()) {
			if (ast.arg2().isList()) {
				variables = (IAST) ast.arg2();
			} else {
				variables = List(ast.arg2());
			}
		} else {
			VariablesSet vSet = new VariablesSet(ast.arg1());
			variables = vSet.getVarList();
		}

		return satisfiableQ(ast.arg1(), variables, 1) ? F.True : F.False;
	}

	private static boolean satisfiableQ(IExpr expr, IAST variables, int position) {
		if (variables.size() <= position) {
			return EvalEngine.get().evalTrue(expr);
		}
		IExpr sym = variables.get(position);
		if (sym.isSymbol()) {
			try {
				((ISymbol) sym).pushLocalVariable(F.True);
				if (satisfiableQ(expr, variables, position + 1)) {
					return true;
				}
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
			try {
				((ISymbol) sym).pushLocalVariable(F.False);
				if (satisfiableQ(expr, variables, position + 1)) {
					return true;
				}
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
		}
		return false;
	}
}