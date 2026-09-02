package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.alg.ExactVertexColoring;
import org.matheclipse.graphtheory.alg.VertexColoringSupport;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindVertexColoring(graph)</code> - a coloring with a minimal number of colors for the
 * vertices of <code>graph</code>, given as integers in <code>VertexList</code> order.
 *
 * <p>
 * <code>FindVertexColoring(graph, l)</code> uses the colors <code>1, 2, ..., l</code> and
 * <code>FindVertexColoring(graph, {c1, c2, ...})</code> uses the given colors; both return the
 * expression unevaluated when no such coloring exists.
 *
 * <p>
 * Options: <code>Method</code> and <code>PerformanceGoal</code>.
 */
public class FindVertexColoring extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, final IAST originalAST) {

    // the second argument: a palette of colors, a number of colors, or neither
    IAST palette = null;
    int limit = Integer.MAX_VALUE;
    if (argSize == 2) {
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
    } else if (argSize != 1) {
      return F.NIL;
    }

    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    // self loops are ignored rather than rejected: Mathematica answers
    // FindVertexColoring[Graph[{1,2},{1<->1}]] with {1,1}
    Graph<IExpr, ?> g = gex.toData();

    String method = optionString(options, 0);
    String performanceGoal = optionString(options, 1);

    VertexColoringSupport.GraphData data = VertexColoringSupport.normalize(g);
    int n = data.vertexCount();
    if (n == 0) {
      return F.CEmptyList;
    }

    int[] colors = null;
    boolean heuristicOnly = false;
    if ("HybridEA".equalsIgnoreCase(method)) {
      // This is the method for large graphs; here it is the DSATUR heuristic,
      // which is fast but may use more colors than the chromatic number
      heuristicOnly = true;
    } else if ("Automatic".equalsIgnoreCase(method)) {
      // PerformanceGoal is only consulted when no Method was named
      heuristicOnly = "Speed".equalsIgnoreCase(performanceGoal);
    } else if (!"ILP".equalsIgnoreCase(method) && !"BacktrackingDS".equalsIgnoreCase(method)) {
      if (!VertexColoringSupport.isJGraphTMethod(method)) {
        // `1` is not a valid `2` specification.
        return Errors.printMessage(ast.topHead(), "bspec", F.list(options[0], S.Method), engine);
      }
      colors = VertexColoringSupport.jgraphtColoring(g, data, method, engine);
      if (colors == null) {
        // only Chordal declines, and only on a graph that is not chordal
        // Value of option `1` -> `2` is not valid; the default value is used.
        Errors.printMessage(ast.topHead(), "nresopt", F.list(S.Method, options[0]), engine);
      }
    }

    ExactVertexColoring solver = new ExactVertexColoring(n, data.adjacency);
    if (colors == null) {
      if (heuristicOnly) {
        colors = solver.dsatur();
      } else {
        colors = exactColoring(solver, limit, ast, engine);
        if (colors == null) {
          return F.NIL;
        }
      }
    }
    // canonicalize first: a JGraphT algorithm may return colors that are not numbered 1..k, so the
    // count of colors is only the largest one after renumbering
    colors = VertexColoringSupport.canonicalize(colors);
    if (limit != Integer.MAX_VALUE && VertexColoringSupport.usedColors(colors) > limit) {
      // a heuristic can overshoot the requested number of colors where an exact search would not,
      // so never report "no coloring" on a heuristic's word alone
      colors = exactColoring(solver, limit, ast, engine);
      if (colors == null) {
        return F.NIL;
      }
      colors = VertexColoringSupport.canonicalize(colors);
    }

    return palette == null ? VertexColoringSupport.toIntegerList(colors)
        : VertexColoringSupport.toPaletteList(colors, palette);
  }

  /**
   * Run the exact search, reporting a search that was cut short.
   *
   * @return the coloring, or <code>null</code> when no coloring within <code>limit</code> colors
   *         was found
   */
  private static int[] exactColoring(ExactVertexColoring solver, int limit, IAST ast,
      EvalEngine engine) {
    ExactVertexColoring.Result result = solver.solve(limit);
    if (!result.proven) {
      // Iteration limit of `1` exceeded for `2`.
      Errors.printMessage(ast.topHead(), "itlim",
          F.list(F.ZZ(ExactVertexColoring.MAX_SEARCH_NODES), ast), engine);
    }
    return result.colors;
  }

  private static String optionString(IExpr[] options, int position) {
    IExpr option = options[position];
    if (option != null && (option.isString() || option.isSymbol())) {
      return option.toString();
    }
    return "";
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Method, S.PerformanceGoal}, //
        new IExpr[] {S.Automatic, S.$PerformanceGoal});
  }
}
