package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/** Graph cases of the Woxi round 2026-11-14 which were settled by a probe of the expected values. */
public class Woxi1114ProbeGraphTest extends AbstractTestCase {

  @Test
  public void testFindIndependentVertexSetAllOrder() {
    // all sets: largest first, and among the sets of one size the latest vertices first
    check("FindIndependentVertexSet(PathGraph({1,2,3,4,5}), Infinity, All)", //
        "{{1,3,5},{2,5},{2,4},{1,4}}");
    check("FindIndependentVertexSet(CycleGraph(8), {4}, All)", //
        "{{2,4,6,8},{1,3,5,7}}");
    // a single set is the one with the earliest vertices
    check("FindIndependentVertexSet(CycleGraph(8))", //
        "{{1,3,5,7}}");
  }
}
