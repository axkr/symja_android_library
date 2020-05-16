
package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import org.hipparchus.complex.Complex;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.FEConfig;

public final class RandomFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.RandomInteger.setEvaluator(new RandomInteger());
			F.RandomPrime.setEvaluator(new RandomPrime());
			F.RandomChoice.setEvaluator(new RandomChoice());
			F.RandomComplex.setEvaluator(new RandomComplex());
			F.RandomReal.setEvaluator(new RandomReal());
			F.RandomSample.setEvaluator(new RandomSample());
		}
	}

	/**
	 * <pre>
	 * RandomChoice({arg1, arg2, arg3,...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * chooses a random <code>arg</code> from the list.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RandomChoice({1,2,3,4,5,6,7})
	 * 5
	 * </pre>
	 */
	private final static class RandomChoice extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1 && ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				ThreadLocalRandom random = ThreadLocalRandom.current();
				int listSize = list.argSize();
				if (listSize == 0) {
					return F.NIL;
				}
				int randomIndex = random.nextInt(listSize);
				if (ast.size() == 2) {
					return list.get(randomIndex + 1);
				}
				if (ast.size() == 3) {
					int n = ast.arg2().toIntDefault(Integer.MIN_VALUE);
					if (n > 0) {
						IASTAppendable result = F.ListAlloc(n);
						for (int i = 0; i < n; i++) {
							result.append(list.get(randomIndex + 1));
							randomIndex = random.nextInt(listSize);
						}
						return result;
					}
				}
			}

			return F.NIL;
		}

	}

	private final static class RandomComplex extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST0()) {
					// RandomReal() gives a double value between 0.0 and 1.0
					ThreadLocalRandom tlr = ThreadLocalRandom.current();
					double re = tlr.nextDouble();
					double im = tlr.nextDouble();
					return F.complexNum(re, im);
				} else if (ast.isAST1()) {
					if (ast.arg1().isAST(F.List, 3)) {
						Complex min = engine.evalComplex(ast.arg1().first());
						Complex max = engine.evalComplex(ast.arg1().second());
						double minRe = min.getReal();
						double minIm = min.getImaginary();
						double maxRe = max.getReal();
						double maxIm = max.getImaginary();
						if (minRe >= maxRe) {
							double temp = minRe;
							minRe = maxRe;
							maxRe = temp;
							if (minRe == maxRe) {
								// return F.num(min);
							}
						}
						if (minIm >= maxIm) {
							double temp = minIm;
							minIm = maxIm;
							maxIm = temp;
							if (minIm == maxIm && minRe == maxRe) {
								F.complexNum(minRe, minIm);
							}
						}
						ThreadLocalRandom tlr = ThreadLocalRandom.current();
						return F.complexNum(tlr.nextDouble(minRe, maxRe), tlr.nextDouble(minIm, maxIm));
					} else {
						Complex max = engine.evalComplex(ast.arg1() ); 
						ThreadLocalRandom tlr = ThreadLocalRandom.current();
						return F.complexNum(tlr.nextDouble(max.getReal()), tlr.nextDouble(max.getImaginary()));
					}
				} else if (ast.isAST2()) {
					if (ast.arg2().isList()) {
						IAST list = (IAST) ast.arg2();
						IExpr[] arr = new IExpr[list.size()];
						arr[0] = F.RandomComplex(ast.arg1());
						for (int i = 1; i < list.size(); i++) {
							arr[i] = F.List(list.get(i));
						}
						return F.ast(arr, F.Table);
					}
				}
			} catch (RuntimeException rex) {
				//
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * RandomInteger(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a random integer number between <code>0</code> and <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RandomInteger(100)
	 * 88
	 * </pre>
	 */
	private final static class RandomInteger extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				ThreadLocalRandom tlr = ThreadLocalRandom.current();
				return randomBigInteger(BigInteger.ONE, false, tlr);
			}
			if (ast.arg1().isAST(F.List, 3)) {
				int min = ast.arg1().first().toIntDefault();
				int max = ast.arg1().second().toIntDefault();
				if (min != Integer.MIN_VALUE && max != Integer.MIN_VALUE) {
					if (min >= max) {
						int temp = min;
						min = max;
						max = temp;
						if (min == max) {
							return F.ZZ(min);
						}
					}
					ThreadLocalRandom tlr = ThreadLocalRandom.current();
					if (ast.isAST2()) {
						int size = ast.arg2().toIntDefault(Integer.MIN_VALUE);
						if (size >= 0) {
							IASTAppendable list = F.ListAlloc(size);
							for (int i = 0; i < size; i++) {
								list.append(F.ZZ(tlr.nextInt((max - min) + 1) + min));
							}
							return list;
						}
					}
					return F.ZZ(tlr.nextInt((max - min) + 1) + min);
				}
				return F.NIL;
			}
			if (ast.arg1().isInteger()) {
				// RandomInteger(100) gives an integer between 0 and 100
				ThreadLocalRandom tlr = ThreadLocalRandom.current();
				BigInteger upperLimit = ((IInteger) ast.arg1()).toBigNumerator();
				boolean negative = false;
				if (upperLimit.compareTo(BigInteger.ZERO) < 0) {
					upperLimit = upperLimit.negate();
					negative = true;
				}
				if (ast.isAST2()) {
					int size = ast.arg2().toIntDefault(Integer.MIN_VALUE);
					if (size >= 0) {
						IASTAppendable list = F.ListAlloc(size);
						for (int i = 0; i < size; i++) {
							list.append(randomBigInteger(upperLimit, negative, tlr));
						}
						return list;
					}

				} else {
					return randomBigInteger(upperLimit, negative, tlr);
				}
			}

			return F.NIL;
		}

		private IExpr randomBigInteger(BigInteger upperLimit, boolean negative, ThreadLocalRandom tlr) {
			BigInteger r;
			final int nlen = upperLimit.bitLength();
			do {
				r = new BigInteger(nlen, tlr);
			} while (r.compareTo(upperLimit) > 0);
			return F.ZZ(negative ? r.negate() : r);
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_2;
		}
	}

	private final static class RandomPrime extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isInteger() && ((IInteger) ast.arg1()).isGE(F.C2)) {
				try {
					// RandomPrime(100) gives a prime integer between 2 and 100
					BigInteger upperLimit = ((IInteger) ast.arg1()).toBigNumerator();
					if (upperLimit.compareTo(BigInteger.ZERO) < 0) {
						return engine.printMessage("RandomPrime: Positive integer value expected.");
					}
					final int nlen = upperLimit.bitLength();
					ThreadLocalRandom tlr = ThreadLocalRandom.current();
					BigInteger randomNumber;
					do {
						randomNumber = new BigInteger(nlen, 32, tlr);
					} while (randomNumber.compareTo(upperLimit) > 0);
					return F.ZZ(randomNumber);
				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
					return engine.printMessage("RandomPrime: There are no primes in the specified interval.");
				}
			}

			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * RandomReal()
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a random number between <code>0.0</code> and <code>1.0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RandomReal( )
	 * 0.53275
	 * </pre>
	 */
	private final static class RandomReal extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST0()) {
					// RandomReal() gives a double value between 0.0 and 1.0
					ThreadLocalRandom tlr = ThreadLocalRandom.current();
					double r = tlr.nextDouble();
					return F.num(r);
				} else if (ast.isAST1()) {
					if (ast.arg1().isAST(F.List, 3)) {
						double min = engine.evalDouble(ast.arg1().first());
						double max = engine.evalDouble(ast.arg1().second());
						if (min >= max) {
							double temp = min;
							min = max;
							max = temp;
							if (min == max) {
								return F.num(min);
							}
						}

						ThreadLocalRandom tlr = ThreadLocalRandom.current();
						return F.num(tlr.nextDouble(min, max));
					} else {
						double max = engine.evalDouble(ast.arg1());
						ThreadLocalRandom tlr = ThreadLocalRandom.current();
						return F.num(tlr.nextDouble(max));
					}
				} else if (ast.isAST2()) {
					if (ast.arg2().isList()) {
						IAST list = (IAST) ast.arg2();
						IExpr[] arr = new IExpr[list.size()];
						arr[0] = F.RandomReal(ast.arg1());
						for (int i = 1; i < list.size(); i++) {
							arr[i] = F.List(list.get(i));
						}
						return F.ast(arr, F.Table);
						// F.Table(F.RandomReal(ast.arg1()), ast.arg2());
					}
				}
			} catch (RuntimeException rex) {
				//
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * RandomSample(&lt;function&gt;)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a random sample for the arguments of the <code>function</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * RandomSample(&lt;function&gt;, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a random sample of <code>n</code> elements for the arguments of the <code>function</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RandomSample(f(1,2,3,4,5))
	 * f(3,4,5,1,2)
	 * 
	 * &gt;&gt; RandomSample(f(1,2,3,4,5),3)
	 * f(3,4,1)
	 * </pre>
	 */
	private final static class RandomSample extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isAST()) {
				int n = ast.isAST2() ? ast.arg2().toIntDefault(Integer.MIN_VALUE) : Integer.MAX_VALUE;
				if (n >= 0) {
					return shuffle((IAST) ast.arg1(), n);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		public static IAST shuffle(IAST list, int n) {
			final int len = list.argSize();

			// Shuffle indices.
			final int[] indexList = MathArrays.natural(len);
			MathArrays.shuffle(indexList);

			if (n < len) {
				IASTAppendable result = list.copyHead();
				for (int j = 0; j < n; j++) {
					result.append(list.get(indexList[j] + 1));
				}
				return result;
			}
			// Create shuffled list.
			return list.copy().setArgs(1, len + 1, i -> list.get(indexList[i - 1] + 1));
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private RandomFunctions() {

	}

}
