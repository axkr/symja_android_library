package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class Replace implements IFunctionEvaluator {

	public Replace() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		try {
			IExpr arg1 = ast.arg1();
			if (ast.arg2().isListOfLists()) {
				IAST result = F.List();

				for (IExpr list : (IAST) ast.arg2()) {
					boolean evaled = false;
					IAST subList = (IAST) list;
					for (IExpr element : subList) {
						if (element.isRuleAST()) {
							IAST rule = (IAST) element;
							Function<IExpr, IExpr> function = Functors.rules(rule);
							IExpr temp = function.apply(arg1);
							if (temp != null) {
								result.add(temp);
								evaled = true;
								break;
							}
						} else {
							WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
							throw wat;
						}
					}
					if (!evaled) {
						result.add(arg1);
					}
				}
				return result;
			} else if (ast.arg2().isList()) {
				IExpr result = arg1;
				for (IExpr element : (IAST) ast.arg2()) {
					if (element.isRuleAST()) {
						IAST rule = (IAST) element;
						Function<IExpr, IExpr> function = Functors.rules(rule);
						IExpr temp = function.apply(arg1);
						if (temp != null) {
							return temp;
						}
					} else {
						WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
						throw wat;
					}

				}
				return result;
			}
			if (ast.arg2().isRuleAST()) {
				IAST rule = (IAST) ast.arg2();
				return replaceRule(arg1, rule);
			} else {
				WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
				EvalEngine.get().printMessage(wat.getMessage());
			}
		} catch (WrongArgumentType wat) {
			EvalEngine.get().printMessage(wat.getMessage());
		}
		return null;
	}

	/**
	 * Try to apply one single rule.
	 * 
	 * @param arg1
	 * @param rule
	 * @return
	 */
	public IExpr replaceRule(IExpr arg1, IAST rule) {
		Function<IExpr, IExpr> function = Functors.rules(rule);
		IExpr temp = function.apply(arg1);
		if (temp != null) {
			return temp;
		}
		return arg1;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}
}
