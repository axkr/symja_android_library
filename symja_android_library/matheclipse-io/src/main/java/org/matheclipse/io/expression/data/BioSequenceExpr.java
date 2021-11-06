package org.matheclipse.io.expression.data;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.sequence.template.Compound;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class BioSequenceExpr<T extends Compound> extends DataExpr<AbstractSequence<T>> {

  private static final long serialVersionUID = -3361914928545221277L;

  public static BioSequenceExpr newDNASequence(final String sequenceStr)
      throws CompoundNotFoundException {
    return new BioSequenceExpr(new DNASequence(sequenceStr));
  }

  public static BioSequenceExpr newRNASequence(final String sequenceStr)
      throws CompoundNotFoundException {
    return new BioSequenceExpr(new RNASequence(sequenceStr));
  }

  protected BioSequenceExpr(final AbstractSequence<T> sequence) {
    super(S.BioSequence, sequence);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BioSequenceExpr) {
      return fData.equals(((BioSequenceExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 523 : 523 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return BIOSEQUENCEID;
  }

  @Override
  public IExpr copy() {
    return new BioSequenceExpr(fData);
  }

  @Override
  public String toString() {
    String typeStr = "";
    if (fData instanceof DNASequence) {
      typeStr = "DNA Sequence";
    } else if (fData instanceof RNASequence) {
      typeStr = "RNA Sequence";
    }
    String str = fData.getSequenceAsString();
    int length = str.length();
    if (str.length() > 9) {
      str = str.substring(0, 6) + "-" + str.substring(str.length() - 3);
    }
    return "BioSequence[" + "Type: " + typeStr + ", Content: " + str + " (" + length + " letters)]";
  }
}
