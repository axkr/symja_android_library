package org.matheclipse.core.interfaces;

@FunctionalInterface
public interface IDimensionFunction<T> {

  T apply(int[] index);
}
