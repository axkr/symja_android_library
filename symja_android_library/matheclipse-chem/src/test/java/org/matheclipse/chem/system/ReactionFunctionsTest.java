package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** Tiers C-E: structural editing, the molecular graph and reaction balancing. */
public class ReactionFunctionsTest extends AbstractTestCase {

  @Test
  public void testMoleculeModify() {
    check("MoleculeModify(\"LargestFragment\", Molecule(\"CCO.O\"))", //
        "Molecule(OCC)");
    check("MoleculeModify(\"RemoveHydrogens\", Molecule(\"CCO\"))", //
        "Molecule(OCC)");
  }

  @Test
  public void testUnknownModifyOperation() {
    check("MoleculeModify(\"NoSuchOp\", Molecule(\"CCO\"))", //
        "MoleculeModify(NoSuchOp,Molecule(OCC))");
  }

  @Test
  public void testMoleculeGraph() {
    // the skeleton as an ordinary Graph, so the graph-theory functions apply to it
    check("MoleculeGraph(Molecule(\"CCO\"))", //
        "Graph({1,2,3},{1<->2,2<->3})");
  }

  @Test
  public void testReactionBalancedQ() {
    check("ReactionBalancedQ({Molecule(\"O\")}, {Molecule(\"O\")})", //
        "True");
    // 1 H2 + 1 O2 -> 1 H2O does not balance
    check("ReactionBalancedQ({Molecule(\"[H][H]\"), Molecule(\"O=O\")}, {Molecule(\"O\")})", //
        "False");
  }

  @Test
  public void testReactionBalance() {
    // 2 H2 + 1 O2 -> 2 H2O
    check("ReactionBalance({Molecule(\"[H][H]\"), Molecule(\"O=O\")}, {Molecule(\"O\")})", //
        "{{2,1},{2}}");
  }
}
