package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class ReplaceList extends AbstractEvaluator {

	private static IAST replaceExpr(final IAST ast, IExpr arg1, IExpr rules, IAST result, int maxNumberOfResults,
			final EvalEngine engine) {
		if (rules.isList()) {
			for (IExpr element : (IAST) rules) {
				if (element.isRuleAST()) {
					IAST rule = (IAST) element;
					Function<IExpr, IExpr> function = Functors.rules(rule);
					IExpr temp = function.apply(arg1);
					if (temp.isPresent()) {
						if (maxNumberOfResults <= result.size()) {
							return result;
						}
						result.append(temp);
					}
				} else {
					WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
					throw wat;
				}

			}
			return result;
		}
		if (rules.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.rules((IAST) rules);
			IExpr temp = function.apply(arg1);
			if (temp.isPresent()) {
				if (maxNumberOfResults <= result.size()) {
					return result;
				}
				result.append(temp);
			}
		} else {
			WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
			engine.printMessage(wat.getMessage());
		}
		return result;
	}

	public ReplaceList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.REPLACE_LIST) {
			return F.NIL;
		}

		Validate.checkRange(ast, 3, 4);
		IAST result = F.List();
		try {
			int maxNumberOfResults = Integer.MAX_VALUE;
			IExpr arg1 = ast.arg1();
			IExpr rules = F.eval(ast.arg2());
			if (ast.isAST3()) {
				IExpr arg3 = F.eval(ast.arg3());
				if (arg3.isSignedNumber()) {
					maxNumberOfResults = ((ISignedNumber) arg3).toInt();
				}
			}
			return replaceExpr(ast, arg1, rules, result, maxNumberOfResults, engine);
		} catch (ArithmeticException ae) {
			engine.printMessage(ae.getMessage());
		} catch (WrongArgumentType wat) {
			engine.printMessage(wat.getMessage());
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		if (!ToggleFeature.REPLACE_LIST) {
			return;
		}
		newSymbol.setAttributes(ISymbol.HOLDREST);
	}
}
