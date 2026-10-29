package org.matheclipse.graphtheory.builtin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.generate.HyperCubeGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;
import org.matheclipse.graphtheory.graphics.GraphGraphics;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;

/** Functions for graph theory algorithms. */
public class GraphDataFunctions {

  private static final TrieBuilder<String, Supplier<Graph<IExpr, ?>>, ArrayList<Supplier<Graph<IExpr, ?>>>> TRIE_STRING2GRAPH_BUILDER =
      TrieBuilder.create();
  private static Map<String, Supplier<Graph<IExpr, ?>>> GRAPH_MAP =
      TRIE_STRING2GRAPH_BUILDER.withMatch(TrieMatch.EXACT).build();

  /** The readable names of the graphs in {@link #GRAPH_MAP}. */
  private static final Map<String, String> GRAPH_NAMES = Map.of( //
      "PappusGraph", "Pappus graph", //
      "IcosahedralGraph", "icosahedral graph", //
      "DodecahedralGraph", "dodecahedral graph");

  /** The properties <code>GraphData(name, property)</code> knows, in alphabetical order. */
  private static final String[] GRAPH_PROPERTIES = {"AdjacencyMatrix", "EdgeCount", "EdgeList",
      "EdgeRules", "Graph", "Name", "StandardName", "VertexCount", "VertexList"};

  /**
   * The skeleton of the icosahedron: every vertex is joined to five others; the edges are sorted
   * pairs.
   */
  private static final int[][] ICOSAHEDRAL_EDGES = {{1, 3}, {1, 5}, {1, 6}, {1, 9}, {1, 10},
      {2, 4}, {2, 7}, {2, 8}, {2, 11}, {2, 12}, {3, 7}, {3, 8}, {3, 9}, {3, 10}, {4, 5}, {4, 6},
      {4, 11}, {4, 12}, {5, 6}, {5, 9}, {5, 11}, {6, 10}, {6, 12}, {7, 8}, {7, 9}, {7, 11}, {8, 10},
      {8, 12}, {9, 11}, {10, 12}};

  /**
   * The skeleton of the dodecahedron: every vertex is joined to three others; the edges are sorted
   * pairs.
   */
  private static final int[][] DODECAHEDRAL_EDGES = {{1, 14}, {1, 15}, {1, 16}, {2, 5}, {2, 6},
      {2, 13}, {3, 7}, {3, 14}, {3, 19}, {4, 8}, {4, 15}, {4, 20}, {5, 11}, {5, 19}, {6, 12},
      {6, 20}, {7, 11}, {7, 16}, {8, 12}, {8, 16}, {9, 10}, {9, 14}, {9, 17}, {10, 15}, {10, 18},
      {11, 12}, {13, 17}, {13, 18}, {17, 19}, {18, 20}};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      // GRAPH_MAP.put("PetersenGraph", () -> petersenGraph());
      GRAPH_MAP.put("PappusGraph", () -> pappusGraph());
      GRAPH_MAP.put("IcosahedralGraph", () -> namedGraph(12, ICOSAHEDRAL_EDGES));
      GRAPH_MAP.put("DodecahedralGraph", () -> namedGraph(20, DODECAHEDRAL_EDGES));
      S.GraphData.setEvaluator(new GraphData());

      S.CompleteGraph.setEvaluator(new CompleteGraph());
      S.ButterflyGraph.setEvaluator(new ButterflyGraph());
      S.CycleGraph.setEvaluator(new CycleGraph());
      S.GridGraph.setEvaluator(new GridGraph());
      S.HypercubeGraph.setEvaluator(new HypercubeGraph());
      S.PathGraph.setEvaluator(new PathGraph());
      S.PetersenGraph.setEvaluator(new PetersenGraph());
      S.RandomGraph.setEvaluator(new RandomGraph());
      S.StarGraph.setEvaluator(new StarGraph());
      S.TorusGraph.setEvaluator(new TorusGraph());
      S.WheelGraph.setEvaluator(new WheelGraph());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>TorusGraph({n1, n2, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the graph Cartesian product of the cycle graphs <code>CycleGraph(n1)</code>,
   * <code>CycleGraph(n2)</code>,... Every vertex of the resulting graph has the degree
   * <code>2*k</code> for <code>k</code> dimensions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; EdgeCount(TorusGraph({3, 3}))
   * 18
   * </code>
   * </pre>
   */
  private static class TorusGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isNonEmptyList()) {
        return F.NIL;
      }
      IAST list = (IAST) arg1;
      int dimensions = list.argSize();
      int[] sizes = new int[dimensions];
      int vertexCount = 1;
      for (int i = 1; i < list.size(); i++) {
        int size = list.get(i).toMachineInt();
        if (size <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
        }
        sizes[i - 1] = size;
        vertexCount *= size;
        if (vertexCount > Config.MAX_GRAPH_VERTICES_SIZE) {
          ASTElementLimitExceeded.throwIt(vertexCount);
        }
      }

      // the vertices are numbered in row major order, `strides[d]` is the distance between two
      // neighbours along dimension `d`
      int[] strides = new int[dimensions];
      int stride = 1;
      for (int d = dimensions - 1; d >= 0; d--) {
        strides[d] = stride;
        stride *= sizes[d];
      }

      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(true).allowingSelfLoops(true) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= vertexCount; i++) {
        target.addVertex(F.ZZ(i));
      }

      int edgeId = 0;
      for (int v = 0; v < vertexCount; v++) {
        for (int d = 0; d < dimensions; d++) {
          // the cyclic successor of `v` along dimension `d`
          int index = (v / strides[d]) % sizes[d];
          int successor = (index + 1 == sizes[d]) //
              ? v - index * strides[d] //
              : v + strides[d];
          target.addEdge(F.ZZ(v + 1), F.ZZ(successor + 1), new ExprEdge(false, ++edgeId));
        }
      }
      return GraphExpr.newInstance(target);
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

  private static class IntegerSupplier implements Supplier<IExpr>, Serializable {
    private static final long serialVersionUID = -4714266728630636497L;

    private int i = 0;

    public IntegerSupplier(int start) {
      this.i = start;
    }

    @Override
    public IExpr get() {
      return F.ZZ(i++);
    }
  }

  private static class GraphData extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {

      if (ast.isAST0()) {
        Set<String> keySet = new TreeSet<String>(GRAPH_MAP.keySet());
        IASTAppendable result = F.ListAlloc(keySet.size());
        for (String name : keySet) {
          result.append(F.List(name));
        }
        return result;
      }
      IExpr arg1 = ast.arg1();
      if (ast.isAST1() && arg1.isString("Properties")) {
        return F.mapRange(0, GRAPH_PROPERTIES.length, i -> F.stringx(GRAPH_PROPERTIES[i]));
      }
      if (arg1.isString()) {
        String graphName = arg1.toString();
        Supplier<Graph<IExpr, ?>> supplier = GRAPH_MAP.get(graphName);
        if (supplier != null) {
          IExpr graph = GraphExpr.newInstance(supplier.get());
          if (ast.isAST1()) {
            return graph;
          }
          IExpr value = property(graphName, graph, ast.arg2(), engine);
          if (value.isPresent()) {
            return value;
          }
          // `1` is not a known property or size specification for `2`. Use `2`("Properties") for
          // a list of properties.
          return Errors.printMessage(S.GraphData, "notprop", F.List(ast.arg2(), S.GraphData),
              engine);
        }
      }
      // `1` is not a known entity, class or tag for GraphData. Use GraphData for a list of
      // entities.
      return Errors.printMessage(S.GraphData, "notent", F.List(arg1, S.GraphData), engine);
    }

    /**
     * The value of a property of a named graph.
     *
     * @return {@link F#NIL} if the property isn't known
     */
    private static IExpr property(String graphName, IExpr graph, IExpr property,
        EvalEngine engine) {
      if (!property.isString()) {
        return F.NIL;
      }
      switch (property.toString()) {
        case "AdjacencyMatrix":
          return engine.evaluate(F.unaryAST1(S.AdjacencyMatrix, graph));
        case "EdgeCount":
          return engine.evaluate(F.unaryAST1(S.EdgeCount, graph));
        case "EdgeList":
          return engine.evaluate(F.unaryAST1(S.EdgeList, graph));
        case "EdgeRules":
          return engine.evaluate(F.unaryAST1(S.EdgeRules, graph));
        case "Graph":
          return graph;
        case "Name":
          return F.stringx(GRAPH_NAMES.getOrDefault(graphName, graphName));
        case "StandardName":
          return F.stringx(graphName);
        case "VertexCount":
          return engine.evaluate(F.unaryAST1(S.VertexCount, graph));
        case "VertexList":
          return engine.evaluate(F.unaryAST1(S.VertexList, graph));
        default:
          return F.NIL;
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.EdgeWeight, S.Automatic)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class CompleteGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      IASTAppendable optionsList = GraphGraphics.createOptionsList(options);
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        if (list.argSize() >= 2) {
          int[] partitions = new int[list.argSize()];
          int vertexCount = 0;
          for (int i = 1; i < list.size(); i++) {
            partitions[i - 1] = list.get(i).toMachineInt();
            if (partitions[i - 1] <= 0) {
              // Positive machine-sized integer expected at position `2` in `1`
              return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
            }
            vertexCount += partitions[i - 1];
          }
          if (vertexCount > Config.MAX_GRAPH_VERTICES_SIZE) {
            ASTElementLimitExceeded.throwIt(vertexCount);
          }
          return multipartiteCompleteGraph(partitions, vertexCount, optionsList);
        }
        if (!arg1.isList1()) {
          // Function `1` only implemented for `2` list arguments.
          return Errors.printMessage(S.CompleteGraph, "zzonlyimpl", F.List(S.CompleteGraph, F.C2),
              engine);
        }
        arg1 = list.arg1();
      }

      int partition = arg1.toMachineInt();
      if (partition <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (partition > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(partition);
      }

      return completeGraph(partition, optionsList);
    }

    /**
     * Generates a complete multipartite graph. The vertices are split into the given partitions and
     * two vertices are connected if and only if they belong to different partitions.
     *
     * @param partitions the sizes of the partitions
     * @param vertexCount the total number of vertices
     * @param optionsList the options of the resulting graph
     */
    private static IExpr multipartiteCompleteGraph(int[] partitions, int vertexCount,
        IASTAppendable optionsList) {
      // partitionOfVertex[v] is the index of the partition the vertex `v+1` belongs to
      int[] partitionOfVertex = new int[vertexCount];
      int vertex = 0;
      for (int p = 0; p < partitions.length; p++) {
        for (int i = 0; i < partitions[p]; i++) {
          partitionOfVertex[vertex++] = p;
        }
      }

      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= vertexCount; i++) {
        target.addVertex(F.ZZ(i));
      }
      for (int u = 0; u < vertexCount; u++) {
        for (int v = u + 1; v < vertexCount; v++) {
          if (partitionOfVertex[u] != partitionOfVertex[v]) {
            target.addEdge(F.ZZ(u + 1), F.ZZ(v + 1));
          }
        }
      }
      return GraphExpr.newInstance(target, optionsList);
    }

    /**
     * Generates a complete graph of any size.
     * 
     * A complete graph is a graph where every vertex shares an edge with every other vertex. If it
     * is a directed graph, then edges must always exist in both directions.
     * 
     */
    private static IExpr completeGraph(int partition, IASTAppendable optionsList) {
      CompleteGraphGenerator<IExpr, ExprEdge> gen =
          new CompleteGraphGenerator<IExpr, ExprEdge>(partition);
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
          .buildGraph();
      gen.generateGraph(target);
      return GraphExpr.newInstance(target, optionsList);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
          GraphGraphics.defaultGraphOptionValues());
    }
  }

  /**
   * <code>ButterflyGraph(n)</code> - the butterfly network of <code>n</code> levels: vertex
   * <code>(i, w)</code>, level <code>i = 0..n</code> and word <code>w</code> of <code>n</code>
   * bits, is joined to <code>(i+1, w)</code> and to <code>(i+1, w</code> with bit <code>i</code>
   * flipped<code>)</code>. The vertices are numbered level by level from 1.
   */
  private static class ButterflyGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      int n = ast.arg1().toIntDefault();
      if (n <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (n > 20 || (long) (n + 1) << n > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt((long) (n + 1) << Math.min(n, 40));
      }
      int words = 1 << n;
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int v = 1; v <= (n + 1) * words; v++) {
        target.addVertex(F.ZZ(v));
      }
      for (int level = 0; level < n; level++) {
        for (int w = 0; w < words; w++) {
          IExpr from = F.ZZ(level * words + w + 1);
          target.addEdge(from, F.ZZ((level + 1) * words + w + 1));
          target.addEdge(from, F.ZZ((level + 1) * words + (w ^ (1 << level)) + 1));
        }
      }
      return GraphExpr.newInstance(target, GraphGraphics.createOptionsList(options));
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
          GraphGraphics.defaultGraphOptionValues());
    }
  }

  private static class CycleGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {


      int order = ast.arg1().toMachineInt();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order == 1) {
        return F.Graph(F.List(F.C1), F.List(F.UndirectedEdge(F.C1, F.C1)));
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      IASTAppendable optionsList = GraphGraphics.createOptionsList(options);
      return cycleGraph(engine, order, optionsList);
    }

    private static IExpr cycleGraph(EvalEngine engine, int order, IASTAppendable optionsList) {
      // edges in sorted order: 1<->2, the closing edge 1<->n, then 2<->3, ..., (n-1)<->n
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= order; i++) {
        target.addVertex(F.ZZ(i));
      }
      target.addEdge(F.C1, F.C2);
      if (order > 2) {
        target.addEdge(F.C1, F.ZZ(order));
      }
      for (int i = 2; i < order; i++) {
        target.addEdge(F.ZZ(i), F.ZZ(i + 1));
      }
      return GraphExpr.newInstance(target, optionsList);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] defaultGraphOptionKeys = GraphGraphics.defaultGraphOptionKeys();
      IExpr[] defaultGraphOptionValues = GraphGraphics.defaultGraphOptionValues();
      defaultGraphOptionValues[GraphGraphics.X_GRAPH_LAYOUT] = F.stringx("CircularEmbedding");
      setOptions(newSymbol, defaultGraphOptionKeys, defaultGraphOptionValues);
    }
  }

  private static class GridGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      IExpr list = ast.arg1();
      if (list.isList2()) {
        int m = list.first().toMachineInt();
        if (m <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
        }
        int n = list.second().toMachineInt();
        if (n <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
        }
        if (m * n > Config.MAX_GRAPH_VERTICES_SIZE) {
          ASTElementLimitExceeded.throwIt(m * n);
        }
        return gridGraph(engine, m, n);
      }
      if (list.isList() && list.argSize() > 0) {
        int[] dimensions = new int[list.argSize()];
        long size = 1;
        for (int i = 0; i < dimensions.length; i++) {
          dimensions[i] = list.getAt(i + 1).toMachineInt();
          if (dimensions[i] <= 0) {
            // Positive machine-sized integer expected at position `2` in `1`
            return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
          }
          size *= dimensions[i];
          if (size > Config.MAX_GRAPH_VERTICES_SIZE) {
            ASTElementLimitExceeded.throwIt(size);
          }
        }
        return gridGraph(dimensions);
      }
      return F.NIL;
    }

    /**
     * The grid graph of any dimension: the vertices <code>1..n1*n2*...*nk</code> numbered with the
     * last coordinate running fastest, and an edge between vertices which differ by 1 in one
     * coordinate.
     */
    private static IExpr gridGraph(int[] dimensions) {
      int size = 1;
      for (int d : dimensions) {
        size *= d;
      }
      Graph<IExpr, ExprEdge> grid = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= size; i++) {
        grid.addVertex(F.ZZ(i));
      }
      int[] stride = new int[dimensions.length];
      int s = 1;
      for (int k = dimensions.length - 1; k >= 0; k--) {
        stride[k] = s;
        s *= dimensions[k];
      }
      for (int v = 0; v < size; v++) {
        // the edges to the neighbours with a larger index, the smallest stride first so the edges
        // come out as sorted pairs
        for (int k = dimensions.length - 1; k >= 0; k--) {
          int coordinate = (v / stride[k]) % dimensions[k];
          if (coordinate + 1 < dimensions[k]) {
            grid.addEdge(F.ZZ(v + 1), F.ZZ(v + stride[k] + 1));
          }
        }
      }
      return GraphExpr.newInstance(grid);
    }

    private static IExpr gridGraph(EvalEngine engine, int m, int n) {
      if (m == 1 || n == 1) {
        // the JGraphT generator needs at least two rows and columns, a degenerated grid is a
        // path graph
        Graph<IExpr, ExprEdge> path = GraphTypeBuilder //
            .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .edgeClass(ExprEdge.class) //
            .buildGraph();
        int size = m * n;
        for (int i = 1; i <= size; i++) {
          path.addVertex(F.ZZ(i));
        }
        for (int i = 2; i <= size; i++) {
          path.addEdge(F.ZZ(i - 1), F.ZZ(i));
        }
        return GraphExpr.newInstance(path);
      }
      GridGraphGenerator<IExpr, ExprEdge> gen = new GridGraphGenerator<IExpr, ExprEdge>(n, m);
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
          .buildGraph();
      gen.generateGraph(target);
      return GraphExpr.newInstance(target);
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

  private static class HypercubeGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      IASTAppendable optionsList = GraphGraphics.createOptionsList(options);

      int order = ast.arg1().toMachineInt();
      if (order == 0) {
        // the 0-dimensional hypercube is a single vertex
        return GraphExpr.newInstance(GraphExpr.newInstance(F.List(F.C1), F.List()).toData(),
            optionsList);
      }
      if (order < 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE / 10) {
        ASTElementLimitExceeded.throwIt(order);
      }

      return hypercubeGraph(order, optionsList);
    }

    private static IExpr hypercubeGraph(int order, IASTAppendable optionsList) {
      HyperCubeGraphGenerator<IExpr, ExprEdge> gen =
          new HyperCubeGraphGenerator<IExpr, ExprEdge>(order);
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
          .buildGraph();
      gen.generateGraph(target);
      return GraphExpr.newInstance(target, optionsList);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
          GraphGraphics.defaultGraphOptionValues());
    }
  }

  private static class PathGraph extends AbstractFunctionOptionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize == 1 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        IASTAppendable optionsList = GraphGraphics.createOptionsList(options);
        return pathGraph(list, optionsList);
      }
      return F.NIL;
    }

    private static IExpr pathGraph(IAST list, IASTAppendable options) {
      Graph<IExpr, ? extends IExprEdge> resultGraph =
          new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      if (list.argSize() > 1) {
        for (int i = 1; i < list.size(); i++) {
          resultGraph.addVertex(list.get(i));
        }

        for (int i = 2; i < list.size(); i++) {
          resultGraph.addVertex(list.get(i));
          resultGraph.addEdge(list.get(i - 1), list.get(i));
        }
        return GraphExpr.newInstance(resultGraph, options);
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] defaultGraphOptionKeys = GraphGraphics.defaultGraphOptionKeys();
      IExpr[] defaultGraphOptionValues = GraphGraphics.defaultGraphOptionValues();
      defaultGraphOptionValues[GraphGraphics.X_GRAPH_LAYOUT] = F.stringx("DiscreteSpiralEmbedding");
      setOptions(newSymbol, defaultGraphOptionKeys, defaultGraphOptionValues);
    }
  }

  private static class PetersenGraph extends AbstractEvaluator {

    @Override
    public IExpr evalCatched(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // The argument `2` in `1` is not valid. 0 or 2 arguments expected.
        return Errors.printMessage(ast.topHead(), "inv02", F.list(ast, ast.arg1()), engine);
      }

      if (ast.isAST0()) {
        return petersenGraphNoArg();
      }
      if (ast.isAST2()) {
        int order = ast.arg1().toMachineInt();
        if (order <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
        }
        if (order > Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(order);
        }
        int k = ast.arg2().toMachineInt();
        if (k <= 0 || k > order) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
        return petersenGraph(engine, order, k);
      }
      return F.NIL;
    }

    /**
     * The generalized Petersen graph numbers <code>PetersenGraph()</code>: the star
     * <code>i - i+k (mod n)</code> on the vertices <code>1..n</code>, the spokes
     * <code>i - n+i</code> and the cycle on <code>n+1..2n</code>; the edges are sorted pairs.
     */
    private IExpr petersenGraph(EvalEngine engine, int order, int k) {
      java.util.TreeSet<int[]> pairs = new java.util.TreeSet<int[]>(
          (x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0]) : Integer.compare(x[1], y[1]));
      for (int i = 1; i <= order; i++) {
        int j = (i - 1 + k) % order + 1;
        if (i != j) {
          pairs.add(new int[] {Math.min(i, j), Math.max(i, j)});
        }
        pairs.add(new int[] {i, order + i});
        int next = order + i % order + 1;
        if (order + i != next) {
          pairs.add(new int[] {Math.min(order + i, next), Math.max(order + i, next)});
        }
      }
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= 2 * order; i++) {
        target.addVertex(F.ZZ(i));
      }
      for (int[] pair : pairs) {
        target.addEdge(F.ZZ(pair[0]), F.ZZ(pair[1]));
      }
      return GraphExpr.newInstance(target);
    }

    private IExpr petersenGraphNoArg() {
      return petersenGraph(EvalEngine.get(), 5, 2);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   * <code>RandomGraph({n, m})</code> or <code>RandomGraph(dist)</code> - a random graph with
   * <code>n</code> vertices and <code>m</code> edges or one drawn from the graph distribution
   * <code>dist</code>; <code>RandomGraph(spec, k)</code> is a list of <code>k</code> such graphs.
   * The distributions are <code>UniformGraphDistribution(n, m)</code>,
   * <code>BarabasiAlbertGraphDistribution(n, k)</code> and
   * <code>WattsStrogatzGraphDistribution(n, p)</code> or
   * <code>WattsStrogatzGraphDistribution(n, p, k)</code>. The graphs are drawn with the random
   * generator of the engine, so <code>SeedRandom</code> makes them reproducible.
   */
  private static class RandomGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      try {
        Supplier<Graph<IExpr, ExprEdge>> generator = generator(ast, engine);
        if (generator == null) {
          return F.NIL;
        }
        // the options are only attached to the graphs if some are given
        IAST optionsList =
            argSize < ast.argSize() ? GraphGraphics.createOptionsList(options) : null;
        if (argSize == 2) {
          int k = ast.arg2().toMachineInt();
          if (k <= 0) {
            // Positive machine-sized integer expected at position `2` in `1`
            return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
          }
          if (k > Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(k);
          }
          return F.mapRange(0, k, i -> newGraph(generator.get(), optionsList));
        }
        return newGraph(generator.get(), optionsList);
      } catch (IllegalArgumentException iae) {
        // more edges than a simple graph with these vertices can have
        return Errors.printMessage(ast.topHead(), iae, engine);
      }
    }

    private static IExpr newGraph(Graph<IExpr, ExprEdge> graph, IAST optionsList) {
      return optionsList == null ? GraphExpr.newInstance(graph)
          : GraphExpr.newInstance(graph, optionsList.copyAppendable());
    }

    /**
     * The generator of the graphs of the specification in the first argument of
     * <code>ast</code>.
     *
     * @return <code>null</code> if the specification is invalid
     */
    private static Supplier<Graph<IExpr, ExprEdge>> generator(final IAST ast,
        EvalEngine engine) {
      IExpr spec = ast.arg1();
      Random random = engine.getRandom();
      if (spec.isList2() || spec.isAST(S.UniformGraphDistribution, 3)) {
        int vertices = vertexCount(spec, ast, engine);
        if (vertices <= 0) {
          return null;
        }
        int edges = spec.second().toMachineInt();
        if (edges < 0 || (edges == 0 && spec.isList())) {
          // Positive machine-sized integer expected at position `2` in `1`
          Errors.printMessage(ast.topHead(), "intpm", F.list(spec, F.C2), engine);
          return null;
        }
        if (edges > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(edges);
        }
        return () -> uniformGraph(vertices, edges, random);
      }
      if (spec.isAST(S.BarabasiAlbertGraphDistribution, 3)) {
        int vertices = vertexCount(spec, ast, engine);
        if (vertices <= 0) {
          return null;
        }
        int k = spec.second().toMachineInt();
        if (k <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          Errors.printMessage(ast.topHead(), "intpm", F.list(spec, F.C2), engine);
          return null;
        }
        return () -> barabasiAlbertGraph(vertices, k, random);
      }
      if (spec.isAST(S.WattsStrogatzGraphDistribution, 3, 4)) {
        int vertices = vertexCount(spec, ast, engine);
        if (vertices <= 0) {
          return null;
        }
        double p = spec.second().evalfNaN();
        if (!(p >= 0.0 && p <= 1.0)) {
          return null;
        }
        int k = 2;
        if (spec.argSize() == 3) {
          k = spec.getAt(3).toMachineInt();
          if (k <= 0) {
            // Positive machine-sized integer expected at position `2` in `1`
            Errors.printMessage(ast.topHead(), "intpm", F.list(spec, F.C3), engine);
            return null;
          }
        }
        final int neighbours = k;
        return () -> wattsStrogatzGraph(vertices, p, neighbours, random);
      }
      return null;
    }

    /** The positive number of vertices in the first argument of <code>spec</code> or -1. */
    private static int vertexCount(IExpr spec, IAST ast, EvalEngine engine) {
      int vertices = spec.first().toMachineInt();
      if (vertices <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        Errors.printMessage(ast.topHead(), "intpm", F.list(spec, F.C1), engine);
        return -1;
      }
      if (vertices > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(vertices);
      }
      return vertices;
    }

    /** The vertices <code>1, 2, ..., n</code> of a simple undirected graph without edges. */
    private static Graph<IExpr, ExprEdge> simpleGraph(int n) {
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= n; i++) {
        target.addVertex(F.ZZ(i));
      }
      return target;
    }

    /** A graph drawn uniformly from the simple graphs with the given numbers of vertices and edges. */
    private static Graph<IExpr, ExprEdge> uniformGraph(int vertices, int edges, Random random) {
      GnmRandomGraphGenerator<IExpr, ExprEdge> gen =
          new GnmRandomGraphGenerator<IExpr, ExprEdge>(vertices, edges, random, false, false);
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .undirected() //
          .allowingMultipleEdges(false) //
          .allowingSelfLoops(false) //
          .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
          .buildGraph();
      gen.generateGraph(target);
      return target;
    }

    /**
     * Preferential attachment: the first <code>k + 1</code> vertices form a complete graph, every
     * further vertex is joined to <code>k</code> distinct earlier vertices, each chosen with a
     * probability proportional to its degree. The graph has
     * <code>k*(k + 1)/2 + (n - k - 1)*k</code> edges.
     */
    private static Graph<IExpr, ExprEdge> barabasiAlbertGraph(int n, int k, Random random) {
      Graph<IExpr, ExprEdge> target = simpleGraph(n);
      int seed = Math.min(n, k + 1);
      // every vertex appears once for each of its edges, so a uniform pick from this list is a
      // pick proportional to the degree
      List<Integer> endpoints = new ArrayList<Integer>();
      for (int i = 1; i <= seed; i++) {
        for (int j = i + 1; j <= seed; j++) {
          target.addEdge(F.ZZ(i), F.ZZ(j));
          endpoints.add(i);
          endpoints.add(j);
        }
      }
      for (int v = seed + 1; v <= n; v++) {
        Set<Integer> chosen = new LinkedHashSet<Integer>();
        while (chosen.size() < k) {
          chosen.add(endpoints.get(random.nextInt(endpoints.size())));
        }
        for (int t : chosen) {
          target.addEdge(F.ZZ(t), F.ZZ(v));
          endpoints.add(t);
          endpoints.add(v);
        }
      }
      return target;
    }

    /**
     * The small world graph: a ring of <code>n</code> vertices, each joined to its
     * <code>k</code> nearest neighbours on either side, whose edges are rewired with the
     * probability <code>p</code>. A rewired edge keeps its first vertex and its place in the edge
     * list and gets a new second vertex, chosen uniformly from the vertices it isn't joined to
     * yet, so the graph stays simple and keeps all its edges.
     */
    private static Graph<IExpr, ExprEdge> wattsStrogatzGraph(int n, double p, int k,
        Random random) {
      List<Set<Integer>> adjacent = new ArrayList<Set<Integer>>(n + 1);
      for (int i = 0; i <= n; i++) {
        adjacent.add(new HashSet<Integer>());
      }
      List<Integer> sources = new ArrayList<Integer>();
      List<Integer> targets = new ArrayList<Integer>();
      // the edges i<->i+1 of all vertices first, then i<->i+2 and so on
      for (int j = 1; j <= k; j++) {
        for (int i = 1; i <= n; i++) {
          int t = (i + j - 1) % n + 1;
          if (t != i && adjacent.get(i).add(t)) {
            adjacent.get(t).add(i);
            sources.add(i);
            targets.add(t);
          }
        }
      }
      if (p > 0.0) {
        for (int e = 0; e < sources.size(); e++) {
          if (random.nextDouble() < p) {
            int s = sources.get(e);
            Set<Integer> neighbours = adjacent.get(s);
            if (neighbours.size() >= n - 1) {
              // no vertex left to rewire to
              continue;
            }
            int w;
            do {
              w = random.nextInt(n) + 1;
            } while (w == s || neighbours.contains(w));
            int old = targets.get(e);
            neighbours.remove(old);
            adjacent.get(old).remove(s);
            neighbours.add(w);
            adjacent.get(w).add(s);
            targets.set(e, w);
          }
        }
      }
      Graph<IExpr, ExprEdge> target = simpleGraph(n);
      for (int e = 0; e < sources.size(); e++) {
        target.addEdge(F.ZZ(sources.get(e)), F.ZZ(targets.get(e)));
      }
      return target;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
          GraphGraphics.defaultGraphOptionValues());
    }
  }

  private static class StarGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      int order = ast.arg1().toMachineInt();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }
      IASTAppendable optionsList = GraphGraphics.createOptionsList(options);
      return starGraph(engine, order, optionsList);
    }

    private static IExpr starGraph(EvalEngine engine, int order, IASTAppendable options) {
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      // JGraphT's StarGraphGenerator makes the leaves the edge sources; WMA gives 1<->k
      for (int i = 1; i <= order; i++) {
        target.addVertex(F.ZZ(i));
      }
      for (int i = 2; i <= order; i++) {
        target.addEdge(F.C1, F.ZZ(i));
      }
      return GraphExpr.newInstance(target, options);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] defaultGraphOptionKeys = GraphGraphics.defaultGraphOptionKeys();
      IExpr[] defaultGraphOptionValues = GraphGraphics.defaultGraphOptionValues();
      defaultGraphOptionValues[GraphGraphics.X_GRAPH_LAYOUT] = F.stringx("StarEmbedding");
      setOptions(newSymbol, defaultGraphOptionKeys, defaultGraphOptionValues);
    }
  }

  private static class WheelGraph extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {

      int order = ast.arg1().toMachineInt();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }
      if (order == 1) {
        return GraphExpr.newInstance(F.List(F.C1), F.List());
      }
      if (order == 2) {
        return GraphExpr.newInstance(F.List(F.C1, F.C2),
            F.List(F.UndirectedEdge(F.C1, F.C2), F.UndirectedEdge(F.C2, F.C2)));
      }
      IASTAppendable optionsList = GraphGraphics.createOptionsList(options);

      return wheelGraph(engine, order, optionsList);
    }

    private IExpr wheelGraph(EvalEngine engine, int order, IASTAppendable options) {
      // the hub is vertex 1 and the rim 2, 3, ..., order; the edges are sorted pairs
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
          .edgeClass(ExprEdge.class) //
          .buildGraph();
      for (int i = 1; i <= order; i++) {
        target.addVertex(F.ZZ(i));
      }
      for (int i = 2; i <= order; i++) {
        target.addEdge(F.C1, F.ZZ(i));
      }
      for (int i = 2; i <= order; i++) {
        if (i == 2) {
          target.addEdge(F.C2, F.C3);
          target.addEdge(F.C2, F.ZZ(order));
        } else if (i < order) {
          target.addEdge(F.ZZ(i), F.ZZ(i + 1));
        }
      }
      return GraphExpr.newInstance(target, options);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] defaultGraphOptionKeys = GraphGraphics.defaultGraphOptionKeys();
      IExpr[] defaultGraphOptionValues = GraphGraphics.defaultGraphOptionValues();
      defaultGraphOptionValues[GraphGraphics.X_GRAPH_LAYOUT] = F.stringx("StarEmbedding");
      setOptions(newSymbol, defaultGraphOptionKeys, defaultGraphOptionValues);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  /**
   * Generates the <a href="https://en.wikipedia.org/wiki/Pappus_graph">Pappus Graph</a>. The Pappus
   * Graph is a bipartite 3-regular undirected graph with 18 vertices and 27 edges.
   */
  private static Graph<IExpr, ExprEdge> pappusGraph() {
    Graph<IExpr, ExprEdge> g =
        GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class).buildGraph();
    // new NamedGraphGenerator<Integer, DefaultEdge>().generatePappusGraph(g);
    HashMap<IExpr, IExpr> vertexMap = new HashMap<>();
    // vertexMap.clear();
    int[][] edges = {{0, 1}, {0, 5}, {0, 6}, {1, 2}, {1, 7}, {2, 3}, {2, 8}, {3, 4}, {3, 9}, {4, 5},
        {4, 10}, {5, 11}, {6, 13}, {6, 17}, {7, 12}, {7, 14}, {8, 13}, {8, 15}, {9, 14}, {9, 16},
        {10, 15}, {10, 17}, {11, 12}, {11, 16}, {12, 15}, {13, 16}, {14, 17}};
    for (int[] edge : edges) {
      addEdge(vertexMap, g, edge[0], edge[1]);
    }
    return g;
  }

  /**
   * A graph on the vertices <code>1..vertexCount</code> with the given edges, in their order.
   *
   * @param edges the pairs of vertices which are joined
   */
  private static Graph<IExpr, ExprEdge> namedGraph(int vertexCount, int[][] edges) {
    Graph<IExpr, ExprEdge> g = GraphTypeBuilder //
        .<IExpr, ExprEdge>undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
        .edgeClass(ExprEdge.class) //
        .buildGraph();
    for (int i = 1; i <= vertexCount; i++) {
      g.addVertex(F.ZZ(i));
    }
    for (int[] edge : edges) {
      g.addEdge(F.ZZ(edge[0]), F.ZZ(edge[1]));
    }
    return g;
  }

  // --------------Helper methods-----------------/
  private static IExpr addVertex(HashMap<IExpr, IExpr> vertexMap,
      Graph<IExpr, ExprEdge> targetGraph, int i) {
    IInteger intNumber = F.ZZ(i);
    if (!vertexMap.containsKey(intNumber)) {
      vertexMap.put(intNumber, targetGraph.addVertex());
    }
    return vertexMap.get(intNumber);
  }

  private static void addEdge(HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph,
      int i, int j) {
    IExpr u = addVertex(vertexMap, targetGraph, i);
    IExpr v = addVertex(vertexMap, targetGraph, j);
    targetGraph.addEdge(u, v);
  }

  // private static void addCycle(HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge>
  // targetGraph,
  // int array[]) {
  // for (int i = 0; i < array.length; i++) {
  // addEdge(vertexMap, targetGraph, array[i], array[(i + 1) % array.length]);
  // }
  // }

  private GraphDataFunctions() {}
}
