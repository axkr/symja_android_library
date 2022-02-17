package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.IOFunctions;
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
    IOFunctions.printExperimental(S.ListLinePlot3D);
    if (ast.argSize() > 0) {

      IAST plotStyle = F.NIL;
      if (ast.argSize() > 1) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        if (options.isInvalidPosition(1)) {
          return options.printNonopt(ast, 1, engine);
        }
        IExpr temp = options.getOption(S.PlotStyle);
        if (temp.isAST()) {
          plotStyle = (IAST) temp;
        }
      }

      // case 1: single line heights
      // e.g.: ListLinePlot3D[{1, 2, 3, 4, 5}]
      if (ast.arg1().isASTSizeGE(S.List, 2)) {
        try {
          double d = ((IAST) ast.arg1()).arg1().evalDouble();
          IExpr heightLinePlot = heightLinePlot(F.List(ast.arg1()), plotStyle, engine);
          if (heightLinePlot.isPresent()) {
            IASTAppendable result = F.Graphics3D(heightLinePlot);
            if (ast.argSize() > 1) {
              // add same options to Graphics3D
              result.appendAll(ast, 2, ast.size());
            }
            return result;
          }
          return F.NIL;
        } catch (ArgumentTypeException ate) {
          // fall through
        }
      }

      // try if arg1 is a matrix
      int[] dimension = ast.arg1().isMatrix(false);

      // case 2: single line coordinates
      // e.g.: ListLinePlot3D[{{x_1, y_1, z_1}, {x_2, y_2, z_2}}]
      if (dimension != null && dimension.length == 2 && dimension[1] == 3) {
        IASTAppendable result =
            F.Graphics3D(coordinateLinePlot(F.List(ast.arg1()), plotStyle, engine));
        if (ast.argSize() > 1) {
          // add same options to Graphics3D
          result.appendAll(ast, 2, ast.size());
        }
        return result;
      }

      // case 3: multiple line heights
      // e.g.: ListLinePlot3D[{{1, 2, 3, 4}, {-1, -2, -3, -4}}]
      if (ast.arg1().isASTSizeGE(S.List, 2) && ((IAST) ast.arg1()).arg1().isASTSizeGE(S.List, 2)) {
        try {
          double d = ((IAST) ((IAST) ast.arg1()).arg1()).arg1().evalDouble();
          IExpr heightLinePlot = heightLinePlot((IAST) ast.arg1(), plotStyle, engine);
          if (heightLinePlot.isPresent()) {
            IASTAppendable result = F.Graphics3D(heightLinePlot);
            if (ast.argSize() > 1) {
              // add same options to Graphics3D
              result.appendAll(ast, 2, ast.size());
            }
            return result;
          }
        } catch (ArgumentTypeException ate) {
          // fall through
        }
      }

      if (ast.arg1().isASTSizeGE(S.List, 2)) {
        dimension = ((IAST) ast.arg1()).arg1().isMatrix(false);
        // case 4: multiple line coordinates
        // e.g.: ListLinePlot3D[{{coord1, coord2}, {coord3, coord4}}]
        if (dimension != null && dimension.length == 2 && dimension[1] == 3) {
          IASTAppendable result =
              F.Graphics3D(coordinateLinePlot((IAST) ast.arg1(), plotStyle, engine));
          if (ast.argSize() > 1) {
            // add same options to Graphics3D
            result.appendAll(ast, 2, ast.size());
          }
          return result;
        }
      }
    }

    // `1` is not a valid dataset or a list of datasets.
    return IOFunctions.printMessage(ast.topHead(), "ldata", F.List(ast.arg1()), engine);
  }

  private IExpr heightLinePlot(IAST heights, IAST plotStyle, EvalEngine engine) {
    final int valuesSize = heights.size();
    IASTAppendable resultList = F.NIL;

    IExpr flattenHeights = engine.evaluate(F.Flatten(heights));
    final double deltaHeight =
        engine.evaluate(F.Max(flattenHeights).subtract(F.Min(flattenHeights))).evalDouble();
    if (F.isZero(deltaHeight)) {
      // Division by zero `1`.
      throw new ArgumentTypeException("zzdivzero", F.List("- delta height is 0"));
    }
    int lineColorNumber = 1;

    for (int i = 1; i < valuesSize; i++) {
      if (heights.get(i).isAST()) {
        IAST rowList = (IAST) heights.get(i);
        final int rowListSize = rowList.size();

        IASTAppendable lineList = F.ListAlloc(rowListSize);

        for (int j = 1; j < rowListSize; j++) {
          // ListLinePlot3D size is 2.5 × 2.5 × 1 independently from its coordinates
          lineList.append(F.List(F.num(i * 2.5 / valuesSize), F.num(j * 2.5 / rowListSize),
              rowList.get(j).divide(deltaHeight)));
        }

        final IAST color = GraphicsFunctions.plotStyleColorExpr(lineColorNumber++, plotStyle);
        if (!resultList.isPresent()) {
          resultList = F.ListAlloc(valuesSize);
        }
        resultList.append(color);
        resultList.append(F.Line(lineList));
      }
    }
    return resultList;
  }

  private IExpr coordinateLinePlot(IAST coordinates, IAST plotStyle, EvalEngine engine) {
    double minX = ((IAST) (((IAST) coordinates.arg1()).arg1())).arg1().evalDouble();
    double maxX = minX;

    double minY = ((IAST) (((IAST) coordinates.arg1()).arg1())).arg2().evalDouble();
    double maxY = minY;

    double minZ = ((IAST) (((IAST) coordinates.arg1()).arg1())).arg3().evalDouble();
    double maxZ = minZ;

    for (int i = 1; i <= coordinates.argSize(); i++) {
      IAST line = (IAST) coordinates.get(i);

      for (int j = 1; j <= line.argSize(); j++) {
        try {
          IAST coordinate = (IAST) line.get(j);

          // evalDouble may throw ArgumentTypeException
          double x = coordinate.arg1().evalDouble();
          if (x < minX)
            minX = x;
          if (x > maxX)
            maxX = x;

          double y = coordinate.arg2().evalDouble();
          if (y < minY)
            minY = y;
          if (y > maxY)
            maxY = y;

          double z = coordinate.arg3().evalDouble();
          if (z < minZ)
            minZ = z;
          if (z > maxZ)
            maxZ = z;
        } catch (ArgumentTypeException ate) {
          // ignore this row
        }
      }
    }

    // ListLinePlot3D size is 2.5 × 2.5 × 1 independently from its coordinates
    IAST deltaXYZ = F.List((maxX - minX) / 2.5, (maxY - minY) / 2.5, maxZ - minZ);

    // the color number with which the line will be printed
    int lineColorNumber = 1;

    IASTAppendable lineList = F.ListAlloc(coordinates.size() * 2);

    for (int i = 1; i <= coordinates.argSize(); i++) {
      final IAST color = GraphicsFunctions.plotStyleColorExpr(lineColorNumber++, plotStyle);

      lineList.append(color);
      // (# / deltaXYZ)& /@ line
      lineList.append(
          F.Line(S.Map.of(engine, F.Function(F.Divide(F.Slot1, deltaXYZ)), coordinates.get(i))));
    }

    return lineList;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
