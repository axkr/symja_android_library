package org.matheclipse.core.builtin.graphics3d;

import java.util.List;
import org.matheclipse.core.builtin.graphics.RectangleChart;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RectangleChart3D({{x1, y1, z1}, {x2, y2, z2}, ...})</code> - a chart of cuboids with the
 * width <code>xi</code>, the height <code>yi</code> and the depth <code>zi</code>, side by side
 * along the x axis from <code>x = 0</code>.
 *
 * <p>
 * The cuboids are placed and coloured as the rectangles of <code>RectangleChart</code>.
 */
public class RectangleChart3D extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST datasets = RectangleChart.datasets(ast.arg1(), 3);
    if (datasets == null) {
      if (ast.arg1().isList()) {
        // `1` is not a valid dataset or a list of datasets.
        return Errors.printMessage(ast.topHead(), "ldata", F.list(ast.arg1()), engine);
      }
      return F.NIL;
    }
    int argSize = 1;
    while (argSize < ast.argSize() && !ast.get(argSize + 1).isRuleAST()) {
      argSize++;
    }
    IExpr barSpacing = GraphicsOptions.optionValue(ast, S.BarSpacing, S.Automatic);
    IExpr chartStyle = GraphicsOptions.optionValue(ast, S.ChartStyle, S.Automatic);
    List<RectangleChart.Box> boxes = RectangleChart.layout(datasets, 3, barSpacing);
    if (boxes.isEmpty()) {
      return F.NIL;
    }
    boolean byPosition = datasets.argSize() > 1 || !chartStyle.isAutomatic();
    IASTAppendable primitives = F.ListAlloc(boxes.size());
    for (RectangleChart.Box box : boxes) {
      PlotWrapper wrapper = PlotWrapper.of(box.item);
      IExpr color = wrapper.style.isPresent() ? wrapper.style
          : RectangleChart.color(chartStyle, byPosition ? box.position : 0);
      IAST cuboid = F.binaryAST2(S.Cuboid, F.List(F.num(box.from), F.CD0, F.CD0),
          F.List(F.num(box.to()), F.num(box.sizes[1]), F.num(box.sizes[2])));
      IAST group = F.List(color, cuboid);
      primitives.append(
          wrapper.hasTooltip() ? F.binaryAST2(S.Tooltip, group, wrapper.tooltip) : group);
    }
    IExpr[] defaults = {F.Rule(S.Lighting, F.stringx("Neutral"))};
    return Plot3DTools.graphics3D(primitives, ast, argSize, defaults, false);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
