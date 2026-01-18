package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ParametricPlot3D extends AbstractFunctionOptionEvaluator {

  private static final IExpr DEFAULT_COLOR = F.RGBColor(F.num(1.0), F.num(0.8), F.num(0.4));

  public ParametricPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    if (argSize < 2)
      return F.NIL;

    IExpr functionArg = ast.arg1();
    if (!functionArg.isList())
      return F.NIL;

    int defaultPoints = 40;
    boolean isSurface = (argSize >= 3 && ast.arg2().isList() && ast.arg3().isList());
    if (!isSurface)
      defaultPoints = 150;

    int plotPoints = defaultPoints;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    } else if (options[0].isList()) {
      plotPoints = ((IAST) options[0]).arg1().toIntDefault(defaultPoints);
    }
    if (plotPoints < 5)
      plotPoints = 5;

    List<IExpr> functions = new ArrayList<>();
    IAST listArg = (IAST) functionArg;
    if (listArg.size() > 1 && listArg.arg1().isList()) {
      for (int i = 1; i < listArg.size(); i++)
        functions.add(listArg.get(i));
    } else {
      functions.add(listArg);
    }

    IExpr plotStyleOpt = options[3];
    List<IExpr> styles = new ArrayList<>();
    if (plotStyleOpt.isList()) {
      IAST styleList = (IAST) plotStyleOpt;
      for (int i = 1; i < styleList.size(); i++)
        styles.add(styleList.get(i));
    } else if (!plotStyleOpt.equals(S.Automatic)) {
      styles.add(plotStyleOpt);
    } else {
      styles.add(DEFAULT_COLOR);
    }

    IASTAppendable allPoints = F.ListAlloc();
    IASTAppendable allPrimitives = F.ListAlloc();

    int currentPointOffset = 0;

    if (isSurface) {
      for (int i = 0; i < functions.size(); i++) {
        IExpr style = styles.get(i % styles.size());
        createSurfaceGeometry(functions.get(i), (IAST) ast.arg2(), (IAST) ast.arg3(), plotPoints,
            engine, allPoints, allPrimitives, style, currentPointOffset);
        currentPointOffset += (plotPoints * plotPoints);
      }
    } else if (argSize >= 2 && ast.arg2().isList()) {
      for (int i = 0; i < functions.size(); i++) {
        IExpr style = styles.get(i % styles.size());
        createCurveGeometry(functions.get(i), (IAST) ast.arg2(), plotPoints, engine, allPoints,
            allPrimitives, style, currentPointOffset);
        currentPointOffset += plotPoints;
      }
    } else {
      return F.NIL;
    }

    IExpr graphicsComplex = F.GraphicsComplex(allPoints, allPrimitives);
    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, S.Automatic)); // Changed to Automatic
    result.append(F.Rule(S.Axes, S.True));

    return result;
  }

  private void createCurveGeometry(IExpr func, IAST range, int pointsCount, EvalEngine engine,
      IASTAppendable allPoints, IASTAppendable allPrimitives, IExpr style, int indexOffset) {

    ISymbol uVar = (ISymbol) range.arg1();
    double uMin = range.arg2().evalDouble();
    double uMax = range.arg3().evalDouble();
    double step = (uMax - uMin) / (pointsCount - 1);

    for (int i = 0; i < pointsCount; i++) {
      double u = uMin + i * step;
      IExpr subst = F.subst(func, F.Rule(uVar, F.num(u)));
      IExpr res = engine.evaluate(subst);
      if (res.isList() && ((IAST) res).size() >= 4)
        allPoints.append(res);
      else
        allPoints.append(F.List(F.C0, F.C0, F.C0));
    }

    IASTAppendable lineIndices = F.ListAlloc(pointsCount);
    for (int i = 1; i <= pointsCount; i++)
      lineIndices.append(F.ZZ(indexOffset + i));

    IASTAppendable group = F.ListAlloc(2);
    group.append(style);
    group.append(F.Line(lineIndices));
    allPrimitives.append(group);
  }

  private void createSurfaceGeometry(IExpr func, IAST uRange, IAST vRange, int pointsCount,
      EvalEngine engine, IASTAppendable allPoints, IASTAppendable allPrimitives, IExpr style,
      int indexOffset) {

    ISymbol uVar = (ISymbol) uRange.arg1();
    double uMin = uRange.arg2().evalDouble();
    double uMax = uRange.arg3().evalDouble();
    ISymbol vVar = (ISymbol) vRange.arg1();
    double vMin = vRange.arg2().evalDouble();
    double vMax = vRange.arg3().evalDouble();
    double uStep = (uMax - uMin) / (pointsCount - 1);
    double vStep = (vMax - vMin) / (pointsCount - 1);

    for (int i = 0; i < pointsCount; i++) {
      double u = uMin + i * uStep;
      for (int j = 0; j < pointsCount; j++) {
        double v = vMin + j * vStep;
        IExpr subst = F.subst(func, F.List(F.Rule(uVar, F.num(u)), F.Rule(vVar, F.num(v))));
        IExpr res = engine.evaluate(subst);
        if (res.isList() && ((IAST) res).size() >= 4)
          allPoints.append(res);
        else
          allPoints.append(F.List(F.C0, F.C0, F.C0));
      }
    }

    IASTAppendable polyFaceList = F.ListAlloc((pointsCount - 1) * (pointsCount - 1));
    for (int i = 0; i < pointsCount - 1; i++) {
      for (int j = 0; j < pointsCount - 1; j++) {
        int p1 = (i * pointsCount) + j + 1;
        int p2 = p1 + 1;
        int p3 = p2 + pointsCount;
        int p4 = p1 + pointsCount;

        polyFaceList.append(F.List(F.ZZ(indexOffset + p1), F.ZZ(indexOffset + p2),
            F.ZZ(indexOffset + p3), F.ZZ(indexOffset + p4)));
      }
    }

    IASTAppendable group = F.ListAlloc(2);
    group.append(style);
    group.append(F.Polygon(polyFaceList));
    allPrimitives.append(group);
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
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic});
  }
}
