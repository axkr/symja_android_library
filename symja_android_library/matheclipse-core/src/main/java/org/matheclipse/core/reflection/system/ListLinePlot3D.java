package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Lines in 3 dimensions */
public class ListLinePlot3D extends AbstractEvaluator {
  public ListLinePlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() > 0) {

      IAST plotStyle = F.NIL;
      if (ast.argSize() > 1) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        IExpr temp = options.getOption(S.PlotStyle);
        if (temp.isAST()) {
          plotStyle = (IAST) temp;
        }
      }
      // the color number with which the line will be printed
      int[] lineColorNumber = new int[] {1};
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        int[] dimension = list.isMatrix(false);
        if (dimension != null && dimension.length == 2) {
          IExpr temp = matrixLinePlot(list, dimension, plotStyle, lineColorNumber, engine);
          if (temp.isPresent()) {
            IASTAppendable result = F.Graphics3D(temp);
            if (ast.argSize() > 1) {
              // add same options to Graphics3D
              result.appendAll(ast, 2, ast.size());
            }
            return result;
          }
          return F.NIL;
        }
        IASTAppendable resultList = F.ListAlloc(list.size());
        for (int i = 1; i < list.size(); i++) {
          int[] dims = list.get(i).isMatrix(false);
          if (dims != null && dims.length == 2) {
            IExpr temp =
                matrixLinePlot((IAST) list.get(i), dims, plotStyle, lineColorNumber, engine);
            if (temp.isPresent()) {
              resultList.append(temp);
            }
          }
        }
        IASTAppendable result = F.Graphics3D(resultList);
        if (ast.argSize() > 1) {
          // add same options to Graphics3D
          result.appendAll(ast, 2, ast.size());
        }
        return result;
      }

    }
    return F.NIL;

  }

  private IExpr matrixLinePlot(IAST heightValueMatrix, int[] dimension, IAST plotStyle,
      int[] lineColorNumber, EvalEngine engine) {
    // convert possible sparse array expression:
    IAST values = (IAST) heightValueMatrix.normal(false);
    if (dimension[0] > 3) {
      if (dimension[1] == 3) {
        double minX = (((IAST) values.arg1()).arg1()).evalDouble();
        double maxX = minX;

        double minY = (((IAST) values.arg1()).arg2()).evalDouble();
        double maxY = minY;

        double minZ = (((IAST) values.arg1()).arg3()).evalDouble();
        double maxZ = minZ;

        final IAST color = GraphicsFunctions.plotStyleColorExpr(lineColorNumber[0]++, plotStyle);
        for (int i = 1; i <= values.argSize(); i++) {
          IAST row = ((IAST) values.get(i));

          try {
            // evalDouble may throw ArgumentTypeException
            double x = row.arg1().evalDouble();
            if (x < minX)
              minX = x;
            if (x > maxX)
              maxX = x;

            double y = row.arg2().evalDouble();
            if (y < minY)
              minY = y;
            if (y > maxY)
              maxY = y;

            double z = row.arg3().evalDouble();
            if (z < minZ)
              minZ = z;
            if (z > maxZ)
              maxZ = z;
          } catch (ArgumentTypeException ate) {
            // ignore this row
          }
        }

        // ListLinePlot3D size is 2.5 × 2.5 × 1 independently from its coordinates
        IAST deltaXYZ =
            F.List(F.num((maxX - minX) / 2.5), F.num((maxY - minY) / 2.5), F.num(maxZ - minZ));

        IASTAppendable pointList = F.ListAlloc(dimension[0]);
        for (int i = 1; i < values.size(); i++) {
          IExpr rowList = values.get(i);
          pointList.append(rowList.divide(deltaXYZ));
        }
        return F.List(color, F.Line(pointList));
      } else if (dimension[1] > 3) {
        IExpr flattenHeights = engine.evaluate(F.Flatten(values));
        double deltaHeight =
            engine.evaluate(F.Max.of(flattenHeights).subtract(F.Min(flattenHeights))).evalDouble();

        final int valuesSize = values.size();
        IASTAppendable resultList = F.ListAlloc(dimension[0]);
        for (int i = 1; i < valuesSize; i++) {
          IAST rowList = (IAST) values.get(i);
          final int rowListSize = rowList.size();
          IASTAppendable lineList = F.ListAlloc(dimension[1]);
          final IAST color = GraphicsFunctions.plotStyleColorExpr(lineColorNumber[0]++, plotStyle);

          for (int j = 1; j < rowListSize; j++) {
            // ListLinePlot3D size is 2.5 × 2.5 × 1 independently from its coordinates
            lineList.append(F.List(F.num(i * 2.5 / valuesSize), F.num(j * 2.5 / rowListSize),
                rowList.get(j).divide(deltaHeight)));
          }
          resultList.append(color);
          resultList.append(F.Line(lineList));
        }
        return resultList;
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
