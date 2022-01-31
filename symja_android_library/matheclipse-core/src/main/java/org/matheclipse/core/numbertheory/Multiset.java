package org.matheclipse.core.numbertheory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Multiset<T> {

  /**
   * Add an entry with multiplicity 1.
   *
   * @param entry
   * @return the previous multiplicity of the entry
   */
  int add(T entry);

  /**
   * Add one entry with given multiplicity.
   *
   * @param entry
   * @param mult
   * @return the previous multiplicity of the entry
   */
  int add(T entry, int mult);

  /**
   * Add another multiset to this.
   *
   * @param other
   */
  void addAll(Multiset<T> other);

  /**
   * Add all values of the given collection.
   *
   * @param values
   */
  void addAll(Collection<T> values);

  /**
   * Add all values of the given array.
   *
   * @param values
   */
  void addAll(T[] values);

  /**
   * Returns the multiplicity of the given value.
   *
   * The key is declared as Object and not as T to match exactly the signature of the same method in
   * the Map-interface. Nevertheless, of course the arguments should be of type T.
   *
   * @param value
   * @return multiplicity
   */
  Integer get(Object value);

  /**
   * Removes one instance of the given value from this multiset, if at least one element is
   * contained.
   *
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

  /**
   * Removes the key-value pair of the given key no matter what its multiplicity was
   *
   * @param key
   * @return previous multiplicity of the argument
   */
  int removeAll(T key);

  /**
   * Returns the multiset of elements contained in both this and in the other multiset.
   *
   * @param other
   * @return intersection of this and other
   */
  Multiset<T> intersect(Multiset<T> other);

  /**
   * @return The number of different elements
   */
  int size();

  /**
   * @return Total number of elements, i.e. the sum of multiplicities of all different elements
   */
  int totalCount();

  /**
   * @return set of distinct values
   */
  Set<T> keySet();

  /**
   * @return set of pairs (value, multiplicity)
   */
  Set<Map.Entry<T, Integer>> entrySet();

  /**
   * @return this as a flat list in which each value occurs 'multiplicity' times.
   */
  List<T> toList();

  String toString();

  boolean equals(Object o);
}
