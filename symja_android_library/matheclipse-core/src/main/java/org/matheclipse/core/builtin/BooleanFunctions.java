package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Equivalent;
import static org.matheclipse.core.expression.F.Implies;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Nand;
import static org.matheclipse.core.expression.F.Nor;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Xor;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.boole.QuineMcCluskyFormula;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

public final class BooleanFunctions {
	static {
		F.AllTrue.setEvaluator(new AllTrue());
		F.And.setEvaluator(new And());
		F.AnyTrue.setEvaluator(new AnyTrue());
		F.Boole.setEvaluator(new Boole());
		F.BooleanConvert.setEvaluator(new BooleanConvert());
		F.BooleanMinimize.setEvaluator(new BooleanMinimize());
		F.BooleanTable.setEvaluator(new BooleanTable());
		F.BooleanVariables.setEvaluator(new BooleanVariables());
		F.NoneTrue.setEvaluator(new NoneTrue());
		F.Nor.setEvaluator(new Nor());
		F.Not.setEvaluator(new Not());
		F.Or.setEvaluator(new Or());
		F.TautologyQ.setEvaluator(new TautologyQ());
		F.TrueQ.setEvaluator(new TrueQ());
		F.Xor.setEvaluator(new Xor());
	}

	private static class AllTrue extends AbstractFunctionEvaluator {

		public AllTrue() {
		}

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
		 * If all expressions evaluates to <code>true</code> for a given unary
		 * predicate function return <code>True</code>, if any expression
		 * evaluates to <code>false</code> return <code>False</code>, else
		 * return an <code>And(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
			IAST logicalAnd = F.And();
			int size = list.size();
			for (int i = 1; i < size; i++) {
				IExpr temp = engine.evaluate(F.unary(head, list.get(i)));
				if (temp.isTrue()) {
					continue;
				} else if (temp.isFalse()) {
					return F.False;
				}
				logicalAnd.append(temp);
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
	 * See <a href="http://en.wikipedia.org/wiki/Logical_conjunction">Logical
	 * conjunction</a>
	 * 
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/And">And
	 * </a>
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

			IAST result = flattenedAST.clone();
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

		public AnyTrue() {
		}

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
		 * If any expression evaluates to <code>true</code> for a given unary
		 * predicate function return <code>True</code>, if all are
		 * <code>false</code> return <code>False</code>, else return an
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
			IAST logicalOr = F.Or();
			int size = list.size();
			for (int i = 1; i < size; i++) {
				IExpr temp = engine.evaluate(F.unary(head, list.get(i)));
				if (temp.isTrue()) {
					return F.True;
				} else if (temp.isFalse()) {
					continue;
				}
				logicalOr.append(temp);
			}
			if (logicalOr.size() > 1) {
				return logicalOr;
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Predicate function
	 * 
	 * Returns <code>1</code> if the 1st argument evaluates to <code>True</code>
	 * ; returns <code>0</code> if the 1st argument evaluates to
	 * <code>False</code>; and <code>null</code> otherwise.
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

		public BooleanConvert() {
			super();
		}

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
				IAST result = F.Or();
				for (int i = 1; i < ast.size(); i++) {
					result.append(Not(ast.get(i)));
				}
				return result;
			}

			public IAST convertNor(IAST ast) {
				IAST result = F.And();
				for (int i = 1; i < ast.size(); i++) {
					result.append(Not(ast.get(i)));
				}
				return result;
			}

			public IAST convertXor(IAST ast) {
				IExpr temp = ast.arg2();
				if (ast.size() > 3) {
					IAST clone = ast.clone();
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
	 * Minimize a boolean function with the
	 * <a href="http://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">
	 * Quine McCluskey algorithm</a>.
	 */
	private static class BooleanMinimize extends AbstractFunctionEvaluator {

		public BooleanMinimize() {
			super();
		}

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
	 * See <a href="https://en.wikipedia.org/wiki/Truth_table">Wikipedia: Truth
	 * table</a>
	 * 
	 */
	private static class BooleanTable extends AbstractFunctionEvaluator {

		public BooleanTable() {
			// default ctor
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

			IAST resultList = F.List();
			booleanTable(ast.arg1(), variables, 1, resultList);
			return resultList;
		}

		private static void booleanTable(IExpr expr, IAST variables, int position, IAST resultList) {
			if (variables.size() <= position) {
				resultList.append(EvalEngine.get().evalTrue(expr) ? F.True : F.False);
				return;
			}
			IExpr sym = variables.get(position);
			if (sym.isSymbol()) {
				try {
					((ISymbol) sym).pushLocalVariable(F.True);
					booleanTable(expr, variables, position + 1, resultList);
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
				try {
					((ISymbol) sym).pushLocalVariable(F.False);
					booleanTable(expr, variables, position + 1, resultList);
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
			}
		}
	}

	/**
	 * Determine the variable symbols of an expression
	 */
	private static class BooleanVariables extends AbstractFunctionEvaluator {

		public BooleanVariables() {
			// empty ctor
		}

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
		 * If any expression evaluates to <code>true</code> for a given unary
		 * predicate function return <code>False</code>, if all are
		 * <code>false</code> return <code>True</code>, else return an
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
			IAST logicalNor = F.ast(F.Nor);
			int size = list.size();
			for (int i = 1; i < size; i++) {
				IExpr temp = engine.evaluate(F.unary(head, list.get(i)));
				if (temp.isTrue()) {
					return F.False;
				} else if (temp.isFalse()) {
					continue;
				}
				logicalNor.append(temp);
			}
			if (logicalNor.size() > 1) {
				return logicalNor;
			}
			return F.True;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
			IAST result = ast.copyHead();
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

		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Not extends AbstractArg1 {

		public Not() {
		}

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
	 * See <a href="http://en.wikipedia.org/wiki/Logical_disjunction">Logical
	 * disjunction</a>
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

			IAST result = flattenedAST.clone();
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

	/**
	 * See
	 * <a href="https://en.wikipedia.org/wiki/Tautology_%28logic%29">Wikipedia:
	 * Tautology_</a>
	 * 
	 */
	private static class TautologyQ extends AbstractFunctionEvaluator {

		public TautologyQ() {
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
	 * Returns <code>True</code> if the 1st argument evaluates to
	 * <code>True</code>; <code>False</code> otherwise
	 */
	private static class TrueQ extends AbstractFunctionEvaluator {

		public TrueQ() {
		}

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
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Exclusive_or">Wikipedia:
	 * Exclusive or</a>
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
			boolean evaled = false;
			for (int i = 2; i < ast.size(); i++) {
				temp = ast.get(i);
				if (temp.isTrue()) {
					if (result.isTrue()) {
						result = F.False;
					} else if (result.isFalse()) {
						result = F.True;
					} else {
						result = F.eval(F.Not(result));
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
							result = F.eval(F.Not(result));
							evaled = true;
						} else if (result.isFalse()) {
							result = temp;
							evaled = true;
						} else {
							if (evaled) {
								IAST xor = F.ast(F.Xor, ast.size() - i + 1, false);
								xor.append(result);
								xor.append(temp);
								xor.appendAll(ast, i + 1, ast.size());
								// for (int j = i + 1; j < ast.size(); j++) {
								// xor.append(ast.get(j));
								// }
								return xor;
							}
							return F.NIL;
						}
					}
				}
			}

			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	final static BooleanFunctions CONST = new BooleanFunctions();

	public static BooleanFunctions initialize() {
		return CONST;
	}

	private BooleanFunctions() {

	}

}
