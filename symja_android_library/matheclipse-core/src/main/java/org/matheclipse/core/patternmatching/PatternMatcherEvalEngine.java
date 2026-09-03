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
    copyBaseFieldsTo(v);
    v.fEngine = fEngine;
    return v;
  }

  @Override
  public boolean test(final IExpr leftHandSide) {
    return test(leftHandSide, fEngine);
  }
}
