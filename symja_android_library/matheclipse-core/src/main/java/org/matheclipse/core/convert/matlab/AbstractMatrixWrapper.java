package org.matheclipse.core.convert.matlab;

import static us.hebi.matlab.mat.format.Mat5WriteUtil.computeArrayHeaderSize;
import static us.hebi.matlab.mat.format.Mat5WriteUtil.writeArrayHeader;
import static us.hebi.matlab.mat.format.Mat5WriteUtil.writeMatrixTag;
import java.io.IOException;
import org.hipparchus.linear.AnyMatrix;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5Serializable;
import us.hebi.matlab.mat.types.AbstractArray;
import us.hebi.matlab.mat.types.Sink;

/**
 * Serializes a Symja Matrix into a MAT 5 file that can be read by MATLAB
 *
 * Note that implementing 'Mat5Attributes' lets us get around the overhead of implementing the
 * Matrix / Sparse interfaces, or alternatively writing the header manually.
 * 
 */
abstract class AbstractMatrixWrapper<M extends AnyMatrix> extends AbstractArray
    implements Mat5Serializable, Mat5Serializable.Mat5Attributes {

    protected final M matrix;

    protected AbstractMatrixWrapper(M matrix) {
      super(Mat5.dims(matrix.getRowDimension(), matrix.getColumnDimension()));
        this.matrix = matrix;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public int[] getDimensions() {
      dims[0] = matrix.getRowDimension();
      dims[1] = matrix.getColumnDimension();
        return dims;
    }

    protected abstract int getMat5DataSize();

    @Override
    public int getMat5Size(String name) {
        return Mat5.MATRIX_TAG_SIZE
                + computeArrayHeaderSize(name, this)
                + getMat5DataSize();
    }

    @Override
    public int getNzMax() {
        return 0;
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isLogical() {
        return false;
    }

    @Override
    protected boolean subEqualsGuaranteedSameClass(Object otherGuaranteedSameClass) {
      AnyMatrixWrapper other = (AnyMatrixWrapper) otherGuaranteedSameClass;
      return other.matrix.equals(matrix);
    }

    @Override
    protected int subHashCode() {
        return matrix.hashCode();
    }

    @Override
    public void writeMat5(String name, boolean isGlobal, Sink sink) throws IOException {
        writeMatrixTag(name, this, sink);
        writeArrayHeader(name, isGlobal, this, sink);
        writeMat5Data(sink);
      }

    /**
     * Writes data part in column-major order
     *
     * @param sink
     * @throws IOException
     */
    protected abstract void writeMat5Data(Sink sink) throws IOException;

}
