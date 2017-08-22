package org.matheclipse.core.patternmatching;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

public class PatternMatcherEvalEngine extends PatternMatcher {
	EvalEngine fEngine;

	public PatternMatcherEvalEngine(IExpr patternExpr, EvalEngine engine) {
		super(patternExpr);
		fEngine = engine;
	}
	
	@Override
	public boolean test(final IExpr leftHandSide) {
		return test(leftHandSide, fEngine);
	}
}
