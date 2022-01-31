/*
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Open Software License (OSL 3.0) that is bundled with this
 * package in the file LICENSE.txt. It is also available through the world-wide-web at
 * http://opensource.org/licenses/osl-3.0.php If you did not receive a copy of the license and are
 * unable to obtain it through the world-wide-web, please send an email to magnos.software@gmail.com
 * so we can send you a copy immediately. If you use any of this software please notify me via our
 * website or email, your feedback is much appreciated.
 *
 * @copyright Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 *
 * @license http://opensource.org/licenses/osl-3.0.php Open Software License (OSL 3.0)
 */

package org.matheclipse.parser.trie;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * An implementation of a compact {@link Trie} with values that are collections
 *
 * @author Philip Diffenderfer
 * @param <S> The sequence/key type.
 * @param <T> The value type.
 * @param <C> The collection type.
 */
public class TrieCollection<S, T, C extends Collection<T>> extends Trie<S, C> {

  /** */
  private static final long serialVersionUID = 1L;

  /** The supplier of empty collections. */
  protected Supplier<C> supplier;

  /**
   * If the default value (the value returned when a search yields no results) should return an
   * empty collection.
   */
  protected boolean defaultCollection;

  /** Instantiates a new TrieCollection for deserialization. */
  protected TrieCollection() {}

  /**
   * Instantiates a new TrieCollection.
   *
   * @param sequencer The TrieSequencer which handles the necessary sequence operations.
   * @param supplier The function which creates new collections on demand.
   */
  public TrieCollection(TrieSequencer<S> sequencer, Supplier<C> supplier) {
    this(sequencer, supplier, false);
  }

  /**
   * Instantiates a new TrieCollection.
   *
   * @param sequencer The TrieSequencer which handles the necessary sequence operations.
   * @param supplier The function which creates new collections on demand.
   * @param defaultCollection If this Trie should return an empty collection if a query is done and
   *        no match is found.
   */
  public TrieCollection(TrieSequencer<S> sequencer, Supplier<C> supplier,
      boolean defaultCollection) {
    super(sequencer);

    this.supplier = supplier;
    this.defaultCollection = defaultCollection;
  }

  /**
   * Returns a TrieCollection with the same default value, match, and {@link TrieSequencer} as this
   * TrieCollection.
   *
   * @return The reference to a new TrieCollection.
   */
  @Override
  public TrieCollection<S, T, C> newEmptyClone() {
    TrieCollection<S, T, C> t = new TrieCollection<S, T, C>(sequencer, supplier, defaultCollection);
    t.defaultMatch = defaultMatch;
    return t;
  }

  /** */
  @Override
  public C getDefaultValue() {
    return defaultCollection ? supplier.get() : super.getDefaultValue();
  }

  /**
   * Adds a value to the collection found at the given sequence using the default matching method.
   *
   * @param query The sequence.
   * @param value The value to place in the collection.
   * @return The collection at the given sequence.
   */
  public C add(S query, T value) {
    return add(query, value, defaultMatch);
  }

  /**
   * Adds a value to the collection found at the given sequence using the given matching method.
   *
   * @param query The sequence.
   * @param value The value to place in the collection.
   * @param match The matching logic to use.
   * @return The collection at the given sequence.
   */
  public C add(S query, T value, TrieMatch match) {
    TrieNode<S, C> node = search(root, query, match);
    C collection = node != null ? node.value : null;

    if (collection == null) {
      collection = supplier.get();

      put(query, collection);
    }

    collection.add(value);

    return collection;
  }

  /**
   * Adds values to the collection found at the given sequence using the default matching method.
   *
   * @param query The sequence.
   * @param values The {@link Iterable} of values to place in the collection.
   * @return The collection at the given sequence.
   */
  public C addAll(S query, Iterable<T> values) {
    return addAll(query, values, defaultMatch);
  }

  /**
   * Adds values to the collection found at the given sequence using the given matching method.
   *
   * @param query The sequence.
   * @param values The {@link Iterable} of values to place in the collection.
   * @param match The matching logic to use.
   * @return The collection at the given sequence.
   */
  public C addAll(S query, Iterable<T> values, TrieMatch match) {
    TrieNode<S, C> node = search(root, query, match);
    C collection = node != null ? node.value : null;

    if (collection == null) {
      collection = supplier.get();

      put(query, collection);
    }

    for (T value : values) {
      collection.add(value);
    }

    return collection;
  }

  /**
   * Calculates the total number of items in all collections in this Trie.
   *
   * @return The number of all items in all collections.
   */
  public int totalSize() {
    int total = 0;

    for (C values : this.values()) {
      total += values.size();
    }

    return total;
  }
}
