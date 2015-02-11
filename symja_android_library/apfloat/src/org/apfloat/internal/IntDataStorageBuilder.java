package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

/**
 * Default data storage creation strategy for the <code>int</code> data type.
 *
 * @see IntMemoryDataStorage
 * @see IntDiskDataStorage
 *
 * @version 1.8.2
 * @author Mikko Tommila
 */

public class IntDataStorageBuilder
    extends AbstractDataStorageBuilder
{
    /**
     * Default constructor.
     */

    public IntDataStorageBuilder()
    {
    }

    protected long getMaxCachedSize()
    {
        return (long) 4 * Integer.MAX_VALUE;
    }

    protected DataStorage createCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new IntMemoryDataStorage();
    }

    protected DataStorage createNonCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new IntDiskDataStorage();
    }

    protected boolean isCached(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        return (dataStorage instanceof IntMemoryDataStorage);
    }
}
