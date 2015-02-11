package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

/**
 * Default data storage creation strategy for the <code>float</code> data type.
 *
 * @see FloatMemoryDataStorage
 * @see FloatDiskDataStorage
 *
 * @version 1.8.2
 * @author Mikko Tommila
 */

public class FloatDataStorageBuilder
    extends AbstractDataStorageBuilder
{
    /**
     * Default constructor.
     */

    public FloatDataStorageBuilder()
    {
    }

    protected long getMaxCachedSize()
    {
        return (long) 4 * Integer.MAX_VALUE;
    }

    protected DataStorage createCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new FloatMemoryDataStorage();
    }

    protected DataStorage createNonCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new FloatDiskDataStorage();
    }

    protected boolean isCached(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        return (dataStorage instanceof FloatMemoryDataStorage);
    }
}
