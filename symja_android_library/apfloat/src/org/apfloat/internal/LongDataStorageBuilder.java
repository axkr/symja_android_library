package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

/**
 * Default data storage creation strategy for the <code>long</code> data type.
 *
 * @see LongMemoryDataStorage
 * @see LongDiskDataStorage
 *
 * @version 1.8.2
 * @author Mikko Tommila
 */

public class LongDataStorageBuilder
    extends AbstractDataStorageBuilder
{
    /**
     * Default constructor.
     */

    public LongDataStorageBuilder()
    {
    }

    protected long getMaxCachedSize()
    {
        return (long) 8 * Integer.MAX_VALUE;
    }

    protected DataStorage createCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new LongMemoryDataStorage();
    }

    protected DataStorage createNonCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new LongDiskDataStorage();
    }

    protected boolean isCached(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        return (dataStorage instanceof LongMemoryDataStorage);
    }
}
