package org.matheclipse.core.patternmatching;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class PatternMatcherList extends PatternMatcherAndEvaluator {
	IASTAppendable fReplaceList;

	public IASTAppendable getReplaceList() {
		return fReplaceList;
	}

	public PatternMatcherList(final int setSymbol, final IExpr leftHandSide, final IExpr rightHandSide) {
		super(setSymbol, leftHandSide, rightHandSide, true, 0);
		fReplaceList = F.ListAlloc();
	}

	protected IExpr replaceSubExpressionOrderlessFlat(final IAST lhsPatternAST, final IAST lhsEvalAST,
			final IExpr rhsExpr, EvalEngine engine) {
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
