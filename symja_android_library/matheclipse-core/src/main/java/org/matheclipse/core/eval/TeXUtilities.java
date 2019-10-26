package org.matheclipse.core.eval;

import java.io.IOException;
import java.io.Writer;

import org.matheclipse.core.basic.Config;
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
	 *            if <code>true</code> use '(...)' instead of '[...]' to parenthesize the arguments of a function.
	 */
	public TeXUtilities(final EvalEngine evalEngine, final boolean relaxedSyntax) {
		fEvalEngine = evalEngine;
		// set the thread local instance
		EvalEngine.set(evalEngine);
		fTeXFactory = new TeXFormFactory();
		fParser = new ExprParser(evalEngine, relaxedSyntax);
	}

	public TeXUtilities(final EvalEngine evalEngine, final boolean relaxedSyntax, int exponentFigures,
			int significantFigures) {
		fEvalEngine = evalEngine;
		// set the thread local instance
		EvalEngine.set(evalEngine);
		fTeXFactory = new TeXFormFactory(exponentFigures, significantFigures);
		fParser = new ExprParser(evalEngine, relaxedSyntax);
	}

	/**
	 * Converts the inputExpression string into a TeX expression and writes the result to the given <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	synchronized public boolean toTeX(final String inputExpression, final Writer out) {
		IExpr parsedExpression = null;
		if (inputExpression != null) {
			try {
				parsedExpression = fParser.parse(inputExpression);
				return toTeX(parsedExpression, out);
				// parsedExpression = AST2Expr.CONST.convert(node);
			} catch (final RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Converts the objectExpression into a TeX expression and writes the result to the given <code>Writer</code>
	 * 
	 * @param objectExpression
	 * @param out
	 */
	synchronized public boolean toTeX(final IExpr objectExpression, final Writer out) {
		final StringBuilder buf = new StringBuilder();

		if (objectExpression != null) {
			try {
				IExpr result = objectExpression;
				if (objectExpression.isAST()) {
					fEvalEngine.reset();
					result = fEvalEngine.evalHoldPattern((IAST) objectExpression, true, true);
				}

				if (fTeXFactory.convert(buf, result, 0)) {
					out.write(buf.toString());
					return true;
				} else {
					out.write("ERROR-IN-TEXFORM");
				}
			} catch (final IOException ioe) {
				// parsedExpression == null ==> fError occured
			} catch (final RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}

	public void stopRequest() {
		fEvalEngine.stopRequest();
	}
}
