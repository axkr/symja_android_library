package org.matheclipse.core.patternmatching;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** The Tester defines a pattern matching rule set. */
public class Tester implements Predicate<IExpr> {

  /** The rule set */
  private RulesData rules;

  private EvalEngine engine;

  /** The constructor */
  protected Tester(EvalEngine engine) {
    this.rules = new RulesData();
    this.engine = engine;
  }

  /**
   * Method called in order to add a new pattern-matching rule to this rule-set.
   *
   * @param patternMatchingRule the pattern-matching rule
   * @return a
   */
  public void caseOf(final IExpr patternMatchingRule) {
    rules.putDownRule(patternMatchingRule, S.True);
  }

  public EvalEngine getEngine() {
    return engine;
  }

  public void setEngine(EvalEngine engine) {
    this.engine = engine;
  }

  /**
   * Main method performing the pattern matching.
   *
   * @param expression the object to be matched
   * @return <code>true</code> if the expression could be matched with one of the pattern-matching
   *     rules; <code>false</code> otherwise.
   */
  @Override
  public boolean test(IExpr expression) {
    return rules.evalDownRule(expression, engine).isTrue();
  }
}
