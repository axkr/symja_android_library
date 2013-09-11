package org.matheclipse.core.generic.interfaces;

/**
 * A INumericFunction provides a transformation of a <code>double</code> value
 * into a resulting object.
 * 
 * <p>
 * Implementors of INumericFunction which may cause side effects upon evaluation
 * are strongly encouraged to state this fact clearly in their API
 * documentation.
 * 
 */
public interface INumericFunction<T> {

	/**
	 * Applys the function to a <code>double</code> value, resulting in an object
	 * of type {@code T}.
	 * 
	 * @param value
	 *          a given <code>double</code> value.
	 * @return The resulting object.
	 */
	T apply(double value);
}
