package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * An IEvaluator can be linked to an ISymbol to define the evaluation behaviour of the symbol at
 * creation time.
 */
public interface IEvaluator {

  default void await() throws InterruptedException {
    // do nothing
  }

  /**
   * This method will be called every time a new ISymbol will be created. In this method you can set
   * ISymbol attributes or constants for the symbol
   *
   * @param newSymbol the symbol which should be set up
   */
  public void setUp(ISymbol newSymbol);

  default boolean evalIsReal(IAST ast) {
    return false;
  }

  /**
   * Define the default options of a symbol (i.e. <code>Options(symbol) = {SameTest -> SameQ, ...}
   * </code>.
   *
   * @param symbol the symbol for which the default options should be defined
   * @param listOfRules a list of rules with the default option settings
   */
  default void setOptions(final ISymbol symbol, IAST listOfRules) {
    symbol.putDownRule(IPatternMatcher.SET, true, F.Options(symbol), listOfRules,
        IPatternMap.DEFAULT_RULE_PRIORITY, true);
  }

  /**
   * The implementation status of this symbol or function.
   * <p>
   * <ul>
   * <li>&#x2705; - {@link ImplementationStatus#FULL_SUPPORT} the symbol / function is supported.
   * Note that this doesn't mean that every symbolic evaluation is supported.
   * <li>&#x2611; - {@link ImplementationStatus#PARTIAL_SUPPORT} the symbol / function is partially
   * implemented and might not support most basic features of the element
   * <li>&#x274C; - {@link ImplementationStatus#NO_SUPPORT} the symbol / function is currently not
   * supported
   * <li>&#x26A0; - {@link ImplementationStatus#DEPRECATED} the symbol / function is deprecated and
   * will not be further improved
   * <li>&#x1F9EA; - {@link ImplementationStatus#EXPERIMENTAL} the symbol / function is an
   * experimental implementation. It may not fully behave as expected.
   * </ul>
   */
  default int status() {
    return ImplementationStatus.NO_SUPPORT;
  }
}
