package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;

import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.NTTBuilder;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.CarryCRTBuilder;

/**
 * Factory class for getting instances of the various builder classes needed
 * to build an <code>ApfloatImpl</code> with the <code>long</code> data element type.
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class LongBuilderFactory
    implements BuilderFactory
{
    /**
     * Default constructor.
     */

    public LongBuilderFactory()
    {
    }

    public ApfloatBuilder getApfloatBuilder()
    {
        return LongBuilderFactory.apfloatBuilder;
    }

    public DataStorageBuilder getDataStorageBuilder()
    {
        return LongBuilderFactory.dataStorageBuilder;
    }

    public <T> AdditionBuilder<T> getAdditionBuilder(Class<T> elementType)
        throws IllegalArgumentException
    {
        if (!Long.TYPE.equals(elementType))
        {
           throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
        @SuppressWarnings("unchecked")
        AdditionBuilder<T> additionBuilder = (AdditionBuilder<T>) LongBuilderFactory.additionBuilder;
        return additionBuilder;
    }

    public ConvolutionBuilder getConvolutionBuilder()
    {
        return LongBuilderFactory.convolutionBuilder;
    }

    public NTTBuilder getNTTBuilder()
    {
        return LongBuilderFactory.nttBuilder;
    }

    public MatrixBuilder getMatrixBuilder()
    {
        return LongBuilderFactory.matrixBuilder;
    }

    public <T> CarryCRTBuilder<T> getCarryCRTBuilder(Class<T> elementArrayType)
        throws IllegalArgumentException
    {
        if (!long[].class.equals(elementArrayType))
        {
           throw new IllegalArgumentException("Unsupported element array type: " + elementArrayType);
        }
        @SuppressWarnings("unchecked")
        CarryCRTBuilder<T> carryCRTBuilder = (CarryCRTBuilder<T>) LongBuilderFactory.carryCRTBuilder;
        return carryCRTBuilder;
    }

    public Class<?> getElementType()
    {
        return Long.TYPE;
    }

    public Class<?> getElementArrayType()
    {
        return long[].class;
    }

    public int getElementSize()
    {
        return 8;
    }

    public void shutdown()
        throws ApfloatRuntimeException
    {
        DiskDataStorage.cleanUp();
    }

    public void gc()
        throws ApfloatRuntimeException
    {
        System.gc();
        System.gc();
        System.runFinalization();
        DiskDataStorage.gc();
    }

    private static ApfloatBuilder apfloatBuilder = new LongApfloatBuilder();
    private static DataStorageBuilder dataStorageBuilder = new LongDataStorageBuilder();
    private static AdditionBuilder<Long> additionBuilder = new LongAdditionBuilder();
    private static ConvolutionBuilder convolutionBuilder = new LongConvolutionBuilder();
    private static NTTBuilder nttBuilder = new LongNTTBuilder();
    private static MatrixBuilder matrixBuilder = new LongMatrixBuilder();
    private static CarryCRTBuilder<long[]> carryCRTBuilder = new LongCarryCRTBuilder();
}
