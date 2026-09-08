package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/**
 * Graph assertions which used to live in <code>matheclipse-core</code>'s
 * <code>LowercaseTestCase</code>, moved here with the functions they exercise.
 */
public class VertexFunctionsTest extends AbstractTestCase {

  @Test
  public void testVertexList() {
    check("VertexList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
        "{1,2,3,4}");
    check(
        "VertexList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
        "{1,2,3}");
    check("VertexList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        "{1,2,3}");
  }

  @Test
  public void testVertexQ() {
    check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),3)", //
        "True");
    check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),5)", //
        "False");
  }

  @Test
  public void testVertexEccentricity() {
    check("VertexEccentricity({1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4, 4 -> 5, 5 -> 3}, 1)", //
        "4");

    check(
        "VertexEccentricity(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
            + "{EdgeWeight->{1.6,1.4,0.62,1.9,2.1}}), 4)", //
        "2.22");
    check(
        "VertexEccentricity({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, 4)", //
        "2");
  }

}
