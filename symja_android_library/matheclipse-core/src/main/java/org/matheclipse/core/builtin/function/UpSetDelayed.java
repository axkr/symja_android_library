package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class UpSetDelayed extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

	public UpSetDelayed() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		final IExpr leftHandSide = ast.arg1();
		final IExpr rightHandSide = ast.arg2();

		createPatternMatcher(leftHandSide, rightHandSide, false);

		return F.Null;
	}

	@Override
	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) throws RuleCreationError {
		final Object[] result = new Object[2];
		final EvalEngine engine = EvalEngine.get();

		if (leftHandSide.isAST() && (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
			leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
		}
		result[0] = null;
		result[1] = rightHandSide;
		// if (leftHandSide.isSymbol()) {
		// final ISymbol lhsSymbol = (ISymbol) leftHandSide;
		// if (lhsSymbol.hasLocalVariableStack()) {
		// lhsSymbol.set(rightHandSide);
		// return result;
		// } else {
		// result[0] = lhsSymbol.putUpRule(F.UpSetDelayed, true, leftHandSide,
		// rightHandSide);
		// return result;
		// }
		// }

		IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
		// if (leftHandSide.isAST()) {

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
		// }

		// throw new RuleCreationError(leftHandSide);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
