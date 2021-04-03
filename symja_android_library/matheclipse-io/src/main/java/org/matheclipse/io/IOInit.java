package org.matheclipse.io;

import org.matheclipse.core.expression.S;
import org.matheclipse.io.builtin.DatasetFunctions;
import org.matheclipse.io.builtin.DynamicSwingFunctions;
import org.matheclipse.io.builtin.FileIOFunctions;
import org.matheclipse.io.builtin.SwingFunctions;

public class IOInit {
  public static void init() {
    S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    S.SemanticImport.setEvaluator(new org.matheclipse.io.builtin.SemanticImport());
    S.SemanticImportString.setEvaluator(new org.matheclipse.io.builtin.SemanticImportString());
    FileIOFunctions.initialize();
    DynamicSwingFunctions.initialize();
    SwingFunctions.initialize();
    DatasetFunctions.initialize();
  }
}
