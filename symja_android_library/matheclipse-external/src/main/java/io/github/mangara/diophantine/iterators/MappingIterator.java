/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.mangara.diophantine.iterators;

import java.util.Iterator;
import java.util.function.Function;

/**
 * An iterator that implements a map operation, transforming each value of the underlying iterator
 * in turn.
 *
 * @param <E> the "source" type returned by the underlying iterator
 * @param <T> the "target" type returned by this iterator
 */
public class MappingIterator<E, T> implements Iterator<T> {

  private final Iterator<E> iterator;
  private final Function<E, T> map;

  /**
   * Creates a new iterator that returns map(x) for each element x returned by the given iterator.
   *
   * @param iterator
   * @param map
   */
  public MappingIterator(Iterator<E> iterator, Function<E, T> map) {
    this.iterator = iterator;
    this.map = map;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return map.apply(iterator.next());
  }

}
