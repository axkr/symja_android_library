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
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.k;

import java.util.HashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.list.algorithms.EvaluationSupport;
import org.matheclipse.core.reflection.system.rules.SumRules;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

/**
 * Summation of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Summation">Wikipedia Summation</a>
 */
public class Sum extends Table implements SumRules {
	private static HashMap<IExpr, IExpr> MAP_0_N = new HashMap<IExpr, IExpr>();

	static {
		// Binomial[#2,#] -> 2^#
		MAP_0_N.put(Binomial(Slot(C2), Slot(C1)), Power(C2, Slot(C1)));
		// #*Binomial[#2,#] -> #*2^(#-1)
		MAP_0_N.put(Times(Slot(C1), Binomial(Slot(C2), Slot(C1))), Times(Slot(C1), Power(C2, Plus(Slot(C1), Times(CN1, C1)))));
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Sum() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isAST()) {
			arg1 = F.expand((IAST) arg1, false, false);
			if (arg1 == null) {
				arg1 = ast.arg1();
			}
		}
		if (arg1.isPlus()) {
			IAST sum = ast.setAtClone(1, null);
			return ((IAST) arg1).mapAt(sum, 1);
		}

		IExpr argN = ast.get(ast.size() - 1);
		// IExpr variable = null;
		// if (argN.isVariable()) {
		// variable = argN;
		// } else if (argN.isList()) {
		// IAST list = (IAST) argN;
		// if (list.size() >= 2) {
		// if (list.arg1().isVariable()) {
		// variable = list.arg1();
		// }
		// }
		// }
		// if (variable != null && arg1.isTimes()) {
		// IAST prod = (IAST) arg1;
		// IAST filterAST = F.Times();
		// IAST restAST = F.Times();
		// prod.filter(filterAST, restAST, Predicates.isFree(variable));
		// if (restAST.size() > 1) {
		// if (ast.size() > 3) {
		// IAST temp = ast.removeAtClone(ast.size() - 1);
		// temp.set(1, restAST.getOneIdentity(filterAST));
		// return F.Sum(temp, argN);
		// }
		// return F.Sum(ast.removeAtClone(ast.size() - 1), argN);
		// }
		// }

		IExpr temp;

		arg1 = evalBlockExpandWithoutReap(ast.arg1(), determineIteratorVariables(ast));

		if (argN.isList()) {
			Iterator iterator = new Iterator((IAST) argN, EvalEngine.get());
			if (iterator.isValidVariable() && !iterator.isNumericFunction()) {
				if (!iterator.getMaxCount().isDirectedInfinity() && iterator.getStep().isOne()) {
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
	public IExpr definiteSum(final IExpr expr, final Iterator iterator, IAST list) {
		final ISymbol var = (ISymbol) iterator.getVariable();
		IExpr arg1 = expr;
		final IExpr from = iterator.getStart();
		final IExpr to = iterator.getMaxCount();

		if (arg1.isFree(var, true)) {
			if (from.isOne()) {
				return F.Times(to, arg1);
			}
			if (from.isZero()) {
				return F.Times(Plus(to, C1), arg1);
			}
			if (!F.evalTrue(F.Greater(C1, from)) && !F.evalTrue(F.Greater(from, to))) {
				return F.Times(Plus(C1, F.Negate(from), to), arg1);
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
					IExpr temp = F.evalQuiet(F.Sum(restCollector.getOneIdentity(F.C1), list));
					if (temp.isFreeAST(F.Sum)) {
						filterCollector.add(temp);
						return filterCollector;
					}

				}
			}

			if (arg1.equals(var)) {
				if ((from.isVariable() && !from.equals(var)) || (to.isVariable() && !to.equals(var))) {
					// Sum(i, {i, from, to})
					return Times(C1D2, Plus(Subtract(to, from), C1), Plus(from, to));
				}
			}
			if (from.isZero() || from.isOne()) {
				if (arg1.isPower()) {
					return sumPower((IAST) arg1, var, F.C0, to);
				} else if (arg1.equals(var)) {
					return sumPowerFormula(from, to, F.C1);
				}
				if (from.isZero()) {
					IExpr repl = arg1.replaceAll(F.List(F.Rule(var, F.Slot(F.C1)), F.Rule(to, F.Slot(F.C2))));
					if (repl != null) {
						EvaluationSupport.sortTimesPlus(repl);
						IExpr temp = MAP_0_N.get(repl);
						if (temp != null) {
							return temp.replaceAll(F.Rule(F.Slot(F.C1), to));
						}
					}
				}
			}
			if (arg1.isPower() && !F.evalTrue(F.Greater(C1, from)) && !F.evalTrue(F.Greater(from, to))) {
				IAST powAST = (IAST) arg1;
				if (powAST.equalsAt(1, var) && powAST.arg2().isFree(var)) {
					// i^a,{i,n,m} ==> HurwitzZeta(-a, n)-HurwitzZeta(-a,1+m)
					return F.Subtract(F.HurwitzZeta(F.Negate(powAST.arg2()), from),
							F.HurwitzZeta(F.Negate(powAST.arg2()), F.Plus(1, to)));
				}
			}
		}
		if (from.isPositive()) {
			IExpr temp1 = F.evalQuiet(F.Sum(arg1, F.List(var, C0, from.minus(F.C1))));
			if (!temp1.isComplexInfinity() && temp1.isFreeAST(F.Sum)) {
				IExpr temp2 = F.evalQuietNull(F.Sum(arg1, F.List(var, C0, to)));
				if (temp2 != null && !temp2.isComplexInfinity()) {
					return F.Subtract(temp2, temp1);
				}
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
			return sumPower((IAST) arg1, var, F.C1, var);
		} else if (arg1.equals(var)) {
			return sumPowerFormula(F.C1, var, F.C1);
		}
		IExpr repl = arg1.replaceAll(F.List(F.Rule(var, F.Slot(F.C1))));
		if (repl != null) {
			EvaluationSupport.sortTimesPlus(repl);
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
	 * @param from
	 *            TODO
	 * @param to
	 * @return
	 */
	public IExpr sumPower(final IAST powAST, final ISymbol var, IExpr from, final IExpr to) {
		if (powAST.equalsAt(1, var) && powAST.arg2().isInteger()) {
			IInteger p = (IInteger) powAST.arg2();
			if (p.isPositive()) {
				return sumPowerFormula(from, to, p);
			}
		}
		return null;
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions">Wikipedia -
	 * Summation#Some_summations_of_polynomial_expressions</a>.
	 * 
	 * @param from
	 *            TODO
	 * @param to
	 * @param p
	 * 
	 * @return
	 */
	public IExpr sumPowerFormula(IExpr from, final IExpr to, IInteger p) {
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

	/**
	 * Evaluate built-in rules and define Attributes for a function.
	 * 
	 */
	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.HOLDALL);
		IAST ruleList;
		if ((ruleList = getRuleAST()) != null) {
			EvalEngine.get().addRules(ruleList);
		}

	}
}
