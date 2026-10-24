package org.matheclipse.ortools.routing.engine;

import java.util.Arrays;

/**
 * A small open addressing hash map from <code>long</code> keys to <code>int</code> values, used
 * for the arc penalties of the guided local search of the simplified routing engine of this
 * module (which reimplements the behaviour of the OR-Tools routing library).
 */
final class LongIntMap {

  private static final long EMPTY = Long.MIN_VALUE;

  private long[] keys = new long[64];

  private int[] values = new int[64];

  private int size;

  LongIntMap() {
    Arrays.fill(keys, EMPTY);
  }

  private static int mix(long key) {
    long h = key * 0x9E3779B97F4A7C15L;
    return (int) (h ^ (h >>> 32));
  }

  int get(long key) {
    int mask = keys.length - 1;
    int i = mix(key) & mask;
    while (true) {
      long k = keys[i];
      if (k == key) {
        return values[i];
      }
      if (k == EMPTY) {
        return 0;
      }
      i = (i + 1) & mask;
    }
  }

  void increment(long key) {
    if (2 * (size + 1) > keys.length) {
      grow();
    }
    int mask = keys.length - 1;
    int i = mix(key) & mask;
    while (true) {
      long k = keys[i];
      if (k == key) {
        values[i]++;
        return;
      }
      if (k == EMPTY) {
        keys[i] = key;
        values[i] = 1;
        size++;
        return;
      }
      i = (i + 1) & mask;
    }
  }

  void clear() {
    Arrays.fill(keys, EMPTY);
    size = 0;
  }

  boolean isEmpty() {
    return size == 0;
  }

  private void grow() {
    long[] oldKeys = keys;
    int[] oldValues = values;
    keys = new long[oldKeys.length * 2];
    values = new int[oldKeys.length * 2];
    Arrays.fill(keys, EMPTY);
    size = 0;
    int mask = keys.length - 1;
    for (int j = 0; j < oldKeys.length; j++) {
      if (oldKeys[j] != EMPTY) {
        int i = mix(oldKeys[j]) & mask;
        while (keys[i] != EMPTY) {
          i = (i + 1) & mask;
        }
        keys[i] = oldKeys[j];
        values[i] = oldValues[j];
        size++;
      }
    }
  }
}
