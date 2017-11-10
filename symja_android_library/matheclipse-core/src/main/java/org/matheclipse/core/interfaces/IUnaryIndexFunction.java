package org.matheclipse.core.interfaces;

import javax.annotation.Nullable;

/**
 * A {@code IUnaryIndexFunction} provides a transformation of an integer and an
 * object and returns the resulting object.
 * 
 * <p>
 * The transformation on the source object does not necessarily result in an
 * object of a different type.
 * 
 * <p>
 * Implementors of BiFunction which may cause side effects upon evaluation are
 * strongly encouraged to state this fact clearly in their API documentation.
 * 
 * @param <F1>
 * @param <T>
 *            the type of the resulting object
 */
@FunctionalInterface
public interface IUnaryIndexFunction<F1, T> {

	/**
	 * Applies the function to an integer and an object of types {@code F1},
	 * resulting in an object of type {@code T}. Note that types {@code F1} and
	 * {@code T} may or may not be the same.
	 * 
	 * @param index
	 *            The current processed index.
	 * @param from1
	 *            The first source object.
	 * @return The resulting object.
	 */
	T apply(int index, @Nullable F1 from1);
}
