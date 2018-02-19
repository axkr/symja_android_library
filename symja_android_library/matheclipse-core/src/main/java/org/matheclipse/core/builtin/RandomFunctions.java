
package org.matheclipse.core.builtin;

import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import java.math.BigInteger;
import java.util.Random;
import com.duy.lambda.IntFunction;

public final class RandomFunctions {

	static {
		F.RandomInteger.setEvaluator(new RandomInteger());
		F.RandomChoice.setEvaluator(new RandomChoice());
		F.RandomReal.setEvaluator(new RandomReal());
		F.RandomSample.setEvaluator(new RandomSample());
	}

	private static class RandomChoice extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 0 && ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				Random random = new Random();
				int listSize = list.size() - 1;
				int randomIndex = random.nextInt(listSize);
				return list.get(randomIndex + 1);
			}

			return F.NIL;
		}

	}

	private static class RandomInteger extends AbstractFunctionEvaluator {
		private final static Random RANDOM = new Random();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				// RandomInteger(100) gives an integer between 0 and 100
				BigInteger n = ((IInteger) ast.arg1()).toBigNumerator();
				BigInteger r;
				do {
					r = new BigInteger(n.bitLength(), RANDOM);
				} while (r.compareTo(n) >= 0);
				return F.integer(r);
			}

			return F.NIL;
		}

	}

	private static class RandomReal extends AbstractFunctionEvaluator {

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
	 * Create a random shuffled list.
	 * 
	 */
	private static class RandomSample extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isAST()) {
				return shuffle((IAST) ast.arg1());
			}

			return F.NIL;
		}

		public static IAST shuffle(IAST list) {
			final int len = list.size() - 1;

			// Shuffle indices.
			final int[] indexList = MathArrays.natural(len);
			MathArrays.shuffle(indexList);

			// Create shuffled list.
			return list.copy().setArgs(1, len + 1, new IntFunction<IExpr>() {
                @Override
                public IExpr apply(int i) {
                    return list.get(indexList[i - 1] + 1);
                }
            });
			// for (int i = 0; i < len; i++) {
			// out.set(i + 1, list.get(indexList[i] + 1));
			// }
			// return out;
		}
	}

	private final static RandomFunctions CONST = new RandomFunctions();

	public static RandomFunctions initialize() {
		return CONST;
	}

	private RandomFunctions() {

	}

}
