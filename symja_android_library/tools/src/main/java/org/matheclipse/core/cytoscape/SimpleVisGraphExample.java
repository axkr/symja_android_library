package org.matheclipse.core.cytoscape;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractBaseGraph;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IExprEdge;
import org.matheclipse.core.expression.IExprWeightedEdge;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * Graph visualization with <a href="https://visjs.org/">vis-network</a>
 *
 */
public class SimpleVisGraphExample {
	private final static String WEB_PAGE = //
			"<html>\n" + //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<head>\n" + //
					"  <title>VIS-Network</title>\n" + //
					"\n" + //
					"  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@5.0.0/dist/vis-network.min.js\"></script>\n"
					+ //
					"  <style type=\"text/css\">\n" + //
					"    #mynetwork {\n" + //
					"      width: 600px;\n" + //
					"      height: 400px;\n" + //
					"      border: 1px solid lightgray;\n" + //
					"    }\n" + //
					"  </style>\n" + //
					"</head>\n" + //
					"<body>\n" + //
					"\n" + //
					"<p>\n" + //
					"  Create a simple network with some nodes and edges.\n" + //
					"</p>\n" + //
					"\n" + //
					"<div id=\"vis\"></div>\n" + //
					"\n" + //
					"<script type=\"text/javascript\">\n" + //
					"`1`\n" + //
					"  // create a network\n" + //
					"  var container = document.getElementById('vis');\n" + //
					"  var data = {\n" + //
					"    nodes: nodes,\n" + //
					"    edges: edges\n" + //
					"  };\n" + //
					"  var options = {};\n" + //
					"  var network = new vis.Network(container, data, options);\n" + //
					"</script>\n" + //
					"\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//

	public static void main(String[] args) {
		try {
			Config.USE_MATHCELL = true;
			ExprEvaluator util = new ExprEvaluator();

			// IExpr result = util
			// .eval("Graph({a \\\\[UndirectedEdge] b, b \\\\[UndirectedEdge] c, c \\\\[UndirectedEdge] a})");
			// IExpr result = util.eval("Graph({1 \\\\[DirectedEdge] 2, 2 \\\\[DirectedEdge] 3, 3 \\\\[DirectedEdge]
			// 1})");
			// IExpr result = util
			// .eval("Graph({1 \\\\[UndirectedEdge] 2, 2 \\\\[UndirectedEdge] 3, 3 \\\\[UndirectedEdge] 1})");
			IExpr result = util.eval(
					"Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}, {EdgeWeight -> {2, 3, 4}})");
			if (result.head().equals(F.Graph) && result instanceof IDataExpr) {
				DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>> graph = (DataExpr<org.jgrapht.Graph<IExpr, IExprEdge>>) result;
				AbstractBaseGraph<IExpr, ?> g = (AbstractBaseGraph<IExpr, ?>) graph.toData();
				Map<IExpr, Integer> map = new HashMap<IExpr, Integer>();
				StringBuilder buf = new StringBuilder();
				if (g.getType().isWeighted()) {
					weightedGraphToIExpr(map, buf, (AbstractBaseGraph<IExpr, IExprWeightedEdge>) g);
				} else {
					graphToIExpr(map, buf, (AbstractBaseGraph<IExpr, IExprEdge>) g);
				}
				String manipulateStr = buf.toString();
				String js = WEB_PAGE;
				js = js.replaceAll("`1`", manipulateStr);
				System.out.println(js);
			}
		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}

	public static void graphToIExpr(Map<IExpr, Integer> map, StringBuilder buf, AbstractBaseGraph<IExpr, IExprEdge> g) {
		vertexToIExpr(map, buf, g);
		edgesToIExpr(map, buf, g);
	}

	public static void weightedGraphToIExpr(Map<IExpr, Integer> map, StringBuilder buf,
			AbstractBaseGraph<IExpr, IExprWeightedEdge> g) {
		vertexToIExpr(map, buf, g);
		weightedEdgesToIExpr(map, buf, g);
	}

	private static IASTAppendable vertexToIExpr(Map<IExpr, Integer> map, StringBuilder buf, Graph<IExpr, ?> g) {
		Set<IExpr> vertexSet = g.vertexSet();
		IASTAppendable vertexes = F.ListAlloc(vertexSet.size());
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
		return vertexes;
	}

	private static IASTAppendable edgesToIExpr(Map<IExpr, Integer> map, StringBuilder buf, Graph<IExpr, IExprEdge> g) {
		Set<IExprEdge> edgeSet = g.edgeSet();
		IASTAppendable edges = F.ListAlloc(edgeSet.size());
		GraphType type = g.getType();
		boolean first = true;
		if (type.isDirected()) {
			buf.append("var edges = new vis.DataSet([\n");
			for (IExprEdge edge : edgeSet) {
				if (first) {
					buf.append("  {from: ");
				} else {
					buf.append(", {from: ");
				}
				// {from: 1, to: 3},

				buf.append(map.get(edge.lhs()));
				buf.append(", to: ");
				buf.append(map.get(edge.rhs()));
				// , arrows: { to: { enabled: true, type: 'arrow'}}
				buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
				buf.append("}\n");
				first = false;
			}
			buf.append("]);\n");
		} else {
			//
			buf.append("var edges = new vis.DataSet([\n");
			for (IExprEdge edge : edgeSet) {
				if (first) {
					buf.append("  {from: ");
				} else {
					buf.append(", {from: ");
				}
				// {from: 1, to: 3},

				buf.append(map.get(edge.lhs()));
				buf.append(", to: ");
				buf.append(map.get(edge.rhs()));
				buf.append("}\n");
				first = false;
			}
			buf.append("]);\n");
		}
		return edges;
	}

	private static IASTAppendable[] weightedEdgesToIExpr(Map<IExpr, Integer> map, StringBuilder buf,
			Graph<IExpr, IExprWeightedEdge> graph) {

		Set<IExprWeightedEdge> edgeSet = (Set<IExprWeightedEdge>) graph.edgeSet();
		IASTAppendable edges = F.ListAlloc(edgeSet.size());
		IASTAppendable weights = F.ListAlloc(edgeSet.size());
		GraphType type = graph.getType();
		boolean first = true;
		if (type.isDirected()) {
			buf.append("var edges = new vis.DataSet([\n");
			for (IExprWeightedEdge edge : edgeSet) {
				if (first) {
					buf.append("  {from: ");
				} else {
					buf.append(", {from: ");
				}
				// {from: 1, to: 3},

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
			buf.append("]);\n");
		} else {
			buf.append("var edges = new vis.DataSet([\n");
			for (IExprWeightedEdge edge : edgeSet) {
				if (first) {
					buf.append("  {from: ");
				} else {
					buf.append(", {from: ");
				}
				// {from: 1, to: 3},

				buf.append(map.get(edge.lhs()));
				buf.append(", to: ");
				buf.append(map.get(edge.rhs()));
				buf.append(", label: '");
				buf.append(edge.weight());
				buf.append("'");
				buf.append("}\n");
				first = false;
			}
			buf.append("]);\n");
		}
		return new IASTAppendable[] { edges, weights };

	}
}
