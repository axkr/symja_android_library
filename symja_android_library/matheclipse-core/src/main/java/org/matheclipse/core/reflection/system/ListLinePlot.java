package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points as a single line */
public class ListLinePlot extends ListPlot {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Constructor for the singleton */
  // public final static ListLinePlot CONST = new ListLinePlot();

  public ListLinePlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // if (Config.USE_MANIPULATE_JS) {
    // IExpr temp = S.Manipulate.of(engine, ast);
    // if (temp.headID() == ID.JSFormData) {
    // return temp;
    // }
    // return F.NIL;
    // }

    // boundingbox an array of double values (length 4) which describes the bounding box
    // <code>[xMin, xMax, yMin, yMax]</code>
    double[] boundingbox =
        new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};
    int[] colour = new int[] {1};
    IAST graphicsPrimitives = plot(ast, boundingbox, colour, true, engine);
    if (graphicsPrimitives.isPresent()) {
      addPadding(boundingbox);
      IExpr result = F.Graphics(graphicsPrimitives, //
          F.Rule(S.Axes, S.True),
          F.Rule(S.PlotRange, F.List(F.List(F.num(boundingbox[0]), F.num(boundingbox[1])),
              F.List(F.num(boundingbox[2]), F.num(boundingbox[3])))));
      return result;
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
