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
// Slim replacement for it.unimi.dsi.fastutil.longs.LongOpenHashSet, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/, drv/OpenHashSet.drv). The open-addressing scheme and the hash
// mixing (see HashCommon) are the same as in fastutil, so the iteration order matches.
package org.matheclipse.external.fastutil.longs;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.matheclipse.external.fastutil.HashCommon;

/** A type-specific hash set of primitive longs, with linear probing. */
public class LongOpenHashSet extends AbstractSet<Long> implements Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353138L;

  /** The default load factor of a hash table. */
  public static final float DEFAULT_LOAD_FACTOR = .75f;
  /** The default initial size of a hash table. */
  public static final int DEFAULT_INITIAL_SIZE = 16;

  /** The array of keys; the key 0 is stored in the extra slot {@link #n}. */
  protected transient long[] key;
  /** The mask for wrapping a position counter. */
  protected transient int mask;
  /** Whether this set contains the key zero. */
  protected transient boolean containsNull;
  /** The current table size. */
  protected transient int n;
  /** Threshold after which we rehash. */
  protected transient int maxFill;
  /** The acceptable load factor. */
  protected final float f;
  /** Number of elements in the set. */
  protected int size;

  public LongOpenHashSet() {
    this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
  }

  public LongOpenHashSet(final int expected) {
    this(expected, DEFAULT_LOAD_FACTOR);
  }

  public LongOpenHashSet(final int expected, final float f) {
    if (f <= 0 || f >= 1) {
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
    }
    if (expected < 0) {
      throw new IllegalArgumentException("The expected number of elements must be nonnegative");
    }
    this.f = f;
    n = HashCommon.arraySize(expected, f);
    mask = n - 1;
    maxFill = HashCommon.maxFill(n, f);
    key = new long[n + 1];
  }

  public LongOpenHashSet(final Collection<? extends Long> c) {
    this(c.size(), DEFAULT_LOAD_FACTOR);
    for (final Long e : c) {
      add(e.longValue());
    }
  }

  public LongOpenHashSet(final long[] a) {
    this(a.length, DEFAULT_LOAD_FACTOR);
    for (final long k : a) {
      add(k);
    }
  }

  private int realSize() {
    return containsNull ? size - 1 : size;
  }

  /** Adds the given element; returns true if the set changed. */
  public boolean add(final long k) {
    if (k == 0) {
      if (containsNull) {
        return false;
      }
      containsNull = true;
      key[n] = 0;
    } else {
      long curr;
      final long[] key = this.key;
      int pos;
      if ((curr = key[pos = (int) HashCommon.mix(k) & mask]) != 0) {
        if (curr == k) {
          return false;
        }
        while ((curr = key[pos = (pos + 1) & mask]) != 0) {
          if (curr == k) {
            return false;
          }
        }
      }
      key[pos] = k;
    }
    if (size++ >= maxFill) {
      rehash(HashCommon.arraySize(size + 1, f));
    }
    return true;
  }

  @Override
  public boolean add(final Long k) {
    return add(k.longValue());
  }

  /** Returns true if the set contains the given element. */
  public boolean contains(final long k) {
    if (k == 0) {
      return containsNull;
    }
    long curr;
    final long[] key = this.key;
    int pos;
    if ((curr = key[pos = (int) HashCommon.mix(k) & mask]) == 0) {
      return false;
    }
    if (curr == k) {
      return true;
    }
    while (true) {
      if ((curr = key[pos = (pos + 1) & mask]) == 0) {
        return false;
      }
      if (curr == k) {
        return true;
      }
    }
  }

  @Override
  public boolean contains(final Object o) {
    return o instanceof Long && contains(((Long) o).longValue());
  }

  protected final void shiftKeys(int pos) {
    int last, slot;
    long curr;
    final long[] key = this.key;
    for (;;) {
      pos = ((last = pos) + 1) & mask;
      for (;;) {
        if ((curr = key[pos]) == 0) {
          key[last] = 0;
          return;
        }
        slot = (int) HashCommon.mix(curr) & mask;
        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
          break;
        }
        pos = (pos + 1) & mask;
      }
      key[last] = curr;
    }
  }

  /** Removes the given element; returns true if the set changed. */
  public boolean remove(final long k) {
    if (k == 0) {
      if (!containsNull) {
        return false;
      }
      containsNull = false;
      key[n] = 0;
      size--;
      return true;
    }
    long curr;
    final long[] key = this.key;
    int pos;
    if ((curr = key[pos = (int) HashCommon.mix(k) & mask]) == 0) {
      return false;
    }
    if (curr == k) {
      size--;
      shiftKeys(pos);
      return true;
    }
    while (true) {
      if ((curr = key[pos = (pos + 1) & mask]) == 0) {
        return false;
      }
      if (curr == k) {
        size--;
        shiftKeys(pos);
        return true;
      }
    }
  }

  @Override
  public boolean remove(final Object o) {
    return o instanceof Long && remove(((Long) o).longValue());
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void clear() {
    if (size == 0) {
      return;
    }
    size = 0;
    containsNull = false;
    java.util.Arrays.fill(key, 0L);
  }

  protected void rehash(final int newN) {
    final long key[] = this.key;
    final int mask = newN - 1;
    final long newKey[] = new long[newN + 1];
    int i = n, pos;
    for (int j = realSize(); j-- != 0;) {
      while (key[--i] == 0) {
        // find the next used slot
      }
      if (newKey[pos = (int) HashCommon.mix(key[i]) & mask] != 0) {
        while (newKey[pos = (pos + 1) & mask] != 0) {
          // find a free slot
        }
      }
      newKey[pos] = key[i];
    }
    n = newN;
    this.mask = mask;
    maxFill = HashCommon.maxFill(n, f);
    this.key = newKey;
  }

  /** Returns an iterator over the elements of this set. */
  public LongIterator longIterator() {
    return new SetIterator();
  }

  @Override
  public Iterator<Long> iterator() {
    return new SetIterator();
  }

  private final class SetIterator implements LongIterator {
    int pos = n;
    int c = size;
    boolean mustReturnNull = containsNull;

    @Override
    public boolean hasNext() {
      return c != 0;
    }

    @Override
    public long nextLong() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      c--;
      if (mustReturnNull) {
        mustReturnNull = false;
        return key[n];
      }
      final long key[] = LongOpenHashSet.this.key;
      for (;;) {
        if (--pos < 0) {
          throw new NoSuchElementException();
        }
        if (key[pos] != 0) {
          return key[pos];
        }
      }
    }
  }

  /** Returns the elements of this set as a freshly allocated array. */
  public long[] toLongArray() {
    final long[] result = new long[size];
    int i = 0;
    final LongIterator it = longIterator();
    while (it.hasNext()) {
      result[i++] = it.nextLong();
    }
    return result;
  }

  @Override
  public LongOpenHashSet clone() {
    final LongOpenHashSet c = new LongOpenHashSet(0, f);
    c.n = n;
    c.mask = mask;
    c.maxFill = maxFill;
    c.size = size;
    c.containsNull = containsNull;
    c.key = new long[n + 1];
    System.arraycopy(key, 0, c.key, 0, n + 1);
    return c;
  }

  private void writeObject(final java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    final LongIterator i = longIterator();
    while (i.hasNext()) {
      s.writeLong(i.nextLong());
    }
  }

  private void readObject(final java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    final int elements = size;
    n = HashCommon.arraySize(elements, f);
    maxFill = HashCommon.maxFill(n, f);
    mask = n - 1;
    key = new long[n + 1];
    size = 0;
    containsNull = false;
    for (int i = elements; i-- != 0;) {
      add(s.readLong());
    }
  }
}
