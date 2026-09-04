package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots {x(t), y(t)} functions */
public class ParametricPlot extends Plot {
  /** Constructor for the singleton */
  public static final ParametricPlot CONST = new ParametricPlot();

  // Sampling resolution for curves
  private final static int STEPS = 1200;

  // Sampling resolution for regions (Grid size 40x40 = 1600 polygons)
  private final static int REGION_STEPS = 40;

  public ParametricPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || !ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
      IExpr arg2 = argSize >= 2 ? ast.arg2() : F.CEmptyString;
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.ParametricPlot, "pllim", F.list(arg2), engine);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.funEval(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    if (argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine, originalAST);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    // a parametric curve's colour function is given the parameter alongside the coordinates
    graphicsOptions.setColorFamily(PlotColorFunction.Family.PARAMETRIC_2D);
    graphicsOptions.applyPlotTheme(originalAST);
    IExpr function = ast.arg1();
    IAST rangeList1 = (IAST) ast.arg2();

    try {
      // Check for Region specification: {fx, fy}, {u, umin, umax}, {v, vmin, vmax}
      boolean isRegion = argSize >= 3 && ast.arg3().isList3() && ast.arg3().first().isSymbol();

      if (isRegion) {
        IAST rangeList2 = (IAST) ast.arg3();

        // Generate GraphicsComplex for the region
        IExpr graphicsComplex = parametricRegionToGraphicsComplex(function, rangeList1, rangeList2,
            graphicsOptions, engine,
            GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic));

        if (graphicsComplex.isNIL()) {
          return F.NIL;
        }

        // Region Rendering Logic:
        // 1. Get Color (Default to first plot style or 0)
        IExpr plotStyle = options[GraphicsOptions.X_PLOTSTYLE];
        IExpr color;
        if (plotStyle.isNone()) {
          color = GraphicsOptions.plotStyleDirective(0, F.NIL, graphicsOptions.curveThickness());
        } else {
          color = GraphicsOptions.getPlotStyle(plotStyle, 0);
        }

        // 2. Construct Graphics Primitive: {Color, EdgeForm[None], GraphicsComplex[...]}
        // EdgeForm(None) is crucial to suppress the wireframe mesh lines.
        IAST graphicsPrimitives = F.List(color, F.EdgeForm(S.None), graphicsComplex);

        // 3. Return Graphics object
        return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);

      } else {
        // Standard Parametric Curve (1 parameter)
        IAST listOfLines = parametricPlotToListPoints(function, rangeList1, ast, graphicsOptions,
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
          GraphicsOptions listPlotOptions = graphicsOptions.copy();
          IASTMutable listPlot = ast.setAtCopy(1, listOfLines);
          IAST graphicsPrimitives = plot(listPlot, options, listPlotOptions, engine);

          if (graphicsPrimitives.isPresent()) {
            return createGraphicsFunction(graphicsPrimitives, listPlotOptions, ast);
          }
        }
      }

    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      Errors.rethrowsInterruptException(rex);
    }

    return F.NIL;
  }

  /**
   * Generates a GraphicsComplex primitive for a parametric region. Structure: GraphicsComplex[{pt1,
   * pt2, ...}, Polygon[{{id1, id2, id3, id4}, ...}]]
   */
  private static IExpr parametricRegionToGraphicsComplex(IExpr functionOrListOfFunctions,
      final IAST rangeU, final IAST rangeV, GraphicsOptions graphicsOptions, EvalEngine engine,
      IExpr regionFunction) {

    // 1. Validate Ranges
    if (!rangeU.arg1().isSymbol() || !rangeV.arg1().isSymbol()) {
      return F.NIL;
    }
    final ISymbol uSym = (ISymbol) rangeU.arg1();
    final ISymbol vSym = (ISymbol) rangeV.arg1();

    final IExpr uMinExpr = engine.evalN(rangeU.arg2());
    final IExpr uMaxExpr = engine.evalN(rangeU.arg3());
    final IExpr vMinExpr = engine.evalN(rangeV.arg2());
    final IExpr vMaxExpr = engine.evalN(rangeV.arg3());

    if (!(uMinExpr instanceof INum) || !(uMaxExpr instanceof INum) || !(vMinExpr instanceof INum)
        || !(vMaxExpr instanceof INum)) {
      return F.NIL;
    }

    double uMin = ((INum) uMinExpr).getRealPart();
    double uMax = ((INum) uMaxExpr).getRealPart();
    double vMin = ((INum) vMinExpr).getRealPart();
    double vMax = ((INum) vMaxExpr).getRealPart();

    // 2. Normalize Functions
    // For simplicity, handling single {x, y} region.
    IExpr fx, fy;
    if (functionOrListOfFunctions.isList()) {
      IAST list = (IAST) functionOrListOfFunctions;
      if (list.size() > 1 && !list.arg1().isList()) {
        fx = list.arg1();
        fy = list.arg2();
      } else if (list.size() > 1 && list.arg1().isList()) {
        IAST first = (IAST) list.arg1();
        fx = first.arg1();
        fy = first.arg2();
      } else {
        return F.NIL;
      }
    } else {
      return F.NIL;
    }

    // 3. Generate Vertices and Index Map
    int steps = REGION_STEPS;
    double uStep = (uMax - uMin) / steps;
    double vStep = (vMax - vMin) / steps;

    // Store all generated points (including failures as null/NIL to maintain grid)
    IExpr[] rawGrid = new IExpr[(steps + 1) * (steps + 1)];
    int gridWidth = steps + 1;

    final RegionFunctionFilter region = RegionFunctionFilter.of(regionFunction, engine);
    int counter = 0;
    for (int i = 0; i <= steps; i++) {
      double u = uMin + i * uStep;
      for (int j = 0; j <= steps; j++) {
        double v = vMin + j * vStep;
        rawGrid[counter++] = evalPoint(fx, fy, uSym, vSym, u, v, region, graphicsOptions, engine);
      }
    }

    // 4. Compact Vertices and Build Index Mapping
    // GraphicsComplex indices refer to the position in the vertex list (1-based).
    // map[gridIndex] -> validVertexIndex (or 0 if invalid)
    int[] indexMap = new int[rawGrid.length];
    IASTAppendable vertexList = F.ListAlloc(rawGrid.length);
    int validCount = 0;

    for (int k = 0; k < rawGrid.length; k++) {
      if (rawGrid[k].isPresent()) {
        vertexList.append(rawGrid[k]);
        validCount++;
        indexMap[k] = validCount;
      } else {
        indexMap[k] = 0;
      }
    }

    if (validCount < 3)
      return F.NIL;

    // 5. Build Polygons using Mapped Indices
    IASTAppendable polyIndices = F.ListAlloc(steps * steps);

    for (int i = 0; i < steps; i++) {
      for (int j = 0; j < steps; j++) {
        // Grid indices for quad (counter-clockwise)
        // (i, j) -> (i+1, j) -> (i+1, j+1) -> (i, j+1)
        int k1 = i * gridWidth + j;
        int k2 = (i + 1) * gridWidth + j;
        int k3 = (i + 1) * gridWidth + (j + 1);
        int k4 = i * gridWidth + (j + 1);

        // Check if all 4 corners are valid
        if (indexMap[k1] > 0 && indexMap[k2] > 0 && indexMap[k3] > 0 && indexMap[k4] > 0) {
          polyIndices.append(F.List(F.ZZ(indexMap[k1]), F.ZZ(indexMap[k2]), F.ZZ(indexMap[k3]),
              F.ZZ(indexMap[k4])));
        }
      }
    }

    if (polyIndices.isEmpty()) {
      return F.NIL;
    }

    // Structure: GraphicsComplex[ {pt1, pt2...}, Polygon[ { {id1...}, {id2...} } ] ]
    return F.GraphicsComplex(vertexList, F.Polygon(polyIndices));
  }

  private static IExpr evalPoint(IExpr fx, IExpr fy, ISymbol uSym, ISymbol vSym, double u, double v,
      RegionFunctionFilter region, GraphicsOptions graphicsOptions, EvalEngine engine) {
    try {
      // Use F.subst for clean substitution
      IAST rules = F.List(F.Rule(uSym, F.num(u)), F.Rule(vSym, F.num(v)));
      IExpr xExpr = F.subst(fx, rules);
      IExpr yExpr = F.subst(fy, rules);

      double x = engine.evalDouble(xExpr);
      double y = engine.evalDouble(yExpr);

      if (Double.isFinite(x) && Double.isFinite(y)) {
        // a point the region rejects is left out the same way one the parametrisation has no
        // value at is: the quads that touch it are simply not built
        if (region != null && !region.accepts(x, y, u, v)) {
          return F.NIL;
        }
        return F.List(F.num(x), F.num(y));
      }
    } catch (RuntimeException e) {
      // Ignore evaluation errors (singularities)
    }
    return F.NIL;
  }

  private static IAST parametricPlotToListPoints(IExpr functionOrListOfFunctions,
      final IAST rangeList, final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine,
      int plotPoints, int maxRecursion, IExpr regionFunction) {
    if (!rangeList.arg1().isSymbol()) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol tSym = (ISymbol) rangeList.arg1();
    // a quantity valued range is stripped to magnitudes; the variable stays a plain number
    IAST tRange = QuantityFunctions.quantityPlotRange(engine.evaluate(rangeList.arg2()),
        engine.evaluate(rangeList.arg3()), GraphicsOptions.optionValue(ast, S.TargetUnits, S.Automatic),
        engine);
    final IExpr tMin = engine.evalN(tRange.isPresent() ? tRange.arg1() : rangeList.arg2());
    final IExpr tMax = engine.evalN(tRange.isPresent() ? tRange.arg2() : rangeList.arg3());
    if ((!(tMin instanceof INum)) || (!(tMax instanceof INum)) || tMin.equals(tMax)) {
      return Errors.printMessage(ast.topHead(), "plld", F.List(tSym, rangeList), engine);
    }
    double tMinD = ((INum) tMin).getRealPart();
    double tMaxD = ((INum) tMax).getRealPart();
    // an explicit PlotPoints replaces the fixed sweep resolution
    int steps = plotPoints;
    if (steps < 2) {
      steps = STEPS;
    }
    // This curve is swept at a fixed resolution rather than refined adaptively, so MaxRecursion
    // is approximated by sampling more finely -- a level doubles the number of steps.
    if (maxRecursion >= 1) {
      steps = (int) Math.min((long) steps << Math.min(maxRecursion, 4), 20000L);
    } else if (maxRecursion == 0) {
      // MaxRecursion -> 0 asks for no refinement at all
      steps = Math.max(2, steps / 8);
    }
    double step = (tMaxD - tMinD) / steps;
    // the parameter is scaled over the range its iterator declares, like every other argument
    graphicsOptions.setColorRange(3, tMinD, tMaxD);

    // a wrapper is taken off before the {fx, fy} shape is read, since Tooltip({Cos(t), Sin(t)})
    // is still one curve and not a collection of two
    final PlotWrapper outer = PlotWrapper.of(functionOrListOfFunctions);
    IAST curveList;
    if (outer.datum.isList()) {
      IAST list = (IAST) outer.datum;
      if (list.size() > 1 && !PlotWrapper.strip(list.arg1()).isList()) {
        curveList = F.List(outer.datum);
      } else {
        curveList = list;
      }
    } else {
      return F.NIL;
    }

    final IASTAppendable listOfLines = F.ListAlloc(curveList.size());
    final RegionFunctionFilter region = RegionFunctionFilter.of(regionFunction, engine);

    for (IExpr wrappedCurve : curveList) {
      PlotWrapper each = PlotWrapper.of(wrappedCurve);
      IExpr curveSpec = each.datum;
      IExpr curveTooltip = outer.tooltipOf(each);
      if (!curveSpec.isList() || ((IAST) curveSpec).size() < 3)
        continue;
      // quantity valued components are plotted by their magnitudes, one unit per axis
      final IAST samplePoint = F.List(F.Rule(tSym, F.num((tMinD + tMaxD) / 2.0)));
      IExpr targetUnits = GraphicsOptions.optionValue(ast, S.TargetUnits, S.Automatic);
      IExpr fx = QuantityFunctions.quantityPlotFunction(((IAST) curveSpec).arg1(), samplePoint,
          targetUnits, 1, 2, engine);
      IExpr fy = QuantityFunctions.quantityPlotFunction(((IAST) curveSpec).arg2(), samplePoint,
          targetUnits, 2, 2, engine);

      // one curve is a list of the polylines it is made of, which is how a curve broken by a
      // region or a pole keeps a single colour instead of being read as several curves
      IASTAppendable segments = F.ListAlloc(4);
      IASTAppendable linePoints = F.ListAlloc(steps);

      for (int i = 0; i <= steps; i++) {
        double t = tMinD + i * step;
        IExpr tVal = F.num(t);
        IExpr xExpr = F.subst(fx, F.Rule(tSym, tVal));
        IExpr yExpr = F.subst(fy, F.Rule(tSym, tVal));

        boolean drawn = false;
        try {
          double x = engine.evalDouble(xExpr);
          double y = engine.evalDouble(yExpr);
          if (Double.isFinite(x) && Double.isFinite(y)
              && (region == null || region.accepts(x, y, t))) {
            linePoints.append(graphicsOptions.parametricPoint(x, y, t));
            drawn = true;
          }
        } catch (RuntimeException e) {
          // Ignore
        }
        if (!drawn) {
          // the curve is broken where it has no value, or where the region ends, rather than
          // jumped across
          if (linePoints.argSize() > 1) {
            segments.append(linePoints);
          }
          linePoints = F.ListAlloc(steps);
        }
      }
      if (linePoints.argSize() > 1) {
        segments.append(linePoints);
      }
      if (segments.argSize() > 0) {
        listOfLines.append(
            curveTooltip.isPresent() ? F.binaryAST2(S.Tooltip, segments, curveTooltip) : segments);
      }
    }

    return listOfLines;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.functionPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
