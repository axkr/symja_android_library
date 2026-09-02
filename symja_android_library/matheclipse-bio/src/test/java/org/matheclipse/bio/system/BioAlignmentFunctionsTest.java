package org.matheclipse.bio.system;

import org.junit.jupiter.api.Test;

/**
 * Tier 2: pairwise alignment and similarity.
 *
 * <p>
 * The two cases — <code>SmithWatermanSimilarity["xxxxABCx", "yABCyyyy"]</code> and
 * <code>SequenceAlignment["BANANA", "ANANAS"]</code> — are asserted first.
 */
public class BioAlignmentFunctionsTest extends AbstractTestCase {

  @Test
  public void testDocumentedExamples() {
    check("SmithWatermanSimilarity(\"xxxxABCx\", \"yABCyyyy\")", //
        "3");
    check("SequenceAlignment(\"BANANA\", \"ANANAS\")", //
        "{{B,},ANANA,{,S}}");
  }

  @Test
  public void testSimilarityIdenticalAndDisjoint() {
    check("NeedlemanWunschSimilarity(\"abc\", \"abc\")", //
        "3");
    check("SmithWatermanSimilarity(\"abc\", \"abc\")", //
        "3");
    check("SmithWatermanSimilarity(\"abc\", \"xyz\")", //
        "0");
  }

  @Test
  public void testSimilarityOnLists() {
    check("SmithWatermanSimilarity({1, 2, 3, 4}, {1, 3, 4})", //
        "2");
    check("NeedlemanWunschSimilarity({1, 2, 3, 4}, {1, 3, 4})", //
        "3");
  }

  @Test
  public void testSequenceAlignmentOnLists() {
    check("SequenceAlignment({1, 2, 3, 4}, {1, 3, 4})", //
        "{{1},{{2},{}},{3,4}}");
  }

  @Test
  public void testMergeDifferences() {
    // adjacent differences collapse into one pair by default
    check("SequenceAlignment(\"aXYb\", \"aZWb\")", //
        "{a,{XY,ZW},b}");
    check("SequenceAlignment(\"aXYb\", \"aZWb\", MergeDifferences -> True)", //
        "{a,{XY,ZW},b}");
    // ... and stay one pair per position when merging is switched off
    check("SequenceAlignment(\"aXYb\", \"aZWb\", MergeDifferences -> False)", //
        "{a,{X,Z},{Y,W},b}");
  }

  @Test
  public void testIgnoreCase() {
    check("NeedlemanWunschSimilarity(\"ABC\", \"abc\")", //
        "0");
    check("NeedlemanWunschSimilarity(\"ABC\", \"abc\", IgnoreCase -> True)", //
        "3");
  }

  @Test
  public void testEmptyArgument() {
    check("SequenceAlignment(\"\", \"abc\")", //
        "{{,abc}}");
    check("SequenceAlignment(\"abc\", \"\")", //
        "{{abc,}}");
    check("NeedlemanWunschSimilarity(\"\", \"abc\")", //
        "0");
  }

  @Test
  public void testBioSequenceArguments() {
    check(
        "NeedlemanWunschSimilarity(BioSequence(\"Peptide\", \"MWA\"), "
            + "BioSequence(\"Peptide\", \"MWA\"))", //
        "3");
    check(
        "SmithWatermanSimilarity(BioSequence(\"DNA\", \"ACGT\"), "
            + "BioSequence(\"DNA\", \"ACGT\"))", //
        "4");
  }

  @Test
  public void testSimilarityRulesNamedMatrix() {
    // BLOSUM62 changes the scoring, but the reported value is still the match count
    check("NeedlemanWunschSimilarity(\"MWA\", \"MYA\", SimilarityRules -> \"BLOSUM62\")", //
        "2");
    // spelling of the matrix name is normalised
    check("NeedlemanWunschSimilarity(\"ACGT\", \"ACGT\", SimilarityRules -> \"NUC.4.4\")", //
        "4");
  }

  @Test
  public void testSimilarityRulesExplicit() {
    // an explicit rule list overrides the +1/-1 default; _ matches any element
    check("NeedlemanWunschSimilarity(\"ab\", \"ax\", SimilarityRules -> {{\"b\", \"x\"} -> 5})", //
        "1");
  }

  @Test
  public void testUnknownMatrixName() {
    check("NeedlemanWunschSimilarity(\"abc\", \"abc\", SimilarityRules -> \"NOSUCHMATRIX\")", //
        "NeedlemanWunschSimilarity(abc,abc,SimilarityRules->NOSUCHMATRIX)");
  }

  @Test
  public void testMethodOption() {
    // SequenceAlignment defaults to a global alignment; "Local" trims the flanks
    check("SequenceAlignment(\"xxABCxx\", \"yABCy\", Method -> \"Local\")", //
        "{ABC}");
  }

  @Test
  public void testGapPenalty() {
    check("SmithWatermanSimilarity(\"ACGT\", \"ACGT\", GapPenalty -> 2)", //
        "4");
    // a global alignment of different-length sequences must contain a gap, so a large
    // penalty cannot avoid one: A-GT/AGT still matches three elements
    check("NeedlemanWunschSimilarity(\"ACGT\", \"AGT\", GapPenalty -> 100)", //
        "3");
    // a large gap penalty does suppress the gaps a local alignment is free not to take
    check("SmithWatermanSimilarity(\"AxxxG\", \"AG\", GapPenalty -> 100)", //
        "1");
  }
}
