package org.matheclipse.core.expression.data;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Trie;

public class GraphExpr<T> extends DataExpr<Graph<IExpr, T>> {

  private static final long serialVersionUID = 6160043985328230156L;

  public static IASTAppendable[] edgesToIExpr(Graph<IExpr, ?> graph) {
    Set<Object> edgeSet = (Set<Object>) graph.edgeSet();
    IASTAppendable edges = F.ListAlloc(edgeSet.size());
    GraphType type = graph.getType();
    IASTAppendable weights = F.NIL;
    if (type.isWeighted()) {
      weights = F.ListAlloc(edgeSet.size());
    }
    for (Object edge : edgeSet) {
      edgeToIExpr(type, edge, edges, weights, edgeSet.size());
    }
    return new IASTAppendable[] {edges, weights};
  }

  public static IASTAppendable[] edgesToRules(Graph<IExpr, ?> graph) {
    Set<Object> edgeSet = (Set<Object>) graph.edgeSet();
    IASTAppendable edges = F.ListAlloc(edgeSet.size());
    IASTAppendable weights = null;
    // GraphType type = graph.getType();

    for (Object edge : edgeSet) {
      if (edge instanceof ExprWeightedEdge) {
        ExprWeightedEdge weightedEdge = (ExprWeightedEdge) edge;
        edges.append(F.Rule(weightedEdge.lhs(), weightedEdge.rhs()));
        if (weights == null) {
          weights = F.ListAlloc(edgeSet.size());
        }
        weights.append(weightedEdge.weight());
      } else if (edge instanceof ExprEdge) {
        ExprEdge exprEdge = (ExprEdge) edge;
        edges.append(F.Rule(exprEdge.lhs(), exprEdge.rhs()));
      }
    }
    return new IASTAppendable[] {edges, weights};
  }

  public static void edgesToVisjs(IdentityHashMap<IExpr, Integer> map, StringBuilder buf,
      Graph<IExpr, ExprEdge> g) {
    Set<ExprEdge> edgeSet = g.edgeSet();
    GraphType type = g.getType();
    boolean first = true;
    if (type.isDirected()) {
      buf.append("var edges = new vis.DataSet([\n");
      for (Object object : edgeSet) {
        if (object instanceof ExprEdge) {
          ExprEdge edge = (ExprEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }
          buf.append(map.get(edge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(edge.rhs()));
          // , arrows: { to: { enabled: true, type: 'arrow'}}
          buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
          buf.append("}\n");
          first = false;
        } else if (object instanceof ExprWeightedEdge) {
          ExprWeightedEdge weightedEdge = (ExprWeightedEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }
          buf.append(map.get(weightedEdge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(weightedEdge.rhs()));
          // , arrows: { to: { enabled: true, type: 'arrow'}}
          buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
          buf.append("}\n");
          first = false;
        }
      }
    } else {
      //
      buf.append("var edges = new vis.DataSet([\n");
      for (Object object : edgeSet) {
        if (object instanceof ExprEdge) {
          ExprEdge edge = (ExprEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }
          buf.append(map.get(edge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(edge.rhs()));
          buf.append("}\n");
          first = false;
        } else if (object instanceof ExprWeightedEdge) {
          ExprWeightedEdge weightedEdge = (ExprWeightedEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }
          buf.append(map.get(weightedEdge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(weightedEdge.rhs()));
          buf.append("}\n");
          first = false;
        }
      }
    }
    buf.append("]);\n");
  }

  public static void edgeToIExpr(GraphType type, Object edge, IASTAppendable edges,
      IASTAppendable weights, int size) {
    if (edge instanceof ExprWeightedEdge) {
      ExprWeightedEdge weightedEdge = (ExprWeightedEdge) edge;
      if (type.isDirected()) {
        edges.append(F.DirectedEdge(weightedEdge.lhs(), weightedEdge.rhs()));
      } else {
        edges.append(F.UndirectedEdge(weightedEdge.lhs(), weightedEdge.rhs()));
      }
      weights.append(weightedEdge.weight());
    } else if (edge instanceof ExprEdge) {
      ExprEdge exprEdge = (ExprEdge) edge;
      if (type.isDirected()) {
        edges.append(F.DirectedEdge(exprEdge.lhs(), exprEdge.rhs()));
      } else {
        edges.append(F.UndirectedEdge(exprEdge.lhs(), exprEdge.rhs()));
      }
    }
  }

  public static IExpr graphToAdjacencyMatrix(Graph<IExpr, ?> graph) {
    Set<IExpr> vertexSet = graph.vertexSet();
    int size = vertexSet.size();
    IdentityHashMap<IExpr, Integer> map = new IdentityHashMap<IExpr, Integer>();
    int indx = 1;
    for (IExpr expr : vertexSet) {
      map.put(expr, indx++);
    }

    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    GraphType type = graph.getType();
    for (Object edge : graph.edgeSet()) {
      if (edge instanceof ExprWeightedEdge) {
        ExprWeightedEdge weightedEdge = (ExprWeightedEdge) edge;
        IExpr lhs = weightedEdge.lhs();
        IExpr rhs = weightedEdge.rhs();
        int from = map.get(lhs);
        int to = map.get(rhs);
        trie.put(new int[] {from, to}, F.C1);
        if (!type.isDirected()) {
          trie.put(new int[] {to, from}, F.C1);
        }
      } else {
        ExprEdge exprEdge = (ExprEdge) edge;
        IExpr lhs = exprEdge.lhs();
        IExpr rhs = exprEdge.rhs();
        int from = map.get(lhs);
        int to = map.get(rhs);
        trie.put(new int[] {from, to}, F.C1);
        if (!type.isDirected()) {
          trie.put(new int[] {to, from}, F.C1);
        }
      }
    }
    return new SparseArrayExpr(trie, new int[] {size, size}, F.C0, false);
  }

  /**
   * Convert a graph into an IExpr object.
   *
   * @param graph
   * @return
   */
  public static IAST fullForm(AbstractBaseGraph<IExpr, ?> graph) {
    IASTAppendable vertexes = vertexToIExpr(graph);
    IASTAppendable[] edgeData = edgesToIExpr(graph);
    if (edgeData[1].isNIL()) {
      return F.Graph(vertexes, edgeData[0]);
    }
    return F.Graph(vertexes, edgeData[0], F.list(F.Rule(S.EdgeWeight, edgeData[1])));
  }

  // private static IExpr fullFormWeightedGraph(AbstractBaseGraph<IExpr, ExprWeightedEdge> graph) {
  // IASTAppendable vertexes = vertexToIExpr(graph);
  // IASTAppendable[] res = weightedEdgesToIExpr(graph);
  // return F.Graph(vertexes, res[0], F.list(F.Rule(S.EdgeWeight, res[1])));
  // }
  /**
   * Convert a Graph into a JavaScript visjs.org form
   *
   * @param graphExpr
   * @return
   */
  public String graphToJSForm() {
    GraphExpr<ExprEdge> gex = (GraphExpr<ExprEdge>) this;
    AbstractBaseGraph<IExpr, ?> g = (AbstractBaseGraph<IExpr, ?>) gex.toData();
    IdentityHashMap<IExpr, Integer> map = new IdentityHashMap<IExpr, Integer>();
    StringBuilder buf = new StringBuilder();
    if (g.getType().isWeighted()) {
      weightedGraphToVisjs(map, buf, (AbstractBaseGraph<IExpr, ExprWeightedEdge>) g);
    } else {
      graphToVisjs(map, buf, (AbstractBaseGraph<IExpr, ExprEdge>) g);
    }
    return buf.toString();
  }

  private static void graphToVisjs(IdentityHashMap<IExpr, Integer> map, StringBuilder buf,
      AbstractBaseGraph<IExpr, ExprEdge> g) {
    vertexToVisjs(map, buf, g);
    edgesToVisjs(map, buf, g);
  }

  /**
   * Be cautious with this method, no new internal IExpr object is created
   *
   * @param value
   * @return
   */
  public static <T> GraphExpr<T> newInstance(final Graph<IExpr, T> value) {
    return new GraphExpr<T>(value);
  }

  public static IASTAppendable vertexToIExpr(Graph<IExpr, ?> graph) {
    return F.mapSet(graph.vertexSet(), x -> x);
  }

  private static void vertexToVisjs(IdentityHashMap<IExpr, Integer> map, StringBuilder buf,
      Graph<IExpr, ?> g) {
    Set<IExpr> vertexSet = g.vertexSet();
    buf.append("var nodes = new vis.DataSet([\n");
    boolean first = true;
    int counter = 1;
    for (IExpr expr : vertexSet) {
      // {id: 1, label: 'Node 1'},
      if (first) {
        buf.append("  {id: ");
      } else {
        buf.append(", {id: ");
      }
      buf.append(counter);
      map.put(expr, counter++);
      buf.append(", label: '");
      buf.append(expr.toString());
      buf.append("'}\n");
      first = false;
    }
    buf.append("]);\n");
  }

  /**
   * Return an array of 2 lists. At index 0 the list of edges. At index 1 the list of corresponding
   * weights.
   *
   * @param graph
   * @return an array of 2 lists. At index 0 the list of edges. At index 1 the list of corresponding
   *         weights.
   */
  // public static IASTAppendable[] weightedEdgesToIExpr(Graph<IExpr, ExprWeightedEdge> graph) {
  //
  // Set<ExprWeightedEdge> edgeSet = graph.edgeSet();
  // IASTAppendable edges = F.ListAlloc(edgeSet.size());
  // IASTAppendable weights = F.ListAlloc(edgeSet.size());
  // GraphType type = graph.getType();
  // if (type.isDirected()) {
  // for (ExprWeightedEdge edge : edgeSet) {
  // edges.append(F.DirectedEdge(edge.lhs(), edge.rhs()));
  // weights.append(edge.weight());
  // }
  // } else {
  // for (ExprWeightedEdge edge : edgeSet) {
  // edges.append(F.UndirectedEdge(edge.lhs(), edge.rhs()));
  // weights.append(edge.weight());
  // }
  // }
  // return new IASTAppendable[] {edges, weights};
  // }

  private static void weightedEdgesToVisjs(Map<IExpr, Integer> map, StringBuilder buf,
      Graph<IExpr, ExprWeightedEdge> graph) {

    Set<ExprWeightedEdge> edgeSet = graph.edgeSet();
    GraphType type = graph.getType();
    boolean first = true;
    if (type.isDirected()) {
      buf.append("var edges = new vis.DataSet([\n");
      for (Object object : edgeSet) {
        if (object instanceof ExprWeightedEdge) {
          ExprWeightedEdge edge = (ExprWeightedEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }

          buf.append(map.get(edge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(edge.rhs()));

          buf.append(", label: '");
          buf.append(edge.weight());
          buf.append("'");
          // , arrows: { to: { enabled: true, type: 'arrow'}}
          buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
          buf.append("}\n");
          first = false;
        }
      }
    } else {
      buf.append("var edges = new vis.DataSet([\n");
      for (Object object : edgeSet) {
        if (object instanceof ExprWeightedEdge) {
          ExprWeightedEdge edge = (ExprWeightedEdge) object;
          // {from: 1, to: 3},
          if (first) {
            buf.append("  {from: ");
          } else {
            buf.append(", {from: ");
          }

          buf.append(map.get(edge.lhs()));
          buf.append(", to: ");
          buf.append(map.get(edge.rhs()));
          buf.append(", label: '");
          buf.append(edge.weight());
          buf.append("'");
          buf.append("}\n");
          first = false;
        }
      }
    }
    buf.append("]);\n");
  }

  public static IExpr weightedGraphToAdjacencyMatrix(Graph<IExpr, ExprWeightedEdge> g) {
    Set<IExpr> vertexSet = g.vertexSet();
    int size = vertexSet.size();
    IdentityHashMap<IExpr, Integer> map = new IdentityHashMap<IExpr, Integer>();
    int indx = 1;
    for (IExpr expr : vertexSet) {
      map.put(expr, indx++);
    }

    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();

    for (ExprWeightedEdge edge : g.edgeSet()) {
      IExpr lhs = edge.lhs();
      IExpr rhs = edge.rhs();
      int from = map.get(lhs);
      int to = map.get(rhs);
      trie.put(new int[] {from, to}, F.C1);
      if (g.containsEdge(rhs, lhs)) {
        trie.put(new int[] {to, from}, F.C1);
      }
    }
    return new SparseArrayExpr(trie, new int[] {size, size}, F.C0, false);
  }

  private static void weightedGraphToVisjs(IdentityHashMap<IExpr, Integer> map, StringBuilder buf,
      AbstractBaseGraph<IExpr, ExprWeightedEdge> g) {
    vertexToVisjs(map, buf, g);
    weightedEdgesToVisjs(map, buf, g);
  }

  public static IExpr weightedGraphToWeightedAdjacencyMatrix(Graph<IExpr, ExprWeightedEdge> g) {
    Set<IExpr> vertexSet = g.vertexSet();
    int size = vertexSet.size();
    IdentityHashMap<IExpr, Integer> map = new IdentityHashMap<IExpr, Integer>();
    int indx = 1;
    for (IExpr expr : vertexSet) {
      map.put(expr, indx++);
    }

    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();

    for (ExprWeightedEdge edge : g.edgeSet()) {
      IExpr lhs = edge.lhs();
      IExpr rhs = edge.rhs();
      int from = map.get(lhs);
      int to = map.get(rhs);
      trie.put(new int[] {from, to}, F.num(edge.weight()));
      if (g.containsEdge(rhs, lhs)) {
        trie.put(new int[] {to, from}, F.num(edge.weight()));
      }
    }
    return new SparseArrayExpr(trie, new int[] {size, size}, F.C0, false);
  }

  protected GraphExpr(final Graph<IExpr, T> graph) {
    super(S.Graph, graph);
  }

  @Override
  public IExpr copy() {
    return new GraphExpr<T>(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof GraphExpr) {
      return fData.equals(((GraphExpr) obj).fData);
    }
    return false;
  }

  /**
   * Convert a graph into an {@link IAST} full form expression.
   * 
   */
  public IAST fullForm() {
    return fullForm((AbstractBaseGraph<IExpr, ExprEdge>) toData());
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 283 : 283 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return GRAPHEXPRID;
  }

  public boolean isUndirectedGraph() {
    return fData instanceof SimpleGraph;
  }

  /**
   * Test if the graph is instance of <code>DefaultDirectedWeightedGraph</code> or <code>
   * DefaultUndirectedWeightedGraph</code>
   *
   * @return <code>true</code> if the graph is a weighted graph
   */
  public boolean isWeightedGraph() {
    return fData instanceof DefaultDirectedWeightedGraph
        || fData instanceof DefaultUndirectedWeightedGraph;
  }

  @Override
  public String toHTML() {
    String javaScriptStr = graphToJSForm();
    String html = Config.VISJS_PAGE;
    html = StringUtils.replace(html, "`1`", javaScriptStr);
    html = StringUtils.replace(html, "`2`", "var options = {};");
    return html;
  }

  @Override
  public String toString() {
    if (fData instanceof AbstractBaseGraph) {
      AbstractBaseGraph<IExpr, ?> g = (AbstractBaseGraph<IExpr, ?>) fData;
      if (g.getType().isWeighted()) {
        return fullForm(g).toString();
      }
      return fullForm(g).toString();
    }

    return fHead + "[" + fData.toString() + "]";
  }
}
