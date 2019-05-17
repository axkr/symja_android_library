package org.matheclipse.core.patternmatching;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class PatternMatcherList extends PatternMatcherAndEvaluator {
	IASTAppendable fReplaceList;
	
	public IASTAppendable getReplaceList() {
		return fReplaceList;
	}

	public PatternMatcherList(final int setSymbol, final IExpr leftHandSide,
			final IExpr rightHandSide) {
		super(setSymbol, leftHandSide, rightHandSide, true, 0);
		fReplaceList = F.ListAlloc();
	}

	final public IExpr replace(final IExpr leftHandSide, @Nonnull EvalEngine engine, boolean evaluate) {
		IPatternMap patternMap = null;
		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			if (fLhsPatternExpr.equals(leftHandSide)) {
				IExpr result = fRightHandSide;
				try {
					if (evaluate) {
						return engine.evaluate(result);
					}
					return result;
				} catch (final ConditionException e) {
					logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
					return F.NIL;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}
			if (!(fLhsPatternExpr.isOrderlessAST() && leftHandSide.isOrderlessAST())) {
				if (!(fLhsPatternExpr.isFlatAST() && leftHandSide.isFlatAST())) {
					return F.NIL;
				}
				// replaceSubExpressionOrderlessFlat() below implements equals matching for
				// special cases, if the AST is Orderless or Flat
			}
			if (fLhsPatternExpr.size() == leftHandSide.size()) {
				return F.NIL;
			}
		} else {
			patternMap = getPatternMap();
			patternMap.initPattern();
			if (matchExpr(fLhsPatternExpr, leftHandSide, engine, new StackMatcher(engine))) {

				if (RulesData.showSteps) {
					if (fLhsPatternExpr.head().equals(F.Integrate)) {
						IExpr rhs = fRightHandSide.orElse(F.Null);
						System.out.println("\nCOMPLEX: " + fLhsPatternExpr.toString() + " := " + rhs.toString());
						System.out.println("\n>>>>> " + toString());
					}
				}

				if (fReturnResult.isPresent()) {
					return fReturnResult;
				}
				IExpr result = patternMap.substituteSymbols(fRightHandSide);
				try {
					// System.out.println(result.toString());
					if (evaluate) {
						result = engine.evaluate(result);
					}
					return result;
				} catch (final ConditionException e) {
					logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
					return F.NIL;
				} catch (final ReturnException e) {
					return e.getValue();
				}

			}
		}

		return F.NIL;
	}
	
	@Override
	public boolean checkRHSCondition(EvalEngine engine) {
		IPatternMap patternMap = getPatternMap();

		if (patternMap.isAllPatternsAssigned()) {
			IExpr result = patternMap.substituteSymbols(fRightHandSide);
			if (result.isPresent()) {
				fReplaceList.append(result);
				return false;
			}
		}
		return super.checkRHSCondition(engine);
	}
}
