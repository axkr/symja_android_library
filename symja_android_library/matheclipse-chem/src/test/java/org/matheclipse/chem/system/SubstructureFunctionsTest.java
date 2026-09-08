package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** Tier B: substructure search and structural comparison. */
public class SubstructureFunctionsTest extends AbstractTestCase {

  @Test
  public void testMoleculeContainsQ() {
    // [OX2H] is the SMARTS for a hydroxyl oxygen
    check("MoleculeContainsQ(Molecule(\"CCO\"), MoleculePattern(\"[OX2H]\"))", //
        "True");
    check("MoleculeContainsQ(Molecule(\"CC\"), MoleculePattern(\"[OX2H]\"))", //
        "False");
  }

  @Test
  public void testMoleculeFreeQ() {
    check("MoleculeFreeQ(Molecule(\"CC\"), MoleculePattern(\"[OX2H]\"))", //
        "True");
    check("MoleculeFreeQ(Molecule(\"CCO\"), MoleculePattern(\"[OX2H]\"))", //
        "False");
  }

  @Test
  public void testMoleculeMatchQ() {
    check("MoleculeMatchQ(Molecule(\"c1ccccc1\"), MoleculePattern(\"c1ccccc1\"))", //
        "True");
  }

  @Test
  public void testSubstructureCount() {
    // six aromatic carbons in benzene
    check("MoleculeSubstructureCount(Molecule(\"c1ccccc1\"), MoleculePattern(\"c\"))", //
        "6");
    check("MoleculeSubstructureCount(Molecule(\"CCO\"), MoleculePattern(\"[OX2H]\"))", //
        "1");
  }

  @Test
  public void testFindMoleculeSubstructure() {
    // 1-based atom indices of each match
    check("FindMoleculeSubstructure(Molecule(\"CCO\"), MoleculePattern(\"[OX2H]\"))", //
        "{{3}}");
  }

  @Test
  public void testMoleculeEquivalentQ() {
    // compared by canonical SMILES, so the input spelling does not matter
    check("MoleculeEquivalentQ(Molecule(\"CCO\"), Molecule(\"OCC\"))", //
        "True");
    check("MoleculeEquivalentQ(Molecule(\"CCO\"), Molecule(\"CC\"))", //
        "False");
  }

  @Test
  public void testInvalidSmartsStaysUnevaluated() {
    check("MoleculeContainsQ(Molecule(\"CCO\"), MoleculePattern(\"[[[\"))", //
        "MoleculeContainsQ(Molecule(OCC),MoleculePattern([[[))");
  }
}
