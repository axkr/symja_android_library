package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.Util;

/**
 * Abstract superclass for step-based FNT strategies.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @see NTTStepStrategy
 *
 * @since 1.7.0
 * @version 1.8.0
 * @author Mikko Tommila
 */

public abstract class AbstractStepFNTStrategy
    implements NTTStrategy, Parallelizable
{
    /**
     * Subclass constructor.
     */

    protected AbstractStepFNTStrategy()
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        this.stepStrategy = ctx.getBuilderFactory().getNTTBuilder().createNTTSteps();
    }

    public void transform(DataStorage dataStorage, int modulus)
        throws ApfloatRuntimeException
    {
        long length = dataStorage.getSize();            // Transform length n

        if (length > this.stepStrategy.getMaxTransformLength())
        {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + length + " > " + this.stepStrategy.getMaxTransformLength());
        }

        if (length < 2)
        {
            return;
        }

        assert (length == (length & -length));          // Must be a power of two

        // Treat the input data as a n1 x n2 matrix

        int logLength = Util.log2down(length),
            n1 = logLength >> 1,
            n2 = logLength - n1;

        n1 = 1 << n1;
        n2 = 1 << n2;

        // Now n2 >= n1

        transform(dataStorage, n1, n2, length, modulus);
    }

    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength)
        throws ApfloatRuntimeException
    {
        long length = dataStorage.getSize();            // Transform length n

        if (Math.max(length, totalTransformLength) > this.stepStrategy.getMaxTransformLength())
        {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + Math.max(length, totalTransformLength) + " > " + this.stepStrategy.getMaxTransformLength());
        }

        if (length < 2)
        {
            return;
        }

        assert (length == (length & -length));          // Must be a power of two

        // Treat the input data as a n1 x n2 matrix

        int logLength = Util.log2down(length),
            n1 = logLength >> 1,
            n2 = logLength - n1;

        n1 = 1 << n1;
        n2 = 1 << n2;

        // Now n2 >= n1

        inverseTransform(dataStorage, n1, n2, length, totalTransformLength, modulus);
    }

    public long getTransformLength(long size)
    {
        return Util.round2up(size);
    }

    /**
     * Transform the data in steps.
     *
     * @param dataStorage The data.
     * @param n1 Height of the data matrix.
     * @param n2 Width of the data matrix.
     * @param length Length of the data.
     * @param modulus Which modulus to use.
     */

    protected abstract void transform(DataStorage dataStorage, int n1, int n2, long length, int modulus)
        throws ApfloatRuntimeException;

    /**
     * Inverse transform the data in steps.
     *
     * @param dataStorage The data.
     * @param n1 Height of the data matrix.
     * @param n2 Width of the data matrix.
     * @param length Length of the data.
     * @param totalTransformLength Total transform length.
     * @param modulus Which modulus to use.
     */

    protected abstract void inverseTransform(DataStorage dataStorage, int n1, int n2, long length, long totalTransformLength, int modulus)
        throws ApfloatRuntimeException;

    /**
     * The NTT steps.
     */

    protected NTTStepStrategy stepStrategy;
}
