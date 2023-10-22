package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.clustering.Cluster;
import org.hipparchus.clustering.Clusterer;
import org.hipparchus.clustering.DBSCANClusterer;
import org.hipparchus.clustering.DoublePoint;
import org.hipparchus.clustering.KMeansPlusPlusClusterer;
import org.hipparchus.clustering.MultiKMeansPlusPlusClusterer;
import org.hipparchus.clustering.distance.DistanceMeasure;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.stat.StatUtils;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class ClusteringFunctions {

  private abstract static class AbstractDistance extends AbstractEvaluator
      implements DistanceMeasure {
    private static final long serialVersionUID = -295980120043414467L;

    public abstract IExpr distance(IExpr a, IExpr b, EvalEngine engine);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      int dim1 = arg1.isVector();
      if (dim1 > (-1)) {
        int dim2 = arg2.isVector();
        if (dim1 == dim2) {
          if (dim1 == 0) {
            return F.NIL;
          }
          return vectorDistance(arg1, arg2, engine);
        }
      }
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
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
    protected IExpr vectorDistance(IExpr arg1, IExpr arg2, EvalEngine engine) {
      // don't call numeric case here!
      return distance(arg1, arg2, engine);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b, EvalEngine engine) {
      return a.equals(b) ? F.C1 : F.C0;
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
        new org.hipparchus.clustering.distance.CanberraDistance();

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
    public IExpr distance(IExpr arg1, IExpr arg2, EvalEngine engine) {
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
        IExpr timesV1 = S.Times.of(engine, F.QQ(-1, v1Length), factorV1);
        IExpr timesV2 = S.Times.of(engine, F.QQ(-1, v1Length), factorV2);

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

          factorV1.append(v1Arg);
          factorV1.append(v2Arg);
        }
        IExpr denominator = engine.evaluate(F.Sqrt(F.Times(plusV1, plusV2)));
        if (denominator.isZero()) {
          return F.C0;
        }
        return F.Subtract(F.C1, F.Divide(plusNumerator, denominator));
      }
      return F.NIL;
    }
    // @Override
    // public IExpr distance(IExpr arg1, IExpr arg2, EvalEngine engine) {
    // int v1Length = arg1.isVector();
    // int v2Length = arg2.isVector();
    // if (v1Length == v2Length && v2Length > 0) {
    // IExpr mean1 = S.Mean.of(engine, F.Unevaluated(arg1)).negate();
    // IExpr mean2 = S.Mean.of(engine, F.Unevaluated(arg2)).negate();
    // IExpr u = arg1.mapExpr(x -> x.plus(mean1));
    // IExpr v = arg2.mapExpr(x -> x.plus(mean2));
    // return super.distance(u, v, engine);
    // }
    // return F.NIL;
    // }

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
      return F.Subtract(F.C1, F.Divide(F.Dot(arg1, F.Conjugate(arg2)), F.Times(norm1, norm2)));
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
        new org.hipparchus.clustering.distance.EuclideanDistance();

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
      plusAST.appendArgs(size, i -> F.Sqr(F.Abs(F.Subtract(vect1.get(i), vect2.get(i)))));
      return F.Sqrt(plusAST);
    }
  }

  private static class FindClusters extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String method = "";
      DistanceMeasure measure = new EuclideanDistance();
      try {
        if (ast.arg1().isList() && ast.arg1().size() > 1) {
          IAST list1 = (IAST) ast.arg1();
          int k = 3;
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

            if ("KMeans".equals(method) || "".equals(method)) {
              k = ast.arg2().toIntDefault();
              if (k == Integer.MIN_VALUE) {
                k = 3;
              }
            }
          }
          if ("DBSCAN".equals(method)) {
            if (ast.size() < 5) {
              return F.NIL;
            }
            eps = engine.evalDouble(ast.arg2());
            minPts = ast.arg3().toIntDefault();
            if (minPts <= 0) {
              return F.NIL;
            }
          }

          if (k > 0) {
            final List<DoublePoint> points = new ArrayList<DoublePoint>(list1.argSize());
            if (list1.isListOfLists()) {
              for (int j = 1; j < list1.size(); j++) {
                double[] values = list1.get(j).toDoubleVector();
                if (values == null) {
                  return F.NIL;
                }
                final DoublePoint p = new DoublePoint(values);
                points.add(p);
              }
            } else {
              double[] values = list1.toDoubleVector();
              if (values == null) {
                return F.NIL;
              }
              for (int i = 0; i < values.length; i++) {
                final DoublePoint p = new DoublePoint(new double[] {values[i]});
                points.add(p);
              }
            }
            final Clusterer<DoublePoint> transformer;
            if ("KMeans".equals(method)) {
              transformer = new KMeansPlusPlusClusterer<DoublePoint>(k, 100, measure);
            } else if ("DBSCAN".equals(method)) {
              transformer = new DBSCANClusterer<DoublePoint>(eps, minPts, measure);
            } else {
              KMeansPlusPlusClusterer<DoublePoint> kMeansTransformer =
                  new KMeansPlusPlusClusterer<DoublePoint>(k, 100, measure);
              transformer = new MultiKMeansPlusPlusClusterer<DoublePoint>(kMeansTransformer, 10);
            }
            List<? extends Cluster<DoublePoint>> cluster = transformer.cluster(points);
            if (cluster != null && cluster.size() > 0) {
              return clustersToList(cluster, list1.isListOfLists());
            }
          }
        }
      } catch (MathRuntimeException mrex) {
        return Errors.printMessage(ast.topHead(), mrex, engine);
      }
      return F.NIL;
    }

    /**
     * Convert the calculated list of clusters to a symja list.
     * 
     * @param clusters
     * @param isListOfLists
     * @return
     */
    private static IAST clustersToList(final List<? extends Cluster<DoublePoint>> clusters,
        boolean isListOfLists) {
      return F.mapRange(0, clusters.size(), j -> {
        final List<DoublePoint> clusterPoints = clusters.get(j).getPoints();
        if (isListOfLists) {
          return F.mapRange(0, clusterPoints.size(),
              i -> new ASTRealVector(clusterPoints.get(i).getPoint().clone(), false));
        }
        return F.mapRange(0, clusterPoints.size(), i -> F.num(clusterPoints.get(i).getPoint()[0]));
      });
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5;
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
      S.CorrelationDistance.setEvaluator(new CorrelationDistance());
      S.CosineDistance.setEvaluator(new CosineDistance());
      S.EuclideanDistance.setEvaluator(new EuclideanDistance());
      S.FindClusters.setEvaluator(new FindClusters());
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
        new org.hipparchus.clustering.distance.ManhattanDistance();

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
      return plusAST.appendArgs(size, i -> F.Abs(F.Subtract(vect1.get(i), vect2.get(i))));
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
