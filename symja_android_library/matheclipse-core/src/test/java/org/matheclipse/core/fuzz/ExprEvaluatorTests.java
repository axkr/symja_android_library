package org.matheclipse.core.fuzz;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
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
					if (argSize[1] <= 10) {
//						System.out.println(sym.toString() + "[" + argSize[0] + "," + argSize[1] + "]");
						IASTAppendable ast = F.ast(sym);
						for (int j = 0; j < argSize[0]; j++) {
							ast.append(F.Null);
						}
						eval = new ExprEvaluator(engine, true, 20); 
						try {
							System.out.println(ast.toString());
							eval.eval(ast);
						} catch (MathException mex) {
							mex.printStackTrace();
						} catch (RuntimeException rex) {
							System.out.println(ast.toString());
							rex.printStackTrace();
							fail();
						}
						for (int j = argSize[0]; j < argSize[1]; j++) {
							eval = new ExprEvaluator(engine, true, 20);
							ast.append(F.Null);
							try {
								System.out.println(ast.toString());
								eval.eval(ast);
							} catch (MathException mex) {
								mex.printStackTrace();
							} catch (RuntimeException rex) {
								System.out.println(ast.toString());
								rex.printStackTrace();
								fail();
							}
						}
					}

				}
			}

		}
		// String str = "sin(x)";
		// IExpr e = eval.eval(str);
		// int i = 100;
		// eval.defineVariable("x", (double) i);
		// double result = e.evalDouble();
		// assertEquals(-0.5063656411097588, result, 0E-10);
	}
}
