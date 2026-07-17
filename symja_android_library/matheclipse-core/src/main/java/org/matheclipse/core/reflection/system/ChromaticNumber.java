package org.matheclipse.core.reflection.system;

import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.BrownBacktrackColoring;
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
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns the chromatic number of a graph (the minimum number of colors needed to color the
 * vertices such that no two adjacent vertices share the same color).
 */
public class ChromaticNumber extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();

      // $Failed if self-loops are present
      if (GraphTests.hasSelfLoops(g)) {
        return S.$Failed;
      }

      // Convert to a simple undirected graph for the coloring algorithm
      Graph<IExpr, ExprEdge> undirected = new DefaultUndirectedGraph<>(ExprEdge.class);
      Graphs.addAllVertices(undirected, g.vertexSet());
      Set<? extends IExprEdge> graphSet = (Set<IExprEdge>) g.edgeSet();
      for (IExprEdge e : graphSet) {
        IExpr u = e.lhs();
        IExpr v = e.rhs();
        if (!u.equals(v)) { 
          undirected.addEdge(u, v);
        }
      }

      // Edge case: Empty graph
      if (undirected.vertexSet().isEmpty()) {
        return F.C0;
      }

      // Edge case: Graph with vertices but no edges requires exactly 1 color
      if (undirected.edgeSet().isEmpty()) {
        return F.C1;
      }

      // Execute exact coloring algorithm
      BrownBacktrackColoring<IExpr, ExprEdge> alg = new BrownBacktrackColoring<>(undirected);
      return F.ZZ(alg.getColoring().getNumberColors());
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