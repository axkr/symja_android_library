package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class PadLeft extends AbstractFunctionEvaluator {

	public PadLeft() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		int n = Validate.checkIntType(ast, 2);

		if (ast.get(1).isAST()) {
			IAST arg1 = (IAST) ast.get(1);
			if (ast.size() > 3) {
				if (ast.get(3).isList()) {
					IAST arg2 = (IAST) ast.get(3);
					return padLeftAST(arg1, n, arg2);
				} else {
					return padLeftAtom(arg1, n, ast.get(3));
				}
			} else {
				return padLeftAtom(arg1, n, F.C0);
			}
		}
		return null;
	}

	public static IExpr padLeftAtom(IAST ast, int n, IExpr atom) {
		int length = n - ast.size() + 1;
		if (length > 0) {
			IAST result = ast.copyHead();
			for (int i = 0; i < length; i++) {
				result.add(atom);
			}
			result.addAll(ast);
			return result;
		}
		return ast;
	}

	public static IAST padLeftAST(IAST ast, int n, IAST arg2) {
		int length = n - ast.size() + 1;
		if (length > 0) {

			IAST result = ast.copyHead();
			if (arg2.size() < 2) {
				return ast;
			}
			int j = 1;
			if ((arg2.size() - 1) < n) {
				int temp = n % (arg2.size() - 1);
				j = arg2.size() - temp;
			}
			for (int i = 0; i < length; i++) {
				if (j < arg2.size()) {
					result.add(arg2.get(j++));
				} else {
					j = 1;
					result.add(arg2.get(j++));
				}
			}
			result.addAll(ast);
			return result;
		}
		return ast;
	}
}
