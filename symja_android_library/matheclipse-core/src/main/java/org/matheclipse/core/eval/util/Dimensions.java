// code by jph
package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public enum Dimensions {
	;
	/**
	 * Examples:
	 * 
	 * <pre>
	 * Dimensions.of[3.14] = {}
	 * Dimensions.of[{}] == {0}
	 * Dimensions.of[{1, 2, 3}] == {3}
	 * Dimensions.of[{{1, 2, 3}, {4, 5, 6}}] == {2, 3}
	 * </pre>
	 * 
	 * @return dimensions of this tensor
	 */
	public static List<Integer> of(IAST tensor) {
		return _list(_complete(tensor));
	}

	/**
	 * @param tensor
	 * @return true if tensor.length() == 0, and false if tensor contains entries or
	 *         is a {@link IExpr}
	 */
	public static boolean isEmptyTensor(IAST tensor) { // Marc's function
		return tensor.size() == 0;
	}

	/***************************************************/
	/**
	 * @return true if tensor structure is identical at all levels, else false. true
	 *         for {@link IExpr}s
	 * 
	 * @see ArrayQ
	 */
	/* package */ static boolean isArray(IAST tensor) {
		return _isArray(_complete(tensor));
	}

	/* package */ static boolean isArrayWithRank(IAST tensor, int rank) {
		List<Set<Integer>> complete = _complete(tensor);
		return _list(complete).size() == rank && _isArray(complete);
	}

	/* package */ static boolean isArrayWithDimensions(IAST tensor, List<Integer> dims) {
		List<Set<Integer>> complete = _complete(tensor);
		return _list(complete).equals(dims) && _isArray(complete);
	}

	/* package */ static Optional<Integer> arrayRank(IAST tensor) {
		List<Set<Integer>> complete = _complete(tensor);
		return _isArray(complete) ? Optional.of(_list(complete).size()) : Optional.empty();
	}

	/***************************************************/
	private static boolean _isArray(List<Set<Integer>> complete) {
		return complete.stream().mapToInt(Set::size).allMatch(size -> size == 1);
	}

	private static List<Integer> _list(List<Set<Integer>> complete) {
		List<Integer> ret = new ArrayList<>();
		for (Set<Integer> set : complete)
			if (set.size() == 1) {
				int val = set.iterator().next(); // get unique element from set
				// if (val == IExpr.LENGTH) // has scalar
				// break;
				ret.add(val);
			} else
				break;
		return ret;
	}

	/**
	 * @param tensor
	 * @return list of set of lengths on all levels also includes length of scalars
	 *         as Scalar.LENGTH == -1
	 */
	private static List<Set<Integer>> _complete(IAST tensor) {
		return _sets(tensor, 0, new ArrayList<>());
	}

	// helper function
	private static List<Set<Integer>> _sets(IExpr tensor, int level, List<Set<Integer>> sets) {
		if (sets.size() <= level)
			sets.add(new HashSet<>());
		if (tensor.isAST()) {// ScalarQ.of(tensor))
			sets.get(level).add(((IAST) tensor).size() - 1);
			((IAST) tensor).stream().forEach(entry -> _sets(entry, level + 1, sets));
		} else {
			sets.get(level).add(0);
		}
		return sets;
	}
}
