package org.matheclipse.core.builtin.function;

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
import org.matheclipse.core.patternmatching.PatternMatcher;

public class UpSet extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {
	public UpSet() {
	}

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
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}