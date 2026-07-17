package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeyMap extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      ast = F.operatorForm1Append(ast);
      if (ast.isNIL()) {
        return F.NIL;
      }
    }

    if (ast.isAST2()) {
      IExpr arg1 = ast.arg1(); // the function f
      IExpr arg2 = ast.arg2(); // the association or list of rules

      if (arg2.isAssociation()) {
        IAssociation assoc = (IAssociation) arg2;
        IAssociation result = F.assoc();
        for (int i = 1; i <= assoc.argSize(); i++) {
          IAST rule = assoc.getRule(i);
          IExpr key = rule.arg1(); // Use the raw key from the rule instead of assoc.getKey(i)
          IExpr value = rule.arg2();
          IExpr newKey = engine.evaluate(F.unaryAST1(arg1, key));
          result.appendRule(F.Rule(newKey, value));
        }
        return result;
      } else if (arg2.isListOfRules(false)) {
        IAST list = (IAST) arg2;
        IASTAppendable result = list.copyHead();
        for (int i = 1; i <= list.argSize(); i++) {
          IAST rule = (IAST) list.get(i);
          IExpr key = rule.arg1();
          IExpr value = rule.arg2();
          IExpr newKey = engine.evaluate(F.unaryAST1(arg1, key));
          result.append(F.binaryAST2(rule.head(), newKey, value));
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