package org.matheclipse.core.preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.io.IOInit;

public class JavaObjectFunctionGenerator {
  static final String[] KEYWORDS = new String[] {"break", "catch", "continue", "default", "do",
      "for", "if", "import", "instanceof", "return", "short", "switch", "throw", "while"};
  static final HashSet<String> KEYWORDS_SET = new HashSet<String>();

  static final String HEADER = //
      "package org.matheclipse.core.expression;\n" + //
          "\n" + //
          "import org.matheclipse.core.convert.Object2Expr;\n"
          + "import org.matheclipse.core.interfaces.IAST;\n" + //
          "\n" //
          + "/**\n" //
          + " * Automatically generated with class <code>org.matheclipse.core.preprocessor.JavaObjectFunctionGenerator</code>.\n"//
          + " * Don't change manually.\n"//
          + " */\n" //
          + "public class J extends S {\n" //
          + "\n"//
          + "  public static IAST plus(final Object... a) {\n"//
          + "    switch (a.length) {\n"//
          + "      case 1:\n"//
          + "        return new AST1(Plus, Object2Expr.convert(a[0]));\n"//
          + "      case 2:\n"//
          + "        return new B2.Plus(Object2Expr.convert(a[0]), Object2Expr.convert(a[1]));\n"//
          + "      case 3:\n"//
          + "        return new AST3(Plus, Object2Expr.convert(a[0]), Object2Expr.convert(a[1]),\n"//
          + "            Object2Expr.convert(a[2]));\n"//
          + "      default:\n"//
          + "        return new AST(Plus, Object2Expr.convertArray(a, false, false));\n"//
          + "    }\n"//
          + "  }\n"//
          + "\n"//
          + "  public static IAST times(final Object... a) {\r\n" + "    switch (a.length) {\n"//
          + "      case 1:\n" + //
          "        return new AST1(Times, Object2Expr.convert(a[0]));\n"//
          + "      case 2:\n"//
          + "        return new B2.Times(Object2Expr.convert(a[0]), Object2Expr.convert(a[1]));\n"//
          + "      case 3:\n"//
          + "        return new AST3(Times, Object2Expr.convert(a[0]), Object2Expr.convert(a[1]),\n"//
          + "            Object2Expr.convert(a[2]));\r\n" + "      default:\n"//
          + "        return new AST(Times, Object2Expr.convertArray(a, false, false));\n"//
          + "    }\n" //
          + ""//
          + "  }";

  public static void main(String[] args) {
    for (int i = 0; i < KEYWORDS.length; i++) {
      KEYWORDS_SET.add(KEYWORDS[i]);
    }
    IOInit.init();
    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < AST2Expr.UPPERCASE_SYMBOL_STRINGS.length; i++) {
      list.add(AST2Expr.UPPERCASE_SYMBOL_STRINGS[i]);
    }
    for (int i = 0; i < AST2Expr.FUNCTION_STRINGS.length; i++) {
      list.add(AST2Expr.FUNCTION_STRINGS[i]);
    }
    Collections.sort(list);

    System.out.println(HEADER);
    for (int i = 0; i < list.size(); i++) {
      String arg = list.get(i);
      ISymbol symbol = F.symbol(arg);
      if (symbol instanceof IBuiltInSymbol) {
        IBuiltInSymbol builtin = (IBuiltInSymbol) symbol;
        IEvaluator evaluator = builtin.getEvaluator();
        if (evaluator instanceof IFunctionEvaluator) {
          IFunctionEvaluator function = (IFunctionEvaluator) evaluator;
          int[] expectedArgSize = function.expectedArgSize(null);
          if (expectedArgSize != null) {
            int min = expectedArgSize[0];
            if (min < 1) {
              min = 1;
            }
            int max = expectedArgSize[1];
            if (max > 3) {
              max = 3;
            }
            if (min > max) {
              continue;
            }

            String lowerCaseArg = arg.toLowerCase();
            if (KEYWORDS_SET.contains(lowerCaseArg)) {
              lowerCaseArg = "$" + lowerCaseArg;
            } else {
              if (arg.length() > 3) {
                lowerCaseArg = lowerCaseArg.charAt(0) + arg.substring(1);
              }
            }
            for (int j = 1; j <= max; j++) {
              if (j == 1) {
                ast1(arg, lowerCaseArg);
              } else if (j == 2) {
                ast2(arg, lowerCaseArg);
              } else if (j == 3) {
                ast3(arg, lowerCaseArg);
              }
            }
          }
        }
      }
    }
    System.out.println("");
    System.out.println("}");
  }

  private static void ast1(String arg, String lowerCaseArg) {
    System.out.println("");
    System.out.println("    public static IAST " + lowerCaseArg + "(final Object a1) {");
    System.out.println("      return new AST1(" + arg + ", Object2Expr.convert(a1));");
    System.out.println("    }");
  }

  private static void ast2(String arg, String lowerCaseArg) {
    System.out.println("");
    System.out
        .println("    public static IAST " + lowerCaseArg + "(final Object a1, final Object a2) {");
    System.out.println(
        "      return new AST2(" + arg + ", Object2Expr.convert(a1), Object2Expr.convert(a2));");
    System.out.println("    }");
  }

  private static void ast3(String arg, String lowerCaseArg) {
    System.out.println("");
    System.out.println("    public static IAST " + lowerCaseArg
        + "(final Object a1, final Object a2, final Object a3) {");
    System.out.println("      return new AST3(" + arg
        + ", Object2Expr.convert(a1), Object2Expr.convert(a2), Object2Expr.convert(a3));");
    System.out.println("    }");
  }
}
