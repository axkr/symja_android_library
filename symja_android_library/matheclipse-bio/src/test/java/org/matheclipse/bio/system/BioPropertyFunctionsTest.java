package org.matheclipse.bio.system;

import org.junit.jupiter.api.Test;

/**
 * Tier 3: peptide properties.
 *
 * <p>
 * The numeric expectations here are this implementation's output, i.e. BioJava's
 * <code>PeptideProperties</code> values. They are <em>not</em> pinned against
 * <code>ProteinData</code> is entity-backed and answers a much larger property list.
 */
public class BioPropertyFunctionsTest extends AbstractTestCase {

  private static final String PEPTIDE = "BioSequence(\"Peptide\", \"MTEYKLVVVGAGGVGKSALTIQ\")";

  @Test
  public void testSequenceAndLength() {
    check("ProteinData(" + PEPTIDE + ", \"Length\")", //
        "22");
    check("ProteinData(" + PEPTIDE + ", \"Sequence\")", //
        "MTEYKLVVVGAGGVGKSALTIQ");
    // a plain string is read as a peptide
    check("ProteinData(\"MW\", \"Length\")", //
        "2");
  }

  @Test
  public void testComputedProperties() {
    check("ProteinData(" + PEPTIDE + ", \"MolecularWeight\")", //
        "2221.64");
    check("ProteinData(" + PEPTIDE + ", \"IsoelectricPoint\")", //
        "8.25423");
    check("ProteinData(" + PEPTIDE + ", \"Aromaticity\")", //
        "0.0454545");
    check("ProteinData(" + PEPTIDE + ", \"AliphaticIndex\")", //
        "115.0");
    check("ProteinData(" + PEPTIDE + ", \"ExtinctionCoefficient\")", //
        "1490.0");
  }

  @Test
  public void testAminoAcidComposition() {
    // only the residues actually present are reported
    check("ProteinData(BioSequence(\"Peptide\", \"MW\"), \"AminoAcidComposition\")", //
        "{M->0.5,W->0.5}");
  }

  @Test
  public void testPropertyList() {
    check("ProteinData(" + PEPTIDE + ", \"Properties\")", //
        "{Absorbance,AliphaticIndex,AminoAcidComposition,Aromaticity,AverageHydropathy,"
            + "ExtinctionCoefficient,InstabilityIndex,IsoelectricPoint,Length,MolecularWeight,"
            + "NetCharge,Sequence}");
  }

  @Test
  public void testUnsupportedPropertyIsMissing() {
    check("ProteinData(" + PEPTIDE + ", \"MembraneBound\")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testNonPeptideArgument() {
    check("ProteinData(BioSequence(\"DNA\", \"ACGT\"), \"Length\")", //
        "ProteinData(BioSequence[Type: DNA, Content: ACGT (4 letters)],Length)");
  }
}
