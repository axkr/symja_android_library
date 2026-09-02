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
import org.matheclipse.graphtheory.alg.ExactVertexColoring;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.alg.VertexSetSearch;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindKPlex(graph, k)</code> - a largest k-plex of <code>graph</code>: a maximal set of
 * vertices in which every vertex is adjacent to all but <code>k</code> of the members.
 *
 * <p>
 * In a set of <code>m</code> vertices that means every member has at least <code>m - k</code>
 * neighbours inside it, so a 1-plex is exactly a clique and
 * <code>FindKPlex(graph, 1)</code> agrees with <code>FindClique(graph)</code>. Raising
 * <code>k</code> relaxes the condition, so every k-plex is also a (k+1)-plex.
 *
 * <p>
 * <code>FindKPlex(graph, k, n)</code> asks for at most <code>n</code> vertices,
 * <code>FindKPlex(graph, k, {n})</code> for exactly <code>n</code>, and
 * <code>FindKPlex(graph, k, {min, max})</code> for a size in that range.
 * <code>FindKPlex(graph, k, nspec, s)</code> returns up to <code>s</code> of them and
 * <code>FindKPlex(graph, k, nspec, All)</code> every one, largest first. Asking for several is much
 * more expensive than asking for one: a single k-plex comes from a branch and bound that prunes to
 * the best answer, while enumerating them walks every subset that qualifies.
 */
public class FindKPlex extends AbstractFunctionEvaluator {

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
    IAST vertexList = GraphExpr.vertexToIExpr(g);
    if (vertexList.argSize() == 0) {
      return F.CEmptyList;
    }
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);

    if (limit == 1) {
      // one set is what the branch and bound is for, and it prunes where enumeration cannot
      VertexSetSearch.KPlexResult result = VertexSetSearch.maximumKPlex(undirected, k);
      if (!result.proven) {
        printSearchLimit(ast, engine);
      }
      return VertexSetSearch.toResult(vertexList, result.vertices, spec);
    }

    boolean[] aborted = new boolean[1];
    List<Set<IExpr>> plexes =
        VertexSetSearch.maximalKPlexes(undirected, k, spec, limit, aborted);
    if (aborted[0]) {
      printSearchLimit(ast, engine);
    }
    IASTAppendable result = F.ListAlloc(plexes.size());
    for (Set<IExpr> plex : plexes) {
      IASTAppendable vertices = F.ListAlloc(plex.size());
      for (IExpr vertex : plex) {
        vertices.append(vertex);
      }
      result.append(vertices);
    }
    return result;
  }

  private static void printSearchLimit(IAST ast, EvalEngine engine) {
    // Iteration limit of `1` exceeded for `2`.
    Errors.printMessage(ast.topHead(), "itlim",
        F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
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
