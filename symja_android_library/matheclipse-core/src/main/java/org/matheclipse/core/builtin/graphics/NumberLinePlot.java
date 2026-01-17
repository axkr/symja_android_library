package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Plot points and intervals along a number line.
 * <p>
 * Examples: <code>NumberLinePlot[{1, 2, 3}]</code>
 * <code>NumberLinePlot[{Prime[Range[20]], Prime[Range[40]]}]</code>
 * <code>NumberLinePlot[Interval[{0, Infinity}]]</code>
 */
public class NumberLinePlot extends ListPlot {

  public NumberLinePlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IExpr dataArg = ast.arg1();
    IAST dataList = dataArg.makeList();

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Heuristic to detect if input is multiple datasets {{d1...}, {d2...}} or single {x1, x2...}
    boolean multiLevel = false;
    if (dataList.size() > 1) {
      IExpr first = dataList.arg1();
      if (first.isList() && !first.isAST(S.Interval)) {
        multiLevel = true;
      }
    }

    IAST levels;
    if (multiLevel) {
      levels = dataList;
    } else {
      levels = F.List(dataList);
    }

    int numLevels = levels.size();

    try {
      // Calculate finite bounds ---
      double minFinite = Double.MAX_VALUE;
      double maxFinite = -Double.MAX_VALUE;
      boolean hasData = false;

      for (int i = 1; i < numLevels; i++) {
        IExpr levelItem = levels.get(i);
        IAST levelData = (levelItem.isList() && !levelItem.isAST(S.Interval)) ? (IAST) levelItem
            : F.List(levelItem);

        for (IExpr item : levelData) {
          if (item.isNumber()) {
            double val = item.evalf();
            if (Double.isFinite(val)) {
              if (val < minFinite)
                minFinite = val;
              if (val > maxFinite)
                maxFinite = val;
              hasData = true;
            }
          } else if (item.isAST(S.Interval)) {
            for (int k = 1; k < item.size(); k++) {
              IExpr intervalRange = ((IAST) item).get(k);
              if (intervalRange.isList() && intervalRange.size() >= 3) {
                checkBounds(intervalRange.get(1), intervalRange.get(2), minFinite, maxFinite);
                double a = intervalRange.get(1).evalf();
                double b = intervalRange.get(2).evalf();
                if (Double.isFinite(a)) {
                  if (a < minFinite)
                    minFinite = a;
                  if (a > maxFinite)
                    maxFinite = a;
                  hasData = true;
                }
                if (Double.isFinite(b)) {
                  if (b < minFinite)
                    minFinite = b;
                  if (b > maxFinite)
                    maxFinite = b;
                  hasData = true;
                }
              }
            }
          } else if (item.isAST("IntervalData")) {
            IExpr arg1 = ((IAST) item).arg1();
            if (arg1.isList() && arg1.size() >= 5) {
              double a = ((IAST) arg1).get(1).evalf();
              double b = ((IAST) arg1).get(4).evalf();
              if (Double.isFinite(a)) {
                if (a < minFinite)
                  minFinite = a;
                if (a > maxFinite)
                  maxFinite = a;
                hasData = true;
              }
              if (Double.isFinite(b)) {
                if (b < minFinite)
                  minFinite = b;
                if (b > maxFinite)
                  maxFinite = b;
                hasData = true;
              }
            }
          }
        }
      }

      // Handle case where we only have infinity or no data
      if (!hasData && minFinite == Double.MAX_VALUE) {
        minFinite = 0.0;
        maxFinite = 1.0;
      } else if (minFinite == maxFinite) {
        minFinite -= 1.0;
        maxFinite += 1.0;
      }

      // Determine visual bounds for arrows (extend 10% beyond finite data)
      double range = maxFinite - minFinite;
      if (range == 0)
        range = 1.0;
      double padding = range * 0.1;
      double drawMin = minFinite - padding;
      double drawMax = maxFinite + padding;

      // --- PASS 2: Generate Primitives ---
      IASTAppendable primitives = F.ListAlloc();

      // Visual constants
      double lineThickness = 0.01;
      double pointSize = 0.025;

      for (int i = 1; i < numLevels; i++) {
        IExpr levelItem = levels.get(i);
        IAST levelData = (levelItem.isList() && !levelItem.isAST(S.Interval)) ? (IAST) levelItem
            : F.List(levelItem);

        IExpr color = GraphicsOptions.plotStyleColorExpr(i - 1, F.NIL);
        IAST styleDirective =
            F.Directive(color, F.PointSize(pointSize), F.Thickness(lineThickness));

        IASTAppendable levelPrimitives = F.ListAlloc();
        levelPrimitives.append(styleDirective);

        double y = i;

        for (IExpr item : levelData) {
          if (item.isNumber()) {
            double val = item.evalf();
            if (Double.isFinite(val)) {
              levelPrimitives.append(F.Point(F.List(F.num(val), F.num(y))));
            }
          } else if (item.isAST(S.Interval)) {
            for (int k = 1; k < item.size(); k++) {
              IExpr intervalRange = ((IAST) item).get(k);
              if (intervalRange.isList() && intervalRange.size() >= 3) {
                drawInterval(levelPrimitives, intervalRange.get(1), intervalRange.get(2),
                    S.LessEqual, S.LessEqual, y, drawMin, drawMax, color, lineThickness, true);
              }
            }
          } else if (item.isAST("IntervalData")) {
            IExpr arg1 = ((IAST) item).arg1();
            if (arg1.isList() && arg1.size() >= 5) {
              IAST spec = (IAST) arg1;
              drawInterval(levelPrimitives, spec.get(1), spec.get(4), spec.get(2), spec.get(3), y,
                  drawMin, drawMax, color, lineThickness, true);
            }
          }
        }

        if (levelPrimitives.argSize() > 1) {
          primitives.append(levelPrimitives);
        }
      }

      if (primitives.size() <= 1 && !hasData)
        return F.NIL;

      graphicsOptions.setBoundingBox(new double[] {drawMin, drawMax, 0.0, numLevels + 1.0});
      graphicsOptions.addPadding();

      IASTAppendable result = F.Graphics(primitives);
      result.appendArgs(graphicsOptions.getListOfRules());
      return result;
    } catch (RuntimeException rex) {
      return Errors.printMessage(S.NumberLinePlot, rex);
    }
  }

  private void drawInterval(IASTAppendable primitives, IExpr minExpr, IExpr maxExpr, IExpr lType,
      IExpr rType, double y, double drawMin, double drawMax, IExpr color, double thickness,
      boolean filledPoints) {

    double a = minExpr.evalf();
    double b = maxExpr.evalf();
    boolean aInf = Double.isInfinite(a);
    boolean bInf = Double.isInfinite(b);
    boolean aNeg = a < 0;
    boolean bPos = b > 0;

    // 1. Draw Line / Arrow
    if (!aInf && !bInf) {
      // Finite segment
      primitives.append(F.Line(F.List(F.List(F.num(a), F.num(y)), F.List(F.num(b), F.num(y)))));
    } else if (!aInf && bInf && bPos) {
      // Start at a, Arrow to Right
      primitives
          .append(F.Arrow(F.List(F.List(F.num(a), F.num(y)), F.List(F.num(drawMax), F.num(y)))));
    } else if (aInf && aNeg && !bInf) {
      // Arrow from Left, End at b. Note: SVG Arrow draws head at 2nd point.
      // We want arrow pointing left. So draw FROM b TO drawMin.
      primitives
          .append(F.Arrow(F.List(F.List(F.num(b), F.num(y)), F.List(F.num(drawMin), F.num(y)))));
    } else if (aInf && bInf) {
      // Full line arrow both sides? Or just line across.
      // Mathematica draws arrows on both ends.
      primitives
          .append(F.Arrow(F.List(F.List(F.num(0), F.num(y)), F.List(F.num(drawMax), F.num(y)))));
      primitives
          .append(F.Arrow(F.List(F.List(F.num(0), F.num(y)), F.List(F.num(drawMin), F.num(y)))));
    }

    // 2. Draw Left Endpoint (if finite)
    if (!aInf) {
      IExpr p = F.Point(F.List(F.num(a), F.num(y)));
      if (lType.equals(S.Less)) {
        primitives.append(F.Style(p, F.FaceForm(S.White),
            F.EdgeForm(F.Directive(color, F.Thickness(thickness)))));
      } else {
        primitives.append(p);
      }
    }

    // 3. Draw Right Endpoint (if finite)
    if (!bInf) {
      IExpr p = F.Point(F.List(F.num(b), F.num(y)));
      if (rType.equals(S.Less)) {
        primitives.append(F.Style(p, F.FaceForm(S.White),
            F.EdgeForm(F.Directive(color, F.Thickness(thickness)))));
      } else {
        primitives.append(p);
      }
    }

  }

  private void checkBounds(IExpr min, IExpr max, double minFinite, double maxFinite) {
    // Helper already integrated into main loop logic
  }

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final IBuiltInSymbol[] optionSymbols, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(optionSymbols, options, engine);
    if (!graphicsOptions.getListOfRules().toString().contains("Axes")) {
      graphicsOptions.setAxes(F.List(S.True, S.False));
    }
    return graphicsOptions;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    defaults[GraphicsOptions.X_AXES] = F.List(S.True, S.False);
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.num(0.2);
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }
}
