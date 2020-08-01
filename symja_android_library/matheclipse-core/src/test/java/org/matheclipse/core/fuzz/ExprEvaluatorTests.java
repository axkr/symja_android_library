package org.matheclipse.core.fuzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

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
import org.matheclipse.core.tensor.qty.IQuantity;
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
		Config.MAX_AST_SIZE = 10000;
		Config.MAX_OUTPUT_SIZE = 10000;
		Config.MAX_INPUT_LEAVES = 100L;
		Config.MAX_MATRIX_DIMENSION_SIZE = 100;
		Config.MAX_PRECISION_APFLOAT = 100;
		Config.MAX_BIT_LENGTH = 200000;
		Config.MAX_POLYNOMIAL_DEGREE = 100;
		Config.FILESYSTEM_ENABLED = false;
		boolean quietMode = true;

		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);

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
				F.CC(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE), //
				F.QQ(Long.MAX_VALUE, Long.MIN_VALUE), F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), //
				F.Slot2, //
				// some primes
				F.C2, F.C3, F.C5, F.C7, F.ZZ(11), F.ZZ(13), F.ZZ(17), F.ZZ(19), F.ZZ(101), F.ZZ(1009), F.ZZ(10007), //
				F.ZZ(Integer.MIN_VALUE), //
				F.ZZ(Integer.MAX_VALUE), //
				F.CInfinity, //
				F.CNInfinity, //
				F.Null, //
				F.Power(F.x, F.C2), //
				F.Indeterminate, //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1))), F.assoc(F.List()),
				F.assoc(F.List(F.Rule(F.stringx("s1"), F.C0), F.RuleDelayed(F.stringx("s2"), F.C1))),
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
				F.List(F.CN5), //  simulate level spec
				F.List(F.C7), //  simulate level spec
				F.C1DSqrt5, //
				F.Divide(F.Plus(F.C1, F.Sqrt(5)), F.C2), // GoldenRatio
				F.Divide(F.C2, F.Plus(F.C1, F.Sqrt(5))), // 1/GoldenRatio
				F.Negate(F.Sqrt(2)), //
				F.Divide(F.Sqrt(2), F.C2), //
				F.Negate(F.Divide(F.Sqrt(2), F.C2)), //
				F.Plus(F.Sqrt(2), F.C1), //
				F.Plus(F.Sqrt(2), F.CN1), //
				F.Exp(F.Times(F.Pi, F.CI, F.C1D3)), //
				F.Plus(F.C1, F.CI), //
				F.Plus(F.CN1, F.CI), //
				F.CSqrt2, //
				F.C2Pi, //
				F.CN3D2, //
				F.C3D2, //
				F.C3D4, //
				F.QQ(Long.MAX_VALUE, 7L), F.QQ(Long.MIN_VALUE, 11L), F.QQ(7, Long.MAX_VALUE), F.QQ(11, Long.MAX_VALUE),
				F.QQ(Long.MAX_VALUE, Long.MAX_VALUE), F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), F.Slot2, //
				IQuantity.of(1.2, "m"), //
				F.stringx(""), //
				F.stringx("\\"), //
				F.stringx("\r"), //
				F.stringx("\t"), //
				F.stringx("\n"), //
				F.stringx("\r\n"), //
				F.stringx("\n   "), //
				F.stringx("\uffff"), //
				F.Subtract(F.C1, F.C1));
		ThreadLocalRandom random = ThreadLocalRandom.current();
		SlowComputationThread thread = null;
		for (int j = 1; j < 10000; j++) {
			int i = 0;
			while (i < node.size()) {
				temp = ast2Expr.convert(node.get(i++));
				if (temp.isAST() && temp.size() > 1) {
					int seedIndex = random.nextInt(1, seedList.size());
					IExpr seed = seedList.get(seedIndex);
					String mutantStr = "initial";
					IASTMutable mutant = ((IAST) temp).copy();
					try {
						ISymbol sym = mutant.topHead();
						if (sym == F.PolynomialGCD || //
								sym == F.On || //
								sym == F.Off || //
								sym == F.Compile || //
								sym == F.CompiledFunction || //
								sym == F.Set || //
								sym == F.SetDelayed || //
								sym == F.UpSet || //
								sym == F.UpSetDelayed) {
							continue;
						}
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
						// mutantStr = fInputFactory.toString(mutant);

						// System.out.println(">> " + mutantStr);
						// if (counter++ > 80) {
						// System.out.println("");
						// counter = 0;
						// System.out.flush();
						// System.err.flush();
						// }

						thread = new SlowComputationThread(">> " + mutant.toString());
						thread.start();
						engine.evaluate(mutant);

					} catch (FlowControlException mex) {
						if (!quietMode) {
							System.err.println(mutant.toString());
							mex.printStackTrace();
							System.err.println();
						}
					} catch (LimitException ile) {
						if (!quietMode) {
							System.err.println(mutant.toString());
							ile.printStackTrace();
							System.err.println();
						}
					} catch (SyntaxError se) {

						System.err.println(mutant.toString());
						se.printStackTrace();
						System.err.println();

						// fail();
					} catch (ValidateException ve) {
						System.err.println(mutant.toString());
						ve.printStackTrace();
						System.err.println();
						// fail();
					} catch (MathException mex) {
						System.err.println(mutant.toString());
						mex.printStackTrace();
						System.err.println();
						fail();
					} catch (RuntimeException rex) {
						System.err.println(mutant.toString());
						rex.printStackTrace();
						fail();
					} catch (Error rex) {
						System.err.println(mutant.toString());
						if (rex instanceof StackOverflowError) {
							System.err.println("java.lang.StackOverflowError");
							rex.printStackTrace();
							fail();
						} else {
							System.err.println(mutantStr);
							rex.printStackTrace();
							fail();
						}
					} finally {
						thread.terminate();
						thread.interrupt();
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
		// define after initialization
		Config.FUZZ_TESTING = true;
	}

	public void testBuiltinFunctionFuzz() {
		Config.MAX_AST_SIZE = 10000;
		Config.MAX_OUTPUT_SIZE = 10000;
		Config.MAX_INPUT_LEAVES = 100L;
		Config.MAX_MATRIX_DIMENSION_SIZE = 100;
		Config.MAX_PRECISION_APFLOAT = 100;  
		Config.MAX_BIT_LENGTH = 200000;
		Config.MAX_POLYNOMIAL_DEGREE = 100;
		Config.FILESYSTEM_ENABLED = false;
		
		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);
		byte[] bArray = new byte[0];
		ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
		byte[] b0Array = new byte[] { 0 };
		ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
		F.x.setAttributes(ISymbol.PROTECTED);
		F.y.setAttributes(ISymbol.PROTECTED);
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
				F.CC(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE), //
				F.QQ(Long.MAX_VALUE, Long.MIN_VALUE), F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), //
				F.Slot2, //
				// some primes
				F.C2, F.C3, F.C5, F.C7, F.ZZ(11), F.ZZ(13), F.ZZ(17), F.ZZ(19), F.ZZ(101), F.ZZ(1009), F.ZZ(10007), //
				F.ZZ(Integer.MIN_VALUE), //
				F.ZZ(Integer.MAX_VALUE), //
				F.CInfinity, //
				F.CNInfinity, //
				F.Null, //
				F.Power(F.x, F.C2), //
				F.Indeterminate, //
				F.ComplexInfinity, //
				F.x_, //
				F.y_, //
				F.CEmptyList, //
				F.assoc(F.List(F.Rule(F.a, F.C0), F.RuleDelayed(F.b, F.C1))), F.assoc(F.List()),
				F.assoc(F.List(F.Rule(F.stringx("s1"), F.C0), F.RuleDelayed(F.stringx("s2"), F.C1))),
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
				F.List(F.CN5), //  simulate level spec
				F.List(F.C7), //  simulate level spec
				F.C1DSqrt5, //
				F.Divide(F.Plus(F.C1, F.Sqrt(5)), F.C2), // GoldenRatio
				F.Divide(F.C2, F.Plus(F.C1, F.Sqrt(5))), // 1/GoldenRatio
				F.Negate(F.Sqrt(2)), //
				F.Divide(F.Sqrt(2), F.C2), //
				F.Negate(F.Divide(F.Sqrt(2), F.C2)), //
				F.Plus(F.Sqrt(2), F.C1), //
				F.Plus(F.Sqrt(2), F.CN1), //
				F.Exp(F.Times(F.Pi, F.CI, F.C1D3)), //
				F.Plus(F.C1, F.CI), //
				F.Plus(F.CN1, F.CI), //
				F.CSqrt2, //
				F.C2Pi, //
				F.CN3D2, //
				F.C3D2, //
				F.C3D4, //
				F.QQ(Long.MAX_VALUE, 7L), F.QQ(Long.MIN_VALUE, 11L), F.QQ(7, Long.MAX_VALUE), F.QQ(11, Long.MAX_VALUE),
				F.QQ(Long.MAX_VALUE, Long.MAX_VALUE), F.QQ(Long.MIN_VALUE, Long.MAX_VALUE), F.Slot2, //
				IQuantity.of(1.2, "m"), //
				F.stringx(""), //
				F.stringx("\\"), //
				F.stringx("\r"), //
				F.stringx("\t"), //
				F.stringx("\n"), //
				F.stringx("\r\n"), //
				F.stringx("\n   "), //
				F.stringx("\uffff"), //
				F.Subtract(F.C1, F.C1));
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String[] functionStrs = AST2Expr.FUNCTION_STRINGS;
		int[] counter = new int[] { 0 };
		for (int loop = 0; loop < 20000; loop++) {
			for (int i = 0; i < functionStrs.length; i++) {
				IBuiltInSymbol sym = (IBuiltInSymbol) F.symbol(functionStrs[i]);
				if (sym == F.PolynomialGCD || //
						sym == F.On || //
						sym == F.Off || //
						sym == F.Compile || //
						sym == F.CompiledFunction || //
						sym == F.Set || //
						sym == F.SetDelayed || //
						sym == F.UpSet || //
						sym == F.UpSetDelayed) {
					continue;
				}
				IEvaluator evaluator = sym.getEvaluator();
				if (evaluator instanceof IFunctionEvaluator) {
					int[] argSize = ((IFunctionEvaluator) evaluator).expectedArgSize(null);
					if (argSize != null) {
						int end = argSize[1];
						if (end <= 10) {
							int start = argSize[0];
							if (start == 0) {
								start = 1;
							}
							generateASTs(sym, start, end, seedList, random, counter, (IFunctionEvaluator) evaluator,
									engine);
							continue;
						}
					} else {
						generateASTs(sym, 1, 5, seedList, random, counter, (IFunctionEvaluator) evaluator, engine);
					}
				}
			}
		}
	}

	public void testNonBuiltinFunctionFuzz() {
		Config.MAX_AST_SIZE = 10000;
		Config.MAX_BIT_LENGTH = 1000;
		Config.MAX_OUTPUT_SIZE = 10000;
		Config.MAX_INPUT_LEAVES = 100L;
		Config.MAX_MATRIX_DIMENSION_SIZE = 100;
		Config.MAX_PRECISION_APFLOAT = 100;  
		Config.MAX_BIT_LENGTH = 200000;
		Config.MAX_POLYNOMIAL_DEGREE = 100;
		Config.FILESYSTEM_ENABLED = false;

		EvalEngine engine = new EvalEngine(true);
		engine.setRecursionLimit(256);
		engine.setIterationLimit(1000);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);
		byte[] bArray = new byte[0];
		ByteArrayExpr ba = ByteArrayExpr.newInstance(bArray);
		byte[] b0Array = new byte[] { 0 };
		ByteArrayExpr b0a = ByteArrayExpr.newInstance(b0Array);
		F.x.setAttributes(ISymbol.PROTECTED);
		F.y.setAttributes(ISymbol.PROTECTED);
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
				F.List(F.CN5), //  simulate level spec
				F.List(F.C7), //  simulate level spec
				F.C1DSqrt5, //
				F.C2Pi, //
				F.CN3D2, //
				F.C3D2, //
				F.C3D4, //
				F.Slot2, //
				F.stringx(""), //
				F.stringx("\\"), //
				F.stringx("\r"), //
				F.stringx("\t"), //
				F.stringx("\n"), //
				F.stringx("\r\n"), //
				F.stringx("\n   "), //
				F.stringx("\uffff"), //
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
			generateASTs(sym, 1, 5, seedList, random, counter, null, engine);
		}
	}

	private static class SlowComputationThread extends Thread {
		private String str;
		private AtomicBoolean running;

		SlowComputationThread(String str) {
			this.str = str;
			this.running = new AtomicBoolean(true);
		}

		@Override
		public void run() {
			if (running.get()) {
				try {
					for (int i = 0; i < 300; i++) {
						join(100);
						if (!running.get()) {
							break;
						}
					}
				} catch (InterruptedException e) {
					//
					running.set(false);
				}
				if (running.get()) {
					System.err.println("SLOW: " + str);
				}
			}
		}

		public void terminate() {
			running.set(false);
		}
	}

	private void generateASTs(IBuiltInSymbol sym, int start, int end, IAST seedList, ThreadLocalRandom random,
			int[] counter, IFunctionEvaluator evaluator, EvalEngine engine) {
		boolean quietMode = true;
		ExprEvaluator eval;
		System.out.flush();

		for (int j = start; j <= end; j++) {

			eval = new ExprEvaluator(engine, true, 20);
			engine.init();
			engine.setQuietMode(quietMode);
			IASTAppendable ast = F.ast(sym);
			SlowComputationThread thread = null;
			try {
				for (int k = 0; k < j; k++) {
					int seedIndex = random.nextInt(1, seedList.size());
					IExpr seed = seedList.get(seedIndex);
					ast.append(seed);
				}

				if (counter[0]++ > 80) {
					// System.out.println("");
					counter[0] = 0;
					System.out.flush();
					System.err.flush();
				}
				// System.out.println(">> " + ast.toString());
				// System.out.print(".");
				thread = new SlowComputationThread(">> " + ast.toString());
				thread.start();
				if (evaluator != null) {
					evaluator.evaluate(ast, engine);
				} else {
					eval.eval(ast);
				}

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
			} finally {
				thread.terminate();
				thread.interrupt();
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
