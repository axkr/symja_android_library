package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** An operator form <code>op(f)</code> applied to no subject or to several of them. */
public class OperatorFormArityTest extends ExprEvaluatorTestCase {

  @Test
  public void testSeveralSubjectsAreNoLevelSpecification() {
    // Map(g, {{u}, {v}}, 2) would map at level 2
    check("Map(g)[{{u}, {v}}, 2]", //
        "Map(g)[{{u},{v}},2]");
    check("Check(Map(g)[{{u}, {v}}, 2], failed, Map::argx)", //
        "failed");
    check("Check(Scan(g)[], failed, Scan::argx)", //
        "failed");
    check("Check(MapIndexed(g)[{u}, {v}], failed, MapIndexed::argx)", //
        "failed");
    check("Check(Apply(g)[h(u), 1], failed, Apply::argx)", //
        "failed");
    check("Check(AssociationMap(g)[], failed, AssociationMap::argx)", //
        "failed");
    check("Check(Select(OddQ)[{1, 2}, 1], failed, Select::argx)", //
        "failed");
  }

  @Test
  public void testOneSubject() {
    check("Map(g)[{u, v}]", //
        "{g(u),g(v)}");
    check("Apply(g)[h(u, v)]", //
        "g(u,v)");
    check("MapThread(g)[{{u, v}, {w, z}}]", //
        "{g(u,w),g(v,z)}");
    check("KeyValueMap(g)[<|u -> 1|>]", //
        "{g(u,1)}");
  }

  @Test
  public void testFoldTakesAStartValueAsSubject() {
    check("Fold(g)[s, {u, v}]", //
        "g(g(s,u),v)");
    check("FoldList(g)[s, {u}]", //
        "{s,g(s,u)}");
    check("Fold(g)[{u, v, w}]", //
        "g(g(u,v),w)");
    check("Check(Fold(g)[], failed, Fold::argt)", //
        "failed");
    check("Check(Fold(g)[s, {u}, w], failed, Fold::argt)", //
        "failed");
  }
}
