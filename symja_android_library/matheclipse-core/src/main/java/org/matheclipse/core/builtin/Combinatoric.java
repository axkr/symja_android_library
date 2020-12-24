package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.combinatoric.KSubsets;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.eval.util.SetSpecification;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.FrobeniusSolve;
import org.matheclipse.parser.client.FEConfig;

public final class Combinatoric {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.CartesianProduct.setEvaluator(new CartesianProduct());
      S.DiceDissimilarity.setEvaluator(new DiceDissimilarity());
      S.IntegerPartitions.setEvaluator(new IntegerPartitions());
      S.JaccardDissimilarity.setEvaluator(new JaccardDissimilarity());
      S.KOrderlessPartitions.setEvaluator(new KOrderlessPartitions());
      S.KPartitions.setEvaluator(new KPartitions());
      S.MatchingDissimilarity.setEvaluator(new MatchingDissimilarity());
      S.Partition.setEvaluator(new Partition());
      S.Permutations.setEvaluator(new Permutations());
      S.RogersTanimotoDissimilarity.setEvaluator(new RogersTanimotoDissimilarity());
      S.RussellRaoDissimilarity.setEvaluator(new RussellRaoDissimilarity());
      S.Signature.setEvaluator(new Signature());
      S.SokalSneathDissimilarity.setEvaluator(new SokalSneathDissimilarity());
      S.Subsets.setEvaluator(new Subsets());
      S.Tuples.setEvaluator(new Tuples());
      S.YuleDissimilarity.setEvaluator(new YuleDissimilarity());
    }
  }

  /**
   * Cartesian product for multiple lists.
   *
   * <p>See: <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian
   * product</a>
   */
  private static final class CartesianProduct extends AbstractFunctionEvaluator {

    /** Cartesian product iterator. */
    static final class CartesianProductIterator implements Iterator<IAST> {

      /** data structure. */
      final List<IAST> comps;

      final List<Iterator<IExpr>> compit;

      IASTAppendable current;

      boolean empty;

      /**
       * CartesianProduct iterator constructor.
       *
       * @param comps components of the cartesian product.
       */
      public CartesianProductIterator(List<IAST> comps, IASTAppendable emptyResultList) {
        if (comps == null) {
          throw new IllegalArgumentException("null comps not allowed");
        }
        this.comps = comps;
        current = emptyResultList;
        compit = new ArrayList<Iterator<IExpr>>(comps.size());
        empty = false;
        for (IAST ci : comps) {
          Iterator<IExpr> it = ci.iterator();
          if (!it.hasNext()) {
            empty = true;
            current.clear();
            break;
          }
          current.append(it.next());
          compit.add(it);
        }
      }

      /**
       * Test for availability of a next tuple.
       *
       * @return true if the iteration has more tuples, else false.
       */
      @Override
      public synchronized boolean hasNext() {
        return !empty;
      }

      /**
       * Get next tuple.
       *
       * @return next tuple.
       */
      @Override
      public synchronized IAST next() {
        if (empty) {
          throw new RuntimeException("invalid call of next()");
        }
        // IAST res = (IAST) current.clone();
        IAST res = current.copyAppendable();
        // search iterator which hasNext
        int i = compit.size() - 1;
        for (; i >= 0; i--) {
          Iterator<IExpr> iter = compit.get(i);
          if (iter.hasNext()) {
            break;
          }
        }
        if (i < 0) {
          empty = true;
          return res;
        }
        // update iterators
        for (int j = i + 1; j < compit.size(); j++) {
          Iterator<IExpr> iter = comps.get(j).iterator();
          compit.set(j, iter);
        }
        // update current
        for (int j = i; j < compit.size(); j++) {
          Iterator<IExpr> iter = compit.get(j);
          IExpr el = iter.next();
          current.set(j + 1, el);
        }
        return res;
      }

      /**
       * Remove a tuple if allowed.
       *
       * @throws UnsupportedOperationException
       */
      @Override
      public void remove() {
        throw new UnsupportedOperationException("cannnot remove tuples");
      }
    }

    /**
     * Cartesian product iterable. <br>
     * See <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian
     * product</a>
     */
    static final class CartesianProductList implements Iterable<IAST> {

      /** data structure. */
      public final List<IAST> comps;

      private final IASTAppendable fEmptyResultList;

      /**
       * CartesianProduct constructor.
       *
       * @param comps components of the cartesian product.
       */
      public CartesianProductList(List<IAST> comps, IASTAppendable emptyResultList) {
        if (comps == null) {
          throw new IllegalArgumentException("null components not allowed");
        }
        this.comps = comps;
        this.fEmptyResultList = emptyResultList;
      }

      /**
       * Get an iterator over subsets.
       *
       * @return an iterator.
       */
      @Override
      public Iterator<IAST> iterator() {
        return new CartesianProductIterator(comps, fEmptyResultList);
      }

      int size() {
        return comps.size();
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<IAST> la = new ArrayList<IAST>(ast.argSize());
      int resultSize = 1;
      for (int i = 1; i < ast.size(); i++) {
        if (ast.get(i).isList()) {
          IAST subList = (IAST) ast.get(i);
          la.add(subList);
          resultSize *= subList.size();
        } else {
          return F.NIL;
        }
      }
      CartesianProductList cpi = new CartesianProductList(la, F.ListAlloc(la.size()));
      IASTAppendable result = F.ListAlloc(resultSize);
      for (IAST iast : cpi) {
        result.append(iast);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  private static final class DiceDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n11 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
            n11++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        return F.Divide(F.ZZ((long) n10 + (long) n01), F.ZZ(2L * n11 + n10 + n01));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * Generate all integer partitions for a given integer number. See <a
   * href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer partition</a>
   */
  private static final class IntegerPartitions extends AbstractFunctionEvaluator {

    /**
     * Returns all partitions of a given int number (i.e. NumberPartitions(3) => [3,0,0] [2,1,0]
     * [1,1,1] ).
     *
     * <p>See <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer
     * partition</a>
     */
    public static final class NumberPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

      private final int n;

      private final int len;

      private final int fPartititionsIndex[];

      private int i;

      private int k;

      private final int fCopiedResultIndex[];

      private int fResultIndex[];

      /** @param n with <code>n > 1</code> */
      public NumberPartitionsIterable(final int n) {
        this(n, n);
      }

      /**
       * @param n with <code>n > 1</code>
       * @param l
       */
      public NumberPartitionsIterable(final int n, final int l) {
        super();
        this.n = n;
        len = l;
        int size = n;
        if (len > n) {
          size = len;
        }
        if (Config.MAX_AST_SIZE < size) {
          ASTElementLimitExceeded.throwIt(size);
        }
        fPartititionsIndex = new int[size];
        fCopiedResultIndex = new int[size];
        fResultIndex = nextBeforehand();
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
            // if (i != len - 1) {
            fPartititionsIndex[i] -= 1;
            // }
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

            } else {
              k++;
              if (fPartititionsIndex[i] != 1) {
                i = k;
              }
              if (fPartititionsIndex[i] == 1) {
                i--;
              }
              continue;
            }

            return fPartititionsIndex;
          }
        } else {
          fPartititionsIndex[0] = n;

          k = 0;
          i = 0;
        }
        return fPartititionsIndex;
      }

      @Override
      public int[] next() {
        System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
        fResultIndex = nextBeforehand();
        return fCopiedResultIndex;
      }

      @Override
      public boolean hasNext() {
        return fResultIndex != null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<int[]> iterator() {
        return this;
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // TODO github #167: generalize IntegerPartitions with the use of FrobeniusSolver
      IntRangeSpec range = IntRangeSpec.createNonNegative(ast, 2);
      if (range != null) {
        IExpr arg1 = ast.arg1();
        if (arg1.isInteger()) {
          final int n = arg1.toIntDefault(-1);
          if (n >= 0) {
            int max = range.maximum();
            if (max > n) {
              range = new IntRangeSpec(1, n);
              max = range.maximum();
            }
            if (ast.isAST3()) {
              return frobeniusPartition(ast, engine);
            }

            if (n == 0) {
              return F.List(F.List());
            }
            if (n == 1) {
              return F.List(F.List(F.C1));
            }
            if (range.isIncluded(0) && !range.isIncluded(1)) {
              return F.CEmptyList;
            }
            // try {
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
                  temp.append(F.ZZ(j[i]));
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
          }
          if (arg1.isNegative()) {
            return F.CEmptyList;
          }
        } else if (arg1.isFraction()) {
          if (ast.size() == 2) {
            return F.CEmptyList;
          }
        }
      }
      return F.NIL;
    }

    private static IExpr frobeniusPartition(final IAST ast, EvalEngine engine) {
      if (ast.arg3().isNonEmptyList() && ast.arg1().isInteger()) {
        try {
          int[] listInt =
              Validate.checkListOfInts(
                  ast, ast.arg3(), Integer.MIN_VALUE, Integer.MAX_VALUE, engine);
          if (listInt != null) {
            IInteger lowerLimitOfCoins = F.C0;
            IInteger upperLimitOfCoins = F.ZZ(Integer.MAX_VALUE);
            if (ast.arg2().isInteger()) {
              upperLimitOfCoins = (IInteger) ast.arg2();
            } else if (ast.arg2().isAST(F.List, 3)
                && ast.arg2().first().isInteger()
                && ast.arg2().second().isInteger()) {
              lowerLimitOfCoins = (IInteger) ast.arg2().first();
              upperLimitOfCoins = (IInteger) ast.arg2().second();
            } else if (ast.arg2() != F.All) {
              return F.NIL;
            }
            IInteger[] solution;

            FrobeniusSolver solver = FrobeniusSolve.getSolver(listInt, (IInteger) ast.arg1());
            int numberOfSolutions = -1; // all solutions
            if (ast.size() == 5) {
              numberOfSolutions = ast.arg5().toIntDefault(-1);
            }

            IASTAppendable result = F.ListAlloc(8);
            int iterations = 0;
            int iterationLimit = engine.getIterationLimit();
            while ((solution = solver.take()) != null) {
              if (iterationLimit > 0 && iterations > iterationLimit) {
                IOFunctions.printMessage(
                    ast.topHead(), "itlimpartial", F.List(F.ZZ(iterationLimit)), engine);
                return result;
              }
              if (numberOfSolutions >= 0) {
                if (--numberOfSolutions < 0) {
                  break;
                }
              }
              iterations++;
              if (createFrobeniusSolution(
                  solution, listInt, lowerLimitOfCoins, upperLimitOfCoins, result)) {
                continue;
              }
              return F.NIL;
            }

            return result;
          }
        } catch (LimitException le) {
          throw le;
        } catch (RuntimeException rex) {
          if (FEConfig.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
        }
      }
      return F.NIL;
    }

    /**
     * @param frobeniusSolution a single solution from the FrobeniusSolver
     * @param numberSpecification original given possible numbers for the partitions
     * @param lowerLimitOfCoins minimum number of coins
     * @param upperLimitOfCoins maximum number of coins
     * @param result
     * @return
     */
    private static boolean createFrobeniusSolution(
        IInteger[] frobeniusSolution,
        int[] numberSpecification,
        IInteger lowerLimitOfCoins,
        IInteger upperLimitOfCoins,
        IASTAppendable result) {
      IInteger sum = F.C0;
      for (int i = 0; i < frobeniusSolution.length; i++) {
        sum = sum.add(frobeniusSolution[i]);
      }
      if (sum.isGE(lowerLimitOfCoins) && sum.isLE(upperLimitOfCoins)) {
        IASTAppendable list = F.ListAlloc();
        for (int i = frobeniusSolution.length - 1; i >= 0; i--) {
          int counter = frobeniusSolution[i].toIntDefault();
          if (counter == Integer.MIN_VALUE) {
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static class JaccardDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n11 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
            n11++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        return F.Divide(F.ZZ((long) n10 + (long) n01), F.ZZ((long) n11 + (long) n10 + n01));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class KOrderlessPartitions extends AbstractFunctionEvaluator {

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int k = ast.arg2().toIntDefault();
      if (ast.arg1().isAST() && ast.arg1().size() > 1 && k > 0) {
        final IAST listArg0 = (IAST) ast.arg1();
        if (k == 1) {
          return F.List(listArg0);
        }
        final int n = listArg0.argSize();
        if (k > n) {
          return F.NIL;
        }
        final ISymbol sym = listArg0.topHead();
        final IASTAppendable result = F.ListAlloc(50);
        final Permutations.KPermutationsIterable permutationIterator =
            new Permutations.KPermutationsIterable(listArg0, n, 1);
        final KPartitions.KPartitionsIterable partitionIterator =
            new KPartitions.KPartitionsIterable(n, k);
        IAST partition;

        // first generate all permutations:
        for (int permutationsIndex[] : permutationIterator) {
          // second generate all partitions:
          for (int partitionsIndex[] : partitionIterator) {
            partition = createSinglePartition(listArg0, sym, permutationsIndex, partitionsIndex);
            if (partition.isPresent()) {
              result.append(partition);
            }
          }
          partitionIterator.reset();
        }
        return result;
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private IAST createSinglePartition(
        final IAST listArg0,
        final ISymbol sym,
        final int[] permutationsIndex,
        final int[] partitionsIndex) {
      IASTAppendable partitionElement;
      int partitionStartIndex;
      IASTAppendable partition = F.ListAlloc(partitionsIndex.length + 1);

      final int n = listArg0.argSize();
      // 0 is always the first index of a partition
      partitionStartIndex = 0;
      for (int i = 1; i < partitionsIndex.length; i++) {
        partitionElement = F.ast(sym);
        if (partitionStartIndex + 1 == partitionsIndex[i]) {
          // OneIdentity check here
          if (sym.hasOneIdentityAttribute()) {
            partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
          } else {
            partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
            partition.append(partitionElement);
          }
        } else {
          for (int m = partitionStartIndex; m < partitionsIndex[i]; m++) {
            if (m + 1 < partitionsIndex[i]) {
              if ((listArg0.get(permutationsIndex[m + 1] + 1))
                  .isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
                return F.NIL;
              }
            }
            partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
          }
          partition.append(partitionElement);
        }
        partitionStartIndex = partitionsIndex[i];
      }
      // generate all elements for the last partitionElement of a
      // partition:
      partitionElement = F.ast(sym);
      if (partitionStartIndex + 1 == n) {
        // OneIdentity check here
        if ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY) {
          partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
        } else {
          partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
          partition.append(partitionElement);
        }
      } else {
        for (int m = partitionStartIndex; m < n; m++) {
          if (m + 1 < n) {
            if ((listArg0.get(permutationsIndex[m + 1] + 1))
                .isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
              return F.NIL;
            }
          }
          partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
        }
        partition.append(partitionElement);
      }

      return partition;
    }
  }

  /**
   * Generate a list of all all k-partitions for a given list with N elements. <br>
   * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a
   * set</a>
   */
  private static final class KPartitions extends AbstractFunctionEvaluator {
    /**
     * This class returns the indexes for partitioning a list of N elements. <br>
     * Usage pattern:
     *
     * <pre>
     * final KPartitionsIterable iter = new KPartitionsIterable(n, k);
     * for (int[] partitionsIndex : iter) {
     *   ...
     * }
     * </pre>
     *
     * Example: KPartitionsIterable(3,5) gives the following sequences [0, 1, 2], [0, 1, 3], [0, 1,
     * 4], [0, 2, 3], [0, 2, 4], [0, 3, 4] <br>
     * If you interpret these integer lists as indexes for a list {a,b,c,d,e} which should be
     * partitioned into 3 parts the results are: <br>
     * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}}, {{a,b},{c},{d,e}},
     * {{a,b},{c,d},{e}}, {{a,b,c},{d},{e}}} <br>
     * <br>
     * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a
     * set</a>
     */
    public static final class KPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

      private final int fLength;

      private final int fNumberOfParts;

      private final int fPartitionsIndex[];

      private final int fCopiedResultIndex[];

      private int fResultIndex[];

      public KPartitionsIterable(final int length, final int parts) {
        super();
        if (parts > length || parts < 1) {
          throw new IllegalArgumentException(
              "KPartitionsIterable: parts " + parts + " > " + length);
        }
        fLength = length;
        fNumberOfParts = parts;
        fPartitionsIndex = new int[fNumberOfParts];
        fCopiedResultIndex = new int[fNumberOfParts];
        fPartitionsIndex[0] = -1;
        fResultIndex = nextBeforehand();
      }

      public final void reset() {
        fResultIndex = null;
        for (int i = 1; i < fNumberOfParts; i++) {
          fPartitionsIndex[i] = 0;
        }
        fPartitionsIndex[0] = -1;
        fResultIndex = nextBeforehand();
      }

      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      private final int[] nextBeforehand() {
        if (fPartitionsIndex[0] < 0) {
          for (int i = 0; i < fNumberOfParts; ++i) {
            fPartitionsIndex[i] = i;
          }
          return fPartitionsIndex;
        } else {
          int i = 0;
          for (i = fNumberOfParts - 1;
              (i >= 0) && (fPartitionsIndex[i] >= fLength - fNumberOfParts + i);
              --i) {}
          if (i <= 0) {
            return null;
          }
          fPartitionsIndex[i]++;
          for (int m = i + 1; m < fNumberOfParts; ++m) {
            fPartitionsIndex[m] = fPartitionsIndex[m - 1] + 1;
          }
          return fPartitionsIndex;
        }
      }

      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public int[] next() {
        System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
        fResultIndex = nextBeforehand();
        return fCopiedResultIndex;
      }

      @Override
      public boolean hasNext() {
        return fResultIndex != null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<int[]> iterator() {
        return this;
      }
    }

    /**
     * This <code>Iterable</code> iterates through all k-partition lists for a given list with N
     * elements. <br>
     * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a
     * set</a>
     */
    public static final class KPartitionsList implements Iterator<IAST>, Iterable<IAST> {

      private final IAST fList;
      private final IAST fResultList;
      private final int fOffset;
      private final KPartitionsIterable fIterable;

      public KPartitionsList(final IAST list, final int kParts, IAST resultList, final int offset) {
        super();
        fIterable = new KPartitionsIterable(list.size() - offset, kParts);
        fList = list;
        fResultList = resultList;
        fOffset = offset;
      }

      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public IAST next() {
        int[] partitionsIndex = fIterable.next();
        if (partitionsIndex == null) {
          return null;
        }
        IASTAppendable part = fResultList.copyAppendable();
        IASTAppendable temp;
        int j = 0;
        for (int i = 1; i < partitionsIndex.length; i++) {
          temp = fResultList.copyAppendable();
          for (int m = j; m < partitionsIndex[i]; m++) {
            temp.append(fList.get(m + fOffset));
          }
          j = partitionsIndex[i];
          part.append(temp);
        }

        temp = fResultList.copyAppendable();
        int n = fList.size() - fOffset;
        for (int m = j; m < n; m++) {
          temp.append(fList.get(m + fOffset));
        }
        part.append(temp);
        return part;
      }

      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<IAST> iterator() {
        return this;
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST() && ast.arg2().isInteger()) {
        final IAST listArg0 = (IAST) ast.arg1();
        final int k = ast.get(2).toIntDefault();
        if (k > 0 && k <= listArg0.argSize()) {
          final KPartitionsList iter = new KPartitionsList(listArg0, k, F.ast(F.List), 1);
          final IASTAppendable result = F.ListAlloc(16);
          for (IAST part : iter) {
            result.append(part);
          }
          return result;
        }
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class MatchingDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        return F.Divide(F.ZZ((long) n10 + (long) n01), F.ZZ(length - 1));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * @see Permutations
   * @see Subsets
   */
  private static class Partition extends AbstractFunctionEvaluator {

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        final int n = ast.arg2().toIntDefault();
        if (n > 0) {
          final IAST f = (IAST) ast.arg1();
          final IASTAppendable result = F.ast(f.head());
          IASTAppendable temp;
          int i = n;
          int v = n;
          if ((ast.isAST3()) && ast.arg3().isInteger()) {
            v = ast.arg3().toIntDefault();
          }
          if (v > 0) {
            while (i <= f.argSize()) {
              if (i < 1) {
                // i+=v might overflow and create a negative value
                break;
              }
              temp = F.ast(f.head());
              for (int j = i - n; j < i; j++) {
                if (j + 1 < 1 || j + 1 >= f.size()) {
                  return result;
                }
                temp.append(f.get(j + 1));
              }
              result.append(temp);
              i += v;
            }
            return result;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * Generate a list of (multiset) permutations
   *
   * <p>See <a href=" http://en.wikipedia.org/wiki/Permutation">Permutation</a>
   *
   * @see Partition
   * @see Subsets
   */
  public static final class Permutations extends AbstractFunctionEvaluator {

    /**
     * Generate an <code>java.lang.Iterable</code> for (multiset) permutations
     *
     * <p>See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
     */
    public static final class KPermutationsIterable implements Iterator<int[]>, Iterable<int[]> {

      private final int n;

      private final int k;

      private final int fPermutationsIndex[];

      private final int y[];

      private boolean first;

      private int h, i, m;

      private final int fCopiedResultIndex[];

      private int fResultIndex[];

      public KPermutationsIterable(final IAST fun, final int parts, final int headOffset) {
        super();
        n = fun.size() - headOffset;
        k = parts;
        if (parts > n || parts < 1) {
          throw new IllegalArgumentException("KPermutationsIterable: parts " + parts + " > " + n);
        }

        fPermutationsIndex = new int[n];
        y = new int[n];
        fCopiedResultIndex = new int[n];
        fPermutationsIndex[0] = 0;
        y[0] = 0;
        for (int a = 1; a < n; a++) {
          if (fun.get(a + headOffset).equals(fun.get(a + headOffset - 1))) {
            fPermutationsIndex[a] = fPermutationsIndex[a - 1];
          } else {
            fPermutationsIndex[a] = a;
          }
          y[a] = a;
        }
        if (k == n) {
          m = k - 1;
        } else {
          m = k;
        }
        first = true;
        i = m - 1;
        fResultIndex = nextBeforehand();
      }

      /**
       * Create an iterator which gives all possible permutations of <code>data</code> which
       * contains at most <code>parts</code> number of elements. Repeated elements are treated as
       * same.
       *
       * @param data a list of integers which should be permutated.
       * @param parts
       */
      public KPermutationsIterable(final int[] data, final int parts) {
        this(data, data.length, parts);
      }

      /**
       * Create an iterator which gives all possible permutations of <code>data</code> which
       * contains at most <code>parts</code> number of elements. Repeated elements are treated as
       * same.
       *
       * @param data a list of integers which should be permutated.
       * @param len consider only the first <code>n</code> elements of <code>data</code> for
       *     permutation
       * @param parts
       */
      public KPermutationsIterable(final int[] data, final int len, final int parts) {
        super();
        n = len;
        k = parts;
        if (parts > n || parts < 1) {
          throw new IllegalArgumentException("KPermutationsIterable: parts " + parts + " > " + n);
        }
        fPermutationsIndex = new int[n];
        y = new int[n];
        fCopiedResultIndex = new int[n];
        for (int a = 0; a < n; a++) {
          fPermutationsIndex[a] = data[a];
          y[a] = a;
        }
        if (k == n) {
          m = k - 1;
        } else {
          m = k;
        }
        first = true;
        i = m - 1;
        fResultIndex = nextBeforehand();
      }

      @Override
      public boolean hasNext() {
        return fResultIndex != null;
      }

      @Override
      public Iterator<int[]> iterator() {
        return this;
      }

      @Override
      public int[] next() {
        System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
        fResultIndex = nextBeforehand();
        return fCopiedResultIndex;
      }

      /** */
      private final int[] nextBeforehand() {
        if (first) {
          first = false;
          return fPermutationsIndex;
        }
        do {
          if (y[i] < (n - 1)) {
            y[i] = y[i] + 1;
            if (fPermutationsIndex[i] != fPermutationsIndex[y[i]]) {
              // check fixpoint
              h = fPermutationsIndex[i];
              fPermutationsIndex[i] = fPermutationsIndex[y[i]];
              fPermutationsIndex[y[i]] = h;
              i = m - 1;
              return fPermutationsIndex;
            }
            continue;
          }
          do {
            h = fPermutationsIndex[i];
            fPermutationsIndex[i] = fPermutationsIndex[y[i]];
            fPermutationsIndex[y[i]] = h;
            y[i] = y[i] - 1;
          } while (y[i] > i);
          i--;
        } while (i != -1);
        return null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    /**
     * Generate an <code>java.lang.Iterable<IAST></code> for (multiset) permutations
     *
     * <p>See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
     */
    private static final class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

      private final IAST fList;
      private final IAST fResultList;
      private final int fOffset;
      private final int fParts;
      private final KPermutationsIterable fIterable;

      /**
       * Create an iterator which gives all possible permutations of <code>list</code> which
       * contains at most <code>parts</code> number of elements. Repeated elements are treated as
       * same.
       *
       * @param list a list of elements
       * @param parts contain at most parts elements in ech permutation
       * @param resultList a template AST where the elements could be appended.
       * @param offset the offset from which to start the list of elements in the list
       */
      public KPermutationsList(
          final IAST list, final int parts, IAST resultList, final int offset) {
        fIterable = new KPermutationsIterable(list, parts, offset);
        fList = list;
        fResultList = resultList;
        fOffset = offset;
        fParts = parts;
      }

      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }

      @Override
      public Iterator<IAST> iterator() {
        return this;
      }

      /**
       * Get the index array for the next permutation.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public IAST next() {
        int[] permutationsIndex = fIterable.next();
        if (permutationsIndex == null) {
          return null;
        }
        IASTAppendable temp = fResultList.copyAppendable();
        // parts <= permutationsIndex.length
        for (int i = 0; i < fParts; i++) {
          temp.append(fList.get(permutationsIndex[i] + fOffset));
        }
        return temp;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        final IAST list = (IAST) ast.arg1();
        int parts = list.argSize();
        if (ast.isAST2()) {
          if (ast.arg2().isInteger()) {
            int maxPart = ast.arg2().toIntDefault();
            if (maxPart >= 0) {
              maxPart = maxPart < parts ? maxPart : parts;
              final IASTAppendable result = F.ListAlloc(100);
              for (int i = 0; i <= maxPart; i++) {
                createPermutationsWithNParts(list, i, result);
              }
              return result;
            }
            return F.NIL;
          }
          if (ast.arg2().isList()) {
            IAST sequence = (IAST) ast.arg2();
            // TODO use ISequence here
            if (!sequence.isAST1() || !sequence.arg1().isInteger()) {
              return F.NIL;
            }
            parts = Validate.checkIntType(F.Permutations, sequence.arg1(), 0, engine);
            if (parts < 0 && parts > list.argSize()) {
              return F.NIL;
            }
          }
        }
        if (parts < 0) {
          return F.NIL;
        }
        if (parts > list.argSize()) {
          return F.CEmptyList;
        }
        final IASTAppendable result = F.ListAlloc(100);
        return createPermutationsWithNParts(list, parts, result);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * All permutations with exactly <code>parts</code>.
     *
     * @param list
     * @param parts
     * @param result
     * @return
     */
    private IAST createPermutationsWithNParts(
        final IAST list, int parts, final IASTAppendable result) {
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
      for (IAST temp : perm) {
        result.append(temp);
      }
      return result;
    }
  }

  private static final class RogersTanimotoDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n00 = 0;
        int n11 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
            n00++;
            continue;
          }
          if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
            n11++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        long r = 2L * ((long) n10 + (long) n01);
        if (r == 0L) {
          return F.C0;
        }
        return F.Divide(F.ZZ(r), F.ZZ((long) n11 + (long) n00 + r));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class RussellRaoDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n00 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
            n00++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        long r = (long) n10 + (long) n01 + n00;
        if (r == 0L) {
          return F.C0;
        }
        return F.Divide(F.ZZ(r), F.ZZ(length - 1));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static final class Signature extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        if (list.argSize() == 1) {
          return F.C1;
        }
        int n = 0;
        for (int i = 1; i < list.size(); i++) {
          for (int j = i + 1; j < list.size(); j++) {
            int compareTo = list.get(i).compareTo(list.get(j));
            if (compareTo > 0) {
              n++;
            } else if (compareTo == 0) {
              return F.C0;
            }
          }
        }

        return ((n % 2) == 0) ? F.C1 : F.CN1;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class SokalSneathDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n11 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
            continue;
          }
          if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
            n11++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        long r = 2L * ((long) n10 + (long) n01);
        if (r == 0L) {
          return F.C0;
        }
        return F.Divide(F.ZZ(r), F.ZZ(n11 + r));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * Generate a list of all k-combinations from a given list
   *
   * <p>See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
   */
  public static final class Subsets extends AbstractFunctionEvaluator {

    /**
     * Iterate over the lists of all k-combinations from a given list
     *
     * <p>See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
     */
    public static final class KSubsetsList implements Iterator<IAST>, Iterable<IAST> {

      private final IAST fList;
      private final IAST fResultList;
      private final int fOffset;
      private final Iterator<int[]> fIterable;
      private final int fK;

      public KSubsetsList(
          final Iterator<int[]> iterable, final IAST list, final int k, IAST resultList) {
        this(iterable, list, k, resultList, 0);
      }

      public KSubsetsList(
          final Iterator<int[]> iterable,
          final IAST list,
          final int k,
          IAST resultList,
          final int offset) {
        fIterable = iterable;
        fList = list;
        fK = k;
        fResultList = resultList;
        fOffset = offset;
      }

      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public IAST next() {
        int j[] = fIterable.next();
        if (j == null) {
          return null;
        }

        IASTAppendable temp = fResultList.copyAppendable();
        return temp.appendArgs(
            0,
            fK,
            i -> {
              if (j.length > i && fList.size() > (j[i] + fOffset)) {
                return fList.get(j[i] + fOffset);
              }
              return F.NIL;
            });
        // for (int i = 0; i < fK; i++) {
        // temp.append(fList.get(j[i] + fOffset));
        // }
        //
        // return temp;
      }

      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<IAST> iterator() {
        return this;
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.NIL;
      }
      if (ast.arg1().isAST()) {
        try {
          final IAST f = (IAST) ast.arg1();
          int n = f.argSize();
          SetSpecification level = new SetSpecification(0, n);
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            if (arg2 != F.All && !arg2.isInfinity()) {
              if (arg2.isInteger()) {
                n = arg2.toIntDefault();
                if (n > Integer.MIN_VALUE) {
                  level = new SetSpecification(0, n > f.argSize() ? f.argSize() : n);
                } else {
                  return F.NIL;
                }
              } else {
                level = new SetSpecification(arg2);
              }
            }
          }

          int k;
          final IASTAppendable result = F.ast(F.List);
          level.setMinCountAsCurrent();
          while (level.isInRange()) {
            k = level.getCurrentCounter();
            if (k > f.argSize()) {
              return F.CEmptyList;
            }
            final KSubsetsList iter = createKSubsets(f, k, F.ast(f.head()), 1);
            for (IAST part : iter) {
              if (part == null) {
                break;
              }
              result.append(part);
            }
            level.incCurrentCounter();
          }

          return result;
        } catch (final ValidateException ve) {
          // see level specification
          return engine.printMessage(ast.topHead(), ve);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    public static KSubsetsList createKSubsets(
        final IAST list, final int k, IAST resultList, final int offset) {
      return new KSubsetsList(
          new KSubsets.KSubsetsIterable(list.size() - offset, k), list, k, resultList, offset);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Tuples(list, n)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>creates a list of all <code>n</code>-tuples of elements in <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Tuples({list1, list2, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns a list of tuples with elements from the given lists.
   *
   * </blockquote>
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Tuple">Wikipedia - Tuple</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Tuples({a, b, c}, 2)
   * {{a,a},{a,b},{a,c},{b,a},{b,b},{b,c},{c,a},{c,b},{c,c}}
   *
   * &gt;&gt; Tuples[{{a, b}, {1, 2, 3}}]
   * {{a,1},{a,2},{a,3},{b,1},{b,2},{b,3}}
   * </code>
   * </pre>
   */
  private static final class Tuples extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1() && arg1.isList()) {

        IAST list = (IAST) arg1;
        if (list.exists(x -> !x.isAST())) {
          return F.NIL;
        }
        IASTAppendable result = F.ListAlloc(16);
        IAST temp = F.List();
        tuplesOfLists(list, 1, result, temp, ast, engine);
        return result;
        
      } else if (ast.isAST2() && arg1.isAST() ) {
        int k = ast.arg2().toIntDefault();
        if (k >= 0) {
          IASTAppendable result = F.ListAlloc(16);
          IAST temp = F.ast(arg1.head());
          tuples((IAST) arg1, k, result, temp, ast, engine);
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * Generate all n-tuples form a list.
     *
     * @param originalList
     * @param n
     * @param result
     * @param subResult
     * @param ast
     * @param engine
     */
    private static void tuples(
        final IAST originalList,
        final int n,
        IASTAppendable result,
        IAST subResult,
        IAST ast,
        EvalEngine engine) {
      if (n == 0) {
        result.append(subResult);
        return;
      }
      final int recursionLimit = engine.getRecursionLimit();

      try {

        if (recursionLimit > 0) {
          int counter = engine.incRecursionCounter();
          if (counter > recursionLimit) {
            RecursionLimitExceeded.throwIt(counter, ast);
          }
        }
        IASTAppendable temp;
        for (int j = 1; j < originalList.size(); j++) {
          temp = subResult.copyAppendable();
          temp.append(originalList.get(j));
          tuples(originalList, n - 1, result, temp, ast, engine);
        }
      } finally {
        if (recursionLimit > 0) {
          engine.decRecursionCounter();
        }
      }
    }

    /**
     * Generate all tuples from a list of lists.
     *
     * @param originalList the list of lists
     * @param k
     * @param result the result list
     * @param subResult the current subList which should be inserted in the result list
     * @param ast
     * @param engine
     */
    private void tuplesOfLists(
        final IAST originalList,
        final int k,
        IASTAppendable result,
        IAST subResult,
        IAST ast,
        EvalEngine engine) {
      if (k == originalList.size()) {
        result.append(subResult);
        return;
      }
      final int recursionLimit = engine.getRecursionLimit();
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(counter, ast);
        }
      }
      IASTAppendable temp;
      IAST subAST = (IAST) originalList.get(k);
      for (int j = 1; j < subAST.size(); j++) {
        temp = subResult.copyAppendable();
        temp.append(subAST.get(j));
        tuplesOfLists(originalList, k + 1, result, temp, ast, engine);
      }
    }
  }

  private static final class YuleDissimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int dim1 = ast.arg1().isVector();
      int dim2 = ast.arg2().isVector();
      if (dim1 == dim2 && dim1 > 0) {
        IAST u = (IAST) ast.arg1().normal(false);
        IAST v = (IAST) ast.arg2().normal(false);
        int length = u.size();
        int n10 = 0;
        int n01 = 0;
        int n00 = 0;
        int n11 = 0;
        IExpr x, y;
        for (int i = 1; i < length; i++) {
          x = u.get(i);
          y = v.get(i);
          if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
            n10++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
            n01++;
            continue;
          }
          if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
            n00++;
            continue;
          }
          if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
            n11++;
            continue;
          }
          if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
              && (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
            continue;
          }
          return F.NIL;
        }

        long r = 2L * n10 * n01;
        if (r == 0L) {
          return F.C0;
        }
        return F.Divide(F.ZZ(r), F.ZZ((long) n11 * (long) n00 + (long) n10 * (long) n01));
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static void initialize() {
    Initializer.init();
  }

  private Combinatoric() {}
}
