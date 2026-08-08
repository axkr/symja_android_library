package org.matheclipse.core.reflection.system;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.builtin.AssociationFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class KeyIntersection extends AbstractFunctionEvaluator {

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
      return F.CEmptyList;
    }

    // 1. The keys of the first association determine the order of the common keys
    Set<IExpr> commonKeys = new LinkedHashSet<>();
    IAssociation first = associations[0];
    for (int j = 1; j <= first.argSize(); j++) {
      commonKeys.add(first.getRule(j).arg1());
    }

    // 2. Retain only the keys which are present in every association
    for (int i = 1; i < associations.length && !commonKeys.isEmpty(); i++) {
      final IAssociation assoc = associations[i];
      commonKeys.removeIf(key -> !assoc.isKey(key));
    }

    // 3. Restrict every association to the common keys, keeping its own values
    IASTAppendable resultList = F.ListAlloc(associations.length);
    for (int i = 0; i < associations.length; i++) {
      IAssociation assoc = associations[i];
      IAssociation newAssoc = F.assoc();
      for (IExpr key : commonKeys) {
        newAssoc.appendRule(assoc.getRule(key));
      }
      resultList.append(newAssoc);
    }
    return resultList;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
