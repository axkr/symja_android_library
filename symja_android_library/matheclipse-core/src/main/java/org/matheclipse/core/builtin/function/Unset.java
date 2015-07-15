package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class Unset extends AbstractCoreFunctionEvaluator {
	public final static Unset CONST = new Unset();

	public Unset() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		final IExpr leftHandSide = ast.arg1();
		if (leftHandSide.isList()) {
			// thread over lists
			IExpr temp = EvalEngine.threadASTListArgs(F.Unset(leftHandSide));
			if (temp != null) {
				return F.eval(temp);
			}
		}
		removePatternMatcher(leftHandSide, EvalEngine.get().isPackageMode());
		return F.Null;
	}

	public void removePatternMatcher(IExpr leftHandSide, boolean packageMode) throws RuleCreationError {

		final EvalEngine engine = EvalEngine.get();
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
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}