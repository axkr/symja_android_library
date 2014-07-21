package org.apfloat;

import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;

/**
 * Map that always throws <code>ApfloatRuntimeException</code> on all operations.
 * Can be used to replace cache maps after JVM shutdown and clean-up
 * has been initiated to prevent other threads from performing any operations.
 *
 * @since 1.6.2
 * @version 1.6.2
 * @author Mikko Tommila
 */

class ShutdownMap<K, V>
    extends AbstractMap<K, V>
{
    public ShutdownMap()
    {
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        throw new ApfloatRuntimeException("Shutdown in progress");
    }

    public V put(K key, V value)
    {
        throw new ApfloatRuntimeException("Shutdown in progress");
    }
}
