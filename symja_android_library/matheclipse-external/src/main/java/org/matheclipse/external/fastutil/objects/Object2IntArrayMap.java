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
// Slim replacement for it.unimi.dsi.fastutil.objects.Object2IntArrayMap, adapted from fastutil
// 8.5.19 (https://fastutil.di.unimi.it/, drv/ArrayMap.drv). Backed by two parallel arrays and
// linear search; entries are kept and iterated in insertion order, as in fastutil.
package org.matheclipse.external.fastutil.objects;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/** A type-specific array-based map from objects to primitive ints, for small maps. */
public class Object2IntArrayMap<K> extends java.util.AbstractMap<K, Integer>
    implements Object2IntMap<K>, Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353133L;

  /** The keys, in insertion order. */
  private transient Object[] key;
  /** The values, parallel to {@link #key}. */
  private transient int[] value;
  /** The number of valid entries. */
  private int size;
  /** The value returned for keys that are not present. */
  private int defRetValue;

  public Object2IntArrayMap() {
    this.key = new Object[0];
    this.value = new int[0];
  }

  public Object2IntArrayMap(final int capacity) {
    this.key = new Object[capacity];
    this.value = new int[capacity];
  }

  public Object2IntArrayMap(final Map<? extends K, ? extends Integer> m) {
    this(m.size());
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

  private int findKey(final Object k) {
    final Object[] key = this.key;
    for (int i = size; i-- != 0;) {
      if (Objects.equals(key[i], k)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int getInt(final Object k) {
    final int pos = findKey(k);
    return pos == -1 ? defRetValue : value[pos];
  }

  @Override
  public int getOrDefault(final Object k, final int defaultValue) {
    final int pos = findKey(k);
    return pos == -1 ? defaultValue : value[pos];
  }

  @Override
  public Integer get(final Object k) {
    final int pos = findKey(k);
    return pos == -1 ? null : Integer.valueOf(value[pos]);
  }

  @Override
  public int put(final K k, final int v) {
    final int pos = findKey(k);
    if (pos != -1) {
      final int oldValue = value[pos];
      value[pos] = v;
      return oldValue;
    }
    if (size == key.length) {
      final int newLength = size == 0 ? 2 : size * 2;
      final Object[] newKey = new Object[newLength];
      final int[] newValue = new int[newLength];
      System.arraycopy(key, 0, newKey, 0, size);
      System.arraycopy(value, 0, newValue, 0, size);
      key = newKey;
      value = newValue;
    }
    key[size] = k;
    value[size] = v;
    size++;
    return defRetValue;
  }

  @Override
  public Integer put(final K k, final Integer v) {
    final int pos = findKey(k);
    final int oldValue = put(k, v.intValue());
    return pos == -1 ? null : Integer.valueOf(oldValue);
  }

  @Override
  public int removeInt(final Object k) {
    final int pos = findKey(k);
    if (pos == -1) {
      return defRetValue;
    }
    final int oldValue = value[pos];
    final int tail = size - pos - 1;
    System.arraycopy(key, pos + 1, key, pos, tail);
    System.arraycopy(value, pos + 1, value, pos, tail);
    size--;
    key[size] = null;
    return oldValue;
  }

  @Override
  public Integer remove(final Object k) {
    final int pos = findKey(k);
    if (pos == -1) {
      return null;
    }
    return Integer.valueOf(removeInt(k));
  }

  @Override
  public boolean containsKey(final Object k) {
    return findKey(k) != -1;
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
    for (int i = size; i-- != 0;) {
      if (value[i] == v) {
        return true;
      }
    }
    return false;
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
    for (int i = size; i-- != 0;) {
      key[i] = null;
    }
    size = 0;
  }

  @Override
  public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
    return new EntrySet();
  }

  private final class MapEntry implements Object2IntMap.Entry<K> {
    private final int index;

    MapEntry(final int index) {
      this.index = index;
    }

    @SuppressWarnings("unchecked")
    @Override
    public K getKey() {
      return (K) key[index];
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
      return Objects.equals(getKey(), e.getKey())
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
        int next = 0;

        @Override
        public boolean hasNext() {
          return next < size;
        }

        @Override
        public Object2IntMap.Entry<K> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return new MapEntry(next++);
        }
      };
    }

    @Override
    public int size() {
      return size;
    }
  }

  @Override
  public Object2IntArrayMap<K> clone() {
    final Object2IntArrayMap<K> c = new Object2IntArrayMap<>(size);
    System.arraycopy(key, 0, c.key, 0, size);
    System.arraycopy(value, 0, c.value, 0, size);
    c.size = size;
    c.defRetValue = defRetValue;
    return c;
  }

  private void writeObject(final java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    for (int i = 0; i < size; i++) {
      s.writeObject(key[i]);
      s.writeInt(value[i]);
    }
  }

  private void readObject(final java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    key = new Object[size];
    value = new int[size];
    for (int i = 0; i < size; i++) {
      key[i] = s.readObject();
      value[i] = s.readInt();
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
