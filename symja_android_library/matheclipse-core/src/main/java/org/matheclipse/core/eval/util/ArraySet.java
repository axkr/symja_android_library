package org.matheclipse.core.eval.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

import org.matheclipse.core.interfaces.ISymbol;

/**
 * A set implementation for very small sets.
 * 
 * @param <T>
 */
public class ArraySet<T> extends AbstractSet<T> implements Iterable<T> {
	private int size = 0;
	private final int capacity;
	private Object[] array;

	public ArraySet() {
		this(3);
	}

	public ArraySet(int capacity) {
		this.capacity = capacity;
		this.array = null;
	}

	public final boolean add(T key) {
		if (array == null) {
			array = new Object[capacity];
			array[size++] = key;
			return true;
		}
		int index = Arrays.binarySearch(array, 0, size, key);
		if (index < 0) {
			int insertIndex = -index - 1;

			if (size < array.length - 1) {
				if (insertIndex < size) {
					System.arraycopy(array, insertIndex, array, insertIndex + 1, size - insertIndex);
				}
				array[insertIndex] = key;
			} else {
				Object[] newArray = new Object[array.length + 1];
				System.arraycopy(array, 0, newArray, 0, insertIndex);
				System.arraycopy(array, insertIndex, newArray, insertIndex + 1, array.length - insertIndex);
				newArray[insertIndex] = key;
				array = newArray;
			}

			size++;
			return true;
		}
		return false;
	}

	public final T get(int position) {
		return array == null ? null : (T) array[position];
	}

	public final int size() {
		return size;
	}

	public final boolean contains(ISymbol key) {
		return array == null ? false : Arrays.binarySearch(array, 0, size, key) >= 0;
	}

	@Override
	public final Iterator<T> iterator() {
		Iterator<T> iter = new Iterator<T>() {
			int offset = 0;

			@Override
			public boolean hasNext() {
				return offset < size;
			}

			@Override
			public T next() {
				if (offset < size) {
					return (T) array[offset++];
				}
				return null;
			}

		};
		return iter;
	}
}