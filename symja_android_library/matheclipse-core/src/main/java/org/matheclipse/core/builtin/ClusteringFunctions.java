package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hipparchus.clustering.Cluster;
import org.hipparchus.clustering.DBSCANClusterer;
import org.hipparchus.util.DoublePoint;
import org.hipparchus.clustering.KMeansPlusPlusClusterer;
import org.hipparchus.clustering.MultiKMeansPlusPlusClusterer;
import org.hipparchus.distance.DistanceMeasure;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.random.Well19937c;
import org.hipparchus.stat.StatUtils;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistance;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class ClusteringFunctions {

  private abstract static class AbstractDistance extends AbstractEvaluator implements IDistance {
    private static final long serialVersionUID = -295980120043414467L;

    @Override
    public abstract IExpr distance(IExpr a, IExpr b, EvalEngine engine);

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      // CPD-OFF per-metric closed form
      int dim1 = arg1.isVector();
      if (dim1 > (-1)) {
        int dim2 = arg2.isVector();
        if (dim2 > (-1)) {
          if (dim1 != dim2) {
            // The arguments `1` and `2` do not have compatible dimensions.
            return Errors.printMessage(ast.topHead(), "bldim", F.List(arg1, arg2), engine);
          }
          if (dim1 != 0) {
            return vectorDistance(arg1, arg2, engine);
            // CPD-ON
          }
        }
        return F.NIL;
      }
      if (arg1.isNumber() && arg2.isNumber()) {
        INumber n1 = (INumber) arg1;
        INumber n2 = (INumber) arg2;
        return scalarDistance(n1, n2, engine);
      }
      if (arg1.isNumericFunction() && arg2.isNumericFunction()) {
        return numericFunctionDistance(arg1, arg2, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public @AllowNIL IExpr numericFunctionDistance(IExpr a, IExpr b, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public @AllowNIL IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return F.NIL;
    }

    protected IExpr vectorDistance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      if (engine.isDoubleMode() && arg1.isNumericAST() && arg2.isNumericAST()) {
        double[] a = arg1.toDoubleVector();
        if (a != null) {
          double[] b = arg2.toDoubleVector();
          if (b != null) {
            return F.num(compute(a, b));
          }
        }
      }
      return distance(arg1, arg2, engine);
    }
  }

  private static final class BinaryDistance extends AbstractDistance {

    private static final long serialVersionUID = 6407163419470076191L;

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      if (a == b) {
        return 1.0;
      }
      if (a.length != b.length) {
        return 0.0;
      }
      for (int i = 0; i < a.length; i++) {
        if (!F.isEqual(a[i], b[i])) {
          return 0.0;
        }
      }
      return 1.0;
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      return a.equals(b) ? F.C1 : F.C0;
    }

    @Override
    protected IExpr vectorDistance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      // don't call numeric case here!
      return distance(arg1, arg2, engine);
    }
  }

  /**
   *
   *
   * <pre>
   * BrayCurtisDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Bray Curtis distance between <code>u</code> and <code>v</code>.
   *
   * </blockquote>
   *
   * <pre>
   * &gt;&gt; BrayCurtisDistance[{-1, -1}, {10, 10}]
   * 11/9
   * </pre>
   */
  private static final class BrayCurtisDistance extends AbstractDistance {
    private static final long serialVersionUID = 669052613809997063L;

    private static double distancePlus(double[] p1, double[] p2)
        throws MathIllegalArgumentException {
      double sum = 0;
      for (int i = 0; i < p1.length; i++) {
        sum += Math.abs(p1[i] + p2[i]);
      }
      return sum;
    }

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      return MathArrays.distance1(a, b) / distancePlus(a, b);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IExpr divide = F.Divide(F.Total(F.Abs(F.Subtract(a, b))), F.Total(F.Abs(F.Plus(a, b))));
      divide = engine.evaluate(divide);
      return divide;
    }

    /**
     * A scalar argument is a one-element vector, so each {@code Total} above collapses to its
     * single summand. Left to the inherited implementation this returns {@code F.NIL} and
     * {@code BrayCurtisDistance[-7, 5]} stays unevaluated instead of giving {@code 6}.
     *
     * <p>
     * The zero denominator is deliberately not guarded, matching the vector case above.
     */
    @Override
    public IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return engine.evaluate(F.Divide(F.Abs(F.Subtract(a, b)), F.Abs(F.Plus(a, b))));
    }
  }

  /**
   *
   *
   * <pre>
   * CanberraDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the canberra distance between <code>u</code> and <code>v</code>, which is a weighted
   * version of the Manhattan distance.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; CanberraDistance({-1, -1}, {1, 1})
   * 2
   * </pre>
   */
  private static final class CanberraDistance extends AbstractDistance {
    private static final long serialVersionUID = 6257588266259496269L;

    static final DistanceMeasure distance =
        new org.hipparchus.distance.CanberraDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IExpr denominator = engine.evaluate(F.Plus(F.Abs(a), F.Abs(b)));
      if (denominator.isList() && ((IAST) denominator).exists(x -> x.isZero())) {
        return F.C0;
      }
      return F.Total(F.Divide(F.Abs(F.Subtract(a, b)), denominator));
    }

    /**
     * A scalar argument is a one-element vector, so the {@code Total} above collapses to its
     * single summand and {@code CanberraDistance[-7, 5]} is {@code 12/12}. Left to the inherited
     * implementation it stays unevaluated. The zero-denominator guard mirrors the vector case.
     */
    @Override
    public IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      IExpr denominator = engine.evaluate(F.Plus(F.Abs(a), F.Abs(b)));
      if (denominator.isZero()) {
        return F.C0;
      }
      return engine.evaluate(F.Divide(F.Abs(F.Subtract(a, b)), denominator));
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
   * ChessboardDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the chessboard distance (also known as Chebyshev distance) between <code>u</code> and
   * <code>v</code>, which is the number of moves a king on a chessboard needs to get from square
   * <code>u</code> to square <code>v</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ChessboardDistance({-1, -1}, {1, 1})
   * 2
   * </pre>
   */
  private static final class ChessboardDistance extends AbstractDistance {
    private static final long serialVersionUID = 6473415254245961676L;

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      return MathArrays.distanceInf(a, b);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IAST vect1 = (IAST) a.normal(false);
      IAST vect2 = (IAST) b.normal(false);
      IASTAppendable maxAST = F.Max();
      return maxAST.appendArgs(a.size(), i -> F.Abs(F.Subtract(vect1.get(i), vect2.get(i))));
    }

    /**
     * A scalar argument is a one-element vector, so the {@code Max} above has a single argument
     * and reduces to the absolute difference. Left to the inherited implementation
     * {@code ChessboardDistance[-7, 5]} stays unevaluated instead of giving {@code 12}.
     */
    @Override
    public IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return a.subtract(b).abs();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class CorrelationDistance extends CosineDistance {

    private static final long serialVersionUID = -3541385908310138318L;

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      double mean1 = -StatUtils.mean(a);
      double mean2 = -StatUtils.mean(b);
      double u[] = new double[a.length];
      for (int i = 0; i < a.length; i++) {
        u[i] = a[i] + mean1;
      }
      double v[] = new double[b.length];
      for (int i = 0; i < b.length; i++) {
        v[i] = b[i] + mean2;
      }
      return super.compute(u, v);
    }


    @Override
    public @AllowNIL IExpr distance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      IExpr normV1 = F.Norm.of(engine, arg1);
      if (normV1.isZero()) {
        return F.C0;
      }
      IExpr normV2 = F.Norm.of(engine, arg2);
      if (normV2.isZero()) {
        return F.C0;
      }
      int v1Length = arg1.isVector();
      int v2Length = arg2.isVector();
      if (v1Length == v2Length && v2Length > 0) {
        if (v1Length == 10) {
          return F.NIL;
        }
        if (v1Length == 1) {
          return F.C0;
        }
        IAST v1 = (IAST) arg1.normal(false);
        IAST v2 = (IAST) arg2.normal(false);

        IASTAppendable factorV1 = F.PlusAlloc(v1Length);
        IASTAppendable factorV2 = F.PlusAlloc(v1Length);
        for (int i = 1; i < v1.size(); i++) {
          IExpr v1Arg = v1.get(i);
          IExpr v2Arg = v2.get(i);
          factorV1.append(v1Arg);
          factorV2.append(v2Arg);
        }
        final IFraction v3 = F.QQ(-1, v1Length);
        IExpr timesV1 = S.Times.of(engine, v3, factorV1);
        IExpr timesV2 = S.Times.of(engine, v3, factorV2);

        IASTAppendable plusNumerator = F.PlusAlloc(v1Length);
        IASTAppendable plusV1 = F.PlusAlloc(v1Length);
        IASTAppendable plusV2 = F.PlusAlloc(v1Length);
        for (int i = 1; i < v1.size(); i++) {
          IExpr v1Arg = v1.get(i);
          IExpr v2Arg = v2.get(i);
          IAST p1 = F.Plus(v1Arg, timesV1);
          IAST p2 = F.Plus(v2Arg, timesV2);
          plusNumerator.append(F.Times(p1, F.Conjugate(p2)));
          plusV1.append(F.Sqr(F.Abs(p1)));
          plusV2.append(F.Sqr(F.Abs(p2)));
        }
        IExpr denominator = engine.evaluate(F.Sqrt(F.Times(plusV1, plusV2)));
        if (denominator.isZero()) {
          return F.C0;
        }
        return F.Subtract(F.C1, F.Divide(plusNumerator, denominator));
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
   * CosineDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the cosine distance between <code>u</code> and <code>v</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(CosineDistance({7, 9}, {71, 89}))
   * 7.596457213221441E-5
   *
   * &gt;&gt; CosineDistance({a, b}, {c, d})
   * 1-(a*c+b*d)/(Sqrt(Abs(a)^2+Abs(b)^2)*Sqrt(Abs(c)^2+Abs(d)^2))
   * </pre>
   */
  private static class CosineDistance extends AbstractDistance {
    private static final long serialVersionUID = -108468814401695919L;

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      double cosAngle = MathArrays.cosAngle(a, b);
      if (Double.isNaN(cosAngle)) {
        return 0.0;
      }
      return 1.0 - cosAngle;
    }

    @Override
    public IExpr distance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      IExpr norm1 = F.Norm.of(engine, arg1);
      if (norm1.isZero()) {
        return F.C0;
      }
      IExpr norm2 = F.Norm.of(engine, arg2);
      if (norm2.isZero()) {
        return F.C0;
      }
      return engine.evaluate(
          F.Subtract(F.C1, F.Divide(F.Dot(arg1, F.Conjugate(arg2)), F.Times(norm1, norm2))));
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr numericFunctionDistance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      IExpr norm1 = arg1.abs();
      if (norm1.isPossibleZero(true)) {
        return F.C0;
      }
      IExpr norm2 = arg2.abs();
      if (norm2.isPossibleZero(true)) {
        return F.C0;
      }
      return engine.evaluate(
          F.Subtract(F.C1, F.Divide(F.Times(arg1, F.Conjugate(arg2)), F.Times(norm1, norm2))));
    }

    @Override
    public IExpr scalarDistance(INumber arg1, INumber arg2, EvalEngine engine) {
      IExpr norm1 = arg1.abs();
      if (norm1.isZero()) {
        return F.C0;
      }
      IExpr norm2 = arg2.abs();
      if (norm2.isZero()) {
        return F.C0;
      }
      final INumber c = arg1.times(arg2.conjugate());
      return engine.evaluate(F.Subtract(F.C1, F.Divide(c, F.Times(norm1, norm2))));
    }
  }

  /**
   *
   *
   * <pre>
   * EuclideanDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the euclidean distance between <code>u</code> and <code>v</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EuclideanDistance({-1, -1}, {1, 1})
   * 2*Sqrt(2)
   *
   * &gt;&gt; EuclideanDistance({a, b}, {c, d})
   * Sqrt(Abs(a-c)^2+Abs(b-d)^2)
   * </pre>
   */
  private static final class EuclideanDistance extends AbstractDistance {
    private static final long serialVersionUID = 2872848600632425591L;

    static final DistanceMeasure distance =
        // CPD-OFF per-metric closed form
        new org.hipparchus.distance.EuclideanDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IAST vect1 = (IAST) a.normal(false);
      IAST vect2 = (IAST) b.normal(false);
      int size = a.size();
      IASTAppendable plusAST = F.PlusAlloc(size);
      // CPD-ON
      plusAST.appendArgs(size, i -> F.Sqr(F.Abs(F.Subtract(vect1.get(i), vect2.get(i)))));
      return F.Sqrt(plusAST);
    }

    @Override
    public IExpr scalarDistance(INumber arg1, INumber arg2, EvalEngine engine) {
      return arg1.subtract(arg2).abs();
    }
  }

  private static class FindClusters extends AbstractEvaluator {

    /** The largest number of clusters which is tried if none is given. */
    private static final int MAX_AUTOMATIC_CLUSTERS = 10;

    /**
     * The largest number of points for which the number of clusters is chosen by the silhouette
     * widths and the one dimensional partition is computed exactly: both take quadratic time.
     */
    private static final int MAX_QUADRATIC_POINTS = 2000;

    /** The number of clusters of a larger point set, if none is given. */
    private static final int DEFAULT_CLUSTERS = 3;

    /**
     * A mean silhouette width below this value shows no substantial cluster structure: the points
     * are one cluster.
     */
    private static final double MIN_SILHOUETTE = 0.25;

    /**
     * How much the mean silhouette width of the points must exceed the one of as many points spread
     * evenly over the same range. Evenly spread points have no cluster structure, but their
     * silhouette width grows from about 0.17 for three points to about 0.62 for many points on a
     * line.
     */
    private static final double MIN_SILHOUETTE_GAIN = 0.15;

    /**
     * A point which remembers its position in the list. Points are distinct by identity, so that
     * equal values don't collapse in the hash maps of a clusterer.
     */
    private static final class IndexedPoint extends DoublePoint {
      private static final long serialVersionUID = 4519326270713917566L;

      final int index;

      IndexedPoint(double[] values, int index) {
        super(values);
        this.index = index;
      }

      @Override
      public boolean equals(Object obj) {
        return this == obj;
      }

      @Override
      public int hashCode() {
        return System.identityHashCode(this);
      }
    }

    /**
     * The clusters as lists of the original elements of <code>list</code>: each cluster in the order
     * of the list, the clusters in the order of their first elements.
     */
    private static IAST clustersToList(List<int[]> clusters, IAST list) {
      clusters.sort((a, b) -> Integer.compare(a[0], b[0]));
      IASTAppendable result = F.ListAlloc(clusters.size());
      for (int[] cluster : clusters) {
        IASTAppendable elements = F.ListAlloc(cluster.length);
        for (int index : cluster) {
          elements.append(list.get(index + 1));
        }
        result.append(elements);
      }
      return result;
    }

    /** The sorted point indices of each nonempty cluster of a clusterer. */
    private static List<int[]> indicesOf(List<? extends Cluster<IndexedPoint>> clusters) {
      List<int[]> result = new ArrayList<int[]>(clusters.size());
      for (Cluster<IndexedPoint> cluster : clusters) {
        List<IndexedPoint> points = cluster.getPoints();
        if (!points.isEmpty()) {
          int[] indices = new int[points.size()];
          for (int i = 0; i < indices.length; i++) {
            indices[i] = points.get(i).index;
          }
          Arrays.sort(indices);
          result.add(indices);
        }
      }
      return result;
    }

    /**
     * The partitions of one dimensional points into intervals with the least sum of the squared
     * distances to their means, by dynamic programming over the sorted values. The table is
     * computed once for the largest number of intervals; each smaller number is read off it.
     */
    private static final class LinePartitions {
      private final List<IndexedPoint> points;
      private final Integer[] order;
      /** <code>start[m][j]</code>: the first value of the last of <code>m</code> intervals. */
      private final int[][] start;

      LinePartitions(List<IndexedPoint> points, int maxK, EvalEngine engine) {
        this.points = points;
        final int n = points.size();
        order = new Integer[n];
        for (int i = 0; i < n; i++) {
          order[i] = i;
        }
        Arrays.sort(order, (a, b) -> Double.compare(points.get(a).values()[0],
            points.get(b).values()[0]));
        double[] sum = new double[n + 1];
        double[] sumOfSquares = new double[n + 1];
        for (int i = 0; i < n; i++) {
          double v = points.get(order[i]).values()[0];
          sum[i + 1] = sum[i] + v;
          sumOfSquares[i + 1] = sumOfSquares[i] + v * v;
        }
        // cost[m][j]: the least sum for the first j values in m intervals
        double[][] cost = new double[maxK + 1][n + 1];
        start = new int[maxK + 1][n + 1];
        for (double[] row : cost) {
          Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        cost[0][0] = 0.0;
        for (int m = 1; m <= maxK; m++) {
          engine.checkTimeLimit();
          for (int j = m; j <= n; j++) {
            for (int i = m; i <= j; i++) {
              // the values i-1 .. j-1 form the last interval
              double s = sum[j] - sum[i - 1];
              double within = sumOfSquares[j] - sumOfSquares[i - 1] - s * s / (j - i + 1);
              double total = cost[m - 1][i - 1] + Math.max(within, 0.0);
              if (total < cost[m][j]) {
                cost[m][j] = total;
                start[m][j] = i;
              }
            }
          }
        }
      }

      /** The best partition into <code>k</code> intervals, <code>k</code> at most the maximum. */
      List<int[]> partition(int k) {
        List<int[]> result = new ArrayList<int[]>(k);
        int j = points.size();
        for (int m = k; m >= 1; m--) {
          int i = start[m][j];
          int[] indices = new int[j - i + 1];
          for (int l = i; l <= j; l++) {
            indices[l - i] = points.get(order[l - 1]).index;
          }
          Arrays.sort(indices);
          result.add(indices);
          j = i - 1;
        }
        return result;
      }
    }

    /**
     * The partition of the points into <code>k</code> clusters: read off <code>line</code> for one
     * dimensional points and the euclidean distance, else the best of several k-means++ runs from a
     * fixed seed.
     *
     * @param line the exact one dimensional partitions, or <code>null</code>
     */
    private static List<int[]> partition(List<IndexedPoint> points, int k, LinePartitions line,
        DistanceMeasure measure) {
      if (k <= 1) {
        int[] all = new int[points.size()];
        for (int i = 0; i < all.length; i++) {
          all[i] = points.get(i).index;
        }
        List<int[]> result = new ArrayList<int[]>(1);
        result.add(all);
        return result;
      }
      if (line != null) {
        return line.partition(k);
      }
      KMeansPlusPlusClusterer<IndexedPoint> kMeans =
          new KMeansPlusPlusClusterer<IndexedPoint>(k, 100, measure, new Well19937c(k));
      return indicesOf(
          new MultiKMeansPlusPlusClusterer<IndexedPoint>(kMeans, 10).cluster(points));
    }

    /**
     * The mean silhouette width of a partition: for each point <code>1 - a/b</code> if
     * <code>a &lt; b</code> and <code>b/a - 1</code> else, with <code>a</code> the mean distance
     * to the other points of its cluster and <code>b</code> the least mean distance to the points
     * of another cluster; <code>0</code> for a point which is a cluster of its own.
     */
    private static double silhouette(List<IndexedPoint> points, List<int[]> clusters,
        DistanceMeasure measure, EvalEngine engine) {
      double[][] values = new double[points.size()][];
      for (IndexedPoint p : points) {
        values[p.index] = p.values();
      }
      double total = 0.0;
      for (int c = 0; c < clusters.size(); c++) {
        engine.checkTimeLimit();
        int[] own = clusters.get(c);
        if (own.length == 1) {
          continue;
        }
        for (int i : own) {
          double a = 0.0;
          for (int j : own) {
            a += measure.compute(values[i], values[j]);
          }
          a /= own.length - 1;
          double b = Double.POSITIVE_INFINITY;
          for (int d = 0; d < clusters.size(); d++) {
            if (d != c) {
              double mean = 0.0;
              for (int j : clusters.get(d)) {
                mean += measure.compute(values[i], values[j]);
              }
              b = Math.min(b, mean / clusters.get(d).length);
            }
          }
          double width = Math.max(a, b);
          if (width > 0.0) {
            total += (b - a) / width;
          }
        }
      }
      return total / points.size();
    }

    /**
     * The largest mean silhouette width of the partitions into <code>2 .. maxK</code> clusters, and
     * the partition which has it.
     *
     * @param threshold only a partition with a larger width is returned
     * @return the width and the partition, or <code>null</code> if no partition exceeds the
     *         threshold
     */
    private static Map.Entry<Double, List<int[]>> widestPartition(List<IndexedPoint> points,
        int maxK, boolean exactLine, DistanceMeasure measure, double threshold,
        EvalEngine engine) {
      LinePartitions line = exactLine && maxK > 1 ? new LinePartitions(points, maxK, engine) : null;
      Map.Entry<Double, List<int[]>> best = null;
      double bestWidth = threshold;
      for (int c = 2; c <= maxK; c++) {
        List<int[]> clusters = partition(points, c, line, measure);
        double width = silhouette(points, clusters, measure, engine);
        if (width > bestWidth) {
          bestWidth = width;
          best = new AbstractMap.SimpleImmutableEntry<Double, List<int[]>>(width, clusters);
        }
      }
      return best;
    }

    /**
     * As many points as <code>points</code>, spread evenly over their bounding box: equally spaced
     * on a line, a Halton sequence in more dimensions. The digits of each base are permuted by a
     * fixed permutation: in the plain sequence the coordinates of all bases above the number of
     * points grow together, and the points would lie on a line.
     */
    private static List<IndexedPoint> evenlySpread(List<IndexedPoint> points) {
      final int n = points.size();
      final int dimension = points.get(0).values().length;
      double[] low = points.get(0).values().clone();
      double[] high = low.clone();
      for (IndexedPoint p : points) {
        double[] values = p.values();
        for (int d = 0; d < dimension; d++) {
          low[d] = Math.min(low[d], values[d]);
          high[d] = Math.max(high[d], values[d]);
        }
      }
      int[] bases = new int[dimension];
      for (int d = 0, candidate = 2; d < dimension; candidate++) {
        boolean prime = true;
        for (int i = 0; i < d && bases[i] * bases[i] <= candidate; i++) {
          if (candidate % bases[i] == 0) {
            prime = false;
            break;
          }
        }
        if (prime) {
          bases[d++] = candidate;
        }
      }
      int[][] digits = new int[dimension][];
      for (int d = 0; d < dimension; d++) {
        // 0 stays 0, the other digits are shuffled from a seed of the base
        digits[d] = new int[bases[d]];
        for (int j = 0; j < bases[d]; j++) {
          digits[d][j] = j;
        }
        Well19937c random = new Well19937c(bases[d]);
        for (int j = bases[d] - 1; j > 1; j--) {
          int swap = 1 + random.nextInt(j);
          int digit = digits[d][j];
          digits[d][j] = digits[d][swap];
          digits[d][swap] = digit;
        }
      }
      List<IndexedPoint> result = new ArrayList<IndexedPoint>(n);
      for (int i = 0; i < n; i++) {
        double[] values = new double[dimension];
        for (int d = 0; d < dimension; d++) {
          double t;
          if (dimension == 1) {
            t = n > 1 ? (double) i / (n - 1) : 0.0;
          } else {
            // the radical inverse of i+1 in the base of this dimension
            t = 0.0;
            double digit = 1.0 / bases[d];
            for (int m = i + 1; m > 0; m /= bases[d], digit /= bases[d]) {
              t += digits[d][m % bases[d]] * digit;
            }
          }
          values[d] = low[d] + t * (high[d] - low[d]);
        }
        result.add(new IndexedPoint(values, i));
      }
      return result;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      String method = "";
      DistanceMeasure measure = new EuclideanDistance();
      try {
        if (ast.arg1().isList() && ast.arg1().size() > 1) {
          IAST list1 = (IAST) ast.arg1();
          int k = -1;
          double eps = 3.0;
          int minPts = 1;
          if (ast.size() > 2) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
            IExpr option = options.getOption(S.Method);
            if (option.isPresent()) {
              method = option.toString();
            }
            option = options.getOption(S.DistanceFunction);
            if (option.isPresent()) {
              measure = null;
              if (option.isBuiltInSymbol()) {
                IEvaluator distanceEvaluator = ((IBuiltInSymbol) option).getEvaluator();
                if (distanceEvaluator instanceof DistanceMeasure) {
                  measure = (DistanceMeasure) distanceEvaluator;
                }
              }
              if (measure == null) {
                return F.NIL;
              }
            }

            if (!"DBSCAN".equals(method) && !ast.arg2().isRuleAST()) {
              k = ast.arg2().toMachineInt();
              if (k <= 0) {
                return F.NIL;
              }
            }
          }
          if ("DBSCAN".equals(method)) {
            if (ast.size() < 5) {
              return F.NIL;
            }
            eps = engine.evalDouble(ast.arg2());
            minPts = ast.arg3().toMachineInt();
            if (minPts <= 0) {
              return F.NIL;
            }
          }

          final boolean listOfLists = list1.isListOfLists();
          final List<IndexedPoint> points = new ArrayList<IndexedPoint>(list1.argSize());
          if (listOfLists) {
            for (int j = 1; j < list1.size(); j++) {
              double[] values = list1.get(j).toDoubleVector();
              if (values == null || (j > 1 && values.length != points.get(0).values().length)) {
                // points of different dimensions have no distance
                return F.NIL;
              }
              points.add(new IndexedPoint(values, j - 1));
            }
          } else {
            double[] values = list1.toDoubleVector();
            if (values == null) {
              return F.NIL;
            }
            for (int i = 0; i < values.length; i++) {
              points.add(new IndexedPoint(new double[] {values[i]}, i));
            }
          }
          for (IndexedPoint p : points) {
            for (double v : p.values()) {
              if (!Double.isFinite(v)) {
                // an infinite point has no distance to the others
                return F.NIL;
              }
            }
          }
          if ("DBSCAN".equals(method)) {
            List<int[]> clusters =
                indicesOf(new DBSCANClusterer<IndexedPoint>(eps, minPts, measure).cluster(points));
            return clusters.isEmpty() ? F.NIL : clustersToList(clusters, list1);
          }
          // more clusters than distinct points would leave some of them empty
          final int distinct = (int) points.stream().map(p -> Arrays.toString(p.values()))
              .distinct().count();
          // every other method is k-means
          final boolean exactLine = !listOfLists && measure instanceof EuclideanDistance
              && points.size() <= MAX_QUADRATIC_POINTS;
          if (k > 0 || points.size() > MAX_QUADRATIC_POINTS) {
            final int clusterCount = Math.min(k > 0 ? k : DEFAULT_CLUSTERS, distinct);
            LinePartitions line =
                exactLine && clusterCount > 1 ? new LinePartitions(points, clusterCount, engine)
                    : null;
            return clustersToList(partition(points, clusterCount, line, measure), list1);
          }
          if (points.size() == 2) {
            // two points without a scale to compare their distance with: apart if it is positive
            List<int[]> clusters = new ArrayList<int[]>(2);
            if (measure.compute(points.get(0).values(), points.get(1).values()) > 0.0) {
              clusters.add(new int[] {0});
              clusters.add(new int[] {1});
            } else {
              clusters.add(new int[] {0, 1});
            }
            return clustersToList(clusters, list1);
          }
          // the number of clusters with the largest mean silhouette width, if that is clearly
          // larger than the width of evenly spread points
          final int maxK = Math.min(MAX_AUTOMATIC_CLUSTERS, Math.min(distinct, points.size() - 1));
          Map.Entry<Double, List<int[]>> best =
              widestPartition(points, maxK, exactLine, measure, MIN_SILHOUETTE, engine);
          if (best != null) {
            Map.Entry<Double, List<int[]>> reference =
                widestPartition(evenlySpread(points), maxK, exactLine, measure, 0.0, engine);
            if (reference != null && best.getKey() <= reference.getKey() + MIN_SILHOUETTE_GAIN) {
              best = null;
            }
          }
          return clustersToList(best == null ? partition(points, 1, null, measure) : best.getValue(),
              list1);
        }
      } catch (MathRuntimeException mrex) {
        return Errors.printMessage(ast.topHead(), mrex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5;
    }
  }

  static final class HammingDistance extends AbstractFunctionOptionEvaluator implements IDistance {

    private static final long serialVersionUID = -5707435719120777249L;

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IAST vect1 = (IAST) a.normal(false);
      IAST vect2 = (IAST) b.normal(false);
      long count = 0L;
      for (int i = 1; i < vect1.size(); i++) {
        if (!vect1.get(i).isSame(vect2.get(i))) {
          count++;
        }
      }
      return F.ZZ(count);
    }

    @Override
    public double compute(double[] u, double[] v) throws MathIllegalArgumentException {
      double count = 0.0;
      for (int i = 0; i < u.length; i++) {
        if (!F.isEqual(u[i], v[i])) {
          count++;
        }
      }
      return count;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isEmptyList() && arg2.isEmptyList()) {
        // no position differs
        return F.C0;
      }
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = option[0].isTrue();

        org.apache.commons.text.similarity.HammingDistance hammingDistance =
            new org.apache.commons.text.similarity.HammingDistance();
        String str1 = arg1.toString();
        String str2 = arg2.toString();
        if (str1.length() != str2.length()) {
          // `1` and `2` must have the same length.
          return Errors.printMessage(ast.topHead(), "idim", F.list(arg1, arg2), engine);
        }

        if (ignoreCase) {
          return F
              .ZZ(hammingDistance.apply(str1.toLowerCase(Locale.US), str2.toLowerCase(Locale.US)));
        }
        return F.ZZ(hammingDistance.apply(str1, str2));
      }

      // CPD-OFF per-metric closed form
      int dim1 = arg1.isVector();
      if (dim1 > (-1)) {
        int dim2 = arg2.isVector();
        if (dim2 > (-1)) {
          if (dim1 != dim2) {
            // The arguments `1` and `2` do not have compatible dimensions.
            return Errors.printMessage(ast.topHead(), "bldim", F.List(arg1, arg2), engine);
          }
          if (dim1 != 0) {
            return distance(arg1, arg2, engine);
            // CPD-ON
          }
        }
      }

      return F.NIL;
    }

    @Override
    public @AllowNIL IExpr numericFunctionDistance(IExpr a, IExpr b, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public @AllowNIL IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BinaryDistance.setEvaluator(new BinaryDistance());
      S.BrayCurtisDistance.setEvaluator(new BrayCurtisDistance());
      S.CanberraDistance.setEvaluator(new CanberraDistance());
      S.ChessboardDistance.setEvaluator(new ChessboardDistance());
      S.ClassifierFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.ClassifierFunction());
      S.ClassifierMeasurements
          .setEvaluator(new org.matheclipse.core.reflection.system.ClassifierMeasurements());
      S.Classify.setEvaluator(new org.matheclipse.core.reflection.system.Classify());
      S.CorrelationDistance.setEvaluator(new CorrelationDistance());
      S.CosineDistance.setEvaluator(new CosineDistance());
      S.EuclideanDistance.setEvaluator(new EuclideanDistance());
      S.FindClusters.setEvaluator(new FindClusters());
      S.HammingDistance.setEvaluator(new HammingDistance());
      S.ManhattanDistance.setEvaluator(new ManhattanDistance());
      S.SquaredEuclideanDistance.setEvaluator(new SquaredEuclideanDistance());
    }
  }

  /**
   *
   *
   * <pre>
   * ManhattanDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Manhattan distance between <code>u</code> and <code>v</code>, which is the number
   * of horizontal or vertical moves in the grid like Manhattan city layout to get from
   * <code>u</code> to <code>v</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Wikipedia - Taxicab geometry</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ManhattanDistance({-1, -1}, {1, 1})
   * 4
   * </pre>
   */
  private static final class ManhattanDistance extends AbstractDistance {
    private static final long serialVersionUID = -3203931866584067444L;

    static final DistanceMeasure distance =
        // CPD-OFF per-metric closed form
        new org.hipparchus.distance.ManhattanDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IAST vect1 = (IAST) a.normal(false);
      IAST vect2 = (IAST) b.normal(false);
      int size = a.size();
      IASTAppendable plusAST = F.PlusAlloc(size);
      // CPD-ON
      return plusAST.appendArgs(size, i -> F.Abs(F.Subtract(vect1.get(i), vect2.get(i))));
    }

    /**
     * A scalar argument is a one-element vector, so the sum above has a single term and reduces
     * to the absolute difference. Left to the inherited implementation
     * {@code ManhattanDistance[-7, 5]} stays unevaluated instead of giving {@code 12}.
     */
    @Override
    public IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return a.subtract(b).abs();
    }
  }

  /**
   *
   *
   * <pre>
   * SquaredEuclideanDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns squared the euclidean distance between <code>u$</code> and <code>v</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SquaredEuclideanDistance({-1, -1}, {1, 1})
   * 8
   * </pre>
   */
  static final class SquaredEuclideanDistance extends AbstractDistance {
    private static final long serialVersionUID = -34208439139174441L;

    @Override
    public double compute(double[] p1, double[] p2) throws MathIllegalArgumentException {
      double sum = 0;
      for (int i = 0; i < p1.length; i++) {
        final double absValue = Math.abs(p1[i] - p2[i]);
        sum += (absValue * absValue);
      }
      return sum;
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      IAST vect1 = (IAST) a.normal(false);
      IAST vect2 = (IAST) b.normal(false);
      int size = a.size();
      IASTAppendable plusAST = F.PlusAlloc(size);
      return plusAST.appendArgs(size, i -> F.Sqr(F.Abs(F.Subtract(vect1.get(i), vect2.get(i)))));
    }

    /**
     * A scalar argument is a one-element vector, so the sum above has a single term. Left to the
     * inherited implementation {@code SquaredEuclideanDistance[-7, 5]} stays unevaluated instead
     * of giving {@code 144}. {@code Abs} before squaring keeps complex arguments correct.
     */
    @Override
    public IExpr scalarDistance(INumber a, INumber b, EvalEngine engine) {
      return engine.evaluate(F.Sqr(F.Abs(F.Subtract(a, b))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ClusteringFunctions() {}
}
