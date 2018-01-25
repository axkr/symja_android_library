package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class TensorFunctions {
	static {
		F.Ordering.setEvaluator(new Ordering());
		F.ListConvolve.setEvaluator(new ListConvolve());
		F.ListCorrelate.setEvaluator(new ListCorrelate());
		F.TensorDimensions.setEvaluator(new TensorDimensions());
		F.TensorProduct.setEvaluator(new TensorProduct());
		F.TensorRank.setEvaluator(new TensorRank());
	}

	private static class ListConvolve extends AbstractEvaluator {
		/**
		 * See: <a href=
		 * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListConvolve.java">tensor/alg/ListConvolve.java</a>
		 * 
		 * <pre>
		 * ListConvolve({x, y}, {a, b, c, d, e, f}) ==
		 * {b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}
		 * </pre>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST kernel = (IAST) ast.arg1();
				IAST tensor = (IAST) ast.arg2();

				int kernelSize = kernel.size();
				int tensorSize = tensor.size();
				if (kernelSize <= tensorSize) {
					return ListCorrelate.listCorrelate(ListFunctions.reverse(kernel), kernelSize, tensor, tensorSize);
				}
			}
			return F.NIL;
		}
	}

	private static class ListCorrelate extends AbstractEvaluator {
		/**
		 * See: <a href=
		 * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListCorrelate.java">tensor/alg/ListCorrelate.java</a>
		 * 
		 * <pre>
		 * ListCorrelate({x, y}, {a, b, c, d, e, f}) ==
		 * {a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}
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
				int kernelSize = kernel.size();
				int tensorSize = tensor.size();
				if (kernelSize <= tensorSize) {
					return listCorrelate(kernel, kernelSize, tensor, tensorSize);
				}
			}
			return F.NIL;
		}

		public static IExpr listCorrelate(IAST kernel, int kernelSize, IAST tensor, int tensorSize) {
			ISymbol fFunction = F.Plus;
			ISymbol gFunction = F.Times;
			int diff = tensorSize - kernelSize;
			IASTAppendable resultList = F.ListAlloc(tensorSize - 1);
			final int[] fi = new int[1];
			for (int i = 0; i <= diff; i++) {
				IASTAppendable plus = F.ast(fFunction, kernelSize, false);
				fi[0] = i;
				plus.appendArgs(kernelSize, k -> F.binaryAST2(gFunction, kernel.get(k), tensor.get(k + fi[0])));
				// for (int k = 1; k < kernelSize; k++) {
				// plus.append(F.binaryAST2(gFunction, kernel.get(k), tensor.get(k + i)));
				// }
				resultList.append(plus);
			}
			return resultList;
		}
	}

	private static class Ordering extends AbstractEvaluator {

		/**
		 * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an array after sorting?</a>
		 *
		 */
		private static class ArrayIndexComparator implements Comparator<Integer> {
			protected final IAST ast;

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

		private static class PredicateComparator extends ArrayIndexComparator {
			final Comparator<IExpr> comparator;

			public PredicateComparator(IAST ast, Comparator<IExpr> comparator) {
				super(ast);
				this.comparator = comparator;
			}

			@Override
			public int compare(Integer index1, Integer index2) {
				return comparator.compare(ast.get(index1), ast.get(index2));
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				ArrayIndexComparator comparator;
				if (ast.size() >= 4) {
					// use the 3rd argument as a head for the comparator operation:
					comparator = new PredicateComparator(list, new Predicates.IsBinaryFalse(ast.arg3()));
				} else {
					// use the default IExpr#compareTo() method
					comparator = new ArrayIndexComparator(list);
				}
				Integer[] indexes = comparator.createIndexArray();
				Arrays.sort(indexes, comparator);
				int n = indexes.length;
				if (ast.size() >= 3) {
					IExpr arg2 = ast.arg2();
					if (arg2.equals(F.All)) {
					} else if (arg2.isSignedNumber()) {
						ISignedNumber sn = (ISignedNumber) arg2;
						n = sn.toIntDefault(Integer.MIN_VALUE);
					}
				}
				if (n == Integer.MIN_VALUE) {
					return F.NIL;
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

	private static class TensorProduct extends AbstractNonOrderlessArgMultiple {

		@Override
		public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
			if (o0.isList() && o1.isList()) {
				// TODO
				// IAST tensor1 = (IAST) o0;
				// IAST tensor2 = (IAST) o0;
				// return tensor1.mapList(tensor2::times, 1);
			}

			return F.NIL;
		}

		private IExpr numericalDot(final IExpr o0, final IExpr o1) throws MathIllegalArgumentException {
			return F.NIL;
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			return evaluate(ast, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
		}

	}

	private static class TensorRank extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				List<Integer> intList = LinearAlgebra.dimensions((IAST) ast.arg1(), list.head(), Integer.MAX_VALUE);
				return F.ZZ(intList.size());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static TensorFunctions CONST = new TensorFunctions();

	public static TensorFunctions initialize() {
		return CONST;
	}

	private TensorFunctions() {

	}

}
