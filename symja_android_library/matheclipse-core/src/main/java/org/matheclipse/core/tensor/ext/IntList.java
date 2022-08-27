// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** unmodifiable list wrapped around an array of primitive ints.
 * Any sublist is backed by a corresponding part of the same array.
 * Outside modification of the array are reflected by the list.
 * 
 * <p>The implementation of the {@link List} interface assumes that
 * the "list does not permit null elements". That means, that calls
 * to certain functions with parameter "null" result in a
 * {@link NullPointerException}. Example: {@link #contains(Object)}.
 * 
 * <p>A sublist of an {@link IntList} is {@link Serializable}. This
 * is in contrast to a sublist of {@link Arrays#asList(Object...)}
 * or {@link ArrayList}, which are not serializable. */
/* package */ class IntList implements List<Integer>, RandomAccess, Serializable {

  /** @param array non-null
   * @return unmodifiable list */
  public static List<Integer> wrap(int[] array) {
    return new IntList(array, 0, array.length);
  }

  // ---
  private final int[] array;
  private final int ofs;
  private final int len;

  /** @param array non-null
   * @param ofs non-negative
   * @param len non-negative */
  private IntList(int[] array, int ofs, int len) {
    this.array = array;
    this.ofs = ofs;
    this.len = len;
  }

  @Override // from List
  public int size() {
    return len;
  }

  @Override // from List
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override // from List
  public Integer get(int index) {
    if (0 <= index && index < len)
      return array[ofs + index];
    throw new IllegalArgumentException("index=" + index + " size=" + len);
  }

  @Override // from List
  public IntList subList(int fromIndex, int toIndex) {
    if (fromIndex < 0) // asserts that 0 <= fromIndex
      throw new IllegalArgumentException();
    if (len < toIndex) // asserts that toIndex <= len
      throw new IllegalArgumentException();
    int dif = Math.subtractExact(toIndex, fromIndex);
    if (dif < 0) // asserts that fromIndex <= toIndex
      throw new IllegalArgumentException();
    // implied: 0 <= fromIndex <= len
    // implied: fromIndex + dif <= len because fromIndex + toIndex - fromIndex <= len is toIndex <= len
    return new IntList(array, ofs + fromIndex, dif);
  }

  @Override // from List
  public Stream<Integer> stream() {
    return Arrays.stream(array).skip(ofs).limit(len).boxed();
  }

  @Override // from List
  public Object[] toArray() {
    return stream().toArray();
  }

  @Override // from List
  public Iterator<Integer> iterator() {
    return listIterator();
  }

  @Override // from List
  public ListIterator<Integer> listIterator() {
    return listIterator(0);
  }

  @Override // from List
  public ListIterator<Integer> listIterator(int index) {
    // "throws exception if the index is out of range index < 0 || index > size()"
    if (index < 0 || size() < index)
      throw new IllegalArgumentException();
    return new ListIterator<>() {
      int count = index;

      @Override
      public boolean hasNext() {
        return count < len;
      }

      @Override
      public Integer next() {
        if (hasNext())
          return array[ofs + (count++)];
        throw new NoSuchElementException();
      }

      @Override
      public boolean hasPrevious() {
        return 0 < count;
      }

      @Override
      public Integer previous() {
        if (hasPrevious())
          return array[ofs + (--count)];
        throw new NoSuchElementException();
      }

      @Override
      public int nextIndex() {
        return count + 1;
      }

      @Override
      public int previousIndex() {
        return count - 1;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void set(Integer e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void add(Integer e) {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override // from List
  public int indexOf(Object object) {
    for (int index = 0; index < len; ++index)
      if (object.equals(array[ofs + index]))
        return index;
    Objects.requireNonNull(object);
    return -1;
  }

  @Override // from List
  public int lastIndexOf(Object object) {
    for (int index = len - 1; 0 <= index; --index)
      if (object.equals(array[ofs + index]))
        return index;
    Objects.requireNonNull(object);
    return -1;
  }

  @Override // from List
  public boolean contains(Object object) {
    // throws exception if object == null even if stream is empty
    return stream().anyMatch(object::equals);
  }

  @Override // from List
  public boolean containsAll(Collection<?> collection) {
    return collection.stream().allMatch(this::contains);
  }

  @Override // from List
  public int hashCode() {
    int hashCode = 1;
    for (int i = ofs; i < ofs + len; ++i)
      hashCode = 31 * hashCode + array[i];
    return hashCode;
  }

  @Override // from List
  public boolean equals(Object object) {
    if (object instanceof List<?> && ((List<?>) object).size() == len) {
      AtomicInteger atomicInteger = new AtomicInteger(ofs);
      return ((List<?>) object).stream()
          .allMatch(value -> value.equals(array[atomicInteger.getAndIncrement()]));
    }
    return false;
  }

  @Override // from List
  public String toString() {
    return stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
  }

  // ---
  @Override // from List
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean add(Integer e) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean addAll(Collection<? extends Integer> c) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean addAll(int index, Collection<? extends Integer> c) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public Integer set(int index, Integer element) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public void add(int index, Integer element) {
    throw new UnsupportedOperationException();
  }

  @Override // from List
  public Integer remove(int index) {
    throw new UnsupportedOperationException();
  }
}
