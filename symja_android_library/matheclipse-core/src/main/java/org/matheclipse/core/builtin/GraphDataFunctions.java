package org.matheclipse.core.builtin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.generate.CompleteBipartiteGraphGenerator;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.HyperCubeGraphGenerator;
import org.jgrapht.generate.RingGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.generate.WheelGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;

/** Functions for graph theory algorithms. */
public class GraphDataFunctions {

  private static final TrieBuilder<String, Supplier<Graph<IExpr, ?>>, ArrayList<Supplier<Graph<IExpr, ?>>>> TRIE_STRING2GRAPH_BUILDER =
      TrieBuilder.create();
  private static Map<String, Supplier<Graph<IExpr, ?>>> GRAPH_MAP =
      TRIE_STRING2GRAPH_BUILDER.withMatch(TrieMatch.EXACT).build();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      // GRAPH_MAP.put("PetersenGraph", () -> petersenGraph());
      GRAPH_MAP.put("PappusGraph", () -> pappusGraph());
      S.GraphData.setEvaluator(new GraphData());

      S.CompleteGraph.setEvaluator(new CompleteGraph());
      S.CycleGraph.setEvaluator(new CycleGraph());
      S.HypercubeGraph.setEvaluator(new HypercubeGraph());
      S.PathGraph.setEvaluator(new PathGraph());
      S.PetersenGraph.setEvaluator(new PetersenGraph());
      S.RandomGraph.setEvaluator(new RandomGraph());
      S.StarGraph.setEvaluator(new StarGraph());
      S.WheelGraph.setEvaluator(new WheelGraph());
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST0()) {
          Set<String> keySet = GRAPH_MAP.keySet();
          IASTAppendable result = F.ListAlloc(keySet.size());
          for (String name : keySet) {
            result.append(F.List(name));
          }
          return result;
        } else if (ast.isAST1()) {
          IExpr arg1 = ast.arg1();
          if (arg1.isString()) {
            String graphName = ((IStringX) arg1).toString();
            Supplier<Graph<IExpr, ?>> supplier = GRAPH_MAP.get(graphName);
            if (supplier != null) {
              return GraphExpr.newInstance(supplier.get());
            }
          }
          // `1` is not a known entity, class or tag for GraphData. Use GraphData for a list of
          // entities.
          return Errors.printMessage(S.GraphData, "notent", F.List(arg1), engine);
        }
      } catch (RuntimeException rex) {
        rex.printStackTrace();
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
      return ARGS_0_1;
    }
  }

  private static class CompleteGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        if (arg1.isList2()) {
          int partitionA = list.arg1().toIntDefault();
          int partitionB = list.arg2().toIntDefault();
          try {
            if (partitionA <= 0 || partitionB <= 0) {
              // Positive machine-sized integer expected at position `2` in `1`
              return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
            }
            CompleteBipartiteGraphGenerator<IExpr, ExprEdge> gen =
                new CompleteBipartiteGraphGenerator<IExpr, ExprEdge>(partitionA, partitionB);
            Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
                .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
                .buildGraph();
            gen.generateGraph(target);
            return GraphExpr.newInstance(target);
          } catch (RuntimeException rex) {
            Errors.printMessage(S.CompleteGraph, rex, engine);
          }
          return F.NIL;
        }
        if (!arg1.isList1()) {
          // Function `1` only implemented for `2` list arguments.
          return Errors.printMessage(S.CompleteGraph, "zzonlyimpl", F.List(S.CompleteGraph, F.C2),
              engine);
        }
        arg1 = list.arg1();
      }
      int order = arg1.toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        CompleteGraphGenerator<IExpr, ExprEdge> gen =
            new CompleteGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        Errors.printMessage(S.CompleteGraph, rex, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class CycleGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int order = ast.arg1().toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        RingGraphGenerator<IExpr, ExprEdge> gen = new RingGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        // Graph<IExpr, ExprEdge> target = new DefaultUndirectedGraph<IExpr,
        // ExprEdge>(ExprEdge.class);
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        Errors.printMessage(S.CycleGraph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class HypercubeGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int order = ast.arg1().toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE / 10) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        HyperCubeGraphGenerator<IExpr, ExprEdge> gen =
            new HyperCubeGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        Errors.printMessage(S.HypercubeGraph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PathGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {


      if (ast.isAST1() && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
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
          return GraphExpr.newInstance(resultGraph);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PetersenGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // The argument `2` in `1` is not valid. 0 or 2 arguments expected.
        return Errors.printMessage(ast.topHead(), "inv02", F.list(ast, ast.arg1()), engine);
      }

      if (ast.isAST0()) {
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        GeneralizedPetersenGraphGenerator<IExpr, ExprEdge> gpgg =
            new GeneralizedPetersenGraphGenerator<>(5, 2);
        gpgg.generateGraph(target);
        return GraphExpr.newInstance(target);
      }
      if (ast.isAST2()) {
        int order = ast.arg1().toIntDefault();
        if (order <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
        }
        if (order > Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(order);
        }
        int k = ast.arg2().toIntDefault();
        if (k <= 0 || k > order) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
        try {
          Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
              .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
              .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
              .buildGraph();
          GeneralizedPetersenGraphGenerator<IExpr, ExprEdge> gpgg =
              new GeneralizedPetersenGraphGenerator<>(order, k);
          gpgg.generateGraph(target);
          return GraphExpr.newInstance(target);
        } catch (RuntimeException rex) {
          Errors.printMessage(S.PetersenGraph, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class RandomGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList2()) {
        int vertices = ast.arg1().first().toIntDefault();
        if (vertices <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast.arg1(), F.C1), engine);
        }
        if (vertices > Config.MAX_GRAPH_VERTICES_SIZE) {
          ASTElementLimitExceeded.throwIt(vertices);
        }

        int edges = ast.arg1().second().toIntDefault();
        if (edges <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast.arg1(), F.C2), engine);
        }
        if (edges > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(edges);
        }

        try {
          if (ast.isAST2()) {
            int k = ast.arg2().toIntDefault();
            if (k <= 0) {
              // Positive machine-sized integer expected at position `2` in `1`
              return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
            }
            if (k > Config.MAX_AST_SIZE) {
              ASTElementLimitExceeded.throwIt(k);
            }
            return F.mapRange(0, k, i -> randomGraph(vertices, edges));
          }
          return randomGraph(vertices, edges);
        } catch (IllegalArgumentException iae) {
          // `1` called with invalid parameters.
          return Errors.printMessage(ast.topHead(), "args", F.List(ast), engine);
        } catch (RuntimeException rex) {
          // rex.printStackTrace();
          Errors.printMessage(S.RandomGraph, rex, engine);
        }
      }
      return F.NIL;
    }

    private IExpr randomGraph(int vertices, int edges) {
      GnmRandomGraphGenerator<IExpr, ExprEdge> gen =
          new GnmRandomGraphGenerator<IExpr, ExprEdge>(vertices, edges);
      Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
          .undirected() //
          .allowingMultipleEdges(false) //
          .allowingSelfLoops(false) //
          .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
          .buildGraph();
      gen.generateGraph(target);
      return GraphExpr.newInstance(target);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class StarGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int order = ast.arg1().toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        StarGraphGenerator<IExpr, ExprEdge> gen = new StarGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        // Graph<IExpr, ExprEdge> target = new DefaultUndirectedGraph<IExpr,
        // ExprEdge>(ExprEdge.class);
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        Errors.printMessage(S.StarGraph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class WheelGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int order = ast.arg1().toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C1), engine);
      }
      if (order > Config.MAX_GRAPH_VERTICES_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        WheelGraphGenerator<IExpr, ExprEdge> gen = new WheelGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target = GraphTypeBuilder //
            .undirected().allowingMultipleEdges(false).allowingSelfLoops(false) //
            .vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class) //
            .buildGraph();
        // Graph<IExpr, ExprEdge> target = new DefaultUndirectedGraph<IExpr,
        // ExprEdge>(ExprEdge.class);
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        Errors.printMessage(S.WheelGraph, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
