package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot an array of coordinates/heights in 3 dimesnsions */
public class ListPlot3D extends AbstractEvaluator {
  public ListPlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST values = (IAST) ast.arg1();

    IAST polygons;

    if (values.argSize() == 3 && ((IAST) values.arg1()).argSize() == 3) {
      // Draw a triagle with x, y and z coordinates.
      polygons = F.Polygon(F.List(
        F.List(
          ((IAST) values.arg1()).arg1(),
          ((IAST) values.arg2()).arg1(),
          ((IAST) values.arg3()).arg1()
        ),
        F.List(
          ((IAST) values.arg1()).arg2(),
          ((IAST) values.arg2()).arg2(),
          ((IAST) values.arg3()).arg2()
        ),
        F.List(
          ((IAST) values.arg1()).arg3(),
          ((IAST) values.arg2()).arg3(),
          ((IAST) values.arg3()).arg3()
        )
      ));
    } else {
      // Draw polygons given just the z value in a grid of size
      // (heights.argSize() - 1) * (heights.arg1().argSize() - 1)
      // 2 polygons per square
      polygons = F.ListAlloc(
        (values.argSize() - 1) * (((IAST) values).arg1().argSize() - 1) * 2
      );

      double minHeight = ((ISignedNumber) (((IAST) values.arg1()).arg1())).doubleValue();
      double maxHeight = ((ISignedNumber) (((IAST) values.arg1()).arg1())).doubleValue();

      for (int i = 1; i <= values.argSize(); i++) {
        for (int j = 1; j <= values.arg1().argSize(); j++) {
          double height = ((ISignedNumber) ((IAST) values.get(i)).get(j)).doubleValue();

          if (height < minHeight) minHeight = height;
          if (height > maxHeight) maxHeight = height;
        }
      }

      // deltaHeight is used to normalize the heights.
      ISignedNumber deltaHeight = F.num(maxHeight - minHeight);

      // As i and j start at 1, the last element is ignored (as it should be).
      for (int i = 1; i < values.argSize(); i++) {
        // This supposes all sublists have the same size.
        for (int j = 1; j < values.arg1().argSize(); j++) {
          ((IASTAppendable) polygons).append(F.Polygon(F.List(
            F.List(
              F.num(i),
              F.num(j),
              ((IAST) values.get(i)).get(j).divide(deltaHeight)
            ),
            F.List(
              F.num(i + 1),
              F.num(j + 1),
              ((IAST) values.get(i + 1)).get(j + 1).divide(deltaHeight)
            ),
            F.List(
              F.num(i),
              F.num(j + 1),
              ((IAST) values.get(i)).get(j + 1).divide(deltaHeight)
            )
          )));
          ((IASTAppendable) polygons).append(F.Polygon(F.List(
            F.List(
              F.num(i),
              F.num(j),
              ((IAST) values.get(i)).get(j).divide(deltaHeight)
            ),
            F.List(
              F.num(i + 1),
              F.num(j + 1),
              ((IAST) values.get(i + 1)).get(j + 1).divide(deltaHeight)
            ),
            F.List(
              F.num(i + 1),
              F.num(j),
              ((IAST) values.get(i + 1)).get(j).divide(deltaHeight)
            )
          )));
        }
      }
    }

    return F.Graphics3D(polygons);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
