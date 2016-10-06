package org.matheclipse.core.generic;

import java.util.ArrayList;
import java.util.Collection;
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
	private static class AppendFunctor implements Function<IExpr, IExpr> {
		protected final IAST fAST;

		/**
		 * 
		 * @param ast
		 *            the AST which should be cloned in the {@code apply} method
		 */
		public AppendFunctor(final IAST ast) {
			fAST = ast;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			final IAST ast = fAST.clone();
			ast.append(arg);
			return ast;
		}

	}

	private static class ApplyFunctor implements Function<IExpr, IExpr> {
		final IExpr fConstant;

		public ApplyFunctor(IExpr constant) {
			this.fConstant = constant;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			IAST result = F.NIL;
			if (arg.isAST()) {
				result = ((IAST) arg).copy();
				result.set(0, fConstant);
			}
			return result;
		}

	}

	private static class EvalArgFunctor implements Function<IExpr, IExpr> {
		EvalEngine fEngine;
		IAST fAST;
		int fPosition;

		/**
		 * Create a functor which evaluates the given <code>ast</code> as a
		 * function with the argument at the given <code>position</code>
		 * replaced in the <code>apply()</code> method.
		 * 
		 * @param ast
		 *            the function which should be evaluated
		 * @param position
		 *            the position at which the argument should be replaced
		 * @param engine
		 *            the current evaluation engine
		 * @return
		 */
		public EvalArgFunctor(IAST ast, int position, EvalEngine engine) {
			this.fEngine = engine;
			this.fAST = ast;
			fPosition = position;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			IAST ast = fAST.copy();
			ast.set(fPosition, arg);
			return fEngine.evaluate(ast);
		}
	}

	private static class CollectFunctor implements Function<IExpr, IExpr> {
		protected Collection<? super IExpr> resultCollection;

		/**
		 * 
		 * @param ast
		 *            the AST which should be cloned in the {@code apply} method
		 */
		public CollectFunctor(Collection<? super IExpr> resultCollection) {
			this.resultCollection = resultCollection;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			resultCollection.add(arg);
			return F.NIL;
		}

	}

	private static class ConstantFunctor implements Function<IExpr, IExpr> {
		final IExpr fConstant;

		public ConstantFunctor(final IExpr expr) {
			fConstant = expr;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			return fConstant;
		}

	}

	private static class ReplaceArgFunctor implements Function<IExpr, IExpr> {
		private final IAST fConstant;
		private final int fPosition;

		/**
		 * 
		 * @param ast
		 *            the complete AST which should be cloned in the
		 *            {@code apply} method
		 * @param position
		 *            the position which should be replaced in the
		 *            <code>apply()</code> method.
		 */
		public ReplaceArgFunctor(final IAST ast, int position) {
			fConstant = ast;
			fPosition = position;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			final IAST ast = fConstant.copy();
			ast.set(fPosition, arg);
			return ast;
		}

	}

	private static class ReplaceAllFunctor implements Function<IExpr, IExpr> {
		private final IAST fConstant;
		private final IExpr fLHS;

		/**
		 * 
		 * @param ast
		 *            the complete AST which should be cloned in the
		 *            {@code apply} method
		 * @param position
		 *            the position which should be replaced in the
		 *            <code>apply()</code> method.
		 */
		public ReplaceAllFunctor(final IAST ast, IExpr lhs) {
			fConstant = ast;
			fLHS = lhs;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			return fConstant.replaceAll(F.Rule(fLHS, arg));
		}

	}

	private static class RulesFunctor implements Function<IExpr, IExpr> {
		private final Map<? extends IExpr, ? extends IExpr> fEqualRules;

		/**
		 * 
		 * @param plusAST
		 *            the complete AST which should be cloned in the
		 *            {@code apply} method
		 * @param position
		 *            the position which should be replaced in the
		 *            <code>apply()</code> method.
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

		/**
		 * 
		 * @param plusAST
		 *            the complete AST which should be cloned in the
		 *            {@code apply} method
		 * @param position
		 *            the position which should be replaced in the
		 *            <code>apply()</code> method.
		 */
		public RulesPatternFunctor(Map<IExpr, IExpr> equalRules, List<PatternMatcherAndEvaluator> matchers) {
			fEqualRules = equalRules;
			fMatchers = matchers;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			IExpr temp = fEqualRules.get(arg);
			if (temp != null) {
				return temp;
			}
			for (int i = 0; i < fMatchers.size(); i++) {
				temp = fMatchers.get(i).eval(arg);
				if (temp.isPresent()) {
					return temp;
				}
			}
			return F.NIL;
		}
	}

	private static class ScanFunctor implements Function<IExpr, IExpr> {
		protected final IAST fAST;
		protected IAST resultCollection;

		/**
		 * 
		 * @param ast
		 *            the AST which should be cloned and appended to in the
		 *            {@code apply} method
		 * @param resultAST
		 *            the collection to which the cloned AST will be appended
		 */
		public ScanFunctor(final IAST ast, IAST resultAST) {
			fAST = ast;
			this.resultCollection = resultAST;
		}

		@Override
		@Nonnull
		public IExpr apply(final IExpr arg) {
			final IAST ast = fAST.clone();
			ast.append(arg);
			resultCollection.append(ast);
			return F.NIL;
		}

	}

	/**
	 * Return a function which clones the given AST and appends the argument to
	 * the cloned AST in the <code>apply()</code> method. The clone is then
	 * appended to the result collection.
	 * 
	 * @param ast
	 *            the AST which should be cloned and appended to in the
	 *            {@code apply} method
	 * @param resultAST
	 *            the collection to which the cloned AST will be appended
	 * @return
	 */
	public static Function<IExpr, IExpr> scan(@Nonnull final IAST ast, @Nonnull IAST resultAST) {
		return new ScanFunctor(ast, resultAST);
	}

	/**
	 * Return a function which clones the given AST and appends the argument to
	 * the cloned AST in the <code>apply()</code> method.
	 * 
	 * @param ast
	 *            the AST which should be cloned in the {@code apply} method
	 * @return
	 */
	public static Function<IExpr, IExpr> append(@Nonnull IAST ast) {
		return new AppendFunctor(ast);
	}

	/**
	 * Create a functor which evaluates the given <code>ast</code> as a function
	 * with the argument at the given <code>position</code> replaced in the
	 * <code>apply()</code> method.
	 * 
	 * @param ast
	 *            the function which should be evaluated
	 * @param position
	 *            the position at which the argument should be replaced
	 * @param engine
	 *            the current evaluation engine
	 * @return
	 */
	public static Function<IExpr, IExpr> evalArg(@Nonnull IAST ast, int position, @Nonnull EvalEngine engine) {
		return new EvalArgFunctor(ast, position, engine);
	}

	/**
	 * Return a function which clones the argument, if it is of type AST and
	 * sets the header to the given <code>expr</code>. <code>expr</code> is
	 * typically a symbol.
	 * 
	 * @param expr
	 * @return
	 */
	public static Function<IExpr, IExpr> apply(@Nonnull IExpr expr) {
		return new ApplyFunctor(expr);
	}

	public static Function<IExpr, IExpr> collect(@Nonnull Collection<? super IExpr> resultCollection) {
		return new CollectFunctor(resultCollection);
	}

	/**
	 * Return a function which returns the given expression in the
	 * <code>apply()</code> method.
	 * 
	 * @param expr
	 * @return
	 */
	public static Function<IExpr, IExpr> constant(@Nonnull IExpr expr) {
		return new ConstantFunctor(expr);
	}

	/**
	 * Create a functor, which replaces all (sub-)expressions in
	 * <code>ast</code> which equals <code>lhs</code> with the argument of the
	 * functors <code>apply()</code> method.
	 * 
	 * @param ast
	 *            an AST which contains the <code>lhs</code> as a placeholder in
	 *            it's subexpressions.
	 * @param lhs
	 *            left-hand-side of a <code>Rule(lhs,...)</code>
	 * @return
	 */
	public static Function<IExpr, IExpr> replaceAll(@Nonnull IAST ast, @Nonnull IExpr lhs) {
		return new ReplaceAllFunctor(ast, lhs);
	}

	/**
	 * Create a functor, which replaces the argument at the first position in
	 * the <code>ast</code> with the argument of the functors
	 * <code>apply()</code> method.
	 * 
	 * @param ast
	 *            an AST where the first argument is replaced with the argument
	 *            of the functors <code>apply()</code> method.
	 * @return
	 * @deprecated use {@link #replaceArg(IAST, int)} instead
	 */
	public static Function<IExpr, IExpr> replace1st(@Nonnull IAST ast) {
		return new ReplaceArgFunctor(ast, 1);
	}

	/**
	 * Create a functor, which replaces the argument at the second position in
	 * the <code>ast</code> with the argument of the functors
	 * <code>apply()</code> method.
	 * 
	 * @param ast
	 *            an AST where the second argument is replaced with the argument
	 *            of the functors <code>apply()</code> method.
	 * @return
	 * @deprecated use {@link #replaceArg(IAST, int)} instead
	 */
	public static Function<IExpr, IExpr> replace2nd(@Nonnull IAST ast) {
		return new ReplaceArgFunctor(ast, 2);
	}

	/**
	 * Create a functor, which replaces the argument at the given position in
	 * the <code>ast</code> with the argument of the functors
	 * <code>apply()</code> method.
	 * 
	 * @param ast
	 *            an AST where the element at the given <code>position</code> is
	 *            replaced with the argument of the functors
	 *            <code>apply()</code> method.
	 * @param position
	 *            the position of the element, which should be replaced in the
	 *            <code>ast</code>.
	 * @return
	 */
	public static Function<IExpr, IExpr> replaceArg(@Nonnull IAST ast, int position) {
		return new ReplaceArgFunctor(ast, position);
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
	 * Create a functor from the given rules. All strings in
	 * <code>strRules</code> are parsed in internal rules form.
	 * 
	 * @param strRules
	 *            array of rules of the form &quot;<code>x-&gt;y</code>&quot;
	 * @return
	 * @throws WrongArgumentType
	 */
	public static Function<IExpr, IExpr> rules(@Nonnull String[] strRules) throws WrongArgumentType {
		IAST astRules = F.ListC(strRules.length);
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
		return rules(astRules);
	}

	/**
	 * Create a functor from the given rules. If <code>astRules</code> is a
	 * <code>List[]</code> object, the elements of the list are taken as the
	 * rules of the form <code>Rule[lhs, rhs]</code>, otherwise the
	 * <code>astRules</code> itself is taken as the <code>Rule[lhs, rhs]</code>.
	 * 
	 * @param astRules
	 * @return
	 * @throws WrongArgumentType
	 */
	public static Function<IExpr, IExpr> rules(@Nonnull IAST astRules) throws WrongArgumentType {
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
			return new RulesPatternFunctor(equalRules, matchers);
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
			return input.isBlank() || input.isPattern() || input.isPatternSequence();
		}
	};

	private static void addRuleToCollection(Map<IExpr, IExpr> equalRules, List<PatternMatcherAndEvaluator> matchers,
			IAST rule) {
		if (rule.arg1().isFree(PATTERNQ_PREDICATE, true)) {
			IExpr temp = equalRules.get(rule.arg1());
			if (temp == null) {
				if (rule.arg1().isOrderlessAST() || rule.arg1().isFlatAST()) {
					matchers.add(
							new PatternMatcherAndEvaluator(ISymbol.RuleType.SET_DELAYED, rule.arg1(), rule.arg2()));
					return;
				}
				equalRules.put(rule.arg1(), rule.arg2());
			}
		} else {
			matchers.add(new PatternMatcherAndEvaluator(ISymbol.RuleType.SET_DELAYED, rule.arg1(), rule.arg2()));
		}
	}

	private Functors() {

	}
}
