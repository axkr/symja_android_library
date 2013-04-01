package org.matheclipse.core.system;

import java.io.IOException;

import junit.framework.TestCase;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;

public class ThreadTestCase extends TestCase {

	public ThreadTestCase(String name) {
		super(name);
		F.initSymbols(null, null, false);
		Config.SERVER_MODE = true;
	}

	public void testThreads() {
		EvalUtilities evalUtilities = new EvalUtilities();
		try {

			evalUtilities.evaluate("$x=4");

			String num = evalUtilities.evaluate("$x").toString();
			// num should be 4
			assertEquals("4", num);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	public void testExpand() throws Exception {
		String input = "";
		String output = "";
		F.initSymbols(null, null, false);
		EvalUtilities evalUtilities = new EvalUtilities();

		input = "Expand[(A*X^2+B*X)^2]";
		output = convertToString(evalUtilities.evaluate(input));
		assertEquals("A^2*X^4+2*A*B*X^3+B^2*X^2", output);

		Config.SERVER_MODE = true;

		// now use $ for variables

		input = "Expand[($A*$X^2+$B*$X)^2]";
		output = convertToString(evalUtilities.evaluate(input));
		// output is: $A^2*$X^4+2*$A*$B*$X^3+$B^2*$X^2
		// So the following assert fails,
		// but it is the same as above, but with $ as a prefix for all variables
		assertEquals("$A^2*$X^4+2*$A*$B*$X^3+$B^2*$X^2", output);
	}

	public String convertToString(IExpr result) throws IOException {
		StringBufferWriter buffer = new StringBufferWriter();
		OutputFormFactory.get().convert(buffer, result);
		return buffer.toString();
	}
}
