package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>EntityList</code> implementation.
 */
public class EntityList extends AbstractFunctionEvaluator {

  public EntityList() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      IExpr arg1 = ast.arg1();

      // Handle EntityList["Element"] -> Returns all 118 elements
      if (arg1.isString() && arg1.toString().replace("\"", "").equals("Element")) {
        return getAllElements(engine);
      }

      // Handle EntityList[EntityClass[type, class]]
      if (arg1.isAST(S.EntityClass, 3)) {
        IAST entityClass = (IAST) arg1;
        IExpr type = entityClass.arg1();
        IExpr className = entityClass.arg2();

        if (type.isString() && type.toString().replace("\"", "").equals("Element")) {
          return getElementEntityList(className.toString().replace("\"", "").toLowerCase(), engine);
        }
      }
    }
    return F.NIL;
  }

  private IExpr getAllElements(EvalEngine engine) {
    IASTAppendable result = F.ListAlloc();
    for (int z = 1; z <= 118; z++) {
      IExpr name = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("StandardName")));
      if (name.isString()) {
        result.append(F.Entity(F.stringx("Element"), name));
      }
    }
    return result;
  }

  private IExpr getElementEntityList(String cName, EvalEngine engine) {
    cName = cName.replace(" ", ""); // Normalize spaces
    IASTAppendable result = F.ListAlloc();

    for (int z = 1; z <= 118; z++) {
      boolean match = false;

      if (cName.startsWith("period")) {
        IExpr period = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("Period")));
        if (period.isInteger() && cName.equals("period" + period.toString())) {
          match = true;
        }
      } else if (cName.startsWith("group")) {
        IExpr group = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("Group")));
        if (group.isInteger() && cName.equals("group" + group.toString())) {
          match = true;
        }
      } else if (cName.endsWith("block")) {
        IExpr block = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("Block")));
        if (block.isString()) {
          String bStr = block.toString().replace("\"", "").toLowerCase() + "block";
          if (cName.equals(bStr)) {
            match = true;
          }
        }
      } else {
        IExpr series = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("Series")));
        if (series.isString()) {
          String sStr = series.toString().replace("\"", "").toLowerCase();
          // Normalize names like "TransitionMetals" vs "TransitionMetal"
          if (cName.equals(sStr) || cName.equals(sStr + "s") || cName.equals(sStr + "es")
              || cName.replace("metals", "metal").equals(sStr)) {
            match = true;
          }
        }
      }

      if (match) {
        IExpr name = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx("StandardName")));
        if (name.isString()) {
          result.append(F.Entity(F.stringx("Element"), name));
        }
      }
    }

    if (result.argSize() > 0) {
      return result;
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }
}