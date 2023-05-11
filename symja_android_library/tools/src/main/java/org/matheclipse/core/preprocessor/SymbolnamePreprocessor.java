package org.matheclipse.core.preprocessor;

import java.io.File;
import java.util.Locale;

/**
 * Generate java init sources for Symja symbol names.
 *
 * @deprecated
 */
@Deprecated
public class SymbolnamePreprocessor {

  public SymbolnamePreprocessor() {}

  /**
   * Generate Java files (*.java) from Symja rule files (*.m)
   *
   * @param sourceLocation source directory for rule (*.m) files
   * @param ignoreTimestamp if <code>false</code> only change the target file (*.java), if the
   *        source file (*.m) has a newer time stamp than the target file.
   */
  public static void generateFunctionStrings1(final File sourceLocation, boolean ignoreTimestamp) {
    if (sourceLocation.exists()) {
      // Get the list of the files contained in the package
      final String[] files = sourceLocation.list();
      if (files != null) {
        StringBuilder buffer = new StringBuilder(16000);
        for (int i = 0; i < files.length; i++) {
          // we are only interested in .java files
          if (files[i].endsWith(".java")) {
            String className = files[i].substring(0, files[i].length() - 5);
            String lcClassName = className.length() == 1 ? className : className.toLowerCase(Locale.US);

            // public final static ISymbol Collect =
            // initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ?
            // "collect" : "Collect",
            // new org.matheclipse.core.builtin.function.Collect());
            buffer.append("public final static ISymbol ");
            buffer.append(className);
            buffer.append(" = initFinalSymbol(\n");
            buffer.append("		Config.PARSER_USE_LOWERCASE_SYMBOLS ? \"" + lcClassName + "\" : \""
                + className + "\",\n");

            buffer.append("		new org.matheclipse.core.builtin.function.");
            buffer.append(className);
            buffer.append("());\n");
          }
        }
        System.out.println(buffer.toString());
      }
    }
  }

  public static void generateFunctionStrings2(final File sourceLocation, boolean ignoreTimestamp) {
    if (sourceLocation.exists()) {
      // Get the list of the files contained in the package
      final String[] files = sourceLocation.list();
      if (files != null) {
        StringBuilder buffer = new StringBuilder(16000);
        for (int i = 0; i < files.length; i++) {
          // we are only interested in .m files
          if (files[i].endsWith(".java")) {
            String className = files[i].substring(0, files[i].length() - 5);
            String lcClassName = className.length() == 1 ? className : className.toLowerCase(Locale.US);
            // public final static ISymbol NestWhile =
            // initFinalSymbol(
            // Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestwhile" :
            // "NestWhile",
            // new
            // org.matheclipse.core.builtin.function.NestWhile());
            buffer.append("public final static ISymbol ");
            buffer.append(className);
            buffer.append(" = initFinalSymbol(\n");
            buffer.append("		Config.PARSER_USE_LOWERCASE_SYMBOLS ? \"" + lcClassName + "\" : \""
                + className + "\");\n");
          }
        }
        System.out.println(buffer.toString());
      }
    }
  }

  public static void generateFunctionStrings3(final File sourceLocation, boolean ignoreTimestamp) {
    if (sourceLocation.exists()) {
      // Get the list of the files contained in the package
      final String[] files = sourceLocation.list();
      if (files != null) {
        StringBuilder buffer = new StringBuilder(16000);
        for (int i = 0; i < files.length; i++) {
          // we are only interested in .m files
          if (files[i].endsWith(".java")) {
            String className = files[i].substring(0, files[i].length() - 5);
            if (className.equals("Integrate") || className.equals("Plus")
                || className.equals("Power") || className.equals("Times")) {
              continue;
            }
            // System.out.println(functionName + ".setEvaluator(new
            // org.matheclipse.core.reflection.system."
            // + functionName + "());");
            buffer.append(className);
            buffer.append(".setEvaluator(new org.matheclipse.core.reflection.system.");
            buffer.append(className);
            buffer.append("());\n");
          }
        }
        System.out.println(buffer.toString());
      }
    }
  }

  public static void main(final String[] args) {
    String userHome = System.getProperty("user.home");
    File sourceLocation1 = new File(userHome
        + "/git/symja_android_library/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/function");

    File sourceLocation2 = new File(userHome
        + "/git/symja_android_library/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system");

    generateFunctionStrings1(sourceLocation1, true);

    generateFunctionStrings2(sourceLocation2, true);
    generateFunctionStrings3(sourceLocation2, true);
  }
}
