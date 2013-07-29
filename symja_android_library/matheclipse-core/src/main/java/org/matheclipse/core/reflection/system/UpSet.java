package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class UpSet implements IFunctionEvaluator, ICreatePatternMatcher {
	public UpSet() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		final IExpr leftHandSide = ast.get(1);
		IExpr rightHandSide = ast.get(2);
		if (leftHandSide.isList()) {
			// thread over lists
			try {
				rightHandSide = F.eval(rightHandSide);
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}
			IExpr temp = EvalEngine.threadASTListArgs(F.UpSet(leftHandSide, rightHandSide));
			if (temp != null) {
				return F.eval(temp);
			}
		}
		Object[] result = createPatternMatcher(leftHandSide, rightHandSide);
		return (IExpr) result[1];
	}

	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide) throws RuleCreationError {
		final Object[] result = new Object[2];
		final EvalEngine engine = EvalEngine.get();

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

		// if (leftHandSide.isSymbol()) {
		// final ISymbol lhsSymbol = (ISymbol) leftHandSide;
		//
		// if (lhsSymbol.hasLocalVariableStack()) {
		// lhsSymbol.set(rightHandSide);
		// return result;
		// } else {
		// result[0] = lhsSymbol.putUpRule(F.UpSet, true, leftHandSide,
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
			result[0] = lhsSymbol.putUpRule(F.UpSet, false, lhsAST, rightHandSide);
		}
		return result;
		// }
		//
		// throw new RuleCreationError(leftHandSide);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}