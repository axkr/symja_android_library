package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.KPermutationsIterable;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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

  /** The number of distinct elements from which a permutation of that length is not countable. */
  private static final int FACTORIAL_LIMIT = 21;

  /** A rough size of one element of a permutation and of the list which holds it, in bytes. */
  private static final long BYTES_PER_ELEMENT = 8L;
  private static final long BYTES_PER_LIST = 48L;

  /**
   * Refuse a result which cannot be built, before it is enumerated: its length is counted from the
   * multiplicities of the elements, without creating a permutation.
   *
   * @return <code>true</code> if a message was printed and the call stays unevaluated
   */
  private static boolean refused(IAST ast, IAST list, int min, int max, int step,
      EvalEngine engine) {
    final int n = list.argSize();
    int longest = -1;
    for (int i = min; step > 0 ? i <= max : i >= max; i += step) {
      if (i >= 0 && i <= n) {
        longest = Math.max(longest, i);
      }
    }
    if (longest < 2) {
      return false;
    }
    Map<IExpr, Integer> multiplicities = new HashMap<IExpr, Integer>();
    for (int i = 1; i <= n; i++) {
      multiplicities.merge(list.get(i), 1, Integer::sum);
    }
    if (multiplicities.size() >= FACTORIAL_LIMIT && longest >= FACTORIAL_LIMIT) {
      Errors.printMessage(S.Permutations, "fac", F.List(ast), engine);
      return true;
    }
    // ways[k]: the number of different arrangements of k of the elements
    BigInteger[] ways = new BigInteger[longest + 1];
    Arrays.fill(ways, BigInteger.ZERO);
    ways[0] = BigInteger.ONE;
    for (int multiplicity : multiplicities.values()) {
      for (int k = longest; k >= 1; k--) {
        BigInteger sum = ways[k];
        BigInteger binomial = BigInteger.ONE;
        for (int j = 1; j <= Math.min(multiplicity, k); j++) {
          // Binomial(k, j) positions for j copies of this element
          binomial = binomial.multiply(BigInteger.valueOf(k - j + 1)).divide(BigInteger.valueOf(j));
          sum = sum.add(binomial.multiply(ways[k - j]));
        }
        ways[k] = sum;
      }
    }
    BigInteger length = BigInteger.ZERO;
    BigInteger bytes = BigInteger.ZERO;
    for (int i = min; step > 0 ? i <= max : i >= max; i += step) {
      if (i >= 0 && i <= n) {
        length = length.add(ways[i]);
        bytes = bytes.add(ways[i].multiply(BigInteger.valueOf(BYTES_PER_LIST + BYTES_PER_ELEMENT * i)));
      }
    }
    if (length.bitLength() > 63) {
      Errors.printMessage(S.Permutations, "len", F.List(ast, F.ZZ(length)), engine);
      return true;
    }
    if (length.bitLength() > 31
        || bytes.compareTo(BigInteger.valueOf(Runtime.getRuntime().maxMemory() / 2)) > 0) {
      Errors.printMessage(S.Permutations, "toobig", F.List(ast), engine);
      return true;
    }
    return false;
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

      // No permutation can be longer than the list, so a bound past n contributes nothing. This
      // matters because Permutations(list, All) answers Integer.MAX_VALUE here: the loops below
      // would run through two billion iterations that the i <= n test discards, and at
      // Integer.MAX_VALUE the increment overflows to negative and the loop never ends at all -
      // Permutations({1,2}, All) did not return.
      //
      // Only the bound the loop walks towards may be lowered. Lowering the one it starts from
      // would visit lengths that were never asked for: Permutations(x^2, {3}) is empty, because
      // x^2 has two arguments, and starting at 2 instead answers {x^2, 2^x}.
      if (step > 0) {
        if (max > n) {
          max = n;
        }
      } else if (step < 0) {
        if (min > n) {
          // down to the largest length at or below n that the stride actually lands on, so that
          // Permutations(Range(4), {4, 0, -2}) still visits 4, 2 and 0 rather than 3 and 1
          int stride = -step;
          min -= ((min - n + stride - 1) / stride) * stride;
        }
      }

      if (step == 0 || refused(ast, list, min, max, step, engine)) {
        return F.NIL;
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
