package org.matheclipse.core.test.examples;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class Plot3DExample {
	public static void main(String[] args) {
		try {
			// initialize the engine:
			F.initSymbols(null, null, false);
			Config.SWING_PLOT_FRAME = false;
			Config.SERVER_MODE = true;

			// create an evaluation utility
			EvalUtilities util = new EvalUtilities();

			// evaluate an expression
			IExpr result = util.evaluate("Plot3D[Pi Sin[E x + Sin[y]], {x, -10, 10}, {y, -2, 2}]");
			StringBufferWriter buf = new StringBufferWriter();
			OutputFormFactory.get().convert(buf, result);

			// print the result in the console
			System.out.println(buf.toString());

		} catch (SyntaxError e) {
			// catch parser errors here
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
