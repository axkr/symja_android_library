package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** <code>ChemicalData</code> over the bundled table of PubChem compounds. */
public class ChemicalDataFunctionsTest extends AbstractTestCase {

  @Test
  public void testNames() {
    check("ChemicalData(\"Ethanol\", \"Name\")", //
        "ethanol");
    check("ChemicalData(\"Benzene\", \"IUPACName\")", //
        "benzene");
    check("ChemicalData(\"Water\", \"MolecularFormulaString\")", //
        "H2O");
    check("ChemicalData(Entity(\"Chemical\", \"Ethanol\"), \"SMILES\")", //
        "CCO");
    check("ChemicalData(\"Ethanol\", EntityProperty(\"Chemical\", \"PubChemCompoundID\"))", //
        "702");
    check("ChemicalData(\"Benzene\", \"CASNumber\")", //
        "CAS71-43-2");
  }

  @Test
  public void testFormula() {
    // Hill order: carbon, hydrogen, then the rest alphabetically
    check("ChemicalData(\"Ethanol\", \"ElementTally\")", //
        "{{C,2},{H,6},{O,1}}");
    // without carbon everything is alphabetical
    check("ChemicalData(\"Water\", \"ElementTally\")", //
        "{{H,2},{O,1}}");
    // an element that occurs once is written without a subscript
    check("ChemicalData(\"Ethanol\", \"CompoundFormulaDisplay\") === "
        + "Style(Row({Subscript(\"C\", 2), Subscript(\"H\", 6), \"O\"}),"
        + " ScriptBaselineShifts -> {0.5, 0.75})", //
        "True");
    // from the standard atomic weights, to the precision their digits allow:
    // 6 * 12.011 + 6 * 1.008, uncertain by 6 * 12.011 * 10^-5 + 6 * 1.008 * 10^-4
    check("ChemicalData(\"Benzene\", \"MolecularMass\") == Quantity(78.114, \"AtomicMassUnit\")", //
        "True");
    check("Round(Precision(QuantityMagnitude(ChemicalData(\"Benzene\", \"MolecularMass\"))),"
        + " 0.0001)", //
        "4.7704");
  }

  /** The structure is PubChem's record: its atom order, its bonds and its depiction. */
  @Test
  public void testStructure() {
    check("ChemicalData(\"Ethanol\", \"VertexTypes\")", //
        "{O,C,C,H,H,H,H,H,H}");
    check("ChemicalData(\"Ethanol\", \"EdgeRules\")", //
        "{1->2,1->9,2->3,2->4,2->5,3->6,3->7,3->8}");
    // the depiction in hundredths of a bond length
    check("ChemicalData(\"Ethanol\", \"VertexCoordinates\")[[1;;2]]", //
        "{{373.21,25.0},{286.6,-25.0}}");
    // Kekule form: an aromatic ring alternates single and double bonds
    check("Counts(ChemicalData(\"Benzene\", \"EdgeTypes\"))", //
        "<|Double->3,Single->9|>");
    check("Length(ChemicalData(\"Benzene\", \"EdgeRules\")) == "
        + "Length(ChemicalData(\"Benzene\", \"EdgeTypes\"))", //
        "True");
  }

  @Test
  public void testDrawings() {
    // one sphere for every atom, hydrogens included
    check("Length(Cases(ChemicalData(\"Ethanol\", \"MoleculePlot\"), _Sphere, Infinity))", //
        "9");
    check("Length(Cases(ChemicalData(\"Benzene\", \"SpaceFillingMoleculePlot\"), _Sphere,"
        + " Infinity))", //
        "12");
    check("Cases(ChemicalData(\"Benzene\", \"SpaceFillingMoleculePlot\"), _Cylinder, Infinity)", //
        "{}");
    check("Head(ChemicalData(\"Ethanol\", \"StructureDiagram\"))", //
        "Graphics");
    check("Head(ChemicalData(\"Benzene\"))", //
        "Graphics");
  }

  @Test
  public void testUnknown() {
    // a compound the table does not hold stays as it is
    check("ChemicalData(\"Unobtainium\", \"Name\")", //
        "ChemicalData(Unobtainium,Name)");
    // a property it does not know is reported, and the call stays as it is
    check("ChemicalData(\"Benzene\", \"Flavour\")", //
        "ChemicalData(Benzene,Flavour)");
    // the charge of a compound is not one of its properties
    check("ChemicalData(\"Benzene\", \"FormalCharge\")", //
        "ChemicalData(Benzene,FormalCharge)");
  }
}
