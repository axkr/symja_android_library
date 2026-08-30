package org.matheclipse.bio.expression.data;

/**
 * The biomolecular sequence types <code>BioSequence</code> understands.
 *
 * <p>
 * BioJava models the alphabet (DNA / RNA / amino acid) but has no notion of a <i>circular</i>
 * strand, so the circularity is carried here rather than in the wrapped
 * {@link org.biojava.nbio.core.sequence.template.Sequence}.
 */
public enum BioSequenceType {

  DNA("DNA", false), //
  RNA("RNA", false), //
  CIRCULAR_DNA("CircularDNA", true), //
  CIRCULAR_RNA("CircularRNA", true), //
  PEPTIDE("Peptide", false), //
  HYBRID_STRAND("HybridStrand", false);

  private final String fName;

  private final boolean fCircular;

  private BioSequenceType(String name, boolean circular) {
    fName = name;
    fCircular = circular;
  }

  /** The type string, for example <code>"CircularDNA"</code>. */
  public String getName() {
    return fName;
  }

  public boolean isCircular() {
    return fCircular;
  }

  public boolean isNucleotide() {
    return this != PEPTIDE;
  }

  /** The linear counterpart of this type; the identity for types which are already linear. */
  public BioSequenceType toLinear() {
    switch (this) {
      case CIRCULAR_DNA:
        return DNA;
      case CIRCULAR_RNA:
        return RNA;
      default:
        return this;
    }
  }

  /** The circular counterpart of this type, or <code>null</code> if there is none. */
  public BioSequenceType toCircular() {
    switch (this) {
      case DNA:
      case CIRCULAR_DNA:
        return CIRCULAR_DNA;
      case RNA:
      case CIRCULAR_RNA:
        return CIRCULAR_RNA;
      default:
        return null;
    }
  }

  /**
   * @param name a type string
   * @return the matching type or <code>null</code> if <code>name</code> is not one of them
   */
  public static BioSequenceType of(String name) {
    for (BioSequenceType type : values()) {
      if (type.fName.equals(name)) {
        return type;
      }
    }
    return null;
  }
}
