package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class GraphUtil {

  /**
   * Computes the connected components of the given graph expression, optionally filtering them by a
   * pattern.
   * 
   * @param graphExpr
   * @param pattern {@link F#NIL} if no filtering is desired
   * @param engine
   * @return a list of connected components as GraphExpr instances
   */
  public static IAST connectedGraphComponents(GraphExpr graphExpr, IExpr pattern,
      EvalEngine engine) {
    Graph<IExpr, ? extends IExprEdge> jGraph =
        (Graph<IExpr, ? extends IExprEdge>) graphExpr.toData();
    List<Set<IExpr>> connectedSets;
  
    // 2. Compute Components using JGraphT
    if (jGraph.getType().isDirected()) {
      // For directed graphs, strongly connected components are computed.
      // Wolfram specifies: "given in an order such that there are no edges from ci to ci+1".
      // This implies a Reverse Topological Sort (Sink components first).
      // KosarajuStrongConnectivityInspector returns components in Topological Order (Source to
      // Sink).
      // Therefore, we reverse the list.
      KosarajuStrongConnectivityInspector<IExpr, ? extends IExprEdge> inspector =
          new KosarajuStrongConnectivityInspector<>(jGraph);
      connectedSets = new ArrayList<>(inspector.stronglyConnectedSets());
      Collections.reverse(connectedSets);
  
    } else {
      // For undirected graphs, vertices are in the same component if there is a path.
      ConnectivityInspector<IExpr, ? extends IExprEdge> inspector =
          new ConnectivityInspector<>(jGraph);
      connectedSets = new ArrayList<>(inspector.connectedSets());
      connectedSets.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed());
    }
  
    // Filter Components (if argument exists)
    if (pattern.isPresent()) {
      IExpr arg2 = pattern;
      List<Set<IExpr>> filteredSets = new ArrayList<>();
  
      if (arg2.isList()) {
        // Case: ConnectedGraphComponents(g, {v1, v2, ...})
        // Keep component if it contains any of the specified vertices.
        Set<IExpr> filterVertices = new HashSet<>();
        for (IExpr v : ((IAST) arg2)) {
          filterVertices.add(v);
        }
  
        for (Set<IExpr> component : connectedSets) {
          if (!Collections.disjoint(component, filterVertices)) {
            filteredSets.add(component);
          }
        }
      } else {
        // Case: ConnectedGraphComponents(g, patt)
        // Keep component if any vertex matches the pattern.
  
        IPatternMatcher patt = engine.evalPatternMatcher(arg2);
        for (Set<IExpr> component : connectedSets) {
          boolean matchFound = false;
          for (IExpr vertex : component) {
            if (patt.test(vertex, engine)) {
              matchFound = true;
              break;
            }
          }
          if (matchFound) {
            filteredSets.add(component);
          }
        }
      }
      connectedSets = filteredSets;
    }
  
    // Returns a list of components {c1, c2, ...}, where each component is a Graph.
    IASTAppendable resultList = F.ListAlloc(connectedSets.size());
    for (Set<IExpr> componentVertices : connectedSets) {
      // Extract the subgraph corresponding to the component vertices
      GraphExpr subgraph = GraphUtil.subgraph(jGraph, componentVertices);
      if (subgraph != null) {
        resultList.append(subgraph);
      }
    }
  
    return resultList;
  }

  private static GraphExpr subgraph(Graph<IExpr, ? extends IExprEdge> jGraph,
      Set<IExpr> componentVertices) {
    if (componentVertices.isEmpty()) {
      return null;
    }
  
    // 1. Create a new empty graph of the same type (directed/undirected/weighted/etc.)
    Graph<IExpr, ? extends IExprEdge> newGraph;
    GraphType t = jGraph.getType();
    if (t == null) {
      return null;
    }
    if (t.isDirected()) {
      newGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    } else {
      newGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    }
  
    for (IExpr vertex : componentVertices) {
      newGraph.addVertex(vertex);
    }
  
    for (IExprEdge edge : jGraph.edgeSet()) {
      IExpr source = edge.lhs();
      IExpr target = edge.rhs();
  
      if (componentVertices.contains(source) && componentVertices.contains(target)) {
        newGraph.addEdge(source, target);
      }
    }
  
    return GraphExpr.newInstance(newGraph);
  }

}
