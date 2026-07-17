package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.BrownBacktrackColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns a minimum vertex coloring for a graph, representing the color of each vertex in the same
 * order as VertexList.
 */
public class FindVertexColoring extends AbstractFunctionEvaluator {

  @Override
  @SuppressWarnings("unchecked")
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      Graph<IExpr, ?> g = gex.toData();

      // Mathematica returns $Failed if self-loops are present
      if (GraphTests.hasSelfLoops(g)) {
        return S.$Failed;
      }

      // Convert to a simple undirected graph for the coloring algorithm
      Graph<IExpr, ExprEdge> undirected = new DefaultUndirectedGraph<>(ExprEdge.class);
      Set<IExpr> vertexSet = g.vertexSet();
      if (vertexSet.size() == 0) {
        return F.CEmptyList;
      }
      Graphs.addAllVertices(undirected, vertexSet);
      Set<? extends IExprEdge> graphSet = (Set<IExprEdge>) g.edgeSet();
      for (IExprEdge e : graphSet) {
        IExpr u = e.lhs();
        IExpr v = e.rhs();
        if (!u.equals(v)) {
          undirected.addEdge(u, v);
        }
      }

      // Execute exact coloring algorithm
      BrownBacktrackColoring<IExpr, ExprEdge> alg = new BrownBacktrackColoring<>(undirected);
      VertexColoringAlgorithm.Coloring<IExpr> coloring = alg.getColoring();
      Map<IExpr, Integer> colorMap = coloring.getColors();

      // Mathematica returns the colors corresponding to the vertices in the order
      // they are listed in VertexList(graph)
      IAST vertices = GraphExpr.vertexToIExpr(g);
      IASTAppendable result = F.ListAlloc(vertices.argSize());

      // Map raw JGraphT colors to canonical 1-based colors
      Map<Integer, Integer> canonicalColors = new HashMap<>();
      int nextColor = 1;

      for (int i = 1; i <= vertices.argSize(); i++) {
        Integer rawColor = colorMap.get(vertices.get(i));

        if (rawColor != null) {
          Integer canonicalColor = canonicalColors.get(rawColor);
          if (canonicalColor == null) {
            canonicalColor = nextColor++;
            canonicalColors.put(rawColor, canonicalColor);
          }
          result.append(F.ZZ(canonicalColor));
        } else {
          // Fallback for isolated vertices
          result.append(F.C1);
        }
      }
      return result;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
