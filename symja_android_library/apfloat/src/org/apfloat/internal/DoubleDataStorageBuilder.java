package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

/**
 * Default data storage creation strategy for the <code>double</code> data type.
 *
 * @see DoubleMemoryDataStorage
 * @see DoubleDiskDataStorage
 *
 * @version 1.8.2
 * @author Mikko Tommila
 */

public class DoubleDataStorageBuilder
    extends AbstractDataStorageBuilder
{
    /**
     * Default constructor.
     */

    public DoubleDataStorageBuilder()
    {
    }

    protected long getMaxCachedSize()
    {
        return (long) 8 * Integer.MAX_VALUE;
    }

    protected DataStorage createCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new DoubleMemoryDataStorage();
    }

    protected DataStorage createNonCachedDataStorage()
        throws ApfloatRuntimeException
    {
        return new DoubleDiskDataStorage();
    }

    protected boolean isCached(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        return (dataStorage instanceof DoubleMemoryDataStorage);
    }
}
