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
// Slim replacement for it.unimi.dsi.fastutil.longs.LongArrayList, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/, drv/ArrayList.drv). Only the subset used by Symja is
// implemented; growth policy and overload set mirror fastutil.
package org.matheclipse.external.fastutil.longs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;
import java.util.function.LongConsumer;

/** A type-specific array-based list of primitive longs. */
public class LongArrayList extends AbstractList<Long>
    implements RandomAccess, Cloneable, Serializable {

  private static final long serialVersionUID = -7046029254386353137L;

  /** The initial default capacity of a list. */
  public static final int DEFAULT_INITIAL_CAPACITY = 10;

  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private static final long[] DEFAULT_EMPTY_ARRAY = {};

  /** The backing array. */
  protected transient long a[];

  /** The current actual size of the list. */
  protected int size;

  public LongArrayList() {
    a = DEFAULT_EMPTY_ARRAY;
  }

  public LongArrayList(final int capacity) {
    if (capacity < 0) {
      throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
    }
    a = capacity == 0 ? DEFAULT_EMPTY_ARRAY : new long[capacity];
  }

  public LongArrayList(final long[] a) {
    this(a.length);
    System.arraycopy(a, 0, this.a, 0, a.length);
    size = a.length;
  }

  public LongArrayList(final Collection<? extends Long> c) {
    this(c.size());
    for (final Long e : c) {
      a[size++] = e.longValue();
    }
  }

  /** Returns a new list containing the given elements. */
  public static LongArrayList of(final long... init) {
    return wrap(init.clone());
  }

  /** Wraps the given array into a list. */
  public static LongArrayList wrap(final long[] a) {
    return wrap(a, a.length);
  }

  /** Wraps the given array into a list of the given size. */
  public static LongArrayList wrap(final long[] a, final int length) {
    if (length > a.length) {
      throw new IllegalArgumentException(
          "The specified length (" + length + ") is greater than the array size (" + a.length + ")");
    }
    final LongArrayList l = new LongArrayList();
    l.a = a;
    l.size = length;
    return l;
  }

  /** Returns the backing array of this list. */
  public long[] elements() {
    return a;
  }

  public void ensureCapacity(final int capacity) {
    if (capacity <= a.length || a == DEFAULT_EMPTY_ARRAY && capacity <= DEFAULT_INITIAL_CAPACITY) {
      return;
    }
    a = forceCapacity(a, capacity, size);
  }

  private void grow(int capacity) {
    if (capacity <= a.length) {
      return;
    }
    if (a != DEFAULT_EMPTY_ARRAY) {
      capacity = (int) Math.max(Math.min((long) a.length + (a.length >> 1), MAX_ARRAY_SIZE),
          capacity);
    } else if (capacity < DEFAULT_INITIAL_CAPACITY) {
      capacity = DEFAULT_INITIAL_CAPACITY;
    }
    a = forceCapacity(a, capacity, size);
  }

  private static long[] forceCapacity(final long[] array, final int length, final int preserve) {
    final long[] t = new long[length];
    System.arraycopy(array, 0, t, 0, preserve);
    return t;
  }

  /** Appends the given element; always returns true. */
  public boolean add(final long k) {
    grow(size + 1);
    a[size++] = k;
    return true;
  }

  /** Inserts the given element at the given position. */
  public void add(final int index, final long k) {
    ensureIndex(index);
    grow(size + 1);
    if (index != size) {
      System.arraycopy(a, index, a, index + 1, size - index);
    }
    a[index] = k;
    size++;
  }

  @Override
  public boolean add(final Long k) {
    return add(k.longValue());
  }

  @Override
  public void add(final int index, final Long k) {
    add(index, k.longValue());
  }

  /** Returns the element at the given position as a primitive long. */
  public long getLong(final int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    return a[index];
  }

  @Override
  public Long get(final int index) {
    return Long.valueOf(getLong(index));
  }

  /** Replaces the element at the given position; returns the old element. */
  public long set(final int index, final long k) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    final long old = a[index];
    a[index] = k;
    return old;
  }

  @Override
  public Long set(final int index, final Long k) {
    return Long.valueOf(set(index, k.longValue()));
  }

  /** Removes the element at the given position; returns the removed element. */
  public long removeLong(final int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    final long old = a[index];
    size--;
    if (index != size) {
      System.arraycopy(a, index + 1, a, index, size - index);
    }
    return old;
  }

  @Override
  public Long remove(final int index) {
    return Long.valueOf(removeLong(index));
  }

  /** Removes the first occurrence of the given value; returns true if the list changed. */
  public boolean rem(final long k) {
    final int index = indexOf(k);
    if (index == -1) {
      return false;
    }
    removeLong(index);
    return true;
  }

  @Override
  public boolean remove(final Object o) {
    return rem(((Long) o).longValue());
  }

  /** Returns the index of the first occurrence of the given value, or -1. */
  public int indexOf(final long k) {
    for (int i = 0; i < size; i++) {
      if (k == a[i]) {
        return i;
      }
    }
    return -1;
  }

  /** Returns the index of the last occurrence of the given value, or -1. */
  public int lastIndexOf(final long k) {
    for (int i = size; i-- != 0;) {
      if (k == a[i]) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int indexOf(final Object o) {
    return indexOf(((Long) o).longValue());
  }

  @Override
  public int lastIndexOf(final Object o) {
    return lastIndexOf(((Long) o).longValue());
  }

  /** Returns true if the list contains the given value. */
  public boolean contains(final long k) {
    return indexOf(k) >= 0;
  }

  @Override
  public boolean contains(final Object o) {
    return o instanceof Long && contains(((Long) o).longValue());
  }

  @Override
  public int size() {
    return size;
  }

  /** Sets the size of this list, growing with zeroes or truncating as needed. */
  public void size(final int size) {
    if (size > a.length) {
      a = forceCapacity(a, size, this.size);
    }
    if (size > this.size) {
      Arrays.fill(a, this.size, size, 0L);
    }
    this.size = size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void clear() {
    size = 0;
  }

  /** Returns the elements of this list as a freshly allocated array. */
  public long[] toLongArray() {
    if (size == 0) {
      return new long[0];
    }
    return Arrays.copyOf(a, size);
  }

  /** Copies {@code length} elements of {@code a} starting at {@code offset} into this list. */
  public void addElements(final int index, final long a[], final int offset, final int length) {
    ensureIndex(index);
    grow(size + length);
    System.arraycopy(this.a, index, this.a, index + length, size - index);
    System.arraycopy(a, offset, this.a, index, length);
    size += length;
  }

  /** Copies elements of this list into the given array. */
  public void getElements(final int from, final long[] a, final int offset, final int length) {
    System.arraycopy(this.a, from, a, offset, length);
  }

  @Override
  public boolean addAll(final Collection<? extends Long> c) {
    if (c instanceof LongArrayList) {
      final LongArrayList l = (LongArrayList) c;
      final int n = l.size();
      if (n == 0) {
        return false;
      }
      grow(size + n);
      System.arraycopy(l.a, 0, a, size, n);
      size += n;
      return true;
    }
    boolean modified = false;
    for (final Long e : c) {
      add(e.longValue());
      modified = true;
    }
    return modified;
  }

  /** Sorts this list in ascending order. */
  public void sort() {
    Arrays.sort(a, 0, size);
  }

  public void forEach(final LongConsumer action) {
    for (int i = 0; i < size; i++) {
      action.accept(a[i]);
    }
  }

  /** Trims the backing array to the current size. */
  public void trim() {
    if (size == a.length) {
      return;
    }
    final long[] t = new long[size];
    System.arraycopy(a, 0, t, 0, size);
    a = t;
  }

  @Override
  public LongArrayList clone() {
    final LongArrayList c = new LongArrayList(size);
    System.arraycopy(a, 0, c.a, 0, size);
    c.size = size;
    return c;
  }

  protected void ensureIndex(final int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
    }
    if (index > size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than list size (" + size + ")");
    }
  }

  private void writeObject(final ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    for (int i = 0; i < size; i++) {
      s.writeLong(a[i]);
    }
  }

  private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    a = new long[size];
    for (int i = 0; i < size; i++) {
      a[i] = s.readLong();
    }
  }
}
