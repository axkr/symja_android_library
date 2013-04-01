package org.matheclipse.core.test.examples;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class EvalJThread {
	String name;
	EvalEngine evalEngine;
	EvalUtilities util;
	String input;
	String rules;
	String output;
	IExpr result;
	StringBufferWriter buf;

	EvalJThread(String n, String r) {
		F.initSymbols(null);
		Config.SERVER_MODE = true;
		evalEngine = new EvalEngine();
		util = new EvalUtilities();
		name = n;
		rules = r;
	}

	public void load(String i) {
		input = i;
	}

	public void run() {
		try {
			buf = new StringBufferWriter();
			String in = input + ";" + rules;

			String out1 = null;
			synchronized (evalEngine) {
				evalEngine.reset();
				IExpr parsedExpression = null;
				parsedExpression = evalEngine.parse(in); // Check
				IExpr temp = null;
				if (parsedExpression != null) {
					// evalEngine.reset();
					out1 = evalEngine.evalAST((IAST) parsedExpression).toString();
					temp = evalEngine.evalWithoutNumericReset(parsedExpression);// ??TODO
					// evalEngine.addOut(temp);
				}

				result = temp;
			}

			OutputFormFactory.get().convert(buf, result);
			output = buf.toString();

			if (!out1.equals(result.toString())) {
				System.out.println(name + ":" + out1 + " " + result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new EvalJThreadTest("AAA", "$y=$x+1").start();
		new EvalJThreadTest("BBB", "$y=$x*2").start();
	}

}