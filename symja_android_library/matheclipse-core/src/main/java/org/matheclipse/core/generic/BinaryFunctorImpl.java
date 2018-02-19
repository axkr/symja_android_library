package org.matheclipse.core.generic;

import com.duy.lambda.BiFunction;
import com.duy.lambda.Function;

/**
 * A function object that takes two arguments and returns a result. The two arguments are both of type <code>T</code>
 * and the result is also of type <code>T</code>
 * 
 * @deprecated
 */
public abstract class BinaryFunctorImpl<T> implements BiFunction<T, T, T> {
	/**
	 * Executes the function and returns the result.
	 * 
	 * @throws FunctionException
	 */
	@Override
	public abstract T apply(T firstArg, T secondArg);

	public Function<T, T> bind2(final T p_param2) {
		final BinaryFunctorImpl<T> f2 = this;

		return new Function<T, T>() {
			@Override
			public T apply(T p_param1) {
				return f2.apply(p_param1, p_param2);
			}
		};
	}
}
