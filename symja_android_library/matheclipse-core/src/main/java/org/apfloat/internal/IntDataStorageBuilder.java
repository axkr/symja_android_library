package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

/**
 * Default data storage creation strategy for the <code>int</code> data type.
 *
 * @see IntMemoryDataStorage
 * @see IntDiskDataStorage
 *
 * @version 1.7.0
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
