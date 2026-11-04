package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** The graph functions of the option sweep 5 which take a list of edges. */
public class Woxi1114FGraphTest extends AbstractTestCase {

  @Test
  public void testGlobalClusteringCoefficientOfEdgeList() {
    // a list of edges is the graph of the edges
    check("GlobalClusteringCoefficient({a <-> b, b <-> c, c <-> a, c <-> d})", //
        "3/5");
    check("GlobalClusteringCoefficient({a <-> b, b <-> c})", //
        "0");
    check("EdgeConnectivity({a <-> b, b <-> c, c <-> d, d <-> a})", //
        "2");
  }
}
