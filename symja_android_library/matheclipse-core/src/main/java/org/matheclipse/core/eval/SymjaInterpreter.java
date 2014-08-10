package org.matheclipse.core.eval;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;

/**
 * Evaluate a Symja expression
 * 
 */
public class SymjaInterpreter extends EvalUtilities {
	String codeString;
	PrintStream outStream;

	/**
	 * Create a new command interpreter attached to the passed in streams.
	 */
	public SymjaInterpreter(String codeString, OutputStream out) {
		super(new EvalEngine(), false, true);
		this.codeString = codeString;

		if (out instanceof PrintStream) {
			this.outStream = (PrintStream) out;
		} else {
			this.outStream = new PrintStream(out);
		}
		fEvalEngine.setOutPrintStream(outStream);
	} 

	public String interpreter(String function) {
		String evalStr = codeString;
		ASTNode node;

		try {
			Parser p = new Parser(true);
			// throws SyntaxError exception, if syntax isn't valid
			if (function != null) {
				evalStr = function + "(" + codeString + ")";
			}
			node = p.parse(evalStr);

		} catch (SyntaxError e1) {
			try {
				Parser p = new Parser();
				// throws SyntaxError exception, if syntax isn't valid
				if (function != null) {
					evalStr = function + "[" + codeString + "]";
				}
				node = p.parse(evalStr);
			} catch (Exception e2) {
				outStream.println(e2.getMessage());
				return "";
			}
		}
		IExpr result;
		final StringBuilder buf = new StringBuilder();
		try {
			IExpr expr = AST2Expr.CONST_LC.convert(node);
			result = evaluate(expr);
			OutputFormFactory.get(true).convert(buf, result);
			return buf.toString();
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				printException(buf, me);
			} else {
				printException(buf, re);
			}
		} catch (final Exception e) {
			printException(buf, e);
		}
		return buf.toString();
	}

	/**
	 * Parse the <code>codeString</code> into an <code>IExpr</code> and if
	 * <code>function</code> unequals <code>null</code>, replace all occurences
	 * of symbol <code>x</code> in the function with the parsed expression.
	 * After that evaluate the given expression.
	 * 
	 * @param function
	 * @return
	 */
	public String interpreter(IAST function) {
		String evalStr = codeString;
		ASTNode node;

		try {
			Parser p = new Parser(true);
			// throws SyntaxError exception, if syntax isn't valid
			node = p.parse(evalStr);

		} catch (SyntaxError e1) {
			try {
				Parser p = new Parser();
				// throws SyntaxError exception, if syntax isn't valid
				node = p.parse(evalStr);
			} catch (Exception e2) {
				outStream.println(e2.getMessage());
				return "";
			}
		}
		IExpr result;
		final StringBuilder buf = new StringBuilder();
		try {
			IExpr expr = AST2Expr.CONST_LC.convert(node);
			if (function != null) {
				expr = function.replaceAll(F.Rule(F.Slot1, expr));
			}
			result = evaluate(expr);
			OutputFormFactory.get(true).convert(buf, result);
			return buf.toString();
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				printException(buf, me);
			} else {
				printException(buf, re);
			}
		} catch (final Exception e) {
			printException(buf, e);
		}
		return buf.toString();
	}

	private void printException(final Appendable buf, final Throwable e) {
		String msg = e.getMessage();
		try {
			if (msg != null) {
				buf.append("\nError: " + msg);
			} else {
				buf.append("\nError: " + e.getClass().getSimpleName());
			}
		} catch (IOException e1) {
		}
	}

	public void eval(String function) {
		String result = interpreter(function);
		outStream.print(result);
	}

	public void eval(IAST function) {
		String result = interpreter(function);
		outStream.print(result);
	}
}
