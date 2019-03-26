
package org.matheclipse.core.builtin;

import java.math.BigInteger;
//import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public final class RandomFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.RandomInteger.setEvaluator(new RandomInteger());
			F.RandomChoice.setEvaluator(new RandomChoice());
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
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				// RandomInteger(100) gives an integer between 0 and 100
				BigInteger n = ((IInteger) ast.arg1()).toBigNumerator();
				BigInteger r;
				do {
					r = new BigInteger(n.bitLength(), ThreadLocalRandom.current());
				} while (r.compareTo(n) >= 0);
				return F.integer(r);
			}

			return F.NIL;
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
			if (ast.isAST0()) {
				// RandomReal() gives a double value between 0.0 and 1.0
				double r = Math.random();
				return F.num(r);
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
			if (ast.size() >= 2 && ast.size() <= 3) {
				if (ast.arg1().isAST()) {
					int n = ast.isAST2() ? ast.arg2().toIntDefault(Integer.MIN_VALUE) : Integer.MAX_VALUE;
					if (n >= 0) {
						return shuffle((IAST) ast.arg1(), n);
					}
				}
				return F.NIL;
			}
			Validate.checkRange(ast, 2, 3);
			return F.NIL;
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
