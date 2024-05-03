package org.matheclipse.core.convert.matlab;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.hipparchus.linear.AnyMatrix;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.AbstractMatrixBase;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.MatFile;
import us.hebi.matlab.mat.types.MatlabType;
import us.hebi.matlab.mat.types.Matrix;
import us.hebi.matlab.mat.types.Source;
import us.hebi.matlab.mat.types.Sources;

/**
 * Matlab file format conversion to Symja
 */
public class Mat5Symja {

  private static AnyMatrix convertToAnyMatrix(AbstractMatrixBase input,
      Class<? extends AnyMatrix> clazz) {
    final int rows = input.getNumRows();
    final int cols = input.getNumCols();
    if (clazz.isAssignableFrom(ASTRealMatrix.class)) {
      RealMatrix realMatrix = convertToArray2DRowRealMatrix(input, rows, cols);
      return new ASTRealMatrix(realMatrix, false);
    } else if (clazz.isAssignableFrom(IAST.class)) {
      IASTAppendable astMatrix = F.ListAlloc(rows);
      for (int i = 0; i < rows; i++) {
        astMatrix.append(F.ListAlloc(cols));
      }
      for (int col = 0; col < cols; col++) {
        for (int row = 0; row < rows; row++) {
          astMatrix.setPart(input.getDouble(row, col), row, col);
        }
      }
    } else if (clazz.isAssignableFrom(RealMatrix.class)) {
      return convertToArray2DRowRealMatrix(input, rows, cols);
    }
    return F.NIL;
  }

  private static RealMatrix convertToArray2DRowRealMatrix(Matrix input, final int rows,
      final int cols) {
    RealMatrix realMatrix = new Array2DRowRealMatrix(rows, cols);
    for (int col = 0; col < cols; col++) {
      for (int row = 0; row < rows; row++) {
        realMatrix.setEntry(row, col, input.getDouble(row, col));
      }
    }
    return realMatrix;
  }

  public static IAST getTensor(AbstractMatrixBase baseMatrix) {
    int[] dimensions = baseMatrix.getDimensions();
    if (dimensions.length == 2) {
      if (baseMatrix.getType() == MatlabType.Double) {
        return (ASTRealMatrix) Mat5Symja.convertToAnyMatrix(baseMatrix, ASTRealMatrix.class);
      }
      if (baseMatrix.getType() == MatlabType.Sparse) {
        return (IAST) Mat5Symja.convertToAnyMatrix(baseMatrix, IAST.class);
      }
    }
    if (baseMatrix.getType() == MatlabType.Sparse) {
      return F.NIL;
      // int[] indices = new int[dimensions.length];
      // final int size = dimensions[0];
      // ISparseArray sparse =
      // F.sparseArray(F.List(F.Rule(F.List(1, 2, 3), F.b), F.Rule(F.List(1, 4, 5), F.a)));
      // IASTAppendable result = F.ListAlloc();
      // for (int i = 0; i < size; i++) {
      // indices[0] = i;
      // getSparseRecursive(baseMatrix, dimensions, indices, 1, result);
      // }
      // return result;
    }
    int[] indices = new int[dimensions.length];
    final int size = dimensions[0];
    IASTAppendable result = F.ListAlloc(size);
    for (int i = 0; i < size; i++) {
      indices[0] = i;
      getTensorRecursive(baseMatrix, dimensions, indices, 1, result);
    }
    return result;
  }

  private static void getSparseRecursive(AbstractMatrixBase baseMatrix, int[] dimensions,
      int[] indices, int indexCounter, IASTAppendable result) {
    int newCounter = indexCounter + 1;
    if (indexCounter == dimensions.length) {
      MatlabType type = baseMatrix.getType();
      switch (type) {
        case UInt8:
          result.append(baseMatrix.getBoolean(indices));
          return;
        case Double:
          result.append(baseMatrix.getDouble(indices));
          return;
        case Single:
          result.append(baseMatrix.getFloat(indices));
          return;
        case Sparse:
          double d = baseMatrix.getDouble(indices);
          result.append(d);
          return;
      }
      return;
    }
    final int size = dimensions[indexCounter];
    IASTAppendable subRow = F.ListAlloc();
    for (int i = 0; i < size; i++) {
      indices[indexCounter] = i;
      getTensorRecursive(baseMatrix, dimensions, indices, newCounter, subRow);
    }
    result.append(subRow);
  }

  private static void getTensorRecursive(AbstractMatrixBase baseMatrix, int[] dimensions,
      int[] indices, int indexCounter, IASTAppendable result) {
    int newCounter = indexCounter + 1;
    if (indexCounter == dimensions.length) {
      MatlabType type = baseMatrix.getType();
      switch (type) {
        case UInt8:
          result.append(baseMatrix.getBoolean(indices));
          return;
        case Double:
          result.append(baseMatrix.getDouble(indices));
          return;
        case Single:
          result.append(baseMatrix.getFloat(indices));
          return;
      }
      return;
    }
    final int size = dimensions[indexCounter];
    IASTAppendable subRow = F.ListAlloc(size);
    for (int i = 0; i < size; i++) {
      indices[indexCounter] = i;
      getTensorRecursive(baseMatrix, dimensions, indices, newCounter, subRow);
    }
    result.append(subRow);
  }

  public static IExpr importMAT(InputStream inputStream, String inputName)
      throws IOException, AssertionError {
    ByteBuffer buffer = ByteBuffer.allocate(inputStream.available());
    int bytes = inputStream.read(buffer.array());
    if (bytes != buffer.array().length) {
      throw new AssertionError("Could not read full contents of " + inputName);
    }
    try (Source source = Sources.wrap(buffer)) {
      Mat5File mat = Mat5.newReader(source)//
          .setReducedHeader(false)//
          .readMat();
      System.out.println(mat.toString());
      for (MatFile.Entry entry : mat.getEntries()) {
        // String name = entry.getName();
        Array value = entry.getValue();
        if (value instanceof AbstractMatrixBase) {
          return getTensor((AbstractMatrixBase) value);
        }
      }
    }
    return F.NIL;
  }
}
