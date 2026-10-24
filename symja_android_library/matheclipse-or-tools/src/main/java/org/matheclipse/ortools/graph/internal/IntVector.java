package org.matheclipse.ortools.graph.internal;

import java.util.Arrays;

/** A growable array of <code>int</code> values used as a vector or a stack. */
final class IntVector {
  private int[] data;
  private int size;

  IntVector() {
    this(16);
  }

  IntVector(int capacity) {
    data = new int[Math.max(capacity, 4)];
  }

  int size() {
    return size;
  }

  boolean isEmpty() {
    return size == 0;
  }

  void clear() {
    size = 0;
  }

  void add(int value) {
    if (size == data.length) {
      data = Arrays.copyOf(data, 2 * size);
    }
    data[size++] = value;
  }

  int get(int index) {
    return data[index];
  }

  void set(int index, int value) {
    data[index] = value;
  }

  int back() {
    return data[size - 1];
  }

  int popBack() {
    return data[--size];
  }

  /** Shrinks the vector to <code>newSize</code> elements (which must be at most size()). */
  void truncate(int newSize) {
    size = newSize;
  }

  int[] toArray() {
    return Arrays.copyOf(data, size);
  }
}
