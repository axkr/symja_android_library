package org.matheclipse.core.fuzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

import junit.framework.TestCase;

public class ExprEvaluatorTests extends TestCase {

	private static List<ASTNode> parseFileToList() {
		try {
			File file = new File("./data/harvest.sym");
			final BufferedReader f = new BufferedReader(new FileReader(file));
			final StringBuffer buff = new StringBuffer(1024);
			String line;
			while ((line = f.readLine()) != null) {
				buff.append(line);
				buff.append('\n');
				// Insert newlines to let the parser see that a new rule starts
				buff.append('\n');
				buff.append('\n');
			}
			f.close();
			String inputString = buff.toString();
			Parser p = new Parser(ASTNodeFactory.RELAXED_STYLE_FACTORY, true, true);
			return p.parsePackage(inputString);
			// return p.parsePackage(inputString);

			// assertEquals(obj.toString(),
			// "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", e.getMessage());
		}
		return null;
	}

	public void testFuzzUnits() {
		boolean quietMode = true;
		EvalEngine engine = EvalEngine.get();
		List<ASTNode> node = parseFileToList();
		IExpr temp;

		OutputFormFactory fInputFactory = OutputFormFactory.get(true, false, 5, 7);
		fInputFactory.setQuotes(true);
		AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
		IAST seedList = F.List(//
				F.num(-0.5), //
				F.num(0.5), //
				F.num(Math.PI * (-0.5)), //
				F.num(Math.PI * 0.5), //
				F.num(-Math.PI), //
				F.num(Math.PI), //

				F.num(-Math.E), //
				F.num(Math.E), //
				F.C0, //
				F.C1, //
				F.CN1, //
				F.CN1D2, //
				F.C1D2, //
				F.CNI, //
				F.CI, //
				// F.ZZ(Integer.MIN_VALUE), //
				F.CInfinity, //
				F.CNInfinity, //
				F.Null, //
				F.Power(F.x, F.C2), //
				// F.Indeterminate, //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.C1DSqrt5, //
				F.Slot2, //
				F.Subtract(F.C1, F.C1));
		int counter = 0;
		for (int j = 1; j < 1000; j++) {
			int i = 0;
			while (i < node.size()) {
				temp = ast2Expr.convert(node.get(i++));
				if (temp.isAST() && temp.size() > 1) {
					ThreadLocalRandom random = ThreadLocalRandom.current();
					int seedIndex = random.nextInt(1, seedList.size());
					IExpr seed = seedList.get(seedIndex);

					IASTMutable mutant = ((IAST) temp).copy();
					int randomIndex = random.nextInt(1, mutant.size());
					mutant.set(randomIndex, seed);

					for (int k = 0; k < 1; k++) {
						seedIndex = random.nextInt(1, seedList.size());
						seed = seedList.get(seedIndex);
						randomIndex = random.nextInt(1, mutant.size());
						mutant.set(randomIndex, seed);
					}

					engine.init();
					engine.setQuietMode(quietMode);
					engine.setRecursionLimit(256);
					engine.setIterationLimit(1000);
					ExprEvaluator eval = new ExprEvaluator(engine, true, 20);
					final String mutantStr = fInputFactory.toString(mutant);
					try {
						// System.out.println(">> " + mutantStr);
						// System.out.print(".");
						if (counter++ > 80) {
							// System.out.println("");
							counter = 0;
							System.out.flush();
						}
						eval.eval(mutantStr);
					} catch (FlowControlException mex) {
						if (!quietMode) {
							System.out.println(mutantStr);
							mex.printStackTrace();
							System.out.println();
						}
					} catch (IterationLimitExceeded ile) {
						System.out.println(mutantStr);
						ile.printStackTrace();
						System.out.println();
					} catch (final RecursionLimitExceeded rle) {
						System.out.println(mutantStr);
						rle.printStackTrace();
						System.out.println();
					} catch (SyntaxError se) {
						if (!quietMode) {
							System.out.println(mutantStr);
							se.printStackTrace();
							System.out.println();
						}
						// fail();
					} catch (final ASTElementLimitExceeded aele) {
						System.out.println(mutantStr);
						aele.printStackTrace();
						System.out.println();
					} catch (MathException mex) {
						System.out.println(mutantStr);
						mex.printStackTrace();
						System.out.println();
						fail();
					} catch (RuntimeException rex) {
						System.out.println(mutantStr);
						rex.printStackTrace();
						fail();
					} catch (Error rex) {
						System.out.println(mutantStr);
						if (rex instanceof StackOverflowError) {
							System.err.println("java.lang.StackOverflowError");
						} else {
							System.out.println(mutantStr);
							rex.printStackTrace();
							fail();
						}
					}
				}
			}
		}
		// return result;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.await();
	}

	public void testFuzz001() {
		Config.MAX_AST_SIZE = 100;
		Config.MAX_BIT_COUNT = 1000;
		Config.MAX_OUTPUT_SIZE = 10000;

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
						generateASTs(sym, start, end, F.Infinity, engine);
						generateASTs(sym, start, end, F.Indeterminate, engine);
						generateASTs(sym, start, end, F.CN1, engine);
						generateASTs(sym, start, end, F.C0, engine);
						generateASTs(sym, start, end, F.Null, engine);
						generateASTs(sym, start, end, F.List(), engine);
						continue;
					}

				}
			}
			generateASTs(sym, 1, 5, F.Infinity, engine);
			generateASTs(sym, 1, 5, F.Indeterminate, engine);
			generateASTs(sym, 1, 5, F.CN1, engine);
			generateASTs(sym, 1, 5, F.C0, engine);
			generateASTs(sym, 1, 5, F.Null, engine);
			generateASTs(sym, 1, 5, F.List(), engine);
		}
	}

	private void generateASTs(IBuiltInSymbol sym, int start, int end, IExpr arg, EvalEngine engine) {
		ExprEvaluator eval;
		for (int j = start; j <= end; j++) {
			eval = new ExprEvaluator(engine, true, 20);
			IAST ast = generate(sym, j, arg);
			try {
				// System.out.println(ast.toString());
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
