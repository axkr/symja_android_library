package org.matheclipse.core.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Base class of the operators which build {@link org.matheclipse.core.interfaces.IExpr} nodes,
 * holding the resolved head symbol of the function an operator expands to.
 *
 * <p>
 * Without the cache every operator application resolved its head by name: {@code F.$s("Plus")}
 * lowercases the name - allocating a string - and then looks the result up in two maps. That ran
 * once per operator node built, which on operator-dense input is once per token.
 *
 * <p>
 * Resolution is deferred to first use rather than done in the constructor, because the operator
 * tables are built from {@link org.matheclipse.core.expression.F}'s own static initializer
 * (F.initSymja calls ExprParserFactory.initialize), so at construction time the symbol tables this
 * would consult are not populated yet.
 */
abstract class ExprOperator extends Operator {

  /**
   * The resolved head, or <code>null</code> until first use. Deliberately neither volatile nor
   * synchronized: two threads racing here both resolve the same name to the same canonical symbol
   * instance, so the worst case is that the work is done twice and one identical result overwrites
   * the other.
   */
  private ISymbol fHeadSymbol;

  ExprOperator(final String oper, final String functionName, final int precedence) {
    super(oper, functionName, precedence);
  }

  /**
   * The symbol this operator's function name denotes - <code>Plus</code> for <code>+</code>,
   * <code>Times</code> for <code>*</code>.
   *
   * <p>
   * Not meaningful for the operators whose function name is a sentinel rather than a symbol
   * (<code>//</code>, <code>§TILDE§</code>, <code>PreMinus</code>, <code>PrePlus</code>). Those all
   * override <code>createFunction</code> and build their result directly, so they never call this.
   */
  final ISymbol headSymbol() {
    ISymbol symbol = fHeadSymbol;
    if (symbol == null) {
      symbol = resolveHeadSymbol(getFunctionName());
      fHeadSymbol = symbol;
    }
    return symbol;
  }

  /**
   * Resolve a built-in head by its integer id, which is a plain array index and, unlike
   * {@link F#$s(String)}, does not depend on
   * {@link org.matheclipse.parser.client.ParserConfig#PARSER_USE_LOWERCASE_SYMBOLS}. The fallback
   * covers a name which is not a built-in symbol at all.
   */
  private static ISymbol resolveHeadSymbol(final String functionName) {
    Integer id = ID.STRING_TO_ID_MAP.get(functionName);
    return id == null ? F.$s(functionName) : S.symbol(id);
  }
}
