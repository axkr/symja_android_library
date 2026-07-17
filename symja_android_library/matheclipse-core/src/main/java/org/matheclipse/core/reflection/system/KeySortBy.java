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
        IAssociation assoc = (IAssociation) arg1;
        int size = assoc.argSize();
        KeyEvaluation[] evals = new KeyEvaluation[size];

        for (int i = 1; i <= size; i++) {
          IAST rule = assoc.getRule(i);
          IExpr key = rule.arg1();
          IExpr evaluatedKey = engine.evaluate(F.unaryAST1(arg2, key));
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

        IAssociation result = F.assoc();
        for (int i = 0; i < size; i++) {
          result.appendRule(evals[i].rule);
        }
        return result;

      } else if (arg1.isListOfRules(false)) {
        IAST list = (IAST) arg1;
        int size = list.argSize();
        KeyEvaluation[] evals = new KeyEvaluation[size];

        for (int i = 1; i <= size; i++) {
          IAST rule = (IAST) list.get(i);
          IExpr key = rule.arg1();
          IExpr evaluatedKey = engine.evaluate(F.unaryAST1(arg2, key));
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

        IASTAppendable result = list.copyHead();
        for (int i = 0; i < size; i++) {
          result.append(evals[i].rule);
        }
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}