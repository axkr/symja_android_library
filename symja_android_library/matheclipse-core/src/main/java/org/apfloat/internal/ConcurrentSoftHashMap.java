package org.apfloat.internal;

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap with softly referenced values.
 * The maximum map size is assumed to be limited so no
 * effort is made to expunge entries for stale values.<p>
 *
 * Values are not properly compared for equality so
 * the only actual concurrent method implemented is
 * <code>putIfAbsent()</code>.<p>
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

class ConcurrentSoftHashMap<K, V>
    extends AbstractMap<K, V>
    implements ConcurrentMap<K, V>
{
    private ConcurrentHashMap<K, SoftReference<V>> map;

    public ConcurrentSoftHashMap()
    {
        this.map = new ConcurrentHashMap<K, SoftReference<V>>();
    }

    public void clear()
    {
        this.map.clear();
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        throw new UnsupportedOperationException();
    }

    public V get(Object key)
    {
        return unwrap(this.map.get(key));
    }

    public V put(K key, V value)
    {
        return unwrap(this.map.put(key, wrap(value)));
    }

    public V putIfAbsent(K key, V value)
    {
        return unwrap(this.map.putIfAbsent(key, wrap(value)));
    }

    public V remove(Object key)
    {
        return unwrap(this.map.remove(key));
    }

    public boolean remove(Object key, Object value)
    {
        throw new UnsupportedOperationException();
    }

    public V replace(K key, V value)
    {
        throw new UnsupportedOperationException();
    }

    public boolean replace(K key, V oldValue, V newValue)
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        return this.map.size();
    }

    private SoftReference<V> wrap(V value)
    {
        return new SoftReference<V>(value);
    }

    private V unwrap(SoftReference<V> value)
    {
        return (value == null ? null : value.get());
    }
}
