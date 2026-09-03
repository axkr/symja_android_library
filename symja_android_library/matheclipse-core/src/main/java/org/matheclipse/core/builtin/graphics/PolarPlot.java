package org.matheclipse.core.builtin.graphics;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots r(theta) functions */
public class PolarPlot extends Plot {
  /** Constructor for the singleton */
  public static final PolarPlot CONST = new PolarPlot();

  public PolarPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || !ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      IExpr arg2 = argSize >= 2 ? ast.arg2() : F.CEmptyString;
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.PolarPlot, "pllim", F.list(arg2), engine);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.funEval(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    if (argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine, originalAST);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    // a polar curve's colour function is given its angle and radius as well as its coordinates
    graphicsOptions.setColorFamily(PlotColorFunction.Family.POLAR_2D);
    graphicsOptions.applyPlotTheme(originalAST);
    IExpr function = ast.arg1();
    IAST rangeList = (IAST) ast.arg2();

    try {
      // Generate list of {{x1,y1}, {x2,y2}...} lists
      final IAST listOfLines = polarPlotToListPoints(function, rangeList, ast, graphicsOptions,
          engine,
          GraphicsOptions.optionValue(originalAST, S.PlotPoints, S.Automatic).toIntDefault(-1),
          GraphicsOptions.optionValue(originalAST, S.MaxRecursion, S.Automatic).toIntDefault(-1),
          GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic));

      if (listOfLines.isNIL()) {
        return F.NIL;
      }

      if (ToggleFeature.JS_ECHARTS) {
        return evaluateECharts(ast, argSize, options, engine, originalAST);
      } else {
        // Use ListPlot logic to render the lines with proper styles/options
        GraphicsOptions listPlotOptions = graphicsOptions.copy();
        IASTMutable listPlot = ast.setAtCopy(1, listOfLines);
        IAST graphicsPrimitives = plot(listPlot, options, listPlotOptions, engine);

        if (graphicsPrimitives.isPresent()) {
          // the polar scale is drawn first so that it sits behind the curve
          IAST polar = polarScale(GraphicsOptions.optionValue(originalAST, S.PolarAxes, S.False),
              GraphicsOptions.optionValue(originalAST, S.PolarGridLines, S.None),
              listPlotOptions.boundingBox());
          if (polar.isPresent()) {
            IASTAppendable withScale = F.ListAlloc(graphicsPrimitives.size() + 1);
            withScale.append(polar);
            withScale.appendArgs(graphicsPrimitives);
            graphicsPrimitives = withScale;
          }
          return createGraphicsFunction(graphicsPrimitives, listPlotOptions, ast);
        }
      }

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }

    return F.NIL;
  }

  /** How many radial spokes the polar grid gets when nothing asked for a particular number. */
  private static final int DEFAULT_SPOKES = 12;

  /**
   * The polar scale behind the curve: rings at round radii, spokes out from the centre, and the
   * radial axis with its distances written on it.
   *
   * @param polarAxes the {@code PolarAxes} value; {@code True} adds the radial axis and its labels
   * @param polarGridLines the {@code PolarGridLines} value; a number asks for that many spokes
   * @param boundingBox the plot extent, as {xMin, xMax, yMin, yMax}
   * @return the primitives, or {@link F#NIL} when neither option asked for anything
   */
  private static IAST polarScale(IExpr polarAxes, IExpr polarGridLines, double[] boundingBox) {
    boolean axes = polarAxes.isTrue() || polarAxes.isAutomatic();
    boolean grid = !polarGridLines.isNone() && !polarGridLines.isFalse();
    if (!axes && !grid) {
      return F.NIL;
    }
    double radius = 0;
    for (double edge : boundingBox) {
      if (Double.isFinite(edge)) {
        radius = Math.max(radius, Math.abs(edge));
      }
    }
    if (radius <= 0) {
      return F.NIL;
    }

    IASTAppendable out = F.ListAlloc(32);
    out.append(F.RGBColor(0.6, 0.6, 0.6));
    out.append(F.AbsoluteThickness(F.num(0.5)));

    java.util.List<org.matheclipse.core.graphics.svg.TickGenerator.Tick> ticks =
        org.matheclipse.core.graphics.svg.TickGenerator.linear(0, radius);
    if (grid) {
      for (org.matheclipse.core.graphics.svg.TickGenerator.Tick tick : ticks) {
        if (tick.major && tick.value > 0) {
          out.append(F.Circle(F.List(F.C0, F.C0), F.num(tick.value)));
        }
      }
      int spokes = polarGridLines.toIntDefault(-1);
      if (spokes < 2) {
        spokes = DEFAULT_SPOKES;
      }
      for (int i = 0; i < spokes; i++) {
        double angle = 2.0 * Math.PI * i / spokes;
        out.append(F.Line(F.List(F.List(F.C0, F.C0),
            F.List(F.num(radius * Math.cos(angle)), F.num(radius * Math.sin(angle))))));
      }
    }

    if (axes) {
      out.append(F.RGBColor(0.3, 0.3, 0.3));
      out.append(F.AbsoluteThickness(F.num(1.0)));
      out.append(F.Line(F.List(F.List(F.num(-radius), F.C0), F.List(F.num(radius), F.C0))));
      out.append(F.Line(F.List(F.List(F.C0, F.num(-radius)), F.List(F.C0, F.num(radius)))));
      out.append(S.Black);
      for (org.matheclipse.core.graphics.svg.TickGenerator.Tick tick : ticks) {
        if (tick.major && tick.value > 0) {
          // the distances go along the positive x axis, which is where a polar plot reads them
          out.append(
              F.Text(F.stringx(tick.label), F.List(F.num(tick.value), F.C0), F.List(F.C0, F.C1)));
        }
      }
    }
    return out;
  }

  private static IAST polarPlotToListPoints(IExpr functionOrListOfFunctions, final IAST rangeList,
      final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine, int plotPoints,
      int maxRecursion, IExpr regionFunction) {
    if (!rangeList.arg1().isSymbol()) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol theta = (ISymbol) rangeList.arg1();
    // a quantity valued range is stripped to magnitudes; the variable stays a plain number
    IAST thetaRange = QuantityFunctions.quantityPlotRange(engine.evaluate(rangeList.arg2()),
        engine.evaluate(rangeList.arg3()), GraphicsOptions.optionValue(ast, S.TargetUnits, S.Automatic),
        engine);
    final IExpr tMin = engine.evalN(thetaRange.isPresent() ? thetaRange.arg1() : rangeList.arg2());
    final IExpr tMax = engine.evalN(thetaRange.isPresent() ? thetaRange.arg2() : rangeList.arg3());
    if ((!(tMin instanceof INum)) || (!(tMax instanceof INum)) || tMin.equals(tMax)) {
      return Errors.printMessage(ast.topHead(), "plld", F.List(theta, rangeList), engine);
    }
    double tMinD = ((INum) tMin).getRealPart();
    double tMaxD = ((INum) tMax).getRealPart();
    if (tMaxD < tMinD) {
      double temp = tMinD;
      tMinD = tMaxD;
      tMaxD = temp;
    }

    final IAST list = functionOrListOfFunctions.makeList();
    int size = list.size();
    final IASTAppendable listOfLines = F.ListAlloc(size - 1);

    final IExpr targetUnits = GraphicsOptions.optionValue(ast, S.TargetUnits, S.Automatic);
    final RegionFunctionFilter region = RegionFunctionFilter.of(regionFunction, engine);
    final IAST samplePoint = F.List(F.Rule(theta, F.num((tMinD + tMaxD) / 2.0)));
    for (int i = 1; i < size; i++) {
      // a quantity valued radius is plotted by its magnitude
      IExpr function =
          QuantityFunctions.quantityPlotFunction(list.get(i), samplePoint, targetUnits, engine);
      double[][] data = null;
      // Use standard Plot sampler to get theta vs radius
      final UnaryNumerical hun = new UnaryNumerical(function, theta, Double.NaN, engine);
      // The region is tested inside the sampler, so the adaptive sampler - which refines wherever
      // it meets a value that is not a number - resolves the edge of the region. RegionFunction is
      // given the point both ways round: as the cartesian point that is drawn, and as the polar
      // coordinates it was computed from.
      DoubleUnaryOperator sampled = hun;
      if (region != null) {
        sampled = th -> {
          double r = hun.applyAsDouble(th);
          if (!Double.isFinite(r)) {
            return r;
          }
          return region.accepts(r * Math.cos(th), r * Math.sin(th), th, r) ? r : Double.NaN;
        };
      }
      data = org.matheclipse.core.sympy.plotting.Plot.computePlot(sampled, data, tMinD, tMaxD,
          "Linear", plotPoints, maxRecursion);

      if (data != null) {
        // one curve is a list of the polylines it is made of, which is how a curve broken by a
        // region or a pole keeps a single colour instead of being read as several curves
        IASTAppendable segments = F.ListAlloc(4);
        IASTAppendable linePoints = F.ListAlloc(data[0].length);
        double[] xData = data[0]; // Theta
        double[] yData = data[1]; // Radius (r)

        for (int k = 0; k < xData.length; k++) {
          double th = xData[k];
          double r = yData[k];
          if (Double.isFinite(r)) {
            double x = r * Math.cos(th);
            double y = r * Math.sin(th);
            linePoints.append(graphicsOptions.point(x, y));
          } else {
            // the curve is broken where it has no value rather than jumped across, which is what
            // leaves a gap where a RegionFunction ends instead of a chord across it
            if (linePoints.argSize() > 1) {
              segments.append(linePoints);
            }
            linePoints = F.ListAlloc(data[0].length);
          }
        }
        if (linePoints.argSize() > 1) {
          segments.append(linePoints);
        }
        if (segments.argSize() > 0) {
          listOfLines.append(segments);
        }
      }
    }
    return listOfLines;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // // Set default AspectRatio to Automatic to preserve geometric shapes (circles)
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.polarExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
