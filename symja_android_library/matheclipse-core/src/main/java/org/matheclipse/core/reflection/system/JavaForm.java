package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Return the internal Java form of this expression. The Java form is useful for
 * generating MathEclipse programming expressions.
 */
public class JavaForm implements IFunctionEvaluator {

	public JavaForm() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		boolean strictJava = false;
		if (ast.size() == 3) {
			final Options options = new Options(ast.topHead(), ast, 2);
			strictJava = options.isOption("Strict");
		}
		return F.stringx(new StringBuffer(ast.get(1).internalFormString(strictJava, 0)));
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
		// symbol.setAttributes(ISymbol.HOLDALL);
	}
}
