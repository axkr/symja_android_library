package org.matheclipse.core.patternmatching;

import java.util.function.Function;

import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
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

	static class MatcherVisitor extends AbstractVisitor {
		final Matcher matcher;

		public MatcherVisitor(Matcher matcher) {
			this.matcher = matcher;
		}

		@Override
		public IExpr visit(IAST ast) {
			boolean evaled = false;
			IExpr temp = matcher.apply(ast);
			if (temp.isPresent()) {
				if (temp.isAST()) {
					ast = (IAST) temp;
					evaled = true;
				} else {
					return temp;
				}
			}
			IAST result = F.NIL;
			int i = 1;
			while (i < ast.size()) {
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					// something was evaluated - return a new IAST:
					result = ast.clone();
					for (int j = 1; j < i; j++) {
						result.set(j, ast.get(j));
					}
					result.set(i++, temp);
					break;
				}
				i++;
			}
			if (result.isPresent()) {
				while (i < ast.size()) {
					temp = ast.get(i).accept(this);
					if (temp.isPresent()) {
						result.set(i, temp);
					} else {
						result.set(i, ast.get(i));
					}
					i++;
				}
			}
			if (result.isPresent()) {
				return result;
			}
			if (evaled) {
				return ast;
			}
			return F.NIL;
		}

		public IExpr visit(IInteger element) {
			return matcher.apply(element);
		}

		public IExpr visit(IFraction element) {
			return matcher.apply(element);
		}

		public IExpr visit(IComplex element) {
			return matcher.apply(element);
		}

		public IExpr visit(INum element) {
			return matcher.apply(element);
		}

		public IExpr visit(IComplexNum element) {
			return matcher.apply(element);
		}

		public IExpr visit(ISymbol element) {
			return matcher.apply(element);
		}

		public IExpr visit(IPattern element) {
			return matcher.apply(element);
		}

		public IExpr visit(IPatternSequence element) {
			return matcher.apply(element);
		}

		public IExpr visit(IStringX element) {
			return matcher.apply(element);
		}

	}

	/**
	 * The rule set
	 */
	private RulesData rules;

	/**
	 * The constructor
	 */
	public Matcher() {
		this.rules = new RulesData(Context.SYSTEM);
	}

	/**
	 * Method called in order to add a new pattern-matching rule to this
	 * rule-set.
	 *
	 * @param patternMatchingRule
	 *            the pattern-matching rule
	 * @param resultExpr
	 *            the result expression which should be returned if the
	 *            pattern-matching rule matches an expression in the apply
	 *            method.
	 * @return a
	 */
	public void caseOf(final IExpr patternMatchingRule, final IExpr resultExpr) {
		rules.putDownRule(patternMatchingRule, resultExpr);
	}

	/**
	 * Main method performing the pattern matching.
	 *
	 * @param expression
	 *            the object to be matched
	 * @return a computation result done by an accepted rule during pattern
	 *         matching process
	 */
	@Override
	public IExpr apply(IExpr expression) {
		return rules.evalDownRule(expression);
	}

	/**
	 * Replace all (sub-) expressions with the given rule set. If no
	 * substitution matches, the method returns the given
	 * <code>expression</code>.
	 * 
	 * @param expression
	 * @return <code>F.NIL</code> if no rule of the rule set matched an
	 *         expression.
	 */
	public IExpr replaceAll(IExpr expression) {
		return expression.accept(new MatcherVisitor(this));
	}

	// public static void main(String[] args) {
	// Matcher matcher = new Matcher();
	// matcher.caseOf(F.Sin(F.x_), F.D(F.Sin(F.x), F.x));
	//
	// System.out.println(matcher.apply(F.Sin(F.y)));
	// }

}
