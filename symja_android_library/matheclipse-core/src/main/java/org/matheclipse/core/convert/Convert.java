package org.matheclipse.core.convert;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Conversions between an IExpr object and misc other object class types
 */
public class Convert {

	/**
	 * Convert rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code> into
	 * <code>java.util.Map</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>
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

	/**
	 * Returns a FieldMatrix if possible.
	 * 
	 * @param expr
	 * @return <code>null</code> if the conversion isn't possible.
	 * @throws ClassCastException
	 * @throws IndexOutOfBoundsException
	 */
	public static FieldMatrix<IExpr> list2Matrix(final IExpr expr)
			throws ClassCastException, IndexOutOfBoundsException {
		if (expr == null) {
			return null;
		}
		int[] dim = expr.isMatrix();
		if (dim == null || dim[0] == 0 || dim[1] == 0) {
			return null;
		}
		IAST list = (IAST) expr;
		IAST currInRow = (IAST) list.arg1();
		if (currInRow.isAST0()) {
			// special case 0-Matrix
			IExpr[][] array = new IExpr[0][0];
			return new Array2DRowFieldMatrix<IExpr>(array, false);
		}
		final int rowSize = expr.argSize();
		final int colSize = currInRow.argSize();

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
		if (header != F.List) {
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

		final int rowSize = expr.argSize();
		IAST list = (IAST) expr;
		final IExpr[] elements = new IExpr[rowSize];
		for (int i = 0; i < rowSize; i++) {
			elements[i] = list.get(i + 1);
		}
		return new ArrayFieldVector<IExpr>(elements, false);
	}

	public static Complex[] list2Complex(final IAST vector) throws ClassCastException {
		if (vector == null) {
			return null;
		}
		final Object header = vector.head();
		if (header != F.List) {
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
	 * 
	 * @param vector
	 * @return <code>F.NIL</code> if conversion is not possible
	 */
	public static IAST toVector(Complex[] vector) {
		if (vector == null) {
			return F.NIL;
		}
		IASTAppendable result = F.ListAlloc(vector.length);

		for (int i = 0; i < vector.length; i++) {
			result.append(F.complexNum(vector[i]));
		}
		return result;
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
	 * Converts a FieldMatrix to the list expression representation.
	 * 
	 * @param matrix
	 * @param matrixFormat
	 *            if <code>true</code> use matrix formatting in printing
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IASTAppendable matrix2List(final FieldMatrix<IExpr> matrix, boolean matrixFormat) {
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
		if (matrixFormat) {
			// because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly. isMatrix() must be
			// used!
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
	 * @param matrixFormat
	 *            if <code>true</code> use matrix formatting in printing
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IASTAppendable matrix2List(final RealMatrix matrix, boolean matrixFormat) {
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
		if (matrixFormat) {
			// because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly. isMatrix() must be
			// used!
			out.isMatrix(true);
		}
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
				sum.append(F.Times(F.num(coefficients[i]), F.Power(sym, F.ZZ(i))));
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
		return vector2List(vector, true);
	}

	/**
	 * Convert a RealVector to a IAST list.
	 * 
	 * @param vector
	 * @param vectorFormat
	 *            set flag for isVector() method
	 * @return <code>F.NIL</code> if no conversion was possible
	 */
	public static IASTAppendable vector2List(final RealVector vector, boolean vectorFormat) {
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
			// because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly. isMatrix() must be
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

		final IASTAppendable out = F.ListAlloc(rowSize);
		for (int i = 0; i < rowSize; i++) {
			out.append(vector.getEntry(i));
		}
		out.addEvalFlags(IAST.IS_VECTOR);
		return out;
	}

	private Convert() {
	}

	/**
	 * Convert the <code>expr</code> into a list of IStringX.
	 * 
	 * @param expr
	 *            the <code>expr</code> which has to be a IStringX or list of IStringX
	 * 
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

	public static int[] checkNonEmptySquareMatrix(ISymbol symbol, IExpr arg1) {
		int[] dim = arg1.isMatrix();
		if (dim == null || dim[0] != dim[1] || dim[1] == 0) {
			if (arg1.isNumericFunction()) {
				if (arg1.isAST()) {
					((IAST) arg1).setEvalFlags(IAST.NO_FLAG);
				}
				// Argument `1` at position `2` is not a non-empty square matrix.
				IOFunctions.printMessage(symbol, "matsq", F.List(arg1, F.C1), EvalEngine.get());
			}
			return null;
		}
		return dim;
	}

	public static int[] checkNonEmptyRectangularMatrix(ISymbol symbol, IExpr arg1) {
		int[] dim = arg1.isMatrix();
		if (dim == null || dim[1] == 0) {
			if (arg1.isNumericFunction()) {
				if (arg1.isAST()) {
					((IAST) arg1).setEvalFlags(IAST.NO_FLAG);
				}
				// Argument `1` at position `2` is not a non-empty rectangular matrix.
				IOFunctions.printMessage(symbol, "matrix", F.List(arg1, F.C1), EvalEngine.get());
			}
			return null;
		}
		return dim;
	}

	/**
	 * Convert the <code>RGBColor(r,g,b)</code> to a <code>java.awt.Color</code>
	 * 
	 * @param rgbAST
	 * @return <code>null</code> if the conversion is not possible
	 */
	public static java.awt.Color toAWTColor(IExpr rgbAST) { 
		return  toAWTColorDefault(rgbAST, null);
	}

	public static java.awt.Color toAWTColorDefault(IExpr rgbAST, Color defaultColor) {
		if (rgbAST.isAST(F.RGBColor, 4, 5)) {
			IAST rgbColor = (IAST) rgbAST;
			float r = (float) rgbColor.arg1().evalDouble();
			float g = (float) rgbColor.arg2().evalDouble();
			float b = (float) rgbColor.arg3().evalDouble();
			return new Color(r, g, b);
		}
		return defaultColor;
	}

	public static java.awt.Color toAWTColorDefault(IAST rgbColor) {
		return toAWTColorDefault(rgbColor, Color.BLACK);
	}
	
	public static String toHex(java.awt.Color c) {
		return "#"+Integer.toHexString(c.getRGB()).substring(2);
	}
}
