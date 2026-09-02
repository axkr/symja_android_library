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
// Slim replacement for it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/). The red-black tree itself is not ported: this class is backed
// by a java.util.TreeMap, which gives the same ascending-key iteration order and O(log n)
// behaviour. The maps used by Symja (prime factor exponents) are small.
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

/** A type-specific sorted map from primitive ints to primitive ints. */
public class Int2IntRBTreeMap implements Int2IntMap, Cloneable, Serializable {

  private static final long serialVersionUID = 8443841425430353135L;

  private final TreeMap<Integer, Integer> map;
  private int defRetValue;

  public Int2IntRBTreeMap() {
    map = new TreeMap<>();
  }

  public Int2IntRBTreeMap(final Comparator<? super Integer> c) {
    map = new TreeMap<>(c);
  }

  public Int2IntRBTreeMap(final Map<? extends Integer, ? extends Integer> m) {
    map = new TreeMap<>();
    map.putAll(m);
  }

  @Override
  public void defaultReturnValue(final int rv) {
    defRetValue = rv;
  }

  @Override
  public int defaultReturnValue() {
    return defRetValue;
  }

  @Override
  public int get(final int key) {
    final Integer v = map.get(Integer.valueOf(key));
    return v == null ? defRetValue : v.intValue();
  }

  @Override
  public Integer get(final Object key) {
    return map.get(key);
  }

  @Override
  public int put(final int key, final int value) {
    final Integer old = map.put(Integer.valueOf(key), Integer.valueOf(value));
    return old == null ? defRetValue : old.intValue();
  }

  @Override
  public Integer put(final Integer key, final Integer value) {
    return map.put(key, value);
  }

  @Override
  public int remove(final int key) {
    final Integer old = map.remove(Integer.valueOf(key));
    return old == null ? defRetValue : old.intValue();
  }

  @Override
  public Integer remove(final Object key) {
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
  public void putAll(final Map<? extends Integer, ? extends Integer> m) {
    map.putAll(m);
  }

  @Override
  public Set<Integer> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<Integer> values() {
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
  public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
    return new EntrySet(map);
  }

  /** A live, ascending view of the entries of a (sub)map. */
  private final class EntrySet extends AbstractSet<Int2IntMap.Entry>
      implements ObjectSortedSet<Int2IntMap.Entry> {

    private final SortedMap<Integer, Integer> view;

    EntrySet(final SortedMap<Integer, Integer> view) {
      this.view = view;
    }

    @Override
    public Iterator<Int2IntMap.Entry> iterator() {
      final Iterator<Map.Entry<Integer, Integer>> i = view.entrySet().iterator();
      return new Iterator<Int2IntMap.Entry>() {
        @Override
        public boolean hasNext() {
          return i.hasNext();
        }

        @Override
        public Int2IntMap.Entry next() {
          return new MapEntry(i.next());
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
    public Comparator<? super Int2IntMap.Entry> comparator() {
      return (a, b) -> Integer.compare(a.getIntKey(), b.getIntKey());
    }

    @Override
    public Int2IntMap.Entry first() {
      final Integer k = view.firstKey();
      return new MapEntry(new java.util.AbstractMap.SimpleEntry<>(k, view.get(k)));
    }

    @Override
    public Int2IntMap.Entry last() {
      final Integer k = view.lastKey();
      return new MapEntry(new java.util.AbstractMap.SimpleEntry<>(k, view.get(k)));
    }

    @Override
    public ObjectSortedSet<Int2IntMap.Entry> headSet(final Int2IntMap.Entry toElement) {
      return new EntrySet(view.headMap(Integer.valueOf(toElement.getIntKey())));
    }

    @Override
    public ObjectSortedSet<Int2IntMap.Entry> tailSet(final Int2IntMap.Entry fromElement) {
      return new EntrySet(view.tailMap(Integer.valueOf(fromElement.getIntKey())));
    }

    @Override
    public ObjectSortedSet<Int2IntMap.Entry> subSet(final Int2IntMap.Entry fromElement,
        final Int2IntMap.Entry toElement) {
      return new EntrySet(view.subMap(Integer.valueOf(fromElement.getIntKey()),
          Integer.valueOf(toElement.getIntKey())));
    }
  }

  /** An entry that writes through to the backing map. */
  private static final class MapEntry implements Int2IntMap.Entry {
    private final Map.Entry<Integer, Integer> entry;

    MapEntry(final Map.Entry<Integer, Integer> entry) {
      this.entry = entry;
    }

    @Override
    public int getIntKey() {
      return entry.getKey().intValue();
    }

    @Override
    public int getIntValue() {
      return entry.getValue().intValue();
    }

    @Override
    public int setValue(final int value) {
      return entry.setValue(Integer.valueOf(value)).intValue();
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

  @Override
  public Int2IntRBTreeMap clone() {
    final Int2IntRBTreeMap c = new Int2IntRBTreeMap();
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
    for (final Map.Entry<Integer, Integer> e : map.entrySet()) {
      if (!first) {
        s.append(", ");
      }
      first = false;
      s.append(e.getKey().intValue()).append("=>").append(e.getValue().intValue());
    }
    return s.append('}').toString();
  }
}
