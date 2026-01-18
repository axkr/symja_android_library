package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
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
      return Errors.printMessage(S.ParametricPlot, "pllim", F.list(arg2), engine);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    if (argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr function = ast.arg1();
    IAST rangeList1 = (IAST) ast.arg2();

    try {
      // Check for Region specification: {fx, fy}, {u, umin, umax}, {v, vmin, vmax}
      boolean isRegion = argSize >= 3 && ast.arg3().isList3() && ast.arg3().first().isSymbol();

      if (isRegion) {
        IAST rangeList2 = (IAST) ast.arg3();

        // Generate GraphicsComplex for the region
        IExpr graphicsComplex = parametricRegionToGraphicsComplex(function, rangeList1, rangeList2,
            graphicsOptions, engine);

        if (graphicsComplex.isNIL()) {
          return F.NIL;
        }

        // Region Rendering Logic:
        // 1. Get Color (Default to first plot style or 0)
        IExpr plotStyle = options[GraphicsOptions.X_PLOTSTYLE];
        IExpr color;
        if (plotStyle.isNone()) {
          color = GraphicsOptions.plotStyleColorExpr(0, F.NIL);
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
        IAST listOfLines =
            parametricPlotToListPoints(function, rangeList1, ast, graphicsOptions, engine);

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
      final IAST rangeU, final IAST rangeV, GraphicsOptions graphicsOptions, EvalEngine engine) {

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

    int counter = 0;
    for (int i = 0; i <= steps; i++) {
      double u = uMin + i * uStep;
      for (int j = 0; j <= steps; j++) {
        double v = vMin + j * vStep;
        rawGrid[counter++] = evalPoint(fx, fy, uSym, vSym, u, v, graphicsOptions, engine);
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
      GraphicsOptions graphicsOptions, EvalEngine engine) {
    try {
      // Use F.subst for clean substitution [cite: 2025-12-05]
      IAST rules = F.List(F.Rule(uSym, F.num(u)), F.Rule(vSym, F.num(v)));
      IExpr xExpr = F.subst(fx, rules);
      IExpr yExpr = F.subst(fy, rules);

      double x = engine.evalDouble(xExpr);
      double y = engine.evalDouble(yExpr);

      if (Double.isFinite(x) && Double.isFinite(y)) {
        return F.List(F.num(x), F.num(y));
      }
    } catch (RuntimeException e) {
      // Ignore evaluation errors (singularities)
    }
    return F.NIL;
  }

  private static IAST parametricPlotToListPoints(IExpr functionOrListOfFunctions,
      final IAST rangeList, final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (!rangeList.arg1().isSymbol()) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol tSym = (ISymbol) rangeList.arg1();
    final IExpr tMin = engine.evalN(rangeList.arg2());
    final IExpr tMax = engine.evalN(rangeList.arg3());
    if ((!(tMin instanceof INum)) || (!(tMax instanceof INum)) || tMin.equals(tMax)) {
      return Errors.printMessage(ast.topHead(), "plld", F.List(tSym, rangeList), engine);
    }
    double tMinD = ((INum) tMin).getRealPart();
    double tMaxD = ((INum) tMax).getRealPart();
    double step = (tMaxD - tMinD) / STEPS;

    IAST curveList;
    if (functionOrListOfFunctions.isList()) {
      IAST list = (IAST) functionOrListOfFunctions;
      if (list.size() > 1 && !list.arg1().isList()) {
        curveList = F.List(list);
      } else {
        curveList = list;
      }
    } else {
      return F.NIL;
    }

    final IASTAppendable listOfLines = F.ListAlloc(curveList.size());

    for (IExpr curveSpec : curveList) {
      if (!curveSpec.isList() || ((IAST) curveSpec).size() < 3)
        continue;
      IExpr fx = ((IAST) curveSpec).arg1();
      IExpr fy = ((IAST) curveSpec).arg2();

      IASTAppendable linePoints = F.ListAlloc(STEPS);

      for (int i = 0; i <= STEPS; i++) {
        double t = tMinD + i * step;
        IExpr tVal = F.num(t);
        IExpr xExpr = F.subst(fx, F.Rule(tSym, tVal));
        IExpr yExpr = F.subst(fy, F.Rule(tSym, tVal));

        try {
          double x = engine.evalDouble(xExpr);
          double y = engine.evalDouble(yExpr);
          if (Double.isFinite(x) && Double.isFinite(y)) {
            linePoints.append(graphicsOptions.point(x, y));
          }
        } catch (RuntimeException e) {
          // Ignore
        }
      }
      if (linePoints.size() > 1) {
        listOfLines.append(linePoints);
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
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}