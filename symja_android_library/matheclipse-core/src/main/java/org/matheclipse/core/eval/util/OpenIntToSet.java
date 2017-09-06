package org.matheclipse.core.eval.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Open addressed map from int to Set<T>.
 * <p>
 * This class provides a dedicated map from integers to Sets with a much smaller
 * memory overhead than standard <code>java.util.Set</code>.
 * </p>
 * <p>
 * This class is not synchronized. The specialized iterators returned by
 * {@link #iterator()} are fail-fast: they throw a
 * <code>ConcurrentModificationException</code> when they detect the map has
 * been modified during iteration.
 * </p>
 * <p>
 * Copied and modified from
 * <code>org.apache.commons.math4.util-OpenIntToFieldHashMap</code>
 * </p>
 *
 * @param <T>
 *            the type of the set elements
 */
public class OpenIntToSet<T> implements Serializable {

	/** Iterator class for the map. */
	public class Iterator {

		/** Reference modification count. */
		private final int referenceCount;

		/** Index of current element. */
		private int current;

		/** Index of next element. */
		private int next;

		/**
		 * Simple constructor.
		 */
		private Iterator() {

			// preserve the modification count of the map to detect concurrent
			// modifications later
			referenceCount = count;

			// initialize current index
			next = -1;
			try {
				advance();
			} catch (NoSuchElementException nsee) { // NOPMD
				// ignored
			}

		}

		/**
		 * Advance iterator one step further.
		 *
		 * @exception ConcurrentModificationException
		 *                if the map is modified during iteration
		 * @exception NoSuchElementException
		 *                if there is no element left in the map
		 */
		public void advance() throws ConcurrentModificationException, NoSuchElementException {

			if (referenceCount != count) {
				throw new ConcurrentModificationException();
			}

			// advance on step
			current = next;

			// prepare next step
			try {
				while (states[++next] != FULL) { // NOPMD
					// nothing to do
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				next = -2;
				if (current < 0) {
					throw new NoSuchElementException();
				}
			}

		}

		/**
		 * Check if there is a next element in the map.
		 *
		 * @return true if there is a next element
		 */
		public boolean hasNext() {
			return next >= 0;
		}

		/**
		 * Get the key of current entry.
		 *
		 * @return key of current entry
		 * @exception ConcurrentModificationException
		 *                if the map is modified during iteration
		 * @exception NoSuchElementException
		 *                if there is no element left in the map
		 */
		public int key() throws ConcurrentModificationException, NoSuchElementException {
			if (referenceCount != count) {
				throw new ConcurrentModificationException();
			}
			if (current < 0) {
				throw new NoSuchElementException();
			}
			return keys[current];
		}

		/**
		 * Get the value of current entry.
		 *
		 * @return value of current entry
		 * @exception ConcurrentModificationException
		 *                if the map is modified during iteration
		 * @exception NoSuchElementException
		 *                if there is no element left in the map
		 */
		public Set<T> value() throws ConcurrentModificationException, NoSuchElementException {
			if (referenceCount != count) {
				throw new ConcurrentModificationException();
			}
			if (current < 0) {
				throw new NoSuchElementException();
			}
			return values[current];
		}

	}

	/** Status indicator for free table entries. */
	protected static final byte FREE = 0;

	/** Status indicator for full table entries. */
	protected static final byte FULL = 1;

	/** Status indicator for removed table entries. */
	protected static final byte REMOVED = 2;

	/** Serializable version identifier. */
	private static final long serialVersionUID = 5483913974727729407L;

	/** Load factor for the map. */
	private static final float LOAD_FACTOR = 0.5f;

	/**
	 * Default starting size.
	 * <p>
	 * This must be a power of two for bit mask to work properly.
	 * </p>
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 16;

	/**
	 * Multiplier for size growth when map fills up.
	 * <p>
	 * This must be a power of two for bit mask to work properly.
	 * </p>
	 */
	private static final int RESIZE_MULTIPLIER = 2;

	/**
	 * Number of bits to perturb the index when probing for collision
	 * resolution.
	 */
	private static final int PERTURB_SHIFT = 5;

	/**
	 * Change the index sign
	 *
	 * @param index
	 *            initial index
	 * @return changed index
	 */
	private static int changeIndexSign(final int index) {
		return -index - 1;
	}

	/**
	 * Compute the capacity needed for a given size.
	 *
	 * @param expectedSize
	 *            expected size of the map
	 * @return capacity to use for the specified size
	 */
	private static int computeCapacity(final int expectedSize) {
		if (expectedSize == 0) {
			return 1;
		}
		final int capacity = (int) Math.ceil(expectedSize / LOAD_FACTOR);
		final int powerOfTwo = Integer.highestOneBit(capacity);
		if (powerOfTwo == capacity) {
			return capacity;
		}
		return nextPowerOfTwo(capacity);
	}

	/**
	 * Find the index at which a key should be inserted
	 *
	 * @param keys
	 *            keys table
	 * @param states
	 *            states table
	 * @param key
	 *            key to lookup
	 * @param mask
	 *            bit mask for hash values
	 * @return index at which key should be inserted
	 */
	private static int findInsertionIndex(final int[] keys, final byte[] states, final int key, final int mask) {
		final int hash = hashOf(key);
		int index = hash & mask;
		if (states[index] == FREE) {
			return index;
		} else if (states[index] == FULL && keys[index] == key) {
			return changeIndexSign(index);
		}

		int perturb = perturb(hash);
		int j = index;
		if (states[index] == FULL) {
			while (true) {
				j = probe(perturb, j);
				index = j & mask;
				perturb >>= PERTURB_SHIFT;

				if (states[index] != FULL || keys[index] == key) {
					break;
				}
			}
		}

		if (states[index] == FREE) {
			return index;
		} else if (states[index] == FULL) {
			// due to the loop exit condition,
			// if (states[index] == FULL) then keys[index] == key
			return changeIndexSign(index);
		}

		final int firstRemoved = index;
		while (true) {
			j = probe(perturb, j);
			index = j & mask;

			if (states[index] == FREE) {
				return firstRemoved;
			} else if (states[index] == FULL && keys[index] == key) {
				return changeIndexSign(index);
			}

			perturb >>= PERTURB_SHIFT;

		}

	}

	/**
	 * Compute the hash value of a key
	 *
	 * @param key
	 *            key to hash
	 * @return hash value of the key
	 */
	private static int hashOf(final int key) {
		final int h = key ^ ((key >>> 20) ^ (key >>> 12));
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/** Return value for missing entries. */
	// private final List<T> missingEntries;

	/**
	 * Find the smallest power of two greater than the input value
	 *
	 * @param i
	 *            input value
	 * @return smallest power of two greater than the input value
	 */
	private static int nextPowerOfTwo(final int i) {
		return Integer.highestOneBit(i) << 1;
	}

	/**
	 * Perturb the hash for starting probing.
	 *
	 * @param hash
	 *            initial hash
	 * @return perturbed hash
	 */
	private static int perturb(final int hash) {
		return hash & 0x7fffffff;
	}

	/**
	 * Compute next probe for collision resolution
	 *
	 * @param perturb
	 *            perturbed hash
	 * @param j
	 *            previous probe
	 * @return next probe
	 */
	private static int probe(final int perturb, final int j) {
		return (j << 2) + j + perturb + 1;
	}
	
	private Comparator<T> comparator;

	/** Keys table. */
	private int[] keys;

	/** Values table. */
	private Set<T>[] values;

	/** States table. */
	private byte[] states;

	/** Current size of the map. */
	private int size;

	/** Bit mask for hash values. */
	private int mask;

	/** Modifications count. */
	private transient int count;

	/**
	 * Build an empty map with default size and using zero for missing entries.
	 *
	 */
	public OpenIntToSet(Comparator<T> comparator) {
		this(comparator, DEFAULT_EXPECTED_SIZE);
	}

	/**
	 * Build an empty map with specified size.
	 *
	 * @param expectedSize
	 *            expected number of elements in the map
	 */
	@SuppressWarnings("unchecked")
	public OpenIntToSet(Comparator<T> comparator, final int expectedSize) {
		final int capacity = computeCapacity(expectedSize);
		this.comparator = comparator;
		this.keys = new int[capacity];
		this.values = new TreeSet[capacity];
		this.states = new byte[capacity];
		this.mask = capacity - 1;
	}

	/**
	 * Copy constructor.
	 *
	 * @param source
	 *            map to copy
	 */
	@SuppressWarnings("unchecked")
	public OpenIntToSet(final OpenIntToSet<T> source) {
		final int length = source.keys.length;
		keys = new int[length];
		System.arraycopy(source.keys, 0, keys, 0, length);
		values = new TreeSet[length];
		System.arraycopy(source.values, 0, values, 0, length);
		states = new byte[length];
		System.arraycopy(source.states, 0, states, 0, length);
		// missingEntries = source.missingEntries;
		size = source.size;
		mask = source.mask;
		count = source.count;
	}

	public boolean containsEntry(int key, T value) {
		Set<T> set = get(key);
		return set != null && set.contains(value);
	}

	/**
	 * Check if a value is associated with a key.
	 *
	 * @param key
	 *            key to check
	 * @return true if a value is associated with key
	 */
	public boolean containsKey(final int key) {

		final int hash = hashOf(key);
		int index = hash & mask;
		if (containsKey(key, index)) {
			return true;
		}

		if (states[index] == FREE) {
			return false;
		}

		int j = index;
		for (int perturb = perturb(hash); states[index] != FREE; perturb >>= PERTURB_SHIFT) {
			j = probe(perturb, j);
			index = j & mask;
			if (containsKey(key, index)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Check if the tables contain an element associated with specified key at
	 * specified index.
	 *
	 * @param key
	 *            key to check
	 * @param index
	 *            index to check
	 * @return true if an element is associated with key at index
	 */
	private boolean containsKey(final int key, final int index) {
		return (key != 0 || states[index] == FULL) && keys[index] == key;
	}

	/**
	 * Remove an element at specified index.
	 *
	 * @param index
	 *            index of the element to remove
	 * @return removed value
	 */
	private Set<T> doRemove(int index) {
		keys[index] = 0;
		states[index] = REMOVED;
		final Set<T> previous = values[index];
		values[index] = null;
		--size;
		++count;
		return previous;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpenIntToSet<T> other = (OpenIntToSet<T>) obj;
		if (!Arrays.equals(keys, other.keys))
			return false;
		if (mask != other.mask)
			return false;
		if (size != other.size)
			return false;
		if (!Arrays.equals(states, other.states))
			return false;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	/**
	 * Find the index at which a key should be inserted
	 *
	 * @param key
	 *            key to lookup
	 * @return index at which key should be inserted
	 */
	private int findInsertionIndex(final int key) {
		return findInsertionIndex(keys, states, key, mask);
	}

	/**
	 * Get the stored value associated with the given key
	 *
	 * @param key
	 *            key associated with the data
	 * @return data associated with the key
	 */
	public Set<T> get(final int key) {

		final int hash = hashOf(key);
		int index = hash & mask;
		if (containsKey(key, index)) {
			return values[index];
		}

		if (states[index] == FREE) {
			return null;
		}

		int j = index;
		for (int perturb = perturb(hash); states[index] != FREE; perturb >>= PERTURB_SHIFT) {
			j = probe(perturb, j);
			index = j & mask;
			if (containsKey(key, index)) {
				return values[index];
			}
		}

		return null;

	}

	public Set<T>[] getValues() {
		return values;
	}

	/**
	 * Grow the tables.
	 */
	private void growTable() {

		final int oldLength = states.length;
		final int[] oldKeys = keys;
		final Set<T>[] oldValues = values;
		final byte[] oldStates = states;

		final int newLength = RESIZE_MULTIPLIER * oldLength;
		final int[] newKeys = new int[newLength];
		@SuppressWarnings("unchecked")
		final Set<T>[] newValues = new TreeSet[newLength];
		final byte[] newStates = new byte[newLength];
		final int newMask = newLength - 1;
		for (int i = 0; i < oldLength; ++i) {
			if (oldStates[i] == FULL) {
				final int key = oldKeys[i];
				final int index = findInsertionIndex(newKeys, newStates, key, newMask);
				newKeys[index] = key;
				newValues[index] = oldValues[i];
				newStates[index] = FULL;
			}
		}

		mask = newMask;
		keys = newKeys;
		values = newValues;
		states = newStates;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keys);
		result = prime * result + mask;
		result = prime * result + size;
		result = prime * result + Arrays.hashCode(states);
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Get an iterator over map elements.
	 * <p>
	 * The specialized iterators returned are fail-fast: they throw a
	 * <code>ConcurrentModificationException</code> when they detect the map has
	 * been modified during iteration.
	 * </p>
	 *
	 * @return iterator over the map elements
	 */
	public Iterator iterator() {
		return new Iterator();
	}

	/**
	 * Put a value associated with a key in the map.
	 *
	 * @param key
	 *            key to which value is associated
	 * @param value
	 *            value to put in the map
	 * @return previous value associated with the key
	 */
	public void put(final int key, final T value) {
		int index = findInsertionIndex(key);
		boolean newMapping = true;
		if (index < 0) {
			index = changeIndexSign(index);
			// previous = values[index];
			newMapping = false;
		}
		keys[index] = key;
		states[index] = FULL;
		if (values[index] == null) {
			values[index] = new TreeSet<T>(comparator);
			values[index].add(value);
		} else {
			values[index].add(value);
		}
		if (newMapping) {
			++size;
			if (shouldGrowTable()) {
				growTable();
			}
			++count;
		}
	}

	/**
	 * Read a serialized object.
	 *
	 * @param stream
	 *            input stream
	 * @throws IOException
	 *             if object cannot be read
	 * @throws ClassNotFoundException
	 *             if the class corresponding to the serialized object cannot be
	 *             found
	 */
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		count = 0;
	}

	/**
	 * Remove the value associated with a key.
	 *
	 * @param key
	 *            key to which the value is associated
	 * @return removed value
	 */
	public Set<T> remove(final int key) {

		final int hash = hashOf(key);
		int index = hash & mask;
		if (containsKey(key, index)) {
			return doRemove(index);
		}

		if (states[index] == FREE) {
			return null;
		}

		int j = index;
		for (int perturb = perturb(hash); states[index] != FREE; perturb >>= PERTURB_SHIFT) {
			j = probe(perturb, j);
			index = j & mask;
			if (containsKey(key, index)) {
				return doRemove(index);
			}
		}

		return null;

	}

	public boolean remove(int key, T value) {
		Set<T> set = get(key);
		return set != null && set.remove(value);
	}

	/**
	 * Check if tables should grow due to increased size.
	 *
	 * @return true if tables should grow
	 */
	private boolean shouldGrowTable() {
		return size > (mask + 1) * LOAD_FACTOR;
	}

	/**
	 * Get the number of elements stored in the map.
	 *
	 * @return number of elements stored in the map
	 */
	public int size() {
		return size;
	}
}
