package org.matheclipse.core.fuzz;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

import junit.framework.TestCase;

public class ExprEvaluatorTests extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.await();
	}

	public void testFuzz001() {
		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);

		String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
		for (int i = 0; i < functionStrs.length; i++) {
			IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
			IEvaluator evaluator = sym.getEvaluator();
			if (evaluator instanceof IFunctionEvaluator) {
				int[] argSize = ((IFunctionEvaluator) evaluator).expectedArgSize();
				if (argSize != null) {
					int end = argSize[1];
					if (end <= 10) {
						int start = argSize[0];
						generateASTs(sym, start, end,  F.C0, engine);
						generateASTs(sym, start, end,  F.Null, engine);
						generateASTs(sym, start, end,  F.List(), engine);
						continue;
					}

				}
			}
			generateASTs(sym, 1, 5, F.C0, engine);
			generateASTs(sym, 1, 5, F.Null, engine);
			generateASTs(sym, 1, 5, F.List(), engine);
		}
	}

	private void generateASTs(IBuiltInSymbol sym, int start, int end, IExpr arg, EvalEngine engine) {
		ExprEvaluator eval;
		for (int j = start; j <= end; j++) {
			eval = new ExprEvaluator(engine, true, 20);
			IAST ast = generate(sym, j,arg);
			try {
				 System.out.println(ast.toString());
				eval.eval(ast);
			} catch (MathException mex) {
				System.out.println(ast.toString());
				mex.printStackTrace();
				System.out.println();
			} catch (RuntimeException rex) {
				System.out.println(ast.toString());
				rex.printStackTrace();
				fail();
			}
		}
	}

	private IAST generate(ISymbol sym, int numberOfArgs, IExpr arg) {
		IASTAppendable ast = F.ast(sym);
		for (int j = 0; j < numberOfArgs; j++) {
			ast.append(arg);
		}
		return ast;
	}
}
