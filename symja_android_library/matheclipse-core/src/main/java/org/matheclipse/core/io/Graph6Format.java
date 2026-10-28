package org.matheclipse.core.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The reader of the <code>Graph6</code> format used by <code>Import</code> and
 * <code>ImportString</code>: one undirected graph per line, the vertex count followed by the upper
 * triangle of the adjacency matrix, column by column, six bits per printable ASCII character
 * (<code>63..126</code>). A line may start with the header <code>&gt;&gt;graph6&lt;&lt;</code>.
 *
 * <p>
 * The graphs are built as the expression <code>Graph({1, ..., n}, {i &lt;-&gt; j, ...})</code>, which
 * the evaluator turns into a graph object, so this reader does not depend on the graph theory
 * module.
 */
public final class Graph6Format {

  /** The elements which can be imported. */
  private static final String[] ELEMENTS = {"AdjacencyMatrix", "EdgeRules", "Graph", "GraphList",
      "VertexCount", "VertexList"};

  private static final String HEADER = ">>graph6<<";

  /** One decoded graph: <code>n</code> vertices and the edges <code>{i, j}</code>, i &lt; j. */
  private static final class Graph6 {
    final int n;
    final List<int[]> edges = new ArrayList<int[]>();

    Graph6(int n) {
      this.n = n;
    }
  }

  /**
   * Import the <code>element</code> of the graphs in <code>text</code>.
   *
   * @param text one graph per line
   * @param element one of {@link #ELEMENTS}, or <code>"Elements"</code> for their list
   * @return a single graph for the <code>"Graph"</code> element of one graph, a list with one entry
   *         per graph otherwise; {@link S#$Failed} if <code>text</code> is not in the format and
   *         {@link F#NIL} for an unknown element
   */
  public static IExpr importString(String text, String element, EvalEngine engine) {
    if (element.equals("Elements")) {
      return F.mapRange(0, ELEMENTS.length, i -> F.stringx(ELEMENTS[i]));
    }
    boolean known = false;
    for (String e : ELEMENTS) {
      known |= e.equals(element);
    }
    if (!known) {
      return F.NIL;
    }
    List<Graph6> graphs = decode(text);
    if (graphs == null || graphs.isEmpty()) {
      // Cannot import data as `1` format.
      Errors.printMessage(S.Import, "fmterr", F.list(F.stringx("Graph6")), engine);
      return S.$Failed;
    }
    IASTAppendable result = F.ListAlloc(graphs.size());
    for (Graph6 g : graphs) {
      result.append(element(g, element, engine));
    }
    if (element.equals("Graph") && result.argSize() == 1) {
      return result.arg1();
    }
    return result;
  }

  private static IExpr element(Graph6 g, String element, EvalEngine engine) {
    switch (element) {
      case "AdjacencyMatrix":
        final long entries = (long) g.n * g.n;
        if (entries > Config.MAX_AST_SIZE || g.n > Config.MAX_MATRIX_DIMENSION_SIZE) {
          throw new ASTElementLimitExceeded(entries);
        }
        // the positions i*n+j of the entries 1, both orders of each edge
        final Set<Long> ones = new HashSet<Long>();
        for (int[] edge : g.edges) {
          ones.add((edge[0] - 1L) * g.n + (edge[1] - 1));
          ones.add((edge[1] - 1L) * g.n + (edge[0] - 1));
        }
        return F.matrix((i, j) -> ones.contains((long) i * g.n + j) ? F.C1 : F.C0, g.n, g.n);
      case "EdgeRules":
        return F.mapFunction(S.List, g.edges, edge -> F.Rule(F.ZZ(edge[0]), F.ZZ(edge[1])));
      case "VertexCount":
        return F.ZZ(g.n);
      case "VertexList":
        return F.Range(g.n).eval(engine);
      default:
        // Graph, GraphList
        IAST edges = F.mapFunction(S.List, g.edges,
            edge -> F.UndirectedEdge(F.ZZ(edge[0]), F.ZZ(edge[1])));
        return engine.evaluate(F.Graph(F.Range(g.n).eval(engine), edges));
    }
  }

  /**
   * Decode the graphs, the edges of each in lexicographic order.
   *
   * @return <code>null</code> if a line is not in the format
   */
  private static List<Graph6> decode(String text) {
    List<Graph6> graphs = new ArrayList<Graph6>();
    for (String line : text.split("\\R")) {
      line = line.trim();
      if (line.startsWith(HEADER)) {
        line = line.substring(HEADER.length());
      }
      if (line.isEmpty()) {
        continue;
      }
      int[] bytes = new int[line.length()];
      for (int i = 0; i < bytes.length; i++) {
        bytes[i] = line.charAt(i) - 63;
        if (bytes[i] < 0 || bytes[i] > 63) {
          return null;
        }
      }
      // the vertex count is one character, or 126 followed by three or 63 126 by six characters
      int start = 1;
      if (bytes[0] == 63) {
        start = bytes.length > 1 && bytes[1] == 63 ? 8 : 4;
      }
      if (bytes.length < start) {
        return null;
      }
      long n = start == 1 ? bytes[0] : 0;
      for (int i = start == 8 ? 2 : 1; i < start; i++) {
        n = (n << 6) | bytes[i];
      }
      if (n > Integer.MAX_VALUE || (bytes.length - start) * 12L < n * (n - 1)) {
        return null;
      }
      Graph6 g = new Graph6((int) n);
      long k = 0;
      for (int j = 1; j < n; j++) {
        for (int i = 0; i < j; i++) {
          if (((bytes[start + (int) (k / 6)] >> (5 - (int) (k % 6))) & 1) == 1) {
            g.edges.add(new int[] {i + 1, j + 1});
          }
          k++;
        }
      }
      g.edges.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0])
          : Integer.compare(a[1], b[1]));
      graphs.add(g);
    }
    return graphs;
  }

  private Graph6Format() {}
}
