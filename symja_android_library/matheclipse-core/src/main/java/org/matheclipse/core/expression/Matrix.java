package org.matheclipse.core.expression;

import org.matheclipse.core.eval.exception.DimensionException;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * @deprecated
 */
@Deprecated
public class Matrix extends ASTDelegate {
	private final int fColumns;

	/**
	 * 
	 * @param ast
	 * @param columns
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Matrix(IAST ast, int columns) {
		super(ast);
		fColumns = columns;
	}

	/**
	 * 
	 * @param rows
	 * @param columns
	 * @deprecated
	 */
	@Deprecated
	public Matrix(int rows, int columns) {
		super();
		fColumns = columns;
		fAst = createAST(rows, columns);
	}

	/**
	 * 
	 * @param values
	 * @deprecated
	 */
	@Deprecated
	public Matrix(int[][] values) {
		super();
		fColumns = values[0].length;
		fAst = createAST(values);
	}

	/**
	 * Create a new AST matrix instance (i.e. List of Lists) and set all sublist elements to <code>null</code>
	 * 
	 * @param rowSize
	 *            the initial number of rows, which should be initialized
	 * @param columnSize
	 *            the initial number of columns, which should be assigned to <code>null</code>
	 * @return a new AST matrix instance
	 */
	protected IAST createAST(int rowSize, int columnSize) {
		IAST matrix = F.ast(F.List, rowSize, true);
		for (int i = 1; i < rowSize + 1; i++) {
			matrix.set(i, F.ast(F.List, columnSize, false));
		}
		return matrix;
	}

	protected IAST createAST(int[][] values) {
		IAST matrix = F.ast(F.List, values.length, true);
		for (int i = 0; i < values.length; i++) {
			matrix.set(i + 1, AST.newInstance(F.List, values[i]));
		}
		return matrix;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Matrix) {
			Matrix other = (Matrix) obj;
			if (fColumns != other.fColumns)
				return false;
			return fAst.equals(other.fAst);
		}
		return false;
	}

	public IExpr getAt(final int row, final int column) {
		return ((IAST) fAst.get(row)).get(column);
	}

	/**
	 * Set the value at <code>row, column</code> position.
	 * 
	 * @param row
	 * @param column
	 * @param value
	 * @return
	 */
	public IExpr setAt(final int row, final int column, final IExpr value) {
		return ((IAST) fAst.get(row)).set(column, value);
	}

	/**
	 * Get the number of columns in this matrix
	 * 
	 * @return
	 */
	public int getColumns() {
		return fColumns;
	}

	@Override
	public int hashCode() {
		return fAst.hashCode() * 59;
	}

	public Matrix minus(final Matrix that) {
		if ((that.getRows() != getRows()) || (that.getColumns() != fColumns)) {
			throw new DimensionException("Matrix#minus([" + getRows() + "," + fColumns + "],[" + that.getRows() + ","
					+ that.getColumns() + "])");
		}
		return null;
	}

	public Matrix multiply(final Matrix that) {
		if (that.getRows() != fColumns) {
			throw new DimensionException("Matrix#multiply([" + getRows() + "," + fColumns + "],[" + that.getRows() + ","
					+ that.getColumns() + "])");
		}
		return null;
	}

	public Matrix plus(final Matrix that) {
		if ((that.getRows() != getRows()) || (that.getColumns() != fColumns)) {
			throw new DimensionException("Matrix#plus([" + getRows() + "," + fColumns + "],[" + that.getRows() + ","
					+ that.getColumns() + "])");
		}
		IAST resultAST = createAST(fAst.size() - 1);
		IAST tempAST;
		for (int i = 1; i < fAst.size(); i++) {
			tempAST = createAST(getColumns());
			((IAST) fAst.get(i)).map(tempAST, (IAST) that.fAst.get(i), new BinaryMap(F.Plus));
			resultAST.add(tempAST);
		}
		return new Matrix(resultAST, getColumns());
	}

	public Matrix power(final Integer n) {
		return null;
	}
}
