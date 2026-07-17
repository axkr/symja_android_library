package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeyUnion extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr missing = F.Function(F.Missing(S.KeyAbsent, F.Slot1));
    if (ast.isAST2()) {
      missing = ast.arg2();
    }
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      Set<IExpr> allKeys = new LinkedHashSet<>();

      // 1. Collect all keys across all associations in first-appearance order
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr item = list.get(i);
        if (item.isAssociation()) {
          IAssociation assoc = (IAssociation) item;
          for (int j = 1; j <= assoc.argSize(); j++) {
            allKeys.add(assoc.getRule(j).arg1());
          }
        } else if (item.isListOfRules(false)) {
          IAST rules = (IAST) item;
          for (int j = 1; j <= rules.argSize(); j++) {
            allKeys.add(((IAST) rules.get(j)).arg1());
          }
        } else {
          // All elements must be associations or lists of rules
          return F.NIL;
        }
      }

      // 2. Pad each association individually
      IASTAppendable resultList = F.ListAlloc(list.argSize());
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr item = list.get(i);
        IAssociation newAssoc = F.assoc();
        Map<IExpr, IExpr> currentMap = new HashMap<>();

        if (item.isAssociation()) {
          IAssociation assoc = (IAssociation) item;
          for (int j = 1; j <= assoc.argSize(); j++) {
            IAST rule = assoc.getRule(j);
            currentMap.putIfAbsent(rule.arg1(), rule.arg2());
          }
        } else if (item.isListOfRules(false)) {
          IAST rules = (IAST) item;
          for (int j = 1; j <= rules.argSize(); j++) {
            IAST rule = (IAST) rules.get(j);
            currentMap.putIfAbsent(rule.arg1(), rule.arg2());
          }
        }

        for (IExpr key : allKeys) {
          IExpr val = currentMap.get(key);
          if (val != null) {
            newAssoc.appendRule(F.Rule(key, val));
          } else {
            newAssoc.appendRule(F.Rule(key, F.unaryAST1(missing, key)));
          }
        }
        resultList.append(newAssoc);
      }
      return resultList;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
