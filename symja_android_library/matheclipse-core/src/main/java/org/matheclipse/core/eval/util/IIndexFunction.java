package org.matheclipse.core.eval.util;

@FunctionalInterface
public interface IIndexFunction<T> {
	T evaluate(int[] index);
}
