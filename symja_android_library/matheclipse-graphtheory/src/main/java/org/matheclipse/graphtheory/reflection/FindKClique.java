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
 * <code>FindKClique(graph, k)</code> - a largest k-clique of <code>graph</code>: a maximal set of
 * vertices that are at a distance no greater than <code>k</code> from each other.
 *
 * <p>
 * This is Luce's k-clique, so the members need not be joined by edges - only reachable within
 * <code>k</code> steps, and the paths may leave the set. <code>k = 1</code> is therefore the
 * ordinary clique, and <code>FindKClique(graph, 1)</code> agrees with <code>FindClique(graph)</code>.
 *
 * <p>
 * <code>FindKClique(graph, k, n)</code> asks for at most <code>n</code> vertices,
 * <code>FindKClique(graph, k, {n})</code> for exactly <code>n</code>, and
 * <code>FindKClique(graph, k, {min, max})</code> for a size in that range.
 * <code>FindKClique(graph, k, nspec, s)</code> returns up to <code>s</code> of them, and
 * <code>FindKClique(graph, k, nspec, All)</code> every one, largest first.
 */
public class FindKClique extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int k = ast.arg2().toIntDefault();
    if (k < 1) {
      // Positive machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
    }
    VertexSetSearch.SizeSpec spec = ast.size() > 3 //
        ? VertexSetSearch.sizeSpec(ast.arg3()) //
        : VertexSetSearch.sizeSpec(F.CInfinity);
    if (spec.invalid) {
      // Non-negative machine-sized integer expected at position `2` in `1`.
      return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C3), engine);
    }
    // without the fourth argument only one k-clique is reported
    int limit = 1;
    if (ast.size() > 4) {
      IExpr arg4 = ast.arg4();
      if (arg4 == S.All || arg4.isInfinity()) {
        limit = Integer.MAX_VALUE;
      } else {
        limit = arg4.toIntDefault();
        if (limit < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C4), engine);
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

    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);
    List<Set<IExpr>> cliques = VertexSetSearch.kCliques(undirected, k, spec, limit);
    IASTAppendable result = F.ListAlloc(cliques.size());
    for (Set<IExpr> clique : cliques) {
      IASTAppendable vertices = F.ListAlloc(clique.size());
      // kCliques already reports each set in VertexList order
      for (IExpr vertex : clique) {
        vertices.append(vertex);
      }
      result.append(vertices);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
