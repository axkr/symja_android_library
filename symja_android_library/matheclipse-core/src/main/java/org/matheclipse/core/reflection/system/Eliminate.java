package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Try to eliminate a variable in a set of equations (i.e. <code>Equal[...]</code> expressions).
 */
public class Eliminate extends AbstractFunctionEvaluator {

	public Eliminate() {
	}

	/**
	 * Check if the argument at the given position is an equation (i.e. Equal[a,b]) or a list of equations and return a list of
	 * expressions, which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	private static IAST checkEquations(final IAST ast, int position) {
		IAST equalList = F.List();
		IAST eqns = null;
		IAST eq;
		if (ast.get(position).isList()) {
			eqns = (IAST) ast.get(position);
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST(F.Equal, 3)) {
					eq = (IAST) eqns.get(i);
					equalList.add(F.Equal(F.evalExpandAll(eq.arg1()), F.evalExpandAll(eq.arg2())));
				} else {
					// not an equation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equal[] expression (a==b) expected");
				}
			}
		} else {
			if (ast.get(position).isAST(F.Equal, 3)) {
				eq = (IAST) ast.get(position);
				equalList.add(F.Equal(F.evalExpandAll(eq.arg1()), F.evalExpandAll(eq.arg2())));
			} else {
				// not an equation
				throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal[] expression (a==b) expected");
			}
		}
		return equalList;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		try {
			IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
			IAST termsEqualZeroList = checkEquations(ast, 1);
			IAST result = termsEqualZeroList;
			IAST temp;
			for (int i = 1; i < vars.size(); i++) {
				ISymbol variable = (ISymbol) vars.get(i);
				temp = eliminateOneVariable(result, variable);
				if (temp != null) {
					result = temp;
				} else {
					return result;
				}
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Analyze an <code>Equal()</code> expression.
	 * 
	 * @param equalAST
	 *            an <code>Equal()</code> expression.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't find an equation for the given <code>variable</code>.
	 */
	private static IExpr eliminateAnalyze(IAST equalAST, ISymbol variable) {
		IExpr arg1 = equalAST.arg1();
		IExpr arg2 = equalAST.arg2();
		if (arg1.isMember(variable, false) && !arg2.isMember(variable, false)) {
			return extractVariable(arg1, arg2, variable);
		} else if (arg2.isMember(variable, false) && !arg1.isMember(variable, false)) {
			return extractVariable(arg2, arg1, variable);
		}
		return null;
	}

	/**
	 * Extract a value for the given <code>variabe</code>.
	 * 
	 * @param exprWithVariable
	 *            expression which contains the given <code>variabe</code>.
	 * @param exprWithoutVariable
	 *            expression which doesn't contain the given <code>variabe</code>.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't find an equation for the given <code>variable</code>.
	 */
	public static IExpr extractVariable(IExpr exprWithVariable, IExpr exprWithoutVariable, ISymbol variable) {
		if (exprWithVariable.isAST()) {
			IAST ast = (IAST) exprWithVariable;
			if (ast.isPlus()) {
				// a + b + c....
				IAST rest = F.Plus();
				IAST plusClone = ast.clone();
				int j = 1;
				for (int i = 1; i < ast.size(); i++) {
					if (ast.get(i).isMember(variable, false)) {
						rest.add(ast.get(i));
						plusClone.remove(j);
					} else {
						j++;
					}
				}
				if (plusClone.size() == 1) {
					// no change for given expression
					return null;
				}
				IExpr value = F.eval(F.Subtract(exprWithoutVariable, plusClone));
				if (rest.size() == 2) {
					return extractVariable(rest.get(1), value, variable);
				} else {
					return extractVariable(rest, value, variable);
				}
			} else if (ast.isTimes()) {
				// a * b * c....
				IAST rest = F.Times();
				IAST timesClone = ast.clone();
				int j = 1;
				for (int i = 1; i < ast.size(); i++) {
					if (ast.get(i).isMember(variable, false)) {
						rest.add(ast.get(i));
						timesClone.remove(j);
					} else {
						j++;
					}
				}
				if (timesClone.size() == 1) {
					// no change for given expression
					return null;
				}
				IExpr value = F.eval(F.Divide(exprWithoutVariable, timesClone));
				if (rest.size() == 2) {
					return extractVariable(rest.get(1), value, variable);
				} else {
					return extractVariable(rest, value, variable);
				}
			}
		} else if (exprWithVariable.equals(variable)) {
			return exprWithoutVariable;
		}
		return null;
	}

	/**
	 * Analyze the <code>Equal()</code> terms, if we find an expression which equals the given <code>variabe</code>
	 * 
	 * @param termsEqualZeroList
	 *            list of <code>Equal()</code> terms
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>variabe</code>
	 */
	public IAST eliminateOneVariable(IAST termsEqualZeroList, ISymbol variable) {
		IAST equalAST;
		for (int i = 1; i < termsEqualZeroList.size(); i++) {
			equalAST = termsEqualZeroList.getAST(i);
			IExpr variableExpr = eliminateAnalyze(equalAST, variable);
			if (variableExpr != null) {

				IExpr temp;
				IAST rule = F.Rule(variable, variableExpr);
				termsEqualZeroList = termsEqualZeroList.clone();
				termsEqualZeroList.remove(i);
				for (int j = 1; j < termsEqualZeroList.size(); j++) {
					temp = termsEqualZeroList.get(j).replaceAll(rule);
					if (temp != null) {
						termsEqualZeroList.set(j, F.evalExpandAll(temp));
					}
				}
				return termsEqualZeroList;

			}
		}
		return null;
	}
}
