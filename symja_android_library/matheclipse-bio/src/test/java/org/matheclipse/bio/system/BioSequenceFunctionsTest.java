package org.matheclipse.bio.system;

import org.junit.jupiter.api.Test;

/** Tier 1: construction and transformation of biomolecular sequences. */
public class BioSequenceFunctionsTest extends AbstractTestCase {

  @Test
  public void testBioSequenceTyped() {
    check("BioSequence(\"DNA\", \"ACGT\")", //
        "BioSequence[Type: DNA, Content: ACGT (4 letters)]");
    check("BioSequence(\"RNA\", \"ACGU\")", //
        "BioSequence[Type: RNA, Content: ACGU (4 letters)]");
    check("BioSequence(\"Peptide\", \"MSTA\")", //
        "BioSequence[Type: Peptide, Content: MSTA (4 letters)]");
  }

  @Test
  public void testBioSequenceInferred() {
    check("BioSequence(\"ACGT\")", //
        "BioSequence[Type: DNA, Content: ACGT (4 letters)]");
    check("BioSequence(\"ACGU\")", //
        "BioSequence[Type: RNA, Content: ACGU (4 letters)]");
  }

  @Test
  public void testBioSequenceUnknownType() {
    check("BioSequence(\"Protein\", \"MSTA\")", //
        "BioSequence(Protein,MSTA)");
  }

  @Test
  public void testBioSequenceQ() {
    check("BioSequenceQ(BioSequence(\"DNA\", \"ACGT\"))", //
        "True");
    check("BioSequenceQ(\"ACGT\")", //
        "False");
    check("BioSequenceQ(42)", //
        "False");
  }

  @Test
  public void testBioSequenceComplement() {
    check("BioSequenceComplement(BioSequence(\"DNA\", \"ACGT\"))", //
        "BioSequence[Type: DNA, Content: TGCA (4 letters)]");
    check("BioSequenceComplement(BioSequence(\"RNA\", \"ACGU\"))", //
        "BioSequence[Type: RNA, Content: UGCA (4 letters)]");
  }

  @Test
  public void testBioSequenceReverseComplement() {
    check("BioSequenceReverseComplement(BioSequence(\"DNA\", \"ACGT\"))", //
        "BioSequence[Type: DNA, Content: ACGT (4 letters)]");
    check("BioSequenceReverseComplement(BioSequence(\"DNA\", \"AAGT\"))", //
        "BioSequence[Type: DNA, Content: ACTT (4 letters)]");
  }

  @Test
  public void testBioSequenceTranscribe() {
    check("BioSequenceTranscribe(BioSequence(\"DNA\", \"ACGT\"))", //
        "BioSequence[Type: RNA, Content: ACGU (4 letters)]");
    // reverse transcription
    check("BioSequenceTranscribe(BioSequence(\"RNA\", \"ACGU\"))", //
        "BioSequence[Type: DNA, Content: ACGT (4 letters)]");
  }

  @Test
  public void testBioSequenceTranslate() {
    // ATG GCA -> Met Ala
    check("BioSequenceTranslate(BioSequence(\"DNA\", \"ATGGCA\"))", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
    check("BioSequenceTranslate(BioSequence(\"RNA\", \"AUGGCA\"))", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
  }

  @Test
  public void testBioSequenceTranslateTable() {
    // table 1 is the standard code, by number and by name
    check("BioSequenceTranslate(BioSequence(\"DNA\", \"ATGGCA\"), 1)", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
    // "Standard" is the NCBI name for the table BioJava ships as UNIVERSAL
    check("BioSequenceTranslate(BioSequence(\"DNA\", \"ATGGCA\"), \"Standard\")", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
    // BioJava's own spelling resolves too
    check("BioSequenceTranslate(BioSequence(\"DNA\", \"ATGGCA\"), \"VERTEBRATE_MITOCHONDRIAL\")", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
    // camel case resolves to the same table
    check("BioSequenceTranslate(BioSequence(\"DNA\", \"ATGGCA\"), \"VertebrateMitochondrial\")", //
        "BioSequence[Type: Peptide, Content: MA (2 letters)]");
  }

  @Test
  public void testBioSequenceInstances() {
    // R stands for A or G
    check("BioSequenceInstances(BioSequence(\"DNA\", \"AR\"))", //
        "{BioSequence[Type: DNA, Content: AA (2 letters)],BioSequence[Type: DNA, Content: AG (2 letters)]}");
    // an unambiguous sequence stands only for itself
    check("BioSequenceInstances(BioSequence(\"DNA\", \"AC\"))", //
        "{BioSequence[Type: DNA, Content: AC (2 letters)]}");
  }

  @Test
  public void testBioSequenceBackTranslateList() {
    // Met has exactly one codon, Trp has exactly one codon
    check("BioSequenceBackTranslateList(BioSequence(\"Peptide\", \"MW\"))", //
        "{BioSequence[Type: RNA, Content: AUGUGG (6 letters)]}");
  }

  @Test
  public void testBioSequenceModifyCircular() {
    check("BioSequenceModify(\"MakeCircular\", BioSequence(\"DNA\", \"ACGT\"))", //
        "BioSequence[Type: CircularDNA, Content: ACGT (4 letters)]");
    check("BioSequenceModify(\"MakeLinear\", BioSequence(\"CircularDNA\", \"ACGT\"))", //
        "BioSequence[Type: DNA, Content: ACGT (4 letters)]");
  }

  @Test
  public void testBioSequenceModifyDropToStartCodon() {
    check("BioSequenceModify(\"DropToStartCodon\", BioSequence(\"DNA\", \"CCATGGCA\"))", //
        "BioSequence[Type: DNA, Content: ATGGCA (6 letters)]");
  }
}
