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
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class Set implements IFunctionEvaluator, ICreatePatternMatcher {
	public final static Set CONST = new Set();

	public Set() {
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
			IExpr temp = EvalEngine.threadASTListArgs(F.Set(leftHandSide, rightHandSide));
			if (temp != null) {
				return F.eval(temp);
			}
		}
		Object[] result;
		// if (rightHandSide.isCondition()) {
		// result = createPatternMatcher(leftHandSide, ((IAST)
		// rightHandSide).get(1), ((IAST) rightHandSide).get(2), null);
		// } else if (rightHandSide.isModule()) {
		// IAST module = (IAST) rightHandSide;
		// if (module.get(2).isCondition()) {
		// IAST condition = (IAST) module.get(2);
		// result = createPatternMatcher(leftHandSide, condition.get(1),
		// condition.get(2), module.get(1));
		// } else {
		// result = createPatternMatcher(leftHandSide, rightHandSide, null, null);
		// }
		// } else {
		result = createPatternMatcher(leftHandSide, rightHandSide, EvalEngine.get().isPackageMode());
		// }
		return (IExpr) result[1];
	}

	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) throws RuleCreationError {

		final EvalEngine engine = EvalEngine.get();
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

		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
			result[0] = lhsSymbol.putDownRule(F.Set, false, leftHandSide, rightHandSide, packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;

			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(F.Set, true, leftHandSide, rightHandSide, packageMode);
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