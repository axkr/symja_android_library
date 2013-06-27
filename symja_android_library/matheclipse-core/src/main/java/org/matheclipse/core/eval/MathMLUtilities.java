package org.matheclipse.core.eval;

import java.io.Writer;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.mathml.MathMLFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert an expression into presentation MathML output
 * 
 * See <a
 * href="http://www.w3.org/TR/2000/CR-MathML2-20001113/byalpha.html">Chracters
 * ordered by Unicode</a>
 * 
 */
public class MathMLUtilities {
	protected EvalEngine fEvalEngine;

	protected MathMLFormFactory fMathMLFactory;

	/**
	 * MS Internet Explorer client ?
	 */
	boolean fMSIE;

	Parser fParser;

	static {
		// initialize the global available symbols
		F.initSymbols();
	}
	
	public MathMLUtilities(final EvalEngine evalEngine, final boolean msie, final boolean relaxedSyntax) {
		fEvalEngine = evalEngine;
		// set the thread local instance
		startRequest();
		if (msie) {
			fMathMLFactory = new MathMLFormFactory("m:");
		} else {
			fMathMLFactory = new MathMLFormFactory();
		}
		fParser = new Parser(relaxedSyntax);
		fMSIE = msie;
	}

	/**
	 * Converts the inputExpression string into a MathML expression and writes the
	 * result to the given <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	synchronized public void toMathML(final String inputExpression, final Writer out) {
		IExpr parsedExpression = null;
		ASTNode node;
		if (inputExpression != null) {
			try {
				node = fParser.parse(inputExpression);
				parsedExpression = AST2Expr.CONST.convert(node);
			} catch (final Throwable e) {
				return;
				// parsedExpression == null ==> fError occured
			}
		}
		toMathML(parsedExpression, out);
	}

	/**
	 * Converts the objectExpression into a MathML expression and writes the
	 * result to the given <code>Writer</code>
	 * 
	 * @param objectExpression
	 * @param out
	 */
	synchronized public void toMathML(final IExpr objectExpression, final Writer out) {
		final StringBuffer buf = new StringBuffer();

		if (objectExpression != null) {
			fMathMLFactory.convert(buf, objectExpression, 0);
			try {
				if (fMSIE) {
					out.write("<m:math>");
					out.write(buf.toString());
					out.write("</m:math>");
				} else {
					// out.write("<math xmlns=\"&mathml;\">");

					// out.write("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">");
					out.write("<?xml version=\"1.0\"?>\n"
							+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
							+ "<math mode=\"display\">\n");
					out.write(buf.toString());
					out.write("</math>");
				}
			} catch (final Throwable e) {
				// parsedExpression == null ==> fError occured
			}
		}
	}

	synchronized public void toJava(final String inputExpression, final Writer out, boolean strictJava) {
		IExpr parsedExpression = null;
		ASTNode node;
		if (inputExpression != null) {
			try {
				node = fParser.parse(inputExpression);
				parsedExpression = AST2Expr.CONST.convert(node);
				out.write(parsedExpression.internalFormString(strictJava, 0));
			} catch (final Throwable e) {
				return;
				// parsedExpression == null ==> fError occured
			}
		}
	}

	/**
	 * Assign the associated EvalEngine to the current thread. Every subsequent
	 * action evaluation in this thread affects the EvalEngine in this class.
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
