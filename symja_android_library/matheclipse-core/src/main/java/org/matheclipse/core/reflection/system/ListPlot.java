package org.matheclipse.core.reflection.system;

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
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
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

    // boundingbox an array of double values (length 4) which describes the bounding box
    // <code>[xMin, xMax, yMin, yMax]</code>
    double[] boundingbox =
        new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};
    int[] colour = new int[] {1};
    IAST graphicsPrimitives = plot(ast, boundingbox, colour, false, engine);
    if (graphicsPrimitives.isPresent()) {
      IExpr result = F.Graphics(graphicsPrimitives, //
          F.Rule(S.Axes, S.True),
          F.Rule(S.PlotRange, F.List(F.List(F.num(boundingbox[0]), F.num(boundingbox[1])),
              F.List(F.num(boundingbox[2]), F.num(boundingbox[3])))));
      return result;
    }

    return F.NIL;
  }

  /**
   * 
   * @param plot
   * @param boundingbox an array of double values (length 4) which describes the bounding box
   *        <code>[xMin, yMax, xMax, yMin]</code>
   * @param colour
   * @param listLinePlot
   * @param engine
   * @return
   */
  protected static IAST plot(IAST plot, double[] boundingbox, int[] colour, boolean listLinePlot,
      EvalEngine engine) {
    // final OptionArgs options = new OptionArgs(plot.topHead(), plot, 2, engine);
    if (plot.size() < 2) {
      return F.NIL;
    }

    // JSXGraph.sliderNamesFromList(manipulateAST, toJS);
    IExpr arg1 = plot.arg1();
    if (!arg1.isList()) {
      arg1 = engine.evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      IAssociation assoc = ((IAssociation) arg1);
      arg1 = assoc.matrixOrList();
    }
    if (arg1.isNonEmptyList()) {
      final IASTAppendable graphicsPrimitives = F.ListAlloc();
      IAST pointList = (IAST) arg1;
      // TODO Labeled lists
      if (pointList.isList(x -> x.isList() || x.isAST(S.Labeled, 3))) {
        int[] dimension = pointList.isMatrix(false);
        if (dimension != null) {
          if (dimension[1] == 2) {
            sequencePointListPlot(graphicsPrimitives, 1, pointList, boundingbox, colour,
                listLinePlot, engine);
            return graphicsPrimitives;
          }
        }
        IAST listOfLists = pointList;
        for (int i = 1; i < listOfLists.size(); i++) {
          pointList = (IAST) listOfLists.get(i);
          dimension = pointList.isMatrix(false);
          if (dimension != null) {
            if (dimension[1] == 2) {
              sequencePointListPlot(graphicsPrimitives, i, pointList, boundingbox, colour,
                  listLinePlot, engine);
            } else {
              return graphicsPrimitives;
            }
          } else {
            sequenceYValuesListPlot(graphicsPrimitives, i, pointList, boundingbox, colour,
                listLinePlot, engine);
          }
        }
      } else {
        sequenceYValuesListPlot(graphicsPrimitives, 1, pointList, boundingbox, colour, listLinePlot,
            engine);
      }
      return graphicsPrimitives; // JSXGraph.boundingBox(manipulateAST, boundingbox,
                                 // function.toString(),
      // toJS, false,
      // true);
    }
    return F.NIL;
  }

  /**
   * Plot a list of 2D points.
   * 
   * @param arg the number of the current argument
   * @param pointList
   * @param listLinePLot TODO
   * @param engine
   * @param ast
   * @param toJS
   * 
   * @return
   */
  private static void sequencePointListPlot(IASTAppendable graphicsPrimitives, int arg,
      IAST pointList, double[] boundingbox, int[] colour, boolean listLinePLot, EvalEngine engine) {
    // plot a list of 2D points

    IAST color = GraphicsFunctions.plotStyleColorExpr(colour[0]++, F.NIL);

    if (listLinePLot) {
      if (pointList.size() > 2) {
        // if (ast.arg1().isAST(S.ListLinePlot) && pointList.size() > 2) {
        // IAST lastPoint = (IAST) pointList.arg1();
        IAST lastPoint = F.NIL;
        boolean isConnected = false;
        int start = Integer.MAX_VALUE;
        for (int i = 1; i < pointList.size(); i++) {
          IAST point = (IAST) pointList.get(i);
          if (isNonReal(point.arg1(), point.arg2())) {
            continue;
          }
          lastPoint = point;
          start = i + 1;
          break;
        }

        if (start < Integer.MAX_VALUE && lastPoint.isPresent()) {
          xBoundingBox(engine, boundingbox, lastPoint.arg1());
          yBoundingBox(engine, boundingbox, lastPoint.arg2());
          graphicsPrimitives.append(color);

          IASTAppendable pointPrimitives = F.ListAlloc();
          IASTAppendable textPrimitives = F.ListAlloc();
          for (int i = start; i < pointList.size(); i++) {

            IAST point = (IAST) pointList.get(i);
            if (isNonReal(point)) {
              if (pointPrimitives.argSize() > 0) {
                graphicsPrimitives.append(F.Line(pointPrimitives));
                pointPrimitives = F.ListAlloc();
              }
              isConnected = false;
              lastPoint = F.NIL;
              continue;
            }
            if (!isConnected && lastPoint.isPresent()) {
              xBoundingBox(engine, boundingbox, lastPoint.arg1());
              yBoundingBox(engine, boundingbox, lastPoint.arg2());
              addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, lastPoint.arg1(),
                  lastPoint.arg2());
              xBoundingBox(engine, boundingbox, point.arg1());
              yBoundingBox(engine, boundingbox, point.arg2());
              addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, point.arg1(),
                  point.arg2());
              isConnected = true;
              continue;
            }
            if (isConnected) {
              xBoundingBox(engine, boundingbox, point.arg1());
              yBoundingBox(engine, boundingbox, point.arg2());
              addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, point.arg1(),
                  point.arg2());
            }
            lastPoint = point;
          }
          if (!isConnected && lastPoint.isPresent()) {
            // dummy line for a single point
            xBoundingBox(engine, boundingbox, lastPoint.arg1());
            yBoundingBox(engine, boundingbox, lastPoint.arg2());
            addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, lastPoint.arg1(),
                lastPoint.arg2());
            addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, lastPoint.arg1(),
                lastPoint.arg2());
          }

          if (pointPrimitives.argSize() > 0) {
            graphicsPrimitives.append(F.Line(pointPrimitives));
          }
          if (textPrimitives.argSize() > 0) {
            graphicsPrimitives.append(textPrimitives);
          }
        }
      }
    } else {
      graphicsPrimitives.append(color);
      IASTAppendable pointPrimitives = F.ListAlloc();
      IASTAppendable textPrimitives = F.ListAlloc();
      for (int i = 1; i < pointList.size(); i++) {
        IAST point = (IAST) pointList.get(i);
        if (isNonReal(point.arg1(), point.arg2())) {
          continue;
        }
        xBoundingBox(engine, boundingbox, point.arg1());
        yBoundingBox(engine, boundingbox, point.arg2());
        addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, point.arg1(),
            point.arg2());
      }
      if (pointPrimitives.argSize() > 0) {
        graphicsPrimitives.append(F.Point(pointPrimitives));
      }
      if (textPrimitives.argSize() > 0) {
        graphicsPrimitives.append(textPrimitives);
      }
    }
  }

  /**
   * Plot a list of points for Y-values for the X-values <code>1,2,3,...</code>.
   * 
   * @param pointList
   * @param listLinePlot TODO
   * @param engine
   * @param ast
   * @param toJS
   *
   * @return
   */
  private static void sequenceYValuesListPlot(IASTAppendable graphicsPrimitives, int arg,
      IAST pointList, double[] boundingbox, int[] colour, boolean listLinePlot, EvalEngine engine) {
    IAST color = GraphicsFunctions.plotStyleColorExpr(colour[0]++, F.NIL);
    xBoundingBox(engine, boundingbox, F.C0);
    xBoundingBox(engine, boundingbox, F.ZZ(pointList.size()));
    if (listLinePlot) {
      graphicsPrimitives.append(color);
      IExpr lastPoint = F.NIL;
      int lastPosition = -1;
      boolean isConnected = false;
      int start = Integer.MAX_VALUE;
      for (int i = 1; i < pointList.size(); i++) {
        IExpr currentPointY = pointList.get(i);
        if (isNonReal(currentPointY)) {
          continue;
        }
        lastPoint = currentPointY;
        lastPosition = i;
        start = i + 1;
        break;
      }
      if (start < Integer.MAX_VALUE) {
        yBoundingBox(engine, boundingbox, lastPoint);
        IASTAppendable pointPrimitives = F.ListAlloc();
        IASTAppendable textPrimitives = F.ListAlloc();
        for (int i = start; i < pointList.size(); i++) {
          IExpr currentPointY = pointList.get(i);
          if (isNonReal(currentPointY)) {
            if (pointPrimitives.argSize() > 0) {
              graphicsPrimitives.append(F.Line(pointPrimitives));
              pointPrimitives = F.ListAlloc();
            }
            lastPoint = F.NIL;
            isConnected = false;
            continue;
          }

          if (!isConnected && lastPoint.isPresent()) {
            addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, lastPosition,
                lastPoint);
            addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, i, currentPointY);
            isConnected = true;
            continue;
          }
          if (isConnected) {
            addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, i, currentPointY);
          }
          lastPoint = currentPointY;
          lastPosition = i;
        }
        if (!isConnected && lastPoint.isPresent()) {
          // dummy line for a single point
          addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, lastPosition,
              lastPoint);
          addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, pointList.size() - 1,
              lastPoint);
        }

        if (pointPrimitives.argSize() > 0) {
          graphicsPrimitives.append(F.Line(pointPrimitives));
        }
        if (textPrimitives.argSize() > 0) {
          graphicsPrimitives.append(textPrimitives);
        }
      }
    } else {
      graphicsPrimitives.append(color);
      if (pointList.isAST(S.Labeled, 3) && pointList.arg1().isList()) {
        pointList = (IAST) pointList.arg1();
        xBoundingBox(engine, boundingbox, F.ZZ(pointList.size()));
      }
      IASTAppendable pointPrimitives = F.ListAlloc();
      IASTAppendable textPrimitives = F.ListAlloc();
      for (int i = 1; i < pointList.size(); i++) {
        IExpr currentPointY = pointList.get(i);
        if (isNonReal(currentPointY)) {
          continue;
        }
        addSinglePoint(pointPrimitives, textPrimitives, boundingbox, engine, i, currentPointY);
      }
      if (pointPrimitives.argSize() > 0) {
        graphicsPrimitives.append(F.Point(pointPrimitives));
      }
      if (textPrimitives.argSize() > 0) {
        graphicsPrimitives.append(textPrimitives);
      }
    }
  }

  private static boolean addSinglePoint(IASTAppendable pointPrimitives,
      IASTAppendable textPrimitives, double[] boundingbox, EvalEngine engine, IExpr currentPointX,
      IExpr currentPointY) {
    ISignedNumber x = currentPointX.evalReal();
    ISignedNumber y = currentPointY.evalReal();
    if (x != null && y != null) {
      yBoundingBox(engine, boundingbox, x);
      if (currentPointX.isAST(S.Labeled, 3)) {
        textPrimitives.append(F.Text(currentPointX.second(), F.List(x, y)));
      }
      yBoundingBox(engine, boundingbox, y);
      if (currentPointY.isAST(S.Labeled, 3)) {
        textPrimitives.append(F.Text(currentPointY.second(), F.List(x, y)));
      }
      pointPrimitives.append(F.List(x, y));
      return true;
    }
    return false;
  }

  private static boolean addSinglePoint(IASTAppendable pointPrimitives,
      IASTAppendable textPrimitives, double[] boundingbox, EvalEngine engine, int i,
      IExpr currentPointY) {
    ISignedNumber x = F.num(i);
    ISignedNumber y = currentPointY.evalReal();
    if (y != null) {
      yBoundingBox(engine, boundingbox, currentPointY);
      if (currentPointY.isAST(S.Labeled, 3)) {
        textPrimitives.append(F.Text(currentPointY.second(), F.List(x, y)));
      }
      pointPrimitives.append(F.List(x, y));
      return true;
    }
    return false;
  }


  private static boolean isNonReal(IExpr lastPoint) {
    return lastPoint.isComplex() || lastPoint.isComplexNumeric() || lastPoint.isDirectedInfinity()
        || lastPoint == S.Indeterminate || lastPoint == S.None || lastPoint.isAST(S.Missing);
  }

  private static boolean isNonReal(IExpr lastPointX, IExpr lastPointY) {
    return isNonReal(lastPointX) || isNonReal(lastPointY);
  }

  private static void xBoundingBox(EvalEngine engine, double[] boundingbox, IExpr xExpr) {
    try {
      double xValue = engine.evalDouble(xExpr);
      if (Double.isFinite(xValue)) {
        if (xValue < boundingbox[0]) {
          boundingbox[0] = xValue;
        }
        if (xValue > boundingbox[1]) {
          boundingbox[1] = xValue;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
  }

  private static void yBoundingBox(EvalEngine engine, double[] boundingbox, IExpr yExpr) {
    try {
      double yValue = engine.evalDouble(yExpr);
      if (Double.isFinite(yValue)) {
        if (yValue < boundingbox[2]) {
          boundingbox[2] = yValue;
        }
        if (yValue > boundingbox[3]) {
          boundingbox[3] = yValue;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
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
