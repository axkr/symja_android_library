package org.matheclipse.io;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.image.ImageInit;
import org.matheclipse.io.builtin.BioFunctions;
import org.matheclipse.io.builtin.DatasetFunctions;
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

    // initialize from module matheclipse-image:
    ImageInit.init();
    NLPInit.init();

    S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    S.SemanticImport.setEvaluator(new org.matheclipse.io.builtin.SemanticImport());
    S.SemanticImportString.setEvaluator(new org.matheclipse.io.builtin.SemanticImportString());
    FileIOFunctions.initialize();
    DynamicSwingFunctions.initialize();
    SwingFunctions.initialize();
    DatasetFunctions.initialize();
    BioFunctions.initialize();

  }
}
