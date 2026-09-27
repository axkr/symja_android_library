package org.matheclipse.graphtheory.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.hipparchus.util.MathArrays;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm.MinimumCostFlow;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.alg.shortestpath.NegativeCycleDetectedException;
import org.jgrapht.alg.spanning.BoruvkaMinimumSpanningTree;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.EdgeListType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.geodesy.GeodesicSolver;
import org.matheclipse.core.numerics.geodesy.ReferenceEllipsoid;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.ExprWeightedEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;
import org.matheclipse.graphtheory.graphics.GraphGraphics;
import com.google.common.collect.Sets;

/** Functions for graph theory algorithms. */
public class GraphFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      S.BetweennessCentrality.setEvaluator(new BetweennessCentrality());
      S.BipartiteGraphQ.setEvaluator(new BipartiteGraphQ());
      S.ClosenessCentrality.setEvaluator(new ClosenessCentrality());
      S.AdjacencyMatrix.setEvaluator(new AdjacencyMatrix());
      S.ConnectedGraphQ.setEvaluator(new ConnectedGraphQ());
      S.EdgeCount.setEvaluator(new EdgeCount());
      S.EdgeList.setEvaluator(new EdgeList());
      S.EdgeQ.setEvaluator(new EdgeQ());
      S.EdgeRules.setEvaluator(new EdgeRules());
      S.EigenvectorCentrality.setEvaluator(new EigenvectorCentrality());
      S.EulerianGraphQ.setEvaluator(new EulerianGraphQ());
      S.FindCycle.setEvaluator(new FindCycle());
      S.FindEulerianCycle.setEvaluator(new FindEulerianCycle());
      S.FindHamiltonianCycle.setEvaluator(new FindHamiltonianCycle());
      S.FindGraphIsomorphism.setEvaluator(new FindGraphIsomorphism());
      S.FindMinimumCostFlow.setEvaluator(new FindMinimumCostFlow());
      S.FindVertexCover.setEvaluator(new FindVertexCover());
      S.FindShortestPath.setEvaluator(new FindShortestPath());
      S.FindShortestTour.setEvaluator(new FindShortestTour());
      S.FindSpanningTree.setEvaluator(new FindSpanningTree());
      S.Graph.setEvaluator(new GraphCTor());
      S.GraphCenter.setEvaluator(new GraphCenter());
      S.GraphComplement.setEvaluator(new GraphComplement());
      S.GraphDifference.setEvaluator(new GraphDifference());
      S.GraphDiameter.setEvaluator(new GraphDiameter());
      S.GraphDisjointUnion.setEvaluator(new GraphDisjointUnion());
      S.GraphIntersection.setEvaluator(new GraphIntersection());
      S.GraphPeriphery.setEvaluator(new GraphPeriphery());
      S.GraphPower.setEvaluator(new GraphPower());
      S.GraphQ.setEvaluator(new GraphQ());
      S.GraphRadius.setEvaluator(new GraphRadius());
      S.GraphUnion.setEvaluator(new GraphUnion());
      S.HamiltonianGraphQ.setEvaluator(new HamiltonianGraphQ());
      S.IndexGraph.setEvaluator(new IndexGraph());
      S.IsomorphicGraphQ.setEvaluator(new IsomorphicGraphQ());
      S.LineGraph.setEvaluator(new LineGraph());
      S.PathGraphQ.setEvaluator(new PathGraphQ());
      S.PlanarGraphQ.setEvaluator(new PlanarGraphQ());
      S.VertexEccentricity.setEvaluator(new VertexEccentricity());
      S.VertexCount.setEvaluator(new VertexCount());
      S.VertexList.setEvaluator(new VertexList());
      S.VertexQ.setEvaluator(new VertexQ());
      S.WeaklyConnectedGraphQ.setEvaluator(new WeaklyConnectedGraphQ());
      S.WeightedAdjacencyMatrix.setEvaluator(new WeightedAdjacencyMatrix());
      S.WeightedGraphQ.setEvaluator(new WeightedGraphQ());
      S.GraphPlot.setEvaluator(new GraphPlot());
      S.HighlightGraph.setEvaluator(new HighlightGraph());
      S.ConnectedComponents.setEvaluator(new ConnectedComponents());
      S.ExpressionGraph.setEvaluator(new ExpressionGraph());
      S.FindMaximumFlow.setEvaluator(new FindMaximumFlow());
      S.GraphEmbedding.setEvaluator(new GraphEmbedding());
      S.NetGraph.setEvaluator(new NetGraph());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ConnectedComponents(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the connected components of the <code>graph</code> as lists of vertices. For a directed
   * <code>graph</code> the strongly connected components are computed.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ConnectedComponents(Graph({1 &lt;-&gt; 2, 2 &lt;-&gt; 3, 4 &lt;-&gt; 5}))
   * {{1,2,3},{4,5}}
   * </code>
   * </pre>
   */
  private static class ConnectedComponents extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      IExpr pattern = ast.isAST2() ? ast.arg2() : F.NIL;
      return GraphUtil.connectedComponents(gex, pattern, engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ExpressionGraph(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the tree of the subexpressions of <code>expr</code> as a graph. The vertices are the
   * integers <code>1, 2, 3,...</code> in depth first order of the subexpressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; EdgeList(ExpressionGraph(f(x, y)))
   * {1&lt;-&gt;2,1&lt;-&gt;3}
   * </code>
   * </pre>
   */
  private static class ExpressionGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      int maxLevel = Integer.MAX_VALUE;
      if (ast.isAST2()) {
        maxLevel = ast.arg2().toIntDefault();
        if (maxLevel < 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }
      Graph<IExpr, ExprEdge> graph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      int[] counter = new int[] {1};
      IInteger root = F.ZZ(counter[0]++);
      graph.addVertex(root);
      expressionGraphRecursive(ast.arg1(), root, 1, maxLevel, counter, graph);
      return GraphExpr.newInstance(graph);
    }

    /**
     * Add the arguments of <code>expr</code> as vertices connected with <code>parent</code> and
     * recurse into them.
     *
     * @param expr the subexpression represented by <code>parent</code>
     * @param parent the vertex number of <code>expr</code>
     * @param level the current nesting level
     * @param maxLevel the maximum nesting level
     * @param counter the next free vertex number in <code>counter[0]</code>
     * @param graph the graph the vertices and edges are added to
     */
    private static void expressionGraphRecursive(IExpr expr, IInteger parent, int level,
        int maxLevel, int[] counter, Graph<IExpr, ExprEdge> graph) {
      if (level > maxLevel || !expr.isAST()) {
        return;
      }
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        IInteger child = F.ZZ(counter[0]++);
        graph.addVertex(child);
        graph.addEdge(parent, child);
        expressionGraphRecursive(list.get(i), child, level + 1, maxLevel, counter, graph);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>FindMaximumFlow(graph, source, target)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the value of the maximum flow from <code>source</code> to <code>target</code> in the
   * <code>graph</code>. The capacity of an edge of an unweighted <code>graph</code> is
   * <code>1</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindMaximumFlow(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3}), 1, 3)
   * 2
   * </code>
   * </pre>
   */
  private static class FindMaximumFlow extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      // FindMaximumFlow(g, s, t), FindMaximumFlow(g, s, t, "prop"); s and t may be lists
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      String property = "FlowValue";
      if (ast.argSize() == 4) {
        if (!ast.arg4().isString()) {
          return F.NIL;
        }
        property = ast.arg4().toString();
        if (!property.equals("FlowValue") && !property.equals("EdgeList")
            && !property.equals("FlowMatrix")) {
          return F.NIL;
        }
      }
      GraphView view = GraphView.of(gex.toData());
      int[] sources = vertexPositions(view, ast.arg2());
      int[] sinks = vertexPositions(view, ast.arg3());
      if (sources == null || sinks == null) {
        return F.NIL;
      }
      IAST options = gex.options();
      double[] edgeCapacity = capacities(options, S.EdgeCapacity, view.m, 1.0);
      double[] vertexCapacity =
          capacities(options, S.VertexCapacity, view.n, Double.POSITIVE_INFINITY);
      if (edgeCapacity == null || vertexCapacity == null) {
        return F.NIL;
      }
      boolean exact = isIntegerCapacity(options, S.EdgeCapacity)
          && isIntegerCapacity(options, S.VertexCapacity);
      for (int s : sources) {
        for (int t : sinks) {
          if (s == t) {
            return property.equals("FlowValue") ? F.C0 : F.CEmptyList;
          }
        }
      }
      // the network: vertex v is split into 2v -> 2v+1 carrying its capacity; edge e runs through
      // a node of its own, 2n+2+2e (and 2n+3+2e for the reverse direction of an undirected edge)
      final int n = view.n;
      final int superSource = 2 * n;
      final int superSink = 2 * n + 1;
      Graph<Integer, DefaultWeightedEdge> network =
          GraphTypeBuilder.<Integer, DefaultWeightedEdge>directed().allowingMultipleEdges(false)
              .allowingSelfLoops(false).weighted(true).edgeClass(DefaultWeightedEdge.class)
              .buildGraph();
      for (int i = 0; i < 2 * n + 2 + 2 * view.m; i++) {
        network.addVertex(i);
      }
      for (int v = 0; v < n; v++) {
        arc(network, 2 * v, 2 * v + 1, vertexCapacity[v]);
      }
      DefaultWeightedEdge[] forward = new DefaultWeightedEdge[view.m];
      DefaultWeightedEdge[] backward = new DefaultWeightedEdge[view.m];
      for (int e = 0; e < view.m; e++) {
        int u = view.source[e];
        int v = view.target[e];
        if (u == v) {
          continue;
        }
        int node = 2 * n + 2 + 2 * e;
        forward[e] = arc(network, 2 * u + 1, node, edgeCapacity[e]);
        arc(network, node, 2 * v, edgeCapacity[e]);
        if (view.undirected[e]) {
          backward[e] = arc(network, 2 * v + 1, node + 1, edgeCapacity[e]);
          arc(network, node + 1, 2 * u, edgeCapacity[e]);
        }
      }
      for (int s : sources) {
        arc(network, superSource, 2 * s, Double.POSITIVE_INFINITY);
      }
      for (int t : sinks) {
        arc(network, 2 * t + 1, superSink, Double.POSITIVE_INFINITY);
      }
      MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> algorithm =
          new EdmondsKarpMFImpl<Integer, DefaultWeightedEdge>(network);
      MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> flow =
          algorithm.getMaximumFlow(superSource, superSink);
      if (property.equals("FlowValue")) {
        return flowNumber(flow.getValue(), exact);
      }
      if (property.equals("FlowMatrix")) {
        // the net flow from vertex i to vertex j as a SparseArray
        double[][] matrix = new double[n][n];
        for (int e = 0; e < view.m; e++) {
          if (forward[e] == null) {
            continue;
          }
          double f = flow.getFlowMap().get(forward[e]);
          if (backward[e] != null) {
            f -= flow.getFlowMap().get(backward[e]);
          }
          if (f >= 0.0) {
            matrix[view.source[e]][view.target[e]] += f;
          } else {
            matrix[view.target[e]][view.source[e]] -= f;
          }
        }
        IASTAppendable rules = F.ListAlloc();
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            if (matrix[i][j] > Config.DOUBLE_TOLERANCE) {
              rules.append(
                  F.Rule(F.list(F.ZZ(i + 1), F.ZZ(j + 1)), flowNumber(matrix[i][j], exact)));
            }
          }
        }
        return F.sparseArray(rules, new int[] {n, n});
      }
      // "EdgeList": the edges carrying flow, oriented along the flow, in flow matrix row order
      List<int[]> carrying = new ArrayList<int[]>();
      for (int e = 0; e < view.m; e++) {
        if (forward[e] == null) {
          continue;
        }
        double f = flow.getFlowMap().get(forward[e]);
        if (backward[e] != null) {
          f -= flow.getFlowMap().get(backward[e]);
        }
        if (f > Config.DOUBLE_TOLERANCE) {
          carrying.add(new int[] {view.source[e], view.target[e], e});
        } else if (f < -Config.DOUBLE_TOLERANCE) {
          carrying.add(new int[] {view.target[e], view.source[e], e});
        }
      }
      carrying.sort((x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0])
          : x[1] != y[1] ? Integer.compare(x[1], y[1]) : Integer.compare(x[2], y[2]));
      IASTAppendable result = F.ListAlloc(carrying.size());
      for (int[] c : carrying) {
        IExpr from = view.vertex(c[0]);
        IExpr to = view.vertex(c[1]);
        result
            .append(view.undirected[c[2]] ? F.UndirectedEdge(from, to) : F.DirectedEdge(from, to));
      }
      return result;
    }

    private static DefaultWeightedEdge arc(Graph<Integer, DefaultWeightedEdge> network, int u,
        int v, double capacity) {
      DefaultWeightedEdge edge = network.addEdge(u, v);
      network.setEdgeWeight(edge, capacity);
      return edge;
    }

    /** The 0-based positions of a vertex or a list of vertices, or <code>null</code>. */
    private static int[] vertexPositions(GraphView view, IExpr arg) {
      if (view.index.containsKey(arg)) {
        return new int[] {view.index.get(arg)};
      }
      if (!arg.isList()) {
        return null;
      }
      IAST list = (IAST) arg;
      int[] result = new int[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        Integer v = view.index.get(list.get(i));
        if (v == null) {
          return null;
        }
        result[i - 1] = v;
      }
      return result;
    }

    /**
     * The capacities given by the graph option <code>key</code> as a list in <code>EdgeList</code>
     * or <code>VertexList</code> order.
     *
     * @return <code>null</code> for a negative or symbolic capacity
     */
    private static double[] capacities(IAST options, IExpr key, int size, double defaultValue) {
      double[] result = new double[size];
      java.util.Arrays.fill(result, defaultValue);
      if (options == null || options.isNIL()) {
        return result;
      }
      for (IExpr option : options) {
        if (option.isRuleAST() && option.first().equals(key)) {
          IExpr value = option.second();
          if (!value.isList() || value.argSize() != size) {
            return null;
          }
          for (int i = 0; i < size; i++) {
            IExpr c = ((IAST) value).get(i + 1);
            double d = c.isInfinity() ? Double.POSITIVE_INFINITY : c.evalfNaN();
            if (Double.isNaN(d) || d < 0.0) {
              return null;
            }
            result[i] = d;
          }
        }
      }
      return result;
    }

    /** Whether the capacities of option <code>key</code> are all integers (or absent). */
    private static boolean isIntegerCapacity(IAST options, IExpr key) {
      if (options == null || options.isNIL()) {
        return true;
      }
      for (IExpr option : options) {
        if (option.isRuleAST() && option.first().equals(key)) {
          return ((IAST) option.second()).forAll(c -> c.isInteger() || c.isInfinity());
        }
      }
      return true;
    }

    /** An exact integer for integer capacities, else a machine real. */
    private static IExpr flowNumber(double value, boolean exact) {
      if (Double.isInfinite(value)) {
        return S.Infinity;
      }
      if (exact) {
        return F.ZZ(Math.round(value));
      }
      return F.num(value);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>GraphEmbedding(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the coordinates of the vertices of the <code>graph</code>. The second argument selects
   * the embedding, the default is <code>&quot;CircularEmbedding&quot;</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; GraphEmbedding(Graph({1 -&gt; 2}))
   * {{0.,-1.},{0.,1.}}
   * </code>
   * </pre>
   */
  private static class GraphEmbedding extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      String embedding = "CircularEmbedding";
      if (ast.isAST2()) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        embedding = ast.arg2().toString();
      }
      Graph<IExpr, ?> g = gex.toData();
      return GraphGraphics.vertexCoordinates(g, embedding);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>NetGraph</code> is currently only defined as a read protected symbol. */
  private static class NetGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.NO_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.READPROTECTED);
    }
  }

  private static class GraphPlot extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() > 1) {
        GraphExpr<?> graph = GraphExpr.newInstance(ast.arg1());
        if (graph == null) {
          return F.NIL;
        }
        try {
          // VertexLabels, GraphLayout, ... given to GraphPlot describe the graph it draws
          graph = GraphGraphics.withPlotOptions(graph, ast, 2);
          GraphGraphics gg = new GraphGraphics(graph);
          IExpr gExpr = gg.toGraphics();

          if (gExpr.isAST(S.Graphics)) {
            IASTAppendable gApp = (IASTAppendable) gExpr;
            // Inject proportional sizing to fix microscopic nodes and thin edges
            gApp.append(F.Rule(S.BaseStyle, F.List(F.PointSize(0.04), F.Thickness(0.005))));
            // the caller's Graphics options (AspectRatio, ...) are kept, as trailing rules, except
            // ImageSize: the WLJS notebook draws a Graph as GraphPlot[g, ImageSize -> 70, ...],
            // picture has ImageSize -> 200 all the same, unless the graph
            // was given a size of its own
            for (int i = 2; i < ast.size(); i++) {
              IExpr option = ast.get(i);
              if (option.isRuleAST() && option.first() != S.ImageSize
                  && !GraphGraphics.isPlotGraphOption(option)) {
                gApp.append(option);
              }
            }
            if (!graph.options().exists(x -> x.isRuleAST() && x.first() == S.ImageSize)) {
              gApp.append(F.Rule(S.ImageSize, F.ZZ(200)));
            }
            return gApp;
          }
          return gExpr;
        } catch (Exception e) {
          // Fallthrough
        }
      }
      return F.NIL;
    }
  }

  /**
   * <code>HighlightGraph(g, {v1, Style(v2, c), e1, ...})</code> - the graph <code>g</code> with the
   * given vertices and edges drawn in their own style, red when none is given.
   */
  private static class HighlightGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2) {
        return F.NIL;
      }
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> graph = gex.toData();
      IAST highlights = ast.arg2().isList() ? (IAST) ast.arg2() : F.list(ast.arg2());
      IASTAppendable options = gex.options().copyAppendable();
      for (IExpr highlight : highlights) {
        IExpr item = highlight;
        IExpr style = F.RGBColor(1.0, 0.0, 0.0);
        if (highlight.isAST(S.Style) && highlight.argSize() >= 2) {
          item = highlight.first();
          style = GraphExpr.styleDirective((IAST) highlight);
        }
        IExpr edge = GraphExpr.unwrapEdge(item);
        if (graph.containsVertex(item)) {
          GraphExpr.addProperty(options, S.VertexStyle, item, style);
        } else if (edge.isAST() && edge.argSize() == 2
            && (edge.isRuleAST() || edge.isAST(S.DirectedEdge) || edge.isAST(S.UndirectedEdge)
                || edge.isAST(S.TwoWayRule))) {
          GraphExpr.addProperty(options, S.EdgeStyle, edge, style);
        }
      }
      return GraphExpr.newInstance(graph, options);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  private static class GraphIntersection extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        // GraphUnion(g1, g2, ..., opts) - the trailing options go onto the result
        int graphs = graphArguments(ast);
        GraphExpr<?> gex1 = GraphExpr.newInstance(ast.arg1());
        if (gex1 == null || graphs < 1) {
          return F.NIL;
        }
        Graph<IExpr, ? extends IExprEdge> resultGraph =
            (Graph<IExpr, ? extends IExprEdge>) gex1.toData();
        GraphType t = resultGraph.getType();
        if (t == null) {
          return F.NIL;
        }
        resultGraph = applyFunctionArg1(resultGraph);
        for (int i = 2; i <= graphs; i++) {
          GraphExpr<?> gexArg = GraphExpr.newInstance(ast.get(i));
          if (gexArg == null) {
            return F.NIL;
          }
          Graph<IExpr, ? extends IExprEdge> graphArg =
              (Graph<IExpr, ? extends IExprEdge>) gexArg.toData();
          Graph<IExpr, ? extends IExprEdge> newGraph;
          if (t.isDirected()) {
            newGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          } else {
            newGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          }

          setOperation(resultGraph, graphArg, newGraph);
          resultGraph = newGraph;
        }
        return withOptions(resultGraph, ast, graphs);

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphIntersection, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Prepare the first element of multiple graph arguments for a set operation.
     * 
     * @param graph
     * @return
     * @see GraphDisjointUnion#applyFunctionArg1(Graph)
     */
    protected Graph<IExpr, ? extends IExprEdge> applyFunctionArg1(
        Graph<IExpr, ? extends IExprEdge> graph) {
      return graph;
    }

    /**
     * The default Set operation is <code>intersection</code>. This method must be overridden in
     * inherited classes.
     * 
     * @param graph1
     * @param graph2
     * @param resultGraph
     * @return
     */
    protected void setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ? extends IExprEdge> resultGraph) {
      for (IExpr v : Sets.union(graph1.vertexSet(), graph2.vertexSet())) {
        resultGraph.addVertex(v);
      }
      // an undirected edge is the same whichever way round it is written
      for (IExprEdge e : graph1.edgeSet()) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        if (graph2.containsEdge(v1, v2)) {
          resultGraph.addEdge(v1, v2);
        }
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // GraphUnion(g), GraphIntersection(g) give g
      return ARGS_1_INFINITY;
    }
  }


  /**
   * The number of leading arguments of a graph combinator which are graphs: the arguments before
   * the first option rule, or <code>-1</code> when a rule is followed by something else.
   */
  private static int graphArguments(IAST ast) {
    int graphs = ast.argSize();
    for (int i = 1; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        graphs = i - 1;
        break;
      }
    }
    for (int i = graphs + 1; i < ast.size(); i++) {
      if (!ast.get(i).isRuleAST()) {
        return -1;
      }
    }
    return graphs;
  }

  /** The graph with the trailing option rules of the combinator call, such as a GraphLayout. */
  private static IExpr withOptions(Graph<IExpr, ?> graph, IAST ast, int graphs) {
    if (graphs == ast.argSize()) {
      return GraphExpr.newInstance(graph);
    }
    IASTAppendable options = F.ListAlloc(ast.argSize() - graphs);
    for (int i = graphs + 1; i < ast.size(); i++) {
      options.append(ast.get(i));
    }
    return GraphExpr.newInstance(graph, options);
  }

  private static class GraphComplement extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = GraphExpr.newInstance(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        Graph<IExpr, ExprEdge> graph = (Graph<IExpr, ExprEdge>) gex1.toData();

        ComplementGraphGenerator<IExpr, ExprEdge> complementGraphGenerator =
            new ComplementGraphGenerator<IExpr, ExprEdge>(graph);
        Graph<IExpr, ExprEdge> resultGraph;
        GraphType t = graph.getType();
        if (t != null) {
          if (t.isDirected()) {
            resultGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          } else {
            resultGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          }
          complementGraphGenerator.generateGraph(resultGraph);
          return GraphExpr.newInstance(resultGraph);
        }


      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphComplement, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  private static class GraphDifference extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (graphArguments(ast) != 2) {
          return F.NIL;
        }
        GraphExpr<?> gex1 = GraphExpr.newInstance(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        GraphExpr<?> gex2 = GraphExpr.newInstance(ast.arg2());
        if (gex2 == null) {
          return F.NIL;
        }
        Graph<IExpr, ExprEdge> g1 = (Graph<IExpr, ExprEdge>) gex1.toData();
        Graph<IExpr, ExprEdge> g2 = (Graph<IExpr, ExprEdge>) gex2.toData();

        Graph<IExpr, ExprEdge> resultGraph;
        GraphType t = g1.getType();
        if (t != null) {
          if (t.isDirected()) {
            resultGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          } else {
            resultGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
          }
          setOperation(g1, g2, resultGraph);
          return withOptions(resultGraph, ast, 2);
        }

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphDifference, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    protected void setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ExprEdge> resultGraph) {
      for (IExpr v : Sets.union(graph1.vertexSet(), graph2.vertexSet())) {
        resultGraph.addVertex(v);
      }
      Set<? extends IExprEdge> graphSet = Sets.difference(graph1.edgeSet(), graph2.edgeSet());
      for (IExprEdge e : graphSet) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        if (resultGraph.containsVertex(v1) && resultGraph.containsVertex(v2)) {
          resultGraph.addEdge(v1, v2);
        }
      }
    }

  }


  private static class IndexGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = GraphExpr.newInstance(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> graph = gex1.toData();

        int newIndex = 1;
        if (ast.isAST2()) {
          int intIndex = ast.arg2().toMachineInt();
          if (F.isNotPresent(intIndex) || intIndex == Integer.MAX_VALUE) {
            return F.NIL;
          }
          newIndex = intIndex;
        }
        Graph<IExpr, ?> resultGraph = GraphExpr.createGraph(graph, newIndex);
        if (resultGraph != null) {
          return GraphExpr.newInstance(resultGraph);
        }

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.IndexGraph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  private static class GraphDisjointUnion extends GraphUnion {
    @Override
    protected Graph<IExpr, ? extends IExprEdge> applyFunctionArg1(
        Graph<IExpr, ? extends IExprEdge> graph) {
      return GraphExpr.createGraph(graph, 1);
    }

    @Override
    protected void setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ? extends IExprEdge> resultGraph) {
      Graph<IExpr, ? extends IExprEdge> g2 =
          GraphExpr.createGraph(graph2, graph1.vertexSet().size() + 1);
      super.setOperation(graph1, g2, resultGraph);
    }

  }


  private static class GraphUnion extends GraphIntersection {

    @Override
    protected void setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ? extends IExprEdge> resultGraph) {
      for (IExpr v : Sets.union(graph1.vertexSet(), graph2.vertexSet())) {
        resultGraph.addVertex(v);
      }
      Set<? extends IExprEdge> graphSet = Sets.union(graph1.edgeSet(), graph2.edgeSet());
      for (IExprEdge e : graphSet) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        if (resultGraph.containsVertex(v1) && resultGraph.containsVertex(v2)) {
          resultGraph.addEdge(v1, v2);
        }
      }
    }

  }


  /**
   *
   *
   * <pre>
   * <code>Graph({edge1,...,edgeN})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a graph from the given edges <code>edge1,...,edgeN</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)">Wikipedia - Graph</a>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * A directed graph:
   *
   * <pre>
   * <code>&gt;&gt; Graph({1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 4 -&gt; 1})
   * </code>
   * </pre>
   *
   * <p>
   * An undirected graph:
   *
   * <pre>
   * <code>&gt;&gt; Graph({1 &lt;-&gt; 2, 2 &lt;-&gt; 3, 3 &lt;-&gt; 4, 4 &lt;-&gt; 1})
   * </code>
   * </pre>
   *
   * <p>
   * An undirected weighted graph:
   *
   * <pre>
   * <code>&gt;&gt; Graph({1 &lt;-&gt; 2, 2 &lt;-&gt; 3, 3 &lt;-&gt; 4, 4 &lt;-&gt; 1},{EdgeWeight-&gt;{2.0,3.0,4.0, 5.0}})
   * </code>
   * </pre>
   */
  private static class GraphCTor extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = GraphExpr.newInstance(ast);
          if (gex != null) {
            return gex;
          }
        } else if (ast.size() >= 3 && ast.arg1().isList()) {
          if (ast.isAST2() && GraphExpr.isInternalEdges(ast.arg2())) {
            // {directed, undirected}: a SparseArray keeps its form, index pairs become edges
            Graph<IExpr, Object> graph = GraphExpr.createGraph((IAST) ast.arg1(),
                ast.arg2().first(), ast.arg2().second(), F.CEmptyList);
            boolean sparse = ast.arg2().first() instanceof SparseArrayExpr
                || ast.arg2().second() instanceof SparseArrayExpr;
            return sparse ? GraphExpr.newInstance(graph, true) : GraphExpr.newInstance(graph);
          }
          IExpr edgeWeight = F.NIL;
          final OptionArgs options = new OptionArgs(S.Graph, ast, ast.argSize(), engine);
          IExpr option = options.getOption(S.EdgeWeight);
          if (option.isPresent() && option != S.Automatic) {
            edgeWeight = option;
          }
          EdgeListType t = ast.arg1().isListOfEdges();
          // the options stay with the graph, for drawing it; a list of rules is options too, as in
          // Graph({1, 2}, {1 <-> 2}, {GraphLayout -> ..., GraphStyle -> ...})
          IASTAppendable graphOptions = F.ListAlloc();
          for (int i = t != null ? 2 : 3; i < ast.size(); i++) {
            IExpr arg = ast.get(i);
            if (arg.isRuleAST()) {
              graphOptions.append(arg);
            } else if (arg.isList() && arg.argSize() > 0
                && ((IAST) arg).forAll(x -> x.isRuleAST())) {
              graphOptions.appendArgs((IAST) arg);
            }
          }
          GraphExpr<?> result = null;
          if (t != null) {
            if (edgeWeight.isList()) {
              result = GraphExpr.createWeightedGraph(F.NIL, (IAST) ast.arg1(), (IAST) edgeWeight);
            } else {
              result = GraphExpr.newInstance(F.NIL, (IAST) ast.arg1());
            }
          } else {
            IAST vertices = GraphExpr.stripVertexAnnotations((IAST) ast.arg1(), graphOptions);
            if (edgeWeight.isList()) {
              result =
                  GraphExpr.createWeightedGraph(vertices, (IAST) ast.arg2(), (IAST) edgeWeight);
            } else if (ast.arg2().isList()) {
              result = GraphExpr.newInstance(vertices, (IAST) ast.arg2());
            }
          }
          if (result != null) {
            if (graphOptions.argSize() > 0) {
              result.setOptions(graphOptions);
            }
            return result;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Graph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.EdgeWeight, S.Automatic)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>GraphCenter(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the <code>graph</code> center. The center of a <code>graph</code> is the set of
   * vertices of graph eccentricity equal to the <code>graph</code> radius.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_center">Wikipedia - Graph center</a>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   */
  private static class GraphCenter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        return eccentricityVertices(gex, true);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphCenter, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>GraphDiameter(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the diameter of the <code>graph</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Distance_(graph_theory)#Related_concepts">Wikipedia
   * - Distance (graph theory) - Related concepts</a>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   */
  private static class GraphDiameter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        return eccentricityBound(gex, false);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphDiameter, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>GraphPeriphery(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the <code>graph</code> periphery. The periphery of a <code>graph</code> is the set of
   * vertices of graph eccentricity equal to the graph diameter.
   *
   * </blockquote>
   */
  private static class GraphPeriphery extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> g = gex.toData();
        // boolean pseudoDiameter = false;
        // if (ast.isAST2()) {
        // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        // IExpr option = options.getOption(F.Method);
        //
        // if (option.isPresent() && option.toString().equals("PseudoDiameter")) {
        // pseudoDiameter = true;
        // } else if (option.isNIL()) {
        // return engine.printMessage("GraphPeriphery: Option PseudoDiameter expected!");
        // }
        // }

        return eccentricityVertices(gex, false);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphPeriphery, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class GraphPower extends AbstractEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg2().toMachineInt();
      if (n >= 0) {
        try {
          GraphExpr<? extends IExprEdge> gex =
              (GraphExpr<ExprEdge>) GraphExpr.newInstance(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ? extends IExprEdge> result = graphPower(gex.toData(), n);
          if (result != null) {
            return GraphExpr.newInstance(result);
          }

        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.GraphPower, rex, engine);
        }
      }
      return F.NIL;
    }

    public static Graph<IExpr, ? extends IExprEdge> graphPower(
        Graph<IExpr, ? extends IExprEdge> graph, int n) {
      GraphType t = graph.getType();
      if (t == null) {
        return null;
      }

      Graph<IExpr, ExprEdge> result;
      if (t.isDirected()) {
        result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      } else {
        result = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      }

      // Add all vertices to the new graph
      for (IExpr vertex : graph.vertexSet()) {
        result.addVertex(vertex);
      }

      // Add edges to the new graph
      for (IExpr v1 : graph.vertexSet()) {
        for (IExpr v2 : graph.vertexSet()) {
          if (!v1.equals(v2)) {
            GraphPath<IExpr, ? extends IExprEdge> path =
                DijkstraShortestPath.findPathBetween(graph, v1, v2);
            if (path != null && path.getLength() <= n) {
              result.addEdge(v1, v2);
            }
          }
        }
      }

      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }
  /**
   *
   *
   * <pre>
   * <code>GraphQ(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * test if <code>expr</code> is a graph object.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)">Wikipedia - Graph</a>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; GraphQ(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}) )
   * True
   *
   * &gt;&gt; GraphQ( Sin(x) )
   * False
   * </code>
   * </pre>
   */
  private static class GraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex != null) {
            return S.True;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphQ, rex, engine);
      }
      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>GraphRadius(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the radius of the <code>graph</code>.
   *
   * </blockquote>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Distance_(graph_theory)#Related_concepts">Wikipedia
   * - Distance (graph theory) - Related concepts</a>
   * </ul>
   */
  private static class GraphRadius extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        return eccentricityBound(gex, true);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.GraphRadius, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code> FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find a shortest tour in the <code>graph</code> with minimum <code>EuclideanDistance</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem">Wikipedia - Travelling
   * salesman problem</a>
   * </ul>
   *
   * <h3>Related terms</h3>
   */
  private static class FindShortestTour extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          if (ast.arg1().isListOfLists()) {
            int[] dim = ast.arg1().isMatrix();
            if (dim != null) {
              IAST m = (IAST) ast.arg1();
              double[][] matrix = m.toDoubleMatrix(true);
              if (matrix != null) {
                int rowDim = dim[0];
                int colDim = dim[1];
                if (colDim == 2) {
                  Graph<IInteger, ExprWeightedEdge> g =
                      new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);
                  // define the vertices as integer numbers 1..rowDim
                  for (int i = 1; i <= rowDim; i++) {
                    g.addVertex(F.ZZ(i));
                  }

                  // create all possible edges between the given vertices
                  for (int i = 0; i < rowDim; i++) {
                    for (int j = i + 1; j < rowDim; j++) {
                      g.setEdgeWeight(g.addEdge(F.ZZ(i + 1), F.ZZ(j + 1)), // EuclideanDistance
                          MathArrays.distance(matrix[i], matrix[j]));
                    }
                  }
                  GraphPath<IInteger, ExprWeightedEdge> tour =
                      new HeldKarpTSP<IInteger, ExprWeightedEdge>().getTour(g);

                  // calculate the shortest tour from the sum of distances and
                  // create list of vertices for the shortest tour
                  List<IInteger> tourPositions = tour.getVertexList();
                  IASTAppendable shortestTourList = F.ListAlloc(tourPositions.size());
                  IASTAppendable sum = F.PlusAlloc(tourPositions.size());
                  IInteger lastPosition = tourPositions.get(tourPositions.size() - 1);
                  shortestTourList.append(lastPosition);
                  for (int i = tourPositions.size() - 2; i >= 0; i--) {
                    IInteger position = tourPositions.get(i);
                    shortestTourList.append(position);
                    sum.append(F.EuclideanDistance(m.get(lastPosition), m.get(position)));
                    lastPosition = position;
                  }
                  return F.list(sum, shortestTourList);
                }
              }
            }
          } else if (ast.arg1().isList()) {
            IAST list = (IAST) ast.arg1();
            if (list.size() > 2 && list.forAll(x -> (x instanceof GeoPositionExpr))) {
              int rowDim = list.size() - 1;
              Graph<IInteger, ExprWeightedEdge> g =
                  new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);
              // define the vertices as integer numbers 1..rowDim
              for (int i = 1; i <= rowDim; i++) {
                g.addVertex(F.ZZ(i));
              }

              // create all possible edges between the given vertices
              for (int i = 0; i < rowDim - 1; i++) {
                GeoPositionExpr p1 = (GeoPositionExpr) list.get(i + 1);
                for (int j = i + 1; j < rowDim; j++) {
                  GeoPositionExpr p2 = (GeoPositionExpr) list.get(j + 1);
                  g.setEdgeWeight(g.addEdge(F.ZZ(i + 1), F.ZZ(j + 1)), geodesicMeters(p1, p2));
                }
              }
              GraphPath<IInteger, ExprWeightedEdge> tour =
                  new HeldKarpTSP<IInteger, ExprWeightedEdge>().getTour(g);

              // calculate the shortest tour from the sum of distances and
              // create list of vertices for the shortest tour
              List<IInteger> tourPositions = tour.getVertexList();
              IASTAppendable shortestTourList = F.ListAlloc(tourPositions.size());
              IASTAppendable sum = F.PlusAlloc(tourPositions.size());
              IInteger lastPosition = tourPositions.get(tourPositions.size() - 1);
              shortestTourList.append(lastPosition);
              for (int i = tourPositions.size() - 2; i >= 0; i--) {
                IInteger position = tourPositions.get(i);
                shortestTourList.append(position);
                // The leg lengths are emitted as quantities rather than as GeoDistance calls:
                // GeoDistance lives in the matheclipse-astro module and measures a rhumb line,
                // which would not agree with the geodesic edge weights the tour was optimized on.
                sum.append(F.Quantity(F.num(geodesicMeters((GeoPositionExpr) list.get(lastPosition),
                    (GeoPositionExpr) list.get(position))), F.stringx("m")));
                lastPosition = position;
              }
              return F.list(F.UnitConvert(sum, F.stringx("mi")), shortestTourList);
            }
            // } else {
            // GraphExpr<ExprEdge> gex = createGraph(ast.arg1());
            // if (gex == null) {
            // return F.NIL;
            // }
            //
            // Graph<IExpr, ExprEdge> g = gex.toData();
            //
            // GraphPath<IExpr, ExprEdge> tour = new HeldKarpTSP<IExpr, ExprEdge>()
            // .getTour(g);
            //
            // // calculate the shortest tour from the sum of distances and
            // // create list of vertices for the shortest tour
            // List<IExpr> tourPositions = tour.getVertexList();
            // IASTAppendable shortestTourList = F.ListAlloc(tourPositions.size());
            // IExpr lastPosition = tourPositions.get(tourPositions.size() - 1);
            // shortestTourList.append(lastPosition);
            // for (int i = tourPositions.size() - 2; i >= 0; i--) {
            // IExpr position = tourPositions.get(i);
            // shortestTourList.append(position);
            // lastPosition = position;
            // }
            // return F.List(F.num(tour.getWeight()), shortestTourList);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.FindShortestTour, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    /**
     * The length of the geodesic between two positions, in meters.
     *
     * <p>
     * A tour is optimized for the <em>shortest</em> route, so the metric has to be the geodesic.
     * Note that this is not what <code>GeoDistance</code> reports: that is implemented in the
     * matheclipse-astro module and measures a rhumb line, which is always at least as long.
     */
    private static double geodesicMeters(GeoPositionExpr p1, GeoPositionExpr p2) {
      return GeodesicSolver.pointToPointDistance(ReferenceEllipsoid.WGS84, //
          p1.latitude(), p1.longitude(), p1.altitude(), //
          p2.latitude(), p2.longitude(), p2.altitude());
    }
  }


  /**
   *
   *
   * <pre>
   * <code> FindSpanningTree(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find the minimum spanning tree in the <code>graph</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Minimum_spanning_tree">Wikipedia - Minimum spanning
   * tree</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindSpanningTree(Graph({a,b,c,d,e,f},{a&lt;-&gt;b,a&lt;-&gt;d,b&lt;-&gt;c,b&lt;-&gt;d,b&lt;-&gt;e,c&lt;-&gt;e,c&lt;-&gt;f,d&lt;-&gt;e,e&lt;-&gt;f}, {EdgeWeight-&gt;{1.0,3.0,6.0,5.0,1.0,5.0,2.0,1.0,4.0}}))
   * </code>
   * </pre>
   */
  private static class FindSpanningTree extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }

          if (gex.isWeightedGraph()) {
            Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
            SpanningTreeAlgorithm<ExprWeightedEdge> k =
                new BoruvkaMinimumSpanningTree<IExpr, ExprWeightedEdge>(g);
            Set<ExprWeightedEdge> edgeSet = k.getSpanningTree().getEdges();
            Graph<IExpr, ExprWeightedEdge> gResult =
                new DefaultDirectedWeightedGraph<IExpr, ExprWeightedEdge>(ExprWeightedEdge.class);
            Graphs.addAllEdges(gResult, g, edgeSet);
            return GraphExpr.newInstance(gResult);
          } else {
            Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
            SpanningTreeAlgorithm<ExprEdge> k = new BoruvkaMinimumSpanningTree<IExpr, ExprEdge>(g);
            Set<ExprEdge> edgeSet = k.getSpanningTree().getEdges();
            Graph<IExpr, ExprEdge> gResult =
                new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
            Graphs.addAllEdges(gResult, g, edgeSet);
            return GraphExpr.newInstance(gResult);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.FindSpanningTree, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>AdjacencyMatrix(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>graph</code> into a adjacency matrix.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Adjacency_matrix">Wikipedia - Adjacency matrix</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; AdjacencyMatrix(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}))
   * {{0,1,1,0},
   *  {0,0,1,0},
   *  {0,0,0,0},
   *  {0,1,0,0}}
   * </code>
   * </pre>
   */
  private static class AdjacencyMatrix extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      if (gex.isWeightedGraph()) {
        return GraphExpr
            .weightedGraphToAdjacencyMatrix((Graph<IExpr, ExprWeightedEdge>) gex.toData());
      }
      return GraphExpr.graphToAdjacencyMatrix(gex.toData());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }



  private static class EdgeCount extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();

      if (ast.isAST2()) {
        // EdgeCount(graph, patt) - count the edges matching `patt`
        IAST edges = GraphExpr.edgesToIExpr(g)[0];
        return F.ZZ(GraphFunctions.countMatches(edges, ast.arg2(), engine));
      }
      if (ast.argSize() == 1) {
        GraphType type = g.getType();

        if (GraphExpr.isMixedGraph(g)) {
          // a mixed graph keeps the orientation per edge, no pair of anti parallel edges is
          // merged into a single undirected edge
          return F.ZZ(g.edgeSet().size());
        }
        if (type.isDirected()) {
          // Count edges, but treat 1->2 and 2->1 as a single undirected pair
          Set<Set<IExpr>> uniquePairs = new HashSet<>();
          int directedOnlyCount = 0;

          for (IExprEdge edge : (Set<IExprEdge>) g.edgeSet()) {
            IExpr u = edge.lhs();
            IExpr v = edge.rhs();

            // Check if the reverse edge exists
            if (g.containsEdge(v, u)) {
              // Canonicalize as a pair to count 1->2 and 2->1 as 1
              Set<IExpr> pair = new HashSet<IExpr>();
              pair.add(u);
              pair.add(v);
              // System.out.println(pair);
              uniquePairs.add(pair);
            } else {
              // No reverse edge, count as a single directed edge
              directedOnlyCount++;
            }
          }
          return F.ZZ(uniquePairs.size() + directedOnlyCount);
        } else {
          // Standard undirected case (normalize JGraphT internal directed representation)
          return F.ZZ(g.edgeSet().size());
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>EdgeList(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>graph</code> into a list of edges.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; EdgeList(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}))
   * {1-&gt;2,2-&gt;3,1-&gt;3,4-&gt;2}
   * </code>
   * </pre>
   */
  private static class EdgeList extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();
      IAST edges = GraphExpr.edgesToIExpr(g)[0];
      if (ast.isAST2()) {
        // EdgeList(graph, patt) - the edges matching `patt`
        return GraphFunctions.selectMatches(edges, ast.arg2(), engine);
      }
      return edges;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>EdgeQ(graph, edge)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * test if <code>edge</code> is an edge in the <code>graph</code> object.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; EdgeQ(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}),2 -&gt; 3)
   * True
   *
   * &gt;&gt; EdgeQ(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}),2 -&gt; 4)
   * False
   * </code>
   * </pre>
   */
  private static class EdgeQ extends AbstractEvaluator {
    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      if (ast.isAST2() && ast.arg2().isEdge()) {
        IAST edge = (IAST) ast.arg2();
        GraphExpr<?> gex = getGraphExpr(ast.arg1());
        if (gex != null) {
          Graph<IExpr, ?> g = gex.toData();
          return F.booleSymbol(g.containsEdge(edge.first(), edge.second()));
        }
      }

      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static class EdgeRules extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();
      return GraphExpr.edgesToRules(g)[0];
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class ClosenessCentrality extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      GraphView view = GraphView.of(gex.toData());
      if (view.hasNegativeWeight()) {
        return F.NIL;
      }
      // the number of vertices reachable from v divided by the sum of their distances,
      // 0 if no vertex can be reached
      return F.mapRange(0, view.n, v -> {
        int reachable = 0;
        double sum = 0.0;
        double[] distance = view.distances(v);
        for (int w = 0; w < view.n; w++) {
          if (w != v && !Double.isInfinite(distance[w])) {
            reachable++;
            sum += distance[w];
          }
        }
        return F.num(sum > 0.0 ? reachable / sum : 0.0);
      });
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ConnectedGraphQ extends AbstractEvaluator {
    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex == null) {
        // "gives False for anything that is not a connected graph"
        return S.False;
      }
      Graph<IExpr, ? extends IExprEdge> graph = (Graph<IExpr, ? extends IExprEdge>) gex.toData();
      if (graph.vertexSet().isEmpty()) {
        // the empty graph is connected
        return S.True;
      }
      return GraphTests.isStronglyConnected(graph) ? S.True : S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BetweennessCentrality extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      GraphView view = GraphView.of(gex.toData());
      double[] score = GraphUtil.betweenness(view, null);
      return F.mapRange(0, view.n, v -> F.num(score[v]));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BipartiteGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ? extends IExprEdge> graph = (Graph<IExpr, ? extends IExprEdge>) gex.toData();
      return GraphTests.isBipartite(graph) ? S.True : S.False;

    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class EigenvectorCentrality extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      boolean in = true;
      if (ast.isAST2()) {
        if (ast.arg2().isString("In")) {
          in = true;
        } else if (ast.arg2().isString("Out")) {
          in = false;
        } else {
          return F.NIL;
        }
      }
      GraphView view = GraphView.of(gex.toData());
      // the Perron vector of every strongly connected component, a vertex scoring the
      // sum over its predecessors ("In") or successors ("Out")
      double[] x = GraphUtil.eigenvectorCentrality(view, in);
      if (x == null) {
        return F.NIL;
      }
      return F.mapRange(0, view.n, v -> F.num(x[v]));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }
  /**
   *
   *
   * <pre>
   * <code>EulerianGraphQ(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>graph</code> is an eulerian graph, and <code>False</code>
   * otherwise.
   *
   * </blockquote>
   */
  private static class EulerianGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex == null) {
        return S.False;
      }

      // if (gex.isWeightedGraph()) {
      // Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>)
      // gex.toData();
      // GraphPath<IExpr, ExprWeightedEdge> path = weightedEulerianCycle(g);
      // if (path != null) {
      // // Graph is Eulerian
      // return S.True;
      // }
      // } else {
      // Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
      GraphPath<IExpr, ?> path = eulerianCycle(gex);
      if (path != null) {
        // Graph is Eulerian
        return S.True;
      }
      // }

      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>HamiltonianGraphQ(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>graph</code> is an hamiltonian graph, and <code>False
   * </code> otherwise.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Hamiltonian_path">Wikipedia - Hamiltonian path</a>
   * <li><a href="https://en.wikipedia.org/wiki/Hamiltonian_path_problem">Wikipedia - Hamiltonian
   * path problem</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; HamiltonianGraphQ(Graph({1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 4 -&gt; 1}))
   * True
   * </code>
   * </pre>
   */
  private static class HamiltonianGraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      if (ast.isAST1()) {
        GraphExpr<?> gex = getGraphExpr(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        List<int[][]> cycles =
            GraphUtil.hamiltonianCycles(GraphView.of(gex.toData()), 1, HAMILTONIAN_SEARCH_NODES);
        if (cycles == null) {
          // the search gave up
          return F.NIL;
        }
        return F.booleSymbol(!cycles.isEmpty());
      }

      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class IsomorphicGraphQ extends AbstractEvaluator {
    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      // IsomorphicGraphQ(g1, g2, ...) - all graphs are isomorphic to the first
      GraphExpr<?> gex1 = getGraphExpr(ast.arg1());
      if (gex1 == null) {
        return F.False;
      }
      for (int i = 2; i < ast.size(); i++) {
        GraphExpr<?> gex2 = getGraphExpr(ast.get(i));
        if (gex2 == null) {
          return F.False;
        }
        Iterator<? extends GraphMapping<IExpr, ?>> mappings =
            isomorphisms(gex1.toData(), gex2.toData());
        if (mappings == null) {
          return F.NIL;
        }
        if (!mappings.hasNext()) {
          return F.False;
        }
      }
      return F.True;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }


  private static class FindCycle extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      // FindCycle(g), FindCycle(g, k) - length at most k, FindCycle(g, {k}) - length k,
      // FindCycle(g, {kmin, kmax}), FindCycle(g, kspec, n); FindCycle({g, v}, ...) - through v
      int minCycleLength = 1;
      int maxCycleLength = Integer.MAX_VALUE;
      int atMostCycles = 1;
      if (ast.argSize() >= 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isInfinity()) {
          // fall through
        } else if (arg2.isList1()) {
          minCycleLength = arg2.first().toIntDefault();
          maxCycleLength = minCycleLength;
        } else if (arg2.isList2()) {
          minCycleLength = arg2.first().toIntDefault();
          maxCycleLength =
              arg2.second().isInfinity() ? Integer.MAX_VALUE : arg2.second().toIntDefault();
        } else {
          maxCycleLength = arg2.toIntDefault();
        }
        if (minCycleLength <= 0 || maxCycleLength <= 0) {
          // The argument `2` in `1` is not a valid parameter.
          return Errors.printMessage(ast.topHead(), "inv", F.List(ast, arg2), engine);
        }
      }
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (arg3 == S.All || arg3.isInfinity()) {
          atMostCycles = Integer.MAX_VALUE;
        } else {
          atMostCycles = arg3.toIntDefault();
          if (atMostCycles <= 0) {
            // The argument `2` in `1` is not a valid parameter.
            return Errors.printMessage(ast.topHead(), "inv", F.List(ast, arg3), engine);
          }
        }
      }
      IExpr arg1 = ast.arg1();
      IExpr through = F.NIL;
      if (arg1.isList2() && arg1.first() instanceof GraphExpr) {
        through = arg1.second();
        arg1 = arg1.first();
      }
      GraphExpr<?> gex = GraphExpr.newInstance(arg1);
      if (gex == null) {
        return F.NIL;
      }
      GraphView view = GraphView.of(gex.toData());
      int throughIndex = -1;
      if (through.isPresent()) {
        Integer index = view.index.get(through);
        if (index == null) {
          return F.NIL;
        }
        throughIndex = index;
      }
      List<int[][]> cycles =
          GraphUtil.simpleCycles(view, minCycleLength, maxCycleLength, atMostCycles, throughIndex);
      IASTAppendable result = F.ListAlloc(cycles.size());
      for (int[][] cycle : cycles) {
        result.append(GraphUtil.walkEdges(view, cycle[0], cycle[1]));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }


  /**
   *
   *
   * <pre>
   * <code> FindEulerianCycle(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find an eulerian cycle in the <code>graph</code> - a cycle traversing every edge exactly once.
   * The result is a list of cycles, holding the one that was found, and <code>{}</code> when the
   * graph has none. <code>FindEulerianCycle(graph, k)</code> asks for at most <code>k</code> of
   * them; only one is produced.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Eulerian_path">Wikipedia - Eulerian path</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindEulerianCycle(Graph({1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 4 -&gt; 1}))
   * {{4-&gt;1,1-&gt;2,2-&gt;3,3-&gt;4}}
   * </code>
   * </pre>
   *
   * <pre>
   * <code>&gt;&gt; FindEulerianCycle(Graph({1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 3 -&gt; 1}))
   * {}
   * </code>
   * </pre>
   */
  private static class FindEulerianCycle extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (!arg2.isInfinity() && arg2 != S.All && arg2.toIntDefault() < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }

      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      GraphPath<IExpr, ?> path = eulerianCycle(gex);
      if (path == null) {
        // Graph is not Eulerian
        return F.CEmptyList;
      }
      final List<IExpr> iList = path.getVertexList();
      // a list of cycles holding the one that was found, as FindPostmanTour reports its tours
      return F.list(
          F.mapRange(0, iList.size() - 1, i -> F.DirectedEdge(iList.get(i), iList.get(i + 1))));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code> FindHamiltonianCycle(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find an hamiltonian cycle in the <code>graph</code> - a cycle visiting every vertex exactly
   * once. The result is a list of cycles, holding the one that was found, and <code>{}</code> when
   * the graph has none. <code>FindHamiltonianCycle(graph, k)</code> asks for at most <code>k</code>
   * of them; only one is produced.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Hamiltonian_path">Wikipedia - Hamiltonian path</a>
   * <li><a href="https://en.wikipedia.org/wiki/Hamiltonian_path_problem">Wikipedia - Hamiltonian
   * path problem</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindHamiltonianCycle( {1 -&gt; 2, 2 -&gt; 3, 3 -&gt; 4, 4 -&gt; 1} )
   * {{1-&gt;2,2-&gt;3,3-&gt;4,4-&gt;1}}
   * </code>
   * </pre>
   */
  private static class FindHamiltonianCycle extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (!arg2.isInfinity() && arg2 != S.All && arg2.toIntDefault() < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }

      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      int limit = 1;
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        limit = (arg2.isInfinity() || arg2 == S.All) ? Integer.MAX_VALUE : arg2.toIntDefault();
      }
      GraphView view = GraphView.of(gex.toData());
      List<int[][]> cycles = GraphUtil.hamiltonianCycles(view, limit, HAMILTONIAN_SEARCH_NODES);
      if (cycles == null) {
        // the search gave up
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(cycles.size());
      for (int[][] cycle : cycles) {
        result.append(GraphUtil.walkEdges(view, cycle[0], cycle[1]));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  private static class FindGraphIsomorphism extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex1 = GraphExpr.newInstance(ast.arg1());
      if (gex1 == null) {
        return F.NIL;
      }
      GraphExpr<?> gex2 = GraphExpr.newInstance(ast.arg2());
      if (gex2 == null) {
        return F.NIL;
      }
      int maximum = 1;
      if (ast.isAST3()) {
        if (ast.arg3() == S.All || ast.arg3().isInfinity()) {
          maximum = Integer.MAX_VALUE;
        } else {
          maximum = ast.arg3().toIntDefault();
          if (maximum < 1) {
            // Positive machine-sized integer expected at position `2` in `1`.
            return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C3), engine);
          }
        }
      }
      Graph<IExpr, ?> g1 = gex1.toData();
      Iterator<? extends GraphMapping<IExpr, ?>> mappings = isomorphisms(g1, gex2.toData());
      if (mappings == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc();
      while (mappings.hasNext() && result.argSize() < maximum) {
        OperationSystem.checkInterrupt();
        GraphMapping<IExpr, ?> mapping = mappings.next();
        // the rules in VertexList order of the first graph
        result.append(F.assoc(
            F.mapSet(g1.vertexSet(), v -> F.Rule(v, mapping.getVertexCorrespondence(v, true)))));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }


  private static class FindMinimumCostFlow extends AbstractEvaluator {
    private static class MinimumCostFlowProblemImpl
        implements MinimumCostFlowProblem<IExpr, ExprEdge> {
      private final Graph<IExpr, ExprEdge> graph;
      private final Map<IExpr, Integer> supplyMap;
      private final Map<ExprEdge, Integer> capacityMap;
      private final Map<ExprEdge, Integer> costMap;

      private MinimumCostFlowProblemImpl(Graph<IExpr, ExprEdge> graph,
          Map<IExpr, Integer> supplyMap, Map<ExprEdge, Integer> capacityMap,
          Map<ExprEdge, Integer> costMap) {
        this.graph = graph;
        this.supplyMap = supplyMap;
        this.capacityMap = capacityMap;
        this.costMap = costMap;
      }

      @Override
      public Graph<IExpr, ExprEdge> getGraph() {
        return graph;
      }

      @Override
      public Function<IExpr, Integer> getNodeSupply() {
        return v -> {
          int val = supplyMap.getOrDefault(v, 0);
          return val;
        };
      }

      @Override
      public Function<ExprEdge, Integer> getArcCapacityLowerBounds() {
        return e -> costMap.getOrDefault(e, 0);
      }

      @Override
      public Function<ExprEdge, Integer> getArcCapacityUpperBounds() {
        return e -> capacityMap.getOrDefault(e, 0);
      }

      @Override
      public Function<ExprEdge, Double> getArcCosts() {
        return x -> 0.0;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr startVertex = ast.arg2();
        IExpr endVertex = ast.arg3();
        // if (!arg2.isList()) {
        // return F.NIL;
        // }
        GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        Graph<IExpr, ExprEdge> graph = (Graph<IExpr, ExprEdge>) gex.toData();
        Map<ExprEdge, Integer> capacityMap = new HashMap<>();
        Map<ExprEdge, Integer> costMap = new HashMap<>();
        Map<IExpr, Integer> supplyMap = new HashMap<>();
        supplyMap.put(startVertex, 1);
        supplyMap.put(endVertex, -1);

        MinimumCostFlowProblem<IExpr, ExprEdge> problem =
            new MinimumCostFlowProblemImpl(graph, supplyMap, capacityMap, costMap);
        MinimumCostFlowAlgorithm<IExpr, ExprEdge> algorithm =
            new CapacityScalingMinimumCostFlow<>();

        MinimumCostFlow<ExprEdge> minimumCostFlow = algorithm.getMinimumCostFlow(problem);

        return F.num(minimumCostFlow.getCost());
      } catch (RuntimeException rex) {
        rex.printStackTrace();
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.FindMinimumCostFlow, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.NO_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  private static class FindVertexCover extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          GraphView view = GraphView.of(gex.toData());
          // a minimum cover; of those the lexicographically smallest in VertexList order
          int[] cover = GraphUtil.minimumVertexCover(view, 50_000_000L);
          if (cover == null) {
            return F.NIL;
          }
          return F.mapRange(0, cover.length, i -> view.vertex(cover[i]));
        }
      } catch (IllegalArgumentException iae) {
        // Fallback catch if algorithm constraints are violated
        Errors.printMessage(S.FindVertexCover, iae, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.FindVertexCover, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code> FindShortestPath(graph, source, destination)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find a shortest path in the <code>graph</code> from <code>source</code> to <code>destination
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Pathfinding">Wikipedia - Pathfinding</a>
   * <li><a href="https://en.wikipedia.org/wiki/Shortest_path_problem">Wikipedia - Shortest path
   * problem</a>
   * </ul>
   */
  private static class FindShortestPath extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      Graph<IExpr, ?> g = gex.toData();
      if (!g.containsVertex(ast.arg2()) || !g.containsVertex(ast.arg3())) {
        return F.NIL;
      }

      ShortestPathAlgorithm<IExpr, ?> alg =
          GraphUtil.hasNegativeEdgeWeight(g) ? new BellmanFordShortestPath<>(g)
              : new DijkstraShortestPath<>(g);
      GraphPath<IExpr, ?> path;
      try {
        path = alg.getPaths(ast.arg2()).getPath(ast.arg3());
      } catch (NegativeCycleDetectedException ncde) {
        return F.NIL;
      }
      if (path == null) {
        return F.CEmptyList;
      }
      return Object2Expr.convertList(path.getVertexList(), true, false);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }


  private static class LineGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      GraphView view = GraphView.of(gex.toData());
      if (view.isMixed()) {
        return F.NIL;
      }
      // the vertices of the line graph are the edge indices
      IASTAppendable vertices = F.ListAlloc(view.m);
      for (int e = 1; e <= view.m; e++) {
        vertices.append(F.ZZ(e));
      }
      IASTAppendable edges = F.ListAlloc();
      if (view.hasDirectedEdge()) {
        // i -> j if edge j starts where edge i ends
        for (int i = 0; i < view.m; i++) {
          for (int j : view.out[view.target[i]]) {
            edges.append(F.DirectedEdge(F.ZZ(i + 1), F.ZZ(j + 1)));
          }
        }
      } else {
        // edge j is joined to the earlier edges at its first, then at its second end
        for (int j = 0; j < view.m; j++) {
          java.util.Set<Integer> joined = new java.util.HashSet<Integer>();
          for (int end : new int[] {view.source[j], view.target[j]}) {
            for (int i : view.out[end]) {
              if (i < j && joined.add(i)) {
                edges.append(F.UndirectedEdge(F.ZZ(j + 1), F.ZZ(i + 1)));
              }
            }
          }
        }
      }
      GraphExpr<?> result = GraphExpr.newInstance(vertices, edges);
      return result == null ? F.NIL : result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PathGraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      Graph<IExpr, ? extends IExprEdge> graph = (Graph<IExpr, ? extends IExprEdge>) gex.toData();
      GraphType t = graph.getType();
      if (t == null) {
        return F.NIL;
      }
      if (graph.vertexSet().size() == 1 && graph.edgeSet().isEmpty()) {
        // a single vertex is a path
        return S.True;
      }
      if (t.isDirected()) {
        for (IExpr v : graph.vertexSet()) {
          if (graph.inDegreeOf(v) != 0 && graph.inDegreeOf(v) != 1) {
            return S.False;
          }
          if (graph.outDegreeOf(v) != 0 && graph.outDegreeOf(v) != 1) {
            return S.False;
          }
          if (graph.inDegreeOf(v) == 0 && graph.outDegreeOf(v) == 0) {
            return S.False;
          }
        }
      } else {
        for (IExpr v : graph.vertexSet()) {
          if (graph.degreeOf(v) != 1 && graph.degreeOf(v) != 2) {
            return S.False;
          }
        }
      }
      return GraphTests.isConnected(graph) ? S.True : S.False;

    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PlanarGraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex == null) {
        return S.False;
      }

      if (gex.isWeightedGraph()) {
        Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
        PlanarityTestingAlgorithm<IExpr, ExprWeightedEdge> inspector =
            new BoyerMyrvoldPlanarityInspector<IExpr, ExprWeightedEdge>(g);
        return F.booleSymbol(inspector.isPlanar());
      }
      Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
      PlanarityTestingAlgorithm<IExpr, ExprEdge> inspector =
          new BoyerMyrvoldPlanarityInspector<IExpr, ExprEdge>(g);
      return F.booleSymbol(inspector.isPlanar());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>VertexEccentricity(graph, vertex)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the eccentricity of <code>vertex</code> in the <code>graph</code>. It's the length of
   * the longest shortest path from the <code>vertex</code> to every other vertex in the <code>
   * graph</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Distance_(graph_theory)">Wikipedia - Distance
   * (graph_theory)</a>
   * </ul>
   */
  private static class VertexEccentricity extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      GraphExpr<?> gex = GraphExpr.newInstance(arg1);
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();
      GraphView view = GraphView.of(g);
      Integer v = view.index.get(arg2);
      if (v != null && !view.hasNegativeWeight()) {
        // measure the eccentricity over the vertices reachable from v
        double max = 0.0;
        for (double d : view.distances(v)) {
          if (!Double.isInfinite(d)) {
            max = Math.max(max, d);
          }
        }
        return view.weighted ? F.num(max) : F.ZZ((int) max);
      }
      GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
      Map<IExpr, Double> centerSet = graphMeasurer.getVertexEccentricityMap();

      Double dValue = centerSet.get(arg2);
      if (dValue != null) {
        INum vertexEccentricity = F.num(dValue);
        if (gex.isWeightedGraph()) {
          return vertexEccentricity;
        }
        int intVertexEccentricity = vertexEccentricity.toIntDefault();
        if (F.isPresent(intVertexEccentricity)) {
          return F.ZZ(intVertexEccentricity);
        }
        return vertexEccentricity;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class VertexCount extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();
      if (ast.isAST2()) {
        // VertexCount(graph, patt) - count the vertices matching `patt`
        return F.ZZ(GraphFunctions.countMatches(GraphExpr.vertexToIExpr(g), ast.arg2(), engine));
      }
      return F.ZZ(g.vertexSet().size());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }
  /**
   *
   *
   * <pre>
   * <code>VertexList(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>graph</code> into a list of vertices.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; VertexList(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}))
   * {1,2,3,4}
   * </code>
   * </pre>
   */
  private static class VertexList extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      Graph<IExpr, ?> g = gex.toData();
      IAST vertices = GraphExpr.vertexToIExpr(g);
      if (ast.isAST2()) {
        // VertexList(graph, patt) - the vertices matching `patt`
        return GraphFunctions.selectMatches(vertices, ast.arg2(), engine);
      }
      return vertices;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>VertexQ(graph, vertex)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * test if <code>vertex</code> is a vertex in the <code>graph</code> object.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Graph_theory">Wikipedia - Graph theory</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; VertexQ(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}),3)
   * True
   *
   * &gt;&gt; VertexQ(Graph({1 -&gt; 2, 2 -&gt; 3, 1 -&gt; 3, 4 -&gt; 2}),5)
   * False
   * </code>
   * </pre>
   */
  private static class VertexQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex != null) {
        Graph<IExpr, ?> g = gex.toData();
        return F.booleSymbol(g.containsVertex(ast.arg2()));
      }
      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class WeaklyConnectedGraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        // "gives False for anything that is not a weakly connected graph"
        return S.False;
      }
      Graph<IExpr, ? extends IExprEdge> graph = (Graph<IExpr, ? extends IExprEdge>) gex.toData();
      if (graph.vertexSet().isEmpty()) {
        // the empty graph is connected
        return S.True;
      }
      return GraphTests.isWeaklyConnected(graph) ? S.True : S.False;

    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class WeightedAdjacencyMatrix extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }
      if (gex.isWeightedGraph()) {
        return GraphExpr
            .weightedGraphToWeightedAdjacencyMatrix((Graph<IExpr, ExprWeightedEdge>) gex.toData());
      }
      return GraphExpr.graphToAdjacencyMatrix(gex.toData());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class WeightedGraphQ extends AbstractEvaluator {

    @Override
    public IExpr defaultReturn() {
      return F.False;
    }

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      GraphExpr<?> gex = getGraphExpr(ast.arg1());
      if (gex != null && gex.isWeightedGraph()) {
        return S.True;
      }
      return S.False;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  /**
   * The graph radius (<code>radius=true</code>) or diameter: the smallest or largest vertex
   * eccentricity. A graph which isn't (strongly) connected has the radius and diameter
   * <code>Infinity</code>.
   */
  private static IExpr eccentricityBound(GraphExpr<?> gex, boolean radius) {
    Graph<IExpr, ?> g = gex.toData();
    GraphView view = GraphView.of(g);
    if (view.n == 0) {
      return F.NIL;
    }
    if (view.hasNegativeWeight()) {
      GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
      return F.num(radius ? graphMeasurer.getRadius() : graphMeasurer.getDiameter());
    }
    double[] eccentricity = view.eccentricities();
    double bound = radius ? Double.POSITIVE_INFINITY : 0.0;
    for (double e : eccentricity) {
      bound = radius ? Math.min(bound, e) : Math.max(bound, e);
    }
    if (!view.isStronglyConnected() || Double.isInfinite(bound)) {
      return S.Infinity;
    }
    return view.weighted ? F.num(bound) : F.ZZ((int) bound);
  }

  /**
   * The graph center (<code>center=true</code>) or periphery: the vertices whose eccentricity is
   * the radius or diameter, in <code>VertexList</code> order. <code>{}</code> for a graph which
   * isn't (strongly) connected.
   */
  private static IExpr eccentricityVertices(GraphExpr<?> gex, boolean center) {
    Graph<IExpr, ?> g = gex.toData();
    GraphView view = GraphView.of(g);
    if (view.hasNegativeWeight()) {
      GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
      return F
          .ListAlloc(center ? graphMeasurer.getGraphCenter() : graphMeasurer.getGraphPeriphery());
    }
    if (!view.isStronglyConnected()) {
      return F.CEmptyList;
    }
    double[] eccentricity = view.eccentricities();
    double bound = center ? Double.POSITIVE_INFINITY : 0.0;
    for (double e : eccentricity) {
      bound = center ? Math.min(bound, e) : Math.max(bound, e);
    }
    IASTAppendable result = F.ListAlloc();
    for (int v = 0; v < view.n; v++) {
      if (Math.abs(eccentricity[v] - bound) <= 1.0e-12 * Math.max(1.0, bound)) {
        result.append(view.vertex(v));
      }
    }
    return result;
  }

  /**
   * The isomorphisms between two graphs (VF2).
   *
   * @return <code>null</code> for a mixed graph or a multigraph, which VF2 doesn't handle; an empty
   *         iterator if the graphs aren't isomorphic
   */
  private static Iterator<? extends GraphMapping<IExpr, ?>> isomorphisms(Graph<IExpr, ?> g1,
      Graph<IExpr, ?> g2) {
    if (GraphExpr.isMixedGraph(g1) || GraphExpr.isMixedGraph(g2)) {
      return null;
    }
    if (g1.getType().isDirected() != g2.getType().isDirected()
        || g1.vertexSet().size() != g2.vertexSet().size()
        || g1.edgeSet().size() != g2.edgeSet().size()) {
      return Collections.emptyIterator();
    }
    try {
      @SuppressWarnings({"unchecked", "rawtypes"})
      VF2GraphIsomorphismInspector<IExpr, Object> inspector =
          new VF2GraphIsomorphismInspector<IExpr, Object>((Graph) g1, (Graph) g2);
      Iterator<GraphMapping<IExpr, Object>> mappings = inspector.getMappings();
      return mappings;
    } catch (IllegalArgumentException iae) {
      // multigraphs aren't supported
      return null;
    }
  }

  /** The size of the search tree after which a Hamiltonian cycle search gives up. */
  private static final long HAMILTONIAN_SEARCH_NODES = 20_000_000L;

  /**
   * Get the <code>GraphExpr<?></code>.
   *
   * @param arg1
   * @return
   */
  public static GraphExpr<?> getGraphExpr(IExpr arg1) {
    if (arg1 instanceof GraphExpr) {
      return (GraphExpr<?>) arg1;
    }
    return null;
  }

  /**
   * Select the elements of <code>list</code> which match <code>pattern</code>.
   *
   * @param list a list of vertices or edges
   * @param pattern the pattern the elements are tested against
   */
  private static IAST selectMatches(IAST list, IExpr pattern, EvalEngine engine) {
    IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
    return list.select(x -> matcher.test(x, engine));
  }

  /**
   * Count the elements of <code>list</code> which match <code>pattern</code>.
   *
   * @param list a list of vertices or edges
   * @param pattern the pattern the elements are tested against
   */
  private static int countMatches(IAST list, IExpr pattern, EvalEngine engine) {
    IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
    int counter = 0;
    for (int i = 1; i < list.size(); i++) {
      if (matcher.test(list.get(i), engine)) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * Create an eulerian cycle.
   *
   * @param gex a graph object
   * @return <code>null</code> if no eulerian cycle can be created
   */
  private static GraphPath<IExpr, ?> eulerianCycle(GraphExpr<?> gex) {
    Graph<IExpr, IExprEdge> g = (Graph<IExpr, IExprEdge>) gex.toData();
    EulerianCycleAlgorithm<IExpr, IExprEdge> eca = new HierholzerEulerianCycle<>();
    try {
      return eca.getEulerianCycle(g);
    } catch (IllegalArgumentException iae) {
      // Graph is not Eulerian
    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private GraphFunctions() {}
}

