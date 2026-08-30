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
// Slim replacement for it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap, adapted from fastutil
// 8.5.19 (https://fastutil.di.unimi.it/). The balanced tree itself is not ported: this class is
// backed by a java.util.TreeMap, which gives the same ascending-key iteration order and the same
// O(log n) behaviour. The maps used by Symja (evaluation history) are small.
package org.matheclipse.external.fastutil.ints;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.external.fastutil.objects.ObjectSortedSet;

/** A type-specific sorted map from primitive ints to objects. */
public class Int2ObjectAVLTreeMap<V> implements Int2ObjectMap<V>, Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353134L;

  private final TreeMap<Integer, V> map;
  private V defRetValue;

  public Int2ObjectAVLTreeMap() {
    map = new TreeMap<>();
  }

  public Int2ObjectAVLTreeMap(final Comparator<? super Integer> c) {
    map = new TreeMap<>(c);
  }

  public Int2ObjectAVLTreeMap(final Map<? extends Integer, ? extends V> m) {
    map = new TreeMap<>();
    map.putAll(m);
  }

  @Override
  public void defaultReturnValue(final V rv) {
    defRetValue = rv;
  }

  @Override
  public V defaultReturnValue() {
    return defRetValue;
  }

  @Override
  public V get(final int key) {
    final V v = map.get(Integer.valueOf(key));
    return v == null ? defRetValue : v;
  }

  @Override
  public V get(final Object key) {
    final V v = map.get(key);
    return v == null ? defRetValue : v;
  }

  @Override
  public V put(final int key, final V value) {
    final V old = map.put(Integer.valueOf(key), value);
    return old == null ? defRetValue : old;
  }

  @Override
  public V put(final Integer key, final V value) {
    return map.put(key, value);
  }

  @Override
  public V remove(final int key) {
    final V old = map.remove(Integer.valueOf(key));
    return old == null ? defRetValue : old;
  }

  @Override
  public V remove(final Object key) {
    return map.remove(key);
  }

  @Override
  public boolean containsKey(final int key) {
    return map.containsKey(Integer.valueOf(key));
  }

  @Override
  public boolean containsKey(final Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return map.containsValue(value);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public void putAll(final Map<? extends Integer, ? extends V> m) {
    map.putAll(m);
  }

  @Override
  public Set<Integer> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<V> values() {
    return map.values();
  }

  /** Returns the first (lowest) key. */
  public int firstIntKey() {
    return map.firstKey().intValue();
  }

  /** Returns the last (highest) key. */
  public int lastIntKey() {
    return map.lastKey().intValue();
  }

  @Override
  public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
    return new EntrySet(map);
  }

  /** A live, ascending view of the entries of a (sub)map. */
  private final class EntrySet extends AbstractSet<Int2ObjectMap.Entry<V>>
      implements ObjectSortedSet<Int2ObjectMap.Entry<V>> {

    private final SortedMap<Integer, V> view;

    EntrySet(final SortedMap<Integer, V> view) {
      this.view = view;
    }

    @Override
    public Iterator<Int2ObjectMap.Entry<V>> iterator() {
      final Iterator<Map.Entry<Integer, V>> i = view.entrySet().iterator();
      return new Iterator<Int2ObjectMap.Entry<V>>() {
        @Override
        public boolean hasNext() {
          return i.hasNext();
        }

        @Override
        public Int2ObjectMap.Entry<V> next() {
          return new MapEntry<>(i.next());
        }

        @Override
        public void remove() {
          i.remove();
        }
      };
    }

    @Override
    public int size() {
      return view.size();
    }

    @Override
    public Comparator<? super Int2ObjectMap.Entry<V>> comparator() {
      return (a, b) -> Integer.compare(a.getIntKey(), b.getIntKey());
    }

    @Override
    public Int2ObjectMap.Entry<V> first() {
      final Integer k = view.firstKey();
      return new MapEntry<>(new java.util.AbstractMap.SimpleEntry<>(k, view.get(k)));
    }

    @Override
    public Int2ObjectMap.Entry<V> last() {
      final Integer k = view.lastKey();
      return new MapEntry<>(new java.util.AbstractMap.SimpleEntry<>(k, view.get(k)));
    }

    @Override
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> headSet(
        final Int2ObjectMap.Entry<V> toElement) {
      return new EntrySet(view.headMap(Integer.valueOf(toElement.getIntKey())));
    }

    @Override
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> tailSet(
        final Int2ObjectMap.Entry<V> fromElement) {
      return new EntrySet(view.tailMap(Integer.valueOf(fromElement.getIntKey())));
    }

    @Override
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> subSet(final Int2ObjectMap.Entry<V> fromElement,
        final Int2ObjectMap.Entry<V> toElement) {
      return new EntrySet(view.subMap(Integer.valueOf(fromElement.getIntKey()),
          Integer.valueOf(toElement.getIntKey())));
    }
  }

  /** An entry that writes through to the backing map. */
  private static final class MapEntry<V> implements Int2ObjectMap.Entry<V> {
    private final Map.Entry<Integer, V> entry;

    MapEntry(final Map.Entry<Integer, V> entry) {
      this.entry = entry;
    }

    @Override
    public int getIntKey() {
      return entry.getKey().intValue();
    }

    @Override
    public V getValue() {
      return entry.getValue();
    }

    @Override
    public V setValue(final V value) {
      return entry.setValue(value);
    }

    @Override
    public String toString() {
      return getIntKey() + "=>" + getValue();
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
      return Integer.valueOf(getIntKey()).equals(e.getKey())
          && java.util.Objects.equals(getValue(), e.getValue());
    }

    @Override
    public int hashCode() {
      return getIntKey() ^ (getValue() == null ? 0 : getValue().hashCode());
    }
  }

  @Override
  public Int2ObjectAVLTreeMap<V> clone() {
    final Int2ObjectAVLTreeMap<V> c = new Int2ObjectAVLTreeMap<>();
    c.map.putAll(map);
    c.defRetValue = defRetValue;
    return c;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Map)) {
      return false;
    }
    return map.equals(o);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder s = new StringBuilder();
    s.append('{');
    boolean first = true;
    for (final Map.Entry<Integer, V> e : map.entrySet()) {
      if (!first) {
        s.append(", ");
      }
      first = false;
      s.append(e.getKey().intValue()).append("=>").append(e.getValue());
    }
    return s.append('}').toString();
  }
}
