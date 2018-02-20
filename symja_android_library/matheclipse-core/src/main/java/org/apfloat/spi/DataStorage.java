package org.apfloat.spi;

import java.io.Serializable;

import org.apfloat.ApfloatRuntimeException;

/**
 * Generic data storage class.<p>
 *
 * Initially when a data storage is created, it is mutable
 * (it can be modified). After the contents have been properly
 * set, the user should call {@link #setReadOnly()} to set the
 * storage to be immutable. After this the data storage can be
 * safely shared between different users.<p>
 *
 * Access to <code>DataStorage</code> objects is generally not internally synchronized.
 * Accessing multiple non-overlapping parts of the storage concurrently with the
 * {@link #getArray(int,long,int)}, {@link #getArray(int,int,int,int)}
 * or {@link #getTransposedArray(int,int,int,int)} method and with
 * iterators over non-overlapping parts is permitted. Invoking
 * other methods must generally be externally synchronized.
 *
 * @version 1.8.1
 * @author Mikko Tommila
 */

public abstract class DataStorage
    implements Serializable
{
    /**
     * Read access mode specifier.
     */

    public static final int READ = 1;

    /**
     * Write access mode specifier.
     */

    public static final int WRITE = 2;

    /**
     * Read-write access mode specifier. For convenience, equivalent to <code>READ | WRITE</code>.
     */

    public static final int READ_WRITE = READ | WRITE;

    /**
     * Iterator for iterating through elements of the data storage.
     */

    public static abstract class Iterator
        implements Serializable
    {
        /**
         * Default constructor. Can be used e.g. for simple anonymous subclasses.
         */

        protected Iterator()
        {
        }

        /**
         * Check if <code>next()</code> can be called without going past the end of the sequence.<p>
         * That is, if <code>next()</code> can be called without deliberately causing an exception.
         *
         * <b>Note:</b> It is important that the iterator is iterated
         * past the last element; that is <code>next()</code> is called
         * <code>startPosition - endPosition</code> times. The
         * <code>get()</code> or <code>set()</code> methods should not
         * be called for the last element.<p>
         *
         * The default implementation always returns <code>false</code>.
         *
         * @return <code>true</code> if {@link #next()} can be called, otherwise <code>false</code>.
         */

        public boolean hasNext()
        {
            return false;
        }

        /**
         * Advances the position in the stream by one element.<p>
         *
         * <b>Note:</b> It is important that the iterator is iterated
         * past the last element; that is <code>next()</code> is called
         * <code>startPosition - endPosition</code> times. The
         * <code>get()</code> or <code>set()</code> methods should not
         * be called for the last element.<p>
         *
         * The default implementation always throws <code>IllegalStateException</code>.
         *
         * @exception IllegalStateException If the iterator has been iterated to the end already.
         */

        public void next()
            throws IllegalStateException, ApfloatRuntimeException
        {
            throw new IllegalStateException("Not implemented");
        }

        /**
         * Gets the current element as an <code>int</code>.<p>
         *
         * The default implementation calls {@link #get(Class)} with argument {@link Integer#TYPE}.
         *
         * @return The current element as an <code>int</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to an <code>int</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public int getInt()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            return get(Integer.TYPE);
        }

        /**
         * Gets the current element as a <code>long</code>.<p>
         *
         * The default implementation calls {@link #get(Class)} with argument {@link Long#TYPE}.
         *
         * @return The current element as a <code>long</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>long</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public long getLong()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            return get(Long.TYPE);
        }

        /**
         * Gets the current element as a <code>float</code>.<p>
         *
         * The default implementation calls {@link #get(Class)} with argument {@link Float#TYPE}.
         *
         * @return The current element as a <code>float</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>float</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public float getFloat()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            return get(Float.TYPE);
        }

        /**
         * Gets the current element as a <code>double</code>.<p>
         *
         * The default implementation calls {@link #get(Class)} with argument {@link Double#TYPE}.
         *
         * @return The current element as a <code>double</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>double</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public double getDouble()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            return get(Double.TYPE);
        }

        /**
         * Sets the current element as an <code>int</code>.<p>
         *
         * The default implementation calls {@link #set(Class,Object)} with first argument {@link Integer#TYPE}.
         *
         * @param value The value to be set to the current element as an <code>int</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to an <code>int</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public void setInt(int value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            set(Integer.TYPE, value);
        }

        /**
         * Sets the current element as a <code>long</code>.<p>
         *
         * The default implementation calls {@link #set(Class,Object)} with first argument {@link Long#TYPE}.
         *
         * @param value The value to be set to the current element as a <code>long</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>long</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public void setLong(long value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            set(Long.TYPE, value);
        }

        /**
         * Sets the current element as a <code>float</code>.<p>
         *
         * The default implementation calls {@link #set(Class,Object)} with first argument {@link Float#TYPE}.
         *
         * @param value The value to be set to the current element as a <code>float</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>float</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public void setFloat(float value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            set(Float.TYPE, value);
        }

        /**
         * Sets the current element as a <code>double</code>.<p>
         *
         * The default implementation calls {@link #set(Class,Object)} with first argument {@link Double#TYPE}.
         *
         * @param value The value to be set to the current element as a <code>double</code>.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to a <code>double</code>.
         * @exception IllegalStateException If the iterator is at the end.
         */

        public void setDouble(double value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            set(Double.TYPE, value);
        }

        /**
         * Gets the current element as a the specified element type.<p>
         *
         * The default implementation always throws <code>UnsupportedOperationException</code>.
         *
         * @param type The type of the element.
         *
         * @return The current element as the specified type.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to the specified type.
         * @exception IllegalStateException If the iterator is at the end.
         *
         * @since 1.7.0
         */

        public <T> T get(Class<T> type)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            throw new UnsupportedOperationException("Not implemented");
        }

        /**
         * Sets the current element as the specified element type.<p>
         *
         * The default implementation always throws <code>UnsupportedOperationException</code>.
         *
         * @param type The type of the element.
         * @param value The value to be set to the current element as the specified type.
         *
         * @exception UnsupportedOperationException If the element type of the data storage can't be converted to the specified type.
         * @exception IllegalArgumentException If the value is not of the specified type.
         * @exception IllegalStateException If the iterator is at the end.
         *
         * @since 1.7.0
         */

        public <T> void set(Class<T> type, T value)
            throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            throw new UnsupportedOperationException("Not implemented");
        }

        /**
         * Closes the iterator. This needs to be called only if the
         * iterator is not iterated to the end.
         */

        public void close()
            throws ApfloatRuntimeException
        {
        }

        private static final long serialVersionUID = 7155668655967297483L;
    }

    /**
     * Abstract base class for iterators iterating through this <code>DataStorage</code>.
     * This class provides most of the common functionality needed.
     */

    protected abstract class AbstractIterator
        extends Iterator
    {
        /**
         * Construct a new iterator. Elements can be iterated either
         * in forward or in reverse order, depending on if <code>startPosition</code>
         * is less than or greater than <code>endPosition</code>, correspondingly.
         *
         * @param mode Access mode for iterator: {@link #READ}, {@link #WRITE} or both.
         * @param startPosition Starting position of iterator in the data set. For reverse access, the first element in the iterator is <code>startPosition - 1</code>.
         * @param endPosition End position of iterator in the data set. For forward access, the last accessible element in the iterator is <code>endPosition - 1</code>.
         *
         * @exception IllegalArgumentException If the requested block is out of bounds of the data storage.
         * @exception IllegalStateException If write access is requested for a read-only data storage.
         */

        protected AbstractIterator(int mode, long startPosition, long endPosition)
            throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
        {
            if (startPosition < 0 || endPosition < 0 ||
                startPosition > getSize() || endPosition > getSize())
            {
                throw new IllegalArgumentException("Requested block out of range: startPosition=" + startPosition + ", endPosition=" + endPosition + ", available=" + getSize());
            }

            if (isReadOnly() && (mode & WRITE) != 0)
            {
                throw new IllegalStateException("Write access requested for read-only data storage");
            }

            this.mode = mode;

            if (endPosition >= startPosition)
            {
                this.position = startPosition;
                this.length = endPosition - startPosition;
                this.increment = 1;
            }
            else
            {
                this.position = startPosition - 1;
                this.length = startPosition - endPosition;
                this.increment = -1;
            }
        }

        /**
         * Check if <code>next()</code> can be called without going past the end of the sequence.
         * That is, if <code>next()</code> can be called without deliberately causing an exception.<p>
         *
         * <b>Note:</b> It is important that the iterator is iterated
         * past the last element; that is <code>next()</code> is called
         * <code>startPosition - endPosition</code> times. The
         * <code>get()</code> or <code>set()</code> methods should not
         * be called for the last element.
         *
         * @return <code>true</code> if {@link #next()} can be called, otherwise <code>false</code>.
         */

        public boolean hasNext()
        {
            return (this.length > 0);
        }

        /**
         * Advances the position in the stream by one element.<p>
         *
         * <b>Note:</b> It is important that the iterator is iterated
         * past the last element; that is <code>next()</code> is called
         * <code>startPosition - endPosition</code> times. The
         * <code>get()</code> or <code>set()</code> methods should not
         * be called for the last element.
         */

        public void next()
            throws IllegalStateException, ApfloatRuntimeException
        {
            checkLength();
            this.position += this.increment;
            this.length--;
        }

        public int getInt()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkGet();
            return super.getInt();
        }

        public long getLong()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkGet();
            return super.getLong();
        }

        public float getFloat()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkGet();
            return super.getFloat();
        }

        public double getDouble()
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkGet();
            return super.getDouble();
        }

        public void setInt(int value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkSet();
            super.setInt(value);
        }

        public void setLong(long value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkSet();
            super.setLong(value);
        }

        public void setFloat(float value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkSet();
            super.setFloat(value);
        }

        public void setDouble(double value)
            throws UnsupportedOperationException, IllegalStateException, ApfloatRuntimeException
        {
            checkSet();
            super.setDouble(value);
        }

        /**
         * Checks if any of the <code>get()</code> methods can be called.
         * This checks both that the iterator is not at the end yet, and
         * that the iterator was opened in a readable mode.
         *
         * @exception IllegalStateException If the iterator is at end or is not readable.
         */

        protected void checkGet()
            throws IllegalStateException
        {
            checkLength();
            if ((this.mode & READ) == 0)
            {
                throw new IllegalStateException("Not a readable iterator");
            }
        }

        /**
         * Checks if any of the <code>set()</code> methods can be called.
         * This checks both that the iterator is not at the end yet, and
         * that the iterator was opened in a writable mode.
         *
         * @exception IllegalStateException If the iterator is at end or is not writable.
         */

        protected void checkSet()
            throws IllegalStateException
        {
            checkLength();
            if ((this.mode & WRITE) == 0)
            {
                throw new IllegalStateException("Not a writable iterator");
            }
        }

        /**
         * Checks if the iterator is at the end yet.
         *
         * @exception IllegalStateException If the iterator is at end.
         */

        protected void checkLength()
            throws IllegalStateException
        {
            if (this.length == 0)
            {
                throw new IllegalStateException("At the end of iterator");
            }
        }

        /**
         * Returns the mode in which the iterator was created.
         *
         * @return The mode in which the iterator was created.
         */

        protected int getMode()
        {
            return this.mode;
        }

        /**
         * Returns the current position of the iterator.
         *
         * @return The current position of the iterator.
         */

        protected long getPosition()
        {
            return this.position;
        }

        /**
         * Returns the remaining length in the iterator.
         *
         * @return The remaining length in the iterator.
         */

        protected long getLength()
        {
            return this.length;
        }

        /**
         * Returns the increment of the iterator.
         * This is 1 if the iterator runs forward, or -1
         * if the iterator runs backwards in the data.
         *
         * @return The increment of the iterator.
         */

        protected int getIncrement()
        {
            return this.increment;
        }

        private static final long serialVersionUID = 1668346231773868058L;

        private int mode,
                    increment;
        private long position,
                     length;
    }

    /**
     * Default constructor. To be called by subclasses when creating a new empty
     * <code>DataStorage</code>.
     */

    protected DataStorage()
    {
        this.offset = 0;
        this.length = 0;
        this.originalDataStorage = null;    // No dataStorage that this is a subsequence of
        this.isReadOnly = false;            // Initially writable
        this.isSubsequenced = false;        // Initially is not a subsequence nor any subsequences of this object exist
    }

    /**
     * Subsequence constructor. To be called by subclasses when creating a subsequence of an
     * existing DataStorage.
     *
     * @param dataStorage The originating data storage.
     * @param offset The subsequence starting position.
     * @param length The subsequence length.
     */

    protected DataStorage(DataStorage dataStorage, long offset, long length)
    {
        this.offset = offset;
        this.length = length;
        this.originalDataStorage = dataStorage;
    }

    /**
     * Get a subsequence of this data storage.
     *
     * @param offset The subsequence starting position.
     * @param length The subsequence length.
     *
     * @return Data storage that represents the specified part of this data storage.
     *
     * @exception IllegalArgumentException If the requested subsequence is out of range.
     */

    public final DataStorage subsequence(long offset, long length)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        if (offset < 0 || length <= 0 || offset + length < 0 ||
            offset + length > getSize())
        {
            throw new IllegalArgumentException("Requested subsequence out of range: offset=" + offset + ", length=" + length + ", available=" + getSize());
        }

        setSubsequenced();

        if (offset == 0 && length == getSize())
        {
            // Full contents of the data set; not actually a subsequence
            return this;
        }

        return implSubsequence(offset, length);
    }

    /**
     * Implementation of getting a subsequence of this data storage.
     * The validity of the arguments of this method do not need to be
     * checked.
     *
     * @param offset The subsequence starting position.
     * @param length The subsequence length.
     *
     * @return Data storage that represents the specified part of this data storage.
     */

    protected abstract DataStorage implSubsequence(long offset, long length)
        throws ApfloatRuntimeException;

    /**
     * Copies all data from another data storage to this data storage.
     *
     * @param dataStorage The data storage where the data should be copied from.
     *
     * @exception IllegalArgumentException If the origin data source has a size of zero.
     * @exception IllegalStateException If this data storage is read-only or has subsequences.
     */

    public final void copyFrom(DataStorage dataStorage)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        copyFrom(dataStorage, dataStorage.getSize());
    }

    /**
     * Copies the specified number of elements from another data storage to this data storage.
     *
     * @param dataStorage The data storage where the data should be copied from.
     * @param size The number of elements to be copied.
     *
     * @exception IllegalArgumentException If the size is invalid or zero.
     * @exception IllegalStateException If this data storage is read-only or has subsequences.
     */

    public final void copyFrom(DataStorage dataStorage, long size)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Illegal size: " + size);
        }
        else if (isReadOnly())
        {
            throw new IllegalStateException("Cannot copy to read-only object");
        }
        else if (isSubsequenced())
        {
            throw new IllegalStateException("Cannot copy to when subsequences exist");
        }

        implCopyFrom(dataStorage, size);
    }

    /**
     * Copies the specified number of elements from another data storage to this data storage.
     * The validity of the arguments of this method do not need to be
     * checked.
     *
     * @param dataStorage The data storage where the data should be copied from.
     * @param size The number of elements to be copied.
     */

    protected abstract void implCopyFrom(DataStorage dataStorage, long size)
        throws ApfloatRuntimeException;

    /**
     * Return the size of the data storage, or the length of this sub-sequence
     * if this data storage is a sub-sequence.
     *
     * @return The size of the data storage.
     */

    public final long getSize()
        throws ApfloatRuntimeException
    {
        if (isReadOnly() || isSubsequenced())
        {
            return this.length;
        }
        else
        {
            return implGetSize();
        }
    }

    /**
     * Return the size of the whole data storage, not including sub-sequence settings.
     *
     * @return The size of the whole data storage, not including sub-sequence settings.
     */

    protected abstract long implGetSize()
        throws ApfloatRuntimeException;

    /**
     * Sets the size of the data storage.
     *
     * @param size The size of the data storage.
     *
     * @exception IllegalArgumentException If the size is invalid or zero.
     * @exception IllegalStateException If this data storage is read-only or has subsequences.
     */

    public final void setSize(long size)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Illegal size: " + size);
        }
        else if (isReadOnly())
        {
            throw new IllegalStateException("Cannot set size of read-only object");
        }
        else if (isSubsequenced())
        {
            throw new IllegalStateException("Cannot set size when subsequences exist");
        }

        implSetSize(size);
    }

    /**
     * Sets the size of the data storage.
     * The validity of the arguments of this method do not need to be
     * checked.
     *
     * @param size The size of the data storage.
     */

    protected abstract void implSetSize(long size)
        throws ApfloatRuntimeException;

    /**
     * Returns the read-only state of this data storage.
     *
     * @return <code>true</code> if this data storage is read-only, otherwise <code>false</code>.
     */

    public final boolean isReadOnly()
    {
        if (this.originalDataStorage == null)
        {
            return this.isReadOnly;
        }
        else
        {
            return this.originalDataStorage.isReadOnly();
        }
    }

    /**
     * Sets this data storage as read-only.
     * All existing sub-sequences (recursively) of this data storage
     * are set to read-only as well.
     */

    public final void setReadOnly()
        throws ApfloatRuntimeException
    {
        if (isReadOnly())
        {
            return;
        }

        if (!isSubsequenced())
        {
            this.length = implGetSize();
        }

        if (this.originalDataStorage == null)
        {
            this.isReadOnly = true;
        }
        else
        {
            this.originalDataStorage.setReadOnly();
        }
    }

    /**
     * Gets an array access to the data of this data storage when
     * the data is treated as a linear block.
     *
     * @param mode Access mode for the array access: {@link #READ}, {@link #WRITE} or both.
     * @param offset Starting position of the array access in the data storage.
     * @param length Number of accessible elements in the array access.
     *
     * @return The array access.
     *
     * @exception IllegalArgumentException If the offset or length are out of bounds of the data storage.
     * @exception IllegalStateException If write access is requested for a read-only data storage.
     */

    public final ArrayAccess getArray(int mode, long offset, int length)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        if (isReadOnly() && (mode & WRITE) != 0)
        {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }

        if (offset < 0 || length < 0 || offset + length < 0 ||
            offset + length > getSize())
        {
            throw new IllegalArgumentException("Requested block out of range: offset=" + offset + ", length=" + length + ", available=" + getSize());
        }

        return implGetArray(mode, offset, length);
    }

    /**
     * Gets an array access to the data of this data storage when it is treated as a linear block.
     * The validity of the arguments of this method do not need to be checked.
     *
     * @param mode Access mode for the array access: {@link #READ}, {@link #WRITE} or both.
     * @param offset Starting position of the array access in the data storage.
     * @param length Number of accessible elements in the array access.
     *
     * @return The array access.
     */

    protected abstract ArrayAccess implGetArray(int mode, long offset, int length)
        throws ApfloatRuntimeException;

    /**
     * Maps a block of data to a memory array when the data is treated as a matrix.
     * The matrix size is n<sub>1</sub> x n<sub>2</sub>.
     * The following picture illustrates the block being accessed (in gray):<p>
     *
     * <div align="center">
     *   <table style="width:400px; height:300px; border-collapse:collapse; border:1px solid black" border="1">
     *     <tr>
     *       <td style="width:200px">
     *         <div align="center">
     *           <code>&larr; startColumn &rarr;</code>
     *         </div>
     *       </td>
     *       <td style="width:150px; background:lightgray">
     *         <div align="center">
     *           <code>&larr; columns &rarr;</code>
     *         </div>
     *       </td>
     *       <td style="width:50px">
     *         <div align="center">
     *           <code>&uarr;<br>
     *           n<sub>1</sub><br>
     *           &darr;<br></code>
     *         </div>
     *       </td>
     *     </tr>
     *   </table>
     *   <code>&larr; n<sub>2</sub> &rarr; </code>
     * </div>
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is read.
     * @param columns The number of columns of data to read.
     * @param rows The number of rows of data to read. This should be equivalent to n<sub>1</sub>, number of rows in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the data.
     *
     * @exception IllegalArgumentException If the requested area is out of bounds of the data storage.
     * @exception IllegalStateException If write access is requested for a read-only data storage.
     *
     * @since 1.7.0
     */

    public final ArrayAccess getArray(int mode, int startColumn, int columns, int rows)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        if (isReadOnly() && (mode & WRITE) != 0)
        {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }

        long size = (long) columns * rows;
        if (startColumn < 0 || columns < 0 || rows < 0 || startColumn + columns < 0 ||
            (long) (startColumn + columns) * rows > getSize())
        {
            throw new IllegalArgumentException("Requested block out of range: startColumn=" + startColumn + ", columns=" + columns + ", rows=" + rows + ", available=" + getSize());
        }
        else if (size > Integer.MAX_VALUE)
        {
            throw new ApfloatRuntimeException("Block too large to fit in an array: " + size);
        }

        return implGetArray(mode, startColumn, columns, rows);
    }

    /**
     * Maps a block of data to a memory array when the data is treated as a matrix.
     * The validity of the arguments of this method do not need to be
     * checked.
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is read.
     * @param columns The number of columns of data to read.
     * @param rows The number of rows of data to read. This should be equivalent to n<sub>1</sub>, number of rows in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the data.
     *
     * @since 1.7.0
     */

    protected abstract ArrayAccess implGetArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException;

    /**
     * Maps a transposed block of data to a memory array when the data is treated as a matrix.
     * The matrix size is n<sub>1</sub> x n<sub>2</sub>. The accessed block is illustrated in gray
     * in the following picture. The argument <code>columns</code> is the value <code>b</code>:<p>
     *
     * <div align="center">
     *   <table style="width:400px; height:300px; border-collapse:collapse; border:1px solid black" border="1">
     *     <tr>
     *       <td style="width:200px" rowspan="5">
     *         <div align="center">
     *           <code>&larr; startColumn &rarr;</code>
     *         </div>
     *       </td>
     *       <td style="width:150px; height:25px; background:lightgray">
     *         <div align="center">
     *           <code>A</code>
     *         </div>
     *       </td>
     *       <td style="width:50px" rowspan="5">
     *         <div align="center">
     *           <code>&uarr;<br>
     *           n<sub>1</sub><br>
     *           &darr;<br></code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px; background:lightgray">
     *         <div align="center">
     *           <code>B</code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px; background:lightgray">
     *         <div align="center">
     *           <code>C</code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px; background:lightgray">
     *         <div align="center">
     *           <code>D</code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:200px; background:lightgray">
     *         <div align="center">
     *           <code>&larr; b &rarr;</code>
     *         </div>
     *       </td>
     *     </tr>
     *   </table>
     *   <code>&larr; n<sub>2</sub> &rarr; </code>
     * </div>
     *
     * The data is read from an n<sub>1</sub> x b area of the matrix, in blocks
     * of b elements, to a b x n<sub>1</sub> memory array as follows:<p>
     *
     * <div align="center">
     *   <table style="width:300px; height:100px; border-collapse:collapse; border:1px solid black; background:lightgray" border="1">
     *     <tr>
     *       <td style="width:100px; height:25px">
     *         <div align="center">
     *           <code>A</code>
     *         </div>
     *       </td>
     *       <td style="width:200px" rowspan="4">
     *         <div align="center">
     *           <code>&uarr;<br>
     *           b<br>
     *           &darr;<br></code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px">
     *         <div align="center">
     *           <code>B</code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px">
     *         <div align="center">
     *           <code>C</code>
     *         </div>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td style="width:100px; height:25px">
     *         <div align="center">
     *           <code>D</code>
     *         </div>
     *       </td>
     *     </tr>
     *   </table>
     *   <code>&larr; n<sub>1</sub> &rarr;</code>
     * </div>
     *
     * Each b x b block is transposed, to form the final b x n<sub>1</sub> array
     * in memory, where the columns are located linearly:<p>
     *
     * <div align="center">
     *   <table style="width:300px; height:100px; border-collapse:collapse; border:1px solid black; background:lightgray" border="1">
     *     <tr>
     *       <td style="width:25px; height:100px">
     *         <div align="center">
     *           <code>A</code>
     *         </div>
     *       </td>
     *       <td style="width:25px; height:100px">
     *         <div align="center">
     *           <code>B</code>
     *         </div>
     *       </td>
     *       <td style="width:25px; height:100px">
     *         <div align="center">
     *           <code>C</code>
     *         </div>
     *       </td>
     *       <td style="width:25px; height:100px">
     *         <div align="center">
     *           <code>D</code>
     *         </div>
     *       </td>
     *       <td style="width:200px" rowspan="4">
     *         <div align="center">
     *           <code>&uarr;<br>
     *           b<br>
     *           &darr;<br></code>
     *         </div>
     *       </td>
     *     </tr>
     *   </table>
     *   <code>&larr; n<sub>1</sub> &rarr;</code>
     * </div>
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is read.
     * @param columns The number of columns of data to read.
     * @param rows The number of rows of data to read. This should be equivalent to n<sub>1</sub>, number of rows in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the transposed data.
     *
     * @exception IllegalArgumentException If the requested area is out of bounds of the data storage.
     * @exception IllegalStateException If write access is requested for a read-only data storage.
     */

    public final ArrayAccess getTransposedArray(int mode, int startColumn, int columns, int rows)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException
    {
        if (isReadOnly() && (mode & WRITE) != 0)
        {
            throw new IllegalStateException("Write access requested for read-only data storage");
        }

        long size = (long) columns * rows;
        if (startColumn < 0 || columns < 0 || rows < 0 || startColumn + columns < 0 ||
            (long) (startColumn + columns) * rows > getSize())
        {
            throw new IllegalArgumentException("Requested block out of range: startColumn=" + startColumn + ", columns=" + columns + ", rows=" + rows + ", available=" + getSize());
        }
        else if (size > Integer.MAX_VALUE)
        {
            throw new ApfloatRuntimeException("Block too large to fit in an array: " + size);
        }

        return implGetTransposedArray(mode, startColumn, columns, rows);
    }

    /**
     * Maps a transposed block of data to a memory array when the data is treated as a matrix.
     * The validity of the arguments of this method do not need to be
     * checked.
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is read.
     * @param columns The number of columns of data to read.
     * @param rows The number of rows of data to read. This should be equivalent to n<sub>1</sub>, number of rows in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the transposed data.
     */

    protected abstract ArrayAccess implGetTransposedArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException;

    /**
     * Constructs a new iterator. Elements can be iterated either
     * in forward or in reverse order, depending on if <code>startPosition</code>
     * is less than or greater than <code>endPosition</code>, correspondingly.
     *
     * @param mode Access mode for iterator: {@link #READ}, {@link #WRITE} or both.
     * @param startPosition Starting position of iterator in the data set. For reverse access, the first element in the iterator is <code>startPosition - 1</code>.
     * @param endPosition End position of iterator in the data set. For forward access, the last accessible element in the iterator is <code>endPosition - 1</code>.
     *
     * @return An iterator.
     *
     * @exception IllegalArgumentException If the requested area is out of bounds of the data storage.
     * @exception IllegalStateException If write access is requested for a read-only data storage.
     */

    public abstract Iterator iterator(int mode, long startPosition, long endPosition)
        throws IllegalArgumentException, IllegalStateException, ApfloatRuntimeException;

    /**
     * Is this object a subsequence of some other object, or do subsequences of this object exist.
     *
     * @return <code>true</code> if this object is a subsequence of some other object, or if subsequences of this object exist, <code>false</code> otherwise.
     */

    public final boolean isSubsequenced()
    {
        if (this.originalDataStorage == null)
        {
            return this.isSubsequenced;
        }
        else
        {
            return true;
        }
    }

    /**
     * Is this object cached in memory.
     *
     * @return <code>true</code> if this object is cached in memory, <code>false</code> if not.
     *
     * @since 1.7.0
     */

    public abstract boolean isCached();

    /**
     * Return the sub-sequence offset.
     *
     * @return Absolute offset of the sub-sequence within the (top-level) base data storage.
     */

    protected final long getOffset()
    {
        return this.offset;
    }

    private void setSubsequenced()
        throws ApfloatRuntimeException
    {
        if (!isSubsequenced())
        {
            if (!isReadOnly())
            {
                this.length = implGetSize();        // Size can't be changed after this
            }
            // This may be called even after the DataStorage has been set to be read-only, but the mutation behaves
            // consistently even if done from multiple threads at the same time
            this.isSubsequenced = true;             // Subsequences exist for this object
        }
    }

    private static final long serialVersionUID = 1862028601696578467L;

    private long offset;
    private long length;
    private DataStorage originalDataStorage;
    private boolean isReadOnly;
    private boolean isSubsequenced;
}
