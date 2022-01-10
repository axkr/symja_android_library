package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot an array of coordinates/heights in 3 dimensions */
public class ListPlot3D extends AbstractEvaluator {
  public ListPlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() > 0) {
      // if (ast.argSize() > 1) {
      // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      // IExpr colorFunction = options.getOption(S.ColorFunction);
      // if (colorFunction.isPresent()) {
      // // ... color function is set...
      // }
      // }
      int[] dimension = ast.arg1().isMatrix(false);
      if (dimension != null && dimension.length == 2) {
        // convert possible sparse array expression:
        IAST values = (IAST) ast.arg1().normal(false);

        if (dimension[0] == 3 && dimension[1] == 3) {
          // Draw a triangle with x, y and z coordinates.
          IAST polygons = F.Polygon(F.List(F.List(//
              ((IAST) values.arg1()).arg1(), ((IAST) values.arg2()).arg1(),
              ((IAST) values.arg3()).arg1()),
              F.List(//
                  ((IAST) values.arg1()).arg2(), ((IAST) values.arg2()).arg2(),
                  ((IAST) values.arg3()).arg2()),
              F.List(//
                  ((IAST) values.arg1()).arg3(), ((IAST) values.arg2()).arg3(),
                  ((IAST) values.arg3()).arg3())));
          IASTAppendable result = F.Graphics3D(polygons);
          if (ast.argSize() > 1) {
            // add same options to Graphics3D
            result.appendAll(ast, 2, ast.size());
          }
          return result;
        } else if (dimension[1] == 4) {
          // Draw polygons given just the z value in a grid of size
          // (heights.argSize() - 1) * (heights.arg1().argSize() - 1)
          // 2 polygons per square
          IASTAppendable polygonList =
              F.ListAlloc((values.argSize() - 1) * (((IAST) values).arg1().argSize() - 1) * 2);

          double minHeight = (((IAST) values.arg1()).arg1()).evalDouble();
          double maxHeight = minHeight;

          ArrayList<double[]> heightRows = new ArrayList<double[]>();
          for (int i = 1; i <= values.argSize(); i++) {
            IAST row = ((IAST) values.get(i));
            try {
              heightRows.add(new double[0]);
              double[] heights = new double[row.argSize()];
              for (int j = 1; j <= row.argSize(); j++) {
                // evalDouble may throw ArgumentTypeException
                heights[j - 1] = row.get(j).evalDouble();
                double height = heights[j - 1];
                if (height < minHeight)
                  minHeight = height;
                if (height > maxHeight)
                  maxHeight = height;
              }
              heightRows.set(i - 1, heights);
            } catch (ArgumentTypeException ate) {
              // ignore this row
            }
          }

          // deltaHeight is used to normalize the heights.
          double deltaHeight = maxHeight - minHeight;

          // As i and j start at 0, the last element is ignored (as it should be).
          for (int i = 0; i < heightRows.size() - 1; i++) {
            double[] heights = heightRows.get(i);
            double[] nextHeights = heightRows.get(i + 1);
            if (heights.length > 0 && nextHeights.length == heights.length) {
              for (int j = 0; j < heights.length - 1; j++) {
                polygonList.append(F.Polygon(F.List(//
                    F.List(F.num(i + 1), F.num(j + 1), F.num(heights[j] / deltaHeight)),
                    F.List(F.num(i + 2), F.num(j + 2), F.num(nextHeights[j + 1] / deltaHeight)),
                    F.List(F.num(i + 1), F.num(j + 2), F.num(heights[j + 1] / deltaHeight)))));
                polygonList.append(F.Polygon(
                    F.List(F.List(F.num(i + 1), F.num(j + 1), F.num(heights[j] / deltaHeight)),
                        F.List(F.num(i + 2), F.num(j + 2), F.num(nextHeights[j + 1] / deltaHeight)),
                        F.List(F.num(i + 2), F.num(j + 1), F.num(nextHeights[j] / deltaHeight)))));
              }
            }
          }
          IASTAppendable result = F.Graphics3D(polygonList);
          if (ast.argSize() > 1) {
            // add same options to Graphics3D
            result.appendAll(ast, 2, ast.size());
          }
          return result;
        }

      }
    }
    return F.NIL;

  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
