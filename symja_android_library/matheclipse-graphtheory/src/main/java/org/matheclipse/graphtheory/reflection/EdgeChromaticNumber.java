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
 * <code>EdgeChromaticNumber(graph)</code> - the chromatic index of <code>graph</code>: the smallest
 * number of colors that can be assigned to its edges so that no two edges sharing an endpoint have
 * the same color.
 *
 * <p>
 * This is the vertex chromatic number of the line graph, so it reuses
 * {@link ExactVertexColoring}. It is not computed as a plain minimal coloring of that line graph,
 * though: Vizing's theorem pins the answer to one of two values, the maximum degree
 * <code>d</code> or <code>d + 1</code>, so a single feasibility search at <code>d</code> decides it.
 * The lower bound needs no proof of its own - the edges meeting at a maximum-degree vertex are
 * pairwise adjacent in the line graph, so they already need <code>d</code> colors.
 */
public class EdgeChromaticNumber extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    // self loops, edge directions and edge weights are ignored, as in VertexChromaticNumber
    Graph<IExpr, ?> g = gex.toData();
    VertexColoringSupport.LineGraphData lineGraph = VertexColoringSupport.lineGraph(g);
    int m = lineGraph.edgeCount();
    if (m == 0) {
      return F.C0;
    }

    int maxDegree = lineGraph.maxDegree;
    ExactVertexColoring solver = new ExactVertexColoring(m, lineGraph.adjacency);

    ExactVertexColoring.Result atMaxDegree = solver.solve(maxDegree);
    if (atMaxDegree.colors != null) {
      return F.ZZ(maxDegree);
    }
    if (atMaxDegree.proven) {
      // Vizing: a simple graph never needs more than maxDegree + 1
      ExactVertexColoring.Result oneMore = solver.solve(maxDegree + 1);
      if (oneMore.colors != null) {
        return F.ZZ(maxDegree + 1);
      }
      if (oneMore.proven) {
        // only a multigraph can get here - a pair of opposite directed edges is the way to build
        // one in Symja - so fall back to a full minimal coloring of the line graph
        ExactVertexColoring.Result minimal = solver.solve(Integer.MAX_VALUE);
        if (!minimal.proven) {
          printSearchLimit(ast, engine);
        }
        return F.ZZ(minimal.usedColors);
      }
    }

    // the search was cut short, so answer with the heuristic coloring rather than with a value the
    // bounds cannot support
    printSearchLimit(ast, engine);
    return F.ZZ(VertexColoringSupport.usedColors(solver.dsatur()));
  }

  private static void printSearchLimit(IAST ast, EvalEngine engine) {
    // Iteration limit of `1` exceeded for `2`.
    Errors.printMessage(ast.topHead(), "itlim",
        F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
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
