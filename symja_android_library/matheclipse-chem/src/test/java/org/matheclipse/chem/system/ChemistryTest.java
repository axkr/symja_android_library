package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/**
 * The <code>Molecule</code> expression form.
 *
 * <p>
 * This suite moved here from <code>matheclipse-core</code> together with the implementation. The
 * <code>Molecule(atoms, bonds, options)</code> shape is unchanged, but two expectations differ from
 * the hand-rolled parser this replaced, and both differences are deliberate:
 *
 * <ul>
 * <li>the named molecules are stored with implicit hydrogens, like every other input, where the old
 * name table spelled them out as <code>O([H])[H]</code>. So <code>Molecule("water")</code> and
 * <code>Molecule("O")</code> are now one and the same object;
 * <li>ring-closure bonds are listed in ascending atom order.
 * </ul>
 */
public class ChemistryTest extends AbstractTestCase {

  @Test
  public void testMoleculeFromSmiles() {
    // ethanol
    check("Molecule(\"CCO\") // InputForm", //
        "Molecule({\"C\",\"C\",\"O\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\")},{})");

    // double bond: formaldehyde
    check("Molecule(\"C=O\") // InputForm", //
        "Molecule({\"C\",\"O\"},{Bond({1,2},\"Double\")},{})");

    // triple bond: hydrogen cyanide
    check("Molecule(\"C#N\") // InputForm", //
        "Molecule({\"C\",\"N\"},{Bond({1,2},\"Triple\")},{})");
  }

  @Test
  public void testMoleculeBranches() {
    // isobutane
    check("Molecule(\"CC(C)C\") // InputForm", //
        "Molecule({\"C\",\"C\",\"C\",\"C\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),"
            + "Bond({2,4},\"Single\")},{})");
  }

  @Test
  public void testMoleculeRings() {
    // cyclopropane; the ring-closure bond is listed as {1,3}
    check("Molecule(\"C1CC1\") // InputForm", //
        "Molecule({\"C\",\"C\",\"C\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),"
            + "Bond({1,3},\"Single\")},{})");
  }

  @Test
  public void testFormalCharges() {
    // ammonium
    check("Molecule(\"[NH4+]\") // InputForm", //
        "Molecule({Atom(\"N\",\"FormalCharge\"->1,\"HydrogenCount\"->4)},{},{})");
  }

  @Test
  public void testMoleculeFromNames() {
    // stored with implicit hydrogens, so identical to Molecule("O") and Molecule("C")
    check("Molecule(\"water\") // InputForm", //
        "Molecule({\"O\"},{},{})");
    check("Molecule(\"methane\") // InputForm", //
        "Molecule({\"C\"},{},{})");
    check("MoleculeEquivalentQ(Molecule(\"water\"), Molecule(\"O\"))", //
        "True");
  }

  @Test
  public void testAromaticityAndStereochemistry() {
    // aromatic bonds are reported as such, and a chiral centre is carried in the options
    check("Molecule(\"C[C@H](Cc1c[nH]c2c1ccc(c2)O)N\") // InputForm", //
        "Molecule({\"C\",\"C\",\"C\",\"C\",\"C\",\"N\",\"C\",\"C\",\"C\",\"C\",\"C\",\"C\","
            + "\"O\",\"N\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),Bond({3,4},\"Single\"),"
            + "Bond({4,5},\"Aromatic\"),Bond({5,6},\"Aromatic\"),Bond({6,7},\"Aromatic\"),"
            + "Bond({7,8},\"Aromatic\"),Bond({4,8},\"Aromatic\"),Bond({8,9},\"Aromatic\"),"
            + "Bond({9,10},\"Aromatic\"),Bond({10,11},\"Aromatic\"),Bond({11,12},\"Aromatic\"),"
            + "Bond({7,12},\"Aromatic\"),Bond({11,13},\"Single\"),Bond({2,14},\"Single\")},"
            + "{StereochemistryElements->{\"TetrahedralChirality\"}})");
  }

  @Test
  public void testInvalidInputStaysUnevaluated() {
    check("Molecule(\"gibberish\") // InputForm", //
        "Molecule(\"gibberish\")");

    // unclosed ring bond
    check("Molecule(\"C1CC\") // InputForm", //
        "Molecule(\"C1CC\")");
  }
}
