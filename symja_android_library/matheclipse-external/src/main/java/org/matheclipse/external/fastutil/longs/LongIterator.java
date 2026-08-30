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
// Slim replacement for it.unimi.dsi.fastutil.longs.LongIterator, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/). Only the subset used by Symja is implemented.
package org.matheclipse.external.fastutil.longs;

import java.util.Iterator;
import java.util.function.LongConsumer;

/** A type-specific {@link Iterator} that avoids boxing of the returned values. */
public interface LongIterator extends Iterator<Long> {

  /** Returns the next element as a primitive type. */
  long nextLong();

  @Override
  default Long next() {
    return Long.valueOf(nextLong());
  }

  default void forEachRemaining(final LongConsumer action) {
    while (hasNext()) {
      action.accept(nextLong());
    }
  }
}
