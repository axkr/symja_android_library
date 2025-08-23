package org.matheclipse.core.bridge.usr;

import java.io.IOException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.itp.BSplineInterpolation;

public class ListDensityPlotDemo {

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    // data = Table[Sin[j^2 + i], {i, 0, Pi, Pi/5}, {j, 0, Pi, Pi/5}];
    IAST d = F.subdivide(Math.PI, 5);
    IAST matrix =
        F.matrix((i, j) -> S.Sin.of(engine, d.get(j + 1).multiply(d.get(j + 1)).add(d.get(i + 1))), //
            6, 6);
    System.out.println(LinearAlgebraUtil.dimensions(matrix));
    for (int degree = 0; degree < 5; ++degree) {
      BSplineInterpolation interpolation = new BSplineInterpolation(degree, matrix, false);
      IAST x = F.subdivide(5, 50);
      IAST y = (IAST) S.Reverse.of(engine, x);
      IAST eval = F.matrix((i, j) -> interpolation.get( //
          F.List(y.get(i + 1), x.get(j + 1))), x.argSize(), x.argSize());
      IAST tensor =
          ((IAST) S.Rescale.of(engine, eval)).mapLeaf(S.List, ColorDataGradients.SOUTH_WEST);
      S.Export.of(engine,
          HomeDirectory.Pictures(ListDensityPlotDemo.class.getSimpleName() + degree + ".png"),
          tensor);
    }
  }
}
