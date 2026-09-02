package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.SparseEdmondsMaximumCardinalityMatching;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;

/**
 * <code>FindEdgeCover(graph)</code> - an edge cover of <code>graph</code> with a minimum number of
 * edges: a set of edges touching every vertex.
 *
 * <p>
 * A vertex with no incident edge cannot be covered at all, so such a graph gives <code>{}</code>.
 * Otherwise the minimum is reached through a maximum matching, by Gallai's theorem: the matching
 * covers two vertices per edge, and one further edge is then needed for each vertex it missed, which
 * makes the cover exactly <code>VertexCount - matching size</code> edges and no smaller cover
 * exists.
 *
 * <p>
 * The edges are reported in <code>EdgeList</code> order and in the form the graph uses, so a
 * directed graph gives back directed edges. Edge directions and edge weights do not constrain the
 * cover - only which vertices an edge touches does.
 */
public class FindEdgeCover extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    IAST vertexList = GraphExpr.vertexToIExpr(g);
    if (vertexList.argSize() == 0) {
      return F.CEmptyList;
    }
    @SuppressWarnings("unchecked")
    List<IExprEdge> originalEdges = new ArrayList<IExprEdge>((Set<IExprEdge>) g.edgeSet());
    if (originalEdges.isEmpty()) {
      return F.CEmptyList; // nothing to cover the vertices with
    }

    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);
    MatchingAlgorithm<IExpr, ExprEdge> algorithm =
        new SparseEdmondsMaximumCardinalityMatching<IExpr, ExprEdge>(undirected);
    MatchingAlgorithm.Matching<IExpr, ExprEdge> matching = algorithm.getMatching();

    Set<IExpr> covered = new HashSet<IExpr>();
    Set<Integer> chosen = new HashSet<Integer>();
    for (ExprEdge edge : matching.getEdges()) {
      IExpr u = undirected.getEdgeSource(edge);
      IExpr v = undirected.getEdgeTarget(edge);
      int index = indexOfEdge(originalEdges, u, v);
      if (index >= 0) {
        chosen.add(Integer.valueOf(index));
        covered.add(u);
        covered.add(v);
      }
    }

    // one more edge for every vertex the matching missed - a self loop counts, since it touches its
    // own vertex, and is the only thing that can cover a vertex carrying nothing else
    for (int i = 1; i <= vertexList.argSize(); i++) {
      IExpr vertex = vertexList.get(i);
      if (covered.contains(vertex)) {
        continue;
      }
      int index = indexOfIncidentEdge(originalEdges, vertex);
      if (index < 0) {
        return F.CEmptyList; // this vertex has no edge at all, so no cover exists
      }
      IExprEdge edge = originalEdges.get(index);
      chosen.add(Integer.valueOf(index));
      covered.add(edge.lhs());
      covered.add(edge.rhs());
    }

    // GraphExpr.edgesToIExpr walks the same edge set in the same order EdgeList does
    IAST edgeExpressions = GraphExpr.edgesToIExpr(g)[0];
    IASTAppendable result = F.ListAlloc(chosen.size());
    for (int i = 0; i < originalEdges.size(); i++) {
      if (chosen.contains(Integer.valueOf(i))) {
        result.append(edgeExpressions.get(i + 1));
      }
    }
    return result;
  }

  private static int indexOfEdge(List<IExprEdge> edges, IExpr u, IExpr v) {
    for (int i = 0; i < edges.size(); i++) {
      IExprEdge edge = edges.get(i);
      if ((edge.lhs().equals(u) && edge.rhs().equals(v))
          || (edge.lhs().equals(v) && edge.rhs().equals(u))) {
        return i;
      }
    }
    return -1;
  }

  private static int indexOfIncidentEdge(List<IExprEdge> edges, IExpr vertex) {
    int loop = -1;
    for (int i = 0; i < edges.size(); i++) {
      IExprEdge edge = edges.get(i);
      boolean self = edge.lhs().equals(edge.rhs());
      if (edge.lhs().equals(vertex) || edge.rhs().equals(vertex)) {
        if (!self) {
          // an ordinary edge covers a second vertex too, so prefer it over a loop
          return i;
        }
        loop = i;
      }
    }
    return loop;
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
