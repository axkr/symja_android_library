package org.matheclipse.core.eval;

import java.io.Writer;

import org.matheclipse.core.form.mathml.MathMLContentFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Convert an expression into content MathML output
 * 
 * 
 */
public class MathMLContentUtilities {
	protected EvalEngine fEvalEngine;

	protected MathMLContentFormFactory fMathMLFactory;

	ExprParser fParser;

	public MathMLContentUtilities(final EvalEngine evalEngine, final boolean relaxedSyntax) {
		fEvalEngine = evalEngine;
		// set the thread local instance
		startRequest();

		fMathMLFactory = new MathMLContentFormFactory();

		fParser = new ExprParser(evalEngine, relaxedSyntax);
	}

	/**
	 * Converts the inputExpression string into a MathML expression and writes the result to the given
	 * <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	synchronized public void toMathML(final String inputExpression, final Writer out) {
		IExpr parsedExpression = null;
		if (inputExpression != null) {
			try {
				parsedExpression = fParser.parse(inputExpression);
			} catch (final Throwable e) {
				return;
				// parsedExpression == null ==> fError occured
			}
		}
		toMathML(parsedExpression, out);
	}

	/**
	 * Converts the objectExpression into a MathML expression and writes the result to the given <code>Writer</code>
	 * 
	 * @param objectExpression
	 * @param out
	 */
	synchronized public void toMathML(final IExpr objectExpression, final Writer out) {
		final StringBuilder buf = new StringBuilder();

		if (objectExpression != null) {
			fMathMLFactory.convert(buf, objectExpression, Integer.MIN_VALUE, false);
			try {

				out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				out.write("<math:math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n");
				out.write(buf.toString());
				out.write("\n</math:math>");

			} catch (final Throwable e) {
				// parsedExpression == null ==> fError occured
			}
		}
	}

	// synchronized public void toJava(final String inputExpression, final
	// Writer out, boolean strictJava) {
	// IExpr parsedExpression = null;
	// ASTNode node;
	// if (inputExpression != null) {
	// try {
	// node = fParser.parse(inputExpression);
	// parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
	// out.write(JavaForm.javaForm(parsedExpression, strictJava));
	// // out.write(parsedExpression.internalFormString(strictJava, 0));
	// } catch (final Throwable e) {
	// return;
	// // parsedExpression == null ==> fError occured
	// }
	// }
	// }

	/**
	 * Assign the associated EvalEngine to the current thread. Every subsequent action evaluation in this thread affects
	 * the EvalEngine in this class.
	 */
	public void startRequest() {
		EvalEngine.set(fEvalEngine);
	}

	/**
	 * Stop the current evaluation thread.
	 */
	public void stopRequest() {
		fEvalEngine.stopRequest();
	}
}
