package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/** <code>ProteinData</code> over the bundled table of human actin-binding proteins. */
public class ProteinDataFunctionsTest extends AbstractTestCase {

  @Test
  public void testNamesAndSequences() {
    // the protein name is the recommended name of the UniProtKB entry
    check("ProteinData(\"GSN\", \"Name\")", //
        "Gelsolin");
    check("ProteinData(\"PFN1\", \"StandardName\")", //
        "PFN1");
    check("ProteinData(\"TMSB4X\", \"Sequence\")", //
        "MSDKPDMAEIEKFDKSKLKKTETQEKNPLPSKETIEQEKQAGES");
    check("StringTake(ProteinData(\"GSN\", \"Sequence\"), -10)", //
        "LDRAMAELAA");
    // a list of proteins is no entity
    check("ProteinData({\"GSN\", \"TMSB4X\", \"DSTN\"}, \"SequenceLength\")", //
        "ProteinData({GSN,TMSB4X,DSTN},SequenceLength)");
    check("Map(ProteinData(#, \"SequenceLength\")&, {\"GSN\", \"TMSB4X\"})", //
        "{Quantity(782,\"AminoAcids\"),Quantity(44,\"AminoAcids\")}");
    check("ProteinData(Entity(\"Protein\", \"DSTN\"), EntityProperty(\"Protein\", \"SequenceLength\"))", //
        "Quantity(165,\"AminoAcids\")");
    check("EntityValue(Entity(\"Protein\", \"PFN1\"), \"StandardName\")", //
        "PFN1");
  }

  @Test
  public void testGeneOntologyAnnotations() {
    check("ProteinData(\"TMSB4X\", \"CellularComponents\")", //
        "{Cytoplasm,Cytoskeleton,Cytosol,ExtracellularRegion,Nucleus,PlateletAlphaGranuleLumen}");
    // the term names line up with the class names
    check("ProteinData(\"DSTN\", \"MolecularFunctions\")", //
        "{ActinFilamentBinding,ActinFilamentSeveringActivity}");
    check("ProteinData(\"DSTN\", \"MolecularFunctions\", \"Name\")", //
        "{actin filament binding,actin filament severing activity}");
    // punctuation and digits: "phosphatidylinositol-4,5-bisphosphate binding"
    check("MemberQ(ProteinData(\"PFN1\", \"MolecularFunctions\"), "
        + "\"Phosphatidylinositol45BisphosphateBinding\")", //
        "True");
    check("And @@ Flatten(Table(MapThread(#1 == StringJoin(Capitalize /@ "
        + "StringSplit(#2, Except(WordCharacter))) &, "
        + "{ProteinData(p, q), ProteinData(p, q, \"Name\")}), {p, {\"GSN\", \"PFN1\", \"VIL1\"}}, "
        + "{q, {\"BiologicalProcesses\", \"CellularComponents\", \"MolecularFunctions\"}}))", //
        "True");
  }

  @Test
  public void testClasses() {
    check("MemberQ(ProteinData(\"Classes\"), \"ActinMonomerBinding\")", //
        "True");
    check("MemberQ(ProteinData(\"ActinMonomerBinding\"), \"PFN1\")", //
        "True");
    // a class holds the proteins annotated directly with its term
    check("AllTrue(ProteinData(\"ActinMonomerBinding\"), "
        + "MemberQ(ProteinData(#, \"MolecularFunctions\"), \"ActinMonomerBinding\") &)", //
        "True");
    // every annotation is a class and every class an annotation
    check("ProteinData(\"Classes\") === Union(Flatten(Table(ProteinData(p, q), "
        + "{p, ProteinData()}, "
        + "{q, {\"BiologicalProcesses\", \"CellularComponents\", \"MolecularFunctions\"}})))", //
        "True");
    check("With({l = ProteinData()}, {l === Sort(l), MemberQ(l, \"VIL1\")})", //
        "{True,True}");
    check("Length(EntityList(\"Protein\")) == Length(ProteinData())", //
        "True");
  }

  @Test
  public void testUnknownEntitiesAndProperties() {
    check("ProteinData(\"Properties\")", //
        "{BiologicalProcesses,CellularComponents,MolecularFunctions,Name,Sequence,SequenceLength,StandardName}");
    check("ProteinData(\"NoSuchGene\", \"Name\")", //
        "ProteinData(NoSuchGene,Name)");
    check("Check(ProteinData(\"NoSuchGene\"), \"failed\")", //
        "failed");
    check("ProteinData(\"VIL1\", \"MeltingPoint\")", //
        "Missing(NotAvailable)");
    check("ProteinData(\"VIL1\", \"Sequence\", \"Name\")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testAlignment() {
    check("Take(SequenceAlignment(ProteinData(\"DSTN\", \"Sequence\"), "
        + "ProteinData(\"PFN1\", \"Sequence\"), MergeDifferences -> False), 1)", //
        "{MA}");
    check("Take(SequenceAlignment(ProteinData(\"DSTN\", \"Sequence\"), "
        + "ProteinData(\"CFL2\", \"Sequence\"), MergeDifferences -> False), 4)", //
        "{MASGV,{Q,T},V,{A,N}}");
  }
}
