package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
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
 * Contracts specified vertices into a single vertex.
 */
public class VertexContract extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      if (!arg2.isList()) {
        return F.NIL;
      }

      IAST list = (IAST) arg2;
      Map<IExpr, IExpr> aliasMap = new HashMap<>();

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();

      processContractLists(g, list, aliasMap);

      return buildContractedGraph(gex, g, aliasMap);
    }
    return F.NIL;
  }

  private void processContractLists(Graph<IExpr, ?> g, IAST list, Map<IExpr, IExpr> aliasMap) {
    if (list.size() <= 1)
      return;

    boolean isListOfLists = false;
    if (list.arg1().isList() && !g.containsVertex(list.arg1())) {
      isListOfLists = true;
    }

    if (isListOfLists) {
      for (IExpr sub : list) {
        if (sub.isList()) {
          contractSet((IAST) sub, aliasMap);
        }
      }
    } else {
      contractSet(list, aliasMap);
    }
  }

  private void contractSet(IAST vertices, Map<IExpr, IExpr> aliasMap) {
    if (vertices.argSize() == 0)
      return;
    // The first vertex in the list acts as the representative
    IExpr mainVertex = getRep(aliasMap, vertices.arg1());
    for (int i = 2; i <= vertices.argSize(); i++) {
      IExpr v = vertices.get(i);
      IExpr repV = getRep(aliasMap, v);
      if (!mainVertex.equals(repV)) {
        aliasMap.put(repV, mainVertex);
      }
    }
  }

  /**
   * Package-private union-find helper for mapping contracted vertices.
   */
  static IExpr getRep(Map<IExpr, IExpr> aliasMap, IExpr v) {
    IExpr current = v;
    while (aliasMap.containsKey(current)) {
      current = aliasMap.get(current);
    }
    // Path compression
    if (!v.equals(current)) {
      aliasMap.put(v, current);
    }
    return current;
  }

  /**
   * Package-private graph builder shared by VertexContract and EdgeContract. Processes edges
   * sequentially to match standard evaluation priority.
   */
  @SuppressWarnings("unchecked")
  static IExpr buildContractedGraph(GraphExpr<?> gex, Graph<IExpr, ?> g,
      Map<IExpr, IExpr> aliasMap) {
    boolean isDirected = g.getType().isDirected();
    boolean isWeighted = gex.isWeightedGraph();

    if (isWeighted) {
      Graph<IExpr, ExprWeightedEdge> origG = (Graph<IExpr, ExprWeightedEdge>) g;
      Graph<IExpr, ExprWeightedEdge> resultGraph =
          isDirected ? new DefaultDirectedWeightedGraph<>(ExprWeightedEdge.class)
              : new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);

      for (IExpr v : origG.vertexSet()) {
        resultGraph.addVertex(getRep(aliasMap, v));
      }

      // Single-pass processing preserves original insertion priority
      for (ExprWeightedEdge edge : origG.edgeSet()) {
        IExpr origS = origG.getEdgeSource(edge);
        IExpr origT = origG.getEdgeTarget(edge);
        IExpr repS = getRep(aliasMap, origS);
        IExpr repT = getRep(aliasMap, origT);

        // STRICT CHECK: Drop any edge that collapses into a NEW self-loop due to contraction
        if (!repS.equals(repT) || origS.equals(origT)) {
          try {
            ExprWeightedEdge newEdge = resultGraph.addEdge(repS, repT);
            if (newEdge != null) {
              resultGraph.setEdgeWeight(newEdge, origG.getEdgeWeight(edge));
            }
          } catch (IllegalArgumentException e) {
            // Ignore if edge already exists
          }
        }
      }
      return GraphExpr.newInstance(resultGraph);
    } else {
      Graph<IExpr, ExprEdge> origG = (Graph<IExpr, ExprEdge>) g;
      Graph<IExpr, ExprEdge> resultGraph = isDirected ? new DefaultDirectedGraph<>(ExprEdge.class)
          : new DefaultUndirectedGraph<>(ExprEdge.class);

      for (IExpr v : origG.vertexSet()) {
        resultGraph.addVertex(getRep(aliasMap, v));
      }

      // Single-pass processing preserves original insertion priority
      for (ExprEdge edge : origG.edgeSet()) {
        IExpr origS = origG.getEdgeSource(edge);
        IExpr origT = origG.getEdgeTarget(edge);
        IExpr repS = getRep(aliasMap, origS);
        IExpr repT = getRep(aliasMap, origT);

        // STRICT CHECK: Drop any edge that collapses into a NEW self-loop due to contraction
        if (!repS.equals(repT) || origS.equals(origT)) {
          try {
            resultGraph.addEdge(repS, repT);
          } catch (IllegalArgumentException e) {
            // Ignore if edge already exists
          }
        }
      }
      return GraphExpr.newInstance(resultGraph);
    }
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