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

package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import org.matheclipse.core.builtin.constant.E;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * HMArrayList is an implementation of {@link List}, backed by an array. All
 * optional operations adding, removing, and replacing are supported. The
 * elements can be any objects.
 * 
 * Copied and modified from the Apache Harmony project.
 * 
 */
public abstract class HMArrayList extends AbstractAST implements Cloneable, Serializable, RandomAccess {

	private static final long serialVersionUID = 8683452581122892189L;

	protected transient IExpr[] array;

	transient int firstIndex;

	protected transient int lastIndex;

	/**
	 * Constructs a new instance of {@code ArrayList} with ten capacity.
	 */
	public HMArrayList() {
		this(10);
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
		firstIndex = hashValue = 0;
		Object[] objects = collection.toArray();
		int size = objects.length;
		array = newElementArray(size + (size / 10));
		System.arraycopy(objects, 0, array, 0, size);
		lastIndex = size;
	}

	public HMArrayList(IExpr ex, IExpr... es) {
		int len = es.length + 1;
		firstIndex = hashValue = 0;
		array = newElementArray(len);
		array[0] = ex;
		System.arraycopy(es, 0, array, 1, es.length);
		lastIndex = len;
	}

	protected HMArrayList(IExpr[] array) {
		init(array);
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
		firstIndex = lastIndex = hashValue = 0;
		array = newElementArray(capacity);
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
	public boolean appendAll(Collection<? extends IExpr> collection) {
		hashValue = 0;
		Object[] dumpArray = collection.toArray();
		if (dumpArray.length == 0) {
			return false;
		}
		if (dumpArray.length > array.length - lastIndex) {
			growAtEnd(dumpArray.length);
		}
		System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
		lastIndex += dumpArray.length;
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
	public boolean appendAll(int location, Collection<? extends IExpr> collection) {
		hashValue = 0;
		int size = lastIndex - firstIndex;
		if (location < 0 || location > size) {
			throw new IndexOutOfBoundsException(
					"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
		}
		if (this == collection) {
			collection = clone().args();
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
		return true;
	}

	/**
	 * Adds the specified object at the end of this {@code ArrayList}.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	@Override
	public boolean append(IExpr object) {
		hashValue = 0;
		if (lastIndex == array.length) {
			growAtEnd(1);
		}
		array[lastIndex++] = object;
		return true;
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
	public void append(int location, IExpr object) {
		hashValue = 0;
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
			throw new IndexOutOfBoundsException(
					"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
		}

	}

	/**
	 * Get the first argument (i.e. the second element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(1) ). <br />
	 * <b>Example:</b> for the AST representing the expression
	 * <code>Sin(x)</code>, <code>arg1()</code> returns <code>x</code>.
	 * 
	 * @return the first argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public final IExpr arg1() {
		return array[firstIndex + 1];
	}

	/**
	 * Get the second argument (i.e. the third element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(2) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>x^y</code>
	 * (i.e. <code>Power(x, y)</code>), <code>arg2()</code> returns
	 * <code>y</code>.
	 * 
	 * @return the second argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public final IExpr arg2() {
		return array[firstIndex + 2];
	}

	/**
	 * Get the third argument (i.e. the fourth element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(3) ).<br />
	 * <b>Example:</b> for the AST representing the expression
	 * <code>f(a, b, c)</code>, <code>arg3()</code> returns <code>c</code>.
	 * 
	 * @return the third argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public final IExpr arg3() {
		return array[firstIndex + 3];
	}

	/**
	 * Get the fourth argument (i.e. the fifth element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(4) ).<br />
	 * <b>Example:</b> for the AST representing the expression
	 * <code>f(a, b ,c, d)</code>, <code>arg4()</code> returns <code>d</code>.
	 * 
	 * @return the fourth argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public final IExpr arg4() {
		return array[firstIndex + 4];
	}

	/**
	 * Get the fifth argument (i.e. the sixth element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(5) ).<br />
	 * <b>Example:</b> for the AST representing the expression
	 * <code>f(a, b ,c, d, e)</code>, <code>arg5()</code> returns <code>e</code>
	 * .
	 * 
	 * @return the fifth argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public final IExpr arg5() {
		return array[firstIndex + 5];
	}

	@Override
	public Set<IExpr> asSet() {
		int size = size();
		Set<IExpr> set = new HashSet<IExpr>(size > 16 ? size : 16);
		for (int i = 1; i < size; i++) {
			set.add(array[firstIndex + i]);
		}
		return set;
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
		}
		hashValue = 0;
	}

	/**
	 * Returns a new {@code HMArrayList} with the same elements, the same size
	 * and the same capacity as this {@code HMArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IAST clone() {
		HMArrayList newList = (HMArrayList) super.clone();
		newList.array = array.clone();
		newList.hashValue = 0;
		return newList;
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

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof HMArrayList) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			HMArrayList list = (HMArrayList) obj;
			int size = lastIndex - firstIndex;
			if (size != list.size()) {
				return false;
			}
			int i1 = firstIndex;
			int i2 = list.firstIndex;
			for (int i = 0; i < size; i++) {
				if (array[i1++].equals(list.array[i2++])) {
					continue;
				}
				return false;
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public IExpr get(int location) {
		int index;
		if ((index = firstIndex + location) < lastIndex) {
			return array[index];
		}
		throw new IndexOutOfBoundsException(
				"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
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
			IExpr[] newArray = newElementArray(size + increment);
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
			IExpr[] newArray = newElementArray(size + increment);
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
		IExpr[] newArray = newElementArray(size + increment);
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
	public final IExpr head() {
		return array[firstIndex];
	}

	/**
	 * Searches this list for the specified object and returns the index of the
	 * first occurrence.
	 * 
	 * @param object
	 *            the object to search for.
	 * @return the index of the first occurrence of the object, or -1 if it was
	 *         not found.
	 */
	public int indexOf(IExpr object) {
		Iterator<IExpr> it = iterator();
		int indx=0;
		if (object != null) {
			while (it.hasNext()) {
				if (object.equals(it.next())) {
					return indx;
				}
				indx++;
			}
		} else {
			while (it.hasNext()) {
				if (it.next() == null) {
					return indx;
				}
				indx++;
			}
		}
		return -1;
	}
	
	protected final void init(IExpr[] array) {
		this.array = array;
		firstIndex = hashValue = 0;
		lastIndex = array.length;
	}

	private IExpr[] newElementArray(int size) {
		return new IExpr[size];
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = stream.readFields();
		lastIndex = fields.get("size", 0); //$NON-NLS-1$
		array = newElementArray(lastIndex);
		for (int i = 0; i < lastIndex; i++) {
			array[i] = (IExpr) stream.readObject();
		}
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
	public IExpr remove(int location) {
		hashValue = 0;
		IExpr result;
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
			throw new IndexOutOfBoundsException(
					"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
		}

		return result;
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
	protected void removeRange(int start, int end) {
		hashValue = 0;
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
		} else {
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
	public IExpr set(int location, IExpr object) {
		hashValue = 0;
		if (0 <= location && location < (lastIndex - firstIndex)) {
			IExpr result = array[firstIndex + location];
			array[firstIndex + location] = object;
			return result;
		}
		throw new IndexOutOfBoundsException(
				"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
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
	public IExpr[] toArray() {
		int size = lastIndex - firstIndex;
		IExpr[] result = new IExpr[size];
		System.arraycopy(array, firstIndex, result, 0, size);
		return result;
	}

	/**
	 * Sets the capacity of this {@code ArrayList} to be the same as the current
	 * size.
	 * 
	 * @see #size
	 */
	public void trimToSize() {
		int size = lastIndex - firstIndex;
		IExpr[] newArray = newElementArray(size);
		System.arraycopy(array, firstIndex, newArray, 0, size);
		array = newArray;
		firstIndex = 0;
		lastIndex = array.length;
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		int size = lastIndex - firstIndex;
		fields.put("size", size); //$NON-NLS-1$
		stream.writeFields();
		// don't use an iterator here!
		for (int i = 0; i < size; i++) {
			stream.writeObject(get(i));
		}
	}

}
