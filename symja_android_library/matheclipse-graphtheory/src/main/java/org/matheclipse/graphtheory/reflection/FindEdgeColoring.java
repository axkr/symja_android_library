package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.alg.ExactVertexColoring;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindEdgeColoring(graph)</code> - a coloring with a minimal number of colors for the edges of
 * <code>graph</code>, given as integers in <code>EdgeList</code> order.
 *
 * <p>
 * <code>FindEdgeColoring(graph, l)</code> uses the colors <code>1, 2, ..., l</code> and
 * <code>FindEdgeColoring(graph, {c1, c2, ...})</code> uses the given colors; both return the
 * expression unevaluated when no such coloring exists.
 *
 * <p>
 * This is a vertex coloring of the line graph, so it shares {@link ExactVertexColoring} with
 * {@code FindVertexColoring}. The minimal case does not run as a plain minimal coloring though:
 * Vizing's theorem pins the number of colors to the maximum degree <code>d</code> or
 * <code>d + 1</code>, and <code>d</code> is already a lower bound because the edges meeting at a
 * maximum-degree vertex are pairwise adjacent - so one feasibility search at <code>d</code> decides
 * it.
 */
public class FindEdgeColoring extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // the second argument: a palette of colors, a number of colors, or neither
    IAST palette = null;
    int limit = Integer.MAX_VALUE;
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        palette = (IAST) arg2;
        limit = palette.argSize();
      } else {
        limit = arg2.toIntDefault();
        if (limit < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C2), engine);
        }
      }
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    VertexColoringSupport.LineGraphData lineGraph = VertexColoringSupport.lineGraph(g);
    int m = lineGraph.edgeCount();
    if (m == 0) {
      return F.CEmptyList;
    }

    ExactVertexColoring solver = new ExactVertexColoring(m, lineGraph.adjacency);
    int[] colors = limit == Integer.MAX_VALUE //
        ? minimalColoring(solver, lineGraph.maxDegree, ast, engine) //
        : boundedColoring(solver, limit, ast, engine);
    if (colors == null) {
      return F.NIL;
    }

    colors = VertexColoringSupport.canonicalize(colors);
    return palette == null ? VertexColoringSupport.toIntegerList(colors)
        : VertexColoringSupport.toPaletteList(colors, palette);
  }

  /** A coloring using as few colors as possible, guided by Vizing's theorem. */
  private static int[] minimalColoring(ExactVertexColoring solver, int maxDegree, IAST ast,
      EvalEngine engine) {
    ExactVertexColoring.Result atMaxDegree = solver.solve(maxDegree);
    if (atMaxDegree.colors != null) {
      // maxDegree colors are necessary, so a coloring that uses them is minimal
      return atMaxDegree.colors;
    }
    if (atMaxDegree.proven) {
      ExactVertexColoring.Result oneMore = solver.solve(maxDegree + 1);
      if (oneMore.colors != null) {
        return oneMore.colors;
      }
      if (oneMore.proven) {
        // only a multigraph can need more than maxDegree + 1
        ExactVertexColoring.Result minimal = solver.solve(Integer.MAX_VALUE);
        if (!minimal.proven) {
          printSearchLimit(ast, engine);
        }
        return minimal.colors;
      }
    }
    // the search was cut short, so answer with a valid but possibly larger coloring
    printSearchLimit(ast, engine);
    return solver.dsatur();
  }

  /** A coloring using at most <code>limit</code> colors, or <code>null</code> if there is none. */
  private static int[] boundedColoring(ExactVertexColoring solver, int limit, IAST ast,
      EvalEngine engine) {
    ExactVertexColoring.Result result = solver.solve(limit);
    if (!result.proven) {
      printSearchLimit(ast, engine);
    }
    return result.colors;
  }

  private static void printSearchLimit(IAST ast, EvalEngine engine) {
    // Iteration limit of `1` exceeded for `2`.
    Errors.printMessage(ast.topHead(), "itlim",
        F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
