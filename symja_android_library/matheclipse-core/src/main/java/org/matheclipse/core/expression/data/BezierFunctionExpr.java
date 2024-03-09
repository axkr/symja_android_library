package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.itp.BinaryAverage;
import org.matheclipse.core.tensor.itp.LinearBinaryAverage;

public class BezierFunctionExpr extends DataExpr<IAST> implements Externalizable {
  public static BezierFunctionExpr newInstance(BinaryAverage binaryAverage, IAST sequence,
      double[][] points) {
    return new BezierFunctionExpr(binaryAverage, sequence, points);
  }

  private BinaryAverage binaryAverage;
  private double[][] points;

  public BezierFunctionExpr() {
    super(S.BezierFunction, F.CEmptyList);
    this.binaryAverage = LinearBinaryAverage.INSTANCE;
  }

  protected BezierFunctionExpr(BinaryAverage binaryAverage, IAST sequence, double[][] points) {
    super(S.BezierFunction, sequence);
    this.binaryAverage = binaryAverage;
    this.points = points;
  }

  @Override
  public IExpr copy() {
    return new BezierFunctionExpr(binaryAverage, fData, points);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BezierFunctionExpr) {
      return fData.equals(((BezierFunctionExpr) obj).fData);
    }
    return false;
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof BezierFunctionExpr && ast.isAST1()) {
      double scalar;
      try {
        scalar = ast.arg1().evalf();
      } catch (ArgumentTypeException ate) {
        return F.NIL;
      }
      double[][] pts = copyMatrix(points);

      for (int i = pts.length; 1 <= i; --i) {
        int count = -1;
        double[] p = pts[0];
        for (int index = 1; index < i; ++index)
          pts[++count] = binaryAverage.split(p, p = pts[index], scalar);
      }
      return F.List(pts[0]);
    }

    return F.NIL;
  }

  private static double[][] copyMatrix(final double[][] points) {
    final int rows = points.length;
    final int columns = points[0].length;
    double[][] pts = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        pts[i][j] = points[i][j];
      }
    }
    return pts;
  }

  public BinaryAverage getBinaryAverage() {
    return binaryAverage;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return BEZIERFUNCTONID;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    IAST sequence = toData();
    return sequence;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.fData = (IAST) in.readObject();
  }

  @Override
  public String toString() {
    return fHead.toString() + "(" + fData + ")";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
