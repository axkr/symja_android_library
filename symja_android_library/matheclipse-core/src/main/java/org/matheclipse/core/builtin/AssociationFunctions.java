package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.expression.ASTAssociation;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

public class AssociationFunctions {
	private final static class MutableInt {
		int value;

		public MutableInt(int value) {
			this.value = value;
		}

		public MutableInt increment() {
			value++;
			return this;
		}

		public int value() {
			return value;
		}
	}

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			S.Association.setEvaluator(new Association());
			S.AssociationThread.setEvaluator(new AssociationThread());
			S.Counts.setEvaluator(new Counts());
			S.KeyExistsQ.setEvaluator(new KeyExistsQ());
			S.Keys.setEvaluator(new Keys());
			S.KeySort.setEvaluator(new KeySort());
			S.KeyTake.setEvaluator(new KeyTake());
			S.LetterCounts.setEvaluator(new LetterCounts());
			S.Lookup.setEvaluator(new Lookup());
			S.Structure.setEvaluator(new Structure());
			S.Summary.setEvaluator(new Summary());
			S.Values.setEvaluator(new Values());
		}
	}

	private static class Association extends AbstractEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAssociation()) {
				return F.NIL;
			}
			if (ast.isAST0()) {
				return F.assoc(F.List());
			} else if (ast.size() > 1) {
				try {

					// IExpr arg1 = engine.evaluate(ast.arg1());
					IAssociation assoc = F.assoc(F.CEmptyList);
					for (int i = 1; i < ast.size(); i++) {
						if (ast.get(i).isAST()) {
							assoc.appendRules((IAST) ast.get(i));
						} else {
							throw new ArgumentTypeException(
									"rule expression expected instead of " + ast.get(i).toString());
						}
					}
					return assoc;
				} catch (ValidateException ve) {
					if (FEConfig.SHOW_STACKTRACE) {
						ve.printStackTrace();
					}
					// print no message
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
		}

		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, IBuiltInSymbol builtinSymbol,
				EvalEngine engine) {
			if (leftHandSide.head().isSymbol()) {
				ISymbol symbol = (ISymbol) leftHandSide.head();

				IExpr temp = symbol.assignedValue();
				if (temp == null) {
					// `1` is not a variable with a value, so its value cannot be changed.
					return IOFunctions.printMessage(builtinSymbol, "rvalue", F.List(symbol), engine);
				} else {
					if (symbol.isProtected()) {
						// Symbol `1` is Protected.
						return IOFunctions.printMessage(builtinSymbol, "wrsym", F.List(symbol), EvalEngine.get());
					}
					try {
						IExpr lhsHead = engine.evaluate(symbol);
						if (lhsHead.isAssociation()) {
							IAssociation assoc = ((IAssociation) lhsHead);
							assoc = assoc.copy();
							assoc.appendRule(F.Rule(((IAST) leftHandSide).arg1(), rightHandSide));
							symbol.assign(assoc);
							return rightHandSide;
						}
					} catch (ValidateException ve) {
						return engine.printMessage(builtinSymbol, ve);
						// } catch (RuntimeException rex) {
						// if (FEConfig.SHOW_STACKTRACE) {
						// rex.printStackTrace();
						// }
						// return engine.printMessage(F.Set, rex);
					}
				}
			}
			return F.NIL;
		}
	}

	private static class AssociationThread extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();
				return associationThread(F.Rule, arg1, arg2);
			}
			if (arg1.isRuleAST()) {
				IAST rule = (IAST) arg1;

				return associationThread((ISymbol) rule.head(), rule.arg1(), rule.arg2());
			}
			return F.NIL;
		}

		private static IExpr associationThread(ISymbol symbol, IExpr arg1, IExpr arg2) {
			if (arg1.isList() && arg2.isList()) {
				if (arg1.size() == arg2.size()) {
					IAST list1 = (IAST) arg1;
					IAST list2 = (IAST) arg2;
					IASTAppendable listOfRules = F.ListAlloc(arg1.size());
					for (int i = 1; i < list1.size(); i++) {
						listOfRules.append(F.binaryAST2(symbol, list1.get(i), list2.get(i)));
					}
					return F.assoc(listOfRules);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class Counts extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isList()) {
				IAST list = (IAST) arg1;
				try {
					HashMap<IExpr, MutableInt> map = new HashMap<IExpr, MutableInt>();
					for (int i = 1; i < list.size(); i++) {
						IExpr key = list.get(i);
						map.compute(key, (k, v) -> (v == null) ? new MutableInt(1) : v.increment());
					}
					IAssociation assoc = new ASTAssociation(map.size(), false);
					for (Map.Entry<IExpr, AssociationFunctions.MutableInt> elem : map.entrySet()) {
						assoc.appendRule(F.Rule(elem.getKey(), F.ZZ(elem.getValue().value())));
					}
					return assoc;
				} catch (ValidateException ve) {
					return engine.printMessage(ast.topHead(), ve);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
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
				if (arg1.isAssociation()) {
					return ((IAssociation) arg1).isKey(arg2) ? F.True : F.False;
				}
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRuleAST()) {
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
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class Keys extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
			if (arg1.isAssociation()) {
				IASTMutable list = ((IAssociation) arg1).keys();
				return mapHeadIfPresent(list, head);
			} else if (arg1.isDataSet()) {
				return ((IASTDataset) arg1).columnNames();
			} else if (arg1.isRuleAST()) {
				if (head.isPresent()) {
					return F.unaryAST1(head, arg1.first());
				}
				return arg1.first();
			} else if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRuleAST()) {
							list.append(rule.first());
						} else if (rule.isEmptyList()) {
							list.append(rule);
						} else {
							// The argument `1` is not a vaild Association or list of rules.
							throw new ArgumentTypeException(IOFunctions.getMessage("invrl", F.List(rule)));
						}
					}
					return mapHeadIfPresent(list, head);
				}

				// thread over Lists in first argument
				return ((IAST) arg1).mapThread(ast.setAtCopy(1, F.Slot1), 1);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class KeySort extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.isAssociation()) {
					if (ast.isAST2()) {
						return ((IAssociation) arg1).keySort(new Predicates.IsBinaryFalse(ast.arg2()));
					}
					return ((IAssociation) arg1).keySort();
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class LetterCounts extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isString()) {
				String str = ((IStringX) arg1).toString();
				try {
					HashMap<Character, MutableInt> map = new HashMap<Character, MutableInt>();
					for (int i = 0; i < str.length(); i++) {
						map.compute(str.charAt(i), //
								(k, v) -> (v == null) ? new MutableInt(1) : v.increment());
					}
					IAssociation assoc = new ASTAssociation(map.size(), false);
					for (Map.Entry<Character, AssociationFunctions.MutableInt> elem : map.entrySet()) {
						assoc.appendRule(F.Rule(F.$str(elem.getKey()), F.ZZ(elem.getValue().value())));
					}
					return assoc;
				} catch (ValidateException ve) {
					return engine.printMessage(ast.topHead(), ve);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class Lookup extends AbstractEvaluator implements ICoreFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormAppend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isList()) {
				if (ast.size() > 2) {
					if (arg1.isListOfRules(true)) {
						IExpr key = engine.evaluate(ast.arg2());
						IAST listOfRules = (IAST) arg1;
						for (int i = 1; i < listOfRules.size(); i++) {
							IExpr rule = listOfRules.get(i);
							if (rule.isRuleAST()) {
								if (rule.first().equals(key)) {
									return rule.second();
								}
							}
						}
						if (ast.isAST3()) {
							return engine.evaluate(ast.arg3());
						}
						return F.Missing(F.stringx("KeyAbsent"), key);
					}
				}
				return ((IAST) arg1).mapThread(ast, 1);
			} else if (arg1.isAssociation()) {
				if (ast.isAST2()) {
					IExpr key = engine.evaluate(ast.arg2());
					if (key.isList()) {
						return ((IAST) key).mapThread(ast, 2);
					}
					return ((IAssociation) arg1).getValue(key);
				}
				if (ast.isAST3()) {
					IExpr key = engine.evaluate(ast.arg2());
					if (key.isList()) {
						return ((IAST) key).mapThread(ast, 2);
					}
					final IExpr arg3 = ast.arg3();
					return ((IAssociation) arg1).getValue(key, () -> engine.evaluate(arg3));
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
		}
	}

	private static class Structure extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isDataSet()) {
				return ((IASTDataset) arg1).structure();
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}
	}

	private final static class KeyTake extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormAppend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			IAST evaledAST = (IAST) engine.evalAttributes(F.KeyTake, ast);
			if (!evaledAST.isPresent()) {
				evaledAST = ast;
			}
			try {
				if (evaledAST.arg1().isListOfRulesOrAssociation(true) || evaledAST.arg1().isListOfLists()) {
					final IAST arg1 = (IAST) evaledAST.arg1();
					if (arg1.forAll(x -> x.isListOfRulesOrAssociation(true))) {
						return arg1.mapThread(ast, 1);
					}
					IExpr arg2 = evaledAST.arg2();
					if (!arg2.isList()) {
						arg2 = F.List(arg2);
					}
					return keyTake(arg1, (IAST) arg2);
				} else {
					return engine.printMessage("KeyTake: Association or list of rules expected at position 1.");
				}
			} catch (final ValidateException ve) {
				return engine.printMessage(ast.topHead(), ve);
			} catch (final RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}

		private static IAST keyTake(final IAST expr, final IAST list) {
			final int size = list.size();
			// final IASTAppendable assoc = F.assoc(expr);
			final IASTAppendable resultAssoc =  F.assoc(10 > size ? size : 10);
			for (int i = 1; i < size; i++) {
				IExpr rule = expr.getRule(list.get(i));
				if (rule.isPresent()) {
					resultAssoc.appendRule(rule);
				}
			}
			return resultAssoc;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDREST);
		}
	}

	private static class Summary extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isDataSet()) {
				return ((IASTDataset) arg1).summary();
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class Values extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			final IExpr head = ast.isAST2() ? ast.arg2() : F.NIL;
			if (arg1.isAssociation()) {
				IASTMutable list = ((IAssociation) arg1).values();
				return mapHeadIfPresent(list, head);
			} else if (arg1.isRuleAST()) {
				if (head.isPresent()) {
					return F.unaryAST1(head, arg1.second());
				}
				return arg1.second();
			} else if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRuleAST()) {
							list.append(rule.second());
						} else if (rule.isEmptyList()) {
							list.append(rule);
						}
					}
					return mapHeadIfPresent(list, head);
				}

				// thread over Lists in first argument
				return ((IAST) arg1).mapThread(ast.setAtCopy(1, F.Slot1), 1);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
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
