package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.ExpandAll;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Slot;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.k;

import java.util.HashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Summation of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Summation">Wikipedia Summation</a>
 */
public class Sum extends Table {
	private static HashMap<IExpr, IExpr> MAP_0_N = new HashMap<IExpr, IExpr>();

	static {
		// Binomial[#2,#] -> 2^#
		MAP_0_N.put(Binomial(Slot(C2), Slot(C1)), Power(C2, Slot(C1)));
		// #*Binomial[#2,#] -> #*2^(#-1)
		MAP_0_N.put(Times(Slot(C1), Binomial(Slot(C2), Slot(C1))), Times(Slot(C1), Power(C2, Plus(Slot(C1), Times(CN1, C1)))));
	}

	public Sum() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);
		IExpr arg1 = ast.arg1();

		if (arg1.isPlus()) {
			IAST sum = ast.setAtClone(1, null);
			return ((IAST) arg1).mapAt(sum, 1);
		}
		IExpr argN = ast.get(ast.size() - 1);
		IExpr temp;
		if (ast.size() >= 3) {
			if (argN.isList()) {
				Iterator iterator = new Iterator((IAST) argN, EvalEngine.get());
				if (iterator.isValidVariable()) {
					if (iterator.getStart().isInteger() && iterator.getMaxCount().isSymbol() && iterator.getStep().isOne()) {
						temp = definiteSum(arg1, iterator, (IAST) argN);
						if (temp != null) {
							if (ast.size() == 3) {
								return temp;
							} else {
								IAST result = ast.clone();
								result.remove(ast.size() - 1);
								result.set(1, temp);
								return result;
							}
						}
					}
				}

				IAST resultList = Plus();
				temp = evaluateLast(ast.arg1(), iterator, resultList, C0);
				if (temp == null || temp.equals(resultList)) {
					return null;
				}
				if (ast.size() == 3) {
					return temp;
				} else {
					IAST result = ast.clone();
					result.remove(ast.size() - 1);
					result.set(1, temp);
					return result;
				}
			} else if (argN.isSymbol()) {
				temp = indefiniteSum(arg1, (ISymbol) argN);
				if (temp != null) {
					if (ast.size() == 3) {
						return temp;
					} else {
						IAST result = ast.clone();
						result.remove(ast.size() - 1);
						result.set(1, temp);
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Evaluate the definite sum: <code>Sum[arg1, {var, from, to}]</code>
	 * 
	 * @param arg1
	 *            the first argument of the <code>Sum[]</code> function.
	 * @param list
	 *            constructed as <code>{Symbol: var, Integer: from, Symbol: to}</code>
	 * @return
	 */
	public IExpr definiteSum(IExpr arg1, final Iterator iterator, IAST list) {
		final ISymbol var = (ISymbol) iterator.getVariable();
		final IInteger from = (IInteger) iterator.getStart();
		final ISymbol to = (ISymbol) iterator.getMaxCount();
		if (arg1.isFree(var, true)) {
			if (from.isOne()) {
				return F.Times(to, arg1);
			}
			if (from.isZero()) {
				return F.Times(Plus(to, C1), arg1);
			}
		} else {
			if (arg1.isTimes()) {
				// Sum[ Times[a,b,c,...], {var, from, to} ]
				IAST filterCollector = F.Times();
				IAST restCollector = F.Times();
				((IAST) arg1).filter(filterCollector, restCollector, new Predicate<IExpr>() {
					@Override
					public boolean apply(IExpr input) {
						return input.isFree(var, true);
					}
				});
				if (filterCollector.size() > 1) {
					if (restCollector.size() == 2) {
						filterCollector.add(F.Sum(restCollector.arg1(), list));
					} else {
						filterCollector.add(F.Sum(restCollector, list));
					}
					return filterCollector;
				}
			}

			if (from.isZero()) {
				if (arg1.isPower()) {
					return sumPower((IAST) arg1, var, to);
				} else if (arg1.equals(var)) {
					return sumPowerFormula(to, F.C1);
				}
				IExpr repl = arg1.replaceAll(F.List(F.Rule(var, F.Slot(F.C1)), F.Rule(to, F.Slot(F.C2))));
				if (repl != null) {
					IExpr temp = MAP_0_N.get(repl);
					if (temp != null) {
						return temp.replaceAll(F.Rule(F.Slot(F.C1), to));
					}
				}
			}
		}
		if (from.isPositive()) {
			IExpr temp = F.evalQuiet(F.Sum(arg1, F.List(var, C0, from.minus(F.C1))));
			if (!temp.isComplexInfinity()) {
				return F.Subtract(F.Sum(arg1, F.List(var, C0, to)), temp);
			}
		}
		return null;
	}

	/**
	 * Evaluate the indefinite sum: <code>Sum[arg1, var]</code>
	 * 
	 * @param arg1
	 * @param var
	 * @return
	 */
	public IExpr indefiniteSum(IExpr arg1, final ISymbol var) {
		if (arg1.isTimes()) {
			// Sum[ Times[a,b,c,...], var ]
			IAST filterCollector = F.Times();
			IAST restCollector = F.Times();
			((IAST) arg1).filter(filterCollector, restCollector, new Predicate<IExpr>() {
				@Override
				public boolean apply(IExpr input) {
					return input.isFree(var, true);
				}
			});
			if (filterCollector.size() > 1) {
				if (restCollector.size() == 2) {
					filterCollector.add(F.Sum(restCollector.arg1(), var));
				} else {
					filterCollector.add(F.Sum(restCollector, var));
				}
				return filterCollector;
			}
		} else if (arg1.isPower()) {
			return sumPower((IAST) arg1, var, var);
		} else if (arg1.equals(var)) {
			return sumPowerFormula(var, F.C1);
		}
		IExpr repl = arg1.replaceAll(F.List(F.Rule(var, F.Slot(F.C1))));
		if (repl != null) {
			IExpr temp = MAP_0_N.get(repl);
			if (temp != null) {
				return temp.replaceAll(F.Rule(F.Slot(F.C1), var));
			}
		}
		return null;
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions">Wikipedia -
	 * Summation#Some_summations_of_polynomial_expressions</a>.
	 * 
	 * @param powAST
	 *            an AST of the form <code>Power[var, i_Integer]</code>
	 * @param var
	 * @param to
	 * @return
	 */
	public IExpr sumPower(final IAST powAST, final ISymbol var, final IExpr to) {
		if (powAST.equalsAt(1, var) && powAST.arg2().isInteger()) {
			IInteger p = (IInteger) powAST.arg2();
			if (p.isPositive()) {
				return sumPowerFormula(to, p);
			}
		}
		return null;
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions">Wikipedia -
	 * Summation#Some_summations_of_polynomial_expressions</a>.
	 * 
	 * @param to
	 * @param p
	 * @return
	 */
	public IExpr sumPowerFormula(final IExpr to, IInteger p) {
		// TODO optimize if BernoulliB==0 for odd k != 1
		// Sum[var ^ p, var] :=
		// (var+1)^(p+1)/(p+1) + Sum[(var+1)^(p-k+1)*Binomial[p,k]*BernoulliB[k]*(p-k+1)^(-1), {k,1,p}]
		if (p.isOne()) {
			return Times(C1D2, to, Plus(C1, to));
		}
		return F.eval(ExpandAll(Plus(
				Times(Power(Plus(to, C1), Plus(p, C1)), Power(Plus(p, C1), CN1)),
				Sum(Times(Times(Times(Power(Plus(to, C1), Plus(Plus(p, Times(CN1, k)), C1)), Binomial(p, k)), BernoulliB(k)),
						Power(Plus(Plus(p, Times(CN1, k)), C1), CN1)), List(k, C1, p)))));
	}
}
