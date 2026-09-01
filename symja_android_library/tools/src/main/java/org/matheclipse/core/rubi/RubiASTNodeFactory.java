package org.matheclipse.core.rubi;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class RubiASTNodeFactory extends ASTNodeFactory {
  public static final RubiASTNodeFactory RUBI_STYLE_FACTORY = new RubiASTNodeFactory(false);

  /**
   * Rubi's inert trigonometric markers. They are <i>meant</i> to become {@code §sin} and friends -
   * {@code UtilityFunctions*.java} defines them under exactly that name - so they are not reported
   * as unresolved.
   */
  private static final Set<String> INERT_TRIG_FUNCTIONS =
      Set.of("sin", "cos", "tan", "cot", "csc", "sec");

  /**
   * Every symbol which {@link #toRubiString(String)} could not resolve and therefore renamed to
   * {@code §name}. A name lands here when it is missing from
   * {@link F#PREDEFINED_INTERNAL_FORM_STRINGS}, i.e. from {@code ConvertRubi.addPredefinedSymbols()}
   * and from the {@code UtilityFunctionCtors} constructors that method is seeded from.
   *
   * <p>
   * This is not cosmetic: a renamed head is emitted as <code>$($s("§name"), ...)</code>, which
   * compiles but never evaluates, so a rule whose condition calls it can never fire. Rubi 4.17.3
   * shipped with {@code FractionalPowerFactorQ} (8 rules) and {@code SubstPower} (2 rules) dead
   * for exactly this reason. {@code ConvertRubi} fails the conversion when this set is not empty.
   */
  private static final Set<String> UNRESOLVED_SYMBOLS = new TreeSet<>();

  /** The Rubi symbols which had to be renamed to {@code §name}, sorted, without the inert trig. */
  public static Set<String> unresolvedSymbols() {
    return UNRESOLVED_SYMBOLS;
  }

  public RubiASTNodeFactory(boolean ignoreCase) {
    super(ignoreCase);
  }

  @Override
  public SymbolNode createSymbol(final String symbolName, final String context) {
    String name = symbolName;
    if (fIgnoreCase) {
      if (name.length() > 1) {
        name = symbolName.toLowerCase(Locale.US);
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
      String lowercaseName = nodeStr.toLowerCase(Locale.US);
      String temp = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(lowercaseName);
      if (temp != null) {
        if (!temp.equals(nodeStr)) {
          temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
          if (temp == null) {
            return renamed(nodeStr, lowercaseName);
          }
        }
      } else {
        if (!nodeStr.equals(nodeStr.toLowerCase(Locale.US))) {
          temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
          if (temp == null) {
            return renamed(nodeStr, lowercaseName);
          }
        }
      }
    }
    return nodeStr;
  }

  /**
   * Rename an unresolved symbol to {@code §name} and record it, so that
   * {@code ConvertRubi} can fail the conversion instead of emitting a rule which can never fire.
   */
  private static String renamed(String nodeStr, String lowercaseName) {
    if (lowercaseName.length() > 1 && !INERT_TRIG_FUNCTIONS.contains(lowercaseName)) {
      UNRESOLVED_SYMBOLS.add(nodeStr);
    }
    return "§" + lowercaseName;
  }
}
