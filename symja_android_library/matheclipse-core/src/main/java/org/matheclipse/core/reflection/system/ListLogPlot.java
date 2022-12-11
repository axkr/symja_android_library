package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ListLogPlot extends ListPlot {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions();
    graphicsOptions.setYFunction(y -> F.Log(y));
    int[] colour = new int[] {1};
    IAST graphicsPrimitives = plot(ast, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      double[] boundingbox = graphicsOptions.boundingBox();
      IExpr result = F.Graphics(graphicsPrimitives, //
          F.Rule(S.ScalingFunctions, //
              F.List(S.None, F.stringx("Log"))), //
          F.Rule(S.Axes, S.True),
          F.Rule(S.PlotRange, F.List(F.List(F.num(boundingbox[0]), F.num(boundingbox[1])),
              F.List(F.num(boundingbox[2]), F.num(boundingbox[3])))));
      return result;
    }

    return F.NIL;
  }
}
