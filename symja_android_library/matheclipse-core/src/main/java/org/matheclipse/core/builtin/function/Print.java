package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

public class Print extends AbstractCoreFunctionEvaluator {

	public Print() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		final PrintStream s = engine.getOutPrintStream();
		final PrintStream stream;// = engine.getOutPrintStream();
		if (s == null) {
			stream = System.out;
		} else {
			stream = s;
		}
		final StringBuilder buf = new StringBuilder();
		OutputFormFactory out = OutputFormFactory.get();
		ast.forEach(new Consumer<IExpr>() {
            @Override
            public void accept(IExpr x) {
                IExpr temp = engine.evaluate(x);
                if (temp instanceof IStringX) {
                    buf.append(temp.toString());
                } else {
                    try {
                        out.convert(buf, temp);
                    } catch (IOException e) {
                        stream.println(e.getMessage());
                        if (Config.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
		stream.println(buf.toString());
		return F.Null;
	}

}
