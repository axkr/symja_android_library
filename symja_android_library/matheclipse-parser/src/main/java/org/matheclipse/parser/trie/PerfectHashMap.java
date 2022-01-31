/*
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Open Software License (OSL 3.0) that is bundled with this
 * package in the file LICENSE.txt. It is also available through the world-wide-web at
 * http://opensource.org/licenses/osl-3.0.php If you did not receive a copy of the license and are
 * unable to obtain it through the world-wide-web, please send an email to magnos.software@gmail.com
 * so we can send you a copy immediately. If you use any of this software please notify me via our
 * website or email, your feedback is much appreciated.
 *
 * @copyright Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * 
 * @license http://opensource.org/licenses/osl-3.0.php Open Software License (OSL 3.0)
 */

package org.matheclipse.parser.trie;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A simple map implementation where the keys are integers and are used as direct indices into the
 * map. The minimum and maximum key values define the size of the underlying table and thus should
 * be as near to each other as possible.
 *
 * @author Philip Diffenderfer
 * @param <T> The value type.
 */
@SuppressWarnings("unchecked")
public class PerfectHashMap<T> implements Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  /** The smallest key in the map. */
  private int min;

  /** The difference between the largest key and the smallest key. */
  private int size;

  /** The values in this map. */
  private T[] values;

  /** Instantiates an Empty PerfectHashMap. */
  public PerfectHashMap() {
    clear();
  }

  /**
   * Instantiates a PerfectHashMap with a single entry.
   *
   * @param firstKey The key of the first entry.
   * @param firstValue The value of the first entry.
   */
  public PerfectHashMap(int firstKey, T firstValue) {
    putFirst(firstKey, firstValue);
  }

  /**
   * Determines whether a value exists in this map with the given key.
   *
   * @param key The key of the value to search for.
   * @return True if a non-null value exists for the given key.
   */
  public boolean exists(int key) {
    final int i = relativeIndex(key);

    return (i >= 0 && i < values.length && values[i] != null);
  }

  /**
   * Returns the value associated with the given key.
   *
   * @param key The key of the value to return.
   * @return The value associated with the key, or null if non exists.
   */
  public T get(int key) {
    final int i = relativeIndex(key);

    return (i < 0 || i >= values.length ? null : values[i]);
  }

  /**
   * Puts the key and associated value in this map.
   *
   * @param key The key to use that determines placement of the value.
   * @param value The value to add to the map.
   * @return The previous value with the same key, or null if non existed.
   */
  public T put(int key, T value) {
    if (size == 0) {
      putFirst(key, value);

      return null;
    }

    T previousValue = null;
    final int i = relativeIndex(key);

    if (i < 0) {
      prepend(-i);
      values[0] = value;
      min = key;
      size++;
    } else if (i >= values.length) {
      resize(i + 1);
      values[i] = value;
      size++;
    } else {
      previousValue = values[i];

      if (previousValue == null) {
        size++;
      }

      values[i] = value;
    }

    return previousValue;
  }

  /**
   * Adds a given number of spaces to the beginning of the underlying table.
   *
   * @param spaces The number of spaces to add to the beginning of the table.
   */
  private void prepend(int spaces) {
    final int length = values.length;

    values = Arrays.copyOf(values, length + spaces);

    System.arraycopy(values, 0, values, spaces, length);
  }

  /**
   * Resizes the underlying table to the given size.
   *
   * @param size The new size of the table.
   */
  private void resize(int size) {
    values = Arrays.copyOf(values, size);
  }

  /**
   * Puts the first key/value entry into the map.
   *
   * @param firstKey The key of the first entry.
   * @param firstValue The value of the first entry.
   */
  private void putFirst(int firstKey, T firstValue) {
    min = firstKey;
    values = (T[]) new Object[1];
    values[0] = firstValue;
    size = 1;
  }

  /** Removes all keys and values from the map. */
  public void clear() {
    min = 0;
    values = (T[]) new Object[0];
    size = 0;
  }

  /**
   * Removes the value with the given key.
   *
   * @param key The key of the value to remove.
   * @return True if a value was found with the given key, otherwise false.
   */
  public boolean remove(int key) {
    int i = relativeIndex(key);

    if (size == 1) {
      boolean match = (i == 0);

      if (match) {
        clear();
      }

      return match;
    }

    final int valuesMax = values.length - 1;

    if (i < 0 || i > valuesMax) {
      return false;
    }

    if (i == 0) {
      do {
        i++;
      } while (i <= valuesMax && values[i] == null);

      values = Arrays.copyOfRange(values, i, values.length);
      min += i;
    } else if (i == valuesMax) {
      do {
        i--;
      } while (i > 0 && values[i] == null);

      values = Arrays.copyOf(values, i + 1);
    } else {
      if (values[i] == null) {
        return false;
      }

      values[i] = null;
    }

    size--;

    return true;
  }

  /**
   * Calculates the relative index of a key based on the minimum key value in the map.
   *
   * @param key The key to calculate the relative index to.
   * @return 0 if the key is the minimum key in this map, a positive value less than
   *         {@link #capacity()} of the map otherwise.
   */
  private final int relativeIndex(int key) {
    return (key - min);
  }

  /**
   * Returns the smallest key in the map.
   *
   * @return The smallest key stored in this map.
   */
  public int getMin() {
    return min;
  }

  /**
   * Returns the largest key in the map.
   *
   * @return The largest key stored in this map.
   */
  public int getMax() {
    return min + values.length - 1;
  }

  /**
   * Determines whether there are any entries in this map.
   *
   * @return True if there are no key/values, otherwise false.
   */
  public boolean isEmpty() {
    return (size == 0);
  }

  /**
   * Returns the number of entries in this map.
   *
   * @return The number of entries in this map.
   */
  public int size() {
    return size;
  }

  /**
   * The capacity of the underlying table. This is equivalent to {@link #getMax()} -
   * {@link #getMin()} + 1.
   *
   * @return The current capacity of the map.
   */
  public int capacity() {
    return values.length;
  }

  /**
   * Returns the value at the given index.
   *
   * @param index 0 for the first entry in the map, {@link #capacity()} - 1 for the last entry in
   *        the map. Entries in between these may be null, meaning a value has not been added for
   *        that key.
   * @return The value with the key "{@link #getMin()} + index" or null if that value/key doesn't
   *         exist.
   */
  public T valueAt(int index) {
    return values[index];
  }
}
