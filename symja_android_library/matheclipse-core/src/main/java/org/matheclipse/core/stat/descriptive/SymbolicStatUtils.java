package org.matheclipse.core.stat.descriptive;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class SymbolicStatUtils {
	/** geometric mean */
	private static final SymbolicGeometricMean GEOMETRIC_MEAN = new SymbolicGeometricMean();

	/**
	 * Returns the geometric mean of the entries in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if the array is null.
	 * </p>
	 * <p>
	 * See {@link org.apache.commons.math3.stat.descriptive.moment.GeometricMean}
	 * for details on the computing algorithm.
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @return the geometric mean of the values or Double.NaN if the array is
	 *         empty
	 * @throws IllegalArgumentException
	 *           if the array is null
	 */
	public static IExpr geometricMean(final IAST values) {
		return GEOMETRIC_MEAN.evaluate(values);
	}
}
