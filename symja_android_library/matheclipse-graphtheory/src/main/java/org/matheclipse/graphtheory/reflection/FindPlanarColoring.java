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
import org.matheclipse.graphtheory.alg.PlanarFaceDecomposition;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindPlanarColoring(graph)</code> - a coloring with a minimal number of colors for the faces
 * of the planar <code>graph</code>, given as integers in the order the faces are traced.
 *
 * <p>
 * <code>FindPlanarColoring(graph, l)</code> uses the colors <code>1, 2, ..., l</code> and
 * <code>FindPlanarColoring(graph, {c1, c2, ...})</code> uses the given colors; both return the
 * expression unevaluated when no such coloring exists.
 *
 * <p>
 * Two faces conflict when they share an edge, so this is a vertex coloring of the dual graph and it
 * runs on the same exact search as {@code FindVertexColoring}. By the four color theorem the answer
 * never exceeds four colors.
 */
public class FindPlanarColoring extends AbstractFunctionEvaluator {

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
    // a simple undirected copy: self loops bound no face of their own here, and a pair of opposite
    // directed edges is one edge of the embedding
    Graph<IExpr, ExprEdge> undirected = VertexColoringSupport.toUndirected(g);

    PlanarFaceDecomposition decomposition = PlanarFaceDecomposition.of(undirected);
    if (decomposition == null) {
      // a graph that is not planar has no face structure at all
      return F.NIL;
    }
    int faceCount = decomposition.faceCount();
    if (faceCount == 0) {
      // no edges, so no face is bounded by anything
      return F.CEmptyList;
    }

    ExactVertexColoring solver = new ExactVertexColoring(faceCount, decomposition.adjacency);
    ExactVertexColoring.Result result = solver.solve(limit);
    if (!result.proven) {
      // Iteration limit of `1` exceeded for `2`.
      Errors.printMessage(ast.topHead(), "itlim",
          F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
    }
    if (result.colors == null) {
      return F.NIL;
    }

    int[] colors = VertexColoringSupport.canonicalize(result.colors);
    return palette == null ? VertexColoringSupport.toIntegerList(colors)
        : VertexColoringSupport.toPaletteList(colors, palette);
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
