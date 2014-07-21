package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.MatrixStrategy;

/**
 * Fast Number Theoretic Transform that uses a "six-step"
 * algorithm to calculate a long transform more efficiently on
 * cache-based memory architectures.<p>
 *
 * When the data to be transformed is considered to be an
 * n<sub>1</sub> x n<sub>2</sub> matrix of data, instead of a linear array,
 * the six steps are as follows:<p>
 *
 * <ol>
 *   <li>Transpose the matrix.</li>
 *   <li>Transform the rows.</li>
 *   <li>Transpose the matrix.</li>
 *   <li>Multiply each matrix element by w<sup>i j</sup> (where w is the n:th root of unity).</li>
 *   <li>Transform the rows.</li>
 *   <li>Transpose the matrix.</li>
 * </ol>
 *
 * In a convolution algorithm the last transposition step can be omitted
 * to increase performance, as well as the first transposition step in
 * the inverse transform. The convolution's element-by-element multiplication
 * is not sensitive to the order in which the elements are.
 * Also scrambling the data can be omitted.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class SixStepFNTStrategy
    extends AbstractStepFNTStrategy
{
    /**
     * Default constructor.
     */

    public SixStepFNTStrategy()
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        this.matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();
    }

    protected void transform(DataStorage dataStorage, int n1, int n2, long length, int modulus)
        throws ApfloatRuntimeException
    {
        if (length > Integer.MAX_VALUE)
        {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }

        assert (n2 >= n1);

        ArrayAccess arrayAccess = dataStorage.getArray(DataStorage.READ_WRITE, 0, (int) length);

        // Step 1: Transpose the data
        transposeInitial(arrayAccess, n1, n2, false);

        // Step 2: Do n2 transforms of length n1
        transformFirst(arrayAccess, n1, n2, false, modulus);

        // Step 3: Transpose the data
        transposeMiddle(arrayAccess, n2, n1, false);

        // Step 4: Multiply each matrix element by w^(i*j)
        multiplyElements(arrayAccess, n1, n2, length, 1, false, modulus);

        // Step 5: Do n1 transforms of length n2
        transformSecond(arrayAccess, n2, n1, false, modulus);

        // Step 6: Transpose the data - omitted as unnecessary
        transposeFinal(arrayAccess, n1, n2, false);

        arrayAccess.close();
    }

    protected void inverseTransform(DataStorage dataStorage, int n1, int n2, long length, long totalTransformLength, int modulus)
        throws ApfloatRuntimeException
    {
        if (length > Integer.MAX_VALUE)
        {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }

        assert (n2 >= n1);

        ArrayAccess arrayAccess = dataStorage.getArray(DataStorage.READ_WRITE, 0, (int) length);

        // Step 1: Transpose the data - omitted as unnecessary
        transposeFinal(arrayAccess, n2, n1, true);

        // Step 2: Do n1 transforms of length n2
        transformSecond(arrayAccess, n2, n1, true, modulus);

        // Step 3: Multiply each matrix element by w^(i*j) / totalTransformLength
        multiplyElements(arrayAccess, n1, n2, length, totalTransformLength, true, modulus);

        // Step 4: Transpose the data
        transposeMiddle(arrayAccess, n1, n2, true);

        // Step 5: Do n2 transforms of length n1
        transformFirst(arrayAccess, n1, n2, true, modulus);

        // Step 6: Transpose the data
        transposeInitial(arrayAccess, n2, n1, true);

        arrayAccess.close();
    }

    /**
     * The initial transpose of the forward transform, or the final transpose
     * of the inverse transform, to transpose the columns of the matrix to be rows.
     * This step is needed in the six-step algorithm but is omitted in the four-step
     * algorithm.
     *
     * @param arrayAccess Accessor to the matrix data. This data will be transposed.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     */

    protected void transposeInitial(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse)
    {
        this.matrixStrategy.transpose(arrayAccess, n1, n2);
    }

    /**
     * The second transpose of either the forward or inverse transform. Normally
     * this step is always required as the four-step algorithm only transforms
     * columns of the matrix and the six-step algorithm transforms only rows.
     *
     * @param arrayAccess Accessor to the matrix data. This data will be transposed.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     */

    protected void transposeMiddle(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse)
    {
        this.matrixStrategy.transpose(arrayAccess, n1, n2);
    }

    /**
     * The final transpose of the forward transform, or the initial transpose
     * of the inverse transform. By default this method does nothing as the step is
     * always unnecessary when the data is only needed for convolution.
     *
     * @param arrayAccess Accessor to the matrix data.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     */

    protected void transposeFinal(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse)
    {
        // Omitted as unnecessary
    }

    /**
     * The first transform of the rows (or columns) of the data matrix.
     * In the default implementation the rows are transformed because in the
     * forward transform the matrix is transposed first. In the inverse transform
     * the matrix is initially in transposed form as it was left like that by the
     * forward transform.<p>
     *
     * By default the row transforms permute the data, leaving it in the correct
     * order so the element-by-element multiplication is simpler.
     *
     * @param arrayAccess The memory array to split and transform.
     * @param length Length of one transform (one row physically, by default).
     * @param count Number of transforms.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     * @param modulus Index of the modulus.
     */

    protected void transformFirst(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus)
    {
        super.stepStrategy.transformRows(arrayAccess, length, count, isInverse, true, modulus);
    }

    /**
     * The second transform of the rows (or columns) of the data matrix.
     * In the default implementation the rows are transformed because in the
     * forward transform the matrix is transposed first. In the inverse transform
     * the matrix is initially in transposed form as it was left like that by the
     * forward transform.<p>
     *
     * By default the row transforms do not permute the data, leaving it in
     * scrambled order, as this does not matter when the data is only used for
     * convolution.
     *
     * @param arrayAccess The memory array to split to rows and to transform.
     * @param length Length of one transform (one row).
     * @param count Number of rows.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     * @param modulus Index of the modulus.
     */

    protected void transformSecond(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus)
    {
        super.stepStrategy.transformRows(arrayAccess, length, count, isInverse, false, modulus);
    }

    /**
     * Multiply each matrix element by a power of the n:th root of unity.
     *
     * @param arrayAccess The memory array to multiply.
     * @param rows The number of rows in the <code>arrayAccess</code> to multiply.
     * @param columns The number of columns in the matrix (= n<sub>2</sub>).
     * @param length The length of data in the matrix being transformed.
     * @param totalTransformLength The total transform length, for the scaling factor. Used only for the inverse case.
     * @param isInverse If the multiplication is done for the inverse transform or not.
     * @param modulus Index of the modulus.
     */

    protected void multiplyElements(ArrayAccess arrayAccess, int rows, int columns, long length, long totalTransformLength, boolean isInverse, int modulus)
    {
        super.stepStrategy.multiplyElements(arrayAccess, 0, 0, rows, columns, length, totalTransformLength, isInverse, modulus);
    }

    private MatrixStrategy matrixStrategy;
}
