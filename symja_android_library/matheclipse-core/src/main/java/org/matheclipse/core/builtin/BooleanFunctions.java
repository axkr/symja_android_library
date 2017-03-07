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
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

public class BooleanFunctions {
	static {
		F.AllTrue.setEvaluator(new AllTrue());
		F.AnyTrue.setEvaluator(new AnyTrue());
		F.Boole.setEvaluator(new Boole());
		F.BooleanConvert.setEvaluator(new BooleanConvert());
		F.BooleanMinimize.setEvaluator(new BooleanMinimize());
		F.BooleanTable.setEvaluator(new BooleanTable());
		F.BooleanVariables.setEvaluator(new BooleanVariables());
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

	final static BooleanFunctions CONST = new BooleanFunctions();

	public static BooleanFunctions initialize() {
		return CONST;
	}

	private BooleanFunctions() {

	}

}
