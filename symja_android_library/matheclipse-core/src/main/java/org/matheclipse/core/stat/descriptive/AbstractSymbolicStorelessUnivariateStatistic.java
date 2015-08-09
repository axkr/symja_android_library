package org.matheclipse.core.stat.descriptive;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatistic;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
*
* Abstract implementation of the {@link StorelessUnivariateStatistic} interface.
* <p>
* Provides default <code>evaluate()</code> and <code>incrementAll(double[])<code>
* implementations.</p>
* <p>
* <strong>Note that these implementations are not synchronized.</strong></p>
*
* @version $Id: AbstractStorelessUnivariateStatistic.java 1244107 2012-02-14 16:17:55Z erans $
*/
public abstract class AbstractSymbolicStorelessUnivariateStatistic implements StorelessSymbolicUnivariateStatistic {
	/**
	 * This default implementation calls {@link #clear}, then invokes
	 * {@link #increment} in a loop over the the input array, and then uses
	 * {@link #getResult} to compute the return value.
	 * <p>
	 * Note that this implementation changes the internal state of the statistic.
	 * Its side effects are the same as invoking {@link #clear} and then
	 * {@link #incrementAll(double[])}.
	 * </p>
	 * <p>
	 * Implementations may override this method with a more efficient and possibly
	 * more accurate implementation that works directly with the input array.
	 * </p>
	 * <p>
	 * If the array is null, an IllegalArgumentException is thrown.
	 * </p>
	 * 
	 * @param values
	 *          input array
	 * @return the value of the statistic applied to the input array
	 * @see org.apache.commons.math4.stat.descriptive.UnivariateStatistic#evaluate(double[])
	 */
	public IExpr evaluate(final IAST values) {
		if (values == null) {
			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}
		return evaluate(values, 1, values.size() - 1);
	}

	/**
	 * This default implementation calls {@link #clear}, then invokes
	 * {@link #increment} in a loop over the specified portion of the input array,
	 * and then uses {@link #getResult} to compute the return value.
	 * <p>
	 * Note that this implementation changes the internal state of the statistic.
	 * Its side effects are the same as invoking {@link #clear} and then
	 * {@link #incrementAll(double[], int, int)}.
	 * </p>
	 * <p>
	 * Implementations may override this method with a more efficient and possibly
	 * more accurate implementation that works directly with the input array.
	 * </p>
	 * <p>
	 * If the array is null or the index parameters are not valid, an
	 * IllegalArgumentException is thrown.
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param begin
	 *          the index of the first element to include
	 * @param length
	 *          the number of elements to include
	 * @return the value of the statistic applied to the included array entries
	 * @see org.apache.commons.math4.stat.descriptive.UnivariateStatistic#evaluate(double[],
	 *      int, int)
	 */
	public IExpr evaluate(final IAST values, final int begin, final int length) {
		if (test(values, begin, length)) {
			clear();
			incrementAll(values, begin, length);
		}
		return getResult();
	}

	public abstract long getN();

	/**
	 * {@inheritDoc}
	 */
	public abstract void clear();

	/**
	 * {@inheritDoc}
	 */
	public abstract IExpr getResult();

	/**
	 * {@inheritDoc}
	 */
	public abstract void increment(final IExpr d);

	/**
	 * This default implementation just calls {@link #increment} in a loop over
	 * the input array.
	 * <p>
	 * Throws IllegalArgumentException if the input values array is null.
	 * </p>
	 * 
	 * @param values
	 *          values to add
	 * @throws IllegalArgumentException
	 *           if values is null
	 * @see org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatistic#incrementAll(double[])
	 */
	public void incrementAll(IAST values) {
		if (values == null) {
			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}
		incrementAll(values, 1, values.size() - 1);
	}

	/**
	 * This default implementation just calls {@link #increment} in a loop over
	 * the specified portion of the input array.
	 * <p>
	 * Throws IllegalArgumentException if the input values array is null.
	 * </p>
	 * 
	 * @param values
	 *          array holding values to add
	 * @param begin
	 *          index of the first array element to add
	 * @param length
	 *          number of array elements to add
	 * @throws IllegalArgumentException
	 *           if values is null
	 * @see org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatistic#incrementAll(double[],
	 *      int, int)
	 */
	public void incrementAll(IAST values, int begin, int length) {
		if (test(values, begin, length)) {
			int k = begin + length;
			for (int i = begin; i < k; i++) {
				increment(values.get(i));
			}
		}
	}

	/**
	 * This method is used by <code>evaluate(double[], int, int)</code> methods to
	 * verify that the input parameters designate a subarray of positive length.
	 * <p>
	 * <ul>
	 * <li>returns <code>true</code> iff the parameters designate a subarray of
	 * positive length</li>
	 * <li>throws <code>IllegalArgumentException</code> if the array is null or or
	 * the indices are invalid</li>
	 * <li>returns <code>false</li> if the array is non-null, but
	 * <code>length</code> is 0.
	 * </ul>
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param begin
	 *          index of the first array element to include
	 * @param length
	 *          the number of elements to include
	 * @return true if the parameters are valid and designate a subarray of
	 *         positive length
	 * @throws IllegalArgumentException
	 *           if the indices are invalid or the array is null
	 */
	protected boolean test(final IAST values, final int begin, final int length) {
		return test(values, begin, length, false);
	}

	/**
	 * This method is used by <code>evaluate(double[], int, int)</code> methods to
	 * verify that the input parameters designate a subarray of positive length.
	 * <p>
	 * <ul>
	 * <li>returns <code>true</code> iff the parameters designate a subarray of
	 * non-negative length</li>
	 * <li>throws <code>IllegalArgumentException</code> if the array is null or or
	 * the indices are invalid</li>
	 * <li>returns <code>false</li> if the array is non-null, but
	 * <code>length</code> is 0 unless <code>allowEmpty</code> is
	 * <code>true</code>
	 * </ul>
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param begin
	 *          index of the first array element to include
	 * @param length
	 *          the number of elements to include
	 * @param allowEmpty
	 *          if <code>true</code> then zero length arrays are allowed
	 * @return true if the parameters are valid
	 * @throws IllegalArgumentException
	 *           if the indices are invalid or the array is null
	 * @since 3.0
	 */
	protected boolean test(final IAST values, final int begin, final int length, final boolean allowEmpty) {

		if (values == null) {
			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}

		if (begin < 0) {
			throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
		}

		if (length < 0) {
			throw new NotPositiveException(LocalizedFormats.LENGTH, length);
		}

		if (begin + length > values.size()) {
			throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, begin + length, values.size(), true);
		}

		if (length == 0 && !allowEmpty) {
			return false;
		}

		return true;

	}

	/**
	 * This method is used by <code>evaluate(double[], double[], int, int)</code>
	 * methods to verify that the begin and length parameters designate a subarray
	 * of positive length and the weights are all non-negative, non-NaN, finite,
	 * and not all zero.
	 * <p>
	 * <ul>
	 * <li>returns <code>true</code> iff the parameters designate a subarray of
	 * positive length and the weights array contains legitimate values.</li>
	 * <li>throws <code>IllegalArgumentException</code> if any of the following
	 * are true:
	 * <ul>
	 * <li>the values array is null</li>
	 * <li>the weights array is null</li>
	 * <li>the weights array does not have the same length as the values array</li>
	 * <li>the weights array contains one or more infinite values</li>
	 * <li>the weights array contains one or more NaN values</li>
	 * <li>the weights array contains negative values</li>
	 * <li>the start and length arguments do not determine a valid array</li>
	 * </ul>
	 * </li>
	 * <li>returns <code>false</li> if the array is non-null, but
	 * <code>length</code> is 0.
	 * </ul>
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param weights
	 *          the weights array
	 * @param begin
	 *          index of the first array element to include
	 * @param length
	 *          the number of elements to include
	 * @return true if the parameters are valid and designate a subarray of
	 *         positive length
	 * @throws IllegalArgumentException
	 *           if the indices are invalid or the array is null
	 * @since 2.1
	 */
	protected boolean test(final IAST values, final IAST weights, final int begin, final int length) {
		return test(values, weights, begin, length, false);
	}

	/**
	 * This method is used by <code>evaluate(double[], double[], int, int)</code>
	 * methods to verify that the begin and length parameters designate a subarray
	 * of positive length and the weights are all non-negative, non-NaN, finite,
	 * and not all zero.
	 * <p>
	 * <ul>
	 * <li>returns <code>true</code> iff the parameters designate a subarray of
	 * non-negative length and the weights array contains legitimate values.</li>
	 * <li>throws <code>IllegalArgumentException</code> if any of the following
	 * are true:
	 * <ul>
	 * <li>the values array is null</li>
	 * <li>the weights array is null</li>
	 * <li>the weights array does not have the same length as the values array</li>
	 * <li>the weights array contains one or more infinite values</li>
	 * <li>the weights array contains one or more NaN values</li>
	 * <li>the weights array contains negative values</li>
	 * <li>the start and length arguments do not determine a valid array</li>
	 * </ul>
	 * </li>
	 * <li>returns <code>false</li> if the array is non-null, but
	 * <code>length</code> is 0 unless <code>allowEmpty</code> is
	 * <code>true</code>.
	 * </ul>
	 * </p>
	 * 
	 * @param values
	 *          the input array.
	 * @param weights
	 *          the weights array.
	 * @param begin
	 *          index of the first array element to include.
	 * @param length
	 *          the number of elements to include.
	 * @param allowEmpty
	 *          if {@code true} than allow zero length arrays to pass.
	 * @return {@code true} if the parameters are valid.
	 * @throws IllegalArgumentException
	 *           if the indices are invalid or the array is {@code null}.
	 * @since 3.0
	 */
	protected boolean test(final IAST values, final IAST weights, final int begin, final int length, final boolean allowEmpty) {

		if (weights == null) {
			throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
		}

		if (weights.size() != values.size()) {
			throw new DimensionMismatchException(weights.size(), values.size());
		}

		boolean containsPositiveWeight = false;
		for (int i = begin; i < begin + length; i++) {
			// TODO implement exceptions
			// if (Double.isNaN(weights[i])) {
			// throw new
			// MathIllegalArgumentException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, i);
			// }
			// if (Double.isInfinite(weights[i])) {
			// throw new
			// MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT,
			// weights[i], i);
			// }
			// if ( weights.get(i) < 0) {
			// throw new
			// MathIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX,
			// i, weights[i]);
			// }
			// if (!containsPositiveWeight && weights[i] > 0.0) {
			// containsPositiveWeight = true;
			// }
		}

		if (!containsPositiveWeight) {
			throw new MathIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO);
		}

		return test(values, begin, length, allowEmpty);
	}
}
