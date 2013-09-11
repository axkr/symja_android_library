package org.matheclipse.core.generic.interfaces;

import javax.annotation.Nullable;

/**
 * A BiFunction provides a transformation on two objects and returns the
 * resulting object.
 * 
 * <p>
 * The transformation on the source objects does not necessarily result in an
 * object of a different type.
 * 
 * <p>
 * Implementors of BiFunction which may cause side effects upon evaluation are
 * strongly encouraged to state this fact clearly in their API documentation.
 * 
 */
public interface BiIndexFunction<F1, F2, T> {

	/**
	 * Applys the function to an object of types {@code F1} and {@code F2},
	 * resulting in an object of type {@code T}. Note that types {@code F1},
	 * {@code F2} and {@code T} may or may not be the same.
	 * 
	 * @param index
	 *          The current processed index.
	 * @param from1
	 *          The first source object.
	 * @param from2
	 *          The second source object.
	 * @return The resulting object.
	 */
	T apply(int index, @Nullable F1 from1, @Nullable F2 from2);
}
