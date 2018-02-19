package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.boole.QuineMcCluskyFormula;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;
import org.matheclipse.core.visit.VisitorExpr;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Equivalent;
import static org.matheclipse.core.expression.F.Implies;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Nand;
import static org.matheclipse.core.expression.F.Nor;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Xor;

public final class BooleanFunctions {
	public final static Equal CONST_EQUAL = new Equal();
	public final static Greater CONST_GREATER = new Greater();
	public final static Less CONST_LESS = new Less();
	public final static GreaterEqual CONST_GREATER_EQUAL = new GreaterEqual();
	public final static LessEqual CONST_LESS_EQUAL = new LessEqual();

	static {
		F.AllTrue.setEvaluator(new AllTrue());
		F.And.setEvaluator(new And());
		F.AnyTrue.setEvaluator(new AnyTrue());
		F.Boole.setEvaluator(new Boole());
		F.BooleanConvert.setEvaluator(new BooleanConvert());
		F.BooleanMinimize.setEvaluator(new BooleanMinimize());
		F.BooleanTable.setEvaluator(new BooleanTable());
		F.BooleanVariables.setEvaluator(new BooleanVariables());
		F.Equal.setEvaluator(CONST_EQUAL);
		F.Equivalent.setEvaluator(new Equivalent());
		F.Greater.setEvaluator(CONST_GREATER);
		F.GreaterEqual.setEvaluator(new GreaterEqual());
		F.Implies.setEvaluator(new Implies());
		F.Less.setEvaluator(CONST_LESS);
		F.LessEqual.setEvaluator(new LessEqual());
		F.Max.setEvaluator(new Max());
		F.Min.setEvaluator(new Min());
		F.Nand.setEvaluator(new Nand());
		F.Negative.setEvaluator(new Negative());
		F.NoneTrue.setEvaluator(new NoneTrue());
		F.NonNegative.setEvaluator(new NonNegative());
		F.NonPositive.setEvaluator(new NonPositive());
		F.Nor.setEvaluator(new Nor());
		F.Not.setEvaluator(new Not());
		F.Or.setEvaluator(new Or());
		F.Positive.setEvaluator(new Positive());
		F.SameQ.setEvaluator(new SameQ());
		F.SatisfiableQ.setEvaluator(new SatisfiableQ());
		F.TautologyQ.setEvaluator(new TautologyQ());
		F.TrueQ.setEvaluator(new TrueQ());
		F.Unequal.setEvaluator(new Unequal());
		F.UnsameQ.setEvaluator(new UnsameQ());
		F.Xor.setEvaluator(new Xor());
	}

	private static class AllTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return allTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If all expressions evaluates to <code>true</code> for a given unary predicate function return
		 * <code>True</code>, if any expression evaluates to <code>false</code> return <code>False</code>, else return
		 * an <code>And(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalAnd = F.And();

			if (!list.forAll(new Predicate<IExpr>() {
                @Override
                public boolean test(IExpr x) {
                    IExpr temp = engine.evaluate(F.unary(head, x));
                    if (temp.isTrue()) {
                        return true;
                    } else if (temp.isFalse()) {
                        return false;
                    }
                    logicalAnd.append(temp);
                    return true;
                }
            }, 1)) {
				return F.False;
			}

			if (logicalAnd.size() > 1) {
				return logicalAnd;
			}
			return F.True;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * 
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Logical_conjunction">Logical conjunction</a>
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/And">And </a>
	 * </p>
	 */
	private static class And extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.True;
			}

			boolean evaled = false;

			int index = 1;
			IExpr temp;
			IExpr sym;

			IAST flattenedAST = EvalAttributes.flatten(ast);
			if (flattenedAST.isPresent()) {
				evaled = true;
			} else {
				flattenedAST = ast;
			}

			IASTAppendable result = flattenedAST.copyAppendable();
			int[] symbols = new int[flattenedAST.size()];
			int[] notSymbols = new int[flattenedAST.size()];
			for (int i = 1; i < flattenedAST.size(); i++) {
				temp = flattenedAST.get(i);
				if (temp.isFalse()) {
					return F.False;
				}
				if (temp.isTrue()) {
					result.remove(index);
					evaled = true;
					continue;
				}

				temp = engine.evaluateNull(temp);
				if (temp.isPresent()) {
					if (temp.isFalse()) {
						return F.False;
					}
					if (temp.isTrue()) {
						result.remove(index);
						evaled = true;
						continue;
					}
					result.set(index, temp);
					evaled = true;
				} else {
					temp = flattenedAST.get(i);
				}

				if (temp.isSymbol()) {
					symbols[i] = flattenedAST.get(i).hashCode();
				} else if (temp.isNot()) {
					sym = ((IAST) temp).getAt(1);
					if (sym.isSymbol()) {
						notSymbols[i] = sym.hashCode();
					}
				}
				index++;
			}
			for (int i = 1; i < symbols.length; i++) {
				if (symbols[i] != 0) {
					for (int j = 1; j < notSymbols.length; j++) {
						if (i != j && symbols[i] == notSymbols[j] && (result.equalsAt(i, result.get(j).getAt(1)))) {
							// And[a, Not[a]] => True
							return F.False;
						}
					}
				}
			}
			if (result.isAST1()) {
				return result.arg1();
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.True;
				}

				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	private static class AnyTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return anyTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If any expression evaluates to <code>true</code> for a given unary predicate function return
		 * <code>True</code>, if all are <code>false</code> return <code>False</code>, else return an
		 * <code>Or(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr anyTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalOr = F.Or();
			if (list.exists(new Predicate<IExpr>() {
				@Override
				public boolean test(IExpr x) {
					return anyTrueArgument(x, head, logicalOr, engine);
				}
			}, 1)) {
				return F.True;
			}
			return logicalOr.isAST0() ? F.False : logicalOr;
		}

		private static boolean anyTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector, EvalEngine engine) {
			IExpr temp = engine.evaluate(F.unary(head, x));
			if (temp.isTrue()) {
				return true;
			} else if (!temp.isFalse()) {
				resultCollector.append(temp);
			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Predicate function
	 * 
	 * Returns <code>1</code> if the 1st argument evaluates to <code>True</code> ; returns <code>0</code> if the 1st
	 * argument evaluates to <code>False</code>; and <code>null</code> otherwise.
	 */
	private static class Boole extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isSymbol()) {
				if (ast.arg1().isTrue()) {
					return F.C1;
				}
				if (ast.arg1().isFalse()) {
					return F.C0;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private static class BooleanConvert extends AbstractFunctionEvaluator {

		static class BooleanConvertVisitor extends VisitorExpr {
			public BooleanConvertVisitor() {
				super();
			}

			@Override
			protected IExpr visitAST(IAST ast) {
				if (ast.isNot()) {
					if (ast.arg1().isAST()) {
						IAST notArg1 = (IAST) ast.arg1();
						if (notArg1.isASTSizeGE(Nand, 1)) {
							return notArg1.apply(And);
						} else if (notArg1.isASTSizeGE(Nor, 1)) {
							return notArg1.apply(Or);
						} else if (notArg1.isASTSizeGE(And, 1)) {
							return convertNand(notArg1);
						} else if (notArg1.isASTSizeGE(Or, 1)) {
							return convertNor(notArg1);
						}

					}
				} else if (ast.isASTSizeGE(Equivalent, 1)) {
					return convertEquivalent(ast);
				} else if (ast.isAST(Implies, 3)) {
					return convertImplies(ast);
				} else if (ast.isASTSizeGE(Nand, 1)) {
					return convertNand(ast);
				} else if (ast.isASTSizeGE(Nor, 1)) {
					return convertNor(ast);
				} else if (ast.isASTSizeGE(Xor, 3)) {
					return convertXor(ast);
				}
				return super.visitAST(ast);
			}

			public IAST convertEquivalent(IAST ast) {
				IAST term1 = ast.apply(F.And);
				IAST term2 = term1.mapThread(F.Not(null), 1);
				return F.Or(term1, term2);
			}

			public IAST convertImplies(IAST ast) {
				return F.Or(F.Not(ast.arg1()), ast.arg2());
			}

			public IAST convertNand(IAST ast) {
				return Lambda.forEachAppend(ast, F.Or(), new Function<IExpr, IExpr>() {
					@Override
					public IExpr apply(IExpr x) {
						return F.Not(x);
					}
				});
			}

			public IAST convertNor(IAST ast) {
				return Lambda.forEachAppend(ast, F.And(), new Function<IExpr, IExpr>() {
					@Override
					public IExpr apply(IExpr x) {
						return F.Not(x);
					}
				});
			}

			public IAST convertXor(IAST ast) {
				IExpr temp = ast.arg2();
				if (ast.size() > 3) {
					IASTAppendable clone = ast.copyAppendable();
					clone.remove(1);
					temp = convertXor(clone);
				}
				return F.Or(F.And(ast.arg1(), F.Not(temp)), F.And(F.Not(ast.arg1()), temp));
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			BooleanConvertVisitor bcVisitor = new BooleanConvertVisitor();
			IExpr result = ast.arg1().accept(bcVisitor);
			return result.isPresent() ? result : ast.arg1();
		}
	}

	/**
	 * Minimize a boolean function with the <a href="http://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">
	 * Quine McCluskey algorithm</a>.
	 */
	private static class BooleanMinimize extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isASTSizeGE(F.Or, 3)) {
				try {
					QuineMcCluskyFormula f = QuineMcCluskyFormula.read((IAST) ast.arg1());
					f.reduceToPrimeImplicants();
					f.reducePrimeImplicantsToSubset();
					return f.toExpr();
				} catch (BooleanFunctionConversionException bfc) {
					if (Config.DEBUG) {
						bfc.printStackTrace();
					}
					return F.NIL;
				}
			}

			return F.NIL;
		}
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Truth_table">Wikipedia: Truth table</a>
	 * 
	 */
	private static class BooleanTable extends AbstractFunctionEvaluator {

		private static class BooleanTableParameter {
			public IAST variables;
			public IASTAppendable resultList;
			public EvalEngine engine;

			public BooleanTableParameter(IAST variables, EvalEngine engine) {
				this.variables = variables;
				this.resultList = F.ListAlloc(variables.size());
				this.engine = engine;
			}

			public IAST booleanTable(IExpr expr, int position) {
				if (variables.size() <= position) {
					resultList.append(engine.evalTrue(expr) ? F.True : F.False);
					return resultList;
				}
				IExpr sym = variables.get(position);
				if (sym.isSymbol()) {
					try {
						((ISymbol) sym).pushLocalVariable(F.True);
						booleanTable(expr, position + 1);
					} finally {
						((ISymbol) sym).popLocalVariable();
					}
					try {
						((ISymbol) sym).pushLocalVariable(F.False);
						booleanTable(expr, position + 1);
					} finally {
						((ISymbol) sym).popLocalVariable();
					}
				}
				return resultList;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IAST variables;
			if (ast.isAST2()) {
				if (ast.arg2().isList()) {
					variables = (IAST) ast.arg2();
				} else {
					variables = List(ast.arg2());
				}
			} else {
				VariablesSet vSet = new VariablesSet(ast.arg1());
				variables = vSet.getVarList();
			}

			BooleanTableParameter btp = new BooleanTableParameter(variables, engine);
			return btp.booleanTable(ast.arg1(), 1);
		}

	}

	/**
	 * Determine the variable symbols of an expression
	 */
	private static class BooleanVariables extends AbstractFunctionEvaluator {

		/**
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			VariablesSet eVar = new VariablesSet();
			eVar.addBooleanVarList(ast.arg1());
			return eVar.getVarList();

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <code>==</code> operator implementation.
	 * 
	 */
	public static class Equal extends AbstractFunctionEvaluator implements ITernaryComparator {

		/**
		 * Create the result for a <code>simplifyCompare()</code> step.
		 * 
		 * @param lhsAST
		 * @param rhs
		 * @param originalHead
		 * @return
		 */
		private IExpr createComparatorResult(IAST lhsAST, IExpr rhs, ISymbol originalHead) {
			IAST lhsClone = lhsAST.removeAtClone(1);
			return F.binary(originalHead, lhsClone, rhs);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x > 6</code> wll be simplified to
		 * <code>x> 2</code>.
		 * 
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * @param originalHead
		 *            symbol for which the simplification was started
		 * @return the simplified comparator expression or <code>null</code> if no simplification was found
		 */
		protected IExpr simplifyCompare(IExpr a1, IExpr a2, ISymbol originalHead) {
			IExpr lhs, rhs;
			if (a2.isNumber()) {
				lhs = a1;
				rhs = a2;
			} else if (a1.isNumber()) {
				lhs = a2;
				rhs = a1;
			} else {
				return F.NIL;
			}
			if (lhs.isAST()) {
				IAST lhsAST = (IAST) lhs;
				if (lhsAST.isTimes()) {
					if (lhsAST.arg1().isNumber()) {
						INumber sn = (INumber) lhsAST.arg1();
						rhs = F.eval(F.Divide(rhs, sn));
						return createComparatorResult(lhsAST, rhs, originalHead);
					}
				} else if (lhsAST.isPlus() && lhsAST.arg1().isNumber()) {
					INumber sn = (INumber) lhsAST.arg1();
					rhs = F.eval(F.Subtract(rhs, sn));
					return createComparatorResult(lhsAST, rhs, originalHead);
				}
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 2) {
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
				if (ast.isAST2()) {
					return equalNull(ast.arg1(), ast.arg2(), engine);
				}
				boolean evaled = false;
				IASTAppendable result = ast.copyAppendable();
				int i = 2;
				IExpr arg1 = F.expandAll(result.get(1), true, true);
				while (i < result.size()) {
					IExpr arg2 = F.expandAll(result.get(i), true, true);
					b = prepareCompare(arg1, arg2, engine);
					if (b == IExpr.COMPARE_TERNARY.FALSE) {
						return F.False;
					}
					if (b == IExpr.COMPARE_TERNARY.TRUE) {
						evaled = true;
						result.remove(i - 1);
					} else {
						result.set(i - 1, arg1);
						i++;
						arg1 = arg2;
					}

				}
				if (evaled) {
					if (result.isAST1()) {
						return F.True;
					}
					return result;
				}
				return F.NIL;
			}
			return F.True;
		}

		/**
		 * 
		 * @param arg1
		 * @param arg2
		 * @param engine
		 * @return
		 */
		public IExpr.COMPARE_TERNARY prepareCompare(final IExpr arg1, final IExpr arg2, EvalEngine engine) {
			if (arg1.isIndeterminate() || arg2.isIndeterminate()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}
			if (arg1.isList() && arg2.isList()) {
				IAST list1 = (IAST) arg1;
				IAST list2 = (IAST) arg2;
				int size1 = list1.size();
				if (size1 != list2.size()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.TRUE;
				for (int i = 1; i < size1; i++) {
					b = prepareCompare(list1.get(i), list2.get(i), engine);
					if (b == IExpr.COMPARE_TERNARY.FALSE) {
						return IExpr.COMPARE_TERNARY.FALSE;
					}
					if (b == IExpr.COMPARE_TERNARY.TRUE) {
					} else {
						return IExpr.COMPARE_TERNARY.UNDEFINED;
					}
				}
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			IExpr a0 = arg1;
			IExpr a1 = arg2;
			if (!a0.isSignedNumber() && a0.isNumericFunction()) {
				a0 = engine.evalN(a0);
			} else if (a1.isNumeric() && a0.isRational()) {
				a0 = engine.evalN(a0);
			}
			if (!a1.isSignedNumber() && a1.isNumericFunction()) {
				a1 = engine.evalN(a1);
			} else if (a0.isNumeric() && a1.isRational()) {
				a1 = engine.evalN(a1);
			}

			return compareTernary(a0, a1);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr o0, final IExpr o1) {
			if (o0.isSame(o1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}

			if (o0.isTrue()) {
				if (o1.isTrue()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (o1.isFalse()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			} else if (o0.isFalse()) {
				if (o1.isTrue()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (o1.isFalse()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
			}
			if (o0.isConstant() && o1.isConstant()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			if (o0.isNumber() && o1.isNumber()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			if ((o0 instanceof StringX) && (o1 instanceof StringX)) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			IExpr difference = F.eval(F.Subtract(o0, o1));
			if (difference.isNumber()) {
				if (difference.isZero()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
				return IExpr.COMPARE_TERNARY.FALSE;
			}
			if (difference.isConstant()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	private final static class Equivalent extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0() || ast.isAST1()) {
				return F.True;
			}
			IASTAppendable result = ast.copyHead();
			IExpr last = F.NIL;
			IExpr boole = F.NIL;
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp.isFalse()) {
					if (!boole.isPresent()) {
						boole = F.False;
						evaled = true;
					} else if (boole.isTrue()) {
						return F.False;
					} else {
						evaled = true;
					}
				} else if (temp.isTrue()) {
					if (!boole.isPresent()) {
						boole = F.True;
						evaled = true;
					} else if (boole.isFalse()) {
						return F.False;
					} else {
						evaled = true;
					}
				} else {
					if (!last.equals(temp)) {
						result.append(temp);
					} else {
						evaled = true;
					}

					last = temp;
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					if (boole.isPresent()) {
						return F.True;
					}
				} else if (result.isAST1() && !boole.isPresent()) {
					return F.True;
				}
				if (boole.isPresent()) {
					result = result.apply(F.And);
					if (boole.isTrue()) {
						return result;
					} else {
						return result.mapThread(F.Not(null), 1);
					}
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS);
		}
	}

	/**
	 * <code>&gt;</code> operator implementation.
	 * 
	 */
	public static class Greater extends AbstractFunctionEvaluator implements ITernaryComparator {
		public final static Greater CONST = new Greater();

		/**
		 * Check assumptions for the comparison operator. Will be overridden in
		 * <code>GreaterEqual, Less, LessEqual</code>.
		 * 
		 * @param arg1
		 *            the left-hand-side of the comparison
		 * @param arg2
		 *            the right-hand-side of the comparison
		 * @return
		 */
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 > "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
			} else if (arg2.isZero()) {
				// arg1 > 0
				if (arg1.isPositiveResult()) {
					return F.True;
				}
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			} else {
				// arg1 > "positive number" > 0
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			}
			return F.NIL;
		}

		/**
		 * Compare two intervals if they are greater.
		 * 
		 * <ul>
		 * <li>Return TRUE if the comparison is <code>true</code></li>
		 * <li>Return FALSE if the comparison is <code>false</code></li>
		 * <li>Return UNDEFINED if the comparison is undetermined (i.e. could not be evaluated)</li>
		 * </ul>
		 * 
		 * @param lower0
		 *            the lower bound of the first interval
		 * @param upper0
		 *            the upper bound of the first interval
		 * @param lower1
		 *            the lower bound of the second interval
		 * @param upper1
		 *            the upper bound of the second interval
		 * @return
		 */
		private static IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(final IExpr lower0, final IExpr upper0,
				final IExpr lower1, final IExpr upper1) {
			if (lower0.greaterThan(upper1).isTrue()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			} else {
				if (upper0.lessThan(lower1).isTrue()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			}
			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// don't compare strings
			if (a0.isSignedNumber()) {
				if (a1.isSignedNumber()) {
					return ((ISignedNumber) a0).isGreaterThan((ISignedNumber) a1) ? IExpr.COMPARE_TERNARY.TRUE
							: IExpr.COMPARE_TERNARY.FALSE;
				} else if (a1.isInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (a1.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (a1.isInterval1()) {
					return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
				}
			} else if (a1.isSignedNumber()) {
				if (a0.isInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (a0.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (a0.isInterval1()) {
					return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
				}
			} else if (a0.isInfinity()) {
				if (a1.isRealResult() || a1.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
			} else if (a0.isNegativeInfinity()) {
				if (a1.isRealResult() || a1.isInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			} else if (a1.isInfinity() && a0.isRealResult()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			} else if (a1.isNegativeInfinity() && a0.isRealResult()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			} else if (a0.isInterval1() && a1.isInterval1()) {
				return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
			}

			if (a0.equals(a1) && !a0.isList()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		/**
		 * Create the result for a <code>simplifyCompare()</code> step.
		 * 
		 * @param lhs
		 *            left-hand-side of the comparator expression
		 * @param rhs
		 *            right-hand-side of the comparator expression
		 * @param useOppositeHeader
		 *            use the opposite header to create the result
		 * @param originalHead
		 *            symbol of the comparator operator for which the simplification was started
		 * @param oppositeHead
		 *            opposite of the symbol of the comparator operator for which the comparison was started
		 * @return
		 */
		private IAST createComparatorResult(IExpr lhs, IExpr rhs, boolean useOppositeHeader, ISymbol originalHead,
				ISymbol oppositeHead) {
			return F.binary(useOppositeHeader ? oppositeHead : originalHead, lhs, rhs);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkRange(ast, 3);
			if (ast.size() <= 2) {
				return F.True;
			}
			if (ast.isAST2()) {
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				IExpr result = simplifyCompare(arg1, arg2);
				if (result.isPresent()) {
					// the result may return an AST with an "opposite header"
					// (i.e.
					// Less instead of Greater)
					return result;
				}
				if (arg2.isSignedNumber()) {
					// this part is used in other comparator operations like
					// Less,
					// GreaterEqual,...
					IExpr temp = checkAssumptions(arg1, arg2);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
			IExpr.COMPARE_TERNARY b;
			boolean evaled = false;
			IASTAppendable result = ast.copyAppendable();
			IExpr.COMPARE_TERNARY[] cResult = new IExpr.COMPARE_TERNARY[ast.size()];
			cResult[0] = IExpr.COMPARE_TERNARY.TRUE;
			for (int i = 1; i < ast.size() - 1; i++) {
				b = prepareCompare(result.get(i), result.get(i + 1), engine);
				if (b == IExpr.COMPARE_TERNARY.FALSE) {
					return F.False;
				}
				if (b == IExpr.COMPARE_TERNARY.TRUE) {
					evaled = true;
				}
				cResult[i] = b;
			}
			cResult[ast.size() - 1] = IExpr.COMPARE_TERNARY.TRUE;
			if (!evaled) {
				// expression doesn't change
				return F.NIL;
			}
			int i = 2;
			evaled = false;
			for (int j = 1; j < ast.size(); j++) {
				if (cResult[j - 1] == IExpr.COMPARE_TERNARY.TRUE && cResult[j] == IExpr.COMPARE_TERNARY.TRUE) {
					evaled = true;
					result.remove(i - 1);
				} else {
					i++;
				}
			}

			if (evaled) {
				if (result.size() <= 2) {
					return F.True;
				}
				return result;
			}

			return F.NIL;
		}

		private IExpr.COMPARE_TERNARY prepareCompare(IExpr a0, IExpr a1, EvalEngine engine) {
			if (!a0.isSignedNumber() && a0.isNumericFunction()) {
				a0 = engine.evalN(a0);
			} else if (a1.isNumeric() && a0.isRational()) {
				a0 = engine.evalN(a0);
			}
			if (!a1.isSignedNumber() && a1.isNumericFunction()) {
				a1 = engine.evalN(a1);
			} else if (a0.isNumeric() && a1.isRational()) {
				a1 = engine.evalN(a1);
			}

			return compareTernary(a0, a1);
		}

		public IExpr.COMPARE_TERNARY prepareCompare(final IExpr o0, final IExpr o1) {
			return prepareCompare(o0, o1, EvalEngine.get());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> will be simplified to
		 * <code>x &gt; 2</code>.
		 * 
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * @return the simplified comparator expression or <code>null</code> if no simplification was found
		 */
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.Greater, F.Less, true);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> wll be simplified to
		 * <code>x &gt; 2</code>.
		 * 
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * @param originalHead
		 *            symbol of the comparator operator for which the simplification was started
		 * @param oppositeHead
		 *            opposite of the symbol of the comparator operator for which the comparison was started
		 * @param setTrue
		 *            if <code>true</code> return F.True otherwise F.False
		 * @return the simplified comparator expression or <code>F.NIL</code> if no simplification was found
		 */
		final protected IExpr simplifyCompare(IExpr a1, IExpr a2, ISymbol originalHead, ISymbol oppositeHead,
				boolean setTrue) {
			IExpr lhs;
			IExpr rhs;
			boolean useOppositeHeader = false;
			if (a2.isNumericFunction()) {
				lhs = a1;
				rhs = a2;
			} else if (a1.isNumericFunction()) {
				lhs = a2;
				rhs = a1;
				useOppositeHeader = true;
			} else {
				lhs = F.eval(F.Subtract(a1, a2));
				rhs = F.C0;
			}
			if (lhs.isAST()) {
				IAST lhsAST = (IAST) lhs;
				if (useOppositeHeader) {
					setTrue = !setTrue;
				}
				if (lhsAST.isInfinity() && rhs.isRealResult()) {
					// Infinity > rhs ?
					return setTrue ? F.True : F.False;
				}
				if (lhsAST.isNegativeInfinity() && rhs.isRealResult()) {
					// -Infinity > rhs ?
					return setTrue ? F.False : F.True;
					// return createComparatorResult(F.CN1, F.C1, useOppositeHeader, originalHead,
					// oppositeHead);
				}
				if (rhs.isInfinity() && lhsAST.isRealResult()) {
					// lhs > Infinity ?
					return setTrue ? F.False : F.True;
				}
				if (rhs.isNegativeInfinity() && lhsAST.isRealResult()) {
					// lhs > -Infinity ?
					return setTrue ? F.True : F.False;
				}
				if (lhsAST.isTimes()) {
					IAST result = lhsAST.partitionTimes(new Predicate<IExpr>() {
						@Override
						public boolean test(IExpr x) {
							return x.isNumericFunction();
						}
					}, F.C0, F.C1, F.List);
					if (!result.get(1).isZero()) {
						if (result.get(1).isNegative()) {
							useOppositeHeader = !useOppositeHeader;
						}
						rhs = rhs.divide(result.get(1));
						return createComparatorResult(result.get(2), rhs, useOppositeHeader, originalHead,
								oppositeHead);
					}
				} else if (lhsAST.isPlus()) {
					IAST result = lhsAST.partitionPlus(new Predicate<IExpr>() {
						@Override
						public boolean test(IExpr x) {
							return x.isNumericFunction();
						}
					}, F.C0, F.C0, F.List);
					if (!result.get(1).isZero()) {
						rhs = rhs.subtract(result.get(1));
						return createComparatorResult(result.get(2), rhs, useOppositeHeader, originalHead,
								oppositeHead);
					}
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <code>&gt;=</code> operator implementation.
	 * 
	 */
	public final static class GreaterEqual extends Greater {
		public final static GreaterEqual CONST = new GreaterEqual();

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 >= "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
			} else if (arg2.isZero()) {
				// arg1 >= 0
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			} else {
				// arg1 >= "positive number" > 0
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.GreaterEqual, F.LessEqual, true);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			if (a0.equals(a1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			return super.compareTernary(a0, a1);
		}

	}

	private final static class Implies extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			boolean evaled = false;
			IExpr arg1 = engine.evaluateNull(ast.arg1());
			if (arg1.isPresent()) {
				evaled = true;
			} else {
				arg1 = ast.arg1();
			}
			if (arg1.isTrue()) {
				return ast.arg2();
			}
			if (arg1.isFalse()) {
				return F.True;
			}

			IExpr arg2 = engine.evaluateNull(ast.arg2());
			if (arg2.isPresent()) {
				evaled = true;
			} else {
				arg2 = ast.arg2();
			}
			if (arg2.isTrue()) {
				return F.True;
			}
			if (arg2.isFalse()) {
				return F.Not(arg1);
			}
			if (arg1.equals(arg2)) {
				return F.True;
			}

			if (evaled) {
				return F.Implies(arg1, arg2);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <code>&lt;</code> operator implementation.
	 * 
	 */
	public final static class Less extends Greater {

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 < "negative number"
				if (arg1.isPositiveResult()) {
					return F.False;
				}

			} else if (arg2.isZero()) {
				// arg1 < 0
				if (arg1.isNegativeResult()) {
					return F.True;
				}
				if (arg1.isPositiveResult()) {
					return F.False;
				}
			} else {
				// arg1 < "positive number"
				if (arg1.isNegativeResult()) {
					return F.True;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.Less, F.Greater, false);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// swap arguments
			return super.compareTernary(a1, a0);
		}

	}

	/**
	 * <code>&lt;=</code> operator implementation.
	 * 
	 */
	public final static class LessEqual extends Greater {

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 <= "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.False;
				}
			} else if (arg2.isZero()) {
				// arg1 <= 0
				if (arg1.isNegativeResult()) {
					return F.True;
				}
				if (arg1.isPositiveResult()) {
					return F.False;
				}
			} else {
				// arg1 <= "positive number"
				if (arg1.isNegativeResult()) {
					return F.True;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.LessEqual, F.GreaterEqual, false);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// don't compare strings
			if (a0.equals(a1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			// swap arguments
			return super.compareTernary(a1, a0);
		}
	}

	/**
	 * <pre>
	 * Max(e_1, e_2, ..., e_i)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the expression with the greatest value among the <code>e_i</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Maximum of a series of numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(4, -8, 1)
	 * 4
	 * </pre>
	 * <p>
	 * <code>Max</code> flattens lists in its arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})
	 * 3.5
	 * </pre>
	 * <p>
	 * <code>Max</code> with symbolic arguments remains in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(x, y)
	 * Max(x,y)
	 * 
	 * &gt;&gt; Max(5, x, -3, y, 40)
	 * Max(40,x,y)
	 * </pre>
	 * <p>
	 * With no arguments, <code>Max</code> gives <code>-Infinity</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max()
	 * -Infinity
	 * 
	 * &gt;&gt; Max(x)
	 * x
	 * </pre>
	 */
	private static class Max extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1);

			if (ast.isAST0()) {
				return F.CNInfinity;
			}

			if (ast.arg1().isInterval1()) {
				IAST list = (IAST) ast.arg1().getAt(1);
				try {
					return list.arg2();
				} catch (ClassCastException cca) {
					// do nothing
				}
			}

			IAST resultList = EvalAttributes.flatten(F.List, ast);
			if (resultList.isPresent()) {
				return maximum(resultList, true);
			}

			return maximum(ast, false);
		}

		private IExpr maximum(IAST list, boolean flattenedList) {
			boolean evaled = false;
			int j = 1;
			IASTAppendable f = Lambda.remove(list, new Predicate<IExpr>() {
				@Override
				public boolean test(IExpr x) {
					return x.isNegativeInfinity();
				}
			});
			if (f.isPresent()) {
				if (f.isAST0()) {
					return F.CNInfinity;
				}
				list = f;
				evaled = true;
			}
			if (!evaled) {
				evaled = flattenedList;
			}
			IExpr max1;
			IExpr max2;
			max1 = list.arg1();

			IExpr.COMPARE_TERNARY comp;
			f = list.copyHead();
			for (int i = 2; i < list.size(); i++) {
				max2 = list.get(i);
				if (max1.equals(max2)) {
					continue;
				}
				comp = BooleanFunctions.CONST_LESS.prepareCompare(max1, max2);

				if (comp == IExpr.COMPARE_TERNARY.TRUE) {
					max1 = max2;
					evaled = true;
				} else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
					evaled = true;
				} else {
					if (comp == IExpr.COMPARE_TERNARY.UNDEFINED) {
						// undetermined
						if (max1.isNumber()) {
							f.append(max2);
						} else {
							f.append(max1);
							max1 = max2;
						}
					}
				}
			}
			if (f.size() > 1) {
				f.append(max1);
				if (!evaled) {
					return F.NIL;
				}
				return f;
			} else {
				return max1;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Min(e_1, e_2, ..., e_i)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the expression with the lowest value among the <code>e_i</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Minimum of a series of numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(4, -8, 1)
	 * -8
	 * </pre>
	 * <p>
	 * <code>Min</code> flattens lists in its arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})
	 * -Infinity
	 * </pre>
	 * <p>
	 * <code>Min</code> with symbolic arguments remains in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min(x, y)
	 * Min(x,y)
	 * 
	 * &gt;&gt; Min(5, x, -3, y, 40)
	 * Min(-3,x,y)
	 * </pre>
	 * <p>
	 * With no arguments, <code>Min</code> gives <code>Infinity</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min()
	 * Infinity
	 * 
	 * &gt;&gt; Min(x)
	 * x
	 * </pre>
	 */
	private static class Min extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1);

			if (ast.isAST0()) {
				return F.CInfinity;
			}

			if (ast.arg1().isInterval1()) {
				IAST list = (IAST) ast.arg1().getAt(1);
				try {
					return list.arg1();
				} catch (ClassCastException cca) {
					// do nothing
				}
			}

			IAST resultList = EvalAttributes.flatten(F.List, ast);
			if (resultList.isPresent()) {
				return minimum(resultList, true);
			}

			return minimum(ast, false);
		}

		private IExpr minimum(IAST list, final boolean flattenedList) {
			boolean evaled = false;
			IASTAppendable f = Lambda.remove(list, new Predicate<IExpr>() {
				@Override
				public boolean test(IExpr x) {
					return x.isInfinity();
				}
			});
			if (f.isPresent()) {
				if (f.isAST0()) {
					return F.CNInfinity;
				}
				list = f;
				evaled = true;
			}
			if (!evaled) {
				evaled = flattenedList;
			}

			IExpr min1;
			IExpr min2;
			min1 = list.arg1();
			f = list.copyHead();
			IExpr.COMPARE_TERNARY comp;
			for (int i = 2; i < list.size(); i++) {
				min2 = list.get(i);
				if (min2.isInfinity()) {
					evaled = true;
					continue;
				}

				if (min1.equals(min2)) {
					continue;
				}
				comp = BooleanFunctions.CONST_GREATER.prepareCompare(min1, min2);

				if (comp == IExpr.COMPARE_TERNARY.TRUE) {
					min1 = min2;
					evaled = true;
				} else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
					evaled = true;
				} else {
					if (comp == IExpr.COMPARE_TERNARY.UNDEFINED) {
						// undetermined
						if (min1.isNumber()) {
							f.append(min2);
						} else {
							f.append(min1);
							min1 = min2;
						}
					}
				}
			}
			if (f.size() > 1) {
				f.append(1, min1);
				if (!evaled) {
					return F.NIL;
				}
				return f;
			} else {
				return min1;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		}
	}

	private final static class Nand extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.False;
			}
			if (ast.isAST1()) {
				return F.Not(ast.arg1());
			}
			IASTAppendable result = ast.copyHead();
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isFalse()) {
					return F.True;
				} else if (temp.isTrue()) {
					evaled = true;
				} else {
					result.append(temp);
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.False;
				}
				if (result.isAST1()) {
					return F.Not(result.arg1());
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Negative extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = ast.arg1();
			if (arg1.isSignedNumber()) {
				return F.bool(arg1.isNegative());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return F.bool(signedNumber.isNegative());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private static class NoneTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return noneTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If any expression evaluates to <code>true</code> for a given unary predicate function return
		 * <code>False</code>, if all are <code>false</code> return <code>True</code>, else return an
		 * <code>Nor(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr noneTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalNor = F.ast(F.Nor);
			if (list.exists(new Predicate<IExpr>() {
				@Override
				public boolean test(IExpr x) {
					return noneTrueArgument(x, head, logicalNor, engine);
				}
			}, 1)) {
				return F.False;
			}
			return logicalNor.isAST0() ? F.True : logicalNor;
		}

		private static boolean noneTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector,
				EvalEngine engine) {
			IExpr temp = engine.evaluate(F.unary(head, x));
			if (temp.isTrue()) {
				return true;
			} else if (!temp.isFalse()) {
				resultCollector.append(temp);
			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class NonNegative extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isSignedNumber()) {
				return F.bool(!arg1.isNegative());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return F.bool(!signedNumber.isNegative());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private final static class NonPositive extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isSignedNumber()) {
				return F.bool(!arg1.isPositive());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return F.bool(!signedNumber.isPositive());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private static class Nor extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.True;
			}
			if (ast.isAST1()) {
				return F.Not(ast.arg1());
			}
			IASTAppendable result = ast.copyHead();
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isTrue()) {
					return F.False;
				} else if (temp.isFalse()) {
					evaled = true;
				} else {
					result.append(temp);
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.True;
				}
				if (result.isAST1()) {
					return F.Not(result.arg1());
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Not extends AbstractArg1 {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			if (o.isTrue()) {
				return F.False;
			}
			if (o.isFalse()) {
				return F.True;
			}
			if (o.isAST()) {
				IAST temp = (IAST) o;
				if (o.isNot()) {
					return ((IAST) o).arg1();
				}
				if (temp.isAST2()) {
					IExpr head = temp.head();
					if (head.equals(F.Equal)) {
						return temp.apply(F.Unequal);
					} else if (head.equals(F.Unequal)) {
						return temp.apply(F.Equal);
					} else if (head.equals(F.Greater)) {
						return temp.apply(F.LessEqual);
					} else if (head.equals(F.GreaterEqual)) {
						return temp.apply(F.Less);
					} else if (head.equals(F.Less)) {
						return temp.apply(F.GreaterEqual);
					} else if (head.equals(F.LessEqual)) {
						return temp.apply(F.Greater);
					}
				}
			}
			return F.NIL;
		}

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Logical_disjunction">Logical disjunction</a>
	 * 
	 */
	private static class Or extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.False;
			}

			boolean evaled = false;
			IAST flattenedAST = EvalAttributes.flatten(ast);
			if (flattenedAST.isPresent()) {
				evaled = true;
			} else {
				flattenedAST = ast;
			}

			IASTAppendable result = flattenedAST.copyAppendable();
			IExpr temp;
			IExpr sym;
			int[] symbols = new int[flattenedAST.size()];
			int[] notSymbols = new int[flattenedAST.size()];
			int index = 1;

			for (int i = 1; i < flattenedAST.size(); i++) {
				temp = flattenedAST.get(i);
				if (temp.isTrue()) {
					return F.True;
				}
				if (temp.isFalse()) {
					result.remove(index);
					evaled = true;
					continue;
				}

				temp = engine.evaluateNull(flattenedAST.get(i));
				if (temp.isPresent()) {
					if (temp.isTrue()) {
						return F.True;
					}
					if (temp.isFalse()) {
						result.remove(index);
						evaled = true;
						continue;
					}
					result.set(index, temp);
					evaled = true;
				} else {
					temp = flattenedAST.get(i);
				}

				if (temp.isSymbol()) {
					symbols[i] = flattenedAST.get(i).hashCode();
				} else if (temp.isNot()) {
					sym = ((IAST) temp).getAt(1);
					if (sym.isSymbol()) {
						notSymbols[i] = sym.hashCode();
					}
				}
				index++;
			}
			for (int i = 1; i < symbols.length; i++) {
				if (symbols[i] != 0) {
					for (int j = 1; j < notSymbols.length; j++) {
						if (i != j && symbols[i] == notSymbols[j] && (result.equalsAt(i, result.get(j).getAt(1)))) {
							// Or[a, Not[a]] => True
							return F.True;
						}
					}
				}
			}
			if (result.isAST1()) {
				return result.arg1();
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.False;
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	private final static class Positive extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isNumber()) {
				return F.bool(arg1.isPositive());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return F.bool(signedNumber.isPositive());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <code>===</code> operator implementation.
	 * 
	 */
	private final static class SameQ extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				return F.bool(!ast.existsLeft(new BiPredicate<IExpr, IExpr>() {
					@Override
					public boolean test(IExpr x, IExpr y) {
						return !x.isSame(y);
					}
				}));
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.NHOLDALL);
		}
	}

	private final static class SatisfiableQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IAST variables;
			if (ast.isAST2()) {
				if (ast.arg2().isList()) {
					variables = (IAST) ast.arg2();
				} else {
					variables = List(ast.arg2());
				}
			} else {
				VariablesSet vSet = new VariablesSet(ast.arg1());
				variables = vSet.getVarList();
			}

			return satisfiableQ(ast.arg1(), variables, 1) ? F.True : F.False;
		}

		private static boolean satisfiableQ(IExpr expr, IAST variables, int position) {
			if (variables.size() <= position) {
				return EvalEngine.get().evalTrue(expr);
			}
			IExpr sym = variables.get(position);
			if (sym.isSymbol()) {
				try {
					((ISymbol) sym).pushLocalVariable(F.True);
					if (satisfiableQ(expr, variables, position + 1)) {
						return true;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
				try {
					((ISymbol) sym).pushLocalVariable(F.False);
					if (satisfiableQ(expr, variables, position + 1)) {
						return true;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
			}
			return false;
		}
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Tautology_%28logic%29">Wikipedia: Tautology_</a>
	 * 
	 */
	private static class TautologyQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IAST variables;
			if (ast.isAST2()) {
				if (ast.arg2().isList()) {
					variables = (IAST) ast.arg2();
				} else {
					variables = List(ast.arg2());
				}
			} else {
				VariablesSet vSet = new VariablesSet(ast.arg1());
				variables = vSet.getVarList();

			}

			return tautologyQ(ast.arg1(), variables, 1) ? F.True : F.False;
		}

		private static boolean tautologyQ(IExpr expr, IAST variables, int position) {
			if (variables.size() <= position) {
				return EvalEngine.get().evalTrue(expr);
			}
			IExpr sym = variables.get(position);
			if (sym.isSymbol()) {
				try {
					((ISymbol) sym).pushLocalVariable(F.True);
					if (!tautologyQ(expr, variables, position + 1)) {
						return false;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
				try {
					((ISymbol) sym).pushLocalVariable(F.False);
					if (!tautologyQ(expr, variables, position + 1)) {
						return false;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
			}
			return true;
		}
	}

	/**
	 * Returns <code>True</code> if the 1st argument evaluates to <code>True</code>; <code>False</code> otherwise
	 */
	private static class TrueQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.bool(ast.equalsAt(1, F.True));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * {@code !=} operator implementation.
	 * 
	 */
	private final static class Unequal extends Equal {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 2) {
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
				if (ast.isAST2()) {
					IExpr arg1 = F.expandAll(ast.arg1(), true, true);
					IExpr arg2 = F.expandAll(ast.arg2(), true, true);

					b = prepareCompare(arg1, arg2, engine);
					if (b == IExpr.COMPARE_TERNARY.FALSE) {
						return F.True;
					}
					if (b == IExpr.COMPARE_TERNARY.TRUE) {
						return F.False;
					}

					IExpr result = simplifyCompare(arg1, arg2, F.Unequal);
					if (result.isPresent()) {
						return result;
					}
				}

				IASTMutable result = ast.copy();
				result.setArgs(result.size(), new IntFunction<IExpr>() {
					@Override
					public IExpr apply(int i) {
						return F.expandAll(result.get(i), true, true);
					}
				});
				// for (int i = 1; i < result.size(); i++) {
				// result.set(i, F.expandAll(result.get(i), true, true));
				// }
				int i = 2;
				int j;
				while (i < result.size()) {
					j = i;
					while (j < result.size()) {
						b = compareTernary(result.get(i - 1), result.get(j++));
						if (b == IExpr.COMPARE_TERNARY.TRUE) {
							return F.False;
						}
						if (b == IExpr.COMPARE_TERNARY.UNDEFINED) {
							return F.NIL;
						}
					}
					i++;
				}
			}
			return F.True;
		}

	}

	/**
	 * {@code =!=} operator implementation.
	 * 
	 */
	private final static class UnsameQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				int i = 2;
				int j;
				while (i < ast.size()) {
					j = i;
					while (j < ast.size()) {
						if (ast.get(i - 1).isSame(ast.get(j++))) {
							return F.False;
						}
					}
					i++;
				}
				return F.True;

			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.NHOLDALL);
		}
	}

	/**
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Exclusive_or">Wikipedia: Exclusive or</a>
	 * 
	 */
	private static class Xor extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 1) {
				return F.False;
			}
			if (ast.size() == 2) {
				return ast.arg1();
			}

			IExpr temp;
			IExpr result = ast.arg1();
			int size = ast.size();
			IASTAppendable xor = F.ast(F.Xor, size - 1, false);
			boolean evaled = false;
			for (int i = 2; i < size; i++) {
				temp = ast.get(i);
				if (temp.isTrue()) {
					if (result.isTrue()) {
						result = F.False;
					} else if (result.isFalse()) {
						result = F.True;
					} else {
						result = engine.evaluate(F.Not(result));
					}
					evaled = true;
				} else if (temp.isFalse()) {
					if (result.isTrue()) {
						result = F.True;
					} else if (result.isFalse()) {
						result = F.False;
					}
					evaled = true;
				} else {
					if (temp.equals(result)) {
						result = F.False;
						evaled = true;
					} else {
						if (result.isTrue()) {
							result = engine.evaluate(F.Not(temp));
							evaled = true;
						} else if (result.isFalse()) {
							result = temp;
							evaled = true;
						} else {
							xor.append(temp);
						}
					}
				}
			}
			if (evaled) {
				xor.append(result);
				return xor;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	/**
	 * Compare if the first and second argument are equal.
	 * 
	 * @param a1
	 *            first argument
	 * @param a2
	 *            second argument
	 * @return <code>F.NIL</code> or the simplified expression, if equality couldn't be determined.
	 */
	public static IExpr equalNull(final IExpr a1, final IExpr a2, EvalEngine engine) {
		IExpr.COMPARE_TERNARY b;
		IExpr arg1 = F.expandAll(a1, true, true);
		IExpr arg2 = F.expandAll(a2, true, true);

		b = CONST_EQUAL.prepareCompare(arg1, arg2, engine);
		if (b == IExpr.COMPARE_TERNARY.FALSE) {
			return F.False;
		}
		if (b == IExpr.COMPARE_TERNARY.TRUE) {
			return F.True;
		}

		return CONST_EQUAL.simplifyCompare(arg1, arg2, F.Equal);
	}

	public static IExpr equals(final IAST ast) {
		return equalNull(ast.arg1(), ast.arg2(), EvalEngine.get()).orElse(ast);
	}

	private final static BooleanFunctions CONST = new BooleanFunctions();

	public static BooleanFunctions initialize() {
		return CONST;
	}

	private BooleanFunctions() {

	}

}
