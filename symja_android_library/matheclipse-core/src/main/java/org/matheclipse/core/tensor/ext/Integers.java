// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;
import org.matheclipse.core.expression.S;

public class Integers {

  /**
   * @param value non-negative
   * @return value
   * @throws Exception if given value is negative
   */
  public static int requirePositiveOrZero(int value) {
    if (0 <= value) {// non-negative, greater or equals zero
      return value;
    }
    throw new IllegalArgumentException(value + " < 0");
  }

  /**
   * @param value strictly positive
   * @return value
   * @throws Exception if given value is negative or zero
   */
  public static int requirePositive(int value) {
    if (0 < value) {// strictly positive
      return value;
    }
    throw new IllegalArgumentException(value + " <= 0");
  }

  /**
   * intended for use in reductive algorithms, for instance in the addition of two vectors, where
   * the length of the two vectors have to be equal.
   * 
   * inspired by junit's assertEquals
   * 
   * @param lhs
   * @param rhs
   * @return the identical value of lhs and rhs
   * @throws Exception if lhs and rhs are not equal
   */
  public static int requireEquals(int lhs, int rhs) {
    if (lhs == rhs) {
      return lhs;
    }
    throw new IllegalArgumentException(lhs + " != " + rhs);
  }

  /**
   * @param lhs
   * @param rhs
   * @throws Exception if lhs is not less than rhs
   */
  public static void requireLessThan(int lhs, int rhs) {
    if (lhs >= rhs) {
      throw new IllegalArgumentException(lhs + " >= " + rhs);
    }
  }

  /**
   * @param lhs
   * @param rhs
   * @throws Exception if lhs is not less than or equals rhs
   */
  public static void requireLessEquals(int lhs, int rhs) {
    if (lhs > rhs) {
      throw new IllegalArgumentException(lhs + " > " + rhs);
    }
  }

  /**
   * @param value
   * @return whether given value is an even number
   */
  public static boolean isEven(int value) {
    return (value & 1) == 0;
  }

  /**
   * @param value strictly positive
   * @return true if value is a power of 2, e.g. 1, 2, 4, 8, 16, etc.
   * @throws Exception if given value is negative or zero
   */
  public static boolean isPowerOf2(int value) {
    return 0 == (requirePositive(value) & (value - 1));
  }

  /**
   * @param min inclusive
   * @param max inclusive
   * @return function that maps an integer x into the closed interval [min, max]
   */
  public static IntUnaryOperator clip(int min, int max) {
    requireLessEquals(min, max);
    return operand -> Math.min(Math.max(min, operand), max);
  }

  // ---
  /**
   * {@link S#PermutationListQ}<code>(permutation-list)</code> - if `permutation-list` is a valid
   * permutation list return <code>true</code>.
   * 
   * @param sigma
   * @return whether sigma encodes a permutation for instance {2, 0, 1, 3}
   */
  public static boolean isPermutation(int[] sigma) {
    return sigma.length == Arrays.stream(sigma) //
        .filter(index -> 0 <= index && index < sigma.length).distinct().count();
  }

  /**
   * @param sigma
   * @return
   * @throws Exception if sigma does not encode a permutation
   */
  public static int[] requirePermutation(int[] sigma) {
    if (isPermutation(sigma)) {
      return sigma;
    }
    throw new IllegalArgumentException(sigma.length <= 16 ? IntList.wrap(sigma).toString() : "");
  }

  /**
   * Examples:
   * 
   * <pre>
   * parity[{0, 1}] == 0
   * parity[{1, 0}] == 1
   * parity[{2, 0, 1}] == 0
   * </pre>
   * 
   * The provided array sigma is not modified by the function. Instead, the algorithm works with a
   * copy of the array.
   * 
   * @param sigma a permutation of the list {0, 1, ..., sigma.length - 1}
   * @return 0 if sigma is even permutation, or 1 if sigma is odd permutation
   * @throws Exception if given sigma is not a permutation
   */
  public static int parity(int[] sigma) {
    int[] array = Arrays.copyOf(requirePermutation(sigma), sigma.length);
    int parity = 0;
    for (int index = 0; index < array.length; ++index) {
      while (array[index] != index) {
        int value = array[index];
        array[index] = array[value];
        array[value] = value;
        parity ^= 1;
      }
    }
    return parity;
  }

  // ---
  /**
   * Careful: The elements of the returned list are backed by the given array. Any subsequent
   * modification of the array is reflected by the list.
   * 
   * Remark: a sublist of the returned list is {@link Serializable}. This is in contrast to sublists
   * of {@link Arrays#asList(Object...)} or {@link ArrayList}, which are not serializable.
   * 
   * @param array non-null
   * @return unmodifiable list with elements {array[0], array[1], ...} and size of array.length
   * @see Arrays#asList(Object...)
   */
  public static List<Integer> asList(int[] array) {
    return IntList.wrap(array);
  }
}
