package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorLevelSpecification;

public class Replace extends AbstractEvaluator {

	private static final class ReplaceFunction implements Function<IExpr, IExpr> {
		private final IAST ast;
		private final EvalEngine engine;
		private IExpr rules;

		public ReplaceFunction(final IAST ast, final IExpr rules, final EvalEngine engine) {
			this.ast = ast;
			this.rules = rules;
			this.engine = engine;
		}

		/**
		 * Replace the <code>input</code> expression with the given rules.
		 * 
		 * @param input
		 *            the expression which should be replaced by the given rules
		 * @return the expression created by the replacements or
		 *         <code>null</code> if no replacement occurs
		 */
		@Override
		public IExpr apply(IExpr input) {
			if (rules.isList()) {
				for (IExpr element : (IAST) rules) {
					if (element.isRuleAST()) {
						IAST rule = (IAST) element;
						Function<IExpr, IExpr> function = Functors.rules(rule);
						IExpr temp = function.apply(input);
						if (temp.isPresent()) {
							return temp;
						}
					} else {
						WrongArgumentType wat = new WrongArgumentType(ast, ast, -1,
								"Rule expression (x->y) expected: ");
						throw wat;
					}

				}
				return input;
			}
			if (rules.isRuleAST()) {
				return replaceRule(input, (IAST) rules);
			} else {
				WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
				engine.printMessage(wat.getMessage());
			}
			return F.NIL;
		}

		public void setRule(IExpr rules) {
			this.rules = rules;
		}

	}

	private static IExpr replaceExpr(final IAST ast, IExpr arg1, IExpr rules, final EvalEngine engine) {
		if (rules.isListOfLists()) {
			IAST result = F.List();

			for (IExpr list : (IAST) rules) {
				IAST subList = (IAST) list;
				IExpr temp = F.NIL;
				for (IExpr element : subList) {
					if (element.isRuleAST()) {
						IAST rule = (IAST) element;
						Function<IExpr, IExpr> function = Functors.rules(rule);
						temp = function.apply(arg1);
						if (temp.isPresent()) {
							break;
						}
					} else {
						WrongArgumentType wat = new WrongArgumentType(ast, ast, -1,
								"Rule expression (x->y) expected: ");
						throw wat;
					}
				}
				if (temp.isPresent()) {
					result.append(temp);
				} else {
					result.append(arg1);
				}
			}
			return result;
		} else if (rules.isList()) {
			for (IExpr element : (IAST) rules) {
				if (element.isRuleAST()) {
					IAST rule = (IAST) element;
					Function<IExpr, IExpr> function = Functors.rules(rule);
					IExpr temp = function.apply(arg1);
					if (temp.isPresent()) {
						return temp;
					}
				} else {
					WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
					throw wat;
				}

			}
			return arg1;
		}
		if (rules.isRuleAST()) {
			return replaceRule(arg1, (IAST) rules);
		} else {
			WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
			engine.printMessage(wat.getMessage());
		}
		return F.NIL;
	}

	private static IExpr replaceExprWithLevelSpecification(final IAST ast, IExpr arg1, IExpr rules,
			IExpr exprLevelSpecification, EvalEngine engine) {
		// use replaceFunction#setRule() method to set the current rules which
		// are initialized with null
		ReplaceFunction replaceFunction = new ReplaceFunction(ast, null, engine);
		VisitorLevelSpecification level = new VisitorLevelSpecification(replaceFunction, exprLevelSpecification, false);

		if (rules.isListOfLists()) {
			IAST result = F.List();
			for (IExpr list : (IAST) rules) {
				IExpr temp = F.NIL;
				IAST subList = (IAST) list;
				for (IExpr element : subList) {
					if (element.isRuleAST()) {
						IAST rule = (IAST) element;
						replaceFunction.setRule(rule);
						temp = arg1.accept(level);
						if (temp.isPresent()) {
							break;
						}
					} else {
						WrongArgumentType wat = new WrongArgumentType(ast, ast, -1,
								"Rule expression (x->y) expected: ");
						throw wat;
					}
				}
				result.append(temp.orElse(arg1));
			}
			return result;
		}

		replaceFunction.setRule(rules);
		return arg1.accept(level).orElse(arg1);
	}

	/**
	 * Try to apply one single rule.
	 * 
	 * @param arg1
	 * @param rule
	 * @return
	 */
	private static IExpr replaceRule(IExpr arg1, IAST rule) {
		Function<IExpr, IExpr> function = Functors.rules(rule);
		IExpr temp = function.apply(arg1);
		if (temp.isPresent()) {
			return temp;
		}
		return arg1;
	}

	public Replace() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST1()) {
			return F.operatorFormAST1(ast);
		}
		Validate.checkRange(ast, 3, 4);
		try {
			IExpr arg1 = ast.arg1();
			IExpr rules = F.eval(ast.arg2());
			if (ast.isAST3()) {
				// arg3 should contain a "level specification":
				return replaceExprWithLevelSpecification(ast, arg1, rules, ast.arg3(), engine);
			}
			return replaceExpr(ast, arg1, rules, engine);
		} catch (WrongArgumentType wat) {
			engine.printMessage(wat.getMessage());
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDREST);
	}
}
