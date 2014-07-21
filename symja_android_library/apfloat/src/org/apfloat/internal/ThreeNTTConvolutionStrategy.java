package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.DataStorage;

/**
 * Convolution using three Number Theoretic Transforms
 * and the Chinese Remainder Theorem to get the final result.<p>
 *
 * Multiplication can be done in linear time in the transform domain, where
 * the multiplication is simply an element-by-element multiplication.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class ThreeNTTConvolutionStrategy
    implements ConvolutionStrategy
{
    /**
     * Creates a new convoluter that uses the specified
     * transform for transforming the data.
     *
     * @param radix The radix to be used.
     * @param nttStrategy The transform to be used.
     */

    public ThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy)
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        this.nttStrategy = nttStrategy;
        this.carryCRTStrategy = builderFactory.getCarryCRTBuilder(builderFactory.getElementArrayType()).createCarryCRT(radix);
        this.stepStrategy = builderFactory.getNTTBuilder().createNTTConvolutionSteps();
    }

    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize)
        throws ApfloatRuntimeException
    {
        if (x == y)
        {
            return autoConvolute(x, resultSize);
        }

        long length = this.nttStrategy.getTransformLength(x.getSize() + y.getSize());

        DataStorage result;
        lock(length);
        try
        {
            DataStorage resultMod0 = convoluteOne(x, y, length, 0, false),
                        resultMod1 = convoluteOne(x, y, length, 1, false),
                        resultMod2 = convoluteOne(x, y, length, 2, true);

            result = this.carryCRTStrategy.carryCRT(resultMod0, resultMod1, resultMod2, resultSize);
        }
        finally
        {
            unlock();
        }
        return result;
    }

    /**
     * Performs a convolution modulo one modulus, of the specified transform length.
     *
     * @param x First data set.
     * @param y Second data set.
     * @param length Length of the transformation.
     * @param modulus Which modulus to use.
     * @param cached If the result data should be kept cached in memory when possible.
     *
     * @return The result of the convolution for one modulus.
     */

    protected DataStorage convoluteOne(DataStorage x, DataStorage y, long length, int modulus, boolean cached)
        throws ApfloatRuntimeException
    {
        DataStorage tmpY = createCachedDataStorage(length);
        tmpY.copyFrom(y, length);                               // Using a cached data storage here can avoid an extra write
        this.nttStrategy.transform(tmpY, modulus);
        tmpY = createDataStorage(tmpY);

        DataStorage tmpX = createCachedDataStorage(length);
        tmpX.copyFrom(x, length);
        this.nttStrategy.transform(tmpX, modulus);

        this.stepStrategy.multiplyInPlace(tmpX, tmpY, modulus);

        this.nttStrategy.inverseTransform(tmpX, modulus, length);
        tmpX = (cached ? tmpX : createDataStorage(tmpX));

        return tmpX;
    }

    /**
     * Convolutes a data set with itself.
     *
     * @param x The data set.
     * @param resultSize Number of elements needed in the result data.
     *
     * @return The convolved data.
     */

    protected DataStorage autoConvolute(DataStorage x, long resultSize)
        throws ApfloatRuntimeException
    {
        long length = this.nttStrategy.getTransformLength(x.getSize() * 2);

        DataStorage result;
        lock(length);
        try
        {
            DataStorage resultMod0 = autoConvoluteOne(x, length, 0, false),
                        resultMod1 = autoConvoluteOne(x, length, 1, false),
                        resultMod2 = autoConvoluteOne(x, length, 2, true);

            result = this.carryCRTStrategy.carryCRT(resultMod0, resultMod1, resultMod2, resultSize);
        }
        finally
        {
            unlock();
        }
        return result;
    }

    /**
     * Performs an autoconvolution modulo one modulus, of the specified transform length.
     *
     * @param x The data set.
     * @param length Length of the transformation.
     * @param modulus Which modulus to use.
     * @param cached If the result data should be kept cached in memory when possible.
     *
     * @return The result of the convolution for one modulus.
     */

    protected DataStorage autoConvoluteOne(DataStorage x, long length, int modulus, boolean cached)
        throws ApfloatRuntimeException
    {
        DataStorage tmp = createCachedDataStorage(length);
        tmp.copyFrom(x, length);
        this.nttStrategy.transform(tmp, modulus);

        this.stepStrategy.squareInPlace(tmp, modulus);

        this.nttStrategy.inverseTransform(tmp, modulus, length);
        tmp = (cached ? tmp : createDataStorage(tmp));

        return tmp;
    }

    /**
     * Lock the execution against a synchronization lock.
     *
     * @param length The length of the data being processed for determining the type of lock to use.
     */

    protected void lock(long length)
    {
    }

    /**
     * Remove the synchronization lock.
     */

    protected void unlock()
    {
    }

    /**
     * Create a cached (if possible) data storage for the specified number of elements.
     * 
     * @param size The number of elements.
     *
     * @return The data storage.
     */

    protected DataStorage createCachedDataStorage(long size)
        throws ApfloatRuntimeException
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createCachedDataStorage(size * ctx.getBuilderFactory().getElementSize());
    }

    /**
     * Create a cached data storage from the (possibly) cached data storage.
     *
     * @param dataStorage The data storage, which may be cached.
     *
     * @return The data storage, which isn't cached.
     */

    protected DataStorage createDataStorage(DataStorage dataStorage)
        throws ApfloatRuntimeException
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createDataStorage(dataStorage);
    }

    /**
     * The transform to use.
     */

    protected NTTStrategy nttStrategy;

    /**
     * The carry-CRT to use.
     */

    protected CarryCRTStrategy carryCRTStrategy;

    /**
     * The convolution steps to use.
     */

    protected NTTConvolutionStepStrategy stepStrategy;
}
