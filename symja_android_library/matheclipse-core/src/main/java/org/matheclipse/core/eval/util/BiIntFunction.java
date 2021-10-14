package org.matheclipse.core.eval.util;

import java.util.function.BiFunction;

/**
 * Represents a function that accepts two int-valued arguments and produces a result. This is the
 * {@code int}-consuming primitive specialization for {@link BiFunction}.
 * 
 * @param <R> the type of the result of the function
 * @see BiFunction
 */
@FunctionalInterface
public interface BiIntFunction<R> {
  R apply(int value, int value2);
}
