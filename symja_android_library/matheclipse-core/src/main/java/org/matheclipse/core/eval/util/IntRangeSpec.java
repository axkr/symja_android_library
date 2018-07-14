package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class IntRangeSpec {
	final int min;
	final int max;

	/**
	 * Create the range <code>[min, max]</code>
	 * 
	 * @return the range <code>[min, max]</code>
	 */
	public IntRangeSpec(int min, int max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Create the default non-negative range <code>[0, Integer.MAX_VALUE]</code>
	 * 
	 * @return the range <code>[0, Integer.MAX_VALUE]</code>
	 */
	public static IntRangeSpec createNonNegative() {
		return new IntRangeSpec(0, Integer.MAX_VALUE);
	}

	/**
	 * Determine a non-negative range in the interval <code>[0, Integer.MAX_VALUE]</code> from
	 * <code>ast.get(position)</code>. If some parts of the range are negative or undefined return <code>null</code>.
	 * <ul>
	 * <li>&quot;All&quot; - gives <code>[0, Integer.MAX_VALUE]</code></li>
	 * <li>&quot;int value&quot; - gives exactly <code>[0, &quot;int value&quot;]</code></li>
	 * <li>&quot;{int value}&quot; - gives <code>[&quot;int value&quot;, &quot;int value&quot;]</code></li>
	 * <li>&quot;{min, max}&quot; - gives <code>[&quot;min&quot;, &quot;max&quot;]</code></li>
	 * </ul>
	 * 
	 * @param specification
	 * @return <code>null</code> if no <code>int</code> range in the interval <code>[0, Integer.MAX_VALUE]</code> could
	 *         be determined
	 */
	public static IntRangeSpec createNonNegative(IAST ast, int position) {
		IntRangeSpec range = null;
		if (ast.size() <= position) {
			range = IntRangeSpec.createNonNegative();
		} else if (ast.size() > position) {
			range = IntRangeSpec.createNonNegative(ast.get(position));
		}
		return range;
	}

	/**
	 * Determine a non-negative range in the interval <code>[0, Integer.MAX_VALUE]</code>. If some parts of the range
	 * are negative or undefined return <code>null</code>.
	 * <ul>
	 * <li>&quot;All&quot; - gives <code>[0, Integer.MAX_VALUE]</code></li>
	 * <li>&quot;int value&quot; - gives exactly <code>[0, &quot;int value&quot;]</code></li>
	 * <li>&quot;{int value}&quot; - gives <code>[&quot;int value&quot;, &quot;int value&quot;]</code></li>
	 * <li>&quot;{min, max}&quot; - gives <code>[&quot;min&quot;, &quot;max&quot;]</code></li>
	 * </ul>
	 * 
	 * @param specification
	 * @return <code>null</code> if no <code>int</code> range in the interval <code>[0, Integer.MAX_VALUE]</code> could
	 *         be determined
	 */
	public static IntRangeSpec createNonNegative(IExpr specification) {
		int min = 0;
		int max = Integer.MAX_VALUE;
		if (specification.equals(F.All) || specification.isInfinity()) {
			// all from 0 to Integer.MAX_VALUE
		} else if (specification.isInteger()) {
			// k - at most k elements
			max = specification.toIntDefault(-1);
			if (max < 0) {
				return null;
			}
		} else if (specification.isAST(F.List, 2)) {
			// {k} - exactly k
			min = specification.first().toIntDefault(-1);
			if (min < 0) {
				return null;
			}
			max = min;
		} else if (specification.isAST(F.List, 3)) {
			// {min, max}
			min = specification.first().toIntDefault(-1);
			if (min < 0) {
				return null;
			}
			max = specification.second().toIntDefault(-1);
			if (max < 0) {
				return null;
			}
		} else {
			return null;
		}
		return new IntRangeSpec(min, max);
	}

	public boolean isIncluded(int value) {
		return (min <= value && value <= max);
	}
}
