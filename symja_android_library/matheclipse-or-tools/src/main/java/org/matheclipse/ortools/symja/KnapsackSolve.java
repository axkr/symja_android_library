package org.matheclipse.ortools.symja;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.ortools.algorithms.KnapsackSolver;

/**
 * <pre>
 * <code>KnapsackSolve({{cost1, value1}, {cost2, value2}, ...}, maxCost)
 * KnapsackSolve({{cost1, value1, maxCount1}, ...}, maxCost)
 * </code>
 * </pre>
 *
 * <p>
 * the number of times each item is taken so that the total value is as large as possible and the
 * total cost is at most <code>maxCost</code>. Without a <code>maxCount</code> an item can be taken
 * any number of times. A cost can be a list <code>{c1, c2, ...}</code> of costs in several
 * dimensions; then <code>maxCost</code> is the list of the maximal total costs. Costs and values
 * must be exact non-negative rational numbers.
 *
 * <p>
 * The problem is solved with the multidimensional branch and bound knapsack solver of OR-Tools: an
 * item which may be taken up to <code>m</code> times becomes the items taken <code>1, 2, 4, ...
 * </code> times.
 *
 * <pre>
 * <code>&gt;&gt; KnapsackSolve({{5, 10}, {4, 40}, {6, 30}, {3, 50}}, 10)
 * {0,0,0,3}
 * </code>
 * </pre>
 */
public class KnapsackSolve extends AbstractFunctionEvaluator {

  /** The time limit of the knapsack search in seconds. */
  private static final double TIME_LIMIT = 60.0;

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (!ast.arg1().isList()) {
      return F.NIL;
    }
    IAST items = (IAST) ast.arg1();
    IExpr maxCostArg = ast.arg2();
    final boolean vectorCost = maxCostArg.isList();
    IAST maxCost = vectorCost ? (IAST) maxCostArg : F.list(maxCostArg);
    final int dims = maxCost.argSize();
    if (dims == 0) {
      return F.NIL;
    }
    final int n = items.argSize();
    IRational[][] costs = new IRational[n][dims];
    IRational[] values = new IRational[n];
    long[] maxCounts = new long[n];
    IRational[] capacity = new IRational[dims];
    for (int d = 0; d < dims; d++) {
      IExpr c = maxCost.get(d + 1);
      if (!c.isRational() || ((IRational) c).isNegative()) {
        if (c.isNumber()) {
          return message("non-negative exact maximal costs expected", engine);
        }
        return F.NIL;
      }
      capacity[d] = (IRational) c;
    }
    for (int i = 0; i < n; i++) {
      IExpr item = items.get(i + 1);
      if (!item.isList() || item.argSize() < 2 || item.argSize() > 3) {
        return F.NIL;
      }
      IExpr cost = item.first();
      IAST costList = cost.isList() ? (IAST) cost : F.list(cost);
      if (cost.isList() != vectorCost || costList.argSize() != dims) {
        return F.NIL;
      }
      for (int d = 0; d < dims; d++) {
        IExpr c = costList.get(d + 1);
        if (!c.isRational() || ((IRational) c).isNegative()) {
          if (c.isNumber()) {
            return message("non-negative exact costs expected", engine);
          }
          return F.NIL;
        }
        costs[i][d] = (IRational) c;
      }
      IExpr value = item.second();
      if (!value.isRational()) {
        if (value.isNumber()) {
          return message("exact rational values expected", engine);
        }
        return F.NIL;
      }
      values[i] = (IRational) value;
      maxCounts[i] = Long.MAX_VALUE;
      if (item.argSize() == 3) {
        IExpr m = item.getAt(3);
        if (m.isInfinity()) {
          maxCounts[i] = Long.MAX_VALUE;
        } else if (m.isInteger() && !((IInteger) m).isNegative()) {
          maxCounts[i] = ((IInteger) m).toLongDefault();
          if (maxCounts[i] == Long.MIN_VALUE) {
            maxCounts[i] = Long.MAX_VALUE;
          }
        } else {
          return F.NIL;
        }
      }
    }
    try {
      return solve(costs, values, maxCounts, capacity, engine);
    } catch (ArithmeticException ae) {
      return message("the costs or values are too large", engine);
    }
  }

  private static IExpr message(String text, EvalEngine engine) {
    // `1`.
    return Errors.printMessage(S.KnapsackSolve, "error", F.list(F.stringx(text)), engine);
  }

  /** The common denominator of the rational numbers. */
  private static IInteger denominator(IRational[] numbers) {
    IInteger lcm = F.C1;
    for (IRational r : numbers) {
      if (r instanceof IFraction) {
        lcm = lcm.lcm(((IFraction) r).denominator());
      }
    }
    return lcm;
  }

  private static long scaled(IRational r, IInteger factor) {
    IExpr product = r.multiply(factor);
    if (!product.isInteger()) {
      throw new ArithmeticException("not integral");
    }
    long value = ((IInteger) product).toLongDefault();
    if (value == Long.MIN_VALUE) {
      throw new ArithmeticException("long overflow");
    }
    return value;
  }

  private static IExpr solve(IRational[][] costs, IRational[] values, long[] maxCounts,
      IRational[] capacity, EvalEngine engine) {
    final int n = values.length;
    final int dims = capacity.length;
    // integer costs per dimension and integer values
    long[][] cost = new long[n][dims];
    long[] cap = new long[dims];
    for (int d = 0; d < dims; d++) {
      IRational[] column = new IRational[n + 1];
      for (int i = 0; i < n; i++) {
        column[i] = costs[i][d];
      }
      column[n] = capacity[d];
      IInteger factor = denominator(column);
      for (int i = 0; i < n; i++) {
        cost[i][d] = scaled(costs[i][d], factor);
      }
      cap[d] = scaled(capacity[d], factor);
    }
    IInteger valueFactor = denominator(values);
    long[] value = new long[n];
    for (int i = 0; i < n; i++) {
      value[i] = scaled(values[i], valueFactor);
    }

    // the pieces: item i taken k times, k = 1, 2, 4, ..., rest
    List<long[]> pieceWeights = new ArrayList<long[]>();
    List<Long> pieceProfits = new ArrayList<Long>();
    List<long[]> pieceItems = new ArrayList<long[]>();
    long[] counts = new long[n];
    for (int i = 0; i < n; i++) {
      if (value[i] <= 0) {
        // never worth a cost
        continue;
      }
      long limit = maxCounts[i];
      boolean free = true;
      for (int d = 0; d < dims; d++) {
        if (cost[i][d] > 0) {
          free = false;
          limit = Math.min(limit, cap[d] / cost[i][d]);
        }
      }
      if (free) {
        if (limit == Long.MAX_VALUE) {
          return message("an item with a positive value and no cost can be taken infinitely often",
              engine);
        }
        // an item without cost is always taken as often as allowed
        counts[i] = limit;
        continue;
      }
      long k = 1;
      long remaining = limit;
      while (remaining > 0) {
        long take = Math.min(k, remaining);
        long[] w = new long[dims];
        for (int d = 0; d < dims; d++) {
          w[d] = Math.multiplyExact(cost[i][d], take);
        }
        pieceWeights.add(w);
        pieceProfits.add(Math.multiplyExact(value[i], take));
        pieceItems.add(new long[] {i, take});
        remaining -= take;
        k = Math.multiplyExact(k, 2L);
      }
    }
    final int m = pieceProfits.size();
    if (m > 0) {
      long[] profits = new long[m];
      long[][] weights = new long[dims][m];
      for (int p = 0; p < m; p++) {
        profits[p] = pieceProfits.get(p);
        for (int d = 0; d < dims; d++) {
          weights[d][p] = pieceWeights.get(p)[d];
        }
      }
      KnapsackSolver solver = new KnapsackSolver(
          KnapsackSolver.SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER, "Symja");
      solver.setTimeLimit(TIME_LIMIT);
      solver.init(profits, weights, cap);
      solver.solve();
      if (!solver.isSolutionOptimal()) {
        // `1`.
        Errors.printMessage(S.KnapsackSolve, "error",
            F.list(F.stringx("the time limit stopped the search; the result may not be optimal")),
            engine);
      }
      for (int p = 0; p < m; p++) {
        if (solver.bestSolutionContains(p)) {
          long[] piece = pieceItems.get(p);
          counts[(int) piece[0]] += piece[1];
        }
      }
    }
    IASTAppendable list = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      list.append(F.ZZ(counts[i]));
    }
    return list;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
