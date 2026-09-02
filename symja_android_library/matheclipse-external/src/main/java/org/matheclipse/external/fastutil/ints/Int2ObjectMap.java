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
// Slim replacement for it.unimi.dsi.fastutil.ints.Int2ObjectMap, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/). Only the subset used by Symja is implemented.
package org.matheclipse.external.fastutil.ints;

import java.util.Map;
import org.matheclipse.external.fastutil.objects.ObjectSet;

/** A type-specific {@link Map} from primitive ints to objects. */
public interface Int2ObjectMap<V> extends Map<Integer, V> {

  /** A type-specific {@link Map.Entry}; provides primitive access to the key. */
  interface Entry<V> extends Map.Entry<Integer, V> {

    /** Returns the key of this entry as a primitive int. */
    int getIntKey();

    @Override
    default Integer getKey() {
      return Integer.valueOf(getIntKey());
    }
  }

  /** Sets the value returned for keys that are not present. Defaults to null. */
  void defaultReturnValue(V rv);

  /** Returns the default return value. */
  V defaultReturnValue();

  /** Returns the value mapped to the given key, or the default return value. */
  V get(int key);

  /** Associates the given value with the given key; returns the old value or the default. */
  V put(int key, V value);

  /** Removes the mapping for the given key; returns the old value or the default. */
  V remove(int key);

  /** Returns true if the given key is present. */
  boolean containsKey(int key);

  /** Returns a type-specific view of the mappings of this map. */
  ObjectSet<Entry<V>> int2ObjectEntrySet();

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  default ObjectSet<Map.Entry<Integer, V>> entrySet() {
    return (ObjectSet) int2ObjectEntrySet();
  }
}
