package org.matheclipse.core.reflection.system;

import java.util.Iterator;
import java.util.function.Function;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherList;

public class ReplaceList extends AbstractEvaluator {

	private static IExpr replaceExpr(final IAST ast, IExpr arg1, IExpr rules, IASTAppendable result,
			int maxNumberOfResults, final EvalEngine engine) {
		if (rules.isList()) {
			IAST rulesList = (IAST) rules;
			IExpr temp = F.NIL;
			for (IExpr element : rulesList) {
				if (element.isRuleAST()) {
					IAST rule = (IAST) element;
					Function<IExpr, IExpr> function = Functors.listRules(rule, result, engine);
					temp = function.apply(arg1);
				} else {
					WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
					throw wat;
				}
			}

			return result;
		}
		if (rules.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.listRules((IAST) rules, result, engine);
			IExpr temp = function.apply(arg1);
			if (temp.isPresent()) {
				return temp;
			}
		} else {
			WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
			engine.printMessage("ReplaceList: " + wat.getMessage());
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
		if (ast.size() == 2 && ast.head().isAST(F.ReplaceList, 2)) {
			return F.ReplaceList(ast.first(), ast.head().first());
		}
		if (ast.size() >= 3 && ast.size() <= 4) {
			try {
				int maxNumberOfResults = Integer.MAX_VALUE;
				IExpr arg1 = ast.arg1();
				IExpr rules = engine.evaluate(ast.arg2());
				if (ast.isAST3()) {
					IExpr arg3 = engine.evaluate(ast.arg3());
					if (arg3.isReal()) {
						maxNumberOfResults = ((ISignedNumber) arg3).toInt();
					}
				}
				IASTAppendable result = F.ListAlloc();
				return replaceExpr(ast, arg1, rules, result, maxNumberOfResults, engine);
			} catch (ArithmeticException ae) {
				engine.printMessage("ReplaceList: " + ae.getMessage());
			} catch (WrongArgumentType wat) {
				engine.printMessage("ReplaceList: " + wat.getMessage());
			}
			return F.List();
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		if (!ToggleFeature.REPLACE_LIST) {
			return;
		}
		newSymbol.setAttributes(ISymbol.HOLDREST);
	}
}
