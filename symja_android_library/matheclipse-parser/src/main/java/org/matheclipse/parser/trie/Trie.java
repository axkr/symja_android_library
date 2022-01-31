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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * An implementation of a compact Trie. <br>
 * <br>
 * <i>From Wikipedia:</i> <br>
 * <br>
 * <code>
 * an ordered tree data structure that is used to store a dynamic set or associative array where the keys are usually strings. Unlike a binary search tree, no node in the tree stores the key associated with that node; instead, its position in the tree defines the key with which it is associated. All the descendants of a node have a common prefix of the string associated with that node, and the root is associated with the empty string. Values are normally not associated with every node, only with leaves and some inner nodes that correspond to keys of interest. For the space-optimized presentation of prefix tree, see compact prefix tree.
 * </code> <br>
 *
 * @author Philip Diffenderfer
 * @param <S> The sequence/key type.
 * @param <T> The value type.
 */
@SuppressWarnings("unchecked")
public class Trie<S, T> implements Map<S, T>, Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  /** An empty collection/set to return. */
  private static final EmptyContainer<?> EMPTY_CONTAINER = new EmptyContainer<Object>();

  /** The root of the Trie. */
  protected final TrieNode<S, T> root;

  /** The implementation that understands the Trie's sequences. */
  protected TrieSequencer<S> sequencer;

  /** The default match of the Trie when one is not specified. */
  protected TrieMatch defaultMatch = TrieMatch.STARTS_WITH;

  /** The cached set of sequences stored in this Trie. */
  protected transient SequenceSet sequences;

  /** The cached collection of values stored in this Trie. */
  protected transient ValueCollection values;

  /** The cached set of Entries stored in this Trie. */
  protected transient EntrySet entries;

  /** The cached set of Nodes stored in this Trie. */
  protected transient NodeSet nodes;

  /** Instantiates a new Trie for deserialization. */
  protected Trie() {
    this(null, null);
  }

  /**
   * Instantiates a new Trie.
   *
   * @param sequencer The TrieSequencer which handles the necessary sequence operations.
   */
  public Trie(TrieSequencer<S> sequencer) {
    this(sequencer, null);
  }

  /**
   * Instantiates a new Trie.
   *
   * @param sequencer The TrieSequencer which handles the necessary sequence operations.
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called and no match was
   *        found.
   */
  public Trie(TrieSequencer<S> sequencer, T defaultValue) {
    this.root =
        new TrieNode<S, T>(null, defaultValue, null, 0, 0, new PerfectHashMap<TrieNode<S, T>>());
    this.sequences = new SequenceSet(root);
    this.values = new ValueCollection(root);
    this.entries = new EntrySet(root);
    this.nodes = new NodeSet(root);
    this.sequencer = sequencer;
  }

  /**
   * Sets the default value of the Trie, which is the value returned when a query is unsuccessful.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called and no match was
   *        found.
   */
  public void setDefaultValue(T defaultValue) {
    root.value = defaultValue;
  }

  /**
   * Gets the default value of the Trie, which is the value returned when a query is unsuccessful.
   *
   * @return The default value of the Trie.
   */
  public T getDefaultValue() {
    return root.value;
  }

  /**
   * Returns a Trie with the same default value, match, and {@link TrieSequencer} as this Trie.
   *
   * @return The reference to a new Trie.
   */
  public Trie<S, T> newEmptyClone() {
    Trie<S, T> t = new Trie<S, T>(sequencer, getDefaultValue());
    t.defaultMatch = defaultMatch;
    return t;
  }

  /**
   * Puts the value in the Trie with the given sequence.
   *
   * @param query The sequence.
   * @param value The value to place in the Trie.
   * @return The previous value in the Trie with the same sequence if one existed, otherwise null.
   */
  @Override
  public T put(S query, T value) {
    return this.put(query, (previousValue) -> value, null);
  }

  /**
   * Updates the value at the given sequence. If no value exists then null is passed to the
   * function. The result of the function replaces the previous value.
   *
   * @param query The sequence.
   * @param updater The function which takes the previous value (if any) and returns a new value.
   * @return The previous value in the Trie with the same sequence if one existed, otherwise null.
   */
  public T put(S query, Function<T, T> updater) {
    return this.put(query, updater, null);
  }

  /**
   * Updates the value at the given sequence. If no value exists then null is passed to the
   * function. The result of the function replaces the previous value.
   *
   * @param query The sequence.
   * @param updater The function which takes the previous value (if any) and returns a new value.
   * @param defaultPrevious The value to pass to the update function when adding to the Trie.
   * @return The previous value in the Trie with the same sequence if one existed, otherwise null.
   */
  public T put(S query, Function<T, T> updater, T defaultPrevious) {
    final int queryLength = sequencer.lengthOf(query);

    if (queryLength == 0) {
      return null;
    }

    int queryOffset = 0;
    TrieNode<S, T> node = root.children.get(sequencer.hashOf(query, 0));

    // The root doesn't have a child that starts with the given sequence...
    if (node == null) {
      // Add the sequence and value directly to root!
      return putReturnNull(root, updater.apply(defaultPrevious), query, queryOffset, queryLength);
    }

    while (node != null) {
      final S nodeSequence = node.sequence;
      final int nodeLength = node.end - node.start;
      final int max = Math.min(nodeLength, queryLength - queryOffset);
      final int matches = sequencer.matches(nodeSequence, node.start, query, queryOffset, max);

      queryOffset += matches;

      // mismatch in current node
      if (matches != max) {
        node.split(matches, null, sequencer);

        return putReturnNull(node, updater.apply(defaultPrevious), query, queryOffset, queryLength);
      }

      // partial match to the current node
      if (max < nodeLength) {
        node.split(max, updater.apply(defaultPrevious), sequencer);
        node.sequence = query;

        return null;
      }

      // Full match to query, replace value and sequence
      if (queryOffset == queryLength) {
        node.sequence = query;

        return node.update(updater);
      }

      // full match, end of the query or node
      if (node.children == null) {
        return putReturnNull(node, updater.apply(defaultPrevious), query, queryOffset, queryLength);
      }

      // full match, end of node
      TrieNode<S, T> next = node.children.get(sequencer.hashOf(query, queryOffset));

      if (next == null) {
        return putReturnNull(node, updater.apply(defaultPrevious), query, queryOffset, queryLength);
      }

      // full match, query or node remaining
      node = next;
    }

    return null;
  }

  /**
   * Adds a new TrieNode to the given node with the given sequence subset.
   *
   * @param node The node to add to; the parent of the created node.
   * @param value The value of the node.
   * @param query The sequence that was put.
   * @param queryOffset The offset into that sequence where the node (subset sequence) should begin.
   * @param queryLength The length of the subset sequence in elements.
   * @return null
   */
  private T putReturnNull(TrieNode<S, T> node, T value, S query, int queryOffset, int queryLength) {
    node.add(new TrieNode<S, T>(node, value, query, queryOffset, queryLength, null), sequencer);

    return null;
  }

  /**
   * Gets the value that matches the given sequence.
   *
   * @param sequence The sequence to match.
   * @param match The matching logic to use.
   * @return The value for the given sequence, or the default value of the Trie if no match was
   *         found. The default value of a Trie is by default null.
   */
  public T get(S sequence, TrieMatch match) {
    TrieNode<S, T> n = search(root, sequence, match);

    return (n != null ? n.value : getDefaultValue());
  }

  /**
   * Gets the value that matches the given sequence using the default TrieMatch.
   *
   * @param sequence The sequence to match.
   * @return The value for the given sequence, or the default value of the Trie if no match was
   *         found. The default value of a Trie is by default null.
   * @see #get(Object, TrieMatch)
   */
  @Override
  public T get(Object sequence) {
    return get((S) sequence, defaultMatch);
  }

  /**
   * Determines whether a value exists for the given sequence.
   *
   * @param sequence The sequence to match.
   * @param match The matching logic to use.
   * @return True if a value exists for the given sequence, otherwise false.
   */
  public boolean has(S sequence, TrieMatch match) {
    return hasAfter(root, sequence, match);
  }

  /**
   * Determines whether a value exists for the given sequence using the default TrieMatch.
   *
   * @param sequence The sequence to match.
   * @return True if a value exists for the given sequence, otherwise false.
   * @see #has(Object, TrieMatch)
   */
  public boolean has(S sequence) {
    return hasAfter(root, sequence, defaultMatch);
  }

  /**
   * Starts at the root node and searches for a node with the given sequence based on the given
   * matching logic.
   *
   * @param root The node to start searching from.
   * @param sequence The sequence to search for.
   * @param match The matching logic to use while searching.
   * @return True if root or a child of root has a match on the sequence, otherwise false.
   */
  protected boolean hasAfter(TrieNode<S, T> root, S sequence, TrieMatch match) {
    return search(root, sequence, match) != null;
  }

  /**
   * Removes the sequence from the Trie and returns it's value. The sequence must be an exact match,
   * otherwise nothing will be removed.
   *
   * @param sequence The sequence to remove.
   * @return The value of the removed sequence, or null if no sequence was removed.
   */
  @Override
  public T remove(Object sequence) {
    return removeAfter(root, (S) sequence);
  }

  /**
   * Starts at the root node and searches for a node with the exact given sequence, once found it
   * removes it and returns the value. If a node is not found with the exact sequence then null is
   * returned.
   *
   * @param root The root to start searching from.
   * @param sequence The exact sequence to search for.
   * @return The value of the removed node or null if it wasn't found.
   */
  protected T removeAfter(TrieNode<S, T> root, S sequence) {
    TrieNode<S, T> n = search(root, sequence, TrieMatch.EXACT);

    if (n == null) {
      return null;
    }

    T value = n.value;

    n.remove(sequencer);

    return value;
  }

  /**
   * Returns the number of sequences-value pairs in this Trie.
   *
   * @return The number of sequences-value pairs in this Trie.
   */
  @Override
  public int size() {
    return root.getSize();
  }

  /**
   * Determines whether this Trie is empty.
   *
   * @return 0 if the Trie doesn't have any sequences-value pairs, otherwise false.
   */
  @Override
  public boolean isEmpty() {
    return (root.getSize() == 0);
  }

  /**
   * Returns the default TrieMatch used for {@link #has(Object)} and {@link #get(Object)}.
   *
   * @return The default TrieMatch set on this Trie.
   */
  public TrieMatch getDefaultMatch() {
    return defaultMatch;
  }

  /**
   * Sets the default TrieMatch used for {@link #has(Object)} and {@link #get(Object)}.
   *
   * @param match The new default TrieMatch to set on this Trie.
   */
  public void setDefaultMatch(TrieMatch match) {
    defaultMatch = match;
  }

  @Override
  public boolean containsKey(Object key) {
    return has((S) key);
  }

  @Override
  public boolean containsValue(Object value) {
    Iterable<T> values = new ValueIterator(root);

    for (T v : values) {
      if (v == value || (v != null && value != null && v.equals(values))) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Set<Entry<S, T>> entrySet() {
    return entries;
  }

  /**
   * Returns a {@link Set} of {@link Entry}s that match the given sequence based on the default
   * matching logic. If no matches were found then a Set with size 0 will be returned. The set
   * returned can have Entries removed directly from it, given that the Entries are from this Trie.
   *
   * @param sequence The sequence to match on.
   * @return The reference to a Set of Entries that matched.
   */
  public Set<Entry<S, T>> entrySet(S sequence) {
    return entrySet(sequence, defaultMatch);
  }

  /**
   * Returns a {@link Set} of {@link Entry}s that match the given sequence based on the given
   * matching logic. If no matches were found then a Set with size 0 will be returned. The set
   * returned can have Entries removed directly from it, given that the Entries are from this Trie.
   *
   * @param sequence The sequence to match on.
   * @param match The matching logic to use.
   * @return The reference to a Set of Entries that matched.
   */
  public Set<Entry<S, T>> entrySet(S sequence, TrieMatch match) {
    TrieNode<S, T> node = search(root, sequence, match);

    return (node == null ? (Set<Entry<S, T>>) EMPTY_CONTAINER : new EntrySet(node));
  }

  /**
   * The same as {@link #entrySet()} except instead of a {@link Set} of {@link Entry}s, it's a
   * {@link Set} of {@link TrieNode}s.
   *
   * @return The reference to the Set of all valued nodes in this Trie.
   * @see #entrySet()
   */
  public Set<TrieNode<S, T>> nodeSet() {
    return nodes;
  }

  /**
   * Returns a {@link Set} of {@link TrieNode}s that match the given sequence based on the default
   * matching logic. If no matches were found then a Set with size 0 will be returned. The set
   * returned can have TrieNodes removed directly from it, given that the TrieNodes are from this
   * Trie and they will be removed from this Trie.
   *
   * @param sequence The sequence to match on.
   * @return The reference to a Set of TrieNodes that matched.
   * @see #entrySet(Object)
   */
  public Set<TrieNode<S, T>> nodeSet(S sequence) {
    return nodeSet(sequence, defaultMatch);
  }

  /**
   * Returns a {@link Set} of {@link TrieNode}s that match the given sequence based on the given
   * matching logic. If no matches were found then a Set with size 0 will be returned. The set
   * returned can have TrieNodes removed directly from it, given that the TrieNodes are from this
   * Trie.
   *
   * @param sequence The sequence to match on.
   * @param match The matching logic to use.
   * @return The reference to a Set of TrieNodes that matched.
   * @see #entrySet(Object, TrieMatch)
   */
  public Set<TrieNode<S, T>> nodeSet(S sequence, TrieMatch match) {
    TrieNode<S, T> node = search(root, sequence, match);

    return (node == null ? (Set<TrieNode<S, T>>) EMPTY_CONTAINER : new NodeSet(node));
  }

  /**
   * Returns an {@link Iterable} of all {@link TrieNode}s in this Trie including naked (null-value)
   * nodes.
   *
   * @return The reference to a new Iterable.
   */
  public Iterable<TrieNode<S, T>> nodeSetAll() {
    return new NodeAllIterator(root);
  }

  /**
   * Returns an {@link Iterable} of all {@link TrieNode}s in this Trie that match the given sequence
   * using the default matching logic including naked (null-value) nodes.
   *
   * @param sequence The sequence to match on.
   * @return The reference to a new Iterable.
   */
  public Iterable<TrieNode<S, T>> nodeSetAll(S sequence) {
    return nodeSetAll(sequence, defaultMatch);
  }

  /**
   * Returns an {@link Iterable} of all {@link TrieNode}s in this Trie that match the given sequence
   * using the given matching logic including naked (null-value) nodes.
   *
   * @param sequence The sequence to match on.
   * @param match The matching logic to use.
   * @return The reference to a new Iterable.
   */
  public Iterable<TrieNode<S, T>> nodeSetAll(S sequence, TrieMatch match) {
    TrieNode<S, T> node = search(root, sequence, match);

    return (node == null ? (Iterable<TrieNode<S, T>>) EMPTY_CONTAINER : new NodeAllIterator(root));
  }

  @Override
  public Set<S> keySet() {
    return sequences;
  }

  /**
   * Returns a {@link Set} of all keys (sequences) in this Trie that match the given sequence given
   * the default matching logic. If no matches were found then a Set with size 0 will be returned.
   * The Set returned can have keys/sequences removed directly from it and they will be removed from
   * this Trie.
   *
   * @param sequence The sequence to match on.
   * @return The reference to a Set of keys/sequences that matched.
   */
  public Set<S> keySet(S sequence) {
    return keySet(sequence, defaultMatch);
  }

  /**
   * Returns a {@link Set} of all keys (sequences) in this Trie that match the given sequence with
   * the given matching logic. If no matches were found then a Set with size 0 will be returned. The
   * Set returned can have keys/sequences removed directly from it and they will be removed from
   * this Trie.
   *
   * @param sequence The sequence to match on.
   * @param match The matching logic to use.
   * @return The reference to a Set of keys/sequences that matched.
   */
  public Set<S> keySet(S sequence, TrieMatch match) {
    TrieNode<S, T> node = search(root, sequence, match);

    return (node == null ? (Set<S>) EMPTY_CONTAINER : new SequenceSet(node));
  }

  @Override
  public Collection<T> values() {
    return values;
  }

  /**
   * Returns a {@link Collection} of the values that match the given sequence given the default
   * matching logic. If no matches were found then a Collection with size 0 will be returned. The
   * Collection returned can have values removed directly from it and they will be removed from this
   * Trie.
   *
   * @param sequence The sequence to match on.
   * @return The reference to a Collection of values that matched.
   */
  public Collection<T> values(S sequence) {
    return values(sequence, defaultMatch);
  }

  /**
   * Returns a {@link Collection} of the values that match the given sequence and the given matching
   * logic. If no matches were found then a Collection with size 0 will be returned. The Collection
   * returned can have values removed directly from it and they will be removed from this Trie.
   *
   * @param sequence The sequence to match on.
   * @param match The matching logic to use.
   * @return The reference to a Collection of values that matched.
   */
  public Collection<T> values(S sequence, TrieMatch match) {
    TrieNode<S, T> node = search(root, sequence, match);

    return (node == null ? null : new ValueCollection(node));
  }

  @Override
  public void putAll(Map<? extends S, ? extends T> map) {
    for (Entry<? extends S, ? extends T> e : map.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  @Override
  public void clear() {
    root.children.clear();
    root.size = 0;
  }

  /**
   * Searches in the Trie based on the sequence query and the matching logic.
   *
   * @param root The node to start searching from.
   * @param query The query sequence.
   * @param match The matching logic.
   * @return The node that best matched the query based on the logic.
   */
  protected TrieNode<S, T> search(TrieNode<S, T> root, S query, TrieMatch match) {
    final int queryLength = sequencer.lengthOf(query);

    // If the query is empty or matching logic is not given, return null.
    if (queryLength == 0 || match == null || queryLength < root.end) {
      return null;
    }

    // If a non-root root was passed in, it might be the node you are looking for.
    if (root.sequence != null) {
      int matches = sequencer.matches(root.sequence, 0, query, 0, root.end);

      if (matches == queryLength) {
        return root;
      }
      if (matches < root.end) {
        return null;
      }
    }

    int queryOffset = root.end;
    TrieNode<S, T> node = root.children.get(sequencer.hashOf(query, queryOffset));

    while (node != null) {
      final int nodeLength = node.end - node.start;
      final int max = Math.min(nodeLength, queryLength - queryOffset);
      final int matches = sequencer.matches(node.sequence, node.start, query, queryOffset, max);

      queryOffset += matches;

      // Not found
      if (matches != max) {
        return null;
      }

      // Potentially PARTIAL match
      if (max != nodeLength) {
        return (match != TrieMatch.PARTIAL ? null : node);
      }

      // Either EXACT or STARTS_WITH match
      if (queryOffset == queryLength || node.children == null) {
        break;
      }

      TrieNode<S, T> next = node.children.get(sequencer.hashOf(query, queryOffset));

      // If there is no next, node could be a STARTS_WITH match
      if (next == null) {
        break;
      }

      node = next;
    }

    // EXACT matches
    if (node != null && match == TrieMatch.EXACT) {
      // Check length of last node against query
      if (node.value == null || node.end != queryLength) {
        return null;
      }

      // Check actual sequence values
      if (sequencer.matches(node.sequence, 0, query, 0, node.end) != node.end) {
        return null;
      }
    }

    return node;
  }

  private class ValueCollection extends AbstractCollection<T> {

    private final TrieNode<S, T> root;

    public ValueCollection(TrieNode<S, T> root) {
      this.root = root;
    }

    @Override
    public Iterator<T> iterator() {
      return new ValueIterator(root);
    }

    @Override
    public int size() {
      return root.getSize();
    }
  }

  private class SequenceSet extends AbstractSet<S> {

    private final TrieNode<S, T> root;

    public SequenceSet(TrieNode<S, T> root) {
      this.root = root;
    }

    @Override
    public Iterator<S> iterator() {
      return new SequenceIterator(root);
    }

    @Override
    public boolean remove(Object sequence) {
      return removeAfter(root, (S) sequence) != null;
    }

    @Override
    public boolean contains(Object sequence) {
      return hasAfter(root, (S) sequence, TrieMatch.EXACT);
    }

    @Override
    public int size() {
      return root.getSize();
    }
  }

  private class EntrySet extends AbstractSet<Entry<S, T>> {

    private final TrieNode<S, T> root;

    public EntrySet(TrieNode<S, T> root) {
      this.root = root;
    }

    @Override
    public Iterator<Entry<S, T>> iterator() {
      return new EntryIterator(root);
    }

    @Override
    public boolean remove(Object entry) {
      TrieNode<S, T> node = (TrieNode<S, T>) entry;
      boolean removable = (node.getRoot() == Trie.this.root);

      if (removable) {
        node.remove(sequencer);
      }

      return removable;
    }

    @Override
    public boolean contains(Object entry) {
      TrieNode<S, T> node = (TrieNode<S, T>) entry;

      return (node.getRoot() == Trie.this.root);
    }

    @Override
    public int size() {
      return root.getSize();
    }
  }

  private class NodeSet extends AbstractSet<TrieNode<S, T>> {

    private final TrieNode<S, T> root;

    public NodeSet(TrieNode<S, T> root) {
      this.root = root;
    }

    @Override
    public Iterator<TrieNode<S, T>> iterator() {
      return new NodeIterator(root);
    }

    @Override
    public boolean remove(Object entry) {
      TrieNode<S, T> node = (TrieNode<S, T>) entry;
      boolean removable = (node.getRoot() == Trie.this.root);

      if (removable) {
        node.remove(sequencer);
      }

      return removable;
    }

    @Override
    public boolean contains(Object entry) {
      TrieNode<S, T> node = (TrieNode<S, T>) entry;

      return (node.getRoot() == Trie.this.root);
    }

    @Override
    public int size() {
      return root.getSize();
    }
  }

  private class SequenceIterator extends AbstractIterator<S> {

    public SequenceIterator(TrieNode<S, T> root) {
      super(root);
    }

    @Override
    public S next() {
      return nextNode().sequence;
    }
  }

  private class ValueIterator extends AbstractIterator<T> {

    public ValueIterator(TrieNode<S, T> root) {
      super(root);
    }

    @Override
    public T next() {
      return nextNode().value;
    }
  }

  private class EntryIterator extends AbstractIterator<Entry<S, T>> {

    public EntryIterator(TrieNode<S, T> root) {
      super(root);
    }

    @Override
    public Entry<S, T> next() {
      return nextNode();
    }
  }

  private class NodeIterator extends AbstractIterator<TrieNode<S, T>> {

    public NodeIterator(TrieNode<S, T> root) {
      super(root);
    }

    @Override
    public TrieNode<S, T> next() {
      return nextNode();
    }
  }

  private class NodeAllIterator extends AbstractIterator<TrieNode<S, T>> {

    public NodeAllIterator(TrieNode<S, T> root) {
      super(root);
    }

    @Override
    public TrieNode<S, T> next() {
      return nextNode();
    }

    @Override
    protected boolean isAnyNode() {
      return true;
    }
  }

  private abstract class AbstractIterator<K> implements Iterable<K>, Iterator<K> {

    private final TrieNode<S, T> root;
    private TrieNode<S, T> previous;
    private TrieNode<S, T> current;
    private int depth;
    private int[] indices = new int[32];

    public AbstractIterator(TrieNode<S, T> root) {
      this.root = root;
      this.reset();
    }

    public AbstractIterator<K> reset() {
      depth = 0;
      indices[0] = -1;

      if (root.value == null) {
        previous = root;
        current = findNext();
      } else {
        previous = null;
        current = root;
      }

      return this;
    }

    protected boolean isAnyNode() {
      return false;
    }

    @Override
    public boolean hasNext() {
      return (current != null);
    }

    public TrieNode<S, T> nextNode() {
      previous = current;
      current = findNext();
      return previous;
    }

    @Override
    public void remove() {
      previous.remove(sequencer);
    }

    private TrieNode<S, T> findNext() {
      if (indices[0] == root.children.capacity()) {
        return null;
      }

      TrieNode<S, T> node = previous;
      boolean foundValue = false;

      if (node.children == null) {
        node = node.parent;
      }

      while (!foundValue) {
        final PerfectHashMap<TrieNode<S, T>> children = node.children;
        final int childCapacity = children.capacity();
        int id = indices[depth] + 1;

        while (id < childCapacity && children.valueAt(id) == null) {
          id++;
        }

        if (id == childCapacity) {
          node = node.parent;
          depth--;

          if (depth == -1) {
            node = null;
            foundValue = true;
          }
        } else {
          indices[depth] = id;
          node = children.valueAt(id);

          if (node.hasChildren()) {
            indices[++depth] = -1;
          }

          if (node.value != null || isAnyNode()) {
            foundValue = true;
          }
        }
      }

      return node;
    }

    @Override
    public Iterator<K> iterator() {
      return this;
    }
  }

  private static class EmptyContainer<T> extends AbstractCollection<T>
      implements Set<T>, Iterator<T> {

    @Override
    public Iterator<T> iterator() {
      return this;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public T next() {
      return null;
    }

    @Override
    public void remove() {}
  }
}
