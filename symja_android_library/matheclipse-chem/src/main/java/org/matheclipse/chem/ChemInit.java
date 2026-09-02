package org.matheclipse.chem;

import org.matheclipse.chem.builtin.DepictionFunctions;
import org.matheclipse.chem.builtin.MoleculeFunctions;
import org.matheclipse.chem.builtin.PeriodicTableFunctions;
import org.matheclipse.chem.builtin.ReactionFunctions;
import org.matheclipse.chem.builtin.SubstructureFunctions;
import org.matheclipse.core.basic.ToggleFeature;

/**
 * Registers the chemistry functions of the <code>matheclipse-chem</code> module with the evaluation
 * engine. Call this after <code>F.initSymja()</code>; <code>org.matheclipse.io.IOInit</code>
 * already does so for the servlets and the consoles.
 *
 * <p>
 * <code>matheclipse-core</code> owns the <code>Molecule</code>, <code>MoleculeValue</code>,
 * <code>PeriodicTablePlot</code>, ... symbols but no longer implements them: without this module they stay unevaluated, exactly as
 * <code>SunPosition</code> does without <code>matheclipse-astro</code>.
 */
public class ChemInit {

  public static void init() {
    if (!ToggleFeature.CHEM) {
      return;
    }
    // before any CDK class initializes its logger, or it keeps the System.err fallback
    CdkLoggingBridge.install();
    MoleculeFunctions.initialize();
    SubstructureFunctions.initialize();
    DepictionFunctions.initialize();
    ReactionFunctions.initialize();
    PeriodicTableFunctions.initialize();
  }

  private ChemInit() {}
}
