package org.matheclipse.core.reflection.system;

import static java.lang.Double.compare;
import static java.lang.Double.isFinite;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots x/y functions */
public class Plot extends ListPlot {
  final static double NUMBER_OF_PIXELS = 1200.0;

  /**
   * See: <a href=
   * "https://www.andr.mu/logs/acquiring-samples-to-plot-a-math-function-adaptive/">Acquiring
   * Samples to Plot a Math Function: Adaptive Sampling</a>
   *
   */
  private static class AdaptivePlot {
    private static class Point {
      final double x, y;

      public Point(double x, double y) {
        this.x = x;
        this.y = y;
      }
    }

    public static double[][] computePlot(final UnaryNumerical hun, double[][] data,
        final double xMin, final double xMax) {
      List<Point> plot =
          new AdaptivePlot(hun, xMin, xMax).computePlot(8, 1.0 / NUMBER_OF_PIXELS).getPlot();
      if (plot.size() > 0) {
        data = new double[2][plot.size()];
        int i = 0;
        for (Point p : plot) {
          data[0][i] = p.x;
          data[1][i] = p.y;
          i++;
        }
      }
      return data;
    }

    private static boolean isOscillation(double ya, double yb, double yc) {
      return !isFinite(ya) || !isFinite(yb) || !isFinite(yc) || (yb > ya && yb > yc)
          || (yb < ya && yb < yc);
    }

    private static boolean oscillates(double ya, double ya1, double yb, double yb1, double yc) {
      return isOscillation(ya, ya1, yb) && isOscillation(ya1, yb, yb1)
          && isOscillation(yb, yb1, yc);
    }

    private static double quadrature(double y0, double y1, double y2) {
      return 5d / 12d * y0 + 2d / 3d * y1 - 1d / 12d * y2;
    }

    private static double quadrature(double y0, double y1, double y2, double y3) {
      return 3d / 8d * y0 + 19d / 24d * y1 - 5d / 24d * y2 + 1d / 24d * y3;
    }

    private static boolean unsmooth(double ya, double ya1, double yb, double yb1, double yc,
        double eps) {
      double y0 = DoubleStream.of(ya, ya1, yb, yb1, yc).min().getAsDouble();
      double[] yg = DoubleStream.of(ya, ya1, yb, yb1, yc).map(y -> y - y0).toArray();
      double q4 = quadrature(yg[0], yg[1], yg[2], yg[3]);
      double q3 = quadrature(yg[2], yg[3], yg[4]);
      return Math.abs(q4 - q3) >= eps * q3;
    }

    private final DoubleUnaryOperator f;

    private double a;

    private double c;

    private final SortedSet<Point> plot = new TreeSet<>((s, t) -> compare(s.x, t.x));

    public AdaptivePlot(DoubleUnaryOperator f, double a, double c) {
      this.f = f;
      this.a = a;
      this.c = c;
    }

    public AdaptivePlot computePlot(int depth, double eps) {
      plot.clear();
      Point pa = null;
      double step = (c - a) / NUMBER_OF_PIXELS;
      while (a <= c) {
        pa = pointAt(a);
        if (pa != null) {
          break;
        }
        a += step;
      }
      if (pa != null) {
        Point pc = null;
        while (c >= a) {
          pc = pointAt(c);
          if (pc != null) {
            break;
          }
          c -= step;

        }
        if (pc != null) {
          plot.add(pa);
          plot.add(pc);
          computePlot(pa, pc, depth, eps);
        }
      }
      return this;
    }

    private void computePlot(Point pa, Point pc, int depth, double eps) {
      Point pb = pointAt(0.5 * (pa.x + pc.x));
      if (pb != null) {
        Point pa1 = pointAt(0.5 * (pa.x + pb.x));
        Point pb1 = pointAt(0.5 * (pb.x + pc.x));
        if (pa1 != null && pb1 != null) {
          plot.add(pb);
          if (depth > 0 && (oscillates(pa.y, pa1.y, pb.y, pb1.y, pc.y)
              || unsmooth(pa.y, pa1.y, pb.y, pb1.y, pc.y, eps))) {
            computePlot(pa, pb, depth - 1, 2 * eps);
            computePlot(pb, pc, depth - 1, 2 * eps);
          }
          plot.add(pa1);
          plot.add(pb1);
        }
      }
    }

    public List<Point> getPlot() {
      return plot.stream().collect(toList());
    }

    private Point pointAt(double x) {
      try {
        return new Point(x, f.applyAsDouble(x));
      } catch (ArgumentTypeException ate) {

      }
      return null;
    }
  }

  /** Constructor for the singleton */
  public static final Plot CONST = new Plot();

  public Plot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    if (ast.arg2().isList3()) {
      final IAST rangeList = (IAST) ast.arg2();
      IExpr variable = rangeList.arg1();
      if (variable.isVariable()) {
        try {
          if (rangeList.isList3()) {
            GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
            setGraphicOptions(graphicsOptions);
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
            graphicsOptions.setOptions(options);
            final IAST listOfLines =
                plotToListPoints(function, rangeList, ast, graphicsOptions, engine);
            if (listOfLines.isNIL()) {
              return F.NIL;
            }

            // simulate ListPlot data
            GraphicsOptions listPlotOptions = graphicsOptions.copy();
            IASTMutable listPlot = ast.setAtCopy(1, listOfLines);
            IAST graphicsPrimitives = plot(listPlot, listPlotOptions, engine);
            if (graphicsPrimitives.isPresent()) {
              graphicsOptions.addPadding();
              listPlotOptions.setBoundingBox(graphicsOptions.boundingBox());
              // listPlotOptions.mergeOptions(listPlotOptions.options().getListOfRules());
              IAST listOfOptions = listOfOptionRules(listPlotOptions);
              return createGraphicsFunction(graphicsPrimitives, listOfOptions, listPlotOptions);
            }

          }
        } catch (RuntimeException rex) {
          rex.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  private static IAST plotToListPoints(IExpr functionOrListOfFunctions, final IAST rangeList,
      final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (!rangeList.arg1().isSymbol()) {
      // `1` is not a valid variable.
      return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol x = (ISymbol) rangeList.arg1();
    final IExpr xMin = engine.evalN(rangeList.arg2());
    final IExpr xMax = engine.evalN(rangeList.arg3());
    if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || xMin.equals(xMax)) {
      // Endpoints in `1` must be distinct machine-size real numbers.
      return IOFunctions.printMessage(ast.topHead(), "plld", F.List(x, rangeList), engine);
    }
    double xMinD = ((INum) xMin).getRealPart();
    double xMaxD = ((INum) xMax).getRealPart();
    if (xMaxD < xMinD) {
      double temp = xMinD;
      xMinD = xMaxD;
      xMaxD = temp;
    }

    // double yMinD = 0.0f;
    // double yMaxD = 0.0f;
    // if ((ast.isAST3()) && ast.arg3().isList()) {
    // final IAST lsty = (IAST) ast.arg3();
    // if (lsty.isAST2()) {
    // final IExpr y0 = engine.evalN(lsty.arg1());
    // final IExpr y1 = engine.evalN(lsty.arg2());
    // if ((y0 instanceof INum) && (y1 instanceof INum)) {
    // yMinD = ((INum) y0).getRealPart();
    // yMaxD = ((INum) y1).getRealPart();
    // }
    // }
    // }

    final IAST list = functionOrListOfFunctions.makeList();
    int size = list.size();
    List<double[][]> dataList = new ArrayList<double[][]>(size - 1);
    final IASTAppendable listOfLines = F.ListAlloc(size - 1);
    double[] yMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
    for (int i = 1; i < size; i++) {
      IExpr function = list.get(i);
      double[][] data = null;
      final UnaryNumerical hun = new UnaryNumerical(function, x, engine);
      data = AdaptivePlot.computePlot(hun, data, xMinD, xMaxD);
      if (data != null) {
        dataList.add(data);
        automaticPlotRange(data[1], yMinMax);
      }
    }
    graphicsOptions.mergeOptions(graphicsOptions.options().getListOfRules(), yMinMax);
    for (int i = 0; i < dataList.size(); i++) {
      IExpr temp = plotLine(dataList.get(i), x, xMinD, xMaxD, yMinMax, graphicsOptions, engine);
      if (temp.isPresent()) {
        // line.append(temp);
        listOfLines.append(temp);
      }
    }
    return listOfLines;
  }

  /**
   * Plot a single data line.
   * 
   * @param data the function data points which should be plotted
   * @param xVar the variable symbol
   * @param xMin the minimum x-range value
   * @param xMax the maximum x-range value
   * @param yMinMax the y plot-range
   * @param graphicsOptions options context
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> is no conversion of the data into an <code>IExpr</code> was possible
   */
  private static IAST plotLine(double[][] data, final ISymbol xVar, final double xMin,
      final double xMax, double[] yMinMax, GraphicsOptions graphicsOptions,
      final EvalEngine engine) {

    graphicsOptions.setBoundingBoxScaled(new double[] {xMin, xMax, yMinMax[0], yMinMax[1]});
    final IASTAppendable listOfLines = F.ListAlloc();
    IASTAppendable lineList = F.NIL;
    double lastx = Double.NaN;
    double lasty = Double.NaN;
    for (int i = 0; i < data[0].length; i++) {
      if (!Double.isFinite(data[1][i])) {
        if (lineList.isPresent()) {
          listOfLines.append(lineList);
          lineList = F.NIL;
        }
        lastx = Double.NaN;
        lasty = Double.NaN;
        continue;
      }
      if (data[1][i] < yMinMax[0] //
          || data[1][i] > yMinMax[1]) {
        if (lineList.isPresent()) {
          lineList.append(graphicsOptions.point(data[0][i], data[1][i]));
          listOfLines.append(lineList);
          lineList = F.NIL;
          lastx = Double.NaN;
          lasty = Double.NaN;
        } else {
          lastx = data[0][i];
          lasty = data[1][i];
        }
        continue;
      }
      if (lineList.isNIL()) {
        lineList = F.ListAlloc();
        if (Double.isFinite(lastx) && Double.isFinite(lasty)) {
          lineList.append(graphicsOptions.point(lastx, lasty));
        }
      }
      lineList.append(graphicsOptions.point(data[0][i], data[1][i]));
    }
    if (lineList.isPresent()) {
      listOfLines.append(lineList);
      lineList = F.NIL;
    }
    return listOfLines;
  }

  protected void setGraphicOptions(GraphicsOptions graphicsOptions) {
    graphicsOptions.setXFunction(x -> x);
    graphicsOptions.setYFunction(y -> y);
    graphicsOptions.setJoined(true);
  }

  protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
    IAST listOfOptions = F.List(//
        F.Rule(S.Axes, S.True), //
        listPlotOptions.plotRange());
    return listOfOptions;
  }

  private static double[] automaticPlotRange(final double[] values, double[] yMinMax) {

    double thresh = 2.0;
    double[] yValues = new double[values.length];
    // for (int i = 0; i < values.length; i++) {
    // yValues[i] = values[1][i];
    // }
    System.arraycopy(values, 0, yValues, 0, values.length);
    Arrays.sort(yValues);
    double valavg = new Mean().evaluate(yValues);
    double valdev = new StandardDeviation().evaluate(yValues, valavg);

    int n1 = 0;
    int n2 = values.length - 1;
    if (valdev != 0) {
      for (double v : yValues) {
        if (Math.abs(v - valavg) / valdev < thresh) {
          break;
        }
        n1 += 1;
      }
      for (int i = yValues.length - 1; i >= 0; i--) {
        double v = yValues[i];
        if (Math.abs(v - valavg) / valdev < thresh) {
          break;
        }
        n2 -= 1;
      }
    }

    double vrange = yValues[n2] - yValues[n1];
    double vmin = yValues[n1] - 0.05 * vrange; // 5% extra looks nice
    double vmax = yValues[n2] + 0.05 * vrange;

    // double vrange = yValues[n2] - yValues[n1];
    // double vmin = yValues[n1]; // 5% extra looks nice
    if (vmin < yValues[0]) {
      vmin = yValues[0];
    } else if (vmin - vrange < yValues[0]) {
      vmin = yValues[0];
    } else if (vmin - vrange > yValues[0]) {
      vmin = vmin - vrange;
    }
    // double vmax = yValues[n2];
    if (vmax > yValues[yValues.length - 1]) {
      vmax = yValues[yValues.length - 1];
    } else if (vmax + vrange > yValues[yValues.length - 1]) {
      vmax = yValues[yValues.length - 1];
    } else if (vmax + vrange < yValues[yValues.length - 1]) {
      vmax = vmax + vrange;
    }
    if (vmin < yMinMax[0]) {
      yMinMax[0] = vmin;
    }
    if (vmax > yMinMax[1]) {
      yMinMax[1] = vmax;
    }
    return new double[] {vmin, vmax};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
  // @Override
  // public IExpr evaluate(final IAST ast, EvalEngine engine) {
  // if (Config.USE_MANIPULATE_JS) {
  // IExpr temp = S.Manipulate.of(engine, ast);
  // if (temp.headID() == ID.JSFormData) {
  // return temp;
  // }
  // return F.NIL;
  // }
  // if ((ast.size() >= 3) && (ast.size() <= 4) && ast.arg2().isList()) {
  // try {
  // final IAST rangeList = (IAST) ast.arg2();
  // if (rangeList.isList3()) {
  // if (!rangeList.arg1().isSymbol()) {
  // // `1` is not a valid variable.
  // return IOFunctions.printMessage(S.Plot, "ivar", F.list(rangeList.arg1()), engine);
  // }
  // final ISymbol x = (ISymbol) rangeList.arg1();
  // final IExpr xMin = engine.evalN(rangeList.arg2());
  // final IExpr xMax = engine.evalN(rangeList.arg3());
  // if ((!(xMin instanceof INum)) || (!(xMax instanceof INum))) {
  // return F.NIL;
  // }
  // final double xMinD = ((INum) xMin).getRealPart();
  // final double xMaxd = ((INum) xMax).getRealPart();
  // if (xMaxd <= xMinD) {
  // return F.NIL;
  // }
  // double yMinD = 0.0f;
  // double yMaxD = 0.0f;
  //
  // if ((ast.isAST3()) && ast.arg3().isList()) {
  // final IAST lsty = (IAST) ast.arg3();
  // if (lsty.isAST2()) {
  // final IExpr y0 = engine.evalN(lsty.arg1());
  // final IExpr y1 = engine.evalN(lsty.arg2());
  // if ((y0 instanceof INum) && (y1 instanceof INum)) {
  // yMinD = ((INum) y0).getRealPart();
  // yMaxD = ((INum) y1).getRealPart();
  // }
  // }
  // }
  // final IASTAppendable graphics = Graphics();
  // IASTAppendable line = Line();
  // IExpr temp;
  // Dimensions2D dim = new Dimensions2D();
  // final IAST list = ast.arg1().makeList();
  //
  // int size = list.size();
  // final IASTAppendable primitives = F.ListAlloc(size - 1);
  // for (int i = 1; i < size; i++) {
  // temp = plotLine(xMinD, xMaxd, yMinD, yMaxD, list.get(i), x, dim, engine);
  //
  // if (temp.isPresent()) {
  // line.append(temp);
  // primitives.append(line);
  // }
  // if (i < size - 1) {
  // line = Line();
  // }
  // }
  // graphics.append(primitives);
  //
  // IAST plotRange;
  // if (dim.isValidRange()) {
  // plotRange =
  // Rule(S.PlotRange, F.list(F.List(dim.xMin, dim.xMax), F.List(dim.yMin, dim.yMax)));
  // } else {
  // plotRange = Rule(S.PlotRange, S.Automatic);
  // }
  // final IExpr options[] =
  // {plotRange, Rule(S.AxesStyle, S.Automatic), Rule(S.AxesOrigin, F.list(F.C0, F.C0)),
  // Rule(S.Axes, S.True), Rule(S.Background, S.White)};
  // graphics.appendAll(F.function(S.List, options), 1, options.length);
  // return Show(graphics);
  // }
  // } catch (RuntimeException rex) {
  // LOGGER.debug("Plot.evaluate() failed", rex);
  // }
  // }
  // return F.NIL;
  // }

  /**
   * Calculates mean and standard deviation, throwing away all points which are more than 'thresh'
   * number of standard deviations away from the mean. These are then used to find good vmin and
   * vmax values. These values can then be used to find Automatic PlotRange.
   *
   * @param values of the y-axis
   * @return vmin and vmax value of the range
   */
  // private double[] automaticPlotRange(final double values[]) {
  //
  // double thresh = 2.0;
  // double[] yValues = new double[values.length];
  // System.arraycopy(values, 0, yValues, 0, values.length);
  // Arrays.sort(yValues);
  // double valavg = new Mean().evaluate(yValues);
  // double valdev = new StandardDeviation().evaluate(yValues, valavg);
  //
  // int n1 = 0;
  // int n2 = values.length - 1;
  // if (valdev != 0) {
  // for (double v : yValues) {
  // if (Math.abs(v - valavg) / valdev < thresh) {
  // break;
  // }
  // n1 += 1;
  // }
  // for (int i = yValues.length - 1; i >= 0; i--) {
  // double v = yValues[i];
  // if (Math.abs(v - valavg) / valdev < thresh) {
  // break;
  // }
  // n2 -= 1;
  // }
  // }
  //
  // double vrange = yValues[n2] - yValues[n1];
  // double vmin = yValues[n1] - 0.05 * vrange; // 5% extra looks nice
  // double vmax = yValues[n2] + 0.05 * vrange;
  // return new double[] {vmin, vmax};
  // }

  /**
   * @param xMin the minimum x-range value
   * @param xMax the maximum x-range value
   * @param yMin if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range
   *        and set yMin or yMax as plot result-range.
   * @param yMax if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range
   *        and set yMin or yMax as plot result-range.
   * @param function the function which should be plotted
   * @param xVar the variable symbol
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> is no conversion of the data into an <code>IExpr</code> was possible
   */
  // public IExpr plotLine(final double xMin, final double xMax, final double yMin, final double
  // yMax,
  // final IExpr function, final ISymbol xVar, Dimensions2D autoPlotRange,
  // final EvalEngine engine) {
  //
  // final double step = (xMax - xMin) / N;
  // double y;
  //
  // final UnaryNumerical hun = new UnaryNumerical(function, xVar, engine);
  // final double data[][] = new double[2][N + 1];
  // double x = xMin;
  //
  // for (int i = 0; i < N + 1; i++) {
  // y = hun.value(x);
  // if ((yMin != 0.0) || (yMax != 0.0)) {
  // if ((y >= yMin) && (y <= yMax)) {
  // data[0][i] = x;
  // data[1][i] = y;
  // } else {
  // if (y < yMin) {
  // data[0][i] = x;
  // data[1][i] = yMin;
  // } else {
  // data[0][i] = x;
  // data[1][i] = yMax;
  // }
  // }
  // } else {
  // data[0][i] = x;
  // data[1][i] = y;
  // }
  // x += step;
  // }
  // double[] vMinMax = automaticPlotRange(data[1]);
  // autoPlotRange.minMax(xMin, x, vMinMax[0], vMinMax[1]);
  // // autoPlotRange.append(F.List(xMin, vMinMax[0]));
  // // autoPlotRange.append(F.List(x, vMinMax[1]));
  // return Convert.toExprTransposed(data);
  // }
  //
  // @Override
  // public void setUp(final ISymbol newSymbol) {
  // newSymbol.setAttributes(ISymbol.HOLDALL);
  // }
}
