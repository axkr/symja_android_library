package org.matheclipse.io.system;

/** Tests forSolve and Roots functions */
public class BioTest extends AbstractTestCase {

  public BioTest(String name) {
    super(name);
  }

  public void testBioSequence001() {
    check(
        "BioSequence(\"DNA\",\"ATAAACGTACGTTTTTAGGCT\")", //
        "BioSequence[Type: DNA Sequence, Content: ATAAAC-GCT (21 letters)]");
    check(
        "BioSequence(\"DNA\",\"GTAC\")", //
        "BioSequence[Type: DNA Sequence, Content: GTAC (4 letters)]");
  }

  public void testBioSequenceRNA() {
    check(
        "BioSequence(\"RNA\", \"AUAACGUAUGUA\")", //
        "BioSequence[Type: RNA Sequence, Content: AUAACG-GUA (12 letters)]");
  }

}
