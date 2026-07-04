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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.itp.BinaryAverage;
import org.matheclipse.core.tensor.itp.LinearBinaryAverage;

/**
 * Represents a Bezier function expression {@link S#BezierFunction} stored as data inside the
 * MathEclipse expression tree.
 * <p>
 * The expression wraps an {@link IAST} sequence describing the control points or parameters and a
 * {@link BinaryAverage} strategy used to interpolate points. Instances are serializable via
 * {@link Externalizable}.
 * </p>
 */
public class BezierFunctionExpr extends DataExpr<IAST> implements Externalizable {

  /**
   * Factory method to create a new {@link BezierFunctionExpr}.
   *
   * @param binaryAverage the interpolation strategy to use (must implement {@link BinaryAverage})
   * @param sequence the IAST sequence describing control points or parameters
   * @param points the control points as a 2D array (rows x dimensions)
   * @return a new {@link BezierFunctionExpr} instance
   */
  public static BezierFunctionExpr newInstance(BinaryAverage binaryAverage, IAST sequence,
      double[][] points) {
    return new BezierFunctionExpr(binaryAverage, sequence, points);
  }

  /**
   * Factory method to create a new {@link BezierFunctionExpr} from the internal 7-argument
   * structure.
   *
   * @param ast the 7-argument IAST representing the evaluated BezierFunction
   * @return a new {@link BezierFunctionExpr} instance, or null if the structure is invalid
   */
  public static BezierFunctionExpr newInstance(IAST ast) {
    if (ast.argSize() == 7) {
      IExpr arg4 = ast.arg4();
      // arg4 contains {pointsMatrix, weights}
      if (arg4.isList() && arg4.argSize() >= 1 && arg4.first().isListOfLists()) {
        IAST list = (IAST) arg4.first();
        double[][] matrix = list.toDoubleMatrix(false);
        if (matrix != null) {
          // Keep the extracted list as the internal fData representation
          return new BezierFunctionExpr(LinearBinaryAverage.INSTANCE, list, matrix);
        }
      }
    }
    return null;
  }

  /**
   * The binary averaging strategy used when computing intermediate points for the Bezier curve.
   * Defaults to {@link LinearBinaryAverage#INSTANCE} in the no-arg constructor.
   */
  private BinaryAverage binaryAverage;

  /**
   * The control points for the Bezier curve stored as a 2D array. Each row is a point in 2D or 3D
   * coordinates; columns correspond to coordinates/dimensions.
   */
  private double[][] points;

  public BezierFunctionExpr() {
    super(S.BezierFunction, F.CEmptyList);
    this.binaryAverage = LinearBinaryAverage.INSTANCE;
  }

  /**
   * Protected constructor used by the factory method to create an instance with the specified
   * interpolation strategy, sequence data and control points.
   *
   * @param binaryAverage the interpolation strategy
   * @param sequence the IAST sequence representing control point data or parameters
   * @param points the control points as a 2D array
   */
  protected BezierFunctionExpr(BinaryAverage binaryAverage, IAST sequence, double[][] points) {
    super(S.BezierFunction, sequence);
    this.binaryAverage = binaryAverage;
    this.points = points;
  }

  /**
   * Create a shallow copy of this expression. The underlying {@link IAST} data and the reference to
   * the points array are shared (no deep copy of the points).
   *
   * @return a new {@link BezierFunctionExpr} with the same internal data
   */
  @Override
  public IExpr copy() {
    return new BezierFunctionExpr(binaryAverage, fData, points);
  }

  /**
   * Equality check based on the wrapped {@link IAST} data. Two {@link BezierFunctionExpr} instances
   * are considered equal if their {@link #toData()} sequences are equal.
   *
   * @param obj the object to compare with
   * @return {@code true} if equal, {@code false} otherwise
   */
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

  /**
   * Evaluate the Bezier function expression when applied to arguments.
   * <p>
   * If the call is of the form BezierFunctionExpr(t) (single numeric argument), the method
   * evaluates the scalar parameter {@code t} and computes the point on the Bezier curve using the
   * configured {@link BinaryAverage} strategy. If the argument is not numeric, {@link F#NIL} is
   * returned to indicate an invalid call.
   * </p>
   *
   * @param ast the AST representing the function application
   * @param engine the evaluation engine used to evaluate arguments
   * @return the computed point as {@link F#List} containing the coordinates or {@link F#NIL}
   */
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

  /**
   * Helper that performs a deep copy of the given 2D points array.
   *
   * @param points the source 2D array to copy
   * @return a newly allocated copy of the points array
   */
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

  @Override
  public IASTAppendable fullForm() {
    if (points == null || points.length == 0) {
      return F.ast(S.BezierFunction);
    }

    IASTAppendable result = F.ast(S.BezierFunction, 8); // Head + 7 arguments

    // 1. Dimension
    result.append(F.C1);

    // 2. Domain
    result.append(F.List(F.List(F.CD0, F.CD1)));

    // 3. Degree
    result.append(F.List(F.ZZ(points.length - 1)));

    // 4. Control Points and Weights
    IASTAppendable pointsList = F.ListAlloc(points.length);
    for (int i = 0; i < points.length; i++) {
      IASTAppendable pt = F.ListAlloc(points[i].length);
      for (int j = 0; j < points[i].length; j++) {
        pt.append(F.num(points[i][j])); // Machine-precision real numbers
      }
      pointsList.append(pt);
    }
    result.append(F.List(pointsList, F.CEmptyList));

    // 5. Flags
    result.append(F.List(F.C0));

    // 6. Precision
    result.append(S.MachinePrecision);

    // 7. Evaluation State
    result.append(F.stringx("Unevaluated"));

    return result;
  }

  /**
   * Accessor for the binary averaging strategy used by this Bezier function.
   *
   * @return the {@link BinaryAverage} instance in use
   */
  public BinaryAverage getBinaryAverage() {
    return binaryAverage;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + fData.hashCode();
  }

  /**
   * Return the internal hierarchy id for this expression type.
   *
   * @return the hierarchy id {@link IExpr#BEZIERFUNCTONID}
   */
  @Override
  public int hierarchy() {
    return BEZIERFUNCTONID;
  }

  /**
   * Return the internal data sequence representing this Bezier function.
   *
   * @param nilIfUnevaluated ignored for this data-holder implementation
   * @return the underlying {@link IAST} sequence
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return toData();
  }


  /**
   * Deserialize the stored {@link IAST} data from the provided input stream.
   *
   * @param in the input stream to read from
   * @throws IOException if an I/O error occurs
   * @throws ClassNotFoundException if the class of a serialized object cannot be found
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.fData = (IAST) in.readObject();
  }

  /**
   * Return a human-readable representation of this Bezier function expression.
   *
   * @return a string of the form "BezierFunction(fData)"
   */
  @Override
  public String toString() {
    return fHead.toString() + "(" + fData + ")";
  }

  /**
   * Serialize the stored {@link IAST} data to the provided output stream.
   *
   * @param output the output stream to write to
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
