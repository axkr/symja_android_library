package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.PrintStream;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class Print implements IFunctionEvaluator {

	public Print() {
	}

	public IExpr evaluate(final IAST ast) {
		PrintStream stream = EvalEngine.get().getOutPrintStream();
		if (stream == null) {
			stream = System.out;
		}
		try {
			final StringBufferWriter buf = new StringBufferWriter();
			for (int i = 1; i < ast.size(); i++) {
				// TODO integrate the print statement in the different
				// environments eclipse, console...
				if (ast.get(i) instanceof IStringX) {
					buf.append(ast.get(i).toString());
				} else {
					OutputFormFactory.get().convert(buf, ast.get(i));
				}
			}
			stream.println(buf.toString());
		} catch (IOException e) {
			stream.println(e.getMessage());
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}

		return F.Null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
