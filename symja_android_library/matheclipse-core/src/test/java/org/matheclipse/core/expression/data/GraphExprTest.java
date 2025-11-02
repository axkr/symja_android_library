package org.matheclipse.core.expression.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

class GraphExprTest {

  @Test
  void testNewInstanceUndirectedUnweighted() {
    IAST vertices = F.List(S.a, S.b, S.c);
    IAST edges = F.List(F.UndirectedEdge(S.a, S.b), F.UndirectedEdge(S.b, S.c));
    IAST fullForm = F.Graph(vertices, edges);

    Graph<IExpr, ExprEdge> graph = GraphExpr.createGraph(fullForm);

    assertNotNull(graph);
    assertTrue(graph instanceof SimpleGraph);
    assertEquals(3, graph.vertexSet().size());
    assertEquals(2, graph.edgeSet().size());
    assertTrue(graph.containsVertex(S.a));
    assertTrue(graph.containsVertex(S.b));
    assertTrue(graph.containsVertex(S.c));
    assertTrue(graph.containsEdge(S.a, S.b));
    assertTrue(graph.containsEdge(S.b, S.c));
    assertFalse(graph.containsEdge(S.a, S.c));
  }

  @Test
  void testNewInstanceDirectedUnweighted() {
    IAST vertices = F.List(S.a, S.b, S.c);
    IAST edges = F.List(F.DirectedEdge(S.a, S.b), F.DirectedEdge(S.c, S.b));
    IAST fullForm = F.Graph(vertices, edges);

    Graph<IExpr, ExprEdge> graph = GraphExpr.createGraph(fullForm);

    assertNotNull(graph);
    assertTrue(graph instanceof DefaultDirectedGraph);
    assertEquals(3, graph.vertexSet().size());
    assertEquals(2, graph.edgeSet().size());
    assertTrue(graph.containsEdge(S.a, S.b));
    assertTrue(graph.containsEdge(S.c, S.b));
    assertFalse(graph.containsEdge(S.b, S.a));
  }

  @Test
  void testNewInstanceUndirectedWeighted() {
    IAST vertices = F.List(S.a, S.b, S.c);
    IAST edges = F.List(F.UndirectedEdge(S.a, S.b), F.UndirectedEdge(S.b, S.c));
    IAST weights = F.List(F.num(1.5), F.num(2.5));
    IAST options = F.List(F.Rule(S.EdgeWeight, weights));
    IAST fullForm = F.Graph(vertices, edges, options);

    Graph<IExpr, ExprWeightedEdge> graph = GraphExpr.createGraph(fullForm);

    assertNotNull(graph);
    assertTrue(graph instanceof DefaultUndirectedWeightedGraph);
    assertEquals(3, graph.vertexSet().size());
    assertEquals(2, graph.edgeSet().size());

    ExprWeightedEdge edgeAB = graph.getEdge(S.a, S.b);
    assertNotNull(edgeAB);
    assertEquals(1.5, graph.getEdgeWeight(edgeAB));

    ExprWeightedEdge edgeBC = graph.getEdge(S.b, S.c);
    assertNotNull(edgeBC);
    assertEquals(2.5, graph.getEdgeWeight(edgeBC));
  }

  @Test
  void testNewInstanceDirectedWeighted() {
    IAST vertices = F.List(S.a, S.b, S.c);
    IAST edges = F.List(F.DirectedEdge(S.a, S.b), F.DirectedEdge(S.c, S.a));
    IAST weights = F.List(F.num(10), F.num(20));
    IAST options = F.List(F.Rule(S.EdgeWeight, weights));
    IAST fullForm = F.Graph(vertices, edges, options);

    Graph<IExpr, ExprWeightedEdge> graph = GraphExpr.createGraph(fullForm);

    assertNotNull(graph);
    assertTrue(graph instanceof DefaultDirectedWeightedGraph);
    assertEquals(3, graph.vertexSet().size());
    assertEquals(2, graph.edgeSet().size());
    assertFalse(graph.containsEdge(S.b, S.a));

    ExprWeightedEdge edgeAB = graph.getEdge(S.a, S.b);
    assertNotNull(edgeAB);
    assertEquals(10.0, graph.getEdgeWeight(edgeAB));

    ExprWeightedEdge edgeCA = graph.getEdge(S.c, S.a);
    assertNotNull(edgeCA);
    assertEquals(20.0, graph.getEdgeWeight(edgeCA));
  }

  @Test
  void testNewInstanceEmptyGraph() {
    IAST vertices = F.List();
    IAST edges = F.List();
    IAST fullForm = F.Graph(vertices, edges);

    Graph<IExpr, ExprEdge> graph = GraphExpr.createGraph(fullForm);

    assertNotNull(graph);
    assertTrue(graph instanceof SimpleGraph);
    assertTrue(graph.vertexSet().isEmpty());
    assertTrue(graph.edgeSet().isEmpty());
  }
}
