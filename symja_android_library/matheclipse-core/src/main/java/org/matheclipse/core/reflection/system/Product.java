package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Product;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.m;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.s_;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Product of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation"> Wikipedia Multiplication</a>
 */
public class Product extends Table {
	// TODO solve initialization problem in using 'implements ProductRules {'
	// RULES must be defined in this class at the moment!

	final public static IAST RULES = List(ISetDelayed(Product(x_Symbol, List(x_, C0, m_)), C0),
			ISetDelayed(Product(x_Symbol, List(x_, C0, m_, s_)), C0),
			ISetDelayed(Product(x_Symbol, List(x_, C1, m_)), Condition(Factorial(m), FreeQ(x, m))));

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
			if (arg1 == null) {
				arg1 = ast.arg1();
			}
		}
		if (arg1.isTimes()) {
			IAST prod = ast.setAtClone(1, null);
			return ((IAST) arg1).mapAt(prod, 1);
		}

		// arg1 = evalBlockExpandWithoutReap(arg1, determineIteratorVariables(ast));

		if (arg1.isPower()) {
			IExpr powArg2 = arg1.getAt(2);
			boolean flag = true;
			// Prod( i^a, {i,from,to},... )
			for (int i = 2; i < ast.size(); i++) {
				Iterator iterator = new Iterator((IAST) ast.get(i), engine);
				if (iterator.isValidVariable() && powArg2.isFree(iterator.getVariable())) {
					continue;
				}
				flag = false;
				break;
			}
			if (flag) {
				IAST prod = ast.clone();
				prod.set(1, arg1.getAt(1));
				return F.Power(prod, powArg2);
			}
		}
		IExpr argN = ast.get(ast.size() - 1);
		if (ast.size() >= 3 && argN.isList()) {
			Iterator iterator = new Iterator((IAST) argN, engine);
			if (iterator.isValidVariable()) {
				if (iterator.getStart().isInteger() && iterator.getMaxCount().isSymbol() && iterator.getStep().isOne()) {
					final ISymbol var = iterator.getVariable();
					final IInteger from = (IInteger) iterator.getStart();
					final ISymbol to = (ISymbol) iterator.getMaxCount();
					if (arg1.isPower()) {
						IExpr powArg1 = arg1.getAt(1);
						IExpr powArg2 = arg1.getAt(2);
						if (powArg1.isFree(var)) {
							if (iterator.getStart().isOne()) {
								if (powArg2.equals(var)) {
									// Prod( a^i, ..., {i,from,to} )
									if (ast.size() == 3) {
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
						if (ast.size() == 3) {
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
			IExpr temp = evaluateLast(ast.arg1(), iterator, resultList, C0);
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
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList, EvalEngine engine) {
		return evaluate(functionList, null);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
