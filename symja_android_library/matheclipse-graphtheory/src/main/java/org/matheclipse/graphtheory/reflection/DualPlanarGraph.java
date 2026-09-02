package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.PlanarFaceDecomposition;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>DualPlanarGraph(graph)</code> - the dual of the planar <code>graph</code>: one vertex per
 * face, and an edge for each pair of faces separated by an edge of <code>graph</code>.
 *
 * <p>
 * The vertices are the integers <code>1, 2, ..., k</code>, numbered as the faces are listed by
 * <code>PlanarFaceList</code>, so vertex <code>i</code> of the dual is face <code>i</code> of the
 * original. The dual is simple: a bridge, which separates a face from itself, contributes no self
 * loop, and two faces sharing several edges are joined once rather than once per edge.
 */
public class DualPlanarGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(gex.toData());
    PlanarFaceDecomposition decomposition = PlanarFaceDecomposition.of(undirected);
    if (decomposition == null) {
      // not planar, so there is no face structure to dualize
      return F.NIL;
    }

    int faceCount = decomposition.faceCount();
    Graph<IExpr, ExprEdge> dual = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    for (int i = 0; i < faceCount; i++) {
      dual.addVertex(F.ZZ(i + 1));
    }
    for (int i = 0; i < faceCount; i++) {
      for (int j : decomposition.adjacency[i]) {
        if (i < j) {
          dual.addEdge(F.ZZ(i + 1), F.ZZ(j + 1));
        }
      }
    }
    return GraphExpr.newInstance(dual);
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
