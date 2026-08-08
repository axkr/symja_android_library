package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
import org.matheclipse.core.builtin.AssociationFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeyComplement extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList()) {
      return F.NIL;
    }
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
    if (associations.length == 0) {
      return F.assoc();
    }

    // 1. Collect the keys of all associations except the first one
    Set<IExpr> excludedKeys = new HashSet<>();
    for (int i = 1; i < associations.length; i++) {
      IAssociation assoc = associations[i];
      for (int j = 1; j <= assoc.argSize(); j++) {
        excludedKeys.add(assoc.getRule(j).arg1());
      }
    }

    // 2. Keep the rules of the first association whose key isn't excluded
    IAssociation first = associations[0];
    IAssociation result = F.assoc();
    for (int j = 1; j <= first.argSize(); j++) {
      if (!excludedKeys.contains(first.getRule(j).arg1())) {
        result.appendRule(first.getRule(j));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
