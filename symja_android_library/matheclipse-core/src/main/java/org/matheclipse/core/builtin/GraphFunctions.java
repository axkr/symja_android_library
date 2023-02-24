package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.hipparchus.util.MathArrays;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.isomorphism.AHUUnrootedTreeIsomorphismInspector;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.alg.spanning.BoruvkaMinimumSpanningTree;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.ExprWeightedEdge;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.trie.Trie;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/** Functions for graph theory algorithms. */
public class GraphFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      S.BetweennessCentrality.setEvaluator(new BetweennessCentrality());
      S.ClosenessCentrality.setEvaluator(new ClosenessCentrality());
      S.AdjacencyMatrix.setEvaluator(new AdjacencyMatrix());
      S.ConnectedGraphQ.setEvaluator(new ConnectedGraphQ());
      S.EdgeList.setEvaluator(new EdgeList());
      S.EdgeQ.setEvaluator(new EdgeQ());
      S.EdgeRules.setEvaluator(new EdgeRules());
      S.EigenvectorCentrality.setEvaluator(new EigenvectorCentrality());
      S.EulerianGraphQ.setEvaluator(new EulerianGraphQ());
      S.FindCycle.setEvaluator(new FindCycle());
      S.FindEulerianCycle.setEvaluator(new FindEulerianCycle());
      S.FindHamiltonianCycle.setEvaluator(new FindHamiltonianCycle());
      S.FindGraphIsomorphism.setEvaluator(new FindGraphIsomorphism());
      S.FindIndependentVertexSet.setEvaluator(new FindIndependentVertexSet());
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
      S.GraphQ.setEvaluator(new GraphQ());
      S.GraphPeriphery.setEvaluator(new GraphPeriphery());
      S.GraphRadius.setEvaluator(new GraphRadius());
      S.GraphUnion.setEvaluator(new GraphUnion());
      S.HamiltonianGraphQ.setEvaluator(new HamiltonianGraphQ());
      S.IndexGraph.setEvaluator(new IndexGraph());
      S.IsomorphicGraphQ.setEvaluator(new IsomorphicGraphQ());
      S.LineGraph.setEvaluator(new LineGraph());
      S.PathGraphQ.setEvaluator(new PathGraphQ());
      S.PlanarGraphQ.setEvaluator(new PlanarGraphQ());
      S.VertexEccentricity.setEvaluator(new VertexEccentricity());
      S.VertexList.setEvaluator(new VertexList());
      S.VertexQ.setEvaluator(new VertexQ());
      S.WeaklyConnectedGraphQ.setEvaluator(new WeaklyConnectedGraphQ());
      S.WeightedAdjacencyMatrix.setEvaluator(new WeightedAdjacencyMatrix());
      S.WeightedGraphQ.setEvaluator(new WeightedGraphQ());
    }
  }

  private static class GraphIntersection extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = createGraph(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        Graph<IExpr, ? extends IExprEdge> resultGraph =
            (Graph<IExpr, ? extends IExprEdge>) gex1.toData();
        GraphType t = resultGraph.getType();
        if (t == null) {
          return F.NIL;
        }
        resultGraph = applyFunctionArg1(resultGraph);
        for (int i = 2; i < ast.size(); i++) {
          GraphExpr<?> gexArg = createGraph(ast.get(i));
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
        return GraphExpr.newInstance(resultGraph);

      } catch (RuntimeException rex) {
        LOGGER.debug("GraphCenter.evaluate() failed", rex);
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
      for (IExpr v : Sets.intersection(graph1.vertexSet(), graph2.vertexSet())) {
        resultGraph.addVertex(v);
      }
      SetView<? extends IExprEdge> graphSet = Sets.intersection(graph1.edgeSet(), graph2.edgeSet());
      for (IExprEdge e : graphSet) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        if (resultGraph.containsVertex(v1) && resultGraph.containsVertex(v2)) {
          resultGraph.addEdge(v1, v2);
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }


  private static class GraphComplement extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = createGraph(ast.arg1());
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
        LOGGER.debug("GraphCenter.evaluate() failed", rex);
      }
      return F.NIL;
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
        GraphExpr<?> gex1 = createGraph(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        GraphExpr<?> gex2 = createGraph(ast.arg2());
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
          return setOperation(g1, g2, resultGraph);
        }

      } catch (RuntimeException rex) {
        LOGGER.debug("GraphCenter.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    protected IExpr setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ExprEdge> resultGraph) {
      for (IExpr v : Sets.union(graph1.vertexSet(), graph2.vertexSet())) {
        resultGraph.addVertex(v);
      }
      SetView<? extends IExprEdge> graphSet = Sets.difference(graph1.edgeSet(), graph2.edgeSet());
      for (IExprEdge e : graphSet) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        if (resultGraph.containsVertex(v1) && resultGraph.containsVertex(v2)) {
          resultGraph.addEdge(v1, v2);
        }
      }
      return GraphExpr.newInstance(resultGraph);
    }

  }


  private static class IndexGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = createGraph(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> graph = gex1.toData();

        int newIndex = 1;
        if (ast.isAST2()) {
          if (!ast.arg2().isInteger()) {
            return F.NIL;
          }
          int intIndex = ast.arg2().toIntDefault();
          if (intIndex == Integer.MAX_VALUE) {
            return F.NIL;
          }
          newIndex = intIndex;
        }
        Graph<IExpr, ?> resultGraph = indexGraph(graph, newIndex);
        if (resultGraph != null) {
          return GraphExpr.newInstance(resultGraph);
        }

      } catch (RuntimeException rex) {
        LOGGER.debug("GraphCenter.evaluate() failed", rex);
      }
      return F.NIL;
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
      return indexGraph(graph, 1);
    }

    @Override
    protected void setOperation(Graph<IExpr, ? extends IExprEdge> graph1,
        Graph<IExpr, ? extends IExprEdge> graph2, Graph<IExpr, ? extends IExprEdge> resultGraph) {
      Graph<IExpr, ? extends IExprEdge> g2 = indexGraph(graph2, graph1.vertexSet().size() + 1);
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
      SetView<? extends IExprEdge> graphSet = Sets.union(graph1.edgeSet(), graph2.edgeSet());
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
          GraphExpr<?> gex = createGraph(ast);
          if (gex != null) {
            return gex;
          }
        } else if (ast.size() >= 3 && ast.arg1().isList()) {
          IExpr edgeWeight = F.NIL;
          final OptionArgs options = new OptionArgs(S.Graph, ast, ast.argSize(), engine);
          IExpr option = options.getOption(S.EdgeWeight);
          if (option.isPresent() && !option.equals(S.Automatic)) {
            edgeWeight = option;
          }
          GraphType t = ast.arg1().isListOfEdges();
          if (t != null) {
            if (edgeWeight.isList()) {
              GraphExpr<ExprWeightedEdge> gex =
                  createWeightedGraph(F.NIL, (IAST) ast.arg1(), (IAST) edgeWeight);
              if (gex != null) {
                return gex;
              }
            } else {
              GraphExpr<ExprEdge> g = createGraph(F.NIL, (IAST) ast.arg1());
              if (g != null) {
                return g;
              }
            }
          } else {
            if (edgeWeight.isList()) {
              GraphExpr<ExprWeightedEdge> gex =
                  createWeightedGraph((IAST) ast.arg1(), (IAST) ast.arg2(), (IAST) edgeWeight);
              if (gex != null) {
                return gex;
              }
            } else {
              if (ast.arg2().isList()) {
                GraphExpr<ExprEdge> g = createGraph((IAST) ast.arg1(), (IAST) ast.arg2());
                if (g != null) {
                  return g;
                }
              }
            }
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphCTor.evaluate() failed", rex);
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
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        Graph<IExpr, ?> g = gex.toData();
        GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
        Set<IExpr> centerSet = graphMeasurer.getGraphCenter();
        IASTMutable list = F.ListAlloc(centerSet);
        return list;
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphCenter.evaluate() failed", rex);
      }
      return F.NIL;
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
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> g = gex.toData();
        GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
        INum diameter = F.num(graphMeasurer.getDiameter());
        if (gex.isWeightedGraph()) {
          return diameter;
        }
        int intDiameter = diameter.toIntDefault();
        if (intDiameter != Integer.MIN_VALUE) {
          return F.ZZ(intDiameter);
        }
        return diameter;
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphDiameter.evaluate() failed", rex);
      }
      return F.NIL;
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
        GraphExpr<?> gex = createGraph(ast.arg1());
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

        GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
        Set<IExpr> centerSet = graphMeasurer.getGraphPeriphery();
        return F.ListAlloc(centerSet);
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphPeriphery.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex != null) {
            return S.True;
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphQ.evaluate() failed", rex);
      }
      return S.False;
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
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> g = gex.toData();

        GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
        INum radius = F.num(graphMeasurer.getRadius());
        if (gex.isWeightedGraph()) {
          return radius;
        }
        int intRadius = radius.toIntDefault();
        if (intRadius != Integer.MIN_VALUE) {
          return F.ZZ(intRadius);
        }
        return radius;
      } catch (RuntimeException rex) {
        LOGGER.debug("GraphRadius.evaluate() failed", rex);
      }
      return F.NIL;
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
              double[][] matrix = m.toDoubleMatrix();
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

              GeodeticCalculator geoCalc = new GeodeticCalculator();
              Ellipsoid reference = Ellipsoid.WGS84;
              // create all possible edges between the given vertices
              for (int i = 0; i < rowDim - 1; i++) {
                GlobalPosition p1 = ((GeoPositionExpr) list.get(i + 1)).toData();
                for (int j = i + 1; j < rowDim; j++) {
                  GlobalPosition p2 = ((GeoPositionExpr) list.get(j + 1)).toData();
                  GeodeticMeasurement gm = geoCalc.calculateGeodeticMeasurement(reference, p1, p2);
                  g.setEdgeWeight(g.addEdge(F.ZZ(i + 1), F.ZZ(j + 1)), // GeoDistance
                      gm.getPointToPointDistance());
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
                sum.append(F.GeoDistance(list.get(lastPosition), list.get(position)));
                lastPosition = position;
              }
              return F.list(sum, shortestTourList);
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
        LOGGER.error("FindShortestTour..evaluate() failed", rex);
      }
      return F.NIL;
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
          GraphExpr<?> gex = createGraph(ast.arg1());
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
        LOGGER.debug("FindSpanningTree.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }

          if (gex.isWeightedGraph()) {
            return weightedGraphToAdjacencyMatrix((Graph<IExpr, ExprWeightedEdge>) gex.toData());
          } else {
            return graphToAdjacencyMatrix((Graph<IExpr, ExprEdge>) gex.toData());
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("AdjacencyMatrix.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ?> g = gex.toData();
          return edgesToIExpr(g)[0];
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("EdgeList.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {

        if (ast.isAST2() && ast.arg2().isEdge()) {
          IAST edge = (IAST) ast.arg2();
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex != null) {
            Graph<IExpr, ?> g = gex.toData();
            return F.booleSymbol(g.containsEdge(edge.first(), edge.second()));
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("EdgeQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static class EdgeRules extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ?> g = gex.toData();
          return edgesToRules(g)[0];
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("EdgeList.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class ClosenessCentrality extends AbstractEvaluator {

    protected Map<IExpr, Double> getScores(Graph<IExpr, ExprEdge> g) {
      final VertexScoringAlgorithm<IExpr, Double> bc =
          new org.jgrapht.alg.scoring.ClosenessCentrality<IExpr, ExprEdge>(g);

      Map<IExpr, Double> scores = bc.getScores();
      return scores;
    }

    protected Map<IExpr, Double> getWeightedScores(Graph<IExpr, ExprWeightedEdge> g) {
      final VertexScoringAlgorithm<IExpr, Double> bc =
          new org.jgrapht.alg.scoring.ClosenessCentrality<IExpr, ExprWeightedEdge>(g);

      Map<IExpr, Double> scores = bc.getScores();
      return scores;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> g = gex.toData();
        Map<IExpr, Double> scores;
        if (gex.isWeightedGraph()) {
          scores = getWeightedScores((Graph<IExpr, ExprWeightedEdge>) g);
        } else {
          scores = getScores((Graph<IExpr, ExprEdge>) g);
        }

        Set<IExpr> vertexSet = g.vertexSet();
        return F.mapSet(vertexSet, expr -> {
          Double value = scores.get(expr);
          if (value == null) {
            return null;
          }
          return F.num(value);
        });
      } catch (RuntimeException rex) {
        LOGGER.debug("ClosenessCentrality.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ConnectedGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ? extends IExprEdge> graph =
              (Graph<IExpr, ? extends IExprEdge>) gex.toData();
          return GraphTests.isConnected(graph) ? S.True : S.False;
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("PathGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BetweennessCentrality extends ClosenessCentrality {

    @Override
    protected Map<IExpr, Double> getScores(Graph<IExpr, ExprEdge> g) {
      final VertexScoringAlgorithm<IExpr, Double> bc =
          new org.jgrapht.alg.scoring.BetweennessCentrality<IExpr, ExprEdge>(g);

      Map<IExpr, Double> scores = bc.getScores();
      return scores;
    }
  }

  private static class EigenvectorCentrality extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // try {
      // GraphExpr<?> gex = createGraph(ast.arg1());
      // if (gex == null) {
      // return F.NIL;
      // }
      // Graph<IExpr, IExpr> g = (Graph<IExpr, IExpr>) gex.toData();
      // org.jgrapht.alg.scoring.EigenvectorCentrality<IExpr, IExpr> evc =
      // new org.jgrapht.alg.scoring.EigenvectorCentrality<IExpr, IExpr>(g);
      // Set<IExpr> vertexSet = g.vertexSet();
      // IASTAppendable list = F.ListAlloc(vertexSet.size());
      // for (IExpr entry : vertexSet) {
      //
      // Double score = evc.getVertexScore(entry);
      // list.append(F.num(score));
      // }
      // return list;
      // } catch (RuntimeException rex) {
      // LOGGER.debug("EigenvectorCentrality.evaluate() failed", rex);
      // }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
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
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("EulerianGraphQ.evaluate() failed", rex);
      }
      return S.False;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }

          // Graph<IExpr, ?> g = gex.toData();
          GraphPath<IExpr, ?> path = hamiltonianCycle(gex);
          if (path != null) {
            // Graph is Hamiltonian
            return S.True;
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("HamiltonianGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class IsomorphicGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = getGraphExpr(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        GraphExpr<?> gex2 = getGraphExpr(ast.arg2());
        if (gex2 == null) {
          return F.NIL;
        }
        AHUUnrootedTreeIsomorphismInspector<IExpr, ExprEdge> isomorphism =
            new AHUUnrootedTreeIsomorphismInspector<>((Graph<IExpr, ExprEdge>) gex1.toData(),
                (Graph<IExpr, ExprEdge>) gex2.toData());
        return F.booleSymbol(isomorphism.isomorphismExists());
      } catch (RuntimeException rex) {
        LOGGER.debug("IsomorphicGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static class FindCycle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        int minCycleLength = 0;
        int maxCycleLength = Integer.MAX_VALUE;
        int atMostCycles = 1;
        if (ast.argSize() >= 2) {
          IExpr arg2 = ast.arg2();
          if (arg2.isInfinity()) {
            // fall through
          } else {
            int vertexes = arg2.toIntDefault();
            if (vertexes > 0) {
              minCycleLength = vertexes;
              maxCycleLength = vertexes;
            } else if (arg2.isList2()) {
              vertexes = arg2.first().toIntDefault();
              if (vertexes <= 0) {
                // The argument `2` in `1` is not a valid parameter.
                return IOFunctions.printMessage(ast.topHead(), "inv", F.List(ast, arg2), engine);
              }
              minCycleLength = vertexes;
              vertexes = arg2.second().toIntDefault();
              if (vertexes <= 0) {
                // The argument `2` in `1` is not a valid parameter.
                return IOFunctions.printMessage(ast.topHead(), "inv", F.List(ast, arg2), engine);
              }
              maxCycleLength = vertexes;
            } else {
              // The argument `2` in `1` is not a valid parameter.
              return IOFunctions.printMessage(ast.topHead(), "inv", F.List(ast, arg2), engine);
            }
          }
        }
        if (ast.isAST3()) {
          IExpr arg3 = ast.arg3();
          if (arg3.equals(S.All)) {
            atMostCycles = Integer.MAX_VALUE;
          } else {
            atMostCycles = arg3.toIntDefault();
            if (atMostCycles <= 0) {
              // The argument `2` in `1` is not a valid parameter.
              return IOFunctions.printMessage(ast.topHead(), "inv", F.List(ast, arg3), engine);
            }
          }
        }
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        return findCycles(gex, minCycleLength, maxCycleLength, atMostCycles);
      } catch (RuntimeException rex) {
        LOGGER.debug("FindCycle.evaluate() failed", rex);
      }
      return F.NIL;
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
   * find an eulerian cycle in the <code>graph</code>.
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
   * {4-&gt;1,1-&gt;2,2-&gt;3,3-&gt;4}
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }

          GraphPath<IExpr, ?> path = eulerianCycle(gex);
          if (path == null) {
            // Graph is not Eulerian
            return F.CEmptyList;
          }
          final List<IExpr> iList = path.getVertexList();
          return F.mapRange(0, iList.size() - 1,
              i -> F.DirectedEdge(iList.get(i), iList.get(i + 1)));
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("FindEulerianCycle.evaluate() failed", rex);
      }
      return F.NIL;
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
   * <code> FindHamiltonianCycle(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find an hamiltonian cycle in the <code>graph</code>.
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
   * {1-&gt;2,2-&gt;3,3-&gt;4,4-&gt;1}
   * </code>
   * </pre>
   */
  private static class FindHamiltonianCycle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          GraphPath<IExpr, ?> path = hamiltonianCycle(gex);
          if (path == null) {
            // Graph is not Hamiltonian
            return F.CEmptyList;
          }
          List<IExpr> iList = path.getVertexList();
          return F.mapRange(0, iList.size() - 1,
              i -> F.DirectedEdge(iList.get(i), iList.get(i + 1)));
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("FindHamiltonianCycle.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class FindGraphIsomorphism extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex1 = getGraphExpr(ast.arg1());
        if (gex1 == null) {
          return F.NIL;
        }
        GraphExpr<?> gex2 = createGraph(ast.arg2());
        if (gex2 == null) {
          return F.NIL;
        }
        AHUUnrootedTreeIsomorphismInspector<IExpr, ExprEdge> isomorphism =
            new AHUUnrootedTreeIsomorphismInspector<>((Graph<IExpr, ExprEdge>) gex1.toData(),
                (Graph<IExpr, ExprEdge>) gex2.toData());
        IsomorphicGraphMapping<IExpr, ExprEdge> mapping = isomorphism.getMapping();
        if (mapping == null) {
          return F.CEmptyList;
        }
        return F.list(F.assoc(F.mapMap(mapping.getForwardMapping(), (k, v) -> F.Rule(k, v))));
      } catch (RuntimeException rex) {
        LOGGER.debug("FindGraphIsomorphism.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static class FindIndependentVertexSet extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // try {
      // GraphExpr<?> gex = createGraph(ast.arg1());
      // if (gex == null) {
      // return F.NIL;
      // }
      // Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>)gex.toData();
      // // VertexCoverAlgorithm<IExpr> greedy = new GreedyVCImpl<>(g);
      // // VertexCoverAlgorithm.VertexCover<IExpr> cover = greedy.getVertexCover();
      // // if (cover == null) {
      // // return F.List();
      // // }
      // // IASTAppendable resultList = F.ListAlloc(10);
      // // cover.forEach(x -> resultList.append(x));
      // // return resultList;
      //
      // ChordalGraphIndependentSetFinder<IExpr, ExprEdge> cgisf =
      // new ChordalGraphIndependentSetFinder<>(g);
      // IndependentSet<IExpr> independentSet = cgisf.getIndependentSet();
      // IASTAppendable resultList = F.ListAlloc(independentSet.size());
      // for (IExpr expr : independentSet) {
      // resultList.append(expr);
      // }
      // return resultList;
      // } catch (RuntimeException rex) {
      // LOGGER.debug("FindIndependentVertexSet.evaluate() failed", rex);
      // }
      return F.NIL;
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
   * <code> FindVertexCover(graph)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * algorithm to find a vertex cover for a <code>graph</code>. A vertex cover is a set of vertices
   * that touches all the edges in the graph.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Vertex_cover">Wikipedia - Vertex cover</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FindVertexCover({1&lt;-&gt;2,1&lt;-&gt;3,2&lt;-&gt;3,3&lt;-&gt;4,3&lt;-&gt;5,3&lt;-&gt;6})
   * {3,1}
   * </code>
   * </pre>
   */
  private static class FindVertexCover extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ?> g = gex.toData();
          // ChordalityInspector<IExpr, IExprEdge> inspector = new ChordalityInspector<IExpr,
          // IExprEdge>(g);
          VertexCoverAlgorithm<IExpr> greedy = new GreedyVCImpl<>(g);
          VertexCoverAlgorithm.VertexCover<IExpr> cover = greedy.getVertexCover();
          if (cover == null) {
            return F.List();
          }
          IASTAppendable result = F.ListAlloc(10);
          cover.forEach(x -> result.append(x));
          return result;
        }
      } catch (IllegalArgumentException iae) {
        LOGGER.log(engine.getLogLevel(), "Graph must be undirected");
      } catch (RuntimeException rex) {
        LOGGER.debug("FindVertexCover.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }

        Graph<IExpr, ?> g = gex.toData();

        DijkstraShortestPath<IExpr, ?> dijkstraAlg = new DijkstraShortestPath<>(g);
        SingleSourcePaths<IExpr, ?> iPaths = dijkstraAlg.getPaths(ast.arg2());
        GraphPath<IExpr, ?> path = iPaths.getPath(ast.arg3());

        return Object2Expr.convertList(path.getVertexList(), true, false);
      } catch (RuntimeException rex) {
        LOGGER.debug("FindShortestPath.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }


  private static class LineGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // TODO
      // try {
      // GraphExpr<ExprEdge> gex1 = createGraph(ast.arg1());
      // if (gex1 == null) {
      // return F.NIL;
      // }
      // Graph<IExpr, ExprEdge> g1 = gex1.toData();
      // LineGraphConverter<IExpr, ExprEdge, ExprEdge> lgc =
      // new LineGraphConverter<IExpr, ExprEdge, ExprEdge>(g1);
      // Graph<ExprEdge, ExprEdge> target = new SimpleGraph<>(ExprEdge.class);
      // lgc.convertToLineGraph(target);
      // // return GraphExpr.newInstance(target);
      // } catch (RuntimeException rex) {
      // LOGGER.debug("LineGraph.evaluate() failed", rex);
      // }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PathGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }

          Graph<IExpr, ? extends IExprEdge> graph =
              (Graph<IExpr, ? extends IExprEdge>) gex.toData();
          GraphType t = graph.getType();
          if (t == null) {
            return F.NIL;
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
      } catch (RuntimeException rex) {
        LOGGER.debug("PathGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class PlanarGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex == null) {
            return S.False;
          }

          if (gex.isWeightedGraph()) {
            Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
            PlanarityTestingAlgorithm<IExpr, ExprWeightedEdge> inspector =
                new BoyerMyrvoldPlanarityInspector<IExpr, ExprWeightedEdge>(g);
            return F.booleSymbol(inspector.isPlanar());
          } else {
            Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
            PlanarityTestingAlgorithm<IExpr, ExprEdge> inspector =
                new BoyerMyrvoldPlanarityInspector<IExpr, ExprEdge>(g);
            return F.booleSymbol(inspector.isPlanar());
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("PlanarGraphQ.evaluate() failed", rex);
      }
      return S.False;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        GraphExpr<?> gex = createGraph(ast.arg1());
        if (gex == null) {
          return F.NIL;
        }
        Graph<IExpr, ?> g = gex.toData();

        GraphMeasurer<IExpr, ?> graphMeasurer = new GraphMeasurer<>(g);
        Map<IExpr, Double> centerSet = graphMeasurer.getVertexEccentricityMap();
        Double dValue = centerSet.get(ast.arg2());
        if (dValue != null) {
          INum vertexEccentricity = F.num(dValue);
          if (gex.isWeightedGraph()) {
            return vertexEccentricity;
          }
          int intVertexEccentricity = vertexEccentricity.toIntDefault();
          if (intVertexEccentricity != Integer.MIN_VALUE) {
            return F.ZZ(intVertexEccentricity);
          }
          return vertexEccentricity;
        }

      } catch (RuntimeException rex) {
        LOGGER.debug("VertexEccentricity.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ?> g = gex.toData();
          return vertexToIExpr(g);
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("VertexList.evaluate() failed", rex);
      }
      return F.NIL;
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST2()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex != null) {
            Graph<IExpr, ?> g = gex.toData();
            return F.booleSymbol(g.containsVertex(ast.arg2()));
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("VertexQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class WeaklyConnectedGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          Graph<IExpr, ? extends IExprEdge> graph =
              (Graph<IExpr, ? extends IExprEdge>) gex.toData();
          return GraphTests.isWeaklyConnected(graph) ? S.True : S.False;
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("PathGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class WeightedAdjacencyMatrix extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = createGraph(ast.arg1());
          if (gex == null) {
            return F.NIL;
          }
          if (gex.isWeightedGraph()) {
            return weightedGraphToWeightedAdjacencyMatrix(
                (Graph<IExpr, ExprWeightedEdge>) gex.toData());
          } else {
            return graphToAdjacencyMatrix((Graph<IExpr, ExprEdge>) gex.toData());
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("WeightedAdjacencyMatrix.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static class WeightedGraphQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          GraphExpr<?> gex = getGraphExpr(ast.arg1());
          if (gex != null && gex.isWeightedGraph()) {
            return S.True;
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("WeightedGraphQ.evaluate() failed", rex);
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  /**
   * Get the <code>GraphExpr<?></code>.
   *
   * @param arg1
   * @return
   */
  private static GraphExpr<?> getGraphExpr(IExpr arg1) {
    if (arg1 instanceof GraphExpr) {
      return (GraphExpr<?>) arg1;
    }
    return null;
  }

  /**
   * Create a <code>Graph<IExpr, ExprWeightedEdge></code> or <code>Graph<IExpr, ExprEdge></code>. In
   * the case of a list of edges the method returns also a graph.
   *
   * @param arg1
   * @return
   */
  private static GraphExpr<?> createGraph(IExpr arg1) {
    Graph<IExpr, ExprEdge> g;
    if (arg1.isList()) {
      GraphType t = arg1.isListOfEdges();
      if (t != null) {
        if (t.isDirected()) {
          g = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        } else {
          g = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        }

        IAST list = (IAST) arg1;
        for (int i = 1; i < list.size(); i++) {
          IAST edge = list.getAST(i);
          g.addVertex(edge.arg1());
          g.addVertex(edge.arg2());
          g.addEdge(edge.arg1(), edge.arg2());
        }

        return GraphExpr.newInstance(g);
      }
      return null;
    }
    if (arg1.head().equals(S.Graph) && arg1 instanceof GraphExpr) {
      return (GraphExpr<?>) arg1;
    }
    if (arg1.isASTSizeGE(S.Graph, 2)) {
      arg1 = arg1.first();

      GraphType t = arg1.isListOfEdges();
      if (t != null) {
        if (t.isDirected()) {
          g = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        } else {
          g = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        }

        IAST list = (IAST) arg1;
        for (int i = 1; i < list.size(); i++) {
          IAST edge = list.getAST(i);
          g.addVertex(edge.arg1());
          g.addVertex(edge.arg2());
          g.addEdge(edge.arg1(), edge.arg2());
        }

        return GraphExpr.newInstance(g);
      }
    }

    return null;
  }

  /**
   * Create an internal DataExpr Graph.
   *
   * @param arg1
   * @return
   */
  private static GraphExpr<ExprWeightedEdge> createWeightedGraph(final IAST vertices,
      final IAST arg1, final IAST edgeWeight) {
    if (arg1.size() != edgeWeight.size()) {
      return null;
    }
    Graph<IExpr, ExprWeightedEdge> g;
    GraphType t = arg1.isListOfEdges();
    if (t != null) {

      if (t.isDirected()) {
        g = new DefaultDirectedWeightedGraph<IExpr, ExprWeightedEdge>(ExprWeightedEdge.class);
      } else {
        g = new DefaultUndirectedWeightedGraph<IExpr, ExprWeightedEdge>(ExprWeightedEdge.class);
      }

      IAST list = arg1;
      for (int i = 1; i < list.size(); i++) {
        IAST edge = list.getAST(i);
        g.addVertex(edge.arg1());
        g.addVertex(edge.arg2());
        g.addEdge(edge.arg1(), edge.arg2());
      }

      if (t.isDirected()) {
        DefaultDirectedWeightedGraph gw = (DefaultDirectedWeightedGraph<IExpr, ExprWeightedEdge>) g;
        for (int i = 1; i < list.size(); i++) {
          IAST edge = list.getAST(i);
          gw.setEdgeWeight(edge.arg1(), edge.arg2(), edgeWeight.get(i).evalf());
        }
      } else {
        DefaultUndirectedWeightedGraph gw =
            (DefaultUndirectedWeightedGraph<IExpr, ExprWeightedEdge>) g;
        for (int i = 1; i < list.size(); i++) {
          IAST edge = list.getAST(i);
          gw.setEdgeWeight(edge.arg1(), edge.arg2(), edgeWeight.get(i).evalf());
        }
      }

      return GraphExpr.newInstance(g);
    }

    return null;
  }

  private static GraphExpr<ExprEdge> createGraph(final IAST vertices, final IAST edges) {

    Graph<IExpr, ExprEdge> g;
    GraphType t = edges.isListOfEdges();
    if (t != null) {
      if (t.isDirected()) {
        g = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      } else {
        g = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      }
      if (vertices.isList()) {
        // Graph<IExpr, IExprEdge> g = new DefaultDirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
        for (int i = 1; i < vertices.size(); i++) {
          g.addVertex(vertices.get(i));
        }
      }

      for (int i = 1; i < edges.size(); i++) {
        IAST edge = edges.getAST(i);
        g.addVertex(edge.arg1());
        g.addVertex(edge.arg2());
        g.addEdge(edge.arg1(), edge.arg2());
      }

      return GraphExpr.newInstance(g);
    }

    return null;
  }

  private static IAST findCycles(GraphExpr<?> gex, int minCycleLength, int maxCycleLength,
      int atMostCycles) {
    if (gex.isWeightedGraph()) {
      Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
      DirectedSimpleCycles<IExpr, ExprWeightedEdge> algorithm =
          new SzwarcfiterLauerSimpleCycles<IExpr, ExprWeightedEdge>(
              (Graph<IExpr, ExprWeightedEdge>) gex.toData());
      return findCyclesList(algorithm, minCycleLength, maxCycleLength, atMostCycles);
    } else {
      if (gex.isUndirectedGraph()) {
        Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
        GraphType type = g.getType();
        CycleBasisAlgorithm<IExpr, ExprEdge> algorithm =
            new PatonCycleBase<IExpr, ExprEdge>((Graph<IExpr, ExprEdge>) gex.toData());
        return findCyclesSet(algorithm, minCycleLength, maxCycleLength, atMostCycles, type);
      } else {
        Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
        DirectedSimpleCycles<IExpr, ExprEdge> algorithm =
            new SzwarcfiterLauerSimpleCycles<IExpr, ExprEdge>(
                (Graph<IExpr, ExprEdge>) gex.toData());
        return findCyclesList(algorithm, minCycleLength, maxCycleLength, atMostCycles);
      }
    }
  }

  private static IAST findCyclesSet(CycleBasisAlgorithm<IExpr, ExprEdge> algorithm,
      int minCycleLength, int maxCycleLength, int atMostCycles, GraphType type) {
    try {
      CycleBasis<IExpr, ExprEdge> cycleBasis = algorithm.getCycleBasis();
      Set<List<ExprEdge>> cycles = cycleBasis.getCycles();
      // System.out.println(cycles.toString());
      IASTAppendable result =
          F.ListAlloc(cycles.size() < atMostCycles ? cycles.size() : atMostCycles);
      int counter = 0;
      for (List<ExprEdge> list : cycles) {
        if (list.size() >= minCycleLength && list.size() <= maxCycleLength) {
          if (counter++ >= atMostCycles) {
            break;
          }
          int size = list.size();
          IASTAppendable cycle = F.ListAlloc(size);
          IASTAppendable weights = F.ListAlloc(size);
          for (int i = 0; i < size; i++) {
            edgeToIExpr(type, list.get(i), cycle, weights, size);
          }
          result.append(cycle);
        }
      }
      return result;
    } catch (IllegalArgumentException iae) {
    }
    return F.NIL;
  }

  private static IAST findCyclesList(DirectedSimpleCycles<IExpr, ?> algorithm, int minCycleLength,
      int maxCycleLength, int atMostCycles) {
    try {
      List<List<IExpr>> path = algorithm.findSimpleCycles();
      IASTAppendable result = F.ListAlloc(path.size() < atMostCycles ? path.size() : atMostCycles);
      int counter = 0;
      for (int i = 0; i < path.size(); i++) {
        List<IExpr> vertexPath = path.get(i);
        if (vertexPath.size() >= minCycleLength && vertexPath.size() <= maxCycleLength) {
          if (counter++ >= atMostCycles) {
            break;
          }
          IASTAppendable list = F.ListAlloc(vertexPath.size() + 1);
          for (int j = 0; j < vertexPath.size() - 1; j++) {
            list.append(F.DirectedEdge(vertexPath.get(j), vertexPath.get(j + 1)));
          }
          list.append(F.DirectedEdge(vertexPath.get(vertexPath.size() - 1), vertexPath.get(0)));
          result.append(list);
        }
      }
      return result;
    } catch (IllegalArgumentException iae) {
    }
    return F.NIL;
  }

  /**
   * Create an eulerian cycle.
   *
   * @param gex a graph object
   * @return <code>null</code> if no eulerian cycle can be created
   */
  private static GraphPath<IExpr, ?> eulerianCycle(GraphExpr<?> gex) {
    if (gex.isWeightedGraph()) {
      Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
      EulerianCycleAlgorithm<IExpr, ExprWeightedEdge> eca = new HierholzerEulerianCycle<>();
      try {
        return eca.getEulerianCycle(g);
      } catch (IllegalArgumentException iae) {
        // Graph is not Eulerian
      }
    } else {
      Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
      EulerianCycleAlgorithm<IExpr, ExprEdge> eca = new HierholzerEulerianCycle<>();
      try {
        return eca.getEulerianCycle(g);
      } catch (IllegalArgumentException iae) {
        // Graph is not Eulerian
      }
    }
    return null;
  }

  private static GraphPath<IExpr, ?> hamiltonianCycle(GraphExpr<?> gex) {
    if (gex.isWeightedGraph()) {
      Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
      HamiltonianCycleAlgorithm<IExpr, ExprWeightedEdge> eca = new HeldKarpTSP<>();
      try {
        return eca.getTour(g);
      } catch (IllegalArgumentException iae) {
        // Graph is not Hamiltonian
      }
    } else {
      Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
      HamiltonianCycleAlgorithm<IExpr, ExprEdge> eca = new HeldKarpTSP<>();
      try {
        return eca.getTour(g);
      } catch (IllegalArgumentException iae) {
        // Graph is not Hamiltonian
      }
    }
    return null;
  }

  private static Graph<IExpr, ? extends IExprEdge> indexGraph(Graph<IExpr, ?> graph, int newIndex) {
    Graph<IExpr, ? extends IExprEdge> resultGraph;
    GraphType t = graph.getType();
    if (t != null) {
      if (t.isDirected()) {
        resultGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      } else {
        resultGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
      }

      HashMap<IExpr, IExpr> hashMap = new HashMap<IExpr, IExpr>();
      for (IExpr v : graph.vertexSet()) {
        IInteger indexExpr = F.ZZ(newIndex++);
        hashMap.put(v, indexExpr);
        resultGraph.addVertex(indexExpr);
      }
      Set<? extends IExprEdge> edgeSet = (Set<? extends IExprEdge>) graph.edgeSet();
      for (IExprEdge e : edgeSet) {
        IExpr v1 = e.lhs();
        IExpr v2 = e.rhs();
        IExpr lhs = hashMap.get(v1);
        IExpr rhs = hashMap.get(v2);
        resultGraph.addEdge(lhs, rhs);
      }
      return resultGraph;
    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private GraphFunctions() {}

  /**
   * Convert a graph into an IExpr object.
   *
   * @param graph
   * @return
   */
  public static IExpr graphToIExpr(AbstractBaseGraph<IExpr, ExprEdge> graph) {
    IASTAppendable vertexes = vertexToIExpr(graph);
    IASTAppendable[] edgeData = edgesToIExpr(graph);
    if (edgeData[1].isNIL()) {
      return F.Graph(vertexes, edgeData[0]);
    }
    return F.Graph(vertexes, edgeData[0], F.list(F.Rule(S.EdgeWeight, edgeData[1])));
  }

  public static IExpr weightedGraphToIExpr(AbstractBaseGraph<IExpr, ExprWeightedEdge> graph) {
    IASTAppendable vertexes = vertexToIExpr(graph);
    IASTAppendable[] res = weightedEdgesToIExpr(graph);
    return F.Graph(vertexes, res[0], F.list(F.Rule(S.EdgeWeight, res[1])));
  }

  private static IASTAppendable vertexToIExpr(Graph<IExpr, ?> graph) {
    return F.mapSet(graph.vertexSet(), x -> x);
  }

  private static IASTAppendable[] edgesToIExpr(Graph<IExpr, ?> graph) {
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

  private static void edgeToIExpr(GraphType type, Object edge, IASTAppendable edges,
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

  private static IASTAppendable[] edgesToRules(Graph<IExpr, ?> graph) {
    Set<Object> edgeSet = (Set<Object>) graph.edgeSet();
    IASTAppendable edges = F.ListAlloc(edgeSet.size());
    IASTAppendable weights = null;
    GraphType type = graph.getType();

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

  /**
   * Return an array of 2 lists. At index 0 the list of edges. At index 1 the list of corresponding
   * weights.
   *
   * @param graph
   * @return an array of 2 lists. At index 0 the list of edges. At index 1 the list of corresponding
   *         weights.
   */
  private static IASTAppendable[] weightedEdgesToIExpr(Graph<IExpr, ExprWeightedEdge> graph) {

    Set<ExprWeightedEdge> edgeSet = graph.edgeSet();
    IASTAppendable edges = F.ListAlloc(edgeSet.size());
    IASTAppendable weights = F.ListAlloc(edgeSet.size());
    GraphType type = graph.getType();
    if (type.isDirected()) {
      for (ExprWeightedEdge edge : edgeSet) {
        edges.append(F.DirectedEdge(edge.lhs(), edge.rhs()));
        weights.append(edge.weight());
      }
    } else {
      for (ExprWeightedEdge edge : edgeSet) {
        edges.append(F.UndirectedEdge(edge.lhs(), edge.rhs()));
        weights.append(edge.weight());
      }
    }
    return new IASTAppendable[] {edges, weights};
  }

  public static IExpr graphToAdjacencyMatrix(Graph<IExpr, ExprEdge> graph) {
    Set<IExpr> vertexSet = graph.vertexSet();
    int size = vertexSet.size();
    Map<IExpr, Integer> map = new HashMap<IExpr, Integer>();
    int indx = 1;
    for (IExpr expr : vertexSet) {
      map.put(expr, indx++);
    }

    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    for (ExprEdge edge : graph.edgeSet()) {
      IExpr lhs = edge.lhs();
      IExpr rhs = edge.rhs();
      int from = map.get(lhs);
      int to = map.get(rhs);
      trie.put(new int[] {from, to}, F.C1);
      if (graph.containsEdge(rhs, lhs)) {
        trie.put(new int[] {to, from}, F.C1);
      }
    }
    return new SparseArrayExpr(trie, new int[] {size, size}, F.C0, false);
  }

  public static IExpr weightedGraphToAdjacencyMatrix(Graph<IExpr, ExprWeightedEdge> g) {
    Set<IExpr> vertexSet = g.vertexSet();
    int size = vertexSet.size();
    Map<IExpr, Integer> map = new HashMap<IExpr, Integer>();
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

  public static IExpr weightedGraphToWeightedAdjacencyMatrix(Graph<IExpr, ExprWeightedEdge> g) {
    Set<IExpr> vertexSet = g.vertexSet();
    int size = vertexSet.size();
    Map<IExpr, Integer> map = new HashMap<IExpr, Integer>();
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

  /**
   * Convert a Graph into a JavaScript visjs.org form
   *
   * @param graphExpr
   * @return
   */
  public static String graphToJSForm(GraphExpr graphExpr) {

    GraphExpr<ExprEdge> gex = graphExpr;
    AbstractBaseGraph<IExpr, ?> g = (AbstractBaseGraph<IExpr, ?>) gex.toData();
    Map<IExpr, Integer> map = new HashMap<IExpr, Integer>();
    StringBuilder buf = new StringBuilder();
    if (g.getType().isWeighted()) {
      weightedGraphToVisjs(map, buf, (AbstractBaseGraph<IExpr, ExprWeightedEdge>) g);
    } else {
      graphToVisjs(map, buf, (AbstractBaseGraph<IExpr, ExprEdge>) g);
    }
    return buf.toString();
  }

  public static void graphToVisjs(Map<IExpr, Integer> map, StringBuilder buf,
      AbstractBaseGraph<IExpr, ExprEdge> g) {
    vertexToVisjs(map, buf, g);
    edgesToVisjs(map, buf, g);
  }

  public static void weightedGraphToVisjs(Map<IExpr, Integer> map, StringBuilder buf,
      AbstractBaseGraph<IExpr, ExprWeightedEdge> g) {
    vertexToVisjs(map, buf, g);
    weightedEdgesToVisjs(map, buf, g);
  }

  private static void vertexToVisjs(Map<IExpr, Integer> map, StringBuilder buf, Graph<IExpr, ?> g) {
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

  private static void edgesToVisjs(Map<IExpr, Integer> map, StringBuilder buf,
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
}
