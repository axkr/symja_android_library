package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** Tier A: constructing molecules and reading their properties. */
public class MoleculeFunctionsTest extends AbstractTestCase {

  @Test
  public void testMoleculeFromSMILES() {
    // a molecule prints as its canonical SMILES, so CCO and OCC are one object
    check("Molecule(\"CCO\")", //
        "Molecule(OCC)");
    check("Molecule(\"OCC\")", //
        "Molecule(OCC)");
  }

  @Test
  public void testMoleculeFromName() {
    check("Molecule(\"water\")", //
        "Molecule(O)");
    check("Molecule(\"benzene\")", //
        "Molecule(c1ccccc1)");
  }

  @Test
  public void testMoleculeFromAtomsAndBonds() {
    check("Molecule({\"C\", \"O\"}, {Bond({1, 2}, \"Single\")})", //
        "Molecule(OC)");
  }

  @Test
  public void testInvalidMoleculeStaysUnevaluated() {
    check("Molecule(\"not-a-molecule***\")", //
        "Molecule(not-a-molecule***)");
  }

  @Test
  public void testMoleculeQ() {
    check("MoleculeQ(Molecule(\"CCO\"))", //
        "True");
    check("MoleculeQ(\"CCO\")", //
        "False");
  }

  @Test
  public void testChemicalFormula() {
    // implicit hydrogens are counted
    check("ChemicalFormula(Molecule(\"CCO\"))", //
        "C2H6O");
    check("ChemicalFormula(Molecule(\"c1ccccc1\"))", //
        "C6H6");
    check("ChemicalFormula(Molecule(\"water\"))", //
        "H2O");
  }

  @Test
  public void testAtomAndBondCount() {
    // ethanol: 3 heavy atoms + 6 hydrogens
    check("AtomCount(Molecule(\"CCO\"))", //
        "9");
    check("BondCount(Molecule(\"CCO\"))", //
        "8");
    check("MoleculeValue(Molecule(\"CCO\"), \"HeavyAtomCount\")", //
        "3");
  }

  @Test
  public void testAtomAndBondList() {
    check("AtomList(Molecule(\"O\"))", //
        "{O,H,H}");
    check("BondList(Molecule(\"O\"))", //
        "{Bond({1,2},Single),Bond({1,3},Single)}");
  }

  @Test
  public void testMoleculeValue() {
    check("MoleculeValue(Molecule(\"CCO\"), \"MolecularMass\")", //
        "46.06852");
    check("MoleculeValue(Molecule(\"CCO\"), {\"AtomCount\", \"MolecularFormula\"})", //
        "{9,C2H6O}");
    check("MoleculeValue(Molecule(\"CCO\"), \"NoSuchProperty\")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testChemicalConvert() {
    check("ChemicalConvert(Molecule(\"OCC\"), \"SMILES\")", //
        "OCC");
    check("ChemicalConvert(Molecule(\"CCO\"), \"MolecularFormula\")", //
        "C2H6O");
  }

  @Test
  public void testMoleculeName() {
    check("MoleculeName(Molecule(\"CCO\"))", //
        "ethanol");
    // CDK ships no name resolver, so anything outside the small built-in table is missing
    check("MoleculeName(Molecule(\"CCCCCCC\"))", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testConnectivity() {
    check("ConnectedMoleculeQ(Molecule(\"CCO\"))", //
        "True");
    check("ConnectedMoleculeQ(Molecule(\"CCO.O\"))", //
        "False");
    check("ConnectedMoleculeComponents(Molecule(\"CCO.O\"))", //
        "{Molecule(OCC),Molecule(O)}");
  }
}
