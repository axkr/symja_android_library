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
// Slim replacement for it.unimi.dsi.fastutil.ints.IntIterator, adapted from fastutil 8.5.19
// (https://fastutil.di.unimi.it/). Only the subset used by Symja is implemented.
package org.matheclipse.external.fastutil.ints;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** A type-specific {@link Iterator} that avoids boxing of the returned values. */
public interface IntIterator extends Iterator<Integer> {

  /** Returns the next element as a primitive type. */
  int nextInt();

  @Override
  default Integer next() {
    return Integer.valueOf(nextInt());
  }

  default void forEachRemaining(final IntConsumer action) {
    while (hasNext()) {
      action.accept(nextInt());
    }
  }

  @Override
  default void forEachRemaining(final Consumer<? super Integer> action) {
    forEachRemaining(action instanceof IntConsumer ? (IntConsumer) action
        : (IntConsumer) action::accept);
  }

  /** Skips the given number of elements and returns the number of elements actually skipped. */
  default int skip(final int n) {
    int i = n;
    while (i-- != 0 && hasNext()) {
      nextInt();
    }
    return n - i - 1;
  }
}
