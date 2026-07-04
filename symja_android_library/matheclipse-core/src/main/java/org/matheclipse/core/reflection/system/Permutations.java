package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.KPermutationsIterable;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Permutations extends AbstractFunctionEvaluator {

  private static final class KPermutationsList implements Iterable<IAST> {

    private class KPermutationsIterator implements Iterator<IAST> {

      private final Iterator<int[]> fIterable;

      private KPermutationsIterator() {
        this.fIterable = new KPermutationsIterable(fList, fParts, fOffset).iterator();
      }

      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }

      @Override
      public IAST next() {
        int[] permutationsIndex = fIterable.next();
        if (permutationsIndex == null) {
          return null;
        }
        IASTAppendable temp = fResultList.copyAppendable();
        for (int i = 0; i < fParts; i++) {
          temp.append(fList.get(permutationsIndex[i] + fOffset));
        }
        return temp;
      }
    }

    private final IAST fList;
    private final IAST fResultList;
    private final int fOffset;
    private final int fParts;

    public KPermutationsList(final IAST list, final int parts, IAST resultList, final int offset) {
      fList = list;
      fResultList = resultList;
      fOffset = offset;
      fParts = parts;
    }

    @Override
    public Iterator<IAST> iterator() {
      return new KPermutationsIterator();
    }
  }

  private IAST createPermutationsWithNParts(final IAST list, int parts,
      final IASTAppendable result) {
    if (parts == 0) {
      result.append(F.List());
      return result;
    }
    if (list.size() <= 2) {
      if (list.isAST1()) {
        result.append(list);
      }
      return result;
    }

    final KPermutationsList perm = new KPermutationsList(list, parts, F.ast(list.head()), 1);
    Set<IAST> set = new HashSet<IAST>();
    for (IAST temp : perm) {
      if (!set.contains(temp)) {
        result.append(temp);
        set.add(temp);
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isAST()) {
      final IAST list = (IAST) ast.arg1();
      int n = list.argSize();

      int min, max, step;

      if (ast.isAST1()) {
        min = n;
        max = n;
        step = 1;
      } else {
        IntRangeSpec range = IntRangeSpec.createNonNegative(ast, 2);
        if (range == null) {
          return F.NIL;
        }
        min = range.minimum();
        max = range.maximum();
        step = range.step();
      }

      final IASTAppendable result = F.ListAlloc(100);

      if (step > 0) {
        for (int i = min; i <= max; i += step) {
          if (i >= 0 && i <= n) {
            createPermutationsWithNParts(list, i, result);
          }
        }
      } else if (step < 0) {
        for (int i = min; i >= max; i += step) {
          if (i >= 0 && i <= n) {
            createPermutationsWithNParts(list, i, result);
          }
        }
      }
      return result;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
