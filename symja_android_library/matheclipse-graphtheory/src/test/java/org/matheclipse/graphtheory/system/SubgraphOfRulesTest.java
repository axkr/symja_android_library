package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Subgraph of a graph given by rules. */
public class SubgraphOfRulesTest extends AbstractTestCase {

  @Test
  public void testSubgraphOfRules() {
    check("VertexList(Subgraph({a -> b, b -> c, c -> d, d -> a}, {b, c, d}))", //
        "{b,c,d}");
    check("EdgeList(Subgraph({a -> b, b -> c, c -> d, d -> a}, {b, c, d}))", //
        "{b->c,c->d}");
  }
}
