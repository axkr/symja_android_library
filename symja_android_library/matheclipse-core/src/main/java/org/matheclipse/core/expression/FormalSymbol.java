package org.matheclipse.core.expression;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.Characters;

/**
 * A formal symbol like <code>\[FormalK]</code>: a <code>System`</code> symbol which is
 * {@link org.matheclipse.core.interfaces.ISymbol#PROTECTED} and can never hold a value, a rule or a
 * changed attribute.
 *
 * <p>
 * The built-in rules use formal symbols ({@link S#k}, {@link S#x}, ...) as pattern names and as the
 * variables of <code>Sum</code>, <code>Product</code>, <code>Module</code> or <code>With</code>.
 * There is one instance of each in the JVM, shared by every {@link EvalEngine}, so a formal symbol
 * which held a value would leak that value into every other evaluation on every other thread.
 *
 * <p>
 * The semantics are the ones of Mathematica:
 *
 * <pre>
 * &gt;&gt; Sum(\[FormalK], {\[FormalK], 1, 10})
 * 55
 *
 * &gt;&gt; Block({\[FormalK] = 3}, \[FormalK])
 * 3
 *
 * &gt;&gt; \[FormalK] = 10
 * Set::wrsym: Symbol k is Protected.
 * 10
 * </pre>
 *
 * Every construct which localizes a variable - <code>Table</code>, <code>Sum</code>,
 * <code>Product</code>, <code>Do</code>, <code>Block</code>, <code>Module</code>,
 * <code>With</code>, <code>Function</code> and pattern matching - works with a formal symbol,
 * because it renames or substitutes the symbol instead of assigning to it. Only global definitions
 * are refused.
 *
 * <p>
 * Java code must therefore never call {@link #assignValue(IExpr, boolean)} on a formal symbol: it
 * throws a {@link RuleCreationError}. Rename the symbol to a fresh {@link F#Dummy(String)} for the
 * duration of the evaluation instead, see
 * {@link org.matheclipse.core.eval.util.Iterator#evaluateWithLocalizedVariables(org.matheclipse.core.interfaces.IAST, EvalEngine)}.
 *
 * <p>
 * The {@link #getSymbolName() name} of a formal symbol is its plain letter (<code>"k"</code>), so
 * it prints, sorts and hashes exactly as the letter does. The parser finds it under a different
 * key: the named character <code>\[FormalK]</code> in the <code>System`</code> context. Input form
 * and full form print that named character, so that the output reads back as the same symbol.
 */
public final class FormalSymbol extends Symbol {

  private static final long serialVersionUID = -3180672713525718426L;

  /**
   * The scripts name of the formal symbol, e.g. <code>\[FormalK]</code>, or <code>null</code> for
   * an internal formal symbol which cannot be entered by the user.
   */
  private final String fInputName;

  /** <code>true</code> for a Greek formal symbol like <code>\[FormalAlpha]</code>. */
  private final boolean fGreek;

  /**
   * @param symbolName the plain name, e.g. <code>"k"</code>
   * @param inputName the script's name, e.g. <code>"\\[FormalK]"</code>, or <code>null</code> for
   *        an internal symbol
   */
  FormalSymbol(String symbolName, String inputName) {
    this(symbolName, inputName, false);
  }

  /**
   * @param symbolName the plain name, e.g. <code>"k"</code> or <code>"α"</code>
   * @param inputName the script's name or <code>null</code> for an internal symbol
   * @param greek <code>true</code> for a Greek formal symbol
   */
  private FormalSymbol(String symbolName, String inputName, boolean greek) {
    super(symbolName, Context.SYSTEM);
    fInputName = inputName;
    fGreek = greek;
    // assigned directly: setAttributes() refuses a locked symbol
    fAttributes = PROTECTED;
  }

  /**
   * The name which reads back as this symbol: <code>\[FormalK]</code>, or the plain name of an
   * internal formal symbol.
   */
  public String inputFormString() {
    return fInputName != null ? fInputName : fSymbolName;
  }

  /**
   * The name of the symbol , as a string of the named character, e.g. <code>"U+F80A"</code> for
   * <code>\[FormalK]</code>. The plain name for an internal formal symbol.
   */
  public String wolframSymbolName() {
    if (fInputName == null) {
      return fSymbolName;
    }
    // strip \[ and ]
    String namedCharacter =
        Characters.NamedCharactersMap.get(fInputName.substring(2, fInputName.length() - 1));
    return namedCharacter != null ? namedCharacter : fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr assignedValue() {
    return null;
  }

  /**
   * A formal symbol never holds a value.
   *
   * @throws RuleCreationError always
   */
  @Override
  public void assignValue(IExpr value, boolean setDelayed) {
    throw new RuleCreationError(this);
  }

  /**
   * Does nothing: a formal symbol has no value to clear. Not throwing keeps a <code>finally</code>
   * block which restores the previous value from hiding the exception which was thrown by
   * {@link #assignValue(IExpr, boolean)}.
   */
  @Override
  public void clearValue(IExpr resetValue) {
    // nothing to clear
  }

  /** {@inheritDoc} */
  @Override
  public void clearAll(EvalEngine engine) {
    throw new RuleCreationError(this);
  }

  /** {@inheritDoc} */
  @Override
  public void clearAttributes(int attributes) {
    throw new RuleCreationError(this);
  }

  /** {@inheritDoc} */
  @Override
  public void addAttributes(int attributes) {
    throw new RuleCreationError(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setAttributes(int attributes) {
    throw new RuleCreationError(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setDefaultValue(int pos, IExpr expr) {
    throw new RuleCreationError(this);
  }

  /** {@inheritDoc} */
  @Override
  public void putMessage(int setSymbol, String messageName,
      org.matheclipse.core.interfaces.IStringX message) {
    throw new RuleCreationError(this);
  }

  /**
   * A formal symbol has no rules. Resetting them to <code>null</code> is allowed, so that code
   * which restores a saved state in a <code>finally</code> block doesn't fail.
   *
   * @throws RuleCreationError if <code>rd</code> isn't <code>null</code>
   */
  @Override
  public void setRulesData(RulesData rd) {
    if (rd != null) {
      throw new RuleCreationError(this);
    }
  }

  /** A formal symbol is locked, in package mode too. */
  @Override
  public boolean isLocked() {
    return true;
  }

  /** A formal symbol is locked, in package mode too. */
  @Override
  public boolean isLocked(boolean packageMode) {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return inputFormString();
  }

  /**
   * The symbol is resolved by its plain name, because the <code>System`</code> context knows it
   * only under its named character.
   */
  @Override
  public Object readResolve() {
    return fGreek ? GREEK_BY_PLAIN_NAME.get(fSymbolName) : F.HIDDEN_SYMBOLS_MAP.get(fSymbolName);
  }

  /**
   * The formal symbol of a plain name, as it is written by a serializer.
   *
   * @param plainName the plain name, e.g. <code>"k"</code> or <code>"α"</code>
   * @return <code>null</code> if there is no formal symbol of the name
   */
  public static ISymbol ofPlainName(String plainName) {
    ISymbol symbol = F.HIDDEN_SYMBOLS_MAP.get(plainName);
    if (symbol instanceof FormalSymbol) {
      return symbol;
    }
    return GREEK_BY_PLAIN_NAME.get(plainName);
  }

  /** The Greek formal symbols by the name of their named character, e.g. "FormalAlpha". */
  private static final Map<String, FormalSymbol> GREEK_FORMAL_SYMBOLS = new HashMap<>();

  /**
   * The Greek formal symbols by their plain name, e.g. "α". They are kept out of the map of the
   * hidden symbols, which creates internal symbols by name.
   */
  private static final Map<String, FormalSymbol> GREEK_BY_PLAIN_NAME = new HashMap<>();

  /**
   * Create the Greek formal symbols <code>\[FormalAlpha]</code>, ...,
   * <code>\[FormalCapitalOmega]</code>, <code>\[FormalCurlyPhi]</code>, ... and register them in
   * the <code>System`</code> context under their named character, as the Latin formal symbols of
   * {@link S} are registered. The plain name of a Greek formal symbol is its Greek letter, e.g.
   * <code>"α"</code> for <code>\[FormalAlpha]</code>.
   *
   * <p>
   * Called once from the static initializer of {@link F}, after {@link S} has created the Latin
   * formal symbols.
   */
  static synchronized void initGreekFormalSymbols() {
    if (!GREEK_FORMAL_SYMBOLS.isEmpty()) {
      return;
    }
    // the Latin formal letters are \uF800 ... \uF833, the Greek ones follow
    for (char ch = '\uF834'; ch <= '\uF88F'; ch++) {
      String characterName = Characters.unicodeName(ch);
      if (characterName == null || !characterName.startsWith("Formal")) {
        continue;
      }
      String letter = Characters.NamedCharactersMap.get(characterName.substring(6));
      String plainName = letter != null ? letter : String.valueOf(ch);
      FormalSymbol symbol = new FormalSymbol(plainName, "\\[" + characterName + "]", true);
      GREEK_FORMAL_SYMBOLS.put(characterName, symbol);
      GREEK_BY_PLAIN_NAME.putIfAbsent(plainName, symbol);
      Context.SYSTEM.put(String.valueOf(ch), symbol);
    }
  }

  /**
   * The block of a symbol in the canonical order, which is compared before the names: a Greek
   * formal symbol comes before all other symbols, so that <code>\[FormalAlpha]+u</code> keeps this
   * order.
   *
   * <p>
   * The Latin formal symbols stay sorted by their plain letter, like the symbol of the same name,
   * because the built-in rules and the Java code use them as stand-ins for the user's variables
   * (<code>F.x</code>, <code>S.k</code>); sorting them first would change the canonical order of
   * the results built from them.
   *
   * @return <code>0</code> for a Greek formal symbol and <code>1</code> for every other symbol
   */
  public static int collationRank(ISymbol symbol) {
    return symbol instanceof FormalSymbol && ((FormalSymbol) symbol).fGreek ? 0 : 1;
  }

  /**
   * The Greek formal symbol of a named character.
   *
   * @param characterName the name of the named character, e.g. <code>"FormalAlpha"</code>
   * @return <code>null</code> if there is no such Greek formal symbol
   */
  public static ISymbol greek(String characterName) {
    return GREEK_FORMAL_SYMBOLS.get(characterName);
  }
}
