package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.PrintStream;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class Definition implements ICoreFunctionEvaluator {

	public Definition() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		ISymbol symbol = Validate.checkSymbolType(ast, 1); 
		
		final EvalEngine engine = EvalEngine.get();
		PrintStream stream;
		stream = engine.getOutPrintStream();
		if (stream == null) {
			stream = System.out;
		}
		try { 
			stream.println(symbol.definitionToString());
		} catch (IOException e) {
			stream.println(e.getMessage());
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}

		return F.Null;
	}

	@Override
	public IExpr numericEval(IAST ast) {
		return evaluate(ast);
	}
	
	@Override
	public void setUp(ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
