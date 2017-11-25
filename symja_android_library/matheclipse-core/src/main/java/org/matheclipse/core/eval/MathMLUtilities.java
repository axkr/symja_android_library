package org.matheclipse.core.eval;

import java.io.Writer;
import java.text.NumberFormat;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.mathml.MathMLFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Convert an expression into presentation MathML output
 * 
 * See <a href="http://www.w3.org/TR/2000/CR-MathML2-20001113/byalpha.html"> Chracters ordered by Unicode</a>
 * 
 */
public class MathMLUtilities {
	protected final EvalEngine fEvalEngine;

	protected MathMLFormFactory fMathMLFactory;

	/**
	 * MS Internet Explorer client ?
	 */
	boolean fMSIE;

	/**
	 * Print MathML header in output
	 */
	boolean fMathMLHeader;

	static {
		// initialize the global available symbols
		F.initSymbols(null, null, true);
	}

	/**
	 * Constructor for an object which converts an expression into presentation MathML output
	 * 
	 * @param evalEngine
	 * @param mathMTagPrefix
	 *            if set to <code>true</code> use &quot;m:&quot; as tag prefix for the MathML output.
	 * @param mathMLHeader
	 *            print MathML header in output
	 * @param numberFormat
	 *            the number formatter (could be <code>null</code>)
	 */
	public MathMLUtilities(final EvalEngine evalEngine, final boolean mathMTagPrefix, final boolean mathMLHeader) {
		this(evalEngine, mathMTagPrefix, mathMLHeader, null);
	}

	/**
	 * Constructor for an object which converts an expression into presentation MathML output
	 * 
	 * @param evalEngine
	 * @param mathMTagPrefix
	 *            if set to <code>true</code> use &quot;m:&quot; as tag prefix for the MathML output.
	 * @param mathMLHeader
	 *            print MathML header in output
	 * @param numberFormat
	 *            the number formatter (could be <code>null</code>)
	 */
	public MathMLUtilities(final EvalEngine evalEngine, final boolean mathMTagPrefix, final boolean mathMLHeader,
			NumberFormat numberFormat) {
		fEvalEngine = evalEngine;
		EvalEngine.set(fEvalEngine);
		// set the thread local instance
		startRequest();
		if (mathMTagPrefix) {
			fMathMLFactory = new MathMLFormFactory("m:", numberFormat);
		} else {
			fMathMLFactory = new MathMLFormFactory("", numberFormat);
		}
		// fParser = new Parser(relaxedSyntax);
		fMSIE = mathMTagPrefix;
		fMathMLHeader = mathMLHeader;
	}

	/**
	 * Get the current evaluation engine.
	 * 
	 * @return
	 */
	public EvalEngine getEvalEngine() {
		return fEvalEngine;
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
		// ASTNode node;
		if (inputExpression != null) {
			try {
				ExprParser parser = new ExprParser(fEvalEngine);
				parsedExpression = parser.parse(inputExpression);
				// node = fEvalEngine.parseNode(inputExpression);
				// parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
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
		toMathML(objectExpression, out, false);
	}

	synchronized public void toMathML(final IExpr objectExpression, final Writer out, boolean useXmlns) {
		final StringBuilder buf = new StringBuilder();

		if (objectExpression != null) {
			fMathMLFactory.convert(buf, objectExpression, Integer.MIN_VALUE, false);
			try {
				if (fMSIE) {
					out.write("<m:math>");
					out.write(buf.toString());
					out.write("</m:math>");
				} else {
					if (fMathMLHeader) {
						out.write("<?xml version=\"1.0\"?>\n"
								+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
								+ "<math mode=\"display\">\n");
					} else {
						if (useXmlns) {
							out.write("<math xmlns=\"http://www.w3.org/1999/xhtml\">");
						} else {
							out.write("<math>");
						}
					}

					out.write(buf.toString());
					// if (useMstyle) {
					// out.write("</mstyle>");
					// }
					out.write("</math>");
				}
			} catch (final Throwable e) {
				e.printStackTrace();
				// parsedExpression == null ==> fError occured
			}
		}
	}

	synchronized public void toJava(final String inputExpression, final Writer out, boolean strictJava) {
		IExpr parsedExpression = null;
		// ASTNode node;
		if (inputExpression != null) {
			try {
				ExprParser parser = new ExprParser(fEvalEngine);
				parsedExpression = parser.parse(inputExpression);
				// node = fEvalEngine.parseNode(inputExpression);
				// parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
				out.write(parsedExpression.internalFormString(strictJava, 0));
			} catch (final Throwable e) {
				return;
				// parsedExpression == null ==> fError occured
			}
		}
	}

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
