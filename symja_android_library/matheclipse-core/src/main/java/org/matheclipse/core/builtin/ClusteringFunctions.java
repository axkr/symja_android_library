package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.clustering.Cluster;
import org.hipparchus.clustering.Clusterer;
import org.hipparchus.clustering.DBSCANClusterer;
import org.hipparchus.clustering.DoublePoint;
import org.hipparchus.clustering.KMeansPlusPlusClusterer;
import org.hipparchus.clustering.MultiKMeansPlusPlusClusterer;
import org.hipparchus.clustering.distance.DistanceMeasure;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.MathArrays;
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
  private static final Logger LOGGER = LogManager.getLogger();

  private abstract static class AbstractDistance extends AbstractEvaluator
      implements DistanceMeasure {

    public abstract IExpr distance(IExpr a, IExpr b);

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
      if (engine.isDoubleMode()
          || //
          arg1.isNumericAST()
          || //
          arg2.isNumericAST()) {
        double[] a = arg1.toDoubleVector();
        if (a != null) {
          double[] b = arg2.toDoubleVector();
          if (b != null) {
            return F.num(compute(a, b));
          }
        }
      }
      return distance(arg1, arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class BinaryDistance extends AbstractDistance {

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
      return distance(arg1, arg2);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b) {
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
   * <p>returns the Bray Curtis distance between <code>u</code> and <code>v</code>.
   *
   * </blockquote>
   *
   * <pre>
   * &gt;&gt; BrayCurtisDistance[{-1, -1}, {10, 10}]
   * 11/9
   * </pre>
   */
  private static final class BrayCurtisDistance extends AbstractDistance {

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
    public IExpr distance(IExpr a, IExpr b) {
      return F.Divide(F.Total(F.Abs(F.Subtract(a, b))), F.Total(F.Abs(F.Plus(a, b))));
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
   * <p>returns the canberra distance between <code>u</code> and <code>v</code>, which is a weighted
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
    static final DistanceMeasure distance =
        new org.hipparchus.clustering.distance.CanberraDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IAST distance(IExpr a, IExpr b) {
      return F.Total(F.Divide(F.Abs(F.Subtract(a, b)), F.Plus(F.Abs(a), F.Abs(b))));
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
   * <p>returns the chessboard distance (also known as Chebyshev distance) between <code>u</code>
   * and <code>v</code>, which is the number of moves a king on a chessboard needs to get from
   * square <code>u</code> to square <code>v</code>.
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

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      return MathArrays.distanceInf(a, b);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b) {
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

  /**
   *
   *
   * <pre>
   * CosineDistance(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the cosine distance between <code>u</code> and <code>v</code>.
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
  private static final class CosineDistance extends AbstractDistance {

    @Override
    public double compute(double[] a, double[] b) throws MathIllegalArgumentException {
      return 1.0 - MathArrays.cosAngle(a, b);
    }

    @Override
    public IExpr distance(IExpr arg1, IExpr arg2) {
      return F.Subtract(F.C1, F.Divide(F.Dot(arg1, arg2), F.Times(F.Norm(arg1), F.Norm(arg2))));
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
   * <p>returns the euclidean distance between <code>u</code> and <code>v</code>.
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
    static final DistanceMeasure distance =
        new org.hipparchus.clustering.distance.EuclideanDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b) {
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
          IAST listArg1 = (IAST) ast.arg1();
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

            if ("KMeans".equals(method)
                || //
                "".equals(method)) {
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
            final List<DoublePoint> points = new ArrayList<DoublePoint>(listArg1.argSize());
            if (listArg1.isListOfLists()) {
              for (int j = 1; j < listArg1.size(); j++) {
                double[] values = listArg1.get(j).toDoubleVector();
                if (values == null) {
                  return F.NIL;
                }
                final DoublePoint p = new DoublePoint(values);
                points.add(p);
              }
            } else {
              double[] values = listArg1.toDoubleVector();
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
            // final FuzzyKMeansClusterer<DoublePoint> transformer = new
            // FuzzyKMeansClusterer<DoublePoint>(k,
            // 2.0);
            final List<? extends Cluster<DoublePoint>> clusters = transformer.cluster(points);
            IASTAppendable result = F.ListAlloc(clusters.size());
            for (final Cluster<DoublePoint> cluster : clusters) {
              final List<DoublePoint> clusterPoints = cluster.getPoints();
              IASTAppendable list = F.ListAlloc(clusterPoints.size());
              if (listArg1.isListOfLists()) {
                for (int i = 0; i < clusterPoints.size(); i++) {
                  double[] dVector = clusterPoints.get(i).getPoint().clone();
                  list.append(new ASTRealVector(dVector, false));
                }
              } else {
                for (int i = 0; i < clusterPoints.size(); i++) {
                  list.append(clusterPoints.get(i).getPoint()[0]);
                }
              }
              result.append(list);
            }
            return result;
          }
        }
      } catch (MathRuntimeException mrex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), mrex);
      }
      return F.NIL;
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
   * <p>returns the Manhattan distance between <code>u</code> and <code>v</code>, which is the
   * number of horizontal or vertical moves in the grid like Manhattan city layout to get from
   * <code>u</code> to <code>v</code>.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Wikipedia - Taxicab geometry</a>
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
    static final DistanceMeasure distance =
        new org.hipparchus.clustering.distance.ManhattanDistance();

    @Override
    public double compute(double[] arg0, double[] arg1) throws MathIllegalArgumentException {
      return distance.compute(arg0, arg1);
    }

    @Override
    public IExpr distance(IExpr a, IExpr b) {
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
   * <p>returns squared the euclidean distance between <code>u$</code> and <code>v</code>.
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
    public IExpr distance(IExpr a, IExpr b) {
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
