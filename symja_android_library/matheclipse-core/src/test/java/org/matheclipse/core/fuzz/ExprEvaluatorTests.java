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
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ByteArrayExpr;
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

	public void testSmartFuzz() {
		boolean quietMode = true;
		EvalEngine engine = EvalEngine.get();
		List<ASTNode> node = parseFileToList();
		IExpr temp;

		OutputFormFactory fInputFactory = OutputFormFactory.get(true, false, 5, 7);
		fInputFactory.setQuotes(true);
		AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
		byte[] bArray = new byte[0];
		ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
		byte[] b0Array = new byte[] { 0 };
		ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
		IAST seedList = F.List(//
				ba, //
				b0a, //
				// F.NIL, //
				F.complex(-0.5, 0.5), //
				F.complex(0.0, 0.5), //
				F.complex(0.0, -1.0), //
				F.complex(0.0, 1.0), //
				F.num(-0.5), //
				F.num(0.5), //
				F.num(Math.PI * (-0.5)), //
				F.num(Math.PI * 0.5), //
				F.num(-Math.PI), //
				F.num(Math.PI), //
				F.num(-Math.E), //
				F.num(Math.E), //
				F.True, //
				F.False, //
				F.assoc(F.CEmptyList), //
				F.assoc(F.List(F.Rule(F.x, F.y))), //
				F.CEmptyList, //
				F.List(F.Rule(F.C1, F.C0)), //
				F.List(F.Rule(F.x, F.CN1)), //
				F.C0, //
				F.C1, //
				F.CN1, //
				F.C2, //
				F.CN2, //
				F.CN10, //
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
				F.C1DSqrt5, // F.List(F.List(F.C0)), //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.List(F.List(F.C1)), //
				F.List(F.List(F.CN1)), //
				F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), //
				F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), //
				F.List(F.CN1, F.CN2, F.C3), //
				F.List(F.CN1D2, F.CN2, F.C3), //
				F.List(F.x, F.CN2, F.C3), //
				F.List(F.x, F.C5, F.CN3), //
				F.List(F.x, F.CN3, F.CN1D2), //
				F.Slot1, //
				F.stringx(""), //
				F.stringx("\uffff"), //
				F.Subtract(F.C1, F.C1));
		int counter = 0;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int j = 1; j < 10000; j++) {
			int i = 0;
			while (i < node.size()) {
				temp = ast2Expr.convert(node.get(i++));
				if (temp.isAST() && temp.size() > 1) {
					int seedIndex = random.nextInt(1, seedList.size());
					IExpr seed = seedList.get(seedIndex);
					String mutantStr = "initial";
					try {
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
						mutantStr = fInputFactory.toString(mutant);

						// System.out.println(">> " + mutantStr);
						System.out.print(".");
						if (counter++ > 80) {
							System.out.println("");
							counter = 0;
							System.out.flush();
							System.err.flush();
						}
						eval.eval(mutantStr);
					} catch (FlowControlException mex) {
						if (!quietMode) {
							System.err.println(mutantStr);
							mex.printStackTrace();
							System.err.println();
						}
					} catch (LimitException ile) {
						if (!quietMode) {
							System.err.println(mutantStr);
							ile.printStackTrace();
							System.err.println();
						}
					} catch (ValidateException ve) {
						if (!quietMode) {
							System.err.println(mutantStr);
							ve.printStackTrace();
							System.err.println();
						}
					} catch (SyntaxError se) {
						if (!quietMode) {
							System.err.println(mutantStr);
							se.printStackTrace();
							System.err.println();
						}
						// fail();
					} catch (MathException mex) {
						System.err.println(mutantStr);
						mex.printStackTrace();
						System.err.println();
						fail();
					} catch (RuntimeException rex) {
						System.err.println(mutantStr);
						rex.printStackTrace();
						fail();
					} catch (Error rex) {
						System.err.println(mutantStr);
						if (rex instanceof StackOverflowError) {
							System.err.println("java.lang.StackOverflowError");
							rex.printStackTrace();
						} else {
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
		// Config.FUZZ_TESTING = true;
		Config.UNPROTECT_ALLOWED = false;
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.await();
	}

	public void testBuiltinFunctionFuzz() {
		Config.MAX_AST_SIZE = 10000;
		Config.MAX_BIT_COUNT = 1000;
		Config.MAX_OUTPUT_SIZE = 10000;

		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);
		byte[] bArray = new byte[0];
		ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
		byte[] b0Array = new byte[] { 0 };
		ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
		IAST seedList = F.List(//
				ba, //
				b0a, //
				// F.NIL, //
				F.complex(-0.5, 0.5), //
				F.complex(0.0, 0.5), //
				F.complex(0.0, -1.0), //
				F.complex(0.0, 1.0), //
				F.num(0.5), //
				F.num(-0.5), // 
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
				F.Indeterminate, //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.List(F.List(F.C0)), //
				F.List(F.List(F.C1)), //
				F.List(F.List(F.CN1)), //
				F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1)), //
				F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0)), //
				F.List(F.List(F.C1, F.C0), F.List(F.C0, F.C1), F.C0), //
				F.List(F.List(F.C0, F.C0), F.List(F.C0, F.C0), F.C0), //
				F.List(F.CN1, F.CN2, F.C3), //
				F.List(F.CN1D2, F.CN2, F.C3), //
				F.List(F.x, F.CN2, F.C3), //
				F.List(F.x, F.C5, F.CN3), //
				F.List(F.x, F.CN3, F.CN1D2), //
				F.C1DSqrt5, //
				F.Slot2, //
				F.stringx(""), //
				F.stringx("\uffff"), //
				F.Subtract(F.C1, F.C1));
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
		int[] counter = new int[] { 0 };
		for (int loop = 0; loop < 10000; loop++) {
			for (int i = 0; i < functionStrs.length; i++) {
				IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
				if (sym == F.On || sym == F.Off) {
					continue;
				}
				IEvaluator evaluator = sym.getEvaluator();
				if (evaluator instanceof IFunctionEvaluator) {
					int[] argSize = ((IFunctionEvaluator) evaluator).expectedArgSize();
					if (argSize != null) {
						int end = argSize[1];
						if (end <= 10) {
							int start = argSize[0];
							generateASTs(sym, start, end, seedList, random, counter, engine);
							continue;
						}
					} else {
						generateASTs(sym, 1, 5, seedList, random, counter, engine);
					}
				}
			}
		}
	}

	public void testNonBuiltinFunctionFuzz() {
		Config.MAX_AST_SIZE = 10000;
		Config.MAX_BIT_COUNT = 1000;
		Config.MAX_OUTPUT_SIZE = 10000;

		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);

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
				F.Indeterminate, //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.C1DSqrt5, //
				F.Slot2, //
				F.Subtract(F.C1, F.C1));

		String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int[] counter = new int[] { 0 };
		for (int i = 0; i < functionStrs.length; i++) {
			IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
			IEvaluator evaluator = sym.getEvaluator();
			if (evaluator instanceof IFunctionEvaluator) {
				continue;
			}
			generateASTs(sym, 1, 5, seedList, random, counter, engine);
		}
	}

	private void generateASTs(IBuiltInSymbol sym, int start, int end, IAST seedList, ThreadLocalRandom random,
			int[] counter, EvalEngine engine) {
		boolean quietMode = true;
		ExprEvaluator eval;
		System.out.flush();

		for (int j = start; j <= end; j++) {

			eval = new ExprEvaluator(engine, true, 20);
			engine.init();
			engine.setQuietMode(quietMode);
			IASTAppendable ast = F.ast(sym);

			try {
				for (int k = 0; k < j; k++) {
					int seedIndex = random.nextInt(1, seedList.size());
					IExpr seed = seedList.get(seedIndex);
					ast.append(seed);
				}

				if (counter[0]++ > 80) {
//					System.out.println("");
					counter[0] = 0;
					System.out.flush();
					System.err.flush();
				}
				// System.out.println(">> " + ast.toString());
//				System.out.print(".");
				eval.eval(ast);
			} catch (FlowControlException mex) {
				if (!quietMode) {
					System.err.println(ast.toString());
					mex.printStackTrace();
					System.err.println();
				}
			} catch (LimitException ile) {
				if (!quietMode) {
					System.err.println(ast.toString());
					ile.printStackTrace();
					System.err.println();
				}
			} catch (SyntaxError se) {

				System.err.println(ast.toString());
				se.printStackTrace();
				System.err.println();

				// fail();
			} catch (ValidateException ve) {
				System.err.println(ast.toString());
				ve.printStackTrace();
				System.err.println();
				// fail();
			} catch (MathException mex) {
				System.err.println(ast.toString());
				mex.printStackTrace();
				System.err.println();
				fail();
			} catch (RuntimeException rex) {
				System.err.println(ast.toString());
				rex.printStackTrace();
				fail();
			} catch (Error rex) {
				System.err.println(ast.toString());
				if (rex instanceof StackOverflowError) {
					System.err.println("java.lang.StackOverflowError");
					rex.printStackTrace();
					fail();
				} else {
					System.err.println(ast.toString());
					rex.printStackTrace();
					fail();
				}
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
