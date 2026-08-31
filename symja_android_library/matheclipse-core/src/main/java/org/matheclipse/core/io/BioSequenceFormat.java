package org.matheclipse.core.io;

import java.io.OutputStream;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The <code>FASTA</code> and <code>GENBANK</code> writers used by <code>Export</code>.
 *
 * <p>
 * <code>Export</code> lives in <code>matheclipse-core</code>, which must not depend on
 * <code>matheclipse-bio</code>, so the dependency is inverted: <code>BioInit.init()</code> installs
 * an implementation here and core calls it through this interface. Exactly the pattern
 * <code>IOInit</code> already uses for <code>Config.PRIME_FACTORS</code>.
 *
 * <p>
 * While no implementation is installed, exporting those formats simply fails, the same as any other
 * unsupported format.
 */
public interface BioSequenceFormat {

  /** The installed writer, or <code>null</code> when <code>matheclipse-bio</code> is not present. */
  BioSequenceFormat[] INSTANCE = new BioSequenceFormat[1];

  /**
   * Write <code>expr</code> — a biomolecular sequence or a list of them — in FASTA format.
   *
   * @return <code>false</code> if <code>expr</code> is not a sequence, or writing failed
   */
  boolean exportFASTA(OutputStream out, IExpr expr);

  /** Install the writer. Called from <code>org.matheclipse.bio.BioInit</code>. */
  static void install(BioSequenceFormat format) {
    INSTANCE[0] = format;
  }

  /** @return the installed writer, or <code>null</code> */
  static BioSequenceFormat get() {
    return INSTANCE[0];
  }
}
