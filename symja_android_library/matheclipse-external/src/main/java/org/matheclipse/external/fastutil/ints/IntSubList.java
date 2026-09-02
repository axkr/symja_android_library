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
// Slim replacement for the sublist view of it.unimi.dsi.fastutil.ints.AbstractIntList,
// adapted from fastutil 8.5.19 (https://fastutil.di.unimi.it/). Like fastutil's sublist,
// this is a view: changes write through to the backing list.
package org.matheclipse.external.fastutil.ints;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.IntConsumer;

/** A view over a contiguous range of an {@link IntList}. */
public class IntSubList extends AbstractList<Integer>
    implements IntList, RandomAccess, Serializable {

  private static final long serialVersionUID = -7046029254386353131L;

  /** The list this sublist restricts. */
  protected final IntList l;
  /** Starting index (inclusive). */
  protected final int from;
  /** End index (exclusive). */
  protected int to;

  public IntSubList(final IntList l, final int from, final int to) {
    this.l = l;
    this.from = from;
    this.to = to;
  }

  private void assertRange(final int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
    }
    if (index >= size()) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than or equal to list size (" + size() + ")");
    }
  }

  private void assertIndex(final int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
    }
    if (index > size()) {
      throw new IndexOutOfBoundsException(
          "Index (" + index + ") is greater than list size (" + size() + ")");
    }
  }

  @Override
  public int size() {
    return to - from;
  }

  @Override
  public boolean isEmpty() {
    return to <= from;
  }

  @Override
  public int getInt(final int index) {
    assertRange(index);
    return l.getInt(from + index);
  }

  @Override
  public Integer get(final int index) {
    return Integer.valueOf(getInt(index));
  }

  @Override
  public int set(final int index, final int k) {
    assertRange(index);
    return l.set(from + index, k);
  }

  @Override
  public Integer set(final int index, final Integer k) {
    return Integer.valueOf(set(index, k.intValue()));
  }

  @Override
  public boolean add(final int k) {
    l.add(to, k);
    to++;
    return true;
  }

  @Override
  public void add(final int index, final int k) {
    assertIndex(index);
    l.add(from + index, k);
    to++;
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
  public int removeInt(final int index) {
    assertRange(index);
    to--;
    return l.removeInt(from + index);
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
  public boolean contains(final int k) {
    return indexOf(k) >= 0;
  }

  @Override
  public boolean contains(final Object o) {
    return o instanceof Integer && contains(((Integer) o).intValue());
  }

  @Override
  public int indexOf(final int k) {
    for (int i = 0, n = size(); i < n; i++) {
      if (k == getInt(i)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(final int k) {
    for (int i = size(); i-- != 0;) {
      if (k == getInt(i)) {
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
  public void clear() {
    for (int i = size(); i-- != 0;) {
      removeInt(i);
    }
  }

  @Override
  public int[] toIntArray() {
    final int n = size();
    final int[] result = new int[n];
    for (int i = 0; i < n; i++) {
      result[i] = getInt(i);
    }
    return result;
  }

  @Override
  public void addElements(final int index, final int[] a, final int offset, final int length) {
    assertIndex(index);
    l.addElements(from + index, a, offset, length);
    to += length;
  }

  @Override
  public void addElements(final int index, final int[] a) {
    addElements(index, a, 0, a.length);
  }

  @Override
  public void getElements(final int fromIndex, final int[] a, final int offset, final int length) {
    l.getElements(from + fromIndex, a, offset, length);
  }

  @Override
  public void size(final int size) {
    final int oldSize = size();
    if (size > oldSize) {
      for (int i = size - oldSize; i-- != 0;) {
        add(0);
      }
    } else {
      for (int i = oldSize; i-- != size;) {
        removeInt(i);
      }
    }
  }

  @Override
  public boolean addAll(final IntList c) {
    return addAll(size(), c);
  }

  @Override
  public boolean addAll(final int index, final IntList c) {
    assertIndex(index);
    final int n = c.size();
    if (n == 0) {
      return false;
    }
    for (int i = 0; i < n; i++) {
      add(index + i, c.getInt(i));
    }
    return true;
  }

  @Override
  public boolean addAll(final Collection<? extends Integer> c) {
    return addAll(size(), c);
  }

  @Override
  public boolean addAll(final int index, final Collection<? extends Integer> c) {
    if (c instanceof IntList) {
      return addAll(index, (IntList) c);
    }
    assertIndex(index);
    int i = index;
    boolean modified = false;
    for (final Integer e : c) {
      add(i++, e.intValue());
      modified = true;
    }
    return modified;
  }

  @Override
  public void forEach(final IntConsumer action) {
    for (int i = 0, n = size(); i < n; i++) {
      action.accept(getInt(i));
    }
  }

  @Override
  public IntIterator iterator() {
    return new IntIterator() {
      int pos = 0;
      int last = -1;

      @Override
      public boolean hasNext() {
        return pos < size();
      }

      @Override
      public int nextInt() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        last = pos;
        return getInt(pos++);
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
  public IntList subList(final int newFrom, final int newTo) {
    assertIndex(newFrom);
    assertIndex(newTo);
    if (newFrom > newTo) {
      throw new IndexOutOfBoundsException(
          "Start index (" + newFrom + ") is greater than end index (" + newTo + ")");
    }
    return new IntSubList(this, newFrom, newTo);
  }

  @Override
  public int compareTo(final java.util.List<? extends Integer> other) {
    if (other == this) {
      return 0;
    }
    final java.util.Iterator<? extends Integer> i2 = other.iterator();
    int i = 0;
    for (; i < size() && i2.hasNext(); i++) {
      final int r = Integer.compare(getInt(i), i2.next().intValue());
      if (r != 0) {
        return r;
      }
    }
    return i2.hasNext() ? -1 : (i < size() ? 1 : 0);
  }
}
