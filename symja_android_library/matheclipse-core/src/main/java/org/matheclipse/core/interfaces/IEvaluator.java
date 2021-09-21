package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
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

  /**
   * Define the default options of a symbol (i.e. <code>Options(symbol) = {SameTest -> SameQ, ...}
   * </code>.
   *
   * @param symbol the symbol for which the default options should be defined
   * @param listOfRules a list of rules with the default option settings
   */
  default void setOptions(final ISymbol symbol, IAST listOfRules) {
    symbol.putDownRule(
        IPatternMatcher.SET,
        true,
        F.Options(symbol),
        listOfRules,
        IPatternMap.DEFAULT_RULE_PRIORITY,
        true);
  }
}
