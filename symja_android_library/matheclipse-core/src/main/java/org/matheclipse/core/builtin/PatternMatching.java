package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbol.RuleType;
import org.matheclipse.core.patternmatching.PatternMatcher;

public final class PatternMatching {

	static {
		
		F.Clear.setEvaluator(new Clear());
		F.ClearAll.setEvaluator(new ClearAll());
		F.Rule.setEvaluator(new Rule());
		F.RuleDelayed.setEvaluator(new RuleDelayed());
		F.Set.setEvaluator(new Set());
		F.SetAttributes.setEvaluator(new SetAttributes());
		F.SetDelayed.setEvaluator(new SetDelayed());
		F.Unset.setEvaluator(new Unset());
		F.UpSet.setEvaluator(new UpSet());
		F.UpSetDelayed.setEvaluator(new UpSetDelayed());
	}

	/**
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Clear">Clear</a>
	 * </p>
	 */
	private static class Clear extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);

			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isSymbol()) {
					ISymbol symbol = (ISymbol) ast.get(i);
					symbol.clear(engine);
				}
			}

			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/ClearAll">ClearAll</a>
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

	private final static class Rule extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
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
			if (leftHandSide.isAST(F.Part) && ((IAST) leftHandSide).size() > 1) {
				IAST part = ((IAST) leftHandSide);
				if (part.arg1().isSymbol()) {
					ISymbol symbol = (ISymbol) part.arg1();
					IExpr temp = symbol.getRulesData().evalDownRule(symbol);
					if (rightHandSide.isList()) {
						IExpr res = Programming.assignPart(temp, part, 2, ((IAST) rightHandSide).iterator(), engine);
						symbol.putDownRule(RuleType.SET, true, symbol, res, false);
						return rightHandSide;
					} else {
						IExpr res = Programming.assignPart(temp, part, 2, rightHandSide, engine);
						symbol.putDownRule(RuleType.SET, true, symbol, res, false);
						return rightHandSide;
					}
				}

			}
			if (leftHandSide.isList()) {
				// thread over lists
				try {
					rightHandSide = engine.evaluate(rightHandSide);
				} catch (final ReturnException e) {
					rightHandSide = e.getValue();
				}
				IExpr temp = EvalEngine.threadASTListArgs(F.Set(leftHandSide, rightHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
				return F.NIL;
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
				System.out.println("Condition[] in right-hand-side of Set[]");
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

	/**
	 * Set the attributes for a symbol
	 * 
	 */
	private final static class SetAttributes extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isSymbol()) {
				IExpr arg2 = engine.evaluate(ast.arg2());
				final ISymbol sym = ((ISymbol) ast.arg1());
				if (!engine.isPackageMode()) {
					if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
						throw new RuleCreationError(sym);
					}
				}
				if (arg2.isSymbol()) {
					ISymbol attribute = (ISymbol) arg2;
					if (attribute == F.Flat) {
						sym.setAttributes(ISymbol.FLAT);
						return F.Null;
					}

					if (attribute == F.Listable) {
						sym.setAttributes(ISymbol.LISTABLE);
						return F.Null;
					}

					if (attribute == F.OneIdentity) {
						sym.setAttributes(ISymbol.ONEIDENTITY);
						return F.Null;
					}

					if (attribute == F.Orderless) {
						sym.setAttributes(ISymbol.ORDERLESS);
						return F.Null;
					}

					if (attribute == F.HoldAll) {
						sym.setAttributes(ISymbol.HOLDALL);
						return F.Null;
					}

					if (attribute == F.HoldFirst) {
						sym.setAttributes(ISymbol.HOLDFIRST);
						return F.Null;
					}

					if (attribute == F.HoldRest) {
						sym.setAttributes(ISymbol.HOLDREST);
						return F.Null;
					}

					if (attribute == F.NHoldAll) {
						sym.setAttributes(ISymbol.NHOLDALL);
						return F.Null;
					}

					if (attribute == F.NHoldFirst) {
						sym.setAttributes(ISymbol.NHOLDFIRST);
						return F.Null;
					}

					if (attribute == F.NHoldRest) {
						sym.setAttributes(ISymbol.NHOLDREST);
						return F.Null;
					}

					if (attribute == F.NumericFunction) {
						sym.setAttributes(ISymbol.NUMERICFUNCTION);
						return F.Null;
					}

				} else {
					if (ast.arg2().isList()) {
						final IAST lst = (IAST) ast.arg2();
						int symbolAttributes = ISymbol.NOATTRIBUTE;
						for (int i = 1; i < lst.size(); i++) {
							ISymbol attribute = (ISymbol) lst.get(i);
							if (attribute == F.Flat) {
								sym.setAttributes(symbolAttributes | ISymbol.FLAT);
							}

							if (attribute == F.Listable) {
								sym.setAttributes(symbolAttributes | ISymbol.LISTABLE);
							}

							if (attribute == F.OneIdentity) {
								sym.setAttributes(symbolAttributes | ISymbol.ONEIDENTITY);
							}

							if (attribute == F.Orderless) {
								sym.setAttributes(symbolAttributes | ISymbol.ORDERLESS);
							}

							if (attribute == F.HoldAll) {
								sym.setAttributes(symbolAttributes | ISymbol.HOLDALL);
							}

							if (attribute == F.HoldFirst) {
								sym.setAttributes(symbolAttributes | ISymbol.HOLDFIRST);
							}

							if (attribute == F.HoldRest) {
								sym.setAttributes(symbolAttributes | ISymbol.HOLDREST);
							}

							if (attribute == F.NHoldAll) {
								sym.setAttributes(symbolAttributes | ISymbol.NHOLDALL);
							}

							if (attribute == F.NHoldFirst) {
								sym.setAttributes(symbolAttributes | ISymbol.NHOLDFIRST);
							}

							if (attribute == F.NHoldRest) {
								sym.setAttributes(symbolAttributes | ISymbol.NHOLDREST);
							}

							if (attribute == F.NumericFunction) {
								sym.setAttributes(symbolAttributes | ISymbol.NUMERICFUNCTION);
							}

							symbolAttributes = sym.getAttributes();
						}
						// end for

						return F.Null;
					}
				}
			}
			return F.NIL;
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
