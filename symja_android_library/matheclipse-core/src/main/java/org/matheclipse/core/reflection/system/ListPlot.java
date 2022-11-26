package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Rule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/** Plot a list of Points */
public class ListPlot extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Constructor for the singleton */
  // public final static ListPlot CONST = new ListPlot();

  public ListPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // if (Config.USE_MANIPULATE_JS) {
    // IExpr temp = S.Manipulate.of(engine, ast);
    // if (temp.headID() == ID.JSFormData) {
    // return temp;
    // }
    // return F.NIL;
    // }

    IExpr arg1 = ast.arg1().normal(false);
    if (arg1.isList()) {
      try {

        final IASTAppendable graphicsPrimitives = F.ListAlloc();
        final IASTAppendable graphics = Graphics(graphicsPrimitives);
        double[] minMax =
            new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};

        IASTAppendable[] pointSets = pointsOfListPlot(ast, minMax);
        if (pointSets != null) {
          graphicsPrimitives.append(F.PointSize(GraphicsFunctions.DEFAULT_POINTSIZE));
          for (int i = 0; i < pointSets.length; i++) {
            IASTAppendable points = pointSets[i];
            if (points.isPresent()) {
              graphicsPrimitives.append(F.Point(points));

              IAST plotRange = Rule(S.PlotRange,
                  F.list(F.List(minMax[0], minMax[1]), F.List(minMax[2], minMax[3])));

              final IExpr options[] = {plotRange, Rule(S.Axes, S.True)};
              graphics.appendAll(F.List(options), 1, options.length + 1);
              return graphics;
            }
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("ListPlot.evaluate() failed", rex);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  public static IASTAppendable[] pointsOfListPlot(final IAST ast, double[] minMax) {
    IExpr arg1 = ast.arg1();
    if (arg1.isVector() > 0) {
      double[] rowPoints = arg1.toDoubleVectorIgnore();
      if (rowPoints != null && rowPoints.length > 0) {
        IASTAppendable points = createPointsArray(rowPoints, minMax);
        return new IASTAppendable[] {points};
      }
    } else {
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        return pointsOfMatrix(list, minMax);
      }
    }
    return null;
  }

  public static IASTAppendable[] pointsOfMatrix(IAST tensor, double[] minMax) {
    IntArrayList dimensions = LinearAlgebra.dimensions(tensor);
    if (dimensions.size() == 3 && dimensions.getInt(2) == 2) {
      IASTAppendable[] result = new IASTAppendable[tensor.argSize()];
      for (int i = 1; i < tensor.size(); i++) {
        result[i - 1] = listPlotMatrix(tensor.get(i), minMax);
      }
      return result;
    }
    if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
      // matrix n X 2
      IASTAppendable points = listPlotMatrix(tensor, minMax);
      return new IASTAppendable[] {points};
    }
    if (tensor.isListOfLists()) {
      IASTAppendable[] result = new IASTAppendable[tensor.argSize()];
      for (int i = 1; i < tensor.size(); i++) {
        double[] rowPoints = tensor.get(i).toDoubleVectorIgnore();
        if (rowPoints != null && rowPoints.length > 0) {
          IASTAppendable points = createPointsArray(rowPoints, minMax);
          result[i - 1] = points;
        } else {
          result[i - 1] = F.NIL;
        }
      }
      return result;
    }
    return null;
  }

  private static IASTAppendable createPointsArray(double[] allPoints, double[] minMax) {
    IASTAppendable points = F.NIL;
    if (0.0 < minMax[0]) {
      minMax[0] = 0.0;
    }
    if (allPoints.length > minMax[1]) {
      minMax[1] = allPoints.length;
    }
    if (0.0 < minMax[2]) {
      minMax[2] = 0.0;
    }

    points = F.ast(S.List, allPoints.length);

    for (int i = 0; i < allPoints.length; i++) {
      if (allPoints[i] > minMax[3]) {
        minMax[3] = allPoints[i];
      } else if (allPoints[i] < minMax[2]) {
        minMax[2] = allPoints[i];
      }
      points.append(F.list(F.num(i + 1), F.num(allPoints[i])));
    }
    return points;
  }

  private static IASTAppendable listPlotMatrix(IExpr arg1, double[] minMax) {
    double[][] allPoints = arg1.toDoubleMatrix();
    return listPlotMatrix(allPoints, minMax);
  }

  private static IASTAppendable listPlotMatrix(double[][] allPoints, double[] minMax) {
    IASTAppendable points = F.NIL;
    if (allPoints != null && allPoints.length > 0) {
      points = F.ListAlloc(allPoints.length);

      for (int i = 0; i < allPoints.length; i++) {
        for (int j = 0; j < allPoints[i].length; j++) {
          if (allPoints[i][j] > minMax[1]) {
            minMax[1] = allPoints[i][0];
          } else if (allPoints[i][j] < minMax[0]) {
            minMax[0] = allPoints[i][0];
          }
          if (allPoints[i][j] > minMax[3]) {
            minMax[3] = allPoints[i][1];
          } else if (allPoints[i][j] < minMax[2]) {
            minMax[2] = allPoints[i][1];
          }
        }
        points.append(F.list(F.num(allPoints[i][0]), F.num(allPoints[i][1])));
      }
    }
    return points;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
