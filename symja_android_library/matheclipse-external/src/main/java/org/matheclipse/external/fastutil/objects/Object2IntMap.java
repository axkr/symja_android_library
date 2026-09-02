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
// Slim replacement for it.unimi.dsi.fastutil.objects.Object2IntMap, adapted from fastutil
// 8.5.19 (https://fastutil.di.unimi.it/). Only the subset used by Symja is implemented.
package org.matheclipse.external.fastutil.objects;

import java.util.Map;

/** A type-specific {@link Map} from objects to primitive ints. */
public interface Object2IntMap<K> extends Map<K, Integer> {

  /** A type-specific {@link Map.Entry}; provides primitive access to the value. */
  interface Entry<K> extends Map.Entry<K, Integer> {

    /** Returns the value of this entry as a primitive int. */
    int getIntValue();

    /** Replaces the value of this entry; returns the old value. */
    int setValue(int value);

    @Override
    default Integer getValue() {
      return Integer.valueOf(getIntValue());
    }

    @Override
    default Integer setValue(final Integer value) {
      return Integer.valueOf(setValue(value.intValue()));
    }
  }

  /**
   * Sets the value returned by {@link #getInt(Object)} and friends for keys that are not present
   * in the map. Defaults to 0.
   */
  void defaultReturnValue(int rv);

  /** Returns the default return value. */
  int defaultReturnValue();

  /** Returns the value mapped to the given key, or the default return value. */
  int getInt(Object key);

  /** Associates the given value with the given key; returns the old value or the default. */
  int put(K key, int value);

  /** Removes the mapping for the given key; returns the old value or the default. */
  int removeInt(Object key);

  /** Returns the value mapped to the given key, or the given default. */
  int getOrDefault(Object key, int defaultValue);

  /** Returns a type-specific view of the mappings of this map. */
  ObjectSet<Entry<K>> object2IntEntrySet();

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  default ObjectSet<Map.Entry<K, Integer>> entrySet() {
    return (ObjectSet) object2IntEntrySet();
  }
}
