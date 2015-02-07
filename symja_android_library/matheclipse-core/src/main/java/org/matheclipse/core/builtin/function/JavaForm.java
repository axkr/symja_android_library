package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol; 

/**
 * Return the internal Java form of this expression. The Java form is useful for generating Symja programming expressions.
 */
public class JavaForm implements IFunctionEvaluator {

	public JavaForm() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = F.eval(ast.arg1());
		boolean strictJava = false;
		if (ast.size() == 3) {
			IExpr arg2 = F.eval(ast.arg2());
			final Options options = new Options(ast.topHead(), arg2);
			strictJava = options.isOption("Strict");
		}
		String resultStr = javaForm(arg1, strictJava);
		return F.stringx(resultStr);
	}

	public static String javaForm(IExpr arg1, boolean strictJava) {
		// necessary for MathMLContentUtilities#toJava() method
		// if (arg1.isAST()) {
		// arg1 = PatternMatcher.evalLeftHandSide((IAST) arg1);
		// }
		return arg1.internalFormString(strictJava, false, 0);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
		// symbol.setAttributes(ISymbol.HOLDALL);
	}
}
