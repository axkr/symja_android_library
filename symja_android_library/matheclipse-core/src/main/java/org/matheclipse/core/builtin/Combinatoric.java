package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.combinatoric.KPartitionsIterable;
import org.matheclipse.core.combinatoric.KPartitionsList;
import org.matheclipse.core.eval.CombinatoricUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.KPermutationsIterable;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.MutableInt;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public final class Combinatoric {

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
        IExpr temp = CombinatoricUtil.canonicalizeCycles(ast, false, engine);
        if (temp.equals(ast)) {
          ast.setEvalFlags(IAST.BUILT_IN_EVALED);
          return F.NIL;
        }
        return temp;
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
        IExpr ordering = S.Ordering.funEval(engine, ast1);
        if (ordering.isList()) {
          return CombinatoricUtil.permutationCycles((IAST) ordering);
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
      return CombinatoricUtil.permutationCycles(permList);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

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
      S.JaccardDissimilarity.setEvaluator(new JaccardDissimilarity());
      S.KOrderlessPartitions.setEvaluator(new KOrderlessPartitions());
      S.KPartitions.setEvaluator(new KPartitions());
      S.MatchingDissimilarity.setEvaluator(new MatchingDissimilarity());
      S.Permute.setEvaluator(new Permute());
      S.PermutationCycles.setEvaluator(new PermutationCycles());
      S.PermutationCyclesQ.setEvaluator(new PermutationCyclesQ());
      S.PermutationList.setEvaluator(new PermutationList());
      S.PermutationListQ.setEvaluator(new PermutationListQ());
      S.PermutationReplace.setEvaluator(new PermutationReplace());
      S.PolygonalNumber.setEvaluator(new PolygonalNumber());
      S.RogersTanimotoDissimilarity.setEvaluator(new RogersTanimotoDissimilarity());
      S.RussellRaoDissimilarity.setEvaluator(new RussellRaoDissimilarity());
      S.Signature.setEvaluator(new Signature());
      S.SokalSneathDissimilarity.setEvaluator(new SokalSneathDissimilarity());
      S.Tuples.setEvaluator(new Tuples());
      S.YuleDissimilarity.setEvaluator(new YuleDissimilarity());
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
        return CombinatoricUtil.checkCycles(cycles, false, engine);
      }

      IExpr head = S.Cycles;
      if (ast.isAST2()) {
        head = ast.arg2();
      }
      if (arg1.isList()) {
        return CombinatoricUtil.permutationCycles((IAST) arg1, head);
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
        IAST cycles = CombinatoricUtil.checkCycles((IAST) arg1, true, engine);
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
          cycles = CombinatoricUtil.checkCycles((IAST) arg1, false, engine);
        }
        if (cycles.isPresent()) {
          IAST cyclesList = (IAST) cycles.arg1();
          if (cyclesList.isEmptyList()) {
            return F.CEmptyList;
          }
          int permutationsListLength = CombinatoricUtil.determineLengthFromCycles(cyclesList);
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
          return CombinatoricUtil.permutationReplace(range, cyclesList);
        }
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
        return permList.isEmptyList() || CombinatoricUtil.isPermutationList((IAST) permList, true) != null ? S.True
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
  public static final class PermutationReplace extends AbstractFunctionEvaluator {

    public static IInteger replaceSingleElement(IAST mainList, IInteger intArg1) {
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
          cycles = CombinatoricUtil.checkCycles((IAST) arg2, false, engine);
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
            return CombinatoricUtil.permutationReplace(list1, mainList);
          }

          return F.NIL;
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

    private static IExpr permuteCycles(IAST list, IAST permutationList, final IAST ast) {
      IExpr temp = CombinatoricUtil.permutationCycles(permutationList);
      if (temp.isAST(S.Cycles, 2)) {
        IAST permutationMainList = (IAST) temp.first();
        return CombinatoricUtil.permute(list, permutationMainList, ast);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isAST()) {
        if (arg2.isList()) {
          return permuteCycles((IAST) arg1, (IAST) arg2, ast);
        } else if (arg2.isAST(S.Cycles, 2)) {
          IAST cycles;
          if (arg2.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
            cycles = (IAST) arg2;
          } else {
            cycles = CombinatoricUtil.checkCycles((IAST) arg2, false, engine);
          }
          if (cycles.isPresent()) {
            IAST mainList = (IAST) cycles.arg1();
            return CombinatoricUtil.permute((IAST) arg1, mainList, ast);
          }
        }
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static final class PolygonalNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr r = ast.arg1();
        IExpr n = ast.arg2();
        IExpr result = engine
            .evaluate(F.Times(F.C1D2, n, F.Plus(F.C4, F.Times(n, F.Plus(F.CN2, r)), F.Negate(r))));
        return result;
      }
      IExpr n = ast.arg1();
      IExpr result = engine.evaluate(F.Times(F.C1D2, n, F.Plus(F.C1, n)));
      return result;
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

  public static void initialize() {
    Initializer.init();
  }

  private Combinatoric() {}
}
