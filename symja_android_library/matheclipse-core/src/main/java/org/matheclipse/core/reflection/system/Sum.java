package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.ExpandAll;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.k;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.SumRules;
import org.matheclipse.parser.client.SyntaxError;

import java.util.function.Predicate;

/**
 * Summation of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Summation">Wikipedia Summation</a>
 */
public class Sum extends Table implements SumRules {

	@Override
	public IAST getRuleAST() {
		IAST rules = RULES1;
		rules.addAll(RULES2);
		return rules;
	}

	public Sum() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isAST()) {
			arg1 = F.expand(arg1, false, false);
			if (arg1 == null) {
				arg1 = ast.arg1();
			}
		}
		if (arg1.isPlus()) {
			IAST sum = ast.setAtClone(1, null);
			return ((IAST) arg1).mapAt(sum, 1);
		}

		VariablesSet variablesSet = determineIteratorExprVariables(ast);
		IAST varList = variablesSet.getVarList();
		IExpr argN = ast.get(ast.size() - 1);
		Iterator iterator = null;
		IExpr temp;
		if (argN.isList()) {
			argN = evalBlockWithoutReap(argN, varList);
			iterator = new Iterator((IAST) argN, engine);
			if (iterator.isSetIterator() || iterator.isNumericFunction()) {
				IAST resultList = Plus();
				temp = evaluateLast(ast.arg1(), iterator, resultList, C0);
				if (temp != null && !temp.equals(resultList)) {
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

		// arg1 = evalBlockExpandWithoutReap(ast.arg1(), varList);
		if (arg1.isTimes()) {
			if (variablesSet.size() > 0) {
				temp = collectConstantFactors(ast, (IAST) arg1, variablesSet);
				if (temp != null) {
					return temp;
				}
			}
		}

		if (iterator != null) {
			if (iterator.isValidVariable() && iterator.isNumericFunction()) {
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
			}

			if (iterator.isValidVariable() && !iterator.isNumericFunction()) {
				if (!iterator.getMaxCount().isDirectedInfinity() && iterator.getStep().isOne()) {

					temp = definiteSum(arg1, iterator, (IAST) argN);
					if (temp != null) {
						if (ast.size() == 3) {
							return temp;
						}
						IAST result = ast.clone();
						result.remove(ast.size() - 1);
						result.set(1, temp);
						return result;
					}

				}
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

	private IExpr collectConstantFactors(final IAST ast, IAST prod, VariablesSet variablesSet) {
		IAST filterAST = F.Times();
		IAST restAST = F.Times();
		prod.filter(filterAST, restAST, VariablesSet.isFree(variablesSet));
		if (filterAST.size() > 1) {
			IAST reducedSum = ast.clone();
			reducedSum.set(1, restAST.getOneIdentity(F.C1));
			return F.Times(filterAST.getOneIdentity(F.C0), reducedSum);
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
		final ISymbol var = iterator.getVariable();
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
					public boolean test(IExpr input) {
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

			if (!F.evalTrue(F.Greater(C0, from)) && !F.evalTrue(F.Greater(from, to))) {
				IExpr temp = null;
				if (arg1.isPower()) {
					temp = sumPower((IAST) arg1, var, from, to);
				} else if (arg1.equals(var)) {
					temp = sumPowerFormula(from, to, F.C1);
				}
				if (temp != null) {
					return temp;
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
				public boolean test(IExpr input) {
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
		IExpr term1 = null;
		if (!from.isOne()) {
			IExpr fromMinusOne = F.Plus(F.CN1, from);
			if (p.isOne()) {
				term1 = Times(C1D2, fromMinusOne, Plus(C1, fromMinusOne));
			} else {
				term1 = F.eval(ExpandAll(Plus(
						Times(Power(Plus(fromMinusOne, C1), Plus(p, C1)), Power(Plus(p, C1), CN1)),
						Sum(Times(
								Times(Times(Power(Plus(fromMinusOne, C1), Plus(Plus(p, Times(CN1, k)), C1)), Binomial(p, k)),
										BernoulliB(k)), Power(Plus(Plus(p, Times(CN1, k)), C1), CN1)), List(k, C1, p)))));
			}
		}
		IExpr term2 = null;
		if (p.isOne()) {
			term2 = Times(C1D2, to, Plus(C1, to));
		} else {
			term2 = F.eval(ExpandAll(Plus(
					Times(Power(Plus(to, C1), Plus(p, C1)), Power(Plus(p, C1), CN1)),
					Sum(Times(Times(Times(Power(Plus(to, C1), Plus(Plus(p, Times(CN1, k)), C1)), Binomial(p, k)), BernoulliB(k)),
							Power(Plus(Plus(p, Times(CN1, k)), C1), CN1)), List(k, C1, p)))));
		}
		if (term1 == null) {
			return term2;
		}
		return Subtract(term2, term1);
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
