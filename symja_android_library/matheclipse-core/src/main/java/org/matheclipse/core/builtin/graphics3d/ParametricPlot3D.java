package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of ParametricPlot3D.
 */
public class ParametricPlot3D extends AbstractFunctionOptionEvaluator {

  public ParametricPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    if (argSize < 2) {
      return F.NIL;
    }

    IExpr functionArg = ast.arg1();
    if (!functionArg.isList()) {
      return F.NIL;
    }

    int defaultPoints = 40;
    boolean isSurface = (argSize >= 3 && ast.arg2().isList() && ast.arg3().isList());
    if (!isSurface) {
      defaultPoints = 150;
    }

    int plotPoints = defaultPoints;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    } else if (options[0].isList()) {
      plotPoints = ((IAST) options[0]).arg1().toIntDefault(defaultPoints);
    }
    if (plotPoints < 5) {
      plotPoints = 5;
    }

    List<IExpr> functions = new ArrayList<>();
    IAST listArg = (IAST) functionArg;

    // Check if it's a list of parametric functions
    if (listArg.argSize() > 0 && listArg.arg1().isList()) {
      for (int i = 1; i <= listArg.argSize(); i++) {
        functions.add(listArg.get(i));
      }
    } else {
      functions.add(listArg);
    }

    IExpr plotStyle = options[3];
    IASTAppendable graphicsList = F.ListAlloc();

    IAST explicitStyles = F.NIL;
    if (plotStyle.isList()) {
      explicitStyles = (IAST) plotStyle;
    }

    if (isSurface) {
      if (!ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
        return Errors.printMessage(S.Plot, "pllim", F.list(ast.arg2()), engine);
      }
      if (!ast.arg3().isList3() || !ast.arg3().first().isSymbol()) {
        return Errors.printMessage(S.Plot, "pllim", F.list(ast.arg3()), engine);
      }

      IAST uRange = (IAST) ast.arg2();
      IAST vRange = (IAST) ast.arg3();

      for (int i = 0; i < functions.size(); i++) {
        IExpr currentStyle;
        if (explicitStyles.argSize() >= 1) {
          int styleIdx = i % explicitStyles.argSize() + 1;
          currentStyle = explicitStyles.get(styleIdx);
        } else if (plotStyle.isAST() && !plotStyle.isList()) {
          currentStyle = plotStyle;
        } else {
          int colorIdx = GraphicsOptions.incColorIndex(i);
          currentStyle = GraphicsOptions.plotStyleColorExpr(colorIdx, F.NIL);
        }

        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);

        IExpr meshOption = options[5];
        if (meshOption.isFalse() || meshOption.equals(S.None)) {
          IASTAppendable edgeForm = F.ast(S.EdgeForm);
          edgeForm.append(S.None);
          builder.setStyle(currentStyle, edgeForm);
        } else {
          builder.setStyle(currentStyle);
        }

        createSurfaceGeometry(functions.get(i), uRange, vRange, plotPoints, engine, builder);

        IExpr complex = builder.build();
        if (!complex.equals(F.NIL)) {
          graphicsList.append(complex);
        }
      }
    } else if (argSize >= 2 && ast.arg2().isList()) {
      if (!ast.arg2().first().isSymbol()) {
        return Errors.printMessage(S.Plot, "pllim", F.list(ast.arg2()), engine);
      }

      IAST range = (IAST) ast.arg2();

      for (int i = 0; i < functions.size(); i++) {
        IExpr currentStyle;
        if (explicitStyles.argSize() >= 1) {
          int styleIdx = i % explicitStyles.argSize() + 1;
          currentStyle = explicitStyles.get(styleIdx);
        } else if (plotStyle.isAST() && !plotStyle.isList()) {
          currentStyle = plotStyle;
        } else {
          int colorIdx = GraphicsOptions.incColorIndex(i);
          currentStyle = GraphicsOptions.plotStyleColorExpr(colorIdx, F.NIL);
        }

        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
        builder.setStyle(currentStyle);

        createCurveGeometry(functions.get(i), range, plotPoints, engine, builder);

        IExpr complex = builder.build();
        if (!complex.equals(F.NIL)) {
          graphicsList.append(complex);
        }
      }
    } else {
      return F.NIL;
    }

    if (graphicsList.argSize() == 0) {
      return F.NIL;
    }

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsList);
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, S.Automatic));
    result.append(F.Rule(S.Axes, S.True));

    return result;
  }

  private void createCurveGeometry(IExpr func, IAST range, int pointsCount, EvalEngine engine,
      GraphicsComplexBuilder builder) {

    ISymbol uVar = (ISymbol) range.arg1();
    double uMin = range.arg2().evalf();
    double uMax = range.arg3().evalf();
    double step = (uMax - uMin) / (pointsCount - 1);

    IASTAppendable currentLine = F.ListAlloc();

    for (int i = 0; i < pointsCount; i++) {
      double u = uMin + i * step;
      IExpr subst = F.subst(func, F.List(F.Rule(uVar, F.num(u))));
      IExpr res = engine.evaluate(subst);

      boolean validPoint = false;

      if (res.isList() && ((IAST) res).argSize() >= 3) {
        try {
          double x = ((IAST) res).arg1().evalf();
          double y = ((IAST) res).arg2().evalf();
          double z = ((IAST) res).arg3().evalf();

          if (!Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z)) {
            int idx = builder.addVertex(x, y, z, null, null);
            currentLine.append(F.ZZ(idx));
            validPoint = true;
          }
        } catch (Exception e) {
          // Ignored. Fall through to break line segment.
        }
      }

      // If a point evaluates to NaN (e.g., asymptote), break the line and start a new one
      if (!validPoint && currentLine.argSize() > 0) {
        builder.addPrimitive(F.Line(currentLine));
        currentLine = F.ListAlloc();
      }
    }

    if (currentLine.argSize() > 0) {
      builder.addPrimitive(F.Line(currentLine));
    }
  }

  private void createSurfaceGeometry(IExpr func, IAST uRange, IAST vRange, int pointsCount,
      EvalEngine engine, GraphicsComplexBuilder builder) {

    ISymbol uVar = (ISymbol) uRange.arg1();
    double uMin = uRange.arg2().evalf();
    double uMax = uRange.arg3().evalf();
    ISymbol vVar = (ISymbol) vRange.arg1();
    double vMin = vRange.arg2().evalf();
    double vMax = vRange.arg3().evalf();
    double uStep = (uMax - uMin) / (pointsCount - 1);
    double vStep = (vMax - vMin) / (pointsCount - 1);

    int[][] indices = new int[pointsCount][pointsCount];

    for (int i = 0; i < pointsCount; i++) {
      double u = uMin + i * uStep;
      for (int j = 0; j < pointsCount; j++) {
        indices[i][j] = -1;
        double v = vMin + j * vStep;

        IExpr subst = F.subst(func, F.List(F.Rule(uVar, F.num(u)), F.Rule(vVar, F.num(v))));
        IExpr res = engine.evaluate(subst);

        if (res.isList() && ((IAST) res).argSize() >= 3) {
          try {
            double x = ((IAST) res).arg1().evalf();
            double y = ((IAST) res).arg2().evalf();
            double z = ((IAST) res).arg3().evalf();

            if (!Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z)) {
              indices[i][j] = builder.addVertex(x, y, z, null, null);
            }
          } catch (Exception e) {
            // Point failed to evaluate; leave index as -1
          }
        }
      }
    }

    for (int i = 0; i < pointsCount - 1; i++) {
      for (int j = 0; j < pointsCount - 1; j++) {
        int p1 = indices[i][j];
        int p2 = indices[i + 1][j];
        int p3 = indices[i + 1][j + 1];
        int p4 = indices[i][j + 1];

        // Form polygon only if all 4 corners are valid coordinates
        if (p1 != -1 && p2 != -1 && p3 != -1 && p4 != -1) {
          builder.addPolygon(new int[] {p1, p2, p3, p4});
        }
      }
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios,
            S.Mesh},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.True});
  }
}