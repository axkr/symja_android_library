package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class MaximalBy extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();
    if (argSize < 2) {
      return F.NIL;
    }
    IExpr dataArg = ast.arg1();
    if (!dataArg.isASTOrAssociation()) {
      return F.NIL;
    }
    IAST data = (IAST) dataArg;
    final boolean isAssociation = data.isAssociation();
    int size = data.argSize();
    if (size == 0) {
      return data.copyHead(0);
    }
    IExpr f = ast.arg2();

    List<IExpr> fValues = new ArrayList<>(size);
    for (int i = 1; i <= size; i++) {
      fValues.add(engine.evaluate(F.unaryAST1(f, data.get(i))));
    }

    if (argSize == 2) {
      return maximalElements(data, fValues, Comparators.CANONICAL_COMPARATOR, isAssociation);
    }

    final Comparator<IExpr> comparator;
    if (argSize >= 4) {
      IExpr p = ast.arg4();
      comparator = (a, b) -> {
        IExpr res = engine.evaluate(F.binaryAST2(p, a, b));
        if (res.isTrue())
          return -1;
        if (res.isFalse())
          return 1;
        return 0;
      };
    } else {
      comparator = Comparators.CANONICAL_COMPARATOR;
    }

    IExpr nArg = ast.arg3();
    boolean upTo = nArg.isAST(S.UpTo, 2);
    int n = upTo ? nArg.first().toIntDefault(-1) : nArg.toIntDefault(-1);
    if (n < 0)
      return F.NIL;
    n = Math.min(n, size);

    Integer[] indices = new Integer[size];
    for (int i = 0; i < size; i++)
      indices[i] = i;
    final List<IExpr> fVals = fValues;
    final Comparator<IExpr> comp = comparator;
    // Sort descending by f value; stable sort preserves original order for ties
    Arrays.sort(indices, (a, b) -> {
      int c = comp.compare(fVals.get(b), fVals.get(a)); // reversed for max
      if (c != 0)
        return c;
      return Integer.compare(a, b);
    });

    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      result.append(data.get(indices[i] + 1));
    }
    return result;
  }

  private static IExpr maximalElements(IAST data, List<IExpr> fValues,
      Comparator<IExpr> comparator, boolean isAssociation) {
    IExpr maxVal = fValues.get(0);
    for (int i = 1; i < fValues.size(); i++) {
      IExpr v = fValues.get(i);
      if (comparator.compare(v, maxVal) > 0) {
        maxVal = v;
      }
    }

    if (isAssociation) {
      IASTAppendable result = data.copyHead(fValues.size());
      for (int i = 0; i < fValues.size(); i++) {
        if (comparator.compare(fValues.get(i), maxVal) == 0) {
          result.appendRule(((IAssociation) data).getRule(i + 1));
        }
      }
      return result;
    }

    IASTAppendable result = F.ListAlloc(fValues.size());
    for (int i = 0; i < fValues.size(); i++) {
      if (comparator.compare(fValues.get(i), maxVal) == 0) {
        result.append(data.get(i + 1));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_4_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
