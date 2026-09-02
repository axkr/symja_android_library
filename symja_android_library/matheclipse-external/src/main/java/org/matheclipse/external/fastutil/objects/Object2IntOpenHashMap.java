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
// Slim replacement for it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap, adapted from
// fastutil 8.5.19 (https://fastutil.di.unimi.it/, drv/OpenHashMap.drv). The open-addressing
// scheme, the hash mixing (see HashCommon) and therefore the iteration order are the same as
// in fastutil. Removal through the entry-set iterator is not supported.
package org.matheclipse.external.fastutil.objects;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.matheclipse.external.fastutil.HashCommon;

/** A type-specific hash map from objects to primitive ints, with linear probing. */
public class Object2IntOpenHashMap<K> extends java.util.AbstractMap<K, Integer>
    implements Object2IntMap<K>, Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353132L;

  /** The default load factor of a hash table. */
  public static final float DEFAULT_LOAD_FACTOR = .75f;
  /** The default initial size of a hash table. */
  public static final int DEFAULT_INITIAL_SIZE = 16;

  /** The array of keys. */
  protected transient K[] key;
  /** The array of values. */
  protected transient int[] value;
  /** The mask for wrapping a position counter. */
  protected transient int mask;
  /** Whether this map contains the key zero (stored in the last slot). */
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

  public Object2IntOpenHashMap() {
    this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
  }

  public Object2IntOpenHashMap(final int expected) {
    this(expected, DEFAULT_LOAD_FACTOR);
  }

  @SuppressWarnings("unchecked")
  public Object2IntOpenHashMap(final int expected, final float f) {
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
    key = (K[]) new Object[n + 1];
    value = new int[n + 1];
  }

  public Object2IntOpenHashMap(final Map<? extends K, ? extends Integer> m) {
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

  private int find(final K k) {
    if (k == null) {
      return containsNullKey ? n : -(n + 1);
    }
    K curr;
    final K[] key = this.key;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & mask]) == null) {
      return -(pos + 1);
    }
    if (k.equals(curr)) {
      return pos;
    }
    while (true) {
      if ((curr = key[pos = (pos + 1) & mask]) == null) {
        return -(pos + 1);
      }
      if (k.equals(curr)) {
        return pos;
      }
    }
  }

  private void insert(final int pos, final K k, final int v) {
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
  public int put(final K k, final int v) {
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
  public Integer put(final K k, final Integer v) {
    final int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v.intValue());
      return null;
    }
    final Integer oldValue = Integer.valueOf(value[pos]);
    value[pos] = v.intValue();
    return oldValue;
  }

  /**
   * Adds an increment to the value of the given key; returns the old value (or the default return
   * value if the key was not present, in which case the increment becomes the new value).
   */
  public int addTo(final K k, final int incr) {
    final int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, defRetValue + incr);
      return defRetValue;
    }
    final int oldValue = value[pos];
    value[pos] += incr;
    return oldValue;
  }

  @Override
  public int getInt(final Object k) {
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
    return pos < 0 ? defRetValue : value[pos];
  }

  @Override
  public int getOrDefault(final Object k, final int defaultValue) {
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
    return pos < 0 ? defaultValue : value[pos];
  }

  @Override
  public Integer get(final Object k) {
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
    return pos < 0 ? null : Integer.valueOf(value[pos]);
  }

  @Override
  public boolean containsKey(final Object k) {
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
    return pos >= 0;
  }

  /** Returns true if the given key is present. */
  public boolean contains(final Object k) {
    return containsKey(k);
  }

  @Override
  public boolean containsValue(final Object ov) {
    if (!(ov instanceof Integer)) {
      return false;
    }
    final int v = ((Integer) ov).intValue();
    final int[] value = this.value;
    final K[] key = this.key;
    if (containsNullKey && value[n] == v) {
      return true;
    }
    for (int i = n; i-- != 0;) {
      if (key[i] != null && value[i] == v) {
        return true;
      }
    }
    return false;
  }

  protected final void shiftKeys(int pos) {
    int last, slot;
    K curr;
    final K[] key = this.key;
    for (;;) {
      pos = ((last = pos) + 1) & mask;
      for (;;) {
        if ((curr = key[pos]) == null) {
          key[last] = null;
          return;
        }
        slot = HashCommon.mix(curr.hashCode()) & mask;
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
    key[n] = null;
    final int oldValue = value[n];
    size--;
    return oldValue;
  }

  @Override
  public int removeInt(final Object k) {
    if (k == null) {
      return containsNullKey ? removeNullEntry() : defRetValue;
    }
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
    if (pos < 0) {
      return defRetValue;
    }
    return removeEntry(pos);
  }

  @Override
  public Integer remove(final Object k) {
    if (k == null) {
      return containsNullKey ? Integer.valueOf(removeNullEntry()) : null;
    }
    @SuppressWarnings("unchecked")
    final int pos = find((K) k);
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
    java.util.Arrays.fill(key, null);
  }

  @SuppressWarnings("unchecked")
  protected void rehash(final int newN) {
    final K key[] = this.key;
    final int value[] = this.value;
    final int mask = newN - 1;
    final K newKey[] = (K[]) new Object[newN + 1];
    final int newValue[] = new int[newN + 1];
    int i = n, pos;
    for (int j = realSize(); j-- != 0;) {
      while (key[--i] == null) {
        // find the next used slot
      }
      if (newKey[pos = HashCommon.mix(key[i].hashCode()) & mask] != null) {
        while (newKey[pos = (pos + 1) & mask] != null) {
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
  public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
    return new EntrySet();
  }

  /** A mutable entry backed by a table position. */
  private final class MapEntry implements Object2IntMap.Entry<K> {
    private final int index;

    MapEntry(final int index) {
      this.index = index;
    }

    @Override
    public K getKey() {
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
      return getKey() + "=>" + getIntValue();
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
      return java.util.Objects.equals(getKey(), e.getKey())
          && Integer.valueOf(getIntValue()).equals(e.getValue());
    }

    @Override
    public int hashCode() {
      return (getKey() == null ? 0 : getKey().hashCode()) ^ getIntValue();
    }
  }

  private final class EntrySet extends AbstractSet<Object2IntMap.Entry<K>>
      implements ObjectSet<Object2IntMap.Entry<K>> {

    @Override
    public Iterator<Object2IntMap.Entry<K>> iterator() {
      return new Iterator<Object2IntMap.Entry<K>>() {
        /** The next position to return, or n for the null key. */
        int pos = n;
        int c = size;
        boolean mustReturnNullKey = containsNullKey;

        @Override
        public boolean hasNext() {
          return c != 0;
        }

        @Override
        public Object2IntMap.Entry<K> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          c--;
          if (mustReturnNullKey) {
            mustReturnNullKey = false;
            return new MapEntry(n);
          }
          final K key[] = Object2IntOpenHashMap.this.key;
          for (;;) {
            if (--pos < 0) {
              throw new NoSuchElementException();
            }
            if (key[pos] != null) {
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
      if (!(e.getValue() instanceof Integer)) {
        return false;
      }
      @SuppressWarnings("unchecked")
      final int pos = find((K) e.getKey());
      return pos >= 0 && value[pos] == ((Integer) e.getValue()).intValue();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object2IntOpenHashMap<K> clone() {
    final Object2IntOpenHashMap<K> c = new Object2IntOpenHashMap<>(0, f);
    c.n = n;
    c.mask = mask;
    c.maxFill = maxFill;
    c.size = size;
    c.containsNullKey = containsNullKey;
    c.defRetValue = defRetValue;
    c.key = (K[]) new Object[n + 1];
    System.arraycopy(key, 0, c.key, 0, n + 1);
    c.value = new int[n + 1];
    System.arraycopy(value, 0, c.value, 0, n + 1);
    return c;
  }

  private void writeObject(final java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    final Iterator<Object2IntMap.Entry<K>> i = object2IntEntrySet().iterator();
    while (i.hasNext()) {
      final Object2IntMap.Entry<K> e = i.next();
      s.writeObject(e.getKey());
      s.writeInt(e.getIntValue());
    }
  }

  @SuppressWarnings("unchecked")
  private void readObject(final java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    final int elements = size;
    n = HashCommon.arraySize(elements, f);
    maxFill = HashCommon.maxFill(n, f);
    mask = n - 1;
    key = (K[]) new Object[n + 1];
    value = new int[n + 1];
    size = 0;
    containsNullKey = false;
    for (int i = elements; i-- != 0;) {
      final K k = (K) s.readObject();
      final int v = s.readInt();
      put(k, v);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public ObjectSet<Map.Entry<K, Integer>> entrySet() {
    return (ObjectSet) object2IntEntrySet();
  }

  @Override
  public String toString() {
    final StringBuilder s = new StringBuilder();
    s.append('{');
    boolean first = true;
    for (final Object2IntMap.Entry<K> e : object2IntEntrySet()) {
      if (!first) {
        s.append(", ");
      }
      first = false;
      s.append(e.getKey()).append("=>").append(e.getIntValue());
    }
    return s.append('}').toString();
  }

}
