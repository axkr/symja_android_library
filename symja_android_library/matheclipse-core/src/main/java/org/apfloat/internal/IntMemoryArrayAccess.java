package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

/**
 * Array access class based on a <code>int[]</code>.
 *
 * @version 1.6.3
 * @author Mikko Tommila
 */

public class IntMemoryArrayAccess
    extends ArrayAccess
{
    /**
     * Create an array access.<p>
     *
     * @param data The underlying array.
     * @param offset The offset of the access segment within the array.
     * @param length The access segment.
     */

    public IntMemoryArrayAccess(int[] data, int offset, int length)
    {
        super(offset, length);
        this.data = data;
    }

    public ArrayAccess subsequence(int offset, int length)
    {
        return new IntMemoryArrayAccess(this.data, getOffset() + offset, length);
    }

    public Object getData()
    {
        return this.data;
    }

    public int[] getIntData()
    {
        return this.data;
    }

    public void close()
        throws ApfloatRuntimeException
    {
        this.data = null;       // Might have an impact on garbage collection
    }

    private static final long serialVersionUID = -1137159053668908693L;

    private int[] data;
}
