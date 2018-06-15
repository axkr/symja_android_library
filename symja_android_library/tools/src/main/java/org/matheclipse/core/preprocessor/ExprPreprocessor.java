package org.matheclipse.core.preprocessor;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.tools.AbstractCodeGenerator;

/**
 * Convert Symja expressions line comments opened by <code>// [$ ... $]</code> and closed by <code>// $$ </code> into
 * Java source code.
 *
 * Example from <code>TrigToExp</code>
 * 
 * <pre>
 * MATCHER.caseOf(Csch(x_), //
 * 		x -> // [$ 2/(E^x-E^(-x)) $]
 * 		F.Times(F.C2, F.Power(F.Plus(F.Negate(F.Power(F.E, F.Negate(x))), F.Power(F.E, x)), -1))); // $$);
 * </pre>
 */
public class ExprPreprocessor extends AbstractCodeGenerator {

	public ExprPreprocessor() {
		super();
	}

	public static void main(String[] args) {
		AbstractCodeGenerator epp = new ExprPreprocessor();
		Config.EXPLICIT_TIMES_OPERATOR = true;
		F.initSymbols();

		System.out.println("Input qualified Java file for converting Symja expressions to Java source");
		System.out.println("In Eclipse do a right mouse click on a Java file and select menu \"Copy qualified name\"");
		runConsole(epp);

	}

	public boolean apply(String command, StringBuilder buf) {
		ExprParser p = new ExprParser(EvalEngine.get(), true);
		IExpr expr = p.parse(command);
		buf.append(expr.internalJavaString(false, 1, false, true, true));
		return true;
	}
}
