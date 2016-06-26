package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the dimensions of an expression
 */
public class Dimensions extends AbstractFunctionEvaluator {

	public Dimensions() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		int n = Integer.MAX_VALUE;
		if (ast.isAST2() && ast.arg2().isInteger()) {
			n = Validate.checkIntType(ast, 2);
		}
		if (ast.arg1().isAST()) {
			IAST res = F.List();
			if (n > 0) {
				IAST list = (IAST) ast.arg1();
				IExpr header = list.head();
				ArrayList<Integer> dims = getDimensions(list, header, n - 1);
				for (int i = 0; i < dims.size(); i++) {
					res.add(F.integer(dims.get(i).intValue()));
				}
			}
			return res;
		}

		return F.List();

	}

	public static ArrayList<Integer> getDimensions(IAST ast, IExpr header, int maxLevel) {
		return getDimensions(ast, header, maxLevel, new ArrayList<Integer>());
	}

	public static ArrayList<Integer> getDimensions(IAST ast, IExpr header, int maxLevel, ArrayList<Integer> dims) {
		int size = ast.size();
		dims.add(size - 1);
		if (size > 1 && ast.arg1().isAST()) {
			IAST arg1AST = (IAST) ast.arg1();
			int arg1Size = arg1AST.size();
			if (!header.equals(arg1AST.head())) {
				return dims;
			}
			if (maxLevel > 0) {
				for (int i = 2; i < size; i++) {
					if (!ast.get(i).isAST()) {
						return dims;
					}
					if (arg1Size != ((IAST) ast.get(i)).size()) {
						return dims;
					}
				}
				getDimensions(arg1AST, header, maxLevel - 1, dims);
			}
		}
		return dims;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
