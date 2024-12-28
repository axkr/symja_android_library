package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.combinatoric.KPartitionsIterable;
import org.matheclipse.core.combinatoric.KPartitionsList;
import org.matheclipse.core.combinatoric.KSubsetsIterable;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IntRangeSpec;
import org.matheclipse.core.eval.util.MutableInt;
import org.matheclipse.core.eval.util.SetSpecification;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.FrobeniusSolve;

public final class Combinatoric {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.CartesianProduct.setEvaluator(new CartesianProduct());
      S.Cycles.setEvaluator(new Cycles());
      S.DiceDissimilarity.setEvaluator(new DiceDissimilarity());
      S.FindPermutation.setEvaluator(new FindPermutation());
      S.IntegerPartitions.setEvaluator(new IntegerPartitions());
      S.JaccardDissimilarity.setEvaluator(new JaccardDissimilarity());
      S.KOrderlessPartitions.setEvaluator(new KOrderlessPartitions());
      S.KPartitions.setEvaluator(new KPartitions());
      S.MatchingDissimilarity.setEvaluator(new MatchingDissimilarity());
      S.Partition.setEvaluator(new Partition());
      S.Permute.setEvaluator(new Permute());
      S.PermutationCycles.setEvaluator(new PermutationCycles());
      S.PermutationCyclesQ.setEvaluator(new PermutationCyclesQ());
      S.PermutationList.setEvaluator(new PermutationList());
      S.PermutationListQ.setEvaluator(new PermutationListQ());
      S.PermutationReplace.setEvaluator(new PermutationReplace());
      S.Permutations.setEvaluator(new Permutations());
      S.PolygonalNumber.setEvaluator(new PolygonalNumber());
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
   *
   *
   * <pre>
   * <code>CartesianProduct(list1, list2)
   * </code>
   * </pre>
   *
   * <p>
   * returns the cartesian product for multiple lists.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CartesianProduct({1,2},{3,4})
   * {{1,3},{1,4},{2,3},{2,4}}
   * </code>
   * </pre>
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

        final IAST res = current.copy();
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

  /**
   *
   *
   * <pre>
   * <code>Cycles(a, b)
   * </code>
   * </pre>
   *
   * <p>
   * expression for defining canonical cycles of a permutation.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Cyclic_permutation">Wikipedia: Cyclic
   * permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The singletons <code>{2}</code> and <code>{5}</code> are deleted:
   *
   * <pre>
   * <code>&gt;&gt; PermutationCycles({4,2,7,6,5,8,1,3})
   * Cycles({{1,4,6,8,3,7}})
   * </code>
   * </pre>
   */
  private static final class Cycles extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Cycles)) {
        if (ast.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
          return F.NIL;
        }
        IExpr temp = canonicalizeCycles(ast, false, engine);
        if (temp.equals(ast)) {
          ast.builtinEvaled();
          return F.NIL;
        }
        return temp;
      }
      return F.NIL;
    }

    /**
     * The <code>cycles</code> expression is canonicalized by dropping empty and singleton cycles,
     * rotating each cycle so that the smallest position is at index 1.
     *
     * @param cycles <code>S.Cycles({{...}, ...})</code> expression
     * @param quiet if <code>true</code> suppress the output of error messages
     * @param engine
     * @return the canonicalized expression or otherwise <code>F.NIL</code>, if the <code>cycles
     *     </code> is no valid <code>Cycles</code> expression.
     */
    private static IAST canonicalizeCycles(final IAST cycles, boolean quiet, EvalEngine engine) {
      if (cycles.arg1().isList()) {
        IAST mainList = (IAST) cycles.arg1();
        if (mainList.isEmptyList()) {
          cycles.builtinEvaled();
          return F.NIL;
        }
        if (!mainList.isListOfLists()) {
          // normalize for possible SparseArray expressions
          IExpr temp = mainList.normal(false);
          if (!temp.isListOfLists()) {
            if (!quiet && temp.isAST()) {
              IAST list = (IAST) temp;
              for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isNumber()) {
                  // `1` is expected to contain a list of lists of integers.
                  return Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
                }
              }
            }
            return F.NIL;
          }
          mainList = (IAST) temp;
        }

        IASTAppendable result = F.ListAlloc(mainList.argSize());
        Set<IExpr> set = new HashSet<IExpr>();
        for (int j = 1; j < mainList.size(); j++) {
          IAST list = (IAST) mainList.get(j);
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);
            if (arg.isInteger()) {
              if (!arg.isPositive()) {
                if (!quiet) {
                  // `1` contains integers that are not positive.
                  Errors.printMessage(S.Cycles, "pospoint", F.list(cycles), engine);
                }
                return F.NIL;
              }
              if (set.contains(arg)) {
                if (!quiet) {
                  // `1` contains repeated integers.
                  Errors.printMessage(S.Cycles, "reppoint", F.list(cycles), engine);
                }
                return F.NIL;
              }
              set.add(arg);
            } else {
              if (arg.isNumber()) {
                if (!quiet) {
                  // `1` is expected to contain a list of lists of integers.
                  Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
                }
                return F.NIL;
              }

              // symbolic args => return unevaluated
              return F.NIL;
            }
          }

          if (list.size() > 2) {
            // drop empty and singleton cycles

            // rotate cycle by rotateLeftPositions so that the smallest position is at index 1
            int rotateLeftPositions = 0;
            IInteger value = (IInteger) list.get(1);
            for (int i = 1; i < list.size(); i++) {
              IInteger arg = (IInteger) list.get(i);
              if (value.isGT(arg)) {
                value = arg;
                rotateLeftPositions = i - 1;
              }
            }
            if (rotateLeftPositions > 0) {
              IASTAppendable newList = F.ListAlloc(list.size());
              result.append(list.rotateLeft(newList, rotateLeftPositions));
            } else {
              result.append(list);
            }
          }
        }

        EvalAttributes.sort(result, Comparators.LEXICAL_COMPARATOR);
        IAST resultCycles = F.Cycles(result);
        resultCycles.builtinEvaled();
        return resultCycles;
      }
      if (!quiet) {
        // `1` is expected to contain a list of lists of integers.
        Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>DiceDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Dice dissimilarity between the two boolean 1-D lists <code>u</code> and <code>v
   * </code>, which is defined as <code>(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)</code>, where n is
   * <code>len(u)</code> and <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code>
   * and <code>v(k)=j</code> for <code>k&lt;n</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; DiceDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 1/2
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>FindPermutation(list1, list2)
   * </code>
   * </pre>
   *
   * <p>
   * create a <code>Cycles({{...},{...}, ...})</code> permutation expression, for two lists whose
   * arguments are the same but may be differently arranged.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindPermutation(CharacterRange(&quot;a&quot;,&quot;d&quot;),{&quot;a&quot;,&quot;d&quot;,&quot;c&quot;,&quot;b&quot;})
   * Cycles({{2,4}})
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>, <a href="Permute.md">Permute</a>
   */
  private static final class FindPermutation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isAST()) {
        // Nonatomic expression expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
      }

      IAST ast1 = (IAST) arg1;
      IAST ast2;
      IExpr arg2;
      if (ast.isAST2()) {
        arg2 = ast.arg2();
        if (!arg2.isAST()) {
          // Nonatomic expression expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "normal", F.list(F.C2, ast), engine);
        }
        ast2 = (IAST) arg2;
      } else {
        if (ast1.size() == 1) {
          return F.Cycles(F.CEmptyList);
        }
        IExpr ordering = S.Ordering.of(engine, ast1);
        if (ordering.isList()) {
          return permutationCycles((IAST) ordering, engine);
        }
        return F.NIL;
      }
      if (ast1.size() != ast2.size() || !ast1.head().equals(ast2.head())) {
        // Expressions `1` and `2` cannot be related by a permutation.
        return Errors.printMessage(ast.topHead(), "norel", F.list(ast1, ast2), engine);
      }
      if (ast1.size() == 1) {
        return F.Cycles(F.CEmptyList);
      } else {
        Map<IExpr, MutableInt> histogramMap = MutableInt.createHistogram(ast1);
        if (!MutableInt.isEqualPermutable(ast2, histogramMap)) {
          // Expressions `1` and `2` cannot be related by a permutation.
          return Errors.printMessage(ast.topHead(), "norel", F.list(ast1, ast2), engine);
        }
      }

      IAST permList = permutationList(ast1, ast2);
      return permutationCycles(permList, engine);
    }

    /**
     * Create a permutation list from two {@link IAST} expressions, which differ only in the order
     * of their arguments. Therefore it's assumed that both expressions have same size and all
     * arguments of <code>ast1</code> appear in the arguments of <code>ast2</code>.
     *
     * @param ast1
     * @param ast2
     * @return the permutation list of all integer position permutations.
     */
    private static IAST permutationList(IAST ast1, IAST ast2) {
      int positions[] = new int[ast1.argSize()];
      for (int i = 0; i < positions.length; i++) {
        positions[i] = i + 1;
      }
      return F.mapList(ast1, expr1 -> {
        for (int i = 1; i < ast2.size(); i++) {
          if (positions[i - 1] > 0) {
            if (expr1.equals(ast2.get(i))) {
              positions[i - 1] = -1;
              return F.ZZ(i);
            }
          }
        }
        return F.NIL;
      });
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>IntegerPartitions(n)
   * </code>
   * </pre>
   *
   * <p>
   * returns all partitions of the integer <code>n</code>.
   *
   * <pre>
   * <code>IntegerPartitions(n, k)
   * </code>
   * </pre>
   *
   * <p>
   * lists the possible ways to partition <code>n</code> into smaller integers, using up to
   * <code>k</code> elements.
   *
   * <pre>
   * <code>IntegerPartitions(n, {lower, upper}, {listOfIntegers})
   * </code>
   * </pre>
   *
   * <p>
   * lists the possible ways to partition <code>n</code> with the numbers in <code>
   * {listOfIntegers}</code>, using between <code>lower</code> and <code>upper</code> number of
   * elements.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Partition_(number_theory)">Wikipedia - Partition
   * (number theory)</a>
   * <li><a href="https://en.wikipedia.org/wiki/Coin_problem">Wikipedia - Coin problem</a>
   * <li><a href="https://en.wikipedia.org/wiki/Diophantine_equation">Wikipedia - Diophantine
   * equation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; IntegerPartitions(3)
   * {{3},{2,1},{1,1,1}}
   *
   * &gt;&gt; IntegerPartitions(10,2)
   * {{10},{9,1},{8,2},{7,3},{6,4},{5,5}}
   * </code>
   * </pre>
   *
   * <p>
   * The &quot;McNugget partitions&quot; <a href="https://oeis.org/A214772">OEIS - Number of
   * partitions of n into parts 6, 9 or 20</a>.
   *
   * <pre>
   * <code>&gt;&gt; Table(Length(IntegerPartitions(i, All, {6, 9, 20})), {i,0, 100, 1})
   * {1,0,0,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0,2,0,1,1,0,0,2,0,1,2,0,1,2,0,1,2,0,1,3,0,2,2,
   * 1,1,3,0,2,3,1,2,3,1,2,3,1,2,4,1,3,3,2,2,5,1,3,4,2,3,5,2,3,5,2,3,6,2,4,5,3,3,7,2,
   * 5,6,3,4,7,3,5,7,3,5,8,3,6,7,4,5,9,3,7,8,5}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FrobeniusNumber.md">FrobeniusNumber</a>,
   * <a href="FrobeniusSolve.md">FrobeniusSolve</a>
   */
  private static final class IntegerPartitions extends AbstractFunctionEvaluator {

    /**
     * Returns all partitions of a given int number (i.e. NumberPartitions(3) => [3,0,0] [2,1,0]
     * [1,1,1] ).
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer
     * partition</a>
     */
    public static final class NumberPartitionsIterable implements Iterable<int[]> {

      private final int n;

      private final int len;

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
                // only exit for while(true)
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
      }

      /**
       * @param n with <code>n > 1</code>
       * @param l
       */
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
        // fPartititionsIndex = new int[size];
        // fCopiedResultIndex = new int[size];
        // fResultIndex = nextBeforehand();
      }

      @Override
      public Iterator<int[]> iterator() {
        return new NumberPartitionsIterator();
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
              IExpr kspec = ast.arg2();
              if (ast.arg3().equals(S.All)) {
                // IntegerPartitions(n_Integer, kspec_, All) := IntegerPartitions(n, kspec)
                return F.IntegerPartitions(F.ZZ(n), kspec);
              }
              if (ast.arg3().isList()) {
                IAST s = (IAST) ast.arg3();
                if (kspec.isList1() && kspec.first().isInteger()) {
                  IInteger k = (IInteger) kspec.first();
                  // "IntegerPartitions(n_Integer, {k_Integer}, s_List) :=
                  // ReverseSort@Select(Union(ReverseSort /@ Tuples(s, k)), Total(#) == n &),
                  return F.ReverseSort(F.Select(F.Union(F.Map(S.ReverseSort, F.Tuples(s, k))),
                      F.Function(F.Equal(F.Total(F.Slot1), n))));
                }
                if (!s.forAll(x -> x.isInteger() && x.isPositive())) {
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
              // "IntegerPartitions(n_Integer, kspec_, sspec_, m_) := Take(IntegerPartitions(n, k,
              // sspec), m)",
              return F.Take(F.IntegerPartitions(F.ZZ(n), kspec, sspec), m);
            }

            if (n == 0) {
              return F.list(F.CEmptyList);
            }
            if (n == 1) {
              return F.list(F.list(F.C1));
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
          int[] listInt = Validate.checkListOfInts(ast, ast.arg3(), Integer.MIN_VALUE,
              Integer.MAX_VALUE, engine);
          if (listInt != null) {
            IInteger lowerLimitOfCoins = F.C0;
            IInteger upperLimitOfCoins = F.ZZ(Integer.MAX_VALUE);
            if (ast.arg2().isInteger()) {
              upperLimitOfCoins = (IInteger) ast.arg2();
            } else if (ast.arg2().isList2() && ast.arg2().first().isInteger()
                && ast.arg2().second().isInteger()) {
              lowerLimitOfCoins = (IInteger) ast.arg2().first();
              upperLimitOfCoins = (IInteger) ast.arg2().second();
            } else if (ast.arg2() != S.All) {
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
                Errors.printMessage(ast.topHead(), "itlimpartial", F.list(F.ZZ(iterationLimit)),
                    engine);
                return result;
              }
              if (numberOfSolutions >= 0) {
                if (--numberOfSolutions < 0) {
                  break;
                }
              }
              iterations++;
              if (createFrobeniusSolution(solution, listInt, lowerLimitOfCoins, upperLimitOfCoins,
                  result)) {
                continue;
              }
              return F.NIL;
            }

            return result;
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

    /**
     * @param frobeniusSolution a single solution from the FrobeniusSolver
     * @param numberSpecification original given possible numbers for the partitions
     * @param lowerLimitOfCoins minimum number of coins
     * @param upperLimitOfCoins maximum number of coins
     * @param result
     * @return
     */
    private static boolean createFrobeniusSolution(IInteger[] frobeniusSolution,
        int[] numberSpecification, IInteger lowerLimitOfCoins, IInteger upperLimitOfCoins,
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
      return ARGS_1_4;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>JaccardDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jaccard-Needham dissimilarity between the two boolean 1-D lists <code>u</code> and
   * <code>v</code>, which is defined as <code>(c_tf + c_ft) / (c_tt + c_ft + c_tf)</code>, where n
   * is <code>len(u)</code> and <code>c_ij</code> is the number of occurrences of <code>
   * u(k)=i</code> and <code>v(k)=j</code> for <code>k&lt;n</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JaccardDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 2/3
   * </code>
   * </pre>
   */
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

    @Override
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
          return F.list(listArg0);
        }
        final int n = listArg0.argSize();
        if (k > n) {
          return F.NIL;
        }
        final ISymbol sym = listArg0.topHead();
        final IASTAppendable result = F.ListAlloc(50);
        final KPermutationsIterable permutationIterator = new KPermutationsIterable(listArg0, n, 1);
        IAST partition;

        // first generate all permutations:
        for (int permutationsIndex[] : permutationIterator) {
          // second generate all partitions:
          final KPartitionsIterable partitionIterator = new KPartitionsIterable(n, k);
          for (int partitionsIndex[] : partitionIterator) {
            partition = createSinglePartition(listArg0, sym, permutationsIndex, partitionsIndex);
            if (partition.isPresent()) {
              result.append(partition);
            }
          }
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private IAST createSinglePartition(final IAST listArg0, final ISymbol symbol,
        final int[] permutationsIndex, final int[] partitionsIndex) {
      IASTAppendable partitionElement;
      int partitionStartIndex;
      IASTAppendable partition = F.ListAlloc(partitionsIndex.length + 1);

      final int n = listArg0.argSize();
      // 0 is always the first index of a partition
      partitionStartIndex = 0;
      for (int i = 1; i < partitionsIndex.length; i++) {
        partitionElement = F.ast(symbol);
        if (partitionStartIndex + 1 == partitionsIndex[i]) {
          // OneIdentity check here
          if (symbol.hasOneIdentityAttribute()) {
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
      partitionElement = F.ast(symbol);
      if (partitionStartIndex + 1 == n) {
        // OneIdentity check here
        if ((symbol.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY) {
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


    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST() && ast.arg2().isInteger()) {
        final IAST listArg0 = (IAST) ast.arg1();
        final int k = ast.get(2).toIntDefault();
        if (k > 0 && k <= listArg0.argSize()) {
          final KPartitionsList iter = new KPartitionsList(listArg0, k, F.ast(S.List), 1);
          final IASTAppendable result = F.ListAlloc(16);
          for (IAST part : iter) {
            result.append(part);
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>MatchingDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Matching dissimilarity between the two boolean 1-D lists <code>u</code> and
   * <code>v</code>, which is defined as <code>(c_tf + c_ft) / n</code>, where <code>n</code> is
   * <code>len(u)</code> and <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code>
   * and <code>v(k)=j</code> for <code>k&lt;n</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; MatchingDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 4/7
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * <code>Partition(list, n)
   * </code>
   * </pre>
   *
   * <p>
   * partitions <code>list</code> into sublists of length <code>n</code>.
   *
   * <pre>
   * <code>Partition(list, n, d)
   * </code>
   * </pre>
   *
   * <p>
   * partitions <code>list</code> into sublists of length <code>n</code> which overlap <code>d
   * </code> indices.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a
   * set</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Partition({a, b, c, d, e, f}, 2)
   * {{a,b},{c,d},{e,f}}
   *
   * &gt;&gt; Partition({a, b, c, d, e, f}, 3, 1)
   * {{a,b,c},{b,c,d},{c,d,e},{d,e,f}}
   *
   * &gt;&gt; Partition({a, b, c, d, e}, 2)
   * {{a,b},{c,d}}
   * </code>
   * </pre>
   */
  private static class Partition extends AbstractFunctionEvaluator {

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        final int partitionLength = ast.arg2().toIntDefault();
        if (partitionLength > 0) {
          final IAST f = (IAST) ast.arg1();
          int i = partitionLength;
          if (i > f.argSize()) {
            return F.headAST0(f.head());
          }

          int offset = partitionLength;
          if ((ast.isAST3()) && ast.arg3().isInteger()) {
            offset = ast.arg3().toIntDefault();
          }
          if (offset > 0) {
            final int allocSize = f.argSize() / offset + 1;
            final IASTAppendable resultListOfPartitions = F.ast(f.head(), allocSize);
            while (true) {

              IASTAppendable singlePartition = F.ast(f.head());
              for (int j = i - partitionLength; j < i; j++) {
                if (j + 1 < 1 || j + 1 >= f.size()) {
                  return resultListOfPartitions;
                }
                singlePartition.append(f.get(j + 1));
              }
              resultListOfPartitions.append(singlePartition);

              if (offset <= f.argSize() - i) { // beware of integer overflow problems
                i += offset;
              } else {
                break;
              }
            }
            return resultListOfPartitions;
          } else {
            // "Positive machine-sized integer expected at position `2` in `1`.", //
            return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C3), engine);
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
   *
   *
   * <pre>
   * <code>Permute(list, Cycles({permutationCycles}))
   * </code>
   * </pre>
   *
   * <p>
   * permutes the <code>list</code> from the cycles in <code>permutationCycles</code>.
   *
   * <pre>
   * <code>Permute(list, permutation-list)
   * </code>
   * </pre>
   *
   * <p>
   * permutes the <code>list</code> from the permutations defined in <code>permutation-list
   * </code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Permute(CharacterRange(&quot;v&quot;, &quot;z&quot;), Cycles({{1, 5, 3}}))
   * {x,w,z,y,v}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>
   */
  private static final class Permute extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isAST()) {
        if (arg2.isList()) {
          IExpr temp = permutationCycles((IAST) arg2, engine);
          if (temp.isAST(S.Cycles, 2)) {
            IAST mainList = (IAST) temp.first();
            return permute((IAST) arg1, mainList, ast, engine);
          }
        } else if (arg2.isAST(S.Cycles, 2)) {
          IAST cycles;
          if (arg2.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
            cycles = (IAST) arg2;
          } else {
            cycles = checkCycles((IAST) arg2, false, engine);
          }
          if (cycles.isPresent()) {
            IAST mainList = (IAST) cycles.arg1();
            return permute((IAST) arg1, mainList, ast, engine);
          }
        }
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    private static IExpr permute(IAST list1, IAST cyclesMainList, final IAST ast,
        EvalEngine engine) {
      IASTMutable result = list1.copy();
      boolean changed = false;

      for (int j = 1; j < cyclesMainList.size(); j++) {
        IAST list = (IAST) cyclesMainList.get(j);
        for (int i = 1; i < list.size(); i++) {
          int fromPosition = list.get(i).toIntDefault();
          if (fromPosition == Integer.MIN_VALUE) {
            return F.NIL;
          }
          if (fromPosition > list1.argSize()) {
            // Required length `1` is smaller than maximum `2` of support of `3`
            return Errors.printMessage(S.Permute, "lowlen",
                F.list(F.ZZ(list1.argSize()), list.get(i), ast), engine);
          }

          int toPosition;
          if (i < list.size() - 1) {
            toPosition = list.get(i + 1).toIntDefault();
          } else {
            toPosition = list.arg1().toIntDefault();
          }
          if (toPosition == Integer.MIN_VALUE) {
            return F.NIL;
          }
          if (toPosition > list1.argSize()) {
            // Required length `1` is smaller than maximum `2` of support of `3`
            return Errors.printMessage(S.Permute, "lowlen",
                F.list(F.ZZ(list1.argSize()), F.ZZ(toPosition), ast), engine);
          }
          changed = true;
          IExpr from = list1.get(fromPosition);
          result.set(toPosition, from);
        }
      }
      return changed ? result : list1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>PermutationCycles(permutation-list)
   * </code>
   * </pre>
   *
   * <p>
   * generate a <code>Cycles({{...},{...}, ...})</code> expression from the <code>
   * permutation-list</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PermutationCycles({3, 1, 2, 5, 4})
   * Cycles({{1,3,2},{4,5}})
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutations.md">FindPermutations</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>, <a href="Permute.md">Permute</a>
   */
  private static final class PermutationCycles extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1() && arg1.isAST(S.Cycles, 2)) {
        IAST cycles = (IAST) arg1;
        if (cycles.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
          return cycles;
        }
        return checkCycles(cycles, false, engine);
      }

      IExpr head = S.Cycles;
      if (ast.isAST2()) {
        head = ast.arg2();
      }
      if (arg1.isList()) {
        return permutationCycles((IAST) arg1, engine, head);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>PermutationCyclesQ(cyclesExpression)
   * </code>
   * </pre>
   *
   * <p>
   * if <code>cyclesExpression</code> is a valid <code>Cycles({{...},{...}, ...})</code> expression
   * return <code>True</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PermutationCyclesQ(Cycles({{1, 6, 2}, {4, 11, 12, 3}}))
   * True
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>, <a href="Permute.md">Permute</a>
   */
  private static final class PermutationCyclesQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Cycles, 2)) {
        IAST cycles = checkCycles((IAST) arg1, true, engine);
        return cycles.isPresent() ? S.True : S.False;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>PermutationList(Cycles({{...},{...}, ...}))
   * </code>
   * </pre>
   *
   * <p>
   * get the permutation list representation from the <code>Cycles({{...},{...}, ...})</code>
   * expression.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PermutationList(Cycles({{3, 2}, { 6, 7},{11,17}}))
   * {1,3,2,4,5,7,6,8,9,10,17,12,13,14,15,16,11}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>, <a href="Permute.md">Permute</a>
   */
  private static final class PermutationList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Cycles, 2)) {
        IAST cycles;
        if (arg1.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
          cycles = (IAST) arg1;
        } else {
          cycles = checkCycles((IAST) arg1, false, engine);
        }
        if (cycles.isPresent()) {
          IAST cyclesList = (IAST) cycles.arg1();
          if (cyclesList.isEmptyList()) {
            return F.CEmptyList;
          }
          int permutationsListLength = determineLengthFromCycles(cyclesList);
          if (permutationsListLength < 1) {
            return F.NIL;
          }
          if (ast.isAST2()) {
            int arg2 = ast.arg2().toIntDefault();
            if (arg2 < 1) {
              return F.NIL;
            }
            if (arg2 >= permutationsListLength) {
              permutationsListLength = arg2;
            } else {
              // Required length `1` is smaller than maximum `2` of support of `3`
              return Errors.printMessage(ast.topHead(), "lowlen",
                  F.list(ast.arg2(), F.ZZ(permutationsListLength), ast), engine);
            }
          }
          IASTAppendable range = F.mapRange(1, permutationsListLength + 1, i -> F.ZZ(i));
          return permutationReplace(range, cyclesList);
        }
      }
      return F.NIL;
    }

    /**
     * Determine the permutations list length from the Cycles first argument.
     *
     * @param cyclesMainList first arg of Cycles
     * @return
     */
    private static int determineLengthFromCycles(IAST cyclesMainList) {
      int permutationsListLength = -1;
      for (int j = 1; j < cyclesMainList.size(); j++) {
        IAST list = (IAST) cyclesMainList.get(j);
        for (int i = 1; i < list.size(); i++) {
          int position = list.get(i).toIntDefault();
          if (position > permutationsListLength) {
            permutationsListLength = position;
          }
        }
      }
      return permutationsListLength;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>PermutationListQ(permutation-list)
   * </code>
   * </pre>
   *
   * <p>
   * if <code>permutation-list</code> is a valid permutation list return <code>True</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PermutationListQ({5, 7, 6, 1, 3, 4, 2, 8})
   * True
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>,
   * <a href="Permutations.md">Permutations</a>, <a href="Permute.md">Permute</a>
   */
  private static final class PermutationListQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr permList = ast.arg1();
      if (permList.isList()) {
        return permList.isEmptyList() || isPermutationList((IAST) permList, true, engine) != null
            ? S.True
            : S.False;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>PermutationReplace(list-or-integer, Cycles({{...},{...}, ...}))
   * </code>
   * </pre>
   *
   * <p>
   * replace the arguments of the first expression with the corresponding element from the <code>
   * Cycles({{...},{...}, ...})</code> expression.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PermutationReplace({1, b, 3, 4, 5}, Cycles({{1, 5,8}, {2, 7}}))
   * {5,b,3,4,8}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>, <a href="Permutations.md">Permutations</a>,
   * <a href="Permute.md">Permute</a>
   */
  private static final class PermutationReplace extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        // arg2 is Listable
        return arg2.mapThread(ast, 2);
      }
      if (arg2.isAST(S.Cycles, 2)) {
        IAST cycles;
        if (arg2.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
          cycles = (IAST) arg2;
        } else {
          cycles = checkCycles((IAST) arg2, false, engine);
        }
        if (cycles.isPresent()) {
          IAST mainList = (IAST) cycles.arg1();
          if (arg1.isInteger()) {
            IInteger intArg1 = (IInteger) arg1;
            return replaceSingleElement(mainList, intArg1);
          } else if (arg1.isList()) {
            IAST list1 = (IAST) ast.arg1();
            if (arg1.isListOfLists()) {
              return list1.mapThread(ast, 1);
            }
            return permutationReplace(list1, mainList);
          }

          return F.NIL;
        }
      }
      return F.NIL;
    }

    private static IInteger replaceSingleElement(IAST mainList, IInteger intArg1) {
      IInteger result = intArg1;
      for (int j = 1; j < mainList.size(); j++) {
        IAST list = (IAST) mainList.get(j);
        for (int i = 1; i < list.size(); i++) {
          IInteger arg = (IInteger) list.get(i);
          if (arg.equals(result)) {
            if (i < list.size() - 1) {
              result = (IInteger) list.get(i + 1);
            } else {
              result = (IInteger) list.arg1();
            }
            return result;
          }
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Permutations(list)
   * </code>
   * </pre>
   *
   * <p>
   * gives all possible orderings of the items in <code>list</code>.
   *
   * <pre>
   * <code>Permutations(list, n)
   * </code>
   * </pre>
   *
   * <p>
   * gives permutations up to length <code>n</code>.
   *
   * <pre>
   * <code>Permutations(list, {n})
   * </code>
   * </pre>
   *
   * <p>
   * finds a list of all possible permutations containing exactly <code>n</code> elements.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Permutations({a, b, c})
   * {{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}}
   *
   * &gt;&gt; Permutations({1, 2, 3}, 2)
   * {{},{1},{2},{3},{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}
   *
   * &gt;&gt; Permutations({a, b, c}, {2})
   * {{a,b},{a,c},{b,a},{b,c},{c,a},{c,b}}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cycles.md">Cycles</a>, <a href="FindPermutation.md">FindPermutation</a>,
   * <a href="PermutationCycles.md">PermutationCycles</a>,
   * <a href="PermutationCyclesQ.md">PermutationCyclesQ</a>,
   * <a href="PermutationList.md">PermutationList</a>,
   * <a href="PermutationListQ.md">PermutationListQ</a>,
   * <a href="PermutationReplace.md">PermutationReplace</a>
   */
  private static final class Permutations extends AbstractFunctionEvaluator {

    /**
     * Generate an <code>java.lang.Iterable<IAST></code> for (multiset) permutations
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
     */
    private static final class KPermutationsList implements Iterable<IAST> {

      private final IAST fList;
      private final IAST fResultList;
      private final int fOffset;
      private final int fParts;

      private class KPermutationsIterator implements Iterator<IAST> {

        private final Iterator<int[]> fIterable;

        private KPermutationsIterator() {
          this.fIterable = new KPermutationsIterable(fList, fParts, fOffset).iterator();
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
        public boolean hasNext() {
          return fIterable.hasNext();
        }
      }

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
      public KPermutationsList(final IAST list, final int parts, IAST resultList,
          final int offset) {
        // fIterable = new KPermutationsIterable(list, parts, offset).iterator();
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
              result.append(F.CEmptyList);
              for (int i = 1; i <= maxPart; i++) {
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
            parts = Validate.checkIntType(S.Permutations, sequence.arg1(), 0, engine);
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
        if (parts == 0) {
          return F.list(F.CEmptyList);
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
  }


  private static final class PolygonalNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr r = ast.arg1();
        IExpr n = ast.arg2();
        return F.Times(F.C1D2, n, F.Plus(F.C4, F.Times(n, F.Plus(F.CN2, r)), F.Negate(r)));
      }
      IExpr n = ast.arg1();
      return F.Times(F.C1D2, n, F.Plus(F.C1, n));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  /**
   *
   *
   * <pre>
   * <code>RogersTanimotoDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Rogers-Tanimoto dissimilarity between the two boolean 1-D lists <code>u</code> and
   * <code>v</code>, which is defined as <code>R / (c_tt + c_ff + R)</code> where n is <code>
   * len(u)</code>, <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code> and <code>
   * v(k)=j</code> for <code>k&lt;n</code>, and <code>R = 2 * (c_tf + c_ft)</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; RogersTanimotoDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 8/11
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * <code>RussellRaoDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Russell-Rao dissimilarity between the two boolean 1-D lists <code>u</code> and
   * <code>v</code>, which is defined as <code>(n - c_tt) / c_tt</code> where <code>n</code> is
   * <code>len(u)</code> and <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code>
   * and <code>v(k)=j</code> for <code>k&lt;n</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; RussellRaoDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 5/7
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * <code>Signature(permutation-list)
   * </code>
   * </pre>
   *
   * <p>
   * determine if the <code>permutation-list</code> has odd (<code>-1</code>) or even (<code>1
   * </code>) parity. Returns <code>0</code> if two elements in the <code>permutation-list</code>
   * are equal.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Permutation">Wikipedia - Permutation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Signature({1,2,3,4})
   * 1
   *
   * &gt;&gt; Signature({1,4,3,2})
   * -1
   *
   * &gt;&gt; Signature({1,2,3,2})
   * 0
   * </code>
   * </pre>
   */
  private static final class Signature extends AbstractFunctionEvaluator {

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


  /**
   *
   *
   * <pre>
   * <code>SokalSneathDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Sokal-Sneath dissimilarity between the two boolean 1-D lists <code>u</code> and
   * <code>v</code>, which is defined as <code>R / (c_tt + R)</code> where n is <code>len(u)</code>,
   * <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code> and <code>v(k)=j</code>
   * for <code>k&lt;n</code>, and <code>R = 2 * (c_tf + c_ft)</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; SokalSneathDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 4/5
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.addAttributes(ISymbol.NHOLDALL);
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Subsets(list)
   * </code>
   * </pre>
   *
   * <p>
   * finds a list of all possible subsets of <code>list</code>.
   *
   * <pre>
   * <code>Subsets(list, n)
   * </code>
   * </pre>
   *
   * <p>
   * finds a list of all possible subsets containing at most <code>n</code> elements.
   *
   * <pre>
   * <code>Subsets(list, {n})
   * </code>
   * </pre>
   *
   * <p>
   * finds a list of all possible subsets containing exactly <code>n</code> elements.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Combination">Wikipedia - Combination</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Subsets({a, b, c})
   * {{},{a},{b},{c},{a,b},{a,c},{b,c},{a,b,c}}
   *
   * &gt;&gt; Subsets({a, b, c}, 2)
   * {{},{a},{b},{c},{a,b},{a,c},{b,c}}
   *
   * &gt;&gt; Subsets({a, b, c}, {2})
   * {{a,b},{a,c},{b,c}}
   *
   * &gt;&gt; Subsets({})
   * {{}}
   *
   * &gt;&gt; Subsets()
   * Subsets()
   * </code>
   * </pre>
   *
   * <p>
   * The <a href="https://oeis.org/A018900">A018900 Sum of two distinct powers of 2</a> integer
   * sequence
   *
   * <pre>
   * <code>&gt;&gt; Union(Total/@Subsets(2^Range(0, 10), {2}))
   * {3,5,6,9,10,12,17,18,20,24,33,34,36,40,48,65,66,68,72,80,96,129,130,132,136,144,160,192,257,258,260,264,272,288,320,384,513,514,516,520,528,544,576,640,768,1025,1026,1028,1032,1040,1056,1088,1152,1280,1536}
   * </code>
   * </pre>
   */
  private static final class Subsets extends AbstractFunctionEvaluator {

    /**
     * Iterate over the lists of all k-combinations from a given list
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
     */
    public static final class KSubsetsList implements Iterable<IAST> {

      private final IAST fList;
      private final IAST fResultList;
      private final int fOffset;

      private final int fK;

      private class KSubsetsIterator implements Iterator<IAST> {

        private final Iterator<int[]> fIterable;

        private KSubsetsIterator() {
          this.fIterable = new KSubsetsIterable(fList.size() - fOffset, fK).iterator();
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
          return temp.appendArgs(0, fK, i -> {
            if (j.length > i && fList.size() > (j[i] + fOffset)) {
              return fList.get(j[i] + fOffset);
            }
            return F.NIL;
          });
        }

        @Override
        public boolean hasNext() {
          return fIterable.hasNext();
        }
      }

      private KSubsetsList(final IAST list, final int k, IAST resultList, final int offset) {
        fList = list;
        fK = k;
        fResultList = resultList;
        fOffset = offset;
      }

      @Override
      public Iterator<IAST> iterator() {
        return new KSubsetsIterator();
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        final IAST f = (IAST) ast.arg1();
        int n = f.argSize();
        SetSpecification level = new SetSpecification(0, n);
        if (ast.argSize() >= 2) {
          IExpr arg2 = ast.arg2();
          if (arg2 != S.All && !arg2.isInfinity()) {
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
        final IASTAppendable result = F.ast(S.List);
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

        if (ast.isAST3()) {
          IExpr arg3 = ast.arg3();
          if (arg3.isList1()) {
            int s = arg3.first().toIntDefault();
            if (s != Integer.MIN_VALUE) {
              if (s < 0) {
                if (result.argSize() >= -s) {
                  return F.List(result.get(result.argSize() + s + 1));
                }
              } else {
                if (result.argSize() >= s) {
                  return F.List(result.get(s));
                }
              }
              // Cannot take positions `1` through `2` in `3`.
              // Errors.printMessage(S.Subsets, "take", F.List(F.C3, ast), engine);
              return F.CEmptyList;
            }
          } else if (arg3.isList2()) {
            int s1 = arg3.first().toIntDefault();
            int s2 = arg3.second().toIntDefault();
            if (s1 != Integer.MIN_VALUE && s2 != Integer.MIN_VALUE) {
              if (s1 > s2) {
                return F.CEmptyList;
              }
              if (s2 < 0) {
                if (result.argSize() >= -s1 && result.argSize() >= -s2) {
                  return result.subList(result.argSize() + s1 + 1, result.argSize() + s2 + 2);
                }
              } else if (s1 > 0) {
                if (result.argSize() >= s1 && result.argSize() >= s1) {
                  return result.subList(s1, s2 + 1);
                }
              }
              return F.CEmptyList;
            }
          } else if (arg3.isList3()) {
            IAST list = (IAST) arg3;
            int s1 = list.arg1().toIntDefault();
            int s2 = list.arg2().toIntDefault();
            int step = list.arg3().toIntDefault();
            if (s1 != Integer.MIN_VALUE && s2 != Integer.MIN_VALUE && step != Integer.MIN_VALUE) {
              if (s1 > s2) {
                if (step < 0) {
                  if (s2 < 0) {
                    // Function `1` not implemented
                    return Errors.printMessage(S.Subsets, "zznotimpl", F.List(ast), engine);
                  }
                } else {
                  return F.CEmptyList;
                }
              }
              if (s2 < 0 && step < 0) {
                if (result.argSize() >= -s1 && result.argSize() >= -s2) {
                  return result.subList(result.argSize() + s1 + 1, result.argSize() + s2 + 1,
                      -step);
                }
              } else if (s1 > 0) {
                if (result.argSize() >= s1 && result.argSize() >= s2) {
                  // step can be negative and s1 > s2
                  return result.subList(s1, step < 0 ? s2 - 1 : s2 + 1, step);
                }
              }
              return F.CEmptyList;
            }
          } else {
            int s = arg3.toIntDefault();
            if (s != Integer.MIN_VALUE) {
              if (s < 0) {
                if (result.argSize() >= -s) {
                  return result.subList(result.argSize() + s + 1);
                }
              } else {
                if (result.argSize() >= s) {
                  return result.subList(1, s + 1);
                }
              }
              // Cannot take positions `1` through `2` in `3`.
              // Errors.printMessage(S.Subsets, "take", F.List(F.C3, ast), engine);
              return F.CEmptyList;
            }
          }
          // Position `1` of `2` must be All, None an integer, or a list of 1,2 or 3 integers, with
          // the third (if present) nonzero.
          return Errors.printMessage(S.Subsets, "seq", F.List(F.C3, ast), engine);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    public static KSubsetsList createKSubsets(final IAST list, final int k, IAST resultList,
        final int offset) {
      return new KSubsetsList(list, k, resultList, offset);
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
   * <p>
   * creates a list of all <code>n</code>-tuples of elements in <code>list</code>.
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
   * <p>
   * returns a list of tuples with elements from the given lists.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Tuple">Wikipedia - Tuple</a>
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
        tuplesOfListsRecursive(list, 1, result, temp, ast, engine);
        return result;

      } else if (ast.isAST2() && arg1.isAST()) {
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
    private static void tuples(final IAST originalList, final int n, IASTAppendable result,
        IAST subResult, IAST ast, EvalEngine engine) {
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
        for (int j = 1; j < originalList.size(); j++) {
          IASTAppendable temp = subResult.appendClone(originalList.get(j));
          // temp.append(originalList.get(j));
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
    private void tuplesOfListsRecursive(final IAST originalList, final int k, IASTAppendable result,
        IAST subResult, IAST ast, EvalEngine engine) {
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
        temp = subResult.copyAppendable(1);
        temp.append(subAST.get(j));
        tuplesOfListsRecursive(originalList, k + 1, result, temp, ast, engine);
      }
    }
  }


  /**
   *
   *
   * <pre>
   * <code>YuleDissimilarity(u, v)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Yule dissimilarity between the two boolean 1-D lists <code>u</code> and <code>v
   * </code>, which is defined as <code>R / (c_tt * c_ff + R / 2)</code> where <code>n</code> is
   * <code>len(u)</code>, <code>c_ij</code> is the number of occurrences of <code>u(k)=i</code> and
   * <code>v(k)=j</code> for <code>k&lt;n</code>, and <code>R = 2 * c_tf * c_ft</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; YuleDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})
   * 6/5
   * </code>
   * </pre>
   */
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   * Generate an <code>java.lang.Iterable</code> for (multiset) permutations
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
   */
  public static final class KPermutationsIterable implements Iterable<int[]> {

    private final int n;

    private final int k;

    private final int fPermutationsIndex[];

    private final int y[];

    private boolean first;

    private int h, i, m;

    private final int fCopiedResultIndex[];

    private class KPermutationsIterator implements Iterator<int[]> {
      private int fResultIndex[];

      private KPermutationsIterator() {
        fResultIndex = nextBeforehand();
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
      public boolean hasNext() {
        return fResultIndex != null;
      }

      @Override
      public int[] next() {
        System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
        fResultIndex = nextBeforehand();
        return fCopiedResultIndex;
      }
    }

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
    }

    /**
     * Create an iterator which gives all possible permutations of <code>data</code> which contains
     * at most <code>parts</code> number of elements. Repeated elements are treated as same.
     *
     * @param data a list of integers which should be permutated.
     * @param parts
     */
    public KPermutationsIterable(final int[] data, final int parts) {
      this(data, data.length, parts);
    }

    /**
     * Create an iterator which gives all possible permutations of <code>data</code> which contains
     * at most <code>parts</code> number of elements. Repeated elements are treated as same.
     *
     * @param data a list of integers which should be permutated.
     * @param len consider only the first <code>n</code> elements of <code>data</code> for
     *        permutation
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
      // fResultIndex = nextBeforehand();
    }

    @Override
    public Iterator<int[]> iterator() {
      return new KPermutationsIterator();
    }

  }

  /**
   * Check if cycles is a valid <code>Cycles({{...},{...},...})</code> , which contains disjoint
   * permutation cycles represented by integer lists.
   *
   * @param cycles
   * @param quiet if <code>true</code> suppress the output of error messages
   * @param engine
   * @return <code>F.NIL</code> if cycles is not a valid <code>Cycles({{...},{...},...})</code>
   *         expression
   */
  public static IAST checkCycles(final IAST cycles, boolean quiet, EvalEngine engine) {
    if (cycles.isAST(S.Cycles, 2)) {
      return Cycles.canonicalizeCycles(cycles, quiet, engine);
    }
    return F.NIL;
  }

  /**
   * Create a <code>Cycles({...})</code> expression from the permutation list.
   *
   * @param permList the permutation list
   * @param engine
   * @return
   */
  public static IExpr permutationCycles(IAST permList, EvalEngine engine) {
    return permutationCycles(permList, engine, S.Cycles);
  }

  /**
   * Create a <code>Cycles({...})</code> expression from the permutation list.
   *
   * @param permList the permutation list
   * @param engine
   * @param head define the head of the resulting expression (can be different from {@link S#Cycles}
   * @return
   */
  public static IExpr permutationCycles(IAST permList, EvalEngine engine, IExpr head) {
    if (permList.isEmptyList()) {
      return F.Cycles(F.CEmptyList);
    }

    int[] positions = isPermutationList(permList, false, engine);
    if (positions == null) {
      // not a valid permutation list
      return F.NIL;
    }
    //
    // permList should be valid here
    //
    IASTAppendable mainList = F.ListAlloc(F.allocMin16(permList));
    IASTAppendable cycleList = F.NIL;
    int oldPosition = -1;
    int newPosition = -1;
    for (int i = 0; i < positions.length; i++) {
      newPosition = positions[i];
      if (newPosition < 0) {
        cycleList = F.NIL;
      } else {
        if (newPosition == i + 1) {
          positions[i] = -1;
          cycleList = F.NIL;
          if (head != S.Cycles) {
            // ignore singletons for S.Cycles; otherwise keep singletons:
            IAST singleton = F.list(F.ZZ(newPosition));
            mainList.append(singleton);
          }
        } else {
          positions[i] = -1;
          if (cycleList.isNIL()) {
            cycleList = F.ListAlloc(permList.argSize() < 16 ? permList.size() : 16);
            mainList.append(cycleList);
          }
          cycleList.append(newPosition);
          oldPosition = newPosition;
          while (positions[oldPosition - 1] > 0) {
            newPosition = positions[oldPosition - 1];
            if (newPosition < 0) {
              break;
            }

            positions[oldPosition - 1] = -1;
            if (positions[newPosition - 1] < 0) {
              cycleList.append(1, newPosition);
            } else {
              cycleList.append(newPosition);
            }
            oldPosition = newPosition;
          }
          cycleList = F.NIL;
        }
      }
    }

    return F.unaryAST1(head, mainList);
  }

  /**
   * Return the permutation list array if <code>permList</code> is a valid permutation list (i.e. a
   * rearrangement of the integers <code>{1..permList.argSize()}</code> ).
   *
   * @param permList a list which should be checked if it is a valid permutation list
   * @param quiet if <code>true</code> suppress the output of error messages
   * @param engine
   * @return <code>null</code> if <code>permList</code> is not a valid permutation list.
   */
  private static int[] isPermutationList(IAST permList, boolean quiet, EvalEngine engine) {
    Set<IExpr> set = new HashSet<IExpr>();
    int[] positions = new int[permList.argSize()];
    for (int i = 1; i < permList.size(); i++) {
      IExpr arg = permList.get(i);
      if (arg.isInteger()) {
        int position = arg.toIntDefault();
        if (position < 1 || position > permList.argSize()) {
          if (!quiet) {
            // Invalid permutation list `1`.
            Errors.printMessage(S.Cycles, "permlist", F.list(permList), engine);
          }
          return null;
        }
        if (set.contains(arg)) {
          // contains repeated integers.
          if (!quiet) {
            // Invalid permutation list `1`.
            Errors.printMessage(S.Cycles, "permlist", F.list(permList), engine);
          }
          return null;
        }
        set.add(arg);
        positions[i - 1] = position;
      } else {
        if (!quiet) {
          // `1` is not a valid permutation.
          Errors.printMessage(S.Cycles, "perm", F.list(permList), engine);
        }
        return null;
      }
    }
    return positions;
  }

  public static IExpr permutationReplace(IAST list1, IAST mainList) {
    IASTMutable result = list1.copy();

    boolean changed = false;
    for (int i = 1; i < list1.size(); i++) {
      IExpr arg = list1.get(i);
      if (arg.isInteger()) {
        IInteger element = PermutationReplace.replaceSingleElement(mainList, (IInteger) arg);
        if (!element.equals(arg)) {
          result.set(i, element);
          changed = true;
        }
      }
    }
    return changed ? result : list1;
  }

  public static void initialize() {
    Initializer.init();
  }

  private Combinatoric() {}
}
