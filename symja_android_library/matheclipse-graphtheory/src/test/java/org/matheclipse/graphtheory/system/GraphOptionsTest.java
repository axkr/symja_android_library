package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Tests for <code>Options</code> of a graph. */
public class GraphOptionsTest extends AbstractTestCase {

  @Test
  public void testSingleAnnotationValueIsListed() {
    // one value for all vertices or edges is listed as a one-element list
    check("Options(Graph({a -> b, b -> c}, EdgeLabels -> \"Name\", VertexShapeFunction -> "
        + "\"Square\"))", //
        "{EdgeLabels->{Name},VertexShapeFunction->{Square}}");
    check("Options(Graph({a -> b}, VertexLabelStyle -> Green, ImageSize -> 150), VertexLabelStyle)", //
        "{VertexLabelStyle->{RGBColor(0,1,0)}}");
    // the rules for single vertices or edges are kept
    check("Options(Graph({a -> b}, VertexSize -> {a -> 0.2}, EdgeStyle -> {a -> b -> Dashed}))", //
        "{EdgeStyle->{a->b->Dashing({Small,Small})},VertexSize->{a->0.2}}");
    // other options keep their value
    check("Options(Graph({a -> b}, ImageSize -> 150, GraphLayout -> \"CircularEmbedding\"))", //
        "{GraphLayout->CircularEmbedding,ImageSize->150}");
  }

  @Test
  public void testWeightsFollowTheOtherOptions() {
    check("Options(WeightedAdjacencyGraph({{Infinity, 3, 4}, {3, Infinity, Infinity}, "
        + "{4, Infinity, Infinity}}, VertexStyle -> Red, EdgeLabels -> \"EdgeWeight\"))", //
        "{EdgeLabels->{EdgeWeight},VertexStyle->{RGBColor(1,0,0)},EdgeWeight->{3,4}}");
    // the stored single value still draws the graph
    check("Head(GraphPlot(Graph({a -> b}, VertexStyle -> Orange)))", //
        "Graphics");
  }

  @Test
  public void testListedOptionsMakeTheSameGraph() {
    // the options as Options lists them give the graph back
    check("gr = Graph({p -> q, q -> r}, EdgeStyle -> Dashed, VertexLabels -> \"Name\", "
        + "VertexSize -> 0.25); Graph({p -> q, q -> r}, Options(gr)) === gr", //
        "True");
    // a highlight is added to the value for all vertices, not nested around it
    check("gr = Graph({p -> q}, VertexStyle -> Gray); "
        + "Options(HighlightGraph(Graph({p -> q}, Options(gr)), {q}), VertexStyle)", //
        "{VertexStyle->{GrayLevel(0.5),q->RGBColor(1.0,0.0,0.0)}}");
    // a one-element list of a rule is a rule for one vertex
    check("Options(Graph({p -> q}, VertexLabels -> {p -> \"x\"}))", //
        "{VertexLabels->{p->x}}");
  }
}
