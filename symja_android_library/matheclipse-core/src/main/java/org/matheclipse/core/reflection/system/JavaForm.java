package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Return the internal Java form of this expression. The Java form is useful for generating MathEclipse programming expressions.
 */
public class JavaForm implements IFunctionEvaluator {

	public JavaForm() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		boolean strictJava = false;
		if (ast.size() == 3) {
			final Options options = new Options(ast.topHead(), ast, 2);
			// TODO check if option is working in lowercase mode
			strictJava = options.isOption("Strict");
		}
		String resultStr = javaForm(ast.get(1), strictJava);
		return F.stringx(resultStr);
	}

	public static String javaForm(IExpr arg1, boolean strictJava) {
		// necessary for MathMLContentUtilities#toJava() method
		// if (arg1.isAST()) {
		// arg1 = PatternMatcher.evalLeftHandSide((IAST) arg1);
		// }
		String resultStr = arg1.internalFormString(strictJava, 0);
		return resultStr;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
		// symbol.setAttributes(ISymbol.HOLDALL);
	}
}
