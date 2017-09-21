// code by jph
package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * Array[0 &, {0, 1}] == {}
 * Array.zeros(0, 1) == {}
 * 
 * Array[0 &, {1, 0, 1}] == {{}}
 * Array.zeros(1, 0, 1) == {{}}
 * </pre>
 */
public enum Array {
	;
	/**
	 * @param function
	 *            maps given index to {@link IAST}, or {@link IExpr}
	 * @param dimensions
	 * @return tensor with given dimensions and entries as function(index)
	 */
	public static IExpr of(Function<List<Integer>, ? extends IAST> function, Integer... dimensions) {
		return of(function, Arrays.asList(dimensions));
	}

	/**
	 * @param function
	 *            maps given index to {@link IAST}, or {@link IExpr}
	 * @param dimensions
	 * @return tensor with given dimensions and entries as function(index)
	 */
	public static IExpr of(Function<List<Integer>, ? extends IExpr> function, List<Integer> dimensions) {
		return _of(function, dimensions, Collections.emptyList());
	}

	/**
	 * @param dimensions
	 * @return tensor of {@link RealScalar#ZERO} with given dimensions
	 * @throws Exception
	 *             if any of the integer parameters is negative
	 */
	public static IExpr zeros(List<Integer> dimensions) {
		if (dimensions.size() == 0)
			return F.C0;
		int length = dimensions.get(0);
		if (length < 0)
			throw new IllegalArgumentException();
		return IAST.of(IntStream.range(0, length) //
				.mapToObj(i -> zeros(dimensions.subList(1, dimensions.size()))));
	}

	/**
	 * Careful: {@link #zeros(Integer...)} is not consistent with MATLAB::zeros. In
	 * the tensor library, the number of integer parameters equals the rank of the
	 * returned tensor. In Matlab this is not the case.
	 * 
	 * Examples:
	 * 
	 * <pre>
	 * Array.zeros(3) == Tensors.vector(0, 0, 0) == {0, 0, 0}
	 * Array.zeros(2, 3) == {{0, 0, 0}, {0, 0, 0}}
	 * </pre>
	 * 
	 * @param dimensions
	 * @return tensor of {@link RealScalar#ZERO} with given dimensions
	 * @throws Exception
	 *             if any of the integer parameters is negative
	 */
	public static IExpr zeros(Integer... dimensions) {
		return zeros(Arrays.asList(dimensions));
	}

	// helper function
	private static IExpr _of(Function<List<Integer>, ? extends IExpr> function, List<Integer> dimensions,
			List<Integer> index) {
		int level = index.size();
		if (level == dimensions.size()) {
			return function.apply(index);
		}
		List<Integer> copy = new ArrayList<>(index);
		copy.add(-1);
		int length = dimensions.get(level);
		if (length < 0) {
			throw new IllegalArgumentException();
		}
		IAST tensor = F.ListAlloc(length);// Tensors.empty();
		for (int count = 1; count < length; ++count) {
			copy.set(level, count);
			tensor.append(_of(function, dimensions, copy));
		}
		return tensor;
	}
}
