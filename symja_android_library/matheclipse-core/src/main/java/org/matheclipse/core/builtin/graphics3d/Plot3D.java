package org.matheclipse.core.builtin.graphics3d;

import static org.matheclipse.core.expression.F.Rule;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryNumerical;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Plot3D extends AbstractFunctionOptionEvaluator {

  public static final Plot3D CONST = new Plot3D();
  // private static IExpr DEFAULT_COLOR = F.RGBColor(F.num(1.0), F.num(0.8), F.num(0.4));
  private static final int NUMBER_OF_DIVISIONS = 41;

  public Plot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    // if (options[0].isTrue()) {
    // IExpr temp = S.Manipulate.of(engine, originalAST);
    // if (temp.headID() == ID.JSFormData)
    // return temp;
    // return F.NIL;
    // }
    // 2. Parse Options
    int plotPoints = 40;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    } else if (options[0].isList()) {
      plotPoints = ((IAST) options[0]).arg1().toIntDefault(40);
    }
    if (plotPoints < 5)
      plotPoints = 5;

    IExpr plotStyle = options[3];

    // IExpr colorFunction = options[2];
    // if (colorFunction.equals(S.Automatic)) {
    // colorFunction = F.stringx("Sunset");
    // }

    if ((ast.size() >= 4) && ast.arg2().isList() && ast.arg3().isList()) {
      return createGraphicsComplex(ast, argSize, plotStyle, originalAST, engine);
    }
    return F.NIL;
  }

  private static IExpr createGraphicsComplex(IAST ast, int argSize, IExpr plotStyle,
      IAST originalAST, final EvalEngine engine) {
    try {
      final IAST lst1 = (IAST) ast.arg2();
      final IAST lst2 = (IAST) ast.arg3();
      if (lst1.isAST3() && lst2.isAST3()) {
        // IASTMutable colorData = F.binaryAST2(S.ColorData, F.ZZ(97), F.stringx("ColorList"));
        // IExpr colorList = engine.evaluate(colorData);

        final IExpr xMin = engine.evalN(lst1.arg2());
        final IExpr xMax = engine.evalN(lst1.arg3());
        final IExpr yMin = engine.evalN(lst2.arg2());
        final IExpr yMax = engine.evalN(lst2.arg3());

        if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || (!(yMin instanceof INum))
            || (!(yMax instanceof INum))) {
          return F.NIL;
        }

        final double xMinD = ((INum) xMin).getRealPart();
        final double xMaxD = ((INum) xMax).getRealPart();
        final double yMinD = ((INum) yMin).getRealPart();
        final double yMaxD = ((INum) yMax).getRealPart();
        if (xMaxD <= xMinD || yMaxD <= yMinD)
          return F.NIL;


        final ISymbol xVar = (ISymbol) lst1.arg1();
        final ISymbol yVar = (ISymbol) lst2.arg1();
        final IExpr functions = ast.arg1().makeList();
        final IASTAppendable graphicsComplexList = F.ListAlloc();

        // Prepare styles list if explicit
        IAST explicitStyles = F.NIL;
        if (plotStyle.isList()) {
          explicitStyles = (IAST) plotStyle;
        }
        for (int f = 1; f < functions.size(); f++) {
          IExpr function = functions.get(f);
          IExpr currentStyle;
          if (explicitStyles.size() > 1) {
            // Explicit list {Red, Green...}
            int styleIdx = (f - 1) % (explicitStyles.size() - 1) + 1;
            currentStyle = explicitStyles.get(styleIdx);
          } else if (plotStyle.isAST() && !plotStyle.isList()) {
            // Explicit single style
            currentStyle = plotStyle;
          } else {
            // Automatic: Cycle default colors
            int colorIdx = GraphicsOptions.incColorIndex(f - 1);
            currentStyle = GraphicsOptions.plotStyleColorExpr(colorIdx, F.NIL);
          }
          // IExpr color = engine.evaluate(F.unaryAST1(colorList, F.ZZ(f)));

          final int gridPoints = NUMBER_OF_DIVISIONS;
          final double xStep = (xMaxD - xMinD) / (gridPoints - 1);
          final double yStep = (yMaxD - yMinD) / (gridPoints - 1);

          final BinaryNumerical hbn = new BinaryNumerical(function, xVar, yVar, engine);
          final double[] zValues = new double[gridPoints * gridPoints];

          // 1. Calculate values
          int zCount = 0;
          double zActualMin = Double.POSITIVE_INFINITY;
          double zActualMax = Double.NEGATIVE_INFINITY;
          int idx = 0;
          for (int i = 0; i < gridPoints; i++) {
            double x = xMinD + i * xStep;
            for (int j = 0; j < gridPoints; j++) {
              double y = yMinD + j * yStep;
              double z = hbn.value(x, y);
              zValues[idx++] = z;
              if (Double.isFinite(z)) {
                zCount++;
                if (z < zActualMin)
                  zActualMin = z;
                if (z > zActualMax)
                  zActualMax = z;
              }
            }
          }

          // 2. Determine range & clamp
          double zMinCalc = zActualMin;
          double zMaxCalc = zActualMax;
          if (zCount > 0) {
            double[] validZ = new double[zCount];
            int vIdx = 0;
            for (double val : zValues) {
              if (Double.isFinite(val)) {
                validZ[vIdx++] = val;
              }
            }
            double[] range = GraphicsUtil.automaticPlotRange3D(validZ);
            if (range[1] > range[0]) {
              zMinCalc = range[0];
              zMaxCalc = range[1];
            }
          } else {
            zMinCalc = -1.0;
            zMaxCalc = 1.0;
          }

          final IASTAppendable pointsList = F.ListAlloc(gridPoints * gridPoints);
          idx = 0;
          for (int i = 0; i < gridPoints; i++) {
            double x = xMinD + i * xStep;
            for (int j = 0; j < gridPoints; j++) {
              double y = yMinD + j * yStep;
              double z = zValues[idx];
              if (Double.isNaN(z) || Double.isInfinite(z)) {
                if (z == Double.POSITIVE_INFINITY)
                  z = zMaxCalc;
                else if (z == Double.NEGATIVE_INFINITY)
                  z = zMinCalc;
                else
                  z = zMaxCalc;
              } else if (z > zMaxCalc) {
                z = zMaxCalc;
              } else if (z < zMinCalc) {
                z = zMinCalc;
              }
              zValues[idx++] = z;
              pointsList.append(F.List(F.num(x), F.num(y), F.num(z)));
            }
          }

          IASTAppendable vertexColors = F.ListAlloc(gridPoints * gridPoints);
          // boolean hasColors = false;

          idx = 0;
          for (int i = 0; i < gridPoints; i++) {
            double x = xMinD + i * xStep;
            for (int j = 0; j < gridPoints; j++) {
              double y = yMinD + j * yStep;
              double z = zValues[idx++];

              if (Double.isFinite(z)) {
                double nx = (x - xMinD) / (xMaxD - xMinD);
                double ny = (y - yMinD) / (yMaxD - yMinD);
                double nz = (z - zMinCalc) / (zMaxCalc - zMinCalc);
                if (Double.isNaN(nz))
                  nz = 0.5;
              }

              // if (currentStyle != null) {
              // vertexColors.append(currentStyle);
              // hasColors = true;
              // } else {
              // vertexColors.append(currentStyle);
              // }
            }
          }

          final IASTAppendable faces = F.ListAlloc((gridPoints - 1) * (gridPoints - 1));
          for (int i = 0; i < gridPoints - 1; i++) {
            for (int j = 0; j < gridPoints - 1; j++) {
              int p1 = (i * gridPoints) + j + 1;
              int p2 = p1 + 1;
              int p3 = p2 + gridPoints;
              int p4 = p1 + gridPoints;
              faces.append(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4)));
            }
          }

          IASTAppendable group = F.ListAlloc(2);
          // if (!hasColors) {
          group.append(currentStyle);
          // }
          group.append(F.Polygon(faces));

          IExpr graphicsComplex;
          // if (hasColors) {
          // graphicsComplex =
          // F.GraphicsComplex(pointsList, F.List(group), Rule(S.VertexColors, vertexColors));
          // } else {
          graphicsComplex = F.GraphicsComplex(pointsList, F.List(group));
          // }
          graphicsComplexList.append(graphicsComplex);
        }

        final IASTAppendable result = F.ast(S.Graphics3D);
        result.append(graphicsComplexList);
        result.append(Rule(S.PlotRange, S.Automatic));

        // Handle AxesEdge option
        for (int i = argSize + 1; i < originalAST.size(); i++) {
          IExpr arg = originalAST.get(i);
          if (arg.isRuleAST()) {
            IExpr key = ((IAST) arg).arg1();
            if (key == S.AxesEdge) {
              result.append(arg);
            }
          }
        }

        // System.out.println(result);
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Plot3D, rex, engine);
    }
    return F.NIL;
  }

  public static IExpr plotArray(double xMin, double xMax, double yMin, double yMax, IExpr function,
      ISymbol xVar, ISymbol yVar, EvalEngine engine) {
    return F.NIL;
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic});
  }
}