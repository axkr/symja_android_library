package org.matheclipse.core.generic;

/**
 * Represents a function that accepts two arguments (T, int) and produces a result.
 *
 * <p>
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 */
@FunctionalInterface
public interface ObjIntFunction<T, R> {

  /**
   * Applies this function to the given arguments.
   *
   * @param t the function argument
   * @param value
   * @return the function result
   */
  R apply(T t, int value);

}
