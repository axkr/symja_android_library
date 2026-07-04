package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CirclePoints extends AbstractFunctionEvaluator {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr center = F.NIL;
    IExpr r = F.C1;
    IExpr theta = F.NIL;
    IExpr nExpr = F.NIL;

    int argSize = ast.argSize();

    // Parse the 1 to 3 arguments based on WL specifications
    if (argSize == 1) {
      nExpr = ast.arg1();
    } else if (argSize == 2) {
      IExpr arg1 = ast.arg1();
      nExpr = ast.arg2();
      if (arg1.isList2()) {
        r = ((IAST) arg1).arg1();
        theta = ((IAST) arg1).arg2();
      } else {
        r = arg1;
      }
    } else if (argSize == 3) {
      center = ast.arg1();
      IExpr arg2 = ast.arg2();
      nExpr = ast.arg3();
      if (arg2.isList2()) {
        r = ((IAST) arg2).arg1();
        theta = ((IAST) arg2).arg2();
      } else {
        r = arg2;
      }
    } else {
      return F.NIL;
    }

    if (nExpr.isReal()) {
      if (nExpr.isNegative()) {
        // Argument `1` should be a real non-negative number.
        return Errors.printMessage(ast.topHead(), "noneg", F.list(nExpr), engine);
      }
      int n = nExpr.toIntDefault();
      if (n > 0) {
        IExpr cx = F.C0;
        IExpr cy = F.C0;
        if (center.isPresent()) {
          if (center.isList2()) {
            cx = ((IAST) center).arg1();
            cy = ((IAST) center).arg2();
          } else {
            return F.NIL; // Center was provided but isn't a 2D coordinate list
          }
        }

        // If the starting angle isn't explicitly provided, use the default: Pi/n - Pi/2
        IExpr startAngle = theta;
        if (!startAngle.isPresent()) {
          startAngle = engine.evaluate(F.Plus(F.Times(F.QQ(1, n), S.Pi), F.Times(F.CN1D2, S.Pi)));
        }

        // The angle step between each point is (2/n)*Pi
        final IExpr angleStep = engine.evaluate(F.Times(F.QQ(2, n), S.Pi));

        // Create effectively final variables to pass into the lambda scope
        final IExpr finalCx = cx;
        final IExpr finalCy = cy;
        final IExpr finalR = r;
        final IExpr finalStart = startAngle;

        // Iterate exactly n times and convert polar specifications into Cartesian coordinates
        return F.mapRange(0, n, j -> {
          IExpr currentAngle = engine.evaluate(F.Plus(finalStart, F.Times(F.ZZ(j), angleStep)));
          IExpr cos = engine.evaluate(F.Cos(currentAngle));
          IExpr sin = engine.evaluate(F.Sin(currentAngle));
          IExpr px = engine.evaluate(F.Plus(finalCx, F.Times(finalR, cos)));
          IExpr py = engine.evaluate(F.Plus(finalCy, F.Times(finalR, sin)));
          return F.List(px, py);
        });

      } else if (n == 0) {
        return F.CEmptyList;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}