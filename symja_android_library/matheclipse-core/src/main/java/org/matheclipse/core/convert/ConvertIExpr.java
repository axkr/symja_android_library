package org.matheclipse.core.convert;

import org.matheclipse.commons.math.linear.ArrayFieldVector;
import org.matheclipse.commons.math.linear.BlockFieldMatrix;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Conversions between an IExpr object and misc other object class types
 */
public class ConvertIExpr {

	private ConvertIExpr() {
	}

	/**
	 * Returns a FieldMatrix if possible.
	 * 
	 * @param listMatrix
	 * @return
	 * @throws ClassCastException
	 * @throws IndexOutOfBoundsException
	 */
	public static FieldMatrix list2Matrix(final IAST listMatrix) throws ClassCastException, IndexOutOfBoundsException {
		if (listMatrix == null) {
			return null;
		}
		final Object header = listMatrix.head();
		if (header != F.List) {
			return null;
		}

		IAST currInRow = (IAST) listMatrix.get(1);
		if (currInRow.size() == 1) {
			// special case 0-Matrix
			IExpr[][] array = new IExpr[0][0];
			return new BlockFieldMatrix(array);
		}
		final int rowSize = listMatrix.size() - 1;
		final int colSize = currInRow.size() - 1;

		final IExpr[][] elements = new IExpr[rowSize][colSize];
		for (int i = 1; i < rowSize + 1; i++) {
			currInRow = (IAST) listMatrix.get(i);
			if (currInRow.head() != F.List) {
				return null;
			}
			for (int j = 1; j < colSize + 1; j++) {
				elements[i - 1][j - 1] = currInRow.get(j);
			}
		}
		return new BlockFieldMatrix(elements);
	}

	/**
	 * Converts a FieldMatrix to the list expression representation.
	 * 
	 * @param matrix
	 * @return
	 */
	public static IAST matrix2List(final FieldMatrix matrix) {
		if (matrix == null) {
			return null;
		}
		final int rowSize = matrix.getRowDimension();
		final int colSize = matrix.getColumnDimension();

		final IAST out = F.List();
		IAST currOutRow;
		for (int i = 0; i < rowSize; i++) {
			currOutRow = F.List();
			out.add(currOutRow);
			for (int j = 0; j < colSize; j++) {
				IExpr expr = matrix.getEntry(i, j);
				if (expr instanceof INumber) {
					currOutRow.add(expr);
				} else {
					currOutRow.add(F.eval(F.Together(expr)));
				}
			}
		}
		out.addEvalFlags(IAST.IS_MATRIX);
		return out;
	}

	public static FieldVector list2Vector(final IAST listVector) throws ClassCastException, IndexOutOfBoundsException {
		if (listVector == null) {
			return null;
		}
		final Object header = listVector.head();
		if (header != F.List) {
			return null;
		}

		final int rowSize = listVector.size() - 1;

		final IExpr[] elements = new IExpr[rowSize];
		for (int i = 0; i < rowSize; i++) {
			elements[i] = listVector.get(i + 1);
		}
		return new ArrayFieldVector(elements);
	}

	/**
	 * Convert a FieldVector to a IAST list.
	 * 
	 * @param vector
	 * @return
	 */
	public static IAST vector2List(final FieldVector vector) {
		if (vector == null) {
			return null;
		}
		final int rowSize = vector.getDimension();

		final IAST out = F.List();
		for (int i = 0; i < rowSize; i++) {
			out.add(vector.getEntry(i));
		}
		out.addEvalFlags(IAST.IS_VECTOR);
		return out;
	}

}
