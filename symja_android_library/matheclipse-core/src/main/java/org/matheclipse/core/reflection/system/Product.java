package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
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
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		if (ast.arg1().isTimes()) {
			IAST prod = ast.setAtClone(1, null);
			return ((IAST) ast.arg1()).mapAt(prod, 1);
		}
		if (ast.arg1().isPower()) {
			IExpr powArg1 = ast.arg1().getAt(1);
			IExpr powArg2 = ast.arg1().getAt(2);
			boolean flag = true;
			// Prod( i^a, {i,from,to},... )
			for (int i = 2; i < ast.size(); i++) {
				if (ast.get(i).isList() && (((IAST) ast.get(i)).size() == 4 || ((IAST) ast.get(i)).size() == 5)) {
					IAST list = (IAST) ast.get(i);
					if (powArg2.isFree(list.arg1(), true)) {
						continue;
					}
				}
				flag = false;
				break;
			}
			if (flag) {
				IAST prod = ast.clone();
				prod.set(1, powArg1);
				return F.Power(prod, powArg2);
			}
		}
		IExpr argN = ast.get(ast.size() - 1);
		if (ast.size() >= 3 && argN.isList()) {
			Iterator iterator = new Iterator((IAST) argN, EvalEngine.get());
			if (iterator.isValidVariable()) {
				if (iterator.getStart().isInteger() && iterator.getMaxCount().isSymbol() && iterator.getStep().isOne()) {
					final ISymbol var = iterator.getVariable();
					final IInteger from = (IInteger) iterator.getStart();
					final ISymbol to = (ISymbol) iterator.getMaxCount();
					if (ast.isFreeAt(1, var)) {
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

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
