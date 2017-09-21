// code by jph
package org.matheclipse.core.eval.util;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.matheclipse.core.interfaces.IAST;

/**
 * an application of Ordering is to arrange the eigenvalues in
 * {@link Eigensystem} in descending order.
 * 
 * <p>
 * inspired by <a href=
 * "https://reference.wolfram.com/language/ref/Ordering.html">Ordering</a>
 */
public enum Ordering {
	INCREASING(tensor -> IntStream.range(1, tensor.size()) //
			.boxed().sorted((i, j) -> tensor.get(i).compareTo(tensor.get(j)))), //
	DECREASING(tensor -> IntStream.range(1, tensor.size()) //
			.boxed().sorted((i, j) -> tensor.get(j).compareTo(tensor.get(i)))), //
	;
	// ---
	private final OrderingInterface orderingInterface;

	private Ordering(OrderingInterface orderingInterface) {
		this.orderingInterface = orderingInterface;
	}

	/**
	 * @param vector
	 * @return array of indices i[:] so that vector[i[0]], vector[i[1]], ... is
	 *         ordered
	 */
	public int[] of(IAST vector) {
		return orderingInterface.stream(vector).mapToInt(Integer::intValue).toArray();
	}
}

/* private */ interface OrderingInterface {
	/**
	 * @param tensor
	 * @return stream of indices so that tensor[i0], tensor[i1], ... is ascending
	 */
	Stream<Integer> stream(IAST tensor);
}
