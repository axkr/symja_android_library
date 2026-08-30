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
// Slim replacement for it.unimi.dsi.fastutil.ints.IntArrayList, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/, drv/ArrayList.drv). Only the subset used by Symja is
// implemented; growth policy, overload set and serialized form mirror fastutil.
package org.matheclipse.external.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/** A type-specific array-based list of primitive ints. */
public class IntArrayList extends AbstractList<Integer>
    implements IntList, RandomAccess, Cloneable, Serializable {

  private static final long serialVersionUID = -7046029254386353130L;

  /** The initial default capacity of a list. */
  public static final int DEFAULT_INITIAL_CAPACITY = 10;

  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private static final int[] DEFAULT_EMPTY_ARRAY = {};

  /** The backing array. */
  protected transient int a[];

  /** The current actual size of the list. */
  protected int size;

  /** Creates a new empty array list with {@link #DEFAULT_INITIAL_CAPACITY} capacity. */
  public IntArrayList() {
    a = DEFAULT_EMPTY_ARRAY;
  }

  /**
   * Creates a new array list with given capacity.
   *
   * @param capacity the initial capacity of the array list (may be 0).
   */
  public IntArrayList(final int capacity) {
    if (capacity < 0) {
      throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
    }
    a = capacity == 0 ? DEFAULT_EMPTY_ARRAY : new int[capacity];
  }

  /**
   * Creates a new array list and fills it with the elements of the given array.
   *
   * @param a an array whose elements will be used to fill the array list.
   */
  public IntArrayList(final int[] a) {
    this(a, 0, a.length);
  }

  /** Creates a new array list and fills it with a range of the given array. */
  public IntArrayList(final int[] a, final int offset, final int length) {
    this(length);
    System.arraycopy(a, offset, this.a, 0, length);
    size = length;
  }

  /** Creates a new array list and fills it with the elements of the given collection. */
  public IntArrayList(final Collection<? extends Integer> c) {
    this(c.size());
    if (c instanceof IntList) {
      final IntList l = (IntList) c;
      for (int i = 0; i < l.size(); i++) {
        a[i] = l.getInt(i);
      }
      size = l.size();
    } else {
      for (final Integer e : c) {
        a[size++] = e.intValue();
      }
    }
  }

  /** Returns a new empty array list. */
  public static IntArrayList of() {
    return new IntArrayList();
  }

  /** Returns a new array list containing the given elements. */
  public static IntArrayList of(final int... init) {
    return wrap(init.clone());
  }

  /** Wraps the given array into an array list of given size. */
  public static IntArrayList wrap(final int[] a, final int length) {
    if (length > a.length) {
      throw new IllegalArgumentException(
          "The specified length (" + length + ") is greater than the array size (" + a.length + ")");
    }
    final IntArrayList l = new IntArrayList();
    l.a = a;
    l.size = length;
    return l;
  }

  /** Wraps the given array into an array list. */
  public static IntArrayList wrap(final int[] a) {
    return wrap(a, a.length);
  }

  /** Returns the backing array of this list. */
  public int[] elements() {
    return a;
  }

  /** Ensures that this list can contain the given number of entries without resizing. */
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

  private static int[] forceCapacity(final int[] array, final int length, final int preserve) {
    final int[] t = new int[length];
    System.arraycopy(array, 0, t, 0, preserve);
    return t;
  }

  /** Trims the backing array to the current size. */
  public void trim() {
    trim(0);
  }

  /** Trims the backing array to max(size, n). */
  public void trim(final int n) {
    if (n >= a.length || size == a.length) {
      return;
    }
    final int[] t = new int[Math.max(n, size)];
    System.arraycopy(a, 0, t, 0, size);
    a = t;
  }

  @Override
  public boolean add(final int k) {
    grow(size + 1);
    a[size++] = k;
    return true;
  }

  @Override
  public void add(final int index, final int k) {
    ensureIndex(index);
    grow(size + 1);
    if (index != size) {
      System.arraycopy(a, index, a, index + 1, size - index);
    }
    a[index] = k;
    size++;
  }

  @Override
  public boolean add(final Integer k) {
    return add(k.intValue());
  }

  @Override
  public void add(final int index, final Integer k) {
    add(index, k.intValue());
  }

  @Override
  public int getInt(final int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    return a[index];
  }

  @Override
  public Integer get(final int index) {
    return Integer.valueOf(getInt(index));
  }

  @Override
  public int set(final int index, final int k) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    final int old = a[index];
    a[index] = k;
    return old;
  }

  @Override
  public Integer set(final int index, final Integer k) {
    return Integer.valueOf(set(index, k.intValue()));
  }

  @Override
  public int removeInt(final int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size + ")");
    }
    final int old = a[index];
    size--;
    if (index != size) {
      System.arraycopy(a, index + 1, a, index, size - index);
    }
    return old;
  }

  @Override
  public Integer remove(final int index) {
    return Integer.valueOf(removeInt(index));
  }

  @Override
  public boolean rem(final int k) {
    final int index = indexOf(k);
    if (index == -1) {
      return false;
    }
    removeInt(index);
    return true;
  }

  @Override
  public boolean remove(final Object o) {
    return rem(((Integer) o).intValue());
  }

  @Override
  public int indexOf(final int k) {
    for (int i = 0; i < size; i++) {
      if (k == a[i]) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(final int k) {
    for (int i = size; i-- != 0;) {
      if (k == a[i]) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int indexOf(final Object o) {
    return indexOf(((Integer) o).intValue());
  }

  @Override
  public int lastIndexOf(final Object o) {
    return lastIndexOf(((Integer) o).intValue());
  }

  @Override
  public boolean contains(final int k) {
    return indexOf(k) >= 0;
  }

  @Override
  public boolean contains(final Object o) {
    return o instanceof Integer && contains(((Integer) o).intValue());
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void size(final int size) {
    if (size > a.length) {
      a = forceCapacity(a, size, this.size);
    }
    if (size > this.size) {
      Arrays.fill(a, this.size, size, 0);
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

  @Override
  public int[] toIntArray() {
    if (size == 0) {
      return new int[0];
    }
    return Arrays.copyOf(a, size);
  }

  @Override
  public void addElements(final int index, final int a[], final int offset, final int length) {
    ensureIndex(index);
    grow(size + length);
    System.arraycopy(this.a, index, this.a, index + length, size - index);
    System.arraycopy(a, offset, this.a, index, length);
    size += length;
  }

  @Override
  public void addElements(final int index, final int a[]) {
    addElements(index, a, 0, a.length);
  }

  @Override
  public void getElements(final int from, final int[] a, final int offset, final int length) {
    System.arraycopy(this.a, from, a, offset, length);
  }

  @Override
  public boolean addAll(final IntList l) {
    final int n = l.size();
    if (n == 0) {
      return false;
    }
    grow(size + n);
    for (int i = 0; i < n; i++) {
      a[size++] = l.getInt(i);
    }
    return true;
  }

  @Override
  public boolean addAll(final int index, final IntList l) {
    ensureIndex(index);
    final int n = l.size();
    if (n == 0) {
      return false;
    }
    grow(size + n);
    System.arraycopy(a, index, a, index + n, size - index);
    for (int i = 0; i < n; i++) {
      a[index + i] = l.getInt(i);
    }
    size += n;
    return true;
  }

  @Override
  public boolean addAll(final Collection<? extends Integer> c) {
    if (c instanceof IntList) {
      return addAll((IntList) c);
    }
    boolean modified = false;
    for (final Integer e : c) {
      add(e.intValue());
      modified = true;
    }
    return modified;
  }

  @Override
  public boolean addAll(final int index, final Collection<? extends Integer> c) {
    if (c instanceof IntList) {
      return addAll(index, (IntList) c);
    }
    ensureIndex(index);
    int i = index;
    boolean modified = false;
    for (final Integer e : c) {
      add(i++, e.intValue());
      modified = true;
    }
    return modified;
  }

  /** Sorts this list in ascending order. */
  public void sort() {
    Arrays.sort(a, 0, size);
  }

  @Override
  public void forEach(final IntConsumer action) {
    for (int i = 0; i < size; i++) {
      action.accept(a[i]);
    }
  }

  /** Returns a primitive stream over the elements of this list. */
  public IntStream intStream() {
    return Arrays.stream(a, 0, size);
  }

  @Override
  public IntIterator iterator() {
    return new IntIterator() {
      int pos = 0;
      int last = -1;

      @Override
      public boolean hasNext() {
        return pos < size;
      }

      @Override
      public int nextInt() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        last = pos;
        return a[pos++];
      }

      @Override
      public void remove() {
        if (last == -1) {
          throw new IllegalStateException();
        }
        removeInt(last);
        if (last < pos) {
          pos--;
        }
        last = -1;
      }
    };
  }

  @Override
  public IntList subList(final int from, final int to) {
    if (from == 0 && to == size()) {
      return this;
    }
    ensureIndex(from);
    ensureIndex(to);
    if (from > to) {
      throw new IndexOutOfBoundsException(
          "Start index (" + from + ") is greater than end index (" + to + ")");
    }
    return new IntSubList(this, from, to);
  }

  @Override
  public int compareTo(final java.util.List<? extends Integer> l) {
    if (l == this) {
      return 0;
    }
    if (l instanceof IntList) {
      final IntList other = (IntList) l;
      final int s1 = size();
      final int s2 = other.size();
      int i = 0;
      for (; i < s1 && i < s2; i++) {
        final int r = Integer.compare(getInt(i), other.getInt(i));
        if (r != 0) {
          return r;
        }
      }
      return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }
    final java.util.Iterator<? extends Integer> i2 = l.iterator();
    int i = 0;
    for (; i < size() && i2.hasNext(); i++) {
      final int r = Integer.compare(getInt(i), i2.next().intValue());
      if (r != 0) {
        return r;
      }
    }
    return i2.hasNext() ? -1 : (i < size() ? 1 : 0);
  }

  @Override
  public IntArrayList clone() {
    final IntArrayList c = new IntArrayList(size);
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
      s.writeInt(a[i]);
    }
  }

  private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    a = new int[size];
    for (int i = 0; i < size; i++) {
      a[i] = s.readInt();
    }
  }
}
