package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class KeyMemberQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST2()) {
      return hasKey(ast, engine);
    }
    return F.NIL;
  }

  /**
   * Whether the association or list of rules <code>ast.arg1()</code> has a key which matches the
   * pattern <code>ast.arg2()</code>: <code>KeyMemberQ(&lt;|x->1,y->2|&gt;, _)</code> is
   * <code>True</code>.
   *
   * @return {@link F#NIL} if the first argument is neither an association nor a list of rules
   */
  static IExpr hasKey(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
    if (arg1.isAssociation()) {
      IAssociation assoc = (IAssociation) arg1;
      for (int i = 1; i <= assoc.argSize(); i++) {
        if (matcher.test(assoc.getRule(i).arg1())) {
          return S.True;
        }
      }
      return S.False;
    } else if (arg1.isListOfRules(false)) {
      IAST list = (IAST) arg1;
      for (int i = 1; i <= list.argSize(); i++) {
        if (matcher.test(((IAST) list.get(i)).arg1())) {
          return S.True;
        }
      }
      return S.False;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2_1;
  }
}
