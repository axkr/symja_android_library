package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Graph functions of the Woxi round 2026-11-14 (area D). */
public class Woxi1114DGraphTest extends AbstractTestCase {

  @Test
  public void testGraphHub() {
    check("g = Graph({5 -> 6, 6 -> 7, 5 -> 7, 8 -> 7}); "
        + "{GraphHub(g), GraphHub(g, \"In\"), GraphHub(g, \"Out\")}", //
        "{{7},{7},{5}}");
    check("GraphHub(Graph({a <-> b, b <-> c, c <-> a, c <-> d}))", //
        "{c}");
    check("GraphHub({x -> y, y -> z})", //
        "{y}");
    check("GraphHub(Graph({1 -> 2}), \"Both\")", //
        "GraphHub(Graph({1,2},{1->2}),Both)");
  }

  @Test
  public void testIncidenceList() {
    check("IncidenceList({5 -> 7, 7 -> 8, 8 -> 5, 9 -> 8}, 8)", //
        "{7->8,8->5,9->8}");
    check("IncidenceList(Graph({a <-> b, b <-> c}), {a, c})", //
        "{a<->b,b<->c}");
    check("IncidenceList({1 -> 2}, 3)", //
        "IncidenceList({1->2},3)");
  }

  @Test
  public void testAdjacencyListBothDirections() {
    // a neighbour is joined by an edge in either direction
    check("AdjacencyList({4 -> 5, 6 -> 4, 5 -> 6, 7 -> 4}, 4)", //
        "{5,6,7}");
    check("AdjacencyList({4 -> 5, 6 -> 4})", //
        "{{5,6},{4},{4}}");
  }

  @Test
  public void testFindIndependentVertexSetMaximal() {
    // only maximal sets: every maximal set of a cycle of 8 vertices has at least 3 of them
    check("FindIndependentVertexSet(CycleGraph(8), 2)", //
        "{}");
    // the centre of a star alone, not a part of the set of its leaves
    check("FindIndependentVertexSet(StarGraph(5), 1)", //
        "{{1}}");
    check("FindIndependentVertexSet(CompleteGraph(3), {2}, All)", //
        "{}");
    check("FindIndependentVertexSet(StarGraph(4), {1}, All)", //
        "{{1}}");
  }

  @Test
  public void testRandomGraphDistributions() {
    check("Map(VertexCount, RandomGraph(BernoulliGraphDistribution(6, 0.5), {3, 2}), {2})", //
        "{{6,6},{6,6},{6,6}}");
    check("Length(RandomGraph({4, 3}, {1}))", //
        "1");
    // vertex v gets Min(k, v - 1) edges to earlier vertices
    check("g = RandomGraph(PriceGraphDistribution(9, 3, 2)); "
        + "{VertexCount(g), EdgeCount(g), DirectedGraphQ(g), SimpleGraphQ(g), "
        + "AllTrue(EdgeList(g), First(#) > Last(#) &)}", //
        "{9,21,True,True,True}");
    check("UndirectedGraphQ(RandomGraph("
        + "PriceGraphDistribution(6, 1, 1.0, DirectedEdges -> False)))", //
        "True");
    check("g = RandomGraph(DegreeGraphDistribution({2, 2, 2, 2, 2, 2})); "
        + "{VertexDegree(g), SimpleGraphQ(g)}", //
        "{{2,2,2,2,2,2},True}");
    // no simple graph has these degrees
    check("RandomGraph(DegreeGraphDistribution({3, 1, 1}))", //
        "RandomGraph(DegreeGraphDistribution({3,1,1}))");
    check("g = RandomGraph(SpatialGraphDistribution(7, 2.0)); {VertexCount(g), EdgeCount(g)}", //
        "{7,21}");
  }
}
