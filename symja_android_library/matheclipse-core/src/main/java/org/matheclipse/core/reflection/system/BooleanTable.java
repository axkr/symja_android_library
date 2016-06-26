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
 * See <a href="https://en.wikipedia.org/wiki/Truth_table">Wikipedia: Truth
 * table</a>
 * 
 */
public class BooleanTable extends AbstractFunctionEvaluator {

	public BooleanTable() {
		// default ctor
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

		IAST resultList = F.List();
		booleanTable(ast.arg1(), variables, 1, resultList);
		return resultList;
	}

	private static void booleanTable(IExpr expr, IAST variables, int position, IAST resultList) {
		if (variables.size() <= position) {
			resultList.add(EvalEngine.get().evalTrue(expr) ? F.True : F.False);
			return;
		}
		IExpr sym = variables.get(position);
		if (sym.isSymbol()) {
			try {
				((ISymbol) sym).pushLocalVariable(F.True);
				booleanTable(expr, variables, position + 1, resultList);
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
			try {
				((ISymbol) sym).pushLocalVariable(F.False);
				booleanTable(expr, variables, position + 1, resultList);
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
		}
	}
}