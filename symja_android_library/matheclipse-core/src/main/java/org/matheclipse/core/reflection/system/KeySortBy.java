package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeySortBy extends AbstractFunctionEvaluator {

  private static class KeyEvaluation {
    final IAST rule;
    final IExpr evaluatedKey;
    final int originalIndex;

    KeyEvaluation(IAST rule, IExpr evaluatedKey, int originalIndex) {
      this.rule = rule;
      this.evaluatedKey = evaluatedKey;
      this.originalIndex = originalIndex;
    }
  }

  /**
   * Sort the rules stably by the value of <code>f(key)</code>.
   *
   * @param rules a list of rules
   * @param f the function applied to the keys
   * @param engine the evaluation engine
   */
  private static KeyEvaluation[] sortedByKey(IAST rules, IExpr f, EvalEngine engine) {
    int size = rules.argSize();
    KeyEvaluation[] evals = new KeyEvaluation[size];

    for (int i = 1; i <= size; i++) {
      IAST rule = (IAST) rules.get(i);
      IExpr key = rule.arg1();
      IExpr evaluatedKey = engine.evaluate(F.unaryAST1(f, key));
      evals[i - 1] = new KeyEvaluation(rule, evaluatedKey, i);
    }

    Arrays.sort(evals, (a, b) -> {
      int cmp = a.evaluatedKey.compareTo(b.evaluatedKey);
      if (cmp == 0) {
        // ensure stable sort on ties
        return Integer.compare(a.originalIndex, b.originalIndex);
      }
      return cmp;
    });
    return evals;
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      ast = F.operatorForm1Append(ast);
      if (ast.isNIL()) {
        return F.NIL;
      }
    }

    if (ast.isAST2()) {
      IExpr arg1 = ast.arg1(); // the association or list of rules
      IExpr arg2 = ast.arg2(); // the sorting function f

      if (arg1.isAssociation()) {
        KeyEvaluation[] evals = sortedByKey(((IAssociation) arg1).normal(false), arg2, engine);
        IAssociation result = F.assoc();
        for (int i = 0; i < evals.length; i++) {
          result.appendRule(evals[i].rule);
        }
        return result;

      } else if (arg1.isListOfRules(false)) {
        IAST list = (IAST) arg1;
        KeyEvaluation[] evals = sortedByKey(list, arg2, engine);
        IASTAppendable result = list.copyHead();
        for (int i = 0; i < evals.length; i++) {
          result.append(evals[i].rule);
        }
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2_1;
  }
}
