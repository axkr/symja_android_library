package org.matheclipse.bio.expression.data;

import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.template.Compound;
import org.biojava.nbio.core.sequence.template.Sequence;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A biomolecular sequence: a BioJava {@link Sequence} plus the {@link BioSequenceType} tag and an
 * optional list of bonds.
 *
 * <p>
 * The type tag is not redundant with the wrapped sequence: BioJava has no circular strand, so
 * <code>"DNA"</code> and <code>"CircularDNA"</code> share one {@link DNASequence} representation
 * and are told apart only by {@link #getType()}.
 */
public class BioSequenceExpr extends DataExpr<Sequence<? extends Compound>> {

  private static final long serialVersionUID = -3361914928545221277L;

  /** never <code>null</code>. */
  private final BioSequenceType fType;

  /**
   * Bonds between positions of this sequence, as a list of <code>{i, j}</code> integer pairs, or
   * <code>null</code> when there are none; 1-based.
   */
  private final IAST fBonds;

  public static BioSequenceExpr newDNASequence(final String sequenceStr)
      throws CompoundNotFoundException {
    return new BioSequenceExpr(
        new DNASequence(sequenceStr, AmbiguityDNACompoundSet.getDNACompoundSet()),
        BioSequenceType.DNA, null);
  }

  public static BioSequenceExpr newRNASequence(final String sequenceStr)
      throws CompoundNotFoundException {
    return new BioSequenceExpr(
        new RNASequence(sequenceStr, AmbiguityRNACompoundSet.getRNACompoundSet()),
        BioSequenceType.RNA, null);
  }

  public static BioSequenceExpr newPeptideSequence(final String sequenceStr)
      throws CompoundNotFoundException {
    return new BioSequenceExpr(
        new ProteinSequence(sequenceStr, AminoAcidCompoundSet.getAminoAcidCompoundSet()),
        BioSequenceType.PEPTIDE, null);
  }

  /**
   * Build a sequence of the given type from its letters.
   *
   * @throws CompoundNotFoundException if a letter is not in the alphabet of <code>type</code>
   */
  public static BioSequenceExpr newSequence(final BioSequenceType type, final String sequenceStr)
      throws CompoundNotFoundException {
    switch (type) {
      case DNA:
      case CIRCULAR_DNA:
      case HYBRID_STRAND:
        return new BioSequenceExpr(
            new DNASequence(sequenceStr, AmbiguityDNACompoundSet.getDNACompoundSet()), type, null);
      case RNA:
      case CIRCULAR_RNA:
        return new BioSequenceExpr(
            new RNASequence(sequenceStr, AmbiguityRNACompoundSet.getRNACompoundSet()), type, null);
      case PEPTIDE:
        return new BioSequenceExpr(
            new ProteinSequence(sequenceStr, AminoAcidCompoundSet.getAminoAcidCompoundSet()), type,
            null);
      default:
        throw new IllegalArgumentException("unhandled BioSequenceType " + type);
    }
  }

  public static BioSequenceExpr newInstance(final Sequence<? extends Compound> sequence,
      final BioSequenceType type) {
    return new BioSequenceExpr(sequence, type, null);
  }

  public static BioSequenceExpr newInstance(final Sequence<? extends Compound> sequence,
      final BioSequenceType type, final IAST bonds) {
    return new BioSequenceExpr(sequence, type, bonds);
  }

  /**
   * Infer the type from the letters: the first alphabet which accepts every letter wins, trying
   * DNA, then RNA, then peptide. An empty string is DNA.
   *
   * @return <code>null</code> if no alphabet accepts the string
   */
  public static BioSequenceExpr inferSequence(final String sequenceStr) {
    BioSequenceType[] order =
        new BioSequenceType[] {BioSequenceType.DNA, BioSequenceType.RNA, BioSequenceType.PEPTIDE};
    for (int i = 0; i < order.length; i++) {
      try {
        return newSequence(order[i], sequenceStr);
      } catch (CompoundNotFoundException e) {
        // try the next alphabet
      }
    }
    return null;
  }

  protected BioSequenceExpr(final Sequence<? extends Compound> sequence, final BioSequenceType type,
      final IAST bonds) {
    super(S.BioSequence, sequence);
    fType = type;
    fBonds = bonds;
  }

  public BioSequenceType getType() {
    return fType;
  }

  /** The bond list, or {@link F#NIL} when this sequence carries no bonds. */
  public IExpr getBonds() {
    return fBonds == null ? F.NIL : fBonds;
  }

  /** A copy of this sequence carrying <code>type</code> and the same letters and bonds. */
  public BioSequenceExpr withType(BioSequenceType type) {
    return new BioSequenceExpr(fData, type, fBonds);
  }

  /** A copy of this sequence carrying <code>bonds</code>. */
  public BioSequenceExpr withBonds(IAST bonds) {
    return new BioSequenceExpr(fData, fType, bonds);
  }

  public String getSequenceAsString() {
    return fData.getSequenceAsString();
  }

  public int length() {
    return fData.getLength();
  }

  /** The letters of this sequence as a list of one-character strings. */
  public IAST toLetterList() {
    String str = getSequenceAsString();
    IASTAppendable list = F.ListAlloc(str.length());
    for (int i = 0; i < str.length(); i++) {
      list.append(F.stringx(str.substring(i, i + 1)));
    }
    return list;
  }

  /** The letters of this sequence as a plain {@link List} of one-character strings. */
  public List<String> toLetters() {
    String str = getSequenceAsString();
    List<String> letters = new ArrayList<String>(str.length());
    for (int i = 0; i < str.length(); i++) {
      letters.add(str.substring(i, i + 1));
    }
    return letters;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BioSequenceExpr) {
      BioSequenceExpr other = (BioSequenceExpr) obj;
      return fType == other.fType //
          && getSequenceAsString().equals(other.getSequenceAsString()) //
          && getBonds().equals(other.getBonds());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result = 523 + fType.hashCode();
    result = 31 * result + getSequenceAsString().hashCode();
    return 31 * result + getBonds().hashCode();
  }

  @Override
  public int hierarchy() {
    return BIOSEQUENCEID;
  }

  @Override
  public IExpr copy() {
    return new BioSequenceExpr(fData, fType, fBonds);
  }

  /**
   * <code>BioSequence["type", "letters"]</code> — the form which round-trips back through the
   * <code>BioSequence</code> evaluator.
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    if (fData == null) {
      return F.NIL;
    }
    return F.binaryAST2(S.BioSequence, F.stringx(fType.getName()),
        F.stringx(getSequenceAsString()));
  }

  @Override
  public String toString() {
    String str = getSequenceAsString();
    int length = str.length();
    if (str.length() > 9) {
      str = str.substring(0, 6) + "-" + str.substring(str.length() - 3);
    }
    return "BioSequence[" + "Type: " + fType.getName() + ", Content: " + str + " (" + length
        + " letters)]";
  }
}
