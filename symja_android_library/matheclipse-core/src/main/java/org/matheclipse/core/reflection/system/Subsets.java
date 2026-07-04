package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Subsets extends AbstractFunctionEvaluator {

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isAST()) {
      final IAST f = (IAST) ast.arg1();
      int n = f.argSize();

      IntRangeSpec range = IntRangeSpec.createNonNegative(ast, 2);
      if (range == null) {
        return F.NIL;
      }

      int min = range.minimum();
      int max = range.maximum();
      int step = range.step();

      if (max > n) {
        max = n;
      }

      // Fast calculation of the total subset space utilizing Binomial coefficient mathematics
      IInteger total = F.C0;
      if (min <= max) {
        for (int k = min; k <= max; k += step) {
          total = total.add((IInteger) F.Binomial.of(F.ZZ(n), F.ZZ(k)));
        }
      }

      long T_long = Long.MAX_VALUE;
      if (total.compareTo(F.ZZ(Long.MAX_VALUE)) <= 0) {
        T_long = total.toLongDefault();
      }

      long start = 1;
      long end = T_long;
      long sliceStep = 1;

      // Parse the third argument (subset index constraints) against the calculated bounds
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (arg3.isList1()) {
          long s = arg3.first().toLongDefault();
          if (s < 0)
            s = T_long + s + 1;
          start = s;
          end = s;
        } else if (arg3.isList2()) {
          long s1 = arg3.first().toLongDefault();
          long s2 = arg3.second().toLongDefault();
          if (s1 < 0)
            s1 = T_long + s1 + 1;
          if (s2 < 0)
            s2 = T_long + s2 + 1;
          start = s1;
          end = s2;
        } else if (arg3.isList3()) {
          IAST list = (IAST) arg3;
          long s1 = list.arg1().toLongDefault();
          long s2 = list.arg2().toLongDefault();
          long st = list.arg3().toLongDefault();
          if (s1 < 0)
            s1 = T_long + s1 + 1;
          if (s2 < 0)
            s2 = T_long + s2 + 1;
          start = s1;
          end = s2;
          sliceStep = st;
        } else if (arg3.isInteger()) {
          long s = arg3.toLongDefault();
          if (s < 0) {
            start = T_long + s + 1;
            end = T_long;
          } else {
            start = 1;
            end = s;
          }
        } else {
          return Errors.printMessage(S.Subsets, "seq", F.List(F.C3, ast), engine);
        }
      }

      if (sliceStep == 0) {
        return Errors.printMessage(S.Subsets, "seq", F.List(F.C3, ast), engine);
      }

      long expectedSize = 0;
      if (sliceStep > 0 && start <= end) {
        expectedSize = (end - start) / sliceStep + 1;
      } else if (sliceStep < 0 && start >= end) {
        expectedSize = (start - end) / (-sliceStep) + 1;
      }

      if (expectedSize == 0) {
        return F.CEmptyList;
      }
      if (expectedSize > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt((int) expectedSize);
      }

      IASTAppendable result = F.ast(S.List, (int) expectedSize + 1);
      int iterationLimit = engine.getIterationLimit();
      long iterationCounter = 0;

      // Lazy Generation: Compute and unrank ONLY the required indices
      for (long i = 0; i < expectedSize; i++) {
        if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
          IterationLimitExceeded.throwIt((int) iterationCounter, ast);
        }

        long currentGlobalIndex = start + i * sliceStep;
        result.append(getSubsetByRank(f, n, min, max, step, currentGlobalIndex));
      }

      return result;
    }
    return F.NIL;
  }

  /**
   * Direct algebraic unranking of a specific combination (combinadic derivation). Locates exactly
   * the targetRank-th subset natively without iterating prior elements.
   */
  private static IAST getSubsetByRank(IAST f, int n, int min, int max, int step, long targetRank) {
    IInteger currentTotal = F.C0;
    int targetK = -1;
    IInteger rankInK = F.C0;
    IInteger targetR = F.ZZ(targetRank);

    // 1. Locate which subset length 'k' the requested rank falls into
    for (int k = min; k <= max; k += step) {
      IInteger count = (IInteger) F.Binomial.of(F.ZZ(n), F.ZZ(k));
      IInteger nextTotal = currentTotal.add(count);
      if (targetR.compareTo(nextTotal) <= 0) {
        targetK = k;
        rankInK = targetR.subtract(currentTotal).subtract(F.C1); // Map to 0-based index
        break;
      }
      currentTotal = nextTotal;
    }

    if (targetK == -1) {
      return F.CEmptyList;
    }

    IASTAppendable subset = F.ast(f.head(), targetK + 1);
    int currentItem = 1;
    IInteger r = rankInK;

    // 2. Unrank the exact combinatorial composition within size 'targetK'
    for (int i = 0; i < targetK; i++) {
      while (currentItem <= n) {
        IInteger count = (IInteger) F.Binomial.of(F.ZZ(n - currentItem), F.ZZ(targetK - 1 - i));
        if (r.compareTo(count) < 0) {
          // If the remaining rank is smaller than permutations skipping this item, it must be
          // included
          subset.append(f.get(currentItem));
          currentItem++;
          break;
        } else {
          // Otherwise, skip the item and subtract those skipped permutations from the rank
          r = r.subtract(count);
          currentItem++;
        }
      }
    }

    return subset;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}