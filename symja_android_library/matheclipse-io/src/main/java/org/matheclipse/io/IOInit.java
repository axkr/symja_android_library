package org.matheclipse.io;

import org.matheclipse.astro.AstroInit;
import org.matheclipse.bio.BioInit;
import org.matheclipse.chem.ChemInit;
import org.matheclipse.compile.CompileInit;
import org.matheclipse.dataset.DatasetInit;
import org.matheclipse.graphtheory.GraphTheoryInit;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.image.ImageInit;
import org.matheclipse.io.builtin.DynamicSwingFunctions;
import org.matheclipse.io.builtin.FileIOFunctions;
import org.matheclipse.io.builtin.SwingFunctions;
import org.matheclipse.nlp.NLPInit;

public class IOInit {
  public static void init() {
    // set for only small prime factorization
    // Config.PRIME_FACTORS = new Primality();

    // set for BigInteger prime factorization
    Config.PRIME_FACTORS = new BigIntegerPrimality();

    // initialize the optional modules matheclipse-image, matheclipse-nlp,
    // matheclipse-astro, matheclipse-bio, matheclipse-chem, matheclipse-graphtheory,
    // matheclipse-compile and matheclipse-dataset:
    ImageInit.init();
    NLPInit.init();
    AstroInit.init();
    BioInit.init();
    ChemInit.init();
    GraphTheoryInit.init();
    // registers Dataset / SemanticImport / SemanticImportString,
    // which live on the vendored tech.tablesaw fork in matheclipse-dataset
    DatasetInit.init();
    // registers Compile / CompiledFunction / CompilePrint for both servlets and both
    // consoles, and installs the IExprCompiler core's numerical functions use
    CompileInit.init();

    S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    FileIOFunctions.initialize();
    DynamicSwingFunctions.initialize();
    SwingFunctions.initialize();

  }
}
