package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Forward and backward substitutions of expressions for polynomials. See
 * <a href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
 * Homogenization</a>
 * 
 */
public class PolynomialHomogenization {
	/**
	 * The names of the variables from the unsubstituted polynomials.
	 */
	private final IAST vars;

	/**
	 * Variables (ISymbols) which are substituted from the original polynomial (backward substitution).
	 */
	protected java.util.IdentityHashMap<ISymbol, IExpr> substitutedVariables = new IdentityHashMap<ISymbol, IExpr>();

	/**
	 * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward substitution).
	 */
	protected java.util.HashMap<IExpr, ISymbol> substitutedExpr = new HashMap<IExpr, ISymbol>();

	/**
	 * Forward and backward substitutions of expressions for polynomials. See
	 * <a href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
	 * Homogenization</a> (page 112)
	 * 
	 * @param listOfVariables
	 *            names for the variables.
	 */
	public PolynomialHomogenization(IAST listOfVariables) {
		vars = listOfVariables;
	}

	// private void substitute(final IExpr exprPoly) {
	// substitute(exprPoly, false, true);
	// }

	// private void substitute(final IExpr exprPoly, boolean coefficient, boolean checkNegativeExponents)
	// throws ArithmeticException, ClassCastException {
	// int ix = ExpVectorLong.indexVar(exprPoly, vars);
	// if (ix >= 0) {
	// return;
	// }
	// if (exprPoly instanceof IAST) {
	// final IAST ast = (IAST) exprPoly;
	// if (ast.isPlus()) {
	// IExpr expr = ast.arg1();
	// substitute(expr, coefficient, checkNegativeExponents);
	// for (int i = 2; i < ast.size(); i++) {
	// expr = ast.get(i);
	// substitute(expr, coefficient, checkNegativeExponents);
	// }
	// return;
	// } else if (ast.isTimes()) {
	// IExpr expr = ast.arg1();
	// substitute(expr, coefficient, checkNegativeExponents);
	// for (int i = 2; i < ast.size(); i++) {
	// expr = ast.get(i);
	// substitute(expr, coefficient, checkNegativeExponents);
	// }
	// return;
	// } else if (ast.isPower()) {
	// final IExpr base = ast.base();
	// IExpr exp = ast.exponent();
	// // if (exp.isTimes()) {
	// // int exponent = exp.first().toIntDefault(Integer.MIN_VALUE);
	// // if (exponent > 0) {
	// // substitute(base, coefficient, checkNegativeExponents);
	// // } else {
	// // substituteExpression(ast);
	// // }
	// // }
	// int exponent = exp.toIntDefault(Integer.MIN_VALUE);
	// if (exponent > 0) {
	// ix = ExpVectorLong.indexVar(base, vars);
	// if (ix >= 0) {
	// return;
	// }
	// substitute(base, coefficient, checkNegativeExponents);
	// } else {
	// substituteExpression(ast);
	// }
	// return;
	// }
	// if (coefficient) {
	// return;
	// }
	// if (exprPoly.isFree(Predicates.in(vars), true)) {
	// return;
	// }
	// substituteExpression(exprPoly);
	// return;
	// } else if (exprPoly instanceof ISymbol) {
	// if (coefficient) {
	// return;
	// }
	// return;
	//
	// } else if (exprPoly.isNumber()) {
	// return;
	// }
	// }

	// private void substituteExpression(final IExpr exprPoly) {
	// ISymbol symbol = substitutedExpr.get(exprPoly);
	// if (symbol != null) {
	// return;
	// }
	// final int moduleCounter = EvalEngine.get().incModuleCounter();
	// final String varAppend = "$" + moduleCounter;
	// ISymbol newSymbol = F.Dummy("jas" + varAppend);// , engine);
	// substitutedVariables.put(newSymbol, exprPoly);
	// substitutedExpr.put(exprPoly, newSymbol);
	// }

	// public IExpr replaceAll(final IExpr exprPoly) {
	// return replaceAll(exprPoly, true);
	// }

	/**
	 * Forward substitution - transforming the expression into a polynomial expression by introducing &quot;substitution
	 * variables&quot;. After transforming the polynomial expression may be solvable by a polynomial factorization.
	 * 
	 * @param expression
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IExpr replaceForward(final IExpr expression) throws ArithmeticException, ClassCastException {
		int ix = ExpVectorLong.indexVar(expression, vars);
		if (ix >= 0) {
			return expression;
		}
		if (expression instanceof IAST) {
			final IAST ast = (IAST) expression;
			if (ast.isPlus() || ast.isTimes()) {
				IASTAppendable newAST = F.ast(ast.head(), ast.size(), false);
				IExpr temp = ast.arg1();
				newAST.append(replaceForward(temp));
				for (int i = 2; i < ast.size(); i++) {
					temp = ast.get(i);
					newAST.append(replaceForward(temp));
				}
				return newAST;
			} else if (ast.isPower()) {
				final IExpr base = ast.base();
				if (!base.has(x -> vars.isMember(x), true)) {
					IExpr exp = ast.exponent();
					if (exp.isTimes()) {
						int exponent = exp.first().toIntDefault(Integer.MIN_VALUE);
						if (exponent > 0) {
							IExpr rest = exp.rest().oneIdentity1();
							return F.Power(replaceExpression(base.power(rest)), exponent);
						}
						return replaceExpression(ast);
					}
					int exponent = exp.toIntDefault(Integer.MIN_VALUE);
					if (exponent > 0) {
						if (base.isSymbol()) {
							return ast;
						}
						ix = ExpVectorLong.indexVar(base, vars);
						if (ix >= 0) {
							return ast;
						}
					}
					return replaceExpression(ast);
				}
				return ast;
			}
//			if (expression.isFree(Predicates.in(vars), true)) {
//				return expression;
//			}
			return replaceExpression(expression);
		}
		return expression;
	}

	private IExpr replaceExpression(final IExpr exprPoly) {
		if (exprPoly.isSymbol()) {
			return (ISymbol) exprPoly;
		}
		ISymbol symbol = substitutedExpr.get(exprPoly);
		if (symbol != null) {
			return symbol;
		}
		if (exprPoly.isAST() && exprPoly.head().isBuiltInSymbol()) {
			if (!((IBuiltInSymbol) exprPoly.head()).isNumericFunctionAttribute()) {
				return exprPoly;
			}
		}
		final int moduleCounter = EvalEngine.get().incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		ISymbol newSymbol = F.Dummy("jas" + varAppend);// , engine);
		substitutedVariables.put(newSymbol, exprPoly);
		substitutedExpr.put(exprPoly, newSymbol);
		return newSymbol;
	}

	/**
	 * Backward substitution - transforming the expression back by replacing the introduce &quot;substitution
	 * variables&quot;.
	 * 
	 * @param expression
	 * @return
	 */
	public IExpr replaceBackward(final IExpr expression) {
		return F.subst(expression, substitutedVariables);
	}

	/**
	 * Variables (ISymbols) which are substituted from the original polynomial (backward substitution) returned in a
	 * <code>IdentityHashMap</code>.
	 */
	public java.util.IdentityHashMap<ISymbol, IExpr> substitutedVariables() {
		return substitutedVariables;
	}

	public java.util.Set<ISymbol> substitutedVariablesSet() {
		return substitutedVariables.keySet();
	}

	public int size() {
		return substitutedVariables.size();
	}

	/**
	 * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward substitution)
	 * returned in a Map.
	 */
	private java.util.Map<IExpr, ISymbol> substitutedExpressions() {
		return substitutedExpr;
	}
}
