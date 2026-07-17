package org.matheclipse.core.reflection.system;

import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns the chromatic polynomial of a graph in terms of the variable z. Evaluates the
 * Deletion-Contraction formula recursively.
 */
public class ChromaticPolynomial extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();

      // Graphs with self-loops cannot be properly colored, polynomial is 0.
      if (GraphTests.hasSelfLoops(g)) {
        return F.C0;
      }

      // Convert to a simple undirected graph
      // (Edge directions and multiple edges do not affect the chromatic polynomial)
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

      IExpr z = ast.arg2();
      IExpr result = getPolynomial(undirected, z, engine);

      // Expand the symbolic polynomial for a standardized output form
      if (!z.isNumber()) {
        return engine.evaluate(F.Expand(result));
      }
      return result;
    }
    return F.NIL;
  }

  private IExpr getPolynomial(Graph<IExpr, ExprEdge> g, IExpr z, EvalEngine engine) {
    int m = g.edgeSet().size();

    // Base Case: Empty graph with n vertices -> z^n
    if (m == 0) {
      return engine.evaluate(F.Power(z, F.ZZ(g.vertexSet().size())));
    }

    // Pick any edge for deletion-contraction
    ExprEdge edge = g.edgeSet().iterator().next();
    IExpr u = g.getEdgeSource(edge);
    IExpr v = g.getEdgeTarget(edge);

    // 1. Deletion: G - e
    Graph<IExpr, ExprEdge> gMinus = new DefaultUndirectedGraph<>(ExprEdge.class);
    Graphs.addAllVertices(gMinus, g.vertexSet());
    Graphs.addAllEdges(gMinus, g, g.edgeSet());
    gMinus.removeEdge(u, v);

    // 2. Contraction: G / e
    Graph<IExpr, ExprEdge> gContract = new DefaultUndirectedGraph<>(ExprEdge.class);
    Graphs.addAllVertices(gContract, g.vertexSet());
    Graphs.addAllEdges(gContract, g, g.edgeSet());

    // Merge vertex v into u
    gContract.removeVertex(v);
    for (ExprEdge e : g.edgesOf(v)) {
      IExpr neighbor = Graphs.getOppositeVertex(g, e, v);
      if (!neighbor.equals(u)) {
        gContract.addEdge(u, neighbor);
      }
    }

    IExpr pMinus = getPolynomial(gMinus, z, engine);
    IExpr pContract = getPolynomial(gContract, z, engine);

    // Recursion: P(G) = P(G - e) - P(G / e)
    return engine.evaluate(F.Subtract(pMinus, pContract));
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