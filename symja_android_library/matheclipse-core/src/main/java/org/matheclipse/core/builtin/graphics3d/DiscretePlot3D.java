package org.matheclipse.core.builtin.graphics3d;

import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class DiscretePlot3D extends AbstractFunctionOptionEvaluator {

  public DiscretePlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    // DiscretePlot3D[f, {i, ...}, {j, ...}]
    if (argSize < 3) {
      return F.NIL;
    }

    IExpr function = ast.arg1();
    IExpr iRange = ast.arg2();
    IExpr jRange = ast.arg3();

    if (!iRange.isList() || !jRange.isList()) {
      return F.NIL;
    }

    // Parse Options
    double extentX = 0.4;
    double extentY = 0.4;
    boolean useThinLineStyle = false; // Default for Automatic/None

    IExpr extentOption = options[0]; // ExtentSize
    if (extentOption.equals(S.None)) {
      useThinLineStyle = true;
    } else if (extentOption.equals(S.Automatic)) {
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

    if (scalingOption.isString()) {
      if ("Log".equalsIgnoreCase(scalingOption.toString())) {
        isLogZ = true;
      }
    } else if (scalingOption.isList()) {
      IAST sList = (IAST) scalingOption;
      if (sList.size() >= 4) {
        IExpr zScale = sList.get(3);
        if (zScale.isString() && "Log".equalsIgnoreCase(zScale.toString())) {
          isLogZ = true;
        }
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
      List<INumber> jValues = parseIterator(jRange, engine);
      ISymbol iVar = (ISymbol) ((IAST) iRange).arg1();
      ISymbol jVar = (ISymbol) ((IAST) jRange).arg1();

      for (int funcIdx = 1; funcIdx < functions.size(); funcIdx++) {
        IExpr f = functions.get(funcIdx);
        IASTAppendable primitives = F.ListAlloc();

        IExpr color = defaultColors[(funcIdx - 1) % defaultColors.length];
        primitives.append(color);

        for (INumber iv : iValues) {
          for (INumber jv : jValues) {
            IExpr val = F.subst(f, F.List(F.Rule(iVar, iv), F.Rule(jVar, jv)));
            IExpr result = engine.evaluate(val);

            if (result.isNumber()) {
              double z = result.evalDouble();

              if (isLogZ && z <= 0)
                continue;

              if (useThinLineStyle) {
                // Point + Thin Line
                double thinW = 0.02;
                IExpr pMin = F.List(iv.subtract(thinW), jv.subtract(thinW), F.num(zBase));
                IExpr pMax = F.List(iv.add(thinW), jv.add(thinW), F.num(z));
                primitives.append(F.Cuboid(pMin, pMax));
                primitives.append(F.Point(F.List(iv, jv, F.num(z))));
              } else {
                // Full Bar
                IExpr pMin = F.List(iv.subtract(extentX), jv.subtract(extentY), F.num(zBase));
                IExpr pMax = F.List(iv.add(extentX), jv.add(extentY), F.num(z));
                primitives.append(F.Cuboid(pMin, pMax));
              }
            }
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
    result.append(F.Rule(S.Axes, options[5])); // Pass resolved Axes option

    if (argSize > 3) {
      for (int k = 4; k < ast.size(); k++)
        result.append(ast.get(k));
    }

    return result;
  }

  private java.util.List<INumber> parseIterator(IExpr iter, EvalEngine engine) {
    java.util.List<INumber> values = new java.util.ArrayList<>();
    if (!iter.isList()) {
      return values;
    }
    IAST list = (IAST) iter;
    if (list.argSize() == 2) {
      if (list.arg2().isList()) {
        IAST valList = (IAST) list.arg2();
        for (int k = 1; k < valList.size(); k++) {
          values.add(valList.get(k).evalNumber());
        }
      } else {
        IInteger v = F.C1;
        INumber max = list.arg2().evalNumber();
        while (v.lessThan(max).isTrue() || v.equals(max)) {
          values.add(v);
          v = v.inc();
        }
      }
    } else if (list.argSize() >= 3) {
      INumber min = list.arg2().evalNumber();
      INumber max = list.arg3().evalNumber();
      INumber step = F.C1;
      if (list.argSize() >= 4) {
        step = list.arg4().evalNumber();
      }
      INumber v = min;
      while (v.lessThan(max).isTrue() || v.equals(max)) {
        values.add(v);
        v = v.plus(step);
      }
    }
    return values;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.ExtentSize, S.PlotRange, S.ColorFunction, S.PlotLegends,
            S.ScalingFunctions, S.Axes},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.None, S.None, S.True});
  }
}