package org.matheclipse.core.polynomials;

import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.structure.RingFactory;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;

/**
 * GenPolynomialRing generic polynomial factory implementing ExprRingFactory; Factory for n-variate ordered polynomials
 * over C. Almost immutable object, except variable names.
 * 
 */

public class PolynomialSubstitutions {
	/**
	 * The names of the variables. This value can be modified.
	 */
	protected IAST vars;

	protected java.util.IdentityHashMap<ISymbol, IExpr> substitutedVariables = new IdentityHashMap<ISymbol, IExpr>();

	protected java.util.HashMap<IExpr, ISymbol> substitutedExpr = new HashMap<IExpr, ISymbol>();

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param listOfVariables
	 *            names for the variables.
	 */
	public PolynomialSubstitutions(IAST listOfVariables) {
		vars = listOfVariables;
	}

	private void substitute(final IExpr exprPoly) {
		substitute(exprPoly, false, true);
	}

	private void substitute(final IExpr exprPoly, boolean coefficient, boolean checkNegativeExponents)
			throws ArithmeticException, ClassCastException {
		int ix = ExpVectorLong.indexVar(exprPoly, vars);
		if (ix >= 0) {
			return;
		}
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			if (ast.isPlus()) {
				IExpr expr = ast.arg1();
				substitute(expr, coefficient, checkNegativeExponents);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					substitute(expr, coefficient, checkNegativeExponents);
				}
				return;
			} else if (ast.isTimes()) {
				IExpr expr = ast.arg1();
				substitute(expr, coefficient, checkNegativeExponents);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					substitute(expr, coefficient, checkNegativeExponents);
				}
				return;
			} else if (ast.isPower()) {
				final IExpr base = ast.base();
				IExpr exp = ast.exponent();
				// if (exp.isTimes()) {
				// int exponent = exp.first().toIntDefault(Integer.MIN_VALUE);
				// if (exponent > 0) {
				// substitute(base, coefficient, checkNegativeExponents);
				// } else {
				// substituteExpression(ast);
				// }
				// }
				int exponent = exp.toIntDefault(Integer.MIN_VALUE);
				if (exponent > 0) {
					ix = ExpVectorLong.indexVar(base, vars);
					if (ix >= 0) {
						return;
					}
					substitute(base, coefficient, checkNegativeExponents);
				} else {
					substituteExpression(ast);
				}
				return;
			}
			if (coefficient) {
				return;
			}
			if (exprPoly.isFree(Predicates.in(vars), true)) {
				return;
			}
			substituteExpression(exprPoly);
			return;
		} else if (exprPoly instanceof ISymbol) {
			if (coefficient) {
				return;
			}
			return;

		} else if (exprPoly.isNumber()) {
			return;
		}
	}

	private void substituteExpression(final IExpr exprPoly) {
		ISymbol symbol = substitutedExpr.get(exprPoly);
		if (symbol != null) {
			return;
		}
		final int moduleCounter = EvalEngine.get().incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		ISymbol newSymbol = F.Dummy("jas" + varAppend);// , engine);
		substitutedVariables.put(newSymbol, exprPoly);
		substitutedExpr.put(exprPoly, newSymbol);
	}

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
				IExpr exp = ast.exponent();
				if (exp.isTimes()) {
					int exponent = exp.first().toIntDefault(Integer.MIN_VALUE);
					if (exponent > 0) {
						IExpr rest = exp.rest().getOneIdentity(F.C1);
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

	public java.util.IdentityHashMap<ISymbol, IExpr> substitutedVariables() {
		return substitutedVariables; // .copyAppendable();
	}

	public java.util.Map<IExpr, ISymbol> substitutedExpressions() {
		return substitutedExpr; // .copyAppendable();
	}
}
