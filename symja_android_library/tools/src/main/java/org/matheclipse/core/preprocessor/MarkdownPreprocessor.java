package org.matheclipse.core.preprocessor;

import java.io.File;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.ISymbol;

/** Generate markdown links for Symja function reference in file index.md */
public class MarkdownPreprocessor {

  public MarkdownPreprocessor() {}

  /**
   * Generate markdown links for Symja function reference.
   *
   * @param sourceLocation source directory for funtions (*.md) files
   */
  public static void generateFunctionStrings(final File sourceLocation) {
    if (sourceLocation.exists()) {
      // Get the list of the files contained in the package
      final String[] files = sourceLocation.list();
      if (files != null) {
        System.out.println("## Reference of functions and built-in symbols\n" //
            + "\n" //
            + "Supported features icon:\n" //
            + "\n" //
            + "* &#x2705; - the symbol / function is supported. Note that this doesn't mean that every possible symbolic evaluation is supported\n" //
            + "* &#x2611; - the symbol / function is partially implemented and might not support most basic features of the function\n" //
            + "* &#x274C; - the symbol / function is currently not supported\n" //
            + "* &#x26A0; - the symbol / function is deprecated and will not be further improved\n" //
            + "* &#x1F9EA; - the symbol / function is an experimental implementation. It may not fully behave as expected\n" //
            + "* &#x2615; - the symbol / function is supported on Java virtual machine\n" //
            + "* &#x229E; - the symbol / function is supported on Windows\n" //
            + "* &#x1F504; - the symbol / function is an alias for another function\n" + "\n" //
            + "Functions in alphabetical order:\n" //
            + "\n" //
            + "");
        for (int i = 0; i < files.length; i++) {
          // we are only interested in .md files
          if (files[i].endsWith(".md")) {
            String className = files[i].substring(0, files[i].length() - 3);
            // [Integrate](functions/Integrate.md)
            System.out.print("*");
            String identifier = F.symbolNameNormalized(className);
            if (AST2Expr.PREDEFINED_ALIASES_MAP.containsKey(identifier)) {
              System.out.print(ImplementationStatus.STATUS_EMOJIS[ImplementationStatus.ALIAS]);
            } else {
              ISymbol symbol = Context.SYSTEM.get(identifier);
              if (symbol != null) {
                String status = SourceCodeFunctions.statusAsEmoji(symbol);
                if (status != null) {
                  System.out.print(status);
                } else {
                  System.out.print(" ");
                }
              } else {
                System.out.print(" ");
              }
            }
            System.out.print("[");
            System.out.print(className);
            System.out.print("](functions/");
            System.out.print(className);
            System.out.println(".md)");
          }
        }
      }
    }
  }

  public static void main(final String[] args) {
    F.initSymbols();

    String userHome = System.getProperty("user.home");
    File sourceLocation = new File(
        // C:\\Users\\dev\\git\\symja_android_library\\
        userHome + "/git/symja_android_library/symja_android_library/doc/functions");

    generateFunctionStrings(sourceLocation);
  }
}
