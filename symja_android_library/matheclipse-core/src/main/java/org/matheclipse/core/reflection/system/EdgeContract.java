package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.ExprWeightedEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Contracts specified edges by merging their incident vertices into a single vertex.
 */
public class EdgeContract extends AbstractFunctionEvaluator {

  @Override
  @SuppressWarnings("unchecked")
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      IExpr pattern = arg2.isList() ? F.Alternatives((IAST) arg2) : arg2;
      Map<IExpr, IExpr> aliasMap = new HashMap<>();

      Graph<IExpr, ?> g = gex.toData();
      boolean isDirected = g.getType().isDirected();

      if (gex.isWeightedGraph()) {
        Graph<IExpr, ExprWeightedEdge> origG = (Graph<IExpr, ExprWeightedEdge>) g;
        for (ExprWeightedEdge edge : origG.edgeSet()) {
          IExpr source = origG.getEdgeSource(edge);
          IExpr target = origG.getEdgeTarget(edge);

          IAST edgeExprCanonical =
              isDirected ? F.DirectedEdge(source, target) : F.UndirectedEdge(source, target);
          IAST edgeExprSugar = isDirected ? F.Rule(source, target) : F.TwoWayRule(source, target);

          if (engine.evaluate(F.MatchQ(edgeExprCanonical, pattern)).isTrue()
              || engine.evaluate(F.MatchQ(edgeExprSugar, pattern)).isTrue()) {

            IExpr repU = VertexContract.getRep(aliasMap, source);
            IExpr repV = VertexContract.getRep(aliasMap, target);
            if (!repU.equals(repV)) {
              // By convention, merge into the source vertex
              aliasMap.put(repV, repU);
            }
          }
        }
      } else {
        Graph<IExpr, ExprEdge> origG = (Graph<IExpr, ExprEdge>) g;
        for (ExprEdge edge : origG.edgeSet()) {
          IExpr source = origG.getEdgeSource(edge);
          IExpr target = origG.getEdgeTarget(edge);

          IAST edgeExprCanonical =
              isDirected ? F.DirectedEdge(source, target) : F.UndirectedEdge(source, target);
          IAST edgeExprSugar = isDirected ? F.Rule(source, target) : F.TwoWayRule(source, target);

          if (engine.evaluate(F.MatchQ(edgeExprCanonical, pattern)).isTrue()
              || engine.evaluate(F.MatchQ(edgeExprSugar, pattern)).isTrue()) {

            IExpr repU = VertexContract.getRep(aliasMap, source);
            IExpr repV = VertexContract.getRep(aliasMap, target);
            if (!repU.equals(repV)) {
              // By convention, merge into the source vertex
              aliasMap.put(repV, repU);
            }
          }
        }
      }

      // Delegate graph rebuilding to the prioritized builder
      return VertexContract.buildContractedGraph(gex, g, aliasMap);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
