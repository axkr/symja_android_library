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
 * <code>FindIndependentEdgeSet(graph)</code> - an independent edge set of <code>graph</code> with a
 * maximum number of edges, also known as a maximum matching: a set of edges no two of which are
 * incident to the same vertex.
 *
 * <p>
 * The edges are reported in <code>EdgeList</code> order and in the form the graph uses, so a
 * directed graph gives back directed edges. Edge direction and edge weights do not constrain the
 * matching - only which vertices an edge touches does. A self loop is incident to its vertex twice
 * and so can never be matched.
 */
public class FindIndependentEdgeSet extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    @SuppressWarnings("unchecked")
    List<IExprEdge> originalEdges = new ArrayList<IExprEdge>((Set<IExprEdge>) g.edgeSet());
    if (originalEdges.isEmpty()) {
      return F.CEmptyList;
    }

    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);
    MatchingAlgorithm<IExpr, ExprEdge> algorithm =
        new SparseEdmondsMaximumCardinalityMatching<IExpr, ExprEdge>(undirected);
    MatchingAlgorithm.Matching<IExpr, ExprEdge> matching = algorithm.getMatching();

    // the matching lives on the undirected copy, so carry it back as unordered vertex pairs
    Set<List<IExpr>> matched = new HashSet<List<IExpr>>();
    for (ExprEdge edge : matching.getEdges()) {
      matched.add(unordered(undirected.getEdgeSource(edge), undirected.getEdgeTarget(edge)));
    }

    // GraphExpr.edgesToIExpr walks the same edge set in the same order EdgeList does, so index i
    // here is the edge EdgeList reports at position i + 1
    IAST edgeExpressions = GraphExpr.edgesToIExpr(g)[0];
    IASTAppendable result = F.ListAlloc(matched.size());
    for (int i = 0; i < originalEdges.size(); i++) {
      IExprEdge edge = originalEdges.get(i);
      if (edge.lhs().equals(edge.rhs())) {
        continue; // a self loop touches its vertex twice
      }
      // remove, so a pair of opposite directed edges contributes only its first edge
      if (matched.remove(unordered(edge.lhs(), edge.rhs()))) {
        result.append(edgeExpressions.get(i + 1));
      }
    }
    return result;
  }

  private static List<IExpr> unordered(IExpr u, IExpr v) {
    List<IExpr> pair = new ArrayList<IExpr>(2);
    if (u.compareTo(v) <= 0) {
      pair.add(u);
      pair.add(v);
    } else {
      pair.add(v);
      pair.add(u);
    }
    return pair;
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
