package org.matheclipse.core.eval;

import java.io.Writer;

import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Convert an expression into TeX output
 * 
 */
public class TeXUtilities {
	protected EvalEngine fEvalEngine;

	protected TeXFormFactory fTeXFactory;

	ExprParser fParser;

	/**
	 * 
	 * @param evalEngine
	 * @param relaxedSyntax
	 *            if <code>true</code> use '(...)' instead of '[...]' to
	 *            parenthesize the arguments of a function.
	 */
	public TeXUtilities(final EvalEngine evalEngine, final boolean relaxedSyntax) {
		fEvalEngine = evalEngine;
		// set the thread local instance
		EvalEngine.set(evalEngine);
		fTeXFactory = new TeXFormFactory();
		fParser = new ExprParser(evalEngine, relaxedSyntax);
	}

	/**
	 * Converts the inputExpression string into a TeX expression and writes the
	 * result to the given <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	synchronized public void toTeX(final String inputExpression, final Writer out) {
		IExpr parsedExpression = null;
		if (inputExpression != null) {
			try {
				parsedExpression = fParser.parse(inputExpression);
				// parsedExpression = AST2Expr.CONST.convert(node);
			} catch (final Throwable e) {
				// parsedExpression == null ==> fError occured
			}
		}
		toTeX(parsedExpression, out);
	}

	/**
	 * Converts the objectExpression into a TeX expression and writes the result
	 * to the given <code>Writer</code>
	 * 
	 * @param objectExpression
	 * @param out
	 */
	synchronized public void toTeX(final IExpr objectExpression, final Writer out) {
		final StringBuilder buf = new StringBuilder();

		if (objectExpression != null) {
			IExpr result = objectExpression;
			if (objectExpression.isAST()) {
				result = fEvalEngine.evalHoldPattern((IAST) objectExpression, true);
			}
			fTeXFactory.convert(buf, result, 0);
			try {
				out.write(buf.toString());
			} catch (final Throwable e) {
				// parsedExpression == null ==> fError occured
			}
		}
	}

	public void stopRequest() {
		fEvalEngine.stopRequest();
	}
}
