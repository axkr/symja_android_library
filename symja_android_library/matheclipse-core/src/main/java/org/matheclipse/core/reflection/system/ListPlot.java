package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.Dimensions2D;
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
    if (Config.USE_MANIPULATE_JS) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    if ((ast.size() == 2) && ast.arg1().isList()) {
      try {
        final IASTAppendable graphics = Graphics();
        Dimensions2D dim = new Dimensions2D();
        IASTAppendable[] pointSets = pointsOfListPlot(ast);
        if (pointSets != null) {
          for (int i = 0; i < pointSets.length; i++) {
            IASTAppendable points = pointSets[i];
            if (points.isPresent()) {
              graphics.append(F.Point(points));

              IAST plotRange;
              if (dim.isValidRange()) {
                plotRange = Rule(S.PlotRange,
                    F.list(F.List(dim.xMin, dim.xMax), F.List(dim.yMin, dim.yMax)));
              } else {
                plotRange = Rule(S.PlotRange, S.Automatic);
              }
              final IExpr options[] =
                  {plotRange, Rule(S.AxesStyle, S.Automatic), Rule(S.AxesOrigin, List(F.C0, F.C0)),
                      Rule(S.Axes, S.True), Rule(S.Background, S.White)};
              graphics.appendAll(F.function(S.List, options), 1, options.length);
              return Show(graphics);
            }
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("ListPlot.evaluate() failed", rex);
      }
    }
    return F.NIL;
  }

  public static IASTAppendable[] pointsOfListPlot(final IAST ast) {


    IExpr arg1 = ast.arg1();
    if (arg1.isVector() > 0) {
      double[] rowPoints = arg1.toDoubleVectorIgnore();
      if (rowPoints != null && rowPoints.length > 0) {
        IASTAppendable points = createPointsArray(rowPoints);
        return new IASTAppendable[] {points};
      }
    } else {
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        IntArrayList dimensions = LinearAlgebra.dimensions(list);
        if (dimensions.size() == 3 && dimensions.getInt(2) == 2) {
          IASTAppendable[] result = new IASTAppendable[list.argSize()];
          for (int i = 1; i < list.size(); i++) {
            result[i - 1] = listPlotMatrix(list.get(i));
          }
          return result;
        }
        if (dimensions.size() == 2 && dimensions.getInt(1) == 2) {
          // matrix n X 2
          IASTAppendable points = listPlotMatrix(arg1);
          return new IASTAppendable[] {points};
        }
        if (arg1.isListOfLists()) {
          IASTAppendable[] result = new IASTAppendable[list.argSize()];
          for (int i = 1; i < list.size(); i++) {
            double[] rowPoints = list.get(i).toDoubleVectorIgnore();
            if (rowPoints != null && rowPoints.length > 0) {
              IASTAppendable points = createPointsArray(rowPoints);
              result[i - 1] = points;
            } else {
              result[i - 1] = F.NIL;
            }
          }
          return result;
        }
      }
    }
    return null;
  }

  private static IASTAppendable createPointsArray(double[] allPoints) {
    IASTAppendable points = F.NIL;
    double xMinD = Double.MAX_VALUE;
    double xMaxD = Double.MIN_VALUE;
    double yMinD = Double.MAX_VALUE;
    double yMaxD = Double.MIN_VALUE;
    xMaxD = 1.0;
    xMaxD = allPoints.length;
    points = F.ast(S.List, allPoints.length);

    for (int i = 0; i < allPoints.length; i++) {
      if (allPoints[i] > yMaxD) {
        yMaxD = allPoints[i];
      } else if (allPoints[i] < yMinD) {
        yMinD = allPoints[i];
      }
      points.append(F.list(F.num(i + 1), F.num(allPoints[i])));
    }
    return points;
  }

  private static IASTAppendable listPlotMatrix(IExpr arg1) {
    double xMinD = Double.MAX_VALUE;
    double xMaxD = Double.MIN_VALUE;
    double yMinD = Double.MAX_VALUE;
    double yMaxD = Double.MIN_VALUE;
    IASTAppendable points = F.NIL;
    double[][] allPoints = arg1.toDoubleMatrix();
    if (allPoints != null && allPoints.length > 0) {
      xMaxD = allPoints.length;
      points = F.ListAlloc(allPoints.length);

      for (int i = 0; i < allPoints.length; i++) {
        for (int j = 0; j < allPoints[i].length; j++) {
          if (allPoints[i][j] > xMaxD) {
            xMaxD = allPoints[i][0];
          } else if (allPoints[i][j] < xMinD) {
            xMinD = allPoints[i][0];
          }
          if (allPoints[i][j] > yMaxD) {
            yMaxD = allPoints[i][1];
          } else if (allPoints[i][j] < yMinD) {
            yMinD = allPoints[i][1];
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
