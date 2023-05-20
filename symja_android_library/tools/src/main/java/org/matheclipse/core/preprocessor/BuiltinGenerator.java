package org.matheclipse.core.preprocessor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
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
    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < AST2Expr.UPPERCASE_SYMBOL_STRINGS.length; i++) {
      list.add(AST2Expr.UPPERCASE_SYMBOL_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.SYMBOL_STRINGS.length; i++) {
      list.add(AST2Expr.SYMBOL_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.FUNCTION_STRINGS.length; i++) {
      list.add(AST2Expr.FUNCTION_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.DOLLAR_STRINGS.length; i++) {
      list.add(AST2Expr.DOLLAR_STRINGS[i]);
    }
    Collections.sort(list, IStringX.US_COLLATOR);
    PrintStream out = System.out;
    // public final static IBuiltInSymbol XXXXX = BuiltIns.valueOf(BuiltIns.XXXXX);
    for (String sym : list) {
      // System.out.println(" public final static IBuiltInSymbol " + sym.name()
      // + " = BuiltIns.valueOf(BuiltIns." + sym.name() + ");");
      if (GENERATE_JAVADOC) {
        StringBuilder buf = new StringBuilder();
        int status = Documentation.extraxtJavadoc(buf, sym);
        if (status == 1) {
          out.println(buf.toString());
        } else if (status == 0) {
          out.println();
          out.print("\n        /**");
          out.print(
              " @see <a href=\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/"
                  + sym + ".md\">" + sym + " documentation</a>");
          out.print(" */\n");
        } else {
          out.println();
        }
      }
      out.println("        public final static IBuiltInSymbol " + sym + " = S.initFinalSymbol(\""
          + sym + "\", ID." + sym + ");");
    }
  }
}
