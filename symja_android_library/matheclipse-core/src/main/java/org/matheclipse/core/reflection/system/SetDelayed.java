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

	public final static SetDelayed CONST = new SetDelayed();

	public SetDelayed() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		final IExpr leftHandSide = ast.arg1();
		final IExpr rightHandSide = ast.arg2();

		createPatternMatcher(leftHandSide, rightHandSide, EvalEngine.get().isPackageMode());

		return F.Null;
	}

	@Override
	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) throws RuleCreationError {
		if (leftHandSide.isAST() && (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
			leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, EvalEngine.get());
		}
		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

			result[0] = lhsSymbol.putDownRule(F.SetDelayed, false, leftHandSide, rightHandSide, packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(F.SetDelayed, true, leftHandSide, rightHandSide, packageMode);
			return result;
		}
		throw new RuleCreationError(leftHandSide);
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
