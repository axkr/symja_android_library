package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Reduce the matrix to row reduced echelon form.
 * </p>
 * 
 * See:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row
 * echelon form</a></li>
 * <li><a href="https://www.math.hmc.edu/calculus/tutorials/linearsystems/">
 * Solving Systems of Linear Equations; Row Reduction </a></li>
 * </ul>
 */
public class RowReduce extends AbstractFunctionEvaluator {

	public RowReduce() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		try {
			Validate.checkSize(ast, 2);

			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
			return Convert.matrix2List(fmw.getRowReducedMatrix());

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

	/**
	 * Return the solution of the given (augmented-)matrix interpreted as a
	 * system of linear equations
	 * 
	 * @param matrix
	 * @return <code>F.NIL</code> if the linear system is inconsistent and has
	 *         no solution
	 */
	public static IAST rowReduced2List(FieldMatrix<IExpr> matrix, EvalEngine engine) {

		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		if (rows == 2 && cols == 3) {
			IAST list = Det.cramersRule2x3(matrix, false, engine);
			if (list.isPresent()) {
				return list;
			}
		} else if (rows == 3 && cols == 4) {
			IAST list = Det.cramersRule3x4(matrix, false, engine);
			if (list.isPresent()) {
				return list;
			}
		}
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
		IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				engine.printMessage("Row reduced linear equations have no solution.");
				return F.NIL;
			}
		}
		IAST list = F.List();
		for (int j = 0; j < rows; j++) {
			list.append(F.eval(F.Together(rowReduced.getEntry(j, cols - 1))));
		}
		if (rows < cols - 1) {
			for (int i = rows; i < cols - 1; i++) {
				list.append(F.C0);
			}
		}
		return list;
	}

	/**
	 * Row reduce the given <code>(augmented-)matrix</code> and append the
	 * result as rules for the given <code>variableList</code>.
	 * 
	 * @param matrix
	 *            a (augmented-)matrix
	 * @param listOfVariables
	 *            list of variable symbols
	 * @param resultList
	 *            a list to which the rules should be appended
	 * 
	 * @return resultList with the appended results as list of rules
	 */
	public static IAST rowReduced2RulesList(FieldMatrix<IExpr> matrix, IAST listOfVariables, IAST resultList,
			EvalEngine engine) {
		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		IAST smallList = null;
		if (rows == 2 && cols == 3) {
			smallList = Det.cramersRule2x3(matrix, true, engine);
		} else if (rows == 3 && cols == 4) {
			smallList = Det.cramersRule3x4(matrix, true, engine);
		}
		if (smallList != null) {
			if (!smallList.isPresent()) {
				// no solution
				return F.List();
			}
			IAST list = F.List();
			IAST rule;
			for (int j = 1; j < smallList.size(); j++) {
				rule = F.Rule(listOfVariables.get(j), F.eval(smallList.get(j)));
				list.append(rule);
			}
			
			resultList.append(list);
			return resultList;
		}
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
		int size = listOfVariables.size() - 1;

		IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
		IAST list = F.List();
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				// no solution
				return F.List();
			}
		}
		IAST rule;
		for (int j = 1; j < rows + 1; j++) {
			if (j < size + 1) {
				IExpr diagonal = rowReduced.getEntry(j - 1, j - 1);
				if (!diagonal.isZero()) {
					IAST plus = F.Plus();
					plus.append(rowReduced.getEntry(j - 1, cols - 1));
					for (int i = j; i < cols - 1; i++) {
						if (!rowReduced.getEntry(j - 1, i).isZero()) {
							plus.append(F.Times(rowReduced.getEntry(j - 1, i).negate(), listOfVariables.get(i + 1)));
						}
					}
					rule = F.Rule(listOfVariables.get(j), F.eval(F.Together(plus.getOneIdentity(F.C0))));
					list.append(rule);
				}
			}
		}
		resultList.append(list);
		return resultList;
	}
}