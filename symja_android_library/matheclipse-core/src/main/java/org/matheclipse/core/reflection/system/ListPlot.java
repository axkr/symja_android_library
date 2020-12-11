package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Line;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;

import java.util.Arrays;

import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

/** Plot a list of Points */
public class ListPlot extends AbstractEvaluator {
  /** Constructor for the singleton */
  // public final static ListPlot CONST = new ListPlot();

  public ListPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.USE_MANIPULATE_JS) {
      IExpr temp = F.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    if ((ast.size() == 2) && ast.arg1().isList()) {
      try {
        double xMinD = Double.MAX_VALUE;
        double xMaxD = Double.MIN_VALUE;
        double yMinD = Double.MAX_VALUE;
        double yMaxD = Double.MIN_VALUE;
        final IASTAppendable graphics = Graphics();
        Dimensions2D dim = new Dimensions2D();
        IASTAppendable points = F.NIL;
        if (ast.arg1().isVector() > 0) {

          double[] allPoints = ast.arg1().toDoubleVector();
          if (allPoints != null && allPoints.length > 0) {
            xMaxD = 1.0;
            xMaxD = allPoints.length;
            points = F.ast(F.List, allPoints.length, false);

            for (int i = 0; i < allPoints.length; i++) {
              if (allPoints[i] > yMaxD) {
                yMaxD = allPoints[i];
              } else if (allPoints[i] < yMinD) {
                yMinD = allPoints[i];
              }
              points.append(F.List(F.num((double) i), F.num(allPoints[i])));
            }
          }
        } else {
          int[] matrixDim = ast.arg1().isMatrix();
          if (matrixDim != null && matrixDim[1] == 2) {

            double[][] allPoints = ast.arg1().toDoubleMatrix();
            if (allPoints != null && allPoints.length > 0) {
              xMaxD = allPoints.length;
              points = F.ast(F.List, allPoints.length, false);

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
                points.append(F.List(F.num((double) allPoints[i][0]), F.num(allPoints[i][1])));
              }
            }
          }
        }

        if (points.isPresent()) {
          graphics.append(F.Point(points));

          IAST plotRange;
          if (dim.isValidRange()) {
            plotRange =
                Rule(F.PlotRange, F.List(F.List(dim.xMin, dim.xMax), F.List(dim.yMin, dim.yMax)));
          } else {
            plotRange = Rule(F.PlotRange, F.Automatic);
          }
          final IExpr options[] = {
            plotRange,
            Rule(F.AxesStyle, F.Automatic),
            Rule(F.AxesOrigin, List(F.C0, F.C0)),
            Rule(F.Axes, F.True),
            Rule(F.Background, F.White)
          };
          graphics.appendAll(F.function(F.List, options), 1, options.length);
          return Show(graphics);
        }

      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
