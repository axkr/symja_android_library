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

public class MinimalBy extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return extremalBy(ast, engine, false);
  }

  /**
   * <code>MinimalBy(data, f, ...)</code> or, with <code>max</code>, <code>MaximalBy(data, f,
   * ...)</code>: the elements of <code>data</code> - a list or an association - with the smallest or
   * largest values of <code>f</code>, or the first <code>n</code> of them ordered by
   * <code>f</code>, optionally with the ordering function <code>p</code>.
   */
  static IExpr extremalBy(final IAST ast, EvalEngine engine, boolean max) {
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

    // Compute f[ei] for each element.
    // For associations, data.get(i) returns the value (not the rule), which is correct.
    List<IExpr> fValues = new ArrayList<>(size);
    for (int i = 1; i <= size; i++) {
      fValues.add(engine.evaluate(F.unaryAST1(f, data.get(i))));
    }

    if (argSize == 2) {
      return extremalElements(data, fValues, Comparators.CANONICAL_COMPARATOR, isAssociation,
          max);
    }

    // argSize >= 3: f(data, f, n) or f(data, f, n, p)
    final Comparator<IExpr> comparator;
    if (argSize >= 4) {
      IExpr p = ast.arg4();
      comparator = (a, b) -> {
        IExpr res = engine.evaluate(F.binaryAST2(p, a, b));
        if (res.isTrue()) {
          return -1;
        }
        if (res.isFalse()) {
          return 1;
        }
        return 0;
      };
    } else {
      comparator = Comparators.CANONICAL_COMPARATOR;
    }

    // Handle n argument (arg3), which may be UpTo[n]
    IExpr nArg = ast.arg3();
    boolean upTo = nArg.isAST(S.UpTo, 2);
    int n = upTo ? nArg.first().toIntDefault(-1) : nArg.toIntDefault(-1);
    if (n < 0) {
      return F.NIL;
    }
    n = Math.min(n, size);

    // Sort indices by f value, descending for the maximum; stable sort preserves original order
    // for ties
    Integer[] indices = new Integer[size];
    for (int i = 0; i < size; i++) {
      indices[i] = i;
    }
    final List<IExpr> fVals = fValues;
    final Comparator<IExpr> comp = comparator;
    Arrays.sort(indices, (a, b) -> {
      int c = max ? comp.compare(fVals.get(b), fVals.get(a))
          : comp.compare(fVals.get(a), fVals.get(b));
      return c != 0 ? c : Integer.compare(a, b);
    });

    return buildResult(data, indices, n, isAssociation);
  }

  /**
   * Returns all elements of {@code data} whose f-value equals the minimum, or with {@code max} the
   * maximum, f-value, preserving original order. Returns an association if {@code data} is an
   * association.
   */
  private static IExpr extremalElements(IAST data, List<IExpr> fValues,
      Comparator<IExpr> comparator, boolean isAssociation, boolean max) {
    IExpr extremalVal = fValues.get(0);
    for (int i = 1; i < fValues.size(); i++) {
      IExpr v = fValues.get(i);
      int c = comparator.compare(v, extremalVal);
      if (max ? c > 0 : c < 0) {
        extremalVal = v;
      }
    }

    if (isAssociation) {
      IASTAppendable result = data.copyHead(fValues.size());
      for (int i = 0; i < fValues.size(); i++) {
        if (comparator.compare(fValues.get(i), extremalVal) == 0) {
          result.appendRule(((IAssociation) data).getRule(i + 1));
        }
      }
      return result;
    }

    IASTAppendable result = F.ListAlloc(fValues.size());
    for (int i = 0; i < fValues.size(); i++) {
      if (comparator.compare(fValues.get(i), extremalVal) == 0) {
        result.append(data.get(i + 1));
      }
    }
    return result;
  }

  /**
   * Builds the result list/association from the given sorted {@code indices}, taking the first
   * {@code n}.
   */
  private static IExpr buildResult(IAST data, Integer[] indices, int n, boolean isAssociation) {
    if (isAssociation) {
      IAssociation result = (IAssociation) data.copyHead(n);
      for (int i = 0; i < n; i++) {
        result.appendRule(((IAssociation) data).getRule(indices[i] + 1));
      }
      return result;
    }

    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      result.append(data.get(indices[i] + 1));
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
