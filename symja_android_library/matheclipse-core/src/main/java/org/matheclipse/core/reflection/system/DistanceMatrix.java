package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>DistanceMatrix(list)</code> - the matrix of the distances between the elements of
 * <code>list</code>; <code>DistanceMatrix(list1, list2)</code> - the distances from the elements of
 * <code>list1</code> to the elements of <code>list2</code>.
 *
 * <p>
 * Without a <code>DistanceFunction</code> option the distance depends on the elements: numbers and
 * numeric vectors of the same length have the euclidean distance, numeric vectors of different
 * lengths the dynamic time warping distance, strings and other lists the edit distance.
 */
public class DistanceMatrix extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (!ast.arg1().isList() || (argSize == 2 && !ast.arg2().isList())) {
      return F.NIL;
    }
    IAST list1 = (IAST) ast.arg1();
    IAST list2 = argSize == 2 ? (IAST) ast.arg2() : list1;
    if (list1.argSize() * (long) list2.argSize() > 1_000_000L) {
      return F.NIL;
    }
    IExpr distanceFunction = options[0];
    int kind = -1;
    if (distanceFunction == S.Automatic) {
      kind = distanceKind(list1, list2);
      if (kind < 0) {
        return F.NIL;
      }
    }
    IASTAppendable matrix = F.ListAlloc(list1.argSize());
    for (int i = 1; i < list1.size(); i++) {
      IASTAppendable row = F.ListAlloc(list2.argSize());
      for (int j = 1; j < list2.size(); j++) {
        engine.checkTimeLimit();
        IExpr a = list1.get(i);
        IExpr b = list2.get(j);
        IExpr distance;
        switch (kind) {
          case NUMBERS:
            distance = engine.evaluate(F.Abs(F.Subtract(a, b)));
            break;
          case VECTORS:
            distance = engine.evaluate(F.EuclideanDistance(a, b));
            break;
          case WARPING:
            distance = warpingDistance((IAST) a, (IAST) b, engine);
            break;
          case STRINGS:
            distance = engine.evaluate(F.binaryAST2(S.EditDistance, a, b));
            break;
          case SEQUENCES:
            distance = F.ZZ(editDistance((IAST) a, (IAST) b));
            break;
          default:
            distance = engine.evaluate(F.binaryAST2(distanceFunction, a, b));
            break;
        }
        row.append(distance);
      }
      matrix.append(row);
    }
    return matrix;
  }

  private static final int NUMBERS = 0;
  private static final int VECTORS = 1;
  private static final int WARPING = 2;
  private static final int STRINGS = 3;
  private static final int SEQUENCES = 4;

  /** The default distance for the elements of both lists, <code>-1</code> if there is none. */
  private static int distanceKind(IAST list1, IAST list2) {
    IASTAppendable all = F.ListAlloc(list1.argSize() + list2.argSize());
    all.appendArgs(list1);
    all.appendArgs(list2);
    if (all.forAll(x -> !x.isList() && x.isNumericFunction())) {
      return NUMBERS;
    }
    if (all.forAll(x -> x.isString())) {
      return STRINGS;
    }
    if (all.forAll(x -> x.isList())) {
      if (all.forAll(x -> ((IAST) x).forAll(y -> !y.isList() && y.isNumericFunction()))) {
        final int length = all.arg1().argSize();
        return all.forAll(x -> x.argSize() == length) ? VECTORS : WARPING;
      }
      return SEQUENCES;
    }
    return -1;
  }

  /** The number of insertions, deletions and substitutions which turn <code>a</code> into b. */
  private static int editDistance(IAST a, IAST b) {
    int n = a.argSize();
    int m = b.argSize();
    int[] previous = new int[m + 1];
    int[] current = new int[m + 1];
    for (int j = 0; j <= m; j++) {
      previous[j] = j;
    }
    for (int i = 1; i <= n; i++) {
      current[0] = i;
      for (int j = 1; j <= m; j++) {
        int cost = a.get(i).equals(b.get(j)) ? 0 : 1;
        current[j] =
            Math.min(Math.min(previous[j] + 1, current[j - 1] + 1), previous[j - 1] + cost);
      }
      int[] temp = previous;
      previous = current;
      current = temp;
    }
    return previous[m];
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.DistanceFunction}, new IExpr[] {S.Automatic});
  }

  /**
   * The dynamic time warping distance of the sequences <code>u</code> and <code>v</code>: the
   * smallest sum of the distances of the matched elements over the monotone alignments which match
   * every element at least once.
   */
  private static IExpr warpingDistance(IAST u, IAST v, EvalEngine engine) {
    int n = u.argSize();
    int m = v.argSize();
    IExpr[][] cost = new IExpr[n + 1][m + 1];
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        engine.checkTimeLimit();
        IExpr a = u.get(i);
        IExpr b = v.get(j);
        IExpr d = a.isList() ? engine.evaluate(F.EuclideanDistance(a, b))
            : engine.evaluate(F.Abs(F.Subtract(a, b)));
        IExpr best;
        if (i == 1 && j == 1) {
          best = F.C0;
        } else if (i == 1) {
          best = cost[1][j - 1];
        } else if (j == 1) {
          best = cost[i - 1][1];
        } else {
          best = engine.evaluate(F.Min(cost[i - 1][j], cost[i][j - 1], cost[i - 1][j - 1]));
        }
        cost[i][j] = engine.evaluate(F.Plus(best, d));
      }
    }
    return cost[n][m];
  }
}
