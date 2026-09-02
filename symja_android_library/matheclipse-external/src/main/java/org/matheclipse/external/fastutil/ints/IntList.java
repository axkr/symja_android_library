/*
 * Copyright (C) 2002-2026 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Slim replacement for it.unimi.dsi.fastutil.ints.IntList, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/). Only the subset used by Symja is implemented; the
// overload set of the type-specific methods mirrors fastutil exactly, so that call sites
// bind to the same methods as before.
package org.matheclipse.external.fastutil.ints;

import java.util.Collection;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * A type-specific {@link List} of primitive ints.
 *
 * <p>
 * Note that, exactly as in fastutil, {@link #remove(int)} removes the element <em>at the given
 * index</em> (inherited from {@link List}), whereas {@link #rem(int)} removes the first occurrence
 * of the given <em>value</em>.
 */
public interface IntList extends List<Integer>, Comparable<List<? extends Integer>> {

  /** Returns the element at the given position as a primitive int. */
  int getInt(int index);

  /** Replaces the element at the given position; returns the old element. */
  int set(int index, int k);

  /** Appends the given element; always returns true. */
  boolean add(int key);

  /** Inserts the given element at the given position. */
  void add(int index, int key);

  /** Removes the element at the given position; returns the removed element. */
  int removeInt(int index);

  /** Removes the first occurrence of the given value; returns true if the list changed. */
  boolean rem(int key);

  /** Returns true if the list contains the given value. */
  boolean contains(int key);

  /** Returns the index of the first occurrence of the given value, or -1. */
  int indexOf(int k);

  /** Returns the index of the last occurrence of the given value, or -1. */
  int lastIndexOf(int k);

  /** Returns the elements of this list as a freshly allocated array. */
  int[] toIntArray();

  /** Copies {@code length} elements of {@code a} starting at {@code offset} into this list. */
  void addElements(int index, int[] a, int offset, int length);

  /** Appends all elements of the given array. */
  void addElements(int index, int[] a);

  /** Copies elements of this list into the given array. */
  void getElements(int from, int[] a, int offset, int length);

  /** Sets the size of this list, growing with zeroes or truncating as needed. */
  void size(int size);

  /** Appends all elements of the given type-specific list. */
  boolean addAll(IntList c);

  /** Appends all elements of the given type-specific list at the given index. */
  boolean addAll(int index, IntList c);

  void forEach(IntConsumer action);

  @Override
  IntIterator iterator();

  @Override
  IntList subList(int from, int to);

  /** Returns an immutable list containing no elements. */
  static IntList of() {
    return IntArrayList.of();
  }

  /** Returns a list containing the given elements. */
  static IntList of(final int... a) {
    return IntArrayList.of(a);
  }

  /** Returns a list containing the elements of the given collection. */
  static IntList copyOf(final Collection<? extends Integer> c) {
    return new IntArrayList(c);
  }
}
