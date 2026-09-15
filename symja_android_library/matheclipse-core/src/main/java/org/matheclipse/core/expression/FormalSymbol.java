package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.Characters;

/**
 * A formal symbol like <code>\[FormalK]</code>: a <code>System`</code> symbol which is
 * {@link org.matheclipse.core.interfaces.ISymbol#PROTECTED} and can never hold a value, a rule or a changed attribute.
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
   * The Wolfram Language name of the formal symbol, e.g. <code>\[FormalK]</code>, or
   * <code>null</code> for an internal formal symbol which cannot be entered by the user.
   */
  private final String fInputName;

  /**
   * @param symbolName the plain name, e.g. <code>"k"</code>
   * @param inputName the Wolfram Language name, e.g. <code>"\\[FormalK]"</code>, or
   *        <code>null</code> for an internal symbol
   */
  FormalSymbol(String symbolName, String inputName) {
    super(symbolName, Context.SYSTEM);
    fInputName = inputName;
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
   * The name of the symbol in the Wolfram Language, as a string of the named character, e.g.
   * <code>"U+F80A"</code> for <code>\[FormalK]</code>. The plain name for an internal formal
   * symbol.
   */
  public String wolframSymbolName() {
    if (fInputName == null) {
      return fSymbolName;
    }
    // strip \[ and ]
    String namedCharacter = Characters.NamedCharactersMap
        .get(fInputName.substring(2, fInputName.length() - 1));
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
   * Does nothing: a formal symbol has no value to clear. Not throwing keeps a
   * <code>finally</code> block which restores the previous value from hiding the exception which
   * was thrown by {@link #assignValue(IExpr, boolean)}.
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
    return F.HIDDEN_SYMBOLS_MAP.get(fSymbolName);
  }
}
