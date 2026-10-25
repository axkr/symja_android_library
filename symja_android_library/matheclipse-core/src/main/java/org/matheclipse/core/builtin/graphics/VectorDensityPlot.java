package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>VectorDensityPlot[{vx, vy}, {x, xmin, xmax}, {y, ymin, ymax}]</code> - the vector field
 * drawn as arrows on top of a density plot of its norm.
 *
 * <p>
 * The background is the <code>DensityPlot</code> of <code>Norm[{vx, vy}]</code> and the arrows are
 * the ones <code>VectorPlot</code> draws, in black unless <code>VectorColorFunction</code> or
 * <code>VectorStyle</code> asks for something else. The options of the arrows
 * (<code>VectorPoints</code>, <code>VectorScale</code>, <code>VectorColorFunction</code>,
 * <code>VectorStyle</code>) go to the arrows, <code>RegionFunction</code> to both, and every other
 * option to the density plot.
 */
public class VectorDensityPlot extends AbstractFunctionEvaluator {

  public VectorDensityPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 3 || !ast.arg1().isList2() || !ast.arg2().isList3()
        || !ast.arg3().isList3()) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    IASTAppendable vectorPlot = F.ast(S.VectorPlot);
    vectorPlot.append(field);
    vectorPlot.append(ast.arg2());
    vectorPlot.append(ast.arg3());
    IASTAppendable densityPlot = F.ast(S.DensityPlot);
    densityPlot.append(F.Norm(field));
    densityPlot.append(ast.arg2());
    densityPlot.append(ast.arg3());
    boolean arrowColors = false;
    for (int i = 4; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        return F.NIL;
      }
      IExpr key = option.first();
      if (key == S.VectorPoints || key == S.VectorScale || key == S.VectorColorFunction
          || key == S.VectorStyle) {
        arrowColors |= key == S.VectorColorFunction || key == S.VectorStyle;
        vectorPlot.append(option);
      } else if (key == S.RegionFunction) {
        vectorPlot.append(option);
        densityPlot.append(option);
      } else {
        densityPlot.append(option);
      }
    }
    if (!arrowColors) {
      vectorPlot.append(F.Rule(S.VectorColorFunction, S.None));
      vectorPlot.append(F.Rule(S.VectorStyle, S.Black));
    }
    IExpr density = engine.evaluate(densityPlot);
    IExpr arrows = engine.evaluate(vectorPlot);
    if (!density.isAST(S.Graphics) || !density.first().isList() || !arrows.isAST(S.Graphics)
        || !arrows.first().isList()) {
      return F.NIL;
    }
    // the arrows are drawn last, on top of the density
    IASTAppendable primitives = F.ListAlloc(2);
    primitives.appendArgs((IAST) density.first());
    primitives.append(arrows.first());
    return ((IAST) density).setAtCopy(1, primitives);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
