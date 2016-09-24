package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class PadRight extends AbstractFunctionEvaluator {

	public PadRight() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);
		int n = Validate.checkIntType(ast, 2);

		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			if (ast.size() > 3) {
				if (ast.arg3().isList()) {
					IAST arg3 = (IAST) ast.arg3();
					return padRightAST(arg1, n, arg3);
				} else {
					return padRightAtom(arg1, n, ast.arg3());
				}
			} else {
				return padRightAtom(arg1, n, F.C0);
			}
		}
		return F.NIL;
	}

	public static IExpr padRightAtom(IAST ast, int n, IExpr atom) {
		int length = n - ast.size() + 1;
		if (length > 0) {
			IAST result = ast.copyHead();
			result.addArgs(ast);
			for (int i = 0; i < length; i++) {
				result.append(atom);
			}
			return result;
		}
		return ast;
	}

	public static IAST padRightAST(IAST ast, int n, IAST arg2) {
		int length = n - ast.size() + 1;
		if (length > 0) {
			IAST result = ast.copyHead();
			result.addArgs(ast);
			if (arg2.size() < 2) {
				return ast;
			}
			int j = 1;
			for (int i = 0; i < length; i++) {
				if (j < arg2.size()) {
					result.append(arg2.get(j++));
				} else {
					j = 1;
					result.append(arg2.get(j++));
				}
			}
			return result;
		}
		return ast;
	}
}
