package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.ASTAssociation;
import org.matheclipse.core.expression.ASTDataset;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
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
			F.Association.setEvaluator(new Association());
			F.Counts.setEvaluator(new Counts());
			F.KeyExistsQ.setEvaluator(new KeyExistsQ());
			F.Keys.setEvaluator(new Keys());
			F.KeySort.setEvaluator(new KeySort());
			F.Lookup.setEvaluator(new Lookup());
			F.Structure.setEvaluator(new Structure());
			F.Summary.setEvaluator(new Summary());
			F.Values.setEvaluator(new Values());
		}
	}

	private static class Association extends AbstractEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAssociation()) {
				return F.NIL;
			}
			if (ast.isAST1()) {
				try {

					IExpr arg1 = engine.evaluate(ast.arg1());
					if (arg1.isListOfRules(true)) {
						return F.assoc((IAST) arg1);
					} else if (arg1.isAST(F.List, 2)) {
						arg1 = arg1.first();
						if (arg1.isListOfRules(true)) {
							return F.assoc((IAST) arg1);
						}
					}

				} catch (ValidateException ve) {
					if (FEConfig.SHOW_STACKTRACE) {
						ve.printStackTrace();
					}
					return engine.printMessage(ast.topHead(), ve);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
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
						// Symbol `1` is Protected.
						return IOFunctions.printMessage(F.Set, "wrsym", F.List(symbol), EvalEngine.get());
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
						return engine.printMessage(F.Set, ve);
//					} catch (RuntimeException rex) {
//						if (FEConfig.SHOW_STACKTRACE) {
//							rex.printStackTrace();
//						}
//						return engine.printMessage(F.Set, rex);  
					}
				}
			}
			return F.NIL;
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
			}
			if (arg1.isDataSet()) {
				ASTDataset dataset = (ASTDataset) arg1;
				return dataset.columnNames();
			}
			if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRule() || rule.isRuleDelayed()) {
							list.append(rule.first());
						} else if (rule.isEmptyList()) {
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

	private static class Lookup extends AbstractEvaluator {

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
							if (rule.isRule() || rule.isRuleDelayed()) {
								if (rule.first().equals(key)) {
									return rule.second();
								}
							}
						}
						if (ast.isAST3()) {
							return ast.arg3();
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
					return ((IAssociation) arg1).getValue(key, () -> arg3);
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
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Structure extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isDataSet()) {
				ASTDataset dataset = (ASTDataset) arg1;
				return dataset.structure();
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class Summary extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isDataSet()) {
				ASTDataset dataset = (ASTDataset) arg1;
				return dataset.summary();
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
			}
			if (arg1.isList()) {
				if (arg1.isListOfRules(true)) {
					IAST listOfRules = (IAST) arg1;
					IASTAppendable list = F.ast(F.List, listOfRules.argSize(), false);
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr rule = listOfRules.get(i);
						if (rule.isRule() || rule.isRuleDelayed()) {
							list.append(rule.second());
						} else if (rule.isEmptyList()) {
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
