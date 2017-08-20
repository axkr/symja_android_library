package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbol.RuleType;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

public final class PatternMatching {

	static {

		F.Clear.setEvaluator(new Clear());
		F.ClearAll.setEvaluator(new ClearAll());
		F.Optional.setEvaluator(new Optional());
		F.Rule.setEvaluator(new Rule());
		F.RuleDelayed.setEvaluator(new RuleDelayed());
		F.Set.setEvaluator(new Set());
		F.SetDelayed.setEvaluator(new SetDelayed());
		F.Unset.setEvaluator(new Unset());
		F.UpSet.setEvaluator(new UpSet());
		F.UpSetDelayed.setEvaluator(new UpSetDelayed());
	}

	/**
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Clear">Clear</a>
	 * </p>
	 */
	private static class Clear extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);
			Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clear(engine));
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/ClearAll">ClearAll</a>
	 * </p>
	 */
	private final static class ClearAll extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			ISymbol symbol = Validate.checkSymbolType(ast, 1);
			symbol.clearAll(engine);
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Optional extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			if (ast.arg1().isPattern()) {
				IPattern patt = (IPattern) ast.arg1();
				return F.$p(patt.getSymbol(), patt.getCondition(), ast.arg2());
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Rule extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			} else {
				leftHandSide = engine.evaluate(leftHandSide);
			}
			IExpr arg2 = engine.evaluateNull(ast.arg2());
			if (!arg2.isPresent()) {
				if (leftHandSide.equals(ast.arg1())) {
					return F.NIL;
				}
				return Rule(leftHandSide, ast.arg2());
			}
			return Rule(leftHandSide, arg2);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class RuleDelayed extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			} else {
				leftHandSide = engine.evaluate(leftHandSide);
			}
			if (!leftHandSide.equals(ast.arg1())) {
				return RuleDelayed(leftHandSide, ast.arg2());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Set extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			IExpr rightHandSide = ast.arg2();
			if (leftHandSide.isAST()) {
				IAST leftHandSideAST = (IAST) leftHandSide;
				if (leftHandSideAST.isAST(F.Part) && leftHandSideAST.size() > 1) {
					IAST part = leftHandSideAST;
					if (part.arg1().isSymbol()) {
						ISymbol symbol = (ISymbol) part.arg1();
						RulesData rd = symbol.getRulesData();
						if (rd == null) {
							engine.printMessage("Set: no value defined for symbol '" + symbol.toString()
									+ "' in Part() expression.");
						} else {
							try {
								IExpr temp = symbol.getRulesData().evalDownRule(symbol);
								if (!temp.isPresent()) {
									engine.printMessage("Set: no value defined for symbol '" + symbol.toString()
											+ "' in Part() expression.");
								} else {
									if (rightHandSide.isList()) {
										IExpr res = Programming.assignPart(temp, part, 2, (IAST) rightHandSide, 1,
												engine);
										symbol.putDownRule(RuleType.SET, true, symbol, res, false);
										return rightHandSide;
									} else {
										IExpr res = Programming.assignPart(temp, part, 2, rightHandSide, engine);
										symbol.putDownRule(RuleType.SET, true, symbol, res, false);
										return rightHandSide;
									}
								}
							} catch (RuntimeException npe) {
								engine.printMessage("Set: wrong argument for Part[] function: " + part.toString()
										+ " selects no part expression.");
							}
						}
					}

				} else if (leftHandSideAST.isList()) {
					// thread over lists
					try {
						rightHandSide = engine.evaluate(rightHandSide);
					} catch (final ReturnException e) {
						rightHandSide = e.getValue();
					}
					IExpr temp = EvalEngine.threadASTListArgs(F.Set(leftHandSideAST, rightHandSide));
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
					return F.NIL;
				} else if (leftHandSideAST.isAST(F.Attributes, 2)) {
					IAST symbolList = Validate.checkSymbolOrSymbolList(leftHandSideAST, 1);
					symbolList.forEach(x -> ((ISymbol) x).setAttributes(ISymbol.NOATTRIBUTE));
					return AttributeFunctions.setSymbolsAttributes(ast, engine, symbolList);
				}
			}
			Object[] result;
			result = createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);
			return (IExpr) result[1];
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				// System.out.println("Condition[] in right-hand-side of Set[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			return setDownRule(leftHandSide, rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class SetDelayed extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		// public final static SetDelayed CONST = new SetDelayed();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			final IExpr rightHandSide = ast.arg2();

			createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);

			return F.Null;
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {
			if (leftHandSide.isAST()
					&& (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			return setDelayedDownRule(leftHandSide, rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	public static Object[] setDownRule(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET, false, leftHandSide, rightHandSide, packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;

			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET, true, leftHandSide, rightHandSide, packageMode);
			return result;
		}

		throw new RuleCreationError(leftHandSide);
	}

	public static Object[] setDelayedDownRule(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET_DELAYED, false, leftHandSide, rightHandSide,
					packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET_DELAYED, true, leftHandSide, rightHandSide,
					packageMode);
			return result;
		}
		throw new RuleCreationError(leftHandSide);
	}

	private final static class Unset extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			final IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isList()) {
				// thread over lists
				IExpr temp = EvalEngine.threadASTListArgs(F.Unset(leftHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
			return F.Null;
		}

		public void removePatternMatcher(IExpr leftHandSide, boolean packageMode, EvalEngine engine)
				throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			removeRule(leftHandSide, packageMode);
		}

		public void removeRule(IExpr leftHandSide, boolean packageMode) {
			if (leftHandSide.isAST()) {
				final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
				lhsSymbol.removeRule(ISymbol.RuleType.SET, false, leftHandSide, packageMode);
				return;
			}
			if (leftHandSide.isSymbol()) {
				final ISymbol lhsSymbol = (ISymbol) leftHandSide;

				lhsSymbol.removeRule(ISymbol.RuleType.SET, true, leftHandSide, packageMode);
				return;
			}

			throw new RuleCreationError(leftHandSide);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSet extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			IExpr rightHandSide = ast.arg2();
			if (leftHandSide.isList()) {
				// thread over lists
				try {
					rightHandSide = engine.evaluate(rightHandSide);
				} catch (final ReturnException e) {
					rightHandSide = e.getValue();
				}
				IExpr temp = EvalEngine.threadASTListArgs(F.UpSet(leftHandSide, rightHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			Object[] result = createPatternMatcher(leftHandSide, rightHandSide, false, engine);
			return (IExpr) result[1];
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				System.out.println("Condition[] in right-hand-side of UpSet[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			result[0] = null; // IPatternMatcher
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(ISymbol.RuleType.UPSET, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSetDelayed extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			final IExpr rightHandSide = ast.arg2();

			createPatternMatcher(leftHandSide, rightHandSide, false, engine);

			return F.Null;
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			if (leftHandSide.isAST()
					&& (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			result[0] = null;
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);

			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(ISymbol.RuleType.UPSET_DELAYED, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	final static PatternMatching CONST = new PatternMatching();

	public static PatternMatching initialize() {
		return CONST;
	}

	private PatternMatching() {

	}
}
