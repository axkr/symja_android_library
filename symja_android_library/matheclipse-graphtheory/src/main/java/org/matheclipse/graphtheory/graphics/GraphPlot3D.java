package org.matheclipse.graphtheory.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>GraphPlot3D(graph)</code> or <code>GraphPlot3D({v1 -> v2, ...})</code> - a
 * <code>Graphics3D</code> of the graph in a three dimensional spring embedding: a sphere for each
 * vertex and a <code>Line</code> for each edge.
 *
 * <p>
 * It draws the graph like {@link Graph3D}, but a directed edge is a plain line unless
 * <code>DirectedEdges -> True</code> is given.
 */
public class GraphPlot3D extends Graph3D {

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ast.argSize() >= 1
        && !ast.exists(x -> x.isRuleAST() && x.first() == S.DirectedEdges)) {
      IASTAppendable withLines = ast.copyAppendable();
      withLines.append(F.Rule(S.DirectedEdges, S.False));
      ast = withLines;
    }
    return super.evaluate(ast, argSize, options, engine, originalAST);
  }
}
