package org.matheclipse.core.builtin;

import java.util.IdentityHashMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.parser.client.math.MathException;

public final class Programming {
	final static Programming CONST = new Programming();

	static {
		F.Break.setEvaluator(new Break());
		F.Block.setEvaluator(new Block());
		F.Catch.setEvaluator(new Catch());
		F.CompoundExpression.setEvaluator(new CompoundExpression());
		F.Condition.setEvaluator(new Condition());
		F.Continue.setEvaluator(new Continue());
		F.For.setEvaluator(new For());
		F.Module.setEvaluator(new Module());
		F.Return.setEvaluator(new Return());
		F.Switch.setEvaluator(new Switch());
		F.Which.setEvaluator(new Which());
		F.While.setEvaluator(new While());

	}

	/**
	 * <p>
	 * <code>Break()</code> leaves a <code>Do</code>, <code>For</code> or
	 * <code>While</code> loop.
	 * </p>
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Break">
	 * Break</a>
	 * </p>
	 *
	 */
	private static class Break extends AbstractCoreFunctionEvaluator {

		public Break() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw BreakException.CONST;
			}
			Validate.checkSize(ast, 1);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * 
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Block">
	 * Block</a>
	 * </p>
	 *
	 */
	private static class Block extends AbstractCoreFunctionEvaluator {
		public Block() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				final IAST blockVariablesList = (IAST) ast.arg1();
				return engine.evalBlock(ast.arg2(), blockVariablesList);
			}

			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Catch">
	 * Catch</a>
	 * </p>
	 */
	private static class Catch extends AbstractCoreFunctionEvaluator {

		public Catch() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			try {
				return engine.evaluate(ast.arg1());
			} catch (final ThrowException e) {
				return e.getValue();
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class CompoundExpression extends AbstractCoreFunctionEvaluator {

		public CompoundExpression() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				for (int i = 1; i < ast.size() - 1; i++) {
					// as sideeffect evaluate the i-th argument
					engine.evaluate(ast.get(i));
				}
				return engine.evaluate(ast.get(ast.size() - 1));
			}
			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Condition extends AbstractCoreFunctionEvaluator {

		public Condition() {
			// default ctor
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				if (engine.evalTrue(ast.arg2())) {
					return engine.evaluate(ast.arg1());
				}
				if (engine.isEvalLHSMode()) {
					return F.NIL;
				}
				throw new ConditionException(ast);
			}
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Continue extends AbstractCoreFunctionEvaluator {

		public Continue() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw ContinueException.CONST;
			}
			Validate.checkSize(ast, 1);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * For[] loop
	 * 
	 * Example: For[$j = 1, $j <= 10, $j++, Print[$j]]
	 * 
	 */
	private static class For extends AbstractCoreFunctionEvaluator {

		public For() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 4, 5);
			final int iterationLimit = engine.getIterationLimit();
			int iterationCounter = 1;

			// For(start, test, incr, body)
			engine.evaluate(ast.arg1()); // start
			IExpr test = ast.arg2();
			IExpr incr = ast.arg3();
			IExpr body = F.Null;
			if (ast.size() == 5) {
				body = ast.arg4();
			}
			boolean exit = false;
			while (true) {
				try {
					if (!engine.evaluate(test).isTrue()) {
						exit = true;
						return F.Null;
					}
					if (ast.size() == 5) {
						engine.evaluate(body);
					}
					if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
						IterationLimitExceeded.throwIt(iterationCounter, ast);
					}
				} catch (final BreakException e) {
					exit = true;
					return F.Null;
				} catch (final ContinueException e) {
					if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
						IterationLimitExceeded.throwIt(iterationCounter, ast);
					}
					continue;
				} catch (final ReturnException e) {
					return e.getValue();
				} finally {
					if (!exit) {
						engine.evaluate(incr);
					}
				}
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Module extends AbstractCoreFunctionEvaluator {
		public Module() {
		}

		/**
		 *
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				IAST lst = (IAST) ast.arg1();
				IExpr arg2 = ast.arg2();
				return evalModule(lst, arg2, engine);
			}

			return F.NIL;
		}

		/**
		 * <code>Module[{variablesList}, rhs ]</code>
		 * 
		 * @param intializerList
		 * @param arg2
		 * @param engine
		 * @return
		 */
		private static IExpr evalModule(IAST intializerList, IExpr arg2, final EvalEngine engine) {
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			final java.util.IdentityHashMap<ISymbol, ISymbol> moduleVariables = new IdentityHashMap<ISymbol, ISymbol>();

			try {
				rememberVariables(intializerList, varAppend, moduleVariables, engine);
				IExpr subst = arg2.accept(new ModuleReplaceAll(moduleVariables));
				if (subst.isPresent()) {
					return engine.evaluate(subst);
				}
				return arg2;
			} finally {
				engine.removeUserVariables(moduleVariables);
			}
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Return extends AbstractCoreFunctionEvaluator {

		public Return() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw new ReturnException();
			}
			if (ast.isAST1()) {
				throw new ReturnException(engine.evaluate(ast.arg1()));
			}
			Validate.checkRange(ast, 1, 2);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private static class Switch extends AbstractCoreFunctionEvaluator {

		public Switch() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 4);
			final IExpr arg1 = engine.evaluate(ast.arg1());
			IPatternMatcher matcher;
			for (int i = 2; i < ast.size(); i += 2) {
				matcher = engine.evalPatternMatcher(ast.get(i));
				if (matcher.test(arg1) && (i + 1 < ast.size())) {
					return engine.evaluate(ast.get(i + 1));
				}
			}
			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Which extends AbstractCoreFunctionEvaluator {

		public Which() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkEven(ast);

			for (int i = 1; i < ast.size(); i += 2) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isFalse()) {
					continue;
				}
				if (temp.isTrue()) {
					if ((i + 1 < ast.size())) {
						return engine.evaluate(ast.get(i + 1));
					}
					continue;
				}
				if (i == 1) {
					return F.NIL;
				}
				return F.ast(ast, ast.head(), true, i, ast.size());
			}
			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class While extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			final int iterationLimit = engine.getIterationLimit();
			int iterationCounter = 1;
			// While(test, body)
			IExpr test = ast.arg1();
			IExpr body = F.Null;
			if (ast.isAST2()) {
				body = ast.arg2();
			}

			while (engine.evaluate(test).isTrue()) {
				try {
					if (ast.isAST2()) {
						engine.evaluate(body);
					}
					if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
						IterationLimitExceeded.throwIt(iterationCounter, ast);
					}
				} catch (final BreakException e) {
					return F.Null;
				} catch (final ContinueException e) {
					continue;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}

			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * Remember which local variable names (appended with the module counter) we
	 * use in the given <code>variablesMap</code>.
	 * 
	 * @param variablesList
	 *            initializer variables list from the <code>Module</code>
	 *            function
	 * @param varAppend
	 *            the module counter string which aer appended to the variable
	 *            names.
	 * @param variablesMap
	 *            the resulting module variables map
	 * @param engine
	 *            the evaluation engine
	 */
	private static void rememberVariables(IAST variablesList, final String varAppend,
			final java.util.Map<ISymbol, ISymbol> variablesMap, final EvalEngine engine) {
		ISymbol oldSymbol;
		ISymbol newSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isSymbol()) {
				oldSymbol = (ISymbol) variablesList.get(i);
				newSymbol = F.$s(oldSymbol.toString() + varAppend);
				variablesMap.put(oldSymbol, newSymbol);
				newSymbol.pushLocalVariable();
			} else {
				if (variablesList.get(i).isAST(F.Set, 3)) {
					final IAST setFun = (IAST) variablesList.get(i);
					if (setFun.arg1().isSymbol()) {
						oldSymbol = (ISymbol) setFun.arg1();
						newSymbol = F.$s(oldSymbol.toString() + varAppend);
						variablesMap.put(oldSymbol, newSymbol);
						IExpr rightHandSide = setFun.arg2();
						try {
							rightHandSide = engine.evaluate(rightHandSide);
						} catch (MathException me) {
							if (Config.DEBUG) {
								me.printStackTrace();
							}
						}
						newSymbol.pushLocalVariable(rightHandSide);
					}
				}
			}
		}
	}

	/**
	 * Check the (possible nested) module condition in pattern matcher without
	 * evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkModuleCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (arg1.isList()) {
			IAST intializerList = (IAST) arg1;
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			final java.util.Map<ISymbol, ISymbol> moduleVariables = new IdentityHashMap<ISymbol, ISymbol>();

			try {
				rememberVariables(intializerList, varAppend, moduleVariables, engine);
				IExpr result = F.subst(arg2, Functors.rules(moduleVariables));
				if (result.isCondition()) {
					return checkCondition(result.getAt(1), result.getAt(2), engine);
				} else if (result.isModule()) {
					return checkModuleCondition(result.getAt(1), result.getAt(2), engine);
				}
			} finally {
				engine.removeUserVariables(moduleVariables);
			}
		}
		return true;
	}

	/**
	 * Check the (possible nested) condition in pattern matcher without
	 * evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (engine.evalTrue(arg2)) {
			if (arg1.isCondition()) {
				return checkCondition(arg1.getAt(1), arg1.getAt(2), engine);
			} else if (arg2.isModule()) {
				return checkModuleCondition(arg2.getAt(1), arg2.getAt(2), engine);
			}
			return true;
		}
		return false;
	}

	public static Programming initialize() {
		return CONST;
	}

	private Programming() {

	}
}
