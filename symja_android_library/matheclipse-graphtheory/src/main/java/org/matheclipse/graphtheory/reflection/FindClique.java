package org.matheclipse.graphtheory.reflection;

import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.alg.VertexSetSearch;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindClique(graph)</code> - a largest clique of <code>graph</code>: a set of vertices every
 * two of which are joined by an edge.
 *
 * <p>
 * <code>FindClique(graph, n)</code> asks for at most <code>n</code> vertices,
 * <code>FindClique(graph, {n})</code> for exactly <code>n</code>, and
 * <code>FindClique(graph, {min, max})</code> for a size in that range.
 * <code>FindClique(graph, nspec, s)</code> returns up to <code>s</code> cliques and
 * <code>FindClique(graph, nspec, All)</code> every one, largest first. The result is an empty list
 * when the size cannot be met.
 *
 * <p>
 * Edge directions and edge weights are ignored. Finding a maximum clique is NP-complete; the search
 * is Bron-Kerbosch with pivoting.
 */
public class FindClique extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    VertexSetSearch.SizeSpec spec = ast.size() > 2 //
        ? VertexSetSearch.sizeSpec(ast.arg2()) //
        : VertexSetSearch.sizeSpec(F.CInfinity);
    if (spec.invalid) {
      // Non-negative machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C2), engine);
    }
    // without the third argument only one clique is reported
    int limit = 1;
    if (ast.size() > 3) {
      IExpr arg3 = ast.arg3();
      if (arg3 == S.All || arg3.isInfinity()) {
        limit = Integer.MAX_VALUE;
      } else {
        limit = arg3.toIntDefault();
        if (limit < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C3), engine);
        }
      }
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    if (GraphExpr.vertexToIExpr(g).argSize() == 0) {
      return F.CEmptyList;
    }

    // a clique is a 1-clique: the distance power at k = 1 is the graph itself
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);
    List<Set<IExpr>> cliques = VertexSetSearch.kCliques(undirected, 1, spec, limit);
    IASTAppendable result = F.ListAlloc(cliques.size());
    for (Set<IExpr> clique : cliques) {
      IASTAppendable vertices = F.ListAlloc(clique.size());
      for (IExpr vertex : clique) {
        vertices.append(vertex);
      }
      result.append(vertices);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
