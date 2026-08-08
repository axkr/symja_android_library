package org.matheclipse.core.reflection.system;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.builtin.AssociationFunctions;
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
      IAssociation[] associations = new IAssociation[list.argSize()];
      for (int i = 1; i <= list.argSize(); i++) {
        IAssociation assoc = AssociationFunctions.toAssociation(list.get(i));
        if (assoc == null) {
          // All elements must be associations or lists of rules
          return F.NIL;
        }
        associations[i - 1] = assoc;
      }

      // 1. Collect all keys across all associations in first-appearance order
      Set<IExpr> allKeys = new LinkedHashSet<>();
      for (int i = 0; i < associations.length; i++) {
        IAssociation assoc = associations[i];
        for (int j = 1; j <= assoc.argSize(); j++) {
          allKeys.add(assoc.getRule(j).arg1());
        }
      }

      // 2. Pad each association individually
      IASTAppendable resultList = F.ListAlloc(associations.length);
      for (int i = 0; i < associations.length; i++) {
        IAssociation assoc = associations[i];
        IAssociation newAssoc = F.assoc();
        for (IExpr key : allKeys) {
          IAST rule = assoc.getRule(key);
          if (rule.isPresent()) {
            newAssoc.appendRule(rule);
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
