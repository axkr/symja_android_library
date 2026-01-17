package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.GraphicsOptions;
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
      return Errors.printMessage(S.PolarPlot, "pllim", F.list(arg2), engine);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

    if (argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr function = ast.arg1();
    IAST rangeList = (IAST) ast.arg2();

    try {
      // Generate list of {{x1,y1}, {x2,y2}...} lists
      final IAST listOfLines =
          polarPlotToListPoints(function, rangeList, ast, graphicsOptions, engine);

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
          return createGraphicsFunction(graphicsPrimitives, listPlotOptions, ast);
        }
      }

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }

    return F.NIL;
  }

  private static IAST polarPlotToListPoints(IExpr functionOrListOfFunctions, final IAST rangeList,
      final IAST ast, GraphicsOptions graphicsOptions, EvalEngine engine) {
    if (!rangeList.arg1().isSymbol()) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(rangeList.arg1()), engine);
    }
    final ISymbol theta = (ISymbol) rangeList.arg1();
    final IExpr tMin = engine.evalN(rangeList.arg2());
    final IExpr tMax = engine.evalN(rangeList.arg3());
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

    for (int i = 1; i < size; i++) {
      IExpr function = list.get(i);
      double[][] data = null;
      // Use standard Plot sampler to get theta vs radius
      final UnaryNumerical hun = new UnaryNumerical(function, theta, engine);
      data =
          org.matheclipse.core.sympy.plotting.Plot.computePlot(hun, data, tMinD, tMaxD, "Linear");

      if (data != null) {
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
          }
        }
        if (linePoints.size() > 1) {
          listOfLines.append(linePoints);
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
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
