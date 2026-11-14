package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.graphics.GraphLayouts;

/**
 * <code>LayeredGraphPlot(g)</code> and <code>LayeredGraphPlot({v1 -&gt; w1, ...}, options...)</code>
 * - the picture <code>GraphPlot</code> draws of the graph with its vertices in layers along the
 * directions of the edges: the vertices no edge points to on top, every other vertex one layer
 * below the longest chain of edges leading to it, the connected pieces of the graph packed apart.
 * <code>VertexCoordinates</code> or <code>GraphLayout</code> given as options take the place of
 * the layered positions.
 */
public final class LayeredGraphPlot extends AbstractCoreFunctionEvaluator {

  public LayeredGraphPlot() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IASTMutable evaluated = engine.evalArgs(ast, ISymbol.NOATTRIBUTE, false);
    IAST call = evaluated.isPresent() ? evaluated : ast;
    if (call.argSize() < 1) {
      return evaluated;
    }
    for (int i = 2; i < call.size(); i++) {
      IExpr option = call.get(i);
      if (!option.isRuleAST()) {
        return evaluated;
      }
    }
    // {{edge1, label1}, ...}: the edges, with their labels unless EdgeLabeling -> False
    IExpr graphArg = GraphFunctions.labelledEdgesGraph(call.arg1(), call, engine);
    GraphExpr<?> graph = GraphExpr.newInstance(graphArg);
    if (graph == null) {
      return evaluated;
    }
    IASTAppendable plot = F.ast(S.GraphPlot, call.size() + 1);
    plot.append(graphArg);
    boolean placed = false;
    for (int i = 2; i < call.size(); i++) {
      IExpr option = call.get(i);
      if (GraphFunctions.isEdgeLabelingOption(option)) {
        // already applied to the edges
        continue;
      }
      placed |= option.first() == S.VertexCoordinates || option.first() == S.GraphLayout;
      plot.append(option);
    }
    if (!placed) {
      plot.append(F.Rule(S.VertexCoordinates,
          GraphLayouts.layeredDigraphCoordinates(graph.toData())));
    }
    IExpr picture = engine.evaluate(plot);
    return picture.isAST(S.Graphics) ? picture : evaluated;
  }
}
