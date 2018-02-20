package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.ArrayAccess;

/**
 * Memory based data storage implementation for the <code>double</code>
 * element type.
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class DoubleMemoryDataStorage
    extends DataStorage
{
    /**
     * Default constructor.
     */

    public DoubleMemoryDataStorage()
    {
        this.data = new double[0];
    }

    /**
     * Subsequence constructor.
     *
     * @param doubleMemoryDataStorage The originating data storage.
     * @param offset The subsequence starting position.
     * @param length The subsequence length.
     */

    protected DoubleMemoryDataStorage(DoubleMemoryDataStorage doubleMemoryDataStorage, long offset, long length)
    {
        super(doubleMemoryDataStorage, offset, length);
        this.data = doubleMemoryDataStorage.data;
    }

    public boolean isCached()
    {
        return true;
    }

    protected DataStorage implSubsequence(long offset, long length)
        throws ApfloatRuntimeException
    {
        return new DoubleMemoryDataStorage(this, offset + getOffset(), length);
    }

    protected void implCopyFrom(DataStorage dataStorage, long size)
        throws ApfloatRuntimeException
    {
        assert (size > 0);
        assert (!isReadOnly());
        assert (!isSubsequenced());

        if (size > Integer.MAX_VALUE)
        {
            throw new ApfloatInternalException("Size too big for memory array: " + size);
        }

        if (dataStorage == this)
        {
            setSize(size);
            return;
        }

        this.data = new double[(int) size];

        ApfloatContext ctx = ApfloatContext.getContext();
        int readSize = (int) Math.min(size, dataStorage.getSize()),
            position = 0,
            bufferSize = ctx.getBlockSize() / 8;

        while (readSize > 0)
        {
            int length = (int) Math.min(bufferSize, readSize);

            ArrayAccess arrayAccess = dataStorage.getArray(READ, position, length);
            System.arraycopy(arrayAccess.getDoubleData(), arrayAccess.getOffset(), this.data, position, length);
            arrayAccess.close();

            readSize -= length;
            position += length;
       }
    }

    protected long implGetSize()
    {
        return this.data.length;
    }

    protected void implSetSize(long size)
        throws ApfloatRuntimeException
    {
        assert (size > 0);
        assert (!isReadOnly());
        assert (!isSubsequenced());

        if (size == this.data.length)
        {
            return;
        }

        if (size > Integer.MAX_VALUE)
        {
            throw new ApfloatInternalException("Size too big for memory array: " + size);
        }

        int newSize = (int) size;

        double[] newData = new double[newSize];
        System.arraycopy(this.data, 0, newData, 0, Math.min(this.data.length, newSize));
        this.data = newData;
    }

    protected ArrayAccess implGetArray(int mode, long offset, int length)
        throws ApfloatRuntimeException
    {
        return new DoubleMemoryArrayAccess(this.data, (int) (offset + getOffset()), length);
    }

    protected ArrayAccess implGetArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        throw new ApfloatInternalException("Method not implemented - would be sub-optimal; change the apfloat configuration settings");
    }

    protected ArrayAccess implGetTransposedArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        throw new ApfloatInternalException("Method not implemented - would be sub-optimal; change the apfloat configuration settings");
    }

    private class ReadWriteIterator
        extends AbstractIterator
    {
        public ReadWriteIterator(long startPosition, long endPosition)
            throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            this(READ_WRITE, startPosition, endPosition);
        }

        protected ReadWriteIterator(int mode, long startPosition, long endPosition)
            throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            super(mode, startPosition, endPosition);

            this.data = DoubleMemoryDataStorage.this.data;

            this.position = (int) getPosition() + (int) getOffset();
            this.length = (int) getLength();
        }

        public boolean hasNext()
        {
            return (this.length > 0);
        }

        public void next()
            throws IllegalStateException
        {
            checkLength();
            this.position += getIncrement();
            this.length--;
        }

        public double getDouble()
            throws IllegalStateException
        {
            checkLength();
            return this.data[this.position];
        }

        public void setDouble(double value)
            throws IllegalStateException
        {
            checkLength();
            this.data[this.position] = value;
        }

        public <T> T get(Class<T> type)
            throws UnsupportedOperationException, IllegalStateException
        {
            if (!(type.equals(Double.TYPE)))
            {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is double");
            }
            @SuppressWarnings("unchecked")
            T value = (T) (Double) getDouble();
            return value;
        }

        public <T> void set(Class<T> type, T value)
            throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException
        {
            if (!(type.equals(Double.TYPE)))
            {
                throw new UnsupportedOperationException("Unsupported data type " + type.getCanonicalName() + ", the only supported type is double");
            }
            if (!(value instanceof Double))
            {
                throw new IllegalArgumentException("Unsupported value type " + value.getClass().getCanonicalName() + ", the only supported type is Double");
            }
            setDouble((Double) value);
        }

        protected void checkLength()
            throws IllegalStateException
        {
            if (this.length == 0)
            {
                throw new IllegalStateException("At the end of iterator");
            }
        }

        private static final long serialVersionUID = -9012199261873349608L;

        private double[] data;
        private int position,
                    length;
    }

    private class ReadOnlyIterator
        extends ReadWriteIterator
    {
        public ReadOnlyIterator(long startPosition, long endPosition)
            throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            super(READ, startPosition, endPosition);
        }

        public void setDouble(double value)
            throws IllegalStateException
        {
            throw new IllegalStateException("Not a writable iterator");
        }

        private static final long serialVersionUID = 5449985546703735328L;
    }

    private class WriteOnlyIterator
        extends ReadWriteIterator
    {
        public WriteOnlyIterator(long startPosition, long endPosition)
            throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            super(WRITE, startPosition, endPosition);
        }

        public double getDouble()
            throws IllegalStateException
        {
            throw new IllegalStateException("Not a readable iterator");
        }

        private static final long serialVersionUID = 3758519654059499404L;
    }

    public Iterator iterator(int mode, long startPosition, long endPosition)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        Iterator iterator;
        switch (mode & READ_WRITE)
        {
            case READ:
                iterator = new ReadOnlyIterator(startPosition, endPosition);
                break;
            case WRITE:
                iterator = new WriteOnlyIterator(startPosition, endPosition);
                break;
            case READ_WRITE:
                iterator = new ReadWriteIterator(startPosition, endPosition);
                break;
            default:
                throw new IllegalArgumentException("Illegal mode: " + mode);
        }
        return iterator;
    }

    private static final long serialVersionUID = 5093781604796636929L;

    private double[] data;
}
