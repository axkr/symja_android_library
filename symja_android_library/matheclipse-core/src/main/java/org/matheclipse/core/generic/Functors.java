package org.matheclipse.core.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;

public class Functors {

	private static class RulesFunctor implements Function<IExpr, IExpr> {
		private final Map<? extends IExpr, ? extends IExpr> fEqualRules;

		/**
		 * 
		 * @param plusAST
		 *            the complete AST which should be cloned in the {@code apply}
		 *            method
		 * @param position
		 *            the position which should be replaced in the <code>apply()</code>
		 *            method.
		 */
		public RulesFunctor(Map<? extends IExpr, ? extends IExpr> rulesMap) {
			fEqualRules = rulesMap;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			IExpr temp = fEqualRules.get(arg);
			return temp != null ? temp : F.NIL;
		}

	}

	private static class RulesPatternFunctor implements Function<IExpr, IExpr> {
		private final Map<IExpr, IExpr> fEqualRules;
		private final List<PatternMatcherAndEvaluator> fMatchers;
		private final EvalEngine fEngine;

		/**
		 * 
		 * @param plusAST
		 *            the complete AST which should be cloned in the {@code apply}
		 *            method
		 * @param position
		 *            the position which should be replaced in the <code>apply()</code>
		 *            method.
		 */
		public RulesPatternFunctor(Map<IExpr, IExpr> equalRules, List<PatternMatcherAndEvaluator> matchers,
				EvalEngine engine) {
			fEqualRules = equalRules;
			fMatchers = matchers;
			fEngine = engine;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			IExpr temp = fEqualRules.get(arg);
			if (temp != null) {
				return temp;
			}
			for (int i = 0; i < fMatchers.size(); i++) {
				temp = fMatchers.get(i).eval(arg, fEngine);
				if (temp.isPresent()) {
					return temp;
				}
			}
			return F.NIL;
		}
	}

	/**
	 * Create a functor from the given map, which calls the
	 * <code>rulesMap.get()</code> in the functors <code>apply</code>method.
	 * 
	 * @param rulesMap
	 * @return
	 */
	public static Function<IExpr, IExpr> rules(Map<? extends IExpr, ? extends IExpr> rulesMap) {
		return new RulesFunctor(rulesMap);
	}

	/**
	 * Create a functor from the given rules. All strings in <code>strRules</code>
	 * are parsed in internal rules form.
	 * 
	 * @param strRules
	 *            array of rules of the form &quot;<code>x-&gt;y</code>&quot;
	 * @return
	 * @throws WrongArgumentType
	 */
	public static Function<IExpr, IExpr> rules(@Nonnull String[] strRules) throws WrongArgumentType {
		IAST astRules = F.ListAlloc(strRules.length);
		ExprParser parser = new ExprParser(EvalEngine.get());
		// final Parser parser = new Parser();
		final EvalEngine engine = EvalEngine.get();
		for (String str : strRules) {
			IExpr expr = parser.parse(str);
			// final ASTNode parsedAST = parser.parse(str);
			// IExpr expr = AST2Expr.CONST.convert(parsedAST, engine);
			expr = engine.evaluate(expr);
			astRules.append(expr);
		}
		return rules(astRules, engine);
	}

	/**
	 * Create a functor from the given rules. If <code>astRules</code> is a
	 * <code>List[]</code> object, the elements of the list are taken as the rules
	 * of the form <code>Rule[lhs, rhs]</code>, otherwise the <code>astRules</code>
	 * itself is taken as the <code>Rule[lhs, rhs]</code>.
	 * 
	 * @param astRules
	 * @return
	 * @throws WrongArgumentType
	 */
	public static Function<IExpr, IExpr> rules(@Nonnull IAST astRules, EvalEngine engine) throws WrongArgumentType {
		final Map<IExpr, IExpr> equalRules;

		List<PatternMatcherAndEvaluator> matchers = new ArrayList<PatternMatcherAndEvaluator>();
		if (astRules.isList()) {
			// assuming multiple rules in a list
			IAST rule;
			int size = astRules.size() - 1;
			if (size <= 5) {
				equalRules = new OpenFixedSizeMap<IExpr, IExpr>(size * 3 - 1);
			} else {
				equalRules = new HashMap<IExpr, IExpr>();
			}

			for (final IExpr expr : astRules) {
				if (expr.isRuleAST()) {
					rule = (IAST) expr;
					addRuleToCollection(equalRules, matchers, rule);
				} else {
					throw new WrongArgumentType(astRules, astRules, -1, "Rule expression (x->y) expected: ");
				}
			}
		} else {
			if (astRules.isRuleAST()) {
				equalRules = new OpenFixedSizeMap<IExpr, IExpr>(3);
				addRuleToCollection(equalRules, matchers, astRules);
			} else {
				throw new WrongArgumentType(astRules, astRules, -1, "Rule expression (x->y) expected: ");
			}
		}
		if (matchers.size() > 0) {
			return new RulesPatternFunctor(equalRules, matchers, engine);
		}
		return rules(equalRules);
	}

	/**
	 * A predicate to determine if an expression is an instance of
	 * <code>IPattern</code> or <code>IPatternSequence</code>.
	 */
	private static Predicate<IExpr> PATTERNQ_PREDICATE = new Predicate<IExpr>() {
		@Override
		public boolean test(IExpr input) {
			return input.isBlank() || input.isPattern() || input.isPatternSequence() || input.isAlternatives()
					|| input.isExcept();
		}
	};

	private static void addRuleToCollection(Map<IExpr, IExpr> equalRules, List<PatternMatcherAndEvaluator> matchers,
			IAST rule) {
		if (rule.arg1().isFree(PATTERNQ_PREDICATE, true)) {
			IExpr temp = equalRules.get(rule.arg1());
			if (temp == null) {
				if (rule.arg1().isOrderlessAST() || rule.arg1().isFlatAST()) {
					if (rule.isRuleDelayed()) {
						matchers.add(
								new PatternMatcherAndEvaluator(ISymbol.RuleType.SET_DELAYED, rule.arg1(), rule.arg2()));
					} else {
						matchers.add(new PatternMatcherAndEvaluator(ISymbol.RuleType.SET, rule.arg1(),
								evalOneIdentity(rule.arg2())));
					}
					return;
				}
				equalRules.put(rule.arg1(), rule.arg2());
			}
		} else {
			if (rule.isRuleDelayed()) {
				matchers.add(new PatternMatcherAndEvaluator(ISymbol.RuleType.SET_DELAYED, rule.arg1(), rule.arg2()));
			} else {
				matchers.add(new PatternMatcherAndEvaluator(ISymbol.RuleType.SET, rule.arg1(),
						evalOneIdentity(rule.arg2())));
			}
		}
	}

	/**
	 * Test if <code>expr</code> is an <code>IAST</code> with one argument and the
	 * head symbol contains the <code>OneIdentity</code> attribute.
	 * 
	 * @param expr
	 * @return
	 */
	private static IExpr evalOneIdentity(IExpr expr) {
		if (expr.isAST()) {
			IAST arg2AST = (IAST) expr;
			if (arg2AST.isAST1() && arg2AST.head().isSymbol()) {
				final int attr = ((ISymbol) arg2AST.head()).getAttributes();
				if ((ISymbol.ONEIDENTITY & attr) == ISymbol.ONEIDENTITY) {
					expr = arg2AST.arg1();
				}
			}
		}
		return expr;
	}

	private Functors() {

	}
}
