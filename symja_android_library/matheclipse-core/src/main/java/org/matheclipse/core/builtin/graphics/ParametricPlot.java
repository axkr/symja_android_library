package org.matheclipse.core.builtin.graphics;

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

  // Sampling resolution
  private final static int STEPS = 1200;

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
    IAST rangeList = (IAST) ast.arg2();

    try {
      final IAST listOfLines =
          parametricPlotToListPoints(function, rangeList, ast, graphicsOptions, engine);

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

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
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

    // Normalize input to a list of curve specifications: {{fx, fy}, {gx, gy}...}
    IAST curveList;
    if (functionOrListOfFunctions.isList()) {
      IAST list = (IAST) functionOrListOfFunctions;
      // Check if it is {fx, fy} (single curve) or {{fx, fy}, ...} (multiple)
      // Heuristic: if first element is not a list, assume single curve {x, y}
      if (list.size() > 1 && !list.arg1().isList()) {
        curveList = F.List(list);
      } else {
        curveList = list;
      }
    } else {
      // Fallback, though ParametricPlot usually requires a list {x,y}
      return F.NIL;
    }

    final IASTAppendable listOfLines = F.ListAlloc(curveList.size());

    for (IExpr curveSpec : curveList) {
      if (!curveSpec.isList() || ((IAST) curveSpec).size() < 3)
        continue; // Expecting {x, y}
      IExpr fx = ((IAST) curveSpec).arg1();
      IExpr fy = ((IAST) curveSpec).arg2();

      IASTAppendable linePoints = F.ListAlloc(STEPS);

      for (int i = 0; i <= STEPS; i++) {
        double t = tMinD + i * step;
        // Substitute t and evaluate
        IExpr tVal = F.num(t);
        IExpr xExpr = fx.replaceAll(F.Rule(tSym, tVal));
        IExpr yExpr = fy.replaceAll(F.Rule(tSym, tVal));

        try {
          double x = engine.evalDouble(xExpr);
          double y = engine.evalDouble(yExpr);
          if (Double.isFinite(x) && Double.isFinite(y)) {
            linePoints.append(graphicsOptions.point(x, y));
          }
        } catch (RuntimeException e) {
          // Ignore evaluation errors at specific points
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
    // Set default AspectRatio to Automatic to preserve geometric shapes
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
