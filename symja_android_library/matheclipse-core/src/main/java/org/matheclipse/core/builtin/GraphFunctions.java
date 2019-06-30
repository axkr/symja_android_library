package org.matheclipse.core.builtin;

import java.util.List;
import java.util.Set;

import org.hipparchus.util.MathArrays;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.spanning.BoruvkaMinimumSpanningTree;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IExprEdge;
import org.matheclipse.core.expression.IExprWeightedEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class GraphFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Graph.setEvaluator(new GraphCTor());
			F.AdjacencyMatrix.setEvaluator(new AdjacencyMatrix());
			F.EdgeList.setEvaluator(new EdgeList());
			F.EdgeQ.setEvaluator(new EdgeQ());
			F.EulerianGraphQ.setEvaluator(new EulerianGraphQ());
			F.FindEulerianCycle.setEvaluator(new FindEulerianCycle());
			F.FindHamiltonianCycle.setEvaluator(new FindHamiltonianCycle());
			F.FindShortestPath.setEvaluator(new FindShortestPath());
			F.FindShortestTour.setEvaluator(new FindShortestTour());
			F.FindSpanningTree.setEvaluator(new FindSpanningTree());
			F.GraphQ.setEvaluator(new GraphQ());
			F.HamiltonianGraphQ.setEvaluator(new HamiltonianGraphQ());
			F.VertexList.setEvaluator(new VertexList());
			F.VertexQ.setEvaluator(new VertexQ());
		}
	}

	/**
	 * <code>Graph</code> constructor.
	 *
	 */
	private static class GraphCTor extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> g = createGraph(ast.arg1());
					if (g != null) {
						return g;
					}
				} else if (ast.isAST2()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> g = createGraph(ast.arg1(), ast.arg2());
					if (g != null) {
						return g;
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	private static class GraphQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex != null) {
						return F.True;
					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

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
							int rowDim = dim[0];
							int colDim = dim[1];
							if (colDim == 2) {
								Graph<IInteger, IExprWeightedEdge> g = new SimpleWeightedGraph<>(
										IExprWeightedEdge.class);
								for (int i = 1; i <= rowDim; i++) {
									g.addVertex(F.ZZ(i));
								}
								for (int i = 0; i < rowDim; i++) {
									for (int j = i + 1; j < rowDim; j++) {
										g.setEdgeWeight(g.addEdge(F.ZZ(i + 1), F.ZZ(j + 1)), // EuclideanInstance
												MathArrays.distance(matrix[i], matrix[j]));
									}
								}
								GraphPath<IInteger, IExprWeightedEdge> tour = new HeldKarpTSP<IInteger, IExprWeightedEdge>()
										.getTour(g);

								List<IInteger> tourPositions = tour.getVertexList();
								IASTAppendable tourList = F.ListAlloc(tourPositions.size());
								IASTAppendable sum = F.PlusAlloc(tourPositions.size());
								IInteger lastPosition = tourPositions.get(tourPositions.size() - 1);
								tourList.append(lastPosition);
								for (int i = tourPositions.size() - 2; i >= 0; i--) {
									IInteger position = tourPositions.get(i);
									tourList.append(position);
									sum.append(F.EuclideanDistance(m.get(lastPosition), m.get(position)));
									lastPosition = position;
								}
								return F.List(sum, tourList);
							}
						}
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_3;
		}
	}

	private static class FindSpanningTree extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}
					Graph<IExpr, IExprEdge> g = dex.toData();
					SpanningTreeAlgorithm<IExprEdge> k = new BoruvkaMinimumSpanningTree<IExpr, IExprEdge>(g);
					Set<IExprEdge> edgeSet = k.getSpanningTree().getEdges();
					Graph<IExpr, IExprEdge> gResult = new DefaultDirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
					Graphs.addAllEdges(gResult, g, edgeSet);
					return DataExpr.newInstance(F.Graph, gResult);
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class AdjacencyMatrix extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}

					Graph<IExpr, IExprEdge> g = dex.toData();
					return graphToAdjacencyMatrix(g);
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class EdgeList extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}
					Graph<IExpr, IExprEdge> g = dex.toData();
					return edgesToIExpr(g);
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class EdgeQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {

				if (ast.isAST2() && ast.arg2().isEdge()) {
					IAST edge = (IAST) ast.arg2();
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex != null) {
						Graph<IExpr, IExprEdge> g = dex.toData();
						return F.bool(g.containsEdge(edge.first(), edge.second()));
					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	private static class EulerianGraphQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}

					Graph<IExpr, IExprEdge> g = dex.toData();
					GraphPath<IExpr, IExprEdge> path = eulerianCycle(g);
					if (path != null) {
						// Graph is Eulerian
						return F.True;
					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class HamiltonianGraphQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}

					Graph<IExpr, IExprEdge> g = dex.toData();
					GraphPath<IExpr, IExprEdge> path = hamiltonianCycle(g);
					if (path != null) {
						// Graph is Hamiltonian
						return F.True;
					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class FindEulerianCycle extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}

					Graph<IExpr, IExprEdge> g = dex.toData();
					GraphPath<IExpr, IExprEdge> path = eulerianCycle(g);
					if (path == null) {
						// Graph is not Eulerian
						return F.CEmptyList;
					}
					List<IExpr> iList = path.getVertexList();
					IASTAppendable list = F.ListAlloc(iList.size());
					for (int i = 0; i < iList.size() - 1; i++) {
						list.append(F.DirectedEdge(iList.get(i), iList.get(i + 1)));
					}
					return list;
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class FindHamiltonianCycle extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}

					Graph<IExpr, IExprEdge> g = dex.toData();
					GraphPath<IExpr, IExprEdge> path = hamiltonianCycle(g);
					if (path == null) {
						// Graph is not Hamiltonian
						return F.CEmptyList;
					}
					List<IExpr> iList = path.getVertexList();
					IASTAppendable list = F.ListAlloc(iList.size());
					for (int i = 0; i < iList.size() - 1; i++) {
						list.append(F.DirectedEdge(iList.get(i), iList.get(i + 1)));
					}
					return list;
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class FindShortestPath extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
				if (dex == null) {
					return F.NIL;
				}

				Graph<IExpr, IExprEdge> g = dex.toData();

				DijkstraShortestPath<IExpr, IExprEdge> dijkstraAlg = new DijkstraShortestPath<>(g);
				SingleSourcePaths<IExpr, IExprEdge> iPaths = dijkstraAlg.getPaths(ast.arg2());
				GraphPath<IExpr, IExprEdge> path = iPaths.getPath(ast.arg3());

				return Object2Expr.convertList(path.getVertexList());
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_3;
		}
	}

	private static class VertexList extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST1()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex == null) {
						return F.NIL;
					}
					Graph<IExpr, IExprEdge> g = dex.toData();
					return vertexToIExpr(g);
					// Set<IExpr> vertexSet = g.vertexSet();
					// int size = vertexSet.size();
					// IASTAppendable result = F.ListAlloc(size);
					// for (IExpr expr : vertexSet) {
					// result.append(expr);
					// }
					// return result;
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	private static class VertexQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				if (ast.isAST2()) {
					DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> dex = createGraph(ast.arg1());
					if (dex != null) {
						Graph<IExpr, IExprEdge> g = dex.toData();
						return F.bool(g.containsVertex(ast.arg2()));
					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	/**
	 * Create an internal DataExpr Graph.
	 * 
	 * @param arg1
	 * @return
	 */
	private static DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> createGraph(final IExpr arg1) {
		if (arg1.head().equals(F.Graph) && arg1 instanceof IDataExpr) {
			return (DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>>) arg1;
		}
		Graph<IExpr, IExprEdge> g;
		GraphType t = arg1.isListOfEdges();
		if (t != null) {
			if (t.isDirected()) {
				g = new DefaultDirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
			} else {
				g = new DefaultUndirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
			}
			IAST list = (IAST) arg1;
			for (int i = 1; i < list.size(); i++) {
				IAST edge = list.getAST(i);
				g.addVertex(edge.arg1());
				g.addVertex(edge.arg2());
				g.addEdge(edge.arg1(), edge.arg2());

			}
			return DataExpr.newInstance(F.Graph, g);
		}

		return null;
	}

	private static DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> createGraph(final IExpr vertices, final IExpr edges) {
		if (vertices.isList()) {
			Graph<IExpr, IExprEdge> g;
			GraphType t = edges.isListOfEdges();
			if (t != null) {
				if (t.isDirected()) {
					g = new DefaultDirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
				} else {
					g = new DefaultUndirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
				}
				// Graph<IExpr, IExprEdge> g = new DefaultDirectedGraph<IExpr, IExprEdge>(IExprEdge.class);
				IAST list = (IAST) vertices;
				for (int i = 1; i < vertices.size(); i++) {
					g.addVertex(list.get(i));
				}

				list = (IAST) edges;
				for (int i = 1; i < list.size(); i++) {
					IAST edge = list.getAST(i);
					g.addEdge(edge.arg1(), edge.arg2());
				}
				return DataExpr.newInstance(F.Graph, g);
			}
		}

		return null;
	}

	/**
	 * Create an eulerian cycle.
	 * 
	 * @param g
	 * @return <code>null</code> if no eulerian cycle can be created
	 */
	private static GraphPath<IExpr, IExprEdge> eulerianCycle(Graph<IExpr, IExprEdge> g) {
		EulerianCycleAlgorithm<IExpr, IExprEdge> eca = new HierholzerEulerianCycle<>();
		try {
			return eca.getEulerianCycle(g);
		} catch (IllegalArgumentException iae) {
			// Graph is not Eulerian
		}
		return null;
	}

	private static GraphPath<IExpr, IExprEdge> hamiltonianCycle(Graph<IExpr, IExprEdge> g) {
		HamiltonianCycleAlgorithm<IExpr, IExprEdge> eca = new HeldKarpTSP<>();
		try {
			return eca.getTour(g);
		} catch (IllegalArgumentException iae) {
			// Graph is not Hamiltonian
		}
		return null;
	}

	public static void initialize() {
		Initializer.init();
	}

	private GraphFunctions() {

	}

	/**
	 * Convert a graph into an IExpr object.
	 * 
	 * @param g
	 * @return
	 */
	public static IExpr graphToIExpr(AbstractBaseGraph<IExpr, IExprEdge> g) {
		IASTAppendable vertexes = vertexToIExpr(g);
		IASTAppendable edges = edgesToIExpr(g);
		IExpr graph = F.Graph(vertexes, edges);
		return graph;
	}

	private static IASTAppendable vertexToIExpr(Graph<IExpr, IExprEdge> g) {
		Set<IExpr> vertexSet = g.vertexSet();
		IASTAppendable vertexes = F.ListAlloc(vertexSet.size());
		for (IExpr expr : vertexSet) {
			vertexes.append(expr);
		}
		return vertexes;
	}

	private static IASTAppendable edgesToIExpr(Graph<IExpr, IExprEdge> g) {
		Set<IExprEdge> edgeSet = g.edgeSet();
		IASTAppendable edges = F.ListAlloc(edgeSet.size());
		GraphType type = g.getType();
		if (type.isDirected()) {
			for (IExprEdge edge : edgeSet) {
				edges.append(F.DirectedEdge(edge.lhs(), edge.rhs()));
			}
		} else {
			for (IExprEdge edge : edgeSet) {
				edges.append(F.UndirectedEdge(edge.lhs(), edge.rhs()));
			}
		}
		return edges;
	}

	public static IAST graphToAdjacencyMatrix(Graph<IExpr, IExprEdge> g) {
		Set<IExpr> vertexSet = g.vertexSet();
		int size = vertexSet.size();
		IExpr[] array = new IExpr[size];
		int indx = 0;
		for (IExpr expr : vertexSet) {
			array[indx++] = expr;
		}
		return F.matrix((i, j) -> g.containsEdge(array[i], array[j]) ? F.C1 : F.C0, size, size);
	}

}
