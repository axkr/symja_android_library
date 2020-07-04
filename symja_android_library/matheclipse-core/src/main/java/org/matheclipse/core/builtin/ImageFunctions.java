package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

public class ImageFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.MaxFilter.setEvaluator(new MaxFilter());
			F.MeanFilter.setEvaluator(new MeanFilter());
			F.MedianFilter.setEvaluator(new MedianFilter());
			F.MinFilter.setEvaluator(new MinFilter());
		}
	}

	private static class MinFilter extends AbstractEvaluator {

		protected IExpr filterHead() {
			return F.Min;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.arg1().isList()) {
					IAST list = (IAST) ast.arg1();
					final int radius = ast.arg2().toIntDefault();
					if (radius >= 0) {
						return filterHead(list, radius, filterHead(), engine);
					}
				}
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		private static IExpr filterHead(IAST list, final int radius, IExpr filterHead, EvalEngine engine) {
			final IASTMutable result = list.copy();
			final int size = list.size();
			list.forEach((x, i) -> result.set(i, engine.evaluate(//
					F.unaryAST1(//
							filterHead, //
							list.slice(Math.max(1, i - radius), Math.min(size, i + radius + 1))//
					))));
			return result;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}
	}

	private static class MaxFilter extends MinFilter {
		protected IExpr filterHead() {
			return F.Max;
		}
	}

	private static class MeanFilter extends MinFilter {
		protected IExpr filterHead() {
			return F.Mean;
		}
	}

	private static class MedianFilter extends MinFilter {
		protected IExpr filterHead() {
			return F.Median;
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private ImageFunctions() {

	}

}
