package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import java.util.Iterator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class IntegerPartitions extends AbstractFunctionEvaluator {

  public static final class NumberPartitionsIterable implements Iterable<int[]> {

    private class NumberPartitionsIterator implements Iterator<int[]> {

      private int i = 0;
      private int k = 0;
      private final int fPartititionsIndex[];
      private final int fCopiedResultIndex[];
      private int fResultIndex[];

      private NumberPartitionsIterator() {
        int size = n;
        if (len > n) {
          size = len;
        }
        fPartititionsIndex = new int[size];
        fCopiedResultIndex = new int[size];
        fResultIndex = nextBeforehand();
      }

      @Override
      public boolean hasNext() {
        return fResultIndex != null;
      }

      @Override
      public int[] next() {
        System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
        fResultIndex = nextBeforehand();
        return fCopiedResultIndex;
      }

      private final int[] nextBeforehand() {
        int l;
        int k1;
        if (i == -1) {
          return null;
        }
        if (fPartititionsIndex[0] != 0) {
          k1 = k;
          while (fPartititionsIndex[k1] == 1) {
            fPartititionsIndex[k1--] = 0;
          }
          while (true) {
            l = k - i;
            k = i;
            fPartititionsIndex[i] -= 1;
            while (fPartititionsIndex[k] <= l) {
              l = l - fPartititionsIndex[k++];
              fPartititionsIndex[k] = fPartititionsIndex[k - 1];
            }
            if (k != n - 1) {
              fPartititionsIndex[++k] = l + 1;
              if (fPartititionsIndex[i] != 1) {
                i = k;
              }
              if (fPartititionsIndex[i] == 1) {
                i--;
              }
              return fPartititionsIndex;
            }
            k++;
            if (fPartititionsIndex[i] != 1) {
              i = k;
            }
            if (fPartititionsIndex[i] == 1) {
              i--;
            }
          }
        }
        fPartititionsIndex[0] = n;
        k = 0;
        i = 0;
        return fPartititionsIndex;
      }
    }

    private final int n;
    private final int len;

    public NumberPartitionsIterable(final int n, final int l) {
      super();
      this.n = n;
      this.len = l;
      int size = n;
      if (len > n) {
        size = len;
      }
      if (Config.MAX_AST_SIZE < size) {
        ASTElementLimitExceeded.throwIt(size);
      }
    }

    @Override
    public Iterator<int[]> iterator() {
      return new NumberPartitionsIterator();
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
            } else if (arg2.second().equals(S.Infinity) || arg2.second().equals(S.All)) {
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
    IntRangeSpec range = IntRangeSpec.createNonNegative(ast, 2);

    if (range != null) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        final int n = arg1.toIntDefault(Integer.MIN_VALUE);

        if (ast.isAST3()) {
          IExpr kspec = ast.arg2();
          if (ast.arg3().equals(S.All)) {
            if (n < 0)
              return F.CEmptyList;
            return F.IntegerPartitions(F.ZZ(n), kspec);
          }
          if (ast.arg3().isList()) {
            IAST s = (IAST) ast.arg3();
            if (kspec.isList1() && kspec.first().isInteger()) {
              IInteger k = (IInteger) kspec.first();
              return F.ReverseSort(F.Select(F.Union(F.Map(S.ReverseSort, F.Tuples(s, k))),
                  F.Function(F.Equal(F.Total(F.Slot1), n))));
            }
            if (!s.forAll(x -> x.isInteger())) {
              return F.NIL;
            }
          }
          IExpr frobeniusPartition = frobeniusPartition(ast, engine);
          if (frobeniusPartition.isList()) {
            IASTMutable mutableList = ((IAST) frobeniusPartition).copy();
            EvalAttributes.sort(mutableList,
                Comparators.reversedComparator(Comparators.LEXICAL_COMPARATOR));
            return mutableList;
          }
          return F.NIL;
        } else if (ast.argSize() == 4) {
          IExpr kspec = ast.arg2();
          IExpr sspec = ast.arg3();
          IExpr m = ast.arg4();
          return F.Take(F.IntegerPartitions(F.ZZ(n), kspec, sspec), m);
        }

        if (n < 0) {
          return F.CEmptyList;
        }

        if (range.minimum() > n) {
          return F.CEmptyList;
        }

        // Impossible to fulfill exactly 0 bounds for elements > 0 without diverging combinations
        // iteration limit
        if (n > 0 && range.maximum() == 0) {
          return F.CEmptyList;
        }

        int max = range.maximum();
        if (max > n) {
          range = new IntRangeSpec(range.minimum(), n, range.step());
          max = range.maximum();
        }

        if (n == 0) {
          return range.isIncluded(0) ? F.list(F.CEmptyList) : F.CEmptyList;
        }
        if (n == 1) {
          return range.isIncluded(1) ? F.list(F.list(F.C1)) : F.CEmptyList;
        }

        IASTAppendable temp;
        final NumberPartitionsIterable comb = new NumberPartitionsIterable(n, max);
        IASTAppendable result = F.ListAlloc(50);
        int iterationLimit = engine.getIterationLimit();
        int iterationCounter = 0;

        for (int j[] : comb) {
          if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
            IterationLimitExceeded.throwIt(iterationCounter, ast);
          }
          if (j.length > max) {
            if (j[max] != 0) {
              continue;
            }
          }
          int count = 0;
          for (int i = 0; i < j.length; i++) {
            if (j[i] != 0) {
              count++;
            }
          }
          if (!range.isIncluded(count)) {
            continue;
          }
          temp = F.ListAlloc(j.length);
          for (int i = 0; i < j.length; i++) {
            if (j[i] != 0) {
              temp.append(j[i]);
            } else {
              break;
            }
          }
          result.append(temp);
          if (max == 1) {
            break;
          }
        }
        return result;
      } else if (arg1.isFraction()) {
        if (ast.argSize() == 1) {
          return F.CEmptyList;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_4;
  }
}
