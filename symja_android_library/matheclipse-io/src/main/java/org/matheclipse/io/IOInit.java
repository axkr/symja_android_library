package org.matheclipse.io;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.numbertheory.Primality;
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

  /**
   * Initialize an optional module that <code>matheclipse-io</code> doesn't depend on, so that its
   * init class can only be found at runtime: the static <code>init()</code> method of
   * <code>initClassName</code> is called if the module is on the classpath, and nothing happens if
   * it isn't. A deployment adds such a module by putting its jar on the classpath, so its absence
   * is the normal case and isn't reported.
   *
   * <p>
   * The class is looked up without being initialized: only a missing class means a missing module.
   * A module that is present but fails while its classes are initialized or its
   * <code>init()</code> runs is a real failure and propagates, as in
   * {@link #initOptional(String, Runnable)}.
   */
  private static void initOptionalByName(String initClassName) {
    final Class<?> initClass;
    try {
      initClass = Class.forName(initClassName, false, IOInit.class.getClassLoader());
    } catch (ClassNotFoundException missing) {
      return;
    }
    try {
      initClass.getMethod("init").invoke(null);
    } catch (java.lang.reflect.InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      if (cause instanceof Error) {
        throw (Error) cause;
      }
      throw new IllegalStateException(cause);
    } catch (ReflectiveOperationException roe) {
      throw new IllegalStateException(initClassName + ".init() can't be called", roe);
    }
  }

  public static void init() {
    // prime factorization of BigInteger numbers by matheclipse-core
    Config.PRIME_FACTORS = new Primality();

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
    // registers JSXGraph / ECharts / MathCell and installs the pages a JSFormData result is
    // shown in; without it those stay unevaluated and JSFormData is shown as text
    initOptional("matheclipse-jsgraphics",
        () -> org.matheclipse.jsgraphics.JSGraphicsInit.init());
    // installs the OR-Tools solvers the option Method -> "ORTools" of LinearOptimization, Solve,
    // FindShortestTour, ... uses and registers KnapsackSolve; matheclipse-io doesn't depend on
    // matheclipse-or-tools, so it is used only if a deployment puts it on the classpath
    initOptionalByName("org.matheclipse.ortools.OrToolsInit");

    S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    FileIOFunctions.initialize();
    DynamicSwingFunctions.initialize();
    SwingFunctions.initialize();

  }
}
