package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FormalSymbol;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The named coordinate charts: their metric, dimension and default parameters, and for the charts
 * of the Euclidean spaces the mapping of their coordinates to Cartesian coordinates.
 *
 * <p>
 * A chart with parameters is written <code>{"Bipolar", {a}}</code>, a chart without parameters as
 * its name <code>"Polar"</code>. A transformation is given as <code>{chart1 -&gt; chart2, metric,
 * dimension}</code>; the short form <code>chart1 -&gt; chart2</code> uses the default parameters
 * and the only dimension of the charts.
 */
public final class CoordinateCharts {

  /** The metric of the Euclidean charts. */
  public static final String EUCLIDEAN = "Euclidean";

  /** The name of the Cartesian chart. */
  public static final String CARTESIAN = "Cartesian";

  /** A named chart of one metric in one dimension. */
  public static final class Chart {
    /** The name, e.g. <code>"Bipolar"</code>. */
    final String name;

    /** The name by which the charts are ordered in the listing of all transformations. */
    final String sortName;

    /** The metric, <code>"Euclidean"</code> or e.g. <code>{"Sphere", {R}}</code>. */
    final IExpr metric;

    /** The number of coordinates. */
    final int dimension;

    /** The default parameters, e.g. <code>{\[FormalA]}</code>. */
    final IAST parameters;

    /**
     * The Cartesian coordinates of the point with the coordinates <code>x</code> (a list with
     * {@link #dimension} elements) for the parameters <code>p</code>, or <code>null</code> if the
     * chart has no mapping to Cartesian coordinates.
     */
    final BiFunction<IAST, IAST, IAST> toCartesian;

    Chart(String name, String sortName, IExpr metric, int dimension, IAST parameters,
        BiFunction<IAST, IAST, IAST> toCartesian) {
      this.name = name;
      this.sortName = sortName;
      this.metric = metric;
      this.dimension = dimension;
      this.parameters = parameters;
      this.toCartesian = toCartesian;
    }

    public int dimension() {
      return dimension;
    }

    public String name() {
      return name;
    }

    /** The chart as it is written with the parameters <code>p</code>. */
    IExpr spec(IAST p) {
      return p.argSize() == 0 ? F.stringx(name) : F.list(F.stringx(name), p);
    }
  }

  /** A chart with the parameters, which are used in a transformation. */
  public static final class ChartSpec {
    public final Chart chart;
    public final IAST parameters;

    ChartSpec(Chart chart, IAST parameters) {
      this.chart = chart;
      this.parameters = parameters;
    }

    /**
     * The Cartesian coordinates of <code>point</code>.
     *
     * @return {@link F#NIL} if the chart has no mapping to Cartesian coordinates
     */
    public IAST toCartesian(IAST point) {
      return chart.toCartesian == null ? F.NIL : chart.toCartesian.apply(point, parameters);
    }
  }

  private static final class Holder {
    static final List<Chart> CHARTS = build();
    static final IAST LISTING = listing(CHARTS);
  }

  private CoordinateCharts() {}

  /** The not evaluated product, which keeps the order of the factors. */
  private static IExpr times(IExpr... factors) {
    return F.function(S.Times, factors);
  }

  /** <code>Cosh(v) - Cos(u)</code> */
  private static IExpr coshMinusCos(IExpr v, IExpr u) {
    return F.Plus(F.Negate(F.Cos(u)), F.Cosh(v));
  }

  /** <code>(u^2 - v^2) / 2</code> */
  private static IExpr halfDifferenceOfSquares(IExpr u, IExpr v) {
    return times(F.C1D2, F.Subtract(F.Sqr(u), F.Sqr(v)));
  }

  /**
   * The confocal ellipsoidal coordinates in <code>n</code> dimensions: the square of the
   * <code>i</code>-th Cartesian coordinate is
   * <code>Product(p_i + x_j, j) / Product(p_i - p_k, k != i)</code>.
   */
  private static IAST confocal(IAST x, IAST p) {
    final int n = x.argSize();
    return F.mapRange(1, n + 1, i -> {
      IExpr numerator = F.mapRange(S.Times, 1, n + 1, j -> F.Plus(p.get(i), x.get(j)));
      IASTAppendable denominator = F.TimesAlloc(n);
      for (int k = 1; k <= n; k++) {
        if (k != i) {
          denominator.append(F.Subtract(p.get(i), p.get(k)));
        }
      }
      return F.Sqrt(F.Divide(numerator, denominator.oneIdentity1()));
    });
  }

  /**
   * The hyperspherical coordinates <code>{r, theta_1, ..., theta_(n-1)}</code>: the
   * <code>k</code>-th Cartesian coordinate is
   * <code>r*Sin(theta_1)*...*Sin(theta_(k-1))*Cos(theta_k)</code>, the last one ends with a sine.
   */
  private static IAST hyperspherical(IAST x, IAST p) {
    final int n = x.argSize();
    return F.mapRange(1, n + 1, k -> {
      IASTAppendable product = F.TimesAlloc(k + 1);
      product.append(x.arg1());
      for (int j = 2; j <= k; j++) {
        product.append(F.Sin(x.get(j)));
      }
      if (k < n) {
        product.append(F.Cos(x.get(k + 1)));
      }
      return product.oneIdentity1();
    });
  }

  private static List<Chart> build() {
    final IExpr euclidean = F.stringx(EUCLIDEAN);
    final IAST a = F.list(S.a);
    final IAST none = F.CEmptyList;
    final IExpr alpha = FormalSymbol.greek("FormalAlpha");
    final IExpr beta = FormalSymbol.greek("FormalBeta");
    final IExpr gamma = FormalSymbol.greek("FormalGamma");
    List<Chart> charts = new ArrayList<>();

    charts.add(new Chart(CARTESIAN, CARTESIAN, euclidean, 1, none, (x, p) -> x));

    // the plane and its cylindrical extensions, whose third coordinate is the Cartesian z
    BiFunction<IAST, IAST, IAST> bipolar = (x, p) -> {
      IExpr d = coshMinusCos(x.arg2(), x.arg1());
      return F.list(F.Divide(times(p.arg1(), F.Sinh(x.arg2())), d),
          F.Divide(times(p.arg1(), F.Sin(x.arg1())), d));
    };
    BiFunction<IAST, IAST, IAST> elliptic =
        (x, p) -> F.list(times(p.arg1(), F.Cos(x.arg2()), F.Cosh(x.arg1())),
            times(p.arg1(), F.Sin(x.arg2()), F.Sinh(x.arg1())));
    BiFunction<IAST, IAST, IAST> parabolic =
        (x, p) -> F.list(halfDifferenceOfSquares(x.arg1(), x.arg2()), times(x.arg1(), x.arg2()));
    // in the not evaluated products of the polar charts the trigonometric functions are in front
    BiFunction<IAST, IAST, IAST> polar =
        (x, p) -> F.list(times(F.Cos(x.arg2()), x.arg1()), times(F.Sin(x.arg2()), x.arg1()));

    charts.add(new Chart("Bipolar", "Bipolar", euclidean, 2, a, bipolar));
    charts.add(new Chart(CARTESIAN, CARTESIAN, euclidean, 2, none, (x, p) -> x));
    charts.add(new Chart("Confocal", "Confocal", euclidean, 2, F.list(alpha, beta),
        CoordinateCharts::confocal));
    charts.add(new Chart("Elliptic", "Elliptic", euclidean, 2, a, elliptic));
    // the 2-dimensional hyperspherical chart
    charts.add(new Chart("Polar", "Hyperspherical", euclidean, 2, none, polar));
    charts.add(new Chart("PlanarParabolic", "PlanarParabolic", euclidean, 2, none, parabolic));

    charts.add(cylinder("BipolarCylindrical", a, bipolar));
    charts.add(new Chart("Bispherical", "Bispherical", euclidean, 3, a, (x, p) -> {
      IExpr d = coshMinusCos(x.arg2(), x.arg1());
      return F.list(F.Divide(times(p.arg1(), F.Cos(x.arg3()), F.Sin(x.arg1())), d),
          F.Divide(times(p.arg1(), F.Sin(x.arg1()), F.Sin(x.arg3())), d),
          F.Divide(times(p.arg1(), F.Sinh(x.arg2())), d));
    }));
    charts.add(new Chart(CARTESIAN, CARTESIAN, euclidean, 3, none, (x, p) -> x));
    charts.add(new Chart("CircularParabolic", "CircularParabolic", euclidean, 3, none,
        (x, p) -> F.list(times(x.arg1(), x.arg2(), F.Cos(x.arg3())),
            times(x.arg1(), x.arg2(), F.Sin(x.arg3())),
            halfDifferenceOfSquares(x.arg1(), x.arg2()))));
    charts.add(new Chart("Confocal", "Confocal", euclidean, 3, F.list(alpha, beta, gamma),
        CoordinateCharts::confocal));
    charts.add(new Chart("ConfocalParaboloidal", "ConfocalParaboloidal", euclidean, 3,
        F.list(alpha, beta), (x, p) -> {
          IExpr productA = F.mapRange(S.Times, 1, 4, j -> F.Subtract(p.arg1(), x.get(j)));
          IExpr productB = F.mapRange(S.Times, 1, 4, j -> F.Subtract(p.arg2(), x.get(j)));
          return F.list(F.Sqrt(F.Divide(productA, F.Subtract(p.arg2(), p.arg1()))),
              F.Sqrt(F.Divide(productB, F.Subtract(p.arg1(), p.arg2()))),
              times(F.C1D2, F.Plus(p.arg1(), p.arg2(), F.Negate(x.arg1()), F.Negate(x.arg2()),
                  F.Negate(x.arg3()))));
        }));
    charts.add(new Chart("Conical", "Conical", euclidean, 3, F.list(S.b, S.c), (x, p) -> {
      IExpr b2 = F.Sqr(p.arg1());
      IExpr c2 = F.Sqr(p.arg2());
      IExpr v2 = F.Sqr(x.arg2());
      IExpr w2 = F.Sqr(x.arg3());
      IExpr c2MinusB2 = F.Subtract(c2, b2);
      return F.list(
          F.Divide(times(x.arg1(),
              F.Sqrt(F.Divide(times(F.Subtract(c2, v2), F.Subtract(c2, w2)), c2MinusB2))),
              p.arg2()),
          F.Divide(times(x.arg1(),
              F.Sqrt(F.Divide(times(F.Subtract(v2, b2), F.Subtract(b2, w2)), c2MinusB2))),
              p.arg1()),
          F.Divide(times(x.arg1(), x.arg2(), x.arg3()), times(p.arg1(), p.arg2())));
    }));
    charts.add(cylinder("Cylindrical", none, polar));
    charts.add(cylinder("EllipticCylindrical", a, elliptic));
    charts.add(new Chart("Hyperspherical", "Hyperspherical", euclidean, 3, none,
        CoordinateCharts::hyperspherical));
    charts.add(new Chart("OblateSpheroidal", "OblateSpheroidal", euclidean, 3, a,
        (x, p) -> F.list(times(p.arg1(), F.Cos(x.arg3()), F.Cosh(x.arg1()), F.Sin(x.arg2())),
            times(p.arg1(), F.Cosh(x.arg1()), F.Sin(x.arg2()), F.Sin(x.arg3())),
            times(p.arg1(), F.Cos(x.arg2()), F.Sinh(x.arg1())))));
    charts.add(cylinder("ParabolicCylindrical", none, parabolic));
    charts.add(new Chart("ProlateSpheroidal", "ProlateSpheroidal", euclidean, 3, a,
        (x, p) -> F.list(times(p.arg1(), F.Cos(x.arg3()), F.Sin(x.arg2()), F.Sinh(x.arg1())),
            times(p.arg1(), F.Sin(x.arg2()), F.Sin(x.arg3()), F.Sinh(x.arg1())),
            times(p.arg1(), F.Cos(x.arg2()), F.Cosh(x.arg1())))));
    charts.add(new Chart("Spherical", "Spherical", euclidean, 3, none,
        (x, p) -> F.list(times(F.Cos(x.arg3()), F.Sin(x.arg2()), x.arg1()),
            times(F.Sin(x.arg2()), F.Sin(x.arg3()), x.arg1()),
            times(F.Cos(x.arg2()), x.arg1()))));
    charts.add(new Chart("Toroidal", "Toroidal", euclidean, 3, a, (x, p) -> {
      IExpr d = coshMinusCos(x.arg1(), x.arg2());
      return F.list(F.Divide(times(p.arg1(), F.Cos(x.arg3()), F.Sinh(x.arg1())), d),
          F.Divide(times(p.arg1(), F.Sin(x.arg3()), F.Sinh(x.arg1())), d),
          F.Divide(times(p.arg1(), F.Sin(x.arg2())), d));
    }));

    // the listing has no 4-dimensional Cartesian chart, but the charts map to Cartesian coordinates
    IAST subscriptedAlpha = F.mapRange(1, 5, i -> F.Subscript(alpha, F.ZZ(i)));
    charts.add(new Chart("Confocal", "Confocal", euclidean, 4, subscriptedAlpha,
        CoordinateCharts::confocal));
    charts.add(new Chart("Hyperspherical", "Hyperspherical", euclidean, 4, none,
        CoordinateCharts::hyperspherical));

    // the charts of the spheres of radius R
    IExpr sphere = F.list(F.stringx("Sphere"), F.list(S.initFinalHiddenSymbol("R")));
    for (int n = 1; n <= 3; n++) {
      charts.add(new Chart("Standard", "Standard", sphere, n, none, null));
      charts.add(new Chart("Stereographic", "Stereographic", sphere, n, none, null));
    }
    return charts;
  }

  /** The cylindrical extension of a 2-dimensional chart, whose third coordinate is the Cartesian z. */
  private static Chart cylinder(String name, IAST parameters, BiFunction<IAST, IAST, IAST> planar) {
    return new Chart(name, name, F.stringx(EUCLIDEAN), 3, parameters, (x, p) -> {
      IAST xy = planar.apply(x, p);
      return F.list(xy.arg1(), xy.arg2(), x.arg3());
    });
  }

  /**
   * The list of all transformations <code>{chart1 -&gt; chart2, metric, dimension}</code> between
   * two charts of the same metric and dimension, ordered by the metric, the source chart, the
   * dimension and the target chart. A parameter of the target chart which is also a parameter of
   * the source chart is renamed from <code>p</code> to <code>p[1]</code>.
   */
  private static IAST listing(List<Chart> charts) {
    Comparator<Chart> bySortName = Comparator.comparing(c -> c.sortName);
    List<Chart> sources = new ArrayList<>(charts);
    // the Euclidean charts come first, the charts of the other metrics keep their order
    sources.sort(Comparator.<Chart, Boolean>comparing(c -> !c.metric.isString(EUCLIDEAN))
        .thenComparing(bySortName).thenComparingInt(c -> c.dimension));
    IASTAppendable result = F.ListAlloc(300);
    for (Chart source : sources) {
      List<Chart> targets = new ArrayList<>();
      for (Chart target : charts) {
        if (target.dimension == source.dimension && target.metric.equals(source.metric)) {
          targets.add(target);
        }
      }
      targets.sort(bySortName);
      for (Chart target : targets) {
        IAST targetParameters = target.parameters
            .map(p -> source.parameters.contains(p) ? F.unaryAST1(p, F.C1) : p);
        result.append(F.list(F.Rule(source.spec(source.parameters), target.spec(targetParameters)),
            source.metric, F.ZZ(source.dimension)));
      }
    }
    return result;
  }

  /** The list of all transformations <code>{chart1 -&gt; chart2, metric, dimension}</code>. */
  public static IAST transformations() {
    return Holder.LISTING;
  }

  /** The chart with the name, metric and dimension, or <code>null</code>. */
  private static Chart find(String name, IExpr metric, int dimension) {
    for (Chart chart : Holder.CHARTS) {
      if (chart.dimension == dimension && chart.name.equals(name) && chart.metric.equals(metric)) {
        return chart;
      }
    }
    return null;
  }

  /** Test if there is a chart of the name and metric in some dimension. */
  public static boolean exists(String name, IExpr metric) {
    for (Chart chart : Holder.CHARTS) {
      if (chart.name.equals(name) && chart.metric.equals(metric)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The dimension of a chart of the metric, if the name is used in one dimension only.
   *
   * @return <code>-1</code> if the chart isn't known or exists in several dimensions
   */
  public static int uniqueDimension(String name, IExpr metric) {
    int dimension = -1;
    for (Chart chart : Holder.CHARTS) {
      if (chart.name.equals(name) && chart.metric.equals(metric)) {
        if (dimension > 0 && dimension != chart.dimension) {
          return -1;
        }
        dimension = chart.dimension;
      }
    }
    return dimension;
  }

  /**
   * The chart of the chart specification <code>"Name"</code> or
   * <code>{"Name", {p1, p2, ...}}</code>.
   *
   * @param name the name of the chart
   * @param parameters the parameters or <code>null</code> for the default parameters
   * @param metric the metric
   * @param dimension the dimension
   * @return <code>null</code> if the chart isn't known or the number of parameters doesn't fit
   */
  public static ChartSpec chartSpec(String name, IAST parameters, IExpr metric, int dimension) {
    Chart chart = find(name, metric, dimension);
    if (chart == null && name.equals(CARTESIAN) && metric.isString(EUCLIDEAN)) {
      // the Cartesian chart exists in every dimension
      chart = new Chart(CARTESIAN, CARTESIAN, metric, dimension, F.CEmptyList, (x, p) -> x);
    }
    if (chart == null) {
      return null;
    }
    if (parameters == null) {
      return new ChartSpec(chart, chart.parameters);
    }
    if (parameters.argSize() != chart.parameters.argSize()) {
      return null;
    }
    return new ChartSpec(chart, parameters);
  }
}
