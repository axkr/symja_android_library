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
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class Set extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {
	public final static Set CONST = new Set();

	public Set() {
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
	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode, final EvalEngine engine) throws RuleCreationError {

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

		return putDownRule(leftHandSide, rightHandSide, packageMode);
	}

	public Object[] putDownRule(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
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

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}