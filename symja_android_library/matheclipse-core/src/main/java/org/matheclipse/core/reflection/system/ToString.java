package org.matheclipse.core.reflection.system;

import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ToString extends AbstractFunctionEvaluator {

	public ToString() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return F.stringx(outputForm(ast.arg1()));
	}

	public static String outputForm(final IExpr expression) {
		try {
			StringBuilder buf = new StringBuilder();
			OutputFormFactory off = OutputFormFactory.get();
			off.setIgnoreNewLine(true);
			off.convert(buf, expression);
			return buf.toString();
		} catch (IOException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
