package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class DiscretePlot3D extends AbstractFunctionOptionEvaluator {

  private static class PlotData {
    double x, y, z;
    IExpr color;

    public PlotData(double x, double y, double z, IExpr color) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.color = color;
    }
  }

  public DiscretePlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    // DiscretePlot3D[f, {i, ...}, {j, ...}]
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr function = ast.arg1();
    IExpr iRange = ast.arg2();
    IExpr jRange = argSize >= 3 ? ast.arg3() : F.NIL;

    if (!iRange.isList() || iRange.argSize() < 2 || !iRange.first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.DiscretePlot3D, "pllim", F.list(iRange), engine);
    }
    if (jRange.isPresent()
        && (!jRange.isList() || jRange.argSize() < 2 || !jRange.first().isSymbol())) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.DiscretePlot3D, "pllim", F.list(jRange), engine);
    }

    // Parse Options
    //
    // Automatic draws a stem with a marker on top; None
    // asks for the markers alone; Full, All, Scaled and an explicit size give each point a box.
    //
    // Full and Scaled are measured in the spacing between neighbouring samples rather than in
    // data units. Resolving them against a spacing of one, as this used to, was only right when
    // the iterator happened to step by one: over {i,0,1,0.1} every bar came out ten times too
    // wide and swallowed its neighbours.
    boolean useThinLineStyle = false;
    boolean pointsOnly = false;
    double extentX = Double.NaN; // an absolute half width, or NaN to take it from the spacing
    double extentY = Double.NaN;
    double extentFractionX = 1.0;
    double extentFractionY = 1.0;

    IExpr extentOption = options[0]; // ExtentSize
    if (extentOption.isNone()) {
      pointsOnly = true;
    } else if (extentOption.equals(S.Automatic)) {
      useThinLineStyle = true;
    } else if (extentOption.equals(S.Full) || extentOption.equals(S.All)) {
      // the bars meet their neighbours, which is what the defaults above already say
    } else if (extentOption.isAST(S.Scaled, 2)) {
      IExpr scaled = extentOption.first();
      if (scaled.isList() && ((IAST) scaled).size() >= 3) {
        extentFractionX = toDouble(((IAST) scaled).arg1(), extentFractionX);
        extentFractionY = toDouble(((IAST) scaled).arg2(), extentFractionY);
      } else {
        extentFractionX = toDouble(scaled, extentFractionX);
        extentFractionY = extentFractionX;
      }
    } else if (extentOption.isNumber()) {
      double val = extentOption.evalfNaN();
      if (!Double.isNaN(val)) {
        extentX = val / 2.0;
        extentY = val / 2.0;
      }
    } else if (extentOption.isList() && ((IAST) extentOption).size() >= 3) {
      IExpr ex = ((IAST) extentOption).get(1);
      double exValue = ex.isNumber() ? ex.evalfNaN() : Double.NaN;
      if (!Double.isNaN(exValue))
        extentX = exValue / 2.0;
      IExpr ey = ((IAST) extentOption).get(2);
      double eyValue = ey.isNumber() ? ey.evalfNaN() : Double.NaN;
      if (!Double.isNaN(eyValue))
        extentY = eyValue / 2.0;
    }
    IExpr joined = options[X_JOINED];
    IExpr plotMarkers = options[X_PLOT_MARKERS];

    // Scaling Logic
    double zBase = 0.0;
    IExpr scalingOption = options[4]; // ScalingFunctions
    boolean isLogZ = false;

    if (scalingOption.isString() && "Log".equalsIgnoreCase(scalingOption.toString())) {
      isLogZ = true;
    } else if (scalingOption.isList() && ((IAST) scalingOption).size() >= 4) {
      IExpr zScale = ((IAST) scalingOption).get(3);
      if (zScale.isString() && "Log".equalsIgnoreCase(zScale.toString())) {
        isLogZ = true;
      }
    }

    if (isLogZ) {
      zBase = 1.0;
    }

    // Prepare iterators
    IASTAppendable graphicsList = F.ListAlloc();

    IAST functions;
    if (function.isList()) {
      functions = (IAST) function;
    } else {
      functions = F.List(function);
    }

    // Thd chart palette: the stems and markers take a slightly darkened palette entry and
    // the body of a bar a lightened, half transparent one, so overlapping bars stay readable
    IExpr plotStyle = options[X_PLOT_STYLE];
    IExpr[] defaultColors = new IExpr[functions.argSize()];
    IExpr[] faceColors = new IExpr[functions.argSize()];
    for (int c = 0; c < defaultColors.length; c++) {
      defaultColors[c] = Plot3DTools.chartStyle(c, plotStyle);
      faceColors[c] = Plot3DTools.chartFaceStyle(c, plotStyle);
    }

    try {
      List<INumber> iValues = parseIterator(iRange, engine);
      ISymbol iVar = (ISymbol) ((IAST) iRange).arg1();

      List<INumber> jValues = null;
      ISymbol jVar = null;
      if (jRange.isList()) {
        jValues = parseIterator(jRange, engine);
        jVar = (ISymbol) ((IAST) jRange).arg1();
      }

      if (Double.isNaN(extentX)) {
        extentX = extentFractionX * sampleSpacing(iValues) / 2.0;
      }
      if (Double.isNaN(extentY)) {
        extentY = extentFractionY * sampleSpacing(jValues == null ? iValues : jValues) / 2.0;
      }

      boolean autoPlotRange = options[1].equals(S.Automatic);

      for (int funcIdx = 1; funcIdx < functions.size(); funcIdx++) {
        IExpr f = functions.get(funcIdx);
        IASTAppendable primitives = F.ListAlloc();

        // Bars are drawn in the lightened, half transparent face colour; stems and markers in
        // the solid one. It is what lets a row of bars behind another row still be read.
        int colorIdx = (funcIdx - 1) % defaultColors.length;
        IExpr color = defaultColors[colorIdx];
        primitives.append(useThinLineStyle || pointsOnly ? color : faceColors[colorIdx]);
        if (!useThinLineStyle && !pointsOnly) {
          primitives.append(F.unaryAST1(S.EdgeForm, S.None));
        }

        List<PlotData> data = new ArrayList<>();

        // Generate data dynamically assigning iterators using Block to support HoldAll semantics
        for (INumber iv : iValues) {
          IAST iSet = F.Set(iVar, iv);

          if (jValues != null && jVar != null) {
            for (INumber jv : jValues) {
              IAST jSet = F.Set(jVar, jv);
              IAST blockVars = F.List(iSet, jSet);
              Plot3DTools.monitor(options[X_EVALUATION_MONITOR], engine);
              IExpr result = engine.evaluate(F.Block(blockVars, f));
              result = engine.evalN(result); // safely evaluate symbolic constants

              if (result.isNumber()) {
                double z = result.evalDouble();
                data.add(new PlotData(iv.evalDouble(), jv.evalDouble(), z, color));
              }
            }
          } else {
            // 1D iterator creating 3D sequence
            IAST blockVars = F.List(iSet);
            Plot3DTools.monitor(options[X_EVALUATION_MONITOR], engine);
            IExpr result = engine.evaluate(F.Block(blockVars, f));
            result = engine.evalN(result);

            if (result.isList() && ((IAST) result).size() >= 4) {
              double px = ((IAST) result).arg1().evalDouble();
              double py = ((IAST) result).arg2().evalDouble();
              double pz = ((IAST) result).arg3().evalDouble();
              data.add(new PlotData(px, py, pz, color));
            }
          }
        }

        // Apply Automatic PlotRange Clamping (Interquartile Range for extreme outliers)
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        // A sampled function needs its poles clamped away, because one asymptote flattens the
        // rest of the surface into the floor. Discrete data has no poles: every value is a
        // measurement, and the tall ones are usually the point of the plot. Clamping them here
        // truncated the peak of any probability mass function and left the bars standing through
        // the top of the box, so the range is simply the range of the data.
        if (autoPlotRange && !data.isEmpty()) {
          minZ = Double.MAX_VALUE;
          maxZ = -Double.MAX_VALUE;
          for (PlotData pd : data) {
            minZ = Math.min(minZ, pd.z);
            maxZ = Math.max(maxZ, pd.z);
          }
        }

        // Construct graphics layout
        List<PlotData> drawn = new ArrayList<>();
        for (PlotData pd : data) {
          double z = pd.z;
          if (autoPlotRange) {
            if (z > maxZ)
              z = maxZ;
            if (z < minZ)
              z = minZ;
          }
          if (isLogZ && z <= 0)
            continue;

          double x = pd.x;
          double y = pd.y;

          drawn.add(new PlotData(x, y, z, color));
          if (pointsOnly) {
            appendMarker(primitives, plotMarkers, colorIdx, x, y, z);
          } else if (useThinLineStyle) {
            // A stem with a marker on top. A stem is a line: drawing it as
            // a very thin cuboid instead gave a row of slivers that read as a barcode, and the
            // slivers had to be given a width in data units that means nothing to the reader.
            primitives.append(F.Line(F.List(F.List(F.num(x), F.num(y), F.num(zBase)),
                F.List(F.num(x), F.num(y), F.num(z)))));
            appendMarker(primitives, plotMarkers, colorIdx, x, y, z);
          } else {
            // Full Bar
            IExpr pMin = F.List(F.num(x - extentX), F.num(y - extentY), F.num(zBase));
            IExpr pMax = F.List(F.num(x + extentX), F.num(y + extentY), F.num(z));
            primitives.append(F.Cuboid(pMin, pMax));
          }
        }
        if (joined.isTrue()) {
          appendJoined(primitives, drawn, jValues != null);
        }
        graphicsList.append(primitives);
      }

    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      return Errors.printMessage(S.DiscretePlot3D, rex);
    }

    // this symbol is HOLDALL so its option values arrive unevaluated; a rule written straight
    // into the graphic would reach the renderer as an unevaluated expression
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.num(0.5))),
            F.Rule(S.ScalingFunctions, engine.evaluate(options[4])),
            F.Rule(S.PlotRange, engine.evaluate(options[1])),
            F.Rule(S.Axes, engine.evaluate(options[5]))});
  }

  private static double toDouble(IExpr expr, double fallback) {
    double value = expr.evalfNaN();
    return Double.isNaN(value) ? fallback : value;
  }

  /**
   * The step between neighbouring samples, which is the unit {@code ExtentSize} is measured in.
   *
   * <p>
   * The smallest gap rather than the average one, so that an unevenly sampled row still has bars
   * that do not overlap.
   */
  private static double sampleSpacing(List<INumber> values) {
    if (values == null || values.size() < 2) {
      return 1.0;
    }
    double smallest = Double.MAX_VALUE;
    for (int i = 1; i < values.size(); i++) {
      double gap = Math.abs(values.get(i).evalDouble() - values.get(i - 1).evalDouble());
      if (gap > 0.0 && gap < smallest) {
        smallest = gap;
      }
    }
    return smallest == Double.MAX_VALUE ? 1.0 : smallest;
  }

  /**
   * The marker that sits on top of a stem, as {@code PlotMarkers} asks for.
   *
   * <p>
   * A list is read as one marker per dataset, unless it is a pair whose second element is a number.
   */
  private static void appendMarker(IASTAppendable primitives, IExpr plotMarkers, int index,
      double x, double y, double z) {
    IAST position = F.List(F.num(x), F.num(y), F.num(z));
    IExpr marker = plotMarkers;
    if (marker.isList()) {
      IAST list = (IAST) marker;
      if (list.argSize() == 2 && list.arg2().isNumber()) {
        // {marker, size}: the size is in printer's points, which a fraction of the image is the
        // nearest thing to here
        double size = toDouble(list.arg2(), 0.0);
        if (size > 0.0) {
          primitives.append(F.unaryAST1(S.PointSize, F.num(Math.min(0.05, size / 400.0))));
        }
        marker = list.arg1();
      } else if (list.argSize() > 0) {
        marker = list.get(Math.floorMod(index, list.argSize()) + 1);
      }
    }
    if (marker.isNone()) {
      return;
    }
    if (marker.isString()) {
      primitives.append(F.binaryAST2(S.Text, marker, position));
      return;
    }
    primitives.append(F.Point(position));
  }

  /**
   * Joins the tops of the samples, as {@code Joined -> True} asks for.
   *
   * <p>
   * A grid is joined along both directions, so the tops read as a wireframe of the surface the
   * samples lie on; a plain sequence of points is joined in the order it was sampled. The points
   * are grouped by coordinate rather than by index, because a sample whose value did not evaluate
   * to a number never reached this list and would otherwise shift the whole row.
   */
  private static void appendJoined(IASTAppendable primitives, List<PlotData> data, boolean grid) {
    if (data.size() < 2) {
      return;
    }
    if (!grid) {
      IASTAppendable points = F.ListAlloc(data.size());
      for (PlotData pd : data) {
        points.append(F.List(F.num(pd.x), F.num(pd.y), F.num(pd.z)));
      }
      primitives.append(F.unaryAST1(S.Line, points));
      return;
    }
    appendLanes(primitives, data, true);
    appendLanes(primitives, data, false);
  }

  private static void appendLanes(IASTAppendable primitives, List<PlotData> data, boolean byX) {
    Map<Double, IASTAppendable> lanes = new LinkedHashMap<>();
    for (PlotData pd : data) {
      lanes.computeIfAbsent(byX ? pd.x : pd.y, key -> F.ListAlloc())
          .append(F.List(F.num(pd.x), F.num(pd.y), F.num(pd.z)));
    }
    for (IASTAppendable lane : lanes.values()) {
      if (lane.argSize() > 1) {
        primitives.append(F.unaryAST1(S.Line, lane));
      }
    }
  }

  private List<INumber> parseIterator(IExpr iter, EvalEngine engine) {
    List<INumber> values = new ArrayList<>();
    if (!iter.isList()) {
      return values;
    }
    IAST list = (IAST) iter;

    if (list.argSize() == 2) {
      IExpr arg2 = engine.evaluate(list.arg2());
      if (arg2.isList()) {
        IAST valList = (IAST) arg2;
        for (int k = 1; k < valList.size(); k++) {
          IExpr v = engine.evaluate(valList.get(k));
          if (v instanceof INumber) {
            values.add((INumber) v);
          }
        }
      } else {
        IInteger v = F.C1;
        if (arg2 instanceof INumber) {
          INumber max = (INumber) arg2;
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.inc();
          }
        }
      }
    } else if (list.argSize() >= 3) {
      IExpr arg2 = engine.evaluate(list.arg2());
      IExpr arg3 = engine.evaluate(list.arg3());
      if (arg2 instanceof INumber && arg3 instanceof INumber) {
        INumber min = (INumber) arg2;
        INumber max = (INumber) arg3;
        INumber step = F.C1;

        if (list.argSize() >= 4) {
          IExpr arg4 = engine.evaluate(list.arg4());
          if (arg4 instanceof INumber) {
            step = (INumber) arg4;
          }
        }

        INumber v = min;
        if (step.isNegative()) {
          while (max.lessThan(v).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        } else {
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        }
      }
    }
    return values;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  /**
   * The option table, built fresh each time so the positions can be looked up rather than counted.
   *
   * <p>
   * The first six keep the positions this evaluator has always read by index; because an option
   * this symbol does not declare stops the option scan and takes the options written before it down
   * with it.
   */
  private static GraphicsOptions.OptionSet optionSet() {
    return Plot3DTools.discreteExtras(new GraphicsOptions.OptionSet() //
        .add(
            new IBuiltInSymbol[] {S.ExtentSize, S.PlotRange, S.ColorFunction, S.PlotLegends,
                S.ScalingFunctions, S.Axes},
            new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.None, S.None, S.True}));
  }

  private static final int X_PLOT_STYLE = Plot3DTools.indexOf(optionSet(), S.PlotStyle);
  private static final int X_EVALUATION_MONITOR =
      Plot3DTools.indexOf(optionSet(), S.EvaluationMonitor);
  private static final int X_JOINED = Plot3DTools.indexOf(optionSet(), S.Joined);
  private static final int X_PLOT_MARKERS = Plot3DTools.indexOf(optionSet(), S.PlotMarkers);

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = optionSet();
    setOptions(newSymbol, options.keys(), options.values());
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
