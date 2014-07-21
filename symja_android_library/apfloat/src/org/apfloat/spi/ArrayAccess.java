package org.apfloat.spi;

import java.io.Serializable;

import org.apfloat.ApfloatRuntimeException;

/**
 * The <code>ArrayAccess</code> class simulates a <code>C</code> language pointer.
 * With one <code>ArrayAccess</code> object you can point to a location within an
 * array. You can easily add or subtract a value to this "pointer", thus
 * essentially emulating <code>C</code> pointer arithmetic. An <code>ArrayAccess</code>
 * provides an array, the starting index within that array and the length of
 * accessible data within the array, all in one convenient package.<p>
 *
 * Just like pointers in the <code>C</code> language, <code>ArrayAccess</code>
 * objects are inherently unsafe and must be used cautiously. It is the
 * responsibility of the user of an <code>ArrayAccess</code> object to make sure
 * that he doesn't access the provided array outside the allowed range. The
 * <code>ArrayAccess</code> object itself does nothing to enforce this, except
 * of course the mandatory bounds check of Java, which can throw an
 * <code>ArrayIndexOutOfBoundsException</code>.
 *
 * @version 1.6.3
 * @author Mikko Tommila
 */

public abstract class ArrayAccess
    implements Serializable
{
    /**
     * Create an array access.<p>
     *
     * @param offset The offset of the access segment within the array.
     * @param length The access segment.
     */

    protected ArrayAccess(int offset, int length)
    {
        this.offset = offset;
        this.length = length;
    }

    /**
     * Create a sub-sequence view of this array access.<p>
     *
     * Note that the changes done to the sub-sequence array
     * are not necessarily committed to the underlying data
     * storage when the sub-sequence is closed (with {@link #close()}),
     * but only when the "base" <code>ArrayAccess</code> is closed.
     *
     * @param offset The sub-sequence starting offset within this ArrayAccess.
     * @param length The sub-sequence length.
     *
     * @return The sub-sequence array access.
     */

    public abstract ArrayAccess subsequence(int offset, int length);

    /**
     * Returns the array of this array access. This is an array of a
     * primitive type, depending on the implementation class.
     *
     * @return The backing array of this array access.
     */

    public abstract Object getData()
        throws ApfloatRuntimeException;

    /**
     * Returns the array of this array access as an <code>int[]</code>.
     *
     * @return The backing array of this array access.
     *
     * @exception UnsupportedOperationException In case the backing array can't be presented as <code>int[]</code>.
     */

    public int[] getIntData()
        throws UnsupportedOperationException, ApfloatRuntimeException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the array of this array access as a <code>long[]</code>.
     *
     * @return The backing array of this array access.
     *
     * @exception UnsupportedOperationException In case the backing array can't be presented as <code>long[]</code>.
     */

    public long[] getLongData()
        throws UnsupportedOperationException, ApfloatRuntimeException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the array of this array access as a <code>float[]</code>.
     *
     * @return The backing array of this array access.
     *
     * @exception UnsupportedOperationException In case the backing array can't be presented as <code>float[]</code>.
     */

    public float[] getFloatData()
        throws UnsupportedOperationException, ApfloatRuntimeException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the array of this array access as a <code>double[]</code>.
     *
     * @return The backing array of this array access.
     *
     * @exception UnsupportedOperationException In case the backing array can't be presented as <code>double[]</code>.
     */

    public double[] getDoubleData()
        throws UnsupportedOperationException, ApfloatRuntimeException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the offset of the access segment within the backing array.
     *
     * @return The starting index within the backing array that is allowed to be accessed.
     */

    public int getOffset()
    {
        return this.offset;
    }

    /**
     * Returns the length of the access segment within the backing array.
     *
     * @return The number of elements within the backing array that is allowed to be accessed.
     */

    public int getLength()
    {
        return this.length;
    }

    /**
     * Close this array access and commit any changes to the
     * underlying data storage if applicable.<p>
     *
     * If the <code>ArrayAccess</code> was obtained in write
     * mode, the changes are saved to the data storage. Note
     * that even if the <code>ArrayAccess</code> was obtained
     * for reading only, any changes made to the array data
     * may still be committed to the data storage.<p>
     *
     * Note that changes done to a sub-sequence array
     * are not necessarily committed to the underlying data
     * storage when the sub-sequence is closed,
     * but only when the "base" <code>ArrayAccess</code> is closed.
     */

    public abstract void close()
        throws ApfloatRuntimeException;

    private static final long serialVersionUID = -7899494275459577958L;

    private int offset;
    private int length;
}
