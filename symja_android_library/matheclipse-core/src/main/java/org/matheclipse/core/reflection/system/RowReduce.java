package org.matheclipse.core.reflection.system;

import org.matheclipse.commons.math.linear.FieldMatrix;
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
 * <li><a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a></li>
 * <li><a href="https://www.math.hmc.edu/calculus/tutorials/linearsystems/">Solving Systems of Linear Equations; Row Reduction </a></li>
 * </ul>
 */
public class RowReduce extends AbstractFunctionEvaluator {

	public RowReduce() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		FieldMatrix matrix;
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

		return null;
	}

	@Override
	public IExpr numericEval(final IAST function) {
		return evaluate(function);
	}

	/**
	 * Return the solution of the given (augmented-)matrix interpreted as a system of linear equations
	 * 
	 * @param matrix
	 * @return <code>null</code> if the linear system is inconsistent and has no solution
	 */
	public static IAST rowReduced2List(FieldMatrix matrix) {
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix rowReduced = ref.getRowReducedMatrix();
		int rows = rowReduced.getRowDimension();
		int cols = rowReduced.getColumnDimension();
		IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				EvalEngine.get().printMessage("Row reduced linear equations have no solution.");
				return null;
			}
		}
		IAST list = F.List();
		for (int j = 0; j < rows; j++) {
			list.add(F.eval(F.Together(rowReduced.getEntry(j, cols - 1))));
		}
		if (rows < cols - 1) {
			for (int i = rows; i < cols - 1; i++) {
				list.add(F.eval(F.C0));
			}
		}
		return list;
	}

	/**
	 * Row reduce the given <code>(augmented-)matrix</code> and append the result as rules for the given <code>variableList</code>.
	 * 
	 * @param matrix
	 *            a (augmented-)matrix
	 * @param variableList
	 *            list of variable symbols
	 * @param resultList
	 *            a list to which the rules should be appended
	 * 
	 * @return resultList with the appended results as list of rules
	 */
	public static IAST rowReduced2RulesList(FieldMatrix matrix, IAST variableList, IAST resultList) {
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix rowReduced = ref.getRowReducedMatrix();
		int size = variableList.size() - 1;
		int rows = rowReduced.getRowDimension();
		int cols = rowReduced.getColumnDimension();
		IExpr lastVarCoefficient = rowReduced.getEntry(size - 1, size - 1);
		IAST list = F.List();
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				// no solution
				return F.List();
			}
		}
		IAST rule;
		for (int j = 1; j < variableList.size(); j++) {
			IExpr diagonal = rowReduced.getEntry(j - 1, j - 1);
			if (!diagonal.isZero()) {
				IAST plus = F.Plus();
				plus.add(rowReduced.getEntry(j - 1, cols - 1));
				for (int i = j; i < cols - 1; i++) {
					if (!rowReduced.getEntry(j - 1, i).isZero()) {
						plus.add(F.Times(rowReduced.getEntry(j - 1, i).negate(), variableList.get(i + 1)));
					}
				}
				rule = F.Rule(variableList.get(j), F.eval(F.Together(plus.getOneIdentity(F.C0))));
				list.add(rule);
			}
		}
		resultList.add(list);
		return resultList;
	}
}