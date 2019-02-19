package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

//final class PowerPolynomialSubstitutions extends PolynomialSubstitutions {
//	public PowerPolynomialSubstitutions(IAST listOfVariables) {
//		super(listOfVariables);
//	}
//
//	protected void substituteExpression(final IExpr exprPoly) {
//		ISymbol symbol = substitutedExpr.get(exprPoly);
//		if (symbol != null) {
//			return;
//		}
//		final int moduleCounter = EvalEngine.get().incModuleCounter();
//		final String varAppend = "$" + moduleCounter;
//		ISymbol newSymbol = F.Dummy("jas" + varAppend);// , engine);
//		substitutedVariables.put(newSymbol, exprPoly);
//		substitutedExpr.put(exprPoly, newSymbol);
//	}
//}

/**
 * Forward and backward substitutions of expressions for polynomials. See
 * <a href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
 * Homogenization</a>
 * 
 */
public class PolynomialSubstitutions {
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
	 * Homogenization</a>
	 * 
	 * @param listOfVariables
	 *            names for the variables.
	 */
	public PolynomialSubstitutions(IAST listOfVariables) {
		vars = listOfVariables;
	}

	/**
	 * Forward and backward substitutions of expressions for polynomials. See
	 * <a href="https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
	 * Homogenization</a>
	 * 
	 * @param listOfVariables
	 * @return
	 */
	public static PolynomialSubstitutions buildSubs(IAST listOfVariables) {
		return new PolynomialSubstitutions(listOfVariables);
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

	public IExpr replaceAll(final IExpr exprPoly) {
		return replaceAll(exprPoly, false, true);
	}

	public IExpr replaceAll(final IExpr exprPoly, boolean coefficient, boolean checkNegativeExponents)
			throws ArithmeticException, ClassCastException {
		int ix = ExpVectorLong.indexVar(exprPoly, vars);
		if (ix >= 0) {
			return exprPoly;
		}
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			if (ast.isPlus()) {
				IASTAppendable plus = F.PlusAlloc(ast.size());
				IExpr expr = ast.arg1();
				plus.append(replaceAll(expr, coefficient, checkNegativeExponents));
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					plus.append(replaceAll(expr, coefficient, checkNegativeExponents));
				}
				return plus;
			} else if (ast.isTimes()) {
				IASTAppendable times = F.TimesAlloc(ast.size());
				IExpr expr = ast.arg1();
				times.append(replaceAll(expr, coefficient, checkNegativeExponents));
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					times.append(replaceAll(expr, coefficient, checkNegativeExponents));
				}
				return times;
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
			if (coefficient) {
				return exprPoly;
			}
			if (exprPoly.isFree(Predicates.in(vars), true)) {
				return exprPoly;
			}
			return replaceExpression(exprPoly);
		}
		return exprPoly;
	}

	private ISymbol replaceExpression(final IExpr exprPoly) {
		if (exprPoly.isSymbol()) {
			return (ISymbol) exprPoly;
		}
		ISymbol symbol = substitutedExpr.get(exprPoly);
		if (symbol != null) {
			return symbol;
		}
		final int moduleCounter = EvalEngine.get().incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		ISymbol newSymbol = F.Dummy("jas" + varAppend);// , engine);
		substitutedVariables.put(newSymbol, exprPoly);
		substitutedExpr.put(exprPoly, newSymbol);
		return newSymbol;
	}

	/**
	 * Variables (ISymbols) which are substituted from the original polynomial (backward substitution) returned in a
	 * <code>IdentityHashMap</code>.
	 */
	public java.util.IdentityHashMap<ISymbol, IExpr> substitutedVariables() {
		return substitutedVariables;
	}

	/**
	 * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward substitution)
	 * returned in a Map.
	 */
	private java.util.Map<IExpr, ISymbol> substitutedExpressions() {
		return substitutedExpr;
	}
}
