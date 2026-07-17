package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ChemistryTest extends ExprEvaluatorTestCase {

  @Test
  public void testMolecule() {
    check("<|a->1,b->2|> // InputForm", "<|a->1,b->2|>");
    check("Molecule(\"C[C@H](Cc1c[nH]c2c1ccc(c2)O)N\") // InputForm",
        "Molecule({\"C\",Atom(\"C\",\"HydrogenCount\"->1),\"C\",\"C\",\"C\",Atom(\"N\"," //
            + "\"HydrogenCount\"->1),\"C\",\"C\",\"C\",\"C\",\"C\",\"C\",\"O\",\"N\"}," //
            + "{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),Bond({3,4},\"Single\"),Bond({4,5},\"Aromatic\")," //
            + "Bond({5,6},\"Aromatic\"),Bond({6,7},\"Aromatic\"),Bond({7,8},\"Aromatic\"),Bond({8,9},\"Aromatic\")," //
            + "Bond({9,10},\"Aromatic\"),Bond({10,11},\"Aromatic\"),Bond({11,12},\"Aromatic\"),Bond({11,13},\"Single\")," //
            + "Bond({2,14},\"Single\"),Bond({8,4},\"Aromatic\"),Bond({12,7},\"Aromatic\")}," //
            + "{StereochemistryElements->{<|\"StereoType\"->\"Tetrahedral\",\"ChiralCenter\"->2," //
            + "\"Direction\"->\"Counterclockwise\"|>}})");
  }

  @Test
  public void testMoleculeFromNames() {
    check("Molecule(\"water\") // InputForm",
        "Molecule({\"O\",\"H\",\"H\"},{Bond({1,2},\"Single\"),Bond({1,3},\"Single\")},{})");

    check("Molecule(\"methane\") // InputForm",
        "Molecule({\"C\",\"H\",\"H\",\"H\",\"H\"},{Bond({1,2},\"Single\"),Bond({1,3},\"Single\"),Bond({1,4},\"Single\"),Bond({1,5},\"Single\")},{})");
  }

  @Test
  public void testMoleculeFromSmiles() {
    // Ethanol: CCO
    check("Molecule(\"CCO\") // InputForm",
        "Molecule({\"C\",\"C\",\"O\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\")},{})");

    // Double bond: C=O (formaldehyde)
    check("Molecule(\"C=O\") // InputForm", "Molecule({\"C\",\"O\"},{Bond({1,2},\"Double\")},{})");

    // Triple bond: C#N
    check("Molecule(\"C#N\") // InputForm", "Molecule({\"C\",\"N\"},{Bond({1,2},\"Triple\")},{})");
  }

  @Test
  public void testMoleculeBranches() {
    // Isobutane: CC(C)C
    check("Molecule(\"CC(C)C\") // InputForm",
        "Molecule({\"C\",\"C\",\"C\",\"C\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),Bond({2,4},\"Single\")},{})");
  }

  @Test
  public void testMoleculeRings() {
    // Cyclopropane: C1CC1
    check("Molecule(\"C1CC1\") // InputForm",
        "Molecule({\"C\",\"C\",\"C\"},{Bond({1,2},\"Single\"),Bond({2,3},\"Single\"),Bond({3,1},\"Single\")},{})");
  }

  @Test
  public void testFormalCharges() {
    // Ammonium: [NH4+]
    check("Molecule(\"[NH4+]\") // InputForm",
        "Molecule({Atom(\"N\",\"FormalCharge\"->1,\"HydrogenCount\"->4)},{},{})");
  }


  @Test
  public void testInvalidInputStaysUnevaluated() {
    // Uninterpretable string
    check("Molecule(\"gibberish\") // InputForm", //
        "Molecule(\"gibberish\")");

    // Unclosed ring bond
    check("Molecule(\"C1CC\") // InputForm", //
        "Molecule(\"C1CC\")");
  }

}
