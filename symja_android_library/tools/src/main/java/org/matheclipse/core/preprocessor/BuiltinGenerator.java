package org.matheclipse.core.preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IStringX;

/**
 * The built-in symbols in class {@link org.matheclipse.core.expression.S} are generated with this
 * tools class.
 */
public class BuiltinGenerator {

  private static final boolean GENERATE_JAVADOC = true;

  public static void main(String[] args) {
    StringBuilder buf = printSSymbols(null);

    System.out.println(buf.toString());
  }

  private static ArrayList<String> createAllSymbols(Set<String> additionalSymbols) {
    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < AST2Expr.UPPERCASE_SYMBOL_STRINGS.length; i++) {
      list.add(AST2Expr.UPPERCASE_SYMBOL_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.SYMBOL_STRINGS.length; i++) {
      list.add(AST2Expr.SYMBOL_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.PHYSICAL_CONSTANTS_STRINGS.length; i++) {
      list.add(AST2Expr.PHYSICAL_CONSTANTS_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.FUNCTION_STRINGS.length; i++) {
      list.add(AST2Expr.FUNCTION_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.DOLLAR_STRINGS.length; i++) {
      list.add(AST2Expr.DOLLAR_STRINGS[i]);
    }
    if (additionalSymbols != null) {
      list.addAll(additionalSymbols);
    }
    return list;
  }

  public static void createJavadocFromFunctionDoc(StringBuilder buf, String sym) {
    StringBuilder buf2 = new StringBuilder();
    int status = Documentation.extraxtJavadoc(buf2, sym);
    if (status == 1) {
      buf.append(buf2.toString());
      buf.append("\n");
    } else if (status == 0) {
      buf.append("\n");
      buf.append("\n        /**");
      buf.append(
          " @see <a href=\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/"
              + sym + ".md\">" + sym + " documentation</a>\n");
      buf.append("         */");
    } else {
      buf.append("\n");
    }
  }

  private static StringBuilder printSSymbols(Set<String> additionalSymbols) {
    ArrayList<String> list = createAllSymbols(additionalSymbols);
    Collections.sort(list, IStringX.US_COLLATOR);
    StringBuilder buf = new StringBuilder();
    // public final static IBuiltInSymbol XXXXX = BuiltIns.valueOf(BuiltIns.XXXXX);
    for (String sym : list) {
      // System.out.println(" public final static IBuiltInSymbol " + sym.name()
      // + " = BuiltIns.valueOf(BuiltIns." + sym.name() + ");");
      if (GENERATE_JAVADOC) {
        createJavadocFromFunctionDoc(buf, sym);
      }
      buf.append("        public final static IBuiltInSymbol " + sym + " = S.initFinalSymbol(\""
          + sym + "\", ID." + sym + ");\n");
    }
    return buf;
  }
}
