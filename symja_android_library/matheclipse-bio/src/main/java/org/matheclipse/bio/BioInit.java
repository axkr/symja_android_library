package org.matheclipse.bio;

import java.io.OutputStream;
import org.matheclipse.bio.builtin.BioAlignmentFunctions;
import org.matheclipse.bio.builtin.BioPropertyFunctions;
import org.matheclipse.bio.builtin.BioSequenceFunctions;
import org.matheclipse.bio.io.BioSequenceImport;
import org.matheclipse.core.io.BioSequenceFormat;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Registers the bioinformatics functions of the <code>matheclipse-bio</code> module with the
 * evaluation engine. Call this after <code>F.initSymja()</code>;
 * <code>org.matheclipse.io.IOInit</code> already does so for the servlets and the consoles.
 */
public class BioInit {

  public static void init() {
    BioSequenceFunctions.initialize();
    BioAlignmentFunctions.initialize();
    BioPropertyFunctions.initialize();
    // invert the dependency for Export, which lives in matheclipse-core
    BioSequenceFormat.install(new BioSequenceFormat() {
      @Override
      public boolean exportFASTA(OutputStream out, IExpr expr) {
        return BioSequenceImport.exportFASTA(out, expr);
      }
    });
  }

  private BioInit() {}
}
