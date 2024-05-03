package org.matheclipse.core.convert.matlab;

import java.io.IOException;
import java.io.NotSerializableException;
import org.hipparchus.linear.AnyMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.interfaces.IAST;
import us.hebi.matlab.mat.format.Mat5Type;
import us.hebi.matlab.mat.types.MatlabType;
import us.hebi.matlab.mat.types.Sink;

class AnyMatrixWrapper extends AbstractMatrixWrapper<AnyMatrix> {

  AnyMatrixWrapper(AnyMatrix matrix) {
    super(matrix);
  }

  @Override
  protected int getMat5DataSize() {
    return Mat5Type.Double
        .computeSerializedSize(matrix.getRowDimension() * matrix.getColumnDimension());
  }

  @Override
  public MatlabType getType() {
    return MatlabType.Double;
  }

  @Override
  protected void writeMat5Data(Sink sink) throws IOException {
    // Real data in column major format
    if (matrix instanceof ASTRealMatrix) {
      ASTRealMatrix astMatrix = (ASTRealMatrix) matrix;
      RealMatrix realMatrix = astMatrix.getRealMatrix();
      int rows = realMatrix.getRowDimension();
      int columns = realMatrix.getColumnDimension();
      int getNumElements = rows * columns;

      Mat5Type.Double.writeTag(getNumElements, sink);
      for (int col = 0; col < rows; col++) {
        for (int row = 0; row < columns; row++) {
          sink.writeDouble(realMatrix.getEntry(row, col));
        }
      }
      Mat5Type.Double.writePadding(getNumElements, sink);
    } else if (matrix instanceof IAST) {
      try {
        IAST astMatrix = (IAST) matrix;
        int rows = astMatrix.getRowDimension();
        int columns = astMatrix.getColumnDimension();
        int getNumElements = rows * columns;

        Mat5Type.Double.writeTag(getNumElements, sink);
        for (int col = 0; col < rows; col++) {
          for (int row = 0; row < columns; row++) {
            sink.writeDouble(astMatrix.getPart(row + 1, col + 1).evalf());
          }
        }
        Mat5Type.Double.writePadding(getNumElements, sink);
      } catch (SymjaMathException sme) {
        throw new NotSerializableException();
      }
    }
  }

}
