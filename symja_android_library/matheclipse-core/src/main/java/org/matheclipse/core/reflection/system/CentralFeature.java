package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CentralFeature extends AbstractFunctionOptionEvaluator {

  public CentralFeature() {
    super();
  }

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    IExpr arg1 = ast.arg1();
    IAST dataList = F.NIL;
    IAST valuesList = F.NIL;

    // Check if the input is a rule: data -> values
    if (arg1.isRule()) {
      dataList = arg1.first().makeList();
      valuesList = arg1.second().makeList();
    } else if (arg1.isList()) {
      IAST list = (IAST) arg1;
      // Check if it is a list of rules: {x1 -> v1, x2 -> v2, ...}
      if (list.argSize() > 0 && list.arg1().isRule()) {
        IASTAppendable dataAppender = F.ListAlloc(list.argSize());
        IASTAppendable valuesAppender = F.ListAlloc(list.argSize());
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr arg = list.get(i);
          if (arg.isRule()) {
            dataAppender.append(arg.first());
            valuesAppender.append(arg.second());
          } else {
            return F.NIL;
          }
        }
        dataList = dataAppender;
        valuesList = valuesAppender;
      } else {
        dataList = list;
      }
    }

    if (!dataList.isPresent() || dataList.argSize() == 0) {
      if (!arg1.isList() && !arg1.isRule()) {
        return Errors.printMessage(S.CentralFeature, "near1", F.List(arg1), engine);
      }
      return F.NIL;
    }

    // Determine the distance metric
    IExpr distFunc = S.EuclideanDistance;
    if (options[0].equals(S.Automatic)) {
      IExpr first = dataList.arg1();
      if (first.isString()) {
        distFunc = S.EditDistance;
      } else if (first.isTrue() || first.isFalse()) {
        distFunc = S.JaccardDissimilarity;
      }
    } else {
      distFunc = options[0];
    }

    IExpr bestElement = F.NIL;
    IExpr minSum = F.NIL;
    int bestIndex = -1;

    int size = dataList.argSize();
    for (int i = 1; i <= size; i++) {
      IExpr xi = dataList.get(i);
      IASTAppendable sumAppender = F.PlusAlloc(size);

      for (int j = 1; j <= size; j++) {
        if (i == j) {
          continue;
        }
        IExpr xj = dataList.get(j);
        IExpr dist = F.binaryAST2(distFunc, xi, xj);
        sumAppender.append(dist);
      }
      IExpr evalSum = engine.evaluate(sumAppender);

      if (minSum.isPresent()) {
        IExpr less = engine.evaluate(F.Less(evalSum, minSum));
        if (less.isTrue()) {
          minSum = evalSum;
          bestElement = xi;
          bestIndex = i;
        }
      } else {
        minSum = evalSum;
        bestElement = xi;
        bestIndex = i;
      }
    }

    if (bestIndex == -1) {
      return F.NIL;
    }

    // Return mapping if valuesList is present (e.g., from Rule or List of Rules)
    if (valuesList.isPresent()) {
      if (bestIndex <= valuesList.argSize()) {
        return valuesList.get(bestIndex);
      }
    }

    return bestElement;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.PROTECTED);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.DistanceFunction}, //
        new IExpr[] {S.Automatic});
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}