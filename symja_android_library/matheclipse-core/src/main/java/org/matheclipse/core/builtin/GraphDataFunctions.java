package org.matheclipse.core.builtin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.Tries;

/**
 * Functions for graph theory algorithms.
 *
 */
public class GraphDataFunctions {

	private static Map<String, Supplier<Graph>> GRAPH_MAP = Tries.forStrings();

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			GRAPH_MAP.put("PetersenGraph", () -> petersenGraph());
			GRAPH_MAP.put("PappusGraph", () -> pappusGraph());
			F.GraphData.setEvaluator(new GraphData());
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
						return engine.printMessage(
								"GraphData: no value associated with the specified graph name: " + graphName);
					}
				}
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public IAST options() {
			return F.List(F.Rule(F.EdgeWeight, F.Automatic));
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	/**
	 * Generates the <a href="https://en.wikipedia.org/wiki/Pappus_graph">Pappus Graph</a>. The Pappus Graph is a
	 * bipartite 3-regular undirected graph with 18 vertices and 27 edges.
	 * 
	 */
	private static Graph<IExpr, ExprEdge> pappusGraph() {
		Graph<IExpr, ExprEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
				.vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class).buildGraph();
		// new NamedGraphGenerator<Integer, DefaultEdge>().generatePappusGraph(g);
		HashMap<IExpr, IExpr> vertexMap = new HashMap<>();
		// vertexMap.clear();
		int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 6 }, { 1, 2 }, { 1, 7 }, { 2, 3 }, { 2, 8 }, { 3, 4 }, { 3, 9 },
				{ 4, 5 }, { 4, 10 }, { 5, 11 }, { 6, 13 }, { 6, 17 }, { 7, 12 }, { 7, 14 }, { 8, 13 }, { 8, 15 },
				{ 9, 14 }, { 9, 16 }, { 10, 15 }, { 10, 17 }, { 11, 12 }, { 11, 16 }, { 12, 15 }, { 13, 16 },
				{ 14, 17 } };
		for (int[] edge : edges) {
			addEdge(vertexMap, g, edge[0], edge[1]);
		}
		return g;
	}

	private static Graph<IExpr, ExprEdge> petersenGraph() {
		Graph<IExpr, ExprEdge> g = GraphTypeBuilder //
				.undirected().allowingMultipleEdges(false).allowingSelfLoops(false)//
				.vertexSupplier(new IntegerSupplier(1)).edgeClass(ExprEdge.class)//
				.buildGraph();
		GeneralizedPetersenGraphGenerator<IExpr, ExprEdge> gpgg = new GeneralizedPetersenGraphGenerator<>(5, 2);
		gpgg.generateGraph(g);
		return g;
	}

	// --------------Helper methods-----------------/
	private static IExpr addVertex(HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int i) {
		IInteger intNumber = F.ZZ(i);
		if (!vertexMap.containsKey(intNumber)) {
			vertexMap.put(intNumber, targetGraph.addVertex());
		}
		return vertexMap.get(intNumber);
	}

	private static void addEdge(HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int i, int j) {
		IExpr u = addVertex(vertexMap, targetGraph, i);
		IExpr v = addVertex(vertexMap, targetGraph, j);
		targetGraph.addEdge(u, v);
	}

	private static void addCycle(HashMap<IExpr, IExpr> vertexMap, Graph<IExpr, ExprEdge> targetGraph, int array[]) {
		for (int i = 0; i < array.length; i++) {
			addEdge(vertexMap, targetGraph, array[i], array[(i + 1) % array.length]);
		}
	}

	private GraphDataFunctions() {

	}

}
