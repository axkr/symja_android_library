package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Generate tuples from elements of a list.
 */
public class Tuples extends AbstractFunctionEvaluator {

	public Tuples() {
		// empty default constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		if (ast.isAST1() && arg1.isList()) {
			try {
				IAST list = (IAST) arg1;
				for (int i = 1; i < list.size(); i++) {
					if (!list.get(i).isAST()){
						return F.NIL;
					}
				}
				IAST result = F.List();
				IAST temp = F.List();
				tuplesOfLists(list, 1, result, temp);
				return result;
			} catch (ArithmeticException ae) {
				return F.NIL;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return F.NIL;
		} else if (ast.isAST2() && arg1.isAST() && ast.arg2().isInteger()) {
			IExpr arg2 = ast.arg2();
			try {
				int k = ((IInteger) arg2).toInt();
				IAST result = F.List();
				IAST temp = F.ast(arg1.head());
				tuples((IAST) arg1, k, result, temp);
				return result;
			} catch (ArithmeticException ae) {
				// because of toInt() method
			}
		}
		return F.NIL;
	}

	/**
	 * Generate all n-tuples form a list.
	 * 
	 * @param originalList
	 * @param n
	 * @param result
	 * @param subResult
	 */
	private void tuples(final IAST originalList, final int n, IAST result, IAST subResult) {
		if (n == 0) {
			result.append(subResult);
			return;
		}
		IAST temp;
		for (int j = 1; j < originalList.size(); j++) {
			temp = subResult.clone();
			temp.append(originalList.get(j));
			tuples(originalList, n - 1, result, temp);
		}

	}

	/**
	 * Generate all tuples from a list of lists.
	 * 
	 * @param originalList
	 *            the list of lists
	 * @param k
	 * @param result
	 *            the result list
	 * @param subResult
	 *            the current subList which should be inserted in the result list
	 */
	private void tuplesOfLists(final IAST originalList, final int k, IAST result, IAST subResult) {
		if (k == originalList.size()) {
			result.append(subResult);
			return;
		}
		IAST temp;
		IAST subAST = (IAST) originalList.get(k);
		for (int j = 1; j < subAST.size(); j++) {
			temp = subResult.clone();
			temp.append(subAST.get(j));
			tuplesOfLists(originalList, k + 1, result, temp);
		}

	}

}
