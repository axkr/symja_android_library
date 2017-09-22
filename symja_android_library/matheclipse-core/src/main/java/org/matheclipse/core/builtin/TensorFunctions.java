package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class TensorFunctions {
	static {
		F.Ordering.setEvaluator(new Ordering());
		F.ListCorrelate.setEvaluator(new ListCorrelate());
		F.TensorDimensions.setEvaluator(new TensorDimensions());
		F.TensorRank.setEvaluator(new TensorRank());
	}

	private static class ListCorrelate extends AbstractEvaluator {
		/**
		 * <pre>
		  * ListCorrelate[{x, y}, {a, b, c, d, e, f}] ==
		  * {a x + b y, b x + c y, c x + d y, d x + e y, e x + f y}
		 * </pre>
		 * 
		 * @param kernel
		 * @param tensor
		 * @return correlation of kernel with tensor
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST kernel = (IAST) ast.arg1();
				IAST tensor = (IAST) ast.arg2();
				// List<Integer> mask = LinearAlgebra.getDimensions(kernel, kernel.head(),
				// Integer.MAX_VALUE);
				// List<Integer> size = LinearAlgebra.getDimensions(tensor, tensor.head(),
				// Integer.MAX_VALUE);
			}
			return F.NIL;
		}
	}

	private static class Ordering extends AbstractEvaluator {

		/**
		 * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an
		 * array after sorting?</a>
		 *
		 */
		private static class ArrayIndexComparator implements Comparator<Integer> {
			private final IAST ast;

			public ArrayIndexComparator(IAST ast) {
				this.ast = ast;
			}

			public Integer[] createIndexArray() {
				int size = ast.size();
				Integer[] indexes = new Integer[size - 1];
				for (int i = 1; i < size; i++) {
					indexes[i - 1] = i;
				}
				return indexes;
			}

			@Override
			public int compare(Integer index1, Integer index2) {
				return ast.get(index1).compareTo(ast.get(index2));
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				ArrayIndexComparator comparator = new ArrayIndexComparator(list);
				Integer[] indexes = comparator.createIndexArray();
				Arrays.sort(indexes, comparator);
				int n = indexes.length;
				if (ast.size() == 3) {
					IExpr arg2 = ast.arg2();
					if (arg2.equals(F.All)) {
						n = indexes.length;
					} else if (arg2.isSignedNumber()) {
						ISignedNumber sn = (ISignedNumber) arg2;
						try {
							n = sn.toInt();
							if (n < 0) {
								return F.NIL;
							}
						} catch (ArithmeticException ae) {
							return F.NIL;
						}
					}
				}
				return F.List(n, indexes);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private static class TensorDimensions extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				// same as Dimensions for List structures
				return F.Dimensions(ast.arg1());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private static class TensorRank extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				List<Integer> intList = LinearAlgebra.getDimensions((IAST) ast.arg1(), list.head(), Integer.MAX_VALUE);
				return F.ZZ(intList.size());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	final static TensorFunctions CONST = new TensorFunctions();

	public static TensorFunctions initialize() {
		return CONST;
	}

	private TensorFunctions() {

	}

}
