package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.AssociationAST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class AssociationFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Association.setEvaluator(new Association());
			F.Keys.setEvaluator(new Keys());
			F.KeyExistsQ.setEvaluator(new KeyExistsQ());
			F.Values.setEvaluator(new Values());
		}
	}

	private static class Association extends AbstractEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast instanceof AssociationAST) {
				return F.NIL;
			}
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isListOfRules(true)) {
					return new AssociationAST((IAST) arg1);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, EvalEngine engine) {
			if (leftHandSide.head().isSymbol()) {
				ISymbol symbol = (ISymbol) leftHandSide.head();

				IExpr temp = symbol.assignedValue();
				if (temp == null) {
					// `1` is not a variable with a value, so its value cannot be changed.
					return IOFunctions.printMessage(F.Set, "rvalue", F.List(symbol), engine);
				} else {
					if (symbol.isProtected()) {
						return IOFunctions.printMessage(F.Set, "write", F.List(symbol), EvalEngine.get());
					}
					try {
						IExpr lhsHead = engine.evaluate(symbol);
						if (lhsHead instanceof AssociationAST) {
							AssociationAST assoc = ((AssociationAST) lhsHead);
							assoc = assoc.copy();
							assoc.appendRule(F.Rule(((IAST) leftHandSide).arg1(), rightHandSide));
							symbol.assign(assoc);
							return rightHandSide;
						}
					} catch (RuntimeException npe) {
						engine.printMessage("Set: " + npe.getMessage());
						return F.NIL;
					}
				}
			}
			return F.NIL;
		}
	}

	private static class KeyExistsQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormPrepend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			if (ast.isAST2()) {
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				if (arg1 instanceof AssociationAST) {
					return ((AssociationAST) arg1).isKey(arg2) ? F.True : F.False;
				}
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRule() || rule.isRuleDelayed()) {
							if (arg2.equals(rule.first())) {
								return F.True;
							}
						}
					}
				}
				return F.False;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class Keys extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
			if (arg1 instanceof AssociationAST) {
				IASTMutable list = ((AssociationAST) arg1).keys();
				return mapHeadIfPresent(list, head);
			}
			if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRule() || rule.isRuleDelayed()) {
							list.append(rule.first());
						} else if (rule.isAST(F.List, 1)) {
							list.append(rule);
						}
					}
					return mapHeadIfPresent(list, head);
				}

				// thread over Lists in first argument
				return ((IAST) arg1).mapThread(ast.setAtCopy(1, F.Null), 1);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class Values extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
			if (arg1 instanceof AssociationAST) {
				IASTMutable list = ((AssociationAST) arg1).values();
				return mapHeadIfPresent(list, head);
			}
			if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRule() || rule.isRuleDelayed()) {
							list.append(rule.second());
						} else if (rule.isAST(F.List, 1)) {
							list.append(rule);
						}
					}
					return mapHeadIfPresent(list, head);
				}

				// thread over Lists in first argument
				return ((IAST) arg1).mapThread(ast.setAtCopy(1, F.Null), 1);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * If <code>head.isPresent()</code> map the <code>head</code> on each argument of list. Otherwise return
	 * <code>list</code>.
	 * 
	 * @param list
	 * @param head
	 * @return
	 */
	private static IExpr mapHeadIfPresent(IASTMutable list, final IExpr head) {
		if (head.isPresent()) {
			return list.mapThread(x -> F.unaryAST1(head, x));
		}
		return list;
	}

	public static void initialize() {
		Initializer.init();
	}

	private AssociationFunctions() {

	}

}
