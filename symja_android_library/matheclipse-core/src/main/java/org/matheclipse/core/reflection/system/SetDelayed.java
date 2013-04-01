package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class SetDelayed implements IFunctionEvaluator, ICreatePatternMatcher {

	public SetDelayed() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		final IExpr leftHandSide = ast.get(1);
		final IExpr rightHandSide = ast.get(2);

		createPatternMatcher(leftHandSide, rightHandSide);

		return F.Null;
	}

	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide) throws RuleCreationError {
		final Object[] result = new Object[2];
		final EvalEngine engine = EvalEngine.get();

		leftHandSide = PatternMatcher.evalLeftHandSide(leftHandSide, engine);

		result[0] = null;
		result[1] = rightHandSide;
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			} else {
				result[0] = lhsSymbol.putDownRule(F.SetDelayed, true, leftHandSide, rightHandSide);
				return result;
			}
		}

		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

			result[0] = lhsSymbol.putDownRule(F.SetDelayed, false, leftHandSide, rightHandSide);
			return result;
		}

		throw new RuleCreationError(leftHandSide);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
