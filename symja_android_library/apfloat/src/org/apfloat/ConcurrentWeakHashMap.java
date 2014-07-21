package org.apfloat;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Combination of WeakHashMap and ConcurrentHashMap,
 * providing weak keys and non-blocking access.
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

class ConcurrentWeakHashMap<K, V>
    extends AbstractMap<K, V>
{
    private static class Key
        extends WeakReference<Object>
    {
        private int hashCode;

        public Key(Object key, ReferenceQueue<Object> queue)
        {
            super(key, queue);
            this.hashCode = key.hashCode();
        }

        public int hashCode()
        {
            return this.hashCode;
        }

        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                // Always matches even if referenced object has been garbage collected, needed for expunging garbage collected keys
                return true;
            }
            if (obj instanceof Key)
            {
                Key that = (Key) obj;
                Object value = get();
                return (value != null && value.equals(that.get()));
            }
            return false;
        }
    }

    private ConcurrentHashMap<Key, V> map;
    private ReferenceQueue<Object> queue;

    public ConcurrentWeakHashMap()
    {
        this.map = new ConcurrentHashMap<Key, V>();
        this.queue = new ReferenceQueue<Object>();
    }

    public void clear()
    {
        expungeStaleEntries();
        this.map.clear();
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        throw new UnsupportedOperationException();
    }

    public V get(Object key)
    {
        // Do not expunge stale entries here to improve performance
        return this.map.get(wrap(key));
    }

    public V put(K key, V value)
    {
        expungeStaleEntries();
        return this.map.put(wrap(key), value);
    }

    public V remove(Object key)
    {
        expungeStaleEntries();
        return this.map.remove(wrap(key));
    }

    public int size()
    {
        expungeStaleEntries();
        return this.map.size();
    }

    private Key wrap(Object key)
    {
        return new Key(key, this.queue);
    }

    private void expungeStaleEntries()
    {
        // Should not cause (much) blocking
        Key key;
        while ((key = (Key) this.queue.poll()) != null)
        {
            this.map.remove(key);
        }
    }
}
