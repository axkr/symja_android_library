package org.matheclipse.core.patternmatching;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

public final class PatternMatcherEvalEngine extends PatternMatcher {
  EvalEngine fEngine;

  public PatternMatcherEvalEngine() {
    super();
  }

  public PatternMatcherEvalEngine(IExpr patternExpr, EvalEngine engine) {
    super(patternExpr);
    fEngine = engine;
  }

  @Override
  public IPatternMatcher copy() {
    PatternMatcherEvalEngine v = new PatternMatcherEvalEngine();
    v.fLHSPriority = fLHSPriority;
    v.fThrowIfTrue = fThrowIfTrue;
    v.fLhsPatternExpr = fLhsPatternExpr;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    v.fEngine = fEngine;
    return v;
  }

  @Override
  public boolean test(final IExpr leftHandSide) {
    return test(leftHandSide, fEngine);
  }
}
