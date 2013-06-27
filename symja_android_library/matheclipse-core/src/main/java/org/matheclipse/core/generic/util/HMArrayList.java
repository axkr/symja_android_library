/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.matheclipse.core.generic.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * HMArrayList is an implementation of {@link List}, backed by an array. All
 * optional operations adding, removing, and replacing are supported. The
 * elements can be any objects.
 * 
 * Copied and modified from the Apache Harmony project.
 * 
 * @since 1.2
 */
public class HMArrayList<E> extends AbstractList<E> implements List<E>, Cloneable, Serializable, RandomAccess {

	private static final long serialVersionUID = 8683452581122892189L;

	private transient int firstIndex;

	private transient int lastIndex;

	private transient E[] array;

	protected HMArrayList(E[] array) {
		this.array = array;
		firstIndex = 0;
		lastIndex = modCount = array.length;
	}

	/**
	 * Constructs a new instance of {@code ArrayList} with ten capacity.
	 */
	public HMArrayList() {
		this(10);
	}

	public HMArrayList(E ex, E... es) {
		int len = es.length + 1;
		firstIndex = 0;
		array = newElementArray(len);
		array[0] = ex;
		System.arraycopy(es, 0, array, 1, es.length);
		lastIndex = modCount = len;
	}

	/**
	 * Constructs a new instance of {@code ArrayList} with the specified
	 * capacity.
	 * 
	 * @param capacity
	 *            the initial capacity of this {@code ArrayList}.
	 */
	public HMArrayList(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}
		firstIndex = lastIndex = 0;
		array = newElementArray(capacity);
	}

	/**
	 * Constructs a new instance of {@code ArrayList} containing the elements of
	 * the specified collection. The initial size of the {@code ArrayList} will
	 * be 10% higher than the size of the specified collection.
	 * 
	 * @param collection
	 *            the collection of elements to add.
	 */
	public HMArrayList(Collection<? extends E> collection) {
		firstIndex = 0;
		Object[] objects = collection.toArray();
		int size = objects.length;
		array = newElementArray(size + (size / 10));
		System.arraycopy(objects, 0, array, 0, size);
		lastIndex = size;
		modCount = 1;
	}

	@SuppressWarnings("unchecked")
	private E[] newElementArray(int size) {
		return (E[]) new Object[size];
	}

	/**
	 * Inserts the specified object into this {@code ArrayList} at the specified
	 * location. The object is inserted before any previous element at the
	 * specified location. If the location is equal to the size of this
	 * {@code ArrayList}, the object is added at the end.
	 * 
	 * @param location
	 *            the index at which to insert the object.
	 * @param object
	 *            the object to add.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	@Override
	public void add(int location, E object) {
		int size = lastIndex - firstIndex;
		if (0 < location && location < size) {
			if (firstIndex == 0 && lastIndex == array.length) {
				growForInsert(location, 1);
			} else if ((location < size / 2 && firstIndex > 0) || lastIndex == array.length) {
				System.arraycopy(array, firstIndex, array, --firstIndex, location);
			} else {
				int index = location + firstIndex;
				System.arraycopy(array, index, array, index + 1, size - location);
				lastIndex++;
			}
			array[location + firstIndex] = object;
		} else if (location == 0) {
			if (firstIndex == 0) {
				growAtFront(1);
			}
			array[--firstIndex] = object;
		} else if (location == size) {
			if (lastIndex == array.length) {
				growAtEnd(1);
			}
			array[lastIndex++] = object;
		} else {
			// throw new IndexOutOfBoundsException(
			// luni.0A=Index: {0}, Size: {1}
			//                Messages.getString("luni.0A", //$NON-NLS-1$
			// Integer.valueOf(location), Integer.valueOf(lastIndex -
			// firstIndex)));
			throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
					+ Integer.valueOf(lastIndex - firstIndex));
		}

		modCount++;
	}

	/**
	 * Adds the specified object at the end of this {@code ArrayList}.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	@Override
	public boolean add(E object) {
		if (lastIndex == array.length) {
			growAtEnd(1);
		}
		array[lastIndex++] = object;
		modCount++;
		return true;
	}

	/**
	 * Inserts the objects in the specified collection at the specified location
	 * in this List. The objects are added in the order they are returned from
	 * the collection's iterator.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false}
	 *         otherwise.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	@Override
	public boolean addAll(int location, Collection<? extends E> collection) {
		int size = lastIndex - firstIndex;
		if (location < 0 || location > size) {
			// throw new IndexOutOfBoundsException(
			// luni.0A=Index: {0}, Size: {1}
			//                Messages.getString("luni.0A", //$NON-NLS-1$
			// Integer.valueOf(location), Integer.valueOf(lastIndex -
			// firstIndex)));
			throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
					+ Integer.valueOf(lastIndex - firstIndex));
		}
		if (this == collection) {
			collection = (HMArrayList) clone();
		}
		Object[] dumparray = collection.toArray();
		int growSize = dumparray.length;
		if (growSize == 0) {
			return false;
		}

		if (0 < location && location < size) {
			if (array.length - size < growSize) {
				growForInsert(location, growSize);
			} else if ((location < size / 2 && firstIndex > 0) || lastIndex > array.length - growSize) {
				int newFirst = firstIndex - growSize;
				if (newFirst < 0) {
					int index = location + firstIndex;
					System.arraycopy(array, index, array, index - newFirst, size - location);
					lastIndex -= newFirst;
					newFirst = 0;
				}
				System.arraycopy(array, firstIndex, array, newFirst, location);
				firstIndex = newFirst;
			} else {
				int index = location + firstIndex;
				System.arraycopy(array, index, array, index + growSize, size - location);
				lastIndex += growSize;
			}
		} else if (location == 0) {
			growAtFront(growSize);
			firstIndex -= growSize;
		} else if (location == size) {
			if (lastIndex > array.length - growSize) {
				growAtEnd(growSize);
			}
			lastIndex += growSize;
		}

		System.arraycopy(dumparray, 0, this.array, location + firstIndex, growSize);
		modCount++;
		return true;
	}

	/**
	 * Adds the objects in the specified collection to this {@code ArrayList}.
	 * 
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean addAll(Collection<? extends E> collection) {
		Object[] dumpArray = collection.toArray();
		if (dumpArray.length == 0) {
			return false;
		}
		if (dumpArray.length > array.length - lastIndex) {
			growAtEnd(dumpArray.length);
		}
		System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
		lastIndex += dumpArray.length;
		modCount++;
		return true;
	}

	/**
	 * Removes all elements from this {@code ArrayList}, leaving it empty.
	 * 
	 * @see #isEmpty
	 * @see #size
	 */
	@Override
	public void clear() {
		if (firstIndex != lastIndex) {
			Arrays.fill(array, firstIndex, lastIndex, null);
			firstIndex = lastIndex = 0;
			modCount++;
		}
	}

	/**
	 * Returns a new {@code ArrayList} with the same elements, the same size and
	 * the same capacity as this {@code ArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			HMArrayList<E> newList = (HMArrayList<E>) super.clone();
 			newList.array = array.clone();
			return newList;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Searches this {@code ArrayList} for the specified object.
	 * 
	 * @param object
	 *            the object to search for.
	 * @return {@code true} if {@code object} is an element of this
	 *         {@code ArrayList}, {@code false} otherwise
	 */
	@Override
	public boolean contains(Object object) {
		if (object != null) {
			for (int i = firstIndex; i < lastIndex; i++) {
				if (object.equals(array[i])) {
					return true;
				}
			}
		} else {
			for (int i = firstIndex; i < lastIndex; i++) {
				if (array[i] == null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Ensures that after this operation the {@code ArrayList} can hold the
	 * specified number of elements without further growing.
	 * 
	 * @param minimumCapacity
	 *            the minimum capacity asked for.
	 */
	public void ensureCapacity(int minimumCapacity) {
		if (array.length < minimumCapacity) {
			if (firstIndex > 0) {
				growAtFront(minimumCapacity - array.length);
			} else {
				growAtEnd(minimumCapacity - array.length);
			}
		}
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list equals
	 * <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @return
	 */
	public final boolean isSameHead(E head) {
		return array[firstIndex] == head;
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list equals
	 * <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	public final boolean isSameHead(E head, int length) {
		return array[firstIndex] == head && length == (lastIndex - firstIndex);
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list is greater or
	 * equal <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	public final boolean isSameHeadSizeGE(E head, int length) {
		return array[firstIndex] == head && length <= (lastIndex - firstIndex);
	}

	@Override
	public E get(int location) {
		if (0 <= location && location < (lastIndex - firstIndex)) {
			return array[firstIndex + location];
		}
		// throw new IndexOutOfBoundsException(
		// luni.0A=Index: {0}, Size: {1}
		//            Messages.getString("luni.0A", //$NON-NLS-1$
		// Integer.valueOf(location), Integer.valueOf(lastIndex -
		// firstIndex)));
		throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
				+ Integer.valueOf(lastIndex - firstIndex));

	}

	private void growAtEnd(int required) {
		int size = lastIndex - firstIndex;
		if (firstIndex >= required - (array.length - lastIndex)) {
			int newLast = lastIndex - firstIndex;
			if (size > 0) {
				System.arraycopy(array, firstIndex, array, 0, size);
				int start = newLast < firstIndex ? firstIndex : newLast;
				Arrays.fill(array, start, array.length, null);
			}
			firstIndex = 0;
			lastIndex = newLast;
		} else {
			int increment = size / 2;
			if (required > increment) {
				increment = required;
			}
			if (increment < 12) {
				increment = 12;
			}
			E[] newArray = newElementArray(size + increment);
			if (size > 0) {
				System.arraycopy(array, firstIndex, newArray, 0, size);
				firstIndex = 0;
				lastIndex = size;
			}
			array = newArray;
		}
	}

	private void growAtFront(int required) {
		int size = lastIndex - firstIndex;
		if (array.length - lastIndex + firstIndex >= required) {
			int newFirst = array.length - size;
			if (size > 0) {
				System.arraycopy(array, firstIndex, array, newFirst, size);
				int length = firstIndex + size > newFirst ? newFirst : firstIndex + size;
				Arrays.fill(array, firstIndex, length, null);
			}
			firstIndex = newFirst;
			lastIndex = array.length;
		} else {
			int increment = size / 2;
			if (required > increment) {
				increment = required;
			}
			if (increment < 12) {
				increment = 12;
			}
			E[] newArray = newElementArray(size + increment);
			if (size > 0) {
				System.arraycopy(array, firstIndex, newArray, newArray.length - size, size);
			}
			firstIndex = newArray.length - size;
			lastIndex = newArray.length;
			array = newArray;
		}
	}

	private void growForInsert(int location, int required) {
		int size = lastIndex - firstIndex;
		int increment = size / 2;
		if (required > increment) {
			increment = required;
		}
		if (increment < 12) {
			increment = 12;
		}
		E[] newArray = newElementArray(size + increment);
		int newFirst = increment - required;
		// Copy elements after location to the new array skipping inserted
		// elements
		System.arraycopy(array, location + firstIndex, newArray, newFirst + location + required, size - location);
		// Copy elements before location to the new array from firstIndex
		System.arraycopy(array, firstIndex, newArray, newFirst, location);
		firstIndex = newFirst;
		lastIndex = size + increment;

		array = newArray;
	}

	@Override
	public int indexOf(Object object) {
		if (object != null) {
			for (int i = firstIndex; i < lastIndex; i++) {
				if (object.equals(array[i])) {
					return i - firstIndex;
				}
			}
		} else {
			for (int i = firstIndex; i < lastIndex; i++) {
				if (array[i] == null) {
					return i - firstIndex;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return lastIndex == firstIndex;
	}

	@Override
	public int lastIndexOf(Object object) {
		if (object != null) {
			for (int i = lastIndex - 1; i >= firstIndex; i--) {
				if (object.equals(array[i])) {
					return i - firstIndex;
				}
			}
		} else {
			for (int i = lastIndex - 1; i >= firstIndex; i--) {
				if (array[i] == null) {
					return i - firstIndex;
				}
			}
		}
		return -1;
	}

	/**
	 * Removes the object at the specified location from this list.
	 * 
	 * @param location
	 *            the index of the object to remove.
	 * @return the removed object.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || >= size()}
	 */
	@Override
	public E remove(int location) {
		E result;
		int size = lastIndex - firstIndex;
		if (0 <= location && location < size) {
			if (location == size - 1) {
				result = array[--lastIndex];
				array[lastIndex] = null;
			} else if (location == 0) {
				result = array[firstIndex];
				array[firstIndex++] = null;
			} else {
				int elementIndex = firstIndex + location;
				result = array[elementIndex];
				if (location < size / 2) {
					System.arraycopy(array, firstIndex, array, firstIndex + 1, location);
					array[firstIndex++] = null;
				} else {
					System.arraycopy(array, elementIndex + 1, array, elementIndex, size - location - 1);
					array[--lastIndex] = null;
				}
			}
			if (firstIndex == lastIndex) {
				firstIndex = lastIndex = 0;
			}
		} else {
			// throw new IndexOutOfBoundsException(
			// luni.0A=Index: {0}, Size: {1}
			//                Messages.getString("luni.0A", //$NON-NLS-1$
			// Integer.valueOf(location), Integer.valueOf(lastIndex -
			// firstIndex)));
			throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
					+ Integer.valueOf(lastIndex - firstIndex));
		}

		modCount++;
		return result;
	}

	@Override
	public boolean remove(Object object) {
		int location = indexOf(object);
		if (location >= 0) {
			remove(location);
			return true;
		}
		return false;
	}

	/**
	 * Removes the objects in the specified range from the start to the end, but
	 * not including the end index.
	 * 
	 * @param start
	 *            the index at which to start removing.
	 * @param end
	 *            the index one after the end of the range to remove.
	 * @throws IndexOutOfBoundsException
	 *             when {@code start < 0, start > end} or {@code end > size()}
	 */
	@Override
	protected void removeRange(int start, int end) {
		if (start >= 0 && start <= end && end <= (lastIndex - firstIndex)) {
			if (start == end) {
				return;
			}
			int size = lastIndex - firstIndex;
			if (end == size) {
				Arrays.fill(array, firstIndex + start, lastIndex, null);
				lastIndex = firstIndex + start;
			} else if (start == 0) {
				Arrays.fill(array, firstIndex, firstIndex + end, null);
				firstIndex += end;
			} else {
				System.arraycopy(array, firstIndex + end, array, firstIndex + start, size - end);
				int newLast = lastIndex + start - end;
				Arrays.fill(array, newLast, lastIndex, null);
				lastIndex = newLast;
			}
			modCount++;
		} else {
			// throw new IndexOutOfBoundsException(
			// luni.0B=Array index out of range: {0}
			//                Messages.getString("luni.0B", //$NON-NLS-1$
			// lastIndex - firstIndex - end));
			throw new IndexOutOfBoundsException("Index: " + (lastIndex - firstIndex - end));
		}
	}

	/**
	 * Replaces the element at the specified location in this {@code ArrayList}
	 * with the specified object.
	 * 
	 * @param location
	 *            the index at which to put the specified object.
	 * @param object
	 *            the object to add.
	 * @return the previous element at the index.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || >= size()}
	 */
	@Override
	public E set(int location, E object) {
		if (0 <= location && location < (lastIndex - firstIndex)) {
			E result = array[firstIndex + location];
			array[firstIndex + location] = object;
			return result;
		}
		throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
				+ Integer.valueOf(lastIndex - firstIndex));
	}

	/**
	 * Returns the number of elements in this {@code ArrayList}.
	 * 
	 * @return the number of elements in this {@code ArrayList}.
	 */
	@Override
	public int size() {
		return lastIndex - firstIndex;
	}

	/**
	 * Returns a new array containing all elements contained in this
	 * {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public Object[] toArray() {
		int size = lastIndex - firstIndex;
		Object[] result = new Object[size];
		System.arraycopy(array, firstIndex, result, 0, size);
		return result;
	}

	/**
	 * Returns an array containing all elements contained in this
	 * {@code ArrayList}. If the specified array is large enough to hold the
	 * elements, the specified array is used, otherwise an array of the same
	 * type is created. If the specified array is used and is larger than this
	 * {@code ArrayList}, the array element following the collection elements is
	 * set to null.
	 * 
	 * @param contents
	 *            the array.
	 * @return an array of the elements from this {@code ArrayList}.
	 * @throws ArrayStoreException
	 *             when the type of an element in this {@code ArrayList} cannot
	 *             be stored in the type of the specified array.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] contents) {
		int size = lastIndex - firstIndex;
		if (size > contents.length) {
			Class<?> ct = contents.getClass().getComponentType();
			contents = (T[]) Array.newInstance(ct, size);
		}
		System.arraycopy(array, firstIndex, contents, 0, size);
		if (size < contents.length) {
			contents[size] = null;
		}
		return contents;
	}

	/**
	 * Sets the capacity of this {@code ArrayList} to be the same as the current
	 * size.
	 * 
	 * @see #size
	 */
	public void trimToSize() {
		int size = lastIndex - firstIndex;
		E[] newArray = newElementArray(size);
		System.arraycopy(array, firstIndex, newArray, 0, size);
		array = newArray;
		firstIndex = 0;
		lastIndex = array.length;
		modCount = 0;
	}

	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("size", Integer.TYPE) }; //$NON-NLS-1$

	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		fields.put("size", lastIndex - firstIndex); //$NON-NLS-1$
		stream.writeFields();
		stream.writeInt(array.length);
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			stream.writeObject(it.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = stream.readFields();
		lastIndex = fields.get("size", 0); //$NON-NLS-1$
		array = newElementArray(stream.readInt());
		for (int i = 0; i < lastIndex; i++) {
			array[i] = (E) stream.readObject();
		}
	}
}
