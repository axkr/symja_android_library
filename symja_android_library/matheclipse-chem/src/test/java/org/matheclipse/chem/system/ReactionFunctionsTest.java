package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** Tiers C-E: structural editing, the molecular graph, isotope data and reaction balancing. */
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
  public void testIsotopeData() {
    check("IsotopeData(\"C\", \"MassNumber\")", //
        "12");
    check("IsotopeData(\"C\", \"AtomicMass\")", //
        "12.0");
    check("IsotopeData(\"H\", \"Abundance\")", //
        "99.9885");
    check("IsotopeData(\"Xx\")", //
        "Missing(NotAvailable)");
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

  @Test
  public void testIsotopeAbundances() {
    // every naturally occurring isotope, as massNumber -> percent; the other properties speak for
    // the most abundant isotope alone and so cannot answer this
    check("IsotopeData(\"C\", \"Abundances\")", //
        "{12->98.93,13->1.07}");
    check("IsotopeData(\"W\", \"Abundances\")", //
        "{180->0.12,182->26.5,183->14.31,184->30.64,186->28.43}");
  }

  @Test
  public void testElementDataReachesTheIsotopeTable() {
    // ElementData lives in matheclipse-core, which has neither CDK nor an isotope table; these
    // properties are answered through IsotopeData, so they work exactly when this module is loaded
    check("ElementData(\"Carbon\", \"KnownIsotopes\")", //
        "{8,9,10,11,12,13,14,15,16,17,18,19,20,21,22}");
    check("ElementData(\"Carbon\", \"IsotopeAbundances\")", //
        "{12->98.93,13->1.07}");
    // the nucleons of the most abundant isotope that are not protons
    check("ElementData(\"Carbon\", \"NeutronCount\")", //
        "6");
    check("ElementData(\"Tungsten\", \"NeutronCount\")", //
        "110");
    // an element with no isotope data says so
    check("ElementData(\"Og\", \"NeutronCount\")", //
        "Missing(NotAvailable)");
  }
}
