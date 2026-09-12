package org.matheclipse.io;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.io.builtin.DynamicSwingFunctions;
import org.matheclipse.io.builtin.FileIOFunctions;
import org.matheclipse.io.builtin.SwingFunctions;

public class IOInit {

  /**
   * Initialize one optional module, tolerating its absence.
   *
   * <p>
   * The modules below are optional in the sense that a deployment may leave them out: a servlet
   * container answering HTTP has no use for Orekit's 17MB of ephemeris data, and excluding
   * <code>matheclipse-astro</code> from the war is the only way not to ship it. Naming those
   * modules directly here turned that exclusion into a <code>NoClassDefFoundError</code> at
   * startup, so the reference goes through a lambda that is only resolved when it runs, and a
   * missing module is reported rather than fatal.
   *
   * <p>
   * Only {@link LinkageError} is caught. A module that is present but throws while initializing is
   * a real failure and propagates.
   */
  private static void initOptional(String moduleName, Runnable init) {
    try {
      init.run();
    } catch (LinkageError missing) {
      // no logger here: IOInit runs before the servlets configure logging, and the consoles want
      // this on the terminal either way
      System.out.println("Symja module " + moduleName + " is not on the classpath - skipped");
    }
  }

  public static void init() {
    // set for only small prime factorization
    // Config.PRIME_FACTORS = new Primality();

    // set for BigInteger prime factorization
    Config.PRIME_FACTORS = new BigIntegerPrimality();

    // initialize the optional modules matheclipse-image, matheclipse-nlp,
    // matheclipse-astro, matheclipse-bio, matheclipse-chem, matheclipse-graphtheory,
    // matheclipse-compile and matheclipse-dataset:
    initOptional("matheclipse-image", () -> org.matheclipse.image.ImageInit.init());
    initOptional("matheclipse-nlp", () -> org.matheclipse.nlp.NLPInit.init());
    initOptional("matheclipse-astro", () -> org.matheclipse.astro.AstroInit.init());
    initOptional("matheclipse-bio", () -> org.matheclipse.bio.BioInit.init());
    initOptional("matheclipse-chem", () -> org.matheclipse.chem.ChemInit.init());
    initOptional("matheclipse-graphtheory",
        () -> org.matheclipse.graphtheory.GraphTheoryInit.init());
    // registers Dataset / SemanticImport / SemanticImportString,
    // which live on the vendored tech.tablesaw fork in matheclipse-dataset
    initOptional("matheclipse-dataset", () -> org.matheclipse.dataset.DatasetInit.init());
    // registers Compile / CompiledFunction / CompilePrint for both servlets and both
    // consoles, and installs the IExprCompiler core's numerical functions use
    initOptional("matheclipse-compile", () -> org.matheclipse.compile.CompileInit.init());

    S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    FileIOFunctions.initialize();
    DynamicSwingFunctions.initialize();
    SwingFunctions.initialize();

  }
}
