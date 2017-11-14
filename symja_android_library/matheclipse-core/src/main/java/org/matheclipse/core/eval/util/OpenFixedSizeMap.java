package org.matheclipse.core.eval.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * Open addressed map from K to V. The map can contain up to <code>tableSize</code> key/value entries. Otherwise a // *
 * <code>java.lang.IllegalStateException</code> exception will be thrown. The <code>entrySet()</code> method is not
 * implemented and throws a <code>java.lang.IllegalStateException</code> exception.
 *
 * <p>
 * This class is not synchronized.
 * </p>
 * 
 * @param <K>
 *            the key type of the objects
 * @param <V>
 *            the value type of the objects
 */
public class OpenFixedSizeMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7777565424942239816L;

	private Object[] table;

	private int size = 0;

	/**
	 * Open addressed map from K to V. The map can contain up to <code>tableSize</code> key/value entries. Otherwise a
	 * <code>java.lang.IllegalStateException</code> exception will be thrown. The <code>entrySet()</code> method is not
	 * implemented and throws a <code>java.lang.IllegalStateException</code> exception.
	 * 
	 * @param tableSize
	 */
	public OpenFixedSizeMap(int tableSize) {
		table = new Object[tableSize << 1];
	}

	@Override
	public OpenFixedSizeMap<K, V> clone() {
		OpenFixedSizeMap<K, V> result = null;
		try {
			result = (OpenFixedSizeMap<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		result.table = new Object[table.length];
		result.size = size;
		for (int i = 0; i < table.length; i++) {
			result.table[i] = table[i];
		}
		return result;
	}

	/**
	 * Throws exception <code>IllegalStateException("entrySet not implemented!")</code>.
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new IllegalStateException("entrySet not implemented!");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof OpenFixedSizeMap) {
			OpenFixedSizeMap<K, V> other = (OpenFixedSizeMap<K, V>) obj;
			if (size != other.size) {
				return false;
			}
			return Arrays.equals(table, other.table);
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		int hash = hash(key);
		int kHash = hash;
		int index = kHash * 2;
		do {
			if (table[index] == null) {
				return null;
			} else if (table[index].equals(key)) {
				return (V) table[index + 1];
			}

			kHash = hash((kHash + 1));
			index = kHash << 1;
		} while (hash != kHash);

		return null;
	}

	private int hash(int index) {
		return index % (table.length >> 1);
	}

	private int hash(Object key) {
		return hash(key.hashCode() & Integer.MAX_VALUE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + size;
		result = prime * result + Arrays.hashCode(table);
		return result;
	}

	@SuppressWarnings("unchecked")
	private V put(int i, K key, V value) {
		do {
			int index = i << 1;
			K entry = (K) table[index];
			if (entry == null) {
				table[index] = key;
				table[index + 1] = value;
				size++;
				return value;
			} else if (entry.equals(key)) {
				index++;
				V oldValue = (V) table[index];
				table[index] = value;
				return oldValue;
			}
			i = (i + 1) % (table.length >> 1);
		} while (true);
	}

	@Override
	public V put(K key, V value) {
		if (size == (table.length >> 1))
			throw new IllegalStateException("Map is full!");
		int hash = hash(key);
		return put(hash, key, value);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		int length = s.readInt();
		table = new Object[length];
		int size = s.readInt();
		for (int i = 0; i < size; i++) {
			K key = (K) s.readObject();
			V value = (V) s.readObject();
			put(hash(key), key, value);
		}
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Serialize map to a stream.
	 *
	 * @param s
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeInt(table.length);
		s.writeInt(size);

		for (int j = 0; j < table.length; j += 2) {
			if (table[j] != null) {
				s.writeObject(table[j]);
				s.writeObject(table[j + 1]);
			}
		}
	}
}