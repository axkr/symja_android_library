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
// Slim replacement for it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap, adapted from fastutil
// 8.5.19 (https://fastutil.di.unimi.it/, drv/OpenHashMap.drv). The open-addressing scheme and
// the hash mixing (see HashCommon) are the same as in fastutil, so the iteration order matches.
// Removal through the entry-set iterator is not supported.
package org.matheclipse.external.fastutil.ints;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import org.matheclipse.external.fastutil.HashCommon;
import org.matheclipse.external.fastutil.objects.ObjectSet;

/** A type-specific hash map from primitive ints to primitive ints, with linear probing. */
public class Int2IntOpenHashMap extends java.util.AbstractMap<Integer, Integer>
    implements Int2IntMap, Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353136L;

  /** The default load factor of a hash table. */
  public static final float DEFAULT_LOAD_FACTOR = .75f;
  /** The default initial size of a hash table. */
  public static final int DEFAULT_INITIAL_SIZE = 16;

  /** The array of keys; the key 0 is stored in the extra slot {@link #n}. */
  protected transient int[] key;
  /** The array of values. */
  protected transient int[] value;
  /** The mask for wrapping a position counter. */
  protected transient int mask;
  /** Whether this map contains the key zero. */
  protected transient boolean containsNullKey;
  /** The current table size. */
  protected transient int n;
  /** Threshold after which we rehash. */
  protected transient int maxFill;
  /** The acceptable load factor. */
  protected final float f;
  /** Number of entries in the map. */
  protected int size;
  /** The value returned for keys that are not present. */
  protected int defRetValue;

  public Int2IntOpenHashMap() {
    this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
  }

  public Int2IntOpenHashMap(final int expected) {
    this(expected, DEFAULT_LOAD_FACTOR);
  }

  public Int2IntOpenHashMap(final int expected, final float f) {
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
    key = new int[n + 1];
    value = new int[n + 1];
  }

  public Int2IntOpenHashMap(final Map<? extends Integer, ? extends Integer> m) {
    this(m.size(), DEFAULT_LOAD_FACTOR);
    putAll(m);
  }

  @Override
  public void defaultReturnValue(final int rv) {
    defRetValue = rv;
  }

  @Override
  public int defaultReturnValue() {
    return defRetValue;
  }

  private int realSize() {
    return containsNullKey ? size - 1 : size;
  }

  private int find(final int k) {
    if (k == 0) {
      return containsNullKey ? n : -(n + 1);
    }
    int curr;
    final int[] key = this.key;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k) & mask]) == 0) {
      return -(pos + 1);
    }
    if (curr == k) {
      return pos;
    }
    while (true) {
      if ((curr = key[pos = (pos + 1) & mask]) == 0) {
        return -(pos + 1);
      }
      if (curr == k) {
        return pos;
      }
    }
  }

  private void insert(final int pos, final int k, final int v) {
    if (pos == n) {
      containsNullKey = true;
    }
    key[pos] = k;
    value[pos] = v;
    if (size++ >= maxFill) {
      rehash(HashCommon.arraySize(size + 1, f));
    }
  }

  @Override
  public int put(final int k, final int v) {
    final int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return defRetValue;
    }
    final int oldValue = value[pos];
    value[pos] = v;
    return oldValue;
  }

  @Override
  public Integer put(final Integer k, final Integer v) {
    final int pos = find(k.intValue());
    if (pos < 0) {
      insert(-pos - 1, k.intValue(), v.intValue());
      return null;
    }
    final Integer oldValue = Integer.valueOf(value[pos]);
    value[pos] = v.intValue();
    return oldValue;
  }

  /** Adds an increment to the value of the given key; returns the old value. */
  public int addTo(final int k, final int incr) {
    final int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, defRetValue + incr);
      return defRetValue;
    }
    final int oldValue = value[pos];
    value[pos] += incr;
    return oldValue;
  }

  /**
   * If the given key is not present, computes a value with the given function, stores and returns
   * it; otherwise returns the present value.
   */
  public int computeIfAbsent(final int k, final IntUnaryOperator mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    final int pos = find(k);
    if (pos >= 0) {
      return value[pos];
    }
    final int newValue = mappingFunction.applyAsInt(k);
    insert(-pos - 1, k, newValue);
    return newValue;
  }

  @Override
  public int get(final int k) {
    final int pos = find(k);
    return pos < 0 ? defRetValue : value[pos];
  }

  @Override
  public Integer get(final Object k) {
    final int pos = find(((Integer) k).intValue());
    return pos < 0 ? null : Integer.valueOf(value[pos]);
  }

  @Override
  public boolean containsKey(final int k) {
    return find(k) >= 0;
  }

  @Override
  public boolean containsKey(final Object k) {
    return k instanceof Integer && find(((Integer) k).intValue()) >= 0;
  }

  /** Returns true if the given key is present. */
  public boolean contains(final int k) {
    return containsKey(k);
  }

  @Override
  public boolean containsValue(final Object ov) {
    if (!(ov instanceof Integer)) {
      return false;
    }
    final int v = ((Integer) ov).intValue();
    final int[] value = this.value;
    final int[] key = this.key;
    if (containsNullKey && value[n] == v) {
      return true;
    }
    for (int i = n; i-- != 0;) {
      if (key[i] != 0 && value[i] == v) {
        return true;
      }
    }
    return false;
  }

  protected final void shiftKeys(int pos) {
    int last, slot, curr;
    final int[] key = this.key;
    for (;;) {
      pos = ((last = pos) + 1) & mask;
      for (;;) {
        if ((curr = key[pos]) == 0) {
          key[last] = 0;
          return;
        }
        slot = HashCommon.mix(curr) & mask;
        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
          break;
        }
        pos = (pos + 1) & mask;
      }
      key[last] = curr;
      value[last] = value[pos];
    }
  }

  private int removeEntry(final int pos) {
    final int oldValue = value[pos];
    size--;
    shiftKeys(pos);
    return oldValue;
  }

  private int removeNullEntry() {
    containsNullKey = false;
    key[n] = 0;
    final int oldValue = value[n];
    size--;
    return oldValue;
  }

  @Override
  public int remove(final int k) {
    if (k == 0) {
      return containsNullKey ? removeNullEntry() : defRetValue;
    }
    final int pos = find(k);
    if (pos < 0) {
      return defRetValue;
    }
    return removeEntry(pos);
  }

  @Override
  public Integer remove(final Object k) {
    final int kk = ((Integer) k).intValue();
    if (kk == 0) {
      return containsNullKey ? Integer.valueOf(removeNullEntry()) : null;
    }
    final int pos = find(kk);
    if (pos < 0) {
      return null;
    }
    return Integer.valueOf(removeEntry(pos));
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
    containsNullKey = false;
    java.util.Arrays.fill(key, 0);
  }

  protected void rehash(final int newN) {
    final int key[] = this.key;
    final int value[] = this.value;
    final int mask = newN - 1;
    final int newKey[] = new int[newN + 1];
    final int newValue[] = new int[newN + 1];
    int i = n, pos;
    for (int j = realSize(); j-- != 0;) {
      while (key[--i] == 0) {
        // find the next used slot
      }
      if (newKey[pos = HashCommon.mix(key[i]) & mask] != 0) {
        while (newKey[pos = (pos + 1) & mask] != 0) {
          // find a free slot
        }
      }
      newKey[pos] = key[i];
      newValue[pos] = value[i];
    }
    newValue[newN] = value[n];
    n = newN;
    this.mask = mask;
    maxFill = HashCommon.maxFill(n, f);
    this.key = newKey;
    this.value = newValue;
  }

  @Override
  public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
    return new EntrySet();
  }

  @Override
  public Set<Integer> keySet() {
    final Set<Integer> result = new java.util.LinkedHashSet<>();
    for (final Int2IntMap.Entry e : int2IntEntrySet()) {
      result.add(Integer.valueOf(e.getIntKey()));
    }
    return result;
  }

  @Override
  public Collection<Integer> values() {
    final java.util.List<Integer> result = new java.util.ArrayList<>(size);
    for (final Int2IntMap.Entry e : int2IntEntrySet()) {
      result.add(Integer.valueOf(e.getIntValue()));
    }
    return result;
  }

  private final class MapEntry implements Int2IntMap.Entry {
    private final int index;

    MapEntry(final int index) {
      this.index = index;
    }

    @Override
    public int getIntKey() {
      return key[index];
    }

    @Override
    public int getIntValue() {
      return value[index];
    }

    @Override
    public int setValue(final int v) {
      final int oldValue = value[index];
      value[index] = v;
      return oldValue;
    }

    @Override
    public String toString() {
      return getIntKey() + "=>" + getIntValue();
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
      return Integer.valueOf(getIntKey()).equals(e.getKey())
          && Integer.valueOf(getIntValue()).equals(e.getValue());
    }

    @Override
    public int hashCode() {
      return getIntKey() ^ getIntValue();
    }
  }

  private final class EntrySet extends AbstractSet<Int2IntMap.Entry>
      implements ObjectSet<Int2IntMap.Entry> {

    @Override
    public Iterator<Int2IntMap.Entry> iterator() {
      return new Iterator<Int2IntMap.Entry>() {
        int pos = n;
        int c = size;
        boolean mustReturnNullKey = containsNullKey;

        @Override
        public boolean hasNext() {
          return c != 0;
        }

        @Override
        public Int2IntMap.Entry next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          c--;
          if (mustReturnNullKey) {
            mustReturnNullKey = false;
            return new MapEntry(n);
          }
          final int key[] = Int2IntOpenHashMap.this.key;
          for (;;) {
            if (--pos < 0) {
              throw new NoSuchElementException();
            }
            if (key[pos] != 0) {
              return new MapEntry(pos);
            }
          }
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException(
              "Removal through the entry-set iterator is not supported");
        }
      };
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public boolean contains(final Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
      if (!(e.getKey() instanceof Integer) || !(e.getValue() instanceof Integer)) {
        return false;
      }
      final int pos = find(((Integer) e.getKey()).intValue());
      return pos >= 0 && value[pos] == ((Integer) e.getValue()).intValue();
    }
  }

  @Override
  public Int2IntOpenHashMap clone() {
    final Int2IntOpenHashMap c = new Int2IntOpenHashMap(0, f);
    c.n = n;
    c.mask = mask;
    c.maxFill = maxFill;
    c.size = size;
    c.containsNullKey = containsNullKey;
    c.defRetValue = defRetValue;
    c.key = new int[n + 1];
    System.arraycopy(key, 0, c.key, 0, n + 1);
    c.value = new int[n + 1];
    System.arraycopy(value, 0, c.value, 0, n + 1);
    return c;
  }

  @Override
  public String toString() {
    final StringBuilder s = new StringBuilder();
    s.append('{');
    boolean first = true;
    for (final Int2IntMap.Entry e : int2IntEntrySet()) {
      if (!first) {
        s.append(", ");
      }
      first = false;
      s.append(e.getIntKey()).append("=>").append(e.getIntValue());
    }
    return s.append('}').toString();
  }

  private void writeObject(final java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    for (final Int2IntMap.Entry e : int2IntEntrySet()) {
      s.writeInt(e.getIntKey());
      s.writeInt(e.getIntValue());
    }
  }

  private void readObject(final java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    final int elements = size;
    n = HashCommon.arraySize(elements, f);
    maxFill = HashCommon.maxFill(n, f);
    mask = n - 1;
    key = new int[n + 1];
    value = new int[n + 1];
    size = 0;
    containsNullKey = false;
    for (int i = elements; i-- != 0;) {
      put(s.readInt(), s.readInt());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
    return (ObjectSet) int2IntEntrySet();
  }

}
