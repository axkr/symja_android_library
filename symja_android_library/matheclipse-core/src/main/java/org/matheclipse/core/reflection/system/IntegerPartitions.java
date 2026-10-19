package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * <code>IntegerPartitions(n, lengths, parts, m)</code>.
 *
 * <p>
 * The partitions are generated in reverse lexicographic order. For the default parts
 * <code>1, 2, ..., s</code> a walk over the parts in descending order chooses each next part
 * between a lower bound from the maximal length and an upper bound from the minimal length, so
 * every visited node leads to a result. An explicit list of positive parts is reversed and the
 * count vectors are enumerated in descending lexicographic order; the parts of a partition are
 * listed in that reversed order.
 */
public class IntegerPartitions extends AbstractFunctionEvaluator {

  /** The allowed numbers of parts: <code>min &lt;= k &lt;= max</code> and k = max - j*step. */
  private static final class LengthSet {
    final long min;
    final long max;
    final long step;
    /** The element from which the step is counted. */
    final long anchor;

    LengthSet(long min, long max, long step, long anchor) {
      this.min = min;
      this.max = max;
      this.step = step;
      this.anchor = anchor;
    }

    boolean isEmpty() {
      return min > max;
    }

    boolean contains(long k) {
      return k >= min && k <= max && Math.abs(k - anchor) % step == 0;
    }
  }

  private static final long INFINITE = Long.MAX_VALUE / 4;

  /**
   * Parses <code>All</code>, <code>k</code>, <code>{k}</code>, <code>{kmin, kmax}</code> or
   * <code>{k1, k2, dk}</code>; <code>null</code> for an invalid specification.
   */
  private static LengthSet lengthSet(IExpr spec) {
    if (spec == S.All || spec.isInfinity()) {
      return new LengthSet(0, INFINITE, 1, 0);
    }
    if (spec.isInteger()) {
      if (spec.isNegative()) {
        return null;
      }
      return new LengthSet(0, lengthBound(spec), 1, 0);
    }
    if (spec.isList1() && spec.first().isInteger() && !spec.first().isNegative()) {
      long k = lengthBound(spec.first());
      return new LengthSet(k, k, 1, k);
    }
    if (spec.isList2() || spec.isList3()) {
      IExpr a = spec.first();
      IExpr b = spec.second();
      if (!a.isInteger() || a.isNegative()) {
        return null;
      }
      long k1 = lengthBound(a);
      long k2;
      if (b.isInfinity() || b == S.All) {
        k2 = INFINITE;
      } else if (b.isInteger() && !b.isNegative()) {
        k2 = lengthBound(b);
      } else {
        return null;
      }
      long step = 1;
      if (spec.isList3()) {
        IExpr s = ((IAST) spec).arg3();
        if (!s.isInteger() || s.isZero()) {
          return null;
        }
        step = ((IInteger) s).toLongDefault();
        if (step == Config.INVALID_LONG) {
          return null;
        }
      }
      if (step > 0) {
        return new LengthSet(k1, k2, step, k1);
      }
      // a negative step counts down from k1 to k2
      if (k2 == INFINITE) {
        return new LengthSet(1, 0, 1, 0);
      }
      return new LengthSet(k2, k1, -step, k1);
    }
    return null;
  }

  private static long lengthBound(IExpr k) {
    long value = ((IInteger) k).toLongDefault();
    return value == Config.INVALID_LONG || value > INFINITE ? INFINITE : value;
  }

  /** Collects rows and stops after <code>limit</code> rows when <code>limit &gt;= 0</code>. */
  private static final class Rows {
    final List<IAST> list = new ArrayList<IAST>();
    final long limit;
    final EvalEngine engine;

    Rows(long limit, EvalEngine engine) {
      this.limit = limit;
      this.engine = engine;
    }

    /** @return <code>false</code> if no more rows are needed */
    boolean add(IAST row) {
      list.add(row);
      int size = list.size();
      if (size >= Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(size);
      }
      if ((size & 4095) == 0) {
        engine.checkTimeLimit();
      }
      return limit < 0 || size < limit;
    }
  }

  /**
   * The partitions of <code>n</code> into parts <code>1..smax</code> with a number of parts in
   * <code>lengths</code>, in reverse lexicographic order.
   */
  private static void rangePartitions(long n, long smax, LengthSet lengths, Rows rows) {
    if (n == 0) {
      if (lengths.contains(0)) {
        rows.add(F.CEmptyList);
      }
      return;
    }
    if (smax < 1 || lengths.max < 1) {
      return;
    }
    long[] part = new long[16];
    int d = 0;
    long rest = n;
    long candidate = upperPart(smax, rest, 0, lengths);
    while (true) {
      if (candidate >= lowerPart(rest, d, lengths) && candidate >= 1) {
        if (d == part.length) {
          if (d >= Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(d);
          }
          part = Arrays.copyOf(part, 2 * d);
        }
        part[d++] = candidate;
        rest -= candidate;
        if (rest == 0) {
          if (lengths.contains(d)) {
            IASTAppendable row = F.ListAlloc(d);
            for (int i = 0; i < d; i++) {
              row.append(F.ZZ(part[i]));
            }
            if (!rows.add(row)) {
              return;
            }
          }
          // replace the last part by a smaller one
          d--;
          rest += part[d];
          candidate = part[d] - 1;
          continue;
        }
        candidate = upperPart(part[d - 1], rest, d, lengths);
        continue;
      }
      // backtrack
      if (d == 0) {
        return;
      }
      d--;
      rest += part[d];
      candidate = part[d] - 1;
    }
  }

  /** The largest next part after <code>d</code> parts with <code>rest</code> left. */
  private static long upperPart(long previous, long rest, int d, LengthSet lengths) {
    long hi = Math.min(previous, rest);
    if (lengths.min > d + 1) {
      // the parts after this one are at most rest - p ones
      hi = Math.min(hi, rest + d + 1 - lengths.min);
    }
    return hi;
  }

  /** The smallest next part after <code>d</code> parts with <code>rest</code> left. */
  private static long lowerPart(long rest, int d, LengthSet lengths) {
    if (lengths.max == INFINITE) {
      return 1;
    }
    long room = lengths.max - d;
    if (room <= 0) {
      return Long.MAX_VALUE;
    }
    return Math.max(1, (rest + room - 1) / room);
  }

  /**
   * The partitions of <code>n</code> into the positive <code>values</code> (scaled to integers),
   * enumerating the count vectors over the values in descending lexicographic order.
   */
  private static void listPartitions(long n, long[] values, IExpr[] parts, LengthSet lengths,
      Rows rows) {
    int size = values.length;
    // the smallest and largest value from position i on
    long[] suffixMin = new long[size + 1];
    long[] suffixMax = new long[size + 1];
    suffixMin[size] = Long.MAX_VALUE;
    suffixMax[size] = 0;
    for (int i = size - 1; i >= 0; i--) {
      suffixMin[i] = Math.min(values[i], suffixMin[i + 1]);
      suffixMax[i] = Math.max(values[i], suffixMax[i + 1]);
    }
    // an explicit stack: rest[i] and used[i] hold the state before position i; the counts of
    // the positions after the current one are 0
    long[] counts = new long[size];
    long[] rest = new long[size + 1];
    long[] used = new long[size + 1];
    rest[0] = n;
    int i = 0;
    while (true) {
      boolean descend = false;
      if (rest[i] == 0) {
        if (lengths.contains(used[i])) {
          if (used[i] >= Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(used[i]);
          }
          IASTAppendable row = F.ListAlloc((int) used[i]);
          for (int j = 0; j < i; j++) {
            for (long c = 0; c < counts[j]; c++) {
              row.append(parts[j]);
            }
          }
          if (!rows.add(row)) {
            return;
          }
        }
      } else if (i < size) {
        // the parts from position i on need between ceil(rest/max) and rest/min more entries
        long fewest = (rest[i] + suffixMax[i] - 1) / suffixMax[i];
        long most = rest[i] / suffixMin[i];
        if (used[i] + fewest <= lengths.max && used[i] + most >= lengths.min) {
          counts[i] = Math.min(rest[i] / values[i], lengths.max - used[i]);
          descend = true;
        }
      }
      if (!descend) {
        // back to the last position whose count can still be decreased
        while (true) {
          if (i == 0) {
            return;
          }
          i--;
          if (counts[i] > 0) {
            counts[i]--;
            break;
          }
        }
      }
      rest[i + 1] = rest[i] - counts[i] * values[i];
      used[i + 1] = used[i] + counts[i];
      i++;
    }
  }

  private static boolean backtrack(int[] sspec, int target, int index, int currentSum,
      int currentParts, int minParts, int maxParts, int stepParts, int[] counts,
      IASTAppendable result, EvalEngine engine, int[] iterations, int iterationLimit,
      int numberOfSolutions, boolean allPositive) {

    iterations[0]++;
    if (iterationLimit > 0 && iterations[0] > iterationLimit) {
      return false;
    }
    if (numberOfSolutions >= 0 && result.argSize() >= numberOfSolutions) {
      return true;
    }

    if (allPositive && currentSum > target) {
      return true;
    }

    if (index == sspec.length - 1) {
      int remainingSum = target - currentSum;
      if (sspec[index] == 0) {
        if (remainingSum == 0) {
          int start = Math.max(0, minParts - currentParts);
          int end = maxParts - currentParts;
          for (int c = end; c >= start; c--) {
            if (stepParts > 1 && (currentParts + c - minParts) % stepParts != 0)
              continue;
            counts[index] = c;
            addPartition(sspec, counts, result);
            if (numberOfSolutions >= 0 && result.argSize() >= numberOfSolutions)
              return true;
          }
        }
      } else {
        if (remainingSum % sspec[index] == 0) {
          int c = remainingSum / sspec[index];
          if (c >= 0 && currentParts + c >= minParts && currentParts + c <= maxParts) {
            if (stepParts <= 1 || (currentParts + c - minParts) % stepParts == 0) {
              counts[index] = c;
              addPartition(sspec, counts, result);
            }
          }
        }
      }
      return true;
    }

    int maxCount = maxParts - currentParts;
    if (allPositive && sspec[index] > 0) {
      int sumBasedMax = (target - currentSum) / sspec[index];
      if (sumBasedMax < maxCount) {
        maxCount = sumBasedMax;
      }
    }

    for (int c = maxCount; c >= 0; c--) {
      counts[index] = c;
      boolean ok = backtrack(sspec, target, index + 1, currentSum + c * sspec[index],
          currentParts + c, minParts, maxParts, stepParts, counts, result, engine, iterations,
          iterationLimit, numberOfSolutions, allPositive);
      if (!ok)
        return false;
      if (numberOfSolutions >= 0 && result.argSize() >= numberOfSolutions)
        return true;
    }
    return true;
  }

  private static void addPartition(int[] sspec, int[] counts, IASTAppendable result) {
    IASTAppendable list = F.ListAlloc();
    for (int i = 0; i < sspec.length; i++) {
      IInteger value = F.ZZ(sspec[i]);
      for (int j = 0; j < counts[i]; j++) {
        list.append(value);
      }
    }
    result.append(list);
  }

  private static boolean createFrobeniusSolution(IInteger[] frobeniusSolution,
      int[] numberSpecification, IInteger lowerLimitOfCoins, IInteger upperLimitOfCoins,
      IInteger stepOfCoins, IASTAppendable result) {
    if (frobeniusSolution.length != numberSpecification.length) {
      return false;
    }

    IInteger sum = F.C0;
    for (int i = 0; i < frobeniusSolution.length; i++) {
      sum = sum.add(frobeniusSolution[i]);
    }
    if (sum.isGE(lowerLimitOfCoins) && sum.isLE(upperLimitOfCoins)) {
      if (!stepOfCoins.isOne()) {
        if (!sum.subtract(lowerLimitOfCoins).mod(stepOfCoins).isZero()) {
          return true; // Valid structurally, but skipped due to step mismatch
        }
      }
      IASTAppendable list = F.ListAlloc();
      // Iterate backwards: Ascending configuration -> Appending descending largest coins first
      for (int i = frobeniusSolution.length - 1; i >= 0; i--) {
        int counter = frobeniusSolution[i].toIntDefault();
        if (F.isNotPresent(counter)) {
          return false;
        }
        if (counter > 0) {
          IInteger value = F.ZZ(numberSpecification[i]);
          for (int j = 0; j < counter; j++) {
            list.append(value);
          }
        }
      }
      result.append(list);
    }
    return true;
  }

  private static IExpr frobeniusPartition(final IAST ast, EvalEngine engine) {
    if (ast.arg3().isNonEmptyList() && ast.arg1().isInteger()) {
      try {
        int[] listInt =
            Validate.checkListOfInts(ast, ast.arg3(), Integer.MIN_VALUE, Integer.MAX_VALUE, engine);
        if (listInt != null) {
          // Distinct and sort ascending
          listInt = Arrays.stream(listInt).distinct().toArray();
          Arrays.sort(listInt);

          IInteger lowerLimitOfCoins = F.C0;
          IInteger upperLimitOfCoins = F.ZZ(Integer.MAX_VALUE);
          IInteger stepOfCoins = F.C1;

          IExpr arg2 = ast.arg2();
          if (arg2.isInteger()) {
            upperLimitOfCoins = (IInteger) arg2;
          } else if ((arg2.isList2() || arg2.isList3()) && arg2.first().isInteger()) {
            lowerLimitOfCoins = (IInteger) arg2.first();
            if (arg2.second().isInteger()) {
              upperLimitOfCoins = (IInteger) arg2.second();
            } else if (arg2.second() == S.Infinity || arg2.second() == S.All) {
              upperLimitOfCoins = F.ZZ(Integer.MAX_VALUE);
            } else {
              return F.NIL;
            }
            if (arg2.isList3()) {
              if (((IAST) arg2).arg3().isInteger()) {
                stepOfCoins = (IInteger) ((IAST) arg2).arg3();
                if (stepOfCoins.compareTo(F.C0) <= 0)
                  return F.NIL;
              } else {
                return F.NIL;
              }
            }
          } else if (arg2.isList1() && arg2.first().isInteger()) {
            lowerLimitOfCoins = (IInteger) arg2.first();
            upperLimitOfCoins = lowerLimitOfCoins;
          } else if (arg2 != S.All && !arg2.isInfinity()) {
            return F.NIL;
          }

          boolean allPositive = true;
          for (int val : listInt) {
            if (val <= 0) {
              allPositive = false;
              break;
            }
          }

          boolean useBacktrack = !allPositive || lowerLimitOfCoins.compareTo(F.C0) > 0
              || upperLimitOfCoins.compareTo(F.ZZ(Integer.MAX_VALUE)) < 0 || !stepOfCoins.isOne();

          int numberOfSolutions = -1; // all solutions
          if (ast.argSize() == 4) {
            numberOfSolutions = ast.arg4().toIntDefault(-1);
          }

          IASTAppendable result = F.ListAlloc(8);
          int iterationLimit = engine.getIterationLimit();

          if (!useBacktrack) {
            IInteger[] solution;
            FrobeniusSolver solver = FrobeniusSolve.getSolver(listInt, (IInteger) ast.arg1());
            int iterations = 0;
            while ((solution = solver.take()) != null) {
              if (iterationLimit > 0 && iterations > iterationLimit) {
                Errors.printMessage(ast.topHead(), "itlimpartial", F.list(F.ZZ(iterationLimit)),
                    engine);
                return result;
              }
              if (numberOfSolutions >= 0 && result.argSize() >= numberOfSolutions) {
                break;
              }
              iterations++;
              if (createFrobeniusSolution(solution, listInt, lowerLimitOfCoins, upperLimitOfCoins,
                  stepOfCoins, result)) {
                continue;
              }
            }
            return result;
          } else {
            if (!allPositive && upperLimitOfCoins.compareTo(F.ZZ(Integer.MAX_VALUE)) >= 0) {
              return F.NIL; // Unbounded infinity with negative subsets diverges
            }
            // Bounded configurations use strict descending orders for immediate fast-branch pruning
            int[] descendingList = new int[listInt.length];
            for (int i = 0; i < listInt.length; i++) {
              descendingList[i] = listInt[listInt.length - 1 - i];
            }

            int minParts = lowerLimitOfCoins.toIntDefault(0);
            int maxParts = upperLimitOfCoins.toIntDefault(Integer.MAX_VALUE);
            int stepParts = stepOfCoins.toIntDefault(1);
            int target = ast.arg1().toIntDefault(0);
            int[] counts = new int[listInt.length];
            int[] iterations = new int[] {0};

            boolean finished =
                backtrack(descendingList, target, 0, 0, 0, minParts, maxParts, stepParts, counts,
                    result, engine, iterations, iterationLimit, numberOfSolutions, allPositive);

            if (!finished && iterationLimit > 0 && iterations[0] > iterationLimit) {
              Errors.printMessage(ast.topHead(), "itlimpartial", F.list(F.ZZ(iterationLimit)),
                  engine);
            }
            return result;
          }
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.IntegerPartitions, rex, engine);
      }
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isRational()) {
      // Position 1 of `1` must be a rational number.
      return Errors.printMessage(S.IntegerPartitions, "rat", F.list(ast), engine);
    }
    LengthSet lengths = lengthSet(ast.argSize() >= 2 ? ast.arg2() : S.All);
    if (lengths == null) {
      return Errors.printMessage(S.IntegerPartitions, "nninfseq", F.list(ast), engine);
    }
    IExpr partSpec = ast.argSize() >= 3 ? ast.arg3() : S.All;
    IExpr takeSpec = ast.argSize() >= 4 ? ast.arg4() : S.All;
    if (!(partSpec == S.All || partSpec.isInfinity() || partSpec.isRational()
        || (partSpec.isList() && ((IAST) partSpec).forAll(x -> x.isRational())))) {
      return Errors.printMessage(S.IntegerPartitions, "rseq", F.list(ast), engine);
    }
    if (takeSpec != S.None && rowLimit(takeSpec) == Long.MIN_VALUE) {
      return Errors.printMessage(S.IntegerPartitions, "seq", F.list(ast), engine);
    }
    if (partSpec.isList() && !((IAST) partSpec).forAll(x -> x.isPositive())) {
      if (lengths.max == INFINITE) {
        // `1` contains partitions that are undefined because they are infinitely large.
        return Errors.printMessage(S.IntegerPartitions, "undef", F.list(ast), engine);
      }
      if (((IAST) partSpec).forAll(x -> x.isInteger())) {
        // non-positive parts
        IExpr result = frobeniusPartition(ast.argSize() == 4 ? F.IntegerPartitions(arg1,
            ast.arg2(), partSpec) : ast, engine);
        if (result.isList()) {
          IASTMutable mutableList = ((IAST) result).copy();
          EvalAttributes.sort(mutableList,
              Comparators.reversedComparator(Comparators.LEXICAL_COMPARATOR));
          return ast.argSize() == 4 ? F.Take(mutableList, takeSpec) : mutableList;
        }
      }
      return F.NIL;
    }
    if (takeSpec == S.None) {
      return F.CEmptyList;
    }
    long limit = rowLimit(takeSpec);
    if (limit == 0) {
      return F.CEmptyList;
    }
    if (arg1.isNegative() || lengths.isEmpty()) {
      return F.CEmptyList;
    }
    Rows rows = new Rows(limit, engine);
    if (partSpec.isList()) {
      IAST list = (IAST) partSpec;
      // scale the number and the parts to integers by the common denominator
      BigInteger denominator = ((IRational) arg1).toBigDenominator();
      for (int i = 1; i < list.size(); i++) {
        BigInteger d = ((IRational) list.get(i)).toBigDenominator();
        denominator = denominator.divide(denominator.gcd(d)).multiply(d);
      }
      IInteger scale = F.ZZ(denominator);
      long n = ((IRational) arg1).multiply(scale).toLongDefault();
      if (n == Config.INVALID_LONG) {
        return F.NIL;
      }
      int size = list.argSize();
      long[] values = new long[size];
      IExpr[] parts = new IExpr[size];
      for (int i = 0; i < size; i++) {
        // the reversed list
        IRational part = (IRational) list.get(size - i);
        values[i] = part.multiply(scale).toLongDefault();
        if (values[i] == Config.INVALID_LONG) {
          return F.NIL;
        }
        parts[i] = part;
      }
      listPartitions(n, values, parts, lengths, rows);
    } else {
      if (!arg1.isInteger()) {
        return F.CEmptyList;
      }
      long n = ((IInteger) arg1).toLongDefault();
      if (n == Config.INVALID_LONG) {
        if (lengths.max == 1 && lengths.min <= 1 && partSpec == S.All) {
          return F.list(F.list(arg1));
        }
        return F.NIL;
      }
      long smax;
      if (partSpec == S.All || partSpec.isInfinity()) {
        smax = n;
      } else if (partSpec.isRational()) {
        IInteger floor = ((IRational) partSpec).floorFraction();
        smax = floor.isNegative() ? 0 : Math.min(n, floor.toLongDefault(n));
      } else {
        return F.NIL;
      }
      rangePartitions(n, smax, lengths, rows);
    }
    return take(rows.list, takeSpec, ast, engine);
  }

  /**
   * The number of rows needed for <code>spec</code>: <code>-1</code> for all, <code>0</code> for
   * none, <code>Long.MIN_VALUE</code> for an invalid specification.
   */
  private static long rowLimit(IExpr spec) {
    if (spec == S.All) {
      return -1;
    }
    if (spec.isInteger()) {
      long m = ((IInteger) spec).toLongDefault();
      if (m == Config.INVALID_LONG) {
        return -1;
      }
      return m >= 0 ? m : -1;
    }
    if (spec.isList() && spec.argSize() >= 1 && spec.argSize() <= 3
        && ((IAST) spec).forAll(x -> x.isInteger())) {
      long m1 = ((IInteger) spec.first()).toLongDefault();
      long m2 = spec.argSize() >= 2 ? ((IInteger) spec.second()).toLongDefault() : m1;
      if (spec.argSize() == 3 && ((IAST) spec).arg3().isZero()) {
        return Long.MIN_VALUE;
      }
      if (m1 == 0 || m2 == 0) {
        // position 0 selects nothing
        return 0;
      }
      if (m1 > 0 && m2 > 0) {
        // one more row tells whether the positions exist
        return Math.max(m1, m2) + 1;
      }
      return -1;
    }
    return Long.MIN_VALUE;
  }

  /** The rows selected by the {@link S#Take} like <code>spec</code>. */
  private static IExpr take(List<IAST> list, IExpr spec, IAST ast, EvalEngine engine) {
    int size = list.size();
    if (spec == S.All) {
      return F.ListAlloc(list);
    }
    if (spec.isInteger()) {
      long m = ((IInteger) spec).toLongDefault();
      if (m >= 0) {
        return F.ListAlloc(list.subList(0, (int) Math.min(m, size)));
      }
      int from = (int) Math.max(0, size + m);
      return F.ListAlloc(list.subList(from, size));
    }
    IAST s = (IAST) spec;
    long m1 = ((IInteger) s.arg1()).toLongDefault();
    long m2 = s.argSize() >= 2 ? ((IInteger) s.arg2()).toLongDefault() : m1;
    long step = s.argSize() == 3 ? ((IInteger) s.arg3()).toLongDefault() : 1;
    long i1 = m1 < 0 ? size + m1 + 1 : m1;
    long i2 = m2 < 0 ? size + m2 + 1 : m2;
    if (m1 == 0 || m2 == 0) {
      return F.CEmptyList;
    }
    if (i1 < 1 || i1 > size || i2 < 1 || i2 > size) {
      // Warning: not all elements were found when attempting to take the sequence `1` from
      // `2`, which has length `3`.
      Errors.printMessage(S.IntegerPartitions, "take",
          F.list(F.List(F.ZZ(m1), F.ZZ(m2), F.ZZ(step)), ast.most(), F.ZZ(size)), engine);
      if (s.argSize() == 1) {
        return F.CEmptyList;
      }
      i1 = Math.max(1, Math.min(i1, size));
      i2 = Math.max(1, Math.min(i2, size));
      if (size == 0) {
        return F.CEmptyList;
      }
    }
    IASTAppendable result = F.ListAlloc();
    if (step > 0) {
      for (long i = i1; i <= i2; i += step) {
        result.append(list.get((int) i - 1));
      }
    } else {
      for (long i = i1; i >= i2; i += step) {
        result.append(list.get((int) i - 1));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "rat",
        F.stringx("Position 1 of `1` must be a rational number."));
    newSymbol.putMessage(IPatternMatcher.SET, "nninfseq", F.stringx(
        "Position 2 of `1` must be All, Infinity, nmax, {nmin}, {nmin, nmax} or {nmin, nmax, dn}, "
            + "where nmin is a non-negative integer, nmax is a non-negative integer or Infinity, "
            + "and dn is a nonzero integer."));
    newSymbol.putMessage(IPatternMatcher.SET, "rseq", F.stringx(
        "Position 3 of `1` must be All, a rational number or a list of rational numbers."));
    newSymbol.putMessage(IPatternMatcher.SET, "seq",
        F.stringx("Position 4 of `1` must be All, None, m, {m}, {m, n} or {m, n, s}, "
            + "where m and n are integers and s is a nonzero integer."));
    newSymbol.putMessage(IPatternMatcher.SET, "undef", F.stringx(
        "`1` contains partitions that are undefined because they are infinitely large."));
    newSymbol.putMessage(IPatternMatcher.SET, "take",
        F.stringx("Warning: not all elements were found when attempting to take the sequence "
            + "`1` from `2`, which has length `3`."));
    super.setUp(newSymbol);
  }
}
