package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class DiscretePlot3D extends AbstractFunctionOptionEvaluator {

  private static class PlotData {
    double x, y, z;
    IExpr color;

    public PlotData(double x, double y, double z, IExpr color) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.color = color;
    }
  }

  public DiscretePlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    // DiscretePlot3D[f, {i, ...}, {j, ...}]
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr function = ast.arg1();
    IExpr iRange = ast.arg2();
    IExpr jRange = argSize >= 3 ? ast.arg3() : null;

    if (!iRange.isList()) {
      return F.NIL;
    }

    // Parse Options
    double extentX = 0.4;
    double extentY = 0.4;
    boolean useThinLineStyle = false; // Default for Automatic/None

    IExpr extentOption = options[0]; // ExtentSize
    if (extentOption.isNone() || extentOption.equals(S.Automatic)) {
      useThinLineStyle = true;
    } else if (extentOption.equals(S.Full)) {
      extentX = 0.5;
      extentY = 0.5;
    } else if (extentOption.isNumber()) {
      double val = extentOption.evalDouble();
      extentX = val / 2.0;
      extentY = val / 2.0;
    } else if (extentOption.isList() && ((IAST) extentOption).size() >= 3) {
      IExpr ex = ((IAST) extentOption).get(1);
      if (ex.isNumber())
        extentX = ex.evalDouble() / 2.0;
      IExpr ey = ((IAST) extentOption).get(2);
      if (ey.isNumber())
        extentY = ey.evalDouble() / 2.0;
    }

    // Scaling Logic
    double zBase = 0.0;
    IExpr scalingOption = options[4]; // ScalingFunctions
    boolean isLogZ = false;

    if (scalingOption.isString() && "Log".equalsIgnoreCase(scalingOption.toString())) {
      isLogZ = true;
    } else if (scalingOption.isList() && ((IAST) scalingOption).size() >= 4) {
      IExpr zScale = ((IAST) scalingOption).get(3);
      if (zScale.isString() && "Log".equalsIgnoreCase(zScale.toString())) {
        isLogZ = true;
      }
    }

    if (isLogZ) {
      zBase = 1.0;
    }

    // Prepare iterators
    IASTAppendable graphicsList = F.ListAlloc();

    IAST functions;
    if (function.isList()) {
      functions = (IAST) function;
    } else {
      functions = F.List(function);
    }

    IExpr[] defaultColors = new IExpr[] {F.RGBColor(1.0, 0.6, 0.6), // Light Red
        F.RGBColor(0.6, 0.6, 1.0), // Light Blue
        F.RGBColor(0.6, 1.0, 0.6), // Light Green
        F.RGBColor(1.0, 1.0, 0.6) // Light Yellow
    };

    try {
      List<INumber> iValues = parseIterator(iRange, engine);
      ISymbol iVar = (ISymbol) ((IAST) iRange).arg1();

      List<INumber> jValues = null;
      ISymbol jVar = null;
      if (jRange != null && jRange.isList()) {
        jValues = parseIterator(jRange, engine);
        jVar = (ISymbol) ((IAST) jRange).arg1();
      }

      boolean autoPlotRange = options[1].equals(S.Automatic);

      for (int funcIdx = 1; funcIdx < functions.size(); funcIdx++) {
        IExpr f = functions.get(funcIdx);
        IASTAppendable primitives = F.ListAlloc();

        IExpr color = defaultColors[(funcIdx - 1) % defaultColors.length];
        primitives.append(color);

        List<PlotData> data = new ArrayList<>();

        // Generate data dynamically assigning iterators using Block to support HoldAll semantics
        for (INumber iv : iValues) {
          IAST iSet = F.Set(iVar, iv);

          if (jValues != null && jVar != null) {
            for (INumber jv : jValues) {
              IAST jSet = F.Set(jVar, jv);
              IAST blockVars = F.List(iSet, jSet);
              IExpr result = engine.evaluate(F.Block(blockVars, f));
              result = engine.evalN(result); // safely evaluate symbolic constants

              if (result.isNumber()) {
                double z = result.evalDouble();
                data.add(new PlotData(iv.evalDouble(), jv.evalDouble(), z, color));
              }
            }
          } else {
            // 1D iterator creating 3D sequence
            IAST blockVars = F.List(iSet);
            IExpr result = engine.evaluate(F.Block(blockVars, f));
            result = engine.evalN(result);

            if (result.isList() && ((IAST) result).size() >= 4) {
              double px = ((IAST) result).arg1().evalDouble();
              double py = ((IAST) result).arg2().evalDouble();
              double pz = ((IAST) result).arg3().evalDouble();
              data.add(new PlotData(px, py, pz, color));
            }
          }
        }

        // Apply Automatic PlotRange Clamping (Interquartile Range for extreme outliers)
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        if (autoPlotRange && !data.isEmpty()) {
          double[] zArr = new double[data.size()];
          for (int i = 0; i < data.size(); i++) {
            zArr[i] = data.get(i).z;
          }
          Arrays.sort(zArr);
          minZ = zArr[0];
          maxZ = zArr[zArr.length - 1];

          if (zArr.length > 10) {
            double q1 = zArr[(int) (zArr.length * 0.25)];
            double q3 = zArr[(int) (zArr.length * 0.75)];
            double iqr = q3 - q1;
            double lower = q1 - 1.5 * iqr;
            double upper = q3 + 1.5 * iqr;
            if (lower > minZ)
              minZ = lower;
            if (upper < maxZ)
              maxZ = upper;
          }
        }

        // Construct graphics layout
        for (PlotData pd : data) {
          double z = pd.z;
          if (autoPlotRange) {
            if (z > maxZ)
              z = maxZ;
            if (z < minZ)
              z = minZ;
          }
          if (isLogZ && z <= 0)
            continue;

          double x = pd.x;
          double y = pd.y;

          if (useThinLineStyle) {
            // Point + Thin Line
            double thinW = extentX * 0.1;
            IExpr pMin = F.List(F.num(x - thinW), F.num(y - thinW), F.num(zBase));
            IExpr pMax = F.List(F.num(x + thinW), F.num(y + thinW), F.num(z));
            primitives.append(F.Cuboid(pMin, pMax));
            primitives.append(F.Point(F.List(F.num(x), F.num(y), F.num(z))));
          } else {
            // Full Bar
            IExpr pMin = F.List(F.num(x - extentX), F.num(y - extentY), F.num(zBase));
            IExpr pMax = F.List(F.num(x + extentX), F.num(y + extentY), F.num(z));
            primitives.append(F.Cuboid(pMin, pMax));
          }
        }
        graphicsList.append(primitives);
      }

    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      return Errors.printMessage(S.DiscretePlot3D, rex);
    }

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsList);
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(0.5))));
    result.append(F.Rule(S.ScalingFunctions, options[4]));
    result.append(F.Rule(S.PlotRange, options[1])); // Handled safely now via clamps
    result.append(F.Rule(S.Axes, options[5]));

    if (argSize > 3) {
      for (int k = 4; k < ast.size(); k++)
        result.append(ast.get(k));
    }

    return result;
  }

  private List<INumber> parseIterator(IExpr iter, EvalEngine engine) {
    List<INumber> values = new ArrayList<>();
    if (!iter.isList()) {
      return values;
    }
    IAST list = (IAST) iter;

    if (list.argSize() == 2) {
      IExpr arg2 = engine.evaluate(list.arg2());
      if (arg2.isList()) {
        IAST valList = (IAST) arg2;
        for (int k = 1; k < valList.size(); k++) {
          IExpr v = engine.evaluate(valList.get(k));
          if (v instanceof INumber) {
            values.add((INumber) v);
          }
        }
      } else {
        IInteger v = F.C1;
        if (arg2 instanceof INumber) {
          INumber max = (INumber) arg2;
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.inc();
          }
        }
      }
    } else if (list.argSize() >= 3) {
      IExpr arg2 = engine.evaluate(list.arg2());
      IExpr arg3 = engine.evaluate(list.arg3());
      if (arg2 instanceof INumber && arg3 instanceof INumber) {
        INumber min = (INumber) arg2;
        INumber max = (INumber) arg3;
        INumber step = F.C1;

        if (list.argSize() >= 4) {
          IExpr arg4 = engine.evaluate(list.arg4());
          if (arg4 instanceof INumber) {
            step = (INumber) arg4;
          }
        }

        INumber v = min;
        if (step.isNegative()) {
          while (max.lessThan(v).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        } else {
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        }
      }
    }
    return values;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.ExtentSize, S.PlotRange, S.ColorFunction, S.PlotLegends,
            S.ScalingFunctions, S.Axes},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.None, S.None, S.True});
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}