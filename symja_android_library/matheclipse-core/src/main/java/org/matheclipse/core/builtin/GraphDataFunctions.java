package org.matheclipse.core.builtin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;

/** Functions for graph theory algorithms. */
public class GraphDataFunctions {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final TrieBuilder<String, Supplier<Graph>, ArrayList<Supplier<Graph>>>
      TRIE_STRING2GRAPH_BUILDER = TrieBuilder.create();
  private static Map<String, Supplier<Graph>> GRAPH_MAP =
      TRIE_STRING2GRAPH_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      //      GRAPH_MAP.put("PetersenGraph", () -> petersenGraph());
      GRAPH_MAP.put("PappusGraph", () -> pappusGraph());
      S.GraphData.setEvaluator(new GraphData());
      S.PetersenGraph.setEvaluator(new PetersenGraph());
      S.StarGraph.setEvaluator(new StarGraph());
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
        if (ast.isAST1()) {
          if (ast.arg1().isString()) {
            String graphName = ((IStringX) ast.arg1()).toString();
            Supplier<Graph> supplier = GRAPH_MAP.get(graphName);
            if (supplier != null) {
              Graph<IExpr, ExprEdge> g = supplier.get();
              return GraphExpr.newInstance(g);
            }
            LOGGER.log(engine.getLogLevel(),
                "GraphData: no value associated with the specified graph name: {}", graphName);
          }
        }
      } catch (RuntimeException rex) {
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(
          newSymbol, //
          F.List(F.Rule(S.EdgeWeight, S.Automatic)));
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
        // The argument `2`  in  `1`  is not valid. 0 or 2 arguments expected.
        return IOFunctions.printMessage(ast.topHead(), "inv", F.List(ast, ast.arg1()), engine);
      }
      if (ast.isAST0()) {
        Graph<IExpr, ExprEdge> target =
            GraphTypeBuilder //
                .undirected()
                .allowingMultipleEdges(false)
                .allowingSelfLoops(false) //
                .vertexSupplier(new IntegerSupplier(1))
                .edgeClass(ExprEdge.class) //
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
          return IOFunctions.printMessage(ast.topHead(), "intpm", F.List(ast, F.C1), engine);
        }
        if (order > Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(order);
        }
        int k = ast.arg2().toIntDefault();
        if (k <= 0 || k > order) {
          // Positive machine-sized integer expected at position `2` in `1`
          return IOFunctions.printMessage(ast.topHead(), "intpm", F.List(ast, F.C2), engine);
        }
        try {
          Graph<IExpr, ExprEdge> target =
              GraphTypeBuilder //
                  .undirected()
                  .allowingMultipleEdges(false)
                  .allowingSelfLoops(false) //
                  .vertexSupplier(new IntegerSupplier(1))
                  .edgeClass(ExprEdge.class) //
                  .buildGraph();
          GeneralizedPetersenGraphGenerator<IExpr, ExprEdge> gpgg =
              new GeneralizedPetersenGraphGenerator<>(order, k);
          gpgg.generateGraph(target);
          return GraphExpr.newInstance(target);
        } catch (RuntimeException rex) {
          if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class StarGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int order = ast.arg1().toIntDefault();
      if (order <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`
        return IOFunctions.printMessage(ast.topHead(), "intpm", F.List(ast, F.C1), engine);
      }
      if (order > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(order);
      }

      try {
        StarGraphGenerator<IExpr, ExprEdge> gen = new StarGraphGenerator<IExpr, ExprEdge>(order);
        Graph<IExpr, ExprEdge> target =
            GraphTypeBuilder //
                .undirected()
                .allowingMultipleEdges(false)
                .allowingSelfLoops(false) //
                .vertexSupplier(new IntegerSupplier(1))
                .edgeClass(ExprEdge.class) //
                .buildGraph();
        //	        Graph<IExpr, ExprEdge> target = new DefaultUndirectedGraph<IExpr,
        // ExprEdge>(ExprEdge.class);
        gen.generateGraph(target);
        return GraphExpr.newInstance(target);
      } catch (RuntimeException rex) {
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
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
        GraphTypeBuilder.undirected()
            .allowingMultipleEdges(false)
            .allowingSelfLoops(false)
            .vertexSupplier(new IntegerSupplier(1))
            .edgeClass(ExprEdge.class)
            .buildGraph();
    // new NamedGraphGenerator<Integer, DefaultEdge>().generatePappusGraph(g);
    HashMap<IExpr, IExpr> vertexMap = new HashMap<>();
    // vertexMap.clear();
    int[][] edges = {
      {0, 1},
      {0, 5},
      {0, 6},
      {1, 2},
      {1, 7},
      {2, 3},
      {2, 8},
      {3, 4},
      {3, 9},
      {4, 5},
      {4, 10},
      {5, 11},
      {6, 13},
      {6, 17},
      {7, 12},
      {7, 14},
      {8, 13},
      {8, 15},
      {9, 14},
      {9, 16},
      {10, 15},
      {10, 17},
      {11, 12},
      {11, 16},
      {12, 15},
      {13, 16},
      {14, 17}
    };
    for (int[] edge : edges) {
      addEdge(vertexMap, g, edge[0], edge[1]);
    }
    return g;
  }

  // --------------Helper methods-----------------/
  private static IExpr addVertex(
      HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int i) {
    IInteger intNumber = F.ZZ(i);
    if (!vertexMap.containsKey(intNumber)) {
      vertexMap.put(intNumber, targetGraph.addVertex());
    }
    return vertexMap.get(intNumber);
  }

  private static void addEdge(
      HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int i, int j) {
    IExpr u = addVertex(vertexMap, targetGraph, i);
    IExpr v = addVertex(vertexMap, targetGraph, j);
    targetGraph.addEdge(u, v);
  }

  private static void addCycle(
      HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int array[]) {
    for (int i = 0; i < array.length; i++) {
      addEdge(vertexMap, targetGraph, array[i], array[(i + 1) % array.length]);
    }
  }

  private GraphDataFunctions() {}
}
