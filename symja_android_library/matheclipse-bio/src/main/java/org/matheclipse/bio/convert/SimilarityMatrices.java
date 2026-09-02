package org.matheclipse.bio.convert;

import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.Compound;
import org.biojava.nbio.core.sequence.template.CompoundSet;

/**
 * Wraps the substitution matrices shipped in <code>biojava-core</code> so they can be looked up by
 * the <code>SimilarityRules</code> name and scored by plain letters.
 */
public class SimilarityMatrices {

  /** A matrix looked up by letter rather than by BioJava compound. */
  public static class Lookup {

    private final SubstitutionMatrix<? extends Compound> fMatrix;

    private final CompoundSet<? extends Compound> fCompoundSet;

    Lookup(SubstitutionMatrix<? extends Compound> matrix,
        CompoundSet<? extends Compound> compoundSet) {
      fMatrix = matrix;
      fCompoundSet = compoundSet;
    }

    /**
     * @return the substitution score of the two letters, or <code>null</code> when either letter is
     *         not in this matrix's alphabet
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Integer score(String a, String b) {
      try {
        Compound first = fCompoundSet.getCompoundForString(a.toUpperCase());
        Compound second = fCompoundSet.getCompoundForString(b.toUpperCase());
        if (first == null || second == null) {
          return null;
        }
        return Integer.valueOf(((SubstitutionMatrix) fMatrix).getValue(first, second));
      } catch (RuntimeException e) {
        return null;
      }
    }
  }

  /**
   * Resolve a matrix name. Names are compared ignoring case and non-alphanumeric characters, so
   * <code>"NUC.4.4"</code>, <code>"NUC44"</code> and <code>"nuc_4_4"</code> all resolve.
   *
   * @return <code>null</code> if no matrix has that name
   */
  public static Lookup byName(String name) {
    String key = normalize(name);
    CompoundSet<AminoAcidCompound> aminoAcids = AminoAcidCompoundSet.getAminoAcidCompoundSet();
    CompoundSet<NucleotideCompound> nucleotides = DNACompoundSet.getDNACompoundSet();

    if ("BLOSUM30".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum30(), aminoAcids);
    }
    if ("BLOSUM35".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum35(), aminoAcids);
    }
    if ("BLOSUM40".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum40(), aminoAcids);
    }
    if ("BLOSUM45".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum45(), aminoAcids);
    }
    if ("BLOSUM50".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum50(), aminoAcids);
    }
    if ("BLOSUM55".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum55(), aminoAcids);
    }
    if ("BLOSUM60".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum60(), aminoAcids);
    }
    if ("BLOSUM62".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum62(), aminoAcids);
    }
    if ("BLOSUM65".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum65(), aminoAcids);
    }
    if ("BLOSUM70".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum70(), aminoAcids);
    }
    if ("BLOSUM75".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum75(), aminoAcids);
    }
    if ("BLOSUM80".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum80(), aminoAcids);
    }
    if ("BLOSUM85".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum85(), aminoAcids);
    }
    if ("BLOSUM90".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum90(), aminoAcids);
    }
    if ("BLOSUM100".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getBlosum100(), aminoAcids);
    }
    if ("PAM250".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getPAM250(), aminoAcids);
    }
    if ("GONNET250".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getGonnet250(), aminoAcids);
    }
    if ("IDENTITY".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getIdentity(), aminoAcids);
    }
    if ("NUC42".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getNuc4_2(), nucleotides);
    }
    if ("NUC44".equals(key)) {
      return new Lookup(SubstitutionMatrixHelper.getNuc4_4(), nucleotides);
    }
    return null;
  }

  /** The names {@link #byName(String)} accepts. */
  public static List<String> names() {
    List<String> names = new ArrayList<String>();
    names.add("BLOSUM30");
    names.add("BLOSUM35");
    names.add("BLOSUM40");
    names.add("BLOSUM45");
    names.add("BLOSUM50");
    names.add("BLOSUM55");
    names.add("BLOSUM60");
    names.add("BLOSUM62");
    names.add("BLOSUM65");
    names.add("BLOSUM70");
    names.add("BLOSUM75");
    names.add("BLOSUM80");
    names.add("BLOSUM85");
    names.add("BLOSUM90");
    names.add("BLOSUM100");
    names.add("PAM250");
    names.add("GONNET250");
    names.add("Identity");
    names.add("NUC.4.2");
    names.add("NUC.4.4");
    return names;
  }

  private static String normalize(String s) {
    StringBuilder buf = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (Character.isLetterOrDigit(ch)) {
        buf.append(Character.toUpperCase(ch));
      }
    }
    return buf.toString();
  }

  /** Unused today; documents that a peptide default of BLOSUM62 is what BioJava's aligners use. */
  static SubstitutionMatrix<AminoAcidCompound> defaultPeptideMatrix()
      throws CompoundNotFoundException {
    return SubstitutionMatrixHelper.getBlosum62();
  }

  private SimilarityMatrices() {}
}
