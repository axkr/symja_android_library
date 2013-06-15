package org.matheclipse.core.eval;

import java.io.Writer;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert an expression into TeX output
 * 
 */
public class TeXUtilities {
	protected EvalEngine fEvalEngine;

	// protected ExprFactory fFactory;

	protected TeXFormFactory fTeXFactory;

	Parser fParser;

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
		fParser = new Parser(relaxedSyntax);
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
		ASTNode node;
		if (inputExpression != null) {
			try {
				node = fParser.parse(inputExpression);
				parsedExpression = AST2Expr.CONST.convert(node);
			} catch (final Throwable e) {
				// parsedExpression == null ==> fError occured
			}
		}
		toTeX(parsedExpression, out);
	}

	/**
	 * Converts the objectExpression into a MathML expression and writes the
	 * result to the given <code>Writer</code>
	 * 
	 * @param objectExpression
	 * @param out
	 */
	synchronized public void toTeX(final Object objectExpression, final Writer out) {
		final StringBuffer buf = new StringBuffer();

		if (objectExpression != null) {
			fTeXFactory.convert(buf, objectExpression, 0);
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
