package org.matheclipse.core.interfaces;

/**
 * Represents a function with 4 arguments.
 *
 * @param <T1> argument 1 of the function
 * @param <T2> argument 2 of the function
 * @param <T3> argument 3 of the function
 * @param <T4> argument 4 of the function
 * @param <T5> argument 4 of the function
 * @param <R> return type of the function
 */
@FunctionalInterface
public interface Function5<T1, T2, T3, T4, T5, R> {

  /**
   * Applies this function to 4 arguments and returns the result.
   *
   * @param t1 argument 1
   * @param t2 argument 2
   * @param t3 argument 3
   * @param t4 argument 4
   * @param t5 argument 5
   * @return the result of function application
   * 
   */
  R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

}
