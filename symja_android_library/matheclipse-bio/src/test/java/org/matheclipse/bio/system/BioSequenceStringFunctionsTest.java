package org.matheclipse.bio.system;

import org.junit.jupiter.api.Test;

/** String functions on biomolecular sequences. */
public class BioSequenceStringFunctionsTest extends AbstractTestCase {

  @Test
  public void testBioSequenceCanonicalLetters() {
    check("BioSequence(\"RNA\", \"acgxu\")", //
        "BioSequence[Type: RNA, Content: ACGNU (5 letters)]");
    check("BioSequence(\"Peptide\", \"mst*\")", //
        "BioSequence[Type: Peptide, Content: MST. (4 letters)]");
    check("BioSequence(\"DNA\", 4)", //
        "BioSequence[Type: DNA, Content: NNNN (4 letters)]");
    check("BioSequence(\"Peptide\", 3)", //
        "BioSequence[Type: Peptide, Content: XXX (3 letters)]");
    check("BioSequence(\"gattaca\")", //
        "BioSequence[Type: DNA, Content: GATTACA (7 letters)]");
  }

  @Test
  public void testLinearPlainFunctions() {
    check("StringLength(BioSequence(\"DNA\", \"GATTACA\"))", //
        "7");
    check("StringTake(BioSequence(\"DNA\", \"GATTACA\"), {2, 4})", //
        "BioSequence[Type: DNA, Content: ATT (3 letters)]");
    check("StringDrop(BioSequence(\"DNA\", \"GATTACA\"), 3)", //
        "BioSequence[Type: DNA, Content: TACA (4 letters)]");
    check("StringReverse(BioSequence(\"RNA\", \"GAUC\"))", //
        "BioSequence[Type: RNA, Content: CUAG (4 letters)]");
    check("StringPadRight(BioSequence(\"RNA\", \"GAU\"), 5)", //
        "BioSequence[Type: RNA, Content: GAUNN (5 letters)]");
    check("StringInsert(BioSequence(\"DNA\", \"GACA\"), \"TT\", 3)", //
        "BioSequence[Type: DNA, Content: GATTCA (6 letters)]");
    check("StringRepeat(BioSequence(\"DNA\", \"AC\"), 2)", //
        "BioSequence[Type: DNA, Content: ACAC (4 letters)]");
    check("StringPart(BioSequence(\"DNA\", \"GATC\"), {1, 4})", //
        "{BioSequence[Type: DNA, Content: G (1 letters)],BioSequence[Type: DNA, Content: C (1 letters)]}");
  }

  @Test
  public void testDegenerateLettersInPatterns() {
    // R stands for A or G, Y for C or T
    check("StringCount(BioSequence(\"DNA\", \"GACGATGG\"), \"GR\")", //
        "3");
    check("StringCount(BioSequence(\"DNA\", \"GACGATGG\"), Verbatim(\"GR\"))", //
        "0");
    check("StringPosition(BioSequence(\"DNA\", \"TTCATG\"), \"YA\")", //
        "{{3,4}}");
    check("StringContainsQ(BioSequence(\"DNA\", \"AAGT\"), \"GW\")", //
        "True");
    check("StringFreeQ(BioSequence(\"DNA\", \"AAGT\"), \"G\" ~~ Verbatim(\"W\"))", //
        "True");
    check("StringStartsQ(BioSequence(\"DNA\", \"CAGT\"), \"M\")", //
        "True");
    check("StringEndsQ(BioSequence(\"DNA\", \"AACCGG\"), \"GR\")", //
        "True");
    check("StringReplace(BioSequence(\"DNA\", \"AACCGG\"), \"CY\" -> \"T\")", //
        "BioSequence[Type: DNA, Content: AATGG (5 letters)]");
    check("StringDelete(BioSequence(\"DNA\", \"ACGTACGT\"), \"CK\")", //
        "BioSequence[Type: DNA, Content: ATAT (4 letters)]");
    check("StringSplit(BioSequence(\"DNA\", \"AATTGCAAT\"), \"GS\")", //
        "{BioSequence[Type: DNA, Content: AATT (4 letters)],BioSequence[Type: DNA, Content: AAT (3 letters)]}");
    check("StringCases(BioSequence(\"RNA\", \"AUGCAUA\"), \"AY\")", //
        "{BioSequence[Type: RNA, Content: AU (2 letters)],BioSequence[Type: RNA, Content: AU (2 letters)]}");
    check("StringMatchQ(BioSequence(\"DNA\", \"ATG\"), \"A\" ~~ (\"K\"..))", //
        "True");
  }

  @Test
  public void testStringJoin() {
    check("StringJoin(BioSequence(\"RNA\", \"AC\"), BioSequence(\"RNA\", \"GU\"))", //
        "BioSequence[Type: RNA, Content: ACGU (4 letters)]");
    check("StringJoin(BioSequence(\"RNA\", \"AC\"), BioSequence(\"DNA\", \"GT\"))", //
        "$Failed");
  }

  @Test
  public void testCircularSequences() {
    check("StringLength(BioSequence(\"CircularDNA\", \"TACGA\"))", //
        "5");
    // AT wraps around from the last letter to the first
    check("StringPosition(BioSequence(\"CircularDNA\", \"TACGA\"), \"AT\")", //
        "{{5,1}}");
    check("StringCount(BioSequence(\"CircularDNA\", \"TACGA\"), \"AN\")", //
        "2");
    check("StringContainsQ(BioSequence(\"CircularDNA\", \"TACGA\"), \"GAT\")", //
        "True");
    check("StringFreeQ(BioSequence(\"DNA\", \"TACGA\"), \"GAT\")", //
        "True");
    check("StringMatchQ(BioSequence(\"CircularDNA\", \"CCAT\"), \"AT\" ~~ ___)", //
        "True");
    check("StringTake(BioSequence(\"CircularRNA\", \"ACGUU\"), {4, 1})", //
        "BioSequence[Type: CircularRNA, Content: UUA (3 letters)]");
    check("StringDrop(BioSequence(\"CircularRNA\", \"ACGUU\"), {4, 1})", //
        "BioSequence[Type: CircularRNA, Content: CG (2 letters)]");
    check("StringCases(BioSequence(\"CircularDNA\", \"GATTC\"), \"CG\")", //
        "{BioSequence[Type: CircularDNA, Content: CG (2 letters)]}");
  }
}
