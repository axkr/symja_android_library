package org.matheclipse.core.convert;

import org.hipparchus.analysis.polynomials.PolynomialFunction;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayFieldVector;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
//import org.matheclipse.commons.math.linear.Array2DRowFieldMatrix;
//import org.matheclipse.commons.math.linear.ArrayFieldVector;
//import org.matheclipse.commons.math.linear.FieldMatrix;
//import org.matheclipse.commons.math.linear.FieldVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Conversions between an IExpr object and misc other object class types
 */
public class Convert {

	/**
	 * Returns a FieldMatrix if possible.
	 * 
	 * @param listMatrix
	 * @return <code>null</code> if the conversion isn't possible.
	 * @throws ClassCastException
	 * @throws IndexOutOfBoundsException
	 */
	public static FieldMatrix<IExpr> list2Matrix(final IAST listMatrix)
			throws ClassCastException, IndexOutOfBoundsException {
		if (listMatrix == null) {
			return null;
		}
		if (!listMatrix.isList()) {
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

		final IExpr[][] elements = new IExpr[rowSize][colSize];
		for (int i = 1; i < rowSize + 1; i++) {
			currInRow = (IAST) listMatrix.get(i);
			if (currInRow.isVector() < 0 || colSize != currInRow.argSize()) {
				return null;
			}
			for (int j = 1; j < colSize + 1; j++) {
				elements[i - 1][j - 1] = currInRow.get(j);
			}
		}
		return new Array2DRowFieldMatrix<IExpr>(elements, false);
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
			if (currInRow.head() != F.List || colSize != currInRow.argSize()) {
				return null;
			}
			for (int j = 1; j < colSize + 1; j++) {
				elements[i - 1][j - 1] = currInRow.get(j);
			}
			elements[i - 1][colSize] = listVector.get(i);
		}
		return new Array2DRowFieldMatrix<IExpr>(elements, false);
	}

	/**
	 * Returns a RealMatrix if possible.
	 * 
	 * @param listMatrix
	 * @return a RealMatrix or <code>null</code> if the given list is no matrix.
	 * @throws WrongArgumentType
	 *             if not all elements are convertable to a <code>double</code> value.
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
		if (header != F.List) {
			return null;
		}

		IAST currInRow = (IAST) listMatrix.arg1();
		if (currInRow.isAST0()) {
			// special case 0-Matrix
			double[][] array = new double[0][0];
			return new Array2DRowRealMatrix(array, false);
		}

		final double[][] elements = Expr2Object.toDoubleMatrix(listMatrix);
		return new Array2DRowRealMatrix(elements, false);
	}

	/**
	 * Returns a RealVector if possible.
	 * 
	 * @param listVector
	 * @return a RealVector or <code>null</code> if the given list is no matrix.
	 * @throws WrongArgumentType
	 *             if not all elements are convertible to a <code>double</code> value.
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
		if (header != F.List) {
			return null;
		}

		final double[] elements = Expr2Object.toDoubleVector(listVector);
		return new ArrayRealVector(elements, false);
	}

	/**
	 * Returns a FieldVector if possible.
	 * 
	 * @param listVector
	 * @return <code>null</code> if the conversion isn't possible.
	 * @throws ClassCastException
	 * @throws IndexOutOfBoundsException
	 */
	public static FieldVector<IExpr> list2Vector(final IAST listVector)
			throws ClassCastException, IndexOutOfBoundsException {
		if (listVector == null) {
			return null;
		}
		final Object header = listVector.head();
		if (header != F.List) {
			return null;
		}

		final int rowSize = listVector.argSize();

		final IExpr[] elements = new IExpr[rowSize];
		for (int i = 0; i < rowSize; i++) {
			elements[i] = listVector.get(i + 1);
		}
		return new ArrayFieldVector<IExpr>(elements, false);
	}

	/**
	 * Converts a FieldMatrix to the list expression representation.
	 * 
	 * @param matrix
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IASTAppendable matrix2List(final FieldMatrix<IExpr> matrix) {
		if (matrix == null) {
			return F.NIL;
		}
		final int rowSize = matrix.getRowDimension();
		final int colSize = matrix.getColumnDimension();

		final IASTAppendable out = F.ListAlloc(rowSize);
		IASTAppendable currOutRow;
		for (int i = 0; i < rowSize; i++) {
			currOutRow = F.ListAlloc(colSize);
			out.append(currOutRow);
			for (int j = 0; j < colSize; j++) {
				IExpr expr = matrix.getEntry(i, j);
				if (expr.isNumber()) {
					currOutRow.append(expr);
				} else {
					// if (expr.isPlusTimesPower()) {
					// // TODO Performance hotspot
					// currOutRow.append(F.eval(F.Together(expr)));
					// } else {
					currOutRow.append(expr);
					// }
				}
			}
		}
		out.addEvalFlags(IAST.IS_MATRIX);
		return out;
	}

	/**
	 * Converts a FieldMatrix to the list expression representation.
	 * 
	 * @param matrix
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IASTAppendable matrix2List(final RealMatrix matrix) {
		if (matrix == null) {
			return F.NIL;
		}
		final int rowSize = matrix.getRowDimension();
		final int colSize = matrix.getColumnDimension();

		final IASTAppendable out = F.ListAlloc(rowSize);
		IASTAppendable currOutRow;
		for (int i = 0; i < rowSize; i++) {
			currOutRow = F.ListAlloc(colSize);
			out.append(currOutRow);
			for (int j = 0; j < colSize; j++) {
				IExpr expr = F.num(matrix.getEntry(i, j));
				if (expr.isNumber()) {
					currOutRow.append(expr);
				} else {
					// if (expr.isPlusTimesPower()) {
					// // TODO Performance hotspot
					// currOutRow.append(F.eval(F.Together(expr)));
					// } else {
					currOutRow.append(expr);
					// }
				}
			}
		}
		out.addEvalFlags(IAST.IS_MATRIX);
		return out;
	}

	/**
	 * Converts an array of coefficients into the (polynomial) expression representation.
	 * 
	 * @param coefficients
	 *            the coefficients of the polynomial function
	 * @param sym
	 *            the name of the polynomial functions variable
	 * @return
	 */
	public static IExpr polynomialFunction2Expr(double[] coefficients, ISymbol sym) {
		if (coefficients[0] == 0.0) {
			if (coefficients.length == 1) {
				return F.C0;
			}
		}
		IASTAppendable sum = F.PlusAlloc(coefficients.length);
		sum.append(F.num(coefficients[0]));
		for (int i = 1; i < coefficients.length; ++i) {
			if (coefficients[i] != 0) {
				sum.append(F.Times(F.num(coefficients[i]), F.Power(sym, F.integer(i))));
			}
		}

		return sum;
	}

	/**
	 * Converts a PolynomialFunction to the (polynomial) expression representation.
	 * 
	 * @param pf
	 *            the polynomial function
	 * @param sym
	 *            the name of the polynomial functions variable
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
	public static IASTAppendable vector2List(final RealVector vector) {
		if (vector == null) {
			return F.NIL;
		}
		final int rowSize = vector.getDimension();

		final IASTAppendable out = F.ListAlloc(rowSize);
		for (int i = 0; i < rowSize; i++) {
			out.append(F.num(vector.getEntry(i)));
		}
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
	 * Convert a matrix of double values to a transposed Symja matrix of <code>List[List[...], ...]</code>.
	 * 
	 * @param dd
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IAST toExprTransposed(final double[][] dd) {
		try {
			int columnLength = dd[0].length;
			int rowLength = dd.length;
			final IASTAppendable list = F.ListAlloc(columnLength);
			for (int i = 0; i < columnLength; i++) {
				final IASTAppendable row = F.ListAlloc(rowLength);
				for (int j = 0; j < rowLength; j++) {
					row.append(F.num(dd[j][i]));
				}
				list.append(row);
			}
			list.addEvalFlags(IAST.IS_MATRIX);
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

		final IASTAppendable out = F.ListAlloc(rowSize);
		for (int i = 0; i < rowSize; i++) {
			out.append(vector.getEntry(i));
		}
		out.addEvalFlags(IAST.IS_VECTOR);
		return out;
	}

	private Convert() {
	}

}
