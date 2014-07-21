package org.apfloat;

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map with an underlying ConcurrentHashMap with softly referenced values.
 * The maximum map size is assumed to be limited so no effort is made to
 * expunge entries for stale values.
 *
 * @since 1.6
 * @version 1.6
 * @author Mikko Tommila
 */

class ConcurrentSoftHashMap<K, V>
    extends AbstractMap<K, V>
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

    public V remove(Object key)
    {
        return unwrap(this.map.remove(key));
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
