package org.matheclipse.graphtheory.reflection;

import java.util.List;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.alg.PlanarFaceDecomposition;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>PlanarFaceList(graph)</code> - the faces of the planar <code>graph</code>, each one the list
 * of vertices bounding it, in the order the boundary is walked.
 *
 * <p>
 * The outer face comes first and is included unless <code>IncludeOuterFace-&gt;False</code> is
 * given. A connected planar graph with <code>n</code> vertices and
 * <code>m</code> edges has <code>m - n + 2</code> faces, and a face bounded by a bridge visits the
 * same vertex more than once, so the boundary walks are not always simple cycles.
 */
public class PlanarFaceList extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, final IAST originalAST) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(gex.toData());
    PlanarFaceDecomposition decomposition = PlanarFaceDecomposition.of(undirected);
    if (decomposition == null) {
      // a graph that is not planar has no face structure at all
      return F.NIL;
    }

    // the outer face is the head of the list, so leaving it out is dropping the first entry
    boolean includeOuterFace = !options[0].isFalse();
    List<List<IExpr>> faces = decomposition.faces;
    int first = includeOuterFace || faces.isEmpty() ? 0 : 1;
    IASTAppendable result = F.ListAlloc(faces.size() - first);
    for (List<IExpr> face : faces.subList(first, faces.size())) {
      IASTAppendable boundary = F.ListAlloc(face.size());
      for (IExpr vertex : face) {
        boundary.append(vertex);
      }
      result.append(boundary);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.IncludeOuterFace}, new IExpr[] {S.True});
  }
}
