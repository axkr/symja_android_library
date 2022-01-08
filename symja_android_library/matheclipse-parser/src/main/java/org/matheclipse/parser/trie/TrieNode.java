/*
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Open Software License (OSL 3.0) that is
 * bundled with this package in the file LICENSE.txt. It is also available
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it
 * through the world-wide-web, please send an email to magnos.software@gmail.com
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via our website or email, your feedback is much appreciated.
 *
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 *              Open Software License (OSL 3.0)
 */

package org.matheclipse.parser.trie;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * A TrieNode is an {@link java.util.Map.Entry Entry} in a Trie that stores the sequence (key),
 * value, the starting and ending indices into the sequence, the number of children in this node,
 * and the parent to this node.
 *
 * <p>There are three types of TrieNodes and each have special properties.
 *
 * <ol>
 *   <li>Root
 *       <ul>
 *         <li>{@link #getStart()} == {@link #getEnd()} == 0
 *         <li>{@link #getValue()} == null
 *         <li>{@link #getKey()} == {@link #getSequence()} == null
 *       </ul>
 *   <li>Naked Branch
 *       <ul>
 *         <li>{@link #getStart()} &lt; {@link #getEnd()}
 *         <li>{@link #getValue()} == null
 *         <li>{@link #getKey()} == {@link #getSequence()} == (a key of one of it's children or a
 *             past child, ignore)
 *       </ul>
 *   <li>Valued (Branch or Leaf)
 *       <ul>
 *         <li>{@link #getStart()} &lt; {@link #getEnd()}
 *         <li>{@link #getValue()} == non-null value passed into {@link Trie#put(Object, Object)}
 *         <li>{@link #getKey()} == {@link #getSequence()} == a non-null key passed into {@link
 *             Trie#put(Object, Object)}
 *       </ul>
 * </ol>
 *
 * <p>You can tell a valued branch or leaf apart by {@link #getChildCount()}, if it returns 0 then
 * it's a leaf, otherwise it's a branch.
 *
 * @author Philip Diffenderfer
 */
public class TrieNode<S, T> implements Entry<S, T>, Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  /** The parent of this node. */
  protected TrieNode<S, T> parent;

  /** The value of this node. */
  protected T value;

  /**
   * The sequence of this node. The subsequence between start and end are the important part of this
   * sequence.
   */
  protected S sequence;

  /** The start index of this node into it's sequence. */
  protected int start;

  /** The end index of this node into it's sequence. */
  protected int end;

  /** The array of child nodes. */
  protected PerfectHashMap<TrieNode<S, T>> children = null;

  /** The number of values beyond this node. */
  protected int size;

  /** Instantiates a new TrieNode for deserialization. */
  protected TrieNode() {}

  /**
   * Instantiates a new TrieNode.
   *
   * @param parent The parent to this node.
   * @param value The value of this node.
   * @param sequence The sequence of this node.
   * @param start The start of the sequence for this node, typically the end of the parent.
   * @param end The end of the sequence for this node.
   * @param children The intial set of children.
   */
  protected TrieNode(
      TrieNode<S, T> parent,
      T value,
      S sequence,
      int start,
      int end,
      PerfectHashMap<TrieNode<S, T>> children) {
    this.parent = parent;
    this.sequence = sequence;
    this.start = start;
    this.end = end;
    this.children = children;
    this.size = calculateSize(children);
    this.setValue(value);
  }

  /**
   * Splits this node at the given relative index and returns the TrieNode with the sequence
   * starting at index. The returned TrieNode has this node's sequence, value, and children. The
   * returned TrieNode is also the only child of this node when this method returns.
   *
   * @param index The relative index (starting at 0 and going to end - start - 1) in the sequence.
   * @param newValue The new value of this node.
   * @param sequencer The sequencer used to add the returned node to this node.
   * @return The reference to the child node created that's sequence starts at index.
   */
  protected TrieNode<S, T> split(int index, T newValue, TrieSequencer<S> sequencer) {
    TrieNode<S, T> c = new TrieNode<S, T>(this, value, sequence, index + start, end, children);
    c.registerAsParent();

    setValue(null);
    setValue(newValue);
    end = index + start;
    children = null;

    add(c, sequencer);

    return c;
  }

  /**
   * Adds the given child to this TrieNode. The child TrieNode is expected to have had this node's
   * reference passed to it's constructor as the parent parameter. This needs to be done to keep the
   * size calculations accurate.
   *
   * @param child The TrieNode to add as a child.
   * @param sequencer The sequencer to use to determine the place of the node in the children
   *     PerfectHashMap.
   */
  protected void add(TrieNode<S, T> child, TrieSequencer<S> sequencer) {
    int hash = sequencer.hashOf(child.sequence, end);

    if (children == null) {
      children = new PerfectHashMap<TrieNode<S, T>>(hash, child);
    } else {
      children.put(hash, child);
    }
  }

  /**
   * Removes this node from the Trie and appropriately adjusts it's parent and children.
   *
   * @param sequencer The sequencer to use to determine the place of this node in this nodes sibling
   *     PerfectHashMap.
   */
  protected void remove(TrieSequencer<S> sequencer) {
    // Decrement size if this node had a value
    setValue(null);

    int childCount = (children == null ? 0 : children.size());

    // When there are no children, remove this node from it's parent.
    if (childCount == 0) {
      parent.children.remove(sequencer.hashOf(sequence, start));
    }
    // With one child, become the child!
    else if (childCount == 1) {
      TrieNode<S, T> child = children.valueAt(0);

      children = child.children;
      value = child.value;
      sequence = child.sequence;
      end = child.end;

      child.children = null;
      child.parent = null;
      child.sequence = null;
      child.value = null;

      registerAsParent();
    }
  }

  /**
   * Adds the given size to this TrieNode and it's parents.
   *
   * @param amount The amount of size to add.
   */
  private void addSize(int amount) {
    TrieNode<S, T> curr = this;

    while (curr != null) {
      curr.size += amount;
      curr = curr.parent;
    }
  }

  /**
   * Sums the sizes of all non-null TrieNodes in the given map.
   *
   * @param nodes The map to calculate the total size of.
   * @return The total size of the given map.
   */
  private int calculateSize(PerfectHashMap<TrieNode<S, T>> nodes) {
    int size = 0;

    if (nodes != null) {
      for (int i = nodes.capacity() - 1; i >= 0; i--) {
        TrieNode<S, T> n = nodes.valueAt(i);

        if (n != null) {
          size += n.size;
        }
      }
    }

    return size;
  }

  /** Ensures all child TrieNodes to this node are pointing to the correct parent (this). */
  private void registerAsParent() {
    if (children != null) {
      for (int i = 0; i < children.capacity(); i++) {
        TrieNode<S, T> c = children.valueAt(i);

        if (c != null) {
          c.parent = this;
        }
      }
    }
  }

  /**
   * Returns whether this TrieNode has children.
   *
   * @return True if children exist, otherwise false.
   */
  public boolean hasChildren() {
    return children != null && children.size() > 0;
  }

  /**
   * Returns the parent of this TrieNode. If this TrieNode doesn't have a parent it signals that
   * this TrieNode is the root of a Trie and null will be returned.
   *
   * @return The reference to the parent of this node, or null if this is a root node.
   */
  public TrieNode<S, T> getParent() {
    return parent;
  }

  /**
   * The value of this TrieNode.
   *
   * @return The value of this TrieNode or null if this TrieNode is a branching node only (has
   *     children but the sequence in this node was never directly added).
   */
  @Override
  public T getValue() {
    return value;
  }

  /**
   * The complete sequence of this TrieNode. The actual sequence is a sub-sequence that starts at
   * {@link #getStart()} (inclusive) and ends at {@link #getEnd()} (exclusive).
   *
   * @return The complete sequence of this TrieNode.
   */
  public S getSequence() {
    return sequence;
  }

  /**
   * The start of the sequence in this TrieNode.
   *
   * @return The start of the sequence in this TrieNode, greater than or equal to 0 and less than
   *     {@link #getEnd()}. In the case of the root node: {@link #getStart()} == {@link #getEnd()}.
   */
  public int getStart() {
    return start;
  }

  /**
   * The end of the sequence in this TrieNode.
   *
   * @return The end of the sequence in this TrieNode, greater than {@link #getStart()}. In the case
   *     of the root node: {@link #getStart()} == {@link #getEnd()}.
   */
  public int getEnd() {
    return end;
  }

  /**
   * Returns the number of non-null values that exist in ALL child nodes (including this node's
   * value).
   *
   * @return The number of non-null values and valid sequences.
   */
  public int getSize() {
    return size;
  }

  /**
   * Returns the number of direct children.
   *
   * @return The number of direct children in this node.
   */
  public int getChildCount() {
    return (children == null ? 0 : children.size());
  }

  /**
   * Calculates the root node by traversing through all parents until it found it.
   *
   * @return The root of the {@link Trie} this TrieNode.
   */
  public TrieNode<S, T> getRoot() {
    TrieNode<S, T> n = parent;

    while (n.parent != null) {
      n = n.parent;
    }

    return n;
  }

  /** @return True if this node is a root, otherwise false. */
  public boolean isRoot() {
    return (parent == null);
  }

  /** @return True if this node is a root or a naked (branch only) node, otherwise false. */
  public boolean isNaked() {
    return (value == null);
  }

  /** @return True if this node has a non-null value (is not a root or naked node). */
  public boolean hasValue() {
    return (value != null);
  }

  @Override
  public S getKey() {
    return sequence;
  }

  @Override
  public T setValue(T newValue) {
    return update((previousValue) -> newValue);
  }

  /**
   * Updates the value in this node with an update function which is given the previous value and
   * expects the new value to be returned.
   *
   * @param updater The function which returns the new value.
   * @return The previous value of this node.
   */
  public T update(Function<T, T> updater) {
    T previousValue = value;

    value = updater.apply(previousValue);

    if (previousValue == null && value != null) {
      addSize(1);
    } else if (previousValue != null && value == null) {
      addSize(-1);
    }

    return previousValue;
  }

  @Override
  public int hashCode() {
    return (sequence == null ? 0 : sequence.hashCode()) ^ (value == null ? 0 : value.hashCode());
  }

  @Override
  public String toString() {
    return sequence + "=" + value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof TrieNode)) {
      return false;
    }

    TrieNode<?, ?> node = (TrieNode<?, ?>) o;

    return (sequence == node.sequence || sequence.equals(node.sequence))
        && (value == node.value
            || (value != null && node.value != null && value.equals(node.value)));
  }
}
