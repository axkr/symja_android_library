package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Graph layouts and named graphs. */
public class GraphLayoutTest extends AbstractTestCase {

  @Test
  public void testNamedGraphLayoutOptions() {
    check("ToString(Options(CompleteGraph(4)), InputForm)", //
        "{GraphLayout->\"StarEmbedding\"}");
    check("ToString(Options(CompleteGraph(7)), InputForm)", //
        "{GraphLayout->{\"CircularEmbedding\",\"OptimalOrder\"->False}}");
    check("ToString(Options(CompleteGraph({3,1})), InputForm)", //
        "{GraphLayout->{\"MultipartiteLayout\",\"VertexPartition\"->{3,1}}}");
    check("ToString(Options(CompleteGraph(6, GraphLayout -> \"SpringEmbedding\")), InputForm)", //
        "{GraphLayout->\"SpringEmbedding\"}");
    check("ToString(Options(GridGraph({4,2})), InputForm)", //
        "{GraphLayout->{\"GridEmbedding\",\"Dimension\"->{4,2}}}");
    check("ToString(Options(TuranGraph(8, 3)), InputForm)", //
        "{GraphLayout->{\"MultipartiteLayout\",\"VertexPartition\"->{3,3,2}}}");
    check("ToString(Options(CycleGraph(6)), InputForm)", //
        "{GraphLayout->\"CircularEmbedding\"}");
    check("ToString(Options(WheelGraph(6)), InputForm)", //
        "{GraphLayout->\"StarEmbedding\"}");
  }

  @Test
  public void testGeneratorsWithoutLayout() {
    // a graph without a layout of its own carries no GraphLayout option
    check("{Options(HypercubeGraph(2)), Options(PathGraph({a, b, c})), Options(ButterflyGraph(1))}", //
        "{{},{},{}}");
    check("ToString(Options(PathGraph({a, b}, GraphLayout -> \"SpiralEmbedding\")), InputForm)", //
        "{GraphLayout->\"SpiralEmbedding\"}");
    check("GraphEmbedding(PathGraph({x, y, z, w}))", //
        "{{0.0,0.0},{1.0,0.0},{2.0,0.0},{3.0,0.0}}");
    check("GraphEmbedding(Graph({p -> q, q -> r, r -> s}))", //
        "{{0.0,0.0},{1.0,0.0},{2.0,0.0},{3.0,0.0}}");
    check("Round(GraphEmbedding(CycleGraph(6)), 0.001)", //
        "{{-0.866,0.5},{0.0,1.0},{0.866,0.5},{0.866,-0.5},{0.0,-1.0},{-0.866,-0.5}}");
    check("Round(GraphEmbedding(CompleteGraph({3, 1})), 0.001)", //
        "{{0.0,-0.359},{0.0,0.0},{0.0,0.359},{1.0,0.0}}");
  }

  @Test
  public void testKnightTourGraph() {
    check("ToString(Options(KnightTourGraph(2, 5)), InputForm)", //
        "{GraphLayout->{\"VertexLayout\"->{\"GridEmbedding\",\"Dimension\"->{5,2}},"
            + "\"PackingLayout\"->None}}");
    check("EdgeList(KnightTourGraph(2, 4))", //
        "{1<->7,2<->8,3<->5,4<->6}");
    check("{VertexCount(KnightTourGraph(4, 4)), EdgeCount(KnightTourGraph(4, 4))}", //
        "{16,24}");
    // the centre of the 3x3 board is isolated
    check("VertexDegree(KnightTourGraph(3, 3), 5)", //
        "0");
    check("GraphEmbedding(KnightTourGraph(2, 3))", //
        "{{1.0,1.0},{1.0,2.0},{1.0,3.0},{2.0,1.0},{2.0,2.0},{2.0,3.0}}");
    check("KnightTourGraph(0, 3)", //
        "KnightTourGraph(0,3)");
  }

  @Test
  public void testPetersenGraphCoordinates() {
    check("c = VertexCoordinates /. Options(PetersenGraph(4, 1)); Round(c, 0.001)", //
        "{{1.0,0.0},{0.0,-1.0},{-1.0,0.0},{0.0,1.0},{2.0,0.0},{0.0,-2.0},{-2.0,0.0},{0.0,2.0}}");
    check("Round(GraphEmbedding(PetersenGraph())[[{3, 8}]], 0.001)", //
        "{{-0.588,-0.809},{-1.176,-1.618}}");
  }

  @Test
  public void testWeightedAdjacencyGraph() {
    check(
        "EdgeList(WeightedAdjacencyGraph({{Infinity, 4}, {7, Infinity}}, VertexLabels -> \"Name\"))", //
        "{1->2,2->1}");
    check(
        "g = WeightedAdjacencyGraph({p, q, r}, {{Infinity, 3, Infinity}, {3, Infinity, 1/2}, "
            + "{Infinity, 1/2, Infinity}}); {EdgeList(g), Normal(WeightedAdjacencyMatrix(g))}", //
        "{{p<->q,q<->r},{{0,3,0},{3,0,1/2},{0,1/2,0}}}");
    // zero is a weight; only Infinity means no edge
    check("EdgeList(WeightedAdjacencyGraph({{0, 6}, {6, Infinity}}))", //
        "{1<->1,1<->2}");
    check(
        "GraphDistance(WeightedAdjacencyGraph({{Infinity, 9, 1}, {9, Infinity, 1}, "
            + "{1, 1, Infinity}}), 1, 2)", //
        "2.0");
    check("EdgeList(WeightedAdjacencyGraph({{Infinity, Infinity}, {Infinity, Infinity}}))", //
        "{}");
    check("WeightedAdjacencyGraph({{1, 2, 3}})", //
        "WeightedAdjacencyGraph({{1,2,3}})");
    check("WeightedAdjacencyGraph({a}, {{Infinity, 1}, {1, Infinity}})", //
        "WeightedAdjacencyGraph({a},{{Infinity,1},{1,Infinity}})");
  }

  @Test
  public void testAnnotationValueEdgeWeight() {
    // the weights follow the order of EdgeList
    check(
        "g = WeightedAdjacencyGraph({{Infinity, 8, 0}, {8, Infinity, 5}, {0, 5, Infinity}}); "
            + "{EdgeList(g), AnnotationValue(g, EdgeWeight)}", //
        "{{1<->2,1<->3,2<->3},{8,0,5}}");
    check("AnnotationValue(Graph({u <-> v, v <-> w}, EdgeWeight -> {3/4, 11}), EdgeWeight)", //
        "{3/4,11}");
    // an undirected edge matches either way round
    check("AnnotationValue({Graph({u <-> v, v <-> w}, EdgeWeight -> {3/4, 11}), w <-> v}, "
        + "EdgeWeight)", //
        "11");
    check("AnnotationValue({Graph({u -> v}, EdgeWeight -> {6}), u -> v}, EdgeWeight)", //
        "6");
    // weights given per edge come back in edge order
    check("g = Graph({p -> q, q -> r, r -> p}, EdgeWeight -> {(r -> p) -> 4, (p -> q) -> 9, "
        + "(q -> r) -> 2}); {AnnotationValue(g, EdgeWeight), AnnotationValue({g, r -> p}, "
        + "EdgeWeight), GraphDistance(g, p, r)}", //
        "{{9,2,4},4,11.0}");
    // no such edge, no weights, or an annotation the whole graph doesn't have
    check("AnnotationValue({Graph({u <-> v}, EdgeWeight -> {2}), u <-> w}, EdgeWeight)", //
        "$Failed");
    check("AnnotationValue({Graph({u <-> v}), u <-> v}, EdgeWeight)", //
        "$Failed");
    check("AnnotationValue(Graph({u <-> v}), EdgeWeight)", //
        "Automatic");
    check("AnnotationValue(Graph({u <-> v}, VertexLabels -> {u -> \"top\"}), VertexLabels)", //
        "$Failed");
    check("AnnotationValue({Graph({u <-> v}, VertexLabels -> {u -> \"top\"}), u}, VertexLabels)", //
        "top");
  }

  @Test
  public void testAnnotationValueVertexWeight() {
    check("g = Graph({k -> m, m -> n}, VertexWeight -> {1/2, 6, 0}); "
        + "{AnnotationValue(g, VertexWeight), AnnotationValue({g, n}, VertexWeight)}", //
        "{{1/2,6,0},0}");
    check("AnnotationValue({Graph({k -> m}, VertexWeight -> {m -> 3}), m}, VertexWeight)", //
        "3");
    check("AnnotationValue({Graph({k -> m}), m}, VertexWeight)", //
        "$Failed");
    check("AnnotationValue(Graph({k -> m}), VertexWeight)", //
        "Automatic");
  }

  @Test
  public void testGraphEmbeddingNamedLayouts() {
    check("GraphEmbedding(GridGraph({3, 2}))", //
        "{{1.0,1.0},{1.0,2.0},{1.0,3.0},{2.0,1.0},{2.0,2.0},{2.0,3.0}}");
    check("Round(GraphEmbedding(StarGraph(4)), 0.001)", //
        "{{0.0,0.0},{0.866,-0.5},{0.0,1.0},{-0.866,-0.5}}");
    check("GraphEmbedding(Graph({1 <-> 2, 1 <-> 3}, GraphLayout -> \"StarEmbedding\"))", //
        "{{0.0,0.0},{1.0,0.0},{-1.0,0.0}}");
    check(
        "Round(GraphEmbedding(Graph({a <-> b, a <-> c, b <-> c}, "
            + "GraphLayout -> {\"StarEmbedding\", \"Center\" -> c})), 0.001)", //
        "{{1.0,0.0},{-1.0,0.0},{0.0,0.0}}");
    check("Round(GraphEmbedding(CompleteGraph({1, 2})), 0.001)", //
        "{{0.0,0.0},{1.0,-0.334},{1.0,0.334}}");
    // an odd cycle is not bipartite
    check(
        "Round(GraphEmbedding(Graph({1 <-> 2, 2 <-> 3, 3 <-> 1}, "
            + "GraphLayout -> \"BipartiteEmbedding\")), 0.001)", //
        "{{-0.866,-0.5},{0.0,1.0},{0.866,-0.5}}");
    check(
        "Round(GraphEmbedding(Graph({x <-> y, y <-> z, x <-> w}, "
            + "GraphLayout -> \"BipartiteEmbedding\")), 0.001)", //
        "{{0.0,-0.334},{1.336,-0.334},{0.0,0.334},{1.336,0.334}}");
    check(
        "Round(GraphEmbedding(Graph({1 <-> 2, 1 <-> 3, 1 <-> 4, 4 <-> 5}, "
            + "GraphLayout -> {\"RadialEmbedding\", \"RootVertex\" -> 1})), 0.001)", //
        "{{0.866,1.0},{0.0,1.5},{0.866,0.0},{1.732,1.5},{2.598,2.0}}");
    check(
        "e = GraphEmbedding(PathGraph(Range(9), GraphLayout -> \"SpiralEmbedding\")); "
            + "{e[[1, 2]], Max(Abs(Differences(e[[All, 2]], 2))) < 10^-12}", //
        "{0.0,True}");
  }

  @Test
  public void testGraphEmbeddingCircular() {
    // the first vertex at 90 degrees + 360/n, the order follows the edges
    check(
        "Round(Mod(ArcTan @@@ GraphEmbedding(Graph({1 <-> 3, 1 <-> 2}, "
            + "GraphLayout -> \"CircularEmbedding\"))/Degree, 360))", //
        "{210,90,330}");
    check(
        "Round(Mod(ArcTan @@@ GraphEmbedding(Graph({1 <-> 3, 1 <-> 2}, "
            + "GraphLayout -> {\"CircularEmbedding\", \"OptimalOrder\" -> False}))/Degree, 360))", //
        "{210,330,90}");
    check("Round(GraphEmbedding(CompleteGraph(4)), 0.001)", //
        "{{0.0,0.0},{0.866,-0.5},{0.0,1.0},{-0.866,-0.5}}");
  }

  @Test
  public void testGraphEmbeddingDefaultLayout() {
    check("GraphEmbedding(Graph({u -> v}))", //
        "{{1.0,0.0},{0.0,0.0}}");
    // a path on a line from the end which comes first in the vertex list
    check("GraphEmbedding(Graph({b <-> c, c <-> a, d <-> b}))", //
        "{{2.0,0.0},{1.0,0.0},{0.0,0.0},{3.0,0.0}}");
    // a tree from its centre 2, edges of mean length 1
    check("Round(GraphEmbedding(Graph({1 <-> 2, 2 <-> 3, 2 <-> 4, 4 <-> 5})), 0.001)", //
        "{{0.0,0.828},{0.828,1.657},{0.828,0.828},{1.657,0.828},{1.657,0.0}}");
    // a directed tree from the vertex no edge points to
    check("Round(GraphEmbedding(Graph({1 -> 2, 1 -> 3, 1 -> 4, 3 -> 5})), 0.001)", //
        "{{0.828,1.657},{0.0,0.828},{0.828,0.828},{1.657,0.828},{0.828,0.0}}");
    check("Round(GraphEmbedding(Graph({1 <-> 2, 2 <-> 3, 3 <-> 1})), 0.001)", //
        "{{-0.866,-0.5},{0.0,1.0},{0.866,-0.5}}");
  }

  @Test
  public void testGraphEmbeddingLayered() {
    check(
        "e = GraphEmbedding(Graph({1 <-> 2, 2 <-> 3, 2 <-> 4, 4 <-> 5, 4 <-> 6}, "
            + "GraphLayout -> {\"LayeredEmbedding\", \"RootVertex\" -> 2})); "
            + "{e[[2, 1]] == (e[[1, 1]] + e[[4, 1]])/2, e[[4, 1]] == (e[[5, 1]] + e[[6, 1]])/2}", //
        "{True,True}");
    check("GraphEmbedding(Graph({5 -> 6, 5 -> 7}, GraphLayout -> \"LayeredDigraphEmbedding\"))", //
        "{{0.0,1.0},{-0.5,0.0},{0.5,0.0}}");
    check(
        "GraphEmbedding(Graph({5 -> 6, 5 -> 7}, "
            + "GraphLayout -> {\"LayeredDigraphEmbedding\", \"Orientation\" -> Left}))", //
        "{{0.0,0.0},{1.0,0.5},{1.0,-0.5}}");
    // the layers close up to 1/GoldenRatio times the width
    check("e = GraphEmbedding(Graph({1 -> 2, 1 -> 3, 1 -> 4, 2 -> 5, 3 -> 5, 4 -> 5}, "
        + "GraphLayout -> \"LayeredDigraphEmbedding\")); "
        + "Round({Max(e[[All, 1]]) - Min(e[[All, 1]]), Max(e[[All, 2]]) - Min(e[[All, 2]])}, 0.001)", //
        "{2.0,1.236}");
  }
}
