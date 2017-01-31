package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ProductRules;

/**
 * Product of expressions.
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation">
 * Wikipedia Multiplication</a>
 */
public class Product extends Table implements ProductRules {
	// TODO solve initialization problem in using 'implements ProductRules {'
	// RULES must be defined in this class at the moment!

	public Product() {
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		IExpr arg1 = ast.arg1();
		if (arg1.isAST()) {
			arg1 = F.expand(arg1, false, false);
			if (!arg1.isPresent()) {
				arg1 = ast.arg1();
			}
		}
		if (arg1.isTimes()) {
			IAST prod = ast.setAtCopy(1, null);
			return ((IAST) arg1).mapThread(prod, 1);
		}

		// arg1 = evalBlockExpandWithoutReap(arg1,
		// determineIteratorVariables(ast));

		if (arg1.isPower()) {
			IExpr powArg2 = arg1.getAt(2);
			boolean flag = true;
			// Prod( i^a, {i,from,to},... )
			for (int i = 2; i < ast.size(); i++) {
				IIterator iterator = Iterator.create((IAST) ast.get(i), engine);
				if (iterator.isValidVariable() && powArg2.isFree(iterator.getVariable())) {
					continue;
				}
				flag = false;
				break;
			}
			if (flag) {
				IAST prod = ast.copy();
				prod.set(1, arg1.getAt(1));
				return F.Power(prod, powArg2);
			}
		}
		IExpr argN = ast.get(ast.size() - 1);
		if (ast.size() >= 3 && argN.isList()) {
			IIterator iterator =  Iterator.create((IAST) argN, engine);
			if (iterator.isValidVariable()) {
				if (iterator.getLowerLimit().isInteger() && iterator.getUpperLimit().isSymbol()
						&& iterator.getStep().isOne()) {
					final ISymbol var = iterator.getVariable();
					final IInteger from = (IInteger) iterator.getLowerLimit();
					final ISymbol to = (ISymbol) iterator.getUpperLimit();
					if (arg1.isPower()) {
						IExpr powArg1 = arg1.getAt(1);
						IExpr powArg2 = arg1.getAt(2);
						if (powArg1.isFree(var)) {
							if (iterator.getLowerLimit().isOne()) {
								if (powArg2.equals(var)) {
									// Prod( a^i, ..., {i,from,to} )
									if (ast.isAST2()) {
										return F.Power(powArg1, Times(C1D2, to, Plus(C1, to)));
									}
									IAST result = ast.clone();
									result.remove(ast.size() - 1);
									result.set(1, F.Power(powArg1, Times(C1D2, to, Plus(C1, to))));
									return result;
								}
							}
						}
					}
					if (arg1.isFree(var)) {
						if (ast.isAST2()) {
							if (from.isOne()) {
								return F.Power(ast.arg1(), to);
							}
							if (from.isZero()) {
								return F.Power(ast.arg1(), Plus(to, C1));
							}
						} else {
							IAST result = ast.clone();
							result.remove(ast.size() - 1);
							if (from.isOne()) {
								result.set(1, F.Power(ast.arg1(), to));
								return result;
							}
							if (from.isZero()) {
								result.set(1, F.Power(ast.arg1(), Plus(to, C1)));
								return result;
							}

						}
					}

				}
			}
			IAST resultList = Times();
			IExpr temp = evaluateLast(ast.arg1(), iterator, resultList, C1);
			if (!temp.isPresent() || temp.equals(resultList)) {
				return F.NIL;
			}
			if (ast.isAST2()) {
				return temp;
			} else {
				IAST result = ast.clone();
				result.remove(ast.size() - 1);
				result.set(1, temp);
				return result;
			}

		}
		return F.NIL;
	}

	@Override
	public IExpr numericEval(final IAST functionList, EvalEngine engine) {
		return evaluate(functionList, engine);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.DELAYED_RULE_EVALUATION);
		IAST ruleList;
		if ((ruleList = getRuleAST()) != null) {
			EvalEngine.get().addRules(ruleList);
		}
	}
}
