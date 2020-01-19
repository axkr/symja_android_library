package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.AssociationAST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

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
			F.Values.setEvaluator(new Values());
		}
	}

	private static class Association extends AbstractEvaluator {

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
						if (rule.isRule()) {
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
						if (rule.isRule()) {
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
