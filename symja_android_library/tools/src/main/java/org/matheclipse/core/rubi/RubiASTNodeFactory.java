package org.matheclipse.core.rubi;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class RubiASTNodeFactory extends ASTNodeFactory {
  public static final RubiASTNodeFactory RUBI_STYLE_FACTORY = new RubiASTNodeFactory(false);

  public RubiASTNodeFactory(boolean ignoreCase) {
    super(ignoreCase);
  }

  @Override
  public SymbolNode createSymbol(final String symbolName, final String context) {
    String name = symbolName;
    if (fIgnoreCase) {
      if (name.length() > 1) {
        name = symbolName.toLowerCase();
      }
    }
    if (Config.RUBI_CONVERT_SYMBOLS) {
      name = toRubiString(name);
    }
    // if (fIgnoreCase) {
    // return new SymbolNode(symbolName.toLowerCase());
    // }
    return new SymbolNode(name);
  }

  private static String toRubiString(final String nodeStr) {
    if (!ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (nodeStr.length() == 1) {
        return nodeStr;
      }
      String lowercaseName = nodeStr.toLowerCase();
      String temp = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(lowercaseName);
      if (temp != null) {
        if (!temp.equals(nodeStr)) {
          temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
          if (temp == null) {
            if (lowercaseName.length() > 1) {

              if (!lowercaseName.equals("sin") && !lowercaseName.equals("cos")
                  && !lowercaseName.equals("tan") && !lowercaseName.equals("cot")
                  && !lowercaseName.equals("csc") && !lowercaseName.equals("sec")) {
                // Rubi inert trig functions
                System.out.println(nodeStr + " => §" + lowercaseName);
              }
            }
            return "§" + lowercaseName;
          }
        }
      } else {
        if (!nodeStr.equals(nodeStr.toLowerCase())) {
          temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
          if (temp == null) {
            if (lowercaseName.length() > 1) {
              System.out.println(nodeStr + " => §" + lowercaseName);
            }
            return "§" + lowercaseName;
          }
        }
      }
    }
    return nodeStr;
  }
}
