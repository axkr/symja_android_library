package org.matheclipse.core.numbertheory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * A multiset with a sort relation between elements.<br>
 * The sorting of elements has the following consequences: - elements must be Comparable -
 * SortedMaps are a bit slower than HashMaps - output of sorted multisets looks nicer than that of
 * unsorted multisets
 *
 * <p>
 * Since elements are sorted, we can easily define a sorting on SortedMultisets, too: A sorted
 * multiset is bigger than another one if it's biggest element is bigger than the largest element of
 * the other multiset; or the 2.nd-biggest if the biggest elements are equal; or the 3.rd biggest,
 * and so on.
 *
 * @param <T> the value class
 */
public interface SortedMultiset<T extends Comparable<T>>
    extends Multiset<T>, SortedMap<T, Integer>, Comparable<SortedMultiset<T>> {

  /** @return The smallest element. */
  T getSmallestElement();

  /** @return The biggest element. */
  T getBiggestElement();

  /** @return an iterator that returns biggest elements first. */
  Iterator<Map.Entry<T, Integer>> getTopDownIterator();

  /**
   * Returns the multiset of elements contained in both this and in the other multiset.
   *
   * @param other
   * @return the intersection of this and the other multiset
   */
  SortedMultiset<T> intersect(Multiset<T> other);

  /**
   * Conversion to String, with
   *
   * @param entrySep e.g. "*" for multiplicative elements
   * @param expSep e.g. "^" for multiplicative elements
   * @return a string representation of this
   */
  String toString(String entrySep, String expSep);

  /** @return set of pairs (value, multiplicity) */
  Set<Map.Entry<T, Integer>> entrySet();

  /**
   * Removes one instance of the given value from this multiset, if at least one element is
   * contained.
   *
   * <p>
   * The key is declared as Object and not as T to match exactly the signature of the same method in
   * the Map-interface. Nevertheless, of course the arguments should be of type T.
   *
   * @param key
   * @return previous multiplicity of the argument
   */
  Integer remove(Object key);

  /**
   * Remove the given key multiple times.
   *
   * @param key
   * @param mult
   * @return old multiplicity
   */
  int remove(T key, int mult);
}
