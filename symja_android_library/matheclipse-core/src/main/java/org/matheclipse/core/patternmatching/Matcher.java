package org.matheclipse.core.patternmatching;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitor;

/**
 * The Matcher defines a pattern matching rule set.
 *
 */
public class Matcher implements Function<IExpr, IExpr> {
	private static class MatcherVisitor extends AbstractVisitor {
		final Matcher matcher;

		public MatcherVisitor(Matcher matcher) {
			this.matcher = matcher;
		}

		@Override
		public IExpr visit(IASTMutable ast) {
			IAST list = ast;
			boolean evaled = false;
			IExpr temp = matcher.apply(list);
			if (temp.isPresent()) {
				if (temp.isAST()) {
					list = (IAST) temp;
					evaled = true;
				} else {
					return temp;
				}
			}
			IASTAppendable result = F.NIL;
			int i = 1;
			while (i < list.size()) {
				temp = list.get(i).accept(this);
				if (temp.isPresent()) {
					// something was evaluated - return a new IAST:
					result = list.clone();
					for (int j = 1; j < i; j++) {
						result.set(j, list.get(j));
					}
					result.set(i++, temp);
					break;
				}
				i++;
			}
			if (result.isPresent()) {
				while (i < list.size()) {
					temp = list.get(i).accept(this);
					if (temp.isPresent()) {
						result.set(i, temp);
					} else {
						result.set(i, list.get(i));
					}
					i++;
				}
			}
			if (result.isPresent()) {
				return result;
			}
			if (evaled) {
				return list;
			}
			return F.NIL;
		}

		@Override
		public IExpr visit(IComplex element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IComplexNum element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IFraction element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IInteger element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(INum element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IPattern element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IPatternSequence element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(IStringX element) {
			return matcher.apply(element);
		}

		@Override
		public IExpr visit(ISymbol element) {
			return matcher.apply(element);
		}

	}

	private static class PatternMatcherBiFunctionMethod extends AbstractPatternMatcherMethod {
		BiFunction<IExpr, IExpr, IExpr> fRightHandSide;

		public PatternMatcherBiFunctionMethod(final IExpr leftHandSide,
				final BiFunction<IExpr, IExpr, IExpr> rightHandSide) {
			super(leftHandSide);
			fRightHandSide = rightHandSide;
		}

		@Override
		IExpr evalMethod() {
			IExpr arg1 = fPatternMap.getValue(0);
			IExpr arg2 = fPatternMap.getValue(0);
			return fRightHandSide.apply(arg1, arg2);
		}
	}

	private static class PatternMatcherBiPredicateMethod extends AbstractPatternMatcherMethod {
		BiPredicate<IExpr, IExpr> fRightHandSide;

		public PatternMatcherBiPredicateMethod(final IExpr leftHandSide,
				final BiPredicate<IExpr, IExpr> rightHandSide) {
			super(leftHandSide);
			fRightHandSide = rightHandSide;
		}

		@Override
		IExpr evalMethod() {
			IExpr arg1 = fPatternMap.getValue(0);
			IExpr arg2 = fPatternMap.getValue(1);
			return fRightHandSide.test(arg1, arg2) ? F.True : F.False;
		}
	}

	private static class PatternMatcherFunctionMethod extends AbstractPatternMatcherMethod {
		Function<IExpr, IExpr> fRightHandSide;

		public PatternMatcherFunctionMethod(final IExpr leftHandSide, final Function<IExpr, IExpr> rightHandSide) {
			super(leftHandSide);
			fRightHandSide = rightHandSide;
		}

		@Override
		IExpr evalMethod() {
			IExpr arg1 = fPatternMap.getValue(0);
			return fRightHandSide.apply(arg1);
		}
	}

	private static class PatternMatcherMapMethod extends AbstractPatternMatcherMethod {
		final IPatternMethod fRightHandSide;

		public PatternMatcherMapMethod(final IExpr leftHandSide, final IPatternMethod rightHandSide) {
			super(leftHandSide);
			fRightHandSide = rightHandSide;
		}

		@Override
		IExpr evalMethod() {
			return fRightHandSide.eval(fPatternMap);
		}
	}

	private static class PatternMatcherPredicateMethod extends AbstractPatternMatcherMethod {
		Predicate<IExpr> fRightHandSide;

		public PatternMatcherPredicateMethod(final IExpr leftHandSide, final Predicate<IExpr> rightHandSide) {
			super(leftHandSide);
			fRightHandSide = rightHandSide;
		}

		@Override
		IExpr evalMethod() {
			IExpr arg1 = fPatternMap.getValue(0);
			return fRightHandSide.test(arg1) ? F.True : F.False;
		}
	}

	/**
	 * The rule set
	 */
	private RulesData rules;

	private EvalEngine engine;

	/**
	 * The constructor
	 */
	public Matcher(EvalEngine engine) {
		this.rules = new RulesData(Context.SYSTEM);
		this.engine = engine;
	}

	/**
	 * Main method performing the pattern matching.
	 *
	 * @param expression
	 *            the object to be matched
	 * @return a computation result done by an accepted rule during pattern matching
	 *         process
	 */
	@Override
	public IExpr apply(IExpr expression) {
		return rules.evalDownRule(expression, engine);
	}

	/**
	 * If this rule matches the evaluation will return <code>F.True</code> or
	 * <code>F.False</code> depending on the <code>predicates</code> result.
	 * 
	 * @param patternMatchingRule
	 * @param predicate
	 */
	public void caseBoole(final IExpr patternMatchingRule, final BiPredicate<IExpr, IExpr> predicate) {
		rules.putDownRule(patternMatchingRule, new PatternMatcherBiPredicateMethod(patternMatchingRule, predicate));
	}

	/**
	 * If this rule matches the evaluation will return <code>F.True</code> or
	 * <code>F.False</code> depending on the <code>predicates</code> result.
	 * 
	 * @param patternMatchingRule
	 * @param predicate
	 */
	public void caseBoole(final IExpr patternMatchingRule, final Predicate<IExpr> predicate) {
		rules.putDownRule(patternMatchingRule, new PatternMatcherPredicateMethod(patternMatchingRule, predicate));
	}

	/**
	 * If this rule matches the evaluation will return the result of the
	 * <code>method.eval()</code> method.
	 * 
	 * @param patternMatchingRule
	 * @param method
	 */
	public void caseMethod(final IExpr patternMatchingRule, final IPatternMethod method) {
		rules.putDownRule(patternMatchingRule, new PatternMatcherMapMethod(patternMatchingRule, method));
	}

	/**
	 * If this rule matches the evaluation will return the result of the
	 * <code>function.apply()</code> method.
	 * 
	 * @param patternMatchingRule
	 * @param function
	 */
	public void caseOf(final IExpr patternMatchingRule, final BiFunction<IExpr, IExpr, IExpr> function) {
		rules.putDownRule(patternMatchingRule, new PatternMatcherBiFunctionMethod(patternMatchingRule, function));
	}

	/**
	 * If this rule matches the evaluation will return the result of the
	 * <code>function.apply()</code> method.
	 * 
	 * @param patternMatchingRule
	 * @param function
	 */
	public void caseOf(final IExpr patternMatchingRule, final Function<IExpr, IExpr> function) {
		rules.putDownRule(patternMatchingRule, new PatternMatcherFunctionMethod(patternMatchingRule, function));
	}

	/**
	 * Method called in order to add a new pattern-matching rule to this rule-set.
	 *
	 * @param patternMatchingRule
	 *            the pattern-matching rule
	 * @param resultExpr
	 *            the result expression which should be returned if the
	 *            pattern-matching rule matches an expression in the apply method.
	 * @return a
	 */
	public void caseOf(final IExpr patternMatchingRule, final IExpr resultExpr) {
		rules.putDownRule(patternMatchingRule, resultExpr);
	}

	public EvalEngine getEngine() {
		return engine;
	}

	/**
	 * Replace all (sub-) expressions with the given rule set. If no substitution
	 * matches, the method returns the given <code>expression</code>.
	 * 
	 * @param expression
	 * @return <code>F.NIL</code> if no rule of the rule set matched an expression.
	 */
	public IExpr replaceAll(IExpr expression) {
		return expression.accept(new MatcherVisitor(this));
	}

	public void setEngine(EvalEngine engine) {
		this.engine = engine;
	}

	// public static IExpr evalTest(PatternMap pm) {
	// return F.List(pm.val(F.y));
	// }
	//
	// public static void main(String[] args) {
	// Matcher matcher = new Matcher();
	// matcher.caseOf(F.Sin(F.x_), F.D(F.Sin(F.x), F.x));
	// matcher.caseOf(F.Cos(F.x_), x -> F.Floor(x));
	// matcher.caseBoole(F.Cot(F.x_), x -> false);
	// matcher.caseMethod(F.Tan(F.y_), Matcher::evalTest);
	// System.out.println(matcher.apply(F.Tan(F.z)));
	// }

}
