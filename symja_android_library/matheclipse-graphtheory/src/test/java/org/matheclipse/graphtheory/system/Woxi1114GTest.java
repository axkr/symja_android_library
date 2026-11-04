package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Graph cases of the Woxi round 2026-11-14 (area G). */
public class Woxi1114GTest extends AbstractTestCase {

  @Test
  public void testSubgraphOfRules() {
    check("VertexList(Subgraph({a -> b, b -> c, c -> d, d -> a}, {b, c, d}))", //
        "{b,c,d}");
    check("EdgeList(Subgraph({a -> b, b -> c, c -> d, d -> a}, {b, c, d}))", //
        "{b->c,c->d}");
  }
}
