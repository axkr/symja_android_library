package org.matheclipse.core.patternmatching;

import com.duy.lambda.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The Tester defines a pattern matching rule set.
 *
 */
public class Tester implements Predicate<IExpr> {

	/**
	 * The rule set
	 */
	private RulesData rules;

	private EvalEngine engine;

	/**
	 * The constructor
	 */
	protected Tester(EvalEngine engine) {
		this.rules = new RulesData(Context.SYSTEM);
		this.engine = engine;
	}

	/**
	 * Method called in order to add a new pattern-matching rule to this rule-set.
	 * 
	 * @param patternMatchingRule
	 *            the pattern-matching rule
	 * @return a
	 */
	public void caseOf(final IExpr patternMatchingRule) {
		rules.putDownRule(patternMatchingRule, F.True);
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
	 * @param expression
	 *            the object to be matched
	 * @return <code>true</code> if the expression could be matched with one of the
	 *         pattern-matching rules; <code>false</code> otherwise.
	 */
	@Override
	public boolean test(IExpr expression) {
		return rules.evalDownRule(expression, engine).isTrue();
	}

	// public static void main(String[] args) {
	// Tester matcher = new Tester();
	// matcher.caseOf(F.Sin(F.x_));
	//
	// System.out.println(matcher.test(F.Sin(F.y)));
	// }

}
