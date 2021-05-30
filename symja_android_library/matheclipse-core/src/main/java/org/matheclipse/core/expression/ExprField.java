package org.matheclipse.core.expression;

import org.hipparchus.Field;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Class for representing a field of <code>ExprFieldElement</code>.
 *
 * @see ExprFieldElement
 */
/* package private*/ final class ExprField implements Field<IExpr> {

  // public final static IExpr ONE = F.C1;
  // public final static IExpr ZERO  =F.C0;

  @Override
  public IExpr getOne() {
    return F.C1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getZero() {
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  public Class<IExpr> getRuntimeClass() {
    return IExpr.class;
  }

  //	/**
  //	 * Returns a FieldVector if possible.
  //	 *
  //	 * @param listVector
  //	 * @return <code>null</code> if the <code>listVector</code> is no list
  //	 * @throws ClassCastException
  //	 * @throws IndexOutOfBoundsException
  //	 */
  //	public static FieldVector<ExprFieldElement> list2Vector(final IAST listVector)
  //			throws ClassCastException, IndexOutOfBoundsException {
  //		if (listVector == null) {
  //			return null;
  //		}
  //		if (!listVector.isList()) {
  //			return null;
  //		}
  //
  //		final int rowSize = listVector.argSize();
  //
  //		final ExprFieldElement[] elements = new ExprFieldElement[rowSize];
  //		for (int i = 0; i < rowSize; i++) {
  //			elements[i] = new ExprFieldElement(listVector.get(i + 1));
  //		}
  //		return new ArrayFieldVector<ExprFieldElement>(elements);
  //	}
  //
  //	/**
  //	 * Returns a FieldMatrix if possible.
  //	 *
  //	 * @param listMatrix
  //	 * @return <code>null</code> if the <code>listMatrix</code> is no list
  //	 * @throws ClassCastException
  //	 * @throws IndexOutOfBoundsException
  //	 */
  //	public static FieldMatrix<ExprFieldElement> list2Matrix(final IAST listMatrix)
  //			throws ClassCastException, IndexOutOfBoundsException {
  //		if (listMatrix == null) {
  //			return null;
  //		}
  //		if (!listMatrix.isList()) {
  //			return null;
  //		}
  //
  //		IAST currInRow = (IAST) listMatrix.arg1();
  //		if (currInRow.isAST0()) {
  //			// special case 0-Matrix
  //			ExprFieldElement[][] array = new ExprFieldElement[0][0];
  //			return new BlockFieldMatrix<ExprFieldElement>(array);
  //		}
  //		final int rowSize = listMatrix.argSize();
  //		final int colSize = currInRow.argSize();
  //
  //		final ExprFieldElement[][] elements = new ExprFieldElement[rowSize][colSize];
  //		for (int i = 1; i < rowSize + 1; i++) {
  //			currInRow = (IAST) listMatrix.get(i);
  //			if (currInRow.head() != F.List) {
  //				return null;
  //			}
  //			for (int j = 1; j < colSize + 1; j++) {
  //				elements[i - 1][j - 1] = new ExprFieldElement(currInRow.get(j));
  //			}
  //		}
  //		return new BlockFieldMatrix<ExprFieldElement>(elements);
  //	}
  //
  //	/**
  //	 * Converts a FieldMatrix to the list expression representation.
  //	 *
  //	 * @param matrix
  //	 * @return
  //	 */
  //	public static IAST matrix2List(final FieldMatrix<ExprFieldElement> matrix) {
  //		if (matrix == null) {
  //			return null;
  //		}
  //		final int rowSize = matrix.getRowDimension();
  //		final int colSize = matrix.getColumnDimension();
  //
  //		final IAST out = F.ListC(rowSize);
  //		IAST currOutRow;
  //		for (int i = 0; i < rowSize; i++) {
  //			currOutRow = F.ListC(colSize);
  //			out.add(currOutRow);
  //			for (int j = 0; j < colSize; j++) {
  //				IExpr expr = matrix.getEntry(i, j).getExpr();
  //				if (expr instanceof INumber) {
  //					currOutRow.add(expr);
  //				} else {
  //					currOutRow.add(F.eval(F.Together(expr)));
  //				}
  //			}
  //		}
  //		out.addEvalFlags(IAST.IS_MATRIX);
  //		return out;
  //	}
  //
  //	/**
  //	 * Convert a FieldVector to an IAST list.
  //	 *
  //	 * @param vector
  //	 * @return
  //	 */
  //	public static IAST vector2List(final FieldVector<ExprFieldElement> vector) {
  //		if (vector == null) {
  //			return null;
  //		}
  //		final int rowSize = vector.getDimension();
  //
  //		final IAST out = F.ListC(rowSize);
  //		for (int i = 0; i < rowSize; i++) {
  //			out.add(vector.getEntry(i).getExpr());
  //		}
  //		out.addEvalFlags(IAST.IS_VECTOR);
  //		return out;
  //	}

}
