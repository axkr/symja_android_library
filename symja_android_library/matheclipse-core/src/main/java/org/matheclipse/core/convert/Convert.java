package org.matheclipse.core.convert;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.hipparchus.analysis.polynomials.PolynomialFunction;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayFieldVector;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;

/** Conversions between an IExpr object and misc other object class types */
public class Convert {

  /**
   * Convert rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code> into <code>
   * java.util.Map</code>.
   *
   * @param astRules rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>
   * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
   */
  public static Map<IExpr, IExpr> rules2Map(IAST astRules) {
    final Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
    IAST rule;
    if (astRules.isListOfLists()) {
      // {{a->b,...},{...}....}
      // what to do in this case?
    } else if (astRules.isList()) {
      // {a->b, c->d, ...}
      if (astRules.size() > 1) {
        // assuming multiple rules in a list
        for (final IExpr expr : astRules) {
          if (expr.isRuleAST()) {
            rule = (IAST) expr;
            map.put(rule.arg1(), rule.arg2());
          }
        }
      }
    } else if (astRules.isRuleAST()) {
      // a->b
      rule = astRules;
      map.put(rule.arg1(), rule.arg2());
    }
    return map;
  }


  private static IExpr[][] list2Array(final IExpr expr, boolean ifNumericReturnNull)
      throws ClassCastException, IndexOutOfBoundsException {
    if (expr == null) {
      return null;
    }
    int[] dim = expr.isMatrix(false);
    if (dim == null || dim[0] == 0 || dim[1] == 0) {
      return null;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IAST currInRow = (IAST) list.arg1();
      if (currInRow.isAST0()) {
        // special case 0-Matrix
        IExpr[][] array = new IExpr[0][0];
        return array;
      }
      final int rowSize = expr.argSize();
      final int colSize = currInRow.argSize();
      if (ifNumericReturnNull) {
        boolean hasInexactNumber = false;
        boolean isNoNumericFunction = true;
        for (int i = 1; i < rowSize + 1; i++) {
          currInRow = (IAST) list.get(i);
          if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
            return null;
          }
          for (int j = 1; j < colSize + 1; j++) {
            final IExpr arg = currInRow.get(j);
            if (arg.isInexactNumber()) {
              hasInexactNumber = true;
            }
            if (!arg.isNumericFunction()) {
              isNoNumericFunction = false;
              break;
            }
          }
          if (!isNoNumericFunction) {
            break;
          }
        }
        if (hasInexactNumber && isNoNumericFunction) {
          if (!EvalEngine.get().isArbitraryMode()) {
            // if all elements are numeric stop conversion
            return null;
          }
        }
      }
      final IExpr[][] elements = new IExpr[rowSize][colSize];
      for (int i = 1; i < rowSize + 1; i++) {
        currInRow = (IAST) list.get(i);
        if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
          return null;
        }
        for (int j = 1; j < colSize + 1; j++) {
          elements[i - 1][j - 1] = currInRow.get(j);
        }
      }
      return elements;
    }
    return null;
  }

  /**
   * Returns a <code>FieldMatrix<IExpr></code> if possible.
   *
   * @param expr
   * @return <code>null</code> if the conversion isn't possible.
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   */
  public static FieldMatrix<IExpr> list2Matrix(final IExpr expr)
      throws ClassCastException, IndexOutOfBoundsException {
    return list2Matrix(expr, false);
  }

  /**
   * Returns a <code>FieldMatrix<IExpr></code> if possible.
   *
   * @param expr
   * @param ifNumericReturnNull if all elements are numeric stop conversion by returning null
   * @return <code>null</code> if the conversion isn't possible.
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   */
  public static FieldMatrix<IExpr> list2Matrix(final IExpr expr, boolean ifNumericReturnNull)
      throws ClassCastException, IndexOutOfBoundsException {
    if (expr == null) {
      return null;
    }
    int[] dim = expr.isMatrix(false);
    if (dim == null || dim[0] == 0 || dim[1] == 0) {
      return null;
    }
    if (expr.isSparseArray()) {
      ISparseArray array = (ISparseArray) expr;
      return array.toFieldMatrix(false);
    }
    if (expr.isList()) {
      IExpr[][] elements = list2Array(expr, ifNumericReturnNull);
      if (elements != null) {
        return new Array2DRowFieldMatrix<IExpr>(elements, false);
      }
    }
    return null;
  }

  public static List<FieldVector<IExpr>> list2ListOfVectors(final IExpr expr)
      throws ClassCastException, IndexOutOfBoundsException {
    return list2ListOfVectors(expr, false);
  }

  public static List<FieldVector<IExpr>> list2ListOfVectors(final IExpr expr,
      boolean ifNumericReturnNull) throws ClassCastException, IndexOutOfBoundsException {
    if (expr == null) {
      return null;
    }
    int[] dim = expr.isMatrix(false);
    if (dim == null || dim[0] == 0 || dim[1] == 0) {
      return null;
    }
    // if (expr.isSparseArray()) {
    // ISparseArray array = (ISparseArray) expr;
    // return array.toFieldMatrix(false);
    // }
    if (expr.isList()) {
      IExpr[][] elements = list2Array(expr, ifNumericReturnNull);
      if (elements != null) {
        int length = elements.length;
        List<FieldVector<IExpr>> listOfVectors = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
          listOfVectors.add(new ArrayFieldVector<>(elements[i], false));
        }
        return listOfVectors;
      }
    }
    return null;
  }


  /**
   * Converts a FieldMatrix to the list expression representation.
   *
   * @param listOfVectors
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable listOfVectors2ListOfLists(
      final List<FieldVector<IExpr>> listOfVectors) {
    if (listOfVectors == null) {
      return F.NIL;
    }
    final int rowSize = listOfVectors.size();
    if (rowSize <= 0) {
      return F.NIL;
    }
    final IASTAppendable result = F.ListAlloc(rowSize);
    for (int i = 0; i < rowSize; i++) {
      FieldVector<IExpr> fieldVector = listOfVectors.get(i);
      int colSize = fieldVector.getDimension();
      IASTAppendable currOutRow = F.ListAlloc(colSize);
      result.append(currOutRow);
      for (int j = 0; j < colSize; j++) {
        currOutRow.append(fieldVector.getEntry(j));
      }
    }
    return result;
  }

  /**
   * Returns a <code>FieldMatrix<Complex></code> if possible.
   *
   * @param expr
   * @return <code>null</code> if the conversion isn't possible.
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   */
  public static FieldMatrix<Complex> list2ComplexMatrix(IExpr expr)
      throws ClassCastException, IndexOutOfBoundsException {
    if (expr == null) {
      return null;
    }
    int[] dim = expr.isMatrix();
    if (dim == null || dim[0] == 0 || dim[1] == 0) {
      return null;
    }
    if (expr.isSparseArray()) {
      // TODO optimize for sparse arrays
      // ISparseArray array = (ISparseArray) expr;
      expr = ((ISparseArray) expr).normal(false);
    }
    if (expr.isList()) {
      try {
        IAST list = (IAST) expr;
        IAST currInRow = (IAST) list.arg1();
        if (currInRow.isAST0()) {
          // special case 0-Matrix
          Complex[][] array = new Complex[0][0];
          return new Array2DRowFieldMatrix<Complex>(array, false);
        }
        final int rowSize = expr.argSize();
        final int colSize = currInRow.argSize();

        final Complex[][] elements = new Complex[rowSize][colSize];
        for (int i = 1; i < rowSize + 1; i++) {
          currInRow = (IAST) list.get(i);
          if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
            return null;
          }
          for (int j = 1; j < colSize + 1; j++) {
            elements[i - 1][j - 1] = currInRow.get(j).evalComplex();
          }
        }
        return new Array2DRowFieldMatrix<Complex>(elements, false);
      } catch (ValidateException vex) {
        // pass
      }
    }
    return null;
  }

  /**
   * Return the augmented FieldMatrix <code>listMatrix | listVector</code>
   *
   * @param listMatrix
   * @param listVector
   * @return
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   */
  public static FieldMatrix<IExpr> list2Matrix(final IAST listMatrix, final IAST listVector)
      throws ClassCastException, IndexOutOfBoundsException {
    if (listMatrix == null || listVector == null) {
      return null;
    }
    if (!listMatrix.isList() || !listVector.isList()) {
      return null;
    }
    if (listMatrix.size() != listVector.size()) {
      return null;
    }

    IAST currInRow = (IAST) listMatrix.arg1();
    if (currInRow.isAST0()) {
      // special case 0-Matrix
      IExpr[][] array = new IExpr[0][0];
      return new Array2DRowFieldMatrix<IExpr>(array, false);
    }
    final int rowSize = listMatrix.argSize();
    final int colSize = currInRow.argSize();

    final IExpr[][] elements = new IExpr[rowSize][colSize + 1];
    for (int i = 1; i < rowSize + 1; i++) {
      currInRow = (IAST) listMatrix.get(i);
      if (currInRow.head() != S.List || colSize != currInRow.argSize()) {
        return null;
      }
      for (int j = 1; j < colSize + 1; j++) {
        elements[i - 1][j - 1] = currInRow.get(j);
      }
      elements[i - 1][colSize] = listVector.get(i);
    }
    return new Array2DRowFieldMatrix<IExpr>(elements, false);
  }

  public static GenMatrix<IExpr> list2GenMatrix(final IExpr expr, boolean ifNumericReturnNull)
      throws ClassCastException, IndexOutOfBoundsException {
    if (expr == null) {
      return null;
    }
    int[] dim = expr.isMatrix();
    if (dim == null || dim[0] == 0 || dim[1] == 0) {
      return null;
    }
    // if (expr.isSparseArray()) {
    // ISparseArray array = (ISparseArray) expr;
    // return array.toFieldMatrix(false);
    // }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IAST currInRow = (IAST) list.arg1();
      if (currInRow.isAST0()) {
        // special case 0-Matrix
        IExpr[][] array = new IExpr[0][0];
        GenMatrixRing<IExpr> ring = new GenMatrixRing<IExpr>(ExprRingFactory.CONST, 0, 0);
        return new GenMatrix<IExpr>(ring, array);
      }
      final int rowSize = expr.argSize();
      final int colSize = currInRow.argSize();
      if (ifNumericReturnNull) {
        boolean hasInexactNumber = false;
        boolean isNoNumericFunction = true;
        for (int i = 1; i < rowSize + 1; i++) {
          currInRow = (IAST) list.get(i);
          if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
            return null;
          }
          for (int j = 1; j < colSize + 1; j++) {
            final IExpr arg = currInRow.get(j);
            if (arg.isInexactNumber()) {
              hasInexactNumber = true;
            }
            if (!arg.isNumericFunction()) {
              isNoNumericFunction = false;
              break;
            }
          }
          if (!isNoNumericFunction) {
            break;
          }
        }
        if (hasInexactNumber && isNoNumericFunction) {
          if (!EvalEngine.get().isArbitraryMode()) {
            // if all elements are numeric stop conversion
            return null;
          }
        }
      }
      final IExpr[][] elements = new IExpr[rowSize][colSize];
      for (int i = 1; i < rowSize + 1; i++) {
        currInRow = (IAST) list.get(i);
        if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
          return null;
        }
        for (int j = 1; j < colSize + 1; j++) {
          elements[i - 1][j - 1] = currInRow.get(j);
        }
      }
      GenMatrixRing<IExpr> ring = new GenMatrixRing<IExpr>(ExprRingFactory.CONST, rowSize, colSize);
      return new GenMatrix<IExpr>(ring, elements);
    }
    return null;
  }

  public static FieldMatrix<IExpr> augmentedFieldMatrix(final FieldMatrix<IExpr> listMatrix,
      final FieldVector<IExpr> listVector) throws ClassCastException, IndexOutOfBoundsException {
    if (listMatrix == null || listVector == null) {
      return null;
    }
    int matrixRows = listMatrix.getRowDimension();
    int matrixColumns = listMatrix.getColumnDimension();
    int vectorDimension = listVector.getDimension();
    if (matrixRows != vectorDimension) {
      return null;
    }

    if (matrixColumns == 0) {
      // special case 0-Matrix
      IExpr[][] array = new IExpr[0][0];
      return new Array2DRowFieldMatrix<IExpr>(array, false);
    }

    final IExpr[][] elements = new IExpr[matrixRows][matrixColumns + 1];
    for (int i = 0; i < matrixRows; i++) {
      for (int j = 0; j < matrixColumns; j++) {
        elements[i][j] = listMatrix.getEntry(i, j);
      }
      elements[i][matrixColumns] = listVector.getEntry(i);
    }
    return new Array2DRowFieldMatrix<IExpr>(elements, false);
  }

  /**
   * Append a String composed of the elements of the {@code vector} joined together with the
   * specified {@code delimiter}.
   *
   * @param vector the vector which should be appended
   * @param builder join the elements as strings
   * @param delimiter the delimiter that separates each element
   */
  public static void joinToString(double[] vector, StringBuilder builder, CharSequence delimiter) {
    final int size = vector.length;
    for (int i = 0; i < size; i++) {
      builder.append(Double.toString(vector[i]));
      if (i < size - 1) {
        builder.append(delimiter);
      }
    }
  }

  /**
   * Returns a RealMatrix if possible.
   *
   * @param listMatrix
   * @return a RealMatrix or <code>null</code> if the given list is no matrix.
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   * @deprecated use {@link IExpr#toRealMatrix()}
   */
  @Deprecated
  public static RealMatrix list2RealMatrix(final IAST listMatrix)
      throws ClassCastException, IndexOutOfBoundsException {
    if (listMatrix == null) {
      return null;
    }
    if (listMatrix instanceof ASTRealMatrix) {
      return ((ASTRealMatrix) listMatrix).getRealMatrix();
    }
    final Object header = listMatrix.head();
    if (header != S.List) {
      return null;
    }

    IAST currInRow = (IAST) listMatrix.arg1();
    if (currInRow.isAST0()) {
      // special case 0-Matrix
      double[][] array = new double[0][0];
      return new Array2DRowRealMatrix(array, false);
    }

    final double[][] elements = listMatrix.toDoubleMatrix();
    if (elements == null) {
      return null;
    }
    return new Array2DRowRealMatrix(elements, false);
  }

  /**
   * Returns a RealVector if possible.
   *
   * @param listVector
   * @return a RealVector or <code>null</code> if the given list is no matrix.
   * @throws ClassCastException
   * @throws IndexOutOfBoundsException
   * @deprecated use {@link IExpr#toRealVector()}
   */
  @Deprecated
  public static RealVector list2RealVector(final IAST listVector)
      throws ClassCastException, IndexOutOfBoundsException {
    if (listVector instanceof ASTRealVector) {
      return ((ASTRealVector) listVector).getRealVector();
    }
    final Object header = listVector.head();
    if (header != S.List) {
      return null;
    }

    final double[] elements = listVector.toDoubleVector();
    if (elements == null) {
      return null;
    }
    return new ArrayRealVector(elements, false);
  }

  /**
   * Returns a FieldVector if possible.
   *
   * @param expr
   * @return <code>null</code> if the conversion isn't possible.
   * @throws ClassCastException
   */
  public static FieldVector<IExpr> list2Vector(final IExpr expr) throws ClassCastException {
    if (expr == null) {
      return null;
    }
    int dim = expr.isVector();
    if (dim <= 0) {
      return null;
    }
    if (expr.isSparseArray()) {
      ISparseArray array = (ISparseArray) expr;
      return array.toFieldVector(false);
    }
    if (expr.isList()) {
      final int rowSize = expr.argSize();
      IAST list = (IAST) expr;
      final IExpr[] elements = new IExpr[rowSize];
      for (int i = 0; i < rowSize; i++) {
        elements[i] = list.get(i + 1);
      }
      return new ArrayFieldVector<IExpr>(elements, false);
    }
    return null;
  }

  public static FieldVector<Complex> list2ComplexVector(IExpr expr) throws ClassCastException {
    if (expr == null) {
      return null;
    }
    int dim = expr.isVector();
    if (dim <= 0) {
      return null;
    }
    if (expr.isSparseArray()) {
      // ISparseArray array = (ISparseArray) expr;
      // return array.toFieldVector(false);
      expr = ((ISparseArray) expr).normal(false);
    }
    if (expr.isList()) {
      try {
        final int rowSize = expr.argSize();
        IAST list = (IAST) expr;
        final Complex[] elements = new Complex[rowSize];
        for (int i = 0; i < rowSize; i++) {
          elements[i] = list.get(i + 1).evalComplex();
        }
        return new ArrayFieldVector<Complex>(elements, false);
      } catch (ValidateException vex) {
        // pass
      }
    }
    return null;
  }

  public static Complex[] list2Complex(final IAST vector) throws ClassCastException {
    if (vector == null) {
      return null;
    }
    final Object header = vector.head();
    if (header != S.List) {
      return null;
    }

    final int size = vector.argSize();

    final Complex[] elements = new Complex[size];
    EvalEngine engine = EvalEngine.get();
    for (int i = 0; i < size; i++) {
      IExpr element = vector.get(i + 1);
      elements[i] = engine.evalComplex(element);
    }
    return elements;
  }

  /**
   * @param vector
   * @return <code>F.NIL</code> if conversion is not possible
   */
  public static IAST toVector(Complex[] vector) {
    if (vector == null) {
      return F.NIL;
    }
    return F.mapRange(0, vector.length, i -> F.complexNum(vector[i]));
  }

  /**
   * Converts a FieldMatrix to the list expression representation.
   *
   * @param matrix
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable matrix2List(final FieldMatrix<IExpr> matrix) {
    return matrix2List(matrix, true);
  }

  /**
   * Converts a FieldMatrix to the sparse array or list expression representation.
   *
   * @param matrix
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IExpr matrix2Expr(final FieldMatrix<IExpr> matrix) {
    if (matrix instanceof SparseArrayExpr.SparseExprMatrix) {
      return ((SparseArrayExpr.SparseExprMatrix) matrix).getSparseArray();
    }
    return Convert.matrix2List(matrix);
  }

  /**
   * Converts a FieldMatrix to the list expression representation.
   *
   * @param matrix
   * @param matrixFormat if <code>true</code> use matrix formatting in printing
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable matrix2List(final FieldMatrix<IExpr> matrix, boolean matrixFormat) {
    if (matrix == null) {
      return F.NIL;
    }
    final int rowSize = matrix.getRowDimension();
    final int colSize = matrix.getColumnDimension();

    final IASTAppendable result =
        F.mapRange(0, rowSize, i -> F.mapRange(0, colSize, j -> matrix.getEntry(i, j)));
    if (matrixFormat) {
      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be used!
      result.isMatrix(true);
    }
    return result;
  }

  public static IASTAppendable genmatrix2List(final GenMatrix<IExpr> matrix, boolean matrixFormat) {
    if (matrix == null) {
      return F.NIL;
    }
    final int rowSize = matrix.ring.rows;
    final int colSize = matrix.ring.cols;

    final IASTAppendable out =
        F.mapRange(0, rowSize, i -> F.mapRange(0, colSize, j -> matrix.get(i, j)));
    if (matrixFormat) {
      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be used!
      out.isMatrix(true);
    }
    return out;
  }

  /**
   * Converts a complex FieldMatrix to the list expression representation.
   *
   * @param matrix
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable complexMatrix2List(final FieldMatrix<Complex> matrix) {
    return complexMatrix2List(matrix, true);
  }

  public static IASTAppendable complexMatrix2List(final FieldMatrix<Complex> matrix,
      boolean matrixFormat) {
    if (matrix == null) {
      return F.NIL;
    }
    final int rowSize = matrix.getRowDimension();
    final int colSize = matrix.getColumnDimension();

    final IASTAppendable out = F.mapRange(0, rowSize,
        i -> F.mapRange(0, colSize, j -> F.complexNum(matrix.getEntry(i, j))));
    if (matrixFormat) {
      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be used!
      out.isMatrix(true);
    }
    return out;
  }

  /**
   * Converts a FieldMatrix to the list expression representation.
   *
   * @param matrix
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable matrix2List(final RealMatrix matrix) {
    return matrix2List(matrix, true);
  }

  /**
   * Converts a FieldMatrix to the list expression representation.
   *
   * @param matrix
   * @param matrixFormat if <code>true</code> use matrix formatting in printing
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable matrix2List(final RealMatrix matrix, boolean matrixFormat) {
    if (matrix == null) {
      return F.NIL;
    }
    final int rowSize = matrix.getRowDimension();
    final int colSize = matrix.getColumnDimension();

    final IASTAppendable result =
        F.mapRange(0, rowSize, i -> F.mapRange(0, colSize, j -> F.num(matrix.getEntry(i, j))));
    if (matrixFormat) {
      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be used!
      result.isMatrix(true);
    }
    return result;
  }

  /**
   * Converts an array of coefficients into the (polynomial) expression representation.
   *
   * @param coefficients the coefficients of the polynomial function
   * @param variable the name of the polynomial functions variable
   * @return
   */
  public static IExpr polynomialFunction2Expr(double[] coefficients, IExpr variable) {
    if (coefficients[0] == 0.0) {
      if (coefficients.length == 1) {
        return F.C0;
      }
    }
    return F.mapRange(S.Plus, 0, coefficients.length, i -> {
      if (i == 0) {
        return F.num(coefficients[0]);
      } else if (coefficients[i] != 0) {
        return F.Times(F.num(coefficients[i]), F.Power(variable, F.ZZ(i)));
      }
      return F.NIL;
    });
  }

  /**
   * Converts a PolynomialFunction to the (polynomial) expression representation.
   *
   * @param pf the polynomial function
   * @param sym the name of the polynomial functions variable
   * @return
   */
  public static IExpr polynomialFunction2Expr(final PolynomialFunction pf, ISymbol sym) {
    double[] coefficients = pf.getCoefficients();

    return polynomialFunction2Expr(coefficients, sym);
  }

  /**
   * Converts a RealMatrix to the list expression representation.
   *
   * @param matrix
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTMutable realMatrix2List(final RealMatrix matrix) {
    if (matrix == null) {
      return F.NIL;
    }
    return new ASTRealMatrix(matrix, false);
  }

  /**
   * Converts a RealVector to the list expression representation.
   *
   * @param vector
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTMutable realVectors2List(final RealVector vector) {
    if (vector == null) {
      return F.NIL;
    }
    return new ASTRealVector(vector, false);
  }

  /**
   * Convert a RealVector to a IAST list.
   *
   * @param vector
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable complexVector2List(final FieldVector<Complex> vector) {
    return complexVector2List(vector, true);
  }

  /**
   * Convert a RealVector to a IAST list.
   *
   * @param vector
   * @param vectorFormat set flag for isVector() method
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable complexVector2List(final FieldVector<Complex> vector,
      boolean vectorFormat) {
    if (vector == null) {
      return F.NIL;
    }
    final int rowSize = vector.getDimension();
    final IASTAppendable out = F.mapRange(0, rowSize, i -> F.complexNum(vector.getEntry(i)));
    out.addEvalFlags(IAST.IS_VECTOR);
    return out;
  }

  /**
   * Convert a RealVector to a IAST list.
   *
   * @param vector
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable vector2List(final RealVector vector) {
    return vector2List(vector, true);
  }

  /**
   * Convert a RealVector to a IAST list.
   *
   * @param vector
   * @param vectorFormat set flag for isVector() method
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IASTAppendable vector2List(final RealVector vector, boolean vectorFormat) {
    if (vector == null) {
      return F.NIL;
    }
    final int rowSize = vector.getDimension();
    final IASTAppendable out = F.mapRange(0, rowSize, i -> F.num(vector.getEntry(i)));
    out.addEvalFlags(IAST.IS_VECTOR);
    return out;
  }

  /**
   * Convert a matrix of double values to a transposed matrix of double values.
   *
   * @param dd
   * @return the transposed matrix of double values
   */
  public static double[][] toDoubleTransposed(final double[][] dd) {
    int columnLength = dd[0].length;
    int rowLength = dd.length;
    // allocate transposed dimensions
    double[][] result = new double[columnLength][rowLength];
    for (int i = 0; i < columnLength; i++) {
      for (int j = 0; j < rowLength; j++) {
        result[i][j] = dd[j][i];
      }
    }
    return result;
  }

  /**
   * Convert a matrix of double values to a transposed Symja matrix of <code>List[List[...], ...]
   * </code>.
   *
   * @param dd
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IAST toExprTransposed(final double[][] dd) {
    try {
      int columnLength = dd[0].length;
      int rowLength = dd.length;
      final IASTAppendable list =
          F.mapRange(0, columnLength, i -> F.mapRange(0, rowLength, j -> F.num(dd[j][i])));

      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be
      // used!
      list.isMatrix(true);
      return list;
    } catch (final Exception ex) {
    }
    return F.NIL;
  }

  /**
   * Convert a FieldVector to a IAST list.
   *
   * @param vector
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IAST vector2List(final FieldVector<IExpr> vector) {
    if (vector == null) {
      return F.NIL;
    }
    final int rowSize = vector.getDimension();
    final IASTAppendable out = F.mapRange(0, rowSize, i -> vector.getEntry(i));
    out.addEvalFlags(IAST.IS_VECTOR);
    return out;
  }

  /**
   * Converts a FieldVector to the sparse array or list expression representation.
   *
   * @param vector
   * @return <code>F.NIL</code> if no conversion was possible
   */
  public static IExpr vector2Expr(final FieldVector<IExpr> vector) {
    if (vector == null) {
      return F.NIL;
    }
    if (vector instanceof SparseArrayExpr.SparseExprVector) {
      return ((SparseArrayExpr.SparseExprVector) vector).getSparseArray();
    }
    return Convert.vector2List(vector);
  }

  private Convert() {}

  /**
   * Convert/copy the <code>expr</code> into a list of IStringX.
   *
   * @param expr the <code>expr</code> which has to be a IStringX or list of IStringX
   * @return a list of String or <code>null</code> otherwise
   */
  public static List<String> toStringList(IExpr expr) {
    if (expr.isList()) {
      List<String> result = new ArrayList<String>(expr.size() - 1);
      IAST listOfStrings = (IAST) expr;
      for (int i = 1; i < listOfStrings.size(); i++) {
        if (listOfStrings.get(i) instanceof IStringX) {
          result.add(listOfStrings.get(i).toString());
          continue;
        }
        return null;
      }
      return result;
    } else {
      List<String> result = new ArrayList<String>(1);
      if (expr instanceof IStringX) {
        result.add(expr.toString());
        return result;
      }
    }
    return null;
  }

  public static List<Integer> toIntegerList(IExpr expr) {
    if (expr.isList()) {
      List<Integer> result = new ArrayList<Integer>(expr.size() - 1);
      IAST listOfIntegers = (IAST) expr;
      for (int i = 1; i < listOfIntegers.size(); i++) {
        if (listOfIntegers.get(i) instanceof IInteger) {
          result.add(((IInteger) listOfIntegers.get(i)).toInt());
          continue;
        }
        return null;
      }
      return result;
    } else {
      List<Integer> result = new ArrayList<Integer>(1);
      if (expr instanceof IInteger) {
        result.add(((IInteger) expr).toInt());
        return result;
      }
    }
    return null;
  }

  /**
   * Convert/copy the <code>expr</code> into a list of type T.
   *
   * @param expr the <code>expr</code> which has to be a T or list of T
   * @return a list of T or <code>null</code> otherwise
   */
  public static <T> List<T> toList(IExpr expr, Function<IExpr, T> function) {
    if (expr.isList()) {
      List<T> result = new ArrayList<T>(expr.size() - 1);
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        result.add(function.apply(list.get(i)));
      }
      return result;
    }
    List<T> result = new ArrayList<T>(1);
    T element = function.apply(expr);
    result.add(element);
    return result;
  }

  public static int[] checkNonEmptySquareMatrix(ISymbol symbol, IExpr arg1) {
    int[] dim = arg1.isMatrix();
    if (dim == null || dim[0] != dim[1] || dim[1] == 0) {
      if (arg1.isListOrAssociation() || arg1.isSparseArray()) {
        if (arg1.isAST()) {
          ((IAST) arg1).setEvalFlags(IAST.NO_FLAG);
        }
        // Argument `1` at position `2` is not a non-empty square matrix.
        IOFunctions.printMessage(symbol, "matsq", F.list(arg1, F.C1), EvalEngine.get());
        return null;
      }
    }
    return dim;
  }

  public static int[] checkNonEmptyRectangularMatrix(ISymbol symbol, IExpr arg1) {
    int[] dim = arg1.isMatrix();
    if (dim == null || dim[1] == 0) {
      if (arg1.isListOrAssociation() || arg1.isSparseArray()) {
        if (arg1.isAST()) {
          ((IAST) arg1).setEvalFlags(IAST.NO_FLAG);
        }
        // Argument `1` at position `2` is not a non-empty rectangular matrix.
        IOFunctions.printMessage(symbol, "matrix", F.list(arg1, F.C1), EvalEngine.get());
      }
      return null;
    }
    return dim;
  }

  /**
   * Convert the <code>RGBColor(r,g,b)</code> to a <code>org.matheclipse.core.convert.RGBColor
   * </code>
   *
   * @param rgbColorAST
   * @return <code>null</code> if the conversion is not possible
   */
  public static RGBColor toAWTColor(IExpr rgbColorAST) {
    return toAWTColorDefault(rgbColorAST, null);
  }

  public static RGBColor toAWTColorDefault(IExpr rgbColorAST, RGBColor defaultColor) {
    if (rgbColorAST.isAST(S.RGBColor, 4, 5)) {
      IAST rgbColor = (IAST) rgbColorAST;
      float r = (float) rgbColor.arg1().evalDouble();
      float g = (float) rgbColor.arg2().evalDouble();
      float b = (float) rgbColor.arg3().evalDouble();
      return new RGBColor(r, g, b);
    }
    return defaultColor;
  }

  public static RGBColor toAWTColorDefault(IAST rgbColor) {
    return toAWTColorDefault(rgbColor, RGBColor.BLACK);
  }

  public static String toHex(RGBColor c) {
    return "#" + Integer.toHexString(c.getRGB()).substring(2);
  }

  public static IAST fromCSV(String fileName) throws IOException {
    InputStreamReader reader = new FileReader(fileName);
    return Convert.fromCSV(reader);
  }

  public static IAST fromCSV(Reader reader) throws IOException {
    EvalEngine engine = EvalEngine.get();
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

    CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(',');
    Iterable<CSVRecord> records = csvFormat.parse(reader);
    IASTAppendable rowList = F.ListAlloc(256);
    for (CSVRecord record : records) {
      IASTAppendable columnList = F.ListAlloc(record.size());
      for (String string : record) {
        final ASTNode node = parser.parse(string);
        IExpr temp = ast2Expr.convert(node);
        columnList.append(temp);
      }
      rowList.append(columnList);
    }
    return rowList;
  }
}
